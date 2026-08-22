# `Player.noticeStuff` call-chain bug list

Traced 260822 against the C original (`player-calcs.c:2536` `notice_stuff`, `obj-gear.c`,
`obj-ignore.c`, `obj-pile.c`, `obj-util.c`). `noticeStuff` itself is a faithful port; every finding below is in
something it calls.

Chain covered: `Player.noticeStuff` → `ignoreDrop` → `ignoreItemOK` → `isIgnored` →
`ItemObject.ignoreLevelOf` / `getIgnoreTypeOf` / `checkForInscription` / `verifyObject` /
`setNote`; `combinePack` → `ItemObject.mergeable` → `objectStackable` → `similar`;
`objectAbsorb` / `objectAbsorbPartial` / `objectAbsorbMerge`; `invenCanStackPartial` →
`quiverAbsorbNum` → `preferredQuiverSlot`, `packSlotsUsed`; `calcInventory`;
`MonsterUtils.showMonsterMessages`.

Note on constants throughout: `carry-cap:quiver-size` is **10** (the number of quiver slots) and
`carry-cap:quiver-slot-size` is **40** (missiles per slot). C uses `z_info->quiver_slot_size` in every capacity
calculation below; several ports reach for `getCarryCapQuiverSize()` instead.

---

## `Player.java` — `combinePack`

- [ ] **The partial-stack test is negated.**
    - `Player.java:1880` reads `if (!invenCanStackPartial(item1, item2, modes1, modes2))`.
    - C (`obj-gear.c:1280`) is `if (inven_can_stack_partial(p, obj2, obj1, ...))` — no `!`.
    - The port therefore does a partial absorb precisely when the two stacks *cannot* be partially stacked, and skips it
      when they can.

- [ ] **The two stacks are handed to it in the opposite roles to C.**
    - `Player.java:1880` passes `(item1, item2, modes1, modes2)`, where `item1` is the outer (C's `obj1`) and `item2`
      the inner (C's `obj2`).
    - C passes `(obj2, obj1, stack_mode2, stack_mode1)` — the inner stack goes first.
    - `invenCanStackPartial` is not symmetric: its first argument is the leading stack whose count is to be maximised,
      and only its first *mode* opens the quiver branch. Both the per-stack quiver limit and the "already at the limit"
      early exits are therefore applied to the wrong stack of the pair.
    - The two `objectAbsorbPartial` calls two lines below (`Player.java:1885-1886`) do pass
      `(item1, modes2, modes1)` on `item2`, matching C — so the test and the action it guards disagree about which stack
      is which.

- [ ] **Inner loop bound is `<=` where it must be `<`.**
    - `Player.java:1852`: `for (int index = 0; index <= gear.size() && gear.get(index) != item1; ...)`.
    - `gear.get(gear.size())` is out of bounds; it survives only because `item1` is always still present in `gear`
      today — which is only true because of the missing-removal bug below.

- [ ] **The outer walk iterates a live view of `gear` while the body mutates the pack.**
    - `Player.java:1847` iterates `gear.reversed()`, a `SequencedCollection` view, not a snapshot.
    - C saves `prev = obj1->prev` *before* merging precisely because `object_absorb` unlinks `obj1`
      from the gear list.
    - Once `objectAbsorb` is made to remove the absorbed stack (see below), this becomes a
      `ConcurrentModificationException`.

## `Player.java` — `invenCanStackPartial`

- [ ] **The store-mode guard is inverted.**
    - `Player.java:1926`: `if (combinedModes.has(ObjectStackEnum.OSTACK_STORE))`.
    - C (`obj-gear.c:1195`) is `if (!(cmode & OSTACK_STORE))`.
    - All the number-checking is thus done only in stores (which have no limits) and skipped everywhere else, so the
      method returns `true` for pack and quiver stacks that are already full.

- [ ] **The per-slot quiver limit uses the slot *count*, not the slot *size*.**
    - `Player.java:1929`: `GameConstants.getCarryCapQuiverSize() / (...)`.
    - C (`obj-gear.c:1198`) uses `z_info->quiver_slot_size`.
    - Limit comes out as 10 (or 10/5 = 2 for thrown) instead of 40 (or 8).

## `Player.java` — `quiverAbsorbNum`

- [ ] **C's two capacity assertions are compared against the wrong constant.**
    - `Player.java:1985` and `Player.java:1993`:
      `if (quiverItem.getNumber() * mult > GameConstants.getCarryCapQuiverSize()) return error;`
    - C (`obj-gear.c:668`, `obj-gear.c:683`) asserts `quiver_obj->number * mult <= z_info->quiver_slot_size`.
    - A legitimately full slot (e.g. 20 arrows > 10) trips the check.

- [ ] **The same assertion is converted into a silent `(-1, -1)` sentinel return.**
    - `Player.java:1963` builds `error`, returned at the two sites above.
    - C has no such path — an assertion failure is a crash, not a value.
    - The `-1` propagates to `invenCanStackPartial`'s `numToQuiver <= 0` test and is read there as
      "the quiver is full", which is indistinguishable from the real answer.

- [ ] **`remainder` is taken modulo the slot count instead of the slot size.**
    - `Player.java:2019`: `quiverCount % GameConstants.getCarryCapQuiverSize()`.
    - C (`obj-gear.c:723`): `quiver_count % z_info->quiver_slot_size`.
    - Every subsequent figure in that block (`limitFromPack`, `spaceFree`, `numToQuiver`,
      `numAddPack`) is computed from it, so the whole pack/quiver split is wrong.

- [ ] **The quiver is a variable-length `ArrayList`, not `quiver_size` fixed slots holding nulls.**
    - `PlayerUpkeep.java:253` initialises `quiverObjects = new ArrayList<>()`.
    - C indexes `p->upkeep->quiver[i]` for `i` in `0 .. z_info->quiver_size`, where an empty slot is a `NULL` entry.
    - Consequences in `quiverAbsorbNum` (`Player.java:1975`): the `numEmpty++` branch is unreachable, so `spaceFree`
      never counts free slots and `displaces` can never be satisfied; and
      `currentSlot` is a position in a dense list, so comparing it to `desiredSlot` from
      `preferredQuiverSlot` compares a list index against an inscribed quiver-slot number.

## `Player.java` — `packSlotsUsed`

- [ ] **The equipped test is inverted, so only equipment is counted.**
    - `Player.java:2080`: `if (body.itemIsEquipped(item)) { ... }` with the counting inside.
    - C (`obj-gear.c:268`): `if (!object_is_equipped(p->body, obj))` — equipment is what gets skipped.
    - The result is the number of *equipped* items rather than pack slots used, so
      `invenCanStackPartial`'s free-slot figure is unrelated to the pack.

- [ ] **The quiver scan never checks that the quiver entry is the item being examined.**
    - `Player.java:2083-2088`: the loop body runs on the first quiver entry, adds
      `quiverItem.getNumber()`, sets `found = true` and breaks unconditionally.
    - C (`obj-gear.c:273`) has `if (p->upkeep->quiver[i] == obj)` guarding all of that.
    - Two further faults in the same lines: the multiplier is taken from `item`'s tval while the count is taken from
      `quiverItem`, and a null quiver entry would NPE on `getNumber()`.

- [ ] **Full-slot division uses the slot count, while the remainder test uses the slot size.**
    - `Player.java:2097`: `packSlots += quiverAmmo / GameConstants.getCarryCapQuiverSize();`
    - `Player.java:2099`: `if (quiverAmmo % GameConstants.getCarryCapQuiverSlotSize() != 0)`.
    - C uses `quiver_slot_size` for both (`obj-gear.c:288`, `obj-gear.c:292`); the pair disagreeing with each other is
      the tell.

## `Player.java` — `ignoreDrop`

- [ ] **Appending `!d` to an uninscribed item produces the literal text `null!d`.**
    - `Player.java:2119`: `item.setNote(item.getNote() + "!d")`.
    - C (`obj-ignore.c:670-677`) branches on `inscription == NULL` and stores a bare `"!d"` in that case.
    - String concatenation in Java renders a null reference as `"null"` rather than throwing, so it fails quietly.

- [ ] **A null pushed command returns from the whole method, skipping the trailing flag updates.**
    - `Player.java:2131`: `if (dropCommand == null) return;`
    - C (`obj-ignore.c:687`) asserts instead, and always reaches
      `p->upkeep->update |= PU_INVEN` / `notice |= PN_COMBINE`.
    - The early return leaves `PU_INVEN` and `PN_COMBINE` unset for a chain that has already pushed drops for earlier
      items.

## `Player.java` — `isIgnored`

- [ ] **The "can't ignore unknown things" guard tests the kind, not the known object.**
    - `Player.java:4215`: `if (item.getKind() == null) return false;`
    - C (`obj-ignore.c:581`): `if (!obj->known) return false;`
    - An item with a kind but no `known` object passes the guard and then has its knowledge read.

- [ ] **The two notice-flag tests read the player's rune knowledge, not the item's known object.**
    - `Player.java:4218`: `itemKnowledge.noticeFlagOn(ObjectNotice.OBJ_NOTICE_IGNORE)`.
    - `Player.java:4235`: `itemKnowledge.noticeFlagOn(ObjectNotice.OBJ_NOTICE_ASSESSED)`.
    - `Player.itemKnowledge` (`Player.java:410`) is the port of C's `p->obj_k` — one shared record of which runes the
      player has learned. C reads `obj->known->notice`, a per-object field (`obj-ignore.c:585`, `obj-ignore.c:608`).
    - So both tests answer a question about the player rather than about the item, and give the same answer for every
      item in the pack.

- [ ] **Those same two calls are mutators, and their return value has the opposite sense to the test being written.**
    - `KnownObject.noticeFlagOn` (`KnownObject.java:578`) is `return noticeFlags.on(notice);`, and
      `Flag.on` (`channel/utils/Flag.java:228`) adds the flag and returns `true` only when it was *not* already present.
    - So `Player.java:4218` raises `OBJ_NOTICE_IGNORE` on the player's knowledge record as a side effect of asking
      whether it is set, and reports "ignore this item" exactly once — the first time any item is tested — and never
      again. `Player.java:4235` does the same for
      `OBJ_NOTICE_ASSESSED`.
    - Same fault, same helper, as `ItemObject.ignoreLevelOf` below; the name reads as a getter at both call sites.

- [ ] **The ego test uses the real ego rather than the known one.**
    - `Player.java:4231`: `if (item.isEgo() && item.egoIsIgnored(type))`, where `isEgo()`
      (`ItemObject.java:2098`) reads the real `ego` field.
    - C (`obj-ignore.c:604`): `if (obj->known->ego && ego_is_ignored(obj->ego->eidx, type))` — the *known* ego gates it,
      the real one supplies the index.
    - Items whose ego the player has not yet learned are ignored as if they had.

## `Player.java` — stub in the chain

- [ ] **`calcInventory` is an empty stub.**
    - `Player.java:4199`.
    - `combinePack` calls it as its last act before signalling the redraws (`obj-gear.c:1302`), and
      `invenCanStackPartial`'s quiver branch exists specifically to avoid combining something
      `calc_inventory` would then have to split.
    - With it empty, `upkeep->inven` and `upkeep->quiver` are never rebuilt after a combine.

---

## `ItemObject.java` — `setNote`

- [ ] **`setNote` discards its argument and returns the old value.**
    - `ItemObject.java:2078`: `public String setNote(String s) { return note; }`
    - Nothing is written; the field is untouched.
    - This makes `ignoreDrop`'s `!d` inscription hack a no-op, so a declined "Really take off and drop" prompt is
      re-asked on every subsequent `PN_IGNORE` notice.

## `ItemObject.java` — `objectStackable`

- [ ] **The inscription-compatibility test is inverted, and NPEs on the case it is meant to allow.**
    - `ItemObject.java:2200`:
      `return toMerge.getNote() != null || this.getNote() != null || toMerge.getNote().equals(this.getNote());`
    - C (`obj-pile.c:504`): `return !obj1->note || !obj2->note || obj1->note == obj2->note;` — compatible when *either*
      side is uninscribed, or the inscriptions match.
    - Both null tests are the wrong way round, so any inscribed item is declared compatible with anything; and two
      uninscribed items fall through to `toMerge.getNote().equals(...)` on a null receiver.

## `ItemObject.java` — `objectAbsorb`

- [ ] **The absorbed object and its known counterpart are never removed or deleted.**
    - `ItemObject.java:2206-2219` does the count merge and the `pileExcise`, then stops.
    - C (`obj-pile.c:685-692`) follows with `delist_object` + `object_delete` on `known`, and
      `object_delete(cave, player->cave, &obj2)` — the latter is what unlinks the emptied stack from
      `p->gear`.
    - The emptied stack therefore stays in the gear at its old count, so `combinePack` duplicates items rather than
      combining them, and the pack never shrinks.

- [ ] **The zero-grid test compares references instead of coordinates.**
    - `ItemObject.java:2216`: `if (known.getGrid() != Loc.zero)`.
    - `Loc` is a class (`cave/Loc.java:40`) with `zero` as one particular instance (`cave/Loc.java:58`); C's
      `loc_is_zero` compares the x and y values.
    - Any independently-constructed `Loc(0, 0)` fails the identity test, so a known object at the origin is excised from
      the pile when C would leave it alone.

## `ItemObject.java` — `objectAbsorbMerge`

- [ ] **The effect copy targets the wrong object and reads the wrong source.**
    - `ItemObject.java:2227`: `this.effect = new ArrayList<>(toAbsorb.getKnown().getEffect());`
    - C (`obj-pile.c:587`): `obj1->known->effect = obj1->effect;` — it writes the *known* object's effect, and the value
      it writes is the surviving object's own real effect.
    - The port overwrites the surviving object's real effect list with the absorbed object's known effect, which is a
      knowledge-to-reality write in the wrong direction.

## `ItemObject.java` — `objectAbsorbPartial`

- [ ] **The non-quiver branch computes the second stack's new size from `largest`.**
    - `ItemObject.java:2291`: `newItm2Size = largest - difference;`
    - C (`obj-pile.c:663`): `newsz2 = smallest - difference;`
    - The pair no longer conserves the total count: items are created or destroyed on every pack-to-pack partial absorb.

- [ ] **Two of C's assertions become mid-method `return`s that leave both stacks unchanged.**
    - `ItemObject.java:2277` and `ItemObject.java:2286` return when
      `newItm2Size >= maxStack`.
    - C (`obj-pile.c:648`, `obj-pile.c:658`) asserts and otherwise carries on to the
      `distribute_charges` / number assignment.
    - The port skips `distributeCharges` and both `setNumber` calls, so `combinePack` then runs its
      "ensure numbers align" writes over stacks that were never actually split.

- [ ] **The second of those checks tests the wrong variable.**
    - `ItemObject.java:2286` checks `newItm2Size`, in the branch where mode2 is the quiver.
    - C (`obj-pile.c:658`) asserts on `newsz1` there — the one that was just computed as
      `(largest + smallest) - limit`.

- [ ] **Store mode returns silently instead of being an impossible state.**
    - `ItemObject.java:2263`: `if (... OSTACK_STORE ...) return;`
    - C (`obj-pile.c:631`) asserts neither mode is `OSTACK_STORE`; the doc comment on the function states it as a
      precondition.
    - As above, the silent return leaves the caller believing a split happened.

## `ItemObject.java` — `mergeable`

- [ ] **Both quiver limits use the slot count instead of the slot size.**
    - `ItemObject.java:2188`: `total > GameConstants.getCarryCapQuiverSize()`.
    - `ItemObject.java:2190`: `getCarryCapQuiverSize() / getCarryCapThrownQuiverMult()`.
    - C (`obj-pile.c:521`, `obj-pile.c:525`) uses `z_info->quiver_slot_size` in both.
    - Ammo stacks are capped at 10 rather than 40, and thrown items at 2 rather than 8.

- [ ] **The quiver check sits outside the store-mode guard.**
    - `ItemObject.java:2186` opens a new `if` after the `OSTACK_STORE` block has closed at
      `ItemObject.java:2183`.
    - C (`obj-pile.c:513-530`) nests the quiver check inside `if (!(mode & OSTACK_STORE))`.
    - With both flags set the port applies a quiver limit C would waive.

- [ ] **The max-stack limit is read from the wrong object.**
    - `ItemObject.java:2182`: `toMerge.getKind().getBase().getMaxStack()`.
    - C (`obj-pile.c:515`): `obj1->kind->base->max_stack`, i.e. the receiver.
    - Harmless while both kinds are identical, but `mergeable` is called *before* `similar` has established that, and it
      NPEs if `toMerge.getKind()` is null — which is the very condition
      `combinePack` filters for on its own item.

## `ItemObject.java` — `ignoreLevelOf`

- [ ] **The assessed-notice test calls `Flag.on`, a mutator, and reads its "did this change"
  return as the answer.**
    - `ItemObject.java:2144`: `if (known.notice.on(ObjectNotice.OBJ_NOTICE_ASSESSED) && !isArtifact())`.
    - `Flag.on` (`channel/utils/Flag.java:228`) adds the flag and returns `true` only when it was *not* already present.
    - Two faults in one call: the item is permanently marked as assessed as a side effect of asking a question, and the
      test is satisfied exactly when C's `obj->known->notice & OBJ_NOTICE_ASSESSED`
      (`obj-ignore.c:507`) would be false.

- [ ] **The jewellery branch reads real modifiers but known combat bonuses.**
    - `ItemObject.java:2113`: iterates `this.modifiers`.
    - `ItemObject.java:2119-2122`: reads `known.toHit`, `known.toDam`, `known.toAC`.
    - C (`obj-ignore.c:475-484`) reads `obj->known->modifiers[i]` as well as the known bonuses.
    - Unlearned modifiers therefore leak into the ignore decision, while the bonuses beside them do not.

---

## `MonsterUtils.java`

- [ ] **`showMonsterMessages` is an empty stub.**
    - `MonsterUtils.java:86`.
    - `noticeStuff` clears `PN_MON_MESSAGE` (`Player.java:1832`) before calling it, so the queued monster messages are
      dropped rather than deferred.
