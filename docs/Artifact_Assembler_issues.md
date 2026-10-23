# Artifact assembler issues

**Date:** 2026-10-22

**What this is:** The seven differences from C found when `HelperFunctions.java`, `ArtifactAssembler.java` and `Artifact.copy()` were checked against the C artifact parser on 2026-10-22. Each entry says what C does first, then what the Java does, how far it reaches and what to do about it. Paths are under `src/main/java/uk/co/jackoftradesltd/`. The port check stopped at stage 1 for `ArtifactAssembler.java` and `Artifact.copy()`, so stages 2 to 5 of `/port` are still to run once these are settled. `HelperFunctions.java` passed and is not listed.

## (1) `middle/objects/Artifact.java`: `copy()` carried the activation and message, C clears both [<span style="color:#006400">**CLOSED**</span> 26-10-22]

- **What C does:** `copy_artifact` in `obj-randart.c` copies the struct, then sets `activation` and `alt_msg` to `NULL` on the copy.

- **What Java did:** `copy()` deep-copied the activation and passed the message through.

- **Closed:** `Artifact.java` `copy()` now passes `null` for both, so the code matches C. The Javadoc still describes the old behaviour; that is entry (2).

- **Call to action:** none for the code. Fix the Javadoc under (2).

## (2) `middle/objects/Artifact.java`: two Javadoc blocks contradicted C and the code [<span style="color:#006400">**CLOSED**</span> 26-10-23]

- **What C does:** `copy_artifact` in `obj-randart.c` copies a whole `struct artifact`, and the random-artifact designer calls it to roll a design back.

- **What Java says:** the class block says "C never copies a `struct artifact`" and that `copy()` is "a Java addition". The `copy()` block says the activation is deep-copied, the message is passed straight through, and a null message stays null. Its provenance line says "activation and message carried across on 261008".

- **Reach:** comments only. Nothing calls `copy()` yet, so the first reader to be misled would be whoever ports `obj-randart.c`.

- **Closed:** both blocks in `Artifact.java` are rewritten. The class block says `copy()` is the port of `copy_artifact`, used by `design_artifact`. The `copy()` block says the activation and message are left `null` as in C. The stamp no longer says "carried across".

- **Call to action:** run `/port` on `Artifact.copy()` to finish stages 2 to 5.

## (3) `backend/parser/artifact/ArtifactAssembler.java`: a `values:` figure of exactly `INT_MAX` or `INT_MIN` loaded [<span style="color:#006400">**CLOSED**</span> 26-10-23]

- **What C does:** `find_value_arg` in `datafile.c` reads the figure with `strtol` and refuses `lv <= INT_MIN || lv >= INT_MAX`, so `parse_artifact_values` ends the load with `PARSE_ERROR_INVALID_VALUE`.

- **What Java did:** `Integer.parseInt` accepted `2147483647` and `-2147483648`. Anything beyond them already threw, so only the two boundary figures differed.

- **Reach:** none from shipped data. No `values:` line in `artifact.txt` is near either limit.

- **Closed:** `ArtifactAssembler.java` now throws `DataFileParseException` when the parsed figure is `Integer.MIN_VALUE` or `Integer.MAX_VALUE`, so the code matches C. Pinned by `ArtifactReaderTest.aValueOfIntMaxOrIntMinOrBeyondStopsTheLoadAsInC`, which also checks that the figures one inside each limit load.

- **Call to action:** none.

## (4) `backend/parser/artifact/ArtifactAssembler.java`: a special light's kind is written even when the record has no such line [<span style="color:#006400">**CLOSED**</span> 26-10-23]

- **What C does:** `parse_artifact_weight`, `parse_artifact_cost` and `parse_artifact_time` in `obj-init.c` write to the kind only when their line is present, and only for a kind above `ordinary_kind_max` (a light, for time). `parse_artifact_act` stores `findact(name)` on the kind, which is `NULL` for an unknown name.

- **What Java did:** set the kind's weight and cost whenever its index was at or above the limit, and set its time to a zero `Random` for a special light with no `time:` line. An unknown `act:` left the kind's old activation, where C clears it.

- **Reach:** none from shipped data. All 14 special artifacts have `weight:` and `cost:`, and the three special lights have `time:` and `act:`. It would show only on a second parse of a record that left a line out.

- **Closed:** `ArtifactAssembler.java` now writes the kind's weight and cost only when the record has a `weight:` or `cost:` line, and its time only when the record has a `time:` line. A written `0` counts as a line. For a special light with an `act:` line, the kind's activations are cleared, and the named activation is added only if it resolves, so an unknown name leaves the kind empty as `findact`'s `NULL` does in C. With no `act:` line the kind is left alone. Pinned by `ArtifactReaderTest.aSpecialKindIsWrittenOnlyByTheLinesTheRecordHas` and `ArtifactReaderTest.aSpecialLightsActIsWrittenToTheKindOnlyWhenTheLineIsThereAndClearsOnAnUnknownName`.

- **Call to action:** none.

## (5) `backend/parser/artifact/ArtifactAssembler.java`: an artifact with no `msg:` had `""`, C has `NULL` [<span style="color:#006400">**CLOSED**</span> 26-10-23]

- **What C does:** `parse_artifact_msg` appends to `alt_msg`, which starts `NULL`. `activation_message` in `cmd-obj.c` tests `obj->artifact->alt_msg` and falls back to the activation's own message when it is `NULL`.

- **What Java did:** `ArtifactAssembler.java` passed `record.msg()` straight through, which is `""` for the 133 of 138 artifacts with no `msg:` line. `ArtifactReaderTest.theShippedFileMatchesTheTableWorkedOutFromTheUpstreamFile` expected `""`.

- **Closed:** `ArtifactAssembler.java:304` now passes `null` when the record has no message. An empty `msg:` line is a hard error, as it is in C, so `""` can only mean there was no line. `Artifact.copy()` passes `null` as `copy_artifact` does. `ArtifactReaderTest.theShippedFileMatchesTheTableWorkedOutFromTheUpstreamFile` now expects `null` for an artifact with no message.

- **Call to action:** none.

## (6) `backend/parser/artifact/ArtifactAssembler.java`: the `assemble` Javadoc described soft errors [<span style="color:#006400">**CLOSED**</span> 26-10-23]

- **What C does:** `artifact_parser` in `obj-init.c` ends the load on the first parse error, and `parse_artifact_act` is the one handler that never errors on a bad name.

- **What Java says:** the block at `ArtifactAssembler.java:67` to `:77` says a record with an unresolvable field is "reported into `errors` and skipped", lists "activation" among the faults, and describes `errors` as the sink that drops the record. Every one of those sites now throws `DataFileParseException`, an unknown activation only logs a warning, and `errors` is not used.

- **Reach:** comments only, but the class block also omits the brand and slay ordering and the `HelperFunctions` number reading.

- **Closed:** the `assemble` block in `ArtifactAssembler.java` now says the load ends at the first bad field, lists every throw site, notes that an unknown `act:` is only a warning and that `errors` is unused, and adds `@throws`. The class block now covers the `HelperFunctions` number reading and the brand and slay ordering.

- **Call to action:** none for the Javadoc. Stages 3 to 5 of `/port` on `ArtifactAssembler.java` are still to run.

## (7) `docs/Assembler_Issues.md`: five artifact entries said "Not fixed" [<span style="color:#006400">**CLOSED**</span> 26-10-23]

- **What C does:** unchanged by this entry; the five entries each describe a C behaviour the Java now matches.

- **What the doc says:** the entries on `IGNORE_`/`HATES_` flags, `alloc:` bounds, an unknown `act:` name, `graphics:` on a kind that is not `INSTA_ART`, and a second parse's special lights are all marked "Not fixed". `ArtifactAssembler.java` now matches C on all five, and `ArtifactReaderTest` pins the new behaviour. The entry on a record with no `base-object:` is still true, because `ArtifactAssembler.java` throws where C keeps the record with tval 0.

- **Reach:** the doc only. A reader would plan work that is already done.

- **Closed:** the five entries in `docs/Assembler_Issues.md` are marked CLOSED with the date, what closed each one, and the new pinning test names. The `base-object:` entry is left open.

- **Call to action:** none.
