package KNN;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;


public class KNN {
	int firstArg=0;
	public static HashMap<Iris, String> trainingSet = new HashMap<Iris, String>();
	public static HashMap<Iris, String> testSet = new HashMap<Iris, String>();
	public static ArrayList<HashMap<Iris, Double>> finallist = new ArrayList<HashMap<Iris, Double>>();

	public static double rangeSepalLength;
	public static double rangeSepalWidth;
	public static double rangePetallLength;
	public static double rangePetallWidth;
	public static int accuracyVal;
	public static int kValue;

	public KNN(){
		rangeSepalLength=0;
		rangeSepalWidth=0;
		rangePetallLength=0;
		rangePetallWidth=0;
		kValue = 0;
	}

	public static void readTest(File fileName) {
		try{
			Scanner scan=new Scanner(new FileReader(fileName));
			while(scan.hasNext()){
				double sepalLength = scan.nextDouble();
				double sepalWidth = scan.nextDouble();
				double petalLength = scan.nextDouble();
				double petalWidth = scan.nextDouble();
				String irisType = scan.next();
				Iris iris= new Iris(irisType,sepalLength,sepalWidth,petalLength,petalWidth);
				testSet.put(iris, irisType);
				scan.nextLine();
			}
			scan.close();
		}
		catch(IOException e){
			System.out.println("TestFileError");
		}
	}

	public static void readTraining(File fileName) {
		try{

			Scanner scan=new Scanner(new FileReader(fileName));
			while(scan.hasNext()){
				double sepalLength = scan.nextDouble();
				double sepalWidth = scan.nextDouble();
				double petalLength = scan.nextDouble();
				double petalWidth = scan.nextDouble();
				String irisType = scan.next();
				Iris iris= new Iris(irisType,sepalLength,sepalWidth,petalLength,petalWidth);
				trainingSet.put(iris, irisType);
				scan.nextLine();
			}
			scan.close();
		}
		catch(IOException e){
			System.out.println("TrainingSetError");
		}
	}

	/**
	 * This method will calculate the range values for all features
	 */
	public static void calcRangeValues(){
		double maxSepalLen=0;
		double minSepalLen=1000;
		double maxSepalWidth=0;
		double minSepalWidth=1000;
		double maxPetalLen=0;
		double minPetalLen=1000;
		double maxPetalWidth=0;
		double minPetalWidth=1000;
		for(Iris i : trainingSet.keySet()){
			//min and max for Sepal Width
			if(i.getSepalWidth()>maxSepalWidth){
				maxSepalWidth=i.getSepalWidth();
			}
			if(i.getSepalWidth()<minSepalWidth){
				minSepalWidth=i.getSepalWidth();
			}

			//min and max for Sepal Length
			if(i.getSepalLength()>maxSepalLen){
				maxSepalLen=i.getSepalLength();
			}
			if(i.getSepalLength()<minSepalLen){
				minSepalLen=i.getSepalLength();
			}

			//min and max for Petal Width
			if(i.getPetalWidth()>maxPetalWidth){
				maxPetalWidth=i.getPetalWidth();
			}
			if(i.getPetalWidth()<minPetalWidth){
				minPetalWidth=i.getPetalWidth();
			}

			//min and max for Petal Length
			if(i.getPetalLength()>maxPetalLen){
				maxPetalLen=i.getPetalLength();
			}
			if(i.getPetalLength()<minPetalLen){
				minPetalLen=i.getPetalLength();
			}
		}
		//calculating the R values
		rangeSepalLength=maxSepalLen-minSepalLen;
		rangeSepalWidth=maxSepalWidth-minSepalWidth;
		rangePetallLength=maxPetalLen-minPetalLen;
		rangePetallWidth=maxPetalWidth-minPetalWidth;
	}

	public static void calcDistanceMeasure(){
		String predicted = "";
		for(Iris i : testSet.keySet()){
			HashMap<Iris, Double> eucledianDistances = new HashMap<Iris, Double>();
			for(Iris j: trainingSet.keySet()){
				double distance = distanceToOtherIris(i, j);
				eucledianDistances.put(j,distance);
			}
			eucledianDistances=sortDistances(eucledianDistances);
			predicted=findKNearestN(eucledianDistances, kValue);
			System.out.println(predicted+"        "+ testSet.get(i));
			calcAccuracy(predicted,testSet.get(i));
		}
		System.out.println("--------------------------------");
		System.out.println("Total accurate  "+accuracyVal +" out of "+testSet.size());
	}

	public static int calcAccuracy(String predicted, String actual){
		if(predicted.equals(actual)){
			accuracyVal++;
		}
		return accuracyVal;
	}


	//Using the eucledian equation to find the distances
	public static double distanceToOtherIris(Iris testIris, Iris trainingIris){
		double sepalLengthDistance = Math.pow(testIris.getSepalLength() - trainingIris.sepalLength, 2) / Math.pow(rangeSepalLength, 2);
		double sepalWidthDistance = Math.pow(testIris.getSepalWidth() - trainingIris.sepalWidth, 2) / Math.pow(rangeSepalWidth, 2);
		double petalLengthDistance = Math.pow(testIris.getPetalLength() - trainingIris.petalLength, 2) / Math.pow(rangePetallLength, 2);
		double petalWidthDistance = Math.pow(testIris.getPetalWidth()- trainingIris.petalWidth, 2) / Math.pow(rangePetallWidth, 2);

		return Math.sqrt(sepalLengthDistance + sepalWidthDistance + petalLengthDistance + petalWidthDistance);
	}

	//Sorting the distances found to find 'k' nearest neighbours
	public static HashMap<Iris, Double> sortDistances(HashMap distances){
		LinkedList list = new LinkedList(distances.entrySet());
		HashMap sortedDist = new LinkedHashMap();
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		for(Iterator it = list.iterator() ; it.hasNext();){
			Map.Entry entry = (Map.Entry) it.next();
			sortedDist.put(entry.getKey(), entry.getValue());
		}
		return sortedDist;
	}



	public static String findKNearestN(HashMap<Iris,Double> distances,int kVal){
		int irisSetosaCount=0;
		int irisVesicolorCount=0;
		int irisVirginicaCount=0;
		String predicted = "";
		HashMap<Iris, Double> newDist = distances;
		for(Iris iris:newDist.keySet()){
			for (int i =0 ;i<kVal;i++){
				if(iris.irisType.equals("Iris-setosa")){
					irisSetosaCount++;
				}if(iris.irisType.equals("Iris-versicolor")){
					irisVesicolorCount++;
				}if(iris.irisType.equals("Iris-virginica")){
					irisVirginicaCount++;
				}	
			}
			break;
		}
		if((irisSetosaCount>irisVesicolorCount) &&(irisSetosaCount>irisVirginicaCount)){
			predicted = "Iris-setosa";
		}
		else if((irisVesicolorCount>irisSetosaCount)&& (irisVesicolorCount>irisVirginicaCount)){
			predicted = "Iris-versicolor";
		}
		else if((irisVirginicaCount>irisVesicolorCount) && (irisVirginicaCount>irisVesicolorCount)){
			predicted = "Iris-virginica";
		}
		else {
			predicted="issue";
		}

		return predicted;
	}

	public static void main(String[] args) {
		if(args.length>1){
			System.out.println(args[0]);
			File trainingFile=new File(args[0]);
			System.out.println(args[1]);
			File testFile=new File(args[1]);
			kValue = Integer.valueOf(args[2]);
			System.out.println(kValue);
			readTraining(trainingFile);
			readTest(testFile);
		}
		long startTime = System.currentTimeMillis();
		calcRangeValues();
		calcDistanceMeasure();
		long endTime = System.currentTimeMillis();
		long totalTime = endTime-startTime;
		System.out.println("Time taken : " +totalTime+ " milliseconds");
	}
}


