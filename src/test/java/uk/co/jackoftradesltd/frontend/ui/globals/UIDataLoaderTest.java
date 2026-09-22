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

package uk.co.jackoftradesltd.frontend.ui.globals;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.StatElemType;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryCategory;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the private finishing steps {@link UIDataLoader} still owns -
 * {@code finalPass} and {@code fillOutShortened} - checked against the C
 * original's whole-file finishing loop ({@code [C] ui-entry.c:2283-2332}).
 *
 * <p>The base-to-placeholder-entry conversion this class used to pin (C's
 * {@code run_parse_ui_entry} post-base-file {@code ENTRY_FLAG_TEMPLATE_ONLY}
 * pass, {@code [C] ui-entry.c:2263-2278}) has since moved onto
 * {@code UIEntryBaseAssembler.parseEachEntry}, which is what
 * {@code UIEntryBaseAssemblerTest} exercises now; there is no
 * {@code UIDataLoader.portUIEntryBasesToUIEntries()} left to test here.
 *
 * <p>{@code finalPass} and {@code fillOutShortened} are private and static, so
 * each test reaches them through reflection rather than the public
 * {@code loadUIEntries()}, which would otherwise require a real gamedata file
 * and a fully populated {@link UIRegistry} just to reach this logic.
 *
 * @author Rowan Crowther
 */
class UIDataLoaderTest {

    private static Method finalPassMethod() throws NoSuchMethodException {
        Method method = UIDataLoader.class.getDeclaredMethod("finalPass", List.class);
        method.setAccessible(true);
        return method;
    }

    private static Method fillOutShortenedMethod() throws NoSuchMethodException {
        Method method = UIDataLoader.class.getDeclaredMethod("fillOutShortened", UIEntry.class);
        method.setAccessible(true);
        return method;
    }

    @SuppressWarnings("unchecked")
    private static List<UIEntry> finalPass(List<UIEntry> entries) throws Exception {
        return (List<UIEntry>) finalPassMethod().invoke(null, entries);
    }

    private static void fillOutShortened(UIEntry entry) throws Exception {
        fillOutShortenedMethod().invoke(null, entry);
    }

    private static UIEntry entry(String name, String label, String label5, String label2,
                                 int priorityNum, List<UIEntryCategory> categories) {
        return new UIEntry(name, null, null, StatElemType.NONE, null, null, categories,
                priorityNum, null, new Flag<>(ChannelEntryFlag.class), "d", label, label5, label2);
    }

    /**
     * The regression case for the bug {@code UIEntryAssembler}'s create path had: a category left
     * with {@code priority_set=false} (as every template-derived category now is - see
     * {@code UIEntryAssembler.java:358-364}) must still pick up the entry's real default priority
     * here, matching C's {@code if (! entries[i]->categories[j].priority_set)} backfill
     * ({@code [C] ui-entry.c:2324-2330}).
     */
    @Test
    void finalPassBackfillsAnUnsetCategoryPriorityFromTheEntrysDefault() throws Exception {
        UIEntryCategory unset = new UIEntryCategory("CHAR_SCREEN1", 0, false);
        UIEntry pblind = entry("pblind_ui_compact_0", "Blindess resistance", "pBlnd", null,
                -1, List.of(unset));

        finalPass(List.of(pblind));

        assertTrue(unset.isPrioritySet());
        assertEquals(-1, unset.getPriority(),
                "an unset category must take the entry's own default_priority, not be left at 0");
    }

    /**
     * The other half of the same C condition: a category whose priority was already explicitly set
     * (by its own {@code category:}/{@code priority:} pair) must survive {@code finalPass} untouched,
     * even when the entry's default priority differs.
     */
    @Test
    void finalPassLeavesAnAlreadySetCategoryPriorityUntouched() throws Exception {
        UIEntryCategory alreadySet = new UIEntryCategory("resistances", 7, true);
        UIEntry withOwnPriority = entry("t", "label", null, null, -1, List.of(alreadySet));

        finalPass(List.of(withOwnPriority));

        assertEquals(7, alreadySet.getPriority());
        assertTrue(alreadySet.isPrioritySet());
    }

    @Test
    void finalPassOnlyBackfillsTheCategoriesThatAreUnset() throws Exception {
        UIEntryCategory unset = new UIEntryCategory("CHAR_SCREEN1", 0, false);
        UIEntryCategory alreadySet = new UIEntryCategory("abilities", 3, true);
        UIEntry mixed = entry("t", "label", null, null, -5, List.of(unset, alreadySet));

        finalPass(List.of(mixed));

        assertEquals(-5, unset.getPriority());
        assertEquals(3, alreadySet.getPriority(), "an already-set category must not be overwritten");
    }

    /**
     * Matches C's {@code if (entries[i]->nlabel == 0)} ({@code [C] ui-entry.c:2301}): an entry whose
     * data-file record never set {@code label:} falls back to its own name.
     */
    @Test
    void finalPassDefaultsAnEmptyLabelToTheEntrysName() throws Exception {
        UIEntry noLabel = entry("regen_ui_compact_0", "", null, null, 0, List.of());

        finalPass(List.of(noLabel));

        assertEquals("regen_ui_compact_0", noLabel.getLabel());
    }

    /**
     * {@code resist_ui_compact_0<POIS>} ({@code [C] lib/gamedata/ui_entry.txt}) sets {@code label5}
     * but no other shortened width, so every other width must cascade from whichever set width is
     * nearest, or the full label past the last one - matching {@code fill_out_shortened}'s search
     * ({@code [C] ui-entry.c:1740-1753}).
     */
    @Test
    void fillOutShortenedCascadesFromTheNearestLongerAbbreviationOrTheFullLabel() throws Exception {
        UIEntry poison = entry("resist_ui_compact_0<POIS>", "Poison", "Pois", null, 0, List.of());

        fillOutShortened(poison);

        assertEquals("P", poison.getShortenedLabel(0), "index 0 truncates the nearest source to 1 char");
        assertEquals("Po", poison.getShortenedLabel(1), "label2 (index 1) truncates \"Pois\" to 2 chars");
        assertEquals("Poi", poison.getShortenedLabel(2));
        assertEquals("Pois", poison.getShortenedLabel(3), "index 3 takes \"Pois\" whole, at its own width");
        assertEquals("Pois", poison.getShortenedLabel(4), "the explicitly-set label5 is left untouched");
        assertEquals("Poison", poison.getShortenedLabel(5),
                "past the last set width, the cascade falls back to the full label");
        assertEquals("Poison", poison.getShortenedLabel(9));
    }

    @Test
    void fillOutShortenedLeavesAnAlreadySetWidthUntouched() throws Exception {
        UIEntry entry = entry("t", "Label", null, "Lb", 0, List.of());

        fillOutShortened(entry);

        assertEquals("Lb", entry.getShortenedLabel(1), "an explicitly-set label2 must not be recomputed");
    }
}
