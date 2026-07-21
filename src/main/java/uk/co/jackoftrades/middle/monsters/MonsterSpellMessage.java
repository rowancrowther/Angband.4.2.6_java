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

package uk.co.jackoftrades.middle.monsters;

/**
 * The alternate messages a monster shows when it casts a particular spell, overriding the spell's
 * default text: what the player sees when the caster is visible ({@code vis}), invisible
 * ({@code invis}), or when the spell misses/fails ({@code miss}). Any leg with no {@code message-*}
 * line in {@code monster.txt} is {@code null}. A monster holds these keyed by spell name. This is the
 * Java port of the C original's {@code struct monster_altmsg} ({@code src/monster.h}).
 *
 * @param vis   the message shown when the caster is visible, or {@code null}
 * @param invis the message shown when the caster is unseen, or {@code null}
 * @param miss  the message shown when the spell misses or fails, or {@code null}
 * @author Rowan Crowther
 */
public record MonsterSpellMessage(String vis,
                                  String invis,
                                  String miss) {
}
