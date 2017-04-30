package DecisionTree;

import DecisionTree.DecisionTree.Instance;

public class Node implements DecisionNode{
	String name;
	private DecisionNode left;
	private DecisionNode right;
	
	public Node(String name, DecisionNode left, DecisionNode right) {
		this.name = name;
		this.right = right;
		this.left = left;
	}
	
	public String getName(){
		return this.name;
	}

	public DecisionNode getLeft() {
		return left;
	}

	public void setLeft(DecisionNode left) {
		this.left = left;
	}

	public DecisionNode getRight() {
		return right;
	}

	public void setRight(DecisionNode right) {
		this.right = right;
	}
	

	
	
	public void report(String indent){

		System.out.format("%s%s = True:\n",
		indent, name);
		left.report(indent+" ");
		System.out.format("%s%s = False:\n",
		indent, name);
		right.report(indent+" ");
		}

	@Override
	public int evaluateInstances(Instance inst) {
		if(inst.getAtt(DecisionTree.getPosition(this.name))){
			return left.evaluateInstances(inst);
		}
		else{
			return right.evaluateInstances(inst);
		}
	

		}
	
	

}
