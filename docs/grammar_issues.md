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
