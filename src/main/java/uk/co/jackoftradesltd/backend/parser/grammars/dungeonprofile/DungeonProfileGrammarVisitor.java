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

// Generated from DungeonProfileGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftradesltd.backend.parser.grammars.dungeonprofile;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DungeonProfileGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DungeonProfileGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(DungeonProfileGrammar.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#params}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParams(DungeonProfileGrammar.ParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#tunnel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTunnel(DungeonProfileGrammar.TunnelContext ctx);
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#streamer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStreamer(DungeonProfileGrammar.StreamerContext ctx);
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#alloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlloc(DungeonProfileGrammar.AllocContext ctx);
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#minLevel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMinLevel(DungeonProfileGrammar.MinLevelContext ctx);
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#room}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoom(DungeonProfileGrammar.RoomContext ctx);
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#profile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProfile(DungeonProfileGrammar.ProfileContext ctx);
	/**
	 * Visit a parse tree produced by {@link DungeonProfileGrammar#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(DungeonProfileGrammar.FileContext ctx);
}