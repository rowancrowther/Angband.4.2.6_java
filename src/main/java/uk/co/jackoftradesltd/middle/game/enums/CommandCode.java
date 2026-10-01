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

package uk.co.jackoftradesltd.middle.game.enums;

/**
 * Every command the game engine can be asked to perform - the port of C's {@code enum cmd_code}
 * ({@code cmd-core.h}). Each constant names a discrete action: a splash-screen choice, a birth
 * step, a main-game action, a store transaction, a spoiler dump, or a wizard/debug command.
 *
 * <p>A command travels to the engine as a {@code CommandCode} plus its arguments; the engine looks
 * the code up in the dispatch table (see {@code CommandProcessor}) to find the handler that runs
 * it. Two things are worth knowing about the set:
 *
 * <ul>
 *   <li>{@link #CMD_NULL} is the "do nothing" sentinel - it exists so a UI has a value meaning
 *       "no command chosen yet", and it is deliberately never a dispatchable action. It is first
 *       here so its ordinal is 0, matching {@code CMD_NULL = 0} in C.</li>
 *   <li>Not every constant has a dispatch-table row. In C's {@code game_cmds[]} (in
 *       {@code cmd-core.c}) exactly three codes have none: {@link #CMD_NULL},
 *       {@link #CMD_BROWSE_SPELL} and {@link #CMD_IGNORE}, the last two being acted on from the
 *       UI's context menus. The Java table in {@code CommandProcessor} omits the same three.
 *       Presence here means "a code exists", not "the engine dispatches it".</li>
 * </ul>
 *
 * <p>The declaration order mirrors the C enum constant for constant, so {@link #ordinal()} equals
 * the C integer value. Do not reorder without a reason, since anything ordinal-sensitive relies on
 * the alignment. The one-line descriptions on each constant are the strings C's {@code game_cmds[]}
 * carries for it.
 *
 * <p>Class CommandCode coded before 260815, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public enum CommandCode {
    /**
     * The "do nothing" sentinel, value 0: what a UI holds when no command has been chosen yet. It
     * has no row in the dispatch table.
     */
    CMD_NULL,
    /*
     * Splash screen commands
     */
    /**
     * Command "load a savefile" - the {@code game_cmds[]} description in C's {@code cmd-core.c}.
     */
    CMD_LOADFILE,
    /** Command "start a new game" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_NEWGAME,

    /*
     * Birth commands
     */
    /**
     * Command "start the character birth process" - the {@code game_cmds[]} description in C's
     * {@code cmd-core.c}.
     */
    CMD_BIRTH_INIT,
    /**
     * Command "go back to the beginning" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_BIRTH_RESET,
    /** Command "select race" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_CHOOSE_RACE,
    /** Command "select class" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_CHOOSE_CLASS,
    /**
     * Command "buy points in a stat" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_BUY_STAT,
    /**
     * Command "sell points in a stat" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_SELL_STAT,
    /** Command "reset stats" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_RESET_STATS,
    /** Command "refresh stats" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_REFRESH_STATS,
    /** Command "roll new stats" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_ROLL_STATS,
    /**
     * Command "use previously rolled stats" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_PREV_STATS,
    /** Command "choose name" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_NAME_CHOICE,
    /** Command "write history" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_HISTORY_CHOICE,
    /** Command "accept character" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_ACCEPT_CHARACTER,

    /*
     * The main game commands
     */
    /** Command "go up stairs" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_GO_UP,
    /** Command "go down stairs" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_GO_DOWN,
    /** Command "walk" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WALK,
    /** Command "jump" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_JUMP,
    /** Command "walk" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_PATHFIND,

    /** Command "inscribe" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_INSCRIBE,
    /** Command "un-inscribe" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_UNINSCRIBE,
    /** Command "autoinscribe" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_AUTOINSCRIBE,
    /** Command "take off" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_TAKEOFF,
    /** Command "wear or wield" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIELD,
    /** Command "drop" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_DROP,
    /**
     * Browse a book's spells. A code only: C's {@code game_cmds[]} has no row for it, and it is
     * acted on from the UI (the object context menu in {@code ui-context.c}).
     */
    CMD_BROWSE_SPELL,
    /** Command "study" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_STUDY,
    /**
     * Cast a spell or pray - one command for both, as in C.
     */
    CMD_CAST,
    /** Command "use" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_USE_STAFF,
    /** Command "aim" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_USE_WAND,
    /** Command "zap" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_USE_ROD,
    /** Command "activate" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_ACTIVATE,
    /** Command "eat" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_EAT,
    /** Command "quaff" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_QUAFF,
    /** Command "read" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_READ_SCROLL,
    /** Command "refuel with" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_REFILL,
    /** Command "use" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_USE,
    /** Command "fire" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_FIRE,
    /** Command "throw" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_THROW,
    /** Command "pickup" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_PICKUP,
    /** Command "autopickup" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_AUTOPICKUP,
    /**
     * Ignore or unignore an item. A code only: C's {@code game_cmds[]} has no row for it, and it
     * is acted on from the UI (the context menus in {@code ui-context.c}).
     */
    CMD_IGNORE,
    /** Command "disarm" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_DISARM,
    /** Command "rest" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_REST,
    /** Command "tunnel" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_TUNNEL,
    /** Command "open" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_OPEN,
    /** Command "close" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_CLOSE,
    /** Command "run" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_RUN,
    /** Command "explore" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_EXPLORE,
    /** Command "navigate up" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_NAVIGATE_UP,
    /** Command "navigate down" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_NAVIGATE_DOWN,
    /** Command "stay still" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_HOLD,
    /** Command "alter" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_ALTER,
    /** Command "steal" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_STEAL,
    /** Command "sleep" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_SLEEP,

    /* Store commands */
    /** Command "sell" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_SELL,
    /** Command "buy" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_BUY,
    /** Command "stash" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_STASH,
    /** Command "retrieve" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_RETRIEVE,

    /* Spoiler commands */
    /**
     * Command "generate spoiler file for artifacts" - the {@code game_cmds[]} description in C's
     * {@code cmd-core.c}.
     */
    CMD_SPOIL_ARTIFACT,
    /**
     * Command "generate spoiler file for monsters" - the {@code game_cmds[]} description in C's
     * {@code cmd-core.c}.
     */
    CMD_SPOIL_MON,
    /**
     * Command "generate brief spoiler file for monsters" - the {@code game_cmds[]} description in
     * C's {@code cmd-core.c}.
     */
    CMD_SPOIL_MON_BRIEF,
    /**
     * Command "generate spoiler file for objects" - the {@code game_cmds[]} description in C's
     * {@code cmd-core.c}.
     */
    CMD_SPOIL_OBJ,

    /* Debugging commands */
    /** Command "acquire objects" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_ACQUIRE,
    /**
     * Command "make character powerful" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_ADVANCE,
    /**
     * Command "banish nearby monsters" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_BANISH,
    /**
     * Command "change number of an item" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_CHANGE_ITEM_QUANTITY,
    /**
     * Command "collect statistics about disconnected levels" - the {@code game_cmds[]} description
     * in C's {@code cmd-core.c}.
     */
    CMD_WIZ_COLLECT_DISCONNECT_STATS,
    /**
     * Command "collect object/monster statistics" - the {@code game_cmds[]} description in C's
     * {@code cmd-core.c}.
     */
    CMD_WIZ_COLLECT_OBJ_MON_STATS,
    /**
     * Command "collect pit statistics" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_COLLECT_PIT_STATS,
    /**
     * Command "create all artifacts" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_CREATE_ALL_ARTIFACT,
    /**
     * Command "create all artifacts of a tval" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_CREATE_ALL_ARTIFACT_FROM_TVAL,
    /**
     * Command "create all objects" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_CREATE_ALL_OBJ,
    /**
     * Command "create all objects of a tval" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_CREATE_ALL_OBJ_FROM_TVAL,
    /** Command "create artifact" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_CREATE_ARTIFACT,
    /** Command "create object" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_CREATE_OBJ,
    /** Command "create trap" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_CREATE_TRAP,
    /** Command "cure everything" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_CURE_ALL,
    /**
     * Command "change a curse on an item" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_CURSE_ITEM,
    /**
     * Command "detect everything nearby" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_DETECT_ALL_LOCAL,
    /**
     * Command "detect all monsters" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_DETECT_ALL_MONSTERS,
    /**
     * Command "display keystroke log" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_DISPLAY_KEYLOG,
    /**
     * Command "write map of level" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_DUMP_LEVEL_MAP,
    /**
     * Command "change the player's experience" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_EDIT_PLAYER_EXP,
    /**
     * Command "change the player's gold" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_EDIT_PLAYER_GOLD,
    /**
     * Command "start editing the player" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_EDIT_PLAYER_START,
    /**
     * Command "edit one of the player's stats" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_EDIT_PLAYER_STAT,
    /**
     * Command "hit all monsters in LOS" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_HIT_ALL_LOS,
    /**
     * Command "increase experience" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_INCREASE_EXP,
    /** Command "jump to a level" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_JUMP_LEVEL,
    /**
     * Command "learn about kinds of objects" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_LEARN_OBJECT_KINDS,
    /** Command "map local area" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_MAGIC_MAP,
    /**
     * Command "peek at noise and scent" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_PEEK_NOISE_SCENT,
    /** Command "perform an effect" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_PERFORM_EFFECT,
    /** Command "play with item" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_PLAY_ITEM,
    /**
     * Command "push objects from square" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_PUSH_OBJECT,
    /**
     * Command "highlight specific feature" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_QUERY_FEATURE,
    /** Command "query square flag" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_QUERY_SQUARE_FLAG,
    /**
     * Command "quit without saving" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_QUIT_NO_SAVE,
    /** Command "recall monster" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_RECALL_MONSTER,
    /** Command "rerate hitpoints" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_RERATE,
    /** Command "reroll an item" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_REROLL_ITEM,
    /**
     * Command "get statistics for an item" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_STAT_ITEM,
    /**
     * Command "summon specific monster" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_SUMMON_NAMED,
    /**
     * Command "summon random monsters" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_SUMMON_RANDOM,
    /** Command "teleport" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_WIZ_TELEPORT_RANDOM,
    /**
     * Command "teleport to location" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_TELEPORT_TO,
    /**
     * Command "modify item attributes" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_TWEAK_ITEM,
    /**
     * Command "erase monster recall" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_WIPE_RECALL,
    /**
     * Command "wizard light the level" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_WIZ_WIZARD_LIGHT,

    /* Hors categorie Commands */
    /** Command "retire character" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_RETIRE,

    /** Command "help" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_HELP,
    /** Command "repeat" - the {@code game_cmds[]} description in C's {@code cmd-core.c}. */
    CMD_REPEAT,
    /**
     * Command "make a monster act" - the {@code game_cmds[]} description in C's {@code
     * cmd-core.c}.
     */
    CMD_COMMAND_MONSTER
}
