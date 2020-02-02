BASE	= $*
TARGET	= $@
DEPENDS	= $<
NEWER	= $?

VERSION	= $(shell cat ../edu/mscd/cs/jrsstlabel/VERSION)
V	= $(shell cat ../edu/mscd/cs/jrsstlabel/VERSION | sed -e 's/\.//g')
JAR	= ../JRSST-$(VERSION).jar

EXECS	= JRSST-$(VERSION) JRSST$(V).exe

ALL : $(EXECS)

JRSST$(V).exe : launch4j.xml $(JAR)
	../../launch4j/launch4j $$PWD/launch4j.xml

launch4j.xml : launch4j ../edu/mscd/cs/jrsstlabel/VERSION
	sed -e "s/VERSION/$(VERSION)/" \
	    -e "s/OUTFILE/JRSST$(V).exe/" \
		< $(DEPENDS) > $(TARGET)

JRSST-$(VERSION) : $(JAR) launch4l ../edu/mscd/cs/jrsstlabel/VERSION
	./launch4l $(JAR)
	chmod 755 $(TARGET)

clean :
	rm -f $(EXECS)
