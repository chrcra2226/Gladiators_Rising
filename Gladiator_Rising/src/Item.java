/**
 * Item is a simple data class representing a single piece of gear
 * available in the armory - either a weapon or a suit of armor.
 *
 * Item objects are immutable once created: all fields are private and
 * final, and there are no setters, only getters. This keeps gear data
 * predictable once it has been loaded from the catalog.
 */
public class Item {

    private final int id;
    private final String name;
    private final String type;      // "WEAPON" or "ARMOR"
    private final int price;
    private final int statBonus;    // attack bonus for weapons, defense bonus for armor

    /**
     * Constructs a new Item.
     *
     * @param id        unique identifier for this item
     * @param name      display name of the item
     * @param type      "WEAPON" or "ARMOR"
     * @param price     cost in gold to purchase this item from the armory
     * @param statBonus the attack or defense bonus this item provides
     */
    public Item(int id, String name, String type, int price, int statBonus) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.statBonus = statBonus;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public int getStatBonus() {
        return statBonus;
    }

    /**
     * Formats this item for armory listings.
     *
     * @return a human-readable description of the item
     */
    @Override
    public String toString() {
        return name + " [" + type + "] - " + price + "g (bonus: +" + statBonus + ")";
    }
}
