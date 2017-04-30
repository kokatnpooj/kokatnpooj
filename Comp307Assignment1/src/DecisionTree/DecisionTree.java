package DecisionTree;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


import java.util.*;
import java.io.*;

public class DecisionTree {

	public static int numCategories;
	public static int numAtts;
	public static List<String> categoryNames;
	public static List<String> attNames;
	public static Set<Instance> allInstances;
	public static Set<Instance> testInstances;
	private static DecisionNode root;
	public DecisionTree() {

	}
	public static void main(String[] args) {
		DecisionTree dt = new DecisionTree();
		dt.readFiles(args[0], args[1]);
		root = dt.buildTree(new HashSet<Instance>(allInstances), new ArrayList<String>(attNames));
		System.out.println();
		root.report("");
		dt.calculate();
	}

	public void readFiles(String train, String test){
		readDataFile(train);
		readTestDataFile(test);
	}

	private void readDataFile(String fname){
		/* format of names file:
		 * names of categories, separated by spaces
		 * names of attributes
		 * category followed by true's and false's for each instance
		 */
		System.out.println("Reading data from file "+fname);
		try {
			Scanner din = new Scanner(new File(fname));

			categoryNames = new ArrayList<String>();
			for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) categoryNames.add(s.next());
			numCategories=categoryNames.size();
			System.out.println(numCategories +" categories");
			attNames = new ArrayList<String>();
			for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) attNames.add(s.next());
			numAtts = attNames.size();
			System.out.println(numAtts +" attributes");
			allInstances = readInstances(din);
			din.close();
		}
		catch (IOException e) {
			throw new RuntimeException("Data File caused IO exception");
		}
	}

	/**
	 * Will use a scanner and return a list of the test instances
	 * @param din
	 * @return set of instances
	 */
	private void readTestDataFile(String fname){
		/* format of names file:
		 * names of categories, separated by spaces
		 * names of attributes
		 * category followed by true's and false's for each instance
		 */
		System.out.println("Reading data from file "+fname);
		try {
			Scanner din = new Scanner(new File(fname));
			din.nextLine();
			din.nextLine();
			testInstances = readInstances(din);
			din.close();
		}

		catch (IOException e) {
			throw new RuntimeException("Data File caused IO exception");
		}
	}

	/**
	 * Will use a scanner and return a list of the instances
	 * @param din
	 * @return set of instances
	 */
	private Set<Instance> readInstances(Scanner din){
		/* instance = classname and space separated attribute values */
		Set<Instance> instances = new HashSet<Instance>();
		String ln;
		while (din.hasNext()){ 
			Scanner line = new Scanner(din.nextLine());
			String next = line.next();
			instances.add(new Instance(categoryNames.indexOf(next),line, next));
		}
		System.out.println("Read " + instances.size()+" instances");
		return instances;
	}


	public DecisionNode buildTree(Set<Instance> instances, List<String> attributes){
		//if instances is empty
		//return a leaf node containing the name and probability of the overall
		//most probable class (ie, the ‘‘baseline’’ predictor)
		if (instances.isEmpty()){
			double prob = calcProbabilty(allInstances);
			LeafNode LeafNode = new LeafNode(findBaselinePredictor(allInstances),prob);
			return LeafNode;
		}
		//if instances are pure
		//return a leaf node containing the name of the class of the instances
		//in the node and probability 1
		double checkPure = calcPurity(instances);
		if(checkPure == 0){
			LeafNode LeafNode = new LeafNode(findBaselinePredictor(instances),1);
			return LeafNode;
		}
		//if attributes is empty
		//return a leaf node containing the name and probability of the
		//majority class of the instances in the node (choose randomly
		//if classes are equal)
		if(attributes.isEmpty()){
			LeafNode LeafNode = new LeafNode(findBaselinePredictor(instances),probMajorityClass(instances));
			return LeafNode;
		}

		else{
			String bestAttn = "";
			int bestAttnIndex = 0;
			double lowestPurity = Double.MAX_VALUE; //will store best purity for true
			Set <Instance>bestInstTrue = new HashSet<Instance>(); 		//separate instances into two sets
			Set <Instance>bestInstFalse = new HashSet<Instance>();		//separate instances into two sets

			for(int i = 0;i<attributes.size();i++){
				if(attributes.get(i) == null)continue;
				Set <Instance>trueSet = new HashSet<Instance>();
				Set <Instance>falseSet = new HashSet<Instance>();
				for(Instance inst : instances){
					if(inst.getAtt(i))
						trueSet.add(inst);
					else
						falseSet.add(inst);
				}
				double calculatedWeightedPurity = calculateWeightedPurity(trueSet,falseSet); 
				if(attributes.get(i).matches("SGOT|VARICES")){
					new Object();
				}
				if(calculatedWeightedPurity < lowestPurity){
					lowestPurity = calculatedWeightedPurity;
					bestAttn = attributes.get(i);
					bestAttnIndex = i;
					bestInstFalse = new HashSet<Instance>(falseSet);

					bestInstTrue = new HashSet<Instance>(trueSet);
				}
			}
			attributes.set(bestAttnIndex, null);

			DecisionNode left = buildTree(bestInstTrue , attributes);
			DecisionNode right = buildTree(bestInstFalse , attributes);
			attributes.set(bestAttnIndex, bestAttn); //build subtrees using the remaining attributes:
			Node nodeToReturn = new Node(bestAttn,left,right);
			return nodeToReturn;
		}
	}

	/**
	 * Will calculate the purity of a given instance
	 * @param instances
	 * @return
	 */
	public double calcPurity(Set<Instance> instances){ //smaller the better
		double numLive = 0;
		double numDie = 0;
		double purity = 0;

		for(Instance i: instances){
			if(i.getCategory()==0){
				numLive++;
			} else {
				numDie++;
			}
		}
		purity = (numLive*numDie)/((numLive+numDie)*(numLive+numDie)); 
		return purity;
	}
	/**
	 * Will calculate the probability for the most popular class
	 * @param instances
	 * @return
	 */
	public double calcProbabilty(Set<Instance> instances){
		double live = 0;
		double die = 0;
		double prob = 0;

		for(Instance i :instances){
			if(i.getCategory()==0)
				live+=1;
			else 
				die+=1;
		}
		if(live>die){
			prob = live/(live+die);
		}
		else{
			prob = die/(live+die);
		}
		return prob;
	}


	public double calculateWeightedPurity(Set<Instance> trueInstances, Set<Instance> falseInstances){
		double topTrueValue = (double)trueInstances.size()/((double)trueInstances.size() + (double)falseInstances.size());
		double topFalseValue = (double)falseInstances.size()/((double)trueInstances.size() + (double)falseInstances.size());
		double trueLiveCount = 0;
		double trueDieCount = 0;
		double falseLiveCount = 0;
		double falseDieCount = 0;
		for(Instance inst : trueInstances){
			if(inst.classLabel.equals(categoryNames.get(0))){
				trueLiveCount++;
			}else{
				trueDieCount++;
			}
		}
		for(Instance inst : falseInstances){
			if(inst.classLabel.equals(categoryNames.get(0))){
				falseLiveCount++;
			}else{
				falseDieCount++;
			}
		}

		double impurityTrue = trueInstances.size() == 0 ? 0 : (trueLiveCount/(trueLiveCount+trueDieCount)) * (trueDieCount/(trueLiveCount+trueDieCount));
		double impurityFalse = falseInstances.size() == 0 ? 0 : (falseLiveCount/(falseLiveCount+falseDieCount)) * (falseDieCount/(falseLiveCount+falseDieCount));
		double avg = (topTrueValue*impurityTrue) + (topFalseValue*impurityFalse);
		return avg;
	}


	public double probMajorityClass(Set<Instance> instances){
		int live = 0;
		int die = 0;
		for(Instance inst : instances){
			if(inst.getCategory()==0){
				live ++;
			}
			else{
				die++;
			}
		}
		if(live>die){
			return (live/(live+die));
		}
		else if(live<die){
			return (die/(live+die));
		}
		else{
			return (die/(live+die));
		}
	}


	public String findBaselinePredictor(Set<Instance> instances){
		HashMap<String, Integer> classLabels = new HashMap<String, Integer>();
		for (Instance inst: instances){
			classLabels.put(inst.classLabel, classLabels.getOrDefault(inst.classLabel, 0)+1); 
		}
		return classLabels.entrySet().stream().max((a, b) -> a.getValue().compareTo(b.getValue())).get().getKey();
	}

	public static int getPosition(String attribute){
		for (int i=0;i<DecisionTree.attNames.size();i++){
			if(attribute.equals(DecisionTree.attNames.get(i))){
				return i;
			}
		}
		return 0;
	}

	public void calculate(){
		double count = 0;
		for(Instance inst : testInstances){
			count= count +root.evaluateInstances(inst);
		}
		double percent = count/testInstances.size();
		System.out.println("Decision Tree "+percent);
		count = 0;
		for(Instance instOther: testInstances){
			if(instOther.classLabel.equals("live")){
				count+=1;
			}
		}
		percent = count/testInstances.size();
		System.out.println("Baseline Classifier "+percent);
	}

	class Instance {

		private int category;
		private List<Boolean> vals;
		public String classLabel;

		public Instance(int cat, Scanner s, String classLabel){
			category = cat;
			vals = new ArrayList<Boolean>();
			this.classLabel = classLabel;
			while (s.hasNextBoolean()) vals.add(s.nextBoolean());
		}

		public boolean getAtt(int index){
			return vals.get(index);
		}

		public int getCategory(){
			return category;
		}

		public String toString(){
			StringBuilder ans = new StringBuilder(categoryNames.get(category));
			ans.append(" ");
			for (Boolean val : vals)
				ans.append(val?"true  ":"false ");
			return ans.toString();
		}

	}


}
