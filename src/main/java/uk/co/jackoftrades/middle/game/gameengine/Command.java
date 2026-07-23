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
 * pulls whatever it needs from {@link #args}.
 *
 * @author Rowan Crowther
 */
public class Command {
    /**
     * The context this command was issued in (splash, birth, game, store, death).
     */
    private CommandContext context;

    /**
     * Which command to perform.
     */
    private CommandCode code;

    /**
     * How many times to attempt to repeat this command.
     */
    private int nrepeats;

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
    private int background_command;

    /**
     * This command's arguments, matched by {@link CommandArgument#getName() name} rather than by
     * position (C used a fixed {@code arg[CMD_MAX_ARGS]} array of four; a list is used here since
     * lookup is by name). Initialised empty so arguments can be added before the command runs.
     */
    private List<CommandArgument> args = new ArrayList<>();

    /**
     * Creates a command.
     *
     * @param context            the context the command is issued in
     * @param code               which command to perform
     * @param nrepeats           how many times to attempt to repeat it
     * @param background_command the tri-state repeat/bloodlust flag (see {@link #background_command})
     * @param arg                the command's arguments (matched by name)
     */
    public Command(CommandContext context, CommandCode code, int nrepeats, int background_command, List<CommandArgument> arg) {
        this.context = context;
        this.code = code;
        this.nrepeats = nrepeats;
        this.background_command = background_command;
        this.args = arg;
    }

    /**
     * Returns an independent copy of this command - the port of the copy that C's {@code cmd_copy}
     * makes when it duplicates a command into the queue.
     *
     * <p>The scalar fields are copied and a fresh {@link ArrayList} wraps the arguments, so the two
     * commands can be executed and mutated (e.g. their {@code nrepeats} decremented) without
     * affecting one another. The {@link CommandArgument} elements themselves are shared, not deep
     * copied: their payloads are effectively immutable, so there is nothing to protect against -
     * which is why C only ever deep-copied string arguments, for heap ownership, not the rest.
     *
     * <p>Used by {@link CommandQueue}'s {@code CMD_REPEAT} handling, where the replayed command must
     * be independent of the retained lastCommand.
     *
     * @return an independent copy of this command
     */
    public Command clone() {
        return new Command(this.context, this.code, this.nrepeats, this.background_command,
                new ArrayList<>(args));
    }

    /**
     * @return the context this command is being carried out in
     */
    public CommandContext getContext() {
        return context;
    }

    /**
     * Sets the context this command runs in, stamped on at execution - the port of C's
     * {@code cmd->context = ctx}.
     *
     * @param context the execution context
     */
    public void setContext(CommandContext context) {
        this.context = context;
    }

    /**
     * @return which command this is
     */
    public CommandCode getCode() {
        return code;
    }

    /**
     * @return how many times this command is still to attempt to repeat
     */
    public int getNrepeats() {
        return nrepeats;
    }

    /**
     * Sets the remaining repeat count.
     *
     * @param nrepeats the number of repeats to attempt
     */
    public void setNrepeats(int nrepeats) {
        this.nrepeats = nrepeats;
    }

    /**
     * Records one execution against the repeat count by decrementing it.
     */
    public void repeated() {
        nrepeats--;
    }

    /**
     * @return the tri-state repeat/bloodlust flag (see {@link #background_command})
     */
    public int getBackground_command() {
        return background_command;
    }

    /**
     * @return this command's arguments (matched by name)
     */
    public List<CommandArgument> getArgs() {
        return args;
    }
}
