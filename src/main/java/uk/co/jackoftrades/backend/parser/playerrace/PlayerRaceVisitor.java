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

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerRace.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playerrace;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PlayerRaceParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 *            operations with no return type.
 */
public interface PlayerRaceVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#name}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitName(PlayerRaceParser.NameContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#stats}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStats(PlayerRaceParser.StatsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_disarm_phys}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_disarm_phys(PlayerRaceParser.Skill_disarm_physContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_disarm_magic}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_disarm_magic(PlayerRaceParser.Skill_disarm_magicContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_device}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_device(PlayerRaceParser.Skill_deviceContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_save}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_save(PlayerRaceParser.Skill_saveContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_stealth}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_stealth(PlayerRaceParser.Skill_stealthContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_search}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_search(PlayerRaceParser.Skill_searchContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_melee}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_melee(PlayerRaceParser.Skill_meleeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_shoot}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_shoot(PlayerRaceParser.Skill_shootContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_throw}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_throw(PlayerRaceParser.Skill_throwContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#skill_dig}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSkill_dig(PlayerRaceParser.Skill_digContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#hitdie}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHitdie(PlayerRaceParser.HitdieContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#exp}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExp(PlayerRaceParser.ExpContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#infravision}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitInfravision(PlayerRaceParser.InfravisionContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#history}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHistory(PlayerRaceParser.HistoryContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#age}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAge(PlayerRaceParser.AgeContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#height}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitHeight(PlayerRaceParser.HeightContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#weight}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWeight(PlayerRaceParser.WeightContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#obj_flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitObj_flags(PlayerRaceParser.Obj_flagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#player_flags}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPlayer_flags(PlayerRaceParser.Player_flagsContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#values}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitValues(PlayerRaceParser.ValuesContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#race}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRace(PlayerRaceParser.RaceContext ctx);

    /**
     * Visit a parse tree produced by {@link PlayerRaceParser#file}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFile(PlayerRaceParser.FileContext ctx);
}