Devin Johnson
LING 571 HW8

My outputs for this homework mostly match the gold file, but not completely, which is expected since
it was noted that we should only get 60% accuracy. I found the code from the resnik paper to be highly
unintuitive, so I instead followed the example in the slides. I found this to be quite helpful.

Predicting word senses:
- For the most part, the algorithm did well in predicting the word senses
- However, sometimes there were ties and so the algorithm picked one of the tied senses
- This made it so that the senses weren't always intuitively correct

Correlation with human judgment:
- I got different values for correlation and similarity when compared to example file
- Some of my similarity values were 0, which made sense for words like "rooster" and "voyage"
- However, I also got 0 similarity on pairs such as "journey" and "car", which I feel are somewhat related
- midday,noon,9.902621636934121 and most other value seemed to make a lot of sense!

I would have liked to learn the subject more in depth before doing this homework, just because
I felt like I only scrateched the surface. Overall, the results seemed to make a lot of sense, even
if some didn't match the gold file. So, I think the algorithm is a decent way to determine word sense.
It certainly makes me curious on how clustering algorithms can do better/worse in certain situations.