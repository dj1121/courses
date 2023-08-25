# Devin Johnson
# LING 571
# HW7

import sys
import re
import numpy
import nltk
import math
import scipy
from gensim.models import Word2Vec

def pre_process(corpus):
    corpus = list(corpus)
    for i in range(0,len(corpus)):
        for j in range(0, len(corpus[i])):
            corpus[i][j] = corpus[i][j].lower()
            corpus[i][j] = sentence = re.sub(r"\W+", "", corpus[i][j])
        corpus[i] = " ".join(corpus[i]).split()
    return corpus
    

def cos_similarity(model):
    with open(sys.argv[2], "r") as judgment, open(sys.argv[3], "w") as output:
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

            # Build vector for word 1 and word 2
            word_1_vec = model.wv[word_1]
            word_2_vec = model.wv[word_2]
                
            similarity = scipy.spatial.distance.cosine(word_1_vec, word_2_vec)
            my_similarities.append(similarity)

            # Printout
            output.write(word_1 + "," + word_2 + ":" + str(similarity) + "\n")

        output.write("Correlation:" + str(scipy.stats.spearmanr(my_similarities, given_similiarities)[0]))



# Create a coocurrence matrix (dictionaries), weighted as necessary
window = int(sys.argv[1])
sents = pre_process(nltk.corpus.brown.sents())
model = Word2Vec(sents, size=100, window=2, min_count=1, workers=1)
cos_similarity(model)









