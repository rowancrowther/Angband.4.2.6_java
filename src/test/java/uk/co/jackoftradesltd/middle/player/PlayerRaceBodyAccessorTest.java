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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests {@link Player#getRace} and {@link Player#setBody} — the two ends of the ownership rule that
 * {@code PlayerBirth.embody} sits between.
 *
 * <p>Neither method does any work, so what is asserted here is <em>sharing</em>, which is the only
 * thing that can go wrong with them and the one thing a reader cannot see from the one-line bodies.
 * C keeps the player's race as a pointer into the {@code races} array and the player's body as an
 * embedded struct, so C gets one shared and one owned for free. Java has a reference for both, and
 * the difference has to be maintained by hand.
 *
 * <p>Two facts follow, and both are checked: the race handed back <em>is</em> the registry's entry
 * and must not be written through, while the body a player holds is never the registry's, because
 * {@code lookupPlayerBody} copies before returning. {@code setBody} itself takes what it is given
 * without copying — so the test states that plainly rather than leaving a reader to assume a
 * defensive copy that is not there.
 *
 * <p>The null case is not a defensive nicety either: a character is built in stages, and
 * {@code embody} tests {@code getRace() == null} to decide whether the race has been chosen yet.
 *
 * <p>Class PlayerRaceBodyAccessorTest coded on 260901, commented in full on 260901.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerRaceBodyAccessorTest {

    /**
     * The player under test, fresh for each test since both fields are mutable.
     */
    private Player player;

    /**
     * A new player, as the constructor leaves one.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * {@code getRace}, which reports the race the character was built from.
     */
    @Nested
    @DisplayName("getRace")
    class Race {

        /**
         * The constructor takes the registry's first race, and the accessor hands back that very
         * object rather than a copy — which is what makes it read-only to callers.
         */
        @Test
        @DisplayName("the race is the registry's own entry, shared not copied")
        void raceIsTheRegistryEntry() {
            assertSame(PlayerRegistry.getFirstPlayerRace(), player.getRace());
        }

        /**
         * And it answers {@code null} rather than failing when there is no race yet, which is the
         * state a half-built character is in and the state {@code embody} guards against.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a raceless player answers null")
        void racelessPlayerAnswersNull() throws Exception {
            set(player, "race", null);

            assertNull(player.getRace());
        }
    }

    /**
     * {@code setBody}, which gives the player their equipment slots.
     */
    @Nested
    @DisplayName("setBody")
    class Body {

        /**
         * What is set is what is read back, unchanged — no copy is made on the way in. That is the
         * contract the callers have to honour: {@code embody} passes a copy of the race's template
         * precisely because this method will not make one for it.
         */
        @Test
        @DisplayName("the body set is the body held, with no copy made")
        void bodySetIsBodyHeld() {
            PlayerBody body = SeededPlayerRegistry.humanoidBody();

            player.setBody(body);

            assertSame(body, player.getPlayerBody());
        }

        /**
         * It replaces the body the constructor gave, so a player's slots come from the last body set
         * rather than accumulating.
         */
        @Test
        @DisplayName("it replaces the body the constructor supplied")
        void bodyReplacesTheConstructorBody() {
            PlayerBody first = player.getPlayerBody();
            PlayerBody second = SeededPlayerRegistry.humanoidBody();

            player.setBody(second);

            assertNotSame(first, player.getPlayerBody());
            assertSame(second, player.getPlayerBody());
        }

        /**
         * The body a fresh player starts with is already its own: the constructor goes through
         * {@code PlayerRegistry.lookupPlayerBody}, which copies. The comparison has to be against
         * the list entry itself rather than against another lookup, since two lookups return two
         * copies and would differ however the registry behaved. A player sharing the registry's body
         * would wear equipment into the game's own data, and nothing else would notice.
         *
         * @throws Exception if the registry's list cannot be reached
         */
        @Test
        @DisplayName("the constructor's body is not the registry's")
        void constructorBodyIsNotTheRegistrys() throws Exception {
            Field field = PlayerRegistry.class.getDeclaredField("playerBodies");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PlayerBody> registered = (List<PlayerBody>) field.get(null);

            assertNotSame(registered.get(0), player.getPlayerBody());
        }
    }
}
