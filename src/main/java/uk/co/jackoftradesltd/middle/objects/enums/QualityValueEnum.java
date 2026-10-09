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
 * The quality bands that quality ignoring works by. An ignore setting pairs a kind of item
 * ({@link IgnoreType}) with one of these bands, and everything at or below that band is ignored: set
 * "Bows" to {@link #IGNORE_AVERAGE} and bad and average Bows go, good ones stay.
 *
 * <p>This is the Java port of the anonymous {@code enum} of {@code IGNORE_*} values in
 * {@code obj-ignore.h}. C keeps each player's setting as a {@code uint8_t} in
 * {@code ignore_level[ITYPE_MAX]}; here {@code ObjectInfo.ignoreLevel} holds these constants
 * instead, keyed by {@link IgnoreType}.
 *
 * <p>Each constant's {@link #ordinal()} is its C value, and the order matters. C's
 * {@code ignore_item_ok} decides whether an item is ignored by testing
 * {@code ignore_level_of(obj) <= ignore_level[type]}, and
 * {@code ObjectIgnore.ignoreItemOK} makes the same test on the ordinals. C also writes the setting
 * to the savefile as a byte, uses it to index the {@code quality_values} name table, and sets it
 * from the menu cursor position in {@code ui-options.c}, so the declaration order must stay exactly
 * as {@code obj-ignore.h} has it.
 *
 * <p>C's {@code quality_values} table in {@code obj-ignore.c} pairs the first five values with the
 * names the ignore menus show ({@code "no ignore"}, {@code "bad"}, {@code "average"},
 * {@code "good"} and {@code "non-artifact"}). It is sized by {@link #IGNORE_MAX}, so
 * {@code IGNORE_MAX} has no name. This enum carries no names, and nothing in Java ports that table
 * yet.
 *
 * <p>This is not the same thing as {@link IgnoreType}, which ports C's kinds of item
 * ({@code ITYPE_*}).
 *
 * <p>Class QualityValueEnum coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public enum QualityValueEnum {
    /**
     * No quality ignoring at all, the setting every kind of item starts with. Being the lowest
     * band, it is below every band {@code ignoreLevelOf} can report, so the ordinal test never
     * ignores anything on quality. {@code ObjectInfo.ignoreLevel} is filled with it at start-up and
     * {@code ObjectIgnore.ignoreBirthInit} puts it back, as C's {@code ignore_birth_init} does with
     * {@code ignore_level[i] = IGNORE_NONE}. C's menu names it {@code "no ignore"}. Nothing returns
     * it from {@code ignore_level_of}: it is a setting, not an item's quality.
     *
     * <p>Constant IGNORE_NONE coded before 261009, commented in full on 261009.
     */
    IGNORE_NONE,
    /**
     * Items worse than their kind's usual run: negative bonuses, or for jewellery any negative
     * combat bonus with no positive one. As a setting it ignores only bad items. It is the only
     * band the menu offers for rings and amulets besides {@link #IGNORE_NONE}, because
     * {@code quality_action} in {@code ui-options.c} shows just {@code IGNORE_BAD + 1} rows for
     * them. C's menu names it {@code "bad"}.
     *
     * <p>Constant IGNORE_BAD coded before 261009, commented in full on 261009.
     */
    IGNORE_BAD,
    /**
     * Items with no bonus either way, and all jewellery that is not bad. As a setting it ignores
     * bad and average items. C's menu names it {@code "average"}.
     *
     * <p>Constant IGNORE_AVERAGE coded before 261009, commented in full on 261009.
     */
    IGNORE_AVERAGE,
    /**
     * Items better than their kind's usual run, that are neither egos nor artefacts. As a setting
     * it ignores everything up to and including good items. C's menu names it {@code "good"}.
     *
     * <p>Constant IGNORE_GOOD coded before 261009, commented in full on 261009.
     */
    IGNORE_GOOD,
    /**
     * As an item's quality, an ego item, or an object the player has assessed but not yet fully
     * learned that is not an artefact. As a setting it ignores everything that is not an artefact,
     * egos included, and {@code ObjectIgnore.ignoreItemOK} gives it its own early test on an
     * assessed non-artefact. C's menu names it {@code "non-artifact"}, the one name that does not
     * match the constant.
     *
     * <p>Constant IGNORE_ALL coded before 261009, commented in full on 261009.
     */
    IGNORE_ALL,
    /**
     * C's count sentinel, which sizes {@code quality_values} and the menu in {@code ui-options.c},
     * and which {@code ignore_level_of} also returns as a real answer: "no band applies". It is
     * returned for an object the player does not know, for a fully known artefact, and for an
     * object not yet fully known that has not been assessed. Because it is above every setting, the
     * ordinal test never ignores such an object, and {@code ui-object.c} uses it to leave the "All
     * (quality) (kind)" entry off the ignore menu. It is never a setting and has no name.
     *
     * <p>Constant IGNORE_MAX coded before 261009, commented in full on 261009.
     */
    IGNORE_MAX
}
