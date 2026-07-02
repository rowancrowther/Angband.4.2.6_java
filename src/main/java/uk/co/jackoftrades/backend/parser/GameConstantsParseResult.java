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

import uk.co.jackoftrades.middle.game.globals.GameConstantsData;

import java.util.List;

/**
 * Holder class for the results from the Game Constants Parse
 *
 * @author Rowan Crowther
 */
public class GameConstantsParseResult {
    /**
     * The data record from the Game Constants parse
     */
    private final GameConstantsData data;

    /**
     * A list of error strings of any errors that have occurred during the Game Constants parse
     */
    private final List<String> errors;

    /**
     * Constructor
     *
     * @param data   The data result from the parse
     * @param errors The list of errors from the parse
     */
    public GameConstantsParseResult(GameConstantsData data, List<String> errors) {
        this.data = data;
        this.errors = errors;
    }

    /**
     * returns whether any errors have occurred during the parsing of this file
     *
     * @return true if any error has been triggered, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Accessor
     *
     * @return the data from the parse, either a full GameConstantsData object, or null
     */
    public GameConstantsData getData() {
        return data;
    }

    /**
     * The list of errors
     *
     * @return the list of errors that have occurred during parsing
     */
    public List<String> getErrors() {
        return errors;
    }

}