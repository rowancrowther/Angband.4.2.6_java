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

package uk.co.jackoftradesltd.middle.magic;

import org.junit.jupiter.api.*;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link MagicBook}, the port of C's {@code struct class_book} ({@code player.h}) as filled
 * in by {@code parse_class_book}, {@code parse_class_spell} and {@code write_book_kind}
 * ({@code init.c}).
 *
 * <p>The expected values come from those C functions, not from the Java. {@code num_spells} is the
 * number of {@code spell:} lines read, so a book with three spells reports three. {@code sval} is
 * the base's running {@code num_svals} count when the book's kind is created, counted from one
 * within each tval and shared between books of the same name, so the first magic book is sval 1, the
 * next is 2, and the first prayer book is sval 1 again.
 *
 * <p>The registry is global static state also touched by other suites, so {@link #snapshot()} and
 * {@link #restore()} save and put back every field {@link #isolate()} writes.
 *
 * <p>Class MagicBookTest coded on 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
@DisplayName("MagicBook")
class MagicBookTest {

    private static final List<String> TOUCHED = List.of("objectKinds", "kindsByTvalSval", "objectBases");
    private static final Map<String, Object> saved = new HashMap<>();

    private ObjectBase magicBase;
    private ObjectBase prayerBase;

    @BeforeAll
    static void snapshot() throws Exception {
        for (String name : TOUCHED) {
            saved.put(name, registryField(name).get(null));
        }
    }

    @AfterAll
    static void restore() throws Exception {
        for (String name : TOUCHED) {
            registryField(name).set(null, saved.get(name));
        }
    }

    @BeforeEach
    void isolate() throws Exception {
        registryField("objectKinds").set(null, new ArrayList<ObjectKind>());
        registryField("kindsByTvalSval").set(null, new HashMap<TValue, Map<Integer, ObjectKind>>());
        magicBase = base(TValue.TV_MAGIC_BOOK, "magic book");
        prayerBase = base(TValue.TV_PRAYER_BOOK, "prayer book");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(magicBase, prayerBase)));
    }

    private static Field registryField(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static ObjectBase base(TValue tval, String name) {
        return new ObjectBase(tval, name, ColourEnum.COLOUR_RED,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), -1, -1);
    }

    /**
     * Registers a kind the way the assembler does for a spellbook: the name is the book's name, and
     * {@link ObjectRegistry#addObjectKind} hands it the next sval under its base.
     */
    private static ObjectKind register(String name, ObjectBase base) throws Exception {
        ObjectKind kind = new ObjectKind();
        Map<String, Object> values = Map.of(
                "name", name, "base", base, "tValue", base.gettVal(), "sValueName", name);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Field f = ObjectKind.class.getDeclaredField(entry.getKey());
            f.setAccessible(true);
            f.set(kind, entry.getValue());
        }
        ObjectRegistry.addObjectKind(kind);
        return kind;
    }

    private static MagicRealm arcane() {
        return new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell", "magic book");
    }

    private static List<MagicSpell> spells(int count) {
        List<MagicSpell> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new MagicSpell("spell" + i, 1, 25, 1, 0, List.of(), ""));
        }
        return list;
    }

    private static MagicBook book(TValue tval, String name, boolean dungeon, List<MagicSpell> spells) {
        return new MagicBook(tval, name, dungeon, arcane(), null, 0, 0, 0, 0, spells);
    }

    @Nested
    @DisplayName("construction")
    class Construction {

        @Test
        @DisplayName("keeps the fields C's class_book holds")
        void keepsTheClassBookFields() {
            List<MagicSpell> list = spells(2);
            MagicRealm realm = arcane();
            MagicBook b = new MagicBook(TValue.TV_MAGIC_BOOK, "[Magic for Beginners]", false,
                    realm, null, 25, 100, 0, 5, list);

            assertAll(
                    () -> assertEquals(TValue.TV_MAGIC_BOOK, b.getBookTValue()),
                    () -> assertEquals("[Magic for Beginners]", b.getBookName()),
                    () -> assertFalse(b.isDungeon()),
                    () -> assertSame(realm, b.getRealm()),
                    () -> assertSame(list, b.getSpells()));
        }

        @Test
        @DisplayName("a dungeon book reports dungeon")
        void dungeonBook() {
            assertTrue(book(TValue.TV_MAGIC_BOOK, "[Arcane Control]", true, spells(1)).isDungeon());
        }

        @Test
        @DisplayName("num_spells is the number of spell lines read: 0, 1 and 3")
        void numSpellsIsTheSpellCount() {
            assertAll(
                    () -> assertEquals(0, book(TValue.TV_MAGIC_BOOK, "none", false, spells(0)).getNumOfSpells()),
                    () -> assertEquals(1, book(TValue.TV_MAGIC_BOOK, "one", false, spells(1)).getNumOfSpells()),
                    () -> assertEquals(3, book(TValue.TV_MAGIC_BOOK, "three", false, spells(3)).getNumOfSpells()));
        }

        @Test
        @DisplayName("the sval is 0 until setSVal runs")
        void svalIsZeroBeforeSetSVal() {
            assertEquals(0, book(TValue.TV_MAGIC_BOOK, "[Magic for Beginners]", false, spells(1)).getSval());
        }
    }

    @Nested
    @DisplayName("setSVal")
    class SetSVal {

        @Test
        @DisplayName("the first kind of a tval is sval 1, as num_svals counts from one")
        void firstKindIsSvalOne() throws Exception {
            register("[Magic for Beginners]", magicBase);
            MagicBook b = book(TValue.TV_MAGIC_BOOK, "[Magic for Beginners]", false, spells(1));

            b.setSVal();

            assertEquals(1, b.getSval());
        }

        @Test
        @DisplayName("each book gets the sval of its own kind, in registration order")
        void eachBookGetsItsOwnSval() throws Exception {
            register("[Magic for Beginners]", magicBase);
            register("[Conjurings and Tricks]", magicBase);
            register("[Incantations and Illusions]", magicBase);
            MagicBook first = book(TValue.TV_MAGIC_BOOK, "[Magic for Beginners]", false, spells(1));
            MagicBook second = book(TValue.TV_MAGIC_BOOK, "[Conjurings and Tricks]", false, spells(1));
            MagicBook third = book(TValue.TV_MAGIC_BOOK, "[Incantations and Illusions]", false, spells(1));

            first.setSVal();
            second.setSVal();
            third.setSVal();

            assertAll(
                    () -> assertEquals(1, first.getSval()),
                    () -> assertEquals(2, second.getSval()),
                    () -> assertEquals(3, third.getSval()));
        }

        @Test
        @DisplayName("svals count per tval: the first prayer book is sval 1 as well")
        void svalsCountPerTval() throws Exception {
            register("[Magic for Beginners]", magicBase);
            register("[Novice's Handbook]", prayerBase);
            MagicBook magic = book(TValue.TV_MAGIC_BOOK, "[Magic for Beginners]", false, spells(1));
            MagicBook prayer = book(TValue.TV_PRAYER_BOOK, "[Novice's Handbook]", false, spells(1));

            magic.setSVal();
            prayer.setSVal();

            assertAll(
                    () -> assertEquals(1, magic.getSval()),
                    () -> assertEquals(1, prayer.getSval()),
                    () -> assertTrue(magic.getBookTValue() != prayer.getBookTValue()));
        }

        @Test
        @DisplayName("two books of one name resolve to the same sval, as write_book_kind reuses the kind")
        void sameNameSharesTheKind() throws Exception {
            register("[Magic for Beginners]", magicBase);
            register("[Conjurings and Tricks]", magicBase);
            MagicBook mage = book(TValue.TV_MAGIC_BOOK, "[Conjurings and Tricks]", false, spells(2));
            MagicBook rogue = book(TValue.TV_MAGIC_BOOK, "[Conjurings and Tricks]", false, spells(1));

            mage.setSVal();
            rogue.setSVal();

            assertAll(
                    () -> assertEquals(2, mage.getSval()),
                    () -> assertEquals(2, rogue.getSval()));
        }

        @Test
        @DisplayName("a name with no kind gives -1")
        void unknownNameGivesMinusOne() throws Exception {
            register("[Magic for Beginners]", magicBase);
            MagicBook b = book(TValue.TV_MAGIC_BOOK, "[Not A Book]", false, spells(1));

            b.setSVal();

            assertEquals(-1, b.getSval());
        }

        @Test
        @DisplayName("a name registered under another tval gives -1 (C matches on the name alone)")
        void nameUnderAnotherTvalGivesMinusOne() throws Exception {
            register("[Novice's Handbook]", prayerBase);
            MagicBook b = book(TValue.TV_MAGIC_BOOK, "[Novice's Handbook]", false, spells(1));

            b.setSVal();

            assertEquals(-1, b.getSval());
        }

        @Test
        @DisplayName("calling it again after a later registration picks up the new kind")
        void canBeRerun() throws Exception {
            MagicBook b = book(TValue.TV_MAGIC_BOOK, "[Conjurings and Tricks]", false, spells(1));
            register("[Magic for Beginners]", magicBase);

            b.setSVal();
            assertEquals(-1, b.getSval());

            register("[Conjurings and Tricks]", magicBase);
            b.setSVal();
            assertEquals(2, b.getSval());
        }

        @Test
        @DisplayName("with no kinds loaded the registry throws rather than answering -1")
        void unloadedRegistryThrows() {
            MagicBook b = book(TValue.TV_MAGIC_BOOK, "[Magic for Beginners]", false, spells(1));

            assertThrows(IllegalStateException.class, b::setSVal);
        }
    }
}
