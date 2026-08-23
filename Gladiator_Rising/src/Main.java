import controller.GameController;
import model.database.GameDatabase;

/**
 * Main is the application's entry point. It wires up the GameDatabase
 * and hands off control to the GameController, then closes the
 * database once the controller returns. It contains no game logic,
 * input handling, or output of its own - those responsibilities
 * belong to the Controller, Model, and View respectively.
 */
public class Main {

    /**
     * Program entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        GameDatabase database = new GameDatabase("coliseum.db");
        GameController controller = new GameController(database);
        controller.start();
        database.close();
    }
}
