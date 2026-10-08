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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the linkage of the three methods ported from {@code object_value} ({@code obj-power.c}),
 * {@code object_kind_name} and {@code obj_desc_name_format} ({@code obj-desc.c}).
 *
 * <p>All three are non-static, externally visible functions in C, and the callers still to be ported
 * live in other classes: the shop and wizard-mode code for {@code object_value}, the ignore and
 * knowledge menus for {@code object_kind_name}, and {@code object_desc} and the save code for
 * {@code obj_desc_name_format}. The behaviour of each is covered in {@code ItemObjectValueTest},
 * {@code ItemObjectKindNameTest} and {@code ItemObjectDescNameFormatTest}; this class only checks
 * that they have not slipped back to {@code private}, which the reflection those tests use would
 * hide.
 *
 * <p>Class ItemObjectNameAndValueVisibilityTest commented in full on 261008.
 */
@DisplayName("ItemObject objectValue, objectKindName and objDescNameFormat linkage")
class ItemObjectNameAndValueVisibilityTest {

    private static Method find(String name, Class<?>... types) throws NoSuchMethodException {
        return ItemObject.class.getDeclaredMethod(name, types);
    }

    @Test
    @DisplayName("objectValue is public and an instance method, as object_value is external in C")
    void objectValueIsPublic() throws NoSuchMethodException {
        int mods = find("objectValue", int.class).getModifiers();

        assertTrue(Modifier.isPublic(mods));
        assertTrue(!Modifier.isStatic(mods));
    }

    @Test
    @DisplayName("objectKindName is public and an instance method, as object_kind_name is external in C")
    void objectKindNameIsPublic() throws NoSuchMethodException {
        int mods = find("objectKindName", ObjectKind.class, boolean.class).getModifiers();

        assertTrue(Modifier.isPublic(mods));
        assertTrue(!Modifier.isStatic(mods));
    }

    @Test
    @DisplayName("objDescNameFormat is public and an instance method, as obj_desc_name_format is external in C")
    void objDescNameFormatIsPublic() throws NoSuchMethodException {
        int mods = find("objDescNameFormat", String.class, String.class, boolean.class).getModifiers();

        assertTrue(Modifier.isPublic(mods));
        assertTrue(!Modifier.isStatic(mods));
    }
}
