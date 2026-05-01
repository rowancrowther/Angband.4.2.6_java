// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ObjectBaseFormatter.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.objectbaseformatter;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ObjectBaseFormatterParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface ObjectBaseFormatterVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#default}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDefault(ObjectBaseFormatterParser.DefaultContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(ObjectBaseFormatterParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#graphics}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGraphics(ObjectBaseFormatterParser.GraphicsContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#break}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBreak(ObjectBaseFormatterParser.BreakContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#maxstack}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMaxstack(ObjectBaseFormatterParser.MaxstackContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#flag}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlag(ObjectBaseFormatterParser.FlagContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(ObjectBaseFormatterParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#base}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBase(ObjectBaseFormatterParser.BaseContext ctx);

    /**
     * Visit a parse tree produced by {@link ObjectBaseFormatterParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(ObjectBaseFormatterParser.FileContext ctx);
}