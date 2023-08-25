import sys
import math
import time

# Files
test_file = sys.argv[1]
boundary_file = sys.argv[2]
model_file = sys.argv[3]
sys_out = sys.argv[4]
beam_size = int(sys.argv[5])
top_n = int(sys.argv[6])
top_k = int(sys.argv[7])

# Globals
model_data = None
CORRECT = 0
TOTAL = 0

class Node():
    def __init__(self, candidate=None, true=None, p_hk=None, p_yx=None, parent=None, depth=0, word=None):
        self.p_yx = p_yx
        self.p_hk = p_hk
        self.candidate = candidate
        self.true = true
        self.parent = parent
        self.children = []
        self.depth = depth
        self.leaf = False
        self.word = word

    def __str__(self):
        return "--Node--\nCandidate:" + str(self.candidate) + \
         "\nTrue:" + str(self.true) + "\nChildren:" + str(len(self.children)) + \
            "\np_yx:" + str(self.p_yx) 

# Read in the model
def read_model(file):
    model_dict_form = {}
    with open(file) as t:
        curr_class = None
        for line in t:
            s = line.split()
            if "FEATURES FOR CLASS" in line:
                model_dict_form[s[-1]] = {}
                curr_class = s[-1]
            else:
                model_dict_form[curr_class][s[0]] = float(s[1])                 
    return model_dict_form

# Read in test data
def read_test(file):
    # sentences -> words -> features : vals
    test_dict = {}
    with open(file) as t:
        for line in t:
            s = line.split()

            # Break up first part of line
            first_el = s[0].split("-")
            sent_num = int(first_el[0])
            word_num = int(first_el[1])
            word = first_el[2]
            true_label = s[1]

            if sent_num not in test_dict:
                test_dict[sent_num] = {}
            test_dict[sent_num][word_num] = {"curW=": word, "gold=": true_label, "pref=": [], "suf=": []}


            # Get rest of features of word, add to dict
            for i in range(2,len(s)):
                curr = s[i].split("=")
                if "prevW=" in s[i]:
                    test_dict[sent_num][word_num]["prevW="] = curr[1]
                
                elif "prev2W=" in s[i]:
                    test_dict[sent_num][word_num]["prev2W="] = curr[1]

                elif "nextW=" in s[i]:
                    test_dict[sent_num][word_num]["nextW="] = curr[1]

                elif "next2W=" in s[i]:
                    test_dict[sent_num][word_num]["next2W="] = curr[1]
                
                elif "pref=" in s[i]:
                    test_dict[sent_num][word_num]["pref="].append(curr[1])

                elif "suf=" in s[i]:
                    test_dict[sent_num][word_num]["suf="].append(curr[1])

                elif "containUC" in s[i]:
                    test_dict[sent_num][word_num]["containUC"] = ""

    return test_dict


def pyx(sentence, model_data, curr_node):
    
    x_val = sentence[curr_node.depth + 1]

    # Add prev tags
    prevt = None
    prevtwotags = None
    if curr_node.parent != None:
        prevt = curr_node.candidate
        if curr_node.parent.parent != None:
            prevtwotags = curr_node.parent.candidate + "+" + curr_node.candidate
        else:
            prevtwotags = "BOS" + "+" + curr_node.candidate
    else:
        prevt = "BOS"
        prevtwotags = "BOS" + "+" + "BOS"
    if prevt != None:
        x_val["prevT="] = prevt
    if prevtwotags != None:
        x_val["prevTwoTags="] = prevtwotags

    # Calc p(y|x)
    y_probs = {}
    z = 0
    for label in model_data:
        # Default feature weight
        num_sum = model_data[label]["<default>"]
        for feature in x_val:
            if type(x_val[feature]) == list:
                for element in x_val[feature]:
                    f = feature + element
                    if f in model_data[label]:
                        num_sum += model_data[label][f]
            else:
                f = feature + x_val[feature]
                if "gold" not in f and f in model_data[label]:
                    num_sum += model_data[label][f]

        y_probs[label] = math.exp(num_sum)
        z += y_probs[label]
    
    for y in y_probs:
        y_probs[y] = y_probs[y] / z

    return y_probs
    

def search(sentence, start):

        # Not really a queue, but acting like one
        queue_list = [start]

        # Used for pruning level i
        curr_leaves = []
        past_leaves = []

        # Keep track of nodes expanded
        expanded = []

        # Let BOS be depth -1
        d = -1

        while len(queue_list) > 0 :
            
            # Check if at max depth, if so return the best node at level
            if d  == len(sentence) - 1:
                return sorted(past_leaves, key=lambda x: x.p_hk, reverse=True)[0]
    
            curr = queue_list.pop(0) # get first element
            expanded.append(curr)

            # Top n nodes
            pyx_word = pyx(sentence, model_data, curr)
            top_tags = sorted(pyx_word.items(), key=lambda x: x[1], reverse=True)[0:top_n]
            for n in top_tags:
                new_node = Node(n[0], test_data[i][curr.depth + 1]["gold="], curr.p_hk * n[1], n[1], curr, curr.depth + 1, test_data[i][curr.depth + 1]["curW="])
                curr.children.append(new_node)
                queue_list.append(new_node)
                curr_leaves.append(new_node)
            

            if curr.candidate == "BOS" or len(expanded) == len(past_leaves):
                print(d)
                # Prune the children (top k probs)
                topk_nodes = sorted(curr_leaves, key=lambda x: x.p_hk, reverse=True)[0:top_k]
                
                # Remove nodes that are not in topk (by removing pointer to it from parent)
                keep = []
                for node in curr_leaves:
                    if (node in topk_nodes) and \
                        (math.log(node.p_hk, 10) + beam_size >= math.log(topk_nodes[0].p_hk, 10)):
                        keep.append(node)
                
                past_leaves = list(keep)
                queue_list = list(keep)
                curr.children = list(keep)
                curr_leaves = []
                expanded = []
                d += 1
            

def output(last_node, sentence_num):
    global CORRECT
    global TOTAL
    with open(sys_out, "a") as sys:
        path = []
        curr = last_node
        while curr.parent != None:
            path.append(curr)
            if curr.true == curr.candidate:
                CORRECT += 1
            TOTAL += 1
            curr = curr.parent
        for i in range(len(path) - 1, -1, -1):
            sys.write(str(sentence_num) + "-" + str(len(path) - 1 - i) + "-" + path[i].word + " " + path[i].true + " " + path[i].candidate + " " + str(path[i].p_yx) + "\n")

t0 = time.time()
model_data = read_model(model_file)
test_data = read_test(test_file)
with open(sys_out, "w") as sys:
    sys.write("%%%%% test data:\n")

# For each sentence, tag it
for i in range(0, len(test_data)):
    i = i + 1
    start = Node("BOS", "BOS", 1, 1, None, -1, "BOS") # Begin with BOS node, don't prune BOS
    best_leaf = search(test_data[i], start) # Pass BOS to function, returns best leaf node
    output(best_leaf, i) # Output the path from that leaf to root (sequence of tags)

print("Accuracy: " + str(CORRECT/TOTAL))
print("Runtime in minutes: " + str((time.time() - t0) / 60))
    