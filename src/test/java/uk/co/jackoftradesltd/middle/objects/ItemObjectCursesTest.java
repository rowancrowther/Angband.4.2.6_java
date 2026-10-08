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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

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
 * two maps directly. The second is that every one of these has to work on an object that has never
 * carried a curse — the counterpart objects the knowledge code writes into are exactly that. Such
 * an object's backing map is already there and empty (the no-argument constructor builds it as a
 * {@link java.util.TreeMap} in curse index order), so the writers are tested against that starting
 * state rather than against a missing map.
 *
 * <p>Class ItemObjectCursesTest coded on 260817, commented in full on 260817, stale null-map
 * wording corrected on 261008.
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
        return curse(name, 0);
    }

    /**
     * The same minimal curse with a chosen index, for the tests that depend on the order the curse
     * map walks in.
     *
     * <p>Function curse coded on 261008.
     *
     * @param name  the curse's name
     * @param index the curse's index, which the curse map orders by
     * @return a curse with every other field empty
     */
    private static Curse curse(String name, int index) {
        return CurseFixture.curse(name, List.of(), 0, null, new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0,
                List.of(), new Flag<>(ObjectFlag.class), "", "", index);
    }

    /**
     * A fresh item straight from the no-argument constructor, whose curse map is empty and has never
     * held a curse. That is the starting state every test here needs — adding a curse first would
     * hide what the writers do on an object that has never been cursed.
     *
     * <p>Function setUp coded on 260817, commented in full on 260817, stale null-map wording
     * corrected on 261008.
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
         * Every writer has to work on an object that has never held a curse, because the objects the
         * knowledge code writes into come from the no-argument constructor and are exactly that.
         * Each is exercised on a fresh object in turn — one of them failing there would be an
         * exception on a path that only fires for uncursed items, which is the common case and so
         * the least likely to be met in casual play.
         *
         * <p>The no-argument constructor builds the map, so this no longer exercises the writers'
         * own guards for a missing one; the method name predates that.
         *
         * <p>Test writersCreateTheMapOnDemand coded on 260817, stale null-map wording corrected on
         * 261008.
         */
        @Test
        @DisplayName("every writer copes with an object that has never held a curse")
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
         * Setting the power of a curse the object does not carry switches it on. C's bare
         * {@code obj->curses[i].power = x} writes into the slot whether or not the curse was active.
         * The bare write touches only the power, so the new entry's timeout is 0, matching the
         * zero-filled slot, and the curse already there is untouched. {@code append_object_curse}
         * does not leave it there: it rolls {@code randcalc(c->obj->time, 0, RANDOMISE)} into the
         * timeout straight after the power write. Only the {@code obj-knowledge.c} write stops at
         * zero, and the port's {@code addCurse} never rolls a timeout.
         *
         * <p>Test setPowerAddsAbsentCurse coded on 260817, zero-timeout claim corrected on 261008.
         */
        @Test
        @DisplayName("setCursePower adds a curse the object does not have")
        void setPowerAddsAbsentCurse() {
            item.addCurse(siren, 3, 7);

            item.setCursePower(teleport, 5);

            assertAll(
                    () -> assertEquals(2, item.getCurses().size()),
                    () -> assertEquals(5, item.getCurses().get(teleport).getPower()),
                    () -> assertEquals(0, item.getCurses().get(teleport).getTimeout()),
                    () -> assertEquals(3, item.getCurses().get(siren).getPower()),
                    () -> assertEquals(7, item.getCurses().get(siren).getTimeout()));
        }

        /**
         * Setting a power of zero, or below, on a curse the object does not carry leaves it absent.
         * In C the slot is already zero, so writing zero changes nothing, and a curse is active only
         * while its power is non-zero. The map holds an entry only for an active curse, so adding a
         * zero-power one would make the object look cursed with something it was never given, and
         * would stop it stacking with an object that simply lacks the curse. The curse already
         * there is untouched.
         */
        @Test
        @DisplayName("setCursePower with power zero or below does not add an absent curse")
        void setPowerZeroDoesNotAddAbsentCurse() {
            item.addCurse(siren, 3, 7);

            item.setCursePower(teleport, 0);
            item.setCursePower(teleport, -4);

            assertAll(
                    () -> assertEquals(1, item.getCurses().size()),
                    () -> assertFalse(item.getCurses().containsKey(teleport)),
                    () -> assertEquals(3, item.getCurses().get(siren).getPower()),
                    () -> assertEquals(7, item.getCurses().get(siren).getTimeout()));
        }

        /**
         * A negative power on a curse the object does carry takes it off, the same as zero does.
         */
        @Test
        @DisplayName("setCursePower with a negative power removes a curse the object has")
        void setPowerNegativeRemoves() {
            item.addCurse(siren, 3, 7);

            item.setCursePower(siren, -1);

            assertTrue(item.getCurses().isEmpty());
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
         * a fresh empty map rather than a null one, which is the same state the no-argument
         * constructor starts it in, so the field never needs to be nulled to match C. A caller cannot
         * tell the two apart anyway: {@link ItemObject#getCurses} reports empty for both.
         *
         * <p>Test clearEmpties coded on 260817, null-field wording corrected on 261008.
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
     * The allocation step {@code copy_curses} takes before it starts writing, ported as
     * {@link ItemObject#initCurses}.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("initCurses")
    class Initializing {

        /**
         * A fresh object's map is already empty, because the no-argument constructor builds it. This
         * checks the allocation does not disturb that: after {@code initCurses} the map is still
         * there and still empty, the Java analogue of C's {@code mem_zalloc} handing back curse_max
         * zeroed slots.
         *
         * <p>Test freshObjectGetsEmptyMap coded on 260817, null-map wording corrected on 261008.
         */
        @Test
        @DisplayName("gives a fresh object an empty map")
        void freshObjectGetsEmptyMap() {
            item.initCurses();

            assertTrue(item.getCurses().isEmpty());
        }

        /**
         * C's allocation only ever runs behind {@code if (!obj->curses)}, so it never has to
         * consider a curse array that already exists. The port's method carries no such guard
         * itself — {@link ObjectUtils#copyCurses} is where that check lives — so called directly on
         * an already-cursed object it does what a bare {@code mem_zalloc} into a live pointer would:
         * the old data is gone, replaced by an empty map rather than leaked or merged.
         */
        @Test
        @DisplayName("discards whatever curses were already there")
        void discardsExistingCurses() {
            item.addCurse(siren, 3, 7);
            item.addCurse(teleport, 1, 0);

            item.initCurses();

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
         * The map walks in ascending curse index, whatever order the curses were added in. It is a
         * {@link java.util.TreeMap} under {@code ItemObject.CURSE_ORDER}, which is C's array order:
         * {@code obj->curses[i]} is walked by index. Three curses go in as 9, 2, 5 and must come out
         * as 2, 5, 9.
         *
         * <p>Two curses sharing an index fall through to their names, so the walk is still fully
         * determined when the index ties: "alpha" goes in after "beta" and comes out before it.
         *
         * <p>Test curseMapWalksInIndexOrder coded on 260817 as {@code noOrderingIsPromised}, when
         * the map was a {@link java.util.HashMap} and checked only the size; rewritten to pin the
         * {@code TreeMap} order and renamed on 261008.
         */
        @Test
        @DisplayName("the curse map walks in ascending curse index, not insertion order")
        void curseMapWalksInIndexOrder() {
            Curse nine = curse("nine", 9);
            Curse two = curse("two", 2);
            Curse five = curse("five", 5);
            item.addCurse(nine, 1, 0);
            item.addCurse(two, 1, 0);
            item.addCurse(five, 1, 0);

            assertEquals(List.of(two, five, nine), List.copyOf(item.getCurses().keySet()));

            Curse beta = curse("beta", 20);
            Curse alpha = curse("alpha", 20);
            item.addCurse(beta, 1, 0);
            item.addCurse(alpha, 1, 0);

            assertEquals(List.of(two, five, nine, alpha, beta), List.copyOf(item.getCurses().keySet()));
        }
    }
}
