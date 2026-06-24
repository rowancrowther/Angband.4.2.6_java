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

package uk.co.jackoftrades.middle.enums;

/**
 * The "combat value" modifiers an object or effect can adjust — stats,
 * resistances, speed/stealth, blows/shots/might, light radius, etc. (the
 * {@code CV_*} family). The constants are self-describing by name and are
 * documented collectively here.
 *
 * @author ClaudeCode
 */
public enum ValueEnum {
    CV_INT,
    CV_WIS,
    CV_STR,
    CV_DEX,
    CV_CON,
    CV_SPEED,
    CV_STEALTH,
    CV_RES_FIRE,
    CV_RES_COLD,
    CV_RES_ELEC,
    CV_RES_SHARD,
    CV_RES_POIS,
    CV_SEARCH,
    CV_INFRA,
    CV_TUNNEL,
    CV_BLOWS,
    CV_SHOTS,
    CV_MIGHT,
    CV_LIGHT,
    CV_DAM_RED,
    CV_MOVES,
}