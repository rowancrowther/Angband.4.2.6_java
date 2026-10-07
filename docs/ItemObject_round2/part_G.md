# Part G: description, messages, slots and glyphs (Phase 1, Stage 1)

Run 2026-10-07. Read-only. Every Java declaration listed for Part G, and the C functions it ports, were read in full in
this turn. Line numbers are as read this turn. Baseline: batch 8 of `docs/ItemObject_port_batches.md`; `modMessage` has
none and got a full Stage 1.

## What was read

- Java, all in `src/main/java/uk/co/jackoftradesltd/middle/objects/ItemObject.java`: `description`
  (`ItemObject.java:2298`),
  `wieldSlot` (`ItemObject.java:6457`), `canBrowse` (`ItemObject.java:6532`), `objectKindName` (`ItemObject.java:6685`),
  `objDescNameFormat` (`ItemObject.java:6748`), `printCustomMessage` (`ItemObject.java:6922`), `getItemObjectADC`
  (`ItemObject.java:7193`), `objectKindChar` (`ItemObject.java:7213`), `objectKindAttr` (`ItemObject.java:7230`),
  `useFlavourGlyph` (`ItemObject.java:7249`), `modMessage` (`ItemObject.java:7294`).

- Fields they touch: `player` (`ItemObject.java:178`), `kind` (`ItemObject.java:207`), `tValue` (`ItemObject.java:334`),
  `modifiers` (`ItemObject.java:564`), `number` (`ItemObject.java:714`), plus the readers
  `getModifierValue(ObjectModifier)`
  (`ItemObject.java:3328`) and `getKind`.

- Delegates and helpers read: `ObjectKind.canBrowse` (`ObjectKind.java:1041`), `ObjectUtils.slotByType`
  (`ObjectUtils.java:763`), the `TValue` predicates (`TValue.java`: `isScroll`, `isLight`, `isRing`, `isBodyArmour`,
  `isHeadArmour`, `isMeleeWeapon`), `MessageTag.getTag` (`MessageTag.java:84`), `Message.message` (`Message.java:161`),
  `Message.messageType` (`Message.java:212`), `FlavourKind.getGlyph`, `Flavour.getColour`, `Flavour.getText`,
  `ObjectKind.getCharacter` (`ObjectKind.java:1368`).

- C: `object_kind_name`, `obj_desc_name_format`, `obj_desc_get_basename` and `obj_desc_name_prefix` (`obj-desc.c`);
  `msg_tag_lookup`, `print_custom_message`, `obj_kind_can_browse`, `obj_can_browse` (`obj-util.c`); `wield_slot` and
  `slot_by_type` (`obj-gear.c`); `mod_message` (`obj-knowledge.c`); `use_flavor_glyph`, `object_kind_attr`,
  `object_kind_char`, `object_attr`, `object_char` (`ui-object.c`); `msg` and `msgt` (`message.c`); the flavor parser
  (`init.c`); `tval_is_*` (`obj-tval.c`).

## What matches

- `modMessage` (`ItemObject.java:7294`) against `mod_message` (`obj-knowledge.c`), the full Stage 1 it never had.

    - Arms: C has eleven non-default arms (STR, INT, WIS, DEX, CON, STEALTH, SPEED, BLOWS, SHOTS, INFRA, LIGHT) plus
      `default: break`. Java has the same eleven, in the same order, plus an empty `default`. Searching, tunnelling,
      might, moves and damage reduction fall to `default` in both and print nothing. `OM_NONE` and `OM_MAX` also print
      nothing in both (C's `OBJ_MOD_MAX` would hit `default`).

    - Each of the nine sign-dependent arms tests `> 0` first and `< 0` second, reading the same modifier that the `case`
      names, so the zero case prints nothing in both. A mixed-up modifier in any arm would be caught by the string order
      below; none is.

    - Strings: the twenty `msg("...")` literals in `mod_message` and the twenty `Message.message("...")` literals in
      `ItemObject.java:7294-7356` were extracted by script and diffed. They are identical character for character and in
      the same order (including the two sentences that end in `.` rather than `!`: "You feel stealthier.", "You feel
      noisier.", "You feel strangely quick.", "You feel strangely sluggish.", "Your weapon tingles in your hands.",
      "Your weapon aches in your hands.", "Your missile weapon tingles in your hands.", "Your missile weapon aches in
      your hands.", "Your eyes tingle.").

    - `INFRA` and `LIGHT` are not sign-tested in either version: an item with infravision 0 or -3 still prints "Your
      eyes tingle." in both.

    - Output channel: C's `msg` logs and signals `EVENT_MESSAGE` as `MSG_GENERIC` and, unlike `msgt`, makes no sound
      (`message.c`). `Message.message` (`Message.java:161`) does the same and skips the sound, so it is the right port.
      `msgt` would have been wrong.

    - Reads: both read the object's own modifier, not the player's. C reads `obj->modifiers[...]` raw; Java reads
      `getModifierValue(OM_x)`, which is `getModifiers().getOrDefault(om, 0)` (`ItemObject.java:3328`), so an absent
      entry is 0 as C's zeroed array is. `modMessage` reads no player and writes nothing on the item.

    - Walked values: STR +2 prints "You feel stronger!" in both; STR -1 prints "You feel weaker!"; STR 0 or absent
      prints nothing; SHOTS -1 prints "Your missile weapon aches in your hands."; LIGHT 0 prints "It glows!"; MIGHT +3
      prints nothing. The `null` guard at `ItemObject.java:7295` is the port's own and is not reachable from C.

    - Callers: `PlayerKnowledge.java:2152` and `PlayerKnowledge.java:2239` pass the real object, as C's two call sites
      (`object_learn_on_wield` and `object_curses_find_modifiers`) do.

- `printCustomMessage` (`ItemObject.java:6922`) against `print_custom_message` and `msg_tag_lookup` (`obj-util.c`).

    - A `null` template returns at once in both.

    - Scan: both copy the text up to a `{`, then walk letters. A `}` after the letters is a valid tag. A run of letters
      cut by the end of the string, or by a non-letter other than `}`, is an invalid tag: C resumes from the character
      after the `{`, so the letters stay in the text; Java keeps the letters (appended at the end-of-string exit, left
      in
      `string` at the non-letter exit) and also resumes after the `{`. Walked: `"a {zz"` gives `a zz`; `"a {z1 {s}"`
      gives
      `a z1 ` plus the `s` arm in both; `"{}"` gives nothing in both (`getTag("")` is `MSG_TAG_NONE`, as C's pointer to
      `}`
      matches no name).

    - Tag lookup is `startsWith` in C's order (`name`, `kind`, `s`, `is`) in `MessageTag.getTag` (`MessageTag.java:84`),
      as
      `strncmp` over the name's length is. Walked: `{names}` is NAME, `{size}` and `{sx}` are VERB, `{isn}` is VERB_IS,
      `{nam}` is NONE. C's `strncmp` runs on the rest of the message, but the `}` that ends the letters can never equal
      a letter of a name, so both agree on every input. Unrecognised-but-braced tags are dropped whole, braces included,
      in both.

    - `{kind}`: `objectKindName(getKind(), true)` against `object_kind_name(..., obj->kind, true)`; `hands` with no
      object in both.

    - `{s}`: `s` only when an object exists and `number == 1`; so number 0 and number 2 give nothing in both. `{is}`:
      `are` with no object or `number > 1`, `is` otherwise; so number 0 gives `is` in both. C's missing `break` after
      the
      `{is}` arm falls into `default: break`, which does nothing, so the Java arm matches.

    - Tail and output: the remaining text is appended, then the whole line goes out through the `msgt` port with the
      caller's type, as `"%s"` (`Message.messageType`, `Message.java:212`, which sounds, logs and signals in `msgt`'s
      order). The 1023-character cut is in `Message.messageType`, as C's 1024-byte buffer cuts at 1023.

    - Callers (`PlayerTimed.java:432-457`) pass `noObject` true with a throwaway `ItemObject` where C passes `weapon ==
    NULL`, and false with the weapon otherwise, so every place C tests `obj` the Java tests the flag.

- `objectKindName` (`ItemObject.java:6685`) against `object_kind_name` (`obj-desc.c`): the test `!easyKnow &&
  !kind.isAware() && kind.getFlavour() != null` is C's `!easy_know && !kind->aware && kind->flavor`; the flavour text is
  returned in that case and `objDescNameFormat(kind.getName(), null, false)` otherwise, which is `obj_desc_name_format(buf,
  max, 0, kind->name, NULL, false)`. Scroll titles are filled in by `ObjectUtils.flavourInit` (`ObjectUtils.java:1417`)
  before use, so an unaware scroll's text is not `null`. The only caller (`ItemObject.java:6963`) passes `true`, so the
  unaware-flavour branch has no caller yet.

- `objDescNameFormat` (`ItemObject.java:6748`) against `obj_desc_name_format` (`obj-desc.c`), pass by pass.

    - `&`: C skips every run of spaces and `&` after the first `&`; Java removes each `&` and the spaces after it, in a
      loop. Walked: `"& Lantern~"` gives `Lantern~`; `"a & b"` gives `a b`; `"& & x"` gives `x`; `"&&"` gives `""`.
      Same.

    - `#`: replaced by the recursively formatted modifier (same `pluralise`, no modifier of its own) when the modifier
      is non-null, and left as a literal `#` when it is null, in both. An empty modifier removes the `#` in both.

    - `~`: singular removes it; plural adds `es` after `s`, `h` or `x` and `s` otherwise, in both. Walked with the real
      basenames: `"Lantern~"` plural gives `Lanterns`; `"Flask~ of oil"` gives `Flasks of oil`; `"Torch~"` gives
      `Torches`;
      `"Box~"` gives `Boxes`; a name ending `x~`, `s~`, `h~` gives `es`. A `~` after a `|` takes the `s` branch in both.

    - `|x|y|`: singular keeps `x`, plural keeps `y`, and the text on either side is kept, in both. Walked:
      `"& # Sta|ff|ves|"`
      with modifier `Oak`: singular `Oak Staff`, plural `Oak Staves`. Same. For `"Sta|ff|ves|"` singular is `Staff`,
      plural is
      `Staves`.

    - `modString` and `string` null handling: a `null` template throws in Java and crashes in C; a `null` `modString` is
      "no modifier" in both.

- The accepted divergence for `objDescNameFormat` (`~` straight after `~` while pluralising) was not re-raised.

- `wieldSlot` (`ItemObject.java:6457`) against `wield_slot` (`obj-gear.c`).

    - Order is the same: the six tval cases (bow, amulet, cloak, shield, gloves, boots) first, then melee weapon, ring,
      light, body armour, head armour, then `-1`. The slot types are the same (`EQUIP_BOW`, `EQUIP_AMULET`,
      `EQUIP_CLOAK`, `EQUIP_SHIELD`, `EQUIP_GLOVES`, `EQUIP_BOOTS`, `EQUIP_WEAPON`, `EQUIP_RING`, `EQUIP_LIGHT`,
      `EQUIP_BODY_ARMOR`, `EQUIP_HAT`) and every lookup asks for an empty slot (`full == false`).

    - The `TValue` predicates used (`isMeleeWeapon`: sword, hafted, polearm, digging; `isRing`; `isLight`;
      `isBodyArmour`:
      soft, hard, dragon armour; `isHeadArmour`: helm, crown) are equal to the `tval_is_*` bodies in `obj-tval.c`.

    - `ObjectUtils.slotByType` (`ObjectUtils.java:763`), which `wieldSlot` returns verbatim, was walked against
      `slot_by_type`: first empty slot of the type at index 2 gives 2; every slot of the type occupied gives the first
      of that type; no slot of the type gives the slot count; a hit at index 0 gives 0. Java's `outValue` is "last index
      passed, plus one" and equals C's loop index `i` in each case.

    - Side effect on shared state: `wieldSlot` writes `this.player` from `GameState.getPlayer()` on every call
      (`ItemObject.java:6458`). That stands in for C's `player` global and is the batch 8 fix, still in place. A potion
      (`TV_POTION`) gives `-1`; a ring on a body with no ring slot gives the slot count; with no character the lookup
      throws where C dereferences null. All three are the behaviour the Javadoc says.

- `canBrowse` (`ItemObject.java:6532`) and `ObjectKind.canBrowse` (`ObjectKind.java:1041`) against `obj_can_browse` and
  `obj_kind_can_browse` (`obj-util.c`).

    - The wrapper only forwards the kind, as `obj_can_browse` does.

    - The loop matches `kind->tval == book.tval && kind->sval == book.sval` over the class's books on both halves. A
      class with `ClassMagic.NONE` answers `false` straight away, which is C's `num_books == 0`. The live player is read
      through
      `GameState.getPlayer()` at call time, as C's `player` global is. `ObjectKind.java:1044` compares by `==` against
      the shared `ClassMagic.NONE` (`ClassMagic.java:79`); a class holding an empty hand-built `ClassMagic` would also
      answer
      `false` through the loop.

    - Walked: a mage's `[Magic for Beginners]` (tval magic book, sval 1) gives `true`; a priest's prayer book with sval
      1 for a mage gives `false` because the tval differs; a warrior (`NONE`) gives `false`.

- `getItemObjectADC`, `objectKindChar`, `objectKindAttr`, `useFlavourGlyph` (`ItemObject.java:7193`, `7213`, `7230`,
  `7249`) against `object_attr`, `object_char`, `object_kind_char`, `object_kind_attr`, `use_flavor_glyph`
  (`ui-object.c`).

    - `useFlavourGlyph` is `kind.getFlavour() != null && !(kind.gettValue().isScroll() && kind.isAware())`, which is
      `kind->flavor && !(kind->tval == TV_SCROLL && kind->aware)`. It reads the kind's tval, as C does. Walked: unaware
      scroll gives `true`; aware scroll gives `false`; aware potion gives `true` (only scrolls are excepted); a
      flavourless kind (sword) gives `false`.

    - `objectKindChar` and `objectKindAttr` pick the flavour's glyph and colour when `useFlavourGlyph` holds and the
      kind's own otherwise. In C's flavour parser (`init.c`) every flavour copies the glyph of the preceding `kind:`
      line and takes its colour from its own line, which is `FlavourKind.getGlyph()` for the glyph and
      `Flavour.getColour()` for the colour. The kind's own pair is `ObjectKind.getCharacter()`. `getItemObjectADC`
      forwards to the two, as `object_char` and
      `object_attr` forward to the kind versions, and builds the pair.

    - The pref-remappable `kind_x_char`, `kind_x_attr`, `flavor_x_char`, `flavor_x_attr` tables are the accepted
      divergence and were not re-raised.

- `description` (`ItemObject.java:2298`) is a stub returning `{DESCRIPTION_TAG}` until Chapter 7. Noted and left. The
  helpers beside `object_desc` in `obj-desc.c` (`obj_desc_get_basename`, `obj_desc_name_prefix`, `obj_desc_name`, and
  the rest) are not ported; they are later-chapter work, not a mismatch.

## What does not match

- No new mismatch in any of the eleven declarations.

- Divergences that the `objDescNameFormat` Javadoc already records as deliberate but that the batches doc's "Accepted
  divergences" list for batch 8 does not name. They are in the code and written down at `ItemObject.java:6712-6738`, so
  they are listed here for you to accept or fix, not as new findings. None is reachable from the shipped data:
  `object.txt` and `object_base.txt` hold no kind name containing `|`, `#` or `~~`, `flavor.txt` holds no flavour text
  containing `|`, `~`, `#` or `&`, and the only template with bars is C's hard-coded `"& # Sta|ff|ves|"`.

    - Bar count not a multiple of three (`ItemObject.java:6768-6773`): Java returns the template whole and unformatted;
      C (`obj_desc_name_format`, the `|` arm) returns the text written so far, cut at the unmatched bar, without
      terminating the buffer. Input `"ab|c"`: Java `ab|c`, C `ab`.

    - `~` at the front of the template while pluralising (`ItemObject.java:6779-6783`): Java logs and returns; C reads
      the byte before the template. Input `"~x"`, plural: Java `~x`, C undefined.

    - `~` directly after `#` while pluralising: Java sees the last letter of the substituted modifier, C sees the `#`.
      Input
      `"#~"` with modifier `Ash`, plural: Java `Ashes`, C `Ashs`.

    - No output bound: C truncates to the caller's buffer, Java does not.

- Not in the Javadoc and not in the accepted list, also unreachable from the shipped data: a `~` inside the alternative
  that `|x|y|` keeps. C copies that alternative raw with `%.*s`, so the `~` survives; Java runs its `~` pass over the
  whole string before the bar pass, so the `~` is removed (singular) or turned into `s`/`es` (plural). Input
  `"A|b~|c|"`, singular: C `Ab~`, Java `Ab`. Only a template with a `~` and a bar together can reach it, and none
  exists.

- Verdict on the above: all five are unreachable now and the first four are already written into the Javadoc, so none
  stopped this sweep. They are listed so the baseline's accepted list can name them or you can decide to close them. The
  fifth is new to the record.

## Out-of-scope observations

- `ItemObject.java:7358`: `setEffectMessage` has no Javadoc. `docs/ItemObject_round2/part_0.md` moved it to Part H as a
  plain setter; it is the only member between `modMessage` and the `PowerAndMult` record without a block.

- `Message.java:161-176`: `Message.message` has no counterpart for C's `if (!messages) return;` in `msg` (`message.c`).
  `modMessage` and `Message.message` therefore print into the log on a path where C stays silent if the message store is
  not yet loaded. Not reachable in play, where messages load before any item is learned.

- `ObjectKind.java:1222` cites `obj-knowledge.c:856` and `PlayerBody.java:191` cites `obj-gear.c:1040` in Javadoc: both
  are C line numbers, which this project's port work does not carry. Neither is in a Part G declaration. (Both lines
  were read this turn through a search that showed them; neither file was read whole.)

- `ObjectKind.java:1360` and `ItemObject.java:7201` link to `objectKindChar()` and `objectKindAttr()` as `{@link}`s;
  those methods are `private`, so the links render as plain text in generated Javadoc. Cosmetic.

- `FlavourKind.getGlyph()` and `AngbandDisplayCharacter` hold a Java `char`, where C's `wchar_t` can hold a code point
  past the basic plane. The shipped `flavor.txt` glyphs are single ASCII characters, so nothing reaches it.

- `objectKindName` (`ItemObject.java:6685`) and `objDescNameFormat` (`ItemObject.java:6748`) are `private`, and the only
  caller of the first always passes `true`, and `printCustomMessage` calls the second only through it with `null` and
  `false`; so the unaware-flavour path and the `pluralise` and `modString` arguments have no caller in `src/main` until
  `description` is ported in Chapter 7.

## Next

- Nothing in Part G needs a code change. Decide whether to add the five unreachable `objDescNameFormat` divergences
  above to the batch 8 accepted list, then Part G can go straight to Stage 2.

clean
