import java.util.*;

public class Eliza {
    private boolean is_running;
    public Eliza(boolean is_running) {
        this.is_running = true;
    }

    public static void main(String[] args) {
        //instantiate engine
        Eliza engine = new Eliza(true);
        //run the repl
        engine.repl();
    }

    //read eval print loop, to process and respond to user input
    public void repl() {
        //Welcome message 
        System.out.println("Hi, I'm Eliza!");
        //get user input 
        //scanner to read user input
        Scanner scanner = new Scanner(System.in); 
        while (this.is_running) {
                
            while(scanner.hasNext()) {
                String data = scanner.next();
                System.out.println(data);
                //keyword to exit
                if (data.equals("exit")) {
                    System.out.print("Goodbye!");
                    //engine should stop running
                    this.is_running = false;
                    //exit while loop
                    break;
                }
            }  
        }
        //close the scanner
        scanner.close();
    }

    public static void keyWords() {
        String input = "I am so very SAD!";
        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("sad");
        keywords.add("i");
        keywords.add("happy");
        keywords.add("very");
        keywords.add("am");

        System.out.println(findPriority(input, keywords));

    }

    // takes an input string and a list of keywords where the earlier an element appears in the list the higher its priority.
    // then finds the highest priority word that is contained in the input string and returns it
    public static String findPriority(String input, ArrayList<String> keywords) {
        // splits the string input up into an array of words
        String[] wordArray = input.split("\\s+");

        // removes punctuation and sets every word to lower case and then adds them to a set of words
        Set<String> wordSet = new HashSet<String>();
        for (int c = 0; c < wordArray.length; c++) {
            wordArray[c] = wordArray[c].replaceAll("[^\\w]", "");
            wordArray[c] = wordArray[c].toLowerCase();
            wordSet.add(wordArray[c]);
        }

        // checks to see if any of the words in the word set are in the keyword list and adds them to the map if they are
        Map<String, Integer> wordPriority = new HashMap<String, Integer>();
        for (int k = 0; k < keywords.size(); k++) {
            for (int w = 0; w < wordSet.size(); w++) {
                if (wordSet.contains(keywords.get(k)))
                    wordPriority.put(keywords.get(k), k);
            }
        }

        // finds the highest priority word from the map of words
        int lowestPriority = -1;
        String highestPriorityWord = null;
        for (Map.Entry<String, Integer> entry : wordPriority.entrySet()) {
            if (lowestPriority == -1) {
                lowestPriority = entry.getValue();
                highestPriorityWord = entry.getKey();
            }
            if (lowestPriority > entry.getValue()) {
                lowestPriority = entry.getValue();
                highestPriorityWord = entry.getKey();
            }
        }

        // will return null if none of the keywords are present in the input string
        return highestPriorityWord;

    }

}
