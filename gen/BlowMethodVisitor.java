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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/BlowMethod.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.middle.combat.BlowMethod;
    import uk.co.jackoftrades.middle.enums.MessageType;

    import java.util.List;
    import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BlowMethodParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BlowMethodVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(BlowMethodParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#cut}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCut(BlowMethodParser.CutContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#stun}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStun(BlowMethodParser.StunContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#miss}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMiss(BlowMethodParser.MissContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#phys}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPhys(BlowMethodParser.PhysContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsg(BlowMethodParser.MsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#act}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAct(BlowMethodParser.ActContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(BlowMethodParser.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#blowMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlowMethod(BlowMethodParser.BlowMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link BlowMethodParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(BlowMethodParser.FileContext ctx);
}