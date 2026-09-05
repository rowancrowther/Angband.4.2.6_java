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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link PlayerClass#getName()}, the direct read of C's {@code class->name}.
 *
 * <p>C reads the field itself wherever it names a class — the character sheet, the death screen,
 * the save file — with no transformation of any kind. The port's accessor has nothing to diverge
 * on: it is a plain field read, so these cases exist to pin that down rather than to hunt for an
 * edge case that could plausibly break it.
 *
 * <p>Class PlayerClassGetNameTest coded on 260904, commented in full on 260904.
 *
 * @author Rowan Crowther
 */
class PlayerClassGetNameTest {

    /**
     * A class whose only interesting property is its name.
     *
     * @param name the class's name
     * @return the class
     */
    private static PlayerClass playerClass(String name) {
        return new PlayerClass(name, List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 5, 30, 5,
                List.of(), ClassMagic.NONE);
    }

    /**
     * The ordinary case: the constructor's name argument comes back unchanged.
     */
    @Test
    @DisplayName("returns the name the class was built with")
    void returnsConstructorName() {
        assertEquals("Ranger", playerClass("Ranger").getName());
    }

    /**
     * Nothing about the string is normalised — case, spacing and punctuation all survive, matching
     * a {@code const char *} field read that applies no transformation of its own.
     */
    @Test
    @DisplayName("the name is returned exactly, with no normalisation")
    void nameIsReturnedVerbatim() {
        assertEquals("Blackguard ", playerClass("Blackguard ").getName());
    }

    /**
     * The same {@link String} instance is handed back, not a copy — consistent with C sharing one
     * {@code const char *} across every read of the field.
     */
    @Test
    @DisplayName("the same String instance is returned, not a copy")
    void returnsSameInstance() {
        String name = "Necromancer";

        assertSame(name, playerClass(name).getName());
    }
}
