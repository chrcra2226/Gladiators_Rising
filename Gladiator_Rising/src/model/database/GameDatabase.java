package model.database;

import model.combatants.Gladiator;
import model.items.Item;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * GameDatabase isolates all SQLite access behind a small set of
 * methods, so no other class in the program contains raw SQL. This
 * satisfies the project's CRUD requirement.
 *
 * It uses the SQLite JDBC driver (org.xerial:sqlite-jdbc) to open a
 * connection to a local .db file and persists two tables, matching
 * the approved design document:
 *
 *   gladiators - one row per saved gladiator (name, round, health,
 *                max_health, gold, and the ids of their equipped
 *                weapon/armor)
 *   gear       - the armory catalog (name, type, price, stat bonus)
 *
 * gladiators.weapon_id and gladiators.armor_id are foreign keys
 * referencing gear.id, matching the Gladiator/Loadout/Item
 * composition used in the class design: a gladiator's save simply
 * points at whichever gear rows it currently has equipped instead of
 * duplicating that data.
 *
 * All SQL statements use PreparedStatement with bound parameters,
 * never string concatenation, to avoid SQL injection.
 */
public class GameDatabase {

    private final Connection connection;

    /**
     * Opens (or creates) the SQLite connection and ensures both the
     * gladiators and gear tables exist.
     *
     * @param dbPath path to the SQLite database file
     */
    public GameDatabase(String dbPath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            try (Statement pragma = connection.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON");
            }
            createTables();
        } catch (SQLException e) {
            // A database that cannot be opened is a fatal setup error
            // for this application, so it is wrapped in an unchecked
            // exception rather than forcing every caller of every
            // GameDatabase method to declare "throws SQLException".
            throw new RuntimeException("Unable to open database at " + dbPath, e);
        }
    }

    /**
     * Creates the gladiators and gear tables if they do not already
     * exist. Safe to call every time the application starts.
     */
    private void createTables() throws SQLException {
        String createGear =
                "CREATE TABLE IF NOT EXISTS gear (" +
                "  id INTEGER PRIMARY KEY, " +
                "  name TEXT NOT NULL, " +
                "  type TEXT NOT NULL, " +
                "  price INTEGER NOT NULL, " +
                "  stat_bonus INTEGER NOT NULL" +
                ")";

        String createGladiators =
                "CREATE TABLE IF NOT EXISTS gladiators (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "  name TEXT NOT NULL UNIQUE, " +
                "  round INTEGER NOT NULL, " +
                "  health INTEGER NOT NULL, " +
                "  max_health INTEGER NOT NULL, " +
                "  gold INTEGER NOT NULL, " +
                "  weapon_id INTEGER, " +
                "  armor_id INTEGER, " +
                "  last_bandage_round INTEGER NOT NULL DEFAULT -1, " +
                "  FOREIGN KEY (weapon_id) REFERENCES gear(id), " +
                "  FOREIGN KEY (armor_id) REFERENCES gear(id)" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createGear);
            stmt.execute(createGladiators);
        }

        // Lightweight migration: if this database file was created by an
        // earlier version of the app (before the Bandage feature added
        // last_bandage_round), CREATE TABLE IF NOT EXISTS above will not
        // add the missing column to an already-existing table. Add it
        // here if it's not already present, so older save files keep
        // working instead of failing on the next save/load.
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("ALTER TABLE gladiators ADD COLUMN last_bandage_round INTEGER NOT NULL DEFAULT -1");
        } catch (SQLException e) {
            // Column already exists - nothing to do. SQLite has no
            // "ADD COLUMN IF NOT EXISTS", so this is the standard way
            // to make the migration idempotent.
        }
    }

    /**
     * Create/Update - saves the given gladiator's current state. If a
     * save with this gladiator's name already exists it is replaced;
     * otherwise a new row is created. This satisfies both the Create
     * and Update parts of the CRUD requirement for the gladiators
     * table.
     *
     * @param g the Gladiator to save
     */
    public void saveGladiator(Gladiator g) {
        String sql =
                "INSERT INTO gladiators (name, round, health, max_health, gold, weapon_id, armor_id, last_bandage_round) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(name) DO UPDATE SET " +
                "  round = excluded.round, " +
                "  health = excluded.health, " +
                "  max_health = excluded.max_health, " +
                "  gold = excluded.gold, " +
                "  weapon_id = excluded.weapon_id, " +
                "  armor_id = excluded.armor_id, " +
                "  last_bandage_round = excluded.last_bandage_round";

        Item weapon = g.getLoadout().getWeapon();
        Item armor = g.getLoadout().getArmor();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, g.getName());
            stmt.setInt(2, g.getRound());
            stmt.setInt(3, g.getHealth());
            stmt.setInt(4, g.getMaxHealth());
            stmt.setInt(5, g.getGold());

            if (weapon != null) {
                stmt.setInt(6, weapon.getId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            if (armor != null) {
                stmt.setInt(7, armor.getId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.setInt(8, g.getLastBandageRound());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving gladiator: " + e.getMessage());
        }
    }

    /**
     * Read - retrieves a previously saved gladiator by name,
     * re-equipping their Loadout from the referenced weapon_id and
     * armor_id in the gear table.
     *
     * @param name the name of the gladiator to load
     * @return the saved Gladiator, or null if no save exists with that name
     */
    public Gladiator loadGladiator(String name) {
        String sql = "SELECT * FROM gladiators WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Gladiator gladiator = new Gladiator(rs.getString("name"), rs.getInt("max_health"));
                gladiator.restoreState(
                        rs.getInt("round"),
                        rs.getInt("health"),
                        rs.getInt("gold"),
                        rs.getInt("last_bandage_round")
                );

                int weaponId = rs.getInt("weapon_id");
                if (!rs.wasNull()) {
                    Item weapon = getItemById(weaponId);
                    if (weapon != null) {
                        gladiator.equip(weapon);
                    }
                }

                int armorId = rs.getInt("armor_id");
                if (!rs.wasNull()) {
                    Item armor = getItemById(armorId);
                    if (armor != null) {
                        gladiator.equip(armor);
                    }
                }

                return gladiator;
            }
        } catch (SQLException e) {
            System.out.println("Error loading gladiator: " + e.getMessage());
            return null;
        }
    }

    /**
     * Delete - removes a saved gladiator.
     *
     * @param name the name of the gladiator save to delete
     */
    public void deleteGladiator(String name) {
        String sql = "DELETE FROM gladiators WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting gladiator: " + e.getMessage());
        }
    }

    /**
     * Create - adds a new item to the gear catalog. Uses INSERT OR
     * IGNORE so re-seeding the starting catalog on a later run does
     * not fail if those rows already exist from a previous run.
     *
     * @param item the Item to add to the catalog
     */
    public void addItem(Item item) {
        String sql = "INSERT OR IGNORE INTO gear (id, name, type, price, stat_bonus) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getType());
            stmt.setInt(4, item.getPrice());
            stmt.setInt(5, item.getStatBonus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
    }

    /**
     * Read - returns the full armory catalog.
     *
     * @return the list of all gear items currently in the catalog
     */
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM gear ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                items.add(mapRowToItem(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving items: " + e.getMessage());
        }

        return items;
    }

    /**
     * Update - changes the price of an existing gear item.
     *
     * @param itemId   the id of the item to update
     * @param newPrice the new price to set
     */
    public void updateItemPrice(int itemId, int newPrice) {
        String sql = "UPDATE gear SET price = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newPrice);
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating item price: " + e.getMessage());
        }
    }

    /**
     * Delete - removes a discontinued gear item from the catalog.
     *
     * @param itemId the id of the item to remove
     */
    public void deleteItem(int itemId) {
        String sql = "DELETE FROM gear WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting item: " + e.getMessage());
        }
    }

    /**
     * Looks up a single gear item by id. Used internally by
     * loadGladiator() to re-hydrate the Item objects referenced by a
     * saved gladiator's weapon_id/armor_id.
     *
     * @param id the gear item's id
     * @return the matching Item, or null if no item has that id
     */
    private Item getItemById(int id) {
        String sql = "SELECT * FROM gear WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToItem(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving item: " + e.getMessage());
            return null;
        }
    }

    /**
     * Builds an Item object from the current row of a gear ResultSet.
     *
     * @param rs a ResultSet positioned on a row from the gear table
     * @return the corresponding Item
     */
    private Item mapRowToItem(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getInt("price"),
                rs.getInt("stat_bonus")
        );
    }

    /**
     * Closes the underlying database connection. Should be called
     * when the application is shutting down.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database: " + e.getMessage());
        }
    }
}
