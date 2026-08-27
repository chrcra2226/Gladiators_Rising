package view;

import java.util.List;

import model.combatants.Gladiator;
import model.items.Item;

/**
 * Name: Christopher Crayton
 * Date: August 27, 2026
 * Course: SDC330 - Advanced Object-Oriented Programming using Java
 * 
 * ConsoleView is the View in this application's MVC structure. It is
 * responsible for every piece of text the player sees - menus,
 * prompts, and status/result messages - and nothing else. It has no
 * game logic and never reads input; GameController tells it what to
 * display, and Scanner input is read back by the Controller.
 *
 * Centralizing all output here means GameController's methods read as
 * a sequence of decisions ("if the purchase succeeded, show success")
 * rather than being interleaved with literal display strings, and it
 * means every user-facing message lives in exactly one place if it
 * ever needs to change (wording, formatting, localization, etc.).
 */
public class ConsoleView {

    // ---- Main menu ----

    public void showMainMenu() {
        System.out.println("\n=== GLADIATOR RISING ===");
        System.out.println("1. New Game");
        System.out.println("2. Load Game");
        System.out.println("3. Quit");
        System.out.print("Choose an option: ");
    }

    public void showInvalidMainMenuChoice() {
        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
    }

    public void showFarewell() {
        System.out.println("Farewell, gladiator.");
    }

    // ---- New game / load game ----

    public void promptGladiatorName() {
        System.out.print("Enter your gladiator's name: ");
    }

    public void showNewGladiatorWelcome(String name) {
        System.out.println("\nWelcome to the coliseum, " + name + "!");
    }

    public void promptSavedGladiatorName() {
        System.out.print("Enter the name of your saved gladiator: ");
    }

    public void showNoSavedGladiatorFound() {
        System.out.println("No saved gladiator found with that name.");
    }

    public void showReturningGladiatorWelcome(String name) {
        System.out.println("Welcome back, " + name + "!");
    }

    // ---- In-game menu ----

    public void showGameLoopMenu(Gladiator gladiator) {
        System.out.println("\n--- " + gladiator + " ---");
        System.out.println("1. Enter the Coliseum");
        System.out.println("2. Visit the Armory");
        System.out.println("3. View Gladiator Status");
        System.out.println("4. Save Game");
        System.out.println("5. Quit to Main Menu");
        System.out.print("Choose an option: ");
    }

    public void showInvalidGameLoopChoice() {
        System.out.println("Invalid choice. Please enter a number from 1 to 5.");
    }

    public void showGladiatorStatus(Gladiator gladiator) {
        System.out.println(gladiator);
    }

    public void showGameSaved() {
        System.out.println("Game saved.");
    }

    public void showGladiatorFallen(String gladiatorName) {
        System.out.println("\n" + gladiatorName + " has fallen in the coliseum. Game over.");
    }

    // ---- Combat ----

    public void showOpponentEntrance(String opponentName) {
        System.out.println("\nA " + opponentName + " enters the arena, ready to fight!");
    }

    public void showOpponentDefeated(String opponentName) {
        System.out.println(opponentName + " has been defeated!");
    }

    public void showGoldEarned(String gladiatorName, int amount) {
        System.out.println(gladiatorName + " earns " + amount + " gold.");
    }

    public void showGladiatorDefeated(String gladiatorName, String opponentName) {
        System.out.println(gladiatorName + " has been defeated by the " + opponentName + "...");
    }

    // ---- Armory ----

    public void showArmory(List<Item> items, int gold) {
        System.out.println("\n=== ARMORY (Gold: " + gold + ") ===");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
        System.out.println((items.size() + 1) + ". Leave the Armory");
        System.out.print("Choose an item to purchase: ");
    }

    public void showInvalidInput() {
        System.out.println("Invalid input.");
    }

    public void showInvalidArmoryChoice() {
        System.out.println("Invalid choice.");
    }

    public void showPurchasedAndEquipped(String itemName) {
        System.out.println("Purchased and equipped " + itemName + "!");
    }

    public void showNotEnoughGold() {
        System.out.println("Not enough gold for that item.");
    }

    public void showBandageUsed(String gladiatorName) {
        System.out.println(gladiatorName + " uses a Bandage and is fully healed!");
    }

    public void showBandageOnCooldown(String bandageStatus) {
        System.out.println("The Bandage is still on cooldown (" + bandageStatus + ").");
    }
}
