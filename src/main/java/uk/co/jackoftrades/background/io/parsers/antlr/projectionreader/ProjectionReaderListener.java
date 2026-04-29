// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/ProjectionReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.projectionreader;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ProjectionReaderParser}.
 */
public interface ProjectionReaderListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#code}.
     *
     * @param ctx the parse tree
     */
    void enterCode(ProjectionReaderParser.CodeContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#code}.
     *
     * @param ctx the parse tree
     */
    void exitCode(ProjectionReaderParser.CodeContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(ProjectionReaderParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(ProjectionReaderParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#type}.
     *
     * @param ctx the parse tree
     */
    void enterType(ProjectionReaderParser.TypeContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#type}.
     *
     * @param ctx the parse tree
     */
    void exitType(ProjectionReaderParser.TypeContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(ProjectionReaderParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(ProjectionReaderParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#playerDesc}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerDesc(ProjectionReaderParser.PlayerDescContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#playerDesc}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerDesc(ProjectionReaderParser.PlayerDescContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#blindDesc}.
     *
     * @param ctx the parse tree
     */
    void enterBlindDesc(ProjectionReaderParser.BlindDescContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#blindDesc}.
     *
     * @param ctx the parse tree
     */
    void exitBlindDesc(ProjectionReaderParser.BlindDescContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#lashDesc}.
     *
     * @param ctx the parse tree
     */
    void enterLashDesc(ProjectionReaderParser.LashDescContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#lashDesc}.
     *
     * @param ctx the parse tree
     */
    void exitLashDesc(ProjectionReaderParser.LashDescContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#numerator}.
     *
     * @param ctx the parse tree
     */
    void enterNumerator(ProjectionReaderParser.NumeratorContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#numerator}.
     *
     * @param ctx the parse tree
     */
    void exitNumerator(ProjectionReaderParser.NumeratorContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#dice}.
     *
     * @param ctx the parse tree
     */
    void enterDice(ProjectionReaderParser.DiceContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#dice}.
     *
     * @param ctx the parse tree
     */
    void exitDice(ProjectionReaderParser.DiceContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#denominator}.
     *
     * @param ctx the parse tree
     */
    void enterDenominator(ProjectionReaderParser.DenominatorContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#denominator}.
     *
     * @param ctx the parse tree
     */
    void exitDenominator(ProjectionReaderParser.DenominatorContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#divisor}.
     *
     * @param ctx the parse tree
     */
    void enterDivisor(ProjectionReaderParser.DivisorContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#divisor}.
     *
     * @param ctx the parse tree
     */
    void exitDivisor(ProjectionReaderParser.DivisorContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#damageCap}.
     *
     * @param ctx the parse tree
     */
    void enterDamageCap(ProjectionReaderParser.DamageCapContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#damageCap}.
     *
     * @param ctx the parse tree
     */
    void exitDamageCap(ProjectionReaderParser.DamageCapContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#msgt}.
     *
     * @param ctx the parse tree
     */
    void enterMsgt(ProjectionReaderParser.MsgtContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#msgt}.
     *
     * @param ctx the parse tree
     */
    void exitMsgt(ProjectionReaderParser.MsgtContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#obvious}.
     *
     * @param ctx the parse tree
     */
    void enterObvious(ProjectionReaderParser.ObviousContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#obvious}.
     *
     * @param ctx the parse tree
     */
    void exitObvious(ProjectionReaderParser.ObviousContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#wake}.
     *
     * @param ctx the parse tree
     */
    void enterWake(ProjectionReaderParser.WakeContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#wake}.
     *
     * @param ctx the parse tree
     */
    void exitWake(ProjectionReaderParser.WakeContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#colour}.
     *
     * @param ctx the parse tree
     */
    void enterColour(ProjectionReaderParser.ColourContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#colour}.
     *
     * @param ctx the parse tree
     */
    void exitColour(ProjectionReaderParser.ColourContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#projection}.
     *
     * @param ctx the parse tree
     */
    void enterProjection(ProjectionReaderParser.ProjectionContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#projection}.
     *
     * @param ctx the parse tree
     */
    void exitProjection(ProjectionReaderParser.ProjectionContext ctx);

    /**
     * Enter a parse tree produced by {@link ProjectionReaderParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(ProjectionReaderParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link ProjectionReaderParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(ProjectionReaderParser.FileContext ctx);
}