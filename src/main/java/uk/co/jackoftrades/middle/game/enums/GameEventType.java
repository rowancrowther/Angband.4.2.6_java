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

package uk.co.jackoftrades.middle.game.enums;

public enum GameEventType {
    EVENT_MAP,        /* Some part of the map has changed. */

    EVENT_STATS,        /* One or more of the stats. */
    EVENT_HP,        /* HP or MaxHP. */
    EVENT_MANA,        /* Mana or MaxMana. */
    EVENT_AC,        /* Armour Class. */
    EVENT_EXPERIENCE,    /* Experience or MaxExperience. */
    EVENT_PLAYERLEVEL,    /* Player's level has changed */
    EVENT_PLAYERTITLE,    /* Player's title has changed */
    EVENT_GOLD,        /* Player's gold amount. */
    EVENT_MONSTERHEALTH,    /* Observed monster's health level. */
    EVENT_DUNGEONLEVEL,    /* Dungeon depth */
    EVENT_PLAYERSPEED,    /* Player's speed */
    EVENT_RACE_CLASS,    /* Race or Class */
    EVENT_STUDYSTATUS,    /* "Study" availability */
    EVENT_STATUS,        /* Status */
    EVENT_DETECTIONSTATUS,  /* TrapEnum detection status */
    EVENT_FEELING,        /* Object level feeling */
    EVENT_LIGHT,        /* Light level */
    EVENT_STATE,        /* The two 'R's: Resting and Repeating */

    EVENT_PLAYERMOVED,
    EVENT_SEEFLOOR,         /* When the player would "see" floor objects */
    EVENT_EXPLOSION,
    EVENT_BOLT,
    EVENT_MISSILE,

    EVENT_INVENTORY,
    EVENT_EQUIPMENT,
    EVENT_ITEMLIST,
    EVENT_MONSTERLIST,
    EVENT_MONSTERTARGET,
    EVENT_OBJECTTARGET,
    EVENT_MESSAGE,
    EVENT_SOUND,
    EVENT_BELL,
    EVENT_USE_STORE,
    EVENT_STORECHANGED,    /* Triggered on a successful buy/retrieve or sell/drop */

    EVENT_INPUT_FLUSH,
    EVENT_MESSAGE_FLUSH,
    EVENT_CHECK_INTERRUPT,
    EVENT_REFRESH,
    EVENT_NEW_LEVEL_DISPLAY,
    EVENT_COMMAND_REPEAT,
    EVENT_ANIMATE,
    EVENT_CHEAT_DEATH,

    EVENT_INITSTATUS,    /* New status message for initialisation */
    EVENT_BIRTHPOINTS,    /* Change in the birth points */

    /* Changing of the game state/context. */
    EVENT_ENTER_INIT,
    EVENT_LEAVE_INIT,
    EVENT_ENTER_BIRTH,
    EVENT_LEAVE_BIRTH,
    EVENT_ENTER_GAME,
    EVENT_LEAVE_GAME,
    EVENT_ENTER_WORLD,
    EVENT_LEAVE_WORLD,
    EVENT_ENTER_STORE,
    EVENT_LEAVE_STORE,
    EVENT_ENTER_DEATH,
    EVENT_LEAVE_DEATH,

    /* Events for introspection into dungeon generation */
    EVENT_GEN_LEVEL_START, /* has string in event data for profile name */
    EVENT_GEN_LEVEL_END, /* has flag in event data indicating success */
    EVENT_GEN_ROOM_START, /* has string in event data for room type */
    EVENT_GEN_ROOM_CHOOSE_SIZE, /* has size in event data */
    EVENT_GEN_ROOM_CHOOSE_SUBTYPE, /* has string in event data with name */
    EVENT_GEN_ROOM_END, /* has flag in event data indicating success */
    EVENT_GEN_TUNNEL_FINISHED, /* has tunnel in event data with results */

    EVENT_END
}
