# PlayerKnowledge.java — stage 1 items 3 to 5, expanded

Written 2026-10-04. Every Java line below was re-read this turn. The C was re-read in `obj-knowledge.c`
and `obj-init.c` (`/home/rowan/Desktop/Angband-4.2.6/src/`).

## Summary

- **Item 3 (`learnInnate`):** fixed in the code. Only its Javadoc is stale.
- **Item 4 (`learnFlag` and the dropped trailing updates):** unchanged. A real but unreachable divergence.
- **Item 5 (`knowObject(Player, Curse)`):** my original finding was **wrong**. It is retracted below. The code matches
  C. Three Javadoc blocks claim the opposite and need rewriting.

---

## Item 3 — `learnInnate` trailing update

### What C does

- `player_learn_innate` (`obj-knowledge.c`) loops the race's resistances, then the race's object flags.
- Each `player_learn_rune` call is silent (`message` false).
- It then ends with an unconditional `update_player_object_knowledge(p)`.
- That function also autoinscribes the pack and signals `EVENT_INVENTORY` and `EVENT_EQUIPMENT`.

### Why it mattered

- Human has no resistances or object flags in `p_race.txt`.
- For a Human, no `player_learn_rune` call learns anything, so none of them updates.
- C's trailing call was the only update that ran, and the only thing that signalled the display.

### What the code does now

- `PlayerKnowledge.java:1302` ends `learnInnate` with `updateObjectKnowledge(player);`, after both loops.
- That matches C's placement and its unconditional nature.
- The caller is `PlayerBirth.java:2514`, which runs after `learnAllRunes` and the combat-rune hack, as in C.

### What is left

- The Javadoc at `PlayerKnowledge.java:1276-1279` still says C's trailing call is "dropped here as in the other
  wrappers". It is not dropped any more.
- Its reasoning, that a race with nothing innate "recomputes a knowledge state that never changed", ignores the
  autoinscribe and the two signals. Those are the reason the call is kept.
- **Action:** rewrite that paragraph in the stage 2 pass. It should say the call is kept, because the signals fire even
  when nothing was learned.

---

## Item 4 — `learnFlag` guard, and the dropped trailing updates

### What C does

- `player_learn_flag` has no guard. It calls `player_learn_rune`, then `update_player_object_knowledge`.
- If the flag was new, `player_learn_rune` updates, so C updates twice.
- If the flag was already known, `player_learn_rune` does nothing, but the trailing update still runs.
- So C always autoinscribes and signals the inventory and equipment events.

### What the code does

- `PlayerKnowledge.java:838-842`: `learnFlag` returns at once if `flagIsKnown(flag)`, else calls `learnRune`.
- For an already-known flag, nothing is learned and nothing is signalled.
- For a new flag, the update and signals run once. C runs them twice.

### The two sub-cases

- **Already-known flag.** This is a real difference: C autoinscribes and signals, Java does nothing. The knowledge
  values are identical, which is the only thing the Javadoc at `PlayerKnowledge.java:827-833`
  argues. The signals are not covered by that argument.
- **New flag.** Once or twice makes no observable difference. The update is a recomputation.

### The same pattern in `learnBrand` and `learnSlay`

- Both are guarded and drop C's trailing update (`PlayerKnowledge.java:769-775` and `:795-800`).
- Here nothing is lost. The guard passes only when the rune is unknown, so `learnRune` always learns, updates and
  signals once.
- The Javadoc argument at `PlayerKnowledge.java:757-764` is sound for these two.

### Reachability

- C's only caller of `player_learn_flag` is the failed uncursing in `effect-handler-general.c`.
- No Java caller of `PlayerKnowledge.learnFlag` exists in `src/main` yet.
- The difference cannot show until that effect handler is ported.

### Choices, and what each costs

- **Match C exactly:** drop the guard and call `updateObjectKnowledge` after `learnRune`. The cost is one duplicate
  update when a flag is new, which is C's behaviour.
- **Keep the guard:** accept that an already-known flag causes no autoinscribe or signals. State that in the Javadoc, in
  place of "nothing is lost".
- Either way, the Javadoc at `PlayerKnowledge.java:832-833` should stop claiming the update recomputes
  "identical values" as the whole story.
- **Action:** pick one and record the reasoning. This is a decision for you, and the port does not need it before the
  uncursing effect is ported.

---

## Item 5 — `knowObject(Player, Curse)` — RETRACTED

### What I reported earlier, and why it was wrong

- I reported that C returns at "Curse object structures are finished now", so a curse never reaches the effect
  assignment or the fully-known block, and that the Java carrying on was a divergence.
- I took that from the comment and from `Curse.java` and the existing Javadoc. I did not check what a curse object's
  `kind` is at runtime. That was the error.

### What C really does

- `obj-init.c`, function `write_curse_kinds`, runs at the end of init, after `curse_object_kind` is defined.
- For every curse it sets `curse->obj->kind = curse_object_kind`, and gives the curse object a known object with the
  same kind.
- It also sets `OBJ_NOTICE_ASSESSED` on that known object, with the comment "Mark it as touched so it can be fully
  known".
- So in a running game a curse object's `kind` is not null, and `if (!obj->kind) return;` in
  `player_know_object` never fires for a curse.

### Walking a curse through C's `player_know_object`

- The head guards pass: the object exists, it has a known object, and the kinds match.
- The "distant object" return does not fire, because `OBJ_NOTICE_ASSESSED` is set.
- Combat details, modifiers, elements and flags are copied, as in Java.
- Brands and slays are skipped, since the curse object has none.
- Ego is null, so `player_knows_ego` gives false and the known ego is null.
- The kind is `<curse object>`, tval `none`: not jewellery, and its index is below `ordinary_kind_max`.
- The effect test's second arm, non-wearable with no flavour, passes, so `known->effect = obj->effect`.
- The report block is silent, because `seen` is still true.
- The fully-known block runs.

### Consequence for the Java

- The effect assignment at `PlayerKnowledge.java:1235` matches C. It is unconditional because the arm that gates it is
  always true for a curse.
- The fully-known block at `PlayerKnowledge.java:1237-1249` matches C.
- `object_has_standard_to_h` has a null-kind hack, but a curse's kind is non-null at runtime. C therefore uses
  `obj->to_h == 0` for a curse, which is what `Curse.hasStandardToH` (`Curse.java:552-554`) does.
- So the claim that the three curses with a to-hit penalty can never be fully known in C is also wrong. They can.

### Three Javadoc blocks that state the opposite and need rewriting

- `PlayerKnowledge.java:1147-1151`: says C's short path has no early returns and omits the dice block. The head guards
  do run in C, but all pass for a curse.
- `PlayerKnowledge.java:1173-1182`: the "Outstanding" paragraph, saying C returns before the effect and the fully-known
  block.
- `Curse.java:530-545`: `hasStandardToH` says C answers true unconditionally and never copies the three to-hit
  penalties.
- `Curse.java:735-742`: `isFullyKnown` says C never asks this question of a curse.
- That is four blocks in two files. The two `Curse.java` blocks are outside this port's file, so I list them here and do
  not touch them.

### The one real residual difference

- `PlayerKnowledge.java:1201-1203` builds the known modifier map over every `ObjectModifier`, including `OM_NONE`
  and `OM_MAX`. The item version skips them at `PlayerKnowledge.java:233`.
- C loops `0 .. OBJ_MOD_MAX - 1`, so its array has no sentinel slots.
- `Curse.isFullyKnown` (`Curse.java:754-759`) iterates the curse's own modifier keys, so the extra entries are not read
  there. I have not checked other readers of `knownModifiers` this turn.
- **Action:** skip the two sentinels in the loop at `PlayerKnowledge.java:1201`, as the item version does.

### What this means for the earlier report

- Item 5 is withdrawn as a C mismatch. Only the sentinel keys remain.
- The earlier "Out of scope" note about `Curse` is also wrong in the same way: `Curse.hasStandardToH` is not a
  deliberate divergence from C.

## Next step

- Item 3: stage 2 Javadoc only. Item 4: your decision. Item 5: skip the sentinels at `PlayerKnowledge.java:1201`.
- Then type `/port PlayerKnowledge.java` to re-check everything and start Javadoc.
