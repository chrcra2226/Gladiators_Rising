package model.items;

/**
 * Loadout holds the Gladiator's currently equipped weapon and armor.
 *
 * This class exists purely to be owned by a Gladiator - a Loadout has
 * no meaningful existence on its own, which makes it the project's
 * demonstration of composition. A Gladiator HAS-A Loadout, and the
 * Loadout is created and destroyed along with the Gladiator that owns
 * it (see Gladiator's constructor).
 */
public class Loadout {

    private Item weapon;   // nullable - no weapon equipped by default
    private Item armor;    // nullable - no armor equipped by default

    /**
     * Constructs a new, empty Loadout with no weapon or armor
     * equipped.
     */
    public Loadout() {
        this.weapon = null;
        this.armor = null;
    }

    /**
     * Equips the given item as the current weapon, replacing whatever
     * weapon (if any) was equipped before.
     *
     * @param item the weapon Item to equip
     */
    public void equipWeapon(Item item) {
        this.weapon = item;
    }

    /**
     * Equips the given item as the current armor, replacing whatever
     * armor (if any) was equipped before.
     *
     * @param item the armor Item to equip
     */
    public void equipArmor(Item item) {
        this.armor = item;
    }

    /**
     * @return the attack bonus provided by the equipped weapon, or 0
     *         if no weapon is equipped
     */
    public int getAttackBonus() {
        return (weapon != null) ? weapon.getStatBonus() : 0;
    }

    /**
     * @return the defense bonus provided by the equipped armor, or 0
     *         if no armor is equipped
     */
    public int getDefenseBonus() {
        return (armor != null) ? armor.getStatBonus() : 0;
    }

    public Item getWeapon() {
        return weapon;
    }

    public Item getArmor() {
        return armor;
    }

    /**
     * Formats the currently equipped weapon and armor for the status
     * screen.
     *
     * @return a human-readable summary of equipped gear
     */
    @Override
    public String toString() {
        String weaponText = (weapon != null) ? weapon.getName() : "None";
        String armorText = (armor != null) ? armor.getName() : "None";
        return "Weapon: " + weaponText + " | Armor: " + armorText;
    }
}
