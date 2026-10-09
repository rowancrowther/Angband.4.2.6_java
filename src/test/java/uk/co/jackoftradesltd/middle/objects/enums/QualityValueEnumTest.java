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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link QualityValueEnum} - the quality bands of ignoring, the port of the
 * anonymous {@code IGNORE_*} enum in {@code obj-ignore.h}.
 *
 * <p>The <b>declaration order</b> is the thing to defend. C compares the bands as integers
 * ({@code ignore_level_of(obj) <= ignore_level[type]}), writes the setting to the savefile as a byte,
 * indexes {@code quality_values} by it and sets it from a menu cursor position, so the position of
 * each constant is its meaning. The tests below spell out the values C's header gives, then run the
 * ignore comparison over every pairing of band and setting.
 *
 * <p>Class QualityValueEnumTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class QualityValueEnumTest {

    /**
     * C has five bands, then {@code IGNORE_MAX}, giving six constants. Unlike most of this port's
     * enums, C's own list already begins with a {@code NONE} and ends with a {@code MAX}, so
     * nothing here is an extra.
     */
    @Test
    void theListIsCsFiveBandsPlusItsMaxSentinel() {
        assertEquals(6, QualityValueEnum.values().length);
    }

    /**
     * The values {@code obj-ignore.h} gives: 0 to 4 for the bands, 5 for {@code IGNORE_MAX}. These
     * are the numbers a savefile byte carries, so they are typed out rather than read from the enum.
     */
    @Test
    void ordinalsAreTheValuesCsHeaderGives() {
        assertEquals(0, QualityValueEnum.IGNORE_NONE.ordinal());
        assertEquals(1, QualityValueEnum.IGNORE_BAD.ordinal());
        assertEquals(2, QualityValueEnum.IGNORE_AVERAGE.ordinal());
        assertEquals(3, QualityValueEnum.IGNORE_GOOD.ordinal());
        assertEquals(4, QualityValueEnum.IGNORE_ALL.ordinal());
        assertEquals(5, QualityValueEnum.IGNORE_MAX.ordinal());
    }

    /**
     * {@code IGNORE_MAX} is the count: it equals the number of real bands, as {@code quality_values}
     * is sized by it.
     */
    @Test
    void maxIsTheCountOfRealBands() {
        assertEquals(QualityValueEnum.values().length - 1, QualityValueEnum.IGNORE_MAX.ordinal());
    }

    /**
     * The menu in {@code ui-options.c} stores {@code menu.cursor} straight into
     * {@code ignore_level[]}, and offers just {@code IGNORE_BAD + 1} rows for rings and amulets. So
     * row 0 must be none and row 1 bad, whatever comes after.
     */
    @Test
    void menuRowsMapToBands() {
        QualityValueEnum[] byRow = QualityValueEnum.values();
        assertEquals(QualityValueEnum.IGNORE_NONE, byRow[0]);
        assertEquals(QualityValueEnum.IGNORE_BAD, byRow[1]);
        assertEquals(QualityValueEnum.IGNORE_AVERAGE, byRow[2]);
        assertEquals(QualityValueEnum.IGNORE_GOOD, byRow[3]);
        assertEquals(QualityValueEnum.IGNORE_ALL, byRow[4]);
    }

    /**
     * Runs the test C's {@code ignore_item_ok} ends with, {@code ignore_level_of(obj) <=
     * ignore_level[type]}, over every band an item can report (bad to max) against every setting
     * (none to all). An item is ignored exactly when its band is not above the setting; a setting of
     * none ignores nothing, and an item reporting {@code IGNORE_MAX} is never ignored by any setting.
     */
    @Test
    void ordinalComparisonIgnoresAnItemOnlyWhenItsBandIsNotAboveTheSetting() {
        QualityValueEnum[] reported = {QualityValueEnum.IGNORE_BAD, QualityValueEnum.IGNORE_AVERAGE,
                QualityValueEnum.IGNORE_GOOD, QualityValueEnum.IGNORE_ALL, QualityValueEnum.IGNORE_MAX};
        QualityValueEnum[] settings = {QualityValueEnum.IGNORE_NONE, QualityValueEnum.IGNORE_BAD,
                QualityValueEnum.IGNORE_AVERAGE, QualityValueEnum.IGNORE_GOOD, QualityValueEnum.IGNORE_ALL};

        // Expected results typed from the integer comparison in C: bands 1..5 against settings 0..4.
        boolean[][] expected = {
                // setting:  NONE   BAD    AVERAGE GOOD   ALL
                /* BAD     */ {false, true, true, true, true},
                /* AVERAGE */ {false, false, true, true, true},
                /* GOOD    */ {false, false, false, true, true},
                /* ALL     */ {false, false, false, false, true},
                /* MAX     */ {false, false, false, false, false},
        };

        for (int i = 0; i < reported.length; i++) {
            for (int j = 0; j < settings.length; j++) {
                boolean ignored = reported[i].ordinal() <= settings[j].ordinal();
                assertEquals(expected[i][j], ignored,
                        "band " + reported[i] + " against setting " + settings[j]);
            }
        }
    }

    /** {@code IGNORE_MAX} sits above every setting, so it can never be reached by one. */
    @Test
    void maxIsAboveEverySetting() {
        for (QualityValueEnum setting : QualityValueEnum.values()) {
            if (setting == QualityValueEnum.IGNORE_MAX) continue;
            assertTrue(QualityValueEnum.IGNORE_MAX.ordinal() > setting.ordinal(),
                    "IGNORE_MAX not above " + setting);
        }
    }
}
