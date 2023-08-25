# Devin Johnson
# LING 572
# HW5 (Maxent)

import sys
import time
import math
import numpy as np

# Files
train_data = sys.argv[1]
out_file = sys.argv[2]

# Relevant problem info (vocab, classes, etc)
# Forms columns labels of matrix
word_to_index = {}
class_to_index = {}
index_to_word = {}
index_to_class = {}

# Get vocab, word indices, classes, etc.
# Set up all the necessary info for the problem
def setup_problem(train_data):
    with open(train_data) as t:
        vocab = set()
        for line in t:
            s = line.split()
            # Add class to classes
            if s[0] not in class_to_index:
                class_to_index[s[0]] = len(class_to_index)
                index_to_class[len(index_to_class)] = s[0]

            # Add words to vocab
            for i in range(1, len(s)):
                vocab.add(s[i].split(":")[0])
        
        # Sort the vocab, get index nums
        vocab = sorted(list(vocab))
        for i in range(0, len(vocab)):
            word_to_index[vocab[i]] = i
            index_to_word[i] = vocab[i]


# Class feature matrix
def calc_expected_val(train_data):
    cf_matrix = np.zeros((len(index_to_class), len(index_to_word)))
    with open(train_data) as t:
        for line in t:
            s = line.split()
            row = class_to_index[s[0]]
            for i in range(1, len(s)):
                word = word_to_index[s[i].split(":")[0]]
                cf_matrix[row][word] += 1

    return cf_matrix


# Output expectations to output file
def output_results(matrix):
    with open(out_file, "a") as o:
        for i in range(0, len(matrix)):
            for j in range(1, len(matrix[i])):
                o.write(index_to_class[i] + " ")
                o.write(index_to_word[j] + " " + str(round(matrix[i][j]/len(index_to_word), 5)) + " " + str(matrix[i][j]) + "\n")
            o.write("\n")


# Setup
t0 = time.time()
setup_problem(train_data)

# Expected val
results = calc_expected_val(train_data)

# Output
open(out_file, "w").close()
output_results(results)
print("Runtime in minutes: " + str((time.time() - t0) / 60))