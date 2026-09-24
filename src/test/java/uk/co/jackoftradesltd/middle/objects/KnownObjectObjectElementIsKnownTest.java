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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
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
 * Tests {@link KnownObject#objectElementIsKnown(Player, ItemObject, ElementEnum)}, the item-shaped
 * counterpart to {@link ObjectUtilsObjectElementIsKnownTest} and the port of C's
 * {@code object_element_is_known} ({@code obj-knowledge.c:804}). Each nested class pins one of the
 * three routes to "yes" the function tries, in the order it tries them, plus the sentinel bound
 * ahead of all three and the fall-through after them.
 *
 * <p>Items are built with the long constructor and live mutable collections, following
 * {@link ObjectUtilsObjectFlagIsKnownTest}'s pattern; a "not fully known" fixture moves the item's
 * real to-hit off the (default zero) known to-hit, which is enough to fail
 * {@link ItemObject#isFullyKnown()} without needing the element halves to disagree with anything.
 *
 * <p>The method is called through {@link Player#getItemKnowledge()} rather than on a bare
 * {@link KnownObject}, matching every real call site — {@code this} inside the method is the same
 * object as {@code player.getItemKnowledge()}, which is what lets the second route read the
 * player's own knowledge straight off the instance's {@code elementResistInfo} field.
 *
 * <p>Class KnownObjectObjectElementIsKnownTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class KnownObjectObjectElementIsKnownTest {

    /**
     * An item of the given type and kind, with every collection field live and empty and the given
     * known-shadow, mirroring {@link ObjectUtilsObjectFlagIsKnownTest#item}.
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
     * satisfies both halves of {@link ItemObject#isFullyKnown()}.
     */
    private static ItemObject fullyKnownItem() {
        ItemObject known = item(TValue.TV_SWORD, null, null);
        return item(TValue.TV_SWORD, null, known);
    }

    /**
     * An item with a known-shadow present, but not fully known — the shadow's to-hit stays at zero
     * while the item's own is moved off it, which is enough to fail {@link ItemObject#isFullyKnown()}
     * without needing every other property to disagree too.
     */
    private static ItemObject notFullyKnownItem() {
        ItemObject known = item(TValue.TV_SWORD, null, null);
        ItemObject obj = item(TValue.TV_SWORD, null, known);
        obj.setToHit(3);
        return obj;
    }

    /**
     * An {@link ElementInfo} carrying the given resistance level and no flags, for writing onto an
     * item's known-shadow through {@link ItemObject#putElInfo}.
     */
    private static ElementInfo elementInfo(int resLevel) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(resLevel);
        return info;
    }

    private static Player playerWithItemKnowledge() {
        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        return player;
    }

    /**
     * The bound check: C's {@code if (element < 0 || element >= ELEM_MAX) return false;}, ported as
     * the two sentinel constants. Runs before every other check, so it answers false even for a
     * fully known item.
     */
    @Nested
    @DisplayName("a sentinel element")
    class SentinelElement {

        @Test
        @DisplayName("ELEM_NONE reports unknown even on a fully known item")
        void elemNoneReportsUnknown() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = fullyKnownItem();

            assertFalse(player.getItemKnowledge().objectElementIsKnown(player, obj, ElementEnum.ELEM_NONE));
        }

        @Test
        @DisplayName("ELEM_MAX reports unknown even on a fully known item")
        void elemMaxReportsUnknown() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = fullyKnownItem();

            assertFalse(player.getItemKnowledge().objectElementIsKnown(player, obj, ElementEnum.ELEM_MAX));
        }
    }

    /**
     * The first route past the bound check: an item that is fully known reports every element as
     * known, whatever the player's own knowledge and the item's known-shadow say — C's
     * {@code if (object_fully_known(obj)) return true;} runs before either other check.
     */
    @Nested
    @DisplayName("a fully known item")
    class FullyKnownItem {

        @Test
        @DisplayName("reports the element as known even though neither the player nor the shadow has it")
        void reportsKnownRegardlessOfTheOtherTwoChecks() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = fullyKnownItem();

            assertTrue(player.getItemKnowledge().objectElementIsKnown(player, obj, ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * The second route: the player's own rune knowledge, {@code p->obj_k} / {@link KnownObject},
     * already covers this element — C's {@code if (p->obj_k->el_info[element].res_level) return
     * true;}, read straight off the instance's own field since {@code this} is
     * {@code player.getItemKnowledge()}. Fires even though the item itself is not fully known.
     */
    @Nested
    @DisplayName("a not-fully-known item whose player already knows the element")
    class PlayerKnowsTheElement {

        @Test
        @DisplayName("reports the element as known")
        void reportsKnown() {
            Player player = playerWithItemKnowledge();
            player.getItemKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            ItemObject obj = notFullyKnownItem();

            assertTrue(player.getItemKnowledge().objectElementIsKnown(player, obj, ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * The third route: the item's own known-shadow, {@link ItemObject#getKnown()}, already carries a
     * non-zero resistance level for this element — C's {@code if (obj->known->el_info[element]
     * .res_level) return true;}. Fires even though the player's own rune knowledge does not cover it.
     */
    @Nested
    @DisplayName("a not-fully-known item whose known-shadow already carries the element")
    class ShadowKnowsTheElement {

        @Test
        @DisplayName("reports the element as known")
        void reportsKnown() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = notFullyKnownItem();
            obj.getKnown().putElInfo(ElementEnum.ELEM_FIRE, elementInfo(1));

            assertTrue(player.getItemKnowledge().objectElementIsKnown(player, obj, ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * The fall-through: none of the three routes fires, so the element is reported unknown — C's
     * bare {@code return false;} at the end of the function.
     */
    @Nested
    @DisplayName("a not-fully-known item that neither the player nor the shadow knows")
    class NeitherKnowsTheElement {

        @Test
        @DisplayName("reports the element as unknown")
        void reportsUnknown() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = notFullyKnownItem();

            assertFalse(player.getItemKnowledge().objectElementIsKnown(player, obj, ElementEnum.ELEM_FIRE));
        }

        @Test
        @DisplayName("checks only the element asked about, not any other the shadow carries")
        void checksOnlyTheNamedElement() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = notFullyKnownItem();
            obj.getKnown().putElInfo(ElementEnum.ELEM_COLD, elementInfo(1));

            assertFalse(player.getItemKnowledge().objectElementIsKnown(player, obj, ElementEnum.ELEM_FIRE));
        }

        @Test
        @DisplayName("a known-shadow entry recorded at resistance level zero reports unknown")
        void aKnownButZeroShadowEntryReportsUnknown() {
            Player player = playerWithItemKnowledge();
            ItemObject obj = notFullyKnownItem();
            obj.getKnown().putElInfo(ElementEnum.ELEM_FIRE, elementInfo(0));

            assertFalse(player.getItemKnowledge().objectElementIsKnown(player, obj, ElementEnum.ELEM_FIRE));
        }
    }
}
