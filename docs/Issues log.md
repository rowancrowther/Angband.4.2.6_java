# Issues to deal with #

## To deal with ##

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
