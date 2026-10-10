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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.List;

/**
 * One spellbook of a class: the Java copy of C's {@code struct class_book} ({@code player.h}),
 * which a class lists under {@code magic.books}. It holds the book's {@code tval} and {@code sval},
 * whether it is a dungeon book, its {@link MagicRealm} and its {@link MagicSpell}s.
 *
 * <p>The port differs from the struct in three ways. The book's name, which C keeps only on the
 * object kind that {@code write_book_kind} ({@code init.c}) synthesizes for it, is kept here as
 * well, and it is what {@link #setSVal()} uses to find that kind. C's {@code num_spells} counter,
 * which {@code parse_class_spell} bumps as each spell line is read, is set once from the size of the
 * spell list. The graphics and loot properties that C writes straight onto the object kind
 * ({@code parse_class_book_graphics}, {@code parse_class_book_properties}) are also copied onto the
 * book, but nothing reads those copies.
 *
 * <p>Instances are built by
 * {@link uk.co.jackoftradesltd.backend.parser.playerclass.ClassSpellBookAssembler}, which calls
 * {@link #setSVal()} once the backing kind is registered. The class has no setters, so apart from
 * that call and the mutable spell list a book does not change after it is built.
 *
 * <p>Class MagicBook coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class MagicBook {
    /**
     * The item type of the book — C's {@code class_book.tval}. Together with {@link #sVal} it is the
     * key that matches a carried object to this book (C's {@code player_object_to_book} and
     * {@code object_kind_to_book}). The backing object kind is registered separately by the
     * assembler, so the book only needs to remember its type here.
     *
     * <p>Field bookType coded before 261010, commented in full on 261010.
     */
    private TValue bookType;

    /**
     * The sub-type of the object kind this book is realized as — C's {@code class_book.sval}, the
     * second half of the {@code tval}/{@code sval} pair. C sets it inside {@code write_book_kind}
     * while the {@code book:} line is parsed; here {@link #setSVal()} fills it in afterwards, so it
     * is {@code 0} (Java's default) between construction and that call. {@code -1} marks a book
     * whose kind could not be found, a state C cannot reach.
     *
     * <p>Field sVal coded before 261010, commented in full on 261010.
     */
    private int sVal;

    /**
     * The book's name as it appears in {@code class.txt}, for example {@code [Magic for Beginners]}.
     * C has no such member on {@code class_book}: it stores the name only as {@code kind->name} on
     * the synthesized object kind. It is the key {@link #setSVal()} uses to find that kind.
     *
     * <p>Field bookName coded before 261010, commented in full on 261010.
     */
    private String bookName;

    /**
     * Whether this is a dungeon-only book — C's {@code class_book.dungeon}, set when the
     * {@code book:} line's quality field is {@code dungeon}. The flag is also what makes the backing
     * kind {@code KF_GOOD} and element-ignoring, but the assembler does that when it builds the kind.
     *
     * <p>Field dungeon coded before 261010, commented in full on 261010.
     */
    private boolean dungeon;

    /**
     * The number of spells the book holds — C's {@code class_book.num_spells}. C counts it up one
     * {@code spell:} line at a time; here it is fixed at construction as the size of the spell list,
     * not read from the {@code spells} count on the {@code book:} line. It is not kept in step if
     * the list returned by {@link #getSpells()} is later changed.
     *
     * <p>Field numOfSpells coded before 261010, commented in full on 261010.
     */
    private int numOfSpells;

    /**
     * The magic realm this book's spells belong to — C's {@code class_book.realm}, resolved from the
     * realm name on the {@code book:} line.
     *
     * <p>Field realm coded before 261010, commented in full on 261010.
     */
    private MagicRealm realm;

    /**
     * The glyph and colour from the {@code book-graphics:} line. C writes these to
     * {@code kind->d_char} and {@code kind->d_attr}, not to {@code class_book}, so this is a copy
     * for the book's own use and there is no getter. The assembler always supplies a value, falling
     * back to a red {@code *} when the line is absent, so it is {@code null} only for a book built
     * by hand. A book listed by a second class carries that default here, whatever graphics the
     * shared kind ended up with.
     *
     * <p>Field character coded before 261010, commented in full on 261010.
     */
    private AngbandDisplayCharacter character;

    /**
     * The base cost in gold from the {@code book-properties:} line. C writes it to {@code kind->cost};
     * this is an unread copy.
     *
     * <p>Field cost coded before 261010, commented in full on 261010.
     */
    private int cost;

    /**
     * How commonly the book is generated, from the {@code book-properties:} line. C writes it to
     * {@code kind->alloc_prob}; this is an unread copy.
     *
     * <p>Field commonness coded before 261010, commented in full on 261010.
     */
    private int commonness;

    /**
     * The shallowest dungeon depth at which the book is generated, from the {@code book-properties:}
     * line. C writes it to {@code kind->alloc_min}, and also to {@code kind->level}; this is an
     * unread copy.
     *
     * <p>Field min coded before 261010, commented in full on 261010.
     */
    private int min;

    /**
     * The deepest dungeon depth at which the book is generated, from the {@code book-properties:}
     * line. C writes it to {@code kind->alloc_max}; this is an unread copy.
     *
     * <p>Field max coded before 261010, commented in full on 261010.
     */
    private int max;

    /**
     * The spells in this book, in book order — C's {@code class_book.spells} array. The list is
     * stored as passed, not copied.
     *
     * <p>Field spells coded before 261010, commented in full on 261010.
     */
    private List<MagicSpell> spells;

    /**
     * Builds a fully-populated spellbook, the form the class loader produces: its graphics, loot
     * properties and resolved spells are all supplied up front. {@link #numOfSpells} is taken from
     * {@code spells.size()}, and {@link #sVal} is left unresolved until {@link #setSVal()} runs.
     *
     * <p>Where C fills a zeroed {@code class_book} in over several parser callbacks, this takes the
     * result of all of them at once. The graphics and loot arguments have no counterpart on the
     * struct; see the fields of the same names.
     *
     * <p>Constructor MagicBook coded before 261010, commented in full on 261010.
     *
     * @param bookType   the book's item type ({@code tval})
     * @param bookName   the book's display name
     * @param dungeon    whether it is a dungeon-only book
     * @param realm      the magic realm the book's spells belong to
     * @param character  the display glyph and colour, or {@code null} if none is supplied
     * @param cost       the base cost in gold
     * @param commonness the loot-generation commonness
     * @param min        the shallowest depth of occurrence
     * @param max        the deepest depth of occurrence
     * @param spells     the spells contained in the book; must not be {@code null}
     */
    public MagicBook(TValue bookType, String bookName, boolean dungeon,
                     MagicRealm realm,
                     AngbandDisplayCharacter character, int cost,
                     int commonness, int min, int max, List<MagicSpell> spells) {
        this.bookType = bookType;
        this.bookName = bookName;
        this.dungeon = dungeon;
        this.numOfSpells = spells.size();
        this.realm = realm;
        this.character = character;
        this.cost = cost;
        this.commonness = commonness;
        this.min = min;
        this.max = max;
        this.spells = spells;
    }

    /**
     * Resolves {@link #sVal} by looking this book's backing kind up in the object registry by tval
     * and name, setting it to {@code -1} if no kind matches.
     *
     * <p>This is the sval half of C's {@code write_book_kind} ({@code init.c}), which scans
     * {@code k_info} for a kind of that name and copies its {@code sval}, or else registers a new
     * kind and copies the sval it was given. The registering half lives in
     * {@link uk.co.jackoftradesltd.backend.parser.playerclass.ClassSpellBookAssembler}, which must
     * have registered the kind before this runs; the registry throws {@link IllegalStateException}
     * if no kinds are loaded at all.
     *
     * <p>Two differences from C. C matches on the name alone, whichever tval the kind has, where
     * this also requires the tval to match, so a name already in use under another tval gives
     * {@code -1} here and that kind's sval in C. The data has no such name. And the registry
     * compares the name with the kind's {@code sValueName} case-insensitively, after stripping
     * the {@code &} and {@code ~} markers, where C uses an exact {@code streq} on the whole name.
     * No book name in {@code class.txt} contains a marker or differs only by case.
     *
     * <p>Function setSVal coded before 261010, commented in full on 261010.
     */
    public void setSVal() {
        ObjectKind bookKind = ObjectRegistry.lookupObjectKind(bookType, bookName);
        if (bookKind == null)
            sVal = -1;
        else
            sVal = bookKind.getsVal();
    }

    /**
     * Returns the number of spells in the book — C's {@code class_book.num_spells}, which
     * {@link ClassMagic} sums into the class's {@code total_spells}. It is the size of the spell
     * list at construction, not the count declared on the {@code book:} line.
     *
     * <p>Function getNumOfSpells coded before 261010, commented in full on 261010.
     *
     * @return the number of spells in this book
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getNumOfSpells() {
        return numOfSpells;
    }

    /**
     * Returns the book's name as written in {@code class.txt}. C has no accessor for it: the name
     * lives on the object kind, as {@code kind->name}.
     *
     * <p>Function getBookName coded before 261010, commented in full on 261010.
     *
     * @return the book's name
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * Returns the book's item type — C's {@code class_book.tval}.
     *
     * <p>Function getBookTValue coded before 261010, commented in full on 261010.
     *
     * @return the book's item type ({@code tval})
     */
    public TValue getBookTValue() {
        return bookType;
    }

    /**
     * Returns whether this is a dungeon book — C's {@code class_book.dungeon}.
     *
     * <p>Function isDungeon coded before 261010, commented in full on 261010.
     *
     * @return {@code true} for a dungeon-only book, {@code false} for a town book
     */
    public boolean isDungeon() {
        return dungeon;
    }

    /**
     * Returns the book's spells in book order — C's {@code class_book.spells}. The list is the one
     * the book holds, not a copy. Each spell's position here feeds the class's flattened spell index;
     * see {@link uk.co.jackoftradesltd.middle.magic.ClassMagic#spellByIndex}.
     *
     * <p>Function getSpells coded before 261010, commented in full on 261010.
     *
     * @return the spells this book holds
     */
    public List<MagicSpell> getSpells() {
        return spells;
    }

    /**
     * Returns the sub-type value of the object kind that backs this book — C's
     * {@code class_book.sval}. Used with {@link #getBookTValue() tval} to match a carried
     * {@link uk.co.jackoftradesltd.middle.objects.ItemObject} to its book, as C's
     * {@code player_object_to_book} does. It is {@code 0} until {@link #setSVal()} has run, and
     * {@code -1} if that call found no kind.
     *
     * <p>Function getSval coded before 261010, commented in full on 261010.
     *
     * @return the book's sval
     */
    public int getSval() {
        return sVal;
    }

    /**
     * The realm this book's spells belong to — C's {@code book->realm}. Determines the casting stat,
     * the verb and the noun used for its spells, and through {@code class_magic_realms} which stats
     * a class's mana is averaged over.
     *
     * <p>Function getRealm coded before 261010, commented in full on 261010.
     *
     * @return this book's magic realm
     */
    public MagicRealm getRealm() {
        return realm;
    }
}