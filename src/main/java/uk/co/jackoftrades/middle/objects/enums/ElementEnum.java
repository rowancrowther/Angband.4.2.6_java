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

package uk.co.jackoftrades.middle.objects.enums;

import uk.co.jackoftrades.channel.enums.ProjectionEnum;

/**
 * The damage elements as referenced by object code (resistances, brands, slays).
 * Mirrors the C original's {@code ELEM_*} list; the constants are self-describing
 * and documented collectively here. {@code ELEM_MAX} is the count sentinel.
 *
 * <p>The declaration order matches {@code src/list-elements.h} exactly, and must: C's projection
 * parser checks each of the first {@code ELEM_MAX} entries of {@code projection.txt} against the
 * element of the same position and refuses to load if they disagree, so the two files are locked
 * together. {@link #getProjectionEnum} relies on the same correspondence, by name rather than by
 * position.
 *
 * <p>Unlike C, this enum carries an {@code ELEM_NONE} zero placeholder, so an element's
 * {@link #ordinal()} is one greater than its C value. Nothing in the port depends on the numeric
 * value — runes and properties hold the constant itself — but the difference matters when reading
 * the original alongside this.
 *
 * @author Rowan Crowther
 */
public enum ElementEnum {
    ELEM_NONE(false, false),
    ELEM_ACID(true, true),
    ELEM_ELEC(true, true),
    ELEM_FIRE(true, true),
    ELEM_COLD(true, true),
    ELEM_POIS(false, true),
    ELEM_LIGHT(false, true),
    ELEM_DARK(false, true),
    ELEM_SOUND(false, true),
    ELEM_SHARD(false, true),
    ELEM_NEXUS(false, true),
    ELEM_NETHER(false, true),
    ELEM_CHAOS(false, true),
    ELEM_DISEN(false, true),
    ELEM_WATER(false, false),
    ELEM_ICE(false, false),
    ELEM_GRAVITY(false, false),
    ELEM_INERTIA(false, false),
    ELEM_FORCE(false, false),
    ELEM_TIME(false, false),
    ELEM_PLASMA(false, false),
    ELEM_METEOR(false, false),
    ELEM_MISSILE(false, false),
    ELEM_MANA(false, false),
    ELEM_HOLY_ORB(false, false),
    ELEM_ARROW(false, false),
    ELEM_MAX(false, false);

    /**
     * Whether this is a "base" element — the four physical damage types (acid, electricity, fire,
     * cold) that objects can ignore/be affected by as a group. Ports the {@code base} column of C's
     * {@code list-elements.h}; used e.g. when a dungeon spellbook is set to ignore every base element.
     *
     * @author Rowan Crowther
     */
    private final boolean isBase;

    /**
     * Whether objects can resist this element, and so whether it has a resistance rune. True for
     * the base elements and the "high" elements up to and including disenchantment; false for the
     * remainder, which are damage types used by spells and monster attacks but which nothing grants
     * resistance to.
     *
     * <p>Ports C's {@code ELEM_HIGH_MAX} bound, which relies on the {@code ELEM_*} constants being
     * ordered so that everything resistable comes first. Making it a per-constant flag rather than
     * an ordinal comparison keeps this port independent of that ordering.
     *
     * @author Rowan Crowther
     */
    private final boolean hasResistRune;

    /**
     * @param isBase        whether this element is one of the four base (physical) damage types
     * @param hasResistRune whether objects can resist this element
     * @author Rowan Crowther
     */
    ElementEnum(boolean isBase, boolean hasResistRune) {
        this.isBase = isBase;
        this.hasResistRune = hasResistRune;
    }

    /**
     * @return {@code true} if this is a base (physical) element
     * @author Rowan Crowther
     */
    public boolean isBase() {
        return isBase;
    }

    /**
     * @return {@code true} if objects can resist this element, and so if it has a resistance rune
     * @author Rowan Crowther
     */
    public boolean isHasResistRune() {
        return hasResistRune;
    }

    /**
     * Finds the projection this element is dealt as — {@link #ELEM_FIRE} gives
     * {@link ProjectionEnum#PROJ_FIRE}, and so on. Every element has one: C's projection parser
     * refuses to load {@code projection.txt} unless its leading entries match the element list
     * position for position, so the elements are simply the first stretch of the projection list.
     *
     * <p>C exploits that alignment directly, subscripting its projection array with an element's
     * value. Here the correspondence is resolved by name instead, which keeps the port clear of
     * both enums' {@link #ordinal()}s — each carries a {@code NONE} placeholder that C lacks, so
     * neither set of ordinals matches the original's numbering anyway.
     *
     * <p>Lives here rather than on {@link ProjectionEnum} because the question it answers is
     * "how is this element delivered?", which is a property of the element; and because
     * {@code ProjectionEnum} is shared vocabulary that both halves of the program can name,
     * while elements are the core's own.
     *
     * @return the matching projection, or {@code null} if no constant of that name exists, which
     * means the two enums have drifted apart
     * @author Rowan Crowther
     */
    public ProjectionEnum getProjectionEnum() {
        try {
            return ProjectionEnum.valueOf("PROJ_" + name().substring(5));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
