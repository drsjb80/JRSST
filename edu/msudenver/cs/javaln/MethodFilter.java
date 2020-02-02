package edu.mscd.cs.javaln;

import java.io.*;
import java.util.logging.*;

/**
 * A class that allows one to see logging only from certain methods.
 */

public class MethodFilter extends SetFilter
{
    public MethodFilter ()
    {
        super();
        add (LogManager.getLogManager().getProperty
            ("edu.mscd.cs.javaln.MethodFilter.names"));
    }

    /**
     * This constructor looks for classes to log from the logging property
     * "edu.mscd.cs.javaln.MethodFilter.names" and the String passed to it,
     * both of which it takes to be a comma separated list of class names.
     */
    public MethodFilter (String methods)
    {
        this();
        add (methods);
    };

    public boolean isLoggable (LogRecord rec)
    {
        return (isLoggable (rec.getSourceClassName() + "." +
            rec.getSourceMethodName()));
    }

    public static void main (String args[])
    {
        // this filter works with all loggers
        Logger logger = Logger.getLogger ("global");

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel (Level.ALL);
        logger.addHandler (ch);
        logger.setUseParentHandlers (false);
        logger.setLevel (Level.ALL);

        // these should display
        MethodFilter mf =
            new MethodFilter ("edu.mscd.cs.javaln.MethodFilter.main");
        System.out.println (mf);
        logger.setFilter (mf);
        logger.info ("info");
        logger.severe ("severe");

        // this shouldn't
        mf = new MethodFilter ("dont.log.this.one");
        System.out.println (mf);
        logger.setFilter (mf);
        logger.info ("info");
        logger.severe ("severe");
    }
}
