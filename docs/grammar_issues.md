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
  `ProjectionReaderTest.anIndentedDirectiveIsAHardErrorWhereCSkipsTheIndent` pins it for `ProjectionLexer`.

### Impact

- None on the shipped data, which has no indented lines.
- Every grammar that imports `CommentsAndEol` has the same behaviour, so a hand-edited or modded data file with
  indentation loads in C and fails in Java.
- Tabs also fail in `EOL`, which only allows spaces before the newline.

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

Found 2026-10-17 during `/port backend/parser/CurseReader.java`. Not fixed.

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

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

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

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- C's `strtok` and `strtol` ignore trailing blanks. The upstream `artifact.txt` has such a line: the `flags:` line of
  "'Bladeturner'" ends `PROT_CONF ` with a space.
- `FLAG_EOL`, `VALUES_EOL` and `ALLOC_EOL` need the newline straight after the last token, so the untouched upstream file
  fails with `token recognition error at: ' \n'` and then loses the next line as well. A trailing blank after a bar, or after an
  integer or dice string, is fine.
- This repository's `lib/gamedata/artifact.txt` has had its trailing blanks trimmed, which is why the shipped file loads.
- `ArtifactReaderTest.aBlankAfterTheLastFlagValueOrAllocFigureIsALexerErrorHereWhereCAcceptsIt` pins three.

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
