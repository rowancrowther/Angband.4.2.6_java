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

package uk.co.jackoftrades.middle.effect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.enums.*;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.security.InvalidParameterException;

public class EffectSubTypeWrapper {
    private static final Logger logger = LogManager.getLogger();

    private EffectSubTypeEnum subType;

    private ProjectionEnum projectionWrapper;
    private TimedEffect timedWrapper;
    private EffectNourish nourishWrapper;
    private EffectMonTimed monTimedWrapper;
    private Summon summonWrapper;
    private SummonType summonTypeWrapper;
    private Stats statsWrapper;
    private EffectEnchant enchantWrapper;
    private PlayerShape shapeWrapper;
    private Earthquake quakeWrapper;
    private GlyphType glyphType;
    private TeleportEnum teleportWrapper;
    private TeleportEnum teleportToWrapper;

    public EffectSubTypeWrapper(TeleportEnum teleportWrapper, boolean to) {
        this.setValue(teleportWrapper, to);
    }

    public void setValue(TeleportEnum teleportWrapper, boolean to) {
        if (to) {
            this.teleportToWrapper = teleportWrapper;
            this.subType = EffectSubTypeEnum.EST_TELEPORT_TO;
        } else {
            this.teleportWrapper = teleportWrapper;
            this.subType = EffectSubTypeEnum.EST_TELEPORT;
        }
    }

    public EffectSubTypeWrapper(GlyphType glyphType) {
        setValue(glyphType);
    }

    public void setValue(GlyphType glyphWrapper) {
        this.glyphType = glyphWrapper;
        this.subType = EffectSubTypeEnum.EST_GLYPH;
    }

    public EffectSubTypeWrapper(Earthquake quakeWrapper) {
        setValue(quakeWrapper);
    }

    public void setValue(Earthquake quakeWrapper) {
        this.quakeWrapper = quakeWrapper;
        this.subType = EffectSubTypeEnum.EST_EARTHQUAKE;
    }

    public EffectSubTypeWrapper(PlayerShape shapeWrapper) {
        setValue(shapeWrapper);
    }

    public void setValue(PlayerShape shapeWrapper) {
        this.shapeWrapper = shapeWrapper;
        this.subType = EffectSubTypeEnum.EST_SHAPECHANGE;
    }

    public EffectSubTypeWrapper(EffectEnchant enchantWrapper) {
        setValue(enchantWrapper);
    }

    public void setValue(EffectEnchant enchantWrapper) {
        this.enchantWrapper = enchantWrapper;
        this.subType = EffectSubTypeEnum.EST_ENCHANT;
    }

    public EffectSubTypeWrapper(Stats statsWrapper) {
        setValue(statsWrapper);
    }

    public void setValue(Stats stat) {
        this.statsWrapper = stat;
        this.subType = EffectSubTypeEnum.EST_STAT;
    }

    public EffectSubTypeWrapper(Summon summonWrapper) {
        setValue(summonWrapper);
    }

    public void setValue(Summon summonWrapper) {
        this.summonWrapper = summonWrapper;
        this.subType = EffectSubTypeEnum.EST_SUMMON;
    }

    public EffectSubTypeWrapper(SummonType summonTypeWrapper) {
        setValue(summonTypeWrapper);
    }

    public void setValue(SummonType summonTypeWrapper) {
        this.summonTypeWrapper = summonTypeWrapper;
        this.subType = EffectSubTypeEnum.EST_SUMMON_SPEC;
    }

    public EffectSubTypeWrapper(EffectMonTimed monTimedWrapper) {
        setValue(monTimedWrapper);
    }

    public void setValue(EffectMonTimed monTimedWrapper) {
        this.monTimedWrapper = monTimedWrapper;
        this.subType = EffectSubTypeEnum.EST_MON_TMD;
    }

    public EffectSubTypeWrapper(EffectNourish nourishWrapper) {
        setValue(nourishWrapper);
    }

    public void setValue(EffectNourish effectNourish) {
        this.nourishWrapper = effectNourish;
        this.subType = EffectSubTypeEnum.EST_NOURISH;
    }

    public EffectSubTypeWrapper(ProjectionEnum projectionWrapper) {
        setValue(projectionWrapper);
    }

    public void setValue(ProjectionEnum projectionWrapper) {
        this.projectionWrapper = projectionWrapper;
        this.subType = EffectSubTypeEnum.EST_PROJ;
    }

    public EffectSubTypeWrapper(TimedEffect timedWrapper) {
        setValue(timedWrapper);
    }

    public void setValue(TimedEffect timedWrapper) {
        this.timedWrapper = timedWrapper;
        this.subType = EffectSubTypeEnum.EST_TMD;
    }

    public EffectSubTypeEnum getSubType() {
        return subType;
    }

    public ProjectionEnum getProjectionWrapper(EffectSubTypeEnum subType) throws Exception {
        if (subType != EffectSubTypeEnum.EST_PROJ) {
            String message = "Invalid subtype, expected EST_PROJ, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return projectionWrapper;
    }

    public TimedEffect getTimedWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_TMD) {
            String message = "Invalid subtype, expected EST_TMD, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return timedWrapper;
    }

    public EffectNourish getNourishWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_NOURISH) {
            String message = "Invalid subtype, expected EST_NOURISH, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return nourishWrapper;
    }

    public EffectMonTimed getMonTimedWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_MON_TMD) {
            String message = "Invalid subtype, expected EST_MON_TMD, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return monTimedWrapper;
    }

    public Summon getSummonWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SUMMON) {
            String message = "Invalid subtype, expected EST_SUMMON, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return summonWrapper;
    }

    public SummonType getSummonTypeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SUMMON_SPEC) {
            String message = "Invalid subtype, expected EST_SUMMON_SPEC, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return summonTypeWrapper;
    }

    public Stats getStatsWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_STAT) {
            String message = "Invalid subtype, expected EST_STAT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return statsWrapper;
    }

    public EffectEnchant getEnchantWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_ENCHANT) {
            String message = "Invalid subtype, expected EST_ENCHANT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return enchantWrapper;
    }

    public PlayerShape getShapeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_SHAPECHANGE) {
            String message = "Invalid subtype, expected EST_SHAPECHANGE, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return shapeWrapper;
    }

    public Earthquake getQuakeWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_EARTHQUAKE) {
            String message = "Invalid subtype, expected EST_EARTHQUAKE, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return quakeWrapper;
    }

    public GlyphType getGlyphType() throws Exception {
        if (subType != EffectSubTypeEnum.EST_GLYPH) {
            String message = "Invalid subtype, expected EST_GLYPH, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return glyphType;
    }

    public TeleportEnum getTeleportWrapper() throws Exception {
        if (subType != EffectSubTypeEnum.EST_TELEPORT) {
            String message = "Invalid subtype, expected EST_TELEPORT, got " + subType.toString();
            Exception ex = new InvalidParameterException(message);
            logger.error(message, ex);
            throw ex;
        }

        return teleportWrapper;
    }

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