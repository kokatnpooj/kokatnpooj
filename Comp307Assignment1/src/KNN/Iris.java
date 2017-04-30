package KNN;
public class Iris {
	String irisType;
	double sepalLength;
	double sepalWidth;
	double petalLength;
	double petalWidth;

	public Iris(String irisType, double sepalLength, double sepalWidth, double petalLength, double petalWidth) {
		this.irisType = irisType;
		this.sepalLength = sepalLength;
		this.sepalWidth = sepalWidth;
		this.petalLength = petalLength;
		this.petalWidth = petalWidth;
	}

	public String getType()
	{
	    return this.irisType;
	}
	public double getSepalLength()
	{
	    return this.sepalLength;
	}

	public double getSepalWidth()
	{
	    return this.sepalWidth;
	}

	public double getPetalLength()
	{
	    return this.petalLength;
	}
	public double getPetalWidth()
	{
	    return this.petalWidth;
	}

}
