package edu.mscd.cs.javaln;

import java.io.*;
import java.util.logging.*;
import java.util.Vector;
import java.util.Enumeration;

public class CompositeFormatter extends Formatter
{
    Vector formatters = new Vector();

    public CompositeFormatter()
    {
        super();
        String f =  LogManager.getLogManager().getProperty
            ("edu.mscd.cs.javaln.CompositeFormatter.formatters");

        if (f != null)
        {
            String names[] = f.split (",");

            for (int i = 0; i < names.length; i++)
            {
                try
                {
                    Class c = Class.forName (names[i].trim());
                    Formatter x = (Formatter) c.newInstance();
                    add (x);
                }
                catch (java.lang.ClassNotFoundException CNFE)
                {
                    System.err.println (CNFE);
                }
                catch (java.lang.InstantiationException IE)
                {
                    System.err.println (IE);
                }
                catch (java.lang.IllegalAccessException IAE)
                {
                    System.err.println (IAE);
                }
            }
        }
    }

    public void add (Formatter formatter)
    {
        formatters.add (formatter);
    }

    public String format (LogRecord rec)
    {
        String ret = "";

        Enumeration e = formatters.elements();

        while (e.hasMoreElements())
        {
            Formatter f = (Formatter) e.nextElement();
            ret += f.format (rec);
        }

        return (ret);
    }

    public static void main (String args[])
    {
        CompositeFormatter cf = new CompositeFormatter();
        cf.add (new NullFormatter());
        cf.add (new LineNumberFormatter());

        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter (cf);
        ch.setLevel (Level.FINEST);

        Logger logger = Logger.getLogger ("global");
        logger.addHandler (ch);
        logger.setUseParentHandlers (false);
        logger.setLevel (Level.FINEST);

        logger.severe ("this is a test");
        logger.finest ("this is another");
    }
}
