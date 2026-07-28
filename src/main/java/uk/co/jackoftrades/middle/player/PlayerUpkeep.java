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

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.monsters.Monster;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.Pile;
import uk.co.jackoftrades.middle.player.enums.PlayerNotice;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerUpkeepEnum;

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
    private Flag<PlayerUpkeepEnum> updateFlags = new Flag<>(PlayerUpkeepEnum.class);
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
    private List<ItemObject> quiverObjects;
    /**
     * The objects held in the pack ({@code inven}).
     */
    private List<ItemObject> inventoryObjects;
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
     * Returns a defensive copy of the pending redraw flags, so callers can inspect them without
     * being able to mutate the live set.
     *
     * @return a copy of the current {@code PR_*} redraw flags
     */
    public Flag<PlayerRedraw> getRedrawFlags() {
        return redrawFlags.copy();
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
}
