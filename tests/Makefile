BASE	= $*
TARGET	= $@
DEPENDS	= $<
NEWER	= $?

CMD	= java -cp .. JRSST -1
DEBUG	= java -Djava.util.logging.config.file=../debug/all.finest -cp .. JRSST -1
TEST	= $(CMD) --pause=5
TESTFAST= $(CMD) --pause=1

runtests : $(JRSST) testhtml testfonts testgoogle testatom testrss1 \
	testrss2 testrdf testinit testopml testinit testchars testgrowleft \
	testcorners testnoinit testjrsst testpbis

testhtml : $(JRSST)
	$(TEST) file:oneliner

testfonts : $(JRSST)
	$(TEST) --font="Bitstream Charter" file:oneliner
	$(TEST) --font-size=20 file:oneliner
	$(TEST) --font-size=20 --font-style=PLAIN file:oneliner

testgoogle : $(JRSST)
	$(TESTFAST) 'http://news.google.com/news?&q=philippines&output=rss'

testatom : $(JRSST)
	$(TESTFAST) file:atom.xml

testrss1 : $(JRSST)
	$(TESTFAST) file:slashdot.rss1

testrss2 : $(JRSST)
	$(TESTFAST) file:cnn_topstories.rss2

testrdf : $(JRSST)
	$(TESTFAST) file:cas.rdf

testopml : $(JRSST)
	$(TESTFAST) --opml=file:mySubscriptions.opml

testinit : $(JRSST)
	$(TESTFAST) -init file:jrsstrc

testchars : $(JRSST)
	$(TEST) file:characters

testgrowleft : $(JRSST)
	$(TEST) --growleft=TRUE file:cnn_topstories.rss2

testcorners : $(JRSST)
	$(TESTFAST) --growleft --upperright
	$(TESTFAST) --growleft --lowerright
	$(TESTFAST) --upperleft file:oneliner
	$(TESTFAST) --lowerleft file:oneliner
	$(TEST) --x=500 file:oneliner
	$(TEST) --y=500 file:oneliner
	$(TEST) --x=500 --y=500 file:oneliner

testnoinit : $(JRSST)
	$(TESTFAST)

testpbis : $(JRSST)
	$(TESTFAST) file:pbis

testjrsst : $(JRSST)
	cp jrsstrc ~/.jrsst
	$(TESTFAST)
	rm ~/.jrsst

testApplet : $(DIR)/JRSSTLabelAppletLocal.html \
    $(DIR)/JRSSTLabelAppletLocal.policy
	export _JAVA_LAUNCHER_DEBUG=1; appletviewer \
	    -J-Djava.security.policy=$(DIR)/JRSSTLabelAppletLocal.policy \
		$(DIR)/JRSSTLabelAppletLocal.html
