// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.summon;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SummonFormatterParser}.
 */
public interface SummonFormatterListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link SummonFormatterParser#word}.
     *
     * @param ctx the parse tree
     */
    void enterWord(SummonFormatterParser.WordContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonFormatterParser#word}.
     *
     * @param ctx the parse tree
     */
    void exitWord(SummonFormatterParser.WordContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonFormatterParser#tagText}.
     *
     * @param ctx the parse tree
     */
    void enterTagText(SummonFormatterParser.TagTextContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonFormatterParser#tagText}.
     *
     * @param ctx the parse tree
     */
    void exitTagText(SummonFormatterParser.TagTextContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonFormatterParser#one_or_zero}.
     *
     * @param ctx the parse tree
     */
    void enterOne_or_zero(SummonFormatterParser.One_or_zeroContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonFormatterParser#one_or_zero}.
     *
     * @param ctx the parse tree
     */
    void exitOne_or_zero(SummonFormatterParser.One_or_zeroContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonFormatterParser#summon}.
     *
     * @param ctx the parse tree
     */
    void enterSummon(SummonFormatterParser.SummonContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonFormatterParser#summon}.
     *
     * @param ctx the parse tree
     */
    void exitSummon(SummonFormatterParser.SummonContext ctx);

    /**
     * Enter a parse tree produced by {@link SummonFormatterParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(SummonFormatterParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link SummonFormatterParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(SummonFormatterParser.FileContext ctx);
}