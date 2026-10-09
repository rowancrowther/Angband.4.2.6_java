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

package uk.co.jackoftradesltd.middle.objects.enums;

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
 * <p>C's array ends in a {@code NULL} entry that {@code N_ELEMENTS} counts, so the group count it
 * hands the browser is eight where there are seven headings. The terminator is not ported: the
 * count only caps how many groups {@code display_knowledge} sizes its tables for, and the browser
 * never reads past the groups that actually occur.
 *
 * <p>Declaration order documents C's rather than driving it. C's browser
 * ({@code display_knowledge}, {@code ui-knowledge.c}) buckets the rune list with a single
 * run-length pass, starting a new group each time the group id changes and never sorting (the rune
 * browser passes no comparison function), so the rune list must already be in group order. A rune
 * appearing out of order would not merely sort oddly — it would produce a second panel entry with
 * the same label. That order comes from the sequence {@code init_rune} ({@code obj-knowledge.c})
 * builds the list in, which is the order of the constants below. Nothing in the port reads an
 * ordinal — each {@link RuneVariety} record names its group directly — so reordering the constants
 * cannot move a rune under the wrong heading, but it would stop agreeing with the order C lists
 * them in.
 *
 * <p>Type RuneGroup coded before 260814, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public enum RuneGroup {
    /**
     * Enchantments to armour class, to-hit and to-damage; C's {@code RUNE_VAR_COMBAT}, the first
     * heading in {@code rune_group_text[]}. Held by {@link RuneVariety.CombatKey}.
     */
    COMBAT("Combat"),
    /**
     * Numeric object modifiers such as stats, speed and stealth; C's {@code RUNE_VAR_MOD}. Held by
     * {@link RuneVariety.ModKey}.
     */
    MODIFIERS("Modifiers"),
    /**
     * Elemental resistances; C's {@code RUNE_VAR_RESIST}. Held by {@link RuneVariety.ResistKey}.
     */
    RESIST("Resists"),
    /**
     * Weapon brands, one rune per brand name; C's {@code RUNE_VAR_BRAND}. Held by
     * {@link RuneVariety.BrandKey}.
     */
    BRAND("Brands"),
    /**
     * Weapon slays, one rune per set of monsters slain; C's {@code RUNE_VAR_SLAY}. Held by
     * {@link RuneVariety.SlayKey}.
     */
    SLAY("Slays"),
    /**
     * Curses, one rune each; C's {@code RUNE_VAR_CURSE}. Held by {@link RuneVariety.CurseKey}.
     */
    CURSE("Curses"),
    /**
     * Boolean object flags such as sustains and protections; C's {@code RUNE_VAR_FLAG}. The heading
     * reads "Other", not "Flags" — the one label in {@code rune_group_text[]} that does not echo its
     * variety. Held by {@link RuneVariety.FlagKey}.
     */
    OTHER("Other");

    /**
     * The heading shown to the player for this group — the string at this group's position in C's
     * {@code rune_group_text[]}, which {@code rune_var_name} ({@code ui-knowledge.c}) returns for
     * the browser's left-hand panel.
     *
     * <p>Field name coded before 260814, commented in full on 261009.
     */
    private final String name;

    /**
     * Bind a group to its player-visible heading.
     *
     * <p>Constructor RuneGroup coded before 260814, commented in full on 261009.
     *
     * @param name the heading shown in the knowledge menu
     */
    RuneGroup(String name) {
        this.name = name;
    }

    /**
     * The player-visible heading for this group, as C's {@code rune_var_name} returns it for the
     * knowledge browser's group panel. This is the display text — "Resists" for {@link #RESIST} —
     * not the constant's identifier, which {@link #name()} gives.
     *
     * <p>Method getName coded before 260814, commented in full on 261009.
     *
     * @return the player-visible heading for this group
     */
    public String getName() {
        return name;
    }
}