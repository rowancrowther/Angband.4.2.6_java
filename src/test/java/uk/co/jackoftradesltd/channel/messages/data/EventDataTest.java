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

package uk.co.jackoftradesltd.channel.messages.data;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.enums.Stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
        assertInstanceOf(GameEventData.class, new EventDataBirthPoints(Map.of(), Map.of(), 20));
        assertInstanceOf(GameEventData.class, new EventDataExplosion(1, 0, new ArrayList<>(), false,
                new ArrayList<>(), new ArrayList<>(), Loc.zero));
        assertInstanceOf(GameEventData.class, new EventDataStat(1, 2));
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
         */
        @Test
        void aTransposedGridIsADifferentGrid() {
            assertNotEquals(new EventDataGrid(3, 17), new EventDataGrid(17, 3));
        }

        @Test
        void gridsAreComparedByValue() {
            assertEquals(new EventDataGrid(3, 17), new EventDataGrid(3, 17));
        }

        /**
         * A grid is a payload in its own right, not only a component of one — C's union carries a
         * bare {@code struct loc} for events that concern a single square.
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
         */
        @Test
        void aSizeIsHeightThenWidth() {
            EventDataSize size = new EventDataSize(11, 33);

            assertEquals(11, size.height(), "first component is the height - C's h");
            assertEquals(33, size.width(), "second component is the width - C's w");
        }

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

        @Test
        void aStringPayloadCarriesItsText() {
            assertEquals("Initializing arrays...", new EventDataString("Initializing arrays...").string());
        }

        @Test
        void stringPayloadsAreComparedByValue() {
            assertEquals(new EventDataString("room"), new EventDataString("room"));
            assertNotEquals(new EventDataString("room"), new EventDataString("vault"));
        }

        @Test
        void aBooleanPayloadCarriesItsFlag() {
            assertTrue(new EventDataBoolean(true).value());
            assertFalse(new EventDataBoolean(false).value());
        }

        /**
         * The no-argument constructor is a convenience for the negative case, so it has to
         * actually produce the negative case — and produce something equal to the explicit form,
         * or callers using the two spellings would build unequal messages meaning the same thing.
         */
        @Test
        void theDefaultBooleanPayloadIsFalse() {
            assertFalse(new EventDataBoolean().value());
            assertEquals(new EventDataBoolean(false), new EventDataBoolean());
            assertNotEquals(new EventDataBoolean(true), new EventDataBoolean());
        }
    }

    /**
     * Tests for {@link EventDataStat}, reused for {@code EVENT_HP} ({@code chp}/{@code mhp}),
     * {@code EVENT_MANA} ({@code csp}/{@code msp}) and {@code EVENT_PLAYERLEVEL} ({@code lev}/
     * {@code max_lev}) — three C globals read directly by {@code prt_hp}, {@code prt_sp} and
     * {@code prt_level} ({@code src/ui-display.c:207,314,332}), no struct behind any of them.
     *
     * @author Rowan Crowther
     */
    @Nested
    class StatPairs {

        /**
         * Current first, other second — asymmetric values, so a swap of the two would be visible.
         */
        @Test
        void aStatPairCarriesCurrentThenOther() {
            EventDataStat stat = new EventDataStat(30, 40);

            assertEquals(30, stat.current(), "first component is current - C's chp/csp/lev");
            assertEquals(40, stat.other(), "second component is other - C's mhp/msp/max_lev");
        }

        @Test
        void aTransposedStatPairIsADifferentPayload() {
            assertNotEquals(new EventDataStat(30, 40), new EventDataStat(40, 30));
        }

        /**
         * {@code prt_hp}/{@code prt_sp} colour full green when current has reached its pair and
         * {@code prt_level} switches "Level"/"LEVEL" the same way ({@code lev >= max_lev},
         * {@code ui-display.c:213}) — the record stores the equal pair verbatim; the colour choice
         * itself is the front end's, not this payload's.
         */
        @Test
        void anEqualPairIsStoredAsGiven() {
            EventDataStat full = new EventDataStat(40, 40);

            assertEquals(40, full.current());
            assertEquals(40, full.other());
        }

        /**
         * {@code current} can be driven below {@code other} — an injured HP/mana pool, or a level
         * drained below its {@code max_lev} — and the record does not clamp or reorder it; that
         * comparison is left to whoever reads the pair.
         */
        @Test
        void currentBelowOtherIsStoredAsGiven() {
            EventDataStat drained = new EventDataStat(12, 40);

            assertEquals(12, drained.current());
            assertEquals(40, drained.other());
        }

        /**
         * {@code current} at zero is a reachable value — a player at 0 HP is dead, not clamped to
         * a minimum by this payload — so it must round-trip like any other value.
         */
        @Test
        void currentAtZeroIsStoredUnchanged() {
            assertEquals(0, new EventDataStat(0, 40).current());
        }

        @Test
        void statPairsAreComparedByValue() {
            assertEquals(new EventDataStat(30, 40), new EventDataStat(30, 40));
        }
    }

    /**
     * Tests for {@link EventDataMessage}.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Messages {

        @Test
        void aMessagePayloadCarriesItsTypeAndText() {
            EventDataMessage message = new EventDataMessage(MessageType.MSG_GENERIC, "You miss the orc.");

            assertEquals(MessageType.MSG_GENERIC, message.type());
            assertEquals("You miss the orc.", message.message());
        }

        /**
         * The type is what the display colours and sounds by, so the same words under two
         * categories are two different messages.
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

    /**
     * Tests for {@link EventDataBirthPoints}, the port of C's {@code birthpoints} struct
     * ({@code game-event.h:133-138}).
     *
     * <p>{@code points} and {@code incPoints} are both {@code Map<Stats, Integer>}, the same
     * argument-order risk the class doc above calls out for two same-typed components in a row,
     * so {@link #swappingPointsAndIncPointsIsADifferentPayload} pins the order against
     * {@code event_signal_birthpoints}'s parameter list ({@code game-event.c:194}), which takes
     * {@code points} before {@code inc_points}.
     *
     * @author Rowan Crowther
     */
    @Nested
    class BirthPoints {

        /**
         * Every component read back by name, with the two maps holding disjoint values so a swap
         * of either would be visible.
         */
        @Test
        void birthPointsCarriesItsMapsAndRemainingInOrder() {
            Map<Stats, Integer> spent = Map.of(Stats.STAT_STR, 0, Stats.STAT_INT, 3,
                    Stats.STAT_WIS, 6, Stats.STAT_DEX, 0, Stats.STAT_CON, 12);
            Map<Stats, Integer> cost = Map.of(Stats.STAT_STR, 1, Stats.STAT_INT, 2,
                    Stats.STAT_WIS, 3, Stats.STAT_DEX, 1, Stats.STAT_CON, 4);

            EventDataBirthPoints birthPoints = new EventDataBirthPoints(spent, cost, 8);

            assertEquals(spent, birthPoints.getPoints(), "first map is points already spent");
            assertEquals(cost, birthPoints.getIncPoints(), "second map is the increment cost, not points spent again");
            assertEquals(8, birthPoints.getRemaining());
        }

        /**
         * The transposition guard proper: the same two maps the other way round must not read
         * back the same values from {@code getPoints()}.
         */
        @Test
        void swappingPointsAndIncPointsIsADifferentPayload() {
            Map<Stats, Integer> spent = Map.of(Stats.STAT_STR, 5);
            Map<Stats, Integer> cost = Map.of(Stats.STAT_STR, 9);

            EventDataBirthPoints birthPoints = new EventDataBirthPoints(spent, cost, 15);

            assertNotEquals(cost, birthPoints.getPoints());
            assertEquals(spent, birthPoints.getPoints());
            assertEquals(cost, birthPoints.getIncPoints());
        }

        /**
         * C's struct fields are bare {@code const int *} pointers into the caller's own arrays —
         * {@code event_signal_birthpoints} never copies. The port's getters must be just as
         * transparent: the very map instance handed to the constructor, not a defensive copy of
         * it.
         */
        @Test
        void gettersReturnTheSameMapInstancesGivenToTheConstructor() {
            Map<Stats, Integer> spent = new HashMap<>();
            Map<Stats, Integer> cost = new HashMap<>();

            EventDataBirthPoints birthPoints = new EventDataBirthPoints(spent, cost, 20);

            assertSame(spent, birthPoints.getPoints());
            assertSame(cost, birthPoints.getIncPoints());
        }

        /**
         * {@code reset_stats} ({@code player-birth.c:713-737}) seeds {@code points_left_local}
         * with {@code MAX_BIRTH_POINTS} (20, {@code player-birth.c:687}) before anything has been
         * spent — the top of the boundary.
         */
        @Test
        void remainingAtTheFullBirthPointBudgetIsStoredUnchanged() {
            EventDataBirthPoints birthPoints = new EventDataBirthPoints(Map.of(), Map.of(), 20);

            assertEquals(20, birthPoints.getRemaining());
        }

        /**
         * {@code buy_stat} ({@code player-birth.c:741-770}) only deducts a cost that is
         * {@code <= *points_left_local}, so it can drive {@code remaining} down to exactly zero
         * but never below it — the bottom of the boundary, and a real reachable value rather than
         * a clamp the payload itself would need to enforce.
         */
        @Test
        void remainingAtZeroIsStoredUnchanged() {
            EventDataBirthPoints birthPoints = new EventDataBirthPoints(Map.of(), Map.of(), 0);

            assertEquals(0, birthPoints.getRemaining());
        }
    }

    /**
     * Tests for {@link EventDataExplosion}, the port of C's {@code explosion} struct
     * ({@code game-event.h:141-150}).
     *
     * <p>{@code projType} and {@code numGrids} are both {@code int} and adjacent in the
     * constructor, the same argument-order risk the class doc above calls out, so
     * {@link #swappingProjTypeAndNumGridsIsADifferentPayload} pins the order against
     * {@code event_signal_blast}'s parameter list ({@code game-event.c:206-213}), which
     * takes {@code proj_type} before {@code num_grids}. The three per-grid lists are
     * different generic types ({@code ArrayList<Integer>}, {@code ArrayList<Boolean>},
     * {@code ArrayList<Loc>}), so a swap between any pair of them would not compile —
     * that risk does not need a runtime guard.
     *
     * @author Rowan Crowther
     */
    @Nested
    class Explosions {

        /**
         * Every component read back by name, with the parallel lists holding values no
         * uniform pattern would survive a shuffle of.
         */
        @Test
        void anExplosionCarriesEveryComponentInOrder() {
            ArrayList<Integer> distances = new ArrayList<>(List.of(0, 1, 1, 2));
            ArrayList<Boolean> seen = new ArrayList<>(List.of(true, false, true, false));
            ArrayList<Loc> grids = new ArrayList<>(List.of(
                    Loc.row(5).col(5), Loc.row(4).col(5), Loc.row(5).col(6), Loc.row(6).col(5)));
            Loc centre = Loc.row(5).col(5);

            EventDataExplosion explosion = new EventDataExplosion(3, 4, distances, true, seen, grids, centre);

            assertEquals(3, explosion.getProjType(), "first int is the projection type");
            assertEquals(4, explosion.getNumGrids(), "second int is the grid count");
            assertEquals(distances, explosion.getDistanceToGrid());
            assertTrue(explosion.isDrawing());
            assertEquals(seen, explosion.getPlayerSeesGrid());
            assertEquals(grids, explosion.getBlastGrid());
            assertEquals(centre, explosion.getCentre());
        }

        /**
         * The transposition guard proper: the same two ints the other way round must not
         * read back the same values.
         */
        @Test
        void swappingProjTypeAndNumGridsIsADifferentPayload() {
            EventDataExplosion explosion = new EventDataExplosion(3, 9, new ArrayList<>(), false,
                    new ArrayList<>(), new ArrayList<>(), Loc.zero);

            assertEquals(3, explosion.getProjType());
            assertNotEquals(9, explosion.getProjType());
            assertEquals(9, explosion.getNumGrids());
        }

        /**
         * C's struct fields for the three per-grid arrays are bare pointers into the
         * caller's own arrays — {@code event_signal_blast} never copies. The port's
         * getters must be just as transparent: the very list instances handed to the
         * constructor, not defensive copies of them.
         */
        @Test
        void gettersReturnTheSameListInstancesGivenToTheConstructor() {
            ArrayList<Integer> distances = new ArrayList<>();
            ArrayList<Boolean> seen = new ArrayList<>();
            ArrayList<Loc> grids = new ArrayList<>();

            EventDataExplosion explosion = new EventDataExplosion(1, 0, distances, false, seen, grids, Loc.zero);

            assertSame(distances, explosion.getDistanceToGrid());
            assertSame(seen, explosion.getPlayerSeesGrid());
            assertSame(grids, explosion.getBlastGrid());
        }

        /**
         * {@code project.c:761-766}: when no grid has been recorded yet, the explosion
         * centre itself is stored first, as {@code blast_grid[0]} at
         * {@code distance_to_grid[0] == 0} — the one-grid case, and the real starting
         * state every wider blast grows from.
         */
        @Test
        void theCentreGridIsStoredAtDistanceZero() {
            ArrayList<Integer> distances = new ArrayList<>(List.of(0));
            ArrayList<Loc> grids = new ArrayList<>(List.of(Loc.row(10).col(10)));

            EventDataExplosion explosion = new EventDataExplosion(1, 1, distances, true,
                    new ArrayList<>(List.of(true)), grids, Loc.row(10).col(10));

            assertEquals(1, explosion.getNumGrids());
            assertEquals(0, explosion.getDistanceToGrid().get(0));
            assertEquals(explosion.getCentre(), explosion.getBlastGrid().get(0));
        }
    }
}