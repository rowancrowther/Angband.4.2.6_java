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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.playerclass.ClassSpellAssembler;
import uk.co.jackoftradesltd.backend.parser.playerclass.ClassSpellParseRecord;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link MagicSpell}, the port of C's {@code struct class_spell} ({@code player.h}).
 *
 * <p><b>The argument order is the thing most likely to go wrong.</b> The struct and the data file's
 * {@code spell:name:level:mana:fail:exp} line both run level, mana, fail, exp, but the Java
 * constructor takes level, fail, mana, exp. Two ints swap without a compile error, so these tests
 * pin each member to the value the shipped {@code class.txt} gives it: Magic Missile is level 1, mana
 * 1, fail 22, exp 4, and Light Room is level 1, mana 2, fail 26, exp 4 (the second spell separates
 * mana from level, since Magic Missile's first two figures are both 1).
 *
 * <p>The realm is the other branch. C copies {@code book->realm} onto the spell as
 * {@code parse_class_spell} ({@code init.c}) reads the spell line, so before that copy the
 * zero-filled struct holds a null pointer and afterwards the spell and the book hold the same one.
 * The Java spell has no realm getter yet, so the fields are read by reflection.
 *
 * <p>Class MagicSpellTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class MagicSpellTest {

    /**
     * Reads a private member of a spell. The class has no getters for them yet.
     *
     * @param spell the spell to read
     * @param name  the field name
     * @return the field's value
     * @throws ReflectiveOperationException if the field does not exist
     */
    private static Object read(MagicSpell spell, String name) throws ReflectiveOperationException {
        Field f = MagicSpell.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(spell);
    }

    /**
     * A realm distinguishable from another by name and by identity.
     *
     * @param name the realm's name
     * @return the realm
     */
    private static MagicRealm realm(String name) {
        return new MagicRealm(name, Stats.STAT_INT, "cast", "spell", TValue.TV_MAGIC_BOOK);
    }

    /**
     * One {@code spell:} record in the file's own order, with no effects.
     */
    private static ClassSpellParseRecord record(String name, String level, String mana, String fail, String exp) {
        return new ClassSpellParseRecord(name, level, mana, fail, "", List.of(), exp, 1);
    }

    /**
     * Class MagicSpellTest, constructor. Each argument lands in its own member.
     */
    @Test
    @DisplayName("constructor puts level, fail, mana and exp in their own members")
    void constructorMapsEveryMember() throws ReflectiveOperationException {
        List<Effect> effects = new ArrayList<>();
        MagicSpell spell = new MagicSpell("Light Room", 1, 26, 2, 4, effects, "Lights up the room.");

        assertEquals("Light Room", spell.getSpellName());
        assertEquals("Light Room", read(spell, "spellName"));
        assertEquals(1, read(spell, "level"));
        assertEquals(26, read(spell, "fail"));
        assertEquals(2, read(spell, "mana"));
        assertEquals(4, read(spell, "exp"));
        assertEquals("Lights up the room.", read(spell, "spellDescription"));
    }

    /**
     * Class MagicSpellTest, constructor. The effect list is stored as given, not copied.
     */
    @Test
    @DisplayName("constructor keeps the caller's effect list by reference")
    void effectListIsNotCopied() throws ReflectiveOperationException {
        List<Effect> effects = new ArrayList<>();
        MagicSpell spell = new MagicSpell("Phase Door", 2, 22, 2, 4, effects, "");

        assertSame(effects, read(spell, "effects"));
    }

    /**
     * Class MagicSpellTest, realm. A new spell has no realm, as C's zero-filled struct has none.
     */
    @Test
    @DisplayName("a new spell has no realm until one is set")
    void realmStartsNull() throws ReflectiveOperationException {
        MagicSpell spell = new MagicSpell("Magic Missile", 1, 22, 1, 4, List.of(), "");

        assertNull(read(spell, "realm"));
    }

    /**
     * Class MagicSpellTest, setRealm. The spell holds the very object it was given.
     */
    @Test
    @DisplayName("setRealm stores the same realm object, as C copies the book's pointer")
    void setRealmStoresTheSameObject() throws ReflectiveOperationException {
        MagicRealm arcane = realm("arcane");
        MagicSpell spell = new MagicSpell("Magic Missile", 1, 22, 1, 4, List.of(), "");

        spell.setRealm(arcane);

        assertSame(arcane, read(spell, "realm"));
    }

    /**
     * Class MagicSpellTest, setRealm. A later call replaces the earlier one, and null clears it.
     */
    @Test
    @DisplayName("setRealm replaces an earlier realm, and accepts null")
    void setRealmReplacesAndAcceptsNull() throws ReflectiveOperationException {
        MagicRealm arcane = realm("arcane");
        MagicRealm divine = realm("divine");
        MagicSpell spell = new MagicSpell("Magic Missile", 1, 22, 1, 4, List.of(), "");

        spell.setRealm(arcane);
        spell.setRealm(divine);
        assertSame(divine, read(spell, "realm"));

        spell.setRealm(null);
        assertNull(read(spell, "realm"));
    }

    /**
     * Class MagicSpellTest, setRealm. Spells that share a book share its realm object.
     */
    @Test
    @DisplayName("two spells given one book's realm hold the same object")
    void spellsOfOneBookShareTheRealm() throws ReflectiveOperationException {
        MagicRealm arcane = realm("arcane");
        MagicSpell first = new MagicSpell("Magic Missile", 1, 22, 1, 4, List.of(), "");
        MagicSpell second = new MagicSpell("Light Room", 1, 26, 2, 4, List.of(), "");

        for (MagicSpell s : List.of(first, second)) {
            s.setRealm(arcane);
        }

        assertSame(read(first, "realm"), read(second, "realm"));
    }

    /**
     * Class MagicSpellTest, ClassSpellAssembler. The file's level:mana:fail:exp order reaches the
     * matching members, which is where a swap would show.
     */
    @Test
    @DisplayName("ClassSpellAssembler maps the file's level:mana:fail:exp onto the right members")
    void assemblerKeepsFileOrder() throws ReflectiveOperationException {
        List<String> errors = new ArrayList<>();
        List<MagicSpell> spells = new ClassSpellAssembler().assemble(List.of(
                record("Magic Missile", "1", "1", "22", "4"),
                record("Light Room", "1", "2", "26", "4"),
                record("Find Traps, Doors & Stairs", "1", "1", "20", "2"),
                record("Phase Door", "2", "2", "22", "4")), errors);

        assertTrue(errors.isEmpty(), errors.toString());
        assertEquals(4, spells.size());

        MagicSpell lightRoom = spells.get(1);
        assertEquals("Light Room", lightRoom.getSpellName());
        assertEquals(1, read(lightRoom, "level"));
        assertEquals(2, read(lightRoom, "mana"));
        assertEquals(26, read(lightRoom, "fail"));
        assertEquals(4, read(lightRoom, "exp"));

        MagicSpell phaseDoor = spells.get(3);
        assertEquals(2, read(phaseDoor, "level"));
        assertEquals(2, read(phaseDoor, "mana"));
        assertEquals(22, read(phaseDoor, "fail"));
        assertEquals(4, read(phaseDoor, "exp"));

        MagicSpell findTraps = spells.get(2);
        assertEquals("Find Traps, Doors & Stairs", findTraps.getSpellName());
        assertEquals(20, read(findTraps, "fail"));
        assertEquals(2, read(findTraps, "exp"));
    }

    /**
     * Class MagicSpellTest, ClassSpellAssembler. The assembler leaves the realm to the book assembler.
     */
    @Test
    @DisplayName("ClassSpellAssembler leaves the realm null for the book assembler to set")
    void assemblerDoesNotSetTheRealm() throws ReflectiveOperationException {
        List<MagicSpell> spells = new ClassSpellAssembler().assemble(
                List.of(record("Magic Missile", "1", "1", "22", "4")), new ArrayList<>());

        assertNull(read(spells.getFirst(), "realm"));
    }

    /**
     * Class MagicSpellTest, ClassSpellAssembler. A malformed scalar drops the spell and reports it,
     * and the spells either side of it survive in file order.
     */
    @Test
    @DisplayName("ClassSpellAssembler drops a spell with a malformed scalar and keeps its neighbours")
    void malformedScalarDropsOnlyThatSpell() {
        List<String> errors = new ArrayList<>();
        List<MagicSpell> spells = new ClassSpellAssembler().assemble(List.of(
                record("Magic Missile", "1", "1", "22", "4"),
                record("Broken", "1", "x", "22", "4"),
                record("Phase Door", "2", "2", "22", "4")), errors);

        assertEquals(1, errors.size());
        assertEquals(2, spells.size());
        assertEquals("Magic Missile", spells.get(0).getSpellName());
        assertEquals("Phase Door", spells.get(1).getSpellName());
    }
}
