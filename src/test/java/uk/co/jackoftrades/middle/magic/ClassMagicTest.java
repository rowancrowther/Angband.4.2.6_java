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

package uk.co.jackoftrades.middle.magic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ClassMagic}, the port of C's {@code struct class_magic} — when a class can cast, how
 * much armour it may wear before its mana suffers, and the books it casts from.
 *
 * <p><b>The flattened index space is the thing under test.</b> A class's books are not independent
 * lists: they form one continuous index run, book 0's spells first and the count carrying on into
 * book 1 rather than restarting ({@code spell_by_index}, {@code player-spell.c}). That index is what
 * a queued spell command stores, so it has to survive a round trip — the number written now must
 * resolve to the same spell when the command is replayed later. An off-by-one at a book boundary
 * would cast the wrong spell, and only at the boundary, which is exactly the kind of fault that
 * survives casual play.
 *
 * <p>The rest of the class is small, but {@code totalSpells} earns a look because it is summed once
 * at construction and then relied on as the literacy test — {@code calcMana} treats a zero total as
 * "cannot cast" and returns before it considers levels or armour at all
 * ({@code player-calcs.c:1484-1488}).
 *
 * <p>Class ClassMagicTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
class ClassMagicTest {

    /**
     * A spell distinguishable from every other by name and by identity.
     *
     * @param name the spell's name
     * @return the spell
     */
    private static MagicSpell spell(String name) {
        return new MagicSpell(name, 1, 25, 1, 0, List.of(), "");
    }

    /**
     * A book holding the named spells, in order.
     *
     * @param name   the book's name
     * @param spells its spells
     * @return the book
     */
    private static MagicBook book(String name, MagicSpell... spells) {
        List<MagicSpell> list = new ArrayList<>(List.of(spells));
        return new MagicBook(TValue.TV_MAGIC_BOOK, name, false, list.size(),
                new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell", TValue.TV_MAGIC_BOOK),
                null, 0, 0, 0, 0, list);
    }

    /**
     * The index space that runs across book boundaries.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("flattened spell index")
    class FlattenedIndex {

        /**
         * The count continues into the second book rather than restarting — the property that makes
         * it a single index space. Two books of two spells give indices 0 to 3, and index 2 is the
         * second book's <em>first</em> spell.
         */
        @Test
        @DisplayName("indices run across the book boundary")
        void indicesRunAcrossBooks() {
            MagicSpell a = spell("a");
            MagicSpell b = spell("b");
            MagicSpell c = spell("c");
            MagicSpell d = spell("d");
            ClassMagic magic = new ClassMagic(1, 300, 2,
                    List.of(book("first", a, b), book("second", c, d)));

            assertAll(
                    () -> assertSame(a, magic.spellByIndex(0)),
                    () -> assertSame(b, magic.spellByIndex(1)),
                    () -> assertSame(c, magic.spellByIndex(2)),
                    () -> assertSame(d, magic.spellByIndex(3)));
        }

        /**
         * The two directions have to agree for every spell, because a command records the index and
         * resolves it again later. Checking the round trip across a boundary is the point: a
         * consistent off-by-one in both directions would still be wrong the moment anything else
         * computed an index.
         */
        @Test
        @DisplayName("index and lookup are inverses, boundary included")
        void roundTrip() {
            MagicSpell a = spell("a");
            MagicSpell b = spell("b");
            MagicSpell c = spell("c");
            ClassMagic magic = new ClassMagic(1, 300, 2,
                    List.of(book("first", a, b), book("second", c)));

            assertAll(
                    () -> assertSame(a, magic.spellByIndex(magic.indexOfSpell(a))),
                    () -> assertSame(b, magic.spellByIndex(magic.indexOfSpell(b))),
                    () -> assertSame(c, magic.spellByIndex(magic.indexOfSpell(c))),
                    () -> assertEquals(2, magic.indexOfSpell(c)));
        }

        /**
         * An index outside the run resolves to null rather than throwing, matching C's {@code NULL}
         * return — a stale command that outlived a change of class must fail softly.
         */
        @Test
        @DisplayName("an out-of-range index resolves to null")
        void outOfRangeIsNull() {
            ClassMagic magic = new ClassMagic(1, 300, 1, List.of(book("only", spell("a"))));

            assertAll(
                    () -> assertNull(magic.spellByIndex(-1)),
                    () -> assertNull(magic.spellByIndex(1)),
                    () -> assertNull(magic.spellByIndex(99)));
        }

        /**
         * A spell from another class's books belongs to no index here, and says so with
         * {@code -1} rather than pointing at whichever spell happens to sit at position zero.
         */
        @Test
        @DisplayName("a spell from no book of this class indexes as -1")
        void foreignSpellIsMinusOne() {
            ClassMagic magic = new ClassMagic(1, 300, 1, List.of(book("only", spell("a"))));

            assertEquals(-1, magic.indexOfSpell(spell("elsewhere")));
        }

        /**
         * Matching is by identity, not by name. Two spells that happen to share a name are different
         * spells, and resolving one to the other's index would cast the wrong thing.
         */
        @Test
        @DisplayName("spells are matched by identity, not by name")
        void matchedByIdentity() {
            MagicSpell real = spell("magic missile");
            ClassMagic magic = new ClassMagic(1, 300, 1, List.of(book("only", real)));

            assertAll(
                    () -> assertEquals(0, magic.indexOfSpell(real)),
                    () -> assertEquals(-1, magic.indexOfSpell(spell("magic missile"))));
        }
    }

    /**
     * The stored figures and the non-caster case.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("casting profile")
    class Profile {

        /**
         * The total is summed across the books at construction rather than declared, so a book list
         * and a count that disagreed would resolve in the books' favour.
         */
        @Test
        @DisplayName("the total is the sum of the books' spell counts")
        void totalIsSummed() {
            ClassMagic magic = new ClassMagic(5, 300, 2,
                    List.of(book("first", spell("a"), spell("b")), book("second", spell("c"))));

            assertEquals(3, magic.getTotalSpells());
        }

        /**
         * Each figure is read back by its own accessor. Distinct values because {@code spellFirst}
         * and {@code numBooks} are both small integers and a crossed pair would otherwise pass.
         */
        @Test
        @DisplayName("the three declared figures are kept apart")
        void figuresKeptApart() {
            ClassMagic magic = new ClassMagic(5, 300, 2,
                    List.of(book("first", spell("a")), book("second", spell("b"))));

            assertAll(
                    () -> assertEquals(5, magic.getSpellFirst()),
                    () -> assertEquals(300, magic.getSpellWeight()),
                    () -> assertEquals(2, magic.getNumBooks()));
        }

        /**
         * The book list is copied in, so the caller's list cannot be added to afterwards and change
         * a class's spells behind its back.
         */
        @Test
        @DisplayName("the book list is copied, not shared")
        void booksAreCopied() {
            List<MagicBook> books = new ArrayList<>(List.of(book("first", spell("a"))));
            ClassMagic magic = new ClassMagic(1, 300, 1, books);

            books.add(book("sneaked in", spell("b")));

            assertAll(
                    () -> assertNotSame(books, magic.getMagicBooks()),
                    () -> assertEquals(1, magic.getMagicBooks().size()));
        }

        /**
         * The shared non-caster sentinel exists so that a class's magic is never null and callers
         * can ask {@code isCaster} instead of testing for one. Its total of zero is what
         * {@code calcMana} reads as illiteracy.
         */
        @Test
        @DisplayName("the NONE sentinel is a literate-looking object that cannot cast")
        void noneSentinel() {
            assertAll(
                    () -> assertEquals(0, ClassMagic.NONE.getTotalSpells()),
                    () -> assertEquals(0, ClassMagic.NONE.getNumBooks()),
                    () -> assertTrue(ClassMagic.NONE.getMagicBooks().isEmpty()),
                    () -> assertEquals(false, ClassMagic.NONE.isCaster()),
                    () -> assertNull(ClassMagic.NONE.spellByIndex(0)));
        }
    }
}
