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

package uk.co.jackoftrades.middle.game.enums;

/**
 * Every command the game engine can be asked to perform - the port of C's {@code enum cmd_code}
 * (cmd-core.h). Each constant names a discrete action: a splash-screen choice, a birth step, a
 * main-game action, a store transaction, a spoiler dump, or a wizard/debug command.
 *
 * <p>A command travels through the queue as a {@code CommandCode} plus its arguments; the engine
 * looks the code up in the dispatch table (see {@code CommandProcessor}) to find the handler that
 * runs it. Two things are worth knowing about the set:
 *
 * <ul>
 *   <li>{@link #CMD_NULL} is the "do nothing" sentinel - it exists so a UI has a value meaning
 *       "no command chosen yet", and it is deliberately never a dispatchable action. It is first
 *       here so its ordinal is 0, matching {@code CMD_NULL = 0} in C.</li>
 *   <li>Not every constant has a dispatch-table row. Some (e.g. {@code CMD_BROWSE_SPELL},
 *       {@code CMD_IGNORE}) are recognised as codes but handled elsewhere upstream, exactly as in
 *       C's {@code game_cmds[]}. Presence here means "a code exists", not "the engine dispatches
 *       it".</li>
 * </ul>
 *
 * <p>The declaration order mirrors the C enum so the two stay easy to diff; do not reorder
 * without a reason, since anything ordinal-sensitive relies on the alignment.
 *
 * @author Rowan Crowther
 */
public enum CommandCode {
    CMD_NULL,	/* A "do nothing" command so that there's something
				   UIs can use as a "no command yet" sentinel. */
    /*
     * Splash screen commands
     */
    CMD_LOADFILE,
    CMD_NEWGAME,

    /*
     * Birth commands
     */
    CMD_BIRTH_INIT,
    CMD_BIRTH_RESET,
    CMD_CHOOSE_RACE,
    CMD_CHOOSE_CLASS,
    CMD_BUY_STAT,
    CMD_SELL_STAT,
    CMD_RESET_STATS,
    CMD_REFRESH_STATS,
    CMD_ROLL_STATS,
    CMD_PREV_STATS,
    CMD_NAME_CHOICE,
    CMD_HISTORY_CHOICE,
    CMD_ACCEPT_CHARACTER,

    /*
     * The main game commands
     */
    CMD_GO_UP,
    CMD_GO_DOWN,
    CMD_WALK,
    CMD_JUMP,
    CMD_PATHFIND,

    CMD_INSCRIBE,
    CMD_UNINSCRIBE,
    CMD_AUTOINSCRIBE,
    CMD_TAKEOFF,
    CMD_WIELD,
    CMD_DROP,
    CMD_BROWSE_SPELL,
    CMD_STUDY,
    CMD_CAST, /* Casting a spell /or/ praying. */
    CMD_USE_STAFF,
    CMD_USE_WAND,
    CMD_USE_ROD,
    CMD_ACTIVATE,
    CMD_EAT,
    CMD_QUAFF,
    CMD_READ_SCROLL,
    CMD_REFILL,
    CMD_USE,
    CMD_FIRE,
    CMD_THROW,
    CMD_PICKUP,
    CMD_AUTOPICKUP,
    CMD_IGNORE,
    CMD_DISARM,
    CMD_REST,
    CMD_TUNNEL,
    CMD_OPEN,
    CMD_CLOSE,
    CMD_RUN,
    CMD_EXPLORE,
    CMD_NAVIGATE_UP,
    CMD_NAVIGATE_DOWN,
    CMD_HOLD,
    CMD_ALTER,
    CMD_STEAL,
    CMD_SLEEP,

    /* Store commands */
    CMD_SELL,
    CMD_BUY,
    CMD_STASH,
    CMD_RETRIEVE,

    /* Spoiler commands */
    CMD_SPOIL_ARTIFACT,
    CMD_SPOIL_MON,
    CMD_SPOIL_MON_BRIEF,
    CMD_SPOIL_OBJ,

    /* Debugging commands */
    CMD_WIZ_ACQUIRE,
    CMD_WIZ_ADVANCE,
    CMD_WIZ_BANISH,
    CMD_WIZ_CHANGE_ITEM_QUANTITY,
    CMD_WIZ_COLLECT_DISCONNECT_STATS,
    CMD_WIZ_COLLECT_OBJ_MON_STATS,
    CMD_WIZ_COLLECT_PIT_STATS,
    CMD_WIZ_CREATE_ALL_ARTIFACT,
    CMD_WIZ_CREATE_ALL_ARTIFACT_FROM_TVAL,
    CMD_WIZ_CREATE_ALL_OBJ,
    CMD_WIZ_CREATE_ALL_OBJ_FROM_TVAL,
    CMD_WIZ_CREATE_ARTIFACT,
    CMD_WIZ_CREATE_OBJ,
    CMD_WIZ_CREATE_TRAP,
    CMD_WIZ_CURE_ALL,
    CMD_WIZ_CURSE_ITEM,
    CMD_WIZ_DETECT_ALL_LOCAL,
    CMD_WIZ_DETECT_ALL_MONSTERS,
    CMD_WIZ_DISPLAY_KEYLOG,
    CMD_WIZ_DUMP_LEVEL_MAP,
    CMD_WIZ_EDIT_PLAYER_EXP,
    CMD_WIZ_EDIT_PLAYER_GOLD,
    CMD_WIZ_EDIT_PLAYER_START,
    CMD_WIZ_EDIT_PLAYER_STAT,
    CMD_WIZ_HIT_ALL_LOS,
    CMD_WIZ_INCREASE_EXP,
    CMD_WIZ_JUMP_LEVEL,
    CMD_WIZ_LEARN_OBJECT_KINDS,
    CMD_WIZ_MAGIC_MAP,
    CMD_WIZ_PEEK_NOISE_SCENT,
    CMD_WIZ_PERFORM_EFFECT,
    CMD_WIZ_PLAY_ITEM,
    CMD_WIZ_PUSH_OBJECT,
    CMD_WIZ_QUERY_FEATURE,
    CMD_WIZ_QUERY_SQUARE_FLAG,
    CMD_WIZ_QUIT_NO_SAVE,
    CMD_WIZ_RECALL_MONSTER,
    CMD_WIZ_RERATE,
    CMD_WIZ_REROLL_ITEM,
    CMD_WIZ_STAT_ITEM,
    CMD_WIZ_SUMMON_NAMED,
    CMD_WIZ_SUMMON_RANDOM,
    CMD_WIZ_TELEPORT_RANDOM,
    CMD_WIZ_TELEPORT_TO,
    CMD_WIZ_TWEAK_ITEM,
    CMD_WIZ_WIPE_RECALL,
    CMD_WIZ_WIZARD_LIGHT,

    /* Hors categorie Commands */
    CMD_RETIRE,

    CMD_HELP,
    CMD_REPEAT,
    CMD_COMMAND_MONSTER
}
