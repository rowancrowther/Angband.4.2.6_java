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

package uk.co.jackoftradesltd.middle.effect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.enums.EffectEnchant;
import uk.co.jackoftradesltd.middle.enums.EffectNourish;
import uk.co.jackoftradesltd.middle.enums.GlyphType;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.monsters.Summon;
import uk.co.jackoftradesltd.middle.monsters.enums.MonTimed;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.security.InvalidParameterException;

/**
 * A type-safe tagged union for an effect's sub-type parameter. An effect's
 * second argument means different things depending on the effect (a projection,
 * a timed-effect index, a summon category, a stat, …); in C this was a single
 * integer reinterpreted per effect. This wrapper instead stores the concrete
 * value in the matching typed field and records which one is live via
 * {@link #subType}. Each payload type therefore has a parallel set of members:
 * a constructor and a {@code setValue} overload that store the value and set the
 * discriminator, and a typed getter that throws if the discriminator does not
 * match — so a mismatched read fails loudly rather than returning a stale value.
 *
 * <p>The C original is {@code effect_subtype()} in {@code effects.c}, which turns the data-file
 * string into that single {@code int} according to the owning effect, and the handlers in
 * {@code effect-handler-general.c} that read it back as {@code context->subtype}. A numeric
 * string, and the {@code -1} failure value, are resolved before a wrapper is built, so neither
 * has a representation here.
 *
 * <p>Two payloads are references to mutable-looking objects ({@link Summon} and
 * {@link PlayerShape}); every other payload is an enum constant or a boolean. {@link #copy()}
 * shares the two references rather than duplicating them.
 *
 * <p>Class EffectSubTypeWrapper coded before 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public class EffectSubTypeWrapper {
    /**
     * Logger used to report mismatched-subtype access. Every payload getter logs an error with the
     * exception before throwing it.
     *
     * <p>Field logger coded before 261001, commented in full on 261001.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The discriminator: which of the payload fields below is currently live. Null when the
     * wrapper was built from an {@link EffectSubTypeEnum} other than {@code EST_NONE}, meaning the
     * sub-type did not resolve; every payload getter then refuses with "got null".
     *
     * <p>Field subType coded before 261001, commented in full on 261001.
     */
    private EffectSubTypeEnum subType;


    /**
     * Payload for {@code EST_NONE} - the effect has no sub-type at all. Always {@code null}; it
     * exists so that the "no payload" case is a field like the others rather than an absence, which
     * keeps {@link #copy()} uniform. There is no getter for it: nothing reads an
     * {@code EST_NONE} payload.
     *
     * <p>Field nullValue coded before 261001, commented in full on 261001.
     */
    private Object nullValue;

    /**
     * Payload for {@code EST_PROJ}: the projection type. In C this is the result of
     * {@code proj_name_to_idx()}, used by the projection-based effects.
     *
     * <p>Field projectionWrapper coded before 261001, commented in full on 261001.
     */
    private ProjectionEnum projectionWrapper;
    /**
     * Payload for {@code EST_TMD}: the player timed effect. In C this is the result of
     * {@code timed_name_to_idx()}.
     *
     * <p>Field timedWrapper coded before 261001, commented in full on 261001.
     */
    private TimedEffect timedWrapper;
    /**
     * Payload for {@code EST_NOURISH}: the nourishment mode. In C the strings {@code INC_BY},
     * {@code DEC_BY}, {@code SET_TO} and {@code INC_TO} become 0 to 3.
     *
     * <p>Field nourishWrapper coded before 261001, commented in full on 261001.
     */
    private EffectNourish nourishWrapper;
    /**
     * Payload for {@code EST_MON_TMD}: the monster timed effect. In C this is the result of
     * {@code mon_timed_name_to_idx()}.
     *
     * <p>Field monTimedWrapper coded before 261001, commented in full on 261001.
     */
    private MonTimed monTimedWrapper;
    /**
     * Payload for {@code EST_SUMMON}: the summon descriptor. In C this is the result of
     * {@code summon_name_to_idx()}. A reference to a {@link Summon} object, shared by
     * {@link #copy()}.
     *
     * <p>Field summonWrapper coded before 261001, commented in full on 261001.
     */
    private Summon summonWrapper;
    /**
     * Payload for {@code EST_SUMMON_SPEC}: the specific summon category. Has no separate C
     * counterpart; it is a Java-side split of the summon family.
     *
     * <p>Field summonTypeWrapper coded before 261001, commented in full on 261001.
     */
    private SummonType summonTypeWrapper;
    /**
     * Payload for {@code EST_STAT}: the affected stat. In C this is the result of
     * {@code stat_name_to_idx()}.
     *
     * <p>Field statsWrapper coded before 261001, commented in full on 261001.
     */
    private Stats statsWrapper;
    /**
     * Payload for {@code EST_ENCHANT}: the enchant mode. In C the strings {@code TOBOTH},
     * {@code TOHIT}, {@code TODAM} and {@code TOAC} become the {@code ENCH_*} bit values.
     *
     * <p>Field enchantWrapper coded before 261001, commented in full on 261001.
     */
    private EffectEnchant enchantWrapper;
    /**
     * Payload for {@code EST_SHAPECHANGE}: the target shape. In C this is the result of
     * {@code shape_name_to_idx()}, later looked up with {@code player_shape_by_idx()}. A
     * reference to a {@link PlayerShape}, shared by {@link #copy()}.
     *
     * <p>Field shapeWrapper coded before 261001, commented in full on 261001.
     */
    private PlayerShape shapeWrapper;
    /**
     * Payload for {@code EST_EARTHQUAKE}: the earthquake targeting mode. In C {@code TARGETED}
     * becomes 1 and {@code NONE} becomes 0.
     *
     * <p>Field quakeWrapper coded before 261001, commented in full on 261001.
     */
    private Earthquake quakeWrapper;
    /**
     * Payload for {@code EST_GLYPH}: the glyph type. In C {@code WARDING} and {@code DECOY}
     * become {@code GLYPH_WARDING} and {@code GLYPH_DECOY}.
     *
     * <p>Field glyphType coded before 261001, commented in full on 261001.
     */
    private GlyphType glyphType;

    /**
     * Payload for {@code EST_TELEPORT}: may a monster use this effect to teleport the player
     * away?
     * <p>
     * Unlike every other payload in this class, the teleport subtype is not a kind but a flag.
     * The C original's {@code effect_subtype} ({@code effects.c}) returns a literal {@code 1}
     * for the single string {@code AWAY} and nothing else, and the handler only ever tests it
     * for truthiness - see the comment and guards in the teleport handler in
     * {@code effect-handler-general.c}. Which <em>sort</em> of teleport happens is decided by the
     * owning effect, not by this field.
     * <p>
     * False is a meaningful value, not merely an unset one: a data file that gives no subtype at
     * all leaves C's {@code effect->subtype} at zero, i.e. the monster may not cast it.
     *
     * <p>Field teleportMonsterMayCast coded before 261001, commented in full on 261001.
     */
    private boolean teleportMonsterMayCast;
    /**
     * Payload for {@code EST_TELEPORT_TO}: may a monster use this effect to teleport toward the
     * player? The mirror of {@link #teleportMonsterMayCast}, set by the single string
     * {@code SELF} - see the teleport-to handler in {@code effect-handler-general.c}.
     * <p>
     * The two strings are not interchangeable: the C original accepts {@code AWAY} only on
     * {@code EF_TELEPORT} and {@code SELF} only on {@code EF_TELEPORT_TO}, and rejects anything
     * else - including the other's string and {@code NONE}.
     *
     * <p>Field teleportToMonsterMayCast coded before 261001, commented in full on 261001.
     */
    private boolean teleportToMonsterMayCast;

    /**
     * Build an untagged, empty wrapper for the teleport factories to populate.
     * <p>
     * Private, and used only by the two teleport factories, which call
     * {@link #setValue(boolean, boolean)} on the very next statement, so no half-built instance
     * escapes from them. It is not the only way to get a wrapper whose {@code subType} is null:
     * {@link #EffectSubTypeWrapper(EffectSubTypeEnum)} does the same on purpose for an unresolved
     * sub-type.
     *
     * <p>Constructor EffectSubTypeWrapper() coded before 261001, commented in full on 261001.
     */
    private EffectSubTypeWrapper() {
        this.subType = null;
        this.nullValue = null;
    }

    /**
     * Builds a wrapper carrying no payload, for an effect whose sub-type is
     * {@code EST_NONE} or unknown.
     *
     * <p>The two branches differ in one respect only: a declared {@code EST_NONE} keeps its
     * discriminator, while any other value is recorded as {@code null}. That distinguishes "this
     * effect states that it has no sub-type" from "this effect's sub-type did not resolve", which
     * matters to the accessors below - each throws when asked for a payload the discriminator does
     * not name.
     *
     * <p>In C, {@code effect_subtype()} returns {@code 0} for {@code NONE} on effects that take
     * only a radius, and {@code -1} for a string it cannot resolve; this constructor is where those
     * two outcomes are kept apart.
     *
     * <p>Constructor EffectSubTypeWrapper(EffectSubTypeEnum) coded before 261001, commented in full
     * on 261001.
     *
     * @param subType {@code EST_NONE} to record an effect with no sub-type; anything else leaves the
     *                discriminator unset
     */
    public EffectSubTypeWrapper(EffectSubTypeEnum subType) {
        if (subType == EffectSubTypeEnum.EST_NONE) {
            this.subType = subType;
            this.nullValue = null;
        } else {
            this.subType = null;
            this.nullValue = null;
        }
    }

    /**
     * Create a glyph-payload wrapper, tagged {@code EST_GLYPH}.
     *
     * <p>Constructor EffectSubTypeWrapper(GlyphType) coded before 261001, commented in full on
     * 261001.
     *
     * @param glyphType the glyph type
     */
    public EffectSubTypeWrapper(GlyphType glyphType) {
        this.glyphType = glyphType;
        this.subType = EffectSubTypeEnum.EST_GLYPH;
    }

    /**
     * Create an earthquake-payload wrapper, tagged {@code EST_EARTHQUAKE}.
     *
     * <p>Constructor EffectSubTypeWrapper(Earthquake) coded before 261001, commented in full on
     * 261001.
     *
     * @param quakeWrapper the earthquake targeting mode
     */
    public EffectSubTypeWrapper(Earthquake quakeWrapper) {
        setValue(quakeWrapper);
    }

    /**
     * Create a shapechange-payload wrapper, tagged {@code EST_SHAPECHANGE}.
     *
     * <p>Constructor EffectSubTypeWrapper(PlayerShape) coded before 261001, commented in full on
     * 261001.
     *
     * @param shapeWrapper the target shape
     */
    public EffectSubTypeWrapper(PlayerShape shapeWrapper) {
        setValue(shapeWrapper);
    }

    /**
     * Create an enchant-payload wrapper, tagged {@code EST_ENCHANT}.
     *
     * <p>Constructor EffectSubTypeWrapper(EffectEnchant) coded before 261001, commented in full on
     * 261001.
     *
     * @param enchantWrapper the enchant mode
     */
    public EffectSubTypeWrapper(EffectEnchant enchantWrapper) {
        setValue(enchantWrapper);
    }

    /**
     * Create a stat-payload wrapper, tagged {@code EST_STAT}.
     *
     * <p>Constructor EffectSubTypeWrapper(Stats) coded before 261001, commented in full on 261001.
     *
     * @param statsWrapper the affected stat
     */
    public EffectSubTypeWrapper(Stats statsWrapper) {
        setValue(statsWrapper);
    }

    /**
     * Create a summon-payload wrapper, tagged {@code EST_SUMMON}.
     *
     * <p>Constructor EffectSubTypeWrapper(Summon) coded before 261001, commented in full on 261001.
     *
     * @param summonWrapper the summon descriptor
     */
    public EffectSubTypeWrapper(Summon summonWrapper) {
        setValue(summonWrapper);
    }

    /**
     * Create a specific-summon-payload wrapper, tagged {@code EST_SUMMON_SPEC}.
     *
     * <p>Constructor EffectSubTypeWrapper(SummonType) coded before 261001, commented in full on
     * 261001.
     *
     * @param summonTypeWrapper the specific summon category
     */
    public EffectSubTypeWrapper(SummonType summonTypeWrapper) {
        setValue(summonTypeWrapper);
    }

    /**
     * Create a monster-timed-effect-payload wrapper, tagged {@code EST_MON_TMD}.
     *
     * <p>Constructor EffectSubTypeWrapper(MonTimed) coded before 261001, commented in full on
     * 261001.
     *
     * @param monTimedWrapper the monster timed effect
     */
    public EffectSubTypeWrapper(MonTimed monTimedWrapper) {
        setValue(monTimedWrapper);
    }

    /**
     * Create a nourish-payload wrapper, tagged {@code EST_NOURISH}.
     *
     * <p>Constructor EffectSubTypeWrapper(EffectNourish) coded before 261001, commented in full on
     * 261001.
     *
     * @param nourishWrapper the nourishment mode
     */
    public EffectSubTypeWrapper(EffectNourish nourishWrapper) {
        setValue(nourishWrapper);
    }

    /**
     * Create a projection-payload wrapper, tagged {@code EST_PROJ}.
     *
     * <p>Constructor EffectSubTypeWrapper(ProjectionEnum) coded before 261001, commented in full on
     * 261001.
     *
     * @param projectionWrapper the projection type
     */
    public EffectSubTypeWrapper(ProjectionEnum projectionWrapper) {
        setValue(projectionWrapper);
    }

    /**
     * Create a timed-effect-payload wrapper, tagged {@code EST_TMD}.
     *
     * <p>Constructor EffectSubTypeWrapper(TimedEffect) coded before 261001, commented in full on
     * 261001.
     *
     * @param timedWrapper the player timed effect
     */
    public EffectSubTypeWrapper(TimedEffect timedWrapper) {
        setValue(timedWrapper);
    }

    /**
     * Create an {@code EST_TELEPORT} payload.
     * <p>
     * A static factory rather than a constructor because the teleport payload needs two
     * booleans - the flag and the choice of which of the two teleport sub-types to tag - and a
     * two-boolean constructor signature would be both unreadable at the call site and impossible
     * to overload against its {@link #teleportTo} twin.
     *
     * <p>Function teleport coded before 261001, commented in full on 261001.
     *
     * @param monsterMayCast whether a monster may cast this at the player, i.e. whether the data
     *                       file supplied {@code AWAY}
     * @return a wrapper tagged {@code EST_TELEPORT}
     */
    public static EffectSubTypeWrapper teleport(boolean monsterMayCast) {
        EffectSubTypeWrapper result = new EffectSubTypeWrapper();
        result.setValue(monsterMayCast, false);
        return result;
    }

    /**
     * Create an {@code EST_TELEPORT_TO} payload. See {@link #teleport} for why this is a factory.
     * The flag is {@code true} only when the data file supplied {@code SELF}; in C that string is
     * accepted on {@code EF_TELEPORT_TO} alone.
     *
     * <p>Function teleportTo coded before 261001, commented in full on 261001.
     *
     * @param monsterMayCast whether a monster may cast this at the player, i.e. whether the data
     *                       file supplied {@code SELF}
     * @return a wrapper tagged {@code EST_TELEPORT_TO}
     */
    public static EffectSubTypeWrapper teleportTo(boolean monsterMayCast) {
        EffectSubTypeWrapper result = new EffectSubTypeWrapper();
        result.setValue(monsterMayCast, true);
        return result;
    }

    /**
     * Store a teleport flag and set the discriminator that matches it. Only the flag belonging to
     * the chosen tag is written; the other flag keeps its default of {@code false}.
     *
     * <p>Function setValue(boolean, boolean) coded before 261001, commented in full on 261001.
     *
     * @param monsterMayCast the flag to store
     * @param to             true to tag this {@code EST_TELEPORT_TO}, false for
     *                       {@code EST_TELEPORT}; this selects which effect the payload belongs
     *                       to, and is not itself part of the ported subtype value
     */
    private void setValue(boolean monsterMayCast, boolean to) {
        if (to) {
            this.teleportToMonsterMayCast = monsterMayCast;
            this.subType = EffectSubTypeEnum.EST_TELEPORT_TO;
        } else {
            this.teleportMonsterMayCast = monsterMayCast;
            this.subType = EffectSubTypeEnum.EST_TELEPORT;
        }
    }

    /**
     * Read the {@code EST_TELEPORT} flag: whether the data file supplied {@code AWAY}, which lets a
     * monster teleport the player away.
     *
     * <p>Function getTeleportMonsterMayCast coded before 261001, commented in full on 261001.
     *
     * @return whether a monster may use this {@code EST_TELEPORT} effect against the player
     * @throws Exception if the live sub-type is not {@code EST_TELEPORT}
     */
    public boolean getTeleportMonsterMayCast() throws Exception {
        if (this.subType != EffectSubTypeEnum.EST_TELEPORT) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_TELEPORT, got null";
            else
                message = "Invalid subtype, expected EST_TELEPORT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return teleportMonsterMayCast;
    }

    /**
     * Read the {@code EST_TELEPORT_TO} flag: whether the data file supplied {@code SELF}, which
     * lets a monster teleport toward the player.
     *
     * <p>Function getTeleportToMonsterMayCast coded before 261001, commented in full on 261001.
     *
     * @return whether a monster may use this {@code EST_TELEPORT_TO} effect against the player
     * @throws Exception if the live sub-type is not {@code EST_TELEPORT_TO}
     */
    public boolean getTeleportToMonsterMayCast() throws Exception {
        if (this.subType != EffectSubTypeEnum.EST_TELEPORT_TO) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_TELEPORT_TO, got null";
            else
                message = "Invalid subtype, expected EST_TELEPORT_TO, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return teleportToMonsterMayCast;
    }

    /**
     * Store a glyph payload and set the {@code EST_GLYPH} discriminator. Other payload fields are
     * left as they were.
     *
     * <p>Function setValue(GlyphType) coded before 261001, commented in full on 261001.
     *
     * @param glyphWrapper the glyph type
     */
    public void setValue(GlyphType glyphWrapper) {
        this.glyphType = glyphWrapper;
        this.subType = EffectSubTypeEnum.EST_GLYPH;
    }

    /**
     * Store an earthquake payload and set the {@code EST_EARTHQUAKE} discriminator. Other payload
     * fields are left as they were.
     *
     * <p>Function setValue(Earthquake) coded before 261001, commented in full on 261001.
     *
     * @param quakeWrapper the earthquake targeting mode
     */
    public void setValue(Earthquake quakeWrapper) {
        this.quakeWrapper = quakeWrapper;
        this.subType = EffectSubTypeEnum.EST_EARTHQUAKE;
    }

    /**
     * Store a shape payload and set the {@code EST_SHAPECHANGE} discriminator. Other payload fields
     * are left as they were.
     *
     * <p>Function setValue(PlayerShape) coded before 261001, commented in full on 261001.
     *
     * @param shapeWrapper the target shape
     */
    public void setValue(PlayerShape shapeWrapper) {
        this.shapeWrapper = shapeWrapper;
        this.subType = EffectSubTypeEnum.EST_SHAPECHANGE;
    }

    /**
     * Store an enchant payload and set the {@code EST_ENCHANT} discriminator. Other payload fields
     * are left as they were.
     *
     * <p>Function setValue(EffectEnchant) coded before 261001, commented in full on 261001.
     *
     * @param enchantWrapper the enchant mode
     */
    public void setValue(EffectEnchant enchantWrapper) {
        this.enchantWrapper = enchantWrapper;
        this.subType = EffectSubTypeEnum.EST_ENCHANT;
    }

    /**
     * Store a stat payload and set the {@code EST_STAT} discriminator. Other payload fields are
     * left as they were.
     *
     * <p>Function setValue(Stats) coded before 261001, commented in full on 261001.
     *
     * @param stat the affected stat
     */
    public void setValue(Stats stat) {
        this.statsWrapper = stat;
        this.subType = EffectSubTypeEnum.EST_STAT;
    }

    /**
     * Store a summon payload and set the {@code EST_SUMMON} discriminator. Other payload fields are
     * left as they were.
     *
     * <p>Function setValue(Summon) coded before 261001, commented in full on 261001.
     *
     * @param summonWrapper the summon descriptor
     */
    public void setValue(Summon summonWrapper) {
        this.summonWrapper = summonWrapper;
        this.subType = EffectSubTypeEnum.EST_SUMMON;
    }

    /**
     * Store a specific-summon payload and set the {@code EST_SUMMON_SPEC} discriminator. Other
     * payload fields are left as they were.
     *
     * <p>Function setValue(SummonType) coded before 261001, commented in full on 261001.
     *
     * @param summonTypeWrapper the specific summon category
     */
    public void setValue(SummonType summonTypeWrapper) {
        this.summonTypeWrapper = summonTypeWrapper;
        this.subType = EffectSubTypeEnum.EST_SUMMON_SPEC;
    }

    /**
     * Store a monster-timed payload and set the {@code EST_MON_TMD} discriminator. Other payload
     * fields are left as they were.
     *
     * <p>Function setValue(MonTimed) coded before 261001, commented in full on 261001.
     *
     * @param monTimedWrapper the monster timed effect
     */
    public void setValue(MonTimed monTimedWrapper) {
        this.monTimedWrapper = monTimedWrapper;
        this.subType = EffectSubTypeEnum.EST_MON_TMD;
    }

    /**
     * Store a nourish payload and set the {@code EST_NOURISH} discriminator. Other payload fields
     * are left as they were.
     *
     * <p>Function setValue(EffectNourish) coded before 261001, commented in full on 261001.
     *
     * @param effectNourish the nourishment mode
     */
    public void setValue(EffectNourish effectNourish) {
        this.nourishWrapper = effectNourish;
        this.subType = EffectSubTypeEnum.EST_NOURISH;
    }

    /**
     * Store a projection payload and set the {@code EST_PROJ} discriminator. Other payload fields
     * are left as they were.
     *
     * <p>Function setValue(ProjectionEnum) coded before 261001, commented in full on 261001.
     *
     * @param projectionWrapper the projection type
     */
    public void setValue(ProjectionEnum projectionWrapper) {
        this.projectionWrapper = projectionWrapper;
        this.subType = EffectSubTypeEnum.EST_PROJ;
    }

    /**
     * Store a timed-effect payload and set the {@code EST_TMD} discriminator. Other payload fields
     * are left as they were.
     *
     * <p>Function setValue(TimedEffect) coded before 261001, commented in full on 261001.
     *
     * @param timedWrapper the player timed effect
     */
    public void setValue(TimedEffect timedWrapper) {
        this.timedWrapper = timedWrapper;
        this.subType = EffectSubTypeEnum.EST_TMD;
    }

    /**
     * Read the discriminator. Unlike the payload getters this never throws; it is how a caller
     * decides which getter is safe to call, and it returns {@code null} for an unresolved
     * sub-type.
     *
     * <p>Function getSubType coded before 261001, commented in full on 261001.
     *
     * @return the discriminator indicating which payload is currently live, or {@code null}
     */
    public EffectSubTypeEnum getSubType() {
        return subType;
    }

    /**
     * Retrieve the projection payload. Refuses unless this wrapper's own discriminator is
     * {@code EST_PROJ}, as every other payload getter does.
     *
     * <p>Function getProjectionWrapper coded before 261001, updated on 261001 when the guard
     * stopped testing a caller-supplied argument and the parameter was removed, commented in full
     * on 261001.
     *
     * @return the stored projection type
     * @throws Exception if the live sub-type is not {@code EST_PROJ}
     */
    public ProjectionEnum getProjectionWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_PROJ) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_PROJ, got null";
            else
                message = "Invalid subtype, expected EST_PROJ, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return projectionWrapper;
    }

    /**
     * Retrieve the player timed-effect payload, refusing unless the discriminator is
     * {@code EST_TMD}.
     *
     * <p>Function getTimedWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored player timed effect
     * @throws Exception if the live sub-type is not {@code EST_TMD}
     */
    public TimedEffect getTimedWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_TMD) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_TMD, got null";
            else
                message = "Invalid subtype, expected EST_TMD, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return timedWrapper;
    }

    /**
     * Retrieve the nourishment-mode payload, refusing unless the discriminator is
     * {@code EST_NOURISH}.
     *
     * <p>Function getNourishWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored nourishment mode
     * @throws Exception if the live sub-type is not {@code EST_NOURISH}
     */
    public EffectNourish getNourishWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_NOURISH) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_NOURISH, got null";
            else
                message = "Invalid subtype, expected EST_NOURISH, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return nourishWrapper;
    }

    /**
     * Retrieve the monster timed-effect payload, refusing unless the discriminator is
     * {@code EST_MON_TMD}.
     *
     * <p>Function getMonTimedWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored monster timed effect
     * @throws Exception if the live sub-type is not {@code EST_MON_TMD}
     */
    public MonTimed getMonTimedWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_MON_TMD) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_MON_TMD, got null";
            else
                message = "Invalid subtype, expected EST_MON_TMD, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return monTimedWrapper;
    }

    /**
     * Retrieve the summon payload, refusing unless the discriminator is {@code EST_SUMMON}. The
     * returned {@link Summon} is the same object the wrapper holds, not a copy.
     *
     * <p>Function getSummonWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored summon descriptor
     * @throws Exception if the live sub-type is not {@code EST_SUMMON}
     */
    public Summon getSummonWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SUMMON) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_SUMMON, got null";
            else
                message = "Invalid subtype, expected EST_SUMMON, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return summonWrapper;
    }

    /**
     * Retrieve the specific-summon payload, refusing unless the discriminator is
     * {@code EST_SUMMON_SPEC}.
     *
     * <p>Function getSummonTypeWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored specific summon category
     * @throws Exception if the live sub-type is not {@code EST_SUMMON_SPEC}
     */
    public SummonType getSummonTypeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SUMMON_SPEC) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_SUMMON_SPEC, got null";
            else
                message = "Invalid subtype, expected EST_SUMMON_SPEC, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return summonTypeWrapper;
    }

    /**
     * Retrieve the stat payload, refusing unless the discriminator is {@code EST_STAT}.
     *
     * <p>Function getStatsWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored affected stat
     * @throws Exception if the live sub-type is not {@code EST_STAT}
     */
    public Stats getStatsWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_STAT) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_STAT, got null";
            else
                message = "Invalid subtype, expected EST_STAT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return statsWrapper;
    }

    /**
     * Retrieve the enchant-mode payload, refusing unless the discriminator is
     * {@code EST_ENCHANT}.
     *
     * <p>Function getEnchantWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored enchant mode
     * @throws Exception if the live sub-type is not {@code EST_ENCHANT}
     */
    public EffectEnchant getEnchantWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_ENCHANT) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_ENCHANT, got null";
            else
                message = "Invalid subtype, expected EST_ENCHANT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return enchantWrapper;
    }

    /**
     * Retrieve the shape payload, refusing unless the discriminator is {@code EST_SHAPECHANGE}.
     * The returned {@link PlayerShape} is the same object the wrapper holds, not a copy.
     *
     * <p>Function getShapeWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored target shape
     * @throws Exception if the live sub-type is not {@code EST_SHAPECHANGE}
     */
    public PlayerShape getShapeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SHAPECHANGE) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_SHAPECHANGE, got null";
            else
                message = "Invalid subtype, expected EST_SHAPECHANGE, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return shapeWrapper;
    }

    /**
     * Retrieve the earthquake payload, refusing unless the discriminator is
     * {@code EST_EARTHQUAKE}.
     *
     * <p>Function getQuakeWrapper coded before 261001, commented in full on 261001.
     *
     * @return the stored earthquake targeting mode
     * @throws Exception if the live sub-type is not {@code EST_EARTHQUAKE}
     */
    public Earthquake getQuakeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_EARTHQUAKE) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_EARTHQUAKE, got null";
            else
                message = "Invalid subtype, expected EST_EARTHQUAKE, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return quakeWrapper;
    }

    /**
     * Retrieve the glyph payload, refusing unless the discriminator is {@code EST_GLYPH}.
     *
     * <p>Function getGlyphType coded before 261001, commented in full on 261001.
     *
     * @return the stored glyph type
     * @throws Exception if the live sub-type is not {@code EST_GLYPH}
     */
    public GlyphType getGlyphType() throws Exception {
        if (subType != EffectSubTypeEnum.EST_GLYPH) {
            String message;
            if (subType == null)
                message = "Invalid subtype, expected EST_GLYPH, got null";
            else
                message = "Invalid subtype, expected EST_GLYPH, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return glyphType;
    }

    /**
     * Returns an independent copy of this wrapper.
     *
     * <p>Every field is copied, not just the live one. That is deliberate: which field is live is
     * decided by {@code subType}, and copying the lot means the copy behaves identically without
     * this method having to switch on the discriminator - and without it needing changing when a new
     * sub-type is added.
     *
     * <p>The copy is shallow. Every payload is an enum constant or a primitive boolean except
     * {@link Summon} and {@link PlayerShape}, which are shared by reference with the original.
     * Nothing in this class mutates them, so the copy and the original cannot disturb each other
     * through this wrapper, but a caller who mutated one of those objects would affect both.
     *
     * <p>Function copy coded before 261001, commented in full on 261001.
     *
     * @return a new wrapper carrying the same discriminator and payloads
     */
    public EffectSubTypeWrapper copy() {
        EffectSubTypeWrapper copy = new EffectSubTypeWrapper();
        copy.subType = this.subType;
        copy.nullValue = this.nullValue;
        copy.projectionWrapper = this.projectionWrapper;
        copy.timedWrapper = this.timedWrapper;
        copy.nourishWrapper = this.nourishWrapper;
        copy.monTimedWrapper = this.monTimedWrapper;
        copy.summonWrapper = this.summonWrapper;
        copy.summonTypeWrapper = this.summonTypeWrapper;
        copy.statsWrapper = this.statsWrapper;
        copy.enchantWrapper = this.enchantWrapper;
        copy.shapeWrapper = this.shapeWrapper;
        copy.quakeWrapper = this.quakeWrapper;
        copy.glyphType = this.glyphType;
        copy.teleportMonsterMayCast = this.teleportMonsterMayCast;
        copy.teleportToMonsterMayCast = this.teleportToMonsterMayCast;
        return copy;
    }
}