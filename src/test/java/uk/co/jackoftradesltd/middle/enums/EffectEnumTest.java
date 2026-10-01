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

package uk.co.jackoftradesltd.middle.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests {@link EffectEnum} against the C original's {@code list-effects.h} and {@code effects.c}.
 *
 * <p>The row data (aim, info label, argument count, info category, description, menu format) is
 * read from the C header itself, so the expected values never pass through the port. The sub-type
 * column is not in the header; its expected values are transcribed from the {@code switch} in
 * {@code effect_subtype()} in {@code effects.c}. The whole-table comparison is skipped when the C
 * tree is not on this machine; the hand-written cases below it always run.
 *
 * <p>Class EffectEnumTest coded on 261001, commented in full on 261001.
 */
class EffectEnumTest {

    private static final Path C_HEADER = Path.of("/home/rowan/Desktop/Angband-4.2.6/src/list-effects.h");

    /**
     * One {@code EFFECT(...)} row; the odd newline in the GRANITE row is flattened before matching.
     */
    private static final Pattern ROW = Pattern.compile(
            "EFFECT\\((\\w+),\\s*(true|false),\\s*(NULL|\"[^\"]*\"),\\s*(\\d+),\\s*(\\w+),\\s*"
                    + "\"((?:[^\"\\\\]|\\\\.)*)\",\\s*\"((?:[^\"\\\\]|\\\\.)*)\"\\)");

    @Test
    @DisplayName("every list-effects.h row matches its Java constant on all six columns")
    void everyCRowMatches() throws IOException {
        assumeTrue(Files.exists(C_HEADER), "C source tree not present");
        String text = Files.readString(C_HEADER).replaceAll("NULL,\\s*\\n\\s*0", "NULL, 0");
        Matcher m = ROW.matcher(text);
        int rows = 0;
        while (m.find()) {
            rows++;
            EffectEnum e = EffectEnum.valueOf("EF_" + m.group(1));
            String label = m.group(3).equals("NULL") ? "" : m.group(3).substring(1, m.group(3).length() - 1);
            assertEquals(Boolean.parseBoolean(m.group(2)), e.getAim(), e + " aim");
            assertEquals(label, e.getInfoLabel(), e + " info label");
            assertEquals(Integer.parseInt(m.group(4)), e.getNumberOfArguments(), e + " args");
            assertEquals(EffectInfoEnum.valueOf(m.group(5)), e.getEffectInfo(), e + " info flags");
            assertEquals(m.group(6), e.getDescription(), e + " description");
            assertEquals(m.group(7), e.getMenuFormat(), e + " menu format");
        }
        assertEquals(112, rows, "rows parsed from list-effects.h");
    }

    @Test
    @DisplayName("order is EF_NONE, the 112 header rows in order, then EF_MAX")
    void ordering() {
        EffectEnum[] all = EffectEnum.values();
        assertEquals(114, all.length);
        assertEquals(EffectEnum.EF_NONE, all[0]);
        assertEquals(EffectEnum.EF_RANDOM, all[1]);
        assertEquals(EffectEnum.EF_UNSCRAMBLE_STATS, all[112]);
        assertEquals(EffectEnum.EF_MAX, all[113]);
    }

    @Test
    @DisplayName("the parenthesised descriptions keep their brackets")
    void parenthesisedDescriptions() {
        assertEquals("%s for %s turns (%s percent)", EffectEnum.EF_NOURISH.getDescription());
        assertEquals("extends %s for %s turns (unresistable)", EffectEnum.EF_TIMED_INC_NO_RES.getDescription());
    }

    @Test
    @DisplayName("NONE and MAX are blank, unaimed, argument-free")
    void sentinels() {
        for (EffectEnum e : new EffectEnum[]{EffectEnum.EF_NONE, EffectEnum.EF_MAX}) {
            assertEquals(false, e.getAim());
            assertEquals("", e.getInfoLabel());
            assertEquals(0, e.getNumberOfArguments());
            assertEquals(EffectInfoEnum.EFINFO_NONE, e.getEffectInfo());
            assertEquals("", e.getDescription());
            assertEquals("", e.getMenuFormat());
            assertEquals(EffectSubTypeEnum.EST_NONE, e.getSubType());
        }
    }

    @Test
    @DisplayName("sub-types follow the switch in effect_subtype(); everything unlisted is EST_NONE")
    void subTypes() {
        Map<EffectEnum, EffectSubTypeEnum> expected = new EnumMap<>(EffectEnum.class);
        // "Projection name" arm
        for (EffectEnum e : new EffectEnum[]{
                EffectEnum.EF_PROJECT_LOS, EffectEnum.EF_PROJECT_LOS_AWARE, EffectEnum.EF_DESTRUCTION,
                EffectEnum.EF_SPOT, EffectEnum.EF_SPHERE, EffectEnum.EF_BALL, EffectEnum.EF_BREATH,
                EffectEnum.EF_ARC, EffectEnum.EF_SHORT_BEAM, EffectEnum.EF_LASH, EffectEnum.EF_SWARM,
                EffectEnum.EF_STRIKE, EffectEnum.EF_STAR, EffectEnum.EF_STAR_BALL, EffectEnum.EF_BOLT,
                EffectEnum.EF_BEAM, EffectEnum.EF_BOLT_OR_BEAM, EffectEnum.EF_LINE, EffectEnum.EF_ALTER,
                EffectEnum.EF_BOLT_STATUS, EffectEnum.EF_BOLT_STATUS_DAM, EffectEnum.EF_BOLT_AWARE,
                EffectEnum.EF_MELEE_BLOWS, EffectEnum.EF_TOUCH, EffectEnum.EF_TOUCH_AWARE}) {
            expected.put(e, EffectSubTypeEnum.EST_PROJ);
        }
        // "Timed effect name" arm
        for (EffectEnum e : new EffectEnum[]{
                EffectEnum.EF_CURE, EffectEnum.EF_TIMED_SET, EffectEnum.EF_TIMED_INC,
                EffectEnum.EF_TIMED_INC_NO_RES, EffectEnum.EF_TIMED_DEC}) {
            expected.put(e, EffectSubTypeEnum.EST_TMD);
        }
        // "Stat name" arm
        for (EffectEnum e : new EffectEnum[]{
                EffectEnum.EF_RESTORE_STAT, EffectEnum.EF_DRAIN_STAT, EffectEnum.EF_LOSE_RANDOM_STAT,
                EffectEnum.EF_GAIN_STAT}) {
            expected.put(e, EffectSubTypeEnum.EST_STAT);
        }
        expected.put(EffectEnum.EF_NOURISH, EffectSubTypeEnum.EST_NOURISH);
        expected.put(EffectEnum.EF_MON_TIMED_INC, EffectSubTypeEnum.EST_MON_TMD);
        expected.put(EffectEnum.EF_SUMMON, EffectSubTypeEnum.EST_SUMMON);
        expected.put(EffectEnum.EF_ENCHANT, EffectSubTypeEnum.EST_ENCHANT);
        expected.put(EffectEnum.EF_SHAPECHANGE, EffectSubTypeEnum.EST_SHAPECHANGE);
        expected.put(EffectEnum.EF_EARTHQUAKE, EffectSubTypeEnum.EST_EARTHQUAKE);
        expected.put(EffectEnum.EF_GLYPH, EffectSubTypeEnum.EST_GLYPH);
        expected.put(EffectEnum.EF_TELEPORT, EffectSubTypeEnum.EST_TELEPORT);
        expected.put(EffectEnum.EF_TELEPORT_TO, EffectSubTypeEnum.EST_TELEPORT_TO);

        for (EffectEnum e : EffectEnum.values()) {
            assertEquals(expected.getOrDefault(e, EffectSubTypeEnum.EST_NONE), e.getSubType(), e + " sub-type");
        }
    }

    @Test
    @DisplayName("22 effects require aiming, as in list-effects.h")
    void aimedSet() {
        int aimed = 0;
        for (EffectEnum e : EffectEnum.values()) {
            if (e.getAim()) aimed++;
        }
        // C rows with aim == true: BALL..STRIKE (7), BOLT..BOLT_AWARE (8), CURSE, COMMAND,
        // MOVE_ATTACK, SINGLE_COMBAT, MELEE_BLOWS, BIZARRE, WONDER (7) = 22
        assertEquals(22, aimed);
    }
}
