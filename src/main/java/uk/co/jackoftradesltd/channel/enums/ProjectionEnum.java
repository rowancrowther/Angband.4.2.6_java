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

package uk.co.jackoftradesltd.channel.enums;

import java.util.Arrays;

/**
 * The complete set of projection ("GF") types — every kind of bolt, ball, beam
 * or area effect the game can resolve, from elemental damage (acid, fire, …)
 * through terrain alteration (kill/make wall, door, trap) to monster-targeting
 * effects (teleport away, turn, dispel, sleep, polymorph, …). This is the Java
 * port of the C original's projection type list. C builds {@code proj_name_list} in
 * {@code project.c} from {@code list-elements.h} followed by {@code list-projections.h}, so the
 * elements ({@code ACID} to {@code ARROW}) come first and an element's value is also its projection
 * value ({@code PROJ_ACID == ELEM_ACID}). The constants here follow that same order. The
 * constants are self-describing by name and are documented collectively here rather than
 * individually.
 *
 * <p>Two placeholders differ from C: {@link #PROJ_NONE} is a leading zero entry that C lacks, so
 * every real projection's ordinal is one more than its C value, and {@link #PROJ_MAX} is the count
 * sentinel (C's {@code PROJ_MAX} and the trailing {@code "MAX"} in its name list).
 *
 * <p>Class ProjectionEnum coded before 261116, commented in full on 261116.
 *
 * @author Rowan Crowther
 */
public enum ProjectionEnum {
    PROJ_NONE,
    PROJ_ACID,
    PROJ_ELEC,
    PROJ_FIRE,
    PROJ_COLD,
    PROJ_POIS,
    PROJ_LIGHT,
    PROJ_DARK,
    PROJ_SOUND,
    PROJ_SHARD,
    PROJ_NEXUS,
    PROJ_NETHER,
    PROJ_CHAOS,
    PROJ_DISEN,
    PROJ_WATER,
    PROJ_ICE,
    PROJ_GRAVITY,
    PROJ_INERTIA,
    PROJ_FORCE,
    PROJ_TIME,
    PROJ_PLASMA,
    PROJ_METEOR,
    PROJ_MISSILE,
    PROJ_MANA,
    PROJ_HOLY_ORB,
    PROJ_ARROW,
    PROJ_LIGHT_WEAK,
    PROJ_DARK_WEAK,
    PROJ_KILL_WALL,
    PROJ_KILL_DOOR,
    PROJ_KILL_TRAP,
    PROJ_MAKE_DOOR,
    PROJ_MAKE_TRAP,
    PROJ_AWAY_UNDEAD,
    PROJ_AWAY_SPIRIT,
    PROJ_AWAY_EVIL,
    PROJ_AWAY_ALL,
    PROJ_TURN_UNDEAD,
    PROJ_TURN_EVIL,
    PROJ_TURN_LIVING,
    PROJ_TURN_ALL,
    PROJ_DISP_UNDEAD,
    PROJ_DISP_EVIL,
    PROJ_DISP_ALL,
    PROJ_SLEEP_UNDEAD,
    PROJ_SLEEP_EVIL,
    PROJ_SLEEP_ALL,
    PROJ_MON_CLONE,
    PROJ_MON_POLY,
    PROJ_MON_HEAL,
    PROJ_MON_SPEED,
    PROJ_MON_SLOW,
    PROJ_MON_CONF,
    PROJ_MON_HOLD,
    PROJ_MON_STUN,
    PROJ_MON_DRAIN,
    PROJ_MON_CRUSH,
    PROJ_MAX;

    /**
     * Finds the projection corresponding to a damage element — {@link ElementEnum#ELEM_FIRE} to
     * {@link #PROJ_FIRE}, and so on. Every element has one, since C's projection parser refuses to
     * load {@code projection.txt} unless its leading entries match the element list position for
     * position; the elements are simply the first stretch of the projection list.
     *
     * <p>C exploits that alignment directly, subscripting its projection array with an element's
     * value. Here the correspondence is resolved by name instead. Both enums happen to carry a
     * matching {@code NONE} placeholder that C lacks, so their ordinals do still line up with each
     * other — but resolving by name means nothing breaks if that ever stops being true.
     *
     * <p>{@link ElementEnum#ELEM_NONE} gives {@link #PROJ_NONE} and {@link ElementEnum#ELEM_MAX}
     * gives {@link #PROJ_MAX}, since the placeholders share their suffixes.
     *
     * <p>Function getFromElementEnum coded before 261116, commented in full on 261116.
     *
     * @param elementEnum the element to find the projection for
     * @return the matching projection, or {@code null} if no constant of that name exists, which
     * means the two enums have drifted apart
     */
    public static ProjectionEnum getFromElementEnum(ElementEnum elementEnum) {
        try {
            return ProjectionEnum.valueOf("PROJ_" + elementEnum.name().substring(5));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Finds a projection by name — the Java counterpart of {@code proj_name_to_idx} in
     * {@code project.c}, which scans its name list for the first entry that equals the text,
     * ignoring ASCII case, and gives its index or -1.
     *
     * <p>C's names are bare ({@code "FIRE"}, {@code "HOLY_ORB"}, {@code "MAX"}). This method adds
     * {@code PROJ_} if the text does not already start with it, then compares against each constant
     * name ignoring case, so the bare and the whole constant name ({@code "PROJ_FIRE"}) find the
     * same constant, as with {@link ElementEnum#lookup(String)}. The prefix test itself is
     * case-sensitive, so {@code "proj_fire"} finds nothing. Matching is otherwise exact: no
     * trimming and no partial names.
     *
     * <p>Where this differs from C: the placeholders {@code "NONE"} and {@code "PROJ_NONE"} find
     * {@link #PROJ_NONE}, which C's name list does not hold, so the caller must refuse it; and the
     * answer is a constant or {@code null} rather than an index or -1. {@code "MAX"} finds
     * {@link #PROJ_MAX}, as C's trailing {@code "MAX"} entry does.
     *
     * <p>Function lookup coded before 261116, commented in full on 261116.
     *
     * @param projectionString the projection name, bare or prefixed, in any ASCII case; may be
     *                         {@code null}
     * @return the matching constant, or {@code null} if the text is {@code null} or names no
     * constant
     */
    public static ProjectionEnum lookup(String projectionString) {
        if (projectionString == null) return null;
        
        String searchString;
        if (projectionString.startsWith("PROJ_")) 
            searchString = projectionString;
        else
            searchString = "PROJ_" + projectionString;
        
        return Arrays.stream(ProjectionEnum.values()).filter(e -> e.name().equalsIgnoreCase(searchString)).findFirst().orElse(null);
    }
}
