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
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Player#setTimed(Map)} and {@link Player#setItemKnowledge(KnownObject)} — the ports of
 * the plain field writes C makes to {@code p->timed} ({@code player.c:497}, {@code player-birth.c:437})
 * and {@code p->obj_k} ({@code player.c:498}, {@code player-birth.c:438}). Neither C assignment
 * validates or copies what it is given; both simply replace whatever the field held, and these tests
 * check that the port does the same, following {@link PlayerQuestsAndUpkeepAccessorTest}'s pattern for
 * the sibling {@code setQuests}/{@code setUpkeep} pair.
 *
 * <p>Class PlayerTimedAndItemKnowledgeAccessorTest coded on 260903, commented in full on 260903.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerTimedAndItemKnowledgeAccessorTest {

    /**
     * The character whose fields are written, fresh for each test.
     */
    private Player player;

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * {@link Player#setTimed(Map)}, the port of {@code p->timed = ...} ({@code player.c:497},
     * {@code player-birth.c:437}).
     */
    @Nested
    @DisplayName("setTimed")
    class SetTimed {

        /**
         * The map handed in is read back through {@link Player#getTimedEffect(TimedEffect)} - C's
         * assignment is a pointer swap, not a copy, so the entries stored are exactly the ones given.
         */
        @Test
        @DisplayName("stores the given map by identity")
        void storesByIdentity() {
            Map<TimedEffect, Integer> timed = new EnumMap<>(TimedEffect.class);
            timed.put(TimedEffect.TMD_BLIND, 12);

            player.setTimed(timed);

            assertEquals(12, player.getTimedEffect(TimedEffect.TMD_BLIND));
        }

        /**
         * A second call replaces the first map outright. C never has two live {@code p->timed}
         * arrays for the same character to merge between - {@code init_player} and
         * {@code player_birth} each {@code mem_zalloc} exactly one.
         */
        @Test
        @DisplayName("a second write replaces the first")
        void secondWriteReplaces() {
            Map<TimedEffect, Integer> first = new EnumMap<>(TimedEffect.class);
            first.put(TimedEffect.TMD_BLIND, 12);
            Map<TimedEffect, Integer> second = new EnumMap<>(TimedEffect.class);
            second.put(TimedEffect.TMD_AFRAID, 7);

            player.setTimed(first);
            player.setTimed(second);

            assertEquals(0, player.getTimedEffect(TimedEffect.TMD_BLIND));
            assertEquals(7, player.getTimedEffect(TimedEffect.TMD_AFRAID));
        }

        /**
         * An effect absent from the map reads as {@code 0} through the getter, matching C's zeroed
         * {@code mem_zalloc}'d slot for an effect that has never been set.
         */
        @Test
        @DisplayName("an effect absent from the map reads as zero")
        void absentEffectReadsAsZero() {
            player.setTimed(new EnumMap<>(TimedEffect.class));
            assertEquals(0, player.getTimedEffect(TimedEffect.TMD_CUT));
        }
    }

    /**
     * {@link Player#setItemKnowledge(KnownObject)}, the port of {@code p->obj_k = ...}
     * ({@code player.c:498}, {@code player-birth.c:438}).
     */
    @Nested
    @DisplayName("setItemKnowledge")
    class SetItemKnowledge {

        /**
         * The struct handed in is the struct held afterwards - the same reference, not a copy. C
         * assigns the pointer {@code object_new}/{@code mem_zalloc} returned; nothing about the
         * assignment itself inspects or duplicates the struct's contents.
         */
        @Test
        @DisplayName("stores the given struct by identity")
        void storesByIdentity() {
            KnownObject knowledge = new KnownObject();
            player.setItemKnowledge(knowledge);
            assertSame(knowledge, player.itemKnowledge);
        }

        /**
         * A second call replaces the first struct outright, discarding it rather than merging into
         * it - C's {@code init_player} and {@code player_birth} each allocate exactly one
         * {@code obj_k} for the character.
         */
        @Test
        @DisplayName("a second write replaces the first")
        void secondWriteReplaces() {
            KnownObject first = new KnownObject();
            KnownObject second = new KnownObject();

            player.setItemKnowledge(first);
            player.setItemKnowledge(second);

            assertSame(second, player.itemKnowledge);
            assertNotSame(first, player.itemKnowledge);
        }
    }
}
