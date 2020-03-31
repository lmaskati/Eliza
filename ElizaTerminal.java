public class ElizaTerminal {

    public static void main(String[] args) {
        // instantiate engine
        Engine engine = new Engine(false);
        // run the repl
        engine.setRunning(true);
        engine.repl();
    }
}
