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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryBase;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;

import java.util.Collections;
import java.util.List;

/**
 * Runtime holder for the UI-entry game data — the entry renderers, entry bases, and entries that
 * drive the character-screen and property displays — plus the name lookups used to resolve them.
 *
 * <p>This is the read side of the UI slice: it is populated once at startup by
 * {@link UIDataLoader} (renderers, then bases, then
 * entries) and thereafter only read. It was split out of {@code GameConstants} as one domain slice
 * of the loader/registry refactor.
 *
 * <p>Class UIRegistry coded before 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
public class UIRegistry {
    /**
     * The longest label text a UI entry can carry. The Java form of C's {@code MAX_ENTRY_LABEL}
     * ({@code [C] ui-entry.c:39}).
     *
     * <p>Field MAX_ENTRY_LABEL coded before 260924, commented in full on 260924.
     */
    public static final int MAX_ENTRY_LABEL = 80;

    // Globals
    /**
     * The number of shortened-label slots a UI entry carries. The Java form of C's
     * {@code MAX_SHORTENED} ({@code [C] ui-entry.c:95}).
     *
     * <p>Field MAX_SHORTENED coded before 260924, commented in full on 260924.
     */
    public static final int MAX_SHORTENED = 10;
    /**
     * Logger for the fatal, unloaded-registry states {@link #getUIEntryBase} and {@link #getUIEntry}
     * throw on. {@link #getUIEntryRenderer} has no matching log call, since its own unloaded case is
     * reported through {@code errors} instead - see that method's Javadoc.
     *
     * <p>Field logger coded before 260924, commented in full on 260924.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The loaded UI-entry renderers, resolved by name via {@link #getUIEntryRenderer}. Set once, by
     * {@code UIDataLoader}, before {@link #uiEntryBases} and {@link #uiEntries} are loaded.
     *
     * <p>Field uiEntryRenderers coded before 260924, commented in full on 260924.
     */
    private static List<UIEntryRenderer> uiEntryRenderers;
    /**
     * The loaded UI-entry bases, resolved by name via {@link #getUIEntryBase}. Set once, by
     * {@code UIDataLoader}, after {@link #uiEntryRenderers} and before {@link #uiEntries}.
     *
     * <p>Field uiEntryBases coded before 260924, commented in full on 260924.
     */
    private static List<UIEntryBase> uiEntryBases;
    /**
     * The loaded UI entries - the single shared list carrying both the {@code ui_entry_base.txt}
     * placeholders and the {@code ui_entry.txt} entries proper, per the 260919 one-registry decision
     * - resolved by name via {@link #getUIEntry}. Set once, by {@code UIDataLoader}, after both
     * {@link #uiEntryRenderers} and {@link #uiEntryBases}. The Java form of C's {@code entries[]}
     * array, searched by name via {@code ui_entry_search} ({@code [C] ui-entry.c:1165}).
     *
     * <p>Field uiEntries coded before 260924, commented in full on 260924.
     */
    private static List<UIEntry> uiEntries;

    /**
     * Returns an unmodifiable view of the loaded UI-entry renderers.
     *
     * <p>Function getUIEntryRenderers coded before 260924, commented in full on 260924.
     *
     * @return an unmodifiable view of the loaded UI-entry renderers
     */
    public static List<UIEntryRenderer> getUIEntryRenderers() {
        return Collections.unmodifiableList(uiEntryRenderers);
    }

    /**
     * Stores the loaded UI-entry renderers; set once by {@code UIDataLoader}.
     *
     * <p>Function setUIEntryRenderers coded before 260924, commented in full on 260924.
     *
     * @param uiEntryRenderers the loaded UI-entry renderers
     */
    public static void setUIEntryRenderers(List<UIEntryRenderer> uiEntryRenderers) {
        UIRegistry.uiEntryRenderers = uiEntryRenderers;
    }

    /**
     * Returns an unmodifiable view of the loaded UI-entry bases.
     *
     * <p>Function getUIEntryBases coded before 260924, commented in full on 260924.
     *
     * @return an unmodifiable view of the loaded UI-entry bases
     */
    public static List<UIEntryBase> getUIEntryBases() {
        return Collections.unmodifiableList(uiEntryBases);
    }

    /**
     * Stores the loaded UI-entry bases; set once by {@code UIDataLoader} (after renderers).
     *
     * <p>Function setUIEntryBases coded before 260924, commented in full on 260924.
     *
     * @param uiEntryBases the loaded UI-entry bases
     */
    public static void setUIEntryBases(List<UIEntryBase> uiEntryBases) {
        UIRegistry.uiEntryBases = uiEntryBases;
    }

    /**
     * Returns an unmodifiable view of the loaded UI entries.
     *
     * <p>Function getUIEntries coded before 260924, commented in full on 260924.
     *
     * @return an unmodifiable view of the loaded UI entries
     */
    public static List<UIEntry> getUIEntries() {
        return Collections.unmodifiableList(uiEntries);
    }

    /**
     * Stores the loaded UI entries; set once by {@code UIDataLoader} (after bases and renderers).
     *
     * <p>Function setUIEntries coded before 260924, commented in full on 260924.
     *
     * @param uiEntries the loaded UI entries
     */
    public static void setUIEntries(List<UIEntry> uiEntries) {
        UIRegistry.uiEntries = uiEntries;
    }

    /**
     * Finds a loaded UI-entry renderer by name, the port of the renderer half of what C resolves
     * inline via {@code ui_entry_renderer_lookup} wherever a {@code renderer:} directive is parsed
     * (for example {@code parse_entry_renderer}, {@code [C] ui-entry.c:2037-2051}). Unlike
     * {@link #getUIEntryBase} and {@link #getUIEntry} below - which throw when queried before load -
     * this one collects its failures into the supplied {@code errors} list and returns {@code null},
     * because it is called from the assembler's collect-every-error load path rather than from the
     * running game, where an unloaded registry or an unknown name should skip one record rather than
     * halt the process.
     *
     * <p>Function getUIEntryRenderer coded before 260924, commented in full on 260924.
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
     * Finds a loaded {@link UIEntryBase} template by name - the port of the template-lookup half of
     * what C resolves via {@code ui_entry_lookup}/{@code ui_entry_search}
     * ({@code [C] ui-entry.c:1165-1208}) when a {@code template:} directive names one
     * ({@code parse_entry_template}, {@code [C] ui-entry.c:1966-2004}). Unlike
     * {@link #getUIEntryRenderer} above, an unloaded registry is treated as a programming error here
     * rather than a per-record failure to collect and skip, so it throws instead of returning
     * {@code null} and logging to an {@code errors} list; a name simply not found among the loaded
     * bases still returns {@code null}; matching name comparisons return exactly one record, since a
     * name never repeats in {@code ui_entry_base.txt}.
     *
     * <p>Function getUIEntryBase coded before 260924, commented in full on 260924.
     *
     * @param name the name of the base we are wanting to find
     * @return a reference to the base with the name equal to the incoming parameter, or null if no base is found
     * with that name
     * @throws IllegalStateException if the base registry has not yet been loaded
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
     * Finds a loaded {@link UIEntry} by name - the port of C's {@code ui_entry_lookup}/
     * {@code ui_entry_search} ({@code [C] ui-entry.c:1165-1208}), the linear search over
     * {@code entries[]} both {@code hatch_embryo} (to find an existing record to override,
     * {@code [C] ui-entry.c:1786-1791}) and every {@code bind_*_to_ui_entry_by_name} function use to
     * resolve a name to a {@code struct ui_entry}. Since {@link #uiEntries} is the single registry the
     * 260919 decision folded {@code ui_entry_base.txt} and {@code ui_entry.txt} records into, this is
     * the one lookup that can return either kind - a template-derived entry is told apart only by
     * {@code ENTRY_FLAG_TEMPLATE_ONLY} on the result, not by which method found it. Throws rather than
     * returning {@code null} when the registry itself is unloaded, the same programming-error
     * distinction {@link #getUIEntryBase} draws; a name simply not found still returns {@code null}.
     *
     * <p>Function getUIEntry coded before 260924, commented in full on 260924.
     *
     * @param name the string representation of the UIEntry's name
     * @return the entry with the same name as {@code name}, or {@code null} if not found
     * @throws IllegalStateException if the entry registry has not yet been loaded
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

    /**
     * The healthy-stat display labels, index-aligned with
     * {@link uk.co.jackoftradesltd.channel.globals.ChannelRegistry#STAT_MAX}-sized stat arrays such
     * as those read by {@code UIPlayer.displayPlayerStatInfo}. The Java form of C's
     * {@code stat_names[STAT_MAX]} ({@code [C] ui-display.c:99-102}), shown for a stat that is at or
     * above its natural maximum ({@code player->stat_cur[stat] >= player->stat_max[stat]}) - see
     * {@link #statReducedNames} for the injured-stat counterpart shown otherwise.
     *
     * <p>Each label carries the trailing space C bakes into the literal (for example
     * {@code "STR: "}, not {@code "STR:"}); that space is not cosmetic - C's natural-maximum
     * indicator overwrites the label's fourth character in place at a fixed column offset
     * ({@code put_str("!", row, col+3)}, {@code [C] ui-player.c:481-482}), turning {@code "STR: "}
     * into {@code "STR!"}, so the label's exact width and character positions matter to any later
     * port of that indicator.
     *
     * <p>Field statNames coded before 260925, commented in full on 260925.
     */
    public static final String[] statNames = {"STR: ", "INT: ", "WIS: ", "DEX: ", "CON: "};

    /**
     * The injured-stat display labels, the lowercase counterpart to {@link #statNames} shown when a
     * stat's current value has been drained below its natural maximum
     * ({@code player->stat_cur[stat] < player->stat_max[stat]}). The Java form of C's
     * {@code stat_names_reduced[STAT_MAX]} ({@code [C] ui-display.c:104-110}), read alongside
     * {@link #statNames} by {@code UIPlayer.displayPlayerStatInfo}
     * ({@code [C] ui-player.c:471-478}). Carries the same trailing space as {@link #statNames}, and
     * for the same reason.
     *
     * <p>Field statReducedNames coded before 260925, commented in full on 260925.
     */
    public static final String[] statReducedNames = {"Str: ", "Int: ", "Wis: ", "Dex: ", "Con: "};
}
