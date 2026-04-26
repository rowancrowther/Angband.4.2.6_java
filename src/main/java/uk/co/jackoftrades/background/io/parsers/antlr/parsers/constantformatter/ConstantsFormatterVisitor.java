// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ConstantsFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.parsers.constantformatter;

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
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection(ConstantsFormatterParser.SectionContext ctx);

	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#furtherValue}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFurtherValue(ConstantsFormatterParser.FurtherValueContext ctx);

	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#multiValue}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiValue(ConstantsFormatterParser.MultiValueContext ctx);

	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#line}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(ConstantsFormatterParser.LineContext ctx);

	/**
	 * Visit a parse tree produced by {@link ConstantsFormatterParser#file}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ConstantsFormatterParser.FileContext ctx);
}