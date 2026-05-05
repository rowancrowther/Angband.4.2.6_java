package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

public class Brand {
    private String code;
    private String name;
    private String verb;
    private MonsterRaceFlag resistFlag;
    private MonsterRaceFlag vulnerableFlag;

    private int multiplier;
    private int oMultiplier;
    private int power;

    public Brand(String code, String name, String verb, MonsterRaceFlag resistFlag, MonsterRaceFlag vulnerableFlag, int multiplier, int oMultiplier, int power) {
        this.code = code;
        this.name = name;
        this.verb = verb;
        this.resistFlag = resistFlag;
        this.vulnerableFlag = vulnerableFlag;
        this.multiplier = multiplier;
        this.oMultiplier = oMultiplier;
        this.power = power;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", verb='" + verb + '\'' +
                ", resistFlag=" + resistFlag +
                ", vulnerableFlag=" + vulnerableFlag +
                ", multiplier=" + multiplier +
                ", oMultiplier=" + oMultiplier +
                ", power=" + power +
                '}';
    }
}