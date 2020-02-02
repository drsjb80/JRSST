BASE	= $*
TARGET	= $@
DEPENDS	= $<
NEWER	= $?

VERSION	= $(shell cat ../edu/mscd/cs/jrsstlabel/VERSION)

HTML	= JRSSTLabelApplet.html JRSSTLabelAppletArgs.html \
	  JRSSTLabelAppletLocal.html applet.html
POLICIES= JRSSTLabelAppletAll.policy JRSSTLabelAppletBBC.policy

INSTALL	= $(HTML) $(POLICIES)

ALL : $(INSTALL)

clean :
	rm -f $(HTML)

applet.html : applet ../edu/mscd/cs/jrsstlabel/VERSION
	sed -e "s/VERSION/$(VERSION)/" < $(DEPENDS) > $(TARGET)

$(SIGNED) : $(JAR)
	cp -f $(JAR) $(SIGNED)
	jarsigner -storepass jrsstjrsst -keystore myKeys $(TARGET) JRSST

JRSSTLabelApplet.html : JRSSTLabelApplet ../edu/mscd/cs/jrsstlabel/VERSION
	sed -e "s/VERSION/$(VERSION)/" \
	    -e 's,CODEBASE,http://emess.mscd.edu/~beaty/,' \
	    -e '/PARAMS/d' < $(DEPENDS) > $(TARGET)

JRSSTLabelAppletLocal.html : JRSSTLabelApplet ../edu/mscd/cs/jrsstlabel/VERSION
	sed -e "s/VERSION/$(VERSION)/" \
	    -e 's,CODEBASE,/Users/beaty-admin/src/JRSST/,' \
	    -e '/PARAMS/d' < $(DEPENDS) > $(TARGET)


JRSSTLabelAppletArgs.html : JRSSTLabelApplet ../edu/mscd/cs/jrsstlabel/VERSION
	sed -e "s/VERSION/$(VERSION)/" \
	    -e 's,CODEBASE,http://emess.mscd.edu/~beaty/,' \
	    -e 's,PARAMS,   <param name="args" value="http://www.nws.noaa.gov/data/current_obs/KDEN.rss http://rss.cnn.com/rss/cnn_topstories.rss">,' \
		< $(DEPENDS) > $(TARGET)

install : $(INSTALL)
	cp $(INSTALL) ~/public_html
	chmod 644 ~/public_html/JRSSTLabelApplet*
