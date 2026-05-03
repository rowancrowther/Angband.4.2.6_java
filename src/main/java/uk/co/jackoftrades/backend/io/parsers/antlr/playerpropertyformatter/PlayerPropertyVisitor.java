// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/PlayerProperty.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.playerpropertyformatter;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerPropertyParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface PlayerPropertyVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PlayerPropertyParser#type}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitType(PlayerPropertyParser.TypeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyParser#code}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCode(PlayerPropertyParser.CodeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyParser#bindui}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBindui(PlayerPropertyParser.BinduiContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(PlayerPropertyParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(PlayerPropertyParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyParser#value}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValue(PlayerPropertyParser.ValueContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyParser#playerProperty}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerProperty(PlayerPropertyParser.PlayerPropertyContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerPropertyParser#playerProperties}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerProperties(PlayerPropertyParser.PlayerPropertiesContext ctx);
}