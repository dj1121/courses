QUESTION 1:
Trial 0 Training NaiveBayesTrainer with 2700 instances
Trial 0 Training NaiveBayesTrainer finished
Trial 0 Trainer NaiveBayesTrainer training data accuracy= 0.9444444444444444
Trial 0 Trainer NaiveBayesTrainer Test Data Confusion Matrix
Confusion Matrix, row=true, column=predicted  accuracy=0.8966666666666666
                    label   0   1   2  |total
0    talk.politics.guns  94   .   6  |100
1 talk.politics.mideast   3  96   1  |100
2    talk.politics.misc  16   5  79  |100

Trial 0 Trainer NaiveBayesTrainer test data accuracy= 0.8966666666666666

NaiveBayesTrainer
Summary. train accuracy mean = 0.9444444444444444 stddev = 0.0 stderr = 0.0
Summary. test accuracy mean = 0.8966666666666666 stddev = 0.0 stderr = 0.0
---------------------------------------------------------------------------------------------------
QUESTION 2:
Cond_Prob_Delta   Training Accuracy     Test Accuracy
    0.1           0.9603703703703703        0.9066666666666666
    0.5           0.9540740740740741        0.8933333333333333
    1.0           0.9503703703703704        0.8833333333333333
---------------------------------------------------------------------------------------------------
QUESTION 3:
Cond_Prob_Delta   Training Accuracy     Test Accuracy
    0.1           0.9581481481481482        0.9166666666666666
    0.5           0.9507407407407408        0.9066666666666666
    1.0           0.9440740740740741        0.9



OTHER NOTES:
- I left my probabilities in sys files as log probabilities. Reasoning for this is that
when converting I still had many lines with zeroes. I attempted to use numpy.float_power
but this also did not work. The results with log probabilties are more interpretable.