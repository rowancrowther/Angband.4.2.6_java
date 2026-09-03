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
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectDescription;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectStackEnum;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerCalcs;
import uk.co.jackoftradesltd.middle.player.PlayerKnowledge;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;

/**
 * Free-standing helper routines for the object/inventory subsystem — the port's landing spot for
 * the gear-management corners of C's {@code obj-gear.c}, {@code obj-desc.c}, {@code obj-curse.c}
 * and {@code obj-knowledge.c}.
 *
 * <p>Static methods that act on objects and the player's pack without belonging to any one object's
 * data model. C reaches the player through a global and so needs no argument for it; the port has
 * no such global, which is why every method here takes the {@link Player} it works on as its first
 * parameter rather than reading one off {@code this}.
 *
 * <p><b>Where these came from.</b> The gear routines — {@link #combinePack} and the capacity
 * arithmetic beneath it, the four slot lookups, {@link #gearInsertEnd}, {@link #gearToLabel} and
 * {@link #isCarried} — were first written as instance methods on {@link Player}, following C's
 * habit of hanging anything that touches {@code p->gear} off the player. They have since moved
 * here, to sit with the objects they manipulate rather than with the player who happens to own
 * them, and became static in the process. Their neighbours moved at the same time and for the same
 * reason: the inventory and equipment rebuild to {@link PlayerCalcs}, the learning routines to
 * {@link PlayerKnowledge}. Anything that still reads as though it lives on the player — the
 * {@code Player.slotByName()} named in {@link #slotByName}'s exception message, say — is a leftover
 * of that move rather than a description of where the code is now.
 *
 * <p><b>Status:</b> the gear, slot and capacity routines are ported. {@link #packOverflow},
 * {@link #equipLearnAfterTime}, {@link #objectDesc} and {@link #doCurseEffect} are still the stubs
 * landed to unblock the game loop, and each says so for itself.
 *
 * @author Rowan Crowther
 */
public class ObjectUtils {
    private final static Logger logger = LogManager.getLogger(ObjectUtils.class);
    
    /**
     * Handle "pack overflow" — the port of C's {@code pack_overflow} ({@code obj-gear.c}). When the
     * pack holds more than it can carry, the excess is dropped (or the offending item is), so the
     * game never proceeds with an over-full inventory. The player-processing pass calls it defensively
     * at the top of each command, in case a menu action left the pack corrupted.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the gear subsystem is ported.
     *
     * @param object the specific item to overflow, or {@code null} to overflow the last pack slot as
     *               C does with {@code pack_overflow(NULL)}
     */
    public static void packOverflow(ItemObject object) {
        // Stub class TODO: implement
    }

    /**
     * Learns the timed/after-time properties of the player's worn equipment — the port of C's
     * {@code equip_learn_after_time} ({@code obj-knowledge.c}), run periodically so flags that are
     * only revealed through prolonged wear become known.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the knowledge subsystem is ported.
     *
     * @param player the player whose equipment is checked
     */
    public static void equipLearnAfterTime(Player player) {
        // Stub class TODO: implement
    }

    /**
     * Builds the display name of an object as this player would see it — the port of C's
     * {@code object_desc} ({@code obj-desc.c}), which writes into a caller-supplied buffer where the
     * port returns a string.
     *
     * <p>The {@code description} flags select how much of the name to include. C treats them as a
     * bit mask ({@code obj-desc.h:26-42}), so an empty set is C's {@code ODESC_BASE == 0x00} — the
     * bare name with no combat bonuses, charges or inscription — and callers OR in extras such as
     * {@code ODESC_COMBAT} or {@code ODESC_PREFIX} from there.
     *
     * <p>The player is a parameter rather than a global because the name depends on what they know:
     * an unidentified potion shows its flavour, an unlearned ego stays anonymous.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the object-naming and knowledge runtimes;
     * returns an empty string, so callers currently produce messages with a blank where the item
     * name belongs. Note that C's {@code ODESC_ALTNUM} passes a count through the high 16 bits of
     * the mode word, which a flag set cannot carry — it will need a separate parameter when this is
     * ported.
     *
     * @param item        the object to name
     * @param description the {@link ObjectDescription} flags selecting how much detail to include
     * @param player      the player whose knowledge governs what the name reveals
     * @return the object's display name
     */
    public static String objectDesc(ItemObject item, Flag<ObjectDescription> description, Player player) {
        // Stub class TODO: implement
        return "";
    }

    /**
     * Fire a curse's effect against the player, as the source item's curse timeout expires. The
     * port of C's {@code do_curse_effect} ({@code obj-curse.c:353}); the returned flag drives
     * whether the player then learns the curse's identity.
     *
     * <p>Takes the curse and the item, and nothing else. The curse's per-object
     * {@link CurseData} is not wanted: the effect is a property of the curse itself, read from the
     * template's own object ({@code curse->obj->effect} and its message), while the timeout that
     * brought us here has already been dealt with by the caller. C's signature is the same shape,
     * taking a curse index rather than the instance data.
     *
     * <p>The return is a discovery, not a success: it reports whether something happened that the
     * player was not already expecting, which is what makes a previously unknown curse worth
     * revealing. C computes it as {@code !was_aware && ident}.
     *
     * <p><b>Stub:</b> not yet implemented, awaiting the effect runtime; reports {@code false}
     * (nothing happened, so the curse is not revealed).</p>
     *
     * <p>Function doCurseEffect coded before 260817, retyped from taking a {@code CurseEntry} on
     * 260817, commented in full on 260817.
     *
     * @param curse the curse whose effect is firing
     * @param item  the worn item the curse is attached to (the effect's source)
     * @return {@code true} if the effect did something the player would notice
     */
    public static boolean doCurseEffect(Curse curse, ItemObject item) {
        // Stub class TODO: implement
        return false;
    }

    /**
     * Merges every pair of stacks in the gear that can share a slot - the port of C's
     * {@code combine_pack} ({@code obj-gear.c:1242}). Walks the gear backwards, and for each stack
     * looks at every earlier stack for one that will take it whole; failing that, for one that will
     * take part of it.
     *
     * <p>A whole merge, through {@link ItemObject#objectAbsorb}, removes the absorbed stack from
     * both {@code gear} and {@code gearKnown} and is announced to the player. A partial merge,
     * through {@link ItemObject#objectAbsorbPartial}, only shifts counts between two stacks that
     * both survive; C leaves that unannounced on the grounds that shuffling items between stacks
     * is not interesting to read about.
     *
     * <p>Both loops are indexed rather than iterators. C walks a linked list and saves
     * {@code obj1->prev} before merging, because {@code object_absorb} unlinks the absorbed object
     * from the gear list; the port cannot borrow that trick, and iterating a live view of
     * {@code gear} while the body removes from it would fail. Running the outer index down from
     * the end and bounding the inner one by {@code outerIndex} gives the same visit order as C and
     * keeps the removal at {@code outerIndex} clear of the positions still to come.
     *
     * <p>The known objects are absorbed and unlinked before the real ones, so that
     * {@link ItemObject#objectAbsorb} is never handed a stack whose {@code known} half has already
     * gone. The two {@code setNumber} calls afterwards realign the counts; C has no equivalent, and
     * they should never change anything.
     *
     * <p>Function combinePack coded on 260822, commented in full on 260824, moved here from
     * {@link Player} and made static on 260901.
     *
     * @param player the player whose gear is to be combined
     */
    public static void combinePack(Player player) {
        ItemObject item1;
        ItemObject item2;
        boolean displayMessage = false;
        boolean displayRepeat = false;
        ObjectStackEnum stackMode2;

        for (int outerIndex = player.getGear().size() - 1; outerIndex >= 0; outerIndex--) {
            item1 = player.getGear().get(outerIndex);

            if (item1.getKind() == null) continue;
            if (item1.gettValue().isMoney()) continue;

            // use an indexed for loop to ensure that we stop at item1
            for (int innerIndex = 0; innerIndex < outerIndex; innerIndex++) {
                item2 = player.getGear().get(innerIndex);
                stackMode2 = item2.isInQuiver(player) ? ObjectStackEnum.OSTACK_QUIVER
                        : ObjectStackEnum.OSTACK_PACK;

                if (item2.getKind() == null) continue;

                // Are item1 & item2 mergeable?
                Flag<ObjectStackEnum> stackModes = new Flag<>(ObjectStackEnum.class);
                stackModes.on(stackMode2);
                if (item2.mergeable(item1, stackModes)) {
                    displayMessage = true;
                    displayRepeat = true;
                    item2.getKnown().objectAbsorb(item1.getKnown());
                    // Ensure we drop the item from gearKnown before we drop it from here
                    ItemObject knownObject = item1.getKnown();
                    if (knownObject != null) {
                        player.getGearKnown().removeIf(known -> known == knownObject);
                        knownObject.nullKnown();
                    }
                    player.getGear().remove(outerIndex);
                    item1.nullKnown();
                    item2.objectAbsorb(item1);

                    // Ensure numbers align - shouldn't be necessary, but just in case
                    item2.getKnown().setNumber(item2.getNumber());

                    break;
                } else {
                    ObjectStackEnum stackMode1 = item1.isInQuiver(player) ? ObjectStackEnum.OSTACK_QUIVER
                            : ObjectStackEnum.OSTACK_PACK;
                    Flag<ObjectStackEnum> modes1 = new Flag<>(ObjectStackEnum.class);
                    Flag<ObjectStackEnum> modes2 = new Flag<>(ObjectStackEnum.class);
                    modes1.on(stackMode1);
                    modes2.on(stackMode2);
                    if (invenCanStackPartial(player, item2, item1, modes2, modes1)) {
                        // Don't display a message for this case: shuffling
                        // items between stacks isn't interesting to the
                        // player.
                        item2.getKnown().objectAbsorbPartial(item1.getKnown(), modes2, modes1);
                        item2.objectAbsorbPartial(item1, modes2, modes1);

                        // Ensure numbers allign - shouldn't be necessary, but just in case
                        item2.getKnown().setNumber(item2.getNumber());
                        item1.getKnown().setNumber(item1.getNumber());

                        break;
                    }
                }
            }
        }

        PlayerCalcs.calcInventory(player);

        // Redraw gear
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_INVENTORY);
        GameEngine.getEventsBusHandler().eventSignal(GameEventType.EVENT_EQUIPMENT);

        // Message
        if (displayMessage) {
            Message.message("You combine some items in your pack.");

            // Stop "repeat last command" from working if a stack was completely
            // combined with another.
            if (displayRepeat) GameState.getCommandQueue().disableRepeat();
        }
    }

    /**
     * Tests whether at least one item could be moved from {@code item2} onto {@code item1} - the
     * port of C's {@code inven_can_stack_partial} ({@code obj-gear.c:1183}).
     *
     * <p>The two stacks are not interchangeable. {@code item1} is the leading stack, the one whose
     * count the caller means to maximise, and only {@code stackMode1} opens the quiver branch
     * below; passing the pair the other way round asks a different question.
     *
     * <p>Stackability is settled first by {@link ItemObject#objectStackable}. Then, unless either
     * mode says {@code OSTACK_STORE} - stores have no capacity limits - the numbers have to allow
     * it:
     *
     * <ul>
     *   <li>a quiver stack is capped per slot at {@code carry-cap:quiver-slot-size}, divided by
     *       {@code carry-cap:thrown-quiver-mult} for anything that is not ammunition, and is
     *       refused outright when it already sits at that cap;</li>
     *   <li>a quiver stack being fed from outside the quiver is additionally put through
     *       {@link #quiverAbsorbNum}, to check the quiver as a whole has room. That second check
     *       exists only to avoid combining a stack that {@link PlayerCalcs#calcInventory} would then have to
     *       split apart again;</li>
     *   <li>a pack stack is capped at its kind's {@code max_stack}.</li>
     * </ul>
     *
     * <p>Function invenCanStackPartial coded on 260822, commented in full on 260824, moved here
     * from {@link Player} and made static on 260901.
     *
     * @param player     the player whose pack and quiver capacity the numbers are checked against
     * @param item1      the leading stack, the one that is to grow
     * @param item2      the stack that would be drawn from
     * @param stackMode1 the stacking rules in force for {@code item1}
     * @param stackMode2 the stacking rules in force for {@code item2}
     * @return {@code true} if a partial absorb would move at least one item
     */
    private static boolean invenCanStackPartial(Player player, ItemObject item1, ItemObject item2, Flag<ObjectStackEnum> stackMode1,
                                                Flag<ObjectStackEnum> stackMode2) {
        Flag<ObjectStackEnum> combinedModes = new Flag<>(ObjectStackEnum.class);
        combinedModes.copyFrom(stackMode1);
        combinedModes.union(stackMode2);

        // Quick fail
        if (!item1.objectStackable(item2, combinedModes)) {
            return false;
        }

        // Now verifying numbers
        // Leading stack, item1, has to have its count maximised
        if (!combinedModes.has(ObjectStackEnum.OSTACK_STORE)) {
            // Quiver has stricter limits
            if (stackMode1.has(ObjectStackEnum.OSTACK_QUIVER)) {
                int quiverLimit = GameConstants.getCarryCapQuiverSlotSize() /
                        (item1.gettValue().isAmmo() ? 1 : GameConstants.getCarryCapThrownQuiverMult());

                // Are we already at the limit?
                if (item1.getNumber() == quiverLimit) return false;

                // Checked per-stack limits - if trying to move
                // items to the quiver, also check the overall
                // quiver limits to avoid combining and then
                // splitting in calcInventory()
                if (!stackMode2.has(ObjectStackEnum.OSTACK_QUIVER)) {
                    int numFreeSlots = GameConstants.getCarryCapPackSize() -
                            packSlotsUsed(player);
                    int numToQuiver = 0;

                    SplitBetweenPackAndQuiver inSplit = new SplitBetweenPackAndQuiver(numToQuiver, numFreeSlots);
                    SplitBetweenPackAndQuiver outSplit = quiverAbsorbNum(player, item2, inSplit);
                    numToQuiver = outSplit.numToQuiver();
                    numFreeSlots = outSplit.noToPack();

                    if (numToQuiver <= 0) return false;
                }
            } else if (item1.getNumber() == item1.getKind().getBase().getMaxStack()) {
                // No reason to combine if we are already at the limit
                return false;
            }
        }

        return true;
    }

    /**
     * Works out how many of {@code item} the quiver could take, and how many of the offered pack
     * slots that would cost - the port of C's {@code quiver_absorb_num} ({@code obj-gear.c:649}).
     *
     * <p>Anything that is neither ammunition nor {@code OF_THROWING} cannot go in the quiver at
     * all, and is answered with nothing to the quiver and the offered pack slots handed back
     * untouched.
     *
     * <p>Otherwise the quiver is walked slot by slot, accumulating two figures: {@code quiverCount},
     * the total the quiver already holds in slot-size units, and {@code spaceFree}, the room this
     * particular object could use. A slot that stacks with {@code item} contributes its unused
     * remainder. An empty slot contributes a whole slot, but only if the object is ammunition or
     * this is the slot the object's inscription asks for - a thrown weapon may only go where it
     * prefers. A slot holding something else that could itself move elsewhere counts as displaced,
     * and its room is available only if some other slot is empty for the displaced pile to move
     * into, which is what the {@code displaces && numEmpty != 0} test at the foot enforces.
     *
     * <p>The room found is then trimmed to what the pack will pay for. Quiver slots are charged to
     * the pack a slot at a time, so only the part-used slot at the top of the quiver is free; every
     * further slot has to come out of {@code noToPack}. The multiplier makes a thrown weapon cost
     * {@code carry-cap:thrown-quiver-mult} times its count.
     *
     * <p>C passes {@code n_add_pack} and {@code n_to_quiver} by address and writes back through
     * them. The port cannot take an address, so the pair travels in and out as a
     * {@link SplitBetweenPackAndQuiver}.
     *
     * <p>C asserts that no slot holds more than a slot's worth; the port throws instead, because a
     * sentinel return value here would be read by the caller as an ordinary "the quiver is full".
     *
     * <p>Function quiverAbsorbNum coded on 260822, commented in full on 260824, moved here from
     * {@link Player} and made static on 260901.
     *
     * @param player  the player whose quiver is being offered the object
     * @param item    the object being offered to the quiver
     * @param splitIn the maximum number of extra pack slots the quiver may take, in
     *                {@code noToPack}; {@code numToQuiver} is not read
     * @return the number that can go to the quiver, and the offered pack slots left unspent
     */
    private static SplitBetweenPackAndQuiver quiverAbsorbNum(Player player, ItemObject item, SplitBetweenPackAndQuiver splitIn) {
        int numAddPack = splitIn.noToPack();
        int numToQuiver = splitIn.numToQuiver();

        boolean ammo = item.gettValue().isAmmo();
        int quiverCount = 0;
        int spaceFree = 0;
        int numEmpty = 0;
        int currentSlot = -1;

        if (ammo || item.hasFlag(ObjectFlag.OF_THROWING)) {
            int desiredSlot = preferredQuiverSlot(player, item);
            boolean displaces = false;

            for (ItemObject quiverItem : player.getPlayerUpkeep().getQuiver()) {
                currentSlot++;

                if (quiverItem != null) {
                    int mult = quiverItem.gettValue().isAmmo() ? 1 : GameConstants.getCarryCapThrownQuiverMult();

                    quiverCount += quiverItem.getNumber() * mult;
                    Flag<ObjectStackEnum> stackFlags = new Flag<>(ObjectStackEnum.class);
                    stackFlags.on(ObjectStackEnum.OSTACK_PACK);
                    if (quiverItem.objectStackable(item, stackFlags)) {
                        if (quiverItem.getNumber() * mult > GameConstants.getCarryCapQuiverSlotSize()) {
                            String message = "Cannot assign that many items (" + quiverItem.getNumber() * mult
                                    + ") in a quiver slot";
                            logger.error(message);
                            throw new RuntimeException(message);
                        }
                        spaceFree += GameConstants.getCarryCapQuiverSlotSize() - quiverItem.getNumber() * mult;
                    } else if (desiredSlot == currentSlot && preferredQuiverSlot(player, quiverItem) != currentSlot) {
                        // The object to be added prefers to go in this slot,
                        // but it's occupied by another object that could be
                        // displaced to a different quiver slot, if one is
                        // available.
                        displaces = true;
                        if (quiverItem.getNumber() * mult > GameConstants.getCarryCapQuiverSlotSize()) {

                            String message = "Cannot assign that many items (" + quiverItem.getNumber() * mult
                                    + ") in a quiver slot";
                            logger.error(message);
                            throw new RuntimeException(message);
                        }
                        // Avoid double counting in the ammo case since the
                        // empty slot, if any, for the displaced stack is
                        // treated as fully available.
                        if (ammo)
                            spaceFree += GameConstants.getCarryCapQuiverSlotSize() - quiverItem.getNumber() * mult;
                        else
                            spaceFree += GameConstants.getCarryCapQuiverSlotSize();
                    }
                } else {
                    numEmpty++;
                    // Ammo can fit in any empty slot, non-ammo thrown items
                    // are restricted to their preferred slots
                    if (ammo || desiredSlot == currentSlot)
                        spaceFree += GameConstants.getCarryCapQuiverSlotSize();
                }
            }

            // Only possible to add if there is space in the quiver and either
            // are displacing a pile with an empty quiver slot avaialble for it
            // or are not displacing a pile at all.
            if (spaceFree != 0 && ((displaces && numEmpty != 0) || !displaces)) {
                int mult = ammo ? 1 : GameConstants.getCarryCapThrownQuiverMult();

                // When quiver count % quiver slot size is zero, adding 
                // anything will require a pack slot
                int remainder = quiverCount % GameConstants.getCarryCapQuiverSlotSize();
                int limitFromPack = remainder != 0 ? GameConstants.getCarryCapQuiverSlotSize() - remainder : 0;

                if (numAddPack > 0)
                    limitFromPack += numAddPack * GameConstants.getCarryCapQuiverSlotSize();

                spaceFree = Math.min(spaceFree, limitFromPack);
                numToQuiver = Math.min(item.getNumber(), spaceFree / mult);
                numAddPack -= (numToQuiver * mult + GameConstants.getCarryCapQuiverSlotSize() - 1 - remainder)
                        / GameConstants.getCarryCapQuiverSlotSize();
                SplitBetweenPackAndQuiver outgoing = new SplitBetweenPackAndQuiver(numToQuiver, numAddPack);
                return outgoing;
            }
        }

        // Not suitable for the quiver or no space
        SplitBetweenPackAndQuiver outgoing = new SplitBetweenPackAndQuiver(0, numAddPack);
        return outgoing;
    }

    /**
     * Reads the quiver slot an object's inscription asks for - the port of C's
     * {@code preferred_quiver_slot} ({@code obj-gear.c:1396}).
     *
     * <p>The inscription is scanned for an {@code @} followed by the fire or throw command key and
     * a digit, so {@code @f1} asks for slot 1. The fire key is {@code f}, or {@code t} under the
     * roguelike keyset; the throw key is {@code v} either way. Only ammunition and
     * {@code OF_THROWING} objects are considered.
     *
     * <p>The scan restarts from each {@code @} in turn rather than the first, so a later tag still
     * counts when an earlier one is something else. The digit is taken raw, as
     * {@code s.charAt(2) - '0'}, exactly as C does - a slot number outside the quiver is the
     * caller's problem, and no caller acts on one it cannot match.
     *
     * <p>Function preferredQuiverSlot coded on 260822, commented in full on 260824, moved here
     * from {@link Player} and made static on 260901.
     *
     * @param player the player whose keyset decides which letter is the fire key
     * @param item   the object whose inscription is to be read
     * @return the slot number asked for, or {@code -1} if the inscription asks for none
     */
    public static int preferredQuiverSlot(Player player, ItemObject item) {
        int desiredSlot = -1;

        if (item.getNote() != null && (item.gettValue().isAmmo() || item.hasFlag(ObjectFlag.OF_THROWING))) {
            String s;
            char fireKey;
            char throwKey;

            if (item.getNote().contains("@")) {
                s = item.getNote().substring(item.getNote().indexOf('@'));
            } else
                s = null;

            fireKey = player.getPlayerOptions().has(PlayerOptionEnum.OP_rogue_like_commands) ? 't' : 'f';
            throwKey = 'v';

            while (true) {
                if (s == null || s.isEmpty() || !s.contains("@")) break;
                if (s.length() < 3) break;
                if (s.charAt(1) == fireKey || s.charAt(1) == throwKey) {
                    desiredSlot = s.charAt(2) - '0';
                    break;
                }
                s = s.substring(1);
                if (s.contains("@")) {
                    s = s.substring(s.indexOf("@"));
                }
            }
        }

        return desiredSlot;
    }

    /**
     * Counts the pack slots the player's gear occupies - the port of C's {@code pack_slots_used}
     * ({@code obj-gear.c:257}).
     *
     * <p>Equipped items occupy no pack slot and are skipped. Everything else costs one slot, except
     * what is actually in the quiver: quivered stacks are gathered into {@code quiverAmmo} in
     * slot-size units, thrown weapons counting {@code carry-cap:thrown-quiver-mult} apiece, and the
     * whole quiver is then charged as the number of full slots it fills plus one more for any
     * remainder.
     *
     * <p>Being ammunition is not enough to be charged as quiver: the item has to be found in the
     * quiver itself, which is why the inner loop compares identities rather than acting on the
     * first entry it sees.
     *
     * <p>Function packSlotsUsed coded on 260822, commented in full on 260824, moved here from
     * {@link Player} and made static on 260901.
     *
     * @param player the player whose gear is counted
     * @return the number of pack slots in use, quiver included
     */
    public static int packSlotsUsed(Player player) {
        int quiverAmmo = 0;
        int packSlots = 0;

        for (ItemObject item : player.getGear()) {
            boolean found = false;

            // Equipped items don't count
            if (!player.getPlayerBody().itemIsEquipped(item)) {
                // Is it in the quiver
                if (item.gettValue().isAmmo() || item.hasFlag(ObjectFlag.OF_THROWING)) {
                    for (ItemObject quiverItem : player.getPlayerUpkeep().getQuiver()) {
                        if (quiverItem == item) {
                            quiverAmmo += quiverItem.getNumber()
                                    * (item.gettValue().isAmmo() ? 1 : GameConstants.getCarryCapThrownQuiverMult());
                            found = true;
                            break;
                        }
                    }
                }

                if (!found)
                    packSlots++;
            }
        }

        // Full slots
        packSlots += quiverAmmo / GameConstants.getCarryCapQuiverSlotSize();

        if (quiverAmmo % GameConstants.getCarryCapQuiverSlotSize() != 0)
            packSlots++;

        return packSlots;
    }

    /**
     * Appends an object to the end of the gear, and its known half to the parallel known list - the
     * port of C's {@code gear_insert_end} ({@code obj-gear.c}).
     *
     * <p>C walks its linked list to the tail and links the object on; a list append is the same
     * thing. The two lists are kept in step by every gear operation, which is what lets the pack
     * rebuild address an object and its knowledge by the same position.
     *
     * <p>Function gearInsertEnd commented in full on 260827, moved here from {@link Player} and
     * made static on 260901.
     *
     * @param player     the player whose gear the object joins
     * @param itemObject the object to append; its known half is appended too
     */
    public static void gearInsertEnd(Player player, ItemObject itemObject) {
        player.getGear().add(itemObject);
        player.getGearKnown().add(itemObject.getKnown());
    }

    /**
     * Finds an equipment slot of a given type, preferring an empty one - the port of C's
     * {@code slot_by_type} ({@code obj-gear.c:71}).
     *
     * <p>Walks the body in order and stops at the first slot of the right type that is in the state
     * asked for: empty when {@code full} is {@code false}, occupied when it is {@code true}. Failing
     * that it answers the first slot of the right type in the wrong state - the fallback - and
     * failing even that, the slot count, one past the last index, which is this code's "not found".
     *
     * <p>Two slots of the same type is the case that makes the fallback matter: with both rings on,
     * asking for an empty ring slot yields the first ring slot rather than nothing, so a caller
     * wanting to swap has somewhere to put the new one.
     *
     * <p><b>Why the counter is shaped the way it is.</b> C's loop variable outlives its loop, so a
     * completed pass leaves it equal to the slot count and the closing test can distinguish "ran off
     * the end" from "stopped somewhere". Java's cannot, so {@code outValue} stands in for it: it
     * starts at {@code -1}, is assigned at the <em>foot</em> of the body so {@code break} skips it,
     * and is incremented after the loop. A break at index <i>k</i> therefore yields <i>k</i>, a
     * completed pass yields the slot count, and an empty body yields the fallback - the three cases
     * C produces.
     *
     * <p>Function slotByType commented in full on 260827, moved here from {@link Player} and made
     * static on 260901.
     *
     * @param player the player whose body is searched
     * @param type   the slot type wanted
     * @param full   {@code true} to look for an occupied slot, {@code false} for an empty one
     * @return the index of the best matching slot, or the slot count if the body has none of that
     * type
     */
    public static int slotByType(Player player, EquipmentSlotsEnum type, boolean full) {
        int fallback = player.getPlayerBody().getSlots().size();

        int outValue = -1;

        for (int index = 0; index < player.getPlayerBody().getSlots().size(); index++) {
            EquipSlot slot = player.getPlayerBody().getSlots().get(index);
            if (slot.getType() == type) {
                if (full) {
                    if (slot.getItem() != null) break;
                } else {
                    if (slot.getItem() == null) break;
                }

                if (fallback == player.getPlayerBody().getSlots().size())
                    fallback = index;
            }
            outValue = index;
        }
        outValue++;

        return (outValue != player.getPlayerBody().getCount()) ? outValue : fallback;
    }

    /**
     * Finds the equipment slot with a given name - the port of C's {@code slot_by_name}
     * ({@code obj-gear.c}).
     *
     * <p>Names come from {@code body.txt}: {@code weapon}, {@code shooting}, {@code right hand} and
     * so on. Every caller in the power and gear code passes a literal, so a miss means a coding
     * error rather than a runtime condition.
     *
     * <p><b>Diverges from C on a miss.</b> C returns {@code body.count} - one past the last slot -
     * and leaves the caller to notice; the port logs and throws. That is deliberate: no caller here
     * tests for the one-past value, so a wrong name would otherwise be read as a real slot number.
     *
     * @param player the player whose body is searched
     * @param name   the slot's name as {@code body.txt} spells it
     * @return the slot's index
     * @throws IllegalArgumentException if no slot carries that name
     */
    public static int slotByName(Player player, String name) {
        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            if (slot.getName().equals(name))
                return numberFromSlot(player, slot);
        }

        String message = "Invalid slot name passed to ObjectUtils.slotByName()";
        logger.error(message);
        throw new IllegalArgumentException(message);
    }

    /**
     * Returns the equipment slot at a given index - the port of indexing C's
     * {@code p->body.slots[number]}.
     *
     * <p>Unguarded, as C's array access is: callers pass an index that came from
     * {@link #slotByName(Player, String)} or {@link #slotByType(Player, EquipmentSlotsEnum, boolean)}, and both of
     * those can answer one past the last slot, so a caller that has not checked will get an
     * exception here rather than a wrong slot.
     *
     * <p>Function slotByNumber commented in full on 260827, moved here from {@link Player} and
     * made static on 260901.
     *
     * @param player the player whose body holds the slot
     * @param number the slot index
     * @return the slot at that index
     */
    public static EquipSlot slotByNumber(Player player, int number) {
        return player.getPlayerBody().getSlots().get(number);
    }

    /**
     * Finds the index of a given equipment slot - the reverse of
     * {@link #slotByNumber(Player, int)}.
     *
     * <p>Compares by identity rather than equality, because the slots are the player's own instances
     * and two slots of the same type are still different slots.
     *
     * <p>Answers the slot count - one past the last index - for a slot this body does not hold,
     * which is C's convention for "not found" throughout the gear code.
     *
     * <p>Function numberFromSlot commented in full on 260827, moved here from {@link Player} and
     * made static on 260901.
     *
     * @param player the player whose body is searched
     * @param slot   the slot to locate
     * @return its index, or the slot count if this body does not hold it
     */
    public static int numberFromSlot(Player player, EquipSlot slot) {
        int index = -1;
        for (EquipSlot testSlot : player.getPlayerBody().getSlots()) {
            index++;
            if (testSlot == slot) return index;
        }

        return player.getPlayerBody().getSlots().size();
    }

    /**
     * Finds the letter or digit the player selects an item by, the port of C's {@code gear_to_label}
     * ({@code obj-gear.c}).
     *
     * <p>Three places an item can be, and each labels differently. Worn equipment takes its letter
     * from the slot it occupies, so a sword's label is a fact about the body rather than about the
     * sword. Quiver ammunition is numbered from {@code '0'}. Everything else in the pack takes its
     * letter from its position in the inventory list. Each label is therefore positional: moving an
     * item renames it, which is why the pack and quiver are ordered lists rather than sets.
     *
     * <p>The label alphabet skips {@code h}, {@code j}, {@code k} and {@code l}. Those are the
     * roguelike movement keys, and an item labelled with one could not be selected without the
     * player walking instead. C keeps the same string for the same reason.
     *
     * <p>Answers the null character for an item the player is not carrying, which is C's {@code '\0'}
     * fall-through rather than an error: asking for the label of something on the floor is a fair
     * question with no answer.
     *
     * <p>Function gearToLabel coded before 260817, commented in full on 260817, moved here from
     * {@link Player} and made static on 260901.
     *
     * @param player the player whose equipment, quiver and inventory the label is read from
     * @param item   the item to label
     * @return the character the item is selected by, or {@code '\0'} if it is not in the gear
     */
    public static char gearToLabel(Player player, ItemObject item) {
        if (item == null) return '\0';

        String labels = "abcdefgimnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        if (player.getPlayerBody().itemIsEquipped(item)) {
            return labels.charAt(player.getPlayerBody().equippedItemSlot(item));
        }

        for (int quiverIndex = 0; quiverIndex < GameConstants.getCarryCapQuiverSize(); quiverIndex++) {
            ItemObject quiverItem = player.getPlayerUpkeep().getQuiver()[quiverIndex];
            if (item.equals(quiverItem)) {
                return (char) ('0' + quiverIndex);
            }
        }

        for (int invenIndex = 0; invenIndex < GameConstants.getCarryCapPackSize(); invenIndex++) {
            ItemObject invenItem = player.getPlayerUpkeep().getInventory()[invenIndex];
            if (item.equals(invenItem)) {
                return labels.charAt(invenIndex);
            }
        }

        return '\0';
    }

    /**
     * Tests whether the player is carrying a given object, the port of C's
     * {@code object_is_carried} ({@code obj-util.c}).
     *
     * <p>Carried means anywhere in the gear: pack, quiver or worn. C's {@code p->gear} is one linked
     * list holding all three, and equipment is reached by following slot pointers into it rather than
     * by living in a separate collection, so a single containment test answers the question. The port
     * keeps that arrangement, which is why this is one line and not three.
     *
     * <p>The distinction it draws is between an object the player has and an object that is merely
     * nearby — {@code PlayerKnowledge.knowObject} uses it to pick between "You have a Long Sword (c)." and "On the
     * ground: a Long Sword." when reporting something newly recognised.
     *
     * <p>Function isCarried coded on 260816, commented in full on 260816, moved here from
     * {@link Player} and made static on 260901.
     *
     * @param player the player whose gear is searched
     * @param item   the object to look for
     * @return {@code true} if the object is in the player's gear
     */
    public static boolean isCarried(Player player, ItemObject item) {
        return player.getGear().contains(item);
    }

    /**
     * Records whether the given artifact has been created this game, the port of C's
     * {@code mark_artifact_created} ({@code obj-util.c}).
     *
     * <p>C looks the artifact up in the parallel {@code aup_info} array by {@code aidx} and
     * asserts the slot it finds agrees with that index before writing to it. The port holds each
     * artifact's {@link ArtifactUpkeep} directly on the {@link Artifact} rather than in a parallel
     * array, so there is no index to disagree and nothing for that assert to guard — see
     * {@link ArtifactUpkeep}'s class Javadoc for the fuller reasoning.
     *
     * <p>Function markArtifactCreated coded on 260903, commented in full on 260903.
     *
     * @param artifact the artifact to update
     * @param created  whether the artifact has been created
     */
    public static void markArtifactCreated(Artifact artifact, boolean created) {
        artifact.getAup().setCreated(created);
    }

    /**
     * Records whether the given artifact has been seen this game, the port of C's
     * {@code mark_artifact_seen} ({@code obj-util.c}).
     *
     * <p>Same shape as {@link #markArtifactCreated}: C writes into the parallel {@code aup_info}
     * array behind an {@code aidx} assert, and the port writes straight into the artifact's own
     * {@link ArtifactUpkeep}, for which see {@link ArtifactUpkeep}'s class Javadoc.
     *
     * <p>Function markArtifactSeen coded on 260903, commented in full on 260903.
     *
     * @param artifact the artifact to update
     * @param seen     whether the artifact has been seen this game
     */
    public static void markArtifactSeen(Artifact artifact, boolean seen) {
        artifact.getAup().setSeen(seen);
    }

    /**
     * The pair of counts {@link ObjectUtils#quiverAbsorbNum} takes in and hands back - how many of an object
     * can go to the quiver, and how many of the offered pack slots are left unspent.
     *
     * <p>Exists only because of a difference in how the two languages pass things. C declares two
     * {@code int}s and passes their addresses ({@code obj-gear.c:649-650}), so
     * {@code quiver_absorb_num} writes back into the caller's own storage. The port cannot take an
     * address, so the two travel together as a value in and a value out. Compare {@link PlayerCalcs.Extras},
     * which solves the same problem for {@code calc_shapechange}.
     *
     * <p>On the way in only {@code noToPack} is read, as the maximum number of extra pack slots the
     * quiver may claim; {@code numToQuiver} is ignored. On the way out both carry answers.
     *
     * <p>Record SplitBetweenPackAndQuiver coded on 260822, commented in full on 260824.
     *
     * @param numToQuiver the number of items that can be added to the quiver
     * @param noToPack    the number of pack slots offered, and on return those left unspent
     */
    private record SplitBetweenPackAndQuiver(int numToQuiver, int noToPack) {
    }
}
