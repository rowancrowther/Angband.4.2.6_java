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
 * The mode bits a caller passes to {@code object_desc()} to say how much of an object's name to
 * build. The port of the anonymous enum under "Modes for object_desc()" in {@code obj-desc.h}.
 *
 * <p>C packs them into a {@code uint32_t} ({@code ODESC_COMBAT} 0x01 up to {@code ODESC_ALTNUM}
 * 0x400) and combines them with {@code |}. This port holds the set in a
 * {@code Flag<ObjectDescription>}, an {@link java.util.EnumSet}, so no mask value is stored and the
 * ordinal carries no meaning for callers. Three things differ from C:
 * <ul>
 *   <li>{@link #ODESC_BASE} is 0x00 in C, so it occupies no bit. Here it is a real constant, and
 *   an empty set means the same thing as a set holding only it.</li>
 *   <li>C's {@code ODESC_FULL} ({@code ODESC_COMBAT | ODESC_EXTRA}) has no constant; callers set
 *   both.</li>
 *   <li>The declaration order follows C, but {@code ODESC_BASE} takes ordinal 0, so ordinal
 *   {@code n} is the bit {@code 1 << (n - 1)} for the constants after it.</li>
 * </ul>
 *
 * <p>{@link #ODESC_ALTNUM} also uses the high 16 bits of the mode word to carry a count, which a
 * flag set cannot hold; see that constant.
 *
 * <p>Enum ObjectDescription coded before 260815, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public enum ObjectDescription {
    /**
     * Only describe the base name. C value 0x00, so in C it is the absence of every other bit and no code
     * tests it; callers write it to say "nothing extra" ({@code ODESC_PREFIX | ODESC_BASE}).
     *
     * <p>Because a {@link uk.co.jackoftradesltd.channel.utils.Flag} is a set of enum constants, this one
     * is a real member, and a set holding only {@code ODESC_BASE} is not {@link
     * uk.co.jackoftradesltd.channel.utils.Flag#isEmpty empty}. The port's {@code object_desc} must therefore
     * never read it as a mode bit: an empty set and a set holding only {@code ODESC_BASE} both mean C's
     * {@code 0x00}.
     *
     * <p>Constant ODESC_BASE coded before 260815, commented in full on 261008.
     */
    ODESC_BASE,
    /**
     * Also show combat bonuses. C bit 0x01. {@code object_desc} ({@code obj-desc.c}) then appends chest
     * trap state, light turns, and to-hit, to-dam and armour class. Half of C's {@code ODESC_FULL}.
     *
     * <p>Constant ODESC_COMBAT coded before 260815, commented in full on 261008.
     */
    ODESC_COMBAT,
    /**
     * Show charges, inscriptions and pvals. C bit 0x02. {@code object_desc} then appends modifiers,
     * charges or charging status, and either the aware/tried marker (with {@link #ODESC_STORE}) or the
     * inscription and ignore marker. Half of C's {@code ODESC_FULL}.
     *
     * <p>Constant ODESC_EXTRA coded before 260815, commented in full on 261008.
     */
    ODESC_EXTRA,
    /**
     * This is an in-store description. C bit 0x04. The flavour is not shown, the object counts as aware,
     * the ego name is shown, and the ignore markers are turned off.
     *
     * <p>Constant ODESC_STORE coded before 260815, commented in full on 261008.
     */
    ODESC_STORE,
    /**
     * Always pluralise, whatever the stack size. C bit 0x08. Ignored for artifacts, and overridden by
     * {@link #ODESC_SINGULAR}.
     *
     * <p>Constant ODESC_PLURAL coded before 260815, commented in full on 261008.
     */
    ODESC_PLURAL,
    /**
     * Always singular. C bit 0x10. Takes precedence over {@link #ODESC_PLURAL}.
     *
     * <p>Constant ODESC_SINGULAR coded before 260815, commented in full on 261008.
     */
    ODESC_SINGULAR,
    /**
     * Display regardless of player knowledge: the object is treated as fully identified, and the
     * "ever seen" markers on its kind and ego are left alone. C bit 0x20. Used by the spoiler and randart
     * writers, which pass a null player.
     *
     * <p>Constant ODESC_SPOIL coded before 260815, commented in full on 261008.
     */
    ODESC_SPOIL,
    /**
     * Prepend "the", "a" or the number to the name. C bit 0x40. Almost every caller that shows the
     * object to the player in a sentence sets it, usually together with {@code ODESC_FULL}, which the port
     * writes as {@link #ODESC_COMBAT} and {@link #ODESC_EXTRA}.
     *
     * <p>Constant ODESC_PREFIX coded before 260815, commented in full on 261008.
     */
    ODESC_PREFIX,
    /**
     * Capitalise the object name. C bit 0x80. The doc comment on {@code object_desc} lists it, but
     * {@code obj-desc.c} never tests the bit, so in 4.2.6 setting it changes nothing there.
     *
     * <p>Constant ODESC_CAPITAL coded before 260815, commented in full on 261008.
     */
    ODESC_CAPITAL,
    /**
     * Make terse names: no flavour, and an aware flavoured object reads {@code 'Name'} instead of
     * {@code of Name}. C bit 0x100.
     *
     * <p>Constant ODESC_TERSE coded before 260815, commented in full on 261008.
     */
    ODESC_TERSE,
    /**
     * Don't show ego names. C bit 0x200. Does not hide an ego in a store ({@link #ODESC_STORE}).
     *
     * <p>Constant ODESC_NOEGO coded before 260815, commented in full on 261008.
     */
    ODESC_NOEGO,
    /**
     * Use a number held in the high 16 bits of the mode word instead of the object's own count. C bit
     * 0x400, and the only constant whose meaning reaches outside the bit it names: the caller ORs
     * {@code count << 16} into the mode.
     *
     * <p>A flag set cannot carry that count, so the port will need a separate parameter on
     * {@code ObjectUtils.objectDesc}, as its Javadoc already records. C itself warns that the option is
     * not fully compatible with {@link #ODESC_EXTRA}: the rods-charging figure ignores the alternate
     * number.
     *
     * <p>Constant ODESC_ALTNUM coded before 260815, commented in full on 261008.
     */
    ODESC_ALTNUM
}
