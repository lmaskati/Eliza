public class ElizaGUI {

    /**
     * creates a gui object and then calls the methods to create all gui elements and then show the main menu
     * @param args command line arguments
     */
    public static void main(String[] args) {
        GUI gui = new GUI();
        gui.createGUI();
        gui.showMenu();
    }
}
