package edu.mscd.cs.javaln;

import java.io.*;
import java.util.logging.*;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Print only messages from a set of possiblities
 */

abstract class SetFilter implements Filter
{
    private final HashSet set = new HashSet();

    /**
     * Add a comma separated list of classes to log.  If there is only one,
     * don't use any commas.
     */
    public void add (String s)
    {
        if (s != null)
        {
            String names[] = s.split (",");

            for (int i = 0; i < names.length; i++)
            {
                set.add ((Object) names[i].trim());
            }
        }
    }

    /**
     * Add a comma separated list of classes to stop logging.  If there is
     * only one, don't use any commas.
     */
    public void remove (String s)
    {
        if (s != null)
        {
            String names[] = s.split (",");

            for (int i = 0; i < names.length; i++)
            {
                set.remove (names[i].trim());
            }
        }
    }

    public String toString()
    {
        String s = getClass().getName() + " = ";

        for (Iterator i = set.iterator(); i.hasNext() ;)
        {
            String l = (String) i.next();
            s += l.toString() + " ";
        }

        return (s);
    }

    protected boolean isLoggable (String s)
    {
        /*
        System.out.println ("s = " + s);
        for (Iterator i = set.iterator(); i.hasNext() ;)
        {
            String l = (String) i.next();
            System.out.println ("l = " + l);
            System.out.println ("l.equals = " + l.equals (s));
        }
        */
        return (set.contains (s));
    }
}
