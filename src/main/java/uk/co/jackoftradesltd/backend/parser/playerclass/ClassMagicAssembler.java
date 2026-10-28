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

package uk.co.jackoftradesltd.backend.parser.playerclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.ParseUtils;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns a class's raw {@link ClassMagicParseRecord} into a domain {@link ClassMagic} — the caster
 * profile. It parses the three {@code magic:} scalars (first-spell level, spell weight, book count)
 * and delegates the book list to {@link ClassSpellBookAssembler}, which does the heavier
 * cross-file resolution.
 *
 * <p>Although the {@link Assembler} contract is list-shaped, a class has at most one {@code magic:}
 * record; {@link PlayerClassAssembler} wraps the single record in a list and unwraps the single
 * result. This class is the port of {@code parse_class_magic()} in {@code init.c}, together with
 * the one check of {@code parse_class_book()} that reads what the {@code magic:} line declared:
 * C remembers the declared book count in {@code class_max_books} and returns
 * {@code PARSE_ERROR_TOO_MANY_ENTRIES} for a {@code book:} line once that many have been read.
 *
 * <p>A malformed scalar or a surplus book is fatal, as it is in C, where the matching
 * {@code PARSE_ERROR_*} stops the parse: the problem is logged and a
 * {@link DataFileParseException} naming the line and file is thrown. The {@code errors} list that
 * the {@link Assembler} contract passes in is never written to here; it is handed on to
 * {@link ClassSpellBookAssembler}, which appends a message for each book it cannot build and skips
 * that book. A skipped book still counts toward the declared limit, because the count is taken
 * from the parse records and not from the books that came back.
 *
 * <p>Differences from C, none of which a shipped {@code class.txt} reaches:
 * <ul>
 *   <li>A figure outside the range of an {@code int} is clamped, where C wraps it into its 32-bit
 *       field (see {@code docs/Accepted_Deviations.md}).</li>
 *   <li>The declared book count is checked after the books have been assembled, where C checks it
 *       at each {@code book:} line. Both end the load; only the message differs, and only when a
 *       surplus book is itself bad.</li>
 *   <li>The declared count is not stored on the result, which holds the number of books actually
 *       read (see {@link ClassMagic}). C keeps it only as the parser's limit.</li>
 *   <li>Outside this class, {@code PlayerClassGrammar.g4} lets a second {@code magic:} block
 *       replace the first, where C refuses it with {@code PARSE_ERROR_REPEATED_DIRECTIVE}; this
 *       class only ever sees the last one.</li>
 * </ul>
 *
 * <p>Class ClassMagicAssembler coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class ClassMagicAssembler implements Assembler<ClassMagicParseRecord, List<ClassMagic>> {
    /**
     * Receives the fatal message for each rejected {@code magic:} record before the
     * {@link DataFileParseException} carrying the same text is thrown.
     *
     * <p>Field logger coded before 260915, commented in full on 261028.
     */
    private static final Logger logger = LogManager.getLogger(ClassMagicAssembler.class);

    /**
     * Assembles the (at most one) parsed {@code magic:} record into a {@link ClassMagic}, reading
     * its three scalars and building its books via {@link ClassSpellBookAssembler}.
     *
     * <p><b>The scalars</b> are C's three {@code uint} fields, read in the order the line gives
     * them: first-spell level, spell weight, then book count. {@code parser.c} reads each with
     * {@code strtoul} in base 0 and refuses it as {@code PARSE_ERROR_NOT_NUMBER} when the text
     * starts with {@code -} (so {@code -0} is refused too) or has no digit. Here a leading
     * {@code -} is refused first, using {@link ParseUtils#minusFound(String)}, and the rest is read
     * by {@link ParseUtils#parseIntString(String, String, int, String, String)}, which is the
     * base-0 reading: {@code 010} is 8, {@code 08} is 0 and {@code 0x10} is 16. The grammar's
     * {@code INTEGER} token is decimal digits with an optional {@code -}, so the prefix forms, a
     * {@code +} and leading whitespace cannot arrive from a data file. A figure past
     * {@code Integer.MAX_VALUE} is clamped to it.
     *
     * <p>A record is refused with a {@link DataFileParseException} when:
     * <ul>
     *   <li>the first-spell level, the spell weight or the book count starts with {@code -};</li>
     *   <li>any of the three has no digit; or</li>
     *   <li>the record holds more {@code book:} blocks than the book count declares (C:
     *       {@code PARSE_ERROR_TOO_MANY_ENTRIES}). A count of 0 therefore refuses every book,
     *       which matches C, where a zero-length allocation leaves the book array {@code NULL} and
     *       the first {@code book:} line fails the same test.</li>
     * </ul>
     * Fewer books than declared is accepted, as in C, where the count only sizes the array. The
     * checks run in the order above for a single record, and a fault in an earlier record stops
     * the load before a later record is read.
     *
     * <p>Function assemble coded before 260915, commented in full on 261028.
     *
     * @param records  the raw magic record(s) for one class, normally exactly one
     * @param errors   not written to by this method; it is passed on to {@link ClassSpellBookAssembler},
     *                 which appends a message for each book it skips
     * @param filename the class file being read, for the error message
     * @return one {@link ClassMagic} per record, in order, built from the books that were not
     *         skipped
     * @throws DataFileParseException if a scalar is negative or not a number, or the record has
     *                                more books than it declares
     */
    @Override
    public List<ClassMagic> assemble(@NotNull List<ClassMagicParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<ClassMagic> results = new ArrayList<>();
        
        for (ClassMagicParseRecord record : records) {
            int line = record.line();
            if (ParseUtils.minusFound(record.firstSpell())) {
                String msg = "Class magic starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative first spell value: " + record.firstSpell();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int firstSpell = ParseUtils.parseIntString("Class magic", record.firstSpell(), line, filename, "first spell");

            if (ParseUtils.minusFound(record.spellWeight())) {
                String msg = "Class magic starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative spell weight value: " + record.spellWeight();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int spellWeight = ParseUtils.parseIntString("Class magic", record.spellWeight(), line, filename, "spell weight");

            if (ParseUtils.minusFound(record.numBooks())) {
                String msg = "Class magic starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal negative number of books value: " + record.numBooks();
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }
            int numBooks = ParseUtils.parseIntString("Class magic", record.numBooks(), line, filename, "number of books");
            
            List<MagicBook> magicBooks = new ClassSpellBookAssembler().assemble(record.books(), errors, filename);
            
            if (record.books().size() > numBooks) {
                String msg = "Class magic starting at line: " + line + " of file: " + filename + " has " +
                        "one or more illegal book entries in the game datafile";
                logger.fatal(msg);
                throw new DataFileParseException(msg);
            }

            results.add(new ClassMagic(firstSpell, spellWeight, magicBooks));
        }

        return results;
    }
}
