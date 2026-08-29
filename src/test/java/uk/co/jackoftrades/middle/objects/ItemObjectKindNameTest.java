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
import uk.co.jackoftrades.channel.colour.ColourEnum;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@code ItemObject.objectKindName(ObjectKind, boolean)} — the port of C's
 * {@code object_kind_name} ({@code src/obj-desc.c:48}).
 *
 * <p>The C is a two-way branch: an unaware flavoured kind answers with the raw flavour text,
 * copied verbatim by {@code my_strcpy}; everything else answers with the kind's name run through
 * {@code obj_desc_name_format} with a null modifier and {@code pluralise} false. Every expected
 * value below was worked out from that C, not from the Java, and the templates are the real
 * {@code name:} lines of {@code object.txt} and {@code object_base.txt} with the flavour texts of
 * {@code flavor.txt}.
 *
 * <p>The two arms of the branch cross at three inputs — {@code easyKnow}, the kind's awareness and
 * whether the kind has a flavour at all — so {@link Branch} walks all of them. {@link Formatting}
 * covers what the singular, unmodified format call does to a real template, and
 * {@link FlavourIsNotFormatted} pins the asymmetry the C creates by copying the flavour text
 * rather than formatting it.
 *
 * <p>The method is private and the kind's name and flavour have no setters, so both are reached
 * by reflection.
 *
 * @author Rowan Crowther
 */
@DisplayName("ItemObject.objectKindName")
class ItemObjectKindNameTest {

    /**
     * The item the method is invoked on. The method reads no instance state, so a blank one does.
     */
    private ItemObject item;

    /**
     * The private method under test, made reachable once.
     */
    private Method objectKindName;

    /**
     * A blank item and a reachable handle on the method.
     *
     * @throws Exception if the method cannot be reached
     */
    @BeforeEach
    void newItem() throws Exception {
        item = new ItemObject();
        objectKindName = ItemObject.class.getDeclaredMethod(
                "objectKindName", ObjectKind.class, boolean.class);
        objectKindName.setAccessible(true);
    }

    /**
     * Builds a kind carrying just the state the method reads.
     *
     * @param name    the kind's name template
     * @param flavour the flavour text, or {@code null} for an unflavoured kind
     * @param aware   whether the player is aware of the kind
     * @return the kind
     * @throws Exception if the fields cannot be reached
     */
    private ObjectKind kind(String name, String flavour, boolean aware) throws Exception {
        ObjectKind kind = new ObjectKind();

        Field nameField = ObjectKind.class.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(kind, name);

        if (flavour != null) {
            Field flavourField = ObjectKind.class.getDeclaredField("flavour");
            flavourField.setAccessible(true);
            flavourField.set(kind, new Flavour(flavour, ColourEnum.COLOUR_WHITE, 1));
        }

        kind.setAware(aware);
        return kind;
    }

    /**
     * Calls the method under test.
     *
     * @param kind     the kind to name
     * @param easyKnow whether to force the identified name
     * @return the name the method produces
     * @throws Exception if the call fails
     */
    private String name(ObjectKind kind, boolean easyKnow) throws Exception {
        return (String) objectKindName.invoke(item, kind, easyKnow);
    }

    /**
     * The three inputs the C branch reads, in all the combinations that matter.
     */
    @Nested
    @DisplayName("branch")
    class Branch {

        /**
         * The only arm that yields the flavour: unaware, flavoured, and not easy-known. C's
         * {@code !easy_know && !kind->aware && kind->flavor} is true, so {@code my_strcpy} copies
         * the flavour text.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("unaware flavoured kind answers with the flavour")
        void unawareFlavoured() throws Exception {
            assertEquals("Azure", name(kind("Intelligence", "Azure", false), false));
        }

        /**
         * {@code easy_know} short-circuits the flavour arm, so the caller that already knows the
         * kind gets the identified name even while the player does not.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("easyKnow forces the identified name")
        void unawareFlavouredEasyKnow() throws Exception {
            assertEquals("Intelligence", name(kind("Intelligence", "Azure", false), true));
        }

        /**
         * Awareness alone is enough to reach the identified name.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("aware flavoured kind answers with the name")
        void awareFlavoured() throws Exception {
            assertEquals("Intelligence", name(kind("Intelligence", "Azure", true), false));
        }

        /**
         * An unflavoured kind has nothing to hide behind, so even unaware it answers with its
         * name. C's third conjunct, {@code kind->flavor}, is the guard.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("unaware unflavoured kind answers with the name")
        void unawareUnflavoured() throws Exception {
            assertEquals("Wooden Torch", name(kind("& Wooden Torch~", null, false), false));
        }

        /**
         * The ordinary case for equipment: aware, unflavoured, not easy-known.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("aware unflavoured kind answers with the name")
        void awareUnflavoured() throws Exception {
            assertEquals("Wooden Torch", name(kind("& Wooden Torch~", null, true), true));
        }
    }

    /**
     * What the name arm's format call — null modifier, singular — does to a template.
     */
    @Nested
    @DisplayName("formatting")
    class Formatting {

        /**
         * {@code &} and the space after it are dropped: the article is chosen further out by the
         * quantity prefix, which this method never adds.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("the article marker is stripped")
        void articleStripped() throws Exception {
            assertEquals("Flask of oil", name(kind("& Flask~ of oil", null, true), false));
        }

        /**
         * {@code pluralise} is hard-wired false, so C skips each {@code ~} without emitting
         * anything. A base-type template that would read "Bolts" in a pile reads "Bolt" here.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("tildes are dropped, never pluralised")
        void tildeDropped() throws Exception {
            assertEquals("Bolt", name(kind("Bolt~", null, true), false));
        }

        /**
         * {@code |x|y|} takes its singular arm for the same reason, so the hard-coded staff
         * basename reads "Staff" and not "Staves".
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("bar groups take the singular arm")
        void barsSingular() throws Exception {
            assertEquals("Staff", name(kind("Sta|ff|ves|", null, true), false));
        }

        /**
         * The modifier is passed as null, so C's {@code *fmt == '#' && modstr} fails and the
         * {@code #} is copied through as an ordinary character. Nothing in {@code object.txt}
         * puts a {@code #} in a kind name, but the semantics are the C's.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("a hash is left in place, having no modifier")
        void hashLeftInPlace() throws Exception {
            assertEquals("# Amulet", name(kind("& # Amulet~", null, true), false));
        }
    }

    /**
     * The flavour arm copies; the name arm formats. Anything that looks like a formatting
     * character survives in a flavour and does not in a name.
     */
    @Nested
    @DisplayName("the flavour is copied, not formatted")
    class FlavourIsNotFormatted {

        /**
         * C reaches the flavour through {@code my_strcpy}, which never sees
         * {@code obj_desc_name_format}. A flavour text carrying {@code &} and {@code ~} therefore
         * comes back with both still in it, where the same characters in a name would be stripped.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("formatting characters survive in a flavour")
        void flavourVerbatim() throws Exception {
            assertEquals("& Azure~", name(kind("Intelligence", "& Azure~", false), false));
        }

        /**
         * The same text as a name is formatted, which is the contrast that makes the point.
         *
         * @throws Exception if the call fails
         */
        @Test
        @DisplayName("the same text as a name is formatted")
        void nameFormatted() throws Exception {
            assertEquals("Azure", name(kind("& Azure~", null, true), false));
        }
    }
}
