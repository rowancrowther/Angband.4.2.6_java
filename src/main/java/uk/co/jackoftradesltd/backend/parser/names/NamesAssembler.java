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

package uk.co.jackoftradesltd.backend.parser.names;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.game.Name;
import uk.co.jackoftradesltd.middle.player.enums.RandnameType;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembles the raw {@link NamesParseRecord}s parsed from {@code names.txt} into the finished
 * {@link Name} domain objects — the random-name fragment lists (reader &rarr; ParseRecord
 * &rarr; assembler &rarr; domain). One {@link Name} is built per {@code section:} block.
 *
 * <p>This is the checking half of C's {@code parse_names_section} ({@code init.c}), together with
 * the {@code uint} branch of {@code parser_parse} ({@code parser.c}) that reads its number. C reads
 * the section with {@code strtoul} in base 0, refuses a token that starts with {@code -}
 * ({@code PARSE_ERROR_NOT_NUMBER}) and refuses a section at or above {@code RANDNAME_NUM_TYPES},
 * which is 3 ({@code PARSE_ERROR_OUT_OF_BOUNDS}). Either error stops the load. Here the same three
 * outcomes throw {@link DataFileParseException}, with the line and filename, which stops the load
 * the same way.
 *
 * <p>The filing half of {@code parse_names_word} and {@code finish_parse_names} is not here.
 * {@code MiscRegistry.setNames} joins the words of every {@link Name} sharing a section into one
 * list per {@link RandnameType}, which is how a second {@code section:1} block adds to the first,
 * as it does in C.
 *
 * <p>Two accepted deviations from C:
 * <ul>
 * <li>Section 0 is refused. C accepts it and files its words in {@code name_sections[0]}, a slot
 * nothing reads, since {@code randname_make} ({@code randname.c}) asserts a type above zero. The
 * Java {@link RandnameType} has no constant for 0, so the words would have nowhere to go; refusing
 * them here gives the error a line number rather than leaving it to {@code setNames}.</li>
 * <li>A number outside the {@code int} range is refused. C stores {@code strtoul}'s result in a
 * 32-bit {@code unsigned int}, so {@code 4294967296} wraps to 0 and loads as section 0.
 * {@link ParseUtils#parseIntString} clamps it to {@link Integer#MAX_VALUE}, which the range check
 * then refuses.</li>
 * </ul>
 *
 * <p>The shipped {@code names.txt} has sections 1 and 2 only, so it loads the same in both.
 *
 * <p>Class NamesAssembler coded before 260915, commented in full on 261027.
 *
 * @author Rowan Crowther
 */
public class NamesAssembler implements Assembler<NamesParseRecord, List<Name>> {
    /**
     * Logs each refused section at {@code fatal} before the {@link DataFileParseException} is
     * thrown, so the message reaches the log even if a caller swallows the exception.
     *
     * <p>Field logger coded on 261027, commented in full on 261027.
     */
    private final static Logger logger = LogManager.getLogger(NamesAssembler.class);

    /**
     * Resolve every parsed {@code names.txt} record into a {@link Name}, in file order. Each
     * section's text goes through three checks, in C's order:
     * <ul>
     * <li>a leading {@code -} is refused, through {@link ParseUtils#minusFound}, as C's
     * {@code *tok == '-'} test is. This catches {@code -0}, whose value is 0, which is why the text
     * is tested and not the number;</li>
     * <li>the text is read by {@link ParseUtils#parseIntString} as {@code strtoul} reads it in base 0:
     * {@code 02} is 2, {@code 010} is octal 8 and {@code 08} stops at the 8 and is 0;</li>
     * <li>0 is refused (an accepted deviation, see the class comment), and so is anything above
     * {@link RandnameType#RANDNAME_NUM_TYPES}'s ordinal. That ordinal is 2, because the enum has no
     * constant in front of {@link RandnameType#RANDNAME_TOLKIEN}, so {@code > 2} is C's
     * {@code >= 3}. A {@code NONE} constant added at the front would move the ordinal to 3 and let
     * section 3 through.</li>
     * </ul>
     * The word list is copied through unchanged, in file order. C's list for a section comes out
     * reversed, because {@code parse_names_word} prepends each word, but the only consumer,
     * {@code build_prob} ({@code randname.c}), counts letter transitions and cannot tell the
     * difference.
     *
     * <p>Function assemble coded before 260915, commented in full on 261027 (sign, zero and range
     * checks added 261027).
     *
     * @param records  the raw names records from the grammar, one per {@code section:} block, in
     *                 file order
     * @param errors   unused: nothing here is a soft error any longer
     * @param filename the file being read, for the message of a refused section
     * @return the assembled name sections, in file order
     * @throws DataFileParseException if a section is negative, not a number, 0, or above 2
     */
    @Override
    public List<Name> assemble(@NotNull List<NamesParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<Name> names = new ArrayList<>();

        for (NamesParseRecord record : records) {
            int line = record.line();
            
            if (ParseUtils.minusFound(record.section())) {
                String msg = "Name starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative section value: " + record.section();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int section = ParseUtils.parseIntString("Name", record.section(), line, filename, "section");
            if (section == 0) {
                String msg = "Name starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal section value of zero: " + record.section();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            if (section > RandnameType.RANDNAME_NUM_TYPES.ordinal()) {
                String msg = "Name starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal section value: " + record.section();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            List<String> words = new ArrayList<>(record.word());

            names.add(new Name(section, words));
        }

        return names;
    }
}
