// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ConstantsFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.constantformatter;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConstantsFormatterParser}.
 */
public interface ConstantsFormatterListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ConstantsFormatterParser#section}.
	 * @param ctx the parse tree
	 */
	void enterSection(ConstantsFormatterParser.SectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConstantsFormatterParser#section}.
	 * @param ctx the parse tree
	 */
	void exitSection(ConstantsFormatterParser.SectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConstantsFormatterParser#furtherValue}.
	 * @param ctx the parse tree
	 */
	void enterFurtherValue(ConstantsFormatterParser.FurtherValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConstantsFormatterParser#furtherValue}.
	 * @param ctx the parse tree
	 */
	void exitFurtherValue(ConstantsFormatterParser.FurtherValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConstantsFormatterParser#multiValue}.
	 * @param ctx the parse tree
	 */
	void enterMultiValue(ConstantsFormatterParser.MultiValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConstantsFormatterParser#multiValue}.
	 * @param ctx the parse tree
	 */
	void exitMultiValue(ConstantsFormatterParser.MultiValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConstantsFormatterParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(ConstantsFormatterParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConstantsFormatterParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(ConstantsFormatterParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConstantsFormatterParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ConstantsFormatterParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConstantsFormatterParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ConstantsFormatterParser.FileContext ctx);
}