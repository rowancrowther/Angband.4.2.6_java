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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectDescription;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectStackEnum;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerCalcs;
import uk.co.jackoftradesltd.middle.player.PlayerKnowledge;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerNotice;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;

import java.util.Iterator;

/**
 * Static helpers for the player's gear list - the pack, quiver and equipment held in
 * {@link Player#getGear()} - ported from {@code obj-gear.c}.
 *
 * <p>This class holds the pieces of that file that put an item into the gear and count what is
 * already there: {@link #invenCarry} (C's {@code inven_carry}) and the private
 * {@link #objectPackTotal} (C's {@code object_pack_total}) with its result pair
 * {@link ItemObjectAndInt}.
 *
 * <p>Class ObjectGear coded before 260905, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public class ObjectGear {
    /**
     * Log sink for the invariant failures that C reports with {@code assert}. Each failure is logged
     * here and then thrown as a {@link RuntimeException}, since a Java {@code assert} is disabled
     * unless the JVM is started with {@code -ea}.
     *
     * <p>Field logger coded before 260905, commented in full on 261011.
     */
    private static final Logger logger = LogManager.getLogger(ObjectGear.class);

    /**
     * Adds {@code obj} to {@code player}'s gear, merging it into an existing mergeable pack/quiver
     * stack when {@code absorb} allows it, or inserting it as a new gear entry otherwise - the port
     * of C's {@code inven_carry} ({@code obj-gear.c}).
     *
     * <p>When {@code absorb} is set, the gear list is searched for the first non-equipped stack that
     * {@link ItemObject#mergeable} accepts, using quiver stacking rules for a quiver stack and pack
     * rules for anything else. A match absorbs {@code obj}'s known half (via a copy: C's inner
     * {@code object_absorb} deletes the object it absorbs, and {@code obj}'s own known object is
     * dropped straight afterwards, so the copy leaves the same result) and then {@code obj} itself,
     * keeps the two known counts aligned, and reassigns the local {@code obj} to alias the
     * combined gear entry - not a copy - so every read further down the method (the pack-total
     * search's identity check, and the closing quiver-sound check) sees the same object C's pointer
     * reassignment would have exposed.
     *
     * <p>Without a match, the item is inserted at the end of the gear list. A paranoia guard (C's
     * {@code assert(pack_slots_used(p) <= z_info->pack_size)}) refuses to insert if the pack is
     * already over capacity going in - by design a single insert is still allowed to push it one
     * over, which the caller's overflow handling then deals with. The item's cave placement (held
     * monster index and both grids) is cleared since it now lives in the gear rather than on a tile,
     * the pack notice and weight are updated, and hobbits/gnomes auto-identify an unknown mushroom or
     * wand/staff picked up this way.
     *
     * <p>Either path finishes by flagging a bonus/inventory recalculation and an inventory redraw,
     * then running it immediately via {@link PlayerCalcs#updateStuff}.
     *
     * <p>When {@code message} is set, the count shown is an aggregate across every matching stack -
     * unless the object tracks its own charges, is a rod, or has a live timeout, in which case only
     * its own count is shown - and the message notes "(1st &lt;label&gt;)" when that aggregate spans
     * more than the one stack being reported, or just "(&lt;label&gt;)" when it doesn't. Two more of
     * C's asserts become {@link RuntimeException}s here: that the pack-total search actually found a
     * stack whose reported total covers at least its own count, and that a total matching the
     * stack's own count really is {@code obj} and not some other stack.
     *
     * <p><b>Outstanding:</b> C builds the message description with the aggregate {@code total}
     * packed into the high 16 bits of the mode word ({@code ODESC_ALTNUM}). The port sets the flag
     * but cannot pass the count, because {@link ObjectUtils#objectDesc} is a stub with no count
     * parameter, so the "You have ..." text will show the stack's own count until that is ported.
     *
     * <p>Function invenCarry coded before 260905, commented in full on 261011, fixed on 260905 so
     * the paranoia guard checks for exceeding pack capacity rather than firing on the ordinary case.
     *
     * @param player  the player receiving the item
     * @param obj     the item being carried; on a successful combine this is reassigned to the gear
     *                entry it was merged into
     * @param absorb  whether to try merging into an existing compatible stack before inserting as new
     * @param message whether to print a "You have ..." pickup message
     */
    public static void invenCarry(Player player, ItemObject obj, boolean absorb, boolean message) {
        boolean combining = false;

        // Check for combining
        if (absorb) {
            ItemObject combineObj = null;

            Iterator<ItemObject> it = player.getGear().getIterator();
            while (it.hasNext()) {
                ItemObject gearObj = it.next();
                if (combineObj != null)
                    break;

                ObjectStackEnum stackMode = gearObj.objectIsInQuiver(player) ? ObjectStackEnum.OSTACK_QUIVER
                        : ObjectStackEnum.OSTACK_PACK;

                Flag<ObjectStackEnum> stackFlags = new Flag<>(ObjectStackEnum.class);
                stackFlags.set(stackMode);

                if (!player.getPlayerBody().itemIsEquipped(gearObj) && gearObj.mergeable(obj, stackFlags)) {
                    combineObj = gearObj;
                }
            }

            if (combineObj != null) {
                // increase the weight
                player.getPlayerUpkeep().setTotalWeight(player.getPlayerUpkeep().getTotalWeight()
                        + obj.getNumber() * obj.objectWeightOne());

                // combine the items and their known values
                combineObj.getKnown().objectAbsorb(obj.getKnown().copy(false));
                obj.setKnown(null);
                combineObj.objectAbsorb(obj);

                // Ensure members are aligned
                combineObj.getKnown().setNumber(combineObj.getNumber());

                obj = combineObj;
                combining = true;
            }
        }

        // We didn't manage the fine an object to combine with
        if (!combining) {
            // Paranoia
            if (ObjectUtils.packSlotsUsed(player) > GameConstants.getCarryCapPackSize()) {
                String msg = "packs slots used greater than pack size";
                logger.error(msg);
                throw new RuntimeException(msg);
            }

            ObjectUtils.gearInsertEnd(player, obj);
            ObjectIgnore.applyAutoinscription(player, obj);

            // remove the cave object details
            obj.setHeldMIndex(0);
            obj.setGrid(Loc.zero);
            obj.getKnown().setGrid(Loc.zero);

            // Update the inventory
            player.getPlayerUpkeep().setTotalWeight(player.getPlayerUpkeep().getTotalWeight()
                    + obj.getNumber() * obj.objectWeightOne());
            player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_COMBINE);

            // Hobbits ID mushrooms on pickup, gnomes ID wands and staves on pickup
            if (!obj.objectFlavourIsAware()) {
                if (player.hasPlayerFlag(PlayerFlag.PF_KNOW_MUSHROOM) && obj.gettValue().isMushroom()) {
                    PlayerKnowledge.flavourAware(player, obj);
                    Message.message("Mushrooms for breakfast!");
                } else if (player.hasPlayerFlag(PlayerFlag.PF_KNOW_ZAPPER) && obj.gettValue().isZapper()) {
                    PlayerKnowledge.flavourAware(player, obj);
                }
            }
        }

        player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_BONUS);
        player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_INVEN);
        player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_INVEN);
        PlayerCalcs.updateStuff(player);

        if (message) {
            String oName;
            int total;
            ItemObject first;

            // Show an aggregate total if the description doesn't have
            // a charge/rechargin notice that's specific to the stack
            if (obj.gettValue().canHaveCharges() || obj.gettValue().isRod()
                    || obj.getTimeout() > 0) {
                total = obj.getNumber();
                first = obj;
            } else {
                ItemObjectAndInt result = ObjectGear.objectPackTotal(player, obj, false);
                first = result.obj;
                total = result.number();
            }

            if (first == null || total < first.getNumber()) {
                String msg = "Invalid results from ObjectGear.objectPackTotal";
                logger.error(msg);
                throw new RuntimeException(msg);
            }
            Flag<ObjectDescription> descFlags = new Flag<>(ObjectDescription.class,
                    ObjectDescription.ODESC_PREFIX, ObjectDescription.ODESC_EXTRA,
                    ObjectDescription.ODESC_COMBAT, ObjectDescription.ODESC_ALTNUM);
            oName = ObjectUtils.objectDesc(obj, descFlags, player);
            char label = ObjectUtils.gearToLabel(player, first);
            if (total > first.getNumber()) {
                Message.message("You have " + oName + " (1st " + label + ").");
            } else {
                if (first != obj) {
                    String msg = "Incorrect object found.";
                    logger.error(msg);
                    throw new RuntimeException(msg);
                }
                Message.message("You have " + oName + " (" + label + ").");
            }
        }

        if (obj.objectIsInQuiver(player))
            Message.sound(MessageType.MSG_QUIVER, player);
    }

    /**
     * Counts every gear stack like {@code obj} and identifies the one that would be shown first in
     * the inventory/quiver listing - the port of C's {@code object_pack_total} ({@code obj-gear.c}).
     *
     * <p>"Like" is judged three ways: a cursor that <em>is</em> {@code obj} itself only counts if
     * {@code obj} is not equipped ({@link ItemObject#similar} and {@link ItemObject#objectStackable}
     * both exclude comparing an object to itself, so that case has to be special-cased here exactly
     * as C special-cases it); otherwise the cursor is compared to {@code obj} with
     * {@link ItemObject#similar} (inscriptions ignored) or {@link ItemObject#objectStackable}
     * (inscriptions considered), chosen by {@code ignoreInscrip}.
     *
     * <p>The label tie-break mirrors what {@link ObjectUtils#gearToLabel} hands out: a quiver digit
     * ({@code '0'}-{@code '9'}) always outranks a pack letter ({@code 'a'}-{@code 'z'}), and within
     * the same kind the lower character wins. It is re-run on every matching cursor, not just the
     * first one found, because the gear list's iteration order need not match the letter/digit order
     * the game assigns - the earliest-labelled match can turn up anywhere in the list.
     *
     * <p>C returns the total and writes the winning stack through {@code struct object **first},
     * resetting {@code *first} to {@code NULL} before the search starts regardless of what the
     * caller passed in. The port cannot take an address, so both values travel out together in an
     * {@link ItemObjectAndInt}; the port has no {@code first} parameter, so the search starts from a
     * local {@code null} and nothing the caller holds can leak in. C only tracks the first stack when
     * the caller passes a non-{@code NULL} {@code first}; the port always tracks it, which cannot
     * change the total. C's {@code uint16_t} return would wrap past 65535; the port's {@code int}
     * does not.
     *
     * <p>Function objectPackTotal coded before 260905, commented in full on 261011, fixed on 260905
     * so the first match starts from {@code null} instead of trusting a caller's placeholder and the
     * label tie-break re-runs for every match instead of stopping after the first; the placeholder
     * parameter was removed on 261011.
     *
     * @param player        the player whose gear is searched
     * @param obj           the template object other gear stacks are compared against
     * @param ignoreInscrip if {@code true}, stacks are compared ignoring inscriptions; if
     *                      {@code false}, inscriptions are considered too
     * @return the matching stack count and the earliest-labelled matching stack, as an
     * {@link ItemObjectAndInt}
     */
    private static ItemObjectAndInt objectPackTotal(Player player, ItemObject obj, boolean ignoreInscrip) {
        int total = 0;
        char firstLabel = '\0';

        ItemObject first = null;

        Iterator<ItemObject> it = player.getGear().getIterator();
        while (it.hasNext()) {
            ItemObject cursor = it.next();
            boolean like;

            if (cursor == obj) {
                // objectSimilar excludes cursor == obj, so if
                // obj is not equipped, account for it here
                like = !player.getPlayerBody().itemIsEquipped(obj);
            } else if (ignoreInscrip) {
                Flag<ObjectStackEnum> stackFlags = new Flag<>(ObjectStackEnum.class, ObjectStackEnum.OSTACK_PACK);
                like = obj.similar(cursor, stackFlags);
            } else {
                Flag<ObjectStackEnum> stackFlags = new Flag<>(ObjectStackEnum.class, ObjectStackEnum.OSTACK_PACK);
                like = obj.objectStackable(cursor, stackFlags);
            }

            if (like) {
                total += cursor.getNumber();
                char testLabel = ObjectUtils.gearToLabel(player, cursor);

                if (first == null) {
                    first = cursor;
                    firstLabel = testLabel;
                } else {
                    if (testLabel >= 'a' && testLabel <= 'z') {
                        if ((firstLabel == '\0')
                                || (firstLabel >= 'a' && firstLabel <= 'z' && firstLabel > testLabel)) {
                            first = cursor;
                            firstLabel = testLabel;
                        }
                    } else if (testLabel >= '0' && testLabel <= '9') {
                        if (firstLabel == '\0'
                                || (firstLabel >= 'a' && firstLabel <= 'z')
                                || (firstLabel >= '0' && firstLabel <= '9' && firstLabel > testLabel)) {
                            first = cursor;
                            firstLabel = testLabel;
                        }
                    }
                }
            }
        }

        return new ItemObjectAndInt(first, total);
    }

    /**
     * The pair {@link #objectPackTotal} hands back in place of C's return value plus its
     * {@code struct object **first} out-parameter ({@code obj-gear.c}, function
     * {@code object_pack_total}).
     *
     * <p>C returns the running total directly and writes the winning stack through {@code first};
     * the port cannot take an address, so both values travel out together here instead -
     * {@link #obj} is what C would have left in {@code *first} and {@link #number} is what C
     * returns.
     *
     * <p>Record ItemObjectAndInt coded before 260905, commented in full on 261011.
     *
     * @param obj    the earliest-labelled matching stack, or {@code null} if none matched
     * @param number the total item count across every matching stack
     */
    public record ItemObjectAndInt(ItemObject obj, int number) {
    }
}
