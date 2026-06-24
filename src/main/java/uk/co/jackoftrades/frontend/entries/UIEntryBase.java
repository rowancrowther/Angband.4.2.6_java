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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.frontend.entries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.frontend.screen.enums.CombinerName;

import java.util.List;

/**
 * A reusable template for {@link UIEntry} definitions: the shared defaults
 * (renderer, combiner, categories, flags, description) that concrete entries
 * inherit from. This is the "base" half of the C original's UI-entry system
 * ({@code src/ui-entry.c}), letting many status entries share common settings.
 *
 * @author ClaudeCode
 */
public class UIEntryBase {
    /**
     * Logger (reserved for diagnostics).
     *
     * @author ClaudeCode
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * The template's name, referenced by entries that use it.
     *
     * @author ClaudeCode
     */
    private final String name;
    /**
     * Default renderer for entries built from this template.
     *
     * @author ClaudeCode
     */
    private final UIEntryRenderer renderer;
    /**
     * Default value-combining strategy.
     *
     * @author ClaudeCode
     */
    private final CombinerName combine;
    /**
     * Categories this template belongs to (used to group entries on screen).
     *
     * @author ClaudeCode
     */
    private final List<String> categories;
    /**
     * Raw flag string applied to entries built from this template.
     *
     * @author ClaudeCode
     */
    private final String flags;
    /**
     * Human-readable description.
     *
     * @author ClaudeCode
     */
    private final String desc;

    /**
     * Build a UI-entry template from its parsed fields.
     *
     * @param name       template name
     * @param renderer   default renderer
     * @param combine    default value combiner
     * @param categories categories the template belongs to
     * @param flags      raw flag string
     * @param desc       description
     * @author ClaudeCode
     */
    public UIEntryBase(String name, UIEntryRenderer renderer, CombinerName combine, List<String> categories, String flags, String desc) {
        this.name = name;
        this.renderer = renderer;
        this.combine = combine;
        this.categories = categories;
        this.flags = flags;
        this.desc = desc;
    }

    /**
     * Getter - the name of the UIEntryBase
     *
     * @return the name of the UIEntryBase
     */
    public String getName() {
        return name;
    }

    /**
     * @return a debug string listing this template's fields
     * @author ClaudeCode
     */
    @Override
    public String toString() {
        return "UIEntryBase{" +
                "name='" + name + '\'' +
                ", renderer=" + renderer +
                ", combine=" + combine +
                ", categories=" + categories +
                ", flags='" + flags + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}