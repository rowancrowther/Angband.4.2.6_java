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
    /**
     * The summon type's name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * The message/sound category used when this summon occurs.
     *
     * @author ClaudeCode
     */
    private MessageType messageType;
    /**
     * Whether unique monsters may be chosen by this summon.
     *
     * @author ClaudeCode
     */
    private boolean uniquesAllowed;
    /**
     * The monster base types this summon may draw from.
     *
     * @author ClaudeCode
     */
    private List<MonsterBase> bases;
    /**
     * A race flag restricting which monsters this summon may choose.
     *
     * @author ClaudeCode
     */
    private MonsterRaceFlag raceFlag;
    /**
     * The name of a fallback summon type used if this one finds no valid monster.
     *
     * @author ClaudeCode
     */
    private String fallback;
    /**
     * Human-readable description of the summon.
     *
     * @author ClaudeCode
     */
    private String description;

    /**
     * Build a summon type from its parsed data-file fields.
     *
     * @param name           summon name
     * @param messageType    message category
     * @param uniquesAllowed whether uniques may be summoned
     * @param bases          allowed monster base types
     * @param raceFlag       restricting race flag
     * @param fallback       fallback summon name
     * @param description    description
     * @author ClaudeCode
     */
    public Summon(String name, MessageType messageType, boolean uniquesAllowed, List<MonsterBase> bases, MonsterRaceFlag raceFlag, String fallback, String description) {
        this.name = name;
        this.messageType = messageType;
        this.uniquesAllowed = uniquesAllowed;
        this.bases = bases;
        this.raceFlag = raceFlag;
        this.fallback = fallback;
        this.description = description;
    }

    /**
     * @return the summon type's name
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }

    /**
     * @return the message category used when this summon occurs
     * @author ClaudeCode
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * @return whether unique monsters may be summoned
     * @author ClaudeCode
     */
    public boolean isUniquesAllowed() {
        return uniquesAllowed;
    }

    /**
     * @return the monster base types this summon may draw from
     * @author ClaudeCode
     */
    public List<MonsterBase> getBases() {
        return bases;
    }

    /**
     * @return the restricting race flag
     * @author ClaudeCode
     */
    public MonsterRaceFlag getRaceFlag() {
        return raceFlag;
    }

    /**
     * @return the fallback summon name
     * @author ClaudeCode
     */
    public String getFallback() {
        return fallback;
    }

    /**
     * @return the summon's description
     * @author ClaudeCode
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return a debug string summarising this summon type
     * @author ClaudeCode
     */
    @Override
    public String toString() {
        return name + " " + messageType + " " + (uniquesAllowed ? "unique " : "") +
                (!bases.isEmpty() ? "has bases " : "") +
                raceFlag + " " + fallback + " " + description;
    }
}