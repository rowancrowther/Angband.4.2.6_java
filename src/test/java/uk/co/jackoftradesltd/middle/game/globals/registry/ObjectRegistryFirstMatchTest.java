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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplements {@code ObjectRegistryTest} and {@code ObjectRegistryCReferenceTest} with the cases
 * the 261107 port check found uncovered, each read off the C source rather than the port:
 *
 * <ul>
 *   <li>the {@code kidx >= ordinary_kind_max} test C's artifact parsers make
 *       ({@code obj-init.c}, {@code finish_parse_object} sets the ceiling);</li>
 *   <li>the first-match rule of the scans in {@code lookup_sval}, {@code lookup_curse} and
 *       {@code findact}, which each stop at the first hit and cover every real entry, first and
 *       last included;</li>
 *   <li>{@code lookup_sval}'s {@code strtoul}, which skips leading white space, and its handling
 *       of a reference with no digits.</li>
 * </ul>
 *
 * <p>The registry is global static state, so each test saves the fields it writes and restores
 * them afterwards.
 *
 * <p>Class ObjectRegistryFirstMatchTest commented in full on 261107.
 *
 * @author Rowan Crowther
 */
class ObjectRegistryFirstMatchTest {

    private static final List<String> TOUCHED = List.of(
            "objectKinds", "kindsByTvalSval", "objectBases", "objectBaseKindMax",
            "curses", "activations", "curseMax", "randartActivationsMax");

    private final Map<String, Object> saved = new HashMap<>();

    private ObjectBase sword;

    private static Field field(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static void set(String name, Object value) throws Exception {
        field(name).set(null, value);
    }

    private static ObjectBase base(TValue tval, String name) {
        return new ObjectBase(tval, name, ColourEnum.COLOUR_WHITE,
                new Flag<>(ObjectKindFlag.class), new HashMap<>(), -1, -1, new Flag<>(ObjectFlag.class));
    }

    private static ObjectKind kind(String name, ObjectBase base, String svalName) throws Exception {
        ObjectKind k = new ObjectKind();
        for (Map.Entry<String, Object> entry : Map.of(
                        "name", name, "base", base, "tValue", base.gettVal(), "sValueName", svalName)
                .entrySet()) {
            Field f = ObjectKind.class.getDeclaredField(entry.getKey());
            f.setAccessible(true);
            f.set(k, entry.getValue());
        }
        return k;
    }

    private static Curse curse(String name) {
        return CurseFixture.curse(name, null, 0, null, null, null, null, 0, 0, 0, null, null, null, null, 0);
    }

    private static Activation activation(String name) {
        return new Activation(name, 0, false, 0, 0, null, null, null);
    }

    @BeforeEach
    void isolate() throws Exception {
        for (String name : TOUCHED) {
            saved.put(name, field(name).get(null));
        }
        set("objectKinds", new ArrayList<ObjectKind>());
        set("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        sword = base(TValue.TV_SWORD, "sword");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(sword)));
    }

    @AfterEach
    void restore() throws Exception {
        for (String name : TOUCHED) {
            set(name, saved.get(name));
        }
    }

    // ---- ordinary_kind_max ------------------------------------------------

    /**
     * C marks a kind special when {@code kidx >= ordinary_kind_max}. With the ceiling recorded
     * after the file's kinds, every ordinary kind sits below it and the first synthesized kind sits
     * exactly on it, so the same comparison singles out synthesized kinds here.
     */
    @Test
    void theCeilingSeparatesOrdinaryFromSynthesizedKindsByIndex() throws Exception {
        ObjectKind dagger = kind("Dagger", sword, "Dagger");
        ObjectKind rapier = kind("Rapier", sword, "Rapier");
        ObjectRegistry.addObjectKind(dagger);
        ObjectRegistry.addObjectKind(rapier);
        ObjectRegistry.updateObjectBaseKindMax();

        ObjectKind special = kind("& Blade~", sword, "Blade");
        ObjectRegistry.addObjectKind(special);

        assertTrue(dagger.getKindIndex() < ObjectRegistry.getObjectBaseKindMax());
        assertTrue(rapier.getKindIndex() < ObjectRegistry.getObjectBaseKindMax());
        assertEquals(ObjectRegistry.getObjectBaseKindMax(), special.getKindIndex(),
                "the first synthesized kind lands on the ceiling");
        assertEquals(3, special.getsVal(), "and takes the next sval of its base, as write_dummy_object_record does");
    }

    // ---- lookup_sval: the number leg ----------------------------------------

    /**
     * {@code strtoul} skips leading white space, so a reference with blanks or a tab before the
     * number still names the sval.
     */
    @Test
    void aNumericReferenceMayStartWithWhiteSpace() throws Exception {
        ObjectKind dagger = kind("Dagger", sword, "Dagger");
        ObjectRegistry.addObjectKind(dagger);

        assertSame(dagger, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, " \t1"));
        assertSame(dagger, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "+1"));
    }

    /**
     * A reference of nothing but blanks has no digits, so {@code strtoul} leaves {@code pe} at the
     * start and C falls to the name scan, where no kind is named with blanks: "none".
     */
    @Test
    void aBlankOnlyReferenceNamesNoKind() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", sword, "Dagger"));

        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "   "));
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "\t"));
    }

    // ---- lookup_sval: the name leg -----------------------------------------

    /**
     * The scan returns the sval of the first kind whose name matches; a later kind with the same
     * name under the same tval is never reached.
     */
    @Test
    void aRepeatedSvalNameResolvesToTheEarlierKind() throws Exception {
        ObjectKind first = kind("Dagger", sword, "Dagger");
        ObjectKind second = kind("Dagger", sword, "Dagger");
        ObjectRegistry.addObjectKind(first);
        ObjectRegistry.addObjectKind(second);

        assertSame(first, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "dagger"));
    }

    /**
     * The same name under another tval is a different kind: the scan skips kinds whose tval
     * differs.
     */
    @Test
    void theSameSvalNameUnderAnotherTvalIsNotConfused() throws Exception {
        ObjectBase polearm = base(TValue.TV_POLEARM, "polearm");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(sword, polearm)));
        ObjectKind swordSpear = kind("Spear", sword, "Spear");
        ObjectKind polearmSpear = kind("Spear", polearm, "Spear");
        ObjectRegistry.addObjectKind(swordSpear);
        ObjectRegistry.addObjectKind(polearmSpear);

        assertSame(polearmSpear, ObjectRegistry.lookupObjectKind(TValue.TV_POLEARM, "Spear"));
        assertSame(swordSpear, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "Spear"));
    }

    // ---- lookup_curse and findact: first hit, every real entry ----------------

    /**
     * {@code lookup_curse} runs from index 1 to {@code curse_max - 1}, which is every real curse,
     * and stops at the first name that {@code streq}s. So the first and the last curse are both
     * found, and a name held twice answers with the earlier.
     */
    @Test
    void lookupCurseReachesTheFirstAndLastAndStopsAtTheFirstDuplicate() {
        Curse first = curse("vulnerability");
        Curse middle = curse("teleportation");
        Curse last = curse("siren");
        Curse again = curse("teleportation");
        ObjectRegistry.setCurses(new ArrayList<>(List.of(first, middle, last, again)));

        assertSame(first, ObjectRegistry.lookupCurse("vulnerability"));
        assertSame(last, ObjectRegistry.lookupCurse("siren"));
        assertSame(middle, ObjectRegistry.lookupCurse("teleportation"));
        assertNull(ObjectRegistry.lookupCurse("Siren"), "streq is case-sensitive");
        assertNull(ObjectRegistry.lookupCurse(""), "an empty name matches no curse");
    }

    /**
     * {@code findact} walks the list from its first real entry and breaks on the first
     * {@code streq}, so the first and last activations are found and a repeated name answers with
     * the earlier. A miss ends the walk on {@code NULL}.
     */
    @Test
    void lookupActivationReachesTheFirstAndLastAndStopsAtTheFirstDuplicate() {
        Activation first = activation("RING_ACID");
        Activation last = activation("DRAGON_BLUE");
        Activation again = activation("RING_ACID");
        ObjectRegistry.setActivations(new ArrayList<>(List.of(first, last, again)));

        assertSame(first, ObjectRegistry.lookupActivation("RING_ACID"));
        assertSame(last, ObjectRegistry.lookupActivation("DRAGON_BLUE"));
        assertNull(ObjectRegistry.lookupActivation("ring_acid"), "streq is case-sensitive");
        assertNull(ObjectRegistry.lookupActivation("NO_SUCH_ACT"));
    }
}
