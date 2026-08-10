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

package uk.co.jackoftrades.channel.messages.data;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.enums.MessageType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link GameEventData} payload shapes — the port of the arms of C's
 * {@code game_event_data} union ({@code src/game-event.h}).
 *
 * <p>These are data carriers with no behaviour, so the interesting failures are not logic errors
 * but <b>argument-order errors</b>. Several of them are two or more same-typed components in a
 * row: a grid is two {@code int}s, a size is two {@code int}s, a bolt has three adjacent
 * {@code boolean}s. Swap any pair and the code compiles perfectly and is wrong — a mirrored map,
 * a transposed room, an invisible bolt — with the symptom appearing a long way from the
 * constructor that caused it.
 *
 * <p>So the tests below deliberately use <b>asymmetric values</b>: never {@code (1, 1)}, never
 * two booleans set the same way. A test built from symmetric values passes under transposition
 * and is worse than no test, because it looks like cover. For the same reason there are no
 * round-trip assertions here: a round trip passes when both directions are wrong the same way.
 * Each ordering is instead pinned against the C original it is ported from.
 *
 * @author Rowan Crowther
 */
class EventDataTest {

    /**
     * Every payload shape is a {@link GameEventData}, which is what lets the bus carry them all
     * through one dispatch signature.
     *
     * @author Rowan Crowther
     */
    @Test
    void everyShapeIsAGameEventData() {
        assertInstanceOf(GameEventData.class, new EventDataGrid(1, 2));
        assertInstanceOf(GameEventData.class, new EventDataSize(1, 2));
        assertInstanceOf(GameEventData.class, new EventDataString("x"));
        assertInstanceOf(GameEventData.class, new EventDataBoolean(true));
        assertInstanceOf(GameEventData.class, new EventDataMessage(MessageType.MSG_GENERIC, "x"));
        assertInstanceOf(GameEventData.class, new EventDataTunnel(1, 2, 3, 4, 5, false));
        assertInstanceOf(GameEventData.class, new EventDataBolt(ProjectionEnum.PROJ_FIRE, true, true, true,
                new EventDataGrid(1, 2), new EventDataGrid(3, 4)));
    }

    /**
     * Tests for {@link EventDataGrid}, the coordinate pair every position-carrying payload uses.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Grids {

        /**
         * Row first, column second. C's {@code struct loc} is {@code {int x, y;}}, so anything
         * translating from the core's own {@code Loc} crosses the order, and this is the assertion
         * that would fail if it stopped doing so.
         *
         * @author Rowan Crowther
         */
        @Test
        void aGridIsRowThenColumn() {
            EventDataGrid grid = new EventDataGrid(3, 17);

            assertEquals(3, grid.row(), "first component is the row - C's y");
            assertEquals(17, grid.col(), "second component is the column - C's x");
        }

        /**
         * The transposition guard proper: the same two numbers the other way round must not be an
         * equal grid.
         *
         * @author Rowan Crowther
         */
        @Test
        void aTransposedGridIsADifferentGrid() {
            assertNotEquals(new EventDataGrid(3, 17), new EventDataGrid(17, 3));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        void gridsAreComparedByValue() {
            assertEquals(new EventDataGrid(3, 17), new EventDataGrid(3, 17));
        }

        /**
         * A grid is a payload in its own right, not only a component of one — C's union carries a
         * bare {@code struct loc} for events that concern a single square.
         *
         * @author Rowan Crowther
         */
        @Test
        void aGridIsItselfAPayload() {
            assertInstanceOf(GameEventData.class, new EventDataGrid(0, 0));
        }
    }

    /**
     * Tests for {@link EventDataSize}.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Sizes {

        /**
         * Height first. C declares {@code struct { int h, w; }} and passes
         * {@code event_signal_size(type, h, w)} — while the natural English phrase is "width and
         * height", which is the wrong way round. That mismatch is the whole reason this test
         * exists.
         *
         * @author Rowan Crowther
         */
        @Test
        void aSizeIsHeightThenWidth() {
            EventDataSize size = new EventDataSize(11, 33);

            assertEquals(11, size.height(), "first component is the height - C's h");
            assertEquals(33, size.width(), "second component is the width - C's w");
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        void aTransposedSizeIsADifferentSize() {
            assertNotEquals(new EventDataSize(11, 33), new EventDataSize(33, 11));
        }
    }

    /**
     * Tests for the single-component payloads.
     *
     * @author Rowan Crowther
     */
    @Nested
    class SimplePayloads {

        /**
         * @author Rowan Crowther
         */
        @Test
        void aStringPayloadCarriesItsText() {
            assertEquals("Initializing arrays...", new EventDataString("Initializing arrays...").string());
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        void stringPayloadsAreComparedByValue() {
            assertEquals(new EventDataString("room"), new EventDataString("room"));
            assertNotEquals(new EventDataString("room"), new EventDataString("vault"));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        void aBooleanPayloadCarriesItsFlag() {
            assertTrue(new EventDataBoolean(true).value());
            assertFalse(new EventDataBoolean(false).value());
        }

        /**
         * The no-argument constructor is a convenience for the negative case, so it has to
         * actually produce the negative case — and produce something equal to the explicit form,
         * or callers using the two spellings would build unequal messages meaning the same thing.
         *
         * @author Rowan Crowther
         */
        @Test
        void theDefaultBooleanPayloadIsFalse() {
            assertFalse(new EventDataBoolean().value());
            assertEquals(new EventDataBoolean(false), new EventDataBoolean());
            assertNotEquals(new EventDataBoolean(true), new EventDataBoolean());
        }
    }

    /**
     * Tests for {@link EventDataMessage}.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Messages {

        /**
         * @author Rowan Crowther
         */
        @Test
        void aMessagePayloadCarriesItsTypeAndText() {
            EventDataMessage message = new EventDataMessage(MessageType.MSG_GENERIC, "You miss the orc.");

            assertEquals(MessageType.MSG_GENERIC, message.type());
            assertEquals("You miss the orc.", message.message());
        }

        /**
         * The type is what the display colours and sounds by, so the same words under two
         * categories are two different messages.
         *
         * @author Rowan Crowther
         */
        @Test
        void theSameTextUnderADifferentTypeIsADifferentMessage() {
            assertNotEquals(new EventDataMessage(MessageType.MSG_GENERIC, "same words"),
                    new EventDataMessage(MessageType.MSG_HIT, "same words"));
        }
    }

    /**
     * Tests for {@link EventDataBolt}, the widest of the payloads and the one most exposed to
     * argument-order mistakes.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Bolts {

        /**
         * Every component read back individually, with the three booleans set to a pattern no
         * permutation of them reproduces (true, false, true would survive a swap of the outer
         * two; true, true, false does not survive any single swap that matters here, so each is
         * asserted by name rather than trusting the shape).
         *
         * @author Rowan Crowther
         */
        @Test
        void aBoltCarriesEveryComponentInOrder() {
            EventDataBolt bolt = new EventDataBolt(ProjectionEnum.PROJ_FIRE, true, false, true,
                    new EventDataGrid(2, 5), new EventDataGrid(7, 11));

            assertEquals(ProjectionEnum.PROJ_FIRE, bolt.projectionType());
            assertTrue(bolt.drawing(), "drawing is the first of the three flags");
            assertFalse(bolt.seen(), "seen is the second of the three flags");
            assertTrue(bolt.beam(), "beam is the third of the three flags");
            assertEquals(new EventDataGrid(2, 5), bolt.origin());
            assertEquals(new EventDataGrid(7, 11), bolt.current());
        }

        /**
         * Origin and current are both grids, so swapping them compiles. C passes
         * {@code (oy, ox, y, x)} — origin before current — and a bolt drawn from the wrong end
         * animates backwards.
         *
         * @author Rowan Crowther
         */
        @Test
        void swappingOriginAndCurrentIsADifferentBolt() {
            EventDataGrid from = new EventDataGrid(2, 5);
            EventDataGrid to = new EventDataGrid(7, 11);

            assertNotEquals(new EventDataBolt(ProjectionEnum.PROJ_FIRE, true, true, false, from, to),
                    new EventDataBolt(ProjectionEnum.PROJ_FIRE, true, true, false, to, from));
        }

        /**
         * A bolt that is not seen must not be equal to one that is, because {@code seen} is what
         * suppresses drawing entirely.
         *
         * @author Rowan Crowther
         */
        @Test
        void theVisibilityFlagIsPartOfABoltsIdentity() {
            EventDataGrid from = new EventDataGrid(2, 5);
            EventDataGrid to = new EventDataGrid(7, 11);

            assertNotEquals(new EventDataBolt(ProjectionEnum.PROJ_FIRE, true, true, false, from, to),
                    new EventDataBolt(ProjectionEnum.PROJ_FIRE, true, false, false, from, to));
        }
    }

    /**
     * Tests for {@link EventDataTunnel}, six components of which five are {@code int}.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Tunnels {

        /**
         * All five counts given distinct values, so any two being swapped shows up. Reading them
         * back by name is the only thing that pins the order of a run of same-typed components.
         *
         * @author Rowan Crowther
         */
        @Test
        void aTunnelCarriesItsCountsInOrder() {
            EventDataTunnel tunnel = new EventDataTunnel(40, 3, 31, 25, 0, false);

            assertEquals(40, tunnel.nStep());
            assertEquals(3, tunnel.nPierce());
            assertEquals(31, tunnel.nDug());
            assertEquals(25, tunnel.dStart());
            assertEquals(0, tunnel.dEnd());
            assertFalse(tunnel.early());
        }

        /**
         * The documented success test: {@code dEnd} of zero means the tunneller arrived. Nothing
         * enforces that reading, so this pins the two cases the Javadoc describes.
         *
         * @author Rowan Crowther
         */
        @Test
        void aTunnelThatStoppedShortIsDistinguishableFromOneThatArrived() {
            EventDataTunnel arrived = new EventDataTunnel(40, 3, 31, 25, 0, false);
            EventDataTunnel gaveUp = new EventDataTunnel(12, 0, 12, 25, 14, true);

            assertEquals(0, arrived.dEnd());
            assertNotEquals(0, gaveUp.dEnd());
            assertTrue(gaveUp.early());
            assertNotEquals(arrived, gaveUp);
        }
    }
}