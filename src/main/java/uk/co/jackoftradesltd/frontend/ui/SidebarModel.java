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

package uk.co.jackoftradesltd.frontend.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

/**
 * The UI-owned model for the character sidebar's hit-point row — the front end's replacement for
 * reading {@code player->chp}/{@code mhp} straight off the shared player global, the way C's
 * {@code prt_hp} ({@code [C] ui-display.c}, function {@code prt_hp}) and {@code get_panel_topleft}
 * ({@code [C] ui-player.c}, function {@code get_panel_topleft}) both do at their own call time.
 *
 * <p>Written only by
 * {@link RedrawRouter#setHP(uk.co.jackoftradesltd.channel.messages.data.GameEventData)}, on the UI
 * thread that drains
 * {@code uiChannel}, and read only by the drawing code that runs on that same thread — see
 * {@code docs/implementation/260926_change_in_architecture_from_cache_to_messages.md}'s "the UI
 * thread owns the models" conclusion. Nothing running on the event dispatch thread may touch this
 * class; the EDT only ever sees the painted {@code Screen} snapshot, never the model itself.
 *
 * <p>Named for what it, and its siblings still to be written, replace field by field:
 * {@link uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate}'s static cache is
 * being architected out in their favour. Today this class holds the HP and SP pairs, the
 * hit-point warning option, whether the class has spells and its first-spell level, the player's
 * level and maximum level, title, race name, class name, full name, wizard and total-winner flags,
 * shape name and shapechanged status, current and displayed experience, gold, armour class, and the
 * five stats' current/maximum/displayed-use values, and the tracked monster's
 * health-bar state (hit points, visibility, seven timed-effect flags, and whether the player is
 * hallucinating), and the player's speed with the effective-speed option and the two energy-table
 * figures its multiplier needs, and the player's dungeon level; the rest of C's {@code prt_*}
 * family in {@code [C] ui-display.c} join it as their own {@code EVENT_*} payloads are ported.
 *
 * <p>The setters are package-private and the getters public, so only a class in this package —
 * today, only {@link RedrawRouter} — can write, while any caller may read.
 *
 * <p>Class SidebarModel coded on 260926, commented in full on 260929.
 *
 * @author Rowan Crowther
 */
public class SidebarModel {
    /**
     * The class's log4j logger, used to report a stat index outside the five the model holds.
     *
     * <p>Field logger coded on 260926, commented in full on 260929.
     */
    private static final Logger logger = LogManager.getLogger(SidebarModel.class);
    
    /**
     * The player's current hit points, C's {@code player->chp}, written each time an
     * {@code EVENT_HP} message is routed and read whenever the sidebar's HP row is drawn.
     *
     * <p>Field currentHP coded on 260926, commented in full on 260926.
     */
    private static int currentHP;

    /**
     * The player's maximum hit points, C's {@code player->mhp}, written each time an
     * {@code EVENT_HP} message is routed and read whenever the sidebar's HP row is drawn.
     *
     * <p>Field maxHP coded on 260926, commented in full on 260926.
     */
    private static int maxHP;

    /**
     * The player's current spell points, C's {@code player->csp}, written each time an
     * {@code EVENT_MANA} message is routed and read whenever the sidebar's SP row is drawn.
     *
     * <p>Field currentSP coded on 260926, commented in full on 260926.
     */
    private static int currentSP;

    /**
     * The player's maximum spell points, C's {@code player->msp}, written each time an
     * {@code EVENT_MANA} message is routed and read whenever the sidebar's SP row is drawn.
     *
     * <p>Field maxSP coded on 260926, commented in full on 260926.
     */
    private static int maxSP;

    /**
     * The player's title, C's {@code show_title()}, written each time an {@code EVENT_PLAYERTITLE}
     * message is routed and read whenever the character sheet's Title row is drawn.
     *
     * <p>Field title coded before 260926, commented in full on 260926.
     */
    private static String title;

    /**
     * The player's class name, C's {@code player->class->name}, written each time an
     * {@code EVENT_RACE_CLASS} message is routed and read whenever the character sheet's Class row
     * is drawn.
     *
     * <p>Field className coded before 260926, commented in full on 260926.
     */
    private static String className;

    /**
     * The player's race name, C's {@code player->race->name}, written each time an
     * {@code EVENT_RACE_CLASS} message is routed and read whenever the character sheet's Race row
     * is drawn.
     *
     * <p>Field raceName coded before 260926, commented in full on 260926.
     */
    private static String raceName;

    /**
     * The player's full name, C's {@code player->full_name}, written each time an
     * {@code EVENT_PLAYER_NAME} message is routed and read whenever the character sheet's Name row
     * is drawn.
     *
     * <p>Field name coded before 260926, commented in full on 260926.
     */
    private static String name;

    /**
     * Whether the player is in wizard mode, C's {@code player->wizard}, written each time an
     * {@code EVENT_PLAYERTITLE} message is routed and read whenever the sidebar's title row is
     * drawn.
     *
     * <p>Field wizard coded on 260927, commented in full on 260927.
     */
    private static boolean wizard;

    /**
     * Whether the player has won the game, C's {@code player->total_winner}, written each time an
     * {@code EVENT_PLAYERTITLE} message is routed and read whenever the sidebar's title row is
     * drawn.
     *
     * <p>Field isTotalWinner coded on 260927, commented in full on 260927.
     */
    private static boolean isTotalWinner;

    /**
     * The name of the player's current shape, C's {@code player->shape->name}, written each time an
     * {@code EVENT_PLAYERTITLE} message is routed and read whenever the sidebar's title row is
     * drawn.
     *
     * <p>Field shapeName coded on 260927, commented in full on 260927.
     */
    private static String shapeName;

    /**
     * The player's current character level, C's {@code player->lev}, written each time an
     * {@code EVENT_PLAYERLEVEL} message is routed and read whenever the sidebar's level row is
     * drawn.
     *
     * <p>Field level coded on 260927, commented in full on 260927.
     */
    private static int level;

    /**
     * The player's highest character level yet attained, C's {@code player->max_lev}, written each
     * time an {@code EVENT_PLAYERLEVEL} message is routed and read whenever the sidebar's level row
     * is drawn.
     *
     * <p>Field maxLevel coded on 260927, commented in full on 260927.
     */
    private static int maxLevel;

    /**
     * The player's current experience total, C's {@code player->exp}, written each time an
     * {@code EVENT_EXPERIENCE} message carrying an
     * {@link uk.co.jackoftradesltd.channel.messages.data.EventDataLongStat} is routed and read
     * whenever the sidebar's experience row is drawn.
     *
     * <p>Field experience coded on 260927, commented in full on 260928.
     */
    private static long experience;

    /**
     * The figure the sidebar's experience row prints, C's local {@code xp} in {@code prt_exp}
     * ({@code [C] ui-display.c}) - the experience needed to reach the next level, or the running
     * total once the character has reached level fifty. Written each time an
     * {@code EVENT_EXPERIENCE} message carrying an
     * {@link uk.co.jackoftradesltd.channel.messages.data.EventDataLong} is routed; the choice
     * between the two figures is made core-side, in {@code PlayerCalcs.redrawStuff}'s
     * {@code PR_EXP} arm, not here.
     *
     * <p>Field xpToLevel coded on 260927, commented in full on 260928.
     */
    private static long xpToLevel;

    /**
     * The player's highest experience total yet held, C's {@code player->max_exp}, written each
     * time an {@code EVENT_EXPERIENCE} message carrying an
     * {@link uk.co.jackoftradesltd.channel.messages.data.EventDataLongStat} is routed and read
     * whenever the sidebar's experience row is drawn, to decide whether the row is coloured as
     * drained or full.
     *
     * <p>Field maxXp coded on 260927, commented in full on 260928.
     */
    private static long maxXp;
    /**
     * Whether the player is currently in a non-normal shape, C's {@code player_is_shapechanged}
     * ({@code [C] player-util.c}) — the port of what {@code prt_race} and {@code prt_class}
     * ({@code [C] ui-display.c}) each re-check against the live global at draw time, so that a
     * shapechanged player's race and class fields are blanked rather than shown.
     *
     * <p>Written by {@link RedrawRouter#setRaceClass} from the third element of the
     * {@code EVENT_RACE_CLASS} payload, which {@code PlayerCalcs.redrawStuff}'s {@code PR_MISC} arm
     * packs from {@code player.isShapeChanged()}. Read by
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtRace(int, int)} and
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtClass(int, int)} to
     * decide whether to blank those rows.
     *
     * <p>Field playerIsShapechanged coded on 260927, commented in full on 260927.
     */
    private static boolean playerIsShapechanged;

    /**
     * The player's current gold total, C's {@code player->au}, written each time an
     * {@code EVENT_GOLD} message is routed and read whenever the sidebar's gold row is drawn.
     *
     * <p>Field gold coded on 260927, commented in full on 260928.
     */
    private static long gold;

    /**
     * The sidebar's equippy row, one {@link AngbandDisplayCharacter} per equipment slot in slot
     * order - the port's stand-in for C's {@code prt_equippy} ({@code [C] ui-display.c}) reading
     * {@code player->body} and calling {@code object_attr}/{@code object_char} on each slot's
     * object directly at draw time. Written each time an {@code EVENT_EQUIPMENT} message is
     * routed and read whenever the sidebar's equippy row is drawn.
     *
     * <p>Field equipString coded on 260927, commented in full on 260928.
     */
    private static AngbandDisplayCharacter[] equipString;

    /**
     * The five stats' current values, C's {@code player->stat_cur[]}, indexed by
     * {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()} — written a row at a time each
     * time an {@code EVENT_STATS} message is routed and read whenever the sidebar's stat rows are
     * drawn, to decide whether a row is shown drained or full.
     *
     * <p>Field currentStats coded on 260927, commented in full on 260928.
     */
    private static int[] currentStats = new int[5];

    /**
     * The five stats' recorded maximum values, C's {@code player->stat_max[]}, indexed by
     * {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()} — written a row at a time each
     * time an {@code EVENT_STATS} message is routed and read whenever the sidebar's stat rows are
     * drawn, to decide whether a row is shown drained or full and whether the natural-maximum
     * marker is shown.
     *
     * <p>Field maxStats coded on 260927, commented in full on 260928.
     */
    private static int[] maxStats = new int[5];

    /**
     * The five stats' displayed values, C's {@code player->state.stat_use[]}, indexed by
     * {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()} — written a row at a time each
     * time an {@code EVENT_STATS} message is routed and read whenever the sidebar's stat rows are
     * drawn; this, not {@link #currentStats}, is the figure actually printed, since equipment or a
     * temporary effect can move it away from a stat's bare current value.
     *
     * <p>Field useStats coded on 260927, commented in full on 260928.
     */
    private static int[] useStats = new int[5];

    /**
     * The player's armour class, C's {@code player->known_state.ac + known_state.to_a}, written
     * each time an {@code EVENT_AC} message is routed and read whenever the sidebar's AC row is
     * drawn.
     *
     * <p>Field ac coded on 260927, commented in full on 260928.
     */
    private static int ac;

    /**
     * The player's hit-point warning option, C's {@code player->opts.hitpoint_warn} - the tenths of
     * maximum hit points (and, in C, maximum spell points) below which the current figure is drawn
     * red rather than yellow. Written each time an {@code EVENT_HP} message carrying an
     * {@link uk.co.jackoftradesltd.channel.messages.data.EventDataInt} is routed and read by
     * {@code HandlersHolder}'s {@code playerHpAttr} and {@code playerSPAttr}.
     *
     * <p>Field playerOptHPWarn coded on 260928, commented in full on 260928.
     */
    private static int playerOptHPWarn;

    /**
     * Whether the player's class has any spells at all, C's
     * {@code player->class->magic.total_spells != 0}. Written each time an {@code EVENT_MANA}
     * message carrying an {@link uk.co.jackoftradesltd.channel.messages.data.EventDataBoolean} is
     * routed and read by {@code HandlersHolder.prtSp} to decide whether the SP row is drawn or, for
     * a level-drained caster, blanked.
     *
     * <p>Field magic coded on 260928, commented in full on 260928.
     */
    private static boolean magic;

    /**
     * The level at which the player's class gains its first spell, C's
     * {@code player->class->magic.spell_first}. Written each time an {@code EVENT_MANA} message
     * carrying an {@link uk.co.jackoftradesltd.channel.messages.data.EventDataInt} is routed and
     * read by {@code HandlersHolder.prtSp}, which draws nothing below this level.
     *
     * <p>Field firstSpell coded on 260928, commented in full on 260928.
     */
    private static int firstSpell;

    /**
     * The tracked monster's current hit points, C's {@code mon->hp},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monHealth coded on 260929, commented in full on 260929.
     */
    private static int monHealth;

    /**
     * The tracked monster's maximum hit points, C's {@code mon->maxhp},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monMaxHealth coded on 260929, commented in full on 260929.
     */
    private static int monMaxHealth;

    /**
     * Whether a monster is being tracked at all, C's {@code player->upkeep->health_who != NULL},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn.
     *
     * <p>Field monExists coded on 260929, commented in full on 260929.
     */
    private static boolean monExists;

    /**
     * Whether the player can see the tracked monster, C's {@code monster_is_visible(mon)},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monVisible coded on 260929, commented in full on 260929.
     */
    private static boolean monVisible;

    /**
     * Whether the tracked monster is afraid, C's {@code mon->m_timed[MON_TMD_FEAR] != 0},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monFeared coded on 260929, commented in full on 260929.
     */
    private static boolean monFeared;

    /**
     * Whether the tracked monster is disenchanted, C's {@code mon->m_timed[MON_TMD_DISEN] != 0},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monDisen coded on 260929, commented in full on 260929.
     */
    private static boolean monDisen;

    /**
     * Whether the tracked monster is commanded, C's {@code mon->m_timed[MON_TMD_COMMAND] != 0},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monCommand coded on 260929, commented in full on 260929.
     */
    private static boolean monCommand;

    /**
     * Whether the tracked monster is confused, C's {@code mon->m_timed[MON_TMD_CONF] != 0},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monConf coded on 260929, commented in full on 260929.
     */
    private static boolean monConf;

    /**
     * Whether the tracked monster is stunned, C's {@code mon->m_timed[MON_TMD_STUN] != 0},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monStun coded on 260929, commented in full on 260929.
     */
    private static boolean monStun;

    /**
     * Whether the tracked monster is asleep, C's {@code mon->m_timed[MON_TMD_SLEEP] != 0},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monSlept coded on 260929, commented in full on 260929.
     */
    private static boolean monSlept;

    /**
     * Whether the tracked monster is held, C's {@code mon->m_timed[MON_TMD_HOLD] != 0},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn. Only
     * meaningful while {@link #monExists} is {@code true}.
     *
     * <p>Field monHeld coded on 260929, commented in full on 260929.
     */
    private static boolean monHeld;

    /**
     * Whether the player is hallucinating, C's {@code player->timed[TMD_IMAGE] != 0},
     * written each time an {@code EVENT_MONSTERHEALTH} message is routed and read whenever the sidebar's monster health bar is drawn.
     *
     * <p>Field playerTmdImage coded on 260929, commented in full on 260929.
     */
    private static boolean playerTmdImage;

    /**
     * The player's speed, C's {@code player->state.speed} - 110 is normal. Written each time an
     * {@code EVENT_PLAYERSPEED} message is routed and read whenever the sidebar's speed row is
     * drawn. Starts at {@code 0}, which reads as very slow, until the first message arrives.
     *
     * <p>Field playerSpeed coded on 260929, commented in full on 260929.
     */
    private static int playerSpeed;

    /**
     * Whether speed is shown as a multiplier rather than a signed offset, C's
     * {@code OPT(player, effective_speed)}. Written each time an {@code EVENT_PLAYERSPEED} message
     * is routed and read whenever the sidebar's speed row is drawn.
     *
     * <p>Field playerEffectiveSpeed coded on 260929, commented in full on 260929.
     */
    private static boolean playerEffectiveSpeed;

    /**
     * The energy gained per game turn at the player's speed, C's
     * {@code extract_energy[player->state.speed]}. Written each time an {@code EVENT_PLAYERSPEED}
     * message is routed and read whenever the speed row is drawn with the effective-speed option
     * on; the numerator of the multiplier.
     *
     * <p>Field energy coded on 260929, commented in full on 260929.
     */
    private static int energy;

    /**
     * The energy gained per game turn at normal speed, C's {@code extract_energy[110]}. Written
     * each time an {@code EVENT_PLAYERSPEED} message is routed and read whenever the speed row is
     * drawn with the effective-speed option on; the denominator of the multiplier. Starts at
     * {@code 0}, so it must not be used to divide before the first message arrives.
     *
     * <p>Field energyNormal coded on 260929, commented in full on 260929.
     */
    private static int energyNormal;

    /**
     * The player's dungeon level, C's {@code player->depth} - {@code 0} is the town. Written each
     * time an {@code EVENT_DUNGEONLEVEL} message is routed and read whenever the sidebar's depth
     * row is drawn. Starts at {@code 0}, so before the first message the row reads "Town".
     *
     * <p>Field playerDepth coded on 260929, commented in full on 260929.
     */
    private static int playerDepth;

    /**
     * Read the dungeon level last written by {@link #setPlayerDepth(int)}, for the sidebar's depth
     * row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code fmt_depth} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getPlayerDepth coded on 260929, commented in full on 260929.
     *
     * @return the dungeon level, in levels rather than feet; {@code 0} in the town
     */
    public static int getPlayerDepth() {
        return playerDepth;
    }

    /**
     * Write the dungeon level. Package-private, so only {@link RedrawRouter#setPlayerDepth} - the
     * only class in this package today - can write the model directly.
     *
     * <p>Method setPlayerDepth coded on 260929, commented in full on 260929.
     *
     * @param playerDepth the player's dungeon level, C's {@code player->depth}
     */
    static void setPlayerDepth(int playerDepth) {
        SidebarModel.playerDepth = playerDepth;
    }

    /**
     * Read whether speed is shown as a multiplier, last written by
     * {@link #setPlayerEffectiveSpeed(boolean)}, for the sidebar's speed row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_speed_aux} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getPlayerEffectiveSpeed coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the effective-speed option is on, C's
     * {@code OPT(player, effective_speed)}
     */
    public static boolean getPlayerEffectiveSpeed() {
        return playerEffectiveSpeed;
    }

    /**
     * Write whether speed is shown as a multiplier. Package-private, so only
     * {@link RedrawRouter#setPlayerSpeed} - the only class in this package today - can write the
     * model directly.
     *
     * <p>Method setPlayerEffectiveSpeed coded on 260929, commented in full on 260929.
     *
     * @param playerEffectiveSpeed whether the effective-speed option is on
     */
    static void setPlayerEffectiveSpeed(boolean playerEffectiveSpeed) {
        SidebarModel.playerEffectiveSpeed = playerEffectiveSpeed;
    }

    /**
     * Read the energy per game turn at the player's speed, last written by
     * {@link #setEnergy(int)}, for the multiplier form of the sidebar's speed row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_speed_aux} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getEnergy coded on 260929, commented in full on 260929.
     *
     * @return C's {@code extract_energy[player->state.speed]}
     */
    public static int getEnergy() {
        return energy;
    }

    /**
     * Write the energy per game turn at the player's speed. Package-private, so only
     * {@link RedrawRouter#setPlayerSpeed} can write the model directly.
     *
     * <p>Method setEnergy coded on 260929, commented in full on 260929.
     *
     * @param energy C's {@code extract_energy[player->state.speed]}
     */
    static void setEnergy(int energy) {
        SidebarModel.energy = energy;
    }

    /**
     * Read the energy per game turn at normal speed, last written by
     * {@link #setEnergyNormal(int)}, for the multiplier form of the sidebar's speed row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_speed_aux} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getEnergyNormal coded on 260929, commented in full on 260929.
     *
     * @return C's {@code extract_energy[110]}; {@code 0} until the first speed message arrives
     */
    public static int getEnergyNormal() {
        return energyNormal;
    }

    /**
     * Write the energy per game turn at normal speed. Package-private, so only
     * {@link RedrawRouter#setPlayerSpeed} can write the model directly.
     *
     * <p>Method setEnergyNormal coded on 260929, commented in full on 260929.
     *
     * @param energyNormal C's {@code extract_energy[110]}
     */
    static void setEnergyNormal(int energyNormal) {
        SidebarModel.energyNormal = energyNormal;
    }

    /**
     * Read the player's speed last written by {@link #setPlayerSpeed(int)}, for the sidebar's speed
     * row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_speed_aux} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getPlayerSpeed coded on 260929, commented in full on 260929.
     *
     * @return C's {@code player->state.speed}; 110 is normal
     */
    public static int getPlayerSpeed() {
        return playerSpeed;
    }

    /**
     * Write the player's speed. Package-private, so only {@link RedrawRouter#setPlayerSpeed} can
     * write the model directly.
     *
     * <p>Method setPlayerSpeed coded on 260929, commented in full on 260929.
     *
     * @param playerSpeed C's {@code player->state.speed}
     */
    static void setPlayerSpeed(int playerSpeed) {
        SidebarModel.playerSpeed = playerSpeed;
    }

    /**
     * Read whether the player is hallucinating last written by {@link #setPlayerTmdImage(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isPlayerTmdImage coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the player is hallucinating, C's {@code player->timed[TMD_IMAGE] != 0}
     */
    public static boolean isPlayerTmdImage() {
        return playerTmdImage;
    }

    /**
     * Write whether the player is hallucinating. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setPlayerTmdImage coded on 260929, commented in full on 260929.
     *
     * @param playerTmdImage whether the player is hallucinating, C's {@code player->timed[TMD_IMAGE] != 0}
     */
    static void setPlayerTmdImage(boolean playerTmdImage) {
        SidebarModel.playerTmdImage = playerTmdImage;
    }

    /**
     * Read whether the tracked monster is held last written by {@link #setMonHeld(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isMonHeld coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the tracked monster is held, C's {@code m_timed[MON_TMD_HOLD] != 0}
     */
    public static boolean isMonHeld() {
        return monHeld;
    }

    /**
     * Write whether the tracked monster is held. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonHeld coded on 260929, commented in full on 260929.
     *
     * @param monHeld whether the tracked monster is held, C's {@code m_timed[MON_TMD_HOLD] != 0}
     */
    static void setMonHeld(boolean monHeld) {
        SidebarModel.monHeld = monHeld;
    }

    /**
     * Read whether the tracked monster is asleep last written by {@link #setMonSlept(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isMonSlept coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the tracked monster is asleep, C's {@code m_timed[MON_TMD_SLEEP] != 0}
     */
    public static boolean isMonSlept() {
        return monSlept;
    }

    /**
     * Write whether the tracked monster is asleep. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonSlept coded on 260929, commented in full on 260929.
     *
     * @param monSlept whether the tracked monster is asleep, C's {@code m_timed[MON_TMD_SLEEP] != 0}
     */
    static void setMonSlept(boolean monSlept) {
        SidebarModel.monSlept = monSlept;
    }

    /**
     * Read whether the tracked monster is stunned last written by {@link #setMonStunned(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isMonStunned coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the tracked monster is stunned, C's {@code m_timed[MON_TMD_STUN] != 0}
     */
    public static boolean isMonStunned() {
        return monStun;
    }

    /**
     * Write whether the tracked monster is stunned. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonStunned coded on 260929, commented in full on 260929.
     *
     * @param monStunned whether the tracked monster is stunned, C's {@code m_timed[MON_TMD_STUN] != 0}
     */
    static void setMonStunned(boolean monStunned) {
        SidebarModel.monStun = monStunned;
    }

    /**
     * Read whether the tracked monster is confused last written by {@link #setMonConf(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isMonConf coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the tracked monster is confused, C's {@code m_timed[MON_TMD_CONF] != 0}
     */
    public static boolean isMonConf() {
        return monConf;
    }

    /**
     * Write whether the tracked monster is confused. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonConf coded on 260929, commented in full on 260929.
     *
     * @param monConf whether the tracked monster is confused, C's {@code m_timed[MON_TMD_CONF] != 0}
     */
    static void setMonConf(boolean monConf) {
        SidebarModel.monConf = monConf;
    }

    /**
     * Read whether the tracked monster is commanded last written by {@link #setMonCommand(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isMonCommand coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the tracked monster is commanded, C's {@code m_timed[MON_TMD_COMMAND] != 0}
     */
    public static boolean isMonCommand() {
        return monCommand;
    }

    /**
     * Write whether the tracked monster is commanded. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonCommand coded on 260929, commented in full on 260929.
     *
     * @param monCommand whether the tracked monster is commanded, C's {@code m_timed[MON_TMD_COMMAND] != 0}
     */
    static void setMonCommand(boolean monCommand) {
        SidebarModel.monCommand = monCommand;
    }

    /**
     * Read whether the tracked monster is disenchanted last written by {@link #setMonDisen(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isMonDisen coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the tracked monster is disenchanted, C's {@code m_timed[MON_TMD_DISEN] != 0}
     */
    public static boolean isMonDisen() {
        return monDisen;
    }

    /**
     * Write whether the tracked monster is disenchanted. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonDisen coded on 260929, commented in full on 260929.
     *
     * @param monDisen whether the tracked monster is disenchanted, C's {@code m_timed[MON_TMD_DISEN] != 0}
     */
    static void setMonDisen(boolean monDisen) {
        SidebarModel.monDisen = monDisen;
    }

    /**
     * Read whether the tracked monster is afraid last written by {@link #setMonFeared(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isMonFeared coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the tracked monster is afraid, C's {@code m_timed[MON_TMD_FEAR] != 0}
     */
    public static boolean isMonFeared() {
        return monFeared;
    }

    /**
     * Write whether the tracked monster is afraid. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonFeared coded on 260929, commented in full on 260929.
     *
     * @param monFeared whether the tracked monster is afraid, C's {@code m_timed[MON_TMD_FEAR] != 0}
     */
    static void setMonFeared(boolean monFeared) {
        SidebarModel.monFeared = monFeared;
    }

    /**
     * Read whether the player can see the tracked monster last written by {@link #setMonVisible(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method isMonVisible coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when the player can see the tracked monster, C's {@code monster_is_visible(mon)}
     */
    public static boolean isMonVisible() {
        return monVisible;
    }

    /**
     * Write whether the player can see the tracked monster. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonVisible coded on 260929, commented in full on 260929.
     *
     * @param monVisible whether the player can see the tracked monster, C's {@code monster_is_visible(mon)}
     */
    static void setMonVisible(boolean monVisible) {
        SidebarModel.monVisible = monVisible;
    }

    /**
     * Read whether a monster is being tracked last written by {@link #setMonExists(boolean)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method monExists coded on 260929, commented in full on 260929.
     *
     * @return {@code true} when a monster is being tracked, C's {@code player->upkeep->health_who != NULL}
     */
    public static boolean monExists() {
        return monExists;
    }

    /**
     * Write whether a monster is being tracked. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonExists coded on 260929, commented in full on 260929.
     *
     * @param monExists whether a monster is being tracked, C's {@code player->upkeep->health_who != NULL}
     */
    static void setMonExists(boolean monExists) {
        SidebarModel.monExists = monExists;
    }

    /**
     * Read the tracked monster's maximum hit points last written by {@link #setMonMaxHealth(int)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method getMonMaxHealth coded on 260929, commented in full on 260929.
     *
     * @return the tracked monster's maximum hit points, C's {@code mon->maxhp}
     */
    public static int getMonMaxHealth() {
        return monMaxHealth;
    }

    /**
     * Write the tracked monster's maximum hit points. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonMaxHealth coded on 260929, commented in full on 260929.
     *
     * @param monMaxHealth the tracked monster's maximum hit points, C's {@code mon->maxhp}
     */
    static void setMonMaxHealth(int monMaxHealth) {
        SidebarModel.monMaxHealth = monMaxHealth;
    }

    /**
     * Read the tracked monster's current hit points last written by {@link #setMonHealth(int)}, for the sidebar's monster health
     * bar - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_health_aux}/{@code monster_health_attr} ({@code [C] ui-display.c}) is today's
     * only reader.
     *
     * <p>Method getMonHealth coded on 260929, commented in full on 260929.
     *
     * @return the tracked monster's current hit points, C's {@code mon->hp}
     */
    public static int getMonHealth() {
        return monHealth;
    }

    /**
     * Write the tracked monster's current hit points. Package-private, so only
     * {@link RedrawRouter#setMonsterHealth} - the only class in this package today - can write
     * the model directly.
     *
     * <p>Method setMonHealth coded on 260929, commented in full on 260929.
     *
     * @param monHealth the tracked monster's current hit points, C's {@code mon->hp}
     */
    static void setMonHealth(int monHealth) {
        SidebarModel.monHealth = monHealth;
    }

    /**
     * Read the first-spell level last written by {@link #setFirstSpell(int)}, for the sidebar's SP
     * row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_sp} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getFirstSpell coded on 260928, commented in full on 260928.
     *
     * @return the level of the class's first spell, C's {@code player->class->magic.spell_first}
     */
    public static int getFirstSpell() {
        return firstSpell;
    }

    /**
     * Write the first-spell level. Package-private, so only {@link RedrawRouter#setSP} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setFirstSpell coded on 260928, commented in full on 260928.
     *
     * @param firstSpell the level of the class's first spell, C's
     *                   {@code player->class->magic.spell_first}
     */
    static void setFirstSpell(int firstSpell) {
        SidebarModel.firstSpell = firstSpell;
    }

    /**
     * Read the has-spells flag last written by {@link #setMagic(boolean)}, for the sidebar's SP
     * row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_sp} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method hasMagic coded on 260928, commented in full on 260928.
     *
     * @return {@code true} when the class has any spells, C's
     * {@code player->class->magic.total_spells != 0}
     */
    public static boolean hasMagic() {
        return magic;
    }

    /**
     * Write the has-spells flag. Package-private, so only {@link RedrawRouter#setSP} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setMagic coded on 260928, commented in full on 260928.
     *
     * @param magic {@code true} when the class has any spells, C's
     *              {@code player->class->magic.total_spells != 0}
     */
    static void setMagic(boolean magic) {
        SidebarModel.magic = magic;
    }

    /**
     * Read the hit-point warning option last written by {@link #setPlayerOptHPWarn(int)}, for the
     * sidebar's HP and SP colours - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s
     * ports of C's {@code player_hp_attr} and {@code player_sp_attr} ({@code [C] player.c}) are
     * today's only readers.
     *
     * <p>Method getPlayerOptHPWarn coded on 260928, commented in full on 260928.
     *
     * @return the warning threshold in tenths, C's {@code player->opts.hitpoint_warn}
     */
    public static int getPlayerOptHPWarn() {
        return playerOptHPWarn;
    }

    /**
     * Write the hit-point warning option. Package-private, so only {@link RedrawRouter#setHP} -
     * the only class in this package today - can write the model directly.
     *
     * <p>Method setPlayerOptHPWarn coded on 260928, commented in full on 260928.
     *
     * @param playerOptHPWarn the warning threshold in tenths, C's {@code player->opts.hitpoint_warn}
     */
    static void setPlayerOptHPWarn(int playerOptHPWarn) {
        SidebarModel.playerOptHPWarn = playerOptHPWarn;
    }

    /**
     * Read the armour class last written by {@link #setAc(int)}, for the sidebar's AC row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtAc(int, int)}'s port
     * of C's {@code prt_ac} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getAc coded on 260927, commented in full on 260928.
     *
     * @return the player's armour class, C's {@code known_state.ac + known_state.to_a}
     */
    public static int getAc() {
        return ac;
    }

    /**
     * Write the armour class. Package-private, so only {@link RedrawRouter#setAC} - the only class
     * in this package today - can write the model directly.
     *
     * <p>Method setAc coded on 260927, commented in full on 260928.
     *
     * @param ac the player's armour class, C's {@code known_state.ac + known_state.to_a}
     */
    static void setAc(int ac) {
        SidebarModel.ac = ac;
    }

    /**
     * Read a stat's recorded maximum last written by {@link #setMaxStat(int, int)}, for the
     * sidebar's stat rows -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtStat(int, int, int)}'s
     * port of C's {@code prt_stat} ({@code [C] ui-display.c}) is today's only reader. Returns
     * {@code 0} and logs an error for an {@code index} outside the five stats, rather than
     * throwing.
     *
     * <p>Method getMaxStat coded on 260927, commented in full on 260928.
     *
     * @param index the stat to read, {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()}
     * @return the stat's recorded maximum, C's {@code player->stat_max[index]}, or {@code 0} for
     * an out-of-range index
     */
    public static int getMaxStat(int index) {
        if (index < 0 || index >= maxStats.length) {
            logger.error("Invalid stat index: " + index);
            return 0;
        }
        return maxStats[index];
    }

    /**
     * Read a stat's current value last written by {@link #setCurrentStat(int, int)}, for the
     * sidebar's stat rows -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtStat(int, int, int)}'s
     * port of C's {@code prt_stat} ({@code [C] ui-display.c}) is today's only reader. Returns
     * {@code 0} and logs an error for an {@code index} outside the five stats, rather than
     * throwing.
     *
     * <p>Method getCurrentStat coded on 260927, commented in full on 260928.
     *
     * @param index the stat to read, {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()}
     * @return the stat's current value, C's {@code player->stat_cur[index]}, or {@code 0} for an
     * out-of-range index
     */
    public static int getCurrentStat(int index) {
        if (index < 0 || index >= currentStats.length) {
            logger.error("Invalid stat index: " + index);
            return 0;
        }
        return currentStats[index];
    }

    /**
     * Read a stat's displayed value last written by {@link #setUseStat(int, int)}, for the
     * sidebar's stat rows -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtStat(int, int, int)}'s
     * port of C's {@code prt_stat} ({@code [C] ui-display.c}) is today's only reader. Returns
     * {@code 0} and logs an error for an {@code index} outside the five stats, rather than
     * throwing.
     *
     * <p>Method getUseStat coded on 260927, commented in full on 260928.
     *
     * @param index the stat to read, {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()}
     * @return the stat's displayed value, C's {@code player->state.stat_use[index]}, or {@code 0}
     * for an out-of-range index
     */
    public static int getUseStat(int index) {
        if (index < 0 || index >= useStats.length) {
            logger.error("Invalid stat index: " + index);
            return 0;
        }
        return useStats[index];
    }

    /**
     * Write a stat's displayed value. Package-private, so only {@link RedrawRouter#setStats} - the
     * only class in this package today - can write the model directly. Logs an error and leaves
     * the array untouched for an {@code index} outside the five stats, rather than throwing.
     *
     * <p>Method setUseStat coded on 260927, commented in full on 260928.
     *
     * @param index the stat being written, {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()}
     * @param value the stat's displayed value, C's {@code player->state.stat_use[index]}
     */
    static void setUseStat(int index, int value) {
        if (index < 0 || index >= useStats.length) {
            logger.error("Invalid stat index: " + index);
        } else {
            useStats[index] = value;
        }
    }

    /**
     * Write a stat's current value. Package-private, so only {@link RedrawRouter#setStats} - the
     * only class in this package today - can write the model directly. Logs an error and leaves
     * the array untouched for an {@code index} outside the five stats, rather than throwing.
     *
     * <p>Method setCurrentStat coded on 260927, commented in full on 260928.
     *
     * @param index the stat being written, {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()}
     * @param value the stat's current value, C's {@code player->stat_cur[index]}
     */
    static void setCurrentStat(int index, int value) {
        if (index < 0 || index >= currentStats.length) {
            logger.error("Invalid stat index: " + index);
        } else {
            currentStats[index] = value;
        }
    }

    /**
     * Write a stat's recorded maximum. Package-private, so only {@link RedrawRouter#setStats} -
     * the only class in this package today - can write the model directly. Logs an error and
     * leaves the array untouched for an {@code index} outside the five stats, rather than
     * throwing.
     *
     * <p>Method setMaxStat coded on 260927, commented in full on 260928.
     *
     * @param index the stat being written, {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()}
     * @param value the stat's recorded maximum, C's {@code player->stat_max[index]}
     */
    static void setMaxStat(int index, int value) {
        if (index < 0 || index >= maxStats.length) {
            logger.error("Invalid stat index: " + index);
        } else {
            maxStats[index] = value;
        }
    }

    /**
     * Read the equippy row last written by {@link #setEquippyString(AngbandDisplayCharacter[])} -
     * {@code HandlersHolder.prtEquippy}'s port of C's {@code prt_equippy} ({@code [C]
     * ui-display.c}) is today's only reader.
     *
     * <p>Method getEquippyString coded on 260927, commented in full on 260928.
     *
     * @return one {@link AngbandDisplayCharacter} per equipment slot, in slot order
     */
    public static AngbandDisplayCharacter[] getEquippyString() {
        return equipString;
    }

    /**
     * Write the equippy row. Package-private, so only {@link RedrawRouter#setEquippy} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setEquippyString coded on 260927, commented in full on 260928.
     *
     * @param equipString one {@link AngbandDisplayCharacter} per equipment slot, in slot order
     */
    static void setEquippyString(AngbandDisplayCharacter[] equipString) {
        SidebarModel.equipString = equipString;
    }

    /**
     * Read the gold total last written by {@link #setGold(long)}, for the sidebar's gold row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_gold} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getGold coded on 260927, commented in full on 260928.
     *
     * @return the player's current gold total, C's {@code player->au}
     */
    public static long getGold() {
        return gold;
    }

    /**
     * Write the gold total. Package-private, so only {@link RedrawRouter#setGold} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setGold coded on 260927, commented in full on 260928.
     *
     * @param gold the player's current gold total, C's {@code player->au}
     */
    static void setGold(long gold) {
        SidebarModel.gold = gold;
    }

    /**
     * Read the maximum experience last written by {@link #setMaxXp(long)}, for the sidebar's
     * experience row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_exp} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getMaxXp coded on 260927, commented in full on 260928.
     *
     * @return the player's highest experience total yet held, C's {@code player->max_exp}
     */
    public static long getMaxXp() {
        return maxXp;
    }

    /**
     * Write the maximum experience. Package-private, so only {@link RedrawRouter#setExperience} -
     * the only class in this package today - can write the model directly.
     *
     * <p>Method setMaxXp coded on 260927, commented in full on 260928.
     *
     * @param maxXp the player's highest experience total yet held, C's {@code player->max_exp}
     */
    static void setMaxXp(long maxXp) {
        SidebarModel.maxXp = maxXp;
    }

    /**
     * Read the displayed experience figure last written by {@link #setXpToLevel(long)}, for the
     * sidebar's experience row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_exp} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getXpToLevel coded on 260927, commented in full on 260928.
     *
     * @return the experience needed to reach the next level, or the running total at level fifty,
     * C's local {@code xp} in {@code prt_exp}
     */
    public static long getXpToLevel() {
        return xpToLevel;
    }

    /**
     * Write the displayed experience figure. Package-private, so only
     * {@link RedrawRouter#setExperience} - the only class in this package today - can write the
     * model directly.
     *
     * <p>Method setXpToLevel coded on 260927, commented in full on 260928.
     *
     * @param xpToLevel the experience needed to reach the next level, or the running total at
     *                  level fifty
     */
    static void setXpToLevel(long xpToLevel) {
        SidebarModel.xpToLevel = xpToLevel;
    }

    /**
     * Read the current experience last written by {@link #setExperience(long)}, for the sidebar's
     * experience row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_exp} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getExperience coded on 260927, commented in full on 260928.
     *
     * @return the player's current experience total, C's {@code player->exp}
     */
    public static long getExperience() {
        return experience;
    }

    /**
     * Write the current experience. Package-private, so only {@link RedrawRouter#setExperience} -
     * the only class in this package today - can write the model directly.
     *
     * <p>Method setExperience coded on 260927, commented in full on 260928.
     *
     * @param experience the player's current experience total, C's {@code player->exp}
     */
    static void setExperience(long experience) {
        SidebarModel.experience = experience;
    }

    /**
     * Read the maximum level last written by {@link #setMaxLevel(int)}, for the sidebar's level row
     * - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_level} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getMaxLevel coded on 260927, commented in full on 260927.
     *
     * @return the player's highest character level yet attained, C's {@code player->max_lev}
     */
    public static int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Write the maximum level. Package-private, so only {@link RedrawRouter#setPlayerLevel} - the
     * only class in this package today - can write the model directly.
     *
     * <p>Method setMaxLevel coded on 260927, commented in full on 260927.
     *
     * @param maxLevel the player's highest character level yet attained, C's {@code player->max_lev}
     */
    static void setMaxLevel(int maxLevel) {
        SidebarModel.maxLevel = maxLevel;
    }

    /**
     * Read the current level last written by {@link #setLevel(int)}, for the sidebar's level row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_level} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getLevel coded on 260927, commented in full on 260927.
     *
     * @return the player's current character level, C's {@code player->lev}
     */
    public static int getLevel() {
        return level;
    }

    /**
     * Write the current level. Package-private, so only {@link RedrawRouter#setPlayerLevel} - the
     * only class in this package today - can write the model directly.
     *
     * <p>Method setLevel coded on 260927, commented in full on 260927.
     *
     * @param level the player's current character level, C's {@code player->lev}
     */
    static void setLevel(int level) {
        SidebarModel.level = level;
    }

    /**
     * Read the shape name last written by {@link #setShapeName(String)}, for the sidebar's title
     * row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code fmt_title} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getShapeName coded on 260927, commented in full on 260927.
     *
     * @return the name of the player's current shape, C's {@code player->shape->name}
     */
    public static String getShapeName() {
        return shapeName;
    }

    /**
     * Write the shape name. Package-private, so only {@link RedrawRouter#setTitle} - the only class
     * in this package today - can write the model directly.
     *
     * <p>Method setShapeName coded on 260927, commented in full on 260927.
     *
     * @param shapeName the name of the player's current shape, C's {@code player->shape->name}
     */
    static void setShapeName(String shapeName) {
        SidebarModel.shapeName = shapeName;
    }

    /**
     * Read the total-winner flag last written by {@link #setTotalWinner(boolean)}, for the
     * sidebar's title row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s
     * port of C's {@code fmt_title} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method isTotalWinner coded on 260927, commented in full on 260927.
     *
     * @return {@code true} once the player has won the game, C's {@code player->total_winner}
     */
    public static boolean isTotalWinner() {
        return isTotalWinner;
    }

    /**
     * Write the total-winner flag. Package-private, so only {@link RedrawRouter#setTitle} - the
     * only class in this package today - can write the model directly.
     *
     * <p>Method setTotalWinner coded on 260927, commented in full on 260927.
     *
     * @param totalWinner {@code true} once the player has won the game, C's
     *                    {@code player->total_winner}
     */
    static void setTotalWinner(boolean totalWinner) {
        isTotalWinner = totalWinner;
    }

    /**
     * Read the wizard-mode flag last written by {@link #setWizard(boolean)}, for the sidebar's title
     * row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code fmt_title} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method isWizard coded on 260927, commented in full on 260927.
     *
     * @return {@code true} while the player is in wizard mode, C's {@code player->wizard}
     */
    public static boolean isWizard() {
        return wizard;
    }
    
    /**
     * Write the wizard-mode flag. Package-private, so only {@link RedrawRouter#setTitle} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setWizard coded on 260927, commented in full on 260927.
     *
     * @param wizard {@code true} while the player is in wizard mode, C's {@code player->wizard}
     */
    static void setWizard(boolean wizard) {
        SidebarModel.wizard = wizard;
    }

    /**
     * Read the current hit-point value last written by {@link #setCurrentHP(int)}, for the
     * sidebar's HP row — {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getCurrentHP coded on 260926, commented in full on 260926.
     *
     * @return the player's current hit points, C's {@code player->chp}
     */
    public static int getCurrentHP() {
        return currentHP;
    }

    /**
     * Write the current hit-point value. Package-private, so only {@link RedrawRouter#setHP} —
     * the only class in this package today — can write the model directly.
     *
     * <p>Method setCurrentHP coded on 260926, commented in full on 260926.
     *
     * @param currentHP the player's current hit points, C's {@code player->chp}
     */
    static void setCurrentHP(int currentHP) {
        SidebarModel.currentHP = currentHP;
    }

    /**
     * Read the maximum hit-point value last written by {@link #setMaxHP(int)}, for the sidebar's
     * HP row — {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getMaxHP coded on 260926, commented in full on 260926.
     *
     * @return the player's maximum hit points, C's {@code player->mhp}
     */
    public static int getMaxHP() {
        return maxHP;
    }

    /**
     * Write the maximum hit-point value. Package-private, so only {@link RedrawRouter#setHP} —
     * the only class in this package today — can write the model directly.
     *
     * <p>Method setMaxHP coded on 260926, commented in full on 260926.
     *
     * @param maxHP the player's maximum hit points, C's {@code player->mhp}
     */
    static void setMaxHP(int maxHP) {
        SidebarModel.maxHP = maxHP;
    }

    /**
     * Read the current spell-point value last written by {@link #setCurrentSP(int)}, for the
     * sidebar's SP row — {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getCurrentSP coded on 260926, commented in full on 260926.
     *
     * @return the player's current spell points, C's {@code player->csp}
     */
    public static int getCurrentSP() {
        return currentSP;
    }

    /**
     * Write the current spell-point value. Package-private, so only {@link RedrawRouter#setSP} —
     * the only class in this package today — can write the model directly.
     *
     * <p>Method setCurrentSP coded on 260926, commented in full on 260926.
     *
     * @param currentSP the player's current spell points, C's {@code player->csp}
     */
    static void setCurrentSP(int currentSP) {
        SidebarModel.currentSP = currentSP;
    }

    /**
     * Read the maximum spell-point value last written by {@link #setMaxSP(int)}, for the sidebar's
     * SP row — {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getMaxSP coded on 260926, commented in full on 260926.
     *
     * @return the player's maximum spell points, C's {@code player->msp}
     */
    public static int getMaxSP() {
        return maxSP;
    }

    /**
     * Write the maximum spell-point value. Package-private, so only {@link RedrawRouter#setSP} —
     * the only class in this package today — can write the model directly.
     *
     * <p>Method setMaxSP coded on 260926, commented in full on 260926.
     *
     * @param maxSP the player's maximum spell points, C's {@code player->msp}
     */
    static void setMaxSP(int maxSP) {
        SidebarModel.maxSP = maxSP;
    }

    /**
     * Read the class name last written by {@link #setClassName(String)}, for the character sheet's
     * Class row - {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getClassName coded before 260926, commented in full on 260926.
     *
     * @return the player's class name, C's {@code player->class->name}
     */
    public static String getClassName() {
        return className;
    }

    /**
     * Write the class name. Package-private, so only {@link RedrawRouter#setRaceClass} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setClassName coded before 260926, commented in full on 260926.
     *
     * @param className the player's class name, C's {@code player->class->name}
     */
    static void setClassName(String className) {
        SidebarModel.className = className;
    }

    /**
     * Read the full name last written by {@link #setName(String)}, for the character sheet's Name
     * row - {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getName coded before 260926, commented in full on 260926.
     *
     * @return the player's full name, C's {@code player->full_name}
     */
    public static String getName() {
        return name;
    }

    /**
     * Write the full name. Package-private, so only {@link RedrawRouter#setName} - the only class
     * in this package today - can write the model directly.
     *
     * <p>Method setName coded before 260926, commented in full on 260926.
     *
     * @param name the player's full name, C's {@code player->full_name}
     */
    static void setName(String name) {
        SidebarModel.name = name;
    }

    /**
     * Read the race name last written by {@link #setRaceName(String)}, for the character sheet's
     * Race row - {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getRaceName coded before 260926, commented in full on 260926.
     *
     * @return the player's race name, C's {@code player->race->name}
     */
    public static String getRaceName() {
        return raceName;
    }

    /**
     * Write the race name. Package-private, so only {@link RedrawRouter#setRaceClass} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setRaceName coded before 260926, commented in full on 260926.
     *
     * @param raceName the player's race name, C's {@code player->race->name}
     */
    static void setRaceName(String raceName) {
        SidebarModel.raceName = raceName;
    }

    /**
     * Read the title last written by {@link #setTitle(String)}, for the character sheet's Title
     * row - {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getTitle coded before 260926, commented in full on 260926.
     *
     * @return the player's title, C's {@code show_title()}
     */
    public static String getTitle() {
        return title;
    }

    /**
     * Write the title. Package-private, so only {@link RedrawRouter#setTitle} - the only class in
     * this package today - can write the model directly.
     *
     * <p>Method setTitle coded before 260926, commented in full on 260926.
     *
     * @param title the player's title, C's {@code show_title()}
     */
    static void setTitle(String title) {
        SidebarModel.title = title;
    }

    /**
     * Reads the shapechanged flag last written by {@link #setPlayerIsShapechanged(boolean)}, for
     * the sidebar's race and class rows —
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtRace(int, int)} and
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtClass(int, int)},
     * both ports of C's {@code prt_race}/{@code prt_class} ({@code [C] ui-display.c}), are today's
     * readers.
     *
     * <p>Method isPlayerIsShapechanged coded on 260927, commented in full on 260927.
     *
     * @return {@code true} when the player is currently shapechanged, C's
     * {@code player_is_shapechanged(player)}
     */
    public static boolean isPlayerIsShapechanged() {
        return playerIsShapechanged;
    }

    /**
     * Writes the shapechanged flag. Package-private, so only {@link RedrawRouter#setRaceClass} -
     * the only class in this package today - can write the model directly.
     *
     * <p>Method setPlayerIsShapechanged coded on 260927, commented in full on 260927.
     *
     * @param playerIsShapechanged whether the player is currently shapechanged, C's
     *                             {@code player_is_shapechanged(player)}
     */
    static void setPlayerIsShapechanged(boolean playerIsShapechanged) {
        SidebarModel.playerIsShapechanged = playerIsShapechanged;
    }
}
