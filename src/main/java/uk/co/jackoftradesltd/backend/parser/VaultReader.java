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
import uk.co.jackoftradesltd.backend.parser.grammars.vault.VaultGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.vault.VaultLexer;
import uk.co.jackoftradesltd.backend.parser.vault.VaultAssembler;
import uk.co.jackoftradesltd.backend.parser.vault.VaultParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code vault.txt} into a list of {@link Vault}s. The four vault-specific pieces —
 * {@link VaultLexer}, {@link VaultGrammar}, {@link #extract} and {@link VaultAssembler} — are
 * handed to {@link GrammarDriver}, which owns the shared lex/parse/assemble ritual every
 * {@code lib/gamedata} reader follows.
 *
 * <p>The one file covers all seven room-builder types C keeps in {@code vaults} — the three vault
 * sizes, their newer variants, and the interesting rooms — since the data distinguishes them only
 * by the {@code type:} line.
 *
 * <p><b>C counterpart:</b> {@code vault_parser} in {@code generate.c}, run as the last of the three
 * parsers in {@code run_template_parser}. Its nine {@code parse_vault_*} handlers, registered by
 * {@code init_parse_vault}, are performed by {@link VaultAssembler}, as is the building of the
 * list that {@code finish_parse_vault} hands to the global {@code vaults}; {@code run_parse_vault}
 * ({@code parse_file_quit_not_found}) is the file open inside {@link GrammarDriver}, where a
 * missing file is an {@link IOException} for {@code DungeonLoader.loadVaultTemplates} to turn into
 * a fatal error. This class itself only wires the grammar to the driver, so it has no C logic of
 * its own to mismatch.
 *
 * <p><b>Order of the result:</b> the list is in reverse file order, because
 * {@link VaultAssembler} prepends each vault, as {@code parse_vault_name} does by pointing the new
 * record's {@code next} at the previous head. {@link #extract} itself keeps file order.
 *
 * <p><b>Where the Java differs from C</b> (the grammar half is recorded in
 * {@code docs/grammar_issues.md}, the assembler half in {@code docs/Assembler_Issues.md}): C
 * registers nine independent one-line directives and so accepts them in any order, missing,
 * repeated or indented; {@code VaultGrammar} demands one or more records in the fixed order
 * {@code name:} to {@code max-depth:}, any number of {@code flags:} lines, then the {@code D:}
 * lines.
 *
 * <p><b>Errors:</b> as in C, where every {@code PARSE_ERROR_*} result stops the load, the first
 * error stops it here and nothing is returned. A syntax error is thrown from {@link #extract} as a
 * {@code ParseCancellationException}, and a record the assembler refuses is thrown as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here.
 *
 * <p>Class VaultReader coded before 260915, commented in full on 261019, updated on 261105 for the
 * removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class VaultReader implements Reader<Vault> {
    /**
     * Logger handed to {@link GrammarDriver#run}, explicitly scoped to {@code VaultReader.class}
     * rather than inferred, so an I/O failure opening the data file is reported under this reader's
     * name. C has no counterpart: it reports a missing file through {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private final static Logger logger = LogManager.getLogger(VaultReader.class);

    /**
     * Parse the file and return just the assembled vaults, unwrapped from the {@link ParseResult}
     * that {@link #parseWithResults} gives.
     *
     * <p>Any error throws exactly as it does there, so there is no partial result, as in C. The C
     * counterpart is the {@code vault_parser} run as a whole ({@code generate.c}).
     *
     * <p>Function parse coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to parse
     * @return every vault in the file, in reverse file order
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Vault> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot perform itself: run the parser's
     * {@code file} rule and pull the raw records out of its result.
     *
     * <p>Ordering is deliberate. {@link ParseErrors#throwIfAny()} fires straight after the parse, so
     * a hard grammar or lexer error fails closed with a {@code ParseCancellationException} before
     * any record is used. The records come back in file order, and the grammar's own
     * {@code ArrayList} is copied so the caller owns the result. {@link VaultAssembler} then
     * type-checks them, and is where the order becomes C's.
     *
     * <p>Function extract coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the constructed parser, ready to run its entry rule
     * @param errorCatcher the installed error listener; {@link ParseErrors#throwIfAny()} must run
     *                     before the records are read out, so a lexer/parser failure aborts here
     *                     rather than handing back a partially-built list
     * @return the raw parse records, in file order
     */
    private static List<VaultParseRecord> extract (
            @NotNull VaultGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        VaultGrammar.FileContext output = parser.file();
        List<VaultParseRecord> result = output.profiles;
        errorCatcher.throwIfAny();
        
        return new ArrayList<>(result);
    }
    
    /**
     * Parses {@code filename} and returns the assembled vaults, in reverse file order, wrapped in a
     * {@link ParseResult}. This is the entry point {@code DungeonLoader.loadVaultTemplates} uses.
     *
     * <p>A missing or unreadable file is an {@link IOException} (C: {@code parse_file_quit_not_found}
     * quits). A hard grammar or lexer error throws a {@code ParseCancellationException}. Every
     * problem {@link VaultAssembler} finds (an unknown {@code type:}, a size over the room builder's
     * cap, a ragged layout, a bad flag) throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, as C stops on a
     * {@code PARSE_ERROR_*}. No result is returned for a file that fails.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the data file to parse
     * @return every vault in the file, in reverse file order
     * @throws IOException if the file cannot be read
     */
    public ParseResult<Vault> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                VaultLexer::new,
                VaultGrammar::new,
                VaultReader::extract,
                new VaultAssembler(), logger);
    }
}
