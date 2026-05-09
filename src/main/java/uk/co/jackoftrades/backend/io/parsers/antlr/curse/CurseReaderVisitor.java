// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/CurseReader.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.curse;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CurseReaderParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CurseReaderVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#name}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(CurseReaderParser.NameContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#type}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(CurseReaderParser.TypeContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#combat}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCombat(CurseReaderParser.CombatContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#effect}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEffect(CurseReaderParser.EffectContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#dice}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDice(CurseReaderParser.DiceContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#expr}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(CurseReaderParser.ExprContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#time}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTime(CurseReaderParser.TimeContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#flags}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(CurseReaderParser.FlagsContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#values}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValues(CurseReaderParser.ValuesContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#msg}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsg(CurseReaderParser.MsgContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#desc}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(CurseReaderParser.DescContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#conflict}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConflict(CurseReaderParser.ConflictContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#conflict_flags}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConflict_flags(CurseReaderParser.Conflict_flagsContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#curse}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCurse(CurseReaderParser.CurseContext ctx);

	/**
	 * Visit a parse tree produced by {@link CurseReaderParser#curses}.
	 *
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCurses(CurseReaderParser.CursesContext ctx);
}