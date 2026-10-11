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

package uk.co.jackoftradesltd.middle.monsters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;

import java.util.ArrayList;
import java.util.List;

/**
 * The player's accumulated knowledge ("lore") about a monster race — what has
 * been observed through play and is therefore shown in the monster recall: kill
 * and sighting counts, observed drops/spells/blows, and the subset of flags the
 * player has learned. This is the Java port of the C original's
 * {@code struct monster_lore} ({@code mon-lore.h}).
 *
 * <p>Differences from the C struct worth knowing about:
 * <ul>
 *   <li>C's {@code ridx} is not carried. C keeps every record in the global {@code l_list}, found
 *       by race index; here the record is a field of its {@link MonsterRace}.</li>
 *   <li>C's {@code blow_known} array is not carried here. The flag lives on each
 *       {@link MonsterBlow} instead (see {@link MonsterBlow#isKnown()}).</li>
 *   <li>C sizes {@code blows} to {@code z_info->mon_blows_max} for every race. Here
 *       {@link #initblows(int)} gives the list one blank blow per blow its own race has, because the
 *       race's blow list is not padded out either (see {@link MonsterRace}).</li>
 *   <li>The {@link #monsterBase} field has no counterpart in {@code struct monster_lore}. C instead
 *       merges the race's base flags into the lore flags when the lore file is loaded
 *       ({@code finish_parse_lore}, {@code mon-init.c}).</li>
 *   <li>C declares the five long counters as {@code uint16_t} and the shorter ones as
 *       {@code uint8_t}. Every count here is an {@code int} and no setter range-checks it.</li>
 *   <li>C's {@code sights} and {@code cast_innate} are called {@link #sightings} and
 *       {@link #caseInnate} here.</li>
 * </ul>
 *
 * <p>Class MonsterLore coded before 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public class MonsterLore {
    /**
     * Log4j logger for this class, used by {@link #initblows(int)} to report a blow list whose
     * length disagrees with the one it is asked for.
     *
     * <p>Field logger coded before 261011, commented in full on 261011.
     */
    private static final Logger logger = LogManager.getLogger(MonsterLore.class);
    /**
     * Number of times the monster has been seen, C's {@code lore->sights}.
     *
     * <p>Field sightings coded before 261011, commented in full on 261011.
     */
    private int sightings;
    /**
     * Number of times this monster has killed the player, C's {@code lore->deaths}.
     *
     * <p>Field deaths coded before 261011, commented in full on 261011.
     */
    private int deaths;
    /**
     * Number of player kills of this monster in this life, C's {@code lore->pkills}. See
     * {@link #setPSkills(int)}.
     *
     * <p>Field pkills coded before 261011, commented in full on 261011.
     */
    private int pkills;
    /**
     * Number of times the monster has stolen from the player in this life, C's
     * {@code lore->thefts}. See {@link #setThefts(int)}.
     *
     * <p>Field thefts coded before 261011, commented in full on 261011.
     */
    private int thefts;
    /**
     * Total kills of this monster across all lives, C's {@code lore->tkills}. C's {@code lore_update}
     * ({@code mon-lore.c}) treats any value above zero as enough to reveal the race's armour and
     * drops.
     *
     * <p>Field tkills coded before 261011, commented in full on 261011.
     */
    private int tkills;
    /**
     * Number of times the monster has been woken up, C's {@code lore->wake}. C's {@code lore_update}
     * learns how deeply the race sleeps once the square of this exceeds the race's sleep value.
     *
     * <p>Field wake coded before 261011, commented in full on 261011.
     */
    private int wake;
    /**
     * Number of times the monster has ignored the player, C's {@code lore->ignore}. C's
     * {@code lore_update} learns how deeply the race sleeps when this reaches 255.
     *
     * <p>Field ignore coded before 261011, commented in full on 261011.
     */
    private int ignore;
    /**
     * The most gold drops seen at once, C's {@code lore->drop_gold}.
     *
     * <p>Field dropGold coded before 261011, commented in full on 261011.
     */
    private int dropGold;
    /**
     * The most item drops seen at once, C's {@code lore->drop_item}.
     *
     * <p>Field dropItem coded before 261011, commented in full on 261011.
     */
    private int dropItem;
    /**
     * The most innate spells seen cast, C's {@code lore->cast_innate}. C's {@code lore_update} learns
     * the innate-attack frequency once this is above 50. The name is a slip for "cast" and is kept
     * as written.
     *
     * <p>Field caseInnate coded before 261011, commented in full on 261011.
     */
    private int caseInnate;
    /**
     * The most other spells seen cast, C's {@code lore->cast_spell}. C's {@code lore_update} learns
     * the spell frequency once this is above 50.
     *
     * <p>Field castSpell coded before 261011, commented in full on 261011.
     */
    private int castSpell;

    /**
     * The base type the lore relates to. It has no counterpart in C's {@code struct monster_lore}
     * (see the class block). The no-argument constructor leaves it {@code null}.
     *
     * <p>Field monsterBase coded before 261011, commented in full on 261011.
     */
    private MonsterBase monsterBase;
    /**
     * The blows the player has observed, C's {@code lore->blows}. Position {@code i} lines up with
     * position {@code i} of the race's own blow list. {@link #initblows(int)} fills it with blank
     * blows.
     *
     * <p>Field blows coded before 261011, commented in full on 261011.
     */
    private List<MonsterBlow> blows;

    /**
     * The race flags the player has learned, C's {@code lore->flags}. A set bit means the flag, or
     * its absence, is known to the player.
     *
     * <p>Field flags coded before 261011, commented in full on 261011.
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * The spell flags the player has learned, C's {@code lore->spell_flags}.
     *
     * <p>Field spellFlags coded before 261011, commented in full on 261011.
     */
    private Flag<MonsterSpell> spellFlags;

    /**
     * Drops the player has observed, C's {@code lore->drops}.
     *
     * <p>Field drops coded before 261011, commented in full on 261011.
     */
    private List<MonsterDrop> drops;
    /**
     * Companion races the player has observed, C's {@code lore->friends}.
     *
     * <p>Field friends coded before 261011, commented in full on 261011.
     */
    private List<MonsterFriends> friends;
    /**
     * Companion base types the player has observed, C's {@code lore->friends_base}.
     *
     * <p>Field friendsBase coded before 261011, commented in full on 261011.
     */
    private List<MonsterFriendsBase> friendsBase;
    /**
     * Mimic kinds the player has observed, C's {@code lore->mimic_kinds}.
     *
     * <p>Field mimicKinds coded before 261011, commented in full on 261011.
     */
    private List<MonsterMimic> mimicKinds;

    // Derived known fields. C's lore_update (mon-lore.c) is what sets these; nothing in this class does.
    /**
     * Whether everything about the race is known, C's {@code lore->all_known}. C sets it for
     * probing and cheating, and {@code lore_update} then marks every blow and flag as known.
     *
     * <p>Field allKnown coded before 261011, commented in full on 261011.
     */
    boolean allKnown;
    /**
     * Whether the race's armour class is known, C's {@code lore->armour_known}.
     *
     * <p>Field armourKnown coded before 261011, commented in full on 261011.
     */
    boolean armourKnown;
    /**
     * Whether the race's drops are known, C's {@code lore->drop_known}.
     *
     * <p>Field dropKnown coded before 261011, commented in full on 261011.
     */
    boolean dropKnown;
    /**
     * Whether how deeply the race sleeps is known, C's {@code lore->sleep_known}.
     *
     * <p>Field sleepKnown coded before 261011, commented in full on 261011.
     */
    boolean sleepKnown;
    /**
     * Whether the race's spell frequency is known, C's {@code lore->spell_freq_known}.
     *
     * <p>Field spellFreqKnown coded before 261011, commented in full on 261011.
     */
    boolean spellFreqKnown;
    /**
     * Whether the race's innate-attack frequency is known, C's {@code lore->innate_freq_known}.
     *
     * <p>Field innateFreqKnown coded before 261011, commented in full on 261011.
     */
    boolean innateFreqKnown;

    /**
     * Build a blank lore record: every count zero, every flag set empty, every list empty and no
     * base. This stands in for one zeroed element of the {@code l_list} that C allocates at the end
     * of {@code finish_parse_monster} ({@code mon-init.c}). The blow list is empty, so
     * {@link #initblows(int)} still has to size it, which {@code MonsterDataLoader.loadMonsters}
     * does for each race.
     *
     * <p>Constructor MonsterLore() coded before 261011, commented in full on 261011.
     */
    public MonsterLore() {
        flags = new Flag<>(MonsterRaceFlag.class);
        spellFlags = new Flag<>(MonsterSpell.class);
        drops = new ArrayList<>();
        friends = new ArrayList<>();
        friendsBase = new ArrayList<>();
        mimicKinds = new ArrayList<>();
        monsterBase = null;
        blows = new ArrayList<>();
    }

    /**
     * Build a lore record with every field given. Each argument is stored as given: the lists and
     * flag sets are kept by reference and not copied, and nothing is validated, so a {@code null}
     * list is stored as {@code null}. No Java file in {@code src/main} calls it: the {@code @after}
     * block of {@code LoreGrammar.g4} builds its record with a different, 19-argument list, and that
     * grammar is parked with the lore reader. A record with a {@code null} blow list is still
     * usable, because {@link #initblows(int)} replaces it with a new list.
     *
     * <p>Constructor MonsterLore(boolean, boolean, List, int, ...) coded before 261011, commented in
     * full on 261011.
     *
     * @param allKnown        whether everything is known
     * @param armourKnown     whether the race's armour is known
     * @param blows           the blows seen
     * @param caseInnate      the most innate spells seen cast
     * @param castSpell       the most other spells seen cast
     * @param deaths          times the monster has killed the player
     * @param dropGold        the most gold drops seen at once
     * @param dropItem        the most item drops seen at once
     * @param dropKnown       whether the drops are known
     * @param drops           the drops seen
     * @param flags           the race flags learned
     * @param friends         the companion races seen
     * @param friendsBase     the companion base types seen
     * @param ignore          times the monster has ignored the player
     * @param innateFreqKnown whether the innate-attack frequency is known
     * @param mimicKinds      the mimic kinds seen
     * @param monsterBase     the base type the lore relates to
     * @param pkills          player kills in this life
     * @param sightings       times the monster has been seen
     * @param sleepKnown      whether the sleep value is known
     * @param spellFlags      the spell flags learned
     * @param spellFreqKnown  whether the spell frequency is known
     * @param thefts          thefts in this life
     * @param tkills          kills across all lives
     * @param wake            times the monster has been woken
     */
    public MonsterLore(boolean allKnown, boolean armourKnown, List<MonsterBlow> blows,
                       int caseInnate, int castSpell, int deaths, int dropGold, int dropItem, boolean dropKnown, 
                       List<MonsterDrop> drops, Flag<MonsterRaceFlag> flags, List<MonsterFriends> friends, 
                       List<MonsterFriendsBase> friendsBase, int ignore, boolean innateFreqKnown,
                       List<MonsterMimic> mimicKinds, MonsterBase monsterBase, int pkills, int sightings,
                       boolean sleepKnown, Flag<MonsterSpell> spellFlags, boolean spellFreqKnown, int thefts,
                       int tkills, int wake) {
        this.allKnown = allKnown;
        this.armourKnown = armourKnown;
        this.blows = blows;
        this.caseInnate = caseInnate;
        this.castSpell = castSpell;
        this.deaths = deaths;
        this.dropGold = dropGold;
        this.dropItem = dropItem;
        this.dropKnown = dropKnown;
        this.drops = drops;
        this.flags = flags;
        this.friends = friends;
        this.friendsBase = friendsBase;
        this.ignore = ignore;
        this.innateFreqKnown = innateFreqKnown;
        this.mimicKinds = mimicKinds;
        this.monsterBase = monsterBase;
        this.pkills = pkills;
        this.sightings = sightings;
        this.sleepKnown = sleepKnown;
        this.spellFlags = spellFlags;
        this.spellFreqKnown = spellFreqKnown;
        this.thefts = thefts;
        this.tkills = tkills;
        this.wake = wake;
    }
    
    /**
     * Gives this record one blank {@link MonsterBlow} per blow its race has, the Java form of the
     * zeroed {@code lore->blows} array that C allocates for every race at the end of
     * {@code finish_parse_monster} ({@code mon-init.c}). C allocates {@code z_info->mon_blows_max}
     * entries for every race; the caller here passes the race's own blow count (see the class block).
     *
     * <p>The method only ever fills an empty list:
     * <ul>
     *   <li>If the list is {@code null} or empty, it is created if need be and given {@code monBlows}
     *       new, separate blank blows. A negative count adds nothing and leaves a {@code null} list
     *       {@code null}.</li>
     *   <li>If the list already has blows, it is left alone, which makes a second call with the same
     *       count harmless. A different count is logged as an error and changes nothing, so a record
     *       whose blows were set elsewhere is never truncated or extended.</li>
     * </ul>
     *
     * <p>Function initblows coded before 261011, commented in full on 261011.
     *
     * @param monBlows the number of blank blows to create, normally the race's blow count
     */
    public void initblows(int monBlows) {
        if (blows == null || blows.isEmpty()) {
            if (monBlows < 0) return;
            
            if (blows == null) blows = new ArrayList<>();

            for (int index = 0; index < monBlows; index++) {
                blows.add(new MonsterBlow());
            }
        } else {
            if (monBlows != blows.size()) {
                logger.error("Mismatch between size of existing blows in " +
                        "MonsterLore and size it is trying to be set to");
            }
        }
    } 

    /**
     * Sets the number of this monster the player has killed this life, C's {@code lore->pkills}. C
     * only ever writes this field directly (never derives it), and this setter does the same, with
     * no range checking even though C declares the field as an unsigned 16-bit count.
     *
     * <p>Function setPSkills coded on 260903, commented in full on 260903.
     *
     * @param pSkills the new player-kill count for this monster
     */
    public void setPSkills(int pSkills) {
        this.pkills = pSkills;
    }

    /**
     * Sets the number of times this monster has stolen from the player this life, C's
     * {@code lore->thefts}. C only ever writes this field directly, and this setter does the same,
     * with no range checking even though C declares the field as an unsigned 16-bit count.
     *
     * <p>Function setThefts coded on 260903, commented in full on 260903.
     *
     * @param thefts the new theft count for this monster
     */
    public void setThefts(int thefts) {
        this.thefts = thefts;
    }

    /**
     * Returns whether everything about the race is known, C's {@code lore->all_known}.
     *
     * <p>Function isAllKnown coded before 261011, commented in full on 261011.
     *
     * @return true if the record is fully known
     */
    public boolean isAllKnown() {
        return allKnown;
    }

    /**
     * Sets whether everything about the race is known. This is a plain assignment: C's
     * {@code cheat_monster_lore} ({@code mon-lore.c}) goes on to call {@code lore_update}, which
     * then marks every blow and flag as known, and this setter does not.
     *
     * <p>Function setAllKnown coded before 261011, commented in full on 261011.
     *
     * @param allKnown true to mark the record as fully known
     */
    public void setAllKnown(boolean allKnown) {
        this.allKnown = allKnown;
    }

    /**
     * Returns whether the race's armour is known, C's {@code lore->armour_known}.
     *
     * <p>Function isArmourKnown coded before 261011, commented in full on 261011.
     *
     * @return true if the armour is known
     */
    public boolean isArmourKnown() {
        return armourKnown;
    }

    /**
     * Returns the blows the player has observed, C's {@code lore->blows}. The list is the live one,
     * not a copy, and its positions line up with the race's own blow list.
     *
     * <p>Function getBlows coded before 261011, commented in full on 261011.
     *
     * @return the lore blows
     */
    public List<MonsterBlow> getBlows() {
        return blows;
    }

    /**
     * Returns the most innate spells seen cast, C's {@code lore->cast_innate}.
     *
     * <p>Function getCaseInnate coded before 261011, commented in full on 261011.
     *
     * @return the innate-cast count
     */
    public int getCaseInnate() {
        return caseInnate;
    }

    /**
     * Returns the most other spells seen cast, C's {@code lore->cast_spell}.
     *
     * <p>Function getCastSpell coded before 261011, commented in full on 261011.
     *
     * @return the spell-cast count
     */
    public int getCastSpell() {
        return castSpell;
    }

    /**
     * Returns how many times this monster has killed the player, C's {@code lore->deaths}.
     *
     * <p>Function getDeaths coded before 261011, commented in full on 261011.
     *
     * @return the death count
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Returns the most gold drops seen at once, C's {@code lore->drop_gold}.
     *
     * <p>Function getDropGold coded before 261011, commented in full on 261011.
     *
     * @return the gold-drop count
     */
    public int getDropGold() {
        return dropGold;
    }

    /**
     * Returns the most item drops seen at once, C's {@code lore->drop_item}.
     *
     * <p>Function getDropItem coded before 261011, commented in full on 261011.
     *
     * @return the item-drop count
     */
    public int getDropItem() {
        return dropItem;
    }

    /**
     * Returns whether the race's drops are known, C's {@code lore->drop_known}.
     *
     * <p>Function isDropKnown coded before 261011, commented in full on 261011.
     *
     * @return true if the drops are known
     */
    public boolean isDropKnown() {
        return dropKnown;
    }

    /**
     * Returns the drops the player has observed, C's {@code lore->drops}. The list is the live one.
     *
     * <p>Function getDrops coded before 261011, commented in full on 261011.
     *
     * @return the observed drops
     */
    public List<MonsterDrop> getDrops() {
        return drops;
    }

    /**
     * Returns the race flags the player has learned, C's {@code lore->flags}. The set is the live
     * one.
     *
     * <p>Function getFlags coded before 261011, commented in full on 261011.
     *
     * @return the learned race flags
     */
    public Flag<MonsterRaceFlag> getFlags() {
        return flags;
    }

    /**
     * Returns the companion races the player has observed, C's {@code lore->friends}. The list is
     * the live one.
     *
     * <p>Function getFriends coded before 261011, commented in full on 261011.
     *
     * @return the observed companion races
     */
    public List<MonsterFriends> getFriends() {
        return friends;
    }

    /**
     * Returns the companion base types the player has observed, C's {@code lore->friends_base}. The
     * list is the live one.
     *
     * <p>Function getFriendsBase coded before 261011, commented in full on 261011.
     *
     * @return the observed companion base types
     */
    public List<MonsterFriendsBase> getFriendsBase() {
        return friendsBase;
    }

    /**
     * Returns how many times this monster has ignored the player, C's {@code lore->ignore}.
     *
     * <p>Function getIgnore coded before 261011, commented in full on 261011.
     *
     * @return the ignore count
     */
    public int getIgnore() {
        return ignore;
    }

    /**
     * Returns whether the race's innate-attack frequency is known, C's
     * {@code lore->innate_freq_known}.
     *
     * <p>Function isInnateFreqKnown coded before 261011, commented in full on 261011.
     *
     * @return true if the innate frequency is known
     */
    public boolean isInnateFreqKnown() {
        return innateFreqKnown;
    }

    /**
     * Returns the mimic kinds the player has observed, C's {@code lore->mimic_kinds}. The list is
     * the live one.
     *
     * <p>Function getMimicKinds coded before 261011, commented in full on 261011.
     *
     * @return the observed mimic kinds
     */
    public List<MonsterMimic> getMimicKinds() {
        return mimicKinds;
    }

    /**
     * Returns the base type the lore relates to. C's {@code struct monster_lore} has no such field
     * (see the class block).
     *
     * <p>Function getMonsterBase coded before 261011, commented in full on 261011.
     *
     * @return the base type, or {@code null} for a record built by the no-argument constructor
     */
    public MonsterBase getMonsterBase() {
        return monsterBase;
    }

    /**
     * Returns the number of this monster the player has killed in this life, C's
     * {@code lore->pkills}.
     *
     * <p>Function getPkills coded before 261011, commented in full on 261011.
     *
     * @return the kill count for this life
     */
    public int getPkills() {
        return pkills;
    }

    /**
     * Returns how many times the monster has been seen, C's {@code lore->sights}.
     *
     * <p>Function getSightings coded before 261011, commented in full on 261011.
     *
     * @return the sighting count
     */
    public int getSightings() {
        return sightings;
    }

    /**
     * Returns whether how deeply the race sleeps is known, C's {@code lore->sleep_known}.
     *
     * <p>Function isSleepKnown coded before 261011, commented in full on 261011.
     *
     * @return true if the sleep value is known
     */
    public boolean isSleepKnown() {
        return sleepKnown;
    }

    /**
     * Returns the spell flags the player has learned, C's {@code lore->spell_flags}. The set is the
     * live one.
     *
     * <p>Function getSpellFlags coded before 261011, commented in full on 261011.
     *
     * @return the learned spell flags
     */
    public Flag<MonsterSpell> getSpellFlags() {
        return spellFlags;
    }

    /**
     * Returns whether the race's spell frequency is known, C's {@code lore->spell_freq_known}.
     *
     * <p>Function isSpellFreqKnown coded before 261011, commented in full on 261011.
     *
     * @return true if the spell frequency is known
     */
    public boolean isSpellFreqKnown() {
        return spellFreqKnown;
    }

    /**
     * Returns how many times the monster has stolen from the player in this life, C's
     * {@code lore->thefts}.
     *
     * <p>Function getThefts coded before 261011, commented in full on 261011.
     *
     * @return the theft count for this life
     */
    public int getThefts() {
        return thefts;
    }

    /**
     * Returns the total kills of this monster across all lives, C's {@code lore->tkills}.
     *
     * <p>Function getTkills coded before 261011, commented in full on 261011.
     *
     * @return the lifetime kill count
     */
    public int getTkills() {
        return tkills;
    }

    /**
     * Returns how many times the monster has been woken up, C's {@code lore->wake}.
     *
     * <p>Function getWake coded before 261011, commented in full on 261011.
     *
     * @return the wake count
     */
    public int getWake() {
        return wake;
    }
}