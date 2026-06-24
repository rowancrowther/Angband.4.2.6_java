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

import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * A monster spell definition (as loaded from {@code monster_spell.txt}): which
 * {@link MonsterSpell} it is, its cast message type, its hit chance, the
 * {@link Effect}s it produces, and its per-power {@link MonsterSpellLevel}s. This
 * is the Java port of the C original's {@code struct monster_spell}
 * ({@code src/mon-spell.h}).
 *
 * @author ClaudeCode
 */
public class MonsterSpellType {
    /**
     * Which spell this defines.
     *
     * @author ClaudeCode
     */
    private MonsterSpell index;
    /**
     * The message/sound category used when casting.
     *
     * @author ClaudeCode
     */
    private MessageType msgT;
    /**
     * The spell's hit chance.
     *
     * @author ClaudeCode
     */
    private int hit;
    /**
     * The effects produced when the spell is cast.
     *
     * @author ClaudeCode
     */
    private final List<Effect> effects = new ArrayList<>();
    /**
     * The per-power levels of the spell (lore/messages scale with power).
     *
     * @author ClaudeCode
     */
    private final List<MonsterSpellLevel> levels = new ArrayList<>();

    /**
     * Build a monster spell from its parsed data-file fields.
     *
     * @param index   which spell this is
     * @param msgT    cast message category
     * @param hit     hit chance
     * @param effects the spell's effects
     * @param levels  the spell's per-power levels
     * @author ClaudeCode
     */
    @Contract(mutates = "this")
    public MonsterSpellType(MonsterSpell index, MessageType msgT, int hit, List<Effect> effects,
                            List<MonsterSpellLevel> levels) {
        this.index = index;
        this.msgT = msgT;
        this.hit = hit;
        this.effects.addAll(effects);
        this.levels.addAll(levels);
    }
}
