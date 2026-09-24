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

package uk.co.jackoftradesltd.frontend.ui.entry.assembler.helperfunctions;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link UIEntryNameParameter}, the port of C's {@code name_parameters[]} table
 * ({@code [C] ui-entry.c:142-146}).
 *
 * <p>Expected values are derived from the C originals directly: {@code get_dummy_param_count} always
 * returns {@code 1}, {@code get_dummy_param_name} always returns {@code ""}, {@code get_stat_count}
 * returns {@code 5}, and {@code get_element_count} returns C's element count unshifted
 * ({@code [C] ui-entry.c:1575-1635}).
 *
 * <p>{@link UIEntryNameParameter#ENTRY_NAME_PARAMETER_ELEMENT}'s {@code getParamName} is not
 * exercised here: {@link HelperFunctions#getElementName} returns {@link ElementEnum}'s own constant
 * name (e.g. {@code "ELEM_ACID"}), not the bare token C's {@code get_element_name} returns
 * (e.g. {@code "ACID"}), a real divergence from the C original found while writing this test. It is
 * currently inert - nothing in the live assembly pipeline calls {@code getParamName} for the element
 * scheme, since {@code UIEntryAssembler}/{@code UIEntryBaseAssembler} expand
 * {@code parameter:element} records with their own hand-rolled loop over {@link ElementEnum#values()}
 * instead (stripping the {@code ELEM_} prefix themselves) - but is left unasserted rather than
 * pinned to the wrong value.
 *
 * <p>Class UIEntryNameParameterTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
class UIEntryNameParameterTest {

    @Test
    void namesMatchTheirDataFileDirectiveText() {
        assertEquals("", UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE.getName());
        assertEquals("element", UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT.getName());
        assertEquals("stat", UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT.getName());
    }

    @Test
    void noneExpandsToExactlyOneUnnamedValue() {
        assertEquals(1, UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE.getCount());
        assertEquals("", UIEntryNameParameter.ENTRY_NAME_PARAMETER_NONE.getParamName(0));
    }

    @Test
    void statExpandsToFivePlayerStatsInOrder() {
        assertEquals(5, UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT.getCount());
        assertEquals("STR", UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT.getParamName(0));
        assertEquals("INT", UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT.getParamName(1));
        assertEquals("WIS", UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT.getParamName(2));
        assertEquals("DEX", UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT.getParamName(3));
        assertEquals("CON", UIEntryNameParameter.ENTRY_NAME_PARAMETER_STAT.getParamName(4));
    }

    @Test
    void elementCountMatchesTheCCountUnshiftedDespiteTwoJavaOnlyPlaceholders() {
        // ElementEnum carries two placeholders C's element_names[] does not - ELEM_NONE and
        // ELEM_MAX - so the count is values().length - 2, not - 1, to land back on C's own 25.
        assertEquals(ElementEnum.values().length - 2, UIEntryNameParameter.ENTRY_NAME_PARAMETER_ELEMENT.getCount());
    }
}
