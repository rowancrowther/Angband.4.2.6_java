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

package uk.co.jackoftradesltd.channel.messages.data;

/**
 * Immutable snapshot of the player-status sidebar — the two-channel migration's stand-in for
 * C's direct reads of the {@code player}/{@code cave} globals scattered across the {@code prt_*}
 * family in {@code src/ui-display.c}. Where C repaints one sidebar line at a time by reading the
 * live global state at redraw, the UI thread on this side of the boundary cannot reach across and
 * read those globals itself, so the core instead pushes every value that would feed a
 * {@code prt_*} call across as it changes; {@link PlayerEventStatusUpdate} accumulates the pushed
 * values into one {@code PlayerStatusView} per change and hands the whole snapshot to the UI
 * thread through {@link PlayerEventStatusUpdate#getPlayerStatusView()}. There is no single C
 * struct behind this shape — it exists only because the port needs one boundary-crossing payload
 * where C has many independent global reads.
 *
 * <p>The components fall into three groups, marked by the comments in the declaration below and
 * mirrored by the three groups of {@code prt_*} functions in {@code ui-display.c}: player details
 * (name, class, stats, HP/SP, AC, speed, gold, experience), tracked-monster details (the health
 * bar and the {@code MON_TMD_*} flags that colour it), and dungeon details (depth, study/DTrap/
 * resting/feeling/light status lines, and the equippy row's slot count). Each component's
 * {@code @param} entry below names the exact {@code prt_*} function (or, where a field has no
 * sidebar line of its own, the raw {@code player}/{@code cave} field) it ports.
 *
 * <p>Being a record, every field here is set once at construction and never mutated in place;
 * {@link PlayerEventStatusUpdate}'s {@code updatePlayerStatus*} family changes one field at a
 * time only by building a whole new {@code PlayerStatusView} from the current one, unlike C's
 * direct mutation of the live {@code player} struct.
 *
 * <p>Record PlayerStatusView coded before 260912, commented in full on 260915.
 *
 * @param name                  the player's name; C's {@code player->full_name} has no dedicated
 *                              sidebar line — it is shown on the character screen, not the
 *                              sidebar this record otherwise mirrors
 * @param title                 the player's title line, the port of {@code prt_title}
 *                              ({@code ui-display.c:197}), which shows the wizard/winner/
 *                              shapechange override from {@code fmt_title} or else
 *                              {@code player->class->title[...]}
 * @param raceName              the player's race name, the port of {@code prt_race}
 *                              ({@code ui-display.c:568}), which blanks the field instead while
 *                              shapechanged
 * @param className             the player's class name, the port of {@code prt_class}
 *                              ({@code ui-display.c:592}), which blanks the field instead while
 *                              shapechanged
 * @param level                 the character's level, the port of {@code prt_level}
 *                              ({@code ui-display.c:210}), which reads {@code player->lev}
 * @param experience            the experience total, the port of {@code prt_exp}
 *                              ({@code ui-display.c:230}), which shows {@code player->exp} at
 *                              level 50 or else the XP still needed for the next level
 * @param maxExperience         the maximum experience total, C's {@code player->max_exp} — the
 *                              threshold {@code prt_exp} compares {@code experience} against to
 *                              choose the drained ({@code "Exp"}/{@code "Nxt"}) or full
 *                              ({@code "EXP"}/{@code "NXT"}) label
 * @param gold                  the gold total, the port of {@code prt_gold}
 *                              ({@code ui-display.c:261}), which reads {@code player->au}
 * @param chp                   current hit points, the port of {@code prt_hp}
 *                              ({@code ui-display.c:322}), which reads {@code player->chp}
 * @param mhp                   maximum hit points, the port of {@code prt_hp}
 *                              ({@code ui-display.c:322}), which reads {@code player->mhp}
 * @param csp                   current spell points, the port of {@code prt_sp}
 *                              ({@code ui-display.c:341}), which reads {@code player->csp}
 * @param msp                   maximum spell points, the port of {@code prt_sp}
 *                              ({@code ui-display.c:341}), which reads {@code player->msp}
 * @param armourClass           the armour class, the port of {@code prt_ac}
 *                              ({@code ui-display.c:308}), which reads
 *                              {@code player->known_state.ac + player->known_state.to_a}
 * @param speed                 the character's speed, the port of {@code prt_speed}/
 *                              {@code prt_speed_aux} ({@code ui-display.c:521, 487}), which read
 *                              {@code player->state.speed} (110 is normal)
 * @param currentStats          the five current stat values in {@code STAT_STR}..{@code STAT_CON}
 *                              order, the port of {@code prt_stat} ({@code ui-display.c:154}),
 *                              which reads {@code player->state.stat_use[stat]}
 * @param maxStats              the five maximum stat values in the same order, the port of the
 *                              {@code player->stat_cur[stat] < player->stat_max[stat]} comparison
 *                              in {@code prt_stat} ({@code ui-display.c:154}) that decides
 *                              whether a stat is shown injured (reduced label, yellow) or
 *                              healthy (full label, green)
 * @param statString            the five stat abbreviations in the same order, the port of the
 *                              {@code stat_names} array ({@code ui-display.c:99-102}) —
 *                              fixed schema data, the same for every character, not a per-player
 *                              value; C's lowercase {@code stat_names_reduced} form
 *                              ({@code ui-display.c:107-110}) for injured stats is not carried
 *                              here, left to whatever renders this record from
 *                              {@link #currentStats} and {@link #maxStats}
 * @param bodyCount             the player's body-part count, the port of
 *                              {@code player->body.count} as read by
 *                              {@code configure_char_sheet}/{@code have_valid_char_sheet_config}
 *                              ({@code src/ui-player.c:223-225, 152-153}) to size the character
 *                              screen's resistance-panel column count; a separate field from
 *                              {@link #equipmentSlotCount} even though both trace back to the
 *                              same C value, because C reads {@code player->body.count} live at
 *                              each call site and this snapshot design has no single global to
 *                              read from
 * @param monsterHealth         the tracked monster's current hit points, the port of
 *                              {@code prt_health_aux} ({@code ui-display.c:436}), which reads
 *                              {@code mon->hp} for the monster at
 *                              {@code player->upkeep->health_who}
 * @param maxMonsterHealth      the tracked monster's maximum hit points, the port of
 *                              {@code prt_health_aux} ({@code ui-display.c:436}), which reads
 *                              {@code mon->maxhp} to compute the health-bar percentage
 * @param monsterVisible        whether the tracked monster is currently visible, the port of the
 *                              {@code monster_is_visible(mon)} check in {@code prt_health_aux}/
 *                              {@code monster_health_attr} ({@code ui-display.c:436, 375}),
 *                              which decides between the real health bar and the
 *                              {@code [----------]} placeholder
 * @param playerHallucinating   whether the player is hallucinating, the port of the
 *                              {@code player->timed[TMD_IMAGE]} check in {@code prt_health_aux}
 *                              ({@code ui-display.c:436}), which forces the placeholder health
 *                              bar even for a visible, healthy monster
 * @param monsterTracked        whether a monster is currently being tracked, the port of the
 *                              {@code player->upkeep->health_who} null check in
 *                              {@code prt_health_aux} ({@code ui-display.c:436}), which clears
 *                              the health bar entirely when nothing is tracked
 * @param monsterTmdFear        whether the tracked monster is afraid, the port of
 *                              {@code mon->m_timed[MON_TMD_FEAR]} in {@code monster_health_attr}
 *                              ({@code ui-display.c:375}), which colours the health bar violet
 * @param monsterTmdDisen       whether the tracked monster is disenchanted, the port of
 *                              {@code mon->m_timed[MON_TMD_DISEN]} in
 *                              {@code monster_health_attr} ({@code ui-display.c:375}), which
 *                              colours the health bar light umber
 * @param monsterTmdCommand     whether the tracked monster is under command, the port of
 *                              {@code mon->m_timed[MON_TMD_COMMAND]} in
 *                              {@code monster_health_attr} ({@code ui-display.c:375}), which
 *                              colours the health bar light purple
 * @param monsterTmdConf        whether the tracked monster is confused, the port of
 *                              {@code mon->m_timed[MON_TMD_CONF]} in {@code monster_health_attr}
 *                              ({@code ui-display.c:375}), which colours the health bar umber
 * @param monsterTmdStun        whether the tracked monster is stunned, the port of
 *                              {@code mon->m_timed[MON_TMD_STUN]} in {@code monster_health_attr}
 *                              ({@code ui-display.c:375}), which colours the health bar light
 *                              blue
 * @param monsterTmdSleep       whether the tracked monster is asleep, the port of
 *                              {@code mon->m_timed[MON_TMD_SLEEP]} in
 *                              {@code monster_health_attr} ({@code ui-display.c:375}), which
 *                              colours the health bar blue
 * @param monsterTmdHold        whether the tracked monster is held, the port of
 *                              {@code mon->m_timed[MON_TMD_HOLD]} in {@code monster_health_attr}
 *                              ({@code ui-display.c:375}), which colours the health bar blue —
 *                              the same colour as asleep, since C does not distinguish the two
 *                              on the bar
 * @param depth                 the dungeon depth, the port of {@code prt_depth}
 *                              ({@code ui-display.c:546}), which formats {@code player->depth}
 *                              via {@code fmt_depth}
 * @param studyStatus           the study-status message, the port of {@code prt_study}
 *                              ({@code ui-display.c:1247}), which formats
 *                              {@code "Study (%d)"} from {@code player->upkeep->new_spells}
 *                              when the player can learn a new spell
 * @param studyConditions       the study-conditions message, the port of the
 *                              {@code player_book_has_unlearned_spells} check in
 *                              {@code prt_study} ({@code ui-display.c:1247}), which dims the
 *                              study message when the player is not carrying a book with spells
 *                              left to learn
 * @param detectionStatus       the trap-detection status message, the port of {@code prt_dtrap}
 *                              ({@code ui-display.c:1227}), which prints {@code "DTrap"} —
 *                              yellow on the border of a trap-detected area, green within it —
 *                              while the player's grid has been trap-detected
 * @param restingRepeatingState the resting/repeat-status message, the port of {@code prt_state}
 *                              ({@code ui-display.c:974}), which prints {@code "Rest"} with a
 *                              turn count while resting, or {@code "Repeat"} with a count while a
 *                              command is being repeated
 * @param levelFeeling          the level-feeling message, the port of {@code prt_level_feeling}
 *                              ({@code ui-display.c:1071}), which formats {@code "LF:"} followed
 *                              by the monster and object feelings derived from
 *                              {@code cave->feeling}
 * @param lightLevel            the player-grid light-level message, the port of {@code prt_light}
 *                              ({@code ui-display.c:1148}), which formats {@code "Light %d"}
 *                              from {@code square_light(cave, player->grid)}
 * @param equipmentSlotCount    the equipment-slot count, the port of {@code prt_equippy}
 *                              ({@code ui-display.c:275}), which loops
 *                              {@code player->body.count} times to draw one equippy character
 *                              per slot
 * @author Rowan Crowther
 */
public record PlayerStatusView(// Player details
                               String name,
                               String title,
                               String raceName,
                               String className,
                               int level,
                               long experience,
                               long maxExperience,
                               long gold,
                               int chp,
                               int mhp,
                               int csp,
                               int msp,
                               int armourClass,
                               int speed,
                               int[] currentStats,
                               int[] maxStats,
                               String[] statString,
                               int bodyCount,

                               // Monster details
                               int monsterHealth,
                               int maxMonsterHealth,
                               boolean monsterVisible,
                               boolean playerHallucinating,
                               boolean monsterTracked,
                               boolean monsterTmdFear,
                               boolean monsterTmdDisen,
                               boolean monsterTmdCommand,
                               boolean monsterTmdConf,
                               boolean monsterTmdStun,
                               boolean monsterTmdSleep,
                               boolean monsterTmdHold,

                               // Dungeon details
                               int depth,
                               String studyStatus,
                               String studyConditions,
                               String detectionStatus,
                               String restingRepeatingState,
                               String levelFeeling,
                               String lightLevel,
                               int equipmentSlotCount) {
}
