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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindAssembler;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourKindParseRecord;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourParseRecord;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Flavour;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the one caller of {@code ObjectKind.setTval}: the throwaway kind {@link FlavourKindAssembler}
 * builds when a {@code fixed:} flavour's sval symbol names no object kind.
 *
 * <p>C has no such kind. {@code parse_flavor_flavor} in {@code init.c} calls {@code lookup_sval}
 * ({@code obj-util.c}), which answers -1 for a name that matches nothing, and stores that in the
 * {@code uint8_t sval} of {@code struct flavor}, where it reads as 255. A symbol that does match
 * stores the matched kind's sval, and a {@code flavor:} line (no sval) stores {@code SV_UNKNOWN},
 * 0. Those three figures are the expected values.
 *
 * <p>The object-kind table is seeded with one kind, {@code Ring of Power}, sval 7, and restored
 * afterwards so no state leaks into other suites.
 *
 * <p>Class FlavourKindAssemblerSvalMissTest coded on 261025, commented in full on 261025.
 *
 * @author Rowan Crowther
 */
class FlavourKindAssemblerSvalMissTest {

    private static final String FILE = "flavor.txt";

    private Object savedObjectKinds;
    private Object savedKindsByTvalSval;

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    /**
     * Seeds the table with a single ring kind named {@code Ring of Power}, given sval 7 afterwards
     * because {@code addObjectKind} hands a kind with no base sval 0.
     */
    @BeforeEach
    void seed() throws Exception {
        savedObjectKinds = setStatic("objectKinds", new ArrayList<ObjectKind>());
        savedKindsByTvalSval = setStatic("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        ObjectKind ring = new ObjectKind(null, 0, 0, 0, 0, "Ring of Power", TValue.TV_RING,
                "Ring of Power", null, false);
        ObjectRegistry.addObjectKind(ring);
        ring.setsVal(7);
    }

    @AfterEach
    void restore() throws Exception {
        setStatic("objectKinds", savedObjectKinds);
        setStatic("kindsByTvalSval", savedKindsByTvalSval);
    }

    private static List<Flavour> assembleRingBlock(FlavourParseRecord... flavours) {
        FlavourKindParseRecord record = new FlavourKindParseRecord("ring", "=", List.of(flavours), 1);
        List<FlavourKind> kinds = new FlavourKindAssembler().assemble(List.of(record), new ArrayList<>(), FILE);
        assertEquals(1, kinds.size());
        return kinds.getFirst().getFlavours();
    }

    /**
     * The flavour with the given {@code fidx}; C pushes each new flavour onto the head of its list,
     * so the block comes back in reverse of file order and position says nothing.
     */
    private static Flavour byIndex(List<Flavour> flavours, int index) {
        return flavours.stream().filter(f -> f.getIndex() == index).findFirst().orElseThrow();
    }

    private static FlavourParseRecord fixed(String index, String sval) {
        return new FlavourParseRecord(index, sval, "Yellow", "Foo", 2);
    }

    private static FlavourParseRecord random(String index) {
        return new FlavourParseRecord(index, null, "Green", "Bar", 3);
    }

    /**
     * {@code lookup_sval} returns -1 for a name no kind of that tval has, and the {@code uint8_t}
     * flavour sval holds it as 255. The block is kept, as C keeps the flavour.
     */
    @Test
    void aSymbolNamingNoKindGives255() {
        List<Flavour> flavours = assembleRingBlock(fixed("1", "Nonexistent Ring"));
        assertEquals(1, flavours.size());
        assertEquals(255, flavours.getFirst().getsVal());
    }

    /**
     * A symbol that matches stores the matched kind's sval, compared without regard to case as
     * {@code my_stricmp} does.
     */
    @Test
    void aSymbolNamingAKindGivesThatKindsSval() {
        assertEquals(7, assembleRingBlock(fixed("1", "Ring of Power")).getFirst().getsVal());
        assertEquals(7, assembleRingBlock(fixed("1", "RING OF POWER")).getFirst().getsVal());
    }

    /**
     * {@code lookup_sval} matches only kinds of the block's own tval, so the same name under another
     * tval is a miss.
     */
    @Test
    void theSameNameUnderAnotherTvalIsAMiss() {
        FlavourKindParseRecord record = new FlavourKindParseRecord("amulet", "\"",
                List.of(fixed("1", "Ring of Power")), 1);
        List<FlavourKind> kinds = new FlavourKindAssembler().assemble(List.of(record), new ArrayList<>(), FILE);
        assertEquals(255, kinds.getFirst().getFlavours().getFirst().getsVal());
    }

    /**
     * A miss on one flavour does not disturb its siblings: the matching fixed flavour keeps 7 and
     * the random one keeps {@code SV_UNKNOWN}, 0.
     */
    @Test
    void aMissLeavesSiblingsAlone() {
        List<Flavour> flavours = assembleRingBlock(
                fixed("1", "Nonexistent Ring"), fixed("2", "Ring of Power"), random("3"));
        assertEquals(255, byIndex(flavours, 1).getsVal());
        assertEquals(7, byIndex(flavours, 2).getsVal());
        assertEquals(0, byIndex(flavours, 3).getsVal());
    }
}
