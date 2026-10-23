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
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests the curse arm of {@link Artifact#copy()}.
 *
 * <p>The expected values come from {@code copy_artifact} in {@code obj-randart.c}. It clears the
 * destination's {@code curses} to {@code NULL} after the {@code memcpy}, then, only
 * {@code if (a_src->curses)}, allocates a fresh array and copies the powers into it. So a source
 * with no curse array gives a copy with none, and a source with one gives a copy with its own array
 * holding the same powers, which the source can change without the copy noticing.
 *
 * <p>Class ArtifactCopyCursesTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class ArtifactCopyCursesTest {

    /**
     * Builds an artifact carrying the given curse map, which may be {@code null}.
     *
     * @param curses the curse map, or {@code null}
     * @return the artifact
     */
    private static Artifact artifact(TreeMap<Curse, CurseData> curses) {
        return new Artifact("Test Blade", "It gleams.", TValue.TV_SWORD, "long sword",
                5, 6, 7, 8, "3d5", 120, 4500, new Flag<>(ObjectFlag.class), new HashMap<>(),
                new HashMap<>(), new HashSet<>(), new HashSet<>(), curses,
                40, 11, 12, 13, null, null, new Random(0, 0, 0, 0, false));
    }

    /**
     * A registry-style curse with the given name and index; the curse object is not needed here.
     *
     * @param name  the curse name
     * @param index the registry index
     * @return the curse
     */
    private static Curse curse(String name, int index) {
        return new Curse(name, List.of(), null, List.of(), new Flag<>(ObjectFlag.class), "", index);
    }

    /**
     * C's NULL arm: no curse array on the source means none on the copy. Before this arm was
     * ported the copy threw here.
     */
    @Test
    @DisplayName("a null curse map is copied without a throw and stays null")
    void nullCursesStayNull() throws Exception {
        Artifact copy = assertDoesNotThrow(() -> artifact(null).copy());

        var field = Artifact.class.getDeclaredField("curses");
        field.setAccessible(true);
        assertNull(field.get(copy), "C leaves the copy's curses NULL");
        assertTrue(copy.getCurses().isEmpty(), "and the getter answers empty for it");
    }

    /**
     * An empty map is not C's NULL array; the copy gets an empty map of its own.
     */
    @Test
    @DisplayName("an empty curse map is copied as a separate empty map")
    void emptyCursesStayEmpty() throws Exception {
        TreeMap<Curse, CurseData> curses = new TreeMap<>(Curse.CURSE_ORDER);
        Artifact copy = artifact(curses).copy();

        var field = Artifact.class.getDeclaredField("curses");
        field.setAccessible(true);
        assertNotNull(field.get(copy));
        assertNotSame(curses, field.get(copy));
        assertTrue(copy.getCurses().isEmpty());
    }

    /**
     * The powers are copied, as C's {@code memcpy} of the {@code int} array copies them, and the
     * curse keys keep index order.
     */
    @Test
    @DisplayName("the curse powers are copied, in index order")
    void powersCopied() {
        Curse vuln = curse("vulnerability", 1);
        Curse tele = curse("teleportation", 2);
        TreeMap<Curse, CurseData> curses = new TreeMap<>(Curse.CURSE_ORDER);
        curses.put(tele, new CurseData(40, 0));
        curses.put(vuln, new CurseData(100, 0));

        Artifact copy = artifact(curses).copy();

        assertEquals(List.of(vuln, tele), new ArrayList<>(copy.getCurses().keySet()));
        assertEquals(100, copy.getCurses().get(vuln).getPower());
        assertEquals(40, copy.getCurses().get(tele).getPower());
    }

    /**
     * C gives the copy a freshly allocated array, so a power changed on the source afterwards
     * does not show on the copy.
     */
    @Test
    @DisplayName("the copy's curse data is its own")
    void curseDataIndependent() {
        Curse vuln = curse("vulnerability", 1);
        CurseData data = new CurseData(30, 0);
        TreeMap<Curse, CurseData> curses = new TreeMap<>(Curse.CURSE_ORDER);
        curses.put(vuln, data);
        Artifact original = artifact(curses);

        Artifact copy = original.copy();
        data.setPower(99);
        curses.clear();

        assertNotSame(data, copy.getCurses().get(vuln));
        assertEquals(30, copy.getCurses().get(vuln).getPower());
        assertEquals(1, copy.getCurses().size());
    }

    /**
     * The brands and slays follow C's same {@code if (a_src->...)} pattern; a null set is read
     * back as empty on the copy. Included so the three NULL arms are tested side by side.
     */
    @Test
    @DisplayName("null brand and slay sets copy to empty")
    void nullBrandsAndSlays() {
        Artifact original = artifact(null);
        set(original, "brands", null);
        set(original, "slays", null);

        Artifact copy = original.copy();

        assertTrue(copy.getBrands().isEmpty());
        assertTrue(copy.getSlays().isEmpty());
    }
}
