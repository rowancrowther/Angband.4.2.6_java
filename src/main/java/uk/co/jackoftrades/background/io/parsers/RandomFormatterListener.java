// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/io/parsers/grammars/RandomFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RandomFormatterParser}.
 */
public interface RandomFormatterListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#offset}.
	 * @param ctx the parse tree
	 */
	void enterOffset(RandomFormatterParser.OffsetContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#offset}.
	 * @param ctx the parse tree
	 */
	void exitOffset(RandomFormatterParser.OffsetContext ctx);
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#mult_int}.
	 * @param ctx the parse tree
	 */
	void enterMult_int(RandomFormatterParser.Mult_intContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#mult_int}.
	 * @param ctx the parse tree
	 */
	void exitMult_int(RandomFormatterParser.Mult_intContext ctx);
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#mult}.
	 * @param ctx the parse tree
	 */
	void enterMult(RandomFormatterParser.MultContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#mult}.
	 * @param ctx the parse tree
	 */
	void exitMult(RandomFormatterParser.MultContext ctx);
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#dice_int}.
	 * @param ctx the parse tree
	 */
	void enterDice_int(RandomFormatterParser.Dice_intContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#dice_int}.
	 * @param ctx the parse tree
	 */
	void exitDice_int(RandomFormatterParser.Dice_intContext ctx);
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#int_dice_int}.
	 * @param ctx the parse tree
	 */
	void enterInt_dice_int(RandomFormatterParser.Int_dice_intContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#int_dice_int}.
	 * @param ctx the parse tree
	 */
	void exitInt_dice_int(RandomFormatterParser.Int_dice_intContext ctx);
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#plus_int_dice_int}.
	 * @param ctx the parse tree
	 */
	void enterPlus_int_dice_int(RandomFormatterParser.Plus_int_dice_intContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#plus_int_dice_int}.
	 * @param ctx the parse tree
	 */
	void exitPlus_int_dice_int(RandomFormatterParser.Plus_int_dice_intContext ctx);
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#plus_dice_int}.
	 * @param ctx the parse tree
	 */
	void enterPlus_dice_int(RandomFormatterParser.Plus_dice_intContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#plus_dice_int}.
	 * @param ctx the parse tree
	 */
	void exitPlus_dice_int(RandomFormatterParser.Plus_dice_intContext ctx);
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(RandomFormatterParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(RandomFormatterParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link RandomFormatterParser#random}.
	 * @param ctx the parse tree
	 */
	void enterRandom(RandomFormatterParser.RandomContext ctx);
	/**
	 * Exit a parse tree produced by {@link RandomFormatterParser#random}.
	 * @param ctx the parse tree
	 */
	void exitRandom(RandomFormatterParser.RandomContext ctx);
}