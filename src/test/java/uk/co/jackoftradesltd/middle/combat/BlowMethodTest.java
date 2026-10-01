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

package uk.co.jackoftradesltd.middle.combat;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.BlowMethodReader;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.enums.MessageType;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link BlowMethod}, the port of C's {@code struct blow_method}. Expected values
 * are read off the shipped {@code lib/gamedata/blow_methods.txt}, not off the Java loader.
 *
 * @author Rowan Crowther
 */
class BlowMethodTest {

    private static final String REAL_FILE = "lib/gamedata/blow_methods.txt";

    private static BlowMethod find(List<BlowMethod> all, String name) {
        return all.stream().filter(m -> m.getName().equals(name)).findFirst()
                .orElseThrow(() -> new AssertionError("no method " + name));
    }

    @Test
    void constructorStoresEveryFieldAsGiven() {
        List<String> acts = List.of("a", "b");
        BlowMethod m = new BlowMethod("X", true, false, true, false, MessageType.MSG_MON_HIT, acts, "x");

        assertEquals("X", m.getName());
        assertTrue(m.isCut());
        assertFalse(m.isStun());
        assertTrue(m.isMiss());
        assertFalse(m.isPhys());
        assertEquals(MessageType.MSG_MON_HIT, m.getMesgT());
        assertEquals(acts, m.getBlowMessage());
        assertEquals("x", m.getDesc());
    }

    @Test
    void hitFlagsMatchDataFile() throws IOException {
        List<BlowMethod> all = new BlowMethodReader().parseWithResults(REAL_FILE).items();
        BlowMethod hit = find(all, "HIT");

        // cut:1 stun:1 miss:1 phys:1 msg:MON_HIT act:hits {target} desc:hit
        assertTrue(hit.isCut());
        assertTrue(hit.isStun());
        assertTrue(hit.isMiss());
        assertTrue(hit.isPhys());
        assertEquals(MessageType.MSG_MON_HIT, hit.getMesgT());
        assertEquals(List.of("hits {target}"), hit.getBlowMessage());
        assertEquals("hit", hit.getDesc());
    }

    @Test
    void touchIsNonPhysicalButAnnouncesMisses() throws IOException {
        BlowMethod touch = find(new BlowMethodReader().parseWithResults(REAL_FILE).items(), "TOUCH");

        // cut:0 stun:0 miss:1 phys:0
        assertFalse(touch.isCut());
        assertFalse(touch.isStun());
        assertTrue(touch.isMiss());
        assertFalse(touch.isPhys());
    }

    @Test
    void punchStunsButDoesNotCut() throws IOException {
        BlowMethod punch = find(new BlowMethodReader().parseWithResults(REAL_FILE).items(), "PUNCH");

        // cut:0 stun:1 miss:1 phys:1
        assertFalse(punch.isCut());
        assertTrue(punch.isStun());
        assertTrue(punch.isPhys());
    }

    @Test
    void clawCutsButDoesNotStun() throws IOException {
        BlowMethod claw = find(new BlowMethodReader().parseWithResults(REAL_FILE).items(), "CLAW");

        // cut:1 stun:0 miss:1 phys:1
        assertTrue(claw.isCut());
        assertFalse(claw.isStun());
    }

    @Test
    void silentMethodsDoNotAnnounceMisses() throws IOException {
        List<BlowMethod> all = new BlowMethodReader().parseWithResults(REAL_FILE).items();

        // CRAWL and DROOL: miss:0, and every other flag 0 as well
        for (String name : List.of("CRAWL", "DROOL")) {
            BlowMethod m = find(all, name);
            assertFalse(m.isMiss(), name);
            assertFalse(m.isCut(), name);
            assertFalse(m.isStun(), name);
            assertFalse(m.isPhys(), name);
        }
        assertEquals("crawl on you", find(all, "CRAWL").getDesc());
        assertEquals(List.of("drools on {target}"), find(all, "DROOL").getBlowMessage());
    }

    @Test
    void actionCountMatchesC() throws IOException {
        List<BlowMethod> all = new BlowMethodReader().parseWithResults(REAL_FILE).items();

        // num_messages in C: INSULT and MOAN have eight act: lines, the rest one.
        assertEquals(8, find(all, "INSULT").getBlowMessage().size());
        assertEquals(8, find(all, "MOAN").getBlowMessage().size());
        assertEquals(1, find(all, "BITE").getBlowMessage().size());
        // 33 act: lines across 19 methods
        assertEquals(33, all.stream().mapToInt(m -> m.getBlowMessage().size()).sum());
        assertEquals(19, all.size());
    }

    @Test
    void everyMethodHasDescAndAction() throws IOException {
        ParseResult<BlowMethod> result = new BlowMethodReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors());
        for (BlowMethod m : result.items()) {
            assertFalse(m.getDesc().isEmpty(), m.getName());
            assertFalse(m.getBlowMessage().isEmpty(), m.getName());
        }
    }
}
