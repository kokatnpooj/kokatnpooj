package Perceptron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Perceptron {

	private String categoryName;
	private  List<Images> imageList;
	private List<Feature> features;
	private double[] weights;
	private List<double[]> featureValues;
	public static final Random random = new Random(); //random seed suggested 
	public static int ITERATION = 100;
	public static int FEATURES = 50;
	private double threshold = -1;


	public Perceptron(List<Images> images, String categoryName) {
		this.imageList = images;
		this.categoryName = categoryName;
		setValues();
		perceptron();

	}

	public static void main(String[] args) {
		if(args.length != 1){
			return;
		}
		Loader load = new Loader();
		List<Images> images = load.loadImages(args[0]);
		String categoryName = load.getCategoryName();
		new Perceptron(images, categoryName);
	}


	public void setValues(){
		features = new ArrayList<>();
		for (int i = 0; i < FEATURES; i++)
			features.add(new Feature());

		weights = new double[FEATURES + 1];
		weights[0]= threshold;
		for (int i = 1; i < weights.length; i++)

			weights[i] = 0; //weights will be 0 for now 


		featureValues = new ArrayList<>();
		for (int i = 0; i < imageList.size(); i++) {
			//evaluate the features and add to list
			featureValues.add(evaluateFeatures(features, imageList.get(i)));
		}
	}

	/**
	 * Will evaluate specified image 
	 * @param features
	 * @param image
	 * @return
	 */
	public static double[] evaluateFeatures(List<Feature> features, Images image) {
		double[] vals = new double[features.size() + 1]; 
		//dummy value
		vals[0] = 1;  
		for (int i = 1; i < features.size(); i++) {
			Feature feature = features.get(i);
			vals[i] = feature.check(image);
		}
		return vals;
	}

	/**
	 * Will evaluate the perceptron
	 * @param weights
	 * @param featureValues
	 * @return
	 */
	public static double evaluatePerceptron(double[] weights, double[] featureValues) {
		double evaluatedVal = 0;
		for (int i = 0; i < weights.length; i++) {
			evaluatedVal += weights[i] * featureValues[i];// sum the weight for each index
		}
		return evaluatedVal;
	}

	/**
	 * Will check the correctness/accuracy of the perceptron algorithm
	 * @param featureVals
	 * @param weights
	 * @param images
	 * @param categoryName
	 * @return
	 */
	public static int checkPerceptron(List<double[]> featureVals, double[] weights, List<Images> images, String categoryName){
		int numCorrect = 0;
		for(int i=0; i < featureVals.size(); i++){
			double[] featureVal = featureVals.get(i);
			double p =  evaluatePerceptron(weights, featureVal);
			boolean isPositive = images.get(i).getCategory().equals(categoryName);
			if ((p > 0 && isPositive) || (p <= 0 && !isPositive))
				numCorrect++;
		}
		return numCorrect;
	}

	public void perceptron(){
		int check = 0;
		int epoch = 0;
		int count = 0;
		while(epoch<ITERATION) { //Until the perceptron is always right (or some limit)
			if (check == imageList.size()){
				break;
			}
			for (int i = 0; i < imageList.size(); i++) {
				Images image = imageList.get(i);
				double[] imageFeature = featureValues.get(i);
				double evaluatedPerceptron = evaluatePerceptron(weights, imageFeature);
				boolean checkFlag = image.getCategory().equals(categoryName);
				//If perceptron is correct, do nothing
				if ((evaluatedPerceptron > 0 && checkFlag==true) || (evaluatedPerceptron <= 0 && checkFlag!=true)) {
				} 
				// -ve and wrong (weights on features are too high)
				else if (checkFlag!=true) {
					for (int j = 0; j < weights.length; j++){
						weights[j] = weights[j]- imageFeature[j];// Subtract feature vector from weight vector
					}
					System.out.println(Arrays.toString(weights));

				}
				// +ve and wrong (weights on feature are too low)
				else {
					for (int j = 0; j < weights.length; j++){
						weights[j] = weights[j]+ imageFeature[j];  // Add feature vector to weight vector
					}
					System.out.println(Arrays.toString(weights));
				}
				check = checkPerceptron(featureValues, weights, imageList, categoryName);
			}
			
			if(epoch %10==0){
				System.out.println("Accuracy: "+ (100 * check / (double) imageList.size())+" Epoch "+ (epoch));
			}
			epoch++;
		}
		System.out.println("Final Accuracy: "+ (100 * check / (double) imageList.size())+" Epoch "+ (epoch));
	}
	 
}
