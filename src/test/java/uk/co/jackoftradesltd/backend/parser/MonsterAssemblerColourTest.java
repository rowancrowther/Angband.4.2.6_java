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
package uk.co.jackoftradesltd.backend.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pins the {@code color:} line of monster.txt end to end (lexer {@code COLOUR_MODE}, grammar
 * {@code colour} rule, {@code MonsterAssembler}) against C's {@code parse_monster_color}
 * ({@code mon-init.c}): a value of more than one character goes through {@code color_text_to_attr}
 * (case-insensitive name; an unknown name is white by C's comment, though the loop walks off the 28 named rows into a NULL name, so C's real behaviour is undefined and is not pinned here); a single character goes through
 * {@code color_char_to_attr} (case-sensitive index character, space means dark, unknown means white).
 * Expected values are worked out by hand from the C colour table, not from the Java.
 *
 * @author Rowan Crowther
 */
class MonsterAssemblerColourTest {

    @TempDir
    Path tempDir;

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
    static void cleanup() throws Exception {
        ObjectRegistry.reset();
    }

    private ColourEnum colourOf(String colourLines) throws Exception {
        Path file = tempDir.resolve("c" + System.nanoTime() + ".txt");
        Files.writeString(file, "name:Probe\nbase:townsfolk\nglyph:t\n" + colourLines);
        ParseResult<MonsterRace> r = new MonsterReader().parseWithResults(file.toString());
        MonsterRace race = r.items().stream().filter(m -> "Probe".equals(m.getName())).findFirst().orElseThrow();
        Field f = MonsterRace.class.getDeclaredField("adc");
        f.setAccessible(true);
        return ((AngbandDisplayCharacter) f.get(race)).getAttributeColour();
    }

    @Test
    void aSingleCharacterIsCaseSensitive() throws Exception {
        assertEquals(ColourEnum.COLOUR_RED, colourOf("color:r\n"));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, colourOf("color:R\n"));
        assertEquals(ColourEnum.COLOUR_WHITE, colourOf("color:w\n"));
        assertEquals(ColourEnum.COLOUR_LIGHT_SLATE, colourOf("color:W\n"));
        assertEquals(ColourEnum.COLOUR_VIOLET, colourOf("color:v\n"));
        assertEquals(ColourEnum.COLOUR_LIGHT_VIOLET, colourOf("color:V\n"));
    }

    @Test
    void aNameOfSeveralCharactersIsCaseInsensitive() throws Exception {
        assertEquals(ColourEnum.COLOUR_WHITE, colourOf("color:white\n"));
        assertEquals(ColourEnum.COLOUR_WHITE, colourOf("color:WHITE\n"));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, colourOf("color:Light Red\n"));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, colourOf("color:light red\n"));
    }

    @Test
    void anUnknownSingleCharacterIsWhite() throws Exception {
        assertEquals(ColourEnum.COLOUR_WHITE, colourOf("color:q\n"));
    }

    @Test
    void anUnknownNameIsWhite() throws Exception {
        assertEquals(ColourEnum.COLOUR_WHITE, colourOf("color:nonsense\n"));
    }

    @Test
    void theLastColourLineWins() throws Exception {
        assertEquals(ColourEnum.COLOUR_BLUE, colourOf("color:r\ncolor:b\n"));
    }

    @Test
    void aWindowsLineEndingDoesNotReachTheColour() throws Exception {
        assertEquals(ColourEnum.COLOUR_UMBER, colourOf("color:u\r\n"));
    }

    @Test
    void aColourLineFollowedByAnotherDirectiveStillParses() throws Exception {
        assertEquals(ColourEnum.COLOUR_GREEN, colourOf("color:g\nspeed:110\n"));
    }

    @Test
    void anEmptyColourLineIsRefused() {
        assertThrows(ParseCancellationException.class, () -> colourOf("color:\n"));
    }

    @Test
    void aSingleSpaceIsDark() throws Exception {
        assertEquals(ColourEnum.COLOUR_DARK, colourOf("color: \n"));
    }
}
