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

package uk.co.jackoftradesltd.frontend.colour;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import java.util.Map;

/**
 * The flicker table: colour cycles keyed by <strong>base attribute</strong>. When an
 * {@code ATTR_FLICKER} monster has no explicitly assigned named cycle, its animation is driven by
 * looking its static base colour up here and stepping the returned {@link ColourCycle} by the global
 * frame counter. Port of the C {@code visuals_flicker} ({@code ui-visuals.c}); this is tier 2 of the
 * three-tier fallback in C's {@code do_animation} ({@code ui-display.c}), between an assigned named
 * cycle (tier 1) and the plain static colour (tier 3).
 * <p>
 * <strong>This half is live, not dead.</strong> The shipped monster.txt has 46 {@code ATTR_FLICKER}
 * monsters but only 9 {@code color-cycle:} assignments, so most flickering monsters fall through to
 * this table - it is the {@code cycle:flicker:} <em>named</em> group in visuals.txt that nothing
 * requests, not the flicker blocks.
 * <p>
 * <strong>A missing key is dark, not a sentinel.</strong> C allocates its table with
 * {@code mem_zalloc} at {@code MAX_COLORS} cycles of three steps, so a base attribute that no
 * {@code flicker:} block wrote reads back as the zero colour, {@code COLOUR_DARK}, at every frame.
 * C returns {@code BASIC_COLORS} (the "no answer" sentinel that sends {@code do_animation} on to its
 * tier 3) only when the table itself is missing or the base attribute is {@code MAX_COLORS} or
 * more. This port has no missing table and every {@link ColourEnum} sits below {@code MAX_COLORS},
 * so the sentinel never arises: {@link #getAttr} answers {@link ColourEnum#COLOUR_DARK} for an
 * absent key, exactly as C's zeroed row does. In practice that makes tier 3 unreachable for any
 * in-range base attribute, in C as here.
 * <p>
 * Every cycle the assembler stores holds exactly three steps (see
 * {@link uk.co.jackoftradesltd.backend.parser.visuals.VisualsFlickerAssembler}), so the wrap width
 * used by {@link #getAttr} is C's {@code colors_per_cycle} of 3. C's other fixed table ceilings
 * ({@code MAX_COLORS} cycles) are dropped as allocation artefacts with no semantics.
 *
 * <p>Class FlickerTable coded before 261108, commented in full on 261108.
 *
 * @author Rowan Crowther
 */
public final class FlickerTable {
    /**
     * Cycles indexed by the base attribute they animate. The key is the monster's static colour; the
     * value is the frame sequence to show instead while it flickers. Java stand-in for the flat
     * {@code cycles} array of C's {@code struct visuals_flicker}, which is indexed by
     * {@code cycle_index * colors_per_cycle + color_index}; a key with no entry corresponds to a
     * row of C's table that was never written (all zero, so {@code COLOUR_DARK}). The map is read
     * only after construction and is never copied or modified by this class.
     *
     * <p>Field byAttr coded before 261108, commented in full on 261108.
     */
    private final Map<ColourEnum, ColourCycle> byAttr;

    /**
     * Wrap an already-built base-attribute-to-cycle map. Ownership of the map passes to the table.
     * Stands in for C's {@code visuals_flicker_new()} and the writes of
     * {@code visuals_flicker_set_color()}, both of which the assembler has already carried out by
     * the time this constructor runs.
     *
     * <p>Function FlickerTable(Map) coded before 261108, commented in full on 261108.
     *
     * @param byAttr the base-attribute-keyed cycles, as assembled from the {@code flicker:} blocks
     */
    public FlickerTable(Map<ColourEnum, ColourCycle> byAttr) {
        this.byAttr = byAttr;
    }

    /**
     * Look up the colour a flickering monster of the given base attribute shows at a given frame.
     * Java port of {@code visuals_flicker_get_attr_for_frame()} ({@code ui-visuals.c}), together with
     * the {@code visuals_flicker_get_color()} read it ends in.
     * <p>
     * C computes {@code color_index = frame % colors_per_cycle} and returns the byte at
     * {@code cycles[selection_attr * colors_per_cycle + color_index]}. Here the cycle for
     * {@code name} is fetched and indexed with {@code floorMod(frame, cycle.getSize())}, which is the
     * same arithmetic because every stored cycle is three steps wide. {@code floorMod} differs from
     * C's unsigned {@code %} only for a negative {@code frame}, which C cannot pass: its parameter is
     * a {@code size_t} fed from a {@code uint8_t} counter.
     * <p>
     * <strong>Absent key.</strong> A base attribute no {@code flicker:} block set answers
     * {@link ColourEnum#COLOUR_DARK}, matching C's zero-filled row. C's {@code BASIC_COLORS} sentinel
     * is returned only for a missing table or an attribute of {@code MAX_COLORS} or more, neither of
     * which exists in this port. For example, with the shipped {@code r} block of red, light red,
     * light pink, frames 0, 1, 2, 3 give red, light red, light pink, red; a base colour with no block
     * gives dark at every frame.
     *
     * <p>Function getAttr(ColourEnum, int) coded before 261108, commented in full on 261108.
     *
     * @param name  the monster's static base colour, the key the {@code flicker:} block was filed under
     * @param frame the global animation counter; any non-negative value, wrapped onto the cycle
     * @return the cycle's colour at {@code frame % 3}, or {@link ColourEnum#COLOUR_DARK} if
     * {@code name} has no cycle
     */
    public ColourEnum getAttr(ColourEnum name, int frame) {
        ColourCycle cycle = byAttr.get(name);
        if (cycle == null) return ColourEnum.COLOUR_DARK;
        
        frame = Math.floorMod(frame, cycle.getSize());
        return cycle.getColour(frame);
    }
}
