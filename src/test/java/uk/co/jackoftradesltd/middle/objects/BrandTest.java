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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.BrandReader;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Brand}, the port of C's {@code struct brand} ({@code object.h}). The class has no C
 * function to cross-reference, so the expected values are the records of the C original's
 * {@code lib/gamedata/brand.txt}, transcribed by hand into {@link #REAL_BRANDS} rather than read
 * back from the Java: each row is code, name, verb, multiplier, o-multiplier, power, resist flag,
 * vuln flag. The equality and hash contracts, which C lacks, are tested against the rule recorded
 * in the Javadoc: all eight fields count.
 *
 * <p>Class BrandTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class BrandTest {

    private static final String REAL_FILE = "lib/gamedata/brand.txt";

    private record Row(String code, String name, String verb, int mult, int oMult, int power,
                       MonsterRaceFlag resist, MonsterRaceFlag vuln) {
    }

    /**
     * Every record of the C original's brand.txt, in file order.
     */
    private static final List<Row> REAL_BRANDS = List.of(
            new Row("ACID_3", "acid", "dissolve", 3, 25, 161, MonsterRaceFlag.RF_IM_ACID, MonsterRaceFlag.RF_NONE),
            new Row("ELEC_3", "lightning", "shock", 3, 25, 116, MonsterRaceFlag.RF_IM_ELEC, MonsterRaceFlag.RF_NONE),
            new Row("FIRE_3", "fire", "burn", 3, 25, 113, MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE),
            new Row("COLD_3", "cold", "freeze", 3, 25, 119, MonsterRaceFlag.RF_IM_COLD, MonsterRaceFlag.RF_HURT_COLD),
            new Row("POIS_3", "poison", "poison", 3, 25, 122, MonsterRaceFlag.RF_IM_POIS, MonsterRaceFlag.RF_NONE),
            new Row("ACID_2", "acid", "corrode", 2, 15, 130, MonsterRaceFlag.RF_IM_ACID, MonsterRaceFlag.RF_NONE),
            new Row("ELEC_2", "lightning", "zap", 2, 15, 108, MonsterRaceFlag.RF_IM_ELEC, MonsterRaceFlag.RF_NONE),
            new Row("FIRE_2", "fire", "singe", 2, 15, 107, MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE),
            new Row("COLD_2", "cold", "chill", 2, 15, 109, MonsterRaceFlag.RF_IM_COLD, MonsterRaceFlag.RF_HURT_COLD),
            new Row("POIS_2", "poison", "sicken", 2, 15, 111, MonsterRaceFlag.RF_IM_POIS, MonsterRaceFlag.RF_NONE));

    private static Brand fire() {
        return new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 3, 25, 113);
    }

    // ---- Fidelity to brand.txt ------------------------------------------

    @Test
    void realFileRecordsCarryEveryFieldAsCDoes() throws IOException {
        ParseResult<Brand> result = new BrandReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(REAL_BRANDS.size(), result.items().size());
        for (int i = 0; i < REAL_BRANDS.size(); i++) {
            Row row = REAL_BRANDS.get(i);
            Brand expected = new Brand(row.code(), row.name(), row.verb(), row.resist(), row.vuln(),
                    row.mult(), row.oMult(), row.power());
            assertEquals(expected, result.items().get(i), "record " + row.code());
        }
    }

    @Test
    void gettersReturnTheConstructorValues() {
        Brand b = fire();

        assertEquals("FIRE_3", b.getCode());
        assertEquals("fire", b.getName());
        assertEquals(3, b.getMultiplier());
        assertEquals(113, b.getPower());
    }

    @Test
    void multiplierAndPowerAreIndependentFields() {
        // ACID_2 is the weaker multiplier (2) yet the higher power (130) than FIRE_3 (3, 113): the two
        // getters must not be crossed, as copyBrands compares the first and slayPower the second.
        Brand acid2 = new Brand("ACID_2", "acid", "corrode", MonsterRaceFlag.RF_IM_ACID,
                MonsterRaceFlag.RF_NONE, 2, 15, 130);

        assertTrue(acid2.getMultiplier() < fire().getMultiplier());
        assertTrue(acid2.getPower() > fire().getPower());
    }

    // ---- equals / hashCode ----------------------------------------------

    @Test
    void equalWhenEveryFieldMatches() {
        assertEquals(fire(), fire());
        assertEquals(fire().hashCode(), fire().hashCode());
    }

    @Test
    void notEqualWhenAnySingleFieldDiffers() {
        Brand base = fire();
        Brand[] variants = {
                new Brand("FIRE_2", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 3, 25, 113),
                new Brand("FIRE_3", "flame", "burn", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 3, 25, 113),
                new Brand("FIRE_3", "fire", "singe", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 3, 25, 113),
                new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_COLD, MonsterRaceFlag.RF_HURT_FIRE, 3, 25, 113),
                new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_NONE, 3, 25, 113),
                new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 2, 25, 113),
                new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 3, 15, 113),
                new Brand("FIRE_3", "fire", "burn", MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 3, 25, 107),
        };

        for (int i = 0; i < variants.length; i++) {
            assertNotEquals(base, variants[i], "variant " + i);
        }
    }

    @Test
    void sameElementAtTwoStrengthsSharesAHashSetOnlyAsTwoMembers() {
        // FIRE_3 and FIRE_2 share the name "fire" (so one rune) but are unequal values.
        Brand fire3 = fire();
        Brand fire2 = new Brand("FIRE_2", "fire", "singe", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 2, 15, 107);
        Set<Brand> set = new HashSet<>(List.of(fire3, fire2));

        assertEquals(2, set.size());
        assertEquals(fire3.getName(), fire2.getName());
        assertTrue(set.contains(fire().copy()));
    }

    @Test
    void notEqualToNullOrAnotherType() {
        assertNotEquals(null, fire());
        assertNotEquals("FIRE_3", fire());
    }

    @Test
    void nullStringFieldsAreEqualAndHashSafely() {
        Brand a = new Brand(null, null, null, null, null, 0, 0, 0);
        Brand b = new Brand(null, null, null, null, null, 0, 0, 0);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    // ---- toString --------------------------------------------------------

    @Test
    void toStringNamesEveryField() {
        assertEquals("Brand{code='FIRE_3', name='fire', verb='burn', resistFlag=RF_IM_FIRE, "
                + "vulnerableFlag=RF_HURT_FIRE, multiplier=3, oMultiplier=25, power=113}", fire().toString());
    }
}
