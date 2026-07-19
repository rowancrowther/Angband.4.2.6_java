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

import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.game.Projection;
import uk.co.jackoftrades.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

/**
 * The definition of a monster blow effect (as loaded from {@code blow_effects.txt})
 * — what a successful melee blow does, its power and evaluation weight, the lore
 * description and colours, the effect/resist types and the projection used for a
 * lash form. This is the Java port of the C original's {@code struct blow_effect}
 * ({@code src/mon-blows.h}).
 *
 * @author Rowan Crowther
 */
public class BlowEffect {
    /**
     * The effect's name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * The effect's power rating.
     *
     * @author Rowan Crowther
     */
    private int power;
    /**
     * The effect's evaluation weight (used in danger rating).
     *
     * @author Rowan Crowther
     */
    private int eval;
    /**
     * Human-readable description of the effect.
     *
     * @author Rowan Crowther
     */
    private String desc;
    /**
     * Lore colour used normally.
     *
     * @author Rowan Crowther
     */
    private ColourType loreAttr;
    /**
     * Lore colour used when the player resists.
     *
     * @author Rowan Crowther
     */
    private ColourType loreAttrResist;
    /**
     * Lore colour used when the player is immune.
     *
     * @author Rowan Crowther
     */
    private ColourType loreAttrImmune;
    /**
     * What kind of player attribute protects against this effect, and so which of the two
     * resist fields below carries a value. {@code null} for the effects that name no
     * {@code effect-type:} at all, which is legal.
     *
     * @author Rowan Crowther
     */
    private BlowEffectType effectType;
    /**
     * The object flag that protects against this effect when {@link #effectType} is
     * {@link BlowEffectType#BET_FLAG}, otherwise {@link ObjectFlag#OF_NONE}.
     * <p>
     * This and {@link #elementEnumResist} together replace the single {@code int resist}
     * of the C original ({@code parse_eff_resist}, [C] {@code src/mon-init.c:388-400}),
     * which holds an object-flag index or an element index depending on the effect type.
     * Splitting them keeps that distinction in the type system rather than in a convention.
     *
     * @author Rowan Crowther
     */
    private ObjectFlag objectFlagResist;
    /**
     * The element resisted when {@link #effectType} is {@link BlowEffectType#BET_ELEMENT},
     * otherwise {@link ElementEnum#ELEM_NONE}. See {@link #objectFlagResist} for why the
     * two are held separately.
     *
     * @author Rowan Crowther
     */
    private ElementEnum elementEnumResist;
    /**
     * The projection used when this effect is delivered as a "lash".
     *
     * @author Rowan Crowther
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
     * @param effectType        what kind of attribute protects against this effect, or
     *                          {@code null} if the data file names none
     * @param objectFlagResist  protecting object flag, or {@link ObjectFlag#OF_NONE}
     * @param elementEnumResist resisted element, or {@link ElementEnum#ELEM_NONE}
     * @param lashType          lash-form projection
     * @author Rowan Crowther
     */
    public BlowEffect(String name, int power, int eval, String desc, ColourType loreAttr, ColourType loreAttrResist,
                      ColourType loreAttrImmune, BlowEffectType effectType, ObjectFlag objectFlagResist, ElementEnum elementEnumResist,
                      Projection lashType) {
        this.name = name;
        this.power = power;
        this.eval = eval;
        this.desc = desc;
        this.loreAttr = loreAttr;
        this.loreAttrResist = loreAttrResist;
        this.loreAttrImmune = loreAttrImmune;
        this.effectType = effectType;
        this.objectFlagResist = objectFlagResist;
        this.elementEnumResist = elementEnumResist;
        this.lashType = lashType;
    }

    /**
     * @return this blow effect's name
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * @return this effect's power rating, used in the monster to-hit calculation
     * @author Rowan Crowther
     */
    public int getPower() {
        return power;
    }

    /**
     * @return this effect's evaluation weight, used when rating a monster's danger
     * @author Rowan Crowther
     */
    public int getEval() {
        return eval;
    }

    /**
     * @return the description used in monster recall, e.g. {@code "reduce strength"}
     * @author Rowan Crowther
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @return the lore colour used when the player has no protection
     * @author Rowan Crowther
     */
    public ColourType getLoreAttr() {
        return loreAttr;
    }

    /**
     * @return the lore colour used when the player resists
     * @author Rowan Crowther
     */
    public ColourType getLoreAttrResist() {
        return loreAttrResist;
    }

    /**
     * @return the lore colour used when the player is immune
     * @author Rowan Crowther
     */
    public ColourType getLoreAttrImmune() {
        return loreAttrImmune;
    }

    /**
     * @return what kind of attribute protects against this effect, or {@code null} if the
     * data file names none
     * @author Rowan Crowther
     */
    public BlowEffectType getEffectType() {
        return effectType;
    }

    /**
     * @return the protecting object flag, or {@link ObjectFlag#OF_NONE} unless
     * {@link #getEffectType()} is {@link BlowEffectType#BET_FLAG}
     * @author Rowan Crowther
     */
    public ObjectFlag getObjectFlagResist() {
        return objectFlagResist;
    }

    /**
     * @return the resisted element, or {@link ElementEnum#ELEM_NONE} unless
     * {@link #getEffectType()} is {@link BlowEffectType#BET_ELEMENT}
     * @author Rowan Crowther
     */
    public ElementEnum getElementEnumResist() {
        return elementEnumResist;
    }

    /**
     * @return the projection this effect uses in its lash form; never {@code null} for an
     * effect whose data-file entry names a {@code lash-type:}
     * @author Rowan Crowther
     */
    public Projection getLashType() {
        return lashType;
    }
}
