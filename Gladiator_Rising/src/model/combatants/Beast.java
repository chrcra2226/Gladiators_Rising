package model.combatants;

import java.util.Random;

/**
 * Beast is the mid-tier opponent in the coliseum - tougher than a
 * Swordsman, with a chance to land a second, follow-up strike each
 * turn. It extends Opponent and provides its own distinct attack
 * behavior, further demonstrating polymorphism alongside Swordsman
 * and Champion.
 */
public class Beast extends Opponent {

    private static final int MAX_HEALTH = 50;
    private static final int GOLD_REWARD = 30;
    private static final int DIFFICULTY_TIER = 2;
    private static final int BASE_DAMAGE = 9;
    private static final double DOUBLE_HIT_CHANCE = 0.35;

    private final Random random;

    /**
     * Constructs a new Beast with stats tuned for a mid-tier fight.
     *
     * @param name the beast's display name
     */
    public Beast(String name) {
        super(name, MAX_HEALTH, GOLD_REWARD, DIFFICULTY_TIER);
        this.random = new Random();
    }

    /**
     * A Beast claws at its target for base damage, with a chance to
     * land a second claw strike in the same turn.
     *
     * @param target the Combatant being attacked
     */
    @Override
    public void attack(Combatant target) {
        System.out.println(getName() + " claws at its target for " + BASE_DAMAGE + " damage!");
        target.takeDamage(BASE_DAMAGE);

        if (random.nextDouble() < DOUBLE_HIT_CHANCE) {
            System.out.println(getName() + " lands a second claw strike for " + BASE_DAMAGE + " more damage!");
            target.takeDamage(BASE_DAMAGE);
        }
    }
}
