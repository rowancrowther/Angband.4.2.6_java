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

package uk.co.jackoftradesltd.middle.monsters;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

/**
 * A monster "base" template (as loaded from {@code monster_base.txt}) — a family
 * of monsters (e.g. "dragon", "orc") sharing default flags, display glyph, pain
 * messages and description, which individual {@link MonsterRace}s inherit from.
 * This is the Java port of the C original's {@code struct monster_base}
 * ({@code monster.h}).
 *
 * <p>C chains its bases into one linked list ({@code rb_info}, via the struct's {@code next}
 * member) and {@code lookup_monster_base} in {@code mon-util.c} walks it by name. The port has no
 * {@code next}: {@code MonsterRegistry} holds the bases in a list and
 * {@code MonsterRegistry.lookupMonsterBase} does the same name search.
 *
 * <p>C's one {@code text} member is the {@code desc:} line of {@code monster_base.txt} (several
 * {@code desc:} lines are appended end to end with no separator). The port keeps it in two fields,
 * {@link #inGameName} and {@link #description}, because the C comment calls it the "in-game name"
 * while the data file calls it a description. {@code MonsterBaseAssembler} fills both from the
 * same text, so they are always equal for a base read from the data file.
 *
 * <p>Instances are made by {@code MonsterBaseAssembler} when {@code MonsterBaseReader} loads
 * {@code monster_base.txt}, which must happen after {@code pain.txt} because each base holds a
 * reference to its {@link MonsterPain} set. The class has only three accessors so far:
 * {@link #getCodeName()}, {@link #getFlags()} and {@link #getDefaultMonsterChar()}.
 *
 * <p>Class MonsterBase coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class MonsterBase {
    /**
     * The base's name as written in {@code monster_base.txt} ({@code name:}), such as
     * {@code ancient dragon}. Other data files ({@code monster.txt}, {@code slay.txt},
     * {@code pit.txt}, {@code summon.txt}) refer to the base by this string, and
     * {@code MonsterRegistry.lookupMonsterBase} matches it exactly, case included. Ports C's
     * {@code monster_base->name}.
     *
     * <p>Field codeName coded before 261010, commented in full on 261010.
     */
    private String codeName;
    /**
     * The base's displayed name, taken from the {@code desc:} line (for example
     * {@code Ancient Dragon/Wyrm}). Ports C's {@code monster_base->text}, which the C comment calls
     * the in-game name; {@link #description} holds the same text. Empty, not {@code null}, on a base
     * built by the code-name-only constructor, where C's zero-filled struct would hold
     * {@code NULL}.
     *
     * <p>Field inGameName coded before 261010, commented in full on 261010.
     */
    private String inGameName;
    /**
     * The race flags every monster of this base inherits ({@code flags:} lines, which accumulate).
     * Ports C's {@code monster_base->flags}. Empty on a base built by the code-name-only
     * constructor, as C's zero-filled flag array is.
     *
     * <p>Field flags coded before 261010, commented in full on 261010.
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * The glyph that monsters of this base display by default ({@code glyph:}). It is also what
     * groups monsters into one "type" for the escort and kin-summon rules. Ports C's
     * {@code monster_base->d_char}, a {@code wchar_t}; a Java {@code char} holds only the Basic
     * Multilingual Plane, which covers every glyph in {@code monster_base.txt}. {@code '\0'} on a
     * base built by the code-name-only constructor.
     *
     * <p>Field defaultMonsterChar coded before 261010, commented in full on 261010.
     */
    private char defaultMonsterChar;
    /**
     * The pain-message set that monsters of this base use when hurt ({@code pain:}). Ports C's
     * {@code monster_base->pain}, a pointer into {@code pain_messages}; here it is the
     * {@link MonsterPain} that {@code MonsterRegistry.lookupMonsterPain} returned for the serial in
     * the data file. {@code null} on a base built by the code-name-only constructor, as C's
     * zero-filled pointer is. It has no accessor yet.
     *
     * <p>Field pain coded before 261010, commented in full on 261010.
     */
    private MonsterPain pain;
    /**
     * The base's text from the {@code desc:} line, shown by the {@code /} command in C's UI.
     * Ports C's {@code monster_base->text}, the same member as {@link #inGameName}, so the two are
     * equal for any base read from the data file. Empty, not {@code null}, on a base built by the
     * code-name-only constructor. It has no accessor yet.
     *
     * <p>Field description coded before 261010, commented in full on 261010.
     */
    private String description;

    /**
     * Builds a bare base with only a code name. The other fields take the values that C's
     * {@code mem_zalloc} gives a new struct, except that the two text fields are empty rather than
     * {@code null}: no flags, a zero glyph and no pain set. The tests use it
     * where only the name matters.
     *
     * <p>Constructor MonsterBase coded before 261010, commented in full on 261010.
     *
     * @param codeName the base's code name
     */
    public MonsterBase(String codeName) {
        this.codeName = codeName;
        inGameName = "";
        flags = new Flag<MonsterRaceFlag>(MonsterRaceFlag.class);
        defaultMonsterChar = '\0';
        pain = null;
        description = "";
    }

    /**
     * Builds a fully specified monster base, as {@code MonsterBaseAssembler} does for each
     * {@code monster_base.txt} record. Nothing is copied or checked: the flag set and pain set are
     * kept by reference, and a {@code null} is stored as given. The assembler passes the same
     * {@code desc:} text as both {@code inGameName} and {@code description}.
     *
     * <p>Constructor MonsterBase coded before 261010, commented in full on 261010.
     *
     * @param codeName           the base's code name
     * @param inGameName         the displayed name, from the {@code desc:} line
     * @param flags              the race flags every monster of this base inherits
     * @param defaultMonsterChar the default display glyph
     * @param pain               the pain-message set
     * @param description        the {@code desc:} text
     */
    public MonsterBase(String codeName,
                       String inGameName,
                       Flag<MonsterRaceFlag> flags,
                       char defaultMonsterChar,
                       MonsterPain pain,
                       String description) {
        this.codeName = codeName;
        this.inGameName = inGameName;
        this.flags = flags;
        this.defaultMonsterChar = defaultMonsterChar;
        this.pain = pain;
        this.description = description;
    }

    /**
     * Describes the base for logs and test failure messages. It lists the code name, in-game name,
     * flags, glyph and pain set, and leaves out {@code description} because that is the same text
     * as the in-game name. C has no counterpart.
     *
     * <p>Method toString coded before 261010, commented in full on 261010.
     *
     * @return a debug string listing this base's fields
     */
    @Override
    public String toString() {
        return "MonsterBase{" +
                "codeName='" + codeName + '\'' +
                ", inGameName='" + inGameName + '\'' +
                ", flags=" + flags +
                ", defaultMonsterChar=" + defaultMonsterChar +
                ", pain=" + pain +
                '}';
    }

    /**
     * Returns the name that other data files use to refer to this base, and that
     * {@code MonsterRegistry.lookupMonsterBase} matches. Reads C's {@code monster_base->name}.
     *
     * <p>Method getCodeName coded before 261010, commented in full on 261010.
     *
     * @return this base's internal code name
     */
    public String getCodeName() {
        return codeName;
    }

    /**
     * Returns the live flag set, not a copy, so a caller that changes it changes the base for every
     * monster race that uses it. Reads C's {@code monster_base->flags}.
     *
     * <p>Method getFlags coded before 261010, commented in full on 261010.
     *
     * @return the race flags shared by all monsters of this base
     */
    public Flag<MonsterRaceFlag> getFlags() {
        return flags;
    }

    /**
     * Returns the glyph that monsters of this base display unless their own race overrides it.
     * Reads C's {@code monster_base->d_char}.
     *
     * <p>Method getDefaultMonsterChar coded before 261010, commented in full on 261010.
     *
     * @return the default display glyph for monsters of this base
     */
    public char getDefaultMonsterChar() {
        return defaultMonsterChar;
    }
}
