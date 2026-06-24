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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.monsters.enums;

/**
 * The categories used to group monster race flags by purpose (obvious property,
 * display, generation, lore note, behaviour, drops, detection, environment
 * alteration, race type, vulnerability/resistance, …). Mirrors the C original's
 * {@code monster_flag_type} ({@code src/list-mon-race-flags.h}); each constant's
 * inline comment gives its meaning.
 *
 * @author ClaudeCode
 */
public enum MonsterRaceCategory {
    RFT_NONE,    /* placeholder flag */
    RFT_OBV,        /* an obvious property */
    RFT_DISP,        /* for display purposes */
    RFT_GEN,        /* related to generation */
    RFT_NOTE,        /* especially noteworthy for lore */
    RFT_BEHAV,        /* behaviour-related */
    RFT_DROP,        /* drop details */
    RFT_DET,        /* detection properties */
    RFT_ALTER,        /* environment shaping */
    RFT_RACE_N,        /* types of monster (noun) */
    RFT_RACE_A,        /* types of monster (adjective) */
    RFT_VULN,        /* vulnerabilities with no corresponding resistance */
    RFT_VULN_I,        /* vulnerabilities with a corresponding resistance */
    RFT_RES,        /* elemental resistances */
    RFT_PROT,        /* immunity from status effects */

    RFT_MAX
}
