import java.util.Enumeration;

class Properties
{
    public static void main (String args[])
    {
	for (Enumeration e = System.getProperties().propertyNames();
	    e.hasMoreElements();)
	{
	    String key = e.nextElement().toString();
	    String value = System.getProperties().getProperty (key);
	    System.out.println (key + ": " + value);
	}
    }
}
