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
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.middle.game.enums.CommandContext;
import uk.co.jackoftrades.middle.game.gameengine.argumentdata.ArgumentItem;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;

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
     * Whether the most recent command permits being repeated - the port of C's
     * {@code repeat_prev_allowed}. {@link CommandProcessor#processCommand} sets it true on every
     * execution, and a handler (or {@link #disableRepeat()} / {@link #disableRepeatFloorItem()}) may
     * clear it; {@link #push(Command)} consults it as the gate a {@code CMD_REPEAT} must pass before
     * it will replay {@link #lastCommand}.
     */
    private boolean repeatPrevAllowed = false;

    /**
     * Whether an auto-repeat is currently in flight - the port of C's {@code repeating}. When set,
     * {@link #commandPop} replays {@link #lastCommand} instead of consuming a fresh command from the
     * queue. {@link #setRepeat(int)} arms it (positive count) and disarms it (zero), and
     * {@link #cancelRepeat()} clears it.
     */
    private boolean repeating = false;

    /**
     * The pending commands, oldest at the front.
     */
    private final ArrayDeque<Command> commandQueue = new ArrayDeque<>();

    /**
     * The last non-background command handed out by {@link #getNextCommand}, retained so
     * {@code CMD_REPEAT} can replay it after the queue has drained. Null until one has been executed.
     */
    private Command lastCommand = null;

    /**
     * The command {@link #commandPop} is currently executing - the deque-world stand-in for C's
     * {@code cmd_queue[prev_cmd_idx(cmd_tail)]}. C's pop only advances an index, so the executing
     * command stays reachable in the ring buffer; this port's {@link #getNextCommand} removes it from
     * the deque, so a reference is held here instead. {@link #setRepeat(int)}, {@link #getNrepeats()}
     * and {@link #cancelRepeat()} all target this command. Null until the first {@link #commandPop}.
     */
    private Command currentCommand = null;

    private Player player;

    public CommandQueue(Player player) {
        this.player = player;
    }

    public void setPlayer(Player mainPlayer) {
        this.player = mainPlayer;
    }

    /**
     * Removes and returns the next command to carry out, or {@code null} if the queue is empty -
     * the port of C's {@code cmdq_pop} dequeue step (the dispatch itself lives in
     * process. A non-background command handed out here is recorded as
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
        if (nextCommand.getBackground_command() == 0)
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
        if (lastCommand == null || lastCommand.getCode() == CommandCode.CMD_NULL)
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
        if (cmd.getCode() != CommandCode.CMD_REPEAT) { // an ordinary command, not the CMD_REPEAT marker
            commandQueue.addLast(cmd);
            return true;
        } else if (!repeatPrevAllowed) // repeating is not currently permitted
            return false;
        if (lastCommand == null || lastCommand.getCode() == CommandCode.CMD_NULL) // nothing to repeat
            return false;
        commandQueue.addLast(lastCommand.clone());
        return true;
    }

    /**
     * Carries out every queued command right now, without waiting for input - the port of C's
     * {@code cmdq_execute}. Pops and dispatches in a loop until {@link #commandPop} reports the
     * queue is empty.
     *
     * @param context the context to carry the commands out in
     */
    public void execute(CommandContext context) {
        while (commandPop(context)) ;
    }

    /**
     * Discards all pending commands - the port of C's {@code cmdq_flush} (which sets
     * {@code cmd_tail = cmd_head}). Leaves the remembered {@link #lastCommand} untouched, so a
     * later {@code CMD_REPEAT} can still replay it; use {@link #release()} to forget that too.
     */
    public void flush() {
        commandQueue.clear();
    }

    /**
     * Empties the queue <em>and</em> forgets the last executed command - the port of C's
     * {@code cmdq_release}. This is {@link #flush()} plus clearing {@link #lastCommand}, so no
     * {@code CMD_REPEAT} can replay across the release (C resets {@code last_command}/
     * {@code last_command_idx} for the same reason). C's per-slot string frees have no counterpart
     * here - garbage collection reclaims the commands.
     */
    public void release() {
        flush();
        lastCommand = null;
    }

    /**
     * Pops the next command and carries it out - the port of C's {@code cmdq_pop}, combining the
     * dequeue with dispatch via {@link CommandProcessor#processCommand}.
     *
     * <p>When {@link #repeating} is set, it replays {@link #lastCommand} without touching the queue
     * (so an in-flight auto-repeat re-runs the same command). Otherwise it consumes the next command
     * from the front via {@link #getNextCommand}. Either way, if there is nothing to run it returns
     * {@code false} without dispatching - which is the game loop's signal to stop and wait for input.
     *
     * @param commandContext the context to carry the command out in
     * @return {@code true} if a command was processed, {@code false} if none was available
     */
    public boolean commandPop(CommandContext commandContext) {
        Command cmd;

        if (repeating) {
            if (lastCommand == null) return false;
            cmd = lastCommand;
        } else {
            cmd = getNextCommand();
            if (cmd == null) return false;
        }
        currentCommand = cmd;

        CommandProcessor.processCommand(commandContext, cmd, this);
        return true;
    }

    /**
     * Sets the repeat count on the currently executing command and flags the state line for
     * redraw - the port of C's {@code cmd_set_repeat}.
     *
     * <p>Like C (which targets {@code cmd_queue[prev_cmd_idx(cmd_tail)]}), this applies to the
     * command {@link #commandPop} is running, held here as {@link #currentCommand}; it is a no-op if
     * nothing is executing. A positive count turns {@link #repeating} on, zero turns it off, and
     * either way the {@code PR_STATE} redraw bit is set so the on-screen state indicator refreshes.
     *
     * @param numberOfRepeats the repeat count to apply
     */
    public void setRepeat(int numberOfRepeats) {
        if (currentCommand == null) return;

        currentCommand.setNrepeats(numberOfRepeats);
        if (numberOfRepeats > 0)
            repeating = true;
        else
            repeating = false;

        player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_STATE);
    }

    /**
     * Sets whether an auto-repeat is in flight (see {@link #repeating}).
     *
     * @param repeating {@code true} while replaying a command, {@code false} otherwise
     */
    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    /**
     * Sets whether the most recent command may be repeated (see {@link #repeatPrevAllowed}).
     *
     * @param repeatPrevAllowed {@code true} to permit {@code CMD_REPEAT} to replay it
     */
    public void setRepeatPrevAllowed(boolean repeatPrevAllowed) {
        this.repeatPrevAllowed = repeatPrevAllowed;
    }

    /**
     * Cancels any pending repeats on the currently executing command - the port of C's
     * {@code cmd_cancel_repeat}.
     *
     * <p>When the command has repeats left <em>or</em> an auto-repeat is in flight, its count is
     * zeroed, {@link #repeating} is cleared, and the {@code PR_STATE} redraw bit is set. When there
     * is nothing to cancel (no count and not repeating) it does nothing, matching C's guard. It is
     * also a no-op if no command is executing; C never needs that check because its target slot
     * always exists, whereas {@link #currentCommand} is null until the first {@link #commandPop}.
     */
    public void cancelRepeat() {
        if (currentCommand == null) return;

        if (currentCommand.getNrepeats() != 0 || repeating) {
            currentCommand.setNrepeats(0);
            setRepeating(false);

            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_STATE);
        }
    }

    /**
     * Returns the number of repeats pending on the currently executing command - the port of C's
     * {@code cmd_get_nrepeats}. Queried both by the command loop and by outside code (C's world
     * processing and status line) to tell whether a repeat is in progress. Reports 0 when no command
     * is executing, where C would read its always-present target slot.
     *
     * @return the executing command's pending repeat count, or 0 if none is executing
     */
    public int getNrepeats() {
        if (currentCommand == null) return 0;
        return currentCommand.getNrepeats();
    }

    /**
     * Forbids the user from repeating the current command with {@code CMD_REPEAT} - the port of C's
     * {@code cmd_disable_repeat}. It simply shuts the {@link #repeatPrevAllowed} gate that
     * {@link #push(Command)} checks, so a subsequent {@code CMD_REPEAT} push is refused.
     */
    public void disableRepeat() {
        repeatPrevAllowed = false;
    }

    /**
     * Forbids repeating the current command if it acted on an item lying on the floor - the port of
     * C's {@code cmd_disable_repeat_floor_item}, a dangling-reference guard. A floor item may have
     * moved, merged, or been destroyed by the time a repeat would replay the command, leaving its
     * reference stale; inventory items are stable and stay repeatable.
     *
     * <p>If repeating is already disallowed it returns immediately, before inspecting any arguments -
     * matching C, whose comment is the point: those object references may already dangle, so they
     * must not be dereferenced once the gate is shut. Otherwise it walks the command's arguments and,
     * on the first {@code arg_ITEM} whose object is on the floor (a non-null, non-origin grid;
     * inventory items carry no grid, C's {@code (0,0)}), shuts the {@link #repeatPrevAllowed} gate.
     *
     * <p><b>Divergence from C.</b> C inspects {@code cmd_queue[cmd_head - 1]}, the last-<em>pushed</em>
     * command. This port inspects {@code [port]} {@link #currentCommand}, the <em>executing</em> one
     * (C's {@code prev_cmd_idx(cmd_tail)}). The two coincide in the common single-command case, and
     * {@link #currentCommand} is the command that actually holds the at-risk floor reference. The
     * literal port - {@code commandQueue.peekLast()} - would be wrong here: {@link #getNextCommand}
     * removes executed commands from the deque, so during a lone command's execution {@code peekLast}
     * is null and the guard would silently no-op exactly when it is needed.
     */
    public void disableRepeatFloorItem() {
        if (!repeatPrevAllowed) return;

        if (currentCommand == null) return;

        for (CommandArgument arg : currentCommand.getArgs()) {
            if (arg.getData() instanceof ArgumentItem argumentItem) {
                ItemObject obj = argumentItem.getValue();
                Loc grid = (obj == null) ? null : obj.getGrid();
                if (grid != null && !grid.isZero()) {
                    repeatPrevAllowed = false;
                    break;
                }
            }
        }
    }
}
