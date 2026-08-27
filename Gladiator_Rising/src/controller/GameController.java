package controller;

import java.util.List;
import java.util.Scanner;

import model.combatants.Beast;
import model.combatants.Champion;
import model.combatants.Gladiator;
import model.combatants.Opponent;
import model.combatants.Swordsman;
import model.database.GameDatabase;
import model.items.Item;
import view.ConsoleView;

/**
 * Name: Christopher Crayton
 * Date: August 27, 2026
 * Course: SDC330 - Advanced Object-Oriented Programming using Java
 * 
 * GameController is the Controller in this application's MVC
 * structure. It owns the input Scanner, drives the main menu and
 * in-game loop, and orchestrates calls between the Model (Gladiator,
 * Opponent, GameDatabase, etc.) and the View (ConsoleView). It never
 * calls System.out directly itself - every message the player sees
 * goes through a ConsoleView method.
 *
 * One deliberate boundary: the combat narration printed inside each
 * Combatant's attack() method (e.g., "Swordsman swings a rusty sword
 * for 6 damage!") stays in the Model layer rather than moving here.
 * That narration is part of *how* each opponent attacks, not a
 * separate reporting step - it is what makes Swordsman, Beast, and
 * Champion's overridden attack() implementations distinct from one
 * another (the project's polymorphism demonstration). Pulling it out
 * would mean changing Combatant.attack(Combatant target) from void to
 * a method that returns a result for the View to print, which departs
 * from the approved design document's interface.
 */
public class GameController {

    private final Scanner scanner;
    private final GameDatabase database;
    private final ConsoleView view;

    /**
     * Constructs the GameController, wiring up the input scanner, the
     * game database, and its own ConsoleView.
     *
     * @param database the GameDatabase used for save/load and the
     *                 armory catalog
     */
    public GameController(GameDatabase database) {
        this.scanner = new Scanner(System.in);
        this.database = database;
        this.view = new ConsoleView();
    }

    /**
     * Starts the application: seeds the armory catalog, then enters
     * the main menu loop. Called once by Main.
     */
    public void start() {
        seedArmory();
        showMainMenu();
    }

    /**
     * Populates the armory catalog with a starting set of weapons,
     * armor, and the Bandage consumable. Uses GameDatabase.addItem(),
     * which performs an INSERT OR IGNORE, so calling this on every
     * run is safe - the first run creates the rows, and every run
     * after that is a no-op for items that already exist in the gear
     * table.
     */
    private void seedArmory() {
        database.addItem(new Item(1, "Iron Shortsword", "WEAPON", 10, 4));
        database.addItem(new Item(2, "Steel Longsword", "WEAPON", 25, 9));
        database.addItem(new Item(3, "War Hammer", "WEAPON", 50, 14));
        database.addItem(new Item(4, "Leather Vest", "ARMOR", 10, 3));
        database.addItem(new Item(5, "Chainmail", "ARMOR", 25, 7));
        database.addItem(new Item(6, "Plate Armor", "ARMOR", 50, 12));
        database.addItem(new Item(7, "Bandage", "CONSUMABLE", 0, 0));
    }

    /**
     * Displays the top-level menu (New Game / Load Game / Quit) and
     * routes to the appropriate flow based on user input.
     */
    private void showMainMenu() {
        boolean running = true;

        while (running) {
            view.showMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    startNewGame();
                    break;
                case "2":
                    loadExistingGame();
                    break;
                case "3":
                    view.showFarewell();
                    running = false;
                    break;
                default:
                    view.showInvalidMainMenuChoice();
            }
        }
    }

    /**
     * Prompts for a gladiator name and begins a fresh campaign.
     */
    private void startNewGame() {
        view.promptGladiatorName();
        String name = scanner.nextLine().trim();
        Gladiator gladiator = new Gladiator(name, 60);
        view.showNewGladiatorWelcome(name);
        runGameLoop(gladiator);
    }

    /**
     * Attempts to load a previously saved gladiator by name.
     */
    private void loadExistingGame() {
        view.promptSavedGladiatorName();
        String name = scanner.nextLine().trim();
        Gladiator gladiator = database.loadGladiator(name);

        if (gladiator == null) {
            view.showNoSavedGladiatorFound();
            return;
        }

        view.showReturningGladiatorWelcome(name);
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
            view.showGameLoopMenu(gladiator);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    enterColiseum(gladiator);
                    break;
                case "2":
                    openArmory(gladiator);
                    break;
                case "3":
                    view.showGladiatorStatus(gladiator);
                    break;
                case "4":
                    database.saveGladiator(gladiator);
                    view.showGameSaved();
                    break;
                case "5":
                    playing = false;
                    break;
                default:
                    view.showInvalidGameLoopChoice();
            }
        }

        if (!gladiator.isAlive()) {
            view.showGladiatorFallen(gladiator.getName());
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
        view.showOpponentEntrance(opponent.getName());
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
            view.showOpponentDefeated(opponent.getName());
            gladiator.addGold(opponent.getGoldReward());
            view.showGoldEarned(gladiator.getName(), opponent.getGoldReward());
            gladiator.advanceRound();
        } else {
            view.showGladiatorDefeated(gladiator.getName(), opponent.getName());
        }
    }

    /**
     * Displays purchasable items and handles buy/equip/use input.
     *
     * @param gladiator the active Gladiator
     */
    private void openArmory(Gladiator gladiator) {
        List<Item> items = database.getAllItems();
        view.showArmory(items, gladiator.getGold());

        String input = scanner.nextLine().trim();
        int choice;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            view.showInvalidInput();
            return;
        }

        if (choice == items.size() + 1) {
            return;
        }

        if (choice < 1 || choice > items.size()) {
            view.showInvalidArmoryChoice();
            return;
        }

        Item selected = items.get(choice - 1);

        if (selected.getType().equalsIgnoreCase("CONSUMABLE")) {
            useConsumable(gladiator, selected);
            return;
        }

        if (gladiator.spendGold(selected.getPrice())) {
            gladiator.equip(selected);
            view.showPurchasedAndEquipped(selected.getName());
        } else {
            view.showNotEnoughGold();
        }
    }

    /**
     * Handles selecting a consumable item from the armory. Unlike
     * weapons and armor, a consumable is used immediately rather than
     * equipped, and its effect is implemented on Gladiator itself
     * (see Gladiator.useBandage()) since it only ever changes the
     * gladiator's own state.
     *
     * @param gladiator the active Gladiator
     * @param item      the selected consumable Item
     */
    private void useConsumable(Gladiator gladiator, Item item) {
        if (item.getName().equalsIgnoreCase("Bandage")) {
            if (gladiator.useBandage()) {
                view.showBandageUsed(gladiator.getName());
            } else {
                view.showBandageOnCooldown(gladiator.getBandageStatus());
            }
        }
    }
}
