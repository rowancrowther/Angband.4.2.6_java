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

import org.junit.jupiter.api.*;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterGroupRole;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the parts of {@link MonsterRace} that {@code MonsterRaceResolutionTest} and the other
 * {@code MonsterRace*Test} classes leave open: the plain accessors, the flag test, and the exact
 * name-matching rules of {@link MonsterRace#resolveFriends()} and {@link MonsterRace#resolveShapes()}.
 *
 * <p>The expected values come from the C source, not from the Java. {@code lookup_monster} in
 * {@code mon-util.c} returns the first race whose name equals the query ignoring case, and failing
 * that the first race whose name <em>contains</em> the query ignoring case; {@code my_stristr} in
 * {@code z-util.c} never matches an empty pattern. {@code lookup_monster_base} is a plain
 * {@code streq}, so it is case-sensitive and has no substring fallback. The "same" friend is
 * substituted by the assembler before {@link MonsterRace#resolveFriends()} runs, so here it appears
 * as the race's own name, which is the form the resolver sees.
 *
 * @author Rowan Crowther
 */
class MonsterRaceLookupAgreementTest {

    /**
     * The race list the registry held before each test.
     */
    private List<MonsterRace> savedRaces;

    /**
     * The base list the registry held before each test.
     */
    private Object savedBases;

    /**
     * The monster-base list, which has a setter but is read here directly so it can be saved.
     *
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field basesField() throws Exception {
        Field field = MonsterRegistry.class.getDeclaredField("monsterBases");
        field.setAccessible(true);
        return field;
    }

    /**
     * Reads a companion entry's resolved race, which has no accessor.
     *
     * @param entry the entry to read
     * @return the race it resolved to, or {@code null} if it has not been resolved
     * @throws Exception if the field cannot be reached
     */
    private static MonsterRace raceOf(MonsterFriends entry) throws Exception {
        Field field = MonsterFriends.class.getDeclaredField("race");
        field.setAccessible(true);
        return (MonsterRace) field.get(entry);
    }

    /**
     * A race with the given name, level, flags, blows, friends and shapes and nothing else.
     *
     * @param name    the race's name
     * @param level   its native level
     * @param flags   its race flags
     * @param blows   its blows
     * @param friends its companion entries
     * @param shapes  its shape entries
     * @return the race
     */
    private static MonsterRace race(String name, int level, Flag<MonsterRaceFlag> flags,
                                    List<MonsterBlow> blows, List<MonsterFriends> friends,
                                    List<MonsterShape> shapes) {
        return new MonsterRace(name, "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                flags, null, blows, level, 0, null, null,
                List.of(), friends, List.of(), List.of(), shapes, shapes.size(), null);
    }

    /**
     * A race that only has a name, for filling the registry.
     *
     * @param name the race's name
     * @return the race
     */
    private static MonsterRace named(String name) {
        return race(name, 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(), List.of());
    }

    /**
     * A companion entry with the given name.
     *
     * @param name the name to resolve
     * @return the entry
     */
    private static MonsterFriends friend(String name) {
        return new MonsterFriends(name, MonsterGroupRole.MON_GROUP_MEMBER, 100, 1, 4);
    }

    /**
     * Seeds an empty registry, so each test states its own contents.
     *
     * @throws Exception if the registry field cannot be reached
     */
    @BeforeEach
    void seedRegistry() throws Exception {
        savedRaces = MonsterRegistry.monsterRaces;
        savedBases = basesField().get(null);

        MonsterRegistry.monsterRaces = new ArrayList<>();
        basesField().set(null, new ArrayList<MonsterBase>());
    }

    /**
     * Puts the registry back.
     *
     * @throws Exception if the registry field cannot be reached
     */
    @AfterEach
    void restoreRegistry() throws Exception {
        MonsterRegistry.monsterRaces = savedRaces;
        basesField().set(null, savedBases);
    }

    /**
     * The accessors that only read what the constructor stored.
     */
    @Nested
    @DisplayName("plain accessors")
    class Accessors {

        /**
         * The name and level the constructor was given come straight back, with no case change or
         * clamping, as C reads {@code race->name} and {@code race->level}.
         */
        @Test
        @DisplayName("getName and getLevel return what was stored")
        void nameAndLevel() {
            MonsterRace r = race("Grip, Farmer Maggot's Dog", 2, new Flag<>(MonsterRaceFlag.class),
                    List.of(), List.of(), List.of());

            assertEquals("Grip, Farmer Maggot's Dog", r.getName());
            assertEquals(2, r.getLevel());
        }

        /**
         * Level zero is the town level and a legitimate value, not "unset".
         */
        @Test
        @DisplayName("level zero is stored as zero")
        void levelZero() {
            assertEquals(0, named("townsperson").getLevel());
        }

        /**
         * The test-only constructor leaves the name unset, so it reads as {@code null}, not as an
         * empty string.
         */
        @Test
        @DisplayName("a bare test race has no name")
        void bareRaceHasNoName() {
            assertNull(new MonsterRace().getName());
        }

        /**
         * C appends each blow to the end of the list while parsing, so the blows come back in the
         * order the {@code blow:} lines appear. The list is the one that was passed in.
         */
        @Test
        @DisplayName("getBlows returns the blows in the order given, as the same list")
        void blowsInOrder() {
            MonsterBlow first = new MonsterBlow(null, null, null);
            MonsterBlow second = new MonsterBlow(null, null, null);
            MonsterBlow third = new MonsterBlow(null, null, null);
            List<MonsterBlow> blows = List.of(first, second, third);

            MonsterRace r = race("triple", 1, new Flag<>(MonsterRaceFlag.class), blows,
                    List.of(), List.of());

            assertSame(blows, r.getBlows());
            assertSame(first, r.getBlows().get(0));
            assertSame(second, r.getBlows().get(1));
            assertSame(third, r.getBlows().get(2));
        }

        /**
         * A race with no {@code blow:} lines has an empty list, where C has a {@code NULL} head
         * (and, after {@code finish_parse_monster}, an array of blank records).
         */
        @Test
        @DisplayName("a race with no blows has an empty list")
        void noBlows() {
            assertTrue(named("harmless").getBlows().isEmpty());
        }
    }

    /**
     * {@code rf_has}, which is a single bit test.
     */
    @Nested
    @DisplayName("hasMonsterRaceFlag")
    class FlagTest {

        /**
         * Each flag the race was built with tests true.
         */
        @Test
        @DisplayName("a flag that is set tests true")
        void setFlagTestsTrue() {
            MonsterRace r = race("flagged", 0,
                    new Flag<>(MonsterRaceFlag.class, MonsterRaceFlag.RF_UNIQUE, MonsterRaceFlag.RF_MALE),
                    List.of(), List.of(), List.of());

            assertTrue(r.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE));
            assertTrue(r.hasMonsterRaceFlag(MonsterRaceFlag.RF_MALE));
        }

        /**
         * A flag that was not set tests false, even when its neighbours in the enum are set.
         */
        @Test
        @DisplayName("a flag that is not set tests false, though a neighbour is set")
        void unsetFlagTestsFalse() {
            MonsterRace r = race("flagged", 0,
                    new Flag<>(MonsterRaceFlag.class, MonsterRaceFlag.RF_MALE),
                    List.of(), List.of(), List.of());

            assertFalse(r.hasMonsterRaceFlag(MonsterRaceFlag.RF_FEMALE));
            assertFalse(r.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE));
        }

        /**
         * The test-only constructor builds an empty flag set, so every flag tests false.
         */
        @Test
        @DisplayName("a bare test race has no flags")
        void bareRaceHasNoFlags() {
            MonsterRace r = new MonsterRace();

            for (MonsterRaceFlag flag : MonsterRaceFlag.values()) {
                assertFalse(r.hasMonsterRaceFlag(flag), flag + " should not be set");
            }
        }

        /**
         * Testing a race does not change it: the answer is the same the second time.
         */
        @Test
        @DisplayName("testing a flag does not alter the set")
        void testingIsPure() {
            MonsterRace r = race("flagged", 0,
                    new Flag<>(MonsterRaceFlag.class, MonsterRaceFlag.RF_UNIQUE),
                    List.of(), List.of(), List.of());

            assertTrue(r.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE));
            assertTrue(r.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE));
            assertFalse(r.hasMonsterRaceFlag(MonsterRaceFlag.RF_MALE));
        }
    }

    /**
     * The name-matching rules of {@code lookup_monster} as {@code resolveFriends} applies them.
     */
    @Nested
    @DisplayName("resolveFriends name matching")
    class FriendMatching {

        /**
         * The match ignores case: C compares with {@code my_stricmp}.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("an exact name matches whatever its case")
        void caseInsensitive() throws Exception {
            MonsterRace kobold = named("kobold");
            MonsterRegistry.monsterRaces.add(kobold);
            MonsterFriends entry = friend("KOBOLD");

            race("chief", 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(entry), List.of())
                    .resolveFriends();

            assertSame(kobold, raceOf(entry));
        }

        /**
         * With no exact match, C settles for the first race whose name contains the text, so a
         * friend named "kobold" finds "large kobold" when that is the only candidate.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("with no exact match, a name inside another name is taken")
        void substringFallback() throws Exception {
            MonsterRace large = named("large kobold");
            MonsterRegistry.monsterRaces.add(large);
            MonsterFriends entry = friend("kobold");

            race("chief", 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(entry), List.of())
                    .resolveFriends();

            assertSame(large, raceOf(entry));
        }

        /**
         * Of several containing names the first in file order wins.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("of several partial matches the first in list order wins")
        void firstPartialMatchWins() throws Exception {
            MonsterRace large = named("large kobold");
            MonsterRace chieftain = named("kobold chieftain");
            MonsterRegistry.monsterRaces.add(large);
            MonsterRegistry.monsterRaces.add(chieftain);
            MonsterFriends entry = friend("kobold");

            race("chief", 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(entry), List.of())
                    .resolveFriends();

            assertSame(large, raceOf(entry));
        }

        /**
         * An exact match beats a partial one that comes earlier: C returns the moment it sees an
         * exact name, and only falls back to {@code closest} at the end of the scan.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("an exact match later in the list beats an earlier partial match")
        void exactBeatsEarlierPartial() throws Exception {
            MonsterRace large = named("large kobold");
            MonsterRace plain = named("kobold");
            MonsterRegistry.monsterRaces.add(large);
            MonsterRegistry.monsterRaces.add(plain);
            MonsterFriends entry = friend("kobold");

            race("chief", 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(entry), List.of())
                    .resolveFriends();

            assertSame(plain, raceOf(entry));
        }

        /**
         * A race that lists itself as a friend, which is what the assembler leaves behind for a
         * {@code "same"} entry, resolves to itself. C points the entry at the race by identity.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("a friend carrying the race's own name resolves to the race itself")
        void sameResolvesToSelf() throws Exception {
            MonsterFriends entry = friend("cave spider");
            MonsterRace spider = race("cave spider", 2, new Flag<>(MonsterRaceFlag.class),
                    List.of(), List.of(entry), List.of());
            MonsterRegistry.monsterRaces.add(named("giant white mouse"));
            MonsterRegistry.monsterRaces.add(spider);

            spider.resolveFriends();

            assertSame(spider, raceOf(entry));
        }

        /**
         * Every entry in the list is resolved, not just the first.
         *
         * @throws Exception if the resolved races cannot be read back
         */
        @Test
        @DisplayName("every companion entry is resolved")
        void everyEntryResolved() throws Exception {
            MonsterRace wolf = named("wolf");
            MonsterRace warg = named("warg");
            MonsterRegistry.monsterRaces.add(wolf);
            MonsterRegistry.monsterRaces.add(warg);
            MonsterFriends first = friend("wolf");
            MonsterFriends second = friend("warg");

            race("pack leader", 0, new Flag<>(MonsterRaceFlag.class), List.of(),
                    List.of(first, second), List.of()).resolveFriends();

            assertSame(wolf, raceOf(first));
            assertSame(warg, raceOf(second));
        }

        /**
         * An empty name matches nothing: C's {@code my_stristr} returns {@code NULL} for an empty
         * pattern, so the lookup fails and {@code finish_parse_monster} quits.
         */
        @Test
        @DisplayName("an empty name matches no race and is refused")
        void emptyNameRefused() {
            MonsterRegistry.monsterRaces.add(named("kobold"));
            MonsterRace lonely = race("lonely", 0, new Flag<>(MonsterRaceFlag.class), List.of(),
                    List.of(friend("")), List.of());

            assertThrows(IllegalArgumentException.class, lonely::resolveFriends);
        }
    }

    /**
     * The base-then-race rules of {@code resolveShapes}.
     */
    @Nested
    @DisplayName("resolveShapes name matching")
    class ShapeMatching {

        /**
         * Adds a base to the registry's base list.
         *
         * @param base the base to add
         * @throws Exception if the registry cannot be reached
         */
        private void addBase(MonsterBase base) throws Exception {
            @SuppressWarnings("unchecked")
            List<MonsterBase> bases = (List<MonsterBase>) basesField().get(null);
            bases.add(base);
        }

        /**
         * {@code lookup_monster_base} is a {@code streq}, so "Dragon" is not the base "dragon". The
         * name then goes to the race lookup, which ignores case, so a race called "dragon" is found.
         *
         * @throws Exception if the registry cannot be reached
         */
        @Test
        @DisplayName("a base name in the wrong case is not a base, and falls through to the races")
        void baseLookupIsCaseSensitive() throws Exception {
            addBase(new MonsterBase("dragon"));
            MonsterRace dragon = named("dragon");
            MonsterRegistry.monsterRaces.add(dragon);

            MonsterShape shape = new MonsterShape("Dragon");
            race("shapeshifter", 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(),
                    List.of(shape)).resolveShapes();

            assertNull(shape.getBase(), "streq does not match 'Dragon' to 'dragon'");
            assertSame(dragon, shape.getRace());
        }

        /**
         * There is no substring fallback for bases: "drag" is not the base "dragon". It is a
         * substring of a race name though, and the race lookup does allow that.
         *
         * @throws Exception if the registry cannot be reached
         */
        @Test
        @DisplayName("a partial base name is not a base, but may be a partial race name")
        void baseLookupHasNoSubstringFallback() throws Exception {
            addBase(new MonsterBase("dragon"));
            MonsterRace baby = named("baby blue dragon");
            MonsterRegistry.monsterRaces.add(baby);

            MonsterShape shape = new MonsterShape("drag");
            race("shapeshifter", 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(),
                    List.of(shape)).resolveShapes();

            assertNull(shape.getBase());
            assertSame(baby, shape.getRace());
        }

        /**
         * The race half of the shape lookup is {@code lookup_monster}, so an exact name beats an
         * earlier partial match here as well.
         */
        @Test
        @DisplayName("an exact race name beats an earlier partial one")
        void exactRaceBeatsPartial() {
            MonsterRace large = named("large wolf");
            MonsterRace wolf = named("wolf");
            MonsterRegistry.monsterRaces.add(large);
            MonsterRegistry.monsterRaces.add(wolf);

            MonsterShape shape = new MonsterShape("wolf");
            race("shapeshifter", 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(),
                    List.of(shape)).resolveShapes();

            assertSame(wolf, shape.getRace());
        }

        /**
         * Each shape is judged on its own: one that names a base and one that names a race, in the
         * same list, each end with the matching field set and the other clear.
         *
         * @throws Exception if the registry cannot be reached
         */
        @Test
        @DisplayName("a base shape and a race shape in one list are each resolved on their own")
        void mixedShapes() throws Exception {
            MonsterBase dragon = new MonsterBase("dragon");
            addBase(dragon);
            MonsterRace wolf = named("wolf");
            MonsterRegistry.monsterRaces.add(wolf);

            MonsterShape family = new MonsterShape("dragon");
            MonsterShape particular = new MonsterShape("wolf");
            race("shapeshifter", 0, new Flag<>(MonsterRaceFlag.class), List.of(), List.of(),
                    List.of(family, particular)).resolveShapes();

            assertSame(dragon, family.getBase());
            assertNull(family.getRace());
            assertSame(wolf, particular.getRace());
            assertNull(particular.getBase());
        }

        /**
         * An empty shape name is neither a base nor a race (the race lookup never matches an empty
         * pattern), so it is refused like any other unknown name.
         */
        @Test
        @DisplayName("an empty shape name is refused")
        void emptyShapeRefused() {
            MonsterRegistry.monsterRaces.add(named("wolf"));
            MonsterRace shifter = race("shapeshifter", 0, new Flag<>(MonsterRaceFlag.class),
                    List.of(), List.of(), List.of(new MonsterShape("")));

            assertThrows(uk.co.jackoftradesltd.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse.class,
                    shifter::resolveShapes);
        }
    }
}
