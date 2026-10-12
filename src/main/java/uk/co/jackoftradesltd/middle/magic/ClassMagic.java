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

import java.util.ArrayList;
import java.util.List;

/**
 * A player class's spellcasting profile: the level at which it can first cast, the armour weight it
 * may wear before its mana suffers, and the {@link MagicBook}s it casts from. This is the Java port
 * of C's {@code struct class_magic} ({@code player.h}), which {@code struct player_class} embeds as
 * its {@code magic} member. The struct's five members map to {@link #firstSpellLevel},
 * {@link #spellWeight}, {@link #numBooks}, {@link #magicBooks} and {@link #totalSpells}, in that
 * order.
 *
 * <p>The books also form one flattened spell-index space that runs across their boundaries. C
 * stamps each {@code class_spell} with its place in that run ({@code sidx}) as
 * {@code parse_class_spell} ({@code init.c}) reads it, and {@code spell_by_index}
 * ({@code player-spell.c}) walks it. Here the index is not stored on the spell:
 * {@link #indexOfSpell(MagicSpell)} works it out on demand and {@link #spellByIndex(int)} is its
 * inverse.
 *
 * <p>The figures are built differently from C. C fills a zeroed struct one parser callback at a
 * time, bumping {@code num_books} for each {@code book:} line and {@code total_spells} for each
 * {@code spell:} line. This class is built once from the finished book list, so {@link #numBooks}
 * is the list's size and {@link #totalSpells} is the sum of the books' spell counts. The count
 * declared on the {@code magic:} line, which C keeps only as the parser's limit on how many
 * {@code book:} lines it accepts, is not stored here.
 *
 * <p>Class ClassMagic coded before 261012, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
public class ClassMagic {
    /**
     * The character level at which this class can first cast spells — C's {@code spell_first}.
     * {@code calc_mana} ({@code player-calcs.c}) works out the "effective" level as the player's
     * level minus this, plus one. A caster exactly at this level therefore has one effective level
     * and the smallest non-zero allowance, and one below it has none at all, so its maximum mana is
     * zero before armour is even weighed.
     *
     * <p>Field firstSpellLevel coded before 261012, commented in full on 261012.
     */
    private int firstSpellLevel;

    /**
     * The greatest weight of armour the class may wear before its mana starts to suffer — C's
     * {@code spell_weight}, documented there as "Max armor weight to avoid mana penalties".
     *
     * <p>Despite the name this has nothing to do with the weight of spellbooks. It is an allowance,
     * in tenth-pounds: {@code calc_mana} ({@code player-calcs.c}) weighs everything worn except the
     * weapon, launcher, rings, amulet and light. Each whole ten tenth-pounds by which that total
     * exceeds this allowance costs one point of maximum mana and sets the encumbrance flag, and an
     * excess of under ten costs nothing and leaves the flag clear.
     *
     * <p>Field spellWeight coded before 261012, commented in full on 261012.
     */
    private int spellWeight;

    /**
     * Number of spellbooks this class uses — C's {@code num_books}. C counts it up one {@code book:}
     * line at a time, so it is the number of books actually read, not the count declared on the
     * {@code magic:} line; here it is fixed at construction as the size of the book list. Zero for a
     * non-caster, which is what {@link #isCaster()} tests.
     *
     * <p>Field numBooks coded before 261012, commented in full on 261012.
     */
    private int numBooks;

    /**
     * The spellbooks available to this class, in the order the data file declares them — C's
     * {@code books}. The order is load-bearing: the books form one flattened index space that runs
     * across their boundaries, which {@link #indexOfSpell(MagicSpell)} and
     * {@link #spellByIndex(int)} walk in this order. The list is a copy made at construction, but
     * the {@link MagicBook}s in it are the caller's own, not copies.
     *
     * <p>Field magicBooks coded before 261012, commented in full on 261012.
     */
    private List<MagicBook> magicBooks;

    /**
     * The number of spells in all this class's books together — C's {@code total_spells}, and the
     * size of the flattened index space. C counts it up one {@code spell:} line at a time; here it is
     * summed once at construction from each book's {@link MagicBook#getNumOfSpells()}.
     *
     * <p>Also the literacy test: {@code calc_mana} ({@code player-calcs.c}) zeroes the player's
     * maximum, current and fractional mana and returns for a class whose total is zero, before it
     * looks at levels or armour at all. Most callers of this class test the total rather than
     * {@link #isCaster()}.
     *
     * <p>Field totalSpells coded before 261012, commented in full on 261012.
     */
    private int totalSpells;

    /**
     * The shared "no magic" sentinel assigned to non-caster classes (Warrior), so callers can rely
     * on {@code magic} never being {@code null} and simply test {@link #isCaster()}. It matches what
     * C gets by zero-filling the struct: first-spell level 0, weight allowance 0, no books and no
     * spells.
     *
     * <p>Field NONE coded before 261012, commented in full on 261012.
     */
    public static final ClassMagic NONE = new ClassMagic(0, 0, List.of());

    /**
     * Tests whether this class has any spellbook at all. C has no such helper: it asks the question
     * in the form it needs, testing {@code total_spells} for literacy ({@code calc_mana}) or looping
     * over {@code num_books} to match a book ({@code player_object_to_book}). A class that listed a
     * book with no spells would answer {@code true} here and still read as illiterate to
     * {@link #getTotalSpells()}.
     *
     * <p>Function isCaster coded before 261012, commented in full on 261012.
     *
     * @return {@code true} if this class can cast — i.e. it defines at least one spellbook
     */
    public boolean isCaster() {
        return numBooks > 0;
    }

    /**
     * Builds a class-magic profile from its casting parameters and books. {@link #numBooks} is taken
     * from the size of {@code books}, and the books' spell counts are summed into
     * {@link #totalSpells}; neither is passed in. The list is copied, but the books in it are not.
     *
     * <p>Constructor ClassMagic coded before 261012, commented in full on 261012.
     *
     * @param firstSpellLevel level at which casting becomes possible
     * @param spellWeight     the armour weight allowance, in tenth-pounds, before mana is penalized
     * @param books           the spellbooks available to the class (the list is copied)
     */
    public ClassMagic(int firstSpellLevel, int spellWeight, List<MagicBook> books) {
        this.firstSpellLevel = firstSpellLevel;
        this.spellWeight = spellWeight;
        this.numBooks = books.size();
        this.magicBooks = new ArrayList<>(books);
        this.totalSpells = 0;

        for (MagicBook magicBook : magicBooks) {
            totalSpells += magicBook.getNumOfSpells();
        }
    }

    /**
     * Returns the class's books — C's {@code books} array. The list is the one held internally, not
     * a copy, so a caller that changes it changes the class.
     *
     * <p>Function getMagicBooks coded before 261012, commented in full on 261012.
     *
     * @return the spellbooks available to this class, in class-load order - the order the flattened
     * spell-index space runs across (see {@link #spellByIndex(int)})
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
     * <p>C has no function for this: it reads the number back from the {@code sidx} it stamped on
     * the spell at parse time, which is the same value. Matching uses {@code List.contains} and
     * {@code indexOf}, so it follows {@code MagicSpell.equals}; {@code MagicSpell} does not
     * override it, which is what makes the match identity. A spell listed in two books answers with
     * its place in the first.
     *
     * <p>Function indexOfSpell coded before 261012, commented in full on 261012.
     *
     * @param spell the spell to locate, matched by identity
     * @return the spell's flattened index, or {@code -1} if it belongs to none of this class's books
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
     * <p>C takes the player and reads {@code p->class->magic}; here the method sits on the class's
     * own profile, so the player argument is gone. A book with no spells is stepped over, as in C.
     * The final {@code return null} is reached only if a book's spell list has been changed since
     * construction, which leaves {@link #totalSpells} out of step with the lists.
     *
     * <p>Function spellByIndex coded on 260830, commented in full on 261012.
     *
     * @param spellIndex the flattened index into this class's books
     * @return the spell at that index, or {@code null} if the index is out of range
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

    /**
     * Returns the number of spells in all the class's books together — C's {@code total_spells}.
     *
     * <p>Function getTotalSpells coded before 261012, commented in full on 261012.
     *
     * @return the number of spells across all this class's books — zero for a non-caster, which is
     * the condition {@code calcMana} treats as illiteracy
     */
    public int getTotalSpells() {
        return totalSpells;
    }

    /**
     * Returns the level at which the class first casts — C's {@code magic.spell_first}.
     *
     * <p>Function getSpellFirst coded before 261012, commented in full on 261012.
     *
     * @return the character level at which this class gains its first spell
     */
    public int getSpellFirst() {
        return firstSpellLevel;
    }

    /**
     * Returns the number of books the class lists — C's {@code magic.num_books}, which is the size of
     * the book list rather than a count declared in the data file.
     *
     * <p>Function getNumBooks coded before 261012, commented in full on 261012.
     *
     * @return the number of spellbooks this class uses
     */
    public int getNumBooks() {
        return numBooks;
    }

    /**
     * Returns the armour weight allowance — C's {@code magic.spell_weight}; see {@link #spellWeight}
     * for how {@code calc_mana} charges for weight above it.
     *
     * <p>Function getSpellWeight coded before 261012, commented in full on 261012.
     *
     * @return the armour weight allowance, in tenth-pounds, before worn weight starts costing
     * maximum mana
     */
    public int getSpellWeight() {
        return spellWeight;
    }
}