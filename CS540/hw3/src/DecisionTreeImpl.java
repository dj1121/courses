import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.lang.Math;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 * 
 * You must add code for the 1 member and 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
  private DecTreeNode root;
  //ordered list of class labels
  private List<String> labels; 
  //ordered list of attributes
  private List<String> attributes; 
  //map to ordered discrete values taken by attributes
  private Map<String, List<String>> attributeValues; 
  //map for getting the index
  private HashMap<String,Integer> label_inv;
  private HashMap<String,Integer> attr_inv;
  
  /**
   * Answers static questions about decision trees.
   */
  DecisionTreeImpl() {
    // no code necessary this is void purposefully
  }

  /**
   * Build a decision tree given only a training set.
   * 
   * @param train: the training set
   */
  DecisionTreeImpl(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    List<Instance> instances = train.instances;
    this.root = buildTree(null, instances, attributes, majorityLabel(instances));
  }

  DecTreeNode buildTree(String parentAttributeValue, List<Instance> instances, List<String> attributes, String defaultLabel){
      // End cases
      if(instances.isEmpty()){
          return new DecTreeNode(defaultLabel, null, parentAttributeValue, true);
      }
      if(sameLabel(instances)){
          return new DecTreeNode(instances.get(0).label, null, parentAttributeValue, true);
      }
      if(attributes.isEmpty()){
          return new DecTreeNode(majorityLabel(instances), null, parentAttributeValue, true);
      }
      // Make a node
      String q = bestAttribute(instances,attributes);
      List<String> qValues = attributeValues.get(q);
      DecTreeNode tree = new DecTreeNode(null, q, parentAttributeValue, false);

      // For each value v of attribute q
      for(String v : qValues){
          // Build subset examples with attribute q == v
          List<Instance> subset = new ArrayList<>();
          for(Instance i : instances){
              if(i.attributes.get(getAttributeIndex(q)).equals(v)){
                  subset.add(i);
              }
          }
          // Make child based on attribute q and subset of examples, remove attribute q from list
          for(String a : attributes){
              if(a.equals(q)){
                  attributes.remove(a);
                  break;
              }
          }
          tree.addChild(buildTree(v, subset, attributes, majorityLabel(instances)));
          attributes.add(q);
      }
      return tree;
  }

  String bestAttribute(List<Instance> instances, List<String> attributes){
      // Store info gain for each attribute in hashmap
      Map<String, Double> infoGains = new HashMap<>();
      Map.Entry<String, Double> maxGain = null;

      // For each attribute, compute its info gain considering the given instances
      for(String attribute : attributes){
          infoGains.put(attribute, InfoGain(instances, attribute));
      }

      // Find the attribute with highest info gain
      for(Map.Entry<String, Double> entry : infoGains.entrySet()){
          if(maxGain == null || entry.getValue().compareTo(maxGain.getValue()) > 0) {
              maxGain = entry;
          }
          else if(entry.getValue().compareTo(maxGain.getValue()) == 0){
              if(getAttributeIndex(entry.getKey()) < getAttributeIndex(maxGain.getKey())){
                  maxGain = entry;
              }
          }
      }
      return maxGain.getKey();
  }

  boolean sameLabel(List<Instance> instances){
      // If all the same label as the first, return true
      String first = instances.get(0).label;
      for(Instance i : instances){
          if(!i.label.equals(first)){
              return false;
          }
      }
      return true;
  }

  String majorityLabel(List<Instance> instances){
      // Store majority label info in hashmap
      Map<String,Integer> occurrences = new HashMap<>();
      Map.Entry<String, Integer> maxLabel = null;

      // Go through all instances, put values in hashmap (label,count)
      for(Instance i : instances){
          if(occurrences.containsKey(i.label)){
              occurrences.put(i.label, occurrences.get(i.label) + 1);
          }
          else{
              occurrences.put(i.label, 1);
          }
      }

      // Traverse hashmap, find key with highest value (label with largest count)
      for(Map.Entry<String, Integer> entry : occurrences.entrySet()){
        if(maxLabel == null || entry.getValue().compareTo(maxLabel.getValue()) > 0){
            maxLabel = entry;
        }
        else if(entry.getValue().compareTo(maxLabel.getValue()) == 0){
            if(getLabelIndex(entry.getKey()) < getLabelIndex(maxLabel.getKey())){
                maxLabel = entry;
            }
         }
      }
      return maxLabel.getKey();
  }

  double entropy(List<Instance> instances){
      double entropy = 0.0;

      // Keep a count of each label occurrence
      Map<String,Integer> map = new HashMap<>();
      for(Instance i : instances){
          if(map.containsKey(i.label)){
              map.put(i.label, map.get(i.label) + 1);
          }
          else{
              map.put(i.label, 1);
          }
      }
      // For every label, make occurrence / instance count and plug into formula
      for(Map.Entry<String, Integer> entry : map.entrySet()){
          if((double)entry.getValue() / (double)instances.size() != 0){
              entropy += ((double)-entry.getValue() / (double)instances.size()) * (Math.log((double)entry.getValue() / (double)instances.size()) / Math.log(2));
          }
      }
      return entropy;
  }

  double conditionalEntropy(List<Instance> instances, String attr){
      // Given the attribute, get all its possible values
      List<String> values = attributeValues.get(attr);
      double condEntropy = 0.0;

      // For each value of the attribute
      for(String v : values){
          // Get instances with that value of attribute
          List<Instance> instancesV = new ArrayList<>();
          for(Instance i : instances){
              if(i.attributes.get(getAttributeIndex(attr)).equals(v)){
                  instancesV.add(i);
              }
          }
          // Update conditional entropy with formula
          condEntropy += ((double)instancesV.size() / (double)instances.size()) * entropy(instancesV);
      }
      return condEntropy;
  }

  double InfoGain(List<Instance> instances, String attr){
      return entropy(instances) - conditionalEntropy(instances,attr);
  }

  @Override
  public String classify(Instance instance) {
      return classifyHelper(this.root, instance);
  }

  private String classifyHelper(DecTreeNode curr, Instance instance){

      if(curr.attribute == null && curr.children == null){
          return curr.label;
      }

      String instanceAttributeVal = instance.attributes.get(getAttributeIndex(curr.attribute));
      for(DecTreeNode child : curr.children){
          if(child.parentAttributeValue.equals(instanceAttributeVal)){
             return classifyHelper(child, instance);
          }
      }
      return null;
  }

  @Override
  public void rootInfoGain(DataSet train) {
    this.labels = train.labels;
    this.attributes = train.attributes;
    this.attributeValues = train.attributeValues;
    List<Instance> instances = train.instances;

    for(String a : attributes){
        System.out.format(a + " %.5f\n",InfoGain(instances, a));
    }
  }

  @Override
  public void printAccuracy(DataSet test) {
      List<Instance> instances = test.instances;
      double correct = 0;
      for(Instance i : instances){
          if(i.label.equals(classify(i))){
              correct++;
          }
      }
      System.out.format("%.5f\n", correct / (double)instances.size());
  }
  
  @Override
  /**
   * Print the decision tree in the specified format
   * Do not modify
   */
  public void print() {
    printTreeNode(root, null, 0);
  }

  /**
   * Prints the subtree of the node with each line prefixed by 4 * k spaces.
   * Do not modify
   */
  public void printTreeNode(DecTreeNode p, DecTreeNode parent, int k) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < k; i++) {
      sb.append("    ");
    }
    String value;
    if (parent == null) {
      value = "ROOT";
    } else {
      int attributeValueIndex = this.getAttributeValueIndex(parent.attribute, p.parentAttributeValue);
      value = attributeValues.get(parent.attribute).get(attributeValueIndex);
    }
    sb.append(value);
    if (p.terminal) {
      sb.append(" (" + p.label + ")");
      System.out.println(sb.toString());
    } else {
      sb.append(" {" + p.attribute + "?}");
      System.out.println(sb.toString());
      for (DecTreeNode child : p.children) {
        printTreeNode(child, p, k + 1);
      }
    }
  }

  /**
   * Helper function to get the index of the label in labels list
   */
  private int getLabelIndex(String label) {
    if(label_inv == null){
        this.label_inv = new HashMap<String,Integer>();
        for(int i=0; i < labels.size();i++)
        {
            label_inv.put(labels.get(i),i);
        }
    }
    return label_inv.get(label);
  }
 
  /**
   * Helper function to get the index of the attribute in attributes list
   */
  private int getAttributeIndex(String attr) {
    if(attr_inv == null)
    {
        this.attr_inv = new HashMap<String,Integer>();
        for(int i=0; i < attributes.size();i++)
        {
            attr_inv.put(attributes.get(i),i);
        }
    }
    return attr_inv.get(attr);
  }

  /**
   * Helper function to get the index of the attributeValue in the list for the attribute key in the attributeValues map
   */
  private int getAttributeValueIndex(String attr, String value) {
    for (int i = 0; i < attributeValues.get(attr).size(); i++) {
      if (value.equals(attributeValues.get(attr).get(i))) {
        return i;
      }
    }
    return -1;
  }
}
