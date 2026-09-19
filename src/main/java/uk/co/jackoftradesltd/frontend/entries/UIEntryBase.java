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

package uk.co.jackoftradesltd.frontend.entries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;

import java.util.List;

/**
 * A reusable template for {@link UIEntry} definitions: the shared defaults
 * (renderer, combiner, categories, flags) that concrete entries inherit from.
 * Built by {@code UIEntryBaseAssembler} from the records the
 * {@code UIEntryBaseGrammar} parser reads out of
 * {@code lib/gamedata/ui_entry_base.txt}.
 * <p>
 * This is the "base" half of the C original's UI-entry system
 * ({@code [C] src/ui-entry.c}), letting many status entries share common
 * settings. C uses one {@code struct ui_entry} ({@code [C] ui-entry.c:97-118})
 * for both templates and the entries built from them, telling the two apart
 * only by the internal {@code ENTRY_FLAG_TEMPLATE_ONLY} bit; this port models
 * the template half as its own type instead. {@code renderer} and
 * {@code combine} are C's numeric {@code renderer_index} /
 * {@code combiner_index} ({@code [C] ui-entry.c:107-108}) already resolved to
 * the Java domain objects, by {@code parse_entry_renderer} /
 * {@code parse_entry_combine} ({@code [C] ui-entry.c:2014-2041}) in the C
 * original. {@code categories} carries only the category names, dropping the
 * per-category priority that C's {@code struct category_reference}
 * ({@code [C] ui-entry.c:55-59}) also stores, because the shipped
 * {@code ui_entry_base.txt} never sets a {@code priority:} against a template
 * category - priority is resolved at the {@link UIEntry} (instance) boundary
 * instead. The description ({@code desc:}) is read by the grammar but never
 * kept here, matching {@code parse_entry_desc} ({@code [C] ui-entry.c:2224-2233}),
 * whose own comment says "don't bother to store the description".
 *
 * <p>Class UIEntryBase coded before 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class UIEntryBase {
    /**
     * Logger for this class, used by {@link #parseFlags(String)} to warn
     * when a {@code flags:} token does not match a known {@link ChannelEntryFlag}.
     *
     * <p>Field logger coded before 260916, commented in full on 260916.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * The template's name, referenced by {@code template:} in
     * {@code ui_entry.txt} entries that pull this template in. The Java form
     * of C's {@code ui_entry.name} ({@code [C] ui-entry.c:98}), set once per
     * block by {@code parse_entry_name} ({@code [C] ui-entry.c:1881-1942}).
     *
     * <p>Field name coded before 260916, commented in full on 260916.
     */
    private final String name;
    /**
     * Default renderer for entries built from this template - the resolved
     * form of C's {@code renderer_index} ({@code [C] ui-entry.c:107}), set by
     * {@code parse_entry_renderer} via {@code ui_entry_renderer_lookup}
     * ({@code [C] ui-entry.c:2014-2026}).
     *
     * <p>Field renderer coded before 260916, commented in full on 260916.
     */
    private final UIEntryRenderer renderer;
    /**
     * Default value-combining strategy - the resolved form of C's
     * {@code combiner_index} ({@code [C] ui-entry.c:108}), set by
     * {@code parse_entry_combine} via {@code ui_entry_combiner_lookup}
     * ({@code [C] ui-entry.c:2029-2041}).
     *
     * <p>Field combine coded before 260916, commented in full on 260916.
     */
    private final CombinerName combine;
    /**
     * Categories this template belongs to (used to group entries on screen),
     * built up by one or more {@code category:} lines via
     * {@code parse_entry_category} ({@code [C] ui-entry.c:2113-2129}). Unlike
     * C's {@code struct category_reference} ({@code [C] ui-entry.c:55-59}),
     * only the name travels here - no per-category priority, since the
     * shipped {@code ui_entry_base.txt} never sets one on a template.
     *
     * <p>Field categories coded before 260916, commented in full on 260916.
     */
    private final List<String> categories;
    /**
     * Behavioural flags applied to entries built from this template, resolved
     * and validated from the raw {@code flags:} text by
     * {@link #parseFlags(String)}. The Java form of C's {@code entry->flags}
     * bitmask ({@code [C] ui-entry.c:111}), built by {@code parse_entry_flags}
     * ({@code [C] ui-entry.c:2187-2221}) against the {@code entry_flags[]}
     * table ({@code [C] ui-entry.c:86-88}).
     *
     * <p>Field flags retyped from String to {@code Flag<ChannelEntryFlag>} on
     * 260916 so an unrecognised flag name is rejected the way C's
     * {@code PARSE_ERROR_INVALID_FLAG} rejects it, rather than carried
     * through unchecked; commented in full on 260916.
     */
    private final Flag<ChannelEntryFlag> flags;

    /**
     * Build a UI-entry template from its parsed fields. {@code desc} is
     * accepted only to guard against a {@code null} coming from the
     * assembler - it is never stored, matching {@code parse_entry_desc}
     * ({@code [C] ui-entry.c:2224-2233}), whose own comment says "don't
     * bother to store the description". {@code flags} is resolved via
     * {@link #parseFlags(String)}; an unrecognised token throws
     * {@link IllegalArgumentException}, mirroring the
     * {@code PARSE_ERROR_INVALID_FLAG} C's {@code parse_entry_flags}
     * ({@code [C] ui-entry.c:2187-2221}) raises for the same case.
     *
     * @param name       template name
     * @param renderer   default renderer
     * @param combine    default value combiner
     * @param categories categories the template belongs to
     * @param flags      raw {@code flags:} text, one or more
     *                   {@link ChannelEntryFlag} names separated by {@code |}
     * @param desc       description; must not be {@code null}, but is
     *                   otherwise discarded
     * @throws IllegalArgumentException if {@code desc} is {@code null} or
     *                                   {@code flags} contains a token that
     *                                   is not a known {@link ChannelEntryFlag}
     *
     * <p>Function UIEntryBase(String, UIEntryRenderer, CombinerName, List,
     * String, String) coded before 260916, updated on 260916 to validate
     * flags and drop the stored description, commented in full on 260916.
     */
    public UIEntryBase(String name, UIEntryRenderer renderer, CombinerName combine, List<String> categories, String flags, String desc) {
        if (desc == null)
            throw new IllegalArgumentException("Description cannot be null in UIEntryBase " + name);

        this.name = name;
        this.renderer = renderer;
        this.combine = combine;
        this.categories = categories;
        this.flags = parseFlags(flags);
        if (this.flags == null)
            throw new IllegalArgumentException("Error parsing flags in UIEntryBase " + name);
    }

    /**
     * Resolve a raw {@code flags:} value into a {@link Flag} of
     * {@link ChannelEntryFlag}. Splits {@code flag} on the literal {@code |}
     * character, trims and upper-cases each piece, and looks it up as
     * {@code ChannelEntryFlag.ENTRY_FLAG_<piece>}, turning each match on in the
     * result. This is the Java form of C's {@code parse_entry_flags}
     * ({@code [C] ui-entry.c:2187-2221}), which tokenizes on {@code strtok(flags,
     * " |")} (space <em>or</em> pipe) against the {@code entry_flags[]} table
     * ({@code [C] ui-entry.c:86-88}) and rejects the whole file load with
     * {@code PARSE_ERROR_INVALID_FLAG} on the first unmatched token; the
     * shipped {@code ui_entry_base.txt} only ever supplies a single flag
     * ({@code TIMED_AS_AUX}) per record, so the multi-flag and
     * space-delimiter paths are unexercised here. Unlike C, a failure here
     * does not stop the file load itself - it is reported to the caller as
     * {@code null} and turned into an {@link IllegalArgumentException} by the
     * constructor.
     *
     * @param flag the raw {@code flags:} text
     * @return the resolved flags, or {@code null} if any {@code |}-separated
     * piece does not match a known {@link ChannelEntryFlag}
     *
     * <p>Function parseFlags(String) coded on 260916, commented in full on
     * 260916.
     */
    private Flag<ChannelEntryFlag> parseFlags(String flag) {
        Flag<ChannelEntryFlag> results = new Flag<>(ChannelEntryFlag.class);

        String[] flagParts = flag.split("\\|");

        try {
            for (String flagPart : flagParts) {
                flagPart = flagPart.trim();

                ChannelEntryFlag entryFlag = ChannelEntryFlag.valueOf("ENTRY_FLAG_" + flagPart.toUpperCase());
                results.on(entryFlag);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid entry flag: " + flag);
            return null;
        }

        return results;
    }

    /**
     * Getter - the name of the UIEntryBase.
     *
     * @return the name of the UIEntryBase
     *
     * <p>Function getName() coded before 260916, commented in full on
     * 260916.
     */
    public String getName() {
        return name;
    }

    /**
     * Debug string listing this template's fields, for logging.
     *
     * @return a debug string listing this template's fields
     *
     * <p>Function toString() coded before 260916, updated on 260916 to drop
     * the removed {@code desc} field, commented in full on 260916.
     */
    @Override
    public String toString() {
        return "UIEntryBase{" +
                "name='" + name + '\'' +
                ", renderer=" + renderer +
                ", combine=" + combine +
                ", categories=" + categories +
                ", flags='" + flags + '\'' +
                '}';
    }

    /**
     * Getter - the default renderer for entries built from this template.
     *
     * @return the default renderer
     *
     * <p>Function getRenderer() coded before 260916, commented in full on
     * 260916.
     */
    public UIEntryRenderer getRenderer() {
        return renderer;
    }

    /**
     * Getter - the default value-combining strategy for entries built from
     * this template.
     *
     * @return the default value combiner
     *
     * <p>Function getCombine() coded before 260916, commented in full on
     * 260916.
     */
    public CombinerName getCombine() {
        return combine;
    }

    /**
     * Getter - the categories this template belongs to.
     *
     * @return the categories this template belongs to
     *
     * <p>Function getCategories() coded before 260916, commented in full on
     * 260916.
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * Getter - the resolved behavioural flags for entries built from this
     * template.
     *
     * @return the resolved flags
     *
     * <p>Function getFlags() retyped from String to {@code Flag<ChannelEntryFlag>}
     * on 260916 so callers see validated flags rather than the raw string,
     * commented in full on 260916.
     */
    public Flag<ChannelEntryFlag> getFlags() {
        return flags;
    }
}