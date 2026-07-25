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
     * Total of all spells across all the books for this magic class
     *
     * @author Rowan Crowther
     */
    private int totalSpells;

    /**
     * The shared "no magic" sentinel assigned to non-caster classes (Warrior), so callers can rely
     * on {@code magic} never being {@code null} and simply test {@link #isCaster()}.
     *
     * @author Rowan Crowther
     */
    public static final ClassMagic NONE = new ClassMagic(0, 0, 0, List.of());

    /**
     * @return {@code true} if this class can cast — i.e. it defines at least one spellbook
     * @author Rowan Crowther
     */
    public boolean isCaster() {
        return numBooks > 0;
    }

    /**
     * Build a class-magic profile from its casting parameters and books, summing the books' spell
     * counts into {@link #totalSpells}.
     *
     * @param firstSpellLevel level at which casting becomes possible
     * @param spellWeight     per-book weight
     * @param numBooks        number of books used
     * @param books           the spellbooks available to the class (defensively copied)
     * @author Rowan Crowther
     */
    public ClassMagic(int firstSpellLevel, int spellWeight, int numBooks, List<MagicBook> books) {
        this.firstSpellLevel = firstSpellLevel;
        this.spellWeight = spellWeight;
        this.numBooks = numBooks;
        this.magicBooks = new ArrayList<>(books);
        this.totalSpells = 0;

        for (MagicBook magicBook : magicBooks) {
            totalSpells += magicBook.getNumOfSpells();
        }
    }

    /**
     * @return the spellbooks available to this class, in class-load order - the order the flattened
     * spell-index space runs across (see {@link #spellByIndex(int)})
     * @author Rowan Crowther
     */
    public List<MagicBook> getMagicBooks() {
        return magicBooks;
    }

    /**
     * Returns the flattened index of {@code spell} across this class's books - the inverse of
     * {@link #spellByIndex(int)}. The books form a single index space (book 0's spells first, then
     * book 1's, and so on), so the result is the number of spells in all earlier books plus the
     * spell's position within its own book. This is the value {@code Command.getSpell} stores as an
     * {@code arg_CHOICE} so a queued spell can be re-resolved on a later replay.
     *
     * @param spell the spell to locate, matched by identity
     * @return the spell's flattened index, or {@code -1} if it belongs to none of this class's books
     * @author Rowan Crowther
     */
    public int indexOfSpell(MagicSpell spell) {
        boolean spellFound = false;
        int spellIndex = 0;
        for (MagicBook magicBook : magicBooks) {
            if (magicBook.getSpells().contains(spell)) {
                spellFound = true;
                spellIndex += magicBook.getSpells().indexOf(spell);
                break;
            }
            spellIndex += magicBook.getSpells().size();
        }
        if (spellFound)
            return spellIndex;
        return -1;
    }

    /**
     * Resolves a flattened spell index back to its {@link MagicSpell} - the port of C's
     * {@code spell_by_index} ({@code player-spell.c}) and the inverse of
     * {@link #indexOfSpell(MagicSpell)}. The index counts across book boundaries in book order, so
     * index 0 is the first book's first spell and the count continues into the next book rather than
     * restarting. An out-of-range index - negative, or at or beyond {@link #totalSpells} - resolves
     * to {@code null}, matching C's {@code NULL} return.
     *
     * @param spellIndex the flattened index into this class's books
     * @return the spell at that index, or {@code null} if the index is out of range
     * @author Rowan Crowther
     */
    public MagicSpell spellByIndex(int spellIndex) {
        if (spellIndex < 0 || spellIndex >= totalSpells) return null;

        for (MagicBook magicBook : magicBooks) {
            if (spellIndex < magicBook.getSpells().size())
                return magicBook.getSpells().get(spellIndex);

            spellIndex -= magicBook.getSpells().size();
        }

        return null;
    }
}