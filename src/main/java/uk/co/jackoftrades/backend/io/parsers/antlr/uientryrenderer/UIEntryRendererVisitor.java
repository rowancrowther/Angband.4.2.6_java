// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/UIEntryRendererEnum.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.uientryrenderer;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UIEntryRendererParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface UIEntryRendererVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(UIEntryRendererParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(UIEntryRendererParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#colours}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitColours(UIEntryRendererParser.ColoursContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#labelColours}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLabelColours(UIEntryRendererParser.LabelColoursContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#symbols}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSymbols(UIEntryRendererParser.SymbolsContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#nDigit}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNDigit(UIEntryRendererParser.NDigitContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#sign}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSign(UIEntryRendererParser.SignContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#entry}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEntry(UIEntryRendererParser.EntryContext ctx);

    /**
     * Visit a parse tree produced by {@link UIEntryRendererParser#entries}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEntries(UIEntryRendererParser.EntriesContext ctx);
}