Devin Johnson
LING 572
HW 5

--QUESTION 1--
Commands used:
vectors2classify --training-file train2.vectors --testing-file test2.vectors --trainer MaxEnt (to get training/test accuracies)
mallet train-classifier --input train2.vectors --output-classifier ../q1/m1 --trainer MaxEnt (to get model file)
classifier2info --classifier ../q1/m1 > ../q1/m1.txt (to get model file in text form)

Accuracies:
Summary. train accuracy mean = 0.9685185185185186 stddev = 0.0 stderr = 0.0
Summary. test accuracy mean = 0.8266666666666667 stddev = 0.0 stderr = 0.0

--QUESTION 2--
My accuracy is exactly the same as Mallet