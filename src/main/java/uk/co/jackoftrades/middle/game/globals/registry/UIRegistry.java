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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;

import java.util.Collections;
import java.util.List;

/**
 * Runtime holder for the UI-entry game data — the entry renderers, entry bases, and entries that
 * drive the character-screen and property displays — plus the name lookups used to resolve them.
 *
 * <p>This is the read side of the UI slice: it is populated once at startup by
 * {@link uk.co.jackoftrades.middle.game.globals.loaders.UIDataLoader} (renderers, then bases, then
 * entries) and thereafter only read. It was split out of {@code GameConstants} as one domain slice
 * of the loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class UIRegistry {
    private static final Logger logger = LogManager.getLogger();

    /**
     * The loaded UI-entry renderers, resolved by name via {@link #getUIEntryRenderer}.
     */
    private static List<UIEntryRenderer> uiEntryRenderers;
    /**
     * The loaded UI-entry bases, resolved by name via {@link #getUIEntryBase}.
     */
    private static List<UIEntryBase> uiEntryBases;
    /**
     * The loaded UI entries, resolved by name via {@link #getUIEntry}.
     */
    private static List<UIEntry> uiEntries;

    /**
     * @return an unmodifiable view of the loaded UI-entry renderers
     */
    public static List<UIEntryRenderer> getUIEntryRenderers() {
        return Collections.unmodifiableList(uiEntryRenderers);
    }

    /**
     * Stores the loaded UI-entry renderers; set once by {@code UIDataLoader}.
     */
    public static void setUIEntryRenderers(List<UIEntryRenderer> uiEntryRenderers) {
        UIRegistry.uiEntryRenderers = uiEntryRenderers;
    }

    /**
     * @return an unmodifiable view of the loaded UI-entry bases
     */
    public static List<UIEntryBase> getUIEntryBases() {
        return Collections.unmodifiableList(uiEntryBases);
    }

    /**
     * Stores the loaded UI-entry bases; set once by {@code UIDataLoader} (after renderers).
     */
    public static void setUIEntryBases(List<UIEntryBase> uiEntryBases) {
        UIRegistry.uiEntryBases = uiEntryBases;
    }

    /**
     * @return an unmodifiable view of the loaded UI entries
     */
    public static List<UIEntry> getUIEntries() {
        return Collections.unmodifiableList(uiEntries);
    }

    /**
     * Stores the loaded UI entries; set once by {@code UIDataLoader} (after bases and renderers).
     */
    public static void setUIEntries(List<UIEntry> uiEntries) {
        UIRegistry.uiEntries = uiEntries;
    }

    /**
     * Return a UIEntryRenderer from the list of all renderers by its name. Unlike the base and entry
     * lookups below — which throw when queried before load — this one collects its failures into the
     * supplied {@code errors} list and returns {@code null}, because it is called from the assembler's
     * collect-every-error load path rather than the running game.
     *
     * @param name   the name of the renderer we wish to obtain
     * @param errors the running error list to append to if the renderers are unloaded or no match is found
     * @return the renderer with the same name as {@code name}, or {@code null} if unloaded or not found
     */
    @Nullable
    public static UIEntryRenderer getUIEntryRenderer(@NotNull String name, @NotNull List<String> errors) {
        if (uiEntryRenderers == null) {
            errors.add("Invalid attempt to access uiEntryRenderers when it hasn't been initialized");
            return null;
        }

        UIEntryRenderer renderer = uiEntryRenderers.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (renderer == null) {
            errors.add("Invalid UIEntryRenderer name: " + name + " no records found");
            return null;
        }

        return renderer;
    }

    /**
     * Return a UIEntryBase from the arrayList of all bases by its name
     *
     * @param name the name of the base we are wanting to find
     * @return a reference to the base with the name equal to the incoming parameter, or null if no base is found
     * with that name
     */
    @Nullable
    @Contract("_ -> _")
    public static UIEntryBase getUIEntryBase(@NotNull String name) {
        if (uiEntryBases == null) {
            String message = "Invalid attempt to access UIEntryBase when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return uiEntryBases.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get a UIEntry from a string
     *
     * @param name the string representation of the UIEntry's name
     * @return a UIEntry
     */
    @Nullable
    @Contract("_ -> _")
    public static UIEntry getUIEntry(String name) {
        if (uiEntries == null) {
            String message = "Invalid attempt to access UIEntry when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return uiEntries.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
