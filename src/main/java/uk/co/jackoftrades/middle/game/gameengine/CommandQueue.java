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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.middle.game.enums.CommandContext;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * The FIFO queue of commands waiting to be carried out - the port of the command-queue half of C's
 * cmd-core.c ({@code cmd_queue} plus its push/pop functions).
 *
 * <p>Callers {@link #push} commands onto the back and the game loop pulls them off the front with
 * {@link #getNextCommand}. Because Java has references and a growable {@link ArrayDeque}, none of
 * C's ring-buffer machinery is reproduced: there is no fixed size, no head/tail indices, and no
 * wraparound. The one piece of real behaviour that survives from C is repeat support - the queue
 * remembers the last non-background command it handed out so that {@code CMD_REPEAT} can replay it.
 *
 * <p>Where C juggled two states for that ({@code last_command_idx} for "still in the queue" and
 * {@code last_command} for "copied out before the slot was reused"), this needs only a single
 * {@link #lastCommand} reference, since a {@link Command} object outlives its position in the deque.
 *
 * @author Rowan Crowther
 */
public class CommandQueue {
    /**
     * Whether the most recent command permits being repeated. C sets its {@code repeat_prev_allowed}
     * true in {@code process_command} on each execution and lets a handler clear it; until that
     * function is ported this stays false, so the {@code CMD_REPEAT} path is structurally present
     * but cannot yet fire.
     */
    private boolean repeatPrevAllowed = false;

    /**
     * The pending commands, oldest at the front.
     */
    private final ArrayDeque<Command> commandQueue = new ArrayDeque<>();

    /**
     * The last non-background command handed out by {@link #getNextCommand}, retained so
     * {@code CMD_REPEAT} can replay it after the queue has drained. Null until one has been executed.
     */
    private Command lastCommand = null;

    public CommandQueue() {
    }

    /**
     * Removes and returns the next command to carry out, or {@code null} if the queue is empty -
     * the port of C's {@code cmdq_pop} dequeue step (the dispatch itself lives in
     * {@link CommandProcessor#process}). A non-background command handed out here is recorded as
     * {@link #lastCommand}; background commands deliberately do not update it, matching C's
     * {@code if (!cmd->background_command)} guard.
     *
     * @return the next command, or {@code null} if none remains
     */
    public Command getNextCommand() {
        Command nextCommand = commandQueue.pollFirst();
        if (nextCommand == null) {
            return null;
        }
        if (nextCommand.background_command == 0)
            lastCommand = nextCommand;
        return nextCommand;
    }

    /**
     * Returns the command {@code CMD_REPEAT} would replay - the last non-background command executed
     * - without removing or re-queueing anything.
     *
     * @return the last executed non-background command, or {@code null} if none is available
     */
    public Command viewPrevCommand() {
        if (lastCommand == null || lastCommand.code == CommandCode.CMD_NULL)
            return null;
        return lastCommand;
    }

    /**
     * Builds an argument-free command from a code and repeat count and queues it - the port of C's
     * {@code cmdq_push_repeat}. The code is validated against the dispatch table first; an unknown
     * code (one with no {@link CommandInfo} row) is rejected rather than queued.
     *
     * @param command         the command code to queue
     * @param numberOfRepeats how many times the command should attempt to repeat
     * @return {@code true} if queued, {@code false} if the code is not dispatchable or the queue
     * rejected it
     */
    public boolean push(@NotNull CommandCode command, int numberOfRepeats) {
        if (!CommandProcessor.containsCommand(command)) return false;
        List<CommandArgument> arg = new ArrayList<>();
        Command cmd = new Command(CommandContext.CTX_INIT, command, numberOfRepeats, 0, arg);
        return push(cmd);
    }

    /**
     * Queues an argument-free command with no repeats - the port of C's {@code cmdq_push}, and the
     * common entry point. Shorthand for {@link #push(CommandCode, int) push(cmd, 0)}.
     *
     * @param cmd the command code to queue
     * @return {@code true} if queued, {@code false} if the code is not dispatchable
     */
    public boolean push(CommandCode cmd) {
        return push(cmd, 0);
    }

    /**
     * Queues a fully-formed command - the port of C's {@code cmdq_push_copy}, and the worker the
     * other overloads delegate to.
     *
     * <p>An ordinary command is simply appended to the back of the queue. {@code CMD_REPEAT} is the
     * exception: rather than queueing the repeat marker itself, it re-queues the last non-background
     * command ({@link #lastCommand}). That replay is gated on {@link #repeatPrevAllowed}, needs a
     * command to actually replay, and enqueues an independent {@link Command#clone() clone} so a
     * later execution cannot mutate the retained original.
     *
     * @param cmd the command to queue
     * @return {@code true} if something was queued, {@code false} if a repeat could not be honoured
     */
    public boolean push(@NotNull Command cmd) {
        if (cmd.code != CommandCode.CMD_REPEAT) { // an ordinary command, not the CMD_REPEAT marker
            commandQueue.addLast(cmd);
            return true;
        } else if (!repeatPrevAllowed) // repeating is not currently permitted
            return false;
        if (lastCommand == null || lastCommand.code == CommandCode.CMD_NULL) // nothing to repeat
            return false;
        commandQueue.addLast(lastCommand.clone());
        return true;
    }
}
