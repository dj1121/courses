import java.util.HashMap;
import java.util.Map;
import java.lang.Math;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifierImpl implements NaiveBayesClassifier {
	private Instance[] trainingData;
	private int v;
	private double delta;
	public int sportsCount, businessCount;
	public int sportsWordCount, businessWordCount;
	private HashMap<String,Integer> sportsMap = new HashMap<>();
	private HashMap<String,Integer> businessMap = new HashMap<>();

  /**
   * Trains the classifier with the provided training data and vocabulary size
   */
  @Override
  public void train(Instance[] instances, int v) {
      // Count number of times each word appears in all instances
  	  this.trainingData = instances;
  	  this.v = v;
  	  for(Instance i : instances){
  	      if(i.label == Label.SPORTS){
  	          sportsCount += 1;
          }
          else if(i.label == Label.BUSINESS){
  	          businessCount += 1;
          }
  	      for(String word : i.words){
  	          if(i.label == Label.SPORTS){
  	              sportsWordCount +=1;
                  if(sportsMap.containsKey(word)){
                      sportsMap.put(word, sportsMap.get(word) + 1);
                  }
                  else{
                      sportsMap.put(word, 1);
                  }
              }
              else if(i.label == Label.BUSINESS){
  	              businessWordCount += 1;
                  if(businessMap.containsKey(word)){
                      businessMap.put(word, businessMap.get(word) + 1);
                  }
                  else{
                      businessMap.put(word, 1);
                  }
              }
          }
      }
  }

  /*
   * Counts the number of documents for each label
   */
  public void documents_per_label_count(Instance[] instances){
      for(Instance i : instances){
          if(i.label == Label.SPORTS){
              this.sportsCount += 1;
          }
          else if(i.label == Label.BUSINESS){
              this.businessCount += 1;
          }
      }
  }

  /*
   * Prints the number of documents for each label
   */
  public void print_documents_per_label_count(){
  	  System.out.println("SPORTS=" + sportsCount);
  	  System.out.println("BUSINESS=" + businessCount);
  }


  /*
   * Counts the total number of words for each label
   */
  public void words_per_label_count(Instance[] instances){
    for(Instance i : instances){
        for(String word : i.words){
            if(i.label == Label.SPORTS){
                this.sportsWordCount += 1;
            }
            else if(i.label == Label.BUSINESS){
                this.businessWordCount += 1;
            }
        }
    }
  }

  /*
   * Prints out the number of words for each label
   */
  public void print_words_per_label_count(){
  	  System.out.println("SPORTS=" + sportsWordCount);
  	  System.out.println("BUSINESS=" + businessWordCount);
  }

  /**
   * Returns the prior probability of the label parameter, i.e. P(SPORTS) or P(BUSINESS)
   */
  @Override
  public double p_l(Label label){
    if(label == Label.SPORTS){
        return (double)sportsCount/(double)trainingData.length;
    }
    else if(label == Label.BUSINESS){
        return (double)businessCount/(double)trainingData.length;
    }
    return 0;
  }

  /**
   * Returns the smoothed conditional probability of the word given the label, i.e. P(word|SPORTS) or
   * P(word|BUSINESS)
   */
  @Override
  public double p_w_given_l(String word, Label label){
    double numerator = 0;
    double denominator = -1;
    double summation = 0;
    this.delta = 0.00001;
    if(label == Label.SPORTS){
        if(!sportsMap.containsKey(word)){
            numerator = delta;
        }
        else{
            numerator = sportsMap.get(word) + delta;
        }
        for(Map.Entry entry : sportsMap.entrySet()){
            summation += (int)entry.getValue();
        }
        denominator = (this.v * this.delta) + summation;
    }
    else if(label == Label.BUSINESS){
        if(!businessMap.containsKey(word)){
            numerator = delta;
        }
        else{
            numerator = businessMap.get(word) + delta;
        }
        for(Map.Entry entry : businessMap.entrySet()){
            summation += (int)entry.getValue();
        }
        denominator = (this.v * this.delta) + summation;
    }
    return numerator/denominator;
  }

  /**
   * Classifies an array of words as either SPORTS or BUSINESS.
   */
  @Override
  public ClassifyResult classify(String[] words) {
    // Sum up the log probabilities for each word in the input data, and the probability of the label
    // Set the label to the class with larger log probability
    ClassifyResult ret = new ClassifyResult();
    ret.label = Label.SPORTS;
    ret.log_prob_sports = -1;
    ret.log_prob_business = -1;

    // Compute the sports probability
    ret.log_prob_sports = Math.log(p_l(Label.SPORTS));
    for(int i = 0; i < words.length; i++){
        ret.log_prob_sports += Math.log(p_w_given_l(words[i], Label.SPORTS));
    }

    // Compute the business probability
    ret.log_prob_business = Math.log(p_l(Label.BUSINESS));
    for(int i = 0; i < words.length; i++){
       ret.log_prob_business += Math.log(p_w_given_l(words[i], Label.BUSINESS));
    }

    // If business higher probability, make that the label instead, otherwise stays with sports
    if(ret.log_prob_business > ret.log_prob_sports){
        ret.label = Label.BUSINESS;
    }
    return ret; n t
  }
  
  /*
   * Constructs the confusion matrix
   */
  @Override
  public ConfusionMatrix calculate_confusion_matrix(Instance[] testData){
    // Count the true positives, true negatives, false positives, false negatives
    int TP, FP, FN, TN;
    TP = 0;
    FP = 0;
    FN = 0;
    TN = 0;
    for(Instance i : testData){
        Label classification = classify(i.words).label;
        if(i.label == Label.BUSINESS && classification == Label.BUSINESS){
            TN += 1;
        }
        else if(i.label == Label.SPORTS && classification == Label.BUSINESS){
            FN += 1;
        }
        else if(i.label == Label.SPORTS && classification == Label.SPORTS){
            TP += 1;
        }
        else if(i.label == Label.BUSINESS && classification == Label.SPORTS){
            FP += 1;
        }
    }
    return new ConfusionMatrix(TP,FP,FN,TN);
  }
  
}
