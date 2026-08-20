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
     * The character level at which this class can first cast spells — C's {@code spell_first}. Mana
     * is scaled by the levels gained <em>since</em> this one, so a caster exactly at this level has
     * the smallest non-zero allowance and one below it has none at all
     * ({@code player-calcs.c:1489-1497}).
     */
    private int firstSpellLevel;

    /**
     * The greatest weight of armour the class may wear before its mana starts to suffer — C's
     * {@code spell_weight}, documented there as "Max armor weight to avoid mana penalties"
     * ({@code player.h:299}).
     *
     * <p>Despite the name this has nothing to do with the weight of spellbooks. It is an allowance:
     * {@code calc_mana} weighs everything worn except weapon, launcher, rings, amulet and light,
     * and every ten tenth-pounds by which that total exceeds this allowance costs one point of
     * maximum mana and sets the encumbrance flag ({@code player-calcs.c:1523-1533}).
     */
    private int spellWeight;

    /**
     * Number of spellbooks this class uses — C's {@code num_books}, the declared count from the
     * {@code magic:} line. Zero for a non-caster, which is what {@link #isCaster()} tests.
     */
    private int numBooks;

    /**
     * The spellbooks available to this class, in the order the data file declares them — C's
     * {@code books}. The order is load-bearing: the books form one flattened index space that runs
     * across their boundaries, which {@link #indexOfSpell(MagicSpell)} and
     * {@link #spellByIndex(int)} walk in this order.
     */
    private List<MagicBook> magicBooks;

    /**
     * The number of spells in all this class's books together — C's {@code total_spells}, and the
     * size of the flattened index space. Summed once at construction rather than recounted.
     *
     * <p>Also the literacy test: {@code calc_mana} returns a flat zero for a class whose total is
     * zero, before it looks at levels or armour at all ({@code player-calcs.c:1484-1488}).
     */
    private int totalSpells;

    /**
     * The shared "no magic" sentinel assigned to non-caster classes (Warrior), so callers can rely
     * on {@code magic} never being {@code null} and simply test {@link #isCaster()}.
     */
    public static final ClassMagic NONE = new ClassMagic(0, 0, 0, List.of());

    /**
     * @return {@code true} if this class can cast — i.e. it defines at least one spellbook
     */
    public boolean isCaster() {
        return numBooks > 0;
    }

    /**
     * Build a class-magic profile from its casting parameters and books, summing the books' spell
     * counts into {@link #totalSpells}.
     *
     * @param firstSpellLevel level at which casting becomes possible
     * @param spellWeight     the armour weight allowance before mana is penalised
     * @param numBooks        number of books used
     * @param books           the spellbooks available to the class (defensively copied)
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
     * @return the number of spells across all this class's books — zero for a non-caster, which is
     * the condition {@code calcMana} treats as illiteracy
     */
    public int getTotalSpells() {
        return totalSpells;
    }

    /**
     * @return the character level at which this class gains its first spell — C's
     * {@code magic.spell_first}
     */
    public int getSpellFirst() {
        return firstSpellLevel;
    }

    /**
     * @return the number of spellbooks this class uses — C's {@code magic.num_books}
     */
    public int getNumBooks() {
        return numBooks;
    }

    /**
     * @return the armour weight allowance, in tenth-pounds, before worn weight starts costing
     * maximum mana — C's {@code magic.spell_weight}
     */
    public int getSpellWeight() {
        return spellWeight;
    }
}