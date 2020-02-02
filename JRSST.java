/**
*  An RSS ticker in Java and Swing.
*  @author	Steve Beaty
*/

// http://www.opml.org/guidelinesForValidation#subscriptionLists
// http://static2.podcatch.com/blogs/gems/opml/mySubscriptions.opml

// http://feedvalidator.org/

// http://backend.userland.com/rss091
// http://web.resource.org/rss/1.0/spec
// http://blogs.law.harvard.edu/tech/rss
// http://www.rss-specifications.com/rss-specifications.htm

/*
** 2.0
** http://www.schneier.com/blog/index.xml
** http://www.nws.noaa.gov/data/current_obs/KDEN.rss
** http://rss.cnn.com/rss/cnn_topstories.rss
** http://newsrss.bbc.co.uk/rss/newsonline_uk_edition/world/rss.xml
** http://rss.xinhuanet.com/rss/native.xml
** http://rss.cbc.ca/canadiannews.xml
** http://blogs.law.harvard.edu/tech/rss
** http://news.google.com/nwshp?hl=en&tab=wn&q=&output=rss
*/

/*
** 1.0
** http://www.undergroundlondon.com/weather/world.rss/093/c00271f
** http://rss.slashdot.org/Slashdot/slashdot
*/

/*
** todo: configuration panel, transparent
*/

import edu.mscd.cs.jrsstlabel.JRSSTLabel;
import edu.mscd.cs.jrsstlabel.JRSSTArgs;
import edu.mscd.cs.jclo.JCLO;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

public class JRSST extends JFrame
{
    private static JRSSTArgs jrsstargs;

    JRSST (JRSSTLabel label)
    {
        super ("JRSST");

        setUndecorated (jrsstargs.undecorated());
        setResizable (jrsstargs.resizable());
        setIconImage (new ImageIcon (new Icon().getIcon()).getImage());

        // thanks to Zoe Gagnon
        if (jrsstargs.do_nothing_on_close())
            setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
        else
            setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout (new FlowLayout());
        getContentPane().add (label);
        setSize (getPreferredSize());
        pack();

        // setOpaque (false);
    }

    public static void main (String args[])
    {
        JRSSTLabel label = new JRSSTLabel (args);

        jrsstargs = label.getconfig().getJRSSTArgs();

        if (jrsstargs.help())
        {
            System.out.println ("Usage: " + new JCLO (jrsstargs).usage());
            System.exit (0);
        }

        JRSST frame = new JRSST (label);
        // frame.setFocusableWindowState (false);
        label.setFrame (frame);

        frame.setVisible (true);
        label.doit ();
    }
}
