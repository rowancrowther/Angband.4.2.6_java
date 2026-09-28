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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.messages.data.EventDataBoolean;
import uk.co.jackoftradesltd.channel.messages.data.EventDataInt;
import uk.co.jackoftradesltd.channel.messages.data.EventDataLongStat;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStat;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.screen.Term;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;
import uk.co.jackoftradesltd.frontend.ui.RedrawRouter;
import uk.co.jackoftradesltd.frontend.ui.SidebarModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link HandlersHolder}'s {@code prtHp}/{@code prtSp} and their colour helpers, and the
 * {@link RedrawRouter} payloads that feed them, checked against C's originals ({@code [C]
 * ui-display.c}, functions {@code prt_hp} and {@code prt_sp}; {@code [C] player.c}, functions
 * {@code player_hp_attr} and {@code player_sp_attr}). The model is written only through
 * {@link RedrawRouter}'s public routing methods, the way the running game writes it, so the wiring
 * from payload to pixel is exercised end to end.
 *
 * <p>Expected values are worked from the C source: {@code "%4d"} fields, columns {@code +3},
 * {@code +7} and {@code +8}, and the colour rule {@code cur >= max -> light green;
 * cur > max * hitpoint_warn / 10 -> yellow; else red} with C's integer division.
 *
 * <p>Class HpSpRowsTest coded on 260928, commented in full on 260928.
 *
 * @author Rowan Crowther
 */
class HpSpRowsTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;

    private CellGrid grid;
    private Term savedTerm;

    private static Field termField() throws Exception {
        Field field = HandlersHolder.class.getDeclaredField("term");
        field.setAccessible(true);
        return field;
    }

    private static void invoke(String name, int row, int col) throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod(name, int.class, int.class);
        method.setAccessible(true);
        method.invoke(null, row, col);
    }

    private Term term() throws Exception {
        return (Term) termField().get(null);
    }

    private AngbandDisplayCharacter cell(int row, int col) {
        return grid.get(row, col);
    }

    private void hp(int current, int max, int warn) {
        RedrawRouter.setHP(new EventDataStat(current, max));
        RedrawRouter.setHP(new EventDataInt(warn));
    }

    private void sp(int current, int max, int first, boolean magic) {
        RedrawRouter.setSP(new EventDataStat(current, max));
        RedrawRouter.setSP(new EventDataInt(first));
        RedrawRouter.setSP(new EventDataBoolean(magic));
    }

    private void levelAndExp(int level, long exp, long maxExp) {
        RedrawRouter.setPlayerLevel(new EventDataStat(level, level));
        RedrawRouter.setExperience(new EventDataLongStat(exp, maxExp));
    }

    private ColourEnum hpColour(int current, int max, int warn) throws Exception {
        hp(current, max, warn);
        invoke("prtHp", 3, 0);
        return cell(3, 3).getAttributeColour();
    }

    private ColourEnum spColour(int current, int max, int warn) throws Exception {
        hp(1, 1, warn);
        sp(current, max, 1, true);
        levelAndExp(10, 100, 100);
        invoke("prtSp", 4, 0);
        return cell(4, 3).getAttributeColour();
    }

    @BeforeEach
    void buildTerm() throws Exception {
        grid = new CellGrid(HEIGHT, WIDTH);
        Term term = new Term();
        term.termInit(WIDTH, HEIGHT, 1024, null, new Screen(grid, new ArrayList<>()));
        savedTerm = (Term) termField().get(null);
        termField().set(null, term);
    }

    @AfterEach
    void restore() throws Exception {
        termField().set(null, savedTerm);
        hp(0, 0, 0);
        sp(0, 0, 0, false);
        levelAndExp(0, 0, 0);
    }

    /**
     * {@code EVENT_HP}'s int payload is the warning option, not anything else.
     */
    @Test
    void hpIntPayloadWritesTheWarningOption() {
        RedrawRouter.setHP(new EventDataInt(4));

        assertEquals(4, SidebarModel.getPlayerOptHPWarn());
    }

    /**
     * {@code EVENT_MANA}'s int is the first-spell level and its boolean the has-spells flag.
     */
    @Test
    void manaIntAndBooleanPayloadsLandInTheirOwnFields() {
        RedrawRouter.setSP(new EventDataInt(5));
        RedrawRouter.setSP(new EventDataBoolean(true));

        assertEquals(5, SidebarModel.getFirstSpell());
        assertEquals(true, SidebarModel.hasMagic());

        RedrawRouter.setSP(new EventDataBoolean(false));
        assertEquals(false, SidebarModel.hasMagic());
        assertEquals(5, SidebarModel.getFirstSpell(), "a boolean must not disturb the first-spell level");
    }

    /**
     * {@code prt_hp}: "HP ", then "%4d" of 25 at col+3, "/" at col+7, "%4d" of 100 at col+8.
     */
    @Test
    void prtHpLaysOutLabelAndFields() throws Exception {
        hp(25, 100, 3);

        invoke("prtHp", 2, 10);

        assertEquals('H', cell(2, 10).getCharacter());
        assertEquals('P', cell(2, 11).getCharacter());
        assertEquals(' ', cell(2, 12).getCharacter());
        assertEquals(' ', cell(2, 13).getCharacter());
        assertEquals(' ', cell(2, 14).getCharacter());
        assertEquals('2', cell(2, 15).getCharacter());
        assertEquals('5', cell(2, 16).getCharacter());
        assertEquals('/', cell(2, 17).getCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, cell(2, 17).getAttributeColour());
        assertEquals(' ', cell(2, 18).getCharacter());
        assertEquals('1', cell(2, 19).getCharacter());
        assertEquals('0', cell(2, 20).getCharacter());
        assertEquals('0', cell(2, 21).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, cell(2, 19).getAttributeColour());
    }

    /**
     * {@code player_hp_attr}, mhp 100, warn 3: threshold is 30 (strictly greater for yellow).
     */
    @Test
    void hpColourBoundaries() throws Exception {
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, hpColour(100, 100, 3));
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, hpColour(120, 100, 3), "over max is still full");
        assertEquals(ColourEnum.COLOUR_YELLOW, hpColour(99, 100, 3));
        assertEquals(ColourEnum.COLOUR_YELLOW, hpColour(31, 100, 3));
        assertEquals(ColourEnum.COLOUR_RED, hpColour(30, 100, 3));
        assertEquals(ColourEnum.COLOUR_RED, hpColour(25, 100, 3));
        assertEquals(ColourEnum.COLOUR_RED, hpColour(0, 100, 3));
    }

    /**
     * C's integer division: mhp 15, warn 3 gives 15*3/10 = 4, so 4 is red and 5 is yellow.
     */
    @Test
    void hpColourUsesIntegerDivision() throws Exception {
        assertEquals(ColourEnum.COLOUR_RED, hpColour(4, 15, 3));
        assertEquals(ColourEnum.COLOUR_YELLOW, hpColour(5, 15, 3));
    }

    /**
     * A warning of 0 makes any positive hit points yellow, and 0 red.
     */
    @Test
    void hpColourWithWarningOff() throws Exception {
        assertEquals(ColourEnum.COLOUR_YELLOW, hpColour(1, 100, 0));
        assertEquals(ColourEnum.COLOUR_RED, hpColour(0, 100, 0));
    }

    /**
     * {@code player_sp_attr} shares the hit-point warning: msp 10, warn 3 gives threshold 3.
     */
    @Test
    void spColourBoundaries() throws Exception {
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, spColour(10, 10, 3));
        assertEquals(ColourEnum.COLOUR_YELLOW, spColour(9, 10, 3));
        assertEquals(ColourEnum.COLOUR_YELLOW, spColour(4, 10, 3));
        assertEquals(ColourEnum.COLOUR_RED, spColour(3, 10, 3));
        assertEquals(ColourEnum.COLOUR_RED, spColour(0, 10, 3));
    }

    /**
     * {@code prt_sp} for a caster who has reached {@code spell_first}: "SP " then the pair.
     */
    @Test
    void prtSpDrawsForACasterAtOrAboveFirstSpellLevel() throws Exception {
        hp(1, 1, 3);
        sp(7, 42, 5, true);
        levelAndExp(5, 100, 100);

        invoke("prtSp", 6, 0);

        assertEquals('S', cell(6, 0).getCharacter());
        assertEquals('P', cell(6, 1).getCharacter());
        assertEquals(' ', cell(6, 2).getCharacter());
        assertEquals('7', cell(6, 6).getCharacter());
        assertEquals('/', cell(6, 7).getCharacter());
        assertEquals('4', cell(6, 10).getCharacter());
        assertEquals('2', cell(6, 11).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, cell(6, 10).getAttributeColour());
    }

    /**
     * {@code total_spells == 0}: C draws nothing and, with no spells, never blanks either.
     */
    @Test
    void prtSpDrawsNothingForAClassWithoutSpells() throws Exception {
        term().putStr("XXXXXXXXXXXX", 6, 0);
        sp(0, 0, 1, false);
        levelAndExp(10, 50, 100);

        invoke("prtSp", 6, 0);

        for (int col = 0; col < 12; col++) {
            assertEquals('X', cell(6, col).getCharacter(), "col " + col + " must be untouched");
        }
    }

    /**
     * {@code player->lev < spell_first} with full experience: C returns without drawing.
     */
    @Test
    void prtSpDrawsNothingBelowFirstSpellLevel() throws Exception {
        term().putStr("XXXXXXXXXXXX", 6, 0);
        sp(0, 0, 5, true);
        levelAndExp(4, 100, 100);

        invoke("prtSp", 6, 0);

        for (int col = 0; col < 12; col++) {
            assertEquals('X', cell(6, col).getCharacter(), "col " + col + " must be untouched");
        }
    }

    /**
     * Below first-spell level but drained ({@code exp < max_exp}): C blanks the twelve columns.
     */
    @Test
    void prtSpBlanksTheFieldWhenLevelDrained() throws Exception {
        term().putStr("XXXXXXXXXXXXXX", 6, 0);
        sp(0, 0, 5, true);
        levelAndExp(4, 50, 100);

        invoke("prtSp", 6, 0);

        for (int col = 0; col < 12; col++) {
            assertEquals(' ', cell(6, col).getCharacter(), "col " + col + " must be blanked");
        }
        assertEquals('X', cell(6, 12).getCharacter(), "the blanking is exactly twelve columns");
    }

    /**
     * Drained but the class has no spells: C's blanking clause needs total_spells, so no blank.
     */
    @Test
    void prtSpDoesNotBlankADrainedClassWithoutSpells() throws Exception {
        term().putStr("XXXXXXXXXXXX", 6, 0);
        sp(0, 0, 5, false);
        levelAndExp(4, 50, 100);

        invoke("prtSp", 6, 0);

        assertEquals('X', cell(6, 0).getCharacter());
    }
}
