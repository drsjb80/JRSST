package edu.msudenver.cs.jrsstlabel;

import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.util.Stack;
import java.util.Vector;

import static org.apache.logging.log4j.LogManager.getLogger;

class ParseXMLs {
    static final Logger logger = getLogger("JRSST");

    void skipBad(PushbackInputStream pbis, String s) throws IOException {
        int c;
        while ((c = pbis.read()) != '<') {
            if (c == -1)
                throw (new IOException("Bad read from" + s));
            logger.trace("Eating: " + c);
        }

        pbis.unread(c);
    }

    ParseXMLs(String s, DefaultHandler dh, boolean fixhtml)
            throws FileNotFoundException {
        try {
            URL url = new URL(s);
            InputStream is = url.openStream();
            PushbackInputStream pbis = new PushbackInputStream(is);

            skipBad(pbis, s);

            InputSource IS = new InputSource(pbis);

            if (is != null) {
                SAXParserFactory.newInstance().newSAXParser().parse(IS, dh);
            }

            pbis.close();
            assert is != null;
            is.close();
        } catch (SAXParseException spe) {
            logger.warn ("In " + s + ", at line " + spe.getLineNumber() +
                    ", column " + spe.getColumnNumber() + ", " + spe);
        } catch (FileNotFoundException fnfe) {
            logger.warn(fnfe);
            throw (fnfe);
        } catch (ParserConfigurationException | SAXException | IOException pce) {
            logger.warn(pce);
        }
    }
}

class ParseOldConfig extends DefaultHandler {
    private String current;
    private Vector<String> URLs = new Vector<>();

    public ParseOldConfig(String s, boolean fixhtml)
            throws FileNotFoundException {
        super();
        new ParseXMLs(s, this, fixhtml);
    }

    public Vector<String> getURLs() {
        return (URLs);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes)  {

        if (qName.equals("url"))
            current = "";
    }

    public void endElement(String namespaceURI, String lName, String qName) {

        if (qName.equals("url"))
            URLs.add(current);
    }

    public void characters(char[] buf, int offset, int len) {
        current += new String(buf, offset, len);
    }
}

class ParseOPML extends DefaultHandler {
    private Vector<String> URLs = new Vector<>();
    static final Logger logger = getLogger("JRSST");

    public ParseOPML(String s, boolean fixhtml)
            throws FileNotFoundException {
        super();
        new ParseXMLs(s, this, fixhtml);
    }

    public Vector<String> getURLs() {
        return (URLs);
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("outline")) {
            String toAdd = attributes.getValue("xmlUrl");
            if (toAdd != null) {
                logger.trace("Adding " + toAdd);
                logger.trace("To " + URLs);
                URLs.add(toAdd);
                logger.trace("Giving " + URLs);
            }
        }
    }
}

class ParseRSS extends DefaultHandler implements Runnable {
    private String current;
    // private InputSource inputsource;
    // private InputStream inputstream;
    private Stack<String> stack = new Stack<>();
    private final Vector<RSSItem> RSSItems;
    private Vector<RSSItem> LocalRSSItems = new Vector<>();
    static final Logger logger = getLogger("JRSST");
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

    public ParseRSS(String s, Vector<RSSItem> RSSItems, boolean fixhtml) {
        super();
        this.s = s;
        this.RSSItems = RSSItems;
        this.fixhtml = fixhtml;
    }

    public void run() {
        logger.traceEntry();
        try {
            new ParseXMLs(s, this, fixhtml);
        } catch (FileNotFoundException FNFE) {
            logger.info("Unable to open " + s);
        }

        synchronized (RSSItems) {
            RSSItems.addAll(LocalRSSItems);
            RSSItems.notify();
        }
    }

    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {
        stack.push(qName);
        current = "";
        attrs = attributes;

        if (qName.equalsIgnoreCase("rdf:RDF")) {
            RSS1 = true;
            logger.trace("RSS1");
        } else if (qName.equalsIgnoreCase("rss")) {
            RSS2 = true;
            logger.trace("RSS2");
        } else if (qName.equalsIgnoreCase("feed")) {
            ATOM = true;
            logger.trace("ATOM");
        } else if (ATOM && qName.equalsIgnoreCase("link")) {
            link = attributes.getValue("href");
            logger.trace(link);
        }
    }

    public void endElement(String uri, String localName, String qName) {
        stack.pop();

        String enclosing = !stack.empty() ? stack.peek() : "";

        if (RSS1) {
            if (qName.equalsIgnoreCase("image") &&
                    enclosing.equalsIgnoreCase("channel")) {
                image = attrs.getValue("rdf:resource");
                logger.trace(image);
            } else if (qName.equalsIgnoreCase("title")) {
                if (enclosing.equalsIgnoreCase("channel")) {
                    channel = current;
                    logger.trace(channel);
                } else if (enclosing.equalsIgnoreCase("item")) {
                    title = current;
                    logger.trace(title);
                }
            } else if (qName.equalsIgnoreCase("link")) {
                link = current;
                logger.trace(link);
            } else if (qName.equalsIgnoreCase("description")) {
                description = current;
                logger.trace(description);
            }
        } else if (RSS2) {
            if (qName.equalsIgnoreCase("url") &&
                    enclosing.equalsIgnoreCase("image")) {
                image = current;
                logger.trace(image);
            } else if (qName.equalsIgnoreCase("title")) {
                if (enclosing.equalsIgnoreCase("channel")) {
                    channel = current;
                    logger.trace(channel);
                } else if (enclosing.equalsIgnoreCase("item")) {
                    title = current;
                    logger.trace(title);
                }
            } else if (qName.equalsIgnoreCase("link") &&
                    enclosing.equalsIgnoreCase("item")) {
                link = current;
                logger.trace(link);
            } else if (qName.equalsIgnoreCase("description") &&
                    enclosing.equalsIgnoreCase("item")) {
                description = current;
                logger.trace(description);
            } else if (qName.equalsIgnoreCase("pubDate")) {
                pubdate = current;
                logger.trace(pubdate);
            }
        } else if (ATOM) {
            if (qName.equalsIgnoreCase("logo") &&
                    enclosing.equalsIgnoreCase("feed")) {
                image = current;
                logger.trace(image);
            } else if (qName.equalsIgnoreCase("title")) {
                if (enclosing.equalsIgnoreCase("feed")) {
                    String rel = attrs.getValue("rel");
                    if (rel != null && rel.equalsIgnoreCase("alternate")) {
                        channel = current;
                        logger.trace(channel);
                    }
                } else if (enclosing.equalsIgnoreCase("entry")) {
                    title = current;
                    logger.trace(title);
                }
            } else if (qName.equalsIgnoreCase("summary")) {
                description = current;
                logger.trace(description);
            } else if (qName.equalsIgnoreCase("updated")) {
                pubdate = current;
                logger.trace(pubdate);
            }
        }

        if ((qName.equalsIgnoreCase("item") && RSS1) ||
                (qName.equalsIgnoreCase("item") && RSS2) ||
                (qName.equalsIgnoreCase("entry") && ATOM)) {
            RSSItem rssitem = new RSSItem(channel, title, link,
                    description, image, pubdate, fixhtml);

            LocalRSSItems.add(rssitem);
            title = link = description = "";
        }
    }

    public void characters(char[] buf, int offset, int len) {
        current += new String(buf, offset, len);
    }
}
