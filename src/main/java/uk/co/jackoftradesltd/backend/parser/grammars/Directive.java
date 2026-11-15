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

package uk.co.jackoftradesltd.backend.parser.grammars;

import java.util.List;

/**
 * One directive line of a data file, as the grammar hands it to an assembler: the directive's
 * name, the values on its line as raw text, and the line it was read from. It is a plain carrier
 * with no behaviour, and it checks nothing. Whether the name is one the file may use, how many
 * values it has, and whether they are legal are all the assembler's to decide.
 *
 * <p>In C there is no single type for this. {@code parser_parse} in {@code parser.c} cuts the line at
 * the first colon, finds the hook registered under that directive name, converts the fields into
 * typed values on the parser, and passes the parser to the hook function. The three parts of this
 * record match three things the C parser holds at that moment:
 * <ul>
 *   <li>{@link #name()}: the hook's directive name ({@code dir}), spelled as C spells it
 *       (for example {@code "type-mult"}).</li>
 *   <li>{@link #values()}: the field values. C holds them already converted ({@code int},
 *       {@code uint}, {@code sym}, {@code str}, {@code rand}); here they stay as the text the
 *       lexer matched, in file order, and the assembler converts them. A {@code str} field that C
 *       takes to the end of the line is one value, with its blanks kept.</li>
 *   <li>{@link #line()}: the line number C holds as {@code p->lineno}. It is 1-based, because C
 *       counts the line before it parses it and ANTLR numbers its first line 1. The grammar
 *       takes it from the directive's first token.</li>
 * </ul>
 *
 * <p>Nothing here stops a caller passing an empty or mutable list, or a name that matches no hook.
 * The grammars always pass a list they have just built, so a hand-built {@code Directive} (as the
 * tests build) is the only way to reach an unusual shape.
 *
 * <p>Coded on 261114, commented in full on 261115.
 *
 * @param name   the directive's name as C spells it, without the colon
 * @param values the directive's values as raw text, in file order; one for most directives, two
 *               for {@code type-mult}
 * @param line   the 1-based line of the directive in its file, used in the assembler's error text
 */
public record Directive(String name, List<String> values, int line) {
}
