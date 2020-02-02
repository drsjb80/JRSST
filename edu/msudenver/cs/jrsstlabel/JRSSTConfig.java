package edu.mscd.cs.jrsstlabel;

import edu.mscd.cs.jclo.JCLO;
import edu.mscd.cs.javaln.JavaLN;
import edu.mscd.cs.javaln.LineNumberFormatter;

import java.net.URL;
import java.net.MalformedURLException;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

import java.util.Vector;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.ConsoleHandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.awt.Font;

public class JRSSTConfig
{
    private static JavaLN logger = JRSSTLabel.logger;
    JRSSTArgs jrsstargs = new JRSSTArgs();
    private Vector RSSItems;
    private final String defaultURL =
        "http://feeds.bbci.co.uk/news/rss.xml";

    Vector getRSSItems() { return (RSSItems); }

    public JRSSTArgs getJRSSTArgs() { return (jrsstargs); }

    private void readConfig (String where, boolean OPML)
	throws FileNotFoundException
    {
	logger.entering (new Object[]{where, new Boolean (OPML)});

	logger.fine ("Opening configuration file: \"" + where + "\"");

	if (jrsstargs.verbose)
            logger.info ("Reading " + where);

	Vector urls = null;

	try
	{
	    urls = OPML ? new ParseOPML (where, jrsstargs.fix_html).getURLs() :
		new ParseOldConfig (where, jrsstargs.fix_html).getURLs();
	}
	catch (FileNotFoundException fnfe)
	{
	    throw (fnfe);
	}
	catch (IOException ioe)
	{
	    logger.warning (ioe);
	}

	logger.finest (urls);
	for (Enumeration e = urls.elements(); e.hasMoreElements();)
	{
	    String next = e.nextElement().toString();
	    logger.finest (next);
	    ParseRSS prss = new ParseRSS (next, RSSItems, jrsstargs.fix_html);

	    if (jrsstargs.quick_init)
	    {
		new Thread (prss).start();
	    }
	    else
	    {
		prss.run();
	    }
	}

	try
	{
	    logger.fine ("Waiting...");
	    synchronized (RSSItems)
	    {
		RSSItems.wait (60000); // wait a minute!
	    }
	    logger.fine ("Done...");
	}
	catch (java.lang.InterruptedException IE)
	{
	    logger.warning (IE);
	}
    }

    private void dealWithFonts (JRSSTLabel label)
    {
	logger.entering ();

	Font font = label.getFont();
	int size = font.getSize();
	int style = font.getStyle();

	String fontname = jrsstargs.font;
	String fontsize = jrsstargs.font__size;
	String fontstyle = jrsstargs.font__style;

	if (fontname != null)
	{
	    label.setFont (new Font (fontname, style, size));
	}

	if (fontsize != null)
	{
	    float fs = Float.parseFloat (fontsize);
	    label.setFont (label.getFont().deriveFont (fs));
	}

	if (fontstyle != null)
	{
	    if (fontstyle.equals ("BOLD")) style = Font.BOLD;
	    if (fontstyle.equals ("ITALIC")) style = Font.ITALIC;
	    if (fontstyle.equals ("PLAIN")) style = Font.PLAIN;

	    label.setFont (label.getFont().deriveFont (style));
	}
    }

    public JRSSTConfig (String args[], JRSSTLabel label)
    {
	RSSItems = new Vector();

	try
	{
	    new JCLO (jrsstargs).parse (args);
	}
	catch (IllegalArgumentException iae)
	{
	    System.out.println ("Usage: " + new JCLO (jrsstargs).usage());
	    System.exit(1);
	}

        if (jrsstargs.version)
        {
            System.out.println (Version.getVersion());
            System.exit (0);
        }

	/*
	String load_defaults = (jrsstargs.load_defaults);
	if (load_defaults != null)
	{
	    try
	    {
		XMLDecoder d =
		    new XMLDecoder (new URL (load_defaults).openStream());
		jrsstargs = (JRSSTArgs) d.readObject();
		jargs = new JCLO (jrsstargs);
		d.close();
	    }
	    catch (MalformedURLException mue) { logger.warning (mue); }
	    catch (IOException ioe) { logger.warning (ioe); }
	}
	*/

	String loglevel = jrsstargs.loglevel;
	if (loglevel != null)
	{
	    Level level = JavaLN.getLevel (loglevel);
	    ConsoleHandler ch = new ConsoleHandler();
	    ch.setLevel (level);
	    ch.setFormatter (new LineNumberFormatter());
	    logger.setLevel (level);
	    logger.addHandler (ch);
	    logger.setUseParentHandlers (false);
    }

	boolean userInit = false;

	String homedir = System.getProperty ("user.home");
	if (homedir != null)
	{
	    String sep = System.getProperty ("file.separator");
	    String file = "file:" + homedir + sep + ".jrsst";

	    try
	    {
		if (jrsstargs.verbose)
		    logger.info ("Reading " + file);
	        readConfig (file, false);
		userInit = true;
	    }
	    catch (FileNotFoundException fnfe) { userInit = false; }
	    catch (IOException ioe) { logger.warning (ioe); }
	}

	String init[] = jrsstargs.init;
	if (init != null)
	{
	    userInit = true;

	    for (int i = 0; i < init.length; i++)
	    {
		try { readConfig (init[i], false); }
		catch (IOException ioe) { logger.warning (ioe); }
	    }
	}

	String opml[] = jrsstargs.opml;
	if (opml != null)
	{
	    userInit = true;

	    for (int i = 0; i < opml.length; i++)
	    {
		try { readConfig (opml[i], true); }
		catch (IOException ioe) { logger.warning (ioe); }
	    }
	}

	dealWithFonts (label);
 	
	String additional[] = jrsstargs.additional;
	if (additional != null)
	{
	    userInit = true;

	    for (int i = 0; i < additional.length; i++)
	    {
		if (jrsstargs.verbose)
		    logger.info ("Reading " + additional[i]);

		try
		{
		    ParseRSS prss = new ParseRSS (additional[i], RSSItems,
			jrsstargs.fix_html);
		    if (jrsstargs.quick_init)
		    {
			new Thread (prss).start();
		    }
		    else
		    {
			prss.run();
		    }

		}
		catch (FileNotFoundException FNFE)
		{
		    logger.warning (FNFE);
		}
	    }

	    try
	    {
		logger.finer ("Waiting...");
		synchronized (RSSItems)
		{
		    RSSItems.wait (60000); // wait a minute!
		}
		logger.finer ("Done...");
	    }
	    catch (java.lang.InterruptedException IE)
	    {
	        logger.warning (IE);
	    }
	}

	/*
	String save_defaults = (jrsstargs.save_defaults);
	if (save_defaults != null)
	{
	    try
	    {
		XMLEncoder e = new XMLEncoder (
		    (new FileOutputStream (save_defaults)));
		System.out.println (jrsstargs);
		e.writeObject (jrsstargs);
		e.close();
	    }
	    catch (IOException ioe) { logger.warning (ioe); }
	}
	*/

	if (! userInit)
	{
	    if (jrsstargs.verbose)
		logger.info ("Reading " + defaultURL);
	    try
	    {
		// only one, no need for a thread
		ParseRSS prss = new ParseRSS (defaultURL, RSSItems,
		    jrsstargs.fix_html);
	    }
	    catch (FileNotFoundException FNFE)
	    {
		logger.warning (FNFE);
	    }
	}

	logger.fine ("size = " + RSSItems.size());
    }
}
