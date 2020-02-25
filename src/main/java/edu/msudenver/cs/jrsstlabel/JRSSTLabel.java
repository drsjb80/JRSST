package edu.msudenver.cs.jrsstlabel;

import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Date;

import static org.apache.logging.log4j.LogManager.getLogger;
/*
ALL
TRACE
DEBUG
INFO
WARN
ERROR
FATAL
OFF
 */

public class JRSSTLabel extends JLabel implements MouseListener {
    private JFrame frame = null;
    private RSSItem current;
    JRSSTConfig config;
    private final String[] args;
    private long start;
    private String retrieved;

    static final Logger logger = getLogger("JRSST");

    public final void setFrame(final JFrame frame) {
        this.frame = frame;
    }

    public JRSSTLabel(final String[] args) {
        super();

        this.args = args;

        final Date d = new Date();
        start = d.getTime();
        retrieved = d.toString();

        /*
         ** and: google won't send stuff at a pure java client, so we mess
         ** with the User-Agent sent...
         */
        System.setProperty("http.agent", "JRSST");

        config = new JRSSTConfig(args, this);

        /*
         ** i started getting a "java.net.SocketException: Invalid
         ** argument or cannot assign requested address" when i upgraded my
         ** Fedora Core 3 kernel.  the following made it better, but should
         ** i check for os, kernel, and JVM version before doing this?
         */
        // System.setProperty ("java.net.preferIPv4Stack", "true");

        addMouseListener(this);

        // setTransferHandler (this);
        // addActionListener(this);
    }

    public final void mouseEntered(final MouseEvent me) {
    }

    public final void mouseExited(final MouseEvent me) {
    }

    public final void mousePressed(final MouseEvent me) {
    }

    public final void mouseReleased(final MouseEvent me) {
    }

    public final void mouseClicked(final MouseEvent me) {
        try {
            Desktop.getDesktop().browse(java.net.URI.create(current.link));
        } catch (IOException e) {
            logger.throwing(e);
        }
    }

    private void setPosition() {
        final Dimension f = frame.getSize();
        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        // System.out.println ("d.width = " + d.width);
        // System.out.println ("d.height = " + d.height);

        int x = config.jrsstargs.x;
        int y = config.jrsstargs.y;

        if (config.jrsstargs.upperright) {
            x = d.width - f.width;
        } else if (config.jrsstargs.lowerright) {
            x = d.width - f.width;
            y = d.height - f.height;
        } else if (config.jrsstargs.lowerleft) {
            y = d.height - f.height;
        }

        if (x != 0 || y != 0)
            frame.setLocation(x, y);
    }

    void display(final String s) {
        logger.traceEntry(s);
        logger.trace(this.toString());

        // before we change things
        final Point prevLoc = frame.getLocation();
        final Dimension prevDim = frame.getSize();

        setText(s == null ? "NO TITLE" : s);

        if (frame != null) {
            frame.setSize(frame.getPreferredSize());
            frame.pack();
            setPosition();

            if (config.jrsstargs.growleft) {
                int prevRHS = prevLoc.x + prevDim.width;

                frame.setLocation(prevRHS - frame.getSize().width,
                        prevLoc.y);
            }
        }
    }

    private void setToolTip(final String retrieved, final RSSItem rssitem) {
        logger.traceEntry(rssitem.toString());

        String s = "<html><div align=\"center\">";
        String t = rssitem.description;

        if (t != null) {
            s += WordWrap.wordWrap(t, 30).replaceAll("\n", "<br>");
            s += "<br>";
        } else {
            s += "NO DESCRIPTION<br>";
        }

        final String u = rssitem.pubdate;

        if (u != null) {
            s += "Published: " + u + "<br>";
        }

        s += "Retrieved: " + retrieved;

        s += "</div></html>";

        setToolTipText(s);
    }

    private void loop(final String retrieved) {
        String channel = "";

        for (RSSItem rssitem: config.RSSItems) {
            current = rssitem;

            final String s = rssitem.channel;

            // is this a new channel to display?  if so, we need to change
            // the title and icon.
            if (s != null && !s.equals(channel)) {
                channel = s;

                if (frame != null) {
                    frame.setTitle(s);
                }

                // BufferedImage imageIcon = rssitem.getImageIcon();
                ImageIcon imageIcon = rssitem.imageIcon;

                if (imageIcon != null) {
                    if (getHeight() != 0) {
                        setIcon(new ImageIcon(imageIcon.getImage().
                                getScaledInstance(-1, getHeight(),
                                        Image.SCALE_SMOOTH)));
                    }
                } else {
                    setIcon(null);
                }
            }

            display(rssitem.title);
            setToolTip(retrieved, rssitem);

            try {
                Thread.sleep(1000 * config.jrsstargs.pause);
            } catch (InterruptedException e) {
                logger.warn(e);
            }
        }
    }

    public static String getVersion() {
        return (Version.getVersion());
    }

    public final void doit() {
        while (true) {
            final Date d = new Date();
            final long now = d.getTime();

            if (now > (start + (1000 * 60 * 30)) ||
                    config.RSSItems.size() == 0) {
                start = now;
                retrieved = d.toString();
                config = new JRSSTConfig(args, this);
            }

            if (config.RSSItems.size() == 0) {
                display("No RSS items found, sleeping for five minutes...");
                setIcon(null);

                try {
                    logger.debug("Going to sleep...");
                    Thread.sleep(1000 * 60 * 5);
                    logger.debug("Waking up...");
                } catch (InterruptedException e) {
                    logger.warn(e);
                }

                continue;
            }

            loop(retrieved);

            if (config.jrsstargs._1)
                System.exit(0);
        }
    }
}
