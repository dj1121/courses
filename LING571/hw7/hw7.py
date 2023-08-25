# Devin Johnson
# LING 571
# HW7

import sys
import re
import numpy
import nltk
import math
import scipy

def pre_process(corpus):
    words = []
    # Remove punctuation, make lower
    for i in range(0, len(corpus)):
        curr_word = corpus[i].lower()

        # Remove punctuation
        curr_word = re.sub(r"\W+", "", curr_word)
        
        if len(curr_word) >= 1:
            words.append(curr_word.lower())

    return words

def create_cooc_dict(words, window):
    # Find occurrences of each word in window
    cooc_dict = {}
    for i in range(0, len(words)):
        # Build appropriate window
        l = i - window
        r = i + window
        while l < 0:
            l += 1 
        while r >= len(words):
            r -= 1
        
        curr_word = words[i]
        if curr_word not in cooc_dict:
            cooc_dict[curr_word] = {}
        
        # Go through words within window
        for j in range(l, r+1):
            if j != i:
                if words[j] in cooc_dict[curr_word]:
                    cooc_dict[curr_word][words[j]] += 1
                else:
                    cooc_dict[curr_word][words[j]] = 1

    return cooc_dict

def apply_PMI(cooc_dict):
    # Get total words
    table_sum = 0
    for key in cooc_dict:
        for key2 in cooc_dict[key]:
            table_sum += cooc_dict[key][key2]

    # Get P(w) for all words
    probabilities = {}
    for key in cooc_dict:
        for key2 in cooc_dict[key]:
            if key not in probabilities:
                probabilities[key] = cooc_dict[key][key2]
            else:
                probabilities[key] += cooc_dict[key][key2]
        probabilities[key] = probabilities[key]/table_sum
    
    # Apply PMI
    for key in cooc_dict:
        for key2 in cooc_dict[key]:
            pw = probabilities[key]
            pf = probabilities[key2]
            pwf = cooc_dict[key][key2] / table_sum
            if pwf != 0:
                pmi = max(math.log(pwf / (pw * pf), 2), 0)
                cooc_dict[key][key2] = pmi
            else:
                cooc_dict[key][key2] = 0

    return cooc_dict

def cos_similarity(cooc_dict, words):
    with open(sys.argv[3], "r") as judgment, open(sys.argv[4], "w") as output:
        words = sorted(list(set(words)))
        my_similarities = []
        given_similiarities = []
        for line in judgment:
            # Get each word
            split = line.split(",")
            word_1 = split[0]
            index_word_i = 0
            word_2 = split[1]
            index_word_j = 0
            given_similiarities.append(split[2])
            
            # Get top 10 for word 1, 2
            word_1_top = sorted(cooc_dict[word_1].items(), key=lambda x: -x[1])
            word_2_top = sorted(cooc_dict[word_2].items(), key=lambda x: -x[1])

            # Build vector for word 1 and word 2
            word_1_vec = []
            word_2_vec = []
            
            for word in words:
                if word in cooc_dict[word_1]:
                    word_1_vec.append(cooc_dict[word_1][word])
                else:
                    word_1_vec.append(0)
            
            for word in words:
                if word in cooc_dict[word_2]:
                    word_2_vec.append(cooc_dict[word_2][word])
                else:
                    word_2_vec.append(0)
                
            similarity = scipy.spatial.distance.cosine(word_1_vec, word_2_vec)
            my_similarities.append(similarity)

            # Printout
            output.write(word_1 + ": ")
            for element in word_1_top:
                output.write(element[0] + ":" + str(element[1]) + " ")
            output.write("\n")
            output.write(word_2 + ": ")
            for element in word_2_top:
                output.write(element[0] + ":" + str(element[1]) + " ")
            output.write("\n")
            output.write(word_1 + "," + word_2 + ":" + str(similarity) + "\n")

        output.write("Correlation:" + str(scipy.stats.spearmanr(my_similarities, given_similiarities)[0]))



# Create a coocurrence matrix (dictionaries), weighted as necessary
window = int(sys.argv[1])
weighting = sys.argv[2]
words = pre_process(nltk.corpus.brown.words())
cooc_dict = create_cooc_dict(words,window)
if weighting == "PMI":
    cooc_dict = apply_PMI(cooc_dict)

# Compute cosine similarities
cos_similarity(cooc_dict, words)










