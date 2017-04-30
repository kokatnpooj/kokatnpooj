package KNN;
public class IrisVirginica {
	String irisType;
	double sepalLength;
	double sepalWidth;
	double petalLength;
	double petalWidth;

	public IrisVirginica(String irisType, double sepalLength, double sepalWidth, double petalLength, double petalWidth) {
		irisType = this.irisType;
		sepalLength = this.sepalLength;
		sepalWidth = this.sepalWidth;
		petalLength = this.petalLength;
		petalWidth = this.petalWidth;
	}

	public void setIrisType(String irisType)
	{
		irisType = this.irisType;
	}

	public void setSepalLength(double sepalLength)
	{
		sepalLength = this.sepalLength;
	}

	public void setSepalWidth(double sepalWidth)
	{
		sepalWidth = this.sepalWidth;
	}

	public void setPetalLength(double petalLength)
	{
		petalLength = this.petalLength;
	}
	public void setPetalWidth(double petalWidth)
	{
		petalWidth = this.petalWidth;
	}


	public String setSepalLength()
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