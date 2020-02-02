// http://java.sun.com/docs/books/tutorial/essential/io/example/CheckedInputStream.java

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;

public class ASCIIInputStream extends FilterInputStream {

    public ASCIIInputStream (InputStream in) { super(in); }

    public int read() throws IOException {
        int b = in.read();

	// read also returns -1 for EOF
	if (b < -1) { b = '?'; }
        return b;
    }

    public int read(byte[] b) throws IOException
    {
        return (this.read (b, 0, b.length));
    }

    public int read(byte[] b, int off, int len) throws IOException
    {
        int count = in.read (b, off, len);
        if (count != -1)
	{
            for (int i = 0; i < count; i++)
	    {
		int val = b[i] & 0xff;
		// System.out.print ("before: " + val + " " + (char) b[i]);
	        if (val > 127)
		{
		    // vague approximations of the real characters
		    switch (val)
		    {
		    	case 130 : b[i] = '\''; break;
		    	case 132 : b[i] = '"'; break;
		    	case 136 : b[i] = '^'; break;
		    	case 137 : b[i] = '%'; break;
		    	case 139 : b[i] = '<'; break;
		    	case 145 : b[i] = '\''; break;
		    	case 146 : b[i] = '\''; break;
		    	case 147 : b[i] = '"'; break;
		    	case 148 : b[i] = '"'; break;
		    	case 150 : b[i] = '-'; break;
		    	case 151 : b[i] = '-'; break;
		    	case 152 : b[i] = '~'; break;
		    	case 155 : b[i] = '>'; break;
		    	case 161 : b[i] = '!'; break;
		    	case 166 : b[i] = '|'; break;
		    	case 180 : b[i] = '\''; break;
		    	case 191 : b[i] = '?'; break;
		    	default : b[i] = '?'; break;
		    }
		}
		// System.out.println (", after: " + (char) b[i]);
	    }
        }
        return (count);
    }
}
