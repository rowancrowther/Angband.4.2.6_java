# Issues to deal with #

## To deal with ##

- [X] 26-10-10 Implement the lore structure that sits behind the lore file. C's `finish_parse_monster` (`mon-init.c`)
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
- To finalise this issue (Java side checked 26-10-11; the first two come before the rest):

- [X] `MonsterLore.java` cannot yet make a blank record. Its only constructor takes 19 arguments, it has no getters, and
       its only setters are `setPSkills` and `setThefts`. Add a way to build the zeroed record C's `mem_zalloc` gives:
       empty `Flag` sets for the race and spell flags, empty drop, friend, base-friend and mimic lists, and no base.
       The `monsterBase` argument has no counterpart in C's `monster_lore`, so decide what a blank record holds there.
- [X] `MonsterLore.java` has no home for the rest of C's `monster_lore`. Missing are `all_known`, `blow_known[]`,
       `armour_known`, `drop_known`, `sleep_known`, `spell_freq_known` and `innate_freq_known`. Its `blows` list and
       `timeBlowsSeen` map also stand in for C's `blows[]`, whose `times_seen` lives inside each `monster_blow`. Decide the
       shape, then size the blow storage to `MonsterRegistry.getMonsterBlowsMax()` when the record is built. That value
       is already set by `MonsterRegistry.setMonsterRaces`, which runs before the point where the records are attached.
- [X] `MonsterDataLoader.loadMonsters` attaches the blank records. Call `race.setLore` for every race, including
       `<player>`, because C's `l_list` has `r_max` entries and `get_lore` works on index 0 too. Put the loop in the
       same second pass as `resolveFriends` and `resolveShapes`, which mirrors where C allocates it.
- [X] `MonsterDataLoader.loadMonsters` also holds two unused locals, `blowsCount` and `pitCount`, under the comment
       "Calculate max blows and pit types". The blow maximum is now calculated in `MonsterRegistry.setMonsterRaces`, so
       remove them.
- [X] `MonsterRegistry.java` has a `private static List<MonsterLore> monsterLore` that nothing assigns or reads. The
       records live on the races, so remove it, or its Javadoc will keep saying the lore has no home.
- [X] Settle the `getLore` question in `MonsterRace.java`. After step 3 only the `@TestOnly` bare-race constructor can
       leave it null. Either keep it returning null for that case, or make it fail loudly, as C's assert does.
- [X]  Check the lore reader's hand-off. The `monsterLore` rule in `LoreGrammar.g4` ends with `race.setLore($lore)`,
       which replaces whatever record the race holds, whereas C's `lore_parser` fills `l_list[race->ridx]` in place.
       Once blanks exist, a lore entry that omits a field would drop the blank's value for it. Parked with the reader,
       but note it in `LoreReaderTest.java`'s class comment.
- [X]  Savefile side: no Java counterpart of `wr_monster_memory` or `rd_monster_memory` exists in `src/main` (searched
       26-10-11). This part therefore waits for the savefile chapter. Record the behaviours to carry over: write only
       races with `pkills` or `thefts` non-zero, reset every `max_num` to 100 (1 for uniques) before reading, restore
       both counts, and set `max_num` to 0 for a unique with a kill count.
- [X]  Tests in `src/test`. `PlayerBirthPlayerInitTest.java` and `MonsterRacePopulationAndLoreTest.java` hand-build their
       own lore, so neither would notice a missing allocation. Add a test that runs `loadMonsters` against the real
       `monster.txt` and checks every race has a non-null, zeroed record with blow storage of the maximum length. Add one
       that runs `PlayerBirth`'s race loop over those loaded races without throwing.
- [X]  Javadoc refresh once it works. `MonsterRace.java` (class block, the `lore` field, `setLore`, `getLore`) says that
        `loadMonsters` attaches nothing and that the reader is the only caller. The `LoreReaderTest.java` class comment
        says the same. Then re-run `/port` on `MonsterRace.java`, and tick this issue.

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
