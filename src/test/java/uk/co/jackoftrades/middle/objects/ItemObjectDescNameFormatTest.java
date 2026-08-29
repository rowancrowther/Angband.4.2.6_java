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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@code ItemObject.objDescNameFormat(String, String, boolean)} — the port of C's
 * {@code obj_desc_name_format} ({@code src/obj-desc.c:231}).
 *
 * <p>Every expected value below was worked out from the C, by hand and by walking the C's
 * single left-to-right pass over each template; none was taken from the Java. The templates
 * themselves are the real ones — the {@code name:} lines of {@code object.txt} and
 * {@code object_base.txt}, and the hard-coded basenames returned by C's
 * {@code obj_desc_get_basename} ({@code obj-desc.c:80}) — so the ordinary cases are the strings
 * the game actually formats.
 *
 * <p>The port rewrites an immutable string in passes where C copies bytes once, left to right.
 * That is deliberate, and the four places it shows are covered in {@link Divergences} with the
 * C behaviour named in each test, so that anyone reading a failure can see which of the two is
 * being asserted.
 *
 * <p>The method is private, so it is reached by reflection.
 *
 * @author Rowan Crowther
 */
@DisplayName("ItemObject.objDescNameFormat")
class ItemObjectDescNameFormatTest {

    /**
     * The item the method is invoked on. The method reads no instance state, so a blank one does.
     */
    private ItemObject item;

    /**
     * The private method under test, made reachable once.
     */
    private Method objDescNameFormat;

    /**
     * A blank item and a reachable handle on the method.
     *
     * @throws Exception if the method cannot be reached
     */
    @BeforeEach
    void newItem() throws Exception {
        item = new ItemObject();
        objDescNameFormat = ItemObject.class.getDeclaredMethod(
                "objDescNameFormat", String.class, String.class, boolean.class);
        objDescNameFormat.setAccessible(true);
    }

    /**
     * Calls the method under test.
     *
     * @param template  the name template
     * @param modString the text to substitute for {@code #}, or {@code null}
     * @param pluralise whether to take plural forms
     * @return what the method returned
     * @throws Exception if the call fails
     */
    private String format(String template, String modString, boolean pluralise) throws Exception {
        return (String) objDescNameFormat.invoke(item, template, modString, pluralise);
    }

    /**
     * The {@code &} that stands in for the article. C skips a run of {@code ' '} and {@code '&'}
     * from the ampersand onwards ({@code obj-desc.c:236}), leaving the article to be chosen later
     * by the quantity prefix.
     */
    @Nested
    @DisplayName("the & marker")
    class Ampersand {

        @Test
        @DisplayName("a leading & and its space go, on a real template")
        void leadingAmpersandGoes() throws Exception {
            assertEquals("Ring", format("& Ring~", null, false));
        }

        @Test
        @DisplayName("an & mid-template takes the space after it, not the one before")
        void midTemplateAmpersand() throws Exception {
            assertEquals("ab", format("a& b", null, false));
        }

        @Test
        @DisplayName("a run of ampersands and spaces is skipped whole")
        void runOfAmpersands() throws Exception {
            assertEquals("x", format("&&  &x", null, false));
        }

        @Test
        @DisplayName("a template with no & is untouched")
        void noAmpersand() throws Exception {
            assertEquals("Adamantite Plate Mail", format("Adamantite Plate Mail~", null, false));
        }
    }

    /**
     * The {@code ~} pluraliser. C appends {@code es} after {@code s}, {@code h} or {@code x} and
     * {@code s} otherwise ({@code obj-desc.c:250}), and drops the {@code ~} entirely when the
     * caller did not ask for a plural ({@code obj-desc.c:245}).
     */
    @Nested
    @DisplayName("the ~ pluraliser")
    class Tilde {

        @Test
        @DisplayName("singular drops the ~")
        void singularDropsTilde() throws Exception {
            assertEquals("Flask of oil", format("& Flask~ of oil", null, false));
        }

        @Test
        @DisplayName("plural adds s after an ordinary letter")
        void pluralAddsS() throws Exception {
            assertEquals("Flasks of oil", format("& Flask~ of oil", null, true));
        }

        @Test
        @DisplayName("plural adds es after s")
        void pluralAddsEsAfterS() throws Exception {
            assertEquals("Cutlasses", format("& Cutlass~", null, true));
        }

        @Test
        @DisplayName("plural adds es after h")
        void pluralAddsEsAfterH() throws Exception {
            assertEquals("Wooden Torches", format("& Wooden Torch~", null, true));
        }

        @Test
        @DisplayName("plural adds es after x")
        void pluralAddsEsAfterX() throws Exception {
            assertEquals("boxes", format("box~", null, true));
        }

        @Test
        @DisplayName("a ~ inside a word still pluralises there, singular just removes it")
        void tildeMidWord() throws Exception {
            assertEquals("boxes", format("box~es", null, false));
            assertEquals("boxeses", format("box~es", null, true));
        }

        @Test
        @DisplayName("a template with no ~ has no plural form")
        void foodHasNoPlural() throws Exception {
            assertEquals("Food", format("Food", null, false));
            assertEquals("Food", format("Food", null, true));
        }
    }

    /**
     * The {@code |singular|plural|} marker. C emits the first field when singular and the second
     * when plural, then resumes at the closing bar ({@code obj-desc.c:253–276}).
     */
    @Nested
    @DisplayName("the |x|y| marker")
    class Bars {

        @Test
        @DisplayName("staff and staves, the real template")
        void staffAndStaves() throws Exception {
            assertEquals("Staff", format("& Sta|ff|ves|", null, false));
            assertEquals("Staves", format("& Sta|ff|ves|", null, true));
        }

        @Test
        @DisplayName("text after the closing bar is kept")
        void tailAfterClosingBarKept() throws Exception {
            assertEquals("ac", format("|a|b|c", null, false));
            assertEquals("bc", format("|a|b|c", null, true));
        }

        @Test
        @DisplayName("two triples in one template are both taken")
        void twoTriples() throws Exception {
            assertEquals("abde", format("a|b|c|d|e|f|", null, false));
            assertEquals("acdf", format("a|b|c|d|e|f|", null, true));
        }

        @Test
        @DisplayName("a ~ after a closing bar pluralises against the bar, giving a double plural")
        void tildeAfterBars() throws Exception {
            assertEquals("knife", format("kni|fe|ves|~", null, false));
            assertEquals("knivess", format("kni|fe|ves|~", null, true));
        }
    }

    /**
     * The {@code #} modifier. C substitutes {@code modstr} by recursing with the same
     * pluralisation and no modifier of its own ({@code obj-desc.c:276–279}), and copies the
     * {@code #} through as a literal when there is no modifier to put there.
     */
    @Nested
    @DisplayName("the # modifier")
    class Modifier {

        @Test
        @DisplayName("a flavour goes in where the # is")
        void flavourSubstituted() throws Exception {
            assertEquals("Copper Ring", format("& # Ring~", "Copper", false));
            assertEquals("Copper Rings", format("& # Ring~", "Copper", true));
        }

        @Test
        @DisplayName("the modifier and the surrounding template both format")
        void modifierAndTemplateBothFormat() throws Exception {
            assertEquals("Aluminium Staff", format("& # Sta|ff|ves|", "Aluminium", false));
            assertEquals("Aluminium Staves", format("& # Sta|ff|ves|", "Aluminium", true));
        }

        @Test
        @DisplayName("a # at the end of the template, as scrolls and books have it")
        void trailingModifier() throws Exception {
            assertEquals("Scrolls titled xyzzy fum",
                    format("& Scroll~ titled #", "xyzzy fum", true));
            assertEquals("Books of Magic Spells Magic for Beginners",
                    format("& Book~ of Magic Spells #", "Magic for Beginners", true));
        }

        @Test
        @DisplayName("a modifier carrying its own |x|y| is formatted before it goes in")
        void modifierWithOwnBars() throws Exception {
            assertEquals("knifea", format("#|a|b|", "kni|fe|ves|", false));
            assertEquals("knivesb", format("#|a|b|", "kni|fe|ves|", true));
        }

        @Test
        @DisplayName("with no modifier the # is copied through as a literal")
        void nullModifierLeavesHash() throws Exception {
            assertEquals("# Ring", format("& # Ring~", null, false));
            assertEquals("# Rings", format("& # Ring~", null, true));
        }
    }

    /**
     * The four places the port parts company with the C. None is reachable from the shipped game
     * data; each test asserts the port's behaviour and names the C's, so a failure says which of
     * the two has moved.
     */
    @Nested
    @DisplayName("deliberate divergences from C")
    class Divergences {

        @Test
        @DisplayName("a ~ straight after a # pluralises against the modifier, where C sees the #")
        void tildeAfterModifier() throws Exception {
            // C reads *(fmt - 1) in the outer template, which is '#', and so appends a bare "s",
            // giving "Fishs". The port substitutes first, so the tilde sees the 'h' of "Fish".
            assertEquals("Fishes", format("#~", "Fish", true));
        }

        @Test
        @DisplayName("a bar count that is not a multiple of three rejects the whole template")
        void badBarCountRejected() throws Exception {
            // C has no such check: it hits the unmatched bar, returns early, and so truncates to
            // the empty string. The port returns the template unformatted instead, bars and all.
            assertEquals("|a|b", format("|a|b", null, false));
            assertEquals("|a|b", format("|a|b", null, true));
        }

        @Test
        @DisplayName("a ~ with nothing before it returns the text formatted so far")
        void leadingTildeReported() throws Exception {
            // C reads the byte before the ~ unconditionally; at the first character of the
            // template that is off the front of the allocation, so C has no defined answer here.
            assertEquals("~ab", format("~ab", null, true));
        }

        @Test
        @DisplayName("a second ~ with nothing before it keeps what was formatted and stops")
        void doubledTildeKeepsProgress() throws Exception {
            // Here C is defined: the second ~ reads the first as its previous character and adds
            // a bare "s", giving "abss". The port stops at the second ~ and hands back "abs"
            // plus the unconsumed tail.
            assertEquals("abs~", format("ab~~", null, true));
        }

        @Test
        @DisplayName("an error exit hands the remaining markers back rather than swallowing them")
        void errorExitKeepsMarkers() throws Exception {
            // The bail-out happens before the bar pass, so the tail still carries its triple.
            assertEquals("as~|b|c|", format("a~~|b|c|", null, true));
        }
    }
}
