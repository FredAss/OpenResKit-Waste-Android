package htw.bui.openreskit.waste;

public class FillLevel {

	public double fillLevel;
	public String stringValue;
	
	public FillLevel(double _fillLevel, String _value) 
	{
		fillLevel = _fillLevel;
		stringValue = _value;
	}
	
	public String toString()
	{
	    return stringValue;
	}
}
