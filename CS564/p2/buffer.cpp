/**
 * @author See Contributors.txt for code contributors and overview of BadgerDB.
 *
 * @section LICENSE
 * Copyright (c) 2012 Database Group, Computer Sciences Department, University of Wisconsin-Madison.
 */

#include <memory>
#include <iostream>
#include "buffer.h"
#include "exceptions/buffer_exceeded_exception.h"
#include "exceptions/page_not_pinned_exception.h"
#include "exceptions/page_pinned_exception.h"
#include "exceptions/bad_buffer_exception.h"
#include "exceptions/hash_not_found_exception.h"

namespace badgerdb { 

	// Create the buffer pool
	BufMgr::BufMgr(std::uint32_t bufs): numBufs(bufs) {
		bufDescTable = new BufDesc[bufs];

	for (FrameId i = 0; i < bufs; i++){
		bufDescTable[i].frameNo = i;
		bufDescTable[i].valid = false;
	}

	bufPool = new Page[bufs];

	int htsize = ((((int) (bufs * 1.2))*2)/2)+1;
	hashTable = new BufHashTbl (htsize);  // allocate the buffer hash table

	clockHand = bufs - 1;
	}

	// Flush the buffer
	BufMgr::~BufMgr() {
		uint32_t i = 0;
		while (i < numBufs){
			BufDesc curr = bufDescTable[i];
			// If there's a dirty bit, write to buffer pool
			if (curr.dirty && curr.valid) {
				(*curr.file).writePage(bufPool[curr.frameNo]);
			}
			i++;
		}
			delete [] bufDescTable;	
			delete [] bufPool;
			delete hashTable;
	}

	// Move the clock hand forward (with modular arithmetic to not exceed numBufs -1)
	void BufMgr::advanceClock(){
		clockHand = (clockHand + 1) % numBufs;
	}

	// Use the clock replacement algorithm to find a frame to allocate to
	void BufMgr::allocBuf(FrameId & frame){
		bool spotFound = false;
		BufDesc currFrame;
		uint32_t clockone = clockHand;
		uint32_t numPinnedPages = 0;

		// Start moving the clock through frames
		advanceClock();
		
		// Go through all frames, stop if we find one to allocate to or we go through full clock cycle
		while (!spotFound && numBufs > numPinnedPages) {
			// Get the current frame that the clock points at
			currFrame = bufDescTable[clockHand];
			
			// Need to clear pinned counter if we cycle back to origin
			if (clockHand == clockone) {
				numPinnedPages = 0;
			}
			// Case 1: If b (the frame) is invalid, use it
			if (!currFrame.valid) {
				spotFound = true;
				frame = currFrame.frameNo;
			}
			// Case 2: If valid but refbit set, clear refbit and move forward
			else if (currFrame.valid && currFrame.refbit) {
				bufDescTable[clockHand].refbit = false;
				advanceClock();
			}
			// Case 3: If frame valid, refbit not set and page pinned, move on.
			else if (currFrame.valid && !currFrame.refbit && currFrame.pinCnt > 0) {
				advanceClock();
				numPinnedPages++;
			}
			// Case 4: If frame valid, refbit not set, page not pinned. Use the frame and write if dirty bit set, then clear to allocate.
			else if (currFrame.valid && !currFrame.refbit && currFrame.pinCnt == 0) {
				// Flush and write to disk
				if (currFrame.dirty) {
					(*bufDescTable[clockHand].file).writePage(bufPool[currFrame.frameNo]);
				}
				// remove the frame from hash table
				hashTable->remove(currFrame.file, currFrame.pageNo);

				// clear the frame in buffer
				bufDescTable[clockHand].Clear();

				// return the frame number
				frame = currFrame.frameNo;
				spotFound = true;
			}
		}
		// Buffer is full
		if (!spotFound)
			throw BufferExceededException();
	}

	// Get a page from the buffer pool
	void BufMgr::readPage(File* file, const PageId pageNo, Page*& page){
		FrameId frameNo;
		try {
			// Go to hashtable and lookup given page
			(*hashTable).lookup(file, pageNo, frameNo);
			// Page has been referenced, so set refbit to true and pin count +1
			bufDescTable[frameNo].refbit = true;
			bufDescTable[frameNo].pinCnt++;     
			page = &bufPool[frameNo];
		}
		// If page not found in hastable, have to bring in from disk
		catch (HashNotFoundException h) {
			// Allocate a buffer frame to place the page and place page there
			allocBuf(frameNo);
			Page myPage = (*file).readPage(pageNo);
			bufPool[frameNo] = myPage;
			// Update hash table to reflect changes and set pin count to 1, valid to 1, refbit to 1, dirty bit to 0
			(*hashTable).insert(file, pageNo, frameNo);
			bufDescTable[frameNo].Set(file, pageNo);
			page = &bufPool[frameNo];
		}
	}

	// Decrement the pin count of a frame
	void BufMgr::unPinPage(File* file, const PageId pageNo, const bool dirty){
		FrameId frameNo;
		// Go to hashtable and lookup page and get from pool
		(*hashTable).lookup(file, pageNo, frameNo);
		BufDesc frame = bufDescTable[frameNo];
		// If already unpinned, throw exception
		if (frame.pinCnt <= 0){
			throw PageNotPinnedException((*file).filename(), pageNo, frameNo);
		}
		// Set dirty bit if needed and decrement pin count
		if(dirty){
			bufDescTable[frameNo].dirty = true;
		}
		bufDescTable[frameNo].pinCnt--;	
	}

	// Flush a file from frame
	void BufMgr::flushFile(const File* file){
		// Go through the buffer
		for(uint32_t i = 0; i < numBufs; i++){
			BufDesc currFrame = bufDescTable[i];
			// If frame contains file
			if (currFrame.file == file ) {
				if (!currFrame.valid) 
					throw BadBufferException(currFrame.frameNo, currFrame.dirty, currFrame.valid, currFrame.refbit);
				if (currFrame.pinCnt > 0) 
					throw PagePinnedException((*file).filename(), currFrame.pageNo, currFrame.frameNo);
				if (currFrame.dirty) {
					Page myPage = bufPool[currFrame.frameNo];
					(*bufDescTable[i].file).writePage(myPage);
					bufDescTable[i].dirty = false;
				}
				// Remove page from hashtable and clear buffer frame
				(*hashTable).remove(file, currFrame.pageNo);
				bufDescTable[i].Clear();
			} 
		}
	}

	// Allocate a page into file then buffer pool
	void BufMgr::allocPage(File* file, PageId &pageNo, Page*& page){
		FrameId frameNo;
		// Allocate a new page in file and allocate buffer frame
		Page myPage = (*file).allocatePage();
		allocBuf(frameNo);
		// Insert into hashtable and put in buffer
		(*hashTable).insert(file, myPage.page_number(), frameNo);
		bufPool[frameNo] = myPage;
		// Set
		page = &bufPool[frameNo];
		pageNo = myPage.page_number();
		bufDescTable[frameNo].Set(file, myPage.page_number());
		
	}

	// Delete page in all places (buffer/disk)
	void BufMgr::disposePage(File* file, const PageId PageNo){
		FrameId frameNo;
		// Try to find the page in hash table and remove
		hashTable->lookup(file,PageNo,frameNo);
		hashTable->remove(file,PageNo);
		// Clear the the buffer frame and delete on disk
		bufDescTable[frameNo].Clear();
		(*file).deletePage(PageNo);
	}

	void BufMgr::printSelf(void){
		BufDesc* tmpbuf;
		int validFrames = 0;
		for (std::uint32_t i = 0; i < numBufs; i++){
			tmpbuf = &(bufDescTable[i]);
				std::cout << "FrameNo:" << i << " ";
				tmpbuf->Print();

			if (tmpbuf->valid == true){
				validFrames++;
			}
		}
			std::cout << "Total Number of Valid Frames:" << validFrames << "\n";
		}
}
