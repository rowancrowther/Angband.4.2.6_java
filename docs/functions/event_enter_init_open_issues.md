# Open issues — `EVENT_ENTER_INIT` UI→core ack (260912)

Snapshot after: moving `ElementEnum` to `channel`, adding the `EVENT_ENTER_INIT` handshake (`UIMessage.SimpleUIMessage`,
the `GameConstants.init(CoreChannel)`/`GameEngine.loadGameConstants
(CoreChannel)` widening, `UILoop`'s post-load send), removing `Core`'s static channel accessor, and widening the five
direct `GameConstants.init()` test call sites plus `GameEngineBootstrapTest`. Numbered in the order raised, not by
severity.

**Re-run 260912 (later the same day), against `./gradlew test --rerun-tasks`, full suite: 4930 tests, 1 failed, 9
skipped.** Status of each item below re-checked against that run and a fresh read of every file it names.

## 1. `GameConstants.init()`'s wait doesn't check what it received — RESOLVED

`GameConstants.java:202` — `init(Core)` now returns `boolean`. The unchecked-message problem itself (the receive at what
is now `GameConstants.java:226` still takes whatever `UIMessage` is next without filtering it first) is unchanged, but
its consequence is fixed: the `else` branch at
`GameConstants.java:233` (`if (core.handleChannelOutput(message)) return true;`) now hands that
`true` back to a caller that acts on it. See issues 2 and 3.

## 2. `CoreTest.saveAndStopIsAnsweredWithStoppedAndThenTheThreadEnds()` fails — RESOLVED

The `true` from issue 1 now propagates: `GameEngine.loadGameConstants(Core)` (`GameEngine.java:144
-145`) returns `boolean` and bails at `if (GameConstants.init(core)) return true;` before building a player or anything
else, and `Core.gameLoop()` (`Core.java:207`) bails the same way at
`if (gameEngine.loadGameConstants(this)) return;` instead of falling into its own `while(true)`. Confirmed green in the
260912 re-run — `CoreTest.xml`: `failures="0" errors="0"`,
`saveAndStopIsAnsweredWithStoppedAndThenTheThreadEnds()` in 0.054s, no stack trace in
`system-err`.

## 3. `CoreTest.startDoesNotEndTheLoop()` and `aMisroutedWindowEventIsIgnored()` pass for the wrong reason — RESOLVED

Same fix as issue 2 closes this too: the `SAVE_AND_STOP` each test sends at teardown now reaches
`Core.gameLoop()`'s main loop the same way the `START`/`WindowCloseRequested` message each test opens with does, rather
than either message being silently absorbed by `GameConstants.init()`'s premature receive. Confirmed green in the same
re-run — `aMisroutedWindowEventIsIgnored()` in 0.504s, `startDoesNotEndTheLoop()` in 0.505s, both under `CoreTest`'s
`failures="0"`.

## 4. `EgoItemReaderTest.numericItemSvalIsRejected()` fails in the full suite, passes alone — NOT REPRODUCED THIS RUN

`EgoItemReaderTest.xml` from the 260912 re-run: `tests="28" ... failures="0" errors="0"`, run as part of the same
full-suite invocation this file's other statuses come from. Whatever
`UIRegistry`-leak condition triggered this earlier in the day did not recur here. Left open rather than closed — nothing
changed the suspected cause (`UIRegistry` still has no `reset()`), so this looks like it depends on JVM-wide test
ordering that this run happened not to hit, not something fixed.

## 5. `Core.java`'s `SimpleUIMessage` arm is a dead stub — STILL OPEN, reworded

Now at `Core.java:252-257`, and no longer the bare `// TODO: Something` originally flagged — it reads the message's
`type` and comments that `EVENT_ENTER_INIT` is deliberately ignored here because it belongs to the UIEntry loaders. But
the arm is still unreachable in practice for the reason originally given: the one `SimpleUIMessage` a real run produces
(the ack) is still consumed by `GameConstants.init()`'s own wait loop (`GameConstants.java:226-236`), never reaching
`gameLoop()`'s switch. The fix in issues 1-3 changed what happens *after* the ack — not whether this arm ever sees
traffic.

## 6. `UIMessage.SimpleUIMessage` has no Javadoc — STILL OPEN

`UIMessage.java:91-93` (line numbers unchanged) — the record and its `type` component remain undocumented, unlike
`LifecycleUIMessage` and `WindowCloseRequested` alongside it.

## 7. `BoundaryTest.java`'s comment is stale — STILL OPEN

Now at `BoundaryTest.java:152-158`. Still argues `UIEntry`'s typed field "should flatten rather than `ElementEnum`
moving to `channel`" — the plan the actual move superseded — and
`FRONTEND_BASELINE` (`BoundaryTest.java:160-166`) still has no `ElementEnum`/`UIEntry` entry for the comment to be
attached to. Unchanged since first raised.

## 8. `BoundaryTest > backendNamesNothingInFrontendBeyondTheBaseline()` — STILL OPEN, still the suite's one failure

`ObjectPropertyAssembler.java:24` and `PlayerPropertyAssembler.java:24` still name
`uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry`, still absent from `BACKEND_BASELINE`
(`BoundaryTest.java:200-205`). This is the single failure in the 260912 re-run
(`BoundaryTest$TheCoreDoesNotKnowTheFrontEnd` XML: `failures="1"`, `BoundaryTest.java:913`). Waiting on a decision: add
the two lines to `BACKEND_BASELINE` if the crossing is meant to stay, or change the assemblers if it isn't — either way
it's a call for whoever owns that file, not something to resolve by guessing.

## 9. `UIPlayerTest` — three failures, pre-existing, unrelated — NOW PASSING, cause not investigated

`UIPlayerTest.java:198`, `:212`, `:261` all pass in the 260912 re-run (`MatchingCache.xml` and
`MismatchedCache.xml`: `failures="0"` across all 5 nested cases). This area (`PlayerEventStatusUpdate`/
`PlayerStatusView.bodyCount()`) is flagged as in-progress work and was explicitly set aside rather than diagnosed, so
this is a status note, not a fix record — don't read it as resolved.
