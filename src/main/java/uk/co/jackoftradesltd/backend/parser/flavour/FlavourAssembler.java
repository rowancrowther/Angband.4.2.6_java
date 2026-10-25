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

package uk.co.jackoftradesltd.backend.parser.flavour;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.objects.Flavour;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link FlavourParseRecord}s of one {@code kind:} block into
 * {@link Flavour} domain objects. It handles the per-line leg only — parsing the
 * index and resolving the colour — and dispatches on whether the record is a
 * {@code fixed:} line (non-null sval) or a plain {@code flavor:} line.
 *
 * <p>It deliberately does <em>not</em> resolve a fixed record's sval symbol: that
 * needs the block's tval, which lives on the enclosing {@link FlavourKindParseRecord},
 * so it is done one level up in {@link FlavourKindAssembler}.
 *
 * <p>The per-record work is C's {@code parse_flavor_flavor} in {@code init.c}, which both the
 * {@code flavor:} and the {@code fixed:} directive register, together with the
 * {@code parser_parse()} reading of its {@code uint index} in {@code parser.c}. Its outcomes are:
 * <ul>
 *   <li>An index that is not a number, or that starts with {@code -}, ends the load with
 *       {@link DataFileParseException}, as C's {@code PARSE_ERROR_NOT_NUMBER} does. Nothing in
 *       this class is a soft error, so the {@code errors} list passed to
 *       {@link #assemble(List, List, String)} is never appended to.</li>
 *   <li>A colour that matches nothing is not an error: C's {@code color_char_to_attr()} and
 *       {@code color_text_to_attr()} both answer white, so the flavour is kept and painted
 *       white. {@code PARSE_ERROR_INVALID_COLOR} in C is reachable only for a negative
 *       {@code char}, which the data files do not contain.</li>
 *   <li>{@link ColourEnum#COLOUR_SHADE} also becomes white, since no {@code color_table[]} row in C
 *       carries a name that could select it.</li>
 * </ul>
 *
 * <p>Class FlavourAssembler coded before 260915, commented in full on 261025.
 *
 * @author Rowan Crowther
 */
public class FlavourAssembler implements Assembler<FlavourParseRecord, List<Flavour>> {
    /**
     * The log the fatal index messages are written to just before {@link DataFileParseException}
     * is thrown. Nothing else is logged here.
     *
     * <p>Field logger coded before 260915, commented in full on 261025.
     */
    private static final Logger logger = LogManager.getLogger(FlavourAssembler.class);

    /**
     * Assembles the flavours of one kind block, one {@link Flavour} per record and in the order
     * the records arrive. Each record goes to {@link #storeFlavour}, which reads its index and
     * resolves its colour; whether {@link FlavourParseRecord#sVal()} is {@code null} then decides
     * the constructor: a {@code null} sval is a plain {@code flavor:} line and a non-null one a
     * {@code fixed:} line, which keeps the symbol text for {@link FlavourKindAssembler} to resolve.
     *
     * <p>No record is skipped: a bad index throws and a bad colour becomes white, so the list has
     * as many flavours as there are records. {@code errors} is part of the {@link Assembler}
     * contract and is not used here.
     *
     * <p>Function assemble coded before 260915, commented in full on 261025.
     *
     * @param records  the raw flavour/fixed records of a single {@code kind:} block
     * @param errors   the soft-error sink, not appended to by this class
     * @param filename the file being read, for the messages
     * @return the assembled flavours (fixed ones still awaiting sval resolution)
     * @throws DataFileParseException if a record's index starts with {@code -} or has no leading
     *                                digits
     */
    @Override
    public List<Flavour> assemble(@NotNull List<FlavourParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<Flavour> flavours = new ArrayList<>();

        for (FlavourParseRecord record : records) {
            int line = record.line();
            storeFlavour(flavours, line, filename, record.text(), record.sVal(), record.attr(),
                    record.index());
        }

        return flavours;
    }
    
    /**
     * Builds the one {@link Flavour} for a record and appends it to {@code flavours}: C's
     * {@code parse_flavor_flavor} in {@code init.c}, less the sval lookup and the tval and glyph,
     * which come from the enclosing {@code kind:} block.
     *
     * <p>The index is a {@code uint} in C, which {@code parser_parse()} in {@code parser.c} reads
     * with {@code strtoul} in base 0 after refusing a first character of {@code -}. The refusal is
     * made here on the raw text with {@link ParseUtils#minusFound(String)}, so {@code -0} is
     * refused although its value is 0. The reading is left to the five-argument
     * {@link ParseUtils#parseIntString(String, String, int, String, String)}, which skips leading
     * whitespace, takes one optional {@code +}, and then chooses the base from the digits that
     * follow: {@code 0x} or {@code 0X} is hexadecimal, a leading {@code 0} is octal and anything
     * else is decimal. So {@code +0x10} is 16, {@code +010} is 8, {@code +08} is 0 (the {@code 8}
     * is not an octal digit, so the {@code 0} is all {@code strtoul} reads), and {@code 12abc} is
     * 12. A value above the range of an {@code int} is clamped, the deviation recorded in the
     * {@link ParseUtils} class block, where C would wrap to 32 bits.
     *
     * <p>The colour goes through {@link ColourEnum#fromCode(String)}, which sends a one-character
     * {@code attr} to C's {@code color_char_to_attr()} and a longer one to
     * {@code color_text_to_attr()} (case-insensitive on the name). Both C functions answer white for
     * a miss, where {@code fromCode} answers {@code null}, so a {@code null} becomes
     * {@link ColourEnum#COLOUR_WHITE} here. {@link ColourEnum#COLOUR_SHADE}, which only the Java
     * name {@code "Shade"} can select, becomes white too, as no name in C's table matches it. A
     * blank {@code attr} is {@link ColourEnum#COLOUR_DARK}, as in C.
     *
     * <p>{@code text} may be {@code null}: C only appends {@code desc} when it is present, which
     * is how scroll flavours come through, and the {@link Flavour} gets the {@code null}.
     *
     * <p>Function storeFlavour coded before 260915, commented in full on 261025.
     *
     * @param flavours the list being built, appended to
     * @param line     the line the record starts on, for the messages
     * @param filename the file being read, for the messages
     * @param text     the flavour description, or {@code null} when the line has none
     * @param sVal     the sval symbol of a {@code fixed:} line, or {@code null} for {@code flavor:}
     * @param attr     the colour, a one-character code or a name
     * @param indexStr the raw index text
     * @throws DataFileParseException if {@code indexStr} starts with {@code -} or has no leading
     *                                digits
     */
    private void storeFlavour(List<Flavour> flavours, int line, String filename, 
                              String text, String sVal, String attr, String indexStr) {
        if (ParseUtils.minusFound(indexStr)) {
            String msg = "Flavour starting at line: " + line + " of file: " + filename + " has " +
                    "an illegal index value: " + indexStr;
            logger.fatal(msg);
            throw new DataFileParseException(msg);
        }
        int index = ParseUtils.parseIntString("Flavour", indexStr, line, filename, "index");
        
        ColourEnum colour = ColourEnum.fromCode(attr);
        if (colour == null || colour == ColourEnum.COLOUR_SHADE) {
            colour = ColourEnum.COLOUR_WHITE;
        }
        if (sVal == null) {
            flavours.add(new Flavour(text, colour, index));
        } else {
            flavours.add(new Flavour(text, sVal, colour, index));
        }
    }
}
