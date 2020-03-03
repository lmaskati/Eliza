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
            // if there is no keyword in the input
            if (findPriority(tokens) == null) {
                System.out.println(noKeywords(tokens));
            } else { // if there is a keyword in the input
                // get the best matching decomposition
                JSONObject matchingDecomp = findDecomposition(findPriority(tokens), readInJson(), tokens);
                // and then pass it in to the choose recomposition method so a recomposition can
                // be chosen and printed
                System.out.println(chooseRecomposition(matchingDecomp));
            }
        }
        // close the scanner
        scanner.close();
    }

    // reads in the jason file storing the decompositon and recomposition rules into
    // an object and then return the object
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

    // takes an input string and a list of keywords where the earlier an element
    // appears in the list the higher its priority.
    // then finds the highest priority word that is contained in the input string
    // and returns it
    public static String findPriority(String[] tokens) {

        ArrayList<String> keywords = new ArrayList<String>();
        keywords.add("sad");
        keywords.add("i");
        keywords.add("happy");
        keywords.add("very");
        keywords.add("am");

        // splits the string input up into an array of words
        // String[] wordArray = input.split("\\s+");

        // removes punctuation and sets every word to lower case and then adds them to a
        // set of words
        String[] wordArray = new String[tokens.length];
        for (int c = 0; c < tokens.length; c++) {
            wordArray[c] = tokens[c];
        }

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

    // finds the best matching decomposition for a keyword using the input and
    // returns it
    public static JSONObject findDecomposition(String keyword, Object allJson, String[] inputArray) {

        // make an array of all decompositions for a keyword
        JSONObject keywordObj = (JSONObject) allJson;
        JSONArray decompositionsArray = (JSONArray) keywordObj.get(keyword);

        // create an arrray of scores the same size as the decomposition array
        Integer[] scoresArray = new Integer[decompositionsArray.size()];
        Integer highestScore = 0;
        Integer highestIndex = 0;

        // loop through the decomposition array and get the decomposition rule and split
        // it into an array
        for (int c = 0; c < decompositionsArray.size(); c++) {
            JSONObject decompositionObj = (JSONObject) decompositionsArray.get(c);
            String decomposition = (String) decompositionObj.get("Decomposition");
            String[] decompArray = decomposition.split("[ \t\n\r]");
            // pass the user's input array of words and the decomposition rule array into
            // the score decomposition method which returns a score which gets added to a
            // scores array
            scoresArray[c] = scoreDecomposition(inputArray, decompArray);
        }

        // loops through the array of scores to find the highest score, and then store
        // the location of the highest score in highest index
        for (int c = 0; c < scoresArray.length; c++) {
            if (scoresArray[c] > highestScore) {
                highestScore = scoresArray[c];
                highestIndex = c;
            }
        }

        // store the object of the best matching decomposition and return it
        JSONObject matchingDecomp = (JSONObject) decompositionsArray.get(highestIndex);
        return matchingDecomp;

    }

    // scores a decomposition rule against the users input by matching words between
    // the two arrays. 1 "score" for every word matched
    public static Integer scoreDecomposition(String[] tokens, String[] decompArray) {

        Integer score = 0;

        // duplicating the users input array of words
        String[] inputArray = new String[tokens.length];
        for (int c = 0; c < tokens.length; c++) {
            inputArray[c] = tokens[c];
        }

        // creating a set to store common words between the two arrays
        Set<String> commonWordSet = new HashSet<String>();

        // set every index in the input array to lower case and remove punctuation and
        // then add every word to the set
        for (int c = 0; c < inputArray.length; c++) {
            inputArray[c] = inputArray[c].replaceAll("[^\\w]", "");
            inputArray[c] = inputArray[c].toLowerCase();
            commonWordSet.add(inputArray[c]);
        }

        // loop through the array of decomposition rule words and if the set contains a
        // word in the rule, increment the score
        for (int c = 0; c < decompArray.length; c++) {
            if (commonWordSet.contains(decompArray[c])) {
                score++;
            }
        }

        // return this score
        return score;

    }

    // given a decompositon rule, choose a recomposition rule at random and then
    // return it to be printed
    public static String chooseRecomposition(JSONObject matchingDecomp) {
        JSONArray recompositions = (JSONArray) matchingDecomp.get("Recompositions");

        // creating a random number between 0 and the size of the array of
        // recompositions
        Random rand = new Random();
        Integer randomIndex = rand.nextInt(recompositions.size());

        // get the randomly chosen recomposition string and print it
        String recomposition = (String) recompositions.get(randomIndex);
        return recomposition;

    }

}
