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

public class MonsterSpellType {
    private MonsterSpell index;
    private MessageType msgT;
    private int hit;
    private final List<Effect> effects = new ArrayList<>();
    private final List<MonsterSpellLevel> levels = new ArrayList<>();

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
