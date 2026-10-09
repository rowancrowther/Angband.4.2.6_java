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

import org.junit.jupiter.api.*;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that {@link PlayerUpkeep} models C's {@code struct player_upkeep} ({@code player.h}) field
 * for field, and the two writes the existing upkeep tests leave open: the bare resting-counter
 * write and the bare health-bar write.
 *
 * <p>The expected field list and the expected starting values are taken from the C struct and from
 * {@code init_player} ({@code player.c}), where {@code mem_zalloc} zeroes the struct and only
 * {@code inven} and {@code quiver} are then allocated. They are not read back from the Java
 * constructor, which is what is being checked.
 *
 * <p>Nothing here pins the static status-cache writes inside {@code setHealthWho} and
 * {@code setRestingCounter}: those are placeholders awaiting replacement by the message-based
 * scheme, and a test that asserted them would only have to be deleted with them.
 *
 * <p>Class PlayerUpkeepStructTest written on 261009.
 *
 * @author Rowan Crowther
 */
class PlayerUpkeepStructTest {

    /**
     * The pack size the constructor sizes its arrays from.
     */
    private static final int PACK_SIZE = 23;

    /**
     * The quiver size, likewise.
     */
    private static final int QUIVER_SIZE = 10;

    /**
     * Whatever was in the constants holder before this class ran.
     */
    private static Object savedConstants;

    /**
     * The instance under test, fresh for each test.
     */
    private PlayerUpkeep upkeep;

    /**
     * Seeds the two carry-capacity figures the constructor needs to size the pack and quiver.
     *
     * @throws Exception if the constants field cannot be reached
     */
    @BeforeAll
    static void seedConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        savedConstants = data.get(null);
        data.set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(PACK_SIZE, QUIVER_SIZE, 40, 5, 16),
                null, null, null, null, null, null, null, null, null, null, null));
    }

    /**
     * Puts the constants holder back.
     *
     * @throws Exception if the constants field cannot be reached
     */
    @AfterAll
    static void restoreConstants() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, savedConstants);
    }

    /**
     * A fresh upkeep for each test.
     */
    @BeforeEach
    void newUpkeep() {
        upkeep = new PlayerUpkeep();
    }

    /**
     * Builds a bare monster. Every collaborator is null, which the {@code Monster} constructor
     * tolerates; only its identity matters here.
     *
     * @return a monster with no race, grid or flags
     */
    private static Monster monster() {
        return new Monster(null, null, null, 0, 0, null, 0, 0, 0, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    /**
     * Reads a field the class does not expose.
     *
     * @param name the field name
     * @return its value on {@link #upkeep}
     * @throws Exception if the field cannot be reached
     */
    private Object read(String name) throws Exception {
        Field field = PlayerUpkeep.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(upkeep);
    }

    /**
     * The shape of the struct, and the state a fresh one starts in.
     */
    @Nested
    @DisplayName("the struct")
    class Struct {

        /**
         * The Java class carries exactly the 32 fields of {@code struct player_upkeep} plus the one
         * Java-only {@code objectPile}. The names on the left are C's, in C's order, mapped to the
         * port's spelling; a field C has and Java lacks, or the reverse, shows up as a difference.
         */
        @Test
        @DisplayName("has every C field, plus only objectPile")
        void fieldSetMatchesC() {
            Set<String> expected = new TreeSet<>(Set.of(
                    "playing", "autosave", "generateLevel", "onlyPartial", "dropping",
                    "energyUse", "newSpells",
                    "healthWho", "monsterRace", "object", "objectKind",
                    "noticeFlags", "updateFlags", "redrawFlags",
                    "command_wrk",
                    "createUpStair", "createDownStair", "lightLevel", "arenaLevel",
                    "restingCounter", "runningCounter", "runningFirstStep",
                    "quiverObjects", "inventoryObjects", "totalWeight",
                    "inventoryCount", "equipmentCount", "quiverCount",
                    "rechargePower", "stepCount", "steps", "pathDestination"));
            assertEquals(32, expected.size(), "the C struct has 32 fields");
            expected.add("objectPile");

            Set<String> actual = new TreeSet<>();
            for (Field field : PlayerUpkeep.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && !field.isSynthetic()) {
                    actual.add(field.getName());
                }
            }

            assertEquals(expected, actual);
        }

        /**
         * Every scalar starts as {@code mem_zalloc} leaves it: false for a boolean, zero for an
         * int. A sweep over the declared fields, so a field added later without a sensible
         * starting value is caught here rather than by whichever test happens to read it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("every boolean starts false and every int starts at zero")
        void scalarsStartZeroed() throws Exception {
            for (Field field : PlayerUpkeep.class.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                if (field.getType() == boolean.class) {
                    assertFalse(field.getBoolean(upkeep), field.getName() + " should start false");
                } else if (field.getType() == int.class) {
                    assertEquals(0, field.getInt(upkeep), field.getName() + " should start at 0");
                }
            }
        }

        /**
         * The three flag sets start empty. In C they are three {@code uint32_t} left at zero.
         */
        @Test
        @DisplayName("the notice, update and redraw sets start empty")
        void flagSetsStartEmpty() {
            assertFalse(upkeep.isNotice());
            assertFalse(upkeep.getUpdate());
            assertTrue(upkeep.getRedrawFlags().isEmpty());
        }

        /**
         * The destination grid is the origin, not null. A zeroed C {@code struct loc} is
         * {@code (0, 0)} and C has no way to express an absent one.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the path destination starts at the origin, not null")
        void pathDestinationIsOrigin() throws Exception {
            assertEquals(Loc.zero, read("pathDestination"));
        }

        /**
         * The step queue starts null, not as an empty list. The pathfinding commands in
         * {@code cmd-cave.c} each begin with {@code assert(!player->upkeep->steps)}, which an empty
         * list would satisfy without meaning the same thing.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the step queue starts null, not empty")
        void stepsStartNull() throws Exception {
            assertNull(read("steps"));
        }

        /**
         * The trackees all start empty: C's {@code mem_zalloc} leaves four null pointers.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("all four trackees start null")
        void trackeesStartNull() throws Exception {
            assertNull(read("healthWho"));
            assertNull(read("monsterRace"));
            assertNull(read("object"));
            assertNull(read("objectKind"));
        }

        /**
         * {@code init_player} allocates the pack as {@code pack_size + 1} slots and the quiver as
         * {@code quiver_size} slots. The extra pack slot is C's and is kept.
         */
        @Test
        @DisplayName("the pack is pack_size + 1 slots and the quiver is quiver_size")
        void gearArraySizes() {
            assertEquals(PACK_SIZE + 1, upkeep.getInventory().length);
            assertEquals(QUIVER_SIZE, upkeep.getQuiver().length);
        }

        /**
         * Two upkeeps do not share their gear arrays. C allocates a fresh pair per
         * {@code mem_zalloc}'d struct.
         */
        @Test
        @DisplayName("two upkeeps do not share arrays")
        void arraysAreNotShared() {
            PlayerUpkeep other = new PlayerUpkeep();

            assertNotSame(upkeep.getInventory(), other.getInventory());
            assertNotSame(upkeep.getQuiver(), other.getQuiver());
        }
    }

    /**
     * The bare resting-counter write: C's {@code upkeep->resting = x}.
     */
    @Nested
    @DisplayName("setRestingCounter")
    class RestingCounter {

        /**
         * A count of turns round-trips. 50 is arbitrary and inside every range.
         */
        @Test
        @DisplayName("a count of turns round-trips")
        void countRoundTrips() {
            upkeep.setRestingCounter(50);

            assertEquals(50, upkeep.getRestingCounter());
        }

        /**
         * Zero is "not resting" and is written like any other value.
         */
        @Test
        @DisplayName("zero is written, not ignored")
        void zeroIsWritten() {
            upkeep.setRestingCounter(50);
            upkeep.setRestingCounter(0);

            assertEquals(0, upkeep.getRestingCounter());
        }

        /**
         * The negative special codes from {@code player-util.h} -
         * {@code REST_ALL_POINTS = -1}, {@code REST_COMPLETE = -2}, {@code REST_SOME_POINTS = -3} -
         * are stored verbatim. Turning a negative number into 0 is
         * {@code player_resting_set_count}'s job for ordinary negatives, not this field write's.
         */
        @Test
        @DisplayName("the negative special rest codes are stored as given")
        void specialCodesStoredVerbatim() {
            for (int code : new int[]{-1, -2, -3}) {
                upkeep.setRestingCounter(code);

                assertEquals(code, upkeep.getRestingCounter());
            }
        }

        /**
         * No clamping. {@code player_resting_set_count} truncates to 9999, but that is a separate
         * function wrapped around the field write; the field write itself is a bare assignment.
         */
        @Test
        @DisplayName("an overlarge value is not clamped here")
        void noClamp() {
            upkeep.setRestingCounter(10000);

            assertEquals(10000, upkeep.getRestingCounter());
        }

        /**
         * Resting does not touch the running counter, or any of the neighbouring booleans.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("leaves the other state alone")
        void leavesOthersAlone() throws Exception {
            upkeep.setRestingCounter(50);

            assertEquals(0, upkeep.getRunning());
            assertFalse(upkeep.isPlaying());
            assertFalse(upkeep.getDropping());
            assertEquals(0, read("stepCount"));
        }
    }

    /**
     * The health-bar trackee: the bare write and C's {@code health_track}.
     */
    @Nested
    @DisplayName("the health-bar trackee")
    class HealthBar {

        /**
         * The bare write sets the trackee and nothing else. C's {@code health_who} assignment
         * raises no flag - it is {@code health_track} that adds {@code PR_HEALTH}.
         */
        @Test
        @DisplayName("setHealthWho sets the monster and raises no redraw")
        void bareWriteRaisesNothing() {
            Monster mon = monster();

            upkeep.setHealthWho(mon);

            assertSame(mon, upkeep.getHealthWho());
            assertTrue(upkeep.healthWho());
            assertTrue(upkeep.getRedrawFlags().isEmpty(), "no redraw was requested");
        }

        /**
         * {@code health_track} does both halves: it sets {@code health_who} and ORs in
         * {@code PR_HEALTH}.
         */
        @Test
        @DisplayName("healthTrack sets the monster and raises PR_HEALTH")
        void healthTrackDoesBoth() {
            Monster mon = monster();

            upkeep.healthTrack(mon);

            assertSame(mon, upkeep.getHealthWho());
            assertTrue(upkeep.healthWho());
            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_HEALTH));
        }

        /**
         * {@code health_track} adds to the redraw set; it does not replace it. A flag already
         * pending stays pending.
         */
        @Test
        @DisplayName("healthTrack leaves other pending redraws alone")
        void healthTrackAdds() {
            upkeep.setRedrawFlagsOn(PlayerRedraw.PR_MANA);

            upkeep.healthTrack(monster());

            Flag<PlayerRedraw> pending = upkeep.getRedrawFlags();
            assertTrue(pending.has(PlayerRedraw.PR_MANA));
            assertTrue(pending.has(PlayerRedraw.PR_HEALTH));
        }

        /**
         * A null monster switches tracking off - the bar clears - and still requests a repaint so
         * the clear is drawn.
         */
        @Test
        @DisplayName("healthTrack(null) stops tracking and still requests a repaint")
        void trackNullClears() {
            upkeep.healthTrack(monster());
            assertTrue(upkeep.healthWho());

            upkeep.healthTrack(null);

            assertNull(upkeep.getHealthWho());
            assertFalse(upkeep.healthWho());
            assertTrue(upkeep.getRedrawFlags().has(PlayerRedraw.PR_HEALTH));
        }

        /**
         * Tracking a second monster replaces the first outright; there is one health bar.
         */
        @Test
        @DisplayName("tracking a second monster replaces the first")
        void secondReplacesFirst() {
            Monster first = monster();
            Monster second = monster();

            upkeep.healthTrack(first);
            upkeep.healthTrack(second);

            assertSame(second, upkeep.getHealthWho());
        }
    }
}
