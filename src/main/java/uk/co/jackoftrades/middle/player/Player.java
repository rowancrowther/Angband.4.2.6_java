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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The player - the port of C's {@code struct player} (player.h), and the central mutable object of a
 * game in progress. It gathers everything about the current character: identity (race, class, name,
 * history), the birth and derived statistics, position and depth, resources (HP, SP, gold, food,
 * energy), the timed effects and options in force, the body plan and any assumed shape, the carried
 * gear, and the transient per-turn bookkeeping held in {@link PlayerUpkeep}.
 *
 * <p>The middle layer reaches the live player through the swappable
 * {@link uk.co.jackoftrades.middle.game.gameengine.GameState#getPlayer()} boundary rather than a global,
 * so there is exactly one player during play and a test can install its own.
 *
 * <p>This is a work in progress: many fields of C's {@code struct player} are present but not yet
 * wired up, and several methods below are deliberate stubs - individually noted - awaiting the
 * subsystems they depend on.
 *
 * @author Rowan Crowther
 */
public class Player {
    /**
     * Log destination for the conditions C asserts on. Ported asserts become a warning and an
     * early return rather than a crash, so that a data file with an unexpected shape spoils one
     * action instead of the session.
     */
    private static final Logger logger = LogManager.getLogger(Player.class);
    
    /**
     * The player's race - the port of C's {@code p->race}.
     */
    private PlayerRace race;
    /**
     * The player's class - the port of C's {@code p->class}.
     */
    private PlayerClass playerClass;

    /**
     * The player's current grid on the level - the port of C's {@code p->grid}.
     */
    private Loc grid;
    /**
     * The player's grid before leaving for an arena - the port of C's {@code p->old_grid}.
     */
    private Loc oldGrid;

    /**
     * Number of sides on the player's hit die - the port of C's {@code p->hitdie}.
     */
    private int hitDie;
    /**
     * Experience factor: the class/race multiplier applied to experience - the port of C's {@code p->expfact}.
     */
    private int expFact;

    /**
     * The character's age in years - the port of C's {@code p->age}.
     */
    private int age;
    /**
     * The character's height - the port of C's {@code p->ht}.
     */
    private int height;
    /**
     * The character's weight - the port of C's {@code p->wt}.
     */
    private int weight;

    /**
     * Current gold - the port of C's {@code p->au}.
     */
    private int au;

    /**
     * Deepest dungeon level yet reached - the port of C's {@code p->max_depth}.
     */
    private int maxDepth;
    /**
     * Level that Word of Recall will return the player to - the port of C's {@code p->recall_depth}.
     */
    private int recallDepth;
    /**
     * Current dungeon depth - the port of C's {@code p->depth}.
     */
    private int depth;

    /**
     * Highest character level yet attained - the port of C's {@code p->max_lev}.
     */
    private int maxLevel;
    /**
     * Current character level - the port of C's {@code p->lev}.
     */
    private int level;

    /**
     * Highest experience total yet held (never drained below) - the port of C's {@code p->max_exp}.
     */
    private int maxExp;
    /**
     * Current experience - the port of C's {@code p->exp}.
     */
    private int exp;
    /**
     * Fractional part of the current experience, scaled by 2^16 - the port of C's {@code p->exp_frac}.
     */
    private int expFrac;

    /**
     * Maximum hit points - the port of C's {@code p->mhp}.
     */
    private int maxHP;
    /**
     * Current hit points - the port of C's {@code p->chp}.
     */
    private int currentHP;
    /**
     * Fractional part of the current hit points, scaled by 2^16 - the port of C's {@code p->chp_frac}.
     */
    private int chpFrac;

    /**
     * Maximum spell points (mana) - the port of C's {@code p->msp}.
     */
    private int maxSP;
    /**
     * Current spell points (mana) - the port of C's {@code p->csp}.
     */
    private int sp;
    /**
     * Fractional part of the current spell points, scaled by 2^16 - the port of C's {@code p->csp_frac}.
     */
    private int cspFrac;

    /**
     * Current "maximal" stat values, before drain - the port of C's {@code p->stat_max}.
     */
    private HashMap<Stats, Integer> statMax;
    /**
     * Current "natural" stat values - the port of C's {@code p->stat_cur}.
     */
    private HashMap<Stats, Integer> statCur;
    /**
     * Tracks stats remapped by a temporary stat swap - the port of C's {@code p->stat_map}.
     */
    private HashMap<Stats, Integer> statMap;

    /**
     * Turns remaining on each timed effect - the port of C's {@code p->timed}.
     */
    private HashMap<TimedEffect, Integer> timed;

    /**
     * Turns until a pending Word of Recall fires - the port of C's {@code p->word_recall}.
     */
    private int wordRecall;
    /**
     * Turns until a pending Deep Descent fires - the port of C's {@code p->deep_descent}.
     */
    private int deepDescent;

    /**
     * Current energy; the player acts once it reaches the action threshold - the port of C's {@code p->energy}.
     */
    private int energy;
    /**
     * Total energy ever used, including resting - the port of C's {@code p->total_energy}.
     */
    private int totalEnergy;
    /**
     * Number of player turns spent resting - the port of C's {@code p->resting_turn}.
     */
    private int restingTurn;

    /**
     * Current nutrition - the port of C's {@code p->food}.
     */
    private int food;

    /**
     * Non-zero while the player is temporarily showing ignored items - the port of C's {@code p->unignoring}.
     */
    private int unignoring;

    /**
     * Bloodlust-coercion skip state for the next command - the port of C's {@code p->skip_cmd_coercion}.
     *
     * @see #getSkipCmdCoercion()
     */
    private int skipCmdCoercion;

    /**
     * Per-spell knowledge/learning flags - the port of C's {@code p->spell_flags}.
     */
    private int spellFlags; // TODO: Change this once we know what we are dealing with

    /**
     * Order in which spells were learned - the port of C's {@code p->spell_order}.
     */
    private int spellOrder; // TODO: Change this once we know what we are dealing with

    /**
     * The character's full name - the port of C's {@code p->full_name}.
     */
    private String fullName;

    /**
     * Cause of death - the port of C's {@code p->died_from}.
     */
    private String diedFrom;

    /**
     * The character's background history text - the port of C's {@code p->history}.
     */
    private String history;

    /**
     * The character's quest history - the port of C's {@code p->quests}.
     */
    private ArrayList<Quest> quests;

    /**
     * Total-winner flag: set once the player has won the game - the port of C's {@code p->total_winner}.
     */
    private int totalWinner;

    /**
     * Cheating flags that disqualify the character from the score list - the port of C's {@code p->noscore}.
     */
    private int noScore;

    /**
     * True once the player has died - the port of C's {@code p->is_dead}.
     */
    private boolean isDead;

    /**
     * True while the player is in wizard mode - the port of C's {@code p->wizard}.
     */
    private boolean isWizard;

    /**
     * Hit points gained at each level, one entry per level - the port of C's {@code p->player_hp}.
     */
    private int[] playerHP;

    /**
     * Saved birth gold, used by quickstart when {@code birth_money} is off - the port of C's {@code p->au_birth}.
     */
    private int auBirth;

    /**
     * Saved birth "natural" stat values, for quickstart - the port of C's {@code p->stat_birth}.
     */
    private HashMap<Stats, Integer> statsBirth;

    /**
     * Saved birth height, for quickstart - the port of C's {@code p->ht_birth}.
     */
    private int htBirth;

    /**
     * Saved birth weight, for quickstart - the port of C's {@code p->wt_birth}.
     */
    private int wtBirth;

    /**
     * The player's option settings - the port of C's {@code p->opts}.
     */
    private PlayerOptions options;

    /**
     * The player's structured history log (see {@code player-history.c}) - the port of C's {@code p->hist}.
     */
    private PlayerHistory playerHistory;

    /**
     * The player's body plan, i.e. the equipment slots available - the port of C's {@code p->body}.
     */
    private PlayerBody body;

    /**
     * The player's current shape, if shapechanged - the port of C's {@code p->shape}.
     */
    private PlayerShape shape;

    /**
     * The player's real carried gear - the port of C's {@code p->gear}.
     */
    private ArrayList<ItemObject> gear;

    /**
     * The player's gear as currently known to the player - the port of C's {@code p->gear_k}.
     */
    private ArrayList<ItemObject> gearKnown;

    /**
     * The player's accumulated object knowledge ("runes") - the port of C's {@code p->obj_k}.
     *
     * <p>C types that field as a whole {@code struct object}, having nowhere else to hang a bag of
     * learned properties; this port gives it {@link KnownObject}, which carries the twelve fields
     * {@code obj_k} actually uses and none of the several dozen it does not. See that class for
     * why the split is safe.
     *
     * <p>Null until the data files are parsed, matching C, which allocates {@code p->obj_k} in
     * {@code init_player} rather than with the player struct because the knowledge is sized from
     * the registries.
     */
    private KnownObject itemKnowledge;

    /**
     * The player's known version of the current level - the port of C's {@code p->cave}.
     */
    private Chunk cave;

    /**
     * The player's fully calculated state - the port of C's {@code p->state}.
     */
    private PlayerState state;

    /**
     * What the player can know of the calculated state - the port of C's {@code p->known_state}.
     */
    private PlayerState knownState;

    /**
     * Transient per-turn bookkeeping - the port of C's {@code p->upkeep}.
     */
    private PlayerUpkeep playerUpkeep;

    /**
     * Builds an empty player. The two comments below mark a real division: the first group is what
     * C's own initialisation does — {@code player_init} ({@code src/player.c}) allocates the
     * upkeep and the timed-effect table and calls {@code options_init_defaults} — while the second
     * group sets fields C leaves to {@code mem_zalloc}. Java has no equivalent blanket zeroing for
     * the reference fields, and writing them out is what makes the starting state readable rather
     * than implied.
     *
     * <p>A player built here is not yet playable: race, class, body, state and level are all null
     * or empty, and {@link #itemKnowledge} is null until the registries exist to size it against.
     * Birth fills them in.
     */
    public Player() {
        // C initialisation
        playerUpkeep = new PlayerUpkeep();
        timed = new HashMap<>();
        for (TimedEffect effect : TimedEffect.values()) {
            timed.put(effect, 0);
        }
        itemKnowledge = null;
        options = new PlayerOptions();
        options.initDefaults();

        // Java initialisation
        body = null;
        cave = null;
        gear = new ArrayList<>();
        gearKnown = new ArrayList<>();
        grid = Loc.zero;
        isDead = false;
        isWizard = false;
        knownState = null;
        oldGrid = Loc.zero;
        playerClass = null;
        playerHistory = null;
        quests = new ArrayList<>();
        race = null;
        shape = null;
        statCur = new HashMap<>();
        statMax = new HashMap<>();
        statMap = new HashMap<>();
        statsBirth = new HashMap<>();
        state = null;
    }

    /**
     * @return the {@link Chunk} (level) the player is currently in - the port of C's {@code p->cave}
     */
    public Chunk getCave() {
        return cave;
    }

    /**
     * @return the player's transient per-turn bookkeeping - the port of C's {@code p->upkeep}
     */
    public PlayerUpkeep getPlayerUpkeep() {
        return playerUpkeep;
    }

    /**
     * @return the player's body plan, i.e. its equipment slots - the port of C's {@code p->body}
     */
    public PlayerBody getPlayerBody() {
        return body;
    }

    /**
     * Reports whether the player is currently in a non-normal shape - the port of C's
     * {@code player_is_shapechanged}, which tests {@code p->shape && !streq(p->shape->name, "normal")}.
     * A shapechanged player is confined to floor items during item selection (see
     * {@link uk.co.jackoftrades.middle.game.gameengine.Command#getItem}).
     *
     * <p>A player with no shape set counts as <em>not</em> shapechanged: the {@code null} check
     * mirrors C's leading {@code p->shape &&} guard, so this returns {@code false} rather than
     * throwing when {@link #shape} is absent.
     *
     * @return {@code true} when the player has a shape whose name is anything other than
     * {@code "normal"}; {@code false} when the shape is {@code "normal"} or unset
     */
    public boolean isShapeChanged() {
        if (shape != null)
            return !shape.getName().equals("normal");
        return false;
    }

    /**
     * Tests whether a player flag is set on the player's calculated {@link PlayerState} - the port of
     * reading C's {@code p->state.pflags}. Because the flags live on the derived state, this reflects
     * the player after race, class and equipment contributions have been folded in.
     *
     * @param flag the player flag to test for
     * @return {@code true} if the flag is set on the current player state
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasPlayerFlag(@NotNull PlayerFlag flag) {
        return state.hasPFlag(flag);
    }

    /**
     * @param flag the object flag to test
     * @return {@code true} if the player's calculated state carries the given object flag
     * @author Rowan Crowther
     */
    public boolean hasObjectFlag(@NotNull ObjectFlag flag) {
        return state.hasOFlag(flag);
    }

    /**
     * Returns the radius of the light the player currently sheds, read from the calculated
     * {@link PlayerState} - the port of C's {@code p->state.cur_light}.
     *
     * @return the current light radius
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getStateLight() {
        return state.getCurLight();
    }

    /**
     * Returns how many turns remain on a timed effect - the port of reading C's {@code p->timed[idx]}.
     * An effect the player is not under reads as {@code 0}, matching C's zeroed slot.
     *
     * @param timedEffect the timed effect to query
     * @return the turns remaining on the effect, or {@code 0} if the player is not under it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getTimedEffect(@NotNull TimedEffect timedEffect) {
        if (timed.containsKey(timedEffect)) {
            return timed.get(timedEffect);
        }
        return 0;
    }

    /**
     * Tests whether one of the player's options is enabled - the port of C's {@code OPT(player, opt)}
     * macro, which reads the player's option table.
     *
     * @param type the option to test
     * @return {@code true} if the option is set
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean opt(@NotNull PlayerOptionEnum type) {
        return options.has(type);
    }

    /**
     * Records that the player has come to know an object. <b>Stub:</b> not yet implemented - the port
     * of C's object-learning path is deferred until the object-knowledge subsystem is ported.
     *
     * @param item the object being learned
     */
    public void knowObject(ItemObject item) {
        // TODO: Expand this
    }

    /**
     * Reports whether a known item may be ignored under the player's ignore settings. <b>Stub:</b> not
     * yet implemented - always answers {@code false} until the ignore subsystem is ported.
     *
     * @param item the item to test
     * @return {@code false} always, for now
     */
    public boolean ignoreKnownItemOk(@NotNull ItemObject item) {
        // TODO: Expand this
        return false;
    }

    /**
     * Returns the bloodlust-coercion skip state - the port of C's {@code p->skip_cmd_coercion}. A
     * non-zero value tells
     * {@link uk.co.jackoftrades.middle.game.gameengine.CommandProcessor#processCommand} to skip the
     * bloodlust attack substitution on the player's next energy-using command: {@code 1} marks it
     * tentatively (pending whether the command is cancelled), {@code 2} confirms it.
     *
     * @return the current skip state (0 none, 1 tentative, 2 confirmed)
     */
    public int getSkipCmdCoercion() {
        return skipCmdCoercion;
    }

    /**
     * Sets the bloodlust-coercion skip state (see {@link #getSkipCmdCoercion()}) - the port of writing
     * C's {@code p->skip_cmd_coercion}.
     *
     * @param skipCmdCoercion the new skip state (0 none, 1 tentative, 2 confirmed)
     */
    public void setSkipCmdCoercion(int skipCmdCoercion) {
        this.skipCmdCoercion = skipCmdCoercion;
    }

    /**
     * Makes a bloodlust-driven attack on a random adjacent monster in place of the player's chosen
     * command - the port of C's {@code player_attack_random_monster}, invoked from
     * {@link uk.co.jackoftrades.middle.game.gameengine.CommandProcessor#processCommand} when a
     * bloodlust check fires. <b>Stub:</b> the attack itself is not yet ported, so this currently takes
     * no action and reports that no attack was made.
     *
     * @return {@code true} if an attack was made (so the original command should be abandoned);
     * {@code false} otherwise - always {@code false} while stubbed
     */
    public boolean attackRandomMonster() {
        int index;
        int direction = RandomValueUtils.randInt0(8);

        if (timed.get(TimedEffect.TMD_CONFUSED) != 0) return false;

        for (index = 0; index < 8; index++, direction++) {

            // DO stuff - this is currently a stub class

        }

        return false;
    }

    /**
     * @return the player's class - the port of C's {@code p->class}
     */
    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    /**
     * @return the player's current energy - the port of C's {@code p->energy}
     */
    public int getEnergy() {
        return energy;
    }

    /**
     * Sets the player's current energy - the port of writing C's {@code p->energy}.
     *
     * @param energy the new energy value
     */
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    /**
     * @return {@code true} once the player has died - the port of C's {@code p->is_dead}
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Clears a timed effect, ending it early - the port of C's {@code player_clear_timed}.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the timed-effects runtime; reports that nothing
     * changed.
     *
     * @param timedEffect the effect to clear
     * @param notify      whether to announce the effect ending to the player
     * @param canDisturb  whether clearing it may interrupt resting/running
     * @return {@code true} if the effect was active and has now been cleared
     */
    public boolean clearTimed(TimedEffect timedEffect, boolean notify, boolean canDisturb) {
        // Stub class TODO: implement
        return false;
    }

    /**
     * Carries out any pending one-off notice actions (combine the pack, apply ignore rules, …) -
     * the port of C's {@code notice_stuff}. Reads and clears the {@code PN_*} notice flags.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the player-calc subsystem.
     */
    public void noticeStuff() {
        // Stub class TODO: implement
    }

    /**
     * Recomputes any stale derived state and repaints any changed screen regions - the port of C's
     * {@code handle_stuff}, which is {@link #updateStuff()} followed by {@link #redrawStuff()}
     * bundled into one call.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the player-calc subsystem.
     */
    public void handleStuff() {
        // Stub class TODO: implement
    }

    /**
     * Recomputes derived quantities (HP, mana, view, bonuses, …) that have been flagged stale -
     * the port of C's {@code update_stuff}. Reads and clears the {@code PU_*} update flags.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the player-calc subsystem.
     */
    public void updateStuff() {
        // Stub class TODO: implement
    }

    /**
     * Repaints the screen regions that have been flagged as changed - the port of C's
     * {@code redraw_stuff}. Reads and clears the {@code PR_*} redraw flags.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the display subsystem.
     */
    public void redrawStuff() {
        // Stub class TODO: implement
    }

    /**
     * @return the player's derived/calculated state - the port of C's {@code p->state}
     */
    public PlayerState getPlayerState() {
        return state;
    }

    /**
     * Tests whether a timed effect is currently at a named grade - the port of C's
     * {@code player_timed_grade_eq}. Some timed effects (stun, cut, fear) have named severity bands;
     * this asks whether the player is presently in a specific one, e.g. {@code TMD_STUN} at
     * "Knocked Out".
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the timed-effects runtime; reports {@code false}.
     *
     * @param index the timed effect to inspect
     * @param match the grade name to test against
     * @return {@code true} if the effect is active and at the named grade
     */
    public boolean timedGradeEqual(TimedEffect index, String match) {
        // Stub class TODO: implement
        return false;
    }

    /**
     * Sets the running total of energy the player has ever used - the port of writing C's
     * {@code p->total_energy}. The per-turn cleanup adds each command's energy cost here, tracking the
     * game's overall pace.
     *
     * @param totalEnergy the new cumulative energy total
     */
    public void setTotalEnergy(int totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    /**
     * @return the running total of energy the player has ever used - the port of C's
     * {@code p->total_energy}
     */
    public int getTotalEnergy() {
        return totalEnergy;
    }

    /**
     * @return the player's current grid on the level - the port of C's {@code p->grid}
     */
    public Loc getGrid() {
        return grid;
    }

    /**
     * @return the player's current dungeon depth (0 = town)
     * @author Rowan Crowther
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @return the player's current hit points
     * @author Rowan Crowther
     */
    public int getCurrentHP() {
        return currentHP;
    }

    /**
     * Reduces a stat, optionally permanently — the port of C's {@code player_stat_dec}
     * ({@code player.c}).
     *
     * <p><b>Stub:</b> not yet implemented; reports no change.
     *
     * @param stat      the stat to lower
     * @param permanant whether the loss also reduces the stat's maximum
     * @return {@code true} if the stat actually changed
     * @author Rowan Crowther
     */
    public boolean statDec(Stats stat, boolean permanant) {
        // Stub function TODO: implement
        return false;
    }

    /**
     * Removes experience points from the player, optionally reducing the maximum too, then
     * re-evaluates the character level. The port of C's {@code player_exp_lose} ({@code player.c}).
     * The loss is capped at the current experience so it cannot go negative.
     *
     * @param amount    the experience to remove
     * @param permanent whether the loss also reduces the player's maximum experience
     * @author Rowan Crowther
     */
    public void expLose(int amount, boolean permanent) {
        if (exp < amount) {
            amount = exp;
        }
        exp -= amount;
        if (permanent) {
            maxExp -= amount;
        }
        adjustLevel(true);
    }

    /**
     * Recomputes the player's character level from current experience, applying any level-up or
     * level-down effects — the port of C's {@code adjust_level} ({@code player.c}).
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param verbose whether to announce level changes to the player
     * @author Rowan Crowther
     */
    private void adjustLevel(boolean verbose) {
        // Stub function TODO: implement
    }

    /**
     * @return the player's current experience points
     * @author Rowan Crowther
     */
    public int getExp() {
        return exp;
    }

    /**
     * Reduce the remaining duration of a timed effect by a given amount, delegating
     * to {@link #setTimed} with the new total. The port of C's {@code player_dec_timed}.
     *
     * <p>If the reduction would take the effect to zero or below, the change is
     * always announced (the {@code notify} argument is forced {@code true}) so the
     * player is told the effect has worn off.</p>
     *
     * @param timedEffect the effect to shorten
     * @param amount      the number of turns to remove
     * @param notify      whether to announce a change that leaves the effect still active
     * @param canDisturb  whether the change may interrupt resting/running
     * @return {@code true} if the effect's value actually changed
     */
    public boolean decTimed(TimedEffect timedEffect, int amount, boolean notify, boolean canDisturb) {
        int newValue;

        newValue = timed.get(timedEffect) - amount;

        if (newValue > 0) {
            return setTimed(timedEffect, newValue, notify, canDisturb);
        }

        return setTimed(timedEffect, newValue, true, canDisturb);
    }

    /**
     * Extend (or begin) a timed effect by a given amount, delegating to {@link #setTimed} with the
     * new total. The port of C's {@code player_inc_timed} ({@code player-timed.c}).
     *
     * <p><b>Stub:</b> not yet implemented; reports no change.
     *
     * @param timedEffect the effect to lengthen
     * @param amount      the number of turns to add
     * @param notify      whether to announce the change to the player
     * @param canDisturb  whether the change may interrupt resting/running
     * @param check       whether to honour the effect's failure conditions before applying it
     * @return {@code true} if the effect's value actually changed
     * @author Rowan Crowther
     */
    public boolean incTimed(TimedEffect timedEffect, int amount, boolean notify, boolean canDisturb, boolean check) {
        // Stub function TODO: implement
        return false;
    }

    /**
     * Set a timed effect to an absolute duration, applying grade thresholds and any
     * on-change messaging. The port of C's {@code player_set_timed}; the common sink
     * that {@link #incTimed} and {@link #decTimed} both funnel through.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the timed-effects runtime; reports
     * {@code false} (no change).</p>
     *
     * @param timedEffect the effect to set
     * @param amount      the new duration in turns (may be zero or negative to clear it)
     * @param notify      whether to announce the change to the player
     * @param canDisturb  whether the change may interrupt resting/running
     * @return {@code true} if the effect's value actually changed
     */
    public boolean setTimed(TimedEffect timedEffect, int amount, boolean notify, boolean canDisturb) {
        // Stub function TODO: implement
        return false;
    }

    /**
     * @return the player's maximum hit points
     * @author Rowan Crowther
     */
    public int getMaxHP() {
        return maxHP;
    }

    /**
     * @return {@code true} if the player is currently resting — either the resting counter is still
     * running or a special stop-condition rest is in progress
     * @author Rowan Crowther
     */
    public boolean isResting() {
        return (playerUpkeep.getRestingCounter() > 0 || restingIsSpecial(playerUpkeep.getRestingCounter()));
    }

    /**
     * Tests whether the given resting counter denotes one of the "rest until a condition is met"
     * sentinel values (as opposed to a fixed turn count) — the port of C's special resting-count
     * handling.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param restingCounter the resting counter to classify
     * @return {@code true} if the counter is a special "rest until…" value
     * @author Rowan Crowther
     */
    private boolean restingIsSpecial(int restingCounter) {
        // Stub function TODO: implement
        return false;
    }

    /**
     * @return the turns remaining until Word of Recall activates (0 = inactive)
     * @author Rowan Crowther
     */
    public int getWordRecall() {
        return wordRecall;
    }

    /**
     * Ticks the Word of Recall countdown down by one turn.
     *
     * @author Rowan Crowther
     */
    public void decrementWordRecall() {
        wordRecall--;
    }

    /**
     * Sets the player's current dungeon depth.
     *
     * @param depth the new depth (0 = town)
     * @author Rowan Crowther
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @return the depth Word of Recall will return the player to
     * @author Rowan Crowther
     */
    public int getRecallDepth() {
        return recallDepth;
    }

    /**
     * Recomputes and stores the depth Word of Recall should return the player to — the port of C's
     * recall-depth handling.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @author Rowan Crowther
     */
    public void setRecallDepth() {
        // Stub function TODO: implement
    }

    /**
     * @return the turns remaining until a Deep Descent triggers (0 = inactive)
     * @author Rowan Crowther
     */
    public int getDeepDescent() {
        return deepDescent;
    }

    /**
     * Ticks the Deep Descent countdown down by one turn.
     *
     * @author Rowan Crowther
     */
    public void decrementDeepDescent() {
        deepDescent--;
    }

    /**
     * Tests whether a given dungeon level hosts one of the player's outstanding quests — the port of
     * C's {@code is_quest} ({@code player-quest.c}). The town (level 0) never holds a quest.
     *
     * @param level the dungeon level to test
     * @return {@code true} if a quest target lives on that level
     * @author Rowan Crowther
     */
    public boolean isQuest(int level) {
        // No quests on town level
        if (level == 0) return false;

        for (Quest quest : quests) {
            if (quest.getLevel() == level) return true;
        }

        return false;
    }

    /**
     * @return the deepest dungeon level the player has reached
     * @author Rowan Crowther
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * @return the player's carried gear (inventory and equipment)
     * @author Rowan Crowther
     */
    public ArrayList<ItemObject> getGear() {
        return gear;
    }

    /**
     * Records that the player has learned the identity of a curse, typically because its effect
     * just fired on a worn item. The port of C's {@code player_learn_curse}
     * ({@code src/obj-knowledge.c}).
     *
     * <p>C resolves the curse to a rune by name — {@code rune_index(RUNE_VAR_CURSE,
     * lookup_curse(curse->name))} — rather than by identity, which is why
     * {@link Rune#runeIndex(Curse)} matches on the name too. A curse reconstructed from a savefile
     * or built by a test is then still recognised.
     *
     * <p>A curse with no rune yields null here, where C's guard is {@code index >= 0}; the null is
     * handled inside {@link #learnRune}, so the two guards sit in different places but reject the
     * same case. The knowledge update stays outside that guard in both, running even when the
     * lookup found nothing.
     *
     * @param curse the curse whose nature has now been revealed
     */
    public void learnCurse(Curse curse) {
        Rune rune = Rune.runeIndex(curse);
        learnRune(rune, true);
        updateObjectKnowledge();
    }

    /**
     * @return this player's option settings, the port of C's {@code player->opts}
     * @author Rowan Crowther
     */
    public PlayerOptions getPlayerOptions() {
        return options;
    }

    /**
     * Raises the high-water mark of experience level to the current level, if the current level is
     * higher. C writes this inline wherever the level changes.
     */
    public void updateMaxLevel() {
        this.maxLevel = Math.max(this.maxLevel, this.level);
    }

    /**
     * Raises the deepest-reached mark to the current depth, and moves the word-of-recall depth
     * down with it. The two travel together deliberately: reaching new depth is what re-targets
     * recall, so a player who then climbs back up still recalls to the deepest point rather than
     * to wherever they happen to be standing.
     */
    public void updateDungeonDepth() {
        if (maxDepth < depth) {
            maxDepth = depth;
            recallDepth = depth;
        }
    }

    /**
     * Records that the player has learned to recognise a brand, typically because they just saw it
     * fire in combat. The port of C's {@code player_learn_brand}.
     *
     * <p>One of the wrapper functions that {@link #learnRune} exists to serve, and it shows the
     * shape they all take: guard on already-knowing, resolve the property to its rune, learn the
     * rune, update. The resolution step is the one that cannot be skipped — a brand belongs to a
     * group of same-named brands sharing a single rune, and {@link Rune#runeIndex(Brand)} returns
     * the rune for the group rather than for the particular strength passed in.
     *
     * <p>The second {@link #updateObjectKnowledge()} is C's, not an oversight: {@link #learnRune}
     * has already called it on any path that learned something, and
     * {@code player_learn_brand} calls it again regardless.
     *
     * @param brand any brand of the wanted kind, at any strength
     */
    public void learnBrand(Brand brand) {
        if (!knowsBrand(brand)) {
            Rune rune = Rune.runeIndex(brand);

            learnRune(rune, true);
            updateObjectKnowledge();
        }
    }

    /**
     * The port of C's {@code player_knows_brand}. Note that this asks about the exact brand given,
     * not its group — which is the same thing in practice, because learning any member of a group
     * marks all of them (see {@link KnownObject#learnBrand}).
     *
     * @param brand the brand to ask about
     * @return true if the player recognises this brand
     */
    public boolean knowsBrand(Brand brand) {
        return itemKnowledge.brandIsKnown(brand);
    }

    /**
     * Learns a single rune: marks the property it names as readable, announces it if anything was
     * genuinely new, and updates everything the player can now see. The port of C's
     * {@code player_learn_rune} ({@code src/obj-knowledge.c}), and the one place object knowledge
     * is added.
     *
     * <p><b>This is an internal choke point, not an entry point.</b> C keeps it file-{@code static}
     * and routes every caller through a wrapper — {@code player_learn_flag},
     * {@code player_learn_slay}, {@code player_learn_brand}, {@code player_learn_curse}, the
     * {@code equip_learn_*} family. The wrappers are not decoration. Each resolves its property to
     * a rune through the matching {@link Rune#runeIndex} overload, and for brands, slays and
     * curses that lookup returns the rune for an <em>equivalence class</em> rather than for the
     * exact object handed in. Code that reaches past a wrapper and builds its own {@link Rune}
     * skips that resolution, and learns one member of a group where the game means all of them.
     * Prefer {@link #learnBrand} and its siblings; add new learning paths as further wrappers.
     *
     * <p>The switch is over a sealed interface, so the seven varieties are matched as record
     * patterns and the compiler proves the set is covered — no {@code default} arm, and no cast to
     * get at each variety's key. C reaches the same seven cases through a {@code switch} on
     * {@code r->variety} followed by an {@code int} index whose meaning changes per case, and
     * closes with a {@code default: learned = false} it cannot show to be unreachable.
     *
     * <p>Only the combat arm can fall through without learning, on the {@code COMBAT_RUNE_MAX}
     * sentinel; C's chain of {@code if}/{@code else if} does the same silently, and the warning
     * here is a Java-side addition for a case that should not arise.
     *
     * <p>The tail order matters and is C's: nothing learned means no message and no update, so a
     * property learned twice is announced once.
     *
     * @param rune         the rune to learn; null is logged and ignored, standing in for C's
     *                     {@code assert} on the rune index
     * @param printMessage whether to announce the discovery, false for the paths that learn in
     *                     bulk and would otherwise bury the player in messages
     */
    public void learnRune(Rune rune, boolean printMessage) {
        if (rune == null) {
            logger.warn("Rune is null on entering learnRune");
            return;
        }

        boolean learned = false;

        switch (rune.getVariety()) {
            case RuneVariety.CombatKey(CombatRunes key) -> {
                switch (key) {
                    case COMBAT_RUNE_TO_A -> learned = itemKnowledge.learnToA();

                    case COMBAT_RUNE_TO_H -> learned = itemKnowledge.learnToH();

                    case COMBAT_RUNE_TO_D -> learned = itemKnowledge.learnToD();

                    case COMBAT_RUNE_MAX -> logger.warn("Combat Rune MAX encountered.");
                }
            }
            case RuneVariety.ModKey(ObjectModifier key, var property) -> learned = itemKnowledge.learnModifier(key);

            case RuneVariety.ResistKey(ElementEnum key, var projection) -> learned = itemKnowledge.learnResistance(key);

            case RuneVariety.BrandKey(Brand key) -> learned = itemKnowledge.learnBrand(key);

            case RuneVariety.SlayKey(Slay key) -> learned = itemKnowledge.learnSlay(key);

            case RuneVariety.CurseKey(Curse key) -> learned = itemKnowledge.learnCurse(key);

            case RuneVariety.FlagKey(ObjectFlag key, var property) -> learned = itemKnowledge.learnFlag(key);
        }

        if (!learned) return;

        if (printMessage)
            Message.messageType(MessageType.MSG_RUNE, "You have learned the rune of "
                    + rune.getVariety().runeName() + ".");

        updateObjectKnowledge();
    }

    /**
     * Re-derives the known copy of every object the player could be looking at, now that a rune
     * has been learned. The port of C's {@code update_player_object_knowledge}, which runs
     * {@code player_know_object} over four populations — the objects on the level, the player's
     * gear, every store's stock, and the objects hanging off the curse definitions — then
     * autoinscribes the ground and the pack and signals the inventory and equipment events.
     *
     * <p>Stores and curse objects are in that list for a reason worth keeping: knowledge is a
     * property of the player rather than of the item, so learning a rune changes how a sword in a
     * shop reads without the player ever having touched it.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the object-knowledge runtime. Callers already
     * invoke it in the right places, so filling it in should not need them changed. Note that
     * calling it twice is normal rather than a mistake — {@link #learnRune} calls it, and each
     * wrapper calls it again — which is C's arrangement and harmless because the work is a
     * recomputation rather than a step.
     */
    public void updateObjectKnowledge() {
        // Stub class TODO: Implement
    }
}
