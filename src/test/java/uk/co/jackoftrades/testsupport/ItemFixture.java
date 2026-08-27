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

package uk.co.jackoftrades.testsupport;

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.objects.Artifact;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.CurseData;
import uk.co.jackoftrades.middle.objects.EgoItem;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.IgnoreFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A builder for a loaded {@link ItemObject} — one that has been all the way through the parser and
 * so has every collection and every dice field filled in.
 *
 * <p><b>Why this exists.</b> A family of methods on {@link ItemObject} assume a fully loaded game
 * and dereference straight through: the copy calls {@code copy()} on the base damage and the
 * recharge time rather than testing them for null, {@code similar} walks the whole element and
 * modifier enums without a null check, {@code originCombine} reads both origin races, and the
 * absorb needs the player's rune knowledge. None of that is wrong — C reaches the same code with a
 * {@code mem_zalloc}'d struct where every pointer is either valid or deliberately null — but each is
 * a precondition nothing in the port states. Before this fixture, seven test classes each
 * rediscovered the list and wrote their own reflective builder for it, and the lists had begun to
 * drift.
 *
 * <p><b>Why the long constructor rather than reflection.</b> Most of the precondition disappears if
 * the item is built the way the parser builds one: {@link ItemObject}'s data-file constructor
 * resolves the two dice strings into {@link Random}s, so an item from it can be copied. Reflection
 * is still needed for the two complete enum maps — the constructor takes them, but a test wanting
 * every element present should not have to spell that out — and for the fields a test wants to
 * break after the fact.
 *
 * <p><b>What the defaults are for.</b> Everything neutral: one item, no charge, no ego, no
 * artifact, no curses, no brands or slays, every element and modifier present and zero. The point is
 * that a test states only what it is asking about, and anything it does not state cannot be the
 * reason it passes. The maps are complete rather than empty because the methods that read them do so
 * by walking the enum, so a partial map is a crash and not a smaller fixture.
 *
 * <p>Class ItemFixture coded on 260827, commented in full on 260827.
 *
 * @author Rowan Crowther
 */
public final class ItemFixture {

    /**
     * The item's type, which decides which branch most of the methods under test take.
     */
    private final TValue tValue;
    /**
     * The item's own flags.
     */
    private final Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
    /**
     * The curses on the item, in the order they were laid.
     */
    private final LinkedHashMap<Curse, CurseData> curses = new LinkedHashMap<>();
    /**
     * The kind the item is built on. Defaults to a bare one; {@link #kind(ObjectKind)} replaces it
     * where the kind itself is read.
     */
    private ObjectKind kind = new ObjectKind();
    /**
     * The stack size.
     */
    private int number = 1;
    /**
     * The recharge counter — for a light, the fuel remaining.
     */
    private int timeout;
    /**
     * The ego, if any.
     */
    private EgoItem ego;
    /**
     * The artifact, if any.
     */
    private Artifact artifact;
    /**
     * Where the item lies. Zero rather than null, which is where an item in a pack sits.
     */
    private Loc location = Loc.zero;
    /**
     * Where the item came from.
     */
    private ObjectOriginEnum origin = ObjectOriginEnum.ORIGIN_NONE;

    /**
     * The depth it came from.
     */
    private int originDepth;

    /**
     * The monster that dropped it, if one did.
     */
    private MonsterRace originRace;

    /**
     * The known half, if one is wanted.
     */
    private ItemObject known;

    /**
     * Whether to attach a known half built identically to the item itself.
     */
    private boolean fullyKnown;

    /**
     * Bind a builder to an item type.
     *
     * @param tValue the item's type
     */
    private ItemFixture(TValue tValue) {
        this.tValue = tValue;
    }

    /**
     * Start a loaded item of the given type.
     *
     * @param tValue the item's type
     * @return the builder
     */
    public static ItemFixture item(TValue tValue) {
        return new ItemFixture(tValue);
    }

    /**
     * Every element, each with its own info.
     *
     * <p>Complete rather than empty because {@code similar} and the quality rules walk the whole
     * enum and read the map without a null check.
     *
     * @return a complete element map
     */
    public static Map<ElementEnum, ElementInfo> allElements() {
        Map<ElementEnum, ElementInfo> elInfo = new EnumMap<>(ElementEnum.class);
        for (ElementEnum element : ElementEnum.values()) {
            elInfo.put(element, new ElementInfo());
        }
        return elInfo;
    }

    /**
     * Every modifier at zero, for the same reason the element map is complete.
     *
     * @return a complete modifier map
     */
    public static Map<ObjectModifier, Integer> allModifiers() {
        Map<ObjectModifier, Integer> modifiers = new EnumMap<>(ObjectModifier.class);
        for (ObjectModifier modifier : ObjectModifier.values()) {
            modifiers.put(modifier, 0);
        }
        return modifiers;
    }

    /**
     * A kind carrying the four dice an item's bonuses are judged against.
     *
     * <p>The quality rules ask whether an item is better than the worst its kind could have rolled,
     * and {@code knowObject} compares an item's bonuses against the same ranges — so a kind with no
     * dice at all cannot be judged, and the parser fills these in after the constructor has run.
     *
     * @param tValue the kind's type
     * @return the kind
     */
    public static ObjectKind kindWithDice(TValue tValue) {
        ObjectKind kind = new ObjectKind(null, 0, 0, 0, 0, "test", tValue, "test", null, false);
        for (String name : new String[]{"toH", "toD", "toA", "pVal"}) {
            set(kind, name, new Random(0, 1, 1, 1, false));
        }
        return kind;
    }

    /**
     * A kind carrying a base, which is where the stacking limit lives.
     *
     * @param tValue   the kind's type
     * @param name     the base's name
     * @param maxStack the largest stack the base allows
     * @return the kind
     */
    public static ObjectKind kindWithBase(TValue tValue, String name, int maxStack) {
        ObjectKind kind = new ObjectKind();
        set(kind, "base", new ObjectBase(tValue, name, null,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), 0, maxStack));
        return kind;
    }

    /**
     * A kind with both, for the tests that stack items and judge them.
     *
     * @param tValue   the kind's type
     * @param name     the base's name
     * @param maxStack the largest stack the base allows
     * @return the kind
     */
    public static ObjectKind loadedKind(TValue tValue, String name, int maxStack) {
        ObjectKind kind = kindWithDice(tValue);
        set(kind, "base", new ObjectBase(tValue, name, null,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), 0, maxStack));
        set(kind, "ignore", new Flag<>(IgnoreFlag.class));
        return kind;
    }

    /**
     * Writes a field that has no setter, for the tests that need to break one thing after the item
     * is built.
     *
     * <p>Walks up the class hierarchy so that a field declared on a superclass is still reachable.
     *
     * @param target the object to write to
     * @param name   the field's name
     * @param value  the value to store
     */
    public static void set(Object target, String name, Object value) {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException expected) {
                // try the superclass
            } catch (IllegalAccessException e) {
                throw new AssertionError(name + " is no longer writable by reflection", e);
            }
        }
        throw new AssertionError(target.getClass().getSimpleName() + " has no field " + name);
    }

    /**
     * Reads a field that has no getter.
     *
     * @param target the object to read from
     * @param name   the field's name
     * @return the field's value
     */
    public static Object read(Object target, String name) {
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException expected) {
                // try the superclass
            } catch (IllegalAccessException e) {
                throw new AssertionError(name + " is no longer readable by reflection", e);
            }
        }
        throw new AssertionError(target.getClass().getSimpleName() + " has no field " + name);
    }

    /**
     * Writes a static field, for the registries and constant tables a loaded game would have filled.
     *
     * @param type  the class the field is declared on
     * @param name  the field's name
     * @param value the value to store
     * @return the value the field held before, so a test can put it back
     */
    public static Object setStatic(Class<?> type, String name, Object value) {
        try {
            Field field = type.getDeclaredField(name);
            field.setAccessible(true);
            Object previous = field.get(null);
            field.set(null, value);
            return previous;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(type.getSimpleName() + "." + name
                    + " is no longer reachable by reflection", e);
        }
    }

    /**
     * A list holding one item, for the single-item cases.
     *
     * @param item the item
     * @param <T>  the item's type
     * @return a mutable list holding it
     */
    public static <T> List<T> listOf(T item) {
        List<T> list = new ArrayList<>();
        list.add(item);
        return list;
    }

    /**
     * An empty mutable set, spelled once so the call sites do not each name the type.
     *
     * @param <T> the set's element type
     * @return an empty set that can be added to
     */
    public static <T> Set<T> mutableSet() {
        return new HashSet<>();
    }

    /**
     * Give the item a particular kind, for the tests where the kind is read — its base's maximum
     * stack, or the dice an item's bonuses are judged against.
     *
     * @param kind the kind to build on
     * @return this builder
     */
    public ItemFixture kind(ObjectKind kind) {
        this.kind = kind;
        return this;
    }

    /**
     * Set the stack size.
     *
     * @param number how many the stack holds
     * @return this builder
     */
    public ItemFixture number(int number) {
        this.number = number;
        return this;
    }

    /**
     * Set the recharge counter.
     *
     * @param timeout the counter
     * @return this builder
     */
    public ItemFixture timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Raise flags on the item.
     *
     * @param on the flags to raise
     * @return this builder
     */
    public ItemFixture flags(ObjectFlag... on) {
        for (ObjectFlag flag : on) {
            flags.on(flag);
        }
        return this;
    }

    /**
     * Give the item an ego.
     *
     * @param ego the ego
     * @return this builder
     */
    public ItemFixture ego(EgoItem ego) {
        this.ego = ego;
        return this;
    }

    /**
     * Make the item an artifact.
     *
     * @param artifact the artifact definition
     * @return this builder
     */
    public ItemFixture artifact(Artifact artifact) {
        this.artifact = artifact;
        return this;
    }

    /**
     * Lay a curse on the item.
     *
     * @param curse the curse
     * @param data  its power and timeout
     * @return this builder
     */
    public ItemFixture curse(Curse curse, CurseData data) {
        curses.put(curse, data);
        return this;
    }

    /**
     * Place the item on the map.
     *
     * @param location the grid
     * @return this builder
     */
    public ItemFixture location(Loc location) {
        this.location = location;
        return this;
    }

    /**
     * Give the item a history.
     *
     * <p>Two items that are to merge without becoming {@code ORIGIN_MIXED} need the same three
     * values, because {@code originCombine} compares all of them.
     *
     * @param origin      where it came from
     * @param originDepth the depth it came from
     * @param originRace  the monster that dropped it, or {@code null}
     * @return this builder
     */
    public ItemFixture origin(ObjectOriginEnum origin, int originDepth, MonsterRace originRace) {
        this.origin = origin;
        this.originDepth = originDepth;
        this.originRace = originRace;
        return this;
    }

    /**
     * Attach a particular known half.
     *
     * @param known the counterpart
     * @return this builder
     */
    public ItemFixture known(ItemObject known) {
        this.known = known;
        this.fullyKnown = false;
        return this;
    }

    /**
     * Attach a known half built exactly like the item — an object the character has identified.
     *
     * <p>Without a counterpart, everything that asks what the character knows reads null and the
     * item answers as unhandled, which is rarely the state a test means to be in.
     *
     * @return this builder
     */
    public ItemFixture fullyKnown() {
        this.fullyKnown = true;
        this.known = null;
        return this;
    }

    /**
     * Build the item.
     *
     * @return the item
     */
    public ItemObject build() {
        ItemObject counterpart = known;
        if (fullyKnown) {
            // The counterpart agrees with the item entirely, which is what "identified" means. It is
            // built with the same arguments and no counterpart of its own, since C's known half does
            // not have one either.
            counterpart = assemble(null);
        }

        return assemble(counterpart);
    }

    /**
     * Assembles the item around a given known half, so that the item and its counterpart are built
     * the same way.
     *
     * <p>Each call gets its own flag set and its own collections: two items sharing a mutable
     * collection is a fixture that passes for the wrong reason, because breaking one breaks both.
     *
     * @param counterpart the known half to attach, or {@code null} for none
     * @return the item
     */
    private ItemObject assemble(ItemObject counterpart) {
        Flag<ObjectFlag> ownFlags = new Flag<>(ObjectFlag.class);
        ownFlags.copyFrom(flags);

        return new ItemObject(kind, ego, artifact, counterpart, location, tValue, 0,
                "0", 0, 0, 0, 0, 0, "0", 0, 0,
                ownFlags, allModifiers(), allElements(),
                new HashSet<Brand>(), new HashSet<Slay>(),
                new LinkedHashMap<>(curses),
                new ArrayList<Effect>(), null, new ArrayList<>(), "0",
                timeout, number,
                new Flag<>(ObjectNotice.class), 0, 0,
                origin, originDepth, originRace, null);
    }
}
