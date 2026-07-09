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
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.enums.*;
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
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The discriminator: which of the payload fields below is currently live.
     *
     * @author Rowan Crowther
     */
    private EffectSubTypeEnum subType;


    private Object nullValue;

    /**
     * Payload for {@code EST_PROJ}: the projection type.
     *
     * @author Rowan Crowther
     */
    private ProjectionEnum projectionWrapper;
    /**
     * Payload for {@code EST_TMD}: the player timed effect.
     *
     * @author Rowan Crowther
     */
    private TimedEffect timedWrapper;
    /**
     * Payload for {@code EST_NOURISH}: the nourishment mode.
     *
     * @author Rowan Crowther
     */
    private EffectNourish nourishWrapper;
    /**
     * Payload for {@code EST_MON_TMD}: the monster timed effect.
     *
     * @author Rowan Crowther
     */
    private MonTimed monTimedWrapper;
    /**
     * Payload for {@code EST_SUMMON}: the summon descriptor.
     *
     * @author Rowan Crowther
     */
    private Summon summonWrapper;
    /**
     * Payload for {@code EST_SUMMON_SPEC}: the specific summon category.
     *
     * @author Rowan Crowther
     */
    private SummonType summonTypeWrapper;
    /**
     * Payload for {@code EST_STAT}: the affected stat.
     *
     * @author Rowan Crowther
     */
    private Stats statsWrapper;
    /**
     * Payload for {@code EST_ENCHANT}: the enchant mode.
     *
     * @author Rowan Crowther
     */
    private EffectEnchant enchantWrapper;
    /**
     * Payload for {@code EST_SHAPECHANGE}: the target shape.
     *
     * @author Rowan Crowther
     */
    private PlayerShape shapeWrapper;
    /**
     * Payload for {@code EST_EARTHQUAKE}: the earthquake targeting mode.
     *
     * @author Rowan Crowther
     */
    private Earthquake quakeWrapper;
    /**
     * Payload for {@code EST_GLYPH}: the glyph type.
     *
     * @author Rowan Crowther
     */
    private GlyphType glyphType;
    /**
     * Payload for {@code EST_TELEPORT}: the teleport mode.
     *
     * @author Rowan Crowther
     */
    private TeleportEnum teleportWrapper;
    /**
     * Payload for {@code EST_TELEPORT_TO}: the teleport-to mode.
     *
     * @author Rowan Crowther
     */
    private TeleportEnum teleportToWrapper;

    /**
     * Create a teleport-payload wrapper.
     *
     * @param teleportWrapper the teleport mode
     * @param to              true for a "teleport-to" payload, false for plain teleport
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(TeleportEnum teleportWrapper, boolean to) {
        this.setValue(teleportWrapper, to);
    }

    /**
     * Store a teleport payload and set the matching discriminator.
     *
     * @param teleportWrapper the teleport mode
     * @param to              true for {@code EST_TELEPORT_TO}, false for {@code EST_TELEPORT}
     * @author Rowan Crowther
     */
    public void setValue(TeleportEnum teleportWrapper, boolean to) {
        if (to) {
            this.teleportToWrapper = teleportWrapper;
            this.subType = EffectSubTypeEnum.EST_TELEPORT_TO;
        } else {
            this.teleportWrapper = teleportWrapper;
            this.subType = EffectSubTypeEnum.EST_TELEPORT;
        }
    }

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
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(GlyphType glyphType) {
        this.glyphType = glyphType;
        this.subType = EffectSubTypeEnum.EST_GLYPH;
    }

    /**
     * Store a glyph payload and set the {@code EST_GLYPH} discriminator.
     *
     * @param glyphWrapper the glyph type
     * @author Rowan Crowther
     */
    public void setValue(GlyphType glyphWrapper) {
        this.glyphType = glyphWrapper;
        this.subType = EffectSubTypeEnum.EST_GLYPH;
    }

    /**
     * Create an earthquake-payload wrapper.
     *
     * @param quakeWrapper the earthquake targeting mode
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(Earthquake quakeWrapper) {
        setValue(quakeWrapper);
    }

    /**
     * Store an earthquake payload and set the {@code EST_EARTHQUAKE} discriminator.
     *
     * @param quakeWrapper the earthquake targeting mode
     * @author Rowan Crowther
     */
    public void setValue(Earthquake quakeWrapper) {
        this.quakeWrapper = quakeWrapper;
        this.subType = EffectSubTypeEnum.EST_EARTHQUAKE;
    }

    /**
     * Create a shapechange-payload wrapper.
     *
     * @param shapeWrapper the target shape
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(PlayerShape shapeWrapper) {
        setValue(shapeWrapper);
    }

    /**
     * Store a shape payload and set the {@code EST_SHAPECHANGE} discriminator.
     *
     * @param shapeWrapper the target shape
     * @author Rowan Crowther
     */
    public void setValue(PlayerShape shapeWrapper) {
        this.shapeWrapper = shapeWrapper;
        this.subType = EffectSubTypeEnum.EST_SHAPECHANGE;
    }

    /**
     * Create an enchant-payload wrapper.
     *
     * @param enchantWrapper the enchant mode
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(EffectEnchant enchantWrapper) {
        setValue(enchantWrapper);
    }

    /**
     * Store an enchant payload and set the {@code EST_ENCHANT} discriminator.
     *
     * @param enchantWrapper the enchant mode
     * @author Rowan Crowther
     */
    public void setValue(EffectEnchant enchantWrapper) {
        this.enchantWrapper = enchantWrapper;
        this.subType = EffectSubTypeEnum.EST_ENCHANT;
    }

    /**
     * Create a stat-payload wrapper.
     *
     * @param statsWrapper the affected stat
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(Stats statsWrapper) {
        setValue(statsWrapper);
    }

    /**
     * Store a stat payload and set the {@code EST_STAT} discriminator.
     *
     * @param stat the affected stat
     * @author Rowan Crowther
     */
    public void setValue(Stats stat) {
        this.statsWrapper = stat;
        this.subType = EffectSubTypeEnum.EST_STAT;
    }

    /**
     * Create a summon-payload wrapper.
     *
     * @param summonWrapper the summon descriptor
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(Summon summonWrapper) {
        setValue(summonWrapper);
    }

    /**
     * Store a summon payload and set the {@code EST_SUMMON} discriminator.
     *
     * @param summonWrapper the summon descriptor
     * @author Rowan Crowther
     */
    public void setValue(Summon summonWrapper) {
        this.summonWrapper = summonWrapper;
        this.subType = EffectSubTypeEnum.EST_SUMMON;
    }

    /**
     * Create a specific-summon-payload wrapper.
     *
     * @param summonTypeWrapper the specific summon category
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(SummonType summonTypeWrapper) {
        setValue(summonTypeWrapper);
    }

    /**
     * Store a specific-summon payload and set the {@code EST_SUMMON_SPEC} discriminator.
     *
     * @param summonTypeWrapper the specific summon category
     * @author Rowan Crowther
     */
    public void setValue(SummonType summonTypeWrapper) {
        this.summonTypeWrapper = summonTypeWrapper;
        this.subType = EffectSubTypeEnum.EST_SUMMON_SPEC;
    }

    /**
     * Create a monster-timed-effect-payload wrapper.
     *
     * @param monTimedWrapper the monster timed effect
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(MonTimed monTimedWrapper) {
        setValue(monTimedWrapper);
    }

    /**
     * Store a monster-timed payload and set the {@code EST_MON_TMD} discriminator.
     *
     * @param monTimedWrapper the monster timed effect
     * @author Rowan Crowther
     */
    public void setValue(MonTimed monTimedWrapper) {
        this.monTimedWrapper = monTimedWrapper;
        this.subType = EffectSubTypeEnum.EST_MON_TMD;
    }

    /**
     * Create a nourish-payload wrapper.
     *
     * @param nourishWrapper the nourishment mode
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(EffectNourish nourishWrapper) {
        setValue(nourishWrapper);
    }

    /**
     * Store a nourish payload and set the {@code EST_NOURISH} discriminator.
     *
     * @param effectNourish the nourishment mode
     * @author Rowan Crowther
     */
    public void setValue(EffectNourish effectNourish) {
        this.nourishWrapper = effectNourish;
        this.subType = EffectSubTypeEnum.EST_NOURISH;
    }

    /**
     * Create a projection-payload wrapper.
     *
     * @param projectionWrapper the projection type
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(ProjectionEnum projectionWrapper) {
        setValue(projectionWrapper);
    }

    /**
     * Store a projection payload and set the {@code EST_PROJ} discriminator.
     *
     * @param projectionWrapper the projection type
     * @author Rowan Crowther
     */
    public void setValue(ProjectionEnum projectionWrapper) {
        this.projectionWrapper = projectionWrapper;
        this.subType = EffectSubTypeEnum.EST_PROJ;
    }

    /**
     * Create a timed-effect-payload wrapper.
     *
     * @param timedWrapper the player timed effect
     * @author Rowan Crowther
     */
    public EffectSubTypeWrapper(TimedEffect timedWrapper) {
        setValue(timedWrapper);
    }

    /**
     * Store a timed-effect payload and set the {@code EST_TMD} discriminator.
     *
     * @param timedWrapper the player timed effect
     * @author Rowan Crowther
     */
    public void setValue(TimedEffect timedWrapper) {
        this.timedWrapper = timedWrapper;
        this.subType = EffectSubTypeEnum.EST_TMD;
    }

    /**
     * @return the discriminator indicating which payload is currently live
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @return the stored teleport mode
     * @throws Exception if the live sub-type is not {@code EST_TELEPORT}
     * @author Rowan Crowther
     */
    public TeleportEnum getTeleportWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_TELEPORT) {
            String message = "Invalid subtype, expected EST_TELEPORT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return teleportWrapper;
    }

    /**
     * @return the stored teleport-to mode
     * @throws Exception if the live sub-type is not {@code EST_TELEPORT_TO}
     * @author Rowan Crowther
     */
    public TeleportEnum getTeleportToWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_TELEPORT_TO) {
            String message = "Invalid subtype, expected EST_TELEPORT_TO, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return teleportToWrapper;
    }
}