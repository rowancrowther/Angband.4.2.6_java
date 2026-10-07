# Part F: knowledge, ignoring and flags (Stage 1, full sweep)

Run 2026-10-07. Read-only. Every Java declaration listed below and every C function named was read in this turn. Line
numbers are from that read. No C line numbers are cited.

Files read this turn: `ItemObject.java`, `EgoItem.java`, `ObjectInfo.java`, `Flag.java`, `ObjectProperty.java`,
`ObjectPropertyAssembler.java`, `ObjectPropertyGrammar.g4`, `ObjectIgnore.java`, `Random.java` (the `randCalc`/`varies`/
`getBase`
callees), `TValue.java` (`isBodyArmour`, `isJewellery`), `QualityValueEnum.java`, `ItemObjectMessagingTest.java` (one
test). C read: `obj-knowledge.c`, `obj-ignore.c`, `obj-properties.c`, `obj-util.c`, `obj-curse.c`
(`modify_weight_for_curse`), `obj-tval.c`,
`obj-desc.h`, `list-ignore-types.h`, `z-rand.c`, `h-basic.h` (`CMP`).

Declarations covered (22 of the 23 in the plan; `getObjectFlags` was moved to Part H by
`docs/ItemObject_round2/part_0.md`, and
`getFlags` was read because `flagsKnown` and `objectFlags` rest on it): `isKnown`, `isFullyKnown`, `runesKnown`,
`effectIsKnown`,
`hasStandardToH`, `hasFlag`, `flagMessage`, `objectFlags`, `flagsKnown`, `easyKnow`, `flavourIsAware`,
`objectFlavourIsAware`,
`getIgnoreTypeOf`, `isEgo`, `egoIsIgnored`, `ignoreLevelOf`, `isGood`, `compareObjectTrait`, `rechargeTimeout`,
`numberCharging`, `checkForInscription`, `verifyObject`, `objectWeightOne`; plus `EgoItem.getIgnoreType`.

Accepted divergences from `docs/ItemObject_port_batches.md` (batches 1, 2, 7) were not re-raised: `flagsKnown` empty set
for no known half; `easyKnow`/`flavourIsAware` false for a kindless item; `objectFlavourIsAware` throws; `egoIsIgnored`
null-ego guard;
`flagMessage` exact `{name}` replace only, no 1,024 truncation, logs its two data errors instead of a "Bug:" line;
`copy(false)` leaves `known` null; `verifyObject` placeholder name.

## What matches

- `ItemObject.java:1494` `isFullyKnown` against `object_fully_known`: runes first, then effect, both must pass. Same
  order, which matters because `object_effect_is_known` dereferences `known`.

- `ItemObject.java:1568` `runesKnown` against `object_runes_known`: no known half gives false; then
  `cursesAreEqual(known)` against
  `curses_are_equal(obj, obj->known)`; then the non-curse runes answer. Same three steps in the same order.
  (`cursesAreEqual` and
  `PlayerKnowledge.nonCurseRunesKnown` are other parts' declarations and were not re-verified here.)

- `ItemObject.java:1595` `effectIsKnown` against `object_effect_is_known`: C compares two effect-chain pointers. Java
  compares list contents both ways with `contains`. `Effect` has no `equals`, so `contains` is identity, and every
  writer (`PlayerKnowledge.java:360`, `PlayerKnowledge.java:563`, `PlayerKnowledge.java:569`,
  `PlayerKnowledge.java:622`,
  `PlayerBirth.java:878`) passes `item.getEffect()` straight through, so learned means the same `Effect` instances.
  Walks: object
  `[E]`, known `[]` gives false in both; object `[]`, known `[]` gives true in both (C: two NULLs); object `[E]`, known
  sharing
  `[E]` gives true in both.

- `ItemObject.java:2225` `hasStandardToH` against `object_has_standard_to_h`: null kind true; body armour (soft, hard,
  dragon, as
  `TValue.java` `isBodyArmour` and `tval_is_body_armor`) with a non-varying `to_h` compares to the dice base; everything
  else compares to zero. `Random.varies` is min against max at level 0, as `randcalc_varies`. Walks: Chain Mail kind
  `-2`, item `-2`
  true, item `0` false, item `+3` false. Dagger `0` true, `-1` false. Soft armour whose kind rolls `d3` falls to the
  zero test.

- `ItemObject.java:2179` `isKnown` is `known != null`; `ItemObject.java:3517` `isEgo` is `ego != null`. Both are
  struct-field reads in C.

- `ItemObject.java:2407` `objectFlags` against `object_flags`: wipe, then copy. Same clause order. `Flag.copyFrom`
  (`Flag.java:478`)
  now snapshots the source into a scratch set before wiping, so it is safe for a distinct source. The `!obj` early
  return has no Java case (instance method).

- `ItemObject.java:2377` `getFlags` builds a fresh `Flag` and calls `copyFrom(flags)` with two distinct objects. The new
  self-copy path is not exercised.

- `ItemObject.java:3263` `flagsKnown` against `object_flags_known`: copy the item's flags, intersect with the known
  half's flags, return if no kind, union the kind's flags if aware, then (ego and `easyKnow`) union the ego's flags and
  difference out the ego's `flags_off`. Order is intersect, then add kind flags, then add ego flags, then remove ego-off
  flags, as C. Flag calls used:
  `Flag.copyFrom` (`Flag.java:478`, distinct objects), `Flag.inter` (`Flag.java:550`, `removeIf(!other.has)`),
  `Flag.union`
  (`Flag.java:503`), `Flag.diff` (`Flag.java:573`, clears the other's flags from this). `Flag.andNot` is not called by
  any Part F declaration. Walk: object flags {A,B,C}, known {A,B}, aware kind adds {D}, easy-know ego adds {E} and turns
  off {B} gives {A,D,E} in both.

- `ItemObject.java:3236` `easyKnow` against `easy_know`: `kind.isAware()` and `KF_EASY_KNOW`, both required. Walks:
  aware without the flag false; flag without awareness false; both true.

- `ItemObject.java:3212` `flavourIsAware` and `ItemObject.java:6506` `objectFlavourIsAware` both return `kind.isAware()`
  for a real kind, as `object_flavor_is_aware`. Only the null-kind behaviour differs (accepted).

- `ItemObject.java:3489` `getIgnoreTypeOf` against `ignore_type_of`: first row whose tval matches; a non-empty
  identifier must be a case-sensitive substring of the kind's name (`contains` is `strstr`; `!isEmpty()` is
  `identifier[0]`); otherwise `continue`;
  `ITYPE_MAX` when nothing matches. The table `ObjectInfo.java:28-67` agrees with `quality_mapping` row for row and in
  order, 38 rows, including every dragon-armour row and the "Chaos"/"Slicing"/"Disruption" great-weapon rows ahead of
  the plain rows. Walks:
  Dagger (sword) gives `ITYPE_SHARP`; "Blade of Chaos" gives `ITYPE_GREAT`; "Long Bow" gives `ITYPE_BOW`; a light
  crossbow gives `ITYPE_CROSSBOW` ("Bow" is capital B and does not match "Crossbow"); a spade (digging) gives
  `ITYPE_DIGGER`; a potion gives `ITYPE_MAX`.

- `ItemObject.java:3540` `egoIsIgnored` and `EgoItem.java:383` `getIgnoreType` against `ego_is_ignored`: a per-category
  flag read. See the "Look for" answers. The constructor (`EgoItem.java:246-249`) seeds every `IgnoreType` including the
  sentinels with false, so `ignoreTypes.get(type)` never unboxes null.

- `ItemObject.java:3575` `ignoreLevelOf` against `ignore_level_of`: no known half gives `IGNORE_MAX`; jewellery (ring or
  amulet, tested on the item) is judged from the known half only, any positive modifier gives average, then any positive
  combat bonus gives average, then any negative gives bad, else average; otherwise fully known gives good/bad/average
  from `isGood`, overridden to
  `IGNORE_ALL` for an ego, else `IGNORE_MAX` for an artifact; not fully known gives `IGNORE_ALL` if the known half's
  notice has
  `OBJ_NOTICE_ASSESSED` and the item is not an artifact, else `IGNORE_MAX`. Same clauses, same order, same
  ego-before-artifact precedence. The Java loop over `known.getModifiers().keySet()` is equivalent to C's loop over
  every modifier index because an absent modifier reads zero. `QualityValueEnum` order (NONE, BAD, AVERAGE, GOOD, ALL,
  MAX) is C's. Walks: ring with known `+2`
  to-hit average; ring with known `-1` to-AC and nothing positive bad; ring with all zeros average; fully known Dagger
  `+0,+0`
  average, `+0,+1` good, `-1,+0` bad; ego Dagger `IGNORE_ALL` whatever its bonuses; artifact `IGNORE_MAX`.

- `ItemObject.java:3642` `isGood` against `is_object_good`: weights 4 (to-dam), 2 (to-hit), 1 (to-AC), each against the
  kind's matching dice (`ObjectKind.getToD`, `getToH`, `getToA` return `toD`, `toH`, `toA`). Item fields `toDam`,
  `toHit`, `toAC` are the right ones.

- `ItemObject.java:3669` `compareObjectTrait` against `cmp_object_trait`: `randCalc(0, MINIMIZE)` is
  `randcalc(base, 0, MINIMISE)`, clamped to at most zero, then compared. `NumberUtils.cmp` is `Integer.compare`, which
  returns -1/0/1, identical to the `CMP` macro. Walks: kind `-2`: bonus `-2` gives 0, `0` gives 1, `-3` gives -1. Kind
  `+3` (floor clamps to 0): bonus `0` gives 0, `-1` gives -1, `+1` gives 1. Kind `1d3` (min 1, clamps to 0): bonus `0`
  gives 0.

- `ItemObject.java:2026` `numberCharging` against `number_charging`: average of `time` at level 0
  (`number * (sides + 1) / 2`, as
  `damcalc`), non-positive interval gives 0, non-positive timeout gives 0, ceiling division `(timeout + t - 1) / t`,
  capped at
  `number`. Walks: `1d10` interval (average 5), timeout 11, number 3 gives 3; timeout 11, number 2 gives 2 (cap);
  timeout 10 gives 2; timeout 5 gives 1; timeout 0 gives 0; interval `Random.Zero()` gives 0. `time` is never null
  (constructors, `wipe`,
  `setTime` all land on `Random.Zero()`).

- `ItemObject.java:1995` `rechargeTimeout` against `recharge_timeout`: same early return on zero,
  `timeout -= min(before, timeout)`, same transition test. Walk of the Javadoc's three rods on a ten-turn interval,
  timeout 30: turns 1-3 return false (count stays 3), turn 4 true (timeout 18, count 2), turn 8 true (timeout 10, count
  1), turn 18 true (timeout 0, count 0). The Javadoc's "fourth, eighth and eighteenth" is right.

- `ItemObject.java:3391` `checkForInscription` against `check_for_inscrip`: null note gives 0; the scan resumes one
  character past each match start (`location = result` where `result` is index + 1), as C's `s++`. Walk: note `"!!!"`,
  fragment `"!!"` gives 2 in both (matches at 0 and 1). Note `"@m1 !d"`, fragment `"!d"` gives 1. Not found gives 0.
  Empty fragment is a documented divergence (C's `strstr` would match at every position).

- `ItemObject.java:3440` `verifyObject` against `verify_object`: describes with prefix plus combat plus extra
  (`ODESC_FULL` is
  `ODESC_COMBAT | ODESC_EXTRA` in `obj-desc.h`), builds `"%s %s? "`, asks through the boundary. Truncation to 80/160 is
  a documented divergence; the name is a stub tag.

- `ItemObject.java:3353` `objectWeightOne` against `object_weight_one`: floor at zero, then each curse with non-zero
  power applied in ascending index. C loops `i = 1 .. curse_max - 1` over a placeholder-0 array; Java indexes from 0
  with no placeholder (`CurseAssembler.java` reverses the prepend-built list, so Java's ascending index is C's ascending
  index, offset by one). The application order, which matters because a multiplier and an addend do not commute, is
  therefore the same. Power-0 entries are skipped. Walk: weight `-5` gives 0 before any curse; weight 100 with a `+20`
  addend curse then a `*50%` multiplier curse applies in registry order in both.

- `ItemObject.java:2268` `hasFlag` is `flags.has(flag)`. This is `of_has(obj->flags, flag)`, which is what every Java
  caller needs (see the first "Look for" answer).

## What does not match

- `ItemObject.java:2341-2358` `flagMessage` against `flag_message`, the "property exists but declares no `msg:`" path.
    - C: `if (!prop->msg) return;`. Silent. Nothing is shown and nothing is logged.
    - Java: `ItemObject.java:2354-2355` returns silently only when `property.getNoticeMessage()` is `null`. In the real
      data path it is never `null`. `ObjectProperty.java:141-152` (the `message` field Javadoc) says the port holds `""`
      for an absent `msg:`, and that is what the code does: `ObjectPropertyGrammar.g4` initialises `msgInit` from an
      empty `StringBuilder`, and
      `ObjectPropertyAssembler.java:181` passes `record.msg()` straight into the `ObjectProperty` constructor.
    - Input that diverges: any flag whose `object_property.txt` record has no `msg:` line. From the file, the `on wield`
      flags with no message are telepathy, see invisible, blessed melee, burns out, takes fuel, no fuel, constant fear,
      stuck on, fragile, intensity 2 and 3 light, power 1, 2 and 3 digging, explode, throwing and multiply weight.
    - Java result: `"".replace("{name}", name)` is `""`, so `Message.message("%s", "")` runs. `Message.message` formats,
      calls
      `messageAdd("")` (which stores an empty entry in the log, or bumps the count of a previous empty one) and signals
      `EVENT_MESSAGE` with empty text. C result: nothing at all.
    - Reachability: `PlayerKnowledge.java:2140` (`objectLearnOnWield`) calls `flagMessage` for every obvious unknown
      flag when
      `isPlaying()`. Wielding a Wooden Torch for the first time reaches it with `OF_BURNS_OUT`, `OF_TAKES_FUEL` and
      `OF_LIGHT_2`, all message-less. `PlayerKnowledge.java:1639` (`equipLearnFlag`) and `PlayerKnowledge.java:1880`
      (curse flags) reach it the same way.
    - Why the tests do not see it: `ItemObjectMessagingTest.java` `noMessageIsSilent` (line 223-224) registers the
      property with a
      `null` message, which is the state C has and the assembler never produces.
    - The method's own Javadoc (`ItemObject.java:2316-2323`, "A property that exists but declares no msg ... returns
      without a word") describes the intended behaviour and is wrong about what the code does with real data.

## Out-of-scope observations

- `ObjectProperty.java:141-152` and `ObjectPropertyAssembler.java:181`: the root of the `flagMessage` mismatch. Either
  side of the boundary could be changed (the assembler to hand over `null` for an empty `msg`, or the test to use `""`);
  that choice is yours and was not assessed. Both files sit outside Part F.

- `ego_has_ignore_type` (`obj-ignore.c`) has no Java port anywhere in `src/main/java` (searched for `egoHasIgnoreType`
  and the C name). Its only C caller is the ego-ignore menu in `ui-options.c`, so nothing in the engine needs it yet,
  but the menu port will.

- `ItemObject.java:2395-2397` (the `objectFlags` Javadoc) calls the explicit `flag.wipe()` "redundant in Java" because
  `copyFrom`
  wipes too. That holds only for a distinct set. `Flag.copyFrom` (`Flag.java:478`) now survives a self-copy, but
  `objectFlags`
  wipes first, so `item.objectFlags(item.getObjectFlags())` (`getObjectFlags` is the live set, `ItemObject.java:6837`)
  empties the item's own flags and leaves them empty. C's `of_wipe` then `of_copy` onto the same array does the same, so
  this matches C; no current caller aliases (the only non-test caller, `PlayerUtils.java:428`, passes a fresh local
  set). The Javadoc sentence is the only thing to reconsider.

- `ItemObject.java:976` second constructor stores `flags`, `effect`, `tValue` and `notice` by reference and unvalidated,
  so a `null`
  for any of them makes `getFlags` (`ItemObject.java:2377`), `effectIsKnown`, `hasStandardToH` and `ignoreLevelOf` throw
  a
  `NullPointerException`. The constructor's own Javadoc says no production code calls it, so this is not reachable
  today.

- `effectIsKnown` (`ItemObject.java:1595`) treats two lists with the same members in a different order as equal; C's
  pointer test would not. Not reachable, because the known half takes the same list object.

- `ObjectIgnore.java:256` `isIgnored` makes the same `known.isEgo() && egoIsIgnored(type)` test as C's
  `object_is_ignored`, so the known-versus-real split documented on `egoIsIgnored` is honoured by its one caller.
  (Context only; `ObjectIgnore` is not Part F.)

## Plan 'Look for' answers

- **Flag.andNot and Flag.copyFrom self-copy.** Every `Flag` call in this part was re-read against `Flag.java` as it
  stands: `getFlags`
  and `flagsKnown` call `copyFrom` with distinct objects; `objectFlags` calls `wipe` then `copyFrom` with distinct
  objects in all real callers; `flagsKnown` also calls `inter`, `union` and `diff`; `verifyObject` uses the varargs
  constructor. No Part F declaration calls `andNot`. No result differs under the new `copyFrom`, and the only place the
  self-copy fix could change an answer (aliased `objectFlags`) is covered above.

- **(1) `hasFlag` against `obj_has_flag`.** `hasFlag` (`ItemObject.java:2268`) checks only the item's own flags;
  `obj_has_flag` also checks every active curse's object flags. It is a real difference but no Java caller can reach it.
  C has exactly one caller of
  `obj_has_flag`, `obj_can_takeoff` (the `OF_STICKY` test), and Java has no `canTakeoff` or `OF_STICKY` use. Every Java
  caller of
  `hasFlag` corresponds to a C `of_has(obj->flags, ...)`: `PlayerKnowledge.java:1633` (`equip_learn_flag`),
  `PlayerCalcs.java:1431`, `PlayerCalcs.java:1433`, `PlayerCalcs.java:1444` (`calc_light`), `ObjectUtils.java:510`,
  `ObjectUtils.java:621`, `ObjectUtils.java:687` (throwing), `ObjectUtils.java:1153`, `ObjectUtils.java:1155` (burns
  out, takes fuel), `ObjectKnowledge.java:113` (rune lookup on the object itself), and the two calls on a curse's own
  object (`CurseSource.java:335`, `UIEntryValueRegistry.java:338`). Curse flags reach `PlayerCalcs` through
  `CurseSource` entries (`PlayerCalcs.java:210-225`), not through `hasFlag`. Verdict: not a mismatch today; it becomes
  one the moment `obj_can_takeoff` is ported, which will need a curse-aware method. The `hasFlag` Javadoc already cites
  `of_has(obj->flags, flag)`, not `obj_has_flag`.

- **(2) `EgoItem.getIgnoreType`.** It implements `ego_is_ignored` (the per-ego, per-category mark table
  `ego_ignore_types[eidx][itype]`), with each `EgoItem` holding its own map instead of a global array; items share the
  registry's `EgoItem`
  (`PlayerKnowledge.java:340`, `ItemObject.java:6576` copy the pointer), so the marks apply to every item of that ego as
  in C. It is not `ego_has_ignore_type`, which asks whether an ego's possible-item list contains any kind that maps to a
  category; that C function has no Java counterpart (see Out-of-scope observations).

- **(3) `flavourIsAware` against `objectFlavourIsAware`.** Both port `object_flavor_is_aware`, which is `assert(obj->kind); return
  obj->kind->aware;`. For a kind, both return `kind.isAware()` and match. For no kind, `flavourIsAware`
  (`ItemObject.java:3212`)
  returns false, `objectFlavourIsAware` (`ItemObject.java:6506`) logs and throws `RuntimeException`; C asserts (aborts
  in a debug build, undefined in release). Both are accepted divergences. Callers: `ItemObject.java:1127` and
  `ItemObject.java:1128` use the lenient form, `ItemObject.java:1137` and `ItemObject.java:1138` guard on a flavour
  first and then use the strict form,
  `ItemObject.java:4033` uses the lenient form and `ItemObject.java:4065` the strict one.

## Call to action

- Decide how `flagMessage` should treat a property with no message: the code only works if `getNoticeMessage()` returns
  `null`, and the data path hands it `""`. Then re-verify Part F with "try that". Part F is stopped until then.

stopped on mismatch
