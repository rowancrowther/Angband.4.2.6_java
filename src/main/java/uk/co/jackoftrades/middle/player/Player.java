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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

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

    private Chunk cave;
    private PlayerUpkeep playerUpkeep;
    private PlayerBody playerBody;

    public Chunk getCave() {
        return cave;
    }

    public PlayerUpkeep getPlayerUpkeep() {
        return playerUpkeep;
    }

    public PlayerBody getPlayerBody() {
        return playerBody;
    }

    /**
     * Get the amount of time left on a timed effect on the player
     *
     * @param timedEffect the timed effect we are looking for
     * @return the amount of turns left on the supplied timed effect, or 0 if they are not under that effect
     */
    public int getTimedEffect(TimedEffect timedEffect) {
        if (timed.containsKey(timedEffect)) {
            return timed.get(timedEffect);
        }
        return 0;
    }

    public void knowObject(ItemObject item) {

    }
}
