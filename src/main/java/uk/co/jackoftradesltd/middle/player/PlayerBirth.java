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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;

/**
 * The character-creation machinery - the port of C's {@code player-birth.c}, minus its parsing, its
 * command handlers and the point-buy interface.
 *
 * <p>C's file is mostly the birth screen: the stat roller, the point costs, the {@code do_cmd_*}
 * handlers behind each key the player presses, and the roman-numeral suffixes on a reused character
 * name. None of that belongs to the model, and none of it is here. What is left is the part that
 * builds a character out of a race and a class, and so far that is one method,
 * {@link #embody}.
 *
 * <p>The methods are static and take the player, as C's take {@code struct player *p}: a character
 * is being built, so there is nothing yet to be a method on. The class is a namespace.
 *
 * <p>Class PlayerBirth commented in full on 260901.
 *
 * @author Rowan Crowther
 */
public class PlayerBirth {
    private static final Logger logger = LogManager.getLogger(PlayerBirth.class);

    /**
     * Gives this player the body their race is built with — the slots they can wear things in.
     *
     * <p><b>Copies rather than shares.</b> A race's body is a template held once and used by every
     * member of that race; a player's body holds the items actually worn. Taking the reference
     * instead of a copy would have every character of a race wearing the same equipment.
     *
     * <p>Returns quietly if the player has no race yet, which happens during character creation
     * before a race is chosen.
     *
     * <p>Function embody commented in full on 260820.
     *
     * @param player
     */
    public static void embody(Player player) {
        if (player.getRace() == null)
            return;

        player.setBody(player.getRace().getBody().copy());
    }
    
    /**
     * Rolls the character's age, height and weight - the port of C's {@code get_ahw}
     * ({@code player-birth.c:353}).
     *
     * <p>Three rolls from the race's own numbers, in that order. The age is
     * {@code b_age + randint1(m_age)}, so it is strictly above the base - a race's base age is a
     * floor the character is always at least a year past. The height and weight are
     * {@code Rand_normal(base, mod)} instead, where the base is a <em>mean</em>: it is reachable,
     * and about half of a race's characters fall below it.
     *
     * <p>C writes each of the last two with one chained assignment putting a single roll into both
     * the working field and the birth copy -
     * {@code p->ht = p->ht_birth = Rand_normal(p->race->base_hgt, p->race->mod_hgt)}
     * ({@code player-birth.c:359-360}). The port spells that as two calls, the second reading the
     * value back off the player, which keeps the one-roll-two-fields property that matters:
     * rolling a second time for the birth copy would leave a character whose recorded birth height
     * was not the height they were born at. The age has no birth copy - quickstart saves
     * {@code p->age} itself ({@code player-birth.c:153}).
     *
     * <p>C calls this from two places, and both take fresh values rather than reusing any:
     * {@code player_generate} when a character is built ({@code player-birth.c:1018}) and the
     * roller each time it produces a new candidate ({@code player-birth.c:1173}, whose comment
     * concedes it is only there by tradition). Calling it again on a live player therefore rerolls
     * all three, birth copies included.
     *
     * <p>The player's race must already be set; C asserts as much upstream, and the port would
     * throw here.
     *
     * <p>Function getAHW coded on 260902, commented in full on 260902.
     *
     * @param player the character being born
     */
    public static void getAHW(Player player) {
        // calculate age
        player.setAge(player.getRace().getBaseAge() + RandomValueUtils.randInt1(player.getRace().getModAge()));

        // height
        player.setHeight(RandomValueUtils.normal(player.getRace().getBaseHeight(), player.getRace().getModHeight()));
        player.setHeightBirth(player.getHeight());

        // weight
        player.setWeight(RandomValueUtils.normal(player.getRace().getBaseWeight(), player.getRace().getModWeight()));
        player.setWeightBirth(player.getWeight());
    }

    /**
     * Walks the history-chart graph and assembles the character's background - the port of C's
     * {@code get_history} ({@code player-birth.c:330}).
     *
     * <p>One chart is one sentence fragment. Starting at the race's chart, a d100 picks one of the
     * chart's entries, its phrase is appended, and generation moves to the successor chart; the
     * biography is finished when a chart has no successor. The Human's chain
     * {@code 1 -> 2 -> 3 -> 50 -> 51 -> 52 -> 53} ({@code history.txt}) is seven charts, so seven
     * rolls and seven fragments, which is why the shipped phrases carry their own leading capital
     * and trailing spaces - nothing is inserted between them.
     *
     * <p><b>The roll is a threshold, not a weight.</b> {@code roll <= entry.getRoll()} takes the
     * first entry the roll does not exceed, so the entries must ascend and the last must be
     * {@code 100}; chart 1's {@code 10/20/95/100} gives its four phrases 10%, 10%, 75% and 5%. The
     * order that makes this work is {@link PlayerHistoryChart#getEntries()}'s concern.
     *
     * <p>A chart whose entries stop short of {@code 100} can be rolled past, and C says so with
     * {@code assert(entry)} - which is a crash, and only in a build with asserts on. The port
     * throws instead: a data file that cannot answer a legal roll is a load-time fault worth
     * naming, not a silent null dereference.
     *
     * <p>Two divergences from C, both deliberate. The successor is read off the <em>chart</em>
     * rather than the chosen entry, because the port hoisted that edge up a level (see
     * {@link PlayerHistoryChart}). And a {@code null} chart yields the empty string where C yields
     * {@code NULL}; C's only callers pass {@code p->race->history}, which is never null, so the
     * difference has no caller to trouble.
     *
     * <p>Function getHistory coded on 260902, commented in full on 260902.
     *
     * @param chart the chart to start from, normally the player race's own; {@code null} gives the
     *              empty string
     * @return the assembled biography, the chosen phrases concatenated in chart order
     * @throws RuntimeException if a chart's entries do not cover the whole 1-100 range and the roll
     *                          falls past the last of them
     */
    public static String getHistory(PlayerHistoryChart chart) {
        StringBuilder result = new StringBuilder();

        while (chart != null) {
            int roll = RandomValueUtils.randInt1(100);

            PlayerHistoryEntry chosenEntry = null;
            for (PlayerHistoryEntry entry : chart.getEntries()) {
                if (roll <= entry.getRoll()) {
                    chosenEntry = entry;
                    break;
                }
            }
            if (chosenEntry == null) {
                String message = "Percentage chance greater than 100 found on PlayerHistoryEntry";
                logger.error(message);
                throw new RuntimeException(message);
            }

            result.append(chosenEntry.getText());
            chart = chart.getSuccessor();
        }
        
        return result.toString();
    }

    /**
     * Gives the character their starting money - the port of C's {@code get_money}
     * ({@code player-birth.c:391}).
     *
     * <p>Both purses open at the same figure, {@code player:start-gold} from
     * {@code constants.txt}, which the shipped data sets to 600. C says it in one chained
     * assignment, {@code p->au = p->au_birth = z_info->start_gold}; the port says it in two
     * statements, reading the working total back for the birth copy so that the two cannot drift
     * apart. C's chain assigns right to left, so it writes the birth copy first, but neither field
     * is read while the other is being written and the outcome is the same.
     *
     * <p>The order in birth matters more than the assignment order does. {@code get_money} runs
     * from {@code do_cmd_accept_character} ({@code player-birth.c:1256}), before
     * {@code player_outfit} ({@code player-birth.c:1298}) buys the starting kit and spends the
     * working total back down - so this is the gross sum, not what the character reaches the
     * dungeon with. It also overwrites whatever the point-based roller had put in the birth copy
     * (see {@link Player#setAUBirth(int)}).
     *
     * <p>C holds the constant as {@code uint16_t} and both fields as {@code int32_t}, so no
     * starting figure the data file can express is capable of overflowing either.
     *
     * <p>Function getMoney coded on 260902, commented in full on 260902.
     *
     * @param player the character being born, whose gold and birth gold are both set
     */
    public static void getMoney(Player player) {
        player.setAU(GameConstants.getPlayerStartGold());
        player.setAUBirth(player.getAU());
    }

    /**
     * Rolls the character's hit points for every level they will ever reach - the port of C's
     * {@code roll_hp} ({@code player-birth.c:279-308}).
     *
     * <p>A character's hit points are settled once, at birth, for all fifty levels. Gaining a level
     * later reads this table rather than rolling against it, so a run of bad luck at level thirty is
     * decided here, before the character has taken a step.
     *
     * <p><b>The acceptance window.</b> A straight run of fifty rolls would sometimes produce a
     * character too frail or too sturdy to be worth playing, so the finished table has to land
     * between two bounds on its top entry. The bounds are three-eighths and five-eighths of the
     * greatest total the die could give above one per level, plus one level's worth for each level:
     * {@code (PY_MAX_LEVEL * (hitdie - 1) * 3) / 8 + PY_MAX_LEVEL} and the same with five. For the
     * common ten-sided die that is 218 to 331 against an unconstrained range of 50 to 500 - roughly
     * the middle quarter. Both divisions truncate, and both operands are positive for any hit die a
     * data file can express, so C's truncation toward zero and Java's are the same rounding.
     *
     * <p><b>The retry loop.</b> Failing either bound throws the whole table away and rolls all of it
     * again; there is no cap on attempts and no adjustment of a table that came close. The bounds
     * are wide enough that this is not a practical concern. Note that the loop rolls indices one
     * upwards and never touches index zero: {@code player_generate} has already seeded that with the
     * full hit die ({@code player-birth.c:1003}), and leaving it alone is what keeps the level-one
     * total the same across every attempt. That makes the seeding a precondition of this method
     * rather than an incidental ordering - see {@link Player#setPlayerHitpoint(int, int)}.
     *
     * <p>The top index tested is {@code PY_MAX_LEVEL - 1}, the last one the loop writes, since the
     * table is indexed one below the character level. C carries a note here that the mid-level
     * totals could be constrained too; they are not, in C or in the port.
     *
     * <p>Rolling at birth rather than on level-up is also what stops a player from resetting the
     * birth screen until the rolls suit them: {@code player_generate} fills the levels it can see
     * with deliberate overestimates and leaves the real rolls to this method, which runs only when
     * the character is accepted ({@code player-birth.c:1237}).
     *
     * <p>Function rollHP coded on 260902, commented in full on 260902.
     *
     * @param player the character being born, whose whole hit point table is written
     */
    public static void rollHP(Player player) {
        // Minimum hit points at highest level
        int minValue = (PlayerRegistry.PY_MAX_LEVEL * (player.getHitDie() - 1) * 3) / 8;
        minValue += PlayerRegistry.PY_MAX_LEVEL;

        // Maximum hit points at highest level
        int maxValue = (PlayerRegistry.PY_MAX_LEVEL * (player.getHitDie() - 1) * 5) / 8;
        maxValue += PlayerRegistry.PY_MAX_LEVEL;

        // Roll out the hit points
        while (true) {
            // roll the hit point values
            for (int level = 1; level < PlayerRegistry.PY_MAX_LEVEL; level++) {
                int levelHP = RandomValueUtils.randInt1(player.getHitDie());
                player.setPlayerHitpoint(level, player.getPlayerHP(level - 1) + levelHP);
            }

            // Require "valid" hitpoints at highest level
            if (player.getPlayerHP(PlayerRegistry.PY_MAX_LEVEL - 1) < minValue) continue;
            if (player.getPlayerHP(PlayerRegistry.PY_MAX_LEVEL - 1) > maxValue) continue;

            // Acceptable values
            break;
        }
    }
}
