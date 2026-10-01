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

package uk.co.jackoftradesltd.middle.enums;

/**
 * Every kind of in-game message/sound event, each paired with the short string
 * key used to look up its configured sound. This is the Java port of the C
 * original's {@code MSG_*} list ({@code list-message.h}, expanded into an enum in
 * {@code message.h}); each constant carries its own block. The constants {@code MSG_GENERIC} through {@code MSG_SCRAMBLE}
 * match the C list one for one, in the same order, with the same sound keys.
 *
 * <p>Two deliberate differences from C:
 * <ul>
 *   <li>{@code MSG_NONE} is Java-only. It is a "no message type" default (used, for
 *       example, by {@code SummonAssembler} before parsing) and has no C counterpart. Because
 *       it is declared first, every ordinal is one higher than the C enum value, so do not
 *       use {@code ordinal()} as if it were the C {@code MSG_*} number.</li>
 *   <li>{@code MSG_MAX} is the end sentinel, as in C, and its sound key is {@code null},
 *       matching the C {@code NULL}.</li>
 * </ul>
 *
 * <p>Class MessageType coded before 260815, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public enum MessageType {
    /**
     * No message type; the Java-only "unset" default, with no C counterpart. Ordinal 0.
     * <p>Constant MSG_NONE coded before 260815, commented in full on 261001.
     */
    MSG_NONE(""),
    /** A generic message with no particular sound; C value 0.
     * <p>Constant MSG_GENERIC coded before 260815, commented in full on 261001. */
    MSG_GENERIC(""),
    /** A character-creation (birth) message; has no sound.
     * <p>Constant MSG_BIRTH coded before 260815, commented in full on 261001. */
    MSG_BIRTH(""),
    /** The player hits a monster.
     * <p>Constant MSG_HIT coded before 260815, commented in full on 261001. */
    MSG_HIT("hit"),
    /** The player misses a monster.
     * <p>Constant MSG_MISS coded before 260815, commented in full on 261001. */
    MSG_MISS("miss"),
    /** A monster flees.
     * <p>Constant MSG_FLEE coded before 260815, commented in full on 261001. */
    MSG_FLEE("flee"),
    /** An object is dropped.
     * <p>Constant MSG_DROP coded before 260815, commented in full on 261001. */
    MSG_DROP("drop"),
    /** A monster is killed.
     * <p>Constant MSG_KILL coded before 260815, commented in full on 261001. */
    MSG_KILL("kill"),
    /** The player gains a level.
     * <p>Constant MSG_LEVEL coded before 260815, commented in full on 261001. */
    MSG_LEVEL("level"),
    /** The player dies.
     * <p>Constant MSG_DEATH coded before 260815, commented in full on 261001. */
    MSG_DEATH("death"),
    /** The player learns a new spell.
     * <p>Constant MSG_STUDY coded before 260815, commented in full on 261001. */
    MSG_STUDY("study"),
    /** The player is teleported.
     * <p>Constant MSG_TELEPORT coded before 260815, commented in full on 261001. */
    MSG_TELEPORT("teleport"),
    /** The player fires a missile.
     * <p>Constant MSG_SHOOT coded before 260815, commented in full on 261001. */
    MSG_SHOOT("shoot"),
    /** The player quaffs a potion.
     * <p>Constant MSG_QUAFF coded before 260815, commented in full on 261001. */
    MSG_QUAFF("quaff"),
    /** The player zaps a rod.
     * <p>Constant MSG_ZAP_ROD coded before 260815, commented in full on 261001. */
    MSG_ZAP_ROD("zap_rod"),
    /** The player walks.
     * <p>Constant MSG_WALK coded before 260815, commented in full on 261001. */
    MSG_WALK("walk"),
    /** A monster is teleported away.
     * <p>Constant MSG_TPOTHER coded before 260815, commented in full on 261001. */
    MSG_TPOTHER("tpother"),
    /** The player walks into a wall.
     * <p>Constant MSG_HITWALL coded before 260815, commented in full on 261001. */
    MSG_HITWALL("hitwall"),
    /** The player eats.
     * <p>Constant MSG_EAT coded before 260815, commented in full on 261001. */
    MSG_EAT("eat"),
    /** Shopkeeper comment on an offer, grade 1 (worthless).
     * <p>Constant MSG_STORE1 coded before 260815, commented in full on 261001. */
    MSG_STORE1("store1"),
    /** Shopkeeper comment on an offer, grade 2 (bad).
     * <p>Constant MSG_STORE2 coded before 260815, commented in full on 261001. */
    MSG_STORE2("store2"),
    /** Shopkeeper comment on an offer, grade 3 (good).
     * <p>Constant MSG_STORE3 coded before 260815, commented in full on 261001. */
    MSG_STORE3("store3"),
    /** Shopkeeper comment on an offer, grade 4 (great).
     * <p>Constant MSG_STORE4 coded before 260815, commented in full on 261001. */
    MSG_STORE4("store4"),
    /** The player digs.
     * <p>Constant MSG_DIG coded before 260815, commented in full on 261001. */
    MSG_DIG("dig"),
    /** A door is opened.
     * <p>Constant MSG_OPENDOOR coded before 260815, commented in full on 261001. */
    MSG_OPENDOOR("opendoor"),
    /** A door is shut.
     * <p>Constant MSG_SHUTDOOR coded before 260815, commented in full on 261001. */
    MSG_SHUTDOOR("shutdoor"),
    /** The player teleports to another level.
     * <p>Constant MSG_TPLEVEL coded before 260815, commented in full on 261001. */
    MSG_TPLEVEL("tplevel"),
    /** The bell sound.
     * <p>Constant MSG_BELL coded before 260815, commented in full on 261001. */
    MSG_BELL("bell"),
    /** There is nothing to open.
     * <p>Constant MSG_NOTHING_TO_OPEN coded before 260815, commented in full on 261001. */
    MSG_NOTHING_TO_OPEN("nothing_to_open"),
    /** A lock-picking attempt fails.
     * <p>Constant MSG_LOCKPICK_FAIL coded before 260815, commented in full on 261001. */
    MSG_LOCKPICK_FAIL("lockpick_fail"),
    /** The player takes a down staircase.
     * <p>Constant MSG_STAIRS_DOWN coded before 260815, commented in full on 261001. */
    MSG_STAIRS_DOWN("stairs_down"),
    /** The player's hitpoints have dropped below the warning level.
     * <p>Constant MSG_HITPOINT_WARN coded before 260815, commented in full on 261001. */
    MSG_HITPOINT_WARN("hitpoint_warn"),
    /** An artifact is activated.
     * <p>Constant MSG_ACT_ARTIFACT coded before 260815, commented in full on 261001. */
    MSG_ACT_ARTIFACT("act_artifact"),
    /** The player uses a staff.
     * <p>Constant MSG_USE_STAFF coded before 260815, commented in full on 261001. */
    MSG_USE_STAFF("use_staff"),
    /** Something is destroyed.
     * <p>Constant MSG_DESTROY coded before 260815, commented in full on 261001. */
    MSG_DESTROY("destroy"),
    /** A monster hits the player.
     * <p>Constant MSG_MON_HIT coded before 260815, commented in full on 261001. */
    MSG_MON_HIT("mon_hit"),
    /** A monster touches the player.
     * <p>Constant MSG_MON_TOUCH coded before 260815, commented in full on 261001. */
    MSG_MON_TOUCH("mon_touch"),
    /** A monster punches the player.
     * <p>Constant MSG_MON_PUNCH coded before 260815, commented in full on 261001. */
    MSG_MON_PUNCH("mon_punch"),
    /** A monster kicks the player.
     * <p>Constant MSG_MON_KICK coded before 260815, commented in full on 261001. */
    MSG_MON_KICK("mon_kick"),
    /** A monster claws the player.
     * <p>Constant MSG_MON_CLAW coded before 260815, commented in full on 261001. */
    MSG_MON_CLAW("mon_claw"),
    /** A monster bites the player.
     * <p>Constant MSG_MON_BITE coded before 260815, commented in full on 261001. */
    MSG_MON_BITE("mon_bite"),
    /** A monster stings the player.
     * <p>Constant MSG_MON_STING coded before 260815, commented in full on 261001. */
    MSG_MON_STING("mon_sting"),
    /** A monster butts the player.
     * <p>Constant MSG_MON_BUTT coded before 260815, commented in full on 261001. */
    MSG_MON_BUTT("mon_butt"),
    /** A monster crushes the player.
     * <p>Constant MSG_MON_CRUSH coded before 260815, commented in full on 261001. */
    MSG_MON_CRUSH("mon_crush"),
    /** A monster engulfs the player.
     * <p>Constant MSG_MON_ENGULF coded before 260815, commented in full on 261001. */
    MSG_MON_ENGULF("mon_engulf"),
    /** A monster crawls on the player.
     * <p>Constant MSG_MON_CRAWL coded before 260815, commented in full on 261001. */
    MSG_MON_CRAWL("mon_crawl"),
    /** A monster drools on the player.
     * <p>Constant MSG_MON_DROOL coded before 260815, commented in full on 261001. */
    MSG_MON_DROOL("mon_drool"),
    /** A monster spits at the player.
     * <p>Constant MSG_MON_SPIT coded before 260815, commented in full on 261001. */
    MSG_MON_SPIT("mon_spit"),
    /** A monster gazes at the player.
     * <p>Constant MSG_MON_GAZE coded before 260815, commented in full on 261001. */
    MSG_MON_GAZE("mon_gaze"),
    /** A monster wails at the player.
     * <p>Constant MSG_MON_WAIL coded before 260815, commented in full on 261001. */
    MSG_MON_WAIL("mon_wail"),
    /** A monster releases spores at the player.
     * <p>Constant MSG_MON_SPORE coded before 260815, commented in full on 261001. */
    MSG_MON_SPORE("mon_spore"),
    /** A monster begs from the player.
     * <p>Constant MSG_MON_BEG coded before 260815, commented in full on 261001. */
    MSG_MON_BEG("mon_beg"),
    /** A monster insults the player.
     * <p>Constant MSG_MON_INSULT coded before 260815, commented in full on 261001. */
    MSG_MON_INSULT("mon_insult"),
    /** A monster moans at the player.
     * <p>Constant MSG_MON_MOAN coded before 260815, commented in full on 261001. */
    MSG_MON_MOAN("mon_moan"),
    /** The player recovers from a timed effect.
     * <p>Constant MSG_RECOVER coded before 260815, commented in full on 261001. */
    MSG_RECOVER("recover"),
    /** The player is blinded.
     * <p>Constant MSG_BLIND coded before 260815, commented in full on 261001. */
    MSG_BLIND("blind"),
    /** The player is confused.
     * <p>Constant MSG_CONFUSED coded before 260815, commented in full on 261001. */
    MSG_CONFUSED("confused"),
    /** The player is poisoned.
     * <p>Constant MSG_POISONED coded before 260815, commented in full on 261001. */
    MSG_POISONED("poisoned"),
    /** The player is afraid.
     * <p>Constant MSG_AFRAID coded before 260815, commented in full on 261001. */
    MSG_AFRAID("afraid"),
    /** The player is paralyzed.
     * <p>Constant MSG_PARALYZED coded before 260815, commented in full on 261001. */
    MSG_PARALYZED("paralyzed"),
    /** The player is drugged.
     * <p>Constant MSG_DRUGGED coded before 260815, commented in full on 261001. */
    MSG_DRUGGED("drugged"),
    /** The player is hasted.
     * <p>Constant MSG_SPEED coded before 260815, commented in full on 261001. */
    MSG_SPEED("speed"),
    /** The player is slowed.
     * <p>Constant MSG_SLOW coded before 260815, commented in full on 261001. */
    MSG_SLOW("slow"),
    /** The player gains a magical shield.
     * <p>Constant MSG_SHIELD coded before 260815, commented in full on 261001. */
    MSG_SHIELD("shield"),
    /** The player is blessed.
     * <p>Constant MSG_BLESSED coded before 260815, commented in full on 261001. */
    MSG_BLESSED("blessed"),
    /** The player is a hero.
     * <p>Constant MSG_HERO coded before 260815, commented in full on 261001. */
    MSG_HERO("hero"),
    /** The player goes berserk.
     * <p>Constant MSG_BERSERK coded before 260815, commented in full on 261001. */
    MSG_BERSERK("berserk"),
    /** The player is bold, protected from fear.
     * <p>Constant MSG_BOLD coded before 260815, commented in full on 261001. */
    MSG_BOLD("bold"),
    /** The player is protected from evil.
     * <p>Constant MSG_PROT_EVIL coded before 260815, commented in full on 261001. */
    MSG_PROT_EVIL("prot_evil"),
    /** The player is invulnerable.
     * <p>Constant MSG_INVULN coded before 260815, commented in full on 261001. */
    MSG_INVULN("invuln"),
    /** The player can see invisible monsters.
     * <p>Constant MSG_SEE_INVIS coded before 260815, commented in full on 261001. */
    MSG_SEE_INVIS("see_invis"),
    /** The player's infravision is enhanced.
     * <p>Constant MSG_INFRARED coded before 260815, commented in full on 261001. */
    MSG_INFRARED("infrared"),
    /** The player gains temporary resistance to acid.
     * <p>Constant MSG_RES_ACID coded before 260815, commented in full on 261001. */
    MSG_RES_ACID("res_acid"),
    /** The player gains temporary resistance to lightning.
     * <p>Constant MSG_RES_ELEC coded before 260815, commented in full on 261001. */
    MSG_RES_ELEC("res_elec"),
    /** The player gains temporary resistance to fire.
     * <p>Constant MSG_RES_FIRE coded before 260815, commented in full on 261001. */
    MSG_RES_FIRE("res_fire"),
    /** The player gains temporary resistance to cold.
     * <p>Constant MSG_RES_COLD coded before 260815, commented in full on 261001. */
    MSG_RES_COLD("res_cold"),
    /** The player gains temporary resistance to poison.
     * <p>Constant MSG_RES_POIS coded before 260815, commented in full on 261001. */
    MSG_RES_POIS("res_pois"),
    /** The player is stunned.
     * <p>Constant MSG_STUN coded before 260815, commented in full on 261001. */
    MSG_STUN("stun"),
    /** The player is cut.
     * <p>Constant MSG_CUT coded before 260815, commented in full on 261001. */
    MSG_CUT("cut"),
    /** The player takes an up staircase.
     * <p>Constant MSG_STAIRS_UP coded before 260815, commented in full on 261001. */
    MSG_STAIRS_UP("stairs_up"),
    /** The player enters a store.
     * <p>Constant MSG_STORE_ENTER coded before 260815, commented in full on 261001. */
    MSG_STORE_ENTER("store_enter"),
    /** The player leaves a store.
     * <p>Constant MSG_STORE_LEAVE coded before 260815, commented in full on 261001. */
    MSG_STORE_LEAVE("store_leave"),
    /** The player enters the home.
     * <p>Constant MSG_STORE_HOME coded before 260815, commented in full on 261001. */
    MSG_STORE_HOME("store_home"),
    /** The player picks up gold worth under 200.
     * <p>Constant MSG_MONEY1 coded before 260815, commented in full on 261001. */
    MSG_MONEY1("money1"),
    /** The player picks up gold worth 200 to 599.
     * <p>Constant MSG_MONEY2 coded before 260815, commented in full on 261001. */
    MSG_MONEY2("money2"),
    /** The player picks up gold worth 600 or more.
     * <p>Constant MSG_MONEY3 coded before 260815, commented in full on 261001. */
    MSG_MONEY3("money3"),
    /** A fired missile hits a monster.
     * <p>Constant MSG_SHOOT_HIT coded before 260815, commented in full on 261001. */
    MSG_SHOOT_HIT("shoot_hit"),
    /** Shopkeeper comment when an offer is accepted.
     * <p>Constant MSG_STORE5 coded before 260815, commented in full on 261001. */
    MSG_STORE5("store5"),
    /** A lock is picked.
     * <p>Constant MSG_LOCKPICK coded before 260815, commented in full on 261001. */
    MSG_LOCKPICK("lockpick"),
    /** A trap is disarmed.
     * <p>Constant MSG_DISARM coded before 260815, commented in full on 261001. */
    MSG_DISARM("disarm"),
    /** An object is identified as bad.
     * <p>Constant MSG_IDENT_BAD coded before 260815, commented in full on 261001. */
    MSG_IDENT_BAD("identify_bad"),
    /** An object is identified as an ego item.
     * <p>Constant MSG_IDENT_EGO coded before 260815, commented in full on 261001. */
    MSG_IDENT_EGO("identify_ego"),
    /** An object is identified as an artifact.
     * <p>Constant MSG_IDENT_ART coded before 260815, commented in full on 261001. */
    MSG_IDENT_ART("identify_art"),
    /** A monster breathes the elements.
     * <p>Constant MSG_BR_ELEMENTS coded before 260815, commented in full on 261001. */
    MSG_BR_ELEMENTS("breathe_elements"),
    /** A monster breathes frost.
     * <p>Constant MSG_BR_FROST coded before 260815, commented in full on 261001. */
    MSG_BR_FROST("breathe_frost"),
    /** A monster breathes lightning.
     * <p>Constant MSG_BR_ELEC coded before 260815, commented in full on 261001. */
    MSG_BR_ELEC("breathe_elec"),
    /** A monster breathes acid.
     * <p>Constant MSG_BR_ACID coded before 260815, commented in full on 261001. */
    MSG_BR_ACID("breathe_acid"),
    /** A monster breathes poison gas.
     * <p>Constant MSG_BR_GAS coded before 260815, commented in full on 261001. */
    MSG_BR_GAS("breathe_gas"),
    /** A monster breathes fire.
     * <p>Constant MSG_BR_FIRE coded before 260815, commented in full on 261001. */
    MSG_BR_FIRE("breathe_fire"),
    /** A monster breathes disenchantment.
     * <p>Constant MSG_BR_DISEN coded before 260815, commented in full on 261001. */
    MSG_BR_DISEN("breathe_disenchant"),
    /** A monster breathes chaos.
     * <p>Constant MSG_BR_CHAOS coded before 260815, commented in full on 261001. */
    MSG_BR_CHAOS("breathe_chaos"),
    /** A monster breathes shards.
     * <p>Constant MSG_BR_SHARDS coded before 260815, commented in full on 261001. */
    MSG_BR_SHARDS("breathe_shards"),
    /** A monster breathes sound.
     * <p>Constant MSG_BR_SOUND coded before 260815, commented in full on 261001. */
    MSG_BR_SOUND("breathe_sound"),
    /** A monster breathes light.
     * <p>Constant MSG_BR_LIGHT coded before 260815, commented in full on 261001. */
    MSG_BR_LIGHT("breathe_light"),
    /** A monster breathes darkness.
     * <p>Constant MSG_BR_DARK coded before 260815, commented in full on 261001. */
    MSG_BR_DARK("breathe_dark"),
    /** A monster breathes nether.
     * <p>Constant MSG_BR_NETHER coded before 260815, commented in full on 261001. */
    MSG_BR_NETHER("breathe_nether"),
    /** A monster breathes nexus.
     * <p>Constant MSG_BR_NEXUS coded before 260815, commented in full on 261001. */
    MSG_BR_NEXUS("breathe_nexus"),
    /** A monster breathes time.
     * <p>Constant MSG_BR_TIME coded before 260815, commented in full on 261001. */
    MSG_BR_TIME("breathe_time"),
    /** A monster breathes inertia.
     * <p>Constant MSG_BR_INERTIA coded before 260815, commented in full on 261001. */
    MSG_BR_INERTIA("breathe_inertia"),
    /** A monster breathes gravity.
     * <p>Constant MSG_BR_GRAVITY coded before 260815, commented in full on 261001. */
    MSG_BR_GRAVITY("breathe_gravity"),
    /** A monster breathes plasma.
     * <p>Constant MSG_BR_PLASMA coded before 260815, commented in full on 261001. */
    MSG_BR_PLASMA("breathe_plasma"),
    /** A monster breathes force.
     * <p>Constant MSG_BR_FORCE coded before 260815, commented in full on 261001. */
    MSG_BR_FORCE("breathe_force"),
    /** A monster summons a monster.
     * <p>Constant MSG_SUM_MONSTER coded before 260815, commented in full on 261001. */
    MSG_SUM_MONSTER("summon_monster"),
    /** A monster summons an Ainu.
     * <p>Constant MSG_SUM_AINU coded before 260815, commented in full on 261001. */
    MSG_SUM_AINU("summon_ainu"),
    /** A monster summons an undead.
     * <p>Constant MSG_SUM_UNDEAD coded before 260815, commented in full on 261001. */
    MSG_SUM_UNDEAD("summon_undead"),
    /** A monster summons an animal.
     * <p>Constant MSG_SUM_ANIMAL coded before 260815, commented in full on 261001. */
    MSG_SUM_ANIMAL("summon_animal"),
    /** A monster summons a spider.
     * <p>Constant MSG_SUM_SPIDER coded before 260815, commented in full on 261001. */
    MSG_SUM_SPIDER("summon_spider"),
    /** A monster summons a hound.
     * <p>Constant MSG_SUM_HOUND coded before 260815, commented in full on 261001. */
    MSG_SUM_HOUND("summon_hound"),
    /** A monster summons a hydra.
     * <p>Constant MSG_SUM_HYDRA coded before 260815, commented in full on 261001. */
    MSG_SUM_HYDRA("summon_hydra"),
    /** A monster summons a demon.
     * <p>Constant MSG_SUM_DEMON coded before 260815, commented in full on 261001. */
    MSG_SUM_DEMON("summon_demon"),
    /** A monster summons a dragon.
     * <p>Constant MSG_SUM_DRAGON coded before 260815, commented in full on 261001. */
    MSG_SUM_DRAGON("summon_dragon"),
    /** A monster summons a greater undead.
     * <p>Constant MSG_SUM_HI_UNDEAD coded before 260815, commented in full on 261001. */
    MSG_SUM_HI_UNDEAD("summon_gr_undead"),
    /** A monster summons a greater dragon.
     * <p>Constant MSG_SUM_HI_DRAGON coded before 260815, commented in full on 261001. */
    MSG_SUM_HI_DRAGON("summon_gr_dragon"),
    /** A monster summons a greater demon.
     * <p>Constant MSG_SUM_HI_DEMON coded before 260815, commented in full on 261001. */
    MSG_SUM_HI_DEMON("summon_gr_demon"),
    /** A monster summons a ringwraith.
     * <p>Constant MSG_SUM_WRAITH coded before 260815, commented in full on 261001. */
    MSG_SUM_WRAITH("summon_ringwraith"),
    /** A monster summons a unique.
     * <p>Constant MSG_SUM_UNIQUE coded before 260815, commented in full on 261001. */
    MSG_SUM_UNIQUE("summon_unique"),
    /** The player wields an object.
     * <p>Constant MSG_WIELD coded before 260815, commented in full on 261001. */
    MSG_WIELD("wield"),
    /** The player puts ammunition in the quiver.
     * <p>Constant MSG_QUIVER coded before 260815, commented in full on 261001. */
    MSG_QUIVER("quiver"),
    /** An object is found to be cursed.
     * <p>Constant MSG_CURSED coded before 260815, commented in full on 261001. */
    MSG_CURSED("cursed"),
    /** The player learns a rune.
     * <p>Constant MSG_RUNE coded before 260815, commented in full on 261001. */
    MSG_RUNE("rune"),
    /** The player is hungry.
     * <p>Constant MSG_HUNGRY coded before 260815, commented in full on 261001. */
    MSG_HUNGRY("hungry"),
    /** A notice message.
     * <p>Constant MSG_NOTICE coded before 260815, commented in full on 261001. */
    MSG_NOTICE("notice"),
    /** Ambient sound for daytime in the town.
     * <p>Constant MSG_AMBIENT_DAY coded before 260815, commented in full on 261001. */
    MSG_AMBIENT_DAY("ambient_day"),
    /** Ambient sound for night-time in the town.
     * <p>Constant MSG_AMBIENT_NITE coded before 260815, commented in full on 261001. */
    MSG_AMBIENT_NITE("ambient_nite"),
    /** Ambient sound for dungeon depths 1 to 20.
     * <p>Constant MSG_AMBIENT_DNG1 coded before 260815, commented in full on 261001. */
    MSG_AMBIENT_DNG1("ambient_dng1"),
    /** Ambient sound for dungeon depths 21 to 40.
     * <p>Constant MSG_AMBIENT_DNG2 coded before 260815, commented in full on 261001. */
    MSG_AMBIENT_DNG2("ambient_dng2"),
    /** Ambient sound for dungeon depths 41 to 60.
     * <p>Constant MSG_AMBIENT_DNG3 coded before 260815, commented in full on 261001. */
    MSG_AMBIENT_DNG3("ambient_dng3"),
    /** Ambient sound for dungeon depths 61 to 80.
     * <p>Constant MSG_AMBIENT_DNG4 coded before 260815, commented in full on 261001. */
    MSG_AMBIENT_DNG4("ambient_dng4"),
    /** Ambient sound for dungeon depths below 80.
     * <p>Constant MSG_AMBIENT_DNG5 coded before 260815, commented in full on 261001. */
    MSG_AMBIENT_DNG5("ambient_dng5"),
    /** A monster creates traps.
     * <p>Constant MSG_CREATE_TRAP coded before 260815, commented in full on 261001. */
    MSG_CREATE_TRAP("mon_create_trap"),
    /** A monster shrieks.
     * <p>Constant MSG_SHRIEK coded before 260815, commented in full on 261001. */
    MSG_SHRIEK("mon_shriek"),
    /** A monster casts a fear spell.
     * <p>Constant MSG_CAST_FEAR coded before 260815, commented in full on 261001. */
    MSG_CAST_FEAR("mon_cast_fear"),
    /** The player lands a good hit.
     * <p>Constant MSG_HIT_GOOD coded before 260815, commented in full on 261001. */
    MSG_HIT_GOOD("hit_good"),
    /** The player lands a great hit.
     * <p>Constant MSG_HIT_GREAT coded before 260815, commented in full on 261001. */
    MSG_HIT_GREAT("hit_great"),
    /** The player lands a superb hit.
     * <p>Constant MSG_HIT_SUPERB coded before 260815, commented in full on 261001. */
    MSG_HIT_SUPERB("hit_superb"),
    /** The player lands a *GREAT* hit.
     * <p>Constant MSG_HIT_HI_GREAT coded before 260815, commented in full on 261001. */
    MSG_HIT_HI_GREAT("hit_hi_great"),
    /** The player lands a *SUPERB* hit.
     * <p>Constant MSG_HIT_HI_SUPERB coded before 260815, commented in full on 261001. */
    MSG_HIT_HI_SUPERB("hit_hi_superb"),
    /** The player casts a spell.
     * <p>Constant MSG_SPELL coded before 260815, commented in full on 261001. */
    MSG_SPELL("cast_spell"),
    /** The player says a prayer.
     * <p>Constant MSG_PRAYER coded before 260815, commented in full on 261001. */
    MSG_PRAYER("pray_prayer"),
    /** A unique monster, other than Morgoth, is killed.
     * <p>Constant MSG_KILL_UNIQUE coded before 260815, commented in full on 261001. */
    MSG_KILL_UNIQUE("kill_unique"),
    /** A unique of the Morgoth monster base is killed.
     * <p>Constant MSG_KILL_KING coded before 260815, commented in full on 261001. */
    MSG_KILL_KING("kill_king"),
    /** One of the player's stats is drained.
     * <p>Constant MSG_DRAIN_STAT coded before 260815, commented in full on 261001. */
    MSG_DRAIN_STAT("drain_stat"),
    /** A monster multiplies.
     * <p>Constant MSG_MULTIPLY coded before 260815, commented in full on 261001. */
    MSG_MULTIPLY("multiply"),
    /** A scramble message; the C list gives it the sound key "scramble".
     * <p>Constant MSG_SCRAMBLE coded before 260815, commented in full on 261001. */
    MSG_SCRAMBLE("scramble"),
    /**
     * End-of-list sentinel and count marker; the sound key is {@code null}, as in C.
     * <p>Constant MSG_MAX coded before 260815, commented in full on 261001.
     */
    MSG_MAX(null);

    /**
     * The short key identifying this message's configured sound: the second column of
     * {@code list-message.h}, which names the matching entry in {@code sound.prf}. It is
     * empty for {@code MSG_NONE}, {@code MSG_GENERIC} and {@code MSG_BIRTH}, and
     * {@code null} for {@code MSG_MAX}.
     *
     * <p>Field messageString coded before 260815, commented in full on 261001.
     */
    private String messageString;

    /**
     * Bind a message type to its sound key.
     *
     * <p>Constructor MessageType coded before 260815, commented in full on 261001.
     *
     * @param messageString the sound-lookup key; {@code null} only for {@code MSG_MAX}
     */
    MessageType(String messageString) {
        this.messageString = messageString;
    }

    /**
     * Read back this message type's sound key. There is no C equivalent; C reads the
     * string straight from the {@code MSG()} table entry.
     *
     * <p>Function getMessageString coded before 260815, commented in full on 261001.
     *
     * @return the sound-lookup key: empty for {@code MSG_NONE}, {@code MSG_GENERIC} and
     * {@code MSG_BIRTH}, {@code null} for {@code MSG_MAX}
     */
    public String getMessageString() {
        return messageString;
    }
}
