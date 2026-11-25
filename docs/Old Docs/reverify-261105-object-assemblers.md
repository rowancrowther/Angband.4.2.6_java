# Reverify results — object assemblers, 2026-11-05

This is a scratch record of the `/reverify` passes of 2026-11-05 over `ObjectBase` (constructor), `ObjectBaseAssembler`,
`ArtifactAssembler`, `EgoItemAssembler`, `ItemObjectAssembler`, `ParseUtils` and `VisualsCycleAssembler`. It lists what
the passes found against the C original, what was changed in the test tree and the Javadoc to close each finding, and
the two differences from C that were left open after the second pass. Updated 2026-11-06: both of those are now closed
(A as an accepted deviation, B by the grammar change), and the build and the full suite are green (13,670 tests, 0
failed, 19 skipped). Line numbers are Java lines as read in that pass and will drift as you edit.

## State of the build

[<span style="color:#006400">**CLOSED**</span> 26-11-05: `compileJava` and `compileTestJava` pass, and the full suite passes with 0 failures. The 129 failures of the first pass are gone, and so are the `initializationError` failures on the real-data loaders. The bullets below describe the first pass.]

- `compileJava` and `compileTestJava` both pass.
- Targeted tests: 129 of 1739 fail.
- Every test that loads the real data files dies in `initializationError` (Artifact, EgoItem, ItemObject, Curse, registry loaders).
- Cause: item 1 below, which stops `object_base.txt` loading at line 19.

## Test-tree edits made in the first pass

[<span style="color:#006400">**CLOSED**</span> 26-11-05: done, and compiled.]

- 13 `ObjectBase` constructor call sites in 11 test files now pass a `HashMap` where they passed two `Flag<ElementEnum>`.
- `ObjectKindConstructorsTest.java` builds a real `HATES` entry for fire instead of a `Flag<ElementEnum>`.
- `ItemObjectReaderTest.java:1177` passes a `List` for the curse parameter, not a `Map`.
- `ObjectBaseTest.java`: the `hates(...)` helper returns a `Map<ElementEnum, ElementInfo>`.
- `ObjectBaseTest.java`: "hatesFlagIsOnlyReadNotRetained" is replaced by two stored-by-reference tests (element map, object flags).
- The two grammars `ArtifactGrammar.java` and `ItemObjectGrammar.java` were regenerated from your edited `.g4` files. Regenerated again in the second pass: the output is identical to the committed parsers.

## (1) ElementEnum.lookup and ParseUtils.grabElementFlag disagree

[<span style="color:#006400">**CLOSED**</span> 26-11-05: `ParseUtils.java` now passes `"ELEM_" + name`, and `ElementEnum.lookup` takes the whole constant name, so the two callers agree with `ItemObjectAssembler.java:313`. `object_base.txt` loads. `ElementEnumLookupTest` and `ParseUtilsGrabElementFlagTest` were moved to the whole-name form, and the Javadoc of `ElementEnum.lookup` and `ParseUtils.grabElementFlag` now describes it. The bullets below describe the state before the fix.]

- C: `grab_element_flag` matches the text after the first underscore (`FIRE`) against `element_names`.
- `ElementEnum.java:229-231` compares `e.name()` with its argument, so only `ELEM_FIRE` is found.
- `ParseUtils.java:1022` passes the bare `FIRE`, so `grabElementFlag` returns `false` for every word.
- `object_base.txt` line 19 (first `HATES_` flag) therefore ends the load.
- 49 `ParseUtilsGrabElementFlagTest` and 29 `ElementEnumLookupTest` cases fail directly.
- The Javadoc at `ElementEnum.java:206-227` still describes the bare-name form.
- `ItemObjectAssembler.java:313` passes `"ELEM_" + name`, which the current `lookup` accepts. The two callers want opposite forms.

## (2) ObjectBaseAssembler: unknown colour

[<span style="color:#006400">**CLOSED**</span> 26-11-05: `ObjectBaseAssembler.java:158-165` gives `COLOUR_WHITE` for a present but unknown colour and `COLOUR_DARK` for an empty one, as C does. The `ObjectBaseAssemblerTest` colour cases and `ObjectBaseReaderTest.anUnknownColourIsWhiteAsInC` pass. The bullets below describe the state before the fix.]

- C: a present but unknown colour is white. An absent `graphics:` line leaves `attr` at 0, `COLOUR_DARK`.
- `ObjectBaseAssembler.java:158-163` turned every `null` into `COLOUR_DARK`.
- 27 `ObjectBaseAssemblerTest` cases and `ObjectBaseReaderTest.anUnknownColourIsWhiteAsInC` failed on this.

## (3) ArtifactAssembler: curse power read from the name

[<span style="color:#006400">**CLOSED**</span> 26-11-05: `ArtifactAssembler.java:258-259` reads `curseDataRecord.power()`. A later `curse:` line with power 0 now leaves an earlier one, as `parse_artifact_curse` does, and `ArtifactReaderTest.aLaterCurseWithNoPowerLeavesAnEarlierOneAsInC` pins it. The same holds for objects: `ItemObjectReaderTest.aLaterZeroPowerCurseLeavesAnEarlierOneAsInC`. The bullets below describe the state before the fix.]

- C: `parse_artifact_curse` takes the power from the line's integer field.
- `ArtifactAssembler.java:258` set `value = curseDataRecord.name()` and parsed that as the power.
- An artifact with a `curse:` line would have ended the load as an invalid power.

## (4) Cleared since the first pass

[<span style="color:#006400">**CLOSED**</span> 26-11-05: and now confirmed by the full suite, which was the caveat in the first pass.]

- `ItemObjectAssembler.java:346-351`: `NONE` and `MAX` modifiers are refused.
- `ItemObjectAssembler.java:327-339`: the `RES_` figure has the trailing-text and `INT_MIN`/`INT_MAX` checks.
- `ItemObjectAssembler.java:368-403`: brands and slays match by code, throw when unknown, and come out in registry order.
- `ItemObjectAssembler.java:404-418`: curses are read as a list.

## (5) Unchanged since the first pass

Three bullets in the first pass. One was withdrawn and the other two are closed, in the section "Still open" at the foot.

- [<span style="color:#006400">**CLOSED**</span> 26-11-05, withdrawn: `EgoItemAssembler.java:391-399` accepts an `item:` whose kind index is 0 or below, where C returns `PARSE_ERROR_INVALID_ITEM_NUMBER`. That check cannot fire in C for a kind that was found, because real kinds start at index 1 there. Java indexes from 0 (`ObjectRegistry.java:1339`), which falls under the accepted base-0 deviation. Not a mismatch.]
- [<span style="color:#006400">**CLOSED**</span> 26-11-06, accepted: `ArtifactAssembler.java:304-315`: a record with no `base-object:` is refused; C loads it as tval 0. See "Still open", item A.]
- [<span style="color:#006400">**CLOSED**</span> 26-11-06: `ArtifactAssembler.java:121`, `ItemObjectAssembler.java:432`, `ItemObjectAssembler.java:458`, `ItemObjectAssembler.java:459`: an absent `desc:`, `msg:` or `vis-msg:` passed through as `""`, where C holds `NULL`. See "Still open", item B.]

## Failing tests found in the second pass

[<span style="color:#006400">**CLOSED**</span> 26-11-05: 52 tests failed after the first pass's fixes, all stale expectations and none a new mismatch with C. They were fixed in `../../src/test` and the suite is green.]

- 35 expected the bare element name from `ElementEnum.lookup`. They now use the whole constant name (see item 1).
- 15 looked for old message wording ("illegal hates element flag value", "sentinel element name", "unknown flag: NOTAFLAG"). They now look for what the assemblers throw ("illegal flag value", "unknown ignore/hates flag").
- 2 asserted the old curse deviation and were renamed and flipped to the C behaviour (see item 3).

## Javadoc that was wrong

[<span style="color:#006400">**CLOSED**</span> 26-11-05: rewritten, each block stamped 261105.]

- `ElementEnum.java`: `lookup` describes the whole-name form.
- `ParseUtils.java`: `grabElementFlag` describes the `"ELEM_" + name` lookup.
- `ObjectBase.java`: the constructor, and the `name`, `elementInfo` and `flags` fields, `getName` and `getFlags`, describe the map and sets the assembler builds and the `null` name.
- `ObjectKindFlag.java`: the class block says the object-base, object-kind, ego-item and artifact assemblers all handle `IGNORE_` and `HATES_`.

## What matches

- `ObjectBase.java:164-175`: the constructor stores its eight arguments as C's record does.
- `ObjectBaseAssembler.java`: tval, name, colour, flag, break/max-stack and sentinel handling.
- `ItemObjectAssembler.java`: flags, `RES_` figures, modifiers, brands, slays, curses.
- `EgoItemAssembler.java`: everything.
- `ArtifactAssembler.java`: everything, with item A below recorded as an accepted deviation.
- `ParseUtils.java`: the number readers, trimmers, sign tests and `grabElementFlag`, including the `strncmp` prefix rule.
- `VisualsCycleAssembler.java`: the 512-line limit, 8 groups, 64 names, 32 steps, the empty-colour error and the two post-placement checks, against `ui-visuals.c`.

## Still open

Two differences from C were left open after the second pass. Neither was hit by the shipped data in a way that failed a test. Both are closed as of 2026-11-06: A is recorded in `../Accepted_Deviations.md` under "Artifact", and B is fixed in the grammars. The bullets under each heading describe the state when it was open.

### A. ArtifactAssembler: a record with no `base-object:` is refused

[<span style="color:#006400">**CLOSED**</span> 26-11-06, accepted: you chose to keep the throw. It is recorded in `../Accepted_Deviations.md` under "Artifact" as "A record with no `base-object:` line is refused, where C loads it as tval 0". The question of what C's `lookup_kind(0, 0)` does is answered there: it prints "No object: 0:0" and returns `NULL`. Not traced: the later handlers on such a record.]

- C: `parse_artifact_name` zero-fills the new artifact, so `tval` and `sval` start at 0. `parse_artifact_base_object` is the only handler that sets them, and nothing at the end of a record checks that it ran. An artifact with no `base-object:` line therefore loads with tval 0.
- Java: `ArtifactAssembler.java:304-315` leaves `objectBase` null when `tVal` is null and throws "unknown object type", so the load ends.
- Shipped data: `artifact.txt` has 138 `name:` lines and 138 `base-object:` lines, so every shipped record has one. Only a modded file could show the difference.
- What I did not check: what C's `lookup_kind(0, 0)` returns for the weight, cost, graphics, `act:` and `time:` handlers on such a record. Those handlers assert the result is non-null, so C may fail there on some records with no `base-object:`. Read `lookup_kind` in `obj-util.c` before deciding.
- Options: leave the throw and record it as an accepted deviation (it turns an odd modded file into a clear error), or load the record as tval 0 as C does, which needs a null-safe path through the kind lookup, the special-kind test and the glyph handling that follow it.

### B. Absent `desc:`, `msg:` and `vis-msg:` are `""` where C holds `NULL`

[<span style="color:#006400">**CLOSED**</span> 26-11-06: `ArtifactGrammar.g4` gives the artifact `desc` as `null` when no line was read, and `ItemObjectGrammar.g4` does the same for the kind's `desc`, `msg` and `vis-msg`. Both parsers were regenerated, so the four assembler sites now receive `null` with no change of their own. `ItemObjectReaderTest` was moved to expect `null` for an absent line, and the full suite passes.]

- C: the text fields of a zero-filled record stay `NULL` until a `desc:`, `msg:` or `vis-msg:` line appends to them with `string_append`.
- Java: `ArtifactAssembler.java:121` passes the artifact's `desc` through, and `ItemObjectAssembler.java:432`, `:458` and `:459` pass the kind's `desc`, `msg` and `vis-msg`. When the line is absent the grammar's empty string arrives, so the field is `""`.
- Already done the C way: the artifact `msg:` (`ArtifactAssembler.java:291`) and the ego `desc:` (`EgoItemAssembler.java:465`) become `null` when empty.
- Shipped data: `object.txt` has 225 `desc:` lines for 375 kinds, so well over a third of the kinds have none. `artifact.txt` has 5 `msg:` lines for 138 artifacts, and `object.txt` has 18 `msg:` and 2 `vis-msg:` lines.
- Impact: any later code that tests `text == null` the way C tests `text != NULL` will take the wrong branch for a kind with no `desc:`. Nothing in the loaders fails, so no test catches it yet.
- Options: map empty to `null` in these four places, as the two fields above already do, or record it as an accepted deviation and make the later code test for empty as well.
