/**
 * Character is the abstract base class for every fighter in the game,
 * including the player's Gladiator and every Opponent the gladiator
 * faces. It implements the Combatant interface and provides the
 * behavior that is identical across all fighters (taking damage,
 * checking if alive), while leaving attack() abstract so each
 * subclass can define its own combat style.
 *
 * This class cannot be instantiated directly - only through one of
 * its subclasses - which is why the constructor is protected and
 * attack() has no body here.
 */
public abstract class Character implements Combatant {

    // Fields are private to enforce encapsulation; access is only
    // through the public getters defined below.
    private String name;
    private int health;
    private int maxHealth;

    /**
     * Constructs a new Character with the given name and maximum
     * health. Current health always starts at maxHealth.
     *
     * Protected access ensures this constructor can only be called
     * by subclasses (via super(...)), never directly, since Character
     * itself is abstract and represents no fighter in particular.
     *
     * @param name      the character's display name
     * @param maxHealth the character's maximum (and starting) health
     */
    protected Character(String name, int maxHealth) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    /**
     * Every subclass must define its own attack behavior. This is the
     * project's central demonstration of polymorphism: the same
     * method signature, called through a Character or Combatant
     * reference, produces different behavior depending on the actual
     * runtime type of the object (Gladiator, Swordsman, Beast, or
     * Champion).
     *
     * @param target the Combatant being attacked
     */
    public abstract void attack(Combatant target);

    /**
     * Reduces health by the given amount, floored at zero so health
     * never goes negative. This implementation is shared by every
     * subclass, so it is written once here instead of being repeated
     * in Gladiator and each Opponent type.
     *
     * @param amount the amount of damage to apply
     */
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    /**
     * A Character is alive as long as its health is above zero.
     *
     * @return true if health > 0, false otherwise
     */
    public boolean isAlive() {
        return health > 0;
    }

    /**
     * Sets health directly to the given value, clamped between 0 and
     * maxHealth. Protected because only a subclass should ever need
     * to set health directly - normal gameplay damage flows through
     * takeDamage() instead. This exists specifically to support
     * Gladiator.restoreState(), which reconstructs a Gladiator's exact
     * saved health when loading from the database.
     *
     * @param health the health value to set
     */
    protected void setHealth(int health) {
        if (health < 0) {
            health = 0;
        } else if (health > maxHealth) {
            health = maxHealth;
        }
        this.health = health;
    }

    // ---- Public getters (fields stay private; this is encapsulation) ----

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Formats the character's name and current/max health for status
     * display. Subclasses may override this to include additional
     * information (gold, equipped gear, etc.).
     *
     * @return a human-readable status string
     */
    @Override
    public String toString() {
        return name + " (" + health + "/" + maxHealth + " HP)";
    }
}
