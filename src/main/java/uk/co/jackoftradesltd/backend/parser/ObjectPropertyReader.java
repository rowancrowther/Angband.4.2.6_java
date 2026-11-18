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
import uk.co.jackoftradesltd.backend.parser.grammars.objectproperty2.ObjectProperty2Grammar;
import uk.co.jackoftradesltd.backend.parser.grammars.objectproperty2.ObjectProperty2Lexer;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectProperty2Assembler;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectProperty2ParseRecord;
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
 * hand-written bridge between the generated {@code ObjectProperty2Lexer} /
 * {@code ObjectProperty2Grammar} pair and the game, plugged into the shared {@link GrammarDriver}
 * pipeline and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code object_property_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_object_property} and the {@code parse_object_property_*} handlers
 *       ({@code name}, {@code type}, {@code subtype}, {@code id-type}, {@code code}, {@code power},
 *       {@code mult}, {@code type-mult}, {@code adjective}, {@code neg-adjective}, {@code msg},
 *       {@code desc} and {@code bindui}) become the grammar plus {@link ObjectProperty2Assembler}:
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
 * to the UI entry at once, and drops the bind silently if no entry has that name. The assembler
 * keeps that order. It reads the aux flag and the optional explicit value, asks
 * {@code GameConstants.uiEntryExists} whether an entry has the name (with any {@code <TAG>} folded
 * in), and only if one does stores the name, the aux flag and the value as an
 * {@link ObjectProperty.UIBinding}; a line naming no entry adds nothing. The names are the specs in
 * {@code GameConstants.entries}, so the reader must run after {@code GameConstants.init} has
 * received the front end's entries. {@code UIEntryValueRegistry} looks the entry up when it is
 * needed.
 * <p>
 * As in C, the first error stops the load and nothing is returned: a syntax error is thrown from
 * {@link #extract} as a {@code ParseCancellationException}, and a record the assembler refuses (an
 * unknown type, subtype, id-type or code, a {@code code:} or {@code bindui:} on a record with no
 * type, a {@code type-mult:} naming no tval, a malformed integer) is thrown as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>Like C, the grammar takes the directives after {@code name:} in any order and any number of
 *       times, accepts a record with no {@code type:} (it becomes an {@code OBJ_PROPERTY_NONE}
 *       property) and accepts a file with no records. The parse record keeps every directive line
 *       in file order, and {@link ObjectProperty2Assembler} applies them one at a time as C's
 *       handlers do: a repeated {@code msg:}, {@code desc:}, {@code adjective:} or
 *       {@code neg-adjective:} replaces the earlier text, every {@code bindui:} that names a UI
 *       entry binds, and
 *       {@code adjective:} takes any string. A {@code code:} or {@code bindui:} ahead of the
 *       {@code type:} is refused, as C refuses it, and a {@code type:} after a {@code code:} reads
 *       the same index through the new type's table.</li>
 *   <li>A record with no {@code msg:}, {@code desc:}, {@code adjective:} or {@code neg-adjective:}
 *       line has {@code null} there, where C has {@code NULL}.</li>
 *   <li>The index a {@code code:} line stores is the Java enum ordinal less one for the modifier and
 *       element tables, which carry a {@code _NONE} placeholder C's tables lack, so a {@code type:}
 *       line that follows a {@code code:} line can read one place apart from C when it crosses
 *       between the flag table and either of the others, and one whose carried index is past the end
 *       of the new table is refused where C loads it. See {@link ObjectProperty2Assembler}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot 0. This is the accepted
 *       deviation recorded in the registry's class block, so {@code property_max} is the plain
 *       record count, one lower than C's. A record's {@code type_mult} slots are not all filled
 *       with 1: only the tvals the file names are stored, and {@code ObjectProperty.getTypeMult}
 *       answers 1 for the rest.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customized copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class ObjectPropertyReader coded before 260915, commented in full on 261113 (updated on 261105
 * for the removal of the soft-error path, and for the {@code code:} sentinel and aux handling now
 * matching C; type-less records, repeated directives and the order differences added 261113; the
 * differences list rewritten 261115 for the switch to {@link ObjectProperty2Assembler}, which now
 * matches C on repeated and out-of-order directives; the {@code bindui:} paragraph rewritten 261116
 * for the check of the entry name).
 *
 * @author Rowan Crowther
 */
public class ObjectPropertyReader implements Reader<ObjectProperty> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code object_property.txt} is logged under this reader's name before it is rethrown. Syntax
     * and assembler errors do not go through it; they are thrown unlogged to the caller.
     *
     * <p>Field logger coded before 260915, commented in full on 261017, updated on 261105 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled object properties, in file order (which is
     * C's {@code obj_properties} order, see the class block), unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. Satisfies the {@link Reader}
     * contract.
     * <p>
     * A syntax error or a refused record throws, exactly as in {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return every object property in the file, in file order
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<ObjectProperty> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, and hand back the raw
     * {@link ObjectProperty2ParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler. That covers the
     * cases where C returns a parser error too: a directive before any {@code name:} line (C's
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER}), an unknown directive
     * ({@code PARSE_ERROR_UNDEFINED_DIRECTIVE}) and a numeric directive with no number
     * ({@code PARSE_ERROR_NOT_NUMBER}). {@code code:} or {@code bindui:} before {@code type:}
     * ({@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE}) is not among them: the grammar takes the
     * directives in any order, so that case is left to {@link ObjectProperty2Assembler}, which
     * applies the directives in file order and refuses the line when no {@code type:} line has come
     * before it.
     * <p>
     * The records stay in file order, and that is the order the assembler keeps. The list returned is
     * the one the grammar built, not a copy, and it holds no {@code null} entries.
     *
     * <p>Function extract coded before 260915, commented in full on 261113 (updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header; order handling
     * corrected 261113 and again 261115).
     *
     * @param parser       the generated object-property parser, positioned at the start of the file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @return the parsed {@link ObjectProperty2ParseRecord}s in file order
     */
    private static List<ObjectProperty2ParseRecord> extract(
            @NotNull ObjectProperty2Grammar parser,
            @NotNull ParseErrors errorCatcher) {
        ObjectProperty2Grammar.FileContext output = parser.file();
        List<ObjectProperty2ParseRecord> result = output.properties;
        errorCatcher.throwIfAny();

        return result;
    }

    /**
     * Parse the given file, returning the assembled {@link ObjectProperty} list (in file order, see
     * the class block) wrapped in a {@link ParseResult}.
     * <p>
     * This is the Java counterpart of {@code run_parse_object_property} and the
     * {@code parse_object_property_*} handlers in {@code obj-init.c}: it wires
     * {@code ObjectProperty2Lexer}, {@code ObjectProperty2Grammar}, {@link #extract} and
     * {@link ObjectProperty2Assembler} into {@link GrammarDriver#run}. As C stops at the first error,
     * so does this: a syntax error throws a {@code ParseCancellationException}, and an unresolved
     * type, subtype, id-type or code, a {@code code:} or {@code bindui:} on a record with no type, a
     * malformed integer or a {@code type-mult:} naming no tval throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, and no result
     * is returned. An unreadable file is an {@link IOException}, the analogue of
     * {@code parse_file_quit_not_found}'s {@code quit()}. A file with no records is not an error:
     * it gives an empty list, as C loads nothing.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261113 (updated on
     * 261105 for the removal of the soft-error path; type-less records and the empty file added
     * 261113).
     *
     * @param filename the object-property data file to read
     * @return every object property in the file, in file order; empty for a file with no records
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<ObjectProperty> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ObjectProperty2Lexer::new,
                ObjectProperty2Grammar::new,
                ObjectPropertyReader::extract,
                new ObjectProperty2Assembler(), logger);
    }
}