package uk.co.jackoftrades.backend.objects.enums;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TValueTest {
    private final TValue tnull = TValue.TV_NULL;
    private final TValue tchest = TValue.TV_CHEST;
    private final TValue tshot = TValue.TV_SHOT;
    private final TValue tarrow = TValue.TV_ARROW;
    private final TValue tbolt = TValue.TV_BOLT;
    private final TValue tbow = TValue.TV_BOW;
    private final TValue tdigging = TValue.TV_DIGGING;
    private final TValue thafted = TValue.TV_HAFTED;
    private final TValue tpolearm = TValue.TV_POLEARM;
    private final TValue tsword = TValue.TV_SWORD;
    private final TValue tboots = TValue.TV_BOOTS;
    private final TValue tgloves = TValue.TV_GLOVES;
    private final TValue thelm = TValue.TV_HELM;
    private final TValue tcrown = TValue.TV_CROWN;
    private final TValue tshield = TValue.TV_SHIELD;
    private final TValue tcloak = TValue.TV_CLOAK;
    private final TValue tsoft = TValue.TV_SOFT_ARMOUR;
    private final TValue thard = TValue.TV_HARD_ARMOUR;
    private final TValue tdragon = TValue.TV_DRAG_ARMOUR;
    private final TValue tlight = TValue.TV_LIGHT;
    private final TValue tamulet = TValue.TV_AMULET;
    private final TValue tring = TValue.TV_RING;
    private final TValue tstaff = TValue.TV_STAFF;
    private final TValue twand = TValue.TV_WAND;
    private final TValue trod = TValue.TV_ROD;
    private final TValue tscroll = TValue.TV_SCROLL;
    private final TValue tpotion = TValue.TV_POTION;
    private final TValue tflask = TValue.TV_FLASK;
    private final TValue tfood = TValue.TV_FOOD;
    private final TValue tmushroom = TValue.TV_MUSHROOM;
    private final TValue tmagicb = TValue.TV_MAGIC_BOOK;
    private final TValue tprayerb = TValue.TV_PRAYER_BOOK;
    private final TValue tnatureb = TValue.TV_NATURE_BOOK;
    private final TValue tshadowb = TValue.TV_SHADOW_BOOK;
    private final TValue totherb = TValue.TV_OTHER_BOOK;
    private final TValue tgold = TValue.TV_GOLD;
    private ArrayList<TValue> allValues;
    private final Logger logger = LogManager.getLogger();

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(tnull);
        allValues.add(tchest);
        allValues.add(tshot);
        allValues.add(tarrow);
        allValues.add(tbolt);
        allValues.add(tbow);
        allValues.add(tdigging);
        allValues.add(thafted);
        allValues.add(tpolearm);
        allValues.add(tsword);
        allValues.add(tboots);
        allValues.add(tgloves);
        allValues.add(thelm);
        allValues.add(tcrown);
        allValues.add(tshield);
        allValues.add(tcloak);
        allValues.add(tsoft);
        allValues.add(thard);
        allValues.add(tdragon);
        allValues.add(tlight);
        allValues.add(tamulet);
        allValues.add(tring);
        allValues.add(tstaff);
        allValues.add(twand);
        allValues.add(trod);
        allValues.add(tscroll);
        allValues.add(tpotion);
        allValues.add(tflask);
        allValues.add(tfood);
        allValues.add(tmushroom);
        allValues.add(tmagicb);
        allValues.add(tprayerb);
        allValues.add(tnatureb);
        allValues.add(tshadowb);
        allValues.add(totherb);
        allValues.add(tgold);
    }

    @Test
    void getName() {
        assertAll(
                () -> assertTrue(tnull.getName().equals("none")),
                () -> assertTrue(tchest.getName().equals("chest")),
                () -> assertTrue(tshot.getName().equals("shot")),
                () -> assertTrue(tarrow.getName().equals("arrow")),
                () -> assertTrue(tbolt.getName().equals("bolt")),
                () -> assertTrue(tbow.getName().equals("bow")),
                () -> assertTrue(tdigging.getName().equals("digger")),
                () -> assertTrue(thafted.getName().equals("hafted")),
                () -> assertTrue(tpolearm.getName().equals("polearm")),
                () -> assertTrue(tsword.getName().equals("sword")),
                () -> assertTrue(tboots.getName().equals("boots")),
                () -> assertTrue(tgloves.getName().equals("gloves")),
                () -> assertTrue(thelm.getName().equals("helm")),
                () -> assertTrue(tcrown.getName().equals("crown")),
                () -> assertTrue(tshield.getName().equals("shield")),
                () -> assertTrue(tcloak.getName().equals("cloak")),
                () -> assertTrue(tsoft.getName().equals("soft armour")),
                () -> assertTrue(thard.getName().equals("hard armour")),
                () -> assertTrue(tdragon.getName().equals("dragon armour")),
                () -> assertTrue(tlight.getName().equals("light")),
                () -> assertTrue(tamulet.getName().equals("amulet")),
                () -> assertTrue(tring.getName().equals("ring")),
                () -> assertTrue(tstaff.getName().equals("staff")),
                () -> assertTrue(twand.getName().equals("wand")),
                () -> assertTrue(trod.getName().equals("rod")),
                () -> assertTrue(tscroll.getName().equals("scroll")),
                () -> assertTrue(tpotion.getName().equals("potion")),
                () -> assertTrue(tflask.getName().equals("flask")),
                () -> assertTrue(tfood.getName().equals("food")),
                () -> assertTrue(tmushroom.getName().equals("mushroom")),
                () -> assertTrue(tmagicb.getName().equals("magic book")),
                () -> assertTrue(tprayerb.getName().equals("prayer book")),
                () -> assertTrue(tnatureb.getName().equals("nature book")),
                () -> assertTrue(tshadowb.getName().equals("shadow book")),
                () -> assertTrue(totherb.getName().equals("other book")),
                () -> assertTrue(tgold.getName().equals("gold"))
        );
    }

    @Test
    void values() {
        for (TValue tValue : TValue.values()) {
            if (!allValues.contains(tValue)) {
                logger.error("Unable to find " + tValue.getName() + " in manually constructed values ArrayList.");
                fail("Unable to find " + tValue.getName() + " in manually constructed values ArrayList.");
            }
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(tnull, TValue.valueOf("TV_NULL")),
                () -> assertEquals(tchest, TValue.valueOf("TV_CHEST")),
                () -> assertEquals(tshot, TValue.valueOf("TV_SHOT")),
                () -> assertEquals(tarrow, TValue.valueOf("TV_ARROW")),
                () -> assertEquals(tbolt, TValue.valueOf("TV_BOLT")),
                () -> assertEquals(tbow, TValue.valueOf("TV_BOW")),
                () -> assertEquals(tdigging, TValue.valueOf("TV_DIGGING")),
                () -> assertEquals(thafted, TValue.valueOf("TV_HAFTED")),
                () -> assertEquals(tpolearm, TValue.valueOf("TV_POLEARM")),
                () -> assertEquals(tsword, TValue.valueOf("TV_SWORD")),
                () -> assertEquals(tboots, TValue.valueOf("TV_BOOTS")),
                () -> assertEquals(tgloves, TValue.valueOf("TV_GLOVES")),
                () -> assertEquals(thelm, TValue.valueOf("TV_HELM")),
                () -> assertEquals(tcrown, TValue.valueOf("TV_CROWN")),
                () -> assertEquals(tshield, TValue.valueOf("TV_SHIELD")),
                () -> assertEquals(tcloak, TValue.valueOf("TV_CLOAK")),
                () -> assertEquals(tsoft, TValue.valueOf("TV_SOFT_ARMOUR")),
                () -> assertEquals(thard, TValue.valueOf("TV_HARD_ARMOUR")),
                () -> assertEquals(tdragon, TValue.valueOf("TV_DRAG_ARMOUR")),
                () -> assertEquals(tlight, TValue.valueOf("TV_LIGHT")),
                () -> assertEquals(tamulet, TValue.valueOf("TV_AMULET")),
                () -> assertEquals(tring, TValue.valueOf("TV_RING")),
                () -> assertEquals(tstaff, TValue.valueOf("TV_STAFF")),
                () -> assertEquals(twand, TValue.valueOf("TV_WAND")),
                () -> assertEquals(trod, TValue.valueOf("TV_ROD")),
                () -> assertEquals(tscroll, TValue.valueOf("TV_SCROLL")),
                () -> assertEquals(tpotion, TValue.valueOf("TV_POTION")),
                () -> assertEquals(tflask, TValue.valueOf("TV_FLASK")),
                () -> assertEquals(tfood, TValue.valueOf("TV_FOOD")),
                () -> assertEquals(tmushroom, TValue.valueOf("TV_MUSHROOM")),
                () -> assertEquals(tmagicb, TValue.valueOf("TV_MAGIC_BOOK")),
                () -> assertEquals(tprayerb, TValue.valueOf("TV_PRAYER_BOOK")),
                () -> assertEquals(tnatureb, TValue.valueOf("TV_NATURE_BOOK")),
                () -> assertEquals(tshadowb, TValue.valueOf("TV_SHADOW_BOOK")),
                () -> assertEquals(totherb, TValue.valueOf("TV_OTHER_BOOK")),
                () -> assertEquals(tgold, TValue.valueOf("TV_GOLD"))
        );
    }
}