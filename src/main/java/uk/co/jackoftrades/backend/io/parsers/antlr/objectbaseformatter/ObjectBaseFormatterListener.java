// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ObjectBaseFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.objectbaseformatter;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ObjectBaseFormatterParser}.
 */
public interface ObjectBaseFormatterListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#default}.
     *
     * @param ctx the parse tree
     */
    void enterDefault(ObjectBaseFormatterParser.DefaultContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#default}.
     *
     * @param ctx the parse tree
     */
    void exitDefault(ObjectBaseFormatterParser.DefaultContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ObjectBaseFormatterParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ObjectBaseFormatterParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#graphics}.
     *
     * @param ctx the parse tree
     */
    void enterGraphics(ObjectBaseFormatterParser.GraphicsContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#graphics}.
     *
     * @param ctx the parse tree
     */
    void exitGraphics(ObjectBaseFormatterParser.GraphicsContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#break}.
     *
     * @param ctx the parse tree
     */
    void enterBreak(ObjectBaseFormatterParser.BreakContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#break}.
     *
     * @param ctx the parse tree
     */
    void exitBreak(ObjectBaseFormatterParser.BreakContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#maxstack}.
     *
     * @param ctx the parse tree
     */
    void enterMaxstack(ObjectBaseFormatterParser.MaxstackContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#maxstack}.
     *
     * @param ctx the parse tree
     */
    void exitMaxstack(ObjectBaseFormatterParser.MaxstackContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#flag}.
     *
     * @param ctx the parse tree
     */
    void enterFlag(ObjectBaseFormatterParser.FlagContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#flag}.
     *
     * @param ctx the parse tree
     */
    void exitFlag(ObjectBaseFormatterParser.FlagContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(ObjectBaseFormatterParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(ObjectBaseFormatterParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#base}.
     *
     * @param ctx the parse tree
     */
    void enterBase(ObjectBaseFormatterParser.BaseContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#base}.
     *
     * @param ctx the parse tree
     */
    void exitBase(ObjectBaseFormatterParser.BaseContext ctx);

    /**
     * Enter a parse tree produced by {@link ObjectBaseFormatterParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ObjectBaseFormatterParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ObjectBaseFormatterParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ObjectBaseFormatterParser.FileContext ctx);
}