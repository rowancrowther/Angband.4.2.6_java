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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.parser;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * <code>Parser</code> interface</p>
 * <p>
 * Interface for all the ANTLR4 parsers. Only one function <code>ArrayList&lt;Object&gt; parse(String filename)</code>.
 * This function should run the file through the generated ANTLR4 code and return an array list of Object, which will
 * be further parsed by the relevant calling class (should be <code>GameConstants</code>).
 */
public interface Parser<T> {
    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @NotNull
    @Contract("_, -> !null")
    List<T> parse(@NotNull String filename) throws IOException;
}
