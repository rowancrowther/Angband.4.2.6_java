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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyAssembler;
import uk.co.jackoftradesltd.backend.parser.objectproperty.ObjectPropertyParseRecord;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ObjectPropertyAssembler}.
 *
 * <p>These build {@link ObjectPropertyParseRecord}s directly, with no grammar, so text the lexer
 * would never produce (a lower-case code, an empty type, a number the lexer would refuse) still
 * reaches the assembler. End-to-end cases against the shipped file live in
 * {@code ObjectPropertyReaderTest}.
 *
 * <p>The expected values are worked out from {@code obj-init.c}, not from the Java:
 * <ul>
 *   <li>{@code parse_object_property_type}, {@code _subtype} and {@code _id_type} are {@code streq}
 *       chains over seven, nine and three exact words, and anything else is a parser error that
 *       ends the load. They store the result on whatever the record's type is, so a
 *       {@code subtype:} on a stat is kept;</li>
 *   <li>{@code parse_object_property_code} looks the code up with {@code code_index_in_array},
 *       which is {@code streq} over {@code obj_mods} (stats, then modifiers, no sentinel),
 *       {@code obj_flags} (a leading {@code "NONE"}, every flag, and the {@code "MAX"} that ends
 *       {@code list-object-flags.h}) or {@code element_names} (the elements, no sentinel), chosen
 *       by the record's type. A name in no table is {@code PARSE_ERROR_INVALID_OBJ_PROP_CODE}. A
 *       record with no {@code code:} line keeps the zero {@code mem_zalloc} gave its index, which
 *       is the first entry of its table;</li>
 *   <li>{@code parse_object_property_power} and {@code _mult} use {@code parser_getint}, which is
 *       {@code strtol} in base 0, and an absent line leaves the zeroed 0;</li>
 *   <li>{@code parse_object_property_type_mult} resolves the tval with {@code tval_find_idx} and
 *       writes {@code type_mult[tval]} at once, so the last line for a tval wins whatever its
 *       spelling. {@code parse_object_property_name} first sets every slot to 1;</li>
 *   <li>{@code parse_object_property_bindui}: {@code isaux} is {@code parser_getint(p, "aux") != 0},
 *       and {@code have_val} is {@code parser_hasval(p, "uival")};</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 * Two accepted deviations are pinned as well: a number outside the {@code int} range is clamped
 * where C keeps the low 32 bits, and a tval written under two spellings with the first spelling
 * repeated can end on the earlier line's figure.
 *
 * <p>Class ObjectPropertyAssemblerTest coded on 261028, commented in full on 261028; it replaces
 * ObjectPropertyAssemblerTypeTest, which asserted the soft errors the assembler no longer gives.
 *
 * @author Rowan Crowther
 */
class ObjectPropertyAssemblerTest {

    private static final String FILE = "object_property.txt";

    /** A record with the given type, subtype, id-type and code and nothing else set. */
    private static ObjectPropertyParseRecord record(String type, String subtype, String idType, String code) {
        return full("test", type, subtype, idType, code, "", "", new LinkedHashMap<>(), "", "", "", "", 7);
    }

    private static ObjectPropertyParseRecord ofType(String type, String code) {
        return record(type, "", "", code);
    }

    private static ObjectPropertyParseRecord withNumbers(String power, String mult) {
        return full("test", "stat", "", "", "STR", power, mult,
                new LinkedHashMap<>(), "", "", "", "", 7);
    }

    private static ObjectPropertyParseRecord withTypeMult(Map<String, String> typeMult) {
        return full("test", "stat", "", "", "STR", "", "", typeMult, "", "", "", "", 7);
    }

    private static ObjectPropertyParseRecord withBindui(String bindui, String tag, String value, String aux) {
        return new ObjectPropertyParseRecord("test", "stat", "", "", "STR", "", "", new LinkedHashMap<>(),
                "", "", "", bindui, tag, value, aux, "", 7);
    }

    private static ObjectPropertyParseRecord full(String name, String type, String subtype, String idType,
                                                  String code, String power, String mult,
                                                  Map<String, String> typeMult, String adjective,
                                                  String negAdjective, String msg, String desc, int line) {
        return new ObjectPropertyParseRecord(name, type, subtype, idType, code, power, mult, typeMult,
                adjective, negAdjective, msg, "", "", null, "", desc, line);
    }

    private static List<ObjectProperty> assemble(ObjectPropertyParseRecord... records) {
        return new ObjectPropertyAssembler().assemble(List.of(records), new ArrayList<>(), FILE);
    }

    private static ObjectProperty one(ObjectPropertyParseRecord record) {
        List<ObjectProperty> properties = assemble(record);
        assertEquals(1, properties.size());
        return properties.getFirst();
    }

    /**
     * A bad field ends the load, as every parse error does in C ({@code print_error} in
     * {@code datafile.c}): the message names the record's line and the file, and ends with the
     * given tail.
     */
    private static void assertStopsTheLoad(ObjectPropertyParseRecord record, String tail) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> assemble(record));
        assertTrue(thrown.getMessage().contains(tail), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 7"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(FILE), thrown::getMessage);
    }

    private static Object field(ObjectProperty prop, String name) {
        try {
            Field f = ObjectProperty.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(prop);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> typeMults(String... keysAndValues) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }

    // ---- type -------------------------------------------------------------------------------

    /** The code resolves against the table the type selects, so each pairing is a distinct path. */
    @Test
    void everyRealTypeAssemblesAgainstItsOwnTable() {
        List<ObjectProperty> result = assemble(
                ofType("stat", "STR"),
                ofType("mod", "STEALTH"),
                ofType("flag", "SLOW_DIGEST"),
                ofType("ignore", "ACID"),
                ofType("resistance", "ACID"),
                ofType("vulnerability", "FIRE"),
                ofType("immunity", "COLD"));

        assertEquals(List.of(
                        ObjPropertyType.OBJ_PROPERTY_STAT, ObjPropertyType.OBJ_PROPERTY_MOD,
                        ObjPropertyType.OBJ_PROPERTY_FLAG, ObjPropertyType.OBJ_PROPERTY_IGNORE,
                        ObjPropertyType.OBJ_PROPERTY_RESIST, ObjPropertyType.OBJ_PROPERTY_VULN,
                        ObjPropertyType.OBJ_PROPERTY_IMM),
                result.stream().map(ObjectProperty::getType).toList());
        assertEquals(ObjectModifier.OM_STR, result.get(0).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_STAT));
        assertEquals(ObjectModifier.OM_STEALTH, result.get(1).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
        assertEquals(ObjectFlag.OF_SLOW_DIGEST, result.get(2).getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
        assertEquals(ElementEnum.ELEM_ACID, result.get(3).getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_IGNORE));
        assertEquals(ElementEnum.ELEM_ACID, result.get(4).getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
        assertEquals(ElementEnum.ELEM_FIRE, result.get(5).getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_VULN));
        assertEquals(ElementEnum.ELEM_COLD, result.get(6).getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_IMM));
    }

    /** C: a word outside the seven falls through the whole streq chain to PARSE_ERROR_INVALID_PROPERTY. */
    @ParameterizedTest
    @ValueSource(strings = {"max", "none", "resist", "Stat", "STAT", " stat", "stat ", "immune"})
    void anUnrecognizedTypeEndsTheLoad(String type) {
        assertStopsTheLoad(ofType(type, "STR"), "an illegal type value: " + type);
    }

    /** The grammar never gives an empty type, but a record built by hand can; there is nothing to resolve. */
    @Test
    void anEmptyTypeEndsTheLoad() {
        assertStopsTheLoad(ofType("", "STR"), "no type value");
    }

    // ---- subtype and id-type ----------------------------------------------------------------

    @ParameterizedTest
    @CsvSource({
            "sustain, OFT_SUST", "protection, OFT_PROT", "misc ability, OFT_MISC", "light, OFT_LIGHT",
            "melee, OFT_MELEE", "bad, OFT_BAD", "dig, OFT_DIG", "throw, OFT_THROW",
            "curse-only, OFT_CURSE_ONLY"})
    void eachOfTheNineSubtypeWordsResolves(String word, ObjectFlagType expected) {
        assertSame(expected, one(record("flag", word, "", "FEATHER")).getSubtype());
    }

    /** C: an absent subtype: line leaves the zeroed OFT_NONE. */
    @Test
    void anAbsentSubtypeIsOftNone() {
        assertSame(ObjectFlagType.OFT_NONE, one(record("flag", "", "", "FEATHER")).getSubtype());
    }

    /** C: PARSE_ERROR_INVALID_SUBTYPE. The match is exact, so case and the abbreviation both fail. */
    @ParameterizedTest
    @ValueSource(strings = {"Sustain", "sust", "misc", "curse_only", "max", " bad"})
    void anUnrecognizedSubtypeEndsTheLoad(String subtype) {
        assertStopsTheLoad(record("flag", subtype, "", "FEATHER"), "an illegal subtype value: " + subtype);
    }

    /** C stores subtype: on whatever the record is; the data file's comment says "only used by flags" but nothing checks. */
    @Test
    void aSubtypeOnANonFlagIsKept() {
        assertSame(ObjectFlagType.OFT_SUST, one(record("stat", "sustain", "", "STR")).getSubtype());
    }

    @ParameterizedTest
    @CsvSource({"on effect, OFID_NORMAL", "timed, OFID_TIMED", "on wield, OFID_WIELD"})
    void eachOfTheThreeIdTypeWordsResolves(String word, ObjectFlagID expected) {
        assertSame(expected, one(record("flag", "", word, "FEATHER")).getIdType());
    }

    @Test
    void anAbsentIdTypeIsOfidNone() {
        assertSame(ObjectFlagID.OFID_NONE, one(record("flag", "", "", "FEATHER")).getIdType());
    }

    /** C: PARSE_ERROR_INVALID_ID_TYPE. */
    @ParameterizedTest
    @ValueSource(strings = {"normal", "On Effect", "on  effect", "wield", "none"})
    void anUnrecognizedIdTypeEndsTheLoad(String idType) {
        assertStopsTheLoad(record("flag", "", idType, "FEATHER"), "an illegal id type value: " + idType);
    }

    @Test
    void anIdTypeOnANonFlagIsKept() {
        assertSame(ObjectFlagID.OFID_WIELD, one(record("mod", "", "on wield", "SPEED")).getIdType());
    }

    // ---- code -------------------------------------------------------------------------------

    /** The same word is a modifier under one type and an element under another; the type decides. */
    @Test
    void theTypeDecidesWhichTableACodeIsLookedUpIn() {
        List<ObjectProperty> result = assemble(ofType("mod", "LIGHT"), ofType("resistance", "LIGHT"));

        assertEquals(ObjectModifier.OM_LIGHT, result.get(0).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
        assertEquals(ElementEnum.ELEM_LIGHT, result.get(1).getPayload().getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
        assertStopsTheLoad(ofType("flag", "LIGHT"), "an illegal code value: LIGHT");
    }

    /** {@code stat} and {@code mod} both read {@code obj_mods}, so either type takes a stat or a modifier. */
    @Test
    void statAndModShareOneTable() {
        assertEquals(ObjectModifier.OM_SPEED,
                one(ofType("stat", "SPEED")).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_STAT));
        assertEquals(ObjectModifier.OM_CON,
                one(ofType("mod", "CON")).getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
    }

    /** All four element types read {@code element_names}. */
    @ParameterizedTest
    @ValueSource(strings = {"ignore", "resistance", "vulnerability", "immunity"})
    void everyElementTypeReadsTheElementTable(String type) {
        ObjectProperty prop = one(ofType(type, "NEXUS"));

        assertEquals(ElementEnum.ELEM_NEXUS, prop.getPayload().getElement(prop.getType()));
    }

    /** C's obj_flags starts with "NONE" and list-object-flags.h ends with OF(MAX, ""), so both are valid names for a flag. */
    @Test
    void theNoneAndMaxFlagsAreValidCodesForAFlag() {
        assertEquals(ObjectFlag.OF_NONE,
                one(ofType("flag", "NONE")).getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
        assertEquals(ObjectFlag.OF_MAX,
                one(ofType("flag", "MAX")).getPayload().getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
    }

    /** C: obj_mods and element_names hold only real entries, so NONE and MAX are PARSE_ERROR_INVALID_OBJ_PROP_CODE. */
    @ParameterizedTest
    @CsvSource({"stat, NONE", "stat, MAX", "mod, NONE", "mod, MAX",
            "ignore, NONE", "ignore, MAX", "resistance, NONE", "resistance, MAX",
            "vulnerability, NONE", "vulnerability, MAX", "immunity, NONE", "immunity, MAX"})
    void aSentinelCodeEndsTheLoadOnEveryTableButFlags(String type, String code) {
        assertStopsTheLoad(ofType(type, code), "an illegal sentinel");
    }

    /** C: code_index_in_array is streq over the whole table, so a name in none of it, or in the wrong case, fails. */
    @ParameterizedTest
    @ValueSource(strings = {"NOT_A_REAL_FLAG", "slow_digest", "Slow_Digest", "OF_SLOW_DIGEST", "SLOW DIGEST", "STR"})
    void aCodeNotInTheFlagTableEndsTheLoad(String code) {
        assertStopsTheLoad(ofType("flag", code), "an illegal code value: " + code);
    }

    @ParameterizedTest
    @CsvSource({"stat, str", "mod, Stealth", "resistance, acid", "immunity, ELEM_ACID"})
    void aCodeInTheWrongCaseOrSpellingEndsTheLoad(String type, String code) {
        assertStopsTheLoad(ofType(type, code), "an illegal code value: " + code);
    }

    /** C: streq is exact, so a trailing space is a different name. */
    @Test
    void aCodeWithATrailingSpaceEndsTheLoad() {
        assertStopsTheLoad(ofType("ignore", "ACID "), "an illegal code value: ACID ");
    }

    /** C: a record with no code: line keeps the zero mem_zalloc gave its index, the first entry of its table. */
    @ParameterizedTest
    @CsvSource({"flag, OF_NONE", "stat, OM_STR", "mod, OM_STR"})
    void anAbsentCodeIsIndexZeroOfTheTable(String type, String expected) {
        ObjectProperty prop = one(ofType(type, ""));
        Object payload = switch (prop.getType()) {
            case OBJ_PROPERTY_FLAG -> prop.getPayload().getFlag(prop.getType());
            default -> prop.getPayload().getModifier(prop.getType());
        };

        assertEquals(expected, payload.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ignore", "resistance", "vulnerability", "immunity"})
    void anAbsentCodeOnAnElementTypeIsAcid(String type) {
        ObjectProperty prop = one(ofType(type, ""));

        assertEquals(ElementEnum.ELEM_ACID, prop.getPayload().getElement(prop.getType()));
    }

    // ---- power, mult ------------------------------------------------------------------------

    @Test
    void absentPowerAndMultAreZero() {
        ObjectProperty prop = one(withNumbers("", ""));

        assertEquals(0, prop.getPower());
        assertEquals(0, prop.getMultiplier());
    }

    /** parser_getint is strtol in base 0: 010 is octal, 0x10 is hexadecimal, a sign is allowed. */
    @ParameterizedTest
    @CsvSource({"9, 9", "010, 8", "0x10, 16", "-5, -5", "+7, 7", "00, 0", "-0, 0"})
    void powerAndMultAreReadAsStrtolInBaseZero(String text, int expected) {
        ObjectProperty prop = one(withNumbers(text, text));

        assertEquals(expected, prop.getPower());
        assertEquals(expected, prop.getMultiplier());
    }

    /** Accepted deviation: C keeps the low 32 bits of the strtol result, Java clamps to the int range. */
    @Test
    void aNumberOutsideTheIntRangeIsClampedWhereCWraps() {
        ObjectProperty prop = one(withNumbers("3000000000", "-3000000000"));

        assertEquals(Integer.MAX_VALUE, prop.getPower(), "C: -1294967296");
        assertEquals(Integer.MIN_VALUE, prop.getMultiplier(), "C: 1294967296");
    }

    @Test
    void anUnreadablePowerEndsTheLoad() {
        assertStopsTheLoad(withNumbers("abc", ""), "an invalid power value: abc");
    }

    @Test
    void anUnreadableMultEndsTheLoad() {
        assertStopsTheLoad(withNumbers("", "x1"), "an invalid mult value: x1");
    }

    // ---- type-mult --------------------------------------------------------------------------

    /** C sets every slot to 1 first, so an unnamed tval is 1 and an explicit 0 survives as 0. */
    @Test
    void typeMultDefaultsToOneAndAnExplicitZeroSurvives() {
        ObjectProperty prop = one(withTypeMult(typeMults("gloves", "0", "boots", "3")));

        assertEquals(0, prop.getTypeMult(TValue.TV_GLOVES));
        assertEquals(3, prop.getTypeMult(TValue.TV_BOOTS));
        assertEquals(1, prop.getTypeMult(TValue.TV_SWORD));
        assertEquals(1, prop.getTypeMult(TValue.TV_NONE));
    }

    /** tval_find_idx compares with my_stricmp after de_armour, and accepts a number below TV_MAX. */
    @Test
    void typeMultTvalsIgnoreCaseAcceptArmourAndNumbers() {
        ObjectProperty prop = one(withTypeMult(typeMults(
                "GLOVES", "2", "soft armour", "4", "dragon armor", "5",
                String.valueOf(TValue.TV_BOOTS.ordinal()), "6")));

        assertEquals(2, prop.getTypeMult(TValue.TV_GLOVES));
        assertEquals(4, prop.getTypeMult(TValue.TV_SOFT_ARMOR));
        assertEquals(5, prop.getTypeMult(TValue.TV_DRAG_ARMOR));
        assertEquals(6, prop.getTypeMult(TValue.TV_BOOTS));
    }

    /** C: PARSE_ERROR_UNRECOGNISED_TVAL. */
    @Test
    void aTypeMultNamingNoTvalEndsTheLoad() {
        assertStopsTheLoad(withTypeMult(typeMults("gloves", "3", "not a tval", "2")),
                "an illegal tValue value: not a tval");
    }

    @Test
    void anUnreadableTypeMultFigureEndsTheLoad() {
        assertStopsTheLoad(withTypeMult(typeMults("gloves", "x")), "an invalid tValue mult value: x");
    }

    /** Two spellings of one tval: the later line wins, as C's second assignment to the slot does. */
    @Test
    void twoSpellingsOfOneTvalTheLaterLineWins() {
        assertEquals(3, one(withTypeMult(typeMults("soft armor", "2", "soft armour", "3")))
                .getTypeMult(TValue.TV_SOFT_ARMOR));
        assertEquals(2, one(withTypeMult(typeMults("soft armour", "3", "soft armor", "2")))
                .getTypeMult(TValue.TV_SOFT_ARMOR));
    }

    /**
     * Accepted deviation ("A type-mult: tval written twice under two spellings can keep the earlier
     * line" in docs/Accepted_Deviations.md). The file has soft armor:2, soft armour:3, soft armor:4, so C
     * ends on 4. The grammar's map holds {soft armor=4, soft armour=3}, in that order, and the later
     * key wins.
     */
    @Test
    void aRepeatedSpellingAfterAnAliasKeepsTheAliasHereWhereCKeepsTheLastLine() {
        ObjectProperty prop = one(withTypeMult(typeMults("soft armor", "4", "soft armour", "3")));

        assertEquals(3, prop.getTypeMult(TValue.TV_SOFT_ARMOR), "C: 4");
    }

    // ---- bindui -----------------------------------------------------------------------------

    /** C only pushes a bound property when the directive is present. */
    @Test
    void aRecordWithoutBinduiHasNoBindings() {
        assertEquals(List.of(), one(ofType("stat", "STR")).getBoundEntries());
    }

    /** The &lt;TAG&gt; is part of the entry's name in ui_entry.txt, so it is folded into the name. */
    @Test
    void theTagIsFoldedIntoTheEntryName() {
        ObjectProperty.UIBinding tagged = one(withBindui("stat_mod_ui_compact_0", "STR", null, "0"))
                .getBoundEntries().getFirst();
        ObjectProperty.UIBinding plain = one(withBindui("some_entry", "", null, "0"))
                .getBoundEntries().getFirst();

        assertEquals("stat_mod_ui_compact_0<STR>", tagged.entry());
        assertEquals("some_entry", plain.entry());
    }

    /** C: isaux is parser_getint(p, "aux") != 0, and strtol reads 00 and -0 as zero. */
    @ParameterizedTest
    @CsvSource({"0, false", "00, false", "-0, false", "1, true", "2, true", "-1, true", "010, true"})
    void auxIsTrueWhenTheNumberIsNotZero(String aux, boolean expected) {
        assertEquals(expected, one(withBindui("entry", "", null, aux)).getBoundEntries().getFirst().aux());
    }

    /** C: have_val is parser_hasval, so an absent value is not the same as an explicit 0. */
    @Test
    void anAbsentBinduiValueIsNullAndAnExplicitZeroIsNot() {
        ObjectProperty.UIBinding absent = one(withBindui("entry", "", null, "0")).getBoundEntries().getFirst();
        ObjectProperty.UIBinding zero = one(withBindui("entry", "", "0", "0")).getBoundEntries().getFirst();
        ObjectProperty.UIBinding five = one(withBindui("entry", "", "5", "1")).getBoundEntries().getFirst();

        assertNull(absent.value());
        assertEquals(0, zero.value());
        assertEquals(5, five.value());
        assertTrue(five.aux());
    }

    /** The aux and value numbers are only read when the record has a bindui line. */
    @Test
    void aBinduiNumberIsOnlyReadWhenThereIsABinduiLine() {
        ObjectPropertyParseRecord noBindui = new ObjectPropertyParseRecord("test", "stat", "", "", "STR", "", "",
                new LinkedHashMap<>(), "", "", "", "", "", "junk", "junk", "", 7);

        assertEquals(List.of(), one(noBindui).getBoundEntries());
    }

    @Test
    void anUnreadableAuxEndsTheLoad() {
        assertStopsTheLoad(withBindui("entry", "", null, "x"), "an invalid aux value: x");
    }

    @Test
    void anUnreadableBinduiValueEndsTheLoad() {
        assertStopsTheLoad(withBindui("entry", "", "y", "0"), "an invalid optional property value: y");
    }

    // ---- the strings, the list, and the stop ------------------------------------------------

    /** The five text fields are copied across unchanged; an absent line is the empty string, where C has NULL. */
    @Test
    void theTextFieldsAreCopiedAcrossAndAbsentOnesAreEmpty() {
        ObjectProperty described = one(full("strength", "stat", "", "", "STR", "", "", new LinkedHashMap<>(),
                "strong", "weak", "You feel {name}.", "Does a thing.", 7));
        ObjectProperty bare = one(ofType("stat", "STR"));

        assertEquals("strength", described.getName());
        assertEquals("strong", field(described, "adjective"));
        assertEquals("weak", field(described, "negAdjective"));
        assertEquals("You feel {name}.", described.getNoticeMessage());
        assertEquals("Does a thing.", field(described, "description"));
        assertEquals("", field(bare, "adjective"));
        assertEquals("", field(bare, "negAdjective"));
        assertEquals("", bare.getNoticeMessage());
        assertEquals("", field(bare, "description"));
    }

    /** One property per record, in file order, with nothing appended; and nothing in, nothing out. */
    @Test
    void thePropertiesComeBackOnePerRecordInFileOrder() {
        List<ObjectProperty> result = assemble(
                full("b", "flag", "", "", "FEATHER", "", "", new LinkedHashMap<>(), "", "", "", "", 2),
                full("a", "stat", "", "", "STR", "", "", new LinkedHashMap<>(), "", "", "", "", 3));

        assertEquals(List.of("b", "a"), result.stream().map(ObjectProperty::getName).toList());
        assertEquals(List.of(), new ObjectPropertyAssembler().assemble(List.of(), new ArrayList<>(), FILE));
    }

    /** Every fault ends the load, so nothing is ever appended to the errors list. */
    @Test
    void theErrorsListIsNeverWritten() {
        List<String> errors = new ArrayList<>();

        new ObjectPropertyAssembler().assemble(List.of(ofType("stat", "STR")), errors, FILE);
        assertThrows(DataFileParseException.class,
                () -> new ObjectPropertyAssembler().assemble(List.of(ofType("bogus", "STR")), errors, FILE));

        assertEquals(List.of(), errors);
    }

    /** C's first parser error ends the whole file: no record after the bad one is read, and none comes back. */
    @Test
    void aBadRecordEndsTheLoadWhateverSurroundsIt() {
        ObjectPropertyParseRecord bad = full("bad", "flag", "", "", "NOT_A_FLAG", "", "", new LinkedHashMap<>(),
                "", "", "", "", 9);
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> assemble(ofType("stat", "STR"), bad, ofType("flag", "FEATHER")));

        assertTrue(thrown.getMessage().contains("line: 9"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("an illegal code value: NOT_A_FLAG"), thrown::getMessage);
    }

    /**
     * The checks run type, subtype, id-type, code, power and mult, type-mult, bindui, which is not
     * C's file order; a record with two faults reports the first of these, and the load ends either way.
     */
    @Test
    void aRecordWithTwoFaultsReportsTheEarlierCheck() {
        assertStopsTheLoad(full("test", "stat", "nonsense", "", "NOPE", "x", "", new LinkedHashMap<>(),
                "", "", "", "", 7), "an illegal subtype value: nonsense");
        assertStopsTheLoad(full("test", "stat", "", "", "NOPE", "x", "", new LinkedHashMap<>(),
                "", "", "", "", 7), "an illegal code value: NOPE");
        assertStopsTheLoad(full("test", "stat", "", "", "STR", "x", "", typeMults("nothing", "1"),
                "", "", "", "", 7), "an invalid power value: x");
    }
}
