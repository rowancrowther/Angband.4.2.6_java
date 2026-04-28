// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/WorldReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.parsers.world;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link WorldReaderParser}.
 */
public interface WorldReaderListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link WorldReaderParser#levelNum}.
     *
     * @param ctx the parse tree
     */
    void enterLevelNum(WorldReaderParser.LevelNumContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldReaderParser#levelNum}.
     *
     * @param ctx the parse tree
     */
    void exitLevelNum(WorldReaderParser.LevelNumContext ctx);

    /**
     * Enter a parse tree produced by {@link WorldReaderParser#levelName}.
     *
     * @param ctx the parse tree
     */
    void enterLevelName(WorldReaderParser.LevelNameContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldReaderParser#levelName}.
     *
     * @param ctx the parse tree
     */
    void exitLevelName(WorldReaderParser.LevelNameContext ctx);

    /**
     * Enter a parse tree produced by {@link WorldReaderParser#upAndDown}.
     *
     * @param ctx the parse tree
     */
    void enterUpAndDown(WorldReaderParser.UpAndDownContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldReaderParser#upAndDown}.
     *
     * @param ctx the parse tree
     */
    void exitUpAndDown(WorldReaderParser.UpAndDownContext ctx);

    /**
     * Enter a parse tree produced by {@link WorldReaderParser#line}.
     *
     * @param ctx the parse tree
     */
    void enterLine(WorldReaderParser.LineContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldReaderParser#line}.
     *
     * @param ctx the parse tree
     */
    void exitLine(WorldReaderParser.LineContext ctx);

    /**
     * Enter a parse tree produced by {@link WorldReaderParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(WorldReaderParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link WorldReaderParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(WorldReaderParser.FileContext ctx);
}