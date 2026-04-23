// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/SummonFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.parsers.summon;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SummonFormatterParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface SummonFormatterVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link SummonFormatterParser#word}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWord(SummonFormatterParser.WordContext ctx);

    /**
     * Visit a parse tree produced by {@link SummonFormatterParser#tagText}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTagText(SummonFormatterParser.TagTextContext ctx);

    /**
     * Visit a parse tree produced by {@link SummonFormatterParser#one_or_zero}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOne_or_zero(SummonFormatterParser.One_or_zeroContext ctx);

    /**
     * Visit a parse tree produced by {@link SummonFormatterParser#summon}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSummon(SummonFormatterParser.SummonContext ctx);

    /**
     * Visit a parse tree produced by {@link SummonFormatterParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(SummonFormatterParser.FileContext ctx);
}