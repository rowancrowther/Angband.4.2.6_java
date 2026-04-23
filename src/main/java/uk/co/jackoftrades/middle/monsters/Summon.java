package uk.co.jackoftrades.middle.monsters;

public class Summon {
    private String name;
    private String messageType;
    private boolean uniquesAllowed;
    private MonsterBases bases;
    private String raceFlag;
    private String fallback;
    private String description;

    public Summon(String name, String messageType, boolean uniquesAllowed, MonsterBases bases, String raceFlag, String fallback, String description) {
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

    public MonsterBases getBases() {
        return bases;
    }

    public void addBase(MonsterBase base) {
        bases.put(base);
    }

    public String getRaceFlag() {
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
        StringBuilder result = new StringBuilder(name + " " + messageType + " ");
        result.append(uniquesAllowed ? "unique " : "");
        result.append(bases.size() > 0 ? "has bases " : "");
        result.append(raceFlag + " " + fallback + " " + description);
        return result.toString();
    }
}
