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

package uk.co.jackoftrades.middle.objects.enums;

/**
 * The headings the knowledge menu groups runes under. Each {@link RuneVariety} maps to exactly one
 * group via {@link RuneVariety#group()}, and the group supplies the label shown in the browser's
 * left-hand panel.
 *
 * <p>Ports two C structures that are kept in step by hand there: the ordinals mirror
 * {@code enum rune_variety} ({@code src/obj-knowledge.h}) and the names mirror
 * {@code rune_group_text[]} ({@code src/ui-knowledge.c}), which C indexes with those ordinals.
 * Note that flag runes display as {@code "Other"} rather than "Flags" — that mismatch is the one
 * place the label diverges from the variety it names, and it is deliberate in the original.
 *
 * <p>Declaration order matters. C's browser buckets the rune list with a single run-length pass,
 * starting a new group each time the group id changes and never sorting, so the rune list must
 * already be in group order. A rune appearing out of order would not merely sort oddly — it would
 * produce a second panel entry with the same label. Keeping this enum in the order the rune list is
 * built preserves that invariant.
 *
 * @author Rowan Crowther
 */
public enum RuneGroup {
    COMBAT("Combat"),
    MODIFIERS("Modifiers"),
    RESIST("Resists"),
    BRAND("Brands"),
    SLAY("Slays"),
    CURSE("Curses"),
    OTHER("Other");

    /**
     * The heading shown to the player for this group.
     *
     * @author Rowan Crowther
     */
    private final String name;

    /**
     * Bind a group to its player-visible heading.
     *
     * @param name the heading shown in the knowledge menu
     * @author Rowan Crowther
     */
    RuneGroup(String name) {
        this.name = name;
    }

    /**
     * @return the player-visible heading for this group
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }
}
