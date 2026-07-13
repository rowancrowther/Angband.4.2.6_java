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
// Generated from PlayerRaceGrammar.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.grammars.playerrace;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerRaceGrammar}.
 */
public interface PlayerRaceGrammarListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void enterRecordCount(PlayerRaceGrammar.RecordCountContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#recordCount}.
     *
     * @param ctx the parse tree
     */
    void exitRecordCount(PlayerRaceGrammar.RecordCountContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(PlayerRaceGrammar.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(PlayerRaceGrammar.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#stats}.
     *
     * @param ctx the parse tree
     */
    void enterStats(PlayerRaceGrammar.StatsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#stats}.
     *
     * @param ctx the parse tree
     */
    void exitStats(PlayerRaceGrammar.StatsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillDisarmPhys}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDisarmPhys(PlayerRaceGrammar.SkillDisarmPhysContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillDisarmPhys}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDisarmPhys(PlayerRaceGrammar.SkillDisarmPhysContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillDisarmMagic}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDisarmMagic(PlayerRaceGrammar.SkillDisarmMagicContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillDisarmMagic}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDisarmMagic(PlayerRaceGrammar.SkillDisarmMagicContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillDevice}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDevice(PlayerRaceGrammar.SkillDeviceContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillDevice}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDevice(PlayerRaceGrammar.SkillDeviceContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillSave}.
     *
     * @param ctx the parse tree
     */
    void enterSkillSave(PlayerRaceGrammar.SkillSaveContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillSave}.
     *
     * @param ctx the parse tree
     */
    void exitSkillSave(PlayerRaceGrammar.SkillSaveContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillStealth}.
     *
     * @param ctx the parse tree
     */
    void enterSkillStealth(PlayerRaceGrammar.SkillStealthContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillStealth}.
     *
     * @param ctx the parse tree
     */
    void exitSkillStealth(PlayerRaceGrammar.SkillStealthContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillSearch}.
     *
     * @param ctx the parse tree
     */
    void enterSkillSearch(PlayerRaceGrammar.SkillSearchContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillSearch}.
     *
     * @param ctx the parse tree
     */
    void exitSkillSearch(PlayerRaceGrammar.SkillSearchContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillMelee}.
     *
     * @param ctx the parse tree
     */
    void enterSkillMelee(PlayerRaceGrammar.SkillMeleeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillMelee}.
     *
     * @param ctx the parse tree
     */
    void exitSkillMelee(PlayerRaceGrammar.SkillMeleeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillShoot}.
     *
     * @param ctx the parse tree
     */
    void enterSkillShoot(PlayerRaceGrammar.SkillShootContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillShoot}.
     *
     * @param ctx the parse tree
     */
    void exitSkillShoot(PlayerRaceGrammar.SkillShootContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillThrow}.
     *
     * @param ctx the parse tree
     */
    void enterSkillThrow(PlayerRaceGrammar.SkillThrowContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillThrow}.
     *
     * @param ctx the parse tree
     */
    void exitSkillThrow(PlayerRaceGrammar.SkillThrowContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#skillDig}.
     *
     * @param ctx the parse tree
     */
    void enterSkillDig(PlayerRaceGrammar.SkillDigContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#skillDig}.
     *
     * @param ctx the parse tree
     */
    void exitSkillDig(PlayerRaceGrammar.SkillDigContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#hitdie}.
     *
     * @param ctx the parse tree
     */
    void enterHitdie(PlayerRaceGrammar.HitdieContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#hitdie}.
     *
     * @param ctx the parse tree
     */
    void exitHitdie(PlayerRaceGrammar.HitdieContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#exp}.
     *
     * @param ctx the parse tree
     */
    void enterExp(PlayerRaceGrammar.ExpContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#exp}.
     *
     * @param ctx the parse tree
     */
    void exitExp(PlayerRaceGrammar.ExpContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#infravision}.
     *
     * @param ctx the parse tree
     */
    void enterInfravision(PlayerRaceGrammar.InfravisionContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#infravision}.
     *
     * @param ctx the parse tree
     */
    void exitInfravision(PlayerRaceGrammar.InfravisionContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#history}.
     *
     * @param ctx the parse tree
     */
    void enterHistory(PlayerRaceGrammar.HistoryContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#history}.
     *
     * @param ctx the parse tree
     */
    void exitHistory(PlayerRaceGrammar.HistoryContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#age}.
     *
     * @param ctx the parse tree
     */
    void enterAge(PlayerRaceGrammar.AgeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#age}.
     *
     * @param ctx the parse tree
     */
    void exitAge(PlayerRaceGrammar.AgeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#height}.
     *
     * @param ctx the parse tree
     */
    void enterHeight(PlayerRaceGrammar.HeightContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#height}.
     *
     * @param ctx the parse tree
     */
    void exitHeight(PlayerRaceGrammar.HeightContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#weight}.
     *
     * @param ctx the parse tree
     */
    void enterWeight(PlayerRaceGrammar.WeightContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#weight}.
     *
     * @param ctx the parse tree
     */
    void exitWeight(PlayerRaceGrammar.WeightContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#objFlags}.
     *
     * @param ctx the parse tree
     */
    void enterObjFlags(PlayerRaceGrammar.ObjFlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#objFlags}.
     *
     * @param ctx the parse tree
     */
    void exitObjFlags(PlayerRaceGrammar.ObjFlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#playerFlags}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerFlags(PlayerRaceGrammar.PlayerFlagsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#playerFlags}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerFlags(PlayerRaceGrammar.PlayerFlagsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(PlayerRaceGrammar.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(PlayerRaceGrammar.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#playerRace}.
     *
     * @param ctx the parse tree
     */
    void enterPlayerRace(PlayerRaceGrammar.PlayerRaceContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#playerRace}.
     *
     * @param ctx the parse tree
     */
    void exitPlayerRace(PlayerRaceGrammar.PlayerRaceContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(PlayerRaceGrammar.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceGrammar#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(PlayerRaceGrammar.FileContext ctx);
}