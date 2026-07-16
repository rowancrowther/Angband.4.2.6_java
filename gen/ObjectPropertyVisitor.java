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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/ObjectProperty.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.frontend.entries.UIEntry;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.ObjectProperty;
    import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
    import uk.co.jackoftrades.middle.objects.enums.TValue;

    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ObjectPropertyParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ObjectPropertyVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(ObjectPropertyParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(ObjectPropertyParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#subType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubType(ObjectPropertyParser.SubTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#idType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdType(ObjectPropertyParser.IdTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(ObjectPropertyParser.CodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#power}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPower(ObjectPropertyParser.PowerContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#mult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMult(ObjectPropertyParser.MultContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#typeMult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeMult(ObjectPropertyParser.TypeMultContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#adjective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdjective(ObjectPropertyParser.AdjectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#negAdjective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegAdjective(ObjectPropertyParser.NegAdjectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#msg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMsg(ObjectPropertyParser.MsgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#bindUI}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBindUI(ObjectPropertyParser.BindUIContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#desc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesc(ObjectPropertyParser.DescContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#objProperty}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObjProperty(ObjectPropertyParser.ObjPropertyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ObjectPropertyParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(ObjectPropertyParser.FileContext ctx);
}