package edu.msudenver.cs.jrsstlabel;

import javax.swing.JApplet;
import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Vector;

// http://www.realapplets.com/tutorial/ThreadExample.html

public class JRSSTLabelApplet extends JApplet implements Runnable
{
    private JRSSTLabel label;

    public void init()
    {
	String args = getParameter ("args");

	if (args != null)
	{
	    args = args.trim().replaceAll ("\\s+", " ");
	 // System.err.println (new Vector (Arrays.asList (args.split (" "))));
	    label = new JRSSTLabel (args.split (" "));
	}
	else
	{
	    label = new JRSSTLabel (new String[]{});
	}

	setLayout (new BorderLayout());
	add (label, "Center");
	new Thread (this).start();
    }

    public void run() { label.doit(); }
}
