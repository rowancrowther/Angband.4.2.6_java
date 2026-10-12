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

package uk.co.jackoftradesltd.middle.game.gameengine.argumentdata;

import uk.co.jackoftradesltd.middle.game.enums.CommandArgumentType;
import uk.co.jackoftradesltd.middle.game.gameengine.CommandArgumentData;

/**
 * A command-argument value holding a text string - the {@code arg_STRING} variant of
 * {@link CommandArgumentData}, from the {@code const char *string} member of C's {@code union
 * cmd_arg_data} (cmd-core.h). {@code Command.setArgString} and {@code Command.getArgString} are
 * the ports of {@code cmd_set_arg_string} and {@code cmd_get_arg_string} (cmd-core.c) that wrap
 * and unwrap it, and {@code Command.getString} is the port of {@code cmd_get_string}, which reads
 * it and prompts when it is absent.
 *
 * <p>This is the one union member C owned: {@code cmd_set_arg_string} stored a private copy made
 * with {@code string_make}, and {@code cmd_set_arg} freed the old copy when an argument was
 * overwritten, as {@code cmd_release} did when a command was finished with. The port holds a
 * {@link String} and leaves all of that to garbage collection, so there is nothing here to free
 * and no release step to call.
 *
 * <p>The record is immutable and stores the string unchecked. A {@code null} is a legal value:
 * C's {@code string_make(NULL)} returns {@code NULL} and {@code cmd_set_arg_string} stores it, so
 * the port keeps the {@code null} too, and {@code Command.getArgString} answers empty for it. An
 * empty string is a different value and reads back as present. Unset is a third state, expressed
 * by the argument being absent from the command.
 *
 * <p>Two string arguments are equal when their strings are, by content, as the record's
 * {@code equals} defers to {@link String#equals(Object)}.
 *
 * <p>Class ArgumentString coded before 261012, commented in full on 261012.
 *
 * @param value the string value, stored as given; may be {@code null}
 * @author Rowan Crowther
 */
public record ArgumentString(String value) implements CommandArgumentData {
    /**
     * Reports the {@code arg_STRING} tag for this variant, the tag C's {@code cmd_set_arg_string}
     * stores beside the {@code string} member and {@code cmd_get_arg_string} checks before it
     * reads it. Every instance returns the same tag whatever it holds, a {@code null} string
     * included.
     *
     * <p>Function type coded before 261012, commented in full on 261012.
     *
     * @return {@link CommandArgumentType#arg_STRING}
     */
    @Override
    public CommandArgumentType type() {
        return CommandArgumentType.arg_STRING;
    }

    /**
     * Returns a new record holding the same string, the port of the per-argument step of C's
     * {@code cmd_copy}. C copies the whole command by struct assignment and then replaces each
     * {@code arg_STRING} pointer with a fresh {@code string_make} duplicate, because two commands
     * sharing one pointer would free it twice. This is the only variant C deep-copies. A
     * {@link String} is immutable and never freed, so sharing it is safe and the port needs no
     * duplicate: the new record holds the same {@link String} and equals this one. A {@code null}
     * string copies as {@code null}, as {@code string_make(NULL)} returns {@code NULL}.
     *
     * <p>Function copy coded before 261012, commented in full on 261012.
     *
     * @return a new {@code ArgumentString} holding the same {@link #value()}
     */
    @Override
    public CommandArgumentData copy() {
        return new ArgumentString(value);
    }
}