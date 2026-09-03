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
import uk.co.jackoftradesltd.middle.enums.Stats;
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
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.HashMap;
import java.util.Map;

import static uk.co.jackoftradesltd.middle.objects.ObjectUtils.*;
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
            player.setPlayerHistory(getHistory(player.getRace().getHistory()));
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
    private void playerInit(Player player) {
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
}
