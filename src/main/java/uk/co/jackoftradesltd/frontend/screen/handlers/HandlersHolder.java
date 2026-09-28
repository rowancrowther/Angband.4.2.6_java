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

package uk.co.jackoftradesltd.frontend.screen.handlers;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.screen.Term;
import uk.co.jackoftradesltd.frontend.screen.TermData;
import uk.co.jackoftradesltd.frontend.screen.enums.Sidebar;
import uk.co.jackoftradesltd.frontend.ui.SidebarModel;
import uk.co.jackoftradesltd.frontend.ui.UIPlayer;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * The Java port of C's {@code side_handlers[]} table ({@code [C] ui-display.c}) - the sidebar rows
 * that redraw themselves in response to a {@code game_event_type} flag, as opposed to the "short"
 * topbar path ({@code update_topbar}, {@code [C] ui-display.c}), which this class does not cover.
 * C's table lists roughly twenty rows; twelve are ported and registered so far -
 * {@code prt_race}, {@code prt_title}, {@code prt_class}, {@code prt_level}, {@code prt_exp},
 * {@code prt_gold}, {@code prt_equippy}, and the five stat rows {@code prt_str}, {@code prt_int},
 * {@code prt_wis}, {@code prt_dex} and {@code prt_con} - so {@link #sideHandlers} today holds
 * twelve {@link SideHandler}s, in the same order C's table lists them, with the rest joining one
 * at a time as each hook is ported.
 *
 * <p>Class HandlersHolder coded on 260927, commented in full on 260928.
 *
 * @author Rowan Crowther
 */
public class HandlersHolder {
    /**
     * The Java equivalent of C's {@code side_handlers[]} array - one {@link SideHandler} per row
     * that has been ported, built once by the static initialiser calling {@link #initHandlers()}.
     *
     * <p>Field sideHandlers coded on 260927, commented in full on 260927.
     */
    private static List<SideHandler> sideHandlers;

    /**
     * The terminal this holder's hooks draw to, written by {@link #setTermData(TermData)}. Stands
     * in for C's implicit target: C's {@code c_put_str} ({@code [C] z-term.c}) always writes to the
     * active {@code Term}, so the port has to be handed that reference explicitly instead.
     *
     * <p>Field term coded on 260927, commented in full on 260927.
     */
    private static Term term;

    static {
        sideHandlers = new ArrayList<>();
        initHandlers();
    }

    /**
     * Builds {@link #sideHandlers} - the port of C's {@code side_handlers[]} initializer
     * ({@code [C] ui-display.c}). Twelve rows are registered today, each at the same priority and
     * against the same {@code game_event_type} flag C's table gives it: {@code prt_race} at
     * {@code 19} against {@code EVENT_RACE_CLASS}, {@code prt_title} at {@code 18} against
     * {@code EVENT_PLAYERTITLE}, {@code prt_class} at {@code 22} against {@code EVENT_RACE_CLASS},
     * {@code prt_level} at {@code 10} against {@code EVENT_PLAYERLEVEL}, {@code prt_exp} at
     * {@code 16} against {@code EVENT_EXPERIENCE}, {@code prt_gold} at {@code 11} against
     * {@code EVENT_GOLD}, {@code prt_equippy} at {@code 17} against {@code EVENT_EQUIPMENT}, and
     * the five stat rows {@code prt_str}, {@code prt_int}, {@code prt_wis}, {@code prt_dex} and
     * {@code prt_con} at {@code 6}, {@code 5}, {@code 4}, {@code 3} and {@code 2} respectively,
     * all against {@code EVENT_STATS} - matching C's table order and figures exactly. The
     * remaining rows join this method as their own hooks are ported.
     *
     * <p>Method initHandlers coded on 260927, commented in full on 260928.
     */
    private static void initHandlers() {
        SideHandler handler = new SideHandler(HandlersHolder::prtRace, 19, GameEventType.EVENT_RACE_CLASS);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtTitle, 18, GameEventType.EVENT_PLAYERTITLE);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtClass, 22, GameEventType.EVENT_RACE_CLASS);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtLevel, 10, GameEventType.EVENT_PLAYERLEVEL);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtExp, 16, GameEventType.EVENT_EXPERIENCE);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtGold, 11, GameEventType.EVENT_GOLD);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtEquippy, 17, GameEventType.EVENT_EQUIPMENT);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtStr, 6, GameEventType.EVENT_STATS);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtInt, 5, GameEventType.EVENT_STATS);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtWis, 4, GameEventType.EVENT_STATS);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtDex, 3, GameEventType.EVENT_STATS);
        sideHandlers.add(handler);
        handler = new SideHandler(HandlersHolder::prtCon, 2, GameEventType.EVENT_STATS);
        sideHandlers.add(handler);
    }

    /**
     * Draws the sidebar's Strength row - the port of C's {@code prt_str} ({@code [C]
     * ui-display.c}), a thin wrapper calling {@link #prtStat} with {@code STAT_STR}'s index.
     *
     * <p>Method prtStr coded on 260927, commented in full on 260928.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtStr(int row, int col) {
        prtStat(0, row, col);
    }

    /**
     * Draws the sidebar's Intelligence row - the port of C's {@code prt_int} ({@code [C]
     * ui-display.c}), a thin wrapper calling {@link #prtStat} with {@code STAT_INT}'s index.
     *
     * <p>Method prtInt coded on 260927, commented in full on 260928.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtInt(int row, int col) {
        prtStat(1, row, col);
    }

    /**
     * Draws the sidebar's Wisdom row - the port of C's {@code prt_wis} ({@code [C]
     * ui-display.c}), a thin wrapper calling {@link #prtStat} with {@code STAT_WIS}'s index.
     *
     * <p>Method prtWis coded on 260927, commented in full on 260928.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtWis(int row, int col) {
        prtStat(2, row, col);
    }

    /**
     * Draws the sidebar's Dexterity row - the port of C's {@code prt_dex} ({@code [C]
     * ui-display.c}), a thin wrapper calling {@link #prtStat} with {@code STAT_DEX}'s index.
     *
     * <p>Method prtDex coded on 260927, commented in full on 260928.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtDex(int row, int col) {
        prtStat(3, row, col);
    }

    /**
     * Draws the sidebar's Constitution row - the port of C's {@code prt_con} ({@code [C]
     * ui-display.c}), a thin wrapper calling {@link #prtStat} with {@code STAT_CON}'s index.
     *
     * <p>Method prtCon coded on 260927, commented in full on 260928.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtCon(int row, int col) {
        prtStat(4, row, col);
    }

    /**
     * Draws one sidebar stat row - the port of C's {@code prt_stat} ({@code [C]
     * ui-display.c:158-176}), called once per stat by {@link #prtStr}, {@link #prtInt},
     * {@link #prtWis}, {@link #prtDex} and {@link #prtCon}.
     *
     * <p>Reads the stat's current and maximum values from {@link SidebarModel#getCurrentStat(int)}
     * and {@link SidebarModel#getMaxStat(int)} to choose between the drained and full
     * presentation, matching C's own {@code player->stat_cur[stat] < player->stat_max[stat]} test:
     * drained draws {@link uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry#statReducedNames}'s
     * entry in yellow, full draws
     * {@link uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry#statNames}'s entry in light
     * green - both label choices six columns left of the displayed value, matching C's
     * {@code col}/{@code col + 6} split. The displayed value itself is always
     * {@link SidebarModel#getUseStat(int)}, formatted through {@link UIPlayer#cnvStat(int, int)},
     * matching C's own {@code cnv_stat(player->state.stat_use[stat], ...)} call in both branches -
     * the drained test only picks the label and colour, not which value is shown.
     *
     * <p>A recorded maximum of {@code 18 + 100} (C's natural-maximum sentinel) draws an extra
     * {@code "!"} marker three columns after the label, matching C's
     * {@code player->stat_max[stat] == 18+100} check exactly.
     *
     * <p>Method prtStat coded on 260927, commented in full on 260928.
     *
     * @param statIndex the stat to draw, {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()}
     * @param row       the row to draw at
     * @param col       the column to draw at
     */
    private static void prtStat(int statIndex, int row, int col) {
        int currentStat = SidebarModel.getCurrentStat(statIndex);
        int maxStat = SidebarModel.getMaxStat(statIndex);

        String normal = UIRegistry.statNames[statIndex];
        String reduced = UIRegistry.statReducedNames[statIndex];

        int stat = SidebarModel.getUseStat(statIndex);

        String str = UIPlayer.cnvStat(stat, 32);

        if (currentStat < maxStat) {
            term.putStr(reduced, row, col);
            term.cPutStr(ColourEnum.COLOUR_YELLOW, str, row, col + 6);
        } else {
            term.putStr(normal, row, col);
            term.cPutStr(ColourEnum.COLOUR_LIGHT_GREEN, str, row, col + 6);
        }

        // Natural maximum
        if (SidebarModel.getMaxStat(statIndex) == 18 + 100) {
            term.putStr("!", row, col + 3);
        }
    }

    /**
     * Draws the sidebar's equippy row - the port of C's {@code prt_equippy} ({@code [C]
     * ui-display.c}), which shows one glyph per equipment slot, in slot order.
     *
     * <p>C reads {@code player->body} directly and calls {@code object_attr}/{@code object_char}
     * on each slot's object at draw time, falling back to a blank white space for an empty slot
     * or when graphics tiles wider or taller than one character cell are in use. On this side of
     * the boundary there is no {@code player} global to read from, so
     * {@code PlayerCalcs.redrawStuff}'s {@code PR_EQUIP} arm builds the whole glyph/colour array
     * up front - one {@link AngbandDisplayCharacter} per slot, via
     * {@code ItemObject.getItemObjectADC()} - and sends it as an {@code EVENT_EQUIPMENT} signal;
     * {@code RedrawRouter.setEquippy} unpacks it into {@link SidebarModel}, and this method only
     * reads and draws that array.
     *
     * <p><b>Outstanding:</b> C's tile-size fallback is not reproduced - the array
     * {@code PlayerCalcs.redrawStuff} builds blanks only an empty slot, not a real object under
     * oversized tiles. Tile width/height are UI-side state the core has no access to, so this is
     * deferred until tiles (as opposed to characters) are ported.
     *
     * <p>Method prtEquippy coded on 260927, commented in full on 260928.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtEquippy(int row, int col) {
        AngbandDisplayCharacter[] equipString = SidebarModel.getEquippyString();

        for (int index = 0; index < equipString.length; index++) {
            term.cPutStr(equipString[index].getAttributeColour(), Character.toString(equipString[index].getCharacter()),
                    row, col + index);
        }
    }

    /**
     * Draws the sidebar's gold row - the port of C's {@code prt_gold} ({@code [C]
     * ui-display.c}), which writes a fixed "AU " label followed by the player's current gold in a
     * nine-wide field.
     *
     * <p>Matches C exactly: {@code "AU "} at {@code col}, then {@code "%9d"} against
     * {@link SidebarModel#getGold()} written at {@code col + 3} - the port of C's
     * {@code strnfmt(tmp, sizeof(tmp), "%9ld", (long) player->au)} written after the same
     * three-character label. Unlike {@link #prtLevel} and {@link #prtExp}, the figure carries no
     * threshold test - it is always light green, matching C's single unconditional
     * {@code c_put_str(COLOUR_L_GREEN, tmp, row, col + 3)} call.
     *
     * <p>Method prtGold coded on 260927, commented in full on 260928.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtGold(int row, int col) {
        long gold = SidebarModel.getGold();

        term.putStr("AU ", row, col);
        String goldString = String.format("%9d", gold);
        term.cPutStr(ColourEnum.COLOUR_LIGHT_GREEN, goldString, row, col + 3);
    }

    /**
     * Draws the sidebar's experience row - the port of C's {@code prt_exp} ({@code [C]
     * ui-display.c}), which shows either the experience needed to reach the next level, or, once
     * the character has reached the level cap, the running total itself.
     *
     * <p>The displayed figure is not computed here: {@code PlayerCalcs.redrawStuff}'s
     * {@code PR_EXP} arm does C's {@code if (!lev50) xp = ...} branch before the signal is even
     * sent, so {@link SidebarModel#getXpToLevel()} already holds whichever of the two C's local
     * {@code xp} would - the experience to the next level below level fifty, the current total at
     * it - and this method only formats and writes it, matching C's {@code "%8ld"} with
     * {@code "%8d"}.
     *
     * <p>The label and colour test is independent of that figure and reads C's own comparison
     * exactly: {@code xp >= maxXp} against {@link SidebarModel#getExperience()} and
     * {@link SidebarModel#getMaxXp()} - the character's actual current and maximum experience,
     * C's {@code player->exp >= player->max_exp} - not against the number being printed.
     * "EXP"/light green once the character is at their personal best, "Exp"/yellow otherwise; the
     * label further swaps to "NXT"/"Nxt" until level fifty, following {@code lev50}.
     *
     * <p>Method prtExp coded on 260927, commented in full on 260928.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtExp(int row, int col) {
        boolean lev50 = (SidebarModel.getLevel() == 50);
        long xp = SidebarModel.getExperience();
        long xpToLevel = SidebarModel.getXpToLevel();
        long maxXp = SidebarModel.getMaxXp();

        String xpString = String.format("%8d", xpToLevel);

        if (xp >= maxXp) {
            term.putStr(lev50 ? "EXP" : "NXT", row, col);
            term.cPutStr(ColourEnum.COLOUR_LIGHT_GREEN, xpString, row, col + 4);
        } else {
            term.putStr(lev50 ? "Exp" : "Nxt", row, col);
            term.cPutStr(ColourEnum.COLOUR_YELLOW, xpString, row, col + 4);
        }
    }

    /**
     * Draws the sidebar's level row - the port of C's {@code prt_level} ({@code [C]
     * ui-display.c}), which formats the level into a six-wide field and colours it by whether the
     * character is currently at their best-ever level.
     *
     * <p>Matches C exactly: {@code "%6d"} against {@link SidebarModel#getLevel()}, "LEVEL "/light
     * green when the current level has reached the recorded maximum
     * ({@link SidebarModel#getMaxLevel()}), otherwise "Level "/yellow.
     *
     * <p>Method prtLevel coded on 260927, commented in full on 260927.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtLevel(int row, int col) {
        String levelString = String.format("%6d", SidebarModel.getLevel());

        if (SidebarModel.getLevel() >= SidebarModel.getMaxLevel()) {
            term.putStr("LEVEL ", row, col);
            term.cPutStr(ColourEnum.COLOUR_LIGHT_GREEN, levelString, row, col + 6);
        } else {
            term.putStr("Level ", row, col);
            term.cPutStr(ColourEnum.COLOUR_YELLOW, levelString, row, col + 6);
        }
    }

    /**
     * Draws the sidebar's title row - the port of C's {@code prt_title} ({@code [C]
     * ui-display.c}), which formats the title text with {@link #fmtTitle} and writes it through the
     * same 13-character field {@link #prtRace} and {@link #prtClass} use.
     *
     * <p>Method prtTitle coded on 260927, commented in full on 260927.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtTitle(int row, int col) {
        String title = fmtTitle(32, false);

        prtField(title, row, col);
    }

    /**
     * Draws the sidebar's class-name row - the port of C's {@code prt_class} ({@code [C]
     * ui-display.c}), which blanks the field for a shapechanged player and otherwise writes
     * {@code player->class->name}.
     *
     * <p>Method prtClass coded on 260927, commented in full on 260927.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtClass(int row, int col) {
        if (SidebarModel.isPlayerIsShapechanged())
            prtField("", row, col);
        else
            prtField(SidebarModel.getClassName(), row, col);
    }

    /**
     * Builds the text {@link #prtTitle} draws - the port of C's {@code fmt_title} ({@code [C]
     * ui-display.c}), which picks one of four texts in a fixed priority order: wizard mode beats
     * being a total winner, which beats being shapechanged, which beats the ordinary class title.
     *
     * <p>The four branches match C's {@code if}/{@code else if} chain exactly, including the one
     * C reaches with neither a {@code my_strcpy} nor a fall-through: when {@code shortMode} is
     * {@code true} and none of the first three apply, C's chain never reaches its last
     * {@code else if (!short_mode)} clause, so {@code buf} stays the empty string it was
     * initialised to; this returns {@code ""} for the same case, since the port has no callers that
     * pass {@code true} today.
     *
     * <p>The winner check ORs in {@code SidebarModel.getLevel() > ChannelRegistry.getPYMaxLevel()},
     * matching C's {@code player->total_winner || (player->lev > PY_MAX_LEVEL)} - a character can be
     * a winner either by the flag or by having somehow exceeded the level cap.
     *
     * <p>The shapechanged branch capitalises the shape name's first letter with
     * {@code toUpperCase()}, the port of C's {@code my_strcap(buf)} ({@code z-util.c}), which
     * uppercases {@code buf[0]} and leaves the rest of the string untouched.
     *
     * <p>Both the shapechanged and plain-title branches clamp {@code size} down to the string's own
     * length before calling {@code substring(0, size)}. C's {@code my_strcpy(buf, src, max)} simply
     * copies a {@code src} shorter than {@code max} unchanged; a bare {@code substring(0, size)}
     * would instead throw {@code StringIndexOutOfBoundsException} whenever the shape name or class
     * title (routinely under the 32-character {@code size} {@link #prtTitle} passes) is shorter than
     * {@code size} - the clamp is what makes the port behave like C's safe copy rather than crash.
     *
     * <p>Method fmtTitle coded on 260927, commented in full on 260927.
     *
     * @param size      the maximum length of the returned text, before the length is clamped down to
     *                  what the underlying string actually holds
     * @param shortMode {@code true} to omit the ordinary class title when none of the other three
     *                  cases apply, matching C's {@code short_mode} parameter; no caller passes
     *                  {@code true} today
     * @return the text to draw in the title field
     */
    private static String fmtTitle(int size, boolean shortMode) {
        if (SidebarModel.isWizard()) {
            return "[=-WIZARD-=]";
        }
        if (SidebarModel.isTotalWinner() || (SidebarModel.getLevel() > ChannelRegistry.getPYMaxLevel())) {
            return "***WINNER***";
        }
        if (SidebarModel.isPlayerIsShapechanged()) {
            size = Math.min(size, SidebarModel.getShapeName().length());
            String shapeName = SidebarModel.getShapeName().substring(0, size);
            shapeName = shapeName.substring(0, 1).toUpperCase() + shapeName.substring(1);
            return shapeName;
        }
        if (!shortMode) {
            size = Math.min(size, SidebarModel.getTitle().length());
            return SidebarModel.getTitle().substring(0, size);
        }
        return "";
    }

    /**
     * Draws the sidebar's race-name row - the port of C's {@code prt_race} ({@code [C]
     * ui-display.c}), which blanks the field for a shapechanged player and otherwise writes
     * {@code player->race->name}.
     *
     * <p>The shapechanged branch reads {@link SidebarModel#isPlayerIsShapechanged()}, which
     * {@code RedrawRouter.setRaceClass} writes from the third element of the {@code EVENT_RACE_CLASS}
     * payload; that payload is built in {@code PlayerCalcs.redrawStuff}'s {@code PR_MISC} arm from
     * {@code player.isShapeChanged()}. So, unlike C's re-check of {@code player_is_shapechanged}
     * against the live global on every draw, this branch reflects whatever the last
     * {@code EVENT_RACE_CLASS} signal carried, which is current as of the last time {@code PR_MISC}
     * was serviced.
     *
     * <p>Method prtRace coded on 260927, commented in full on 260927.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     */
    private static void prtRace(int row, int col) {
        if (SidebarModel.isPlayerIsShapechanged())
            prtField("", row, col);
        else
            prtField(SidebarModel.getRaceName(), row, col);
    }

    /**
     * Draws a 13-character sidebar field - the port of C's {@code prt_field} ({@code [C]
     * ui-display.c}), which blanks the field with 13 spaces before writing the new text over it, so
     * a shorter replacement value never leaves stray characters from a longer previous one.
     *
     * <p>Method prtField coded on 260927, commented in full on 260927.
     *
     * @param text the text to write, or {@code ""} to leave the field blank
     * @param row  the row to draw at
     * @param col  the column to draw at
     */
    private static void prtField(String text, int row, int col) {
        // Dump 13 spaces to clear
        term.cPutStr(ColourEnum.COLOUR_WHITE, " ".repeat(13), row, col);

        // Output the text
        term.cPutStr(ColourEnum.COLOUR_LIGHT_BLUE, text, row, col);
    }

    /**
     * Writes the {@link Term} that {@link #prtField(String, int, int)} draws to. Has no single C
     * counterpart - C's drawing calls reach the active {@code Term} directly, with nothing to hand
     * it in.
     *
     * <p>Method setTermData coded on 260927, commented in full on 260927.
     *
     * @param termData the wrapper this holder reads its {@link Term} out of
     */
    public static void setTermData(TermData termData) {
        term = termData.getTerm();
    }

    /**
     * A bespoke {@code void (T, U)} functional interface standing in for C's {@code void (*)(int,
     * int)} sidebar-hook function pointer ({@code side_handler_t} in {@code [C] ui-display.c}).
     * {@link SideHandler#SideHandler} takes one of these, bound to a method reference such as
     * {@link HandlersHolder}{@code ::prtRace}, exactly as each row of C's {@code side_handlers[]}
     * literal names its hook function directly.
     *
     * <p>A bespoke type rather than {@link java.util.function.BiConsumer}, which would serve the
     * same shape; this predates a check of whether the JDK's own two-argument, void-returning
     * interface would have done as well.
     *
     * <p>Interface prtFunction coded on 260927, commented in full on 260928.
     *
     * @param <T> the type of the first argument (row)
     * @param <U> the type of the second argument (column)
     */
    @FunctionalInterface
    public interface prtFunction<T, U> {
        void apply(T t, U u);
    }
}
