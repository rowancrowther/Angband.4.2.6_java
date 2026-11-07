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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ObjectKindFlag}, the port of C's {@code KF_*} list in
 * {@code list-kind-flags.h}.
 *
 * <p>The expected table is typed out from the C header, not read back from the enum, so a flag
 * moved, dropped, added or relabelled in the port fails here. The case that prompted the test is
 * the four {@code KF_IGNORE_*} constants an earlier version carried: C has no such kind flags,
 * because {@code grab_element_flag} in {@code obj-init.c} turns {@code IGNORE_FIRE} and its
 * siblings into element info, so they must not resolve as kind flags.
 *
 * <p>Class ObjectKindFlagTest coded on 261012, commented in full on 261012.
 */
class ObjectKindFlagTest {

    /**
     * Every {@code KF()} entry of {@code list-kind-flags.h}, in the order the header lists them,
     * {@code KF_NONE} first and {@code KF_MAX} last. Position in this array is the flag's number in
     * C, which is why the Java ordinal has to equal it.
     */
    private static final String[] C_NAMES = {
            "KF_NONE",
            "KF_RAND_HI_RES",
            "KF_RAND_SUSTAIN",
            "KF_RAND_POWER",
            "KF_INSTA_ART",
            "KF_QUEST_ART",
            "KF_EASY_KNOW",
            "KF_GOOD",
            "KF_SHOW_DICE",
            "KF_SHOW_MULT",
            "KF_SHOOTS_SHOTS",
            "KF_SHOOTS_ARROWS",
            "KF_SHOOTS_BOLTS",
            "KF_RAND_BASE_RES",
            "KF_RAND_RES_POWER",
            "KF_MAX",
    };

    /**
     * The enum has exactly the header's sixteen entries, so {@code KF_MAX} is 15 as in C. Any extra
     * constant (the old {@code KF_IGNORE_*} four) shifts {@code KF_MAX} and fails this.
     */
    @Test
    void hasExactlyTheEntriesOfTheCHeader() {
        assertEquals(C_NAMES.length, ObjectKindFlag.values().length);
        assertEquals(15, ObjectKindFlag.KF_MAX.ordinal());
    }

    /** Each constant sits at its C number, and has the C spelling. */
    @Test
    void everyFlagIsInTheSamePositionAsInC() {
        ObjectKindFlag[] actual = ObjectKindFlag.values();
        for (int i = 0; i < C_NAMES.length; i++) {
            assertEquals(C_NAMES[i], actual[i].name(), "flag number " + i);
            assertEquals(i, actual[i].ordinal(), C_NAMES[i]);
        }
    }

    /** {@code KF_NONE} is flag 0, as every C flag list starts. */
    @Test
    void noneIsFlagZero() {
        assertEquals(0, ObjectKindFlag.KF_NONE.ordinal());
    }

    /**
     * The assemblers resolve a {@code flags:} token with {@code valueOf("KF_" + token)}. Every
     * kind-flag token the shipped {@code object.txt}, {@code object_base.txt} and
     * {@code ego_item.txt} use must resolve.
     */
    @Test
    void dataFileTokensResolveWithTheKfPrefix() {
        String[] tokens = {"EASY_KNOW", "SHOW_DICE", "SHOW_MULT", "SHOOTS_SHOTS", "SHOOTS_ARROWS",
                "SHOOTS_BOLTS", "RAND_HI_RES", "RAND_SUSTAIN", "RAND_POWER", "RAND_BASE_RES",
                "RAND_RES_POWER", "INSTA_ART", "QUEST_ART", "GOOD"};
        for (String token : tokens) {
            assertEquals("KF_" + token, ObjectKindFlag.valueOf("KF_" + token).name(), token);
        }
    }

    /**
     * {@code IGNORE_<element>} is an element flag in C ({@code grab_element_flag}), never a kind
     * flag, so the four tokens in the shipped data must not resolve here.
     */
    @Test
    void ignoreTokensAreNotKindFlags() {
        for (String element : new String[]{"FIRE", "ACID", "ELEC", "COLD"}) {
            String name = "KF_IGNORE_" + element;
            assertThrows(IllegalArgumentException.class, () -> ObjectKindFlag.valueOf(name), name);
        }
    }

    /** A token that is not in the list must fail, not fall to some default. */
    @Test
    void unknownTokenDoesNotResolve() {
        assertThrows(IllegalArgumentException.class, () -> ObjectKindFlag.valueOf("KF_NOTAFLAG"));
        assertThrows(IllegalArgumentException.class, () -> ObjectKindFlag.valueOf("EASY_KNOW"));
    }

    /**
     * C's {@code lookup_flag} searches the {@code kind_flags} name table from {@code FLAG_START}
     * (1), so every real flag from {@code RAND_HI_RES} to {@code RAND_RES_POWER} resolves to its
     * own constant. The prefixed name is the Java spelling of the table entry.
     */
    @Test
    void lookupResolvesEveryRealFlagToItself() {
        for (int i = 1; i < C_NAMES.length - 1; i++) {
            ObjectKindFlag found = ObjectKindFlag.lookup(C_NAMES[i]);
            assertNotNull(found, C_NAMES[i]);
            assertEquals(C_NAMES[i], found.name());
            assertEquals(i, found.ordinal(), C_NAMES[i]);
        }
    }

    /**
     * {@code lookup_flag} starts at index 1, so slot 0 ({@code NONE}) is never matched and C
     * reports an invalid flag; the Java "not found" is {@code null}.
     */
    @Test
    void lookupDoesNotMatchTheNoneSlot() {
        assertNull(ObjectKindFlag.lookup("KF_NONE"));
    }

    /**
     * The {@code kind_flags} table is built from the whole list, so its last entry is {@code MAX}
     * and {@code lookup_flag} finds it at index 15. Java agrees: {@code "KF_MAX"} resolves.
     */
    @Test
    void lookupMatchesTheMaxSlotAsCDoes() {
        assertSame(ObjectKindFlag.KF_MAX, ObjectKindFlag.lookup("KF_MAX"));
    }

    /**
     * Not found is {@code null}, never an exception and never some default flag: an unknown name,
     * the unprefixed data-file spelling, the {@code IGNORE_*} element tokens, a wrong-case name,
     * the empty string and {@code null} (C's {@code streq} is exact and case-sensitive).
     */
    @Test
    void lookupReturnsNullForAnythingElse() {
        assertNull(ObjectKindFlag.lookup("KF_NOTAFLAG"));
        assertNull(ObjectKindFlag.lookup("EASY_KNOW"));
        assertNull(ObjectKindFlag.lookup("KF_IGNORE_FIRE"));
        assertNull(ObjectKindFlag.lookup("IGNORE_FIRE"));
        assertNull(ObjectKindFlag.lookup("kf_good"));
        assertNull(ObjectKindFlag.lookup("KF_Good"));
        assertNull(ObjectKindFlag.lookup(" KF_GOOD"));
        assertNull(ObjectKindFlag.lookup("KF_"));
        assertNull(ObjectKindFlag.lookup(""));
        assertNull(ObjectKindFlag.lookup(null));
    }

    /**
     * The tokens the shipped data files use for kind flags all resolve through
     * {@code lookup("KF_" + token)}, which is how the object-kind assembler calls it.
     */
    @Test
    void lookupResolvesEveryDataFileToken() {
        String[] tokens = {"EASY_KNOW", "SHOW_DICE", "SHOW_MULT", "SHOOTS_SHOTS", "SHOOTS_ARROWS",
                "SHOOTS_BOLTS", "RAND_HI_RES", "RAND_SUSTAIN", "RAND_POWER", "RAND_BASE_RES",
                "RAND_RES_POWER", "INSTA_ART", "QUEST_ART", "GOOD"};
        for (String token : tokens) {
            ObjectKindFlag found = ObjectKindFlag.lookup("KF_" + token);
            assertNotNull(found, token);
            assertEquals("KF_" + token, found.name());
        }
    }

    /** Every message column in {@code list-kind-flags.h} is the empty string. */
    @Test
    void everyMessageIsEmpty() {
        for (ObjectKindFlag flag : ObjectKindFlag.values()) {
            assertEquals("", flag.getFlag(), flag.name());
        }
    }

    /**
     * A {@code Flag<ObjectKindFlag>} holds each of the real flags independently, and the
     * base-to-kind union that {@code finish_parse_object} does ({@code kf_union}) keeps both sets.
     */
    @Test
    void flagSetHoldsAndUnionsKindFlags() {
        Flag<ObjectKindFlag> kind = new Flag<>(ObjectKindFlag.class);
        Flag<ObjectKindFlag> base = new Flag<>(ObjectKindFlag.class);
        kind.on(ObjectKindFlag.KF_SHOOTS_ARROWS);
        base.on(ObjectKindFlag.KF_EASY_KNOW);

        kind.union(base);

        assertTrue(kind.has(ObjectKindFlag.KF_SHOOTS_ARROWS));
        assertTrue(kind.has(ObjectKindFlag.KF_EASY_KNOW));
        assertFalse(kind.has(ObjectKindFlag.KF_SHOOTS_BOLTS));
        assertFalse(base.has(ObjectKindFlag.KF_SHOOTS_ARROWS));
    }
}
