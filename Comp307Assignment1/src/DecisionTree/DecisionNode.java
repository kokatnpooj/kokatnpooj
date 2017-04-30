package DecisionTree;

import DecisionTree.DecisionTree.Instance;

public interface DecisionNode {
	public void report(String indent);

	public int evaluateInstances(DecisionTree.Instance inst);

	
		
	
}
