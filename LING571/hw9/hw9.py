# Devin Johnson
# LING 571
# HW9

import nltk
import sys
import re

# Load in text files
sentences = open(sys.argv[2], "r").readlines()

# Load in the CFG to a variable
grammar = nltk.data.load(sys.argv[1])

# Load in parser to variable
parser = nltk.parse.EarleyChartParser(grammar)

# Tokenize each sentence and send it to the parser and output
output = open(sys.argv[3],"w") 
prev = ""
for curr in sentences:
    if len(curr.split()) == 0 or len(prev.split()) == 0 or prev == "":
        prev = curr
        continue

    # Get parses (only looking at first possible parse for each sentence)
    tokenized_prev = nltk.word_tokenize(prev)
    tokenized_sent = nltk.word_tokenize(curr)
    parse_prev = ""
    parse_curr = ""
    for tree in parser.parse(tokenized_prev):
        parse_prev = ' '.join(str(tree).split()) 
        break
    for tree in parser.parse(tokenized_sent):
        parse_curr = ' '.join(str(tree).split())
        break
    
    # Get pronouns in second sentence, output
    pronouns = re.findall(r'\(PRP [A-Za-z]+\)', parse_curr)
    for pronoun in pronouns:
        output.write(re.sub(r"\)", "", pronoun.split()[1]) + " ")
        output.write(parse_prev + " " + parse_curr + "\n")
        output.write(parse_curr + "\n")
        output.write(parse_prev + "\n")
        output.write("\n")
    
    
    
