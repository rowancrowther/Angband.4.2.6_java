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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the two {@link MonsterLore} constructors.
 *
 * <p>C allocates {@code l_list} with {@code mem_zalloc} at the end of {@code finish_parse_monster}
 * ({@code mon-init.c}), so a new lore record has every count at zero, every flag set empty, every
 * list empty and every "known" flag false. {@code struct monster_lore} ({@code mon-lore.h}) has no
 * base field, so the Java-only {@code monsterBase} must start {@code null}.
 *
 * <p>Class MonsterLoreBlankRecordTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
class MonsterLoreBlankRecordTest {

    @Test
    @DisplayName("a blank record has every count at zero")
    void countsStartAtZero() {
        MonsterLore lore = new MonsterLore();

        assertEquals(0, lore.getSightings());
        assertEquals(0, lore.getDeaths());
        assertEquals(0, lore.getPkills());
        assertEquals(0, lore.getThefts());
        assertEquals(0, lore.getTkills());
        assertEquals(0, lore.getWake());
        assertEquals(0, lore.getIgnore());
        assertEquals(0, lore.getDropGold());
        assertEquals(0, lore.getDropItem());
        assertEquals(0, lore.getCaseInnate());
        assertEquals(0, lore.getCastSpell());
    }

    @Test
    @DisplayName("a blank record knows nothing")
    void knownFlagsStartFalse() {
        MonsterLore lore = new MonsterLore();

        assertFalse(lore.isAllKnown());
        assertFalse(lore.isArmourKnown());
        assertFalse(lore.isDropKnown());
        assertFalse(lore.isSleepKnown());
        assertFalse(lore.isSpellFreqKnown());
        assertFalse(lore.isInnateFreqKnown());
    }

    @Test
    @DisplayName("a blank record has empty flag sets and empty lists, none of them null")
    void collectionsStartEmpty() {
        MonsterLore lore = new MonsterLore();

        assertTrue(lore.getFlags().isEmpty());
        assertTrue(lore.getSpellFlags().isEmpty());
        assertTrue(lore.getBlows().isEmpty());
        assertTrue(lore.getDrops().isEmpty());
        assertTrue(lore.getFriends().isEmpty());
        assertTrue(lore.getFriendsBase().isEmpty());
        assertTrue(lore.getMimicKinds().isEmpty());
    }

    @Test
    @DisplayName("a blank record has no base, which struct monster_lore does not have either")
    void baseStartsNull() {
        assertNull(new MonsterLore().getMonsterBase());
    }

    @Test
    @DisplayName("two blank records share no list or flag set")
    void blankRecordsAreIndependent() {
        MonsterLore first = new MonsterLore();
        MonsterLore second = new MonsterLore();

        first.getFlags().on(MonsterRaceFlag.values()[0]);
        first.initblows(2);

        assertTrue(second.getFlags().isEmpty());
        assertTrue(second.getBlows().isEmpty());
    }

    @Test
    @DisplayName("the full constructor stores each argument in its own field, lists and sets by reference")
    void fullConstructorStoresEveryField() {
        List<MonsterBlow> blows = new ArrayList<>();
        List<MonsterDrop> drops = new ArrayList<>();
        List<MonsterFriends> friends = new ArrayList<>();
        List<MonsterFriendsBase> friendsBase = new ArrayList<>();
        List<MonsterMimic> mimics = new ArrayList<>();
        Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
        Flag<MonsterSpell> spellFlags = new Flag<>(MonsterSpell.class);

        MonsterLore lore = new MonsterLore(true, true, blows,
                1, 2, 3, 4, 5, true,
                drops, flags, friends,
                friendsBase, 6, true,
                mimics, null, 7, 8,
                true, spellFlags, true, 9,
                10, 11);

        assertTrue(lore.isAllKnown());
        assertTrue(lore.isArmourKnown());
        assertTrue(lore.isDropKnown());
        assertTrue(lore.isSleepKnown());
        assertTrue(lore.isSpellFreqKnown());
        assertTrue(lore.isInnateFreqKnown());
        assertEquals(1, lore.getCaseInnate());
        assertEquals(2, lore.getCastSpell());
        assertEquals(3, lore.getDeaths());
        assertEquals(4, lore.getDropGold());
        assertEquals(5, lore.getDropItem());
        assertEquals(6, lore.getIgnore());
        assertEquals(7, lore.getPkills());
        assertEquals(8, lore.getSightings());
        assertEquals(9, lore.getThefts());
        assertEquals(10, lore.getTkills());
        assertEquals(11, lore.getWake());
        assertSame(blows, lore.getBlows());
        assertSame(drops, lore.getDrops());
        assertSame(flags, lore.getFlags());
        assertSame(friends, lore.getFriends());
        assertSame(friendsBase, lore.getFriendsBase());
        assertSame(mimics, lore.getMimicKinds());
        assertSame(spellFlags, lore.getSpellFlags());
        assertNull(lore.getMonsterBase());
        assertNotNull(lore.getBlows());
    }

    @Test
    @DisplayName("setAllKnown only sets the flag and does not mark any blow known")
    void setAllKnownDoesNotRunLoreUpdate() {
        MonsterLore lore = new MonsterLore();
        lore.initblows(2);

        lore.setAllKnown(true);

        assertTrue(lore.isAllKnown());
        for (MonsterBlow blow : lore.getBlows())
            assertFalse(blow.isKnown());
    }
}
