# Part D: power components (Stage 1, round 2)

Run 2026-10-07. Read-only. Every Java declaration, field, table and C function named below was read in this turn. C
side: `obj-power.c` (all nine functions, plus `curse_power`, `object_power` for the call order), `obj-power.h`,
`obj-properties.c` (`lookup_obj_property`), `obj-init.c` (property parsing, `write_curse_kinds`, the curse parsers),
`obj-util.c` (`object_flags`, `object_weight_one`), `obj-gear.c` (`wield_slot`, `slot_by_name`), `obj-tval.c`
(`tval_is_jewelry`), `lib/gamedata/object_property.txt`, `curse.txt` and `object.txt`.

## What matches

- Scope: all eighteen declarations were read in full, plain and `Curse` overload: `effectsPower`
  (`ItemObject.java:4997`, `ItemObject.java:5028`), `elementPower` (`ItemObject.java:5067`, `ItemObject.java:5153`),
  `flagsPower` (`ItemObject.java:5251`, `ItemObject.java:5321`), `modifierPower` (`ItemObject.java:5396`,
  `ItemObject.java:5457`), `jewelleryPower` (`ItemObject.java:5509`, `ItemObject.java:5531`), `toAcPower`
  (`ItemObject.java:5557`, `ItemObject.java:5596`), `acPower` (`ItemObject.java:5647`, `ItemObject.java:5689`),
  `toHitPower` (`ItemObject.java:5709`, `ItemObject.java:5733`), `rescaleBowPower` (`ItemObject.java:5765`,
  `ItemObject.java:5786`).

- No mismatch found in the code of any of the eighteen. The baseline (batch 5) still holds, and the curse unflattening
  work did not break it.

- Call order: `objectPower(boolean, String)` (`ItemObject.java:4246`) and `objectPower(Curse, ...)`
  (`ItemObject.java:4319`) call these nine in `object_power`'s order: rescale bow, to-hit, base AC, to-AC, jewellery,
  modifiers, flags, elements, effects. That order matters because the element and flag count rows are shared state, and
  `cursePower` (`ItemObject.java:4543`) runs after the base object has finished counting, as in C.

- `effectsPower` (`ItemObject.java:4997`) vs `effects_power`: an activation, when there is one, supplies the figure and
  the kind's power is not consulted even if the activation's is zero (Java tests
  `activation != null && !isEmpty() && getFirst() != null`, C tests the pointer). A zero figure adds nothing. Walked:
  activation power 7 and kind power 12 gives +7 in both; no activation and kind power 12 gives +12; activation power 0
  and kind power 12 gives +0 in both. The null-kind answer of 0 is an accepted divergence and is not raised.

- `elementPower` (`ItemObject.java:5067`) vs `element_power`, tables: `GameConstants.java:476-510` holds the three
  `element_sets` rows `(T_LRES,3,6,INHIBIT_POWER,4)`, `(T_LRES,1,1,10,4)`, `(T_HRES,1,2,10,9)` in C's order, and the
  thirteen `el_powers` rows (acid 3/-6/5/38, electricity 1/-6/6/35, fire 3/-6/6/40, cold 1/-6/6/37, poison 28, light 6,
  dark 16, sound 14, shards 8, nexus 15, nether 20, chaos 20, disenchantment 20) in C's order. The constructor argument
  order of `ElementSet.java:97` and `ElementPowers.java:98` was checked against the call sites. `ElementEnum` lists the
  same thirteen elements in the same order as `list-elements.h`.

- `elementPower` clauses: per element the ignore test, then vulnerable (`-1`), resist (`1`), immune (`3`) as an
  else-chain; immunity adds `imPower + resPower` and only when `imPower != 0`; level 2 scores nothing; the combination
  count uses `set.resLevel <= elInfo.resLevel` (so a level-3 element also counts in the low-resists row); the bonus pass
  is after the walk and does the multiple test (`count > 1`, `factor * count * count`) before the full-set test
  (`count == size`). An absent `ElementInfo` is skipped, which equals C's zeroed entry because every row needs a level
  of at least 1.

- `elementPower` walked: all four basic immunities gives 43+41+46+43 = 173 from the walk, then immunities row 6 *16 = 96
  plus 20000, then low-resists row 1*16 = 16 plus 10, in both. One immunity gives its walk figure and the low-resists
  row counts 1, so no multiple and no full set, in both. All nine high elements at level 1 gives 162 plus 10 in both.

- `flagsPower` (`ItemObject.java:5251`) vs `flags_power`: flags come from a plain copy of the item's own flags (C's
  `object_flags` is `of_wipe` then `of_copy`; checked in `obj-util.c`), iterated in ordinal order as `of_next` does.
  Power is `flag.power * type_mult[tval]` with a default of 1 (`ObjectProperty.java:354`, and
  `parse_object_property_name` in `obj-init.c` sets every entry to 1). Family counting is on `subtype`; the multiple
  test is before the full-set test. `GameConstants.java:468-474` holds `(SUST,1,10,5)`, `(PROT,3,15,4)`, `(MISC,1,25,8)`
  as in C. Every flag in `list-object-flags.h` has a property entry in `object_property.txt`, so the "unknown property"
  throw is not reachable with shipped data (the divergence itself is accepted).

- `flagsPower` walked: five sustains (9+4+4+7+8 = 32) gives 32 + 1 *25 + 10 = 67 in both; four protections (6+16+24+12 =
  58) gives 58 + 3*16 + 15 = 121 in both; a single flag gets no multiple bonus in both; a flag with power 0 still counts
  toward its family in both.

- `modifierPower` (`ItemObject.java:5396`) vs `modifier_power`: loops all sixteen modifiers (`OM_NONE` and `OM_MAX`
  skipped; the other sixteen map one-for-one to `OBJ_MOD_STR` to `OBJ_MOD_MOVES`); the stat properties are found through
  the "stats count as mods" clause in `ObjectRegistry.lookupObjectProperty` (`ObjectRegistry.java:1003`), as in
  `lookup_obj_property`. An absent modifier reads as 0. `k * mult` is accumulated for every modifier; the power term is
  `k * power * type_mult` and only when `power != 0`. Thresholds: more than 249 adds `INHIBIT_POWER`, else more than 0
  indexes `abilityPower[total / 10]` with an early return on a zero entry; the table at `ObjectRegistry.java:334` equals
  C's 25-entry `ability_power`.

- `modifierPower` walked: total 69 gives index 6, entry 0, so nothing added; total 70 gives index 7, +2; total 249 gives
  index 24, +110; total 250 gives +20000; total 0 or negative adds nothing and is not looked up. All identical in both.
  Strength +5 on a ring (power 9, mult 13) gives q = 45 and a weighted total of 65, so no ability bonus, in both.

- `jewelleryPower` (`ItemObject.java:5509`) vs `jewelry_power`: `TValue.isJewellery()` (`TValue.java:1084`) is ring or
  amulet, as `tval_is_jewelry`; the constant is 4 (`ObjectRegistry.java:72`).

- `toAcPower` (`ItemObject.java:5557`) vs `to_ac_power`: zero returns early; base term is `to_a * 2 / 2`; `> 26` adds
  `(to_a - 25) * 2`; `> 36` adds `(to_a - 35) * 4`; `>= 56` adds 20000 on top. Constants 26, 36, 56, 2 match
  `obj-power.h` (`ObjectRegistry.java:94`, `:142`, `:146`, `:150`). Walked: 26 gives 26; 27 gives 27+4 = 31; 30 gives
  40; 36 gives 36+22 = 58; 37 gives 37+24+8 = 69; 55 gives 55+60+80 = 195; 56 gives 56+62+84+20000 = 20202; -3 gives -3.
  All identical in both.

- `acPower` (`ItemObject.java:5647`) vs `ac_power`: nothing happens when base AC is 0; otherwise `+1`, `q = ac * 2 / 2`,
  then for weight above 0 `i = 750 * (ac + to_a) / weight` (truncating) capped at 450 with no floor, `q = q * i / 100`
  (truncating), and for weight 0 or less `q *= 5`. The weight is `objectWeightOne()`, the port of `object_weight_one`.
  Walked: ac 10, weight 100 gives 1 + 7 = 8; ac 10, to_a -20, weight 100 gives i = -75 and q = -7 (truncation toward
  zero) in both; i exactly 450 stays 450 and 451 caps to 450 in both; weight 0 gives q = 5 * ac in both.

- `toHitPower` (`ItemObject.java:5709`) vs `to_hit_power`: no early return, `q = to_h * 3 / 2`, truncating toward zero
  in both; the log fires on the running total being nonzero in both. Walked: +1 gives 1, +2 gives 3, -1 gives -1, 0
  gives 0.

- `rescaleBowPower` (`ItemObject.java:5765`) vs `rescale_bow_power`: the test is
  `wieldSlot() == slotByName(player, "shooting")`, the divisor is 5 and truncates toward zero (-7 gives -1, 4 gives 0, 5
  gives 1, in both). `wieldSlot` (`ItemObject.java:6457`) answers -1 for a type with no slot (C's `wield_slot` falls
  through to -1 and `slot_by_name` never returns a negative, so never equal in both). Reads the live player from
  `GameState.getPlayer()`, like C's `player` global at the call.

- Curse overloads: in C a curse object is `curses[i].obj`, a zeroed `struct object` that `write_curse_kinds`
  (`obj-init.c`) gives the `<curse object>` kind and an sval, and that never gets a tval (so tval 0, `TV_NULL`,
  `TValue.TV_NONE` here). `<curse object>` in `object.txt` has no `power:`, `curse.txt` has no way to set base AC or an
  activation, and the curse parsers only set `to_h`, `to_d`, `to_a`, weight, flags, modifiers, element info, effect,
  effect message and time. So `effects_power`, `jewelry_power`, `ac_power` and `rescale_bow_power` return their input on
  a curse object, and the four identity overloads (`ItemObject.java:5028`, `:5531`, `:5689`, `:5786`) are correct.
  `wield_slot` on tval 0 reaches the end and returns -1, so no equality with the shooting slot.

- Curse overloads that do real work read the curse's own `ItemObject` through `Curse.getItemObject()`
  (`Curse.java:198`), which `CurseAssembler.java:333-342` fills with flags, modifiers, element info, to-hit, to-damage
  and to-AC. `curse.txt`'s `combat:` order (to-hit, to-damage, to-AC, from `parse_curse_combat`) matches the grammar
  action in `CurseGrammar.g4:40-44` (h to `toh`, d to `tod`, a to `toa`) and the assembler's `setToHit` / `setToDam` /
  `setToAC` calls. `toAcPower(Curse)` and `toHitPower(Curse)` are the plain bodies over that object's figures;
  `elementPower(Curse)` is the plain body over its `getElInfo()`; `modifierPower(Curse)` walks only the declared
  modifiers, which equals C's walk of all sixteen because an undeclared one is 0 and adds 0 to both the weighted total
  and the power term; `flagsPower(Curse)` and `modifierPower(Curse)` use a multiplier of 1, correct because no property
  in `object_property.txt` has a `type-mult:none` line (grep of the C copy of the file; the repo copy differs from it
  only in two comment lines' trailing spaces, a `record-count:79` line, and nothing in the data lines).

- Shared state: `elementPower` and `flagsPower` zero the count rows before use and fill them in place, in both
  overloads, as C does on its static tables. The only other write is `wieldSlot` refreshing `player`
  (`ItemObject.java:6458`), which is harmless. Nothing else is written; `acPower`'s early read of the weight has no
  effect.

## What does not match

- None in the code. No clause, table row, threshold, truncation or early return differs from C in any of the eighteen
  declarations on any value walked.

## Out-of-scope observations

- Javadoc on Part D declarations has gone stale since the curse unflattening (documentation only, Stage 2 work, not a
  code mismatch): `ItemObject.java:5312` links `Curse#getObjectFlags()`, `ItemObject.java:5587` links
  `Curse#getCombatAC()` and `ItemObject.java:5725` links `Curse#getCombatToHit()`. I read `Curse.java` this turn and it
  declares neither; the code at `ItemObject.java:5323`, `:5597` and `:5734` reads `curse.getItemObject()` instead. The
  `toAcPower(Curse)` and `toHitPower(Curse)` blocks also say the figure is "read through" those getters.

- The class block at `ItemObject.java:77` ("the port's `Curse` is a flattened record and not an object") and the
  `objectPower(Curse, ...)` block at `ItemObject.java:4294` ("flattened record") no longer describe `Curse`, which now
  holds a real `ItemObject` (`Curse.java:116`). The comment at `ItemObject.java:4854` says "flattened curse data" for
  the same reason. These are Part C and class-level text.

- C line numbers remain in Javadoc outside `ItemObject.java`, against the house rule: `ObjectRegistry.java:188`, `:198`,
  `:205`, `:328` (`obj-power.c:71`, `:93`, `:112`, `:132`), `FlagSet.java:24` (`:64-75`), `ElementSet.java:24`
  (`:84-99`), `ElementPowers.java:26` (`:103-127`) and `ElementPowers.java:40` (`:676`).

- `modifierPower` has a dead guard: `ItemObject.java:5412` tests `getModifiers() == null`, but `getModifiers()`
  (`ItemObject.java:2778`) answers `Map.of()` for a null field and cannot return null. It changes nothing.

- `effectsPower` can only see an activation if the constructor was given one (`ItemObject.java:1022`) or the item came
  from `copy`; `ItemObject.java` has no setter, and `ObjectUtils.java` has no assignment to the field (grepped this
  turn). C's `make_artifact` and the ego path in `obj-make.c` copy `art->activation`, `kind->activation` and
  `ego->activation` into `obj->activation` at generation. Whether the Java generation code does the equivalent belongs
  to a later chapter, but until it does, `effectsPower` falls back to the kind's power for every generated item.

- `objectPower` is private and its only callers outside the curse plumbing are `ItemObject.java:4131`
  (`objectValueReal`), so the plain components are reached by `PlayerBirth.java:882` only through that value path;
  nothing reaches the `Curse` overloads except `cursePower`.

## Next

Part D is clean on the code. Fix the stale Javadoc links listed above in the Stage 2 pass, and strip the C line numbers
when you next touch `ObjectRegistry.java`, `FlagSet.java`, `ElementSet.java` and `ElementPowers.java`. Part D can move
to Stages 2 and 3.

clean
