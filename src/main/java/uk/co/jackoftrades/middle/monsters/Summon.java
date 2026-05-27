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

import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.List;

/**
 * STUB CLASS
 * TODO: Code, comment and test this
 */
public class Summon {
    private String name;
    private MessageType messageType;
    private boolean uniquesAllowed;
    private List<MonsterBase> bases;
    private MonsterRaceFlag raceFlag;
    private String fallback;
    private String description;

    public Summon(String name, MessageType messageType, boolean uniquesAllowed, List<MonsterBase> bases, MonsterRaceFlag raceFlag, String fallback, String description) {
        this.name = name;
        this.messageType = messageType;
        this.uniquesAllowed = uniquesAllowed;
        this.bases = bases;
        this.raceFlag = raceFlag;
        this.fallback = fallback;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public boolean isUniquesAllowed() {
        return uniquesAllowed;
    }

    public List<MonsterBase> getBases() {
        return bases;
    }

    public MonsterRaceFlag getRaceFlag() {
        return raceFlag;
    }

    public String getFallback() {
        return fallback;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " " + messageType + " " + (uniquesAllowed ? "unique " : "") +
                (!bases.isEmpty() ? "has bases " : "") +
                raceFlag + " " + fallback + " " + description;
    }
}