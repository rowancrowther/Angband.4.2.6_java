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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/parser/grammars/PlayerRace.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.parser.playerrace;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlayerRaceParser}.
 */
public interface PlayerRaceListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#name}.
     *
     * @param ctx the parse tree
     */
    void enterName(PlayerRaceParser.NameContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#name}.
     *
     * @param ctx the parse tree
     */
    void exitName(PlayerRaceParser.NameContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#stats}.
     *
     * @param ctx the parse tree
     */
    void enterStats(PlayerRaceParser.StatsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#stats}.
     *
     * @param ctx the parse tree
     */
    void exitStats(PlayerRaceParser.StatsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_disarm_phys}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_disarm_phys(PlayerRaceParser.Skill_disarm_physContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_disarm_phys}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_disarm_phys(PlayerRaceParser.Skill_disarm_physContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_disarm_magic}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_disarm_magic(PlayerRaceParser.Skill_disarm_magicContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_disarm_magic}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_disarm_magic(PlayerRaceParser.Skill_disarm_magicContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_device}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_device(PlayerRaceParser.Skill_deviceContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_device}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_device(PlayerRaceParser.Skill_deviceContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_save}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_save(PlayerRaceParser.Skill_saveContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_save}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_save(PlayerRaceParser.Skill_saveContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_stealth}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_stealth(PlayerRaceParser.Skill_stealthContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_stealth}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_stealth(PlayerRaceParser.Skill_stealthContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_search}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_search(PlayerRaceParser.Skill_searchContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_search}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_search(PlayerRaceParser.Skill_searchContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_melee}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_melee(PlayerRaceParser.Skill_meleeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_melee}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_melee(PlayerRaceParser.Skill_meleeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_shoot}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_shoot(PlayerRaceParser.Skill_shootContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_shoot}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_shoot(PlayerRaceParser.Skill_shootContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_throw}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_throw(PlayerRaceParser.Skill_throwContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_throw}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_throw(PlayerRaceParser.Skill_throwContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#skill_dig}.
     *
     * @param ctx the parse tree
     */
    void enterSkill_dig(PlayerRaceParser.Skill_digContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#skill_dig}.
     *
     * @param ctx the parse tree
     */
    void exitSkill_dig(PlayerRaceParser.Skill_digContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#hitdie}.
     *
     * @param ctx the parse tree
     */
    void enterHitdie(PlayerRaceParser.HitdieContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#hitdie}.
     *
     * @param ctx the parse tree
     */
    void exitHitdie(PlayerRaceParser.HitdieContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#exp}.
     *
     * @param ctx the parse tree
     */
    void enterExp(PlayerRaceParser.ExpContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#exp}.
     *
     * @param ctx the parse tree
     */
    void exitExp(PlayerRaceParser.ExpContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#infravision}.
     *
     * @param ctx the parse tree
     */
    void enterInfravision(PlayerRaceParser.InfravisionContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#infravision}.
     *
     * @param ctx the parse tree
     */
    void exitInfravision(PlayerRaceParser.InfravisionContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#history}.
     *
     * @param ctx the parse tree
     */
    void enterHistory(PlayerRaceParser.HistoryContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#history}.
     *
     * @param ctx the parse tree
     */
    void exitHistory(PlayerRaceParser.HistoryContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#age}.
     *
     * @param ctx the parse tree
     */
    void enterAge(PlayerRaceParser.AgeContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#age}.
     *
     * @param ctx the parse tree
     */
    void exitAge(PlayerRaceParser.AgeContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#height}.
     *
     * @param ctx the parse tree
     */
    void enterHeight(PlayerRaceParser.HeightContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#height}.
     *
     * @param ctx the parse tree
     */
    void exitHeight(PlayerRaceParser.HeightContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#weight}.
     *
     * @param ctx the parse tree
     */
    void enterWeight(PlayerRaceParser.WeightContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#weight}.
     *
     * @param ctx the parse tree
     */
    void exitWeight(PlayerRaceParser.WeightContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#obj_flags}.
     *
     * @param ctx the parse tree
     */
    void enterObj_flags(PlayerRaceParser.Obj_flagsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#obj_flags}.
     *
     * @param ctx the parse tree
     */
    void exitObj_flags(PlayerRaceParser.Obj_flagsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#player_flags}.
     *
     * @param ctx the parse tree
     */
    void enterPlayer_flags(PlayerRaceParser.Player_flagsContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#player_flags}.
     *
     * @param ctx the parse tree
     */
    void exitPlayer_flags(PlayerRaceParser.Player_flagsContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#values}.
     *
     * @param ctx the parse tree
     */
    void enterValues(PlayerRaceParser.ValuesContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#values}.
     *
     * @param ctx the parse tree
     */
    void exitValues(PlayerRaceParser.ValuesContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#race}.
     *
     * @param ctx the parse tree
     */
    void enterRace(PlayerRaceParser.RaceContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#race}.
     *
     * @param ctx the parse tree
     */
    void exitRace(PlayerRaceParser.RaceContext ctx);

    /**
     * Enter a parse tree produced by {@link PlayerRaceParser#file}.
     *
     * @param ctx the parse tree
     */
    void enterFile(PlayerRaceParser.FileContext ctx);

    /**
     * Exit a parse tree produced by {@link PlayerRaceParser#file}.
     *
     * @param ctx the parse tree
     */
    void exitFile(PlayerRaceParser.FileContext ctx);
}