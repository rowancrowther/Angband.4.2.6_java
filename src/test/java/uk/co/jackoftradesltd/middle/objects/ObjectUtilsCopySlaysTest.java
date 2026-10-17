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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ObjectUtils#copySlays}, the port of C's {@code copy_slays} ({@code obj-slays.c}).
 *
 * <p>C stores a {@code bool} per slot in a fixed global table, ORs source into dest, and then walks
 * every pair of set indices clearing whichever has the lower multiplier when both name the same
 * monsters ({@link Slay#sameMonsterSlain}). The port holds actual {@link Slay} instances, so the
 * dedup is a name-free scan of the item's slays comparing {@link Slay#getMultiplier()} directly.
 * Private, so reached by reflection rather than through its only caller,
 * {@link ObjectUtils#objectPrep}.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsCopySlaysTest {

    /**
     * A slay against evil creatures, C's {@code EVIL_2} (slay.txt:31-34).
     */
    private static final MonsterRaceFlag EVIL = MonsterRaceFlag.RF_EVIL;
    /**
     * A slay against orcs, C's {@code ORC_3} (slay.txt:49-52) — a different race group from
     * {@link #EVIL}, so it never merges with a slay targeting it.
     */
    private static final MonsterRaceFlag ORC = MonsterRaceFlag.RF_ORC;

    /**
     * Runs the method under test.
     *
     * @param dest   the item the merge writes into
     * @param source the kind whose slays are being added
     */
    private static void copySlays(ItemObject dest, ObjectKind source) {
        try {
            Method method = ObjectUtils.class.getDeclaredMethod("copySlays", ItemObject.class, ObjectKind.class);
            method.setAccessible(true);
            method.invoke(null, dest, source);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("copySlays threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("copySlays is no longer reachable", e);
        }
    }

    /**
     * A slay against a given race, distinguishable from another against the same race only by
     * multiplier and name.
     *
     * @param code       the slay's code, e.g. {@code EVIL_2} — its {@code RF_} prefix must name a
     *                   real {@link MonsterRaceFlag}
     * @param name       display name, so two slays against the same race remain distinguishable
     *                   in failure output
     * @param raceFlag   the race this slay targets — what {@link Slay#sameMonsterSlain} compares
     * @param multiplier the standard damage multiplier
     * @return the slay
     */
    private static Slay slay(String code, String name, MonsterRaceFlag raceFlag, int multiplier) {
        return new Slay(code, name, null, "smite", "pierce", raceFlag, multiplier, multiplier * 8, 100);
    }

    /**
     * The ordinary path: slays against races the item's slays does not yet cover.
     */
    @Nested
    @DisplayName("adding slays for new monster groups")
    class Adding {

        /**
         * A slay for a race not yet present is added outright.
         */
        @Test
        @DisplayName("an empty dest gains every slay from source")
        void addsToEmptyDest() {
            ItemObject dest = new ItemObject();
            Slay evil = slay("EVIL_2", "evil creatures", EVIL, 2);

            ObjectKind source = new ObjectKind();
            source.addSlay(evil);
            copySlays(dest, source);

            assertEquals(Set.of(evil), dest.getSlays());
        }

        /**
         * Two slays against different monster groups both survive - {@link Slay#sameMonsterSlain}
         * only merges same-group pairs.
         */
        @Test
        @DisplayName("slays for distinct monster groups coexist")
        void distinctGroupsCoexist() {
            ItemObject dest = new ItemObject();
            dest.addSlay(slay("EVIL_2", "evil creatures", EVIL, 2));
            Slay orcs = slay("ORC_3", "orcs", ORC, 3);

            ObjectKind source = new ObjectKind();
            source.addSlay(orcs);
            copySlays(dest, source);

            assertEquals(2, dest.getSlays().size());
            assertTrue(dest.containsSlay(orcs));
        }
    }

    /**
     * Two slays that kill the same monsters - the dedup path, and what distinguishes it from
     * {@link ObjectUtilsCopyCursesTest}'s unconditional overwrite.
     */
    @Nested
    @DisplayName("merging two slays against the same monster group")
    class SameGroup {

        /**
         * The stronger of the two survives when {@code source}'s is the stronger one - C clears the
         * weaker array slot; the port replaces the weaker set entry with a copy of the stronger.
         */
        @Test
        @DisplayName("a stronger source slay replaces a weaker dest one")
        void strongerSourceReplacesWeakerDest() {
            ItemObject dest = new ItemObject();
            Slay weak = slay("EVIL_2", "evil creatures", EVIL, 2);
            dest.addSlay(weak);
            Slay strong = slay("EVIL_2", "greater evil", EVIL, 5);

            ObjectKind source = new ObjectKind();
            source.addSlay(strong);
            copySlays(dest, source);

            assertEquals(1, dest.getSlays().size(), "same-group slays must merge into one entry");
            Slay survivor = dest.getSlays().iterator().next();
            assertEquals(5, survivor.getMultiplier());
            assertEquals("greater evil", survivor.getName());
        }

        /**
         * A weaker source slay changes nothing - C's tie-break for the reverse direction keeps the
         * stronger array slot regardless of which side (source or dest) it originated from, and so
         * does the port.
         */
        @Test
        @DisplayName("a weaker source slay leaves the stronger dest one exactly as it was")
        void weakerSourceLeavesStrongerDestAlone() {
            ItemObject dest = new ItemObject();
            Slay strong = slay("EVIL_2", "greater evil", EVIL, 5);
            dest.addSlay(strong);
            Slay weak = slay("EVIL_2", "evil creatures", EVIL, 2);

            ObjectKind source = new ObjectKind();
            source.addSlay(weak);
            copySlays(dest, source);

            assertEquals(1, dest.getSlays().size());
            assertSame(strong, dest.getSlays().iterator().next(),
                    "the surviving instance must be dest's own, not a copy of the loser");
        }
    }

    /**
     * A {@code source} that adds nothing.
     */
    @Nested
    @DisplayName("an empty source")
    class EmptySource {

        /**
         * Leaves the item's slays exactly as it was.
         */
        @Test
        @DisplayName("dest is unchanged")
        void leavesDestUnchanged() {
            ItemObject dest = new ItemObject();
            Slay evil = slay("EVIL_2", "evil creatures", EVIL, 2);
            dest.addSlay(evil);

            copySlays(dest, new ObjectKind());

            assertEquals(Set.of(evil), dest.getSlays());
        }
    }
}
