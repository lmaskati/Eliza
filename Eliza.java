import java.io.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class Eliza {
    private boolean is_running;
    private String script;

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

        //scanner to read user input
        Scanner scanner = new Scanner(System.in);

        //this of code gets the user to choose a script, if the user doesn't input the correct thing then it displays
        //an error message and loops until they do
        System.out.println("What version of Eliza would you like to speak to today, enter 1 for therapist or " +
                "2 for tech support");
        boolean validAnswer = false;
        while (!validAnswer) {
            switch (scanner.nextLine()) {
                case "1":
                    this.script = "therapist";
                    validAnswer = true;
                    break;
                case "2":
                    this.script = "tech support";
                    validAnswer = true;
                    break;
                default:
                    System.out.println("Invalid answer, Please only enter \"1\" or \"2\" ");
            }

        }

        //chosen script message
        System.out.println("You have chosen the " + this.script + " version of Eliza \n");

        // Welcome message
        System.out.println("Hi, I'm Eliza!");

        while (this.is_running) {
            // takes in the user's input and sets it to lower case, removes all non letter
            // characters and replaces double space with single space
            String input = scanner.nextLine().toLowerCase().replaceAll("[^a-zA-Z ]", "")
                    .replaceAll(" {2}", " ");

            // user's input as an array of words
            String[] tokens = preprocess(input).split(" ");

            // for every word in the array replace any further punctuation or non letter
            // characters not caught by
            // the preprocess method
            for (String token : tokens) {
                token = token.replaceAll("[^a-zA-Z ]", "");
            }

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


    public String preprocess(String str) {
        Map<String, String> preprocessMap = new HashMap<>();
        preprocessMap.put("I'm", "I am");
        preprocessMap.put("you're", "you are");
        preprocessMap.put("we've", "we have");
        preprocessMap.put("she's", "she is");
        preprocessMap.put("he's", "he is");
        preprocessMap.put("they're", "they are");

        // iterate through map
        for (Map.Entry<String, String> mapElement : preprocessMap.entrySet()) {
            String key = mapElement.getKey();

            str = str.replaceAll(key, preprocessMap.get(key));
        }
        return str;
    }

    // reads in a JSON script file storing the decompositon and recomposition rules
    // into an object and then return the object
    public Object readInJson() {
        try {
            FileReader reader = new FileReader(this.script + ".json");
            return new JSONParser().parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return new Object();
    }

    public String noKeywords(String[] input) {
        // input: array
        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.put("I", "you");
        replacementMap.put("me", "you");
        replacementMap.put("your", "mine");
        replacementMap.put("we", "you");
        replacementMap.put("us", "you");
        replacementMap.put("am", "are");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length; i++) {
            // accessing each element of array
            if (replacementMap.containsKey(input[i])) {
                result.append(replacementMap.get(input[i]));
            } else {
                result.append(input[i]);
            }
            if (i < input.length - 1) {
                result.append(" ");
            }
        }
        result.append("?");
        String[] options = {"Could you explain that further?", "Would you like to go into more detail?",
                result.toString()};

        Random random = new Random();
        int rand = random.nextInt(3);

        return options[rand];
    }

    // returns highest priority keyword object included in the users input or
    // returns a null object if the input contains no keywords
    public JSONObject findKeyword(String[] tokens) {

        // creating an array of keyword objects
        JSONObject allJson = (JSONObject) readInJson();
        JSONArray keywords = (JSONArray) allJson.get("keywords");

        // add every word to a set of words
        Set<String> wordSet = new HashSet<>(Arrays.asList(tokens));

        // adds a keyword object plus its priority to the map if it is included in the
        // set of words
        Map<JSONObject, Integer> wordPriority = new HashMap<>();

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
    public JSONObject findDecomposition(JSONObject keyword, String input) {

        // get the array of all decomposition rules for a keyword
        JSONArray decompositionsArray = (JSONArray) keyword.get("Decompositions");

        // find the longest decomposition rule that exists within the input string
        String longestDecomp = "";
        JSONObject longestDecompObj = null;

        for (Object o : decompositionsArray) {
            JSONObject decompositionObj = (JSONObject) o;
            String decomposition = (String) decompositionObj.get("Rule");

            int length = decomposition.length();
            if (decomposition.contains("*") && input.length() >= decomposition.length())
                length++;

            if (input.contains(decomposition.replaceAll("\\*", ""))
                    && length > longestDecomp.length()) {

                longestDecompObj = (JSONObject) o;
                longestDecomp = decomposition;
            }
        }

        System.out.println("\n FOR DEBUGGING - decomposition rule chosen is: " + longestDecomp + "\n");
        // return the object of the best matching decomposition rule
        return longestDecompObj;

    }

    // given a decompositon rule, choose a recomposition rule at random and then
    // return it to be printed
    public String chooseRecomposition(JSONObject matchingDecomp, String[] words) {
        // getting an array of recompositions
        JSONArray recompositions = (JSONArray) matchingDecomp.get("Recompositions");

        // creating a random number between 0 and the size of the array of
        // recompositions
        Random rand = new Random();
        int randomIndex = rand.nextInt(recompositions.size());

        // get the randomly chosen recomposition string and return it to be printed
        String recomposition = (String) recompositions.get(randomIndex);

        //if the recomposition that is chosen contains a wildcard
        if (recomposition.contains("*")) {
            //then call the substituteWildcard method
            recomposition = substituteWildcard(recomposition, words, matchingDecomp);
        }

        return recomposition;

    }

    //method substitutes the wildcard (*) at either the start or the end of the decomposition rule with the
    //relevant text that the user input
    public String substituteWildcard(String recomposition, String[] words, JSONObject matchingDecomp) {

        //get the decomposition rule and split it into an array of words
        String decompRule = (String) matchingDecomp.get("Rule");
        String[] decompArray = decompRule.split(" ");

        //replacement string will hold the words that are being put in place of the wildcard in the recomposition rule
        StringBuilder replacement = new StringBuilder();
        boolean replace = false;

        //if the wildcard is at the end of the decomposition rule
        if (decompArray[decompArray.length - 1].equals("*")) {

            //loops from the start of the array to the end
            for (int c = 0; c < words.length; c++) {

                //if replace is true
                if (replace) {
                    //then add the current word to the replacement string
                    replacement.append(words[c]);
                    //if the loop isn't on the last word in the array then add a space too
                    if (c < words.length - 1)
                        replacement.append(" ");
                }
                //find where the word next to the wildcard appears in the word array and if replace is false, then
                //set it to true so subsequent words can be added to the replacement string.
                if (words[c].equals(decompArray[decompArray.length - 2]) && !replace)
                    replace = true;

            }

            //if the wildcard is at the start of the decomposition rule
        } else if (decompArray[0].equals("*")) {

            //loops from the end of the array to the start
            for (int c = words.length - 1; c >= 0; c--) {

                //if replace is true
                if (replace) {
                    //then add the current word to the replacement string
                    replacement.append(words[c]);
                    //if the loop isn't on the first word in the array then add a space too
                    if (c > 0)
                        replacement.append(" ");
                }
                //find where the word next to the wildcard appears in the word array and if replace is false, then
                //set it to true so subsequent words can be added to the replacement string.
                if (words[c].equals(decompArray[1]) && !replace) {
                    replace = true;
                }

            }

        }

        //return the recomposition rule and replace the wildcard with the replacement string
        return recomposition.replaceAll("\\*", replacement.toString());

    }

}
