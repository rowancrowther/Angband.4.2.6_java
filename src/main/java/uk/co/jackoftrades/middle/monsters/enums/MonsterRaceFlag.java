package uk.co.jackoftrades.middle.monsters.enums;

public enum MonsterRaceFlag {
    RF_NONE(MonsterRaceCategory.RFT_NONE, ""),
    RF_UNIQUE(MonsterRaceCategory.RFT_OBV, ""),
    RF_QUESTOR(MonsterRaceCategory.RFT_OBV, ""),
    RF_MALE(MonsterRaceCategory.RFT_OBV, ""),
    RF_FEMALE(MonsterRaceCategory.RFT_OBV, ""),
    RF_GROUP_AI(MonsterRaceCategory.RFT_OBV, ""),
    RF_NAME_COMMA(MonsterRaceCategory.RFT_OBV, ""),
    RF_CHAR_CLEAR(MonsterRaceCategory.RFT_DISP, ""),
    RF_ATTR_RAND(MonsterRaceCategory.RFT_DISP, ""),
    RF_ATTR_CLEAR(MonsterRaceCategory.RFT_DISP, ""),
    RF_ATTR_MULTI(MonsterRaceCategory.RFT_DISP, ""),
    RF_ATTR_FLICKER(MonsterRaceCategory.RFT_DISP, ""),
    RF_FORCE_DEPTH(MonsterRaceCategory.RFT_GEN, ""),
    RF_FORCE_SLEEP(MonsterRaceCategory.RFT_GEN, ""),
    RF_FORCE_EXTRA(MonsterRaceCategory.RFT_GEN, ""),
    RF_SEASONAL(MonsterRaceCategory.RFT_GEN, ""),
    RF_UNAWARE(MonsterRaceCategory.RFT_NOTE, ""),
    RF_MULTIPLY(MonsterRaceCategory.RFT_NOTE, ""),
    RF_REGENERATE(MonsterRaceCategory.RFT_NOTE, ""),
    RF_FRIGHTENED(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_NEVER_BLOW(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_NEVER_MOVE(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_RAND_25(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_RAND_50(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_MIMIC_INV(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_STUPID(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_SMART(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_SPIRIT(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_POWERFUL(MonsterRaceCategory.RFT_BEHAV, ""),
    RF_ONLY_GOLD(MonsterRaceCategory.RFT_DROP, ""),
    RF_ONLY_ITEM(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_40(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_60(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_1(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_2(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_3(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_4(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_GOOD(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_GREAT(MonsterRaceCategory.RFT_DROP, ""),
    RF_DROP_20(MonsterRaceCategory.RFT_DROP, ""),
    RF_INVISIBLE(MonsterRaceCategory.RFT_DET, "invisible"),
    RF_COLD_BLOOD(MonsterRaceCategory.RFT_DET, "cold blooded"),
    RF_EMPTY_MIND(MonsterRaceCategory.RFT_DET, "not detected by telepathy"),
    RF_WEIRD_MIND(MonsterRaceCategory.RFT_DET, "rarely detected by telepathy"),
    RF_OPEN_DOOR(MonsterRaceCategory.RFT_ALTER, "open doors"),
    RF_BASH_DOOR(MonsterRaceCategory.RFT_ALTER, "bash down doors"),
    RF_PASS_WALL(MonsterRaceCategory.RFT_ALTER, "pass through walls"),
    RF_KILL_WALL(MonsterRaceCategory.RFT_ALTER, "bore through walls"),
    RF_SMASH_WALL(MonsterRaceCategory.RFT_ALTER, "smash walls"),
    RF_MOVE_BODY(MonsterRaceCategory.RFT_ALTER, "push past weaker monsters"),
    RF_KILL_BODY(MonsterRaceCategory.RFT_ALTER, "destroy weaker monsters"),
    RF_TAKE_ITEM(MonsterRaceCategory.RFT_ALTER, "pick up objects"),
    RF_KILL_ITEM(MonsterRaceCategory.RFT_ALTER, "destroy objects"),
    RF_CLEAR_WEB(MonsterRaceCategory.RFT_ALTER, "clear webs"),
    RF_PASS_WEB(MonsterRaceCategory.RFT_ALTER, "pass through webs"),
    RF_ORC(MonsterRaceCategory.RFT_RACE_N, "orc"),
    RF_TROLL(MonsterRaceCategory.RFT_RACE_N, "troll"),
    RF_GIANT(MonsterRaceCategory.RFT_RACE_N, "giant"),
    RF_DRAGON(MonsterRaceCategory.RFT_RACE_N, "dragon"),
    RF_DEMON(MonsterRaceCategory.RFT_RACE_N, "demon"),
    RF_ANIMAL(MonsterRaceCategory.RFT_RACE_A, "natural"),
    RF_EVIL(MonsterRaceCategory.RFT_RACE_A, "evil"),
    RF_UNDEAD(MonsterRaceCategory.RFT_RACE_A, "undead"),
    RF_NONLIVING(MonsterRaceCategory.RFT_RACE_A, "nonliving"),
    RF_METAL(MonsterRaceCategory.RFT_RACE_A, "metal"),
    RF_HURT_LIGHT(MonsterRaceCategory.RFT_VULN, "bright light"),
    RF_HURT_ROCK(MonsterRaceCategory.RFT_VULN, "rock remover"),
    RF_HURT_FIRE(MonsterRaceCategory.RFT_VULN_I, "fire"),
    RF_HURT_COLD(MonsterRaceCategory.RFT_VULN_I, "cold"),
    RF_IM_ACID(MonsterRaceCategory.RFT_RES, "acid"),
    RF_IM_ELEC(MonsterRaceCategory.RFT_RES, "lightning"),
    RF_IM_FIRE(MonsterRaceCategory.RFT_RES, "fire"),
    RF_IM_COLD(MonsterRaceCategory.RFT_RES, "cold"),
    RF_IM_POIS(MonsterRaceCategory.RFT_RES, "poison"),
    RF_IM_NETHER(MonsterRaceCategory.RFT_RES, "nether"),
    RF_IM_WATER(MonsterRaceCategory.RFT_RES, "water"),
    RF_IM_PLASMA(MonsterRaceCategory.RFT_RES, "plasma"),
    RF_IM_NEXUS(MonsterRaceCategory.RFT_RES, "nexus"),
    RF_IM_DISEN(MonsterRaceCategory.RFT_RES, "disenchantment"),
    RF_NO_FEAR(MonsterRaceCategory.RFT_PROT, "frightened"),
    RF_NO_STUN(MonsterRaceCategory.RFT_PROT, "stunned"),
    RF_NO_CONF(MonsterRaceCategory.RFT_PROT, "confused"),
    RF_NO_SLEEP(MonsterRaceCategory.RFT_PROT, "slept"),
    RF_NO_HOLD(MonsterRaceCategory.RFT_PROT, "held"),
    RF_NO_SLOW(MonsterRaceCategory.RFT_PROT, "slowed");

    private MonsterRaceCategory category;
    private String description;

    MonsterRaceFlag(MonsterRaceCategory category, String description) {
        this.category = category;
        this.description = description;
    }

    public MonsterRaceCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }
}