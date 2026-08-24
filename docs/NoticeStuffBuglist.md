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
calculation below.

Re-verified 260824: everything else on the original list is now fixed and has been cut. What remains is what was still
open at that re-read.

---

## `Player.java` — stub in the chain

- [ ] **`calcInventory` is an empty stub.**
    - `Player.java:4231`.
    - `combinePack` calls it as its last act before signalling the redraws (`obj-gear.c:1302`), and
      `invenCanStackPartial`'s quiver branch exists specifically to avoid combining something
      `calc_inventory` would then have to split.
    - With it empty, `upkeep->inven` and `upkeep->quiver` are never rebuilt after a combine.

---

## `ItemObject.java` — `objectAbsorbPartial`

- [X] **The mode2-quiver branch checks the wrong variable.**
    - `ItemObject.java:2299` checks `newItm2Size`, in the branch where mode2 is the quiver.
    - C (`obj-pile.c:658`) asserts on `newsz1` there — the one just computed at
      `ItemObject.java:2297` as `(largest + smallest) - limit`, i.e. `newThisSize`.
    - `newItm2Size` in that branch is `limit`, which cannot exceed `max_stack`, so the check can never fire and the real
      overflow goes unnoticed.
    - The matching check in the other branch (`ItemObject.java:2286`) is now on the right variable.

- [X] **Both checks read `max_stack` from `item2`'s kind rather than the receiver's.**
    - `ItemObject.java:2286` and `ItemObject.java:2299`: `item2.getKind().getBase().getMaxStack()`.
    - C (`obj-pile.c:648`, `obj-pile.c:658`) reads `obj1->kind->base->max_stack` in both — the receiver, i.e. `this`.
    - Harmless while the two kinds are identical, which `objectStackable` will have established by the time this runs;
      noted because it is a silent divergence, and because the message text at
      `ItemObject.java:2300` still says `this.getKind().getName()` while the value beside it comes from `item2` — the
      pair disagreeing is the tell.

---

## `MonsterUtils.java`

- [ ] **`showMonsterMessages` is an empty stub.**
    - `MonsterUtils.java:86`.
    - `noticeStuff` clears `PN_MON_MESSAGE` (`Player.java:1832`) before calling it, so the queued monster messages are
      dropped rather than deferred.
