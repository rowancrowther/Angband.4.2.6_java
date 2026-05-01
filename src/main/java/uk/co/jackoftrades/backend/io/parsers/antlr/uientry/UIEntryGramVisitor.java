// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/UIEntryGram.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientry;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryGramParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface UIEntryGramVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(UIEntryGramParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#parameter}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParameter(UIEntryGramParser.ParameterContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#renderer}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRenderer(UIEntryGramParser.RendererContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#combine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCombine(UIEntryGramParser.CombineContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#priority}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPriority(UIEntryGramParser.PriorityContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#category}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCategory(UIEntryGramParser.CategoryContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(UIEntryGramParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(UIEntryGramParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#label}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLabel(UIEntryGramParser.LabelContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#label2}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLabel2(UIEntryGramParser.Label2Context ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#label5}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLabel5(UIEntryGramParser.Label5Context ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#template}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTemplate(UIEntryGramParser.TemplateContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#entry}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEntry(UIEntryGramParser.EntryContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryGramParser#entries}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEntries(UIEntryGramParser.EntriesContext ctx);
}