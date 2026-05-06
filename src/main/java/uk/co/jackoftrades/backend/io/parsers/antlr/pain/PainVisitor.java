// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/Pain.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.pain;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PainParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface PainVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PainParser#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(PainParser.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link PainParser#message}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMessage(PainParser.MessageContext ctx);

    /**
     * Visit a parse tree produced by {@link PainParser#entry}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEntry(PainParser.EntryContext ctx);

    /**
     * Visit a parse tree produced by {@link PainParser#painMessages}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPainMessages(PainParser.PainMessagesContext ctx);
}