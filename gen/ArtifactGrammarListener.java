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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/ArtifactGrammar.g4 by ANTLR 4.13.2

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ArtifactGrammar}.
 */
public interface ArtifactGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void enterRecordCount(ArtifactGrammar.RecordCountContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#recordCount}.
	 * @param ctx the parse tree
	 */
	void exitRecordCount(ArtifactGrammar.RecordCountContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#name}.
	 * @param ctx the parse tree
	 */
	void enterName(ArtifactGrammar.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ArtifactGrammar.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#baseObject}.
	 * @param ctx the parse tree
	 */
	void enterBaseObject(ArtifactGrammar.BaseObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#baseObject}.
	 * @param ctx the parse tree
	 */
	void exitBaseObject(ArtifactGrammar.BaseObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#graphics}.
	 * @param ctx the parse tree
	 */
	void enterGraphics(ArtifactGrammar.GraphicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#graphics}.
	 * @param ctx the parse tree
	 */
	void exitGraphics(ArtifactGrammar.GraphicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#level}.
	 * @param ctx the parse tree
	 */
	void enterLevel(ArtifactGrammar.LevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#level}.
	 * @param ctx the parse tree
	 */
	void exitLevel(ArtifactGrammar.LevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(ArtifactGrammar.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(ArtifactGrammar.WeightContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#cost}.
	 * @param ctx the parse tree
	 */
	void enterCost(ArtifactGrammar.CostContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#cost}.
	 * @param ctx the parse tree
	 */
	void exitCost(ArtifactGrammar.CostContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#alloc}.
	 * @param ctx the parse tree
	 */
	void enterAlloc(ArtifactGrammar.AllocContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#alloc}.
	 * @param ctx the parse tree
	 */
	void exitAlloc(ArtifactGrammar.AllocContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#attack}.
	 * @param ctx the parse tree
	 */
	void enterAttack(ArtifactGrammar.AttackContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#attack}.
	 * @param ctx the parse tree
	 */
	void exitAttack(ArtifactGrammar.AttackContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#armour}.
	 * @param ctx the parse tree
	 */
	void enterArmour(ArtifactGrammar.ArmourContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#armour}.
	 * @param ctx the parse tree
	 */
	void exitArmour(ArtifactGrammar.ArmourContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void enterFlags(ArtifactGrammar.FlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#flags}.
	 * @param ctx the parse tree
	 */
	void exitFlags(ArtifactGrammar.FlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#act}.
	 * @param ctx the parse tree
	 */
	void enterAct(ArtifactGrammar.ActContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#act}.
	 * @param ctx the parse tree
	 */
	void exitAct(ArtifactGrammar.ActContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#time}.
	 * @param ctx the parse tree
	 */
	void enterTime(ArtifactGrammar.TimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#time}.
	 * @param ctx the parse tree
	 */
	void exitTime(ArtifactGrammar.TimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#msg}.
	 * @param ctx the parse tree
	 */
	void enterMsg(ArtifactGrammar.MsgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#msg}.
	 * @param ctx the parse tree
	 */
	void exitMsg(ArtifactGrammar.MsgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#values}.
	 * @param ctx the parse tree
	 */
	void enterValues(ArtifactGrammar.ValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#values}.
	 * @param ctx the parse tree
	 */
	void exitValues(ArtifactGrammar.ValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#brand}.
	 * @param ctx the parse tree
	 */
	void enterBrand(ArtifactGrammar.BrandContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#brand}.
	 * @param ctx the parse tree
	 */
	void exitBrand(ArtifactGrammar.BrandContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#slay}.
	 * @param ctx the parse tree
	 */
	void enterSlay(ArtifactGrammar.SlayContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#slay}.
	 * @param ctx the parse tree
	 */
	void exitSlay(ArtifactGrammar.SlayContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#curse}.
	 * @param ctx the parse tree
	 */
	void enterCurse(ArtifactGrammar.CurseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#curse}.
	 * @param ctx the parse tree
	 */
	void exitCurse(ArtifactGrammar.CurseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(ArtifactGrammar.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(ArtifactGrammar.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#artifact}.
	 * @param ctx the parse tree
	 */
	void enterArtifact(ArtifactGrammar.ArtifactContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#artifact}.
	 * @param ctx the parse tree
	 */
	void exitArtifact(ArtifactGrammar.ArtifactContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactGrammar#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ArtifactGrammar.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactGrammar#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ArtifactGrammar.FileContext ctx);
}