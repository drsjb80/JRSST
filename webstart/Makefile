BASE	= $*
TARGET	= $@
DEPENDS	= $<
NEWER	= $?

VERSION	= $(shell cat ../edu/mscd/cs/jrsstlabel/VERSION)
SIGNED	= ../JRSST-$(VERSION).signed.jar

ALL	= JRSSTWSLocal.jnlp JRSSTWS.jnlp

ALL : $(ALL)

JRSSTWS.jnlp : JRSSTWS ../edu/mscd/cs/jrsstlabel/VERSION
	sed -e "s/VERSION/$(VERSION)/" \
	    -e "s,CODEBASE,http://emess.mscd.edu/~beaty/," \
		< $(DEPENDS) > $(TARGET)

JRSSTWSLocal.jnlp : JRSSTWS ../edu/mscd/cs/jrsstlabel/VERSION
	sed -e "s/VERSION/$(VERSION)/" \
	    -e "s,CODEBASE,file:///home/beaty/src/JRSST/," \
		< $(DEPENDS) > $(TARGET)

runJNLP : $(JAR) JRSSTWSLocal.jnlp
	javaws JRSSTWSLocal.jnlp

clean :
	rm -f $(ALL)

install :  JRSSTWS.jnlp
	cp $(DEPENDS) ~/public_html
	chmod 644 ~/public_html/$(DEPENDS) 
