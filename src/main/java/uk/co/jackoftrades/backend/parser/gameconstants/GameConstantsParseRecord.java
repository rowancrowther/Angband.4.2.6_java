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

package uk.co.jackoftrades.backend.parser.gameconstants;

import java.util.List;

/**
 * Thin parse-DTO produced by the GameConstantsGrammar grammar for a single
 * category/field values in lib/gamedata/constants.txt.
 *
 * <p>This class is the boundary between the grammar layer and the reader layer.
 * The grammar's only job is to extract raw text from the data file and populate
 * this record — no enum resolution, no domain-object construction, no calls to
 * GameConstants. All of that work happens in the reader (GameConstantsReader),
 * which converts a {@code List<WorldParseRecord>} into the real domain
 * objects once the domain API is stable enough to use.</p>
 *
 * <p>A DTO rather than returning the domain values directly
 * from the grammar, as the domain model is still being ported and its
 * constructors change frequently. Design decision to un-coupling a grammar
 * from a changing constructor. The DTO decouples the two: the grammar is
 * "done" once it parses correctly, regardless of whether the domain class
 * it feeds is ready.</p>
 *
 * <p>Field types are kept as primitives and Strings wherever possible.
 *
 * @author Rowan Crowther
 */
public class GameConstantsParseRecord {
    /**
     * Holder for the line that any error occurred on
     */
    private final int lineNumber;

    /**
     * The category for this group of fields, i.e. 'mon-gen'
     */
    private final String category;

    /**
     * A list of string fields for this group, i.e. 'chance', '500'
     */
    private final List<String> fields;

    /**
     * Constructor - takes in the three parameters and assigns them to private members
     *
     * @param category   the main category for this group of fields
     * @param fields     the remaining strings in this group of files
     * @param lineNumber the line number of this line - used in error handling
     * @author Rowan Crowther
     */
    public GameConstantsParseRecord(String category, List<String> fields, int lineNumber) {
        this.category = category;
        this.fields = fields;
        this.lineNumber = lineNumber;
    }

    /**
     * Accessor
     *
     * @return the line this group of fields was on in the datafile 'contants.txt'
     * @author Rowan Crowther
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /*
     * Accessor
     * @return the main category for this group of fields, designated by the token at the start of the line.
     *
     * @author Rowan Crowther
     */
    public String getCategory() {
        return category;
    }

    /**
     * Accessor
     *
     * @return the fields of this line, separated into a List of Strings.
     * @author Rowan Crowther
     */
    public List<String> getFields() {
        return fields;
    }
}