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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.MonsterBaseReader;
import uk.co.jackoftradesltd.backend.parser.PainReader;
import uk.co.jackoftradesltd.backend.parser.SummonReader;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Summon}, the Java port of {@code struct summon} in {@code mon-summon.h}.
 *
 * <p>The class has no behaviour beyond storing what {@code parse_summon_*} and
 * {@code finish_parse_summon} in {@code mon-summon.c} leave in the struct, so the first group pins
 * the constructor, the late-bound fallback and {@code toString}. The second group loads the shipped
 * {@code summon.txt} and checks every record against a plain-text reading of the same file, so the
 * expected values come from the data file and the C rules, not from the assembler.
 *
 * <p>The assembler resolves {@code base:} lines against the monster bases, so {@link #seed()} loads
 * {@code pain.txt} and {@code monster_base.txt} and injects them into {@link MonsterRegistry} by
 * reflection, putting the originals back in {@link #restore()}.
 *
 * <p>Class SummonTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class SummonTest {

    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";

    private static Object savedPains;
    private static Object savedBases;

    @BeforeAll
    static void seed() throws Exception {
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();
        savedPains = setStatic("monsterPains", pains);
        List<MonsterBase> bases = new MonsterBaseReader().parseWithResults(BASE_FILE).items();
        savedBases = setStatic("monsterBases", bases);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = MonsterRegistry.class.getDeclaredField(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    private static Summon summon(String name, boolean uniques, List<MonsterBase> bases,
                                 MonsterRaceFlag flag, Summon fallback, String fallbackName) {
        return new Summon(name, MessageType.MSG_SUM_MONSTER, uniques, bases, flag, fallback,
                fallbackName, "a monster");
    }

    // ---- Constructor and accessors ---------------------------------------

    @Test
    @DisplayName("the constructor stores every field and the getters return them")
    void constructorRoundTrip() {
        List<MonsterBase> bases = List.of(new MonsterBase("zephyr hound"));
        Summon target = summon("HI_UNDEAD", false, List.of(), MonsterRaceFlag.RF_NONE, null, "");
        Summon s = new Summon("WRAITH", MessageType.MSG_SUM_UNDEAD, true, bases,
                MonsterRaceFlag.RF_UNDEAD, target, "HI_UNDEAD", "a wraith");

        assertEquals("WRAITH", s.getName());
        assertEquals(MessageType.MSG_SUM_UNDEAD, s.getMessageType());
        assertTrue(s.isUniquesAllowed());
        assertSame(bases, s.getBases());
        assertEquals(MonsterRaceFlag.RF_UNDEAD, s.getRaceFlag());
        assertSame(target, s.getFallback());
        assertEquals("HI_UNDEAD", s.getFallbackName());
        assertEquals("a wraith", s.getDescription());
    }

    @Test
    @DisplayName("setFallback links the reference without touching the raw name, and null clears it")
    void setFallbackLeavesTheNameAlone() {
        Summon target = summon("HI_UNDEAD", false, List.of(), MonsterRaceFlag.RF_NONE, null, "");
        Summon s = summon("UNIQUE", true, List.of(), MonsterRaceFlag.RF_UNIQUE, null, "HI_UNDEAD");

        assertNull(s.getFallback());
        s.setFallback(target);
        assertSame(target, s.getFallback());
        assertEquals("HI_UNDEAD", s.getFallbackName());

        s.setFallback(null);
        assertNull(s.getFallback());
        assertEquals("HI_UNDEAD", s.getFallbackName());
    }

    // ---- toString --------------------------------------------------------

    @Test
    @DisplayName("toString shows 'unique' and 'has bases' only when they apply")
    void toStringOptionalParts() {
        Summon plain = summon("MONSTER", false, List.of(), MonsterRaceFlag.RF_NONE, null, "");
        assertEquals("MONSTER MSG_SUM_MONSTER " + MonsterRaceFlag.RF_NONE + "  a monster",
                plain.toString());

        Summon full = summon("HOUND", true, List.of(new MonsterBase("canine")),
                MonsterRaceFlag.RF_NONE, null, "");
        assertEquals("HOUND MSG_SUM_MONSTER unique has bases " + MonsterRaceFlag.RF_NONE + "  a monster",
                full.toString());
    }

    @Test
    @DisplayName("toString takes the fallback's name from the resolved reference, not the raw name")
    void toStringFallbackComesFromTheReference() {
        Summon target = summon("HI_UNDEAD", false, List.of(), MonsterRaceFlag.RF_NONE, null, "");
        Summon unresolved = summon("UNIQUE", true, List.of(), MonsterRaceFlag.RF_UNIQUE, null, "HI_UNDEAD");
        Summon resolved = summon("UNIQUE", true, List.of(), MonsterRaceFlag.RF_UNIQUE, target, "HI_UNDEAD");

        assertFalse(unresolved.toString().contains("HI_UNDEAD"));
        assertTrue(resolved.toString().contains(" HI_UNDEAD "));
    }

    // ---- The shipped summon.txt ------------------------------------------

    /**
     * One record read straight from the text of {@code summon.txt}, with the C defaults applied:
     * {@code unique_allowed} false, no bases, {@code race_flag} none, no fallback.
     */
    private static final class Expected {
        String name;
        String msgt = "";
        boolean uniques;
        int bases;
        String raceFlag = "";
        String fallback = "";
        String desc = "";
    }

    private static List<Expected> readShippedFile() throws IOException {
        List<Expected> records = new ArrayList<>();
        Expected cur = null;
        for (String line : Files.readAllLines(Path.of(SUMMON_FILE))) {
            if (line.isBlank() || line.startsWith("#")) continue;
            int colon = line.indexOf(':');
            if (colon < 0) continue;
            String key = line.substring(0, colon);
            String value = line.substring(colon + 1);
            if (key.equals("name")) {
                cur = new Expected();
                cur.name = value;
                records.add(cur);
            } else if (cur == null) {
                continue;
            } else switch (key) {
                case "msgt" -> cur.msgt = value;
                // C: any non-zero integer sets unique_allowed.
                case "uniques" -> cur.uniques = Integer.parseInt(value.trim()) != 0;
                case "base" -> cur.bases++;
                case "race-flag" -> cur.raceFlag = value;
                case "fallback" -> cur.fallback = value;
                case "desc" -> cur.desc = value;
                default -> { }
            }
        }
        return records;
    }

    @Test
    @DisplayName("every record in the shipped summon.txt loads, in file order, with the C field values")
    void shippedFileMatchesTheDataFile() throws IOException {
        List<Expected> expected = readShippedFile();
        List<Summon> loaded = new SummonReader().parseWithResults(SUMMON_FILE).items();

        assertEquals(17, expected.size());
        assertEquals(expected.size(), loaded.size());
        for (int i = 0; i < expected.size(); i++) {
            Expected e = expected.get(i);
            Summon s = loaded.get(i);
            assertEquals(e.name, s.getName(), "name at " + i);
            assertEquals(MessageType.valueOf("MSG_" + e.msgt), s.getMessageType(), e.name + " msgt");
            assertEquals(e.uniques, s.isUniquesAllowed(), e.name + " uniques");
            assertEquals(e.bases, s.getBases().size(), e.name + " base count");
            MonsterRaceFlag flag = e.raceFlag.isEmpty()
                    ? MonsterRaceFlag.RF_NONE : MonsterRaceFlag.valueOf("RF_" + e.raceFlag);
            assertEquals(flag, s.getRaceFlag(), e.name + " race-flag");
            assertEquals(e.fallback, s.getFallbackName(), e.name + " fallback name");
            assertEquals(e.desc, s.getDescription(), e.name + " desc");
        }
    }

    @Test
    @DisplayName("a fallback name resolves to the record of that name, and no name leaves null")
    void shippedFallbacksResolveByName() throws IOException {
        List<Summon> loaded = new SummonReader().parseWithResults(SUMMON_FILE).items();
        for (Summon s : loaded) {
            if (s.getFallbackName().isEmpty()) {
                assertNull(s.getFallback(), s.getName());
            } else {
                Summon target = loaded.stream()
                        .filter(t -> t.getName().equals(s.getFallbackName())).findFirst().orElseThrow();
                assertSame(target, s.getFallback(), s.getName());
            }
        }
    }

    @Test
    @DisplayName("ANY is the first record and allows uniques with no restriction")
    void anyIsFirstAndUnrestricted() throws IOException {
        Summon any = new SummonReader().parseWithResults(SUMMON_FILE).items().get(0);

        assertEquals("ANY", any.getName());
        assertTrue(any.isUniquesAllowed());
        assertTrue(any.getBases().isEmpty());
        assertEquals(MonsterRaceFlag.RF_NONE, any.getRaceFlag());
        assertNull(any.getFallback());
    }
}
