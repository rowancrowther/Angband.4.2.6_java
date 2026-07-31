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
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.outputtouser.Message;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.cave.enums.DirectionEnum;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.game.bespokeexceptions.CommandArgumentWrongTypeException;
import uk.co.jackoftrades.middle.game.enums.CommandArgumentType;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.middle.game.enums.CommandContext;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.game.gameengine.argumentdata.*;
import uk.co.jackoftrades.middle.gameinput.EffectChoice;
import uk.co.jackoftrades.middle.gameinput.GameInput;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.magic.MagicBook;
import uk.co.jackoftrades.middle.magic.MagicSpell;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.GetItemFlags;
import uk.co.jackoftrades.middle.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static uk.co.jackoftrades.middle.game.enums.CommandArgumentType.arg_CHOICE;

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
     * The player this command acts for, captured once at construction so {@link #getItem} can
     * consult shapechange state without another global lookup. In C the handlers reached the global
     * {@code player} directly; the port holds the reference on the command instead.
     */
    private Player player;

    /**
     * Creates a command and binds it to the current {@link GameState#getPlayer() player}.
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

        this.player = GameState.getPlayer();
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

    /**
     * Stores {@code value} as this command's {@code argName} target argument - the port of C's
     * {@code cmd_set_arg_target}. A target is a direction <em>code</em> (a real direction, or a
     * sentinel meaning "the current health-bar target"), which is why it is a distinct argument
     * variant even though C physically stored a target and a direction in the same union int.
     *
     * @param argName the name the argument is looked up by
     * @param value   the target code to store
     */
    public void setArgTarget(String argName, DirectionEnum value) {
        ArgumentTarget arg = new ArgumentTarget(value);
        setArg(argName, CommandArgumentType.arg_TARGET, arg);
    }

    /**
     * Stores {@code value} as this command's {@code argName} string argument - the port of C's
     * {@code cmd_set_arg_string}. C deep-copied the string here because the command owned the heap
     * buffer; a Java {@code String} is immutable, so the reference is stored as-is.
     *
     * @param argName the name the argument is looked up by
     * @param value   the string to store
     */
    public void setArgString(String argName, String value) {
        ArgumentString arg = new ArgumentString(value);
        setArg(argName, CommandArgumentType.arg_STRING, arg);
    }

    /**
     * Stores {@code value} as this command's {@code argName} point argument - the port of C's
     * {@code cmd_set_arg_point}. A point is a grid location on the map, distinct from a direction.
     *
     * @param argName the name the argument is looked up by
     * @param value   the grid location to store
     */
    public void setArgPoint(String argName, Loc value) {
        ArgumentPoint arg = new ArgumentPoint(value);
        setArg(argName, CommandArgumentType.arg_POINT, arg);
    }

    /**
     * Stores {@code value} as this command's {@code argName} number argument - the port of C's
     * {@code cmd_set_arg_number}. Used for plain counts and quantities (see {@link #getQuantity}).
     *
     * @param argName the name the argument is looked up by
     * @param value   the number to store
     */
    public void setArgNumber(String argName, int value) {
        ArgumentNumber arg = new ArgumentNumber(value);
        setArg(argName, CommandArgumentType.arg_NUMBER, arg);
    }

    /**
     * Stores {@code value} as this command's {@code argName} item argument - the port of C's
     * {@code cmd_set_arg_item}. The stored reference is the selected object itself.
     *
     * @param argName the name the argument is looked up by
     * @param value   the item to store
     */
    public void setArgItem(String argName, ItemObject value) {
        ArgumentItem arg = new ArgumentItem(value);
        setArg(argName, CommandArgumentType.arg_ITEM, arg);
    }

    /**
     * Stores {@code value} as this command's {@code argName} direction argument - the port of C's
     * {@code cmd_set_arg_direction}.
     *
     * @param argName the name the argument is looked up by
     * @param value   the direction to store
     */
    public void setArgDirection(String argName, DirectionEnum value) {
        ArgumentDirection arg = new ArgumentDirection(value);
        setArg(argName, CommandArgumentType.arg_DIRECTION, arg);
    }

    /**
     * Stores {@code value} as this command's {@code argName} choice argument - the port of C's
     * {@code cmd_set_arg_choice}. A choice is a menu selection index rather than a game quantity.
     *
     * @param argName the name the argument is looked up by
     * @param value   the choice index to store
     */
    public void setArgChoice(String argName, int value) {
        ArgumentChoice arg = new ArgumentChoice(value);
        setArg(argName, arg_CHOICE, arg);
    }

    /**
     * Shared back-end for the typed {@code setArg*} methods - the port of C's {@code cmd_set_arg}.
     * Stores {@code value} under {@code argName}, updating the existing argument in place when one
     * of that name is already present and otherwise appending a new one. C overwrote the matching
     * slot or filled the next free entry of its fixed {@code arg[]} array; the port matches by name
     * against a list instead, so the type tag and payload are kept in step via
     * {@link CommandArgument#update}.
     *
     * @param argName      the name the argument is looked up by
     * @param argumentType the type tag for the value
     * @param value        the payload to store
     */
    private void setArg(@NotNull String argName, @NotNull CommandArgumentType argumentType, @NotNull CommandArgumentData value) {
        CommandArgument argument = args.stream()
                .filter(a -> a.getName().equals(argName)).findFirst().orElse(null);

        if (argument == null) {
            args.add(new CommandArgument(argumentType, value, argName));
        } else {
            argument.update(argumentType, value, argName);
        }
    }

    /**
     * Reads this command's {@code argName} argument as a choice - the port of C's
     * {@code cmd_get_arg_choice}. C folded three outcomes into an {@code int} return code; the port
     * splits them: an empty {@link Optional} for an unset argument (C's {@code CMD_ARG_NOT_PRESENT})
     * and a {@link CommandArgumentWrongTypeException} for one that is set but holds another type
     * (C's {@code CMD_ARG_WRONG_TYPE}).
     *
     * @param argName the name to look up
     * @return the stored choice, or empty if no argument of that name is set
     * @throws CommandArgumentWrongTypeException if the argument is set but is not a choice
     */
    public Optional<Integer> getArgChoice(@NotNull String argName) {
        CommandArgument arg = findArg(argName);

        if (arg == null) return Optional.empty();
        if (arg.getData() instanceof ArgumentChoice(int value)) return Optional.of(value);
        throw new CommandArgumentWrongTypeException(
                argName + " requested as choice but stored as " + arg.getType());
    }

    /**
     * Reads this command's {@code argName} argument as a direction - the port of C's
     * {@code cmd_get_arg_direction}. Empty when unset; throws when set to another type.
     *
     * @param argName the name to look up
     * @return the stored direction, or empty if no argument of that name is set
     * @throws CommandArgumentWrongTypeException if the argument is set but is not a direction
     */
    public Optional<DirectionEnum> getArgDirection(@NotNull String argName) {
        CommandArgument arg = findArg(argName);

        if (arg == null) return Optional.empty();
        if (arg.getData() instanceof ArgumentDirection(DirectionEnum value))
            return Optional.of(value);
        throw new CommandArgumentWrongTypeException(
                argName + " requested as direction but stored as " + arg.getType());
    }

    /**
     * Reads this command's {@code argName} argument as an item - the port of C's
     * {@code cmd_get_arg_item}. Empty when unset; throws when set to another type.
     *
     * @param argName the name to look up
     * @return the stored item, or empty if no argument of that name is set
     * @throws CommandArgumentWrongTypeException if the argument is set but is not an item
     */
    public Optional<ItemObject> getArgItem(@NotNull String argName) {
        CommandArgument argument = findArg(argName);

        if (argument == null)
            return Optional.empty();
        if (argument.getData() instanceof ArgumentItem(ItemObject value))
            return Optional.of(value);
        throw new CommandArgumentWrongTypeException(
                argName + " requested as item but stored as " + argument.getType());
    }

    /**
     * Reads this command's {@code argName} argument as a number - the port of C's
     * {@code cmd_get_arg_number}. Empty when unset; throws when set to another type.
     *
     * @param argName the name to look up
     * @return the stored number, or empty if no argument of that name is set
     * @throws CommandArgumentWrongTypeException if the argument is set but is not a number
     */
    public Optional<Integer> getArgNumber(@NotNull String argName) {
        CommandArgument argument = findArg(argName);

        if (argument == null)
            return Optional.empty();
        if (argument.getData() instanceof ArgumentNumber(int value))
            return Optional.of(value);
        throw new CommandArgumentWrongTypeException(
                argName + " requested as number but stored as " + argument.getType());
    }

    /**
     * Reads this command's {@code argName} argument as a point - the port of C's
     * {@code cmd_get_arg_point}. Empty when unset; throws when set to another type.
     *
     * @param argName the name to look up
     * @return the stored grid location, or empty if no argument of that name is set
     * @throws CommandArgumentWrongTypeException if the argument is set but is not a point
     */
    public Optional<Loc> getArgPoint(@NotNull String argName) {
        CommandArgument argument = findArg(argName);

        if (argument == null)
            return Optional.empty();
        if (argument.getData() instanceof ArgumentPoint(Loc value))
            return Optional.of(value);
        throw new CommandArgumentWrongTypeException(
                argName + " requested as point but stored as " + argument.getType());
    }

    /**
     * Reads this command's {@code argName} argument as a string - the port of C's
     * {@code cmd_get_arg_string}. Empty when unset; throws when set to another type.
     *
     * @param argName the name to look up
     * @return the stored string, or empty if no argument of that name is set
     * @throws CommandArgumentWrongTypeException if the argument is set but is not a string
     */
    public Optional<String> getArgString(@NotNull String argName) {
        CommandArgument argument = findArg(argName);

        if (argument == null)
            return Optional.empty();
        if (argument.getData() instanceof ArgumentString(String value))
            return Optional.of(value);
        throw new CommandArgumentWrongTypeException(
                argName + " requested as string but stored as " + argument.getType());
    }

    /**
     * Reads this command's {@code argName} argument as a target - the port of C's
     * {@code cmd_get_arg_target}. Empty when unset; throws when set to another type. Note this is a
     * raw read: it does <em>not</em> validate that the target is still reachable - that live check
     * belongs to {@link #getTarget}.
     *
     * @param argName the name to look up
     * @return the stored target code, or empty if no argument of that name is set
     * @throws CommandArgumentWrongTypeException if the argument is set but is not a target
     */
    public Optional<DirectionEnum> getArgTarget(@NotNull String argName) {
        CommandArgument argument = findArg(argName);

        if (argument == null)
            return Optional.empty();
        if (argument.getData() instanceof ArgumentTarget(DirectionEnum value))
            return Optional.of(value);
        throw new CommandArgumentWrongTypeException(
                argName + " requested as target but stored as " + argument.getType());
    }

    /**
     * Gets a direction for this command, taking a queued argument when there is one and otherwise
     * prompting the player - the port of C's {@code cmd_get_direction}.
     *
     * <p>A stored direction is trusted only when it is a real direction: a queued {@link
     * DirectionEnum#DIR_NONE} is treated as absent and falls through to the prompt, exactly as C
     * re-checks {@code *dir != DIR_NONE} before returning a stored arg. On abort (the player
     * declines to choose) the in-flight repeat is cancelled via {@link CommandQueue#cancelRepeat} -
     * a failed direction must stop a repeating command dead - and an empty {@link Optional} is
     * returned to signal C's {@code CMD_ARG_ABORTED}.
     *
     * @param argName the name the direction argument is stored under
     * @param allow5  whether the "5"/self target is a permitted answer (C's {@code allow_5})
     * @return the chosen direction, or empty if the player aborted
     */
    public Optional<DirectionEnum> getDirection(String argName, boolean allow5) {
        Optional<DirectionEnum> result = getArgDirection(argName);
        if (result.isPresent() && result.get() != DirectionEnum.DIR_NONE) return result;

        Optional<DirectionEnum> repDir = GameInputHolder.getInstance().getRepDir(allow5);
        if (repDir.isEmpty()) {
            GameState.getCommandQueue().cancelRepeat();
            return Optional.empty();
        }

        setArgDirection(argName, repDir.get());
        return repDir;
    }

    /**
     * Gets an item for this command, taking a queued argument when there is one and otherwise
     * opening the item-selection UI - the port of C's {@code cmd_get_item}.
     *
     * <p>A stored item is trusted only when it still passes {@code filter} (a {@code null} filter
     * accepts anything, matching C's {@code !filter} branch); a queued item that no longer
     * qualifies is treated as absent and re-prompted. Before prompting, a shapechanged player is
     * confined to the floor by clearing the equipment, inventory and quiver bits from {@code mode},
     * mirroring C's {@code mode &= ~(USE_EQUIP | USE_INVEN | USE_QUIVER)}.
     *
     * <p>C returned the chosen object through a {@code struct object **} out-parameter alongside a
     * boolean; Java has no out-parameters, so the selection comes back as the {@link Optional}
     * return value instead - present is C's {@code CMD_OK}, empty is {@code CMD_ARG_ABORTED}.
     *
     * @param argName the name the item argument is stored under
     * @param prompt  the selection prompt shown to the player
     * @param reject  the message shown when nothing eligible exists (C's {@code str})
     * @param filter  a predicate the item must satisfy, or {@code null} to accept any item
     * @param mode    the permitted source locations (equipment/inventory/quiver/floor)
     * @return the chosen item, or empty if the player aborted
     */
    public Optional<ItemObject> getItem(String argName, String prompt, String reject,
                                        Predicate<ItemObject> filter, Flag<GetItemFlags> mode) {
        Optional<ItemObject> result = getArgItem(argName);

        if (result.isPresent() && (filter == null || filter.test(result.get()))) {
            return result;
        }

        if (player.isShapeChanged()) {
            mode.off(GetItemFlags.USE_EQUIP);
            mode.off(GetItemFlags.USE_INVEN);
            mode.off(GetItemFlags.USE_QUIVER);
        }

        Optional<ItemObject> chosen = GameInputHolder.getInstance().getItem(prompt, reject, this.code, filter, mode);
        if (chosen.isEmpty()) return Optional.empty();

        setArgItem(argName, chosen.get());
        return chosen;
    }

    /**
     * Gets a string for this command, taking a queued argument when there is one and otherwise
     * prompting the player - the port of C's {@code cmd_get_string}.
     *
     * <p>When no argument is queued it announces {@code title} (passed through {@code "%s"} so a
     * literal {@code %} in the title is not treated as a format directive, matching C's
     * {@code msg("%s", title)}), flushes pending messages, then opens the text prompt seeded with
     * {@code initial}. An accepted <em>empty</em> string is a valid answer; only a genuine abort
     * yields an empty {@link Optional} (C's {@code CMD_ARG_ABORTED}), which is why the accept/abort
     * decision comes from the UI's own optional rather than from testing the text for emptiness.
     *
     * @param argName the name the string argument is stored under
     * @param initial the value the input field is pre-filled with (may be {@code null} or empty)
     * @param title   an introductory message shown before the prompt
     * @param prompt  the input prompt label
     * @return the entered string, or empty if the player aborted
     */
    public Optional<String> getString(String argName, String initial, String title, String prompt) {
        String temp = "";

        Optional<String> result = getArgString(argName);
        if (result.isPresent()) return result;

        Message.send("%s", title); // This is currently a stub class
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_MESSAGE_FLUSH);

        if (initial != null && !initial.isEmpty())
            temp = initial;

        Optional<String> resultFromUser = GameInputHolder.getInstance().getString(prompt, temp);

        if (resultFromUser.isPresent()) {
            setArgString(argName, resultFromUser.get());
            return resultFromUser;
        }

        return Optional.empty();
    }

    /**
     * Gets an aim target for this command, taking a queued argument when there is one and otherwise
     * prompting the player to aim - the port of C's {@code cmd_get_target}.
     *
     * <p>A stored target is trusted only when it is still usable: it must not be {@link
     * DirectionEnum#DIR_UNKNOWN}, and a {@link DirectionEnum#DIR_TARGET} (meaning "the current
     * health-bar target") is only honoured while that target is still valid ({@link
     * GameState#targetOkay}). Anything else - no arg, an unknown target, or a stale health-bar
     * target - falls through to the aim prompt, matching C's re-check before it calls
     * {@code get_aim_dir}. The aimed direction returns as the {@link Optional} value (C's out-param
     * {@code *target}); empty is C's {@code CMD_ARG_ABORTED}. Unlike {@link #getDirection}, an
     * aborted aim does not cancel the repeat.
     *
     * @param argName the name the target argument is stored under
     * @return the chosen target, or empty if the player aborted
     */
    public Optional<DirectionEnum> getTarget(String argName) {
        Optional<DirectionEnum> result = getArgTarget(argName);

        if (result.isPresent()) {
            DirectionEnum target = result.get();
            if (target != DirectionEnum.DIR_UNKNOWN && (target != DirectionEnum.DIR_TARGET || GameState.targetOkay()))
                return result;
        }

        Optional<DirectionEnum> response = GameInputHolder.getInstance().getAimDir();

        if (response.isEmpty()) return Optional.empty();

        setArgTarget(argName, response.get());
        return response;
    }

    /**
     * Gets a quantity for this command, taking a queued number argument when there is one and
     * otherwise prompting the player - the port of C's {@code cmd_get_quantity}.
     *
     * <p>A stored number is trusted as-is, with no lower-bound check - exactly as C returns whatever
     * {@code cmd_get_arg_number} held. The {@code > 0} gate applies only to a freshly prompted
     * amount: a prompt result of zero (or the UI aborting) yields an empty {@link Optional}, C's
     * {@code CMD_ARG_ABORTED}. The stored value is backed by the same {@code arg_NUMBER} slot as
     * {@link #getArgNumber}/{@link #setArgNumber}; there is no separate quantity argument type.
     *
     * @param argName the name the number argument is stored under
     * @param max     the largest quantity the prompt will allow
     * @return the chosen quantity, or empty if the player aborted (or entered zero)
     */
    public Optional<Integer> getQuantity(String argName, int max) {
        Optional<Integer> result = getArgNumber(argName);
        if (result.isPresent()) return result;

        Optional<Integer> amountRec = GameInputHolder.getInstance().getQuantity(null, max);

        if (amountRec.isPresent() && amountRec.get() > 0) {
            setArgNumber(argName, amountRec.get());
            return amountRec;
        }

        return Optional.empty();
    }

    /**
     * Gets a spell for this command, taking a queued choice when there is one and otherwise
     * prompting the player - the port of C's {@code cmd_get_spell}.
     *
     * <p>The flow mirrors C exactly. First the fast path: a stored {@code arg_CHOICE} is resolved
     * through the caster's flattened spell index ({@link ClassMagic#spellByIndex}) and returned only
     * if it still passes {@code spellFilter}; a missing, out-of-range, or now-rejected choice falls
     * through. Then, if a {@code "book"} item is already queued, the spell is chosen from just that
     * book ({@link #getSpellFromBook}); otherwise the full picker runs ({@link
     * GameInputHolder#getInstance()}'s {@code getSpell}), which also hands back the book the player
     * settled on. On success the chosen book is stored as the {@code "book"} item argument and the
     * spell's flattened index ({@link ClassMagic#indexOfSpell}) as {@code argName}'s choice, so a
     * replay re-resolves the same spell; any abort yields an empty {@link Optional} (C's
     * {@code CMD_ARG_ABORTED}). Non-casters short-circuit to empty.
     *
     * <p>Where C threaded the selection through an {@code int *spell} out-parameter, the port returns
     * the resolved {@link MagicSpell}; the {@code int} lives only in the queued {@code arg_CHOICE}
     * round-trip, converted to and from the object at this boundary via the {@link ClassMagic}
     * helpers.
     *
     * @param argName     the name the spell-choice argument is stored under
     * @param verb        the action the spell is wanted for (e.g. "cast", "study"), shown by the UI
     * @param bookFilter  the predicate a book must satisfy to be eligible, for the full picker
     * @param bookError   the message shown when no eligible book exists
     * @param spellFilter the predicate a spell must satisfy, tested against the player and spell
     * @param spellError  the message shown when no eligible spell exists
     * @return the chosen spell, or empty if the player aborted (or is not a caster)
     */
    public Optional<MagicSpell> getSpell(String argName, String verb,
                                         @NotNull Predicate<ItemObject> bookFilter, String bookError,
                                         @NotNull BiPredicate<Player, MagicSpell> spellFilter, String spellError) {
        Optional<Integer> result = getArgChoice(argName);
        ItemObject bookItem;
        MagicSpell spell;
        ClassMagic magic = player.getPlayerClass().getMagic();
        if (magic == null || !magic.isCaster())
            return Optional.empty();

        if (result.isPresent()) {
            spell = magic.spellByIndex(result.get());
            if (spell != null && spellFilter.test(player, spell))
                return Optional.of(spell);
        }

        Optional<ItemObject> itemObject = getArgItem("book");

        if (itemObject.isPresent()) {
            Optional<MagicSpell> spellFromBook = getSpellFromBook(player, verb, itemObject.get(),
                    spellError, spellFilter);
            if (spellFromBook.isPresent()) {
                spell = spellFromBook.get();
                bookItem = itemObject.get();
            } else return Optional.empty();
        } else {
            Optional<GameInput.SpellSelection> spellReceived = GameInputHolder.getInstance().getSpell(player, verb,
                    bookFilter, code, bookError, spellFilter, spellError, null);
            if (spellReceived.isPresent()) {
                spell = spellReceived.get().spell();
                bookItem = spellReceived.get().book();
            } else return Optional.empty();
        }

        setArgItem("book", bookItem);

        // calculate the spell index
        int newSpellIndex = magic.indexOfSpell(spell);

        if (newSpellIndex == -1) return Optional.empty();

        setArgChoice(argName, newSpellIndex);

        return Optional.of(spell);
    }

    /**
     * Finds the argument stored under {@code argName}, if any - the shared name lookup behind the
     * {@code getArg*} readers.
     *
     * @param argName the name to look up
     * @return the matching argument, or {@code null} if none is stored under that name
     */
    private CommandArgument findArg(String argName) {
        return args.stream().filter(a -> a.getName().equals(argName)).findFirst().orElse(null);
    }

    /**
     * Asks the input seam to choose a spell from a single, already-known book - the port of C's
     * {@code get_spell_from_book} call in {@code cmd_get_spell}. A thin pass-through to the installed
     * {@link GameInput}; kept as its own method so {@link #getSpell}'s book-arg branch reads as one
     * step and the seam lookup lives in a single place.
     *
     * @param player      the caster
     * @param verb        the action the spell is wanted for (e.g. "cast", "study")
     * @param book        the book to choose from
     * @param spellError  the message shown when the book offers no eligible spell
     * @param spellFilter the predicate a spell must satisfy, tested against the player and spell
     * @return the chosen spell, or empty if the player aborted
     */
    public Optional<MagicSpell> getSpellFromBook(Player player, String verb, ItemObject book,
                                                 String spellError, BiPredicate<Player, MagicSpell> spellFilter) {
        return GameInputHolder.getInstance().getSpellFromBook(player, verb, book, spellError, spellFilter);
    }

    /**
     * Gets a choice from a list of effects, taking a queued choice when there is one and otherwise
     * prompting the player - the port of C's {@code cmd_get_effect_from_list}. Used when an effect
     * offers the player several sub-effects to pick between.
     *
     * <p>Unlike {@link #getSpell}, the selection is a plain {@code int} throughout - a list index in
     * {@code [0, count)}, or the sentinel {@code -2} meaning "choose at random" when
     * {@code allowRandom} is set - so there is no object to resolve and the value round-trips through
     * {@code arg_CHOICE} directly. A stored choice is used only if still valid; a missing,
     * out-of-range, or (when random is disallowed) {@code -2} choice falls through to the prompt.
     * Whatever is finally settled on is stored back under {@code argName} so a replay reuses it. An
     * abort - or a prompt result that is still invalid - yields an empty {@link Optional} (C's
     * {@code CMD_ARG_ABORTED}).
     *
     * <p>Where C returned the outcome through an {@code int *choice} out-parameter and a status code,
     * the port folds both into the return: present is the chosen index (or {@code -2}), empty is the
     * abort. Note {@code -2} is a valid <em>present</em> value, not a failure - the empty case is
     * reserved for a genuine abort, which is why {@code Optional<Integer>} and not {@code Optional} of
     * the {@link Effect} itself.
     *
     * @param argName     the name the choice argument is stored under
     * @param prompt      the prompt to show, or {@code null} for the default
     * @param effects     the effects to choose among (only its size is read here; the seam shows them)
     * @param count       how many of the effects to offer, or {@code -1} for all of them
     * @param allowRandom whether to offer an extra "choose at random" option, whose result is {@code -2}
     * @return the chosen index (or {@code -2} for random), or empty if the player aborted
     */
    public EffectChoice getEffectFromList(String argName, String prompt,
                                               List<Effect> effects, int count, boolean allowRandom) {
        if (count == -1) {
            count = effects.size();
        }
        final int num = count;

        EffectChoice choice = null;

        Optional<Integer> stored = getArgChoice(argName);

        if (stored.isPresent()) {
            int v = stored.get();
            if (v == -2 && allowRandom) choice = new EffectChoice.Random();
            else if (v >= 0 && v < num) choice = new EffectChoice.Index(v);
        }

        if (choice == null) {
            choice = GameInputHolder.getInstance().getEffectFromList(prompt, effects, count, allowRandom);
        }

        return switch (choice) {
            case EffectChoice.Random r when allowRandom -> {
                setArgChoice(argName, -2);
                yield new EffectChoice.Random();
            }
            case EffectChoice.Index(int i) when i >= 0 && i < num -> {
                setArgChoice(argName, i);
                yield new EffectChoice.Index(i);
            }
            default -> new EffectChoice.Aborted();
        };
    }

    /**
     * Resolves a carried book item to the class {@link MagicBook} it represents - the port of C's
     * {@code player_object_to_book}. The match is on the item kind's {@code (tval, sval)} against
     * each of the caster's books, exactly as C compares {@code obj->tval}/{@code obj->sval} to
     * {@code magic.books[i]}.
     *
     * @param player the caster whose books are searched
     * @param item   the book item to identify
     * @return the matching {@link MagicBook}, or {@code null} if the item is not one of this class's
     * books
     */
    private MagicBook playerObjectToBook(Player player, ItemObject item) {
        for (MagicBook book : player.getPlayerClass().getMagic().getMagicBooks()) {
            if (item.getKind().gettValue() == book.getBookTValue()
                    && item.getKind().getsVal() == book.getSval())
                return book;
        }
        return null;
    }
}