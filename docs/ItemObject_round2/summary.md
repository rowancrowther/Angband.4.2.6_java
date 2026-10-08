# ItemObject.java second pass: summary

Written 2026-10-07 (Phase 5 of `docs/ItemObject_agent_plan.md`). Sources: the eight reports `part_A.md` to `part_H.md`
in this folder, `part_0.md`, and `docs/precis/261007.md`. What the fixes did is taken from the precis, which records
them as made on 2026-10-07; this summary did not re-read `ItemObject.java` method by method. Re-verify before relying on
any
"fixed" line below.

## What each part found

- **Part A (construction, copy, wipe, curse/brand/slay editors): clean.**
    - The 14:40 report found `copy` building `HashMap`s for `modifiers` and `elInfo`, `setCurses(null)` and
      `clearAndPutCurses(null)` emptying the item before throwing, and the full constructor accepting `null` flags and
      notice. The re-verify found all three already fixed.
    - `addCurse` and `removeCurse` are deliberate simplifications of `append_object_curse` and `remove_object_curse`,
      not ports. `object_copy_amt` waits for Chapter 8.
    - Ten Javadoc drifts (items 3 to 10) went to Stage 2; the precis records them rewritten.
- **Part B (pack order, stacking, absorb, split): clean.**
    - `objectAbsorbPartial` had no baseline, so it got a full Stage 1 and matches C clause for clause.
    - The batch 1 heading "Stage 1 FAILED" is stale; all five of its findings and the three batch 2 fixes hold.
    - Two Javadoc blocks were wrong (`nullKnown`, `objectAbsorbPartial`'s "fit `max_stack`"). The `objectSplit`
      exception text says "more than" where the legal range is below the stack size.
- **Part C (value and the power driver): clean.**
    - The curse-visiting-order divergence accepted in batch 4 has closed itself: the curse map is a `TreeMap` under
      `CURSE_ORDER`.
    - Five Javadoc statements were stale (flattened curse record, registry order, zero price for never-seen wearables).
- **Part D (power components): clean.**
    - The curse unflattening did not disturb the first pass. Three blocks linked to `Curse` getters that no longer
      exist.
- **Part E (damage power): clean.**
    - Two comment drifts: `toDamagePower(Curse)` names `getCombatDam()`, and `slayPower` says the search starts from 1
      when it is `best_power`.
- **Part F (knowledge, ignoring, flags): stopped on mismatch, then fixed.**
    - `flagMessage` returned silently only for a `null` message, but the parser holds `""` for a property with no
      `msg:`, so wielding a Wooden Torch logged empty messages where C says nothing. You made `null` and `""` alike.
    - `hasFlag` lacking `obj_has_flag`'s curse check is unreachable today, because C's only caller is `obj_can_takeoff`.
    - `EgoItem.getIgnoreType` implements `ego_is_ignored`; `ego_has_ignore_type` has no port.
- **Part G (description, messages, slots, glyphs): clean.**
    - `modMessage` had no baseline and matches. The five `objDescNameFormat` divergences are unreachable from shipped
      data; four were already in the Javadoc and the fifth (a `~` inside a kept `|x|y|` alternative) is new.
- **Part H (accessors and mutators): stopped on mismatch, then fixed.**
    - `getGrid()` answered `null` for a bare item and `getFlags()` threw on a missing set. You made them answer
      `Loc.zero` and an empty set. `setFlagsTo` got an explanation of why its wipe stays, and `setEffectMessage` got its
      block.
    - `isArtifact` is `artifact != null`, which equals neither C known-artifact test; no caller reaches the difference.

## What you fixed (per the precis)

- Part A: `copy` map types, `setCurses`/`clearAndPutCurses` null handling, full-constructor null flags and notice.
- Part F: `flagMessage` treats `null` and `""` alike.
- Part H: `getGrid()` and the flag accessors read a missing field as C's zero value.
- Also today, outside the eight parts: `Flag.copyFrom` self-copy, `Flag.andNot`, the `PlayerKnowledge` curse overload
  sentinel keys.

## What was accepted as a divergence

- `addCurse` and `removeCurse` as simplifications (Part A).
- `copy(false)` leaving `known` null; `flavourIsAware` against `objectFlavourIsAware` on a kindless item (batch lists).
- The five `objDescNameFormat` cases (Part G), pending your decision on adding them to the batch 8 accepted list.
- `hasFlag` ignoring curse flags, until `obj_can_takeoff` is ported (Part F).
- `ObjectUtils.copyCurses` with a non-null all-power-0 source leaves an empty map where C holds an allocated zero array,
  so `cursesAreEqual` says equal where `curses_are_equal` says different (Parts A, H baseline; accepted 2026-10-07,
  marked in the `copyCurses` Javadoc).

## Out-of-scope observations, all parts

Shape follows `docs/ItemObject_stage1_mismatches.md`. File and line references are as the part reports read them on
2026-10-07 and shift as you edit.

### 1. `applyCurseAttributes` compares the held-back curse by identity (Part C)

- **Where:** `ItemObject.java:4849`, private, one caller (`cursePower`).
- **What differs:** `curse == curseToIgnore`, where the neighbouring lookups go through `CURSE_ORDER`.
- **Reach:** none in production, since `ObjectUtils.copyCurses` draws from the registry. A fixture or a reloaded
  registry would make the "all but c" copy equal the "all curses" copy and silently give `powerCurse` 0.
- **Result:** FIXED.

### 2. The `Curse` overloads are a hand-reduced `objectPower` (Parts C, D, E)

- The parts found `objectPower(Curse ...)`, `nonStandardWeightPower(Curse ...)`, `cursePower(Curse ...)` and the Part D
  and E curse overloads standing in for calling `objectPower` on `Curse.getItemObject()`, as C does. Each
  identity-overload justification was correct but would drift if `curse.txt` gained a base armour, a brand or a kind.
- **Result:** FIXED. Re-read on 2026-10-07: `ItemObject.java` has no `Curse` overloads of the power methods.
  `cursePower` calls `c.getItemObject().objectPower(verbose, logFileName)` directly, and `nonStandardWeightPower` takes
  only the running power.

### 3. C line numbers in Javadoc (Parts B, D, E, G)

- `ObjectRegistry.java` (about `:188`, `:198`, `:205`, `:328`), `FlagSet.java:24`, `ElementSet.java:24`,
  `ElementPowers.java:26` and `:40`, `Archery.java:24`, `ObjectKind.java:1222`, `PlayerBody.java:191`.
- The house rule says name the C file only. Strip them when you next touch each file.
- **Result:** FIXED.

### 4. `ObjectUtils.copyCurses` with all-power-0 source (Parts A, H baseline)

- Leaves an empty map where C holds an allocated all-zero array, and `curses_are_equal` treats NULL and all-zero as
  different. Hand-built sources only.
- **Result:** ACCEPTED 2026-10-07; see the accepted list above and the `copyCurses` Javadoc.

### 5. `cursesFactory` comparator with a `null` curse name and a tied index (Part A)

- Throws. Hand-built curses only.
- **Result:** FIXED.

### 6. Curse editors with no production caller (Part A)

- `initCurses`, `setCursePower`, `addCurses`, `clearAndPutCurses` and the `(Curse, int, int)` `addCurse`.
- `ObjectMake.java` has no curse code, so C's `apply_curse` has no counterpart yet.
- **Result:** Callers are to be written in Chapter 8

### 7. `appendBrand` and `appendSlay` lack C's replace-if-stronger rule (Part E)

- Dedupe lives in `ObjectUtils.copyBrands` and `copySlays`. The two outside callers (`PlayerKnowledge.java:286`, `:301`)
  fill a known object, so the power calculation cannot reach it.
- **Result:** FIXED.

### 8. `activation` has no setter and nothing assigns it (Parts D, H)

- `effectsPower` falls back to the kind's power for every generated item until generation copies `art->activation`,
  `kind->activation` and `ego->activation`. Part H adds that `activation` is a `List<Activation>` where C holds one
  pointer, and that `artifact`, `originDepth`, `originRace` and the origin have no getters or setters.
- No caller has worked around a missing getter.
- **Result:** Callers are to be written in Chapter 7

### 9. `object_to_hit`, `object_to_dam`, `object_to_ac`, `object_effect` have no Java counterpart (Part H)

- Every current caller wants the bare field. The first caller that doesn't is `object_desc` (Chapter 7), then the attack
  code, `obj-info.c` and `use_aux`.
- The port must work on the known half as well, where it reads `obj->known->curses`.
- **Result:** To be written in Chapter 7

### 10. `ego_has_ignore_type` is not ported (Part F)

- Its only C caller is the ego-ignore menu in `ui-options.c`.

### 11. `isArtifact` against C's known-artifact tests (Part H)

- No production caller reaches the difference. The sites that do (`object_touch`, `mon-make.c`, `generate.c`,
  `obj-desc.c`, `compare_items`) are unported, and `ItemObject` has no artifact setter, so `known.isArtifact()` is false
  for every known half production code builds.

### 12. `Message.message` has no `if (!messages) return;` (Part G)

- `modMessage` would log where C stays silent before the message store loads. Not reachable in play.

### 13. Dead and redundant code (Parts C, D, E, G)

- The `getModifiers() == null` guard at `modifierPower`, since `getModifiers()` answers `Map.of()`. The guards in the
  `Curse` overloads of `extraBlowsPower`, `extraShotsPower`, `extraMightPower` and `damageDicePower` went with the
  overloads (item 2).
- The re-sort of curse indices in `objectWeightOne`, now that the map is a `TreeMap`. Not re-checked in
  `applyCurseAttributes`.
- Gone, re-read 2026-10-07: the copied "Add damage from dice" comment in `damageDicePower(Curse)` (the plain
  `damageDicePower` keeps its own, which is right) and the `{@link}` to private `objectKindChar()` and
  `objectKindAttr()`.

### 14. Visibility to widen later (Parts C, G)

- `objectValue` is private where C's is public (needed by Chapter 8). `objectKindName` and `objDescNameFormat` have no
  caller for the unaware-flavour, `pluralise` and `modString` paths until Chapter 7.

### 15. Stale test comments (Part A)

- `ItemObjectCursesTest.java` says the curse map starts `null` and repeats the `append_object_curse` zero-timeout claim;
  `ItemObjectWipeTest.java` names insertion order for the wiped maps.

### 16. Stale docs (Part B)

- The batch 1 heading in `docs/ItemObject_port_batches.md` still says FAILED, and its C sources line files
  `object_origin_combine` and `distribute_charges` under the wrong C files.

### 17. `effectMessage` and `heldMIndex` are written but barely read (Part H)

- Only `copy` reads them. `ObjectUtils.doCurseEffect` (a stub) will be the first real reader of `effectMessage`.

## Call to action

- Tick `ItemObject` on `docs/ROADMAP.md` when you are satisfied. That tick is yours.
- Decide whether the five `objDescNameFormat` divergences join the batch 8 accepted list.
- Fix the stale batch 1 heading in `docs/ItemObject_port_batches.md` (item 16).
- Say "try that" if you want Parts F and H re-verified from scratch; their reports still say "stopped on mismatch".
