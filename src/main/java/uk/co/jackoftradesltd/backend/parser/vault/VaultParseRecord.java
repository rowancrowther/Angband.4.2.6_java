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

package uk.co.jackoftradesltd.backend.parser.vault;

import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

import java.util.List;

/**
 * One raw vault record straight out of
 * {@link uk.co.jackoftradesltd.backend.parser.grammars.vault.VaultGrammar}: the directive lines of
 * a single {@code vault.txt} entry, in the order the file wrote them, each still as the raw text the
 * lexer matched. Nothing is parsed, typed or checked here; interpreting the lines is
 * {@link VaultAssembler}'s job, so this record is the boundary between "the file parsed" and "the
 * file makes sense".
 *
 * <p>In C there is no such object. {@code generate.c} handles each line as it is read, through the
 * {@code parse_vault_*} handlers, writing straight into the {@code struct vault} that
 * {@code parse_vault_name()} began; the "record" exists only as that half-filled struct. This
 * record stands in for it by keeping the lines until the assembler is ready.
 *
 * <p>Every record opens with its {@code name:} directive, which is the one the grammar requires
 * first, as C's {@code parse_vault_name()} starts the struct that every other handler needs. The
 * rest ({@code type:}, {@code rating:}, {@code rows:}, {@code columns:}, {@code min-depth:},
 * {@code max-depth:}, {@code flags:} and {@code D:}) follow in file order, in any mix, and a
 * directive may repeat: several {@code flags:} lines are all kept, and so is every {@code D:} line,
 * one per row of the layout. The list is the grammar's own, never {@code null}, and an absent
 * directive is simply not in it. Whether a record that never says {@code rows:} or {@code type:} is
 * acceptable, and what a repeated {@code rows:} line means, is for the assembler to decide.
 *
 * <p>Each entry is a {@link Directive}: its name as C spells it, its values as text, and the line
 * of the file it came from, which is what the assembler's error messages quote. The record itself
 * carries no line number of its own; the line of its {@code name:} directive is the one that
 * identifies it.
 *
 * <p>Record VaultParseRecord coded before 260915, commented in full on 261118 (first commented
 * 260915; rewritten 261118 once the named fields gave way to the one list of directives).
 *
 * @param directives the record's directive lines in file order, {@code name:} first; never
 *                   {@code null}
 */
public record VaultParseRecord(List<Directive> directives) {
}
