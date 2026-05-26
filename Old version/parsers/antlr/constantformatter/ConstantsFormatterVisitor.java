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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ConstantsFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.constantformatter;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ConstantsFormatterParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ConstantsFormatterVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#section}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection(ConstantsFormatterParser.SectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#furtherValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFurtherValue(ConstantsFormatterParser.FurtherValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#multiValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiValue(ConstantsFormatterParser.MultiValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(ConstantsFormatterParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ConstantsFormatterParser.FileContext ctx);
}