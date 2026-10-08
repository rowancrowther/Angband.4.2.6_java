# Accepted deviations from C

One register of every place the Java port knowingly behaves differently from Angband 4.2.6, and you have decided to
leave it. Add an entry when you accept a divergence; do not add findings you have not decided on.

Each entry gives the Java symbol, the C counterpart, what differs, why it cannot bite today, and when to revisit. No
line numbers (C or Java), as the house rule says; the Javadoc on the symbol carries the detail.

Seeded 2026-10-07 from `docs/ItemObject_round2/summary.md` (the "What was accepted as a divergence" list). The older
batch lists in `docs/ItemObject_port_batches.md` are not in the repo, so entries that only the summary names are
recorded here as the summary states them; fill in the detail when you next touch the symbol.

## ItemObject

### `copyCurses` with a non-null all-power-0 source

- **Java:** `ObjectUtils.copyCurses`. **C:** `copy_curses` and `curses_are_equal` (`obj-curse.c`).
- **Differs:** C allocates a zeroed `obj->curses` before skipping power-0 entries, and `curses_are_equal` treats that
  array as different from `NULL`. Java leaves an empty map, which `cursesAreEqual` reads as `NULL`, so it says equal.
- **Reach:** none from shipped data (no power-0 `curse:` line in `object.txt`, `ego_item.txt` or `artifact.txt`).
  Hand-built sources only.
- **Revisit:** if a fixture or generator ever builds a power-0 curse entry.
- **Accepted:** 2026-10-07. Marked in the `copyCurses` Javadoc.

### `addCurse` and `removeCurse` are simplifications

- **Java:** `ItemObject.addCurse`, `removeCurse`. **C:** `append_object_curse`, `remove_object_curse`.
- **Differs:** deliberate simplifications, not ports (Part A).
- **Revisit:** when curse application (`apply_curse`) is ported.

### `copy(false)` leaves `known` null

- **Java:** `ItemObject.copy`. **Source:** batch 2 list, as the summary records it.

### `flavourIsAware` on a kindless item

- **Java:** `flavourIsAware` against C's `object_flavor_is_aware`.
- **Differs:** only the null-kind answer differs (Part F). Batch list, as the summary records it.

### `hasFlag` ignores curse flags

- **Java:** `ItemObject.hasFlag`. **C:** `obj_has_flag`.
- **Differs:** no curse check.
- **Reach:** C's only caller is `obj_can_takeoff`, which is not ported.
- **Revisit:** when `obj_can_takeoff` is ported.

## Message

### The message log always exists, so C's `if (!messages) return;` has no counterpart

- **Java:** `Message.message`, `Message.messageType`. **C:** `msg`, `msgt` (`message.c`).
- **Differs:** C's `messages` pointer starts `NULL`, is allocated by `messages_init` and freed by `messages_free`, and
  `msg` and `msgt` return silently while it is `NULL`. Java's `messageLog` is a `static` field initialised at
  declaration and never reassigned, so there is no "store not loaded" state, and neither `message` nor `messageType`
  guards on it. There is no port of `messages_init` or `messages_free`.
- **Reach:** none in play. Messages load at startup, before any item can be learned, so `modMessage` and every other
  caller run with the log present in both languages.
- **Revisit:** if `messages_init` and `messages_free` are ported with the init modules. Then `messageLog` starts `null`
  and both `message` and `messageType` guard on it.
- **Accepted:** 2026-10-08. From `docs/ItemObject_round2/summary.md` item 12 (Part G). Marked in the `message` and
  `messageType` Javadoc.

## Pending your decision

- The five `objDescNameFormat` divergences (Part G; `docs/ItemObject_round2/part_G.md`). All unreachable from shipped
  data; four are already in the Javadoc and the fifth (a `~` inside a kept `|x|y|` alternative) is new. Not accepted
  until you say so.

## Call to action

- Say which of the pending `objDescNameFormat` cases to accept, and I will move them into the ItemObject section.
- Check `docs/ItemObject_round2/summary.md` item 4: it now carries both `ACCEPTED` and `FIXED` as its **Result**, and
  they cannot both be true.
