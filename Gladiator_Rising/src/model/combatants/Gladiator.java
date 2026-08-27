package model.combatants;

import model.items.Item;
import model.items.Loadout;

/**
 * Name: Christopher Crayton
 * Date: August 27, 2026
 * Course: SDC330 - Advanced Object-Oriented Programming using Java
 * 
 * Gladiator represents the player. It extends Character, inheriting
 * name/health tracking and the takeDamage()/isAlive() behavior, while
 * providing its own attack() implementation (polymorphism) and
 * additional state specific to the player: gold, the current round of
 * the coliseum campaign, and equipped gear.
 *
 * The equipped gear is stored as a Loadout object rather than as
 * separate weapon/armor fields directly on Gladiator - this is the
 * project's composition example. A Gladiator HAS-A Loadout.
 */
public class Gladiator extends Character {

    private int gold;
    private int round;
    private final Loadout loadout;   // composition: Gladiator has-a Loadout

    // The round the gladiator last used a bandage. -1 means "never
    // used," which is chosen specifically so a bandage is available
    // starting in round 1 (see useBandage()'s cooldown check below).
    private int lastBandageRound;

    // A bandage can be used once every other round (i.e., a 1-round
    // cooldown between uses).
    private static final int BANDAGE_COOLDOWN_ROUNDS = 2;

    /**
     * Constructs a new Gladiator with the given name and starting
     * health. New gladiators begin with no gold, at round 1, with an
     * empty Loadout (no gear equipped), and with a bandage ready to
     * use immediately.
     *
     * @param name      the gladiator's name
     * @param maxHealth the gladiator's maximum starting health
     */
    public Gladiator(String name, int maxHealth) {
        super(name, maxHealth);
        this.gold = 0;
        this.round = 1;
        this.loadout = new Loadout();
        this.lastBandageRound = -1;
    }

    /**
     * Attacks the given target. Damage is a base value plus whatever
     * attack bonus the gladiator's currently equipped weapon
     * provides, demonstrated through the Loadout composition.
     *
     * This overrides Character's abstract attack() method - a
     * concrete demonstration of polymorphism, since Opponent
     * subclasses override the very same method with completely
     * different logic.
     *
     * @param target the Combatant being attacked
     */
    @Override
    public void attack(Combatant target) {
        final int BASE_DAMAGE = 8;
        int damage = BASE_DAMAGE + loadout.getAttackBonus();
        System.out.println(getName() + " attacks with " +
                (loadout.getWeapon() != null ? loadout.getWeapon().getName() : "bare fists") +
                " for " + damage + " damage!");
        target.takeDamage(damage);
    }

    /**
     * Reduces incoming damage by the gladiator's equipped armor
     * bonus before applying it. Overrides Character's takeDamage() so
     * the Loadout's defense bonus is factored in - another example of
     * polymorphism (same method signature as Character, different
     * behavior here).
     *
     * @param amount the raw incoming damage amount
     */
    @Override
    public void takeDamage(int amount) {
        int reduced = amount - loadout.getDefenseBonus();
        if (reduced < 0) {
            reduced = 0;
        }
        super.takeDamage(reduced);
    }

    /**
     * Equips a piece of gear purchased from the armory. Delegates to
     * the Loadout based on the item's type.
     *
     * @param gear the Item to equip
     */
    public void equip(Item gear) {
        if (gear.getType().equalsIgnoreCase("WEAPON")) {
            loadout.equipWeapon(gear);
        } else if (gear.getType().equalsIgnoreCase("ARMOR")) {
            loadout.equipArmor(gear);
        }
    }

    /**
     * Restores previously saved round, health, gold, and bandage
     * cooldown onto this Gladiator. Used only by
     * GameDatabase.loadGladiator() when reconstructing a Gladiator
     * from a saved database row - normal gameplay only ever changes
     * this state through advanceRound(), addGold()/spendGold(),
     * takeDamage(), and useBandage().
     *
     * @param round            the saved round number
     * @param health           the saved current health
     * @param gold             the saved gold balance
     * @param lastBandageRound the saved round the bandage was last used
     */
    public void restoreState(int round, int health, int gold, int lastBandageRound) {
        this.round = round;
        this.gold = gold;
        this.lastBandageRound = lastBandageRound;
        setHealth(health);
    }

    /**
     * Attempts to use a bandage to fully restore health. Bandages are
     * free but can only be used once every other round - this method
     * enforces that cooldown itself so Coliseum only has to check the
     * boolean result, keeping the cooldown rule in one place.
     *
     * @return true if the bandage was used and health was fully
     *         restored, false if the bandage is still on cooldown
     */
    public boolean useBandage() {
        if (!isBandageReady()) {
            return false;
        }
        setHealth(getMaxHealth());
        lastBandageRound = round;
        return true;
    }

    /**
     * Reports whether a bandage is currently usable.
     *
     * @return true if enough rounds have passed since the last
     *         bandage use (or none has ever been used)
     */
    public boolean isBandageReady() {
        return (round - lastBandageRound) >= BANDAGE_COOLDOWN_ROUNDS;
    }

    /**
     * Formats the bandage's current availability for display on the
     * status screen.
     *
     * @return "Ready" if usable now, or how many more rounds until it
     *         is usable again
     */
    public String getBandageStatus() {
        if (isBandageReady()) {
            return "Ready";
        }
        int roundsRemaining = BANDAGE_COOLDOWN_ROUNDS - (round - lastBandageRound);
        return "Ready in " + roundsRemaining + (roundsRemaining == 1 ? " round" : " rounds");
    }

    public int getLastBandageRound() {
        return lastBandageRound;
    }

    /**
     * Adds gold to the gladiator's balance (e.g., after winning a
     * fight).
     *
     * @param amount the amount of gold to add
     */
    public void addGold(int amount) {
        gold += amount;
    }

    /**
     * Attempts to spend gold, such as when purchasing gear from the
     * armory. Fails safely if the gladiator cannot afford the cost.
     *
     * @param amount the amount of gold to spend
     * @return true if the purchase succeeded, false if funds were
     *         insufficient
     */
    public boolean spendGold(int amount) {
        if (amount > gold) {
            return false;
        }
        gold -= amount;
        return true;
    }

    /**
     * Advances the gladiator to the next round after a victory.
     */
    public void advanceRound() {
        round++;
    }

    public int getGold() {
        return gold;
    }

    public int getRound() {
        return round;
    }

    public Loadout getLoadout() {
        return loadout;
    }

    /**
     * Formats the gladiator's full status for display: name, round,
     * health, gold, equipped gear, and bandage availability.
     * Overrides Character's toString() to include the additional
     * Gladiator-specific state.
     *
     * @return a human-readable status string
     */
    @Override
    public String toString() {
        return getName() + " | Round " + round + " | " + getHealth() + "/" + getMaxHealth() +
                " HP | " + gold + "g | " + loadout + " | Bandage: " + getBandageStatus();
    }
}
