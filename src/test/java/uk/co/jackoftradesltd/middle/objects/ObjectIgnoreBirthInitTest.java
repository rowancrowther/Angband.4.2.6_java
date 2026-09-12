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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.QualityValueEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectIgnore#ignoreBirthInit} — the port of C's {@code ignore_birth_init}
 * ({@code obj-ignore.c:143-159}).
 *
 * <p>The C function walks three fixed-size tables and zeroes every slot it holds, so the tests here
 * seed each table with a non-default value first and check the sweep reaches every entry — not just
 * the first — and, at the {@code ITYPE_MAX} boundary the C loops share, that the sentinel slot itself
 * is left alone rather than being written through an out-of-range index.
 *
 * <p>Class ObjectIgnoreBirthInitTest coded on 260908, commented in full on 260908.
 *
 * @author Rowan Crowther
 */
class ObjectIgnoreBirthInitTest {

    private List<ObjectKind> savedKinds;
    private List<EgoItem> savedEgos;
    private Map<IgnoreType, QualityValueEnum> savedIgnoreLevels;

    /**
     * Reads {@code ObjectRegistry.egoItems} directly, since it starts life {@code null} rather than
     * an empty list, and {@link ObjectRegistry#getEgoItems} cannot wrap a {@code null} in an
     * unmodifiable view.
     */
    @SuppressWarnings("unchecked")
    private static List<EgoItem> rawEgoItems() throws Exception {
        Field field = ObjectRegistry.class.getDeclaredField("egoItems");
        field.setAccessible(true);
        return (List<EgoItem>) field.get(null);
    }

    /**
     * Writes {@code ObjectRegistry.egoItems} back exactly as it was found, {@code null} included,
     * rather than going through {@link ObjectRegistry#setEgoItems} and leaving a previously-null
     * field permanently non-null.
     */
    private static void restoreRawEgoItems(List<EgoItem> value) throws Exception {
        Field field = ObjectRegistry.class.getDeclaredField("egoItems");
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Reads {@code ObjectRegistry.objectKinds} directly, since {@link ObjectRegistry#getObjectKinds}
     * wraps it in an unmodifiable view — feeding that view back through {@link
     * ObjectRegistry#setObjectKinds} would leave the shared field permanently unmodifiable for every
     * test that runs after this class.
     */
    @SuppressWarnings("unchecked")
    private static List<ObjectKind> rawObjectKinds() throws Exception {
        Field field = ObjectRegistry.class.getDeclaredField("objectKinds");
        field.setAccessible(true);
        return (List<ObjectKind>) field.get(null);
    }

    /**
     * Writes {@code ObjectRegistry.objectKinds} back exactly as it was found, bypassing {@link
     * ObjectRegistry#setObjectKinds} for the same reason as {@link #restoreRawEgoItems}.
     */
    private static void restoreRawObjectKinds(List<ObjectKind> value) throws Exception {
        Field field = ObjectRegistry.class.getDeclaredField("objectKinds");
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * An ego with nothing on it, for the cases that only read the ignore-type map.
     */
    private static EgoItem bareEgo() {
        return new EgoItem("of Testing", "a test ego", 1, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class),
                new Flag<>(ObjectKindFlag.class), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashSet<>(), new HashSet<>(), new HashMap<>(),
                0, 0, 0, 0, new ArrayList<>(), null, null, null, 0, 0, 0,
                null, null, false);
    }

    @BeforeEach
    void saveState() throws Exception {
        savedKinds = rawObjectKinds();
        savedEgos = rawEgoItems();
        savedIgnoreLevels = ObjectInfo.ignoreLevel;
        ObjectInfo.ignoreLevel = new HashMap<>(savedIgnoreLevels);

        // ObjectRegistry.getEgoItems can't wrap a null list; real play always loads the ego table
        // before birth runs, so every test starts from that same non-null baseline.
        ObjectRegistry.setEgoItems(List.of());
    }

    @AfterEach
    void restoreState() throws Exception {
        restoreRawObjectKinds(savedKinds);
        restoreRawEgoItems(savedEgos);
        ObjectInfo.ignoreLevel = savedIgnoreLevels;
    }

    /**
     * C's first sweep is {@code k_info[i].ignore = false} across every loaded kind. Both bits are
     * raised on two separate kinds beforehand, so the test also confirms the loop does not stop
     * after the first entry.
     */
    @Test
    @DisplayName("wipes both ignore flags on every loaded kind")
    void wipesIgnoreFlagsOnEveryKind() {
        ObjectKind first = new ObjectKind();
        first.setIgnoredAware(true);
        first.setIgnoredUnaware(true);
        ObjectKind second = new ObjectKind();
        second.setIgnoredAware(true);
        second.setIgnoredUnaware(true);
        ObjectRegistry.setObjectKinds(List.of(first, second));

        ObjectIgnore.ignoreBirthInit();

        assertFalse(first.isIgnoredAware());
        assertFalse(first.isIgnoredUnaware());
        assertFalse(second.isIgnoredAware());
        assertFalse(second.isIgnoredUnaware());
    }

    /**
     * C's second sweep runs {@code for (i = ITYPE_NONE; i < ITYPE_MAX; i++)}, an exclusive upper
     * bound: every real quality category is reset to {@code IGNORE_NONE}, but the {@code ITYPE_MAX}
     * slot — a size sentinel, not a category — is never indexed at all. Every type, sentinel
     * included, starts at {@code IGNORE_ALL} here so the untouched slot is visibly different from a
     * merely-already-{@code IGNORE_NONE} one.
     */
    @Test
    @DisplayName("resets every quality band except the ITYPE_MAX sentinel")
    void resetsIgnoreLevelExceptSentinel() {
        for (IgnoreType type : IgnoreType.values()) {
            ObjectInfo.ignoreLevel.put(type, QualityValueEnum.IGNORE_ALL);
        }

        ObjectIgnore.ignoreBirthInit();

        for (IgnoreType type : IgnoreType.values()) {
            if (type == IgnoreType.ITYPE_MAX) continue;
            assertEquals(QualityValueEnum.IGNORE_NONE, ObjectInfo.ignoreLevel.get(type),
                    type + " is reset");
        }
        assertEquals(QualityValueEnum.IGNORE_ALL, ObjectInfo.ignoreLevel.get(IgnoreType.ITYPE_MAX),
                "the sentinel is never indexed by C's loop, so it is left as it was");
    }

    /**
     * C's third sweep nests the same {@code ITYPE_NONE..ITYPE_MAX} bound inside a loop over every
     * loaded ego, zeroing {@code ego_ignore_types[i][j]}. Two egos are seeded here, each with two
     * categories marked, to check the sweep reaches every ego and every category on it.
     */
    @Test
    @DisplayName("clears every ignore-by-type mark on every loaded ego")
    void clearsEgoIgnoreTypesOnEveryEgo() {
        EgoItem first = bareEgo();
        first.setIgnoreType(IgnoreType.ITYPE_SHARP);
        first.setIgnoreType(IgnoreType.ITYPE_RING);
        EgoItem second = bareEgo();
        second.setIgnoreType(IgnoreType.ITYPE_BOW);
        second.setIgnoreType(IgnoreType.ITYPE_AMULET);
        ObjectRegistry.setEgoItems(List.of(first, second));

        ObjectIgnore.ignoreBirthInit();

        assertFalse(first.getIgnoreType(IgnoreType.ITYPE_SHARP));
        assertFalse(first.getIgnoreType(IgnoreType.ITYPE_RING));
        assertFalse(second.getIgnoreType(IgnoreType.ITYPE_BOW));
        assertFalse(second.getIgnoreType(IgnoreType.ITYPE_AMULET));
    }

    /**
     * The same {@code ITYPE_MAX} boundary as {@link #resetsIgnoreLevelExceptSentinel}, but on the
     * per-ego map: C's inner loop never reaches index {@code ITYPE_MAX} in
     * {@code ego_ignore_types[i]} either, so a mark placed there survives the sweep.
     */
    @Test
    @DisplayName("leaves an ego's ITYPE_MAX slot untouched")
    void leavesEgoSentinelSlotUntouched() {
        EgoItem ego = bareEgo();
        ego.setIgnoreType(IgnoreType.ITYPE_MAX);
        ObjectRegistry.setEgoItems(List.of(ego));

        ObjectIgnore.ignoreBirthInit();

        assertTrue(ego.getIgnoreType(IgnoreType.ITYPE_MAX),
                "the sentinel is never indexed by C's loop, so it is left as it was");
    }

    /**
     * With nothing loaded, the first and third sweeps simply run zero times — C's own loops over
     * {@code z_info->k_max} and {@code z_info->e_max} do the same when the tables are empty — but the
     * fixed-size ignore-level sweep is independent of either table and still runs in full.
     */
    @Test
    @DisplayName("still resets the quality bands when no kinds or egos are loaded")
    void resetsIgnoreLevelWithEmptyTables() {
        ObjectRegistry.setObjectKinds(List.of());
        ObjectRegistry.setEgoItems(List.of());
        ObjectInfo.ignoreLevel.put(IgnoreType.ITYPE_LIGHT, QualityValueEnum.IGNORE_ALL);

        ObjectIgnore.ignoreBirthInit();

        assertEquals(QualityValueEnum.IGNORE_NONE, ObjectInfo.ignoreLevel.get(IgnoreType.ITYPE_LIGHT));
    }
}
