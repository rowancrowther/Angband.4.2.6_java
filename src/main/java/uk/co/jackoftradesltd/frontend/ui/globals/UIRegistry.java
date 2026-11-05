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
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;

import java.util.Collections;
import java.util.List;

/**
 * Runtime holder for the UI-entry game data - the entry renderers and the entries that drive the
 * character-screen and property displays - plus the name lookups used to resolve them, and the
 * size constants and stat labels those displays share.
 *
 * <p>This is the read side of the UI slice: it is populated once at startup by
 * {@link UIDataLoader} (renderers first, then the entries) and thereafter only read. It was split
 * out of {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * <p>There is one entry list, as in C's {@code entries[]} in {@code ui-entry.c}: the records of
 * {@code ui_entry_base.txt} and of {@code ui_entry.txt} live together, and a base record is told
 * apart only by {@code ENTRY_FLAG_TEMPLATE_ONLY}. A {@code template:} directive, an override of an
 * existing name and a {@code bind_*_to_ui_entry_by_name} call all resolve through the one
 * {@link #getUIEntry} lookup, as they all search the one {@code entries[]} in C (the first two
 * through {@code ui_entry_lookup}, the bind functions through {@code ui_entry_search} directly).
 * There is no separate registry of bases (260919 decision).
 *
 * <p>Class UIRegistry coded before 260924, commented in full on 261102.
 *
 * @author Rowan Crowther
 */
public class UIRegistry {
    /**
     * The longest label text a UI entry can carry. The Java form of C's {@code MAX_ENTRY_LABEL}
     * ({@code #define MAX_ENTRY_LABEL (80)} in {@code ui-entry.c}), which C uses to limit what is
     * extracted from the configuration file.
     *
     * <p>Field MAX_ENTRY_LABEL coded before 260924, commented in full on 261102.
     */
    public static final int MAX_ENTRY_LABEL = 80;

    // Globals
    /**
     * The number of shortened-label slots a UI entry carries. The Java form of C's
     * {@code MAX_SHORTENED} ({@code #define MAX_SHORTENED (10)} in {@code ui-entry.c}): the shortened
     * versions of a label have lengths of one to {@code MAX_SHORTENED} characters, not counting
     * the terminating null C stores.
     *
     * <p>Field MAX_SHORTENED coded before 260924, commented in full on 261102.
     */
    public static final int MAX_SHORTENED = 10;
    /**
     * Logger for the fatal states the two lookups throw on. {@link #getUIEntry} logs one case, an
     * unloaded registry. {@link #getUIEntryRenderer} logs two, an unloaded registry and a name with
     * no match. Each is logged at {@code fatal} just before the throw.
     *
     * <p>Field logger coded before 260924, commented in full on 261102, updated on 261105 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The loaded UI-entry renderers, resolved by name via {@link #getUIEntryRenderer}. The Java form
     * of C's {@code renderers[]} array in {@code ui-entry-renderers.c}. Set once, by
     * {@code UIDataLoader}, before {@link #uiEntries} is loaded, because both entry assemblers
     * resolve each record's {@code renderer:} name against it. {@code null} until then.
     *
     * <p>Field uiEntryRenderers coded before 260924, commented in full on 261102.
     */
    private static List<UIEntryRenderer> uiEntryRenderers;
    
    /**
     * The loaded UI entries - the single shared list carrying both the {@code ui_entry_base.txt}
     * placeholders (flagged {@code ENTRY_FLAG_TEMPLATE_ONLY}) and the {@code ui_entry.txt} entries
     * proper, per the 260919 one-registry decision - resolved by name via {@link #getUIEntry}. The
     * Java form of C's {@code entries[]} array in {@code ui-entry.c}, searched by name via
     * {@code ui_entry_search}.
     *
     * <p>It is filled in two steps: {@code UIEntryBaseAssembler} stores the base records while
     * {@code ui_entry_base.txt} is parsed, then {@code UIEntryAssembler} seeds from that list and
     * {@code UIDataLoader.loadUIEntries} stores the finished result. {@code null} until the first
     * of those.
     *
     * <p>Unlike C, whose array is kept sorted by name for a binary search, this list is in load
     * order and is searched linearly, returning the first match; the two find the same record
     * whenever a name is present only once.
     *
     * <p>Field uiEntries coded before 260924, commented in full on 261102.
     */
    private static List<UIEntry> uiEntries;

    /**
     * Returns an unmodifiable view of the loaded UI-entry renderers, in file order.
     *
     * <p>Unlike {@link #getUIEntry} and {@link #getUIEntryRenderer}, this does not check for an
     * unloaded registry: called before {@link #setUIEntryRenderers} it throws a
     * {@link NullPointerException} from {@code Collections.unmodifiableList(null)}. C has no
     * equivalent state, as its unloaded {@code renderers[]} is simply empty.
     *
     * <p>Function getUIEntryRenderers coded before 260924, commented in full on 261102.
     *
     * @return an unmodifiable view of the loaded UI-entry renderers
     * @throws NullPointerException if the renderers have not yet been loaded
     */
    public static List<UIEntryRenderer> getUIEntryRenderers() {
        return Collections.unmodifiableList(uiEntryRenderers);
    }

    /**
     * Stores the loaded UI-entry renderers; set once by {@code UIDataLoader}, before any entry is
     * assembled. The list is kept as given, not copied, and it holds real records only: unlike C's
     * 1-based renderer index, where 0 means "none", there is no blank slot 0.
     *
     * <p>Function setUIEntryRenderers coded before 260924, commented in full on 261102.
     *
     * @param uiEntryRenderers the loaded UI-entry renderers
     */
    public static void setUIEntryRenderers(List<UIEntryRenderer> uiEntryRenderers) {
        UIRegistry.uiEntryRenderers = uiEntryRenderers;
    }

    /**
     * Returns an unmodifiable view of the loaded UI entries, base records included. This is the
     * list {@code UIEntryAssembler} seeds from, so that a {@code ui_entry.txt} record can template
     * off, or override, a record of {@code ui_entry_base.txt}.
     *
     * <p>Like {@link #getUIEntryRenderers}, this does not check for an unloaded registry: called
     * before {@link #setUIEntries} it throws a {@link NullPointerException}, where C's unloaded
     * {@code entries[]} is simply empty.
     *
     * <p>Function getUIEntries coded before 260924, commented in full on 261102.
     *
     * @return an unmodifiable view of the loaded UI entries
     * @throws NullPointerException if the entries have not yet been loaded
     */
    public static List<UIEntry> getUIEntries() {
        return Collections.unmodifiableList(uiEntries);
    }

    /**
     * Stores the loaded UI entries. Called twice during start-up: once by
     * {@code UIEntryBaseAssembler} with the base-file records, and again by
     * {@code UIDataLoader.loadUIEntries} with the finished list, which replaces the first. Passing
     * {@code null} puts the registry back to its unloaded state, which {@link #getUIEntry} then
     * refuses to search.
     *
     * <p>Function setUIEntries coded before 260924, commented in full on 261102.
     *
     * @param uiEntries the loaded UI entries
     */
    public static void setUIEntries(List<UIEntry> uiEntries) {
        UIRegistry.uiEntries = uiEntries;
    }

    /**
     * Finds a loaded UI-entry renderer by name, the port of C's {@code ui_entry_renderer_lookup}
     * in {@code ui-entry-renderers.c}, which {@code parse_entry_renderer} in {@code ui-entry.c}
     * calls wherever a {@code renderer:} directive names one. C compares with {@code streq} - whole
     * name, case-sensitive - and returns the first match's index plus one, or 0 for no match; this
     * returns the renderer itself.
     *
     * <p>The two failure cases are handled differently:
     * <ul>
     *   <li>An unloaded registry throws {@link IllegalArgumentException}, logged at {@code fatal}.
     *   In C an unloaded {@code renderers[]} is empty, so the lookup just returns 0; the "hasn't
     *   been initialized" report is Java-only.</li>
     *   <li>A name with no match returns {@code null}, as C's lookup returns 0, and leaves the error
     *   to the caller. Both callers, {@code UIEntryAssembler} and {@code UIEntryBaseAssembler},
     *   check for {@code null} and throw a {@code DataFileParseException} naming the record's line
     *   and the file, which stops the load. That is where C's {@code parse_entry_renderer} gets 0
     *   back and returns {@code PARSE_ERROR_INVALID_VALUE}, which fails the parse of the file.</li>
     * </ul>
     *
     * <p>This matches {@link #getUIEntry}, which also throws only for an unloaded registry and
     * returns {@code null} for an unknown name.
     *
     * <p>The name is compared with {@code String.equals}, so it matches case-sensitively as C's
     * {@code streq} does.
     *
     * <p>Function getUIEntryRenderer coded before 260924, commented in full on 261102, updated on
     * 261105 for the removal of the soft-error path and again on 261105 for the {@code null} return
     * on an unknown name.
     *
     * @param name the name of the renderer we wish to obtain
     * @return the renderer with the same name as {@code name} or {@code null} if no renderer with
     * that name was found.
     * @throws IllegalArgumentException if the renderers have not yet been loaded
     */
    @Nullable
    public static UIEntryRenderer getUIEntryRenderer(@NotNull String name) {
        if (uiEntryRenderers == null) {
            String message = "Invalid attempt to access uiEntryRenderers when it hasn't been initialized";
            logger.fatal(message);
            throw new IllegalArgumentException(message);
        }

        UIEntryRenderer renderer = uiEntryRenderers.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);

        return renderer;
    }

    /**
     * Finds a loaded {@link UIEntry} by name - the port of C's {@code ui_entry_lookup} and
     * {@code ui_entry_search} in {@code ui-entry.c}. C's {@code ui_entry_search} is a binary search
     * over the name-sorted {@code entries[]} using {@code strcmp}; this is a linear search that
     * returns the first match. Both match the whole name, case-sensitively, and find the same
     * record while a name is present only once. C's {@code ui_entry_lookup} returns the index plus
     * one (0 for no match); this returns the entry, or {@code null}.
     *
     * <p>In C the name search has three kinds of caller. {@code parse_entry_name} calls
     * {@code ui_entry_lookup} to find an existing record to override, and
     * {@code parse_entry_template} calls it to find the {@code template:} record to copy. Each
     * {@code bind_*_to_ui_entry_by_name} function calls {@code ui_entry_search} directly, and
     * {@code ui_entry_insert} (reached from {@code hatch_embryo}) uses it to refuse a duplicate
     * name. Since {@link #uiEntries} is the single registry the 260919 decision folded
     * {@code ui_entry_base.txt} and {@code ui_entry.txt} records into, this is the one lookup for
     * the override, template and bind cases, and it can return either kind: a base record is told
     * apart only by {@code ENTRY_FLAG_TEMPLATE_ONLY} on the result, not by which method found it,
     * so a {@code template:} may name a {@code ui_entry.txt} record as well as a base.
     *
     * <p>Throws rather than returning {@code null} when the registry itself is unloaded, a
     * programming error rather than a per-record failure; a name simply not found still returns
     * {@code null}. A {@code null} name finds nothing, where C's {@code strcmp} would fault.
     *
     * <p>Function getUIEntry coded before 260924, commented in full on 261102.
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
     * {@code stat_names[STAT_MAX]} in {@code ui-display.c}, shown for a stat that is at or above its
     * natural maximum ({@code player->stat_cur[stat] >= player->stat_max[stat]}) - see
     * {@link #statReducedNames} for the injured-stat counterpart shown otherwise.
     *
     * <p>Each label carries the trailing space C bakes into the literal (for example
     * {@code "STR: "}, not {@code "STR:"}); that space is not cosmetic - C's natural-maximum
     * indicator overwrites the label's fourth character in place at a fixed column offset
     * ({@code put_str("!", row, col+3)} in {@code ui-display.c} and {@code ui-player.c}), turning
     * {@code "STR: "} into {@code "STR!"}, so the label's exact width and character positions matter
     * to any later port of that indicator.
     *
     * <p>Field statNames coded before 260925, commented in full on 261102.
     */
    public static final String[] statNames = {"STR: ", "INT: ", "WIS: ", "DEX: ", "CON: "};

    /**
     * The injured-stat display labels, the lowercase counterpart to {@link #statNames} shown when a
     * stat's current value has been drained below its natural maximum
     * ({@code player->stat_cur[stat] < player->stat_max[stat]}). The Java form of C's
     * {@code stat_names_reduced[STAT_MAX]} in {@code ui-display.c}, read alongside
     * {@link #statNames} by {@code UIPlayer.displayPlayerStatInfo} (C's equivalent in
     * {@code ui-player.c}). Carries the same trailing space as {@link #statNames}, and for the same
     * reason.
     *
     * <p>Field statReducedNames coded before 260925, commented in full on 261102.
     */
    public static final String[] statReducedNames = {"Str: ", "Int: ", "Wis: ", "Dex: ", "Con: "};

    /**
     * The longest colour or symbol palette a UI-entry renderer can carry. The Java form of C's
     * {@code MAX_PALETTE}, defined in {@code ui-entry-renderers.c} (not {@code ui-entry.c}, where the
     * other constants in this class come from).
     *
     * <p>C uses it to cap the parsed {@code colors:} and {@code label-colors:} lists
     * ({@code renderer->ncolors} and {@code renderer->nlabcolors} become {@code min(n, MAX_PALETTE)}),
     * and to size the symbol buffers ({@code defsym[MAX_PALETTE]} and the {@code MAX_PALETTE + 1}
     * allocation for {@code renderer->symbols}). It lives here rather than beside the renderer
     * because {@code UIEntryRendererAssembler} reads it as {@code UIRegistry.MAX_PALETTE}.
     *
     * <p>Field MAX_PALETTE coded before 260929, commented in full on 261102.
     */
    public static final int MAX_PALETTE = 64;
}
