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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;

public class MagicBook {
    private ObjectKind bookType;
    private String bookName;
    private boolean dungeon;
    private int numOfSpells;
    private MagicRealm realm;
    private List<MagicSpell> spells;

    @Contract(mutates = "this")
    public MagicBook(ObjectKind bookType, boolean dungeon, String bookName, int numOfSpells, MagicRealm realm) {
        this.bookType = bookType;
        this.bookName = bookName;
        this.dungeon = dungeon;
        this.numOfSpells = numOfSpells;
        this.realm = realm;
        spells = new ArrayList<>();
    }

    public void addSpell(MagicSpell spell) {
        spells.add(spell);
    }

    @Contract(pure = true)
    @CheckReturnValue
    public int getNumOfSpells() {
        return numOfSpells;
    }

    public String getBookName() {
        return bookName;
    }

    public ObjectKind getBookKind() {
        return bookType;
    }

    public void setBookKind(ObjectKind bookType) {
        this.bookType = bookType;
    }

    public boolean isDungeon() {
        return dungeon;
    }
}