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

package uk.co.jackoftrades.middle.combat.enums;

/**
 * The complete set of projection ("GF") types — every kind of bolt, ball, beam
 * or area effect the game can resolve, from elemental damage (acid, fire, …)
 * through terrain alteration (kill/make wall, door, trap) to monster-targeting
 * effects (teleport away, turn, dispel, sleep, polymorph, …). This is the Java
 * port of the C original's {@code PROJ_*} list ({@code src/list-projections.h});
 * the constants are self-describing by name and are documented collectively here
 * rather than individually.
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
    PROJ_MON_CRUSH
}
