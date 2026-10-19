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
 * repeated or indented; {@code VaultGrammar} demands a {@code record-count:} header, which the
 * shipped C file does not carry and which {@link GrammarDriver#checkRecordCount} checks, and then
 * one or more records in the fixed order {@code name:} to {@code max-depth:}, any number of
 * {@code flags:} lines, then the {@code D:} lines. A bad record costs only itself in Java, where
 * every one of C's {@code PARSE_ERROR_*} results stops the load.
 *
 * <p>Class VaultReader coded before 260915, commented in full on 261019.
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
     * Parse the file and return just the assembled vaults, discarding the error list.
     *
     * <p>This is {@link #parseWithResults} with its soft errors thrown away, so a caller that needs
     * to know a vault was dropped (an unknown {@code type:}, a size over the room builder's cap, a
     * ragged layout, a bad flag, a record-count mismatch) must use that method instead. A hard
     * grammar error gives an empty list here, indistinguishable from a file with no vaults. The C
     * counterpart is the {@code vault_parser} run as a whole ({@code generate.c}); C has no
     * partial result.
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
     *
     * @param filename the data file to parse
     * @return the successfully assembled vaults, in reverse file order
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
     * a hard grammar or lexer error fails closed before the soft
     * {@link GrammarDriver#checkRecordCount} check runs against whatever did parse. That check
     * compares the file's {@code record-count:} header with the number of records parsed and is a
     * soft error on mismatch; C's own parser never reads this header, so a mismatch here is
     * stricter than the original. The records come back in file order, and the grammar's own
     * {@code ArrayList} is copied so the caller owns the result. {@link VaultAssembler} then
     * type-checks them, and is where the order becomes C's.
     *
     * <p>Function extract coded before 260915, commented in full on 261019.
     *
     * @param parser       the constructed parser, ready to run its entry rule
     * @param errorCatcher the installed error listener; {@link ParseErrors#throwIfAny()} must run
     *                     before the records are read out, so a lexer/parser failure aborts here
     *                     rather than handing back a partially-built list
     * @param errors       soft-error sink for the record-count check
     * @return the raw parse records, in file order
     */
    private static List<VaultParseRecord> extract (
            @NotNull VaultGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        VaultGrammar.FileContext output = parser.file();
        List<VaultParseRecord> result = output.profiles;
        errorCatcher.throwIfAny();
        
        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);
        
        return new ArrayList<>(result);
    }
    
    /**
     * As {@link #parse}, but keeps the soft errors alongside the assembled vaults rather than
     * discarding them. The preferred entry point when the caller wants to gate on
     * {@link ParseResult#hasErrors()}, as {@code DungeonLoader.loadVaultTemplates} does before
     * storing the result.
     *
     * <p>A missing or unreadable file is an {@link IOException} (C: {@code parse_file_quit_not_found}
     * quits). A hard grammar or lexer error gives an empty list with the messages as errors. Every
     * problem {@link VaultAssembler} finds, where C would stop on a {@code PARSE_ERROR_*}, is a
     * soft error that drops that one vault and leaves the rest.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the data file to parse
     * @return the assembled vaults, in reverse file order, plus any soft errors gathered along the way
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
