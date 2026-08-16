/**
 * Champion is the final, hardest opponent in the coliseum. It extends
 * Opponent and provides its own attack behavior featuring a heavy
 * special attack on a cooldown, completing the set of three distinct
 * attack implementations (Swordsman, Beast, Champion) that
 * demonstrate polymorphism in this project.
 */
public class Champion extends Opponent {

    private static final int MAX_HEALTH = 80;
    private static final int GOLD_REWARD = 60;
    private static final int DIFFICULTY_TIER = 3;
    private static final int BASE_DAMAGE = 10;
    private static final int HEAVY_DAMAGE = 22;
    private static final int HEAVY_ATTACK_COOLDOWN = 3; // turns between heavy attacks

    private int turnsSinceHeavyAttack;

    /**
     * Constructs a new Champion with stats tuned for the final,
     * hardest fight. The heavy attack cooldown starts "ready" so the
     * Champion can use its special attack from the very first turn.
     *
     * @param name the champion's display name
     */
    public Champion(String name) {
        super(name, MAX_HEALTH, GOLD_REWARD, DIFFICULTY_TIER);
        this.turnsSinceHeavyAttack = HEAVY_ATTACK_COOLDOWN;
    }

    /**
     * A Champion attacks with a standard strike each turn, but every
     * few turns unleashes a much heavier special attack instead.
     *
     * @param target the Combatant being attacked
     */
    @Override
    public void attack(Combatant target) {
        if (turnsSinceHeavyAttack >= HEAVY_ATTACK_COOLDOWN) {
            System.out.println(getName() + " unleashes a devastating heavy attack for " + HEAVY_DAMAGE + " damage!");
            target.takeDamage(HEAVY_DAMAGE);
            turnsSinceHeavyAttack = 0;
        } else {
            System.out.println(getName() + " strikes for " + BASE_DAMAGE + " damage!");
            target.takeDamage(BASE_DAMAGE);
            turnsSinceHeavyAttack++;
        }
    }
}
