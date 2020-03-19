import java.io.*;
import java.math.MathContext;
import java.util.*;
import org.json.simple.*;
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
            // takes in the user's input and sets it to lower case, removes all non letter
            // characters and replaces double space with single space
            String input = scanner.nextLine().toLowerCase().replaceAll("[^a-zA-Z ]", "").replaceAll("  ", " ");

            // user's input as an array of words
            String tokens[] = preprocess(input).split(" ");

            // for every word in the array replace any further puncatuation or non letter
            // characters not caught by
            // the preprocess method
            for (String token : tokens)
                token = token.replaceAll("[^a-zA-Z ]", "");

            // try and find a keyword in the user's input
            JSONObject keyword = findKeyword(tokens);

            // if there are no keywords in the user's input
            if (keyword == null) {
                // call the no keywords method and print the output
                System.out.println(noKeywords(tokens));
            } else { // if there is a keyword in the input
                // get the object of the best matching decomposition.
                JSONObject bestDecomp = findDecomposition(keyword, input);
                // and then pass it in to the choose recomposition method so a recomposition can
                // be chosen and printed
                System.out.println(chooseRecomposition(bestDecomp, tokens));
            }
        }
        // close the scanner
        scanner.close();
    }

    public static String preprocess(String str) {
        Map<String, String> preprocessMap = new HashMap<String, String>();
        preprocessMap.put("I'm", "I am");
        preprocessMap.put("you're", "you are");
        preprocessMap.put("we've", "we have");
        preprocessMap.put("she's", "she is");
        preprocessMap.put("he's", "he is");
        preprocessMap.put("they're", "they are");

        // iterate through map
        for (Map.Entry mapElement : preprocessMap.entrySet()) {
            String key = (String) mapElement.getKey();

            str = str.replaceAll(key, preprocessMap.get(key));
        }
        return str;
    }

    // reads in a JSON script file storing the decompositon and recomposition rules
    // into an object and then return the object
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

    // returns highest priority keyword object included in the users input or
    // returns a null object if the input contains no keywords
    public static JSONObject findKeyword(String[] tokens) {

        // creating an array of keyword objects
        JSONObject allJson = (JSONObject) readInJson();
        JSONArray keywords = (JSONArray) allJson.get("keywords");

        // add every word to a set of words
        Set<String> wordSet = new HashSet<String>();
        for (String word : tokens)
            wordSet.add(word);

        // adds a keyword object plus its priority to the map if it is included in the
        // set of words
        Map<JSONObject, Integer> wordPriority = new HashMap<JSONObject, Integer>();

        for (int k = 0; k < keywords.size(); k++) {
            JSONObject keywordObj = (JSONObject) keywords.get(k);
            String keywordStr = (String) keywordObj.get("keyword");
            if (wordSet.contains(keywordStr)) {
                wordPriority.put(keywordObj, k);
            }
        }

        // finds the highest priority keyword from the map of keywords
        int lowestPriority = -1;
        JSONObject highestPriorityKeyword = null;
        for (Map.Entry<JSONObject, Integer> entry : wordPriority.entrySet()) {
            if (lowestPriority == -1) {
                lowestPriority = entry.getValue();
                highestPriorityKeyword = entry.getKey();
            }
            if (lowestPriority > entry.getValue()) {
                lowestPriority = entry.getValue();
                highestPriorityKeyword = entry.getKey();
            }
        }

        // will return null if none of the keywords are present in the input string
        return highestPriorityKeyword;

    }

    // finds the best matching decomposition for a keyword using the input and
    // returns it (the longer the decompositon matched, the better the match is)
    public static JSONObject findDecomposition(JSONObject keyword, String input) {

        // get the array of all decomposition rules for a keyword
        JSONArray decompositionsArray = (JSONArray) keyword.get("Decompositions");

        // find the longest decomposition rule that exists within the input string
        String longestDecomp = "";
        JSONObject longestDecompObj = null;

        for (int c = 0; c < decompositionsArray.size(); c++) {
            JSONObject decompositionObj = (JSONObject) decompositionsArray.get(c);
            String decomposition = (String) decompositionObj.get("Rule");

            Integer length = decomposition.length();
            if (decomposition.contains("*") && input.length() >= decomposition.length())
                length++;

            if (input.contains(decomposition.replaceAll("\\*", "")) && length > longestDecomp.length()) {
                longestDecompObj = (JSONObject) decompositionsArray.get(c);
                longestDecomp = decomposition;
            }
        }

        System.out.println("\n FOR DEBUGGING - decomposition rule chosen is: " + longestDecomp + "\n");
        // return the object of the best matching decomposition rule
        return longestDecompObj;

    }

    // given a decompositon rule, choose a recomposition rule at random and then
    // return it to be printed
    public static String chooseRecomposition(JSONObject matchingDecomp, String[] words) {
        // getting an array of recompositions
        JSONArray recompositions = (JSONArray) matchingDecomp.get("Recompositions");

        // creating a random number between 0 and the size of the array of
        // recompositions
        Random rand = new Random();
        Integer randomIndex = rand.nextInt(recompositions.size());

        // get the randomly chosen recomposition string and return it to be printed
        String recomposition = (String) recompositions.get(randomIndex);

        if (recomposition.contains("*")) {
            recomposition = substituteWildcard(recomposition, words, matchingDecomp);
        }

        return recomposition;

    }

    public static String substituteWildcard(String recomposition, String[] words, JSONObject matchingDecomp) {

        String decompRule = (String) matchingDecomp.get("Rule");

        String recompArray[] = decompRule.split(" ");

        String replacement = "";
        Boolean replace = false;

        if (recompArray[recompArray.length - 1].equals("*")) {

            for (int c = 0; c < words.length; c++) {

                if (replace)
                    replacement = replacement + words[c];

                if (words[c].equals(recompArray[recompArray.length - 2]) && replace == false)
                    replace = true;

            }
            
        } else if (recompArray[0].equals("*")) {

            for (int c = words.length - 1; c >= 0; c--) {

                if (replace) {
                    replacement = replacement + words[c];
                }

                if (words[c].equals(recompArray[1]) && replace == false) {
                    replace = true;
                }

            }

        }

        recomposition = recomposition.replaceAll("\\*", replacement);

        return recomposition;

    }

}
