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
import uk.co.jackoftradesltd.backend.parser.grammars.objectproperty.ObjectPropertyGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.objectproperty.ObjectPropertyLexer;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyAssembler;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;

import java.io.IOException;
import java.util.List;

/**
 * Loads {@code lib/gamedata/object_property.txt} into {@link ObjectProperty} objects - the
 * catalogue of every generic object property (the five stats, the modifiers, the flags, and the
 * elemental ignore, resistance, vulnerability and immunity entries), each with the power and
 * multiplier used in object valuation, the per-tval multipliers, the adjectives, notice message and
 * description used to present it, and an optional binding to a user-interface entry. It is the thin
 * hand-written bridge between the generated {@code ObjectPropertyLexer} /
 * {@code ObjectPropertyGrammar} pair and the game, plugged into the shared {@link GrammarDriver}
 * pipeline and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code object_property_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_object_property} and the {@code parse_object_property_*} handlers
 *       ({@code name}, {@code type}, {@code subtype}, {@code id-type}, {@code code}, {@code power},
 *       {@code mult}, {@code type-mult}, {@code adjective}, {@code neg-adjective}, {@code msg},
 *       {@code desc} and {@code bindui}) become the grammar plus {@link ObjectPropertyAssembler}:
 *       the grammar collects each record's raw text, and the assembler resolves the {@code type:},
 *       {@code subtype:} and {@code id-type:} words to enums, resolves {@code code:} against the
 *       table the {@code type:} selects (modifiers, flags or elements), parses the integers and
 *       builds the per-tval multipliers;</li>
 *   <li>{@code run_parse_object_property} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_object_property} becomes the list handed back, one
 *       {@link ObjectProperty} per {@code name:} block. Copying the chain into the
 *       {@code obj_properties} array and setting {@code z_info->property_max} are done by
 *       {@code ObjectDataLoader.loadObjectProperties}, which hands the list to
 *       {@code ObjectRegistry.setObjectProperties};</li>
 *   <li>{@code cleanup_object_property} has no counterpart, as the garbage collector frees the
 *       records.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> The list comes back in file order, and that is C's order too, unlike
 * the slays, brands and curses. {@code parse_object_property_name} pushes each record on the head of
 * a chain, but {@code finish_parse_object_property} copies the chain into {@code obj_properties}
 * from the top slot downward, so slot 1 holds the first record in the file ({@code strength} in the
 * shipped data) and the last record ({@code cold immunity}) holds the top slot. Nothing is reversed
 * anywhere in the pipeline.
 * <p>
 * <strong>{@code bindui:}.</strong> C's handler calls
 * {@code bind_object_property_to_ui_entry_by_name} while it reads the line, which links the property
 * to the UI entry at once. The port does not: the assembler stores the entry's name (with any
 * {@code <TAG>} folded in), the aux flag and the optional explicit value as an
 * {@link ObjectProperty.UIBinding}, and {@code UIEntryValueRegistry} looks the entry up when it is
 * needed.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code object_property.txt} has no such line, and this repository's copy adds it
 *       (79 records).</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but
 *       an assembler error (an unknown type, subtype, id-type or code, a record with no {@code code:}
 *       at all, a subtype on a record that is not a flag, a malformed integer) skips only the
 *       offending record and the rest still load. A
 *       {@code type-mult:} naming no tval is the exception: the line is dropped and the record is
 *       kept.</li>
 *   <li>C takes the directives after {@code name:} in any order, accepts a record with no
 *       {@code type:} or with {@code type:} after other directives, and accepts a file with no
 *       records. The grammar needs {@code name:} then {@code type:} then at least one more
 *       directive in any order, and at least one record. A second {@code msg:} or {@code desc:}
 *       appends where C replaces it, a second {@code bindui:} replaces the first where C binds both,
 *       and {@code adjective:} takes one lower-case word where C takes any string; see
 *       {@code docs/grammar_issues.md}.</li>
 *   <li>A {@code code:} of {@code NONE} or {@code MAX} on a modifier, or {@code MAX} on a flag, or
 *       either on an element, resolves to a sentinel constant and loads, where C rejects it; and an
 *       aux written as {@code 00} or {@code -0} is true, where C reads zero. See
 *       {@code docs/Assembler_Issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot 0. This is the accepted
 *       deviation recorded in the registry's class block, so {@code property_max} is the plain
 *       record count, one lower than C's. A record's {@code type_mult} slots are not all filled
 *       with 1: only the tvals the file names are stored, and {@code ObjectProperty.getTypeMult}
 *       answers 1 for the rest.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class ObjectPropertyReader coded before 260915, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
public class ObjectPropertyReader implements Reader<ObjectProperty> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code object_property.txt} is logged under this reader's name before it is rethrown. Syntax
     * and soft errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled object properties, in file order (which is
     * C's {@code obj_properties} order, see the class block), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list, and a record skipped for an unknown code or subtype just goes
     * missing. Callers that must tell a partial or failed load from a clean one have to use
     * {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the object properties read from the file
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<ObjectProperty> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link ObjectPropertyParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a directive before any
     * {@code name:} line (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}), an unknown directive
     * ({@code PARSE_ERROR_UNDEFINED_DIRECTIVE}) and a numeric directive with no number
     * ({@code PARSE_ERROR_NOT_NUMBER}), and {@code code:} before {@code type:}
     * ({@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE}), which is a syntax error here because the record
     * shape puts {@code type:} straight after {@code name:}. The header check is soft - {@link GrammarDriver#checkRecordCount} only appends to {@code errors}, so a
     * miscount never discards the records that did parse.
     * <p>
     * The records stay in file order, and that is the order the assembler keeps. The list returned is
     * the one the grammar built, not a copy.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated object-property parser, positioned at the start of the file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, appended to on a record-count mismatch
     * @return the parsed {@link ObjectPropertyParseRecord}s in file order
     */
    private static List<ObjectPropertyParseRecord> extract(
            @NotNull ObjectPropertyGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        ObjectPropertyGrammar.FileContext output = parser.file();
        List<ObjectPropertyParseRecord> result = output.properties;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return result;
    }

    /**
     * Parse the given file, returning both the assembled {@link ObjectProperty} list (in file order,
     * see the class block) and any soft errors gathered along the way, for callers that need to tell
     * a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_object_property} and the
     * {@code parse_object_property_*} handlers in {@code obj-init.c}: it wires
     * {@code ObjectPropertyLexer}, {@code ObjectPropertyGrammar}, {@link #extract} and
     * {@link ObjectPropertyAssembler} into {@link GrammarDriver#run}. A syntax error returns no items
     * and the collected messages; an unresolved type, subtype, id-type or code, a subtype on a record
     * that is not a flag, a malformed integer, a {@code type-mult:} naming no tval or a
     * {@code record-count:} mismatch leaves the good records in the result alongside the messages, so
     * the list can be short without anything having been thrown. C would have stopped at the first of
     * any of these. An unreadable file is not a result at all but an {@link IOException}, the analogue
     * of {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code ObjectDataLoader.loadObjectProperties} gates
     * on the errors for exactly that reason.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the object-property data file to read
     * @return the parse result: the object properties plus any error messages
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<ObjectProperty> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ObjectPropertyLexer::new,
                ObjectPropertyGrammar::new,
                ObjectPropertyReader::extract,
                new ObjectPropertyAssembler(), logger);
    }
}