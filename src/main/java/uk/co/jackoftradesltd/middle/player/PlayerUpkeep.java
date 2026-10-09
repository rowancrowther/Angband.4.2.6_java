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

import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.player.enums.PlayerNotice;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;

import java.util.List;

/**
 * The player's transient runtime bookkeeping — the recomputed-each-session state that is
 * <em>not</em> part of the saved character, as opposed to the persistent data held on
 * {@link Player}.
 *
 * <p>Ports the C {@code struct player_upkeep} ({@code player.h}). In the original this carries
 * the pending notice ({@code PN_*}), update ({@code PU_*}) and redraw ({@code PR_*}) flag sets,
 * the current trackees (health-bar target, recalled monster race, examined object and kind),
 * inventory and quiver contents and counts, the resting/running/pathfinding counters, and the
 * level-generation requests. All of it is volatile — rebuilt rather than serialized — which is
 * exactly what lets the engine discard the upkeep and recompute it on load.
 *
 * <p><b>Status:</b> every field of the C struct is modelled. Accessors are being added as
 * callers need them. Ten fields have no accessor at all yet — {@code newSpells},
 * {@code objectKind}, {@code createUpStair}, {@code createDownStair}, {@code lightLevel},
 * {@code runningFirstStep}, {@code rechargePower}, {@code stepCount}, {@code steps} and
 * {@code pathDestination} — and several more are readable but not writable, or the reverse
 * ({@code autosave}, {@code onlyPartial}, {@code monsterRace}, {@code runningCounter}).
 *
 * <p><b>The status-cache writes are temporary.</b> {@link #setHealthWho} and
 * {@link #setRestingCounter} push values into the static {@link PlayerEventStatusUpdate} cache
 * as well as writing the field. C does nothing of the kind: its UI reads the fields when it
 * redraws. The port is moving to passing these values through to the UI as part of the messages
 * it is sent, to be used at display time, so both writes are going to be replaced. The field
 * writes themselves are the part that matches C and will stay.
 *
 * <p>Class PlayerUpkeep coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class PlayerUpkeep {
    /**
     * True while a game is actually in progress — the turn loop's master condition ({@code playing}).
     *
     * <p>Field playing coded before 261009, commented in full on 261009.
     */
    private boolean playing;

    /**
     * True when an autosave is pending ({@code autosave}).
     *
     * <p>Field autosave coded before 261009, commented in full on 261009.
     */
    private boolean autosave;

    /**
     * True when the current level needs regenerating ({@code generate_level}).
     *
     * <p>Field generateLevel coded before 261009, commented in full on 261009.
     */
    private boolean generateLevel;

    /**
     * True when only partial updates are needed ({@code only_partial}).
     *
     * <p>Field onlyPartial coded before 261009, commented in full on 261009.
     */
    private boolean onlyPartial;

    /**
     * True while an auto-drop is in progress ({@code dropping}).
     *
     * <p>Field dropping coded before 261009, commented in full on 261009.
     */
    private boolean dropping;

    /**
     * Energy spent this turn; the loop reads it to tell whether a turn was actually taken ({@code energy_use}).
     *
     * <p>Field energyUse coded before 261009, commented in full on 261009.
     */
    private int energyUse;

    /**
     * Number of spells currently available to learn ({@code new_spells}).
     *
     * <p>Field newSpells coded before 261009, commented in full on 261009.
     */
    private int newSpells;

    /**
     * The monster shown on the health bar — the health-bar trackee ({@code health_who}). Null
     * means nothing is tracked and the bar is blank. Written through {@link #setHealthWho} or
     * {@link #healthTrack}.
     *
     * <p>Field healthWho coded before 261009, commented in full on 261009.
     */
    private Monster healthWho;

    /**
     * The monster race currently being recalled — the race trackee ({@code monster_race}).
     *
     * <p>Field monsterRace coded before 261009, commented in full on 261009.
     */
    private MonsterRace monsterRace;

    /**
     * The object currently being examined — the object trackee ({@code object}).
     *
     * <p>Field object coded before 261009, commented in full on 261009.
     */
    private ItemObject object;

    /**
     * The object kind currently being examined — the kind trackee ({@code object_kind}).
     *
     * <p>Field objectKind coded before 261009, commented in full on 261009.
     */
    private ObjectKind objectKind;

    /**
     * Pending one-off housekeeping actions such as combining the pack or applying ignore rules
     * ({@code notice}). C holds the {@code PN_*} bits in a {@code uint32_t}; the port holds them in
     * a {@link Flag}, so "any pending" is an emptiness test rather than a comparison with zero.
     *
     * <p>Field noticeFlags coded before 261009, commented in full on 261009.
     */
    private Flag<PlayerNotice> noticeFlags = new Flag<>(PlayerNotice.class);

    /**
     * Derived quantities (HP, mana, view, …) that have gone stale and must be recomputed
     * ({@code update}). C holds the {@code PU_*} bits in a {@code uint32_t}; the port holds them in
     * a {@link Flag}, so "any pending" is an emptiness test rather than a comparison with zero.
     *
     * <p>Field updateFlags coded before 261009, commented in full on 261009.
     */
    private Flag<PlayerUpdateEnum> updateFlags = new Flag<>(PlayerUpdateEnum.class);

    /**
     * Parts of the screen that have changed and need repainting by the UI ({@code redraw}). C holds
     * the {@code PR_*} bits in a {@code uint32_t}; the port holds them in a {@link Flag}. Unlike
     * the other two sets this one is never handed out live: {@link #getRedrawFlags} returns a
     * copy.
     *
     * <p>Field redrawFlags coded before 261009, commented in full on 261009.
     */
    private Flag<PlayerRedraw> redrawFlags = new Flag<>(PlayerRedraw.class);

    /**
     * Used by the UI to decide whether to start off showing equipment or
     * inventory listings when offering a choice ({@code command_wrk}, see {@code obj-ui.c}). The
     * snake-case name is C's, kept so the field is recognizable.
     *
     * <p>Field command_wrk coded before 261009, commented in full on 261009.
     */
    private int command_wrk;

    /**
     * Create an up staircase on the next level generated ({@code create_up_stair}).
     *
     * <p>Field createUpStair coded before 261009, commented in full on 261009.
     */
    private boolean createUpStair;

    /**
     * Create a down staircase on the next level generated ({@code create_down_stair}).
     *
     * <p>Field createDownStair coded before 261009, commented in full on 261009.
     */
    private boolean createDownStair;

    /**
     * The next level is to be fully lit on creation ({@code light_level}).
     *
     * <p>Field lightLevel coded before 261009, commented in full on 261009.
     */
    private boolean lightLevel;

    /**
     * The current level is an arena ({@code arena_level}).
     *
     * <p>Field arenaLevel coded before 261009, commented in full on 261009.
     */
    private boolean arenaLevel;

    /**
     * Resting counter: turns of rest remaining ({@code resting}). Zero means not resting, a
     * positive value is a count of turns, and the negative values are the special rest codes
     * ({@code REST_ALL_POINTS}, {@code REST_COMPLETE}, {@code REST_SOME_POINTS} in
     * {@code player-util.h}). C clamps it to 9999 in {@code player_resting_set_count}; this field
     * does no clamping of its own.
     *
     * <p>Field restingCounter coded before 261009, commented in full on 261009.
     */
    private int restingCounter;

    /**
     * Running counter: steps still to be taken in an in-progress run ({@code running}); zero when
     * the player is not running. {@code player-path.c} counts it down as the run proceeds, and the
     * pathfinding commands in {@code cmd-cave.c} start it from {@code step_count}.
     *
     * <p>Field runningCounter coded before 261009, commented in full on 261009.
     */
    private int runningCounter;

    /**
     * True if this is the first step of a run rather than following a precomputed path ({@code running_firststep}).
     *
     * <p>Field runningFirstStep coded before 261009, commented in full on 261009.
     */
    private boolean runningFirstStep;

    /**
     * The objects held in the quiver ({@code quiver}) - one slot per quiver position, sized to
     * {@code z_info->quiver_size} by the constructor and replaceable wholesale through
     * {@link #setQuiverObjects}.
     *
     * <p>Field quiverObjects coded before 261009, commented in full on 261009.
     */
    private ItemObject[] quiverObjects;

    /**
     * The objects held in the pack ({@code inven}) - one slot per pack position, sized to
     * {@code z_info->pack_size + 1} by the constructor (the extra slot is C's, kept) and
     * replaceable wholesale through {@link #setInventory}.
     *
     * <p>Field inventoryObjects coded before 261009, commented in full on 261009.
     */
    private ItemObject[] inventoryObjects;

    /**
     * Total weight currently carried, in tenth-pounds ({@code total_weight}) - pack, quiver and
     * worn gear together.
     *
     * <p>Field totalWeight coded before 261009, commented in full on 261009.
     */
    private int totalWeight;

    /**
     * Number of items in the inventory ({@code inven_cnt}).
     *
     * <p>Field inventoryCount coded before 261009, commented in full on 261009.
     */
    private int inventoryCount;

    /**
     * Number of items in the equipment ({@code equip_cnt}).
     *
     * <p>Field equipmentCount coded before 261009, commented in full on 261009.
     */
    private int equipmentCount;

    /**
     * Number of items in the quiver ({@code quiver_cnt}).
     *
     * <p>Field quiverCount coded before 261009, commented in full on 261009.
     */
    private int quiverCount;

    /**
     * Power of the recharge effect in progress ({@code recharge_pow}).
     *
     * <p>Field rechargePower coded before 261009, commented in full on 261009.
     */
    private int rechargePower;

    /**
     * Pathfinding: number of steps left to walk ({@code step_count}).
     *
     * <p>Field stepCount coded before 261009, commented in full on 261009.
     */
    private int stepCount;

    /**
     * Pathfinding: the queued steps, in reverse order ({@code steps}, an {@code int16_t *} in C).
     * Null means no walk is queued; the pathfinding commands in {@code cmd-cave.c} assert it is
     * null before starting one, so an empty list is deliberately not the same thing (see the
     * constructor).
     *
     * <p>Field steps coded before 261009, commented in full on 261009.
     */
    private List<Integer> steps;

    /**
     * Pathfinding: the destination grid being walked to ({@code path_dest}). Never null - a zeroed
     * C {@code loc} is the origin grid, so the constructor uses {@link Loc#zero}.
     *
     * <p>Field pathDestination coded before 261009, commented in full on 261009.
     */
    private Loc pathDestination;

    /**
     * Builds an empty upkeep, the port of the two-part setup C performs in {@code init_player}
     * ({@code player.c}).
     *
     * <p><b>The two blocks below are not "C's fields" and "Java's fields" — every field here is
     * C's.</b> The split is between the two things C actually does. {@code p->upkeep} arrives from
     * {@code mem_zalloc}, so the whole struct is zero before anything else runs; only {@code inven}
     * and {@code quiver} then get an explicit allocation of their own, because they are arrays of
     * pointers rather than scalars. The first block is that pair of allocations. The second is C's
     * zeroing written out by hand, which Java needs no more than C does — the field defaults are
     * already null and false — but which is kept because it states the starting position of each
     * field where the reader can see it.
     *
     * <p>Two entries are worth reading closely rather than skimming as more zeroing, because they
     * pull in opposite directions and the difference is deliberate.
     *
     * <p>{@code pathDestination} is set to {@link Loc#zero} rather than left null, because C's
     * zeroed {@code loc} struct <em>is</em> the origin grid — there is no such thing as an absent
     * {@code loc} in C, and a null here would invent a state the original cannot express.
     *
     * <p>{@code steps} is set to null rather than an empty list, because in C the null is
     * load-bearing. The pathfinder opens every entry point with
     * {@code assert(!player->upkeep->steps)} ({@code cmd-cave.c}, in each of the pathfinding
     * commands) to catch a walk being started while another is still queued. An empty list would satisfy
     * that test without meaning what it means, silently retiring a check C relies on; a null keeps
     * "no walk in progress" distinguishable from "a walk with nothing left in it". The port has no
     * pathfinder yet, so nothing reads this — the point is that the distinction is still available
     * when one arrives.
     *
     * <p>{@code playing} being false is the state {@link uk.co.jackoftradesltd.middle.player.Player}
     * starts in before birth completes, and several methods gate their messages on it.
     *
     * <p>The one Java-only line is {@code objectPile = null}, for the field C does not have. Another
     * side effect C does not have: {@code setHealthWho(null)} writes {@code monsterTracked=false}
     * into the static {@link PlayerEventStatusUpdate} cache, so building any extra upkeep clears
     * the live tracked flag. That is part of the cache write being replaced (see the class
     * comment), not something to preserve.
     *
     * <p>Constructor PlayerUpkeep commented in full on 260816, {@code steps} changed from an empty
     * list to null the same day, line-number citations removed and cache side effect recorded on
     * 261009.
     */
    public PlayerUpkeep() {
        // C allocates these two explicitly; the rest of the struct is covered by mem_zalloc
        inventoryObjects = new ItemObject[GameConstants.getCarryCapPackSize() + 1];
        quiverObjects = new ItemObject[GameConstants.getCarryCapQuiverSize()];

        // C's mem_zalloc, written out by hand
        setHealthWho(null);
        monsterRace = null;
        object = null;
        objectKind = null;
        pathDestination = Loc.zero;
        playing = false;
        steps = null;
    }

    /**
     * Sets the UI's equipment-vs-inventory listing preference - the port of writing C's
     * {@code upkeep->command_wrk}. A plain assignment in both languages; the UI sets it before it
     * offers an item choice so the listing opens on the right half.
     *
     * <p>Function setCommand_wrk coded before 261009, commented in full on 261009.
     *
     * @param command_wrk the preference value (see {@code obj-ui.c} in the original)
     */
    public void setCommand_wrk(int command_wrk) {
        this.command_wrk = command_wrk;
    }

    /**
     * Returns the UI's equipment-vs-inventory listing preference - the port of reading C's
     * {@code upkeep->command_wrk}.
     *
     * <p>Function getCommand_wrk coded before 261009, commented in full on 261009.
     *
     * @return the UI's equipment-vs-inventory listing preference
     */
    public int getCommand_wrk() {
        return command_wrk;
    }

    /**
     * Returns a snapshot of the parts of the screen currently waiting to be repainted — the port
     * of C's {@code uint32_t redraw = p->upkeep->redraw;} at the head of {@code redraw_stuff}
     * ({@code player-calcs.c}).
     *
     * <p><b>The copy is mutable on purpose, and that is not the leak it looks like.</b> It belongs
     * to the caller: the live set stays private and is unreachable from here, so anything done to
     * the returned object is done to the caller's own working value. C relies on exactly that —
     * {@code redraw_stuff} narrows its local copy to {@code PR_SUBWINDOW} when the map is not on
     * screen while leaving the pending set on the player untouched, so the flags
     * it skipped are still waiting the next time round. A read-only view could not express that
     * step, and a live reference would corrupt the pending set while taking it.
     *
     * <p>A snapshot rather than a view for the same reason. The set goes on changing while a
     * repaint runs — anything the redraw itself disturbs raises another flag — and the pass needs
     * to work from what was pending when it started, not from a total that moves under it.
     *
     * <p>Pair with {@link #clearRedrawFlags} once the work is done; between them they are C's
     * take-narrow-act-clear sequence. Raising a single flag is {@link #setRedrawFlagsOn}, which
     * needs no copy.
     *
     * <p>Function getRedrawFlags commented in full on 260816, return type and rationale revised on
     * 260818 when the differencing clear arrived, line-number citations removed on 261009.
     *
     * @return a snapshot, owned by the caller, of the current {@code PR_*} redraw flags
     */
    public Flag<PlayerRedraw> getRedrawFlags() {
        Flag<PlayerRedraw> copyFlags = new Flag<>(PlayerRedraw.class);
        copyFlags.copyFrom(redrawFlags);
        return copyFlags;
    }

    /**
     * Clears the redraw flags that have now been dealt with, leaving the rest pending — the port
     * of C's {@code p->upkeep->redraw &= ~redraw;} near the end of {@code redraw_stuff}
     * ({@code player-calcs.c}).
     *
     * <p><b>A difference, not a wipe, and the distinction earns its keep.</b> Only the flags named
     * in {@code handled} are cleared. Two kinds of flag therefore survive the call: one raised
     * <em>during</em> the repaint by the work the repaint itself did, and one the caller
     * deliberately narrowed out of its snapshot because it could not be serviced yet. Clearing the
     * whole set instead would silently drop both, and the screen would be left stale with nothing
     * recording that it was.
     *
     * <p>{@code handled} is read and never written, hence the {@link FlagView}. It is normally the
     * snapshot from {@link #getRedrawFlags} after the caller has narrowed it, but nothing requires
     * that — any set of flags will do.
     *
     * <p>The {@code boolean} has no counterpart in C, whose {@code &=} answers nothing. It falls
     * out of {@link uk.co.jackoftradesltd.channel.utils.Flag#diff} and is there for a caller that
     * wants it; the redraw pass has no use for it.
     *
     * <p>Function clearRedrawFlags coded on 260818, commented in full on 260818, line-number
     * citation removed on 261009.
     *
     * @param handled the flags whose repaint has been carried out, left unmodified
     * @return {@code true} if any flag was actually cleared
     */
    public boolean clearRedrawFlags(FlagView<PlayerRedraw> handled) {
        return redrawFlags.diff(handled);
    }

    /**
     * Raises a redraw flag, marking that part of the screen as needing a repaint - the port of C's
     * {@code p->upkeep->redraw |= PR_...}. Adds without disturbing the flags already raised.
     *
     * <p>The return value is the port's own addition - C's {@code |=} yields nothing a caller
     * reads - and is ignored at almost every call site.
     *
     * <p>Function setRedrawFlagsOn coded before 261009, commented in full on 261009.
     *
     * @param flag the {@code PR_*} flag to set
     * @return {@code true} if the flag was previously clear (i.e. this call changed it)
     */
    public boolean setRedrawFlagsOn(PlayerRedraw flag) {
        return redrawFlags.on(flag);
    }

    /**
     * Clears a redraw flag, once that part of the screen has been repainted - the port of C's
     * {@code p->upkeep->redraw &= ~PR_...}. A single-flag counterpart of
     * {@link #clearRedrawFlags}, which clears a whole set at once.
     *
     * <p>The return value is the port's own addition; C's {@code &=} yields nothing.
     *
     * <p>Function setRedrawFlagsOff coded before 261009, commented in full on 261009.
     *
     * @param flag the {@code PR_*} flag to clear
     * @return {@code true} if the flag was previously set (i.e. this call changed it)
     */
    public boolean setRedrawFlagsOff(PlayerRedraw flag) {
        return redrawFlags.off(flag);
    }

    /**
     * Reports whether a game is actually in progress - the port of reading C's
     * {@code upkeep->playing}, the turn loop's master condition.
     *
     * <p>Function isPlaying coded before 261009, commented in full on 261009.
     *
     * @return {@code true} while a game is actually in progress
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Reports whether the player has spent energy this turn, i.e. whether a turn was actually taken -
     * the port of testing C's {@code upkeep->energy_use}.
     *
     * <p>Function energyUse coded before 261009, commented in full on 261009.
     *
     * @return {@code true} if energy was used this turn
     */
    public boolean energyUse() {
        return energyUse != 0;
    }

    /**
     * Records the energy spent by the current command - the port of writing C's
     * {@code upkeep->energy_use}. The player-processing pass sets it to {@code 0} to assume a free
     * turn, and a command that acts writes its cost here so the loop can tell a turn was taken.
     *
     * <p>Function setEnergyUse coded before 261009, commented in full on 261009.
     *
     * @param energyUse the energy the current command used ({@code 0} for a free turn)
     */
    public void setEnergyUse(int energyUse) {
        this.energyUse = energyUse;
    }

    /**
     * Returns the energy spent by the current command - the port of reading C's
     * {@code upkeep->energy_use}. Unlike {@link #energyUse()}, which reports only whether any energy
     * was used, this returns the actual amount, for the loop to deduct from the player's energy.
     *
     * <p>Function getEnergyUse coded before 261009, commented in full on 261009.
     *
     * @return the energy spent by the current command
     */
    public int getEnergyUse() {
        return energyUse;
    }

    /**
     * Reports whether an auto-drop is in progress - the port of reading C's
     * {@code upkeep->dropping}. During an auto-drop the per-turn cleanup skips its monster-refresh
     * work, since the map is about to be redrawn anyway.
     *
     * <p>Function getDropping coded before 261009, commented in full on 261009.
     *
     * @return {@code true} while an auto-drop is in progress
     */
    public boolean getDropping() {
        return dropping;
    }

    /**
     * Sets (or clears) the auto-drop-in-progress flag - the port of writing C's
     * {@code upkeep->dropping}. The per-turn cleanup clears it once the drop has been handled.
     *
     * <p>Function setDropping coded before 261009, commented in full on 261009.
     *
     * @param dropping {@code true} while stuff is being auto-dropped
     */
    public void setDropping(boolean dropping) {
        this.dropping = dropping;
    }

    /**
     * Reports whether the current level needs regenerating - the port of reading C's
     * {@code upkeep->generate_level}. The name is C's field name rather than an {@code isX}
     * getter, so read it as "is a new level wanted".
     *
     * <p>Function generateLevel coded before 261009, commented in full on 261009.
     *
     * @return {@code true} when the current level needs regenerating
     */
    public boolean generateLevel() {
        return generateLevel;
    }

    /**
     * Requests (or clears the request for) regenerating the current level - the port of writing C's
     * {@code upkeep->generate_level}. The game loop honours this on its next pass, building a fresh
     * level via {@link uk.co.jackoftradesltd.middle.cave.Generate#prepareNextLevel} and clearing the
     * flag.
     *
     * <p>Function setGenerateLevel coded before 261009, commented in full on 261009.
     *
     * @param generateLevel {@code true} to request a new level
     */
    public void setGenerateLevel(boolean generateLevel) {
        this.generateLevel = generateLevel;
    }

    /**
     * Reports whether the current level is an arena - the port of reading C's
     * {@code upkeep->arena_level}.
     *
     * <p>Function isArenaLevel coded before 261009, commented in full on 261009.
     *
     * @return {@code true} when the current level is an arena
     */
    public boolean isArenaLevel() {
        return arenaLevel;
    }

    /**
     * Marks (or unmarks) the current level as an arena - the port of writing C's
     * {@code upkeep->arena_level}. Cleared by the game loop once an arena bout has been left behind.
     *
     * <p>Function setArenaLevel coded before 261009, commented in full on 261009.
     *
     * @param arenaLevel {@code true} if the current level is an arena
     */
    public void setArenaLevel(boolean arenaLevel) {
        this.arenaLevel = arenaLevel;
    }

    /**
     * Reports whether a monster is currently on the health bar - the port of testing C's
     * {@code upkeep->health_who}.
     *
     * <p>Function healthWho coded before 261009, commented in full on 261009.
     *
     * @return {@code true} if a health-bar trackee is set
     */
    public boolean healthWho() {
        return healthWho != null;
    }

    /**
     * Returns the health-bar trackee - the port of reading C's {@code upkeep->health_who}.
     *
     * <p>Function getHealthWho coded before 261009, commented in full on 261009.
     *
     * @return the monster currently shown on the health bar, or {@code null} if none
     */
    public Monster getHealthWho() {
        return healthWho;
    }

    /**
     * Returns the monster-race trackee - the port of reading C's {@code upkeep->monster_race}.
     * There is no setter yet; the field is only ever null until the race-tracking code
     * ({@code monster_race_track} in the original) is ported.
     *
     * <p>Function getMonsterRace coded before 261009, commented in full on 261009.
     *
     * @return the monster race currently being recalled, or {@code null} if none
     */
    public MonsterRace getMonsterRace() {
        return monsterRace;
    }

    /**
     * Raises an update ({@code PU_*}) flag, marking a derived quantity for recalculation on the next
     * update pass - the port of C's {@code p->upkeep->update |= PU_...}. Discards the answer that
     * {@link #updateOn} returns; the two do the same thing.
     *
     * <p>Function setUpdateFlagOn coded before 261009, commented in full on 261009.
     *
     * @param flag the {@link PlayerUpdateEnum} recalculation to request
     */
    public void setUpdateFlagOn(PlayerUpdateEnum flag) {
        updateFlags.on(flag);
    }

    /**
     * Raises several update ({@code PU_*}) flags at once, the batch form of {@link #setUpdateFlagOn}.
     *
     * <p>No single C function to point at: C writes the disjunction inline, as
     * {@code p->upkeep->update |= (PU_BONUS | PU_HP | PU_SPELLS)}, because a bitfield makes raising
     * three flags no more work than raising one. The port holds a {@link Flag} instead, so the
     * convenience has to be a method or every such site becomes three calls.
     *
     * <p>Adds; it does not replace. Flags already raised and not named here stay raised, which is
     * what the {@code |=} guarantees and what callers depend on — several parts of a turn each ask
     * for their own recalculations before the update pass runs and clears the lot.
     *
     * <p>Function setUpdateFlagsOn commented in full on 260816, name corrected on 261009 (the
     * block previously called it {@code updateFlagsOn}).
     *
     * @param flags the {@link PlayerUpdateEnum} recalculations to request
     */
    public void setUpdateFlagsOn(PlayerUpdateEnum... flags) {
        updateFlags.set(flags);
    }

    /**
     * Raises a notice ({@code PN_*}) flag, queuing a housekeeping action for the next notice pass.
     *
     * <p>Discards the answer that {@link #orNoticeFlag} and {@link #setNoticeFlagOn} give; the
     * three do the same thing and exist side by side because they arrived at different times.
     *
     * <p>Function noticeFlagOn coded before 261009, commented in full on 261009.
     *
     * @param flag the {@link PlayerNotice} action to request
     */
    public void noticeFlagOn(PlayerNotice flag) {
        noticeFlags.on(flag);
    }

    /**
     * Returns the resting counter - the port of reading C's {@code upkeep->resting}, which is what
     * {@code player_resting_count} ({@code player-util.c}) returns. Zero means not resting; a
     * negative value is one of the special rest codes, not a count.
     *
     * <p>Function getRestingCounter coded before 261009, commented in full on 261009.
     *
     * @return the number of turns of rest remaining, or a negative special rest code
     */
    public int getRestingCounter() {
        return restingCounter;
    }

    /**
     * Sets whether the game should autosave at the next opportunity (e.g. on reaching a new level)
     * - the port of writing C's {@code upkeep->autosave}. There is no getter yet; the field is
     * write-only until the save code is ported.
     *
     * <p>Function setAutosave coded before 261009, commented in full on 261009.
     *
     * @param autosave {@code true} to request an autosave
     */
    public void setAutosave(boolean autosave) {
        this.autosave = autosave;
    }

    /**
     * Points the health bar at a monster, the port of C's {@code health_track}
     * ({@code player-calcs.c}).
     *
     * <p>Tracking is a display concern rather than a combat one: it decides whose health the sidebar
     * shows, and nothing else follows from it. The monster the player is fighting is the usual
     * subject, but so is one they have merely looked at, which is why this is separate from anything
     * that knows about attacks.
     *
     * <p>Both statements are needed and neither implies the other. Setting the tracked monster
     * changes what <em>should</em> be on screen; raising {@code PR_HEALTH} is what gets it drawn.
     * C pairs them in the same two lines for the same reason.
     *
     * <p>A null monster is not a missing argument but the way tracking is switched off — the bar
     * clears when there is nothing worth watching. {@code GameWorld} passes null on exactly that
     * path, and C does the same.
     *
     * <p>C passes the upkeep in as a parameter; here the method lives on it. The tracked-flag cache
     * write inside {@link #setHealthWho} is the Java-only part and is going to be replaced (see the
     * class comment).
     *
     * <p>Function healthTrack commented in full on 260816, line-number citation removed on 261009.
     *
     * @param monster the monster to track, or {@code null} to stop tracking
     */
    public void healthTrack(Monster monster) {
        setHealthWho(monster);
        setRedrawFlagsOn(PlayerRedraw.PR_HEALTH);
    }

    /**
     * Switches on one of the notice flags, the port of C's {@code p->upkeep->notice |= PN_…}.
     *
     * <p>The notice flags are a request queue rather than a description of state: setting
     * {@code PN_IGNORE} does not ignore anything, it asks for the ignore pass to be run at the next
     * convenient point in the turn. That indirection is what lets a discovery deep inside the
     * knowledge code — {@code PlayerKnowledge.knowObject} becoming aware of a
     * flavour — ask for expensive work without doing it there and then.
     *
     * <p>The name says {@code or} because C's is a bitwise or, and the return value is the answer to
     * "was this new?" that {@link uk.co.jackoftradesltd.channel.utils.Flag#on} gives. C's macro answers
     * nothing at all; callers that only want the request made can ignore it.
     *
     * <p>Function orNoticeFlag coded on 260816, commented in full on 260816.
     *
     * @param flag the notice to request
     * @return {@code false} if the flag was already set, {@code true} if this call set it
     */
    public boolean orNoticeFlag(PlayerNotice flag) {
        return noticeFlags.on(flag);
    }

    /**
     * Returns the quiver, the port of reading C's {@code p->upkeep->quiver}.
     *
     * <p>Live, not a copy — the quiver is rebuilt in place as ammunition is picked up and fired. Slot
     * order is what the player sees and types: position in this list is the label the object is
     * chosen by, which is why {@code gearToLabel} walks it by index rather than searching it.
     *
     * <p>Function getQuiver coded on 260816, commented in full on 260816.
     *
     * @return the quiver slots, shared with this instance
     */
    public ItemObject[] getQuiver() {
        return quiverObjects;
    }

    /**
     * Returns the pack, the port of reading C's {@code p->upkeep->inven}.
     *
     * <p>Live, not a copy, and ordered — as with {@link #getQuiver}, an object's position here is the
     * letter the player selects it by.
     *
     * <p>This is a view of part of the gear, not a second store of it. C keeps every carried object
     * on the one {@code p->gear} list and rebuilds {@code inven} as an index into it, so an object
     * appearing here is also in the gear; see
     * {@code PlayerKnowledge.knowObject} for the carried test that relies on
     * that.
     *
     * <p>Function getInventory coded on 260816, commented in full on 260816.
     *
     * @return the pack slots, shared with this instance
     */
    public ItemObject[] getInventory() {
        return inventoryObjects;
    }

    /**
     * The total weight the player is carrying, in tenth-pounds — C's {@code upkeep->total_weight}
     * ({@code player.h}).
     *
     * <p>Read by {@code calcBonuses} for the carrying penalty: once the load passes half the
     * strength-derived limit, every further tenth of that limit costs a point of speed
     * ({@code calc_bonuses} in {@code player-calcs.c}). This is the whole burden — pack, quiver and
     * worn gear — not just what is worn.
     *
     * <p>Function getTotalWeight commented in full on 260820, line-number citations removed on
     * 261009.
     *
     * @return the carried weight in tenth-pounds
     */
    public int getTotalWeight() {
        return totalWeight;
    }

    /**
     * Reports whether only a partial update is wanted - the port of reading C's
     * {@code upkeep->only_partial}. The level-feeling code sets it so that a refresh does not redo
     * the whole calculation. There is no setter yet; the field is only ever false until that code
     * is ported.
     *
     * <p>Function isOnlyPartial coded before 261009, commented in full on 261009.
     *
     * @return {@code true} when only a partial update is wanted
     */
    public boolean isOnlyPartial() {
        return onlyPartial;
    }

    /**
     * Answers whether any one-off notice action is pending - the port of C's truth test on
     * {@code p->upkeep->notice}, which is a bit field and so is simply tested against zero.
     *
     * <p>{@code Player.noticeStuff} returns immediately when this is {@code false}, which is the
     * common case: the flags are raised by events and cleared as they are acted on.
     *
     * <p>Function isNotice commented in full on 260827.
     *
     * @return {@code true} if at least one {@code PN_} flag is raised
     */
    public boolean isNotice() {
        return !noticeFlags.isEmpty();
    }

    /**
     * Returns the pending notice flags for reading - the port of reading C's
     * {@code p->upkeep->notice}. A {@link FlagView}, so a reader can test the set but not change
     * it; {@link #setNoticeFlagOn} and {@link #setNoticeFlagOff} are the way to change it.
     *
     * <p>Function getNoticeFlags coded before 261009, commented in full on 261009.
     *
     * @return a read-only view of the pending notice flags
     */
    public FlagView<PlayerNotice> getNoticeFlags() {
        return noticeFlags;
    }

    /**
     * Clears one pending notice action - the port of C's
     * {@code p->upkeep->notice &= ~(PN_...)}.
     *
     * <p>{@code noticeStuff} clears each flag <em>before</em> doing the work it asks for, so that an
     * action which raises the same flag again - as the ignore drop does for the pack combine - has
     * its request survive to the next pass instead of being wiped by this one.
     *
     * <p>Function setNoticeFlagOff commented in full on 260827.
     *
     * @param playerNotice the flag to clear
     */
    public void setNoticeFlagOff(PlayerNotice playerNotice) {
        noticeFlags.off(playerNotice);
    }

    /**
     * Raises one pending notice action - the port of C's
     * {@code p->upkeep->notice |= (PN_...)}.
     *
     * <p>Raised by whatever notices the need - an ignore setting changing, an item entering the
     * pack - and acted on by {@code noticeStuff} on the next pass rather than at once.
     *
     * <p>Function setNoticeFlagOn commented in full on 260827.
     *
     * @param playerNotice the flag to raise
     */
    public void setNoticeFlagOn(PlayerNotice playerNotice) {
        noticeFlags.on(playerNotice);
    }

    /**
     * Returns how many items the pack currently holds - C's {@code upkeep->inven_cnt}, rebuilt by
     * {@code calcInventory} rather than maintained item by item.
     *
     * <p>Function getInventoryCount coded before 261009, commented in full on 261009.
     *
     * @return the number of items in the pack
     */
    public int getInventoryCount() {
        return inventoryCount;
    }

    /**
     * Records how many items the pack holds. Written by the inventory rebuild, which counts the
     * slots as it fills them; nothing else should set it.
     *
     * <p>Function setInventoryCount coded before 261009, commented in full on 261009.
     *
     * @param i the new pack count
     */
    public void setInventoryCount(int i) {
        this.inventoryCount = i;
    }

    /**
     * Returns how many items the quiver currently holds - C's {@code upkeep->quiver_cnt}, rebuilt
     * by {@code calcInventory}.
     *
     * <p>Function getQuiverCount coded before 261009, commented in full on 261009.
     *
     * @return the number of items in the quiver
     */
    public int getQuiverCount() {
        return quiverCount;
    }

    /**
     * Records how many items the quiver holds. Written by the inventory rebuild, which counts the
     * slots as it fills them; nothing else should set it.
     *
     * <p>Function setQuiverCount coded before 261009, commented in full on 261009.
     *
     * @param quiverCount the new quiver count
     */
    public void setQuiverCount(int quiverCount) {
        this.quiverCount = quiverCount;
    }

    /**
     * Returns the object trackee - C's {@code p->upkeep->object}.
     *
     * <p>Function getObject coded before 261009, commented in full on 261009.
     *
     * @return the object the player is currently examining, or {@code null} when nothing is being
     * tracked
     */
    public ItemObject getObject() {
        return object;
    }

    /**
     * Sets or clears the object the player is currently examining - C's object trackee.
     *
     * <p>Cleared with {@code null} when the tracked object is deleted, so that nothing holds a
     * reference to an object that no longer exists.
     *
     * <p>Function setObject coded before 261009, commented in full on 261009.
     *
     * @param object the object now being examined, or {@code null} to stop tracking
     */
    public void setObject(ItemObject object) {
        this.object = object;
    }

    /**
     * Reports whether any recalculation at all is pending - the port of C's truth test on the whole
     * bitfield, {@code if (!p->upkeep->update) return;} at the head of {@code update_stuff}
     * ({@code player-calcs.c}), and the same test guarding the opportunistic
     * {@code if (player->upkeep->update) update_stuff(player)} calls in {@code obj-gear.c} and
     * {@code project.c}.
     *
     * <p>C can ask this because {@code update} is a single {@code u32b}: zero means nothing pending.
     * The port holds a {@link Flag} instead, so the question becomes "is the set empty", and the
     * inversion has to be written out. The name follows C's field rather than Java's {@code isX}
     * convention for a boolean, so read it as "is there update work", not as a getter for a flag.
     *
     * <p>Function getUpdate commented in full on 260828.
     *
     * @return {@code true} when at least one {@code PU_*} flag is raised, {@code false} when the
     * update set is empty
     */
    public boolean getUpdate() {
        return !updateFlags.isEmpty();
    }

    /**
     * Asks whether one particular recalculation is pending - the port of C's
     * {@code if (p->upkeep->update & (PU_BONUS))} test in {@code update_stuff}
     * ({@code player-calcs.c}).
     *
     * <p>Read-only: unlike {@link #updateOff} it leaves the flag raised, so a caller that acts on a
     * {@code true} answer must clear the flag itself or the same work will be done again on the next
     * update pass.
     *
     * <p>Function updateHas commented in full on 260828.
     *
     * @param flag the {@link PlayerUpdateEnum} recalculation to ask about
     * @return {@code true} when that flag is raised
     */
    public boolean updateHas(PlayerUpdateEnum flag) {
        return updateFlags.has(flag);
    }

    /**
     * Lowers one update flag - the port of C's {@code p->upkeep->update &= ~(PU_BONUS)}, which
     * {@code update_stuff} ({@code player-calcs.c}) performs immediately before running the
     * corresponding recalculation, so that work requested again from inside that recalculation is
     * not lost.
     *
     * <p>Clearing before recalculating, not after, is the order C chose and the port keeps it: the
     * calculation being run may itself raise its own flag again, and clearing afterwards would
     * discard that request.
     *
     * <p>The return value is the port's own addition - C's {@code &=} yields nothing a caller reads.
     * It reports whether the flag was actually raised, which lets a caller collapse the C pair of a
     * test then a clear into the single call {@code if (updateOff(PU_BONUS)) { ... }}.
     *
     * <p>Function updateOff commented in full on 260828.
     *
     * @param flag the {@link PlayerUpdateEnum} recalculation to clear
     * @return {@code true} when the flag had been raised and is now lowered, {@code false} when it
     * was already clear and nothing changed
     */
    public boolean updateOff(PlayerUpdateEnum flag) {
        return updateFlags.off(flag);
    }

    /**
     * Raises one update flag - the port of C's {@code p->upkeep->update |= (PU_BONUS)}, the request
     * a caller makes when it has changed something a derived quantity was computed from and wants
     * that quantity recomputed on the next update pass.
     *
     * <p>Adds; it does not replace. A flag already raised stays raised and nothing else in the set
     * is disturbed, which is what C's {@code |=} guarantees and what callers rely on - several
     * parts of a turn each ask for their own recalculations before {@code update_stuff} runs and
     * clears the lot.
     *
     * <p>Raising a flag is idempotent, so the request carries no count: two calls before an update
     * pass produce one recalculation, not two. C has no choice about this and the port keeps it.
     *
     * <p>The return value is the port's own addition - C's {@code |=} yields nothing a caller reads.
     * It reports whether the flag had been clear, so a caller can tell a fresh request from a
     * duplicate. It is the mirror of {@link #updateOff}'s answer, and is ignored at almost every
     * call site.
     *
     * <p>This is the same operation as {@link #setUpdateFlagOn}, which discards the answer instead
     * of returning it; the two exist side by side because the flag-returning form arrived later.
     *
     * <p>Function updateOn commented in full on 260831.
     *
     * @param flag the {@link PlayerUpdateEnum} recalculation to request
     * @return {@code true} when the flag had been clear and is now raised, {@code false} when it
     * was already raised and nothing changed
     */
    public boolean updateOn(PlayerUpdateEnum flag) {
        return updateFlags.on(flag);
    }

    /**
     * Returns the running counter - the port of reading C's {@code upkeep->running}, which is the
     * count of steps still to be taken, and zero when the player is not running. There is no
     * setter yet; the field is only ever zero until the running and pathfinding code is ported.
     *
     * <p>Function getRunning coded before 261009, commented in full on 261009.
     *
     * @return the steps still to be taken in the current run, or {@code 0} when not running
     */
    public int getRunning() {
        return runningCounter;
    }

    /**
     * Replaces the pack outright - there is no single C statement this ports, because C never
     * reassigns {@code p->upkeep->inven} once {@code init_player} ({@code player.c}) or
     * {@code player_generate} ({@code player-birth.c}) allocates it; every other C write goes
     * through a slot, {@code p->upkeep->inven[i] = obj}.
     *
     * <p>{@link #getInventory} promises a live view, not a copy - true only up to the next call
     * here. A reference obtained before a swap keeps pointing at the old array, so a caller that
     * holds onto one across a call to this method is looking at a stale pack.
     *
     * <p>Function setInventory commented in full on 260903, line-number citations removed on
     * 261009.
     *
     * @param inventory the array to install as the pack, replacing whatever was there
     */
    public void setInventory(ItemObject[] inventory) {
        this.inventoryObjects = inventory;
    }

    /**
     * Replaces the quiver outright - the same wholesale swap as {@link #setInventory}, and for the
     * same reason with no single C statement behind it: {@code p->upkeep->quiver} is allocated once,
     * in {@code init_player} ({@code player.c}) and {@code player_generate}
     * ({@code player-birth.c}), and every other C write addresses a slot rather than the array
     * itself.
     *
     * <p>{@link #getQuiver} promises a live view, not a copy - true only up to the next call here;
     * see {@link #setInventory} for what that means for a caller holding an old reference.
     *
     * <p>Function setQuiverObjects commented in full on 260903, line-number citations removed on
     * 261009.
     *
     * @param quiverObjects the array to install as the quiver, replacing whatever was there
     */
    public void setQuiverObjects(ItemObject[] quiverObjects) {
        this.quiverObjects = quiverObjects;
    }

    /**
     * Replaces the total weight the player is carrying - the port of writing C's
     * {@code upkeep->total_weight} ({@code player.h}). C assigns the field directly wherever it
     * recomputes the burden, most notably {@code calc_inventory} and {@code calc_weight}
     * ({@code player-calcs.c}) after any change to the pack, quiver or worn gear, and birth zeroes it
     * outright before either runs ({@code player-birth.c}). Assignment itself does no validation
     * in either language; see {@link #getTotalWeight} for what the value is used for.
     *
     * <p>Function setTotalWeight commented in full on 260904, line-number citations removed on
     * 261009.
     *
     * @param weight the carried weight in tenth-pounds
     */
    public void setTotalWeight(int weight) {
        this.totalWeight = weight;
    }

    /**
     * Returns the number of equipment slots currently occupied - the port of reading C's
     * {@code upkeep->equip_cnt} ({@code player.h}). C never reads the field through a
     * function; every caller ({@code ui-knowledge.c}, {@code ui-death.c}) tests the
     * struct member directly for zero/non-zero. This getter is that same read, wrapped, and is
     * also how a caller here gets the value to increment or decrement (see {@link #setEquipCount}).
     *
     * <p>Function getEquipCount commented in full on 260905, line-number citations removed on
     * 261009.
     *
     * @return the count of occupied equipment slots
     */
    public int getEquipCount() {
        return equipmentCount;
    }

    /**
     * Replaces the equipment count outright - the port of writing C's {@code upkeep->equip_cnt}
     * ({@code player.h}). Unlike {@link #setInventoryCount} and {@link #setQuiverCount},
     * which are rebuilt wholesale by {@code calcInventory} from scratch, C never assigns this
     * field wholesale: every write is an in-place {@code ++} or {@code --} at the moment a single
     * item is worn or removed ({@code player-birth.c}, {@code obj-gear.c}, {@code load.c}).
     * A caller here reproduces that by reading {@link #getEquipCount} and
     * passing the incremented or decremented value straight back in; the setter itself does no
     * arithmetic and no validation.
     *
     * <p>Function setEquipCount commented in full on 260905, line-number citations removed on
     * 261009.
     *
     * @param equipmentCount the new count of occupied equipment slots
     */
    public void setEquipCount(int equipmentCount) {
        this.equipmentCount = equipmentCount;
    }

    /**
     * Sets (or clears) whether a game is actually in progress - the port of writing C's
     * {@code upkeep->playing}. C has no setter function for it either; every call site assigns it
     * directly, {@code true} once birth completes ({@code player-birth.c}) or a save loads
     * ({@code savefile.c}), and {@code false} again on death or quitting
     * ({@code ui-command.c}, {@code ui-signals.c}). The turn loop in {@code game-world.c}
     * reads it as its master condition, stopping the moment it goes false.
     *
     * <p>Function setPlaying commented in full on 260908, line-number citations removed on
     * 261009.
     *
     * @param playing {@code true} while a game is actually in progress
     */
    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    /**
     * Sets or clears the health-bar trackee - the port of writing C's {@code upkeep->health_who}.
     * Unlike {@link #healthTrack}, which is C's {@code health_track} and also raises
     * {@code PR_HEALTH}, this is the bare field write: nothing is marked for repainting.
     *
     * <p><b>The block after the field write is temporary and is going to be replaced.</b> It
     * publishes whether anything is tracked through the static {@link PlayerEventStatusUpdate}
     * cache, which the port is moving away from; the replacement passes the tracked monster's
     * values through to the UI as part of the messages it is sent, to be used at display time. C
     * pushes nothing at assignment - its health bar reads {@code health_who} when it redraws. When
     * the replacement arrives this setter goes back to being the plain assignment.
     *
     * <p>Function setHealthWho coded before 261009, commented in full on 261009.
     *
     * @param healthWho the monster to show on the health bar, or {@code null} for none
     */
    public void setHealthWho(Monster healthWho) {
        this.healthWho = healthWho;

        // Update cached value
        PlayerEventStatusUpdate.updatePlayerStatusMonsterTracked(healthWho != null);
    }

    /**
     * Replaces the resting counter - the port of writing C's {@code upkeep->resting}. The field
     * write is the whole of the C behaviour: {@code player_resting_set_count} ({@code player-util.c})
     * does the clamping and the disturb handling around it, and the UI works out the rest text from
     * {@code player_resting_count} when it redraws, so nothing in C is pushed anywhere at the moment
     * of assignment.
     *
     * <p><b>The block after the field write is temporary and is going to be replaced.</b> It
     * publishes the rest text through the static {@link PlayerEventStatusUpdate} cache, which the
     * port is moving away from. The replacement passes the resting values through to the UI as part
     * of the messages it is sent, so that the text is built from them at display time, as C does,
     * rather than being held in a cache. When that arrives this setter goes back to being the plain
     * assignment above.
     *
     * <p>Until then the block is a placeholder and is known to be wrong. The text is the fixed string
     * {@code "Resting string goes here"} rather than a real rest status, and it is published only
     * while the counter is non-zero, so a counter returning to {@code 0} leaves the last text in the
     * cache. The negative special codes ({@code REST_ALL_POINTS}, {@code REST_COMPLETE},
     * {@code REST_SOME_POINTS}) also count as non-zero. None of this needs fixing in place, because
     * the block is the part being thrown away.
     *
     * <p>Function setRestingCounter coded before 261009, commented in full on 261009.
     *
     * @param restingCounter the new resting counter - a count of turns, or one of the negative
     *                       {@code REST_*} special codes
     */
    public void setRestingCounter(int restingCounter) {
        this.restingCounter = restingCounter;

        // update cached value
        if (this.restingCounter != 0) {
            // TODO: Update resting string with correct value
            PlayerEventStatusUpdate.updatePlayerStatusRestingRepeatStatus("Resting string goes here");
        }
    }
}
