# Devin Johnson
# LING 571
# HW8

from nltk.corpus import wordnet_ic
from nltk.corpus import wordnet as wn
from nltk.corpus.reader.wordnet import information_content
import sys
import scipy

# Read in IC file
brown_ic = wordnet_ic.ic(sys.argv[1])
with open(sys.argv[2], "r") as wsd_contexts, open(sys.argv[4], "w") as output, open(sys.argv[3]) as judgment:
     
     # Calculate similarities and word senses
    for line in wsd_contexts:
        split_line = line.split()
        probe = split_line[0]
        group = split_line[1].split(",")

        highest_overall = -1
        best_sense_overall = None
            
        for word in group:
            # Keep a place to store similarities
            highest_similarity = -1

            # Best sense of probe
            best_sense_probe = None

            # Get all concepts of probe and word
            concepts_probe = wn.synsets(probe, pos=wn.NOUN)
            concepts_word = wn.synsets(word, pos=wn.NOUN)

            # Go pairwise through each concept of each word (probe1 word1, probe1 word2, etc.)
            for c_i in concepts_probe:
                for c_j in concepts_word:
                    # Find least common subsumer
                    commons = c_i.common_hypernyms(c_j)
                    for common in commons:
                        ic = information_content(wn.synset(common.name()), brown_ic)
                        if ic > highest_similarity:
                            highest_similarity = ic
                            best_sense_probe = c_i
                        if highest_similarity > highest_overall:
                            highest_overall = highest_similarity
                            best_sense_overall = c_i              

            # Print out
            output.write("(" + probe + ", " + word + ", " + str(highest_similarity)  + ") " )
        output.write("\n")
        output.write(best_sense_overall.name() + "\n")
    
    # Calculate correlation
    given_similarities = []
    my_similarities = []
    for line in judgment:
        line_split = line.split(",")
        given_similarities.append(float(line_split[2]))

        probe = line_split[0]
        word = line_split[1]

        # Keep a place to store similarities
        highest_similarity = -1

        # Best sense of probe
        best_sense_probe = None

        # Get all concepts of probe and word
        concepts_probe = wn.synsets(probe, pos=wn.NOUN)
        concepts_word = wn.synsets(word, pos=wn.NOUN)

        # Go pairwise through each concept of each word (probe1 word1, probe1 word2, etc.)
        for c_i in concepts_probe:
            for c_j in concepts_word:
                # Find least common subsumer
                commons = c_i.common_hypernyms(c_j)
                for common in commons:
                    ic = information_content(wn.synset(common.name()), brown_ic)
                    if ic > highest_similarity:
                        highest_similarity = ic
                        best_sense_probe = c_i               
        
        my_similarities.append(highest_similarity)
        output.write(probe + "," + word + "," + str(highest_similarity) + "\n")

    output.write("Correlation:" + str(scipy.stats.spearmanr(my_similarities, given_similarities)[0]))

