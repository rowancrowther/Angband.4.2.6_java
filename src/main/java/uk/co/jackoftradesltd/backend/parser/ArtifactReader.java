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
import uk.co.jackoftradesltd.backend.parser.artifact.ArtifactAssembler;
import uk.co.jackoftradesltd.backend.parser.artifact.ArtifactParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.artifact.ArtifactGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.artifact.ArtifactLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.Artifact;

import java.io.IOException;
import java.util.List;

/**
 * Loads {@code lib/gamedata/artifact.txt} into {@link Artifact} objects - the table of named
 * artifacts ("of Galadriel", 'Narthanc', "of Morgoth") that {@code a_info} holds in C. It is the
 * thin hand-written bridge between the generated {@code ArtifactLexer} / {@code ArtifactGrammar}
 * pair and the game, plugged into the shared {@link GrammarDriver} pipeline and implementing the
 * common {@link Reader} contract.
 * <p>
 * It stands in for {@code artifact_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_artifact} and the {@code parse_artifact_*} handlers become the grammar
 *       plus {@link ArtifactAssembler}: the grammar collects each record's raw text, and the
 *       assembler resolves the {@code base-object:} tval and sval into an object base and kind, the
 *       flags into object flags, the {@code values:} entries into modifiers and resistances, and the
 *       brands, slays, curses and activation into their registry entries, and turns the numbers and
 *       dice into ints and {@code Random}s;</li>
 *   <li>{@code run_parse_artifact} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_artifact} becomes the list handed back, one {@link Artifact} per
 *       {@code name:} block. C counts the chain to set {@code a_max}, copies it into {@code a_info}
 *       numbering each entry {@code aidx}, and builds {@code aup_info} beside it; here
 *       {@code ObjectDataLoader.loadArtifacts} hands the list to
 *       {@code ObjectRegistry.setArtifacts}, which records the count, and each {@link Artifact}
 *       creates its own {@code ArtifactUpkeep}. The rest of that C function is not in this class:
 *       the curse-kind pass ({@code write_curse_kinds}) is {@code ObjectDataLoader.parseCurseKinds},
 *       and the lookup of the sentinel kinds is made earlier, by
 *       {@code ObjectRegistry.loadUnknownKinds}.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> The list is in file order, which is {@code a_info} order:
 * {@code parse_artifact_name} pushes each record on the head of a chain, but
 * {@code finish_parse_artifact} starts at that head with the highest index and counts down, so the
 * first record in the file is {@code aidx} 1. (The sibling readers whose C lists run last-first,
 * such as {@code SlayReader}, are the opposite case.) The first record of the shipped file is "of
 * Galadriel" and the last is "of Morgoth". An {@link Artifact} carries no index of its own, so its
 * place in the list is its index, and a record the assembler skips moves every later artifact up one
 * place.
 * <p>
 * <strong>The kind table is written to as a side effect.</strong> A record whose {@code base-object:}
 * sval is not in {@code object.txt} gets an object kind of its own, as C's
 * {@code write_dummy_object_record} does while parsing; in the shipped file those are the 14
 * records with a {@code graphics:} line (three lights, six rings and five amulets). So
 * {@code object.txt} must already be loaded, and the brands, slays, curses and activations likewise.
 * For those kinds {@code weight:} and {@code cost:} are copied onto the kind, {@code graphics:} sets
 * its glyph and colour, and a light's {@code act:} and {@code time:} belong to the kind and not to
 * the artifact. A second parse of the same file finds the kinds the first one left behind and
 * registers none; see {@code ObjectDataLoader.loadArtifacts} for why that matters at birth.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code artifact.txt} has no such line, and this repository's copy adds one.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (an unknown tval, flag, value name, element, brand, slay, curse or
 *       activation, or a number or dice string that does not parse) skips only the offending record
 *       and the rest still load.</li>
 *   <li>C takes the directives in any order and lets a repeated scalar one overwrite while
 *       {@code flags:}, {@code values:}, {@code slay:}, {@code brand:}, {@code msg:} and
 *       {@code desc:} accumulate (the message and description lines are joined with no separator).
 *       The grammar does the same, but a record with nothing after its {@code name:} line is a
 *       syntax error here, and the grammar and assembler differ from C in several smaller ways; see
 *       {@code docs/grammar_issues.md} and {@code docs/Assembler_Issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot at the front, which is where
 *       C keeps it. This is the accepted deviation recorded in the registry's class block, so the
 *       count is the plain record count, one lower than C's {@code a_max}.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class ArtifactReader coded before 260915, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
public class ArtifactReader implements Reader {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code artifact.txt} is logged under this reader's name before it is rethrown. Syntax and soft
     * errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses {@code filename} and returns just the assembled artifacts, in file order, discarding
     * the soft-error channel (use {@link #parseWithResults} to inspect errors). Satisfies the
     * {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty result are indistinguishable
     * here: a syntax error gives an empty list, and a record skipped for an unknown tval, flag,
     * brand, slay, curse or activation just goes missing. Callers that must tell a partial or failed
     * load from a clean one have to use {@link #parseWithResults}, as
     * {@code ObjectDataLoader.loadArtifacts} does.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the artifacts read from the file (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Artifact> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: runs the parser's {@code file} entry
     * rule, fails closed on any hard lexer/parser error, validates the {@code record-count:} header,
     * and hands back the raw {@link ArtifactParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a directive before any
     * {@code name:} line (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}), an unknown directive
     * ({@code PARSE_ERROR_UNDEFINED_DIRECTIVE}) and a directive with nothing after its colon, such as
     * an empty {@code desc:}. The header check is soft - {@link GrammarDriver#checkRecordCount} only
     * appends to {@code errors}, so a miscount never discards the records that did parse.
     * <p>
     * The records stay in file order, which is the order the artifacts are returned in. The list is
     * the grammar's own, not a copy.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated {@code ArtifactGrammar} parser, positioned at the start of the file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link ArtifactParseRecord}s in file order
     */
    private static List<ArtifactParseRecord> extract(
            @NotNull ArtifactGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        ArtifactGrammar.FileContext output = parser.file();
        List<ArtifactParseRecord> result = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return result;
    }

    /**
     * Parses {@code filename} and returns both the assembled {@link Artifact} list (in file order)
     * and any soft errors gathered along the way, for callers that need to tell a clean load from a
     * partial one. {@link #parse} is the items-only convenience over this.
     * <p>
     * This is the Java counterpart of {@code run_parse_artifact} and the {@code parse_artifact_*}
     * handlers in {@code obj-init.c}: it wires {@code ArtifactLexer}, {@code ArtifactGrammar},
     * {@link #extract} and {@link ArtifactAssembler} into {@link GrammarDriver#run}. A syntax error
     * returns no items and the collected messages; an unresolved tval, flag, value name, brand, slay,
     * curse or activation, or a {@code record-count:} mismatch, leaves the good records in the result
     * alongside the messages, so the list can be short without anything having been thrown. C would
     * have stopped at the first of any of these (bar an unknown {@code act:} name, which C does not
     * treat as an error at all). An unreadable file is not a result at all but an
     * {@link IOException}, the analogue of {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * The first call also registers the object kinds of the special artifacts in
     * {@code ObjectRegistry} (see the class description); a repeat call finds them and adds none.
     * <p>
     * This is the variant callers should prefer: {@code ObjectDataLoader.loadArtifacts} gates on the
     * errors for exactly that reason.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the artifact data file to read
     * @return a {@link ParseResult} of the assembled artifacts plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<Artifact> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ArtifactLexer::new,
                ArtifactGrammar::new,
                ArtifactReader::extract,
                new ArtifactAssembler(), logger);
    }
}
