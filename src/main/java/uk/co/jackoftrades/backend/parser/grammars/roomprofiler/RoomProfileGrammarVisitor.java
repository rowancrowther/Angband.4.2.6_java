// Generated from src/main/java/uk/co/jackoftrades/backend/parser/grammars/RoomProfileGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.roomprofiler;

    import uk.co.jackoftrades.backend.parser.roomprofile.RoomProfileParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link RoomProfileGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface RoomProfileGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(RoomProfileGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(RoomProfileGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(RoomProfileGrammar.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#rating}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRating(RoomProfileGrammar.RatingContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#rows}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRows(RoomProfileGrammar.RowsContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#columns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumns(RoomProfileGrammar.ColumnsContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#doors}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoors(RoomProfileGrammar.DoorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#tval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTval(RoomProfileGrammar.TvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#flags}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlags(RoomProfileGrammar.FlagsContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#dLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDLine(RoomProfileGrammar.DLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#roomMap}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoomMap(RoomProfileGrammar.RoomMapContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#roomProfile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoomProfile(RoomProfileGrammar.RoomProfileContext ctx);
	/**
	 * Visit a parse tree produced by {@link RoomProfileGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(RoomProfileGrammar.FileContext ctx);
}