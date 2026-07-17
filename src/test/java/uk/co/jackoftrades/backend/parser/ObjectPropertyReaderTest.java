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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.frontend.entries.UIEntry;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.ObjectProperty;
import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the object-property pipeline: text file → ANTLR
 * lexer/parser ({@link uk.co.jackoftrades.backend.parser.grammars.objectproperty.ObjectPropertyLexer}
 * / {@code ObjectPropertyGrammar}) → {@link ObjectPropertyReader} →
 * {@link uk.co.jackoftrades.backend.parser.objectproperty.ObjectPropertyAssembler}
 * → resolved {@link ObjectProperty} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>The headline test runs against the real shipped
 * {@code lib/gamedata/object_property.txt}. Rather than assert only a load count
 * (which the Artifact work showed can stay green while the record <em>shape</em>
 * silently rots), it pins the counts that each field-resolution step has to get
 * right: 79 records, 275 type-mult pairs, the 13 records that carry no
 * {@code bindui}, and the {@code type}/{@code subtype}/{@code id-type} domains.
 *
 * <p>The remaining tests isolate one resolution path each, several using a
 * synthetic single record so a failure points at exactly one mapping:
 * the four element-relation {@code type} strings ({@code resistance} etc.), the
 * {@code type-mult} tval lookup, the {@code <TAG>}-decorated {@code bindui} key,
 * the {@code aux}/{@code value} split, and the promise that a record with no
 * {@code bindui} line ends up with an empty binding list rather than a binding
 * to nothing. Two injected-defect tests exercise the hard (missing
 * {@code record-count}) and soft (record-count mismatch) error channels.
 *
 * <p>{@link ObjectProperty} exposes no getters, so field reads go through the
 * {@link #field(ObjectProperty, String)} reflection helper. The assembler resolves
 * {@code bindui} targets against the {@code GameConstants} UI-entry registry, so
 * {@link #seedRegistries()} loads the real renderer, base and entry files and
 * injects them into {@code GameConstants}' private static fields via reflection,
 * independent of full-game init order.
 *
 * @author Rowan Crowther
 */
class ObjectPropertyReaderTest {

    private static final String REAL_FILE = "lib/gamedata/object_property.txt";

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seedRegistries() throws Exception {
        // The UI-entry pipeline resolves bottom-up: renderers, then bases (which
        // resolve renderers), then entries (which resolve both). The object-property
        // assembler's bindui look-ups need the finished entry list seeded.
        List<UIEntryRenderer> renderers = new UIEntryRendererReader()
                .parseWithResults("lib/gamedata/ui_entry_renderer.txt").items();
        setStatic("uiEntryRenderers", renderers);
        List<UIEntryBase> bases = new UIEntryBaseReader()
                .parseWithResults("lib/gamedata/ui_entry_base.txt").items();
        setStatic("uiEntryBases", bases);
        List<UIEntry> entries = new UIEntryReader()
                .parseWithResults("lib/gamedata/ui_entry.txt").items();
        setStatic("uiEntries", entries);
    }

    private static void setStatic(String fieldName, Object value) throws Exception {
        Field f = GameConstants.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(null, value);
    }

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    /**
     * Read a private field off an {@link ObjectProperty} (the domain has no getters).
     */
    private static Object field(ObjectProperty prop, String name) {
        try {
            Field f = ObjectProperty.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(prop);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectProperty byName(List<ObjectProperty> items, String name) {
        return items.stream().filter(p -> name.equals(field(p, "name"))).findFirst()
                .orElseThrow(() -> new AssertionError("no ObjectProperty named " + name));
    }

    @SuppressWarnings("unchecked")
    private static List<ObjectProperty.UIBinding> bindings(ObjectProperty prop) {
        return (List<ObjectProperty.UIBinding>) field(prop, "boundEntries");
    }

    // ---- headline: the real file ------------------------------------------------------------

    /**
     * The shipped {@code object_property.txt} loads cleanly and with the right shape.
     * The counts below are each a proxy for one resolution step: 79 records means every
     * {@code type} string resolved (the four element-relation words — {@code resistance},
     * {@code vulnerability}, {@code immunity}, {@code ignore} — are the ones most likely to
     * drift from the enum); 275 type-mult pairs means the tval-name lookup resolved every
     * one; and exactly 13 records with an empty binding list means the {@code bindui} block
     * did not manufacture a binding-to-nothing for the records that have no {@code bindui} line.
     */
    @Test
    void realFileLoadsAll79RecordsWithCorrectShape() throws IOException {
        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(REAL_FILE);

        assertEquals(List.of(), result.errors(), () -> "expected a clean load but got: " + result.errors());
        List<ObjectProperty> items = result.items();
        assertEquals(79, items.size());

        int typeMultPairs = items.stream()
                .mapToInt(p -> ((Map<?, ?>) field(p, "typeMults")).size()).sum();
        assertEquals(275, typeMultPairs, "every type-mult line should resolve to a (TValue, int) pair");

        long unbound = items.stream().filter(p -> bindings(p).isEmpty()).count();
        assertEquals(13, unbound, "the 13 records with no bindui line should have no bindings");

        // No surviving binding should point at a null UI entry: an unresolvable (or absent)
        // bindui must drop the binding, never keep a binding to nothing.
        assertTrue(items.stream().flatMap(p -> bindings(p).stream()).allMatch(b -> b.entry() != null),
                "every retained binding should resolve to a real UI entry");
    }

    // ---- element-relation type strings (resistance/vulnerability/immunity/ignore) -----------

    /**
     * The four element-relation {@code type} strings must map to their enum constants. These
     * are spelled in full in both the data file and C's {@code parse_object_property_type}
     * ({@code streq(name, "resistance")} etc.), so the port's {@link ObjPropertyType} value
     * strings have to match the full words, not abbreviations.
     */
    @Test
    void elementRelationTypesResolve() throws IOException {
        String path = tempFile("elems.txt", """
                record-count:4
                name:acid resistance
                type:resistance
                code:ACID
                power:5
                name:fire vulnerability
                type:vulnerability
                code:FIRE
                power:0
                name:cold immunity
                type:immunity
                code:COLD
                power:0
                name:acid ignore
                type:ignore
                code:ACID
                power:0
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(List.of(), result.errors(), result.errors()::toString);
        assertEquals(4, result.items().size());
        assertEquals(ObjPropertyType.OBJ_PROPERTY_RESIST, field(byName(result.items(), "acid resistance"), "type"));
        assertEquals(ObjPropertyType.OBJ_PROPERTY_VULN, field(byName(result.items(), "fire vulnerability"), "type"));
        assertEquals(ObjPropertyType.OBJ_PROPERTY_IMM, field(byName(result.items(), "cold immunity"), "type"));
        assertEquals(ObjPropertyType.OBJ_PROPERTY_IGNORE, field(byName(result.items(), "acid ignore"), "type"));

        // The element index resolves off the code for an element-relation type.
        ObjectPropertyTypeWrapper wrapper =
                (ObjectPropertyTypeWrapper) field(byName(result.items(), "acid resistance"), "index");
        assertEquals(ElementEnum.ELEM_ACID, wrapper.getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
    }

    // ---- type-mult -------------------------------------------------------------------------

    /**
     * A {@code type-mult} line resolves against the tval's data-file <em>name</em>
     * ({@code gloves}, {@code dragon armor}), not its enum constant name — so the lookup must
     * go through {@link TValue#fromName(String)}, which normalises case and spaces, rather than
     * {@code TValue.valueOf}. {@code dragon armor} is the sharp case: it contains a space, so
     * no {@code valueOf}-style key could ever match it.
     */
    @Test
    void typeMultResolvesAgainstTvalNames() throws IOException {
        String path = tempFile("typemult.txt", """
                record-count:1
                name:dexterity
                type:stat
                code:DEX
                power:8
                type-mult:gloves:2
                type-mult:dragon armor:3
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(List.of(), result.errors(), result.errors()::toString);
        @SuppressWarnings("unchecked")
        Map<TValue, Integer> typeMults =
                (Map<TValue, Integer>) field(byName(result.items(), "dexterity"), "typeMults");
        assertEquals(Map.of(TValue.TV_GLOVES, 2, TValue.TV_DRAGON_ARMOR, 3), typeMults);
    }

    // ---- bindui: tag, aux, value -----------------------------------------------------------

    /**
     * A {@code <TAG>} is part of the target entry's <em>name</em> in {@code ui_entry.txt}
     * (e.g. {@code stat_mod_ui_compact_0<STR>}), not a separate parameter, so the lookup key is
     * {@code bindui + "<" + tag + ">"}. This pins that the decorated key resolves and the aux
     * flag is read: {@code stat_mod_ui_compact_0<STR>:1} is the "sustain STR" shape — aux true,
     * no explicit value (so the natural value is used).
     */
    @Test
    void taggedBinduiResolvesAndAuxIsRead() throws IOException {
        String path = tempFile("tagged.txt", """
                record-count:1
                name:sustain strength
                type:flag
                subtype:sustain
                id-type:on effect
                code:SUST_STR
                power:9
                bindui:stat_mod_ui_compact_0<STR>:1
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(List.of(), result.errors(), result.errors()::toString);
        List<ObjectProperty.UIBinding> binds = bindings(byName(result.items(), "sustain strength"));
        assertEquals(1, binds.size());
        ObjectProperty.UIBinding bind = binds.get(0);
        assertNotNull(bind.entry(), "the <STR>-decorated entry should resolve");
        assertEquals("stat_mod_ui_compact_0<STR>", bind.entry().getName());
        assertTrue(bind.aux(), "aux param is 1 -> auxiliary");
        assertNull(bind.value(), "no third param -> use the natural value (null), not 0");
    }

    /**
     * The optional third {@code bindui} param is an explicit value to present. {@code power 1
     * digging} carries {@code tunneling_ui_compact_0:0:1} — aux false, explicit value 1 — one of
     * only three records in the file that set it. This is the case that distinguishes "send this
     * literal value" (value present) from "send the natural value" (value {@code null}).
     */
    @Test
    void binduiExplicitValueIsParsed() throws IOException {
        String path = tempFile("uival.txt", """
                record-count:1
                name:power 1 digging
                type:flag
                subtype:dig
                id-type:on wield
                code:DIG_1
                power:3
                bindui:tunneling_ui_compact_0:0:1
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(List.of(), result.errors(), result.errors()::toString);
        ObjectProperty.UIBinding bind = bindings(byName(result.items(), "power 1 digging")).get(0);
        assertNotNull(bind.entry());
        assertFalse(bind.aux(), "aux param is 0");
        assertEquals(1, bind.value(), "third param present -> explicit value 1");
    }

    /**
     * A record with no {@code bindui} line must end up with an empty binding list, not a binding
     * whose entry is null. C resolves the property index unconditionally but only pushes a bound
     * property when a {@code bindui} directive is present; the port must not fabricate one.
     */
    @Test
    void recordWithoutBinduiHasNoBindings() throws IOException {
        String path = tempFile("nobind.txt", """
                record-count:1
                name:multiply
                type:flag
                subtype:curse-only
                id-type:on wield
                code:MULTIPLY_WEIGHT
                power:0
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(List.of(), result.errors(), result.errors()::toString);
        assertTrue(bindings(byName(result.items(), "multiply")).isEmpty(),
                "no bindui line -> no bindings (not a binding to a null entry)");
    }

    // ---- flag record: subtype / id-type / code ---------------------------------------------

    /**
     * A full flag record resolves its {@code subtype} and {@code id-type} to their enums and its
     * {@code code} to the flag index. The two awkward mappings are covered elsewhere; here we
     * confirm a straightforward flag round-trips end to end.
     */
    @Test
    void flagRecordResolvesSubtypeIdTypeAndCode() throws IOException {
        String path = tempFile("flag.txt", """
                record-count:1
                name:free action
                type:flag
                subtype:misc ability
                id-type:on effect
                code:FREE_ACT
                power:20
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(List.of(), result.errors(), result.errors()::toString);
        ObjectProperty prop = byName(result.items(), "free action");
        assertEquals(ObjPropertyType.OBJ_PROPERTY_FLAG, field(prop, "type"));
        assertEquals(ObjectFlagType.OFT_MISC, field(prop, "subtype"));
        assertEquals(ObjectFlagID.OFID_NORMAL, field(prop, "idType"));
        ObjectPropertyTypeWrapper wrapper = (ObjectPropertyTypeWrapper) field(prop, "index");
        assertEquals(ObjectFlag.OF_FREE_ACT, wrapper.getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    /**
     * A stat record resolves its code through the shared stat/mod modifier table.
     */
    @Test
    void statRecordResolvesCodeToModifier() throws IOException {
        String path = tempFile("stat.txt", """
                record-count:1
                name:strength
                type:stat
                code:STR
                power:9
                mult:13
                adjective:strong
                neg-adjective:weak
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(List.of(), result.errors(), result.errors()::toString);
        ObjectProperty prop = byName(result.items(), "strength");
        assertEquals(ObjPropertyType.OBJ_PROPERTY_STAT, field(prop, "type"));
        assertEquals(9, field(prop, "power"));
        assertEquals(13, field(prop, "mult"));
        assertEquals("strong", field(prop, "adjective"));
        assertEquals("weak", field(prop, "negAdjective"));
        ObjectPropertyTypeWrapper wrapper = (ObjectPropertyTypeWrapper) field(prop, "index");
        assertEquals(ObjectModifier.OM_STR, wrapper.getModifier(ObjPropertyType.OBJ_PROPERTY_STAT));
    }

    // ---- error channels --------------------------------------------------------------------

    @Test
    void missingRecordCountHeaderFailsClosed() throws IOException {
        // No record-count directive -> the file rule can't match -> grammar error via ParseErrors.
        String path = tempFile("no-header.txt", "name:strength\ntype:stat\ncode:STR\npower:9\n");

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordCountMismatchIsLoggedButValidRecordSurvives() throws IOException {
        String path = tempFile("bad-count.txt",
                "record-count:5\nname:strength\ntype:stat\ncode:STR\npower:9\n");

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void unresolvableCodeIsLoggedAndRecordSkipped() throws IOException {
        // NOT_A_REAL_FLAG is not an ObjectFlag constant -> the code is the property's identity, so
        // the assembler drops the whole record and logs the error.
        String path = tempFile("bad-code.txt",
                "record-count:1\nname:bogus\ntype:flag\nsubtype:bad\nid-type:on wield\ncode:NOT_A_REAL_FLAG\npower:0\n");

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

        assertEquals(0, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid code")),
                result.errors()::toString);
    }
}
