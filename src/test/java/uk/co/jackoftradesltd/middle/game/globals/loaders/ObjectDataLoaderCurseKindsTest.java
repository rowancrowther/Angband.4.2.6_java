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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ObjectDataLoader#parseCurseKinds()} and its private worker, the port of C's
 * {@code write_curse_kinds} in {@code obj-init.c}, which runs at the end of
 * {@code finish_parse_artifact}.
 *
 * <p>The expected values are read off the C function. Per curse it sets {@code obj->kind} and
 * {@code obj->sval}, makes {@code obj->known} only if there is none, sets the known object's kind
 * and sval, and ORs {@code OBJ_NOTICE_ASSESSED} into the known object's notice, never the real
 * object's. The kind is {@code lookup_kind(none, lookup_sval(none, "<curse object>"))}, and
 * {@code lookup_sval} answers {@code -1} for a name it cannot find. C's loop skips slot 0, a dummy;
 * the port's list has none.
 *
 * <p>The registry's kind table and curse list are replaced by reflection and put back afterwards,
 * so the tests do not depend on the data files having been loaded.
 *
 * <p>Class ObjectDataLoaderCurseKindsTest coded on 261005.
 */
class ObjectDataLoaderCurseKindsTest {

    private List<ObjectKind> savedObjectKinds;
    private List<Curse> savedCurses;

    private static Field registryField(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * A kind with the given type, sval name and numeric sval. The type and sval name are set by
     * reflection since the no-argument constructor leaves them unset and there are no setters for
     * them; the sval has a public setter.
     */
    private static ObjectKind kind(TValue tValue, String svalName, int sval) throws Exception {
        ObjectKind kind = new ObjectKind();
        Field tv = ObjectKind.class.getDeclaredField("tValue");
        tv.setAccessible(true);
        tv.set(kind, tValue);
        Field sn = ObjectKind.class.getDeclaredField("sValueName");
        sn.setAccessible(true);
        sn.set(kind, svalName);
        kind.setsVal(sval);
        return kind;
    }

    /**
     * A curse that has nothing but an object of its own, built by the current constructor.
     */
    private static Curse curse(String name, ItemObject object, int index) {
        return new Curse(name, List.of(), object, 0, null, new Flag<>(ObjectFlag.class),
                Map.of(), Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class),
                "", "", index);
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void isolate() throws Exception {
        savedObjectKinds = (List<ObjectKind>) registryField("objectKinds").get(null);
        savedCurses = (List<Curse>) registryField("curses").get(null);
    }

    @AfterEach
    void restore() throws Exception {
        registryField("objectKinds").set(null, savedObjectKinds);
        registryField("curses").set(null, savedCurses);
    }

    /**
     * The ordinary path. The curse's object takes the kind and the kind's sval, and gets a known
     * twin of the same kind and sval. A sval other than 1 is used so a hard-coded value shows up.
     */
    @Test
    void ordinaryPath_objectAndKnownTakeKindAndSval() throws Exception {
        ObjectKind curseKind = kind(TValue.TV_NONE, "<curse object>", 4);
        ObjectRegistry.setObjectKinds(List.of(curseKind));
        ItemObject object = new ItemObject();
        ObjectRegistry.setCurses(List.of(curse("vulnerability", object, 0)));

        ObjectDataLoader.parseCurseKinds();

        ItemObject known = object.getKnown();
        assertAll(
                () -> assertSame(curseKind, object.getKind()),
                () -> assertEquals(4, object.getsValue()),
                () -> assertNotNull(known),
                () -> assertSame(curseKind, known.getKind()),
                () -> assertEquals(4, known.getsValue()));
    }

    /**
     * C marks only the known object as assessed ("so it can be fully known"); the real object's
     * notice is left alone.
     */
    @Test
    void assessedIsOnTheKnownObjectOnly() throws Exception {
        ObjectRegistry.setObjectKinds(List.of(kind(TValue.TV_NONE, "<curse object>", 1)));
        ItemObject object = new ItemObject();
        ObjectRegistry.setCurses(List.of(curse("teleportation", object, 0)));

        ObjectDataLoader.parseCurseKinds();

        assertAll(
                () -> assertTrue(object.getKnown().getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED)),
                () -> assertFalse(object.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED)));
    }

    /**
     * C only allocates the known object if there is none ("tolerate an already allocated known
     * version"). An existing one keeps its identity and is still brought up to date.
     */
    @Test
    void existingKnownIsKeptAndUpdated() throws Exception {
        ObjectKind curseKind = kind(TValue.TV_NONE, "<curse object>", 2);
        ObjectRegistry.setObjectKinds(List.of(curseKind));
        ItemObject object = new ItemObject();
        ItemObject existing = new ItemObject();
        object.setKnown(existing);
        ObjectRegistry.setCurses(List.of(curse("siphoning", object, 0)));

        ObjectDataLoader.parseCurseKinds();

        assertAll(
                () -> assertSame(existing, object.getKnown()),
                () -> assertSame(curseKind, existing.getKind()),
                () -> assertEquals(2, existing.getsValue()),
                () -> assertTrue(existing.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED)));
    }

    /**
     * Every curse is written, including the one at index 0. C's loop starts at 1 only because its
     * slot 0 is a dummy; the port's list has no dummy, so skipping the first entry would leave a
     * real curse with no kind.
     */
    @Test
    void everyCurseIsWritten_includingIndexZero() throws Exception {
        ObjectKind curseKind = kind(TValue.TV_NONE, "<curse object>", 3);
        ObjectRegistry.setObjectKinds(List.of(curseKind));
        ItemObject first = new ItemObject();
        ItemObject second = new ItemObject();
        ItemObject third = new ItemObject();
        ObjectRegistry.setCurses(List.of(
                curse("first", first, 0), curse("second", second, 1), curse("third", third, 2)));

        ObjectDataLoader.parseCurseKinds();

        assertAll(
                () -> assertSame(curseKind, first.getKind()),
                () -> assertSame(curseKind, second.getKind()),
                () -> assertSame(curseKind, third.getKind()),
                () -> assertNotNull(first.getKnown()),
                () -> assertNotNull(second.getKnown()),
                () -> assertNotNull(third.getKnown()));
    }

    /**
     * The lookup is by tval {@code none} and sval name together, as {@code lookup_kind(none, ...)}.
     * A kind with the same name under another tval must not be taken.
     */
    @Test
    void lookupIsByTvalAndName() throws Exception {
        ObjectKind decoy = kind(TValue.TV_SWORD, "<curse object>", 9);
        ObjectKind curseKind = kind(TValue.TV_NONE, "<curse object>", 5);
        ObjectRegistry.setObjectKinds(List.of(decoy, curseKind));
        ItemObject object = new ItemObject();
        ObjectRegistry.setCurses(List.of(curse("anti-teleportation", object, 0)));

        ObjectDataLoader.parseCurseKinds();

        assertAll(
                () -> assertSame(curseKind, object.getKind()),
                () -> assertEquals(5, object.getsValue()));
    }

    /**
     * The boundary found in stage 1: the kind is missing from a non-empty table. C carries on —
     * {@code lookup_kind} answers NULL and {@code lookup_sval} answers -1, so every curse object
     * and its known twin get a NULL kind and sval -1, and the known twin is still made and
     * assessed. Nothing throws.
     */
    @Test
    void missingKind_nullKindAndSvalMinusOne_noThrow() throws Exception {
        ObjectRegistry.setObjectKinds(List.of(kind(TValue.TV_NONE, "<pile>", 1)));
        ItemObject object = new ItemObject();
        ObjectRegistry.setCurses(List.of(curse("impair hitpoint recovery", object, 0)));

        assertDoesNotThrow(ObjectDataLoader::parseCurseKinds);

        ItemObject known = object.getKnown();
        assertAll(
                () -> assertNull(object.getKind()),
                () -> assertEquals(-1, object.getsValue()),
                () -> assertNotNull(known),
                () -> assertNull(known.getKind()),
                () -> assertEquals(-1, known.getsValue()),
                () -> assertTrue(known.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED)));
    }

    /**
     * With no curses loaded the pass is a no-op rather than an error.
     */
    @Test
    void noCurses_isANoOp() throws Exception {
        ObjectRegistry.setObjectKinds(List.of(kind(TValue.TV_NONE, "<curse object>", 1)));
        ObjectRegistry.setCurses(List.of());

        assertDoesNotThrow(ObjectDataLoader::parseCurseKinds);
    }
}
