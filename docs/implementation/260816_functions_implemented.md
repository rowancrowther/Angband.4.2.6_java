# 260816 — C functions implemented in the `player_know_object` work

Every C function this batch of work stands in for, where it landed in Java, and whether it matches. C references are
`/home/rowan/Desktop/Angband-4.2.6/src/`.

| C function                                                                                | C site                 | Java                                      | Status                                                                                              |
|-------------------------------------------------------------------------------------------|------------------------|-------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `player_know_object`                                                                      | `obj-knowledge.c:1018` | `Player.knowObject` (530)                 | **needs work**                                                                                      |
| `object_set_base_known`                                                                   | `obj-knowledge.c:816`  | `Player.setBaseKnown` (888)               | **needs work**                                                                                      |
| `object_flavor_aware`                                                                     | `obj-knowledge.c:2262` | `Player.flavourAware` (739)               | **correct** — commented and tested 260816 (store loop deferred to ch. 8; `Square.lightSpot` a stub) |
| `player_knows_ego`                                                                        | `obj-knowledge.c:471`  | `Player.knowsEgo` (832)                   | **needs work**                                                                                      |
| `object_non_curse_runes_known`                                                            | `obj-knowledge.c:674`  | `Player.nonCurseRunesKnown` (783)         | **needs work**                                                                                      |
| `gear_to_label`                                                                           | `obj-gear.c:443`       | `Player.gearToLabel` (709)                | **needs work**                                                                                      |
| `object_is_carried`                                                                       | `obj-gear.c`           | `Player.isCarried` (735)                  | **correct** — commented and tested 260816                                                           |
| `square_holds_object`                                                                     | `cave-square.c`        | `Square.holdsObject` (166)                | **correct** — commented and tested 260816                                                           |
| `square_light_spot`                                                                       | `cave-view.c`          | `Square.lightSpot` (1155)                 | **needs work** (empty stub)                                                                         |
| `object_is_equipped`                                                                      | `obj-gear.c`           | `PlayerBody.itemIsEquipped` (75)          | **correct** — commented and tested 260816                                                           |
| `equipped_item_slot`                                                                      | `player-calcs.c`       | `PlayerBody.equippedItemSlot` (120)       | **needs work** (NPE on empty slot)                                                                  |
| `p->upkeep->notice \|= …`                                                                 | macro                  | `PlayerUpkeep.orNoticeFlag` (453)         | **correct** — commented and tested 260816                                                           |
| `p->upkeep->quiver` / `->inven`                                                           | field reads            | `PlayerUpkeep.getQuiver` / `getInventory` | **correct** — commented and tested 260816                                                           |
| `ego->everseen`, `->flags`, `->modifiers`, `->el_info`, `->brands`, `->slays`, `->curses` | field reads            | `EgoItem` accessors (291–317)             | **correct** — commented and tested 260816                                                           |
| `kind_ignore_when_aware`                                                                  | `obj-ignore.c`         | `ObjectKind.setIgnoredAware` (785)        | **correct**                                                                                         |
| `kind_is_ignored_unaware`                                                                 | `obj-ignore.c`         | `ObjectKind.isIgnoredUnaware` (772)       | **correct**                                                                                         |
| `object_has_standard_to_h`                                                                | `obj-knowledge.c:580`  | `ItemObject.hasStandardToH` (1134)        | **correct** (pre-existing, 260815)                                                                  |
| `object_fully_known`                                                                      | `obj-knowledge.c:750`  | `ItemObject.isFullyKnown` (609)           | **correct** (pre-existing, 260815)                                                                  |

---

## `Player.knowObject` — block by block

C order is followed faithfully, so the blocks line up one-for-one.

| Block                                  | Java    | Status                                                                                                         |
|----------------------------------------|---------|----------------------------------------------------------------------------------------------------------------|
| Null / no-known / kind-mismatch guards | 534–537 | correct                                                                                                        |
| Distant object → base only             | 542–545 | correct                                                                                                        |
| Dice, AC, pval                         | 548–552 | correct                                                                                                        |
| Combat details                         | 555–558 | **needs work**                                                                                                 |
| Modifiers                              | 561–568 | **needs work**                                                                                                 |
| Elements                               | 571–582 | **needs work**                                                                                                 |
| Object flags                           | 585–586 | **needs work**                                                                                                 |
| Curse-object early return              | 589–590 | correct (inverted condition fixed 260816)                                                                      |
| Brands                                 | 593–610 | **needs work**                                                                                                 |
| Slays                                  | 613–629 | correct                                                                                                        |
| Curses                                 | 632–653 | **needs work**                                                                                                 |
| Ego / jewellery / special artifact     | 656–671 | correct                                                                                                        |
| Effect                                 | 674–678 | correct                                                                                                        |
| Report on new stuff                    | 681–698 | correct in shape; blocked by the `description()` stub, and the first message is missing C's trailing full stop |
| Fully-known copy-back                  | 701–706 | **needs work**                                                                                                 |

### Combat details (555)

```java
known.setToAC(item.getToAC() *itemKnowledge.

getAc());
```

C is `obj->known->to_a = p->obj_k->to_a * obj->to_a;` — the multiplier is `to_a`, not `ac`. They are separate
one-or-zero knowledge bits, so an object whose AC rune is known but whose `to_a` rune is not currently leaks its
enchantment. `KnownObject` has the `toA` field already; it has no `getToA()`.

### Modifiers (561–568)

The loop zeroes every modifier, then copies across **every** modifier the item carries. C copies a modifier only when
`p->obj_k->modifiers[i]` says the player can read it:

```c
if (p->obj_k->modifiers[i]) obj->known->modifiers[i] = obj->modifiers[i];
else obj->known->modifiers[i] = 0;
```

The gate is `itemKnowledge.getModifier(modifier)`, which exists and is unused here. As written, every modifier on every
object is known to the player.

### Elements (571–582)

Three separate problems:

1. The second loop walks `knownElements.keySet()` — the map has an entry for *every* element, so the
   `Boolean` value is what says whether it is known, and it is never read. C tests
   `p->obj_k->el_info[i].res_level == 1`.
2. One `zero` `ElementInfo` instance is put into every slot of `knownElInfo`. They alias; setting a res level on one
   sets it on all. `ElementInfo.copy()` exists for exactly this.
3. `knownElInfo.put(key, itemElInfo.get(key))` stores the item's own live `ElementInfo`, so the known copy and the real
   one are the same object from then on — knowledge and truth can no longer differ, which is the whole point of the
   known object.

`ELEM_NONE` / `ELEM_MAX` are skipped in the first loop but not the second.

### Object flags (585–586)

```java
Flag<ObjectFlag> knownFlags = known.getKnownFlags();
knownFlags.copyFrom(item.getKnownFlags());
```

`getKnownFlags()` reaches `known.known` — the known object's *own* known counterpart, which is null — so this is an NPE
on the left and reads the wrong set on the right. It also returns a copy (`getObjectFlags()` copies), so a successful
write would be discarded anyway.

C wipes the known set and switches on each flag the player knows *and* the item has:

```c
of_wipe(obj->known->flags);
for (flag = of_next(p->obj_k->flags, FLAG_START); flag != FLAG_END; flag = of_next(p->obj_k->flags, flag + 1))
    if (of_has(obj->flags, flag)) of_on(obj->known->flags, flag);
```

That is an intersection of `itemKnowledge.getFlags()` with `item.getFlags()`, written to the known object's own flag
set — which needs a live accessor that does not yet exist.

### Curse-object early return (589) — fixed 260816

Was `if (itemKind != null) return;`, the inverse of C's `if (!obj->kind) return;`. A curse object — the only thing with
a null kind — is what should stop here; every real object continues. Now reads
`if (itemKind == null) return;` and matches.

Worth keeping the note: while it was inverted, everything below it was dead code for real objects and ran only for curse
objects, which then NPE'd on `itemKind.isEverseen()` at 665. Every finding below was theoretical until this flipped, and
is now live.

### Brands (593–610)

```java
if (!known.getBrands().contains(brand)) {
    known.getBrands().add(brand);
    knownBrand = true;
}
```

`knownBrand` is set only when the brand was *newly* added. On the second call for the same object every known brand is
already present, so `knownBrand` stays false and the block below clears the lot. C sets its `known_brand` on every known
brand, not only new ones — move `knownBrand = true;` outside the `contains` test (or drop the test; `Set.add` is
idempotent).

The `else if (known.getBrands() != null)` guard is dead — `getBrands()` was already dereferenced.

### Slays (613–629)

Matches C. `knowSlay` is set unconditionally inside the known branch, which is what the brands block should do.

### Curses (632–653)

`ItemObject.getCurses()` returns `Collections.unmodifiableMap(curses)`. `put`, `remove` and `clear`
all throw `UnsupportedOperationException`, so this block cannot run at all once reachable.

Beyond that, the not-known branch does:

```java
entry.curseData().setPower(0);
```

`entry` came from `itemCurses` — the *item's* map — so this zeroes the real curse's power, not the known copy's. C
zeroes `obj->known->curses[i].power` and leaves `obj->curses[i]` alone. This destroys game state rather than hiding it.

The `if (!knownCursed)` clear matches C, and so does the `else if` on a null curse map.

### Fully-known copy-back (701–706)

```java
for(ElementEnum element :ElementEnum.

values()){
        knownElInfo.

put(element, itemElInfo.get(element));
        known.

setFlags(item.getFlags());
        }
```

`known.setFlags(...)` belongs outside the loop, and it assigns the item's live `Flag` object rather than copying it —
from then on the known flags and the real flags are one set. C does
`of_wipe(obj->known->flags); of_copy(obj->known->flags, obj->flags);`. The element copy has the same aliasing problem as
the earlier block and does not skip `ELEM_NONE` / `ELEM_MAX`.

---

## `Player.setBaseKnown` (888)

| Block                                        | Status         |
|----------------------------------------------|----------------|
| kind / tval / sval / weight / number         | correct        |
| dd, ds, ac                                   | **needs work** |
| standard to-hit                              | **needs work** |
| launcher pval                                | correct        |
| aware flavours and unflavoured non-wearables | correct        |
| standard activations                         | correct        |

C only fills the generic dice/AC when the known object has **nothing** there yet:

```c
if (!obj->known->dd) obj->known->dd = obj->kind->dd * p->obj_k->dd;
```

Java tests `if (known.getDamageDice() != 0)` — the opposite. As written it overwrites a real, learned value and leaves
an empty one empty.

The source of the value is wrong too: `itemKind.getToD().getDice()` reads the kind's *to-damage*
random value, not its damage dice. `ObjectKind` has `damageDice` and `damageSides` fields but no getters for them; those
are what C's `obj->kind->dd` / `->ds` mean.

`known.setToHit(itemKind.getToH().getBase())` is correct, including the `hasStandardToH` gate.

The final block sets `known.setEffect(itemKind.getEffect())` where C sets `obj->effect`. In practice the same list, but
the item's effect is what C reads.

## `Player.knowsEgo` (832)

| Block                             | Status         |
|-----------------------------------|----------------|
| null ego                          | correct        |
| all flags known                   | **needs work** |
| all modifiers known               | **needs work** |
| all elements known                | correct        |
| all brands / slays / curses known | correct        |

```java
if (knownFlags.isSubset(egoFlags)) return false;
```

C is `if (!of_is_subset(p->obj_k->flags, ego->flags)) return false;`, and `Flag.isSubset` has the same argument order
and meaning as `flag_is_subset` (true when every flag in the argument is set in the receiver). So the test is inverted:
it currently rejects an ego precisely when the player *does* know all its flags.

In the modifier loop:

```java
Random egoModifier = ego.getModifier(modifier);
if (egoModifier == null) return false;
```

There is no such bail-out in C. An ego with no entry for a modifier has a zero `random_value`, giving
`modmax == modmin == 0`, and the loop moves on. Returning false here means any ego that does not list every modifier is
never recognised. Treat a null as zero and `continue`.

`item.getModifiers().get(modifier) != 0` will also NPE-on-unbox if the item has no entry for that modifier.

## `Player.nonCurseRunesKnown` (783)

| Block          | Status         |
|----------------|----------------|
| null guards    | correct        |
| combat details | correct        |
| modifiers      | **needs work** |
| elements       | correct        |
| brands / slays | correct        |
| flags          | **needs work** |

```java
return !knownFlags.isSubset(itemFlags);
```

C ends `if (!of_is_subset(obj->known->flags, obj->flags)) return false; return true;` — i.e. the result is
`knownFlags.isSubset(itemFlags)`, unnegated. As written the function answers "all runes known" exactly when they are
not.

The modifier loop iterates `itemModifiers.keySet()` and treats a missing known entry as a mismatch. C compares all
`OBJ_MOD_MAX` slots, where absent means zero on both sides; a modifier the item does not carry and the known object does
not carry should compare equal, not fail. Worth walking
`ObjectModifier.values()` and comparing `getOrDefault(key, 0)` on both.

## `Player.gearToLabel` (709)

The label string and the equipment and inventory branches match C. The quiver branch does not:

```java
return (Character.toChars(index)[0]);
```

C returns `I2D(i)` — the *digit* `'0' + i`. `Character.toChars(0)` is `'\0'`, `toChars(1)` is ``
and so on, so quiver slots come back as control characters. Wants `(char) ('0' + index)`.

## `Player.flavourAware` (739) — correct

A faithful port of `object_flavor_aware`, including the aware-already early return, the ignore fixup, the `PN_IGNORE`
notice, the gear pass and the floor sweep from `(1,1)`. Two knowingly-deferred pieces:
the store stock pass is marked for chapter 8, and `Square.lightSpot` is an empty stub so the display refresh is a no-op
for now. Neither is a correctness error in this function.

## `Player.isCarried` (735) — correct

`gear.contains(item)` is `pile_contains(p->gear, obj)`.
