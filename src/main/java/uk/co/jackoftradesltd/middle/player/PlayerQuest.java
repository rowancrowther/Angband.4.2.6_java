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

package uk.co.jackoftradesltd.middle.player;

import uk.co.jackoftradesltd.middle.game.globals.registry.DungeonRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * The quest machinery - the port of C's {@code player-quest.c}, minus its parser.
 *
 * <p>A quest in 4.2.6 is a small thing: a named unique monster pinned to a dungeon level, loaded
 * from {@code quest.txt}. The standard set is shared and immutable, and lives in
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry}; each character is given
 * their own copy of it at birth, reached through {@link Player#getQuests}, and it is that copy which
 * records what they have done. Killing the quest monster zeroes the level on the character's entry,
 * so a completed quest stops matching and the level stops being a quest level for them.
 *
 * <p>C's file also holds the {@code quest.txt} parser and the level feeling and stair-building that
 * follow a quest kill. The port keeps only the questions asked of the quest list; the parsing is
 * already done by the time anything here runs.
 *
 * <p>The methods are static and take the player, as C's take {@code struct player *p}: a quest
 * history belongs to a character, and there is no state here to hold. The class is a namespace.
 *
 * <p>Class PlayerQuest commented in full on 260901.
 *
 * @author Rowan Crowther
 */
public class PlayerQuest {
    /**
     * Tests whether a given dungeon level still holds one of this character's quests - the port of
     * C's {@code is_quest} ({@code player-quest.c:141}).
     *
     * <p><b>It asks about a level, not about a monster.</b> Nothing here looks at whether the quest
     * monster is alive, where it is, or whether the character has ever been down there. The question
     * is only whether some entry in the character's quest list is pinned to this depth, which is what
     * the callers need: a quest level cannot be left by a down staircase, so the level generator, the
     * stair placement and the deep-descent effects all have to know a depth is spoken for before
     * anything is built or anyone is moved.
     *
     * <p><b>Outstanding, not merely listed.</b> The list is never shortened - a completed quest keeps
     * its entry - but finishing one sets that entry's level to zero, so it stops matching any real
     * depth from then on. The zero it is set to is the same zero the town has, which is why the
     * town-level guard is first: without it, every completed quest would make the town a quest level
     * and the player could never leave it downwards.
     *
     * <p><b>The loop is over the character's own list</b>, the copy behind {@link Player#getQuests},
     * not the shared set in
     * {@link uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry#getQuests}. Two characters
     * in the same game answer differently for the same depth once one of them has finished a quest.
     * C indexes a fixed array of {@code z_info->quest_max} entries and relies on an unused slot
     * having a zeroed level; the port walks a list, so an absent quest is an absent element and the
     * zero-level slots never arise. The two agree because the town guard already rejects the only
     * depth a zeroed entry could match.
     *
     * <p>Function isQuest commented in full on 260901.
     *
     * @param player the character whose quest history is being asked about
     * @param level  the dungeon level to test
     * @return {@code true} if a quest target lives on that level
     */
    public static boolean isQuest(Player player, int level) {
        // No quests on town level
        if (level == 0) return false;

        for (Quest quest : player.getQuests()) {
            if (quest.getLevel() == level) return true;
        }

        return false;
    }

    /**
     * Rebuilds this character's quest history from the shared standard set - the port of C's
     * {@code player_quests_reset} ({@code player-quest.c:158}).
     *
     * <p>C frees any existing {@code p->quests} first, {@code mem_zalloc}s a fresh array sized to
     * {@code z_info->quest_max}, and copies each of the shared {@code quests[]} entries into it field
     * by field. The port has no array to free - the old list, if any, is simply dropped in favour of
     * the new one when {@link Player#setQuests} runs - and builds the replacement by asking each
     * shared {@link Quest} for {@link Quest#copy()}, which does the per-field work C's loop body does.
     * The result is the same either way: a list the same length as
     * {@link uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry#getQuests}, holding
     * entries that are independent of the shared set and of each other - except for
     * {@link Quest#getRace}, which {@link Quest#copy()} deliberately leaves shared. See that method
     * for why.
     *
     * <p>Called once at birth ({@code player_birth.c:413}) and once on loading a save
     * ({@code load.c:636}); the port's callers are expected to follow the same two occasions.
     *
     * <p>Function playerQuestsReset coded on 260903, commented in full on 260903.
     *
     * @param player the character whose quest history is (re)built from the standard set
     */
    public static void playerQuestsReset(Player player) {
        ArrayList<Quest> quests = new ArrayList<>();
        for (Quest quest : WorldRegistry.getQuests()) {
            quests.add(quest.copy());
        }
        player.setQuests(quests);
    }
}
