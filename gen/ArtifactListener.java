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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/scratch/antlr_artifact_check/src/uk/co/jackoftrades/backend/parser/grammars/Artifact.g4 by ANTLR 4.13.2

    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.Activation;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.Artifact;
    import uk.co.jackoftrades.middle.objects.Brand;
    import uk.co.jackoftrades.middle.objects.Curse;
    import uk.co.jackoftrades.middle.objects.CurseData;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.Slay;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ArtifactParser}.
 */
public interface ArtifactListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(ArtifactParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ArtifactParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#baseObject}.
	 * @param ctx the parse tree
	 */
	void enterBaseObject(ArtifactParser.BaseObjectContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#baseObject}.
	 * @param ctx the parse tree
	 */
	void exitBaseObject(ArtifactParser.BaseObjectContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#graphics}.
	 * @param ctx the parse tree
	 */
	void enterGraphics(ArtifactParser.GraphicsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#graphics}.
	 * @param ctx the parse tree
	 */
	void exitGraphics(ArtifactParser.GraphicsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#level}.
	 * @param ctx the parse tree
	 */
	void enterLevel(ArtifactParser.LevelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#level}.
	 * @param ctx the parse tree
	 */
	void exitLevel(ArtifactParser.LevelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#weight}.
	 * @param ctx the parse tree
	 */
	void enterWeight(ArtifactParser.WeightContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#weight}.
	 * @param ctx the parse tree
	 */
	void exitWeight(ArtifactParser.WeightContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#cost}.
	 * @param ctx the parse tree
	 */
	void enterCost(ArtifactParser.CostContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#cost}.
	 * @param ctx the parse tree
	 */
	void exitCost(ArtifactParser.CostContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#alloc}.
	 * @param ctx the parse tree
	 */
	void enterAlloc(ArtifactParser.AllocContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#alloc}.
	 * @param ctx the parse tree
	 */
	void exitAlloc(ArtifactParser.AllocContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#attack}.
	 * @param ctx the parse tree
	 */
	void enterAttack(ArtifactParser.AttackContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#attack}.
	 * @param ctx the parse tree
	 */
	void exitAttack(ArtifactParser.AttackContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#armour}.
	 * @param ctx the parse tree
	 */
	void enterArmour(ArtifactParser.ArmourContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#armour}.
	 * @param ctx the parse tree
	 */
	void exitArmour(ArtifactParser.ArmourContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#flags}.
	 * @param ctx the parse tree
	 */
	void enterFlags(ArtifactParser.FlagsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#flags}.
	 * @param ctx the parse tree
	 */
	void exitFlags(ArtifactParser.FlagsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#values}.
	 * @param ctx the parse tree
	 */
	void enterValues(ArtifactParser.ValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#values}.
	 * @param ctx the parse tree
	 */
	void exitValues(ArtifactParser.ValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#act}.
	 * @param ctx the parse tree
	 */
	void enterAct(ArtifactParser.ActContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#act}.
	 * @param ctx the parse tree
	 */
	void exitAct(ArtifactParser.ActContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#time}.
	 * @param ctx the parse tree
	 */
	void enterTime(ArtifactParser.TimeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#time}.
	 * @param ctx the parse tree
	 */
	void exitTime(ArtifactParser.TimeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#desc}.
	 * @param ctx the parse tree
	 */
	void enterDesc(ArtifactParser.DescContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#desc}.
	 * @param ctx the parse tree
	 */
	void exitDesc(ArtifactParser.DescContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#brand}.
	 * @param ctx the parse tree
	 */
	void enterBrand(ArtifactParser.BrandContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#brand}.
	 * @param ctx the parse tree
	 */
	void exitBrand(ArtifactParser.BrandContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#slay}.
	 * @param ctx the parse tree
	 */
	void enterSlay(ArtifactParser.SlayContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#slay}.
	 * @param ctx the parse tree
	 */
	void exitSlay(ArtifactParser.SlayContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#curse}.
	 * @param ctx the parse tree
	 */
	void enterCurse(ArtifactParser.CurseContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#curse}.
	 * @param ctx the parse tree
	 */
	void exitCurse(ArtifactParser.CurseContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#msg}.
	 * @param ctx the parse tree
	 */
	void enterMsg(ArtifactParser.MsgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#msg}.
	 * @param ctx the parse tree
	 */
	void exitMsg(ArtifactParser.MsgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#artifact}.
	 * @param ctx the parse tree
	 */
	void enterArtifact(ArtifactParser.ArtifactContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#artifact}.
	 * @param ctx the parse tree
	 */
	void exitArtifact(ArtifactParser.ArtifactContext ctx);
	/**
	 * Enter a parse tree produced by {@link ArtifactParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(ArtifactParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link ArtifactParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(ArtifactParser.FileContext ctx);
}