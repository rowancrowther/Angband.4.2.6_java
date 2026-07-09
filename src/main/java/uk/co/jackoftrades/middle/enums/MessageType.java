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

package uk.co.jackoftrades.middle.enums;

/**
 * Every kind of in-game message/sound event, each paired with the short string
 * key used to look up its configured sound. This is the Java port of the C
 * original's {@code MSG_*} list ({@code src/list-message-sounds.h}); the
 * constants are self-describing and are documented collectively here.
 * {@code MSG_MAX} is the count sentinel.
 *
 * @author Rowan Crowther
 */
public enum MessageType {
    MSG_NONE(""),
    MSG_GENERIC(""),
    MSG_BIRTH(""),
    MSG_HIT("hit"),
    MSG_MISS("miss"),
    MSG_FLEE("flee"),
    MSG_DROP("drop"),
    MSG_KILL("kill"),
    MSG_LEVEL("level"),
    MSG_DEATH("death"),
    MSG_STUDY("study"),
    MSG_TELEPORT("teleport"),
    MSG_SHOOT("shoot"),
    MSG_QUAFF("quaff"),
    MSG_ZAP_ROD("zap_rod"),
    MSG_WALK("walk"),
    MSG_TPOTHER("tpother"),
    MSG_HITWALL("hitwall"),
    MSG_EAT("eat"),
    MSG_STORE1("store1"),
    MSG_STORE2("store2"),
    MSG_STORE3("store3"),
    MSG_STORE4("store4"),
    MSG_DIG("dig"),
    MSG_OPENDOOR("opendoor"),
    MSG_SHUTDOOR("shutdoor"),
    MSG_TPLEVEL("tplevel"),
    MSG_BELL("bell"),
    MSG_NOTHING_TO_OPEN("nothing_to_open"),
    MSG_LOCKPICK_FAIL("lockpick_fail"),
    MSG_STAIRS_DOWN("stairs_down"),
    MSG_HITPOINT_WARN("hitpoint_warn"),
    MSG_ACT_ARTIFACT("act_artifact"),
    MSG_USE_STAFF("use_staff"),
    MSG_DESTROY("destroy"),
    MSG_MON_HIT("mon_hit"),
    MSG_MON_TOUCH("mon_touch"),
    MSG_MON_PUNCH("mon_punch"),
    MSG_MON_KICK("mon_kick"),
    MSG_MON_CLAW("mon_claw"),
    MSG_MON_BITE("mon_bite"),
    MSG_MON_STING("mon_sting"),
    MSG_MON_BUTT("mon_butt"),
    MSG_MON_CRUSH("mon_crush"),
    MSG_MON_ENGULF("mon_engulf"),
    MSG_MON_CRAWL("mon_crawl"),
    MSG_MON_DROOL("mon_drool"),
    MSG_MON_SPIT("mon_spit"),
    MSG_MON_GAZE("mon_gaze"),
    MSG_MON_WAIL("mon_wail"),
    MSG_MON_SPORE("mon_spore"),
    MSG_MON_BEG("mon_beg"),
    MSG_MON_INSULT("mon_insult"),
    MSG_MON_MOAN("mon_moan"),
    MSG_RECOVER("recover"),
    MSG_BLIND("blind"),
    MSG_CONFUSED("confused"),
    MSG_POISONED("poisoned"),
    MSG_AFRAID("afraid"),
    MSG_PARALYZED("paralyzed"),
    MSG_DRUGGED("drugged"),
    MSG_SPEED("speed"),
    MSG_SLOW("slow"),
    MSG_SHIELD("shield"),
    MSG_BLESSED("blessed"),
    MSG_HERO("hero"),
    MSG_BERSERK("berserk"),
    MSG_BOLD("bold"),
    MSG_PROT_EVIL("prot_evil"),
    MSG_INVULN("invuln"),
    MSG_SEE_INVIS("see_invis"),
    MSG_INFRARED("infrared"),
    MSG_RES_ACID("res_acid"),
    MSG_RES_ELEC("res_elec"),
    MSG_RES_FIRE("res_fire"),
    MSG_RES_COLD("res_cold"),
    MSG_RES_POIS("res_pois"),
    MSG_STUN("stun"),
    MSG_CUT("cut"),
    MSG_STAIRS_UP("stairs_up"),
    MSG_STORE_ENTER("store_enter"),
    MSG_STORE_LEAVE("store_leave"),
    MSG_STORE_HOME("store_home"),
    MSG_MONEY1("money1"),
    MSG_MONEY2("money2"),
    MSG_MONEY3("money3"),
    MSG_SHOOT_HIT("shoot_hit"),
    MSG_STORE5("store5"),
    MSG_LOCKPICK("lockpick"),
    MSG_DISARM("disarm"),
    MSG_IDENT_BAD("identify_bad"),
    MSG_IDENT_EGO("identify_ego"),
    MSG_IDENT_ART("identify_art"),
    MSG_BR_ELEMENTS("breathe_elements"),
    MSG_BR_FROST("breathe_frost"),
    MSG_BR_ELEC("breathe_elec"),
    MSG_BR_ACID("breathe_acid"),
    MSG_BR_GAS("breathe_gas"),
    MSG_BR_FIRE("breathe_fire"),
    MSG_BR_DISEN("breathe_disenchant"),
    MSG_BR_CHAOS("breathe_chaos"),
    MSG_BR_SHARDS("breathe_shards"),
    MSG_BR_SOUND("breathe_sound"),
    MSG_BR_LIGHT("breathe_light"),
    MSG_BR_DARK("breathe_dark"),
    MSG_BR_NETHER("breathe_nether"),
    MSG_BR_NEXUS("breathe_nexus"),
    MSG_BR_TIME("breathe_time"),
    MSG_BR_INERTIA("breathe_inertia"),
    MSG_BR_GRAVITY("breathe_gravity"),
    MSG_BR_PLASMA("breathe_plasma"),
    MSG_BR_FORCE("breathe_force"),
    MSG_SUM_MONSTER("summon_monster"),
    MSG_SUM_AINU("summon_ainu"),
    MSG_SUM_UNDEAD("summon_undead"),
    MSG_SUM_ANIMAL("summon_animal"),
    MSG_SUM_SPIDER("summon_spider"),
    MSG_SUM_HOUND("summon_hound"),
    MSG_SUM_HYDRA("summon_hydra"),
    MSG_SUM_DEMON("summon_demon"),
    MSG_SUM_DRAGON("summon_dragon"),
    MSG_SUM_HI_UNDEAD("summon_gr_undead"),
    MSG_SUM_HI_DRAGON("summon_gr_dragon"),
    MSG_SUM_HI_DEMON("summon_gr_demon"),
    MSG_SUM_WRAITH("summon_ringwraith"),
    MSG_SUM_UNIQUE("summon_unique"),
    MSG_WIELD("wield"),
    MSG_QUIVER("quiver"),
    MSG_CURSED("cursed"),
    MSG_RUNE("rune"),
    MSG_HUNGRY("hungry"),
    MSG_NOTICE("notice"),
    MSG_AMBIENT_DAY("ambient_day"),
    MSG_AMBIENT_NITE("ambient_nite"),
    MSG_AMBIENT_DNG1("ambient_dng1"),
    MSG_AMBIENT_DNG2("ambient_dng2"),
    MSG_AMBIENT_DNG3("ambient_dng3"),
    MSG_AMBIENT_DNG4("ambient_dng4"),
    MSG_AMBIENT_DNG5("ambient_dng5"),
    MSG_CREATE_TRAP("mon_create_trap"),
    MSG_SHRIEK("mon_shriek"),
    MSG_CAST_FEAR("mon_cast_fear"),
    MSG_HIT_GOOD("hit_good"),
    MSG_HIT_GREAT("hit_great"),
    MSG_HIT_SUPERB("hit_superb"),
    MSG_HIT_HI_GREAT("hit_hi_great"),
    MSG_HIT_HI_SUPERB("hit_hi_superb"),
    MSG_SPELL("cast_spell"),
    MSG_PRAYER("pray_prayer"),
    MSG_KILL_UNIQUE("kill_unique"),
    MSG_KILL_KING("kill_king"),
    MSG_DRAIN_STAT("drain_stat"),
    MSG_MULTIPLY("multiply"),
    MSG_SCRAMBLE("scramble"),
    MSG_MAX("");

    /**
     * The short key identifying this message's configured sound.
     *
     * @author Rowan Crowther
     */
    private String messageString;

    /**
     * Bind a message type to its sound key.
     *
     * @param messageString the sound-lookup key
     * @author Rowan Crowther
     */
    MessageType(String messageString) {
        this.messageString = messageString;
    }
}
