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

    // TODO: At present the codeName and inGameName are pulled in from the same line in the lib/gamedata file, this
    // is because I don't know where they are. It could be that one of them is desc, which doesn't appear in the C files
    // or it could be another reason. FIND THIS OUT.
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
