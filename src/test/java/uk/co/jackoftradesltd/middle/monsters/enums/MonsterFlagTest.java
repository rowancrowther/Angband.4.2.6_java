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

package uk.co.jackoftradesltd.middle.monsters.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link MonsterFlag} against the rows of {@code list-mon-temp-flags.h}, which C's
 * {@code MFLAG_*} enum in {@code monster.h} is built from.
 *
 * <p>The expected values are written out from the C header, one {@link Row} per {@code MFLAG(...)}
 * line, in its column order (name, description). The description is private and has no getter, so
 * it is read by reflection. C discards the description when it builds the enum, so the text is a
 * Java-side copy of the header and the only thing to compare it with is the header itself. The
 * position of each flag is C's index for it, because {@code MFLAG_NONE} is the header's own first
 * row.
 *
 * <p>Class MonsterFlagTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class MonsterFlagTest {

    /**
     * One line of {@code list-mon-temp-flags.h}.
     */
    private record Row(String name, String description) {
    }

    /**
     * The eleven rows of {@code list-mon-temp-flags.h}, in the header's order. The index of a row
     * is the value C gives its {@code MFLAG_} constant.
     */
    private static final List<Row> C_ROWS = List.of(
            new Row("MFLAG_NONE", ""),
            new Row("MFLAG_VIEW", "Monster is in line of sight"),
            new Row("MFLAG_ACTIVE", "Monster is in active mode"),
            new Row("MFLAG_NICE", "Monster is still being nice"),
            new Row("MFLAG_SHOW", "Monster is recently memorized"),
            new Row("MFLAG_MARK", "Monster is currently memorized"),
            new Row("MFLAG_VISIBLE", "Monster is \"visible\""),
            new Row("MFLAG_CAMOUFLAGE", "Player doesn't know this is a monster"),
            new Row("MFLAG_AWARE", "Monster is aware of the player"),
            new Row("MFLAG_HANDLED", "Monster has been processed this turn"),
            new Row("MFLAG_TRACKING", "Monster is tracking the player by sound or scent"));

    /**
     * Reads the private description of a constant.
     *
     * @param flag the constant
     * @return its description
     * @throws Exception if the field cannot be reached
     */
    private static String description(MonsterFlag flag) throws Exception {
        Field field = MonsterFlag.class.getDeclaredField("description");
        field.setAccessible(true);
        return (String) field.get(flag);
    }

    /**
     * C has eleven rows and then {@code MFLAG_MAX}; Java has the same twelve constants in the same
     * order.
     *
     * <p>Function constantsFollowTheHeaderOrder coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("the eleven header rows in order, then MAX")
    void constantsFollowTheHeaderOrder() {
        MonsterFlag[] all = MonsterFlag.values();

        assertEquals(C_ROWS.size() + 1, all.length);
        for (int i = 0; i < C_ROWS.size(); i++) {
            assertEquals(C_ROWS.get(i).name(), all[i].name(), "row " + i);
        }
        assertEquals("MFLAG_MAX", all[C_ROWS.size()].name());
    }

    /**
     * An ordinal is C's index for the flag, since {@code NONE} leads in both; a different lead
     * would shift every flag by one, as it does for {@code MonTimed}.
     *
     * <p>Function ordinalsAreCsIndices coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("ordinals equal C's indices: NONE 0, VIEW 1, TRACKING 10, MAX 11")
    void ordinalsAreCsIndices() {
        assertEquals(0, MonsterFlag.MFLAG_NONE.ordinal());
        assertEquals(1, MonsterFlag.MFLAG_VIEW.ordinal());
        assertEquals(2, MonsterFlag.MFLAG_ACTIVE.ordinal());
        assertEquals(3, MonsterFlag.MFLAG_NICE.ordinal());
        assertEquals(4, MonsterFlag.MFLAG_SHOW.ordinal());
        assertEquals(5, MonsterFlag.MFLAG_MARK.ordinal());
        assertEquals(6, MonsterFlag.MFLAG_VISIBLE.ordinal());
        assertEquals(7, MonsterFlag.MFLAG_CAMOUFLAGE.ordinal());
        assertEquals(8, MonsterFlag.MFLAG_AWARE.ordinal());
        assertEquals(9, MonsterFlag.MFLAG_HANDLED.ordinal());
        assertEquals(10, MonsterFlag.MFLAG_TRACKING.ordinal());
        assertEquals(11, MonsterFlag.MFLAG_MAX.ordinal());
    }

    /**
     * Each description is the header's text for its row, including the escaped quotes of
     * {@code "Monster is \"visible\""}, which must reach Java as plain quotation marks.
     *
     * <p>Function descriptionsMatchTheHeader coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("every description is the header's text")
    void descriptionsMatchTheHeader() throws Exception {
        for (int i = 0; i < C_ROWS.size(); i++) {
            assertEquals(C_ROWS.get(i).description(), description(MonsterFlag.values()[i]),
                    C_ROWS.get(i).name());
        }
    }

    /**
     * The one description with embedded quotes, spelt out on its own so a regression to the
     * backslashes shows up by name.
     *
     * <p>Function visibleDescriptionKeepsItsQuotationMarks coded on 261014, commented in full on
     * 261014.
     */
    @Test
    @DisplayName("MFLAG_VISIBLE's description holds two quotation marks and no backslash")
    void visibleDescriptionKeepsItsQuotationMarks() throws Exception {
        String text = description(MonsterFlag.MFLAG_VISIBLE);

        assertEquals(2, text.chars().filter(c -> c == '"').count());
        assertFalse(text.contains("\\"));
    }

    /**
     * Only the two entries that are not real flags carry the empty string; every real flag has
     * text, and no two rows share it.
     *
     * <p>Function onlyNoneAndMaxHaveNoDescription coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("only NONE and MAX are empty, and the other ten descriptions are distinct")
    void onlyNoneAndMaxHaveNoDescription() throws Exception {
        for (MonsterFlag flag : MonsterFlag.values()) {
            boolean placeholder = flag == MonsterFlag.MFLAG_NONE || flag == MonsterFlag.MFLAG_MAX;
            assertEquals(placeholder, description(flag).isEmpty(), flag.name());
        }
        long distinct = Arrays.stream(MonsterFlag.values()).filter(f -> f.ordinal() > 0
                && f != MonsterFlag.MFLAG_MAX).map(f -> {
            try {
                return description(f);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }).distinct().count();
        assertEquals(10, distinct);
    }

    /**
     * The real flags are independent bits, as {@code mflag_on}, {@code mflag_off} and
     * {@code mflag_has} treat them: C's {@code process_player_cleanup} clears {@code NICE} and
     * {@code SHOW} and leaves {@code MARK} on a monster that is still {@code SHOW}n.
     *
     * <p>Function flagsSetAndClearIndependently coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("each flag sets and clears without touching the others")
    void flagsSetAndClearIndependently() {
        Flag<MonsterFlag> set = new Flag<>(MonsterFlag.class);

        for (Row row : C_ROWS.subList(1, C_ROWS.size())) {
            set.on(MonsterFlag.valueOf(row.name()));
        }
        assertEquals(10, set.count());

        set.off(MonsterFlag.MFLAG_NICE);
        set.off(MonsterFlag.MFLAG_SHOW);

        assertFalse(set.has(MonsterFlag.MFLAG_NICE));
        assertFalse(set.has(MonsterFlag.MFLAG_SHOW));
        assertTrue(set.has(MonsterFlag.MFLAG_MARK));
        assertTrue(set.has(MonsterFlag.MFLAG_VISIBLE));
        assertEquals(8, set.count());
    }

    /**
     * A new set holds nothing, in particular not {@code MFLAG_NONE}, which C never sets on a
     * monster.
     *
     * <p>Function aNewSetIsEmpty coded on 261014, commented in full on 261014.
     */
    @Test
    @DisplayName("a new Flag<MonsterFlag> is empty and does not hold NONE")
    void aNewSetIsEmpty() {
        Flag<MonsterFlag> set = new Flag<>(MonsterFlag.class);

        assertTrue(set.isEmpty());
        assertFalse(set.has(MonsterFlag.MFLAG_NONE));
    }
}
