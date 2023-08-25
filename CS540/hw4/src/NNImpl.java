import java.util.*;

/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 */

public class NNImpl {
    private ArrayList<Node> inputNodes; //list of the output layer nodes.
    private ArrayList<Node> hiddenNodes;    //list of the hidden layer nodes
    private ArrayList<Node> outputNodes;    // list of the output layer nodes

    private ArrayList<Instance> trainingSet;    //the training set

    private double learningRate;    // variable to store the learning rate
    private int maxEpoch;   // variable to store the maximum number of epochs
    private Random random;  // random number generator to shuffle the training set

    /**
     * This constructor creates the nodes necessary for the neural network
     * Also connects the nodes of different layers
     * After calling the constructor the last node of both inputNodes and
     * hiddenNodes will be bias nodes.
     */

    NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Random random, Double[][] hiddenWeights, Double[][] outputWeights) {
        this.trainingSet = trainingSet;
        this.learningRate = learningRate;
        this.maxEpoch = maxEpoch;
        this.random = random;

        //input layer nodes
        inputNodes = new ArrayList<>();
        int inputNodeCount = trainingSet.get(0).attributes.size();
        int outputNodeCount = trainingSet.get(0).classValues.size();
        for (int i = 0; i < inputNodeCount; i++) {
            Node node = new Node(0);
            inputNodes.add(node);
        }

        //bias node from input layer to hidden
        Node biasToHidden = new Node(1);
        inputNodes.add(biasToHidden);

        //hidden layer nodes
        hiddenNodes = new ArrayList<>();
        for (int i = 0; i < hiddenNodeCount; i++) {
            Node node = new Node(2);
            //Connecting hidden layer nodes with input layer nodes
            for (int j = 0; j < inputNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
                node.parents.add(nwp);
            }
            hiddenNodes.add(node);
        }

        //bias node from hidden layer to output
        Node biasToOutput = new Node(3);
        hiddenNodes.add(biasToOutput);

        //Output node layer
        outputNodes = new ArrayList<>();
        for (int i = 0; i < outputNodeCount; i++) {
            Node node = new Node(4);
            //Connecting output layer nodes with hidden layer nodes
            for (int j = 0; j < hiddenNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
                node.parents.add(nwp);
            }
            outputNodes.add(node);
        }
    }

    /**
     * Get the prediction from the neural network for a single instance
     * Return the idx with highest output values. For example if the outputs
     * of the outputNodes are [0.1, 0.5, 0.2], it should return 1.
     * The parameter is a single instance
     */

    public int predict(Instance instance) {
        // Forward pass
        // For input nodes set inputs to example attribute values
        for (int j = 0; j < inputNodes.size() - 1; j++) {
            inputNodes.get(j).setInput(instance.attributes.get(j));
        }
        // For hidden nodes
        for (int j = 0; j < hiddenNodes.size(); j++) {
            hiddenNodes.get(j).calculateOutput();
        }
        // For output nodes
        double sum = 0.0;
        for (int j = 0; j < outputNodes.size(); j++) {
            outputNodes.get(j).calculateOutput();
            sum += outputNodes.get(j).getOutput();
        }
        // Normalize output node outputs (denominator)
        for (int j = 0; j < outputNodes.size(); j++) {
            outputNodes.get(j).normalize(sum);
        }
        // Find output node with biggest activation, return index
        double highest = 0;
        int indexHighest = 0;
        for(int j = 0; j < outputNodes.size(); j++){
            if(outputNodes.get(j).getOutput() > highest){
                indexHighest = j;
                highest = outputNodes.get(j).getOutput();
            }
        }
        return indexHighest;
    }

    /**
     * Train the neural networks with the given parameters
     * <p>
     * The parameters are stored as attributes of this class
     */

    public void train() {
        // For each epoch
        for(int k = 0; k < maxEpoch; k++){
            Collections.shuffle(trainingSet, random);
            double loss = 0;
            // For each example in training set
            for(Instance i : trainingSet){
                // Forward pass
                // For input nodes set inputs to example attribute values
                for(int j = 0; j < inputNodes.size()-1; j++){
                    inputNodes.get(j).setInput(i.attributes.get(j));
                }
                // For hidden nodes
                for(int j =0; j < hiddenNodes.size(); j++){
                    hiddenNodes.get(j).calculateOutput();
                }
                // For output nodes
                double sum = 0.0;
                for(int j =0; j < outputNodes.size(); j++){
                    outputNodes.get(j).calculateOutput();
                    sum += outputNodes.get(j).getOutput();
                }
                // Normalize output node outputs (denominator)
                for(int j =0; j < outputNodes.size(); j++){
                    outputNodes.get(j).normalize(sum);
                }

                // Backward pass
                // For each node in output layer calculate delta K
                for(int j = 0; j < outputNodes.size(); j++){
                    outputNodes.get(j).calculateDelta(null, j, i);
                }
                // For each node in hidden layer calculate delta J
                for(int j = 0; j < hiddenNodes.size(); j++){
                    hiddenNodes.get(j).calculateDelta(outputNodes, j, null);
                }
                // Update weights from between hidden and output
                for(int j = 0; j < outputNodes.size(); j++){
                    outputNodes.get(j).updateWeight(learningRate);
                }
                // Update weights between input and hidden
                for(int j = 0; j < hiddenNodes.size(); j++){
                    hiddenNodes.get(j).updateWeight(learningRate);
                }
            }
            // Another forward pass for each training example
            for(Instance i : trainingSet){
                // Forward pass
                // For input nodes set inputs to example attribute values
                for(int j = 0; j < inputNodes.size()-1; j++){
                    inputNodes.get(j).setInput(i.attributes.get(j));
                }
                // For hidden nodes
                for(int j =0; j < hiddenNodes.size(); j++){
                    hiddenNodes.get(j).calculateOutput();
                }
                // For output nodes
                double sum = 0.0;
                for(int j =0; j < outputNodes.size(); j++){
                    outputNodes.get(j).calculateOutput();
                    sum += outputNodes.get(j).getOutput();
                }
                // Normalize output node outputs (denominator)
                for(int j =0; j < outputNodes.size(); j++){
                    outputNodes.get(j).normalize(sum);
                }
                loss += loss(i);
            }
            // Calculate loss
            loss = loss / trainingSet.size();
            System.out.print("Epoch: " + k + ", Loss: ");
            System.out.printf("%.8e\n", loss);
        }
    }

    /**
     * Calculate the cross entropy loss from the neural network for
     * a single instance.
     * The parameter is a single instance
     */
    private double loss(Instance instance) {
        //  Same output index as teacher index
        double loss = 0.0;
        for(int i = 0; i < outputNodes.size(); i++){
            loss += (instance.classValues.get(i)) * Math.log(outputNodes.get(i).getOutput());
        }
        return -loss;
    }
}
