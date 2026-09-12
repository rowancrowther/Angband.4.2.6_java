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

package uk.co.jackoftradesltd.channel.messages.data;

public record PlayerStatusView(// Player details
                               String name,
                               String title,
                               String raceName,
                               String className,
                               int level,
                               long experience,
                               long maxExperience,
                               long gold,
                               int chp,
                               int mhp,
                               int csp,
                               int msp,
                               int armourClass,
                               int speed,
                               int[] currentStats,
                               int[] maxStats,
                               String[] statString,
                               int bodyCount,

                               // Monster details
                               int monsterHealth,
                               int maxMonsterHealth,
                               boolean monsterVisible,
                               boolean playerHallucinating,
                               boolean monsterTracked,
                               boolean monsterTmdFear,
                               boolean monsterTmdDisen,
                               boolean monsterTmdCommand,
                               boolean monsterTmdConf,
                               boolean monsterTmdStun,
                               boolean monsterTmdSleep,
                               boolean monsterTmdHold,

                               // Dungeon details
                               int depth,
                               String studyStatus,
                               String studyConditions,
                               String detectionStatus,
                               String restingRepeatingState,
                               String levelFeeling,
                               String lightLevel,
                               int equipmentSlotCount) {
}
