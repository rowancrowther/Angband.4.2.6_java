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
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerOption;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    // private PlayerRace race;
    // private PlayerClass class;

    private Loc grid;
    private Loc oldGrid;

    private int hitDie;
    private int expFact;

    private int age;
    private int height;
    private int weight;

    private int au;

    private int maxDepth;
    private int recallDepth;
    private int depth;

    private int maxLevel;
    private int level;

    private int maxExp;
    private int exp;
    private int expFrac;

    private int maxHP;
    private int currentHP;
    private int chpFrac;

    private int maxSP;
    private int sp;
    private int cspFrac;

    private HashMap<Stats, Integer> statMax;
    private HashMap<Stats, Integer> statCur;
    private HashMap<Stats, Integer> statMap;

    private HashMap<TimedEffect, Integer> timed;

    private int wordRecall;
    private int deepDescent;

    private int energy;
    private int totalEnergy;
    private int restingTurn;

    private int food;

    private int unignoring;

    private int skipCmdCoercion;
    private int spellFlags; // TODO: Change this once we know what we are dealing with
    private int spellOrder; // TODO: Change this once we know what we are dealing with

    private String fullName;
    private String diedFrom;
    private String history;
    private ArrayList<Quest> quests;
    private int totalWinner;

    private int noScore;

    private boolean isDead;
    private boolean isWizard;

    private int[] playerHP;

    private int auBirth;
    private HashMap<Stats, Integer> statsBirth;
    private int htBirth;
    private int wtBirth;

    private PlayerOptions options;
    private PlayerHistory playerHistory;

    private PlayerBody body;
    private ArrayList<PlayerShape> shape;

    private ArrayList<ItemObject> gear;
    private ArrayList<ItemObject> gearKnown;

    private ArrayList<ItemObject> itemObjectsKnown;
    private Chunk cave;

    private PlayerState state;
    private PlayerState knownState;
    private PlayerUpkeep playerUpkeep;

    public Chunk getCave() {
        return cave;
    }

    public PlayerUpkeep getPlayerUpkeep() {
        return playerUpkeep;
    }

    public PlayerBody getPlayerBody() {
        return body;
    }

    /**
     * Tests to see if a given player flag ]is set on the player state
     *
     * @param flag the player flag to check for
     * @return true if the player flag is set on the player state
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasPlayerFlag(@NotNull PlayerFlag flag) {
        return state.hasPFlag(flag);
    }

    /**
     * Get the radius of the light of the current state of the player
     *
     * @return the light radius of the current state of the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int getStateLight() {
        return state.getCurLight();
    }

    /**
     * Get the amount of time left on a timed effect on the player
     *
     * @param timedEffect the timed effect we are looking for
     * @return the amount of turns left on the supplied timed effect, or 0 if they are not under that effect
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
     * Check whether a given PlayerOptionType flag is set
     *
     * @param type the flag to check the state of
     * @return true if the type flag is set
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean opt(@NotNull PlayerOption type) {
        return options.has(type);
    }

    public void knowObject(ItemObject item) {
        // TODO: Expand this
    }

    public boolean ignoreKnownItemOk(@NotNull ItemObject item) {
        // TODO: Expand this
        return false;
    }
}
