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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.*;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.middle.monsters.*;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the lookups and counters of {@link MonsterRegistry}, added on 261009.
 *
 * <p>The expectations come from the C each piece ports, not from the Java:
 *
 * <ul>
 *   <li>{@code lookup_monster} ({@code mon-util.c}) skips a record with no name, returns the first
 *       case-insensitive exact match at once, and otherwise returns the first record whose name
 *       contains the query ({@code my_stristr}, {@code z-util.c}). {@code my_stristr} never matches
 *       an empty pattern, so the empty query finds nothing.</li>
 *   <li>{@code lookup_monster_base} ({@code mon-util.c}) takes the first {@code streq} match.</li>
 *   <li>{@code findmeth} and {@code findeff} ({@code mon-init.c}) take the first {@code streq}
 *       match and return {@code NULL} at the end of the list.</li>
 *   <li>{@code summon_name_to_idx} ({@code mon-summon.c}) takes the first {@code streq} match.</li>
 *   <li>{@code parse_mon_base_pain} ({@code mon-init.c}) reads {@code pain_messages[pain_idx]}, and
 *       {@code pain.txt} ships the twelve indices 1 to 12.</li>
 *   <li>{@code finish_parse_monster} ({@code mon-init.c}) takes {@code mon_blows_max} as the longest
 *       blow list of any race.</li>
 * </ul>
 *
 * <p>The counters are the accepted deviation recorded on 261009. Each C counter ({@code r_max},
 * {@code mp_max}, {@code pit_max}, {@code blow_methods_max}, {@code blow_effects_max}) is one more than
 * the record count because C keeps a blank slot, and the Java counters equal the record count. These
 * tests pin the Java value, so a change of mind about the deviation shows up here.
 *
 * <p>The registry is global static state shared with the other suites, so every field these tests
 * touch is saved before each test and put back after it.
 *
 * <p>Class MonsterRegistryTest coded on 261009, commented in full on 261009.
 */
class MonsterRegistryTest {

    /**
     * Every registry field these tests assign, by name.
     */
    private static final List<String> FIELDS = List.of(
            "monsterRaces", "monsterRaceMax", "monsterPainMsgMax", "monsterPitTypeMax", "monsterBlowsMax",
            "monsterBlowsMethodsMax", "monsterBlowsEffectsMax", "monsterPains", "monsterBases", "summons",
            "blowMethods", "blowEffects", "monsterPitProfiles");

    /**
     * The values the registry held before each test, by field name.
     */
    private final Map<String, Object> saved = new HashMap<>();

    /**
     * Reaches a registry field.
     *
     * @param name the field name
     * @return the field, made accessible
     * @throws Exception if there is no such field
     */
    private static Field field(String name) throws Exception {
        Field f = MonsterRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * Saves the registry fields and empties them, so each test states its own contents.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void saveAndClear() throws Exception {
        for (String name : FIELDS) {
            Field f = field(name);
            saved.put(name, f.get(null));
            f.set(null, f.getType() == int.class ? (Object) 0 : null);
        }
    }

    /**
     * Puts the registry back.
     *
     * @throws Exception if a field cannot be reached
     */
    @AfterEach
    void restore() throws Exception {
        for (String name : FIELDS)
            field(name).set(null, saved.get(name));
    }

    /**
     * A race with a name and a number of blows, and nothing else worth speaking of.
     *
     * @param name  the race's name
     * @param blows how many blows it has
     * @return the race
     */
    private static MonsterRace race(String name, int blows) {
        List<MonsterBlow> list = new ArrayList<>();
        for (int i = 0; i < blows; i++)
            list.add(new MonsterBlow(null, null, null));
        return new MonsterRace(name, "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                null, null, list, 0, 0, null, null,
                List.of(), List.of(), List.of(), List.of(), List.of(), 0, null);
    }

    /**
     * A race with a name and no blows.
     *
     * @param name the race's name
     * @return the race
     */
    private static MonsterRace race(String name) {
        return race(name, 0);
    }

    /**
     * Loads the races through the setter, as the loader does.
     *
     * @param races the races to load
     */
    private static void loadRaces(MonsterRace... races) {
        MonsterRegistry.setMonsterRaces(new ArrayList<>(Arrays.asList(races)));
    }

    /**
     * Pain records numbered 1 to {@code count}, as {@code pain.txt} numbers its twelve.
     *
     * @param count how many records
     * @return the records, in file order
     */
    private static List<MonsterPain> pains(int count) {
        List<MonsterPain> list = new ArrayList<>();
        for (int i = 1; i <= count; i++)
            list.add(new MonsterPain(i, List.of("a", "b", "c", "d", "e", "f", "g")));
        return list;
    }

    /**
     * {@code lookup_monster}.
     */
    @Nested
    @DisplayName("lookupMonsterRace")
    class LookupRace {

        /**
         * The loaded list, with the {@code <player>} record at element 0 as in {@code r_info[0]}.
         */
        @BeforeEach
        void load() {
            loadRaces(race("<player>"), race("filthy street urchin"), race("scrawny cat"),
                    race("Grip, Farmer Maggot's Dog"), race("Fang, Farmer Maggot's Dog"));
        }

        @Test
        @DisplayName("an exact name is found whatever its case")
        void exactAnyCase() {
            assertEquals("scrawny cat", MonsterRegistry.lookupMonsterRace("SCRAWNY CAT").getName());
            assertEquals("scrawny cat", MonsterRegistry.lookupMonsterRace("scrawny cat").getName());
        }

        @Test
        @DisplayName("a substring finds the first race containing it")
        void substringFirst() {
            assertEquals("Grip, Farmer Maggot's Dog", MonsterRegistry.lookupMonsterRace("farmer maggot").getName());
            assertEquals("filthy street urchin", MonsterRegistry.lookupMonsterRace("STREET").getName());
        }

        @Test
        @DisplayName("an exact match later in the list beats an earlier substring match")
        void exactBeatsEarlierSubstring() {
            loadRaces(race("<player>"), race("cat lord"), race("cat"));

            assertEquals("cat", MonsterRegistry.lookupMonsterRace("cat").getName());
        }

        @Test
        @DisplayName("the <player> record at element 0 is searched, as r_info[0] is")
        void playerRecordSearched() {
            assertEquals("<player>", MonsterRegistry.lookupMonsterRace("player").getName());
            assertEquals("<player>", MonsterRegistry.lookupMonsterRace("<PLAYER>").getName());
        }

        @Test
        @DisplayName("the empty query finds nothing, as my_stristr never matches an empty pattern")
        void emptyQuery() {
            assertNull(MonsterRegistry.lookupMonsterRace(""));
        }

        @Test
        @DisplayName("a query matching no race finds nothing")
        void noMatch() {
            assertNull(MonsterRegistry.lookupMonsterRace("zzz"));
        }

        @Test
        @DisplayName("a space is an ordinary character, so it matches the first name containing one")
        void spaceQuery() {
            assertEquals("filthy street urchin", MonsterRegistry.lookupMonsterRace(" ").getName());
        }

        @Test
        @DisplayName("a race with no name is skipped, as C skips a NULL name")
        void nullNameSkipped() throws Exception {
            // Seeded directly: the no-argument race has no blow list, which setMonsterRaces would trip over.
            field("monsterRaces").set(null, new ArrayList<>(Arrays.asList(new MonsterRace(), race("scrawny cat"))));

            assertEquals("scrawny cat", MonsterRegistry.lookupMonsterRace("cat").getName());
            assertNull(MonsterRegistry.lookupMonsterRace("zzz"));
        }

        @Test
        @DisplayName("a null element in the list is skipped")
        void nullElementSkipped() throws Exception {
            // Seeded directly: setMonsterRaces reads every element's blow list, so a null element would trip it.
            field("monsterRaces").set(null,
                    new ArrayList<>(Arrays.asList(race("<player>"), null, race("scrawny cat"))));

            assertEquals("scrawny cat", MonsterRegistry.lookupMonsterRace("cat").getName());
        }

        @Test
        @DisplayName("the race returned is the list's own record")
        void sameRecord() {
            assertSame(MonsterRegistry.getMonsterRaces().get(2), MonsterRegistry.lookupMonsterRace("scrawny cat"));
        }

        @Test
        @DisplayName("an unloaded list throws, where C has only a NULL r_info")
        void unloaded() throws Exception {
            field("monsterRaces").set(null, null);

            assertThrows(IllegalStateException.class, () -> MonsterRegistry.lookupMonsterRace("cat"));
        }
    }

    /**
     * The race list and its counters.
     */
    @Nested
    @DisplayName("races and counters")
    class RaceCounters {

        @Test
        @DisplayName("the race count is the list size, one below C's r_max (accepted deviation)")
        void raceMax() {
            loadRaces(race("<player>"), race("a"), race("b"), race("c"), race("d"));

            assertEquals(5, MonsterRegistry.getMonsterRaceMax());
            assertEquals(5, MonsterRegistry.monsterRaceMax);
        }

        @Test
        @DisplayName("the blow maximum is the longest blow list of any race")
        void blowsMax() {
            loadRaces(race("<player>", 0), race("a", 2), race("b", 4), race("c", 1));

            assertEquals(4, MonsterRegistry.getMonsterBlowsMax());
        }

        @Test
        @DisplayName("no races gives a zero blow maximum")
        void blowsMaxEmpty() {
            loadRaces();

            assertEquals(0, MonsterRegistry.getMonsterBlowsMax());
            assertEquals(0, MonsterRegistry.getMonsterRaceMax());
        }

        @Test
        @DisplayName("getMonsterRaces is a read-only view whose element 0 is the first race loaded")
        void racesView() {
            loadRaces(race("<player>"), race("a"));

            assertEquals("<player>", MonsterRegistry.getMonsterRaces().get(0).getName());
            assertThrows(UnsupportedOperationException.class,
                    () -> MonsterRegistry.getMonsterRaces().add(race("b")));
        }

        @Test
        @DisplayName("getMonsterRaces before loading throws NullPointerException")
        void racesViewUnloaded() {
            assertThrows(NullPointerException.class, MonsterRegistry::getMonsterRaces);
        }
    }

    /**
     * Pain records, indexed 1 to 12 in {@code pain.txt}.
     */
    @Nested
    @DisplayName("pain records")
    class Pain {

        @Test
        @DisplayName("every shipped index 1 to 12 is found, whichever lookup is used")
        void shippedIndices() {
            MonsterRegistry.setMonsterPains(pains(12));

            for (int i = 1; i <= 12; i++) {
                assertEquals(i, MonsterRegistry.lookupMonsterPain(i).getPainIndex());
                assertEquals(i, MonsterRegistry.getPainFromIndex(i).getPainIndex());
            }
        }

        @Test
        @DisplayName("the count is 12, one below C's mp_max of 13 (accepted deviation)")
        void painMax() {
            MonsterRegistry.setMonsterPains(pains(12));

            assertEquals(12, MonsterRegistry.getMonsterPainMsgMax());
        }

        @Test
        @DisplayName("index 0 finds nothing, where C has a blank record in slot 0 (accepted deviation)")
        void indexZero() {
            MonsterRegistry.setMonsterPains(pains(12));

            assertNull(MonsterRegistry.lookupMonsterPain(0));
            assertNull(MonsterRegistry.getPainFromIndex(0));
        }

        @Test
        @DisplayName("an index below 0 or above the highest finds nothing")
        void outOfRange() {
            MonsterRegistry.setMonsterPains(pains(12));

            assertNull(MonsterRegistry.lookupMonsterPain(-1));
            assertNull(MonsterRegistry.lookupMonsterPain(13));
            assertNull(MonsterRegistry.getPainFromIndex(13));
        }

        @Test
        @DisplayName("an unloaded list throws from both lookups")
        void unloaded() {
            assertThrows(IllegalStateException.class, () -> MonsterRegistry.lookupMonsterPain(1));
            assertThrows(IllegalStateException.class, () -> MonsterRegistry.getPainFromIndex(1));
        }
    }

    /**
     * {@code lookup_monster_base}.
     */
    @Nested
    @DisplayName("monster bases")
    class Bases {

        @BeforeEach
        void load() {
            MonsterRegistry.setMonsterBases(new ArrayList<>(List.of(
                    new MonsterBase("dragon"), new MonsterBase("ancient dragon"), new MonsterBase("Dragon"))));
        }

        @Test
        @DisplayName("an exact name is found")
        void exact() {
            assertEquals("ancient dragon", MonsterRegistry.lookupMonsterBase("ancient dragon").getCodeName());
        }

        @Test
        @DisplayName("the match is case-sensitive, as streq is")
        void caseSensitive() {
            assertEquals("Dragon", MonsterRegistry.lookupMonsterBase("Dragon").getCodeName());
            assertNull(MonsterRegistry.lookupMonsterBase("DRAGON"));
        }

        @Test
        @DisplayName("there is no substring fallback")
        void noSubstring() {
            assertNull(MonsterRegistry.lookupMonsterBase("drag"));
        }

        @Test
        @DisplayName("getBaseFromName gives the same answer")
        void delegates() {
            assertSame(MonsterRegistry.lookupMonsterBase("dragon"), MonsterRegistry.getBaseFromName("dragon"));
            assertNull(MonsterRegistry.getBaseFromName("zzz"));
        }

        @Test
        @DisplayName("an unloaded list throws")
        void unloaded() throws Exception {
            field("monsterBases").set(null, null);

            assertThrows(IllegalStateException.class, () -> MonsterRegistry.lookupMonsterBase("dragon"));
            assertThrows(IllegalStateException.class, () -> MonsterRegistry.getBaseFromName("dragon"));
        }
    }

    /**
     * {@code findmeth}, {@code findeff} and their counters.
     */
    @Nested
    @DisplayName("blow methods and effects")
    class Blows {

        private BlowMethod method(String name) {
            return new BlowMethod(name, false, false, false, false, null, List.of(), "");
        }

        private BlowEffect effect(String name) {
            return new BlowEffect(name, 0, 0, "", null, null, null, null, null, null, null);
        }

        @Test
        @DisplayName("the first method is found, since the Java list has no blank slot 0 to skip")
        void methodFirst() {
            MonsterRegistry.setBlowMethods(new ArrayList<>(List.of(method("HIT"), method("TOUCH"), method("PUNCH"))));

            assertEquals("HIT", MonsterRegistry.lookupBlowMethod("HIT").getName());
            assertEquals("PUNCH", MonsterRegistry.lookupBlowMethod("PUNCH").getName());
        }

        @Test
        @DisplayName("a method name is case-sensitive and must match in full")
        void methodExact() {
            MonsterRegistry.setBlowMethods(new ArrayList<>(List.of(method("HIT"))));

            assertNull(MonsterRegistry.lookupBlowMethod("hit"));
            assertNull(MonsterRegistry.lookupBlowMethod("HI"));
        }

        @Test
        @DisplayName("the method count is the list size, one below C's blow_methods_max (accepted deviation)")
        void methodMax() {
            MonsterRegistry.setBlowMethods(new ArrayList<>(List.of(method("HIT"), method("TOUCH"), method("PUNCH"))));

            assertEquals(3, MonsterRegistry.getMonsterBlowsMethodsMax());
        }

        @Test
        @DisplayName("the first effect, NONE, is found, as blow_effects[0] is real in C")
        void effectFirst() {
            MonsterRegistry.setBlowEffects(new ArrayList<>(List.of(effect("NONE"), effect("HURT"))));

            assertEquals("NONE", MonsterRegistry.lookupBlowEffect("NONE").getName());
            assertEquals("HURT", MonsterRegistry.lookupBlowEffect("HURT").getName());
            assertNull(MonsterRegistry.lookupBlowEffect("none"));
        }

        @Test
        @DisplayName("the effect count is the list size, one below C's blow_effects_max (accepted deviation)")
        void effectMax() {
            MonsterRegistry.setBlowEffects(new ArrayList<>(List.of(effect("NONE"), effect("HURT"))));

            assertEquals(2, MonsterRegistry.getMonsterBlowsEffectsMax());
        }

        @Test
        @DisplayName("unloaded lists throw")
        void unloaded() {
            assertThrows(IllegalStateException.class, () -> MonsterRegistry.lookupBlowMethod("HIT"));
            assertThrows(IllegalStateException.class, () -> MonsterRegistry.lookupBlowEffect("NONE"));
        }
    }

    /**
     * {@code summon_name_to_idx}.
     */
    @Nested
    @DisplayName("summons")
    class Summons {

        private Summon summon(String name) {
            return new Summon(name, null, false, List.of(), null, null, "", "");
        }

        @Test
        @DisplayName("a summon is found by exact name and returned as the list's own record")
        void exact() {
            List<Summon> list = new ArrayList<>(List.of(summon("NONE"), summon("ANY"), summon("KIN")));
            MonsterRegistry.setSummons(list);

            assertSame(list.get(0), MonsterRegistry.lookupSummon("NONE"));
            assertSame(list.get(2), MonsterRegistry.lookupSummon("KIN"));
        }

        @Test
        @DisplayName("a miss is null, where C returns -1, and the match is case-sensitive")
        void miss() {
            MonsterRegistry.setSummons(new ArrayList<>(List.of(summon("KIN"))));

            assertNull(MonsterRegistry.lookupSummon("kin"));
            assertNull(MonsterRegistry.lookupSummon("ZZZ"));
        }

        @Test
        @DisplayName("an unloaded or null list throws")
        void unloaded() {
            assertThrows(IllegalStateException.class, () -> MonsterRegistry.lookupSummon("KIN"));

            MonsterRegistry.setSummons(null);

            assertThrows(IllegalStateException.class, () -> MonsterRegistry.lookupSummon("KIN"));
        }
    }

    /**
     * The pit-profile counter.
     */
    @Test
    @DisplayName("the pit count is the list size, one below C's pit_max (accepted deviation)")
    void pitMax() {
        MonsterRegistry.setMonsterPitProfiles(new ArrayList<>(Arrays.asList(null, null, null)));

        assertEquals(3, MonsterRegistry.getMonsterPitTypeMax());
    }
}
