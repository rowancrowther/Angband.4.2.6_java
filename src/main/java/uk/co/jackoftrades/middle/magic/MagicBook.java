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
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.util.ArrayList;
import java.util.List;

/**
 * A spellbook: the item that holds a set of {@link MagicSpell}s for a given
 * {@link MagicRealm}. Tracks the backing item kind, name, whether it is a
 * dungeon-only book, and the spells it contains. This is the Java port of the C
 * original's class spellbook data.
 *
 * @author ClaudeCode
 */
public class MagicBook {
    /**
     * The object kind (item) that represents this book in the game.
     *
     * @author ClaudeCode
     */
    private ObjectKind bookType;
    /**
     * The book's display name.
     *
     * @author ClaudeCode
     */
    private String bookName;
    /**
     * Whether this is a dungeon-only book (not sold in town).
     *
     * @author ClaudeCode
     */
    private boolean dungeon;
    /**
     * The number of spells the book holds.
     *
     * @author ClaudeCode
     */
    private int numOfSpells;
    /**
     * The magic realm this book's spells belong to.
     *
     * @author ClaudeCode
     */
    private MagicRealm realm;
    /**
     * The spells contained in this book.
     *
     * @author ClaudeCode
     */
    private List<MagicSpell> spells;

    /**
     * Build an empty spellbook; spells are added later via {@link #addSpell(MagicSpell)}.
     *
     * @param bookType    the backing object kind
     * @param dungeon     whether it is a dungeon-only book
     * @param bookName    the book's name
     * @param numOfSpells the declared number of spells
     * @param realm       the magic realm
     * @author ClaudeCode
     */
    @Contract(mutates = "this")
    public MagicBook(ObjectKind bookType, boolean dungeon, String bookName, int numOfSpells, MagicRealm realm) {
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
     * @author ClaudeCode
     */
    public void addSpell(MagicSpell spell) {
        spells.add(spell);
    }

    /**
     * @return the declared number of spells in this book
     * @author ClaudeCode
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getNumOfSpells() {
        return numOfSpells;
    }

    /**
     * @return the book's name
     * @author ClaudeCode
     */
    public String getBookName() {
        return bookName;
    }

    /**
     * @return the object kind backing this book
     * @author ClaudeCode
     */
    public ObjectKind getBookKind() {
        return bookType;
    }

    /**
     * Set the object kind backing this book.
     *
     * @param bookType the object kind
     * @author ClaudeCode
     */
    public void setBookKind(ObjectKind bookType) {
        this.bookType = bookType;
    }

    /**
     * @return whether this is a dungeon-only book
     * @author ClaudeCode
     */
    public boolean isDungeon() {
        return dungeon;
    }
}