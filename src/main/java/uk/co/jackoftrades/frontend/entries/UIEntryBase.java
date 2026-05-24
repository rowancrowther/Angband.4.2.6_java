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

public class UIEntryBase {
    private final static Logger logger = LogManager.getLogger();

    private final String name;
    private final UIEntryRenderer renderer;
    private final CombinerName combine;
    private final List<String> categories;
    private final String flags;
    private final String desc;

    public UIEntryBase(String name, UIEntryRenderer renderer, CombinerName combine, List<String> categories, String flags, String desc) {
        this.name = name;
        this.renderer = renderer;
        this.combine = combine;
        this.categories = categories;
        this.flags = flags;
        this.desc = desc;
    }

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