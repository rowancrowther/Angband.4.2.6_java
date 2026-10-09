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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.Name;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.loaders.MiscDataLoader;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.numerics.RandomValueUtils;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerOptions;
import uk.co.jackoftradesltd.middle.player.enums.PlayerOptionEnum;
import uk.co.jackoftradesltd.middle.player.enums.RandnameType;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ObjectUtils#flavourInit()}, the port of C's {@code flavor_init} ({@code obj-util.c}).
 *
 * <p>Every test here runs with {@link GameState#setTurn} at 2, skipping C's turn-1 rescrub/reparse
 * branch (which calls {@code cleanup_parser}/{@code run_parser} in C, {@link MiscDataLoader#loadFlavours()}
 * in the port) — that branch delegates entirely to the data loader, already exercised by its own
 * tests, so re-running it here would only add a filesystem dependency without covering anything new.
 * Every test also keeps {@link ObjectRegistry}'s kinds confined to tvals it does not otherwise care
 * about, so the seven {@link ObjectUtils#flavourAssignRandom} sweeps this method runs never meet an
 * unflavoured kind of a tval with zero candidate flavours — that combination is C's
 * {@code quit_fmt}/the port's {@link System#exit} branch, and hitting it here would kill the test JVM.
 *
 * <p>The four things below are what {@code flavourInit} adds on top of its already-tested private
 * helpers ({@link ObjectUtilsFlavourResetFixedTest}, {@link ObjectUtilsFlavourAssignFixedTest},
 * {@link ObjectUtilsFlavourAssignRandomTest}): the randarts on/off wiring between
 * {@code flavourResetFixed} and {@code flavourAssignFixed}, the scroll-title generation loop's
 * length bound (the exact clause this port diverged from C on before the fix verified in this
 * session — see the class body for the boundary this defends), the hookup of those titles into
 * {@code flavourAssignRandom(TV_SCROLL)}, and the final aware-marking pass.
 *
 * <p>{@code new Player()} itself needs {@link PlayerRegistry}'s body/race lists and
 * {@link GameConstants}'s carry-cap figures loaded before its constructor can run, so this class is
 * extended with {@link SeededPlayerRegistry}, which supplies bare-minimum stand-ins for exactly that
 * (see its own Javadoc) without needing the real data files.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectUtilsFlavourInitTest {

    private static Player savedPlayer;
    private static int savedTurn;

    private List<ObjectKind> savedObjectKinds;
    private String[] savedScrollAdj;

    private static Field objectKindsField() throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField("objectKinds");
        f.setAccessible(true);
        return f;
    }

    private static Field scrollAdjField() throws Exception {
        Field f = ObjectUtils.class.getDeclaredField("scrollAdj");
        f.setAccessible(true);
        return f;
    }

    @BeforeAll
    static void saveGameState() {
        savedPlayer = GameState.getPlayer();
        savedTurn = GameState.getTurn();
        // The scroll-title loop always runs, so NameCreator.randnameMake needs a RANDNAME_SCROLL
        // word list to learn from — a Tolkien-ish list with no repeated adjacent-letter pair, as
        // PlayerNameRandnameMakeTest uses, so every walk terminates quickly.
        MiscRegistry.setNames(List.of(new Name(RandnameType.RANDNAME_SCROLL.ordinal() + 1,
                new ArrayList<>(List.of("elrond", "aragorn", "frodo", "gandalf", "legolas",
                        "boromir", "galadriel", "celeborn", "thranduil", "faramir", "eowyn",
                        "denethor", "isildur", "beren")))));
    }

    @AfterAll
    static void restoreGameState() {
        GameState.setPlayer(savedPlayer);
        GameState.setTurn(savedTurn);
    }

    private static Player playerWithOptions(PlayerOptions options) {
        Player player = new Player();
        player.setOptions(options);
        return player;
    }

    @SuppressWarnings("unchecked")
    private static void switchOn(PlayerOptions options, PlayerOptionEnum option) throws Exception {
        Field field = PlayerOptions.class.getDeclaredField("options");
        field.setAccessible(true);
        ((Flag<PlayerOptionEnum>) field.get(options)).on(option);
    }

    /**
     * A random flavour, whose resolved sval stays {@code SV_UNKNOWN} (0).
     */
    private static Flavour unresolvedRandomFlavour(String text, int index) {
        return new Flavour(text, ColourEnum.COLOUR_WHITE, index);
    }

    /**
     * A fixed flavour, already resolved to a real sval.
     */
    private static Flavour resolvedFixedFlavour(String text, int sVal, int index) {
        Flavour flavour = new Flavour(text, "some sval symbol", ColourEnum.COLOUR_WHITE, index);
        flavour.setsVal(sVal);
        return flavour;
    }

    private static ObjectKind objectKind(TValue tValue, int sVal) throws Exception {
        ObjectKind kind = new ObjectKind();
        Field tValueField = ObjectKind.class.getDeclaredField("tValue");
        tValueField.setAccessible(true);
        tValueField.set(kind, tValue);
        kind.setsVal(sVal);
        return kind;
    }

    private static ObjectKind namedKind(String name, TValue tValue, int sVal) throws Exception {
        ObjectKind kind = objectKind(tValue, sVal);
        Field nameField = ObjectKind.class.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(kind, name);
        return kind;
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void isolate() throws Exception {
        savedObjectKinds = (List<ObjectKind>) objectKindsField().get(null);
        savedScrollAdj = (String[]) scrollAdjField().get(null);

        GameState.setTurn(2);
        GameState.setPlayer(playerWithOptions(new PlayerOptions()));
        MiscRegistry.setFlavours(List.of());
        ObjectRegistry.setObjectKinds(List.of());
    }

    @AfterEach
    void restore() throws Exception {
        objectKindsField().set(null, savedObjectKinds);
        scrollAdjField().set(null, savedScrollAdj);
    }

    /**
     * The {@code OPT(player, birth_randarts)} gate between {@code flavor_reset_fixed} and
     * {@code flavor_assign_fixed} ({@code obj-util.c}) — off, a fixed flavour's sval is
     * never touched, so the unconditional {@code flavor_assign_fixed} pass binds it by matching
     * sval; on, {@code flavor_reset_fixed} clears it first, so {@code flavor_assign_fixed} skips it
     * ({@code f->sval == SV_UNKNOWN}) and it falls to {@code flavor_assign_random} instead, whose
     * draw (not the original sval match) decides which kind it lands on.
     */
    @Nested
    @DisplayName("the randarts gate")
    class RandartsGate {

        @Test
        @DisplayName("off: fixed ring flavours stay bound by their resolved sval")
        void offKeepsTheFixedBinding() throws Exception {
            Flavour ruby = resolvedFixedFlavour("Ruby", 12, 3);
            Flavour jade = resolvedFixedFlavour("Jade", 40, 7);
            MiscRegistry.setFlavours(List.of(new FlavourKind(TValue.TV_RING, '=', List.of(ruby, jade))));

            ObjectKind ringA = objectKind(TValue.TV_RING, 12);
            ObjectKind ringB = objectKind(TValue.TV_RING, 40);
            ObjectRegistry.setObjectKinds(List.of(ringA, ringB));

            ObjectUtils.flavourInit();

            assertSame(ruby, ringA.getFlavour());
            assertSame(jade, ringB.getFlavour());
        }

        @Test
        @DisplayName("on: fixed ring flavours are reset, then reassigned by the random draw")
        void onReassignsByRandomDraw() throws Exception {
            Flavour ruby = resolvedFixedFlavour("Ruby", 12, 3);
            Flavour jade = resolvedFixedFlavour("Jade", 40, 7);
            MiscRegistry.setFlavours(List.of(new FlavourKind(TValue.TV_RING, '=', List.of(ruby, jade))));

            ObjectKind ringA = objectKind(TValue.TV_RING, 12);
            ObjectKind ringB = objectKind(TValue.TV_RING, 40);
            ObjectRegistry.setObjectKinds(List.of(ringA, ringB));

            PlayerOptions options = new PlayerOptions();
            switchOn(options, PlayerOptionEnum.OP_birth_randarts);
            GameState.setPlayer(playerWithOptions(options));

            long seed = 99L;
            RandomValueUtils.stateInit(seed);
            int firstDraw = new Random(seed).nextInt(0, 2);

            ObjectUtils.flavourInit();

            // C's list is newest-first: draw 0 is the last flavour in the file
            Flavour expectedForA = firstDraw == 0 ? jade : ruby;
            Flavour expectedForB = firstDraw == 0 ? ruby : jade;

            assertSame(expectedForA, ringA.getFlavour());
            assertSame(expectedForB, ringB.getFlavour());
            assertEquals(12, expectedForA.getsVal());
            assertEquals(40, expectedForB.getsVal());
        }
    }

    /**
     * The scroll-title loop ({@code obj-util.c}): each of the {@code MAX_TITLES} (50, C's
     * {@code obj-util.h}) titles is built word by word, and a word is kept only while the running
     * length of kept words and their trailing spaces plus the next word stays under
     * {@code sizeof(scroll_adj[0]) - 3} (15; the array is 18 bytes, C's {@code maxTitleLength}).
     * C then writes the closing quote over the last trailing space with
     * {@code buf[titlelen] = '"'}, so a title is the opening quote, the words with single spaces
     * between them, and the closing quote straight after the last letter. That makes the longest
     * title 16 characters and the shortest 8: C's own comment gives 6 to 14 letters and spaces
     * between the quotes. A trailing space before the closing quote is the fault this pins.
     */
    @Nested
    @DisplayName("scroll title generation")
    class ScrollTitleGeneration {

        @Test
        @DisplayName("every generated title is quoted, within the length bound, and unique")
        void everyTitleIsWellFormedAndUnique() throws Exception {
            ObjectUtils.flavourInit();

            String[] titles = (String[]) scrollAdjField().get(null);
            assertEquals(50, titles.length, "MAX_TITLES, obj-util.h");

            Set<String> seen = new HashSet<>();
            for (String title : titles) {
                assertNotNull(title);
                assertTrue(title.startsWith("\""), () -> "must open with a quote: " + title);
                assertTrue(title.endsWith("\""), () -> "must close with a quote: " + title);
                assertTrue(title.length() <= 16,
                        () -> "title exceeds the 14-character body bound: " + title);
                assertTrue(title.length() >= 8,
                        () -> "title is shorter than C can produce: " + title);
                assertFalse(title.endsWith(" \""),
                        () -> "C overwrites the trailing space with the closing quote: " + title);
                assertFalse(title.contains("  "), () -> "words are separated by one space: " + title);
                assertTrue(seen.add(title), () -> "duplicate title, C's i-- retry should prevent this: " + title);
            }
        }
    }

    /**
     * The scroll-only text overwrite ({@code f->text = scroll_adj[k_info[i].sval]},
     * {@code obj-util.c}), reached this time through {@code flavourInit}'s own call to
     * {@code flavourAssignRandom(TV_SCROLL)} rather than invoked directly, proving the titles built
     * earlier in the same pass are what get bound.
     */
    @Nested
    @DisplayName("scroll flavour wiring")
    class ScrollFlavourWiring {

        @Test
        @DisplayName("a scroll kind's flavour text comes from the title generated this pass")
        void scrollFlavourTextComesFromGeneratedTitles() throws Exception {
            Flavour blank = unresolvedRandomFlavour(null, 5);
            MiscRegistry.setFlavours(List.of(new FlavourKind(TValue.TV_SCROLL, '?', List.of(blank))));

            ObjectKind scroll = objectKind(TValue.TV_SCROLL, 9);
            ObjectRegistry.setObjectKinds(List.of(scroll));

            ObjectUtils.flavourInit();

            assertSame(blank, scroll.getFlavour());
            String[] titles = (String[]) scrollAdjField().get(null);
            assertEquals(titles[9], blank.getText());
        }
    }

    /**
     * The final analyse pass ({@code obj-util.c}): a named, unflavoured, non-artifact kind
     * becomes {@code aware}; a nameless (empty) kind is skipped entirely; a special artifact kind is
     * exempt even though it too has no flavour, matching C's
     * {@code kind->kidx < z_info->ordinary_kind_max} guard via
     * {@link ObjectKind#isSpecialArtifactKind()}.
     */
    @Nested
    @DisplayName("the final aware-marking pass")
    class AwareMarkingPass {

        @Test
        @DisplayName("marks a named, unflavoured, non-artifact kind aware")
        void marksOrdinaryUnflavouredKindAware() throws Exception {
            ObjectKind sword = namedKind("Long Sword", TValue.TV_SWORD, 3);
            ObjectRegistry.setObjectKinds(List.of(sword));

            ObjectUtils.flavourInit();

            assertTrue(sword.isAware());
        }

        @Test
        @DisplayName("leaves a nameless (empty) kind untouched")
        void leavesNamelessKindUntouched() throws Exception {
            ObjectKind empty = objectKind(TValue.TV_SWORD, 1);
            ObjectRegistry.setObjectKinds(List.of(empty));

            ObjectUtils.flavourInit();

            assertFalse(empty.isAware());
        }

        @Test
        @DisplayName("leaves a special artifact kind's aware flag untouched even when unflavoured")
        void leavesSpecialArtifactKindUntouched() throws Exception {
            ObjectKind artifactKind = namedKind("Grond, the Hammer", TValue.TV_SWORD, 1);
            Field specialField = ObjectKind.class.getDeclaredField("isSpecialArtifactKind");
            specialField.setAccessible(true);
            specialField.set(artifactKind, true);
            ObjectRegistry.setObjectKinds(List.of(artifactKind));

            ObjectUtils.flavourInit();

            assertFalse(artifactKind.isAware());
        }
    }
}
