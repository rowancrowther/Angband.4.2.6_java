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
 * {@code struct init_module} ({@code init.h}), which pairs a {@code name}
 * with an {@code init}/{@code cleanup} function pointer so each subsystem can
 * be brought up and torn down uniformly.
 * <p>
 * In C, the game's full roster of modules is a static array,
 * {@code modules[]} ({@code init.c}) - one entry per subsystem, terminated by
 * a {@code NULL} sentinel instead of a length. {@code init_angband()} and
 * {@code cleanup_angband()} each walk that array in the same forward,
 * declaration order (never reversed), calling every entry's {@code init}/
 * {@code cleanup} in turn. {@code cleanup_angband()} also releases the chunk
 * list before its module loop and the main cave after it, so a module's
 * {@code cleanup} runs with the dungeon chunks already freed.
 * Ordering matters: several modules depend on an earlier one already having
 * run (e.g. the comment against {@code ui_visuals_module} in the array notes
 * it must load before the monster and object modules).
 * <p>
 * One divergence from the C original worth knowing before implementing this
 * interface: {@code struct init_module}'s function pointers are individually
 * optional - both loops in {@code init.c} guard each call with
 * {@code if (modules[i]->init)} / {@code if (modules[i]->cleanup)}, so a C
 * module may legitimately supply only one of the pair. In 4.2.6 every
 * module in {@code modules[]} supplies both, but some supply a pair half that
 * does nothing (see {@code ui-player.c}'s {@code init_ui_player()}, whose whole
 * body is a comment saying it is lazily initialized). Java interface methods
 * carry no optionality, so an implementor with nothing to do for one half of
 * the pair still needs a method - simply with an empty body - rather than
 * omitting it.
 * <p>
 * Not yet ported: the {@code modules[]} roster itself and the two loops that
 * walk it. This interface is only the per-module contract; its one
 * implementor so far is {@code Quark}.
 * <p>Class AngbandModule coded before 260914, commented in full on 261015.
 *
 * @author Rowan Crowther
 */
public interface AngbandModule {
    /**
     * Returns this module's display name - the port of reading C's
     * {@code init_module.name} ({@code init.h}).
     * <p>
     * In the C original this field is set once per module, as a designated
     * initializer (e.g. {@code .name = "ui-player"}), and is never read back:
     * neither module loop in {@code init.c} touches it. It exists purely as a
     * diagnostic label rather than a key used to look modules up. (The
     * {@code name} fields in {@code main.c} and {@code sound-core.c} belong to
     * different structs.) Nothing in this interface requires the returned
     * value to be unique across modules or stable across repeated
     * {@link #init()}/{@link #cleanup()} cycles, though implementors are free
     * to make either guarantee.
     * <p>Function getName coded before 260914, commented in full on 261015.
     *
     * @return the human-readable name of this module
     */
    String getName();

    /**
     * Allocate and prepare this module's state, making it ready for use -
     * the port of calling C's {@code init_module.init} function pointer
     * ({@code init.h}).
     * <p>
     * In the running game this is invoked once per module, in the
     * declaration order of the C {@code modules[]} array, during
     * {@code init_angband()} ({@code init.c}), after the game constants are
     * set up and before the monster and object lists are initialized.
     * Implementors whose C counterpart has nothing to allocate should still
     * provide a method with an empty body, per the class-level note on
     * optionality.
     * <p>Function init coded before 260914, commented in full on 261015.
     */
    void init();

    /**
     * Release this module's resources so it can be safely discarded or
     * re-initialized - the port of calling C's {@code init_module.cleanup}
     * function pointer ({@code init.h}).
     * <p>
     * In the running game this is invoked once per module, in the same
     * forward, declaration order as {@link #init()} - not reversed - during
     * {@code cleanup_angband()} ({@code init.c}), after the chunk list has
     * been freed and before the event handlers and main cave are. Implementors
     * whose C counterpart has nothing to free should still provide a method
     * with an empty body, per the class-level note on optionality.
     * <p>Function cleanup coded before 260914, commented in full on 261015.
     */
    void cleanup();
}