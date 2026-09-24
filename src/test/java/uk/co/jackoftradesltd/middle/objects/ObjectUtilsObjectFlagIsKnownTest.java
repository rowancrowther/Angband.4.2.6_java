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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectUtils#objectFlagIsKnown}, the port of C's {@code object_flag_is_known}
 * ({@code obj-knowledge.c:781}).
 *
 * <p>C tries three routes to "yes" in a fixed order — the object is fully known, the player's own
 * rune knowledge ({@code p->obj_k}, the port's {@link Player#getItemKnowledge()}) has the flag, or
 * the object's own known-shadow ({@code obj->known}, the port's {@link ItemObject#getKnown()}) has
 * it — and falls through to "no" only when none of the three fires. Each nested class below pins
 * one of those routes; the "neither knows" case pins the fall-through.
 *
 * <p>Items are built with the long constructor and live mutable collections, following
 * {@link ObjectKnowledgeTest}'s pattern; a "not fully known" fixture gives the item its own
 * known-shadow but mismatches one combat figure between them (the to-hit bonus), which is enough
 * to fail {@link uk.co.jackoftradesltd.middle.player.PlayerKnowledge#nonCurseRunesKnown} and so
 * {@link ItemObject#isFullyKnown()} without needing every property to disagree.
 *
 * <p>Class ObjectUtilsObjectFlagIsKnownTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectUtilsObjectFlagIsKnownTest {

    /**
     * An item of the given type and kind, with every collection field live and empty and the given
     * known-shadow, mirroring {@code ObjectKnowledgeTest#item}.
     *
     * @param tValue the item type
     * @param kind   the kind this item is an instance of, or {@code null} for none
     * @param known  the item's known-shadow, or {@code null} for none
     * @return the item, with toAC/toDam/toHit all at zero
     */
    private static ItemObject item(TValue tValue, ObjectKind kind, ItemObject known) {
        return new ItemObject(kind, null, null, known, Loc.zero, tValue, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, "");
    }

    /**
     * A fully known item: a known-shadow whose every property matches the item's own, which
     * satisfies {@link uk.co.jackoftradesltd.middle.player.PlayerKnowledge#nonCurseRunesKnown} and
     * the empty-effect check in {@link ItemObject#isFullyKnown()} alike.
     */
    private static ItemObject fullyKnownItem() {
        ItemObject known = item(TValue.TV_SWORD, null, null);
        return item(TValue.TV_SWORD, null, known);
    }

    /**
     * An item with a known-shadow present, but not fully known — the shadow's to-hit stays at zero
     * while the item's own is moved off it, which is enough to fail {@code nonCurseRunesKnown}
     * without needing every other property to disagree too.
     */
    private static ItemObject notFullyKnownItem() {
        ItemObject known = item(TValue.TV_SWORD, null, null);
        ItemObject obj = item(TValue.TV_SWORD, null, known);
        obj.setToHit(3);
        return obj;
    }

    private static Player playerWithItemKnowledge() {
        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        return player;
    }

    /**
     * The first route: an object that is fully known reports every flag as known, whatever the
     * player's own knowledge and the object's known-shadow say — C's
     * {@code if (object_fully_known(obj)) return true;} runs before either other check.
     */
    @Nested
    @DisplayName("a fully known object")
    class FullyKnownObject {

        @Test
        @DisplayName("reports the flag as known even though neither the player nor the shadow has it")
        void reportsKnownRegardlessOfTheOtherTwoChecks() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = fullyKnownItem();

            assertTrue(ObjectUtils.objectFlagIsKnown(player, obj, ObjectFlag.OF_FREE_ACT));
        }
    }

    /**
     * The second route: the player's own rune knowledge, {@code p->obj_k} / {@link KnownObject},
     * has the flag — C's {@code if (of_has(p->obj_k->flags, flag)) return true;}. Fires even though
     * the object itself is not fully known.
     */
    @Nested
    @DisplayName("a not-fully-known object whose player already knows the flag")
    class PlayerKnowsTheFlag {

        @Test
        @DisplayName("reports the flag as known")
        void reportsKnown() {
            Player player = playerWithItemKnowledge();
            player.getItemKnowledge().learnFlag(ObjectFlag.OF_FREE_ACT);
            ItemObject obj = notFullyKnownItem();

            assertTrue(ObjectUtils.objectFlagIsKnown(player, obj, ObjectFlag.OF_FREE_ACT));
        }
    }

    /**
     * The third route: the object's own known-shadow, {@code obj->known} / {@link ItemObject#getKnown()},
     * already carries the flag — C's {@code if (of_has(obj->known->flags, flag)) return true;}. Fires
     * even though the player's own rune knowledge does not have it.
     */
    @Nested
    @DisplayName("a not-fully-known object whose known-shadow already carries the flag")
    class ShadowKnowsTheFlag {

        @Test
        @DisplayName("reports the flag as known")
        void reportsKnown() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = notFullyKnownItem();
            obj.getKnown().setFlag(ObjectFlag.OF_FREE_ACT);

            assertTrue(ObjectUtils.objectFlagIsKnown(player, obj, ObjectFlag.OF_FREE_ACT));
        }
    }

    /**
     * The fall-through: none of the three routes fires, so the flag is reported unknown — C's bare
     * {@code return false;} at the end of the function.
     */
    @Nested
    @DisplayName("a not-fully-known object that neither the player nor the shadow knows")
    class NeitherKnowsTheFlag {

        @Test
        @DisplayName("reports the flag as unknown")
        void reportsUnknown() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = notFullyKnownItem();

            assertFalse(ObjectUtils.objectFlagIsKnown(player, obj, ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("checks only the flag asked about, not any other the shadow carries")
        void checksOnlyTheNamedFlag() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = notFullyKnownItem();
            obj.getKnown().setFlag(ObjectFlag.OF_SEE_INVIS);

            assertFalse(ObjectUtils.objectFlagIsKnown(player, obj, ObjectFlag.OF_FREE_ACT));
        }
    }
}
