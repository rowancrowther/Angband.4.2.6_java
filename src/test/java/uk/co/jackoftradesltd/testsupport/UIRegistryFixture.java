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

package uk.co.jackoftradesltd.testsupport;

import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Puts the list of UI entry names that {@code ObjectProperty2Assembler.parseBindui} checks into a
 * known state for tests that read {@code object_property.txt}.
 *
 * <p>{@code parseBindui} asks {@link GameConstants#uiEntryExists} whether a {@code bindui:} name
 * belongs to a UI entry, as C's {@code bind_object_property_to_ui_entry_by_name} does with
 * {@code ui_entry_search}: an unknown name drops the bind, and an unset
 * {@link GameConstants#entries} throws. In the game {@code GameConstants.init} fills that list from
 * the front end's {@code UIEntriesLoaded} message. A test that expects a binding therefore has to
 * fill it, and, if it uses a made-up entry name, add that name with {@link #addEntries(String...)}.
 *
 * <p>The real names come from the front end: this loads the shipped renderers, entry bases and entries
 * into {@link UIRegistry} and turns each {@link UIEntry} into a {@link UIEntrySpec}, as
 * {@code ObjectDataLoaderRegistryTest} does before it calls {@code GameConstants.init}. A test
 * reaching into the front end is allowed here by decision of 261116, because the names are the
 * shipped ones and not a second copy; it is not a pattern for main code.
 *
 * <p>The usual use is {@code saved = UIRegistryFixture.loadReal()} in a {@code @BeforeAll}, then
 * {@code UIRegistryFixture.restore(saved)} in an {@code @AfterAll}, so the next test class finds both
 * statics as they were. Both are static, so every test class that reads a {@code bindui:} line
 * through the assembler has to do this.
 *
 * <p>Class UIRegistryFixture coded on 261116, reworked on 261116 for {@code GameConstants.entries}.
 */
public final class UIRegistryFixture {

    private UIRegistryFixture() {
    }

    /**
     * The statics as a test found them, to be handed back to {@link #restore}.
     *
     * @param renderers   the {@code UIRegistry.uiEntryRenderers} field before the load, or {@code null}
     * @param entries     the {@code UIRegistry.uiEntries} field before the load, or {@code null}
     * @param gameEntries {@link GameConstants#entries} before the load, or {@code null}
     */
    public record Saved(Object renderers, Object entries, List<UIEntrySpec> gameEntries) {
    }

    /**
     * Loads the shipped renderers, entry bases and entries into {@link UIRegistry}, in the order the
     * loaders need, and sets {@link GameConstants#entries} to one spec per entry.
     *
     * @return the state the statics held before, for {@link #restore}
     * @throws IOException if a data file cannot be read
     */
    public static Saved loadReal() throws IOException {
        Saved saved = new Saved(
                ItemFixture.setStatic(UIRegistry.class, "uiEntryRenderers", null),
                ItemFixture.setStatic(UIRegistry.class, "uiEntries", null),
                GameConstants.entries);
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> specs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            specs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }
        GameConstants.entries = specs;
        return saved;
    }

    /**
     * Appends one bare spec for each name to {@link GameConstants#entries}, so a {@code bindui:}
     * naming it finds an entry. The specs carry no combiner and no flags: only the name is ever read.
     * Call this after {@link #loadReal()}.
     *
     * @param names the entry names to add, tag included, such as {@code "face<FACE>"}
     */
    public static void addEntries(String... names) {
        List<UIEntrySpec> all = new ArrayList<>(GameConstants.entries);
        for (String name : names) {
            all.add(new UIEntrySpec(name, CombinerName.NONE, new Flag<>(ChannelEntryFlag.class)));
        }
        GameConstants.entries = all;
    }

    /**
     * Puts both statics back as {@link #loadReal()} found them.
     *
     * @param saved the value {@link #loadReal()} returned
     */
    public static void restore(Saved saved) {
        ItemFixture.setStatic(UIRegistry.class, "uiEntryRenderers", saved.renderers());
        ItemFixture.setStatic(UIRegistry.class, "uiEntries", saved.entries());
        GameConstants.entries = saved.gameEntries();
    }
}
