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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerTimedEffect}, the port of C's {@code struct timed_effect_data}
 * ({@code player-timed.h}).
 *
 * <p>Most of the class is a parsed record with plain accessors, and testing those would only
 * restate the constructor. What is worth pinning is the one field pair the constructor does
 * <em>not</em> take as an argument: the redraw and update flags, which it fetches from the
 * {@link TimedEffect} identity instead.
 *
 * <p>That split mirrors C, where the flags come from the compile-time table in
 * {@code list-player-timed.h} while everything else is parsed from {@code player_timed.txt} — two
 * sources feeding one struct. A definition built for {@code TMD_BLIND} must therefore arrive
 * already carrying blindness's map repaint and view recomputation, with nothing in the data file
 * having said so.
 *
 * <p>The second group covers what the accessors hand back. The chain from the enum constant
 * through this class to a caller copies at no point, so the set a caller receives is the enum
 * constant's own — a singleton for the life of the JVM. Only the {@link
 * uk.co.jackoftradesltd.channel.utils.FlagView} return type stands between that and a caller who
 * mutates it, which makes the identity worth asserting rather than assuming.
 *
 * <p>Two further groups, added on 261008, pin the whole {@code list-player-timed.h} table against
 * the C header (all 53 rows, not a sample) and the defaults C's parser leaves for the fields
 * the data file omits.
 *
 * <p>Class PlayerTimedEffectTest coded on 260818, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class PlayerTimedEffectTest {

    /**
     * Builds a definition for an effect, with only the fields a test needs populated.
     *
     * @param effect the effect the definition is for
     * @param grades its severity bands
     * @return the definition
     */
    private static PlayerTimedEffect definitionFor(TimedEffect effect, TimedGrade... grades) {
        return new PlayerTimedEffect(effect, "test effect", "it ends", "it rises", "it falls",
                null, List.of(), List.of(grades), null, null, true, 7, null, false,
                null, null, null);
    }

    /**
     * Collects a flag set's contents in iteration order.
     *
     * @param flags the set to drain
     * @param <E>   the flag domain
     * @return its members
     */
    private static <E extends Enum<E>> List<E> contentsOf(Iterable<E> flags) {
        List<E> out = new ArrayList<>();
        flags.forEach(out::add);
        return out;
    }

    /**
     * The flags the constructor derives rather than receives.
     */
    @Nested
    class DerivedFlags {

        @Test
        void theCommonCaseTakesTheStatusLineAndTheBonusRecalculation() {
            PlayerTimedEffect definition = definitionFor(TimedEffect.TMD_FAST);

            assertEquals(List.of(PlayerRedraw.PR_STATUS), contentsOf(definition.getFlagRedraw()));
            assertEquals(List.of(PlayerUpdateEnum.PU_BONUS), contentsOf(definition.getFlagUpdate()));
        }

        /**
         * Blindness is the case that proves the flags come from the identity, not from a default.
         *
         * <p>Nothing passed to the constructor mentions the map or the field of view; the only
         * thing distinguishing this definition from the one above is which constant it names.
         */
        @Test
        void blindnessArrivesCarryingTheMapRepaintAndViewRecomputation() {
            PlayerTimedEffect definition = definitionFor(TimedEffect.TMD_BLIND);

            assertEquals(List.of(PlayerRedraw.PR_MAP), contentsOf(definition.getFlagRedraw()));
            assertEquals(List.of(PlayerUpdateEnum.PU_UPDATE_VIEW, PlayerUpdateEnum.PU_MONSTERS),
                    contentsOf(definition.getFlagUpdate()));
        }

        @Test
        void hallucinationArrivesCarryingAllThreeOfItsRepaints() {
            PlayerTimedEffect definition = definitionFor(TimedEffect.TMD_IMAGE);

            assertEquals(List.of(PlayerRedraw.PR_MAP, PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST),
                    contentsOf(definition.getFlagRedraw()));
        }

        @Test
        void theSentinelArrivesCarryingNothing() {
            PlayerTimedEffect definition = definitionFor(TimedEffect.TMD_NONE);

            assertTrue(definition.getFlagRedraw().isEmpty());
            assertTrue(definition.getFlagUpdate().isEmpty());
        }

        @Test
        void twoDefinitionsOfTheSameEffectAgreeOnTheirFlags() {
            PlayerTimedEffect first = definitionFor(TimedEffect.TMD_SINVIS);
            PlayerTimedEffect second = definitionFor(TimedEffect.TMD_SINVIS);

            assertEquals(contentsOf(first.getFlagUpdate()), contentsOf(second.getFlagUpdate()));
        }
    }

    /**
     * The whole {@code list-player-timed.h} table, checked effect by effect.
     *
     * <p>Expected values come from that header, not from {@link TimedEffect}: of its 53 rows, 49
     * are {@code PR_STATUS} / {@code PU_BONUS} and four are exceptions.
     */
    @Nested
    class CTable {

        @Test
        void everyEffectOutsideTheFourExceptionsRaisesStatusAndBonus() {
            Set<TimedEffect> exceptions = EnumSet.of(TimedEffect.TMD_NONE, TimedEffect.TMD_BLIND,
                    TimedEffect.TMD_IMAGE, TimedEffect.TMD_SINVIS, TimedEffect.TMD_SINFRA);
            int checked = 0;
            for (TimedEffect effect : TimedEffect.values()) {
                if (exceptions.contains(effect)) {
                    continue;
                }
                PlayerTimedEffect definition = definitionFor(effect);
                assertEquals(List.of(PlayerRedraw.PR_STATUS), contentsOf(definition.getFlagRedraw()),
                        effect + " redraw");
                assertEquals(List.of(PlayerUpdateEnum.PU_BONUS), contentsOf(definition.getFlagUpdate()),
                        effect + " update");
                checked++;
            }
            assertEquals(49, checked);
        }

        @Test
        void seeInvisibleAndInfravisionAlsoRefreshMonsterVisibility() {
            for (TimedEffect effect : List.of(TimedEffect.TMD_SINVIS, TimedEffect.TMD_SINFRA)) {
                PlayerTimedEffect definition = definitionFor(effect);

                assertEquals(List.of(PlayerRedraw.PR_STATUS), contentsOf(definition.getFlagRedraw()));
                assertEquals(List.of(PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_MONSTERS),
                        contentsOf(definition.getFlagUpdate()));
            }
        }

        @Test
        void hallucinationRaisesOnlyTheBonusRecalculation() {
            assertEquals(List.of(PlayerUpdateEnum.PU_BONUS),
                    contentsOf(definitionFor(TimedEffect.TMD_IMAGE).getFlagUpdate()));
        }
    }

    /**
     * The fields the data file supplies and the constructor stores untouched, including the
     * defaults C's parser leaves for a line the file omits.
     */
    @Nested
    class StoredFields {

        @Test
        void aFullySpecifiedDefinitionRoundTrips() {
            PlayerTimedEffect definition = new PlayerTimedEffect(TimedEffect.TMD_OPP_FIRE, "d",
                    "e", "i", "x", MessageType.MSG_RECOVER, List.of(), List.of(), null, null,
                    false, 0, ObjectFlag.OF_PROT_CONF, true, ElementEnum.ELEM_FIRE, null, null);

            assertEquals(MessageType.MSG_RECOVER, definition.getMsgT());
            assertEquals(ObjectFlag.OF_PROT_CONF, definition.getoFlagDup());
            assertTrue(definition.isoFlagExactlySyn());
            assertEquals(ElementEnum.ELEM_FIRE, definition.getTempResist());
        }

        /**
         * C's cleanup resets {@code flags}, {@code lower_bound}, {@code oflag_dup},
         * {@code oflag_syn} and the three temporary-property indices to these values.
         */
        @Test
        void theDefaultsMatchWhatCLeavesForAnOmittedLine() {
            PlayerTimedEffect definition = new PlayerTimedEffect(TimedEffect.TMD_FAST, null,
                    null, null, null, MessageType.MSG_NONE, List.of(), List.of(), null, null,
                    false, 0, ObjectFlag.OF_NONE, false, ElementEnum.ELEM_NONE, null, null);

            assertFalse(definition.isNonStacking());
            assertEquals(0, definition.getLowerBound());
            assertEquals(ObjectFlag.OF_NONE, definition.getoFlagDup());
            assertFalse(definition.isoFlagExactlySyn());
            assertEquals(ElementEnum.ELEM_NONE, definition.getTempResist());
            assertNull(definition.getTempBrand());
            assertNull(definition.getTempSlay());
            assertNull(definition.getOnBeginEffect());
            assertNull(definition.getOnEndEffect());
        }

        @Test
        void theLowerBoundAcceptsCsUpperLimit() {
            PlayerTimedEffect definition = new PlayerTimedEffect(TimedEffect.TMD_FAST, "", "", "",
                    "", MessageType.MSG_NONE, List.of(), List.of(), null, null, false, 32767,
                    ObjectFlag.OF_NONE, false, ElementEnum.ELEM_NONE, null, null);

            assertEquals(32767, definition.getLowerBound());
        }
    }

    /**
     * What the accessors hand back, and what they share.
     */
    @Nested
    class Accessors {

        /**
         * The set reaches the caller from the enum constant with no copy anywhere in between.
         *
         * <p>Asserting identity rather than equality is the point: it records that nothing along
         * the chain is defending by copying, so the {@code FlagView} return type is the only thing
         * keeping the constant safe.
         */
        @Test
        void theFlagsAreTheEnumConstantsOwnSetRatherThanACopy() {
            PlayerTimedEffect definition = definitionFor(TimedEffect.TMD_STUN);

            assertSame(TimedEffect.TMD_STUN.getRedrawFlags(), definition.getFlagRedraw());
            assertSame(TimedEffect.TMD_STUN.getUpdateFlags(), definition.getFlagUpdate());
        }

        @Test
        void theIdentityIsWhatTheDefinitionWasBuiltFor() {
            assertEquals(TimedEffect.TMD_CUT, definitionFor(TimedEffect.TMD_CUT).getName());
        }

        @Test
        void theParsedFieldsRoundTrip() {
            PlayerTimedEffect definition = definitionFor(TimedEffect.TMD_CUT);

            assertEquals("test effect", definition.getDescription());
            assertEquals("it ends", definition.getOnEnd());
            assertEquals("it rises", definition.getOnIncrease());
            assertEquals("it falls", definition.getOnDecrease());
            assertTrue(definition.isNonStacking());
            assertEquals(7, definition.getLowerBound());
        }

        @Test
        void theGradesComeBackInTheOrderTheyWereGiven() {
            TimedGrade first = new TimedGrade(1, ColourEnum.COLOUR_WHITE, 50, "Stun", "up", "down");
            TimedGrade second = new TimedGrade(2, ColourEnum.COLOUR_RED, 150, "Heavy Stun", "up", "down");

            PlayerTimedEffect definition = definitionFor(TimedEffect.TMD_STUN, first, second);

            assertEquals(List.of(first, second), definition.getGrade());
        }
    }
}
