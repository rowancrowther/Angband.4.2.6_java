# ItemObject.java: plan for a parallel agent pass

Written 2026-10-07. This plan splits `ItemObject.java` into eight parts and runs one agent on each. It is a plan only;
nothing in it has been run.

## Read this first

- `docs/ItemObject_port_batches.md` records nine batches run on 2026-10-02, so most of `ItemObject` has been through
  `/port` once already.

- `ItemObject.java` is now 7,381 lines, where that doc says about 5,600, so a good share of the file is newer than those
  batches.

- `objectAbsorbPartial` and `modMessage` are not in the batches doc at all, and `Flag.copyFrom` and `Flag.andNot`
  changed today in code the batches covered. The curse unflattening work may also postdate them.

- So this is a second pass over the whole file, with the batches doc as the baseline each agent starts from. It is not a
  from-scratch port.

- You still write every fix to `src/main/**`. The agents verify, document, test and report.

## Rules every agent works under

- **Read-only on `src/main/**` code.** An agent may change Javadoc and comments only, and only in Stage 2, and only
  through the Javadoc-edit confirmation prompt.

- **Stage 1 stops on a mismatch.** An agent that finds one writes the finding and does nothing further for that part.

- **No `git`**, not even read-only.

- **Re-read before reporting.** Each agent reads the Java methods and the C functions in its own turn, never from the
  batches doc alone. The doc says what was found on 2026-10-02, not what is true now.

- **Names the file on every reference**, with `File.java:line`, and no C line numbers anywhere.

- **Stub calls are not mismatches.** A call into a later-chapter stub is not a Stage 1 finding; `description` is the
  known case.

- **Known divergences are not findings.** Each part's agent is handed that part's "Accepted divergences" from the
  batches doc, and does not re-raise them. Anything new goes under "Out-of-scope observations".

## The eight parts

Parts are named by method, not line number, because line numbers move as you edit. The C files are the ones to read;
`/home/rowan/Desktop/Angband-4.2.6/src/` is the root.

### Part A: fields, construction, copy and wipe

- **Java:** both constructors, `cursesFactory`, `copy`, `wipe`, `initCurses`, `freeCurses`, `freeSlays`, `freeBrands`,
  the curse getters and editors (`getCurses`, `setCurses`, `addCurse`, `addCurses`, `clearAndPutCurses`, `clearCurses`,
  `setCursePower`, `removeCurse`), and the brand and slay methods (`addBrand`, `removeBrand`, `clearBrands`,
  `getBrands`, `addSlay`, `removeSlay`, `clearSlays`, `getSlays`, `setSlays`).

- **C:** `object_new`, `object_wipe`, `object_copy` (`obj-pile.c`), `copy_curses`, `append_object_curse`,
  `remove_object_curse` (`obj-curse.c`), `struct object` (`object.h`).

- **Baseline:** batch 8 (`wipe`, `initCurses`) and batch 9.

- **Look for:** `object_copy_amt`, which batch 2 deferred to Chapter 8, and whether `copy` and `wipe` still agree with
  the current field list.

- **Decide:** `append_object_curse` and `remove_object_curse` are unstamped, and the Javadoc on `addCurse` and
  `removeCurse` cites plain array assignment rather than them. The agent reports whether those two methods are ports or
  deliberate simplifications, and whether C's `curses_conflict` and `check_object_curses` logic is missing from them.

### Part B: pack order, stacking, absorb, split

- **Java:** `earlierObject`, `similar`, `checkElementStacking`, `cursesAreEqual`, `originCombine`, `distributeCharges`,
  `mergeable`, `objectStackable`, `objectAbsorb`, `objectAbsorbMerge`, `objectAbsorbPartial`, `objectSplit`,
  `nullKnown`, `objectIsInQuiver`.

- **C:** `earlier_object` (`player-calcs.c`), `object_similar`, `object_stackable`, `object_mergeable`,
  `object_origin_combine`, `object_absorb_partial`, `object_absorb`, `object_split` (`obj-pile.c`), `curses_are_equal`
  (`obj-curse.c`), `distribute_charges` (`obj-util.c`), `object_is_in_quiver` (`obj-gear.c`).

- **Baseline:** batches 1 and 2.

- **Look for:** `objectAbsorbPartial` is the one method here with no baseline at all, so it gets a full Stage 1 of its
  own. `cursesAreEqual` and `similar` read the curse map, and the curse unflattening work
  (`docs/Curse_object_unflattening.md`) may postdate the baseline, so the agent re-checks both against the code.

- **Header caveat:** the batch 1 heading in the batches doc still says Stage 1 failed. The agent should re-check each of
  its findings against the code rather than trust that heading.

### Part C: value and the power driver

- **Java:** `objectValue`, `objectValueBase`, `objectValueReal`, both `objectPower` overloads, both
  `nonStandardWeightPower` overloads, both `cursePower` overloads, `applyCurseAttributes`.

- **C:** `object_value`, `object_value_base`, `object_value_real`, `object_power`, `nonstandard_weight_power`,
  `curse_power` (`obj-power.c`), `apply_curse_attributes` (`obj-curse.c`).

- **Baseline:** batches 3 and 4.

### Part D: power components

- **Java:** the plain and `Curse` overloads of `effectsPower`, `elementPower`, `flagsPower`, `modifierPower`,
  `jewelleryPower`, `toAcPower`, `acPower`, `toHitPower`, `rescaleBowPower`.

- **C:** `effects_power`, `element_power`, `flags_power`, `modifier_power`, `jewelry_power`, `to_ac_power`, `ac_power`,
  `to_hit_power`, `rescale_bow_power` (`obj-power.c`).

- **Baseline:** batch 5.

### Part E: damage power

- **Java:** the plain and `Curse` overloads of `slayPower`, `extraMightPower`, `extraShotsPower`, `extraBlowsPower`,
  `launcherAmmoDamagePower`, `bowMulitplier`, `ammoDamagePower`, `damageDicePower`, `toDamagePower`, and the
  `PowerAndMult` record.

- **C:** `slay_power`, `extra_might_power`, `extra_shots_power`, `extra_blows_power`, `launcher_ammo_damage_power`,
  `bow_multiplier`, `ammo_damage_power`, `damage_dice_power`, `to_damage_power` (`obj-power.c`).

- **Baseline:** batch 6.

### Part F: knowledge, ignoring and flags

- **Java:** `isKnown`, `isFullyKnown`, `runesKnown`, `effectIsKnown`, `hasStandardToH`, `hasFlag`, `flagMessage`,
  `objectFlags`, `flagsKnown`, `easyKnow`, `flavourIsAware`, `objectFlavourIsAware`,
  `getIgnoreTypeOf`, `isEgo`, `egoIsIgnored`, `ignoreLevelOf`, `isGood`, `compareObjectTrait`, `rechargeTimeout`,
  `numberCharging`, `checkForInscription`, `verifyObject`, `objectWeightOne`.

- **C:** `obj-knowledge.c`, `obj-ignore.c` (including `ego_has_ignore_type`), `obj-properties.c` (`flag_message`),
  `obj-util.c` (`obj_has_flag`, `object_flags`, `object_flags_known`, `recharge_timeout`, `number_charging`,
  `check_for_inscrip`, `verify_object`, `object_weight_one`).

- **Baseline:** batch 7, with `isFullyKnown`, `runesKnown` and `effectIsKnown` from batch 1 and the last four methods
  from batch 2.

- **Look for:** `Flag.andNot` and the `Flag.copyFrom` self-copy fix both landed after the baseline. The agent re-reads
  every `Flag` call in this part rather than assuming the old semantics.

- **Look for:** `hasFlag` is only `flags.has(flag)`, where C's `obj_has_flag` also checks every curse's object flags.
  The agent reports whether that is a mismatch the callers can reach.

- **Look for:** whether `EgoItem.getIgnoreType` implements `ego_is_ignored` or `ego_has_ignore_type`, and how
  `flavourIsAware` (false on a null kind) differs from `objectFlavourIsAware` (throws).

### Part G: description, messages, slots and glyphs

- **Java:** `description`, `printCustomMessage`, `modMessage`, `objectKindName`, `objDescNameFormat`, `wieldSlot`,
  `canBrowse`, `objectKindChar`, `objectKindAttr`, `useFlavourGlyph`, `getItemObjectADC`.

- **C:** `obj-desc.c`, `print_custom_message` and `obj_can_browse` (`obj-util.c`), `wield_slot` (`obj-gear.c`),
  `mod_message` (`obj-knowledge.c`), `object_attr`, `object_char`, `object_kind_char`, `object_kind_attr` and
  `use_flavor_glyph` (`ui-object.c`). `getItemObjectADC` is the port of `object_attr` and `object_char`, which only
  forward to the kind versions.

- **Baseline:** batch 8, plus `modMessage`, which has none.

- **Not verifiable:** `description` is a stub until Chapter 7, so the agent notes it and moves on.

### Part H: accessors and mutators

- **Java, 65 declarations** (Phase 0 counted 187 members in the file; the other seven parts hold the rest):
    - Position, kind and counts: `getGrid`, `setGrid`, `getKind`, `setKind`, `getNumber`, `setNumber`, `gettValue`,
      `settValue`, `getTimeout`, `setTimeout`, `getWeight`, `setWeight`, `getNote`, `setNote`, `getTime`, `setTime`.
    - Combat and value fields: `getToHit`, `setToHit`, `getToDam`, `setToDam`, `getToAC`, `setToAC`, `getBaseAC`,
      `setBaseAC`, `getDamageDice`, `setDamageDice`, `getDamageSides`, `setDamageSides`, `getsValue`, `setsValue`,
      `getpValue`, `setpValue`.
    - Links and effect: `getKnown`, `setKnown`, `getEgo`, `setEgo`, `getEffect`, `setEffect`, `setEffectMessage`,
      `getOwningPile`, `setOwningPile`, `getMimickingMIndex`, `setMimickingMIndex`, `setHeldMIndex`, `setOrigin`.
    - Maps: `getModifiers`, `setModifiers`, `putModifier`, `getElInfo`, `setElInfo`, `putElInfo`, `setElInfoResLevel`,
      both `getModifierValue` overloads.
    - Flags and notices, which read or build a `Flag`: `getFlags`, `setFlags`, `setFlag`, `setFlagsTo`,
      `getObjectFlags`,
      `getNotice`, `getNoticeHas`, `setNoticeOn`, `setNoticeOff`, `orNotice`.
    - Predicate: `isArtifact`.

- **C:** `struct object` and its supporting enums in `object.h`. There is no function to compare for the plain
  accessors; the four below are the exceptions.

- **Baseline:** batch 9.

- **Look for:** the batch 9 list of getters and setters that do not exist yet (artifact, effect message, activation,
  origin depth, origin race, holding monster, `setBrands`). The agent re-checks that list against the file and against
  what callers now need, and reports a getter that a caller has since had to work around.

- **Look for:** C's `object_to_hit`, `object_to_dam` and `object_to_ac` (`obj-util.c`) add each active curse's bonus to
  the object's own, and `object_effect` prefers the activation's effect. `getToHit`, `getToDam`, `getToAC` and
  `getEffect` return the bare fields, and no Java counterpart to the four C functions exists in `src/main/java`. The
  agent reports where callers need them.

- **Look for:** `isArtifact` checks only `artifact != null`, where C's known-artifact tests (`object_is_known_artifact`
  and `obj_is_known_artifact`, named in `docs/ItemObject_round2/part_0.md`) also require the known half.

- **Flag re-read:** the flag and notice accessors call `Flag.copyFrom`, which changed on 2026-10-07. The agent re-reads
  those calls the way Part F's does.

## Phases

### Phase 0: partition check (one agent, read-only)

- Takes the method list from `ItemObject.java` and the eight parts above and reports any method that is in no part, and
  any in two.

- Takes the function list for `struct object` from `obj-pile.c`, `obj-util.c`, `obj-knowledge.c` and `obj-gear.c` and
  reports any C function with a Java counterpart in `ItemObject` that no part names.

- Output: `docs/ItemObject_round2/part_0.md`. Fix the table above before Phase 1 if it finds anything.

- **Done 2026-10-07.** It found 54 of 187 members in no part, two misfiled methods and six C functions no part named;
  the part sections above now carry those corrections, and Parts F and H also carry its list of C functions with no Java
  counterpart at all. New counts: A 27, B 14, C 10, D 18, E 19, F 23, G 11, H 65, total
    187. It found no method named twice and no named method missing.

### Phase 1: Stage 1, eight agents in parallel (all read-only)

- One agent per part, each given its section above, the rules above, and the matching batches-doc sections.

- Each writes its findings to `docs/ItemObject_round2/part_X.md` in the shape the `/port` skill reports: what matches,
  what does not, and out-of-scope observations, each with `File.java:line`.

- Nothing here edits a file under `src/`, so there is nothing to collide, and the agents can all run at once.

- Each file ends with one line: "clean" or "stopped on mismatch".

### Phase 2: your fixes

- You read the eight reports and fix what is yours to fix. Parts that came back "clean" skip straight to Phase 3.

- Parts that stopped come back for a re-verify, one at a time, by saying "try that". Same-day findings expire, so each
  re-verify re-reads the part from scratch.

### Phase 3: Stages 2 and 3, split by what can overlap

- **Stage 3 (tests) can run in parallel.** Every part writes to its own test class under `src/test/**`, so eight agents
  cannot collide there. Expected values come from the C source, never from the Java, and each agent runs only its own
  test class.

- **Stage 2 (Javadoc) runs one part at a time.** All eight parts edit the same 7,000-line file, and two agents editing
  one file at once can clobber each other's blocks. Each Javadoc pass also goes through your confirmation prompt, so
  running them in series costs nothing extra.

- **Order:** run Stage 2 for a part before its Stage 3 agent starts, because the tests cite the Javadoc's C references.
  In practice: Stage 2 for A, B, C and so on in turn, with each part's Stage 3 agent started as that part's Javadoc
  lands.

- **Full suite after each part**, run by whoever is working, not by the parallel agents, so a red result is traced to
  the part that caused it.

### Phase 4: precis and C stamps, serial

- **Precis:** one agent, or you, writes one `###` point per part into that day's precis, in the house shape.

- **C stamps:** several parts share a C file (`obj-pile.c` in A and B, `obj-util.c` in A, B, F and G, `obj-power.c` in
  C, D and E), so stamping runs one file at a time, never one agent per part.

- **Already stamped:** most of these functions carried `/* Ported to Java 2026-08-30 */` before the first pass. The
  stamping agent warns and leaves those alone, and lists everything it stamped.

### Phase 5: consolidation

- One agent reads the eight reports and the precis and writes `docs/ItemObject_round2/summary.md`: what each part found,
  what you fixed, what was accepted as a divergence.

- It also lists the new out-of-scope observations across all parts in one place, in the shape of
  `docs/ItemObject_stage1_mismatches.md`.

- Ticking `ItemObject` on `docs/ROADMAP.md` is yours.

## Brief to hand each agent

> You are verifying part X of `ItemObject.java` against the Angband 4.2.6 C source. Read CLAUDE.md first and follow its
> working rules. Read the Java methods listed for this part, in full, in this turn. Read the C functions listed, in
> full. Compare clause by clause: control flow, ordering, early returns, flag semantics, bounds, integer division and
> rounding, side effects on shared state. Walk the interesting values through both versions. Do not edit any file under
> `src/main/**`. Do not run `git`. Do not re-raise the accepted divergences listed for this part. Report what matches
> and what does not, with `File.java:line` on every reference and no C line numbers. If anything does not match, stop
> after reporting. Write your report to `docs/ItemObject_round2/part_X.md` and finish with "clean" or "stopped on
> mismatch".

## Things this plan does not decide

- Whether Phase 3's Stage 3 agents run alongside Stage 2, or wait for it. The plan waits, which is slower and safer.

- Whether Part H earns a full agent. Its C side is a struct, so most of its weight is Javadoc, and you may prefer to
  fold it into Part A.

- Whether to run Phase 1 at all for parts whose code has not changed since 2026-10-02. You know which those are; the
  agents do not.
