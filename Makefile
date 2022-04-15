# Assignment 2 advanced makefile
# Hamza Amir
# 1 Sep 2021
JAVAC=/usr/bin/javac
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<


CLASSES= Score.class WordDictionary.class WordRecord.class WordPanel.class controller.class WordApp.class
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/*.class
runapp: $(CLASS_FILES)
	java -cp $(BINDIR) WordApp 12 5 data/example_dict.txt 