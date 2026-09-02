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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.Curse;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.Slay;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ObjectRegistry} itself, rather than the readers that fill it: the sval allocation and
 * indexing {@link ObjectRegistry#addObjectKind} performs, what {@link ObjectRegistry#reset()} does
 * and deliberately does not do, the several {@code lookupObjectKind} overloads (including the
 * {@code lookup_sval} reference form C uses in its data files), and the uninitialised-access guards
 * every lookup opens with.
 *
 * <p>The registry is global static state that the reader suites also load, so {@link #snapshot()}
 * saves every field this class touches and {@link #restore()} puts it back, with {@link #isolate()}
 * giving each test an empty registry to work in. Fixtures are built by hand rather than parsed:
 * these are the registry's own rules, and they should hold for any kind, not only the shipped ones.
 *
 * <p>{@link ObjectKind} has no setters for {@code name}/{@code base}/{@code tValue}/
 * {@code sValueName} and only a 35-argument constructor that reaches them, so {@link #kind} sets
 * those four reflectively.
 *
 * @author Rowan Crowther
 */
class ObjectRegistryTest {

    /**
     * Every registry field these tests write to, saved and restored around the class.
     */
    private static final List<String> TOUCHED = List.of(
            "objectKinds", "kindsByTvalSval", "objectBases", "objectBaseKindMax",
            "slays", "curses", "brands", "activations");

    private static final Map<String, Object> saved = new HashMap<>();

    private ObjectBase swordBase;
    private ObjectBase bookBase;

    @BeforeAll
    static void snapshot() throws Exception {
        for (String name : TOUCHED) {
            saved.put(name, get(name));
        }
    }

    @AfterAll
    static void restore() throws Exception {
        for (String name : TOUCHED) {
            set(name, saved.get(name));
        }
    }

    private static Field field(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    // ---- fixture + reflection helpers ------------------------------------

    private static Object get(String name) throws Exception {
        return field(name).get(null);
    }

    private static void set(String name, Object value) throws Exception {
        field(name).set(null, value);
    }

    private static ObjectBase base(TValue tval, String name) {
        return new ObjectBase(tval, name, ColourEnum.COLOUR_WHITE,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), -1, -1);
    }

    /**
     * An object kind carrying only what the registry reads: its name, its base, its tval and its
     * sval name.
     */
    private static ObjectKind kind(String name, ObjectBase base, String svalName) throws Exception {
        ObjectKind k = new ObjectKind();
        for (Map.Entry<String, Object> entry : Map.of(
                        "name", name, "base", base, "tValue", base.gettVal(), "sValueName", svalName)
                .entrySet()) {
            Field f = ObjectKind.class.getDeclaredField(entry.getKey());
            f.setAccessible(true);
            f.set(k, entry.getValue());
        }
        return k;
    }

    /**
     * A slay carrying only its code and name. The code is not free-form: {@link Slay}'s constructor
     * splits it into a race flag and a level, so it has to read {@code FLAG_level}.
     */
    private static Slay slay(String code, String name) {
        return new Slay(code, name, null, null, null, null, 0, 0, 0);
    }

    private static Brand brand(String code, String name) {
        return new Brand(code, name, null, null, null, 0, 0, 0);
    }

    private static Curse curse(String name) {
        return new Curse(name, null, 0, null, null, null, null, 0, 0, 0, null, null, null, null);
    }

    private static Activation activation(String name) {
        return new Activation(name, 0, false, 0, 0, null, null, null);
    }

    /**
     * Each test starts from an empty registry with two bases loaded, so nothing another suite left
     * behind can satisfy a lookup by accident.
     */
    @BeforeEach
    void isolate() throws Exception {
        set("objectKinds", new ArrayList<ObjectKind>());
        set("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        swordBase = base(TValue.TV_SWORD, "sword");
        bookBase = base(TValue.TV_MAGIC_BOOK, "magic book");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(swordBase, bookBase)));
        set("slays", null);
        set("curses", null);
        set("brands", null);
        set("activations", null);
    }

    // ---- addObjectKind: the registration choke-point ----------------------

    /**
     * Svals are 1-based and allocated per <em>base</em>, not globally: C's {@code sval} is an item's
     * position within its own tval, and the data files address kinds that way
     * ({@code tval:sword} + {@code sval:2}). The counter lives on the base, so two bases number
     * independently from 1.
     */
    @Test
    void svalsAreAllocatedFromOnePerBase() throws Exception {
        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        ObjectKind rapier = kind("Rapier", swordBase, "Rapier");
        ObjectKind magicBook = kind("Magic Book", bookBase, "Magic Book");

        ObjectRegistry.addObjectKind(dagger);
        ObjectRegistry.addObjectKind(rapier);
        ObjectRegistry.addObjectKind(magicBook);

        assertEquals(1, dagger.getsVal());
        assertEquals(2, rapier.getsVal());
        assertEquals(1, magicBook.getsVal(), "a second base numbers from 1 again");
        assertEquals(2, swordBase.getNumSvals(), "the base carries the running count");
        assertEquals(1, bookBase.getNumSvals());
    }

    /**
     * The kind index is the kind's position in the whole table, across bases - the identity C uses
     * for {@code k_info[]} and stores in savefiles - so unlike the sval it never restarts.
     */
    @Test
    void theKindIndexIsThePositionInTheWholeTable() throws Exception {
        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        ObjectKind magicBook = kind("Magic Book", bookBase, "Magic Book");
        ObjectKind rapier = kind("Rapier", swordBase, "Rapier");

        ObjectRegistry.addObjectKind(dagger);
        ObjectRegistry.addObjectKind(magicBook);
        ObjectRegistry.addObjectKind(rapier);

        assertEquals(0, dagger.getKindIndex());
        assertEquals(1, magicBook.getKindIndex(), "indices do not restart per base");
        assertEquals(2, rapier.getKindIndex());
        assertEquals(3, ObjectRegistry.getObjectKindCount());
    }

    /**
     * The point of routing every registration through one method: the table and the
     * {@code kindsByTvalSval} index cannot drift apart, so every registered kind is reachable by the
     * (tval, sval) pair it was given.
     */
    @Test
    void everyRegisteredKindIsReachableThroughTheIndex() throws Exception {
        for (String name : List.of("Dagger", "Rapier", "Short Sword")) {
            ObjectRegistry.addObjectKind(kind(name, swordBase, name));
        }
        ObjectRegistry.addObjectKind(kind("Magic Book", bookBase, "Magic Book"));

        for (ObjectKind k : ObjectRegistry.getObjectKinds()) {
            assertSame(k, ObjectRegistry.lookupObjectKind(k.gettValue(), k.getsVal()),
                    () -> k.getName() + " is in the table but not in the index");
        }
    }

    /**
     * The index is keyed by tval while svals are counted per base, so the two agree only while no
     * two bases share a tval. That holds in the shipped data - C's {@code object_base.txt} has one
     * record per tval - but nothing in the registry enforces it, and a second base on an existing
     * tval silently displaces the first base's kinds from the index. Pinned here as the assumption
     * {@link ObjectRegistry#addObjectKind} rests on rather than as desired behaviour.
     */
    @Test
    void twoBasesSharingATvalCollideInTheIndex() throws Exception {
        ObjectBase secondSwordBase = base(TValue.TV_SWORD, "another sword base");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(swordBase, secondSwordBase)));

        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        ObjectKind impostor = kind("Impostor", secondSwordBase, "Impostor");
        ObjectRegistry.addObjectKind(dagger);
        ObjectRegistry.addObjectKind(impostor);

        assertEquals(1, dagger.getsVal());
        assertEquals(1, impostor.getsVal(), "each base numbers from 1 in ignorance of the other");
        assertSame(impostor, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, 1),
                "the later registration wins the (tval, sval) slot");
        assertEquals(2, ObjectRegistry.getObjectKindCount(), "though both are still in the table");
    }

    // ---- reset -----------------------------------------------------------

    /**
     * {@code reset} clears the table and the index together. Clearing one alone would leave the
     * other holding kinds that no longer exist, which is why they are cleared in the same method.
     */
    @Test
    void resetClearsTheTableAndTheIndexTogether() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));

        ObjectRegistry.reset();

        assertEquals(0, ObjectRegistry.getObjectKindCount());
        assertTrue(ObjectRegistry.getObjectKinds().isEmpty());
        assertTrue(ObjectRegistry.getKindsByTvalSval().isEmpty(), "a stale index outlives the kinds");
    }

    /**
     * What {@code reset} does <em>not</em> do: the sval counters live on the {@link ObjectBase}
     * objects, not in the registry, so resetting alone leaves them where they were and the next
     * registration continues from 2. That is harmless only because {@code GameConstants.init()}
     * reloads {@code object_base.txt} after the reset, replacing every base with a fresh one - so
     * this test is really pinning that ordering dependency.
     */
    @Test
    void resetLeavesTheBasesSvalCountersAlone() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));

        ObjectRegistry.reset();
        ObjectKind afterReset = kind("Rapier", swordBase, "Rapier");
        ObjectRegistry.addObjectKind(afterReset);

        assertEquals(2, afterReset.getsVal(),
                "the counter is on the base, so only reloading the bases restarts it");
        assertEquals(0, afterReset.getKindIndex(), "while the table index does restart");
    }

    // ---- the table's accessors -------------------------------------------

    /**
     * {@code getObjectKinds} hands back a live unmodifiable view - it tracks later registrations,
     * and cannot be written through. Note this is the opposite of
     * {@link ObjectRegistry#getRunes()}, which hands back an immutable list that a later
     * {@code setRunes} replaces rather than updates; the two are worth reading side by side before
     * relying on either, since neither can be told from the other by its signature.
     */
    @Test
    void getObjectKindsIsAnUnmodifiableViewThatTracksTheTable() throws Exception {
        List<ObjectKind> view = ObjectRegistry.getObjectKinds();
        assertTrue(view.isEmpty());

        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));

        assertEquals(1, view.size(), "the view follows the registry");
        assertThrows(UnsupportedOperationException.class, view::clear);
    }

    /**
     * {@code setObjectKinds} replaces the table without touching the index, so a kind installed that
     * way is invisible to the (tval, sval) lookup - the reason its Javadoc points callers at
     * {@link ObjectRegistry#addObjectKind} instead. No production code calls it.
     */
    @Test
    void setObjectKindsBypassesTheIndex() throws Exception {
        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        dagger.setsVal(1);

        ObjectRegistry.setObjectKinds(new ArrayList<>(List.of(dagger)));

        assertEquals(1, ObjectRegistry.getObjectKindCount());
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, 1),
                "the index was never told about it");
        assertSame(dagger, ObjectRegistry.lookupObjectKind("Dagger"),
                "though the name lookup scans the table and does find it");
    }

    /**
     * {@code updateObjectBaseKindMax} freezes the ordinary-kind ceiling at whatever the table holds
     * when it is called - {@code ObjectDataLoader} calls it after the file-loaded kinds and before
     * the artifact kinds are synthesised into the same table, which is what makes the later ones
     * distinguishable from the ordinary ones.
     */
    @Test
    void updateObjectBaseKindMaxRecordsTheCountAtTheMomentItIsCalled() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));
        ObjectRegistry.addObjectKind(kind("Rapier", swordBase, "Rapier"));

        ObjectRegistry.updateObjectBaseKindMax();
        ObjectRegistry.addObjectKind(kind("Synthesised", swordBase, "Synthesised"));

        assertEquals(2, ObjectRegistry.getObjectBaseKindMax());
        assertEquals(3, ObjectRegistry.getObjectKindCount(), "later kinds do not move the ceiling");
    }

    // ---- lookupObjectKind ------------------------------------------------

    @Test
    void theNumericLookupMissesReturnNullRatherThanThrowing() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));

        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, 99), "unknown sval");
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_RING, 1), "tval not in the index");
    }

    @Test
    void theNameLookupIsExactAndCaseSensitive() throws Exception {
        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        ObjectRegistry.addObjectKind(dagger);

        assertSame(dagger, ObjectRegistry.lookupObjectKind("Dagger"));
        assertNull(ObjectRegistry.lookupObjectKind("dagger"),
                "unlike the sval-name lookup, this one does not fold case");
        assertNull(ObjectRegistry.lookupObjectKind("Not a kind"));
    }

    /**
     * The reference form ports C's {@code lookup_sval}: an all-digits reference is a literal sval,
     * anything else is matched against the sval <em>name</em>, case-insensitively.
     */
    @Test
    void theReferenceLookupTakesEitherAnSvalOrAnSvalName() throws Exception {
        ObjectKind dagger = kind("Dagger", swordBase, "Dagger");
        ObjectKind rapier = kind("Rapier", swordBase, "Rapier");
        ObjectRegistry.addObjectKind(dagger);
        ObjectRegistry.addObjectKind(rapier);

        assertSame(rapier, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "2"));
        assertSame(rapier, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, " 2 "),
                "the numeric form is trimmed first");
        assertSame(dagger, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "Dagger"));
        assertSame(dagger, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "dAgGeR"),
                "the name form folds case");
    }

    /**
     * The two legs fail independently: a number that no kind carries does not fall back to a name
     * match, and the name leg is bounded by the tval as well as the name.
     */
    @Test
    void theReferenceLookupReturnsNullWhenNeitherLegMatches() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));
        ObjectRegistry.addObjectKind(kind("Magic Book", bookBase, "Magic Book"));

        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "99"), "no such sval");
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "Magic Book"),
                "the right name, but under the wrong tval");
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "no such kind"));
    }

    /**
     * The tval fan-out returns a fresh list, so a caller that sorts or filters it in place - the
     * natural thing to do with a result like this - cannot disturb the registry.
     */
    @Test
    void theTvalFanOutReturnsAModifiableCopyAndAnEmptyListForAMiss() throws Exception {
        ObjectRegistry.addObjectKind(kind("Dagger", swordBase, "Dagger"));
        ObjectRegistry.addObjectKind(kind("Rapier", swordBase, "Rapier"));
        ObjectRegistry.addObjectKind(kind("Magic Book", bookBase, "Magic Book"));

        List<ObjectKind> swords = ObjectRegistry.lookupObjectKind(TValue.TV_SWORD);
        assertEquals(List.of("Dagger", "Rapier"), swords.stream().map(ObjectKind::getName).toList());

        swords.clear();
        assertEquals(3, ObjectRegistry.getObjectKindCount(), "the copy is the caller's to mangle");

        List<ObjectKind> none = ObjectRegistry.lookupObjectKind(TValue.TV_RING);
        assertNotNull(none, "a miss is an empty list, never null");
        assertTrue(none.isEmpty());
    }

    // ---- the uninitialised-access guards ---------------------------------

    /**
     * Every kind lookup refuses to answer against an empty table rather than reporting "no such
     * kind": at the point these are called the data is meant to be loaded, so an empty table is a
     * startup-ordering bug and a null answer would bury it.
     */
    @Test
    void everyKindLookupThrowsWhenTheTableIsEmpty() {
        assertThrows(IllegalStateException.class,
                () -> ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, 1));
        assertThrows(IllegalStateException.class,
                () -> ObjectRegistry.lookupObjectKind("Dagger"));
        assertThrows(IllegalStateException.class,
                () -> ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "Dagger"));
        assertThrows(IllegalStateException.class,
                () -> ObjectRegistry.lookupObjectKind(TValue.TV_SWORD));
    }

    /**
     * The other lookups guard on {@code null} instead, which is the same intent applied to lists
     * that are assigned wholesale rather than grown.
     *
     * <p>{@code objectBases} is emptied through the field rather than through
     * {@link ObjectRegistry#setObjectBases}, whose parameter is {@code @NotNull}: no caller may pass
     * null, and a test that did would be asserting a contract the registry does not offer. The other
     * lists here are already null from {@link #isolate()} for the same reason - what is under test is
     * the state "never set", which the setter is not a way to reach.
     */
    @Test
    void theOtherLookupsThrowWhenTheirListWasNeverSet() throws Exception {
        assertThrows(IllegalStateException.class, () -> ObjectRegistry.lookupSlay("SLAY_ANIMAL"));
        assertThrows(IllegalStateException.class, () -> ObjectRegistry.lookupCurse("teleportation"));
        assertThrows(IllegalStateException.class, () -> ObjectRegistry.lookupBrandCode("ACID_2"));
        assertThrows(IllegalStateException.class, () -> ObjectRegistry.lookupActivation("RING_ACID"));

        set("objectBases", null);
        assertThrows(IllegalStateException.class, () -> ObjectRegistry.lookupObjectBase("sword"));
        assertThrows(IllegalStateException.class,
                () -> ObjectRegistry.lookupObjectBase("sword", TValue.TV_SWORD));
        assertThrows(IllegalStateException.class,
                () -> ObjectRegistry.getBaseFromTVal(TValue.TV_SWORD));
    }

    /**
     * The two guards are not the same test, and the difference shows on an empty-but-loaded list: a
     * kind lookup treats empty as "not loaded" and throws, while a slay lookup treats it as "loaded,
     * and there are none" and returns null. Data that legitimately loads nothing would therefore be
     * fine everywhere except the kind table.
     */
    @Test
    void anEmptyButLoadedListAnswersNullWhereAnEmptyKindTableThrows() {
        ObjectRegistry.setSlays(new ArrayList<>());

        assertNull(ObjectRegistry.lookupSlay("SLAY_ANIMAL"));
        assertThrows(IllegalStateException.class, () -> ObjectRegistry.lookupObjectKind("Dagger"));
    }

    // ---- the name-keyed lookups ------------------------------------------

    /**
     * The base lookups: by name, by name and tval together, and the first base carrying a tval. The
     * two-argument form exists because a name alone is not unique across tvals in principle, and the
     * tval form is how a kind reaches its base when only the type is known.
     */
    @Test
    void theBaseLookupsMatchOnNameTvalOrBoth() {
        assertSame(swordBase, ObjectRegistry.lookupObjectBase("sword"));
        assertSame(swordBase, ObjectRegistry.lookupObjectBase("sword", TValue.TV_SWORD));
        assertNull(ObjectRegistry.lookupObjectBase("sword", TValue.TV_MAGIC_BOOK),
                "the name matches but the tval does not");
        assertSame(bookBase, ObjectRegistry.getBaseFromTVal(TValue.TV_MAGIC_BOOK));
        assertNull(ObjectRegistry.getBaseFromTVal(TValue.TV_RING), "no base carries that tval");
        assertNull(ObjectRegistry.lookupObjectBase("no such base"));
    }

    /**
     * Slays and brands are looked up by their {@code code}, curses and activations by their
     * {@code name} - a distinction that is easy to get backwards, since all four are single-field
     * matches over a list.
     */
    @Test
    void slaysAndBrandsMatchOnCodeWhileCursesAndActivationsMatchOnName() {
        Slay slay = slay("ANIMAL_2", "animals");
        Brand brand = brand("ACID_2", "acid");
        Curse curse = curse("teleportation");
        Activation activation = activation("RING_ACID");

        ObjectRegistry.setSlays(new ArrayList<>(List.of(slay)));
        ObjectRegistry.setBrands(new ArrayList<>(List.of(brand)));
        ObjectRegistry.setCurses(new ArrayList<>(List.of(curse)));
        ObjectRegistry.setActivations(new ArrayList<>(List.of(activation)));

        assertSame(slay, ObjectRegistry.lookupSlay("ANIMAL_2"));
        assertNull(ObjectRegistry.lookupSlay("animals"), "the slay's name is not its code");
        assertSame(brand, ObjectRegistry.lookupBrandCode("ACID_2"));
        assertNull(ObjectRegistry.lookupBrandCode("acid"), "nor is the brand's");
        assertSame(curse, ObjectRegistry.lookupCurse("teleportation"));
        assertSame(activation, ObjectRegistry.lookupActivation("RING_ACID"));
    }

    /**
     * The counters the setters maintain are the sizes of the lists they were given, so a caller can
     * use either interchangeably. They are separate fields only because C keeps separate globals.
     */
    @Test
    void theSettersKeepTheirCountersInStepWithTheirLists() {
        ObjectRegistry.setSlays(new ArrayList<>(List.of(
                slay("ANIMAL_2", "animals"), slay("EVIL_2", "evil creatures"))));
        ObjectRegistry.setBrands(new ArrayList<>(List.of(brand("ACID_2", "acid"))));
        ObjectRegistry.setCurses(new ArrayList<>(List.of(
                curse("teleportation"), curse("poison"), curse("siren"))));

        assertEquals(ObjectRegistry.getSlays().size(), ObjectRegistry.getSlayMax());
        assertEquals(ObjectRegistry.getBrands().size(), ObjectRegistry.getBrandMax());
        assertEquals(ObjectRegistry.getCurses().size(), ObjectRegistry.getCurseMax());
    }
}
