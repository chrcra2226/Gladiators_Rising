# Gladiator Rising

A text-based  combat game played entirely in the terminal. The player takes on the role of a gladiator who progresses linearly through a series of coliseum battles, facing tougher opponents each round, earning gold, and upgrading gear (or healing with a free Bandage) at the armory between fights.

This submission restructures the application into an **MVC (Model-View-Controller)** package layout and adds a **Bandage** consumable. All classes from the approved design document are implemented, and `GameDatabase` uses the real SQLite JDBC driver (`org.xerial:sqlite-jdbc`) to persist gladiator saves and the armory catalog to a `coliseum.db` file on disk.

## YouTube link for video demonstration

Link - https://youtu.be/1YyCFG4YCSw

## How to Compile and Run

This project depends on the SQLite JDBC driver, included at `lib/sqlite-jdbc-3.53.2.1.jar`. That jar needs to be on the classpath for both compiling and running. Because the source is now organized into packages, use the `@sources.txt`-style file list or a recursive find rather than a flat `src/*.java` glob.

**Compile** (from the project root):

find src -name "*.java" > sources.txt
javac -cp lib/sqlite-jdbc-3.53.2.1.jar -d bin @sources.txt


**Run**, using the entry point class (default package, so no qualified name needed):

java -cp "bin:lib/sqlite-jdbc-3.53.2.1.jar" Main

(On Windows, use a semicolon instead of a colon to separate classpath entries: `-cp "bin;lib/sqlite-jdbc-3.53.2.1.jar"`)

You'll be dropped into the main menu (New Game / Load Game / Quit) and can play from there. A `coliseum.db` SQLite file will be created in the working directory the first time you run it, and your gladiator saves and armory catalog will persist in that file across runs.

## Project Structure

GladiatorRising/
README.md
Crayton_Project_Part1.docx (Initial Proposal)
Crayton_Project_Part2.docx (Final Proposal + Design Document)
Crayton_Project_Part2 (Updated).docx (Final Proposal + Design Document)

├── coliseum.db

├── .gitignore

├── lib/

│   └── sqlite-jdbc-3.53.2.1.jar (SQLite JDBC driver)

└── src/

|   ├── Main.java (entry point; wires up the database and hands off to the Controller)
    
|   │
    
|   ├── controller/
    
|   │   └── GameController.java (menu loop, battle flow; orchestrates Model + View)
    
|   │
    
|   ├── model/
    
|   │   ├── combatants/
    
|   │   │   ├── Combatant.java (interface)
   
|   │   │   ├── Character.java (abstract class, implements Combatant)
    
|   │   │   ├── Gladiator.java (extends Character; composition via Loadout)
    
|   │   │   ├── Opponent.java (abstract class, extends Character)
    
|   │   │   ├── Swordsman.java (extends Opponent)
    
|   │   │   ├── Beast.java (extends Opponent)
    
|   │   │   └── Champion.java (extends Opponent)
    
|   │   │
    
|   │   ├── items/
    
|   │   │   ├── Item.java
    
|   │   │   └── Loadout.java (composition: owned by Gladiator)
    
|   │   │
    
|   │   └── database/
    
|   │       └── GameDatabase.java (SQLite-backed CRUD for gladiator saves and the gear catalog)
    
|   │
    
|   └── view/
   
|       └── ConsoleView.java (every piece of text the player sees; no game logic)

## MVC Structure

- **Model** (`model.combatants`, `model.items`, `model.database`) — the game's data and rules: fighters, gear, and persistence. Knows nothing about menus, console formatting, or Scanner input.
- **View** (`ConsoleView`) — every prompt, menu, and result message the player sees. Has no game logic and never reads input; it only formats and prints what the Controller tells it to.
- **Controller** (`GameController`) — owns the `Scanner`, drives the menu/game loop, and is the only class that talks to both the Model and the View. It reads input, asks the Model to do things (attack, save, spend gold), and tells the View what to display in response.

**One deliberate boundary**, documented in `GameController`'s class comment: the combat narration printed inside each `Combatant`'s `attack()` method (e.g., "Swordsman swings a rusty sword for 6 damage!") stays in the Model rather than moving to the View. That narration is part of *how* each opponent attacks, not a separate reporting step — it's what makes `Swordsman`, `Beast`, and `Champion`'s overridden `attack()` implementations distinct from one another, which is the project's polymorphism demonstration. Moving it out would mean changing `Combatant.attack(Combatant target)` from `void` to a method that returns a result for the View to print, which departs from the approved design document's interface.

## Class Overview

- **Combatant** — interface declaring `attack()`, `takeDamage()`, and `isAlive()`, the contract every fighter in the game must fulfill.
- **Character** — abstract base class implementing `Combatant`. Holds shared state (name, health) and shared behavior (`takeDamage()`, `isAlive()`), while leaving `attack()` abstract for subclasses.
- **Gladiator** — the player. Extends `Character`, overrides `attack()` and `takeDamage()` to factor in equipped gear, owns a `Loadout` (composition), and can use a free **Bandage** every other round to fully heal (`useBandage()`, `isBandageReady()`, `getBandageStatus()`).
- **Opponent** — abstract class extending `Character`, adding fields shared by all enemies (gold reward, difficulty tier) while still leaving `attack()` abstract.
- **Swordsman / Beast / Champion** — concrete `Opponent` subclasses representing the early, mid, and final tiers of the coliseum. Each overrides `attack()` with distinct behavior, which is the project's core demonstration of polymorphism.
- **Item** — immutable data class representing armory gear: a weapon, armor, or the Bandage consumable.
- **Loadout** — holds a `Gladiator`'s currently equipped weapon and armor; exists only to be owned by a `Gladiator` (composition).
- **GameDatabase** — isolates all persistence behind Create/Read/Update/Delete methods for gladiator saves and the gear catalog, backed by a real SQLite database via the `org.xerial:sqlite-jdbc` driver. All statements use `PreparedStatement` with bound parameters rather than string concatenation. Includes a lightweight schema migration so older `coliseum.db` files (from before the Bandage feature) keep working.
- **ConsoleView** — the View. Every menu, prompt, and message shown to the player lives here as a named method (e.g., `showArmory()`, `showBandageUsed()`), so `GameController` never contains a literal display string.
- **GameController** — the Controller. Owns the `Scanner`, drives the main menu and in-game loop, and is the only class that calls both Model methods and View methods.
- **Main** — the entry point. Creates the `GameDatabase`, builds a `GameController`, and starts it. No game logic of its own.

## Armory Catalog

| Item | Type | Price | Bonus |
|---|---|---|---|
| Iron Shortsword | Weapon | 10g | +4 attack |
| Steel Longsword | Weapon | 25g | +9 attack |
| War Hammer | Weapon | 50g | +14 attack |
| Leather Vest | Armor | 10g | +3 defense |
| Chainmail | Armor | 25g | +7 defense |
| Plate Armor | Armor | 50g | +12 defense |
| Bandage | Consumable | Free | Fully restores health; usable every other round |

## Database Details

- **gear** table: the armory catalog (`id`, `name`, `type`, `price`, `stat_bonus`). Seeded automatically on first run via `GameController.seedArmory()`; safe to re-run since `addItem()` uses `INSERT OR IGNORE`.
- **gladiators** table: one row per saved gladiator (`id`, `name` (unique), `round`, `health`, `max_health`, `gold`, `weapon_id`, `armor_id`, `last_bandage_round`). `weapon_id`/`armor_id` are nullable foreign keys referencing `gear.id`, matching the Gladiator/Loadout/Item composition from the class design — a save simply points at the gear rows it currently has equipped rather than duplicating gear data.
- Foreign key enforcement is turned on via `PRAGMA foreign_keys = ON` when the connection opens.
- All four CRUD operations are exercised: gladiator saves are created/updated together through `saveGladiator()` (an upsert keyed on the unique `name` column), read through `loadGladiator()`, and removed through `deleteGladiator()`; gear items are created through `addItem()`, read through `getAllItems()`, updated through `updateItemPrice()`, and removed through `deleteItem()`.
- The database file (`coliseum.db`) is created automatically in the working directory the first time the app runs.

## Testing Performed

The full compiled build was manually played through several times to verify:
- Combat resolves correctly and alternates turns until one fighter is defeated.
- Gold is awarded correctly and round progression advances after a win.
- Armory purchases correctly check and deduct gold, and equip the purchased item.
- The Bandage fully heals when used, is correctly blocked on the following round, and becomes available again two rounds after use (verified with a small standalone driver against `Gladiator` directly, since live combat has randomness that makes exact-round testing unreliable).
- Equipped armor correctly reduces incoming damage via `Loadout.getDefenseBonus()`.
- The Champion's heavy-attack cooldown fires on the expected turn cadence.
- Menu navigation (including "Quit to Main Menu") correctly returns control without exiting the program unexpectedly.

Database persistence was additionally verified across separate program runs (separate JVM processes, not just in-memory state within one run):
- A new gladiator was created, played through several rounds, equipped with purchased armor, saved, and reloaded in a fresh JVM process with round, health, gold, and equipped gear all correctly restored.
- A database file with the pre-Bandage schema (no `last_bandage_round` column) was used to confirm the automatic migration adds the column and existing saves still load correctly.
- Update and Delete were exercised directly against `GameDatabase`: updating a gear item's price, deleting a gladiator save, and deleting a gear item, confirming all four CRUD operations work correctly against the live database.

After the MVC restructure, the full test suite above was re-run against the new package layout to confirm behavior is unchanged — same combat output, same armory listing and pricing, same save/load round-trip, now running through the flat `Main` class (default package) instead of the old `Coliseum` class.
