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

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.channel.utils.FlagView;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.Pile;
import uk.co.jackoftrades.middle.player.enums.PlayerNotice;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;

import java.util.List;

/**
 * The player's transient runtime bookkeeping — the recomputed-each-session state that is
 * <em>not</em> part of the saved character, as opposed to the persistent data held on
 * {@link Player}.
 *
 * <p>Ports the C {@code struct player_upkeep} ({@code player.h}). In the original this carries
 * the pending notice ({@code PN_*}), update ({@code PU_*}) and redraw ({@code PR_*}) flag sets,
 * the current trackees (health-bar target, recalled monster race, examined object), inventory and
 * quiver contents and counts, the resting/running/pathfinding counters, and the floor object pile
 * under the player. All of it is volatile — rebuilt rather than serialised — which is exactly what
 * lets the engine discard the upkeep and recompute it on load.
 *
 * <p><b>Status:</b> the full field set is modelled; accessors are being added as callers need
 * them, so many fields are not yet exposed.
 *
 * @author Rowan Crowther
 */
public class PlayerUpkeep {
    /**
     * The pile of objects on the floor beneath the player.
     */
    private Pile objectPile;

    /**
     * True while a game is actually in progress — the turn loop's master condition ({@code playing}).
     */
    private boolean playing;

    /**
     * True when an autosave is pending ({@code autosave}).
     */
    private boolean autosave;

    /**
     * True when the current level needs regenerating ({@code generate_level}).
     */
    private boolean generateLevel;

    /**
     * True when only partial updates are needed ({@code only_partial}).
     */
    private boolean onlyPartial;

    /**
     * True while an auto-drop is in progress ({@code dropping}).
     */
    private boolean dropping;

    /**
     * Energy spent this turn; the loop reads it to tell whether a turn was actually taken ({@code energy_use}).
     */
    private int energyUse;

    /**
     * Number of spells currently available to learn ({@code new_spells}).
     */
    private int newSpells;

    /**
     * The monster shown on the health bar — the health-bar trackee ({@code health_who}).
     */
    private Monster healthWho;

    /**
     * The monster race currently being recalled — the race trackee ({@code monster_race}).
     */
    private MonsterRace monsterRace;

    /**
     * The object currently being examined — the object trackee ({@code object}).
     */
    private ItemObject object;

    /**
     * The object kind currently being examined — the kind trackee ({@code object_kind}).
     */
    private ObjectKind objectKind;

    /**
     * Pending one-off housekeeping actions such as combining the pack or applying ignore rules ({@code notice}).
     */
    private Flag<PlayerNotice> noticeFlags = new Flag<>(PlayerNotice.class);

    /**
     * Derived quantities (HP, mana, view, …) that have gone stale and must be recomputed ({@code update}).
     */
    private Flag<PlayerUpdateEnum> updateFlags = new Flag<>(PlayerUpdateEnum.class);

    /**
     * Parts of the screen that have changed and need repainting by the UI ({@code redraw}).
     */
    private Flag<PlayerRedraw> redrawFlags = new Flag<>(PlayerRedraw.class);

    /**
     * Used by the UI to decide whether to start off showing equipment or
     * inventory listings when offering a choice.
     */
    private int command_wrk;

    /**
     * Create an up staircase on the next level generated ({@code create_up_stair}).
     */
    private boolean createUpStair;

    /**
     * Create a down staircase on the next level generated ({@code create_down_stair}).
     */
    private boolean createDownStair;

    /**
     * The next level is to be fully lit on creation ({@code light_level}).
     */
    private boolean lightLevel;

    /**
     * The current level is an arena ({@code arena_level}).
     */
    private boolean arenaLevel;

    /**
     * Resting counter: turns of rest remaining ({@code resting}).
     */
    private int restingCounter;

    /**
     * Running counter: state of an in-progress run ({@code running}).
     */
    private int runningCounter;

    /**
     * True if this is the first step of a run rather than following a precomputed path ({@code running_firststep}).
     */
    private boolean runningFirstStep;

    /**
     * The objects held in the quiver ({@code quiver}).
     */
    private ItemObject[] quiverObjects;

    /**
     * The objects held in the pack ({@code inven}).
     */
    private ItemObject[] inventoryObjects;

    /**
     * Total weight currently carried ({@code total_weight}).
     */
    private int totalWeight;

    /**
     * Number of items in the inventory ({@code inven_cnt}).
     */
    private int inventoryCount;

    /**
     * Number of items in the equipment ({@code equip_cnt}).
     */
    private int equipmentCount;

    /**
     * Number of items in the quiver ({@code quiver_cnt}).
     */
    private int quiverCount;

    /**
     * Power of the recharge effect in progress ({@code recharge_pow}).
     */
    private int rechargePower;

    /**
     * Pathfinding: number of steps left to walk ({@code step_count}).
     */
    private int stepCount;

    /** Pathfinding: the queued steps, in reverse order ({@code steps}). */
    private List<Integer> steps;

    /** Pathfinding: the destination grid being walked to ({@code path_dest}). */
    private Loc pathDestination;

    /**
     * Builds an empty upkeep, the port of the two-part setup C performs in {@code init_player}
     * ({@code player.c:481-483}).
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
     * {@code assert(!player->upkeep->steps)} ({@code cmd-cave.c:1433}, and again at 1479, 1530 and
     * 1561) to catch a walk being started while another is still queued. An empty list would satisfy
     * that test without meaning what it means, silently retiring a check C relies on; a null keeps
     * "no walk in progress" distinguishable from "a walk with nothing left in it". The port has no
     * pathfinder yet, so nothing reads this — the point is that the distinction is still available
     * when one arrives.
     *
     * <p>{@code playing} being false is the state {@link uk.co.jackoftrades.middle.player.Player}
     * starts in before birth completes, and several methods gate their messages on it.
     *
     * <p>Constructor PlayerUpkeep commented in full on 260816, {@code steps} changed from an empty
     * list to null the same day.
     */
    public PlayerUpkeep() {
        // C allocates these two explicitly; the rest of the struct is covered by mem_zalloc
        inventoryObjects = new ItemObject[GameConstants.getCarryCapPackSize() + 1];
        quiverObjects = new ItemObject[GameConstants.getCarryCapQuiverSize()];

        // C's mem_zalloc, written out by hand
        healthWho = null;
        monsterRace = null;
        object = null;
        objectKind = null;
        objectPile = null;
        pathDestination = Loc.zero;
        playing = false;
        steps = null;
    }

    /**
     * @return the pile of objects currently under the player
     */
    public Pile getPile() {
        return objectPile;
    }

    /**
     * Sets the UI's equipment-vs-inventory listing preference.
     *
     * @param command_wrk the preference value (see {@code obj-ui.c} in the original)
     */
    public void setCommand_wrk(int command_wrk) {
        this.command_wrk = command_wrk;
    }

    /**
     * @return the UI's equipment-vs-inventory listing preference
     */
    public int getCommand_wrk() {
        return command_wrk;
    }

    /**
     * Returns a snapshot of the parts of the screen currently waiting to be repainted — the port
     * of C's {@code uint32_t redraw = p->upkeep->redraw;} ({@code player-calcs.c:2681}).
     *
     * <p><b>The copy is mutable on purpose, and that is not the leak it looks like.</b> It belongs
     * to the caller: the live set stays private and is unreachable from here, so anything done to
     * the returned object is done to the caller's own working value. C relies on exactly that —
     * {@code redraw_stuff} narrows its local copy to {@code PR_SUBWINDOW} when the map is not on
     * screen ({@code :2691}) while leaving the pending set on the player untouched, so the flags
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
     * 260818 when the differencing clear arrived.
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
     * of C's {@code p->upkeep->redraw &= ~redraw;} ({@code player-calcs.c:2711}).
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
     * out of {@link uk.co.jackoftrades.channel.utils.Flag#diff} and is there for a caller that
     * wants it; the redraw pass has no use for it.
     *
     * <p>Function clearRedrawFlags coded on 260818, commented in full on 260818.
     *
     * @param handled the flags whose repaint has been carried out, left unmodified
     * @return {@code true} if any flag was actually cleared
     */
    public boolean clearRedrawFlags(FlagView<PlayerRedraw> handled) {
        return redrawFlags.diff(handled);
    }

    /**
     * Raises a redraw flag, marking that part of the screen as needing a repaint.
     *
     * @param flag the {@code PR_*} flag to set
     * @return {@code true} if the flag was previously clear (i.e. this call changed it)
     */
    public boolean setRedrawFlagsOn(PlayerRedraw flag) {
        return redrawFlags.on(flag);
    }

    /**
     * Clears a redraw flag, once that part of the screen has been repainted.
     *
     * @param flag the {@code PR_*} flag to clear
     * @return {@code true} if the flag was previously set (i.e. this call changed it)
     */
    public boolean setRedrawFlagsOff(PlayerRedraw flag) {
        return redrawFlags.off(flag);
    }

    /**
     * @return {@code true} while a game is actually in progress - the port of reading C's {@code upkeep->playing}
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Reports whether the player has spent energy this turn, i.e. whether a turn was actually taken -
     * the port of testing C's {@code upkeep->energy_use}.
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
     * @param energyUse the energy the current command used ({@code 0} for a free turn)
     */
    public void setEnergyUse(int energyUse) {
        this.energyUse = energyUse;
    }

    /**
     * @return the energy spent by the current command - the port of reading C's
     * {@code upkeep->energy_use}. Unlike {@link #energyUse()}, which reports only whether any energy
     * was used, this returns the actual amount, for the loop to deduct from the player's energy.
     */
    public int getEnergyUse() {
        return energyUse;
    }

    /**
     * @return {@code true} while an auto-drop is in progress - the port of reading C's
     * {@code upkeep->dropping}. During an auto-drop the per-turn cleanup skips its monster-refresh
     * work, since the map is about to be redrawn anyway.
     */
    public boolean getDropping() {
        return dropping;
    }

    /**
     * Sets (or clears) the auto-drop-in-progress flag - the port of writing C's
     * {@code upkeep->dropping}. The per-turn cleanup clears it once the drop has been handled.
     *
     * @param dropping {@code true} while stuff is being auto-dropped
     */
    public void setDropping(boolean dropping) {
        this.dropping = dropping;
    }

    /**
     * @return {@code true} when the current level needs regenerating - the port of reading C's
     * {@code upkeep->generate_level}
     */
    public boolean generateLevel() {
        return generateLevel;
    }

    /**
     * Requests (or clears the request for) regenerating the current level - the port of writing C's
     * {@code upkeep->generate_level}. The game loop honours this on its next pass, building a fresh
     * level via {@link uk.co.jackoftrades.middle.cave.Generate#prepareNextLevel} and clearing the
     * flag.
     *
     * @param generateLevel {@code true} to request a new level
     */
    public void setGenerateLevel(boolean generateLevel) {
        this.generateLevel = generateLevel;
    }

    /**
     * @return {@code true} when the current level is an arena - the port of reading C's
     * {@code upkeep->arena_level}
     */
    public boolean isArenaLevel() {
        return arenaLevel;
    }

    /**
     * Marks (or unmarks) the current level as an arena - the port of writing C's
     * {@code upkeep->arena_level}. Cleared by the game loop once an arena bout has been left behind.
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
     * @return {@code true} if a health-bar trackee is set
     */
    public boolean healthWho() {
        return healthWho != null;
    }

    /**
     * @return the monster currently shown on the health bar, or {@code null} if none - the port of
     * reading C's {@code upkeep->health_who}
     */
    public Monster getHealthWho() {
        return healthWho;
    }

    /**
     * @return the monster race currently being recalled, or {@code null} if none - the port of
     * reading C's {@code upkeep->monster_race}
     */
    public MonsterRace getMonsterRace() {
        return monsterRace;
    }

    /**
     * Raises an update ({@code PU_*}) flag, marking a derived quantity for recalculation on the next
     * update pass.
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
     * <p>Function updateFlagsOn commented in full on 260816.
     *
     * @param flags the {@link PlayerUpdateEnum} recalculations to request
     */
    public void setUpdateFlagsOn(PlayerUpdateEnum... flags) {
        updateFlags.set(flags);
    }

    /**
     * Raises a notice ({@code PN_*}) flag, queuing a housekeeping action for the next notice pass.
     *
     * @param flag the {@link PlayerNotice} action to request
     */
    public void noticeFlagOn(PlayerNotice flag) {
        noticeFlags.on(flag);
    }

    /**
     * @return the number of turns of rest remaining (the resting countdown)
     */
    public int getRestingCounter() {
        return restingCounter;
    }

    /**
     * Sets whether the game should autosave at the next opportunity (e.g. on reaching a new level).
     *
     * @param autosave {@code true} to request an autosave
     */
    public void setAutosave(boolean autosave) {
        this.autosave = autosave;
    }

    /**
     * Points the health bar at a monster, the port of C's {@code health_track}
     * ({@code player-calcs.c:2470}).
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
     * <p>Function healthTrack commented in full on 260816.
     *
     * @param monster the monster to track, or {@code null} to stop tracking
     */
    public void healthTrack(Monster monster) {
        healthWho = monster;
        setRedrawFlagsOn(PlayerRedraw.PR_HEALTH);
    }

    /**
     * Switches on one of the notice flags, the port of C's {@code p->upkeep->notice |= PN_…}.
     *
     * <p>The notice flags are a request queue rather than a description of state: setting
     * {@code PN_IGNORE} does not ignore anything, it asks for the ignore pass to be run at the next
     * convenient point in the turn. That indirection is what lets a discovery deep inside the
     * knowledge code — {@link PlayerKnowledge#knowObject} becoming aware of a
     * flavour — ask for expensive work without doing it there and then.
     *
     * <p>The name says {@code or} because C's is a bitwise or, and the return value is the answer to
     * "was this new?" that {@link uk.co.jackoftrades.channel.utils.Flag#on} gives. C's macro answers
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
     * {@link PlayerKnowledge#knowObject} for the carried test that relies on
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
     * ({@code player.h:487}).
     *
     * <p>Read by {@code calcBonuses} for the carrying penalty: once the load passes half the
     * strength-derived limit, every further tenth of that limit costs a point of speed
     * ({@code player-calcs.c:2222-2227}). This is the whole burden — pack, quiver and worn gear —
     * not just what is worn.
     *
     * <p>Function getTotalWeight commented in full on 260820.
     *
     * @return the carried weight in tenth-pounds
     */
    public int getTotalWeight() {
        return totalWeight;
    }

    /**
     * @return {@code true} when only a partial update is wanted - C's {@code only_partial}, which
     * the level-feeling code sets so that a refresh does not redo the whole calculation
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
     * @return a read-only view of the pending notice flags - readers test them, and the two
     *         mutators below are the only way to change them
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
     * @return how many items the pack currently holds - C's {@code inven_cnt}, rebuilt by
     * {@code calcInventory} rather than maintained item by item
     */
    public int getInventoryCount() {
        return inventoryCount;
    }

    /**
     * Records how many items the pack holds. Written by the inventory rebuild, which counts the
     * slots as it fills them; nothing else should set it.
     *
     * @param i the new pack count
     */
    public void setInventoryCount(int i) {
        this.inventoryCount = i;
    }

    /**
     * @return how many items the quiver currently holds - C's {@code quiver_cnt}, rebuilt by
     * {@code calcInventory}
     */
    public int getQuiverCount() {
        return quiverCount;
    }

    /**
     * Records how many items the quiver holds. Written by the inventory rebuild, which counts the
     * slots as it fills them; nothing else should set it.
     *
     * @param quiverCount the new quiver count
     */
    public void setQuiverCount(int quiverCount) {
        this.quiverCount = quiverCount;
    }

    /**
     * @return the object the player is currently examining - C's object trackee
     * {@code p->upkeep->object}, or {@code null} when nothing is being tracked
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
     * @return the state of an in-progress run - the port of reading C's {@code upkeep->running},
     * which is the count of steps still to be taken, and zero when the player is not running
     */
    public int getRunning() {
        return runningCounter;
    }
}
