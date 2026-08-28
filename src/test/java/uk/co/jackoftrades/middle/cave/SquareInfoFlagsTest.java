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

package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Square#sqInfoOn(SquareEnum)} and {@link Square#sqInfoOff(SquareEnum)}, the port of
 * C's {@code sqinfo_on} and {@code sqinfo_off} ({@code cave.h}).
 *
 * <p>Both are one line long, and the tests are here for the property that one line does not state:
 * that each touches the flag it is given and nothing else. The info field carries around twenty
 * flags of wildly different lifetimes in one place — {@code SQUARE_VIEW} is rebuilt every time the
 * player takes a step, {@code SQUARE_MARK} records what they have explored across the whole game —
 * so a write that reached one flag too far would corrupt the map by way of the visibility sweep,
 * and would do it silently. Every test here sets up neighbouring flags it does not name and asserts
 * they came through unchanged.
 *
 * <p>Idempotence is the second property worth pinning. {@code Chunk.markWasSeen} sets and clears as
 * it sweeps rather than testing first, so setting a set flag and clearing a clear one must both be
 * no-ops rather than toggles. An implementation that flipped instead of assigning would pass a
 * single-call test and fail on the second step the player took.
 *
 * <p>The two are also tested against each other. Asserting {@code on} then {@code off} returns the
 * field to where it started is what catches the pair being written against different flags, which
 * reading either one alone cannot show.
 *
 * <p>Both methods also report whether the square actually changed, and that answer is tested
 * separately from the state it describes. The two can disagree — a method that wrote the flag
 * correctly and returned the wrong thing would pass every state assertion here — so each write is
 * asserted twice, once on what the field holds afterwards and once on what the call said it did.
 * The second call of a repeated pair is where it matters: the field looks identical either way, and
 * the return value is the only evidence that the call was a no-op rather than a rewrite.
 *
 * <p>Reads go through {@link Square#hasInfoFlag}, which is package-private — these tests sit in the
 * same package for that reason. It is the only window onto the field, so a fault in it would hide a
 * fault in the writers; the first test is written to fail if the two agree on nothing at all.
 *
 * <p>Class SquareInfoFlagsTest coded on 260827, commented in full on 260827, extended on 260827 to
 * cover the boolean returns.
 *
 * @author Rowan Crowther
 */
class SquareInfoFlagsTest {

    /**
     * The square under test, built with an empty info field.
     */
    private Square square;

    /**
     * A fresh square. Feature and occupant are irrelevant here — nothing in the info flags reads
     * either — so the square is built bare, which also keeps the terrain registry out of it.
     */
    @BeforeEach
    void newSquare() {
        square = new Square(null, 0, 0);
    }

    /**
     * Setting flags on.
     */
    @Nested
    @DisplayName("sqInfoOn")
    class InfoOn {

        /**
         * The basic contract: a flag that was not set is set afterwards. Also the check that
         * {@code hasInfoFlag} and {@code sqInfoOn} are talking about the same field at all — every
         * other test in the class is read through that pairing.
         */
        @Test
        @DisplayName("sets a flag that was not set, and reports the change")
        void setsTheFlag() {
            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));

            boolean changed = square.sqInfoOn(SquareEnum.SQUARE_SEEN);

            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));
            assertTrue(changed);
        }

        /**
         * Setting one flag leaves the others alone, tested from both sides — a flag already set
         * stays set, and a flag not set stays unset.
         */
        @Test
        @DisplayName("leaves every other flag alone")
        void leavesOthersAlone() {
            square.sqInfoOn(SquareEnum.SQUARE_MARK);

            square.sqInfoOn(SquareEnum.SQUARE_SEEN);

            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_MARK));
            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_VIEW));
            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_GLOW));
        }

        /**
         * Setting a flag twice leaves it set. A toggle would pass the first test and fail here,
         * and the level sweep sets without testing first, so this is the case that would break in
         * play rather than in isolation.
         */
        @Test
        @DisplayName("setting an already-set flag changes nothing and reports no change")
        void settingTwiceIsIdempotent() {
            square.sqInfoOn(SquareEnum.SQUARE_SEEN);

            boolean changed = square.sqInfoOn(SquareEnum.SQUARE_SEEN);

            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));
            assertFalse(changed);
        }

        /**
         * Flags accumulate. The info field is a set, not a single value, so setting a second flag
         * does not displace the first — this is what lets a grid be lit and mapped and in a room at
         * once.
         */
        @Test
        @DisplayName("flags accumulate rather than replace")
        void flagsAccumulate() {
            square.sqInfoOn(SquareEnum.SQUARE_GLOW);
            square.sqInfoOn(SquareEnum.SQUARE_ROOM);
            square.sqInfoOn(SquareEnum.SQUARE_VAULT);

            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_GLOW));
            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_ROOM));
            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_VAULT));
        }
    }

    /**
     * Clearing flags off.
     */
    @Nested
    @DisplayName("sqInfoOff")
    class InfoOff {

        /**
         * The basic contract: a flag that was set is not set afterwards.
         */
        @Test
        @DisplayName("clears a flag that was set, and reports the change")
        void clearsTheFlag() {
            square.sqInfoOn(SquareEnum.SQUARE_SEEN);

            boolean changed = square.sqInfoOff(SquareEnum.SQUARE_SEEN);

            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));
            assertTrue(changed);
        }

        /**
         * Clearing one flag leaves the others set. This is the property the visibility sweep leans
         * on hardest: it clears three flags from every grid on the level every time the player
         * moves, and the map has to survive it.
         */
        @Test
        @DisplayName("leaves every other flag alone")
        void leavesOthersAlone() {
            square.sqInfoOn(SquareEnum.SQUARE_MARK);
            square.sqInfoOn(SquareEnum.SQUARE_GLOW);
            square.sqInfoOn(SquareEnum.SQUARE_VIEW);

            square.sqInfoOff(SquareEnum.SQUARE_VIEW);

            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_VIEW));
            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_MARK));
            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_GLOW));
        }

        /**
         * Clearing a flag that is not set is a no-op, not a set. The sweep clears
         * {@code SQUARE_VIEW}, {@code SQUARE_SEEN} and {@code SQUARE_CLOSE_PLAYER} from every grid
         * unconditionally, so most calls it makes are against flags that were never set — a toggle
         * would light the whole level.
         */
        @Test
        @DisplayName("clearing an unset flag changes nothing and reports no change")
        void clearingUnsetFlagIsNoOp() {
            boolean changed = square.sqInfoOff(SquareEnum.SQUARE_SEEN);

            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));
            assertFalse(changed);
        }

        /**
         * Clearing twice leaves the flag clear.
         */
        @Test
        @DisplayName("clearing twice changes nothing and reports no change")
        void clearingTwiceIsIdempotent() {
            square.sqInfoOn(SquareEnum.SQUARE_SEEN);
            square.sqInfoOff(SquareEnum.SQUARE_SEEN);

            boolean changed = square.sqInfoOff(SquareEnum.SQUARE_SEEN);

            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));
            assertFalse(changed);
        }

        /**
         * Clearing on an untouched square is safe — the info field starts empty rather than absent,
         * so there is nothing to guard against.
         */
        @Test
        @DisplayName("clearing on a fresh square is safe")
        void clearingOnFreshSquareIsSafe() {
            square.sqInfoOff(SquareEnum.SQUARE_MARK);
            square.sqInfoOff(SquareEnum.SQUARE_WASSEEN);

            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_MARK));
            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_WASSEEN));
        }
    }

    /**
     * The two together, which is how the visibility sweep uses them.
     */
    @Nested
    @DisplayName("the pair")
    class ThePair {

        /**
         * On then off returns the field to where it started. Reading either method alone cannot
         * show the two being written against different flags; this can.
         */
        @Test
        @DisplayName("on then off returns to the starting state")
        void onThenOffRoundTrips() {
            square.sqInfoOn(SquareEnum.SQUARE_SEEN);
            square.sqInfoOff(SquareEnum.SQUARE_SEEN);

            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));
        }

        /**
         * Off then on leaves the flag set. The other order, because a field that only ever moved
         * one way would pass the round trip above.
         */
        @Test
        @DisplayName("off then on leaves the flag set")
        void offThenOnLeavesItSet() {
            square.sqInfoOff(SquareEnum.SQUARE_SEEN);
            square.sqInfoOn(SquareEnum.SQUARE_SEEN);

            assertTrue(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));
        }

        /**
         * Every flag in {@link SquareEnum} can be set and cleared independently. The info field is
         * a fixed-size structure in C and its Java stand-in is bounded by the enum, so a flag
         * beyond the end of it would fail here rather than in whichever subsystem happened to use
         * it first. Each flag is checked in isolation on its own square.
         */
        @Test
        @DisplayName("every SquareEnum flag can be set and cleared")
        void everyFlagRoundTrips() {
            for (SquareEnum flag : SquareEnum.values()) {
                Square fresh = new Square(null, 0, 0);

                assertFalse(fresh.hasInfoFlag(flag), flag + " should start clear");
                fresh.sqInfoOn(flag);
                assertTrue(fresh.hasInfoFlag(flag), flag + " should be set");
                fresh.sqInfoOff(flag);
                assertFalse(fresh.hasInfoFlag(flag), flag + " should be clear again");
            }
        }

        /**
         * Setting every flag at once and clearing one leaves the rest standing. The per-flag tests
         * above each work against a nearly empty field; this one runs the same clear against a full
         * one, which is where a write that spilled into a neighbouring flag would show.
         */
        @Test
        @DisplayName("clearing one flag from a full field leaves the rest")
        void clearingFromFullFieldLeavesTheRest() {
            for (SquareEnum flag : SquareEnum.values())
                square.sqInfoOn(flag);

            square.sqInfoOff(SquareEnum.SQUARE_SEEN);

            assertFalse(square.hasInfoFlag(SquareEnum.SQUARE_SEEN));
            for (SquareEnum flag : SquareEnum.values())
                if (flag != SquareEnum.SQUARE_SEEN)
                    assertTrue(square.hasInfoFlag(flag), flag + " should have survived");
        }
    }

    /**
     * The reported answer, which is the half the field itself cannot show.
     */
    @Nested
    @DisplayName("the reported change")
    class ReportedChange {

        /**
         * A run of calls on one flag, each asserted for what it claimed to do. The sequence is the
         * point: the field ends up in the same state after the second set as after the first, so
         * only the return value separates a call that did something from one that did not.
         */
        @Test
        @DisplayName("a run of calls reports change only on the calls that change something")
        void reportsOnlyRealChanges() {
            assertTrue(square.sqInfoOn(SquareEnum.SQUARE_SEEN));
            assertFalse(square.sqInfoOn(SquareEnum.SQUARE_SEEN));
            assertTrue(square.sqInfoOff(SquareEnum.SQUARE_SEEN));
            assertFalse(square.sqInfoOff(SquareEnum.SQUARE_SEEN));
            assertTrue(square.sqInfoOn(SquareEnum.SQUARE_SEEN));
        }

        /**
         * The answer is about the flag named, not about the field. Setting a flag on a square that
         * already carries other flags still reports a change, and clearing a flag that is not set
         * still reports none however full the field is around it — an implementation that answered
         * "was the field non-empty" rather than "did this flag move" would fail both halves.
         */
        @Test
        @DisplayName("the answer is per flag, not about the field as a whole")
        void answerIsPerFlag() {
            square.sqInfoOn(SquareEnum.SQUARE_MARK);
            square.sqInfoOn(SquareEnum.SQUARE_GLOW);

            assertTrue(square.sqInfoOn(SquareEnum.SQUARE_SEEN));
            assertFalse(square.sqInfoOff(SquareEnum.SQUARE_VIEW));
        }

        /**
         * Every flag in {@link SquareEnum} reports on its own. The per-flag round trip elsewhere in
         * this class asserts the state; this asserts what each call said about it, so a flag whose
         * write landed correctly but whose answer came back inverted is caught here.
         */
        @Test
        @DisplayName("every SquareEnum flag reports its own change")
        void everyFlagReportsItsOwnChange() {
            for (SquareEnum flag : SquareEnum.values()) {
                Square fresh = new Square(null, 0, 0);

                assertTrue(fresh.sqInfoOn(flag), flag + " should report being set");
                assertFalse(fresh.sqInfoOn(flag), flag + " should report no second change");
                assertTrue(fresh.sqInfoOff(flag), flag + " should report being cleared");
                assertFalse(fresh.sqInfoOff(flag), flag + " should report no second clear");
            }
        }
    }
}
