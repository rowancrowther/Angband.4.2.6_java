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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.ObjectBaseReader;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ObjectBase}, the port of C's {@code struct object_base} ({@code object.h}).
 *
 * <p>The class has no behaviour beyond its constructor, so the C-derived expectations come from two
 * places: the zero-state C gives a record after {@code parse_object_base_name} (empty object flags,
 * {@code num_svals} 0, a zeroed {@code el_info} entry for every element that is not hated), and the
 * shipped {@code object_base.txt} itself, read by a small independent parser that follows
 * {@code obj-init.c} (defaults first, per-record override after, {@code HATES_x} sets
 * {@code EL_INFO_HATES} on element {@code x}). That parser shares no code with the grammar and
 * assembler, so agreement between the two is evidence rather than mirroring.
 *
 * <p>Class ObjectBaseTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class ObjectBaseTest {

    private static final String REAL_FILE = "lib/gamedata/object_base.txt";

    private static Flag<ElementEnum> hates(ElementEnum... elements) {
        // The varargs constructor rejects an empty list, so build empty and switch each flag on.
        Flag<ElementEnum> flag = new Flag<>(ElementEnum.class);
        for (ElementEnum element : elements) {
            flag.on(element);
        }
        return flag;
    }

    private static ObjectBase base(Flag<ElementEnum> hates, Flag<ObjectKindFlag> kindFlags) {
        return new ObjectBase(TValue.TV_ARROW, "Arrow~", ColourEnum.COLOUR_LIGHT_UMBER, kindFlags, hates, 35, 40);
    }

    // ---- Constructor: plain fields ----------------------------------------

    /**
     * Reads {@code object_base.txt} the way {@code obj-init.c} does: {@code default:} lines set the
     * values every later {@code name:} record starts from, {@code break:}/{@code max-stack:}
     * override them, and each {@code HATES_x} token in a {@code flags:} line marks element
     * {@code x}. Other tokens ({@code EASY_KNOW}, {@code SHOW_DICE}...) are kind flags and are
     * ignored here.
     */
    private static List<Expected> readFileAsC() throws IOException {
        int defBreak = 0;
        int defStack = 0;
        List<Expected> out = new ArrayList<>();
        String tval = null;
        String name = "";
        String colour = null;
        int brk = 0;
        int stack = 0;
        Set<ElementEnum> hated = EnumSet.noneOf(ElementEnum.class);
        for (String line : Files.readAllLines(Path.of(REAL_FILE))) {
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split(":", 3);
            switch (parts[0]) {
                case "default" -> {
                    if (parts[1].equals("break-chance")) {
                        defBreak = Integer.parseInt(parts[2].trim());
                    } else if (parts[1].equals("max-stack")) {
                        defStack = Integer.parseInt(parts[2].trim());
                    }
                }
                case "name" -> {
                    if (tval != null) {
                        out.add(new Expected(tval, name, colour, brk, stack, hated));
                    }
                    tval = parts[1];
                    name = parts.length > 2 ? parts[2] : "";
                    colour = null;
                    brk = defBreak;
                    stack = defStack;
                    hated = EnumSet.noneOf(ElementEnum.class);
                }
                case "graphics" -> colour = parts[1];
                case "break" -> brk = Integer.parseInt(parts[1].trim());
                case "max-stack" -> stack = Integer.parseInt(parts[1].trim());
                case "flags" -> {
                    for (String token : parts[1].split("[ |]+")) {
                        if (token.startsWith("HATES_")) {
                            hated.add(ElementEnum.valueOf("ELEM_" + token.substring(6)));
                        }
                    }
                }
                default -> {
                    // record-count: and any other header line
                }
            }
        }
        out.add(new Expected(tval, name, colour, brk, stack, hated));
        return out;
    }

    @Test
    void constructorStoresEveryScalarFieldAsGiven() {
        Flag<ObjectKindFlag> kf = new Flag<>(ObjectKindFlag.class, ObjectKindFlag.KF_SHOW_DICE);
        ObjectBase b = base(hates(), kf);

        assertEquals(TValue.TV_ARROW, b.gettVal());
        assertEquals("Arrow~", b.getName());
        assertEquals(ColourEnum.COLOUR_LIGHT_UMBER, b.getAttr());
        assertEquals(35, b.getBreakPerc());
        assertEquals(40, b.getMaxStack());
        assertTrue(b.getKindFlags().has(ObjectKindFlag.KF_SHOW_DICE));
    }

    @Test
    void freshBaseHasZeroSvalsAndEmptyObjectFlags() {
        // C: parse_object_base_name sets num_svals = 0 and the memcpy of the zeroed defaults leaves
        // flags[] clear.
        ObjectBase b = base(hates(), new Flag<>(ObjectKindFlag.class));

        assertEquals(0, b.getNumSvals());
        assertTrue(b.getFlags().isEmpty());
    }

    // ---- Constructor: HATES_ folding --------------------------------------

    @Test
    void setNumSvalsOverwritesTheCount() {
        ObjectBase b = base(hates(), new Flag<>(ObjectKindFlag.class));

        b.setNumSvals(7);

        assertEquals(7, b.getNumSvals());
    }

    @Test
    void noHatedElementsGivesAnEmptyElementMap() {
        // C: el_info[] all zero, so no element carries any flag.
        ObjectBase b = base(hates(), new Flag<>(ObjectKindFlag.class));

        assertTrue(b.getElementMap().isEmpty());
    }

    @Test
    void eachHatedElementGetsAHatesOnlyEntryWithNeutralResistance() {
        // object_base.txt "arrow": flags:HATES_ACID | HATES_FIRE
        ObjectBase b = base(hates(ElementEnum.ELEM_ACID, ElementEnum.ELEM_FIRE),
                new Flag<>(ObjectKindFlag.class));

        assertEquals(Set.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_FIRE), b.getElementMap().keySet());
        for (ElementInfo info : b.getElementMap().values()) {
            assertTrue(info.has(ElementInfoEnum.EL_INFO_HATES));
            assertFalse(info.has(ElementInfoEnum.EL_INFO_IGNORE), "grab_element_flag sets only the named flag");
            assertFalse(info.has(ElementInfoEnum.EL_INFO_RANDOM));
            assertEquals(0, info.getResLevel(), "el_info res_level is zero until something sets it");
        }
    }

    @Test
    void anElementThatIsNotHatedHasNoEntry() {
        // bolt: HATES_ACID only. C's FIRE slot is a zeroed el_info; the sparse map has no key.
        ObjectBase b = base(hates(ElementEnum.ELEM_ACID), new Flag<>(ObjectKindFlag.class));

        assertFalse(b.getElementMap().containsKey(ElementEnum.ELEM_FIRE));
    }

    // ---- Constructor: aliasing (documented behaviour) ---------------------

    @Test
    void hatesFlagIsOnlyReadNotRetained() {
        Flag<ElementEnum> h = hates(ElementEnum.ELEM_ACID);
        ObjectBase b = base(h, new Flag<>(ObjectKindFlag.class));

        h.on(ElementEnum.ELEM_COLD);

        assertFalse(b.getElementMap().containsKey(ElementEnum.ELEM_COLD));
    }

    // ---- setElementInfo ---------------------------------------------------

    @Test
    void kindFlagsAreStoredByReferenceNotCopied() {
        // Pinned because it differs from C, where the record owns its own bit array. If the
        // constructor is ever changed to copy, this test and the constructor Javadoc change together.
        Flag<ObjectKindFlag> kf = new Flag<>(ObjectKindFlag.class);
        ObjectBase b = base(hates(), kf);

        kf.on(ObjectKindFlag.KF_EASY_KNOW);

        assertTrue(b.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW));
        assertSame(kf, b.getKindFlags());
    }

    // ---- toString ---------------------------------------------------------

    @Test
    void setElementInfoReplacesTheTableWithoutMerging() {
        ObjectBase b = base(hates(ElementEnum.ELEM_ACID), new Flag<>(ObjectKindFlag.class));
        Map<ElementEnum, ElementInfo> replacement = new HashMap<>();
        ElementInfo cold = new ElementInfo();
        cold.on(ElementInfoEnum.EL_INFO_IGNORE);
        replacement.put(ElementEnum.ELEM_COLD, cold);

        b.setElementInfo(replacement);

        assertSame(replacement, b.getElementMap());
        assertFalse(b.getElementMap().containsKey(ElementEnum.ELEM_ACID), "the constructor's entry is gone");
        assertTrue(b.getElementMap().get(ElementEnum.ELEM_COLD).has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    // ---- Shipped data, checked against an independent reading of the file -

    @Test
    void toStringNamesTheScalarFields() {
        ObjectBase b = base(hates(), new Flag<>(ObjectKindFlag.class));
        b.setNumSvals(3);

        String s = b.toString();

        assertTrue(s.startsWith("ObjectBase{"), s);
        assertTrue(s.contains("name='Arrow~'"), s);
        assertTrue(s.contains("tVal=TV_ARROW"), s);
        assertTrue(s.contains("breakPerc=35"), s);
        assertTrue(s.contains("maxStack=40"), s);
        assertTrue(s.contains("numSvals=3"), s);
    }

    @Test
    void everyShippedBaseMatchesAnIndependentReadingOfTheFile() throws IOException {
        List<Expected> expected = readFileAsC();
        ParseResult<ObjectBase> result = new ObjectBaseReader().parseWithResults(REAL_FILE);
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        List<ObjectBase> actual = result.items().stream()
                .filter(b -> b.gettVal() != TValue.TV_NONE)
                .toList();

        assertEquals(expected.size(), actual.size(), "record count");
        for (int i = 0; i < expected.size(); i++) {
            Expected e = expected.get(i);
            ObjectBase a = actual.get(i);
            String where = "record " + i + " (" + e.tval() + ")";

            assertEquals(e.name(), a.getName(), where + " name");
            assertEquals(ColourEnum.fromCode(e.colour()), a.getAttr(), where + " colour");
            assertEquals(e.breakPerc(), a.getBreakPerc(), where + " break_perc");
            assertEquals(e.maxStack(), a.getMaxStack(), where + " max_stack");
            assertEquals(e.hated(), a.getElementMap().keySet(), where + " hated elements");
            for (ElementInfo info : a.getElementMap().values()) {
                assertTrue(info.has(ElementInfoEnum.EL_INFO_HATES), where);
                assertFalse(info.has(ElementInfoEnum.EL_INFO_IGNORE), where);
            }
            assertTrue(a.getFlags().isEmpty(), where + ": the shipped file has no OF_ tokens");
            assertEquals(0, a.getNumSvals(), where + " num_svals");
        }
    }

    @Test
    void spotCheckedRecordsMatchTheFileByHand() throws IOException {
        // Values read straight off lib/gamedata/object_base.txt, not off the independent parser.
        ParseResult<ObjectBase> result = new ObjectBaseReader().parseWithResults(REAL_FILE);
        Map<TValue, ObjectBase> byTval = new HashMap<>();
        result.items().forEach(b -> byTval.put(b.gettVal(), b));

        ObjectBase chest = byTval.get(TValue.TV_CHEST);
        assertEquals(10, chest.getBreakPerc(), "inherits default:break-chance:10");
        assertEquals(40, chest.getMaxStack(), "inherits default:max-stack:40");
        assertEquals(Set.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_FIRE), chest.getElementMap().keySet());

        ObjectBase shot = byTval.get(TValue.TV_SHOT);
        assertEquals(0, shot.getBreakPerc(), "break:0 overrides the default");
        assertTrue(shot.getKindFlags().has(ObjectKindFlag.KF_SHOW_DICE));
        assertTrue(shot.getElementMap().isEmpty());

        assertEquals(35, byTval.get(TValue.TV_ARROW).getBreakPerc());
        assertEquals(20, byTval.get(TValue.TV_BOLT).getBreakPerc());
        assertEquals(Set.of(ElementEnum.ELEM_ACID), byTval.get(TValue.TV_BOLT).getElementMap().keySet());
    }

    /** One record as C's obj-init.c would build it: defaults, then per-record overrides. */
    private record Expected(String tval, String name, String colour, int breakPerc, int maxStack,
                            Set<ElementEnum> hated) {
    }
}
