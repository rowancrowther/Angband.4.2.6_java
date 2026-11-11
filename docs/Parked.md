# Parked items

*Created 2026-11-11.* This file lists the work that has been deliberately set aside, one line each: what it is, what it waits on, the date it was decided to park it, and where the detail lives. It links to the existing entries and does not copy them, so a change of mind is made in the place the detail lives and then reflected here. It is not a checklist (`ROADMAP.md` is) and it is not a list of accepted deviations (`Accepted_Deviations.md` is). Parked means "not now, and not forgotten"; accepted means "decided, and Java differs from C on purpose".

Dates are the day the item was decided to be parked. Where a source gives only a file date or a "still open at" date, the line says so, and that date is the best record there is.

## Parsers and data

- **Monster lore parsing (`LoreReader`, `LoreGrammar.g4`, `LoreLexer.g4`).** Parked 2026-08-13. Waits on real game-generated lore data to test against; it is the last item on the roadmap. The `LoreReaderTest` skips are this park, not neglect. Detail: `Issue_Approach_Order.md` Stage 10, `grammar_issues.md` (the lore entry), `ReaderPort.md`.
- **Loading `lore.txt` at start-up.** Parked 2026-08-13 ("Defer to chapter 8"). The load call in `GameConstants` stays commented out; the file lives in the user directory and C tolerates its absence. Waits on save/load in Chapter 8. Detail: `ROADMAP.md`.
- **Load-time checks on `dice:` and `expr:` text.** Decided 2026-10-25, entered in Stage 10 on 2026-11-11. A bad dice string, a bad operations string and an `expr:` letter that no `$` binds are refused by C at load and are not checked in Java. Waits on the roll engine, which `EffectAssembler` will call at load, as it calls `Random.parseStr` today. Detail: `Issue_Approach_Order.md` Stage 10, memory `effect-dice-validation-deferred-to-roll-engine`.

## Design

- **The sealed `ObjectPropertySubject` interface.** Parked 2026-08-10 ("deferred, not rejected"). It would buy `hashCode` and exhaustiveness, which is not worth six touches in main for two call sites. Detail: `Chapter_2_Roadmap.md`.
- **`Term_what`, `Term_save`, `Term_load` and `Term_load_all`.** Parked 2026-09-10, when `CellGrid` was made the only record of terminal content. They cannot be ported as direct reads of `Term.scr` and need a new design that reads from `Screen`/`CellGrid`. Detail: memory `term-cellgrid-single-source-of-truth`.
- **The birth note (`splashScreenBirthNote`).** Parked for Chapter 3; `Architecture_migration.md` marks the note "still open at 2026-08-13" and gives no earlier date. Status not re-checked on 2026-11-11, so confirm it is still parked before relying on this line. Detail: `Architecture_migration.md`, the "Why the birth note is parked" note.

## Waiting on a later chapter

- **Spellbook `sval` (`MagicBook.setSVal()`) and flavour assignment (`flavor_init`).** Parked 2026-08-27. Until the first is called every book's `sval` reads 0, and until the second is written `getFlavour()` answers null for every kind. Detail: `ROADMAP.md`.
- **Chapter 4 and Chapter 7 worklists.** Each was moved out of the day-to-day lists from 2026-08-16 onwards, with its own date on each line (for example `Square.lightSpot` and `ItemObject.description`). Detail: `Chapter_4_Worklist.md`, `Chapter_7_Worklist.md`.

## Keeping it current

- When something is parked, add one line here with its date and a pointer to the detail.
- When it is picked up, delete the line here and tick or close it where the detail lives.
