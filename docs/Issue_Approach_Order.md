# Suggested order of approach: assembler and grammar issues

**Date:** 2026-10-19

**What this is:** a suggested order for working through every entry in `docs/Assembler_Issues.md` (91 entries, tagged **A**) and
`docs/grammar_issues.md` (108 entries, tagged **G**), arranged in stages from the faults that reach the shipped data down to
comment-only fixes. It was built from the two files as they stood on the date above. Nothing in it was re-checked against the source, so
confirm an entry still holds before working on it. Entries are named by their heading, shortened.

This is a route, not a checklist. `docs/ROADMAP.md` stays the only place anything is ticked.

## How the order was chosen

- Faults that change a loaded game on the shipped data come first, because they are the only ones that can break play today.
- Faults that crash or lose records without saying so come second.
- Anything that is the same fix in many places is done once, as a helper or shared import, before the per-file work that uses it.
- Assembler work comes before grammar work. Several grammar fixes make `ParseRecord` fields absent-capable, and the assemblers should
  settle the absent-value convention before they are touched a second time.
- Grammar edits are batched by shared import, so each ANTLR regeneration covers as many lexers as possible.
- Every entry has a test that pins the Java behaviour ("...HereWhereC..."). Each fix flips that test, so the test changes ride with the fix.

## Stage 0: decisions that set the conventions

Take these first. They are the eight "Question for Rowan" entries plus two conventions that many entries lean on.

- **Absent value.** When a directive is left out, does the Java field hold `null` (C's `NULL`), `""`, or a zero value (`Random.Zero()` for
  dice)? Stage 1 and Stage 3 apply the answer in about twenty places.

**Answer: Absent values are set to null**

- **Strict or C-lenient.** Where C accepts something sloppy (a colour typo becoming white, a bare flag list, a colour name), does the
  port copy C or stay strict on purpose? Stage 3 and Stage 7 apply the answer.

**Answer (2026-10-19): colours are C-lenient.** An unknown colour letter or name loads as white, as in `color_char_to_attr` and
`color_text_to_attr` (`z-color.c`), and a space is `COLOUR_DARK`. Names are matched case-insensitively. This covers the assemblers
(`ObjectBaseAssembler`, `MonsterSpellLevelAssembler`, `FlavourAssembler`) and the grammars (`PitLexer.g4`, `VisualsGrammar.g4`,
`imports/AngbandDisplayCharacter.g4`). Colours only: the bare flag list and the other items below are still open.

**Strict or sloppy: when each group is decided (2026-10-19).** Each row is a group of entries where C is more or less forgiving than
the Java. Colours are settled above; every other group is only tiered here, not decided.

- **Tier 0:** decide now, before any Stage 1 work.
- **Tier 1:** decide before leaving this document.
- **Tier 2:** not decided here. It goes in the "things not to drop" section of a precis.

| Group | Tier | What it covers |
|---|---|---|
| Colours | decided | Lenient, see above. |
| Absent fields | decided | Settled as `null` by the first answer; the strict/sloppy side is a missing field dropping the record where C stores 0. |
| Whitespace and separators | decided | Indented lines, flags split on spaces, trailing blanks, `alloc:` spacing. |
| Missing or empty directives | decided | A bare `name:`, a fixed directive order and count, an empty `flags:`. |
| Unresolved names | decided | An unknown `act:`, `mon-ban:` or `fallback:` name, an unknown `color-cycle:` group. |
| Limits and range checks | decided | `drop:` bounds, `alloc:` 0 to 255, the 254 limits, the Visuals caps. Java is the lenient side here. |
| Boolean parsing | decided | `aim:`, `uniques:`, `destroy:` and `magic:` true only for `1`; `bindui:` aux true for anything but `0`. |
| Case sensitivity | decided | Exact-case `msg:` and `msgt:` lookups against case-insensitive roles, vault `type:` and realm `stat:`. |
| Repeated directives | decided | Overwrite, append or reject for `msg:`, `desc:`, `curse:`, `race-flag:`, `base:` and `magic:`. |
| Text content | decided | Trimmed `name:`, glyphs outside the Basic Multilingual Plane, colons in descriptions, text after a value, upper-case-only identifiers. |
| Empty file or missing `record-count:` | decided | A header with no records, an empty file, a header the upstream file does not carry. |
| Reversed chains | decided | Always echo C's ordering. Supersedes the 2026-10-17 decision (file order, sorted by registry position only where C's order is needed). |
| Bad record: drop it, drop the file, or stop the load | 1 | A lexer error empties the file, an assembler error drops one record, some paths abort the load. |
| Sentinel names `NONE` and `MAX` | 1 | Accepted by the Slay, Brand, EgoItem, Shape, Body, PlayerRace, Projection and PlayerProperty assemblers; C rejects them. |
| A field on the wrong kind of record | 1 | `graphics:` on a non-`INSTA_ART` artifact, `subtype:` on a non-flag property, `value:` and `code:` on the wrong property record. |
| Numbers | 2 | Octal, hex and plus signs read by `strtol` base 0 in C, decimal only in Java. |

"Unresolved names" was listed twice in the request; it is one row, and the second listing was read as "Missing or empty directives".

**Answers for the Tier 0 groups (2026-10-19).** The rule running through them is "match C exactly": where C accepts something the
Java rejects, the Java accepts it, and where C rejects something the Java accepts, the Java rejects it. Where C raises a parse error,
the port's usual soft error that skips the record applies; whether that should instead drop the file or stop the load is the Tier 1
row "Bad record".

- **Absent fields: C-lenient.** A record that leaves a directive out loads with C's zero, `null` or default, and `ParseRecord` fields
  become absent-capable. `MonsterSpellLevelAssembler` prepends the implicit level 0 (power 0, `null` text) when the first level's power
  is above 0 or the list is empty, so every later walker can start at the head as C does.
- **Whitespace and separators: match C exactly in both directions.** Indented lines and comments, flags split on any run of spaces
  and bars, blanks after the last token and any spacing round `alloc:` figures are accepted. The places where Java is the more lenient
  side (trailing blanks after a flag in `BrandLexer.g4` and `ProjectionLexer.g4`, optional spaces round `to` in `ItemObjectLexer.g4`,
  trimmed `name:` in `BlowMethodGrammar.g4` and `ActivationsLexer.g4`) are made to reject as C does.
- **Missing or empty directives: match C.** Directives come in any order, any can be omitted, a bare `name:` is a record, and `flags:`
  may be empty. This is the Stage 7a and 7b work.
- **Unresolved names: match C both ways.** Where C is silent (unknown `act:`, `fallback:`, `mon-ban:`, `color-cycle:` group, projection
  `code:` after the 25 elements) the record loads silently. Where C raises an error (unknown spell in `message-*:`, unknown
  `type-mult:` tval, `subtype:` on a non-flag, which C stores) the Java follows C: a soft error and a skipped record for the first two,
  and the `subtype:` stored. [**CLOSED** 26-10-20 for the unknown spell in `message-*:` in `MonsterGrammar.g4` and
  `MonsterAssembler.java`; still open for `type-mult:` and `subtype:`]
- **Limits and range checks: enforce everything C enforces.** The `drop:` bounds, unsigned negatives, `alloc:` 0 to 255, the 254-brand,
  slay and curse limits (the curse limit counts records, not survivors) and the Visuals caps. This answers the Stage 0 question on
  `VisualsCycleAssembler` caps: enforce the 8, 64 and 32 limits.
- **Boolean parsing: match C.** The number is parsed, then C's test for that field is applied through one helper: `!= 0` for most,
  `== 1` for `pit:`.
- **Case sensitivity: match C both ways.** `msg:`, `msgt:` and realm `stat:` become case-blind, with the numeric form for message
  names. The `friends:` role and vault `type:` become case-exact.
- **Repeated directives: match C per handler.** Join, replace, bind every line or reject, as each C handler does. `bindui:` in
  `ObjectPropertyParseRecord` becomes a list, and a later zero-power `curse:` no longer erases an earlier one.
- **Text content: match C throughout.** Free-text fields take the rest of the line, colons included, text after a value is ignored,
  the `RealmLexer.g4` second colon on `stat:` is dropped, and glyphs widen from `char` to a code point.
- **Empty file or missing `record-count:`: the header is optional.** If present it is still checked as a soft error. An empty file
  loads where C loads it and fails where C fails it, so upstream data files load unchanged.
- **Reversed chains: always echo C's ordering.** This replaces the 2026-10-17 decision, so the `brands-slays-order-at-point-of-use`
  approach no longer stands.
- A: `visuals/VisualsCycleAssembler.java` caps. Enforce the 8, 64 and 32 limits, or keep "no limit"?

**Answer (2026-10-19): enforce as C does.** A 33rd colour on a cycle and a 513th `cycle:` line are errors that refuse the file; a ninth
group and a 65th cycle in a group are silently dropped. This confirms the Tier 0 "Limits and range checks" answer.
- G: `VisualsGrammar.g4`. Accept any single character as a colour and map the unlisted ones to white, or stay strict? `PitLexer.g4` shares the question.

**Answer (2026-10-19): lenient.** Any single character is accepted as a colour and the unlisted ones map to white. This confirms the
Stage 0 "Strict or C-lenient" answer and covers `VisualsGrammar.g4` and `PitLexer.g4`.
- G: `RoomProfileGrammar.g4`. Is the single `|`-separated `flags:` line deliberate?

**Answer (2026-10-19): not deliberate, match C.** A second `flags:` line accumulates and any run of spaces and bars separates flags.
This follows the Tier 0 "Whitespace and separators" and "Repeated directives" answers and is done in Stage 7d.
- G: `DungeonProfileGrammar.g4`. Is requiring a directive after each `name:` deliberate?

**Answer (2026-10-19): not deliberate, load it as C does.** `parse_profile_name` in `generate.c` makes a zeroed record and the other six
directives are optional, so a bare `name:` is a record with every number zero. This follows the Tier 0 "Missing or empty directives"
answer and is done in Stage 7b.
- G: `ChestTrapGrammar.g4`. Join a repeated `msg:` as C does, or refuse it?

**Answer (2026-10-19): join, as C does.** `parse_chest_trap_msg` and `parse_chest_trap_msg_death` in `obj-chest.c` use `string_append`, so a
repeated `msg:` or `msg-death:` line is appended with no separator. This follows the Tier 0 "Repeated directives" answer and is done in
Stage 7c. Requiring `code:` stays deliberate.
- G: `PlayerTimedGrammar.g4`. Support `effect-yx`, `effect-expr` and `effect-msg` on timed effects?

**Answer (2026-10-19): support all three.** In `player-timed.c` they fill the effect just opened by `on-begin-effect:` or
`on-end-effect:`: `effect-yx` sets y and x, `effect-expr` binds an expression to the dice (an error if no `effect-dice:` came first)
and `effect-msg` appends to the message. Done in Stage 8, reusing the fields `EffectParseRecord` already carries.
- G: `Random.g4`. Should a number above `INT_MAX` return `null` rather than throw? Decides Stage 2's `Random` item.

**Answer (2026-10-19): return `null`, as C refuses it.** `parse_random` in `parser.c` refuses a number above `INT_MAX` with
`PARSE_ERROR_NOT_RANDOM`, so the assemblers record a line-numbered error instead of the exception stopping start-up. The Stage 2
`Random` item is done this way, and the "by design" note on `RandomReader.parse` goes.
- G: `LoreGrammar.g4`. Follow C's optional fields, or the shapes `lore_save` writes? Parked in Stage 10.

**Answer (2026-10-19): follow C.** `init_parse_lore` in `mon-init.c` takes `blow:` with only the method required, a bare `flags:`, and
the `monster.txt` directives routed to an ignored handler. The grammar does the same when it is picked up in Stage 10. The cast
counts, `base:`, `drop-base:` and the `friends:` dice form are fixed either way.
- Anything decided as "keep the Java behaviour on purpose" goes in `docs/Accepted_Deviations.md` and drops out of the later stages.

## Stage 1: faults that reach the shipped data

These change what a loaded game holds today, even where nothing reads the value yet.

1. **A: `monster/MonsterAssembler.java` `spell_power`.** Never defaulted to the level; 595 of 624 races differ from C.
2. **A: `monster/MonsterAssembler.java` list order.** Drops, friends, mimics and shapes are reversed in C, drops are grouped, and 34 races differ. The order is observable in drop and mimic picks. Per the Stage 0 answer "always echo C's ordering", these lists end up in C's order.
3. **A: `egoitem/EgoItemAssembler.java` eligible kinds.** Run first to last, C runs last to first; 99 of 107 egos differ and `object_info_ego` takes the head. Same Stage 0 answer: the list is built in C's order.
4. **A: `itemobject/ItemObjectAssembler.java` null dice.** `ObjectUtils.objectPrep` already throws on `& Ration~ of Food` and on `Cure Light Wounds`. Start the five dice as `Random.Zero()`.
5. **A: `egoitem/EgoItemAssembler.java` null `combat:` and `time:` dice.** 71 of 107 egos; same fix as the item dice, so do the two together.
6. **A: `trap/TrapAssembler.java`.** Five shipped kinds have a `null` power, and the trap-placing port will meet it at once. The bad-effect `null` list is in the same entry; see Stage 2.
7. **Absent text is `null`, as the Stage 0 convention decides.**
   - A: `activation/ActivationAssembler.java` absent `msg:` and `desc:` (115 and 47 of 163 records).
   - A: `projection/ProjectionAssembler.java` lines left out are `""`.
   - A: `monster/MonsterAssembler.java` absent `plural:` (528 of 624 races).
   - A: `curse/CurseAssembler.java` absent `msg:` (three shipped curses would print a blank line).
8. **G: `ActivationsGrammar.g4` second `desc:` or `msg:`.** The only grammar fault that hits the shipped data: `TELE_LONG` and `RESIST_ALL` lose their first description line. Do it with item 7's activation entry.
9. **G: `ArtifactLexer.g4` blank after the last token.** The untouched upstream `artifact.txt` does not load; this repository's copy was trimmed by hand. Do it here, or fold it into Stage 6's whitespace work.

## Stage 2: crashes and silent loss on the error paths

None of these show on the shipped data. Each turns a bad modded line into an exception, a `null` that blows up later, or a quiet loss.

- A: `monster/MonsterAssembler.java` a die that overflows `int`, and G: `Random.g4` a number above `INT_MAX`. One fix in `Random.java` or the call sites; the Stage 0 answer decides which.
- A: `monster/MonsterAssembler.java` a `friends:` or `friends-base:` die that `Random.parseStr` rejects. C reads the `number` field with `parse_random` in `parser.c` before `parse_monster_friends` or `parse_monster_friends_base` runs, so a bad die is a parse error and no friend is stored. The Java records the soft error and sets `badFriend` (or `badFriendBase`), then still calls `numberDie.getDice()` with `numberDie` `null` in `friends.add(...)` (`MonsterAssembler.java:471`) and `friendsBase.add(...)` (`MonsterAssembler.java:529`). The load ends in a `NullPointerException` where the record should be skipped. Once the entry above makes an overflowing die return `null`, that is the input that reaches these lines. Fix by leaving out the `add` when `numberDie` is `null`, and do it with the entry above.
- A: `slay/SlayAssembler.java` a code the `Slay` constructor cannot split aborts the whole load.
- A: `curse/CurseAssembler.java` the 254-curse limit throws out of the reader and counts survivors.
- A: `itemobject/ItemObjectAssembler.java` a record with no `type:` leaves a `null` base that `ObjectRegistry.addObjectKind` dereferences.
- A: `trap/TrapAssembler.java` the bad-effect `null` list (the other half of the Stage 1 item).
- A: `projection/ProjectionAssembler.java` and `GrammarDriver.java` a misplaced element gives no projections and no error. Any assembler that throws `ParseCancellationException` is open to the same loss, so check them when this is fixed.
- A: `dungeonprofile/DungeonProfileAssembler.java` and `RoomAssembler.java` unknown names load with a `null` builder; a bad tunnel or streamer keeps the profile.
- A: `playerclass/ClassSpellBookAssembler.java` and `ClassMagicAssembler.java` a class whose books were all dropped loads as a non-caster, and a book keeps more spells than it declares.

## Stage 3: shared assembler helpers, fixed once and applied everywhere

Each group is one helper (or one policy) and then one pass over its users.

### Message lookup (`message_lookup_by_name`)

- A: `blowmethod/BlowMethodAssembler.java` `msg:`.
- A: `summon/SummonAssembler.java` `msgt:`.
- A: `monsterspell/MonsterSpellAssembler.java` `msgt:`.
- A: `projection/ProjectionAssembler.java` `msgt:`, which also has the numeric path one constant late.
- Case-insensitive, with a numeric form, and no name `NONE`. Do the lookup helper first, then the four call sites.

### `NONE` and `MAX` sentinels accepted by `valueOf`

- A: `slay/SlayAssembler.java` and `brand/BrandAssembler.java` race flags.
- A: `egoitem/EgoItemAssembler.java` and `shape/ShapeAssembler.java` elements and modifiers.
- A: `objectproperty/ObjectPropertyAssembler.java` `code:`, which also has the missing-`code:` case.
- A: `body/BodyAssembler.java` slot types.
- A: `playerrace/PlayerRaceAssembler.java` flags and resists, plus its race index and missing-history parts.
- A: `playerproperty/PlayerPropertyAssembler.java` `code:MAX`, plus its element-record `code:` and `value:` parts.
- A: `realm/RealmAssembler.java` `stat:`, which is also case-sensitive and drops a realm with no `stat:`.
- A: `trap/TrapAssembler.java` `flags:MAX` and `save:MAX` are a smaller part of the trap entry.
- One lookup helper that starts past the sentinel would serve all of them.

### `grab_element_flag` (`IGNORE_` and `HATES_`)

- A: `itemobject/ItemObjectAssembler.java` (knows `IGNORE_`, not `HATES_`).
- A: `objectbase/ObjectBaseAssembler.java` (knows `HATES_`, not `IGNORE_`).
- A: `egoitem/EgoItemAssembler.java` and `artifact/ArtifactAssembler.java`.
- Write one equivalent of C's function and call it from all four.

### Booleans read from text (`!= 0`, not `equals("1")`)

- A: `summon/SummonAssembler.java` `uniques:`.
- A: `activation/ActivationAssembler.java` `aim:`.
- A: `objectproperty/ObjectPropertyAssembler.java` `bindui:` aux, and A: `playerproperty/PlayerPropertyAssembler.java` aux.
- A: `chesttrap/ChestTrapAssembler.java` `destroy:` and `magic:`.
- A: `playertimed/PlayerTimedAssembler.java` `exact`, which sits in its larger entry.
- A: `dungeonprofile/RoomAssembler.java` `pit:01`, in the dungeon-profile entry.
- G: `BlowMethodLexer.g4` `INTEGER` (`!text.equals("0")`, the mirror image).
- One helper that does what `strtol(..., 0) != 0` does.

### Numbers: range, sign and base

- A: `slay`, `brand`: no range check on the three numbers (assembler half; the lexer half is in Stage 6).
- A: `monster/MonsterAssembler.java` `drop:` bounds and a negative chance.
- A: `egoitem/EgoItemAssembler.java` and `artifact/ArtifactAssembler.java` `alloc:` figures against 0 to 255.
- A: `projection/ProjectionAssembler.java` values above 2147483647.
- A: `names/NamesAssembler.java` and `history/HistoryAssembler.java` base and sign.
- A: `flavour/FlavourAssembler.java` negative or hex index.
- A: `GameConstantsAssembler` negative values and overflow.
- A: `playertimed/PlayerTimedAssembler.java` `lower-bound:`.
- A: `roomprofile/RoomProfileAssembler.java` `type:`, `rating:` and `doors:` ranges.

### Colours that C reads as white

- A: `monsterspell/MonsterSpellLevelAssembler.java` lore colours (drops the level).
- A: `objectbase/ObjectBaseAssembler.java` (drops the record).
- A: `flavour/FlavourAssembler.java` colour names, in the flavour entry.
- The Stage 0 strict-or-lenient answer decides all three, and the grammar side in Stage 7f.

### Absent directives that C zeroes

- A: `monsterspell/MonsterSpellAssembler.java` absent `hit:`.
- A: `monsterspell/MonsterSpellLevelAssembler.java` no implicit level 0. Settle this before the spell-walking code is ported.
- A: `slay/SlayAssembler.java` missing numbers.
- A: `monster/MonsterAssembler.java` no `color:`.
- A: `artifact/ArtifactAssembler.java` no `base-object:`.
- A: `objectbase/ObjectBaseAssembler.java` and G: `ObjectBaseGrammar.g4` one missing default. Take this as a pair.

## Stage 4: list order (C's chains are reversed)

Only the observable cases were put in Stage 1. The Stage 0 answer "always echo C's ordering" applies to every list below as well, observable or not, so none of them is left as an accepted deviation. What is still open is how each list carries C's order: building it in that order, or a collection type that keeps it. That is decided per list when the file is open, and it supersedes the "sort at the point of use" approach from `brands-slays-order-at-point-of-use`.

- A: `monsterspell/MonsterSpellAssembler.java` and `monsterbase/MonsterBaseAssembler.java`. Matters in play only for a name used twice, where the lookup should take the last match. The list itself is in C's order regardless.
- A: `shape/ShapeAssembler.java` shapes and blows. The blow order shows only under a fixed roll.
- A: `summon/SummonAssembler.java` `base:` and `pit/PitAssembler.java` bases, colours and bans. Not observable, but they follow the same rule.
- Brands and slays follow the rule too, wherever a loop depends on their order (`copy_brands` and `copy_slays`). That reverses the 2026-10-17 decision to leave them as a `HashSet`.

## Stage 5: assembler leftovers, in any order

Local edits with no effect on the shipped data. Group by file when the file is open for something else.

- Soft error where C is silent: A: `summon` unresolved `fallback:`, `pit` unresolved `mon-ban:`, `monster` unknown `color-cycle:` group, `projection` unknown `code:` after the 25 elements.
- Record dropped where C keeps it: A: `egoitem` and `artifact` unknown `act:`, `objectproperty` `subtype:` on a non-flag. (`monster` unknown spell name in `message-*:` moved out: C rejects it too, and the Java now does. [**CLOSED** 26-10-20])
- Stricter or looser than C by a small margin: A: `monster` `friends-base:` chance above 100, `monster` group role case, `monster` directive-order effects, `artifact` `graphics:` on a non-`INSTA_ART` kind, `objectproperty` unknown `type-mult:` tval keeps the record.
- Glyphs outside the Basic Multilingual Plane: A: `monsterbase`, `monster`.
- Limits: A: `brand` no 254-brand limit, `visuals` caps (per the Stage 0 answer).
- Numbering and names: A: `egoitem` skipped record takes no `egoIndex`, `objectbase` synthetic `TV_NONE` named "none", `egoitem` two misspelled error messages.
- Larger files that differ in several small ways, one entry each: A: `vault`, `roomprofile`, `playertimed`, `chesttrap`, `flavour` (tval, glyph, `fixed:` sval parts), `history` (successors), `visuals` (cycles and flicker).
- On a second parse: A: `artifact` special-light activation and time go to the artifact. Low risk, but the birth re-run is the only place it shows.

## Stage 6: grammar fixes in shared files

One edit, many lexers. Regenerate once at the end of the stage.

1. **`imports/CommentsAndEol.g4`.** Leading whitespace, a last-line comment with no newline, and tabs before the newline.
   - G: "Leading whitespace on a line" is the entry; the same gap is repeated in the Realm, Flavour, Names, History, Quest, Vault, RoomProfile, DungeonProfile, GameConstants, ChestTrap, PlayerTimed, PlayerRace, PlayerProperty, Terrain, Trap, Shape and Visuals entries.
   - G: trailing blanks also belong here: `BrandLexer.g4` spaces after a flag, `ProjectionLexer.g4` a blank after `code:` or `msgt:`.
2. **`imports/EffectBlock.g4` and `EffectBlockLexer.g4`.** `effect-yx:` with `dice:`, a line before the first `effect:`, a second `dice:`, and `effect-msg:` accepted where C has none.
   - G: `MonsterSpell`, `ItemObject`, `Curse`, `Activations`, `Shape`, `Trap` and `PlayerClass` entries all describe this one block.
3. **`imports/DiceStrings.g4`.** G: `ProjectionLexer.g4` lower-case `m` and a leading blank.
4. **The `INTEGER` rule.** C reads `strtol`/`strtoul` in base 0, refuses a leading minus on unsigned fields, and ignores trailing text.
   - Decide whether the shared answer is a signed and an unsigned token, then apply it to: G: `Pain`, `BlowMethod`, `Pit`, `MonsterSpell` (`hit:`), `MonsterBase`, `Monster`, `Slay`, `Brand`, `Curse`, `EffectBlockLexer` (`aim:`, `level:`, `power:`), `ObjectProperty`, `EgoItems`, `Artifact`, `Projection`, `Shape`.
   - Pair with the Stage 3 numbers helper, since the assembler half of the same entries is there.

## Stage 7: grammar patterns that repeat across files

Each pattern is one recipe applied to many grammars. Do the recipe once on the simplest grammar, then repeat. Regenerate after each pattern.

### 7a. Fixed record order to unordered, repeatable directives

- G: `BlowMethodGrammar.g4`, `SummonGrammar.g4`, `MonsterBaseGrammar.g4`, `BrandGrammar.g4`, `ObjectBaseGrammar.g4`, `ProjectionGrammar.g4`, `ObjectPropertyGrammar.g4` (also needs `type:` optional), `PlayerPropertyGrammar.g4`, `TerrainFeatureGrammar.g4`, `QuestGrammar.g4`, `VaultGrammar.g4`, `RoomProfileGrammar.g4`, `ChestTrapGrammar.g4` (`code:` after `name:`), `PlayerTimedGrammar.g4`.
- Each needs its `ParseRecord` and assembler to cope with an absent field, so this is where the Stage 0 absent-value convention is applied for the second time.

### 7b. Name-only records and empty files

- Same edit each time: `+` becomes `*` on the record's directive loop and on the file's record loop.
- G: `Pit`, `MonsterSpell`, `Monster`, `Slay` (file), `Brand` (file), `ItemObject`, `Curse`, `Activations`, `ObjectProperty`, `EgoItems`, `Artifact`, `Realm`, `Shape`, `Flavour` (bare `kind:`), `History` (chart with no phrase), `Body`, `PlayerRace`, `PlayerClass` (also a `magic:` with no book and a book with no spell), `Trap`, `World`, `DungeonProfile`.
- Decide once whether the Java-only `record-count:` header stays. It touches G: `Projection` (upstream file fails without it), `ChestTrap`, `PlayerTimed` and the other files that carry it.
- Do this inside 7a for any grammar in both lists.

### 7c. Free text that appends in one language and replaces in the other

- Java replaces, C appends: G: `BlowEffectGrammar.g4` `desc:`, `CurseGrammar.g4` `msg:`, `ChestTrapGrammar.g4` `msg:` and `msg-death:`, `PlayerTimedGrammar.g4` text lines and repeated effects, `TerrainFeatureGrammar.g4` message lines.
- Java appends, C replaces: G: `ObjectPropertyGrammar.g4` `msg:` and `desc:`.
- Last wins in Java, C joins or keeps all: G: `ObjectPropertyGrammar.g4` `bindui:`, `PlayerPropertyGrammar.g4` split `desc:`, `HistoryGrammar.g4` second `phrase:`.
- Overwrites where C rejects: G: `SlayGrammar.g4` second `race-flag:` or `base:`.
- A later zero-power `curse:` erases the earlier: G: `ItemObjectGrammar.g4`, `ArtifactGrammar.g4`, and A: `egoitem/EgoItemAssembler.java` with `EgoItemsGrammar.g4`. Fix the three together.
- The open item at the end of `BlowEffectGrammar.g4`'s entry (search the other grammars for the same fault) is answered by this list; recheck it once the pass is done.

### 7d. Flag and value lists

- C splits on any run of spaces and bars with `strtok(s, " |")`, and an empty `?str` is legal.
- G: `Pit` (four lists), `MonsterBase`, `Monster` (including `spells:`), `ObjectBase`, `Curse` (three lists), `EgoItems`, `Artifact`, `Shape`, `PlayerRace`, `PlayerClass`, `Trap` (`flags:` and `save:`), `TerrainFeature`, `RoomProfile`.
- Empty `flags:` alone: G: `EgoItems`, `Artifact`, `Monster`, `Shape`.
- Settle the shared token shape on one lexer, then copy it. This pattern is the likeliest to bite a hand-edited file.

### 7e. `alloc:` spacing

- G: `ItemObjectLexer.g4`, `EgoItemsLexer.g4`, `ArtifactLexer.g4`. All three read `N to N` with optional blanks in C.

### 7f. Colours given by name or mistyped

- G: `PitLexer.g4`, `MonsterLexer.g4`, `VisualsLexer.g4`, `imports/AngbandDisplayCharacter.g4` (reaches `ItemObject`, `Trap` and `TerrainFeature` too).
- All turn on the Stage 0 strict-or-lenient answer, so the pass is short once it is made.

### 7g. Message names in `msgt:`

- G: `SummonLexer.g4` and `ProjectionLexer.g4`. The projection mode never pops after a bad value, so one lower-case `msgt:` costs the rest of the file; fix that with the case rule.

### 7h. Numeric tval and sval names

- G: `ObjectPropertyLexer.g4` `type-mult:`, `ArtifactLexer.g4` `base-object:`. `TValue.fromName(int)` already has the lookup.

### 7i. Free-text fields the lexer constrains too tightly

- G: `ActivationsLexer.g4` `name:`, `ObjectPropertyLexer.g4` adjectives, `MonsterGrammar.g4` message with a colon or with no text (`message-vis:SHRIEK` is a syntax error, where C stores `""`), `FlavourLexer.g4` colon in a description, `TrapLexer.g4` colon in a description, `RealmLexer.g4` second colon on `stat:`, `PlayerPropertyLexer.g4` `bindui:` symbols and tags, `WorldLexer.g4` level names, `GameConstantsLexer.g4` message and category names, `RoomProfileLexer.g4` `tval:`.

## Stage 8: grammar one-offs

Each is specific to one file and independent of the rest.

- G: `MonsterSpellGrammar.g4` lore and message lines split into extra levels.
- G: `MonsterLexer.g4` a bare number as the `friends:` or `friends-base:` count.
- G: `NamesGrammar.g4` a `word:` before the first `section:`, and repeated sections.
- G: `PlayerClassGrammar.g4` a second `magic:` replaces the first (the one that is silently accepted).
- G: `PlayerTimedGrammar.g4` effect directives (per the Stage 0 answer).
- G: `BlowMethodGrammar.g4` `name:` is trimmed.
- G: `Random.g4` the remaining differences (lower-case `m`, `$` variables, blanks, repeated `d` or `M`), once the `INT_MAX` decision is in.
- G: `TerrainFeatureGrammar.g4`, `GameConstantsGrammar.g4`, `WorldGrammar.g4`, `QuestGrammar.g4`, `VaultGrammar.g4`: anything left after Stages 6 and 7 (indent, base 0, trailing field, negative depth).

## Stage 9: comment and Javadoc sweep

All comment-only, so no regeneration is needed for the Javadoc ones, and the grammar comments can ride along with the last regeneration.

- G: C line numbers in `PainLexer.g4` and `PainGrammar.g4`, `SummonGrammar.g4` and `SummonLexer.g4`, `MonsterBaseGrammar.g4` and `MonsterBaseLexer.g4`, `SlayGrammar.g4` and `SlayLexer.g4`, `BrandGrammar.g4` and `BrandLexer.g4`, `ActivationsGrammar.g4` and `ActivationsLexer.g4`, `ProjectionGrammar.g4` and `ProjectionLexer.g4`, plus the Flavour, GameConstants and PlayerProperty headers.
- G: wrong or stale comments: `BrandGrammar.g4` "six of ten" (it is four), `ObjectBaseLexer.g4` header names handlers C does not have, `ActivationsGrammar.g4` says the index is removed, `ProjectionGrammar.g4` points at a missing note, `PlayerPropertyGrammar.g4` stale note 1.
- A: `blowmethod/BlowMethodAssembler.java` does not mention the reversed list.
- A: `pit/PitAssembler.java` `room:` comment, `monsterspell/` C line number, `activation/` names a function that does not exist, `objectproperty/` says it looks `bindui:` up in the registry, `roomprofile/RoomProfileAssembler.java` says "file order" for a reversed list.
- This is the one stage that is comment-only throughout, so it can be handed to Claude as a comment pass if you ask for it.

## Stage 10: parked

- G: `LoreGrammar.g4` and `LoreLexer.g4`. Nothing calls the reader and no parser is generated from the grammar. It stays parked until the lore data exists (see `monsterlore-parsing-deferred`), and the Stage 0 question is answered then.

## Where to start

- Make the Stage 0 absent-value decision, then take Stage 1 items 1 to 5.
