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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.EgoItem;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests what {@link ObjectDataLoader} leaves in {@link ObjectRegistry} after the whole init chain
 * has run against the real {@code lib/gamedata}.
 *
 * <p>The expected values are counts taken from the C distribution's own data files, not from the
 * Java readers: 34 {@code name:} records in {@code object_base.txt} (its two {@code default:} lines
 * are not bases), 11 in {@code slay.txt}, 10 in {@code brand.txt}, 27 in {@code curse.txt}, 375 in
 * {@code object.txt}, 163 in {@code activation.txt}, 107 in {@code ego_item.txt}, 138 in
 * {@code artifact.txt}, 79 in {@code object_property.txt} and 7 in {@code chest_trap.txt}. C's own
 * counters ({@code slay_max}, {@code brand_max}, {@code curse_max}, {@code act_max},
 * {@code e_max}, {@code a_max}, {@code property_max}) are all one higher because each array carries
 * a blank slot; that is the accepted deviation recorded in the registry's class block, so the tests
 * assert the plain record count.
 *
 * <p>One base more than the file holds is expected: the port appends a synthetic {@code none} base
 * for the kinds of tval {@code none} ({@code <pile>}, {@code <unknown item>} and the like), where C
 * simply leaves {@code kb_info[0]} zeroed. It is C's slot 0, not a record read from the file.
 *
 * <p>Book kinds: {@code class.txt} names 20 distinct books that {@code object.txt} lacks (every
 * book of the four dungeon realms' first-to-last, none of which the object file lists), and the
 * class parser writes a kind for each, ahead of the artifact parser. They take indexes 375 to 394 -
 * above {@code ordinary_kind_max} but not special artifact kinds.
 *
 * <p>{@code INSTA_ART} is on every special artifact kind, and on exactly two ordinary ones that
 * {@code object.txt} flags itself: {@code & Mighty Hammer~} and {@code & Massive Iron Crown~}.
 *
 * <p>Special artifact kinds: C's {@code write_dummy_object_record} writes one only when
 * {@code lookup_sval} finds no kind of that tval and name in {@code object.txt}. Reading the 138
 * artifacts' {@code base-object:} lines against the 375 kinds (names compared case-insensitively
 * with {@code &} and {@code ~} stripped) leaves 14 distinct pairs unmatched: five amulets, three
 * lights ({@code Phial}, {@code Star}, {@code Arkenstone}) and six rings. They take the kind indexes
 * from 395 up (after the 20 book kinds), and, because C hands out each new sval as {@code kb_info[tval].num_svals + 1}, the
 * three lights follow the two ordinary lights in {@code object.txt} as svals 3, 4 and 5 in file
 * order.
 *
 * <p>List order: C's slay, brand, curse and activation arrays hold the file's records last-first,
 * because the parsers prepend and the {@code finish_parse_*} functions copy from the head; ego
 * items, artifacts and object properties keep file order. One test pins the first and last record of
 * each list against the data files.
 *
 * <p>Class ObjectDataLoaderRegistryTest coded on 261013, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
class ObjectDataLoaderRegistryTest {

    @BeforeAll
    static void bootstrap() throws IOException {
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);
    }

    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
    }

    @Test
    @DisplayName("each object-domain file registers its C record count, with no blank slot")
    void recordCounts() {
        assertAll(
                () -> assertEquals(34 + 1, ObjectRegistry.getObjectBases().size()),
                () -> assertEquals(11, ObjectRegistry.getSlays().size()),
                () -> assertEquals(10, ObjectRegistry.getBrands().size()),
                () -> assertEquals(27, ObjectRegistry.getCurses().size()),
                () -> assertEquals(163, ObjectRegistry.getActivations().size()),
                () -> assertEquals(107, ObjectRegistry.getEgoItems().size()),
                () -> assertEquals(138, ObjectRegistry.getArtifacts().size()),
                () -> assertEquals(79, ObjectRegistry.getObjectProperties().size()));
    }

    @Test
    @DisplayName("the *Max counters are the plain record counts")
    void countersAreRecordCounts() {
        assertAll(
                () -> assertEquals(11, ObjectRegistry.getSlayMax()),
                () -> assertEquals(10, ObjectRegistry.getBrandMax()),
                () -> assertEquals(27, ObjectRegistry.getCurseMax()),
                () -> assertEquals(163, ObjectRegistry.getRandartActivationsMax()),
                () -> assertEquals(107, ObjectRegistry.getEgoItemKindMax()),
                () -> assertEquals(138, ObjectRegistry.getArtifactKindMax()),
                () -> assertEquals(79, ObjectRegistry.getObjectsPropertyMax()));
    }

    /**
     * C's parsers prepend each slay, brand, curse and activation, and the matching
     * {@code finish_parse_*} copies from the head, so slot 1 of those arrays is the last record in
     * the file. Ego items, artifacts, object properties and kinds are copied tail-first and keep file
     * order. The first and last records of each file below were read off the data files.
     */
    @Test
    @DisplayName("slays, brands, curses and activations are in C's reverse file order; the rest keep file order")
    void listOrderFollowsC() throws Exception {
        assertAll(
                () -> assertEquals("UNDEAD_5", ObjectRegistry.getSlays().getFirst().getCode(), "slay first"),
                () -> assertEquals("EVIL_2", ObjectRegistry.getSlays().getLast().getCode(), "slay last"),
                () -> assertEquals("POIS_2", ObjectRegistry.getBrands().getFirst().getCode(), "brand first"),
                () -> assertEquals("ACID_3", ObjectRegistry.getBrands().getLast().getCode(), "brand last"),
                () -> assertEquals("air swing", ObjectRegistry.getCurses().getFirst().getName(), "curse first"),
                () -> assertEquals("vulnerability", ObjectRegistry.getCurses().getLast().getName(), "curse last"),
                () -> assertEquals("DRAGON_POWER", ObjectRegistry.getActivations().getFirst().getName(),
                        "activation first"),
                () -> assertEquals("CURE_POISON", ObjectRegistry.getActivations().getLast().getName(),
                        "activation last"),
                () -> assertEquals("of Shadows", egoName(ObjectRegistry.getEgoItems().getFirst()), "ego first"),
                () -> assertEquals("of Speed", egoName(ObjectRegistry.getEgoItems().getLast()), "ego last"),
                () -> assertEquals("of Galadriel", ObjectRegistry.getArtifacts().getFirst().getName(),
                        "artifact first"),
                () -> assertEquals("of Morgoth", ObjectRegistry.getArtifacts().getLast().getName(),
                        "artifact last"),
                () -> assertEquals("strength", ObjectRegistry.getObjectProperties().getFirst().getName(),
                        "property first"),
                () -> assertEquals("cold immunity", ObjectRegistry.getObjectProperties().getLast().getName(),
                        "property last"));
    }

    /**
     * {@code EgoItem} has no name getter, so the name is read from its field.
     */
    private static String egoName(EgoItem ego) throws Exception {
        Field f = EgoItem.class.getDeclaredField("name");
        f.setAccessible(true);
        return (String) f.get(ego);
    }

    @Test
    @DisplayName("chest_trap.txt registers its 7 traps")
    void chestTrapsLoaded() throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField("chestTraps");
        f.setAccessible(true);

        assertEquals(7, ((List<?>) f.get(null)).size());
    }

    @Test
    @DisplayName("object.txt's 375 kinds are the ordinary ceiling; 20 book kinds then 14 special artifact kinds sit above it")
    void kindTableAndCeiling() {
        assertEquals(375, ObjectRegistry.getObjectBaseKindMax());
        assertEquals(375 + 20 + 14, ObjectRegistry.getObjectKindCount());

        List<ObjectKind> kinds = ObjectRegistry.getObjectKinds();
        for (int i = 0; i < kinds.size(); i++) {
            ObjectKind kind = kinds.get(i);
            assertEquals(i, kind.getKindIndex(), "index of " + kind.getName());
            assertEquals(i >= 375 + 20, kind.isSpecialArtifactKind(), "special flag of " + kind.getName());
            boolean fileInstaArt = kind.getName().equals("& Mighty Hammer~")
                    || kind.getName().equals("& Massive Iron Crown~");
            assertEquals(i >= 375 + 20 || fileInstaArt, kind.getKindFlags().has(ObjectKindFlag.KF_INSTA_ART),
                    "KF_INSTA_ART of " + kind.getName());
        }
    }

    @Test
    @DisplayName("special light kinds take svals 3, 4 and 5 after the two ordinary lights, in file order")
    void specialLightSvals() {
        ObjectKind phial = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Phial");
        ObjectKind star = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Star");
        ObjectKind arkenstone = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Arkenstone");

        assertAll(
                () -> assertEquals("& Phial~", phial.getName()),
                () -> assertEquals(3, phial.getsVal()),
                () -> assertEquals(4, star.getsVal()),
                () -> assertEquals(5, arkenstone.getsVal()),
                () -> assertSame(phial, ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, 3)));
    }

    @Test
    @DisplayName("an artifact whose base-object names an object.txt kind reuses it rather than synthesizing one")
    void ordinaryBaseObjectIsReused() {
        ObjectKind leather = ObjectRegistry.lookupObjectKind(TValue.TV_SOFT_ARMOR, "Soft Leather Armour");

        assertNotNull(leather);
        assertFalse(leather.isSpecialArtifactKind());
        assertTrue(leather.getKindIndex() < 375);
    }

    @Test
    @DisplayName("the unknown item and treasure sentinels are resolved by name")
    void unknownSentinels() {
        assertNotNull(ObjectRegistry.unknownItemKind);
        assertNotNull(ObjectRegistry.unknownGoldKind);
        assertEquals("<unknown item>", ObjectRegistry.unknownItemKind.getName());
        assertEquals("<unknown treasure>", ObjectRegistry.unknownGoldKind.getName());
        assertEquals(TValue.TV_NONE, ObjectRegistry.unknownItemKind.gettValue());
    }

    @Test
    @DisplayName("every curse's object carries the <curse object> kind, and a known twin marked assessed")
    void curseObjectsHaveKinds() {
        ObjectKind curseKind = ObjectRegistry.lookupObjectKind(TValue.TV_NONE, "<curse object>");
        assertNotNull(curseKind);

        for (Curse curse : ObjectRegistry.getCurses()) {
            assertSame(curseKind, curse.getItemObject().getKind(), curse.getName());
            assertEquals(curseKind.getsVal(), curse.getItemObject().getsValue(), curse.getName());
            assertNotNull(curse.getItemObject().getKnown(), curse.getName());
            assertSame(curseKind, curse.getItemObject().getKnown().getKind(), curse.getName());
            assertTrue(curse.getItemObject().getKnown().getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED),
                    curse.getName());
        }
    }

    @Test
    @DisplayName("running loadArtifacts again, as birth does, neither duplicates the special kinds nor changes the counts")
    void secondArtifactRunIsIdempotent() {
        ObjectKind phialBefore = ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Phial");

        ObjectDataLoader.loadArtifacts();

        assertAll(
                () -> assertEquals(375 + 20 + 14, ObjectRegistry.getObjectKindCount()),
                () -> assertEquals(138, ObjectRegistry.getArtifacts().size()),
                () -> assertEquals(138, ObjectRegistry.getArtifactKindMax()),
                () -> assertSame(phialBefore, ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Phial")),
                () -> assertEquals(375, ObjectRegistry.getObjectBaseKindMax()));
    }
}
