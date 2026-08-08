// Generated from src/main/java/uk/co/jackoftrades/backend/parser/grammars/VaultGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.vault;

    import uk.co.jackoftrades.backend.parser.vault.VaultParseRecord;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link VaultGrammar}.
 */
public interface VaultGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(VaultGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(VaultGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(VaultGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(VaultGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#type}.
	 * @param ctx the parse tree
	 */
	void enterType(VaultGrammar.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#type}.
	 * @param ctx the parse tree
	 */
	void exitType(VaultGrammar.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#rating}.
	 * @param ctx the parse tree
	 */
	void enterRating(VaultGrammar.RatingContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#rating}.
	 * @param ctx the parse tree
	 */
	void exitRating(VaultGrammar.RatingContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#rows}.
	 * @param ctx the parse tree
	 */
	void enterRows(VaultGrammar.RowsContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#rows}.
	 * @param ctx the parse tree
	 */
	void exitRows(VaultGrammar.RowsContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#columns}.
	 * @param ctx the parse tree
	 */
	void enterColumns(VaultGrammar.ColumnsContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#columns}.
	 * @param ctx the parse tree
	 */
	void exitColumns(VaultGrammar.ColumnsContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#minDepth}.
	 * @param ctx the parse tree
	 */
	void enterMinDepth(VaultGrammar.MinDepthContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#minDepth}.
	 * @param ctx the parse tree
	 */
	void exitMinDepth(VaultGrammar.MinDepthContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#maxDepth}.
	 * @param ctx the parse tree
	 */
	void enterMaxDepth(VaultGrammar.MaxDepthContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#maxDepth}.
	 * @param ctx the parse tree
	 */
	void exitMaxDepth(VaultGrammar.MaxDepthContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#flag}.
	 * @param ctx the parse tree
	 */
	void enterFlag(VaultGrammar.FlagContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#flag}.
	 * @param ctx the parse tree
	 */
	void exitFlag(VaultGrammar.FlagContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#d}.
	 * @param ctx the parse tree
	 */
	void enterD(VaultGrammar.DContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#d}.
	 * @param ctx the parse tree
	 */
	void exitD(VaultGrammar.DContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#profileRecord}.
	 * @param ctx the parse tree
	 */
	void enterProfileRecord(VaultGrammar.ProfileRecordContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#profileRecord}.
	 * @param ctx the parse tree
	 */
	void exitProfileRecord(VaultGrammar.ProfileRecordContext ctx);
	/**
	 * Enter a parse tree produced by {@link VaultGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(VaultGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link VaultGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(VaultGrammar.FileContext ctx);
}