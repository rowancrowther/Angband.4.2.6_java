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

package uk.co.jackoftrades.middle.effect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.channel.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.enums.EffectEnchant;
import uk.co.jackoftrades.middle.enums.EffectNourish;
import uk.co.jackoftrades.middle.enums.GlyphType;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

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
 * @author Rowan Crowther
 */
public class EffectSubTypeWrapper {
    /**
     * Logger used to report mismatched-subtype access.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The discriminator: which of the payload fields below is currently live.
     */
    private EffectSubTypeEnum subType;


    /**
     * Payload for {@code EST_NONE} - the effect has no sub-type at all. Always {@code null}; it
     * exists so that the "no payload" case is a field like the others rather than an absence, which
     * keeps {@link #copy()} and the discriminator switch uniform.
     */
    private Object nullValue;

    /**
     * Payload for {@code EST_PROJ}: the projection type.
     */
    private ProjectionEnum projectionWrapper;
    /**
     * Payload for {@code EST_TMD}: the player timed effect.
     */
    private TimedEffect timedWrapper;
    /**
     * Payload for {@code EST_NOURISH}: the nourishment mode.
     */
    private EffectNourish nourishWrapper;
    /**
     * Payload for {@code EST_MON_TMD}: the monster timed effect.
     */
    private MonTimed monTimedWrapper;
    /**
     * Payload for {@code EST_SUMMON}: the summon descriptor.
     */
    private Summon summonWrapper;
    /**
     * Payload for {@code EST_SUMMON_SPEC}: the specific summon category.
     */
    private SummonType summonTypeWrapper;
    /**
     * Payload for {@code EST_STAT}: the affected stat.
     */
    private Stats statsWrapper;
    /**
     * Payload for {@code EST_ENCHANT}: the enchant mode.
     */
    private EffectEnchant enchantWrapper;
    /**
     * Payload for {@code EST_SHAPECHANGE}: the target shape.
     */
    private PlayerShape shapeWrapper;
    /**
     * Payload for {@code EST_EARTHQUAKE}: the earthquake targeting mode.
     */
    private Earthquake quakeWrapper;
    /**
     * Payload for {@code EST_GLYPH}: the glyph type.
     */
    private GlyphType glyphType;

    /**
     * Payload for {@code EST_TELEPORT}: may a monster use this effect to teleport the player
     * away?
     * <p>
     * Unlike every other payload in this class, the teleport subtype is not a kind but a flag.
     * The C original's {@code effect_subtype} ({@code effects.c}) returns a literal {@code 1}
     * for the single string {@code AWAY} and nothing else, and the handler only ever tests it
     * for truthiness - see the comment and guards at {@code effect-handler-general.c:2502},
     * {@code :2522} and {@code :2539}. Which <em>sort</em> of teleport happens is decided by the
     * owning effect ({@code EF_TELEPORT} vs {@code EF_TELEPORT_TO} vs {@code EF_TELEPORT_LEVEL}),
     * not by this field.
     * <p>
     * False is a meaningful value, not merely an unset one: a data file that gives no subtype at
     * all leaves C's {@code effect->subtype} at zero, i.e. the monster may not cast it.
     */
    private boolean teleportMonsterMayCast;
    /**
     * Payload for {@code EST_TELEPORT_TO}: may a monster use this effect to teleport toward the
     * player? The mirror of {@link #teleportMonsterMayCast}, set by the single string
     * {@code SELF} - see {@code effect-handler-general.c:2694}, {@code :2722} and {@code :2761}.
     * <p>
     * The two strings are not interchangeable: the C original accepts {@code AWAY} only on
     * {@code EF_TELEPORT} and {@code SELF} only on {@code EF_TELEPORT_TO}, and rejects anything
     * else - including the other's string and {@code NONE}.
     */
    private boolean teleportToMonsterMayCast;

    /**
     * Create an {@code EST_TELEPORT} payload.
     * <p>
     * A static factory rather than a constructor because the teleport payload needs two
     * booleans - the flag and the choice of which of the two teleport sub-types to tag - and a
     * two-boolean constructor signature would be both unreadable at the call site and impossible
     * to overload against its {@link #teleportTo} twin.
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
     * Build an untagged, empty wrapper for the teleport factories to populate.
     * <p>
     * Private, and deliberately the only way to reach a wrapper whose {@code subType} is null:
     * {@link #setValue} sets the discriminator on the very next statement in both factories, so
     * no half-built instance escapes.
     */
    private EffectSubTypeWrapper() {
        this.subType = null;
        this.nullValue = null;
    }

    /**
     * Store a teleport flag and set the discriminator that matches it.
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
     * @return whether a monster may use this {@code EST_TELEPORT} effect against the player
     * @throws Exception if the live sub-type is not {@code EST_TELEPORT}
     */
    public boolean getTeleportMonsterMayCast() throws Exception {
        if (this.subType != EffectSubTypeEnum.EST_TELEPORT) {
            String message = "Invalid subtype, expected EST_TELEPORT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return teleportMonsterMayCast;
    }

    /**
     * @return whether a monster may use this {@code EST_TELEPORT_TO} effect against the player
     * @throws Exception if the live sub-type is not {@code EST_TELEPORT_TO}
     */
    public boolean getTeleportToMonsterMayCast() throws Exception {
        if (this.subType != EffectSubTypeEnum.EST_TELEPORT_TO) {
            String message = "Invalid subtype, expected EST_TELEPORT_TO, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return teleportToMonsterMayCast;
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
     * <p>Constructor EffectSubTypeWrapper commented in full on 260827.
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
     * Create a glyph-payload wrapper.
     *
     * @param glyphType the glyph type
     */
    public EffectSubTypeWrapper(GlyphType glyphType) {
        this.glyphType = glyphType;
        this.subType = EffectSubTypeEnum.EST_GLYPH;
    }

    /**
     * Store a glyph payload and set the {@code EST_GLYPH} discriminator.
     *
     * @param glyphWrapper the glyph type
     */
    public void setValue(GlyphType glyphWrapper) {
        this.glyphType = glyphWrapper;
        this.subType = EffectSubTypeEnum.EST_GLYPH;
    }

    /**
     * Create an earthquake-payload wrapper.
     *
     * @param quakeWrapper the earthquake targeting mode
     */
    public EffectSubTypeWrapper(Earthquake quakeWrapper) {
        setValue(quakeWrapper);
    }

    /**
     * Store an earthquake payload and set the {@code EST_EARTHQUAKE} discriminator.
     *
     * @param quakeWrapper the earthquake targeting mode
     */
    public void setValue(Earthquake quakeWrapper) {
        this.quakeWrapper = quakeWrapper;
        this.subType = EffectSubTypeEnum.EST_EARTHQUAKE;
    }

    /**
     * Create a shapechange-payload wrapper.
     *
     * @param shapeWrapper the target shape
     */
    public EffectSubTypeWrapper(PlayerShape shapeWrapper) {
        setValue(shapeWrapper);
    }

    /**
     * Store a shape payload and set the {@code EST_SHAPECHANGE} discriminator.
     *
     * @param shapeWrapper the target shape
     */
    public void setValue(PlayerShape shapeWrapper) {
        this.shapeWrapper = shapeWrapper;
        this.subType = EffectSubTypeEnum.EST_SHAPECHANGE;
    }

    /**
     * Create an enchant-payload wrapper.
     *
     * @param enchantWrapper the enchant mode
     */
    public EffectSubTypeWrapper(EffectEnchant enchantWrapper) {
        setValue(enchantWrapper);
    }

    /**
     * Store an enchant payload and set the {@code EST_ENCHANT} discriminator.
     *
     * @param enchantWrapper the enchant mode
     */
    public void setValue(EffectEnchant enchantWrapper) {
        this.enchantWrapper = enchantWrapper;
        this.subType = EffectSubTypeEnum.EST_ENCHANT;
    }

    /**
     * Create a stat-payload wrapper.
     *
     * @param statsWrapper the affected stat
     */
    public EffectSubTypeWrapper(Stats statsWrapper) {
        setValue(statsWrapper);
    }

    /**
     * Store a stat payload and set the {@code EST_STAT} discriminator.
     *
     * @param stat the affected stat
     */
    public void setValue(Stats stat) {
        this.statsWrapper = stat;
        this.subType = EffectSubTypeEnum.EST_STAT;
    }

    /**
     * Create a summon-payload wrapper.
     *
     * @param summonWrapper the summon descriptor
     */
    public EffectSubTypeWrapper(Summon summonWrapper) {
        setValue(summonWrapper);
    }

    /**
     * Store a summon payload and set the {@code EST_SUMMON} discriminator.
     *
     * @param summonWrapper the summon descriptor
     */
    public void setValue(Summon summonWrapper) {
        this.summonWrapper = summonWrapper;
        this.subType = EffectSubTypeEnum.EST_SUMMON;
    }

    /**
     * Create a specific-summon-payload wrapper.
     *
     * @param summonTypeWrapper the specific summon category
     */
    public EffectSubTypeWrapper(SummonType summonTypeWrapper) {
        setValue(summonTypeWrapper);
    }

    /**
     * Store a specific-summon payload and set the {@code EST_SUMMON_SPEC} discriminator.
     *
     * @param summonTypeWrapper the specific summon category
     */
    public void setValue(SummonType summonTypeWrapper) {
        this.summonTypeWrapper = summonTypeWrapper;
        this.subType = EffectSubTypeEnum.EST_SUMMON_SPEC;
    }

    /**
     * Create a monster-timed-effect-payload wrapper.
     *
     * @param monTimedWrapper the monster timed effect
     */
    public EffectSubTypeWrapper(MonTimed monTimedWrapper) {
        setValue(monTimedWrapper);
    }

    /**
     * Store a monster-timed payload and set the {@code EST_MON_TMD} discriminator.
     *
     * @param monTimedWrapper the monster timed effect
     */
    public void setValue(MonTimed monTimedWrapper) {
        this.monTimedWrapper = monTimedWrapper;
        this.subType = EffectSubTypeEnum.EST_MON_TMD;
    }

    /**
     * Create a nourish-payload wrapper.
     *
     * @param nourishWrapper the nourishment mode
     */
    public EffectSubTypeWrapper(EffectNourish nourishWrapper) {
        setValue(nourishWrapper);
    }

    /**
     * Store a nourish payload and set the {@code EST_NOURISH} discriminator.
     *
     * @param effectNourish the nourishment mode
     */
    public void setValue(EffectNourish effectNourish) {
        this.nourishWrapper = effectNourish;
        this.subType = EffectSubTypeEnum.EST_NOURISH;
    }

    /**
     * Create a projection-payload wrapper.
     *
     * @param projectionWrapper the projection type
     */
    public EffectSubTypeWrapper(ProjectionEnum projectionWrapper) {
        setValue(projectionWrapper);
    }

    /**
     * Store a projection payload and set the {@code EST_PROJ} discriminator.
     *
     * @param projectionWrapper the projection type
     */
    public void setValue(ProjectionEnum projectionWrapper) {
        this.projectionWrapper = projectionWrapper;
        this.subType = EffectSubTypeEnum.EST_PROJ;
    }

    /**
     * Create a timed-effect-payload wrapper.
     *
     * @param timedWrapper the player timed effect
     */
    public EffectSubTypeWrapper(TimedEffect timedWrapper) {
        setValue(timedWrapper);
    }

    /**
     * Store a timed-effect payload and set the {@code EST_TMD} discriminator.
     *
     * @param timedWrapper the player timed effect
     */
    public void setValue(TimedEffect timedWrapper) {
        this.timedWrapper = timedWrapper;
        this.subType = EffectSubTypeEnum.EST_TMD;
    }

    /**
     * @return the discriminator indicating which payload is currently live
     */
    public EffectSubTypeEnum getSubType() {
        return subType;
    }

    /**
     * Retrieve the projection payload.
     *
     * @param subType the expected sub-type (must be {@code EST_PROJ})
     * @return the stored projection type
     * @throws Exception if the live sub-type is not {@code EST_PROJ}
     */
    public ProjectionEnum getProjectionWrapper(EffectSubTypeEnum subType) throws Exception {
        if (subType != EffectSubTypeEnum.EST_PROJ) {
            String message = "Invalid subtype, expected EST_PROJ, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return projectionWrapper;
    }

    /**
     * @return the stored player timed effect
     * @throws Exception if the live sub-type is not {@code EST_TMD}
     */
    public TimedEffect getTimedWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_TMD) {
            String message = "Invalid subtype, expected EST_TMD, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return timedWrapper;
    }

    /**
     * @return the stored nourishment mode
     * @throws Exception if the live sub-type is not {@code EST_NOURISH}
     */
    public EffectNourish getNourishWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_NOURISH) {
            String message = "Invalid subtype, expected EST_NOURISH, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return nourishWrapper;
    }

    /**
     * @return the stored monster timed effect
     * @throws Exception if the live sub-type is not {@code EST_MON_TMD}
     */
    public MonTimed getMonTimedWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_MON_TMD) {
            String message = "Invalid subtype, expected EST_MON_TMD, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return monTimedWrapper;
    }

    /**
     * @return the stored summon descriptor
     * @throws Exception if the live sub-type is not {@code EST_SUMMON}
     */
    public Summon getSummonWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SUMMON) {
            String message = "Invalid subtype, expected EST_SUMMON, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return summonWrapper;
    }

    /**
     * @return the stored specific summon category
     * @throws Exception if the live sub-type is not {@code EST_SUMMON_SPEC}
     */
    public SummonType getSummonTypeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SUMMON_SPEC) {
            String message = "Invalid subtype, expected EST_SUMMON_SPEC, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return summonTypeWrapper;
    }

    /**
     * @return the stored affected stat
     * @throws Exception if the live sub-type is not {@code EST_STAT}
     */
    public Stats getStatsWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_STAT) {
            String message = "Invalid subtype, expected EST_STAT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return statsWrapper;
    }

    /**
     * @return the stored enchant mode
     * @throws Exception if the live sub-type is not {@code EST_ENCHANT}
     */
    public EffectEnchant getEnchantWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_ENCHANT) {
            String message = "Invalid subtype, expected EST_ENCHANT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return enchantWrapper;
    }

    /**
     * @return the stored target shape
     * @throws Exception if the live sub-type is not {@code EST_SHAPECHANGE}
     */
    public PlayerShape getShapeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SHAPECHANGE) {
            String message = "Invalid subtype, expected EST_SHAPECHANGE, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return shapeWrapper;
    }

    /**
     * @return the stored earthquake targeting mode
     * @throws Exception if the live sub-type is not {@code EST_EARTHQUAKE}
     */
    public Earthquake getQuakeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_EARTHQUAKE) {
            String message = "Invalid subtype, expected EST_EARTHQUAKE, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return quakeWrapper;
    }

    /**
     * @return the stored glyph type
     * @throws Exception if the live sub-type is not {@code EST_GLYPH}
     */
    public GlyphType getGlyphType() throws Exception {
        if (subType != EffectSubTypeEnum.EST_GLYPH) {
            String message = "Invalid subtype, expected EST_GLYPH, got " + subType.toString();
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
     * <p>The payloads are all enum constants or boxed primitives, so sharing the references is safe;
     * there is nothing here a copy could mutate behind the original's back.
     *
     * <p>Function copy commented in full on 260827.
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