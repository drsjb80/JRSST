package edu.msudenver.cs.jrsstlabel;

public class JRSSTArgs {
    String init[];
    boolean no_init;    // ignored, here for backward compatibility
    String opml[];
    String save_defaults;
    String load_defaults;
    int pause = 10;
    boolean verbose = false;
    boolean version = false;
    String loglevel;
    boolean quick_init = true;
    boolean fix_html = true;

    boolean growleft = false;

    boolean help = false;

    public boolean help() {
        return (help);
    }

    boolean undecorated = false;

    public boolean undecorated() {
        return (undecorated);
    }

    boolean do_nothing_on_close = false;

    public boolean do_nothing_on_close() {
        return (do_nothing_on_close);
    }

    boolean resizable = true;

    public boolean resizable() {
        return (resizable);
    }

    int x;

    public int x() {
        return (x);
    }

    int y;

    public int y() {
        return (y);
    }

    boolean upperleft = false;

    public boolean upperleft() {
        return (upperleft);
    }

    boolean upperright = false;

    public boolean upperright() {
        return (upperright);
    }

    boolean lowerleft = false;

    public boolean lowerleft() {
        return (lowerleft);
    }

    boolean lowerright = false;

    public boolean lowerright() {
        return (lowerright);
    }

    String font;
    String font__size;
    String font__style;

    boolean _1 = false;
    String additional[];
}
