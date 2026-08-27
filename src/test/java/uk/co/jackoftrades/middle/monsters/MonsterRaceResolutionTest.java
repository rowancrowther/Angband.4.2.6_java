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

package uk.co.jackoftrades.middle.monsters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link MonsterRace}'s second-pass resolution — the two methods that turn the names in
 * {@code monster.txt} into the races and bases they refer to.
 *
 * <p>They exist because the file is not ordered: a monster may name a companion or a shape defined
 * further down, so the loader records the name on the first pass and resolves it on a second, once
 * every race exists. That makes the resolution registry-dependent, so these tests seed the monster
 * registry and put it back afterwards.
 *
 * <p>The interesting half is {@link MonsterRace#resolveShapes()}, which tries each name as a monster
 * <em>base</em> first and only then as a specific race. That order is what lets a shapechange name a
 * whole family — "any dragon" — rather than one dragon, and the two outcomes are stored in different
 * fields, so a name resolved the wrong way would change what the shapechange does.
 *
 * @author Rowan Crowther
 */
class MonsterRaceResolutionTest {

    /**
     * Whatever the registry held before each test.
     */
    private List<MonsterRace> savedRaces;

    /**
     * The bases the registry held before each test.
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
     * A race carrying the given friends and shapes, and nothing else worth speaking of.
     *
     * @param name    the race's name
     * @param friends its companion entries
     * @param shapes  its shape entries
     * @return the race
     */
    private static MonsterRace race(String name, List<MonsterFriends> friends, List<MonsterShape> shapes) {
        return new MonsterRace(name, "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                null, null, List.of(), 0, 0, null, null,
                List.of(), friends, List.of(), List.of(), shapes, shapes.size(), null);
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
     * Companion resolution, which is a plain lookup by name.
     */
    @Nested
    @DisplayName("resolveFriends")
    class Friends {

        /**
         * A companion entry naming a race that exists is resolved to it.
         *
         * @throws Exception if the resolved race cannot be read back
         */
        @Test
        @DisplayName("a named companion resolves to its race")
        void companionResolves() throws Exception {
            MonsterRace kobold = race("kobold", List.of(), List.of());
            MonsterRegistry.monsterRaces.add(kobold);

            MonsterFriends entry = new MonsterFriends("kobold", MonsterGroupRole.MON_GROUP_MEMBER, 100, 1, 4);
            MonsterRace chief = race("kobold chieftain", List.of(entry), List.of());

            chief.resolveFriends();

            assertSame(kobold, raceOf(entry));
        }

        /**
         * A companion naming a race that does not exist is a data error, and is refused rather than
         * left unresolved — a companion nobody can generate would fail later and further away.
         */
        @Test
        @DisplayName("an unknown companion is refused")
        void unknownCompanionRefused() {
            MonsterFriends entry = new MonsterFriends("nothing at all", MonsterGroupRole.MON_GROUP_MEMBER, 100, 1, 4);
            MonsterRace lonely = race("lonely", List.of(entry), List.of());

            assertThrows(IllegalArgumentException.class, lonely::resolveFriends);
        }

        /**
         * A race with no companions resolves quietly, which is the common case.
         */
        @Test
        @DisplayName("a race with no companions resolves quietly")
        void noCompanions() {
            race("solitary", List.of(), List.of()).resolveFriends();
        }
    }

    /**
     * Shape resolution, which tries a base before a race.
     */
    @Nested
    @DisplayName("resolveShapes")
    class Shapes {

        /**
         * A shape naming a base resolves to the base and leaves the race unset — the shapechange is
         * to a whole family, and which member is chosen later.
         *
         * @throws Exception if the registry cannot be reached
         */
        @Test
        @DisplayName("a base name resolves to the base, not a race")
        void baseNameResolvesToBase() throws Exception {
            MonsterBase dragon = new MonsterBase("dragon");
            @SuppressWarnings("unchecked")
            List<MonsterBase> bases = (List<MonsterBase>) basesField().get(null);
            bases.add(dragon);

            MonsterShape shape = new MonsterShape("dragon");
            MonsterRace shifter = race("shapeshifter", List.of(), List.of(shape));

            shifter.resolveShapes();

            assertSame(dragon, shape.getBase());
            assertNull(shape.getRace(), "a family shapechange names no particular member");
        }

        /**
         * A name that is not a base is tried as a race, and resolving that way clears the base — the
         * two fields are alternatives, and both being set would be ambiguous.
         */
        @Test
        @DisplayName("a race name resolves to the race, and clears the base")
        void raceNameResolvesToRace() {
            MonsterRace wolf = race("wolf", List.of(), List.of());
            MonsterRegistry.monsterRaces.add(wolf);

            MonsterShape shape = new MonsterShape("wolf");
            MonsterRace shifter = race("shapeshifter", List.of(), List.of(shape));

            shifter.resolveShapes();

            assertSame(wolf, shape.getRace());
            assertNull(shape.getBase(), "a specific shapechange names no family");
        }

        /**
         * A name that is neither is a fatal data error, thrown as a parse failure because that is
         * what it is — the file named something that does not exist.
         */
        @Test
        @DisplayName("a name that is neither base nor race is refused")
        void unknownShapeRefused() {
            MonsterShape shape = new MonsterShape("not a thing");
            MonsterRace shifter = race("shapeshifter", List.of(), List.of(shape));

            assertThrows(InvalidTokenFoundDuringParse.class, shifter::resolveShapes);
        }

        /**
         * A base is preferred over a race of the same name, which is the order the method tries them
         * in and the reason a family name cannot be shadowed by one of its members.
         *
         * @throws Exception if the registry cannot be reached
         */
        @Test
        @DisplayName("a base wins over a race of the same name")
        void basePreferredOverRace() throws Exception {
            MonsterBase dragon = new MonsterBase("dragon");
            @SuppressWarnings("unchecked")
            List<MonsterBase> bases = (List<MonsterBase>) basesField().get(null);
            bases.add(dragon);
            MonsterRegistry.monsterRaces.add(race("dragon", List.of(), List.of()));

            MonsterShape shape = new MonsterShape("dragon");
            race("shapeshifter", List.of(), List.of(shape)).resolveShapes();

            assertSame(dragon, shape.getBase());
            assertNull(shape.getRace());
        }
    }

    /**
     * The spell-message record, which groups the three variants C keeps as parallel strings.
     */
    @Nested
    @DisplayName("MonsterSpellMessages")
    class SpellMessages {

        /**
         * The three components come back as given. They are all strings, and the difference between
         * them is which of the three the player is in a position to perceive — seen, unseen, or
         * missed — so a transposition would show the wrong one at the wrong moment.
         */
        @Test
        @DisplayName("the three message variants are distinct")
        void variantsAreDistinct() {
            MonsterRace.MonsterSpellMessages messages = new MonsterRace.MonsterSpellMessages(
                    "The orc casts a spell.", "Something casts a spell.", "The spell fizzles.");

            assertEquals("The orc casts a spell.", messages.visible());
            assertEquals("Something casts a spell.", messages.invisible());
            assertEquals("The spell fizzles.", messages.miss());
        }

        /**
         * Being a record, two with the same three strings are equal — which is what lets the message
         * table be compared and de-duplicated.
         */
        @Test
        @DisplayName("two with the same variants are equal")
        void equalityIsByComponents() {
            MonsterRace.MonsterSpellMessages one =
                    new MonsterRace.MonsterSpellMessages("a", "b", "c");
            MonsterRace.MonsterSpellMessages same =
                    new MonsterRace.MonsterSpellMessages("a", "b", "c");
            MonsterRace.MonsterSpellMessages different =
                    new MonsterRace.MonsterSpellMessages("a", "b", "d");

            assertEquals(one, same);
            assertTrue(!one.equals(different));
        }
    }
}
