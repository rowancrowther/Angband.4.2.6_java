# Issues to deal with #

## To deal with ##

[ ] 26-09-29 (rewritten 26-10-11) Finish folding `UIEntryBase` into `UIEntry`, as decided 26-09-19 (`docs/precis/260918.md`,
section "The registry is shared across both files"). C keeps one `entries[]` array, so `name:` and `template:` both
search the same list. `/port` of `UIRegistry.java` failed stage 1 on 26-09-29 because it still holds the split.

The fold is half done. The write side and the flag are in place; the read side and the type are not.

**Already in place (checked 26-10-11)**

- `UIEntryBaseAssembler.java` builds a `UIEntry` for every base record (`assemble`), folds it into one list in
  `parseEachEntry`, ORs `ENTRY_FLAG_TEMPLATE_ONLY` in, and calls `UIRegistry.setUIEntries(entries)` itself.
- The base entries' categories are already `UIEntryCategory` (priority 0, `prioritySet = false`), and the create path
  already gives the unused fields C's fresh-entry values. Nothing needs adding to `UIEntry`.
- `UIEntryCode.java` filters `TEMPLATE_ONLY` where the iterator's list is built; `UIEntryAssembler.java` masks it off
  when it copies a template's flags.

**Still the old design**

- `UIEntryBaseAssembler.assemble` also builds a `UIEntryBase` per record and returns `List<UIEntryBase>`, which
  `UIEntryBaseReader` returns and `UIDataLoader.loadUIEntryBases` hands to `UIRegistry.setUIEntryBases`.
- `UIRegistry.java` still holds `uiEntryBases`, `getUIEntryBases` (no caller outside the class), `setUIEntryBases` and
  `getUIEntryBase`, and imports `UIEntryBase`.
- `UIEntryAssembler.java` resolves `template:` through `UIRegistry.getUIEntryBase`, declares `UIEntryBase template`, and
  reads `template.getCategories()` as strings in both the override and the create paths.
- `UIEntry.java` types its `template` field, constructor parameter, getter and setter as `UIEntryBase`.
- `UIEntryBase.java` still exists.

**To do**

- Send `template:` lookups through `getUIEntry` and make `UIEntry.template` a `UIEntry`. C's `parse_entry_template`
  accepts any entry already in `entries[]`, not only a `TEMPLATE_ONLY` one, and copies the template's
  `default_priority` and each category's `priority` and `priority_set`. `UIEntryAssembler.java` currently copies
  neither (it sets priority 0 or the record's own and passes `false`), so port those with the type change.
- Remove `uiEntryBases`, `getUIEntryBases`, `setUIEntryBases` and `getUIEntryBase` from `UIRegistry.java`, then
  `UIEntryBase.java`.
- Change what `UIEntryBaseAssembler` and `UIEntryBaseReader` return, so nothing builds a `UIEntryBase` any more, and
  decide whether `UIDataLoader.loadUIEntryBases` or the assembler owns the `setUIEntries` call. Today it is a side
  effect inside `assemble`, which `UIDataLoader` does not show.
- Reflection seeds the removed field by name, so these fail at run time (not compile time) once it goes:
  `ItemObjectPartCRound2Test`, `ItemObjectPartDRound2Test`, `ItemObjectPropertyPowerTest`, `ItemObjectPowerCursesTest`,
  `ItemObjectValueTest`, `RuneInitTest`, `UIEntryAssemblerTest` and `UIEntryReaderTest` (all `setStatic("uiEntryBases", ...)`
  or `ItemFixture.setStatic(UIRegistry.class, "uiEntryBases", ...)`). They need to seed `uiEntries` instead.
- Tests aimed at the type or the old registry go or change: `UIEntryBaseTest`, the `getUIEntryBase` and
  `setUIEntryBases` cases in `UIRegistryTest`, and the `List<UIEntryBase>` assertions in `UIEntryBaseAssemblerTest` and
  `UIEntryBaseReaderTest`. `UIEntryBaseGrammarTest` and `UIEntryBaseLexerTest` only name the grammar's own
  `uiEntryBases` rule output, so they are unaffected unless that rule is renamed.
- Fourteen tests call `UIDataLoader.loadUIEntryBases()` as setup (`PlayerBirthDoCmdAcceptCharacterTest`,
  `GameConstantsBadNamesTest`, `GameConstantsInitUIBindingTest`, `GameConstantsInitTablesTest`, `GameEngineLoadTest`,
  `GameEngineBootstrapTest`, `MonsterDataLoaderLoreTest`, `MonsterAssemblerFrequencyTest`, `MonsterAssemblerHearingTest`,
  `ElementSetTest` and the `Pit`, `Monster`, `Quest` and `Vault` reader tests), plus `UILoop.java`. They are
  affected only if that method is renamed or removed.
- Javadoc that still describes the split: `UIRegistry.java` (class-level, the logger, `uiEntryRenderers`, `uiEntries`,
  `getUIEntryRenderer`, `getUIEntry`), `UIDataLoader.java` (class and `loadUIEntryBases`), `UILoop.java` (the
  "Dependent on UIEntryBase" comment), `UIEntry.java`, and the three docs
  `docs/functions/initialize_ui_entry_iterator.md`, `docs/GameConstants_UIEntryAssembler_stage1_followup.md` and
  `docs/implementation/260926_change_in_architecture_from_cache_to_messages.md`. The precis files are history, so leave
  them.
- Once the split is gone, re-run `/port` on `UIRegistry.java`.

## Dealt with ##

- [X] 26-08-10-12-16 Moving `GameEventType` into channel is a bigger move than it looks. It's referenced across middle, so
it's a stage-0-style import churn on top of stage 1's new code. Worth doing as its own commit before the records, so a
compile failure tells you which of the two broke.

- [X] 26-10-11 Store a pit's `innate-freq:` as `100 / pct`, as C does. `parse_pit_innate_freq` (`mon-init.c`) rejects a
percentage outside 1 to 100 and stores `100 / pct` in `pit_profile.freq_innate`, the same convention as a race's
`freq_innate`. `PitAssembler.java` parses the integer and passes it to `PitProfile` as written, with no range check and
no division. Found while fixing the race side of the same rule in `MonsterAssembler.java`.

- [X] 26-10-11 Scale a race's `hearing:` by `max_sight / 20`. `parse_monster_hearing` (`mon-init.c`) stores
`parser_getint(p, "hearing") * z_info->max_sight / 20`, with a comment that it assumes `max_sight` is 20. The Java
`MonsterAssembler.java` parses the value and passes it to `MonsterRace` unscaled. The two agree only while
`player:max-sight` in `constants.txt` is 20, which it is in the shipped file.

- [X] 26-10-11 `MonsterSpell.java` has 19 breath constants that C does not. `RSF_ACID` to `RSF_MANA` repeat the breaths
that `RSF_BR_ACID` to `RSF_BR_MANA` already hold, with identical types (`RST_BREATH`, `RST_INNATE`), and nothing in
the main code reads the short names: the monster and pit assemblers build the constant as `"RSF_" + code`, so
`BR_ACID` in a data file always reaches the `RSF_BR_` one. The enum has 112 constants where `list-mon-spells.h` has 93
plus `NONE` and `MAX`, so every ordinal after the block is 19 too high.

- [X] 26-10-10 Implement the lore structure that sits behind the lore file. C's `finish_parse_monster` (`mon-init.c`)
  allocates `l_list`, one zeroed `monster_lore` per race with its `blows` and `blow_known` arrays, and `get_lore`
  (`mon-lore.c`) returns a record from it for every race. The Java has no such allocation: nothing in main code calls
  `MonsterRace.setLore`, so `getLore()` returns null for every race loaded from `monster.txt`. Found by `/port` of
  `MonsterRace.java` on 26-10-10.