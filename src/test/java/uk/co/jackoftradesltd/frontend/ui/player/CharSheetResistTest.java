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

package uk.co.jackoftradesltd.frontend.ui.player;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for {@link CharSheetResist}, the Java form of C's {@code struct char_sheet_resist}
 * ({@code ui-player.c}). C's struct has exactly two members, {@code entry} and {@code label[6]};
 * the colon C writes into slot 5 is carried inside the Java label string, so the label is stored
 * verbatim with no padding or truncation of its own.
 *
 * <p>Class CharSheetResistTest coded on 260929, commented in full on 260929.
 */
class CharSheetResistTest {

    private static UIEntry makeEntry() {
        return new UIEntry("resist", null, null, StatElemType.NONE, null, null, java.util.List.of(),
                0, null, new Flag<>(ChannelEntryFlag.class), "d", "Acid", "Ac", "Acid");
    }

    /**
     * The constructor stores the entry by reference (C stores the {@code ui_entry} pointer) and
     * leaves the label empty until it is filled.
     */
    @Test
    void constructorKeepsEntryAndEmptyLabel() {
        UIEntry entry = makeEntry();
        CharSheetResist resist = new CharSheetResist(entry);

        assertSame(entry, resist.entry);
        assertEquals("", resist.getLabel());
    }

    /**
     * A null entry is accepted, as in the config tests that never render the row.
     */
    @Test
    void nullEntryIsAccepted() {
        CharSheetResist resist = new CharSheetResist(null);

        assertNull(resist.entry);
        assertEquals("", resist.getLabel());
    }

    /**
     * A finished six-character label, {@code "   Ac:"} (five padded characters then C's colon in
     * slot 5), round-trips unchanged.
     */
    @Test
    void labelRoundTripsVerbatim() {
        CharSheetResist resist = new CharSheetResist(null);

        resist.setLabel("   Ac:");

        assertEquals("   Ac:", resist.getLabel());
        assertEquals(6, resist.getLabel().length());
    }

    /**
     * The setter does no padding or truncation, unlike {@code get_ui_entry_label}: a label longer
     * than C's six-slot buffer, or empty, is stored as given.
     */
    @Test
    void setLabelDoesNotPadOrTruncate() {
        CharSheetResist resist = new CharSheetResist(null);

        resist.setLabel("Acid Resist:");
        assertEquals("Acid Resist:", resist.getLabel());

        resist.setLabel("");
        assertEquals("", resist.getLabel());
    }

    /**
     * A second {@code setLabel} replaces the first; nothing accumulates.
     */
    @Test
    void setLabelReplacesPreviousValue() {
        CharSheetResist resist = new CharSheetResist(null);

        resist.setLabel("   Ac:");
        resist.setLabel("   Fi:");

        assertEquals("   Fi:", resist.getLabel());
    }

    /**
     * Setting the label leaves the entry reference untouched.
     */
    @Test
    void setLabelDoesNotTouchEntry() {
        UIEntry entry = makeEntry();
        CharSheetResist resist = new CharSheetResist(entry);

        resist.setLabel("   Ac:");

        assertSame(entry, resist.entry);
    }

    /**
     * Two instances share nothing: labels are independent per row, as C's array elements are.
     */
    @Test
    void instancesAreIndependent() {
        CharSheetResist a = new CharSheetResist(null);
        CharSheetResist b = new CharSheetResist(null);

        a.setLabel("   Ac:");

        assertEquals("", b.getLabel());
    }

    /**
     * The class holds exactly the two members C's struct does ({@code entry} and {@code label}),
     * both per-instance.
     */
    @Test
    void hasExactlyTheTwoStructMembers() {
        Field[] fields = CharSheetResist.class.getDeclaredFields();

        assertEquals(2, fields.length);
        assertEquals("entry", fields[0].getName());
        assertEquals("label", fields[1].getName());
        for (Field f : fields) {
            assertEquals(false, Modifier.isStatic(f.getModifiers()), f.getName());
        }
    }
}
