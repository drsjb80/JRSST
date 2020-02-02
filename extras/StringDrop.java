import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

class StringDrop extends TransferHandler
{
    public boolean canImport(JComponent dest, DataFlavor[] flavors) {
	// System.out.println ("canImport called");

	for (int i = 0; i < flavors.length; i++)
	{
	    // System.out.println (flavors[i]);
	    if (DataFlavor.stringFlavor.equals (flavors[i])) return (true);
	}

	// System.out.println ("nope");
	return (false);
    }

    public boolean importData(JComponent c, Transferable t)
    {
	// System.out.println ("importData called");
	try
	{
	    String str = (String) t.getTransferData (DataFlavor.stringFlavor);
	    // System.out.println (str);
	    return true;
	}
	catch (UnsupportedFlavorException ufe) {}
	catch (IOException ioe) {}

        return false;
    }

    public static void main (String argv[])
    {
        JLabel one = new JLabel ("Drop some text on me");

	one.setTransferHandler (new StringDrop ());

        JFrame frame = new JFrame ("DandD");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout (new FlowLayout());
        frame.getContentPane().add (one);
        frame.pack();
        frame.setVisible (true);
    }
}
