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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the object-property pipeline: text file → ANTLR
 * lexer/parser ({@link uk.co.jackoftradesltd.backend.parser.grammars.objectproperty.ObjectPropertyLexer}
 * / {@code ObjectPropertyGrammar}) → {@link ObjectPropertyReader} →
 * {@link uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyAssembler}
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
 * <p>{@link ObjectProperty} has getters for most fields, which the newer tests use; the adjectives,
 * the description and the type-mult map have none, so those reads go through the
 * {@link #field(ObjectProperty, String)} reflection helper, as the older tests do for everything.
 * The assembler no longer resolves
 * {@code bindui} targets against a UI-entry registry: it stores the raw {@code name + tag}
 * string verbatim, leaving the look-up to the UI layer (via {@code UIRegistryLoader}) at the
 * point the entry is actually needed.
 *
 * @author Rowan Crowther
 */
class ObjectPropertyReaderTest {

    private static final String REAL_FILE = "lib/gamedata/object_property.txt";

    @TempDir
    Path tempDir;

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

        // No surviving binding should carry a blank entry name: an absent bindui line must
        // produce no binding at all, never a binding to nothing.
        assertTrue(items.stream().flatMap(p -> bindings(p).stream())
                        .allMatch(b -> b.entry() != null && !b.entry().isEmpty()),
                "every retained binding should carry a non-empty raw entry name");
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
                (ObjectPropertyTypeWrapper) field(byName(result.items(), "acid resistance"), "payload");
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
        assertEquals(Map.of(TValue.TV_GLOVES, 2, TValue.TV_DRAG_ARMOR, 3), typeMults);
    }

    // ---- bindui: tag, aux, value -----------------------------------------------------------

    /**
     * A {@code <TAG>} is part of the target entry's <em>name</em> in {@code ui_entry.txt}
     * (e.g. {@code stat_mod_ui_compact_0<STR>}), not a separate parameter, so the stored key is
     * {@code bindui + "<" + tag + ">"}. This pins that the decorated key is captured verbatim and
     * the aux flag is read: {@code stat_mod_ui_compact_0<STR>:1} is the "sustain STR" shape — aux
     * true, no explicit value (so the natural value is used).
     */
    @Test
    void taggedBinduiCapturesTagAndAuxIsRead() throws IOException {
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
        assertEquals("stat_mod_ui_compact_0<STR>", bind.entry(), "the <STR>-decorated name should be captured verbatim");
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
        assertEquals("tunneling_ui_compact_0", bind.entry());
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
        ObjectPropertyTypeWrapper wrapper = (ObjectPropertyTypeWrapper) field(prop, "payload");
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
        ObjectPropertyTypeWrapper wrapper = (ObjectPropertyTypeWrapper) field(prop, "payload");
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

    /**
     * NOT_A_REAL_FLAG is not in {@code obj_flags}, so {@code code_index_in_array} gives -1 and C
     * returns {@code PARSE_ERROR_INVALID_OBJ_PROP_CODE}: the load ends.
     */
    @Test
    void anUnresolvableCodeEndsTheLoad() throws IOException {
        assertStopsTheLoad(
                "record-count:1\nname:bogus\ntype:flag\nsubtype:bad\nid-type:on wield\ncode:NOT_A_REAL_FLAG\npower:0\n",
                2, "an illegal code value: NOT_A_REAL_FLAG");
    }

    // ---- the reader's own contract, against values worked out from the upstream file --------------

    /**
     * One row per record of the upstream {@code object_property.txt}, worked out from that file with
     * a script that reads the text and knows nothing of the Java classes:
     * {@code name|type|subtype|id-type|code|power|mult|type-mult lines|bindui lines}. The order is the
     * file's, which is also {@code obj_properties} order in C: {@code parse_object_property_name}
     * pushes each record on the head of a chain, and {@code finish_parse_object_property} then
     * copies the chain into the array from the top slot down, so slot 1 is the first record.
     */
    private static final String[] UPSTREAM = {
            "strength|stat|||STR|9|13|0|1",
            "intelligence|stat|||INT|5|10|0|1",
            "wisdom|stat|||WIS|5|10|0|1",
            "dexterity|stat|||DEX|8|10|1|1",
            "constitution|stat|||CON|12|15|0|1",
            "stealth|mod|||STEALTH|8|12|0|1",
            "searching skill|mod|||SEARCH|2|5|0|1",
            "infravision|mod|||INFRA|4|8|0|1",
            "tunneling|mod|||TUNNEL|3|8|0|1",
            "speed|mod|||SPEED|20|6|0|1",
            "damage reduction|mod|||DAM_RED|5|6|0|1",
            "extra blows|mod|||BLOWS|0|50|13|1",
            "shooting speed|mod|||SHOTS|0|5|16|1",
            "shooting power|mod|||MIGHT|0|30|16|1",
            "extra moves|mod|||MOVES|0|50|16|1",
            "light|mod|||LIGHT|3|6|1|1",
            "sustain strength|flag|sustain|on effect|SUST_STR|9|0|0|1",
            "sustain intelligence|flag|sustain|on effect|SUST_INT|4|0|0|1",
            "sustain wisdom|flag|sustain|on effect|SUST_WIS|4|0|0|1",
            "sustain dexterity|flag|sustain|on effect|SUST_DEX|7|0|0|1",
            "sustain constitution|flag|sustain|on effect|SUST_CON|8|0|0|1",
            "protection from fear|flag|protection|on effect|PROT_FEAR|6|0|0|1",
            "protection from blindness|flag|protection|on effect|PROT_BLIND|16|0|0|1",
            "protection from confusion|flag|protection|on effect|PROT_CONF|24|0|0|1",
            "protection from stunning|flag|protection|on effect|PROT_STUN|12|0|0|1",
            "slow digestion|flag|misc ability|timed|SLOW_DIGEST|2|0|0|1",
            "feather falling|flag|misc ability|on effect|FEATHER|1|0|0|1",
            "regeneration|flag|misc ability|timed|REGEN|5|0|12|1",
            "telepathy|flag|misc ability|on wield|TELEPATHY|35|0|12|1",
            "see invisible|flag|misc ability|on wield|SEE_INVIS|6|0|12|1",
            "free action|flag|misc ability|on effect|FREE_ACT|8|0|12|1",
            "hold life|flag|misc ability|on effect|HOLD_LIFE|5|0|12|1",
            "earthquakes|flag|melee|on effect|IMPACT|10|0|13|0",
            "blessed melee|flag|melee|on wield|BLESSED|1|0|14|1",
            "burns out|flag|light|on wield|BURNS_OUT|0|0|16|0",
            "takes fuel|flag|light|on wield|TAKES_FUEL|0|0|16|0",
            "no fuel|flag|light|on wield|NO_FUEL|5|0|16|0",
            "impaired hitpoint recovery|flag|bad|timed|IMPAIR_HP|-8|0|0|1",
            "impaired mana recovery|flag|bad|timed|IMPAIR_MANA|-8|0|0|1",
            "constant fear|flag|bad|on wield|AFRAID|-20|0|0|1",
            "teleportation ban|flag|bad|on effect|NO_TELEPORT|-20|0|0|1",
            "aggravation|flag|bad|timed|AGGRAVATE|-20|0|0|1",
            "experience drain|flag|bad|timed|DRAIN_EXP|-5|0|0|1",
            "stuck on|flag|bad|on wield|STICKY|-5|0|0|1",
            "fragile|flag|bad|on wield|FRAGILE|-1|0|0|1",
            "intensity 2 light|flag|light|on wield|LIGHT_2|9|0|16|0",
            "intensity 3 light|flag|light|on wield|LIGHT_3|18|0|16|0",
            "power 1 digging|flag|dig|on wield|DIG_1|3|0|0|1",
            "power 2 digging|flag|dig|on wield|DIG_2|6|0|0|1",
            "power 3 digging|flag|dig|on wield|DIG_3|9|0|0|1",
            "explode|flag|light|on wield|EXPLODE|0|0|16|0",
            "trap immunity|flag|misc ability|on effect|TRAP_IMMUNE|5|0|12|1",
            "throwing|flag|throw|on wield|THROWING|4|0|0|0",
            "multiply weight|flag|curse-only|on wield|MULTIPLY_WEIGHT|0|0|17|0",
            "ignore acid|ignore|||ACID|3|0|0|0",
            "ignore electricity|ignore|||ELEC|1|0|0|0",
            "ignore fire|ignore|||FIRE|3|0|0|0",
            "ignore cold|ignore|||COLD|1|0|0|0",
            "acid resistance|resistance|||ACID|5|0|0|1",
            "electricity resistance|resistance|||ELEC|6|0|0|1",
            "fire resistance|resistance|||FIRE|6|0|0|1",
            "cold resistance|resistance|||COLD|6|0|0|1",
            "poison resistance|resistance|||POIS|28|0|0|1",
            "light resistance|resistance|||LIGHT|6|0|0|1",
            "dark resistance|resistance|||DARK|16|0|0|1",
            "sound resistance|resistance|||SOUND|14|0|0|1",
            "shards resistance|resistance|||SHARD|8|0|0|1",
            "nexus resistance|resistance|||NEXUS|15|0|0|1",
            "nether resistance|resistance|||NETHER|20|0|0|1",
            "chaos resistance|resistance|||CHAOS|20|0|0|1",
            "disenchantment resistance|resistance|||DISEN|20|0|0|1",
            "acid vulnerability|vulnerability|||ACID|-6|0|0|1",
            "lightning vulnerability|vulnerability|||ELEC|-6|0|0|1",
            "fire vulnerability|vulnerability|||FIRE|-6|0|0|1",
            "cold vulnerability|vulnerability|||COLD|-6|0|0|1",
            "acid immunity|immunity|||ACID|43|0|0|1",
            "lightning immunity|immunity|||ELEC|41|0|0|1",
            "fire immunity|immunity|||FIRE|46|0|0|1",
            "cold immunity|immunity|||COLD|43|0|0|1",
    };

    private static final Map<String, ObjPropertyType> TYPES = Map.of(
            "stat", ObjPropertyType.OBJ_PROPERTY_STAT,
            "mod", ObjPropertyType.OBJ_PROPERTY_MOD,
            "flag", ObjPropertyType.OBJ_PROPERTY_FLAG,
            "ignore", ObjPropertyType.OBJ_PROPERTY_IGNORE,
            "resistance", ObjPropertyType.OBJ_PROPERTY_RESIST,
            "vulnerability", ObjPropertyType.OBJ_PROPERTY_VULN,
            "immunity", ObjPropertyType.OBJ_PROPERTY_IMM);

    private static final Map<String, ObjectFlagType> SUBTYPES = Map.of(
            "", ObjectFlagType.OFT_NONE,
            "sustain", ObjectFlagType.OFT_SUST,
            "protection", ObjectFlagType.OFT_PROT,
            "misc ability", ObjectFlagType.OFT_MISC,
            "light", ObjectFlagType.OFT_LIGHT,
            "melee", ObjectFlagType.OFT_MELEE,
            "bad", ObjectFlagType.OFT_BAD,
            "dig", ObjectFlagType.OFT_DIG,
            "throw", ObjectFlagType.OFT_THROW,
            "curse-only", ObjectFlagType.OFT_CURSE_ONLY);

    private static final Map<String, ObjectFlagID> ID_TYPES = Map.of(
            "", ObjectFlagID.OFID_NONE,
            "on effect", ObjectFlagID.OFID_NORMAL,
            "timed", ObjectFlagID.OFID_TIMED,
            "on wield", ObjectFlagID.OFID_WIELD);

    /**
     * The data-file code of a property, read back off its typed payload: C's {@code code:} handler
     * stores an index into the table the {@code type:} picks, and the port stores the enum constant.
     */
    private static String codeOf(ObjectProperty prop) {
        ObjPropertyType type = prop.getType();
        ObjectPropertyTypeWrapper payload = prop.getPayload();
        return switch (type) {
            case OBJ_PROPERTY_STAT, OBJ_PROPERTY_MOD -> payload.getModifier(type).name().substring("OM_".length());
            case OBJ_PROPERTY_FLAG -> payload.getFlag(type).name().substring("OF_".length());
            default -> payload.getElement(type).name().substring("ELEM_".length());
        };
    }

    private ParseResult<ObjectProperty> load(String content) throws IOException {
        return new ObjectPropertyReader().parseWithResults(tempFile("case.txt", content));
    }

    /**
     * A bad field ends the load, as every parse error does in C ({@code print_error} in
     * {@code datafile.c}): the assembler throws a {@link DataFileParseException} whose message names
     * the record's {@code name:} line and the file, and nothing is returned.
     */
    private void assertStopsTheLoad(String content, int line, String tail) throws IOException {
        String path = tempFile("case.txt", content);
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> new ObjectPropertyReader().parseWithResults(path));
        assertTrue(thrown.getMessage().contains(tail), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: " + line), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("case.txt"), thrown::getMessage);
    }

    /**
     * Every one of the 79 records comes back in file order with the type, subtype, id-type, code,
     * power, multiplier, type-mult count and bindui count the upstream file gives. The file order is
     * {@code obj_properties} order, slot 1 first, so {@code strength} is first and
     * {@code cold immunity} is last. Unlike the slays, brands and curses, whose chains are walked the
     * other way, nothing is reversed.
     */
    @Test
    void realFileComesBackInFileOrderWithEveryFieldAsUpstream() throws IOException {
        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(REAL_FILE);

        assertEquals(List.of(), result.errors());
        List<ObjectProperty> items = result.items();
        assertEquals(UPSTREAM.length, items.size());
        for (int i = 0; i < UPSTREAM.length; i++) {
            String[] row = UPSTREAM[i].split("\\|", -1);
            ObjectProperty prop = items.get(i);
            String where = "record " + i + " (" + row[0] + ")";
            assertEquals(row[0], prop.getName(), where);
            assertEquals(TYPES.get(row[1]), prop.getType(), where);
            assertEquals(SUBTYPES.get(row[2]), prop.getSubtype(), where);
            assertEquals(ID_TYPES.get(row[3]), prop.getIdType(), where);
            assertEquals(row[4], codeOf(prop), where);
            assertEquals(Integer.parseInt(row[5]), prop.getPower(), where);
            assertEquals(Integer.parseInt(row[6]), prop.getMultiplier(), where);
            assertEquals(Integer.parseInt(row[7]), ((Map<?, ?>) field(prop, "typeMults")).size(), where);
            assertEquals(Integer.parseInt(row[8]), prop.getBoundEntries().size(), where);
        }
        assertEquals("strength", items.get(0).getName());
        assertEquals("cold immunity", items.get(items.size() - 1).getName());
    }

    /**
     * Totals over the whole upstream file, worked out by the same script: power adds to 546,
     * multiplier to 244 and the type-mult figures to 236; 21 records carry a {@code msg:}, 22 a
     * {@code desc:}, five an {@code adjective:} and five a {@code neg-adjective:}; and the 66
     * {@code bindui:} lines include exactly three with an explicit third figure, 1, 2 and 3, on the
     * three digging flags.
     */
    @Test
    void realFileTotalsAgreeWithUpstream() throws IOException {
        List<ObjectProperty> items = new ObjectPropertyReader().parse(REAL_FILE);

        assertEquals(546, items.stream().mapToInt(ObjectProperty::getPower).sum());
        assertEquals(244, items.stream().mapToInt(ObjectProperty::getMultiplier).sum());
        assertEquals(236, items.stream().flatMap(p -> ((Map<?, ?>) field(p, "typeMults")).values().stream())
                .mapToInt(v -> (Integer) v).sum());
        assertEquals(21, items.stream().filter(p -> !p.getNoticeMessage().isEmpty()).count());
        assertEquals(22, items.stream().filter(p -> !((String) field(p, "description")).isEmpty()).count());
        assertEquals(5, items.stream().filter(p -> !((String) field(p, "adjective")).isEmpty()).count());
        assertEquals(5, items.stream().filter(p -> !((String) field(p, "negAdjective")).isEmpty()).count());
        assertEquals(66, items.stream().mapToInt(p -> p.getBoundEntries().size()).sum());
        assertEquals(List.of(1, 2, 3), items.stream().flatMap(p -> p.getBoundEntries().stream())
                .map(ObjectProperty.UIBinding::value).filter(v -> v != null).toList());
    }

    /**
     * {@link ObjectPropertyReader#parse} is the items-only view of {@link ObjectPropertyReader#parseWithResults}:
     * the same objects in the same order.
     */
    @Test
    void parseReturnsTheSameItemsAsParseWithResults() throws IOException {
        ObjectPropertyReader reader = new ObjectPropertyReader();

        List<ObjectProperty> parsed = reader.parse(REAL_FILE);
        List<ObjectProperty> detailed = reader.parseWithResults(REAL_FILE).items();

        assertEquals(detailed.size(), parsed.size());
        for (int i = 0; i < parsed.size(); i++) {
            assertEquals(detailed.get(i).getName(), parsed.get(i).getName());
        }
    }

    /**
     * A file that cannot be read is an {@link IOException} from both entry points, the analogue of
     * {@code parse_file_quit_not_found} in {@code run_parse_object_property}, which quits.
     */
    @Test
    void aMissingFileIsAnIOExceptionFromBothEntryPoints() {
        String missing = tempDir.resolve("no-such-file.txt").toString();

        assertThrows(IOException.class, () -> new ObjectPropertyReader().parse(missing));
        assertThrows(IOException.class, () -> new ObjectPropertyReader().parseWithResults(missing));
    }

    /**
     * A hard grammar error gives no items at all, and {@code parse} cannot tell it from an empty file.
     */
    @Test
    void aSyntaxErrorGivesNoItemsAndParseHidesTheMessages() throws IOException {
        String path = tempFile("syntax.txt", "record-count:1\nname:x\ntype:stat\nbogus-directive:1\n");

        assertTrue(new ObjectPropertyReader().parse(path).isEmpty());
        assertTrue(new ObjectPropertyReader().parseWithResults(path).hasErrors());
    }

    /**
     * An assembler error ends the load, as C stops at the first parse error: no record before or
     * after the bad one comes back. The middle record's {@code subtype:} is not one of C's nine.
     */
    @Test
    void anAssemblerErrorEndsTheLoad() throws IOException {
        assertStopsTheLoad("""
                record-count:3
                name:first
                type:flag
                code:FREE_ACT
                name:second
                type:flag
                subtype:no such subtype
                code:SEE_INVIS
                name:third
                type:flag
                code:PROT_FEAR
                """, 5, "an illegal subtype value: no such subtype");
    }

    /**
     * Two records with the same {@code name:} both load, in file order, as in C where nothing
     * checks for a repeated name.
     */
    @Test
    void duplicateNamesBothLoadInFileOrder() throws IOException {
        ParseResult<ObjectProperty> result = load("""
                record-count:2
                name:twin
                type:flag
                code:FREE_ACT
                power:1
                name:twin
                type:flag
                code:SEE_INVIS
                power:2
                """);

        assertEquals(List.of(), result.errors());
        assertEquals(2, result.items().size());
        assertEquals(1, result.items().get(0).getPower());
        assertEquals(2, result.items().get(1).getPower());
    }

    /**
     * The directives after {@code type:} may come in any order, as C's handlers are independent of
     * each other.
     */
    @Test
    void directivesAfterTypeMayComeInAnyOrder() throws IOException {
        ParseResult<ObjectProperty> result = load("""
                record-count:1
                name:jumbled
                type:stat
                mult:7
                type-mult:gloves:2
                neg-adjective:weak
                power:3
                adjective:strong
                code:STR
                desc:some text
                msg:a message
                """);

        assertEquals(List.of(), result.errors());
        ObjectProperty prop = result.items().get(0);
        assertEquals(7, prop.getMultiplier());
        assertEquals(3, prop.getPower());
        assertEquals("STR", codeOf(prop));
        assertEquals("some text", field(prop, "description"));
        assertEquals("a message", prop.getNoticeMessage());
    }

    /**
     * An omitted {@code power:} or {@code mult:} is 0, as {@code mem_zalloc} leaves it in
     * {@code parse_object_property_name}.
     */
    @Test
    void absentPowerAndMultAreZero() throws IOException {
        ObjectProperty prop = load("record-count:1\nname:bare\ntype:flag\ncode:FREE_ACT\n").items().get(0);

        assertEquals(0, prop.getPower());
        assertEquals(0, prop.getMultiplier());
    }

    /**
     * Negative figures are ordinary {@code int}s in both: {@code parser_getint} accepts a minus sign.
     */
    @Test
    void negativePowerAndMultAreAccepted() throws IOException {
        ObjectProperty prop = load("record-count:1\nname:bad\ntype:flag\ncode:FREE_ACT\npower:-5\nmult:-2\n")
                .items().get(0);

        assertEquals(-5, prop.getPower());
        assertEquals(-2, prop.getMultiplier());
    }

    /**
     * C sets every {@code type_mult} to 1 when the record starts and a {@code type-mult:} line
     * overwrites one slot; the port stores only the named tvals and {@code getTypeMult} supplies the 1.
     * An explicit 0 ("stop the property appearing on this type of random artifact") must survive and
     * not read as absent.
     */
    @Test
    void typeMultDefaultsToOneAndAnExplicitZeroSurvives() throws IOException {
        ObjectProperty prop = load("""
                record-count:1
                name:dexterity
                type:stat
                code:DEX
                type-mult:gloves:0
                type-mult:boots:3
                """).items().get(0);

        assertEquals(0, prop.getTypeMult(TValue.TV_GLOVES));
        assertEquals(3, prop.getTypeMult(TValue.TV_BOOTS));
        assertEquals(1, prop.getTypeMult(TValue.TV_SWORD));
    }

    /**
     * Naming one tval twice keeps the later figure, as C's second assignment to the same
     * {@code type_mult} slot does.
     */
    @Test
    void aRepeatedTypeMultTvalKeepsTheLaterFigure() throws IOException {
        ObjectProperty prop = load("""
                record-count:1
                name:dexterity
                type:stat
                code:DEX
                type-mult:gloves:2
                type-mult:gloves:5
                """).items().get(0);

        assertEquals(5, prop.getTypeMult(TValue.TV_GLOVES));
    }

    /**
     * {@code tval_find_idx} compares names with {@code my_stricmp} after {@code de_armour} turns
     * {@code armour} into {@code armor}, so upper case and the British spelling both resolve.
     */
    @Test
    void typeMultTvalNamesIgnoreCaseAndAcceptArmourSpelling() throws IOException {
        ObjectProperty prop = load("""
                record-count:1
                name:dexterity
                type:stat
                code:DEX
                type-mult:GLOVES:2
                type-mult:soft armour:4
                """).items().get(0);

        assertEquals(2, prop.getTypeMult(TValue.TV_GLOVES));
        assertEquals(4, prop.getTypeMult(TValue.TV_SOFT_ARMOR));
    }

    /**
     * A {@code type-mult:} naming no tval is {@code PARSE_ERROR_UNRECOGNISED_TVAL} in C, which is
     * fatal, and so it is here: the load ends.
     */
    @Test
    void anUnknownTypeMultTvalEndsTheLoad() throws IOException {
        assertStopsTheLoad("""
                record-count:1
                name:dexterity
                type:stat
                code:DEX
                type-mult:not a tval:2
                type-mult:gloves:3
                """, 2, "an illegal tValue value: not a tval");
    }

    /**
     * {@code code:} before {@code type:} is {@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE} in C, fatal, and
     * a syntax error here: the record shape puts {@code type:} straight after {@code name:}.
     */
    @Test
    void codeBeforeTypeIsAHardError() throws IOException {
        ParseResult<ObjectProperty> result = load("record-count:1\nname:x\ncode:STR\ntype:stat\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * A {@code type:} that is not one of C's seven words is {@code PARSE_ERROR_INVALID_PROPERTY},
     * fatal in C, and so it is here: the load ends.
     */
    @Test
    void anUnknownTypeEndsTheLoad() throws IOException {
        assertStopsTheLoad("record-count:1\nname:x\ntype:resist\ncode:ACID\n", 2, "an illegal type value: resist");
    }

    /**
     * A {@code code:} that is not in the table the {@code type:} selects is
     * {@code PARSE_ERROR_INVALID_OBJ_PROP_CODE}; the same word is valid under another type
     * ({@code LIGHT} is both a modifier and an element), and the type decides.
     */
    @Test
    void theTypeDecidesWhichTableACodeIsLookedUpIn() throws IOException {
        ParseResult<ObjectProperty> result = load("""
                record-count:2
                name:light modifier
                type:mod
                code:LIGHT
                name:light resistance
                type:resistance
                code:LIGHT
                """);

        assertEquals(List.of("light modifier", "light resistance"),
                result.items().stream().map(ObjectProperty::getName).toList());
        assertEquals(ObjectModifier.OM_LIGHT, result.items().get(0).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
        assertEquals(ElementEnum.ELEM_LIGHT, result.items().get(1).getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
        assertStopsTheLoad("record-count:1\nname:light flag\ntype:flag\ncode:LIGHT\n", 2,
                "an illegal code value: LIGHT");
    }

    // ---- places the grammar or assembler differs from C (see docs/grammar_issues.md and Assembler_Issues.md) ----

    /**
     * C's {@code parse_object_property_msg} and {@code parse_object_property_desc} free the old
     * string and make a new one, so a second line replaces the first. {@code ObjectPropertyGrammar}
     * appends to a {@code StringBuilder}, so two lines run together.
     */
    @Test
    void aSecondMsgOrDescLineIsAppendedHereWhereCReplacesIt() throws IOException {
        ObjectProperty prop = load("""
                record-count:1
                name:twice
                type:flag
                code:FREE_ACT
                msg:A
                msg:B
                desc:C
                desc:D
                """).items().get(0);

        assertEquals("AB", prop.getNoticeMessage(), "C gives B");
        assertEquals("CD", field(prop, "description"), "C gives D");
    }

    /**
     * The header of {@code object_property.txt} says {@code bindui} "can appear multiple times to bind
     * a property to more than one user interface element", and C binds each line as it reads it. The
     * grammar keeps one set of locals per record, so the last line wins and the earlier ones are lost
     * before the assembler sees them.
     */
    @Test
    void aSecondBinduiLineReplacesTheFirstHereWhereCBindsBoth() throws IOException {
        ObjectProperty prop = load("""
                record-count:1
                name:strength
                type:stat
                code:STR
                bindui:first_entry:0
                bindui:second_entry:1
                """).items().get(0);

        assertEquals(1, prop.getBoundEntries().size(), "C binds both");
        assertEquals("second_entry", prop.getBoundEntries().get(0).entry());
        assertTrue(prop.getBoundEntries().get(0).aux());
    }

    /**
     * C accepts a record with no {@code type:} (it stays {@code OBJ_PROPERTY_NONE}), and a
     * {@code type:} after other directives. The record shape here is {@code name:} then {@code type:}
     * then the rest, so both are syntax errors.
     */
    @Test
    void aMissingOrLateTypeIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        ParseResult<ObjectProperty> none = load("record-count:1\nname:x\npower:1\n");
        ParseResult<ObjectProperty> late = load("record-count:1\nname:x\npower:1\ntype:flag\ncode:FREE_ACT\n");

        assertTrue(none.items().isEmpty() && none.hasErrors());
        assertTrue(late.items().isEmpty() && late.hasErrors());
    }

    /**
     * A second {@code type:} overwrites the first in C. In the grammar {@code type:} is not among the
     * repeatable directives, so it is a syntax error.
     */
    @Test
    void aSecondTypeLineIsAHardErrorHereWhereCOverwrites() throws IOException {
        ParseResult<ObjectProperty> result = load("record-count:1\nname:x\ntype:stat\ntype:mod\ncode:STR\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * A file with the header and no records loads nothing in C ({@code property_max} becomes 1). The
     * grammar's {@code file} rule needs at least one record.
     */
    @Test
    void aFileWithNoRecordsIsAHardErrorHereWhereCLoadsNothing() throws IOException {
        ParseResult<ObjectProperty> result = load("record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * C stores {@code subtype:} on any record; the comment says it is "currently only used by flags"
     * but nothing checks. The assembler does the same and keeps it.
     */
    @Test
    void aSubtypeOnANonFlagIsKeptAsInC() throws IOException {
        ParseResult<ObjectProperty> result = load("record-count:1\nname:x\ntype:stat\nsubtype:sustain\ncode:STR\n");

        assertEquals(List.of(), result.errors());
        assertEquals(ObjectFlagType.OFT_SUST, result.items().get(0).getSubtype());
    }

    /**
     * {@code obj_mods} lists the five stats and the eleven modifiers and nothing else, and
     * {@code element_names} holds only the elements, so {@code NONE} and {@code MAX} on a stat, a
     * modifier or an element are {@code PARSE_ERROR_INVALID_OBJ_PROP_CODE}. {@code obj_flags} is
     * different: it starts with {@code "NONE"} and {@code list-object-flags.h} ends with
     * {@code OF(MAX, "")}, so both are valid names for a flag.
     */
    @Test
    void sentinelCodesEndTheLoadOnEveryTableButFlags() throws IOException {
        String[][] cases = {
                {"mod", "NONE"}, {"mod", "MAX"}, {"stat", "NONE"}, {"stat", "MAX"},
                {"resistance", "NONE"}, {"resistance", "MAX"}};
        for (String[] c : cases) {
            assertStopsTheLoad("record-count:1\nname:x\ntype:" + c[0] + "\ncode:" + c[1] + "\n", 2,
                    "an illegal sentinel");
        }
        assertEquals(ObjectFlag.OF_NONE, load("record-count:1\nname:x\ntype:flag\ncode:NONE\n").items().get(0)
                .getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
        assertEquals(ObjectFlag.OF_MAX, load("record-count:1\nname:x\ntype:flag\ncode:MAX\n").items().get(0)
                .getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    /**
     * C reads {@code adjective:} and {@code neg-adjective:} as {@code str}, the whole rest of the line.
     * The lexer takes one lower-case word, so a space or a capital is a lexer error and the file loads as
     * nothing.
     */
    @Test
    void anAdjectiveWithASpaceOrACapitalIsAHardErrorHereWhereCTakesAnyString() throws IOException {
        ParseResult<ObjectProperty> spaced = load("record-count:1\nname:x\ntype:stat\ncode:STR\nadjective:very strong\n");
        ParseResult<ObjectProperty> capital = load("record-count:1\nname:x\ntype:stat\ncode:STR\nadjective:Strong\n");

        assertTrue(spaced.items().isEmpty() && spaced.hasErrors());
        assertTrue(capital.items().isEmpty() && capital.hasErrors());
    }

    /**
     * C tests {@code parser_getint(p, "aux") != 0}, and {@code strtol} reads {@code 00} and {@code -0}
     * as zero, so all three are false.
     */
    @Test
    void anAuxWrittenAsZeroInAnySpellingIsFalse() throws IOException {
        ObjectProperty padded = load("record-count:1\nname:x\ntype:stat\ncode:STR\nbindui:entry:00\n").items().get(0);
        ObjectProperty negative = load("record-count:1\nname:x\ntype:stat\ncode:STR\nbindui:entry:-0\n").items().get(0);
        ObjectProperty plain = load("record-count:1\nname:x\ntype:stat\ncode:STR\nbindui:entry:0\n").items().get(0);

        assertFalse(padded.getBoundEntries().get(0).aux());
        assertFalse(negative.getBoundEntries().get(0).aux());
        assertFalse(plain.getBoundEntries().get(0).aux());
    }

    /**
     * {@code tval_find_idx} also accepts a number below {@code TV_MAX}, so {@code type-mult:3:2} is
     * valid in C. In the {@code DELIMITER} mode a bare number lexes as {@code DELIM_INTEGER}, and the
     * grammar wants a symbol there, so it is a syntax error.
     */
    @Test
    void aNumericTvalInTypeMultIsAHardErrorHereWhereCAcceptsIt() throws IOException {
        ParseResult<ObjectProperty> result = load("record-count:1\nname:x\ntype:stat\ncode:STR\ntype-mult:3:2\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * C reads {@code power:} and {@code mult:} with {@code strtol} in base 0: {@code 010} is 8, and
     * {@code +5} and {@code 0x10} are numbers. The assembler reads {@code 010} as 8 as C does, but the
     * lexer's {@code INTEGER} is decimal only, so {@code +5} and {@code 0x10} are lexer errors.
     */
    @Test
    void anOctalPowerIsReadAsInCButASignOrHexIsALexerError() throws IOException {
        ObjectProperty octal = load("record-count:1\nname:x\ntype:stat\ncode:STR\npower:010\n").items().get(0);
        ParseResult<ObjectProperty> plus = load("record-count:1\nname:x\ntype:stat\ncode:STR\npower:+5\n");
        ParseResult<ObjectProperty> hex = load("record-count:1\nname:x\ntype:stat\ncode:STR\npower:0x10\n");

        assertEquals(8, octal.getPower());
        assertTrue(plus.items().isEmpty() && plus.hasErrors());
        assertTrue(hex.items().isEmpty() && hex.hasErrors());
    }

    /**
     * {@code parser_parse} skips leading blanks on every line, so an indented directive is valid in C.
     * The shared {@code CommentsAndEol} rules do not, and the file loads as nothing.
     */
    @Test
    void anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        ParseResult<ObjectProperty> result = load("record-count:1\nname:x\ntype:stat\n  code:STR\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    /**
     * A record with no {@code code:} line is valid in C: {@code parse_object_property_name} zero-fills
     * the record, so {@code index} is 0, which for a stat is {@code STR}. The assembler gives the
     * same.
     */
    @Test
    void aRecordWithNoCodeHoldsIndexZeroAsInC() throws IOException {
        ParseResult<ObjectProperty> result = load("record-count:1\nname:x\ntype:stat\npower:1\n");

        assertEquals(List.of(), result.errors());
        assertEquals(ObjectModifier.OM_STR, result.items().get(0).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_STAT));
    }
}
