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
 * reference to its {@link MonsterPain} set. The class has only five accessors so far:
 * {@link #getCodeName()}, {@link #getFlags()}, {@link #getDefaultMonsterChar()},
 * {@link #getWideMonsterChar()} and {@link #isWideChar()}.
 *
 * <p>C holds the default glyph in one {@code wchar_t} ({@code d_char}), which takes any code
 * point. A Java {@code char} is one UTF-16 unit and cannot hold a code point above
 * {@code 0xFFFF}, so the port splits the glyph in two fields, the same way
 * {@code AngbandDisplayCharacter} does: {@link #defaultMonsterChar} holds a code point up to
 * {@code 0xFFFF}, and {@link #wideMonsterChar} holds a supplementary one ({@code 0x10000} to
 * {@code 0x10FFFF}) with {@link #defaultMonsterChar} left at {@code '\0'}. C has no member or
 * function matching {@link #wideMonsterChar}, {@link #getWideMonsterChar()} or
 * {@link #isWideChar()}; they exist only to carry what a {@code char} cannot.
 *
 * <p>Class MonsterBase coded before 261010, commented in full on 261026.
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
     * Multilingual Plane, which covers every glyph in the shipped {@code monster_base.txt}. A
     * glyph above {@code 0xFFFF} lives in {@link #wideMonsterChar} instead, and this field is then
     * {@code '\0'}, so {@code '\0'} here alone does not tell a NUL glyph from a wide one - ask
     * {@link #isWideChar()}. {@code '\0'} on a base built by the code-name-only constructor.
     *
     * <p>Field defaultMonsterChar coded before 261010, commented in full on 261026.
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
     * The default glyph of this base when it is a supplementary code point ({@code 0x10000} to
     * {@code 0x10FFFF}) and so cannot be held in {@link #defaultMonsterChar}; {@code 0} for every
     * other glyph. Together with {@link #defaultMonsterChar} it is C's single {@code wchar_t}
     * {@code monster_base->d_char}. The full-argument constructor stores it as given and checks
     * neither its range nor that {@link #defaultMonsterChar} is {@code '\0'} alongside it, so the
     * split is a convention the caller keeps, not one this class enforces. {@code 0} on a base
     * built by the code-name-only constructor, as C's zero-filled struct is.
     *
     * <p>Field wideMonsterChar coded before 261026, commented in full on 261026.
     */
    private int wideMonsterChar;

    /**
     * Builds a bare base with only a code name. The other fields take the values that C's
     * {@code mem_zalloc} gives a new struct, except that the two text fields are empty rather than
     * {@code null}: no flags, a zero glyph (both {@link #defaultMonsterChar} and
     * {@link #wideMonsterChar}, so {@link #isWideChar()} is {@code false}) and no pain set. The
     * tests use it where only the name matters.
     *
     * <p>Constructor MonsterBase coded before 261010, commented in full on 261026.
     *
     * @param codeName the base's code name
     */
    public MonsterBase(String codeName) {
        this.codeName = codeName;
        inGameName = "";
        flags = new Flag<MonsterRaceFlag>(MonsterRaceFlag.class);
        defaultMonsterChar = '\0';
        wideMonsterChar = 0;
        pain = null;
        description = "";
    }

    /**
     * Builds a fully specified monster base, as {@code MonsterBaseAssembler} does for each
     * {@code monster_base.txt} record. Nothing is copied or checked: the flag set and pain set are
     * kept by reference, and a {@code null} is stored as given. The assembler passes the same
     * {@code desc:} text as both {@code inGameName} and {@code description}. The glyph arrives in
     * two halves, {@code defaultMonsterChar} and {@code wideMonsterChar}, which are stored as given:
     * the caller passes a glyph up to {@code 0xFFFF} in the first with {@code 0} in the second, or
     * a supplementary one in the second with {@code '\0'} in the first. Nothing here rejects a pair
     * that breaks that rule, or a {@code wideMonsterChar} outside {@code 0..0x10FFFF}.
     *
     * <p>Constructor MonsterBase coded before 261010, commented in full on 261026.
     *
     * @param codeName           the base's code name
     * @param inGameName         the displayed name, from the {@code desc:} line
     * @param flags              the race flags every monster of this base inherits
     * @param defaultMonsterChar the default display glyph when it is up to {@code 0xFFFF}, else
     *                           {@code '\0'}
     * @param wideMonsterChar    the default display glyph when it is above {@code 0xFFFF}, else
     *                           {@code 0}
     * @param pain               the pain-message set
     * @param description        the {@code desc:} text
     */
    public MonsterBase(String codeName,
                       String inGameName,
                       Flag<MonsterRaceFlag> flags,
                       char defaultMonsterChar,
                       int wideMonsterChar,
                       MonsterPain pain,
                       String description) {
        this.codeName = codeName;
        this.inGameName = inGameName;
        this.flags = flags;
        this.defaultMonsterChar = defaultMonsterChar;
        this.wideMonsterChar = wideMonsterChar;
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
     * Reads C's {@code monster_base->d_char}, but only the half that fits a {@code char}: it
     * returns {@code '\0'} when the glyph is above {@code 0xFFFF}, which lives in
     * {@link #getWideMonsterChar()}. A caller that needs every glyph must check
     * {@link #isWideChar()} first.
     *
     * <p>Method getDefaultMonsterChar coded before 261010, commented in full on 261026.
     *
     * @return the default display glyph for monsters of this base, or {@code '\0'} if it is a
     *         wide glyph
     */
    public char getDefaultMonsterChar() {
        return defaultMonsterChar;
    }

    /**
     * Returns the default glyph when it is a supplementary code point, the half that
     * {@link #getDefaultMonsterChar()} cannot hold. Reads the other half of C's single
     * {@code wchar_t} {@code monster_base->d_char}; C has no separate member for it.
     *
     * <p>Method getWideMonsterChar coded before 261026, commented in full on 261026.
     *
     * @return the code point ({@code 0x10000} to {@code 0x10FFFF}) of a wide default glyph, or
     *         {@code 0} when the glyph is not wide
     */
    public int getWideMonsterChar() {
        return wideMonsterChar;
    }

    /**
     * Says whether the default glyph is a supplementary code point and so is held in
     * {@link #getWideMonsterChar()} rather than {@link #getDefaultMonsterChar()}. It tests only
     * that {@link #wideMonsterChar} is non-zero, so it is how a caller tells a wide glyph from a
     * genuine NUL, for which both halves are zero. C has no counterpart, because a {@code wchar_t}
     * holds either kind of glyph.
     *
     * <p>Method isWideChar coded before 261026, commented in full on 261026.
     *
     * @return {@code true} if the default glyph is above {@code 0xFFFF}
     */
    public boolean isWideChar() {
        return wideMonsterChar != 0;
    }
}
