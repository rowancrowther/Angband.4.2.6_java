# grabElementFlag wiring: state of the five call sites

*Datestamp: 261105.* This note records where `ParseUtils.grabElementFlag(Map<ElementEnum, ElementInfo>, String)`
has been put in place in the five object-side assemblers that mirror C's five `grab_element_flag` call sites
in `obj-init.c`, and what each one looks like now. Every file below was re-read in the turn that wrote this note,
and `./gradlew compileJava` was run the same turn.

## Build state

- `compileJava` fails with one error: `ArtifactAssembler.java:236`, "for-each not applicable to expression type",
  found `ElementInfo`.
- Nothing else fails to compile, but the tests cannot run until that is cleared.
- Tests were not run this turn.

## What C does at all five sites

- C tries the object-flag test, the kind-flag test (where the record has kind flags) and
  `grab_element_flag` on every token, with no short-circuit.
- A token is invalid only when every test fails; the loop then stops and the parser returns
  `PARSE_ERROR_INVALID_FLAG`.
- No `OF_` or `KF_` name starts with `IGNORE_` or `HATES_` in the Java enums (grep this turn), so trying the
  element test first, or last, gives the same answer on real data.

## CurseAssembler.java: done

- `CurseAssembler.java:232` declares `elInfo` before the flags loop.
- `CurseAssembler.java:237` calls `ParseUtils.grabElementFlag(elInfo, flag)`; only a `false` falls to the
  `OF_` lookup at line 241.
- The message at `CurseAssembler.java:244-245` now reads "invalid object/element flag value", which fits the
  merged path.
- The old `startsWith` / `substring` / `valueOf` block is gone.
- Nothing left over.

## EgoItemAssembler.java: wired, one leftover

- `EgoItemAssembler.java:150` declares `elInfo`; `EgoItemAssembler.java:152` calls the helper.
- The `eFlagName`, `ignore`, `hates` bookkeeping and the `ElementEnum.valueOf` try/catch are gone.
- `EgoItemAssembler.java:185` gates the `OF_`/`KF_` apply on `!eFlagFound`, and line 192 throws only when
  neither flag kind matched.
- Leftover: `EgoItemAssembler.java:159` declares `ElementEnum eFlag = ElementEnum.ELEM_NONE;` and nothing
  reads it any more.
- The `OF_`/`KF_` lookups at lines 163-183 still run when `eFlagFound` is true and their results are
  discarded. That matches C's no-short-circuit shape and is harmless.

## ItemObjectAssembler.java: wired, dead code left

- `ItemObjectAssembler.java:279` now declares `elInfo` above the flags loop.
- `ItemObjectAssembler.java:292` calls the helper as the last resort after the `OF_` and `KF_` lookups,
  and line 293 throws "unknown flag" when it returns `false`.
- Leftover: `ItemObjectAssembler.java:277-278` still declare `eIFlags` and `eHFlags`, and nothing fills
  them now.
- Leftover: `ItemObjectAssembler.java:410-418` still loop over those two empty flags and write into
  `elInfo`.
- Leftover: `ItemObjectAssembler.java:283` declares `ElementEnum eFlag` and nothing reads it.
- The `ElementInfoEnum` import is only used by the loops at lines 410-418.

## ArtifactAssembler.java: wired but does not compile

- `ArtifactAssembler.java:140` now declares `elements` as `Map<ElementEnum, ElementInfo>`, and
  `ArtifactAssembler.java:169` passes it to the helper.
- `ArtifactAssembler.java:233-240`, the merge loop, still treats `elements.get(ee)` as a collection of
  `ElementInfoEnum`. `ElementInfo` is not `Iterable`, so line 236 is the compile error.
- `elements` and `elInfo` (line 185) are now two maps of the same type, and the merge loop is a leftover of
  the old `Set<ElementInfoEnum>` shape.
- `elInfo` is declared at line 185, after the flags loop. The helper writes into `elements`, so the merge
  loop is the only thing carrying it across.
- Smaller leftovers inside the flags loop:
  - The `catch (IllegalArgumentException e1)` at `ArtifactAssembler.java:176` guards a block that no longer
    contains a call that throws it.
  - The sentinel pre-check at lines 160-168 duplicates the helper's refusal of `NONE`/`MAX`, but it gives a
    more specific message, so it changes wording rather than behaviour.

## ObjectBaseAssembler.java: not wired

- `ObjectBaseAssembler.java:166-232` is unchanged. It still has its own `HATES` branch (lines 172-188) and
  `IGNORE` branch (lines 189-205), each with a `valueOf` and sentinel check.
- The assembler fills two `Flag<ElementEnum>` values, `hatesFlag` and `ignoreFlag`
  (`ObjectBaseAssembler.java:163-164`), and passes both at line 236.
- `ObjectBase.java:164-166` takes those two flags in its constructor and builds the `elementInfo` map itself
  at `ObjectBase.java:172-180`. The helper needs the map, so the shape has to meet in the middle somewhere.
- `ObjectBase.java:195` has `setElementInfo(Map)`, which replaces the map wholesale. Its Javadoc at
  `ObjectBase.java:185-190` says nothing in `../../src/main` or `../../src/test` calls it.
- Javadoc drift in the same file: the constructor block at `ObjectBase.java:143-162` has no `@param` for
  `ignoreFlag` or `oFlags` (signature at lines 164-166), and its prose at lines 144-149 describes only the
  hated elements being folded in.

## Next step

Open `ArtifactAssembler.java:233-240` first, since it is the only thing stopping the build. Then decide the
`ObjectBase` shape (constructor flags versus `setElementInfo`) before touching `ObjectBaseAssembler.java`.
