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

package uk.co.jackoftrades.backend.parser;

import java.util.List;

/**
 * A record template to be used when returning items from a
 * {@code lib/gamedata} file parse
 *
 * @param items  A list of items of type T (the template class)
 * @param errors A String List of errors
 * @param <T>    The object type which this parser is holding.
 */
public record ParseResult<T>(List<T> items, List<String> errors) {
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
