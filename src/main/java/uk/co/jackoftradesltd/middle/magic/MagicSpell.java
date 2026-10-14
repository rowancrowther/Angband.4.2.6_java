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

import uk.co.jackoftradesltd.middle.effect.Effect;

import java.util.List;

/**
 * A single spell: its identity, the {@link MagicRealm} it belongs to, the
 * {@link Effect}s it produces when cast, and its casting parameters (required
 * level, mana cost, base failure rate and the experience awarded for first
 * casting it). This is the Java port of the C original's {@code struct class_spell}
 * ({@code player.h}), which a {@code class_book} lists under {@code spells}.
 *
 * <p>The struct's {@code name}, {@code text}, {@code effect}, {@code realm}, {@code slevel},
 * {@code smana}, {@code sfail} and {@code sexp} map to {@link #spellName}, {@link #spellDescription},
 * {@link #effects}, {@link #realm}, {@link #level}, {@link #mana}, {@link #fail} and {@link #exp}.
 * The two index members are not stored. {@code sidx}, the spell's place in the class's flattened
 * spell-index space, is worked out on demand by {@code ClassMagic.indexOfSpell}. {@code bidx}, the
 * index of the spell's book in the class's book list, has no Java reader at all: the only C code that
 * reads it is the borg and the parser's own test.
 *
 * <p>The realm is not a constructor argument. C copies it from the book onto each spell as
 * {@code parse_class_spell} ({@code init.c}) reads the spell line; here the book assembler builds
 * the spells first and then hands each one the book's realm through {@link #setRealm(MagicRealm)}.
 * A spell built any other way has no realm until that call is made, which is the state C's zeroed
 * struct is in before the copy.
 *
 * <p>Class MagicSpell coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class MagicSpell {
    /**
     * The spell's name — C's {@code name}, read from the {@code spell:} line. It is the only
     * member with a getter so far.
     *
     * <p>Field spellName coded before 261014, commented in full on 261014.
     */
    private String spellName;
    /**
     * Human-readable description of the spell's effect — C's {@code text}, which the data file builds
     * up from its {@code desc:} lines.
     *
     * <p>Field spellDescription coded before 261014, commented in full on 261014.
     */
    private String spellDescription;

    /**
     * The effects produced when the spell is cast, in the order the data file lists them — C's
     * {@code effect}, which is a linked list there and a {@link List} here. A spell with no
     * {@code effect:} lines holds an empty list where C holds a {@code NULL} pointer.
     *
     * <p>Field effects coded before 261014, commented in full on 261014.
     */
    private List<Effect> effects;

    /**
     * Minimum character level required to learn the spell — C's {@code slevel}. The same figure
     * is the reference level for the failure rate, which falls by three points for each level the
     * caster is above it. A level of 99 or more marks a spell the player cannot browse, which the spell
     * menu shows as "(illegible)".
     *
     * <p>Field level coded before 261014, commented in full on 261014.
     */
    private int level;
    /**
     * Mana cost to cast the spell — C's {@code smana}. Casting with less mana than this is allowed,
     * at five extra points of failure for each point short, and a cast that succeeds on too little
     * mana drains the player to zero and over-exerts them (fainting, and possible constitution
     * damage).
     *
     * <p>Field mana coded before 261014, commented in full on 261014.
     */
    private int mana;
    /**
     * Base failure rate (percent) before stat/level adjustment — C's {@code sfail}.
     *
     * <p>Field fail coded before 261014, commented in full on 261014.
     */
    private int fail;
    /**
     * The spell's experience figure — C's {@code sexp}. Awarded the first time the spell is cast,
     * multiplied by the spell's {@link #level}.
     *
     * <p>Field exp coded before 261014, commented in full on 261014.
     */
    private int exp;

    /**
     * The magic realm the spell belongs to — C's {@code realm}. It always equals the realm of the
     * book that lists the spell, and it decides the casting stat, the verb and the noun used for the
     * spell. It stays {@code null} until {@link #setRealm(MagicRealm)} is called, and nothing reads
     * it yet.
     *
     * <p>Field realm coded on 261014, commented in full on 261014.
     */
    private MagicRealm realm;

    /**
     * Build a spell from its casting parameters, resolved effects and description. The realm is not
     * set here; see {@link #setRealm(MagicRealm)}. The parameters are stored as given, with no
     * range checks and no copy of the effect list.
     *
     * <p>The two int pairs come in a different order from the C struct and from the data file's
     * {@code spell:} line, which both run level, mana, fail, exp. Here the order is level, fail,
     * mana, exp, so swapping the two middle arguments compiles without complaint.
     *
     * <p>Constructor MagicSpell coded before 261014, commented in full on 261014.
     *
     * @param spellName        the spell's name
     * @param level            required level
     * @param fail             base failure rate
     * @param mana             mana cost
     * @param exp              first-cast experience
     * @param effects          the effects produced when the spell is cast, in order
     * @param spellDescription the spell's description text
     */
    public MagicSpell(String spellName, int level, int fail, int mana, int exp,
                      List<Effect> effects, String spellDescription) {
        this.spellName = spellName;
        this.level = level;
        this.fail = fail;
        this.mana = mana;
        this.exp = exp;
        this.effects = effects;
        this.spellDescription = spellDescription;
    }

    /**
     * Gives the spell its realm — the Java form of C's {@code spell->realm = book->realm} in
     * {@code parse_class_spell} ({@code init.c}). The caller passes the realm of the book that
     * lists the spell, so the spell and its book share one {@link MagicRealm} object, as the two
     * pointers do in C. A second call replaces the first, and {@code null} is accepted.
     *
     * <p>Function setRealm coded on 261014, commented in full on 261014.
     *
     * @param realm the realm of the book the spell belongs to
     */
    public void setRealm(MagicRealm realm) {
        this.realm = realm;
    }

    /**
     * Returns the spell's name — C's {@code name}.
     *
     * <p>Function getSpellName coded before 261014, commented in full on 261014.
     *
     * @return the name given on the {@code spell:} line
     */
    public String getSpellName() {
        return spellName;
    }
}
