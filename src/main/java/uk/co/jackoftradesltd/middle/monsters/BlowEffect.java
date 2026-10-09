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

package uk.co.jackoftradesltd.middle.monsters;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

/**
 * The definition of a monster blow effect (as loaded from {@code blow_effects.txt})
 * — what a successful melee blow does, its power and evaluation weight, the lore
 * description and colours, the effect/resist types and the projection used for a
 * lash form. This is the Java port of the C original's {@code struct blow_effect}
 * ({@code mon-blows.h}), built from the {@code parse_eff_*} handlers in {@code mon-init.c}.
 * <p>
 * Four representation choices differ from the C struct, none of them a change of behaviour:
 * <ul>
 *   <li>C's {@code next} link is dropped; the effects live in a Java list in file order.</li>
 *   <li>C's {@code char *effect_type} becomes the {@link BlowEffectType} enum, {@code null}
 *       where C leaves the pointer unset.</li>
 *   <li>C's single {@code int resist} is split into {@link #objectFlagResist} and
 *       {@link #elementEnumResist}.</li>
 *   <li>C's zeroed defaults (colour {@code COLOUR_DARK}, lash type index 0) are {@code null}
 *       here when the data file does not name a value, and C's {@code NULL} description is the
 *       empty string; callers must handle both.</li>
 * </ul>
 * Instances are immutable once built; the assembler resolves every data-file string before
 * construction.
 * <p>
 * Class BlowEffect coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class BlowEffect {
    /**
     * The effect's name, as monster blows refer to it in {@code monster.txt} (for example
     * {@code POISON} or {@code LOSE_STR}). Names are matched case-sensitively when a monster
     * race resolves its blow effect.
     * <p>
     * Field name coded before 261009, commented in full on 261009.
     */
    private String name;
    /**
     * The effect's power rating. C adds it to three times the monster's level (minimum 1) to
     * give the blow's base to-hit value, in {@code mon-attack.c}, function
     * {@code chance_of_monster_hit_base()}.
     * <p>
     * Field power coded before 261009, commented in full on 261009.
     */
    private int power;
    /**
     * The effect's evaluation weight. The data file describes it as feeding power evaluation
     * in {@code eval_blow_effect()}, but that function is not in the 4.2.6 source tree: C
     * parses and stores the value in {@code mon-init.c} and nothing else in the game reads it.
     * <p>
     * Field eval coded before 261009, commented in full on 261009.
     */
    private int eval;
    /**
     * Human-readable description of the effect, used in monster recall, for example
     * {@code "reduce strength"}. An effect with no {@code desc:} line (only {@code NONE} in the
     * shipped file) gets the empty string from the assembler, where C holds a {@code NULL}
     * pointer. C builds it with {@code string_append}, so repeated {@code desc:} lines
     * concatenate.
     * <p>
     * Field desc coded before 261009, commented in full on 261009.
     */
    private String desc;
    /**
     * Lore colour used normally ({@code lore-color-base:}), and the colour
     * {@code blow_color()} in {@code mon-lore.c} returns when no protection applies.
     * {@code null} when the data file names none; C holds {@code COLOUR_DARK} (0) there.
     * <p>
     * Field loreAttr coded before 261009, commented in full on 261009.
     */
    private ColourEnum loreAttr;
    /**
     * Lore colour used when the player resists ({@code lore-color-resist:}). {@code null} when
     * the data file names none. C stores 0 for that case, and {@code blow_color()} treats a
     * zero here and in {@link #loreAttrImmune} as "this effect has no protected colours".
     * <p>
     * Field loreAttrResist coded before 261009, commented in full on 261009.
     */
    private ColourEnum loreAttrResist;
    /**
     * Lore colour used when the player is strongly resistant, that is immune
     * ({@code lore-color-immune:}). {@code null} when the data file names none; see
     * {@link #loreAttrResist} for the C zero-means-absent convention.
     * <p>
     * Field loreAttrImmune coded before 261009, commented in full on 261009.
     */
    private ColourEnum loreAttrImmune;
    /**
     * What kind of player attribute protects against this effect, and so which of the two
     * resist fields below carries a value. {@code null} for the effects that name no
     * {@code effect-type:} at all, which is legal.
     * <p>
     * Field effectType coded before 261009, commented in full on 261009.
     */
    private BlowEffectType effectType;
    /**
     * The object flag that protects against this effect when {@link #effectType} is
     * {@link BlowEffectType#BET_FLAG}, otherwise {@link ObjectFlag#OF_NONE}.
     * <p>
     * This and {@link #elementEnumResist} together replace the single {@code int resist}
     * of the C original ({@code mon-blows.h}; set by {@code parse_eff_resist()} in
     * {@code mon-init.c}), which holds an object-flag index or an element index depending on
     * the effect type. Splitting them keeps that distinction in the type system rather than
     * in a convention.
     * <p>
     * Field objectFlagResist coded before 261009, commented in full on 261009.
     */
    private ObjectFlag objectFlagResist;
    /**
     * The element resisted when {@link #effectType} is {@link BlowEffectType#BET_ELEMENT},
     * otherwise {@link ElementEnum#ELEM_NONE}. See {@link #objectFlagResist} for why the
     * two are held separately.
     * <p>
     * Field elementEnumResist coded before 261009, commented in full on 261009.
     */
    private ElementEnum elementEnumResist;
    /**
     * The projection used when this effect is delivered as a "lash". C reads it from the
     * first blow's effect in {@code effect-handler-attack.c} and {@code mon-spell.c}, the
     * latter for the lash spell's message text. {@code null} when the data file names no
     * {@code lash-type:} (only {@code NONE} in the shipped file), where C holds index 0,
     * the first element, instead.
     * <p>
     * Field lashType coded before 261009, commented in full on 261009.
     */
    private Projection lashType;

    /**
     * Build a blow effect from its parsed data-file fields. Nothing is validated or defaulted
     * here; the assembler decides what an omitted directive becomes before calling.
     * <p>
     * Constructor BlowEffect coded before 261009, commented in full on 261009.
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
     */
    public BlowEffect(String name, int power, int eval, String desc, ColourEnum loreAttr, ColourEnum loreAttrResist,
                      ColourEnum loreAttrImmune, BlowEffectType effectType, ObjectFlag objectFlagResist, ElementEnum elementEnumResist,
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
     * Accessor for the effect's name.
     * <p>
     * Function getName coded before 261009, commented in full on 261009.
     *
     * @return this blow effect's name
     */
    public String getName() {
        return name;
    }

    /**
     * Accessor for the effect's power rating.
     * <p>
     * Function getPower coded before 261009, commented in full on 261009.
     *
     * @return this effect's power rating, used in the monster to-hit calculation
     */
    public int getPower() {
        return power;
    }

    /**
     * Accessor for the effect's evaluation weight. See {@link #eval} for why nothing in the
     * C game reads it.
     * <p>
     * Function getEval coded before 261009, commented in full on 261009.
     *
     * @return this effect's evaluation weight
     */
    public int getEval() {
        return eval;
    }

    /**
     * Accessor for the recall description.
     * <p>
     * Function getDesc coded before 261009, commented in full on 261009.
     *
     * @return the description used in monster recall, e.g. {@code "reduce strength"}, or the
     * empty string if the data file gives none
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Accessor for the unprotected lore colour.
     * <p>
     * Function getLoreAttr coded before 261009, commented in full on 261009.
     *
     * @return the lore colour used when the player has no protection, or {@code null} if the
     * data file names none
     */
    public ColourEnum getLoreAttr() {
        return loreAttr;
    }

    /**
     * Accessor for the resisted lore colour.
     * <p>
     * Function getLoreAttrResist coded before 261009, commented in full on 261009.
     *
     * @return the lore colour used when the player resists, or {@code null} if the data file
     * names none
     */
    public ColourEnum getLoreAttrResist() {
        return loreAttrResist;
    }

    /**
     * Accessor for the immune lore colour.
     * <p>
     * Function getLoreAttrImmune coded before 261009, commented in full on 261009.
     *
     * @return the lore colour used when the player is immune, or {@code null} if the data file
     * names none
     */
    public ColourEnum getLoreAttrImmune() {
        return loreAttrImmune;
    }

    /**
     * Accessor for the protection category.
     * <p>
     * Function getEffectType coded before 261009, commented in full on 261009.
     *
     * @return what kind of attribute protects against this effect, or {@code null} if the
     * data file names none
     */
    public BlowEffectType getEffectType() {
        return effectType;
    }

    /**
     * Accessor for the protecting object flag.
     * <p>
     * Function getObjectFlagResist coded before 261009, commented in full on 261009.
     *
     * @return the protecting object flag, or {@link ObjectFlag#OF_NONE} unless
     * {@link #getEffectType()} is {@link BlowEffectType#BET_FLAG}
     */
    public ObjectFlag getObjectFlagResist() {
        return objectFlagResist;
    }

    /**
     * Accessor for the resisted element.
     * <p>
     * Function getElementEnumResist coded before 261009, commented in full on 261009.
     *
     * @return the resisted element, or {@link ElementEnum#ELEM_NONE} unless
     * {@link #getEffectType()} is {@link BlowEffectType#BET_ELEMENT}
     */
    public ElementEnum getElementEnumResist() {
        return elementEnumResist;
    }

    /**
     * Accessor for the lash-form projection.
     * <p>
     * Function getLashType coded before 261009, commented in full on 261009.
     *
     * @return the projection this effect uses in its lash form; never {@code null} for an
     * effect whose data-file entry names a {@code lash-type:}
     */
    public Projection getLashType() {
        return lashType;
    }
}