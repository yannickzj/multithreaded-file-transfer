JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = Server.java Client.java

all: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class port
