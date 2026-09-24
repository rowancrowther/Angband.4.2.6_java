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

package uk.co.jackoftradesltd.frontend.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;
import uk.co.jackoftradesltd.frontend.entries.UIEntryIterator;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link UIEntryCode}, the port of C's {@code initialize_ui_entry_iterator} and its private
 * comparator {@code cmp_desc_prio} ({@code [C] ui-entry.c:392-483}).
 *
 * <p>Expected orderings are derived from {@code cmp_desc_prio} directly: an entry not carrying the
 * sort category is pushed to the end regardless of name, two entries both carrying it sort by
 * descending priority with name as the tiebreak, and two entries neither carrying it (or no sort
 * category at all) sort by name alone.
 *
 * <p>Class UIEntryCodeTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
class UIEntryCodeTest {

    private static Flag<ChannelEntryFlag> noFlags() {
        return new Flag<>(ChannelEntryFlag.class);
    }

    private static Flag<ChannelEntryFlag> templateOnlyFlag() {
        Flag<ChannelEntryFlag> flag = new Flag<>(ChannelEntryFlag.class);
        flag.on(ChannelEntryFlag.ENTRY_FLAG_TEMPLATE_ONLY);
        return flag;
    }

    private static UIEntry entry(String name, Flag<ChannelEntryFlag> flags, UIEntryCategory... categories) {
        return new UIEntry(name, null, ElementEnum.ELEM_NONE, StatElemType.NONE, null, CombinerName.NONE,
                List.of(categories), 0, "", flags, "test", "", "", "");
    }

    @BeforeEach
    void resetRegistry() {
        UIRegistry.setUIEntries(List.of());
    }

    @Nested
    @DisplayName("initialiseUIEntryIterator")
    class InitialiseUIEntryIterator {

        @Test
        @DisplayName("excludes template-only entries even when the predicate accepts them")
        void excludesTemplateOnlyEntries() {
            UIEntry real = entry("real", noFlags());
            UIEntry template = entry("template", templateOnlyFlag());
            UIRegistry.setUIEntries(List.of(real, template));

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator((closure, e) -> true, new String[0], "");

            assertEquals(1, it.getEntries().size());
            assertEquals("real", it.getEntries().getFirst().getName());
        }

        @Test
        @DisplayName("excludes entries the predicate rejects")
        void excludesEntriesThePredicateRejects() {
            UIEntry accepted = entry("accepted", noFlags());
            UIEntry rejected = entry("rejected", noFlags());
            UIRegistry.setUIEntries(List.of(accepted, rejected));

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator(
                    (closure, e) -> e.getName().equals("accepted"), new String[0], "");

            assertEquals(1, it.getEntries().size());
            assertEquals("accepted", it.getEntries().getFirst().getName());
        }

        @Test
        @DisplayName("with no sort category, orders entries by name")
        void ordersByNameWithNoSortCategory() {
            UIEntry b = entry("b", noFlags());
            UIEntry a = entry("a", noFlags());
            UIRegistry.setUIEntries(List.of(b, a));

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator((closure, e) -> true, new String[0], "");

            assertEquals(List.of("a", "b"), it.getEntries().stream().map(UIEntry::getName).toList());
        }

        @Test
        @DisplayName("orders entries carrying the sort category by descending priority")
        void ordersByDescendingCategoryPriority() {
            UIEntry low = entry("low", noFlags(), new UIEntryCategory("cat", 1, true));
            UIEntry high = entry("high", noFlags(), new UIEntryCategory("cat", 5, true));
            UIRegistry.setUIEntries(List.of(low, high));

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator((closure, e) -> true, new String[0], "cat");

            assertEquals(List.of("high", "low"), it.getEntries().stream().map(UIEntry::getName).toList());
        }

        @Test
        @DisplayName("an entry carrying the sort category always sorts before one that does not")
        void categoryMemberSortsBeforeNonMember() {
            UIEntry member = entry("z", noFlags(), new UIEntryCategory("cat", 0, true));
            UIEntry nonMember = entry("a", noFlags());
            UIRegistry.setUIEntries(List.of(nonMember, member));

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator((closure, e) -> true, new String[0], "cat");

            assertEquals(List.of("z", "a"), it.getEntries().stream().map(UIEntry::getName).toList());
        }

        @Test
        @DisplayName("two entries tied on category priority fall back to name order")
        void tiedPriorityFallsBackToName() {
            UIEntry b = entry("b", noFlags(), new UIEntryCategory("cat", 3, true));
            UIEntry a = entry("a", noFlags(), new UIEntryCategory("cat", 3, true));
            UIRegistry.setUIEntries(List.of(b, a));

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator((closure, e) -> true, new String[0], "cat");

            assertEquals(List.of("a", "b"), it.getEntries().stream().map(UIEntry::getName).toList());
        }

        @Test
        @DisplayName("two entries neither carrying the sort category fall back to name order")
        void neitherCarryingCategoryFallsBackToName() {
            UIEntry b = entry("b", noFlags());
            UIEntry a = entry("a", noFlags());
            UIRegistry.setUIEntries(List.of(b, a));

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator((closure, e) -> true, new String[0], "cat");

            assertEquals(List.of("a", "b"), it.getEntries().stream().map(UIEntry::getName).toList());
        }

        @Test
        @DisplayName("the closure array is passed through to the predicate unchanged")
        void closureIsPassedThroughToThePredicate() {
            UIEntry only = entry("only", noFlags());
            UIRegistry.setUIEntries(List.of(only));
            String[] closure = {"needle"};

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator(
                    (c, e) -> c.length == 1 && c[0].equals("needle"), closure, "");

            assertTrue(it.getEntries().contains(only));
        }

        @Test
        @DisplayName("an empty registry produces an empty iterator")
        void emptyRegistryProducesEmptyIterator() {
            UIRegistry.setUIEntries(List.of());

            UIEntryIterator it = UIEntryCode.initialiseUIEntryIterator((closure, e) -> true, new String[0], "");

            assertFalse(it.getEntries().iterator().hasNext());
        }
    }
}
