Devin Johnson
LING 571 HW 6

- Some of my manually-written target representations differ from those output by the parser.

- I had most difficulty encoding "OR" into the grammar, especially how to combine multiple verbs
which would require different events but with the same subject. Even though I got it to work, 
I will be going to office hours for further explanation.

- "John eats every sandwich or bagel" can be represented multiple ways, and my parser and written results differ.
however, both results I believe are logically valid. My written representation relies on the interpretation that
John eats every sandwich OR John eats every bagel. The parser instead took the interpretation that John eats all sandwiches
AND John eats all bagels.

- "nobody eats a bagel" was represented differently between my parser and my written results. The parser results
essentially say NOT(there exists a person who eats a bagel) while my written results instead used a different order
of parentheses but I believe they are equivalent.

- In my hw6_semantics.py I had to add an extra new line at the last sentence because of a bug that wouldn't print a new line.
The new line is inserted if the sentence is "no student eats a bagel". If you use a different last sentence, there will be a small
formatting error at the end of the output file (no newline) but it will still be readable.

- The assignment gave me lots of practice in understanding lambda calculus, though sometimes I still don't know how some things
work so I will be going to office hours. Sometimes it is unclear to me when we need a lambda and when we don't and how values
get passed up the tree to S.