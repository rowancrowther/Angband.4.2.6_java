/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

// Generated from src/main/java/uk/co/jackoftradesltd/backend/parser/grammars/RoomProfileGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.roomprofiler;

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