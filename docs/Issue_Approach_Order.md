# Suggested order of approach: assembler and grammar issues

**Date:** 2026-10-19

**What this is:** a suggested order for working through every entry in `docs/Assembler_Issues.md` (91 entries, tagged **A**) and
`docs/grammar_issues.md` (108 entries, tagged **G**), arranged in stages from the faults that reach the shipped data down to
comment-only fixes. It was built from the two files as they stood on the date above. Nothing in it was re-checked against the source, so
confirm an entry still holds before working on it. Entries are named by their heading, shortened.

This is a route, not a checklist. `docs/ROADMAP.md` stays the only place anything is ticked.

## How the order was chosen

- Faults that change a loaded game on the shipped data come first, because they are the only ones that can break play today.
- Faults that crash or lose records without saying so come second.
- Anything that is the same fix in many places is done once, as a helper or shared import, before the per-file work that uses it.
- Assembler work comes before grammar work. Several grammar fixes make `ParseRecord` fields absent-capable, and the assemblers should
  settle the absent-value convention before they are touched a second time.
- Grammar edits are batched by shared import, so each ANTLR regeneration covers as many lexers as possible.
- Every entry has a test that pins the Java behaviour ("...HereWhereC..."). Each fix flips that test, so the test changes ride with the fix.

## Stage 0: decisions that set the conventions

Take these first. They are the eight "Question for Rowan" entries plus two conventions that many entries lean on.

- **Absent value.** When a directive is left out, does the Java field hold `null` (C's `NULL`), `""`, or a zero value (`Random.Zero()` for
  dice)? Stage 1 and Stage 3 apply the answer in about twenty places.

**Answer: Absent values are set to null**

- **Strict or C-lenient.** Where C accepts something sloppy (a colour typo becoming white, a bare flag list, a colour name), does the
  port copy C or stay strict on purpose? Stage 3 and Stage 7 apply the answer.

**Answer (2026-10-19): colours are C-lenient.** An unknown colour letter or name loads as white, as in `color_char_to_attr` and
`color_text_to_attr` (`z-color.c`), and a space is `COLOUR_DARK`. Names are matched case-insensitively. This covers the assemblers
(`ObjectBaseAssembler`, `MonsterSpellLevelAssembler`, `FlavourAssembler`) and the grammars (`PitLexer.g4`, `VisualsGrammar.g4`,
`imports/AngbandDisplayCharacter.g4`). Colours only: the bare flag list and the other items below are still open.

**Strict or sloppy: when each group is decided (2026-10-19).** Each row is a group of entries where C is more or less forgiving than
the Java. Colours are settled above; every other group is only tiered here, not decided.

- **Tier 0:** decide now, before any Stage 1 work.
- **Tier 1:** decide before leaving this document.
- **Tier 2:** not decided here. It goes in the "things not to drop" section of a precis.

| Group | Tier | What it covers |
|---|---|---|
| Colours | decided | Lenient, see above. |
| Absent fields | decided | Settled as `null` by the first answer; the strict/sloppy side is a missing field dropping the record where C stores 0. |
| Whitespace and separators | decided | Indented lines, flags split on spaces, trailing blanks, `alloc:` spacing. |
| Missing or empty directives | decided | A bare `name:`, a fixed directive order and count, an empty `flags:`. |
| Unresolved names | decided | An unknown `act:`, `mon-ban:` or `fallback:` name, an unknown `color-cycle:` group. |
| Limits and range checks | decided | `drop:` bounds, `alloc:` 0 to 255, the 254 limits, the Visuals caps. Java is the lenient side here. |
| Boolean parsing | decided | `aim:`, `uniques:`, `destroy:` and `magic:` true only for `1`; `bindui:` aux true for anything but `0`. |
| Case sensitivity | decided | Exact-case `msg:` and `msgt:` lookups against case-insensitive roles, vault `type:` and realm `stat:`. |
| Repeated directives | decided | Overwrite, append or reject for `msg:`, `desc:`, `curse:`, `race-flag:`, `base:` and `magic:`. |
| Text content | decided | Trimmed `name:`, glyphs outside the Basic Multilingual Plane, colons in descriptions, text after a value, upper-case-only identifiers. |
| Empty file or missing `record-count:` | decided | A header with no records, an empty file, a header the upstream file does not carry. |
| Reversed chains | decided | Always echo C's ordering. Supersedes the 2026-10-17 decision (file order, sorted by registry position only where C's order is needed). |
| Bad record: drop it, drop the file, or stop the load | decided | Stop the load, see "Answer for the Tier 1 row" below. A lexer error empties the file, an assembler error drops one record, some paths abort the load. |
| Sentinel names `NONE` and `MAX` | 1 | Accepted by the Slay, Brand, EgoItem, Shape, Body, PlayerRace, Projection and PlayerProperty assemblers; C rejects them. |
| A field on the wrong kind of record | 1 | `graphics:` on a non-`INSTA_ART` artifact, `subtype:` on a non-flag property, `value:` and `code:` on the wrong property record. |
| Numbers | 2 | Octal, hex and plus signs read by `strtol` base 0 in C, decimal only in Java. |

"Unresolved names" was listed twice in the request; it is one row, and the second listing was read as "Missing or empty directives".

**Answers for the Tier 0 groups (2026-10-19).** The rule running through them is "match C exactly": where C accepts something the
Java rejects, the Java accepts it, and where C rejects something the Java accepts, the Java rejects it. Where C raises a parse error,
the port's usual soft error that skips the record was assumed; the Tier 1 row "Bad record" has since been decided as stop the load,
which supersedes that assumption (see the answer below).

**Answer for the Tier 1 row "Bad record" (2026-10-21): stop the load.** Any parse error ends the load with the file and line, as
`run_parser` and `print_error` in `datafile.c` do, and an error from a `finish_parse_*` function (the 254 limits included) does the
same. This covers syntax errors, assembler errors and limit overflows alike, so the port no longer skips a bad record and carries on.

- **What changes in the code.** Every `errors.add` followed by `continue` in the assemblers becomes a stop, and the mixed
  `IllegalArgumentException`, `RuntimeException` and `ParseCancellationException` throws become one deliberate stop-the-load path
  that carries the file, the line and the message. `ProjectionAssembler.java` no longer has its error swallowed by `GrammarDriver`.
- **What changes in the tests.** Tests that pin skip-and-continue flip to expect the stop.
- **What changes in these documents.** The roughly 45 grammar entries that say "the whole file loads as nothing" describe intended
  behaviour, not a fault. Entries already marked CLOSED on the soft-error assumption (for example the unknown spell in
  `message-*:`) stand as closed but are due this second change.

- **Absent fields: C-lenient.** A record that leaves a directive out loads with C's zero, `null` or default, and `ParseRecord` fields
  become absent-capable. `MonsterSpellLevelAssembler` prepends the implicit level 0 (power 0, `null` text) when the first level's power
  is above 0 or the list is empty, so every later walker can start at the head as C does.
- **Whitespace and separators: match C exactly in both directions.** Indented lines and comments, flags split on any run of spaces
  and bars, blanks after the last token and any spacing round `alloc:` figures are accepted. The places where Java is the more lenient
  side (trailing blanks after a flag in `BrandLexer.g4` and `ProjectionLexer.g4`, optional spaces round `to` in `ItemObjectLexer.g4`,
  trimmed `name:` in `BlowMethodGrammar.g4` and `ActivationsLexer.g4`) are made to reject as C does.
- **Missing or empty directives: match C.** Directives come in any order, any can be omitted, a bare `name:` is a record, and `flags:`
  may be empty. This is the Stage 7a and 7b work.
- **Unresolved names: match C both ways.** Where C is silent (unknown `act:`, `fallback:`, `mon-ban:`, `color-cycle:` group, projection
  `code:` after the 25 elements) the record loads silently. Where C raises an error (unknown spell in `message-*:`, unknown
  `type-mult:` tval, `subtype:` on a non-flag, which C stores) the Java follows C: a soft error and a skipped record for the first two,
  and the `subtype:` stored. [<span style="color:#006400">**CLOSED**</span> 26-10-20 for the unknown spell in `message-*:` in `MonsterGrammar.g4` and
  `MonsterAssembler.java`; still open for `type-mult:` and `subtype:`]
- **Limits and range checks: enforce everything C enforces.** The `drop:` bounds, unsigned negatives, `alloc:` 0 to 255, the 254-brand,
  slay and curse limits (the curse limit counts records, not survivors) and the Visuals caps. This answers the Stage 0 question on
  `VisualsCycleAssembler` caps: enforce the 8, 64 and 32 limits.
- **Boolean parsing: match C.** The number is parsed, then C's test for that field is applied through one helper: `!= 0` for most,
  `== 1` for `pit:`.
- **Case sensitivity: match C both ways.** `msg:`, `msgt:` and realm `stat:` become case-blind, with the numeric form for message
  names. The `friends:` role and vault `type:` become case-exact.
- **Repeated directives: match C per handler.** Join, replace, bind every line or reject, as each C handler does. `bindui:` in
  `ObjectPropertyParseRecord` becomes a list, and a later zero-power `curse:` no longer erases an earlier one.
- **Text content: match C throughout.** Free-text fields take the rest of the line, colons included, text after a value is ignored,
  the `RealmLexer.g4` second colon on `stat:` is dropped, and glyphs widen from `char` to a code point.
- **Empty file or missing `record-count:`: the header is optional.** If present it is still checked as a soft error. An empty file
  loads where C loads it and fails where C fails it, so upstream data files load unchanged.
- **Reversed chains: always echo C's ordering.** This replaces the 2026-10-17 decision, so the `brands-slays-order-at-point-of-use`
  approach no longer stands.
- A: `visuals/VisualsCycleAssembler.java` caps. Enforce the 8, 64 and 32 limits, or keep "no limit"?

**Answer (2026-10-19): enforce as C does.** A 33rd colour on a cycle and a 513th `cycle:` line are errors that refuse the file; a ninth
group and a 65th cycle in a group are silently dropped. This confirms the Tier 0 "Limits and range checks" answer.
- G: `VisualsGrammar.g4`. Accept any single character as a colour and map the unlisted ones to white, or stay strict? `PitLexer.g4` shares the question.

**Answer (2026-10-19): lenient.** Any single character is accepted as a colour and the unlisted ones map to white. This confirms the
Stage 0 "Strict or C-lenient" answer and covers `VisualsGrammar.g4` and `PitLexer.g4`.
- G: `RoomProfileGrammar.g4`. Is the single `|`-separated `flags:` line deliberate?

**Answer (2026-10-19): not deliberate, match C.** A second `flags:` line accumulates and any run of spaces and bars separates flags.
This follows the Tier 0 "Whitespace and separators" and "Repeated directives" answers and is done in Stage 7d.
- G: `DungeonProfileGrammar.g4`. Is requiring a directive after each `name:` deliberate?

**Answer (2026-10-19): not deliberate, load it as C does.** `parse_profile_name` in `generate.c` makes a zeroed record and the other six
directives are optional, so a bare `name:` is a record with every number zero. This follows the Tier 0 "Missing or empty directives"
answer and is done in Stage 7b.
- G: `ChestTrapGrammar.g4`. Join a repeated `msg:` as C does, or refuse it?

**Answer (2026-10-19): join, as C does.** `parse_chest_trap_msg` and `parse_chest_trap_msg_death` in `obj-chest.c` use `string_append`, so a
repeated `msg:` or `msg-death:` line is appended with no separator. This follows the Tier 0 "Repeated directives" answer and is done in
Stage 7c. Requiring `code:` stays deliberate.
- G: `PlayerTimedGrammar.g4`. Support `effect-yx`, `effect-expr` and `effect-msg` on timed effects?

**Answer (2026-10-19): support all three.** In `player-timed.c` they fill the effect just opened by `on-begin-effect:` or
`on-end-effect:`: `effect-yx` sets y and x, `effect-expr` binds an expression to the dice (an error if no `effect-dice:` came first)
and `effect-msg` appends to the message. Done in Stage 8, reusing the fields `EffectParseRecord` already carries.
- G: `Random.g4`. Should a number above `INT_MAX` return `null` rather than throw? Decides Stage 2's `Random` item. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `RandomReader.parse` and `Random.parseStr` return `null`; see Stage 2 item 1.]

**Answer (2026-10-19): return `null`, as C refuses it.** `parse_random` in `parser.c` refuses a number above `INT_MAX` with
`PARSE_ERROR_NOT_RANDOM`, so the assemblers record a line-numbered error instead of the exception stopping start-up. The Stage 2
`Random` item is done this way, and the "by design" note on `RandomReader.parse` goes.
- G: `LoreGrammar.g4`. Follow C's optional fields, or the shapes `lore_save` writes? Parked in Stage 10.

**Answer (2026-10-19): follow C.** `init_parse_lore` in `mon-init.c` takes `blow:` with only the method required, a bare `flags:`, and
the `monster.txt` directives routed to an ignored handler. The grammar does the same when it is picked up in Stage 10. The cast
counts, `base:`, `drop-base:` and the `friends:` dice form are fixed either way.
- Anything decided as "keep the Java behaviour on purpose" goes in `docs/Accepted_Deviations.md` and drops out of the later stages.

## Stage 1: faults that reach the shipped data

These change what a loaded game holds today, even where nothing reads the value yet.

1. **A: `monster/MonsterAssembler.java` `spell_power`.** Never defaulted to the level; 595 of 624 races differ from C. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `MonsterGrammar.g4` sets `spellPowerInit` from `depth:` and a later `spell-power:` overrides it, in file order as in C; `MonsterReaderTest` passes with the flipped tests.]
2. **A: `monster/MonsterAssembler.java` list order.** Drops, friends, mimics and shapes are reversed in C, drops are grouped, and 34 races differ. The order is observable in drop and mimic picks. Per the Stage 0 answer "always echo C's ordering", these lists end up in C's order. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `MonsterGrammar.g4` pushes `drop:` and `drop-base:` onto one list, and `mimic:`, `friends:`, `friends-base:` and `shape:` onto theirs, with `addFirst`, as C's head-pushed chains do; `MonsterAssembler.java` keeps that order and the second-pass resolution does not reorder it. `MonsterReaderTest` passes (87 tests) with order tests for drops, mixed drops, friends, friends-base, mimics and shapes.]
3. **A: `egoitem/EgoItemAssembler.java` eligible kinds.** Run first to last, C runs last to first; 99 of 107 egos differ and `object_info_ego` takes the head. Same Stage 0 answer: the list is built in C's order. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `EgoItemsGrammar.g4` pushes `type:` and `item:` lines onto one `itemRefsInit` list with `addFirst`, as C's head-pushed chain does, and `EgoItemAssembler.java` appends each `type:` line's kinds reversed. `EgoItemReaderTest` passes with order tests for single and repeated `type:` lines, `item:` lines and mixed lines; the full suite is green.]
4. **A: `itemobject/ItemObjectAssembler.java` null dice.** `ObjectUtils.objectPrep` already throws on `& Ration~ of Food` and on `Cure Light Wounds`. Start the five dice as `Random.Zero()`. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `ItemObjectAssembler.java` starts `attBase`, `attToh`, `attTod`, `acToa`, `pVal`, `charges`, `pileAmount` and `time` as `Random.Zero()`, and `ObjectKind.copy` and `Effect.copy` are null-safe. `ItemObjectAssemblerObjectPrepTest` runs `objectPrep` and `copy` over all 375 shipped kinds and the full suite is green.]
5. **A: `egoitem/EgoItemAssembler.java` null `combat:` and `time:` dice.** 71 of 107 egos; same fix as the item dice, so do the two together. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `EgoItemAssembler.java` starts `toH`, `toD`, `toA` and `time` as `Random.Zero()`, and a present line is parsed over the top. `EgoItemReaderTest` has `omittedCombatAndTimeDiceAreAZeroRandomAsInC` and `realFileEgosHoldNoNullDiceAndAllCopy` (all 107 egos hold no null dice and copy), the upstream table now records `0+0d0M0` for `time` on all 107 rows, and the full suite is green.]
6. **A: `trap/TrapAssembler.java`.** Five shipped kinds have a `null` power, and the trap-placing port will meet it at once. The bad-effect `null` list is in the same entry; see Stage 2. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `TrapAssembler.java` starts `power` as `Random.Zero()`, starts the display character as `AngbandDisplayCharacter.blank`, and drops the record (before `index++`) when either `EffectAssembler.assemble` call returns `null`. `TrapReaderTest` has `noShippedKindHasANullPowerAsInC`, the bad-effect tests expect the record dropped, and the full suite is green.]
7. **Absent text is `null`, as the Stage 0 convention decides.** [<span style="color:#006400">**CLOSED**</span> 26-10-20: all four sub-items below; the full suite is green.]

   a. A: `activation/ActivationAssembler.java` absent `msg:` and `desc:` (115 and 47 of 163 records). [<span style="color:#006400">**CLOSED**</span> 26-10-20: `ActivationsGrammar.g4` starts `messageInit` and `descInit` at `null`; `ActivationReaderTest` passes (44 tests) with the flipped tests.]

   b. A: `projection/ProjectionAssembler.java` lines left out are `""`. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `ProjectionGrammar.g4` fills every slot with `null` and `ProjectionAssembler.java` tests the optional fields and `msgt` with `!= null`; `ProjectionReaderTest`, `ProjectionAssemblerTest` and `ProjectionGrammarTest` pass with the flipped tests.]

   c. A: `monster/MonsterAssembler.java` absent `plural:` (528 of 624 races). [<span style="color:#006400">**CLOSED**</span> 26-10-20: `MonsterGrammar.g4` starts `pluralInit` at `null`; `MonsterReaderTest` passes (87 tests).]

   d. A: `curse/CurseAssembler.java` absent `msg:` (three shipped curses would print a blank line). [<span style="color:#006400">**CLOSED**</span> 26-10-20: `CurseGrammar.g4` starts `msgInit` at `null`; `CurseReaderTest` passes (75 tests).]

8. **G: `ActivationsGrammar.g4` second `desc:` or `msg:`.** The only grammar fault that hits the shipped data: `TELE_LONG` and `RESIST_ALL` lose their first description line. Do it with item 7's activation entry. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `msg` and `desc` are plain alternatives in the `activation` rule's outer `(...)+` and a repeat is appended with no separator, as `string_append` does in C. `ActivationReaderTest` has `theShippedTeleLongAndResistAllKeepBothDescLinesAsCDoes`, `aSecondDescLineIsAppendedToTheFirstAsCDoes` and `aSecondMsgLineIsAppendedToTheFirstAsCDoes`.]
9. **G: `ArtifactLexer.g4` blank after the last token.** The untouched upstream `artifact.txt` does not load; this repository's copy was trimmed by hand. Do it here, or fold it into Stage 6's whitespace work. [<span style="color:#006400">**CLOSED**</span> 26-10-20: the `EOL_POP_MODE` fragment in `ArtifactLexer.g4` takes spaces before the newline, so the `flags:`, `values:` and `alloc:` mode rules accept a trailing blank. The upstream `artifact.txt` loads 138 records with no errors once `record-count:138` is put back, and `ArtifactReaderTest` passes (49 tests) with `aBlankAfterTheLastFlagValueOrAllocFigureLoadsAsInC`. A tab before the newline is still open under Stage 6 item 1.]

## Stage 2: crashes and silent loss on the error paths

None of these show on the shipped data. Each turns a bad modded line into an exception, a `null` that blows up later, or a quiet loss.

1. A: `monster/MonsterAssembler.java` a die that overflows `int`, and G: `Random.g4` a number above `INT_MAX`. One fix in `Random.java` or the call sites; the Stage 0 answer decides which. [<span style="color:#006400">**CLOSED**</span> 26-10-20: `numberOrMissing` in `Random.g4` returns the missing-number marker for a number above `INT_MAX` or too long for a `long`, the `@after` block tests `baseInit` as well, `RandomReader.parse` logs and returns `null`, and the dead `NumberFormatException` catch in `Random.parseStr` is gone. `RandomReaderTest` pins it with `aNumberAboveIntMaxIsRefusedInEverySlotAsCRefusesIt`; the full suite is green.]
2. A: `monster/MonsterAssembler.java` a `friends:` or `friends-base:` die that `Random.parseStr` rejects. C reads the `number` field with `parse_random` in `parser.c` before `parse_monster_friends` or `parse_monster_friends_base` runs, so a bad die is a parse error and no friend is stored. The Java records the soft error and sets `badFriend` (or `badFriendBase`), then still calls `numberDie.getDice()` with `numberDie` `null` in `friends.add(...)` (`MonsterAssembler.java:471`) and `friendsBase.add(...)` (`MonsterAssembler.java:529`). The load ends in a `NullPointerException` where the record should be skipped. Once the entry above makes an overflowing die return `null`, that is the input that reaches these lines. Fix by leaving out the `add` when `numberDie` is `null`, and do it with the entry above. [<span style="color:#006400">**CLOSED**</span> 26-10-20: both loops now skip the `add` when `numberDie` is `null`, and the `friends-base:` loop keeps a per-line `badDice` flag so one bad line does not hide the next. `MonsterReaderTest` pins it with `aFriendCountThatOverflowsIntIsASoftErrorAsInC`, `aFriendBaseCountThatOverflowsIntIsASoftErrorAsInC` and `everyBadFriendsBaseLineIsReportedNotJustTheFirst`.]
3. A: `slay/SlayAssembler.java` a code the `Slay` constructor cannot split aborts the whole load. [<span style="color:#006400">**CLOSED**</span> 26-10-21: `Slay.java` no longer splits `code`, so the constructor stores its nine arguments and cannot throw; `monsterType` and `monsterLevel` are removed from the class, `equals` and `hashCode`. `SlayAssembler.java` is unchanged. `SlayTest` has `constructorStoresAnyCodeAsGivenAsInC` and `SlayReaderTest` has `codeThatIsNotAFlagUnderscoreNumberLoadsAsInC`; the full suite is green.]
4. A: `curse/CurseAssembler.java` the 254-curse limit throws out of the reader and counts survivors. [<span style="color:#006400">**CLOSED**</span> 26-10-21: `CurseAssembler.java` counts every record before any other check and throws on the 255th with the count and line in the message, as C counts every entry and quits. The throw stays: the Tier 1 "Bad record" row is decided as stop the load. `CurseAssemblerTest.skippedRecordsStillCountTowardsTheCapAsInC` pins it and the full suite is green.]
5. A: `itemobject/ItemObjectAssembler.java` a record with no `type:` leaves a `null` base that `ObjectRegistry.addObjectKind` dereferences. [<span style="color:#006400">**CLOSED**</span> 26-10-21: C's load accepts such a record, because only `parse_object_type` sets `base` and the sval, so the kind keeps a `NULL` base, tval 0 and sval 0. `ItemObjectAssembler.java` gives it `TV_NONE` and a `null` base, and `ObjectRegistry.addObjectKind` takes sval 0 and moves no counter when the base is `null`. `ItemObjectReaderTest.aRecordWithNoTypeHasTvNoneAndANullBaseAsInC` and `ObjectRegistryTest.aKindWithNoBaseRegistersWithSvalZeroAndTouchesNoCounter` pin it, and the full suite is green. Two base-less kinds share the (TV_NONE, 0) slot in `kindsByTvalSval`, which C's scanning `lookup_kind` does not; `twoKindsWithNoBaseCollideInTheIndex` pins that as a known divergence.]
6. A: `trap/TrapAssembler.java` the bad-effect `null` list (the other half of the Stage 1 item). [<span style="color:#006400">**CLOSED**</span> 26-10-20 with Stage 1 item 6.]
7. A: `projection/ProjectionAssembler.java` and `GrammarDriver.java` a misplaced element gives no projections and no error. Any assembler that throws `ParseCancellationException` is open to the same loss, so check them when this is fixed. [<span style="color:#006400">**CLOSED**</span> 26-10-21: `ProjectionAssembler.java` throws `DataFileParseException` for an unknown, empty or misplaced code in the first 25 records, an empty code anywhere, and an element count other than 25, as C quits in `parse_projection_code`, `parser_parse` and `finish_parse_projection`. `GrammarDriver.java` does not catch it, so it leaves `ProjectionReader.parseWithResults` and `WorldDataLoader.loadProjections` and start-up stops before the registry is touched. From the 26th record on, C does not read the code, so a code that does not resolve is stored as `PROJ_NONE` with no error. The check of the other assemblers found none that throws `ParseCancellationException`: the only throw is `ParseErrors.throwIfAny`, the hard-error channel that `GrammarDriver.java` catches and answers with the parser's messages. `ProjectionAssemblerTest` and `ProjectionReaderTest` pass (32 tests each) and the full suite is green.]
8. A: `dungeonprofile/DungeonProfileAssembler.java` and `RoomAssembler.java` unknown names load with a `null` builder; a bad tunnel or streamer keeps the profile. [<span style="color:#006400">**CLOSED**</span> 26-10-21: C stops the load on an unknown name, as `parse_profile_name` returns `PARSE_ERROR_NO_BUILDER_FOUND` and `parse_profile_room` returns `PARSE_ERROR_NO_ROOM_FOUND`, and `print_error` in `datafile.c` quits. `DungeonProfileAssembler.java` and `RoomAssembler.java` now throw `DataFileParseException` naming the name, the line and the file, and every bad-field and wrong-count path in `TunnelAssembler.java` and `StreamerAssembler.java` throws the same, so a profile is never kept with a `null` `tun` or `str`. `CaveProfile.java` now holds a `BuilderType`. `DungeonProfileReaderTest` has `unknownRoomNameStopsTheLoadAsInC`, `aNearMissRoomNameStopsTheLoadAfterAGoodRoom`, `anUnknownProfileNameStopsTheLoadAsInC`, `anOverflowingTunnelFieldStopsTheLoadAsInC` and `anOverflowingStreamerFieldStopsTheLoadAsInC`, and `DungeonLoaderTest` has `unknownProfileNameStopsTheLoad`; the full suite is green. The soft `params:`, `min-level:`, `alloc:` and `room:` number paths are not part of this item and stay with the Stop-the-load list.]
9. A: `playerclass/ClassSpellBookAssembler.java` and `ClassMagicAssembler.java` a class whose books were all dropped loads as a non-caster, and a book keeps more spells than it declares.
   **Reminder (2026-10-22):** the too-many-spells error in `ClassSpellBookAssembler.java` cites the `book:` line, which is the decided
   wording (C stops on the extra `spell:` line, but the book's line is kept). Add the name of the first extra spell to the message as
   well, so the line and the spell can both be found. That name comes from `record.spells()` at index `noOfSpells`, not from the assembled
   `spells` list, and `moreSpellsThanDeclaredStopTheLoadAsInC` and `theSpellCapIsPerBookAsInC` in `PlayerClassReaderTest.java` then
   assert it.

## Stop-the-load sites: every `errors.add` in the assemblers

**Date:** 2026-10-21

**What this is:** the work list for the Tier 1 "Bad record" answer (stop the load). It names every `errors.add(...)` call in the 51 assembler
files that have one, 503 calls in all, so each can be turned into the one stop-the-load path. Line numbers are as the files stood on
the date above and move as soon as a file is edited, so re-run the search before working on a file. `HintAssembler.java` and
`WorldAssembler.java` have no `errors.add` call and are not listed. The throws that already stop the load (in `CurseAssembler.java`,
`TerrainFeatureAssembler.java`, `QuestAssembler.java` and `ProjectionAssembler.java`) are not in this list.

**Order (2026-10-21):** this is done after Stage 2 and before Stage 3, so the helpers in Stage 3 are written against the stop-the-load path.

**First item: the assemblers are passed the filename (2026-10-21).** The stop-the-load message names the file and the line, as `print_error`
in `datafile.c` does. An assembler knows the line from its parse record but not the file, so the filename is passed into each assembler and
the assembler builds its own message with both. This changes the `Assembler.assemble` signature in `Assembler.java`, so it is done before
any of the sites below are converted, and every assembler and every test that calls `assemble` changes with it. The stop itself is the
unchecked `DataFileParseException` in `backend/io/bespokeexceptions/`, which `GrammarDriver.run` and the loaders do not catch.

Paths are under `src/main/java/uk/co/jackoftradesltd/`. The figure in brackets is the number of calls in that file.

- `backend/parser/activation/ActivationAssembler.java` (2): ~~72~~, ~~82~~
- `backend/parser/artifact/ArtifactAssembler.java` (21): ~~86, 97, 107, 117, 127, 138, 148, 161, 183, 187, 205, 218, 229, 243, 255, 265, 275, 285, 294, 304, 317~~
- `backend/parser/bloweffect/BlowEffectAssembler.java` (8): ~~86, 97, 110, 119, 128, 144, 160, 168~~
- `backend/parser/blowmethod/BlowMethodAssembler.java` (1): ~~90~~
- `backend/parser/body/BodyAssembler.java` (1): ~~74~~
- `backend/parser/brand/BrandAssembler.java` (5): ~~86, 97, 108, 116, 124~~
- `backend/parser/chesttrap/ChestTrapAssembler.java` (9): ~~78, 88, 95, 102, 113, 120, 131, 137, 155~~
- `backend/parser/curse/CurseAssembler.java` (17): ~~190, 205, 220, 225, 250, 260, 281, 285, 295, 299, 311, 321, 331, 344, 357, 366, 408~~
- `backend/parser/dungeonprofile/DungeonProfileAssembler.java` (3): ~~80, 106, 116~~
- `backend/parser/dungeonprofile/RoomAssembler.java` (6): ~~66, 74, 82, 90, 99, 107~~
- `backend/parser/dungeonprofile/StreamerAssembler.java` (7): ~~52, 66, 73, 80, 87, 94, 101~~
- `backend/parser/dungeonprofile/TunnelAssembler.java` (6): ~~57, 73, 81, 89, 97, 105~~
- `backend/parser/egoitem/EgoItemAssembler.java` (30): ~~83, 100, 113, 128, 146, 150, 157, 166, 183, 187, 198, 210, 223, 234, 246, 256, 266, 276, 286, 295, 303, 313, 323, 332, 341, 351, 361, 371, 380, 389~~
- `backend/parser/flavour/FlavourAssembler.java` (4): ~~67, 73, 85, 91~~
- `backend/parser/flavour/FlavourKindAssembler.java` (3): ~~74, 80, 94~~
- `backend/parser/gameconstants/GameConstantsAssembler.java` (39): ~~58, 93, 115, 131, 138, 158, 192, 214, 231, 238, 258, 286, 308, 325, 348, 388, 411, 427, 453, 490, 509, 531, 550, 578, 597, 619, 638, 664, 684, 724, 743, 778, 798, 824, 844, 882, 902, 914, 935~~
- `backend/parser/grammars/EffectAssembler.java` (22): ~~112, 120, 147, 157, 167, 177, 238, 245, 255, 296, 306, 316, 326, 341, 349, 358, 368, 378, 388, 398, 408, 416~~
- `backend/parser/history/HistoryAssembler.java` (5): ~~83, 91, 99, 113, 133~~
- `backend/parser/itemobject/ItemObjectAssembler.java` (32): ~~96, 102, 112, 119, 130, 140, 150, 162, 171, 180, 190, 199, 209, 217, 224, 231, 240, 251, 256, 262, 288, 293, 315, 319, 329, 335, 347, 359, 371, 382, 400, 418~~
- `backend/parser/monsterbase/MonsterBaseAssembler.java` (4): ~~70, 77, 86, 92~~
- `backend/parser/monster/MonsterAssembler.java` (41): ~~89, 103, 113, 123, 134, 144, 154, 164, 174, 185, 194, 205, 213, 223, 233, 244, 256, 269, 279, 286, 307, 317, 328, 338, 364, 386, 397, 410, 436, 442, 450, 463, 495, 501, 507, 516, 530, 538, 556, 562, 585~~
- `backend/parser/monsterspell/MonsterSpellAssembler.java` (3): ~~77, 91, 100~~
- `backend/parser/monsterspell/MonsterSpellLevelAssembler.java` (4): ~~72, 82, 91, 100~~
- `backend/parser/names/NamesAssembler.java` (1): ~~56~~
- `backend/parser/objectbase/ObjectBaseAssembler.java` (6): ~~66, 74, 88, 97, 108, 116~~
- `backend/parser/objectproperty/ObjectPropertyAssembler.java` (12): ~~79, 84, 93, 99, 105, 133, 139, 148, 158, 173, 180, 206~~
- `backend/parser/pain/PainAssembler.java` (2): ~~64, 69~~
- `backend/parser/pit/PitAssembler.java` (12): ~~92, 102, 112, 127, 140, 152, 159, 173, 187, 200, 215, 231~~
- `backend/parser/playerclass/ClassEquipAssembler.java` (11): ~~92, 99, 110, 115, 120, 129, 134, 139, 145, 191, 197~~
- `backend/parser/playerclass/ClassMagicAssembler.java` (5): ~~61, 69, 77, 84, 89~~
- `backend/parser/playerclass/ClassSpellAssembler.java` (4): ~~62, 72, 82, 92~~
- `backend/parser/playerclass/ClassSpellBookAssembler.java` (9): ~~126, 152, 163, 172, 184, 194, 204, 214, 231~~
- `backend/parser/playerclass/PlayerClassAssembler.java` (14): ~~82, 86, 100, 104, 118, 122, 132, 142, 152, 163, 173, 187, 201, 209~~
- `backend/parser/playerproperty/PlayerPropertyAssembler.java` (7): ~~137, 144, 151, 158, 172, 177, 191~~
- `backend/parser/playerrace/PlayerRaceAssembler.java` (19): ~~72, 82, 92, 102, 112, 122, 132, 142, 152, 167, 173, 187, 192, 204, 217, 228, 233, 247, 251~~
- `backend/parser/playertimed/PlayerTimedAssembler.java` (13): ~~84, 107, 114, 119, 130, 145, 183, 189, 214, 224, 234, 245, 255~~
- `backend/parser/projection/ProjectionAssembler.java` (27): ~~149, 152, 156, 159, 171, 174, 184, 187, 191, 194, 203, 206, 210, 213, 225, 228, 232, 235, 247, 250, 254, 257, 269, 294, 326, 407, 415~~
- `backend/parser/quest/QuestAssembler.java` (2): ~~156, 177~~
- `backend/parser/realm/RealmAssembler.java` (1): ~~80~~
- `backend/parser/roomprofile/RoomProfileAssembler.java` (11): ~~69, 79, 89, 97, 107, 113, 123, 141, 157, 170, 179~~
- `backend/parser/shape/ShapeAssembler.java` (17): ~~84, 95, 106, 119, 131, 143, 155, 167, 179, 191, 203, 216, 230, 249, 253, 264, 268~~
- `backend/parser/slay/SlayAssembler.java` (7): ~~81, 92, 99, 103, 113, 121, 129~~
- `backend/parser/summon/SummonAssembler.java` (4): ~~70, 80, 96, 122~~
- `backend/parser/terrainfeature/TerrainFeatureAssembler.java` (9): ~~155, 183, 193, 198, 208, 213, 233, 248, 279~~
- `backend/parser/trap/TrapAssembler.java` (7): ~~128, 141, 152, 163, 173, 184, 196~~
- `backend/parser/vault/VaultAssembler.java` (10): ~~97, 105, 114, 123, 129, 148, 158, 166, 174, 193~~
- `backend/parser/visuals/VisualsCycleAssembler.java` (2): ~~82, 90~~
- `backend/parser/visuals/VisualsFlickerAssembler.java` (2): ~~74, 85~~
- `frontend/ui/entry/assembler/UIEntryAssembler.java` (10): ~~223, 231, 250, 262, 278, 292, 313, 344, 372, 384~~
~~- `frontend/ui/entrybase/assembler/UIEntryBaseAssembler.java` (3): 95, 105, 128~~
- `frontend/ui/entryrenderer/assembler/UIEntryRendererAssembler.java` (3): ~~63, 91, 104~~

## Stage 2.5: remove soft error functionality ##

## Stage 3: shared assembler helpers, fixed once and applied everywhere

Each group is one helper (or one policy) and then one pass over its users.

### Message lookup (`message_lookup_by_name`) [<span style="color:#006400">**CLOSED**</span> 26-11-05]

- A: `blowmethod/BlowMethodAssembler.java` `msg:`.
- A: `summon/SummonAssembler.java` `msgt:`.
- A: `monsterspell/MonsterSpellAssembler.java` `msgt:`.
- A: `projection/ProjectionAssembler.java` `msgt:`, which also has the numeric path one constant late.
- Case-insensitive, with a numeric form, and no name `NONE`. Do the lookup helper first, then the four call sites.

### `NONE` and `MAX` sentinels accepted by `valueOf` [<span style="color:#006400">**CLOSED**</span> 26-11-05: `NONE` is refused everywhere C refuses it, and `MAX` follows C's tables, which carry it for object flags and kind flags only.]

- A: `slay/SlayAssembler.java` and `brand/BrandAssembler.java` race flags. [<span style="color:#006400">**CLOSED**</span> 26-11-05]
- A: `egoitem/EgoItemAssembler.java` and `shape/ShapeAssembler.java` elements and modifiers. [<span style="color:#006400">**CLOSED**</span> 26-11-05: both sentinels refused for elements and modifiers; `OF_MAX` and `KF_MAX` load for flags, as C's tables carry them.]
- A: `objectproperty/ObjectPropertyAssembler.java` `code:`, which also has the missing-`code:` case. [<span style="color:#006400">**CLOSED**</span> 26-11-05]
- A: `body/BodyAssembler.java` slot types. [<span style="color:#006400">**CLOSED**</span> 26-11-05]
- A: `playerrace/PlayerRaceAssembler.java` flags and resists, plus its race index and missing-history parts. [<span style="color:#006400">**CLOSED**</span> 26-11-05: `OF_MAX` loads.]
- A: `playerproperty/PlayerPropertyAssembler.java` `code:MAX`, plus its element-record `code:` and `value:` parts. [<span style="color:#006400">**CLOSED**</span> 26-11-05: an object `code:MAX` loads, as C's `code_index_in_array` finds it.]
- A: `realm/RealmAssembler.java` `stat:`, which is also case-sensitive and drops a realm with no `stat:`. [<span style="color:#006400">**CLOSED**</span> 26-11-05]
- A: `trap/TrapAssembler.java` `flags:MAX` and `save:MAX` are a smaller part of the trap entry. [<span style="color:#006400">**CLOSED**</span> 26-11-05: `flags:MAX` is refused and `save:MAX` loads, as C's `trap_flags` has no `MAX` and `list_obj_flag_names` does.]
- One lookup helper that starts past the sentinel would serve all of them. [<span style="color:#006400">**CLOSED**</span> 26-11-05: not written; each assembler carries its own inline check.]

### `grab_element_flag` (`IGNORE_` and `HATES_`)

- A: `itemobject/ItemObjectAssembler.java` (knows `IGNORE_`, not `HATES_`). [<span style="color:#006400">**CLOSED**</span> 26-11-07: calls `ParseUtils.grabElementFlag` on every `flags:` token, so `HATES_` and `IGNORE_` both load; `ItemObjectReaderTest` and `ItemObjectAssemblerObjectPrepTest` pass.]
- A: `objectbase/ObjectBaseAssembler.java` (knows `HATES_`, not `IGNORE_`). [<span style="color:#006400">**CLOSED**</span> 26-10-28: `IGNORE_` is read with C's prefix rule and sets `EL_INFO_IGNORE`; `ObjectBaseAssemblerTest` and `ObjectBaseReaderTest` pass.]
- A: `egoitem/EgoItemAssembler.java` and `artifact/ArtifactAssembler.java`. [<span style="color:#006400">**CLOSED**</span> 26-11-07: both call `ParseUtils.grabElementFlag`, the artifact one after `ObjectFlag.valueOf` fails, as C tries `grab_flag` first; `EgoItemReaderTest` and `ArtifactReaderTest` pass.]
- Write one equivalent of C's function and call it from all four. [<span style="color:#006400">**CLOSED**</span> 26-11-07: `ParseUtils.grabElementFlag` is the one equivalent, called from all four and from `CurseAssembler`; `ParseUtilsGrabElementFlagTest` pins it.]

### Booleans read from text (`!= 0`, not `equals("1")`) [<span style="color:#006400">**CLOSED**</span> 26-11-07]

- A: `summon/SummonAssembler.java` `uniques:`. [<span style="color:#006400">**CLOSED**</span> 26-11-07: read by `ParseUtils.isTrue`; `SummonReaderTest` and `SummonAssemblerTest` pass.]
- A: `activation/ActivationAssembler.java` `aim:`. [<span style="color:#006400">**CLOSED**</span> 26-10-22: any non-zero `aim:` is true, read with C's base 0 rules; `ActivationAssemblerTest.anyNonZeroAimIsTrueAsInC` pins it.]
- A: `objectproperty/ObjectPropertyAssembler.java` `bindui:` aux, and A: `playerproperty/PlayerPropertyAssembler.java` aux. [<span style="color:#006400">**CLOSED**</span> 26-11-07: both read the aux through `ParseUtils.isTrue`; `ObjectPropertyReaderTest` and `PlayerPropertyReaderTest` pass.]
- A: `chesttrap/ChestTrapAssembler.java` `destroy:` and `magic:`. [<span style="color:#006400">**CLOSED**</span> 26-11-07: both read through `ParseUtils.isTrue`; `ChestTrapAssemblerTest` passes.]
- A: `playertimed/PlayerTimedAssembler.java` `exact`, which sits in its larger entry. [<span style="color:#006400">**CLOSED**</span> 26-11-07 for the boolean part: the `flag-synonym:` exactness is read by `ParseUtils.isTrue`; the `PlayerTimed` tests pass.]
- A: `dungeonprofile/RoomAssembler.java` `pit:01`, in the dungeon-profile entry. [<span style="color:#006400">**CLOSED**</span> 26-11-07: the parsed figure is compared with 1, so `01` is a pit and `2` is not; `DungeonProfileReaderTest.aPitWrittenWithALeadingZeroIsAPitAsInC` and `RoomAssemblerTest` pass.]
- G: `BlowMethodLexer.g4` `INTEGER` (`!text.equals("0")`, the mirror image). [<span style="color:#006400">**CLOSED**</span> 26-11-07: the lexer keeps the text and `BlowMethodAssembler.java` reads `cut:`, `stun:`, `miss:` and `phys:` through `ParseUtils.isTrue`; `BlowMethodReaderTest` passes.]
- One helper that does what `strtol(..., 0) != 0` does. [<span style="color:#006400">**CLOSED**</span> 26-11-07: `ParseUtils.isTrue`, pinned by `ParseUtilsIsTrueTest`.]

### Numbers: range, sign and base

Checked 26-11-07 by reading each assembler and the C it ports. Every bullet below was found already met in the code. The tests were not run in that pass, so each CLOSED tag rests on the code reading only; run the named reader tests before ticking `docs/ROADMAP.md`.

- A: `slay`, `brand`: no range check on the three numbers (assembler half; the lexer half is in Stage 6). [<span style="color:#006400">**CLOSED**</span> 26-11-07 for the assembler half, tests not run.]
  - What C does: `multiplier`, `o-multiplier` and `power` are `uint` (`obj-init.c`). `parser_parse` refuses a token starting with `-`, `-0` included, and reads the digits with `strtoul` in base 0.
  - Need: refuse a leading `-` on the raw text, and read `010` as 8.
  - Java now: `slay/SlayAssembler.java` and `brand/BrandAssembler.java` call `ParseUtils.minusFound` and then `ParseUtils.parseIntString`.
  - Still open: whether `SlayLexer.g4` and `BrandLexer.g4` let a `-` reach the assembler. That is the Stage 6 `INTEGER` rule and was not checked.
- A: `monster/MonsterAssembler.java` `drop:` bounds and a negative chance. [<span style="color:#006400">**CLOSED**</span> 26-11-07, tests not run.]
  - What C does: `parse_monster_drop` (`mon-init.c`) returns `PARSE_ERROR_INVALID_ITEM_NUMBER` when `min` or `max` is above 99. Chance, min and max are `uint`, so a leading `-` is refused. Chance has no upper bound.
  - Need: a minus check on all three, and 0 to 99 on min and max only.
  - Java now: `monster/MonsterAssembler.java` has the minus checks and the 0 to 99 tests, and leaves chance unbounded.
- A: `egoitem/EgoItemAssembler.java` and `artifact/ArtifactAssembler.java` `alloc:` figures against 0 to 255. [<span style="color:#006400">**CLOSED**</span> 26-11-07, tests not run. The artifact half was already closed 26-10-23.]
  - What C does: `parse_ego_alloc` and `parse_artifact_alloc` (`obj-init.c`) read the range with `grab_int_range` (`strtol`, base 10). Either end outside 0 to 255 is `PARSE_ERROR_OUT_OF_BOUNDS`. The commonness is an `int` with no check.
  - Need: a base 10 read, so `010` is 10, then 0 to 255 on both ends and nothing on the commonness.
  - Java now: both assemblers read the range through the fixed-base `ParseUtils.parseIntString` and test 0 to 255 on each end.
- A: `projection/ProjectionAssembler.java` values above 2147483647. [<span style="color:#006400">**CLOSED**</span> 26-11-07 as a clamp, tests not run.]
  - What C does: `numerator`, `divisor` and `damage-cap` are `uint` stored in an `int` (`obj-init.c`). Nothing is refused above `INT_MAX`; the value wraps.
  - Need: stop dropping the record, and with it the whole file, for a large figure.
  - Java now: `projection/ProjectionAssembler.java` refuses a leading `-`, then `parseIntString` clamps to `Integer.MAX_VALUE` and the load carries on.
  - Deviation: C wraps and Java clamps. `ParseUtils` records it as an accepted deviation.
- A: `names/NamesAssembler.java` and `history/HistoryAssembler.java` base and sign. [<span style="color:#006400">**CLOSED**</span> 26-11-07, tests not run.]
  - What C does: in `init.c`, `section` and `chart` are `uint` and `next` and `roll` are `int`, all read in base 0. A section of 3 or more is `PARSE_ERROR_OUT_OF_BOUNDS`.
  - Need: read `010` as 8, refuse a leading `-` on the unsigned fields, and range-check the section.
  - Java now: `names/NamesAssembler.java` refuses the minus, reads base 0 and refuses 0 and anything above 2. `history/HistoryAssembler.java` refuses the minus on the chart and reads all three numbers in base 0.
  - Deviation: section 0 is refused where C accepts it (class block of `NamesAssembler.java`).
- A: `flavour/FlavourAssembler.java` negative or hex index. [<span style="color:#006400">**CLOSED**</span> 26-11-07, tests not run.]
  - What C does: `parse_flavor_flavor` (`init.c`) reads `index` as a `uint`: a leading `-` is refused and the digits go through `strtoul` in base 0, so `0x10` is 16.
  - Need: refuse the minus, accept hex and octal.
  - Java now: `flavour/FlavourAssembler.java` (`storeFlavour`) does both. The colour fallback to white is the separate item below.
- A: `GameConstantsAssembler` negative values and overflow. [<span style="color:#006400">**CLOSED**</span> 26-11-07, tests not run.]
  - What C does: `level-max`, `mon-gen`, `mon-play`, `dun-gen`, `world`, `carry-cap`, `store`, `obj-make` and `player` (`init.c`) return `PARSE_ERROR_INVALID_VALUE` for a value below 0. The four critical handlers have no such check, because `melee-critical:chance-offset:-60` relies on that.
  - Need: a negative test in exactly those nine handlers and not in the critical ones.
  - Java now: `gameconstants/GameConstantsAssembler.java` has a `value < 0` throw in each of the nine and none in the critical handlers. `coerceInt` clamps an overflowing value where C truncates it.
- A: `playertimed/PlayerTimedAssembler.java` `lower-bound:`. [<span style="color:#006400">**CLOSED**</span> 26-11-07, tests not run.]
  - What C does: `parse_player_timed_effect_lower_bound` (`player-timed.c`) reads an `int` and refuses a value below 0 or above 32767, because the duration is an `int16_t`.
  - Need: both ends of that range.
  - Java now: `playertimed/PlayerTimedAssembler.java` throws outside 0 to `Short.MAX_VALUE`.
- A: `roomprofile/RoomProfileAssembler.java` `type:`, `rating:` and `doors:` ranges. [<span style="color:#006400">**CLOSED**</span> 26-11-07, tests not run.]
  - What C does: in `generate.c`, `type`, `rows`, `columns` and `doors` are `uint` and `rating` is an `int`. All are stored in `uint8_t` fields of `struct room_template`, so a value over 255 wraps, and rows and columns are compared with the template maximum after the wrap.
  - Need: refuse a minus on the unsigned fields, wrap every field to a byte, and compare the wrapped rows and columns with the maximum.
  - Java now: `roomprofile/RoomProfileAssembler.java` refuses the minus on `type`, `rows`, `columns` and `doors`, wraps with `floorMod` (rows and columns before the bound test), and wraps `rating` without a minus check, as C does.
  - Not covered: the zero-size and ragged-map items in that file's entry in `docs/Assembler_Issues.md`.

### Colours that C reads as white

- A: `monsterspell/MonsterSpellLevelAssembler.java` lore colours (drops the level). [<span style="color:#006400">**CLOSED**</span> 26-10-27: an unknown base, resist or immune colour reads as `COLOUR_WHITE`; `MonsterSpellLevelAssemblerTest` and `MonsterSpellReaderTest` pass.]
- A: `objectbase/ObjectBaseAssembler.java` (drops the record). [<span style="color:#006400">**CLOSED**</span> 26-10-28: an unknown colour reads as `COLOUR_WHITE`; `ObjectBaseAssemblerTest` and `ObjectBaseReaderTest` pass.]
- A: `flavour/FlavourAssembler.java` colour names, in the flavour entry. [<span style="color:#006400">**CLOSED**</span> 26-11-08: closed at Rowan's request; the last recorded run of `FlavourAssemblerTest` was green.]
  - What C does: `parse_flavor_flavor` (`init.c`) sends a one-character `attr` to `color_char_to_attr` and anything longer to `color_text_to_attr` (`z-color.c`). Both answer white for an unknown input, a blank is `COLOUR_DARK`, and names match case-insensitively. `PARSE_ERROR_INVALID_COLOR` needs a negative `char`, so it never fires on this data.
  - Need: an unknown letter or name keeps the flavour and paints it white, a blank is dark, and `Shade` is white because C has no `color_table` row for it.
  - Java now: `storeFlavour` in `flavour/FlavourAssembler.java` goes through `ColourEnum.fromCode` and sets `COLOUR_WHITE` for a null result or `COLOUR_SHADE`. `FlavourAssemblerTest` has the cases for a letter, a name, `Shade`, a blank and the case rules, and ran green this turn.
  - To close: nothing is missing in the assembler; it can be ticked on those tests.
- G: the colour value in `PitLexer.g4`, `MonsterLexer.g4`, `VisualsLexer.g4` and `imports/AngbandDisplayCharacter.g4`, which is Stage 7f. [<span style="color:#006400">**CLOSED**</span> 26-11-08: closed at Rowan's request; the grammars and tests were not re-read for this tag.]
  - What C does: Pit and Monster `color:` (`mon-init.c`) and the display-character lines (`ui-prefs.c`) take a name when the text is longer than one character and a letter otherwise, and both fall back to white. The three Visuals handlers (`ui-visuals.c`) read only `color_code[0]`, so `Red` is the letter `R`, and they refuse an empty value only.
  - Need: the lexer takes any value up to the colon or the end of the line, and the assembler does the mapping, so `color:q` and `color:Mauve` load as white instead of failing in the lexer.
  - Java now: all four grammars take colours from `imports/Colours.g4`, where `COLOUR_CODE` and `COLOUR_NAME` are closed lists. Read from the grammar text, not run: `q` and `Mauve` are lexer errors in each, and a blank cannot be written at all.
  - To close: a rule per grammar that accepts any non-empty value, `.g4` regenerated, and one test per grammar showing an unknown letter and an unknown name reach the assembler as white (Visuals: the first character only). Whether each assembler already maps the text was not read this turn.
- The Stage 0 strict-or-lenient answer decides all of the above. [<span style="color:#006400">**CLOSED**</span> 26-11-08: closed at Rowan's request, with the three items above it.]

### Absent directives that C zeroes

- A: `monsterspell/MonsterSpellAssembler.java` absent `hit:`. [<span style="color:#006400">**CLOSED**</span> 26-11-08: `hit` starts at 0 in `MonsterSpellAssembler.assemble` and is parsed only when `record.hit()` is non-empty, as C's zeroed `hit` stays 0 when `parse_mon_spell_hit` never runs; `MonsterSpellAssemblerTest.anAbsentHitIsZeroAsCsZeroedFieldIs` and `MonsterSpellReaderTest.anAbsentHitLoadsWithZeroAsCDoes` pass.]
- A: `monsterspell/MonsterSpellLevelAssembler.java` no implicit level 0. Settle this before the spell-walking code is ported. [<span style="color:#006400">**CLOSED**</span> 26-10-27: the blank level 0 is added for a leading `power-cutoff:` and for a spell with no level text, and a power-less record merges into the last level; `MonsterSpellLevelAssemblerTest` and `MonsterSpellReaderTest` pass.]
- A: `slay/SlayAssembler.java` missing numbers. [<span style="color:#006400">**CLOSED**</span> 26-11-08: empty `multiplier`, `o-multiplier` and `power` text goes through `ParseUtils.parseIntString`, which gives 0, as the zeroed C slay does; `SlayReaderTest.missingPowerLoadsAsZeroAsInC` and `missingMultiplierLoadsAsZeroAsInC` pass.]
- A: `monster/MonsterAssembler.java` no `color:`. [<span style="color:#006400">**CLOSED**</span> 26-11-08: an empty `record.colour()` gives `ColourEnum.COLOUR_DARK`, C's zeroed `d_attr`; `MonsterReaderTest.aMonsterWithNoColourLineGetsDarkAsCLeavesTheAttrAtZero` and `MonsterAssemblerColourTest` pass.]
- A: `artifact/ArtifactAssembler.java` no `base-object:`. [<span style="color:#006400">**CLOSED**</span> 26-11-08 as an accepted deviation: C loads the record as tval 0, `ArtifactAssembler.assemble` throws "no assigned bases"; registered in `docs/Accepted_Deviations.md` ("A record with no `base-object:` line is refused, where C loads it as tval 0").]
- A: `objectbase/ObjectBaseAssembler.java` and G: `ObjectBaseGrammar.g4` one missing default. Take this as a pair. [<span style="color:#006400">**CLOSED**</span> 26-10-28 for the assembler half: `""` reads as 0 through `ParseUtils.parseIntString`, so the record loads. The grammar half is also <span style="color:#006400">**CLOSED**</span> 26-11-08: the `default_value` group in the `file` rule of `ObjectBaseGrammar.g4` closes with `)*`, the parser is regenerated, and `ObjectBaseReaderTest.aFileWithNoDefaultLineLoadsWithZeroDefaultsAsInC` and `aFileWithOnlyOneDefaultLineLeavesTheOtherAtZeroAsInC` pass with the rest of `ObjectBaseReaderTest` and `ObjectBaseAssemblerTest`.]
  - What C does: `init_parse_object_base` (`obj-init.c`) `mem_zalloc`s the parse data, so `defaults.break_perc` and `defaults.max_stack` are 0, and `parse_object_base_defaults` only runs for a `default:` line the file has. A file with no `default:` line, or with only one of the two, loads and every base takes 0 for what was not given.
  - Java now (read 26-11-08): `ObjectBaseGrammar.g4` rule `file`, line 164 to 169, is `(default_value { ... })+`, so a file with no `default:` line is a parse error. Only one of the two is already fine: `defaultMaxStack` and `defaultBreakChance` (lines 161 and 162) start as `""`, and `ObjectBaseAssembler` reads `""` as 0.
  - To close: change the `+` closing the `default_value` group at `ObjectBaseGrammar.g4` line 169 to `*`, regenerate, and add a test in `ObjectBaseReaderTest` for a file with no `default:` line (its `DEFAULTS` constant, line 78, is prefixed to every fixture, so none covers it).

## Stage 4: list order (C's chains are reversed)

Only the observable cases were put in Stage 1. The Stage 0 answer "always echo C's ordering" applies to every list below as well, observable or not, so none of them is left as an accepted deviation. What is still open is how each list carries C's order: building it in that order, or a collection type that keeps it. That is decided per list when the file is open, and it supersedes the "sort at the point of use" approach from `brands-slays-order-at-point-of-use`.

- A: `monsterspell/MonsterSpellAssembler.java` and `monsterbase/MonsterBaseAssembler.java`. Matters in play only for a name used twice, where the lookup should take the last match. The list itself is in C's order regardless. [<span style="color:#006400">**CLOSED**</span> 26-11-08: `MonsterSpellAssembler.java` and `MonsterBaseAssembler.java` add each record with `addFirst`, as C's head-pushed chains do. `MonsterRegistry.lookupMonsterBase` takes the first match in that list, so a repeated base name answers the later record as `lookup_monster_base` does. `MonsterRegistry` has no spell lookup yet (the list is stored and never read), so the first-match search for `monster_spell_by_index` is for whoever ports it. `MonsterSpellAssemblerTest` (16), `MonsterSpellReaderTest` (39) and `MonsterBaseReaderTest` (35) pass, including the last-record-first and repeated-name tests.]
- A: `shape/ShapeAssembler.java` shapes and blows. The blow order shows only under a fixed roll. [<span style="color:#006400">**CLOSED**</span> 26-11-08: `ShapeAssembler.java` pushes each `blow:` line and each shape on the head of its list with `addFirst`, as `parse_shape_blow` and `parse_shape_name` do. Each shape's `sidx` is its place in the file, set with `PlayerShape.setShapeIndex` before the push, and `PlayerRegistry.lookupPlayerShape(int)` matches on it, as `player_shape_by_idx` does. `lookupPlayerShape(String)` takes the first match, which is the later of two same-named shapes, as in C. `ShapeReaderTest` has the reverse-order tests for the shape list, duplicate names and the nine shipped shapes, each with its `sidx`; `EffectAssemblerSubTypeTest` and `PlayerRegistryLookupsTest` pass with the numbered fixture.]
- A: `summon/SummonAssembler.java` `base:` and `pit/PitAssembler.java` bases, colours and bans. Not observable, but they follow the same rule. [<span style="color:#006400">**CLOSED**</span> 26-11-08: `SummonAssembler.java` puts each base on the head of its list with `addFirst`, as `parse_summon_base` does, and `PitAssembler.java` does the same for the colours, bases and `mon-ban:` races, as `parse_pit_color`, `parse_pit_mon_base` and `parse_pit_mon_ban` do. The summon and pit record lists stay in file order, because `finish_parse_summon` and `finish_parse_pit` fill their arrays from the end of a reversed chain. `SummonAssemblerTest` (64), `SummonReaderTest` (33), `PitAssemblerTest` (87) and `PitReaderTest` (33) pass with order tests for two and three summon bases, pit colours, bases and bans, and the upstream tables compared reversed; the full suite is green (14078 tests).]
- Brands and slays follow the rule too, wherever a loop depends on their order (`copy_brands` and `copy_slays`). That reverses the 2026-10-17 decision to leave them as a `HashSet`. [<span style="color:#006400">**CLOSED**</span> 26-11-08: every brand and slay set in `ItemObject.java`, `EgoItem.java`, `ObjectKind.java` and `Artifact.java` is a `LinkedHashSet`, so copies, clears and rebuilds keep the order the assemblers load (C's registry order). The `copyBrands` and `copySlays` Javadoc in `ObjectUtils.java` says so. `BrandSlayOrderThroughCopiesTest`, `ObjectUtilsCopyBrandsTest`, `ObjectUtilsCopySlaysTest` and `ObjectUtilsObjectPrepTest` pin it. Two divergences from C are pinned, not fixed: the merge appends the kind's unmatched brands and slays after the item's instead of re-sorting by registry position, and on an exact multiplier tie the item's own survives where C keeps the higher index. Neither shows on the shipped data (`BrandSlayNoTieDataTest`), and `objectPrep` always starts from an empty item.]

## Stage 5: assembler leftovers, in any order

Local edits with no effect on the shipped data. Group by file when the file is open for something else.

- Soft error where C is silent: A: `summon` unresolved `fallback:`, `pit` unresolved `mon-ban:`, `monster` unknown `color-cycle:` group, `projection` unknown `code:` after the 25 elements. [<span style="color:#006400">**CLOSED**</span> 26-11-08: none of the four raises an error now. `summon` keeps the record with a `null` fallback (accepted deviation, C fails in `strcmp`), `pit` stores a `null` race for an unmatched `mon-ban:`, `monster` leaves the cycler `null`, and `projection` stores `PROJ_NONE` after the 25th record. `PitAssemblerTest`, `PitReaderTest`, `MonsterReaderTest`, `ProjectionAssemblerTest`, `ProjectionReaderTest`, `SummonAssemblerTest` and `SummonReaderTest` all passed this date.]
- Record dropped where C keeps it: A: `egoitem` and `artifact` unknown `act:`, `objectproperty` `subtype:` on a non-flag. (`monster` unknown spell name in `message-*:` moved out: C rejects it too, and the Java now does. [<span style="color:#006400">**CLOSED**</span> 26-10-20])
- Stricter or looser than C by a small margin: A: `monster` `friends-base:` chance above 100, `monster` group role case, `monster` directive-order effects, `artifact` `graphics:` on a non-`INSTA_ART` kind, `objectproperty` unknown `type-mult:` tval keeps the record. [<span style="color:#006400">**CLOSED**</span> 26-11-08: all five are met in the code. `MonsterAssembler.java` keeps a `friends-base:` chance above 100 and compares the role case-sensitively; the directive-order effects are an accepted deviation in `docs/Accepted_Deviations.md`, pinned by three tests including `aFlagsOffLineBeforeAFlagsLineNamingTheSameFlagLeavesItOffHereWhereCLeavesItOn`; `ArtifactAssembler.java` stops the load on `graphics:` for a kind without `KF_INSTA_ART`; `ObjectPropertyAssembler.java` stops the load on an unknown `type-mult:` tval. `MonsterReaderTest`, `ObjectPropertyReaderTest` and `ArtifactReaderTest` passed this date.]
- Glyphs outside the Basic Multilingual Plane: A: `monsterbase`, `monster`. [<span style="color:#006400">**CLOSED**</span> 26-11-08: both assemblers count code points and keep a glyph above U+FFFF whole in the wide half of the display character, and a monster with no `glyph:` line inherits both halves of its base's glyph. `MonsterBaseReaderTest` (35) and `MonsterReaderTest` (115) pass, the latter with `anExplicitGlyphOutsideTheBasicMultilingualPlaneIsKeptWhole` and `aMonsterWithoutAGlyphLineInheritsABaseGlyphOutsideTheBasicMultilingualPlane`.]
- Limits: A: `brand` no 254-brand limit, `visuals` caps (per the Stage 0 answer). [<span style="color:#006400">**CLOSED**</span> 26-11-08: `BrandAssembler.java` throws on the 255th record, as C's `finish_parse_brand` refuses it, and `VisualsCycleAssembler.java` enforces the 512-line, 32-colour, 8-group and 64-name caps as C does. `BrandReaderTest` (39), `BrandAssemblerTest` (34), `VisualsCycleAssemblerTest` (28) and `VisualsReaderTest` passed this date. The slay 254 limit and the visuals flicker three-step rule are separate items.]
- Numbering and names: A: `egoitem` skipped record takes no `egoIndex`, `objectbase` synthetic `TV_NONE` named "none" [<span style="color:#006400">**CLOSED**</span> 26-10-28: the sentinel has a `null` name], `egoitem` two misspelled error messages. [<span style="color:#006400">**CLOSED**</span> 26-11-08: `EgoItemAssembler.java` skips no record, since every bad field throws, so an ego's `egoIndex` is its place in the file counted from 0, as `finish_parse_ego` numbers it. `EgoItemReaderTest.egoIndexIsFileOrderFromZero` pins it. Neither misspelled message is left: the `"beginning at line:"` text now comes from `ParseUtils.parseIntString` with its space, and the curse power is read through the same helper, so `"an malformed curse power"` is gone. `EgoItemReaderTest` passed this date (108).]
- Larger files that differ in several small ways, one entry each: A: `vault`, `roomprofile`, `playertimed`, `chesttrap`, `flavour` (tval, glyph, `fixed:` sval parts), `history` (successors), `visuals` (cycles and flicker). [<span style="color:#006400">**CLOSED**</span> 26-11-08: open items 1 to 6 below are closed and the seven entries in `Assembler_Issues.md` are tagged.]
  - **Checked 26-11-08:** each assembler and the C it ports was read, and the reader and assembler tests for all seven files ran with `BUILD SUCCESSFUL`. The `Assembler_Issues.md` entries for these files date from 2026-10-18 and 2026-10-19, and their "What Java does" bullets are out of date.
  - **Met in the code:**
    - `vault/VaultAssembler.java`: zero rows and columns load, `type:` is case-exact, the `D:` line count is not compared with `rows:`, and every fault stops the load.
    - `roomprofile/RoomProfileAssembler.java`: `type:`, `rating:`, `rows:`, `columns:` and `doors:` wrap to a byte, the layout is unchecked, and the result is in reverse file order.
    - `playertimed/PlayerTimedAssembler.java`: a one-character down message is kept, `lower-bound:` is limited to 0 to 32767, `flags:` accepts only `NONSTACKING`, `fail:` lines run in C's order, and `exact` is read through `ParseUtils.isTrue`.
    - `chesttrap/ChestTrapAssembler.java`: `destroy:` and `magic:` take any non-zero value, and the whole-file rejection is an accepted deviation.
    - `flavour/FlavourAssembler.java` and `FlavourKindAssembler.java`: colour fallback, index base and sign, glyph, tval and the `fixed:` sval path match C.
    - `visuals/VisualsCycleAssembler.java` and `VisualsFlickerAssembler.java`: the caps, the empty-cycler error and the three-step flicker padding are in.
  - **Open 1: vault values do not wrap to a byte.** [<span style="color:#006400">**CLOSED**</span> 26-11-08]
    - What C does: `rat`, `hgt`, `wid`, `min_lev` and `max_lev` in `struct vault` are `uint8_t`. Rows and columns are wrapped before the size cap is tested, and `max-depth:` is tested for zero on the raw value before the wrap.
    - Closed: `VaultAssembler.java` wraps all five with `Math.floorMod(…, 256)` and tests `max-depth:` for zero on the raw value first. `VaultAssemblerTest` pins `rows:278` giving 22, `rows:279` refused, `columns:278` giving 22, `min-depth:260` giving 4, `max-depth:300` giving 44, `max-depth:256` giving 0 and the rating wrap (-3 gives 253). `VaultReaderTest` pins the rating wrap through the reader. Both classes passed this date.
  - **Open 2: the room template accepts the `NONE` flag.** [<span style="color:#006400">**CLOSED**</span> 26-11-08]
    - What C does: `lookup_flag` starts at index 1, so `NONE` is refused in `parse_room_flags`, as it is in `parse_vault_flags`.
    - Closed: `RoomProfileAssembler.java` throws "illegal sentinel room flag value" for `NONE`, alone or after a good flag, and its Javadoc no longer lists `NONE` as valid. `theSentinelNoneFlagStopsTheLoad` is in both `RoomProfileReaderTest` and `RoomProfileAssemblerTest`, which passed this date.
  - **Open 3: chest trap absent text is `""`.** [<span style="color:#006400">**CLOSED**</span> 26-11-08]
    - What C does: an absent `msg:` or `msg-death:` leaves `NULL`, and the Stage 0 convention is `null`.
    - Closed: `ChestTrapGrammar.g4` seeds both with `null` and `ChestTrapAssembler.java` copies them through, so no deviation needs registering. The generated parser was regenerated, and the Javadoc in `ChestTrapAssembler.java`, `ChestTrapParseRecord.java`, `ChestTrap.java` and the grammar comment now say `null`. `ChestTrapAssemblerTest` and `ChestTrapReaderTest` expect `null` and passed this date.
  - **Open 4: history keeps one successor per chart.** [<span style="color:#006400">**CLOSED**</span> 26-11-08: accepted deviation, registered in `docs/Accepted_Deviations.md` under History and marked as potential future work (successor per entry) if modded history files are ever supported.]
    - What C does: `parse_history_chart` stores the successor on each entry and accepts a repeated chart whatever successor it names.
    - Java now: `HistoryAssembler.java` refuses a repeat that names a different successor, pinned by `HistoryReaderTest.aChartRepeatedWithADifferentSuccessorStopsTheLoadHereWhereCAcceptsIt`. `Accepted_Deviations.md` has no history entry.
    - To close: register the deviation, or store the successor per entry.
  - **Open 5: history chart order is first-appearance.** [<span style="color:#006400">**CLOSED**</span> 26-11-08: `HistoryAssembler.java` builds the charts in file order and returns them reversed, so they come back newest first as C's list does. `HistoryReaderTest.chartsComeBackNewestFirstAsInC`, `aRepeatedChartKeepsItsFirstPlaceInTheNewestFirstList` and `everyShippedChartAgreesWithTheUpstreamTableInNewestFirstOrder` pin it, and `PlayerHistoryChartTest`, `PlayerBirthGetHistoryTest` and the `PlayerRegistry` and `PlayerRace` tests passed this date. The `addFirst` fault in `PlayerHistoryChart.addEntry` was found on the way and is fixed (`entries.add`).]
    - What C does: `parse_history_chart` points each new chart at the old head, so the chart list is newest first.
    - Java now: `HistoryAssembler.java` returns the charts in first-appearance order and its Javadoc says nothing observes the difference. Stage 0 says "always echo C's ordering".
    - To close: reverse the list, or register the deviation.
  - **Open 6: flicker has no test and its entry is stale.** [<span style="color:#006400">**CLOSED**</span> 26-11-08]
    - What C does: `visuals_flicker_get_attr_for_frame` reads `cycles[attr * 3 + (frame % 3)]` from a zero-filled table, so an unwritten base colour reads colour 0, dark, at every frame. `BASIC_COLORS` comes back only for a missing table or an attribute of `MAX_COLORS` or more.
    - Closed: `FlickerTable.java` has the read path, `getAttr(ColourEnum, int)`, and its Javadoc says a missing key reads dark. `FlickerTableTest` (10) pins the frame wrap, the absent key, the empty table, a padded dark step and every shipped block; `VisualsReaderTest.FlickerAssemblerUnit` (23) pins the three-step padding and the dropped fourth step. Both passed this date. The visuals entry in `Assembler_Issues.md` carries the flicker rule in its CLOSED tag.
  - **Closing the bullet:** [<span style="color:#006400">**CLOSED**</span> 26-11-08: items 1 to 6 are settled. The vault, room profile, chest trap, player timed, flavour and history entries in `Assembler_Issues.md` are tagged CLOSED, and the visuals entry now covers the flicker rule.]
- On a second parse: A: `artifact` special-light activation and time go to the artifact. Low risk, but the birth re-run is the only place it shows. [<span style="color:#006400">**CLOSED**</span> 26-11-08: `ArtifactAssembler.java` tests `kind.getKindIndex() >= ObjectRegistry.getObjectBaseKindMax()` for a `TV_LIGHT`, as C tests `kidx >= z_info->ordinary_kind_max`, so on the birth re-run the activation and time stay on the kind and the artifact keeps none. `ArtifactReaderTest` passed this date (53), including `aSecondParseKeepsTheSpecialLightsActivationAndTimeOnTheKindAsInC`. Closed earlier in `Assembler_Issues.md` on 26-10-23.]

## Stage 6: grammar fixes in shared files

One edit, many lexers. Regenerate once at the end of the stage.

1. **`imports/CommentsAndEol.g4`.** Leading whitespace, a last-line comment with no newline, and tabs before the newline.
   - G: "Leading whitespace on a line" is the entry; the same gap is repeated in the Realm, Flavour, Names, History, Quest, Vault, RoomProfile, DungeonProfile, GameConstants, ChestTrap, PlayerTimed, PlayerRace, PlayerProperty, Terrain, Trap, Shape and Visuals entries.
   - G: trailing blanks also belong here: `BrandLexer.g4` spaces after a flag, `ProjectionLexer.g4` a blank after `code:` or `msgt:`.
2. **`imports/EffectBlock.g4` and `EffectBlockLexer.g4`.** `effect-yx:` with `dice:`, a line before the first `effect:`, a second `dice:`, and `effect-msg:` accepted where C has none.
   - G: `MonsterSpell`, `ItemObject`, `Curse`, `Activations`, `Shape`, `Trap` and `PlayerClass` entries all describe this one block.
3. **`imports/DiceStrings.g4`.** G: `ProjectionLexer.g4` lower-case `m` and a leading blank.
4. **The `INTEGER` rule.** C reads `strtol`/`strtoul` in base 0, refuses a leading minus on unsigned fields, and ignores trailing text.
   - Decide whether the shared answer is a signed and an unsigned token, then apply it to: G: `Pain`, `BlowMethod`, `Pit`, `MonsterSpell` (`hit:`), `MonsterBase`, `Monster`, `Slay`, `Brand`, `Curse`, `EffectBlockLexer` (`aim:`, `level:`, `power:`), `ObjectProperty`, `EgoItems`, `Artifact`, `Projection`, `Shape`.
   - Pair with the Stage 3 numbers helper, since the assembler half of the same entries is there.

## Stage 7: grammar patterns that repeat across files

Each pattern is one recipe applied to many grammars. Do the recipe once on the simplest grammar, then repeat. Regenerate after each pattern.

### 7a. Fixed record order to unordered, repeatable directives

- G: `BlowMethodGrammar.g4`, `SummonGrammar.g4`, `MonsterBaseGrammar.g4`, `BrandGrammar.g4`, `ObjectBaseGrammar.g4`, `ProjectionGrammar.g4`, `ObjectPropertyGrammar.g4` (also needs `type:` optional), `PlayerPropertyGrammar.g4`, `TerrainFeatureGrammar.g4`, `QuestGrammar.g4`, `VaultGrammar.g4`, `RoomProfileGrammar.g4`, `ChestTrapGrammar.g4` (`code:` after `name:`), `PlayerTimedGrammar.g4`.
- Each needs its `ParseRecord` and assembler to cope with an absent field, so this is where the Stage 0 absent-value convention is applied for the second time.

### 7b. Name-only records and empty files

- Same edit each time: `+` becomes `*` on the record's directive loop and on the file's record loop.
- G: `Pit`, `MonsterSpell`, `Monster`, `Slay` (file), `Brand` (file), `ItemObject`, `Curse`, `Activations`, `ObjectProperty`, `EgoItems`, `Artifact`, `Realm`, `Shape`, `Flavour` (bare `kind:`), `History` (chart with no phrase), `Body`, `PlayerRace`, `PlayerClass` (also a `magic:` with no book and a book with no spell), `Trap`, `World`, `DungeonProfile`.
- Decide once whether the Java-only `record-count:` header stays. It touches G: `Projection` (upstream file fails without it), `ChestTrap`, `PlayerTimed` and the other files that carry it.
- Do this inside 7a for any grammar in both lists.

### 7c. Free text that appends in one language and replaces in the other

- Java replaces, C appends: G: `BlowEffectGrammar.g4` `desc:`, `CurseGrammar.g4` `msg:` ([<span style="color:#006400">**CLOSED**</span> 26-10-20: now appends, as `string_append` does]), `ChestTrapGrammar.g4` `msg:` and `msg-death:`, `PlayerTimedGrammar.g4` text lines and repeated effects, `TerrainFeatureGrammar.g4` message lines.
- Java appends, C replaces: G: `ObjectPropertyGrammar.g4` `msg:` and `desc:`.
- Last wins in Java, C joins or keeps all: G: `ObjectPropertyGrammar.g4` `bindui:`, `PlayerPropertyGrammar.g4` split `desc:`, `HistoryGrammar.g4` second `phrase:`.
- Overwrites where C rejects: G: `SlayGrammar.g4` second `race-flag:` or `base:`.
- A later zero-power `curse:` erases the earlier: G: `ItemObjectGrammar.g4`, `ArtifactGrammar.g4`, and A: `egoitem/EgoItemAssembler.java` with `EgoItemsGrammar.g4`. Fix the three together.
- The open item at the end of `BlowEffectGrammar.g4`'s entry (search the other grammars for the same fault) is answered by this list; recheck it once the pass is done.

### 7d. Flag and value lists

- C splits on any run of spaces and bars with `strtok(s, " |")`, and an empty `?str` is legal.
- G: `Pit` (four lists), `MonsterBase`, `Monster` (including `spells:`), `ObjectBase`, `Curse` (three lists), `EgoItems`, `Artifact`, `Shape`, `PlayerRace`, `PlayerClass`, `Trap` (`flags:` and `save:`), `TerrainFeature`, `RoomProfile`.
- Empty `flags:` alone: G: `EgoItems`, `Artifact`, `Monster`, `Shape`.
- Settle the shared token shape on one lexer, then copy it. This pattern is the likeliest to bite a hand-edited file.

### 7e. `alloc:` spacing

- G: `ItemObjectLexer.g4`, `EgoItemsLexer.g4`, `ArtifactLexer.g4`. All three read `N to N` with optional blanks in C.

### 7f. Colours given by name or mistyped

- G: `PitLexer.g4`, `MonsterLexer.g4`, `VisualsLexer.g4`, `imports/AngbandDisplayCharacter.g4` (reaches `ItemObject`, `Trap` and `TerrainFeature` too).
- All turn on the Stage 0 strict-or-lenient answer, so the pass is short once it is made.

### 7g. Message names in `msgt:`

- G: `SummonLexer.g4` and `ProjectionLexer.g4`. The projection mode never pops after a bad value, so one lower-case `msgt:` costs the rest of the file; fix that with the case rule.

### 7h. Numeric tval and sval names

- G: `ObjectPropertyLexer.g4` `type-mult:`, `ArtifactLexer.g4` `base-object:`. `TValue.fromName(int)` already has the lookup.

### 7i. Free-text fields the lexer constrains too tightly

- G: `ActivationsLexer.g4` `name:`, `ObjectPropertyLexer.g4` adjectives, `MonsterGrammar.g4` message with a colon or with no text (`message-vis:SHRIEK` is a syntax error, where C stores `""`), `FlavourLexer.g4` colon in a description, `TrapLexer.g4` colon in a description, `RealmLexer.g4` second colon on `stat:`, `PlayerPropertyLexer.g4` `bindui:` symbols and tags, `WorldLexer.g4` level names, `GameConstantsLexer.g4` message and category names, `RoomProfileLexer.g4` `tval:`.

## Stage 8: grammar one-offs

Each is specific to one file and independent of the rest.

- G: `MonsterSpellGrammar.g4` lore and message lines split into extra levels.
- G: `MonsterLexer.g4` a bare number as the `friends:` or `friends-base:` count.
- G: `NamesGrammar.g4` a `word:` before the first `section:`, and repeated sections.
- G: `PlayerClassGrammar.g4` a second `magic:` replaces the first (the one that is silently accepted).
- G: `PlayerTimedGrammar.g4` effect directives (per the Stage 0 answer).
- G: `BlowMethodGrammar.g4` `name:` is trimmed.
- G: `Random.g4` the remaining differences (lower-case `m`, `$` variables, blanks, repeated `d` or `M`), once the `INT_MAX` decision is in.
- G: `TerrainFeatureGrammar.g4`, `GameConstantsGrammar.g4`, `WorldGrammar.g4`, `QuestGrammar.g4`, `VaultGrammar.g4`: anything left after Stages 6 and 7 (indent, base 0, trailing field, negative depth).

## Stage 9: comment and Javadoc sweep

All comment-only, so no regeneration is needed for the Javadoc ones, and the grammar comments can ride along with the last regeneration.

- G: C line numbers in `PainLexer.g4` and `PainGrammar.g4`, `SummonGrammar.g4` and `SummonLexer.g4`, `MonsterBaseGrammar.g4` and `MonsterBaseLexer.g4`, `SlayGrammar.g4` and `SlayLexer.g4`, `BrandGrammar.g4` and `BrandLexer.g4`, `ActivationsGrammar.g4` and `ActivationsLexer.g4`, `ProjectionGrammar.g4` and `ProjectionLexer.g4`, plus the Flavour, GameConstants and PlayerProperty headers.
- G: wrong or stale comments: `BrandGrammar.g4` "six of ten" (it is four), `ObjectBaseLexer.g4` header names handlers C does not have, `ActivationsGrammar.g4` says the index is removed, `ProjectionGrammar.g4` points at a missing note, `PlayerPropertyGrammar.g4` stale note 1.
- A: `blowmethod/BlowMethodAssembler.java` does not mention the reversed list.
- A: `pit/PitAssembler.java` `room:` comment, `monsterspell/` C line number, `activation/` names a function that does not exist, `objectproperty/` says it looks `bindui:` up in the registry, `roomprofile/RoomProfileAssembler.java` says "file order" for a reversed list.
- This is the one stage that is comment-only throughout, so it can be handed to Claude as a comment pass if you ask for it.

## Stage 10: parked

- G: `LoreGrammar.g4` and `LoreLexer.g4`. Nothing calls the reader and no parser is generated from the grammar. It stays parked until the lore data exists (see `monsterlore-parsing-deferred`), and the Stage 0 question is answered then.

## Where to start

- Make the Stage 0 absent-value decision, then take Stage 1 items 1 to 5.
