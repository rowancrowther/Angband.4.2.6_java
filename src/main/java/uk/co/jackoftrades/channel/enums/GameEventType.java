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

package uk.co.jackoftrades.channel.enums;

/**
 * Every kind of game event the UI/event system can broadcast — display updates
 * (map, stats, HP, …), gameplay notifications (player moved, explosion, message),
 * context transitions (enter/leave birth, game, world, store, death) and
 * dungeon-generation introspection hooks. Observers register against these to be
 * notified of state changes. This is the Java port of the C original's
 * {@code game_event_type} ({@code src/game-event.h}); each constant's inline
 * comment gives its meaning.
 *
 * @author Rowan Crowther
 */
public enum GameEventType {
    /**
     * Some part of the map has changed. Carries the affected square as an
     * {@link uk.co.jackoftrades.channel.messages.data.EventDataGrid}; C signals it through
     * {@code event_signal_point}.
     *
     * @author Rowan Crowther
     */
    EVENT_MAP,

    /**
     * One or more of the stats.
     *
     * @author Rowan Crowther
     */
    EVENT_STATS,
    /**
     * HP or MaxHP.
     *
     * @author Rowan Crowther
     */
    EVENT_HP,
    /**
     * Mana or MaxMana.
     *
     * @author Rowan Crowther
     */
    EVENT_MANA,
    /**
     * Armour Class.
     *
     * @author Rowan Crowther
     */
    EVENT_AC,
    /**
     * Experience or MaxExperience.
     *
     * @author Rowan Crowther
     */
    EVENT_EXPERIENCE,
    /**
     * Player's level has changed.
     *
     * @author Rowan Crowther
     */
    EVENT_PLAYERLEVEL,
    /**
     * Player's title has changed.
     *
     * @author Rowan Crowther
     */
    EVENT_PLAYERTITLE,
    /**
     * Player's gold amount.
     *
     * @author Rowan Crowther
     */
    EVENT_GOLD,
    /**
     * Observed monster's health level.
     *
     * @author Rowan Crowther
     */
    EVENT_MONSTERHEALTH,
    /**
     * Dungeon depth.
     *
     * @author Rowan Crowther
     */
    EVENT_DUNGEONLEVEL,
    /**
     * Player's speed.
     *
     * @author Rowan Crowther
     */
    EVENT_PLAYERSPEED,
    /**
     * Race or Class.
     *
     * @author Rowan Crowther
     */
    EVENT_RACE_CLASS,
    /**
     * "Study" availability — whether there are spells the player could learn now.
     *
     * @author Rowan Crowther
     */
    EVENT_STUDYSTATUS,
    /**
     * Status — the transient conditions shown along the status line, such as poisoning, fear or
     * confusion.
     *
     * @author Rowan Crowther
     */
    EVENT_STATUS,
    /**
     * Trap detection status — whether the player is standing inside an area where traps have been
     * detected.
     *
     * @author Rowan Crowther
     */
    EVENT_DETECTIONSTATUS,
    /**
     * Object level feeling.
     *
     * @author Rowan Crowther
     */
    EVENT_FEELING,
    /**
     * Light level.
     *
     * @author Rowan Crowther
     */
    EVENT_LIGHT,
    /**
     * The two 'R's: Resting and Repeating.
     *
     * @author Rowan Crowther
     */
    EVENT_STATE,

    /**
     * The player has moved, so anything drawn relative to them needs revisiting.
     *
     * @author Rowan Crowther
     */
    EVENT_PLAYERMOVED,
    /**
     * When the player would "see" floor objects — signalled on arriving at a square or after
     * picking up, so the front end can report what is lying there.
     *
     * @author Rowan Crowther
     */
    EVENT_SEEFLOOR,
    /**
     * One frame of a ball effect going off. Carries an
     * {@link uk.co.jackoftrades.channel.messages.data.EventDataExplosion}.
     *
     * @author Rowan Crowther
     */
    EVENT_EXPLOSION,
    /**
     * One step of a bolt in flight. Carries an
     * {@link uk.co.jackoftrades.channel.messages.data.EventDataBolt}; sent repeatedly as the
     * projectile advances.
     *
     * @author Rowan Crowther
     */
    EVENT_BOLT,
    /**
     * One step of a thrown or fired object in flight. C's {@code game_event_data.missile} carries
     * the object, its visibility and its position; no payload shape has been ported for it yet.
     *
     * @author Rowan Crowther
     */
    EVENT_MISSILE,

    /**
     * The pack has changed.
     *
     * @author Rowan Crowther
     */
    EVENT_INVENTORY,
    /**
     * What the player is wearing or wielding has changed.
     *
     * @author Rowan Crowther
     */
    EVENT_EQUIPMENT,
    /**
     * The set of objects known to be on the level has changed.
     *
     * @author Rowan Crowther
     */
    EVENT_ITEMLIST,
    /**
     * The set of monsters known to be on the level has changed.
     *
     * @author Rowan Crowther
     */
    EVENT_MONSTERLIST,
    /**
     * The targeted monster has changed.
     *
     * @author Rowan Crowther
     */
    EVENT_MONSTERTARGET,
    /**
     * The targeted object has changed.
     *
     * @author Rowan Crowther
     */
    EVENT_OBJECTTARGET,
    /**
     * A line of text for the player. Carries an
     * {@link uk.co.jackoftrades.channel.messages.data.EventDataMessage}, whose category selects
     * the colour and sound the front end gives it.
     *
     * @author Rowan Crowther
     */
    EVENT_MESSAGE,
    /**
     * Play the sound belonging to a message category, with no text to go with it. Signalled from
     * C's {@code message.c} alongside {@link #EVENT_MESSAGE}.
     *
     * @author Rowan Crowther
     */
    EVENT_SOUND,
    /**
     * Alert the player without words — C's {@code bell()}, signalled with the
     * {@code MSG_BELL} category.
     *
     * @author Rowan Crowther
     */
    EVENT_BELL,
    /**
     * The player has stepped onto a shop entrance and the store screen should open.
     *
     * @author Rowan Crowther
     */
    EVENT_USE_STORE,
    /**
     * Triggered on a successful buy/retrieve or sell/drop.
     *
     * @author Rowan Crowther
     */
    EVENT_STORECHANGED,

    /**
     * Discard any input typed ahead. Signalled after something has happened that the player could
     * not have known about when they typed, so that a queued keypress does not answer a prompt
     * they have not read.
     *
     * @author Rowan Crowther
     */
    EVENT_INPUT_FLUSH,
    /**
     * Show any messages held back so far, before something is displayed that would otherwise
     * overwrite them.
     *
     * @author Rowan Crowther
     */
    EVENT_MESSAGE_FLUSH,
    /**
     * Ask whether the player has interrupted a repeated or resting action. Signalled once per
     * player turn from C's {@code process_player}.
     *
     * @author Rowan Crowther
     */
    EVENT_CHECK_INTERRUPT,
    /**
     * Bring the display up to date and put the cursor where it belongs. The most frequent event
     * there is: C signals it around every command in {@code process_player}.
     *
     * @author Rowan Crowther
     */
    EVENT_REFRESH,
    /**
     * A new level has been generated and the whole display should be rebuilt for it.
     *
     * @author Rowan Crowther
     */
    EVENT_NEW_LEVEL_DISPLAY,
    /**
     * The command about to run is a repeat of the last one rather than something the player has
     * just typed.
     *
     * @author Rowan Crowther
     */
    EVENT_COMMAND_REPEAT,
    /**
     * Advance any animation the display is running — the flickering of multi-hued monsters and
     * the like. Signalled as the world takes its turns.
     *
     * @author Rowan Crowther
     */
    EVENT_ANIMATE,
    /**
     * The player has died and is using the cheat-death option to carry on regardless.
     *
     * @author Rowan Crowther
     */
    EVENT_CHEAT_DEATH,

    /**
     * New status message for initialisation. Carries the line to show as an
     * {@link uk.co.jackoftrades.channel.messages.data.EventDataString}.
     *
     * @author Rowan Crowther
     */
    EVENT_INITSTATUS,
    /**
     * Change in the birth points. Carries an
     * {@link uk.co.jackoftrades.channel.messages.data.EventDataBirthPoints}.
     *
     * @author Rowan Crowther
     */
    EVENT_BIRTHPOINTS,

    /* Changing of the game state/context. */

    /**
     * Loading of the game's data files is starting.
     *
     * @author Rowan Crowther
     */
    EVENT_ENTER_INIT,
    /**
     * Loading of the game's data files has finished.
     *
     * @author Rowan Crowther
     */
    EVENT_LEAVE_INIT,
    /**
     * Character creation is starting.
     *
     * @author Rowan Crowther
     */
    EVENT_ENTER_BIRTH,
    /**
     * Character creation has finished.
     *
     * @author Rowan Crowther
     */
    EVENT_LEAVE_BIRTH,
    /**
     * A play session is starting. Brackets the whole session, outside
     * {@link #EVENT_ENTER_WORLD}.
     *
     * @author Rowan Crowther
     */
    EVENT_ENTER_GAME,
    /**
     * The play session is ending.
     *
     * @author Rowan Crowther
     */
    EVENT_LEAVE_GAME,
    /**
     * The player is in the dungeon proper, with the map on screen. Unlike
     * {@link #EVENT_ENTER_GAME} this can happen several times in a session: entering a store
     * leaves the world and closing it enters again.
     *
     * @author Rowan Crowther
     */
    EVENT_ENTER_WORLD,
    /**
     * The player has left the dungeon view, for a store or for the end of the session.
     *
     * @author Rowan Crowther
     */
    EVENT_LEAVE_WORLD,
    /**
     * The store screen is opening.
     *
     * @author Rowan Crowther
     */
    EVENT_ENTER_STORE,
    /**
     * The store screen is closing.
     *
     * @author Rowan Crowther
     */
    EVENT_LEAVE_STORE,
    /**
     * The death screens are starting.
     *
     * @author Rowan Crowther
     */
    EVENT_ENTER_DEATH,
    /**
     * The death screens have finished.
     *
     * @author Rowan Crowther
     */
    EVENT_LEAVE_DEATH,

    /* Events for introspection into dungeon generation */

    /**
     * Level generation is starting. Has string in event data for profile name.
     *
     * @author Rowan Crowther
     */
    EVENT_GEN_LEVEL_START,
    /**
     * Level generation has finished. Has flag in event data indicating success.
     *
     * @author Rowan Crowther
     */
    EVENT_GEN_LEVEL_END,
    /**
     * A room is being built. Has string in event data for room type.
     *
     * @author Rowan Crowther
     */
    EVENT_GEN_ROOM_START,
    /**
     * The room's extent has been chosen. Has size in event data.
     *
     * @author Rowan Crowther
     */
    EVENT_GEN_ROOM_CHOOSE_SIZE,
    /**
     * The room's variety has been chosen. Has string in event data with name.
     *
     * @author Rowan Crowther
     */
    EVENT_GEN_ROOM_CHOOSE_SUBTYPE,
    /**
     * The room is finished. Has flag in event data indicating success.
     *
     * @author Rowan Crowther
     */
    EVENT_GEN_ROOM_END,
    /**
     * A tunnel is finished. Has tunnel in event data with results.
     *
     * @author Rowan Crowther
     */
    EVENT_GEN_TUNNEL_FINISHED,

    /**
     * Can be sent at the end of a series of events. Not an event in its own right: C uses it as
     * the terminator of the {@code game_event_type} array and sizes
     * {@code N_GAME_EVENTS} from it.
     *
     * @author Rowan Crowther
     */
    EVENT_END
}
