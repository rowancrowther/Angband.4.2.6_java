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
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link HandlersHolder}'s {@code prt_field}/{@code prt_race} port and its {@code side_handlers[]}
 * table, checked against C's originals ({@code [C] ui-display.c}, functions {@code prt_field} and
 * {@code prt_race}, and the {@code side_handlers[]} initializer). {@code prtField} and {@code
 * prtRace} are private static methods with no public entry point yet - {@link SideHandler#getResult}
 * is never invoked from anywhere in the port today - so this test reaches them the same way {@code
 * PlayerIsImmuneTest} reaches {@link uk.co.jackoftradesltd.middle.player.Player}'s private fields:
 * reflectively, with {@code setAccessible(true)}.
 *
 * <p>Not in the same package as {@link SidebarModel} - its writers are exercised reflectively too,
 * rather than moving this test into {@code frontend.ui} - since the class under test here is
 * {@link HandlersHolder}, not {@link SidebarModel} (which has its own test).
 *
 * <p>Class HandlersHolderTest coded on 260927, commented in full on 260927.
 *
 * @author Rowan Crowther
 */
class HandlersHolderTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;

    private CellGrid grid;
    private Term savedTerm;
    private String savedRaceName;
    private boolean savedShapechanged;

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

    private static Method setRaceNameMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setRaceName", String.class);
        method.setAccessible(true);
        return method;
    }

    private static Method setShapechangedMethod() throws Exception {
        Method method = SidebarModel.class.getDeclaredMethod("setPlayerIsShapechanged", boolean.class);
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
        savedShapechanged = SidebarModel.isPlayerIsShapechanged();
    }

    @AfterEach
    void restoreState() throws Exception {
        termField().set(null, savedTerm);
        setRaceNameMethod().invoke(null, savedRaceName);
        setShapechangedMethod().invoke(null, savedShapechanged);
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

        Object result = prtRaceMethod().invoke(null, 4, 0);

        assertEquals(1, result);
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

        Object result = prtRaceMethod().invoke(null, 4, 0);

        assertEquals(1, result);
        for (int col = 0; col < 13; col++) {
            assertEquals(' ', cell(4, col).getCharacter(), "race name must not appear when shapechanged");
            assertEquals(ColourEnum.COLOUR_WHITE, cell(4, col).getAttributeColour());
        }
    }

    /**
     * {@link HandlersHolder#initHandlers()} - the port of C's {@code side_handlers[]}
     * initializer - registers exactly the one row ported so far: {@code prt_race} at priority
     * {@code 19} against {@code EVENT_RACE_CLASS}, matching C's {@code { prt_race, 19,
     * EVENT_RACE_CLASS }} entry.
     */
    @Test
    @SuppressWarnings("unchecked")
    void initHandlersRegistersThePrtRaceRowAtPriorityNineteen() throws Exception {
        Field field = HandlersHolder.class.getDeclaredField("sideHandlers");
        field.setAccessible(true);
        List<SideHandler> handlers = (List<SideHandler>) field.get(null);

        assertEquals(1, handlers.size());
        SideHandler handler = handlers.get(0);
        assertEquals(19, handler.getPriority());
        assertEquals(GameEventType.EVENT_RACE_CLASS, handler.getType());

        setShapechangedMethod().invoke(null, false);
        setRaceNameMethod().invoke(null, "Dwarf");
        assertTrue(handler.getResult(6, 0) == 1);
        assertEquals('D', cell(6, 0).getCharacter());
    }
}
