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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.middle.monsters.enums.MonsterAltmsgType;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

/**
 * An alternate spell-cast message for a monster: the message text, which
 * situation it applies to ({@link MonsterAltmsgType} — seen/unseen/miss) and the
 * {@link MonsterSpell} it overrides. This is the Java port of the C original's
 * {@code struct monster_altmsg} ({@code src/mon-spell.h}).
 *
 * @author ClaudeCode
 */
public class MonsterAltmsg {
    /**
     * The alternate message text.
     *
     * @author ClaudeCode
     */
    private String message;
    /**
     * Which situation this alternate message applies to.
     *
     * @author ClaudeCode
     */
    private MonsterAltmsgType msgType;
    /**
     * The spell whose default message this overrides.
     *
     * @author ClaudeCode
     */
    MonsterSpell index;

    /**
     * Build an alternate-message entry.
     *
     * @param message the message text
     * @param msgType the situation it applies to
     * @param index   the spell it overrides
     * @author ClaudeCode
     */
    public MonsterAltmsg(String message, MonsterAltmsgType msgType, MonsterSpell index) {
        this.message = message;
        this.msgType = msgType;
        this.index = index;
    }
}
