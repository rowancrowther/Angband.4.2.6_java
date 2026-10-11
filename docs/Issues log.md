# Issues to deal with #

## To deal with ##

[ ] 26-10-11 `MonsterSpell.java` has 19 breath constants that C does not. `RSF_ACID` to `RSF_MANA` repeat the breaths
that `RSF_BR_ACID` to `RSF_BR_MANA` already hold, with identical types (`RST_BREATH`, `RST_INNATE`), and nothing in
the main code reads the short names: the monster and pit assemblers build the constant as `"RSF_" + code`, so
`BR_ACID` in a data file always reaches the `RSF_BR_` one. The enum has 112 constants where `list-mon-spells.h` has 93
plus `NONE` and `MAX`, so every ordinal after the block is 19 too high.

- Ordinals matter wherever a `Flag<MonsterSpell>` is written or compared by position, and for anything that walks the
  enum in C's order.
- Delete the 19 short-name constants. A whole-word grep of `src` (main and test) outside the enum on 26-10-11 found no
  use of them; repeat it just before deleting.
- After the deletion, add a table test that pins every constant's ordinal and types against `list-mon-spells.h`, as
  `MonsterRaceFlagTableTest` does for the race flags.
- Related and already fixed: `MonsterAssembler.java` classifies spells with `getTypes()`, which is correct for the
  breath types either way, so the frequency defaults are not affected by the duplicates.

[ ] 26-10-10 Implement the lore structure that sits behind the lore file. C's `finish_parse_monster` (`mon-init.c`)
allocates `l_list`, one zeroed `monster_lore` per race with its `blows` and `blow_known` arrays, and `get_lore`
(`mon-lore.c`) returns a record from it for every race. The Java has no such allocation: nothing in main code calls
`MonsterRace.setLore`, so `getLore()` returns null for every race loaded from `monster.txt`. Found by `/port` of
`MonsterRace.java` on 26-10-10.

- Give each race a blank `MonsterLore` when the races are registered (`MonsterDataLoader.loadMonsters`), sized to the
  longest blow list as C's `mon_blows_max` does. This does not need the lore file, and it comes first.
- `PlayerBirth.java` calls `getLore()` and then `setPSkills(0)` and `setThefts(0)` on the result for every race after
  the first, so it throws until the records exist.
- The lore file itself (`lore.txt`, `lore_parser` in `mon-init.c`, writer `lore_save` in `mon-lore.c`) stays parked
  behind `LoreReader`, as already decided; it fills the records this issue creates and is not part of it.
- The savefile side (`rd_monster_memory` in `load.c`, `wr_monster_memory` in `save.c`) restores only `pkills` and
  `thefts`, and also resets `max_num` and keeps dead uniques at zero. It needs the records too.
- Decide whether `getLore` should keep returning null for a bare race, since C's `get_lore` asserts the race is not
  null and never returns null itself.

[ ] 26-09-29 Fold `UIEntryBase` into `UIEntry`, as decided 26-09-19 (`docs/precis/260918.md`, section "The registry is
shared across both files"). C keeps one `entries[]` array, so `name:` and `template:` both search the same list.
`/port` of `UIRegistry.java` failed stage 1 on 26-09-29 because `UIRegistry.java` still holds the split.

- Remove `uiEntryBases`, `getUIEntryBases`, `setUIEntryBases` and `getUIEntryBase` from `UIRegistry.java`, and send
  `template:` lookups through `getUIEntry`. Drop the `UIEntryBase` tests in `UIRegistryTest.java` with them.
- `UIEntryBase`'s five fields are a strict subset of `UIEntry`'s, so nothing new is added to `UIEntry`. Base-derived
  entries take C's fresh-entry values for the unused fields (empty label, unset shortened slots, `defaultPriority` 0,
  `paramIndex` -1, categories at `prioritySet = false`), never null.
- Base entries' categories widen from `List<String>` to `List<UIEntryCategory>`, because `parse_entry_template` copies
  each template category's `priority` and `priority_set`, not just its name.
- `ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY` becomes load-bearing: OR'd in after the base file, masked off in template
  copies, and filtered on where the iterator's list is built.
- Files that mention `UIEntryBase` (grep of 26-09-29, match counts in brackets; generated ANTLR output under
  `antlr4/` left out because regenerating it covers it):
    - Main, holding the type or the split: `frontend/entries/UIEntryBase.java` [8], `frontend/entries/UIEntry.java` [6],
      `frontend/ui/globals/UIRegistry.java` [22], `frontend/ui/globals/UIDataLoader.java` [5].
    - Main, the `ui_entry_base.txt` pipeline: `frontend/ui/entrybase/UIEntryBaseGrammar.g4` [3],
      `frontend/ui/entrybase/reader/UIEntryBaseReader.java` [7],
      `frontend/ui/entrybase/assembler/UIEntryBaseAssembler.java` [10],
      `frontend/ui/entrybase/assembler/UIEntryBaseParseRecord.java` [1].
    - Main, consumers: `frontend/ui/entry/assembler/UIEntryAssembler.java` [7],
      `frontend/inputfromuser/UILoop.java` [2].
    - Tests aimed at the type or pipeline: `frontend/entries/UIEntryBaseTest.java` [9],
      `backend/parser/UIEntryBaseAssemblerTest.java` [10], `backend/parser/UIEntryBaseReaderTest.java` [12],
      `backend/parser/grammars/uientrybase/UIEntryBaseGrammarTest.java` [12],
      `frontend/ui/globals/UIRegistryTest.java` [8], `backend/parser/UIEntryAssemblerTest.java` [4],
      `backend/parser/UIEntryReaderTest.java` [3], `frontend/ui/globals/UIDataLoaderTest.java` [1].
    - Tests that probably only seed the registry with an empty base list (one match each):
      `middle/player/PlayerBirthDoCmdAcceptCharacterTest.java`, `middle/game/gameengine/GameEngineBootstrapTest.java`,
      `middle/game/globals/GameConstantsInitUIBindingTest.java`, and `backend/parser/` `PitReaderTest.java`,
      `MonsterReaderTest.java`, `QuestReaderTest.java`, `VaultReaderTest.java`; `RuneInitTest.java` has two.
    - Docs: `docs/functions/initialize_ui_entry_iterator.md` [4],
      `docs/GameConstants_UIEntryAssembler_stage1_followup.md`
      [1], `docs/no_low_javadoc.md` [1]. The precis files are history, so leave them.
- Once the split is gone, refresh the class and field Javadoc in `UIRegistry.java`, which currently describes both
  designs, then re-run `/port` on it.

## Dealt with ##

[X] 26-08-10-12-16 Moving `GameEventType` into channel is a bigger move than it looks. It's referenced across middle, so
it's a stage-0-style import churn on top of stage 1's new code. Worth doing as its own commit before the records, so a
compile failure tells you which of the two broke.

[X] 26-10-11 Store a pit's `innate-freq:` as `100 / pct`, as C does. `parse_pit_innate_freq` (`mon-init.c`) rejects a
percentage outside 1 to 100 and stores `100 / pct` in `pit_profile.freq_innate`, the same convention as a race's
`freq_innate`. `PitAssembler.java` parses the integer and passes it to `PitProfile` as written, with no range check and
no division. Found while fixing the race side of the same rule in `MonsterAssembler.java`.

[X] 26-10-11 Scale a race's `hearing:` by `max_sight / 20`. `parse_monster_hearing` (`mon-init.c`) stores
`parser_getint(p, "hearing") * z_info->max_sight / 20`, with a comment that it assumes `max_sight` is 20. The Java
`MonsterAssembler.java` parses the value and passes it to `MonsterRace` unscaled. The two agree only while
`player:max-sight` in `constants.txt` is 20, which it is in the shipped file.