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
 * @author Rowan Crowther
 */
public class VaultReader implements Reader <Vault> {
    private final static Logger logger = LogManager.getLogger(VaultReader.class);

    /**
     * @param filename the data file to parse
     * @return the successfully assembled vaults; soft errors are logged but not surfaced here
     */
    @Override
    public @NotNull List<Vault> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * As {@link #parse}, but keeps the soft errors alongside the assembled vaults rather than
     * discarding them.
     *
     * @param filename the data file to parse
     * @return the assembled vaults plus any soft errors gathered along the way
     */
    public ParseResult<Vault> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                VaultLexer::new,
                VaultGrammar::new,
                VaultReader::extract,
                new VaultAssembler(), logger);
    }
    
    /**
     * Runs the grammar's entry rule, fails closed on hard parse errors, checks the file's declared
     * {@code record-count:} against how many records actually parsed (a soft error on mismatch —
     * note C's own parser never validates this header at all, so a mismatch here is stricter than
     * the original), and hands back the raw records for {@link VaultAssembler} to type-check.
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
}
