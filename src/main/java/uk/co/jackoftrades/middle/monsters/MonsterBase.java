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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

/**
 * A monster "base" template (as loaded from {@code monster_base.txt}) — a family
 * of monsters (e.g. "dragon", "orc") sharing default flags, display glyph, pain
 * messages and description, which individual {@link MonsterRace}s inherit from.
 * This is the Java port of the C original's {@code struct monster_base}
 * ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterBase {
    /**
     * The base's internal code name (used for cross-references).
     *
     * @author Rowan Crowther
     */
    private String codeName;
    /**
     * The base's in-game display name.
     *
     * @author Rowan Crowther
     */
    private String inGameName;
    /**
     * Default race flags shared by monsters of this base.
     *
     * @author Rowan Crowther
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * Default display glyph for monsters of this base.
     *
     * @author Rowan Crowther
     */
    private char defaultMonsterChar;
    /**
     * The pain-message set used by monsters of this base.
     *
     * @author Rowan Crowther
     */
    private MonsterPain pain;
    /**
     * Human-readable description of the base.
     *
     * @author Rowan Crowther
     */
    private String description;

    /**
     * Build a bare base with only a code name; other fields take empty/default values.
     *
     * @param codeName the base's code name
     * @author Rowan Crowther
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
     * Build a fully-specified monster base.
     *
     * @param codeName           the base's code name
     * @param inGameName         the in-game display name
     * @param flags              default race flags
     * @param defaultMonsterChar default display glyph
     * @param pain               pain-message set
     * @param description        description
     * @author Rowan Crowther
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
     * @return a debug string listing this base's fields
     * @author Rowan Crowther
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
     * @return this base's internal code name
     * @author Rowan Crowther
     */
    public String getCodeName() {
        return codeName;
    }
}
