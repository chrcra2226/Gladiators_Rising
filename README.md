# Gladiator Rising

A text-based, story-driven combat game played entirely in the terminal. The player takes on the role of a gladiator who progresses linearly through a series of coliseum battles, facing tougher opponents each round, earning gold, and upgrading gear at the armory between fights.

This submission covers **Week 3: Class Implementation**. All classes from the approved design document have been implemented and compile/run successfully. Database persistence is intentionally left as an in-memory placeholder (see `GameDatabase.java`) and will be replaced with real SQLite support in Week 4.

## How to Compile and Run

From the `src` directory (or pointing at it), compile all source files into a `bin` output folder:

javac -d bin src/*.java


Then run the game, telling Java where to find the compiled classes and which class contains `main()`:

java -cp bin Coliseum


You'll be dropped into the main menu (New Game / Load Game / Quit) and can play from there.

## Project Structure

GladiatorRising/
├── README.md
├── Crayton_Project_Part2.docx (final proposal + design document)
└── src/
├── Combatant.java (interface)
├── Character.java (abstract class, implements Combatant)
├── Gladiator.java (extends Character; composition via Loadout)
├── Opponent.java (abstract class, extends Character)
├── Swordsman.java (extends Opponent)
├── Beast.java (extends Opponent)
├── Champion.java (extends Opponent)
├── Item.java
├── Loadout.java (composition: owned by Gladiator)
├── GameDatabase.java (CRUD method stubs; SQLite added in Week 4)
└── Coliseum.java (main() and the terminal menu/game loop)


## Class Overview

- **Combatant** — interface declaring `attack()`, `takeDamage()`, and `isAlive()`, the contract every fighter in the game must fulfill.
- **Character** — abstract base class implementing `Combatant`. Holds shared state (name, health) and shared behavior (`takeDamage()`, `isAlive()`), while leaving `attack()` abstract for subclasses.
- **Gladiator** — the player. Extends `Character`, overrides `attack()` and `takeDamage()` to factor in equipped gear, and owns a `Loadout` (composition).
- **Opponent** — abstract class extending `Character`, adding fields shared by all enemies (gold reward, difficulty tier) while still leaving `attack()` abstract.
- **Swordsman / Beast / Champion** — concrete `Opponent` subclasses representing the early, mid, and final tiers of the coliseum. Each overrides `attack()` with distinct behavior, which is the project's core demonstration of polymorphism.
- **Item** — immutable data class representing a piece of armory gear (weapon or armor).
- **Loadout** — holds a `Gladiator`'s currently equipped weapon and armor; exists only to be owned by a `Gladiator` (composition).
- **GameDatabase** — isolates all persistence behind Create/Read/Update/Delete methods for gladiator saves and the gear catalog. Currently backed by in-memory lists with `TODO (Week 4)` comments marking where SQLite/JDBC code will be added.
- **Coliseum** — contains `main()` and drives the terminal menu, game loop, and battles. The only class that touches `Scanner`/`System.out`, keeping I/O separate from game logic.

## Known Limitations (By Design, This Week)

- No data persists between program runs yet — `GameDatabase` uses in-memory lists as a placeholder so the rest of the application could be written and tested against its final method signatures. Real SQLite support arrives in Week 4.
- The armory catalog is seeded with a fixed set of starting items each run (see `Coliseum.seedArmory()`); this will instead be loaded from the database once persistence is added.

## Testing Performed

The full compiled build was manually played through several times to verify:
- Combat resolves correctly and alternates turns until one fighter is defeated.
- Gold is awarded correctly and round progression advances after a win.
- Armory purchases correctly check and deduct gold, and equip the purchased item.
- Equipped armor correctly reduces incoming damage via `Loadout.getDefenseBonus()`.
- The Champion's heavy-attack cooldown fires on the expected turn cadence.
- Save Game and Load Game correctly round-trip a gladiator's round, health, gold, and equipped gear.
- Menu navigation (including "Quit to Main Menu") correctly returns control without exiting the program unexpectedly.