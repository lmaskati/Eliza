To run the program directly from our runnable jar files use one of the following commands:

java -jar ElizaTerminal.jar (this runs Eliza via the terminal)
java -jar ElizaGUI.jar (this runs Eliza via a GUI)


Alternatively compile and run the source code like this:

WINDOWS

	To compile:
	javac -cp .\json-simple-1.1.jar *.java

	To run Eliza via a GUI:
	java -classpath .;json-simple-1.1.jar ElizaGUI

	To run Eliza via terminal:
	java -classpath .;json-simple-1.1.jar ElizaTerminal


LINUX / MAC

	To set the classpath:
	export CLASSPATH=${CLASSPATH}:./json-simple-1.1.jar

	To compile:
	javac *.java

	To run Eliza via a GUI:
	java ElizaGUI

	To run Eliza via terminal:
	java ElizaTerminal



