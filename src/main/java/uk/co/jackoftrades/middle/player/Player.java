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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerOption;
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
 * {@link uk.co.jackoftrades.middle.game.gameengine.GameState#getPlayer()} seam rather than a global,
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
     * The player's race - the port of C's {@code p->race}.
     */
    private PlayerRace race;
    /** The player's class - the port of C's {@code p->class}. */
    private PlayerClass playerClass;

    /** The player's current grid on the level - the port of C's {@code p->grid}. */
    private Loc grid;
    /** The player's grid before leaving for an arena - the port of C's {@code p->old_grid}. */
    private Loc oldGrid;

    /** Number of sides on the player's hit die - the port of C's {@code p->hitdie}. */
    private int hitDie;
    /** Experience factor: the class/race multiplier applied to experience - the port of C's {@code p->expfact}. */
    private int expFact;

    /** The character's age in years - the port of C's {@code p->age}. */
    private int age;
    /** The character's height - the port of C's {@code p->ht}. */
    private int height;
    /** The character's weight - the port of C's {@code p->wt}. */
    private int weight;

    /** Current gold - the port of C's {@code p->au}. */
    private int au;

    /** Deepest dungeon level yet reached - the port of C's {@code p->max_depth}. */
    private int maxDepth;
    /** Level that Word of Recall will return the player to - the port of C's {@code p->recall_depth}. */
    private int recallDepth;
    /** Current dungeon depth - the port of C's {@code p->depth}. */
    private int depth;

    /** Highest character level yet attained - the port of C's {@code p->max_lev}. */
    private int maxLevel;
    /** Current character level - the port of C's {@code p->lev}. */
    private int level;

    /** Highest experience total yet held (never drained below) - the port of C's {@code p->max_exp}. */
    private int maxExp;
    /** Current experience - the port of C's {@code p->exp}. */
    private int exp;
    /** Fractional part of the current experience, scaled by 2^16 - the port of C's {@code p->exp_frac}. */
    private int expFrac;

    /** Maximum hit points - the port of C's {@code p->mhp}. */
    private int maxHP;
    /** Current hit points - the port of C's {@code p->chp}. */
    private int currentHP;
    /** Fractional part of the current hit points, scaled by 2^16 - the port of C's {@code p->chp_frac}. */
    private int chpFrac;

    /** Maximum spell points (mana) - the port of C's {@code p->msp}. */
    private int maxSP;
    /** Current spell points (mana) - the port of C's {@code p->csp}. */
    private int sp;
    /** Fractional part of the current spell points, scaled by 2^16 - the port of C's {@code p->csp_frac}. */
    private int cspFrac;

    /** Current "maximal" stat values, before drain - the port of C's {@code p->stat_max}. */
    private HashMap<Stats, Integer> statMax;
    /** Current "natural" stat values - the port of C's {@code p->stat_cur}. */
    private HashMap<Stats, Integer> statCur;
    /** Tracks stats remapped by a temporary stat swap - the port of C's {@code p->stat_map}. */
    private HashMap<Stats, Integer> statMap;

    /** Turns remaining on each timed effect - the port of C's {@code p->timed}. */
    private HashMap<TimedEffect, Integer> timed;

    /** Turns until a pending Word of Recall fires - the port of C's {@code p->word_recall}. */
    private int wordRecall;
    /** Turns until a pending Deep Descent fires - the port of C's {@code p->deep_descent}. */
    private int deepDescent;

    /** Current energy; the player acts once it reaches the action threshold - the port of C's {@code p->energy}. */
    private int energy;
    /** Total energy ever used, including resting - the port of C's {@code p->total_energy}. */
    private int totalEnergy;
    /** Number of player turns spent resting - the port of C's {@code p->resting_turn}. */
    private int restingTurn;

    /** Current nutrition - the port of C's {@code p->food}. */
    private int food;

    /** Non-zero while the player is temporarily showing ignored items - the port of C's {@code p->unignoring}. */
    private int unignoring;

    /**
     * Bloodlust-coercion skip state for the next command - the port of C's {@code p->skip_cmd_coercion}.
     *
     * @see #getSkipCmdCoercion()
     */
    private int skipCmdCoercion;
    /** Per-spell knowledge/learning flags - the port of C's {@code p->spell_flags}. */
    private int spellFlags; // TODO: Change this once we know what we are dealing with
    /** Order in which spells were learned - the port of C's {@code p->spell_order}. */
    private int spellOrder; // TODO: Change this once we know what we are dealing with

    /** The character's full name - the port of C's {@code p->full_name}. */
    private String fullName;
    /** Cause of death - the port of C's {@code p->died_from}. */
    private String diedFrom;
    /** The character's background history text - the port of C's {@code p->history}. */
    private String history;
    /** The character's quest history - the port of C's {@code p->quests}. */
    private ArrayList<Quest> quests;
    /** Total-winner flag: set once the player has won the game - the port of C's {@code p->total_winner}. */
    private int totalWinner;

    /** Cheating flags that disqualify the character from the score list - the port of C's {@code p->noscore}. */
    private int noScore;

    /** True once the player has died - the port of C's {@code p->is_dead}. */
    private boolean isDead;
    /** True while the player is in wizard mode - the port of C's {@code p->wizard}. */
    private boolean isWizard;

    /** Hit points gained at each level, one entry per level - the port of C's {@code p->player_hp}. */
    private int[] playerHP;

    /** Saved birth gold, used by quickstart when {@code birth_money} is off - the port of C's {@code p->au_birth}. */
    private int auBirth;
    /** Saved birth "natural" stat values, for quickstart - the port of C's {@code p->stat_birth}. */
    private HashMap<Stats, Integer> statsBirth;
    /** Saved birth height, for quickstart - the port of C's {@code p->ht_birth}. */
    private int htBirth;
    /** Saved birth weight, for quickstart - the port of C's {@code p->wt_birth}. */
    private int wtBirth;

    /** The player's option settings - the port of C's {@code p->opts}. */
    private PlayerOptions options;
    /** The player's structured history log (see {@code player-history.c}) - the port of C's {@code p->hist}. */
    private PlayerHistory playerHistory;

    /** The player's body plan, i.e. the equipment slots available - the port of C's {@code p->body}. */
    private PlayerBody body;
    /** The player's current shape, if shapechanged - the port of C's {@code p->shape}. */
    private PlayerShape shape;

    /** The player's real carried gear - the port of C's {@code p->gear}. */
    private ArrayList<ItemObject> gear;
    /** The player's gear as currently known to the player - the port of C's {@code p->gear_k}. */
    private ArrayList<ItemObject> gearKnown;

    /** The player's accumulated object knowledge ("runes") - the port of C's {@code p->obj_k}. */
    private ArrayList<ItemObject> itemObjectsKnown;
    /** The player's known version of the current level - the port of C's {@code p->cave}. */
    private Chunk cave;

    /** The player's fully calculated state - the port of C's {@code p->state}. */
    private PlayerState state;
    /** What the player can know of the calculated state - the port of C's {@code p->known_state}. */
    private PlayerState knownState;
    /** Transient per-turn bookkeeping - the port of C's {@code p->upkeep}. */
    private PlayerUpkeep playerUpkeep;

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
    public boolean opt(@NotNull PlayerOption type) {
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
}
