// Generated from src/main/java/uk/co/jackoftrades/backend/parser/grammars/RoomProfileGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.roomprofiler;

    import uk.co.jackoftrades.backend.parser.roomprofile.RoomProfileParseRecord;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link RoomProfileGrammar}.
 */
public interface RoomProfileGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(RoomProfileGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(RoomProfileGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(RoomProfileGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(RoomProfileGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#type}.
	 * @param ctx the parse tree
	 */
	void enterType(RoomProfileGrammar.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#type}.
	 * @param ctx the parse tree
	 */
	void exitType(RoomProfileGrammar.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#rating}.
	 * @param ctx the parse tree
	 */
	void enterRating(RoomProfileGrammar.RatingContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#rating}.
	 * @param ctx the parse tree
	 */
	void exitRating(RoomProfileGrammar.RatingContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#rows}.
	 * @param ctx the parse tree
	 */
	void enterRows(RoomProfileGrammar.RowsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#rows}.
	 * @param ctx the parse tree
	 */
	void exitRows(RoomProfileGrammar.RowsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#columns}.
	 * @param ctx the parse tree
	 */
	void enterColumns(RoomProfileGrammar.ColumnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#columns}.
	 * @param ctx the parse tree
	 */
	void exitColumns(RoomProfileGrammar.ColumnsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#doors}.
	 * @param ctx the parse tree
	 */
	void enterDoors(RoomProfileGrammar.DoorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#doors}.
	 * @param ctx the parse tree
	 */
	void exitDoors(RoomProfileGrammar.DoorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#tval}.
	 * @param ctx the parse tree
	 */
	void enterTval(RoomProfileGrammar.TvalContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#tval}.
	 * @param ctx the parse tree
	 */
	void exitTval(RoomProfileGrammar.TvalContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void enterFlags(RoomProfileGrammar.FlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void exitFlags(RoomProfileGrammar.FlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#dLine}.
	 * @param ctx the parse tree
	 */
	void enterDLine(RoomProfileGrammar.DLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#dLine}.
	 * @param ctx the parse tree
	 */
	void exitDLine(RoomProfileGrammar.DLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#roomMap}.
	 * @param ctx the parse tree
	 */
	void enterRoomMap(RoomProfileGrammar.RoomMapContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#roomMap}.
	 * @param ctx the parse tree
	 */
	void exitRoomMap(RoomProfileGrammar.RoomMapContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#roomProfile}.
	 * @param ctx the parse tree
	 */
	void enterRoomProfile(RoomProfileGrammar.RoomProfileContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#roomProfile}.
	 * @param ctx the parse tree
	 */
	void exitRoomProfile(RoomProfileGrammar.RoomProfileContext ctx);
	/**
	 * Enter a parse tree produced by {@link RoomProfileGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(RoomProfileGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link RoomProfileGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(RoomProfileGrammar.FileContext ctx);
}