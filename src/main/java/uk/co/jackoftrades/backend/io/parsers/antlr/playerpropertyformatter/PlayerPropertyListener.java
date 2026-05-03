// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/PlayerProperty.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.playerpropertyformatter;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerPropertyParser}.
 */
public interface PlayerPropertyListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PlayerPropertyParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(PlayerPropertyParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(PlayerPropertyParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(PlayerPropertyParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(PlayerPropertyParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyParser#bindui}.
     *
     * @param ctx the parse tree
     */
    void enterBindui(PlayerPropertyParser.BinduiContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyParser#bindui}.
     *
     * @param ctx the parse tree
     */
    void exitBindui(PlayerPropertyParser.BinduiContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(PlayerPropertyParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(PlayerPropertyParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(PlayerPropertyParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(PlayerPropertyParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyParser#value}.
     *
     * @param ctx the parse tree
     */
    void enterValue(PlayerPropertyParser.ValueContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyParser#value}.
     *
     * @param ctx the parse tree
     */
    void exitValue(PlayerPropertyParser.ValueContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyParser#playerProperty}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerProperty(PlayerPropertyParser.PlayerPropertyContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyParser#playerProperty}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerProperty(PlayerPropertyParser.PlayerPropertyContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerPropertyParser#playerProperties}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerProperties(PlayerPropertyParser.PlayerPropertiesContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerPropertyParser#playerProperties}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerProperties(PlayerPropertyParser.PlayerPropertiesContext ctx);
}