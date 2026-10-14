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
 * Tests {@link MonsterFriends}, the Java form of C's {@code struct monster_friends}, and the
 * {@link MonsterFriends#setRace(String)} step that stands in for the friends half of
 * {@code finish_parse_monster} in {@code mon-init.c}.
 *
 * <p>The expected values come from the C source, not from the Java. {@code parse_monster_friends}
 * copies the dice and sides of {@code number}, the chance, the role (member by default) and the
 * name into the record and leaves {@code race} zero. {@code finish_parse_monster} then calls
 * {@code lookup_monster} ({@code mon-util.c}), which returns the first race whose name equals the
 * text ignoring case, and failing that the first race whose name contains the text ignoring case;
 * {@code my_stristr} ({@code z-util.c}) never matches an empty pattern, so an empty name finds
 * nothing. The sample entries use the figures of real {@code monster.txt} lines, such as
 * {@code friends:80:1d7:Scruffy looking hobbit:servant} on Bullroarer and
 * {@code friends:80:2d7:Wolf} on Wormtongue.
 *
 * <p>The monster registry is seeded for each test and put back afterwards.
 *
 * <p>Class MonsterFriendsTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class MonsterFriendsTest {

    /**
     * The race list the registry held before each test.
     */
    private List<MonsterRace> savedRaces;

    /**
     * Seeds an empty race list, remembering what was there.
     */
    @BeforeEach
    void seedRegistry() {
        savedRaces = MonsterRegistry.monsterRaces;
        MonsterRegistry.monsterRaces = new ArrayList<>();
    }

    /**
     * Puts the registry back.
     */
    @AfterEach
    void restoreRegistry() {
        MonsterRegistry.monsterRaces = savedRaces;
    }

    /**
     * Reads a private field of an entry, none of which except the name has an accessor.
     *
     * @param entry the entry to read
     * @param field the field name
     * @return the field's value
     * @throws Exception if the field cannot be reached
     */
    private static Object read(MonsterFriends entry, String field) throws Exception {
        Field f = MonsterFriends.class.getDeclaredField(field);
        f.setAccessible(true);
        return f.get(entry);
    }

    /**
     * A race that has only a name, for filling the registry.
     *
     * @param name the race's name
     * @return the race
     */
    private static MonsterRace named(String name) {
        return new MonsterRace(name, "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                new Flag<>(MonsterRaceFlag.class), null, List.of(), 0, 0, null, null,
                List.of(), List.of(), List.of(), List.of(), List.of(), 0, null);
    }

    /**
     * A member entry with the given name, as a plain {@code friends:100:1d4:name} line gives.
     *
     * @param name the name to resolve
     * @return the entry
     */
    private static MonsterFriends member(String name) {
        return new MonsterFriends(name, MonsterGroupRole.MON_GROUP_MEMBER, 100, 1, 4);
    }

    /**
     * Adds the named races to the seeded registry, in the order given.
     *
     * @param names the race names
     * @return the races, in the same order
     */
    private static List<MonsterRace> register(String... names) {
        List<MonsterRace> races = new ArrayList<>();
        for (String name : names) {
            MonsterRace race = named(name);
            MonsterRegistry.monsterRaces.add(race);
            races.add(race);
        }
        return races;
    }

    /**
     * What the constructor keeps, which is what {@code parse_monster_friends} stores.
     */
    @Nested
    @DisplayName("construction")
    class Construction {

        /**
         * A servant line keeps its role, chance, dice and sides. These are Bullroarer's
         * {@code friends:80:1d7:Scruffy looking hobbit:servant}; C stores the chance as given and
         * the dice and sides of {@code 1d7} as 1 and 7.
         *
         * @throws Exception if a field cannot be read back
         */
        @Test
        @DisplayName("a servant line keeps all five parsed values")
        void servantLine() throws Exception {
            MonsterFriends entry = new MonsterFriends("Scruffy looking hobbit",
                    MonsterGroupRole.MON_GROUP_SERVANT, 80, 1, 7);

            assertEquals("Scruffy looking hobbit", entry.getName());
            assertEquals(MonsterGroupRole.MON_GROUP_SERVANT, read(entry, "role"));
            assertEquals(80, read(entry, "percentChance"));
            assertEquals(1, read(entry, "numberDice"));
            assertEquals(7, read(entry, "numberSides"));
        }

        /**
         * A bodyguard line keeps its role, and a member line keeps the member role: C gives
         * {@code MON_GROUP_MEMBER} when the line names no role, and never {@code MON_GROUP_NONE}.
         *
         * @throws Exception if a field cannot be read back
         */
        @Test
        @DisplayName("the role is kept as given")
        void roleIsKept() throws Exception {
            MonsterFriends guard = new MonsterFriends("Orc captain",
                    MonsterGroupRole.MON_GROUP_BODYGUARD, 100, 2, 3);
            MonsterFriends wolf = new MonsterFriends("Wolf", MonsterGroupRole.MON_GROUP_MEMBER, 80, 2, 7);

            assertEquals(MonsterGroupRole.MON_GROUP_BODYGUARD, read(guard, "role"));
            assertEquals(MonsterGroupRole.MON_GROUP_MEMBER, read(wolf, "role"));
            assertEquals(80, read(wolf, "percentChance"));
            assertEquals(2, read(wolf, "numberDice"));
            assertEquals(7, read(wolf, "numberSides"));
        }

        /**
         * The boundary chances are kept untouched: 0 (which C's {@code randint0(100) >= 0} makes
         * never place the companions) and 100 (which always places them) are both legal.
         *
         * @throws Exception if a field cannot be read back
         */
        @Test
        @DisplayName("chances of 0 and 100 are kept")
        void boundaryChances() throws Exception {
            assertEquals(0, read(new MonsterFriends("a", MonsterGroupRole.MON_GROUP_MEMBER, 0, 1, 1), "percentChance"));
            assertEquals(100, read(new MonsterFriends("a", MonsterGroupRole.MON_GROUP_MEMBER, 100, 1, 1), "percentChance"));
        }

        /**
         * The race is not found at construction: C's record is zero-filled, so {@code f->race} is
         * {@code NULL} until {@code finish_parse_monster} runs, even when the named race is already
         * in the registry.
         *
         * @throws Exception if the field cannot be read back
         */
        @Test
        @DisplayName("the race is null until it is resolved")
        void raceStartsNull() throws Exception {
            register("kobold");

            assertNull(read(member("kobold"), "race"));
        }
    }

    /**
     * Resolving the name to a race, by the rule of {@code lookup_monster}.
     */
    @Nested
    @DisplayName("setRace")
    class SetRace {

        /**
         * An exact name finds that race, by identity.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("an exact name resolves to that race")
        void exactName() throws Exception {
            List<MonsterRace> races = register("Kobold", "Scruffy looking hobbit");
            MonsterFriends entry = member("Scruffy looking hobbit");

            entry.setRace(entry.getName());

            assertSame(races.get(1), read(entry, "race"));
        }

        /**
         * Case is ignored, as {@code my_stricmp} ignores it: the file writes {@code cave orc} in
         * lower case on some lines and {@code Cave bear} capitalised on others.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("case is ignored")
        void caseIgnored() throws Exception {
            List<MonsterRace> races = register("Cave bear");
            MonsterFriends entry = member("cave BEAR");

            entry.setRace(entry.getName());

            assertSame(races.get(0), read(entry, "race"));
        }

        /**
         * A name that is not exact but is contained in a race name finds that race: the substring
         * fallback of {@code lookup_monster}, the first containing race in list order.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("a contained name finds the first race containing it")
        void substringFirstWins() throws Exception {
            List<MonsterRace> races = register("Grizzly bear", "Cave bear");
            MonsterFriends entry = member("bear");

            entry.setRace(entry.getName());

            assertSame(races.get(0), read(entry, "race"));
        }

        /**
         * An exact match later in the list beats a substring match earlier in it: C remembers the
         * first close match but keeps looking, and returns at once on an equal name. So
         * {@code Wolf} on Wormtongue finds the wolf, not {@code White wolf} listed before it.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("a later exact match beats an earlier substring match")
        void exactBeatsEarlierSubstring() throws Exception {
            List<MonsterRace> races = register("White wolf", "Wolf");
            MonsterFriends entry = member("Wolf");

            entry.setRace(entry.getName());

            assertSame(races.get(1), read(entry, "race"));
        }

        /**
         * A name that is longer than every race name cannot be contained in one, so it finds
         * nothing even though a race name is contained in <em>it</em>.
         */
        @Test
        @DisplayName("containment only runs one way")
        void containmentOneWay() {
            register("Wolf");
            MonsterFriends entry = member("Wolf pack leader");

            assertThrows(IllegalArgumentException.class, () -> entry.setRace(entry.getName()));
        }

        /**
         * The substituted {@code "same"} name resolves back to the owner itself. The assembler
         * replaces {@code Same} with the owning race's name, so the lookup lands on the owner by
         * identity, as C's {@code f->race = race} does.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("an owner's own name resolves to the owner")
        void sameResolvesToOwner() throws Exception {
            List<MonsterRace> races = register("Filthy street urchin");
            MonsterFriends entry = new MonsterFriends("Filthy street urchin",
                    MonsterGroupRole.MON_GROUP_MEMBER, 100, 3, 4);

            entry.setRace(entry.getName());

            assertSame(races.get(0), read(entry, "race"));
        }

        /**
         * An empty name finds nothing: no race has an empty name, and {@code my_stristr} never
         * matches an empty pattern, so the substring fallback cannot catch every race.
         */
        @Test
        @DisplayName("an empty name finds nothing")
        void emptyNameFindsNothing() {
            register("kobold", "Wolf");

            assertThrows(IllegalArgumentException.class, () -> member("").setRace(""));
        }

        /**
         * A name that matches no race is refused, as {@code quit_fmt} refuses it, and the message
         * names the missing companion.
         */
        @Test
        @DisplayName("an unknown name is refused, naming the companion")
        void unknownRefused() {
            register("kobold");
            MonsterFriends entry = member("dragon");

            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> entry.setRace(entry.getName()));

            assertTrue(e.getMessage().contains("dragon"), e.getMessage());
        }

        /**
         * A failed lookup leaves the race {@code null}, including on an entry that had already
         * resolved: the field takes the lookup result before the check.
         *
         * @throws Exception if the field cannot be read back
         */
        @Test
        @DisplayName("a failed lookup leaves the race null")
        void failureNullsRace() throws Exception {
            register("kobold");
            MonsterFriends entry = member("kobold");
            entry.setRace("kobold");
            assertNotNull(read(entry, "race"));

            assertThrows(IllegalArgumentException.class, () -> entry.setRace("nothing at all"));

            assertNull(read(entry, "race"));
        }

        /**
         * A lookup before the races are loaded is a programming error, not a missing companion,
         * and says so with a different exception.
         */
        @Test
        @DisplayName("resolving before the races are loaded is refused")
        void registryNotLoaded() {
            MonsterRegistry.monsterRaces = null;

            assertThrows(IllegalStateException.class, () -> member("kobold").setRace("kobold"));
        }

        /**
         * Resolving does not alter the name or the other four values.
         *
         * @throws Exception if a field cannot be read back
         */
        @Test
        @DisplayName("resolving leaves the other values alone")
        void resolutionKeepsOtherValues() throws Exception {
            register("Wolf");
            MonsterFriends entry = new MonsterFriends("Wolf", MonsterGroupRole.MON_GROUP_SERVANT, 80, 2, 7);

            entry.setRace(entry.getName());

            assertEquals("Wolf", entry.getName());
            assertEquals(MonsterGroupRole.MON_GROUP_SERVANT, read(entry, "role"));
            assertEquals(80, read(entry, "percentChance"));
            assertEquals(2, read(entry, "numberDice"));
            assertEquals(7, read(entry, "numberSides"));
        }
    }
}
