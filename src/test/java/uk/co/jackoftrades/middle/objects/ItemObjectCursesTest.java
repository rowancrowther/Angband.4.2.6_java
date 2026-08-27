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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the seven methods that change an {@link ItemObject}'s curses — the write side of what C
 * reaches by assigning into {@code obj->curses[i]}.
 *
 * <p><b>These exist because the read side is closed.</b> {@link ItemObject#getCurses} hands back an
 * unmodifiable view, so every change has to come through one of these; that is what stops a caller
 * altering an object's curses behind its back, and it means the set has to be complete enough to
 * express everything C does. C performs four operations on a curse array — put a curse at a power,
 * change a power, take a curse away, and wipe the lot — and each has a method here.
 *
 * <p><b>Two conventions are being pinned rather than merely exercised.</b> The first is that absence
 * <em>is</em> power zero: C cannot delete from an array indexed by curse so it zeroes the power and
 * reads that back as "not cursed", while the port removes the entry. The two only agree as long as
 * nothing stores a curse at power zero, which is what makes {@code cursesAreEqual} able to compare
 * two maps directly. The second is that the backing map starts null and is created on demand, so
 * every one of these has to work on an object that has never carried a curse — the counterpart
 * objects the knowledge code writes into are exactly that.
 *
 * <p>Class ItemObjectCursesTest coded on 260817, commented in full on 260817.
 *
 * @author Rowan Crowther
 */
class ItemObjectCursesTest {

    private ItemObject item;
    private Curse siren;
    private Curse teleport;

    /**
     * A minimal curse definition, distinct from every other by identity.
     *
     * <p>{@link Curse} declares no {@code equals}, so two of these are never equal and each is its
     * own key. The name is carried only so a failure names the curse.
     *
     * @param name the curse's name
     * @return a curse with every other field empty
     */
    private static Curse curse(String name) {
        return new Curse(name, List.of(), 0, null, new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0,
                List.of(), new Flag<>(ObjectFlag.class), "", "");
    }

    /**
     * A fresh item straight from the no-argument constructor, whose curse map is still null. That is
     * the starting state every test here needs — creating the map first would hide the on-demand
     * behaviour that half of them are about.
     */
    @BeforeEach
    void setUp() {
        item = new ItemObject();
        siren = curse("siren");
        teleport = curse("teleportation");
    }

    /**
     * Putting curses on.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("adding")
    class Adding {

        /**
         * The numeric form builds the instance data itself. This is what the knowledge code uses, C
         * copying a power onto a known counterpart with {@code obj->known->curses[i].power =
         * obj->curses[i].power} and leaving the timeout at zero — so the object it stores must be a
         * fresh one rather than the real item's, or the counterpart would share the countdown.
         */
        @Test
        @DisplayName("the power/timeout form builds its own data")
        void numericFormBuildsData() {
            item.addCurse(siren, 3, 7);

            CurseData stored = item.getCurses().get(siren);
            assertAll(
                    () -> assertEquals(3, stored.getPower()),
                    () -> assertEquals(7, stored.getTimeout()));
        }

        /**
         * The other form stores what it is given, by reference. That is deliberate — a caller that
         * has just installed data can keep reading it — and it is the reason the callers holding a
         * template's data have to copy before calling.
         */
        @Test
        @DisplayName("the data form stores the instance it is handed")
        void dataFormSharesInstance() {
            CurseData data = new CurseData(2, 4);
            item.addCurse(siren, data);

            assertSame(data, item.getCurses().get(siren));
        }

        /**
         * Adding a curse the object already carries replaces its data outright, matching the plain
         * assignment C makes into its array. Worth stating because the alternative — merging, or
         * refusing — would both be defensible designs and neither is what happens.
         */
        @Test
        @DisplayName("adding a curse twice replaces its data")
        void addingTwiceReplaces() {
            item.addCurse(siren, 3, 7);
            item.addCurse(siren, 5, 1);

            assertAll(
                    () -> assertEquals(1, item.getCurses().size()),
                    () -> assertEquals(5, item.getCurses().get(siren).getPower()));
        }

        /**
         * The batch form adds rather than replaces, which is what an object picking up an ego's
         * curses on top of its kind's needs. The contrast with {@code clearAndPutCurses} next door is
         * the whole reason both exist.
         */
        @Test
        @DisplayName("addCurses keeps the curses already there")
        void addCursesAccumulates() {
            item.addCurse(siren, 3, 0);
            item.addCurses(Map.of(teleport, new CurseData(1, 0)));

            assertAll(
                    () -> assertEquals(2, item.getCurses().size()),
                    () -> assertTrue(item.getCurses().containsKey(siren)),
                    () -> assertTrue(item.getCurses().containsKey(teleport)));
        }

        /**
         * The replacing form discards what was there, so the object ends up carrying exactly what it
         * was given. The pair of assertions is what separates it from {@code addCurses}: one that the
         * new curse arrived, one that the old one left.
         */
        @Test
        @DisplayName("clearAndPutCurses replaces the whole set")
        void clearAndPutReplaces() {
            item.addCurse(siren, 3, 0);
            item.clearAndPutCurses(Map.of(teleport, new CurseData(1, 0)));

            assertAll(
                    () -> assertEquals(1, item.getCurses().size()),
                    () -> assertFalse(item.getCurses().containsKey(siren)),
                    () -> assertTrue(item.getCurses().containsKey(teleport)));
        }

        /**
         * Every writer has to cope with the null map, because the objects the knowledge code writes
         * into come from the no-argument constructor and have never held a curse. Each is exercised
         * from that state in turn — one of them missing the guard would be a null pointer on a path
         * that only fires for uncursed items, which is the common case and so the least likely to be
         * met in casual play.
         */
        @Test
        @DisplayName("every writer copes with a map that does not exist yet")
        void writersCreateTheMapOnDemand() {
            assertAll(
                    () -> new ItemObject().addCurse(curse("a"), 1, 0),
                    () -> new ItemObject().addCurse(curse("b"), new CurseData(1, 0)),
                    () -> new ItemObject().addCurses(Map.of(curse("c"), new CurseData(1, 0))),
                    () -> new ItemObject().clearAndPutCurses(Map.of(curse("d"), new CurseData(1, 0))),
                    () -> new ItemObject().clearCurses(),
                    () -> new ItemObject().setCursePower(curse("e"), 2),
                    () -> new ItemObject().removeCurse(curse("f")));
        }
    }

    /**
     * Changing and taking away.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("changing and removing")
    class Changing {

        /**
         * The port of C's bare {@code obj->curses[i].power = ...}. The timeout is deliberately left
         * where it was: a curse being weakened has not had its countdown reset, and re-arming it
         * silently would give the player a reprieve C does not.
         */
        @Test
        @DisplayName("setCursePower changes the power and leaves the timeout")
        void setPowerLeavesTimeout() {
            item.addCurse(siren, 3, 7);

            item.setCursePower(siren, 8);

            assertAll(
                    () -> assertEquals(8, item.getCurses().get(siren).getPower()),
                    () -> assertEquals(7, item.getCurses().get(siren).getTimeout()));
        }

        /**
         * Setting the power of a curse the object does not carry does nothing at all — it does not
         * throw, and it does not invent the curse. Inventing it would be the worse of the two: the
         * object would come away cursed with something it was never given, and by a call whose name
         * suggests it only adjusts.
         */
        @Test
        @DisplayName("setCursePower ignores a curse the object does not have")
        void setPowerIgnoresAbsentCurse() {
            item.addCurse(siren, 3, 0);

            item.setCursePower(teleport, 5);

            assertAll(
                    () -> assertEquals(1, item.getCurses().size()),
                    () -> assertFalse(item.getCurses().containsKey(teleport)));
        }

        /**
         * A null curse takes the same silent exit rather than reaching the map with it.
         */
        @Test
        @DisplayName("setCursePower ignores a null curse")
        void setPowerIgnoresNull() {
            item.setCursePower(null, 5);

            assertTrue(item.getCurses().isEmpty());
        }

        /**
         * Removal is how a curse comes off, and the entry goes rather than being zeroed. That is the
         * convention the whole representation rests on: {@code cursesAreEqual} compares two maps
         * directly, so an object left holding a zero-power entry would refuse to stack with one that
         * simply lacks the curse, though C considers the two identical.
         */
        @Test
        @DisplayName("removeCurse takes the entry out rather than zeroing it")
        void removeTakesTheEntryOut() {
            item.addCurse(siren, 3, 0);

            item.removeCurse(siren);

            assertAll(
                    () -> assertTrue(item.getCurses().isEmpty()),
                    () -> assertFalse(item.getCurses().containsKey(siren)));
        }

        /**
         * Removing something that is not there is not an error. C reaches the same place by assigning
         * zero to a slot that already held zero.
         */
        @Test
        @DisplayName("removing an absent curse changes nothing")
        void removingAbsentCurseIsQuiet() {
            item.addCurse(siren, 3, 0);

            item.removeCurse(teleport);

            assertEquals(1, item.getCurses().size());
        }

        /**
         * The port of C freeing the array and nulling the pointer. What the object is left holding is
         * an empty map rather than a null one, which callers cannot tell apart — {@link
         * ItemObject#getCurses} reports empty for both — and that indistinguishability is the reason
         * the null field never needs to be restored.
         */
        @Test
        @DisplayName("clearCurses empties the set")
        void clearEmpties() {
            item.addCurse(siren, 3, 0);
            item.addCurse(teleport, 1, 0);

            item.clearCurses();

            assertTrue(item.getCurses().isEmpty());
        }
    }

    /**
     * What the mutators and the closed accessor promise each other.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the view over the writes")
    class ViewAndWrites {

        /**
         * A change made through a mutator shows through a view taken beforehand, because the view is
         * over the live map. A caller holding the result of {@link ItemObject#getCurses} across a
         * mutation is therefore not looking at a stale snapshot — which matters most for the curse
         * tick, which walks what it read while the knowledge code may be writing.
         */
        @Test
        @DisplayName("a view taken early sees later writes")
        void viewSeesLaterWrites() {
            item.addCurse(siren, 3, 0);
            Map<Curse, CurseData> view = item.getCurses();

            item.addCurse(teleport, 1, 0);

            assertEquals(2, view.size());
        }

        /**
         * Insertion order is not promised. The mutators create a {@link java.util.HashMap}, so
         * anything depending on the order two curses were added in is depending on a hash. Stated
         * here so that a test elsewhere written against an accidental ordering has something to point
         * at — and so that a later switch to a {@link LinkedHashMap} is a deliberate widening of the
         * contract rather than a silent one.
         */
        @Test
        @DisplayName("the curse map promises no ordering")
        void noOrderingIsPromised() {
            item.addCurse(siren, 3, 0);
            item.addCurse(teleport, 1, 0);

            assertEquals(2, item.getCurses().keySet().size());
        }
    }
}
