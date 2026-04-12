// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/io/parsers/grammars/RandomFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.io.parsers;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RandomFormatterParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RandomFormatterVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(RandomFormatterParser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#mult_int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult_int(RandomFormatterParser.Mult_intContext ctx);
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#mult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult(RandomFormatterParser.MultContext ctx);
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#dice_int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDice_int(RandomFormatterParser.Dice_intContext ctx);
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#int_dice_int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt_dice_int(RandomFormatterParser.Int_dice_intContext ctx);
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#plus_int_dice_int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlus_int_dice_int(RandomFormatterParser.Plus_int_dice_intContext ctx);
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#plus_dice_int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlus_dice_int(RandomFormatterParser.Plus_dice_intContext ctx);
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(RandomFormatterParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link RandomFormatterParser#random}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRandom(RandomFormatterParser.RandomContext ctx);
}