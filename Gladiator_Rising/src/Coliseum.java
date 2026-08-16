import java.util.List;
import java.util.Scanner;

/**
 * Coliseum drives the terminal menu, the game loop, and ties every
 * other class together. It contains the program's main() method and
 * is the only class with a Scanner and System.out calls tied to menu
 * flow - keeping input/output concerns out of the model classes
 * (Gladiator, Opponent, Item, Loadout, etc.).
 *
 * All combat is run using Character/Combatant references, so
 * runBattle() works unchanged no matter which concrete Opponent
 * subclass (Swordsman, Beast, Champion) is passed in - the
 * polymorphism demonstration in action.
 */
public class Coliseum {

    private final Scanner scanner;
    private final GameDatabase database;

    /**
     * Constructs the Coliseum application, wiring up the input
     * scanner and the game database.
     *
     * @param database the GameDatabase used for save/load and the
     *                 armory catalog
     */
    public Coliseum(String[] args, GameDatabase database) {
        this.scanner = new Scanner(System.in);
        this.database = database;
    }

    /**
     * Program entry point. Seeds the armory catalog with starting
     * gear, then starts the main menu loop.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        GameDatabase database = new GameDatabase("coliseum.db");
        seedArmory(database);

        Coliseum game = new Coliseum(args, database);
        game.showMainMenu();
    }

    /**
     * Populates the armory catalog with a small starting set of
     * weapons and armor. In Week 4 this seed data will instead be
     * loaded from (or written into) the SQLite gear table.
     *
     * @param database the GameDatabase to seed
     */
    private static void seedArmory(GameDatabase database) {
        database.addItem(new Item(1, "Iron Shortsword", "WEAPON", 25, 4));
        database.addItem(new Item(2, "Steel Longsword", "WEAPON", 60, 9));
        database.addItem(new Item(3, "War Hammer", "WEAPON", 100, 14));
        database.addItem(new Item(4, "Leather Vest", "ARMOR", 20, 3));
        database.addItem(new Item(5, "Chainmail", "ARMOR", 55, 7));
        database.addItem(new Item(6, "Plate Armor", "ARMOR", 95, 12));
    }

    /**
     * Displays the top-level menu (New Game / Load Game / Quit) and
     * routes to the appropriate flow based on user input.
     */
    private void showMainMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== GLADIATOR RISING ===");
            System.out.println("1. New Game");
            System.out.println("2. Load Game");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    startNewGame();
                    break;
                case "2":
                    loadExistingGame();
                    break;
                case "3":
                    System.out.println("Farewell, gladiator.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    /**
     * Prompts for a gladiator name and begins a fresh campaign.
     */
    private void startNewGame() {
        System.out.print("Enter your gladiator's name: ");
        String name = scanner.nextLine().trim();
        Gladiator gladiator = new Gladiator(name, 60);
        System.out.println("\nWelcome to the coliseum, " + name + "!");
        runGameLoop(gladiator);
    }

    /**
     * Attempts to load a previously saved gladiator by name.
     */
    private void loadExistingGame() {
        System.out.print("Enter the name of your saved gladiator: ");
        String name = scanner.nextLine().trim();
        Gladiator gladiator = database.loadGladiator(name);

        if (gladiator == null) {
            System.out.println("No saved gladiator found with that name.");
            return;
        }

        System.out.println("Welcome back, " + name + "!");
        runGameLoop(gladiator);
    }

    /**
     * The gladiator's in-game menu loop: enter the coliseum, visit
     * the armory, view status, save, or quit.
     *
     * @param gladiator the active Gladiator
     */
    private void runGameLoop(Gladiator gladiator) {
        boolean playing = true;

        while (playing && gladiator.isAlive()) {
            System.out.println("\n--- " + gladiator + " ---");
            System.out.println("1. Enter the Coliseum");
            System.out.println("2. Visit the Armory");
            System.out.println("3. View Gladiator Status");
            System.out.println("4. Save Game");
            System.out.println("5. Quit to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    enterColiseum(gladiator);
                    break;
                case "2":
                    openArmory(gladiator);
                    break;
                case "3":
                    System.out.println(gladiator);
                    break;
                case "4":
                    database.saveGladiator(gladiator);
                    System.out.println("Game saved.");
                    break;
                case "5":
                    playing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        }

        if (!gladiator.isAlive()) {
            System.out.println("\n" + gladiator.getName() + " has fallen in the coliseum. Game over.");
        }
    }

    /**
     * Selects the next opponent based on the gladiator's current
     * round and runs a battle against them.
     *
     * @param gladiator the active Gladiator
     */
    private void enterColiseum(Gladiator gladiator) {
        Opponent opponent = nextOpponent(gladiator.getRound());
        System.out.println("\nA " + opponent.getName() + " enters the arena, ready to fight!");
        runBattle(gladiator, opponent);
    }

    /**
     * Builds the opponent for a given round number. Rounds 1-2 face a
     * Swordsman, rounds 3-4 face a Beast, and round 5 and beyond face
     * the Champion.
     *
     * @param round the gladiator's current round
     * @return a newly constructed Opponent appropriate for that round
     */
    private Opponent nextOpponent(int round) {
        if (round <= 2) {
            return new Swordsman("Swordsman");
        } else if (round <= 4) {
            return new Beast("Beast");
        } else {
            return new Champion("Champion");
        }
    }

    /**
     * Runs a battle between the gladiator and an opponent, alternating
     * attacks until one of them is no longer alive. Both fighters are
     * referenced only through the Combatant/Character abstraction, so
     * this method works identically no matter which concrete Opponent
     * subclass was passed in - the project's clearest demonstration of
     * polymorphism.
     *
     * @param gladiator the active Gladiator
     * @param opponent  the Opponent to fight
     */
    private void runBattle(Gladiator gladiator, Opponent opponent) {
        while (gladiator.isAlive() && opponent.isAlive()) {
            gladiator.attack(opponent);
            if (!opponent.isAlive()) {
                break;
            }
            opponent.attack(gladiator);
        }

        if (gladiator.isAlive()) {
            System.out.println(opponent.getName() + " has been defeated!");
            gladiator.addGold(opponent.getGoldReward());
            System.out.println(gladiator.getName() + " earns " + opponent.getGoldReward() + " gold.");
            gladiator.advanceRound();
        } else {
            System.out.println(gladiator.getName() + " has been defeated by the " + opponent.getName() + "...");
        }
    }

    /**
     * Displays purchasable items and handles buy/equip input.
     *
     * @param gladiator the active Gladiator
     */
    private void openArmory(Gladiator gladiator) {
        List<Item> items = database.getAllItems();

        System.out.println("\n=== ARMORY (Gold: " + gladiator.getGold() + ") ===");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
        System.out.println((items.size() + 1) + ". Leave the Armory");
        System.out.print("Choose an item to purchase: ");

        String input = scanner.nextLine().trim();
        int choice;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (choice == items.size() + 1) {
            return;
        }

        if (choice < 1 || choice > items.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Item selected = items.get(choice - 1);
        if (gladiator.spendGold(selected.getPrice())) {
            gladiator.equip(selected);
            System.out.println("Purchased and equipped " + selected.getName() + "!");
        } else {
            System.out.println("Not enough gold for that item.");
        }
    }
}
