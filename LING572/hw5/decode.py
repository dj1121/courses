# Devin Johnson
# LING 572
# HW5 (Maxent)

import sys
import time
import math
import numpy as np


# Files
test_file = sys.argv[1]
model_file = sys.argv[2]
sys_file = sys.argv[3]

# Relevant problem info (vocab, classes, etc)
# Forms columns labels of matrix
word_to_index = {}
index_to_word = {}
class_to_num = {}
num_to_class = {}

# Model (rows = classes, cols = feats, cells = weights (lambdas))
# Dimension (|C|x|F|)
model = []



# Get vocab, word indices, classes, etc.
# Set up all the necessary info for the problem
def setup(model):
    with open(model) as t:
        vocab = set()
        for line in t:
            s = line.split()
            if "FEATURES FOR CLASS" in line:
                class_to_num[s[3]] = len(class_to_num)
                num_to_class[len(num_to_class)] = s[3]
            else:
                # Add words to vocab
                vocab.add(s[0])
        
        # Sort the vocab, get index nums
        vocab = sorted(list(vocab))
        for i in range(0, len(vocab)):
            word_to_index[vocab[i]] = i
            index_to_word[i] = vocab[i]


# Build the model matrix
def model_matrix(model_file):
    global model
    model = np.zeros((len(num_to_class), len(index_to_word)))
    with open(model_file) as m:
        curr_class_index = None
        curr_word_index = None
        for line in m:
            s = line.split()
            if "FEATURES FOR CLASS" in line:
                curr_class_index = class_to_num[s[3]]
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
            curr_row.append(class_to_num[s[0]])

            # Append current row to matrix
            matrix.append(curr_row)
    
    return np.array(matrix)


# Classify documents
def classify(test_docs):
    classfications = np.zeros((len(test_docs), len(num_to_class) + 1))
    # Calculate all p(y|x)
    for i in range(0, len(test_docs)):
        x = test_docs[i]
        y_probs = np.zeros((len(num_to_class),1))
        z = 0
        for j in range(0, len(num_to_class)):
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
            classfications[i][j] = y_probs[j]
        classfications[i][-1] = test_docs[i][-1]
    
    return classfications


# Output classification results given classification matrix
def output_classifications(data, mode):

    confusion_matrix = np.zeros((len(num_to_class), len(num_to_class)))

    with open(sys_file, "a") as s:
        s.write("%%%%% " + mode + " data:\n")

        # For each document (go over classification matrix)
        for i in range(0, len(data)):
            curr_row = data[i]
            best_class = np.where(curr_row[0:-1] == np.amax(curr_row[0:-1]))[0][0]
            true_class = int(curr_row[-1])
            s.write("array:" + str(i) + " " + num_to_class[true_class] + " ")

            # List of tuples
            probs_to_sort = []
            for j in range(0, len(data[i]) - 1):
                probs_to_sort.append( (num_to_class[j], data[i][j]) )
            probs_to_sort = sorted(probs_to_sort, key=lambda x: -x[1])
            for p in probs_to_sort:
                s.write(" " + str(p[0]) + " " + str(round(p[1], 5)))
            s.write("\n")

            # Acc file using best_class
            confusion_matrix[best_class][true_class] += 1

    # Print confusion matrix
    print("Confusion matrix for the training data:\nrow is the truth, column is the system output")
    print("\t\t\t", end="")
    for i in range(0, len(confusion_matrix)):
        print(num_to_class[i] + " ", end="")
    print()
    correct = 0
    total = 0
    for i in range(0, len(confusion_matrix)):
        print(num_to_class[i] + "\t\t", end="")
        for j in range(0, len(confusion_matrix)):
            print(str(confusion_matrix[i][j]) + "\t\t\t", end="")
            if i == j:
                correct += confusion_matrix[i][j]
            total += confusion_matrix[i][j]
        print()
    print(mode + " accuracy=" + str(correct/total))
    print()


# Setup data
t0 = time.time()
setup(model_file)
model_matrix(model_file)

# Classify
test_docs = build_matrix(test_file)
results = classify(test_docs)

# Output
open(sys_file, "w").close()
output_classifications(results, "test")
print("Runtime in minutes: " + str((time.time() - t0) / 60))