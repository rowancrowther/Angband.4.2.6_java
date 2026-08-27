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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.ProjectionEnum;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.EffectEnchant;
import uk.co.jackoftrades.middle.enums.EffectNourish;
import uk.co.jackoftrades.middle.enums.GlyphType;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.monsters.enums.MonTimed;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link EffectSubTypeWrapper}, the port of the untagged union C keeps in an effect's
 * {@code subtype} field.
 *
 * <p>C stores every sub-type as a plain {@code int} and relies on the effect's identity to say how
 * to read it; the port keeps one field per payload and a discriminator saying which is live. That
 * makes the class safe where C was not, and the safety is the thing worth testing: each accessor
 * refuses to answer unless the discriminator names its payload, so an effect that asked for the
 * wrong one gets an exception rather than a plausible-looking number meant for something else.
 *
 * <p>So each payload is tested twice — once that it round-trips through the accessor that belongs to
 * it, and once that a different accessor refuses it.
 *
 * @author Rowan Crowther
 */
class EffectSubTypeWrapperTest {

    /**
     * {@link EffectSubTypeWrapper#copy()} duplicates every field rather than just the live one, so
     * that it needs no changing when a sub-type is added.
     *
     * @throws Exception if the accessor rejects the payload
     */
    @Test
    @DisplayName("a copy carries the discriminator and the payload")
    void copyCarriesEverything() throws Exception {
        EffectSubTypeWrapper original = new EffectSubTypeWrapper(Stats.STAT_STR);
        EffectSubTypeWrapper duplicate = original.copy();

        assertNotSame(original, duplicate);
        assertEquals(EffectSubTypeEnum.EST_STAT, duplicate.getSubType());
        assertEquals(Stats.STAT_STR, duplicate.getStatsWrapper());
    }

    /**
     * Each payload type, stored and read back through its own accessor.
     */
    @Nested
    @DisplayName("payloads round-trip")
    class RoundTrips {

        /**
         * The projection accessor is the odd one out: it takes the expected sub-type as an argument
         * rather than reading the field, so the caller states what it is expecting.
         *
         * @throws Exception if the accessor rejects the payload, which would be the failure
         */
        @Test
        @DisplayName("a projection payload comes back")
        void projection() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(ProjectionEnum.PROJ_ACID);

            assertEquals(EffectSubTypeEnum.EST_PROJ, wrapper.getSubType());
            assertEquals(ProjectionEnum.PROJ_ACID,
                    wrapper.getProjectionWrapper(EffectSubTypeEnum.EST_PROJ));
        }

        /**
         * The timed-effect payload, which is how a potion of speed names what it grants.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("a timed-effect payload comes back")
        void timed() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(TimedEffect.TMD_FAST);

            assertEquals(EffectSubTypeEnum.EST_TMD, wrapper.getSubType());
            assertEquals(TimedEffect.TMD_FAST, wrapper.getTimedWrapper());
        }

        /**
         * The nourishment payload, which distinguishes setting a food value from adding to it.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("a nourishment payload comes back")
        void nourish() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(EffectNourish.EN_INC_BY);

            assertEquals(EffectSubTypeEnum.EST_NOURISH, wrapper.getSubType());
            assertEquals(EffectNourish.EN_INC_BY, wrapper.getNourishWrapper());
        }

        /**
         * The monster-timed payload, the monster's counterpart of the player's timed effects.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("a monster-timed payload comes back")
        void monTimed() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(MonTimed.MON_TMD_SLEEP);

            assertEquals(EffectSubTypeEnum.EST_MON_TMD, wrapper.getSubType());
            assertEquals(MonTimed.MON_TMD_SLEEP, wrapper.getMonTimedWrapper());
        }

        /**
         * The summon-type payload, which names a family of monsters rather than one.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("a summon-type payload comes back")
        void summonType() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(SummonType.SUM_KIN);

            assertEquals(EffectSubTypeEnum.EST_SUMMON_SPEC, wrapper.getSubType());
            assertEquals(SummonType.SUM_KIN, wrapper.getSummonTypeWrapper());
        }

        /**
         * The stat payload, which says which characteristic an effect raises or drains.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("a stat payload comes back")
        void stat() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(Stats.STAT_STR);

            assertEquals(EffectSubTypeEnum.EST_STAT, wrapper.getSubType());
            assertEquals(Stats.STAT_STR, wrapper.getStatsWrapper());
        }

        /**
         * The enchantment payload, which says which of an object's three bonuses is raised.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("an enchantment payload comes back")
        void enchant() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(EffectEnchant.EE_TOBOTH);

            assertEquals(EffectSubTypeEnum.EST_ENCHANT, wrapper.getSubType());
            assertEquals(EffectEnchant.EE_TOBOTH, wrapper.getEnchantWrapper());
        }

        /**
         * The earthquake payload, which says what the quake is centred on.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("an earthquake payload comes back")
        void earthquake() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(Earthquake.QUAKE_NONE);

            assertEquals(EffectSubTypeEnum.EST_EARTHQUAKE, wrapper.getSubType());
            assertEquals(Earthquake.QUAKE_NONE, wrapper.getQuakeWrapper());
        }

        /**
         * The glyph payload, which says which kind of rune is inscribed on the floor.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("a glyph payload comes back")
        void glyph() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(GlyphType.GLYPH_WARDING);

            assertEquals(EffectSubTypeEnum.EST_GLYPH, wrapper.getSubType());
            assertEquals(GlyphType.GLYPH_WARDING, wrapper.getGlyphType());
        }

        /**
         * The shapechange payload, which names the form the player takes. A whole
         * {@link PlayerShape} rather than an enum constant, so the wrapper is holding a reference
         * here where its other payloads hold values.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("a shapechange payload comes back")
        void shape() throws Exception {
            PlayerShape bear = new PlayerShape("bear", 0, 0, 0, Map.of(),
                    new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                    Map.of(), Map.of(), List.of(), 1, List.of());
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(bear);

            assertEquals(EffectSubTypeEnum.EST_SHAPECHANGE, wrapper.getSubType());
            assertSame(bear, wrapper.getShapeWrapper());
        }

        /**
         * The summon payload, which names one kind of summoning. Also a reference rather than a
         * value, and the two reference payloads are the ones a copy shares rather than duplicates.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("a summon payload comes back")
        void summon() throws Exception {
            Summon kin = new Summon("kin", null, false, List.of(), null, null, null, "your kin");
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(kin);

            assertEquals(EffectSubTypeEnum.EST_SUMMON, wrapper.getSubType());
            assertSame(kin, wrapper.getSummonWrapper());
        }
    }

    /**
     * The discriminator is what keeps one payload from being read as another.
     */
    @Nested
    @DisplayName("payload guards")
    class Guards {

        /**
         * Asking a glyph wrapper for its timed effect is a coding error, and is refused rather than
         * answered with the field's default. This is the whole point of the class.
         */
        @Test
        @DisplayName("an accessor refuses a payload that is not live")
        void wrongAccessorThrows() {
            EffectSubTypeWrapper glyph = new EffectSubTypeWrapper(GlyphType.GLYPH_WARDING);

            assertThrows(Exception.class, glyph::getTimedWrapper);
            assertThrows(Exception.class, glyph::getStatsWrapper);
            assertThrows(Exception.class, glyph::getQuakeWrapper);
        }

        /**
         * The projection accessor takes the expectation as an argument, so it refuses when the
         * caller's expectation and the stored payload disagree — even though the payload is present.
         */
        @Test
        @DisplayName("the projection accessor refuses a mismatched expectation")
        void projectionExpectationChecked() {
            EffectSubTypeWrapper projection = new EffectSubTypeWrapper(ProjectionEnum.PROJ_ACID);

            assertThrows(Exception.class,
                    () -> projection.getProjectionWrapper(EffectSubTypeEnum.EST_TMD));
        }
    }

    /**
     * The two teleport factories, which exist because that payload is a boolean and could not be
     * distinguished by an overloaded constructor.
     */
    @Nested
    @DisplayName("teleport factories")
    class Teleport {

        /**
         * {@code teleport} tags {@code EST_TELEPORT} and stores the flag under it.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("teleport tags its own sub-type")
        void teleportTags() throws Exception {
            EffectSubTypeWrapper away = EffectSubTypeWrapper.teleport(true);

            assertEquals(EffectSubTypeEnum.EST_TELEPORT, away.getSubType());
            assertTrue(away.getTeleportMonsterMayCast());
        }

        /**
         * {@code teleportTo} tags the other one, so the two cannot be confused despite carrying the
         * same type of payload.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("teleportTo tags the other sub-type")
        void teleportToTags() throws Exception {
            EffectSubTypeWrapper self = EffectSubTypeWrapper.teleportTo(true);

            assertEquals(EffectSubTypeEnum.EST_TELEPORT_TO, self.getSubType());
            assertTrue(self.getTeleportToMonsterMayCast());
        }

        /**
         * And each refuses the other's accessor, which is what the separate tags buy.
         */
        @Test
        @DisplayName("each teleport kind refuses the other's accessor")
        void teleportKindsAreDistinct() {
            EffectSubTypeWrapper away = EffectSubTypeWrapper.teleport(true);
            EffectSubTypeWrapper self = EffectSubTypeWrapper.teleportTo(true);

            assertThrows(Exception.class, away::getTeleportToMonsterMayCast);
            assertThrows(Exception.class, self::getTeleportMonsterMayCast);
        }
    }

    /**
     * {@code setValue} re-tags an existing wrapper, which is how the parser fills one in after
     * building it empty.
     */
    @Nested
    @DisplayName("setValue")
    class SetValue {

        /**
         * Storing a payload sets the discriminator with it, so the two cannot fall out of step.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("storing a payload sets its discriminator")
        void setValueTags() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(EffectSubTypeEnum.EST_NONE);
            wrapper.setValue(GlyphType.GLYPH_WARDING);

            assertEquals(EffectSubTypeEnum.EST_GLYPH, wrapper.getSubType());
            assertEquals(GlyphType.GLYPH_WARDING, wrapper.getGlyphType());
        }

        /**
         * Storing a second payload re-tags, so the wrapper answers as the new one and refuses the
         * old — the fields both hold values, but only one is live.
         *
         * @throws Exception if the accessor rejects the payload
         */
        @Test
        @DisplayName("storing again re-tags the wrapper")
        void setValueRetags() throws Exception {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(GlyphType.GLYPH_WARDING);
            wrapper.setValue(Earthquake.QUAKE_NONE);

            assertEquals(EffectSubTypeEnum.EST_EARTHQUAKE, wrapper.getSubType());
            assertEquals(Earthquake.QUAKE_NONE, wrapper.getQuakeWrapper());
            assertThrows(Exception.class, wrapper::getGlyphType);
        }
    }

    /**
     * The no-payload constructor, for an effect whose data file gives no sub-type.
     */
    @Nested
    @DisplayName("the empty wrapper")
    class Empty {

        /**
         * A declared {@code EST_NONE} keeps its discriminator, so the wrapper can say that the
         * effect stated it has no sub-type.
         */
        @Test
        @DisplayName("a declared EST_NONE keeps its discriminator")
        void declaredNone() {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(EffectSubTypeEnum.EST_NONE);

            assertEquals(EffectSubTypeEnum.EST_NONE, wrapper.getSubType());
        }

        /**
         * Any other value passed to that constructor is recorded as no discriminator at all, which
         * distinguishes "stated none" from "did not resolve".
         */
        @Test
        @DisplayName("any other value leaves the discriminator unset")
        void unresolvedIsNull() {
            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(EffectSubTypeEnum.EST_TMD);

            assertNull(wrapper.getSubType());
        }
    }
}
