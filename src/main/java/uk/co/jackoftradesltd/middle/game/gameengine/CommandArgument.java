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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;

/**
 * One named argument attached to a {@link Command} - the port of C's {@code struct cmd_arg}
 * (cmd-core.h). It pairs a {@link CommandArgumentType type} tag with its {@link CommandArgumentData
 * data} payload and the {@link #name} the argument is looked up by.
 *
 * <p>Arguments are addressed by name, not position, so a handler asks for (say) {@code "direction"}
 * regardless of where it sits in the command's list. The {@link #type} mirrors the payload's own
 * {@link CommandArgumentData#type() type()} and lets a lookup reject a name that resolves to the
 * wrong kind of value ({@code CMD_ARG_WRONG_TYPE}).
 *
 * <p>Two differences from C, neither of which changes behaviour. C's {@code arg[CMD_MAX_ARGS]} is a
 * fixed array of four slots in which an empty {@code name[0]} marks a free slot; here the owning
 * {@link Command} keeps a growable list, so a slot is simply absent until it is added. C's
 * {@code name[20]} silently truncates a longer name to 19 characters ({@code my_strcpy}); {@link
 * #name} is unbounded, which is harmless because every name the game uses is a short literal.
 *
 * <p>Class CommandArgument coded before 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public class CommandArgument {
    /**
     * The kind of value this argument holds; mirrors {@code data.type()}. Port of {@code struct
     * cmd_arg}'s {@code type} member ({@code enum cmd_arg_type}); {@link
     * CommandArgumentType#arg_NONE} (C's unused-slot state) is expressed by the argument being
     * absent from the list rather than by a stored tag.
     *
     * <p>Field type coded before 261001, commented in full on 261001.
     */
    CommandArgumentType type;

    /**
     * The argument's value, as one variant of the sealed data hierarchy. Port of {@code struct
     * cmd_arg}'s {@code data} member ({@code union cmd_arg_data}).
     *
     * <p>Field data coded before 261001, commented in full on 261001.
     */
    CommandArgumentData data;

    /**
     * The name this argument is matched by when a handler requests it. Port of {@code struct
     * cmd_arg}'s {@code char name[20]}; see the class note on the dropped 19-character limit.
     *
     * <p>Field name coded before 261001, commented in full on 261001.
     */
    String name;

    /**
     * Creates a named argument. No validation is done that {@code type} agrees with {@code
     * data.type()}; the {@link Command} {@code setArg*} methods are the callers and always pass a
     * matching pair.
     *
     * <p>Constructor CommandArgument coded before 261001, commented in full on 261001.
     *
     * @param type the kind of value, expected to agree with {@code data.type()}
     * @param data the value payload
     * @param name the name the argument is looked up by
     */
    public CommandArgument(CommandArgumentType type, CommandArgumentData data, String name) {
        this.type = type;
        this.data = data;
        this.name = name;
    }

    /**
     * Returns the name this argument is matched by.
     *
     * <p>Function getName coded before 261001, commented in full on 261001.
     *
     * @return the name this argument is matched by
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value payload. Callers switch on the variant rather than reading a union member.
     *
     * <p>Function getData coded before 261001, commented in full on 261001.
     *
     * @return the value payload
     */
    public CommandArgumentData getData() {
        return data;
    }

    /**
     * Returns the type tag, the value a lookup compares against to produce {@code
     * CMD_ARG_WRONG_TYPE}.
     *
     * <p>Function getType coded before 261001, commented in full on 261001.
     *
     * @return the kind of value this argument holds
     */
    public CommandArgumentType getType() {
        return type;
    }

    /**
     * Overwrites this argument's fields in place, ignoring any that are {@code null}. Used by {@link
     * Command}'s {@code setArg*} methods to re-point an existing named argument at a new type and
     * value rather than allocating a fresh {@link CommandArgument} - the port's stand-in for C
     * simply reassigning the matching {@code arg[]} slot's union member. Passing {@code null} leaves
     * the corresponding field untouched, so a caller can update the payload without disturbing the
     * name.
     *
     * <p>Function update coded before 261001, commented in full on 261001.
     *
     * @param argumentType the new type tag, or {@code null} to leave it unchanged
     * @param value        the new payload, or {@code null} to leave it unchanged
     * @param argName      the new name, or {@code null} to leave it unchanged
     */
    public void update(@Nullable CommandArgumentType argumentType, @Nullable CommandArgumentData value, @Nullable String argName) {
        if (argumentType != null)
            this.type = argumentType;
        if (value != null)
            this.data = value;
        if (argName != null)
            this.name = argName;
    }

    /**
     * Returns an independent copy of this argument: the type tag and name are carried across and
     * the payload is duplicated via {@link CommandArgumentData#copy()}. This is the per-argument
     * half of {@link Command#clone()}, standing in for the {@code *dest = *src} struct assignment
     * in C's {@code cmd_copy}, which copies the {@code arg[]} array by value (and deep-copies only
     * string payloads).
     *
     * <p>Function copy coded before 261001, commented in full on 261001.
     *
     * @return a new {@link CommandArgument} sharing no mutable state with this one
     */
    public CommandArgument copy() {
        return new CommandArgument(type, data.copy(), name);
    }
}
