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
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.QualityValueEnum;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.PlayerNotice;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;

/**
 * The decision half of the ignore subsystem - the port of the questions C's {@code obj-ignore.c}
 * asks, and of the one action it takes.
 *
 * <p><b>Why these live here rather than on {@link Player}.</b> They were written as static methods
 * on {@code Player}, because {@code ignore_drop} is reached from {@code notice_stuff} and the
 * player is the only argument C passes. They read almost nothing of the player, though: the
 * unignoring toggle and the gear list, and everything else they ask is asked of an
 * {@link ItemObject}. Gathering them here puts the ignore decision beside the object state it
 * actually reads, and leaves {@code Player} the character rather than the rules. The methods moved
 * out of {@code Player} on 260901, unchanged; their callers changed with them
 * ({@code PlayerCalcs.noticeStuff} for {@link #ignoreDrop}, {@code GridData} for
 * {@link #ignoreKnownItemOk}).
 *
 * <p><b>What is elsewhere.</b> Only the decision is here. The settings it reads sit with the things
 * they describe - the per-quality bands in {@code ObjectInfo.ignoreLevel}, the aware/unaware kind
 * flags on {@link ObjectKind}, the per-category ego marks on {@code EgoItem} - and the per-object
 * judgements it calls are methods on {@link ItemObject}: {@code getIgnoreTypeOf},
 * {@code ignoreLevelOf}, {@code egoIsIgnored}, {@code checkForInscription}. So this class is three
 * short methods of policy over machinery that belongs to the objects themselves.
 *
 * <p>Class ObjectIgnore assembled on 260901 from methods coded on 260822, commented in full on
 * 260901.
 *
 * @author Rowan Crowther
 */
public class ObjectIgnore {
    private static final Logger logger = LogManager.getLogger(ObjectIgnore.class);

    /**
     * Reports whether a known item may be ignored under the player's ignore settings - the port of
     * C's {@code ignore_known_item_ok} ({@code obj-ignore.c:645}). <b>Stub:</b> always answers
     * {@code false}, so nothing on the floor is hidden by this route yet.
     *
     * <p>The distinction from {@link #ignoreItemOK} is which object is judged. This one is handed
     * the <em>known</em> copy an object list or a map square holds, and C looks the real object up
     * by its index ({@code cave->objects[obj->oidx]}) before asking {@link #isIgnored} about that.
     * Judging the known copy directly would be the wrong question: the known copy is a picture of
     * what the player has learned, not the object the ignore settings describe.
     *
     * <p>Its one caller so far is {@code GridData}, which drops an ignored item out of what a grid
     * shows. While the stub answers {@code false}, every item on a grid is displayed - which is the
     * safe direction for a stub to fail in, since the alternative hides things the player owns.
     *
     * <p><b>Outstanding:</b> C also takes the player here, to test the unignoring toggle before
     * anything else; the port's signature drops the argument and will need it back when the
     * cave's object index is available to resolve the real object.
     *
     * <p>Function ignoreKnownItemOk stubbed on 260822, commented in full on 260901.
     *
     * @param item the known copy of the item to test
     * @return {@code false} always, for now
     */
    public static boolean ignoreKnownItemOk(@NotNull ItemObject item) {
        // TODO: Expand this
        return false;
    }

    /**
     * Drops everything in the gear that the player's ignore settings now cover - the port of C's
     * {@code ignore_drop} ({@code obj-ignore.c:651}).
     *
     * <p>Walks the gear in reverse and, for each item {@link #ignoreItemOK} accepts, pushes a
     * {@code CMD_DROP}. An item inscribed {@code !d} or {@code !*} is left alone. An equipped item
     * asks for confirmation first; a refusal inscribes {@code !d} on it so the same question is not
     * put again on every later notice pass. Nothing is dropped while standing in a shop.
     *
     * <p>The pushed command is marked as a background command, so that {@code CMD_REPEAT} repeats
     * whatever the player actually did rather than this drop, and so the drop does not count
     * towards bloodlust.
     *
     * <p>The two flags at the foot are raised whatever happened above, because a chain that dropped
     * earlier items still needs the gear rebuilt and the pack recombined. C asserts that the
     * command it just pushed is really there; the port throws, for the same reason - an early
     * return would skip those flags.
     *
     * <p>Its caller is {@code PlayerCalcs.noticeStuff}, which clears {@code PN_IGNORE} before
     * calling and runs its combine block afterwards - so the {@code PN_COMBINE} raised below is
     * carried out in the same pass rather than waiting a turn. That pairing is why the flag order
     * at the foot matters, and it is tested from the dispatcher's side in
     * {@code PlayerNoticeChainTest}.
     *
     * <p>Function ignoreDrop coded on 260822, commented in full on 260824, moved here from
     * {@code Player} on 260901.
     *
     * @param player the character whose gear is walked; their unignoring toggle decides whether
     *               anything is eligible at all, and their upkeep carries the two flags raised at
     *               the foot
     */
    public static void ignoreDrop(Player player) {
        for (ItemObject item : player.getGear().reversed()) {
            // skip non-objects & unignoreable objects
            if (item.getKind() == null)
                continue;

            if (!ignoreItemOK(player, item)) continue;

            // check for !d (no drop) inscriptions
            if (item.checkForInscription("!d") == 0 && item.checkForInscription("!*") == 0) {
                // Confirm the drop if the object is equipped
                if (player.getPlayerBody().itemIsEquipped(item)) {
                    if (!item.verifyObject("Really take off and drop", player)) {
                        // Inscribe the item with !d to prevent repeated confirmations
                        String newInscription = item.getNote();
                        if (newInscription == null)
                            newInscription = "!d";
                        else
                            newInscription = newInscription + "!d";
                        item.setNote(newInscription);
                        continue;
                    }
                }

                // We are allowed to drop it. Use the real chunk, not the player's one
                if (!GameState.getCave().getSquare(player.getGrid()).isShop()) {
                    Command dropCommand;

                    player.getPlayerUpkeep().setDropping(true);
                    GameState.getCommandQueue().push(CommandCode.CMD_DROP);
                    dropCommand = GameState.getCommandQueue().commandQueuePeek();
                    if (dropCommand == null) {
                        String message = "Invalid command found on peeking the command queue. Expected a CMD_DROP " +
                                "found a null.";
                        logger.error(message);
                        throw new RuntimeException(message);
                    }
                    dropCommand.setArgItem("item", item);
                    dropCommand.setArgNumber("quantity", item.getNumber());
                    /*
                     * This drop is a side effect:  whatever
                     * command triggered it will be the target
                     * for CMD_REPEAT rather than repeating the
                     * drop, and the drop will not trigger
                     * bloodlust.
                     */
                    dropCommand.setBacgroundCommand(2);
                }
            }
        }

        // update the gear
        player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_INVEN);

        // Combine/reorder the pack
        player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_COMBINE);
    }

    /**
     * Tests whether an object may be ignored right now - the port of C's {@code ignore_item_ok}
     * ({@code obj-ignore.c:622}).
     *
     * <p>Nothing is ignorable while the player is unignoring, which is the state the "show ignored
     * items" toggle puts them in; otherwise the question is passed to {@link #isIgnored}.
     *
     * <p>Private because {@link #ignoreDrop} is its only caller so far. C exports it and asks it
     * from all over - pickup, pile handling, monster movement, object description - so it turns
     * public as the first of those is ported, rather than being reached around.
     *
     * <p>Function ignoreItemOK coded on 260822, commented in full on 260824, moved here from
     * {@code Player} on 260901.
     *
     * @param player the character whose unignoring toggle is read
     * @param item   the object to test
     * @return {@code true} if the object is eligible to be ignored
     */
    public static boolean ignoreItemOK(Player player, ItemObject item) {
        if (player.isUnignoring() != 0) return false;

        return isIgnored(item);
    }

    /**
     * Tests whether an object falls under the player's ignore settings - the port of C's
     * {@code object_is_ignored} ({@code obj-ignore.c:576}).
     *
     * <p>An object with no known half cannot be ignored at all: the player has nothing to judge it
     * by. Beyond that the tests run in C's order - the per-object ignore mark, then the escapes
     * ({@code !k} or {@code !*}, or being an artefact, which is only ever ignored by an explicit
     * mark), then ignore-by-kind, then by ego, then by quality.
     *
     * <p>Every test that asks what the player knows reads {@code item.getKnown()}, the object's own
     * known half, and not {@code Player.itemKnowledge}, which is the port of C's {@code p->obj_k}
     * and records which runes the player has learned in general. That is why no player is passed
     * here at all: the object carries the answer. The two are related only through
     * {@code PlayerKnowledge}, which is where the code that writes a known half from the player's
     * rune knowledge now lives - so an object reaching this method already reflects whatever the
     * last {@code PlayerKnowledge.knowObject} pass wrote into it, and this method never
     * consults the player's runes itself.
     *
     * <p>The distinction matters most at the ego test: C gates on the <em>known</em> ego but takes
     * the index from the real one, so an ego the player has not yet learned does not make the
     * object ignorable.
     *
     * <p>Function isIgnored coded on 260822, commented in full on 260824, moved here from
     * {@code Player} on 260901.
     *
     * @param item the object to test
     * @return {@code true} if the player's settings cover this object
     */
    public static boolean isIgnored(ItemObject item) {
        // Can't ignore unknown things
        if (item.getKnown() == null) return false;

        // Are individual items are marked ignore
        if (item.getKnown().getNotice().has(ObjectNotice.OBJ_NOTICE_IGNORE)) return true;

        // Only ignore artefacts marked to be ignored
        if (item.isArtifact() || item.checkForInscription("!k") != 0
                || item.checkForInscription("!*") != 0) return false;

        // Do ignore by kind
        if (item.flavourIsAware() ? item.getKind().isIgnoredAware()
                : item.getKind().isIgnoredUnaware()) return true;

        IgnoreType type = item.getIgnoreTypeOf();
        if (type == IgnoreType.ITYPE_MAX) return false;

        // ignore ego items if known
        if (item.getKnown().isEgo() && item.egoIsIgnored(type)) return true;

        // Ignore non-artefact objects
        if (item.getKnown().getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED) && !item.isArtifact()
                && ObjectInfo.ignoreLevel.get(type) == QualityValueEnum.IGNORE_ALL) return true;

        return item.ignoreLevelOf().ordinal() <= ObjectInfo.ignoreLevel.get(type).ordinal();
    }
}
