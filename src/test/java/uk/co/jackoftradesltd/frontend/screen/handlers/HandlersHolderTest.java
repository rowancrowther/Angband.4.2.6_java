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
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.screen.Term;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;
import uk.co.jackoftradesltd.frontend.ui.SidebarModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link HandlersHolder}'s {@code prt_field}/{@code prt_race}/{@code prt_class}/{@code prt_level}/
 * {@code prt_title}/{@code fmt_title}/{@code prt_gold}/{@code prt_stat}/{@code prt_ac} port and its
 * {@code side_handlers[]} table, checked against C's originals ({@code [C] ui-display.c},
 * functions {@code prt_field}, {@code prt_race}, {@code prt_class}, {@code prt_level},
 * {@code prt_title}, {@code fmt_title}, {@code prt_gold}, {@code prt_stat}, {@code prt_ac} and its
 * {@code prt_str}/{@code prt_int}/{@code prt_wis}/{@code prt_dex}/{@code prt_con} wrappers, and the
 * {@code side_handlers[]} initializer). All of these are private static
 * methods with no public
 * entry point yet - {@link SideHandler#getResult} is never invoked from anywhere in the port today
 * - so this test reaches them the same way {@code PlayerIsImmuneTest} reaches
 * {@link uk.co.jackoftradesltd.middle.player.Player}'s private fields: reflectively, with
 * {@code setAccessible(true)}.
 *
 * <p>Not in the same package as {@link SidebarModel} - its writers are exercised reflectively too,
 * rather than moving this test into {@code frontend.ui} - since the class under test here is
 * {@link HandlersHolder}, not {@link SidebarModel} (which has its own test).
 *
 * <p>Class HandlersHolderTest coded on 260927, commented in full on 260928.
 *
 * @author Rowan Crowther
 */
class HandlersHolderTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;

    private CellGrid grid;
    private Term savedTerm;
    private String savedRaceName;
    private String savedClassName;
    private String savedTitle;
    private String savedShapeName;
    private boolean savedShapechanged;
    private boolean savedWizard;
    private boolean savedTotalWinner;
    private int savedLevel;
    private int savedMaxLevel;
    private long savedGold;
    private int savedAc;
    private int savedPyMaxLevel;
    private AngbandDisplayCharacter[] savedEquipString;
    private int[] savedCurrentStats;
    private int[] savedMaxStats;
    private int[] savedUseStats;

    private static Field termField() throws Exception {
        Field field = HandlersHolder.class.getDeclaredField("term");
        field.setAccessible(true);
        return field;
    }

    private static Method prtFieldMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtField", String.class, int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtRaceMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtRace", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtClassMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtClass", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtLevelMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtLevel", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtTitleMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtTitle", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method fmtTitleMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("fmtTitle", int.class, boolean.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtGoldMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtGold", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtEquippyMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtEquippy", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtStrMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtStr", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtIntMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtInt", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtWisMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtWis", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtDexMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtDex", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtConMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtCon", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtStatMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtStat", int.class, int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method prtAcMethod() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtAc", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setCurrentStatMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setCurrentStat", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setMaxStatMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setMaxStat", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setUseStatMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setUseStat", int.class, int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setRaceNameMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setRaceName", String.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setClassNameMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setClassName", String.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setTitleMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setTitle", String.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setShapeNameMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setShapeName", String.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setWizardMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setWizard", boolean.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setTotalWinnerMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setTotalWinner", boolean.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setLevelMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setLevel", int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setMaxLevelMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setMaxLevel", int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setShapechangedMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setPlayerIsShapechanged", boolean.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setGoldMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setGold", long.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setAcMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setAc", int.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setEquippyStringMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setEquippyString", AngbandDisplayCharacter[].class);
        method.setAccessible(true);
        return method;
    }

    private AngbandDisplayCharacter cell(int row, int col) {
        return grid.get(row, col);
    }

    @BeforeEach
    void buildTermAndSaveState() throws Exception {
        grid = new CellGrid(HEIGHT, WIDTH);
        Term term = new Term();
        term.termInit(WIDTH, HEIGHT, 1024, null, new Screen(grid, new ArrayList<>()));

        Field field = termField();
        savedTerm = (Term) field.get(null);
        field.set(null, term);

        savedRaceName = SidebarModel.getRaceName();
        savedClassName = SidebarModel.getClassName();
        savedTitle = SidebarModel.getTitle();
        savedShapeName = SidebarModel.getShapeName();
        savedShapechanged = SidebarModel.isPlayerIsShapechanged();
        savedWizard = SidebarModel.isWizard();
        savedTotalWinner = SidebarModel.isTotalWinner();
        savedLevel = SidebarModel.getLevel();
        savedMaxLevel = SidebarModel.getMaxLevel();
        savedGold = SidebarModel.getGold();
        savedAc = SidebarModel.getAc();
        savedPyMaxLevel = ChannelRegistry.getPYMaxLevel();
        savedEquipString = SidebarModel.getEquippyString();
        savedCurrentStats = new int[5];
        savedMaxStats = new int[5];
        savedUseStats = new int[5];
        for (int index = 0; index < 5; index++) {
            savedCurrentStats[index] = SidebarModel.getCurrentStat(index);
            savedMaxStats[index] = SidebarModel.getMaxStat(index);
            savedUseStats[index] = SidebarModel.getUseStat(index);
        }
    }

    @AfterEach
    void restoreState() throws Exception {
        termField().set(null, savedTerm);
        setRaceNameMethod().invoke(null, savedRaceName);
        setClassNameMethod().invoke(null, savedClassName);
        setTitleMethod().invoke(null, savedTitle);
        setShapeNameMethod().invoke(null, savedShapeName);
        setShapechangedMethod().invoke(null, savedShapechanged);
        setWizardMethod().invoke(null, savedWizard);
        setTotalWinnerMethod().invoke(null, savedTotalWinner);
        setLevelMethod().invoke(null, savedLevel);
        setMaxLevelMethod().invoke(null, savedMaxLevel);
        setGoldMethod().invoke(null, savedGold);
        setAcMethod().invoke(null, savedAc);
        ChannelRegistry.setPYMaxLevel(savedPyMaxLevel);
        setEquippyStringMethod().invoke(null, (Object) savedEquipString);
        for (int index = 0; index < 5; index++) {
            setCurrentStatMethod().invoke(null, index, savedCurrentStats[index]);
            setMaxStatMethod().invoke(null, index, savedMaxStats[index]);
            setUseStatMethod().invoke(null, index, savedUseStats[index]);
        }
    }

    /**
     * {@code prt_field} dumps 13 spaces in white before writing the text, so a shorter
     * replacement value leaves the tail of the field blank in white rather than showing
     * whatever the previous, longer value left there.
     */
    @Test
    void prtFieldClearsThirteenCellsThenWritesTheText() throws Exception {
        prtFieldMethod().invoke(null, "Elf", 3, 5);

        assertEquals('E', cell(3, 5).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, cell(3, 5).getAttributeColour());
        assertEquals('l', cell(3, 6).getCharacter());
        assertEquals('f', cell(3, 7).getCharacter());

        // The remaining ten cells of the thirteen-cell field are still the white-space clear.
        for (int col = 8; col < 5 + 13; col++) {
            assertEquals(' ', cell(3, col).getCharacter(), "cell at col " + col + " should be the clear");
            assertEquals(ColourEnum.COLOUR_WHITE, cell(3, col).getAttributeColour());
        }
    }

    /**
     * An empty field (C's {@code prt_field("", row, col)}) writes nothing over the clear, so
     * every one of the thirteen cells is left as the white-space blank.
     */
    @Test
    void prtFieldWithEmptyTextLeavesTheWholeFieldBlank() throws Exception {
        prtFieldMethod().invoke(null, "", 3, 5);

        for (int col = 5; col < 5 + 13; col++) {
            assertEquals(' ', cell(3, col).getCharacter());
            assertEquals(ColourEnum.COLOUR_WHITE, cell(3, col).getAttributeColour());
        }
    }

    /**
     * The ordinary path: not shapechanged, so {@code prt_race} ({@code [C] ui-display.c})
     * writes {@code player->race->name} - here, {@link SidebarModel#getRaceName()} - into the
     * field in light blue.
     */
    @Test
    void prtRaceWritesTheRaceNameWhenNotShapechanged() throws Exception {
        setShapechangedMethod().invoke(null, false);
        setRaceNameMethod().invoke(null, "Half-Troll");

        prtRaceMethod().invoke(null, 4, 0);

        assertEquals('H', cell(4, 0).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, cell(4, 0).getAttributeColour());
        assertEquals('a', cell(4, 1).getCharacter());
    }

    /**
     * The shapechanged branch: C's {@code prt_race} blanks the field with {@code prt_field("",
     * row, col)} rather than showing the race name, when {@code player_is_shapechanged(player)}
     * is true. {@link SidebarModel#isPlayerIsShapechanged()} has no writer wired up in the port
     * yet (see that field's own Javadoc), but the branch itself, once the flag is set directly,
     * behaves exactly as C's does.
     */
    @Test
    void prtRaceBlanksTheFieldWhenShapechanged() throws Exception {
        setRaceNameMethod().invoke(null, "Half-Troll");
        setShapechangedMethod().invoke(null, true);

        prtRaceMethod().invoke(null, 4, 0);

        for (int col = 0; col < 13; col++) {
            assertEquals(' ', cell(4, col).getCharacter(), "race name must not appear when shapechanged");
            assertEquals(ColourEnum.COLOUR_WHITE, cell(4, col).getAttributeColour());
        }
    }

    /**
     * {@link HandlersHolder#initHandlers()} - the port of C's {@code side_handlers[]}
     * initializer - registers all fourteen rows built so far, in C's table order: {@code prt_race}
     * at priority {@code 19} against {@code EVENT_RACE_CLASS}, {@code prt_title} at {@code 18}
     * against {@code EVENT_PLAYERTITLE}, {@code prt_class} at {@code 22} against
     * {@code EVENT_RACE_CLASS}, {@code prt_level} at {@code 10} against
     * {@code EVENT_PLAYERLEVEL}, {@code prt_exp} at {@code 16} against
     * {@code EVENT_EXPERIENCE}, {@code prt_gold} at {@code 11} against {@code EVENT_GOLD},
     * {@code prt_equippy} at {@code 17} against {@code EVENT_EQUIPMENT}, the five stat rows
     * {@code prt_str}, {@code prt_int}, {@code prt_wis}, {@code prt_dex} and {@code prt_con} at
     * {@code 6}, {@code 5}, {@code 4}, {@code 3} and {@code 2} respectively, all against
     * {@code EVENT_STATS}, C's {@code { NULL, 15, 0 }} placeholder row, and {@code prt_ac} at
     * {@code 7} against {@code EVENT_AC} - matching C's {@code { prt_race, 19, EVENT_RACE_CLASS },
     * { prt_title, 18, EVENT_PLAYERTITLE }, { prt_class, 22, EVENT_RACE_CLASS },
     * { prt_level, 10, EVENT_PLAYERLEVEL }, { prt_exp, 16, EVENT_EXPERIENCE },
     * { prt_gold, 11, EVENT_GOLD }, { prt_equippy, 17, EVENT_EQUIPMENT },
     * { prt_str, 6, EVENT_STATS }, { prt_int, 5, EVENT_STATS }, { prt_wis, 4, EVENT_STATS },
     * { prt_dex, 3, EVENT_STATS }, { prt_con, 2, EVENT_STATS }, { NULL, 15, 0 },
     * { prt_ac, 7, EVENT_AC }} entries exactly. Every row built by
     * {@link HandlersHolder#initHandlers()} must reach {@link #sideHandlers} - a handler built but
     * never added, as {@code prt_gold}'s once was, is registered in name only and is never invoked
     * by anything that walks the table.
     */
    @Test
    @SuppressWarnings("unchecked")
    void initHandlersRegistersAllFourteenRowsInTableOrder() throws Exception {
        Field field = HandlersHolder.class.getDeclaredField("sideHandlers");
        field.setAccessible(true);
        List<SideHandler> handlers = (List<SideHandler>) field.get(null);

        assertEquals(20, handlers.size());

        assertEquals(19, handlers.get(0).getPriority());
        assertEquals(GameEventType.EVENT_RACE_CLASS, handlers.get(0).getType());

        assertEquals(18, handlers.get(1).getPriority());
        assertEquals(GameEventType.EVENT_PLAYERTITLE, handlers.get(1).getType());

        assertEquals(22, handlers.get(2).getPriority());
        assertEquals(GameEventType.EVENT_RACE_CLASS, handlers.get(2).getType());

        assertEquals(10, handlers.get(3).getPriority());
        assertEquals(GameEventType.EVENT_PLAYERLEVEL, handlers.get(3).getType());

        assertEquals(16, handlers.get(4).getPriority());
        assertEquals(GameEventType.EVENT_EXPERIENCE, handlers.get(4).getType());

        assertEquals(11, handlers.get(5).getPriority());
        assertEquals(GameEventType.EVENT_GOLD, handlers.get(5).getType());

        assertEquals(17, handlers.get(6).getPriority());
        assertEquals(GameEventType.EVENT_EQUIPMENT, handlers.get(6).getType());

        assertEquals(6, handlers.get(7).getPriority());
        assertEquals(GameEventType.EVENT_STATS, handlers.get(7).getType());

        assertEquals(5, handlers.get(8).getPriority());
        assertEquals(GameEventType.EVENT_STATS, handlers.get(8).getType());

        assertEquals(4, handlers.get(9).getPriority());
        assertEquals(GameEventType.EVENT_STATS, handlers.get(9).getType());

        assertEquals(3, handlers.get(10).getPriority());
        assertEquals(GameEventType.EVENT_STATS, handlers.get(10).getType());

        assertEquals(2, handlers.get(11).getPriority());
        assertEquals(GameEventType.EVENT_STATS, handlers.get(11).getType());

        assertEquals(15, handlers.get(12).getPriority());
        assertEquals(null, handlers.get(12).getType());

        assertEquals(7, handlers.get(13).getPriority());
        assertEquals(GameEventType.EVENT_AC, handlers.get(13).getType());

        // C: { prt_hp, 8, EVENT_HP }, { prt_sp, 9, EVENT_MANA }, { NULL, 21, 0 }
        assertEquals(8, handlers.get(14).getPriority());
        assertEquals(GameEventType.EVENT_HP, handlers.get(14).getType());

        assertEquals(9, handlers.get(15).getPriority());
        assertEquals(GameEventType.EVENT_MANA, handlers.get(15).getType());

        assertEquals(21, handlers.get(16).getPriority());
        assertEquals(null, handlers.get(16).getType());

        // C: { prt_health, 12, EVENT_MONSTERHEALTH }, { NULL, 20, 0 }, { NULL, 22, 0 }
        assertEquals(12, handlers.get(17).getPriority());
        assertEquals(GameEventType.EVENT_MONSTERHEALTH, handlers.get(17).getType());

        assertEquals(20, handlers.get(18).getPriority());
        assertEquals(null, handlers.get(18).getType());

        assertEquals(22, handlers.get(19).getPriority());
        assertEquals(null, handlers.get(19).getType());

        setShapechangedMethod().invoke(null, false);
        setRaceNameMethod().invoke(null, "Dwarf");
        handlers.get(0).getResult(6, 0);
        assertEquals('D', cell(6, 0).getCharacter());

        setGoldMethod().invoke(null, 42L);
        handlers.get(5).getResult(9, 0);
        assertEquals('A', cell(9, 0).getCharacter());

        setEquippyStringMethod().invoke(null, (Object) new AngbandDisplayCharacter[]{
                new AngbandDisplayCharacter('/', ColourEnum.COLOUR_WHITE)
        });
        handlers.get(6).getResult(10, 0);
        assertEquals('/', cell(10, 0).getCharacter());

        setCurrentStatMethod().invoke(null, 0, 18);
        setMaxStatMethod().invoke(null, 0, 18);
        setUseStatMethod().invoke(null, 0, 18);
        handlers.get(7).getResult(12, 0);
        assertEquals('S', cell(12, 0).getCharacter());

        setAcMethod().invoke(null, 15);
        handlers.get(13).getResult(13, 0);
        assertEquals('C', cell(13, 0).getCharacter());
    }

    /**
     * {@code prt_equippy} ({@code [C] ui-display.c}): draws one glyph per equipment slot, in slot
     * order, straight from {@link SidebarModel#getEquippyString()} - there is no {@code player}
     * global on this side of the boundary for the method to read itself, unlike C's original,
     * which calls {@code object_attr}/{@code object_char} on each slot directly.
     */
    @Test
    void prtEquippyDrawsEachGlyphAtItsOwnColumn() throws Exception {
        setEquippyStringMethod().invoke(null, (Object) new AngbandDisplayCharacter[]{
                new AngbandDisplayCharacter('/', ColourEnum.COLOUR_WHITE),
                new AngbandDisplayCharacter(')', ColourEnum.COLOUR_UMBER),
                new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE)
        });

        prtEquippyMethod().invoke(null, 11, 0);

        assertEquals('/', cell(11, 0).getCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, cell(11, 0).getAttributeColour());
        assertEquals(')', cell(11, 1).getCharacter());
        assertEquals(ColourEnum.COLOUR_UMBER, cell(11, 1).getAttributeColour());
        assertEquals(' ', cell(11, 2).getCharacter());
    }

    /**
     * {@code prt_class} ({@code [C] ui-display.c}): the ordinary path writes
     * {@code player->class->name} - here, {@link SidebarModel#getClassName()} - into the field.
     */
    @Test
    void prtClassWritesTheClassNameWhenNotShapechanged() throws Exception {
        setShapechangedMethod().invoke(null, false);
        setClassNameMethod().invoke(null, "Ranger");

        prtClassMethod().invoke(null, 5, 0);

        assertEquals('R', cell(5, 0).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, cell(5, 0).getAttributeColour());
        assertEquals('a', cell(5, 1).getCharacter());
    }

    /**
     * {@code prt_class} ({@code [C] ui-display.c}): the shapechanged branch blanks the field with
     * {@code prt_field("", row, col)} rather than showing the class name, the same guard
     * {@code prt_race} applies.
     */
    @Test
    void prtClassBlanksTheFieldWhenShapechanged() throws Exception {
        setClassNameMethod().invoke(null, "Ranger");
        setShapechangedMethod().invoke(null, true);

        prtClassMethod().invoke(null, 5, 0);

        for (int col = 0; col < 13; col++) {
            assertEquals(' ', cell(5, col).getCharacter(), "class name must not appear when shapechanged");
        }
    }

    /**
     * {@code prt_level} ({@code [C] ui-display.c}): the current level, level-at-maximum path writes
     * "LEVEL " in light green followed by the level right-justified in a six-wide field, matching
     * C's {@code "%6d"}.
     */
    @Test
    void prtLevelWritesLevelInLightGreenWhenAtMaximum() throws Exception {
        setLevelMethod().invoke(null, 12);
        setMaxLevelMethod().invoke(null, 12);

        prtLevelMethod().invoke(null, 7, 0);

        assertEquals('L', cell(7, 0).getCharacter());
        assertEquals('E', cell(7, 1).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, cell(7, 6).getAttributeColour());
        // "%6d" of 12 is four spaces then "12"
        assertEquals(' ', cell(7, 6).getCharacter());
        assertEquals('1', cell(7, 10).getCharacter());
        assertEquals('2', cell(7, 11).getCharacter());
    }

    /**
     * {@code prt_level} ({@code [C] ui-display.c}): below the recorded maximum, the row switches
     * to "Level " in yellow instead.
     */
    @Test
    void prtLevelWritesLevelInYellowWhenBelowMaximum() throws Exception {
        setLevelMethod().invoke(null, 9);
        setMaxLevelMethod().invoke(null, 12);

        prtLevelMethod().invoke(null, 7, 0);

        // "Level " and "LEVEL " both start with 'L'; the second character is what distinguishes them.
        assertEquals('e', cell(7, 1).getCharacter());
        assertEquals(ColourEnum.COLOUR_YELLOW, cell(7, 6).getAttributeColour());
        assertEquals('9', cell(7, 11).getCharacter());
    }

    /**
     * {@code prt_title} ({@code [C] ui-display.c}): draws whatever {@code fmt_title} builds through
     * the same 13-character field {@code prt_race}/{@code prt_class} use - here, the wizard branch,
     * so the test does not depend on {@code fmt_title}'s own behaviour being separately correct.
     */
    @Test
    void prtTitleDrawsTheFormattedTitleText() throws Exception {
        setWizardMethod().invoke(null, true);

        prtTitleMethod().invoke(null, 8, 0);

        assertEquals('[', cell(8, 0).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, cell(8, 0).getAttributeColour());
    }

    /**
     * {@code fmt_title} ({@code [C] ui-display.c}): wizard mode wins over every other case,
     * matching C's {@code my_strcpy(buf, "[=-WIZARD-=]", max)} - including the dashes, not tildes.
     */
    @Test
    void fmtTitleReturnsWizardTextWhenWizard() throws Exception {
        setWizardMethod().invoke(null, true);
        setTotalWinnerMethod().invoke(null, true);

        String result = (String) fmtTitleMethod().invoke(null, 32, false);

        assertEquals("[=-WIZARD-=]", result);
    }

    /**
     * {@code fmt_title}: the total-winner flag alone produces "***WINNER***", matching C's
     * {@code player->total_winner} half of the {@code ||}.
     */
    @Test
    void fmtTitleReturnsWinnerTextWhenTotalWinner() throws Exception {
        setWizardMethod().invoke(null, false);
        setTotalWinnerMethod().invoke(null, true);

        String result = (String) fmtTitleMethod().invoke(null, 32, false);

        assertEquals("***WINNER***", result);
    }

    /**
     * {@code fmt_title}: exceeding the level cap also produces "***WINNER***" even with the flag
     * unset, matching C's {@code (player->lev > PY_MAX_LEVEL)} half of the {@code ||} -
     * a case the flag alone would miss.
     */
    @Test
    void fmtTitleReturnsWinnerTextWhenLevelExceedsCap() throws Exception {
        setWizardMethod().invoke(null, false);
        setTotalWinnerMethod().invoke(null, false);
        ChannelRegistry.setPYMaxLevel(50);
        setLevelMethod().invoke(null, 51);

        String result = (String) fmtTitleMethod().invoke(null, 32, false);

        assertEquals("***WINNER***", result);
    }

    /**
     * {@code fmt_title}: the shapechanged branch capitalises the shape name's first letter, the
     * port of C's {@code my_strcap(buf)}, and leaves the rest of the name as given.
     */
    @Test
    void fmtTitleCapitalisesTheShapeNameWhenShapechanged() throws Exception {
        setWizardMethod().invoke(null, false);
        setTotalWinnerMethod().invoke(null, false);
        ChannelRegistry.setPYMaxLevel(50);
        setLevelMethod().invoke(null, 10);
        setShapechangedMethod().invoke(null, true);
        setShapeNameMethod().invoke(null, "wolf");

        String result = (String) fmtTitleMethod().invoke(null, 32, false);

        assertEquals("Wolf", result);
    }

    /**
     * {@code fmt_title}: a shape name shorter than {@code size} must round-trip unchanged rather
     * than throw - C's {@code my_strcpy(buf, src, max)} copies a short {@code src} unchanged, and a
     * bare {@code substring(0, size)} would instead throw
     * {@code StringIndexOutOfBoundsException} here.
     */
    @Test
    void fmtTitleDoesNotThrowWhenShapeNameIsShorterThanSize() throws Exception {
        setWizardMethod().invoke(null, false);
        setTotalWinnerMethod().invoke(null, false);
        ChannelRegistry.setPYMaxLevel(50);
        setLevelMethod().invoke(null, 10);
        setShapechangedMethod().invoke(null, true);
        setShapeNameMethod().invoke(null, "bat");

        String result = (String) fmtTitleMethod().invoke(null, 32, false);

        assertEquals("Bat", result);
    }

    /**
     * {@code fmt_title}: with none of wizard, winner or shapechanged, and {@code short_mode}
     * false, the plain class title is returned - here, shorter than {@code size}, which must not
     * throw for the same reason as the shapechanged case.
     */
    @Test
    void fmtTitleReturnsThePlainTitleWhenNoOtherCaseApplies() throws Exception {
        setWizardMethod().invoke(null, false);
        setTotalWinnerMethod().invoke(null, false);
        ChannelRegistry.setPYMaxLevel(50);
        setLevelMethod().invoke(null, 10);
        setShapechangedMethod().invoke(null, false);
        setTitleMethod().invoke(null, "Rogue");

        String result = (String) fmtTitleMethod().invoke(null, 32, false);

        assertEquals("Rogue", result);
    }

    /**
     * {@code fmt_title}: with {@code short_mode} true and none of the first three cases applying,
     * C's {@code if}/{@code else if} chain never reaches its last clause, so {@code buf} stays the
     * empty string it was initialised to - this returns {@code ""} for the same case.
     */
    @Test
    void fmtTitleReturnsEmptyStringInShortModeWhenNoOtherCaseApplies() throws Exception {
        setWizardMethod().invoke(null, false);
        setTotalWinnerMethod().invoke(null, false);
        ChannelRegistry.setPYMaxLevel(50);
        setLevelMethod().invoke(null, 10);
        setShapechangedMethod().invoke(null, false);
        setTitleMethod().invoke(null, "Rogue");

        String result = (String) fmtTitleMethod().invoke(null, 32, true);

        assertEquals("", result);
    }

    /**
     * {@code prt_gold} ({@code [C] ui-display.c}): writes the "AU " label followed by the gold
     * total right-justified in a nine-wide field, matching C's {@code "%9ld"} - here with
     * {@code "%9d"} of {@code 1234}, five leading spaces then the four digits.
     */
    @Test
    void prtGoldWritesLabelAndFormattedFigure() throws Exception {
        setGoldMethod().invoke(null, 1234L);

        prtGoldMethod().invoke(null, 9, 0);

        assertEquals('A', cell(9, 0).getCharacter());
        assertEquals('U', cell(9, 1).getCharacter());
        assertEquals(' ', cell(9, 2).getCharacter());

        // "%9d" of 1234 is five leading spaces then "1234".
        assertEquals(' ', cell(9, 3).getCharacter());
        assertEquals(' ', cell(9, 7).getCharacter());
        assertEquals('1', cell(9, 8).getCharacter());
        assertEquals('2', cell(9, 9).getCharacter());
        assertEquals('3', cell(9, 10).getCharacter());
        assertEquals('4', cell(9, 11).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, cell(9, 8).getAttributeColour());
    }

    /**
     * {@code prt_gold}: zero gold is a real total, not an "unset" sentinel, so it must format
     * through the same nine-wide field as any other value - eight leading spaces then "0".
     */
    @Test
    void prtGoldFormatsZeroAsNineWideField() throws Exception {
        setGoldMethod().invoke(null, 0L);

        prtGoldMethod().invoke(null, 9, 0);

        for (int col = 3; col < 11; col++) {
            assertEquals(' ', cell(9, col).getCharacter(), "cell at col " + col + " should be a leading space");
        }
        assertEquals('0', cell(9, 11).getCharacter());
    }

    /**
     * {@code prt_ac} ({@code [C] ui-display.c}): writes the "Cur AC " label followed by the armour
     * class right-justified in a five-wide field, matching C's {@code "%5d"} - here with
     * {@code "%5d"} of {@code 15}, three leading spaces then the two digits.
     */
    @Test
    void prtAcWritesLabelAndFormattedFigure() throws Exception {
        setAcMethod().invoke(null, 15);

        prtAcMethod().invoke(null, 9, 0);

        assertEquals('C', cell(9, 0).getCharacter());
        assertEquals('u', cell(9, 1).getCharacter());
        assertEquals('r', cell(9, 2).getCharacter());
        assertEquals(' ', cell(9, 3).getCharacter());
        assertEquals('A', cell(9, 4).getCharacter());
        assertEquals('C', cell(9, 5).getCharacter());
        assertEquals(' ', cell(9, 6).getCharacter());

        // "%5d" of 15 is three leading spaces then "15", at col + 7.
        assertEquals(' ', cell(9, 7).getCharacter());
        assertEquals(' ', cell(9, 8).getCharacter());
        assertEquals(' ', cell(9, 9).getCharacter());
        assertEquals('1', cell(9, 10).getCharacter());
        assertEquals('5', cell(9, 11).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, cell(9, 10).getAttributeColour());
    }

    /**
     * {@code prt_ac}: zero AC is a real value, not an "unset" sentinel, so it must format through
     * the same five-wide field as any other value - four leading spaces then "0".
     */
    @Test
    void prtAcFormatsZeroAsFiveWideField() throws Exception {
        setAcMethod().invoke(null, 0);

        prtAcMethod().invoke(null, 9, 0);

        for (int col = 7; col < 11; col++) {
            assertEquals(' ', cell(9, col).getCharacter(), "cell at col " + col + " should be a leading space");
        }
        assertEquals('0', cell(9, 11).getCharacter());
    }

    /**
     * {@code prt_stat} ({@code [C] ui-display.c:158-176}): the drained path (current below max)
     * writes the reduced/lowercase name in yellow and displays the stat's <em>use</em> value, not
     * its current value - matching C's {@code cnv_stat(player->state.stat_use[stat], ...)} call,
     * which reads {@code state.stat_use}, not {@code stat_cur}, in both branches.
     */
    @Test
    void prtStatWritesReducedNameAndUseValueWhenDrained() throws Exception {
        setCurrentStatMethod().invoke(null, 0, 10);
        setMaxStatMethod().invoke(null, 0, 18);
        setUseStatMethod().invoke(null, 0, 14);

        prtStatMethod().invoke(null, 0, 6, 0);

        assertEquals('S', cell(6, 0).getCharacter());
        assertEquals('t', cell(6, 1).getCharacter());
        assertEquals(ColourEnum.COLOUR_YELLOW, cell(6, 6).getAttributeColour());
        // cnvStat(14, 32) is "    14" - four spaces then the two digits, at col + 6.
        assertEquals('1', cell(6, 10).getCharacter());
        assertEquals('4', cell(6, 11).getCharacter());
    }

    /**
     * {@code prt_stat}: the full path (current at or above max) writes the normal/uppercase name
     * in light green instead, still displaying the <em>use</em> value.
     */
    @Test
    void prtStatWritesNormalNameAndUseValueWhenFull() throws Exception {
        setCurrentStatMethod().invoke(null, 0, 18);
        setMaxStatMethod().invoke(null, 0, 18);
        setUseStatMethod().invoke(null, 0, 18);

        prtStatMethod().invoke(null, 0, 6, 0);

        assertEquals('S', cell(6, 0).getCharacter());
        assertEquals('T', cell(6, 1).getCharacter());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, cell(6, 6).getAttributeColour());
    }

    /**
     * {@code prt_stat}: a recorded maximum of {@code 18 + 100} draws the natural-maximum "!"
     * marker three columns after the label, matching C's
     * {@code player->stat_max[stat] == 18+100} check.
     */
    @Test
    void prtStatDrawsNaturalMaximumMarker() throws Exception {
        setCurrentStatMethod().invoke(null, 4, 18 + 100);
        setMaxStatMethod().invoke(null, 4, 18 + 100);
        setUseStatMethod().invoke(null, 4, 18 + 100);

        prtStatMethod().invoke(null, 4, 6, 0);

        assertEquals('!', cell(6, 3).getCharacter());
    }

    /**
     * {@code prt_stat}: below the natural-maximum sentinel, no "!" marker overwrites the label -
     * {@code col + 3} is left as whichever character {@code "CON: "}/{@code "Con: "} already put
     * there (the colon), rather than being blanked.
     */
    @Test
    void prtStatDrawsNoMarkerBelowNaturalMaximum() throws Exception {
        setCurrentStatMethod().invoke(null, 4, 18);
        setMaxStatMethod().invoke(null, 4, 18);
        setUseStatMethod().invoke(null, 4, 18);

        prtStatMethod().invoke(null, 4, 6, 0);

        assertEquals(':', cell(6, 3).getCharacter(), "no '!' marker should overwrite the label's own colon");
    }

    /**
     * {@code prt_str}/{@code prt_int}/{@code prt_wis}/{@code prt_dex}/{@code prt_con}
     * ({@code [C] ui-display.c}): each is a thin wrapper calling {@code prt_stat} with its own
     * fixed stat index - checked here by giving each stat index a distinct use value and
     * confirming each wrapper reads its own slot, not another's.
     */
    @Test
    void statWrappersEachReadTheirOwnIndex() throws Exception {
        Method[] wrappers = {prtStrMethod(), prtIntMethod(), prtWisMethod(), prtDexMethod(), prtConMethod()};
        for (int index = 0; index < 5; index++) {
            setCurrentStatMethod().invoke(null, index, 18);
            setMaxStatMethod().invoke(null, index, 18);
            setUseStatMethod().invoke(null, index, 10 + index);
        }

        for (int index = 0; index < 5; index++) {
            wrappers[index].invoke(null, 6 + index, 0);
            // cnvStat(10 + index, 32) is "    1" followed by a digit, at col + 6; the last digit
            // identifies which index the wrapper actually read.
            char expectedDigit = Character.forDigit((10 + index) % 10, 10);
            assertEquals(expectedDigit, cell(6 + index, 11).getCharacter(),
                    "wrapper at index " + index + " must read SidebarModel's stat " + index);
        }
    }
}
