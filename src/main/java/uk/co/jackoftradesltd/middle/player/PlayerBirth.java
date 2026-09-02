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
}
