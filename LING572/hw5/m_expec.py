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
model_file = None
if len(sys.argv) == 4:
    model_file = sys.argv[3]

# Relevant problem info (vocab, classes, etc)
# Forms columns labels of matrix
word_to_index = {}
class_to_index = {}
index_to_word = {}
index_to_class = {}
n_docs = 0

model = []

# Build the model matrix
def model_matrix(model_file):
    global model
    model = np.zeros((len(index_to_class), len(index_to_word)))
    with open(model_file) as m:
        curr_class_index = None
        curr_word_index = None
        for line in m:
            s = line.split()
            if "FEATURES FOR CLASS" in line:
                curr_class_index = class_to_index[s[3]]
            else:
                curr_word_index = word_to_index[s[0]]
                model[curr_class_index][curr_word_index] = s[1]


# Build matrix of test docs
# Build a numpy matrix from the data in a given file
def build_matrix(file):
    matrix = []
    with open(file) as f:
        # For each document
        for line in f:
            # Build a new row
            curr_row = []
            word_counts = {}
            s = line.split(" ")

            # Sentences have format: label word1:count word2:count
            doc_words = set()
            for i in range(1, len(s)):
                word_and_count = s[i].split(":")
                if len(word_and_count) > 1:
                    curr_word = word_and_count[0]
                    curr_word_count = float(word_and_count[1].strip())
                    word_counts[curr_word] = curr_word_count
                    doc_words.add(curr_word)

            # For word in vocab, see if doc has or does not have it
            for i in range(0, len(index_to_word)):
                if index_to_word[i] in doc_words:
                    curr_row.append(word_counts[index_to_word[i]])
                else:
                    curr_row.append(0)
            
            # Append label to end
            curr_row.append(class_to_index[s[0]])

            # Append current row to matrix
            matrix.append(curr_row)
    
    return np.array(matrix)


# Classify documents
def get_probs(test_docs):
    classifications = np.zeros((len(test_docs), len(index_to_class) + 1))
    if model_file != None:
        # Calculate all p(y|x)
        for i in range(0, len(test_docs)):
            x = test_docs[i]
            y_probs = np.zeros((len(index_to_class),1))
            z = 0
            for j in range(0, len(index_to_class)):
                y = j
                # Default feature weight
                num_sum = model[y][0]
                for k in range(1, len(x) - 1):
                    if x[k] == 1:
                        num_sum += model[y][k]
                y_probs[y] = math.exp(num_sum)
                z += y_probs[y]
            
            for j in range(0, len(y_probs)):
                y_probs[j] = y_probs[j] / z
                classifications[i][j] = y_probs[j]
            classifications[i][-1] = test_docs[i][-1]
    else:
        for i in range(0, len(classifications)):
            classifications[i][-1] = test_docs[i][-1]
            for j in range(0,len(classifications[i]) - 1):
                classifications[i][j] = 1/len(index_to_class)

    return classifications


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
                word = s[i].split(":")[0]
                vocab.add(word)
                    
        
        # Sort the vocab, get index nums
        vocab = sorted(list(vocab))
        for i in range(0, len(vocab)):
            word_to_index[vocab[i]] = i
            index_to_word[i] = vocab[i]


# Get vocab, word indices, classes, etc.
# Set up all the necessary info for the problem
def setup(model):
    with open(model) as t:
        vocab = set()
        curr_class = None
        for line in t:
            s = line.split()
            if "FEATURES FOR CLASS" in line:
                class_to_index[s[3]] = len(class_to_index)
                index_to_class[len(index_to_class)] = s[3]
            else:
                # Add words to vocab
                vocab.add(s[0])
        
        # Sort the vocab, get index nums
        vocab = sorted(list(vocab))
        for i in range(0, len(vocab)):
            word_to_index[vocab[i]] = i
            index_to_word[i] = vocab[i]


# Class feature matrix
def calc_expected_val(train_data, classifications):
    # |C| x |F|
    cf_matrix = np.zeros((len(index_to_class), len(index_to_word)))
    # for each doc x
    n = len(train_data)
    for i in range(0, n):
        #  for each feature f in x
        for j in range(0, len(train_data[i]) - 1):
            # Check if in x
            if train_data[i][j] == 1:
                # for each y in Y
                for k in range(0, len(cf_matrix)):
                    cf_matrix[k][j] += (1/n)  * classifications[i][k]
                    
    return cf_matrix


# Output expectations to output file
def output_results(training_docs, matrix):
    n = len(training_docs)
    with open(out_file, "a") as o:
        for i in range(0, len(matrix)):
            for j in range(1, len(matrix[i])):
                o.write(index_to_class[i] + " ")
                o.write(index_to_word[j] + " " + str(round(matrix[i][j], 5)) + " " + str(round(matrix[i][j] * n, 5)) + "\n")
            o.write("\n")




# Setup
t0 = time.time()
# If model file present, use it to make vocab
if model_file != None:
    setup(model_file)
    model_matrix(model_file)
else:
    setup_problem(train_data)

# Calculate P(y | xi) for all x
training_docs = build_matrix(train_data)
p_y_x = get_probs(training_docs)

# Expected val
results = calc_expected_val(training_docs, p_y_x)

# Output
open(out_file, "w").close()
output_results(training_docs, results)
print("Runtime in minutes: " + str((time.time() - t0) / 60))


