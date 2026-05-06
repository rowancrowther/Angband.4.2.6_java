// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/MonsterBaseReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.monsterbasereader;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MonsterBaseReaderParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface MonsterBaseReaderVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link MonsterBaseReaderParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(MonsterBaseReaderParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseReaderParser#glyph}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGlyph(MonsterBaseReaderParser.GlyphContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseReaderParser#pain}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPain(MonsterBaseReaderParser.PainContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseReaderParser#flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFlags(MonsterBaseReaderParser.FlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseReaderParser#desc}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDesc(MonsterBaseReaderParser.DescContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseReaderParser#entry}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEntry(MonsterBaseReaderParser.EntryContext ctx);

    /**
     * Visit a parse tree produced by {@link MonsterBaseReaderParser#monsterBases}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMonsterBases(MonsterBaseReaderParser.MonsterBasesContext ctx);
}