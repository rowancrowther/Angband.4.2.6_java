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
import uk.co.jackoftradesltd.middle.objects.Artifact;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pins the level of the kinds {@code ArtifactAssembler} writes for special artifacts against C.
 *
 * <p>C's {@code parse_artifact_base_object} calls {@code write_dummy_object_record} while it reads
 * the {@code base-object:} line, and that copies {@code art->level} as it stands then. Every one of
 * the 14 such records in {@code artifact.txt} has its {@code level:} line after
 * {@code base-object:}, so in C each special kind's level is 0. A second run at birth finds the kind
 * and leaves its level alone. The artifact keeps its own {@code level:} value, and an ordinary kind
 * keeps its {@code object.txt} level ({@code & Lantern~} is 3, {@code & Long Sword~} is 10).
 *
 * <p>{@code ObjectKind} has no level getter, so the field is read by reflection.
 *
 * <p>Class SpecialKindLevelTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class SpecialKindLevelTest {

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

    private static int levelOf(ObjectKind kind) throws ReflectiveOperationException {
        Field f = ObjectKind.class.getDeclaredField("level");
        f.setAccessible(true);
        return f.getInt(kind);
    }

    private static List<ObjectKind> specialKinds() {
        return ObjectRegistry.getObjectKinds().stream()
                .filter(ObjectKind::isSpecialArtifactKind)
                .toList();
    }

    private static Artifact artifact(String name) {
        return ObjectRegistry.getArtifacts().stream()
                .filter(a -> name.equals(a.getName()))
                .findFirst().orElseThrow();
    }

    @Test
    @DisplayName("every special artifact kind has level 0, as C's write_dummy_object_record leaves it")
    void specialKindsAreLevelZero() throws ReflectiveOperationException {
        List<ObjectKind> kinds = specialKinds();
        assertEquals(14, kinds.size());
        for (ObjectKind k : kinds)
            assertEquals(0, levelOf(k), k.getName());
    }

    @Test
    @DisplayName("the three special lights' kinds are level 0 while their artifacts keep 5, 30 and 50")
    void lightsKindVersusArtifact() throws ReflectiveOperationException {
        assertAll(
                () -> assertEquals(0, levelOf(ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Phial"))),
                () -> assertEquals(0, levelOf(ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Star"))),
                () -> assertEquals(0, levelOf(ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Arkenstone"))),
                () -> assertEquals(5, artifact("of Galadriel").getLevel()),
                () -> assertEquals(30, artifact("of Elendil").getLevel()),
                () -> assertEquals(50, artifact("of Thráin").getLevel()));
    }

    @Test
    @DisplayName("ordinary kinds that artifacts are built on keep their object.txt level")
    void ordinaryKindsUntouched() throws ReflectiveOperationException {
        assertAll(
                () -> assertEquals(3, levelOf(ObjectRegistry.lookupObjectKind(TValue.TV_LIGHT, "Lantern"))),
                () -> assertEquals(10, levelOf(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "Long Sword"))));
    }

    @Test
    @DisplayName("a second artifact run, as at birth, leaves the special kinds at level 0")
    void secondRunKeepsZero() throws ReflectiveOperationException {
        ObjectDataLoader.loadArtifacts();
        List<ObjectKind> kinds = specialKinds();
        assertEquals(14, kinds.size());
        for (ObjectKind k : kinds)
            assertEquals(0, levelOf(k), k.getName());
    }
}
