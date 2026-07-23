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

package uk.co.jackoftrades.middle.game.gameengine;

import uk.co.jackoftrades.middle.game.enums.CommandCode;

import java.util.EnumMap;
import java.util.Map;

import static uk.co.jackoftrades.middle.game.enums.CommandCode.*;

/**
 * Dispatches commands to their handlers - the port of the command-execution core in C's
 * cmd-core.c, and the owner of the {@code game_cmds[]} table.
 *
 * <p>The table maps each {@link CommandCode} to the {@link CommandInfo} describing it (handler plus
 * repeat/energy flags). As in C it is closed and known in full at build time, so it is a private
 * static field rather than an open registry other code contributes to. An {@link EnumMap} is used
 * so lookup is an array index on the code's ordinal - the direct analogue of C indexing
 * {@code game_cmds[]}.
 *
 * @author Rowan Crowther
 */
public class CommandProcessor {
    /**
     * The dispatch table: every dispatchable command code mapped to its {@link CommandInfo}.
     */
    private static Map<CommandCode, CommandInfo> gameCommands = buildTable();

    /**
     * Builds the dispatch table, one entry per command that the engine dispatches here. Codes that
     * exist in {@link CommandCode} but are handled elsewhere are deliberately absent, matching C's
     * {@code game_cmds[]}. Handlers are left {@code null} until their subsystems are ported.
     *
     * @return the fully populated command table
     */
    private static Map<CommandCode, CommandInfo> buildTable() {
        final Map<CommandCode, CommandInfo> map = new EnumMap<>(CommandCode.class);

        put(map, new CommandInfo(CMD_LOADFILE, "load a savefile", null, false, false, 0));
        put(map, new CommandInfo(CMD_NEWGAME, "start a new game", null, false, false, 0));

        put(map, new CommandInfo(CMD_BIRTH_INIT, "start the character birth process", null, false, false, 0));
        put(map, new CommandInfo(CMD_BIRTH_RESET, "go back to the beginning", null, false, false, 0));
        put(map, new CommandInfo(CMD_CHOOSE_RACE, "select race", null, false, false, 0));
        put(map, new CommandInfo(CMD_CHOOSE_CLASS, "select class", null, false, false, 0));
        put(map, new CommandInfo(CMD_BUY_STAT, "buy points in a stat", null, false, false, 0));
        put(map, new CommandInfo(CMD_SELL_STAT, "sell points in a stat", null, false, false, 0));
        put(map, new CommandInfo(CMD_RESET_STATS, "reset stats", null, false, false, 0));
        put(map, new CommandInfo(CMD_REFRESH_STATS, "refresh stats", null, false, false, 0));
        put(map, new CommandInfo(CMD_ROLL_STATS, "roll new stats", null, false, false, 0));
        put(map, new CommandInfo(CMD_PREV_STATS, "use previously rolled stats", null, false, false, 0));
        put(map, new CommandInfo(CMD_NAME_CHOICE, "choose name", null, false, false, 0));
        put(map, new CommandInfo(CMD_HISTORY_CHOICE, "write history", null, false, false, 0));
        put(map, new CommandInfo(CMD_ACCEPT_CHARACTER, "accept character", null, false, false, 0));
        put(map, new CommandInfo(CMD_GO_UP, "go up stairs", null, false, true, 0));
        put(map, new CommandInfo(CMD_GO_DOWN, "go down stairs", null, false, true, 0));
        put(map, new CommandInfo(CMD_WALK, "walk", null, true, true, 0));
        put(map, new CommandInfo(CMD_RUN, "run", null, true, true, 0));
        put(map, new CommandInfo(CMD_EXPLORE, "explore", null, false, true, 0));
        put(map, new CommandInfo(CMD_NAVIGATE_UP, "navigate up", null, false, true, 0));
        put(map, new CommandInfo(CMD_NAVIGATE_DOWN, "navigate down", null, false, true, 0));
        put(map, new CommandInfo(CMD_JUMP, "jump", null, false, true, 0));
        put(map, new CommandInfo(CMD_OPEN, "open", null, true, true, 99));
        put(map, new CommandInfo(CMD_CLOSE, "close", null, true, true, 99));
        put(map, new CommandInfo(CMD_TUNNEL, "tunnel", null, true, true, 99));
        put(map, new CommandInfo(CMD_HOLD, "stay still", null, true, true, 0));
        put(map, new CommandInfo(CMD_DISARM, "disarm", null, true, true, 99));
        put(map, new CommandInfo(CMD_ALTER, "alter", null, true, true, 99));
        put(map, new CommandInfo(CMD_STEAL, "steal", null, false, true, 0));
        put(map, new CommandInfo(CMD_REST, "rest", null, false, true, 0));
        put(map, new CommandInfo(CMD_SLEEP, "sleep", null, false, true, 0));
        put(map, new CommandInfo(CMD_PATHFIND, "walk", null, false, true, 0));
        put(map, new CommandInfo(CMD_PICKUP, "pickup", null, false, true, 0));
        put(map, new CommandInfo(CMD_AUTOPICKUP, "autopickup", null, false, true, 0));
        put(map, new CommandInfo(CMD_WIELD, "wear or wield", null, false, true, 0));
        put(map, new CommandInfo(CMD_TAKEOFF, "take off", null, false, true, 0));
        put(map, new CommandInfo(CMD_DROP, "drop", null, false, true, 0));
        put(map, new CommandInfo(CMD_UNINSCRIBE, "un-inscribe", null, false, false, 0));
        put(map, new CommandInfo(CMD_AUTOINSCRIBE, "autoinscribe", null, false, false, 0));
        put(map, new CommandInfo(CMD_EAT, "eat", null, false, true, 0));
        put(map, new CommandInfo(CMD_QUAFF, "quaff", null, false, true, 0));
        put(map, new CommandInfo(CMD_USE_ROD, "zap", null, true, true, 99));
        put(map, new CommandInfo(CMD_USE_STAFF, "use", null, true, true, 99));
        put(map, new CommandInfo(CMD_USE_WAND, "aim", null, true, true, 99));
        put(map, new CommandInfo(CMD_READ_SCROLL, "read", null, false, true, 0));
        put(map, new CommandInfo(CMD_ACTIVATE, "activate", null, true, true, 99));
        put(map, new CommandInfo(CMD_REFILL, "refuel with", null, false, true, 0));
        put(map, new CommandInfo(CMD_FIRE, "fire", null, false, true, 0));
        put(map, new CommandInfo(CMD_THROW, "throw", null, false, true, 0));
        put(map, new CommandInfo(CMD_INSCRIBE, "inscribe", null, false, false, 0));
        put(map, new CommandInfo(CMD_STUDY, "study", null, false, true, 0));
        put(map, new CommandInfo(CMD_CAST, "cast", null, false, true, 0));
        put(map, new CommandInfo(CMD_SELL, "sell", null, false, false, 0));
        put(map, new CommandInfo(CMD_STASH, "stash", null, false, false, 0));
        put(map, new CommandInfo(CMD_BUY, "buy", null, false, false, 0));
        put(map, new CommandInfo(CMD_RETRIEVE, "retrieve", null, false, false, 0));
        put(map, new CommandInfo(CMD_USE, "use", null, true, true, 0));
        put(map, new CommandInfo(CMD_RETIRE, "retire character", null, false, false, 0));
        put(map, new CommandInfo(CMD_HELP, "help", null, false, false, 0));
        put(map, new CommandInfo(CMD_REPEAT, "repeat", null, false, false, 0));

        put(map, new CommandInfo(CMD_COMMAND_MONSTER, "make a monster act", null, false, true, 0));

        put(map, new CommandInfo(CMD_SPOIL_ARTIFACT, "generate spoiler file for artifacts", null, false, false, 0));
        put(map, new CommandInfo(CMD_SPOIL_MON, "generate spoiler file for monsters", null, false, false, 0));
        put(map, new CommandInfo(CMD_SPOIL_MON_BRIEF, "generate brief spoiler file for monsters", null, false, false, 0));
        put(map, new CommandInfo(CMD_SPOIL_OBJ, "generate spoiler file for objects", null, false, false, 0));

        put(map, new CommandInfo(CMD_WIZ_ACQUIRE, "acquire objects", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_ADVANCE, "make character powerful", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_BANISH, "banish nearby monsters", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CHANGE_ITEM_QUANTITY, "change number of an item", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_COLLECT_DISCONNECT_STATS, "collect statistics about disconnected levels", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_COLLECT_OBJ_MON_STATS, "collect object/monster statistics", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_COLLECT_PIT_STATS, "collect pit statistics", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CREATE_ALL_ARTIFACT, "create all artifacts", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CREATE_ALL_ARTIFACT_FROM_TVAL, "create all artifacts of a tval", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CREATE_ALL_OBJ, "create all objects", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CREATE_ALL_OBJ_FROM_TVAL, "create all objects of a tval", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CREATE_ARTIFACT, "create artifact", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CREATE_OBJ, "create object", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CREATE_TRAP, "create trap", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CURE_ALL, "cure everything", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_CURSE_ITEM, "change a curse on an item", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_DETECT_ALL_LOCAL, "detect everything nearby", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_DETECT_ALL_MONSTERS, "detect all monsters", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_DISPLAY_KEYLOG, "display keystroke log", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_DUMP_LEVEL_MAP, "write map of level", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_EDIT_PLAYER_EXP, "change the player's experience", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_EDIT_PLAYER_GOLD, "change the player's gold", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_EDIT_PLAYER_START, "start editing the player", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_EDIT_PLAYER_STAT, "edit one of the player's stats", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_HIT_ALL_LOS, "hit all monsters in LOS", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_INCREASE_EXP, "increase experience", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_JUMP_LEVEL, "jump to a level", null, false, true, 0));
        put(map, new CommandInfo(CMD_WIZ_LEARN_OBJECT_KINDS, "learn about kinds of objects", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_MAGIC_MAP, "map local area", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_PEEK_NOISE_SCENT, "peek at noise and scent", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_PERFORM_EFFECT, "perform an effect", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_PLAY_ITEM, "play with item", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_PUSH_OBJECT, "push objects from square", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_QUERY_FEATURE, "highlight specific feature", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_QUERY_SQUARE_FLAG, "query square flag", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_QUIT_NO_SAVE, "quit without saving", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_RECALL_MONSTER, "recall monster", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_RERATE, "rerate hitpoints", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_REROLL_ITEM, "reroll an item", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_STAT_ITEM, "get statistics for an item", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_SUMMON_NAMED, "summon specific monster", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_SUMMON_RANDOM, "summon random monsters", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_TELEPORT_RANDOM, "teleport", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_TELEPORT_TO, "teleport to location", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_TWEAK_ITEM, "modify item attributes", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_WIPE_RECALL, "erase monster recall", null, false, false, 0));
        put(map, new CommandInfo(CMD_WIZ_WIZARD_LIGHT, "wizard light the level", null, false, false, 0));

        return map;
    }

    public static boolean containsCommand(CommandCode key) {
        return gameCommands.containsKey(key);
    }

    public static CommandInfo getCommandInfo(CommandCode code) {
        return gameCommands.get(code);
    }

    /**
     * Executes a command by looking its code up in the table and invoking the handler. A command
     * whose code has no table entry, or whose entry has no handler yet, is silently ignored -
     * mirroring C's {@code if (game_cmds[idx].fn)} guard.
     *
     * @param cmd the command to execute
     */
    public void process(Command cmd) {
        CommandInfo info = gameCommands.get(cmd.code);
        if (info != null && info.function() != null)
            info.function().handle(cmd);
    }

    /**
     * Adds one entry to the table, keyed by the {@link CommandInfo}'s own
     * {@link CommandInfo#command() command}. Keying off the info itself keeps the call sites in
     * {@link #buildTable()} to a single argument and removes the chance of filing an entry under
     * the wrong code.
     *
     * @param map  the table being built
     * @param info the row to add
     */
    private static void put(Map<CommandCode, CommandInfo> map, CommandInfo info) {
        map.put(info.command(), info);
    }

    public static String getCommandVerb(CommandCode code) {
        CommandInfo info = gameCommands.get(code);
        return info.verb();
    }
}
