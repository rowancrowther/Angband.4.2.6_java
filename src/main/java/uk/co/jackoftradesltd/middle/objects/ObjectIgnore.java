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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.game.enums.CommandCode;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.*;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerKnowledge;
import uk.co.jackoftradesltd.middle.player.enums.PlayerNotice;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.middle.utils.StringUtils;

import java.util.Iterator;

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

    /**
     * Puts the appropriate autoinscription on an object, if any is due - the port of C's
     * {@code apply_autoinscription} ({@code obj-ignore.c:242-288}).
     *
     * <p>The note fetched at the top is whichever of {@link ObjectKind#getNoteAware()} or
     * {@link ObjectKind#getNoteUnaware()} matches the object's current aware state
     * ({@link #getAutoinscription}). Before anything else, a note that is still the
     * <em>unaware</em> autoinscription is cleared once the object becomes aware and that
     * unaware text no longer matches the note the object should now carry - matching C's
     * {@code streq(obj->note, kind->note_unaware)} guard. The check is narrow on purpose: a
     * note the player wrote themselves, or one that already happens to equal the current
     * autoinscription, is left alone rather than overwritten.
     *
     * <p>{@link #runesAutoinscribe} always runs next, C's own comment marking that ordering
     * "for now" rather than settled. Only after that do the four early-return guards apply, in
     * C's order: no autoinscription configured, already inscribed, not carried, or ignored under
     * the player's settings ({@link #ignoreItemOK}) - each answering the caller with {@code 0} the
     * same way C's {@code int} return does. Passing every guard writes the note and reports the
     * autoinscription with a message, returning {@code 1}, exactly as C does with its {@code msg}
     * call and its own {@code return 1}.
     *
     * <p>The {@code obj != null} check at line 268 mirrors a check C itself never needs: C
     * dereferences {@code obj->kind->aware} to compute {@code aware} one line above its own
     * {@code obj ? ... : NULL} ternary, so the null branch there is as unreachable in the
     * original as it is here.
     *
     * <p>Function applyAutoinscription coded on 260905, commented in full on 260905.
     *
     * @param player the character whose gear and ignore settings are consulted
     * @param obj    the object to inscribe
     * @return {@code 1} if the object was autoinscribed, {@code 0} if none of the guards let it
     * through
     */
    public static int applyAutoinscription(Player player, ItemObject obj) {
        boolean aware = obj.getKind().isAware();
        String note = obj != null ? getAutoinscription(obj.getKind(), aware) : null;

        if (aware
                && obj.getNote() != null
                && obj.getKind().getNoteUnaware() != null
                && obj.getNote().equals(obj.getKind().getNoteUnaware())
                && (note == null || !(obj.getNote().equals(note)))) {
            obj.setNote(null);
        }

        // Make rune autoinscription go first
        runesAutoinscribe(player, obj);

        // No note - don't inscribe
        if (note == null) return 0;

        // Don't reinscribe if it is already inscribed
        if (obj.getNote() != null) return 0;

        // Don't inscribe unless the player is carrying it
        if (!ObjectUtils.isCarried(player, obj)) return 0;

        // Don't inscribe if ignored
        if (ignoreItemOK(player, obj)) return 0;

        // Get an object description
        Flag<ObjectDescription> descFlags = new Flag<>(ObjectDescription.class,
                ObjectDescription.ODESC_PREFIX, ObjectDescription.ODESC_COMBAT,
                ObjectDescription.ODESC_EXTRA);
        String oName = ObjectUtils.objectDesc(obj, descFlags, player);

        obj.setNote(note);

        Message.message("You autoinscribe " + oName + ".");

        return 1;
    }

    /**
     * Applies every rune autoinscription the player is entitled to see onto a single object -
     * the port of C's {@code runes_autoinscribe} ({@code obj-ignore.c:217-224}).
     *
     * <p>C walks the full rune index range {@code 0..max_runes()} and inscribes only where
     * {@code object_has_rune(obj, i) && player_knows_rune(p, i)} both hold; this walks
     * {@link ObjectRegistry#getRunes()} instead, but keeps the same two-part guard:
     * {@link ObjectKnowledge#objectHasRune} asks whether the object carries the rune's property,
     * and {@link PlayerKnowledge#knowsRune} asks whether the player has learned it. Neither half
     * alone is enough - an object can carry a property the player hasn't identified, and that
     * boundary between "the object has it" and "the player may be told" is exactly what the pair
     * of checks enforces.
     *
     * <p>Function runesAutoinscribe coded on 260905, commented in full on 260905.
     *
     * @param player the player whose knowledge gates which runes get inscribed
     * @param obj    the object to inscribe
     */
    private static void runesAutoinscribe(Player player, ItemObject obj) {
        for (Rune rune : ObjectRegistry.getRunes()) {
            if (ObjectKnowledge.objectHasRune(obj, rune) && PlayerKnowledge.knowsRune(player, rune))
                runeAddAutoinscription(obj, rune);
        }
    }

    /**
     * Makes or extends a single rune's autoinscription on an object - the port of C's
     * {@code rune_add_autoinscription} ({@code obj-ignore.c:172-188}).
     *
     * <p>Three clauses mirror the C in order: a rune with no configured note
     * ({@link Rune#getNote()} {@code == null}) is skipped outright, matching {@code !rune_note(i)};
     * a note that already contains the rune's text is left untouched, the same test as C's
     * {@code strstr(quark_str(obj->note), quark_str(rune_note(i)))}; otherwise the rune's note is
     * appended to whatever note the object already carries, with an absent note treated as the
     * empty string, matching C's zero-initialised {@code current_note} buffer.
     *
     * <p>C accumulates the combined note in a fixed 80-byte stack buffer, and
     * {@code my_strcpy}/{@code my_strcat} silently truncate at that limit; this builds an
     * unbounded {@link String} instead, so a combination of rune notes long enough to overflow
     * C's buffer would not truncate here. Every rune note in the shipped data is short enough
     * that the two haven't been observed to diverge in practice.
     *
     * <p>Function runeAddAutoinscription coded on 260905, commented in full on 260905.
     *
     * @param obj  the object to inscribe
     * @param rune the rune whose note is being added
     */
    private static void runeAddAutoinscription(ItemObject obj, Rune rune) {
        String currentNote = "";

        // No inscription or already there - don't bother
        if (rune.getNote() == null) return;
        if (obj.getNote() != null && obj.getNote().contains(rune.getNote())) return;

        // Extend any current note
        if (obj.getNote() != null)
            currentNote = obj.getNote();
        currentNote = currentNote + rune.getNote();

        obj.setNote(currentNote);
    }

    /**
     * Looks up an object kind's autoinscription for the given aware state - the port of C's
     * {@code get_autoinscription} ({@code obj-ignore.c:229}).
     *
     * <p>A missing kind answers {@code null} before either branch is tried, matching C's
     * {@code !kind} guard; otherwise the aware flag alone picks {@link ObjectKind#getNoteAware()}
     * or {@link ObjectKind#getNoteUnaware()}, mirroring C reading {@code note_aware} or
     * {@code note_unaware} through {@code quark_str}. Nothing here reads the kind's own awareness -
     * the caller decides which side of the boundary to ask for, the way {@code apply_autoinscription}
     * passes {@code obj->kind->aware} in explicitly rather than this function reading it itself.
     *
     * <p>Function getAutoinscription coded on 260905, commented in full on 260905.
     *
     * @param kind  the object kind to look up, or {@code null}
     * @param aware {@code true} to fetch the aware autoinscription, {@code false} for the unaware one
     * @return the matching autoinscription, or {@code null} if the kind is {@code null} or has none
     * set
     */
    private static String getAutoinscription(ObjectKind kind, boolean aware) {
        if (kind == null) return null;

        if (aware) {
            return kind.getNoteAware();
        } else {
            return kind.getNoteUnaware();
        }
    }

    /**
     * Reports whether the player has chosen to ignore unidentified items of this kind - the port of
     * C's {@code kind_is_ignored_unaware} ({@code obj-ignore.c:561-564}), which tests the
     * {@code IGNORE_IF_UNAWARE} bit of {@code kind->ignore} directly. Here that same bit test goes
     * through {@link ObjectKind#hasIgnoreFlag}, so this wrapper does nothing C's version doesn't - it
     * just names the one flag being asked about.
     *
     * <p>Function kindIsIgnoredUnaware coded on 260905, commented in full on 260905.
     *
     * @param kind the object kind to test
     * @return {@code true} if unidentified items of this kind are ignored
     */
    public static boolean kindIsIgnoredUnaware(ObjectKind kind) {
        return kind.hasIgnoreFlag(IgnoreFlag.IGNORE_IF_UNAWARE);
    }

    /**
     * Marks an object kind to be ignored once it is identified - the port of C's
     * {@code kind_ignore_when_aware} ({@code obj-ignore.c:567-571}).
     *
     * <p>Two statements, in C's order: set the {@code IGNORE_IF_AWARE} bit on the kind through
     * {@link ObjectKind#setIgnoreFlag}, then raise {@code PN_IGNORE} on the player's upkeep, matching
     * C's {@code kind->ignore |= IGNORE_IF_AWARE; player->upkeep->notice |= PN_IGNORE;}. C reaches the
     * player through its global; the port takes one as an argument instead, which is why this method
     * carries a {@link Player} that {@link ObjectKind#setIgnoreFlag} itself does not need to.
     *
     * <p>The distinction from {@link ObjectKind#setIgnoredAware} matters: that setter only touches the
     * bit, both on and off, and raises no notice flag, so it is not a stand-in for this method wherever
     * C's function is the one being called - only wherever a caller already raises {@code PN_IGNORE}
     * itself, the way {@code object_flavor_aware} does.
     *
     * <p>Function kindIgnoreWhenAware coded on 260905, commented in full on 260905.
     *
     * @param kind   the object kind to mark ignored once identified
     * @param player the character whose upkeep receives the {@code PN_IGNORE} notice flag
     */
    public static void kindIgnoreWhenAware(ObjectKind kind, Player player) {
        kind.setIgnoreFlag(IgnoreFlag.IGNORE_IF_AWARE);
        player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_IGNORE);
    }

    /**
     * Applies whatever autoinscription is due to every object the player carries - the port of C's
     * {@code autoinscribe_pack} ({@code obj-ignore.c:352-359}).
     *
     * <p>Walks the gear forward, in C's order - {@code p->gear} down its {@code obj->next} chain -
     * and calls {@link #applyAutoinscription} on each item in turn. Forward is the right direction
     * here, unlike {@link #ignoreDrop}'s reverse walk: C's own loop starts at the head of the list
     * and follows {@code next}, with no equivalent of {@code ignore_drop}'s explicit backwards scan.
     * Nothing {@link #applyAutoinscription} does changes gear membership, so the iterator never sees
     * the list it is walking mutate underneath it.
     *
     * <p>Function autoinscribePack coded on 260905, commented in full on 260905.
     *
     * @param player the character whose gear is walked
     */
    public static void autoinscribePack(Player player) {
        if (player.getGear() == null) return;
        
        Iterator<ItemObject> it = player.getGear().getIterator();

        while (it.hasNext()) {
            ItemObject obj = it.next();
            applyAutoinscription(player, obj);
        }
    }

    /**
     * Applies whatever autoinscription is due to every object on the floor beneath the player - the
     * port of C's {@code autoinscribe_ground} ({@code obj-ignore.c:340-347}).
     *
     * <p>C's loop starts from {@code square_object(cave, p->grid)}, which answers {@code NULL} -
     * and so never runs the loop body at all - when the player's grid fails
     * {@code square_in_bounds}. The bounds guard at the top mirrors that: {@link Chunk#inBounds}
     * is the same x/y range test as {@code square_in_bounds}, and returning early on failure is the
     * port's equivalent of the C loop never starting, rather than dereferencing a square that is not
     * there.
     *
     * <p>Once the grid is known to be in bounds, this is {@link #autoinscribePack}'s loop over a
     * different pile: walk the square's objects forward and call {@link #applyAutoinscription} on
     * each in turn, matching C's {@code obj->next} chain from the square's object list.
     *
     * <p>Function autoinscribeGround coded on 260905, commented in full on 260905.
     *
     * @param player the character whose square is inspected
     */
    public static void autoinscribeGround(Player player) {
        Chunk cave = GameState.getCave();
        if (!cave.inBounds(player.getGrid())) return;

        Iterator<ItemObject> it = cave.getSquare(player.getGrid()).getObjectPile().getIterator();

        while (it.hasNext()) {
            applyAutoinscription(player, it.next());
        }
    }
}
