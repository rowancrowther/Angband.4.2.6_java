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

package uk.co.jackoftrades.middle.magic;

import uk.co.jackoftrades.middle.effect.Effect;

import java.util.List;

/**
 * A single spell: its identity, the {@link MagicRealm} it belongs to, the
 * {@link Effect}s it produces when cast, and its casting parameters (required
 * level, mana cost, base failure rate and the experience awarded for first
 * casting it). This is the Java port of the C original's {@code struct class_spell}
 * ({@code src/player.h}).
 *
 * @author Rowan Crowther
 */
public class MagicSpell {
    /**
     * The spell's name.
     *
     * @author Rowan Crowther
     */
    private String spellName;
    /**
     * Human-readable description of the spell's effect.
     *
     * @author Rowan Crowther
     */
    private String spellDescription;

    /**
     * The effects produced when the spell is cast.
     *
     * @author Rowan Crowther
     */
    private List<Effect> effects;

    /**
     * Minimum class level required to learn/cast the spell.
     *
     * @author Rowan Crowther
     */
    private int level;
    /**
     * Mana cost to cast the spell.
     *
     * @author Rowan Crowther
     */
    private int mana;
    /**
     * Base failure rate (percent) before stat/level adjustment.
     *
     * @author Rowan Crowther
     */
    private int fail;
    /**
     * Experience awarded the first time the spell is cast.
     *
     * @author Rowan Crowther
     */
    private int exp;

    /**
     * Build a fully-formed spell from its casting parameters, resolved effects and description.
     *
     * @param spellName        the spell's name
     * @param level            required level
     * @param fail             base failure rate
     * @param mana             mana cost
     * @param exp              first-cast experience
     * @param effects          the effects produced when the spell is cast, in order
     * @param spellDescription the spell's description text
     * @author Rowan Crowther
     */
    public MagicSpell(String spellName, int level, int fail, int mana, int exp,
                      List<Effect> effects, String spellDescription) {
        this.spellName = spellName;
        this.level = level;
        this.fail = fail;
        this.mana = mana;
        this.exp = exp;
        this.effects = effects;
        this.spellDescription = spellDescription;
    }
}
