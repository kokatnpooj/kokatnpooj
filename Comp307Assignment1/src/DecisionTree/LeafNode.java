package DecisionTree;

import DecisionTree.DecisionTree.Instance;

public class LeafNode implements DecisionNode{
	String name;
	double probability;
	
	public LeafNode(String name, double probability) {
		this.name = name;
		this.probability = probability;
	}
	
	public String getName(){
		return this.name;
	}
	
	public double getProbability(){
		return this.probability;
	}
	
	public void report(String indent){
		System.out.format("%sClass %s, prob=%f\n",
		indent, name, probability);
		}

	@Override
	public int evaluateInstances(Instance inst) {
		if(inst.classLabel.equals(this.name)){
			return 1;
		}
		else{
			return 0;
		}
		
	}
	

}
