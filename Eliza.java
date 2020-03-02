import java.io.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


public class Eliza {
    private boolean is_running;

    public Eliza(boolean is_running) {
        this.is_running = true;
    }

    public static void main(String[] args) {
        // instantiate engine
        Eliza engine = new Eliza(true);
        // run the repl
        engine.repl();
    }

    // read eval print loop, to process and respond to user input
    public void repl() {
        // Welcome message
        System.out.println("Hi, I'm Eliza!");
        // get user input
        // scanner to read user input
        Scanner scanner = new Scanner(System.in);
        while (this.is_running) {
            String tokens[] = scanner.nextLine().split(" ");
            // if array is empty
            if(findPriority(tokens) == null)
            System.out.println(noKeywords(tokens));
            else
            generateResponse(findPriority(tokens), readInJson(), tokens);
        }
        // close the scanner
        scanner.close();
    }

    public static Object readInJson() {
        try {
            FileReader reader = new FileReader("therapist.json");
            Object allJson = new JSONParser().parse(reader);
            return allJson;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Object nullObj = new Object();
        return nullObj;
    }

    public static String noKeywords(String[] input) {
        // input: array
        Map<String, String> replacementMap = new HashMap<String, String>();
        replacementMap.put("I", "you");
        replacementMap.put("me", "you");
        replacementMap.put("your", "mine");
        replacementMap.put("we", "you");
        replacementMap.put("us", "you");
        replacementMap.put("am", "are");
        String result = "";

        for (int i = 0; i < input.length; i++) {
            // accessing each element of array
            if (replacementMap.containsKey(input[i])) {
                result += replacementMap.get(input[i]);
            } else {
                result += input[i];
            }
            if (i < input.length - 1) {
                result += " ";
            }
        }
        result += "?";
        String[] options = { "Could you explain that further?", "Would you like to go into more detail?", result };

        Random random = new Random();
        int rand = random.nextInt(3);

        return options[rand];
    }


    public static void generateResponse(String keyword, Object allJson, String[] inputArray) {

    //     JSONObject keywordObj = (JSONObject) allJson;
    //     JSONArray decompositionsArray = (JSONArray) keywordObj.get(keyword);
    //   //  String[] inputArray = input.split("[ \t\n\r]");

    //     for (int c = 0; c < decompositionsArray.size(); c++) {
    //         String decomposition = decompositionsArray.get(c);
    //         String[] decompArray = input.split("[ \t\n\r]");
    //         checkDecomposition(inputArray, decompArray, keyword);
    //     }

    }

    public static void checkDecomposition(String[] inputArray, String[] decompArray, String keyword) {

        // String preword, postword;

        // for (int c = 0; c < decompArray.length(); c++) {
        //     if (decompArray[c].equals("*") == false) {

        //     }
        // }

    }

    // takes an input string and a list of keywords where the earlier an element
    // appears in the list the higher its priority.
    // then finds the highest priority word that is contained in the input string
    // and returns it
    public static String findPriority(String[] wordArray) {

        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("sad");
        keywords.add("i");
        keywords.add("happy");
        keywords.add("very");
        keywords.add("am");

        // splits the string input up into an array of words
        //String[] wordArray = input.split("\\s+");

        // removes punctuation and sets every word to lower case and then adds them to a
        // set of words
        Set<String> wordSet = new HashSet<String>();
        for (int c = 0; c < wordArray.length; c++) {
            wordArray[c] = wordArray[c].replaceAll("[^\\w]", "");
            wordArray[c] = wordArray[c].toLowerCase();
            wordSet.add(wordArray[c]);
        }

        // checks to see if any of the words in the word set are in the keyword list and
        // adds them to the map if they are
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
