import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUI {

    //colour scheme for the GUI
    Color colourA = new Color(38, 70, 83);
    Color colourB = new Color(42, 157, 143);
    Color colourC = new Color(233, 196, 106);
    Color colourD = new Color(244, 162, 97);
    Color colourE = new Color(231, 111, 81);

    //frame and panels
    JFrame menuFrame = setupFrame(800, 800, "Eliza");
    JPanel menuGUI = setupPanel(colourA, 0, 0, 800, 800);
    JPanel chatGUI = setupPanel(colourA, 0, 0, 800, 800);

    //creating a new engine object
    Engine engine = new Engine(true);

    //all the variables needed to hold responses / inputs and then display these
    ArrayList<String> responseList = new ArrayList<>();
    ArrayList<String> inputList = new ArrayList<>();
    JLabel[] responseLabels = new JLabel[10];
    JLabel[] inputLabels = new JLabel[10];
    JPanel responsechatLog = setupGridPanel(Color.white, 50, 125, 350, 575, 10, 1);
    JPanel inputchatLog = setupGridPanel(Color.white, 400, 125, 350, 575, 10, 1);


    /**
     * creates and sets up every GUI element, ready to be displayed / manipulated.
     */
    public void createGUI() {

        JPanel titleMenuPanel = setupGridPanel(colourA, 100, 100, 600, 100, 1, 3);
        JLabel titleMenuText = setupJLabel(colourA, colourC, "Eliza Menu", 40, 0);

        JPanel buttonPanel = setupGridPanel(colourA, 100, 250, 600, 100, 2, 3);
        ButtonGroup scripts = new ButtonGroup();
        JRadioButton therapist = setupRadioButton(colourA, "   Therapist ", colourE, 0, 30, true);

        therapist.addActionListener(e -> {
            engine.setScript("therapist");
        });

        JRadioButton techSupport = setupRadioButton(colourA, "Tech Support", colourE, 0, 30, false);
        techSupport.addActionListener(e -> {
            engine.setScript("tech support");
        });

        JRadioButton shakeSpeare = setupRadioButton(colourA, "Shakespeare", colourE, 0, 30, false);
        shakeSpeare.addActionListener(e -> {
            engine.setScript("shakespeare");
        });

        scripts.add(therapist);
        scripts.add(techSupport);
        scripts.add(shakeSpeare);

        JLabel chooseScript = setupJLabel(colourA, colourB, "Choose Script", 30, 0);

        JPanel debugModePanel = setupGridPanel(colourA, 100, 400, 600, 50, 1, 3);
        JLabel debugMode = setupJLabel(colourA, colourB, "Debugging Mode:", 25, 0);
        JButton whatisdebugBtn = setupJButton(colourA, "What is debugging mode?", colourB, 10, false);
        JPanel debugBtnPanel = setupGridPanel(colourA, 310, 445, 200, 70, 1, 2);
        ButtonGroup debugs = new ButtonGroup();
        JRadioButton debuggingOnBtn = setupRadioButton(colourA, "On", colourE, 0, 20, false);
        JRadioButton debuggingOffBtn = setupRadioButton(colourA, "Off", colourE, 0, 20, true);

        debugs.add(debuggingOnBtn);
        debugs.add(debuggingOffBtn);

        debuggingOnBtn.addActionListener(e -> {
            engine.setDebugMode(true);
        });

        debuggingOffBtn.addActionListener(e -> {
            engine.setDebugMode(false);
        });


        whatisdebugBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(menuFrame, "Debugging mode will print the decomposition rule that the program chooses in the terminal");
        });


        JPanel speakBtnPanel = setupGridPanel(colourA, 100, 600, 600, 100, 1, 1);

        JButton speakBtn = setupJButton(colourA, "Speak to Eliza", colourB, 34, true);

        speakBtn.addActionListener(e -> {
            startChat();
        });

        titleMenuPanel.add(new JLabel(""));
        titleMenuPanel.add(titleMenuText);
        titleMenuPanel.add(new JLabel(""));

        buttonPanel.add(new JLabel(""));
        buttonPanel.add(chooseScript);
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(therapist);
        buttonPanel.add(techSupport);
        buttonPanel.add(shakeSpeare);

        debugModePanel.add(new JLabel(""));
        debugModePanel.add(debugMode);
        debugModePanel.add(whatisdebugBtn);

        debugBtnPanel.add(debuggingOnBtn);
        debugBtnPanel.add(debuggingOffBtn);

        speakBtnPanel.add(speakBtn);

        menuGUI.add(titleMenuPanel);
        menuGUI.add(buttonPanel);
        menuGUI.add(debugModePanel);
        menuGUI.add(debugBtnPanel);
        menuGUI.add(speakBtnPanel);

        JPanel titleChatPanel = setupGridPanel(colourA, 100, 25, 600, 100, 1, 3);
        JLabel titleChatText = setupJLabel(colourA, colourC, "Eliza Chat", 40, 0);

        JPanel returnBtnPanel = setupGridPanel(colourA, 0, 0, 150, 75, 1, 1);

        JButton returnBtn = setupJButton(colourA, "Return to Menu", colourB, 15, true);

        returnBtn.addActionListener(e -> {
            engine.setRunning(false);
            showMenu();
        });

        returnBtnPanel.add(returnBtn);

        titleChatPanel.add(new JLabel(""));
        titleChatPanel.add(titleChatText);
        titleChatPanel.add(new JLabel(""));


        JPanel inputPanel = setupGridPanel(Color.white, 50, 715, 600, 30, 1, 1);
        JPanel sendBtnPanel = setupGridPanel(colourA, 650, 715, 100, 30, 1, 1);
        JTextField inputField = new JTextField();

        JButton sendBtn = setupJButton(colourC, "Send", colourE, 12, true);

        //when the "send" button is clicked
        sendBtn.addActionListener(e -> {
            //call the send message method and pass in the text currently inside the input box
            sendMsg(inputField.getText());
            //clears the input field
            inputField.setText("");
        });


        inputPanel.add(inputField);
        sendBtnPanel.add(sendBtn);

        chatGUI.add(returnBtnPanel);
        chatGUI.add(titleChatPanel);

        //fill the labels array with empty labels.
        for (int c = 0; c < 10; c++) {
            responseLabels[c] = setupJLabel(Color.white, Color.red, "", 15, 2);
            responsechatLog.add(responseLabels[c]);
            inputLabels[c] = setupJLabel(Color.white, Color.blue, "", 15, 4);
            inputchatLog.add(inputLabels[c]);
        }

        chatGUI.add(responsechatLog);
        chatGUI.add(inputchatLog);
        chatGUI.add(inputPanel);
        chatGUI.add(sendBtnPanel);

    }

    /**
     * called when the user clicks the "Speak to Eliza button"
     */
    public void startChat() {
        //the log is cleared
        responseList.clear();
        inputList.clear();
        //the log lists are filled with empty strings
        for (int c = 0; c < 10; c++) {
            responseList.add("");
            inputList.add("");
        }
        //call the method to show the chat page
        showChat();
        //start the engine running and call the script message
        engine.setRunning(true);
        engine.scriptMsg();
        //add this script message to the response list and a blank space
        responseList.add(engine.getResponse());
        responseList.add("");
        //same again with the welcome message
        engine.welcomeMsg();
        responseList.add(engine.getResponse());
        responseList.add("");
        //call the update log method to update the chat log
        updateLog();
    }

    /**
     * hides chat, shows menu
     */
    public void showMenu() {
        //make the chat page invisible and the menu page visible
        menuFrame.setVisible(false);
        menuFrame.remove(chatGUI);
        menuFrame.add(menuGUI);
        menuFrame.setVisible(true);
    }

    /**
     * hides menu, shows chat
     */
    public void showChat() {
        //make the menu page invisible and the chat page visible
        menuFrame.setVisible(false);
        menuFrame.remove(menuGUI);
        menuFrame.add(chatGUI);
        menuFrame.setVisible(true);
    }

    /**
     * method gets called every time there is a new message and the chat log needs updating
     */
    public void updateLog() {
        //fills the log with the 10 most recent responses and inputs
        for (int c = 2; c < 11; c++) {
            String responseText = responseList.get(responseList.size() - (c - 1));
            String inputText = inputList.get(inputList.size() - (c - 1));
            responseLabels[11 - c].setText(responseText);
            inputLabels[11 - c].setText(inputText);

            //sets the font for each message dynamically based on the length of the message. calls the calcLogtxtSize method to do this
            responseLabels[11 - c].setFont(new Font("Arial", Font.PLAIN, calcLogtxtSize(responseText.length() / 10)));
            inputLabels[11 - c].setFont(new Font("Arial", Font.PLAIN, calcLogtxtSize(inputText.length() / 10)));
        }
    }

    /**
     * method gets called after the user clicks the send button, it sends the input to the engine to be processed and then gets the response to be printed
     *
     * @param inputTxt text in the input box
     */
    public void sendMsg(String inputTxt) {
        //sets the user input field in the engine class to the input
        engine.setUserInput(inputTxt.toLowerCase());
        //adds this input to the list of inputs along with 2 blanks
        inputList.add(inputTxt);
        inputList.add("");
        inputList.add("");
        //call the method to process this input and get a response from engine
        engine.processAndPrintResponse();
        //add this response to the list along with a blank
        responseList.add(engine.getResponse());
        responseList.add("");
        //update the chat log
        updateLog();
    }


    /**
     * method takes in a length of a string and then returns 1 of 3 font sizes that would be suitable. so the text can fit in the log
     *
     * @param strLen a message character length
     * @return a font size
     */
    public int calcLogtxtSize(int strLen) {
        int txtSize;
        switch (strLen) {
            case 0:
            case 1:
            case 2:
                txtSize = 25;
                break;
            case 3:
            case 4:
                txtSize = 15;
                break;
            default:
                txtSize = 10;
        }
        return txtSize;
    }

    //region constructors for GUI elements
    private JFrame setupFrame(int WIDTH, int HEIGHT, String name) {
        JFrame frame = new JFrame();
        frame.setTitle(name);
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    private JPanel setupPanel(Color BACKGROUND, int LOCX, int LOCY, int WIDTH, int HEIGHT) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(BACKGROUND);
        panel.setLocation(LOCX, LOCY);
        panel.setSize(WIDTH, HEIGHT);
        return panel;
    }

    private JPanel setupGridPanel(Color BACKGROUND, int LOCX, int LOCY, int WIDTH, int HEIGHT, int ROWS, int COLUMNS) {
        JPanel panel = new JPanel(new GridLayout(ROWS, COLUMNS));
        panel.setBackground(BACKGROUND);
        panel.setLocation(LOCX, LOCY);
        panel.setSize(WIDTH, HEIGHT);
        return panel;
    }

    private JLabel setupJLabel(Color BACKGROUND, Color FOREGROUND, String text, int TXTSIZE, int ALIGNMENT) {
        JLabel label = new JLabel(text);
        label.setBackground(BACKGROUND);
        label.setForeground(FOREGROUND);
        label.setFont(new Font("Arial", Font.PLAIN, TXTSIZE));
        label.setVerticalTextPosition(SwingConstants.TOP);
        label.setHorizontalTextPosition(ALIGNMENT);
        return label;

    }

    private JButton setupJButton(Color BACKGROUND, String text, Color FOREGROUND, int TXTSIZE, boolean BORDER) {
        JButton button = new JButton(text);
        button.setBackground(BACKGROUND);
        button.setForeground(FOREGROUND);
        button.setFont(new Font("Arial", Font.BOLD, TXTSIZE));
        button.setBorderPainted(BORDER);
        return button;
    }

    private JRadioButton setupRadioButton(Color BACKGROUND, String text, Color FOREGROUND, int ALIGNMENT, int TXTSIZE, boolean SELECTED) {
        JRadioButton button = new JRadioButton(text, SELECTED);
        button.setBackground(BACKGROUND);
        button.setForeground(FOREGROUND);
        button.setFont(new Font("Arial", Font.BOLD, TXTSIZE));
        button.setVerticalTextPosition(SwingConstants.TOP);
        button.setHorizontalTextPosition(ALIGNMENT);
        return button;

    }
    //endregion

}
