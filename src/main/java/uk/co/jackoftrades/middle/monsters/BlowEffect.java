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

import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.game.Projection;

/**
 * The definition of a monster blow effect (as loaded from {@code blow_effects.txt})
 * — what a successful melee blow does, its power and evaluation weight, the lore
 * description and colours, the effect/resist types and the projection used for a
 * lash form. This is the Java port of the C original's {@code struct blow_effect}
 * ({@code src/monster.h}).
 *
 * @author ClaudeCode
 */
public class BlowEffect {
    /**
     * The effect's name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * The effect's power rating.
     *
     * @author ClaudeCode
     */
    private int power;
    /**
     * The effect's evaluation weight (used in danger rating).
     *
     * @author ClaudeCode
     */
    private int eval;
    /**
     * Human-readable description of the effect.
     *
     * @author ClaudeCode
     */
    private String desc;
    /**
     * Lore colour used normally.
     *
     * @author ClaudeCode
     */
    private ColourType loreAttr;
    /**
     * Lore colour used when the player resists.
     *
     * @author ClaudeCode
     */
    private ColourType loreAttrResist;
    /**
     * Lore colour used when the player is immune.
     *
     * @author ClaudeCode
     */
    private ColourType loreAttrImmune;
    /**
     * The effect type identifier.
     *
     * @author ClaudeCode
     */
    private String effectType;
    /**
     * The resistance that mitigates the effect.
     *
     * @author ClaudeCode
     */
    private String resist;
    /**
     * The projection used when this effect is delivered as a "lash".
     *
     * @author ClaudeCode
     */
    private Projection lashType;

    /**
     * Build a blow effect from its parsed data-file fields.
     *
     * @param name           effect name
     * @param power          power rating
     * @param eval           evaluation weight
     * @param desc           description
     * @param loreAttr       normal lore colour
     * @param loreAttrResist resisted lore colour
     * @param loreAttrImmune immune lore colour
     * @param effectType     effect type identifier
     * @param resist         mitigating resistance
     * @param lashType       lash-form projection
     * @author ClaudeCode
     */
    public BlowEffect(String name, int power, int eval, String desc, ColourType loreAttr, ColourType loreAttrResist,
                      ColourType loreAttrImmune, String effectType, String resist,
                      Projection lashType) {
        this.name = name;
        this.power = power;
        this.eval = eval;
        this.desc = desc;
        this.loreAttr = loreAttr;
        this.loreAttrResist = loreAttrResist;
        this.loreAttrImmune = loreAttrImmune;
        this.effectType = effectType;
        this.resist = resist;
        this.lashType = lashType;
    }

    /**
     * @return this blow effect's name
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }
}
