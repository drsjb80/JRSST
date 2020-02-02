BASE	= $*
TARGET	= $@
DEPENDS	= $<
NEWER	= $?

PACKAGE	= edu.mscd.cs.javaln
BASEDIR	= ../../../..
DIR	= $(BASEDIR)/edu/mscd/cs/javaln
CP	= -cp $(BASEDIR)

JAVALN	= $(DIR)/JavaLN.java $(DIR)/LineNumberFormatter.java \
    $(DIR)/NullFormatter.java \
    $(DIR)/LevelFilter.java $(DIR)/Version.java \
    $(DIR)/SetFilter.java $(DIR)/CompositeFormatter.java \
    $(DIR)/ClassFilter.java $(DIR)/MethodFilter.java

TESTS = Test.java Package.java

CLASSES = $(JAVALN:.java=.class)

SYSLOG	= $(DIR)/syslog/Makefile $(DIR)/syslog/*.java $(DIR)/syslog/*.class \
    $(DIR)/syslog/*.[ch] $(DIR)/syslog/*.props

MISC	= $(DIR)/Makefile $(DIR)/MainClass $(DIR)/VERSION

ALL     = $(JAVALN) $(CLASSES) $(SYSLOG) doc $(MISC)
DALL	= $(ALL) doc $(DJNILIBS)

VERSION = $(shell cat $(DIR)/VERSION)
JAR	= JavaLN-$(VERSION).jar

.SUFFIXES: .java .class

.java.class : 
	javac -source 1.5 -target 1.5 $(CP) -Xlint:deprecation -Xlint:unchecked $(DEPENDS)

$(JAR) : $(ALL)
	cd syslog; make
	jar cMf $(BASEDIR)/$(TARGET) $(DALL)

doc : $(JAVALN) $(DIR)/syslog/*.java
	javadoc -quiet -d doc $(JAVALN) $(DIR)/syslog/*.java
	touch doc

clean : 
	cd $(DIR)/syslog; make clean
	rm -f $(CLASSES) $(JAR)

test : testBasic testPackage testClassFilter testJavaLN \
	testVersion testLevelFilter testLineNumberFormatter testNullFormatter

testBasic : Test.class $(JAR)
	java Test 2>&1 | tests/rmdate | tee /tmp/$(TARGET).good
	diff tests/$(TARGET).good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good

testPackage : Package.class $(JAR)
	java Package 2>&1 | tests/rmdate | tee /tmp/$(TARGET).good
	diff tests/$(TARGET).good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good

testClassFilter : $(DIR)/ClassFilter.class
	java $(PACKAGE).ClassFilter 2>&1 | tests/rmdate > /tmp/$(TARGET).good
	diff tests/$(TARGET).good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good

testMethodFilter : $(DIR)/MethodFilter.class
	java $(PACKAGE).MethodFilter 2>&1 | tests/rmdate > /tmp/$(TARGET).good
	diff tests/$(TARGET).good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good

testJavaLN : $(DIR)/JavaLN.class $(DIR)/LineNumberFormatter.class
	java $(PACKAGE).JavaLN 2>&1 | tests/rmdate > /tmp/$(TARGET).good
	diff tests/$(TARGET).good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good

testVersion : $(JAR)
	java $(CP) $(PACKAGE).Version

testLevelFilter : $(DIR)/LevelFilter.class Test.class
	java $(PACKAGE).LevelFilter > /tmp/$(TARGET).good 2>&1 
	diff tests/$(TARGET).good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good
	java $(CP) -Djava.util.logging.config.file=LevelFilter.props Test > \
        /tmp/$(TARGET).good 2>&1 
	diff tests/$(TARGET)1.good /tmp/$(TARGET).good

testLineNumberFormatter : Test.class LineNumberFormatter.props
	java $(PACKAGE).MethodFilter 2>&1 | tests/rmdate > /tmp/$(TARGET).good
	java -Djava.util.logging.config.file=LineNumberFormatter.props Test 2>&1 \
        | tests/rmdate > /tmp/$(TARGET).good
	diff tests/$(TARGET).good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good

testNullFormatter : $(DIR)/NullFormatter.class NullFormatter.props
	java $(PACKAGE).NullFormatter > /tmp/$(TARGET).good 2>&1
	diff tests/$(TARGET).good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good
	java -Djava.util.logging.config.file=NullFormatter.props Test \
        > /tmp/$(TARGET).good 2>&1
	diff tests/$(TARGET)1.good /tmp/$(TARGET).good
	rm /tmp/$(TARGET).good

testCompositeFormatter : $(DIR)/CompositeFormatter.class # CompositeFormatter.props
	java $(DIR)/CompositeFormatter
	# java $(CP) -Djava.util.logging.config.file=CompositeFormatter.props \
		# $(PACKAGE).Test

Version.java : VERSION
	echo "package edu.mscd.cs.javaln; public class Version { public static String getVersion() { return (\"$(VERSION)\"); } public static void main (String args[]) { System.out.println (getVersion()); }}" > $(TARGET)
