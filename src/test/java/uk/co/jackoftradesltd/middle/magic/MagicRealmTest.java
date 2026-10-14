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

package uk.co.jackoftradesltd.middle.magic;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.enums.Stats;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests for {@link MagicRealm}, the Java form of C's {@code struct magic_realm}. The expected
 * values are the four records of {@code lib/gamedata/realm.txt} and the strings C's
 * {@code tests/parse/realm.c} feeds {@code parse_realm_book_noun}, not whatever the constructor
 * happens to do.
 *
 * <p>{@link MagicRealm} has no getters for the verb, spell noun or book noun yet, so those are read
 * reflectively.
 *
 * <p>Class MagicRealmTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class MagicRealmTest {

    /**
     * Reads a private field of a realm.
     *
     * @param realm the realm
     * @param name  the field name
     * @return the field's value
     * @throws Exception if the field does not exist
     */
    private static Object field(MagicRealm realm, String name) throws Exception {
        Field f = MagicRealm.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(realm);
    }

    @Test
    void arcaneRealmKeepsEveryRealmTxtField() throws Exception {
        MagicRealm r = new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell", "magic book");

        assertEquals("arcane", r.getName());
        assertEquals(Stats.STAT_INT, r.getStat());
        assertEquals("cast", field(r, "verb"));
        assertEquals("spell", field(r, "spellNoun"));
        assertEquals("magic book", field(r, "bookNoun"));
    }

    @Test
    void divineRealmIsWisdomDriven() throws Exception {
        MagicRealm r = new MagicRealm("divine", Stats.STAT_WIS, "recite", "prayer", "prayer book");

        assertEquals(Stats.STAT_WIS, r.getStat());
        assertEquals("recite", field(r, "verb"));
        assertEquals("prayer", field(r, "spellNoun"));
        assertEquals("prayer book", field(r, "bookNoun"));
    }

    @Test
    void bookNounIsStoredVerbatimWhateverItSays() throws Exception {
        // C's tests/parse/realm.c stores "exercise manual" and "hymnal" untouched.
        assertEquals("exercise manual",
                field(new MagicRealm("gym", Stats.STAT_STR, "flex", "set", "exercise manual"), "bookNoun"));
        assertEquals("hymnal",
                field(new MagicRealm("choir", Stats.STAT_WIS, "sing", "hymn", "hymnal"), "bookNoun"));
    }

    @Test
    void nameIsKeptWithItsCaseSoLookupsDecideWhetherToFoldIt() {
        // lookup_realm folds case itself (my_stricmp); the realm must not do it for the caller.
        assertEquals("Arcane", new MagicRealm("Arcane", Stats.STAT_INT, "cast", "spell", "magic book").getName());
    }

    @Test
    void nullTextFieldsStayNullLikeC() throws Exception {
        // A zero-filled C struct leaves unset strings NULL; the constructor must not invent text.
        MagicRealm r = new MagicRealm("bare", Stats.STAT_INT, null, null, null);

        assertNull(field(r, "verb"));
        assertNull(field(r, "spellNoun"));
        assertNull(field(r, "bookNoun"));
    }

    @Test
    void everyStatTheDataFilesCanNameIsKept() {
        for (Stats stat : new Stats[]{Stats.STAT_STR, Stats.STAT_INT, Stats.STAT_WIS, Stats.STAT_DEX, Stats.STAT_CON}) {
            assertEquals(stat, new MagicRealm("r", stat, "v", "n", "b").getStat());
        }
    }
}
