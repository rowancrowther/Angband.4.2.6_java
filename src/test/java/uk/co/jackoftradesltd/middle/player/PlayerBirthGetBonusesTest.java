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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.game.globals.registry.StatTables;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerBirth#getBonuses(Player)}, the port of C's {@code get_bonuses}
 * ({@code player-birth.c:311-324}).
 *
 * <p>The C is four lines:
 *
 * <pre>{@code
 * player->upkeep->update |= (PU_BONUS | PU_HP);
 * update_stuff(player);
 * player->chp = player->mhp;
 * player->csp = player->msp;
 * }</pre>
 *
 * <p><b>What is actually under test.</b> The arithmetic all belongs to the calculations the update
 * pass runs, and each of those has its own suite — {@link PlayerCalcHitpointsTest},
 * {@link PlayerCalcManaTest}, {@link PlayerUpdateBonusesTest} — with the dispatch itself pinned by
 * {@link PlayerUpdateStuffTest}. What is left here, and what these tests read off the C, is the
 * shape of the four lines: which two flags are raised, that one update pass services them, and that
 * the two fills come <em>after</em> that pass and take the maxima it has just produced rather than
 * the maxima that were there before.
 *
 * <p><b>The ordering is the whole point, so it needs a case that can tell the orders apart.</b> A
 * character whose stored maximum is already right would pass either way. So the fixture starts each
 * character with a deliberately wrong maximum — a stale {@code mhp} far above what the rolled table
 * and the constitution give — and a current total below both. C's order leaves the current total at
 * the newly computed maximum; the reversed order would leave it at the stale one, and a pass that
 * skipped the fill entirely would leave it where it started. All three are distinguishable.
 *
 * <p><b>Unconditional, not a clamp.</b> {@code calc_hitpoints} already pulls a current total down
 * to a falling maximum, but only when it is at or above it ({@code player-calcs.c:1588}); these two
 * lines have no such guard, so a character coming in <em>below</em> the maximum is raised to it.
 * That is the difference between healing and clamping, and the fixture's low starting current total
 * is what makes it visible. The mana side is checked in both directions: a caster is filled upwards
 * to a maximum greater than zero, and a character with no realm is pushed down to zero from a
 * current total the fixture set above it.
 *
 * <p><b>The flags are added, not assigned.</b> C's {@code |=} leaves anything already pending in
 * place, so one test raises an unrelated flag first and checks it survives the call still raised —
 * the update pass here runs with no generated character, so the map-side clauses are never reached
 * and cannot clear it.
 *
 * <p>{@link GameWorld#characterGenerated} is set false for the duration, which is both what makes
 * the pass stop after the six calculation clauses and what is actually true at birth; it is put back
 * afterwards so no other suite inherits it.
 *
 * <p>Class PlayerBirthGetBonusesTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerBirthGetBonusesTest {

    /**
     * The hit points the rolled table gives at every level, so the hit point calculation has
     * something to read.
     */
    private static final int HP_PER_LEVEL = 10;

    /**
     * A maximum no calculation could arrive at, stored before the call so that a fill taking the old
     * maximum rather than the new one is visible.
     */
    private static final int STALE_MAX = 999;

    /**
     * A current total below every maximum in play, so that the fill has to raise it rather than
     * clamp it.
     */
    private static final int STARTING_CURRENT = 3;

    /**
     * The level at which the test caster gains its first spell.
     */
    private static final int FIRST_SPELL_LEVEL = 5;

    /**
     * The level the caster is born at, above {@link #FIRST_SPELL_LEVEL} so its mana is not zero.
     */
    private static final int CASTER_LEVEL = 10;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * Whether a character was generated before the test, put back afterwards.
     */
    private boolean realCharacterGenerated;

    /**
     * A class that knows one spell, drawn from a single intelligence-scaled realm.
     *
     * @return the class
     */
    private static PlayerClass casterClass() {
        MagicRealm arcane = new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell",
                TValue.TV_MAGIC_BOOK);
        MagicBook book = new MagicBook(TValue.TV_MAGIC_BOOK, "Magic for Beginners", false,
                1, arcane, null, 0, 0, 0, 0, List.of());

        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<PlayerSkill, Integer> extra = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
            extra.put(skill, 0);
        }

        return new PlayerClass("Test Caster", List.of(), stats, skills, extra, 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(),
                new ClassMagic(FIRST_SPELL_LEVEL, 300, 1, List.of(book)));
    }

    /**
     * A plain character partway through birth: a rolled hit point table, wrong maxima, low current
     * totals, and no generated character.
     *
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    @BeforeEach
    void newCharacter() throws ReflectiveOperationException {
        player = new Player();
        CalcBonusesFixture.plainCharacter(player);

        int[] hitPoints = new int[50];
        for (int i = 0; i < hitPoints.length; i++) hitPoints[i] = HP_PER_LEVEL * (i + 1);
        set("playerHP", hitPoints);

        set("maxHP", STALE_MAX);
        set("currentHP", STARTING_CURRENT);
        set("maxSP", STALE_MAX);
        set("curSp", STARTING_CURRENT);

        realCharacterGenerated = GameWorld.characterGenerated;
        GameWorld.characterGenerated = false;
    }

    /**
     * Puts the generated-character flag back.
     */
    @AfterEach
    void restoreGlobals() {
        GameWorld.characterGenerated = realCharacterGenerated;
    }

    /**
     * Writes one of the player's private fields.
     *
     * @param name  the field's name
     * @param value the value to store
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private void set(String name, Object value) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * The hit points the calculation arrives at for the character as the fixture leaves it: the
     * level-one entry of the rolled table, plus a hundredth of the constitution row per level. The
     * bonus pass runs first, so the row read is the one for a neutral constitution.
     *
     * @return the maximum the pass should produce
     */
    private int expectedMaxHP() {
        return player.getPlayerHP(player.getLevel() - 1)
                + StatTables.adjConMhp[player.getPlayerState().getStatInd(Stats.STAT_CON)]
                * player.getLevel() / 100;
    }

    /**
     * The two flags C raises, and what the pass does with them.
     */
    @Nested
    @DisplayName("the flags")
    class Flags {

        /**
         * Both flags are raised and both are serviced, so neither is left pending afterwards.
         */
        @Test
        @DisplayName("PU_BONUS and PU_HP are both raised and both consumed")
        void bothFlagsConsumed() {
            PlayerBirth.getBonuses(player);

            assertFalse(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_BONUS),
                    "PU_BONUS should have been serviced by the pass");
            assertFalse(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_HP),
                    "PU_HP should have been serviced by the pass");
        }

        /**
         * C's {@code |=} adds; a flag raised beforehand that this pass cannot reach is still raised
         * afterwards, rather than having been assigned away.
         */
        @Test
        @DisplayName("a flag already pending survives the call")
        void pendingFlagSurvives() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_UPDATE_VIEW);

            PlayerBirth.getBonuses(player);

            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_UPDATE_VIEW),
                    "the flags are added, not assigned");
        }

        /**
         * The bonus pass installs a fresh state rather than leaving the one the character was
         * carrying, which is how the hit point clause sees the new constitution index.
         */
        @Test
        @DisplayName("the pass installs a recalculated state")
        void stateRecalculated() {
            PlayerState before = player.getPlayerState();

            PlayerBirth.getBonuses(player);

            assertTrue(before != player.getPlayerState(),
                    "PU_BONUS should have replaced the state object");
        }
    }

    /**
     * "Fully healed" - the third line of the C.
     */
    @Nested
    @DisplayName("fully healed")
    class FullyHealed {

        /**
         * The current total ends at the maximum the pass has just computed, not at the stale one it
         * came in with and not where it started.
         */
        @Test
        @DisplayName("current hit points end at the newly computed maximum")
        void healedToNewMaximum() {
            PlayerBirth.getBonuses(player);

            assertEquals(expectedMaxHP(), player.getMaxHP(),
                    "the update pass should have replaced the stale maximum");
            assertEquals(player.getMaxHP(), player.getCurrentHP(),
                    "the character should be fully healed");
        }

        /**
         * The distinguishing case: the fill runs after the update, so neither the stale maximum nor
         * the starting current total survives.
         */
        @Test
        @DisplayName("the stale maximum is not what the character is healed to")
        void notHealedToStaleMaximum() {
            PlayerBirth.getBonuses(player);

            assertFalse(player.getCurrentHP() == STALE_MAX,
                    "healing before the update would have copied the stale maximum");
            assertFalse(player.getCurrentHP() == STARTING_CURRENT,
                    "the fill should have raised the current total");
        }

        /**
         * The line is a fill, not a clamp: a character below the maximum is raised to it, where
         * {@code calc_hitpoints} on its own would have left it alone.
         */
        @Test
        @DisplayName("a character below the maximum is raised, not left")
        void raisesRatherThanClamps() {
            PlayerBirth.getBonuses(player);

            assertTrue(player.getCurrentHP() > STARTING_CURRENT,
                    "the fill is unconditional, not a downward clamp");
        }
    }

    /**
     * "Fully rested" - the fourth line of the C. The setter it writes through,
     * {@link Player#setCurSp(int)}, is pinned separately by {@link PlayerVitalsAccessorTest}; what
     * is checked here is only which value reaches it.
     */
    @Nested
    @DisplayName("fully rested")
    class FullyRested {

        /**
         * A character with no realm has a maximum of zero, so the rest line writes zero over the
         * current total the fixture set above it. No special case is needed for a non-caster.
         */
        @Test
        @DisplayName("a character with no realm is rested to zero")
        void noRealmRestsToZero() {
            PlayerBirth.getBonuses(player);

            assertEquals(0, player.getMaxSP(), "a class with no spells has no mana");
            assertEquals(0, player.getCurSp(), "the rest line should have written the zero");
        }

        /**
         * A caster is filled upwards to a maximum greater than zero, which is what shows the line
         * copying a real value rather than merely agreeing with a zero.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("a caster is rested to a maximum above zero")
        void casterRestsToItsMaximum() throws ReflectiveOperationException {
            set("playerClass", casterClass());
            set("level", CASTER_LEVEL);

            PlayerBirth.getBonuses(player);

            assertTrue(player.getMaxSP() > 0, "a caster past its first spell level has mana");
            assertEquals(player.getMaxSP(), player.getCurSp(),
                    "the character should be fully rested");
            assertTrue(player.getCurSp() > STARTING_CURRENT,
                    "the fill is unconditional, not a downward clamp");
        }

        /**
         * The stale maximum does not survive the pass, so it is not what the character is rested to.
         */
        @Test
        @DisplayName("the stale maximum is not what the character is rested to")
        void notRestedToStaleMaximum() {
            PlayerBirth.getBonuses(player);

            assertFalse(player.getCurSp() == STALE_MAX,
                    "resting before the update would have copied the stale maximum");
        }
    }
}
