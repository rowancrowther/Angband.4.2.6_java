// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/UIEntryGram.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientry;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link UIEntryGramParser}.
 */
public interface UIEntryGramListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(UIEntryGramParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(UIEntryGramParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#parameter}.
     *
     * @param ctx the parse tree
     */
    void enterParameter(UIEntryGramParser.ParameterContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#parameter}.
     *
     * @param ctx the parse tree
     */
    void exitParameter(UIEntryGramParser.ParameterContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#renderer}.
     *
     * @param ctx the parse tree
     */
    void enterRenderer(UIEntryGramParser.RendererContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#renderer}.
     *
     * @param ctx the parse tree
     */
    void exitRenderer(UIEntryGramParser.RendererContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#combine}.
     *
     * @param ctx the parse tree
     */
    void enterCombine(UIEntryGramParser.CombineContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#combine}.
     *
     * @param ctx the parse tree
     */
    void exitCombine(UIEntryGramParser.CombineContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#priority}.
     *
     * @param ctx the parse tree
     */
    void enterPriority(UIEntryGramParser.PriorityContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#priority}.
     *
     * @param ctx the parse tree
     */
    void exitPriority(UIEntryGramParser.PriorityContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#category}.
     *
     * @param ctx the parse tree
     */
    void enterCategory(UIEntryGramParser.CategoryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#category}.
     *
     * @param ctx the parse tree
     */
    void exitCategory(UIEntryGramParser.CategoryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(UIEntryGramParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(UIEntryGramParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(UIEntryGramParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(UIEntryGramParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#label}.
     *
     * @param ctx the parse tree
     */
    void enterLabel(UIEntryGramParser.LabelContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#label}.
     *
     * @param ctx the parse tree
     */
    void exitLabel(UIEntryGramParser.LabelContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#label2}.
     *
     * @param ctx the parse tree
     */
    void enterLabel2(UIEntryGramParser.Label2Context ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#label2}.
     *
     * @param ctx the parse tree
     */
    void exitLabel2(UIEntryGramParser.Label2Context ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#label5}.
     *
     * @param ctx the parse tree
     */
    void enterLabel5(UIEntryGramParser.Label5Context ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#label5}.
     *
     * @param ctx the parse tree
     */
    void exitLabel5(UIEntryGramParser.Label5Context ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#template}.
     *
     * @param ctx the parse tree
     */
    void enterTemplate(UIEntryGramParser.TemplateContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#template}.
     *
     * @param ctx the parse tree
     */
    void exitTemplate(UIEntryGramParser.TemplateContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#entry}.
     *
     * @param ctx the parse tree
     */
    void enterEntry(UIEntryGramParser.EntryContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#entry}.
     *
     * @param ctx the parse tree
     */
    void exitEntry(UIEntryGramParser.EntryContext ctx);

    /**
     * Enter a parse tree produced by {@link UIEntryGramParser#entries}.
     *
     * @param ctx the parse tree
     */
    void enterEntries(UIEntryGramParser.EntriesContext ctx);

    /**
     * Exit a parse tree produced by {@link UIEntryGramParser#entries}.
     *
     * @param ctx the parse tree
     */
    void exitEntries(UIEntryGramParser.EntriesContext ctx);
}