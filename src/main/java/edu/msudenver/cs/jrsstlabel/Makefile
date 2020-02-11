BASE	= $*
TARGET	= $@
DEPENDS	= $<
NEWER	= $?

VERSION	= $(shell cat VERSION)

DIR	= ../../../..

SOURCES	= Version.java ParseXMLs.java \
	  JRSSTArgs.java JRSSTConfig.java JRSSTLabelApplet.java \
	  JRSSTLabel.java RSSItem.java WordWrap.java

CLASSES = $(SOURCES:.java=.class) ParseOldConfig.class ParseOPML.class \
	  ParseRSS.class

JAR	= JRSSTLabel-$(VERSION).jar

$(JAR) : $(CLASSES)
	jar cvf $(TARGET) $(CLASSES)

.SUFFIXES : .java .class

.java.class : 
	javac -cp $(DIR) $(DEPENDS)

ALL	= $(CLASSES) $(JAR)

all : $(ALL)

clean :
	rm -f $(ALL)

tag :
	cvs tag -R jrsst`cat VERSION | sed -e 's/\.//g'`

ParseOldConfig.class ParseOPML.class ParseRSS.class : ParseXMLs.class

JRSSTLabel.class JRSSTConfig.class : Version.class

Version.java : VERSION
	echo "package edu.mscd.cs.jrsstlabel; public class Version { public static String getVersion() { return (\"$(VERSION)\"); }}" > $(TARGET)
