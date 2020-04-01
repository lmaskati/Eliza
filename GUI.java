import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUI {

    Color colourA = new Color(38, 70, 83);
    Color colourB = new Color(42, 157, 143);
    Color colourC = new Color(233, 196, 106);
    Color colourD = new Color(244, 162, 97);
    Color colourE = new Color(231, 111, 81);

    JFrame menuFrame = setupFrame(800, 800, "Eliza");
    JPanel menuGUI = setupPanel(colourA, 0, 0, 800, 800);
    JPanel chatGUI = setupPanel(colourA, 0, 0, 800, 800);
    Engine engine = new Engine(true);

    ArrayList<String> responseList = new ArrayList<>();
    ArrayList<String> inputList = new ArrayList<>();
    
    JLabel[] responseLabels = new JLabel[10];
    JLabel[] inputLabels = new JLabel[10];

    JPanel responsechatLog = setupGridPanel(Color.white, 50, 125, 350, 575, 10, 1);
    JPanel inputchatLog = setupGridPanel(Color.white, 400, 125, 350, 575, 10, 1);

    
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
        JLabel debugMode = setupJLabel(colourA, colourB, "Debug Mode", 30, 0);
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

        JPanel speakBtnPanel = setupGridPanel(colourA, 100, 600, 600, 100, 1, 1);

        JButton speakBtn = setupButton(colourA, "Speak to Eliza", colourB, 34);

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
        debugModePanel.add(new JLabel(""));

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

        JButton returnBtn = setupButton(colourA, "Return to Menu", colourB, 15);

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

      JButton sendBtn = setupButton(colourC, "Send", colourE, 12);
        sendBtn.addActionListener(e -> {
            engine.setUserInput(inputField.getText().toLowerCase());
            inputList.add(inputField.getText());
            inputList.add("");
            inputList.add("");
            engine.processAndPrintResponse();
            responseList.add(engine.getResponse());
            responseList.add("");
            updateLog();
        });


      inputPanel.add(inputField);
      sendBtnPanel.add(sendBtn);

      chatGUI.add(returnBtnPanel);
      chatGUI.add(titleChatPanel);


      for(int c = 0; c< 10; c ++) {
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

    private JButton setupButton(Color BACKGROUND, String text, Color FOREGROUND, int TXTSIZE) {
        JButton button = new JButton(text);
        button.setBackground(BACKGROUND);
        button.setForeground(FOREGROUND);
        button.setFont(new Font("Arial", Font.BOLD, TXTSIZE));
       // button.setBorderPainted(true);
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

    public void startChat(){
        responseList.clear();
        inputList.clear();
        for(int c =0; c < 10; c++){
            responseList.add("");
            inputList.add("");
        }
        showChat();
        engine.setRunning(true);
        engine.scriptMsg();
        responseList.add(engine.getResponse());
        responseList.add("");
        engine.welcomeMsg();
        responseList.add(engine.getResponse());
        responseList.add("");
        updateLog();
    }

    public void showMenu() {
        menuFrame.setVisible(false);
        menuFrame.remove(chatGUI);
        menuFrame.add(menuGUI);
        menuFrame.setVisible(true);
    }

    public void showChat() {
        menuFrame.setVisible(false);
        menuFrame.remove(menuGUI);
        menuFrame.add(chatGUI);
        menuFrame.setVisible(true);
    }

    public void updateLog(){
        for(int c = 2; c < 11; c ++){
            responseLabels[11-c].setText(responseList.get(responseList.size() - (c-1)));
            inputLabels[11-c].setText(inputList.get(inputList.size() - (c-1)));
        }
    }

}
