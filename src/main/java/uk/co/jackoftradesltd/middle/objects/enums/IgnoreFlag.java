/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.middle.objects.enums;

/**
 * The two senses in which the player can choose to ignore an object kind — the port of C's
 * {@code IGNORE_IF_AWARE} and {@code IGNORE_IF_UNAWARE} ({@code obj-ignore.h}), held on
 * {@link uk.co.jackoftradesltd.middle.objects.ObjectKind} as a flag set rather than C's packed byte.
 *
 * <p><b>Why two, and not one.</b> Ignoring is a judgement about a kind, and the player can hold
 * different judgements about the same kind depending on whether they know what it is. Unknown
 * potions are worth stepping over once the floor is littered with them; Potions of Cure Light Wounds
 * are not, and the moment an unknown potion turns out to be one, the earlier judgement should stop
 * applying. Keeping the two apart is what lets that happen without the player having to intervene.
 *
 * <p>The transition is one-way and it runs through
 * {@code Player.flavourAware}: a kind marked
 * {@code IGNORE_IF_UNAWARE} that the player becomes aware of picks up {@code IGNORE_IF_AWARE}, so a
 * standing decision to ignore the unidentified is carried across into a decision about the thing
 * itself rather than silently lapsing.
 *
 * <p>Enum IgnoreFlag coded on 260816, commented in full on 260816.
 *
 * @author Rowan Crowther
 */
public enum IgnoreFlag {
    /**
     * The player ignores this kind once they know what it is.
     */
    IGNORE_IF_AWARE,
    /**
     * The player ignores this kind while it is still an unidentified flavour.
     */
    IGNORE_IF_UNAWARE
}
