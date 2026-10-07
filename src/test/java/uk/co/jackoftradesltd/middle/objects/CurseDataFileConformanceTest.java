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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import uk.co.jackoftradesltd.backend.parser.BrandReader;
import uk.co.jackoftradesltd.backend.parser.CurseReader;
import uk.co.jackoftradesltd.backend.parser.ItemObjectReader;
import uk.co.jackoftradesltd.backend.parser.MonsterBaseReader;
import uk.co.jackoftradesltd.backend.parser.ObjectBaseReader;
import uk.co.jackoftradesltd.backend.parser.PainReader;
import uk.co.jackoftradesltd.backend.parser.ShapeReader;
import uk.co.jackoftradesltd.backend.parser.SlayReader;
import uk.co.jackoftradesltd.backend.parser.SummonReader;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.loaders.ObjectDataLoader;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks every curse the real {@code curse.txt} defines against what the loaded {@link Curse}'s own
 * {@link ItemObject} holds, after the full load path has run: {@code CurseReader}, then
 * {@code ObjectDataLoader.parseCurseKinds()}.
 *
 * <p>The expected values are not taken from the Java. {@link #readDataFile()} re-reads the text of
 * {@code curse.txt} line by line, applying what the C {@code parse_curse_*} functions in
 * {@code obj-init.c} do to {@code curse->obj}: {@code weight:} to {@code obj->weight}, {@code combat:}
 * to {@code to_h/to_d/to_a}, {@code flags:} to {@code obj->flags} and the {@code HATES_/IGNORE_}
 * element flags, {@code values:} to {@code obj->modifiers[]} and {@code el_info[].res_level},
 * {@code effect:} / {@code dice:} to the effect chain, {@code msg:} lines concatenated into
 * {@code obj->effect_msg}, and {@code time:} to {@code obj->time}. Then {@code write_curse_kinds}
 * gives the object and its known twin the {@code <curse object>} kind and marks the twin
 * {@code OBJ_NOTICE_ASSESSED}.
 *
 * <p>Each curse is its own dynamic test and collects all its mismatches, so one run lists everything
 * that differs instead of stopping at the first.
 *
 * <p>The registries are seeded by reflection and put back afterwards.
 *
 * @author Rowan Crowther
 */
class CurseDataFileConformanceTest {

    private static final String CURSE_FILE = "lib/gamedata/curse.txt";
    private static final String OBJECT_FILE = "lib/gamedata/object.txt";
    private static final Map<String, Object> savedElsewhere = new LinkedHashMap<>();
    private static List<Curse> curses;
    private static ObjectKind curseKind;

    @BeforeAll
    static void seed() throws Exception {
        setStatic("monsterPains", new PainReader().parse("lib/gamedata/pain.txt"));
        setStatic("monsterBases", new MonsterBaseReader().parse("lib/gamedata/monster_base.txt"));
        setStatic("summons", new SummonReader().parse("lib/gamedata/summon.txt"));
        setStatic("slays", new SlayReader().parse("lib/gamedata/slay.txt"));
        setStatic("brands", new BrandReader().parse("lib/gamedata/brand.txt"));
        setStatic("objectBases", new ObjectBaseReader().parse("lib/gamedata/object_base.txt"));
        setStatic("curses", new CurseReader().parse(CURSE_FILE));
        setStatic("playerShapes", new ShapeReader().parse("lib/gamedata/shape.txt"));
        setStatic("objectKinds", new ItemObjectReader().parse(OBJECT_FILE));

        ObjectDataLoader.parseCurseKinds();

        curses = ObjectRegistry.getCurses();
        curseKind = ObjectRegistry.lookupObjectKind(TValue.TV_NONE, "<curse object>");
    }

    @AfterAll
    static void restore() throws Exception {
        for (Map.Entry<String, Object> e : savedElsewhere.entrySet()) {
            Field f = findField(e.getKey());
            f.setAccessible(true);
            f.set(null, e.getValue());
        }
    }

    private static Field findField(String name) throws Exception {
        for (Class<?> c : List.of(
                uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry.class,
                ObjectRegistry.class,
                uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry.class)) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                // try the next registry
            }
        }
        throw new NoSuchFieldException(name);
    }

    private static void setStatic(String name, Object value) throws Exception {
        Field f = findField(name);
        f.setAccessible(true);
        savedElsewhere.put(name, f.get(null));
        f.set(null, value);
    }

    /**
     * Applies the C {@code parse_curse_*} rules to the text of {@code curse.txt}.
     */
    private static List<Expected> readDataFile() throws Exception {
        List<Expected> out = new ArrayList<>();
        Expected cur = null;
        for (String raw : Files.readAllLines(Path.of(CURSE_FILE))) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("record-count:")) {
                continue;
            }
            int colon = line.indexOf(':');
            String key = line.substring(0, colon);
            String val = line.substring(colon + 1);
            if (key.equals("name")) {
                cur = new Expected();
                cur.name = val;
                out.add(cur);
                continue;
            }
            final Expected target = cur;
            switch (key) {
                case "weight" -> cur.weight = Integer.parseInt(val);
                case "combat" -> {
                    String[] p = val.split(":");
                    cur.toHit = Integer.parseInt(p[0]);
                    cur.toDam = Integer.parseInt(p[1]);
                    cur.toAC = Integer.parseInt(p[2]);
                }
                case "flags" -> Stream.of(val.split("[ |]+")).filter(s -> !s.isEmpty()).forEach(target.flags::add);
                case "values" -> Stream.of(val.split("[ |]+")).filter(s -> !s.isEmpty()).forEach(t -> {
                    int b = t.indexOf('[');
                    target.values.put(t.substring(0, b), Integer.parseInt(t.substring(b + 1, t.length() - 1)));
                });
                case "effect" -> cur.effects.add(val);
                case "dice" -> cur.dice.add(val);
                case "msg" -> cur.message.append(val);
                case "time" -> cur.time = val;
                default -> { /* type, desc, conflict, conflict-flags, expr: not part of this check */ }
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static <T> T read(Object target, String field) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return (T) f.get(target);
    }

    /**
     * The wrapper has no toString, so list what each non-null payload field holds: an enum by name,
     * anything else through its {@code getName()} if it has one.
     */
    private static String describe(Object wrapper) throws Exception {
        StringBuilder sb = new StringBuilder();
        if (wrapper == null) {
            return "null";
        }
        for (Field f : wrapper.getClass().getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(f.getModifiers()) || f.getType().isPrimitive()) {
                continue;
            }
            f.setAccessible(true);
            Object v = f.get(wrapper);
            if (v == null) {
                continue;
            }
            if (v instanceof Enum<?> en) {
                sb.append(f.getName()).append('=').append(en.name()).append(' ');
            } else {
                try {
                    sb.append(f.getName()).append('=').append(v.getClass().getMethod("getName").invoke(v)).append(' ');
                } catch (NoSuchMethodException ex) {
                    sb.append(f.getName()).append('=').append(v).append(' ');
                }
            }
        }
        return sb.toString().strip();
    }

    private static void check(List<String> bad, String what, Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            bad.add(what + ": data file says <" + expected + "> but object has <" + actual + ">");
        }
    }

    @TestFactory
    Stream<DynamicTest> everyCurseMatchesTheDataFile() throws Exception {
        List<Expected> expected = readDataFile();
        assertEquals(27, expected.size(), "curse.txt record count");
        assertEquals(expected.size(), curses.size(), "loaded curse count");

        return expected.stream().map(e -> DynamicTest.dynamicTest(e.name, () -> {
            Curse curse = curses.stream().filter(c -> c.getName().equals(e.name)).findFirst().orElse(null);
            assertTrue(curse != null, "curse not loaded: " + e.name);
            ItemObject obj = curse.getItemObject();
            List<String> bad = new ArrayList<>();

            check(bad, "weight", e.weight, obj.getWeight());
            check(bad, "to_h", e.toHit, obj.getToHit());
            check(bad, "to_d", e.toDam, obj.getToDam());
            check(bad, "to_a", e.toAC, obj.getToAC());

            // flags: object flags into obj->flags, HATES_/IGNORE_ into el_info flags
            Map<ElementEnum, ElementInfo> el = obj.getElInfo();
            for (String f : e.flags) {
                if (f.startsWith("HATES_") || f.startsWith("IGNORE_")) {
                    boolean hates = f.startsWith("HATES_");
                    ElementEnum element = ElementEnum.valueOf("ELEM_" + f.substring(hates ? 6 : 7));
                    ElementInfoEnum bit = hates ? ElementInfoEnum.EL_INFO_HATES : ElementInfoEnum.EL_INFO_IGNORE;
                    check(bad, "element flag " + f, true,
                            el != null && el.get(element) != null && el.get(element).getFlags().has(bit));
                } else {
                    check(bad, "flag " + f, true, obj.getFlags().has(ObjectFlag.valueOf("OF_" + f)));
                }
            }
            int expectedObjectFlags = (int) e.flags.stream()
                    .filter(f -> !f.startsWith("HATES_") && !f.startsWith("IGNORE_")).count();
            check(bad, "object flag count", expectedObjectFlags, obj.getFlags().count());

            // values: additive modifiers, RES_ to el_info res_level
            Map<ObjectModifier, Integer> modifiers = obj.getModifiers();
            Map<ObjectModifier, Integer> wantMods = new EnumMap<>(ObjectModifier.class);
            Map<ElementEnum, Integer> wantRes = new EnumMap<>(ElementEnum.class);
            e.values.forEach((k, v) -> {
                if (k.startsWith("RES_")) {
                    wantRes.put(ElementEnum.valueOf("ELEM_" + k.substring(4)), v);
                } else {
                    wantMods.put(ObjectModifier.valueOf("OM_" + k), v);
                }
            });
            for (Map.Entry<ObjectModifier, Integer> m : wantMods.entrySet()) {
                check(bad, "modifier " + m.getKey(), m.getValue(),
                        modifiers == null ? null : modifiers.get(m.getKey()));
            }
            if (modifiers != null) {
                modifiers.forEach((k, v) -> {
                    if (v != 0 && !wantMods.containsKey(k)) {
                        bad.add("modifier " + k + ": not in data file but object has <" + v + ">");
                    }
                });
            }
            for (Map.Entry<ElementEnum, Integer> r : wantRes.entrySet()) {
                ElementInfo info = el == null ? null : el.get(r.getKey());
                check(bad, "res_level " + r.getKey(), r.getValue(), info == null ? null : info.getResLevel());
            }
            if (el != null) {
                el.forEach((k, v) -> {
                    if (v.getResLevel() != 0 && !wantRes.containsKey(k)) {
                        bad.add("res_level " + k + ": not in data file but object has <" + v.getResLevel() + ">");
                    }
                });
            }

            // effect chain
            List<Effect> effects = obj.getEffect();
            int have = effects == null ? 0 : effects.size();
            check(bad, "effect count", e.effects.size(), have);
            if (effects != null) {
                for (int i = 0; i < Math.min(have, e.effects.size()); i++) {
                    String[] parts = e.effects.get(i).split(":");
                    Effect eff = effects.get(i);
                    Object index = read(eff, "index");
                    if (!index.toString().toUpperCase().endsWith(parts[0])) {
                        bad.add("effect[" + i + "] name: data file says <" + parts[0] + "> but object has <" + index + ">");
                    }
                    if (parts.length > 1) {
                        String sub = describe(read(eff, "value"));
                        if (!sub.toUpperCase().contains(parts[1])) {
                            bad.add("effect[" + i + "] subtype: data file says <" + parts[1] + "> but object has <" + sub + ">");
                        }
                    }
                    if (i < e.dice.size()) {
                        check(bad, "effect[" + i + "] dice", e.dice.get(i), read(eff, "diceString"));
                    }
                }
            }

            // msg: concatenated into obj->effect_msg
            String msg = read(obj, "effectMessage");
            check(bad, "effect message", e.message.toString(), msg == null ? "" : msg);

            // time: obj->time
            Random wantTime = e.time == null ? Random.Zero() : Random.parseStr(e.time);
            check(bad, "obj time", wantTime.toString(), obj.getTime().toString());

            // write_curse_kinds
            check(bad, "kind is <curse object>", curseKind, obj.getKind());
            ItemObject known = obj.getKnown();
            if (known == null) {
                bad.add("known object: null");
            } else {
                check(bad, "known kind is <curse object>", curseKind, known.getKind());
                check(bad, "known ASSESSED", true, known.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED));
            }

            assertTrue(bad.isEmpty(), e.name + " mismatches:\n  " + String.join("\n  ", bad));
        }));
    }

    /**
     * One curse's expectations, read from the data file text.
     */
    private static final class Expected {
        final List<String> flags = new ArrayList<>();
        final Map<String, Integer> values = new LinkedHashMap<>();
        final List<String> effects = new ArrayList<>();
        final List<String> dice = new ArrayList<>();
        final StringBuilder message = new StringBuilder();
        String name;
        int weight;
        int toHit;
        int toDam;
        int toAC;
        String time;
    }
}
