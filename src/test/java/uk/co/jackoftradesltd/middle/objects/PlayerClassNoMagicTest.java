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
package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests that {@link PlayerClass} turns a {@code null} spellcasting definition into
 * {@link ClassMagic#NONE}, and that {@link ObjectKind#canBrowse()} therefore answers {@code false}
 * for such a class rather than throwing.
 *
 * <p>C's {@code obj_kind_can_browse} loops {@code class->magic.num_books}, which is zero for a
 * warrior, so the loop runs no times and the answer is false. The port reaches the same answer by
 * walking an empty book list, provided the class never holds a null.
 */
@ExtendWith(SeededPlayerRegistry.class)
@DisplayName("PlayerClass without magic")
class PlayerClassNoMagicTest {

    private Player savedPlayer;

    private static PlayerClass classWith(ClassMagic magic) {
        Map<Stats, Integer> stats = new HashMap<>();
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        return new PlayerClass("Test Class", List.of(), stats, skills, new HashMap<>(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(), magic);
    }

    @BeforeEach
    void installPlayer() throws Exception {
        savedPlayer = GameState.getPlayer();
    }

    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    @Test
    @DisplayName("a null magic becomes the shared NONE sentinel")
    void nullBecomesNone() {
        assertSame(ClassMagic.NONE, classWith(null).getMagic());
    }

    @Test
    @DisplayName("a real magic definition is kept as given")
    void realMagicKept() {
        ClassMagic magic = new ClassMagic(1, 300, 0, List.of());

        assertSame(magic, classWith(magic).getMagic());
    }

    @Test
    @DisplayName("a copy of a class built with null magic also holds NONE")
    void copyKeepsNone() {
        assertSame(ClassMagic.NONE, classWith(null).copy().getMagic());
    }

    @Test
    @DisplayName("a class without magic can browse nothing, and does not throw")
    void nonCasterBrowsesNothing() throws Exception {
        Player player = new Player();
        Field field = Player.class.getDeclaredField("playerClass");
        field.setAccessible(true);
        field.set(player, classWith(null));
        GameState.setPlayer(player);

        ObjectKind book = new ObjectKind(null, 0, 0, 0, 0, "test", TValue.TV_MAGIC_BOOK, "test",
                null, false);
        book.setsVal(1);

        assertDoesNotThrow(book::canBrowse);
        assertFalse(book.canBrowse());
    }
}
