// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/UIEntryBase.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientrybase;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryBaseParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface UIEntryBaseVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link UIEntryBaseParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(UIEntryBaseParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryBaseParser#renderer}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRenderer(UIEntryBaseParser.RendererContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryBaseParser#combine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCombine(UIEntryBaseParser.CombineContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryBaseParser#category}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCategory(UIEntryBaseParser.CategoryContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryBaseParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(UIEntryBaseParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryBaseParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(UIEntryBaseParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryBaseParser#base}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBase(UIEntryBaseParser.BaseContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryBaseParser#bases}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBases(UIEntryBaseParser.BasesContext ctx);
}