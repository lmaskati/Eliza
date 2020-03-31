import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUI {

    Color colourA = new Color(38, 70, 83);
    Color colourB = new Color(42, 157, 143);
    Color colourC = new Color(233, 196, 106);
    Color colourD = new Color(244, 162, 97);
    Color colourE = new Color(231, 111, 81);

    JFrame menuFrame = setupFrame(1000, 1000, "Eliza");
    JPanel menuGUI = setupPanel(colourA, 0, 0, 1000, 100);
    JPanel chatGUI = setupPanel(colourA, 0, 0, 1000, 100);
    Engine engine = new Engine(true);

    ArrayList<String> responseList = new ArrayList<>();
    ArrayList<String> inputList = new ArrayList<>();
    
    JLabel[] responseLabels = new JLabel[10];

    JLabel[] inputLabels = new JLabel[10];


    JPanel responsechatLog = setupGridPanel(Color.white, 100, 125, 400, 700, 10, 1);
    JPanel inputchatLog = setupGridPanel(Color.white, 500, 125, 400, 700, 10, 1);

    
    public void createGUI() {
        
        JPanel titleMenuPanel = setupGridPanel(colourA, 100, 100, 800, 100, 1, 3);
        JLabel titleMenuText = setupJLabel(colourA, colourC, "Eliza Menu", 50, 0);

        JPanel buttonPanel = setupGridPanel(colourA, 200, 300, 600, 100, 2, 3);
        ButtonGroup scripts = new ButtonGroup();
        JRadioButton therapist = setupRadioButton(colourA, "   Therapist ", colourE, 0, 30);

        therapist.addActionListener(e -> {
            engine.setScript("therapist");
        });

        JRadioButton techSupport = setupRadioButton(colourA, "Tech Support", colourE, 0, 30);
        techSupport.addActionListener(e -> {
            engine.setScript("tech support");
        });

        JRadioButton shakeSpeare = setupRadioButton(colourA, "Shakespeare", colourE, 0, 30);
        shakeSpeare.addActionListener(e -> {
            engine.setScript("restructure example");
        });

        scripts.add(therapist);
        scripts.add(techSupport);
        scripts.add(shakeSpeare);

        JLabel chooseScript = setupJLabel(colourA, colourB, "Choose Script", 30, 0);

        JPanel debugModePanel = setupGridPanel(colourA, 100, 500, 800, 100, 1, 3);
        JLabel debugMode = setupJLabel(colourA, colourB, "  Debug Mode", 40, 0);
        JPanel debugBtnPanel = setupGridPanel(colourA, 400, 575, 300, 100, 1, 2);
        ButtonGroup debugs = new ButtonGroup();
        JRadioButton debuggingOnBtn = setupRadioButton(colourA, "On", colourE, 0, 30);
        JRadioButton debuggingOffBtn = setupRadioButton(colourA, "Off", colourE, 0, 30);

        debugs.add(debuggingOnBtn);
        debugs.add(debuggingOffBtn);

        debuggingOnBtn.addActionListener(e -> {
            engine.setDebugMode(true);
        });

        debuggingOffBtn.addActionListener(e -> {
            engine.setDebugMode(false);
        });

        JPanel speakBtnPanel = setupGridPanel(colourA, 200, 800, 600, 100, 1, 1);

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

        JPanel titleChatPanel = setupGridPanel(colourA, 100, 50, 800, 100, 1, 3);
        JLabel titleChatText = setupJLabel(colourA, colourC, "Eliza Chat", 50, 0);

        JPanel returnBtnPanel = setupGridPanel(colourA, 0, 0, 200, 100, 1, 1);

        JButton returnBtn = setupButton(colourA, "Return to Menu", colourB, 20);

        returnBtn.addActionListener(e -> {
            engine.setRunning(false);
            showMenu();
        });

        returnBtnPanel.add(returnBtn);

        titleChatPanel.add(new JLabel(""));
        titleChatPanel.add(titleChatText);
        titleChatPanel.add(new JLabel(""));


      JPanel inputPanel = setupGridPanel(Color.white, 100, 850, 700, 30, 1, 1);
      JPanel sendBtnPanel = setupGridPanel(colourA, 800, 850, 100, 30, 1, 1);
      JTextField inputField = new JTextField();

      JButton sendBtn = setupButton(colourC, "Send", colourE, 12);
        sendBtn.addActionListener(e -> {
            engine.setUserInput(inputField.getText());
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
          responseLabels[c] = setupJLabel(Color.white, Color.red, "", 20, 2);
          responsechatLog.add(responseLabels[c]);
          inputLabels[c] = setupJLabel(Color.white, Color.blue, "", 20, 4);
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

    private JTextArea setupTextArea(Color BACKGROUND, Color FOREGROUND, int LOCX, int LOCY, int WIDTH, int HEIGHT, String TEXT) {
        JTextArea txtarea = new JTextArea();
        txtarea.setLocation(LOCX, LOCY);
        txtarea.setSize(WIDTH, HEIGHT);
        txtarea.setText(TEXT);
        txtarea.setDisabledTextColor(BACKGROUND);
        txtarea.setSelectedTextColor(FOREGROUND);
        return txtarea;
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

    private JRadioButton setupRadioButton(Color BACKGROUND, String text, Color FOREGROUND, int ALIGNMENT, int TXTSIZE) {
        JRadioButton button = new JRadioButton(text);
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
