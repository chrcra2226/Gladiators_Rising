package model.combatants;

/**
 * Swordsman is the earliest, least dangerous opponent in the
 * coliseum. It extends Opponent and provides a simple, single-strike
 * attack pattern - a straightforward baseline before the tougher
 * Beast and Champion tiers.
 */
public class Swordsman extends Opponent {

    private static final int MAX_HEALTH = 30;
    private static final int GOLD_REWARD = 15;
    private static final int DIFFICULTY_TIER = 1;
    private static final int BASE_DAMAGE = 6;

    /**
     * Constructs a new Swordsman with stats tuned for an early,
     * low-difficulty fight.
     *
     * @param name the swordsman's display name
     */
    public Swordsman(String name) {
        super(name, MAX_HEALTH, GOLD_REWARD, DIFFICULTY_TIER);
    }

    /**
     * A Swordsman attacks with a single, low-damage sword strike.
     * This is one of three distinct attack implementations
     * (Swordsman, Beast, Champion) sharing the same method signature,
     * demonstrating polymorphism.
     *
     * @param target the Combatant being attacked
     */
    @Override
    public void attack(Combatant target) {
        System.out.println(getName() + " swings a rusty sword for " + BASE_DAMAGE + " damage!");
        target.takeDamage(BASE_DAMAGE);
    }
}
