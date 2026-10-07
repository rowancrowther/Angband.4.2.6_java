# Phase 0: partition check for ItemObject.java

Run 2026-10-07. Read-only. `ItemObject.java` and the C files were read or grepped this turn; every line number below is
from that read.

## What was counted

- `src/main/java/uk/co/jackoftradesltd/middle/objects/ItemObject.java` is 7,381 lines.

- It declares 187 things at member level: 2 constructors, 184 methods, and 1 nested type (`PowerAndMult`, a private
  record at
  `ItemObject.java:7380`). There are no other nested types and no static or instance initialisers.

- Multi-line and annotated signatures were checked separately. Two signatures wrap across lines and a plain
  "ends in `{`" grep misses them: the second constructor at `ItemObject.java:976` and `objectAbsorbPartial` at
  `ItemObject.java:4727`. Annotated methods (`@CheckReturnValue`, `@Contract`, `@NotNull`) were all picked up.

- Every other member-level line is a field (checked: 40 fields, none missed) or an annotation line.

- Constructors: `ItemObject()` at `ItemObject.java:851` and `ItemObject(ObjectKind, EgoItem, ...)` at
  `ItemObject.java:976`. The plan's "both constructors" covers both.

## (a) Java members in no part

- 54 declarations are named by no part. All are accessors or small mutators; none is a C-derived algorithm.

- Plain getters and setters: `getGrid` (`ItemObject.java:1171`), `setGrid`, `getKind`, `setKind`, `getNumber`,
  `setNumber`,
  `gettValue`, `settValue`, `getTimeout`, `setTimeout`, `getToHit`, `getToDam`, `getToAC`, `setToHit`, `setToDam`,
  `setToAC`,
  `getFlags` (`ItemObject.java:2377`), `getKnown`, `getNotice` (`ItemObject.java:2442`), `getTime`, `setTime`,
  `getNote`,
  `setNote`, `getsValue`, `setsValue`, `getWeight`, `setWeight`, `getDamageDice`, `setDamageDice`, `getDamageSides`,
  `setDamageSides`, `getBaseAC`, `setBaseAC`, `getpValue`, `setpValue`, `getModifiers`, `setModifiers`, `getElInfo`,
  `setElInfo`, `getEffect`, `setEffect`, `getEgo`, `setEgo`.

- Flag mutators: `setFlags` (`ItemObject.java:3048`), `setFlag` (`ItemObject.java:3066`), `setFlagsTo`
  (`ItemObject.java:3085`).

- Map editors: `putElInfo` (`ItemObject.java:2903`), `putModifier` (`ItemObject.java:2924`), `setElInfoResLevel`
  (`ItemObject.java:2871`).

- Modifier lookup, two overloads: `getModifierValue(Stats)` (`ItemObject.java:3308`) and
  `getModifierValue(ObjectModifier)`
  (`ItemObject.java:3328`).

- Brand and slay accessors: `getBrands` (`ItemObject.java:3839`), `getSlays` (`ItemObject.java:3870`), `setSlays`
  (`ItemObject.java:3000`). The plan's Part A says "the brand and slay editors", which names no methods; the editors
  that exist are `addBrand`, `removeBrand`, `clearBrands`, `addSlay`, `removeSlay`, `clearSlays` (`ItemObject.java:3106`
  to
  `ItemObject.java:3187`). There is no `setBrands` (the plan's own Part H list says so too).

## (b) Java members named in more than one part

- None. No method name appears in two parts, and the nine "both overloads" families (D and E) stay in one part each.

- Overload note: one name covers several declarations in these families, so a part's "method count" below counts
  declarations, not names. `addCurse` has two overloads (`ItemObject.java:1797` with `(Curse, int, int)` and
  `ItemObject.java:1824` with `(Curse, CurseData)`), both in Part A.

- Judgement call, not a double-listing: the plan puts `getFlags`-style Flag handling under F ("re-reads every `Flag`
  call")
  but the Flag-touching accessors `getFlags`, `getNotice`, `setFlags`, `setFlag`, `setFlagsTo` fall to H by default. See
  the table at the end.

## (c) Names in the plan that do not exist in ItemObject.java

- None. Every method name in Parts A to H exists in `ItemObject.java`.

- `bowMulitplier` (Part E) is spelled that way in the plan and in the code (`ItemObject.java:6203`,
  `ItemObject.java:6226`). It is a typo of "bowMultiplier" in the Java; the plan matches the file, so nothing breaks,
  but a search for the correct spelling finds nothing.

- `objectAbsorbPartial` does exist (`ItemObject.java:4727`), sitting between `freeBrands` and `applyCurseAttributes`.

## (d) Per-part declaration counts as the plan stands

- Part A: 24 (2 constructors, `cursesFactory`, `copy`, `wipe`, `initCurses`, `freeCurses`, `freeSlays`, `freeBrands`, 9
  curse editors/getters counting both `addCurse` overloads, and the 6 brand/slay editors).

- Part B: 14.

- Part C: 10.

- Part D: 18.

- Part E: 19 (18 methods plus the `PowerAndMult` record).

- Part F: 24.

- Part G: 12.

- Part H: 12 (the explicit names only).

- Named in a part: 133. Named in none: 54. Total 187.

## Accessor classification

- Part H's "every plain getter and setter" absorbs 51 of the 54 unnamed items: everything in (a) except `getBrands`,
  `getSlays` and `setSlays`.

- Those three go to Part A, with the other brand and slay editors, because they share the brand/slay `Set` fields and
  the null-safe `Set.of()` handling (`ItemObject.java:3839`, `ItemObject.java:3870`).

- Two named items are misfiled and should move to H:

    - `setEffectMessage` (`ItemObject.java:7358`) is in Part G but is a one-line field setter with no C function behind
      it.

    - `getObjectFlags` (`ItemObject.java:6837`) is in Part F but returns the raw `flags` field. C's `object_flags` is
      ported by `objectFlags(Flag)` (`ItemObject.java:2407`), not by this getter.

- Wrinkles H's agent should know: `getTime` (`ItemObject.java:2468`) returns a copy; `getModifiers` and `getElInfo`
  return
  `Map.of()` when null; `getFlags` and `getNotice` build a new `Flag` through `copyFrom`, which changed today.

- Staying where the plan has them: `isKnown` (`ItemObject.java:2179`, `known != null`), `isEgo` (`ItemObject.java:3517`)
  and
  `isArtifact` (`ItemObject.java:1216`) are one-line predicates too, but the plan already places the first two in F and
  the third in H. Left alone.

## C side: counterparts no part names

- Method: all C functions in the eleven files were listed, then checked against the `/* Ported to Java ... */` stamps
  and against the Java by name search across `src/main/java`.

- `object_attr` and `object_char` (`ui-object.c`): the Java counterpart is `getItemObjectADC` (`ItemObject.java:7193`),
  which calls `objectKindChar` and `objectKindAttr`. Part G names only the `object_kind_*` pair. C's two just forward to
  the kind versions. Add them to Part G's C list.

- `append_object_curse` and `remove_object_curse` (`obj-curse.c`): Part A's C list names only `copy_curses`, yet
  `addCurse` (`ItemObject.java:1797`, `ItemObject.java:1824`), `setCursePower` (`ItemObject.java:1921`) and
  `removeCurse`
  (`ItemObject.java:1959`) are the nearest Java. Neither C function is stamped and the Javadoc on `addCurse` and
  `removeCurse` cites plain array assignment, not these functions, so Part A should decide whether they are ports or
  deliberate simplifications. C's `curses_conflict` and `check_object_curses` (also unstamped) are the conflict logic
  that these Java methods do not appear to carry.

- `obj_has_flag` (`obj-util.c`): the Java counterpart is `hasFlag` (`ItemObject.java:2268`), which is just
  `flags.has(flag)`. C's version also checks every curse's object flags. Part F names `hasFlag` but not the C function,
  so F's agent needs `obj_has_flag` added to its C list.

- `object_to_hit`, `object_to_dam`, `object_to_ac` (`obj-util.c`): C adds each active curse's bonus to the object's own.
  `getToHit`, `getToDam` and `getToAC` (`ItemObject.java:2124` to `ItemObject.java:2160`) are plain field getters, so
  they are not counterparts. No `objectToHit`-style method exists anywhere in `src/main/java`. Not ported, and not named
  by any part.

- `object_effect` (`obj-util.c`): C prefers the activation's effect over the object's own. `getEffect`
  (`ItemObject.java:2942`) returns the field only. No Java `objectEffect` exists. Not ported.

- `object_is_known_artifact` and `obj_is_known_artifact`: no Java counterpart in `ItemObject`. `isArtifact`
  (`ItemObject.java:1216`) checks only `artifact != null`, so it matches neither (both C versions also require `known`).

- `ego_has_ignore_type` (`obj-ignore.c`): not in `ItemObject`. `egoIsIgnored` (`ItemObject.java:3540`) calls
  `EgoItem.getIgnoreType` (`EgoItem.java:383`), which is the `ego_is_ignored` side. Part F should check which of the two
  C functions that EgoItem method really implements.

## C side: functions named in the plan with no ItemObject counterpart

- Ported elsewhere (nothing to do in ItemObject):

    - `object_delete` is `Chunk.objectDelete` (`Chunk.java:1832`).

    - `copy_curses` is `ObjectUtils.copyCurses` (`ObjectUtils.java:1234`); Part A's `copy` and `initCurses` are its
      callers.

    - `apply_curse_attributes` is in ItemObject (`ItemObject.java:4832`) as named in Part C; `modify_weight_for_curse`
      is
      `Curse.modifyWeightForCurse` (`Curse.java:352`); `do_curse_effect` is `ObjectUtils.doCurseEffect`
      (`ObjectUtils.java:233`).

    - `ignore_item_ok`, `ignore_drop`, `object_is_ignored` are in `ObjectIgnore.java` (`ignoreItemOK`, `ignoreDrop`,
      `isIgnored`).

    - `object_flag_is_known`, `object_element_is_known`, `object_has_rune`, `object_set_base_known` and
      `object_learn_on_wield` are in `ObjectUtils.java`, `KnownObject.java`, `ObjectKnowledge.java` and
      `PlayerKnowledge.java`.

    - `equipped_item_slot` is `PlayerBody.equippedItemSlot` (`PlayerBody.java:208`); `object_pack_total` is in
      `ObjectGear.java`; `object_desc` is `ObjectUtils.objectDesc` (`ObjectUtils.java:199`).

- Not ported at all (searched by likely Java names, found nothing): `object_copy_amt` (`obj-pile.c`, which Part A
  already expects to find deferred), `object_free`, `object_pile_free`, `object_slot`, `object_is_in_store`,
  `object_flavor_was_tried`, `object_flavor_tried`, `check_for_inscrip_with_int`, `is_unknown`, `compare_items`,
  `convert_depth_to_origin`, `obj_has_inscrip`, `obj_has_charges`, `obj_can_*`, `obj_is_*` activation and device
  predicates, `object_sense`, `object_see`, `object_touch`, `object_grab`, `object_learn_unknown_rune`,
  `object_learn_on_use`. None is claimed by any part, so none is a partition gap; they are Chapter 7 or later work.

- Every C function the plan names for Parts B, C, D, E, F and G, other than the above, has a counterpart in
  `ItemObject.java` and is covered by exactly one part. `flavourIsAware` (`ItemObject.java:3212`) has no C twin of its
  own; it sits beside `objectFlavourIsAware` (`ItemObject.java:6506`), which is the `object_flavor_is_aware` port. They
  differ:
  `flavourIsAware` returns false on a null kind, the other throws. Part F should note both.

## Suggested corrected assignment table

Only the changes. Everything else stays as the plan has it.

| Method (declarations)                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | Plan today | Move to | Reason                                                                 |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------|---------|------------------------------------------------------------------------|
| `getBrands`, `getSlays`, `setSlays` (3)                                                                                                                                                                                                                                                                                                                                                                                                                                                   | none       | A       | same `Set` fields as the brand and slay editors                        |
| `setEffectMessage`                                                                                                                                                                                                                                                                                                                                                                                                                                                                        | G          | H       | plain setter, no C function                                            |
| `getObjectFlags`                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | F          | H       | raw field getter, not `object_flags`                                   |
| `getGrid`, `setGrid`, `getKind`, `setKind`, `getNumber`, `setNumber`, `gettValue`, `settValue`, `getTimeout`, `setTimeout`, `getToHit`, `setToHit`, `getToDam`, `setToDam`, `getToAC`, `setToAC`, `getsValue`, `setsValue`, `getpValue`, `setpValue`, `getWeight`, `setWeight`, `getDamageDice`, `setDamageDice`, `getDamageSides`, `setDamageSides`, `getBaseAC`, `setBaseAC`, `getNote`, `setNote`, `getTime`, `setTime`, `getKnown`, `getEgo`, `setEgo`, `getEffect`, `setEffect` (37) | none       | H       | plain getters and setters                                              |
| `getModifiers`, `setModifiers`, `getElInfo`, `setElInfo`, `putModifier`, `putElInfo`, `setElInfoResLevel`, `getModifierValue` x2 (9)                                                                                                                                                                                                                                                                                                                                                      | none       | H       | field map accessors and editors                                        |
| `getFlags`, `getNotice`, `setFlags`, `setFlag`, `setFlagsTo` (5)                                                                                                                                                                                                                                                                                                                                                                                                                          | none       | H       | flag-field accessors; H's agent must re-read the `Flag` calls like F's |

- Resulting counts: A 27, B 14, C 10, D 18, E 19, F 23, G 11, H 65. Total 187.

- C-list additions for the briefs: Part A add `append_object_curse`, `remove_object_curse` (`obj-curse.c`); Part F add
  `obj_has_flag` (`obj-util.c`) and `ego_has_ignore_type` (`obj-ignore.c`); Part G add `object_attr` and `object_char`
  (`ui-object.c`); Part H's C side stays "none".

## Next step

- Paste the table above into `docs/ItemObject_agent_plan.md` (Parts A, F, G, H and the C lists), then Phase 1 can start.

partition needs fixes
