import sys
from json import loads
from re import sub
import os

# Dictionary of months used for date transformation
MONTHS = {'Jan':'01','Feb':'02','Mar':'03','Apr':'04','May':'05','Jun':'06',\
        'Jul':'07','Aug':'08','Sep':'09','Oct':'10','Nov':'11','Dec':'12'}

"""
Returns true if a file ends in .json
"""
def isJson(f):
    return len(f) > 5 and f[-5:] == '.json'

"""
Converts month to a number, e.g. 'Dec' to '12'
"""
def transformMonth(mon):
    if mon in MONTHS:
        return MONTHS[mon]
    else:
        return mon

"""
Transforms a timestamp from Mon-DD-YY HH:MM:SS to YYYY-MM-DD HH:MM:SS
"""
def transformDttm(dttm):
    dttm = dttm.strip().split(' ')
    dt = dttm[0].split('-')
    date = '20' + dt[2] + '-'
    date += transformMonth(dt[0]) + '-' + dt[1]
    return date + ' ' + dttm[1]

"""
Transform a dollar value amount from a string like $3,453.23 to XXXXX.xx
"""
def transformDollar(money):
    if money == None or len(money) == 0:
        return money
    return sub(r'[^\d.]', '', money)

"""
Parses a single json file. Currently, there's a loop that iterates over each
item in the data set. Your job is to extend this functionality to create all
of the necessary SQL tables for your database.
"""
def parseJson(json_file):
    # Open .dat files (or create if not already created)
    user_data = open("user.dat", 'a')
    category_data = open("category.dat", 'a')
    bid_data = open("bid.dat", 'a')
    item_data = open("item.dat", 'a')

    # Parse the json files
    with open(json_file, 'r') as f:
        items = loads(f.read())['Items']
        # Go through each entry in the json
        for item in items:
            # Insert seller of item into users and insert item with its seller into items
            insert_user(user_data, item)
            insert_item(item_data, item)
            # Loops through categories of an item adds category and item into category table
            for category in item["Category"]:
                insert_category(category_data, category, item)
            # Insert bid into bids and bidder into respective tables for each bid
            if "Bids" in item:
                if item["Bids"] is not None:
                    for bid in item["Bids"]:
                        bid = bid["Bid"]
                        insert_bidder(user_data, bid["Bidder"])
                        insert_bid(bid_data, bid, item, bid["Bidder"])
            pass
    # Close files
    user_data.close()
    category_data.close()
    bid_data.close()
    item_data.close()

    # Remove duplicates
    # remove_dups("temp_user.dat", "user.dat")
    # remove_dups("temp_item.dat", "item.dat")
    # remove_dups("temp_bid.dat", "bid.dat")
    # remove_dups("temp_category.dat", "category.dat")

    # # Remove temp files
    # os.remove("temp_user.dat")
    # os.remove("temp_category.dat")
    # os.remove("temp_bid.dat")
    # os.remove("temp_item.dat")

# Inserts an item into the items.dat file
def insert_item(data_file, item):
    seller = item["Seller"]
    item_id = item["ItemID"]
    seller_id = quotation_fix(seller["UserID"])
    item_name = quotation_fix(item["Name"])
    currently = transformDollar(item["Currently"])
    buy_price = "NULL"
    if "Buy_Price" in item:
        buy_price = transformDollar(item["Buy_Price"])
    first_bid = transformDollar(item["First_Bid"])
    num_bids = item["Number_of_Bids"]
    started = transformDttm(item["Started"])
    ends = transformDttm(item["Ends"])
    description = "NULL"
    if "Description" in item and item["Description"] is not None:
        description = quotation_fix(item["Description"])
    to_write = item_id + "|" + seller_id + "|" + item_name + "|" + currently + "|" + buy_price + "|" \
               + first_bid + "|" + num_bids + "|" + started + "|" + ends + "|" + description + "\n"
    data_file.write(to_write)


# Insert category into category.dat
def insert_category(data_file, category, item):
    item_id = item["ItemID"]
    category = quotation_fix(category)
    to_write = item_id + "|" + category + "\n"
    data_file.write(to_write)


# Insert bid into bid.dat file
def insert_bid(data_file, bid, item, bidder):
    item_id = item["ItemID"]
    bidder_id = quotation_fix(bidder["UserID"])
    amount = transformDollar(bid["Amount"])
    time = transformDttm(bid["Time"])
    to_write = item_id + "|" + bidder_id + "|" + amount + "|" + time + "\n"
    data_file.write(to_write)


# Insert seller into user.dat file
def insert_user(data_file, item):
    user = item["Seller"]
    user_id = quotation_fix(user["UserID"])
    rating = user["Rating"]
    country = "NULL"
    if "Country" in item:
        country = quotation_fix(item["Country"])
    location = "NULL"
    if "Location" in item:
        location = quotation_fix(item["Location"])
    to_write = user_id + "|" + rating + "|" + location + "|" + country + "\n"
    data_file.write(to_write)


# Insert bidder into user.dat file
def insert_bidder(data_file, bidder):
    user_id = quotation_fix(bidder["UserID"])
    rating = bidder["Rating"]
    country = "NULL"
    if "Country" in bidder:
        country = quotation_fix(bidder["Country"])
    location = "NULL"
    if "Location" in bidder:
        location = quotation_fix(bidder["Location"])
    to_write = user_id + "|" + rating + "|" + location + "|" + country + "\n"
    data_file.write(to_write)


# Deal with quotation marks
def quotation_fix(string):
    return '"' + string.replace('"', '""') + '"'


def remove_dups(infile, outfile):
    lines_seen = set()
    outfile = open(outfile, "w")
    infile = open(infile, "r")
    for line in infile:
        if line not in lines_seen:
            outfile.write(line)
            lines_seen.add(line)
    outfile.close()


"""
Loops through each json files provided on the command line and passes each file
to the parser
"""
def main(argv):
    if len(argv) < 2:
        print >> sys.stderr, 'Usage: python skeleton_json_parser.py <path to json files>'
        sys.exit(1)
    # loops over all .json files in the argument
    for f in argv[1:]:
        if isJson(f):
            parseJson(f)
            print("Success parsing " + f)

if __name__ == '__main__':
    main(sys.argv)