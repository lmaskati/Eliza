import java.util.Scanner;

public class ElizaTerminal {

    /**
     * gets user to choose from one of 3 scripts and to enable or disable debugging mode.
     * Then goes on to start the engine repl.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // instantiate engine
        Engine engine = new Engine(false);

        //scanner to read in from terminal
        Scanner scanner = new Scanner(System.in);

        //this chunk of code gets the user to choose a script, if the user doesn't input the correct thing then it
        //displays an error message and loops until they do
        System.out.println("What version of Eliza would you like to speak to today, enter 1 for therapist, " +
                "2 for tech support or 3 for \"Shakespeare\"");
        boolean validAnswer = false;
        while (!validAnswer) {
            switch (scanner.nextLine()) {
                case "1":
                    engine.setScript("therapist");
                    validAnswer = true;
                    break;
                case "2":
                    engine.setScript("tech support");
                    validAnswer = true;
                    break;
                case "3":
                    engine.setScript("shakespeare");
                    validAnswer = true;
                    break;
                default:
                    System.out.println("Invalid answer, Please only enter \"1\", \"2\" or \"3\"");
            }
        }

        //this chunk of code asks the user if they want debugging mode on or off, if the user doesn't input the correct thing then it
        //displays an error message and loops until they do
        System.out.println("Would you like to enable debugging mode? (the decomposition rules are shown)\n" +
                "Enter \"Y\" for debugging on, or \"N\" for debugging off");
        validAnswer = false;
        while (!validAnswer) {
            switch (scanner.nextLine()) {
                case "Y":
                    engine.setDebugMode(true);
                    validAnswer = true;
                    break;
                case "N":
                    engine.setDebugMode(false);
                    validAnswer = true;
                    break;
                default:
                    System.out.println("Invalid answer, Please only enter \"Y\" or \"N\"");
            }
        }

        // run the repl
        engine.setRunning(true);
        engine.repl();

        //close the scanner after program finishes and repl loop is broken
        scanner.close();
    }
}
