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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Feature;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.game.enums.CommandCode;
import uk.co.jackoftrades.middle.game.gameengine.Command;
import uk.co.jackoftrades.middle.game.gameengine.CommandQueue;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.gameinput.GameInput;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.objects.enums.IgnoreType;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.objects.enums.QualityValueEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.enums.PlayerNotice;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.testsupport.ItemFixture;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftrades.testsupport.ItemFixture.set;

/**
 * Tests {@link ObjectIgnore} — the ignore decision, and the one action it takes.
 *
 * <p>These tests used to live in the player package, because the methods did: {@code ignoreDrop}
 * was tested from {@code PlayerNoticeChainTest} beside the dispatcher that calls it, and the
 * {@code ignoreKnownItemOk} stub from {@code PlayerProgressionTest} beside the other stubs. Both
 * moved with the code on 260901. What is left behind in {@code PlayerNoticeChainTest} is the
 * dispatcher's side of the pairing — that {@code noticeStuff} carries out the combine the ignore
 * pass asks for in the same pass — which is a fact about {@code noticeStuff}'s block order and
 * belongs there.
 *
 * <p><b>How the decision is reached.</b> {@code isIgnored} and {@code ignoreItemOK} are private, so
 * everything here goes through {@link ObjectIgnore#ignoreDrop} and reads the answer off the command
 * queue: a {@code CMD_DROP} carrying the item means the item was judged ignorable, an empty queue
 * means it was not. That is not a workaround but the actual contract — a private predicate has no
 * behaviour of its own, only the drop it does or does not cause.
 *
 * <p><b>Why the world has to be built.</b> {@code ignoreDrop} reaches three globals: the cave, to
 * ask whether the player is standing in a shop; the command queue, to push the drop; and the input
 * boundary, to confirm taking off an equipped item. Each is stood up here and put back afterwards,
 * and the square is given a real feature because {@code isShop} reads through it — the {@link Chunk}
 * constructor fills the grid with featureless squares.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectIgnoreTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The level the player stands on.
     */
    private Chunk level;

    /**
     * The player the game held before.
     */
    private Player savedPlayer;

    /**
     * The cave the game held before.
     */
    private Chunk savedCave;

    /**
     * The command queue the game held before.
     */
    private CommandQueue savedQueue;

    /**
     * The quality bands as they were before, put back after each test. They are a birth option
     * rather than a constant, so a test that raises one has to lower it again.
     */
    private Map<IgnoreType, QualityValueEnum> savedIgnoreLevels;

    /**
     * A feature with the given flags, since {@code isShop} reads the square's feature and the
     * squares a fresh {@link Chunk} is filled with have none.
     *
     * @param flags the terrain flags to raise
     * @return the feature
     */
    private static Feature feature(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> featureFlags = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags) {
            featureFlags.on(flag);
        }
        return new Feature(null, "test", "test", null, 0, 0, featureFlags,
                null, null, null, null, null, null, null, null, null);
    }

    /**
     * An input boundary that answers every yes/no question the same way.
     *
     * @param answer what the player says
     * @return the boundary
     */
    private static GameInput answering(boolean answer) {
        return (GameInput) Proxy.newProxyInstance(
                GameInput.class.getClassLoader(),
                new Class<?>[]{GameInput.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("getCheck")) return answer;
                    return method.getReturnType().isPrimitive() ? false : null;
                });
    }

    /**
     * A potion the player has not learned, which is the plainest ignorable-by-kind case: the kind
     * carries the ignore choice, and a potion's tval maps to no quality category, so nothing else
     * can be the reason it is dropped.
     *
     * @return the item
     */
    private static ItemObject potion() {
        return ItemFixture.item(TValue.TV_POTION).fullyKnown().build();
    }

    /**
     * A minimal artifact definition. Nothing on it is read — {@code isArtifact} only asks whether
     * the field is null — so every field is left empty.
     *
     * @return the artifact
     */
    private static Artifact artifact() {
        return new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(),
                new LinkedHashMap<>(), 0, 0, 0, 0, null, null, null);
    }

    /**
     * Marks an item ignored in its own right, which is the first test {@code isIgnored} makes and
     * the only one that overrides the artifact escape.
     *
     * @param item the item, which must have a known half
     */
    private static void markIgnored(ItemObject item) {
        item.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_IGNORE);
    }

    /**
     * Stands up the cave, the queue and the player.
     */
    @BeforeEach
    void seedWorld() {
        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();
        savedQueue = GameState.getCommandQueue();
        savedIgnoreLevels = ObjectInfo.ignoreLevel;
        ObjectInfo.ignoreLevel = new HashMap<>(savedIgnoreLevels);

        player = new Player();
        level = new Chunk("level", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        level.setCurrentLevel(level);
        set(level.getSquare(Loc.zero), "feat", feature());

        GameState.setCave(level);
        GameState.setPlayer(player);
        GameState.setCommandQueue(new CommandQueue(player));
    }

    /**
     * Puts every global back.
     */
    @AfterEach
    void restoreWorld() {
        GameState.setPlayer(savedPlayer);
        GameState.setCave(savedCave);
        GameState.setCommandQueue(savedQueue);
        ObjectInfo.ignoreLevel = savedIgnoreLevels;
        GameInputHolder.resetInstance();
    }

    /**
     * The drop the ignore pass queued, if it queued one.
     *
     * @return the command on the queue, or {@code null} if nothing was pushed
     */
    private Command queuedCommand() {
        return GameState.getCommandQueue().commandQueuePeek();
    }

    /**
     * The flags raised at the foot, which are the part of {@code ignoreDrop} that runs whatever the
     * loop above decided.
     */
    @Nested
    @DisplayName("the closing flags")
    class ClosingFlags {

        /**
         * An empty pack drops nothing, and still asks for the gear to be rebuilt and the pack
         * recombined. The two flags are raised unconditionally because a pass that dropped
         * something earlier in the chain needs them, and an early return would skip them — which is
         * why the port throws on a missing command rather than returning.
         */
        @Test
        @DisplayName("an empty pack still asks for a rebuild and a recombine")
        void emptyPackStillRaisesBothFlags() {
            ObjectIgnore.ignoreDrop(player);

            assertTrue(player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_COMBINE),
                    "the pack is to be recombined");
            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_INVEN),
                    "and the gear rebuilt");
        }
    }

    /**
     * Which items the pass picks up — the private decision, read off the queue.
     */
    @Nested
    @DisplayName("what is judged ignorable")
    class Decision {

        /**
         * Nothing is marked, so nothing is dropped and no command is queued.
         */
        @Test
        @DisplayName("an unmarked pack is left alone")
        void unmarkedPackIsUntouched() {
            ItemObject item = potion();
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertTrue(player.getGear().contains(item), "the item is still carried");
            assertNull(queuedCommand(), "and nothing was queued to drop it");
        }

        /**
         * An item the player has marked individually is dropped, and the command carries the item
         * and the whole stack.
         */
        @Test
        @DisplayName("an item marked ignore is queued for dropping, stack and all")
        void markedItemIsDropped() {
            ItemObject item = ItemFixture.item(TValue.TV_POTION).number(4).fullyKnown().build();
            markIgnored(item);
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            Command drop = queuedCommand();
            assertNotNull(drop, "a drop was queued");
            assertEquals(CommandCode.CMD_DROP, drop.getCode());
            assertSame(item, drop.getArgItem("item").orElse(null));
            assertEquals(4, drop.getArgNumber("quantity").orElse(0),
                    "the whole stack goes, not one of it");
            assertTrue(player.getPlayerUpkeep().getDropping(),
                    "and the upkeep knows a drop is in progress");
        }

        /**
         * The drop is marked as a side effect, so {@code CMD_REPEAT} repeats whatever the player
         * actually did rather than this drop, and the drop does not count towards bloodlust. The
         * value is C's, and it is the reason the command is peeked at rather than simply pushed.
         */
        @Test
        @DisplayName("the queued drop is marked a background command")
        void dropIsABackgroundCommand() {
            ItemObject item = potion();
            markIgnored(item);
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertEquals(2, queuedCommand().getBackgroundCommand());
        }

        /**
         * An object with no known half cannot be judged at all: the player has nothing to go on, so
         * it is never ignored however the settings stand.
         */
        @Test
        @DisplayName("an object with no known half is never ignored")
        void unknownObjectIsNeverIgnored() {
            ItemObject item = ItemFixture.item(TValue.TV_POTION).build();
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand());
        }

        /**
         * A kind the player has chosen to ignore while unaware of it takes the whole kind, without
         * any individual mark.
         */
        @Test
        @DisplayName("a kind ignored while unaware is dropped")
        void kindIgnoredWhileUnawareIsDropped() {
            ObjectKind kind = new ObjectKind();
            kind.setIgnoredUnaware(true);
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(kind).fullyKnown().build();
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNotNull(queuedCommand(), "the kind's choice covers this item");
        }

        /**
         * The aware and unaware choices are separate settings, and the item's awareness decides
         * which of the two is read. An unaware item is not covered by the aware choice.
         */
        @Test
        @DisplayName("the aware choice does not cover an unaware item")
        void awareChoiceDoesNotCoverAnUnawareItem() {
            ObjectKind kind = new ObjectKind();
            kind.setIgnoredAware(true);
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(kind).fullyKnown().build();
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand(), "the player does not yet know what this is");
        }

        /**
         * {@code !k} is the escape from every settings-driven test, so a kind the player is
         * ignoring wholesale still keeps an item inscribed against it.
         */
        @Test
        @DisplayName("an item inscribed !k escapes the kind's choice")
        void inscriptionProtectsAgainstTheKindChoice() {
            ObjectKind kind = new ObjectKind();
            kind.setIgnoredUnaware(true);
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(kind).fullyKnown().build();
            item.setNote("!k");
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand());
        }

        /**
         * {@code !*}, the blanket escape, does the same.
         */
        @Test
        @DisplayName("an item inscribed !* escapes it too")
        void blanketInscriptionProtectsToo() {
            ObjectKind kind = new ObjectKind();
            kind.setIgnoredUnaware(true);
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(kind).fullyKnown().build();
            item.setNote("!*");
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand());
        }

        /**
         * An artifact is exempt from every settings-driven test, so the kind's choice does not
         * reach it — the escape and the inscriptions are tested by the same branch.
         */
        @Test
        @DisplayName("an artifact escapes the settings")
        void artifactEscapesTheSettings() {
            ObjectKind kind = new ObjectKind();
            kind.setIgnoredUnaware(true);
            ItemObject item = ItemFixture.item(TValue.TV_POTION).kind(kind)
                    .artifact(artifact()).fullyKnown().build();
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand());
        }

        /**
         * But an individual mark overrides that: an artifact the player has marked by hand is
         * ignored, because the mark is tested before the artifact escape. This is the ordering the
         * two branches exist to produce, and it is the only way an artifact ever disappears.
         */
        @Test
        @DisplayName("a marked artifact is ignored all the same")
        void markedArtifactIsIgnored() {
            ItemObject item = ItemFixture.item(TValue.TV_POTION)
                    .artifact(artifact()).fullyKnown().build();
            markIgnored(item);
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNotNull(queuedCommand(), "the mark comes first");
        }

        /**
         * An item whose tval maps to no quality category leaves the decision at
         * {@link IgnoreType#ITYPE_MAX}, and the quality tests below it are skipped rather than
         * indexing a band that is not there. A potion is such an item, so raising every band to
         * {@code IGNORE_ALL} does not touch one.
         */
        @Test
        @DisplayName("an item outside the quality categories is never ignored on quality")
        void itemWithNoQualityCategoryIsSafe() {
            for (IgnoreType type : IgnoreType.values()) {
                ObjectInfo.ignoreLevel.put(type, QualityValueEnum.IGNORE_ALL);
            }
            ItemObject item = potion();
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand());
        }

        /**
         * Quality ignoring proper: an assessed item in a category the player has set to
         * {@code IGNORE_ALL} goes. Soft armour that is not a Robe falls to
         * {@link IgnoreType#ITYPE_BODY_ARMOR}, which is the mapping's second entry for that tval —
         * the first narrows to a kind named Robe and is passed over.
         */
        @Test
        @DisplayName("an assessed item in a category set to ignore-all is dropped")
        void assessedItemInAnIgnoreAllCategoryIsDropped() {
            ObjectInfo.ignoreLevel.put(IgnoreType.ITYPE_BODY_ARMOR, QualityValueEnum.IGNORE_ALL);
            ItemObject item = ItemFixture.item(TValue.TV_SOFT_ARMOR)
                    .kind(ItemFixture.loadedKind(TValue.TV_SOFT_ARMOR, "armour", 40)).fullyKnown().build();
            item.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNotNull(queuedCommand());
        }

        /**
         * The same item with the category left at its default is kept, so the band is doing the
         * work above rather than the tval.
         */
        @Test
        @DisplayName("and is kept when the category is left alone")
        void assessedItemIsKeptWhenTheCategoryIsUnset() {
            ItemObject item = ItemFixture.item(TValue.TV_SOFT_ARMOR)
                    .kind(ItemFixture.loadedKind(TValue.TV_SOFT_ARMOR, "armour", 40)).fullyKnown().build();
            item.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand());
        }
    }

    /**
     * The three things that stop a droppable item being dropped.
     */
    @Nested
    @DisplayName("what stops the drop")
    class Blocks {

        /**
         * The unignoring toggle — what "show ignored items" puts the player into — makes nothing
         * eligible at all, so a marked item stays where it is while it is on.
         */
        @Test
        @DisplayName("nothing is dropped while the player is unignoring")
        void unignoringPlayerDropsNothing() {
            ItemObject item = potion();
            markIgnored(item);
            player.getGear().add(item);
            set(player, "unignoring", 1);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand(), "the toggle is read before anything else");
            assertTrue(player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_COMBINE),
                    "and the closing flags are raised anyway");
        }

        /**
         * Nothing is dropped in a shop, because the floor there is the shopkeeper's. The item is
         * still judged ignorable — it is the drop that is refused, not the decision.
         */
        @Test
        @DisplayName("nothing is dropped while standing in a shop")
        void nothingIsDroppedInAShop() {
            set(level.getSquare(Loc.zero), "feat", feature(TerrainFeatureFlags.TF_SHOP));
            ItemObject item = potion();
            markIgnored(item);
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand());
        }

        /**
         * {@code !d} is the no-drop inscription, so an item carrying it is judged ignorable and
         * then left alone.
         */
        @Test
        @DisplayName("an item inscribed !d is judged ignorable and left alone")
        void noDropInscriptionKeepsTheItem() {
            ItemObject item = potion();
            markIgnored(item);
            item.setNote("!d");
            player.getGear().add(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand());
        }
    }

    /**
     * Equipped items, which are the only ones the player is asked about.
     */
    @Nested
    @DisplayName("an equipped item")
    class Equipped {

        /**
         * Puts an item in the weapon slot, which is what makes {@code itemIsEquipped} true.
         *
         * @param item the item to wield
         */
        private void wield(ItemObject item) {
            set(player.getPlayerBody().getSlots().getFirst(), "item", item);
            player.getGear().add(item);
        }

        /**
         * Taking something off is asked about first, and a yes drops it like anything else.
         */
        @Test
        @DisplayName("is dropped when the player agrees")
        void agreedDropGoesAhead() {
            GameInputHolder.setInstance(answering(true));
            ItemObject item = potion();
            markIgnored(item);
            wield(item);

            ObjectIgnore.ignoreDrop(player);

            assertNotNull(queuedCommand());
        }

        /**
         * A refusal keeps the item and inscribes {@code !d} on it, so the same question is not put
         * again on every later notice pass — the pass runs whenever the ignore flag is raised, and
         * without the inscription a stubborn player would be asked each time.
         */
        @Test
        @DisplayName("is inscribed !d when the player refuses, so the question is not repeated")
        void refusalInscribesNoDrop() {
            GameInputHolder.setInstance(answering(false));
            ItemObject item = potion();
            markIgnored(item);
            wield(item);

            ObjectIgnore.ignoreDrop(player);

            assertNull(queuedCommand(), "it stays equipped");
            assertEquals("!d", item.getNote(), "and says so");
        }

        /**
         * An existing inscription is kept and the tag appended, rather than the note being replaced
         * — the player's own words survive the refusal.
         */
        @Test
        @DisplayName("keeps its existing inscription, with the tag appended")
        void refusalAppendsToAnExistingNote() {
            GameInputHolder.setInstance(answering(false));
            ItemObject item = potion();
            markIgnored(item);
            item.setNote("@w1");
            wield(item);

            ObjectIgnore.ignoreDrop(player);

            assertEquals("@w1!d", item.getNote());
        }
    }

    /**
     * The floor-item test, which is not written yet.
     */
    @Nested
    @DisplayName("ignoreKnownItemOk")
    class KnownItem {

        /**
         * The stub always answers false, so nothing on the floor is hidden by this route. That is
         * the safe direction for a stub to fail in: items the player would rather not see are
         * shown, rather than items they own being hidden.
         */
        @Test
        @DisplayName("always answers false")
        void alwaysAnswersFalse() {
            assertFalse(ObjectIgnore.ignoreKnownItemOk(new ItemObject()));
        }

        /**
         * Including for an item that every other route would ignore, which is what makes it a stub
         * rather than a decision.
         */
        @Test
        @DisplayName("even for an item marked ignore")
        void evenForAMarkedItem() {
            ItemObject item = potion();
            markIgnored(item);

            assertFalse(ObjectIgnore.ignoreKnownItemOk(item));
        }
    }
}
