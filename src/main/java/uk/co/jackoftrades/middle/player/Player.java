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
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
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
import java.util.Map;

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
     * rune. The resolution step is the one that cannot be skipped — a brand belongs to a group of
     * same-named brands sharing a single rune, and {@link Rune#runeIndex(Brand)} returns the rune
     * for the group rather than for the particular strength passed in. Propagating the new
     * knowledge is not this method's job; {@link #learnRune} has done it by the time it returns.
     *
     * <p>C's {@code player_learn_brand} closes with a second
     * {@code update_player_object_knowledge}, which this port deliberately drops. It cannot do
     * anything: the guard above means the rune is unknown whenever the call is reached — knowledge
     * of a brand and of its rune move together, since {@link KnownObject#learnBrand} marks every
     * same-named brand at once — so {@link #learnRune} always learns, and always updates. The
     * duplicate is boilerplate copied from {@code player_learn_flag}, which has no guard and so is
     * the one wrapper where the trailing call can be the only one that runs. Even there it changes
     * nothing, because it recomputes identical values.
     *
     * @param brand any brand of the wanted kind, at any strength
     */
    public void learnBrand(Brand brand) {
        if (!knowsBrand(brand)) {
            Rune rune = Rune.runeIndex(brand);

            learnRune(rune, true);
        }
    }

    /**
     * Records that the player has learned to recognise a slay, typically because they just saw it
     * bite. The port of C's {@code player_learn_slay}, and the sibling of {@link #learnBrand}.
     *
     * <p>Same three steps — guard on already-knowing, resolve the property to its rune, learn the
     * rune — but the equivalence the resolution walks is a different one.
     * {@link Rune#runeIndex(Slay)} groups by {@link Slay#sameMonsterSlain}, following C's
     * {@code same_monsters_slain}, and <em>not</em> by name as brands do. The distinction is real:
     * two slays can share the name "evil" and kill different monsters, because one carries a
     * monster base and the other does not. Grouping those together would teach the player a rune
     * they have seen no evidence for.
     *
     * <p>As with {@link #learnBrand}, C's trailing {@code update_player_object_knowledge} is
     * dropped — the guard means {@link #learnRune} always learns, and so always updates.
     *
     * @param slay any slay of the wanted kind, at any strength
     */
    public void learnSlay(Slay slay) {
        if (!knowsSlay(slay)) {
            Rune rune = Rune.runeIndex(slay);
            learnRune(rune, true);
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
     * Records that the player has learned to recognise an object flag. The port of C's
     * {@code player_learn_flag}, whose one caller is the failed uncursing that leaves an item
     * {@code OF_FRAGILE} ({@code effect-handler-general.c:203}).
     *
     * <p>Flags need no group resolution — each has its own rune, so unlike {@link #learnBrand} and
     * {@link #learnSlay} there is no equivalence class for {@link Rune#runeIndex(ObjectFlag)} to
     * find. The lookup can still answer {@code null}, because not every flag is a learnable
     * property: {@code init_rune} skips the placeholder subtypes, the ones describing the object
     * rather than the player, and the curse-only ones. {@link #learnRune} logs that and returns,
     * where C hands {@code rune_index}'s {@code -1} straight to {@code rune_list[-1]}.
     *
     * <p><b>The already-known guard is this port's, not C's.</b> C's version is unguarded, and
     * relies on the flag arm of {@code player_learn_rune} using {@code of_on}, which reports
     * whether it changed anything — so a flag learned twice is silently not announced twice. The
     * guard here changes no answer (it is the same test one call deeper) and buys consistency with
     * the other wrappers. It also makes C's trailing {@code update_player_object_knowledge}
     * unreachable, which matters only in that this was the single wrapper where that call could
     * have been the one that ran; it recomputed identical values, so nothing is lost.
     *
     * @param flag the flag now readable
     */
    public void learnFlag(@NotNull ObjectFlag flag) {
        if (itemKnowledge.flagIsKnown(flag)) return;

        learnRune(Rune.runeIndex(flag), true);
    }

    /**
     * Whether the player can read a rune. The port of C's {@code player_knows_rune}
     * ({@code obj-knowledge.c:257-306}), and the mirror image of {@link #learnRune}: the same seven
     * varieties, each asking {@link #itemKnowledge} the question the corresponding {@code learn}
     * arm answers.
     *
     * <p>This is the method that decided {@link KnownObject}'s shape. C's version is a seven-armed
     * switch in which every arm reads one field of {@code p->obj_k}, so between them the arms
     * enumerate everything a knowledge object has to hold. A port of {@code obj_k} is the right
     * size exactly when it can serve all seven with nothing left over — which is why the twelve
     * fields, and not a whole {@code struct object}, are enough.
     *
     * <p>Two arms are worth reading against C rather than taken on trust. The curse arm is
     * {@code p->obj_k->curses[index].power == 1}, where {@code power} is a severity everywhere else
     * in the game but a 0/1 flag on the knowledge side — {@code save.c:661} writes it as
     * {@code power ? 1 : 0} — so {@link KnownObject#curseIsKnown} answering from a boolean loses
     * nothing. The combat arm splits three ways on {@link CombatRunes} where C compares
     * {@code r->index} against three constants, and its {@code COMBAT_RUNE_MAX} case is the
     * sentinel, which is a data error rather than an answer; it is logged and reported unknown.
     *
     * <p>No {@code default}: the switch is over the sealed {@link RuneVariety}, so the compiler
     * proves the seven are covered. An eighth variety would be a compile error here, which is the
     * point — a {@code default} would answer {@code false} for it and say nothing.
     *
     * @param rune the rune to ask about
     * @return true if the player can read this rune
     */
    @NotNull
    @Contract(pure = true)
    @CheckReturnValue
    public boolean knowsRune(@NotNull Rune rune) {
        boolean known = false;

        switch (rune.getVariety()) {
            case RuneVariety.CombatKey(CombatRunes key) -> known = switch (key) {
                case COMBAT_RUNE_TO_A -> itemKnowledge.toAIsKnown();
                case COMBAT_RUNE_TO_D -> itemKnowledge.toDIsKnown();
                case COMBAT_RUNE_TO_H -> itemKnowledge.toHIsKnown();
                case COMBAT_RUNE_MAX -> {
                    logger.warn("Combat Rune MAX encountered.");
                    yield false;
                }
            };

            case RuneVariety.BrandKey(Brand key) -> known = itemKnowledge.brandIsKnown(key);
            case RuneVariety.FlagKey(ObjectFlag key, var property) -> known = itemKnowledge.flagIsKnown(key);
            case RuneVariety.CurseKey(Curse key) -> known = itemKnowledge.curseIsKnown(key);
            case RuneVariety.ModKey(ObjectModifier key, var property) -> known = itemKnowledge.modifierIsKnown(key);
            case RuneVariety.ResistKey(ElementEnum key, var projection) -> known = itemKnowledge.resistanceIsKnown(key);
            case RuneVariety.SlayKey(Slay key) -> known = itemKnowledge.slayIsKnown(key);
        }

        return known;
    }

    /**
     * The port of C's {@code player_knows_slay}, a bare array lookup. As with
     * {@link #knowsBrand}, it asks about the exact slay given rather than its group, and gets the
     * same answer either way: {@link KnownObject#learnSlay} marks every slay that kills the same
     * monsters, so the cost of grouping is paid once on the learning side and this stays cheap.
     *
     * @param slay the slay to ask about
     * @return true if the player recognises this slay
     */
    public boolean knowsSlay(@NotNull Slay slay) {
        return itemKnowledge.slayIsKnown(slay);
    }

    /**
     * The port of C's {@code player_knows_curse}, which reads
     * {@code p->obj_k->curses[index].power == 1}.
     *
     * <p>That {@code power} is not a severity. On a real object it is one — 1 to 99 from
     * {@code apply_curse}, deciding how strong a removal spell must be, with 100 and above meaning
     * permanent — but on the knowledge side it only ever holds 0 or 1, because C types
     * {@code p->obj_k} as a whole {@code struct object} and inherits {@code struct curse_data}
     * whether it wants two integers or not. {@code player_learn_rune} writes a literal 1 and
     * {@code save.c:661} normalises with {@code power ? 1 : 0}. So the port keeps a boolean, and
     * the {@code == 1} has nothing to test.
     *
     * <p>The two meanings meet in {@code player_know_object} ({@code obj-knowledge.c:1131}), where
     * this answer <em>gates</em> the real severity: a recognised curse shows its true power on the
     * known copy of an object, an unrecognised one reads as zero. That is why the curse-removal
     * menu can offer only what the player has learned.
     *
     * <p>Curses are never grouped, so unlike brands and slays there is no fan-out behind this.
     *
     * @param curse the curse to ask about
     * @return true if the player recognises this curse
     */
    public boolean knowsCurse(@NotNull Curse curse) {
        return itemKnowledge.curseIsKnown(curse);
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
     * <p><b>A wrapper does not need to call {@link #updateObjectKnowledge()}.</b> This method
     * leaves object knowledge propagated on every path that learned anything, and that is the
     * invariant the rest of the system is written against: most of C's callers — the
     * {@code equip_learn_*} family, {@code object_learn_on_wield},
     * {@code object_learn_unknown_rune}, {@code missile_learn_on_ranged_attack}, the
     * {@code object_curses_find_*} family, {@code player_learn_all_runes} — have no update call of
     * their own and rely entirely on this one. Only four of C's wrappers add a second, and it is
     * redundant in each (see {@link #learnBrand}); this port omits it rather than copy it.
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
     * <p>The work is a recomputation rather than a step, so calling this twice in a row is
     * harmless — which is why C's habit of calling it again in the learning wrappers went
     * unnoticed. It is not free, though: each call sweeps four populations and signals two events,
     * so the port calls it once, from {@link #learnRune}.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the object-knowledge runtime. Callers already
     * invoke it in the right places, so filling it in should not need them changed.
     */
    public void updateObjectKnowledge() {
        // Stub class TODO: Implement
    }

    /**
     * Learns every rune the player's race knows from birth — the elements it resists or is
     * vulnerable to, and the object flags it carries innately. The port of C's
     * {@code player_learn_innate}.
     *
     * <p>A character does not have to find a ring of free action to know what free action feels
     * like when it is part of their body; the point of this pass is that a race's own properties
     * are legible to it from the start, and so are the runes naming them.
     *
     * <p><b>Both loops learn silently.</b> {@link #learnRune} is called with {@code printMessage}
     * false, because this runs at birth and a dwarf does not want a message telling them they have
     * noticed they are a dwarf. That is C's choice too, and the reason {@link #learnRune} takes the
     * flag at all.
     *
     * <p>The element loop skips {@link ElementEnum#ELEM_NONE} and {@link ElementEnum#ELEM_MAX},
     * which are sentinels rather than elements; C has no equivalent of the former and excludes the
     * latter by bounding at {@code ELEM_MAX}. Elements above the highest one carrying a resistance
     * rune answer {@code null} from {@link Rune#runeIndex(ElementEnum)}, which {@link #learnRune}
     * logs and ignores — C would index {@code rune_list[-1]}, so this is a place the port is
     * deliberately safer rather than merely different.
     *
     * <p>The flag loop walks all of {@link ObjectFlag} and asks the race about each, where C walks
     * only the bits actually set, with {@code of_next}. Same set reached, more iterations.
     *
     * <p>C closes with {@code update_player_object_knowledge}, dropped here as in the other
     * wrappers. The reasoning differs slightly: there is no guard to make it unreachable, but each
     * {@link #learnRune} that learned anything has already updated, and if the race knows nothing
     * innately then C's call recomputes a knowledge state that never changed.
     */
    public void learnInnate() {
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX)
                continue;

            if (race.getResistKnowledge(element)) {
                Rune resistRune = Rune.runeIndex(element);
                learnRune(resistRune, false);
            }
        }

        for (ObjectFlag flag : ObjectFlag.values()) {
            if (race.getObjectFlagKnowledge(flag)) {
                Rune rune = Rune.runeIndex(flag);
                learnRune(rune, false);
            }
        }
    }

    /**
     * Learns every rune in the game at once. The port of C's {@code player_learn_all_runes}, which
     * is not part of normal play — it is what the debug command and the cheat option call, and what
     * a winner's character gets so the final dump shows everything.
     *
     * <p>C counts to {@code rune_max}; the loop here is over the rune list itself
     * ({@link ObjectRegistry#getRunes}), which is the same set in the same order, that count being
     * only the list's length.
     *
     * <p><b>Silent.</b> {@link #learnRune} is called with {@code printMessage} false for the obvious
     * reason: announcing several hundred discoveries one at a time is not a message, it is a wall.
     * Same reasoning as {@link #learnInnate}, and the second reason the flag exists.
     *
     * <p>Learning is left to run per rune rather than short-circuited, so anything already known
     * falls out at {@link #learnRune}'s own guard and the trailing
     * {@link #updateObjectKnowledge} fires once per rune actually learned.
     */
    public void learnAllRunes() {
        for (Rune rune : ObjectRegistry.getRunes()) {
            learnRune(rune, false);
        }
    }

    /**
     * Learns the to-AC rune from whatever the player is wearing, on the occasion of being
     * attacked. The port of C's {@code equip_learn_on_defend} ({@code obj-knowledge.c:1970}), the
     * first of the {@code equip_learn_*} family and the model for the rest.
     *
     * <p>The premise is that a property announces itself when it does its job. A blow that lands
     * less heavily than it should have is evidence that something is adding to the armour class,
     * and a blow is the only thing that can produce that evidence — which is why armour is learned
     * by being hit rather than by being examined.
     *
     * <p><b>Three sources are checked, and the first success ends the method.</b> The leading guard
     * and the one at the foot of the loop are the same test: once
     * {@link KnownObject#toAIsKnown} answers true there is nothing further to learn, so the walk
     * stops rather than announcing the same rune from every remaining slot. That early return is
     * also what makes the shape at the end reachable only for an unhelmeted, unarmoured player.
     *
     * <ol>
     *   <li>each equipped item's own bonus, via {@link ItemObject#getToAC} tested against zero —
     *       the faithful port of C's plain {@code if (obj->to_a)}, which is available because the
     *       item carries the figure it rolled rather than the dice it rolled from;</li>
     *   <li>each equipped item's curses, via {@link ItemObject#cursesFindToA}, which learns the
     *       curse's rune as well as the to-AC one;</li>
     *   <li>the player's assumed shape, whose {@link PlayerShape#getToAc} is a flat parsed
     *       {@code int} — a bear's hide is a to-AC bonus like any other.</li>
     * </ol>
     *
     * <p>An empty slot is skipped, standing in for C's {@code if (obj)} around the whole body:
     * {@code slot_object} answers NULL for a slot with nothing in it, which is most of them for most
     * characters. C's {@code assert(obj->known)} has no counterpart here — it is a debug-build check
     * that the known counterpart was attached, never a condition on learning, and folding it into
     * the test above would quietly skip items instead of failing loudly. See
     * {@link ItemObject#isKnown} for why that reading of the name is a trap.
     *
     * <p>The shape branch drops C's {@code lookup_player_shape(p->shape->name)}, which re-fetches by
     * name the definition {@code p->shape} already points at.
     *
     * <p>Function equipLearnOnDefend coded before 260815, commented in full before 260815, updated on
     * 260815 when the item's own bonus arm stopped being a stub.
     */
    public void equipLearnOnDefend() {
        if (itemKnowledge.toAIsKnown()) return;

        for (EquipSlot slot : body.getSlots()) {
            ItemObject slotObject = slot.getItem();
            if (slotObject == null) continue;
            if (slotObject.getToAC() != 0) {
                learnRune(Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_A), true);
            }
            slotObject.cursesFindToA(this);
            if (itemKnowledge.toAIsKnown()) return;
        }
        if (shape != null) {
            if (shape.getToAc() != 0) {
                learnRune(Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_A), true);
            }
        }
    }

    /**
     * Learns the to-hit rune from whatever the player is wearing, on the occasion of loosing a
     * missile. The port of C's {@code equip_learn_on_ranged_attack} ({@code obj-knowledge.c:2003}).
     *
     * <p>Same premise as {@link #equipLearnOnDefend}, applied to accuracy: a shot that flies truer
     * than the archer had any right to expect is evidence that something is helping, and only
     * shooting can produce that evidence. Only to-hit is learned here — a missile's damage is the
     * launcher's and the ammunition's business, so a ranged attack says nothing about to-damage.
     *
     * <p><b>Two slots are skipped, and this is the reason the method exists separately from
     * {@link #equipLearnOnMeleeAttack}.</b> C skips {@code slot_by_name(p, "weapon")} and
     * {@code slot_by_name(p, "shooting")}; {@code body.txt} pairs those names one-to-one with the
     * slot types ({@code slot:WEAPON:weapon}, {@code slot:BOW:shooting}), so the port compares
     * {@link EquipSlot#getType} and needs no lookup by name. The melee weapon is skipped because a
     * sword hanging at the belt cannot have helped the shot; the launcher is skipped because its
     * contribution cannot be told apart from the archer's own skill.
     *
     * <p>Otherwise the shape is {@link #equipLearnOnDefend}'s: an empty slot is skipped, each
     * surviving item is asked about its own bonus and then about its curses via
     * {@link ItemObject#cursesFindToH}, the walk stops at the first success because
     * {@link KnownObject#toHIsKnown} has nothing left to gain, and the shape branch at the end is
     * therefore reachable only by a player carrying nothing that could teach it.
     *
     * <p>The item's own bonus goes through {@link ItemObject#hasStandardToH} rather than a non-zero
     * test on the figure, exactly as {@link #equipLearnOnMeleeAttack} does — C calls the same
     * predicate from both. Body armour carries a to-hit penalty as standard equipment, so a plain
     * {@code getToHit() != 0} would have every archer in a hauberk learning the rune from their
     * armour.
     *
     * <p>Function equipLearnOnRangedAttack coded on 260815, commented in full on 260815,
     * updated on 260815 to test the predicate the right way round.
     */
    public void equipLearnOnRangedAttack() {
        if (itemKnowledge.toHIsKnown()) return;

        for (EquipSlot slot : body.getSlots()) {
            ItemObject slotObject = slot.getItem();
            if (slotObject == null || slot.getType() == EquipmentSlotsEnum.EQUIP_WEAPON
                    || slot.getType() == EquipmentSlotsEnum.EQUIP_BOW) continue;
            if (!slotObject.hasStandardToH()) {
                learnRune(Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H), true);
            }
            slotObject.cursesFindToH(this);
            if (itemKnowledge.toHIsKnown()) return;
        }
        if (shape != null) {
            if (shape.getToHit() != 0) {
                learnRune(Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H), true);
            }
        }
    }

    /**
     * Learns the to-hit and to-damage runes from whatever the player is wearing, on the occasion
     * of striking a blow. The port of C's {@code equip_learn_on_melee_attack}
     * ({@code obj-knowledge.c:2039}), the largest of the {@code equip_learn_*} family because it is
     * the only one that pursues two runes at once.
     *
     * <p>That pairing is what makes the method's guards different in kind from its siblings'. Both
     * the leading test and the one at the foot of the loop are conjunctions: there is nothing left
     * to learn only when {@link KnownObject#toHIsKnown} <em>and</em>
     * {@link KnownObject#toDIsKnown} are both satisfied, so a player who has already worked out
     * their weapon's damage keeps walking the remaining slots in the hope of learning accuracy from
     * their gloves. Getting either guard down to a single term would end the walk early and quietly
     * lose the other rune.
     *
     * <p><b>One slot is skipped.</b> C skips {@code slot_by_name(p, "shooting")} and nothing else —
     * a bow is no part of a sword-stroke, but the weapon very much is, which is precisely the slot
     * {@link #equipLearnOnRangedAttack} has to leave alone. As there, the port compares
     * {@link EquipSlot#getType} rather than looking the slot up by name.
     *
     * <p><b>The two tests are not symmetrical.</b> To-damage is a plain non-zero check on
     * {@link ItemObject#getToDam}, matching C's {@code if (obj->to_d)}; to-hit goes through
     * {@link ItemObject#hasStandardToH}, because body armour carries a to-hit penalty as standard
     * equipment and testing it against zero would teach the rune to anyone who wore a hauberk. The
     * curse pair {@link ItemObject#cursesFindToH} and {@link ItemObject#cursesFindToD} is then asked
     * for both, and each learns the offending curse's own rune alongside the combat one.
     *
     * <p>The shape branch tests {@link PlayerShape#getToHit} and {@link PlayerShape#getToDam}
     * independently rather than as alternatives, since a shape may well grant both.
     *
     * <p>Function equipLearnOnMeleeAttack coded on 260815, commented in full on 260815.
     */
    public void equipLearnOnMeleeAttack() {
        if (itemKnowledge.toDIsKnown() && itemKnowledge.toHIsKnown()) return;

        for (EquipSlot slot : body.getSlots()) {
            ItemObject slotObject = slot.getItem();
            if (slotObject == null || slot.getType() == EquipmentSlotsEnum.EQUIP_BOW) continue;
            if (!slotObject.hasStandardToH())
                learnRune(Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H), true);
            if (slotObject.getToDam() != 0)
                learnRune(Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_D), true);

            slotObject.cursesFindToD(this);
            slotObject.cursesFindToH(this);
            if (itemKnowledge.toDIsKnown() && itemKnowledge.toHIsKnown()) return;
        }
        if (shape != null) {
            if (shape.getToDam() != 0) {
                learnRune(Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_D), true);
            }
            if (shape.getToHit() != 0) {
                learnRune(Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H), true);
            }
        }
    }

    /**
     * Learns one named object flag from whatever the player is wearing, on the occasion of that
     * flag having just done something. The port of C's {@code equip_learn_flag}
     * ({@code obj-knowledge.c:2084}), and the busiest member of the family — upstream calls it from
     * some thirty places, each naming the flag its own event could have revealed: {@code OF_AFRAID}
     * on failing to attack, {@code OF_FEATHER} on a fall, {@code OF_HOLD_LIFE} on a drain,
     * {@code OF_TRAP_IMMUNE} on a trap that did not fire.
     *
     * <p><b>Unlike its siblings, this one does not stop early.</b> The {@code equip_learn_on_*}
     * methods return the moment their rune is known, because a second slot cannot teach the same
     * thing twice. Here the walk always runs to the end of the body, and the reason is the
     * {@code else} branch: every slot has bookkeeping to do whether or not anything was learned, so
     * there is nothing to be saved by leaving early.
     *
     * <p><b>Three things happen per slot, and the first two are alternatives.</b>
     *
     * <ol>
     *   <li><b>The item has the flag.</b> If the player cannot yet read it, the flag announces
     *       itself — {@link ItemObject#description} names the item, {@link ItemObject#flagMessage}
     *       delivers the property's own wording, and the rune is learned. The inner
     *       {@link KnownObject#flagIsKnown} guard is what keeps a player wearing three items with
     *       the same flag from being told about it three times.</li>
     *   <li><b>The item does not have the flag.</b> Then its absence is itself worth recording, but
     *       only while there is anything left to learn about the item: an item that is not yet
     *       {@link ItemObject#isFullyKnown} gets the flag switched on in its known set, marking that
     *       it has had its chance to display the property and did not. This is how an item is
     *       identified by being used rather than examined — enough events rule out enough
     *       properties, and what remains is the item.</li>
     *   <li><b>Either way, the curses are asked.</b> The flag may be riding on a curse rather than
     *       on the item, which is a different question from both of the above, so
     *       {@link ItemObject#cursesFindFlags} runs unconditionally. It takes a set rather than a
     *       single flag because its other callers pass real masks; the one-element set built here is
     *       C's {@code f}, assembled at the top of {@code equip_learn_flag} for exactly this
     *       purpose.</li>
     * </ol>
     *
     * <p>The leading guard is C's {@code if (!flag) return;}. C's flag is an index into the flag
     * table and its zero is {@link ObjectFlag#OF_NONE}, so the enum equivalent has to name that
     * sentinel rather than test for null — {@link ObjectFlag#OF_MAX} is rejected on the same
     * grounds, being the other end-marker and no more a real flag than the first.
     *
     * <p>As elsewhere in the family, C's {@code assert(obj->known)} has no counterpart; the null it
     * asserts against is handled where it would actually be dereferenced, in
     * {@link ItemObject#getKnownFlags}. See {@link #equipLearnOnDefend} for the reasoning.
     *
     * <p><b>Outstanding:</b> {@link ItemObject#description} is still a stub, so both this method's
     * message and the one {@link ItemObject#cursesFindFlags} sends name the item with a
     * placeholder.
     *
     * <p>Function equipLearnFlag coded on 260815, commented in full on 260815, updated on 260815
     * once the curse arm stopped being a stub.
     *
     * @param flag the flag whose moment this is; ignored if null or a sentinel
     */
    public void equipLearnFlag(ObjectFlag flag) {
        if (flag == null || flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX) return;
        for (EquipSlot slot : body.getSlots()) {
            ItemObject slotObject = slot.getItem();
            if (slotObject == null) continue;
            if (slotObject.hasFlag(flag)) {
                if (!itemKnowledge.flagIsKnown(flag)) {
                    Flag<ObjectDescription> descriptionMode = new Flag<>(ObjectDescription.class);
                    descriptionMode.on(ObjectDescription.ODESC_BASE);

                    String objDesc = slotObject.description(descriptionMode, this);
                    slotObject.flagMessage(flag, objDesc);
                    learnRune(Rune.runeIndex(flag), true);
                }
            } else if (!slotObject.isFullyKnown()) {
                Flag<ObjectFlag> knownFlags = slotObject.getKnownFlags();
                if (knownFlags != null)
                    knownFlags.on(flag);
            }

            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            flags.on(flag);

            slotObject.cursesFindFlags(this, flags);
        }
    }

    /**
     * Reports whether the player can already read the given object flag — the port of C's
     * {@code of_has(p->obj_k->flags, flag)}.
     *
     * <p>This is knowledge, not equipment. It asks nothing about what the player is wearing: once
     * the rune for a flag has been learned it is legible on every item that carries it, now and in
     * future. The companion question — does <em>this</em> item have the flag — is
     * {@link ItemObject#hasFlag}, and the two are played against each other throughout the learning
     * code.
     *
     * <p>It exists as a method on {@link Player} because the flag-learning that needs it lives on
     * {@link ItemObject}, and an item has no business reaching into {@code itemKnowledge}. The same
     * reasoning puts {@link #learnRune} where it is: knowledge is the player's, so an item asks
     * rather than writes. {@link ItemObject#cursesFindFlags} is the caller.
     *
     * <p>Function hasKnownFlag coded on 260815, commented in full on 260815.
     *
     * @param testSubject the flag to ask about
     * @return whether the player has learned that flag's rune
     */
    public boolean hasKnownFlag(ObjectFlag testSubject) {
        return itemKnowledge.flagIsKnown(testSubject);
    }
}