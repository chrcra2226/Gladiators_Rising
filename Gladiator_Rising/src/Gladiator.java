/**
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

    /**
     * Constructs a new Gladiator with the given name and starting
     * health. New gladiators begin with no gold, at round 1, and with
     * an empty Loadout (no gear equipped).
     *
     * @param name      the gladiator's name
     * @param maxHealth the gladiator's maximum starting health
     */
    public Gladiator(String name, int maxHealth) {
        super(name, maxHealth);
        this.gold = 0;
        this.round = 1;
        this.loadout = new Loadout();
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
     * health, gold, and equipped gear. Overrides Character's
     * toString() to include the additional Gladiator-specific state.
     *
     * @return a human-readable status string
     */
    @Override
    public String toString() {
        return getName() + " | Round " + round + " | " + getHealth() + "/" + getMaxHealth() +
                " HP | " + gold + "g | " + loadout;
    }
}
