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
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectKind#canBrowse()}, the port of C's {@code obj_kind_can_browse}
 * ({@code obj-util.c}).
 *
 * <p>C's loop walks the class's books and answers true on the first whose {@code tval} and
 * {@code sval} both equal the kind's, otherwise false. The cases below follow that body: a match
 * on both halves, a match on one half only (each way), a match on a later book in the list, a
 * class with magic but no books, and a class without magic. {@link PlayerClassNoMagicTest} covers
 * the null-magic constructor; the no-magic case here pins the answer for the {@code NONE}
 * sentinel by the same route.
 */
@ExtendWith(SeededPlayerRegistry.class)
@DisplayName("ObjectKind.canBrowse")
class ObjectKindCanBrowseTest {

    private Player savedPlayer;

    /**
     * A book of the given type whose resolved sval is fixed, bypassing the registry lookup that
     * normally fills it.
     */
    private static MagicBook book(TValue tval, int sval) throws ReflectiveOperationException {
        MagicBook book = new MagicBook(tval, false, "test book", 0, null);
        Field field = MagicBook.class.getDeclaredField("sVal");
        field.setAccessible(true);
        field.setInt(book, sval);
        return book;
    }

    /**
     * Makes the current player a class holding the given books.
     */
    private static void playAsClassWith(List<MagicBook> books) throws ReflectiveOperationException {
        ClassMagic magic = books == null ? null : new ClassMagic(1, 300, books.size(), books);
        PlayerClass playerClass = new PlayerClass("Test Class", List.of(), new HashMap<Stats, Integer>(),
                new HashMap<PlayerSkill, Integer>(), new HashMap<>(), 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(), magic);
        Player player = new Player();
        Field field = Player.class.getDeclaredField("playerClass");
        field.setAccessible(true);
        field.set(player, playerClass);
        GameState.setPlayer(player);
    }

    /**
     * A kind of the given type and sval.
     */
    private static ObjectKind kind(TValue tval, int sval) {
        ObjectKind kind = new ObjectKind(null, 0, 0, 0, 0, "test", tval, "test", null, false);
        kind.setsVal(sval);
        return kind;
    }

    @BeforeEach
    void rememberPlayer() {
        savedPlayer = GameState.getPlayer();
    }

    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    @Test
    @DisplayName("a kind matching a class book on tval and sval can be browsed")
    void matchOnBothHalves() throws Exception {
        playAsClassWith(List.of(book(TValue.TV_MAGIC_BOOK, 1)));

        assertTrue(kind(TValue.TV_MAGIC_BOOK, 1).canBrowse());
    }

    @Test
    @DisplayName("the right tval with the wrong sval cannot be browsed")
    void wrongSval() throws Exception {
        playAsClassWith(List.of(book(TValue.TV_MAGIC_BOOK, 1)));

        assertFalse(kind(TValue.TV_MAGIC_BOOK, 2).canBrowse());
    }

    @Test
    @DisplayName("the right sval with the wrong tval cannot be browsed")
    void wrongTval() throws Exception {
        playAsClassWith(List.of(book(TValue.TV_MAGIC_BOOK, 1)));

        assertFalse(kind(TValue.TV_PRAYER_BOOK, 1).canBrowse());
    }

    @Test
    @DisplayName("a match on a later book is found; the loop does not stop at the first")
    void matchOnLaterBook() throws Exception {
        List<MagicBook> books = new ArrayList<>();
        books.add(book(TValue.TV_MAGIC_BOOK, 1));
        books.add(book(TValue.TV_MAGIC_BOOK, 2));
        books.add(book(TValue.TV_MAGIC_BOOK, 3));
        playAsClassWith(books);

        assertTrue(kind(TValue.TV_MAGIC_BOOK, 3).canBrowse());
        assertFalse(kind(TValue.TV_MAGIC_BOOK, 4).canBrowse());
    }

    @Test
    @DisplayName("a class with magic but an empty book list can browse nothing")
    void emptyBookList() throws Exception {
        playAsClassWith(List.of());

        assertFalse(kind(TValue.TV_MAGIC_BOOK, 1).canBrowse());
    }

    @Test
    @DisplayName("a class without magic can browse nothing")
    void noMagic() throws Exception {
        playAsClassWith(null);

        assertFalse(kind(TValue.TV_MAGIC_BOOK, 1).canBrowse());
    }
}
