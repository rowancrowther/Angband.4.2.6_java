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

package uk.co.jackoftrades.middle.player.enums;

import uk.co.jackoftrades.channel.enums.GameEventType;

/**
 * "Redraw" flags identifying which on-screen region has become stale and needs
 * repainting — the {@code PR_*} set from the C original ({@code player-calcs.h}).
 *
 * <p>When game state changes, code raises the relevant {@code PR_*} flag(s) on the
 * player's upkeep ({@link uk.co.jackoftrades.middle.player.PlayerUpkeep}); the render
 * loop later consults them and repaints <em>only</em> the flagged areas, clearing each
 * flag as it goes. This is the engine's dirty-region mechanism: it avoids redrawing
 * the whole terminal every turn, which matters for responsiveness on a character cell
 * display.
 *
 * <p><b>Why an enum rather than C bit constants:</b> the original packs these as bit
 * positions in a single integer and OR-s them together. The port represents the set
 * with an {@link java.util.EnumSet}, trading bit-twiddling for type-safety (a redraw
 * flag can never be mixed up with an update/{@code PU_*} or notice flag) while keeping
 * the same declared order as the C list.
 *
 * <p><b>The three booleans replace C's group masks.</b> C composes {@code PR_BASIC},
 * {@code PR_EXTRA} and {@code PR_SUBWINDOW} by OR-ing named subsets together
 * ({@code player-calcs.h:81-96}), so asking "is this flag part of the basic display?" means testing
 * a constant against a mask. Without bit values to OR, the port turns the question round and lets
 * each constant carry its own membership. The three groups are disjoint and do not cover
 * everything: the map, the two item lists, the message line, the trap-detect edge, the level
 * feeling and the light radius belong to none of them.
 *
 * @author Rowan Crowther
 */
public enum PlayerRedraw {
    /**
     * Miscellaneous header fields (race / class line).
     */
    PR_MISC(true, false, false, GameEventType.EVENT_RACE_CLASS),
    /** The character's title (changes with class level). */
    PR_TITLE(true, false, false, GameEventType.EVENT_PLAYERTITLE),
    /** Experience level. */
    PR_LEV(true, false, false, GameEventType.EVENT_PLAYERLEVEL),
    /** Experience points. */
    PR_EXP(true, false, false, GameEventType.EVENT_EXPERIENCE),
    /** The six primary stats. */
    PR_STATS(true, false, false, GameEventType.EVENT_STATS),
    /** Armour class. */
    PR_ARMOR(true, false, false, GameEventType.EVENT_AC),
    /** Hit points (current / maximum). */
    PR_HP(true, false, false, GameEventType.EVENT_HP),
    /** Spell points / mana. */
    PR_MANA(true, false, false, GameEventType.EVENT_MANA),
    /** Gold total. */
    PR_GOLD(true, false, false, GameEventType.EVENT_GOLD),
    /** The monster health bar. */
    PR_HEALTH(true, false, false, GameEventType.EVENT_MONSTERHEALTH),
    /** Movement speed. */
    PR_SPEED(true, false, false, GameEventType.EVENT_PLAYERSPEED),
    /** Number of spells available to study. */
    PR_STUDY(false, true, false, GameEventType.EVENT_STUDYSTATUS),
    /** Current dungeon depth. */
    PR_DEPTH(true, false, false, GameEventType.EVENT_DUNGEONLEVEL),
    /** Status-line indicators (hunger, conditions, …). */
    PR_STATUS(false, true, false, GameEventType.EVENT_STATUS),
    /** Trap-detection boundary indicator. */
    PR_DTRAP(false, false, false, GameEventType.EVENT_DETECTIONSTATUS),
    /** Action state (resting, searching, etc.). */
    PR_STATE(false, true, false, GameEventType.EVENT_STATE),
    /** The dungeon map view. */
    PR_MAP(false, false, false, GameEventType.EVENT_MAP),
    /** Inventory listing. */
    PR_INVEN(false, false, false, GameEventType.EVENT_INVENTORY),
    /** Equipment listing. */
    PR_EQUIP(false, false, false, GameEventType.EVENT_EQUIPMENT),
    /** Message line. */
    PR_MESSAGE(false, false, false, GameEventType.EVENT_MESSAGE),
    /** The currently targeted/tracked monster's recall. */
    PR_MONSTER(false, false, true, GameEventType.EVENT_MONSTERTARGET),
    /** The currently tracked object's details. */
    PR_OBJECT(false, false, true, GameEventType.EVENT_OBJECTTARGET),
    /** The visible-monster list subwindow. */
    PR_MONLIST(false, false, true, GameEventType.EVENT_MONSTERLIST),
    /** The visible-item list subwindow. */
    PR_ITEMLIST(false, false, true, GameEventType.EVENT_ITEMLIST),
    /** The level-feeling indicator. */
    PR_FEELING(false, false, false, GameEventType.EVENT_FEELING),
    /** The player's light radius (affects what must be repainted). */
    PR_LIGHT(false, false, false, GameEventType.EVENT_LIGHT),
    ;

    /**
     * Membership of C's {@code PR_BASIC} group — the character-sheet fields down the side bar.
     */
    private boolean basic;
    /**
     * Membership of C's {@code PR_EXTRA} group — the status, state and study indicators.
     */
    private boolean extra;
    /**
     * Membership of C's {@code PR_SUBWINDOW} group — the detachable recall and list panes.
     */
    private boolean subwindow;

    private GameEventType eventType;

    /**
     * Binds a redraw flag to the display groups it belongs to. A flag may belong to none of the
     * three, and no flag belongs to more than one.
     *
     * @param basic     whether the flag is part of C's {@code PR_BASIC}
     * @param extra     whether the flag is part of C's {@code PR_EXTRA}
     * @param subwindow whether the flag is part of C's {@code PR_SUBWINDOW}
     */
    PlayerRedraw(boolean basic, boolean extra, boolean subwindow, GameEventType eventType) {
        this.basic = basic;
        this.extra = extra;
        this.subwindow = subwindow;
        this.eventType = GameEventType.EVENT_RACE_CLASS;
    }

    /**
     * @return {@code true} if this flag is one of C's {@code PR_BASIC} set
     * ({@code player-calcs.h:81-84})
     */
    public boolean isBasic() {
        return basic;
    }

    /**
     * @return {@code true} if this flag is one of C's {@code PR_EXTRA} set
     * ({@code player-calcs.h:89-90})
     */
    public boolean isExtra() {
        return extra;
    }

    /**
     * @return {@code true} if this flag is one of C's {@code PR_SUBWINDOW} set
     * ({@code player-calcs.h:95-96})
     */
    public boolean isSubwindow() {
        return subwindow;
    }

    public GameEventType getEventType() {
        return eventType;
    }
}
