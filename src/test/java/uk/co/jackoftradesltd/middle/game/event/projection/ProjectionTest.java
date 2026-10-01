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

package uk.co.jackoftradesltd.middle.game.event.projection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Projection}, the Java form of {@code struct projection} in {@code project.h}.
 *
 * <p>The class holds data only, so the tests pin that every C member survives construction. The
 * expected values are the {@code ACID} and {@code LIGHT} records of {@code lib/gamedata/projection.txt}
 * in the C tree, which is where {@code obj-init.c} would read them from. The two records differ in
 * the one place the port departs from C's layout: {@code ACID} has the integer denominator
 * {@code 3} and {@code LIGHT} the dice denominator {@code 8+1d4}, which C holds in a single
 * {@code random_value}.
 *
 * <p>Class ProjectionTest coded on 261001, commented in full on 261001.
 */
class ProjectionTest {
    /**
     * Builds the {@code ACID} record of {@code projection.txt} through the integer-denominator
     * constructor.
     *
     * <p>Function acid coded on 261001, commented in full on 261001.
     *
     * @return the {@code ACID} projection
     */
    private static Projection acid() {
        return new Projection(ProjectionEnum.PROJ_ACID, "acid", ProjectionType.PT_ELEMENT,
                "acid", "acid", "acid", "acid", 1, 3, 3, 1600,
                MessageType.MSG_BR_ACID, true, true, ColourEnum.COLOUR_SLATE);
    }

    /**
     * Builds the {@code LIGHT} record of {@code projection.txt} through the dice-denominator
     * constructor.
     *
     * <p>Function light coded on 261001, commented in full on 261001.
     *
     * @return the {@code LIGHT} projection
     */
    private static Projection light() {
        return new Projection(ProjectionEnum.PROJ_LIGHT, "light", ProjectionType.PT_ELEMENT,
                "light", "light", "something", "brightness", 6, Random.parseStr("8+1d4"), 6, 500,
                MessageType.MSG_BR_LIGHT, true, true, ColourEnum.COLOUR_ORANGE);
    }

    /**
     * Reads a private field of a {@link Projection} by reflection, since the class has no getter
     * for most of its members.
     *
     * <p>Function field coded on 261001, commented in full on 261001.
     *
     * @param p    the projection to read
     * @param name the field name
     * @return the field's value
     */
    private static Object field(Projection p, String name) throws ReflectiveOperationException {
        Field f = Projection.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(p);
    }

    /**
     * Test method acidKeepsEveryMember coded on 261001, commented in full on 261001.
     */
    @Test
    @DisplayName("integer-denominator constructor keeps every member of the ACID record")
    void acidKeepsEveryMember() throws ReflectiveOperationException {
        Projection p = acid();

        assertEquals(ProjectionEnum.PROJ_ACID, p.getProjection());
        assertEquals("acid", p.getName());
        assertEquals(ProjectionType.PT_ELEMENT, p.getType());
        assertEquals("acid", field(p, "description"));
        assertEquals("acid", p.getPlayerDescription());
        assertEquals("acid", field(p, "blindDescription"));
        assertEquals("acid", p.getLashDescription());
        assertEquals(1, field(p, "numerator"));
        assertEquals(3, field(p, "denominator"));
        assertEquals(3, field(p, "divisor"));
        assertEquals(1600, field(p, "damageCap"));
        assertEquals(MessageType.MSG_BR_ACID, field(p, "msgt"));
        assertTrue((boolean) field(p, "isObvious"));
        assertTrue((boolean) field(p, "willWake"));
        assertEquals(ColourEnum.COLOUR_SLATE, field(p, "colour"));
    }

    /**
     * Test method acidHasNoDiceDenominator coded on 261001, commented in full on 261001.
     */
    @Test
    @DisplayName("integer-denominator constructor leaves the dice denominator null")
    void acidHasNoDiceDenominator() throws ReflectiveOperationException {
        assertNull(field(acid(), "diceDenominator"));
    }

    /**
     * Test method lightKeepsEveryMember coded on 261001, commented in full on 261001.
     */
    @Test
    @DisplayName("dice-denominator constructor keeps every member of the LIGHT record")
    void lightKeepsEveryMember() throws ReflectiveOperationException {
        Projection p = light();

        assertEquals(ProjectionEnum.PROJ_LIGHT, p.getProjection());
        assertEquals("light", p.getName());
        assertEquals(ProjectionType.PT_ELEMENT, p.getType());
        assertEquals("light", field(p, "description"));
        assertEquals("light", p.getPlayerDescription());
        assertEquals("something", field(p, "blindDescription"));
        assertEquals("brightness", p.getLashDescription());
        assertEquals(6, field(p, "numerator"));
        assertEquals(6, field(p, "divisor"));
        assertEquals(500, field(p, "damageCap"));
        assertEquals(MessageType.MSG_BR_LIGHT, field(p, "msgt"));
        assertTrue((boolean) field(p, "isObvious"));
        assertTrue((boolean) field(p, "willWake"));
        assertEquals(ColourEnum.COLOUR_ORANGE, field(p, "colour"));
    }

    /**
     * Test method lightDiceDenominatorIsKeptAndIntegerIsZero coded on 261001, commented in full on
     * 261001.
     */
    @Test
    @DisplayName("8+1d4 stays a dice expression and the integer denominator is C's zero")
    void lightDiceDenominatorIsKeptAndIntegerIsZero() throws ReflectiveOperationException {
        Projection p = light();
        Random dice = (Random) field(p, "diceDenominator");

        assertEquals(0, field(p, "denominator"));
        assertEquals(8, dice.getBase());
        assertEquals(1, dice.getDice());
        assertEquals(4, dice.getSides());
    }

    /**
     * Test method diceDenominatorIsStoredByReference coded on 261001, commented in full on 261001.
     */
    @Test
    @DisplayName("the dice expression passed in is the one stored")
    void diceDenominatorIsStoredByReference() throws ReflectiveOperationException {
        Random dice = Random.parseStr("8+1d4");
        Projection p = new Projection(ProjectionEnum.PROJ_LIGHT, "light", ProjectionType.PT_ELEMENT,
                "light", "light", "something", "brightness", 6, dice, 6, 500,
                MessageType.MSG_BR_LIGHT, true, true, ColourEnum.COLOUR_ORANGE);

        assertSame(dice, field(p, "diceDenominator"));
    }

    /**
     * Test method flagsAreIndependent coded on 261001, commented in full on 261001.
     */
    @Test
    @DisplayName("obvious and wake are stored independently, as C's two bool members are")
    void flagsAreIndependent() throws ReflectiveOperationException {
        Projection p = new Projection(ProjectionEnum.PROJ_ACID, "acid", ProjectionType.PT_ELEMENT,
                "acid", "acid", "acid", "acid", 1, 3, 3, 1600,
                MessageType.MSG_BR_ACID, true, false, ColourEnum.COLOUR_SLATE);

        assertTrue((boolean) field(p, "isObvious"));
        assertFalse((boolean) field(p, "willWake"));
    }

    /**
     * Test method unsetRecordKeepsCZeroValues coded on 261001, commented in full on 261001.
     */
    @Test
    @DisplayName("zero and null arguments are stored as given, as C's zalloc'd record would hold them")
    void unsetRecordKeepsCZeroValues() throws ReflectiveOperationException {
        Projection p = new Projection(ProjectionEnum.PROJ_ACID, null, ProjectionType.PT_ELEMENT,
                null, null, null, null, 0, 0, 0, 0,
                MessageType.MSG_GENERIC, false, false, ColourEnum.COLOUR_SLATE);

        assertNull(p.getName());
        assertEquals(0, field(p, "numerator"));
        assertEquals(0, field(p, "denominator"));
        assertEquals(0, field(p, "divisor"));
        assertEquals(0, field(p, "damageCap"));
        assertEquals(MessageType.MSG_GENERIC, field(p, "msgt"));
    }

    /**
     * Test method toStringListsTheFields coded on 261001, commented in full on 261001.
     */
    @Test
    @DisplayName("toString names the projection and its numeric members")
    void toStringListsTheFields() {
        String s = acid().toString();

        assertTrue(s.startsWith("Projection{projection=PROJ_ACID"), s);
        assertTrue(s.contains("numerator=1"), s);
        assertTrue(s.contains("denominator=3"), s);
        assertTrue(s.contains("damageCap=1600"), s);
    }
}
