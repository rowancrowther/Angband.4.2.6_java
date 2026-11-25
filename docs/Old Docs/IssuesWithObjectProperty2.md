# Issues with ObjectProperty2

14 November 2026 (261114). A numbered list of the differences between the Java object-property pipeline and C's `object_property.txt` parser. The pipeline is `ObjectProperty2Lexer.g4`, `ObjectProperty2Grammar.g4`, `ObjectProperty2ParseRecord.java` and `ObjectProperty2Assembler.java`. The C side is `parse_object_property_*` in `obj-init.c`, `parser_parse` in `parser.c`, and `bind_object_property_to_ui_entry_by_name` in `ui-entry.c`. Section A was re-checked and its tests re-run on 261114, after the fixes below. B to D come from the earlier read the same day and have not been re-checked since, except items 6 to 8, which were re-checked and closed on 261114, item 9, which was re-checked and closed on 261115, and item 10, which was re-checked and closed on 261116.

## What C does

- Each directive is cut at the first `:`. A `str` field takes the rest of the line, blanks included.
- `type:`, `code:`, `subtype:` and `id-type:` compare that text exactly against a fixed table.
- `adjective:`, `neg-adjective:`, `msg:`, `desc:` and `name:` store the text as written.
- `code:` is resolved at its own line, against the type current then, and stores an integer `index`.
- `type:` never touches `index`.
- The three code tables (`obj_mods`, `obj_flags`, `element_names` in `obj-init.c`):
  - `obj_mods` starts at STR. It has no NONE and no MAX.
  - `obj_flags` starts with a literal `"NONE"` and ends with `MAX`.
  - `element_names` starts at ACID. It has no NONE and no MAX.

## A. Retype after a `code:` (ObjectProperty2Assembler.java)

C indexes against those tables, so the C index of a Java enum constant depends on the table:
- `ObjectFlag`: the C index is the ordinal, because `OF_NONE` lines up with C's `"NONE"`.
- `ObjectModifier` and `ElementEnum`: the C index is the ordinal minus 1, because the Java enum has an `OM_NONE`/`ELEM_NONE` placeholder at 0 that C's table does not.

Status on 261114, after your edits, one test pass and one comment pass:
- Items 1 to 4 are fixed in `ObjectProperty2Assembler.java`.
- Item 5 is fixed in `ObjectProperty2AssemblerTest.java`.
- `ObjectProperty2AssemblerTest` runs 62 tests with none failing.

1. **FIXED.** The first `type:stat`/`type:mod`/element type in a record threw `ArrayIndexOutOfBoundsException` (index -1). Cause: a `prevCode - 1` read against a `prevCode` that starts at 0. `parsePrevCode` now reads `values()[prevCode + 1]` for modifiers and elements again.
2. **FIXED.** `parseCode` stored the Java ordinal and `parsePrevCode` read it as a C index. It now stores `ordinal() - 1` for modifiers and elements and `ordinal()` for flags, so `type:stat code:STR type:mod` keeps STR.
3. **FIXED.** The range check was off by one against the read. Now it refuses at `prevCode >= values().length - 1` for modifiers and elements, and at `prevCode >= values().length` for flags (which read `values()[prevCode]`). A raw `ArrayIndexOutOfBoundsException` is gone.
   - The refused cutoffs are flag index 17 and up retyped to `stat`/`mod`, flag index 26 and up retyped to an element type, and element index 17 and up retyped to `stat`/`mod`.
   - The last slot loads, holding `OM_MAX` or `ELEM_MAX`: index 16 into `obj_mods`, flag index 25 into the element types.
   - `../Accepted_Deviations.md` ("type: after code:") was corrected to these numbers on 261114. It said 18, 27 and 18.
4. **FIXED.** The `prevCode` field block and the `parsePrevCode` Javadoc in `ObjectProperty2Assembler.java` now describe `prevCode` as C's index (ordinal minus 1 for modifiers and elements, the ordinal for flags), with index 0 as STR, ACID and `NONE`, and the check cutoffs explained. Both stamps carry "(the index and ordinal description corrected 261114)".
5. **FIXED (tests).** `typeOnlyRecordHoldsIndexZeroUnderItsType`, `theCarriedCodeDoesNotLeakIntoTheNextRecord` and `crossTableRetypeKeepsTheIndexWhenItFits` now expect C's indexes (STR, ACID, INT, `OF_NONE`). `aRetypeLandingOnOmMaxLoads` now uses flag index 16 and checks that 17 is refused. `theEdgeOfEachRetypeTargetTable` is new: it pins the last slot loading and the next index refused for each of the three retype targets.

## B. Where the grammar refuses what C loads

6. <span style="color:#006400">**CLOSED**</span> **`adjective:` and `neg-adjective:` values.** Was: `ObjectProperty2Grammar.g4` took one `LCASE`, which is `[a-z0-9_]+`, where C's `str` takes anything. Now both directives push `FREE_TEXT_MODE` in `ObjectProperty2Lexer.g4` and take a `STRING`. Re-checked 261114 against the regenerated parser (`ObjectProperty2AdjectiveGrammarCheck.java`) and `parser.c`.
   - `adjective:fast moving`    C accepts, Java accepts
   - `adjective:Slow`    C accepts, Java accepts
   - `adjective:half-hearted`    C accepts, Java accepts
   - `adjective: foo`    C accepts and keeps the blank, Java keeps it
   - `adjective:fast`    C accepts, Java accepts
   - `adjective:foo␠`    C stores `"foo "`, Java stores `"foo "` (the adjective half of item 9)
   - `adjective:` and `neg-adjective:` with nothing after the colon    C refuses (`strtok` gives no token for a non-optional `str`, `PARSE_ERROR_MISSING_FIELD`), Java refuses (syntax error)
   - Pinned in `ObjectProperty2AssemblerTest` by `anAdjectiveIsStoredAsWritten` and `aRepeatedAdjectiveKeepsTheLast`. These test the assembler only: Gradle does not compile `../../src/main/gen`, so the grammar half is checked by the scratch file and not by the suite.
   - The shipped `object_property.txt` has only plain lower-case adjectives.
7. <span style="color:#006400">**CLOSED**</span> **A number as the `type-mult:` tval.** Was: `DELIM_INTEGER` was tried before the name token in `ObjectProperty2Lexer.g4`, so `5` lexed as an integer where the grammar expects the name. Now the tval field is lexed in `DELIMITER_INTRO` as `DELIM_STRING` only, and `DELIM_INTEGER` exists only after the colon, in `DELIMITER_POWER`. `parseTypeMult` sends an all-digit field through `ParseUtils.parseIntString` and `TValue.fromName(int)`. Re-checked 261114 against freshly regenerated `ObjectProperty2Lexer` and `ObjectProperty2Grammar`, running lexer, parser and assembler, and against `tval_find_idx` in `obj-tval.c` (`strtoul` base 10, only blanks after the number, below `TV_MAX` = 36).
   - `type-mult:5:2`, `0:2`, `35:2`, `05:2`, `+5:2`    C accepts, Java accepts
   - `type-mult: 5:2`, `5 :2`, `5  :2`    C accepts (blanks around the number), Java accepts
   - `type-mult:36:2`, `999:2`, `99999999999:2`, `4294967301:2`    C refuses, Java refuses
   - `type-mult:-5:2`    C refuses (wraps to a huge unsigned), Java refuses
   - `type-mult:0x5:2`, `5x:2`, `5 6:2`    C refuses, Java refuses
   - `type-mult:sword:2`, `dragon armor:2`    C accepts, Java accepts
   - `type-mult:5:`, `5`, `sword:`    C refuses (missing `int`), Java refuses at the grammar
8. <span style="color:#006400">**CLOSED**</span> **`bindui:` entry names.** Was: the lexer needed `[a-z0-9_]+`, then an optional `<UPPER>` tag, where C's `sym` is any text up to the colon (C then drops a name that matches no entry, see 11). Now `BINDUI_STRING` takes anything up to a `<` and `BINDUI_TAG` anything up to a `>`. Re-checked 261114 against freshly regenerated `ObjectProperty2Lexer` and `ObjectProperty2Grammar`, and against the real `object_property.txt` (79 records, 66 `bindui` lines, no errors). Four shapes and one rename remain and are accepted: see "A `bindui:` entry name with a stray `<` or `>`" in `../Accepted_Deviations.md`.
   - `bindui:Resist-UI:0`    C loads, Java loads
   - `bindui:resist<fire>:0`    C loads, Java loads
   - `bindui:resist<FIRE:0`    C loads, Java refuses (accepted)
   - `bindui:resist<FIRE>x:0`    C loads, Java refuses (accepted)
   - `bindui:resist_ui_compact_0<FIRE>:0`    C loads, Java loads (the shipped shape)
   - `bindui:a<B>c<D>:0` and `bindui:<:0`    C loads, Java refuses (accepted)
   - `bindui::x:0`    C loads as `x`, Java refuses (accepted)
   - `bindui:x<>:0`    C loads the name `x<>`, Java loads the name `x` (accepted)
9. <span style="color:#006400">**CLOSED**</span> **A trailing blank on a word value.** Was: the `EOL` rule swallowed blanks before the newline, so `type:` and `code:` took a trailing blank that C keeps (`file_getl` does not strip, and a `str` field keeps the rest of the line). Now `type:` and `code:` push `LCASE_MODE` and `UCASE_MODE` in `ObjectProperty2Lexer.g4`, which end only on a bare `NEWLINE`, so a blank or tab after the value is a lexer error. `BINDUI_NL` is `NL`, and `NL` in `CommentsAndEol.g4` gained a "blanks then end of file" alternative, so a blank after a figure is ignored even on a last line with no final newline. Re-checked 261115 against a freshly regenerated `ObjectProperty2Lexer` and `ObjectProperty2Grammar`, running `ObjectPropertyReader`, and against the field types in `init_parse_object_property` in `obj-init.c` and `parser_parse` in `parser.c`.
   - `type:stat␠` and `code:STR␠`    C refuses (`"stat "` is not `"stat"`), Java refuses (also with a tab, and on a last line with no final newline)
   - `adjective:foo␠`    C stores `"foo "`, Java stores `"foo "` (closed with item 6)
   - `name:`, `msg:`, `desc:`, `neg-adjective:` with a trailing blank    C keeps it, Java keeps it (`STRING` runs to the end of the line)
   - `subtype:` and `id-type:` with a trailing blank    C keeps it, so the word matches no table entry and C refuses; Java keeps it and the assembler refuses (`an unknown subtype value: sustain `)
   - `power:`, `mult:`, `type-mult:` and `bindui:` with a trailing blank after the figure    C accepts (`strtol` ignores it), Java accepts, on a middle line and on a last line with no final newline
   - The refusal of `type:` and `code:` is a lexer error, so it surfaces as a `ParseCancellationException` with no line number, where the assembler's refusals are `DataFileParseException`s that name the line. Both end the load.
   - Pinned in `ObjectPropertyReaderTest` by `aTrailingBlankAfterTypeOrCodeIsRefusedAsInC`, `aTrailingBlankAfterTypeOrCodeOnTheLastLineIsRefusedAsInC`, `aTypeOrCodeWithNoBlankStillLoads`, `aTrailingBlankAfterAFigureIsIgnoredAsInC`, `aTrailingBlankInATextValueIsKeptAsInC` and `aTrailingBlankAfterSubtypeOrIdTypeEndsTheLoad`.
   - The shipped `object_property.txt` has no trailing blank on any directive line. The suite was green on the regenerated parser on 261115.
   - The `NL` change is in the shared `CommentsAndEol.g4`, so every lexer that imports it now accepts blanks that run to the end of a file. The suite is green, but no other reader has a test for it.

## C. `bindui:` handling (ObjectProperty2Assembler.java)

10. <span style="color:#006400">**CLOSED**</span> **The binding is not frozen at the line.** Was: `UIBinding(name, value, aux)` held no type or index, so a later `type:` or `code:` changed what it bound. Now `UIBinding` is `UIBinding(entry, value, aux, code, type)` and `parseBindui` in `ObjectProperty2Assembler.java` passes `property.getPayload()` and `property.getType()` as they are at the line. Both are replaced, not changed in place, so a later line cannot alter them. Re-checked 261116 against `parse_object_property_bindui` in `obj-init.c`, which calls `bind_object_property_to_ui_entry_by_name` at once with `prop->type` and `prop->index`.
    - C calls `bind_object_property_to_ui_entry_by_name` straight away with the `type` and `index` current at that line.
    - `type:flag`, `code:SUST_STR`, `bindui:x:0:0`, `code:FREE_ACT`    C binds SUST_STR, Java binds SUST_STR
    - `type:mod`, `bindui:x:0:0`, `code:SPEED`    C binds index 0 (STR), Java binds `OM_STR`
    - `type:flag`, `bindui:x:0:0`, `type:mod`    C binds with type flag, Java binds with type flag
    - Pinned in `ObjectProperty2AssemblerTest` by `aBindingKeepsTheCodeCurrentAtItsLine`, `aBindingBeforeAnyCodeBindsIndexZero` and `aBindingKeepsTheTypeCurrentAtItsLine`. The full suite was green on 261116.
    - `ObjectPropertyAssembler` (the older one) passes `null` for both new fields, and nothing reads them yet.
    - The real file puts `bindui:` after `code:`, so only a modded file reached this.
11. <span style="color:#006400">**CLOSED**</span> **An unknown entry name.** C's `ui_entry_search` fails, `bind_object_property_to_ui_entry_by_name` returns 1, and `parse_object_property_bindui` discards the return with `(void)` and returns `PARSE_ERROR_NONE`, so the bind is dropped and the file keeps loading. Was: Java kept the binding, because `parseBindui` made no lookup. Now `parseBindui` in `ObjectProperty2Assembler.java` reads the aux and value figures, then asks `GameConstants.uiEntryExists` whether an entry has the name (`name<TAG>` as a whole) and returns without a binding if none does. The names are the `UIEntrySpec`s in `GameConstants.entries`, which `GameConstants.init` sets from the front end's `UIEntriesLoaded` message before `object_property.txt` is read, so the assembler names nothing in `frontend`. Re-checked 261116 against `parse_object_property_bindui` in `obj-init.c` and `ui_entry_search` in `ui-entry.c`.
    - `bindui:no_such_entry:0`    C drops the bind and loads, Java drops the bind and loads
    - `bindui:ui_a<STR>:0` where only `ui_a<STR>` is an entry, and `bindui:ui_a<DEX>:0`    C binds the first and drops the second, Java the same (the whole name is compared)
    - `bindui:no_such_entry:abc`    C refuses (the parser reads the `aux` int before the handler runs), Java refuses (the figures are read before the lookup)
    - Pinned in `ObjectProperty2AssemblerTest` by `aBinduiNamingNoEntryIsDropped`, `theTagIsPartOfTheNameLookedUp` and `aBadNumberIsRefusedWhateverTheEntry`. The test classes that read `bindui:` lines set `GameConstants.entries` through `UIRegistryFixture`. The full suite was green on 261116.
    - `GameConstants.entries` unset is an `IllegalStateException` in Java, where C's `entries[]` is simply empty. A programming error, not a data error.
    - `ObjectPropertyAssembler` (the older one) does not check the name, and `player_property.txt` bindings are not checked at parse time; `GameConstants.init` drops an unknown name for the second.

## D. `code:` prefixes

12. <span style="color:#006400">**CLOSED**</span> **Prefixed names resolve in Java.** `ObjectModifier.lookup`, `ObjectFlag.lookup` and `ElementEnum.lookup` add `OM_`, `OF_` or `ELEM_` only when it is missing. C's tables hold the bare names. Accepted 2026-11-16: the three entries are in `../Accepted_Deviations.md` ("Object modifiers", "Object flags" and "Elements"). C never looks these names up after loading: the only name-to-index searches of `obj_mods`, `obj_flags` and `element_names` are parser handlers (checked 2026-11-16), so the wider lookup reaches game files only.
    - `type:mod code:OM_STR`    C refuses, Java accepts
    - `type:flag code:OF_SUST_STR`    C refuses, Java accepts
    - `type:flag code:OF_NONE`    C refuses, Java accepts (`flagNoneLoadsAsIndexZero` pins it)
    - `type:flag code:OF_MAX`    C refuses, Java accepts (`flagMaxLoadsAsTheLastIndex` pins it)
    - `type:resistance code:ELEM_FIRE`    C refuses, Java accepts
    - `type:flag code:NONE` and `code:MAX`    C accepts, Java accepts
    - `type:stat`, `type:mod` and the element types with `code:NONE` or `code:MAX`    C refuses, Java refuses

## E. The same in both

- A directive before `name:` is refused in both. A second `name:` starts a new record in both.
- `code:` or `bindui:` before any `type:` is refused in both.
- A repeated `msg:`, `desc:` or `code:` keeps the last one in both.
- A repeated `type-mult:` is last-wins in file order in both.
- `bindui` with no tag, or no `uival`, loads in both. The absent `uival` stays `null`.
- Numbers (a leading blank, trailing text, out of int range) are covered by the existing accepted deviations and are not repeated here.
