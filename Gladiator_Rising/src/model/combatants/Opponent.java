package model.combatants;

/**
 * Name: Christopher Crayton
 * Date: August 27, 2026
 * Course: SDC330 - Advanced Object-Oriented Programming using Java
 * 
 * Opponent is the abstract base class for every enemy the gladiator
 * faces in the coliseum. It extends Character (multi-level
 * inheritance: Opponent extends Character, which implements
 * Combatant), adding the fields shared by all enemies - the gold they
 * award on defeat and their difficulty tier - while still leaving
 * attack() abstract for its concrete subclasses to implement.
 *
 * This class lets the Coliseum's battle loop treat every enemy
 * uniformly as an Opponent (or even more generally as a Character or
 * Combatant), without needing to know whether it is actually facing a
 * Swordsman, a Beast, or a Champion.
 */
public abstract class Opponent extends Character {

    private final int goldReward;
    private final int difficultyTier;

    /**
     * Constructs a new Opponent. Protected because Opponent is
     * abstract and should only ever be constructed by a concrete
     * subclass via super(...).
     *
     * @param name           the opponent's display name
     * @param maxHealth      the opponent's maximum starting health
     * @param goldReward     gold awarded to the gladiator on victory
     * @param difficultyTier a simple numeric indicator of how tough
     *                       this opponent is (1 = easiest)
     */
    protected Opponent(String name, int maxHealth, int goldReward, int difficultyTier) {
        super(name, maxHealth);
        this.goldReward = goldReward;
        this.difficultyTier = difficultyTier;
    }

    /**
     * Still abstract at this level - each concrete opponent type
     * (Swordsman, Beast, Champion) supplies its own attack behavior.
     *
     * @param target the Combatant being attacked
     */
    @Override
    public abstract void attack(Combatant target);

    public int getGoldReward() {
        return goldReward;
    }

    public int getDifficultyTier() {
        return difficultyTier;
    }
}
