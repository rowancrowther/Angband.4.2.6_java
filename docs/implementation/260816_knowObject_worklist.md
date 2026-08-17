# 260816 — `player_know_object` worklist

Closed list. One checkbox per function; a function appears only if it needs work; nothing gets added. Tick and move on.
Full reasoning for each is in `260816_functions_implemented.md`.

Thirteen items.

## `Player.java`

- [ ] **`knowObject`** — seven blocks diverge from C. Not split further, but in line order:
    - `555` to-AC uses `getAc()` as the multiplier; C uses `to_a`. Needs `KnownObject.getToA`.
    - `561–568` copies every modifier the item has; C copies only ones `itemKnowledge` says are readable.
    - `571–582` reads the element map's keys instead of its boolean values; puts one shared `zero`
      instance in every slot; stores the item's live `ElementInfo` into the known object. Use
      `ElementInfo.copy()`.
    - `585–586` writes to a copy, and reads `known.known`. Wants the intersection of
      `itemKnowledge.getFlags()` with `item.getFlags()`, written via `setFlagsTo`.
    - `593–610` brands: `knownBrand` is only set when a brand is *newly* added, so a second call over the same object
      clears the lot. Set it on every known brand.
    - `632–653` curses: writes to an unmodifiable map, and `entry.curseData().setPower(0)` zeroes the item's real curse
      rather than the known copy.
    - `701–706` fully-known: `setFlags` is inside the element loop, and it unions where C wipes and copies — wants
      `setFlagsTo` outside the loop. Element copy aliases and does not skip
      `ELEM_NONE`/`ELEM_MAX`.
- [ ] **`setBaseKnown`** — `dd`/`ds`/`ac` guards are inverted (C fills only when the known value is zero), and the
  source is wrong: `getToD().getDice()` reads the kind's to-damage dice, not its damage dice. Needs
  `ObjectKind.getDamageDice` / `getDamageSides`.
- [ ] **`knowsEgo`** — flag subset test is inverted; the `egoModifier == null` bail-out has no counterpart in C and
  rejects any ego that does not list every modifier (treat null as zero and continue);
  `item.getModifiers().get(modifier) != 0` unboxes a possible null.
- [ ] **`nonCurseRunesKnown`** — returns the negation of its answer (`return !knownFlags.isSubset(…)`). Modifier loop
  should walk all of `ObjectModifier.values()` comparing `getOrDefault(key, 0)`.
- [ ] **`gearToLabel`** — quiver branch returns `Character.toChars(index)[0]`, a control character. C's `I2D(i)` is the
  digit `(char) ('0' + index)`.

## `ItemObject.java`

- [ ] **`getKnownFlags`** — returns a copy and reads `known.known`, so writes through it are discarded. **This is the
  one failing test in the suite** (`PlayerRuneLearningTest > equipLearnFlag`). Now that `getFlags`/`setFlag`/
  `setFlagsTo` exist, this member should go and callers should say
  `getKnown().setFlag(…)`.
- **Need to put in place named mutators** **`getCurses`** — returns `Collections.unmodifiableMap`, and `knowObject` uses
  it as a write target. Either return live, or add named mutators as was done for the flags.
- **Deferred** **`description`** — stub returning `{DESCRIPTION_TAG}`. Blocks `knowObject`'s
  "You have …" / "On the ground: …" messages, and `equipLearnFlag`'s flag
  message. [Put off development until Chapter 7 is implemented. Set on 260816.]

## `KnownObject.java`

- [x] **`getToA`** — does not exist. The `toA` field does. Needed by `knowObject:555`.

## `ObjectKind.java`

- [x] **`getDamageDice`** — does not exist. The `damageDice` field does. Needed by `setBaseKnown`.
- [x] **`getDamageSides`** — does not exist. The `damageSides` field does. Needed by `setBaseKnown`.

## `PlayerBody.java`

- [x] **`equippedItemSlot`** — dereferences `slot.getItem()` with no null guard, so it NPEs on the first empty slot.
  `itemIsEquipped` next to it has the guard.

## `Square.java`

- **Deferred** **`lightSpot`** — empty stub. `flavourAware`'s floor sweep computes the right squares and then redraws
  none of them. [Put off development until Chapter 4 is implemented. Set on 260816.]
