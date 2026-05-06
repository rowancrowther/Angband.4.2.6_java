package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;

/**
 * STUB CLASS
 * TODO: Code, comment and test this
 */
public class Summon {
    private String name;
    private String messageType;
    private boolean uniquesAllowed;
    private ArrayList<MonsterBase> bases;
    private MonsterRaceFlag raceFlag;
    private String fallback;
    private String description;

    public Summon(String name, String messageType, boolean uniquesAllowed, ArrayList<MonsterBase> bases, MonsterRaceFlag raceFlag, String fallback, String description) {
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

    public String getMessageType() {
        return messageType;
    }

    public boolean isUniquesAllowed() {
        return uniquesAllowed;
    }

    public ArrayList<MonsterBase> getBases() {
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