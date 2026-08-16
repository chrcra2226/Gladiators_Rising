import java.util.ArrayList;
import java.util.List;

/**
 * GameDatabase will isolate all SQLite access behind a small set of
 * methods, so no other class in the program contains raw SQL. This
 * satisfies the project's CRUD requirement.
 *
 * Per the course project schedule, actual database interaction code
 * (opening a SQLite connection, running SQL statements through
 * PreparedStatement, etc.) is implemented in Week 4. For this Week 3
 * submission, the class framework and method signatures defined in
 * the approved design document are in place, but each method
 * currently uses simple in-memory placeholders so the rest of the
 * application (Coliseum) can already be written against this class's
 * final public interface.
 */
public class GameDatabase {

    // Temporary in-memory stand-ins for the SQLite tables described in
    // the design document (gladiators, gear). These will be replaced
    // by real SQLite-backed persistence in Week 4.
    private final List<Gladiator> savedGladiators = new ArrayList<>();
    private final List<Item> gearCatalog = new ArrayList<>();

    /**
     * Opens (or creates) the SQLite connection and ensures both the
     * gladiators and gear tables exist.
     *
     * TODO (Week 4): open a real SQLite connection using JDBC and run
     * CREATE TABLE IF NOT EXISTS statements for gladiators and gear.
     *
     * @param dbPath path to the SQLite database file
     */
    public GameDatabase(String dbPath) {
        // Week 4 will open the SQLite connection here.
        // For now, the in-memory lists above act as a stand-in.
    }

    /**
     * Create/Update - saves the given gladiator's current state.
     *
     * TODO (Week 4): replace with an INSERT ... ON CONFLICT UPDATE (or
     * equivalent) PreparedStatement against the gladiators table.
     *
     * @param g the Gladiator to save
     */
    public void saveGladiator(Gladiator g) {
        savedGladiators.removeIf(saved -> saved.getName().equals(g.getName()));
        savedGladiators.add(g);
    }

    /**
     * Read - retrieves a previously saved gladiator by name.
     *
     * TODO (Week 4): replace with a SELECT PreparedStatement against
     * the gladiators table, re-equipping the Loadout from the
     * referenced weapon_id/armor_id in the gear table.
     *
     * @param name the name of the gladiator to load
     * @return the saved Gladiator, or null if no save exists with that name
     */
    public Gladiator loadGladiator(String name) {
        for (Gladiator g : savedGladiators) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }

    /**
     * Delete - removes a saved gladiator.
     *
     * TODO (Week 4): replace with a DELETE PreparedStatement against
     * the gladiators table.
     *
     * @param name the name of the gladiator save to delete
     */
    public void deleteGladiator(String name) {
        savedGladiators.removeIf(saved -> saved.getName().equals(name));
    }

    /**
     * Create - adds a new item to the gear catalog.
     *
     * TODO (Week 4): replace with an INSERT PreparedStatement against
     * the gear table.
     *
     * @param item the Item to add to the catalog
     */
    public void addItem(Item item) {
        gearCatalog.add(item);
    }

    /**
     * Read - returns the full armory catalog.
     *
     * TODO (Week 4): replace with a SELECT * PreparedStatement against
     * the gear table.
     *
     * @return the list of all gear items currently in the catalog
     */
    public List<Item> getAllItems() {
        return new ArrayList<>(gearCatalog);
    }

    /**
     * Update - changes the price of an existing gear item.
     *
     * TODO (Week 4): replace with an UPDATE PreparedStatement against
     * the gear table.
     *
     * @param itemId   the id of the item to update
     * @param newPrice the new price to set
     */
    public void updateItemPrice(int itemId, int newPrice) {
        for (Item item : gearCatalog) {
            if (item.getId() == itemId) {
                gearCatalog.remove(item);
                gearCatalog.add(new Item(item.getId(), item.getName(), item.getType(), newPrice, item.getStatBonus()));
                break;
            }
        }
    }

    /**
     * Delete - removes a discontinued gear item from the catalog.
     *
     * TODO (Week 4): replace with a DELETE PreparedStatement against
     * the gear table.
     *
     * @param itemId the id of the item to remove
     */
    public void deleteItem(int itemId) {
        gearCatalog.removeIf(item -> item.getId() == itemId);
    }
}
