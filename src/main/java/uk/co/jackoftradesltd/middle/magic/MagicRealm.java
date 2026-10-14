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

import uk.co.jackoftradesltd.middle.enums.Stats;

/**
 * A school of magic — one of the four spellcasting "realms" defined by {@code realm.txt} (arcane,
 * divine, nature, shadow) — bundling the flavour that varies between them: the {@link Stats stat}
 * that governs casting, the verb used ("cast", "recite", …), the noun for a casting ("spell",
 * "prayer", …) and the noun for the books that hold its spells ("magic book", "prayer book", …).
 *
 * <p>Java port of the C {@code struct magic_realm} ({@code player.h}). That struct also carries a
 * {@code code} field, but in 4.2.6 nothing ever populates it and {@code lookup_realm} matches realms
 * by {@code name}, so the port omits it. Its {@code next} link is replaced by the
 * {@code List<MagicRealm>} that {@code PlayerRegistry} holds. The book noun is kept as the raw
 * {@code book-noun:} text, as C keeps {@code book_noun}: nothing in C reads it back, and the data
 * files may give any text (C's own parser test loads "exercise manual").
 * A book's item type comes from the class's {@code book:} line, not from the realm.
 *
 * <p>A realm is built once by {@code RealmAssembler} and never changed afterwards. Only
 * {@link #getName()} and {@link #getStat()} exist so far; getters for the verb, the spell noun and
 * the book noun arrive with the chapters that read them (casting, study and the spell menus).
 *
 * <p>Class MagicRealm coded before 261010, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class MagicRealm {
    /**
     * The realm's name — C's {@code realm->name}, from the {@code name:} line (e.g. "arcane"). Realms
     * are looked up by it, ignoring case ({@code lookup_realm}, {@code player.c}), and a class's
     * realm list is de-duplicated by it.
     *
     * <p>Field name coded before 261010, commented in full on 261014.
     */
    private String name;
    /**
     * The stat that governs casting in this realm — C's {@code realm->stat}, the index of the
     * {@code stat:} value ({@code INT} or {@code WIS} in the shipped data). C holds an {@code int};
     * the port holds the {@link Stats} constant.
     *
     * <p>Field stat coded before 261010, commented in full on 261014.
     */
    private Stats stat;
    /**
     * The verb for casting in this realm — C's {@code realm->verb}, from the {@code verb:} line
     * (e.g. "cast", "recite", "chant", "perform"). C reads it when a spell is cast
     * ({@code cmd-obj.c}); nothing in the port reads it yet.
     *
     * <p>Field verb coded before 261010, commented in full on 261014.
     */
    private String verb;
    /**
     * The noun for a spell in this realm — C's {@code realm->spell_noun}, from the
     * {@code spell-noun:} line (e.g. "spell", "prayer", "verse", "ritual"). C builds its study and
     * forgetting messages from it ({@code player-calcs.c}, {@code player-spell.c}); nothing in the
     * port reads it yet.
     *
     * <p>Field spellNoun coded before 261010, commented in full on 261014.
     */
    private String spellNoun;
    /**
     * The noun for the books that hold this realm's spells — C's {@code realm->book_noun}, the raw
     * text of the {@code book-noun:} line (e.g. "magic book", "prayer book"). It is kept as text.
     * C's {@code parse_realm_book_noun} stores whatever the line
     * says, and nothing in C reads the result back.
     *
     * <p>Field bookNoun coded on 261014, commented in full on 261014.
     */
    private String bookNoun;

    /**
     * Build a magic realm from its parsed data-file fields. The values are stored as given: no
     * validation is done here, and a {@code null} verb, spell noun or book noun stays {@code null},
     * as the unset pointers of C's zero-filled struct do. Resolving the {@code stat:} text to a
     * {@link Stats} is the caller's job ({@code RealmAssembler}).
     *
     * <p>Constructor MagicRealm coded before 261010, commented in full on 261014.
     *
     * @param name      the realm's name, from {@code name:}
     * @param stat      the stat that governs casting, from {@code stat:}
     * @param verb      the casting verb, from {@code verb:}
     * @param spellNoun the noun for a spell, from {@code spell-noun:}
     * @param bookNoun  the noun for a spellbook, from {@code book-noun:}, kept as raw text
     */
    public MagicRealm(String name, Stats stat, String verb, String spellNoun, String bookNoun) {
        this.name = name;
        this.stat = stat;
        this.verb = verb;
        this.spellNoun = spellNoun;
        this.bookNoun = bookNoun;
    }

    /**
     * The realm's name — C's {@code realm->name}. {@code PlayerRegistry.lookupRealm} matches on it,
     * ignoring case as {@code lookup_realm} does; C's {@code class_magic_realms} de-duplicates a
     * class's realm list on it with a case-sensitive compare.
     *
     * <p>Function getName coded before 261010, commented in full on 261014.
     *
     * @return this realm's name
     */
    public String getName() {
        return name;
    }

    /**
     * The stat casting in this realm is governed by — C's {@code realm->stat}. Read when working
     * out a caster's mana: {@code calc_mana} averages this stat's table index across every realm the
     * class draws on ({@code average_spell_stat}, {@code player-calcs.c}), so a class with
     * two realms is limited by the mean of two stats rather than either one.
     *
     * <p>Function getStat coded before 261010, commented in full on 261014.
     *
     * @return the governing stat
     */
    public Stats getStat() {
        return stat;
    }
}
