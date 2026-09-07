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
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.enums.CommandContext;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandQueue;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterLore;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.*;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.*;

import static uk.co.jackoftradesltd.middle.player.PlayerUtils.modifyStatValue;

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

    private static final int MAX_BIRTH_POINTS = 20;

    /**
     * The point-buy price of a stat, indexed by <em>stat value + 1</em> - the port of C's
     * {@code birth_stat_costs} ({@code player-birth.c:679}).
     *
     * <p>A stat's own value is the index minus one, so entry 0 answers "what does base 9 cost"
     * and is never read: the point-buy screen starts every stat at 10 and can only sell down to
     * 10, never below. Reading at {@code stat + 1} rather than {@code stat} is what buys the next
     * point rather than pricing the one already held - {@code buy_stat} charges
     * {@code birth_stat_costs[stats_local[choice] + 1]} for raising a stat that currently sits at
     * {@code stats_local[choice]} ({@code player-birth.c:744}).
     *
     * <p>Costs are flat at one point per point through the climb from 10 to 16, then jump to 2 for
     * the point that reaches 17 and to 4 for the point that reaches 18 - the highest a stat can
     * reach through point-buy, and the array's last entry; there is no cost for 19. That is why the
     * C comment above the array notes it was feasible to autoroll a base 17 in three stats - the
     * array is shaped around what the roller could already produce, not an arbitrary curve, and it
     * is the last two points that are made deliberately expensive rather than the whole climb.
     *
     * <p>Ported here as a bare field because the point-buy screen itself is UI, not model, and is
     * out of scope for this class (see the class Javadoc) - {@code sell_stat} is still nothing more
     * than that C name here. {@link #resetStats} and {@link #buyStat} both read the field directly,
     * since pricing a point is a one-line array lookup rather than something worth wrapping.
     *
     * <p>Field birthStatCosts coded on 260901 / commented in full on 260906.
     */
    private static final int[] birthStatCosts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 4};

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
     * (see {@link Player#setAUBirth(long)}).
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

    /**
     * Recalculates the character's derived totals and then fills them - the port of C's
     * {@code get_bonuses} ({@code player-birth.c:311-324}).
     *
     * <p>Called at each point in birth where a choice has changed what the character is made of -
     * after the race and class are picked ({@code player-birth.c:697}), after the stats are rolled
     * ({@code player-birth.c:1047}), and at the end of the whole process
     * ({@code player-birth.c:1170, 1202}) - so that the birth screen always shows totals that match
     * the current choices rather than the previous ones.
     *
     * <p><b>Two flags, then one update pass.</b> {@code PU_BONUS} rebuilds the whole player state
     * (the stat totals, the skills, the speed) and {@code PU_MANA} is raised as a consequence of it,
     * not here; {@code PU_HP} recomputes the maximum hit points from the rolled table and the new
     * constitution bonus. Raising both before a single {@link PlayerCalcs#updateStuff} call rather
     * than calling twice matters, because the hit point calculation reads the constitution the bonus
     * pass has just settled: {@code updateStuff} handles {@code PU_BONUS} before {@code PU_HP}
     * ({@code PlayerCalcs.java:769, 779}), which is the order C's chain of {@code if} blocks gives.
     *
     * <p>The flags are raised, never replaced - {@link PlayerUpkeep#setUpdateFlagOn} is the port of
     * C's {@code |=} - so anything else already pending is serviced by the same pass.
     *
     * <p><b>Then fully healed and fully rested.</b> The two assignments are deliberately unclamped
     * and unconditional: a character being born has no history to preserve, so the current values
     * are simply set to the maxima the update pass has just produced. This must follow the update,
     * not precede it, or it would copy the previous maxima. Note that the fractional remainders
     * ({@code chp_frac}, {@code csp_frac}) are left alone; C does not clear them here either,
     * and at birth they are already zero.
     *
     * <p>A character with no spell realm gets a maximum of zero from the mana calculation, so the
     * rest line writes zero and the method needs no special case for one.
     *
     * <p>Function getBonuses coded on 260902, commented in full on 260902.
     *
     * @param player the character being born, whose state, maxima and current totals are all
     *               rewritten
     */
    public static void getBonuses(Player player) {
        // Calculate the bonuses and hitpoints
        player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_BONUS);
        player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_HP);

        // Update stuff
        PlayerCalcs.updateStuff(player);

        // Fully healed
        player.setCurrentHP(player.getMaxHP());

        // Fully rested
        player.setCurSp(player.getMaxSP());
    }

    /**
     * Rolls a fresh set of stats for a character being born - the port of C's {@code get_stats}
     * ({@code player-birth.c:231}). This is the standard roller: {@code do_cmd_roll_stats} calls it
     * for each re-roll ({@code player-birth.c:1167}), and the point-based birth path does not use it
     * at all.
     *
     * <p>Fifteen dice are rolled at once, three per stat, and the sizes cycle with the index:
     * {@code randint1(3 + index % 3)} gives a d3, a d4 and a d5 to each stat in turn. The three
     * belonging to a stat are consecutive - {@code dice[3i]}, {@code dice[3i + 1]},
     * {@code dice[3i + 2]} - so the coefficient three is the stride, and every stat draws its own
     * d3, d4 and d5 rather than a mixture of its neighbours'. Added to a base of 5, that gives a
     * value of 8 to 17 per stat, before race and class.
     *
     * <p>The whole set is rejected and re-rolled unless the fifteen dice total strictly between
     * {@code 7 * STAT_MAX} and {@code 9 * STAT_MAX} - 36 to 44 inclusive, against a possible range
     * of 15 to 60. Both comparisons are strict, so 35 and 45 are both rejected. The accumulator is
     * declared inside the loop precisely so that it starts at zero on every attempt; C resets it in
     * the {@code for} initialiser, {@code for (j = i = 0; ...)} ({@code player-birth.c:239}). A
     * total carried between attempts could never fall back inside the window, and the loop would
     * never terminate.
     *
     * <p>The roll is a rejection sampler with no attempt limit in either version, which is safe
     * because the window sits around the mean: a d3, a d4 and a d5 average 7.5 between them, so
     * fifteen dice average 37.5 and an acceptable set turns up in a handful of attempts.
     *
     * <p>Five things are written per stat, in C's order: the rolled maximum, the current value
     * seeded equal to it, the identity entry in the scramble map, the caller's working value, and
     * the birth record. The working value is where the comment in C about including "a chunk of
     * {@code calc_bonuses()}" comes in - the race and class adjustments are applied through
     * {@link PlayerUtils#modifyStatValue} here so the birth screen can show usable figures without
     * a full recalculation, and {@link PlayerCalcs} will compute the same thing properly later.
     * Note that the bonus reaches only {@code statUse}: the stat stored on the player stays the
     * bare rolled value.
     *
     * <p>Where C walks {@code i} from zero to {@code STAT_MAX}, the port walks the enum and skips
     * the two sentinels, which covers exactly the five real stats in the same order.
     *
     * <p>Function getStats commented in full on 260902.
     *
     * @param player  the character being born; their maxima, current values, scramble map and birth
     *                record are all overwritten
     * @param statUse receives the rolled values with the racial and class adjustments applied, one
     *                entry per real stat; the caller owns the map, and existing entries for the five
     *                stats are replaced
     */
    public static void getStats(Player player, Map<Stats, Integer> statUse) {
        int[] dice = new int[3 * Stats.STAT_MAX.getValue()];

        // roll and verify some stats
        while (true) {
            int total = 0;
            // roll some dice
            for (int index = 0; index < 3 * Stats.STAT_MAX.getValue(); index++) {
                // Roll the dice
                dice[index] = RandomValueUtils.randInt1(3 + index % 3);

                // collect the totals
                total += dice[index];
            }

            // Verify totals
            if (total > 7 * Stats.STAT_MAX.getValue() && total < 9 * Stats.STAT_MAX.getValue()) break;
        }

        // Roll the stats
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            int bonus;
            int idx = stat.getValue();

            // extract 5 + 1d3 + 1d4 + 1d5
            int maxValue = 5 + dice[3 * idx] + dice[3 * idx + 1] + dice[3 * idx + 2];

            // Save that value
            player.setStatMax(stat, maxValue);

            // Obtain a bonus for race and class
            bonus = player.getRace().getStatAdjust(stat) + player.getPlayerClass().getStatsAdj(stat);

            // Start fully healed
            player.setCurrStatValue(stat, player.getMaxStatValue(stat));

            // Start with unscrambple stats
            player.setCurrStatMap(stat, stat);

            // Efficienvcy - apply the racial/class bonuses
            statUse.put(stat, modifyStatValue(player.getMaxStatValue(stat), bonus));

            player.setStatBirth(stat, player.getStatMax(stat));
        }
    }

    /**
     * Fleshes out a character from the race and class chosen so far - the port of C's
     * {@code player_generate} ({@code player-birth.c:980-1028}).
     *
     * <p>This runs every time a choice is made on the birth screen, not once at the end of it. C
     * calls it when the screen is reset, when a race is picked and when a class is picked
     * ({@code player-birth.c:1042, 1079, 1099, 1110}), so it has to be able to overwrite the
     * results of its own previous run rather than assuming a blank character. That is why each
     * step assigns outright instead of accumulating, and why the level-1 hit point entry is
     * rewritten below even though a hit point table may already exist from an earlier choice.
     *
     * <p>A {@code null} race or class means "keep what the player already has", which is how the
     * two single-choice callers work: picking a class passes {@code null} for the race and leaves
     * it standing. The port hands the player a {@link PlayerRace#copy} and
     * {@link PlayerClass#copy} where C assigns the pointer to the shared definition, so a
     * character owns its race and class rather than aliasing the registry's.
     *
     * <p>What is derived, in C's order: the character is set to level 1; the experience factor is
     * the race's plus the class's; the hit die is likewise the sum of the two contributions. The
     * level-1 entry of the hit point table is then the whole hit die - a character does not roll
     * for their first level - and the loop above level 1 fills in <em>overestimates</em>, a full
     * hit die per level rather than a roll. That is deliberate: rolling here would let a player
     * reset the birth screen until the rolls came out well, so the real rolls are left to
     * {@link #rollHP(Player)} once the character is committed. Since the level is 1 the loop never
     * actually runs, and maximum hit points come from the single entry it skipped over.
     *
     * <p>Age, height and weight are then rolled, and the character starts one point below a full
     * stomach. That last write goes straight into the timed-effect map rather than through
     * {@link PlayerTimed#setTimed}, matching C's raw {@code p->timed[TMD_FOOD] =}
     * ({@code player-birth.c:1021}): there is no character yet for the grade-change messages and
     * recalculations to be about.
     *
     * <p>The history is regenerated last, unless {@code oldHistory} says to keep the one already
     * there - the flag quickstart sets when it restores a previous character, so that a replayed
     * background is not silently rerolled.
     *
     * <p>Function playerGenerate commented in full on 260902.
     *
     * @param player      the character to flesh out; must not be {@code null}
     * @param race        the race to apply, or {@code null} to keep the player's current one
     * @param playerClass the class to apply, or {@code null} to keep the player's current one
     * @param oldHistory  {@code true} to leave the existing history text alone, {@code false} to
     *                    roll a fresh one from the race's chart
     * @throws RuntimeException if {@code player} is {@code null}
     */
    public static void playerGenerate(Player player, PlayerRace race,
                                      PlayerClass playerClass, boolean oldHistory) {
        if (player == null) {
            String message = "Trying to generate on a null player";
            logger.error(message);
            throw new RuntimeException(message);
        }

        if (playerClass == null) playerClass = player.getPlayerClass();
        if (race == null) race = player.getRace();

        player.setClass(playerClass.copy());
        player.setRace(race.copy());

        // Level 1
        player.setMaxLevel(1);
        player.setLevel(1);

        // Experience factor
        player.setExpFact(player.getRace().getExpFactor() + player.getPlayerClass().getExpFactor());

        // Hitdice
        player.setHitDie(player.getRace().getMaxHitDie() + player.getPlayerClass().getMaxHitDie());

        // Pre calculate level 1 hitdice
        player.setPlayerHitpoint(0, player.getHitDie());

        /*
         * Fill in overestimates of hitpoints for additional levels.  Do not
         * do the actual rolls so the player can not reset the birth screen
         * to get a desirable set of initial rolls.
         */
        for (int level = 1; level < player.getLevel(); level++) {
            player.setPlayerHitpoint(level, player.getPlayerHP(level - 1) + player.getHitDie());
        }

        // Initial hitpoints
        player.setPlayerMaxHP(player.getPlayerHP(player.getLevel() - 1));

        // Roll for age/weight/height
        getAHW(player);

        // Always start with a well-fed player
        player.putTimed(TimedEffect.TMD_FOOD, PlayerRegistry.getPyFoodFull() - 1);

        if (!oldHistory) {
            player.setHistoryBirth(getHistory(player.getRace().getHistory()));
        }
    }

    /**
     * Resets a player to Angband's blank starting state - the port of C's {@code player_init}
     * ({@code player-birth.c:396}). This runs before {@link #playerGenerate} does the actual
     * character build, so what a fresh character inherits from here is the baseline every
     * race/class combination starts from: no artifacts made, no quests underway, every object
     * kind untried and every monster race unkilled.
     *
     * <p>{@link Player#wipe} stands in for C's {@code memset(p, 0, sizeof(struct player))}. It
     * mutates every field on the object the caller already holds rather than allocating a new
     * one, because reassigning a Java parameter is only ever visible inside this method - unlike
     * C's pointer write, the caller would never see it.
     *
     * <p>The object-kind and monster-race loops both start one element past the port's own
     * index 0, matching C's {@code for (i = 1; ...)} bound at {@code player-birth.c:415,421}.
     * C's tables carry a synthetic zeroth entry - {@code k_info[0]} is the {@code <pile>}
     * sentinel kind, {@code r_info[0]} is the {@code <player>} sentinel race used only to hold
     * the minimap glyph colour - and {@code player_init} explicitly skips both. The port's kind
     * and race lists carry the same sentinel entries first, in the same file order, so the loops
     * here skip index 0 for the same reason.
     *
     * <p>The player's options are saved before the wipe and restored after it, matching C's
     * {@code opts_save}/{@code p->opts = opts_save}: a fresh player still keeps whatever options
     * were already in force. The upkeep, timed-effect table and item-knowledge (brand/slay/curse)
     * records are then rebuilt to size, and the player is left pointed at the first race and
     * class in the edit files with an unshapechanged {@code "normal"} shape, exactly as C leaves
     * {@code p->race}, {@code p->class} and {@code p->shape}.
     *
     * <p>Outstanding: nothing calls this yet. C's {@code player_init} runs once at game start,
     * before the birth screen; nothing in the port's birth flow reaches this method.
     *
     * <p>Function playerInit coded on 260903, commented in full on 260903.
     *
     * @param player the character to reset to Angband's starting baseline
     */
    private static void playerInit(Player player) {
        PlayerOptions optionsSave = player.getPlayerOptions().copy();

        // Wipe the player
        player.wipe();

        // Start with no artifacts made yet
        for (Artifact art : ObjectRegistry.getArtifacts()) {
            ObjectUtils.markArtifactCreated(art, false);
            ObjectUtils.markArtifactSeen(art, false);
        }

        // Quests
        PlayerQuest.playerQuestsReset(player);

        int index = 0;
        for (ObjectKind kind : ObjectRegistry.getObjectKinds()) {
            if (index == 0) {
                index++;
                continue;
            }
            kind.setTried(false);
            kind.setAware(false);
        }

        index = 0;
        for (MonsterRace race : MonsterRegistry.getMonsterRaces()) {
            if (index == 0) {
                index++;
                continue;
            }
            MonsterLore lore = race.getLore();
            race.setCurNum(0);
            race.setMaxNum(100);
            if (race.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE))
                race.setMaxNum(1);
            lore.setPSkills(0);
            lore.setThefts(0);
        }

        PlayerUpkeep playerUpkeep = new PlayerUpkeep();
        player.setUpkeep(playerUpkeep);
        playerUpkeep.setQuiverObjects(new ItemObject[GameConstants.getCarryCapQuiverSize()]);
        playerUpkeep.setInventory(new ItemObject[GameConstants.getCarryCapPackSize() + 1]);
        Map<TimedEffect, Integer> timed = new HashMap<>();
        for (TimedEffect effect : TimedEffect.values()) {
            timed.put(effect, 0);
        }
        player.setTimed(timed);
        KnownObject itemKnowledge = new KnownObject();
        itemKnowledge.initBrands();
        itemKnowledge.initSlays();
        itemKnowledge.initCurses();
        player.setItemKnowledge(itemKnowledge);

        // Options should persist
        player.setOptions(optionsSave);

        // First turn
        GameState.setTurn(1);
        player.setTotalEnergy(0);
        player.setRestingTurn(0);

        // Default ot the first race/class in the edit file
        PlayerRace race = PlayerRegistry.getPlayerRaces().getFirst();
        PlayerClass playerClass = PlayerRegistry.getPlayerClasses().getFirst();
        player.setClass(playerClass);
        player.setRace(race);

        // Player starts unshapechanged
        player.setShape(PlayerRegistry.lookupPlayerShape("normal"));
    }

    /**
     * Drives a whole birth through the command queue in one call, as if a player had picked a
     * race, a class and a name at the birth screen and accepted the result - the port of C's
     * {@code player_make_simple} ({@code player-birth.c:523}). Angband's test suite and its
     * spoiler-file generator both use this to get a fully-initialised player without a UI.
     *
     * <p>{@code raceName} and {@code className} are resolved to the index of the matching entry
     * in {@link PlayerRegistry#getPlayerRaces()} / {@link PlayerRegistry#getPlayerClasses()},
     * found by a forward scan for a name match, returning {@code false} if the list runs out
     * first. C does the equivalent lookup against its {@code races}/{@code classes} linked lists,
     * but then rewrites the found position with {@code ir = nr - ir - 1} ({@code
     * player-birth.c:543}) before using it as a choice index. That rewrite exists only because
     * C's lists are built by prepending each parsed entry, so a forward walk from the list head
     * finds entries in the reverse of the file's order and needs converting back. The port's race
     * and class lists are built by appending in file order (see {@code PlayerRaceAssembler},
     * {@code PlayerClassAssembler}), so index 0 already means the first entry in the edit file on
     * both sides, and the forward-scan index can be used directly with no rewrite needed. A
     * {@code null} name leaves the corresponding index at 0, matching C's untouched {@code ir}/
     * {@code ic} when {@code nrace}/{@code nclass} is {@code NULL} - both mean "first in the edit
     * file".
     *
     * <p>The resolved indices, and the player name ({@code "Simple"} when {@code playerName} is
     * {@code null}, matching C's own fallback), are pushed onto the command queue as {@code
     * CMD_BIRTH_INIT}, {@code CMD_BIRTH_RESET}, {@code CMD_CHOOSE_RACE}, {@code CMD_CHOOSE_CLASS},
     * {@code CMD_NAME_CHOICE} and {@code CMD_ACCEPT_CHARACTER} in that order, then executed under
     * {@link CommandContext#CTX_BIRTH} - the same sequence and order as C's {@code cmdq_push}
     * calls at {@code player-birth.c:564-574}.
     *
     * <p>Outstanding: nothing calls this yet; it exists for the same test/spoiler-harness use C
     * puts it to, and the port has neither yet.
     *
     * <p>Function playerMakeSimple coded on 260903, commented in full on 260904.
     *
     * @param player     the character to build; passed through unused to the command handlers
     *                   that do the actual building, matching C's implicit use of the global
     *                   {@code player}
     * @param raceName   the race to select by name, or {@code null} to use the first race in the
     *                   edit file
     * @param className  the class to select by name, or {@code null} to use the first class in
     *                   the edit file
     * @param playerName the name to give the character, or {@code null} to default to
     *                   {@code "Simple"}
     * @return {@code true} once the birth commands have been queued and executed; {@code false}
     * if {@code raceName} or {@code className} was given but matched no entry in the edit files
     */
    public static boolean playerMakeSimple(Player player, String raceName, String className, String playerName) {
        int raceIndex = 0;
        List<PlayerRace> races = PlayerRegistry.getPlayerRaces();
        int raceNum = races.size();

        if (raceName != null) {
            while (true) {
                if (raceIndex >= races.size()) return false;
                if (races.get(raceIndex).getName().equals(raceName)) break;
                raceIndex++;
            }
        }

        int classIndex = 0;
        List<PlayerClass> classes = PlayerRegistry.getPlayerClasses();
        int classNum = classes.size();

        if (className != null) {
            while (true) {
                if (classIndex >= classes.size()) return false;
                if (classes.get(classIndex).getName().equals(className)) break;
                classIndex++;
            }
        }

        CommandQueue commandQueue = GameState.getCommandQueue();
        commandQueue.push(CommandCode.CMD_BIRTH_INIT);
        commandQueue.push(CommandCode.CMD_BIRTH_RESET);
        commandQueue.push(CommandCode.CMD_CHOOSE_RACE);
        Command command = commandQueue.commandQueuePeek();
        command.setArgChoice("choice", raceIndex);
        commandQueue.push(CommandCode.CMD_CHOOSE_CLASS);
        command = commandQueue.commandQueuePeek();
        command.setArgChoice("choice", classIndex);
        commandQueue.push(CommandCode.CMD_NAME_CHOICE);
        command = commandQueue.commandQueuePeek();
        command.setArgString("name", playerName == null ? "Simple" : playerName);
        commandQueue.push(CommandCode.CMD_ACCEPT_CHARACTER);
        commandQueue.execute(CommandContext.CTX_BIRTH);

        return true;
    }

    /**
     * Gives a freshly-born character their starting kit - the port of C's {@code player_outfit}
     * ({@code player-birth.c:586-672}). Having an item identifies it and makes the player aware of
     * its purpose, so this is also where a character's earliest object knowledge comes from.
     *
     * <p><b>Obvious knowledge first.</b> Before any item exists, the player's {@link KnownObject} is
     * given the three properties that are never worth hiding - damage dice, damage sides and armour
     * class always read as their true values - and then every flag whose {@link ObjectFlagType}
     * subtype is {@code OFT_LIGHT}, {@code OFT_DIG}, {@code OFT_THROW} or {@code OFT_CURSE_ONLY} is
     * marked known. C loops {@code i} from 1 to {@code OF_MAX} exclusive, skipping the zeroth
     * sentinel flag; the port walks the enum and excludes {@code OF_NONE} and {@code OF_MAX} by name
     * instead, which is the same range. C dereferences the looked-up {@code obj_property} with no
     * null check; the port skips a flag whose property is missing rather than risk a
     * {@code NullPointerException} - a difference that never bites while the data file registers a
     * property for every flag, which it does.
     *
     * <p><b>The starting-equipment loop.</b> Each of the class's {@link StartItem} entries rolls a
     * quantity, looks up its {@link ObjectKind}, and - unless {@code birth_start_kit} is on - is
     * skipped outright unless it is food or light, in which case exactly one is granted regardless of
     * the rolled quantity. A {@code null} kind is a data-file fault, not a runtime possibility, so the
     * port throws where C only asserts.
     *
     * <p><b>Exclusion options.</b> A {@link StartOptionExclusion} that is not negated excludes the
     * item when its option is set; one that is negated excludes it when the option is <em>not</em>
     * set - matching C's sign-encoded {@code eopts} array, where a positive entry tests
     * {@code p->opts.opt[i]} directly and a negative one tests the negation of
     * {@code p->opts.opt[-i]}. The port evaluates every entry rather than stopping at the first
     * exclusion, but continuing past a already-excluded entry cannot change {@code included} back to
     * {@code true}, so the result is the same as C's short-circuiting {@code while} loop.
     *
     * <p>What survives both checks is built as a fresh {@link ItemObject}, given a matching
     * {@code known} shadow, made base-known and flavour-aware, and marked
     * {@link ObjectNotice#OBJ_NOTICE_ASSESSED} - all before it is priced and paid for out of the
     * gold {@link #getMoney} set earlier, and carried into the gear. The kind itself is marked
     * {@code everSeen} regardless of how many of it were granted.
     *
     * <p>Spending can drive gold negative if the starting kit is expensive enough, so the total is
     * clamped to zero afterwards rather than checked before each purchase - C does the same, one
     * clamp at the end rather than a guard per item. Finally {@link #wieldAll} tries to equip
     * whatever was carried, and {@link PlayerKnowledge#updateObjectKnowledge} brings the player's
     * overall knowledge state in line with everything just granted.
     *
     * <p>Function playerOutfit coded on 260905, commented in full on 260905.
     *
     * @param player the character being born, whose knowledge, gear and gold are all set from
     *               scratch
     */
    public static void playerOutfit(Player player) {
        ItemObject known;

        // Currently carrying nothing
        player.getPlayerUpkeep().setTotalWeight(0);

        // Give the player obvious object knowledge
        KnownObject itemKnowledge = player.getItemKnowledge();
        itemKnowledge.setDD(1);
        itemKnowledge.setDS(1);
        itemKnowledge.setAC(1);

        for (ObjectFlag flag : ObjectFlag.values()) {
            if (flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX) continue;
            ObjPropertyType flagType = ObjPropertyType.OBJ_PROPERTY_FLAG;
            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(flagType, flag);
            ObjectProperty property = ObjectRegistry.lookupObjectProperty(flagType, wrapper);
            if (property == null) continue;
            if (property.getSubtype() == ObjectFlagType.OFT_LIGHT || property.getSubtype() == ObjectFlagType.OFT_DIG
                    || property.getSubtype() == ObjectFlagType.OFT_THROW || property.getSubtype() == ObjectFlagType.OFT_CURSE_ONLY)
                itemKnowledge.learnFlag(flag);
        }

        // Starting equipment
        for (StartItem start : player.getPlayerClass().getStartItems()) {
            int num = RandomValueUtils.randRange(start.getMin(), start.getMax());
            ObjectKind kind = ObjectUtils.lookupKind(start.gettValue(), start.getsValue());

            if (kind == null) {
                String message = "Null kind obtained from start item: " + start.gettValue();
                logger.error(message);
                throw new RuntimeException(message);
            }

            // Without start kit, only start with 1 food and 1 light
            if (!player.getPlayerOptions().has(PlayerOptionEnum.OP_birth_start_kit)) {
                if (!kind.gettValue().isFood() && !kind.gettValue().isLight())
                    continue;

                num = 1;
            }

            // Exclude if configured to do so based on birth options
            if (start.geteOpts() != null && !start.geteOpts().isEmpty()) {
                boolean included = true;

                for (StartOptionExclusion exclusion : start.geteOpts()) {
                    if (!included) continue;

                    if (!exclusion.negated()) {
                        if (player.getPlayerOptions().has(exclusion.option())) {
                            included = false;
                        }
                    } else if (!player.getPlayerOptions().has(exclusion.option()))
                        included = false;
                }
                if (!included) continue;
            }

            // prepare a new object
            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 0, DamageAspect.MINIMIZE);
            obj.setNumber(num);
            obj.setOrigin(ObjectOriginEnum.ORIGIN_BIRTH);

            known = new ItemObject();
            obj.setKnown(known);
            PlayerKnowledge.objectSetBaseKnown(player, obj);
            PlayerKnowledge.flavourAware(player, obj);
            known.setpValue(obj.getNumber());
            known.setEffect(obj.getEffect());
            known.orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);

            // deduct cost of item from starting gold
            player.setAU(player.getAU() - obj.objectValueReal(obj.getNumber()));

            // Carry the item
            ObjectGear.invenCarry(player, obj, true, false);
            kind.setEverSeen(true);
        }

        // Sanity check
        if (player.getAU() < 0)
            player.setAU(0);

        // Now try wielding everything
        wieldAll(player);

        // Update knowledge
        PlayerKnowledge.updateObjectKnowledge(player);
    }

    /**
     * Tries to wield everything wieldable in the gear pile — the port of C's {@code wield_all}
     * ({@code player-birth.c:463}). Called once at the end of birth, after the starting kit has
     * been bought and carried, so every newly-acquired item gets a pass at going into an equipment
     * slot before play begins.
     *
     * <p>The scan is a single pass over the gear: an item is worn only if {@link ItemObject#wieldSlot()}
     * names a slot that exists on this body and that slot is currently empty. A stack of more than
     * one is split first — one goes on the body, the rest is held back in {@code newPile} /
     * {@code newKnownPile} rather than being appended to the gear mid-scan, because the C original
     * also defers the merge to the end ({@code pile_insert_end} after the loop) to avoid the new
     * split object being walked again by the same {@code for (obj = p->gear; ...)} traversal this
     * scan mirrors.
     *
     * <p>The slot-bounds check is {@code slotNum < 0 || slotNum >= size()}, matching C's
     * {@code slot < 0 || slot >= p->body.count}: {@code size()} itself is one past the last valid
     * index, so it must be excluded, not just exceeded.
     *
     * <p>Function wieldAll coded on 260819, commented in full on 260905.
     *
     * @param player the player whose gear is being wielded
     */
    private static void wieldAll(Player player) {
        Pile newPile = new Pile();
        Pile newKnownPile = new Pile();

        // Scan through the gear
        Iterator<ItemObject> it = player.getGear().getIterator();
        while (it.hasNext()) {
            ItemObject obj = it.next();
            // Skip non-objects
            if (obj == null) continue;

            // Make sure we can wield it
            int slotNum = obj.wieldSlot();
            if (slotNum < 0 || slotNum >= player.getPlayerBody().getSlots().size())
                continue;

            EquipSlot slot = player.getPlayerBody().getSlot(slotNum);
            ItemObject tempObj = slot.getItem();

            if (tempObj != null)
                continue;

            // Split if necessary
            if (obj.getNumber() > 1) {
                // All but 1 go to the new object
                ItemObject newObj = obj.objectSplit(obj.getNumber() - 1);

                // Add to the pile of new objects to carry
                newPile.insert(newObj);
                newKnownPile.insert(newObj.getKnown());
            }

            // Wear the new stuff
            slot.setItem(obj);
            PlayerKnowledge.objectLearnOnWield(player, obj);

            // Increment the equipment counter by hand
            player.getPlayerUpkeep().setEquipCount(player.getPlayerUpkeep().getEquipCount() + 1);
        }

        // Add the unwielded split items to the gear
        if (newPile != null) {
            player.getGear().insertEnd(newPile);
            player.getGearKnown().insertEnd(newKnownPile);
        }
    }

    /**
     * Recalculates the derived character state from a set of point-buy stat values - the port of
     * C's {@code recalculate_stats} ({@code player-birth.c:685-707}).
     *
     * <p>Writes each of the five real stats' current, maximum and birth values from the supplied
     * map, resets the scramble map to the identity permutation, derives the birth gold from the
     * points left unspent, and then calls {@link #getBonuses} and signals the UI so every display
     * driven by these totals catches up. The stat loop skips {@code STAT_NONE} and {@code STAT_MAX}
     * - the same guard {@link #getStats} uses on the same enum - because {@link Stats#values()}
     * enumerates those two sentinels alongside the five real stats, and {@code statsLocalLocal}
     * carries no entry for either.
     *
     * <p>Where C reaches for the global {@code player}, this is the one method in the class that
     * fetches the equivalent through {@link GameState#getPlayer()} rather than taking the character
     * as a parameter, since C's version is likewise free-standing rather than a method on the
     * struct.
     *
     * <p>Gold is the inverse of point-buy cost - C's own comment reads "gold is inversely
     * proportional to cost": {@code z_info->start_gold + (50 * points_left_local)}, so the fewer
     * points spent on stats, the more gold is left over. See {@link Player#setAUBirth} for why this
     * figure never reaches the started game: every C caller of {@code recalculate_stats} is one of
     * the point-buy birth commands, and accepting the character re-derives the plain starting gold
     * afterwards.
     *
     * <p>Those callers - {@code buy_stat}, {@code sell_stat} and {@code reset_stats} - are the
     * point-buy birth screen and stay out of the model's scope, so this method has no caller yet.
     *
     * <p>Function recalculateStats commented in full on 260905.
     *
     * @param statsLocalLocal the point-buy stat values, one entry per real stat
     * @param pointsLeftLocal the unspent point-buy points, used to derive the starting gold
     */
    public static void recalculateStats(Map<Stats, Integer> statsLocalLocal, int pointsLeftLocal) {
        Player player = GameState.getPlayer();

        // Variable stat maxes
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_MAX || stat == Stats.STAT_NONE) continue;
            
            player.setCurrStatValue(stat, statsLocalLocal.get(stat));
            player.setStatMax(stat, statsLocalLocal.get(stat));
            player.setStatBirth(stat, statsLocalLocal.get(stat));
            player.setCurrStatMap(stat, stat);
        }

        // Gold is inversely proportional to cost
        player.setAUBirth(GameConstants.getPlayerStartGold() + (50 * pointsLeftLocal));

        // Update bonuses, hp, etc
        getBonuses(player);

        // Tell the UI about all the stuff that's changed
        EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
        eventsHandler.eventSignal(GameEventType.EVENT_GOLD);
        eventsHandler.eventSignal(GameEventType.EVENT_AC);
        eventsHandler.eventSignal(GameEventType.EVENT_HP);
        eventsHandler.eventSignal(GameEventType.EVENT_STATS);
    }

    /**
     * Resets the point-buy stats to their starting values - the port of C's
     * {@code reset_stats} ({@code player-birth.c:710-735}).
     *
     * <p>Every real stat goes back to base 10 with nothing spent on it yet, and its increment
     * cost is reseeded from {@link #birthStatCosts} at <em>value + 1</em> - the same offset
     * {@code buy_stat} uses when pricing the point it is about to buy
     * ({@code player-birth.c:746}). Reading each stat's own freshly-set value, rather than some
     * other stat's, is what keeps this in step with C, where {@code stats_local[i]} and
     * {@code points_inc_local[i]} share the one index throughout the loop. The stat loop skips
     * {@code STAT_NONE} and {@code STAT_MAX}, the same guard {@link #recalculateStats} and
     * {@link #getStats} use on the same enum.
     *
     * <p>C takes the running point total as {@code int *points_left_local} and writes
     * {@link #MAX_BIRTH_POINTS} through the pointer, so every caller's own variable is updated
     * by the call regardless of what it held going in. The port has no by-reference {@code int},
     * so it returns the new total instead and ignores whatever {@code pointsLeftLocal} arrived
     * with - a caller has to assign the result rather than read a mutated parameter, unlike
     * every C caller of {@code reset_stats} ({@code player-birth.c:1094,1105,1116,1148}).
     *
     * <p>{@code updateDisplay} is the same UI guard {@code buy_stat} and {@code sell_stat} take -
     * when set, this also calls {@link #recalculateStats} and signals the birthpoints event, so
     * the point-buy screen catches up with the reset.
     *
     * <p>This is the point-buy birth screen and stays out of the model's scope, so this method
     * has no caller yet.
     *
     * <p>Function resetStats coded on 260906, commented in full on 260906.
     *
     * @param statsLocal       the point-buy stat values, one entry per real stat, overwritten in place
     * @param pointsSpentLocal the points spent per stat, overwritten in place
     * @param pointsIncLocal   the cost of the next point per stat, overwritten in place
     * @param pointsLeftLocal  the caller's running point total; its incoming value is discarded,
     *                         since the reset always replaces it
     * @param updateDisplay    whether to recompute derived stats and signal the UI
     * @return the point total after the reset, always {@link #MAX_BIRTH_POINTS}
     */
    public static int resetStats(Map<Stats, Integer> statsLocal, Map<Stats, Integer> pointsSpentLocal,
                                 Map<Stats, Integer> pointsIncLocal, int pointsLeftLocal,
                                 boolean updateDisplay) {
        EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
        pointsLeftLocal = MAX_BIRTH_POINTS;

        for (int index = 0; index < Stats.values().length; index++) {
            Stats stat = Stats.values()[index];
            if (stat == Stats.STAT_MAX || stat == Stats.STAT_NONE) continue;
            statsLocal.put(stat, 10);
            pointsSpentLocal.put(stat, 0);
            pointsIncLocal.put(stat, birthStatCosts[statsLocal.get(stat) + 1]);
        }

        // Use the new "birth stat" values to work out the "other" stat values (i.e. after
        // modifiers) and tell the UI things have changed
        if (updateDisplay) {
            recalculateStats(statsLocal, pointsLeftLocal);
            eventsHandler.eventSignalBirthpoints(GameEventType.EVENT_BIRTHPOINTS, pointsSpentLocal,
                    pointsIncLocal, pointsLeftLocal);
        }

        return pointsLeftLocal;
    }

    /**
     * Spends one point-buy point raising a single stat by one - the port of C's {@code buy_stat}
     * ({@code player-birth.c:738-773}).
     *
     * <p>{@code choice} arrives as C's own raw stat index - {@code STAT_STR} is 0, {@code STAT_CON}
     * is 4 - and is incremented once before use so it can index straight into
     * {@link Stats#values()}, whose ordinal 0 is the {@code STAT_NONE} sentinel. That shift is why
     * the validity test below reads {@code choice <= 0} and {@code choice >= STAT_MAX.getValue() + 1}
     * rather than C's unshifted {@code choice < 0} / {@code choice >= STAT_MAX}: both exclude the
     * same five stats, just at indices one higher.
     *
     * <p><b>Out-of-range {@code choice} returns, it does not throw.</b> C's {@code do_cmd_buy_stat}
     * passes {@code choice} straight from a command argument with no validation of its own
     * ({@code player-birth.c:1122-1130}), so {@code buy_stat} itself is the only thing standing
     * between a malformed argument and an out-of-bounds array read - which is exactly why its
     * guard is a plain {@code if}, not an {@code assert}, and answers {@code false} rather than
     * crashing. The port's first check, before {@code choice} ever indexes {@link Stats#values()},
     * reproduces that: any {@code choice} landing outside the enum's own bounds answers
     * {@code new IntAndBoolean(pointsLeftLocal, false)} immediately, the same "no-op, nothing
     * spent" result C gives for the identical input.
     *
     * <p>The point-cost lookup, the cost-mismatch guard and the spend are otherwise C's clauses in
     * C's order: {@code birthStatCosts[stat + 1]} prices the point being bought, a mismatch against
     * the caller's own {@code pointsIncLocal} throws rather than asserting (the same substitution
     * documented on {@link #getHistory}), and the point is only taken if enough are left.
     *
     * <p><b>One deliberate divergence.</b> Buying a stat from 17 to 18 is reachable within
     * {@link #MAX_BIRTH_POINTS} - the full climb from base 10 costs 12 of the 20 available points,
     * eight of them on the last two steps alone ({@link #birthStatCosts}'s own entries 17 and 18
     * are 2 and 4, not 1) - and C's next line then reads {@code birth_stat_costs[stat + 1]} with
     * the freshly-incremented value 18, i.e.
     * {@code birth_stat_costs[19]}: one past both that array's and {@link #birthStatCosts}'s
     * nineteen entries, undefined behaviour in C. The port guards the same read with
     * {@code index < birthStatCosts.length} and simply leaves {@code pointsIncLocal} unwritten at
     * that point rather than following C past the end of the array - reproducing C's own
     * out-of-bounds read is not something the port takes on.
     *
     * <p>Where C takes the running point total as {@code int *points_left_local} and writes through
     * it, the port has no by-reference {@code int}: {@link IntAndBoolean} carries both the new
     * points-left figure and the success flag back to the caller, the same pairing
     * {@link #resetStats} uses its own return value for.
     *
     * <p>Function buyStat coded on 260906, commented in full on 260906.
     *
     * @param choice           the stat to raise, as C's raw {@code STAT_*} index (0 for
     *                         {@code STAT_STR} through 4 for {@code STAT_CON}); any other value
     *                         leaves the stats untouched
     * @param statsLocal       the point-buy stat values, one entry per real stat; the chosen stat's
     *                         entry is incremented on success
     * @param pointsSpentLocal the points spent per stat so far; the chosen stat's entry grows by
     *                         the cost paid on success
     * @param pointsIncLocal   the cost of the next point per stat; refreshed for the chosen stat on
     *                         success, except at the one boundary noted above
     * @param pointsLeftLocal  the caller's running point total before this purchase
     * @param updateDisplay    whether to recompute derived stats and signal the UI on a successful
     *                         purchase
     * @return the new points-left total paired with whether the purchase succeeded; the points-left
     * figure is unchanged from {@code pointsLeftLocal} whenever the flag is {@code false}
     */
    public static IntAndBoolean buyStat(Stats choice, Map<Stats, Integer> statsLocal,
                                        Map<Stats, Integer> pointsSpentLocal,
                                        Map<Stats, Integer> pointsIncLocal,
                                        int pointsLeftLocal, boolean updateDisplay) {
        if (choice == Stats.STAT_NONE || choice == Stats.STAT_MAX) {
            return new IntAndBoolean(pointsLeftLocal, false);
        }

        // Increment to max value to handle Stats.STAT_NONE
        if ((choice != Stats.STAT_MAX && choice != Stats.STAT_NONE) && (statsLocal.get(choice) < 18)) {
            // Get the cost of buying the extra point (beyond what it has already cost
            // to get this far            
            int statCost = birthStatCosts[statsLocal.get(choice) + 1];

            if (statCost != pointsIncLocal.get(choice)) {
                String msg = "Invalid point buy cost";
                logger.error(msg);
                throw new RuntimeException(msg);
            }
            if (statCost <= pointsLeftLocal) {
                statsLocal.compute(choice, (k, statLocal) -> statLocal + 1);
                pointsSpentLocal.compute(choice, (k, statLocal) -> statLocal + statCost);

                int index = statsLocal.get(choice) + 1;
                if (index >= 0 && index < birthStatCosts.length) {
                    pointsIncLocal.put(choice, birthStatCosts[statsLocal.get(choice) + 1]);
                }
                pointsLeftLocal -= statCost;

                if (updateDisplay) {
                    // tell the UI the new points situation
                    GameEngine.getEventsBusHandler().eventSignalBirthpoints(GameEventType.EVENT_BIRTHPOINTS, pointsSpentLocal,
                            pointsIncLocal, pointsLeftLocal);

                    // Recalculate everything that's changed because the stat has changes,
                    // and inform the UI
                    recalculateStats(statsLocal, pointsLeftLocal);
                }

                return new IntAndBoolean(pointsLeftLocal, true);
            }
        }

        // Didn't adjust stat
        return new IntAndBoolean(pointsLeftLocal, false);
    }

    /**
     * Spends one point-buy point lowering a single stat by one, refunding the point it cost to
     * raise it - the port of C's {@code sell_stat} ({@code player-birth.c:777-807}).
     *
     * <p>Unlike {@link #buyStat}, {@code choice} arrives as the enum constant directly rather than
     * C's raw {@code STAT_*} index, so there is no shift to apply before the validity check:
     * {@code choice == STAT_MAX || choice == STAT_NONE} excludes the same two sentinels C's
     * {@code choice >= STAT_MAX || choice < 0} does, at the enum's own bounds rather than an
     * incremented one.
     *
     * <p><b>The floor guard is strict.</b> {@code statsLocal.get(choice) <= 10} refuses the sale,
     * matching C's {@code stats_local[choice] > 10} test for proceeding: the point-buy screen
     * starts every stat at 10 and a stat sitting there has nothing left to sell back, so selling at
     * exactly 10 is a no-op returning {@code false}, not a drop to 9.
     *
     * <p>The point-cost lookup, the decrement and the refund are C's clauses in C's order: the cost
     * is read from {@link #birthStatCosts} at the stat's <em>current</em> value before anything
     * changes, the stat is then decremented, and only after that is {@code pointsIncLocal}
     * refreshed - reading the array at the now-lower value plus one, which is arithmetically the
     * value the stat held a line earlier and so always answers the same figure as the cost just
     * refunded. Reading {@code pointsIncLocal} before the decrement, as {@link #buyStat} reads its
     * own cost, would land one index higher and - at the top of the range - one past
     * {@link #birthStatCosts}'s last entry.
     *
     * <p>This is the point-buy birth screen and stays out of the model's scope (see the class
     * Javadoc), so this method has no caller yet.
     *
     * <p>Function sellStat coded on 260906, commented in full on 260906.
     *
     * @param choice           the stat to lower; {@code STAT_NONE} or {@code STAT_MAX} leaves the
     *                         stats untouched
     * @param statsLocal       the point-buy stat values, one entry per real stat; the chosen stat's
     *                         entry is decremented on success
     * @param pointsSpentLocal the points spent per stat so far; the chosen stat's entry shrinks by
     *                         the cost refunded on success
     * @param pointsIncLocal   the cost of the next point per stat; refreshed for the chosen stat on
     *                         success
     * @param pointsLeftLocal  the caller's running point total before this sale
     * @param updateDisplay    whether to recompute derived stats and signal the UI on a successful
     *                         sale
     * @return the new points-left total paired with whether the sale succeeded; the points-left
     * figure is unchanged from {@code pointsLeftLocal} whenever the flag is {@code false}
     */
    public static IntAndBoolean sellStat(Stats choice, Map<Stats, Integer> statsLocal,
                                         Map<Stats, Integer> pointsSpentLocal,
                                         Map<Stats, Integer> pointsIncLocal,
                                         int pointsLeftLocal, boolean updateDisplay) {
        // Must be a valid stat and cwe can't sell stats below the base of 10
        if (choice == Stats.STAT_MAX || choice == Stats.STAT_NONE) {
            return new IntAndBoolean(pointsLeftLocal, false);
        }

        if (statsLocal.get(choice) <= 10) {
            return new IntAndBoolean(pointsLeftLocal, false);
        }

        int statCost = birthStatCosts[statsLocal.get(choice)];

        statsLocal.put(choice, statsLocal.get(choice) - 1);
        pointsSpentLocal.put(choice, pointsSpentLocal.get(choice) - statCost);
        pointsIncLocal.put(choice, birthStatCosts[statsLocal.get(choice) + 1]);
        pointsLeftLocal += statCost;

        if (updateDisplay) {
            // Tell the UI the new points situation
            GameEngine.getEventsBusHandler().eventSignalBirthpoints(GameEventType.EVENT_BIRTHPOINTS, pointsSpentLocal,
                    pointsIncLocal, pointsLeftLocal);

            // Recalculate everything that's changed because the stat has
            // changed and inform the UI
            recalculateStats(statsLocal, pointsLeftLocal);
        }

        return new IntAndBoolean(pointsLeftLocal, true);
    }

    /**
     * Picks reasonable starting stats for the current race/class combination by driving
     * {@link #buyStat} and {@link #sellStat} through a five-step heuristic - the port of C's
     * {@code generate_stats} ({@code player-birth.c:824-981}). {@code st}, {@code spent} and
     * {@code inc} are mutated in place across the run, the same as C's arrays passed by pointer;
     * there is no by-reference {@code left}, so the running total is threaded back out through
     * the return value the way {@link #buyStat} and {@link #sellStat} thread theirs through
     * {@link IntAndBoolean}.
     *
     * <p>The five steps run in a {@code while (left != 0 && step >= 0)} loop, each advancing
     * {@code step} once its own work is done, exactly mirroring C's {@code switch} inside the
     * same {@code while}:
     * <ol>
     *   <li>buy base {@code STAT_STR} up to 17; a pure caster class skips straight to step 3;</li>
     *   <li>buy base {@code STAT_DEX} up to 17, recording the highest-{@code STAT_DEX} breakpoint
     *   that still increases {@link uk.co.jackoftradesltd.middle.player.PlayerState#getNumBlows}
     *   by a whole blow;</li>
     *   <li>sell back any {@code STAT_DEX} bought past that breakpoint, since it bought no extra
     *   blows;</li>
     *   <li>spend up to half of what's left on each of {@code spellStat} and {@code STAT_CON} - or
     *   all of what's left, for a warrior class, which has no {@code spellStat} to split against -
     *   capped at a base of 16/18 unless the class is a pure caster or warrior;</li>
     *   <li>spend whatever remains maximising {@code STAT_DEX}, then {@code STAT_INT} and
     *   {@code STAT_WIS} in turn, skipping whichever of those two is {@code spellStat}.</li>
     * </ol>
     *
     * <p>{@code spellStat} is read once, from the realm of the class's first spellbook, matching
     * C's {@code player->class->magic.books[0].realm->stat}. A class with no spells falls back to
     * {@code STAT_STR}, matching C's own fallback of the literal {@code 0} - {@code STAT_STR} is
     * index 0 in both the C enum and {@link Stats} - not a "no stat" sentinel; step 3's spell-stat
     * loop is only reachable this way for a non-warrior, non-caster class with no spellbook, and
     * every such class shipped with the game (Paladin, Rogue, Ranger, Blackguard) carries one, so
     * the fallback is currently unexercised.
     *
     * <p>Outstanding: this is the point-buy birth screen's auto-generate step and stays out of the
     * model's scope (see the class Javadoc), so this method has no caller yet.
     *
     * <p>Function generateStats coded on 260906, commented in full on 260906.
     *
     * @param st    the point-buy stat values, one entry per real stat; mutated in place by the
     *              {@link #buyStat}/{@link #sellStat} calls made along the way
     * @param spent the points spent per stat so far; mutated in place alongside {@code st}
     * @param inc   the cost of the next point per stat; mutated in place alongside {@code st}
     * @param left  the caller's running point total before this run
     * @return the points-left total once every step has either maxed out or run out of points
     */
    private static int generateStats(Map<Stats, Integer> st, Map<Stats, Integer> spent,
                              Map<Stats, Integer> inc, int left) {
        int step = 0;
        Map<Stats, Boolean> maxed = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_MAX || stat == Stats.STAT_NONE) continue;
            maxed.put(stat, false);
        }
        // Hack - just use start of first book
        Player player = GameState.getPlayer();
        Stats spellStat = player.getPlayerClass().getMagic().getTotalSpells() != 0
                ? player.getPlayerClass().getMagic().getMagicBooks().getFirst().getRealm().getStat()
                : Stats.STAT_STR; // possibly change this to STAT_NONE in future
        boolean caster = player.getPlayerClass().getMaxAttacks() < 5;
        boolean warrior = player.getPlayerClass().getMaxAttacks() > 5;
        int blows = 10;
        int dexBreak = 10;

        while (left != 0 && step >= 0) {
            switch (step) {
                // Buy base STR 17
                case 0 -> {
                    if (!maxed.get(Stats.STAT_STR) && st.get(Stats.STAT_STR) < 17) {
                        IntAndBoolean result = buyStat(Stats.STAT_STR, st, spent, inc, left, false);
                        left = result.value();
                        if (!result.bool) {
                            maxed.put(Stats.STAT_STR, true);
                        }
                    } else {
                        step++;

                        // Pure casters skip to step 3
                        if (caster) step = 3;
                    }
                }

                // Buy base DEX of 17, record best break point
                case 1 -> {
                    if (!maxed.get(Stats.STAT_DEX) && st.get(Stats.STAT_DEX) < 17) {
                        IntAndBoolean result = buyStat(Stats.STAT_DEX, st, spent, inc, left, true);
                        left = result.value();
                        if (!result.bool) {
                            maxed.put(Stats.STAT_DEX, true);
                        }
                        if (player.getPlayerState().getNumBlows() / 10 > blows) {
                            blows = player.getPlayerState().getNumBlows() / 10;
                            dexBreak = st.get(Stats.STAT_DEX);
                        }
                    } else {
                        step++;
                    }
                }

                // Sell back DEX that isn't getting us an extra blow
                case 2 -> {
                    while (st.get(Stats.STAT_DEX) > dexBreak) {
                        IntAndBoolean result = sellStat(Stats.STAT_DEX, st, spent, inc, left, false);
                        left = result.value();
                        maxed.put(Stats.STAT_DEX, false);
                    }
                    step++;
                }

                // Spend up to half the remaining points on each of the spell stat and con, but only
                // up to a max base of 16 unless a pure class [caster or warrior]
                case 3 -> {
                    int pointsTrigger = left / 2;

                    if (warrior) {
                        pointsTrigger = left;
                    } else {
                        while (!maxed.get(spellStat)
                                && (caster || st.get(spellStat) < 18)
                                && spent.get(spellStat) < pointsTrigger) {

                            IntAndBoolean result = buyStat(spellStat, st, spent, inc, left, false);
                            left = result.value();
                            if (!result.bool) {
                                maxed.put(spellStat, true);
                            }

                            if (spent.get(spellStat) > pointsTrigger) {
                                result = sellStat(spellStat, st, spent, inc, left, false);
                                left = result.value();
                                maxed.put(spellStat, true);
                            }
                        }
                    }

                    while (!maxed.get(Stats.STAT_CON)
                            && st.get(Stats.STAT_CON) < 16
                            && spent.get(Stats.STAT_CON) < pointsTrigger) {

                        IntAndBoolean result = buyStat(Stats.STAT_CON, st, spent, inc, left, false);
                        left = result.value();
                        if (!result.bool) {
                            maxed.put(Stats.STAT_CON, true);
                        }

                        if (spent.get(Stats.STAT_CON) > pointsTrigger) {
                            result = sellStat(Stats.STAT_CON, st, spent, inc, left, false);
                            left = result.value();
                            maxed.put(Stats.STAT_CON, true);
                        }
                    }

                    step++;
                }

                // If there aer any points left, spend as much as possible in order on DEX and the non
                // spell stat
                case 4 -> {
                    Stats nextStat = Stats.STAT_NONE;

                    if (!maxed.get(Stats.STAT_DEX)) {
                        nextStat = Stats.STAT_DEX;
                    } else if (!maxed.get(Stats.STAT_INT) && spellStat != Stats.STAT_INT) {
                        nextStat = Stats.STAT_INT;
                    } else if (!maxed.get(Stats.STAT_WIS) && spellStat != Stats.STAT_WIS) {
                        nextStat = Stats.STAT_WIS;
                    } else {
                        step++;
                    }

                    if (nextStat != Stats.STAT_NONE) {
                        // Buy until we can't buy anymore
                        IntAndBoolean result = buyStat(nextStat, st, spent, inc, left, false);
                        left = result.value();
                        while (result.bool) {
                            result = buyStat(nextStat, st, spent, inc, left, false);
                            left = result.value();
                        }
                        maxed.put(nextStat, true);
                    }
                }

                default -> step = -1;
            }
        }

        // Tell the UI the new points situation
        GameEngine.getEventsBusHandler().eventSignalBirthpoints(GameEventType.EVENT_BIRTHPOINTS, spent, inc, left);

        // recalculate everything that's changed because the stat
        // has changed, and inform the UI
        recalculateStats(st, left);

        return left;
    }

    /**
     * Copies the currently-rolled character into {@code toSave}, the port of C's
     * {@code save_roller_data} ({@code player-birth.c:146}). This is the snapshot the birth process
     * keeps for undo and quickstart - {@code prev} and {@code quickstart_prev} in C - so a step back
     * through the birth screens, or a quickstart restart, has something to restore from.
     *
     * <p>Every field read here is the birth-time copy, not the live one - {@link Player#getWeightBirth},
     * {@link Player#getHeightBirth} and {@link Player#getAUBirth} over {@code getWeight}/{@code getHeight}/
     * {@code getAu} - matching C's {@code wt_birth}/{@code ht_birth}/{@code au_birth} over {@code wt}/
     * {@code ht}/{@code au}. The stat loop walks {@link Stats#values()} and skips the two sentinels,
     * {@code STAT_NONE} and {@code STAT_MAX}, leaving the five real stats C's {@code for (i = 0; i <
     * STAT_MAX; i++)} covers ({@code player-birth.c:159-160}).
     *
     * <p>The background-text handoff - {@code toSave.setHistoryBirth(player.getHistoryBirth())} then
     * {@code player.setHistoryBirth(null)} - reads and nulls the same field, matching C's
     * {@code tosave->history = player->history; player->history = NULL;} ({@code player-birth.c:165-166}),
     * which hands the rolled background-text pointer to {@code toSave} and leaves the player without
     * one. C also frees {@code tosave->history} first if it already held a string
     * ({@code player-birth.c:162-164}); the port has nothing to do there, since the old value is simply
     * unreferenced rather than leaked.
     *
     * <p>Function saveRollerData coded on 260906, commented in full on 260906.
     *
     * @param toSave the birther record to fill in
     * @return {@code toSave}, for the caller's convenience
     */
    private static Birther saveRollerData(Birther toSave) {
        Player player = GameState.getPlayer();

        // save the data
        toSave.setRace(player.getRace());
        toSave.setPlayerClass(player.getPlayerClass());
        toSave.setAge(player.getAge());
        toSave.setWeight(player.getWeightBirth());
        toSave.setHeight(player.getHeightBirth());
        toSave.setAu(player.getAUBirth());

        // Save the stats
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;

            toSave.setStat(stat, player.getStatBirth(stat));
        }

        toSave.setHistoryBirth(player.getHistoryBirth());
        player.setHistoryBirth(null);
        toSave.setName(player.getFullName());

        return toSave;
    }

    /**
     * Restores a previously-saved snapshot as the currently-rolled character, optionally handing
     * the caller back what was displaced - the port of C's {@code load_roller_data}
     * ({@code player-birth.c:181-225}). Paired with {@link #saveRollerData}, this is the "flick
     * between two rolls" undo and the quickstart restore reading from the other direction: where
     * {@code saveRollerData} pulls the live player into a {@link Birther}, this pushes a
     * {@link Birther} back onto the live player.
     *
     * <p>{@code saved} is read in full before anything is written - race, class, age, the birth
     * copies of weight/height (each also written back to the live field, matching C's chained
     * {@code p->wt = p->wt_birth = saved->wt}), the birth gold, all five real stats (written to the
     * max/current/birth trio and the identity scramble map, matching C's own triple chain), the
     * history and the name - so a caller passing the same {@link Birther} for both {@code saved}
     * and {@code prevPlayer} (C's own documented case, {@code player-birth.c:177-178}, and its one
     * real caller at {@code player-birth.c:1211}) sees the read finish before the write below
     * touches that same object. The live gold itself, rather than the birth copy, is reset to
     * {@link GameConstants#getPlayerStartGold()} - matching C's
     * {@code player->au = z_info->start_gold} - not carried over from {@code saved} at all.
     *
     * <p>When {@code prevPlayer} is non-{@code null}, the live player's <em>previous</em> state is
     * captured into a local {@code temp} via {@link #saveRollerData} before the load above
     * overwrites it, then written field-by-field onto {@code prevPlayer} afterwards - matching C's
     * {@code *prev_player = temp;} ({@code player-birth.c:223}), a raw struct assignment that
     * overwrites every field of the caller's own struct in place. The port reproduces that in-place
     * effect with individual setters rather than {@link Birther#copy()}, so the object the caller
     * passed in is the very one left holding {@code temp}'s values, with no need for the caller to
     * reassign anything from the return value the way {@link #saveRollerData} requires. The leading
     * {@code prevPlayer.setHistoryBirth(null)} stands in for C's guarded
     * {@code if (prev_player->history) string_free(prev_player->history);}; the port has nothing to
     * free, and the assignment two lines later overwrites it regardless, so the call is a harmless
     * no-op rather than a load-bearing step.
     *
     * <p>C returns {@code void} and communicates entirely through the two pointers; the port has
     * nothing to return {@code prevPlayer} to when it is {@code null}, so it returns
     * {@code prevPlayer} unchanged (itself {@code null}) for exactly that case, and the same,
     * now-updated object otherwise.
     *
     * <p>Function LoadRollerData coded on 260906, commented in full on 260907.
     *
     * @param saved      the snapshot to restore onto the live player; must not be {@code null}
     * @param prevPlayer the snapshot to overwrite with the live player's state before the restore,
     *                   or {@code null} to skip that step; may be the same object as {@code saved}
     * @return {@code prevPlayer}, now holding the state displaced from the live player, or
     * {@code null} if {@code prevPlayer} was {@code null}
     */
    private static Birther LoadRollerData(Birther saved, Birther prevPlayer) {
        Player player = GameState.getPlayer();

        Birther temp = new Birther();

        // Save the previous data if we'll need it later
        if (prevPlayer != null) {
            temp = saveRollerData(temp);
        }

        // Load the previous data
        player.setRace(saved.getRace());
        player.setClass(saved.getPlayerClass());
        player.setAge(saved.getAge());
        player.setWeight(saved.getWeight());
        player.setWeightBirth(saved.getWeight());
        player.setHeightBirth(saved.getHeight());
        player.setHeight(saved.getHeight());
        player.setAUBirth(saved.getAu());
        player.setAU(GameConstants.getPlayerStartGold());

        // load previous stats
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            player.setStatMax(stat, saved.getStat().get(stat));
            player.setCurrStatValue(stat, saved.getStat().get(stat));
            player.setStatBirth(stat, saved.getStat().get(stat));
            player.setCurrStatMap(stat, stat);
        }

        // load previous history
        player.setHistoryBirth(saved.getHistory());
        player.setFullName(saved.getName());

        // Save the current data if the caller is interested in it
        if (prevPlayer != null) {
            prevPlayer.setHistoryBirth(null);

            prevPlayer.setHistoryBirth(temp.getHistory());
            prevPlayer.setName(temp.getName());
            prevPlayer.setRace(temp.getRace());
            prevPlayer.setPlayerClass(temp.getPlayerClass());
            prevPlayer.setAge(temp.getAge());
            prevPlayer.setWeight(temp.getWeight());
            prevPlayer.setHeight(temp.getHeight());
            prevPlayer.setSc(temp.getSc());
            prevPlayer.setAu(temp.getAu());
            prevPlayer.getStat().clear();
            for (Stats stat : temp.getStat().keySet()) {
                prevPlayer.setStat(stat, temp.getStat().get(stat));
            }
        }

        return prevPlayer;
    }

    /**
     * Puts the character back to the state it would be in on entering the birth screen fresh - the
     * port of C's {@code do_birth_reset} ({@code player-birth.c:1045-1058}).
     *
     * <p>Quickstart data is restored first, but only when both {@code useQuickstart} is set
     * <em>and</em> {@code quickstartPrevLevel} is non-{@code null} - mirroring C's
     * {@code use_quickstart && quickstart_prev_local}, where a null pointer reads as false. The same
     * compound condition is passed straight through as {@link #playerGenerate}'s {@code oldHistory}
     * argument, so a quickstart reset keeps the restored history rather than rerolling it. The
     * {@link Birther} that {@link #LoadRollerData} hands back is discarded here, matching the C
     * caller's own disregard for {@code load_roller_data}'s return value.
     *
     * <p>{@link #playerGenerate} is called with a {@code null} race and class, so it leaves whatever
     * race and class the player already has and only rebuilds the derived fields - the level,
     * experience factor, hit dice, age/height/weight and (conditionally) history. Depth is then reset
     * to town, and {@link #getBonuses} recalculates everything that depends on the now-current stats,
     * matching C's own trailing {@code get_bonuses()} call.
     *
     * <p>Function doBirthReset coded on 260906, commented in full on 260907.
     *
     * @param useQuickstart       whether quickstart data should be used at all
     * @param quickstartPrevLevel the previously saved quickstart character, or {@code null} if there
     *                            is none
     */
    private static void doBirthReset(boolean useQuickstart, Birther quickstartPrevLevel) {
        // Use quickstart data to set default character choices if it exists
        if (useQuickstart && quickstartPrevLevel != null) {
            LoadRollerData(quickstartPrevLevel, null);
        }

        Player player = GameState.getPlayer();
        playerGenerate(player, null, null,
                useQuickstart && quickstartPrevLevel != null);

        player.setDepth(0);

        // Update stats with bonuses, etc.
        getBonuses(player);
    }

    /**
     * Handler for {@code CMD_BIRTH_INIT}, starting the birth process - the port of C's
     * {@code do_cmd_birth_init} ({@code player-birth.c:1061-1096}).
     *
     * <p>The dungeon is marked not ready first, matching C's own leading
     * {@code character_dungeon = false}. What happens next hinges on whether the player already
     * carries quickstart data, tested the same way C does - by the birth height being non-zero
     * rather than by any explicit flag.
     *
     * <p>With quickstart data present, a reused character's name is bumped to the next dynastic
     * numeral before the roller state is saved. {@link PlayerName#findRomanSuffixStart} finds the
     * trailing Roman numeral (if the name has one); {@link #romanToInt} reads its current value,
     * one is added unconditionally - mirroring C's own unconditional
     * {@code roman_to_int(buf) + 1}, with no guard against a failed lookup - and {@link
     * #intToRoman} builds the incremented numeral back into text. C writes that text straight
     * into the {@code full_name} buffer through the same pointer {@code roman_to_int} read from;
     * the port reproduces that in-place rename with {@link Player#setFullName}, splicing
     * {@code result} onto whatever preceded the old suffix. Only a genuine build failure - an
     * empty {@code result}, the same signal C's {@code int_to_roman} gives by returning 0 and
     * clearing its buffer - reaches the user, via the message C itself shows on that path. {@link
     * #saveRollerData} is then called and {@link PlayerBirthStateRegistry#isQuickstartAllowed()}
     * set, both unconditionally on
     * this branch, matching C's own {@code save_roller_data(&quickstart_prev)} and
     * {@code quickstart_allowed = true}.
     *
     * <p>Without quickstart data, {@link #playerGenerate} builds a fresh character from the
     * first race and class by index and {@link PlayerBirthStateRegistry#isQuickstartAllowed()}
     * is cleared, matching C's
     * {@code player_generate(player, player_id2race(0), player_id2class(0), false)} and
     * {@code quickstart_allowed = false}.
     *
     * <p>Either way, the method finishes by raising {@code EVENT_ENTER_BIRTH} with the now-current
     * {@link PlayerBirthStateRegistry#isQuickstartAllowed()}, matching C's trailing 
     * {@code event_signal_flag} call.
     *
     * <p>Function doCmdBirthInit coded on 260907, commented in full on 260907.
     *
     * @param cmd the birth-init command; unused, matching C's own unused {@code cmd} parameter
     */
    public static void doCmdBirthInit(Command cmd) {
        GameWorld.setCharacterDungeon(false);
        PlayerBirthStateRegistry.initPlayerBirthStateRegistry();
        Player player = GameState.getPlayer();

        // If there is a quickstart character, store it for later use, 
        // otherwise default to whatever the first of the choices is
        if (player.getHeightBirth() != 0) {
            // handle incrementing name suffix
            String suffix = PlayerName.findRomanSuffixStart(player.getFullName());
            if (suffix != null) {
                // Try to increment the roman suffix
                int newSuffix = romanToInt(suffix);
                newSuffix++;
                String result = intToRoman(newSuffix);
                if (result.isEmpty())
                    Message.message("Sorry, couldn't deal with suffix.");
                else {
                    String newName = player.getFullName();
                    newName = newName.substring(0, newName.length() - suffix.length()) + result;
                    player.setFullName(newName);
                }
            }
            PlayerBirthStateRegistry.setQuickstartPrev(saveRollerData(PlayerBirthStateRegistry.getQuickstartPrev()));
            PlayerBirthStateRegistry.setQuickstartAllowed(true);
        } else {
            playerGenerate(player, PlayerRace.getRaceFromIndex(0),
                    PlayerClass.getClassFromIndex(0), false);
            PlayerBirthStateRegistry.setQuickstartAllowed(false);
        }

        // We're ready to start the birth process
        GameEngine.getEventsBusHandler().eventSignalFlag(GameEventType.EVENT_ENTER_BIRTH,
                PlayerBirthStateRegistry.isQuickstartAllowed());
    }

    /**
     * Converts an arabic integer to its upper-case Roman numeral - the port of C's
     * {@code int_to_roman} ({@code player-birth.c:1379-1424}). The only caller is
     * {@link #doCmdBirthInit}, building the incremented numeral suffix for a reused
     * character name.
     *
     * <p>Roman numerals have no representation for zero or negative numbers, so any
     * {@code value} below 1 answers the empty string immediately, matching C's own
     * {@code n < 1} guard.
     *
     * <p>The greedy symbol-table walk mirrors C's {@code int_to_roman} symbol for symbol: both
     * treat a symbol as usable once {@code value} is no smaller than it. C's inner loop
     * advances {@code i} {@code while (n < roman_symbol_values[i])}, so it stops - and
     * appends - the moment {@code n >= roman_symbol_values[i]}; the port matches this with
     * an inclusive {@code value >= romanSymbolValues[index]} test, not a strict {@code >}.
     * Without that inclusive bound, an exact match would never be consumed, and since the
     * smallest symbol is {@code I} = 1, the remainder could never reach zero.
     *
     * <p>C bounds the write against a caller-supplied {@code bufsize} and signals failure
     * by returning 0 with an empty buffer; the port has no fixed buffer to overflow, so the
     * only failure path left is the one C also falls back to when its own table runs out -
     * answered here with an empty string, matching the empty-string failure convention
     * {@link #romanToInt} already uses.
     *
     * <p>Function intToRoman coded on 260907, commented in full on 260907.
     *
     * @param value the arabic value to convert; values below 1 have no Roman representation
     * @return the upper-case Roman numeral for {@code value}, or the empty string if
     * {@code value} is less than 1
     */
    private static String intToRoman(int value) {
        String result = "";
        StringBuilder romanBuilder = new StringBuilder();

        // Roman numerals have no zero or negative numbers
        if (value < 1) return result;

        int[] romanSymbolValues = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanSymbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        int index = 0;

        // Build the roman numeral i the buffer
        while (value > 0) {
            if (value >= romanSymbolValues[index]) {
                romanBuilder.append(romanSymbols[index]);
                value -= romanSymbolValues[index];
                index = 0;
            } else {
                index++;

                if (index >= romanSymbolValues.length) {
                    return "";
                }
            }
        }

        return romanBuilder.toString();
    }

    /**
     * Looks up the value of a single Roman-numeral letter - the port of the single-letter
     * entries C keeps in {@code roman_token_vals[*][0]}, read via
     * {@code roman_token_chr1 = "MDCLXVI"} ({@code player-birth.c:1445-1454}). C never factors
     * this lookup out into a function of its own; the port does, since {@link #romanToInt}
     * needs the same lookup at both the leading and the lookahead character of every position
     * it examines.
     *
     * <p>Only the seven canonical letters answer a value; anything else, including a lowercase
     * letter, answers {@code -1} - matching a failed {@code strchr(roman_token_chr1, c1)}
     * against C's uppercase-only table.
     *
     * <p>Function value coded on 260907, commented in full on 260907.
     *
     * @param roman the letter to look up
     * @return the letter's Roman-numeral value, or {@code -1} if it is not one of
     * {@code I V X L C D M}
     */
    private static int value(char roman) {
        return switch (roman) {
            case 'I' -> 1;
            case 'V' -> 5;
            case 'X' -> 10;
            case 'L' -> 50;
            case 'C' -> 100;
            case 'D' -> 500;
            case 'M' -> 1000;
            default -> -1;
        };
    }

    /**
     * Converts a Roman numeral to its arabic value - the port of C's {@code roman_to_int}
     * ({@code player-birth.c:1439-1492}). The only caller is {@link #doCmdBirthInit}, incrementing
     * the numeral suffix on a reused character name.
     *
     * <p>Walks the string left to right. Each position's letter is looked up with {@link #value};
     * an unrecognised letter - including a lowercase one, since neither version does case-folding
     * - answers {@code -1} immediately, matching a failed {@code strchr} in C. An empty string
     * likewise answers {@code -1} outright, matching C's own {@code strlen(roman) == 0} check.
     *
     * <p><b>The subtraction is a fixed set of six pairs, not a general rule.</b> A letter's value
     * is only subtracted from its neighbour's when the pair is exactly one of
     * {@code IV IX XL XC CD CM} - the same six two-character tokens C's {@code roman_token_chr2}
     * restricts {@code I}, {@code X} and {@code C} to. {@code M}, {@code D}, {@code L} and
     * {@code V} never lead a pair in either version, matching C's {@code null} entries for those
     * four letters; a letter that could lead a pair but isn't followed by its specific partner -
     * or has no next letter at all, being the last in the string - simply adds its own value
     * instead, the same fallback C reaches when its {@code chr2} lookup fails or the next
     * character is the string's null terminator. This is deliberately narrower than the common
     * "any smaller value before a larger one subtracts" rule: {@code "VX"} totals 15 in both
     * versions, not 5, because {@code V} is never on C's list of pair-leading letters.
     *
     * <p>When a pair does match, both letters are consumed together - the loop index is advanced
     * an extra step inside the match, on top of the {@code for} loop's own increment - mirroring
     * C's manual {@code i++} alongside its own {@code for} loop's increment at the same spot.
     *
     * <p>Like C, this will parse some nonsense strings as if they were Roman numerals (C's own
     * comment names {@code "IVXCCCVIII"}), since neither version checks that the letters are in
     * descending order overall - only the six-pair restriction above is enforced.
     *
     * <p>Function romanToInt coded on 260907, commented in full on 260907.
     *
     * @param roman the Roman numeral to convert; only the uppercase letters {@code I V X L C D M}
     *              are recognised
     * @return the numeral's arabic value, or {@code -1} if {@code roman} is empty or contains a
     * letter that is not a recognised Roman numeral
     */
    private static int romanToInt(String roman) {
        int result = 0;
        int currValue;
        char currChar = '\0';
        char nextChar = '\0';
        String toCheck = roman;

        if (toCheck.isEmpty()) return -1;

        for (int i = 0; i < toCheck.length(); i++) {
            currChar = toCheck.charAt(i);
            currValue = value(currChar);
            if (currValue == -1) return -1;

            if (i < (toCheck.length() - 1)) {
                nextChar = toCheck.charAt(i + 1);

                if (currChar == 'I') {
                    if (nextChar == 'V') {
                        result += 4;
                        i++;
                    } else if (nextChar == 'X') {
                        result += 9;
                        i++;
                    } else {
                        result += 1;
                    }
                } else if (currChar == 'X') {
                    if (nextChar == 'L') {
                        result += 40;
                        i++;
                    } else if (nextChar == 'C') {
                        result += 90;
                        i++;
                    } else {
                        result += 10;
                    }
                } else if (currChar == 'C') {
                    if (nextChar == 'D') {
                        result += 400;
                        i++;
                    } else if (nextChar == 'M') {
                        result += 900;
                        i++;
                    } else {
                        result += 100;
                    }
                } else {
                    result += currValue;
                }
            } else {
                result += currValue;
            }
        }

        return result;
    }

    /**
     * Handler for {@code CMD_BIRTH_RESET}, putting the character and its point-buy state back to
     * how they'd be on entering the birth screen fresh - the port of C's {@code do_cmd_birth_reset}
     * ({@code player-birth.c:1101-1106}).
     *
     * <p>C's four statements run in the same order here: {@link #playerInit} rebuilds the player
     * from scratch, {@link #resetStats} puts every stat back to 10 with zero spent and the base
     * next-point cost, {@link #doBirthReset} restores quickstart data (if any is allowed) and
     * regenerates the derived character fields, and {@link PlayerBirthStateRegistry#setRolledStats}
     * clears the rolled-stats flag, matching C's trailing {@code rolled_stats = false}.
     *
     * <p>C threads {@code points_left} through {@code reset_stats} by pointer, so the write to
     * {@code MAX_BIRTH_POINTS} lands straight in the shared global. {@link #resetStats} has no such
     * pointer - it takes the current total by value and returns the new one instead - so this
     * method captures that return and writes it back explicitly via
     * {@link PlayerBirthStateRegistry#setPointsLeft}, reproducing the same by-reference update C
     * gets for free. The three per-stat maps need no such round trip: {@link
     * PlayerBirthStateRegistry#getStats()}, {@link PlayerBirthStateRegistry#getPointsSpent()} and
     * {@link PlayerBirthStateRegistry#getPointsInc()} hand {@link #resetStats} the same {@code Map}
     * instances it mutates in place, the same way C's array arguments decay to pointers into the
     * shared arrays.
     *
     * <p>Function doCmdBirthReset coded on 260906, commented in full on 260907.
     *
     * @param cmd the birth-reset command; unused, matching C's own unused {@code cmd} parameter
     */
    public static void doCmdBirthReset(Command cmd) {
        Player player = GameState.getPlayer();
        playerInit(player);
        int pointsLeft = resetStats(PlayerBirthStateRegistry.getStats(), PlayerBirthStateRegistry.getPointsSpent(),
                PlayerBirthStateRegistry.getPointsInc(), PlayerBirthStateRegistry.getPointsLeft(), false);
        PlayerBirthStateRegistry.setPointsLeft(pointsLeft);
        doBirthReset(PlayerBirthStateRegistry.isQuickstartAllowed(), PlayerBirthStateRegistry.getQuickstartPrev());
        PlayerBirthStateRegistry.setRolledStats(false);
    }

    /**
     * Handler for {@code CMD_CHOOSE_RACE}, applying the player's race choice from the birth
     * screen and re-running the point-buy pipeline against it - the port of C's {@code
     * do_cmd_choose_race} ({@code player-birth.c:1110-1119}).
     *
     * <p>C reads the {@code choice} arg into a stack {@code int} with {@code
     * cmd_get_arg_choice(cmd, "choice", &choice)} and never checks the return value, so on the
     * (currently unreachable) path where the arg is missing it falls through and calls {@code
     * player_id2race} on whatever garbage was left on the stack. Every producer of {@code
     * CMD_CHOOSE_RACE} in the C tree - the birth-screen race menu, its {@code '*'} random-race
     * key, the {@code -p} random-character path, and the scripted-birth path - sets the arg on
     * the same command it just pushed, so that fallthrough is never exercised in practice; it is
     * dead-but-present behaviour in C, not a real caller this port needs to reproduce. This
     * method chooses safety over exact replication and returns early via {@code
     * chosen.isPresent()} when the arg is absent, deliberately diverging from C here.
     *
     * <p>The rest of the body follows C step for step: {@link #playerGenerate} rebuilds the
     * player for the new race, {@link #resetStats} recomputes the point-buy totals from scratch,
     * and {@link #generateStats} runs the auto-buy pass on top of that. C threads {@code
     * points_left} through both {@code reset_stats} and {@code generate_stats} by pointer; {@link
     * #resetStats} and {@link #generateStats} instead return the new total by value, so each call
     * here is followed by an explicit {@link PlayerBirthStateRegistry#setPointsLeft} to land the
     * result back in the shared registry, reproducing the same by-reference update C gets for
     * free. The trailing {@link PlayerBirthStateRegistry#setRolledStats} matches C's closing
     * {@code rolled_stats = false}.
     *
     * <p>Function doCmdChooseRace coded on 260906, commented in full on 260907.
     *
     * @param cmd the choose-race command; carries the chosen race's index in its {@code "choice"}
     *            arg
     */
    public static void doCmdChooseRace(Command cmd) {
        Player player = GameState.getPlayer();
        Optional<Integer> chosen = cmd.getArgChoice("choice");

        if (!chosen.isPresent()) return;

        int choice = chosen.get();
        playerGenerate(player, PlayerRace.getRaceFromIndex(choice), null, false);

        int pointsLeft = resetStats(PlayerBirthStateRegistry.getStats(), PlayerBirthStateRegistry.getPointsSpent(),
                PlayerBirthStateRegistry.getPointsInc(), PlayerBirthStateRegistry.getPointsLeft(), false);
        PlayerBirthStateRegistry.setPointsLeft(pointsLeft);
        pointsLeft = generateStats(PlayerBirthStateRegistry.getStats(), PlayerBirthStateRegistry.getPointsSpent(),
                PlayerBirthStateRegistry.getPointsInc(), PlayerBirthStateRegistry.getPointsLeft());
        PlayerBirthStateRegistry.setPointsLeft(pointsLeft);
        PlayerBirthStateRegistry.setRolledStats(false);
    }

    /**
     * Handler for {@code CMD_CHOOSE_CLASS}, applying the player's class choice from the birth
     * screen and re-running the point-buy pipeline against it - the port of C's {@code
     * do_cmd_choose_class} ({@code player-birth.c:1122-1131}).
     *
     * <p>Like {@link #doCmdChooseRace}, C reads the {@code choice} arg into a stack {@code int}
     * with {@code cmd_get_arg_choice(cmd, "choice", &choice)} and never checks the return value,
     * so on the (currently unreachable) path where the arg is missing it falls through and calls
     * {@code player_id2class} on whatever garbage was left on the stack. Every producer of {@code
     * CMD_CHOOSE_CLASS} in the C tree - the birth-screen class menu, its {@code '*'} random-class
     * key, the {@code -p} random-character path, and the scripted-birth path - sets the arg on
     * the same command it just pushed, so that fallthrough is never exercised in practice; it is
     * dead-but-present behaviour in C, not a real caller this port needs to reproduce. This
     * method chooses safety over exact replication and returns early via {@code
     * chosen.isPresent()} when the arg is absent, deliberately diverging from C here.
     *
     * <p>The rest of the body follows C step for step: {@link #playerGenerate} rebuilds the
     * player for the new class, {@link #resetStats} recomputes the point-buy totals from scratch,
     * and {@link #generateStats} runs the auto-buy pass on top of that. C threads {@code
     * points_left} through both {@code reset_stats} and {@code generate_stats} by pointer; {@link
     * #resetStats} and {@link #generateStats} instead return the new total by value, so each call
     * here is followed by an explicit {@link PlayerBirthStateRegistry#setPointsLeft} to land the
     * result back in the shared registry, reproducing the same by-reference update C gets for
     * free. The trailing {@link PlayerBirthStateRegistry#setRolledStats} matches C's closing
     * {@code rolled_stats = false}.
     *
     * <p>Function doCmdChooseClass coded on 260907, commented in full on 260907.
     *
     * @param cmd the choose-class command; carries the chosen class's index in its {@code
     *            "choice"} arg
     */
    public static void doCmdChooseClass(Command cmd) {
        Player player = GameState.getPlayer();
        Optional<Integer> chosen = cmd.getArgChoice("choice");

        if (!chosen.isPresent()) return;

        int choice = chosen.get();
        playerGenerate(player, null, PlayerClass.getClassFromIndex(choice), false);

        int pointsLeft = resetStats(PlayerBirthStateRegistry.getStats(), PlayerBirthStateRegistry.getPointsSpent(),
                PlayerBirthStateRegistry.getPointsInc(), PlayerBirthStateRegistry.getPointsLeft(), false);
        PlayerBirthStateRegistry.setPointsLeft(pointsLeft);
        pointsLeft = generateStats(PlayerBirthStateRegistry.getStats(), PlayerBirthStateRegistry.getPointsSpent(),
                PlayerBirthStateRegistry.getPointsInc(), PlayerBirthStateRegistry.getPointsLeft());
        PlayerBirthStateRegistry.setPointsLeft(pointsLeft);
        PlayerBirthStateRegistry.setRolledStats(false);
    }

    /**
     * Handler for {@code CMD_BUY_STAT}, spending one point-buy point to raise a single birth stat
     * from the stat-purchase screen - the port of C's {@code do_cmd_buy_stat} ({@code
     * player-birth.c:1134-1143}).
     *
     * <p>Skipped entirely once {@link PlayerBirthStateRegistry#isRolledStats()}, matching C's own
     * {@code if (!rolled_stats)} boundary: a rolled character has no point-buy total left to spend
     * against.
     *
     * <p>Like {@link #doCmdChooseRace} and {@link #doCmdChooseClass}, C reads the {@code choice}
     * arg into a stack {@code int} with {@code cmd_get_arg_choice(cmd, "choice", &choice)} and
     * never checks the return value; every producer of {@code CMD_BUY_STAT} in the C tree - the
     * birth-screen points menu - asserts the stat index in range before pushing the command
     * ({@code ui-birth.c:1258-1261}), so the missing-arg path is dead-but-present in C, not a real
     * caller this port needs to reproduce. This method chooses safety over exact replication and
     * returns early via {@code chosen.isPresent()} when the arg is absent, deliberately diverging
     * from C here.
     *
     * <p>C hands its raw {@code int} straight to {@code buy_stat}, whose own bounds check ({@code
     * choice >= STAT_MAX || choice < 0}) absorbs every out-of-range value as a silent no-op. This
     * method instead converts the arg to a {@link Stats} with {@link Stats#getStats} first, and
     * that conversion only recognises the two sentinels {@link Stats#STAT_NONE} and
     * {@link Stats#STAT_MAX} - anything further out of range comes back {@code null}, which {@link
     * #buyStat}'s own sentinel check does not catch. The explicit {@code chosenStat == null} guard
     * below stands in for the missing half of C's range test, so an invalid index still falls
     * through to a no-op here instead of the {@code NullPointerException} a bare map lookup on a
     * {@code null} key would otherwise throw.
     *
     * <p>{@link #buyStat}'s {@code bool} half of its return is discarded, matching C discarding
     * {@code buy_stat}'s return value; only the points-left total is threaded back into the
     * registry, reproducing the by-reference update C gets for free through {@code
     * &points_left}.
     *
     * <p>Function doCmdBuyStat coded on 260907, commented in full on 260907.
     *
     * @param cmd the buy-stat command; carries the stat to raise in its {@code "choice"} arg
     */
    public static void doCmdBuyStat(Command cmd) {
        if (!PlayerBirthStateRegistry.isRolledStats()) {
            Optional<Integer> chosen = cmd.getArgChoice("choice");
            if (!chosen.isPresent()) return;
            Stats chosenStat = Stats.getStats(chosen.get());
            if (chosenStat == null) return;
            IntAndBoolean result = buyStat(chosenStat, PlayerBirthStateRegistry.getStats(),
                    PlayerBirthStateRegistry.getPointsSpent(), PlayerBirthStateRegistry.getPointsInc(),
                    PlayerBirthStateRegistry.getPointsLeft(), true);
            PlayerBirthStateRegistry.setPointsLeft(result.value());
        }
    }

    /**
     * The pair {@link #buyStat} hands back in place of C's by-reference {@code int} and {@code bool}
     * return - {@code value} stands in for what C writes through {@code points_left_local} and
     * {@code bool} for C's own return value. Private, and scoped to {@link #buyStat}: nothing else
     * in the class needs the pairing.
     *
     * <p>Record IntAndBoolean coded on 260906, commented in full on 260906.
     *
     * @param value the points-left total after the call
     * @param bool  whether the purchase succeeded
     */
    public record IntAndBoolean(int value, boolean bool) {
    }
}
