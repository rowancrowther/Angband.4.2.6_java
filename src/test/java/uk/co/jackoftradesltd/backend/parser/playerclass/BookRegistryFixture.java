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

package uk.co.jackoftradesltd.backend.parser.playerclass;

import uk.co.jackoftradesltd.backend.parser.ObjectBaseReader;
import uk.co.jackoftradesltd.backend.parser.RealmReader;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Seeds the registries that {@link ClassSpellBookAssembler} resolves a good book against, for the
 * tests that need a book to pass: the magic realms and object bases from the real
 * {@code realm.txt} and {@code object_base.txt}, and an object-kind table holding one placeholder
 * kind so that it counts as loaded.
 *
 * <p>The registries expose only setters for some of these, and registering a book moves the base's
 * sval count, so a test calls {@link #seed()} before each test and {@link #save()} and
 * {@link #restore(Object[])} around the class so that no state leaks to other suites.
 *
 * <p>Class BookRegistryFixture coded on 261028, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
final class BookRegistryFixture {

    static final String REALM_FILE = "lib/gamedata/realm.txt";
    static final String OBJECT_BASE_FILE = "lib/gamedata/object_base.txt";

    private BookRegistryFixture() {
    }

    /** Reads the four fields {@link #seed()} replaces, in the order {@link #restore(Object[])} takes them. */
    static Object[] save() throws Exception {
        return new Object[]{get("realms"), get("objectBases"), get("objectKinds"), get("kindsByTvalSval")};
    }

    static void restore(Object[] saved) throws Exception {
        set("realms", saved[0]);
        set("objectBases", saved[1]);
        set("objectKinds", saved[2]);
        set("kindsByTvalSval", saved[3]);
    }

    /** Loads fresh realms and bases, and a kind table holding only a placeholder. */
    static void seed() throws Exception {
        set("realms", new RealmReader().parse(REALM_FILE));
        set("objectBases", new ObjectBaseReader().parse(OBJECT_BASE_FILE));
        List<ObjectKind> kinds = new ArrayList<>();
        kinds.add(new ObjectKind());
        set("objectKinds", kinds);
        set("kindsByTvalSval", new HashMap<>());
    }

    static Object get(String name) throws Exception {
        Field f = fieldOf(name);
        f.setAccessible(true);
        return f.get(null);
    }

    static void set(String name, Object value) throws Exception {
        Field f = fieldOf(name);
        f.setAccessible(true);
        f.set(null, value);
    }

    private static Field fieldOf(String name) throws NoSuchFieldException {
        if (name.equals("realms"))
            return PlayerRegistry.class.getDeclaredField(name);
        return ObjectRegistry.class.getDeclaredField(name);
    }
}
