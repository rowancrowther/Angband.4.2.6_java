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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.enums.*;
import uk.co.jackoftradesltd.testsupport.UIRegistryFixture;

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
 * to nothing. Injected-defect tests check that a syntax or assembler error ends the load.
 *
 * <p>{@link ObjectProperty} has getters for most fields, which the newer tests use; the adjectives,
 * the description and the type-mult map have none, so those reads go through the
 * {@link #field(ObjectProperty, String)} reflection helper, as the older tests do for everything.
 * The assembler looks each {@code bindui} name, with its {@code <TAG>} folded in, up in the UI
 * registry as C does, and drops the bind if no entry has that name. These tests therefore load the
 * real entries and add the invented names some of them use, through {@code UIRegistryFixture}.
 *
 * @author Rowan Crowther
 */
class ObjectPropertyReaderTest {

    private static final String REAL_FILE = "lib/gamedata/object_property.txt";

    /**
     * The UI registry as the previous test class left it, put back by {@link #restoreUIEntries()}.
     */
    private static UIRegistryFixture.Saved savedUIEntries;

    /**
     * Loads the real UI entries. The assembler drops a {@code bindui:} whose entry it cannot find,
     * as C does, and throws if the registry is not loaded at all.
     *
     * @throws IOException if a UI data file cannot be read
     */
    @BeforeAll
    static void loadUIEntries() throws IOException {
        savedUIEntries = UIRegistryFixture.loadReal();
        // Entry names the tests below invent; the real file does not hold them.
        UIRegistryFixture.addEntries("entry", "entry<TAG>", "first_entry", "second_entry", "face<FACE>");
    }

    /**
     * Puts the UI registry back as it was found.
     */
    @AfterAll
    static void restoreUIEntries() {
        UIRegistryFixture.restore(savedUIEntries);
    }

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
                name:dexterity
                type:stat
                code:DEX
                power:8
                type-mult:gloves:2
                type-mult:dragon armor:3
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

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
                name:sustain strength
                type:flag
                subtype:sustain
                id-type:on effect
                code:SUST_STR
                power:9
                bindui:stat_mod_ui_compact_0<STR>:1
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

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
                name:power 1 digging
                type:flag
                subtype:dig
                id-type:on wield
                code:DIG_1
                power:3
                bindui:tunneling_ui_compact_0:0:1
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

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
                name:multiply
                type:flag
                subtype:curse-only
                id-type:on wield
                code:MULTIPLY_WEIGHT
                power:0
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

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
                name:free action
                type:flag
                subtype:misc ability
                id-type:on effect
                code:FREE_ACT
                power:20
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

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
                name:strength
                type:stat
                code:STR
                power:9
                mult:13
                adjective:strong
                neg-adjective:weak
                """);

        ParseResult<ObjectProperty> result = new ObjectPropertyReader().parseWithResults(path);

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

    /**
     * NOT_A_REAL_FLAG is not in {@code obj_flags}, so {@code code_index_in_array} gives -1 and C
     * returns {@code PARSE_ERROR_INVALID_OBJ_PROP_CODE}: the load ends.
     */
    @Test
    void anUnresolvableCodeEndsTheLoad() throws IOException {
        assertStopsTheLoad(
                "name:bogus\ntype:flag\nsubtype:bad\nid-type:on wield\ncode:NOT_A_REAL_FLAG\npower:0\n",
                5, "an unknown Object Property flag value: NOT_A_REAL_FLAG");
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
     * the line of the directive at fault (the assembler applies directives in file order, so a
     * record has no line of its own) and the file, and nothing is returned.
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
        assertEquals(21, items.stream().filter(p -> p.getNoticeMessage() != null).count());
        assertEquals(22, items.stream().filter(p -> field(p, "description") != null).count());
        assertEquals(5, items.stream().filter(p -> field(p, "adjective") != null).count());
        assertEquals(5, items.stream().filter(p -> field(p, "negAdjective") != null).count());
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
     * A grammar error is thrown from both entry points, so {@code parse} can no longer mistake it for
     * an empty file.
     *
     * <p>Function bothEntryPointsThrowOnASyntaxError coded on 261104, commented in full on 261104.
     * It replaces aSyntaxErrorGivesNoItemsAndParseHidesTheMessages.
     */
    @Test
    void bothEntryPointsThrowOnASyntaxError() throws IOException {
        String path = tempFile("syntax.txt", "name:x\ntype:stat\nbogus-directive:1\n");

        assertThrows(ParseCancellationException.class, () -> new ObjectPropertyReader().parse(path));
        assertThrows(ParseCancellationException.class, () -> new ObjectPropertyReader().parseWithResults(path));
    }

    /**
     * An assembler error ends the load, as C stops at the first parse error: no record before or
     * after the bad one comes back. The middle record's {@code subtype:} is not one of C's nine.
     */
    @Test
    void anAssemblerErrorEndsTheLoad() throws IOException {
        assertStopsTheLoad("""
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
                """, 6, "an unknown subtype value: no such subtype");
    }

    /**
     * Two records with the same {@code name:} both load, in file order, as in C where nothing
     * checks for a repeated name.
     */
    @Test
    void duplicateNamesBothLoadInFileOrder() throws IOException {
        ParseResult<ObjectProperty> result = load("""
                name:twin
                type:flag
                code:FREE_ACT
                power:1
                name:twin
                type:flag
                code:SEE_INVIS
                power:2
                """);

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
        ObjectProperty prop = load("name:bare\ntype:flag\ncode:FREE_ACT\n").items().get(0);

        assertEquals(0, prop.getPower());
        assertEquals(0, prop.getMultiplier());
    }

    /**
     * Negative figures are ordinary {@code int}s in both: {@code parser_getint} accepts a minus sign.
     */
    @Test
    void negativePowerAndMultAreAccepted() throws IOException {
        ObjectProperty prop = load("name:bad\ntype:flag\ncode:FREE_ACT\npower:-5\nmult:-2\n")
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
                name:dexterity
                type:stat
                code:DEX
                type-mult:not a tval:2
                type-mult:gloves:3
                """, 4, "an unknown TValue value: not a tval");
    }

    /**
     * {@code code:} before {@code type:} is {@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE} in C, fatal:
     * {@code parse_object_property_code} reads {@code prop->type} when it meets the line and finds 0.
     * {@code ObjectProperty2Assembler} applies the directives in file order and refuses the line the
     * same way, naming the {@code code:} line. A {@code type:} further down the record does not
     * rescue it.
     *
     * <p>Function aCodeBeforeTheTypeEndsTheLoadAsInC coded on 261115. It replaces
     * aCodeBeforeTheTypeLoadsHereWhereCRefusesIt.
     */
    @Test
    void aCodeBeforeTheTypeEndsTheLoadAsInC() throws IOException {
        assertStopsTheLoad("name:x\ncode:STR\ntype:stat\n", 2,
                "a code directive, with no type directive loaded yet");
    }

    /**
     * {@code bindui:} before {@code type:} is {@code PARSE_ERROR_MISSING_OBJ_PROP_TYPE} in C, fatal:
     * {@code parse_object_property_bindui} reads {@code prop->type} when it meets the line and finds 0.
     * The assembler refuses it at the {@code bindui:} line, whatever follows. Its message says "a code
     * directive" for a {@code bindui:} too; the directive's own name is not in it.
     *
     * <p>Function aBinduiBeforeTheTypeEndsTheLoadAsInC coded on 261115. It replaces
     * aBinduiBeforeTheTypeLoadsHereWhereCRefusesIt.
     */
    @Test
    void aBinduiBeforeTheTypeEndsTheLoadAsInC() throws IOException {
        assertStopsTheLoad("name:x\nbindui:foo:0\ntype:flag\ncode:FREE_ACT\n", 2,
                "a code directive, with no type directive loaded yet");
    }

    /**
     * C resolves the {@code code:} when it reads the line, and a later {@code type:} only overwrites
     * {@code prop->type}. So {@code type:stat}, {@code code:STR}, {@code type:flag} loads in C as a flag
     * whose index is 0: STR is entry 0 of {@code obj_mods}, and entry 0 of {@code obj_flags} is
     * {@code NONE}. The assembler carries the index across the {@code type:} line and does the same,
     * giving {@code OF_NONE}.
     *
     * <p>Function aSecondTypeAfterTheCodeKeepsTheFirstIndexAsInC coded on 261115. It replaces
     * aSecondTypeAfterTheCodeEndsTheLoadHereWhereCKeepsTheFirstIndex.
     */
    @Test
    void aSecondTypeAfterTheCodeKeepsTheFirstIndexAsInC() throws IOException {
        ObjectProperty prop = load("name:x\ntype:stat\ncode:STR\ntype:flag\n").items().get(0);

        assertEquals(ObjPropertyType.OBJ_PROPERTY_FLAG, prop.getType());
        assertEquals(ObjectFlag.OF_NONE, prop.getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    /**
     * A {@code type:} that is not one of C's seven words is {@code PARSE_ERROR_INVALID_PROPERTY},
     * fatal in C, and so it is here: the load ends.
     */
    @Test
    void anUnknownTypeEndsTheLoad() throws IOException {
        assertStopsTheLoad("name:x\ntype:resist\ncode:ACID\n", 2, "an unknown type: resist");
    }

    /**
     * A {@code code:} that is not in the table the {@code type:} selects is
     * {@code PARSE_ERROR_INVALID_OBJ_PROP_CODE}; the same word is valid under another type
     * ({@code LIGHT} is both a modifier and an element), and the type decides.
     */
    @Test
    void theTypeDecidesWhichTableACodeIsLookedUpIn() throws IOException {
        ParseResult<ObjectProperty> result = load("""
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
        assertStopsTheLoad("name:light flag\ntype:flag\ncode:LIGHT\n", 3,
                "an unknown Object Property flag value: LIGHT");
    }

    // ---- places the grammar or assembler differs from C (see docs/grammar_issues.md and Assembler_Issues.md) ----

    /**
     * C's {@code parse_object_property_msg} and {@code parse_object_property_desc} free the old
     * string and make a new one, so a second line replaces the first. The assembler does the same,
     * so the last line is the one kept.
     *
     * <p>Function aSecondMsgOrDescLineReplacesTheFirstAsInC coded on 261115. It replaces
     * aSecondMsgOrDescLineIsAppendedHereWhereCReplacesIt.
     */
    @Test
    void aSecondMsgOrDescLineReplacesTheFirstAsInC() throws IOException {
        ObjectProperty prop = load("""
                name:twice
                type:flag
                code:FREE_ACT
                msg:A
                msg:B
                desc:C
                desc:D
                """).items().get(0);

        assertEquals("B", prop.getNoticeMessage());
        assertEquals("D", field(prop, "description"));
    }

    /**
     * The header of {@code object_property.txt} says {@code bindui} "can appear multiple times to bind
     * a property to more than one user interface element", and C binds each line as it reads it. The
     * parse record keeps every {@code bindui:} line, and the assembler binds each, in file order.
     *
     * <p>Function aSecondBinduiLineBindsAsWellAsInC coded on 261115. It replaces
     * aSecondBinduiLineReplacesTheFirstHereWhereCBindsBoth.
     */
    @Test
    void aSecondBinduiLineBindsAsWellAsInC() throws IOException {
        ObjectProperty prop = load("""
                name:strength
                type:stat
                code:STR
                bindui:first_entry:0
                bindui:second_entry:1
                """).items().get(0);

        assertEquals(2, prop.getBoundEntries().size());
        assertEquals("first_entry", prop.getBoundEntries().get(0).entry());
        assertFalse(prop.getBoundEntries().get(0).aux());
        assertEquals("second_entry", prop.getBoundEntries().get(1).entry());
        assertTrue(prop.getBoundEntries().get(1).aux());
    }

    /**
     * C accepts a record with no {@code type:}: {@code parse_object_property_name} zero-fills the
     * property, so the type stays {@code OBJ_PROPERTY_NONE} and {@code index} stays 0. Here that is a
     * NONE property whose payload is NONE with {@code ELEM_NONE}. C also accepts a {@code type:} after
     * other directives, provided none of them is a {@code code:} or {@code bindui:}, which read the
     * type at once.
     */
    @Test
    void aMissingOrLateTypeLoadsAsInC() throws IOException {
        ObjectProperty bare = load("name:x\npower:1\n").items().get(0);

        assertEquals(ObjPropertyType.OBJ_PROPERTY_NONE, bare.getType());
        assertEquals(ObjPropertyType.OBJ_PROPERTY_NONE, bare.getPayload().getType());
        assertSame(ElementEnum.ELEM_NONE, bare.getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
        assertEquals(1, bare.getPower());

        ObjectProperty late = load("name:x\npower:1\ntype:flag\ncode:FREE_ACT\n").items().get(0);

        assertEquals(ObjPropertyType.OBJ_PROPERTY_FLAG, late.getType());
        assertEquals("FREE_ACT", codeOf(late));
        assertEquals(1, late.getPower());
    }

    /**
     * A second {@code type:} overwrites the first in C, which stores the word and nothing else. The
     * {@code code:} here comes after both, so it resolves against {@code obj_mods} in C and in Java.
     */
    @Test
    void aSecondTypeLineOverwritesTheFirstAsInC() throws IOException {
        ObjectProperty prop = load("name:x\ntype:stat\ntype:mod\ncode:STR\n").items().get(0);

        assertEquals(ObjPropertyType.OBJ_PROPERTY_MOD, prop.getType());
        assertEquals("STR", codeOf(prop));
    }

    /**
     * A file with no records loads nothing in C ({@code property_max} becomes 1, slot 0 only). The
     * list here is empty.
     */
    @Test
    void anEmptyFileLoadsNothingAsInC() throws IOException {
        assertTrue(load("").items().isEmpty());
    }

    /**
     * C stores {@code subtype:} on any record; the comment says it is "currently only used by flags"
     * but nothing checks. The assembler does the same and keeps it.
     */
    @Test
    void aSubtypeOnANonFlagIsKeptAsInC() throws IOException {
        ParseResult<ObjectProperty> result = load("name:x\ntype:stat\nsubtype:sustain\ncode:STR\n");

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
            assertStopsTheLoad("name:x\ntype:" + c[0] + "\ncode:" + c[1] + "\n", 3,
                    "an illegal sentinel");
        }
        assertEquals(ObjectFlag.OF_NONE, load("name:x\ntype:flag\ncode:NONE\n").items().get(0)
                .getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
        assertEquals(ObjectFlag.OF_MAX, load("name:x\ntype:flag\ncode:MAX\n").items().get(0)
                .getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    /**
     * C reads {@code adjective:} and {@code neg-adjective:} as {@code str}, the whole rest of the line,
     * and the port now does the same: a space or a capital loads, and the text is kept as written.
     *
     * <p>Function anAdjectiveWithASpaceOrACapitalLoadsAsInC coded on 261115. It replaces
     * anAdjectiveWithASpaceOrACapitalIsAHardErrorHereWhereCTakesAnyString.
     */
    @Test
    void anAdjectiveWithASpaceOrACapitalLoadsAsInC() throws IOException {
        assertEquals("very strong", field(load("name:x\ntype:stat\ncode:STR\nadjective:very strong\n")
                .items().get(0), "adjective"));
        assertEquals("Strong", field(load("name:x\ntype:stat\ncode:STR\nadjective:Strong\n")
                .items().get(0), "adjective"));
        assertEquals("Very Weak", field(load("name:x\ntype:stat\ncode:STR\nneg-adjective:Very Weak\n")
                .items().get(0), "negAdjective"));
    }

    /**
     * C tests {@code parser_getint(p, "aux") != 0}, and {@code strtol} reads {@code 00} and {@code -0}
     * as zero, so all three are false.
     */
    @Test
    void anAuxWrittenAsZeroInAnySpellingIsFalse() throws IOException {
        ObjectProperty padded = load("name:x\ntype:stat\ncode:STR\nbindui:entry:00\n").items().get(0);
        ObjectProperty negative = load("name:x\ntype:stat\ncode:STR\nbindui:entry:-0\n").items().get(0);
        ObjectProperty plain = load("name:x\ntype:stat\ncode:STR\nbindui:entry:0\n").items().get(0);

        assertFalse(padded.getBoundEntries().get(0).aux());
        assertFalse(negative.getBoundEntries().get(0).aux());
        assertFalse(plain.getBoundEntries().get(0).aux());
    }

    /**
     * {@code tval_find_idx} also accepts a number below {@code TV_MAX}, so {@code type-mult:3:2} is
     * valid in C, and names tval 3, {@code arrow} in {@code list-tvals.h}. The port loads it the same
     * way.
     *
     * <p>Function aNumericTvalInTypeMultLoadsAsInC coded on 261115. It replaces
     * aNumericTvalInTypeMultIsAHardErrorHereWhereCAcceptsIt.
     */
    @Test
    void aNumericTvalInTypeMultLoadsAsInC() throws IOException {
        ObjectProperty prop = load("name:x\ntype:stat\ncode:STR\ntype-mult:3:2\n").items().get(0);

        assertEquals(2, prop.getTypeMult(TValue.TV_ARROW));
    }

    /**
     * C reads every {@code int} field of this file with {@code strtol} in base 0: {@code power:},
     * {@code mult:}, the {@code type-mult:} figure, the {@code bindui:} aux flag and its optional value.
     * So {@code +5} is 5, {@code 0x10} and {@code 0X10} are 16, {@code 010} is 8, {@code -0x10} is -16
     * and {@code 08} is 0 (the {@code 8} is not an octal digit, so it stops the read). The lexer's
     * {@code INTEGER} and {@code DELIM_INTEGER} keep the text and the assembler converts it.
     *
     * <p>Function anyIntFieldIsReadInBaseZeroAsInC coded on 261112, commented in full on 261112. It
     * replaces anOctalPowerIsReadAsInCButASignOrHexIsALexerError.
     */
    @Test
    void anyIntFieldIsReadInBaseZeroAsInC() throws IOException {
        String[][] cases = {{"+5", "5"}, {"0x10", "16"}, {"0X1f", "31"}, {"010", "8"}, {"-0x10", "-16"},
                {"-010", "-8"}, {"08", "0"}, {"-7", "-7"}, {"0", "0"}};
        for (String[] c : cases) {
            int expected = Integer.parseInt(c[1]);
            ObjectProperty p = load("name:x\ntype:stat\ncode:STR\npower:" + c[0] + "\nmult:" + c[0]
                    + "\ntype-mult:ring:" + c[0] + "\n").items().get(0);
            assertEquals(expected, p.getPower(), "power:" + c[0]);
            assertEquals(expected, p.getMultiplier(), "mult:" + c[0]);
            assertEquals(expected, p.getTypeMult(TValue.TV_RING), "type-mult:ring:" + c[0]);
            ObjectProperty b = load("name:x\ntype:stat\ncode:STR\nbindui:entry:1:" + c[0] + "\n")
                    .items().get(0);
            assertEquals(expected, b.getBoundEntries().get(0).value(), "bindui value " + c[0]);
        }
    }

    /**
     * The aux flag is the same figure read as C's {@code parser_getint(p, "aux") != 0}: any spelling
     * of a non-zero number is true, any spelling of zero ({@code 0x0}, {@code 00}, {@code 08}) false.
     */
    @Test
    void auxInBaseZeroIsTrueExactlyWhenTheNumberIsNotZero() throws IOException {
        for (String t : new String[]{"+1", "0x10", "010", "-1", "0x1"})
            assertTrue(load("name:x\ntype:stat\ncode:STR\nbindui:entry:" + t + "\n").items().get(0)
                    .getBoundEntries().get(0).aux(), "aux " + t);
        for (String f : new String[]{"+0", "0x0", "00", "08"})
            assertFalse(load("name:x\ntype:stat\ncode:STR\nbindui:entry:" + f + "\n").items().get(0)
                    .getBoundEntries().get(0).aux(), "aux " + f);
    }

    /**
     * C reads {@code type:} and {@code code:} as {@code str}, which keeps the rest of the line, blanks
     * included, so {@code "flag "} and {@code "FREE_ACT "} match no table entry and C refuses the line.
     * The lexer's {@code LCASE_MODE} and {@code UCASE_MODE} end only on a bare line end, so a blank or
     * tab after the value is a lexer error and the file is refused, whatever follows the line.
     *
     * <p>Function aTrailingBlankAfterTypeOrCodeIsRefusedAsInC coded on 261115, commented in full on 261115.
     */
    @Test
    void aTrailingBlankAfterTypeOrCodeIsRefusedAsInC() {
        for (String pad : new String[]{" ", "  ", "\t", " \t "}) {
            String where = "pad [" + pad.replace("\t", "\\t") + "]";
            assertThrows(ParseCancellationException.class,
                    () -> load("name:x\ntype:flag" + pad + "\ncode:FREE_ACT\n"), "type: " + where);
            assertThrows(ParseCancellationException.class,
                    () -> load("name:x\ntype:flag\ncode:FREE_ACT" + pad + "\npower:1\n"), "code: " + where);
        }
    }

    /**
     * The same refusal when the blank is on the very last line and the file has no final newline. C
     * reads such a last line, and refuses the blank in it.
     *
     * <p>Function aTrailingBlankAfterTypeOrCodeOnTheLastLineIsRefusedAsInC coded on 261115, commented
     * in full on 261115.
     */
    @Test
    void aTrailingBlankAfterTypeOrCodeOnTheLastLineIsRefusedAsInC() {
        assertThrows(ParseCancellationException.class, () -> load("name:x\ntype:flag "));
        assertThrows(ParseCancellationException.class, () -> load("name:x\ntype:flag\ncode:FREE_ACT "));
        assertThrows(ParseCancellationException.class, () -> load("name:x\ntype:flag\ncode:FREE_ACT\t\n"));
    }

    /**
     * The clean lines still load, including a {@code type:} or {@code code:} on the last line with no
     * final newline, and with a comment line straight after: the new modes must pop back to the
     * default mode on the line end.
     *
     * <p>Function aTypeOrCodeWithNoBlankStillLoads coded on 261115, commented in full on 261115.
     */
    @Test
    void aTypeOrCodeWithNoBlankStillLoads() throws IOException {
        assertEquals("FREE_ACT", codeOf(load("name:x\ntype:flag\ncode:FREE_ACT").items().get(0)));
        assertEquals("FREE_ACT", codeOf(load("name:x\ntype:flag\ncode:FREE_ACT\n").items().get(0)));
        assertEquals("FREE_ACT", codeOf(load("name:x\ntype:flag\n# a comment\ncode:FREE_ACT\n# another\n")
                .items().get(0)));
        assertEquals("DIG_1", codeOf(load("name:x\ntype:flag\ncode:DIG_1\n").items().get(0)));
    }

    /**
     * C reads the figure of {@code power:}, {@code mult:}, {@code type-mult:} and the aux figure and
     * value of {@code bindui:} with {@code strtol}, which ignores whatever follows the digits, so a
     * blank or tab after the figure loads. The end-of-line rules for those lines are {@code NL}, which
     * takes the blanks, and the file's last line may have no final newline.
     *
     * <p>Function aTrailingBlankAfterAFigureIsIgnoredAsInC coded on 261115, commented in full on 261115.
     */
    @Test
    void aTrailingBlankAfterAFigureIsIgnoredAsInC() throws IOException {
        for (String pad : new String[]{" ", "  ", "\t", " \t "}) {
            for (String after : new String[]{"\n", "\nmsg:next\n", ""}) {
                String where = "pad [" + pad.replace("\t", "\\t") + "] then [" + after.replace("\n", "\\n") + "]";
                String head = "name:x\ntype:stat\ncode:STR\n";

                assertEquals(3, load(head + "power:3" + pad + after).items().get(0).getPower(), "power " + where);
                assertEquals(3, load(head + "mult:3" + pad + after).items().get(0).getMultiplier(), "mult " + where);
                assertEquals(3, load(head + "type-mult:gloves:3" + pad + after).items().get(0)
                        .getTypeMult(TValue.TV_GLOVES), "type-mult " + where);

                ObjectProperty aux = load(head + "bindui:entry:1" + pad + after).items().get(0);
                assertTrue(aux.getBoundEntries().get(0).aux(), "bindui aux " + where);
                assertNull(aux.getBoundEntries().get(0).value(), "bindui aux " + where);

                ObjectProperty tagged = load(head + "bindui:entry<TAG>:0" + pad + after).items().get(0);
                assertEquals("entry<TAG>", tagged.getBoundEntries().get(0).entry(), "bindui tag " + where);
                assertFalse(tagged.getBoundEntries().get(0).aux(), "bindui tag " + where);

                ObjectProperty valued = load(head + "bindui:entry:1:7" + pad + after).items().get(0);
                assertEquals(7, valued.getBoundEntries().get(0).value(), "bindui value " + where);
            }
        }
    }

    /**
     * C's {@code str} fields keep a trailing blank, and so does {@code STRING}: the blank is part of
     * the value of {@code name:}, {@code msg:}, {@code desc:}, {@code adjective:} and
     * {@code neg-adjective:}, and no end-of-line rule takes it.
     *
     * <p>Function aTrailingBlankInATextValueIsKeptAsInC coded on 261115, commented in full on 261115.
     */
    @Test
    void aTrailingBlankInATextValueIsKeptAsInC() throws IOException {
        ObjectProperty prop = load("name:x \ntype:stat\ncode:STR\nmsg:hello \ndesc:text\t\nadjective:fast  \n"
                + "neg-adjective:slow ").items().get(0);

        assertEquals("x ", prop.getName());
        assertEquals("hello ", prop.getNoticeMessage());
        assertEquals("text\t", field(prop, "description"));
        assertEquals("fast  ", field(prop, "adjective"));
        assertEquals("slow ", field(prop, "negAdjective"));
    }

    /**
     * {@code subtype:} and {@code id-type:} are {@code str} in C too, so a trailing blank makes the
     * word match no entry and C refuses it. Here {@code STRING} keeps the blank, the lexer lets it
     * through, and the assembler refuses it, naming the line.
     *
     * <p>Function aTrailingBlankAfterSubtypeOrIdTypeEndsTheLoad coded on 261115, commented in full on
     * 261115.
     */
    @Test
    void aTrailingBlankAfterSubtypeOrIdTypeEndsTheLoad() throws IOException {
        assertStopsTheLoad("name:x\ntype:flag\nsubtype:sustain \ncode:FREE_ACT\n", 3,
                "an unknown subtype value: sustain ");
        assertStopsTheLoad("name:x\ntype:flag\nid-type:timed \ncode:FREE_ACT\n", 3,
                "an unknown id-type value: timed ");
    }

    /**
     * A figure with no digit ({@code ff}, hex letters without {@code 0x}) is {@code NOT_NUMBER} in C
     * and ends the load here. It lexes as {@code INTEGER} (the rule accepts a-f) and the assembler
     * refuses it.
     */
    @Test
    void hexLettersWithoutAPrefixEndTheLoad() throws IOException {
        assertStopsTheLoad("name:x\ntype:stat\ncode:STR\npower:ff\n", 4, "an invalid power value: ff");
    }

    /**
     * {@code INTEGER} accepts the letters a-f, so a word made only of them ties on length with
     * {@code LCASE} and {@code INTEGER}, coming first in the lexer, wins the token. The directive
     * that wants a word then fails to parse, though C takes any string for an adjective.
     */
    @Test
    void anAdjectiveMadeOnlyOfHexLettersStillLoads() throws IOException {
        assertEquals("deaf", field(load("name:x\ntype:stat\ncode:STR\nadjective:deaf\n").items().get(0),
                "adjective"));
    }

    /** As {@link #anAdjectiveMadeOnlyOfHexLettersStillLoads()}, for {@code neg-adjective:}. */
    @Test
    void aNegAdjectiveMadeOnlyOfHexLettersStillLoads() throws IOException {
        assertEquals("bad", field(load("name:x\ntype:stat\ncode:STR\nneg-adjective:bad\n").items().get(0),
                "negAdjective"));
    }

    /** As {@link #anAdjectiveMadeOnlyOfHexLettersStillLoads()}, for the {@code bindui:} entry name. */
    @Test
    void aBinduiNameMadeOnlyOfHexLettersStillLoads() throws IOException {
        assertEquals("face<FACE>", load("name:x\ntype:stat\ncode:STR\nbindui:face<FACE>:1\n").items().get(0)
                .getBoundEntries().get(0).entry());
    }

    /**
     * {@code parser_parse} skips leading blanks on every line, so an indented directive is valid in C.
     * The {@code INDENT} rule in the shared {@code CommentsAndEol} does the same, so it loads.
     *
     * <p>Function anIndentedDirectiveLoadsAsInC coded on 261109, commented in full on 261109. It
     * replaces anIndentedDirectiveIsAHardErrorHereWhereCSkipsTheIndent.
     */
    @Test
    void anIndentedDirectiveLoadsAsInC() throws IOException {
        assertEquals("x", load("name:x\ntype:stat\n  code:STR\n").items().get(0).getName());
    }

    /**
     * A record with no {@code code:} line is valid in C: {@code parse_object_property_name} zero-fills
     * the record, so {@code index} is 0, which for a stat is {@code STR}. The assembler gives the
     * same.
     */
    @Test
    void aRecordWithNoCodeHoldsIndexZeroAsInC() throws IOException {
        ParseResult<ObjectProperty> result = load("name:x\ntype:stat\npower:1\n");

        assertEquals(ObjectModifier.OM_STR, result.items().get(0).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_STAT));
    }
}
