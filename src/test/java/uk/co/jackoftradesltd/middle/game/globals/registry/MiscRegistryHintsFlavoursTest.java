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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.*;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.game.Hint;
import uk.co.jackoftradesltd.middle.objects.Flavour;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the hint and flavour holders of {@code MiscRegistry}: {@code setHints} and
 * {@code getHints}, and {@code setFlavours} and {@code getFlavours}.
 *
 * <p><b>The expectations come from the C, not from the port.</b> C's {@code finish_parse_hints} and
 * {@code finish_parse_flavor} ({@code init.c}) do one thing each: assign the list the parser built
 * to the global ({@code hints} in {@code store.c}, {@code flavors} in {@code obj-util.c}). Nothing
 * is copied, filtered or checked, so what goes in is what comes out, a second assignment replaces the
 * first, and an empty file gives an empty list (C's {@code NULL}). Before either has run the global
 * holds nothing; the port signals that with a {@link NullPointerException} from the getter rather
 * than a quiet empty list, which is the one deliberate difference and is pinned here.
 *
 * <p>The registry's fields are private statics shared with every other test in the JVM, so each
 * test saves the three lists before it runs and puts them back afterwards, and the "not yet loaded"
 * cases clear the field by reflection.
 *
 * @author Rowan Crowther
 */
@DisplayName("MiscRegistry — hints and flavours")
class MiscRegistryHintsFlavoursTest {

    private List<Hint> savedHints;
    private List<FlavourKind> savedFlavours;

    @BeforeEach
    void saveRegistry() throws ReflectiveOperationException {
        savedHints = readField("hints");
        savedFlavours = readField("flavours");
    }

    @AfterEach
    void restoreRegistry() throws ReflectiveOperationException {
        writeField("hints", savedHints);
        writeField("flavours", savedFlavours);
    }

    @SuppressWarnings("unchecked")
    private static <T> T readField(String name) throws ReflectiveOperationException {
        Field field = MiscRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(null);
    }

    private static void writeField(String name, Object value) throws ReflectiveOperationException {
        Field field = MiscRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static FlavourKind kind(TValue tval, char glyph, String... texts) {
        List<Flavour> flavours = new ArrayList<>();
        int index = 1;
        for (String text : texts) {
            flavours.add(new Flavour(text, ColourEnum.COLOUR_WHITE, index++));
        }
        return new FlavourKind(tval, glyph, flavours);
    }

    @Nested
    @DisplayName("hints")
    class Hints {

        @Test
        @DisplayName("the stored hints come back, in the order given")
        void storedHintsComeBack() {
            Hint first = new Hint("Staves can be used even when confused and blinded.");
            Hint second = new Hint("Carry lots of Cure Critical Wounds potions.");
            MiscRegistry.setHints(List.of(first, second));

            assertEquals(List.of(first, second), MiscRegistry.getHints());
        }

        @Test
        @DisplayName("an empty hint file gives an empty list, C's NULL hints")
        void emptyListIsEmpty() {
            MiscRegistry.setHints(List.of());

            assertTrue(MiscRegistry.getHints().isEmpty());
        }

        @Test
        @DisplayName("a second load replaces the first rather than adding to it")
        void secondLoadReplaces() {
            Hint old = new Hint("old");
            Hint fresh = new Hint("fresh");
            MiscRegistry.setHints(List.of(old));
            MiscRegistry.setHints(List.of(fresh));

            assertEquals(List.of(fresh), MiscRegistry.getHints());
        }

        @Test
        @DisplayName("the list is stored by reference, as C assigns the parser's pointer")
        void listIsStoredByReference() {
            List<Hint> loaded = new ArrayList<>();
            MiscRegistry.setHints(loaded);

            Hint late = new Hint("added after the load");
            loaded.add(late);

            assertEquals(List.of(late), MiscRegistry.getHints());
        }

        @Test
        @DisplayName("the returned view is read-only")
        void viewIsReadOnly() {
            MiscRegistry.setHints(new ArrayList<>(List.of(new Hint("one"))));

            List<Hint> view = MiscRegistry.getHints();

            assertThrows(UnsupportedOperationException.class, () -> view.add(new Hint("two")));
            assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        }

        @Test
        @DisplayName("each hint is the very object that was stored, text untouched")
        void hintsAreNotCopied() {
            Hint hint = new Hint("Hints are kept exactly as written.");
            MiscRegistry.setHints(List.of(hint));

            assertSame(hint, MiscRegistry.getHints().get(0));
            assertEquals("Hints are kept exactly as written.", MiscRegistry.getHints().get(0).getHint());
        }

        @Test
        @DisplayName("asking before the loader has run throws rather than returning an empty list")
        void unloadedThrows() throws ReflectiveOperationException {
            writeField("hints", null);

            assertThrows(NullPointerException.class, MiscRegistry::getHints);
        }
    }

    @Nested
    @DisplayName("flavours")
    class Flavours {

        @Test
        @DisplayName("the stored kinds come back, in the order given")
        void storedKindsComeBack() {
            FlavourKind ring = kind(TValue.TV_RING, '=', "Ruby", "Jade");
            FlavourKind potion = kind(TValue.TV_POTION, '!', "Azure");
            MiscRegistry.setFlavours(List.of(ring, potion));

            assertEquals(List.of(ring, potion), MiscRegistry.getFlavours());
        }

        @Test
        @DisplayName("an empty flavour file gives an empty list")
        void emptyListIsEmpty() {
            MiscRegistry.setFlavours(List.of());

            assertTrue(MiscRegistry.getFlavours().isEmpty());
        }

        @Test
        @DisplayName("a second load replaces the first rather than adding to it")
        void secondLoadReplaces() {
            FlavourKind ring = kind(TValue.TV_RING, '=', "Ruby");
            FlavourKind wand = kind(TValue.TV_WAND, '-', "Iron");
            MiscRegistry.setFlavours(List.of(ring));
            MiscRegistry.setFlavours(List.of(wand));

            assertEquals(List.of(wand), MiscRegistry.getFlavours());
        }

        @Test
        @DisplayName("the list is stored by reference, as C assigns the parser's pointer")
        void listIsStoredByReference() {
            List<FlavourKind> loaded = new ArrayList<>();
            MiscRegistry.setFlavours(loaded);

            FlavourKind late = kind(TValue.TV_ROD, '-', "Tin");
            loaded.add(late);

            assertEquals(List.of(late), MiscRegistry.getFlavours());
        }

        @Test
        @DisplayName("the returned view is read-only")
        void viewIsReadOnly() {
            MiscRegistry.setFlavours(new ArrayList<>(List.of(kind(TValue.TV_RING, '=', "Ruby"))));

            List<FlavourKind> view = MiscRegistry.getFlavours();

            assertThrows(UnsupportedOperationException.class,
                    () -> view.add(kind(TValue.TV_WAND, '-', "Iron")));
            assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        }

        @Test
        @DisplayName("each kind keeps its tval, glyph and flavours, untouched by the registry")
        void kindsAreNotCopied() {
            FlavourKind ring = kind(TValue.TV_RING, '=', "Ruby", "Jade");
            MiscRegistry.setFlavours(List.of(ring));

            FlavourKind stored = MiscRegistry.getFlavours().get(0);

            assertSame(ring, stored);
            assertEquals(TValue.TV_RING, stored.getValue());
            assertEquals('=', stored.getGlyph());
            assertEquals(2, stored.getFlavours().size());
        }

        @Test
        @DisplayName("asking before the loader has run throws rather than returning an empty list")
        void unloadedThrows() throws ReflectiveOperationException {
            writeField("flavours", null);

            assertThrows(NullPointerException.class, MiscRegistry::getFlavours);
        }
    }

    @Nested
    @DisplayName("independence of the three lists")
    class Independence {

        @Test
        @DisplayName("loading hints leaves the flavours alone, and the reverse")
        void settersDoNotDisturbEachOther() {
            FlavourKind ring = kind(TValue.TV_RING, '=', "Ruby");
            Hint hint = new Hint("hint");
            MiscRegistry.setFlavours(List.of(ring));
            MiscRegistry.setHints(List.of(hint));

            assertEquals(List.of(ring), MiscRegistry.getFlavours());

            MiscRegistry.setFlavours(List.of());

            assertEquals(List.of(hint), MiscRegistry.getHints());
        }
    }
}
