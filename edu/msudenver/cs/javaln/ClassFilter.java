package edu.mscd.cs.javaln;

import java.io.*;
import java.util.logging.*;

public class ClassFilter extends SetFilter
{
    public ClassFilter ()
    {
        super();
        add (LogManager.getLogManager().getProperty
            ("edu.mscd.cs.javaln.ClassFilter.names"));
    }

    /**
     * This constructor looks for classes to log from the logging property
     * "edu.mscd.cs.javaln.ClassFilter.names" and the String passed to it,
     * both of which it takes to be a comma separated list of class names.
     */
    public ClassFilter (String classes)
    {
        this();
        add (classes);
    };

    public boolean isLoggable (LogRecord rec)
    {
        return (isLoggable (rec.getSourceClassName()));
    }

    public static void main (String args[])
    {
        // this class works with any logger
        Logger logger = Logger.getLogger ("global");
        ConsoleHandler ch = new ConsoleHandler();

        ch.setLevel (Level.ALL);
        logger.addHandler (ch);
        logger.setUseParentHandlers (false);
        logger.setLevel (Level.ALL);

        ClassFilter mf = new ClassFilter ("edu.mscd.cs.javaln.ClassFilter");
        System.out.println (mf);
        logger.setFilter (mf);
        logger.info ("info");
        logger.severe ("severe");

        mf = new ClassFilter ("dont.log.this.one");
        System.out.println (mf);
        logger.setFilter (mf);
        logger.info ("info");
        logger.severe ("severe");
    }
}
