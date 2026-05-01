// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/UIEntryRendererEnum.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientryrenderer;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryRendererParser}.
 */
public interface UIEntryRendererListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(UIEntryRendererParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(UIEntryRendererParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(UIEntryRendererParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(UIEntryRendererParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#colours}.
     *
     * @param ctx the parse tree
     */
    void enterColours(UIEntryRendererParser.ColoursContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#colours}.
     *
     * @param ctx the parse tree
     */
    void exitColours(UIEntryRendererParser.ColoursContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#labelColours}.
     *
     * @param ctx the parse tree
     */
    void enterLabelColours(UIEntryRendererParser.LabelColoursContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#labelColours}.
     *
     * @param ctx the parse tree
     */
    void exitLabelColours(UIEntryRendererParser.LabelColoursContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#symbols}.
     *
     * @param ctx the parse tree
     */
    void enterSymbols(UIEntryRendererParser.SymbolsContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#symbols}.
     *
     * @param ctx the parse tree
     */
    void exitSymbols(UIEntryRendererParser.SymbolsContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#nDigit}.
     *
     * @param ctx the parse tree
     */
    void enterNDigit(UIEntryRendererParser.NDigitContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#nDigit}.
     *
     * @param ctx the parse tree
     */
    void exitNDigit(UIEntryRendererParser.NDigitContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#sign}.
     *
     * @param ctx the parse tree
     */
    void enterSign(UIEntryRendererParser.SignContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#sign}.
     *
     * @param ctx the parse tree
     */
    void exitSign(UIEntryRendererParser.SignContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#entry}.
     *
     * @param ctx the parse tree
     */
    void enterEntry(UIEntryRendererParser.EntryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#entry}.
     *
     * @param ctx the parse tree
     */
    void exitEntry(UIEntryRendererParser.EntryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryRendererParser#entries}.
     *
     * @param ctx the parse tree
     */
    void enterEntries(UIEntryRendererParser.EntriesContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryRendererParser#entries}.
     *
     * @param ctx the parse tree
     */
    void exitEntries(UIEntryRendererParser.EntriesContext ctx);
}