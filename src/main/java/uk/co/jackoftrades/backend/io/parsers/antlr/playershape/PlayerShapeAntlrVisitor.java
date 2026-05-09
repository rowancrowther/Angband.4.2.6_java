// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/PlayerShapeAntlr.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.playershape;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerShapeAntlrParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface PlayerShapeAntlrVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(PlayerShapeAntlrParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#combat}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCombat(PlayerShapeAntlrParser.CombatContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#skillDisarmPhys}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDisarmPhys(PlayerShapeAntlrParser.SkillDisarmPhysContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#skillDisarmMagic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDisarmMagic(PlayerShapeAntlrParser.SkillDisarmMagicContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#skillSave}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillSave(PlayerShapeAntlrParser.SkillSaveContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#skillStealth}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillStealth(PlayerShapeAntlrParser.SkillStealthContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#skillSearch}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillSearch(PlayerShapeAntlrParser.SkillSearchContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#skillMelee}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillMelee(PlayerShapeAntlrParser.SkillMeleeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#skillThrow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillThrow(PlayerShapeAntlrParser.SkillThrowContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#skillDig}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDig(PlayerShapeAntlrParser.SkillDigContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#objFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObjFlags(PlayerShapeAntlrParser.ObjFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#playerFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerFlags(PlayerShapeAntlrParser.PlayerFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(PlayerShapeAntlrParser.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#effect}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffect(PlayerShapeAntlrParser.EffectContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#dice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDice(PlayerShapeAntlrParser.DiceContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#expr}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpr(PlayerShapeAntlrParser.ExprContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#effectMsg}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEffectMsg(PlayerShapeAntlrParser.EffectMsgContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#blow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlow(PlayerShapeAntlrParser.BlowContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#shape}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitShape(PlayerShapeAntlrParser.ShapeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerShapeAntlrParser#shapes}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitShapes(PlayerShapeAntlrParser.ShapesContext ctx);
}