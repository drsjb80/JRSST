BASE	= $*
TARGET	= $@
DEPENDS	= $<
NEWER	= $?

OS	= $(shell java GetOS)


PACKAGE	= edu.mscd.cs.javaln.syslog
DIR	= edu/mscd/cs/javaln/syslog
ROOT	= ../../../../..
CP	= -classpath $(ROOT)

ifeq ($(OS),MacOSX-x86_64)
    LIBEXT=jnilib
    CCINC=-I/System/Library/Frameworks/JavaVM.framework/Headers
    CCARGS=-bundle -I$(ROOT) $(CCINC)
endif

ifeq ($(OS),MacOSX-i386)
    LIBEXT=jnilib
    CCINC=-I/System/Library/Frameworks/JavaVM.framework/Headers
    CCARGS=-bundle -I$(ROOT) $(CCINC)
endif

ifeq ($(OS),MacOSX-ppc)
    LIBEXT=jnilib
    CCINC=-I/System/Library/Frameworks/JavaVM.framework/Headers
    CCARGS=-bundle -I$(ROOT) $(CCINC)
endif

ifeq ($(OS),Linux-i386)
    LIBEXT=so
    CCARGS=-I$(ROOT) -I/usr/lib/jvm/default-java/include -I/usr/lib/jvm/default-java/include/linux -I/usr/java/default/include/linux -I/usr/java/default/include -shared
endif

JNILIB	= lib$(OS)DomainSocket.$(LIBEXT)
JNILIBS	= $(JNILIB)

# JNILIBS	= libMacOSX-i386DomainSocket.jnilib
#	  libMacOSX-ppcDomainSocket.jnilib

DJNILIBS = $(patsubst %,$(DIR)/%,$(JNILIBS))

SYSLOG	= SyslogFormatter.java UNIXDomainHandler.java \
	  SyslogdFormatter.java SyslogdHandler.java \
	  SyslogHandler.java
CLI 	= CLIFormatter.java CLIHandler.java

JSRC	= $(SYSLOG) $(CLI) $(DGRAM)
DJSRC	= $(patsubst %,$(DIR)/%,$(JSRC))

CSRC	= UNIXDomainSocket.c UNIXDomainHandler.h 
DCSRC	= $(patsubst %,$(DIR)/%,$(CSRC))

CLASSES = $(JSRC:.java=.class)
DCLASSES = $(patsubst %,$(DIR)/%,$(CLASSES))

MISC	= GetOS.java GetOS.class
DMISC	= $(patsubst %,$(DIR)/%,$(MISC))

ALL	= $(JSRC) $(CLASSES) $(CSRC) $(MISC) $(JNILIBS)
DALL	= $(DJSRC) $(DCLASSES) $(DCSRC) $(DIR)/package.html $(DIR)/doc $(DMISC) $(DJNILIBS)

VERSION = 1.1
JAR	= JavaLN-$(VERSION).jar

.SUFFIXES: .java .class

.java.class : 
	javac -source 1.5 -target 1.5 $(CP) -Xlint:deprecation -Xlint:unchecked $(DEPENDS)

all : $(ALL)

doc : $(JSRC)
	javadoc -quiet -d doc $(JSRC)
	touch doc

clean : 
	rm -f $(CLASSES) UNIXDomainHandler.h

# ----------------------------------------------------------------------

UNIXDomainHandler.h : UNIXDomainHandler.class
	javah $(CP) $(PACKAGE).UNIXDomainHandler
	mv edu_mscd_cs_javaln_syslog_UNIXDomainHandler.h $(TARGET)

$(JNILIB) : UNIXDomainHandler.h UNIXDomainSocket.c
	$(CC) $(CCARGS) UNIXDomainSocket.c -o $(JNILIB)

UNIXDomainHandler.class : SyslogHandler.class

testUNIXDomainHandler : UNIXDomainHandler.class $(JNILIB)
	java $(CP) -Djava.library.path=. $(PACKAGE).UNIXDomainHandler
	java $(CP) -Djava.util.logging.config.file=UNIXDomainHandler.props \
		-Djava.library.path=. edu.mscd.cs.javaln.Test

# ----------------------------------------------------------------------

CLIFormatter.class : SyslogFormatter.class

testCLIFormatter : CLIFormatter.class
	java $(CP) $(PACKAGE).CLIFormatter

CLIHandler.class : SyslogHandler.class

testCLIHandler : CLIHandler.class CLIFormatter.class CLIHandler.props
	java $(CP) $(PACKAGE).CLIHandler
	java $(CP) -Djava.util.logging.config.file=CLIHandler.props \
		edu.mscd.cs.javaln.Test

# ----------------------------------------------------------------------

SyslogdFormatter.class : SyslogFormatter.class

testSyslogdFormatter : SyslogdFormatter.class
	java $(CP) $(PACKAGE).SyslogdFormatter

SyslogdHandler.class : SyslogHandler.class

testSyslogdHandler : SyslogdHandler.class SyslogdFormatter.class \
	SyslogdHandler.props
	java $(CP) $(PACKAGE).SyslogdHandler
	java $(CP) -Djava.util.logging.config.file=SyslogdHandler.props \
		edu.mscd.cs.javaln.Test

