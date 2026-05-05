package uk.co.jackoftrades.middle.monsters.enums;

public enum MonsterRaceCategory {
    RFT_NONE,    /* placeholder flag */
    RFT_OBV,        /* an obvious property */
    RFT_DISP,        /* for display purposes */
    RFT_GEN,        /* related to generation */
    RFT_NOTE,        /* especially noteworthy for lore */
    RFT_BEHAV,        /* behaviour-related */
    RFT_DROP,        /* drop details */
    RFT_DET,        /* detection properties */
    RFT_ALTER,        /* environment shaping */
    RFT_RACE_N,        /* types of monster (noun) */
    RFT_RACE_A,        /* types of monster (adjective) */
    RFT_VULN,        /* vulnerabilities with no corresponding resistance */
    RFT_VULN_I,        /* vulnerabilities with a corresponding resistance */
    RFT_RES,        /* elemental resistances */
    RFT_PROT,        /* immunity from status effects */

    RFT_MAX
}
