package edu.msudenver.cs.jrsstlabel;

import edu.msudenver.cs.jclo.JCLO;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import static org.apache.logging.log4j.LogManager.getLogger;

public class JRSSTConfig {
    static final Logger logger = getLogger("JRSST");
    JRSSTArgs jrsstargs = new JRSSTArgs();
    private final Vector<RSSItem> RSSItems;

	Vector<RSSItem> getRSSItems() {
        return (RSSItems);
    }

    JRSSTArgs getJRSSTArgs() {
        return (jrsstargs);
    }

	private void readConfig(String where, boolean OPML)
            throws FileNotFoundException {
        logger.traceEntry(where);
        logger.traceEntry(String.valueOf(OPML));

        logger.trace("Opening configuration file: \"" + where + "\"");

        if (jrsstargs.verbose)
            logger.info("Reading " + where);

        Vector<String> urls;

		urls = OPML ? new ParseOPML(where, jrsstargs.fix_html).getURLs() :
				new ParseOldConfig(where, jrsstargs.fix_html).getURLs();

		logger.trace(urls);
		for (String next: urls) {
            logger.trace(next);
            ParseRSS prss = new ParseRSS(next, RSSItems, jrsstargs.fix_html);

            if (jrsstargs.quick_init) {
                new Thread(prss).start();
            } else {
                prss.run();
            }
        }

        try {
            logger.trace("Waiting...");
            synchronized (RSSItems) {
                RSSItems.wait(60000); // wait a minute!
            }
            logger.trace("Done...");
        } catch (java.lang.InterruptedException IE) {
            logger.throwing(IE);
        }
    }

    private void dealWithFonts(JRSSTLabel label) {
        logger.traceEntry();

        Font font = label.getFont();
        int size = font.getSize();
        int style = font.getStyle();

        String fontname = jrsstargs.font;
        String fontsize = jrsstargs.font__size;
        String fontstyle = jrsstargs.font__style;

        if (fontname != null) {
            label.setFont(new Font(fontname, style, size));
        }

        if (fontsize != null) {
            float fs = Float.parseFloat(fontsize);
            label.setFont(label.getFont().deriveFont(fs));
        }

        if (fontstyle != null) {
            if (fontstyle.equals("BOLD")) style = Font.BOLD;
            if (fontstyle.equals("ITALIC")) style = Font.ITALIC;
            if (fontstyle.equals("PLAIN")) style = Font.PLAIN;

            label.setFont(label.getFont().deriveFont(style));
        }
    }

    public JRSSTConfig(String[] args, JRSSTLabel label) {
        RSSItems = new Vector<>();

        try {
            new JCLO(jrsstargs).parse(args);
        } catch (IllegalArgumentException iae) {
            System.out.println("Usage: " + new JCLO(jrsstargs).usage());
            System.exit(1);
        }

        if (jrsstargs.version) {
            System.out.println(Version.getVersion());
            System.exit(0);
        }

        boolean userInit = dotJRSST() || userInits() || OPMLInits() || additionals();

        dealWithFonts(label);

        if (!userInit) {
			String defaultURL = "https://feeds.bbci.co.uk/news/rss.xml";
			if (jrsstargs.verbose)
                logger.info("Reading " + defaultURL);
			// only one, no need for a thread
			new ParseRSS(defaultURL, RSSItems, jrsstargs.fix_html).run();
		}

        logger.trace("size = " + RSSItems.size());
    }

    private boolean additionals() {
        boolean good = false;
        for (String s : jrsstargs.additional) {
            good = true;
            ParseRSS prss = new ParseRSS(s, RSSItems, jrsstargs.fix_html);
            if (jrsstargs.quick_init) {
                new Thread(prss).start();
            } else {
                prss.run();
            }
        }
        return good;
    }

    private boolean OPMLInits() {
        boolean good = false;
        for (String s : jrsstargs.opml) {
            try {
                readConfig(s, true);
                good = true;
            } catch (IOException ioe) {
                logger.warn(ioe);
            }
        }
            return good;
    }

    private boolean userInits() {
        boolean good = false;
        for (String s : jrsstargs.init) {
            try {
                readConfig(s, false);
                good = true;
            } catch (IOException ioe) {
                logger.warn(ioe);
            }
        }
        return good;
    }

    private boolean dotJRSST() {
        String homedir = System.getProperty("user.home");
        if (homedir != null) {
            String sep = System.getProperty("file.separator");
            String file = "file:" + homedir + sep + ".jrsst";

            try {
                if (jrsstargs.verbose) {
                    logger.info("Reading " + file);
                }
                readConfig(file, false);
                return true;
			} catch (IOException ioe) {
                logger.debug(ioe);
            }
        }
        return false;
    }
}
