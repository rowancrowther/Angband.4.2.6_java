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
import uk.co.jackoftrades.middle.game.enums.CommandContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A single queued command: what to do, where, how many times, and with which arguments - the port
 * of C's {@code struct command} (cmd-core.h). Instances flow through the command queue; the game
 * engine pops one, looks its {@link #code} up in the dispatch table, and runs the handler, which
 * pulls whatever it needs from {@link #arg}.
 *
 * @author Rowan Crowther
 */
public class Command {
    /**
     * The context this command was issued in (splash, birth, game, store, death).
     */
    CommandContext context;

    /**
     * Which command to perform.
     */
    CommandCode code;

    /**
     * How many times to attempt to repeat this command.
     */
    int nrepeats;

    /**
     * Tri-state flag governing repetition and the bloodlust coercion check, carried over verbatim
     * from C:
     * <ul>
     *   <li>{@code 0} - may be the target of {@code CMD_REPEAT}, and may trigger bloodlust;</li>
     *   <li>{@code 1} - may not be repeated, but may trigger bloodlust;</li>
     *   <li>{@code >1} - may not be repeated, and does not trigger bloodlust.</li>
     * </ul>
     * The engine tests {@code background_command > 1} to decide whether to skip the bloodlust
     * (berserk-attack) substitution, which is why this is a small counter rather than a boolean.
     */
    int background_command;

    /**
     * This command's arguments, matched by {@link CommandArgument#getName() name} rather than by
     * position (C used a fixed {@code arg[CMD_MAX_ARGS]} array of four; a list is used here since
     * lookup is by name). Initialised empty so arguments can be added before the command runs.
     */
    List<CommandArgument> arg = new ArrayList<>();

    public Command(CommandContext context, CommandCode code, int nrepeats, int background_command, List<CommandArgument> arg) {
        this.context = context;
        this.code = code;
        this.nrepeats = nrepeats;
        this.background_command = background_command;
        this.arg = arg;
    }

    public Command clone() {
        return new Command(this.context, this.code, this.nrepeats, this.background_command,
                new ArrayList<>(arg));
    }
}
