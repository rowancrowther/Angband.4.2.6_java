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

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerRaceGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface PlayerRaceGrammarVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#recordCount}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRecordCount(PlayerRaceGrammar.RecordCountContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(PlayerRaceGrammar.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#stats}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStats(PlayerRaceGrammar.StatsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillDisarmPhys}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDisarmPhys(PlayerRaceGrammar.SkillDisarmPhysContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillDisarmMagic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDisarmMagic(PlayerRaceGrammar.SkillDisarmMagicContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillDevice}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDevice(PlayerRaceGrammar.SkillDeviceContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillSave}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillSave(PlayerRaceGrammar.SkillSaveContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillStealth}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillStealth(PlayerRaceGrammar.SkillStealthContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillSearch}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillSearch(PlayerRaceGrammar.SkillSearchContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillMelee}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillMelee(PlayerRaceGrammar.SkillMeleeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillShoot}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillShoot(PlayerRaceGrammar.SkillShootContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillThrow}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillThrow(PlayerRaceGrammar.SkillThrowContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#skillDig}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkillDig(PlayerRaceGrammar.SkillDigContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#hitdie}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHitdie(PlayerRaceGrammar.HitdieContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#exp}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExp(PlayerRaceGrammar.ExpContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#infravision}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitInfravision(PlayerRaceGrammar.InfravisionContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#history}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHistory(PlayerRaceGrammar.HistoryContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#age}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAge(PlayerRaceGrammar.AgeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#height}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHeight(PlayerRaceGrammar.HeightContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#weight}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWeight(PlayerRaceGrammar.WeightContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#objFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObjFlags(PlayerRaceGrammar.ObjFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#playerFlags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerFlags(PlayerRaceGrammar.PlayerFlagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(PlayerRaceGrammar.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#playerRace}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayerRace(PlayerRaceGrammar.PlayerRaceContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceGrammar#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(PlayerRaceGrammar.FileContext ctx);
}