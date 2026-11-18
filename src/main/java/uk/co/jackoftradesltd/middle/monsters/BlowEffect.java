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
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

/**
 * The definition of a monster blow effect, as loaded from {@code blow_effects.txt}: what a
 * successful melee blow does, its power and evaluation weight, the recall description and
 * colours, which player attribute protects against it, and the projection used for its lash
 * form. This is the Java port of the C original's {@code struct blow_effect}
 * ({@code mon-blows.h}), which the {@code parse_eff_*} handlers in {@code mon-init.c} fill in.
 * <p>
 * Five representation choices differ from the C struct:
 * <ul>
 *   <li>C's {@code next} link is dropped; the effects live in a Java list in file order.</li>
 *   <li>C's {@code char *effect_type} becomes the {@link BlowEffectType} enum, {@code null}
 *       where C leaves the pointer unset.</li>
 *   <li>C's single {@code int resist} is held as three typed fields, {@link #objectFlagResist},
 *       {@link #elementEnumResist} and {@link #projectionResist}.</li>
 *   <li>C's {@code int lash_type}, an index into the projections, becomes the
 *       {@link Projection} record itself.</li>
 *   <li>C's {@code NULL} name and description are the empty string.</li>
 * </ul>
 * There are two ways to build one. The no-argument constructor gives the state C's
 * {@code mem_zalloc} gives a new record in {@code parse_eff_name}, and
 * {@code BlowEffect2Assembler} then applies each directive of the record through the setters, in
 * file order. That constructor looks up the ACID projection, C's zeroed {@code lash_type} 0, so it
 * needs the projections loaded first; C stores only the index and has no such dependency (an
 * accepted deviation, recorded in {@code docs/Accepted_Deviations.md}). The full constructor stores
 * exactly what it is given and touches no registry. Instances are mutable.
 * <p>
 * Class BlowEffect coded before 261009, commented in full on 261118.
 *
 * @author Rowan Crowther
 */
public class BlowEffect {
    /**
     * The effect's name, as monster blows refer to it in {@code monster.txt} (for example
     * {@code POISON} or {@code LOSE_STR}). C's {@code findeff} in {@code mon-init.c} compares it
     * case-sensitively, with {@code streq}, when a monster's {@code blow:} line names its effect.
     * The empty string until a {@code name:} directive sets it, where C's zeroed struct holds a
     * {@code NULL} pointer; {@code BlowEffect2Assembler} reads the empty string as "no name yet".
     * <p>
     * Field name coded before 261009, commented in full on 261118.
     */
    private String name;
    /**
     * The effect's power rating. C adds it to three times the monster's level (minimum 1) to
     * give the blow's base to-hit value, in {@code mon-attack.c}, function
     * {@code chance_of_monster_hit_base()}. Zero until a {@code power:} directive sets it, and
     * stored as read with no bound, as in C.
     * <p>
     * Field power coded before 261009, commented in full on 261118.
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
     * shipped file) holds the empty string, where C's zeroed struct holds a {@code NULL}
     * pointer. C builds it with {@code string_append}, so repeated {@code desc:} lines
     * concatenate, and {@code BlowEffect2Assembler} does the same through {@link #setDesc}.
     * <p>
     * Field desc coded before 261009, commented in full on 261118.
     */
    private String desc;
    /**
     * Lore colour used normally ({@code lore-color-base:}), and the colour
     * {@code blow_color()} in {@code mon-lore.c} returns when no protection applies.
     * {@link ColourEnum#COLOUR_DARK}, C's zero, until a {@code lore-color-base:} directive sets
     * it. Only the full constructor can leave it {@code null}.
     * <p>
     * Field loreAttr coded before 261009, commented in full on 261118.
     */
    private ColourEnum loreAttr;
    /**
     * Lore colour used when the player resists ({@code lore-color-resist:}).
     * {@link ColourEnum#COLOUR_DARK}, C's zero, when the data file names none. {@code blow_color()}
     * treats a zero here together with a zero {@link #loreAttrImmune} as "this effect has no
     * protected colours" and returns {@link #loreAttr}, so a reader of this class must take
     * {@code COLOUR_DARK} the same way. C cannot tell a named {@code Dark} from an unset colour,
     * and neither can this field. Only the full constructor can leave it {@code null}.
     * <p>
     * Field loreAttrResist coded before 261009, commented in full on 261118.
     */
    private ColourEnum loreAttrResist;
    /**
     * Lore colour used when the player is strongly resistant, that is immune
     * ({@code lore-color-immune:}). {@link ColourEnum#COLOUR_DARK}, C's zero, when the data file
     * names none; see {@link #loreAttrResist} for the zero-means-absent convention. A non-zero
     * value makes {@code blow_color()} choose by the player's resistance to the element named
     * like the effect itself ({@code ACID}, {@code ELEC}, {@code FIRE} and {@code COLD} in the
     * shipped file), before it looks at the effect type. Only the full constructor can leave it
     * {@code null}.
     * <p>
     * Field loreAttrImmune coded before 261009, commented in full on 261118.
     */
    private ColourEnum loreAttrImmune;
    /**
     * What kind of player attribute protects against this effect, and so which of the resist
     * fields below carries a value. {@code null} for the effects that name no
     * {@code effect-type:} at all, which is legal, and until a directive sets it. C stores the
     * text unchecked and {@code blow_color()} in {@code mon-lore.c} compares it with seven
     * spellings, treating any other as "base colour"; {@link BlowEffectType} is a closed enum, so
     * {@code BlowEffect2Assembler} refuses a spelling outside the seven (an accepted deviation).
     * <p>
     * Field effectType coded before 261009, commented in full on 261118.
     */
    private BlowEffectType effectType;
    /**
     * The object flag that protects against this effect when {@link #effectType} is
     * {@link BlowEffectType#BET_FLAG}, otherwise {@link ObjectFlag#OF_NONE}. For a {@code flag}
     * effect {@code blow_color()} in {@code mon-lore.c} tests it against the player's known flags.
     * <p>
     * This, {@link #elementEnumResist} and {@link #projectionResist} together replace the single
     * {@code int resist} of the C original ({@code mon-blows.h}; set by {@code parse_eff_resist()}
     * in {@code mon-init.c}), which holds an object-flag index, or an index into the
     * elements-then-projections name list, depending on the effect type. Splitting them keeps
     * that distinction in the type system rather than in a convention.
     * <p>
     * Field objectFlagResist coded before 261009, commented in full on 261118.
     */
    private ObjectFlag objectFlagResist;
    /**
     * The element resisted when {@link #effectType} is {@link BlowEffectType#BET_ELEMENT},
     * otherwise {@link ElementEnum#ELEM_NONE}. This is the form {@code blow_color()} in
     * {@code mon-lore.c} needs: for an {@code element} effect it indexes the player's element
     * table with C's {@code resist} int. {@code ELEM_NONE} is also what an {@code element} effect
     * holds when its {@code resist:} names no element, where C stores -1. See
     * {@link #objectFlagResist} for why the resists are held separately.
     * <p>
     * Field elementEnumResist coded before 261009, commented in full on 261118.
     */
    private ElementEnum elementEnumResist;
    
    /**
     * The same {@code resist:} name as {@link #elementEnumResist}, held as a
     * {@link ProjectionEnum}, when {@link #effectType} is {@link BlowEffectType#BET_ELEMENT};
     * otherwise {@link ProjectionEnum#PROJ_NONE}. C's {@code proj_name_to_idx} looks the name up
     * in one list of the elements followed by the projections, so the one int C stores can name
     * either. The port keeps what the name is in both forms: an element name such as {@code POIS}
     * fills both fields, a projection name that is no element fills only this one, and a name that
     * is neither leaves both on their {@code NONE} values, where C stores -1.
     * <p>
     * Field projectionResist coded before 261118, commented in full on 261118.
     */
    private ProjectionEnum projectionResist;
    
    /**
     * The projection used when this effect is delivered as a "lash". C's {@code lash_type} is an
     * index into the projections; here it is the {@link Projection} record. C reads it from the
     * first blow's effect in {@code effect-handler-attack.c} and {@code mon-spell.c}, the
     * latter for the lash spell's message text, with no check for an unset value.
     * <p>
     * With no {@code lash-type:} (only {@code NONE} in the shipped file) the field holds the
     * ACID projection, C's zeroed index 0, which {@link #BlowEffect()} looks up through
     * {@code WorldRegistry}. A {@code lash-type:} replaces it; a name C does not know gives
     * {@code PROJ_MISSILE}, as {@code parse_eff_lash_type} does. It is {@code null} only where
     * the lookup finds no record, as for {@code lash-type:MAX}, which names C's trailing
     * {@code MAX} entry (an accepted deviation), or when the full constructor was given
     * {@code null}.
     * <p>
     * Field lashType coded before 261009, commented in full on 261118.
     */
    private Projection lashType;

    /**
     * Build a blow effect from fields that are already resolved. Nothing is validated or
     * defaulted here, and no registry is touched: every argument is stored as given, so a
     * {@code null} colour, description or lash projection stays {@code null}, where the
     * no-argument constructor would hold {@code COLOUR_DARK}, the empty string or the ACID
     * projection.
     * <p>
     * Constructor BlowEffect coded before 261009, commented in full on 261118.
     *
     * @param name              effect name
     * @param power             power rating
     * @param eval              evaluation weight
     * @param desc              description
     * @param loreAttr          normal lore colour
     * @param loreAttrResist    resisted lore colour
     * @param loreAttrImmune    immune lore colour
     * @param effectType        what kind of attribute protects against this effect, or
     *                          {@code null} if the data file names none
     * @param objectFlagResist  protecting object flag, or {@link ObjectFlag#OF_NONE}
     * @param elementEnumResist resisted element, or {@link ElementEnum#ELEM_NONE}
     * @param projectionResist  the resisted name as a projection, or
     *                          {@link ProjectionEnum#PROJ_NONE}
     * @param lashType          lash-form projection
     */
    public BlowEffect(String name, int power, int eval, String desc, ColourEnum loreAttr, ColourEnum loreAttrResist,
                      ColourEnum loreAttrImmune, BlowEffectType effectType, ObjectFlag objectFlagResist, 
                      ElementEnum elementEnumResist, ProjectionEnum projectionResist, Projection lashType) {
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
        this.projectionResist = projectionResist;
        this.lashType = lashType;
    }
    
    /**
     * Build a blow effect in the state C's {@code parse_eff_name} gives a new record, which it
     * creates with {@code mem_zalloc}, before any directive has been read:
     * <ul>
     *   <li>name and description empty (C holds {@code NULL}), power and eval zero;</li>
     *   <li>the three lore colours {@link ColourEnum#COLOUR_DARK}, C's zero;</li>
     *   <li>no effect type, and the three resist fields on {@link ObjectFlag#OF_NONE},
     *       {@link ElementEnum#ELEM_NONE} and {@link ProjectionEnum#PROJ_NONE}, which stand in for
     *       C's zeroed {@code resist} under the accepted {@code X_NONE} register;</li>
     *   <li>the lash the ACID projection, C's zeroed {@code lash_type} 0.</li>
     * </ul>
     * The ACID lookup goes through {@code WorldRegistry}, so the projections must already be
     * loaded; C stores only the index and has no such dependency (an accepted deviation, recorded
     * in {@code docs/Accepted_Deviations.md}). The game loads the projections before the blow
     * effects.
     * <p>
     * Constructor BlowEffect coded before 261118, commented in full on 261118.
     *
     * @throws IllegalStateException if the projections have not been loaded
     */
    public BlowEffect() {
        this.name = "";
        this.power = 0;
        this.eval = 0;
        this.desc = "";
        this.loreAttr = ColourEnum.COLOUR_DARK;
        this.loreAttrResist = ColourEnum.COLOUR_DARK;
        this.loreAttrImmune = ColourEnum.COLOUR_DARK;
        this.effectType = null;
        this.objectFlagResist = ObjectFlag.OF_NONE;
        this.elementEnumResist = ElementEnum.ELEM_NONE;
        this.projectionResist = ProjectionEnum.PROJ_NONE;
        this.lashType = WorldRegistry.lookupProjectionByCode(ProjectionEnum.PROJ_ACID);
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
     * Function getLoreAttr coded before 261009, commented in full on 261118.
     *
     * @return the lore colour used when the player has no protection; {@code COLOUR_DARK} if
     * the data file names none, or {@code null} if the full constructor was given {@code null}
     */
    public ColourEnum getLoreAttr() {
        return loreAttr;
    }

    /**
     * Accessor for the resisted lore colour.
     * <p>
     * Function getLoreAttrResist coded before 261009, commented in full on 261118.
     *
     * @return the lore colour used when the player resists; {@code COLOUR_DARK} if the data
     * file names none, or {@code null} if the full constructor was given {@code null}
     */
    public ColourEnum getLoreAttrResist() {
        return loreAttrResist;
    }

    /**
     * Accessor for the immune lore colour.
     * <p>
     * Function getLoreAttrImmune coded before 261009, commented in full on 261118.
     *
     * @return the lore colour used when the player is immune; {@code COLOUR_DARK} if the data
     * file names none, or {@code null} if the full constructor was given {@code null}
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
     * Function getLashType coded before 261009, commented in full on 261118.
     *
     * @return the projection this effect uses in its lash form: the ACID projection when the
     * data file names none, {@code PROJ_MISSILE} for a name C does not know, and {@code null}
     * only where the named projection has no record (see {@link #lashType})
     */
    public Projection getLashType() {
        return lashType;
    }

    /**
     * Replaces the description. The setter stores what it is given; the appending that C does
     * with {@code string_append} in {@code parse_eff_desc} is done by the caller, which passes the
     * old description plus the new text.
     * <p>
     * Function setDesc coded before 261118, commented in full on 261118.
     *
     * @param desc the new description
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Replaces the protection category, as {@code parse_eff_effect_type} stores
     * {@code effect_type}. It does not clear the resist fields; that is the caller's decision.
     * <p>
     * Function setEffectType coded before 261118, commented in full on 261118.
     *
     * @param effectType the new category, or {@code null} for none
     */
    public void setEffectType(BlowEffectType effectType) {
        this.effectType = effectType;
    }

    /**
     * Replaces the resisted element, one of the three forms of C's {@code resist}.
     * <p>
     * Function setElementEnumResist coded before 261118, commented in full on 261118.
     *
     * @param elementEnumResist the new element, or {@link ElementEnum#ELEM_NONE}
     */
    public void setElementEnumResist(ElementEnum elementEnumResist) {
        this.elementEnumResist = elementEnumResist;
    }

    /**
     * Replaces the evaluation weight, as {@code parse_eff_eval} stores {@code eval}.
     * <p>
     * Function setEval coded before 261118, commented in full on 261118.
     *
     * @param eval the new evaluation weight
     */
    public void setEval(int eval) {
        this.eval = eval;
    }

    /**
     * Replaces the lash-form projection, as {@code parse_eff_lash_type} stores {@code lash_type}.
     * <p>
     * Function setLashType coded before 261118, commented in full on 261118.
     *
     * @param lashType the new lash projection, or {@code null} for none
     */
    public void setLashType(Projection lashType) {
        this.lashType = lashType;
    }

    /**
     * Replaces the unprotected lore colour, as {@code parse_eff_lore_color} stores
     * {@code lore_attr}.
     * <p>
     * Function setLoreAttr coded before 261118, commented in full on 261118.
     *
     * @param loreAttr the new colour
     */
    public void setLoreAttr(ColourEnum loreAttr) {
        this.loreAttr = loreAttr;
    }

    /**
     * Replaces the immune lore colour, as {@code parse_eff_lore_color_immune} stores
     * {@code lore_attr_immune}.
     * <p>
     * Function setLoreAttrImmune coded before 261118, commented in full on 261118.
     *
     * @param loreAttrImmune the new colour
     */
    public void setLoreAttrImmune(ColourEnum loreAttrImmune) {
        this.loreAttrImmune = loreAttrImmune;
    }

    /**
     * Replaces the resisted lore colour, as {@code parse_eff_lore_color_resist} stores
     * {@code lore_attr_resist}.
     * <p>
     * Function setLoreAttrResist coded before 261118, commented in full on 261118.
     *
     * @param loreAttrResist the new colour
     */
    public void setLoreAttrResist(ColourEnum loreAttrResist) {
        this.loreAttrResist = loreAttrResist;
    }

    /**
     * Replaces the name, as {@code parse_eff_name} stores {@code name}. C's {@code findeff} reads
     * it when a monster's {@code blow:} line resolves its effect.
     * <p>
     * Function setName coded before 261118, commented in full on 261118.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Replaces the protecting object flag, one of the three forms of C's {@code resist}.
     * <p>
     * Function setObjectFlagResist coded before 261118, commented in full on 261118.
     *
     * @param objectFlagResist the new flag, or {@link ObjectFlag#OF_NONE}
     */
    public void setObjectFlagResist(ObjectFlag objectFlagResist) {
        this.objectFlagResist = objectFlagResist;
    }

    /**
     * Replaces the power rating, as {@code parse_eff_power} stores {@code power}.
     * <p>
     * Function setPower coded before 261118, commented in full on 261118.
     *
     * @param power the new power rating
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Accessor for the resisted name in its projection form. See {@link #projectionResist} for how
     * it relates to {@link #getElementEnumResist()}.
     * <p>
     * Function getProjectionResist coded before 261118, commented in full on 261118.
     *
     * @return the resisted projection, or {@link ProjectionEnum#PROJ_NONE} unless
     * {@link #getEffectType()} is {@link BlowEffectType#BET_ELEMENT} and the {@code resist:} name
     * is a projection
     */
    public ProjectionEnum getProjectionResist() {
        return projectionResist;
    }

    /**
     * Replaces the resisted projection, one of the three forms of C's {@code resist}.
     * <p>
     * Function setProjectionResist coded before 261118, commented in full on 261118.
     *
     * @param projectionResist the new projection, or {@link ProjectionEnum#PROJ_NONE}
     */
    public void setProjectionResist(ProjectionEnum projectionResist) {
        this.projectionResist = projectionResist;
    }
}