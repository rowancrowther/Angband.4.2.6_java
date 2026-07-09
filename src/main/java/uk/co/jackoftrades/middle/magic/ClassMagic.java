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

import java.util.ArrayList;
import java.util.List;

/**
 * A player class's spellcasting profile: when it can first cast, the weight its
 * spellbooks contribute to encumbrance, and the set of {@link MagicBook}s it can
 * use. This is the Java port of the C original's {@code struct class_magic}
 * ({@code src/player.h}).
 *
 * @author Rowan Crowther
 */
public class ClassMagic {
    /**
     * The character level at which this class can first cast spells.
     *
     * @author Rowan Crowther
     */
    private int firstSpellLevel;
    /**
     * Weight contributed by each spellbook (affects encumbrance/casting).
     *
     * @author Rowan Crowther
     */
    private int spellWeight;
    /**
     * Number of spellbooks this class uses.
     *
     * @author Rowan Crowther
     */
    private int numBooks;
    /**
     * The spellbooks available to this class.
     *
     * @author Rowan Crowther
     */
    List<MagicBook> magicBooks;
    /**
     * Running total of spells across all books (maintained by {@link #addMagicBook}).
     *
     * @author Rowan Crowther
     */
    private int totalSpells;

    /**
     * Build an empty class-magic profile with the given casting parameters.
     *
     * @param firstSpellLevel level at which casting becomes possible
     * @param spellWeight     per-book weight
     * @param numBooks        number of books used
     * @author Rowan Crowther
     */
    public ClassMagic(int firstSpellLevel, int spellWeight, int numBooks) {
        this.firstSpellLevel = firstSpellLevel;
        this.spellWeight = spellWeight;
        this.numBooks = numBooks;
        this.magicBooks = new ArrayList<>();
        this.totalSpells = 0;
    }

    /**
     * Add a spellbook to this profile and accumulate its spell count into
     * {@link #totalSpells}.
     *
     * @param magicBook the book to add
     * @author Rowan Crowther
     */
    public void addMagicBook(MagicBook magicBook) {
        this.magicBooks.add(magicBook);
        this.totalSpells += magicBook.getNumOfSpells();
    }
}