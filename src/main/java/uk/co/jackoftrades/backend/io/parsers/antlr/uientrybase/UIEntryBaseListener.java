// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/UIEntryBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientrybase;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryBaseParser}.
 */
public interface UIEntryBaseListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(UIEntryBaseParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(UIEntryBaseParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#renderer}.
     *
     * @param ctx the parse tree
     */
    void enterRenderer(UIEntryBaseParser.RendererContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#renderer}.
     *
     * @param ctx the parse tree
     */
    void exitRenderer(UIEntryBaseParser.RendererContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#combine}.
     *
     * @param ctx the parse tree
     */
    void enterCombine(UIEntryBaseParser.CombineContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#combine}.
     *
     * @param ctx the parse tree
     */
    void exitCombine(UIEntryBaseParser.CombineContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#category}.
     *
     * @param ctx the parse tree
     */
    void enterCategory(UIEntryBaseParser.CategoryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#category}.
     *
     * @param ctx the parse tree
     */
    void exitCategory(UIEntryBaseParser.CategoryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(UIEntryBaseParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(UIEntryBaseParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(UIEntryBaseParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(UIEntryBaseParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#base}.
     *
     * @param ctx the parse tree
     */
    void enterBase(UIEntryBaseParser.BaseContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#base}.
     *
     * @param ctx the parse tree
     */
    void exitBase(UIEntryBaseParser.BaseContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryBaseParser#bases}.
     *
     * @param ctx the parse tree
     */
    void enterBases(UIEntryBaseParser.BasesContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryBaseParser#bases}.
     *
     * @param ctx the parse tree
     */
    void exitBases(UIEntryBaseParser.BasesContext ctx);
}