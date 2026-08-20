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
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.channel.utils.FlagView;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.enums.DamageAspect;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.GameWorld;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.globals.Food;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.StatTables;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.enums.*;

import java.util.*;

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
     * Transfers what the player knows about object properties in general onto one particular object,
     * the port of C's {@code player_know_object} ({@code obj-knowledge.c:1018}).
     *
     * <p><b>The direction of travel is the thing to hold on to.</b> This does not look at the object
     * and work out what the player has learned; it looks at {@link #itemKnowledge} — the player's
     * standing knowledge of what each rune, modifier and element <em>means</em> — and rewrites the
     * object's known counterpart to show only the properties that knowledge entitles the player to
     * read. Learning happens elsewhere, in the {@code learnRune} family; this is the propagation step
     * that runs afterwards, over every object in play, so that a rune learned on one sword shows up
     * on every other object carrying it.
     *
     * <p>That is why nearly every assignment here is a multiplication or a gate rather than a copy.
     * {@link KnownObject}'s numeric fields are one-or-zero knowledge bits, so
     * {@code item.getDamageDice() * itemKnowledge.getDd()} yields the real dice when the player can
     * read dice and zero when they cannot — C's idiom, kept rather than rewritten as a conditional
     * because the zero is meaningful: it is what the display shows for an unknown quantity.
     *
     * <p><b>Three early returns, and they are not degrees of the same thing.</b> A null item or a
     * null counterpart is nothing to do. A kind mismatch between the object and its counterpart means
     * the player has the wrong idea about what the object even is — only sensed, not assessed — and
     * imposing property knowledge on that would be asserting detail about the wrong item. A distant
     * object that has not been {@code OBJ_NOTICE_ASSESSED} gets {@link #setBaseKnown} and no more:
     * the player can see a sword on the floor across the room and know it is a sword, without being
     * close enough to have formed a view about its enchantment.
     *
     * <p>The fourth return, after the flags, is the odd one. A curse holds its own bearer-less
     * {@link ItemObject} to carry the properties it confers, and that object has a null kind. It has
     * flags and modifiers worth knowing, but no ego, no flavour, no effect and nothing to become
     * aware of, so it stops there while real objects carry on.
     *
     * <p><b>Correctness is not yet established.</b> The audit of 260816 found divergences from C in
     * the combat-detail, modifier, element, flag, brand, curse and fully-known blocks; several of
     * them need accessors that do not exist yet. See
     * {@code docs/implementation/260816_functions_implemented.md} for the block-by-block comparison.
     * The blocks recorded there as matching C are the slays, the ego/jewellery/special-artifact
     * branch, the effect, and the guards and early returns described above.
     *
     * <p>Function knowObject coded before 260815 as a stub, implemented on 260816, commented in full
     * on 260816.
     *
     * @param item the object whose known counterpart should be brought up to date; may be
     *             {@code null}, matching C's {@code if (!obj) return}
     */
    public void knowObject(ItemObject item) {
        boolean seen = true;

        // unseen or only sensed items don't get any id
        if (item == null) return;
        if (item.getKnown() == null) return;
        ObjectKind itemKind = item.getKind();
        if (itemKind != item.getKnown().getKind()) return;

        ItemObject known = item.getKnown();

        // Distant objects
        if (itemKind != null && !(known.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED))) {
            setBaseKnown(item);
            return;
        }

        // Dice and pval for !chests
        known.setDamageDice(item.getDamageDice() * itemKnowledge.getDd());
        known.setDamageSides(item.getDamageSides() * itemKnowledge.getDs());
        known.setBaseAC(item.getBaseAC() * itemKnowledge.getAc());
        if (!item.gettValue().isChest())
            known.setpValue(item.getpValue());

        // combat details
        known.setToAC(item.getToAC() * itemKnowledge.getToA());
        if (!item.hasStandardToH())
            known.setToHit(item.getToHit() * itemKnowledge.getToH());
        known.setToDam(item.getToDam() * itemKnowledge.getToD());

        // modifiers
        Map<ObjectModifier, Integer> modifiers = item.getModifiers();
        Map<ObjectModifier, Integer> newModifiers = new HashMap<>();
        for (ObjectModifier modifier : ObjectModifier.values()) {
            newModifiers.put(modifier, 0);
        }
        for (ObjectModifier key : modifiers.keySet()) {
            if (itemKnowledge.modifierIsKnown(key))
                newModifiers.put(key, modifiers.get(key));
        }
        known.setModifiers(newModifiers);

        // Elements
        Map<ElementEnum, Boolean> knownElements = itemKnowledge.getElementResistInfo();
        Map<ElementEnum, ElementInfo> itemElInfo = item.getElInfo();
        Map<ElementEnum, ElementInfo> newElInfo = new HashMap<>(known.getElInfo());
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;

            ElementInfo zero = new ElementInfo();
            zero.setResLevel(0);
            newElInfo.put(element, zero);
        }
        for (ElementEnum key : knownElements.keySet()) {
            if (knownElements.get(key))
                newElInfo.put(key, itemElInfo.get(key).copy());
        }
        known.setElInfo(newElInfo);

        // ObjectFlags
        Flag<ObjectFlag> knownFlags = itemKnowledge.getFlags();
        FlagView<ObjectFlag> itemFlags = item.getFlags();
        knownFlags.inter(itemFlags);
        known.setFlagsTo(knownFlags);

        // Curse object structures are finished now
        if (itemKind == null)
            return;

        // Brands
        Set<Brand> brands = item.getBrands();
        if (brands == null) brands = new HashSet<>();
        Set<Brand> knownBrands = known.getBrands();
        if (knownBrands == null) knownBrands = new HashSet<>();
        Set<Brand> union = new HashSet<>(brands);
        union.addAll(knownBrands);

        boolean knownBrand = false;
        for (Brand brand : union) {
            if (knowsBrand(brand)) {
                known.addBrand(brand);
                knownBrand = true;
            } else {
                known.removeBrand(brand);
            }
        }

        if (!knownBrand && !known.getBrands().isEmpty()) {
            known.clearBrands();
        }


        // Slays
        Set<Slay> itemSlays = item.getSlays();
        if (itemSlays == null) itemSlays = new HashSet<>();
        Set<Slay> knownSlays = known.getSlays();
        if (knownSlays == null) knownSlays = new HashSet<>();
        Set<Slay> unionSlays = new HashSet<>(itemSlays);
        unionSlays.addAll(knownSlays);

        boolean knowSlay = false;

        for (Slay slay : unionSlays) {
            if (knowsSlay(slay)) {
                known.addSlay(slay);
                knowSlay = true;
            } else {
                known.removeSlay(slay);
            }
        }

        if (!knowSlay && !known.getSlays().isEmpty()) {
            known.clearSlays();
        }

        // Curses - be careful re alignment of knowledge
        Map<Curse, CurseData> itemCurses = item.getCurses();
        if (!itemCurses.isEmpty()) {
            boolean knownCursed = false;

            for (Curse curse : itemCurses.keySet()) {
                if (itemKnowledge.curseIsKnown(curse) && itemCurses.get(curse).getPower() != 0) {
                    knownCursed = true;
                    CurseData oldData = itemCurses.get(curse);
                    CurseData data = new CurseData(oldData.getPower(), 0);
                    known.addCurse(curse, data);
                } else if (known.getCurses().containsKey(curse)) {
                    known.removeCurse(curse);
                }
            }

            if (!knownCursed) {
                known.clearCurses();
            }
        } else if (!known.getCurses().isEmpty()) {
            known.clearCurses();
        }

        // ego type & jewellery type
        if (knowsEgo(item)) {
            seen = item.getEgo().isEverSeen();
            known.setEgo(item.getEgo());
        } else {
            known.setEgo(null);
        }

        if (item.gettValue().isJewelry()) {
            if (nonCurseRunesKnown(item)) {
                seen = (item.isArtifact() || itemKind.isEverseen());
                flavourAware(item);
            }
        } else if (itemKind.isSpecialArtifactKind()) {
            seen = true;
            flavourAware(item);
        }

        // Effect is known
        if ((itemKind.isAware() && itemKind.getFlavour() != null) ||
                (!item.gettValue().isWearable() && itemKind.getFlavour() == null) ||
                (item.gettValue().isWearable() && itemKind.getEffect() != null && itemKind.isAware())) {
            known.setEffect(item.getEffect());
        }

        // New stuff
        if (!seen) {
            String objectName;
            Flag<ObjectDescription> descriptionFlag = new Flag<>(ObjectDescription.class);

            if (isCarried(item)) {
                descriptionFlag.set(ObjectDescription.ODESC_PREFIX,
                        ObjectDescription.ODESC_COMBAT, ObjectDescription.ODESC_EXTRA);
                objectName = item.description(descriptionFlag, this);
                String msg = String.format("You have %s (%c)", objectName, gearToLabel(item));
                Message.message(msg);
            } else if (cave != null && cave.getSquare(grid).holdsObject(item)) {
                descriptionFlag.set(ObjectDescription.ODESC_PREFIX,
                        ObjectDescription.ODESC_COMBAT, ObjectDescription.ODESC_EXTRA);
                objectName = item.description(descriptionFlag, this);
                String msg = String.format("On the ground: %s.", objectName);
                Message.message(msg);
            }
        }

        // Fully known objects
        if (item.isFullyKnown()) {
            for (ElementEnum element : item.getElInfo().keySet()) {
                if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;

                ElementInfo eInfo = itemElInfo.get(element).copy();
                known.putElInfo(element, eInfo);
            }

            Flag<ObjectFlag> copy = new Flag<>(ObjectFlag.class);
            copy.copyFrom(item.getFlags());
            known.setFlagsTo(copy);
        }
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
     * Reports whether every rune on an item except its curses has been learned, the port of C's
     * {@code object_non_curse_runes_known} ({@code obj-knowledge.c}).
     *
     * <p>Answered by comparing the item against its own known counterpart: a property the player can
     * read has been copied across, so anything the item has and the counterpart lacks is something
     * still unlearned. The combat bonuses must match exactly, while the modifiers, elements, brands,
     * slays and flags are one-way containments — the counterpart must cover the item, and is allowed
     * to carry more.
     *
     * <p>Curses are excluded because they are compared differently, by power rather than by
     * presence, and are handled by {@code ItemObject.cursesAreEqual} instead. {@code runesKnown}
     * calls both in turn, which is how C's {@code object_runes_known} is built.
     *
     * <p>Lives on the player rather than on the item, despite reading only the item, because it is
     * the counterpart to the rest of the knowledge code here and there was previously a second copy
     * of it on {@link ItemObject} that drifted from this one. {@code ItemObject.runesKnown}
     * delegates here so there is one implementation to keep right.
     *
     * <p>Iterates each map's own keys rather than the full enum, since an item records only the
     * modifiers and elements it actually carries; C can loop over fixed bounds because its arrays
     * have a slot for every one.
     *
     * <p>Function nonCurseRunesKnown coded before 260817, made public on 260817 when
     * {@code ItemObject}'s duplicate was folded into it, commented in full on 260817.
     *
     * @param item the item to test
     * @return {@code true} if every non-curse rune on the item has been learned
     */
    public static boolean nonCurseRunesKnown(ItemObject item) {
        if (item == null || item.getKnown() == null)
            return false;

        ItemObject knownItem = item.getKnown();

        // Combat details known
        if (knownItem.getToAC() != item.getToAC()) return false;
        if (knownItem.getToDam() != item.getToDam()) return false;
        if (knownItem.getToHit() != item.getToHit()) return false;

        // Modifiers
        Map<ObjectModifier, Integer> knownModifiers = knownItem.getModifiers();
        Map<ObjectModifier, Integer> itemModifiers = item.getModifiers();

        for (ObjectModifier key : itemModifiers.keySet()) {
            if (key == ObjectModifier.OM_MAX || key == ObjectModifier.OM_NONE) continue;
            if (!knownModifiers.containsKey(key)) return false;
            if (!Objects.equals(knownModifiers.get(key), itemModifiers.get(key))) return false;
        }

        // elements
        Map<ElementEnum, ElementInfo> knownEInfo = knownItem.getElInfo();
        Map<ElementEnum, ElementInfo> itemEInfo = item.getElInfo();

        for (ElementEnum key : itemEInfo.keySet()) {
            if (!knownEInfo.containsKey(key)) return false;
            if (itemEInfo.get(key).getResLevel() != 0 && knownEInfo.get(key).getResLevel() == 0) return false;
        }

        // Brands
        Set<Brand> itemBrands = item.getBrands();
        Set<Brand> knownBrands = knownItem.getBrands();

        if (!knownBrands.containsAll(itemBrands)) return false;

        // Slays
        Set<Slay> itemSlays = item.getSlays();
        Set<Slay> knownSlays = knownItem.getSlays();
        if (knownSlays == null) return false;
        if (!knownSlays.containsAll(itemSlays)) return false;

        // Flags
        Flag<ObjectFlag> knownFlags = knownItem.getFlags();
        Flag<ObjectFlag> itemFlags = item.getFlags();

        return knownFlags.isSubset(itemFlags);
    }

    /**
     * Copies onto an item's known counterpart everything that follows from simply recognising what
     * the item is, the port of C's {@code object_set_base_known} ({@code obj-knowledge.c}).
     *
     * <p>The division this draws is between what an item <em>is</em> and what has been done to it.
     * Knowing a weapon is a Long Sword settles its kind, its weight and its damage dice, because
     * every Long Sword shares them; it settles nothing about the enchantment on this particular one,
     * which still has to be learned rune by rune. So the kind-level facts are copied here and the
     * per-object ones are left to {@link #knowObject}.
     *
     * <p>The dice, armour class and to-hit are copied only where the counterpart still holds
     * nothing, so that a figure already learned is never overwritten by the kind's generic one. Each
     * is multiplied by the corresponding 0/1 flag on {@link KnownObject}, which is how C masks a
     * property the player cannot yet read: an unknown armour class multiplies to zero rather than
     * being copied.
     *
     * <p>The effect is copied in two cases, and both are about whether using the item would have
     * taught it. A flavoured kind the player is aware of has been used before; an unflavoured
     * non-wearable — a scroll, a potion — announces what it does when read or drunk. A wearable's
     * activation follows the same rule through the kind's awareness.
     *
     * <p>Throws rather than returning quietly when there is no counterpart to write to, because a
     * carried object without one is a broken invariant rather than a case to handle: C asserts on
     * the same condition.
     *
     * <p>Function setBaseKnown coded before 260817, commented in full on 260817.
     *
     * @param item the item whose known counterpart is being brought up to date
     * @throws RuntimeException if the item or its known counterpart is missing
     */
    private void setBaseKnown(ItemObject item) {
        if (item == null || item.getKnown() == null) {
            logger.error("Item or item known nonexistent in Player.setBaseKnown");
            throw new RuntimeException("Item or item known nonexistent in Player.setBaseKnown");
        }

        ItemObject known = item.getKnown();
        known.setKind(item.getKind());
        known.settValue(item.gettValue());
        known.setsValue(item.getsValue());
        known.setWeight(item.getWeight());
        known.setNumber(item.getNumber());

        ObjectKind itemKind = item.getKind();

        // generic dice and ac/to_h for armour/launcher multipliers
        if (known.getDamageDice() == 0)
            known.setDamageDice(itemKind.getDamageDice() * itemKnowledge.getDd());
        if (known.getDamageSides() == 0)
            known.setDamageSides(itemKind.getDamageSides() * itemKnowledge.getDs());
        if (known.getBaseAC() == 0)
            known.setBaseAC(itemKind.getAc() * itemKnowledge.getAc());
        if (item.hasStandardToH())
            known.setToHit(itemKind.getToH().getBase());
        if (item.gettValue().isLauncher())
            known.setpValue(item.getpValue());

        // Aware flavours and unflavoured non-wearables
        if ((itemKind.isAware() && itemKind.getFlavour() != null)
                || (!item.gettValue().isWearable() && itemKind.getFlavour() == null)) {
            known.setpValue(item.getpValue());
            known.setEffect(item.getEffect());
        }

        // standard activations
        if (item.gettValue().isWearable() && itemKind.isAware() && itemKind.getEffect() != null)
            known.setEffect(item.getEffect());
    }

    /**
     * Finds the letter or digit the player selects an item by, the port of C's {@code gear_to_label}
     * ({@code obj-gear.c}).
     *
     * <p>Three places an item can be, and each labels differently. Worn equipment takes its letter
     * from the slot it occupies, so a sword's label is a fact about the body rather than about the
     * sword. Quiver ammunition is numbered from {@code '0'}. Everything else in the pack takes its
     * letter from its position in the inventory list. Each label is therefore positional: moving an
     * item renames it, which is why the pack and quiver are ordered lists rather than sets.
     *
     * <p>The label alphabet skips {@code h}, {@code j}, {@code k} and {@code l}. Those are the
     * roguelike movement keys, and an item labelled with one could not be selected without the
     * player walking instead. C keeps the same string for the same reason.
     *
     * <p>Answers the null character for an item the player is not carrying, which is C's {@code '\0'}
     * fall-through rather than an error: asking for the label of something on the floor is a fair
     * question with no answer.
     *
     * <p>Function gearToLabel coded before 260817, commented in full on 260817.
     *
     * @param item the item to label
     * @return the character the item is selected by, or {@code '\0'} if it is not in the gear
     */
    private char gearToLabel(ItemObject item) {
        String labels = "abcdefgimnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        if (body.itemIsEquipped(item)) {
            return labels.charAt(body.equippedItemSlot(item));
        }

        int index = 0;
        for (ItemObject quiverItem : getPlayerUpkeep().getQuiver()) {
            if (quiverItem.equals(item)) {
                return (char) ('0' + index);
            }
            index++;
        }

        int invenIndex = 0;
        for (ItemObject invenItem : getPlayerUpkeep().getInventory()) {
            if (invenItem.equals(item)) {
                return labels.charAt(invenIndex);
            }
            invenIndex++;
        }

        return '\0';
    }

    /**
     * Tests whether the player is carrying a given object, the port of C's
     * {@code object_is_carried} ({@code obj-util.c}).
     *
     * <p>Carried means anywhere in the gear: pack, quiver or worn. C's {@code p->gear} is one linked
     * list holding all three, and equipment is reached by following slot pointers into it rather than
     * by living in a separate collection, so a single containment test answers the question. The port
     * keeps that arrangement, which is why this is one line and not three.
     *
     * <p>The distinction it draws is between an object the player has and an object that is merely
     * nearby — {@link #knowObject} uses it to pick between "You have a Long Sword (c)." and "On the
     * ground: a Long Sword." when reporting something newly recognised.
     *
     * <p>Function isCarried coded on 260816, commented in full on 260816.
     *
     * @param item the object to look for
     * @return {@code true} if the object is in the player's gear
     */
    private boolean isCarried(ItemObject item) {
        return gear.contains(item);
    }

    /**
     * Marks an object's flavour as one the player has become aware of, and propagates the
     * consequences — the port of C's {@code object_flavor_aware} ({@code obj-knowledge.c:2262}).
     *
     * <p><b>Awareness is a property of the kind, not of the object.</b> Learning that the pink potion
     * is a Potion of Speed is learning it about every pink potion in the game at once, which is why
     * the flag is set on {@link ObjectKind} and why so much of this method is a sweep afterwards
     * putting the rest of the world in step. The object passed in is only the occasion for the
     * discovery, not its subject.
     *
     * <p>The early return on an already-aware kind is what makes the method safe to call freely —
     * {@link #knowObject} calls it on every jewellery item whose non-curse runes are all known, which
     * is most of them once the player is experienced. Without it the floor sweep at the foot would
     * run on every pass.
     *
     * <p><b>The three consequences, in C's order.</b> First the effect becomes readable on this
     * object's counterpart, since an identified flavour is an identified effect. Then the ignore
     * settings are reconciled: a kind the player had set to ignore <em>while unaware</em> of it
     * becomes one to ignore now that they are aware, so the pile of unknown potions they were
     * stepping over does not suddenly reappear under a name. {@code PN_IGNORE} then asks for the
     * ignore pass to be re-run. Finally every object the player is carrying has its base knowledge
     * refreshed, because an aware flavour reveals pval and effect that {@link #setBaseKnown}
     * withholds while the kind is unknown.
     *
     * <p>The floor sweep exists because some kinds change tile on awareness, so any square holding
     * an object of this kind needs redrawing. It starts at {@code (1,1)} rather than {@code (0,0)}:
     * the outermost ring of a level is permanent wall and can hold nothing.
     *
     * <p><b>Two pieces are knowingly absent.</b> C also refreshes store stock, which waits on
     * Chapter 8, and {@link uk.co.jackoftrades.middle.cave.Square#lightSpot} is currently an empty
     * stub deferred to Chapter 4, so the sweep computes the right set of squares and then redraws
     * none of them. Neither is a divergence in this method's own logic.
     *
     * <p>Function flavourAware coded on 260816, commented in full on 260816.
     *
     * @param item an object of the kind the player has just become aware of
     */
    private void flavourAware(ItemObject item) {
        ItemObject known = item.getKnown();
        if (known == null) return;
        ObjectKind kind = item.getKind();
        if (kind == null) return;

        if (kind.isAware()) return;
        kind.setAware(true);
        known.setEffect(item.getEffect());

        // Fix ignore/autoinscribe
        if (kind.isIgnoredUnaware())
            kind.setIgnoredAware(true);
        getPlayerUpkeep().orNoticeFlag(PlayerNotice.PN_IGNORE);

        // Update player objects
        for (ItemObject obj : gear) {
            setBaseKnown(obj);
        }

        // Store objects
        // STUB - Todo: Implement in chapter 8

        if (cave == null) return;

        for (int y = 1; y < cave.getHeight(); y++) {
            for (int x = 1; x < cave.getWidth(); x++) {
                boolean light = false;
                Loc grid = Loc.row(y).col(x);

                Iterator<ItemObject> iterator = cave.getSquare(grid).getObjectPile().getIterator();

                while (iterator.hasNext()) {
                    ItemObject floorObj = iterator.next();
                    if (floorObj.getKind() == kind) {
                        light = true;
                        break;
                    }
                }
                if (light) cave.getSquare(grid).lightSpot();
            }
        }
    }

    /**
     * Reports whether the player could recognise an item's ego type from the properties they can
     * already read, the port of C's {@code player_knows_ego} ({@code obj-knowledge.c}).
     *
     * <p>An ego is not learned directly; it is deduced. Every flag, modifier, resistance, brand,
     * slay and curse an ego always grants must be a rune the player can read, because an ego is
     * only identifiable once nothing it confers is still a mystery. So this walks the ego's
     * properties and asks the player's knowledge about each.
     *
     * <p>The modifier test is the subtle one. An ego's modifier is a range rolled per item, so a
     * range spanning zero can leave an item showing nothing at all — and an item showing nothing
     * gives the player nothing to have failed to notice. That is why an unreadable modifier only
     * disqualifies the ego when the range cannot produce zero ({@code modmax * modmin > 0}) or when
     * this particular item did roll a non-zero value. The ranges are evaluated at both extremes at
     * maximum depth, following C.
     *
     * <p>The item is a parameter rather than the ego alone for exactly that test: C accepts a null
     * object and skips the concession when it has no specific item to consult.
     *
     * <p>Function knowsEgo coded before 260817, commented in full on 260817.
     *
     * @param item the item whose ego is being tested
     * @return {@code true} if the ego is one the player could now identify, {@code false} for an
     * item with no ego at all
     */
    private boolean knowsEgo(ItemObject item) {
        EgoItem ego = item.getEgo();

        if (ego == null) return false;

        Flag<ObjectFlag> knownFlags = itemKnowledge.getFlags();
        Flag<ObjectFlag> egoFlags = ego.getFlags();

        // All flags known
        if (!knownFlags.isSubset(egoFlags)) return false;

        // Modifiers all known
        for (ObjectModifier modifier : ObjectModifier.values()) {
            if (modifier == ObjectModifier.OM_NONE || modifier == ObjectModifier.OM_MAX) continue;

            Random egoModifier = ego.getModifier(modifier);
            if (egoModifier == null) continue;

            int modMax = egoModifier.randCalc(GameConstants.getWorldMaxDepth(), DamageAspect.MAXIMIZE);
            int modMin = egoModifier.randCalc(GameConstants.getWorldMaxDepth(), DamageAspect.MINIMIZE);

            if ((modMax > 0 || modMin < 0) && !itemKnowledge.modifierIsKnown(modifier))
                if (modMax * modMin > 0 || item.getModifiers().getOrDefault(modifier, 0) != 0)
                    return false;
        }

        // all elements known
        Map<ElementEnum, ElementInfo> egoElInfo = ego.getElInfo();
        Map<ElementEnum, Boolean> itemElInfo = itemKnowledge.getElementResistInfo();

        for (ElementEnum key : egoElInfo.keySet()) {
            if (key == ElementEnum.ELEM_MAX || key == ElementEnum.ELEM_NONE) continue;
            ElementInfo egoInfo = egoElInfo.get(key);
            if (egoInfo.getResLevel() != 0 && !itemElInfo.get(key))
                return false;
        }

        // All brands known
        Set<Brand> egoBrands = ego.getBrands();
        for (Brand brand : egoBrands) {
            if (!knowsBrand(brand)) return false;
        }

        // All slays known
        Set<Slay> egoSlays = ego.getSlays();
        for (Slay slay : egoSlays) {
            if (!knowsSlay(slay)) return false;
        }

        // All curses known
        for (Curse curse : ego.getCurses().keySet()) {
            if (!knowsCurse(curse)) return false;
        }

        return true;
    }

    /**
     * Recomputes everything about the character that is derived rather than stored, filling
     * {@code state} from the player's race, class, level, equipment, shape and running statuses —
     * the port of C's {@code calc_bonuses} ({@code player-calcs.c:1877-2325}).
     *
     * <p>This is the game's central derivation. Nothing here is remembered between calls: the state
     * is wiped and rebuilt from scratch every time, so there is no incremental update to get wrong
     * and no way for a stale contribution to survive a change of gear.
     *
     * <p><b>Order is the method's substance.</b> The sequence below is not arbitrary and several
     * steps read what earlier ones wrote:
     *
     * <ol>
     *   <li>Defaults — speed 110, one blow — then race and class: infravision, the skill bases, the
     *       innate resistances and the player flags.</li>
     *   <li>Every worn item, and every curse on it, contributing flags, modifiers, resistances and
     *       combat bonuses.</li>
     *   <li>The shape, which adds to all of the above.</li>
     *   <li>Vulnerabilities, held back until now so that a resistance from any source is compared
     *       against the unpenalised level rather than a lowered one.</li>
     *   <li>Light, the environment-dependent resistances, and the stats — converted here from raw
     *       values into the compressed table indices everything downstream subscripts with.</li>
     *   <li>Hunger, then the timed statuses, then fear.</li>
     *   <li>Carried weight against the strength limit, giving the speed penalty.</li>
     *   <li>The stat-derived bonuses to armour, to-hit, to-damage and the skills — <em>after</em>
     *       step 5, because they are table lookups on the indices it computed.</li>
     *   <li>Launcher and weapon, which need the finished strength index to decide whether either is
     *       too heavy to use properly; then mana, which needs the finished stat indices.</li>
     * </ol>
     *
     * <p><b>The two boolean parameters are independent and neither is a debug switch.</b>
     *
     * <p>{@code knownOnly} builds the state the player <em>believes</em> they have rather than the
     * one they have, by admitting a contribution only where the corresponding rune has been learned.
     * It is what the character sheet displays, so that unidentified gear does not give away its
     * properties. Note what it does to a curse: a curse's template object has a blank known
     * counterpart ({@code obj-init.c:188-194}), so under {@code knownOnly} a curse contributes its
     * modifiers and nothing else — no flags, no resistances, no combat bonuses.
     *
     * <p>{@code update} distinguishes a real recalculation from a hypothetical one. When it is
     * clear, the method must not write anything back to the player: the mana calculation stops short
     * of storing a new maximum, the stun handler does not cancel fast-casting, and the stat indices
     * are nudged by the values the incoming state already carried — C's "hack to allow calculating
     * hypothetical blows", which is how the game answers "what would this weapon give me?" without
     * disturbing the character.
     *
     * <p><b>The equipment walk is the part that looks least like its original.</b> C runs a
     * {@code while (obj)} loop that binds one pointer first to the slot's item and then, in turn, to
     * the template object of each curse on it ({@code player-calcs.c:1929-2020}) — one body,
     * {@code n + 1} passes. The port cannot do that directly because a {@link Curse} has no object
     * of its own, so the passes are built as a list of {@link BonusSource} and the body reads
     * whichever is current. The behaviour is the same, including that curse objects' own curses are
     * never walked and that a curse recorded at zero power contributes nothing.
     *
     * <p>Function calcBonuses commented in full on 260820.
     *
     * @param state     the state to fill; wiped on entry and wholly rewritten
     * @param knownOnly {@code true} to count only what the player has learned
     * @param update    {@code true} for a real recalculation that may write back to the player;
     *                  {@code false} for a hypothetical one that must not
     */
    @Contract(mutates = "param1")
    public void calcBonuses(@NotNull PlayerState state, boolean knownOnly, boolean update) {
        int extraBlows = 0;
        int extraShots = 0;
        int extraMight = 0;
        int extraMoves = 0;

        ItemObject launcher = body.equippedItemBySlotName("shooting");
        ItemObject weapon = body.equippedItemBySlotName("weapon");

        Flag<ObjectFlag> f = new Flag<>(ObjectFlag.class);
        Flag<ObjectFlag> collectF = new Flag<>(ObjectFlag.class);
        Map<ElementEnum, Boolean> vulnerabilities = new HashMap<>();

        // Hack to allow calculating hypothetical blows for extra Str, Dex
        int strInd = state.getStatInd(Stats.STAT_STR);
        int dexInd = state.getStatInd(Stats.STAT_DEX);

        // reset the player state
        state.wipe();

        // Various defaults
        state.setSpeed(110);
        state.setNumBlows(100);

        // Race class info
        state.setSeeInfra(race.getInfravision());
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_MAX || skill == PlayerSkill.SKILL_NONE) continue;
            state.setStateSkill(skill, race.getSkill(skill) + playerClass.getSkill(skill));
        }
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            if (race.getResistanceLevel(element) == -1)
                vulnerabilities.put(element, true);
            else
                state.setElInfo(element, race.getResistanceLevel(element));
        }

        // Base pFlags
        state.copyPlayerFlag(race.getpFlags());
        state.unionPlayerFlags(playerClass.getpFlags());

        // Extract the player flags
        flags(state, collectF);

        // Analyse equipment
        for (EquipSlot slot : body.getSlots()) {
            ItemObject item = slot.getItem();
            if (item == null) continue;

            List<BonusSource> sources = new ArrayList<>();
            sources.add(new ItemSource(item));
            for (Map.Entry<Curse, CurseData> e : item.getCurses().entrySet()) {
                if (e.getValue().getPower() != 0) sources.add(new CurseSource(e.getKey()));
            }

            for (BonusSource source : sources) {
                int dig = 0;

                // Extract the item flags
                if (knownOnly) {
                    f = source.flagsKnown();
                } else {
                    f.copyFrom(source.flags());
                }
                collectF.union(f);

                // Apply modifiers
                state.statAdd(Stats.STAT_STR, source.modifier(ObjectModifier.OM_STR)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_STR) ? 1 : 0));
                state.statAdd(Stats.STAT_INT, source.modifier(ObjectModifier.OM_INT)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_INT) ? 1 : 0));
                state.statAdd(Stats.STAT_WIS, source.modifier(ObjectModifier.OM_WIS)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_WIS) ? 1 : 0));
                state.statAdd(Stats.STAT_DEX, source.modifier(ObjectModifier.OM_DEX)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_DEX) ? 1 : 0));
                state.statAdd(Stats.STAT_CON, source.modifier(ObjectModifier.OM_CON)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_CON) ? 1 : 0));
                state.skillAdd(PlayerSkill.SKILL_STEALTH, source.modifier(ObjectModifier.OM_STEALTH)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_STEALTH) ? 1 : 0));
                state.skillAdd(PlayerSkill.SKILL_SEARCH, source.modifier(ObjectModifier.OM_SEARCH) * 5
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_SEARCH) ? 1 : 0));
                state.infraAdd(source.modifier(ObjectModifier.OM_INFRA)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_INFRA) ? 1 : 0));

                if (source.isDigger()) {
                    if (source.flagSet(ObjectFlag.OF_DIG_1))
                        dig = 1;
                    else if (source.flagSet(ObjectFlag.OF_DIG_2))
                        dig = 2;
                    else if (source.flagSet(ObjectFlag.OF_DIG_3))
                        dig = 3;
                }

                dig += source.modifier(ObjectModifier.OM_TUNNEL)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_TUNNEL) ? 1 : 0);
                state.skillAdd(PlayerSkill.SKILL_DIGGING, dig * 20);
                state.setSpeed(state.getSpeed() + source.modifier(ObjectModifier.OM_SPEED)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_SPEED) ? 1 : 0));
                state.setDamRed(state.getDamRed() + source.modifier(ObjectModifier.OM_DAM_RED)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_DAM_RED) ? 1 : 0));
                extraBlows += source.modifier(ObjectModifier.OM_BLOWS)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_BLOWS) ? 1 : 0);
                extraShots += source.modifier(ObjectModifier.OM_SHOTS)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_SHOTS) ? 1 : 0);
                extraMight += source.modifier(ObjectModifier.OM_MIGHT)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_MIGHT) ? 1 : 0);
                extraMoves += source.modifier(ObjectModifier.OM_MOVES)
                        * (itemKnowledge.modifierIsKnown(ObjectModifier.OM_MOVES) ? 1 : 0);

                // Apply element info, noting vulnerabilities for later processing
                for (ElementEnum element : ElementEnum.values()) {
                    if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
                    if (!knownOnly || source.knownResLevel(element) != 0) {
                        if (source.resLevel(element) == -1)
                            vulnerabilities.put(element, true);

                        // Res level hasn't included vulnerability yet
                        if (source.resLevel(element) > state.getResLevel(element))
                            state.setResLevel(element, source.resLevel(element));
                    }
                }

                // Apply combat bonuses
                state.setBaseAc(state.getBaseAc() + source.baseAC());
                if (!knownOnly || source.knownToAC() != 0)
                    state.toAcAdd(source.toAC());
                if (slot.getType() != EquipmentSlotsEnum.EQUIP_WEAPON &&
                        slot.getType() != EquipmentSlotsEnum.EQUIP_BOW) {

                    if (!knownOnly || source.knownToHit() != 0) {
                        state.toHitAdd(source.toHit());
                    }
                    if (!knownOnly || source.knownToDam() != 0) {
                        state.toDamAdd(source.toDam());
                    }
                }
            }
        }

        // apply collected flags
        state.unionObjectFlags(collectF);

        // Add shapechange info
        Extras ingoing = new Extras(extraBlows, extraShots, extraMight, extraMoves);
        Extras outgoing = calcShapechange(state, vulnerabilities, shape, ingoing);
        extraBlows = outgoing.blows();
        extraShots = outgoing.shots();
        extraMight = outgoing.might();
        extraMoves = outgoing.moves();

        // Vulnerabilities
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            if (vulnerabilities.getOrDefault(element, false) && (state.getResLevel(element) < 3))
                state.setResLevel(element, state.getResLevel(element) - 1);
        }

        // Light
        calcLight(state, update);

        // Unlight - needs change if anything but resist is introduced for dark
        if (state.hasPFlag(PlayerFlag.PF_UNLIGHT) && GameWorld.hasCharacterDungeon()) {
            state.setElInfo(ElementEnum.ELEM_DARK, 1);
        }

        // Evil
        if (state.hasPFlag(PlayerFlag.PF_EVIL) && GameWorld.hasCharacterDungeon()) {
            state.setElInfo(ElementEnum.ELEM_NETHER, 1);
            state.setElInfo(ElementEnum.ELEM_HOLY_ORB, -1);
        }

        // Various stat values
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_MAX || stat == Stats.STAT_NONE) continue;

            int add = state.getStatAdd(stat);
            add += race.getStatAdjust(stat);
            add += playerClass.getStatsAdj(stat);
            state.setStatTop(stat, PlayerUtils.modifyStatValue(statMax.get(stat), add));
            int use = PlayerUtils.modifyStatValue(statCur.get(stat), add);

            state.setStatUse(stat, use);

            int ind;
            if (use <= 3)
                ind = 0;
            else if (use <= 18)
                ind = use - 3;
            else if (use <= 18 + 219)
                ind = (15 + (use - 18) / 10);
            else
                ind = 37;

            // Hack for hypothetical blows
            if (!update) {
                if (stat == Stats.STAT_STR) {
                    ind += strInd;
                    ind = Math.min(ind, 37);
                    ind = Math.max(ind, 3);
                } else if (stat == Stats.STAT_DEX) {
                    ind += dexInd;
                    ind = Math.min(ind, 37);
                    ind = Math.max(ind, 3);
                }
            }

            // save the new index
            state.setStatInd(stat, ind);
        }

        // Effects of food outside the "fed" range
        if (!timedGradeEq(TimedEffect.TMD_FOOD, "Fed")) {
            int excess = timed.get(TimedEffect.TMD_FOOD) - Food.PY_FOOD_FULL.getFoodValue();
            int lack = Food.PY_FOOD_HUNGRY.getFoodValue() - timed.get(TimedEffect.TMD_FOOD);
            if (excess > 0 && timed.get(TimedEffect.TMD_ATT_VAMP) == 0) {
                excess = (excess * 10) / (Food.PY_FOOD_MAX.getFoodValue() - Food.PY_FOOD_FULL.getFoodValue());
                state.setSpeed(state.getSpeed() - excess);
            } else if (lack > 0) {
                // Scale to 1/20 of range
                lack = (lack * 20) / Food.PY_FOOD_HUNGRY.getFoodValue();

                // Apply effects progressively
                state.toHitAdd(-lack);
                state.toDamAdd(-lack);
                if (lack > 10 && lack <= 15) {
                    int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
                    value = adjustSkillScale(value, -1, 10, 0);
                    state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
                } else if (lack > 15 && lack <= 18) {
                    int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
                    value = adjustSkillScale(value, -1, 5, 0);
                    state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS) * 9);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS) / 10);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC) * 9);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC) / 10);
                } else if (lack > 18) {
                    int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
                    value = adjustSkillScale(value, -3, 10, 0);
                    state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS) * 8);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS) / 10);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC) * 8);
                    state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC) / 10);
                    state.setStateSkill(PlayerSkill.SKILL_SAVE, state.getPlayerSkill(PlayerSkill.SKILL_SAVE) * 9);
                    state.setStateSkill(PlayerSkill.SKILL_SAVE, state.getPlayerSkill(PlayerSkill.SKILL_SAVE) / 10);
                    state.setStateSkill(PlayerSkill.SKILL_SEARCH, state.getPlayerSkill(PlayerSkill.SKILL_SEARCH) * 9);
                    state.setStateSkill(PlayerSkill.SKILL_SEARCH, state.getPlayerSkill(PlayerSkill.SKILL_SEARCH) / 10);
                }
            }
        }

        // Other timed effects
        flagsTimed(state.getObjectFlag());

        if (timedGradeEq(TimedEffect.TMD_STUN, "Heavy Stun")) {
            state.toHitAdd(-20);
            state.toDamAdd(-20);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 5, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
            if (update)
                timed.put(TimedEffect.TMD_FASTCAST, 0);
        } else if (timedGradeEq(TimedEffect.TMD_STUN, "Stun")) {
            state.toHitAdd(-5);
            state.toDamAdd(-5);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 10, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
            if (update)
                timed.put(TimedEffect.TMD_FASTCAST, 0);
        }
        if (timed.getOrDefault(TimedEffect.TMD_INVULN, 0) != 0)
            state.toAcAdd(100);
        if (timed.getOrDefault(TimedEffect.TMD_BLESSED, 0) != 0) {
            state.toAcAdd(5);
            state.toHitAdd(10);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, 1, 20, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (timed.getOrDefault(TimedEffect.TMD_SHIELD, 0) != 0)
            state.toAcAdd(50);
        if (timed.getOrDefault(TimedEffect.TMD_STONESKIN, 0) != 0) {
            state.toAcAdd(40);
            state.setSpeed(state.getSpeed() - 5);
        }
        if (timed.getOrDefault(TimedEffect.TMD_HERO, 0) != 0) {
            state.toHitAdd(12);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, 1, 20, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (timed.getOrDefault(TimedEffect.TMD_SHERO, 0) != 0) {
            state.skillAdd(PlayerSkill.SKILL_TO_HIT_MELEE, 75);
            state.toAcAdd(-10);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 10, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (timed.getOrDefault(TimedEffect.TMD_FAST, 0) != 0
                || timed.getOrDefault(TimedEffect.TMD_SPRINT, 0) != 0)
            state.setSpeed(state.getSpeed() + 10);
        if (timed.getOrDefault(TimedEffect.TMD_SLOW, 0) != 0)
            state.setSpeed(state.getSpeed() - 10);
        if (timed.getOrDefault(TimedEffect.TMD_SINFRA, 0) != 0)
            state.infraAdd(5);
        if (timed.getOrDefault(TimedEffect.TMD_TERROR, 0) != 0)
            state.setSpeed(state.getSpeed() + 10);
        for (TimedEffect tmd : TimedEffect.values()) {
            if (tmd == TimedEffect.TMD_NONE) continue;
            int resLevel;
            PlayerTimedEffect effect = PlayerRegistry.lookupPlayerTimedEffect(tmd);
            if (effect != null) {
                ElementEnum elementEnum = effect.getTempResist();
                if (elementEnum == null)
                    resLevel = 0;
                else {
                    ElementInfo elementInfo = state.getElInfo().getOrDefault(elementEnum, null);
                    if (elementInfo == null)
                        resLevel = 0;
                    else
                        resLevel = elementInfo.getResLevel();
                }
            } else {
                resLevel = 0;
            }
            if (timed.getOrDefault(tmd, 0) != 0 && effect != null
                    && effect.getTempResist() != ElementEnum.ELEM_NONE
                    && state.getElInfo().get(effect.getTempResist()) != null
                    && resLevel < 2)
                state.setElInfo(effect.getTempResist(), resLevel + 1);
        }
        if (timed.getOrDefault(TimedEffect.TMD_CONFUSED, 0) != 0) {
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 4, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (timed.getOrDefault(TimedEffect.TMD_AMNESIA, 0) != 0) {
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 5, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (timed.getOrDefault(TimedEffect.TMD_POISONED, 0) != 0) {
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 20, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (timed.getOrDefault(TimedEffect.TMD_IMAGE, 0) != 0) {
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 5, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }
        if (timed.getOrDefault(TimedEffect.TMD_BLOODLUST, 0) != 0) {
            state.toDamAdd(timed.getOrDefault(TimedEffect.TMD_BLOODLUST, 0) / 2);
            extraBlows += timed.getOrDefault(TimedEffect.TMD_BLOODLUST, 0) / 20;
        }
        if (timed.getOrDefault(TimedEffect.TMD_STEALTH, 0) != 0)
            state.setStateSkill(PlayerSkill.SKILL_STEALTH, state.getPlayerSkill(PlayerSkill.SKILL_STEALTH) + 10);

        // Analyze flags, check for fear
        if (state.hasOFlag(ObjectFlag.OF_AFRAID)) {
            state.toHitAdd(-20);
            state.toAcAdd(8);
            int value = state.getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            value = adjustSkillScale(value, -1, 20, 0);
            state.setStateSkill(PlayerSkill.SKILL_DEVICE, value);
        }

        // Analyze weight
        int totalWeight = getPlayerUpkeep().getTotalWeight();
        int limit = state.weightLimit();
        if (totalWeight > limit / 2)
            state.setSpeed(state.getSpeed() - (totalWeight - (limit / 2)) / (limit / 10));
        if (state.getSpeed() < 0)
            state.setSpeed(0);
        if (state.getSpeed() > 199)
            state.setSpeed(199);

        // Apply modifier bonuses (un-inflate stat bonuses)
        state.toAcAdd(StatTables.adjDexTa[state.getStatInd(Stats.STAT_DEX)]);
        state.toDamAdd(StatTables.adjStrTd[state.getStatInd(Stats.STAT_STR)]);
        state.toHitAdd(StatTables.adjDexTh[state.getStatInd(Stats.STAT_DEX)]);
        state.toHitAdd(StatTables.adjStrTh[state.getStatInd(Stats.STAT_STR)]);

        // Modify skills
        state.setStateSkill(PlayerSkill.SKILL_DISARM_PHYS, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS)
                + StatTables.adjDexDis[state.getStatInd(Stats.STAT_DEX)]);
        state.setStateSkill(PlayerSkill.SKILL_DISARM_MAGIC, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC)
                + StatTables.adjIntDis[state.getStatInd(Stats.STAT_INT)]);
        state.setStateSkill(PlayerSkill.SKILL_DEVICE, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)
                + StatTables.adjIntDev[state.getStatInd(Stats.STAT_INT)]);
        state.setStateSkill(PlayerSkill.SKILL_SAVE, state.getPlayerSkill(PlayerSkill.SKILL_SAVE)
                + StatTables.adjWisSav[state.getStatInd(Stats.STAT_WIS)]);
        state.setStateSkill(PlayerSkill.SKILL_DIGGING, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING)
                + StatTables.adjStrDig[state.getStatInd(Stats.STAT_STR)]);

        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_MAX || skill == PlayerSkill.SKILL_NONE) continue;

            state.setStateSkill(skill, state.getPlayerSkill(skill) + getPlayerClass().getXSkill(skill) * level / 10);
        }

        if (state.getPlayerSkill(PlayerSkill.SKILL_DIGGING) < 1) state.setStateSkill(PlayerSkill.SKILL_DIGGING, 1);
        if (state.getPlayerSkill(PlayerSkill.SKILL_STEALTH) > 30) state.setStateSkill(PlayerSkill.SKILL_STEALTH, 30);
        if (state.getPlayerSkill(PlayerSkill.SKILL_STEALTH) < 0) state.setStateSkill(PlayerSkill.SKILL_STEALTH, 0);
        int hold = StatTables.adjStrHold[state.getStatInd(Stats.STAT_STR)];

        // Analyze launcher
        state.setHeavyShoot(false);
        if (launcher != null) {
            int launcherWeight = launcher.weightOne();

            if (hold < launcherWeight / 10) {
                state.toHitAdd(2 * (hold - launcherWeight / 10));
                state.setHeavyShoot(true);
            }

            state.setNumShots(10);

            // Type of ammo
            if (launcher.getKind() == null)
                state.setAmmoTValue(TValue.TV_NONE);
            else {
                if (launcher.getKind().getKindFlags().has(ObjectKindFlag.KF_SHOOTS_SHOTS))
                    state.setAmmoTValue(TValue.TV_SHOT);
                else if (launcher.getKind().getKindFlags().has(ObjectKindFlag.KF_SHOOTS_ARROWS))
                    state.setAmmoTValue(TValue.TV_ARROW);
                else if (launcher.getKind().getKindFlags().has(ObjectKindFlag.KF_SHOOTS_BOLTS))
                    state.setAmmoTValue(TValue.TV_BOLT);
            }

            // Multiplier
            state.setAmmoMult(launcher.getpValue());

            // Special flags
            if (!state.isHeavyShoot()) {
                state.setNumShots(state.getNumShots() + extraShots);
                state.setAmmoMult(state.getAmmoMult() + extraMight);
                if (state.hasPFlag(PlayerFlag.PF_FAST_SHOT))
                    state.setNumShots(state.getNumShots() + level / 3);
            }

            // Need at least 1 shot
            if (state.getNumShots() < 10) state.setNumShots(10);
        }

        // Analyse weapon
        state.setHeavyWield(false);
        state.setBlessWield(false);
        if (weapon != null) {
            int weaponWeight = weapon.weightOne();

            // Can you take the weight (of the weapon)
            if (hold < weaponWeight / 10) {
                state.toHitAdd(2 * (hold - weaponWeight / 10));
                state.setHeavyWield(true);
            }

            if (!state.isHeavyWield()) {
                state.setNumBlows(calcBlows(weapon, state, extraBlows));
                state.setStateSkill(PlayerSkill.SKILL_DIGGING, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING) + weaponWeight / 10);
            }

            // Divine weapon bonus
            if (state.hasPFlag(PlayerFlag.PF_BLESS_WEAPON)
                    && (weapon.gettValue() == TValue.TV_HAFTED || state.hasOFlag(ObjectFlag.OF_BLESSED))) {
                state.toDamAdd(2);
                state.setBlessWield(true);
            }
        } else {
            // unarmed
            state.setNumBlows(calcBlows(null, state, extraBlows));
        }

        // Mana
        calcMana(state, update);
        if (maxSP == 0)
            state.setPlayerFlag(PlayerFlag.PF_NO_MANA);

        state.setNumMoves(extraMoves);
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
     * Reduces a stat, optionally permanently — the port of C's {@code player_stat_dec}
     * ({@code player.c}).
     *
     * <p><b>Stub:</b> not yet implemented; reports no change.
     *
     * @param stat      the stat to lower
     * @param permanant whether the loss also reduces the stat's maximum
     * @return {@code true} if the stat actually changed
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
     */
    private void adjustLevel(boolean verbose) {
        // Stub function TODO: implement
    }

    /**
     * @return the player's current experience points
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
     */
    public int getMaxHP() {
        return maxHP;
    }

    /**
     * @return {@code true} if the player is currently resting — either the resting counter is still
     * running or a special stop-condition rest is in progress
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
     */
    private boolean restingIsSpecial(int restingCounter) {
        // Stub function TODO: implement
        return false;
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
     * Recomputes and stores the depth Word of Recall should return the player to — the port of C's
     * recall-depth handling.
     *
     * <p><b>Stub:</b> not yet implemented.
     */
    public void setRecallDepth() {
        // Stub function TODO: implement
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
     * Tests whether a given dungeon level hosts one of the player's outstanding quests — the port of
     * C's {@code is_quest} ({@code player-quest.c}). The town (level 0) never holds a quest.
     *
     * @param level the dungeon level to test
     * @return {@code true} if a quest target lives on that level
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
     * <p><b>Package-private, and that is the whole of the enforcement.</b> C's {@code static} means
     * nothing outside {@code obj-knowledge.c} can call it; the package is this port's equivalent, so
     * every wrapper and every {@code object_curses_find_*} helper belongs in
     * {@code middle.player} beside it. The rule has been broken once already — the curse-finding
     * family briefly lived on {@link ItemObject}, which forced this method public for as long as it
     * did. If a future learning path seems to want an object-side home
     * ({@code item.learnOnWield(player)} rather than {@code player.learnOnWield(item)}), that is the
     * same mistake wearing different clothes. Knowledge is player state, the item is only the thing
     * being read, and C's argument order says so.
     *
     * <p>Package-private rather than {@code private} because {@code PlayerRuneLearningTest} shares
     * the package and drives this directly, to exercise each of the seven variety arms in isolation.
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
     * <p>Function learnRune coded before 260815, commented in full before 260815, narrowed to
     * package-private on 260815, briefly public while the curse-finding family lived on
     * {@link ItemObject}, and narrowed again on 260815 when that family moved here.
     *
     * @param printMessage whether to announce the discovery, false for the paths that learn in
     *                     bulk and would otherwise bury the player in messages
     */
    void learnRune(Rune rune, boolean printMessage) {
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
     * <p><b>Two of the four populations are live.</b> The level and the pack are walked; stores and
     * curse objects are not, and neither is a matter of writing the loop:
     *
     * <ul>
     *   <li><b>Stores</b> wait on the shop subsystem, Chapter 8.</li>
     *   <li><b>Curse objects</b> wait on somewhere to put the answer. C sweeps
     *       {@code curses[i].obj}, which is the curse's properties held as a template object with a
     *       known counterpart of its own; the port flattens those properties onto {@link Curse}
     *       itself, which is the more accurate shape but leaves no field holding what the player has
     *       learned about them.</li>
     *   <li><b>Autoinscribe</b> of ground and pack waits on Chapter 4.</li>
     * </ul>
     *
     * <p><b>The guards are not symmetrical, and only one of them is C's.</b> {@code if (cave)} is
     * real and load-bearing — knowledge is updated during birth and on loading a save, before any
     * level exists. The null test on the gear has no counterpart: C walks {@code p->gear} as a linked
     * list, where a null head is simply an empty loop, while a null {@link java.util.ArrayList} would
     * throw. That guard is the port paying for the container change, not copying anything.
     *
     * <p><b>The two signals sit outside every guard</b>, so they fire even when nothing was walked.
     * That is C's placement and it is right: the display has to redraw on the strength of the rune
     * just learned, whether or not any object currently in play happens to carry it.
     *
     * <p>The real work is delegated to {@link #knowObject}, which was written on 260816. What this
     * method is responsible for is the shape around it: the populations, their order, the guards and
     * the signals. See {@code PlayerUpdateObjectKnowledgeTest}, which observes the walk rather than
     * its outcome — deliberately, so that it stays valid however {@code knowObject} changes.
     *
     * <p>Function updateObjectKnowledge coded before 260815 as a stub, implemented as far as the
     * available subsystems allow on 260815, commented in full on 260815. Stub note on
     * {@code knowObject} corrected on 260816.
     */
    public void updateObjectKnowledge() {
        // Know the cave objects
        if (cave != null) {
            for (ItemObject itemObject : cave.getObjects()) {
                knowObject(itemObject);
            }
        }

        // Know the player objects
        if (gear != null) {
            for (ItemObject itemObject : gear) {
                knowObject(itemObject);
            }
        }

        // Store objects
        // TODO: Implement this branch in chapter 8

        // Curse objects
        // TODO: Implement this branch once known object on curse is understood

        // Inscription
        // TODO: Implement this branch in chapter 4
        //if (cave != null) 
        //    autoinscribeGround();
        //autoinscribePack();

        EventsHandler eventsBusHandler = GameEngine.getEventsBusHandler();
        eventsBusHandler.eventSignal(GameEventType.EVENT_INVENTORY);
        eventsBusHandler.eventSignal(GameEventType.EVENT_EQUIPMENT);
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
     *   <li>each equipped item's curses, via {@link #cursesFindToA}, which learns the
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
            cursesFindToA(slotObject);
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
     * {@link #cursesFindToH}, the walk stops at the first success because
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
            cursesFindToH(slotObject);
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
     * curse pair {@link #cursesFindToH} and {@link #cursesFindToD} is then asked
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

            cursesFindToD(slotObject);
            cursesFindToH(slotObject);
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
     *       {@link #cursesFindFlags} runs unconditionally. It takes a set rather than a
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
     * <p><b>Outstanding:</b> {@link ItemObject#description} is still a stub, deferred to Chapter 7,
     * so both this method's message and the one {@link #cursesFindFlags} sends name the item with a
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
            } else if (!slotObject.isFullyKnown() && slotObject.getKnown() != null) {
                slotObject.getKnown().setFlag(flag);
            }

            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            flags.on(flag);

            cursesFindFlags(slotObject, flags);
        }
    }

    /**
     * Learns the to-AC rune, and the curse's own rune, if any curse on the given item contributes an
     * armour-class change the player has just felt. The port of C's
     * {@code object_curses_find_to_a} ({@code obj-knowledge.c:1557}), the first of six near-identical
     * functions covering to-AC, to-hit, to-damage, flags, modifiers and elements.
     *
     * <p>A curse is a thing the player learns by being bitten by it, which is why this is reached
     * from {@link #equipLearnOnDefend} rather than from anything to do with inspecting the item. Two
     * runes are learned, not one: the fact that <em>something</em> is altering the armour class, and
     * the identity of the curse doing it.
     *
     * <p><b>Why the family lives here and not on {@link ItemObject}.</b> All six are {@code static}
     * in {@code obj-knowledge.c}, the same translation unit as {@code player_learn_rune} — they are
     * not object methods in C but player-side helpers that take an object, and the signature says so:
     * {@code (struct player *p, struct object *obj)}. C's file boundary is this port's package
     * boundary, so putting them here is what keeps {@link #learnRune} package-private and lets the
     * compiler refuse any caller that reaches past a wrapper. They read the item entirely through its
     * public surface.
     *
     * <p><b>Where the numbers come from.</b> The armour-class figure belongs to the curse
     * definition, not to the item — {@link Curse#getCombatAC}, the port of {@code curses[i].obj->to_a},
     * parsed once from {@code curse.txt}. What the item holds is the instance data: the power and
     * timeout in {@link CurseData}. C keeps those in two arrays indexed alike, so every one of these
     * functions has to walk {@code 1 .. curse_max} and read {@code obj->curses[i].power} and
     * {@code curses[i].obj->to_a} at the same subscript. {@link ItemObject#getCurses} pairs them
     * directly, mapping each curse to its own {@link CurseData}, so the loop visits only the curses
     * the item actually carries and no index arithmetic survives the port.
     *
     * <p>That also disposes of C's two guards. {@code !obj->curses[i].power} is what stops a dense
     * array from reporting curses the item does not have, and is unnecessary against a map that only
     * contains the ones it does — the port removes a curse outright rather than zeroing it, so an
     * entry of power zero should not arise. The test is kept as a cheap restatement of that
     * invariant. {@code !curses[i].obj} is dead code upstream: the parser allocates that object at
     * the {@code name:} line, so the only null in the array is index 0, the reserved no-curse slot
     * the loop already skips.
     *
     * <p>The rune is resolved once, before the loop. C recomputes it into the same {@code index}
     * variable it then overwrites with the curse's rune, so on a second qualifying curse it relearns
     * the previous curse instead of the to-AC rune — harmless there only because the to-AC rune is
     * already known by that point. Hoisting the lookup out makes the bug unexpressible.
     *
     * <p>Function cursesFindToA coded before 260815, commented in full before 260815, moved here
     * from {@link ItemObject} on 260815 and its arguments turned round to C's order.
     *
     * @param item the item whose curses are being read
     */
    void cursesFindToA(ItemObject item) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_A);
        if (!item.getCurses().isEmpty()) {
            for (Curse curse : item.getCurses().keySet()) {
                CurseData value = item.getCurses().get(curse);
                if (value.getPower() != 0)
                    if (curse.getCombatAC() != 0) {
                        // Learn the to AC rune
                        learnRune(rune, true);
                        // Learn the to AC Curse rune
                        learnRune(Rune.runeIndex(curse), true);
                    }
            }
        }
    }

    /**
     * Learns the to-damage rune, and the curse's own rune, if any curse on the given item
     * contributes a damage change the player has just dealt. The port of C's
     * {@code object_curses_find_to_d} ({@code obj-knowledge.c:1603}), the to-damage sibling of
     * {@link #cursesFindToA}.
     *
     * <p>Structurally identical to that method, and the reasoning there applies unchanged: why the
     * family lives on {@link Player} rather than {@link ItemObject}, why the figure is read from the
     * curse definition ({@link Curse#getCombatDam}, C's {@code curses[i].obj->to_d}) rather than from
     * the item, why the power test survives, and why the rune is resolved once above the loop.
     *
     * <p>What differs is the occasion. This is reached from {@link #equipLearnOnMeleeAttack} — a
     * curse that saps damage announces itself when a blow lands softly, not when one is taken.
     *
     * <p>Function cursesFindToD coded on 260815, commented in full on 260815, moved here from
     * {@link ItemObject} on 260815 and its arguments turned round to C's order, {@code testFlags}
     * widened to {@link FlagView} on 260818.
     *
     * @param item the item whose curses are being read
     */
    void cursesFindToD(ItemObject item) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_D);
        if (!item.getCurses().isEmpty()) {
            for (Curse curse : item.getCurses().keySet()) {
                if (item.getCurses().get(curse).getPower() != 0)
                    if (curse.getCombatDam() != 0) {
                        // Learn the to-damage rune
                        learnRune(rune, true);
                        // Learn the rune of the curse that caused it
                        learnRune(Rune.runeIndex(curse), true);
                    }
            }
        }
    }

    /**
     * Learns the to-hit rune, and the curse's own rune, if any curse on the given item contributes
     * an accuracy change the player has just felt. The port of C's {@code object_curses_find_to_h}
     * ({@code obj-knowledge.c:1580}), the to-hit sibling of {@link #cursesFindToA}.
     *
     * <p>Structurally identical to that method — see it for why the family lives here, why the
     * figure is read from the curse definition ({@link Curse#getCombatToHit}, C's
     * {@code curses[i].obj->to_h}), why the power test is kept, and why the rune is hoisted above
     * the loop.
     *
     * <p>This is the one of the three reached from both attack methods,
     * {@link #equipLearnOnMeleeAttack} and {@link #equipLearnOnRangedAttack}: a curse that spoils
     * the player's aim shows itself whichever way they attack.
     *
     * <p>Note that the curse's contribution is judged by a plain non-zero test, with no counterpart
     * to {@link ItemObject#hasStandardToH}. That asymmetry is correct: "standard" is a fact about
     * what a kind of item normally carries, and a curse has no normal to-hit to be measured against.
     *
     * <p>Function cursesFindToH coded on 260815, commented in full on 260815, moved here from
     * {@link ItemObject} on 260815 and its arguments turned round to C's order, {@code testFlags}
     * widened to {@link FlagView} on 260818.
     *
     * @param item the item whose curses are being read
     */
    void cursesFindToH(ItemObject item) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H);
        if (!item.getCurses().isEmpty()) {
            for (Curse curse : item.getCurses().keySet()) {
                if (item.getCurses().get(curse).getPower() != 0)
                    if (curse.getCombatToHit() != 0) {
                        // Learn the to-hit rune
                        learnRune(rune, true);
                        // Learn the rune of the curse that caused it
                        learnRune(Rune.runeIndex(curse), true);
                    }
            }
        }
    }

    /**
     * Learns any of the given flags that a curse on the given item has just betrayed, together with
     * the rune of the curse betraying them — the port of C's {@code object_curses_find_flags}
     * ({@code obj-knowledge.c:1634}), the flag member of the same family as {@link #cursesFindToA}
     * and its two siblings.
     *
     * <p><b>Why this one takes a set where the others take nothing.</b> The to-AC, to-hit and
     * to-damage finders each pursue a single fixed property, so the caller has nothing to say. Flags
     * are a population, and the caller decides which of them this occasion could plausibly have
     * revealed. C passes that as a {@code bitflag *test_flags} and intersects it with the curse's
     * own flags, keeping only what is in both. Three call sites, three different sets: a one-element
     * set built on the spot by {@code equip_learn_flag}, the {@code obvious_mask} of everything a
     * wield could show, and the {@code timed_mask} of what only prolonged wear reveals.
     *
     * <p>As in the sibling finders, two runes are learned per hit and not one — the flag itself, and
     * the identity of the curse that carries it. The curse's rune is learned whether or not the flag
     * was new, since meeting a curse is knowledge even when its effect was already understood.
     *
     * <p><b>The intersection is taken on a copy, and it has to be.</b> {@link Flag#inter} is
     * {@code retainAll} — it mutates the set it is called on. The flags being intersected belong to
     * the {@link Curse} definition parsed once from {@code curse.txt} and shared by every item
     * carrying that curse, so intersecting them in place would permanently delete from the
     * definition every flag this one occasion happened not to be asking about.
     * {@link Flag#set(java.util.List)} copies element by element into a fresh set, which is what
     * keeps the definition intact. The caller's own set is left alone for the same reason:
     * {@link #equipLearnFlag} builds one and hands it to every slot in turn.
     *
     * <p><b>The curse's rune is learned inside the flag loop, not beside it.</b> That is C's
     * placement and it is load-bearing in one direction: a curse whose flags do not meet the test
     * set teaches nothing at all, not even its own existence, because the player has had no
     * evidence of it. It also means a curse matching two flags learns its rune twice, which the
     * guard inside {@link #learnRune} makes harmless.
     *
     * <p><b>The message is conditional where the learning is not.</b> C wraps only
     * {@code flag_message} in {@code p->upkeep->playing}, so knowledge is recorded during character
     * generation and loading but nothing is announced into a game that has not started. The
     * returned {@code boolean} is C's {@code new} — true if any flag was learned that was not
     * already known, ignored by this caller and used by the wield-time learning.
     *
     * <p>The per-curse guard is on {@link CurseData#getPower}, as in the three sibling finders and
     * as C's {@code if (!obj->curses[i].power)} requires. Power is what says the curse is on the
     * item at all — {@link CurseData#setPower} with a zero is how a curse is removed, so a zeroed
     * entry can outlive the curse it names. C's second guard, {@code !curses[i].obj}, has no
     * counterpart: it exists to skip the reserved index 0 of a dense array, and a map holding only
     * the curses this item carries has no such hole.
     *
     * <p><b>Outstanding:</b> {@link ItemObject#description} is still a stub, deferred to Chapter 7,
     * so the message names the item with a placeholder.
     *
     * <p>Function cursesFindFlags coded on 260815, commented in full on 260815, moved here from
     * {@link ItemObject} on 260815 and its arguments turned round to C's order, {@code testFlags}
     * widened to {@link FlagView} on 260818.
     *
     * @param item      the item whose curses are being read
     * @param testFlags the flags this occasion could have revealed, C's {@code test_flags}; read
     *                  only, hence the {@link FlagView} — it is intersected into a working copy
     *                  rather than modified
     * @return whether any flag was learned that the player did not already know
     */
    boolean cursesFindFlags(ItemObject item, FlagView<ObjectFlag> testFlags) {
        boolean curseLearned = false;

        Flag<ObjectDescription> baseDesc = new Flag<>(ObjectDescription.class);
        baseDesc.on(ObjectDescription.ODESC_BASE);
        String name = item.description(baseDesc, this);

        if (item.getCurses().isEmpty()) return false;

        // Only loop through the curses on the object, not the entire set of curses
        for (Curse curse : item.getCurses().keySet()) {
            CurseData value = item.getCurses().get(curse);
            if (value.getPower() == 0) continue;

            Flag<ObjectFlag> toTest = new Flag<>(ObjectFlag.class);
            toTest.set(curse.getObjectFlags());
            toTest.inter(testFlags);

            for (ObjectFlag testSubject : toTest) {
                if (!itemKnowledge.flagIsKnown(testSubject)) {
                    curseLearned = true;
                    learnRune(Rune.runeIndex(testSubject), true);
                    if (getPlayerUpkeep().isPlaying())
                        item.flagMessage(testSubject, name);
                }

                // Learn the curse
                Rune rune = Rune.runeIndex(curse);
                if (rune != null)
                    learnRune(rune, true);
            }
        }

        return curseLearned;
    }

    /**
     * Reports whether an active timed effect is currently at the grade of the given name — the
     * port of C's {@code player_timed_grade_eq} ({@code player-timed.c:734}).
     *
     * <p>A timed effect is a single counter, but the player-facing status is a band of that
     * counter: stunning runs "Stun" → "Heavy Stun" → "Knocked Out" as the number climbs. This
     * answers which band the counter is in, by name, so that callers can branch on severity
     * without knowing the thresholds — {@code GameWorld.decreaseTimeouts} asks whether a wound is
     * a "Mortal Wound" before deciding it does not bleed down, and the digestion code asks whether
     * nourishment is "Full" or "Faint".
     *
     * <p><b>Exactly one grade is tested.</b> The grades are ordered by ascending {@code max}, and
     * the effect's band is the first one whose {@code max} the value does not exceed; that grade's
     * name is compared and the answer returned whether it matched or not. Continuing past it into
     * the higher grades would be the easy mistake, because their maxima also cover the value — a
     * lightly stunned player would answer {@code true} to "Knocked Out". C expresses this as a
     * {@code while} that walks to the band and then a single {@code streq} outside the loop.
     *
     * <p>An effect at zero answers {@code false} without consulting its grades, matching C's
     * opening {@code if (p->timed[idx])}. The check is needed rather than incidental: the map is
     * populated with a zero for every effect at construction, and the port's grade list has no
     * entry for the dormant state, so a zero reaching the loop would be tested against the first
     * real grade.
     *
     * <p>The null definition guard has no counterpart in C, which indexes a static table that is
     * always populated. Here the effects are loaded from {@code player_timed.txt} into
     * {@link PlayerRegistry}, and {@link PlayerRegistry#lookupPlayerTimedEffect} answers null for
     * an effect with no loaded definition — {@link TimedEffect#TMD_NONE} being the standing
     * example, though its zero value means it never reaches this far.
     *
     * <p>Function timedGradeEq coded on 260818, commented in full on 260818.
     *
     * @param index the timed effect to inspect
     * @param match the grade name to compare against, as written in {@code player_timed.txt}
     * @return {@code true} if the effect is running and its current grade has that name
     */
    public boolean timedGradeEq(TimedEffect index, String match) {
        if (timed != null && timed.containsKey(index) && timed.get(index) != 0) {
            int value = timed.get(index);
            PlayerTimedEffect effect = PlayerRegistry.lookupPlayerTimedEffect(index);
            if (effect == null) return false;

            List<TimedGrade> grades = effect.getGrade();

            for (TimedGrade grade : grades) {
                if (grade.max() < value)
                    continue;

                return (grade.status() != null && grade.status().equals(match));
            }
        }

        return false;
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
    public void flags(@NotNull PlayerState state, @NotNull Flag<ObjectFlag> flags) {
        flags.copyFrom(race.getoFlags());
        flags.union(playerClass.getoFlags());
        if (state.hasPFlag(PlayerFlag.PF_BRAVERY_30) && level >= 30) {
            flags.on(ObjectFlag.OF_PROT_FEAR);
        }
    }

    /**
     * Works out the player's maximum mana, and whether their armour is heavy enough to cost them
     * some — the port of C's {@code calc_mana} ({@code player-calcs.c:1480-1554}).
     *
     * <p>Three questions in order. Can the class cast at all: a class with no spells has its mana
     * zeroed outright and the method returns. What does level give: mana grows with the levels
     * gained <em>since</em> the class's first spell level, scaled by a table indexed on the average
     * of the governing stats, so a caster below that level gets nothing. What does armour take
     * away: everything worn except weapon, launcher, rings, amulet and light is weighed, and each
     * ten tenth-pounds above the class's allowance costs a point and raises the encumbrance flag.
     *
     * <p>Writes to two places, and the split follows {@code update}. The encumbrance flag always
     * goes to the state, because it describes the calculation. The maximum itself goes to the
     * <em>player</em>, not the state, and only when {@code update} is set — so a hypothetical
     * recalculation leaves the character's mana alone. When the maximum does change, current mana is
     * capped to it and a redraw is asked for.
     *
     * <p>Function calcMana commented in full on 260820.
     *
     * @param state  the state being filled; receives the armour-encumbrance flag
     * @param update {@code true} to store the new maximum on the player, {@code false} to compute
     *               and discard it
     */
    public void calcMana(PlayerState state, boolean update) {
        // Must know spells
        if (getPlayerClass().getMagic().getTotalSpells() == 0) {
            maxSP = 0;
            curSp = 0;
            cspFrac = 0;
            return;
        }

        int tempMaxSP;

        // Extract effective player level
        int levels = (level - getPlayerClass().getMagic().getSpellFirst()) + 1;
        if (levels > 0) {
            tempMaxSP = 1;
            tempMaxSP += StatTables.adjMagMana[averageSpellStat(state)] * levels / 100;
        } else {
            tempMaxSP = 0;
        }

        // Assume not encumbered by armour
        state.setCumberArmour(false);

        // weigh the armour
        int currentWeight = 0;
        for (EquipSlot slot : body.getSlots()) {
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_WEAPON) continue;
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_BOW) continue;
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_RING) continue;
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_AMULET) continue;
            if (slot.getType() == EquipmentSlotsEnum.EQUIP_LIGHT) continue;

            ItemObject item = slot.getItem();

            if (item != null)
                currentWeight += item.weightOne();
        }

        // determine max weight allowance
        int maxWeight = getPlayerClass().getMagic().getSpellWeight();

        // Heavy armour penalises mana
        if (((currentWeight - maxWeight) / 10) > 0) {
            // Encumbered
            state.setCumberArmour(true);

            // reduce mana
            tempMaxSP -= ((currentWeight - maxWeight) / 10);
        }

        // Non-negative
        tempMaxSP = Math.max(tempMaxSP, 0);

        // if no updates, return
        if (!update) return;

        if (maxSP != tempMaxSP) {
            maxSP = tempMaxSP;

            // enforce new limits
            if (curSp >= maxSP) {
                curSp = maxSP;
                cspFrac = 0;
            }

            // Display mana at next draw
            getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_MANA);
        }
    }

    /**
     * The stat table index a caster's mana is scaled by, averaged over every realm the class draws
     * on — the port of C's {@code average_spell_stat}
     * ({@code player-calcs.c:1247-1259}).
     *
     * <p>Averages the compressed <em>indices</em>, not the stat values, because that is what indexes
     * the mana table. A class casting from one realm gets that realm's stat unchanged; a class
     * spanning two is held to the mean of both, so neglecting either costs mana. The division rounds
     * up.
     *
     * <p>Function averageSpellStat commented in full on 260820.
     *
     * @param state the state whose stat indices to read
     * @return the averaged stat index
     * @throws ArithmeticException if the class has no realms — the caller must establish that it
     *                             casts before asking, as {@code calcMana}'s literacy test does
     */
    private int averageSpellStat(PlayerState state) {
        Set<MagicRealm> realms = getPlayerClass().magicRealm();
        int numRealms = realms.size();
        int total = 0;
        for (MagicRealm realm : realms) {
            total += state.getStatInd(realm.getStat());
        }

        return (total + numRealms - 1) / numRealms;
    }

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
     */
    public void embody() {
        if (race == null)
            return;

        body = race.getBody().copy();
    }

    /**
     * Blows per turn with a given weapon, scaled by 100 — the port of C's {@code calc_blows}
     * ({@code player-calcs.c:1703-1735}).
     *
     * <p>Strength is weighed against the weapon: {@code adjStrBlow[STR]} times the class's attack
     * multiplier, divided by the weapon's weight. A heavy weapon in a weak arm lands on a low rung,
     * and the class's minimum weight is a floor on the divisor — a weapon lighter than that is
     * treated as if it weighed that much, which both stops the division by zero for a weightless
     * weapon and stops a class with a high minimum profiting endlessly from daggers. Dexterity gives
     * the second subscript; both saturate at 11.
     *
     * <p>The table holds energy per blow, so the count is 10000 divided by it, capped at the class's
     * maximum attacks. Extra blows from equipment are added <em>after</em> that cap, so a modifier
     * can carry a character past the class ceiling where strength and dexterity alone cannot.
     *
     * <p>The floor at the end is one blow, or two under the percentage-damage birth option, where
     * blows are worth proportionally less.
     *
     * <p>Function calcBlows commented in full on 260820.
     *
     * @param item       the weapon, or {@code null} for unarmed — which weighs nothing and so gets
     *                   the class minimum as its divisor
     * @param state      the state whose strength and dexterity indices to read
     * @param extraBlows extra blows gathered from equipment, shape and statuses
     * @return blows per turn, multiplied by 100
     */
    private int calcBlows(ItemObject item, PlayerState state, int extraBlows) {
        int weight = (item == null) ? 0 : item.weightOne();
        int minWeight = playerClass.getMinWeight();

        // Enforce a 1/10 pound minimum weight
        int divisor = Math.max(weight, minWeight);

        // Get the strength v weight
        int strIndex = StatTables.adjStrBlow[state.getStatInd(Stats.STAT_STR)]
                * playerClass.getAttMultiply() / divisor;

        // Maximal value
        if (strIndex > 11) strIndex = 11;

        // Dexterity
        int dexIndex = Math.min(StatTables.adjDexBlow[state.getStatInd(Stats.STAT_DEX)], 11);

        // Energy per blow
        int blowEnergy = StatTables.blowsTable[strIndex][dexIndex];

        int blows = Math.min((10000 / blowEnergy), (100 * playerClass.getMaxAttacks()));

        return Math.max(blows + (100 * extraBlows),
                getPlayerOptions().has(PlayerOptionEnum.OP_birth_percent_damage) ? 200 : 100);
    }

    /**
     * Adds the player's current shape's contribution to the state — the port of C's
     * {@code calc_shapechange} ({@code player-calcs.c:1798-1853}).
     *
     * <p>A shape contributes on the same terms as a piece of equipment: combat bonuses, skills,
     * object and player flags, stats, the seven other modifiers, and resistances. It is applied
     * after the equipment walk and before vulnerabilities are settled, so a shape's resistance is
     * weighed against the gear's on equal footing and its vulnerability is remembered for later
     * alongside everything else's.
     *
     * <p>Two departures from C, both forced by the port's shapes:
     *
     * <ul>
     *   <li><b>A null shape returns the totals untouched.</b> C cannot reach this state — a player
     *       always has a shape, "normal", assigned at birth and restored on changing back
     *       ({@code player-birth.c:456}, {@code player-util.c:1050}) — but the port allows the field
     *       to be absent, and an absent shape must contribute nothing rather than throw.</li>
     *   <li><b>The extra blows and shots travel by value.</b> C passes four pointers and adds into
     *       the caller's storage; see {@link Extras}.</li>
     * </ul>
     *
     * <p>Function calcShapechange commented in full on 260820.
     *
     * @param state           the state being filled, mutated in place
     * @param vulnerabilities the running set of elements something has made the player vulnerable
     *                        to, added to if the shape carries a vulnerability
     * @param shape           the player's current shape, or {@code null} if none is set
     * @param incoming        the blow, shot, might and move totals gathered so far
     * @return the same four totals with the shape's contribution added
     */
    private Extras calcShapechange(PlayerState state,
                                   Map<ElementEnum, Boolean> vulnerabilities,
                                   PlayerShape shape,
                                   Extras incoming) {
        // If shape == null, not shape changed
        if (shape == null) return incoming;

        // Combat stats
        state.toAcAdd(shape.getToAc());
        state.toHitAdd(shape.getToHit());
        state.toDamAdd(shape.getToDam());

        // Skills
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            state.skillAdd(skill, shape.getSkills().getOrDefault(skill, 0));
        }

        // Object flags
        state.unionObjectFlags(shape.getFlags());

        // Player flags
        state.unionPlayerFlags(shape.getPflags());

        // Stats
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            state.statAdd(stat, shape.getModifier(stat));
        }

        // Other modifiers
        state.skillAdd(PlayerSkill.SKILL_STEALTH, shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_STEALTH, 0));
        state.skillAdd(PlayerSkill.SKILL_SEARCH, shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_SEARCH, 0) * 5);
        state.infraAdd(shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_INFRA, 0));
        state.skillAdd(PlayerSkill.SKILL_DIGGING,
                shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_TUNNEL, 0) * 20);
        state.setSpeed(state.getSpeed() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_SPEED, 0));
        state.setDamRed(state.getDamRed() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_DAM_RED, 0));

        int extraBlows = incoming.blows() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0);
        int extraShots = incoming.shots() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_SHOTS, 0);
        int extraMight = incoming.might() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_MIGHT, 0);
        int extraMoves = incoming.moves() + shape.getObjectValueModifiers().getOrDefault(ObjectModifier.OM_MOVES, 0);

        // Resists and vulnerabilities
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            ElementInfo elInfo = shape.getElementValueModifiers().getOrDefault(element, null);
            if (elInfo != null && elInfo.getResLevel() == -1) {
                vulnerabilities.put(element, true);
            } else if (elInfo != null && elInfo.getResLevel() > state.getResLevel(element)) {
                state.setResLevel(element, elInfo.getResLevel());
            }
        }

        return new Extras(extraBlows, extraShots, extraMight, extraMoves);
    }

    /**
     * Works out the radius of light the player sheds — the port of C's {@code calc_light}
     * ({@code player-calcs.c:1598-1646}).
     *
     * <p>Sums rather than picks: every worn item's contribution is added, so a lantern and a glowing
     * ring both count. An item's contribution is its innate radius from {@code OF_LIGHT_2} or
     * {@code OF_LIGHT_3} plus its light modifier, with two adjustments. A player with
     * {@code PF_UNLIGHT} loses a point from any positive light modifier, which lets them carry
     * lightly-glowing gear without spoiling the dark they depend on. And a fuelled light source that
     * has burnt out contributes nothing at all — its whole contribution is zeroed, not just its
     * innate part.
     *
     * <p>In the town by day the answer is simply zero and the method returns early, but not before
     * checking whether that differs from the player's current light and asking for a redraw if so —
     * which is why the early return is inside the {@code update} branch rather than around it.
     *
     * <p>Function calcLight commented in full on 260820.
     *
     * @param state  the state to write the light radius to
     * @param update {@code true} if this is a real recalculation, which may ask the display to
     *               refresh
     */
    public void calcLight(PlayerState state, boolean update) {
        state.setCurLight(0);

        // Is it day in the town
        if (depth == 0 && GameWorld.isDaytime() && update) {
            if (this.state != null && this.state.getCurLight() != state.getCurLight()) {
                getPlayerUpkeep().updateFlagOn(PlayerUpdateEnum.PU_MONSTERS);
                getPlayerUpkeep().updateFlagOn(PlayerUpdateEnum.PU_UPDATE_VIEW);
            }
            return;
        }

        // Get brightest of wielded objects
        for (EquipSlot slot : body.getSlots()) {
            ItemObject item = slot.getItem();
            if (item == null) continue;
            int amount = 0;

            // does item have light radius?
            if (item.hasFlag(ObjectFlag.OF_LIGHT_2)) {
                amount = 2;
            } else if (item.hasFlag(ObjectFlag.OF_LIGHT_3)) {
                amount = 3;
            }
            amount += item.getModifierValue(ObjectModifier.OM_LIGHT);

            // Adjustment to allow UNLIGHT players to use +1 LIGHT gear
            if (item.getModifierValue(ObjectModifier.OM_LIGHT) > 0 && state.hasPFlag(PlayerFlag.PF_UNLIGHT)) {
                amount--;
            }

            if (item.gettValue() != null && item.gettValue().isLight()
                    && !item.hasFlag(ObjectFlag.OF_NO_FUEL)
                    && item.getTimeout() == 0) {
                // Items without fuel yield no light
                amount = 0;
            }

            state.setCurLight(state.getCurLight() + amount);
        }
    }

    /**
     * Scales a skill by a fraction of itself — the port of C's {@code adjust_skill_scale}
     * ({@code player-calcs.c:1781-1792}), the way every temporary skill penalty and bonus in
     * {@code calcBonuses} is applied.
     *
     * <p>Proportional rather than flat: blessing improves device skill by a twentieth of what it
     * already is, so it is worth more to a character who is already good. The adjustment is computed
     * from the magnitude of the value, so a negative skill is scaled by the same amount a positive
     * one would be rather than moving the other way, and {@code minValue} sets a floor on that
     * magnitude so a skill of zero can still be adjusted.
     *
     * <p><b>A negative numerator is not simply the positive case with the sign flipped.</b> The
     * subtraction rounds <em>up</em> — the {@code + denominator - 1} — so that the result matches
     * what {@code value * (denominator + numerator) / denominator} would give for a positive value.
     * Truncating instead would make a penalty slightly gentler than the equivalent multiplication,
     * and the two idioms are used interchangeably in the original.
     *
     * <p>Function adjustSkillScale commented in full on 260820.
     *
     * @param value       the skill value to adjust
     * @param numerator   the fraction's numerator; negative for a penalty
     * @param denominator the fraction's denominator
     * @param minValue    a floor on the magnitude the fraction is taken of, so that a small or zero
     *                    skill still moves
     * @return the adjusted skill value
     */
    public int adjustSkillScale(int value, int numerator, int denominator, int minValue) {
        if (numerator >= 0) {
            int add = Math.max(minValue, Math.abs(value)) * numerator / denominator;
            return value + add;
        }
        int sub = ((Math.max(minValue, Math.abs(value)) * -numerator) + denominator - 1) / denominator;
        return value - sub;
    }

    /**
     * Recalculates the character's maximum hit points and clamps the current total to it — the port
     * of C's {@code calc_hitpoints} ({@code player-calcs.c:1562-1588}).
     *
     * <p>Two numbers make the maximum. {@code playerHP[level - 1]} is the running total of the hit
     * dice rolled at each level, fixed at birth and never re-rolled, and {@code adjConMhp} adds a
     * bonus for constitution expressed in hundredths of a hit point per level — so the table's 250
     * at 18/40 is two and a half hit points for every level the character has. The bonus is negative
     * for poor constitution, and the division truncates toward zero in both languages, so a penalty
     * is rounded the same way a bonus is rather than one point harsher.
     *
     * <p>C declares {@code bonus} as {@code long}; an {@code int} carries it here because the widest
     * product the table can reach is 1250 × 50, nowhere near overflow.
     *
     * <p><b>Everything is guarded on the maximum having actually changed.</b> Most calls do not move
     * it, and the body must not run for those: it would clamp and repaint on every recalculation.
     * Inside the guard the clamp is {@code >=}, not {@code >}, so a character already sitting exactly
     * on the new maximum has {@link #chpFrac} cleared as well. That matters because the fraction is
     * sub-hitpoint regeneration credit — leaving a stale one behind hands out a free hit point at
     * the next tick.
     *
     * <p>{@link PlayerRedraw#PR_HP} is raised rather than the display being touched, in keeping with
     * the rest of the calculation: this runs in the game half and the UI repaints when it is told.
     *
     * <p>The constitution read is {@code state}'s <em>derived</em> stat index, so this must run after
     * {@code calcBonuses} has filled it. C guarantees that by ordering the flags in
     * {@code update_stuff}, where {@code PU_BONUS} is handled before {@code PU_HP}
     * ({@code player-calcs.c:2575-2588}).
     *
     * <p>Function calcHitpoints commented in full on 260820.
     */
    public void calcHitpoints() {
        // 1/100th hitpoint bonus per level
        int bonus = StatTables.adjConMhp[state.getStatInd(Stats.STAT_CON)];

        // Calculate max hp
        int mhp = playerHP[level - 1] + (bonus * level / 100);

        // Always have 1 hp per level
        if (mhp < level + 1) mhp = level + 1;

        // New maximum hitpoints
        if (this.maxHP != mhp) {
            // save the new limit
            this.maxHP = mhp;

            // enforce new limit
            if (this.currentHP >= mhp) {
                this.currentHP = mhp;
                this.chpFrac = 0;
            }

            // Prepare to display the hitpoints
            getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_HP);
        }
    }

    /**
     * The four running totals {@code calcBonuses} accumulates across the equipment walk and then
     * hands to {@code calcShapechange} to add to — extra blows, shots, shooting might and movement
     * actions.
     *
     * <p>Exists only because of a difference in how the two languages pass things. C declares four
     * {@code int}s and passes their addresses ({@code player-calcs.c:2030-2031}), so
     * {@code calc_shapechange} adds into the caller's own storage. The port cannot take an address,
     * so the four travel together as a value in and a value out, and the caller assigns the result
     * back over its locals.
     *
     * @param blows extra blows per turn, unscaled
     * @param shots extra shots per turn, unscaled
     * @param might extra shooting multiplier
     * @param moves extra movement actions per turn
     */
    private record Extras(int blows, int shots, int might, int moves) {
    }
}