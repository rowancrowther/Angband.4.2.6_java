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

import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.enums.TValue;

/**
 * A school of magic (e.g. arcane, holy) — the spellcasting "realm" that defines
 * which stat governs casting, the verb used ("cast"/"pray"), the noun for its
 * spells, and the book item type that holds them. This is the Java port of the
 * C original's {@code struct magic_realm} ({@code src/player.h}).
 *
 * @author Rowan Crowther
 */
public class MagicRealm {
    /**
     * The realm's name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * Short code identifying the realm.
     *
     * @author Rowan Crowther
     */
    private String code;
    /**
     * The stat that governs casting in this realm.
     *
     * @author Rowan Crowther
     */
    private Stats stat;
    /**
     * The verb used for casting (e.g. "cast", "pray").
     *
     * @author Rowan Crowther
     */
    private String verb;
    /**
     * The noun used for a spell in this realm (e.g. "spell", "prayer").
     *
     * @author Rowan Crowther
     */
    private String spellNoun;
    /**
     * The item type (tval) of the books that hold this realm's spells.
     *
     * @author Rowan Crowther
     */
    private TValue book;

    /**
     * Build a magic realm from its parsed data-file fields.
     *
     * @param name      realm name
     * @param stat      governing stat
     * @param verb      casting verb
     * @param spellNoun spell noun
     * @param book      book item type
     * @author Rowan Crowther
     */
    public MagicRealm(String name, Stats stat, String verb, String spellNoun, TValue book) {
        this.name = name;
        this.stat = stat;
        this.verb = verb;
        this.spellNoun = spellNoun;
        this.book = book;
    }

    /**
     * @return this realm's name
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }
}
