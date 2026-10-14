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

## Monster base

### `MonsterBase` holds C's one `text` member twice

- **Java:** `MonsterBase.inGameName` and `MonsterBase.description`; `MonsterBaseParseRecord.inGameName` and
  `description`; the `monsterBase` rule in `MonsterBaseGrammar.g4`. **C:** `monster_base->text` (`monster.h`), filled
  by `parse_mon_base_desc` (`mon-init.c`).
- **Differs:** C has one `text` member, which the C comment calls the in-game name and the data file calls the
  description. Java stores the same `desc:` text in two fields, and the grammar passes it for both.
- **Reach:** none. Both fields are always equal for a base read from `monster_base.txt`. Neither has an accessor yet.
- **Revisit:** when the first accessor is added, or when the record is next reworked. Then drop one component from the
  record, the grammar, the assembler and the `MonsterBase` constructor.
- **Accepted:** 2026-10-10. Marked in the `MonsterBase` and `MonsterBaseParseRecord` Javadoc.

## Terrain

### An unknown terrain colour is rejected, where C reads it as white

- **Java:** the `COLOUR_VALUES` token in `AngbandDisplayCharacter.g4` (used by `TerrainFeatureGrammar.g4`) and
  `TerrainFeatureAssembler.assemble`, through `ColourEnum.fromCode`. **C:** `parse_feat_graphics` (`init.c`), with
  `color_text_to_attr` and `color_char_to_attr` (`z-color.c`).
- **Differs:** both C look-ups fall back to `COLOUR_WHITE` for a colour they do not know, so a `graphics:` line never
  fails in practice and `PARSE_ERROR_INVALID_COLOR` is unreachable. Java has lexer tokens only for the colours in C's
  table, so an unknown colour is a hard grammar error that fails the whole file. If a token ever got past the lexer,
  the assembler drops the record with an `Unknown colour` soft error, and the load carries on without an entry for
  that code.
- **Reach:** none from shipped data; every `graphics:` line in `terrain.txt` names a colour in C's table. Hand-edited
  files only.
- **Revisit:** if a hand-edited terrain file is ever meant to load with a white fallback, or if the colour handling of
  the other readers is brought back into line with C.
- **Accepted:** 2026-10-13. Pinned by the `ColourCheck` tests in `TerrainDataLoaderFeaturesTest`; marked in the
  `TerrainDataLoader` Javadoc.

## Rational

### `Rational` holds signed 63-bit values, where C's `my_rational` holds unsigned 32-bit ones

- **Java:** `Rational` (`multi`, `add`, `toUint`, and the constructors). **C:** `struct my_rational`,
  `my_rational_construct`, `my_rational_product`, `my_rational_sum` and `my_rational_to_uint` (`z-util.c`, `z-util.h`).
- **Differs:** C stores two `unsigned int` and, when an exact result will not fit, approximates and saturates at
  `UINT_MAX`. Java stores two `long`, runs the same approximation algorithm at `Long.MAX_VALUE`, and saturates at
  `Long.MAX_VALUE`. Results agree exactly while every numerator and denominator stays below 2^32; above that Java is
  exact where C has approximated (`4294967295 + 1` is `4294967296/1` in Java and `4294967295/1` in C). `toUint` does not
  saturate at `UINT_MAX` either. Java also takes negative values and a negative denominator, and throws
  `IllegalArgumentException` on a zero denominator where C asserts.
- **Reach:** none from the game. C's callers (`obj-info.c`, `player-path.c`, `obj-randart.c`, `wiz-stats.c`, and `mean`
  and `variance` in `z-util.c`) work on dice, counts and turn figures far below 2^32.
- **Revisit:** if a port ever needs C's exact `UINT_MAX` saturation, for example to reproduce a displayed figure.
- **Accepted:** 2026-10-14. Marked in the `Rational` Javadoc.

## NumberUtils

### `variance` saturates at `INT_MAX` for a value between `INT_MAX` and `INT_MAX + 1`

- **Java:** `NumberUtils.variance`. **C:** `variance` (`z-util.c`).
- **Differs:** C returns `INT_MAX` with a zero fraction only when the whole part of the variance is above `INT_MAX`,
  that is when the value is at least `INT_MAX + 1`. A value such as `INT_MAX + 1/2` comes back as `INT_MAX` with the
  fraction kept. Java returns one `Rational` and tests whether the value is above `Integer.MAX_VALUE`, so that
  `INT_MAX + 1/2` becomes `INT_MAX` exactly and the fraction is lost. Values up to `INT_MAX` and values from
  `INT_MAX + 1` upward agree.
- **Reach:** none shown. A differential run of 80,000 random cases against the compiled C found no difference, and a
  search for a small integer input inside the range found none (n = 2 cannot produce one).
- **Revisit:** if a displayed or compared variance ever needs the fraction of a value just above `INT_MAX`.
- **Accepted:** 2026-10-14. To be marked in the `variance` Javadoc by the stage 2 pass.

## Pending your decision

- The five `objDescNameFormat` divergences (Part G; `docs/ItemObject_round2/part_G.md`). All unreachable from shipped
  data; four are already in the Javadoc and the fifth (a `~` inside a kept `|x|y|` alternative) is new. Not accepted
  until you say so.

## Call to action

- Say which of the pending `objDescNameFormat` cases to accept, and I will move them into the ItemObject section.
- Check `docs/ItemObject_round2/summary.md` item 4: it now carries both `ACCEPTED` and `FIXED` as its **Result**, and
  they cannot both be true.
