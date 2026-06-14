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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.magic;

import java.util.ArrayList;
import java.util.List;

public class ClassMagic {
    private int firstSpellLevel;
    private int spellWeight;
    private int numBooks;
    List<MagicBook> magicBooks;
    private int totalSpells;

    public ClassMagic(int firstSpellLevel, int spellWeight, int numBooks) {
        this.firstSpellLevel = firstSpellLevel;
        this.spellWeight = spellWeight;
        this.numBooks = numBooks;
        this.magicBooks = new ArrayList<>();
        this.totalSpells = 0;
    }

    public void addMagicBook(MagicBook magicBook) {
        this.magicBooks.add(magicBook);
        this.totalSpells += magicBook.getNumOfSpells();
    }
}