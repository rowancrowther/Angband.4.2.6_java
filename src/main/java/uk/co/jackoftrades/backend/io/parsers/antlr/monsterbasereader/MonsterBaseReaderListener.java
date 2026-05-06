// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/MonsterBaseReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.monsterbasereader;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MonsterBaseReaderParser}.
 */
public interface MonsterBaseReaderListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link MonsterBaseReaderParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(MonsterBaseReaderParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseReaderParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(MonsterBaseReaderParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseReaderParser#glyph}.
     *
     * @param ctx the parse tree
     */
    void enterGlyph(MonsterBaseReaderParser.GlyphContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseReaderParser#glyph}.
     *
     * @param ctx the parse tree
     */
    void exitGlyph(MonsterBaseReaderParser.GlyphContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseReaderParser#pain}.
     *
     * @param ctx the parse tree
     */
    void enterPain(MonsterBaseReaderParser.PainContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseReaderParser#pain}.
     *
     * @param ctx the parse tree
     */
    void exitPain(MonsterBaseReaderParser.PainContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseReaderParser#flags}.
     *
     * @param ctx the parse tree
     */
    void enterFlags(MonsterBaseReaderParser.FlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseReaderParser#flags}.
     *
     * @param ctx the parse tree
     */
    void exitFlags(MonsterBaseReaderParser.FlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseReaderParser#desc}.
     *
     * @param ctx the parse tree
     */
    void enterDesc(MonsterBaseReaderParser.DescContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseReaderParser#desc}.
     *
     * @param ctx the parse tree
     */
    void exitDesc(MonsterBaseReaderParser.DescContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseReaderParser#entry}.
     *
     * @param ctx the parse tree
     */
    void enterEntry(MonsterBaseReaderParser.EntryContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseReaderParser#entry}.
     *
     * @param ctx the parse tree
     */
    void exitEntry(MonsterBaseReaderParser.EntryContext ctx);

    /**
     * Enter a parse tree produced by {@link MonsterBaseReaderParser#monsterBases}.
     *
     * @param ctx the parse tree
     */
    void enterMonsterBases(MonsterBaseReaderParser.MonsterBasesContext ctx);

    /**
     * Exit a parse tree produced by {@link MonsterBaseReaderParser#monsterBases}.
     *
     * @param ctx the parse tree
     */
    void exitMonsterBases(MonsterBaseReaderParser.MonsterBasesContext ctx);
}