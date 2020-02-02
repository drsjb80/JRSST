package edu.mscd.cs.jrsstlabel;

import java.util.Vector;
import java.util.Stack;
import java.util.logging.Level;
import edu.mscd.cs.javaln.JavaLN;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;


class ParseXMLs
{
    private static JavaLN logger = JRSSTLabel.logger;
    private boolean fixhtml;

    void skipBad (PushbackInputStream pbis, String s) throws IOException
    {
        int c;
        while ((c = pbis.read()) != '<')
        {
            if (c == -1)
                throw (new IOException ("Bad read from" + s));
            logger.fine ("Eating: " + c);
        }

        pbis.unread (c);
    }

    ParseXMLs (String s, DefaultHandler dh, boolean fixhtml)
        throws FileNotFoundException
    {
        try
        {
            URL url = new URL (s);
            InputStream is = url.openStream();
            PushbackInputStream pbis = new PushbackInputStream (is);

            skipBad (pbis, s);

            InputSource IS = new InputSource (pbis);

            if (is != null)
                SAXParserFactory.newInstance().newSAXParser().parse (IS, dh);

            pbis.close();
            is.close();
        }
        catch (SAXParseException spe)
        {
            logger.warning ("In " + s + ", at line " + spe.getLineNumber() +
                            ", column " + spe.getColumnNumber() + ", " + spe);
        }
        catch (ParserConfigurationException pce)
        {
            logger.warning (pce);
        }
        catch (SAXException se)
        {
            logger.warning (se);
        }
        catch (FileNotFoundException fnfe)
        {
            throw (fnfe);
        }
        catch (MalformedURLException mue)
        {
            logger.warning (mue);
        }
        catch (UnknownHostException uhe)
        {
            logger.warning (uhe);
        }
        catch (IOException ioe)
        {
            logger.warning (ioe);
        }
    }
}

class ParseOldConfig extends DefaultHandler
{
    private String current;
    private Vector URLs = new Vector();
    private static JavaLN logger = JRSSTLabel.logger;

    public ParseOldConfig (String s, boolean fixhtml)
        throws FileNotFoundException
    {
        super();
        new ParseXMLs (s, this, fixhtml);
    }

    public Vector getURLs()
    {
        return (URLs);
    }

    public void startElement (String uri, String localName, String qName,
        Attributes attributes) throws SAXException
    {

        if (qName.equals ("url") )
            current = "";
    }

    public void endElement (String namespaceURI, String lName, String qName)
        throws SAXException
    {

        if (qName.equals ("url") )
            URLs.add (current);
    }

    public void characters (char buf[], int offset, int len) throws SAXException
    {
        current += new String (buf, offset, len);
    }
}

class ParseOPML extends DefaultHandler
{
    private String current;
    private InputSource inputsource;
    private InputStream inputstream;
    private Vector URLs = new Vector();
    private static JavaLN logger = JRSSTLabel.logger;
    private URL url;

    public ParseOPML (String s, boolean fixhtml)
        throws FileNotFoundException
    {
        super();
        new ParseXMLs (s, this, fixhtml);
    }

    public Vector getURLs()
    {
        return (URLs);
    }

    public void startElement (String uri, String localName, String qName,
        Attributes attributes) throws SAXException
    {
        if (qName.equals ("outline") )
        {
            String toAdd = attributes.getValue ("xmlUrl");
            if (toAdd != null)
            {
                logger.finest ("Adding " + toAdd);
                logger.finest ("To " + URLs);
                URLs.add (toAdd);
                logger.finest ("Giving " + URLs);
            }
        }
    }
}

class ParseRSS extends DefaultHandler implements Runnable
{
    private String current;
    private InputSource inputsource;
    private InputStream inputstream;
    private Stack stack = new Stack();
    private Vector RSSItems;
    private Vector LocalRSSItems = new Vector();
    private static JavaLN logger = JRSSTLabel.logger;
    private URL url;
    private boolean RSS1;
    private boolean RSS2;
    private boolean ATOM;
    private Attributes attrs;
    private String channel;
    private String image;
    private String pubdate;
    private String title;
    private String link;
    private String description;
    private String s;
    private boolean fixhtml;

    public ParseRSS (String s, Vector RSSItems, boolean fixhtml)
        throws FileNotFoundException
    {
        super();
        this.s = s;
        this.RSSItems = RSSItems;
        this.fixhtml = fixhtml;
    }

    public void run()
    {
        try
        {
            new ParseXMLs (s, this, fixhtml);
        }
        catch (FileNotFoundException FNFE)
        {}

        synchronized (RSSItems)
        {
            RSSItems.addAll (LocalRSSItems);
            RSSItems.notify();
        }
    }

    public void startElement (String uri, String localName, String qName,
        Attributes attributes) throws SAXException
    {
        stack.push (qName);
        current = "";
        attrs = attributes;

        if (qName.equalsIgnoreCase ("rdf:RDF") )
        {
            RSS1 = true;
            logger.finest ("RSS1");
        }
        else if (qName.equalsIgnoreCase ("rss") )
        {
            RSS2 = true;
            logger.finest ("RSS2");
        }
        else if (qName.equalsIgnoreCase ("feed") )
        {
            ATOM = true;
            logger.finest ("ATOM");
        }
        else if (ATOM && qName.equalsIgnoreCase ("link") )
        {
            link = attributes.getValue ("href");
            logger.finest (link);
        }
    }

    public void endElement (String uri, String localName, String qName)
    throws SAXException
    {
        stack.pop();

        String enclosing = ! stack.empty() ? (String) stack.peek() : "";

        if (RSS1)
        {
            if (qName.equalsIgnoreCase ("image") &&
                    enclosing.equalsIgnoreCase ("channel") )
            {
                image = attrs.getValue ("rdf:resource");
                logger.finest (image);
            }
            else if (qName.equalsIgnoreCase ("title") )
            {
                if (enclosing.equalsIgnoreCase ("channel") )
                {
                    channel = current;
                    logger.finest (channel);
                }
                else if (enclosing.equalsIgnoreCase ("item") )
                {
                    title = current;
                    logger.finest (title);
                }
            }
            else if (qName.equalsIgnoreCase ("link") )
            {
                link = current;
                logger.finest (link);
            }
            else if (qName.equalsIgnoreCase ("description") )
            {
                description = current;
                logger.finest (description);
            }
        }
        else if (RSS2)
        {
            if (qName.equalsIgnoreCase ("url") &&
                    enclosing.equalsIgnoreCase ("image") )
            {
                image = current;
                logger.finest (image);
            }
            else if (qName.equalsIgnoreCase ("title") )
            {
                if (enclosing.equalsIgnoreCase ("channel") )
                {
                    channel = current;
                    logger.finest (channel);
                }
                else if (enclosing.equalsIgnoreCase ("item") )
                {
                    title = current;
                    logger.finest (title);
                }
            }
            else if (qName.equalsIgnoreCase ("link") &&
                     enclosing.equalsIgnoreCase ("item") )
            {
                link = current;
                logger.finest (link);
            }
            else if (qName.equalsIgnoreCase ("description") &&
                     enclosing.equalsIgnoreCase ("item") )
            {
                description = current;
                logger.finest (description);
            }
            else if (qName.equalsIgnoreCase ("pubDate") )
            {
                pubdate = current;
                logger.finest (pubdate);
            }
        }
        else if (ATOM)
        {
            if (qName.equalsIgnoreCase ("logo") &&
                    enclosing.equalsIgnoreCase ("feed") )
            {
                image = current;
                logger.finest (image);
            }
            else if (qName.equalsIgnoreCase ("title") )
            {
                if (enclosing.equalsIgnoreCase ("feed") )
                {
                    String rel = attrs.getValue ("rel");
                    if (rel != null && rel.equalsIgnoreCase ("alternate"));
                    {
                        channel = current;
                        logger.finest (channel);
                    }
                }
                else if (enclosing.equalsIgnoreCase ("entry") )
                {
                    title = current;
                    logger.finest (title);
                }
            }
            else if (qName.equalsIgnoreCase ("summary") )
            {
                description = current;
                logger.finest (description);
            }
            else if (qName.equalsIgnoreCase ("updated") )
            {
                pubdate = current;
                logger.finest (pubdate);
            }
        }

        if ((qName.equalsIgnoreCase ("item") && RSS1) ||
            (qName.equalsIgnoreCase ("item") && RSS2) ||
            (qName.equalsIgnoreCase ("entry") && ATOM))
        {
            RSSItem rssitem = new RSSItem (channel, title, link,
                description, image, pubdate, fixhtml);

            LocalRSSItems.add (rssitem);
            title = link = description = "";
        }
    }

    public void characters (char buf[], int offset, int len) throws SAXException
    {
        current += new String (buf, offset, len);
    }
}
