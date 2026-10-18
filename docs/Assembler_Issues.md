# Assembler issues

Differences between the Java assemblers and the C data-file handlers they replace, and gaps in their Javadoc. Found
during reader ports (`docs/ReaderPort.md`) and left for the separate assembler pass. All assembler paths are under
`src/main/java/uk/co/jackoftradesltd/backend/parser/`.

## `blowmethod/BlowMethodAssembler.java`: `msg:` lookup is exact-case and name-only

Found 2026-10-17 during `/port backend/parser/BlowMethodReader.java`. Not fixed.

### What C does

- `parse_meth_message_type` in `mon-init.c` passes the `msg:` text to `message_lookup_by_name` in `message.c`.
- That function first reads the text with `strtoul`. If any digits were read, the text is taken as a message number and
  accepted when only spaces follow it and the number is below `MSG_MAX`.
- Otherwise it compares the text against every message name with `my_stricmp`, so the match ignores case.
- The names come from `list-message.h` with the `MSG_` prefix removed (`GENERIC`, `HIT`, `MON_HIT`), and there is no
  name `NONE`.
- No match gives `-1`, and `parse_meth_message_type` returns `PARSE_ERROR_INVALID_MESSAGE`.

### What Java does

- `BlowMethodAssembler.java:88` uses `MessageType.valueOf("MSG_" + record.msg())`, which is case-sensitive and has no
  numeric form.
- `msg:mon_hit` and `msg:12` therefore load in C and are a soft error here, and the record is skipped.
- `msg:NONE` resolves to `MSG_NONE`, an enum constant C does not have (an accepted divergence for the constant itself),
  so Java accepts a `msg:` line that C rejects.

### Impact

- None on the shipped data: every `msg:` in `blow_methods.txt` is an upper-case `MON_*` name.
- A modded file using lower case or a message number would load in C and lose records in Java.
- Any other assembler that resolves a message name the same way (monster spells use `message_lookup_by_name` too) has
  the same gap. Not yet searched for.

## `blowmethod/BlowMethodAssembler.java`: Javadoc does not mention the reversed list

Found 2026-10-17 during `/port backend/parser/BlowMethodReader.java`. Not fixed.

- `BlowMethodAssembler.assemble` now builds the list with `addFirst`, so the result is in reverse file order, as C's
  `blow_methods` is after `finish_parse_meth`.
- Neither the class block nor the `assemble` block says so. The `@param records` line says "in file order" and the
  `@return` line says nothing about order, so a reader of the assembler alone would take the output to be in file order.
- `BlowMethodReader.java` records the reason in its class block; the assembler, which does the reversing, should say it
  too.

## `summon/SummonAssembler.java`: `uniques:` is true only for exactly `1`

Found 2026-10-17 during `/port backend/parser/SummonReader.java`. Not fixed.

- `parse_summon_unique` in `mon-summon.c` reads an `int` and sets `unique_allowed` when it is non-zero. So `uniques:2`
  and `uniques:-1` are true in C.
- `SummonAssembler.java` runs `record.uniques().equals("1")`, so both are false in Java, and so is `uniques:01`, which
  C reads as 1.
- None on the shipped data, which uses `0` and `1` only. `SummonReaderTest.uniquesOtherThanOneIsFalseHereWhereCTreatsAnyNonZeroAsTrue`
  pins the Java result.
- Same family as the `BlowMethodLexer.g4` entry in `docs/grammar_issues.md` (`!text.equals("0")` there, the mirror
  image).

## `summon/SummonAssembler.java`: `msgt:` lookup is exact-case and name-only

Found 2026-10-17 during `/port backend/parser/SummonReader.java`. Not fixed.

- `parse_summon_message_type` in `mon-summon.c` calls `message_lookup_by_name`, the same lookup as the blow-method
  `msg:` entry above: case-insensitive, with a numeric form, and no name `NONE`.
- `SummonAssembler.java` uses `MessageType.valueOf("MSG_" + rawMsgt)`, which is exact-case and has no numeric form, and
  `msgt:NONE` resolves to `MSG_NONE`, which C rejects.
- The lexer rejects lower-case and numeric values before they reach the assembler (see `docs/grammar_issues.md`), so
  only `msgt:NONE` is reachable through the assembler today.

## `summon/SummonAssembler.java`: an unresolved `fallback:` is an error here, silent in C

Found 2026-10-17 during `/port backend/parser/SummonReader.java`. Not fixed.

- `finish_parse_summon` in `mon-summon.c` sets each summon's `fallback` to `summon_name_to_idx(fallback_name)`, which
  is -1 for a name that matches nothing. There is no error.
- `SummonAssembler.java` adds `"Invalid fallback name: ..."` to the soft errors and keeps the summon with a `null`
  fallback. The record survives, but the error makes `MonsterDataLoader.loadSummons` report the file as carrying errors.
- Stricter than C rather than wrong, and the shipped file's two fallbacks both resolve. Pinned by
  `SummonReaderTest.anUnresolvedFallbackKeepsTheRecordWhereCStoresMinusOneSilently`.

## `summon/SummonAssembler.java`: `base:` order is file order, C's is reversed

Found 2026-10-17 during `/port backend/parser/SummonReader.java`. Not fixed.

- `parse_summon_base` pushes each base on the head of `s->bases`, so a summon with `base:zephyr hound` then
  `base:canine` holds canine first in C and zephyr hound first in Java.
- Not observable: `summon_specific_okay` only walks the list for membership, so the order never changes a result.
- Recorded so it is not rediscovered as a mismatch; `SummonReaderTest.realFileComesBackInFileOrderWithEveryFieldAsUpstream`
  pins the Java order.

## `pit/PitAssembler.java`: an unresolved `mon-ban:` name is an error here, silent in C

Found 2026-10-17 during `/port backend/parser/PitReader.java`. Not fixed.

- `parse_pit_mon_ban` in `mon-init.c` stores whatever `lookup_monster` returns and never checks it, so a name that
  matches nothing becomes a forbidden-monster entry with a `NULL` race. `mon_pit_hook` in `gen-room.c` compares
  `race == monster->race`, which is never true for `NULL`, so the entry does nothing.
- `PitAssembler.java:229-235` adds an `"... unknown banned monster base: ..."` soft error and drops the whole pit.
  `lookup_monster` matches the exact name case-insensitively and then the first name containing the text, and
  `MonsterRegistry.lookupMonsterRace` does the same, so only a name found in no monster at all gets this far.
- The message says "monster base" for what is a monster name; `PitAssembler.java:232`.
- `mon-base:` is the other way round: `parse_pit_mon_base` returns `PARSE_ERROR_UNRECOGNISED_TVAL`, fatal in C, and the
  assembler's soft error and skipped record is the port's usual partial-results rule.
- None on the shipped data: the one `mon-ban:` resolves. `PitReaderTest.unresolvableMonsterNamesDropTheRecord` pins
  the Java result.

## `pit/PitAssembler.java`: the `room:` comment says the lexer constrains the digit, it does not

Found 2026-10-17 during `/port backend/parser/PitReader.java`. Not fixed.

- The comment at `PitAssembler.java:74` says an unrecognized digit falls to `PIT_TYPE_NONE` "since the digit is already
  constrained by the lexer". `PIT_INTEGER` takes any integer, so `room:4`, `room:-1` and `room:99999999999` get here and
  become `PIT_TYPE_NONE` without an error.
- C stores the number as written (`pit->room_type = parser_getuint(p, "type")`). Not observable: `set_pit_type` in
  `gen-room.c` is only ever called for type 1, type 2 or 0 (any). A room number other than 1 and 2 is skipped for the
  first two and eligible for the third, exactly as `PIT_TYPE_NONE` is.
- `PitReaderTest.anUnrecognisedRoomDigitFallsBackToNoneRatherThanErroring` pins the fall-through for `room:9`.

## `pit/PitAssembler.java`: bases, colours and forbidden monsters are in file order, C's are reversed

Found 2026-10-17 during `/port backend/parser/PitReader.java`. Not fixed.

- `parse_pit_mon_base`, `parse_pit_color` and `parse_pit_mon_ban` each push on the head of their list, so a pit with
  `mon-base:orc` then `mon-base:troll` holds troll first in C and orc first in Java.
- Not observable: `mon_pit_hook` only walks each list for a match, so the order never changes a result.
- Recorded so it is not rediscovered as a mismatch; `PitReaderTest.listsInsideAProfileAreInFileOrderHereWhereCHoldsThemReversed`
  pins the Java order.

## `monsterspell/MonsterSpellAssembler.java`: an absent `hit:` drops the record, C leaves it at 0

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `parse_mon_spell_name` in `mon-init.c` allocates the spell with `mem_zalloc`, so a record with no `hit:` line has a
  `hit` of 0. In `mon-spell.c` a hit of 0 means the spell never hits.
- The grammar accepts a record with no `hit:` and hands the assembler an empty string. `MonsterSpellAssembler.java:98`
  runs `Integer.parseInt("")`, which throws, and the catch at line 99 adds a "malformed hit value" soft error and skips
  the record.
- `MonsterSpellReaderTest.anAbsentHitDropsTheRecordHereWhereCLeavesItZero` pins the Java result.
- None on the shipped data: all 91 records carry `hit:`. Compare `msgt:` in the same method, which treats an empty value
  as absent (`MonsterSpellAssembler.java:87`).

## `monsterspell/MonsterSpellLevelAssembler.java`: an unknown lore colour drops the level, C reads it as white

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `parse_mon_spell_lore_color` and its resist and immune twins in `mon-init.c` call `color_text_to_attr` for text longer
  than one character and `color_char_to_attr` for one character. Both fall back to `COLOUR_WHITE` when nothing matches
  (`z-color.c`), so `PARSE_ERROR_INVALID_COLOR` fires only for a one-character code whose byte is negative as a signed
  `char`, which `color_char_to_attr` hands straight back.
- `MonsterSpellLevelAssembler.java:79-103` calls `ColourEnum.fromCode`, which returns `null` for an unknown name or
  character. The assembler adds a soft error and runs `continue`, so that one level is left out of the list.
- The method's own Javadoc (`MonsterSpellLevelAssembler.java:49-52`) promises all-or-nothing, a `null` return if any
  block fails. Only the bad-power branch at line 74 does that; the three colour branches return a list with a level
  missing, and `MonsterSpellAssembler.java:111` only drops the spell on `null`. The spell loads with the error recorded.
- `MonsterSpellReaderTest.anUnknownLoreColourDropsTheLevelHereWhereCReadsWhite` pins the Java result.
- None on the shipped data: every colour resolves.

## `monsterspell/MonsterSpellAssembler.java`: `msgt:` lookup is exact-case and name-only

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `parse_mon_spell_message_type` in `mon-init.c` uses `message_lookup_by_name`, the lookup described in the
  `blowmethod/BlowMethodAssembler.java` entry above: case-insensitive, with a numeric form, and no name `NONE`. That
  entry's last bullet asked whether other assemblers had the gap; this is one.
- `MonsterSpellAssembler.java:89` uses `MessageType.valueOf("MSG_" + messageT)`. `msgt:teleport` and `msgt:6` load in C
  and are a soft error here, and the record is skipped. `msgt:NONE` resolves to `MSG_NONE`, which C rejects.
- Unlike the summon lexer, `MonsterSpellLexer.g4` takes any text after `msgt:`, so every one of these reaches the
  assembler.
- `MonsterSpellReaderTest.msgtIsExactCaseAndNameOnlyHereWhereCIsCaseBlindWithANumericForm` and
  `msgtNoneIsAcceptedHereWhereCHasNoSuchMessage` pin them.
- None on the shipped data: every `msgt:` is an upper-case name.

## `monsterspell/MonsterSpellLevelAssembler.java`: C always has a level 0, Java has none unless the text gives one

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `parse_mon_spell_name` allocates a level with power 0 for every spell, and `mon-spell.c` (`spell_message`,
  `do_mon_spell` and `mon_spell_lore_description`) walks from `spell->level` without a `NULL` check.
- Java builds a level only from a text block. A spell with effects but no `lore:` or `message-*:` line has an empty
  level list, and a spell that opens with `power-cutoff:20` has the one level at power 20 and none at 0, where C has
  both.
- `MonsterSpellParseRecord.java` says the `levels` list is "always at least one", which is wrong for the first case.
- `MonsterSpellReaderTest.aRecordWithNoTextHasNoLevelHereWhereCAlwaysHasOne` and
  `aLeadingPowerCutoffLeavesNoImplicitLevelZeroHereWhereCKeepsIt` pin them.
- None on the shipped data: every record has `lore:`, and none starts with `power-cutoff:`. The port of the code that
  walks the levels has to cope with a missing level 0, or the assembler has to add one.

## `monsterspell/MonsterSpellAssembler.java`: the list is in file order, C's chain is reversed

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `parse_mon_spell_name` pushes each record on the head of the chain and `finish_parse_mon_spell` keeps it, so
  `monster_spells` starts with the last record in the file. `MonsterSpellAssembler.java:112` appends in file order.
- Only a spell named twice makes it matter: `monster_spell_by_index` in `mon-spell.c` returns the first match in the
  chain, which is the later record. `MonsterRegistry` has no lookup over the list yet; one written over the Java order
  must take the last match, or the assembler must put each spell at the front as `BlowMethodAssembler` does.
- `MonsterSpellReaderTest.duplicateSpellNamesAreBothKeptInFileOrder` pins the Java order.
- None on the shipped data: all 91 names are different.

## `monsterspell/` Javadoc cites a C line number

Found 2026-10-17 during `/port backend/parser/MonsterSpellReader.java`. Not fixed.

- `MonsterSpellParseRecord.java:58` and `MonsterSpellLevelAssembler.java:34` cite `mon-init.c:596`. Both should name
  the function (`parse_mon_spell_name`) without a line number.
- Not a behaviour difference; recorded here because the fix is in comments.

## `monsterbase/MonsterBaseAssembler.java`: the list is in file order, C's chain is reversed

Found 2026-10-17 during `/port backend/parser/MonsterBaseReader.java`. Not fixed.

- `parse_mon_base_name` pushes each record on the head of the chain (`rb->next = h`) and `finish_parse_mon_base` keeps it,
  so `rb_info` starts with the last record in the file (`Morgoth`). `MonsterBaseAssembler.java` appends to `results` in
  file order.
- Only a base named twice makes it matter: `lookup_monster_base` in `mon-util.c` returns the first match down the chain,
  which is the later record, and `MonsterRegistry.lookupMonsterBase` takes the first match in the list, which is the
  earlier. The assembler would have to put each base at the front, as `BlowMethodAssembler` does, or the lookup take
  the last match.
- `MonsterBaseReaderTest.duplicateNamesAreBothKeptInFileOrderWhereCHoldsThemReversed` pins the Java order.
- None on the shipped data: all 56 names are different.

## `monsterbase/MonsterBaseAssembler.java`: a glyph outside the Basic Multilingual Plane is rejected

Found 2026-10-17 during `/port backend/parser/MonsterBaseReader.java`. Not fixed.

- `parse_mon_base_glyph` stores `parser_getchar` in `d_char`, a `wchar_t`, which holds any code point. The assembler
  insists on `record.glyph().length() == 1` and stores a `char`, so a code point above U+FFFF (two UTF-16 units) is
  a soft error that skips the record.
- `MonsterBase.java` already says the `char` covers every glyph in `monster_base.txt`, so this is a known limit; it
  matters only for a modded data file.
- `MonsterBaseReaderTest.aGlyphOutsideTheBasicMultilingualPlaneIsASoftErrorHereWhereCHoldsAWideChar` pins it.

## `monster/MonsterAssembler.java`: `spell_power` is never defaulted to the level

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_depth` in `mon-init.c` sets `r->spell_power = r->level` ("Level is default spell power"), and
  `parse_monster_spell_power` overrides it only if a `spell-power:` line follows. A `spell-power:` line that comes before
  `depth:` is overwritten by the level.
- `MonsterAssembler.java` starts `spellPower` at 0 and sets it only from `record.spellPower()`. A race with no
  `spell-power:` line keeps 0, and a `spell-power:` line before `depth:` keeps its value.
- Of the 624 races in the shipped `monster.txt`, 595 have no `spell-power:` line (12 do) and so differ: `grey mold`
  (depth 1) is 0 here and 1 in C. Every other stat the reader loads, and every entry count, agrees with an independent
  reading of the file (`MonsterReaderTest.everyRealRacesStatsAndEntryCountsMatchAnIndependentReadOfTheText`).
- `race->spell_power` picks the lore description and the cast message for each spell, in `mon_spell_lore_description`
  and `spell_message` in `mon-spell.c`, and it is the value of the `SPELL_POWER` base in `effects.c`. So the lowest spell
  level is the one a race with no `spell-power:` line uses.
- `MonsterReaderTest.aMonsterWithNoSpellPowerLineGetsSpellPowerZeroHereWhereCUsesItsLevel`,
  `theRealFilesSpellPowerIsZeroForRacesWithNoSpellPowerLineWhereCUsesTheirLevel` and
  `aSpellPowerLineBeforeDepthKeepsItsValueHereWhereCOverwritesItWithTheLevel` pin the Java result.

## `monster/MonsterAssembler.java`: no `plural:` line gives `""`, C leaves NULL

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_plural` sets `r->plural` only for a non-empty value, so a race with no `plural:` line has NULL.
  `MonsterRace.java` (field `plural`, and the constructor's `@param plural`) says `null` is meaningful.
- The grammar's `monster` rule starts `pluralInit` at `""` and the assembler passes `record.plural()` straight through,
  so 528 of the 624 shipped races have `""`.
- Nothing in `src/main` reads `plural` yet, so nothing is affected today, but the first reader that tests for `null` will
  take every race as having a plural.
- `MonsterReaderTest.aMonsterWithNoPluralLineHasAnEmptyPluralHereWhereCHasNull` pins it.

## `monster/MonsterAssembler.java`: a race with no `color:` is skipped, C leaves `d_attr` at 0

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `color` is optional in C. A race without one keeps the zeroed `d_attr`, which is `COLOUR_DARK`. A value that is not a
  colour is `PARSE_ERROR_INVALID_COLOR`.
- `MonsterAssembler.java` calls `ColourEnum.fromCode(record.colour())` on the empty default, gets `null`, and reports
  "unknown colour type" for a record that never gave a colour, then skips the record.
- `MonsterReaderTest.aMonsterWithNoColourLineIsSkippedHereWhereCLeavesTheAttrAtZero` pins it.
- None on the shipped data, where all 624 records have a `color:` line.

## `monster/MonsterAssembler.java`: the `drop:` min and max are not bounded, and a negative chance is kept

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_drop` and `parse_monster_drop_base` return `PARSE_ERROR_INVALID_ITEM_NUMBER` when `min` or `max` is
  above 99, and the chance is a `uint`, so a negative one is refused by `parser_parse`.
- `MonsterAssembler.java` parses all three with `Integer.parseInt` and stores them as they come, so `100:100:100` and a
  chance of `-5` are kept in the `MonsterDrop`.
- `MonsterReaderTest.aDropWithMinOrMaxAbove99IsAcceptedHereWhereCReportsAnInvalidItemNumber` and
  `MonsterReaderTest.aNegativeDropChanceIsAcceptedHereWhereCRejectsAUint` pin it. The second one starts in
  `MonsterLexer.g4`; see `docs/grammar_issues.md`.
- None on the shipped data, where no `min` or `max` is above 99 and no chance is negative.

## `monster/MonsterAssembler.java`: the lists are in file order, C's chains are reversed, and drops are grouped

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_drop`, `parse_monster_drop_base`, `parse_monster_friends`, `parse_monster_friends_base`,
  `parse_monster_mimic`, `parse_monster_shape` and `add_alternate_spell_message` each push the new entry on the head of
  the race's list, so the later line in the file comes first. Only the blows are appended to the end.
- `MonsterRace.java` records this for each list ("C's list runs in reverse") as a known difference. Two cases go beyond
  it. `drop:` and `drop-base:` share one chain in C, in reverse file order, where `MonsterAssembler.java` builds the
  `drop:` entries first and the `drop-base:` entries after, each in file order. And the order is observable: the monster
  drop code in `mon-make.c` walks the chain in order, `mon-lore.c` lists the drops from it, and the mimic and shape code
  walk theirs in order too, so which entry a random pick lands on depends on it.
- 34 of the shipped races have more than one `drop:` or `drop-base:` line, so those differ from C.
- `MonsterReaderTest.dropsAreInFileOrderHereWhereCsChainHoldsThemReversed`,
  `aDropFollowedByADropBaseKeepsThatOrderHereWhereCWalksTheDropBaseFirst` and
  `friendsAreInFileOrderHereWhereCsChainHoldsThemReversed` pin the Java order.

## `monster/MonsterAssembler.java`: a `friends-base:` chance above 100 is an error, C keeps it

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_friends_base` stores `parser_getuint(chance)` with no upper bound, as `parse_monster_friends` does. The
  `friends:` loop in `MonsterAssembler.java` agrees (`MonsterReaderTest.aFriendChanceAboveOneHundredIsKeptAsWritten`),
  but the `friends-base:` loop rejects `chance > 100` with "friends base chance value out of bounds" and skips the record.
- `MonsterReaderTest.aFriendsBaseChanceAbove100IsASoftErrorHereWhereCKeepsIt` pins it.
- None on the shipped data, where the highest `friends-base:` chance is 100.

## `monster/MonsterAssembler.java`: the group role is matched ignoring case, C is case-sensitive

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_friends` and `parse_monster_friends_base` compare the role with `streq(role_name, "servant")` and
  `streq(role_name, "bodyguard")`, so `Servant` is `PARSE_ERROR_INVALID_MONSTER_ROLE`.
- `MonsterAssembler.java` uses `equalsIgnoreCase` in both loops and accepts it.
- `MonsterReaderTest.aFriendRoleInCapitalsIsAcceptedHereWhereCComparesCaseSensitively` pins it.
- None on the shipped data, which writes the role in lower case.

## `monster/MonsterAssembler.java`: a die that overflows `int` escapes as `NumberFormatException`

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- C reports a `friends:`, `friends-base:` or `blow:` value that `parse_random` cannot read as a parser error.
- `MonsterAssembler.java` hands the dice to `Random.parseStr`, which throws `NumberFormatException` for
  `99999999999d2` and does not return `null`. Nothing in `MonsterAssembler.assemble` or `GrammarDriver.run` catches it, so
  `MonsterReader.parseWithResults` throws an unchecked exception and the whole load fails, instead of the record being
  skipped with a soft error. Overflow in the sides (`1d99999999999`) does the same.
- The fix could sit in `Random.java` (return `null`) or in the three call sites in `MonsterAssembler.java`.
- `MonsterReaderTest.aFriendCountThatOverflowsIntThrowsHereWhereCReportsAParserError` and
  `aBlowDamageThatOverflowsIntThrowsHereWhereCReportsAParserError` pin it. The `friends-base:` call site is the same
  shape and has no test of its own.
- None on the shipped data.

## `monster/MonsterAssembler.java`: an unknown spell name in `message-vis:`, `message-invis:` or `message-miss:` is ignored

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- The three handlers call `grab_name("monster spell", spell, r_info_spell_flags, ...)` and return
  `PARSE_ERROR_INVALID_SPELL_NAME` when the name is not a spell. A message is kept for a spell the race does not cast.
- `MonsterAssembler.java` builds `spellMessages` only from the names in `record.spells()` and looks the messages up by
  them, so a message naming an unknown spell, or a spell the race does not list, is dropped with no error.
- The message for a spell the race does not cast is never shown in C either, so only the unknown-name case is visible.
- `MonsterReaderTest.aMessageForAnUnknownSpellIsAcceptedHereWhereCReportsAnInvalidSpellName` pins it.
- None on the shipped data.

## `monster/MonsterAssembler.java`: an unknown `color-cycle:` group is an error, C is silent

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `visuals_cycler_set_cycle_for_race` in `ui-visuals.c` returns without a message when the cycle is not found, whether
  the group or the name is wrong, and `parse_monster_color_cycle` returns no error.
- `MonsterAssembler.java` adds "unknown colour cycle name" when the group is unknown and keeps the race with a `null`
  cycler. An unknown name in a known group is silent, as in C. The class block says a bad cross-reference drops the
  record; this one does not.
- `MonsterReaderTest.anUnknownColourCycleGroupIsASoftErrorButTheRaceIsKeptWhereCIsSilent` and
  `anUnknownColourCycleNameInAKnownGroupIsSilentAsInC` pin it.
- None on the shipped data.

## `monster/MonsterAssembler.java`: C's directive-order effects are not reproduced

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_base` copies the base's glyph into `d_char` and unions the base's flags when `base:` is read, so a
  `glyph:` or `flags-off:` line that comes before `base:` is overridden. `MonsterAssembler.java` seeds the flags from the
  base first and lets an explicit glyph win, so it keeps both. The `spell-power:` before `depth:` case is in its own entry
  above.
- `MonsterReaderTest.aFlagsOffLineBeforeBaseRemovesTheBaseFlagHereWhereCAddsItBack` and
  `aGlyphLineBeforeBaseKeepsItsGlyphHereWhereCOverwritesItWithTheBases` pin it.
- None on the shipped data, where `base:` comes before every `glyph:`, `flags:` and `flags-off:` line (it follows
  `name:`, or `plural:` when there is one). Java's reading is the one the file's author meant.

## `monster/MonsterAssembler.java`: a glyph outside the Basic Multilingual Plane is rejected

Found 2026-10-17 during `/port backend/parser/MonsterReader.java`. Not fixed.

- `parse_monster_glyph` stores `parser_getchar` in `d_char`, a `wchar_t`. `MonsterAssembler.java` insists on
  `glyphStr.length() == 1` and stores a `char`, so a code point above U+FFFF is "illegal glyph character" and skips the
  record. Same limit as the `monsterbase/MonsterBaseAssembler.java` entry above; matters only for a modded data file.

## `slay/SlayAssembler.java`: a missing `multiplier:`, `o-multiplier:` or `power:` skips the record, C leaves it 0

Found 2026-10-17 during `/port backend/parser/SlayReader.java`. Not fixed.

- `parse_slay_multiplier`, `parse_slay_o_multiplier` and `parse_slay_power` in `obj-init.c` only run when the line is
  present, and the slay is allocated with `mem_zalloc`, so a record that leaves one out loads with 0 in that field.
- `SlayGrammar.g4` defaults every absent directive to `""`, and `SlayAssembler.java` calls `Integer.parseInt("")` on it.
  The `NumberFormatException` becomes the soft error "invalid integer format for power: " and the record is skipped.
- A missing `name:`, `melee-verb:` or `range-verb:` reaches the `Slay` as `""` where C holds `NULL`.
- `SlayReaderTest.missingPowerSkipsTheRecordHereWhereCLeavesItZero` and
  `missingMultiplierSkipsTheRecordHereWhereCLeavesItZero` pin it.
- None on the shipped data, where all eleven records carry all three numbers.

## `slay/SlayAssembler.java`: `race-flag:NONE` is accepted, C rejects it

Found 2026-10-17 during `/port backend/parser/SlayReader.java`. Not fixed.

- `parse_slay_race_flag` calls `lookup_flag`, which starts at `FLAG_START` (1), so the `NONE` entry at index 0 of
  `mon_race_flags` is never matched and the handler returns `PARSE_ERROR_INVALID_FLAG`. The comment in `slay.txt` also
  says the flag "has to be different than NONE".
- `SlayAssembler.java` uses `MonsterRaceFlag.valueOf("RF_" + rawRaceFlag)`, and `RF_NONE` is a constant, so the slay
  loads with `raceFlag == RF_NONE` and targets nothing.
- `SlayReaderTest.raceFlagNoneLoadsHereWhereCLookupFlagSkipsIt` pins it.
- None on the shipped data. Any other assembler that resolves a race flag the same way has the same gap. Not yet searched for.

## `slay/SlayAssembler.java`: a code the `Slay` constructor cannot split aborts the whole load

Found 2026-10-17 during `/port backend/parser/SlayReader.java`. Not fixed.

- C stores the `code:` text as given and never takes it apart.
- The `Slay` constructor splits `code` at the underscore and reads the front as an `RF_` flag and the back as the level.
  `SlayAssembler.java` calls it with no guard, so `code:BANE` (`ArrayIndexOutOfBoundsException`), `code:FOO_2`
  (`IllegalArgumentException`) and `code:EVIL_X` (`NumberFormatException`) escape as unchecked exceptions instead of a
  soft error that skips the record. The partial-results contract in the class block does not hold for them.
- `SlayReaderTest.codeWithNoUnderscoreEscapesAsAnExceptionNotASoftError` pins the first case.
- None on the shipped data, where all eleven codes are a flag, an underscore and a digit. A modded `slay.txt` with a
  differently shaped code would not load at all.

## `slay/SlayAssembler.java`: no range check on the three numbers

Found 2026-10-17 during `/port backend/parser/SlayReader.java`. Not fixed.

- `parser_getuint` refuses a negative value; `SlayAssembler.java` takes whatever `Integer.parseInt` returns, so a
  negative `multiplier:`, `o-multiplier:` or `power:` loads. The token-level cause is in `docs/grammar_issues.md`
  (`SlayLexer.g4` `INTEGER`); this is the assembler half, since the lexer comment promises the check lives here.
- `SlayReaderTest.negativeMultiplierLoadsHereWhereCParserGetUintRejectsIt` pins it.

## `brand/BrandAssembler.java`: `resist-flag:NONE` and `vuln-flag:NONE` are accepted, C rejects them

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- `parse_brand_resist_flag` and `parse_brand_vuln_flag` call `lookup_flag`, which starts at `FLAG_START` (1), so the
  `NONE` entry at index 0 of `mon_race_flags` is never matched and both return `PARSE_ERROR_INVALID_FLAG`.
- `BrandAssembler.java` uses `MonsterRaceFlag.valueOf("RF_" + ...)`, and `RF_NONE` is a constant, so the brand loads.
  `resist-flag:NONE` leaves the resist flag as `RF_NONE`, and `vuln-flag:NONE` is indistinguishable from leaving the line out.
- `BrandReaderTest.resistFlagNoneLoadsHereWhereCLookupFlagSkipsIt` and `vulnFlagNoneLoadsHereWhereCLookupFlagSkipsIt` pin it.
  `resistFlagMaxIsRejectedAsInC` pins that `MAX` is refused in both.
- None on the shipped data. Same gap as the `slay/SlayAssembler.java` entry above, and in any assembler that resolves a
  monster race flag the same way.

## `brand/BrandAssembler.java`: no 254-brand limit

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- `finish_parse_brand` stops counting at 254 and returns `PARSE_ERROR_TOO_MANY_ENTRIES`; only the first 254 records
  reach `brands[]`.
- `BrandAssembler.java` has no limit, and `ObjectDataLoader.loadBrands` documents that it applies none, so 255 brands
  load in full with no error.
- `BrandReaderTest.moreThanTwoHundredFiftyFourBrandsAllLoadHereWhereCFailsTheParse` pins it.
- None on the shipped data, which has ten. `ObjectDataLoader.loadSlays` documents the same unapplied limit for slays.

## `brand/BrandAssembler.java`: no range check on the three numbers

Found 2026-10-17 during `/port backend/parser/BrandReader.java`. Not fixed.

- `parser_getuint` refuses a negative value; `BrandAssembler.java` takes whatever `Integer.parseInt` returns, so a
  negative `multiplier:`, `o-multiplier:` or `power:` loads. The token-level cause is in `docs/grammar_issues.md`
  (`BrandLexer.g4` `INTEGER`); this is the assembler half, since the lexer comment promises the check lives here.
- `BrandReaderTest.negativeMultiplierLoadsHereWhereCParserGetUintRejectsIt` pins it.

## `itemobject/ItemObjectAssembler.java`: dice a record leaves out are `null`, C holds a zero `struct random`

Found 2026-10-17 during `/port backend/parser/ItemObjectReader.java`. Not fixed.

- C zeroes the record, so a kind with no `attack:`, `armor:`, `charges:`, `pile:` or `pval:` line holds a zero
  `struct random` for `to_h`, `to_d`, `to_a`, `charge`, `stack_size` and `pval`.
- `ItemObjectAssembler.java` starts `attToh` and `attTod` (from `:167`), `acToa` (`:195`), `charges` (`:236`),
  `pileAmount` (`:246`) and `pVal` (`:389`) as `null` and only fills them when the line is there. `time` alone is
  started as `Random.Zero()`. In the shipped file `getToH()` and `getToD()` are `null` for the 153 kinds with no
  `attack:` line, `getToA()` for the 153 with no `armor:` line, `getCharge()` for 321, the pile stack for 277 and
  `getPVal()` for 368.
- `ObjectUtils.objectPrep` calls `randCalc` on `getToH()`, `getToD()` and `getToA()` for every kind, and on `getPVal()`
  for a potion, food, flask or launcher. Run on the loaded `& Ration~ of Food` and the `Cure Light Wounds` potion it
  throws `NullPointerException` from `getPVal()`; `& Dagger~` is fine because it has both lines. `PlayerBirth.java`
  reaches `objectPrep` for the starting kit, and `ItemObject.hasStandardToH` dereferences `getToH()` too.
- `ItemObjectReaderTest.absentDiceAreNullHereWhereCHoldsAZeroRandom` pins the nulls, so it needs to change with any fix.
- The fix is to start each of the five as `Random.Zero()`, as `time` is.

## `itemobject/ItemObjectAssembler.java`: a record with no `type:` has a `null` tval and base, C has tval 0

Found 2026-10-17 during `/port backend/parser/ItemObjectReader.java`. Not fixed.

- C leaves `k->tval` at 0 (`TV_NONE`) and `k->base` at `NULL` when `parse_object_type` never runs, and
  `finish_parse_object` still indexes `kb_info[0]`.
- `ItemObjectAssembler.java:89-90` starts `base` and `tValue` as `null` and only sets them from a `type:` line, so the
  kind's `gettValue()` is `null` rather than `TV_NONE`.
- `ObjectRegistry.addObjectKind` reads `toAdd.getBase()` without a guard, so `ObjectDataLoader.loadItemObjects` would
  throw `NullPointerException` on such a kind instead of reporting it.
- `ItemObjectReaderTest.aRecordWithNoTypeHasANullTvalAndBaseWhereCHasTvalZero` pins the reader's half.
- None on the shipped data: every record, the four `type:none` sentinels included, has a `type:` line.

## `itemobject/ItemObjectAssembler.java`: `HATES_` flags are unknown, C sets `EL_INFO_HATES`

Found 2026-10-17 during `/port backend/parser/ItemObjectReader.java`. Not fixed.

- `grab_element_flag` in `obj-init.c` accepts `IGNORE_<element>` and `HATES_<element>`, setting `EL_INFO_IGNORE` or
  `EL_INFO_HATES` on that element of the kind's `el_info`.
- `ItemObjectAssembler.java:284` checks `flag.startsWith("IGNORE_")` only, so `flags:HATES_FIRE` is an
  `unknown flag` soft error and the kind is dropped. `ElementInfoEnum` already has `EL_INFO_HATES`.
- `ItemObjectReaderTest.aHatesFlagIsAnUnknownFlagHereWhereCSetsTheHatesBit` pins it.
- None on the shipped data: `object.txt` has no `HATES_` token. The base kinds in `object_base.txt` do use them, and
  that file has its own parser.

## `objectbase/ObjectBaseAssembler.java`: an unknown colour is an error here, white in C

Found 2026-10-17 during `/port backend/parser/ObjectBaseReader.java`. Not fixed.

- `parse_object_base_graphics` in `obj-init.c` calls `color_text_to_attr` for a colour longer than one character and
  `color_char_to_attr` for one character. Both answer `COLOUR_WHITE` when nothing matches, with no error.
- `ObjectBaseAssembler.java:72` calls `ColourEnum.fromCode`, which answers `null` for an unknown name or character, and
  the next lines add an `invalid colour` soft error and skip the whole record.
- `ObjectBaseReaderTest.anUnknownColourSkipsTheRecordWhereCQuietlyUsesWhite` pins it.
- None on the shipped data: all thirteen colours used in `object_base.txt` resolve. Unlike the `ItemObject` entry in
  `docs/grammar_issues.md`, the text reaches the assembler here, because `graphics:` takes any text to the end of the
  line.

## `objectbase/ObjectBaseAssembler.java`: `IGNORE_` flags are unknown, C sets `EL_INFO_IGNORE`

Found 2026-10-17 during `/port backend/parser/ObjectBaseReader.java`. Not fixed.

- `grab_element_flag` in `obj-init.c` accepts `IGNORE_<element>` and `HATES_<element>`, setting `EL_INFO_IGNORE` or
  `EL_INFO_HATES` on that element of the base's `el_info`.
- `ObjectBaseAssembler.java:82` checks `flag.startsWith("HATES_")` only, so `flags:IGNORE_ACID` falls through to
  `ObjectKindFlag.valueOf("KF_IGNORE_ACID")`, fails, and the record is dropped with an `invalid non-HATES_ flag` error.
- `ObjectBaseReaderTest.anIgnoreElementFlagSkipsTheRecordWhereCAcceptsIt` pins it.
- None on the shipped data, which has only `HATES_*`. It is the mirror image of the `ItemObjectAssembler.java` entry
  above, which knows `IGNORE_` and not `HATES_`; the two assemblers could share one `grab_element_flag` equivalent.

## `objectbase/ObjectBaseAssembler.java` and `ObjectBaseGrammar.g4`: one missing default drops the record, C stores 0

Found 2026-10-17 during `/port backend/parser/ObjectBaseReader.java`. Not fixed.

- C's parser starts with zeroed defaults, so a file that sets only `default:break-chance:` gives a record that omits
  `max-stack:` a max stack of 0.
- `ObjectBaseGrammar.g4:162` starts both defaults as `""` and folds them in as they are, so the record carries `""`, and
  `ObjectBaseAssembler.java:114` fails `Integer.parseInt("")` with an `invalid max stack` soft error and skips the record.
  The same happens to `break:` when only `default:max-stack:` is given.
- `ObjectBaseReaderTest.aMissingDefaultSkipsTheRecordWhereCStoresZero` pins it.
- None on the shipped data, which sets both defaults.

## `objectbase/ObjectBaseAssembler.java`: the synthetic `TV_NONE` base is named "none", C's slot has no name

Found 2026-10-17 during `/port backend/parser/ObjectBaseReader.java`. Not fixed.

- `ObjectBaseAssembler.java:124` appends `new ObjectBase(TValue.TV_NONE, "none", ...)` to every load. C's `kb_info[0]`
  is zero-filled, so its `name` is `NULL`.
- `ObjectRegistry.lookupObjectBase("none")` therefore finds the sentinel, where C has no base called that. Nothing in
  `src/main` looks a base up by that name today.
- If a file declares `name:none:...` the list holds that record and the sentinel; `getBaseFromTVal(TV_NONE)` takes the
  declared one, as C's `kb_info[0]` would hold it.

## `curse/CurseAssembler.java`: a curse with no `msg:` line carries `""`, C leaves `effect_msg` NULL

Found 2026-10-17 during `/port backend/parser/CurseReader.java`. Not fixed.

- `parse_curse_name` in `obj-init.c` zero-fills the curse's object, so `effect_msg` is `NULL` until a `msg:` line appends to it.
- `CurseGrammar.g4` starts `msgInit` at `""`, and `CurseAssembler.java:334` and `:361` pass it to
  `ItemObject.setEffectMessage` as it stands, so an absent line gives `""`. `setEffectMessage`'s Javadoc says `null`
  stands for "no message".
- `CurseReaderTest.absentMsgLeavesAnEmptyStringWhereCLeavesNull` pins it.
- None today: nothing reads the field back except `ItemObject.copy`. `do_curse_effect` in `obj-curse.c` tests the message
  with `if (curse->obj->effect_msg)` before printing, so when `ObjectUtils.doCurseEffect` is ported an empty string would pass
  that test and print a blank line for every curse that has an effect and no `msg:` (`poison`, `hallucination` and
  `stone` in the shipped data).

## `curse/CurseAssembler.java`: the 254-curse limit throws out of the reader and counts survivors, C counts records

Found 2026-10-17 during `/port backend/parser/CurseReader.java`. Not fixed.

- `finish_parse_curse` in `obj-init.c` counts every `name:` record and stops at 254 with `PARSE_ERROR_TOO_MANY_ENTRIES`.
- `CurseAssembler.java:370` compares `results.size()` with `MAX_CURSES` after the first pass, so a record skipped for a bad
  type or flag does not count. A file of 255 records with one bad one loads 254 curses and no limit error in Java, and
  fails in C.
- The check throws `IllegalArgumentException` rather than adding to `errors`. `GrammarDriver.run` catches only
  `IOException` and `ParseCancellationException`, so the exception escapes `CurseReader.parseWithResults`;
  `ObjectDataLoader.loadCurses` catches `Exception` and rethrows it as a `RuntimeException`, so a load is still fatal.
- `CurseReaderTest.aTwoHundredFiftyFifthCurseThrowsOutOfTheReaderRatherThanIntoTheResult` pins the throw and
  `twoHundredFiftyFourCursesLoadAndTheLimitIsTheLastOneC` the boundary. The assembler's class block already says the cap
  throws.
- None on the shipped data, which has 27 curses.


## `activation/ActivationAssembler.java`: `aim:` is true only for exactly `1`, C takes any non-zero number

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

- `parse_act_aim` in `obj-init.c` reads `aim` as a `uint` and runs `act->aim = val ? true : false`, so `aim:2` and `aim:01`
  (read by `strtol` base 0 as 1) are both true.
- `ActivationAssembler.java:66`: `boolean aim = record.aim().equals("1")` compares the raw text, so both are false.
- `ActivationReaderTest.anAimOtherThanExactlyOneIsFalseHereWhereCReadsAnyNonZeroAsTrue` pins it.
- None on the shipped data, which has only `aim:0` (106 records) and `aim:1` (57).

## `activation/ActivationAssembler.java`: an absent `msg:` or `desc:` is an empty string, C holds `NULL`

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

- `parse_act_msg` and `parse_act_desc` run `string_append` on a field that `mem_zalloc` left `NULL`, so a record with no
  such line keeps `NULL`. `cmd-obj.c` tests `!obj->activation->message` and returns, and `obj-info.c` tests
  `obj->activation->desc` before printing.
- `ActivationAssembler.java:92-93`: the grammar's `@init` leaves both at `""` and the assembler passes them straight to
  `new Activation(...)`, so an absent line is an empty string.
- Nothing reads either field today: `Activation` has no getter for `message` or `desc`. When the activation command and
  the object description are ported, an empty string would pass a `!= null` test and print a blank message or a blank
  description line.
- It touches most of the shipped data: 115 of 163 records have no `msg:` and 47 have no `desc:`.
- `ActivationReaderTest.anAbsentMsgOrDescIsAnEmptyStringHereWhereCHoldsNull` pins it.

## `activation/ActivationAssembler.java` and `ActivationParseRecord.java`: Javadoc names a C function that does not exist and misdescribes the others

Found 2026-10-17 during `/port backend/parser/ActivationReader.java`. Not fixed.

- `ActivationAssembler.java:31` calls the class the "Java port of `parse_activation_*` in `src/obj-init.c`". `obj-init.c` has
  no such functions; the handlers are `parse_act_*`.
- `ActivationParseRecord.java:34-35` says `parse_act_*` "only stashes strings on the `activation` struct" and a "later
  finalization pass" interprets them. `parse_act_aim`, `parse_act_level` and `parse_act_power` convert their values as they
  read them, and `finish_parse_act` only counts, copies and numbers the records.
- Not a behaviour difference; recorded here because the fix is in the Javadoc.

## `objectproperty/ObjectPropertyAssembler.java`: a `subtype:` on a record that is not a flag is refused, C stores it

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `parse_object_property_subtype` in `obj-init.c` sets `prop->subtype` on whatever record is current. The data-file comment
  says only flags use it, but nothing checks.
- `ObjectPropertyAssembler.java:94` adds `"a subtype on a non flag type"` to the soft errors and skips the record, so
  `type:stat` with `subtype:sustain` loads in C and is lost here.
- `ObjectPropertyReaderTest.aSubtypeOnANonFlagIsASoftErrorHereWhereCAcceptsIt` pins it.
- None on the shipped data: all 38 subtypes are on flags.

## `objectproperty/ObjectPropertyAssembler.java`: the sentinel codes resolve, and a missing `code:` skips the record

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `parse_object_property_code` in `obj-init.c` looks the code up with `code_index_in_array` in `datafile.c`, an exact match
  against `obj_mods` (the five stats and eleven modifiers, no `NONE` or `MAX`), `obj_flags` (which starts with `NONE` and
  has no maximum) or `element_names` (neither). Anything else is `PARSE_ERROR_INVALID_OBJ_PROP_CODE`.
- `ObjectPropertyAssembler.java:120`, `:124` and `:129` use `valueOf("OM_" + code)`, `valueOf("OF_" + code)` and
  `valueOf("ELEM_" + code)`, which also find `OM_NONE`, `OM_MAX`, `OF_MAX`, `ELEM_NONE` and `ELEM_MAX`. So `code:NONE` on a
  stat, modifier, resistance, vulnerability, immunity or ignore, and `code:MAX` on anything, load here and fail in C. Only
  `code:NONE` on a flag is valid in both.
- The other way round: a record with no `code:` line is valid in C, where `index` stays 0 (`STR` for a stat). Here the code
  is `""`, `valueOf("OM_")` throws, and the record is skipped with `"has an invalid code: "`, an empty code in the message.
- `ObjectPropertyReaderTest.sentinelCodesAreAcceptedHereWhereCRejectsAllButFlagNone` and
  `aRecordWithNoCodeIsSkippedHereWhereCHoldsIndexZero` pin both.
- None on the shipped data: all 79 records carry a real code.

## `objectproperty/ObjectPropertyAssembler.java`: `bindui:` aux is true for any text but exactly `0`, C tests the number

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `parse_object_property_bindui` in `obj-init.c` runs `bool isaux = (parser_getint(p, "aux") != 0)`. `strtol` reads `00`
  and `-0` as zero, so both are false.
- `ObjectPropertyAssembler.java:196`: `boolean isAux = !record.aux().equals("0")` compares the raw text, so both are true.
- `ObjectPropertyReaderTest.anAuxWrittenAsZeroOtherThanPlainZeroIsTrueHereWhereCReadsFalse` pins it.
- None on the shipped data, which has only `0` and `1`. Same family as the `ActivationAssembler.java` `aim:` entry above.

## `objectproperty/ObjectPropertyAssembler.java` and `ObjectPropertyParseRecord.java`: Javadoc says the assembler looks `bindui:` up in the UI-entry registry, it does not

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `ObjectPropertyParseRecord.java` says the assembler turns the raw strings into the typed property, "looking the
  `bindui` target up in the UI-entry registry". `ObjectPropertyAssembler.java` has that call commented out
  (`// UIEntry uientry = UIRegistry.getUIEntry(uiEntryName);`) and stores the name in an `ObjectProperty.UIBinding`
  instead. `ObjectPropertyReaderTest`'s own class block already says so.
- `ObjectDataLoader.loadObjectProperties` still says it "must run after the UI entries, which the property assembler
  resolves `bindui` targets against", and `GameConstants.java` marks the call "Dependent on UIEntry". Neither is true while
  the lookup is commented out.
- C binds at parse time, using the `prop->index` the record has reached, so a `bindui:` written before `code:` binds index 0.
  The port stores the binding with the whole property, so the order of the two lines does not matter.
- Not a behaviour difference in the shipped file; recorded here because the fix is in the Javadoc.

## `objectproperty/ObjectPropertyAssembler.java`: an unknown `type-mult:` tval keeps the record, every other bad field skips it

Found 2026-10-17 during `/port backend/parser/ObjectPropertyReader.java`. Not fixed.

- `parse_object_property_type_mult` returns `PARSE_ERROR_UNRECOGNISED_TVAL`, fatal in C. The assembler's usual rule for a bad
  field is `continue` out to the next record.
- `ObjectPropertyAssembler.java:175` and the `NumberFormatException` catch after it add the error and carry on with the next
  `type-mult:` line, because the `continue` is inside the loop over the record's lines. The record is kept without that line.
- `ObjectPropertyReaderTest.anUnknownTypeMultTvalIsASoftErrorThatKeepsTheRecord` pins it.
- Also in that method: the `default ->` branch of the `code:` switch (`ObjectPropertyAssembler.java:132`) can never run,
  because an unknown, `NONE` or `MAX` type has already skipped the record above it.

## `egoitem/EgoItemAssembler.java`: an unknown `act:` name skips the record, C keeps it with no activation

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parse_ego_act` in `obj-init.c` stores `findact(name)` and returns `PARSE_ERROR_NONE`. `findact` walks the activation chain
  and answers `NULL` when nothing matches, so the ego loads with no activation and no message.
- `EgoItemAssembler.java:390` looks the name up and, on `null`, adds `"has an unknown activation type"` and `continue`s past
  the whole record.
- `EgoItemReaderTest.unknownActivationIsReportedAndSkippedHereWhereCKeepsTheRecordWithNone` pins it.
- None on the shipped data: no ego has an `act:` line, and none has a `time:` line either.

## `egoitem/EgoItemAssembler.java`: `alloc:` figures are not checked against 0 to 255

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parse_ego_alloc` returns `PARSE_ERROR_OUT_OF_BOUNDS` when either end of the range is above 255 or below 0. The commonness is not
  checked.
- `EgoItemAssembler.java:289` and `:299` parse the lower and upper bound with `Integer.parseInt` and keep any int, so
  `alloc:10:256 to 300` and `alloc:10:-1 to 5` load.
- `EgoItemReaderTest.allocFiguresOutsideZeroTo255AreAcceptedHereWhereCRejectsThem` pins it.
- None on the shipped data: the largest upper bound is 127. A depth above 255 would never be reached in play, so the harm is
  a bad data file loading quietly.

## `egoitem/EgoItemAssembler.java` and `EgoItemsGrammar.g4`: a later `curse:` line with no power erases the earlier one, C ignores it

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parse_ego_curse` adds the curse "only if it has power", so `curse:vulnerability:10` then `curse:vulnerability:0` leaves
  power 10 in C. The zero line does nothing.
- `EgoItemsGrammar.g4:212` puts each line into a map by curse name, so the second line replaces the first, and
  `EgoItemAssembler.java:254` then drops the curse because the last power is not above 0.
- `EgoItemReaderTest.aLaterCurseLineWithNoPowerErasesTheEarlierOneHereWhereCKeepsIt` pins zero and negative.
- None on the shipped data, which has no `curse:` line at all.

## `egoitem/EgoItemAssembler.java`: `HATES_<element>` is an unknown flag, C accepts it

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `grab_element_flag` in `obj-init.c` takes the text after the first underscore as an element name and sets
  `EL_INFO_IGNORE` for an `IGNORE` prefix and `EL_INFO_HATES` for a `HATES` prefix. The prefix test is
  `strncmp(flag_name, "IGNORE", under - flag_name)`, which also passes any shorter start of the word such as `IGN_FIRE`.
- `EgoItemAssembler.java:118` handles the `IGNORE_` prefix only. `HATES_FIRE` then fails both the `OF_` and `KF_` lookups and
  the record is skipped with `"has an unknown flag"`.
- `EgoItemReaderTest.aHatesFlagIsAnUnknownFlagHereWhereCAcceptsIt` pins it.
- None on the shipped data: no ego uses `HATES_`. `ElementInfoEnum.EL_INFO_HATES` exists, so only the assembler stands in the
  way.

## `egoitem/EgoItemAssembler.java`: the `NONE` and `MAX` names are accepted for elements and modifiers, C has neither

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `element_names` and `obj_mods` in `obj-init.c` are the names from `list-elements.h`, `list-stats.h` and
  `list-object-modifiers.h` and nothing more. `grab_element_flag` loops to `ELEM_MAX` exclusive, and `grab_index_and_int` and
  `grab_rand_value` stop at the terminating `NULL`.
- `EgoItemAssembler.java:121`, `:166`, `:188` and `:204` use `valueOf` on `ElementEnum` and `ObjectModifier`, which do have
  `ELEM_NONE`, `ELEM_MAX`, `OM_NONE` and `OM_MAX`. So `IGNORE_NONE`, `IGNORE_MAX`, `RES_NONE`, `RES_MAX` and `values:NONE[1]`
  load here and are `PARSE_ERROR_INVALID_FLAG` or `PARSE_ERROR_INVALID_VALUE` in C.
- `EgoItemReaderTest.sentinelElementAndModifierNamesAreAcceptedHereWhereCRejectsThem` pins the five.
- None on the shipped data. Same family as the `ObjectPropertyAssembler.java` `code:` entry above.

## `egoitem/EgoItemAssembler.java`: eligible kinds run first to last, C builds the list last to first

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parse_ego_type` and `parse_ego_item` push each kind on the head of `poss_items`. After `type:sword` and
  `item:light:Lantern` the list is Lantern, then the swords from the highest `kidx` down.
- `EgoItemAssembler.java:90` adds every `type:` line's kinds in `k_info` order, and `:318` adds every `item:` line's after
  them, whatever order the lines were written in.
- It matters to the readers of the list. `object_info_ego` in `obj-info.c` takes `poss_items->kidx`, the head, as the
  example item when it describes the ego, and the port's `possItems.get(0)` would be a different kind.
  The other users of the list that were read (`lookup_ego_item` in `obj-util.c` and the knowledge menu in `ui-knowledge.c`)
  only test membership or count.
- `EgoItemReaderTest.eligibleKindsRunFirstToLastHereWhereCRunsLastToFirst` pins the Java order.
- Differs on every ego with more than one eligible kind, which is 99 of the shipped 107.

## `egoitem/EgoItemAssembler.java`: omitted `combat:` and `time:` dice are `null`, C holds zero

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `parse_ego_name` zeroes the record, so an ego with no `combat:` line has three zero `struct random`s and no `time:` line a
  zero time. `ego_apply_magic` in `obj-make.c` rolls `randcalc(ego->to_h, ...)` for every ego without a test.
- `EgoItemAssembler.java:331` and the two after it, and `:397`, leave `toH`, `toD`, `toA` and `time` as `null`. The
  constructor Javadoc of `EgoItem` says so on purpose, so every caller that rolls them has to guard.
- `EgoItemReaderTest.omittedCombatAndTimeDiceAreNullHereWhereCHoldsZero` pins it.
- On the shipped data 71 of the 107 egos have no `combat:` line, so a port of `ego_apply_magic` meets the `null` straight
  away. Same family as the `ItemObjectAssembler.java` null dice entry above.

## `egoitem/EgoItemAssembler.java`: a skipped record takes no `egoIndex`

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `finish_parse_ego` numbers every ego by its place in the file, and a parse error in C ends the whole load, so a gap never
  appears there.
- `EgoItemAssembler.java:414` increments `index` only for a record it keeps, so after a skipped record every later ego is one
  number lower than its place in the file. `egoIndex` still equals the position in the returned list.
- `EgoItemReaderTest.aSkippedRecordTakesNoEgoIndexHereWhereCNumbersByFilePosition` pins it.
- None on the shipped data, where no record is skipped. It would matter if anything saved or compared the index across a load
  that lost a record.

## `egoitem/EgoItemAssembler.java`: two error messages are misspelled

Found 2026-10-17 during `/port backend/parser/EgoItemReader.java`. Not fixed.

- `EgoItemAssembler.java:248` builds `"beginning at line:" + line` with no space after the colon, where every other message
  has one.
- `EgoItemAssembler.java:260` says `"an malformed curse power"`.
- Cosmetic. `EgoItemReaderTest` matches on `unknown curse` and the other stable parts, so it will not notice a fix.

## `artifact/ArtifactAssembler.java`: `IGNORE_` and `HATES_` flags are unknown, C sets element info

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_flags` tries `grab_flag` and then `grab_element_flag`, which turns `IGNORE_FIRE` and `HATES_FIRE` into
  `EL_INFO_IGNORE` and `EL_INFO_HATES` on `el_info`.
- `ArtifactAssembler.java:158` only tries `ObjectFlag.valueOf("OF_" + ...)`, so both are `"has an unknown flag"` and the record
  is skipped.
- `ArtifactReaderTest.ignoreAndHatesFlagsAreUnknownHereWhereCSetsElementInfo` pins both.
- None on the shipped data. Same family as the `EgoItemAssembler.java` `HATES_` entry above.

## `artifact/ArtifactAssembler.java`: `alloc:` figures are not checked against 0 to 255

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_alloc` returns `PARSE_ERROR_OUT_OF_BOUNDS` for a bound above 255 or below 0.
- `ArtifactAssembler.java:271` and `:281` keep any int, so `alloc:1:300 to 400` and `alloc:1:-5 to 10` load.
- `ArtifactReaderTest.allocFiguresAreNotCheckedAgainstZeroTo255HereWhereCRefusesThem` pins it.
- None on the shipped data (largest upper bound 127).

## `artifact/ArtifactAssembler.java`: an unknown `act:` name skips the record, C keeps it with no activation

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `findact` answers `NULL` when nothing matches and `parse_artifact_act` stores it without an error.
- `ArtifactAssembler.java:292` adds `"has an unknown activation"` and `continue`s past the record.
- `ArtifactReaderTest.anUnknownActNameSkipsTheRecordHereWhereCKeepsItWithNoActivation` pins it.
- None on the shipped data. Same family as the `EgoItemAssembler.java` entry above.

## `artifact/ArtifactAssembler.java`: `graphics:` on a kind that is not `INSTA_ART` is ignored, C refuses it

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_graphics` returns `PARSE_ERROR_NOT_SPECIAL_ARTIFACT` unless the kind has `KF_INSTA_ART`.
- `ArtifactAssembler.java:363` skips the glyph silently when the flag is missing and keeps the record.
- `ArtifactReaderTest.graphicsOnAnOrdinaryKindIsIgnoredHereWhereCRefusesIt` pins it.
- None on the shipped data: every `graphics:` line is on a special kind.

## `artifact/ArtifactAssembler.java`: a record with no `base-object:` is rejected, C keeps it with tval 0

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_name` zeroes the record. Only `weight:`, `cost:`, `graphics:`, `act:` and `time:` look the kind up, so a record
  with none of them loads in C.
- `ArtifactAssembler.java:318` reports `"unknown object type: null"` and skips it.
- `ArtifactReaderTest.aRecordWithNoBaseObjectIsRejectedHereWhereCKeepsItWithTvalZero` pins it.
- None on the shipped data: all 138 records have one.

## `artifact/ArtifactAssembler.java`: on a second parse a special light's activation and time go to the artifact, C keeps them on the kind

Found 2026-10-17 during `/port backend/parser/ArtifactReader.java`. Not fixed.

- `parse_artifact_act` and `parse_artifact_time` test `k->kidx >= z_info->ordinary_kind_max`, so the three special lights
  (Phial, Star, Arkenstone) give the kind its activation and time on every run.
- `ArtifactAssembler.java:325` tests `objectKind == null && tVal == TV_LIGHT`, true only when this record minted the kind. The
  birth re-run via `ObjectDataLoader.loadArtifacts` finds the kinds already there, so the artifact gets them itself.
- `ArtifactReaderTest.aSecondParseGivesTheSpecialLightsTheirActivationAndTimeHereWhereCKeepsThemOnTheKind` pins it.
- The kind table is unchanged by the second run (checked), so only the artifact objects differ.

## `projection/ProjectionAssembler.java`: a line left out is `""`, C holds `NULL`

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- `parse_projection_code` zero-fills the record with `mem_zalloc`, so a projection with no `name:`, `player-desc:` or
  `lash-desc:` has `NULL` in that field (the shipped file has 25 `name:`, 27 `player-desc:` and 23 `lash-desc:` lines in 56 records).
- `ProjectionAssembler.java:139-143` copies `record.name()`, `playerDesc()` and `lashDesc()` as they come, and the grammar fills
  an absent directive with `""`. So `Projection.getName()` answers `""` where C has no name.
- `ProjectionReaderTest.aLineLeftOutIsAnEmptyStringWhereCHoldsNull` pins it.
- Same family as the `ActivationAssembler.java` entry above. Anything that tests for "no name" has to test for empty.

## `projection/ProjectionAssembler.java`: a `numerator:`, `divisor:` or `damage-cap:` above 2147483647 drops the record

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- C reads these with `parser_getuint` and stores the result in an `int` field, so 2147483648 is kept (as a negative number) and
  nothing is refused above that.
- `ProjectionAssembler.java:147`, `:182` and `:201` use `Integer.parseInt`, so the value is an `"invalid ..."` error and the
  record is skipped. A skipped `type:element` record then fails the file ("Invalid number of elements").
- `ProjectionReaderTest.aNumeratorAboveIntMaxDropsTheRecordAndFailsTheFileWhereCReadsAnUnsignedInt` pins it for `numerator:`.
- None on the shipped data, where the largest figure is the 1600 damage cap.

## `projection/ProjectionAssembler.java`: `msgt:NONE` is accepted, and a numeric `msgt:` is one constant late

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- `message_lookup_by_name` in `message.c` compares the text with the names built from `list-message.h` (`GENERIC` first, `MAX`
  last, no `NONE`), or takes a number below `MSG_MAX`, which is 153.
- `ProjectionAssembler.java:404` is `MessageType.valueOf("MSG_" + ...)`, so `msgt:NONE` gives `MSG_NONE`, an enum constant C
  does not have (an accepted divergence for the constant itself, but here it makes a line load that C rejects).
- `ProjectionAssembler.java:411` is `MessageType.values()[msgOrdinal]`, and `MSG_NONE` takes ordinal 0, so `msgt:0` is
  `MSG_NONE` (C: `GENERIC`), `msgt:1` is `MSG_GENERIC` (C: `BIRTH`), and 153, which C rejects, is `MSG_SCRAMBLE`. The upper bound
  at `:406` uses `MSG_MAX.ordinal()`, which is one higher than C's `MSG_MAX` for the same reason.
- A data file cannot reach the numeric path today (`ProjectionLexer.g4` only lexes upper-case names; see `grammar_issues.md`), so
  only the `NONE` case shows through the reader. `ProjectionReaderTest.msgtNoneIsAcceptedWhereCHasNoSuchName` and
  `ProjectionAssemblerTest.numericMsgtIsOneConstantLateAndOneNumberTooWideWhereCCountsFromGeneric` pin them.
- `ProjectionAssemblerTest.numericMsgtBoundsFollowMsgMax` already encodes the shifted bound as correct.
- Same family as the `BlowMethodAssembler.java` entry above.

## `projection/ProjectionAssembler.java` and `GrammarDriver.java`: a misplaced element gives no projections and no error, and the loader carries on

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- `parse_projection_code` returns `PARSE_ERROR_ELEMENT_NAME_MISMATCH` when one of the first 25 codes is not the element at that
  position, and the parse quits.
- `ProjectionAssembler.java:137` throws a `ParseCancellationException` with the message. `channel/parser/GrammarDriver.java:141`
  catches it and returns `errorCatcher.getErrors()`, which holds only lexer and grammar errors, so the message goes to the log and
  never reaches the `ParseResult`. The assembler's Javadoc says the message "carries the errors gathered so far"; it does, to a
  catch block that discards it.
- `ErrorParsing.reportAndCheck` answers `true` when there are no errors, whatever the item count, so `WorldDataLoader.loadProjections`
  registers an empty projection list and start-up carries on, where C quits.
- `ProjectionReaderTest.aSwappedElementBlockGivesNoProjectionsAndNoErrorMessage` pins the empty, silent result.
- None on the shipped data, whose element block is in order. Any assembler that throws `ParseCancellationException` is open to
  the same thing.

## `projection/ProjectionAssembler.java`: an unknown `code:` after the 25 elements skips the record, C never reads it

Found 2026-10-18 during `/port backend/parser/ProjectionReader.java`. Not fixed.

- `parse_projection_code` compares the code with `element_names[index]` only while `index < ELEM_MAX`. After that it stores
  nothing from the code, so any text loads and the projection is whatever the other directives say.
- `ProjectionAssembler.java:123-125` resolves every code through `ProjectionEnum` and skips a record whose code is unknown, with
  an error. The grammar also limits the code to an upper-case identifier.
- `ProjectionReaderTest.anUnknownCodeBeyondTheElementBlockSkipsTheRecordWhereCNeverReadsTheCode` pins it.
- None on the shipped data. The assembler's `resolveCode` Javadoc already says C does not look the code up.
