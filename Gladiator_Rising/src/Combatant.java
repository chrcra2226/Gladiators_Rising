/**
 * Combatant defines the behavior that every fighting entity in the game
 * must provide. It does not dictate HOW that behavior is implemented -
 * only WHAT every fighter must be able to do. This keeps the combat
 * loop in Coliseum decoupled from the concrete type of fighter it is
 * dealing with (Gladiator, Swordsman, Beast, Champion, etc.).
 *
 * All methods in an interface are implicitly public and abstract, so
 * those modifiers are omitted here per Java convention.
 */
public interface Combatant {

    /**
     * Performs an attack against the given target. Concrete
     * implementations decide how much damage is dealt and how it is
     * calculated (base damage, equipment bonuses, special abilities).
     *
     * @param target the Combatant being attacked
     */
    void attack(Combatant target);

    /**
     * Reduces this Combatant's health by the given amount. Used by an
     * attacker to apply damage to whichever Combatant they targeted.
     *
     * @param amount the amount of damage to apply
     */
    void takeDamage(int amount);

    /**
     * Reports whether this Combatant can still fight.
     *
     * @return true if health is above zero, false otherwise
     */
    boolean isAlive();
}
