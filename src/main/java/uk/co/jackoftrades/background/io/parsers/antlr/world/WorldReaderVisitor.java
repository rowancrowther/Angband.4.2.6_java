// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/background/io/parsers/antlr/grammars/WorldReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.background.io.parsers.antlr.world;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WorldReaderParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface WorldReaderVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link WorldReaderParser#levelNum}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevelNum(WorldReaderParser.LevelNumContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldReaderParser#levelName}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLevelName(WorldReaderParser.LevelNameContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldReaderParser#upAndDown}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUpAndDown(WorldReaderParser.UpAndDownContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldReaderParser#line}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLine(WorldReaderParser.LineContext ctx);

    /**
     * Visit a parse tree produced by {@link WorldReaderParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(WorldReaderParser.FileContext ctx);
}