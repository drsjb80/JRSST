/**
*  @author Steve Beaty
*/

package edu.mscd.cs.jrsstlabel;

import edu.mscd.cs.javaln.JavaLN;
import edu.mscd.cs.jclo.JCLO;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.awt.Point;
import java.awt.Dimension;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.Toolkit;

// import java.awt.MediaTracker;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.util.Date;
// import java.lang.Math;
import java.lang.Runtime;
import java.io.IOException;

import java.util.logging.Level;

public class JRSSTLabel extends JLabel implements MouseListener
{
    /*
    SEVERE (highest value)
    WARNING
    INFO
    CONFIG
    FINE
    FINER
    FINEST (lowest value)
    */

    static JavaLN logger;

    private JFrame frame;
    private int current;
    private JRSSTConfig config;
    private String args[];
    private long start;
    private String retrieved;

    public JRSSTConfig getconfig() { return (config); }

    public JRSSTLabel (String args[])
    {
        // super ("Initializing...");
        super ();

        this.args = args;

        if (logger == null)
            logger = new JavaLN (this.getClass().getPackage().getName());

        logger.setThrowingLevel (Level.WARNING);

        Date d = new Date();
        start = d.getTime();
        retrieved = d.toString();

        /*
        ** and: google won't send stuff at a pure java client, so we mess
        ** with the User-Agent sent...
        */
        System.setProperty ("http.agent", "JRSST");

        config = new JRSSTConfig (args, this);

        /*
        ** i started getting a "java.net.SocketException: Invalid
        ** argument or cannot assign requested address" when i upgraded my
        ** Fedora Core 3 kernel.  the following made it better, but should
        ** i check for os, kernel, and JVM version before doing this?
        */
        // System.setProperty ("java.net.preferIPv4Stack", "true");

        addMouseListener (this);

        // setTransferHandler (this);
        // addActionListener(this);
    }

    public final void setFrame (final JFrame frame)
    {
        this.frame = frame;
    }

    public final boolean canImport (final JComponent dest,
        final DataFlavor[] flavors)
    {
        for (int i = 0; i < flavors.length; i++)
        {
            if (DataFlavor.stringFlavor.equals (flavors[i]))
                return (true);
        }

        return (false);
    }

    public final boolean importData (final JComponent c, final Transferable t)
    {
        try
        {
            String str = (String) t.getTransferData (DataFlavor.stringFlavor);
            System.out.println (str);
            // Feeds.addElement (str);
            return true;
        }
        catch (UnsupportedFlavorException ufe)
        {
            logger.warning (ufe);
        }
        catch (IOException ioe)
        {
            logger.warning (ioe);
        }

        return false;
    }

    public final void mouseEntered (final MouseEvent me) { }

    public final void mouseExited (final MouseEvent me) { }

    public final void mousePressed (final MouseEvent me) { }

    public final void mouseReleased (final MouseEvent me) { }

    public final void mouseClicked (final MouseEvent me)
    {
        RSSItem rssitem = (RSSItem) config.getRSSItems().elementAt (current);
        com.centerkey.utils.BareBonesBrowserLaunch.openURL (rssitem.getLink());
    }

    /**
    * from:
    * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
    * Convenience method that returns a scaled instance of the
    * provided {@code BufferedImage}.
    *
    * @param img the original image to be scaled
    * @param targetWidth the desired width of the scaled instance,
    *    in pixels
    * @param targetHeight the desired height of the scaled instance,
    *    in pixels
    * @param hint one of the rendering hints that corresponds to
    *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
    *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
    *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
    *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
    * @param higherQuality if true, this method will use a multi-step
    *    scaling technique that provides higher quality than the usual
    *    one-step technique (only useful in downscaling cases, where
    *    {@code targetWidth} or {@code targetHeight} is
    *    smaller than the original dimensions, and generally only when
    *    the {@code BILINEAR} hint is specified)
    * @return a scaled version of the original {@code BufferedImage}
    */
    static BufferedImage getScaledInstance (BufferedImage img,
        int targetWidth, int targetHeight)
    {
        /*
        int type = (img.getTransparency() == Transparency.OPAQUE) ?
        BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        */
        int type = (img.getColorModel().getTransparency() ==
            Transparency.OPAQUE) ?
                BufferedImage.TYPE_INT_RGB :
                BufferedImage.TYPE_INT_ARGB;

        logger.finest ("targetWidth = " + targetWidth);
        logger.finest ("targetHeight = " + targetHeight);
        logger.finest ("type = " + type);

        // create new image
        BufferedImage tmp = new BufferedImage (targetWidth, targetHeight, type);

        // get the Graphics2D associated with it
        Graphics2D g2 = tmp.createGraphics();
        g2.setRenderingHint (RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // draw on to the new image from the old one
        g2.drawImage (img, 0, 0, targetWidth, targetHeight, null);
        g2.dispose();

        return (tmp);
    }

    private void setPosition ()
    {
        Dimension f = frame.getSize();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        // System.out.println ("d.width = " + d.width);
        // System.out.println ("d.height = " + d.height);

        int x = config.jrsstargs.x();
        int y = config.jrsstargs.y();

        if (config.jrsstargs.upperright())
        {
            x = d.width - f.width;
        }
        else if (config.jrsstargs.lowerright())
        {
            x = d.width - f.width;
            y = d.height - f.height;
        }
        else if (config.jrsstargs.lowerleft())
        {
            y = d.height - f.height;
        }

        if (x != 0 || y != 0)
            frame.setLocation (x, y);
    }

    /**
    *  A method to call from lots of places to display a string in the
    *
    *  text field.
    *  @author  Steve Beaty
    *  @version 1.0
    *  @param  s -- the string to be displayed
    */
    void display (String s)
    {
        logger.entering (s);
        logger.finest (this.toString());

        // before we change things
        Point prevLoc = frame.getLocation();
        Dimension prevDim = frame.getSize();

        setText (s == null ? "NO TITLE" : s);

        if (frame != null)
        {
            frame.setSize (frame.getPreferredSize());
            frame.pack();
            setPosition();

            if (config.jrsstargs.growleft)
            {
                int prevRHS = prevLoc.x + prevDim.width;

                frame.setLocation (prevRHS - frame.getSize().width,
                    prevLoc.y);
            }
        }
    }

    private void setToolTip (final String retrieved, final RSSItem rssitem)
    {
        logger.entering (rssitem);

        String s = "<html><div align=\"center\">";
        String t = rssitem.getDescription();

        if (t != null)
        {
            s += WordWrap.wordWrap (t, 30).replaceAll ("\n", "<br>");
            s += "<br>";
        }
        else
        {
            s += "NO DESCRIPTION<br>";
        }

        String u = rssitem.getPubDate();

        if (u != null)
            s += "Published: " + u + "<br>";

        s += "Retrieved: " + retrieved;

        s += "</div></html>";

        setToolTipText (s);
    }

    /**
    *  Loop through all of the RSS items.
    *
    *  @author  Steve Beaty
    *  @version 1.0
    */
    private void loop (String retrieved)
    {
        String channel = "";
        int size = 0;

        for (current = 0; current < config.getRSSItems().size(); current++)
        {
            if (size != config.getRSSItems().size())
            {
                size = config.getRSSItems().size();
                logger.fine (size);
            }

            RSSItem rssitem =
                (RSSItem) config.getRSSItems().elementAt (current);

            String s = rssitem.getChannel();

            // is this a new channel to display?  if so, we need to change
            // the title and icon.
            if (s != null && ! s.equals (channel))
            {
                channel = s;

                if (frame != null)
                    frame.setTitle (s);

                // BufferedImage imageIcon = rssitem.getImageIcon();
                ImageIcon imageIcon = rssitem.getImageIcon();

                if (imageIcon != null)
                {
                    // int iconWidth = imageIcon.getWidth();
                    // int iconHeight = imageIcon.getHeight();

                    // System.out.println ("iconWidth = " + iconWidth);
                    // System.out.println ("iconHeight = " + iconHeight);

                    // int height = getHeight();
                    // float scale = (float) height / iconHeight;
                    // int width = Math.round (iconWidth * scale);

                    // System.out.println ("width = " + width);
                    // System.out.println ("height = " + height);
                    // System.out.println ("scale = " + scale);

                    if (getHeight() != 0)
                        setIcon (new ImageIcon (imageIcon.getImage().
                            getScaledInstance (-1, getHeight(),
                                Image.SCALE_SMOOTH)));
                    // (getScaledInstance (imageIcon, width, height)));
                }
                else
                {
                    setIcon (null);
                }
            }

            display (rssitem.getTitle());
            setToolTip (retrieved, rssitem);

            try
            {
                Thread.sleep (1000 * config.jrsstargs.pause);
            }
            catch (InterruptedException e)
            {
                logger.warning (e);
            }
        }
    }

    public static String getVersion () { return (Version.getVersion()); }

    public final void doit ()
    {
        while (true)
        {
            Date d = new Date();
            long now = d.getTime();

            if (now > (start + (1000 * 60 * 30)) || 
                config.getRSSItems().size() == 0)
            {
                start = now;
                retrieved = d.toString();
                config = new JRSSTConfig (args, this);
            }

            if (config.getRSSItems().size() == 0)
            {
                display ("No RSS items found, sleeping for five minutes...");
                setIcon (null);

                try
                {
                    logger.fine ("Going to sleep...");
                    Thread.sleep (1000 * 60 * 5);
                    logger.fine ("Waking up...");
                }
                catch (InterruptedException e)
                {
                    logger.warning (e);
                }

                continue;
            }

            loop (retrieved);

            logger.config (Runtime.getRuntime().totalMemory());
            Runtime.getRuntime().gc();
            logger.config (Runtime.getRuntime().totalMemory());

            if (config.jrsstargs._1)
            System.exit (0);
        }
    }
}
