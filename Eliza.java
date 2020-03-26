import java.io.*;
import java.util.*;
import java.util.regex.*;
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

        //this chunk of code gets the user to choose a script, if the user doesn't input the correct thing then it
        //displays an error message and loops until they do
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
        System.out.println(">> Hi, I'm Eliza! What's your problem today?");

        while (this.is_running) {
            // takes in the user's input and sets it to lower case, removes all non letter
            // characters and replaces double space with single space
            String input = scanner.nextLine().toLowerCase();
            input = preprocess(input).replaceAll("[^a-zA-Z ]", "")
                    .replaceAll(" {2}", " ");
            // user's input as an array of words
            String[] tokens = input.split(" ");

            // try and find keywords in the user's input
            JSONObject keywordObj = findKeywordObject(input);

            // if there are no keywords in the user's input
            if (keywordObj == null) {
                // call the no keywords method and print the output
                System.out.println(">> " + noKeywords(tokens));
            } else { // if there is a keyword in the input

                //find their keyword as a string
                String chosenKeyword = "";
                for (Object keyword : (JSONArray) keywordObj.get("keyword")) {
                    if (input.contains((String) keyword)) {
                        chosenKeyword = (String) keyword;
                    }
                }

                // get the object of the best matching decomposition.
                JSONObject bestDecomp = findDecomposition(keywordObj, input, chosenKeyword);
                // and then pass it in to the choose recomposition method so a recomposition can
                // be chosen and printed

                String response = chooseRecomposition(bestDecomp, tokens, chosenKeyword);
                System.out.println(">> " + response);

                if(response.equals("Goodbye!")){
                    this.is_running = false;
                }
            }

        }
        // close the scanner
        scanner.close();
    }


    public String preprocess(String str) {
        Map<String, String> preprocessMap = new HashMap<>();
        preprocessMap.put("i'm", "i am");
        preprocessMap.put("you're", "you are");
        preprocessMap.put("you've", "you have");
        preprocessMap.put("we've", "we have");
        preprocessMap.put("we're", "we are");
        preprocessMap.put("she's", "she is");
        preprocessMap.put("he's", "he is");
        preprocessMap.put("they're", "they are");
        preprocessMap.put("they've", "they have");

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

    public String secondPerson(String[] input) {
        // input: array
        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.put("i", "you");
        replacementMap.put("me", "you");
        replacementMap.put("your", "mine");
        replacementMap.put("we", "you");
        replacementMap.put("us", "you");
        replacementMap.put("am", "are");
        replacementMap.put("my", "your");
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
        return result.toString();

    }

    public String noKeywords(String[] input) {
        String result = secondPerson(input);
        result = result + ("?");
        String[] options = {"Could you explain that further?", "Would you like to go into more detail?",
                result};

        Random random = new Random();
        int rand = random.nextInt(3);

        return options[rand];
    }

    // returns highest priority keyword object included in the users input or
    // returns a null object if the input contains no keywords
    public JSONObject findKeywordObject(String input) {

        // creating an array of keyword objects
        JSONObject allJson = (JSONObject) readInJson();
        JSONArray keywords = (JSONArray) allJson.get("keywords");

        // adds a keyword object plus its priority to the map if any of it's keywords are included in the
        // user's string
        Map<JSONObject, Integer> wordPriority = new HashMap<>();

        for (int k = 0; k < keywords.size(); k++) {
            JSONObject keywordObj = (JSONObject) keywords.get(k);

            for (Object keyword :(JSONArray) keywordObj.get("keyword")) {
                if (input.contains((String) keyword) && !(wordPriority.containsKey(keywordObj))){
                    wordPriority.put(keywordObj, k);
                }
            }
        }

        // finds the highest priority keyword object from the map of keyword objects
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
    public JSONObject findDecomposition(JSONObject keywordObj, String input, String chosenKeyword) {

        // get the array of all decomposition rules for a keyword
        JSONArray decompositionsArray = (JSONArray) keywordObj.get("Decompositions");

        // find the longest decomposition rule that exists within the input string
        String longestDecomp = "";
        JSONObject longestDecompObj = null;

        for (Object o : decompositionsArray) {
            JSONObject decompositionObj = (JSONObject) o;
            String decomposition = (String) decompositionObj.get("Rule");
            //decomposition = decomposition.replaceAll(Pattern.quote("$"), "");

            int length = decomposition.length();
            if (decomposition.contains("*") && input.length() >= decomposition.length())
                length++;

            if (input.contains(decomposition.replaceAll("\\*", "")
                    .replaceAll(Pattern.quote("$"), chosenKeyword)) && length > longestDecomp.length()) {
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
    public String chooseRecomposition(JSONObject matchingDecomp, String[] words, String chosenKeyword) {
        // getting an array of recompositions
        JSONArray recompositions = (JSONArray) matchingDecomp.get("Recompositions");

        // creating a random number between 0 and the size of the array of
        // recompositions
        Random rand = new Random();
        int randomIndex = rand.nextInt(recompositions.size());

        // get the randomly chosen recomposition string and return it to be printed
        String recomposition = (String) recompositions.get(randomIndex);
        recomposition = recomposition.replaceAll(Pattern.quote("$"), chosenKeyword);


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
        //convert to second person
        String toReplace = secondPerson(replacement.toString().split(" "));
        //return the recomposition rule and replace the wildcard with the replacement string
        return recomposition.replaceAll("\\*", toReplace);
    }
}
