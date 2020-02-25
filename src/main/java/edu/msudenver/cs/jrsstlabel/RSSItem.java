package edu.msudenver.cs.jrsstlabel;

import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static org.apache.logging.log4j.LogManager.getLogger;

public class RSSItem {
    final String channel;
    final ImageIcon imageIcon;
    final String title;
    final String link;
    final String description;
    final String pubdate;
    static final Logger logger = getLogger("JRSST");

    private String fixhtml(String s, boolean really_fix) {
        logger.traceEntry(s);
        if (s == null)
            return (null);

        /*
         ** why doesn't a jlabel display characters 128 -- 159???
         **
         ** from: http://safari.oreilly.com/1565924851/appb-17175
         **
         ** Characters 128 through 159 are nonprinting control characters,
         ** much like characters 0 through 31 of the ASCII set. Unicode does
         ** not specify any meanings for these 32 characters, but their common
         ** interpretations are listed in the table. On Windows most of these
         ** positions are used for noncontrol characters not normally included
         ** in Latin-1
         **
         ** that said, there are a number of RSS feeds that use those
         ** characters assuming they will be displayed on a Microsoft box.
         ** Microsoft provides the following mapping:
         **
         ** http://www.microsoft.com/typography/unicode/1252.htm
         */

        // replace replaces all the matching characters...
        s = s.replace((char) 0x80, (char) 0x20AC);
        // no 0x81
        s = s.replace((char) 0x82, (char) 0x201A);
        s = s.replace((char) 0x83, (char) 0x0192);
        s = s.replace((char) 0x84, (char) 0x201E);
        s = s.replace((char) 0x85, (char) 0x2026);
        s = s.replace((char) 0x86, (char) 0x2020);
        s = s.replace((char) 0x87, (char) 0x2021);
        s = s.replace((char) 0x88, (char) 0x02C6);
        s = s.replace((char) 0x89, (char) 0x2030);
        s = s.replace((char) 0x8A, (char) 0x0160);
        s = s.replace((char) 0x8B, (char) 0x2039);
        s = s.replace((char) 0x8C, (char) 0x0152);
        // no 0x8D
        s = s.replace((char) 0x8E, (char) 0x017D);
        // no 0x8F
        // no 0x90
        s = s.replace((char) 0x91, (char) 0x2018);
        s = s.replace((char) 0x92, (char) 0x2019);
        s = s.replace((char) 0x93, (char) 0x201C);
        s = s.replace((char) 0x94, (char) 0x201D);
        s = s.replace((char) 0x95, (char) 0x2022);
        s = s.replace((char) 0x96, (char) 0x2013);
        s = s.replace((char) 0x97, (char) 0x2014);
        s = s.replace((char) 0x98, (char) 0x02DC);
        s = s.replace((char) 0x99, (char) 0x2122);
        s = s.replace((char) 0x9A, (char) 0x0161);
        s = s.replace((char) 0x9B, (char) 0x203A);
        s = s.replace((char) 0x9C, (char) 0x0153);
        // no 0x9D
        s = s.replace((char) 0x9E, (char) 0x017E);
        s = s.replace((char) 0x9F, (char) 0x0178);

        // strictly speaking, we shouldn't see these as they should have
        // been dealt with in the parser.  some rss feeds appear to put
        // these in CDATA sections, which they really shouldn't do.  sigh.
        if (really_fix) {
            s = s.replaceAll("&amp;", "&");
            s = s.replaceAll("&lt;", "<");
            s = s.replaceAll("&gt;", ">");
            s = s.replaceAll("&#39;", "'");
            s = s.replaceAll("&apos;", "'");
            s = s.replaceAll("&quot;", "\"");
            s = s.replaceAll("&ndash;", "\u8211");
            s = s.replaceAll("&mdash;", "\u8212");
            s = s.replaceAll("&amp;amp;", "&");
        }

        logger.traceExit(s);
        return (s);
    }

    /*
     * a very simple helper method
     */
    private String trimNotNull(String s) {
        if (s != null) {
            return (s.trim());
        } else {
            return (null);
        }
    }

    private ImageIcon getImageIcon(final String url) {
        logger.traceEntry(url);

        if (url == null) {
            logger.traceExit(null);
            return (null);
        }

        URL u;

        try {
            u = new URL(url);
            URLConnection uc = u.openConnection();
            uc.connect();

            /*
             ** see if this is HTTP and see if there is a redirection.
             */
            if (uc instanceof HttpURLConnection) {
                HttpURLConnection huc = (HttpURLConnection) uc;
                // logger.info (huc.getResponseCode());

                if (301 == huc.getResponseCode() ||
                        302 == huc.getResponseCode()) {
                    // logger.info (huc.getHeaderField ("Location"));
                    u = new URL(u, huc.getHeaderField("Location"));
                    huc = (HttpURLConnection) u.openConnection();
                    huc.connect();
                    // logger.info (huc.getResponseCode());

                    if (200 != huc.getResponseCode()) {
                        logger.trace(url + " not found");
                        return (null);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e);
            return (null);
        }

        ImageIcon imageIcon = new ImageIcon(u);
        int status = imageIcon.getImageLoadStatus();
        if (status != MediaTracker.COMPLETE) {
            logger.warn(imageIcon + ": " + status);
        }

        return (imageIcon);
    }

    public RSSItem(final String channel, final String title, final String link,
                   final String description, final String image, final String pubdate, final boolean really_fix) {
        this.channel = trimNotNull(fixhtml(channel, really_fix));
        this.imageIcon = getImageIcon(image);
        this.title = trimNotNull(fixhtml(title, really_fix));
        this.link = trimNotNull(fixhtml(link, really_fix));
        this.description = trimNotNull(fixhtml(description, really_fix));
        this.pubdate = trimNotNull(fixhtml(pubdate, really_fix));
    }

    public String toString() {
        return ("----- RSSItem -----" +
                "\n    Channel: " + channel +
                "\n    ImageIcon: " + imageIcon +
                "\n    Title: " + title +
                "\n    Link: " + link +
                "\n    Description: " + description +
                "\n    Pubdate: " + pubdate +
                "\n");
    }
}
