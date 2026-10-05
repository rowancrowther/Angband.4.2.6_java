# ItemObject.java Stage 1: out-of-scope code worth a look

Source: the nine read-only Stage 1 agents of 2026-10-02, one per batch of `docs/ItemObject_port_batches.md`. Their code
and comment findings are all settled; what is left is code the agents noticed outside the methods being checked. Claude
re-read each file below on 2026-10-03 and expanded each finding. Line numbers are as read that day and shift as you
edit.

None of these is a Stage 1 failure for `ItemObject.java`. Each lives in another file, or reaches `ItemObject` only
through a caller, so none blocks a batch. They are yours to fix, or to accept and record as divergences.

## 1. `ObjectUtils.copyCurses` differs from C in two ways (batch 9)

- **Where:** `ObjectUtils.java:1097`. Its only caller is `objectPrep` at `ObjectUtils.java:1039`, which passes
  `kind.getCurses()`.

### 1a. It does not skip power-0 source entries

- **What C does:** `copy_curses` (`obj-curse.c`) loops over every curse index and starts with
  `if (!source[i]) continue;`. A source entry at power 0 is "no curse" and is passed over, so it writes neither a power
  nor a timeout.
- **What Java does:** the loop at `ObjectUtils.java:1103-1119` copies every key in `source`. A power-0 entry is written
  to the destination with a freshly rolled timeout, so the object ends up holding a curse of power 0.
- **Why it matters:** in this port "off" means the key is absent. `setCursePower` removes an entry at power 0 and
  `removeCurse` removes the key, so a power-0 entry from `copyCurses` is the one way left to put an "off" curse in the
  map. `applyCurseAttributes` (`ItemObject.java:4776-4779`) already skips power-0 entries, which shows the rest of the
  code expects them to be possible, but other readers of `getCurses()` would count it as a curse.
- **Reach:** `lib/gamedata/object.txt` only has curse powers such as `curse:teleportation:100`, so the shipped data
  can't hit this. A hand-built kind, or a future caller with a flattened curse array, could.
- **What a fix needs:** skip a source entry whose power is 0 before it touches the destination map or rolls a timeout.
  Rolling the timeout for a skipped entry would also draw from the random stream, which C does not do.

### 1b. It merges in a `HashMap`, so insertion order is lost

- **What C does:** `obj->curses` is an array indexed by curse, so anything that walks it sees the curses in index order
  every time.
- **What Java does:** `ObjectUtils.java:1101` copies the object's curses into a `HashMap`. `Curse` has no `equals` or
  `hashCode`, so the keys hash by identity, and the iteration order of that map can differ from one run to the next.
  `setCurses` (`ItemObject.java:6898`) then copies it into a `LinkedHashMap`, which keeps whatever order the `HashMap`
  produced.
- **Why it matters:** `ItemObjectCursesTest` already states that the curse map promises no ordering, so nothing is
  broken today. The risk is a later port that lists an object's curses, for example in the object description or the
  knowledge screens, and prints them in a different order each game.
- **What a fix needs:** build the merged map as a `LinkedHashMap`. If you want C's index order, walk
  `ObjectRegistry.getCurses()` (which is in index order, as `applyCurseAttributes` relies on) and take each curse's data
  from the merged result. Either way, decide whether the "promises no ordering" test should be widened.

## 2. `ObjectKind.canBrowse` has no guard for a class with null magic (batch 8)

**Decision (2026-10-03, Rowan):** `PlayerClass.magic` is always `ClassMagic.NONE` for a class without magic, never
`null`.

- **Where:** `ObjectKind.java:1013-1020`. `ItemObject.canBrowse` (`ItemObject.java:6455`) delegates to it, and
  `earlierObject`
  calls it at `ItemObject.java:1039-1040`.
- **What C does:** `obj_kind_can_browse` (`obj-util.c`) loops over `class->magic.num_books`. A class with no spells has
  `num_books` of 0, so the loop runs zero times and the answer is false.
- **Done in code:** the `PlayerClass` constructor (`PlayerClass.java:135-138`) now turns a null `magic` into
  `ClassMagic.NONE`. With that, `canBrowse` walks an empty list and answers false, as C does. About eleven callers of
  `getMagic()` (`PlayerCalcs`, `PlayerBirth`, `PlayerMagic`, `PlayerClass.magicRealm`, `Command.java:900`) also read it
  unguarded and are now safe for a hand-built class too.
- **Tests:** `PlayerClassNoMagicTest` pins the constructor, `copy()`, and `canBrowse` on a class built with null.
- **Still to do (yours):**
    - Javadoc that still says `null`: the field at `PlayerClass.java:93`, the `@param magic` at `PlayerClass.java:113`,
      and the getter at `PlayerClass.java:159`.
    - `PlayerClass.copy()` (`PlayerClass.java:339`) still does `if (this.magic == null) this.magic = ClassMagic.NONE;`.
      It can no longer be true, and it writes to `this` inside a copy, so it can go.
    - The null check at `Command.java:749-751` is now dead.
    - Ten test builders pass `null` as the last argument of `new PlayerClass(...)`. They still work, and need no change.
- **Minor, unchanged:** C loops `num_books`, Java loops the book list, and `ClassMagicAssembler` does not check the two
  agree. Nothing in the shipped data separates them.

## 3. `copyCurses` calls `initCurses()` straight before `setCurses(...)` (batch 8)

- **Where:** `ObjectUtils.java:1122-1123`.
- **What happens:** `initCurses` (`ItemObject.java:6921`) assigns a new empty `LinkedHashMap` to `curses`, and
  `setCurses` (`ItemObject.java:6898`) then assigns `new LinkedHashMap<>(destCurseMap)` over it. The first assignment is
  thrown away unread, so the pair does the same work as `setCurses` alone.
- **Why the call is there:** the `copyCurses` Javadoc says `initCurses` makes the method safe for an object whose map
  was never created. That reasoning no longer holds: the merge starts from `dest.getCurses()` at
  `ObjectUtils.java:1101`, which answers an empty map for a `null` field, and `setCurses` does not read the old field.
  The comment and the call both need a second look.
- **What C does:** `copy_curses` allocates `obj->curses` only if it is still null, and then writes into it. Replacing
  the whole map is a divergence the Javadoc already records; the extra `initCurses` call is only noise.
- **What a fix needs:** drop the `initCurses()` call and correct the paragraph in the `copyCurses` and `initCurses`
  Javadoc that justifies it. This is independent of item 1, but you may want to do them together because both change
  those lines.

## 4. `applyCurseAttributes` reads a curse's element map without a null guard (batch 4)

- **Where:** `ItemObject.java:4806`, `curse.getElInfo().getOrDefault(elem, null)`. `ItemObject.java:5086` does the same
  with `curse.getElInfo().get(...)`, and `Curse.java:692` walks `elInfo.keySet()` on its own field.
- **What happens:** `Curse.getElInfo()` (`Curse.java:296`) returns the field as it is. If a `Curse` were built with a
  `null` map, each of those reads throws a `NullPointerException`.
- **Why it is safe today:** the only constructor call, `CurseAssembler.java:260`, passes the map built at
  `CurseAssembler.java:139`, which is a new `HashMap` for every curse.
- **The inconsistency:** `Curse.getModifiers()` (`Curse.java:281`) already answers `Map.of()` for a `null` field, and
  `ItemObject.getElInfo()` (`ItemObject.java:2641`) does the same. `Curse.getElInfo()` is the one getter of the three
  that does not.
- **What a fix needs:** make `Curse.getElInfo()` answer `Map.of()` for a `null` field, as its neighbours do. That covers
  both `ItemObject.java` readers at once. `Curse.java:692` reads the field directly, so it needs the same treatment or a
  switch to the getter.

## 5. `Slay` has no `equals` or `hashCode` (batch 6)

- **Where:** `Slay.java:33`. `Brand.java:131` and `Brand.java:147` have both.
- **What happens:** `ItemObject` holds `Set<Slay>` (`ItemObject.java:588`), so two `Slay` objects count as the same
  entry only if they are the same instance. A `Set<Brand>` compares every field.
- **Where it could bite:**
    - `addSlay` and `removeSlay` (`ItemObject.java:2987`, `ItemObject.java:3005`) work on the set. Adding a copy of a
      slay the object already holds keeps both, and removing by a copy removes nothing.
    - `Slay.copy()` (`Slay.java:180`) exists, and `copySlays` (`ObjectUtils.java:1199`) uses it when the kind's slay
      beats the one already there. A caller holding the original reference then fails to remove the copy.
    - Any comparison of two slay sets, or a `contains` against a copy, answers by identity.
- **Why it is safe today:** `Slay` has no setters, so an instance never changes once built, and
  `ItemObject.copy` (`ItemObject.java:3969`) copies the set shallowly, so the same instances travel together. Claude did
  not check how the registry hands slays out, so whether every slay in play is a shared instance is untested.
- **Not the same as C's grouping:** `sameMonsterSlain` (`Slay.java:159`) is the comparison C uses to decide two slays
  are the same property. `Brand.equals` documents that it is stricter than the matching C uses for runes, which compares
  names. A `Slay.equals` over every field would be stricter than `sameMonsterSlain` in the same way, and that is the
  deliberate half of the design rather than a mismatch.
- **What a fix needs:** decide whether `Slay` should be a value type like `Brand`. If so, add `equals` and `hashCode`
  over the same fields `Brand` uses and say in the Javadoc how it differs from `sameMonsterSlain`. If not, record in the
  `Slay` Javadoc that identity is the contract and that `copy()` results are not interchangeable with the original in a
  set.

## Next

Work down items 1 to 5 in `src/main`, then run `/reverify` on each method you change. Items 1 and 3 touch the same lines
of `copyCurses`, so doing them together saves a pass.
