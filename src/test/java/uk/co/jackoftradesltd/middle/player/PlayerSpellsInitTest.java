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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.magic.MagicSpell;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link Player#initSpellOrder(int, int)} and {@link PlayerMagic#playerSpellsInit(Player)},
 * the port of C's {@code player_spells_init} ({@code player-spell.c:139-153}).
 *
 * <p>C allocates {@code spell_flags} and {@code spell_order} together with two
 * {@code mem_zalloc} calls, so the two are always the same length and always freshly zeroed —
 * a second call gets a brand new block, not the old one topped up. That is the property this
 * suite is built around: {@link RepeatedAllocation} calls {@code initSpellOrder} twice on the
 * same player and checks that the second call replaces the first rather than appending to it,
 * which is exactly the divergence the port carried before {@code spellFlags.clear()} was added
 * alongside {@code spellOrder.clear()}.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerSpellsInitTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A spell distinguishable from every other only by identity - its stats don't matter here.
     *
     * @param name the spell's name
     * @return the spell
     */
    private static MagicSpell spell(String name) {
        return new MagicSpell(name, 1, 25, 1, 0, List.of(), "");
    }

    /**
     * A book holding the given number of spells.
     *
     * @param count how many spells the book holds
     * @return the book
     */
    private static MagicBook book(int count) {
        List<MagicSpell> spells = new ArrayList<>();
        for (int i = 0; i < count; i++) spells.add(spell("spell" + i));
        return new MagicBook(TValue.TV_MAGIC_BOOK, "Test Book", false, spells.size(),
                new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell", TValue.TV_MAGIC_BOOK),
                null, 0, 0, 0, 0, spells);
    }

    /**
     * A class whose books together hold {@code totalSpells} spells.
     *
     * @param totalSpells the class's total spell count
     * @return the class
     */
    private static PlayerClass casterClass(int totalSpells) {
        ClassMagic magic = totalSpells == 0
                ? ClassMagic.NONE
                : new ClassMagic(1, 300, 1, List.of(book(totalSpells)));
        return new PlayerClass("Test Class", List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                0, 0, 0, List.of(), magic);
    }

    /**
     * A new player for each test.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Reads one of the player's private {@code List<Integer>} fields.
     *
     * @param name the field's name ({@code spellOrder} or {@code spellFlags})
     * @return an unmodifiable snapshot of its current contents
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private List<Integer> listField(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return Collections.unmodifiableList(new ArrayList<>((List<Integer>) field.get(player)));
    }

    /**
     * The ordinary path: a caster class's total spell count drives both lists.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("playerSpellsInit for a caster")
    class CasterPath {

        /**
         * C fills every {@code spell_order} slot with {@code 99} and every {@code spell_flags}
         * slot with {@code 0}; both arrays are sized to the class's {@code total_spells}.
         */
        @Test
        @DisplayName("fills spellOrder with 99 and spellFlags with 0, sized to total spells")
        void fillsBothListsToTotalSpells() throws Exception {
            player.setClass(casterClass(3));

            PlayerMagic.playerSpellsInit(player);

            assertEquals(List.of(99, 99, 99), listField("spellOrder"));
            assertEquals(List.of(0, 0, 0), listField("spellFlags"));
        }

        /**
         * A single-spell class is the smallest non-zero case, and worth its own check since it
         * sits right next to the zero-spell early return.
         */
        @Test
        @DisplayName("a one-spell class allocates exactly one slot")
        void oneSpellClassAllocatesOneSlot() throws Exception {
            player.setClass(casterClass(1));

            PlayerMagic.playerSpellsInit(player);

            assertEquals(List.of(99), listField("spellOrder"));
            assertEquals(List.of(0), listField("spellFlags"));
        }
    }

    /**
     * The early-return branch: C's {@code if (!num_spells) return;} skips both
     * {@code mem_zalloc} calls entirely.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("playerSpellsInit for a non-caster")
    class NonCasterPath {

        /**
         * A class with no spellbooks has {@code total_spells == 0}, so C returns before touching
         * either array. The port's lists, which start empty from the constructor, must therefore
         * stay empty rather than being allocated at size zero through some other path.
         */
        @Test
        @DisplayName("leaves spellOrder and spellFlags untouched")
        void leavesListsEmpty() throws Exception {
            player.setClass(casterClass(0));

            PlayerMagic.playerSpellsInit(player);

            assertEquals(List.of(), listField("spellOrder"));
            assertEquals(List.of(), listField("spellFlags"));
        }
    }

    /**
     * The boundary found while verifying this port: C's {@code mem_zalloc} always hands back a
     * fresh, fully zeroed block, so a repeat call to {@code player_spells_init} (for instance
     * after the player's class changes) discards whatever the arrays held before rather than
     * growing them.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("initSpellOrder called more than once")
    class RepeatedAllocation {

        /**
         * A second, smaller allocation must leave both lists sized to the <em>new</em> count, not
         * the sum of the two calls. Before {@code spellFlags.clear()} was added next to
         * {@code spellOrder.clear()}, {@code spellFlags} kept growing across calls while
         * {@code spellOrder} correctly reset - this is exactly that divergence.
         */
        @Test
        @DisplayName("a smaller second call replaces the first rather than appending to it")
        void secondSmallerCallReplacesTheFirst() throws Exception {
            player.initSpellOrder(5, 99);
            player.initSpellOrder(3, 99);

            assertEquals(List.of(99, 99, 99), listField("spellOrder"));
            assertEquals(List.of(0, 0, 0), listField("spellFlags"));
        }

        /**
         * A previously-learned flag (a non-zero {@code spellFlags} entry) must not survive a
         * fresh allocation - C's {@code mem_zalloc} always zeroes, it never reuses the old block's
         * contents.
         */
        @Test
        @DisplayName("a stale learned flag is reset to zero by the next allocation")
        void staleFlagIsResetByNextAllocation() throws Exception {
            player.initSpellOrder(2, 99);
            Field flagsField = Player.class.getDeclaredField("spellFlags");
            flagsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Integer> flags = (List<Integer>) flagsField.get(player);
            flags.set(0, 1);

            player.initSpellOrder(2, 99);

            assertEquals(List.of(0, 0), listField("spellFlags"));
        }
    }
}
