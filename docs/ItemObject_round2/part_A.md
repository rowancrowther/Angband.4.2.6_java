# Part A: fields, construction, copy and wipe (Stage 1, round 2 re-verify)

Re-run 2026-10-07 after your edits to `ItemObject.java` (the 14:40 report is replaced by this one). Read-only. Every
Java declaration and C function named below was read in full this turn. Java: `ItemObject.java` (class and field
Javadoc, both constructors, `cursesFactory`, `copy`, `wipe`, `initCurses`,
`freeCurses`, `freeSlays`, `freeBrands`, `getCurses`, `setCurses`, both `addCurse`, `addCurses`, `clearAndPutCurses`,
`clearCurses`, `setCursePower`, `removeCurse`, `addBrand`, `removeBrand`, `clearBrands`, `getBrands`, `addSlay`,
`removeSlay`, `clearSlays`, `getSlays`, `setSlays`, `getModifiers`, `getElInfo`, `setKind`). Also read: `Flag.copyFrom`
and
`Flag.union` (`Flag.java`), `ElementInfo.copy` (`ElementInfo.java`), `Random.copy` (`Random.java`), `CurseData.java`,
`ObjectUtils.copyCurses` (`ObjectUtils.java`). C: `object_new`, `object_wipe`, `object_copy`, `object_copy_amt`
(`obj-pile.c`); `copy_curses`, `append_object_curse`,
`remove_object_curse`, `curses_conflict`, `check_object_curses`, `curses_are_equal` (`obj-curse.c`); `struct object`
(`object.h`); `curse_power` (`obj-power.c`, for the three `free*` helpers' call pattern).

## What changed since the 14:40 report

- **1. `copy` map type: fixed.** `ItemObject.java:6630` and `ItemObject.java:6635` now build `LinkedHashMap`s for
  `modifiers` and `elInfo`.
- **2. `setCurses(null)` and `clearAndPutCurses(null)`: fixed.** `ItemObject.java:1775-1776` and
  `ItemObject.java:1878-1879` return before touching the field, as C's `copy_curses` does with `if (!source) return;`.
- **Full constructor `null` flags and notice: fixed.** `ItemObject.java:1013` and `ItemObject.java:1028` substitute an
  empty set.
- **3 to 7, the Javadoc drift, are all still there.** They are Stage 2 work and are listed under "Stage 2 list" below.

## What matches

- Field coverage of `struct object` is complete. Each of the 34 data members (everything but `prev`, `next`, `oidx`) has
  a field, is assigned by `copy` (`ItemObject.java:6596-6678`) and is reset by `wipe` (`ItemObject.java:7056-7093`).
  `prev`/`next` are stood in for by `owningPile`, which `copy` leaves at the constructor's `null` and `wipe` resets, as
  C's `object_copy` ("Detach from any pile") and `memset` do. `oidx` has no field.

- `ItemObject()` (`ItemObject.java:851`) is `object_new`: primitives 0, `origin` `ORIGIN_NONE`, `tValue` `TV_NONE`,
  `time`
  a zero `Random` (`ItemObject.java:864`), the collections empty where C's are NULL, `curses` from `cursesFactory()`.

- `copy` reproduces `object_copy`. Walked: curses `{A: (50, 3), B: (20, 7)}` come out as fresh `CurseData`, and mutating
  the copy's A leaves the source's timeout at 3 (C: the `memcpy`'d array is independent). `curses == null` stays `null`
  (C: `dest->curses` keeps the `memcpy` of NULL). `brands` and `slays` follow the same rule. `location` null stays null,
  and `(3, 4)` becomes a `Loc` copy. `effect` and `activation` are shared (C copies the pointer). `time` is copied via
  `Random.copy()`.

- `wipe` reproduces `object_wipe`: the old collections are dropped, every other field lands on zero, `known` is nulled
  without touching the counterpart, `player` is left alone (Java-only snapshot).

- `initCurses` (`ItemObject.java:7114`) is the allocation branch of `copy_curses` without its `if (!obj->curses)` guard;
  a known simplification. `ObjectUtils.copyCurses` (`ObjectUtils.java:1234`) no longer calls it, skips a power-0 source
  entry as `if (!source[i]) continue;` does, and returns early on a `null` source.

- `freeCurses`, `freeBrands`, `freeSlays` match their use in `curse_power`: the call order at
  `ItemObject.java:4610-4613` and `ItemObject.java:4639-4642` is curses, `objectPower`, brands, slays, as in C.

- The curse editors (`getCurses`, `setCurses`, both `addCurse`, `addCurses`, `clearAndPutCurses`, `clearCurses`,
  `setCursePower`, `removeCurse`) and the brand and slay editors behave as the 14:40 walk found: absence stands for
  power zero, an empty map for C's NULL array, the sets for the `bool` arrays. `setCurses(getCurses())` is safe because
  the field is reassigned before `putAll` reads the old map through the view.

- Full constructor (`ItemObject.java:976`): `pValue` `""` is 0, `time` `""` is a zero `Random`, `curses` is copied into
  a fresh `cursesFactory()` map (`ItemObject.java:1018-1019`), `flags` and `notice` are never `null`.

- `object_copy_amt` is still unported: the only mention in `src/main/java` is the `copy` Javadoc. Its C callers are
  `store.c` and `ui-store.c` (Chapter 8). Not a mismatch.

## What does not match

- Nothing in the code. Verdict: clean.

## Stage 2 list (Javadoc drift, still present)

- **3.** `curses` described as an insertion-ordered `LinkedHashMap`, where `cursesFactory()` builds a `TreeMap` by
  `CURSE_ORDER`: no-arg constructor (`ItemObject.java:833-835`), `setCurses` (`ItemObject.java:1760`),
  `clearAndPutCurses` (`ItemObject.java:1865`), `initCurses` (`ItemObject.java:7108`).
- **4.** No-arg constructor lists `time` among the fields that stay `null` (`ItemObject.java:837-841`); the code assigns
  `Random.Zero()` (`ItemObject.java:864`).
- **5.** Full constructor says `curses` is stored by reference (`ItemObject.java:919-925`); the code copies the map
  (`ItemObject.java:1018-1019`) and replaces a `null` `flags` or `notice`.
- **6.** `setCursePower` says `append_object_curse` writes a zero timeout (`ItemObject.java:1911-1915`); it rolls
  `randcalc(c->obj->time, 0, RANDOMISE)`. Only the `obj-knowledge.c` write leaves the timeout at zero.
- **7.** `copy` and the `effect` field do not say `effect` is shared (`ItemObject.java:6568-6575`,
  `ItemObject.java:620-630`).
- **8 (new).** `setCurses` Javadoc still says a `null` argument throws (`ItemObject.java:1767`); it is now ignored.
- **9 (new).** `wipe` Javadoc says "Two fields come back as `null`" and then "not among the three"
  (`ItemObject.java:7038-7041`).
- **10 (new).** `addCurses` throws `NullPointerException` for a `null` argument: the link that is `null` is the
  parameter `curses`, not the field `this.curses`, which the method creates on demand (`ItemObject.java:1851-1856`). C
  has no counterpart (the nearest, `copy_curses`, returns early), and the sibling `setCurses` now ignores `null`, so the
  Javadoc should say so. Recorded, not a mismatch.

## Out-of-scope observations

- `ItemObjectCursesTest.java:51-53` and `ItemObjectCursesTest.java:80-83` still say the curse map starts `null`, where
  the no-arg constructor now builds an empty one. `ItemObjectCursesTest.java:232` repeats the `append_object_curse`
  zero-timeout claim from item 6. `ItemObjectWipeTest.java:314` and `ItemObjectWipeTest.java:326` name insertion order
  for the wiped maps, which is still true for `modifiers` and `elInfo`.
- `ObjectUtils.copyCurses` with a source whose entries are all power 0 leaves an empty map where C holds an allocated
  all-zero array (`curses_are_equal` treats NULL and all-zero as different). Only a hand-built source reaches it.
- `ItemObject.cursesFactory` (`ItemObject.java:1052`) would throw from its comparator for a `Curse` with a `null` name
  and a tied index. Hand-built curses only.
- `ItemObject.initCurses`, `setCursePower`, `addCurses`, `clearAndPutCurses` and the `(Curse, int, int)` `addCurse` have
  no production caller; `ObjectMake.java` has no curse code, so C's `apply_curse` (the main caller of
  `append_object_curse`) has no Java counterpart yet.

## Plan 'Look for' answers

- **`object_copy_amt`:** not ported, waits for Chapter 8.
- **`copy` and `wipe` against the field list:** agree, all 34 data members plus the Java-only `baseDamage`,
  `owningPile`.
- **`addCurse` and `removeCurse` against `append_object_curse` and `remove_object_curse`:** deliberate simplifications,
  not ports. `addCurse(A, 50, 9)` over A at 70 replaces it, where C refuses unless `power > existing`; Java never rolls
  a timeout and never runs `curses_conflict` or the TIMED_INC and `conflict_flags` rejection tests; `removeCurse`
  returns nothing and prints nothing, and removes a power-0 entry where C returns false. `check_object_curses` is
  present in effect (empty map for NULL array), except a map of only power-0 entries put there by `addCurse`.

clean
