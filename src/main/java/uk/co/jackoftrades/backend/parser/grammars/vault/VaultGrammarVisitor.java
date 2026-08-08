// Generated from src/main/java/uk/co/jackoftrades/backend/parser/grammars/VaultGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.vault;

    import uk.co.jackoftrades.backend.parser.vault.VaultParseRecord;

    import java.util.ArrayList;
    import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link VaultGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface VaultGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#recordCount}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordCount(VaultGrammar.RecordCountContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(VaultGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(VaultGrammar.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#rating}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRating(VaultGrammar.RatingContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#rows}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRows(VaultGrammar.RowsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#columns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumns(VaultGrammar.ColumnsContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#minDepth}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinDepth(VaultGrammar.MinDepthContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#maxDepth}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMaxDepth(VaultGrammar.MaxDepthContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#flag}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFlag(VaultGrammar.FlagContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#d}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitD(VaultGrammar.DContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#profileRecord}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProfileRecord(VaultGrammar.ProfileRecordContext ctx);
	/**
	 * Visit a parse tree produced by {@link VaultGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(VaultGrammar.FileContext ctx);
}