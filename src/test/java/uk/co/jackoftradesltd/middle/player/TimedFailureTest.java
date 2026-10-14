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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType.*;

/**
 * Checks {@link TimedFailure} against C's {@code struct timed_failure} ({@code player-timed.h}) and
 * the {@code fail:} lines of {@code player_timed.txt} that {@code parse_player_timed_fail} reads.
 *
 * <p>The expected codes are C's {@code TMD_FAIL_FLAG_*} enum (1 to 5). The expected pairs are the
 * sixteen {@code fail:} lines in C's {@code lib/gamedata/player_timed.txt}. The class pins that each
 * constructor accepts only its own code, that each accessor answers only for its own category, and
 * that nothing a constructor does can leave a half-built entry.
 *
 * <p>Class TimedFailureTest coded on 261014, commented in full on 261014.
 */
class TimedFailureTest {

    // The five categories, one constructor call each, for use by the cross-checks below.
    private static final List<Supplier<TimedFailure>> ONE_OF_EACH = List.of(
            () -> new TimedFailure(ObjectFlag.OF_FREE_ACT, TYPE_OBJECT_FLAG),
            () -> new TimedFailure(ElementEnum.ELEM_POIS, TYPE_RESIST),
            () -> new TimedFailure(ElementEnum.ELEM_FIRE, TYPE_VULN),
            () -> new TimedFailure(PlayerFlag.PF_ROCK, TYPE_PLAYER_FLAG),
            () -> new TimedFailure(TimedEffect.TMD_OPP_POIS, TYPE_TIMED_EFFECT));

    // ---- codes ------------------------------------------------------------------------------

    /**
     * C's enum starts at {@code TMD_FAIL_FLAG_OBJECT = 1}; the Java constants must carry the same
     * numbers and {@code fromValue} must map them back, because the data file writes these numbers.
     */
    @Test
    void theCodesAreCsFiveFlagValues() {
        assertEquals(TYPE_OBJECT_FLAG, TimedEffectReasonType.fromValue(1));
        assertEquals(TYPE_RESIST, TimedEffectReasonType.fromValue(2));
        assertEquals(TYPE_VULN, TimedEffectReasonType.fromValue(3));
        assertEquals(TYPE_PLAYER_FLAG, TimedEffectReasonType.fromValue(4));
        assertEquals(TYPE_TIMED_EFFECT, TimedEffectReasonType.fromValue(5));
    }

    /** C's parser rejects every other code; 0 is the Java-only {@code TYPE_NONE} placeholder. */
    @Test
    void codesOutsideOneToFiveAreNotFailureCategories() {
        assertEquals(TYPE_NONE, TimedEffectReasonType.fromValue(0));
        assertNull(TimedEffectReasonType.fromValue(6));
        assertNull(TimedEffectReasonType.fromValue(-1));
    }

    // ---- constructors accept their own code -------------------------------------------------

    @Test
    void anObjectFlagEntryHoldsTheFlagAndItsCode() {
        TimedFailure f = new TimedFailure(ObjectFlag.OF_PROT_BLIND, TYPE_OBJECT_FLAG);
        assertEquals(TYPE_OBJECT_FLAG, f.getCode());
        assertEquals(ObjectFlag.OF_PROT_BLIND, f.getObjFlagCode());
    }

    @Test
    void aResistEntryHoldsTheElementAndItsCode() {
        TimedFailure f = new TimedFailure(ElementEnum.ELEM_CHAOS, TYPE_RESIST);
        assertEquals(TYPE_RESIST, f.getCode());
        assertEquals(ElementEnum.ELEM_CHAOS, f.getElementCode());
    }

    @Test
    void aVulnerabilityEntryHoldsTheElementAndItsCode() {
        TimedFailure f = new TimedFailure(ElementEnum.ELEM_ACID, TYPE_VULN);
        assertEquals(TYPE_VULN, f.getCode());
        assertEquals(ElementEnum.ELEM_ACID, f.getElementCode());
    }

    @Test
    void aPlayerFlagEntryHoldsTheFlagAndItsCode() {
        TimedFailure f = new TimedFailure(PlayerFlag.PF_ROCK, TYPE_PLAYER_FLAG);
        assertEquals(TYPE_PLAYER_FLAG, f.getCode());
        assertEquals(PlayerFlag.PF_ROCK, f.getPlayerFlagCode());
    }

    @Test
    void aTimedEffectEntryHoldsTheEffectAndItsCode() {
        TimedFailure f = new TimedFailure(TimedEffect.TMD_OPP_POIS, TYPE_TIMED_EFFECT);
        assertEquals(TYPE_TIMED_EFFECT, f.getCode());
        assertEquals(TimedEffect.TMD_OPP_POIS, f.getEffectCode());
    }

    // ---- constructors reject every other code -----------------------------------------------

    private static final List<TimedEffectReasonType> EVERY_CODE = List.of(
            TYPE_NONE, TYPE_OBJECT_FLAG, TYPE_RESIST, TYPE_VULN, TYPE_PLAYER_FLAG, TYPE_TIMED_EFFECT);

    @Test
    void theObjectFlagConstructorRejectsEveryCodeButItsOwn() {
        for (TimedEffectReasonType code : EVERY_CODE) {
            if (code == TYPE_OBJECT_FLAG) continue;
            assertThrows(InvalidParameterException.class,
                    () -> new TimedFailure(ObjectFlag.OF_FREE_ACT, code), "code " + code);
        }
        assertThrows(InvalidParameterException.class, () -> new TimedFailure(ObjectFlag.OF_FREE_ACT, null));
    }

    @Test
    void theElementConstructorAcceptsResistAndVulnAndNothingElse() {
        for (TimedEffectReasonType code : EVERY_CODE) {
            if (code == TYPE_RESIST || code == TYPE_VULN) {
                assertEquals(code, new TimedFailure(ElementEnum.ELEM_COLD, code).getCode());
            } else {
                assertThrows(InvalidParameterException.class,
                        () -> new TimedFailure(ElementEnum.ELEM_COLD, code), "code " + code);
            }
        }
        assertThrows(InvalidParameterException.class, () -> new TimedFailure(ElementEnum.ELEM_COLD, null));
    }

    @Test
    void thePlayerFlagConstructorRejectsEveryCodeButItsOwn() {
        for (TimedEffectReasonType code : EVERY_CODE) {
            if (code == TYPE_PLAYER_FLAG) continue;
            assertThrows(InvalidParameterException.class,
                    () -> new TimedFailure(PlayerFlag.PF_ROCK, code), "code " + code);
        }
        assertThrows(InvalidParameterException.class, () -> new TimedFailure(PlayerFlag.PF_ROCK, null));
    }

    @Test
    void theTimedEffectConstructorRejectsEveryCodeButItsOwn() {
        for (TimedEffectReasonType code : EVERY_CODE) {
            if (code == TYPE_TIMED_EFFECT) continue;
            assertThrows(InvalidParameterException.class,
                    () -> new TimedFailure(TimedEffect.TMD_OPP_POIS, code), "code " + code);
        }
        assertThrows(InvalidParameterException.class, () -> new TimedFailure(TimedEffect.TMD_OPP_POIS, null));
    }

    /** The rejection names the category it wanted, so a bad call site can be found from the log. */
    @Test
    void aRejectedConstructionNamesTheCategoryItExpected() {
        InvalidParameterException e = assertThrows(InvalidParameterException.class,
                () -> new TimedFailure(ObjectFlag.OF_FREE_ACT, TYPE_VULN));
        assertTrue(e.getMessage().contains("TYPE_OBJECT_FLAG"), e.getMessage());
        assertTrue(e.getMessage().contains("TYPE_VULN"), e.getMessage());
    }

    // ---- accessors answer only for their own category ---------------------------------------

    /**
     * Where C would read {@code idx} as the wrong kind of value without complaint, each accessor must
     * throw for every entry that is not its own category: 4 accessors against 5 entries each.
     */
    @Test
    void everyAccessorThrowsForEveryEntryOfAnotherCategory() {
        TimedFailure obj = ONE_OF_EACH.get(0).get();
        TimedFailure res = ONE_OF_EACH.get(1).get();
        TimedFailure vul = ONE_OF_EACH.get(2).get();
        TimedFailure plr = ONE_OF_EACH.get(3).get();
        TimedFailure tmd = ONE_OF_EACH.get(4).get();

        for (TimedFailure f : List.of(res, vul, plr, tmd))
            assertThrows(IllegalArgumentException.class, f::getObjFlagCode);
        for (TimedFailure f : List.of(obj, plr, tmd))
            assertThrows(IllegalArgumentException.class, f::getElementCode);
        for (TimedFailure f : List.of(obj, res, vul, tmd))
            assertThrows(IllegalArgumentException.class, f::getPlayerFlagCode);
        for (TimedFailure f : List.of(obj, res, vul, plr))
            assertThrows(IllegalArgumentException.class, f::getEffectCode);
    }

    /** Resist and vulnerability share the one element payload, so both can read it. */
    @Test
    void bothElementCategoriesCanReadTheElement() {
        assertDoesNotThrow(() -> ONE_OF_EACH.get(1).get().getElementCode());
        assertDoesNotThrow(() -> ONE_OF_EACH.get(2).get().getElementCode());
    }

    /** The message names the category the accessor wanted, for each of the four. */
    @Test
    void anAccessorRefusalNamesTheCategoryItExpected() {
        String obj = assertThrows(IllegalArgumentException.class,
                () -> ONE_OF_EACH.get(4).get().getObjFlagCode()).getMessage();
        assertTrue(obj.contains("TYPE_OBJECT_FLAG"), obj);

        String elem = assertThrows(IllegalArgumentException.class,
                () -> ONE_OF_EACH.get(0).get().getElementCode()).getMessage();
        assertTrue(elem.contains("TYPE_RESIST") && elem.contains("TYPE_VULN"), elem);

        String plr = assertThrows(IllegalArgumentException.class,
                () -> ONE_OF_EACH.get(0).get().getPlayerFlagCode()).getMessage();
        assertTrue(plr.contains("TYPE_PLAYER_FLAG"), plr);

        String tmd = assertThrows(IllegalArgumentException.class,
                () -> ONE_OF_EACH.get(0).get().getEffectCode()).getMessage();
        assertTrue(tmd.contains("TYPE_TIMED_EFFECT"), tmd);
    }

    /** No entry can be built with a missing or placeholder code, so {@code getCode} is always a real category. */
    @Test
    void everyBuiltEntryHasARealCategory() {
        for (Supplier<TimedFailure> s : ONE_OF_EACH) {
            TimedEffectReasonType code = s.get().getCode();
            assertNotNull(code);
            assertNotEquals(TYPE_NONE, code);
        }
    }

    // ---- the real data ----------------------------------------------------------------------

    /**
     * The sixteen {@code fail:} lines of C's {@code player_timed.txt}, built the way
     * {@code PlayerTimedAssembler} builds them from the line's number: each must construct and give
     * back its own payload and no other.
     */
    @Test
    void everyFailLineInCsPlayerTimedDataBuildsAndReadsBack() {
        List<TimedFailure> built = new ArrayList<>();

        // fail:1: SLOW, BLIND, PARALYZED, CONFUSED, AFRAID, STUN
        ObjectFlag[] objs = {ObjectFlag.OF_FREE_ACT, ObjectFlag.OF_PROT_BLIND, ObjectFlag.OF_FREE_ACT,
                ObjectFlag.OF_PROT_CONF, ObjectFlag.OF_PROT_FEAR, ObjectFlag.OF_PROT_STUN};
        for (ObjectFlag o : objs) {
            TimedFailure f = new TimedFailure(o, TYPE_OBJECT_FLAG);
            assertEquals(o, f.getObjFlagCode());
            built.add(f);
        }

        // fail:2: IMAGE (CHAOS), POISONED (POIS), SCRAMBLE (NEXUS)
        for (ElementEnum e : new ElementEnum[]{ElementEnum.ELEM_CHAOS, ElementEnum.ELEM_POIS, ElementEnum.ELEM_NEXUS}) {
            TimedFailure f = new TimedFailure(e, TYPE_RESIST);
            assertEquals(e, f.getElementCode());
            built.add(f);
        }

        // fail:3: OPP_ACID, OPP_ELEC, OPP_FIRE, OPP_COLD, OPP_POIS
        for (ElementEnum e : new ElementEnum[]{ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC,
                ElementEnum.ELEM_FIRE, ElementEnum.ELEM_COLD, ElementEnum.ELEM_POIS}) {
            TimedFailure f = new TimedFailure(e, TYPE_VULN);
            assertEquals(e, f.getElementCode());
            built.add(f);
        }

        // fail:4: CUT (ROCK)
        TimedFailure rock = new TimedFailure(PlayerFlag.PF_ROCK, TYPE_PLAYER_FLAG);
        assertEquals(PlayerFlag.PF_ROCK, rock.getPlayerFlagCode());
        built.add(rock);

        // fail:5: POISONED (OPP_POIS)
        TimedFailure opp = new TimedFailure(TimedEffect.TMD_OPP_POIS, TYPE_TIMED_EFFECT);
        assertEquals(TimedEffect.TMD_OPP_POIS, opp.getEffectCode());
        built.add(opp);

        assertEquals(6 + 3 + 5 + 1 + 1, built.size());
    }
}
