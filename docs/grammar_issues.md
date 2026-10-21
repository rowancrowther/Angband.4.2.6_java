# Grammar issues

Differences between the ANTLR grammars and the C data-file parsers they replace. All grammar paths are under
`src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/`.

## `desc:` appends in C, overwrites in `BlowEffectGrammar.g4`

Found 2026-10-17 during `/port backend/parser/BlowEffectReader.java`. Not fixed.

### What C does

- `parse_eff_desc` in `mon-init.c` runs `eff->desc = string_append(eff->desc, parser_getstr(p, "desc"))`.
- `string_append` in `z-virt.c` reallocates the first string and copies the second onto its end.
- When `eff->desc` is still `NULL` (the first `desc:` line of a record), it just makes a copy.
- There is no separator, so `desc:attack` then `desc: and more` gives `attack and more`. Any space has to be written
  into the data file.
- This is how C lets a long description run over several `desc:` lines.
- Every other `parse_eff_*` handler (`power`, `eval`, the three lore colours, `effect-type`, `resist`, `lash-type`)
  overwrites its field. `desc` is the only one that appends.

### What Java does

- `BlowEffectGrammar.g4:91-93`: the `desc` rule returns `$STRING.getText()` for a single `desc:` line.
- `BlowEffectGrammar.g4:194`: the `blowEffect` rule does `descInit = $desc.descStr`, so a second `desc:` line
  replaces the first.
- `BlowEffectAssembler.java:102` takes `record.desc()` as it finds it, so nothing downstream restores the lost text.
- `BlowEffectGrammar.g4:167-168` says a repeated directive keeps the last value "which is what the C original does
  too - its parse handlers simply overwrite the field". That is true for eight of the nine directives and wrong for
  `desc`.

### Impact

- None on the shipped data. `lib/gamedata/blow_effects.txt` has one `desc:` line per record, so every description
  loads whole.
- A modded or user-edited `blow_effects.txt` that splits a description over two lines would lose all but the last
  line in Java and show the joined text in C.
- No test covers a repeated `desc:`. `BlowEffectReaderTest.repeatedDirectiveKeepsTheLastValue` pins the overwrite for
  `power:` only.

### Where a fix would touch

- `BlowEffectGrammar.g4:194`: the `desc` alternative would concatenate onto `descInit` instead of assigning.
- `BlowEffectGrammar.g4:167-168`: the comment would name `desc:` as the exception.
- `BlowEffectParseRecord.java` and `BlowEffectAssembler.java` would need no change, since the record already carries
  one string.
- The grammar has to be regenerated afterwards (see the ANTLR generation note in the memory index).

### Other grammars to check

- Any other grammar whose rule for a free-text directive assigns where its C handler calls `string_append` has the
  same fault. Not yet searched for.

## `PainLexer.g4`: a negative `type:` serial is accepted, C rejects it

Found 2026-10-17 during `/port backend/parser/PainReader.java`. Not fixed.

### What C does

- `init_parse_pain` in `mon-init.c` registers the directive as `type uint index`.
- `parser_parse` in `parser.c` reads a `uint` with `strtoul`, then fails with `PARSE_ERROR_NOT_NUMBER` when no digits
  were read or when the token starts with `-`.
- A parser error from a data file is fatal at start-up, so `type:-1` stops the game loading.

### What Java does

- `PainLexer.g4:51-54`: `INTEGER` is `'-'? ('0'..'9')+`, so `-1` lexes as one token. Its comment calls it "a bare
  non-negative integer", which the rule does not match.
- `PainGrammar.g4:47-53`: the `type` rule takes any `INTEGER`.
- `PainAssembler.java:62` parses it with `Integer.parseInt`, which accepts the minus sign, so the record loads with a
  negative `pain_idx`.
- `PainReaderTest.negativeTypeSerialIsAcceptedWithoutValidation` pins that behaviour, so it would need to change with
  any fix.

### Impact

- None on the shipped data: `pain.txt` has `type:1` to `type:12`.
- A negative serial can never be matched by a `pain:` line, because `MonsterRegistry.lookupMonsterPain` returns `null`
  for an index below 0, so such a record is dead weight rather than a crash.
- `INTEGER` is probably shared in shape by other lexers that read C `uint` fields; each would have the same gap.
  Not yet searched for.

## Leading whitespace on a line: C skips it, the shared lexer rules do not

Found 2026-10-17 during `/port backend/parser/PainReader.java`. Not fixed.

### What C does

- `parser_parse` in `parser.c` skips every leading `isspace` character before it looks at the line.
- A line that is empty after that, or that then starts with `#`, is ignored.
- So `  type:1`, `<tab>type:1` and `  # note` are all accepted.

### What Java does

- `imports/CommentsAndEol.g4`: `COMMENT` must begin with `#` in column 0, and `EOL` is only spaces followed by a
  newline.
- A line that starts with a space or tab and carries a directive or a comment has no lexer rule, so it is a hard
  error and the whole file loads as nothing.
- `PainReaderTest.indentedDirectiveLineIsAHardErrorHereWhereCSkipsTheIndent` and
  `indentedCommentLineIsAHardErrorHereWhereCSkipsIt` pin this for `PainLexer`.
  `MonsterReaderTest.anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent` pins it for `MonsterLexer`.
  `ActivationReaderTest.anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent` pins it for `ActivationsLexer`,
  `ObjectPropertyReaderTest.anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent` pins it for `ObjectPropertyLexer`, and
  `ProjectionReaderTest.anIndentedDirectiveIsAHardErrorWhereCSkipsTheIndent` pins it for `ProjectionLexer`, and
  `HintReaderTest.leadingWhitespaceBeforeADirectiveIsAHardErrorWhereCSkipsIt` pins it for `HintLexer`, and
  `TrapReaderTest.anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent` pins it for `TrapLexer`.

### Impact

- None on the shipped data, which has no indented lines.
- Every grammar that imports `CommentsAndEol` has the same behaviour, so a hand-edited or modded data file with
  indentation loads in C and fails in Java.
- Tabs also fail in `EOL`, which only allows spaces before the newline.
- `COMMENT` also ends in a required `\n`, so a comment on the last line of a file with no newline after it is a hard error
  where C accepts it. `HintReaderTest` pins this too; a data line on the last line without a newline is fine.

### Where a fix would touch

- `imports/CommentsAndEol.g4` is the one place: the `COMMENT` rule would allow leading whitespace and a rule would skip
  leading whitespace in default mode. All importing lexers would need regenerating.

## `PainLexer.g4` and `PainGrammar.g4` cite C line numbers

Found 2026-10-17 during `/port backend/parser/PainReader.java`. Not fixed.

- `PainLexer.g4:21` cites `src/mon-init.c:516` and `src/mon-init.c:569`, and `PainGrammar.g4:22` cites
  `src/mon-init.c:516`. Both should name the function (`init_parse_pain`, `pain_parser`) without a line number.
- Not a behaviour difference; recorded here because the fix is in the grammar comments.

## `BlowMethodGrammar.g4`: a record is a fixed sequence with one `desc:`, C takes any directives in any order

Found 2026-10-17 during `/port backend/parser/BlowMethodReader.java`. Not fixed.

### What C does

- `init_parse_meth` in `mon-init.c` registers eight independent directives (`name`, `cut`, `stun`, `miss`, `phys`,
  `msg`, `act`, `desc`). `parser_parse` in `parser.c` dispatches each line on its own, with no ordering between them.
- Only `name:` has to come first, and only because the other handlers need the record `name:` creates
  (`parse_meth_act_msg` returns `PARSE_ERROR_MISSING_RECORD_HEADER`; the rest `assert`).
- `cut:`, `stun:`, `miss:`, `phys:` and `desc:` may be left out: `mem_zalloc` leaves the booleans false and `desc`
  `NULL`. `src/tests/parse/blowm.c` (`test_name0`) checks that a name-only record is accepted.
- `parse_meth_desc` calls `string_append`, so repeated `desc:` lines join, as for `parse_eff_desc`.

### What Java does

- `BlowMethodGrammar.g4:122-129`: the `blow` rule is `name cut stun miss phys (msg)? (act)* desc`, in that order.
- `cut:`, `stun:`, `miss:`, `phys:` and `desc:` are mandatory, so leaving one out is a syntax error and the whole
  file loads as nothing. `BlowMethodReaderTest.missingDescIsAHardError` and `outOfOrderDirectivesAreAHardError` pin
  that.
- A second `desc:` is also a syntax error, not an overwrite: `desc` closes the rule, so the next token must be
  `name:` or the end of the file. This is stricter than the `BlowEffectGrammar.g4` case above, which silently keeps the
  last line.
- `BlowMethodGrammar.g4:13-15` justifies the sequence by the shipped data, which is true of the shipped file only.

### Impact

- None on the shipped data: all 19 records carry every directive in this order, with one `desc:`.
- A modded `blow_methods.txt` that reorders directives, drops `phys:` or splits a description loads in C and loads as
  nothing in Java (fatal at start-up through `MonsterDataLoader.loadBlowMethods`).
- Same family as the `BlowEffectGrammar.g4` name-only-record difference in `docs/precis/261017.md`.

### Where a fix would touch

- `BlowMethodGrammar.g4`: `blow` would take its directives as an unordered, repeatable set, each with the C default
  when absent, and `desc` would concatenate.
- `BlowMethodParseRecord.java` and `BlowMethodAssembler.java` would need `cut`, `stun`, `miss`, `phys` and `desc` to
  be absent-capable, since the assembler calls `record.cut().equals("0")` on a value that is `""` today.

## `BlowMethodLexer.g4`: `INTEGER` is decimal-only and a leading zero reads as true

Found 2026-10-17 during `/port backend/parser/BlowMethodReader.java`. Not fixed.

### What C does

- `parse_meth_cut` and its siblings read a `uint`. `parser_parse` in `parser.c` uses `strtoul(tok, &z, 0)`, base 0,
  so `0x1`, octal `010` and `+1` are numbers, trailing text after the digits is ignored, and a leading `-` is
  rejected.
- The handler then stores `val ? true : false`.

### What Java does

- `BlowMethodLexer.g4:60`: `INTEGER` is `'-'? ('0'..'9')+`. `0x1` and `+1` are lexer errors, and `-1` is accepted
  (the lexer comment above the rule already says so).
- `BlowMethodAssembler.java:81-84` decides truth with `!text.equals("0")`. So `cut:00` and `cut:-0` are **true** in
  Java, where C reads `00` as zero (false) and rejects `-0`. `cut:010` is true in both, by luck of the test.

### Impact

- None on the shipped data, which uses `0` and `1` only.
- The `00` case is the only one that changes a loaded value rather than failing a load. `BlowMethodReaderTest.anyNonZeroValueMakesABooleanDirectiveTrue`
  pins `2` and `7`, not the leading-zero case.
- Same family as the `PainLexer.g4` negative-serial entry above; any lexer that tests the token text for `"0"`
  has the same hole.

## `BlowMethodGrammar.g4`: `name:` is trimmed, C keeps trailing blanks

Found 2026-10-17 during `/port backend/parser/BlowMethodReader.java`. Not fixed.

- `BlowMethodGrammar.g4:122` runs `$name.nameStr.trim()`. C's `parse_meth_name` stores the rest of the line as
  `parser_getstr` returns it, so `name:HIT ` is a method called `"HIT "` that `findmeth("HIT")` never matches.
- Java would match it. Harmless in practice (it forgives a typo C would punish), and no other field in this grammar is
  trimmed, so it is a lone inconsistency rather than a bug.

## `SummonGrammar.g4`: a record is a fixed sequence, C takes any directives in any order

Found 2026-10-17 during `/port backend/parser/SummonReader.java`. Not fixed.

### What C does

- `init_parse_summon` in `mon-summon.c` registers seven independent directives (`name`, `msgt`, `uniques`, `base`,
  `race-flag`, `fallback`, `desc`). `parser_parse` in `parser.c` dispatches each line on its own, with no ordering
  between them.
- Only `name:` has to come first, and only because the other handlers need the record `name:` creates (each of them
  `assert`s `s`).
- `msgt:`, `uniques:`, `race-flag:`, `fallback:` and `desc:` may be left out: `mem_zalloc` leaves the message type at 0,
  `unique_allowed` false, the race flag at 0 and the strings `NULL`.
- `base:` lines accumulate wherever they appear, since `parse_summon_base` pushes onto `s->bases` each time it runs.
- `race-flag:`, `fallback:` and `desc:` each overwrite their field (`parse_summon_desc` is a plain `string_make`, so
  unlike `parse_eff_desc` it does not append). A second one is accepted and the last wins.
- A file with no records at all is accepted: `finish_parse_summon` gives `summon_max` 1 and one blank slot.

### What Java does

- `SummonGrammar.g4:103-128`: the `summon` rule is `name msgt uniques (base)? (raceFlag)? (fallback)? desc`, in that
  order, and `file` is `recordCount (summon)+ EOF`.
- `msgt:`, `uniques:` and `desc:` are mandatory. A `race-flag:` before a `base:`, `base:` lines split by another
  directive, a second `race-flag:`, `fallback:` or `desc:`, and a file with no records are all syntax errors, so the
  whole file loads as nothing.
- `SummonReaderTest` pins each of these (`raceFlagBeforeBaseIsAHardErrorHereWhereCTakesAnyOrder`,
  `baseLinesSplitByAnotherDirectiveAreAHardErrorHereWhereCAccumulatesThem`,
  `repeatedRaceFlagIsAHardErrorHereWhereCKeepsTheLast`, `repeatedDescIsAHardErrorHereWhereCKeepsTheLast`,
  `missingMsgtIsAHardErrorHereWhereCLeavesTheGenericMessage`, `missingDescIsAHardErrorHereWhereCLeavesItNull`,
  `aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing`), so each would change with a fix.
- `SummonGrammar.g4:99-102` justifies the order by the shipped data (WRAITH is the only record with both `base:` and
  `race-flag:`), which is true of the shipped file only.

### Impact

- None on the shipped data: all 17 records carry `name`, `msgt`, `uniques`, `desc` in that order, with the optional
  ones in the grammar's order.
- A modded `summon.txt` that reorders directives, drops `desc:` or splits its `base:` lines loads in C and loads as
  nothing in Java (fatal at start-up through `MonsterDataLoader.loadSummons`).
- Same family as the `BlowMethodGrammar.g4` entry above.

### Where a fix would touch

- `SummonGrammar.g4`: `summon` would take its directives as an unordered, repeatable set, each with the C default when
  absent, and `file` would allow zero records.
- `SummonParseRecord.java` and `SummonAssembler.java` would need `msgType`, `uniques` and `desc` to be absent-capable;
  the assembler calls `MessageType.valueOf("MSG_" + rawMsgt)` on a value that is `""` today.

## `SummonLexer.g4`: `msgt:` and `race-flag:` values must be upper-case identifiers, `uniques:` is decimal-only

Found 2026-10-17 during `/port backend/parser/SummonReader.java`. Not fixed.

- C reads `msgt:` and `race-flag:` as `sym`, which `parser_parse` tokenizes on `:` and nothing else. The lexer's
  `FLAG` token is `('A'..'Z') ('A'..'Z' | '_' | '0'..'9')*`, so `msgt:sum_monster` and `msgt:12` are lexer errors and
  the whole file loads as nothing. C accepts both through `message_lookup_by_name` (case-insensitive, and a number
  below `MSG_MAX`). `SummonReaderTest.lowerCaseMessageTypeIsAHardErrorHereWhereCMatchesCaseInsensitively` and
  `numericMessageTypeIsAHardErrorHereWhereCAcceptsAMessageNumber` pin this.
- C reads `uniques:` as `int`, with `strtol` in base 0. `SummonLexer.g4:89-90` `INTEGER` is `'-'? ('0'..'9')+`, so
  `uniques:0x1` is a lexer error, and a trailing field (`uniques:1:x`, which C ignores) is too. The meaning of the
  values the lexer does accept is in `docs/Assembler_Issues.md`.
- `race-flag:` names are compared with `streq` in C (`lookup_flag`), so the upper-case-only token costs nothing there.
- None on the shipped data, which uses upper-case names and `0`/`1` only.

## `SummonGrammar.g4` and `SummonLexer.g4` cite a C line number

Found 2026-10-17 during `/port backend/parser/SummonReader.java`. Not fixed.

- `SummonGrammar.g4:22` and `SummonLexer.g4:21` cite `src/mon-summon.c:146`. Both should name the function
  (`init_parse_summon`) without a line number.
- Not a behaviour difference; recorded here because the fix is in the grammar comments.

## `PitGrammar.g4`: a record needs a directive after `name:` and the file needs a record, C accepts neither rule

Found 2026-10-17 during `/port backend/parser/PitReader.java`. Not fixed.

### What C does

- `init_parse_pit` in `mon-init.c` registers twelve independent directives. Only `name:` has to come first, and only
  because the other handlers need the record `name:` creates (each returns `PARSE_ERROR_MISSING_RECORD_HEADER`).
- `parse_pit_name` builds a complete record from the name alone, so a name-only pit is accepted and keeps zero for
  every number, empty flag sets and empty lists.
- `finish_parse_pit` copes with a chain of no records: `pit_max` becomes 1 and `pit_info` holds only the blank slot.
- Repeated `room:`, `alloc:`, `obj-rarity:` and `innate-freq:` lines overwrite, and repeated `flags-*`, `spell-*`,
  `mon-base:`, `mon-ban:` and `color:` lines add. The grammar does the same for all of them.

### What Java does

- `PitGrammar.g4:161`: the `)+` closing the directive alternatives in `pitRecord` needs at least one directive after
  `name:`, so a name-only record is a syntax error and the whole file loads as nothing.
- `PitGrammar.g4:170`: `file` is `recordCount (pitRecord ...)+ EOF`, so a file with the header and no records is a syntax
  error too.
- `PitReaderTest.aRecordWithNoDirectivesFailsClosed` and `aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing` pin both.

### Impact

- None on the shipped data: all 40 records carry directives.
- A modded `pit.txt` with a name-only pit, or an emptied one, loads in C and loads as nothing in Java (fatal at
  start-up through `MonsterDataLoader.loadPitProfiles`).
- Same family as the `BlowEffectGrammar.g4` name-only-record difference in `docs/precis/261017.md`.

### Where a fix would touch

- `PitGrammar.g4`: `(...)+` at line 161 would become `(...)*`, and `(pitRecord ...)+` at line 170 would become `*`.
  The grammar has to be regenerated afterwards.

## `PitLexer.g4`: flag and spell lists must be `CODE | CODE`, C splits on spaces and bars and skips padding

Found 2026-10-17 during `/port backend/parser/PitReader.java`. Not fixed.

### What C does

- `flags-req`, `flags-ban`, `spell-req` and `spell-ban` are registered as `?str`, so the value is optional.
  `parse_pit_flags_req` and its three siblings return without error when `parser_hasval` is false.
- Otherwise they split the value with `strtok(flags, " |")`. A space separates codes as well as a bar, and leading,
  trailing and doubled separators are skipped.
- So `flags-req:`, `flags-req: ANIMAL`, `flags-req:ANIMAL EVIL`, `flags-req:ANIMAL ` and `flags-req:ANIMAL |` are all
  accepted.

### What Java does

- `PitLexer.g4:102-109`: `FLAG` is one code and `FLAG_OR` is a bar with an optional space either side. A space on its
  own is not a token.
- `PitGrammar.g4:73-74` (and the three rules below it) need a `FLAG` straight after the colon, then `(FLAG_OR FLAG)*`.
- Probed 2026-10-17: the empty value is `missing FLAG`; a space after the colon, a space between codes and a trailing
  space are token recognition errors; a trailing bar is `missing FLAG`. Each loads nothing.
- `PitReaderTest.anEmptyFlagListIsAHardErrorHereWhereCAcceptsIt` and
  `spaceSeparatedOrPaddedFlagListsAreAHardErrorHereWhereCSplitsOnSpaceToo` pin this.

### Impact

- None on the shipped data, which writes every list as `CODE | CODE` with no padding.
- A hand-edited `pit.txt` with a space-separated list or a stray trailing space loads in C and fails in Java.
- The four directives are the same shape, so each has the gap.

### Where a fix would touch

- `PitLexer.g4`: `FLAG_OR` would become a run of spaces and bars, and `FLAG_EOL` would have to tolerate a trailing
  separator.
- `PitGrammar.g4`: the four list rules would allow an empty list.
- Lower-case codes are rejected in both (`grab_flag` uses `streq`), so that is not part of the gap.

## `PitLexer.g4`: `color:` takes one letter, C also takes a name and never rejects a colour

Found 2026-10-17 during `/port backend/parser/PitReader.java`. Not fixed.

### What C does

- `parse_pit_color` sends a value longer than one character to `color_text_to_attr` and a single character to
  `color_char_to_attr`, both in `z-color.c`.
- `color_text_to_attr` matches `color_table` names with `my_stricmp`, so `Red`, `red` and `RED` are all red.
- Both functions answer `COLOUR_WHITE` for something they cannot match, and `color_char_to_attr` answers
  `COLOUR_DARK` for a space. So `color:q` and `color:Nonsense` load as white.
- The `attr < 0` test that returns `PARSE_ERROR_INVALID_COLOR` cannot fire for either.

### What Java does

- `PitLexer.g4:49-51`: `color:` pushes `COLOUR_MODE`, whose only token is `COLOUR_CHAR` (`PitLexer.g4:149-151`), the
  shared 28-letter alphabet. A name or an unlisted letter is a lexer error and the file loads as nothing.
- `PitReaderTest.aColourNameIsAHardErrorHereWhereCAcceptsIt` and `anUnknownColourCodeIsALexerErrorNotASoftOne` pin this.
- `PitAssembler.java` has an unknown-colour arm that no file can reach.

### Impact

- None on the shipped data: all ten `color:` lines are a listed letter.
- A modded file with a colour name loads in C and loads as nothing in Java; one with a mistyped letter loads in C as
  white and loads as nothing in Java.

## `PitLexer.g4`: a leading minus is accepted on `room:`, `alloc:` and `obj-rarity:`, C rejects it

Found 2026-10-17 during `/port backend/parser/PitReader.java`. Not fixed.

- `init_parse_pit` registers those three as `uint`, and `parser_parse` in `parser.c` refuses a `uint` token that starts
  with `-` (`PARSE_ERROR_NOT_NUMBER`). It also reads with `strtoul` in base 0, so `0x1` and octal are numbers.
- `PitLexer.g4:88-90` (`PIT_INTEGER`) and `PitLexer.g4:137-139` (`ALLOC_INTEGER`) are `'-'? ('0'..'9')+`, so `room:-1`,
  `alloc:-2:-3` and `obj-rarity:-4` load: the assembler stores the numbers, and a negative room becomes
  `PIT_TYPE_NONE`. Hex and octal forms are lexer errors.
- `PitReaderTest.aNegativeUnsignedValueIsAcceptedHereWhereCRejectsIt` pins the Java result.
- `innate-freq:` is an `int` in C, so its minus sign is legal there; the assembler's 1 to 100 check rejects it as C's
  `parse_pit_innate_freq` does.
- None on the shipped data. Same family as the `PainLexer.g4` negative-serial entry above.

## `MonsterSpellGrammar.g4`: a record needs a directive after `name:` and the file needs a record, C accepts neither rule

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `parse_mon_spell_name` in `mon-init.c` builds a complete record from the name alone, and every other handler only
  needs that record to exist. A name-only spell is accepted; a file with no spells loads nothing.
- `MonsterSpellGrammar.g4:133`: the `)+` closing the alternatives in `monsterSpell` needs a directive after `name:`, so
  a name-only record is a syntax error and the whole file loads as nothing.
- `MonsterSpellGrammar.g4:140`: `file` is `recordCount (monsterSpell ...)+ EOF`, so a header with no records is a syntax
  error too.
- `MonsterSpellReaderTest.aRecordWithOnlyANameIsASyntaxErrorHereWhereCAcceptsIt` pins the first.
- None on the shipped data: all 91 records carry directives. Same family as the `PitGrammar.g4` entry above.

## `MonsterSpellGrammar.g4`: lore and message lines are split into separate levels when another directive interrupts them

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

### What C does

- `parse_mon_spell_name` makes one level (power 0) per spell, and `parse_mon_spell_power_cutoff` appends another.
- `lore:`, `lore-color-*`, `message-vis:`, `message-invis:`, `message-miss:` and `message-save:` all act on the last
  level in the chain, whatever directive came between them. So `lore:one`, `hit:100`, `lore: two` is one level with the
  lore `one two`.
- Two `power-cutoff:` lines in a row are fine: the first level is simply empty.

### What Java does

- `MonsterSpellGrammar.g4:81-103`: `powerCutoffBlock` is an optional `power-cutoff:` and then one or more text
  directives. `msgt:`, `hit:` and `effect:` are separate alternatives of `monsterSpell` at line 133, so any of them ends
  the block.
- A text directive after one of those starts a new block with no `power-cutoff:`, and the level assembler gives it power
  0. The spell ends up with two levels where C has one.
- `power-cutoff:` followed at once by `power-cutoff:` is a syntax error, because the block needs at least one text
  directive (`MonsterSpellGrammar.g4:102`).
- `MonsterSpellReaderTest.loreAfterAnEffectOpensASecondLevelHereWhereCAppendsToTheFirst`,
  `aHitOrMsgtLineBetweenTwoLoreLinesAlsoSplitsTheLevel` and `twoPowerCutoffsInARowAreASyntaxErrorHereWhereCAcceptsAnEmptyLevel`
  pin these.

### Impact

- None on the shipped data: every record keeps its text lines together, after its effects. The reader test checks
  all 91 records' level powers against the upstream file.
- A modded file that puts a `hit:` or `effect:` between two lore lines gets two levels in Java, so the later lines only
  apply from a different power than the author meant.

### Where a fix would touch

- `MonsterSpellGrammar.g4`: the text directives would need to be alternatives of `monsterSpell` itself, with
  `power-cutoff:` as its own alternative that starts a level, and the level list built in the actions. The grammar has
  to be regenerated afterwards.

## `MonsterSpellGrammar.g4` and `EffectBlock.g4`: an effect takes `effect-yx:` or `dice:` but not both, and its lines cannot come first

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `parse_mon_spell_effect_yx`, `parse_mon_spell_dice` and `parse_mon_spell_expr` in `mon-init.c` each act on the last
  effect, in any order, and return `PARSE_ERROR_NONE` without doing anything when there is no effect yet ("assume that
  this is human and not parser error"). `expr:` is likewise ignored when the effect has no dice.
- `imports/EffectBlock.g4:228-237`: `effectBlock` is `effect`, then `effectYX` or an optional `dice`, then an optional
  `effectMsg`. So `effect-yx:` and `dice:` on the same effect, in either order, are a syntax error, and a `dice:` or
  `effect-yx:` before the first `effect:` of a record is one too.
- `effect-msg:` is the other way round: `mon_spell_parser` registers no such directive, so C gives
  `PARSE_ERROR_UNDEFINED_DIRECTIVE`, and Java accepts it because the shared block does.
- `MonsterSpellReaderTest.effectYxAndDiceOnTheSameEffectAreASyntaxErrorHereWhereCTakesBoth`,
  `aDiceLineBeforeAnyEffectIsASyntaxErrorHereWhereCIgnoresIt` and `effectMsgIsAcceptedHereWhereCHasNoSuchDirective` pin
  the three.
- None on the shipped data: no spell uses `effect-yx:` or `effect-msg:`, and every `dice:` follows its `effect:`.
- The block is shared by every grammar that takes effects, so those grammars have the same shape; the C parsers differ
  from one another in which of the lines they register.

## `MonsterSpellGrammar.g4`: a negative `hit:` is accepted, C rejects it

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `hit` is a `uint` in `init_parse_mon_spell`, and `parser.c` rejects a `uint` token that starts with `-` with
  `PARSE_ERROR_NOT_NUMBER`.
- `imports/EffectBlockLexer.g4:137-138`: `INTEGER` is `'-'? ('0'..'9')+`, `MonsterSpellGrammar.g4:33` takes it for
  `hit:`, and `MonsterSpellAssembler.java:98` parses it with `Integer.parseInt`, so `hit:-5` loads with a hit of -5.
- `MonsterSpellReaderTest.aNegativeHitIsAcceptedHereWhereCRejectsIt` pins that.
- `strtoul` in C is called with base 0, so `hit:0x10` and `hit:010` are 16 and 8 there. `INTEGER` is decimal only: `010`
  reads as 10 and `0x10` is a lexer error. Same family as the `BlowMethodLexer.g4` `INTEGER` entry above.
- None on the shipped data, where every `hit:` is a plain number from 0 to 100.

## `MonsterBaseGrammar.g4`: a record is a fixed sequence, C takes any directives in any order

Found 2026-10-17 during `/port backend/parser/MonsterBaseReader.java`. Not fixed.

### What C does

- `init_parse_mon_base` registers `glyph`, `pain`, `flags` and `desc` as separate directives. Each handler only needs the
  record header that `parse_mon_base_name` made, so they can come in any order and any of them can be left out.
- A missing `glyph:` leaves `d_char` at 0, a missing `pain:` leaves `pain` NULL, and a missing `desc:` leaves `text`
  NULL.

### What Java does

- `MonsterBaseGrammar.g4`, rule `monsterBase`: `name`, `glyph`, `pain`, then `flags` any number of times, then `desc`
  (itself one or more `desc:` lines), in that order.
- A record with the directives in another order, or without `glyph:`, `pain:` or `desc:`, is a syntax error, and the
  whole file loads as nothing.
- `MonsterBaseReaderTest.directivesOutOfOrderAreAHardErrorHereWhereCTakesAnyOrder` and
  `aTemplateWithNoGlyphIsAHardErrorHereWhereCLeavesTheGlyphZero` pin it; `MonsterBaseReaderTest.aTemplateMissingItsDescIsAHardError`
  pins the `desc:` case.

### Impact

- None on the shipped data, where every one of the 56 records has `name:`, `glyph:`, `pain:`, optional `flags:` lines
  and `desc:` in that order.
- A hand-edited `monster_base.txt` that reorders lines loads in C and fails in Java.

### Where a fix would touch

- `MonsterBaseGrammar.g4`: `monsterBase` would become a loop over alternatives, as the other fixed-sequence entries
  above describe for `BlowMethodGrammar.g4` and `SummonGrammar.g4`.

## `MonsterBaseLexer.g4`: `flags:` cannot be empty, and the list must be `CODE | CODE`

Found 2026-10-17 during `/port backend/parser/MonsterBaseReader.java`. Not fixed.

- `init_parse_mon_base` registers `flags` as `?str`, so the value is optional, and `parse_mon_base_flags` returns
  without error when `parser_hasval` is false. Otherwise it splits with `strtok(flags, " |")`, so a space separates
  codes as well as a bar and padding is skipped.
- `MonsterBaseLexer.g4`, modes `FLAG_MODE`: `FLAG` is one code and `FLAG_OR` is a bar with an optional space either
  side. `MonsterBaseGrammar.g4`, rule `flags`, needs a `FLAG` straight after the colon.
- So `flags:` with nothing after it and `flags:ANIMAL EVIL` are both syntax errors here and both load in C.
  `MonsterBaseReaderTest.anEmptyFlagsValueIsAHardErrorHereWhereCAcceptsIt` and
  `spaceSeparatedFlagsAreAHardErrorHereWhereCSplitsOnSpaceToo` pin this.
- None on the shipped data, which writes every list as `CODE | CODE`. Same shape as the `PitLexer.g4` flag-list entry
  above, whose fix section applies here too.

## `MonsterBaseLexer.g4`: `pain:` is decimal-only, and a leading minus is accepted

Found 2026-10-17 during `/port backend/parser/MonsterBaseReader.java`. Not fixed.

- `init_parse_mon_base` registers `pain` as `uint`, which `parser_parse` in `parser.c` reads with `strtoul(tok, &z, 0)`
  (base 0, so `0x1` and octal `010` are numbers) and refuses when the token starts with `-`.
- `MonsterBaseLexer.g4`, rule `INTEGER`, is `'-'? ('0'..'9')+`, and the comment above it says "non-negative", which
  the rule is not. `pain:010` is 10 here and 8 in C; `pain:-1` loads as far as the assembler, which skips the record as
  an unknown pain set, where C reports a parser error. `0x1` is a lexer error.
- `MonsterBaseReaderTest.aLeadingZeroPainIsReadAsDecimalHereWhereCReadsOctal` and
  `aNegativePainIsASoftErrorHereWhereCRejectsItAsNotANumber` pin the Java result.
- None on the shipped data, which writes `1` to `12` with no leading zero. Same family as the `PainLexer.g4`
  negative-serial entry above.

## `MonsterBaseGrammar.g4` and `MonsterBaseLexer.g4` cite C line numbers

Found 2026-10-17 during `/port backend/parser/MonsterBaseReader.java`. Not fixed.

- `MonsterBaseGrammar.g4` (the header comment) cites `src/mon-init.c:1069`, and `MonsterBaseLexer.g4` (header comment)
  cites `src/mon-init.c:1069` and `src/mon-init.c:1105`. They should name `init_parse_mon_base()` and
  `mon_base_parser` without a line number.
- Not a behaviour difference; recorded here because the fix is in comments.

## `MonsterGrammar.g4`: a record needs a directive after `name:` and the file needs a record, C accepts neither rule

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_name` in `mon-init.c` only makes the record header, and the other 32 handlers are all optional, so
  `name:Bare` on its own is a monster in C. An empty file is accepted too.
- `MonsterGrammar.g4`, rule `monster`, is `name ( ... )+`, so a name with nothing after it is a syntax error, and rule
  `file` is `recordCount (monster)+ EOF`, so a file with the header and no record is one too. The whole file loads as
  nothing.
- `MonsterReaderTest.aRecordWithOnlyANameIsAHardErrorHereWhereCAcceptsIt` and
  `aFileOfOnlyTheHeaderIsAHardErrorHereWhereCAcceptsAnEmptyFile` pin it.
- None on the shipped data, where all 624 records carry directives. Same shape as the `PitGrammar.g4` entry above.

## `MonsterLexer.g4`: `plural:`, `flags:` and `flags-off:` cannot be empty, and flag and spell lists must be `CODE | CODE`

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `init_parse_monster` registers `plural`, `flags` and `flags-off` as `?str`. `parse_monster_plural` leaves
  `race->plural` NULL for an empty value, and `parse_monster_flags` and `parse_monster_flags_off` return without error
  when there is no value. Lists are split with `strtok(flags, " |")`, so a space separates codes as well as a bar. This
  holds for `spells:` too, which is `str` (the value is required) but split the same way.
- `MonsterLexer.g4`: `STRING` needs at least one character, `FLAG` is one code and `FLAG_OR` is a bar with an optional
  space either side. `MonsterGrammar.g4`, rules `flags`, `flagsOff` and `spells`, need a `FLAG` straight after the colon.
- So `plural:`, `flags:` and `flags-off:` with nothing after them, and `flags:MALE FEMALE`, are syntax errors here and
  load in C. `MonsterReaderTest.anEmptyPluralIsAHardErrorHereWhereCTakesItAsNoPlural`,
  `anEmptyFlagsValueIsAHardErrorHereWhereCAcceptsIt` and
  `spaceSeparatedFlagsAreAHardErrorHereWhereCSplitsOnSpaceToo` pin this.
- None on the shipped data, which writes every list as `CODE | CODE` and never leaves a value empty. Same shape as the
  `MonsterBaseLexer.g4` and `PitLexer.g4` flag-list entries above, whose fix sections apply here too.

## `MonsterLexer.g4`: `color:` takes one letter, C also takes a name

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_color` reads `color` as a `sym` and, when it is longer than one character, passes it to
  `color_text_to_attr`, so `color:white` is accepted. A one-character value goes through `color_char_to_attr`.
- `MonsterLexer.g4`, mode `COLOUR_MODE`, has `COLOUR_CHAR : COLOUR_CODE`, the 28 one-letter codes only. The `Colours`
  import already defines `COLOUR_NAME`, but this lexer does not use it. `color:white` is a lexer error and the file
  loads as nothing.
- `MonsterReaderTest.aColourNameIsAHardErrorHereWhereCAcceptsOne` pins it.
- None on the shipped data, where every `color:` is one letter. Same family as the `PitLexer.g4` `color:` entry above.

## `MonsterGrammar.g4`: `message-vis:`, `message-invis:` and `message-miss:` need a message with no colon in it

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `init_parse_monster` registers these as `sym spell ?str message`. The message is optional, a missing or blank one is
  stored as `""`, and as the last field it takes the rest of the line, colons included.
- `MonsterLexer.g4`, mode `DELIMITED`: `DELIMITED_STRING` stops at a colon, and the rules `messageVis`, `messageInvis`
  and `messageMiss` need `DELIMITED_STRING COLON DELIMITED_STRING`. `message-vis:SHRIEK` is a syntax error, and so is
  `message-vis:SHRIEK:It says: hello`, where the second colon starts a stray token.
- `MonsterReaderTest.aMessageWithNoTextIsAHardErrorHereWhereCTakesItAsEmpty` and
  `aMessageContainingAColonIsAHardErrorHereWhereCKeepsTheRestOfTheLine` pin this.
- None on the shipped data, where every message has text and none contains a colon.

## `MonsterLexer.g4`: a bare number as the `friends:` or `friends-base:` count is a syntax error

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- Both directives are `uint chance rand number sym name ?sym role`. `parse_random` in `parser.c` accepts `5` (a base
  and no dice), and the handler keeps only `number.dice` and `number.sides`.
- `MonsterLexer.g4`, mode `FRIENDS_MODE`: `FRIEND_INTEGER` is declared before `FRIEND_DICE`, so `5` lexes as
  `FRIEND_INTEGER` and rules `friends` and `friendsBase` expect `FRIEND_DICE`. `friends:100:5:Somebody` is a syntax
  error. `M4` is accepted, as `FRIEND_DICE` is the only rule that matches it.
- `MonsterReaderTest.aBareNumberFriendCountIsAHardErrorHereWhereCReadsItAsARandomValue` pins it.
- None on the shipped data, where every count has a `d`.

## `MonsterLexer.g4`: integers are decimal-only, and a leading minus is accepted on unsigned fields

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `int` fields (`speed`, `hit-points`, `light`, `hearing`, `smell`, `armor-class`, `sleepiness`, `depth`, `rarity`,
  `experience`, `innate-freq`, `spell-freq`) are read by `parser_parse` with `strtol(tok, &z, 0)`, base 0, so `010` is 8
  and `0x10` is 16. `uint` fields (`spell-power`, and the `chance`, `min` and `max` of `drop:` and `drop-base:`, and the
  chance of `friends:` and `friends-base:`) use `strtoul` and refuse a token that starts with `-`.
- `MonsterLexer.g4`: `MON_INTEGER`, `DROP_INTEGER` and `FRIEND_INTEGER` are all `'-'? ('0'..'9')+`. `speed:010` is 10
  here and 8 in C, `speed:0x10` is a lexer error, and `spell-power:-1` and a `drop:` chance of `-5` get as far as the
  assembler, which keeps both.
- `MonsterReaderTest.aLeadingZeroSpeedIsReadAsDecimalHereWhereCReadsOctal`, `aHexSpeedIsAHardErrorHereWhereCReadsItAsSixteen`,
  `aNegativeSpellPowerIsAcceptedHereWhereCRejectsAUint` and `aNegativeDropChanceIsAcceptedHereWhereCRejectsAUint` pin
  the Java result.
- None on the shipped data, which writes plain decimal numbers with no sign on an unsigned field. Same family as the
  `MonsterBaseLexer.g4` `pain:` entry above.

## `SlayGrammar.g4`: a second `race-flag:` or `base:` in one record overwrites, C rejects it

Found 2026-10-17 during `/port backend/parser/SlayReader.java`. Not fixed.

- `parse_slay_race_flag` and `parse_slay_base` in `obj-init.c` both return `PARSE_ERROR_INVALID_SLAY` when the slay
  already has a race flag or a base ("Flag or base, not both nor multiple race flags"), and the load stops.
- `SlayGrammar.g4`, rule `slay`: `raceFlag { raceFlagInit = ... }` and `base { baseInit = ... }` simply overwrite, so the
  last of two `race-flag:` lines wins. `SlayAssembler.java` only rejects the case where both strings are non-empty, so
  two `race-flag:` lines, or two `base:` lines, load as one slay with no error.
- The grammar comment says a repeated directive keeps the last value "as in C". That holds for `name:`, the three
  numbers and both verbs, and not for these two.
- `SlayReaderTest.secondRaceFlagInOneRecordKeepsTheLastHereWhereCRejects` and
  `secondBaseInOneRecordKeepsTheLastHereWhereCRejects` pin the Java result.
- None on the shipped data, where every record has exactly one `race-flag:` and no `base:`.

## `SlayLexer.g4`: `INTEGER` accepts a leading minus and decimal only, and the comment says the assembler checks the range

Found 2026-10-17 during `/port backend/parser/SlayReader.java`. Not fixed.

- `multiplier`, `o-multiplier` and `power` are registered as `uint`. `parser_parse` in `parser.c` reads a `uint` with
  `strtoul(tok, &z, 0)` and returns `PARSE_ERROR_NOT_NUMBER` when the token starts with `-`, and base 0 means `010` is 8
  and `0x10` is 16.
- `SlayLexer.g4`: `INTEGER : '-'? ('0'..'9')+`. `multiplier:-2` lexes, `SlayAssembler.java` keeps it, and the slay loads
  with a multiplier of -2. `multiplier:010` is 10 here and 8 in C, and `multiplier:0x10` is a lexer error.
- The comment above `INTEGER` says "range is validated in the assembler". `SlayAssembler.java` only calls
  `Integer.parseInt`, so nothing checks the sign.
- `SlayReaderTest.negativeMultiplierLoadsHereWhereCParserGetUintRejectsIt` pins the Java result.
- None on the shipped data, which writes plain unsigned decimals. Same family as the `MonsterLexer.g4` integers entry
  above.

## `SlayGrammar.g4`: a file with no slay records is a syntax error

Found 2026-10-17 during `/port backend/parser/SlayReader.java`. Not fixed.

- `run_parse_slay` and `finish_parse_slay` accept a `slay.txt` with no records: the chain is empty and `slay_max` ends
  at 1.
- `SlayGrammar.g4`, rule `file`: `(slay { ... })+` needs at least one, so a header-only file is a hard error with no
  items.
- `SlayReaderTest.headerOnlyFileIsAHardErrorHereWhereCAcceptsAnEmptyFile` pins it.
- None in practice, since the game always ships a populated `slay.txt`.

## `SlayGrammar.g4` and `SlayLexer.g4`: comments cite C line numbers

Found 2026-10-17 during `/port backend/parser/SlayReader.java`. Not fixed.

- `SlayGrammar.g4` cites `init_parse_slay() (src/obj-init.c:786)`, and `SlayLexer.g4` cites `init_parse_slay()
  (src/obj-init.c:786-798)` and `slay_parser (src/obj-init.c:851)`.
- The line numbers drift whenever a function in `obj-init.c` is stamped as ported. Name the file and function only.

## `BrandGrammar.g4`: directives must come in one fixed order, and a missing or repeated one is a syntax error that costs the whole file

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- `init_parse_brand` in `obj-init.c` registers each directive on its own, so `parser_parse` runs them in whatever order
  the file gives, a left-out directive leaves the zero-filled field (`power` 0, `resist_flag` 0, `name` and `verb`
  `NULL`), and a repeated one overwrites (`parse_brand_name` frees the old string first).
- `BrandGrammar.g4`, rule `brand`: `code name verb multiplier o-multiplier power resist-flag (vuln-flag)?` in exactly
  that order. `verb:` before `name:`, a missing `power:` or `resist-flag:`, a second `name:` and a `vuln-flag:` ahead of
  `resist-flag:` are all syntax errors.
- A syntax error is a hard error, so one malformed record empties the whole file, where C stops at the line that failed.
- `BrandReaderTest.directivesOutOfOrderAreAHardErrorHereWhereCTakesAnyOrder`, `missingPowerIsAHardErrorHereWhereCLeavesItZero`,
  `missingResistFlagIsAHardErrorHereWhereCLeavesFlagZero`, `oneBadRecordSyntaxCostsTheWholeFileHereWhereCStopsAtThatRecord`
  and `repeatedNameIsAHardErrorHereWhereCOverwrites` pin the Java result.
- None on the shipped data, where every record has the same eight directives in this order (`vuln-flag:` only on four).
  Same family as the `SlayGrammar.g4` entry above.

## `BrandGrammar.g4`: a file with no brand records is a syntax error

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- `run_parse_brand` and `finish_parse_brand` accept a `brand.txt` with no records: the chain is empty and `brand_max`
  ends at 1.
- `BrandGrammar.g4`, rule `file`: `(brand { ... })+` needs at least one, so a header-only file is a hard error with no
  items.
- `BrandReaderTest.headerOnlyFileIsAHardErrorHereWhereCAcceptsAnEmptyFile` pins it.
- None in practice, since the game always ships a populated `brand.txt`.

## `BrandLexer.g4`: `INTEGER` accepts a leading minus and decimal only, and the comment says the assembler checks the range

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- `multiplier`, `o-multiplier` and `power` are registered as `uint`, so `parser_parse` in `parser.c` reads them with
  `strtoul(tok, &z, 0)`, refuses a token that starts with `-`, and reads `010` as 8 and `0x10` as 16.
- `BrandLexer.g4`: `INTEGER : '-'? ('0'..'9')+`. `multiplier:-2` lexes and loads as -2, `multiplier:010` is 10, and
  `multiplier:0x10` is a lexer error. The comment above `INTEGER` says "range is validated in the assembler", and
  `BrandAssembler.java` only calls `Integer.parseInt`.
- `BrandReaderTest.negativeMultiplierLoadsHereWhereCParserGetUintRejectsIt`, `leadingZeroNumberIsDecimalHereWhereCReadsOctal`
  and `hexNumberIsAHardErrorHereWhereCReadsItAsSixteen` pin the Java result.
- None on the shipped data, which writes plain unsigned decimals. Same family as the `SlayLexer.g4` entry above.

## `BrandLexer.g4`: spaces after a flag are swallowed, C keeps them and fails the lookup

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- `parse_brand_resist_flag` and `parse_brand_vuln_flag` take a `sym`, which `parser_parse` splits on `:` only, so
  `resist-flag:IM_FIRE ` reaches `lookup_flag` as `"IM_FIRE "`, which matches nothing, and the load stops with
  `PARSE_ERROR_INVALID_FLAG`.
- `BrandLexer.g4`: `FLAG` pops its mode, and the default-mode `EOL` rule (`' '* '\r'? '\n'`) then eats the trailing
  spaces, so the same line loads.
- `BrandReaderTest.spaceAfterAFlagIsIgnoredHereWhereCLooksUpTheTextWithTheSpace` pins it. Lenient rather than harmful.
- None on the shipped data.

## `BrandGrammar.g4` and `BrandLexer.g4`: comments cite a C line number, already stale

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- `BrandGrammar.g4` cites `init_parse_brand() (src/obj-init.c:959)` and `BrandLexer.g4` cites the same line for the
  directive table. `init_parse_brand` is no longer at 959, and the number moves again whenever a function in
  `obj-init.c` is stamped as ported. Name the file and function only.

## `BrandGrammar.g4`: the comment on rule `brand` says six of ten brands carry a `vuln-flag:`, it is four

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- The comment above rule `brand` reads "only six of ten shipped brands carry a vuln-flag". `brand.txt` has four
  `vuln-flag:` lines (FIRE_3, COLD_3, FIRE_2, COLD_2), in the shipped copy and in the upstream file.
- `BrandReaderTest.realFileHoldsEveryFieldOfEveryRecordInCsLastFirstOrder` pins which four.

## `ItemObjectGrammar.g4`: a record with only a `name:` line is a syntax error, C accepts it

Found 2026-10-17 during `/port backend/parser/ItemObjectReader.java`. Not fixed.

- `parse_object_name` in `obj-init.c` starts the record and nothing else is required, so a `name:` line on its own is
  a kind with every field 0.
- `ItemObjectGrammar.g4:249`: the directives after `name:` are a `( ... )+` loop, so at least one must follow.
- `ItemObjectReaderTest.aRecordWithOnlyANameLineIsASyntaxErrorHereWhereCAcceptsIt` pins it.
- None on the shipped data, where every record has a `type:` at least. A modded `object.txt` with a bare name loads in
  C and loads as nothing in Java.
- Same family as the `BlowEffectGrammar.g4` name-only-record difference in `docs/precis/261017.md`.

## `ItemObjectGrammar.g4`: a later `curse:` for the same name replaces an earlier one, C only ever raises it

Found 2026-10-17 during `/port backend/parser/ItemObjectReader.java`. Not fixed.

- `parse_object_curse` in `obj-init.c` writes `k->curses[i] = power` only when `power > 0`, and nothing clears a slot.
  So `curse:teleportation:50` then `curse:teleportation:0` leaves 50, and `0` then `50` leaves 50.
- `ItemObjectGrammar.g4:245`: `curseInit.put(name, power)` keeps the last line per name, so the `0` replaces the `50`, and
  `ItemObjectAssembler.java` then drops it as a zero-power curse. The reverse order does keep the 50, as C does.
- Two lines for the same curse with different non-zero powers give the last in both, so only a trailing zero differs.
- `ItemObjectReaderTest.aLaterZeroPowerCurseDropsAnEarlierOneHereWhereCKeepsIt` and
  `aZeroPowerCurseBeforeANonZeroOneLeavesTheNonZeroOne` pin the two orders.
- None on the shipped data: the only `curse:` lines are the two `curse:teleportation:100`.

## `ItemObjectLexer.g4`: the spaces around `to` in `alloc:` are optional, C requires them

Found 2026-10-17 during `/port backend/parser/ItemObjectReader.java`. Not fixed.

- `parse_object_alloc` hands the range text to `grab_int_range` with the separator `to`. That function requires white
  space after the first number and after the separator, so `alloc:5:1to100` and `alloc:5:1 to100` are
  `PARSE_ERROR_INVALID_ALLOCATION`.
- `ItemObjectLexer.g4:133-135`: `ALLOC_TO` is `' '? 'to' ' '?`, so both load.
- `ItemObjectReaderTest.allocWithNoSpacesAroundToIsAcceptedHereWhereCRejectsIt` pins it. `allocWithoutToIsAHardError`
  pins that a missing `to` is refused in both.
- Lenient rather than harmful; none on the shipped data, which always writes `N to M`.

## `imports/AngbandDisplayCharacter.g4`: an unknown `graphics:` colour is a lexer error, C falls back to white

Found 2026-10-17 during `/port backend/parser/ItemObjectReader.java`. Not fixed.

- `parse_object_graphics` in `obj-init.c` calls `color_text_to_attr` for a colour longer than one character and
  `color_char_to_attr` for one character. Both answer white when nothing matches, so `graphics:|:q` is a white `|`.
- `imports/AngbandDisplayCharacter.g4:103`: `COLOUR_VALUES` is a fixed set of colour codes and names. `q` and `9` match
  nothing, so the lexer reports `token recognition error` and the whole file loads as nothing, not just the record.
  `ItemObjectAssembler.java` has an "invalid colour" soft error, which can now only fire for a code the lexer accepts
  and `ColourEnum.fromCode` does not.
- `ItemObjectReaderTest.anUnknownColourIsAHardErrorHereWhereCFallsBackToWhite` pins it for `9` and `q`.
- None on the shipped data. The import is shared, so every grammar with a `graphics:` line has the same behaviour; the
  C handlers differ from one another in how they treat an unknown colour.

## `ItemObjectGrammar.g4` and `EffectBlock.g4`: `effect-msg:` is accepted on an object, C has no such directive

Found 2026-10-17 during `/port backend/parser/ItemObjectReader.java`. Not fixed.

- `init_parse_object` in `obj-init.c` registers `effect`, `effect-yx`, `dice` and `expr` but no `effect-msg`, so C
  gives `PARSE_ERROR_UNDEFINED_DIRECTIVE`. The object-level texts are `msg:` and `vis-msg:`.
- `ItemObjectGrammar.g4:235` takes the shared `effectBlock`, which accepts `effect-msg:`.
- `ItemObjectReaderTest.effectMsgIsAcceptedHereWhereCHasNoSuchDirective` pins it. Same shared-block cause as the
  `MonsterSpellGrammar.g4` entry above, which also covers the `effect-yx:` / `dice:` shape.
- None on the shipped data.

## `ObjectBaseGrammar.g4`: a fixed shape of directives, C takes any order and any omission

Found 2026-10-17 during `/port backend/parser/ObjectBaseReader.java`. Not fixed.

### What C does

- `init_parse_object_base` in `obj-init.c` registers `default`, `name`, `graphics`, `break`, `max-stack` and `flags` as
  independent handlers.
- `parse_object_base_defaults` can run any number of times, anywhere, including never. Each call overwrites one field
  of the parser's defaults, and `parse_object_base_name` copies those defaults into the record it starts, so a
  `default:` line only affects the records after it.
- The four record handlers only need a record to exist (`PARSE_ERROR_MISSING_RECORD_HEADER` otherwise). They can come
  in any order, any can be left out, and a repeated `graphics:`, `break:` or `max-stack:` overwrites. A record with
  no `graphics:` keeps colour 0.
- `finish_parse_object_base` accepts a file with no record at all.

### What Java does

- `ObjectBaseGrammar.g4:162`: the `file` rule is `recordCount (default_value)+ (object_base)+ EOF`, so the file needs at
  least one `default:` line, all of them before the first `name:`, and at least one record.
- `ObjectBaseGrammar.g4:125`: the `object_base` rule is `name graphics (break_chance)? (max_stack)? (flags)*`, so
  `graphics:` is compulsory, `break:` must precede `max-stack:`, `flags:` must come last, and `break:` or `max-stack:`
  may appear once.
- Each departure is a syntax error, and a syntax error returns no items, so one misplaced line costs the whole file.
- `ObjectBaseReaderTest` pins seven of them: `aFileWithNoDefaultLineIsAHardErrorWhereCAcceptsIt`,
  `aDefaultLineAfterARecordIsAHardErrorWhereCAcceptsIt`, `aFileWithNoRecordIsAHardErrorWhereCAcceptsIt`,
  `aRecordWithNoGraphicsLineIsAHardErrorWhereCAcceptsIt`, `maxStackBeforeBreakIsAHardErrorWhereCAcceptsAnyOrder`,
  `aRepeatedBreakLineIsAHardErrorWhereCLetsItOverwrite` and `flagsSeparatedBySpacesAloneAreAHardErrorWhereCAcceptsThem`.

### Impact

- None on the shipped data, which has the one shape the grammar wants.
- A modded `object_base.txt` that reorders directives, drops `graphics:` or sets the defaults part-way down loads in C
  and fails in Java.
- The same comparison applies to every grammar that wrote a fixed record shape; `BrandGrammar.g4` is the same family.

## `ObjectBaseLexer.g4`: `flags:` needs a pipe between names, C splits on spaces as well

Found 2026-10-17 during `/port backend/parser/ObjectBaseReader.java`. Not fixed.

- `parse_object_base_flags` in `obj-init.c` runs `strtok(s, " |")`, so `flags:HATES_ACID HATES_FIRE` is two flags in C.
- `ObjectBaseLexer.g4:124`: `FLAG_OR` is `' '? '|' ' '?`, so a bare space is not a separator; the second name is a
  token recognition error and the whole file loads as nothing.
- `ObjectBaseReaderTest.flagsSeparatedBySpacesAloneAreAHardErrorWhereCAcceptsThem` pins it.
- None on the shipped data, which always writes ` | `. `A|B` with no spaces works in both.

## `ObjectBaseLexer.g4`: the header comment names handlers C does not have

Found 2026-10-17 during `/port backend/parser/ObjectBaseReader.java`. Not fixed.

- `ObjectBaseLexer.g4:23` refers to `parse_object_base_default_*`, which suggests one handler per default. C has a single
  `parse_object_base_defaults` that tests the label (`break-chance` or `max-stack`) and returns
  `PARSE_ERROR_UNDEFINED_DIRECTIVE` for any other.
- A comment fix only; no behaviour difference.

## `CurseGrammar.g4`: a second `msg:` replaces the first, C appends it

Found 2026-10-17 during `/port backend/parser/CurseReader.java`. [<span style="color:#006400">**CLOSED**</span> 26-10-20: in `CurseGrammar.g4` the `msg` alternative starts from `msgInit = null` and appends a repeat with no separator, as `string_append` does. `CurseReaderTest` passes. The bullets below describe the state before the fix; the pinning test is now `aSecondMsgLineIsAppendedToTheFirstAsCDoes`.]

- `parse_curse_msg` in `obj-init.c` runs `curse->obj->effect_msg = string_append(curse->obj->effect_msg, parser_getstr(p, "text"))`,
  so two `msg:` lines in one record run together with no separator, as `desc:` does.
- `CurseGrammar.g4:135`: `msg { msgInit = $msg.msgStr; }` assigns, so the second line replaces the first.
- `CurseReaderTest.aSecondMsgLineReplacesTheFirstHereWhereCAppendsIt` pins it (`msg:A` then `msg:B` gives `B`, C gives `AB`).
- None on the shipped data: no curse has two `msg:` lines.
- Same family as the `BlowEffectGrammar.g4` `desc:` entry at the top of this file. `desc:` is fine here: `CurseGrammar.g4`
  collects every line and `CurseAssembler` joins them with no separator, as C does.

## `CurseGrammar.g4`: a record needs a directive after `name:` and the file needs a record, C accepts neither rule

Found 2026-10-17 during `/port backend/parser/CurseReader.java`. Not fixed.

- `parse_curse_name` in `obj-init.c` builds a complete record from the name alone, and `finish_parse_curse` copes with an
  empty chain (`curse_max` becomes 1, only the blank slot).
- `CurseGrammar.g4:139`: the `)+` closing the directive alternatives in `curseRecord` needs at least one directive after
  `name:`, so a name-only record is a syntax error and the whole file loads as nothing.
- `CurseGrammar.g4:146`: `file` is `recordCount (curseRecord ...)+ EOF`, so a file with the header and no records is a
  syntax error too.
- `CurseReaderTest.aRecordWithOnlyANameIsAHardErrorHereWhereCAcceptsIt` and
  `aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing` pin both.
- None on the shipped data: all 27 records carry directives. Same family as the `PitGrammar.g4` entry above.

## `CurseGrammar.g4` and `EffectBlock.g4`: the effect lines are a fixed block, and a second `dice:` is a syntax error

Found 2026-10-17 during `/port backend/parser/CurseReader.java`. Not fixed.

- `parse_curse_effect_yx`, `parse_curse_dice` and `parse_curse_expr` in `obj-init.c` act on the curse's last effect, in any
  order, and return `PARSE_ERROR_NONE` without doing anything when there is no effect yet. `parse_curse_dice` frees the
  effect's old dice and installs the new ones, so a second `dice:` line replaces the first.
- `CurseGrammar.g4:127`: the `effectBlock` alternative is the shared block, which is `effect:`, then `effect-yx:` or one
  optional `dice:`, then an optional `effect-msg:`. So these are all syntax errors in Java and fine in C:
  a `dice:` before the first `effect:`, `effect-yx:` and `dice:` on the same effect, and two `dice:` lines on one effect.
- `effect-msg:` is the other way round: `init_parse_curse` registers no such directive, so C gives
  `PARSE_ERROR_UNDEFINED_DIRECTIVE`, and Java accepts it because the shared block does.
- `CurseReaderTest.aDiceLineBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt`,
  `effectYxAndDiceOnTheSameEffectAreAHardErrorHereWhereCTakesBoth`,
  `aSecondDiceLineOnOneEffectIsAHardErrorHereWhereCReplacesTheFirst` and
  `effectMsgIsAcceptedHereWhereCHasNoSuchDirective` pin the four. Several `effect:` lines on one curse work in both
  (`severalEffectsOnOneCurseAllLoadInOrder`).
- None on the shipped data: no curse uses `effect-yx:` or `effect-msg:`, and every `dice:` follows its `effect:`.
- Same shared-block cause as the `MonsterSpellGrammar.g4` and `ItemObjectGrammar.g4` entries above; the second-`dice:`
  case is new to this one.

## `CurseLexer.g4`: `flags:`, `values:` and `conflict-flags:` need a pipe between names, C splits on spaces as well

Found 2026-10-17 during `/port backend/parser/CurseReader.java`. Not fixed.

- `parse_curse_flags`, `parse_curse_values` and `parse_curse_conflict_flags` in `obj-init.c` each run `strtok(s, " |")`, so
  `flags:AGGRAVATE AFRAID` is two flags in C and `values:STR[-1] INT[-1]` is two values.
- `CurseLexer.g4:81` (`FLAG_OR`) and `CurseLexer.g4:63` (`VALUE_OR`) are `' '? '|' ' '?`, so a bare space is not a
  separator; the second name is a token recognition error and the whole file loads as nothing.
- `CurseReaderTest.flagsSeparatedBySpacesAloneAreAHardErrorHereWhereCAcceptsThem`,
  `valuesSeparatedBySpacesAloneAreAHardErrorHereWhereCAcceptsThem` and
  `conflictFlagsSeparatedBySpacesAloneAreAHardErrorHereWhereCAcceptsThem` pin it.
- None on the shipped data, which always writes ` | `. Same family as the `ObjectBaseLexer.g4` entry above.

## `CurseLexer.g4`: integers are decimal-only with no plus sign, C reads them with `strtol` base 0

Found 2026-10-17 during `/port backend/parser/CurseReader.java`. Not fixed.

- `parser_parse` in `parser.c` reads an `int` field (`weight:` and the three `combat:` numbers) with `strtol(tok, &z, 0)`.
  Base 0 means `010` is 8, `0x10` is 16 and `+1` is 1.
- `imports/EffectBlockLexer.g4:137`: `INTEGER` is `'-'? ('0'..'9')+`. So `weight:010` loads as 10, `weight:0x10` is a syntax error
  and `combat:+1:0:0` is a syntax error.
- `CurseReaderTest.aLeadingZeroWeightIsDecimalHereWhereCReadsOctal`, `aHexWeightIsAHardErrorHereWhereCReadsItAsSixteen`
  and `aPlusSignOnACombatNumberIsAHardErrorHereWhereCAcceptsIt` pin the three.
- None on the shipped data, which writes plain decimal numbers. Same family as the `BrandLexer.g4` and `SlayLexer.g4`
  entries above.

## `ActivationsGrammar.g4`: a second `desc:` or `msg:` replaces the first, C appends it

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. [<span style="color:#006400">**CLOSED**</span> 26-10-20: in `ActivationsGrammar.g4` `msg` and `desc` are plain alternatives of the `activation` rule's outer `(...)+` (a `*` inside it made ANTLR reject the rule), and each action starts from `null` and appends a repeat with no separator, as `string_append` does. `ActivationReaderTest` passes (44 tests): `TELE_LONG` and `RESIST_ALL` now load the full C strings. The bullets below describe the state before the fix; the pinning tests are now `aSecondDescLineIsAppendedToTheFirstAsCDoes`, `theShippedTeleLongAndResistAllKeepBothDescLinesAsCDoes` and `aSecondMsgLineIsAppendedToTheFirstAsCDoes`.]

- `parse_act_desc` and `parse_act_msg` in `obj-init.c` run `act->desc = string_append(act->desc, ...)` and
  `act->message = string_append(act->message, ...)`, so two lines in one record run together with no separator. A space
  has to be written into the data file, which is why the shipped continuation line is `desc: could go`.
- `ActivationsGrammar.g4:181` and `:188`: `msg { messageInit = $msg.message; }` and `desc { descInit = $desc.descStr; }`
  assign, so the second line replaces the first. `ActivationAssembler.java:92-93` takes both as they come.
- Unlike the `BlowEffectGrammar.g4` and `CurseGrammar.g4` entries above, this one **does** hit the shipped data. `TELE_LONG`
  and `RESIST_ALL` each split their description over two `desc:` lines, so Java loads `" could go"` for the first (C gives
  `"teleports you randomly about 60 per cent of the largest distance you could go"`) and `" for 1d20+20 turns"` for the
  second (C gives `"grants temporary resistance to acid, electricity, fire, cold and poison for 1d20+20 turns"`).
- No Java code reads `Activation.desc` yet, so nothing shows it today. `obj-info.c` prints it in the item description, so
  it will once that is ported.
- `ActivationReaderTest.aSecondDescLineReplacesTheFirstHereWhereCAppendsIt`,
  `theShippedTeleLongAndResistAllLoseTheirFirstDescLine` and `aSecondMsgLineReplacesTheFirstHereWhereCAppendsIt` pin the
  Java result.
- This is the first grammar found where the fault reaches the shipped data. It answers the open question at the end of the
  `BlowEffectGrammar.g4` entry (other grammars with the same fault); a search of the rest is still needed.

## `ActivationsGrammar.g4`: a record needs a directive after `name:` and the file needs a record, C accepts neither rule

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

- `parse_act_name` in `obj-init.c` builds a complete record from the name alone, and `finish_parse_act` copes with an
  empty chain (`act_max` becomes 1, only the blank slot).
- `ActivationsGrammar.g4:188`: the `)+` closing the directive alternatives in `activation` needs at least one directive
  after `name:`, so a name-only record is a syntax error and the whole file loads as nothing.
- `ActivationsGrammar.g4:205`: `file` is `recordCount (activation ...)+ EOF`, so a file with the header and no records is
  a syntax error too.
- `ActivationReaderTest.aRecordWithOnlyANameIsAHardErrorHereWhereCAcceptsIt` and
  `aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing` pin both.
- None on the shipped data: all 163 records carry directives. Same family as the `CurseGrammar.g4` and `PitGrammar.g4`
  entries above.

## `ActivationsLexer.g4`: `name:` takes only upper case, digits and underscore, and trailing blanks are dropped

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

- `init_parse_act` registers `name str name`, and a `str` field takes the whole rest of the line, so `name:lower`,
  `name:TWO WORDS` and `name:T  ` are accepted and the last keeps its trailing blanks.
- `ActivationsLexer.g4:121-122`: `NAME_STRING` is `('A'..'Z' | '0'..'9' | '_')+`. A lower-case letter or an inner space has
  no lexer rule, so it is a hard error and the file loads as nothing. Trailing blanks are swallowed by the `EOL` rule, so
  `T  ` loads as `T`.
- `ActivationReaderTest.aLowerCaseNameIsAHardErrorHereWhereCTakesAnyString` and
  `aNameWithTrailingBlanksIsTrimmedHereWhereCKeepsThem` pin both.
- None on the shipped data: all 163 names are upper case with underscores and digits. A modded name written in lower case
  would fail the whole file here. Same family as the `BlowMethodGrammar.g4` `name:` entry above.

## `EffectBlockLexer.g4`: `aim:`, `level:` and `power:` are decimal-only, and a leading minus is accepted on the unsigned two

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

- `init_parse_act` registers `aim uint`, `level int` and `power uint`. `parser_parse` reads an `int` with
  `strtol(tok, &z, 0)`, base 0, so `010` is 8 and `0x10` is 16, and refuses a `uint` token that starts with `-` as
  `PARSE_ERROR_NOT_NUMBER`.
- `imports/EffectBlockLexer.g4:137-139`: `INTEGER` is `'-'? ('0'..'9')+`, and `ActivationsGrammar.g4` uses it for all three.
  So `level:010` is 10 here and 8 in C, `level:0x10` is a lexer error, and `power:-3` and `aim:-1` get as far as the
  assembler, which keeps the first as -3 and reads the second as false.
- `ActivationReaderTest.aLeadingZeroLevelIsReadAsDecimalHereWhereCReadsOctal`,
  `aHexLevelIsAHardErrorHereWhereCReadsItAsSixteen`, `aNegativePowerIsAcceptedHereWhereCRejectsAUint` and
  `aNegativeAimIsAcceptedAndReadAsFalseHereWhereCRejectsAUint` pin the Java result.
- None on the shipped data, which writes plain unsigned decimals. Same family as the `MonsterLexer.g4` integers entry above.

## `ActivationsGrammar.g4` and `EffectBlock.g4`: an effect takes `effect-yx:` or `dice:` but not both, its lines cannot come first, and `effect-msg:` is accepted

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

- This is the shape described in the `MonsterSpellGrammar.g4` and `EffectBlock.g4` entry above, and it applies to
  `activation.txt` unchanged: `parse_act_effect_yx`, `parse_act_dice` and `parse_act_expr` act on the last effect in any
  order and ignore a line that arrives before any effect.
- Java makes `effect-yx:` and `dice:` on one effect a syntax error in either order, and a `dice:` or `effect-yx:` before
  the first `effect:` one too. `act_parser` registers no `effect-msg:`, so C gives `PARSE_ERROR_UNDEFINED_DIRECTIVE`, and
  Java accepts it.
- `ActivationReaderTest.aDiceLineBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt`,
  `anEffectYxLineBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt`,
  `effectYxAndDiceOnTheSameEffectAreAHardErrorHereWhereCTakesBoth` and
  `effectMsgIsAcceptedHereWhereCHasNoSuchDirective` pin the four.
- None on the shipped data: 11 `effect-yx:` lines and 157 `dice:` lines, every one after its `effect:`, and never both on
  one effect.

## `ActivationsGrammar.g4` and `ActivationsLexer.g4`: the header comments cite C line numbers, and say the index is removed

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

- Both files open with the same block, `ActivationsGrammar.g4:26-28` and `ActivationsLexer.g4:26-28`, citing
  `obj-init.c:1678`, `obj-init.c:1600-1630` and `obj-init.c:1635`. They should name `act_parser`, `init_parse_act` and
  `finish_parse_act` without a line number.
- The same block says the sequential index "is removed, as in Java we are using the object itself from a fixed List". It
  is not: `ActivationAssembler` numbers every activation through `Activation.setIndex` (from 0, where C counts from 1),
  because the savefile will need the slot.
- Not a behaviour difference; recorded here because the fix is in the grammar comments.

## `ObjectPropertyGrammar.g4`: a second `msg:` or `desc:` appends, C replaces it

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `parse_object_property_msg` and `parse_object_property_desc` in `obj-init.c` run `string_free` on the old string and
  `string_make` on the new one, so a second line replaces the first. This is the reverse of the `desc:` entries above,
  where C appends and Java replaces.
- `ObjectPropertyGrammar.g4:166` and `:171`: `msgSB.append($msg.msgLine)` and `descSB.append($desc.line)` append to a
  `StringBuilder`, so `msg:A` then `msg:B` gives `AB` in Java and `B` in C. The file comment at `ObjectPropertyGrammar.g4:18-20`
  knows this and calls it harmless.
- `ObjectPropertyReaderTest.aSecondMsgOrDescLineIsAppendedHereWhereCReplacesIt` pins the Java result.
- None on the shipped data: no record has two `msg:` or two `desc:` lines (21 carry one `msg:`, 22 one `desc:`).

## `ObjectPropertyGrammar.g4`: a second `bindui:` replaces the first, C binds every line

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- The header of `object_property.txt` says `bindui` "can appear multiple times to bind a property to more than one user
  interface element". `parse_object_property_bindui` in `obj-init.c` calls `bind_object_property_to_ui_entry_by_name` for
  each line as it reads it.
- `ObjectPropertyGrammar.g4:167-170`: the `bindui` alternative assigns `binduiInit`, `tagInit`, `valueInit` and `auxInit`,
  which are single locals, so the last line wins and the earlier ones are gone before `ObjectPropertyAssembler` runs.
  `ObjectPropertyParseRecord` holds one binding, and the assembler's comment saying the list shape "allows more than one
  bind" is true only of the list.
- `ObjectPropertyReaderTest.aSecondBinduiLineReplacesTheFirstHereWhereCBindsBoth` pins the Java result.
- None on the shipped data: all 66 `bindui:` lines are in different records, so no record has two.
- It will matter for a modded file, and for the day a second UI entry is bound to a stat.

## `ObjectPropertyGrammar.g4`: a record is `name:` then `type:` then at least one directive, and the file needs a record

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- C accepts the directives of a record in any order. Only `code:` and `bindui:` need `type:` to have come first, and
  `type:` itself can be left out (the record stays `OBJ_PROPERTY_NONE`) or come after `power:` or `mult:`. A second `type:`
  overwrites the first. `parse_object_property_name` builds a complete record from the name alone, and
  `finish_parse_object_property` copes with an empty chain (`property_max` becomes 1).
- `ObjectPropertyGrammar.g4:156-157` makes `name` then `type` the fixed start of `objectProperty`, and the `)+` at `:171`
  needs one more directive. `type` is not among the repeatable alternatives. `ObjectPropertyGrammar.g4:182` is
  `(objectProperty ...)+ EOF`, so a file with the header and no records is a syntax error as well.
- So a record with no `type:`, a `type:` after another directive, a second `type:`, a record that is only a name and a
  file with no records are all syntax errors, and the whole file loads as nothing.
- `ObjectPropertyReaderTest.aMissingOrLateTypeIsAHardErrorHereWhereCAcceptsIt`,
  `aSecondTypeLineIsAHardErrorHereWhereCOverwrites`, `codeBeforeTypeIsAHardError` and
  `aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing` pin them. `codeBeforeTypeIsAHardError` is a fault in C too.
- None on the shipped data: all 79 records open with `name:` then `type:`. Same family as the `CurseGrammar.g4` and
  `PitGrammar.g4` entries above.

## `ObjectPropertyLexer.g4`: `adjective:` and `neg-adjective:` take one lower-case word, C takes any string

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `init_parse_object_property` registers `adjective str adj` and `neg-adjective str neg_adj`, and a `str` field takes the
  whole rest of the line, so `adjective:very strong` and `adjective:Strong` are accepted.
- `ObjectPropertyGrammar.g4:85` and `:90` take an `LCASE` token, and `ObjectPropertyLexer.g4:92` makes that
  `('a'..'z' | '0'..'9' | '_')+`. A space or a capital has no lexer rule, so it is a hard error and the file loads as
  nothing.
- `ObjectPropertyReaderTest.anAdjectiveWithASpaceOrACapitalIsAHardErrorHereWhereCTakesAnyString` pins it.
- None on the shipped data: the five adjectives and five negative adjectives are single lower-case words (`strong`,
  `weak`, `smart`, `stupid`, ...). Same family as the `ActivationsLexer.g4` `name:` entry above.

## `ObjectPropertyGrammar.g4` and `ObjectPropertyLexer.g4`: a numeric tval in `type-mult:` is a syntax error, C accepts it

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `parse_object_property_type_mult` in `obj-init.c` calls `tval_find_idx` in `obj-tval.c`. That function first reads the
  name with `strtoul`, and if digits were read it returns the number when only spaces follow and it is below `TV_MAX`. So
  `type-mult:3:2` names tval 3.
- `ObjectPropertyGrammar.g4:78` wants `STRING_BETWEEN_COLONS` for the tval. In `DELIMITER` mode a bare number matches
  `DELIM_INTEGER` first (`ObjectPropertyLexer.g4:118`), so `3` is the wrong token and the line is a syntax error.
- `ObjectPropertyReaderTest.aNumericTvalInTypeMultIsAHardErrorHereWhereCAcceptsIt` pins it.
- None on the shipped data, which names every tval in words (275 lines). `TValue.fromName(int)` already has the numeric
  lookup, so only the lexer stands in the way.

## `ObjectPropertyLexer.g4`: integers are decimal-only with no plus sign, C reads them with `strtol` base 0

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `parser_parse` in `parser.c` reads an `int` field (`power:`, `mult:`, the `type-mult:` figure and both `bindui:` figures)
  with `strtol(tok, &z, 0)`. Base 0 means `010` is 8, `0x10` is 16 and `+1` is 1.
- `ObjectPropertyLexer.g4:79` and `:118`: `INTEGER` and `DELIM_INTEGER` are `'-'? ('0'..'9')+`. So `power:010` loads as 10,
  and `power:0x10` and `power:+5` are lexer errors.
- `ObjectPropertyReaderTest.integersAreDecimalOnlyHereWhereCReadsBaseZero` pins the three.
- None on the shipped data, which writes plain decimal numbers. Same family as the `CurseLexer.g4` entry above.

## `EgoItemsGrammar.g4`: a record is `name:` then at least one directive, and the file needs a record

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parse_ego_name` in `obj-init.c` builds a complete record from the name alone, and `finish_parse_ego` copes with an empty
  chain (`e_max` ends up as 1 with a blank `e_info[0]`).
- `EgoItemsGrammar.g4:149` starts `egoItem` with `name` and the `)+` at `:213` needs one more directive. `:216-220` is
  `( egoItem ... )+ EOF`, so a file with the header and no records is a syntax error as well.
- So a record that is only a name and a file with no records are both syntax errors, and the whole file loads as nothing.
- `EgoItemReaderTest.aRecordOfOnlyANameIsASyntaxErrorHereWhereCAcceptsIt` and
  `aFileWithNoRecordsIsASyntaxErrorHereWhereCLoadsNothing` pin them.
- None on the shipped data: all 107 records carry directives. Same family as the `CurseGrammar.g4`, `PitGrammar.g4` and
  `ObjectPropertyGrammar.g4` entries above.

## `EgoItemsGrammar.g4`: `flags:` and `flags-off:` need a flag, C takes an empty list

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `init_parse_ego` registers `flags ?str flags` and `flags-off ?str flags`. The `?` makes the field optional, and
  `parse_ego_flags` and `parse_ego_flags_off` return no error when `parser_hasval` is false. So `flags:` with nothing after
  the colon loads in C and sets nothing.
- `EgoItemsGrammar.g4:77` (`FLAGS f1=FLAG`) and `:87` (`FLAGS_OFF f1=FLAG`) need at least one `FLAG` token, so an empty line
  is a syntax error and the file loads as nothing.
- `EgoItemReaderTest.emptyFlagsAndFlagsOffLinesAreSyntaxErrorsHereWhereCAcceptsThem` pins both.
- None on the shipped data: every `flags:` and `flags-off:` line names at least one flag.

## `EgoItemsGrammar.g4` and `EgoItemsLexer.g4`: C splits flags and values on any run of spaces and bars, the grammar wants one bar between two

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parse_ego_flags`, `parse_ego_flags_off`, `parse_ego_values` and `parse_ego_min_val` walk the text with
  `strtok(text, " |")`. Any run of spaces and bars is one separator, so `SEE_INVIS FREE_ACT`, `SEE_INVIS || FREE_ACT`, a
  leading `|` and a trailing `|` all load in C. A tab is not a separator, so it ends up inside a token and fails there too.
- `EgoItemsLexer.g4:149-150` and `:163-164` make `VALUE_OR` and `FLAG_OR` a single `|` with at most one space either side.
  `EgoItemsGrammar.g4:77-81` and `:84-91` allow one trailing `FLAG_OR?` after the last flag, and `:94-115` allow none after
  the last value.
- So flags separated by a space alone, a doubled or leading bar, and values separated by a space alone, doubled or trailed by
  a bar, are syntax errors. The whole file loads as nothing.
- `EgoItemReaderTest.flagsSeparatedBySpaceAloneOrByOddBarsAreSyntaxErrorsHereWhereCAcceptsThem` and
  `valuesSeparatedBySpaceAloneOrEndingInABarAreSyntaxErrorsHereWhereCAcceptsThem` pin them.
- On the shipped data only the trailing bar on the `flags:FREE_ACT |` line shows, and that one the grammar accepts.

## `EgoItemsLexer.g4`: `alloc:` wants exactly `N to N`, C accepts any spacing round the figures

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parse_ego_alloc` hands the `minmax` text to `grab_int_range` in `datafile.c` with the separator `"to"`. That reads
  `<int><whitespace>to<whitespace><int>` with `strtol`, which skips leading whitespace, and accepts trailing spaces.
- `EgoItemsLexer.g4:123-124` makes `ALLOC_TO` the literal `' to '` and gives the figures no whitespace of their own. So two
  spaces, a tab, a space after the colon and a space at the end of the line are all lexer errors, and the file loads as
  nothing.
- `EgoItemReaderTest.allocSpacingOtherThanOneSpaceEitherSideOfToIsASyntaxErrorHereWhereCAcceptsIt` pins the four.
- None on the shipped data: all 107 `alloc:` lines are `N:N to N`.

## `EgoItemsLexer.g4`: integers are decimal-only with no plus sign or leading space, C reads them with `strtol` base 0

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parser_parse` in `parser.c` reads an `int` field (`info:`, `alloc:`'s commonness, `min-combat:`) with `strtol(tok, &z, 0)`,
  so leading whitespace is skipped, `+5` is 5 and `0x10` is 16. The bracketed figure of a `RES_` value and of `min-values:`
  is read by `find_value_arg` in `datafile.c` with `strtol` base 10, which also accepts `+1`.
- `EgoItemsLexer.g4:77` and `:115`: `INTEGER` and `ALLOC_INT` are `'-'? ('0'..'9')+`, and the bracketed figure is a
  `VALUE_DICE` (`:141`), which has no plus sign either. So `info: 5: 6`, `info:+5:6`, `info:0x10:6`, `RES_FIRE[+1]` and
  `min-values:STEALTH[+2]` are lexer errors and the file loads as nothing.
- `EgoItemReaderTest.integersWithLeadingSpaceAPlusSignOrAHexPrefixAreErrorsHereWhereCAcceptsThem` pins the five.
- None on the shipped data, which writes plain decimal numbers. Same family as the `ObjectPropertyLexer.g4` and
  `CurseLexer.g4` entries above.

## `ArtifactGrammar.g4`: a record is `name:` then at least one directive, and the file needs a record

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_name` in `obj-init.c` builds a complete record from the name alone, and `finish_parse_artifact` copes with an
  empty chain (`a_max` ends up as 1).
- `ArtifactGrammar.g4:181` starts `artifact` with `name` and the `)+` at `:204` needs one more directive; `file` is
  `recordCount (artifact)+ EOF`. So a name-only record and a header with no records are both syntax errors and the file loads as
  nothing.
- `ArtifactReaderTest.aRecordOfOnlyANameOrAFileOfNoRecordsIsASyntaxErrorHereWhereCAcceptsThem` pins both.
- None on the shipped data. Same family as the `EgoItemsGrammar.g4` entry above.

## `ArtifactGrammar.g4`: `flags:` needs a flag, C takes an empty list

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `init_parse_artifact` registers `flags ?str flags` and `parse_artifact_flags` returns no error when `parser_hasval` is false.
- `ArtifactGrammar.g4:80` (`FLAGS f1=FLAG`) needs one `FLAG` token, so `flags:` alone is a syntax error.
- `ArtifactReaderTest.anEmptyFlagsLineIsASyntaxErrorHereWhereCAcceptsIt` pins it.
- None on the shipped data: all 118 `flags:` lines name a flag.

## `ArtifactLexer.g4` and `ArtifactGrammar.g4`: C splits flags and values on any run of spaces and bars, the lexer wants one bar between two

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_flags` and `parse_artifact_values` use `strtok(s, " |")`, so a space alone, a doubled or a leading bar all
  separate names in C.
- `ArtifactLexer.g4:145` and `:171` make `FLAG_OR` and `VALUES_OR` one `|` with at most one space either side, and the grammar
  (`:80-84`, `:110-113`) allows one trailing bar. A space-only list, `||` or a leading bar is a syntax error.
- `ArtifactReaderTest.flagsOrValuesSeparatedBySpaceAloneOrByOddBarsAreSyntaxErrorsHereWhereCAcceptsThem` pins four.
- None on the shipped data.

## `ArtifactLexer.g4`: a blank after the last flag, value or alloc figure is a lexer error, C accepts it

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. [<span style="color:#006400">**CLOSED**</span> 26-10-20: the `EOL_POP_MODE` fragment in `ArtifactLexer.g4` is now `' '* '\r'* '\n'`, so `FLAG_EOL`, `VALUES_EOL` and `ALLOC_EOL` take spaces before the newline. The upstream `artifact.txt` (with `record-count:138` put back) loads 138 records with no errors, and `ArtifactReaderTest` passes (49 tests). The bullets below describe the state before the fix; the pinning test is now `aBlankAfterTheLastFlagValueOrAllocFigureLoadsAsInC`. A tab before the newline is still a lexer error and belongs to the `CommentsAndEol.g4` entry.]

- C's `strtok` and `strtol` ignore trailing blanks. The upstream `artifact.txt` has such a line: the `flags:` line of
  "'Bladeturner'" ends `PROT_CONF ` with a space.
- `FLAG_EOL`, `VALUES_EOL` and `ALLOC_EOL` need the newline straight after the last token, so the untouched upstream file
  fails with `token recognition error at: ' \n'` and then loses the next line as well. A trailing blank after a bar, or after an
  integer or dice string, is fine.
- This repository's `lib/gamedata/artifact.txt` has had its trailing blanks trimmed, which is why the shipped file loads.
- `ArtifactReaderTest.aBlankAfterTheLastFlagValueOrAllocFigureLoadsAsInC` now pins the three (it was `...IsALexerErrorHereWhereCAcceptsIt`).

## `ArtifactLexer.g4`: `alloc:` wants exactly `N to N`, C accepts any spacing round the figures

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_alloc` hands the text to `grab_int_range` with separator `"to"`, which uses `strtol` and skips blanks.
- `ArtifactLexer.g4:103` makes `ALLOC_TO` the literal `' to '`, so two spaces or none are lexer errors.
- `ArtifactReaderTest.allocSpacingOtherThanOneSpaceEitherSideOfToIsASyntaxErrorHereWhereCAcceptsIt` pins two.
- None on the shipped data.

## `ArtifactLexer.g4`: integers are decimal-only with no plus sign or leading space, C reads them with `strtol` base 0

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parser_parse` reads an `int` with `strtol(tok, &z, 0)`: leading blanks skipped, `+5` is 5, `0x10` is 16, `010` is 8.
- `ArtifactLexer.g4:85` `INT_FRAGMENT` is `'-'? ('0'..'9')+`, so the first three are lexer errors and `010` reads as 10. The
  bracketed figure of `values:` has no plus sign either.
- `ArtifactReaderTest.integersWithLeadingSpaceAPlusSignOrAHexPrefixAreErrorsAndALeadingZeroIsDecimalHereWhereCDiffers` pins them.
- None on the shipped data. Same family as the `EgoItemsLexer.g4` entry above.

## `ArtifactLexer.g4` and `ArtifactGrammar.g4`: a number in `base-object:` is a syntax error, C accepts it

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `tval_find_idx` returns a number as the tval, and `lookup_sval` returns a number as the sval.
- `ArtifactGrammar.g4:26` takes `STRING_BETWEEN_COLONS` twice, but the lexer's `DELIM_INTEGER` wins for digits, so
  `base-object:23:Dagger` and `base-object:sword:1` fail.
- `ArtifactReaderTest.aNumericTvalOrSvalInBaseObjectIsASyntaxErrorHereWhereCAcceptsIt` pins both.
- None on the shipped data.

## `ArtifactGrammar.g4`: a later `curse:` for the same name replaces an earlier one, C only ever raises it

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_curse` stores a power only above zero, so `curse:air swing:30` then `curse:air swing:0` leaves 30.
- `ArtifactGrammar.g4:202` does `curseInit.putAll`, keyed by name, so the 0 replaces the 30 and the assembler then drops it.
- `ArtifactReaderTest.aLaterCurseWithNoPowerErasesAnEarlierOneHereWhereCKeepsIt` pins it.
- None on the shipped data, which has one `curse:` line.

## `ProjectionGrammar.g4`: a record is a fixed sequence with five mandatory directives, C takes any directives in any order

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- `init_parse_projection` in `obj-init.c` registers each of the fifteen directives on its own, so `parser_parse` runs them in
  whatever order the file gives. A left-out directive leaves the zero-filled field (`NULL` strings, 0 numbers, message 0,
  colour 0), and a repeated string replaces the first (each handler frees the old one).
- `ProjectionGrammar.g4:229-255`, rule `projection`: `code name? type desc player-desc? blind-desc lash-desc? numerator?
  denominator? divisor? damage-cap? msgt? obvious wake? color`. `type:`, `desc:`, `blind-desc:`, `obvious:` and `color:` are
  required.
- A repeated `desc:`, a `name:` after `type:`, a directive ahead of `code:` and a missing required one are all syntax errors.
  A syntax error is a hard error, so one bad record empties the whole file, where C stops at the line that failed.
- `ProjectionReaderTest.aRepeatedOrMisplacedDirectiveIsAHardErrorWhereCTakesAnyOrder` and
  `aLeftOutTypeDescBlindDescObviousOrColourIsAHardErrorWhereCLeavesItZero` pin the Java result.
- None on the shipped data, where all 56 records follow this order. Same family as the `BrandGrammar.g4` entry above.

## `ProjectionGrammar.g4`: the file needs a `record-count:` header and a record, and the upstream file has no header

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- The `record-count:` header is a Java addition. The upstream `projection.txt` starts its records with `code:ACID` and has no
  header; `lib/gamedata/projection.txt` here has `record-count:56` added at line 17.
- `ProjectionGrammar.g4:262-271`, rule `file`, is `recordCount (projection)+ EOF`. The unchanged upstream file fails with
  `mismatched input 'code:' expecting 'record-count:'` and loads as nothing. A header with no records, and an empty file, are
  syntax errors too.
- C has no header to miss. For an empty file `finish_parse_projection` quits on "Too few elements", so no records fails there
  as well; only the missing header is new.
- `ProjectionReaderTest.theUpstreamFileWithNoRecordCountHeaderIsAHardError` and
  `aHeaderWithNoRecordsAndAnEmptyFileAreHardErrors` pin it.
- A non-numeric header (`record-count:abc`) is a hard error; a header that is a number but wrong is a soft error, and the
  records load.

## `ProjectionLexer.g4`: `msgt:` values must be upper-case identifiers, and one bad value costs the rest of the file

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- C reads `msgt:` as a `sym` and `message_lookup_by_name` in `message.c` matches it case-insensitively against the names in
  `list-message.h`, or takes a number below `MSG_MAX`.
- `msgt:` pushes `FLAG_MODE`, whose only token is `FLAG`, `('A'..'Z') ('A'..'Z' | '_' | '0'..'9')*` from `imports/Flags.g4`. So
  `msgt:br_acid` and `msgt:20` are lexer errors.
- The mode is only popped by a `FLAG`, so the lexer stays in `FLAG_MODE` and every later line errors as well: 34 errors for one
  lower-case `msgt:` in the shipped file, 29 for `msgt:20`.
- The number and any-case handling in `ProjectionAssembler.java`'s `resolveMsgt` cannot be reached from a data file.
- `ProjectionReaderTest.aLowerCaseOrNumericMsgtIsAHardErrorWhereCAcceptsBoth` pins both.
- None on the shipped data, which uses upper-case names. Same family as the `SummonLexer.g4` entry above.

## `ProjectionLexer.g4`: a blank after the `code:` or `msgt:` value is dropped, C keeps it and rejects the line

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- C keeps a trailing blank in a `str` or `sym`. `code:ACID ` is not `element_names[0]` (`PARSE_ERROR_ELEMENT_NAME_MISMATCH`), and
  `msgt:BR_ACID ` matches no name (`PARSE_ERROR_INVALID_MESSAGE`).
- After a `FLAG` the `EOL` rule from `imports/CommentsAndEol.g4` takes the blanks before the newline, so both lines load
  cleanly in Java. Java accepts what C rejects.
- `type:` and `color:` keep the blank in Java as C does (`element ` is no longer an element; `Slate ` is White), so the two
  directives that read through `FLAG_MODE` are the only ones that differ.
- `ProjectionReaderTest.aBlankAfterCodeOrMsgtIsDroppedWhereCKeepsItAndRejectsTheLine` and `aBlankAfterATypeOrColourIsKeptAsCKeepsIt`
  pin the two sides.
- None on the shipped data.

## `ProjectionLexer.g4`: integers are decimal-only with a leading minus, C reads them with `strtoul` base 0

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- `numerator:`, `divisor:`, `damage-cap:`, `obvious:` and `wake:` are `uint` in C. `parser_parse` calls `strtoul(tok, &z, 0)` and
  refuses a value only when no digit was read or the first character is `-`. So ` 1`, `+1`, `0x10` (16), `010` (8) and `1abc`
  (1) are all accepted.
- `ProjectionLexer.g4:462`, `INTEGER` is `'-'? ('0'..'9')+`. `+1`, `0x1`, `1abc` and ` 1` are lexer errors, and `010` reads as 10.
  `-1` lexes, and the assembler then drops the record (`Assembler_Issues.md` has the values it keeps).
- `ProjectionReaderTest.aPlusSignHexPrefixTrailingTextOrLeadingBlankInAnIntegerIsAHardErrorWhereCAcceptsIt` and
  `aLeadingZeroIntegerReadsAsDecimalWhereCReadsOctal` pin them.
- None on the shipped data, which writes plain decimal numbers. Same family as the `ArtifactLexer.g4` entry above.

## `ProjectionLexer.g4` and `DiceStrings.g4`: `denominator:` accepts a lower-case `m` and rejects a leading blank

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- `parse_random` in `parser.c` reads the value with `strtoul`, which skips leading blanks, and treats only an upper-case `M` as
  the modifier. `5+1d4m2` falls to the trailing-text test and is refused.
- `imports/DiceStrings.g4` makes `DICE_M` `'M' | 'm'`, so `denominator:5+1d4m2` lexes, and the assembler loads it as `5+1d4M2`.
  `DICE_MODE` has no rule for a blank, so `denominator: 3` is a lexer error.
- The other seventeen forms checked agree with `parse_random`, including the negated ones: `3`, `0`, `08`, `-3`, `5 `, `d4`,
  `1d4`, `5+1d4`, `5+d4`, `2d6M3`, `5+d4M2`, `M2`, `5+M2`, `-5+1d4`, `-d4`, `-2d6M3` and `-M2`.
- `ProjectionReaderTest.aLeadingBlankInADenominatorIsAHardErrorAndALowerCaseMIsAcceptedWhereCTakesTheOpposite` pins the two, and
  `denominatorFormsGiveTheValuesOfCsParseRandom` pins the agreeing forms.
- `DiceStrings.g4` is shared, so every lexer that imports it accepts a lower-case `m` too.
- None on the shipped data, whose denominators are `3` and `8+1d4`.

## `ProjectionGrammar.g4` and `ProjectionLexer.g4` cite C line numbers, and one comment points at a note that is not there

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- `ProjectionGrammar.g4:23-24` and `ProjectionLexer.g4:25-26` cite `obj-init.c:470` and `obj-init.c:400-414`. Both should name
  `projection_parser` and `init_parse_projection` without a line number.
- `ProjectionGrammar.g4:227`, the comment on rule `projection`, says the shape was "confirmed to match every record in
  projection.txt (see top-of-file note)". The comment at the top of the file (`:18-27`) has no such note.
- Not a behaviour difference; recorded here because the fix is in the grammar comments.

## `RealmGrammar.g4` and `RealmLexer.g4`: an empty file, indented lines, a second colon and absent directives differ from C

Found 2026-10-18 during `/port backend/parser/RealmReader.java`. Not fixed.

- **Empty file.** The `file` rule in `RealmGrammar.g4` needs one or more `realm` records, so `record-count:0` with no
  realms is a hard error. C reads an empty `realm.txt` and ends with an empty `realms` list.
- **Indented lines.** `parser_parse` in `parser.c` skips leading whitespace, and treats a line whose first non-blank
  character is `#` as a comment. `CommentsAndEol.g4` only skips `#` at column 0 and `EOL` only consumes spaces before a
  newline, so an indented `  stat:INT` is a hard error here. `RealmReaderTest.anIndentedDirectiveIsAHardErrorHereWhereCSkipsLeadingWhitespace`
  pins it. `CommentsAndEol.g4` is shared, so every lexer that imports it has the same gap.
- **Second colon on a `sym` line.** `stat` is a `sym` in `init_parse_realm`, so `parser_parse` tokenizes it on `:` and drops
  anything after a second colon. `RealmLexer.g4` captures the whole rest of the line, so `stat:INT:extra` gives the stat
  `INT:extra`. `RealmReaderTest.textAfterASecondColonOnTheStatLineIsPartOfTheStatHereWhereCIgnoresIt` pins it.
- **Absent directives.** The `realm` rule in `RealmGrammar.g4` seeds every field with `""`. C zero-fills the realm, so an absent
  `verb:`, `spell-noun:` or `book-noun:` is `NULL` there, and an absent `stat:` is `0`. The `stat` consequence is in
  `docs/Assembler_Issues.md`.
- None on the shipped data, whose four realms are complete and unindented.

## `ShapeGrammar.g4`: the file needs a shape, and `obj-flags:` and `player-flags:` need a flag

Found 2026-10-18 during `/port backend/parser/ShapeReader.java`. Not fixed.

- **Empty file.** The `file` rule in `ShapeGrammar.g4` is `recordCount (shape)+ EOF`, so `record-count:0` with no shapes is a
  hard error. C reads an empty `shape.txt` and ends with an empty `shapes` list.
  `ShapeReaderTest.anEmptyFileIsAHardErrorWhereCAcceptsOne` pins it.
- **Empty flag lines.** `init_parse_shape` registers `obj-flags ?str flags` and `player-flags ?str flags`, and both handlers
  return `PARSE_ERROR_NONE` when `parser_hasval` finds no text. The `objFlags` and `playerFlags` rules want a `FLAG` after
  the directive, so `obj-flags:` alone is a hard error.
  `ShapeReaderTest.anEmptyFlagLineIsAHardErrorWhereCTakesAnOptionalString` pins it.
- A record with only a `name:` line is accepted, as in C; the shipped `normal` shape is one.
- None on the shipped data. `PlayerDataLoader.loadPlayerShapes` stops the load on an empty result anyway, and the game needs
  `normal`.

## `ShapeLexer.g4`: flag and value lists need a bar between names, and a blank after `values:` is a lexer error

Found 2026-10-18 during `/port backend/parser/ShapeReader.java`. Not fixed.

### What C does

- `parse_shape_obj_flags`, `parse_shape_play_flags` and `parse_shape_values` in `init.c` split the text with
  `strtok(s, " |")`, so any run of spaces and bars separates two names. `REGEN FREE_ACT` and `STR[1] INT[2]` both load.
- The text is read as a `str`, so a blank straight after `values:` is skipped by the same `strtok`.
- Names are matched with `streq`, so a lower-case name is refused in C too.

### What Java does

- `FLAG_MODE` and `VALUE_MODE` in `ShapeLexer.g4` take `' '? '|' ' '?` as the only separator, so `obj-flags:REGEN FREE_ACT`
  and `values:STR[1] INT[2]` are hard errors that cost the whole file.
- Neither mode has a rule for a leading blank, so `values: STR[3]` is a hard error. The upstream `shape.txt` writes the `bear`
  record that way; the shipped `lib/gamedata/shape.txt` has the blank removed.
- `ShapeReaderTest.namesSeparatedBySpacesAreAHardErrorWhereCSplitsOnSpaceAndBar` and
  `aBlankAfterValuesColonIsAHardErrorWhereCSkipsIt` pin them; `aBarWithNoBlanksAroundItIsAccepted` pins the form that works.
- `CommentsAndEol.g4` and the indented-line gap are written up under "Leading whitespace on a line"; `ShapeReaderTest.anIndentedDirectiveIsAHardErrorHereWhereCSkipsLeadingWhitespace` pins it for shapes.
- None on the shipped data once the `values:` blank is removed, which is a data edit rather than a fix.

## `ShapeLexer.g4`: integers are decimal-only with no plus sign or leading blank, C reads them with `strtol` base 0

Found 2026-10-18 during `/port backend/parser/ShapeReader.java`. Not fixed.

- `parser_getint` in `parser.c` reads with `strtol(..., 0)`, which takes a plus sign, leading blanks, and octal and hex.
  `combat:+5:0:0`, `skill-dig:+5`, `skill-dig: 5` and `combat: 1 : 2 : 3` all load in C.
- The `INTEGER` rule that `ShapeGrammar.g4` uses for `combat:` and the eight `skill-*:` lines does not match `+` or a blank, so
  each is a lexer error. A leading minus is accepted, as C does for these signed fields.
- `ShapeReaderTest.aPlusSignOrBlankInAFigureIsAHardErrorWhereStrtolTakesThem` pins the four.
- `INTEGER` comes from the `EffectBlockLexer.g4` import that `ShapeLexer.g4` shares with the other effect-bearing lexers, so
  they have the same gap; see the `CurseLexer.g4` entry.
- None on the shipped data.

## `ShapeGrammar.g4` and `EffectBlock.g4`: `effect-msg:`, `dice:` and `expr:` before the first effect are syntax errors, and a second `effect-msg:` is too

Found 2026-10-18 during `/port backend/parser/ShapeReader.java`. Not fixed.

### What C does

- `parse_shape_effect_msg`, `parse_shape_dice` and `parse_shape_expr` return `PARSE_ERROR_NONE` without doing anything when the
  shape has no effect yet ("assume that this is human and not parser error").
- `parse_shape_effect_msg` uses `string_append`, so two `effect-msg:` lines join into one message.

### What Java does

- The `shape` rule in `ShapeGrammar.g4` only takes those lines as part of an `effectBlock`, which opens with `effect:`. A
  line of the three kinds before the first `effect:` is a hard error, and so is a second `effect-msg:` in one block.
- The upstream `werewolf` record opens its effects with `effect-msg:taking werewolf form`, so the file does not load as shipped.
  `lib/gamedata/shape.txt` moves the line below `effect:PROJECT_LOS:TURN_ALL` and says so in a comment.
- The moved line changes the data: C drops the message, because there is no effect yet when it is read, while the shipped copy
  attaches it to the `PROJECT_LOS` effect.
- `ShapeReaderTest.anEffectMessageBeforeTheFirstEffectIsAHardErrorWhereCDropsItSilently`,
  `diceBeforeTheFirstEffectIsAHardErrorWhereCIgnoresIt` and `aSecondEffectMessageIsAHardErrorWhereCAppendsIt` pin them, and
  `theShippedFileDiffersFromUpstreamInThreeLinesThatTheseTestsDependOn` pins the three data edits.
- `EffectBlock.g4` is shared; the `MonsterSpellGrammar.g4` and `ActivationsGrammar.g4` entries above describe the same block.

## `FlavourGrammar.g4` and `FlavourLexer.g4`: a bare `kind:`, a leading flavour, a colon in a description, a trailing colon, an indented line and text after the glyph differ from C

Found 2026-10-18 during `/port backend/parser/FlavourReader.java`. Not fixed.

### What C does

- `parse_flavor_kind` in `init.c` only sets the static `flavor_tval` and `flavor_glyph`, so a `kind:` line with no `flavor:` or
  `fixed:` under it is accepted. A `flavor:` line before any `kind:` is accepted too, with both statics still zero.
- `desc` is a `str`, and `parser_parse` in `parser.c` does not tokenize a `str`, so `flavor:2:Green:A: B` has the description
  `A: B`. With nothing after the last colon, as in `flavor:2:Green:`, there is no token for the optional `desc`, so the line is a
  flavour with no description.
- `glyph` is a `char`: `parser_parse` takes one character, skips a colon after it, and ignores the rest of the line, so
  `kind:ring:=:extra` has the glyph `=`. A second character that is not a colon is `PARSE_ERROR_FIELD_TOO_LONG`.
- `parser_parse` skips leading whitespace, so an indented directive is read.
- An empty file ends with an empty `flavors` list.

### What Java does

- The `section` rule in `FlavourGrammar.g4` is `kind` then `fixedOrFlavourBlock`, which is one or more flavours, and the `file`
  rule is one or more sections after the header. A bare `kind:`, a leading `flavor:` and `record-count:0` with no sections are
  hard errors, and so the whole file gives no items.
- `STRING` in `FlavourLexer.g4` stops at a colon, and the `flavour` rule takes `COLON STRING` only once, so a colon in the
  description, a trailing colon after the colour and any text after the glyph's colon each leave a `COLON` the grammar has no place
  for. All three are hard errors.
- `CommentsAndEol.g4` is shared, and does not skip indentation. The gap is written up under `RealmGrammar.g4` and `RealmLexer.g4`.
- Both `.g4` headers cite `src/init.c:4242-4249` for the directive table; `init_parse_flavor` has moved since, and no line number
  should be recorded.
- `FlavourReaderTest.aKindWithNoFlavoursIsAHardErrorHereWhereCAcceptsIt`, `aFlavourBeforeAnyKindIsAHardErrorHereWhereCAcceptsIt`,
  `aColonInTheDescriptionIsAHardErrorHereWhereCKeepsItInTheText`, `aTrailingColonAfterTheColourIsAHardErrorHereWhereCAcceptsIt`,
  `textAfterTheGlyphIsAHardErrorHereWhereCIgnoresIt`, `anIndentedLineIsAHardErrorHereWhereCSkipsLeadingWhitespace` and
  `anEmptyFileWithAZeroCountIsAHardErrorHereWhereCAcceptsIt` pin them.
- None on the shipped data: eight blocks, each with flavours, no colon in any description, no indentation. A description with a
  colon is the one most likely to be written by hand.

## `NamesGrammar.g4` and `NamesLexer.g4`: a `word:` before the first `section:`, a negative section and indented lines differ from C

Found 2026-10-18 during `/port backend/parser/NamesReader.java`. Not fixed.

- **A `word:` before any `section:`.** `init_parse_names` gives `names_parse` a running `section` that starts at 0, so
  `parse_names_word` in `init.c` files such a word under section 0. The `record` rule in `NamesGrammar.g4` starts with
  `section`, so the same file is a hard error here and loads as nothing.
  `NamesReaderTest.aWordBeforeAnySectionIsAHardErrorWhereCFilesItUnderSectionZero` pins it.
- **A negative section.** `section` is a `uint` in `init_parse_names`, and `parser_parse` in `parser.c` rejects a leading
  `-`. `INTEGER` in `NamesLexer.g4` takes an optional `-`, so `section:-1` reaches the assembler as `-1`.
  `NamesReaderTest.aNegativeSectionLoadsHereWhereCRejectsIt` pins it; the consequence is in `docs/Assembler_Issues.md`.
- **Indented lines and a last-line comment.** The same gap as the shared `CommentsAndEol.g4` entry above: an indented
  `word:` or `#` line is a hard error, and so is a comment on the last line with no newline.
  `NamesReaderTest.leadingWhitespaceBeforeADirectiveIsAHardErrorWhereCSkipsIt` pins it for `NamesLexer`.
- **Repeated sections.** C keeps one word list per section, so a second `section:1` adds to the first. The `file` rule
  makes one record per `section:` line, so the reader returns two records with section 1 and `MiscRegistry.setNames` joins
  them. Not a loss of data, but `record-count:` counts blocks, not distinct sections.
  `NamesReaderTest.aRepeatedSectionGivesTwoRecordsWhereCJoinsThemIntoOneList` pins it.
- None on the shipped data, which has two unindented sections in order and no word before the first.

## `HistoryGrammar.g4` and `HistoryLexer.g4`: a chart with no phrase, a second phrase, a negative chart, text after the roll and indentation differ from C

Found 2026-10-18 during `/port backend/parser/HistoryReader.java`. Not fixed.

### What C does

- `init_parse_history` registers `chart uint chart int next int roll` and `phrase str text`. `parse_history_chart` makes a new
  entry for every `chart:` line, and `parse_history_phrase` does `string_append` onto the newest entry, so a chart line with no
  phrase gives an entry with NULL text and a second `phrase:` line is joined onto the first.
- `parser_parse` in `parser.c` tokenizes the int fields on `:` and reads each with `strtol`/`strtoul` base 0 without checking where
  it stopped, so the text after the last field is dropped (`chart:1:0:10:extra` and `chart:1:0:10abc` both read roll 10), `0x10` is
  16 and `010` is 8. A `uint` with a leading `-` is `PARSE_ERROR_NOT_NUMBER`. Leading whitespace on a line is skipped. An empty
  `phrase:` is `PARSE_ERROR_MISSING_FIELD`. A file with no charts is accepted.

### What Java does

- The `record` rule in `HistoryGrammar.g4` is exactly one `chart` then one `phrase`, and `file` is one or more records after the
  header. A chart line with no phrase, a second `phrase:` line and a header with no records are hard errors, so the whole file
  gives no items.
- `INTEGER` in `HistoryLexer.g4` is an optional `-` and decimal digits, so `chart:-1:0:100` reaches the assembler as chart -1 where
  C rejects it, and anything after the roll (`:extra`, `abc`) or a hex roll is a hard error where C reads it.
- `CommentsAndEol.g4` is shared, and does not skip indentation. The gap is written up under `RealmGrammar.g4` and `RealmLexer.g4`.
- An empty `phrase:` is a hard error here as in C, and a phrase keeps its colons, leading spaces and trailing spaces, as C's `str`
  field does.
- `HistoryReaderTest.aChartWithNoPhraseBetweenTwoRecordsIsAHardErrorHereWhereCAcceptsIt`,
  `aSecondPhraseLineIsAHardErrorHereWhereCAppendsItToTheEntry`, `aNegativeChartNumberLoadsHereWhereCRejectsIt`,
  `textAfterTheRollIsAHardErrorHereWhereCIgnoresIt`, `aHexRollIsAHardErrorHereWhereCReadsItInBaseSixteen`,
  `anIndentedLineIsAHardErrorHereWhereCSkipsLeadingWhitespace` and
  `aHeaderWithNoRecordsIsAHardErrorHereWhereCAcceptsAnEmptyFile` pin them.
- None on the shipped data: 165 unindented chart/phrase pairs, each chart line ending at the roll. A phrase split over two
  `phrase:` lines is the one most likely to be written by hand.

## `BodyGrammar.g4`: a body with no `slot:` line, and a file with no bodies, are hard errors, C accepts both

Found 2026-10-18 during `/port backend/parser/BodyReader.java`. Not fixed.

### What C does

- `parse_body_body` in `init.c` allocates a body with `count` 0 and no slots, and nothing later requires a `slot:` line.
- `finish_parse_body` gives every body, including one with no slots, an array of `equip_slots_max` zeroed slots.
- A file with no `body:` line at all leaves `bodies` empty and loads without error.

### What Java does

- `BodyGrammar.g4`: the `bodyType` rule is `body (slot ...)+`, so a `body:` line with nothing after it is a syntax error.
- `BodyGrammar.g4`: the `file` rule is `recordCount (bodyType ...)+ EOF`, so a header with no bodies is a syntax error too.
- Both are hard errors: nothing loads and the messages come back.
- `BodyReaderTest.bodyWithNoSlotsIsAHardError` and `aHeaderWithNoBodiesIsAHardErrorHereWhereCAcceptsAnEmptyFile` pin them.

### Impact

- None on the shipped data: `body.txt` has one body of twelve slots.
- The comment in `body.txt` says each body "must take the form of" a name line followed by slot lines, so a slotless body is
  probably a mistake in a hand-edited file, and failing it is arguably safer than C. It is still a difference.

## `PlayerRaceGrammar.g4` and `PlayerRaceLexer.g4`: a race with only a name, a file with no races, space-separated flags and values, and text after a number differ from C

Found 2026-10-18 during `/port backend/parser/PlayerRaceReader.java`. Not fixed.

### What C does

- `parse_p_race_name` in `init.c` allocates a zeroed race, and none of the other handlers is required, so a `name:` line alone is a
  race with every field 0, no flags and a NULL history. A file with no `name:` line leaves `races` empty and loads without error.
- `parse_p_race_obj_flags`, `parse_p_race_play_flags` and `parse_p_race_values` split their text with `strtok(s, " |")`, so
  `obj-flags:SUST_STR REGEN` and `values:RES_ACID[1] RES_FIRE[1]` read both entries. An empty `obj-flags:` or `player-flags:` is
  accepted as no flags; an empty `values:` is `PARSE_ERROR_MISSING_FIELD`.
- `parser_parse` in `parser.c` reads each int with `strtol` base 0 and does not check where it stopped, so `hitdie:10abc` reads 10
  and `0x10` is 16. A `history:` is a `uint`, and a leading `-` is `PARSE_ERROR_NOT_NUMBER`. Leading whitespace on a line is skipped.

### What Java does

- `PlayerRaceGrammar.g4`: the `playerRace` rule is a `name` then one or more of the other lines, so `name:Empty` on its own is a
  syntax error, and the `file` rule is one or more races, so a header with no races is a syntax error too.
- `PlayerRaceLexer.g4`: `FLAG_OR` and `VALUE_OR` are an optional space, a `|`, and an optional space, so a space on its own between
  two flags or two values is a lexer error. An empty `values:` is a syntax error, as in C.
- `INTEGER` is an optional `-` and decimal digits, so text after a number and a hex number are hard errors. `history:-1` is lexed
  and then dropped by the assembler as an unknown chart, which also rejects it.
- `CommentsAndEol.g4` is shared, and does not skip indentation. The gap is written up under `RealmGrammar.g4` and `RealmLexer.g4`.
- All of these are hard errors: nothing loads and the messages come back.
- `PlayerRaceReaderTest.aNameOnlyRaceIsAHardErrorHereWhereCAcceptsIt`, `aHeaderWithNoRacesIsAHardErrorHereWhereCAcceptsAnEmptyFile`,
  `objectFlagsSeparatedByASpaceAreAHardErrorHereWhereCSplitsOnSpaceToo`,
  `playerFlagsSeparatedByASpaceAreAHardErrorHereWhereCSplitsOnSpaceToo`,
  `valuesSeparatedByASpaceAreAHardErrorHereWhereCSplitsOnSpaceToo`, `textAfterANumberIsAHardErrorHereWhereCIgnoresIt`,
  `aHexNumberIsAHardErrorHereWhereCReadsItInBaseSixteen` and `anIndentedLineIsAHardErrorHereWhereCSkipsLeadingWhitespace` pin them.

### Impact

- None on the shipped data: all eleven races have a full set of lines, use `|` between flags and values, and have no text after a
  number. A flag list written with spaces alone is the one most likely to be written by hand.

## `PlayerClassGrammar.g4` and `PlayerClassLexer.g4`: a class with only a name, a file with no classes, a `magic:` with no book, a book with no spell, space-separated flags, an empty flag list and a repeated `magic:` differ from C

Found 2026-10-18 during `/port backend/parser/PlayerClassReader.java`. Not fixed.

### What C does

- `parse_class_name` in `init.c` allocates a zeroed class, and none of the other handlers is required, so a `name:` line alone is a
  class with every field 0. A file with no `name:` line leaves `classes` empty and loads without error.
- `parse_class_obj_flags` and `parse_class_play_flags` split their text with `strtok(s, " |")`, so `player-flags:NO_MANA SHIELD_BASH`
  reads both flags, and the argument is `?str`, so an empty `obj-flags:` or `player-flags:` is accepted as no flags.
- `parse_class_magic` accepts a `magic:` line followed by no `book:` line, and `parse_class_book` accepts `spells` 0 with no
  `spell:` line after it. Every directive other than `book:`, `book-graphics:`, `book-properties:`, `spell:` and the effect lines may
  fall anywhere in the class, including between `magic:` and its first `book:`.
- A second `magic:` in the same class is `PARSE_ERROR_REPEATED_DIRECTIVE`.
- `parse_class_dice`, `parse_class_expr`, `parse_class_effect_yx` and `parse_class_effect_msg` return no error when the spell has no
  effect yet ("assume that this is human and not parser error").

### What Java does

- `PlayerClassGrammar.g4`: the `playerClass` rule is a `name` then one or more of the other lines, so `name:Empty` on its own is a
  syntax error, and the `file` rule is one or more classes, so a header with no classes is a syntax error too.
- `PlayerClassLexer.g4`: `FLAG_OR` is an optional space, a `|`, and an optional space, so a space on its own between two flags is a
  lexer error, and `FLAG_MODE` needs a `FLAG`, so an empty `obj-flags:` or `player-flags:` is a syntax error.
- `PlayerClassGrammar.g4`: `magicBlock` is `magic (bookBlock)+` and `bookBlock` ends in `(spellBlock)+`, so a `magic:` with no book,
  a book with no spell, and any line between `magic:` and its first `book:` are syntax errors.
- `PlayerClassGrammar.g4`: each `magicBlock` in a class overwrites `magicInit`, so a second `magic:` with its own books replaces the
  first without an error.
- `PlayerClassGrammar.g4`: `spellBlock` takes `dice:` and its siblings only through the imported `effectBlock` rule, so one with no
  `effect:` before it is a syntax error.
- All but the repeated `magic:` are hard errors: nothing loads and the messages come back.
- `PlayerClassReaderTest.aNameOnlyClassIsAHardErrorHereWhereCAcceptsIt`,
  `aHeaderWithNoClassesIsAHardErrorHereWhereCAcceptsAnEmptyFile`,
  `playerFlagsSeparatedByASpaceAreAHardErrorHereWhereCSplitsOnSpaceToo`,
  `objectFlagsSeparatedByASpaceAreAHardErrorHereWhereCSplitsOnSpaceToo`, `anEmptyFlagListIsAHardErrorHereWhereCAcceptsIt`,
  `aMagicLineWithNoBookIsAHardErrorHereWhereCAcceptsIt`, `aBookWithNoSpellLineIsAHardErrorHereWhereCAcceptsIt`,
  `aTitleBetweenMagicAndItsFirstBookIsAHardErrorHereWhereCAcceptsIt`, `aDiceLineWithNoEffectIsAHardErrorHereWhereCIgnoresIt` and
  `aRepeatedMagicLineReplacesTheFirstHereWhereCRejectsIt` pin them.

### Impact

- None on the shipped data: all nine classes have a full set of lines, use `|` between flags, put `book:` straight after `magic:`
  and give every book at least one spell. A flag list written with spaces alone, and a `magic:` repeated by a copy and paste, are
  the two most likely to be written by hand; the second is the only one that is silently accepted.

## `TrapGrammar.g4`, `TrapLexer.g4` and `EffectBlock.g4`: a bare `name:`, a colon in the description, space-only and empty flags, the extra-effect lines, spaces in the dice and an unknown colour name differ from C

Found 2026-10-18 during `/port backend/parser/TrapReader.java`. Not fixed.

### What C does

- `init_parse_trap` in `init.c` registers every line as its own handler. After `name:` they come in any order, and a record needs
  nothing but its `name:` line.
- `name` is `sym name str desc`, and a `str` takes the rest of the line, so `name:a:b: c` has the description `b: c`.
- `flags` is `?str flags` and `parse_trap_flags` runs `strtok(flags, " |")`, so the value may be missing, and a space alone
  separates two flags as well as `|`. `parse_trap_save_flags` splits `save` the same way.
- `parse_trap_effect_yx_xtra` sets the position of the last extra effect, and `parse_trap_dice`, `parse_trap_dice_xtra`,
  `parse_trap_expr` and `parse_trap_expr_xtra` return `PARSE_ERROR_NONE` without doing anything when there is no effect (or, for the
  two `expr` ones, no dice) to act on. `parse_trap_dice` and `parse_trap_expr` walk `t->effect`, so they reach the primary list even
  after an `effect-xtra:`.
- `effect-yx` and `dice` both act on the last effect, in either order.
- `init_parse_trap` registers no `effect-msg`, so C gives `PARSE_ERROR_UNDEFINED_DIRECTIVE`.
- `appear` is three `uint`s, and `parser_parse` rejects a leading minus.
- `visibility` is a `str` that `dice_parse_string` reads, and that function skips whitespace, so `2d10 M50` is `2d10M50`.
- `parse_trap_graphics` sends a colour longer than one character to `color_text_to_attr` in `z-color.c`, which compares with
  `my_stricmp` and answers white for a name it does not know.

### What Java does

- `TrapGrammar.g4`: `trapRecord` is a `name` then one or more of the other lines, so `name:a:b` on its own is a syntax error, and
  `file` is one or more records, so a header with no records is one too.
- `TrapLexer.g4`: in `DELIMITED_MODE`, `DELIMITED_STRING` stops at a colon, and `name` needs `DELIMITED_STRING DELIMITER_COLON
  DELIMITED_STRING`, so a colon in the description is a syntax error.
- `TrapLexer.g4`: `FLAG_OR` is an optional space, a `|` and an optional space, so a space on its own between two flags is a lexer
  error, and `FLAGS_MODE` needs a `FLAG`, so an empty `flags:` is a syntax error. `save:` takes the same path.
- `TrapGrammar.g4`: `effectXtraBlock` has no `effect-yx-xtra:`, and there is no token for it, so it is a syntax error. Every other
  line of the group reaches the grammar only through `effectBlock` (`EffectBlock.g4`) or `effectXtraBlock`, which open with
  `effect:` or `effect-xtra:`. A `dice:`, `dice-xtra:` or `expr:` with no effect of its own kind before it is a syntax error, and so
  is a `dice:` after an `effect-xtra:`. The `effectBlock` rule takes `effect-yx:` or `dice:` but not both, so both on one effect is
  a syntax error.
- `EffectBlock.g4`: `effectBlock` ends in an optional `effect-msg:`, so a trap accepts a directive C does not have.
- `TrapLexer.g4`: `DELIMITED_INTEGER` is `'-'? ('0'..'9')+`, so `appear:-1:-2:-3` loads as -1, -2 and -3.
- `EffectBlockLexer.g4`: `DICE_STRING_MODE` has no rule for whitespace inside the value, so `visibility:2d10 M50` is a lexer error.
- `AngbandDisplayCharacter.g4`: `COLOUR_VALUES` is built from C's colour characters and names, so `graphics:^:nonsense` is a lexer
  error. C loads it and draws white.
- All of these are hard errors except the negative `appear:` and `effect-msg:`: nothing loads and the messages come back.
- `TrapReaderTest.aNameOnlyRecordIsAHardErrorHereWhereCAcceptsIt`, `aHeaderWithNoRecordsIsAHardErrorHereWhereCAcceptsAnEmptyFile`,
  `aColonInTheNameDescriptionIsAHardErrorHereWhereCTakesTheRestOfTheLine`,
  `flagsSeparatedByASpaceAloneAreAHardErrorHereWhereCSplitsOnSpaceToo`,
  `saveFlagsSeparatedByASpaceAloneAreAHardErrorHereWhereCSplitsOnSpaceToo`, `anEmptyFlagsLineIsAHardErrorHereWhereCAcceptsIt`,
  `effectYxXtraIsAHardErrorHereWhereCAcceptsIt`, `aDiceLineBeforeAnyEffectIsAHardErrorHereWhereCIgnoresIt`,
  `aDiceXtraLineBeforeAnyExtraEffectIsAHardErrorHereWhereCIgnoresIt`, `anExprLineWithNoDiceBeforeItIsAHardErrorHereWhereCIgnoresIt`,
  `aDiceLineAfterAnExtraEffectIsAHardErrorHereWhereCReachesThePrimaryEffect`,
  `effectYxAndDiceOnTheSameEffectAreAHardErrorHereWhereCTakesBoth`, `effectMsgIsAcceptedHereWhereCHasNoSuchDirective`,
  `aNegativeAppearValueLoadsHereWhereCRejectsIt`, `aSpaceInsideTheVisibilityDiceIsAHardErrorHereWhereCSkipsIt` and
  `anUnknownColourNameIsAHardErrorHereWhereCDrawsItWhite` pin them.

### Impact

- None on the shipped data: all forty records have a full set of lines, a colon-free description, `|` between flags, every
  `dice:` straight after its `effect:`, and a colour from C's table.
- A flag list written with spaces alone and a description with a colon are the two most likely to be written by hand, and both
  lose the whole file.
- A modded file with `effect-msg:` or a negative `appear:` loads in Java and is rejected by C.
- Indentation is the shared `CommentsAndEol.g4` gap under "Leading whitespace on a line" above;
  `TrapReaderTest.anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent` pins it for `TrapLexer`.
- `EffectBlock.g4` is shared; the `MonsterSpellGrammar.g4`, `ActivationsGrammar.g4` and `ShapeGrammar.g4` entries above describe the
  same block.

## `PlayerPropertyGrammar.g4` and `PlayerPropertyLexer.g4`: a fixed line order, a required `name:`, repeated and split lines, an unknown `type:`, narrow `bindui:` symbols and base-10 numbers differ from C

Found 2026-10-18 during `/port PlayerPropertyReader.java`. Not fixed.

### What C does

- `init_parse_player_prop` in `init.c` registers `type`, `code`, `desc`, `name`, `value` and `bindui` as six handlers. After a `type:` they
  come in any order, any number of times, and a record needs nothing but its `type:` line.
- `parse_player_prop_code`, `parse_player_prop_name` and `parse_player_prop_value` overwrite their field, so a repeat keeps the last.
  `parse_player_prop_desc` calls `string_append`, so `desc:` lines add on to each other wherever they sit in the record.
- `type` is `str`, so `parse_player_prop_type` stores any text. `finish_parse_player_prop` treats everything but `element` as one property.
- `bindui` is `sym ui int aux sym uival`, and `parser_parse` in `parser.c` splits a `sym` or `int` on `:` and takes it whole, so `ui` can
  be any text without a colon, `<HOLY_ORB>` or lower case included.
- An `int` is read with `strtol(tok, &z, 0)` and only `z == tok` is an error. `0x3` is 3, `010` is 8 and `1x` is 1.
- Leading whitespace on a line is skipped.
- An empty `name:` or `desc:` has no field for `strtok`, so it is `PARSE_ERROR_MISSING_FIELD`, a hard error.

### What Java does

- `PlayerPropertyGrammar.g4`: `property` is `type`, an optional `code`, `bindUI`, `name`, an optional `desc` and an optional `value`, in that
  order. A `bindui:` after the `name:`, a `desc:` before it and a `value:` before the `desc:` are syntax errors.
- A record with no `name:` is a syntax error, and so is a second `code:`, `name:` or `value:`. The `desc` rule is a run of `DESC STRING`, so
  a `desc:` that has another line between it and the last is a syntax error where C appends it.
- `file` is `recordCount` and one or more `property`, so `record-count:0` with no records is a syntax error where C loads an empty file.
- `PlayerPropertyLexer.g4`: `TYPE_VALUE` is `player`, `object` or `element`, so any other `type:` is a syntax error.
- `LCASE` is `[a-z0-9_]+` and `TAG` is `<` and upper-case letters only, so `bindui:A_B`, `bindui:a-b`, `bindui:a_b<dark>` and
  `bindui:a_b<HOLY_ORB>` are lexer errors. The last matters for a modded file: `HOLY_ORB` is a real element.
- `INTEGER` is `'-'? [0-9]+`, so `value:0x3` and `value:1x` are syntax errors.
- Indentation is the shared `CommentsAndEol.g4` gap under "Leading whitespace on a line" above.
- All of these are hard errors: nothing loads and the messages come back. An empty `name:` or `desc:` and a `value:` or `aux` that is not a
  number are hard errors in both.
- The comment at the top of both `.g4` files is stale: its note 1 says `property` matches `bindui` with `?`, but the rule is `bindUI`, which
  is `binduiEntry*`, so several `bindui:` lines in a row load. It also cites C line numbers.
- `PlayerPropertyReaderTest.aBindingAfterTheNameIsAHardErrorHereWhereCAcceptsAnyOrder`,
  `aDescBeforeTheNameIsAHardErrorHereWhereCAcceptsAnyOrder`, `aValueBeforeTheDescIsAHardErrorHereWhereCAcceptsAnyOrder`,
  `aRecordWithNoNameIsAHardErrorHereWhereCAcceptsIt`, `aRepeatedNameIsAHardErrorHereWhereCKeepsTheLast`,
  `aRepeatedValueIsAHardErrorHereWhereCKeepsTheLast`, `aRepeatedCodeIsAHardErrorHereWhereCKeepsTheLast`,
  `aDescSplitByAnotherLineIsAHardErrorHereWhereCAppendsIt`, `aHeaderWithNoRecordsIsAHardErrorHereWhereCAcceptsAnEmptyFile`,
  `anUnknownTypeIsAHardErrorHereWhereCTakesAnyText`, `anUpperCaseBindingNameIsAHardErrorHereWhereCTakesAnySymbol`,
  `aBindingNameWithAHyphenIsAHardErrorHereWhereCTakesAnySymbol`, `aBindingTagWithAnUnderscoreIsAHardErrorHereWhereCTakesAnySymbol`,
  `aLowerCaseBindingTagIsAHardErrorHereWhereCTakesAnySymbol`, `anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent`,
  `aHexadecimalValueIsAHardErrorHereWhereCReadsBaseZero` and `textAfterTheValueNumberIsAHardErrorHereWhereCIgnoresIt` pin them.

### Impact

- None on the shipped data: all 44 records have a `name:`, put their lines in the grammar's order, repeat nothing, and use lower-case
  symbols, upper-case tags and plain decimal numbers.
- The order and the missing `name:` are the likeliest to bite a hand-edited file, and each loses the whole file.
- A tag with an underscore only matters if a `bindui:` is written with an explicit `<HOLY_ORB>`; the shipped file leaves the tag to the
  expansion, which builds it from the element code in the assembler.
- Indentation is the shared `CommentsAndEol.g4` gap;
  `PlayerPropertyReaderTest.anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent` pins it for `PlayerPropertyLexer`.

## `TerrainFeatureGrammar.g4` and `TerrainFeatureLexer.g4`: a fixed three-line header, a repeated `graphics:`, replaced message lines, space-only and empty flags, base-10 numbers with nothing after them, a second field after `resist-flag:` and an unknown colour name differ from C

Found 2026-10-19 during `/port TerrainReader.java`. Not fixed.

### What C does

- `init_parse_feat` in `init.c` registers sixteen handlers. A record starts at `code:`, which picks the `f_info` slot, and every other
  line needs only a slot to act on, so `code:FLOOR` on its own is a record and the lines after it come in any order.
- `parse_feat_graphics` assigns, so a second `graphics:` replaces the first.
- `parse_feat_desc`, `parse_feat_walk_msg`, `parse_feat_run_msg`, `parse_feat_hurt_msg`, `parse_feat_die_msg`,
  `parse_feat_confused_msg`, `parse_feat_look_prefix` and `parse_feat_look_in_preposition` all call `string_append`, so repeated lines join
  with nothing between them. `parse_feat_name` is the one that refuses a repeat, with `PARSE_ERROR_REPEATED_DIRECTIVE`.
- `flags` is `?str flags` and `parse_feat_flags` runs `strtok(flags, " |")`, so the value may be missing, and a space alone separates two
  flags as well as `|`.
- `priority` is a `uint` and `digging` an `int`, both read with `strtoul` or `strtol` in base 0 and checked only for `z == tok`, so `0x3`
  is 3, `3x` is 3, and nothing looks at what follows the last field on the line. `resist-flag` is a `sym`, tokenised on a colon, so
  `RAND_50:x` is `RAND_50`.
- `parse_feat_graphics` sends a colour longer than one character to `color_text_to_attr` in `z-color.c`, which answers white for a name it
  does not know.
- Leading whitespace on a line is skipped, as under "Leading whitespace on a line" above.
- A file with only a header runs `finish_parse_feat` over the zeroed `f_info` table and loads.

### What Java does

- `TerrainFeatureGrammar.g4`: `terrain` is `code`, `name` and `graphics` in that order, then the optional lines. A record missing any of the
  three, or with them in another order, is a syntax error, and so is a second `graphics:`, because the optional group has no alternative for
  it.
- `TerrainFeatureGrammar.g4`: `file` is `recordCount` and one or more `terrain`, so a header with no records is a syntax error.
- `TerrainFeatureGrammar.g4`: `walk_message`, `run_message`, `hurt_message`, `die_message`, `confused_message`, `look_prefix` and
  `look_in_preposition` assign into the record, so the last line wins. `desc` is the one that adds (`descInit + $desc.descStr`), as C does.
  `priority`, `digging`, `mimic` and `resist_flag` assign, as C does.
- `TerrainFeatureLexer.g4`: `FLAG_OR` is an optional space, a `|` and an optional space, so two flags with only a space between them are a
  lexer error, and `FLAG_MODE` needs a `FLAG`, so an empty `flags:` is a syntax error.
- `TerrainFeatureLexer.g4`: `INTEGER` is `'-'? ('0'..'9')+`, so `priority:0x3` and `priority:3x` are lexer errors. `digging:2:junk` and
  `resist-flag:RAND_50:x` are lexer errors too, because the default mode has no rule for a colon there and `FLAG_EOL` wants a newline.
- `AngbandDisplayCharacter.g4`: `COLOUR_VALUES` is built from C's colour characters and names, so `graphics:.:nonsense` is a lexer error.
  C loads it and draws white.
- Indentation is the shared `CommentsAndEol.g4` gap under "Leading whitespace on a line" above.
- All of these are hard errors except the replaced message lines: nothing loads and the messages come back.
- `TerrainReaderTest.aRecordWithOnlyACodeIsAHardErrorHereWhereCAcceptsIt`, `aRecordWithNoGraphicsIsAHardErrorHereWhereCAcceptsIt`,
  `aRecordWithNoNameIsAHardErrorHereWhereCAcceptsIt`, `graphicsBeforeTheNameIsAHardErrorHereWhereCAcceptsAnyOrder`,
  `aRepeatedGraphicsLineIsAHardErrorHereWhereCKeepsTheLast`, `repeatedMessageLinesReplaceEachOtherHereWhereCAppendsThem`,
  `repeatedLookLinesReplaceEachOtherHereWhereCAppendsThem`, `flagsSeparatedByASpaceAloneAreAHardErrorHereWhereCSplitsOnSpaceToo`,
  `anEmptyFlagsLineIsAHardErrorHereWhereCAcceptsIt`, `aHeaderWithNoRecordsIsAHardErrorHereWhereCAcceptsAnEmptyFile`,
  `aHexadecimalPriorityIsAHardErrorHereWhereCReadsBaseZero`, `textAfterThePriorityNumberIsAHardErrorHereWhereCIgnoresIt`,
  `aSecondFieldAfterTheDiggingNumberIsAHardErrorHereWhereCIgnoresIt`, `aSecondFieldAfterTheResistFlagIsAHardErrorHereWhereCIgnoresIt`,
  `anUnknownColourNameIsAHardErrorHereWhereCDrawsItWhite` and `anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent` pin them.

### Impact

- None on the shipped data: all 25 records have `code:`, `name:` and `graphics:` in that order, repeat no message line, put `|` between
  flags, and use plain decimal numbers and a colour from C's table.
- The line order and a missing `name:` or `graphics:` are the likeliest to bite a hand-edited file, and each loses the whole file.
- A repeated message line in a modded file keeps only the last line in Java, and C would have joined them; the look strings then get their
  one trailing space from `TerrainFeatureAssembler` either way.
- `lib/gamedata/terrain.txt`: it differs from the upstream file in the `record-count:25` header with the blank line after it, one trailing
  space on a comment, and the trailing space on the lava `walk-msg:` and `run-msg:` lines. C keeps the trailing space as part of the message.
  `TerrainReaderTest.theShippedFileDiffersFromUpstreamInTheHeaderAndThreeTrailingSpaces` pins the header and the two lava lines.
- `.idea/misc.xml`: the `TerrainFeatureLexer.g4` and `TerrainFeatureGrammar.g4` entries have an empty `outputDir` and `pkg`, though the
  generated parser sits in `grammars/terrainfeature`. The committed parser has the same ATN as a fresh generation.

## `WorldGrammar.g4` and `WorldLexer.g4`: a required `record-count:` header and at least one level, no fields after `down`, names of letters, digits and spaces, and base-10 depths differ from C

Found 2026-10-19 during `/port WorldReader.java`. Not fixed.

### What C does

- `init_parse_world` in `init.c` registers one directive, `level int depth sym name sym up sym down`, handled by `parse_world_level`.
  There is no header, so an empty `world.txt` loads an empty world.
- Each field is cut at `:` by `strtok`. A `sym` is any text without a colon, and nothing after the last field is looked at.
- An `int` is read by `strtol(tok, &z, 0)`: base zero, so `0x10` is 16 and `010` is 8, with leading space skipped and trailing text ignored.
  Only a field with no digits at all is `PARSE_ERROR_NOT_NUMBER`.

### What Java does

- `WorldGrammar.g4`: `file` is `recordCount (line)+ EOF`. A file with no header, or a header and no levels (`record-count:0`), or an
  empty file, is a syntax error. Nothing loads.
- `line` ends at `upAndDown`, so `level:0:Town:None:Angband 1:extra` is a syntax error where C ignores `extra`.
- `WorldLexer.g4`: `NAME` is letters, digits and spaces, so `Hell-hole` or `Angband 1'` is a lexer error where C keeps it.
- `INTEGER` is `'-'? ('0'..'9')+`, so `level:0x10:...` is a syntax error and `010` is 10, not 8.
- A depth outside `int` is a hard error from `WorldReader`; C clamps it to a `long` and truncates it to `int`.
- `WorldReaderTest.aHeaderWithNoLevelsIsHardHereWhereCLoadsAnEmptyWorld`, `anEmptyFileIsHardHereWhereCLoadsAnEmptyWorld`,
  `aMissingHeaderIsAHardErrorHereWhereCHasNone`, `aFieldAfterTheDownNameIsHardHereWhereCIgnoresIt`,
  `aHyphenInANameIsHardHereWhereCTakesAnySymExceptColon`, `aHexadecimalDepthIsHardHereWhereCReadsBaseZero` and
  `aLeadingZeroDepthIsReadAsDecimalWhereCReadsItAsOctal` pin them.

### Impact

- None on the shipped data: all 128 levels have plain decimal depths, names of letters, digits and spaces, and four fields.
- `lib/gamedata/world.txt` differs from the upstream file in the `record-count:128` header and the comment above it.
- A modded file that drops the header, uses a hyphen or apostrophe in a level name, or ends up with no levels loses the whole file.

## `GameConstantsGrammar.g4` and `GameConstantsLexer.g4`: lines with a field after the value, indents, hex and octal values, lower-case message names and a last-line comment differ from C

Found 2026-10-19 during `/port GameConstantsReader.java`. Not fixed.

### What C does

- `init_parse_constants` in `init.c` registers one shape per category, `<category> sym label int value`, and four critical-level
  shapes ending in `str msg`. `parse_line` in `parser.c` reads only the fields a directive registers, so anything after the value is
  ignored.
- `parse_line` skips leading whitespace, and a line whose first non-blank character is `#` is a comment, with or without a newline.
- An `int` is read by `strtol(tok, &z, 0)`: base zero, so `0x10` is 16 and `010` is 8, and only a field with no digits is
  `PARSE_ERROR_NOT_NUMBER`.
- `msg` is a `str`, the rest of the line, and `message_lookup_by_name` compares with `my_stricmp`, so `hit_good` resolves. It also
  accepts a number below `MSG_MAX`.

### What Java does

- `GameConstantsGrammar.g4`: `line` is a name followed by `(COLON field)+`, and `GameConstantsAssembler` then insists on an exact field
  count, so `world:max-depth:5:6` is a soft error where C loads 5.
- `CommentsAndEol.g4`: `EOL` skips spaces only before the newline, so a line that starts with spaces or a tab is a lexer error, and
  `COMMENT` needs a closing `\n`, so a comment on the last line with no newline is a lexer error.
- `GameConstantsLexer.g4`: `INTEGER` is `'-'? ('0'..'9')+`, so `0x10` is a syntax error and `010` is 10, not 8. A run of digits too long
  for an `int` passes the lexer and fails in `GameConstantsAssembler.coerceInt`.
- `GC_MSG` is upper-case letters and underscores, so `hit_good` is a syntax error and a numeric message such as `12` is read as an
  `INTEGER` and then fails the `MessageType` lookup.
- `GC_NAME` is lower-case letters and hyphens only, so an upper-case category is a syntax error where C reports an undefined directive;
  both refuse the file.
- `GameConstantsReaderTest.aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt`, `aHexadecimalValueIsRefusedHereWhereCReadsBaseZero`,
  `aLeadingZeroValueIsReadAsDecimalWhereCReadsItAsOctal`, `anIndentedLineIsRefusedHereWhereCSkipsTheIndent`,
  `aLowerCaseMessageNameIsRefusedHereWhereCIsCaseBlind` and `aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt` pin them.

### Impact

- None on the shipped data: `lib/gamedata/constants.txt` has no indented lines, no hex or octal values, upper-case message names, and a
  newline after every comment.
- A hand-edited or modded file that indents a line, ends on a comment with no newline, or writes a message name in lower case loses the
  whole file, and the lexer message does not say why.
- The header comments of both grammars still cite C line numbers (`init.c:1051`, `init.c:949-980`), and the lexer's `GC_MSG` comment
  names `GameConstantsReader` as the place that checks the message, which is `GameConstantsAssembler`.

## `QuestGrammar.g4` and `QuestLexer.g4`: the grammar fixes the directive order and count, and the lexer shares the comment, indent and number differences

Found 2026-10-19 during `/port QuestReader.java`. Not fixed.

### What C does

- `init_parse_quest` in `player-quest.c` registers four independent one-line shapes: `name str`, `level uint`, `race str` and `number uint`.
  `parse_quest_name` allocates a new quest and makes it current; the other three fill in the current quest, so they can come in any order
  after a `name:`, can repeat (the last wins), and can be left out (the field stays zero, and a missing `race:` leaves a null race).
  A `level:`, `race:` or `number:` before any `name:` trips an `assert`.
- `finish_parse_quest` accepts any number of quests, including none, and sets `z_info->quest_max` to the count.
- `parser_parse` in `parser.c` skips leading whitespace and a `#` comment with or without a final newline, tokenizes an `uint` on `:` so
  anything after the value is ignored, and reads an `uint` with `strtoul` in base zero, so `0x10` is 16 and `010` is 8. A `str` is the rest
  of the line, and an empty one is `PARSE_ERROR_MISSING_FIELD`.

### What Java does

- `QuestGrammar.g4`: `file` is `recordCount (quest)+ EOF` and `quest` is `name level race number` in that order. Out of order, a missing
  or repeated directive, or a header with no quest after it is a syntax error, and `GrammarDriver.run` returns no quests at all.
- `QuestLexer.g4` and `CommentsAndEol.g4`: an indented line and a last-line comment with no newline are lexer errors, a field after the value
  is a syntax error, `0x10` is a syntax error and `010` is read as 10 (the assembler documents the decimal-only choice).
- The shared `NAME` and `RACE` modes take the rest of the line after `name:` and `race:` with colons kept, as C does, and an empty value is
  refused by both.
- `QuestReaderTest.directivesInAnotherOrderAreRefusedHereWhereCAcceptsThem`, `aMissingDirectiveIsRefusedHereWhereCLeavesTheFieldZero`,
  `aRepeatedDirectiveIsRefusedHereWhereCTakesTheLastOne`, `aFileWithNoQuestsIsRefusedHereWhereCLoadsAnEmptyList`,
  `aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt`, `anIndentedLineIsRefusedHereWhereCSkipsTheIndent`,
  `aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt`, `aHexadecimalLevelIsRefusedHereWhereCReadsBaseZero` and
  `aLeadingZeroLevelIsReadAsDecimalWhereCReadsItAsOctal` pin them.

### Impact

- None on the shipped data: `lib/gamedata/quest.txt` has the header and two records in the grammar's order, with no indents and a newline
  after every line.
- A hand-edited or modded `quest.txt` that reorders, omits or repeats a line, or empties the list, loses the whole file in Java, and
  `WorldDataLoader.loadQuests` then stops start-up. C loads such a file, with zero fields where lines are missing.

## `VaultGrammar.g4` and `VaultLexer.g4`: the grammar fixes the directive order and count, `min-depth:` and `max-depth:` take a minus sign, and the lexer shares the comment, indent and number differences

Found 2026-10-19 during `/port VaultReader.java`. Not fixed.

### What C does

- `init_parse_vault` in `generate.c` registers nine independent one-line shapes: `name str`, `type str`, `rating int`, `rows uint`,
  `columns uint`, `min-depth uint`, `max-depth uint`, `flags str` and `D str`. `parse_vault_name` allocates a new vault and makes it
  current; the other eight fill in the current vault, so they can come in any order after a `name:`, can repeat, and can be left out.
  A directive before any `name:` is `PARSE_ERROR_MISSING_RECORD_HEADER`.
- `parse_vault_rows` and `parse_vault_columns` look the vault's `type` up in `room_builders`, so they do need `type:` first, but nothing
  else is order-dependent except the `D:` length check, which needs `columns:`.
- `run_parse_vault` accepts a file with no vaults and leaves `vaults` null.
- `parser_parse` in `parser.c` skips leading whitespace and a `#` comment with or without a final newline, tokenizes an `uint` on `:` so
  anything after the value is ignored, and reads an `uint` with `strtoul` in base zero, so `0x10` is 16. It fails with
  `PARSE_ERROR_NOT_NUMBER` when no digits were read or when the token starts with `-`.

### What Java does

- `VaultGrammar.g4`: `file` is `recordCount (profileRecord)+ EOF` and `profileRecord` is `name type rating rows columns minDepth maxDepth`,
  any number of `flag`, then one or more `d`, in that order. Out of order, a missing directive, or a header with no record after it is a
  syntax error, and `GrammarDriver.run` returns no vaults at all. The `record-count:` header is a Java-only addition that the upstream
  `vault.txt` does not carry.
- `VaultLexer.g4`: `INTEGER` is `'-'? ('0'..'9')+` for every numeric directive, so `min-depth:-5` and `max-depth:-5` lex and the
  assembler loads them as negative depths, where C refuses the file. `rating:` is the one `int` and rightly takes a minus sign. A maximum
  of `-5` is not rewritten, since only a declared 0 becomes the world maximum.
- `VaultLexer.g4` and `CommentsAndEol.g4`: an indented directive is a lexer error, `rows:0x3` is a syntax error, and a field after a
  value is a syntax error.
- `VaultReaderTest.negativeDepthsLoadHereWhereCRefusesThem`, `typeAfterRowsIsRefusedHereWhereCTakesItInAnyOrder`,
  `aFileWithNoVaultsIsRefusedHereWhereCLoadsAnEmptyList`, `anIndentedDirectiveIsRefusedHereWhereCSkipsTheIndent` and
  `aHexadecimalRowCountIsRefusedHereWhereCReadsBaseZero` pin them.

### Impact

- None on the shipped data: `lib/gamedata/vault.txt` has 162 records in the grammar's order, every depth zero or more, no indents and no
  hex values.
- A hand-edited or modded `vault.txt` that reorders, omits or indents a directive, or empties the list, loses the whole file in Java, and
  `DungeonLoader.loadVaultTemplates` then stops start-up. A negative depth loads where C would refuse it.

## `RoomProfileGrammar.g4` and `RoomProfileLexer.g4`: the grammar fixes the directive order and count, `flags:` takes one `|`-separated line, `type:` and `doors:` take a minus sign, and the lexer shares the comment, indent and number differences

Found 2026-10-19 during `/port RoomProfileReader.java`. Not fixed.

### What C does

- `init_parse_room` in `generate.c` registers nine independent one-line shapes: `name str`, `type uint`, `rating int`, `rows uint`,
  `columns uint`, `doors uint`, `tval sym`, `flags str` and `D str`. `parse_room_name` allocates a new template and makes it current; the
  other eight fill in the current template, so they can come in any order after a `name:`, can repeat, and can be left out. A directive
  before any `name:` is `PARSE_ERROR_MISSING_RECORD_HEADER`.
- `parse_room_flags` ORs into `t->flags`, so a second `flags:` line adds to the first, and it splits the text with `strtok(s, " |")`, so a
  space on its own separates two flags.
- `tval` is a `sym`, tokenized on `:`, so `tval:rod:junk` reads `rod`.
- `run_parse_room` accepts a file with no templates and leaves `room_templates` null.
- `parser_parse` in `parser.c` skips leading whitespace and a `#` comment with or without a final newline, tokenizes an `int` or `uint` on
  `:` so anything after the value is ignored, and reads a `uint` with `strtoul` in base zero, so `0x3` is 3. It fails with
  `PARSE_ERROR_NOT_NUMBER` when no digits were read or, for a `uint`, when the token starts with `-`. A `str` field that is empty is
  `PARSE_ERROR_MISSING_FIELD`, which the Java grammar also refuses.

### What Java does

- `RoomProfileGrammar.g4`: `file` is `recordCount (roomProfile)+ EOF` and `roomProfile` is `name type rating rows columns doors tval`, at most
  one `flags`, then one or more `D` lines, in that order. Out of order, a missing directive, a repeated `flags:` or a header with no record
  after it is a syntax error, and `GrammarDriver.run` returns no templates at all. The `record-count:` header is a Java-only addition that the
  upstream `room_template.txt` does not carry.
- `RoomProfileLexer.g4`: in flag mode `FLAG_OR` is `' '? '|' ' '?`, so `flags:FEW_ENTRANCES FEW_ENTRANCES` is a lexer error. `TVAL`
  hands the whole rest of the line to `REST_OF_LINE`, so `rod:junk` reaches the assembler as the name and the record is dropped.
- `RoomProfileLexer.g4`: `INTEGER` is `'-'? ('0'..'9')+` for every numeric directive, so `type:-1` and `doors:-1` lex and load as -1, where C
  refuses the file. `rows:-1` and `columns:-1` are caught by the assembler's size check.
- `RoomProfileLexer.g4` and `CommentsAndEol.g4`: an indented directive is a lexer error, `rows:0x3` is a syntax error, a field after a
  value (`type:1:7`) is a syntax error, and a `#` comment on the last line with no newline is a lexer error.
- `RoomProfileReaderTest.directivesInAnotherOrderAreRefusedHereWhereCAcceptsThem`, `aRepeatedFlagsLineIsRefusedHereWhereCAccumulatesIt`,
  `flagsSeparatedByASpaceAloneAreRefusedHereWhereCSplitsOnIt`, `aFileWithNoTemplatesIsRefusedHereWhereCLoadsAnEmptyList`,
  `anIndentedDirectiveIsRefusedHereWhereCSkipsTheIndent`, `aHexadecimalRowCountIsRefusedHereWhereCReadsBaseZero`,
  `aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt`, `aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt`,
  `textAfterATvalNameIsTreatedAsPartOfTheNameHereWhereCDropsIt` and `aNegativeTypeAndDoorCountLoadHereWhereCRefusesThem` pin them.

### Impact

- None on the shipped data: `lib/gamedata/room_template.txt` has 500 records in the grammar's order, `flags:` only as a single
  `FEW_ENTRANCES`, no indents, no hex values, no negative numbers and a newline after every line.
- A hand-edited or modded `room_template.txt` that reorders, omits or indents a directive, repeats or space-separates `flags:`, or empties
  the list, loses the whole file in Java, and the loader then stops start-up. A negative `type:` or `doors:` loads where C refuses the file.

### Question for Rowan

- `flags:` is the one directive with a free-form value in this file, and C lets a modder write several lines or use spaces. Is the single
  `|`-separated line a deliberate choice for this file (only `FEW_ENTRANCES` exists), as the same lexer shape is for `vault.txt`?

## `DungeonProfileGrammar.g4`: no empty list, a bare `name:` is refused, and the shared indent, base-zero and trailing-field cases

Found 2026-10-19 during `/port DungeonProfileReader.java`. Not fixed.

### What C does

- `init_parse_profile` in `generate.c` registers seven independent directives. Only `name:` creates a record
  (`parse_profile_name`); the other six fill in the current one, so they can come in any order after a `name:`, can repeat (the last
  wins), and can be left out. A directive before any `name:` is `PARSE_ERROR_MISSING_RECORD_HEADER`.
- A `name:` with nothing after it is a valid record, zeroed apart from its name.
- `run_parse_profile` accepts a file with no records, and `finish_parse_profile` then sets `z_info->profile_max` to 0.
- `parser_parse` in `parser.c` skips leading whitespace, reads an `int` with `strtol` in base zero (so `0x10` is 16 and `010` is 8),
  and ignores anything after the value up to the next `:`. A missing field is `PARSE_ERROR_MISSING_FIELD`.

### What Java does

- `DungeonProfileGrammar.g4`: `file` is `recordCount (profile)+ EOF`, so a file with the header and no profile is a syntax error and
  `GrammarDriver.run` returns no profiles at all. The `record-count:` header is a Java-only addition that the upstream
  `dungeon_profile.txt` does not carry.
- `DungeonProfileGrammar.g4`: `profile` is a `name` followed by `( ... )+`, so a `name:` with no directive after it is a syntax error
  and the whole file is lost.
- `DungeonProfileLexer.g4`: `INTEGER` is `'-'? ('0'..'9')+`, so `alloc:0x10` and a field after the value (`alloc:5:junk`) are errors, and an
  indented directive (`  alloc:5`) is a lexer error.
- A directive before any `name:` and a `params:` line with a field missing are refused, as in C.
- `DungeonProfileReaderTest.aFileWithNoProfilesIsRefusedHereWhereCLoadsAnEmptyList`, `aProfileWithNoDirectivesFailsClosed`,
  `hexIndentedAndTrailingFieldValuesAreRefusedHereWhereCAcceptsThem`, `aDirectiveBeforeAnyNameFailsClosedAsInC` and
  `aParamsLineWithAFieldMissingFailsClosedAsInC` pin them.

### Impact

- None on the shipped data: `lib/gamedata/dungeon_profile.txt` has 9 profiles, each with `params:` and `alloc:`, no indents, no hex values and
  nothing after a value.
- A modded file that empties the list, leaves a `name:` bare, indents a line or writes a value in hex loses the whole file in Java, and the
  loader then stops start-up, where C would load it.

### Question for Rowan

- Is requiring at least one directive after each `name:` deliberate? C allows it, but a profile with no `params:` has a block size of 0 and
  no use, so it may be worth refusing on purpose; if so the grammar comment should say so.

## `ChestTrapGrammar.g4` and `ChestTrapLexer.g4`: the file needs a `record-count:` header and a record, `code:` must follow `name:`, and a repeated `msg:` overwrites where C appends

Found 2026-10-19 during `/port ChestTrapReader.java`. Not fixed.

### What C does

- `init_parse_chest_trap` in `obj-chest.c` registers ten independent directives and no `record-count:`. Only `name:` creates a record
  (`parse_chest_trap_name`); the rest fill in the current one in any order, can repeat, and can be left out. A directive before any
  `name:` is `PARSE_ERROR_MISSING_RECORD_HEADER`.
- `parse_chest_trap_msg` and `parse_chest_trap_msg_death` use `string_append`, so a repeated `msg:` or `msg-death:` line is joined
  onto the text already held, with no separator.
- `parse_chest_trap_dice` and `parse_chest_trap_expr` return `PARSE_ERROR_NONE` when the trap has no effect yet (and, for `expr:`, no
  dice yet), "assuming that this is human and not parser error". The line is dropped and the file still loads.
- `run_parse_chest_trap` accepts a file with no records and leaves `chest_traps` NULL.
- `parser_parse` in `parser.c` reads an `int` with `strtol` in base zero (so `0x19` is 25), skips leading whitespace, and ignores
  anything after the value up to the next `:`.

### What Java does

- `ChestTrapGrammar.g4`: `file` is `recordCount (chestTrap)+ EOF`. The upstream `chest_trap.txt` has no header, so it is a syntax
  error here as it stands; `lib/gamedata/chest_trap.txt` has `record-count:7` added. A header with no trap is also a syntax error.
- `ChestTrapGrammar.g4`: `chestTrap` is `name code (...)*`, so a `name:` not followed at once by `code:` is a syntax error and the
  whole file is lost. C would keep a record with a NULL code.
- `ChestTrapGrammar.g4`: `msg` and `msgDeath` assign to a single local, so the last line wins. A modded record with two `msg:` lines
  reads the second only; C reads both joined.
- `ChestTrapGrammar.g4`: `dice:` and `expr:` belong to the `effectBlock` rule, so one before any `effect:` line is a syntax error
  rather than a dropped line.
- `ChestTrapLexer.g4`: `INTEGER` takes no hex or octal, so `level:0x19` is an error, and an indented directive or a field after the
  value is an error too.
- `ChestTrapReaderTest.theUpstreamFileWithNoHeaderIsRefusedHereWhereCLoadsIt`,
  `aFileWithAHeaderAndNoTrapsIsRefusedHereWhereCLoadsAnEmptyList`, `aMissingCodeLineIsAHardParseErrorAndRejectsTheFile`,
  `aRepeatedMsgLineKeepsTheLastHereWhereCConcatenatesThem`, `aRepeatedMsgDeathLineKeepsTheLastHereWhereCConcatenatesThem`,
  `aDiceLineBeforeAnyEffectIsRefusedHereWhereCIgnoresIt` and `aHexadecimalLevelIsRefusedHereWhereCReadsBaseZero` pin them.

### Impact

- None on the shipped data: `lib/gamedata/chest_trap.txt` has the header, seven records each with `name:` then `code:`, no repeated
  `msg:`, no stray `dice:`, decimal levels and no indents.
- A modded file that drops the header, leaves `code:` out, splits a message over two lines, or has a stray `dice:` loses the whole file
  in Java, and the loader then stops start-up, where C would load it (with the second message line appended).
- Copying the upstream `chest_trap.txt` over this one fails until `record-count:7` is added.

### Question for Rowan

- Requiring `code:` is deliberate (the lexer comment says the code is load-bearing here), but should a repeated `msg:` be joined as in
  C, or refused as an error, instead of silently dropping the first line?

## `PlayerTimedGrammar.g4` and `PlayerTimedLexer.g4`: the file needs a `record-count:` header and a record, several lines C reads are refused, and repeated text lines overwrite where C appends

Found 2026-10-19 during `/port PlayerTimedReader.java`. Not fixed.

### What C does

- `init_parse_player_timed` in `player-timed.c` registers twenty independent directives and no `record-count:`. Only `name:` picks a
  record (`parse_player_timed_name` points at `timed_effects[index]`); the rest fill it in, in any order, and can be left out. A
  directive before any `name:` is `PARSE_ERROR_MISSING_RECORD_HEADER`.
- `desc:`, `on-end:`, `on-increase:`, `on-decrease:` and `effect-msg:` use `string_append`, so a repeated line is joined onto the text
  already held. A second `name:` for the same effect carries on with the same record, and `on-begin-effect:` and `on-end-effect:`
  chain a new effect on the end of the list.
- `on-begin-effect:` and `on-end-effect:` take `eff ?type ?radius ?other` through `grab_effect_data`, and the four `effect-*`
  directives (`effect-yx`, `effect-dice`, `effect-expr`, `effect-msg`) fill the effect just opened, whichever of the two it was.
  `effect-dice` takes a dice string such as `1d4`.
- `flags` is `?str flags`, so a bare `flags:` is legal and does nothing. A `grade:` colour of more than one character is a colour
  name, read by `color_text_to_attr`.
- `parser_parse` in `parser.c` reads an `int` with `strtol` in base zero (so `0x10` is 16), skips leading whitespace, and ignores
  anything after the value up to the next `:`.
- `run_parse_player_timed` accepts a file with no records.
- `grab_name` compares with `streq`, so a lower-case `name:fast` is refused in C as well.

### What Java does

- `PlayerTimedGrammar.g4`: `file` is `recordCount (playerTimed)+ EOF`. The upstream `player_timed.txt` has no header, so it is a syntax
  error here as it stands; `lib/gamedata/player_timed.txt` is the upstream file with `record-count:53` added and nothing else
  changed. A header with no record is also a syntax error.
- `PlayerTimedGrammar.g4`: `desc`, `onEnd`, `onIncrease` and `onDecrease` assign to a single local, so the last line wins. Likewise a
  second `on-begin-effect:` or `on-end-effect:` replaces the first, where C keeps both. A second `name:` opens a second record for the
  same effect, where C continues the first.
- `PlayerTimedGrammar.g4`: `onBeginEffect` takes a type and an optional sub-type and nothing more, so a radius or `other` value is a
  syntax error. `onEndEffectBlock` takes one optional `effect-dice:` followed by a bare `INTEGER`, so `effect-dice:1d4` is an error, and
  `effect-dice:` after an `on-begin-effect:` is an error. `effect-yx:`, `effect-expr:` and `effect-msg:` are not tokens at all.
- `PlayerTimedGrammar.g4`: `flags` needs a first `FLAG`, so a bare `flags:` is a syntax error. `PlayerTimedLexer.g4`: the `grade:`
  colour must be a single colour code, so `grade:Yellow:...` is an error.
- `PlayerTimedLexer.g4`: `INTEGER` and `DELIM_INTEGER` take no hex or octal, so `lower-bound:0x10` is an error, and an indented
  directive is an error too.
- `PlayerTimedReaderTest.aHeaderWithNoRecordIsRefusedHereWhereCLoadsAnEmptyList`, `missingRecordCountHeaderIsAHardError`,
  `aRepeatedDescLineKeepsTheLastHereWhereCConcatenatesThem`, `aRepeatedEndIncreaseOrDecreaseLineKeepsTheLastHereWhereCConcatenatesThem`,
  `aRepeatedNameLineMakesASecondRecordHereWhereCContinuesTheFirst`, `anOnBeginEffectWithARadiusIsRefusedHereWhereCReadsIt`,
  `effectYxEffectExprAndEffectMsgAreRefusedHereWhereCReadsThem`, `anEffectDiceAfterAnOnBeginEffectIsRefusedHereWhereCAcceptsIt`,
  `aDiceStringInEffectDiceIsRefusedHereWhereCParsesIt`, `aBareFlagsLineIsRefusedHereWhereCAcceptsIt`,
  `aGradeColourWrittenAsAWordIsRefusedHereWhereCReadsIt`, `aHexadecimalLowerBoundIsRefusedHereWhereCReadsBaseZero` and
  `anIndentedDirectiveIsRefusedHereWhereCSkipsLeadingWhitespace` pin them. The repeated `on-begin-effect:` and `on-end-effect:` case
  is read from the grammar and has no test.

### Impact

- None on the shipped data: `lib/gamedata/player_timed.txt` has 53 records, none of which repeats `desc:`, `on-end:`, `on-increase:` or
  `on-decrease:`, one
  `on-begin-effect:` and two `on-end-effect:` lines (one with `effect-dice:100`), no `effect-yx`, `effect-expr` or `effect-msg`, colour
  codes of one character, no hex values and no indents.
- A modded file that drops the header, empties the list, gives an effect a radius, a dice string or an `effect-msg:`, writes a colour
  by name, or indents a line loses the whole file in Java, and the loader then stops start-up, where C would load it. A modded file
  that splits a description over two `desc:` lines, or repeats a `name:`, loads with only the last line, or a split record.
- Copying the upstream `player_timed.txt` over this one fails until `record-count:53` is added.

### Question for Rowan

- Should `effect-yx`, `effect-expr` and `effect-msg` be supported on timed effects? `EffectParseRecord` already carries those fields for
  other grammars, and C lets the begin and end effects use them, so the only cost is grammar work. Today no shipped effect needs them.

## `Random.g4`: a lower-case `m`, a base straight into `M` and `$` variables are accepted, and blanks and a repeated `d` or `M` are refused

Found 2026-10-19 during `/port RandomReader.java`. Not fixed, except the number above `INT_MAX` ([<span style="color:#006400">**CLOSED**</span> 26-10-20: `numberOrMissing` in `Random.g4` returns the missing-number marker for a number above `INT_MAX` or too long for a `long`, the `@after` block tests `baseInit` as well as the other three terms, and `RandomReader.parse` logs and returns `null` for the resulting empty `Random`, as C refuses it. `RandomReaderTest` passes (26 tests). The bullets below that mention the overflow describe the state before the fix; the pinning test is now `aNumberAboveIntMaxIsRefusedInEverySlotAsCRefusesIt`.]).

### What C does

- C has two parsers for this notation. `parse_random` in `parser.c` reads every `rand` field of the data files and has no `$` variables. `dice_parse_string` in `z-dice.c` reads the `dice:` and `time:` lines of an effect and does.
- `parse_random` takes an optional leading `-` for the whole value (the base is then shifted by the bonus and the dice so the range flips sign), then `strtoul` for each number. It refuses a number above `INT_MAX` and a number that starts with `+`.
- Only an upper-case `M` marks the bonus. A plain number followed by anything but `d`, `+` or the end is refused, so `5M3` is refused and `5+M3` is not.
- The loop lets a second `d` or `M` through: `dd1` is 1d1, `MM1` is a bonus of 1, and in `M1M2` the last bonus wins. `strtoul` reads a signed zero, so `2d-0` is 2d0.
- Leading whitespace is skipped by `strtoul` and trailing blanks or tabs are allowed, so ` 2d6` and `2d6 ` both load.

### What Java does

- `Random.g4`: the lexer's `M` is `'M' | 'm'`, so `2d6m3` loads. There is an alternative for a base straight into `M`, so `5M3` loads as base 5, bonus 3. `$` variables are accepted in the base and sides slots (`$S`, `4d$S`).
- `Random.g4`: there is no rule for a blank, a repeated `d` or `M`, or a signed number after the first character, so `dd1`, `MM1`, `M1M2` and `2d-0` are syntax errors, and so is any blank. A `$` dice count (`$Dd5`, `$B+d$S`) is also a syntax error; the file header already lists these two as known gaps.
- `Random.g4`: a number above `INT_MAX` reaches `Integer.parseInt` in `numberOrMissing` and throws `NumberFormatException`. `RandomReader.parse` logs it and rethrows it unchecked, by design.
- `Random.g4` now applies a leading `-` itself, with the same shift as C, and `Random.parseStr` strips the `-` first, so `--1` loads as -1 where C refuses it.
- A number missing after `M` or `d` (`5M`, `-1d`, `$S+dM`) used to throw `NumberFormatException` from the action; `numberOrMissing` now turns ANTLR's placeholder token into a marker, the collected error ends the parse and the reader returns `null`, as C refuses it.
- `RandomReaderTest` pins each of these: `aLowerCaseMIsAcceptedHereWhereParseRandomRefusesIt`, `aBaseStraightIntoMIsAcceptedHereWhereParseRandomRefusesIt`, `dollarVariablesAreAcceptedHereWhereParseRandomHasNone`, `aDoubleMinusIsAcceptedByParseStrWhereCRefusesIt`, `blanksAreRefusedHereWhereCAllowsThemAtTheEnds`, `aRepeatedDOrMIsRefusedHereWhereCsLoopLetsItThrough`, `aSignedZeroInsideTheStringIsRefusedHereWhereStrtoulReadsIt`, `aVariableDiceCountIsRefusedHereWhereZDiceReadsIt` and `aNumberAboveIntMaxIsRefusedInEverySlotAsCRefusesIt` (named `aNumberAboveIntMaxThrowsHereWhereCRejectsIt` before the fix). Two sweeps over every string of up to five characters from `0 1 2 d M + -` (19,607 strings) check that nothing else differs from a transliteration of `parse_random`.

### Impact

- None seen on the shipped data: the reader tests that load the real files still pass with the new parser, and nothing in them hit a refused dice string. I did not diff every dice string in `lib/gamedata` against C by hand.
- A modded file that writes a lower-case `m`, a base straight into `M`, or a `$` variable in a `rand` field loads here and is refused by C; one that puts a blank around a dice string, repeats a `d` or an `M`, or uses a `$` dice count loses the value in Java where C loads it.
- A modded file with a number above `INT_MAX` in a dice string throws out of the assembler instead of recording a line-numbered error, so it stops start-up where C would report a bad value.

### Question for Rowan

- Should a number above `INT_MAX` return `null` like every other malformed string, so the assemblers record a line-numbered error as C does? [<span style="color:#006400">**CLOSED**</span> 26-10-20: yes. `RandomReader.parse` and `Random.parseStr` return `null`. The dead `catch (NumberFormatException)` and rethrow in `Random.parseStr` has been removed.]

## `VisualsGrammar.g4` and `VisualsLexer.g4`: a colour is one listed letter, `flicker-color:` and `cycle-color:` must follow their header at once, the name takes the rest of the line, and indented lines are refused

Found 2026-10-19 during `/port VisualsReader.java`. Not fixed.

### What C does

- `visuals_parse_flicker`, `visuals_parse_flicker_color` and `visuals_parse_cycle_color` in `ui-visuals.c` read the colour as a `sym`
  and pass only its first character to `color_char_to_attr` in `z-color.c`. That function never fails: a character it does not know is
  `COLOUR_WHITE`, and a space is `COLOUR_DARK`. The `attr < 0` test that returns `PARSE_ERROR_INVALID_COLOR` cannot fire.
- So `cycle-color:q`, `cycle-color:red`, `flicker:dd:name` and `flicker-color:d` followed by a blank all load.
- `cycle sym group sym name` is tokenized on `:`, so a field after the name is dropped: `cycle:fancy:rainbow:junk` is the cycle
  `rainbow`. `flicker sym color str name` takes the rest of the line as the name.
- The parse context remembers the last `flicker:` header (`flicker_cycle_index`, `flicker_color_index`). A `flicker-color:` line
  extends it however many `cycle:` lines came between, a leading `flicker-color:` writes to cycle 0 (dark), and a `flicker:` with no
  colour lines is legal, its three steps staying at colour 0.
- A `cycle-color:` before any `cycle:` dereferences `cycles[0]`, which is NULL, so C crashes. A `cycle:` with no `cycle-color:`
  parses, then fails in `visuals_file_parser_finish` (`visuals_color_cycle_new` refuses zero steps), and so does an empty file (no
  groups). Java refuses all three too, so those do not differ.
- `parser_parse` in `parser.c` skips leading whitespace before it looks at the line.

### What Java does

- `VisualsLexer.g4`: after `flicker:`, `flicker-color:` and `cycle-color:` the lexer is in `COLOUR` mode, whose only colour token is
  `COLOUR_CHAR`, one of the shared 28 letters. A name, an unlisted letter, a second letter, or a blank before the newline is a lexer
  error and the file loads as nothing.
- `VisualsGrammar.g4`: `flickerBlock` is `flicker (flickerColour)+` and `cycleBlock` is `cycle (cycleColour)+`, so a colour line must
  follow its own header at once, and a header with no colour line is a syntax error. A leading colour line is a syntax error too.
- `VisualsGrammar.g4`: after `cycle:` the group is `STRING_BETWEEN_COLON`, then `DELIM_COLON`, then `STRING`, which is the rest of the
  line, so `cycle:fancy:rainbow:junk` is the cycle `rainbow:junk`.
- `imports/CommentsAndEol.g4` does not skip indentation; the gap is written up under "Leading whitespace on a line" above.
- `VisualsReaderTest.anUnlistedColourLetterOnACycleColourIsAHardErrorHereWhereCReadsItAsWhite`,
  `aColourWrittenAsAWordIsAHardErrorHereWhereCTakesItsFirstLetter`,
  `aTwoLetterBaseColourOnAFlickerHeaderIsAHardErrorHereWhereCTakesTheFirst`,
  `aBlankAfterAColourLetterIsAHardErrorHereWhereCReadsTheLetter`,
  `aFieldAfterTheCycleNameBecomesPartOfTheNameHereWhereCIgnoresIt`,
  `aFlickerColourAfterAnInterveningCycleBlockIsAHardErrorHereWhereCAttachesIt`,
  `aColourLineBeforeAnyHeaderIsAHardErrorHereWhereCWritesToCycleZero` and
  `anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent` pin them. `flickerBlockWithNoColourLinesIsAHardError` pins the bare
  `flicker:` header.

### Impact

- None on the shipped data: `lib/gamedata/visuals.txt` is identical to the upstream file, every colour is one listed letter with
  nothing after it, no name carries a second `:`, each colour line follows its header, and nothing is indented.
- A modded file with a colour word, a mistyped colour letter, a trailing blank after a colour, an indented line, or a `flicker-color:`
  separated from its header loses the whole file in Java and the loader then stops start-up, where C loads it (a mistyped letter as a
  white step). A stray `:` field after a cycle name loads in both but under a different name.
- The cycler lookup is by name, so a cycle named `rainbow:junk` is simply never found by `color-cycle:fancy:rainbow` in `monster.txt`.

### Question for Rowan

- Should the lexer accept any single character as a colour and map the unlisted ones to white as C does, or stay strict? A typo
  becoming a silent white step is arguably worse than a refused file, so the strictness may be worth keeping on purpose; if so the
  grammar comment should say so. Several grammars (`PitLexer.g4` above) share the same question.

## `LoreGrammar.g4` and `LoreLexer.g4`: the cast counts are dropped, `base:` and `drop-base:` never match, and `blow:`, `flags:` and `friends:` are narrower than C

Found 2026-10-19 during `/port backend/parser/LoreReader.java`. Not fixed, and parked with the lore reader: nothing calls it, and no
parser is generated from this grammar today.

### What C does

- `parse_lore_counts` in `mon-init.c` stores all seven counts, including `cast_innate` and `cast_spell`.
- `parse_lore_base` sets `all_known` and every race flag, and refuses an unknown base name.
- `parse_lore_blow` takes `blow:method` with the effect, damage, seen count and index all optional, and records a blow only when the
  seen count is not zero, at the slot the index names.
- `parse_lore_flags` splits on any run of spaces and `|`, and a bare `flags:` is accepted.
- `parse_lore_drop_base` is registered, and `parse_lore_drop`, `parse_lore_drop_base`, `parse_lore_friends`,
  `parse_lore_friends_base` and `parse_lore_mimic` prepend to their lists, so the lists end up in reverse file order.
- `parse_lore_friends` and `parse_lore_friends_base` default the role to `MON_GROUP_MEMBER` and accept only `servant` and `bodyguard`.
- `parse_lore_drop` refuses a `min` or `max` above 99.
- `init_parse_lore` also registers the monster.txt directives (`plural`, `glyph`, `color`, `speed`, `desc`, `shape` and the rest) and
  ignores them.
- An unknown `name:` leaves the private pointer `NULL`, so every following directive of that record is skipped.

### What Java does

- `LoreGrammar.g4:291-299` reads `$counts.innate` and `$counts.spell` into locals called `innate` and `spell`, but the `@after` block at
  `LoreGrammar.g4:277-289` passes `castInnate` and `castSpell`, which stay 0. The two counts are lost.
- `LoreGrammar.g4:129-135` matches `BASE MONSTER_NAME`, but `LoreLexer.g4` only produces `MONSTER_NAME` in the mode that `name:` pushes
  and `BASE` pushes no mode, so by reading the lexer the rule can never match. If it did, it would store the base and neither set
  `all_known` nor the flags, and an unknown name gives `null` with no error.
- `LoreGrammar.g4:163-179` builds `dropBase` with no `@after` block, so `$monsterDropBase` is never set, and `monsterLore` at
  `LoreGrammar.g4:291-324` has no alternative for it. A `drop-base:` line is a syntax error.
- `LoreLexer.g4` has one token for the whole `blow:` value (`BLOW_MODE_VALUES`), with all five fields, upper-case names and a dice
  string in the `0+1d10M0` form. `blow:HIT` and `blow:HIT:HURT` are refused, a seen count of 0 is recorded, and the index is read and
  thrown away.
- `LoreLexer.g4` takes `flags:` and `spells:` as names joined by exactly ` | `, with at least one name, so a bare `flags:` or a
  separator without spaces is refused. An unknown name throws from `valueOf` (the parked soft-error gap).
- `LoreGrammar.g4:187` defaults a `friends:` role to `MON_GROUP_NONE`, and `LoreGrammar.g4:206` and `LoreGrammar.g4:232` accept any
  `MonsterGroupRole` name, so `leader` and `summon` load where C refuses them.
- `LoreLexer.g4` takes the `friends:` and `friends-base:` number only as `DICE_STRING`, the `0+2d3M0` form, but `lore_save` in
  `mon-lore.c` writes it as `2d3`, so a `friends:` or `friends-base:` line from the game is refused.
- The grammar appends to the drop, friends, friends-base and mimic lists, so they come out in file order, and it has no 99 limit.
- The lexer has no tokens for the monster.txt directives C ignores, so a file containing one is a syntax error.
- An unknown `name:` still builds a `MonsterLore` and adds it to `loreEntries`; only the `race.setLore` call is skipped.
- The `@after` block of `monsterLore` calls a 19-argument `MonsterLore` constructor with a `Map<MonsterBlow, Integer>`, and
  `MonsterLore.java` has only the no-argument and the 25-argument `List<MonsterBlow>` constructors, so the grammar would not compile as
  it stands (`MonsterLore.java` already says so).

### Impact

- None on the game today: the reader is a stub and `lib/user/lore.txt` only holds `name:`, `counts:`, `blow:` and `flags:` lines.
- Once the parser is picked up, the cast counts would read back as 0, and a file written by `lore_save` with `base:`, `drop-base:` or
  a `friends:` line would be refused whole. Lists that C reads in reverse order would come back in file order.

### Question for Rowan

- When the lore parser is picked up, should the grammar follow C's optional `blow:` fields and ignored monster.txt directives, or stay
  with the shapes `lore_save` writes? Either way the cast counts, `base:`, `drop-base:` and the `friends:` dice want fixing.
