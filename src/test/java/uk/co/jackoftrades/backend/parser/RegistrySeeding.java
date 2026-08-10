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

package uk.co.jackoftrades.backend.parser;

import uk.co.jackoftrades.middle.game.globals.registry.DungeonRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.StatTables;
import uk.co.jackoftrades.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.UIRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.WorldRegistry;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Finds the private static store behind a registry so a test can seed it directly.
 *
 * <p>The registries hold what C keeps in file-scope globals, and expose only setters. A reader test
 * that needs another subsystem already loaded - a slay test needing monster bases, say - would
 * otherwise have to run the whole {@code GameConstants.init()} chain for one list. Instead it
 * resolves the field by name, saves the old value, drops its own in, and puts the original back in
 * an {@code @AfterAll}.
 *
 * <p>Callers name only the field, not its owner, so this searches every registry and returns the
 * first match. Field names are unique across the registries; if that ever stops being true, this
 * will quietly pick whichever class comes first in {@link #REGISTRIES} and the ambiguity should be
 * resolved by renaming rather than by reordering the list.
 *
 * @author Rowan Crowther
 */
final class RegistrySeeding {

    /**
     * Every class holding registry state a test may need to seed.
     */
    private static final List<Class<?>> REGISTRIES = List.of(
            DungeonRegistry.class,
            MiscRegistry.class,
            MonsterRegistry.class,
            ObjectRegistry.class,
            PlayerRegistry.class,
            StatTables.class,
            TerrainRegistry.class,
            UIRegistry.class,
            WorldRegistry.class);

    private RegistrySeeding() {
    }

    /**
     * Find a registry's backing field by name.
     *
     * <p>The returned field is not made accessible - the caller does that, since it also decides
     * whether it is reading, writing or both.
     *
     * @param name the field's name, as declared on whichever registry owns it
     * @return the matching field
     * @throws NoSuchFieldException if no registry declares a field with that name
     */
    static Field resolve(String name) throws NoSuchFieldException {
        for (Class<?> registry : REGISTRIES) {
            try {
                return registry.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                // Try the next registry; only the last one failing is an error.
            }
        }

        throw new NoSuchFieldException(
                "no registry in " + REGISTRIES + " declares a field named " + name);
    }
}
