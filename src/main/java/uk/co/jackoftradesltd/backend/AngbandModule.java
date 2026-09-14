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

package uk.co.jackoftradesltd.backend;

/**
 * Common lifecycle contract for the game's pluggable subsystems (quark tables,
 * data stores, etc.). This is the Java port of the C original's
 * {@code struct init_module} ({@code src/init.h:214-218}), which pairs a
 * {@code name} with an {@code init_*}/{@code cleanup_*} function pointer so
 * each subsystem can be brought up and torn down uniformly.
 * <p>
 * In C, the game's full roster of modules is a static array,
 * {@code modules[]} ({@code src/init.c:4445-4461}) - one entry per subsystem,
 * terminated by a {@code NULL} sentinel instead of a length. {@code init_angband}
 * and {@code cleanup_angband} each walk that array in the same forward,
 * declaration order (never reversed) calling every entry's {@code init}/
 * {@code cleanup} in turn ({@code src/init.c:4486-4488}, {@code 4519-4521}).
 * Ordering matters: several modules depend on an earlier one already having
 * run (e.g. the comment against {@code ui_visuals_module} in the array notes
 * it must load before the monster and object modules).
 * <p>
 * One divergence from the C original worth knowing before implementing this
 * interface: {@code struct init_module}'s function pointers are individually
 * optional - both loops in {@code init.c} guard each call with
 * {@code if (modules[i]->init)} / {@code if (modules[i]->cleanup)}, so a C
 * module may legitimately supply only one of the pair (see
 * {@code ui-player.c}'s {@code init_ui_player}, whose whole body is a comment
 * saying there's nothing to do). Java interface methods carry no such
 * optionality, so an implementor with nothing to do for one half of the pair
 * still needs a method - simply with an empty body - rather than omitting it.
 * <p>Class AngbandModule coded before 2026-09-14, commented in full on 2026-09-14.
 *
 * @author Rowan Crowther
 */
public interface AngbandModule {
    /**
     * Returns this module's display name - the port of reading C's
     * {@code init_module.name} ({@code src/init.h:215}).
     * <p>
     * In the C original this field is set once, as a struct literal, and
     * never read back anywhere else in {@code init.c}; it exists purely as a
     * diagnostic label rather than a key used to look modules up. Nothing in
     * this interface requires the returned value to be unique across
     * modules or stable across repeated {@link #init()}/{@link #cleanup()}
     * cycles, though implementors are free to make either guarantee.
     * <p>Function getName commented in full on 2026-09-14.
     *
     * @return the human-readable name of this module
     */
    String getName();

    /**
     * Allocate and prepare this module's state, making it ready for use -
     * the port of calling C's {@code init_module.init} function pointer
     * ({@code src/init.h:216}).
     * <p>
     * In the running game this is invoked once per module, in the
     * declaration order of the C {@code modules[]} array, during
     * {@code init_angband} ({@code src/init.c:4486-4488}). Implementors
     * whose C counterpart has nothing to allocate should still provide a
     * method with an empty body, per the class-level note on optionality.
     * <p>Function init commented in full on 2026-09-14.
     */
    void init();

    /**
     * Release this module's resources so it can be safely discarded or
     * re-initialised - the port of calling C's {@code init_module.cleanup}
     * function pointer ({@code src/init.h:217}).
     * <p>
     * In the running game this is invoked once per module, in the same
     * forward, declaration order as {@link #init()} - not reversed - during
     * {@code cleanup_angband} ({@code src/init.c:4519-4521}). Implementors
     * whose C counterpart has nothing to free should still provide a method
     * with an empty body, per the class-level note on optionality.
     * <p>Function cleanup commented in full on 2026-09-14.
     */
    void cleanup();
}