import javax.swing.ImageIcon;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.awt.MediaTracker;

public class GetImageIcon
{
    public static void main (String url[])
    {

        try
        {
            URL u = new URL (url[0]);
            URLConnection uc = u.openConnection();
            uc.connect();

            /*
            ** see if this is HTTP and see if there is a redirection.
            */
            if (uc instanceof HttpURLConnection)
            {
                HttpURLConnection huc = (HttpURLConnection) uc;
                System.out.println (huc.getResponseCode());

                if (301 == huc.getResponseCode() ||
                    302 == huc.getResponseCode())
                {
                    System.out.println (huc.getHeaderField ("Location"));
                    u = new URL (u, huc.getHeaderField ("Location"));
                    huc = (HttpURLConnection) u.openConnection();
                    huc.connect();
                    System.out.println (huc.getResponseCode());

                    if (200 != huc.getResponseCode())
                    {
                        System.out.println ("crapped out");
                        return;
                    }
                }
            }

            ImageIcon imageIcon = new ImageIcon (u);

            if (imageIcon.getImageLoadStatus() != MediaTracker.COMPLETE)
            {
                System.out.println ("Failed to load: " + imageIcon);
            }
        }
        catch (Exception e)
        {
            System.out.println (e);
        }
    }
}
