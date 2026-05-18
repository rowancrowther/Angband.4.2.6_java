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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.strings.Quark;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.Effect;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Stem class to hold the c angband object
 */

public class ItemObject {
    private ObjectKind kind;
    private EgoItem ego;
    private Artifact artifact;

    private ItemObject known;

    private Loc location;

    private TValue tValue;
    private int sValue;

    private int pValue;

    private int weight;

    private int damageDice;
    private int damageSides;
    private int normalAC;
    private int toAC;
    private int toDam;
    private int toHit;

    private Flag<ObjectFlag> flags;
    private HashMap<ObjectModifier, Integer> modifiers;
    private HashMap<ElementEnum, ElementInfo> elInfo;
    private boolean brands;
    private boolean slays;
    private ArrayList<CurseData> curses;

    private ArrayList<Effect> effect;
    private String effectMessage;
    private Activation activation;
    private Random time;
    private int timeout;

    private int number;
    private Flag<ObjectNotice> notice;

    private int heldMIndex;
    private int mimickingMIndex;

    private int origin;
    private int originDepth;
    private MonsterRace originRace;

    private Quark note;

    public ItemObject() {
        notice = new Flag<>(ObjectNotice.class);
    }

    public void setGrid(Loc grid) {
        location = grid;
    }

    public void orNotice(ObjectNotice notice) {
        this.notice.on(notice);
    }

    public boolean isArtifact() {
        return artifact != null;
    }

    public ObjectKind getKind() {
        return kind;
    }

    public void setHeldMIndex(int heldMIndex) {
        this.heldMIndex = heldMIndex;
    }

    public void setMimickingMIndex(int mimickingMIndex) {
        this.mimickingMIndex = mimickingMIndex;
    }

    public boolean similar(ItemObject item2) {
        Player player = GameConstants.mainPlayer;

        // Check for equipped items
        if (player.getPlayerBody().isEquipped(this)) return false;
        if (player.getPlayerBody().isEquipped(item2)) return false;

        // Check for mimicked items
        if (this.mimickingMIndex != 0 || item2.mimickingMIndex != 0) return false;

        // Check for unknown items
        return true;
    }
}