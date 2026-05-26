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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

public class MonsterBase {
    private String codeName;
    private String inGameName;
    private Flag<MonsterRaceFlag> flags;
    private char defaultMonsterChar;
    private MonsterPain pain;
    private String description;

    public MonsterBase(String codeName) {
        this.codeName = codeName;
        inGameName = "";
        flags = new Flag<MonsterRaceFlag>(MonsterRaceFlag.class);
        defaultMonsterChar = ' ';
        pain = null;
        description = "";
    }

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

    public String getCodeName() {
        return codeName;
    }
}
