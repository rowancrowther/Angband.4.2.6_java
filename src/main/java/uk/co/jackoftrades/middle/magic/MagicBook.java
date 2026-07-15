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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

/**
 * A spellbook: the item that holds a set of {@link MagicSpell}s for a given
 * {@link MagicRealm}. Tracks the backing item kind, name, whether it is a
 * dungeon-only book, and the spells it contains. This is the Java port of the C
 * original's class spellbook data.
 *
 * @author Rowan Crowther
 */
public class MagicBook {
    /**
     * The item type ({@code tval}) of the book. The concrete object kind is synthesised and
     * registered separately (see {@link uk.co.jackoftrades.backend.parser.playerclass.ClassSpellBookAssembler}),
     * so the book itself only needs to remember its type here.
     *
     * @author Rowan Crowther
     */
    private TValue bookType;
    /**
     * The book's display name.
     *
     * @author Rowan Crowther
     */
    private String bookName;
    /**
     * Whether this is a dungeon-only book (not sold in town).
     *
     * @author Rowan Crowther
     */
    private boolean dungeon;
    /**
     * The number of spells the book holds.
     *
     * @author Rowan Crowther
     */
    private int numOfSpells;
    /**
     * The magic realm this book's spells belong to.
     *
     * @author Rowan Crowther
     */
    private MagicRealm realm;

    /**
     * The book's display glyph and colour (from {@code book-graphics:}), or {@code null} when the
     * book re-references one defined earlier and so omits its own graphics line.
     *
     * @author Rowan Crowther
     */
    private AngbandDisplayCharacter character;

    /**
     * The book's base cost in gold (from {@code book-properties:}).
     *
     * @author Rowan Crowther
     */
    private int cost;

    /**
     * How commonly the book is generated as loot (from {@code book-properties:}).
     *
     * @author Rowan Crowther
     */
    private int commonness;

    /**
     * The shallowest dungeon depth at which the book appears (from {@code book-properties:}).
     *
     * @author Rowan Crowther
     */
    private int min;

    /**
     * The deepest dungeon depth at which the book appears (from {@code book-properties:}).
     *
     * @author Rowan Crowther
     */
    private int max;

    /**
     * The spells contained in this book.
     *
     * @author Rowan Crowther
     */
    private List<MagicSpell> spells;

    /**
     * Build a fully-populated spellbook — the form the class loader produces, with its graphics,
     * loot properties and resolved spells all supplied up front.
     *
     * @param bookType    the book's item type ({@code tval})
     * @param bookName    the book's display name
     * @param dungeon     whether it is a dungeon-only book
     * @param numOfSpells the declared number of spells
     * @param realm       the magic realm the book's spells belong to
     * @param character   the display glyph/colour, or {@code null} if inherited from an earlier definition
     * @param cost        the base cost in gold
     * @param commonness  the loot-generation commonness
     * @param min         the shallowest depth of occurrence
     * @param max         the deepest depth of occurrence
     * @param spells      the spells contained in the book
     * @author Rowan Crowther
     */
    public MagicBook(TValue bookType, String bookName, boolean dungeon,
                     int numOfSpells, MagicRealm realm,
                     AngbandDisplayCharacter character, int cost,
                     int commonness, int min, int max, List<MagicSpell> spells) {
        this.bookType = bookType;
        this.bookName = bookName;
        this.dungeon = dungeon;
        this.numOfSpells = numOfSpells;
        this.realm = realm;
        this.character = character;
        this.cost = cost;
        this.commonness = commonness;
        this.min = min;
        this.max = max;
        this.spells = spells;
    }

    /**
     * Build an empty spellbook; spells are added later via {@link #addSpell(MagicSpell)}.
     *
     * @param bookType    the backing object kind
     * @param dungeon     whether it is a dungeon-only book
     * @param bookName    the book's name
     * @param numOfSpells the declared number of spells
     * @param realm       the magic realm
     * @author Rowan Crowther
     */
    @Contract(mutates = "this")
    public MagicBook(TValue bookType, boolean dungeon, String bookName, int numOfSpells, MagicRealm realm) {
        this.bookType = bookType;
        this.bookName = bookName;
        this.dungeon = dungeon;
        this.numOfSpells = numOfSpells;
        this.realm = realm;
        spells = new ArrayList<>();
    }

    /**
     * Add a spell to this book.
     *
     * @param spell the spell to add
     * @author Rowan Crowther
     */
    public void addSpell(MagicSpell spell) {
        spells.add(spell);
    }

    /**
     * @return the declared number of spells in this book
     * @author Rowan Crowther
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getNumOfSpells() {
        return numOfSpells;
    }

    /**
     * @return the book's name
     * @author Rowan Crowther
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * @return the book's item type ({@code tval})
     * @author Rowan Crowther
     */
    public TValue getBookTValue() {
        return bookType;
    }

    /**
     * Set the book's item type ({@code tval}).
     *
     * @param bookType the item type value
     * @author Rowan Crowther
     */
    public void setBookKind(TValue bookType) {
        this.bookType = bookType;
    }

    /**
     * @return whether this is a dungeon-only book
     * @author Rowan Crowther
     */
    public boolean isDungeon() {
        return dungeon;
    }
}