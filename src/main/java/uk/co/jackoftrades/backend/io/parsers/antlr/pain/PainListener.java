// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/Pain.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.pain;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PainParser}.
 */
public interface PainListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PainParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(PainParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link PainParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(PainParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link PainParser#message}.
     *
     * @param ctx the parse tree
     */
    void enterMessage(PainParser.MessageContext ctx);

    /**
     * Exit a parse tree produced by {@link PainParser#message}.
     *
     * @param ctx the parse tree
     */
    void exitMessage(PainParser.MessageContext ctx);

    /**
     * Enter a parse tree produced by {@link PainParser#entry}.
     *
     * @param ctx the parse tree
     */
    void enterEntry(PainParser.EntryContext ctx);

    /**
     * Exit a parse tree produced by {@link PainParser#entry}.
     *
     * @param ctx the parse tree
     */
    void exitEntry(PainParser.EntryContext ctx);

    /**
     * Enter a parse tree produced by {@link PainParser#painMessages}.
     *
     * @param ctx the parse tree
     */
    void enterPainMessages(PainParser.PainMessagesContext ctx);

    /**
     * Exit a parse tree produced by {@link PainParser#painMessages}.
     *
     * @param ctx the parse tree
     */
    void exitPainMessages(PainParser.PainMessagesContext ctx);
}