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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the three {@link PlayerRace} accessors that {@link Player#learnInnate()} drives — the two
 * knowledge questions and the display name.
 *
 * <p>The knowledge pair is where this port's representation departs furthest from C's. C reads a
 * full {@code el_info[ELEM_MAX]} array in which an unmentioned element is simply zero, and walks
 * {@code p->race->flags} with {@code of_next} over the bits that are set. Here the resists are a
 * <em>sparse</em> {@link Map} — {@code PlayerRaceAssembler} creates an entry only for a
 * {@code RES_} line actually present in {@code p_race.txt} — and the flags answer one question at a
 * time. Both differences are invisible for the races the game ships, and both are exactly the kind
 * of thing that breaks on a modded data file, so they are pinned here rather than left to the
 * caller's tests.
 *
 * @author Rowan Crowther
 */
class PlayerRaceKnowledgeTest {

    /**
     * Builds a race carrying nothing but the two things under test. Everything a race needs for
     * character generation is left null or empty: none of it is reachable from these accessors, and
     * naming it would only obscure which field the test is about.
     *
     * @param resists the element map, sparse as the assembler builds it
     * @param flags   the innate object flags
     * @return a race with those and nothing else
     * @author Rowan Crowther
     */
    private static PlayerRace race(Map<ElementEnum, ElementInfo> resists, Flag<ObjectFlag> flags) {
        return new PlayerRace("Half-Troll", 6, 12, 120, 20, 10, 96, 10, 255, 60, 3, null,
                Map.<Stats, Integer>of(), Map.<PlayerSkill, Integer>of(), flags,
                new Flag<>(PlayerFlag.class), null, resists);
    }

    /**
     * An element map with one entry, as the assembler would build it for a single {@code RES_} line.
     *
     * @param element the element the race's data mentions
     * @param level   the resistance level given
     * @return a map holding that one entry
     * @author Rowan Crowther
     */
    private static Map<ElementEnum, ElementInfo> resistMap(ElementEnum element, int level) {
        Map<ElementEnum, ElementInfo> map = new HashMap<>();
        ElementInfo info = new ElementInfo();
        info.setResLevel(level);
        map.put(element, info);

        return map;
    }

    private static Flag<ObjectFlag> flags(ObjectFlag... set) {
        Flag<ObjectFlag> flag = new Flag<>(ObjectFlag.class);
        for (ObjectFlag f : set) {
            flag.on(f);
        }

        return flag;
    }

    /**
     * @author Rowan Crowther
     */
    @Test
    @DisplayName("getName returns the race's display name")
    void name() {
        assertEquals("Half-Troll", race(Map.of(), flags()).getName());
    }

    /**
     * The element side, whose contract is "non-zero", not "positive" and not "mentioned".
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("getResistKnowledge")
    class Resists {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a resisted element is known")
        void resisted() {
            PlayerRace kobold = race(resistMap(ElementEnum.ELEM_POIS, 1), flags());

            assertTrue(kobold.getResistKnowledge(ElementEnum.ELEM_POIS));
        }

        /**
         * C tests {@code res_level != 0}, so a vulnerability is as learnable as a resistance — the
         * rune names the element, not the sign. A {@code > 0} test would pass every other case here
         * and quietly leave a vulnerable race unable to read a rune it plainly knows.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a vulnerability is known just as a resistance is")
        void vulnerability() {
            PlayerRace burns = race(resistMap(ElementEnum.ELEM_FIRE, -1), flags());

            assertTrue(burns.getResistKnowledge(ElementEnum.ELEM_FIRE));
        }

        /**
         * An entry present but zero is C's "mentioned, but no stake in it". Testing for the key
         * rather than the value would get this one wrong.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an entry of zero is not knowledge")
        void explicitZero() {
            PlayerRace indifferent = race(resistMap(ElementEnum.ELEM_ACID, 0), flags());

            assertFalse(indifferent.getResistKnowledge(ElementEnum.ELEM_ACID));
        }

        /**
         * The sparse-map case, and the one C does not have. Absent has to answer the same as zero,
         * because C's array gives every element a value whether the data file mentioned it or not.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("an element the data never mentions is not knowledge")
        void absentElement() {
            PlayerRace kobold = race(resistMap(ElementEnum.ELEM_POIS, 1), flags());

            assertFalse(kobold.getResistKnowledge(ElementEnum.ELEM_FIRE));
        }

        /**
         * Most races resist nothing at all, so an empty map is the common case rather than an edge
         * one — and reading straight through a missing entry would throw on the first element
         * {@link Player#learnInnate()} asks about.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a race with no resists at all answers rather than throws")
        void emptyMap() {
            PlayerRace human = race(new HashMap<>(), flags());

            assertAll(
                    () -> assertDoesNotThrow(() -> human.getResistKnowledge(ElementEnum.ELEM_ACID)),
                    () -> assertFalse(human.getResistKnowledge(ElementEnum.ELEM_ACID)));
        }

        /**
         * Every element, for a race that mentions one — the loop
         * {@link Player#learnInnate()} actually runs.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("answers for every element without throwing")
        void everyElement() {
            PlayerRace elf = race(resistMap(ElementEnum.ELEM_LIGHT, 1), flags());

            for (ElementEnum element : ElementEnum.values()) {
                assertDoesNotThrow(() -> elf.getResistKnowledge(element), element.name());
            }
        }
    }

    /**
     * The flag side, which is a plain membership test — the interest is in what it says about the
     * flags a race does not have, since the caller asks about all of them.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("getObjectFlagKnowledge")
    class Flags {

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a conferred flag is known")
        void conferred() {
            PlayerRace dwarf = race(Map.of(), flags(ObjectFlag.OF_PROT_BLIND));

            assertTrue(dwarf.getObjectFlagKnowledge(ObjectFlag.OF_PROT_BLIND));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a flag the race lacks is not known")
        void notConferred() {
            PlayerRace dwarf = race(Map.of(), flags(ObjectFlag.OF_PROT_BLIND));

            assertFalse(dwarf.getObjectFlagKnowledge(ObjectFlag.OF_FEATHER));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("a race with no innate flags knows none")
        void none() {
            PlayerRace human = race(Map.of(), flags());

            assertFalse(human.getObjectFlagKnowledge(ObjectFlag.OF_FEATHER));
        }

        /**
         * The sentinels are not flags. C never reaches them, because {@code of_next} starts at
         * {@code FLAG_START} and stops at {@code FLAG_END}; this port's caller walks the whole enum,
         * so they have to answer false rather than be excluded by the loop.
         *
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("the enum sentinels are not conferred")
        void sentinels() {
            PlayerRace dwarf = race(Map.of(), flags(ObjectFlag.OF_PROT_BLIND));

            assertAll(
                    () -> assertFalse(dwarf.getObjectFlagKnowledge(ObjectFlag.OF_NONE)),
                    () -> assertFalse(dwarf.getObjectFlagKnowledge(ObjectFlag.OF_MAX)));
        }

        /**
         * @author Rowan Crowther
         */
        @Test
        @DisplayName("several flags are all known")
        void several() {
            PlayerRace hybrid = race(Map.of(),
                    flags(ObjectFlag.OF_PROT_BLIND, ObjectFlag.OF_FEATHER, ObjectFlag.OF_SUST_STR));

            assertAll(
                    () -> assertTrue(hybrid.getObjectFlagKnowledge(ObjectFlag.OF_PROT_BLIND)),
                    () -> assertTrue(hybrid.getObjectFlagKnowledge(ObjectFlag.OF_FEATHER)),
                    () -> assertTrue(hybrid.getObjectFlagKnowledge(ObjectFlag.OF_SUST_STR)),
                    () -> assertFalse(hybrid.getObjectFlagKnowledge(ObjectFlag.OF_FRAGILE)));
        }
    }
}