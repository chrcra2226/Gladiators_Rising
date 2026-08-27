package model.items;

/**
 * Name: Christopher Crayton
 * Date: August 27, 2026
 * Course: SDC330 - Advanced Object-Oriented Programming using Java
 * 
 * Item is a simple data class representing a single piece of gear
 * available in the armory - a weapon, a suit of armor, or a
 * consumable (currently just the Bandage).
 *
 * Item objects are immutable once created: all fields are private and
 * final, and there are no setters, only getters. This keeps gear data
 * predictable once it has been loaded from the catalog.
 */
public class Item {

    private final int id;
    private final String name;
    private final String type;      // "WEAPON", "ARMOR", or "CONSUMABLE"
    private final int price;
    private final int statBonus;    // attack bonus for weapons, defense bonus for armor; unused (0) for consumables

    /**
     * Constructs a new Item.
     *
     * @param id        unique identifier for this item
     * @param name      display name of the item
     * @param type      "WEAPON", "ARMOR", or "CONSUMABLE"
     * @param price     cost in gold to purchase this item from the armory
     * @param statBonus the attack or defense bonus this item provides
     *                  (unused for consumables)
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
     * Formats this item for armory listings. Consumables (which have
     * no attack/defense bonus) get a simpler description than
     * equippable gear.
     *
     * @return a human-readable description of the item
     */
    @Override
    public String toString() {
        if (type.equalsIgnoreCase("CONSUMABLE")) {
            String priceText = (price == 0) ? "Free" : (price + "g");
            return name + " [" + type + "] - " + priceText;
        }
        return name + " [" + type + "] - " + price + "g (bonus: +" + statBonus + ")";
    }
}
