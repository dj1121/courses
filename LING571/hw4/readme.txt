LING 571
HW 4
Devin Johnson, Andrew Barker

- For our improvements we chose to implement OOV handling. Therefore, in our inducer, we implemented an UNK
terminal which would take the place of any out of vocab words. We also added productions from all Nonterminals to
an UNK. Then, when parsing, if a token was unrecognized, it would be designated an UNK and the parser would
find the highest probability parse given the possible parents it could have. This does NOT change our evalb results.

Andrew's Work:
    - Algorithm ideas
    - CKY parser from hw3
    - Improvements
    - Parser

Devin's Work:
    - Algorithm ideas
    - Readme
    - evalb
    - Improvements
    - Inducer

What we learned:
    - How to create a PCFG from given trees
    - How to make improvements to PCFG
    - How to create parses from a PCFG
    - Collaboration techniques