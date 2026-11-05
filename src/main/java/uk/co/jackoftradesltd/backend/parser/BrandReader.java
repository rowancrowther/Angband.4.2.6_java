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

package uk.co.jackoftradesltd.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.brand.BrandAssembler;
import uk.co.jackoftradesltd.backend.parser.brand.BrandParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.brand.BrandGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.brand.BrandLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.Brand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/brand.txt} into {@link Brand} objects - the catalogue of elemental
 * brands (ACID_3, FIRE_2 and so on) that ego items, artifacts, object kinds and timed effects name
 * by code. It is the thin hand-written bridge between the generated {@code BrandLexer} /
 * {@code BrandGrammar} pair and the game, plugged into the shared {@link GrammarDriver} pipeline
 * and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code brand_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_brand} and the {@code parse_brand_*} handlers become the grammar plus
 *       {@link BrandAssembler}: the grammar collects each record's raw text, and the assembler
 *       resolves the {@code resist-flag:} and {@code vuln-flag:} names to monster race flags and
 *       turns the three numbers into ints;</li>
 *   <li>{@code run_parse_brand} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_brand} becomes the list handed back, one {@link Brand} per
 *       {@code code:} block. Copying the chain into the {@code brands} array, setting
 *       {@code z_info->brand_max} and the 254-entry limit are not done here;
 *       {@code ObjectDataLoader.loadBrands} hands the list to {@code ObjectRegistry}, and its
 *       Javadoc records that the limit is not applied.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> {@link #extract} returns the parse records in file order, but the
 * {@link Brand} list that comes back is in <em>reverse</em> file order, because
 * {@link BrandAssembler} puts each brand at the front. That is C's order: {@code parse_brand_code}
 * pushes each record on the head of a chain, and {@code finish_parse_brand} fills {@code brands}
 * from slot 1 upward starting at that head, so slot 1 is the last record in the file (POIS_2 in the
 * shipped data) and ACID_3 comes last. It matters wherever two brands share a name and the first is
 * kept, such as {@code Rune.initRunes}.
 * <p>
 * As in C, the first error stops the load and nothing is returned: a syntax error is thrown from
 * {@link #extract} as a {@code ParseCancellationException}, and a record the assembler refuses (an
 * unknown resist or vulnerability flag, a number too large for an int) is thrown as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>C takes the directives in any order, leaves any of them out, and lets a repeated one
 *       overwrite. The grammar insists on {@code code:}, {@code name:}, {@code verb:},
 *       {@code multiplier:}, {@code o-multiplier:}, {@code power:} and {@code resist-flag:} in that
 *       order with an optional {@code vuln-flag:} last, so any other shape is a syntax error and
 *       costs the whole file; see {@code docs/grammar_issues.md}.</li>
 *   <li>A brand with no {@code vuln-flag:} line carries {@code RF_NONE}, as C's zero-filled record
 *       carries flag 0. {@code resist-flag:NONE} and {@code vuln-flag:NONE} are accepted here and
 *       rejected by C's {@code lookup_flag}; see {@code docs/Assembler_Issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot 0. This is the accepted
 *       deviation recorded in the registry's class block, so {@code brand_max} is the plain record
 *       count, one lower than C's.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class BrandReader coded before 260915, commented in full on 261017, updated on 261105 for the
 * removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class BrandReader implements Reader<Brand> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code brand.txt} is logged under this reader's name before it is rethrown. Syntax and
     * assembler errors do not go through it; they are thrown unlogged to the caller.
     *
     * <p>Field logger coded before 260915, commented in full on 261017, updated on 261105 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger();


    /**
     * Parse the given file and return just the assembled brands, in C's order (the reverse of the
     * file's, see the class block), unwrapped from the {@link ParseResult} that
     * {@link #parseWithResults} gives. Satisfies the {@link Reader} contract.
     * <p>
     * A syntax error or a refused record throws, exactly as in {@link #parseWithResults}, so an
     * empty list only ever means a clean file with no records.
     *
     * <p>Function parse coded before 260915, commented in full on 261017, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the brand data file to read
     * @return every {@link Brand} in the file, in C's order
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Brand> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, and hand back the raw
     * {@link BrandParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler. That covers the
     * cases where C returns a parser error too: a directive before any {@code code:} line (C's
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER}) and a numeric directive with no number.
     * <p>
     * The records stay in file order here. The reversal into C's order is done later, by
     * {@link BrandAssembler}. The copy at the end detaches the result from the parse tree, so the
     * returned list is the caller's to keep.
     *
     * <p>Function extract commented in full before 260915, provenance stamp added on 260915,
     * commented in full again on 261017, updated on 261105 for the removal of the soft-error path
     * and the {@code record-count:} header.
     *
     * @param parser       the generated Brand parser
     * @param errorCatcher the live hard-error collector installed by the driver
     * @return the parsed {@link BrandParseRecord}s in file order
     */
    private static @NotNull List<BrandParseRecord> extract(
            @NotNull BrandGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        BrandGrammar.FileContext output = parser.file();
        List<BrandParseRecord> results = output.records;
        errorCatcher.throwIfAny();

        return new ArrayList<>(results);
    }

    /**
     * Parse the given file, returning the assembled {@link Brand} list (in C's order, the reverse
     * of the file's) wrapped in a {@link ParseResult}.
     * <p>
     * This is the Java counterpart of {@code run_parse_brand} and the {@code parse_brand_*}
     * handlers in {@code obj-init.c}: it wires {@code BrandLexer}, {@code BrandGrammar},
     * {@link #extract} and {@link BrandAssembler} into {@link GrammarDriver#run}. As C stops at the
     * first error, so does this: a syntax error throws a {@code ParseCancellationException}, an
     * unresolved flag or a number too large for an int throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, and no result is
     * returned. An unreadable file is an {@link IOException}, the analogue of
     * {@code parse_file_quit_not_found}'s {@code quit()}.
     *
     * <p>Function parseWithResults commented in full before 260915, provenance stamp added on 260915,
     * commented in full again on 261017, updated on 261105 for the removal of the soft-error path.
     *
     * @param filename the brand data file to read
     * @return every brand in the file, in C's order
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<Brand> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                BrandLexer::new,
                BrandGrammar::new,
                BrandReader::extract,
                new BrandAssembler(), logger);
    }
}
