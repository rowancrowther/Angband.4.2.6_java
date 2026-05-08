// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/CurseReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.curse;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CurseReaderParser}.
 */
public interface CurseReaderListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link CurseReaderParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(CurseReaderParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(CurseReaderParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(CurseReaderParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(CurseReaderParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#combat}.
     *
     * @param ctx the parse tree
     */
    void enterCombat(CurseReaderParser.CombatContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#combat}.
     *
     * @param ctx the parse tree
     */
    void exitCombat(CurseReaderParser.CombatContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#effect}.
     *
     * @param ctx the parse tree
     */
    void enterEffect(CurseReaderParser.EffectContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#effect}.
     *
     * @param ctx the parse tree
     */
    void exitEffect(CurseReaderParser.EffectContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(CurseReaderParser.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(CurseReaderParser.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#expr}.
     *
     * @param ctx the parse tree
     */
    void enterExpr(CurseReaderParser.ExprContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#expr}.
     *
     * @param ctx the parse tree
     */
    void exitExpr(CurseReaderParser.ExprContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#time}.
     *
     * @param ctx the parse tree
     */
    void enterTime(CurseReaderParser.TimeContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#time}.
     *
     * @param ctx the parse tree
     */
    void exitTime(CurseReaderParser.TimeContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(CurseReaderParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(CurseReaderParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(CurseReaderParser.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(CurseReaderParser.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#msg}.
     *
     * @param ctx the parse tree
     */
    void enterMsg(CurseReaderParser.MsgContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#msg}.
     *
     * @param ctx the parse tree
     */
    void exitMsg(CurseReaderParser.MsgContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(CurseReaderParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(CurseReaderParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#conflict}.
     *
     * @param ctx the parse tree
     */
    void enterConflict(CurseReaderParser.ConflictContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#conflict}.
     *
     * @param ctx the parse tree
     */
    void exitConflict(CurseReaderParser.ConflictContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#conflict_flags}.
     *
     * @param ctx the parse tree
     */
    void enterConflict_flags(CurseReaderParser.Conflict_flagsContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#conflict_flags}.
     *
     * @param ctx the parse tree
     */
    void exitConflict_flags(CurseReaderParser.Conflict_flagsContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#curse}.
     *
     * @param ctx the parse tree
     */
    void enterCurse(CurseReaderParser.CurseContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#curse}.
     *
     * @param ctx the parse tree
     */
    void exitCurse(CurseReaderParser.CurseContext ctx);

    /**
     * Enter a parse tree produced by {@link CurseReaderParser#curses}.
     *
     * @param ctx the parse tree
     */
    void enterCurses(CurseReaderParser.CursesContext ctx);

    /**
     * Exit a parse tree produced by {@link CurseReaderParser#curses}.
     *
     * @param ctx the parse tree
     */
    void exitCurses(CurseReaderParser.CursesContext ctx);
}