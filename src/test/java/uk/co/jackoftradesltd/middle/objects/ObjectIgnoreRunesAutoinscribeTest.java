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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests {@link ObjectIgnore#runesAutoinscribe} and {@link ObjectIgnore#runeAddAutoinscription}, the
 * ports of C's {@code runes_autoinscribe} and {@code rune_add_autoinscription}
 * ({@code obj-ignore.c:217-224} and {@code obj-ignore.c:172-188}).
 *
 * <p>The case that matters most here is the one an earlier version of the port got wrong: C guards
 * every rune with {@code object_has_rune(obj, i) && player_knows_rune(p, i)}, and a version that
 * dropped the second half would spoil an unidentified property by inscribing it anyway.
 * {@link Present#knownRuneOnItsOwnIsNotEnough()} pins that the object carrying the rune is not
 * sufficient on its own — the player has to know it too.
 *
 * <p>Both methods are private, so reached by reflection rather than through their only caller,
 * {@code applyAutoinscription}. {@code runesAutoinscribe} is exercised with a single {@code ModKey}
 * rune seeded into {@link ObjectRegistry}, since the modifier branch of {@code objectHasRune} and
 * {@code knowsRune} needs nothing beyond a modifier value and a learned flag — {@code ModKey},
 * {@code CombatKey} and the rest already have their own dedicated coverage in
 * {@code ObjectKnowledgeTest}.
 *
 * <p>Class ObjectIgnoreRunesAutoinscribeTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ObjectIgnoreRunesAutoinscribeTest {

    /**
     * The rune list as it stood before this suite replaced it, restored after each test since
     * {@link ObjectRegistry#setRunes} publishes a shared, static list.
     */
    private List<Rune> savedRunes;

    /**
     * Runs {@code runesAutoinscribe} by reflection.
     *
     * @param player the player whose knowledge gates the inscription
     * @param obj    the object to inscribe
     */
    private static void runesAutoinscribe(Player player, ItemObject obj) {
        try {
            Method method = ObjectIgnore.class.getDeclaredMethod("runesAutoinscribe", Player.class,
                    ItemObject.class);
            method.setAccessible(true);
            method.invoke(null, player, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("runesAutoinscribe threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("runesAutoinscribe is no longer reachable", e);
        }
    }

    /**
     * Runs {@code runeAddAutoinscription} by reflection.
     *
     * @param obj  the object to inscribe
     * @param rune the rune whose note is being added
     */
    private static void runeAddAutoinscription(ItemObject obj, Rune rune) {
        try {
            Method method = ObjectIgnore.class.getDeclaredMethod("runeAddAutoinscription",
                    ItemObject.class, Rune.class);
            method.setAccessible(true);
            method.invoke(null, obj, rune);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("runeAddAutoinscription threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("runeAddAutoinscription is no longer reachable", e);
        }
    }

    /**
     * An item of the given type and modifiers, built with the long constructor so that
     * {@link ItemObject#setModifiers} has a live map to replace — the no-argument constructor's
     * collections are shared immutables, matching {@code ObjectKnowledgeTest}'s reasoning for doing
     * the same.
     *
     * @param modifiers the modifiers this item carries
     * @return the item, with everything else at its zero default
     */
    private static ItemObject item(Map<ObjectModifier, Integer> modifiers) {
        ItemObject obj = new ItemObject(null, null, null, null, Loc.zero, TValue.TV_RING, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
        obj.setModifiers(modifiers);
        return obj;
    }

    /**
     * A player carrying the given item knowledge. {@code itemKnowledge} is package-private on
     * {@link Player} (its own package, not this one), so it is written by reflection rather than
     * assigned directly.
     *
     * @param knowledge the item knowledge to give the player
     * @return the player
     */
    private static Player player(KnownObject knowledge) {
        Player player = new Player();
        set(player, "itemKnowledge", knowledge);
        return player;
    }

    /**
     * A modifier rune, with the given auto-inscription note.
     *
     * @param modifier the modifier this rune represents
     * @param note     the auto-inscription to give it, or {@code null} for none
     * @return the rune
     */
    private static Rune modRune(ObjectModifier modifier, String note) {
        Rune rune = new Rune(new RuneVariety.ModKey(modifier, null));
        rune.setNote(note);
        return rune;
    }

    @BeforeEach
    void saveRunes() {
        savedRunes = ObjectRegistry.getRunes();
    }

    @AfterEach
    void restoreRunes() {
        ObjectRegistry.setRunes(savedRunes);
    }

    /**
     * The two-part guard {@code runesAutoinscribe} enforces: the object must carry the rune's
     * property, and the player must have learned it. Neither alone is enough.
     */
    @Nested
    @DisplayName("runesAutoinscribe")
    class Guard {

        /**
         * The regression case: an object carrying a property the player has not identified is left
         * alone, exactly as C's {@code player_knows_rune} guard requires. Before the fix, this port
         * inscribed it anyway.
         */
        @Test
        @DisplayName("a rune the object has but the player does not know is not inscribed")
        void knownRuneOnItsOwnIsNotEnough() {
            ObjectRegistry.setRunes(List.of(modRune(ObjectModifier.OM_STR, "{str}")));
            ItemObject obj = item(Map.of(ObjectModifier.OM_STR, 2));
            Player player = player(new KnownObject());

            runesAutoinscribe(player, obj);

            assertNull(obj.getNote(), "the player hasn't identified the modifier yet");
        }

        /**
         * The mirror case: the player knows the rune, but the object does not carry it, so there is
         * nothing to tell them about.
         */
        @Test
        @DisplayName("a rune the player knows but the object does not have is not inscribed")
        void unknownToTheObjectIsNotEnough() {
            ObjectRegistry.setRunes(List.of(modRune(ObjectModifier.OM_STR, "{str}")));
            ItemObject obj = item(Map.of());
            KnownObject knowledge = new KnownObject();
            knowledge.learnModifier(ObjectModifier.OM_STR);
            Player player = player(knowledge);

            runesAutoinscribe(player, obj);

            assertNull(obj.getNote(), "the object has no strength bonus to report");
        }

        /**
         * Both halves true: the object carries the property, and the player has identified it, so
         * the rune's note is applied.
         */
        @Test
        @DisplayName("a rune the object has and the player knows is inscribed")
        void knownAndCarriedIsInscribed() {
            ObjectRegistry.setRunes(List.of(modRune(ObjectModifier.OM_STR, "{str}")));
            ItemObject obj = item(Map.of(ObjectModifier.OM_STR, 2));
            KnownObject knowledge = new KnownObject();
            knowledge.learnModifier(ObjectModifier.OM_STR);
            Player player = player(knowledge);

            runesAutoinscribe(player, obj);

            assertEquals("{str}", obj.getNote());
        }

        /**
         * Several qualifying runes accumulate onto the same object, each appended in turn — the
         * order {@link ObjectRegistry#getRunes()} lists them in, matching C walking
         * {@code 0..max_runes()} in index order.
         */
        @Test
        @DisplayName("every qualifying rune is inscribed, in registry order")
        void multipleQualifyingRunesAccumulate() {
            ObjectRegistry.setRunes(List.of(
                    modRune(ObjectModifier.OM_STR, "{str}"),
                    modRune(ObjectModifier.OM_DEX, "{dex}")));
            ItemObject obj = item(Map.of(ObjectModifier.OM_STR, 2, ObjectModifier.OM_DEX, 1));
            KnownObject knowledge = new KnownObject();
            knowledge.learnModifier(ObjectModifier.OM_STR);
            knowledge.learnModifier(ObjectModifier.OM_DEX);
            Player player = player(knowledge);

            runesAutoinscribe(player, obj);

            assertEquals("{str}{dex}", obj.getNote());
        }
    }

    /**
     * {@code runeAddAutoinscription}'s three clauses in isolation, with no registry or player
     * involved.
     */
    @Nested
    @DisplayName("runeAddAutoinscription")
    class Note {

        /**
         * A rune with no configured note is skipped outright, matching C's {@code !rune_note(i)} —
         * an object with no note stays that way.
         */
        @Test
        @DisplayName("a rune with no note leaves an unset note unset")
        void noRuneNoteLeavesUnsetNoteUnset() {
            ItemObject obj = item(Map.of());
            Rune rune = modRune(ObjectModifier.OM_STR, null);

            runeAddAutoinscription(obj, rune);

            assertNull(obj.getNote());
        }

        /**
         * And leaves an existing note untouched too — the guard returns before either branch below
         * it is reached.
         */
        @Test
        @DisplayName("a rune with no note leaves an existing note untouched")
        void noRuneNoteLeavesExistingNoteUntouched() {
            ItemObject obj = item(Map.of());
            obj.setNote("@w1");
            Rune rune = modRune(ObjectModifier.OM_STR, null);

            runeAddAutoinscription(obj, rune);

            assertEquals("@w1", obj.getNote());
        }

        /**
         * An object with no note starts from the empty string, matching C's zero-initialised
         * {@code current_note} buffer, so the rune's note becomes the whole note.
         */
        @Test
        @DisplayName("an unset note becomes exactly the rune's note")
        void unsetNoteBecomesTheRuneNote() {
            ItemObject obj = item(Map.of());
            Rune rune = modRune(ObjectModifier.OM_STR, "{str}");

            runeAddAutoinscription(obj, rune);

            assertEquals("{str}", obj.getNote());
        }

        /**
         * An existing note is extended, not replaced.
         */
        @Test
        @DisplayName("an existing note has the rune's note appended")
        void existingNoteHasRuneNoteAppended() {
            ItemObject obj = item(Map.of());
            obj.setNote("@w1");
            Rune rune = modRune(ObjectModifier.OM_STR, "{str}");

            runeAddAutoinscription(obj, rune);

            assertEquals("@w1{str}", obj.getNote());
        }

        /**
         * A note that already contains the rune's text is left alone, the same substring test as
         * C's {@code strstr(quark_str(obj->note), quark_str(rune_note(i)))} — not re-added even
         * though it is not the whole note.
         */
        @Test
        @DisplayName("a note already containing the rune's text is not duplicated")
        void alreadyPresentNoteIsNotDuplicated() {
            ItemObject obj = item(Map.of());
            obj.setNote("@w1{str}");
            Rune rune = modRune(ObjectModifier.OM_STR, "{str}");

            runeAddAutoinscription(obj, rune);

            assertEquals("@w1{str}", obj.getNote());
        }
    }
}
