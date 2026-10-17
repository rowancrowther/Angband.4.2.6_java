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
