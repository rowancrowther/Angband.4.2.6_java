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
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftradesltd.middle.effect.EffectUtil;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.projection.Source;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.*;
import uk.co.jackoftradesltd.middle.player.enums.*;

import java.util.*;

/**
 * The player - the port of C's {@code struct player} (player.h), and the central mutable object of a
 * game in progress. It gathers everything about the current character: identity (race, class, name,
 * history), the birth and derived statistics, position and depth, resources (HP, SP, gold, food,
 * energy), the timed effects and options in force, the body plan and any assumed shape, the carried
 * gear, and the transient per-turn bookkeeping held in {@link PlayerUpkeep}.
 *
 * <p>The middle layer reaches the live player through the swappable
 * {@link uk.co.jackoftradesltd.middle.game.gameengine.GameState#getPlayer()} boundary rather than a global,
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
     * Current spell points (mana) - the port of C's {@code p->csp}.
     */
    private int curSp;
    
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
    private long maxExp;
    /**
     * Current experience - the port of C's {@code p->exp}.
     */
    private long exp;
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
        body = PlayerRegistry.lookupPlayerBody(0);
        // TO be changed to a chunk on level creation
        cave = null;
        gear = new ArrayList<>();
        gearKnown = new ArrayList<>();
        grid = Loc.zero;
        isDead = false;
        isWizard = false;
        knownState = null;
        oldGrid = Loc.zero;
        playerClass = null;
        playerHistory = new PlayerHistory();
        quests = new ArrayList<>();
        race = PlayerRegistry.getFirstPlayerRace();
        // Crash if there are no races
        if (race == null) {
            logger.fatal("No player races loaded - game crashing.");
            throw new IllegalStateException("No player races loaded - game crashing.");
        }
        shape = null;
        statCur = new HashMap<>();
        statMax = new HashMap<>();
        statMap = new HashMap<>();
        statsBirth = new HashMap<>();
        state = null;
    }
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
    KnownObject itemKnowledge;

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
     * Sets the character's age in years — C's {@code p->age}.
     *
     * <p>Written at birth by {@code get_ahw} as {@code race->b_age + randint1(race->m_age)}
     * ({@code player-birth.c:356}), saved and restored across the birth "roller" by
     * {@code save_roller_data}/{@code load_roller_data} ({@code player-birth.c:153,196}), and read
     * back from a savefile by {@code rd_player} ({@code load.c:718}). Nothing in the game ages a
     * character after birth; the debug and stats-collection builds simply overwrite it
     * ({@code wiz-debug.c:31}, {@code main-stats.c:471}).
     *
     * <p>C's field is an {@code int16_t} ({@code player.h:518}) while the port uses {@code int}, so
     * the port accepts values C would wrap. No birth roll can reach that range — the largest
     * {@code b_age + m_age} in {@code p_race.txt} is the High-Elf's 130 — and no clamping is
     * applied here, because C applies none either.
     *
     * <p>Function setAge commented in full on 260902.
     *
     * @param age the age in years to store
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets the player's remembered version of the current level - C's {@code p->cave}.
     *
     * <p>C allocates it in {@code prepare_next_level} ({@code generate.c:1241}), sized from the real
     * chunk, and replaces it on every level change; it is null before the first level is generated
     * and across the persistent-level swap. The port's level generation has not reached that point,
     * so nothing calls this yet and the field stays null - code that reads it has to cope with that.
     *
     * <p>Function setCave commented in full on 260827.
     *
     * @param cave the player's remembered level, or {@code null} between levels
     */
    public void setCave(Chunk cave) {
        this.cave = cave;
    }

    /**
     * @return the player's current spell points - the port of C's {@code p->csp}
     */
    public int getCurSp() {
        return curSp;
    }

    /**
     * Sets the player's current spell points - the port of writing C's {@code p->csp}.
     *
     * <p>Nothing here clamps to the maximum. C does not either: the callers that lower the ceiling
     * are the ones that pull the current value down with it - {@code calc_mana}
     * ({@code player-calcs.c:1551}) caps it only when the maximum has just changed - so a caller
     * that writes a value above {@link #getMaxSP} gets exactly what it asked for.
     *
     * <p>Function setCurSp commented in full on 260901.
     *
     * @param curSp the new current spell points
     */
    public void setCurSp(int curSp) {
        this.curSp = curSp;
    }

    /**
     * Sets the fractional part of the current spell points - the port of writing C's
     * {@code p->csp_frac}.
     *
     * <p>The fraction is a sixteen-bit remainder carried alongside {@link #getCurSp} so that
     * regeneration of less than a whole point still accumulates. It is cleared, not carried, whenever
     * the current value is forced down to a new maximum.
     *
     * <p>Function setCspFrac commented in full on 260901.
     *
     * @param cspFrac the new fractional spell points, scaled by 2^16
     */
    public void setCspFrac(int cspFrac) {
        this.cspFrac = cspFrac;
    }

    /**
     * The player's race — the port of C's {@code p->race}.
     *
     * <p><b>The race is shared, not owned.</b> What comes back is the registry's own entry, held
     * once and pointed at by every character of that race, exactly as C's {@code p->race} points
     * into the {@code races} array. It is read-only in practice: writing through it would change the
     * race for every player and would outlive the character, so callers take what they need from it
     * — see {@code PlayerBirth.embody}, which copies the body rather than keeping the reference.
     *
     * <p>Can be {@code null}. A character is built in stages and the race is chosen partway through,
     * so code that runs during creation has to cope with not having one yet; C reaches the same
     * point with an {@code assert(p->race)} in {@code player_embody} ({@code player-birth.c:369}).
     *
     * <p>Function getRace commented in full on 260901.
     *
     * @return the player's race, or {@code null} before one has been chosen
     */
    public PlayerRace getRace() {
        return race;
    }

    /**
     * The shape the player is currently in — the port of C's {@code p->shape}.
     *
     * <p><b>The shape is shared, not owned.</b> What comes back is the registry's own entry, the
     * one {@link PlayerRegistry#lookupPlayerShape} hands out and every player of that form points
     * at, exactly as C's {@code p->shape} points into the {@code shapes} list. It describes the
     * form rather than this character's state, so nothing may be written through it: a shape's
     * contribution is read out and added to the calculated {@link PlayerState} by
     * {@link PlayerCalcs#calcShapechange}, never stored back.
     *
     * <p><b>"Normal" is a shape, not the absence of one.</b> C gives every character
     * {@code lookup_player_shape("normal")} in {@code player_init} ({@code player-birth.c:457}) and
     * returns them to it in {@code player_resume_normal_shape} ({@code player-util.c:1053}), so the
     * question "is this player shapechanged?" is a name comparison and not a null test — ask
     * {@link #isShapeChanged}, which does exactly that.
     *
     * <p><b>Can be {@code null} in the port, where C's cannot.</b> The constructor leaves the field
     * null and nothing assigns it yet: the shapechange effect that sets it in C
     * ({@code effect-handler-general.c:3453}) is not ported, and neither is the birth assignment
     * above. C only ever sees a null shape while loading a save, and treats that as a corrupt file
     * ({@code load.c:691}). So callers here have to guard, and the ported readers do — see
     * {@link PlayerCalcs#calcShapechange}, which returns the totals untouched, and the shape branches of
     * {@code PlayerKnowledge.equipLearnOnDefend}, {@code equipLearnOnRangedAttack} and
     * {@code equipLearnOnMeleeAttack}.
     *
     * <p>Function getShape commented in full on 260901.
     *
     * @return the player's current shape, or {@code null} while none has been set
     */
    public PlayerShape getShape() {
        return shape;
    }

    /**
     * Sets the player's body — the equipment slots they can wear things in, the port of C's
     * {@code p->body}.
     *
     * <p><b>The body passed in must belong to this player alone.</b> C has no equivalent setter
     * because {@code p->body} is an embedded struct rather than a pointer, so its assignment in
     * {@code player_embody} ({@code player-birth.c:369}) copies by construction. In Java the field
     * is a reference, and that safety has to be supplied by the caller: pass a
     * {@link PlayerBody#copy} of a race's template, never the template itself, or every character of
     * the race ends up wearing the same equipment and writing into the registry's data.
     *
     * <p>The one caller is {@code PlayerBirth.embody}, which does exactly that. The constructor
     * fills the field separately, from {@code PlayerRegistry.lookupPlayerBody(0)} — which copies
     * before handing the body back — so a player has a body of their own before this is ever
     * called, and this replaces it once the race is known.
     *
     * <p>Function setBody commented in full on 260901.
     *
     * @param body the player's own body, not a shared template
     */
    public void setBody(PlayerBody body) {
        this.body = body;
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
        return timed.getOrDefault(timedEffect, 0);
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
     * {@link uk.co.jackoftradesltd.middle.game.gameengine.Command#getItem}).
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
     * Returns how many turns remain on a timed effect, answering with a caller-chosen figure when the
     * effect has no entry at all - the port of reading C's {@code p->timed[idx]} for a caller that
     * wants to name the value an absent slot stands for.
     *
     * <p>C's {@code timed} is a fixed array with a slot for every effect, so nothing can be missing
     * there. The port holds a {@link java.util.HashMap}, and the constructor seeds it with a zero for
     * every {@link TimedEffect}, so in a live character nothing is missing here either and
     * {@code defaultValue} is never reached. It exists for the half-built character - a test fixture
     * that populates the map itself - which would otherwise read an absent key. Passing {@code 0}, as
     * {@link PlayerTimed#playerDecTimed} does, makes this exactly {@link #getTimedEffect}.
     *
     * <p>The value is a turn count, and callers compare it against zero rather than test for
     * presence: an effect that is not running is a zero in its slot, not a missing slot.
     *
     * <p>Function getTimedEffectOrDefault coded on 260901, commented in full on 260901.
     *
     * @param timedEffect  the timed effect to query
     * @param defaultValue the figure to answer with when the effect has no entry
     * @return the turns remaining on the effect, or {@code defaultValue} when it has no entry
     */
    public int getTimedEffectOrDefault(@NotNull TimedEffect timedEffect, int defaultValue) {
        return timed.getOrDefault(timedEffect, defaultValue);
    }

    /**
     * Writes a timed effect's turn count directly - the port of C's bare {@code p->timed[idx] = value}
     * assignment, as at {@code player-calcs.c:2154} and {@code player-calcs.c:2161}, where a stun
     * cancels fast casting, {@code mon-util.c:1287}, and {@code player-birth.c:1021}.
     *
     * <p>This is deliberately not {@link PlayerTimed#setTimed}. The C sites that assign the slot outright are the
     * ones that must not run the timed-effects machinery: no grade message, no notification, no
     * disturb, and none of the redraw or update flags the effect declares. {@code calc_bonuses} is the
     * clearest case - it is already inside an update, so announcing the change or asking for another
     * recalculation would be wrong. Every ordinary route into an effect goes through
     * {@link PlayerTimed#setTimed}, {@link PlayerTimed#incTimed} or
     * {@link PlayerTimed#playerDecTimed(Player, TimedEffect, int, boolean, boolean)}, which do all of that.
     *
     * <p>Nothing is validated here, exactly as in C: the count is stored as given, and the caller owns
     * the decision that it is a sensible one.
     *
     * @param timedEffect the timed effect to write
     * @param value       the turn count to store
     */
    public void putTimed(@NotNull TimedEffect timedEffect, int value) {
        timed.put(timedEffect, value);
    }

    /**
     * Returns one of the player's current "natural" stat values - the port of reading C's
     * {@code p->stat_cur[stat]}. This is the drained value: it starts equal to the maximum and falls
     * below it while the stat is damaged, so it is the figure everything the character can actually
     * do is computed from ({@link PlayerCalcs} feeds it through
     * {@link PlayerUtils#modifyStatValue} to reach the state's {@code statUse}).
     *
     * <p>Values use the C encoding: 3 to 18 for the ordinary range, then {@code 18 + percentile} for
     * the exceptional range, up to {@code 18 + 100} (118, displayed as 18/100). Nothing is scaled or
     * clamped on the way out.
     *
     * <p>Where C subscripts a zeroed array, the port reads a map, so a stat never written at birth
     * would fail here rather than reading as zero. Every stat is populated during birth, so this is
     * a difference in failure mode, not in behaviour; the same applies to {@code STAT_NONE} and
     * {@code STAT_MAX}, which have no slot in C either.
     *
     * @param stat the stat to read; one of the five real stats, not {@code STAT_NONE} or
     *             {@code STAT_MAX}
     * @return the current natural value of that stat
     */
    public int getCurStatValue(Stats stat) {
        return statCur.get(stat);
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
     * Returns the bloodlust-coercion skip state - the port of C's {@code p->skip_cmd_coercion}. A
     * non-zero value tells
     * {@link uk.co.jackoftradesltd.middle.game.gameengine.CommandProcessor#processCommand} to skip the
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
     * Returns one of the player's current "maximal" stat values - the port of reading C's
     * {@code p->stat_max[stat]}. This is the value before any drain, so it never sits below the
     * matching current value: {@link #playerStatInc} drags it up whenever a gain takes the current value
     * past it, and only a permanent drain ({@link #statDec}) lowers it. Restoring a stat means
     * copying this back over the current value.
     *
     * <p>Values use the same encoding as {@link #getCurStatValue}: 3 to 18, then
     * {@code 18 + percentile} up to {@code 18 + 100}, and the same map-versus-array caveat applies
     * to an unwritten stat.
     *
     * @param stat the stat to read; one of the five real stats, not {@code STAT_NONE} or
     *             {@code STAT_MAX}
     * @return the maximal value of that stat, before drain
     */
    public int getMaxStatValue(Stats stat) {
        return statMax.get(stat);
    }

    /**
     * @return the player's maximum spell points - the port of C's {@code p->msp}; zero for a
     * character with no spell realm, which is how C tests for one ({@code p->msp} guards
     * {@code player-calcs.c:2335})
     */
    public int getMaxSP() {
        return maxSP;
    }

    /**
     * Sets the player's maximum spell points - the port of writing C's {@code p->msp}.
     *
     * <p>This is the ceiling only; it does not touch {@link #setCurSp} or {@link #setCspFrac}, which
     * {@code calc_mana} writes separately when the new ceiling has dropped below what the player is
     * carrying.
     *
     * <p>Function setMaxSP commented in full on 260901.
     *
     * @param maxSP the new maximum spell points
     */
    public void setMaxSP(int maxSP) {
        this.maxSP = maxSP;
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
     * Reports whether the timed-effect table holds an entry for an effect at all. This has no
     * counterpart in C, where {@code p->timed} is an array of {@code TMD_MAX} slots and every effect
     * therefore always has one; it is the port's guard over a {@link java.util.HashMap} which, in a
     * character assembled by hand, could be missing a key.
     *
     * <p>Presence is not the same question as "is the effect running". A seeded table contains every
     * effect with a count of zero, so this answers {@code true} for effects the player is not under.
     * The call sites in {@link PlayerTimed} pair it with a {@code getTimedEffect(...) != 0} test for
     * that reason.
     *
     * <p>Function playerTimedContains coded on 260901, commented in full on 260901.
     *
     * @param timedEffect the timed effect to look for
     * @return {@code true} when the table has an entry for the effect, whatever its count
     */
    public boolean playerTimedContains(TimedEffect timedEffect) {
        return timed.containsKey(timedEffect);
    }

    /**
     * Reports whether the player has a timed-effect table at all. Like {@link #playerTimedContains}
     * this has no counterpart in C, where {@code p->timed} is an array embedded in the player struct
     * and cannot be absent; here it is a {@link java.util.HashMap} reference, which a character built
     * without the constructor's seeding leaves null.
     *
     * <p>It is the outermost of the three guards {@link PlayerTimed} stacks - table present, then
     * entry present, then count non-zero - and answering {@code true} says nothing about whether any
     * effect is running.
     *
     * <p>Function playerHasTimed coded on 260901, commented in full on 260901.
     *
     * @return {@code true} when the timed-effect table has been created
     */
    public boolean playerHasTimed() {
        return timed != null;
    }

    /**
     * @return the player's derived/calculated state - the port of C's {@code p->state}
     */
    public PlayerState getPlayerState() {
        return state;
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
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @return the player's current hit points
     */
    public int getCurrentHP() {
        return currentHP;
    }

    /**
     * Drains one stat by a single step, the way a monster's attack or a poison does, and optionally
     * makes the loss permanent. Ports {@code player_stat_dec} ({@code player.c:171}).
     *
     * <p>The scale is the stretched one described on {@link #playerStatInc}: 3 to 18 are the
     * ordinary values, and 19 to 118 hold the percentile tail. A drain takes ten points off the
     * tail, drops to 18 from anywhere inside the tail's bottom ten, or takes one point off the plain
     * part - so the expensive percentile points go quickly and the cheap ones slowly, which is the
     * reverse of how they were gained. Three is the floor and there is no branch that leaves it.
     *
     * <p>A temporary drain moves the current value and leaves the maximum, which is what lets the
     * character recover the ground later. A permanent one moves both, by the same rules applied
     * independently - the two need not fall by the same amount, since they can be sitting in
     * different bands.
     *
     * <p><b>The permanent flag replaces the answer rather than adding to it, and this matters.</b>
     * C computes {@code res} from the current value, then, when {@code permanent}, overwrites it
     * with the comparison on the maximum. So a permanent drain of a character whose maximum is
     * already at the floor of 3 reports no change and writes nothing back - even if the current
     * value would have moved. That looks like a bug and is not treated as one here: it is the
     * behaviour the C has, and a character with a maximum of 3 has nothing left to lose anyway,
     * since the current value cannot exceed it.
     *
     * <p>Nothing is written unless the answer is {@code true}. The two values are then stored as a
     * pair, which is harmless in the temporary case because the maximum was never modified.
     * {@code PU_BONUS} is raised so everything derived from the stat recomputes, and
     * {@code PR_STATS} so the stat panel repaints.
     *
     * <p>Unlike {@link #playerStatInc}, whose answer means "a gain was attempted", this one means
     * what it says: the stat actually moved.
     *
     * <p>Function statDec coded on 260831, commented in full on 260831.
     *
     * @param stat      the stat to drain
     * @param permanant whether the maximum falls as well as the current value
     * @return {@code true} if the stat was changed, {@code false} if it was already as low as this
     * call can take it
     */
    public boolean statDec(Stats stat, boolean permanant) {
        int cur = statCur.get(stat);
        int max = statMax.get(stat);

        if (cur > 18 + 10)
            cur -= 10;
        else if (cur > 18)
            cur = 18;
        else if (cur > 3)
            cur -= 1;

        boolean res = (cur != statCur.get(stat));

        if (permanant) {
            if (max > 18 + 10)
                max -= 10;
            else if (max > 18)
                max = 18;
            else if (max > 3)
                max -= 1;
            res = (max != statMax.get(stat));
        }
        
        if (res) {
            statCur.put(stat, cur);
            statMax.put(stat, max);
            getPlayerUpkeep().updateOn(PlayerUpdateEnum.PU_BONUS);
            getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_STATS);
        }

        return res;
    }

    /**
     * Re-evaluates the character level from the experience totals, the port of C's
     * {@code adjust_level} ({@code player.c}). Every route that changes experience - a gain, a
     * drain, a restore - ends here, so this is the single place the level, the maximum level and
     * the experience-related redraws are settled.
     *
     * <p>The totals are made sane first: both are floored at zero and capped at
     * {@link PlayerRegistry#PY_MAX_EXP}, and the maximum is then raised to the current total if the
     * current one is somehow the higher of the two. {@code PR_EXP} is raised and
     * {@link PlayerCalcs#handleStuff(Player)} run before any level arithmetic, so the new experience figure reaches
     * the display even when the level does not move.
     *
     * <p>Three loops follow, in C's order. The first walks the level <em>down</em> while the
     * current experience has fallen below the threshold for the level below, the second walks it
     * <em>up</em> while the experience has reached the threshold for the next level, and the third
     * walks {@code maxLevel} up against {@code maxExp} alone - which is why a drained character
     * keeps the highest level they ever reached even after the working level has fallen. Only the
     * up loop announces anything; the down loop is silent, as in C.
     *
     * <p>The thresholds come from {@link PlayerRegistry#playerExperience}, which is keyed exactly
     * as C's {@code player_exp[]} is indexed - from zero, so entry {@code 0} is the cost of
     * reaching level 2. That is why the loops read {@code level - 2}, {@code level - 1} and
     * {@code maxLevel - 1} rather than the level numbers themselves. Each threshold is scaled by
     * the character's {@code expFact} percentage, and C's integer division is preserved by doing
     * the arithmetic in {@code long}: the multiplication happens before the division by 100, so the
     * scaled figure truncates rather than rounds.
     *
     * <p>Levelling up restores all five stats unconditionally - the {@code EF_RESTORE_STAT} calls
     * sit outside the {@code verbose} gate in C, so a silent level gain still undoes any drain.
     * {@code verbose} governs only the pair of announcements: the history entry and the
     * {@code MSG_LEVEL} message.
     *
     * <p><b>Outstanding.</b> {@link EffectUtil#effectSimple} is a stub
     * ({@code EffectUtil.java:54}), so the stat restores currently do nothing; the calls are in
     * place and will start working when the effect subsystem is ported.
     *
     * <p>Function adjustLevel coded on 260831, commented in full on 260831.
     *
     * @param verbose whether a level gain is announced to the player and written to their history
     */
    private void adjustLevel(boolean verbose) {
        if (exp < 0) exp = 0;

        if (maxExp < 0) maxExp = 0;

        if (exp > PlayerRegistry.PY_MAX_EXP) exp = PlayerRegistry.PY_MAX_EXP;

        if (maxExp > PlayerRegistry.PY_MAX_EXP) maxExp = PlayerRegistry.PY_MAX_EXP;

        if (exp > maxExp) maxExp = exp;

        getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_EXP);

        PlayerCalcs.handleStuff(this);

        while ((level > 1)
                && exp < PlayerRegistry.playerExperience.getOrDefault(level - 2, 0L) * expFact / 100L) {
            level--;
        }

        while (level < PlayerRegistry.PY_MAX_LEVEL
                && exp >= PlayerRegistry.playerExperience.getOrDefault(level - 1, 0L) * expFact / 100L) {
            level++;

            // Save the highest level
            if (level > maxLevel)
                maxLevel = level;

            if (verbose) {
                // Log level updates
                String buf = "Reached level " + level;
                PlayerHistory.historyAdd(this, buf, PlayerHistoryType.HIST_GAIN_LEVEL);

                // Message
                Message.messageType(MessageType.MSG_LEVEL, "Welcome to level %d.", level);
            }

            EffectSubTypeWrapper wrapper = new EffectSubTypeWrapper(Stats.STAT_STR);
            EffectUtil.effectSimple(EffectEnum.EF_RESTORE_STAT, Source.sourceNone(), "0",
                    wrapper, 0, 0, 0, 0, null);
            wrapper = new EffectSubTypeWrapper(Stats.STAT_INT);
            EffectUtil.effectSimple(EffectEnum.EF_RESTORE_STAT, Source.sourceNone(), "0",
                    wrapper, 0, 0, 0, 0, null);
            wrapper = new EffectSubTypeWrapper(Stats.STAT_WIS);
            EffectUtil.effectSimple(EffectEnum.EF_RESTORE_STAT, Source.sourceNone(), "0",
                    wrapper, 0, 0, 0, 0, null);
            wrapper = new EffectSubTypeWrapper(Stats.STAT_DEX);
            EffectUtil.effectSimple(EffectEnum.EF_RESTORE_STAT, Source.sourceNone(), "0",
                    wrapper, 0, 0, 0, 0, null);
            wrapper = new EffectSubTypeWrapper(Stats.STAT_CON);
            EffectUtil.effectSimple(EffectEnum.EF_RESTORE_STAT, Source.sourceNone(), "0",
                    wrapper, 0, 0, 0, 0, null);
        }

        while ((maxLevel < PlayerRegistry.PY_MAX_LEVEL)
                && (maxExp >= PlayerRegistry.playerExperience.getOrDefault(maxLevel - 1, 0L) * expFact / 100L)) {
            maxLevel++;
        }

        getPlayerUpkeep().updateOn(PlayerUpdateEnum.PU_BONUS);
        getPlayerUpkeep().updateOn(PlayerUpdateEnum.PU_HP);
        getPlayerUpkeep().updateOn(PlayerUpdateEnum.PU_SPELLS);
        getPlayerUpkeep().getRedrawFlags().set(PlayerRedraw.PR_LEV, PlayerRedraw.PR_TITLE, PlayerRedraw.PR_EXP,
                PlayerRedraw.PR_STATS);

        PlayerCalcs.handleStuff(this);
    }

    /**
     * Returns the player's current experience total, the port of C's {@code p->exp}. This is the
     * drainable figure: it is what {@link #playerExpLose} reduces and what {@link #adjustLevel} clamps
     * to {@link PlayerRegistry#PY_MAX_EXP}, and it may sit below {@code maxExp} after a drain.
     *
     * <p>The fractional part held in {@code expFrac} is not included.</p>
     *
     * <p>Function getExp coded before 260831, commented in full on 260831.</p>
     *
     * @return the player's current experience points
     */
    public long getExp() {
        return exp;
    }
    
    /**
     * Returns the player's history ledger, the port of C's {@code p->hist}. This is the running log
     * of notable events - birth, levels gained, uniques slain, artifacts found or missed, and the
     * player's own notes - and not the block of background text rolled at birth, which is C's
     * {@code p->history} and a different thing entirely.
     *
     * <p>The ledger is built by the constructor and never replaced, so this never answers
     * {@code null}; C reaches the same state the long way round, {@code history_add_full} calling
     * {@code history_init} whenever it finds no array. The ledger itself is mutable, and
     * {@link PlayerHistory#addEntry} is the only thing that writes to it.</p>
     *
     * <p>Function getPlayerHistory coded before 260901, commented in full on 260901.</p>
     *
     * @return the player's history ledger, never {@code null}
     */
    public PlayerHistory getPlayerHistory() {
        return playerHistory;
    }

    /**
     * Returns the known counterparts of the player's carried gear, the port of C's
     * {@code p->gear_k}. Each entry is the known half of an object in {@link #getGear} - the picture
     * of it the player's rune knowledge entitles them to see - and the two lists are held in step,
     * so an object and its knowledge are found at the same position.
     *
     * <p>The list is built by the constructor and never replaced, so this never answers
     * {@code null}. It is the live list rather than a copy, and the gear operations in
     * {@link uk.co.jackoftradesltd.middle.objects.ObjectUtils} write to it through this accessor:
     * {@code gearInsertEnd} appends the object's known half beside it, and the absorbing half of
     * {@code combinePack} removes a merged object's known half before dropping the object. Adding
     * to it anywhere else would put the two lists out of step, which is what C's
     * {@code obj->known} pointer makes impossible and this pairing does not.</p>
     *
     * <p>Function getGearKnown coded before 260901, commented in full on 260901.</p>
     *
     * @return the known counterparts of the carried gear, never {@code null}
     */
    public ArrayList<ItemObject> getGearKnown() {
        return gearKnown;
    }

    /**
     * @return the player's maximum hit points
     */
    public int getMaxHP() {
        return maxHP;
    }

    /**
     * @return {@code true} if the player is currently resting — either the resting counter is still
     * running or a special stop-condition rest is in progress
     */
    public boolean isResting() {
        return (playerUpkeep.getRestingCounter() > 0 || PlayerUtils.restingIsSpecial(playerUpkeep.getRestingCounter()));
    }

    /**
     * @return the turns remaining until Word of Recall activates (0 = inactive)
     */
    public int getWordRecall() {
        return wordRecall;
    }

    /**
     * Ticks the Word of Recall countdown down by one turn.
     */
    public void decrementWordRecall() {
        wordRecall--;
    }

    /**
     * Sets the player's current dungeon depth.
     *
     * @param depth the new depth (0 = town)
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * @return the depth Word of Recall will return the player to
     */
    public int getRecallDepth() {
        return recallDepth;
    }

    /**
     * @return the turns remaining until a Deep Descent triggers (0 = inactive)
     */
    public int getDeepDescent() {
        return deepDescent;
    }

    /**
     * Ticks the Deep Descent countdown down by one turn.
     */
    public void decrementDeepDescent() {
        deepDescent--;
    }

    /**
     * @return the deepest dungeon level the player has reached
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * @return the player's carried gear (inventory and equipment)
     */
    public ArrayList<ItemObject> getGear() {
        return gear;
    }

    /**
     * @return this player's option settings, the port of C's {@code player->opts}
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
     * Adds to {@code flags} the object flags that the player's currently running timed effects
     * duplicate, so that a temporary status confers the same protection as the permanent flag it
     * imitates. Port of C's {@code player_flags_timed} ({@code player.c:310}).
     *
     * <p>Several timed effects are, for their duration, indistinguishable from carrying an object
     * with a particular flag: {@code TMD_OPP_CONF} is confusion protection, so it duplicates
     * {@code OF_PROT_CONF}; {@code TMD_BOLD} duplicates {@code OF_PROT_FEAR}, {@code TMD_SINVIS}
     * duplicates {@code OF_SEE_INVIS}. The pairing is data, not code — it is the
     * {@code flag-synonym} line in {@code player_timed.txt}, which loads into
     * {@link PlayerTimedEffect#getoFlagDup()}. Rather than have every consumer ask both "does the
     * gear grant this?" and "is the matching status running?", the statuses are folded into the
     * same flag set the gear contributes to and the consumer asks once. That is why the flags are
     * added to the caller's set rather than returned in a fresh one: {@code calc_bonuses}
     * ({@code player-calcs.c:2135}) passes {@code state->flags}, which already holds what the
     * equipment gave, and the character sheet ({@code ui-entry.c:886}) does the same with its
     * cache.
     *
     * <p>Only ever switches flags on, never off. A status cannot take away a flag the equipment
     * granted, and afterwards nothing can tell which of the two sources put a given flag there.
     *
     * <p>{@link TimedEffect#TMD_TRAPSAFE} is excluded even though it names a duplicate flag, and
     * that indistinguishability is exactly why. Being unable to tell the sources apart is normally
     * the point, but the trap code needs to: finding {@code OF_TRAP_IMMUNE} in the player's flags
     * has to mean a worn object granted it, because that is the cue to learn the trap-immunity
     * rune from the equipment ({@code trap.c:518}, {@code obj-chest.c:626}). Were the timed status
     * folded in, walking over a trap while merely under a potion of trap-safety would teach a rune
     * the player has no item for. C labels the exclusion a "Hack" in the comment above
     * {@code player_flags_timed} and implements it as a bare {@code i != TMD_TRAPSAFE}.
     *
     * <p>The dormant-effect test is what makes this correct rather than merely populated: the map
     * holds a zero for every effect from construction onwards, so without it every duplicable flag
     * in the game would be added at all times.
     *
     * <p>Two guards have no counterpart in C, which walks {@code 0} to {@code TMD_MAX} over a
     * static table that is always full. Iterating the map's keys covers the same population, since
     * the constructor seeds it with every {@link TimedEffect}; but a key may have no loaded
     * definition, so {@link PlayerRegistry#lookupPlayerTimedEffect} can answer null and that effect
     * is skipped. The extra {@link TimedEffect#TMD_NONE} constant, which C's enum does not have, is
     * excluded by its zero counter before that guard is reached. The map's iteration order is
     * unspecified where C's is the enum order, which is not observable here: the body only adds to
     * a set.
     *
     * <p>Function flagsTimed coded on 260818, commented in full on 260818.
     *
     * @param flags the flag set to add the duplicated flags to, mutated in place
     */
    @Contract(mutates = "param1")
    public void flagsTimed(@NotNull Flag<ObjectFlag> flags) {
        for (TimedEffect effect : timed.keySet()) {
            PlayerTimedEffect playerEffect = PlayerRegistry.lookupPlayerTimedEffect(effect);
            if (playerEffect == null) continue;

            if (timed.get(effect) != 0 && playerEffect.getoFlagDup() != ObjectFlag.OF_NONE
                    && effect != TimedEffect.TMD_TRAPSAFE) {
                flags.on(playerEffect.getoFlagDup());
            }
        }
    }

    /**
     * Fills {@code flags} with the object flags the player has innately, from race, from class, and
     * from the one class ability that grants a flag on reaching a level. Port of C's
     * {@code player_flags} ({@code player.c:290}).
     *
     * <p>These are the flags a naked player still has. The equipment's contribution is gathered
     * separately by {@code calc_bonuses}, and the flags duplicated by running statuses by
     * {@link #flagsTimed}; this is the third source, and the only one that does not depend on
     * anything the player is carrying or currently under.
     *
     * <p><b>Replaces the caller's set rather than adding to it</b>, which is the opposite of its
     * neighbour {@link #flagsTimed} and easy to misread from the shared shape of the two
     * signatures. C's first statement is a {@code memcpy} over the whole set, not an
     * {@code of_union}, so anything already in the caller's set is discarded;
     * {@link Flag#copyFrom} wipes before unioning and so agrees. Both C call sites pass a set that
     * is empty at that moment ({@code player-calcs.c:1922}, {@code player-timed.c:752}), so the
     * difference is invisible there — but it is what the method promises, and a caller that
     * gathered equipment flags first would lose them.
     *
     * <p>Race is copied and class is unioned in, so a flag from either source is enough and a flag
     * on both is held once. Which of the two is copied and which unioned has no effect on the
     * result, only on there being no need to wipe first.
     *
     * <p>{@link PlayerFlag#PF_BRAVERY_30} is the only conditional, and it is the reason this method
     * needs a {@link PlayerState} at all. The flag is data — in the shipped
     * {@code class.txt} only the Warrior carries it ({@code class.txt:158}), described as "You
     * become immune to fear at level 30" — and the level threshold is the hard-coded half of the
     * pair. Note that the flag is looked for on the calculated state, C's
     * {@code player_has} being {@code pf_has(p->state.pflags, flag)} ({@code player.h:440}), and
     * not on the class's own list: a shape or another later contribution to {@code pflags} counts
     * too. That in turn means the state must already have its player flags filled in when this is
     * called, which in C holds because {@code calc_bonuses} fills them three lines earlier
     * ({@code player-calcs.c:1917}) than it calls this ({@code player-calcs.c:1922}).
     *
     * <p><b>Deliberate divergence from the C original.</b> C takes only the player and reads
     * {@code p->state} directly; this takes the state to consult as a parameter. C can afford the
     * shortcut because a {@code struct player_state} is a value that callers copy about freely,
     * whereas the port passes references — {@code calcBonuses} builds a state that is not
     * necessarily the player's own, so reaching for the field would consult one state while filling
     * another. The divergence is also a small behaviour change, and in the port's favour: at the
     * call sites that ask a hypothetical question, such as the "pretend we are wielding this"
     * calculation behind an object's blow counts ({@code obj-info.c:888}), C answers the bravery
     * test from the real player while filling a scratch state, and the port answers it from the
     * state being filled.
     *
     * <p>Function flags coded on 260818, commented in full on 260818.
     *
     * @param state the calculated state to read {@link PlayerFlag#PF_BRAVERY_30} from, not written to
     * @param flags the flag set to fill, wiped first and mutated in place
     */
    @Contract(mutates = "param2")
    public void playerFlags(@NotNull PlayerState state, @NotNull Flag<ObjectFlag> flags) {
        flags.copyFrom(race.getoFlags());
        flags.union(playerClass.getoFlags());
        if (state.hasPFlag(PlayerFlag.PF_BRAVERY_30) && level >= 30) {
            flags.on(ObjectFlag.OF_PROT_FEAR);
        }
    }
    
    /**
     * Reads one entry of the rolled hit-point table - the port of C's {@code p->player_hp[]}.
     *
     * <p>The table is the character's whole hit-point history, rolled once at birth
     * ({@code player-birth.c:296}) and never re-rolled, so that a character's maximum is a property
     * of who they are rather than of when the calculation last ran. Entries are cumulative totals,
     * not per-level gains, and the array is indexed from zero: C reads the current level's figure as
     * {@code p->player_hp[p->lev - 1]} ({@code player-calcs.c:1577}), and the port passes that
     * subtraction in at the call site rather than hiding it here.
     *
     * <p>The table is null on a freshly constructed player, and the port's birth code does not roll
     * it yet, so this throws for every character the port can currently make; the tests that need it
     * install an array by reflection.
     *
     * <p>Function getPlayerHP commented in full on 260901.
     *
     * @param level the zero-based index into the table, one below the character level being asked
     *              about
     * @return the cumulative hit points rolled by that level
     */
    public int getPlayerHP(int level) {
        return playerHP[level];
    }

    /**
     * Sets the player's maximum hit points - the port of writing C's {@code p->mhp}.
     *
     * <p>Named for the calculation rather than the field, to keep it clear of {@link #getPlayerHP},
     * which reads the rolled table and not this derived ceiling.
     *
     * <p>Function setPlayerMaxHP commented in full on 260901.
     *
     * @param maxHP the new maximum hit points
     */
    public void setPlayerMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    /**
     * Sets the player's current hit points - the port of writing C's {@code p->chp}.
     *
     * <p>Unclamped in both directions, exactly as C leaves it: death is decided by the damage code
     * rather than by this write, and {@code calc_hitpoints} ({@code player-calcs.c:1588}) is what
     * pulls the value down when the maximum falls.
     *
     * <p>Function setCurrentHP commented in full on 260901.
     *
     * @param currentHP the new current hit points
     */
    public void setCurrentHP(int currentHP) {
        this.currentHP = currentHP;
    }

    /**
     * Sets the fractional part of the current hit points - the port of writing C's
     * {@code p->chp_frac}.
     *
     * <p>The sixteen-bit remainder that lets regeneration of less than a whole hit point accumulate;
     * cleared whenever the current total is forced down to a new maximum.
     *
     * <p>Function setChpFrac commented in full on 260901.
     *
     * @param chpFrac the new fractional hit points, scaled by 2^16
     */
    public void setChpFrac(int chpFrac) {
        this.chpFrac = chpFrac;
    }

    /**
     * Installs the player's fully calculated state - the port of writing C's {@code p->state}.
     *
     * <p>C recalculates in place, into the live {@code p->state}; the port calculates into a copy and
     * swaps it in here at the end of {@code PlayerCalcs.updateBonuses}, because the comparisons that
     * decide what to redraw need the old state to still be readable while the new one is being
     * built. So the value handed in is taken by identity and becomes the state from that moment,
     * with no copy made.
     *
     * <p>Function setState commented in full on 260901.
     *
     * @param state the newly calculated state, kept by reference
     */
    public void setState(PlayerState state) {
        this.state = state;
    }

    /**
     * The state as the player is entitled to see it - the port of C's {@code p->known_state}.
     *
     * <p>Calculated by the same pass as {@link #getPlayerState}, but with the unknown runes of the
     * carried gear left out, so it is what the character sheet may print rather than what the game
     * actually resolves attacks with. Null until the first bonus calculation, which is what creates
     * both.
     *
     * <p>Function getKnownState commented in full on 260901.
     *
     * @return the known state, or {@code null} before the first bonus calculation
     */
    public PlayerState getKnownState() {
        return knownState;
    }

    /**
     * Installs the player's known state - the port of writing C's {@code p->known_state}.
     *
     * <p>Taken by identity and swapped in alongside {@link #setState}, for the same reason: the
     * armour-class comparison that decides whether to redraw reads the outgoing known state, so the
     * incoming one cannot be written until that has been asked.
     *
     * <p>Function setKnownState commented in full on 260901.
     *
     * @param knownState the newly calculated known state, kept by reference
     */
    public void setKnownState(PlayerState knownState) {
        this.knownState = knownState;
    }

    /**
     * Returns the player's current character level - the port of reading C's {@code p->lev}.
     *
     * <p>Distinct from , C's {@code p->max_lev}: experience drain can lower
     * the current level, but never the highest one attained.
     *
     * <p>Function getLevel commented in full on 260828.
     *
     * @return the player's current character level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Whether the player has temporarily switched ignoring off - the port of reading C's
     * {@code p->unignoring}.
     *
     * <p>Ignoring normally hides things: an item whose kind or quality the player has told the game
     * to ignore is dropped out of lists and off the floor display. This flag is the override that
     * suspends that, so everything shows again. It changes nothing about <em>what</em> is ignored -
     * the per-kind and per-quality settings are untouched - only whether the hiding is currently
     * being applied. C's {@code ignore_item_ok} and {@code ignore_known_item_ok} both test it before
     * anything else and answer {@code false} while it is set; the port does the same in
     * {@code middle.objects.ObjectIgnore.ignoreItemOK}.
     *
     * <p>Held as an {@code int} because C holds a {@code uint8_t}, and read as a number rather than a
     * boolean for the same reason: the value C writes is {@code !p->unignoring}, so it is only ever
     * zero or one, and callers ask {@code != 0}. The {@code is} prefix promises a boolean the return
     * type does not give; the name follows the field.
     *
     * <p>Function isUnignoring commented in full on 260901.
     *
     * @return non-zero while ignored items are being shown
     */
    public int isUnignoring() {
        return unignoring;
    }

    /**
     * This character's quest history - the port of reading C's {@code p->quests}.
     *
     * <p>Not the same list as the one in
     * {@link uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry#getQuests}. That one is
     * the set of standard quests loaded from {@code quest.txt}, shared by the whole game and never
     * altered; this one is a per-character copy taken from it at birth, by C's
     * {@code player_quests_reset}, and it is the copy that records progress. Completing a quest sets
     * that character's entry level to zero ({@code player-quest.c:233}), which is what makes
     * {@link PlayerQuest#isQuest} a test for the quests still outstanding rather than for every
     * quest in the game.
     *
     * <p>C holds a fixed array of {@code z_info->quest_max} entries and indexes it directly. The port
     * holds a list and hands it back wrapped, so a caller can walk the quests but cannot add or
     * remove one; the {@link Quest} objects inside are the live ones, not copies.
     *
     * <p>Function getQuests commented in full on 260901.
     *
     * @return the character's quests, unmodifiable
     */
    public List<Quest> getQuests() {
        return Collections.unmodifiableList(quests);
    }

    /**
     * Raises one stat by a single point of gain, the way a potion of strength or a stat-gain effect
     * does. Ports {@code player_stat_inc} ({@code player.c:145}).
     *
     * <p>Stats in Angband are stored on a stretched scale: 3 to 18 are the ordinary values, and
     * everything above 18 is a "percentile" tail written as 18/01 to 18/100 but held internally as
     * 19 to 118. So {@code 18 + 100} is the hard ceiling and {@code 18 + 90} is the point where the
     * tail stops being earned and starts being handed over. The method reads the current value and
     * picks one of three bands.
     *
     * <p>At or above the ceiling there is nothing to give: the method answers {@code false} without
     * touching the stat and without flagging a recalculation. Below 18 the gain is a flat one point,
     * the plain part of the scale. In between, the gain is rolled, and the roll shrinks as the stat
     * climbs - {@code gain} is a quarter of the remaining distance to the ceiling, plus a little,
     * and the player receives {@code randint1(gain) + gain / 2}. The floor of one keeps the roll
     * meaningful when the distance has been divided away. Above {@code 18 + 90} the roll is skipped
     * entirely and the stat is set straight to the ceiling, so the last ten points are a gift rather
     * than a grind.
     *
     * <p>The clamp to {@code 18 + 99} after the roll is defensive rather than load-bearing. With
     * 4.2.6's constants the rolled band cannot reach it: the largest result anywhere in the band is
     * 113, at a current value of 107. It is ported because it is a branch in the C, and because it
     * is what stops the middle band from ever landing on the ceiling if those constants move.
     *
     * <p>Two side effects follow every successful gain. The maximum value is dragged up if the
     * current has passed it, which is what makes gained points survive later drain; and
     * {@code PU_BONUS} is raised so the next update recomputes everything derived from the stat.
     *
     * <p>The return value says only that a band was entered, not that the number moved. A stat at
     * 117 goes to 118 and answers {@code true}; one already at 118 answers {@code false}. Callers
     * such as {@code effect-handler-general.c:879} use it exactly that way, to decide whether to
     * print the gain message.
     *
     * <p>Where C subscripts a zeroed array, the port reads a map, so a stat never written at birth
     * would fail here rather than reading as zero. Every stat is populated during birth, so this is
     * a difference in failure mode, not in behaviour.
     *
     * <p>Function playerStatInc coded on 260831, commented in full on 260831.
     *
     * @param stat the stat to raise
     * @return {@code true} if a gain was applied, {@code false} if the stat was already at the
     * ceiling of {@code 18 + 100}
     */
    public boolean playerStatInc(Stats stat) {
        int amount = statCur.get(stat);

        if (amount >= 18 + 100)
            return false;
        if (amount < 18) {
            statCur.put(stat, amount + 1);
        } else if (amount < 18 + 90) {
            int gain = (((18 + 100) - amount) / 2 + 3) / 2;
            if (gain < 1) gain = 1;
            statCur.put(stat, amount + RandomValueUtils.randInt1(gain) + gain / 2);
            if (statCur.get(stat) > 18 + 99)
                statCur.put(stat, 18 + 99);
        } else {
            statCur.put(stat, 18 + 100);
        }
        if (statCur.get(stat) > statMax.get(stat)) {
            statMax.put(stat, statCur.get(stat));
        }

        getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_BONUS);
        return true;
    }

    /**
     * Awards experience to the player and re-evaluates their level, the port of C's
     * {@code player_exp_gain} ({@code player.c:269}). Everything that pays experience - killing a
     * monster, disarming a trap, opening a chest, learning a rune, casting a spell for the first
     * time - arrives here.
     *
     * <p>The current total takes the whole award. The maximum takes a tenth of it, and only while
     * the character is behind: the test is made <em>after</em> the award has landed, so a character
     * whose current total has caught up with their maximum adds nothing to the maximum here and
     * lets {@link #adjustLevel(boolean)} drag it up instead. That tenth is what makes drained
     * experience cost something permanent - earning it back a second time also lifts the ceiling
     * slightly, but the character is still climbing ground they had already covered.
     *
     * <p>The tenth is C's integer division, so any award below ten adds nothing at all to the
     * maximum while the character is behind. Java's {@code long} division truncates towards zero
     * exactly as C's does, so a negative award - which {@code cmd-wizard.c:1208} can produce - is
     * split the same way in both.
     *
     * <p>No clamping happens here. Both totals can be driven past
     * {@link PlayerRegistry#PY_MAX_EXP}, or below zero, and {@link #adjustLevel(boolean)} is what
     * settles them before the level loops read them. The call is always verbose, so a level gained
     * from an award is announced and written to the character's history.
     *
     * <p>C holds both totals in a signed 32-bit field and the port holds them in {@code long}, so
     * the intermediate overflow C would suffer on an award close to {@code PY_MAX_EXP} cannot
     * happen here. With 4.2.6's constants C does not overflow either - twice {@code PY_MAX_EXP} is
     * still inside 32 bits - so this is headroom, not a behavioural difference.
     *
     * <p>Function playerExpGain coded on 260831, commented in full on 260831.
     *
     * @param amount the experience to award
     */
    public void playerExpGain(long amount) {
        exp += amount;
        if (exp < maxExp)
            maxExp += amount / 10;
        adjustLevel(true);
    }

    /**
     * Drains experience from the player and re-evaluates their level, the port of C's
     * {@code player_exp_lose} ({@code player.c:278}). Everything that takes experience away arrives
     * here: the nether and dark breaths and the exp-draining monster blows
     * ({@code project-player.c}, {@code mon-blows.c:623}), the black-breath upkeep in
     * {@code game-world.c:659}, and the permanent drain in {@code effect-handler-general.c:3533}.
     *
     * <p>The loss is capped at what the character actually has before anything is subtracted, so
     * the current total can reach zero but never pass it. The cap matters twice over: because
     * {@code amount} is overwritten by the cap, a permanent drain reduces the maximum by what was
     * really taken rather than by what was asked for. A character with 400 points asked for 9999
     * loses 400 from both, not 9999 from the maximum.
     *
     * <p>Only the current total is capped, though. The maximum has no floor of its own here, and a
     * permanent drain can drive it below zero when it was already the lower of the two;
     * {@link #adjustLevel(boolean)} is what floors it at zero afterwards, along with settling both
     * levels.
     *
     * <p>The call is always verbose, but that changes nothing a player sees on the way down: C's
     * downward walk in {@code adjust_level} is silent whatever {@code verbose} says, and only a
     * level <em>gain</em> is announced or written to the character's history. A drain that costs
     * levels leaves no record. The highest level reached is not taken back either, because
     * {@code maxLevel} is driven by {@code maxExp} alone - so a temporary drain lowers the working
     * level and leaves the character's best level standing.
     *
     * <p>C takes the amount as {@code int32_t} and the port takes it as {@code long}, matching the
     * width the two experience totals are held at.
     *
     * <p>Function playerExpLose coded on 260831, commented in full on 260831.
     *
     * @param amount    the experience to remove, capped at what the character has
     * @param permanent whether the loss also reduces the maximum, putting it beyond earning back
     */
    public void playerExpLose(long amount, boolean permanent) {
        if (exp < amount) amount = exp;
        exp -= amount;
        if (permanent) maxExp -= amount;
        adjustLevel(true);
    }

    /**
     * The character's working height in inches - the read half of C's {@code p->ht}.
     *
     * <p>C reads the field directly and only from the character sheet, which splits it into feet
     * and inches ({@code ui-player.c:829}) - nothing in play consults a character's height. The
     * save writer is the other reader ({@code save.c:437}).
     *
     * <p>See {@link #setHeight(int)} for the units, the birth roll and the range.
     *
     * <p>Function getHeight commented in full on 260902.
     *
     * @return the height in inches
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the character's working height in inches - the port of C's {@code p->ht}.
     *
     * <p>The unit is inches, not any composite: the character sheet is the only place the value is
     * shown, and it splits it there with {@code player->ht / 12} and {@code player->ht % 12}
     * ({@code ui-player.c:829}).
     *
     * <p>Height is rolled once, at birth, from the race's height distribution, and the same roll is
     * stored to both this field and the birth copy in one statement -
     * {@code p->ht = p->ht_birth = Rand_normal(p->race->base_hgt, p->race->mod_hgt)}
     * ({@code player-birth.c:359}). Nothing in play changes it afterwards; the only other writers
     * are the save-file loader ({@code load.c:719}) and quickstart, which restores the saved birth
     * value ({@code player-birth.c:198}).
     *
     * <p>C holds the height as {@code int16_t} and the port holds it as {@code int}. The wider type
     * costs nothing: {@code Rand_normal} caps its offset at four standard deviations, so even the
     * tallest race, the Half-Troll at {@code 90} base and {@code 16} spread, is confined to
     * {@code 26 .. 154} inches.
     *
     * <p>Function setHeight commented in full on 260902.
     *
     * @param height the height in inches
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the saved birth height, the quickstart copy - the port of C's {@code p->ht_birth}.
     *
     * <p>Birth writes it from the same {@code Rand_normal} roll that sets the working height
     * ({@code player-birth.c:359}), and it is left alone from then on, so it keeps the height the
     * character was born with however the working value is later reloaded.
     *
     * <p>It is read back in two places. Quickstart restores it -
     * {@code player->ht = player->ht_birth = saved->ht} ({@code player-birth.c:198}) - and its
     * being non-zero is what C uses to decide a previous character exists to quickstart from
     * ({@code player-birth.c:1061}), which is why a zero here is meaningful rather than merely
     * unset.
     *
     * <p>C holds it as {@code int16_t}; see {@link #setHeight(int)} for why {@code int} is
     * interchangeable here.
     *
     * <p>Function setHeightBirth commented in full on 260902.
     *
     * @param height the birth height in inches
     */
    public void setHeightBirth(int height) {
        this.htBirth = height;
    }

    /**
     * The character's working weight in pounds - the read half of C's {@code p->wt}.
     *
     * <p>Unlike the height, the weight is read in play: it is one of the four terms of a shield
     * bash's quality, {@code p->wt / 8} ({@code player-attack.c:929}), so a heavier character bashes
     * harder. The character sheet ({@code ui-player.c:830}) and the save writer
     * ({@code save.c:438}) are the other readers.
     *
     * <p>See {@link #setWeight(int)} for the units, the birth roll and the range.
     *
     * <p>Function getWeight commented in full on 260902.
     *
     * @return the weight in pounds
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the character's working weight in pounds - the port of C's {@code p->wt}.
     *
     * <p>The unit is pounds. The character sheet is where that shows: it prints stones and pounds
     * with {@code player->wt / 14} and {@code player->wt % 14} ({@code ui-player.c:830}), a stone
     * being fourteen pounds.
     *
     * <p>Weight is rolled once, at birth, from the race's weight distribution, and the same roll is
     * stored to both this field and the birth copy in one statement -
     * {@code p->wt = p->wt_birth = Rand_normal(p->race->base_wgt, p->race->mod_wgt)}
     * ({@code player-birth.c:360}). Nothing in play changes it: gear carried is tracked separately
     * as {@code upkeep->total_weight}, and the only other writers here are the save-file loader
     * ({@code load.c:720}) and quickstart, which restores the saved birth value
     * ({@code player-birth.c:197}).
     *
     * <p>The value is read in play, which the height is not - a shield bash's quality takes
     * {@code p->wt / 8} alongside the melee skill, the carried weight and the shield's own weight
     * ({@code player-attack.c:929}).
     *
     * <p>C holds the weight as {@code int16_t} and the port holds it as {@code int}. The wider type
     * costs nothing: {@code Rand_normal} caps its offset at four standard deviations, so even the
     * heaviest race, the Half-Troll at {@code 240} base and {@code 60} spread, is confined to
     * {@code 0 .. 480} pounds.
     *
     * <p>Function setWeight commented in full on 260902.
     *
     * @param weight the weight in pounds
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Sets the saved birth weight, the quickstart copy - the port of C's {@code p->wt_birth}.
     *
     * <p>Birth writes it from the same {@code Rand_normal} roll that sets the working weight
     * ({@code player-birth.c:360}), and nothing changes it afterwards, so it keeps the weight the
     * character was born with however the working value is later reloaded - which for the weight
     * matters more than it does for the height, since play has no way to change {@code p->wt}
     * either.
     *
     * <p>Quickstart is the reader: it copies this field out to the saved character
     * ({@code player-birth.c:154}) and restores both weights from it on the way back in -
     * {@code player->wt = player->wt_birth = saved->wt} ({@code player-birth.c:197}). The save
     * file carries it separately from the working weight ({@code save.c:449},
     * {@code load.c:735}).
     *
     * <p>C holds it as {@code int16_t}; see {@link #setWeight(int)} for why {@code int} is
     * interchangeable here.
     *
     * <p>Function setWeightBirth commented in full on 260902.
     *
     * @param weight the birth weight in pounds
     */
    public void setWeightBirth(int weight) {
        this.wtBirth = weight;
    }
}