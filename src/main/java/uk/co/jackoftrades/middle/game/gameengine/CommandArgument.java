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

import uk.co.jackoftrades.middle.game.enums.CommandArgumentType;

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
 * @author Rowan Crowther
 */
public class CommandArgument {
    /**
     * The kind of value this argument holds; mirrors {@code data.type()}.
     */
    CommandArgumentType type;

    /**
     * The argument's value, as one variant of the sealed data hierarchy.
     */
    CommandArgumentData data;

    /**
     * The name this argument is matched by when a handler requests it.
     */
    String name;

    /**
     * Creates a named argument.
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
     * @return the name this argument is matched by
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value payload
     */
    public CommandArgumentData getData() {
        return data;
    }

    /**
     * @return the kind of value this argument holds
     */
    public CommandArgumentType getType() {
        return type;
    }
}
