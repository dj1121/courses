Devin Johnson
LING 571
HW 7

- I wasn't sure what was expected for training our model with Word2Vec, so output results may vary from the grader.

- I implemented a coocurrence matrix by using dictionaries of dictionaries where each cell was the number of times
two words occurred in the same context. This means that we end up with a symmetric diagonal matrix.

- For weighting, I took FREQ as the default, meaning that each cell in the matrix represented how many times
words co-occured.

- For calculating PMI, I was able to take sums of items in my dictionaries to get probabilities. I then stored
the PMI values in the dictionary once they were calculated.

- This assignment gave me a much better understanding of distributional semantics, though I wish there was more explanation
on how Word2Vec works and how we were supposed to use it for this homework. On that part, I felt like I was mostly guessing
on how we should use it.

- I would have liked a better way to check if our answers matched a certain output, like we have "toy" files in previous homeworks.
This makes it easier to know if what I did was correct in terms of how we will be graded