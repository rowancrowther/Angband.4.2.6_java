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

package uk.co.jackoftradesltd.middle.monsters.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.enums.MessageType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link MonsterMessage} against the rows of {@code list-mon-message.h}, which C's
 * {@code enum mon_messages} and {@code msg_repository[]} in {@code mon-msg.c} are built from.
 *
 * <p>The expected values are written out from the C header, one {@link Row} per {@code MON_MSG(...)}
 * line, in the header's order. A row's position in {@link #C_ROWS} is C's message code, so the
 * ordinal checks are checks on the enum's numbering. The template checks use a local copy of the
 * bracket parser in C's {@code get_message_text()}, written from that function rather than from
 * the Java, to show the stored text yields the intended singular and plural sentences.
 *
 * <p>Class MonsterMessageTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class MonsterMessageTest {

    /**
     * One line of {@code list-mon-message.h}; {@code name} is the column without the
     * {@code MON_MSG_} prefix.
     */
    private record Row(String name, MessageType type, boolean omitSubject, String text) {
    }

    /**
     * All 63 rows of {@code list-mon-message.h}, in the header's order.
     */
    private static final List<Row> C_ROWS = List.of(
            new Row("NONE", MessageType.MSG_GENERIC, false, "[is|are] hurt."),
            new Row("DIE", MessageType.MSG_KILL, false, "die[s]."),
            new Row("DESTROYED", MessageType.MSG_KILL, false, "[is|are] destroyed."),
            new Row("RESIST_A_LOT", MessageType.MSG_GENERIC, false, "resist[s] a lot."),
            new Row("HIT_HARD", MessageType.MSG_GENERIC, false, "[is|are] hit hard."),
            new Row("RESIST", MessageType.MSG_GENERIC, false, "resist[s]."),
            new Row("IMMUNE", MessageType.MSG_GENERIC, false, "[is|are] immune."),
            new Row("RESIST_SOMEWHAT", MessageType.MSG_GENERIC, false, "resist[s] somewhat."),
            new Row("UNAFFECTED", MessageType.MSG_GENERIC, false, "[is|are] unaffected!"),
            new Row("SPAWN", MessageType.MSG_GENERIC, false, "spawn[s]!"),
            new Row("HEALTHIER", MessageType.MSG_GENERIC, false, "look[s] healthier."),
            new Row("FALL_ASLEEP", MessageType.MSG_GENERIC, false, "fall[s] asleep!"),
            new Row("WAKES_UP", MessageType.MSG_GENERIC, false, "wake[s] up."),
            new Row("CRINGE_LIGHT", MessageType.MSG_GENERIC, false, "cringe[s] from the light!"),
            new Row("SHRIVEL_LIGHT", MessageType.MSG_KILL, false, "shrivel[s] away in the light!"),
            new Row("LOSE_SKIN", MessageType.MSG_GENERIC, false, "lose[s] some skin!"),
            new Row("DISSOLVE", MessageType.MSG_KILL, false, "dissolve[s]!"),
            new Row("CATCH_FIRE", MessageType.MSG_GENERIC, false, "catch[es] fire!"),
            new Row("BADLY_FROZEN", MessageType.MSG_GENERIC, false, "[is|are] badly frozen."),
            new Row("SHUDDER", MessageType.MSG_GENERIC, false, "shudder[s]."),
            new Row("CHANGE", MessageType.MSG_GENERIC, false, "change[s]!"),
            new Row("DISAPPEAR", MessageType.MSG_GENERIC, false, "disappear[s]!"),
            new Row("MORE_DAZED", MessageType.MSG_GENERIC, false, "[is|are] even more stunned."),
            new Row("DAZED", MessageType.MSG_GENERIC, false, "[is|are] stunned."),
            new Row("NOT_DAZED", MessageType.MSG_GENERIC, false, "[is|are] no longer stunned."),
            new Row("MORE_CONFUSED", MessageType.MSG_GENERIC, false, "look[s] more confused."),
            new Row("CONFUSED", MessageType.MSG_GENERIC, false, "look[s] confused."),
            new Row("NOT_CONFUSED", MessageType.MSG_GENERIC, false, "[is|are] no longer confused."),
            new Row("MORE_SLOWED", MessageType.MSG_GENERIC, false, "look[s] more slowed."),
            new Row("SLOWED", MessageType.MSG_GENERIC, false, "look[s] slowed."),
            new Row("NOT_SLOWED", MessageType.MSG_GENERIC, false, "speed[s] up."),
            new Row("MORE_HASTED", MessageType.MSG_GENERIC, false, "look[s] even faster!"),
            new Row("HASTED", MessageType.MSG_GENERIC, false, "start[s] moving faster."),
            new Row("NOT_HASTED", MessageType.MSG_GENERIC, false, "slow[s] down."),
            new Row("MORE_AFRAID", MessageType.MSG_GENERIC, false, "look[s] more terrified!"),
            new Row("FLEE_IN_TERROR", MessageType.MSG_FLEE, false, "flee[s] in terror!"),
            new Row("NOT_AFRAID", MessageType.MSG_GENERIC, false, "[is|are] no longer afraid."),
            new Row("HELD", MessageType.MSG_GENERIC, false, "[is|are] frozen to the spot!"),
            new Row("NOT_HELD", MessageType.MSG_GENERIC, false, "can move again."),
            new Row("DISEN", MessageType.MSG_GENERIC, false, "seem[s] less magical!"),
            new Row("NOT_DISEN", MessageType.MSG_GENERIC, false, "seem[s] magical again."),
            new Row("COMMAND", MessageType.MSG_GENERIC, false, "falls under your spell!"),
            new Row("NOT_COMMAND", MessageType.MSG_GENERIC, false, "is no longer under your control."),
            new Row("SHAPE_FAIL", MessageType.MSG_GENERIC, false, "shimmers for a moment."),
            new Row("MORIA_DEATH", MessageType.MSG_KILL, true, "You hear [a|several] scream[|s] of agony!"),
            new Row("DISINTEGRATES", MessageType.MSG_KILL, false, "disintegrate[s]!"),
            new Row("FREEZE_SHATTER", MessageType.MSG_KILL, false, "freeze[s] and shatter[s]!"),
            new Row("MANA_DRAIN", MessageType.MSG_GENERIC, false, "lose[s] some mana!"),
            new Row("BRIEF_PUZZLE", MessageType.MSG_GENERIC, false, "look[s] briefly puzzled."),
            new Row("MAINTAIN_SHAPE", MessageType.MSG_GENERIC, false, "maintain[s] the same shape."),
            new Row("UNHARMED", MessageType.MSG_GENERIC, false, "[is|are] unharmed."),
            new Row("APPEAR", MessageType.MSG_GENERIC, false, "appear[s]!"),
            new Row("HIT_AND_RUN", MessageType.MSG_GENERIC, true, "There is a puff of smoke!"),
            new Row("QUAKE_DEATH", MessageType.MSG_KILL, false, "[is|are] embedded in rock!"),
            new Row("QUAKE_HURT", MessageType.MSG_GENERIC, false, "wail[s] out in pain!"),
            new Row("95", MessageType.MSG_GENERIC, false, ""),
            new Row("75", MessageType.MSG_GENERIC, false, ""),
            new Row("50", MessageType.MSG_GENERIC, false, ""),
            new Row("35", MessageType.MSG_GENERIC, false, ""),
            new Row("20", MessageType.MSG_GENERIC, false, ""),
            new Row("10", MessageType.MSG_GENERIC, false, ""),
            new Row("0", MessageType.MSG_GENERIC, false, ""),
            new Row("MAX", MessageType.MSG_GENERIC, false, ""));

    /**
     * The seven pain rows, in order, which C swaps for the monster base's pain messages.
     */
    private static final List<String> PAIN_NAMES = List.of("95", "75", "50", "35", "20", "10", "0");

    /**
     * Local copy of the bracket parser in {@code get_message_text()}, {@code mon-msg.c}:
     * {@code [} opens the singular side, {@code |} switches to the plural side, {@code ]} returns to
     * plain text, and the markers are never copied.
     */
    private static String expand(String source, boolean plural) {
        final int normal = 0, single = 1, plur = 2;
        int state = normal;
        StringBuilder buf = new StringBuilder();
        for (char cur : source.toCharArray()) {
            if (state == normal && cur == '[') {
                state = single;
            } else if (state == single && cur == '|') {
                state = plur;
            } else if (state != normal && cur == ']') {
                state = normal;
            } else if (state == normal || (state == single && !plural) || (state == plur && plural)) {
                buf.append(cur);
            }
        }
        assertEquals(normal, state, "template must end in the plain state: " + source);
        return buf.toString();
    }

    @Test
    @DisplayName("Sixty-three constants, one per header row, with MAX last")
    void count_and_order() {
        MonsterMessage[] all = MonsterMessage.values();
        assertEquals(C_ROWS.size(), all.length);
        assertEquals(63, all.length);
        assertSame(MonsterMessage.MON_MSG_NONE, all[0]);
        assertSame(MonsterMessage.MON_MSG_MAX, all[all.length - 1]);
    }

    @Test
    @DisplayName("Every constant matches its header row in name, category, flag and text")
    void every_row_matches_c() {
        MonsterMessage[] all = MonsterMessage.values();
        for (int i = 0; i < C_ROWS.size(); i++) {
            Row row = C_ROWS.get(i);
            MonsterMessage m = all[i];
            String where = "code " + i + " (" + row.name() + ")";
            assertEquals("MON_MSG_" + row.name(), m.name(), where);
            assertEquals(row.type(), m.getType(), where);
            assertEquals(row.omitSubject(), m.isOmitMonsterName(), where);
            assertEquals(row.text(), m.getMessage(), where);
        }
    }

    @Test
    @DisplayName("The second pain constant is MON_MSG_75, not ON_MSG_75")
    void mon_msg_75_resolves_by_name() {
        assertSame(MonsterMessage.MON_MSG_75, MonsterMessage.valueOf("MON_MSG_75"));
        assertThrows(IllegalArgumentException.class, () -> MonsterMessage.valueOf("ON_MSG_75"));
    }

    @Test
    @DisplayName("The seven pain rows are consecutive, empty, generic and sit just below MAX")
    void pain_rows() {
        MonsterMessage first = MonsterMessage.valueOf("MON_MSG_95");
        for (int i = 0; i < PAIN_NAMES.size(); i++) {
            MonsterMessage m = MonsterMessage.valueOf("MON_MSG_" + PAIN_NAMES.get(i));
            assertEquals(first.ordinal() + i, m.ordinal(), PAIN_NAMES.get(i));
            assertEquals("", m.getMessage(), PAIN_NAMES.get(i));
            assertEquals(MessageType.MSG_GENERIC, m.getType(), PAIN_NAMES.get(i));
            assertFalse(m.isOmitMonsterName(), PAIN_NAMES.get(i));
        }
        assertEquals(MonsterMessage.MON_MSG_MAX.ordinal() - 1, MonsterMessage.MON_MSG_0.ordinal());
        assertEquals(MonsterMessage.MON_MSG_QUAKE_HURT.ordinal() + 1, first.ordinal());
    }

    @Test
    @DisplayName("Only MORIA_DEATH and HIT_AND_RUN omit the monster's name")
    void omit_subject_rows() {
        for (MonsterMessage m : MonsterMessage.values()) {
            boolean expected = m == MonsterMessage.MON_MSG_MORIA_DEATH
                    || m == MonsterMessage.MON_MSG_HIT_AND_RUN;
            assertEquals(expected, m.isOmitMonsterName(), m.name());
        }
    }

    @Test
    @DisplayName("Kill-category rows are exactly the eight the header marks MSG_KILL; FLEE_IN_TERROR is MSG_FLEE")
    void category_rows() {
        List<MonsterMessage> kills = List.of(
                MonsterMessage.MON_MSG_DIE, MonsterMessage.MON_MSG_DESTROYED,
                MonsterMessage.MON_MSG_SHRIVEL_LIGHT, MonsterMessage.MON_MSG_DISSOLVE,
                MonsterMessage.MON_MSG_MORIA_DEATH, MonsterMessage.MON_MSG_DISINTEGRATES,
                MonsterMessage.MON_MSG_FREEZE_SHATTER, MonsterMessage.MON_MSG_QUAKE_DEATH);
        for (MonsterMessage m : MonsterMessage.values()) {
            if (kills.contains(m)) {
                assertEquals(MessageType.MSG_KILL, m.getType(), m.name());
            } else if (m == MonsterMessage.MON_MSG_FLEE_IN_TERROR) {
                assertEquals(MessageType.MSG_FLEE, m.getType(), m.name());
            } else {
                assertEquals(MessageType.MSG_GENERIC, m.getType(), m.name());
            }
        }
        assertEquals(8, kills.size());
    }

    @Test
    @DisplayName("Singular and plural expansion of the ordinary bracket forms")
    void ordinary_expansion() {
        assertEquals("dies.", expand(MonsterMessage.MON_MSG_DIE.getMessage(), false));
        assertEquals("die.", expand(MonsterMessage.MON_MSG_DIE.getMessage(), true));
        assertEquals("is hurt.", expand(MonsterMessage.MON_MSG_NONE.getMessage(), false));
        assertEquals("are hurt.", expand(MonsterMessage.MON_MSG_NONE.getMessage(), true));
        assertEquals("catches fire!", expand(MonsterMessage.MON_MSG_CATCH_FIRE.getMessage(), false));
        assertEquals("catch fire!", expand(MonsterMessage.MON_MSG_CATCH_FIRE.getMessage(), true));
        assertEquals("freezes and shatters!",
                expand(MonsterMessage.MON_MSG_FREEZE_SHATTER.getMessage(), false));
        assertEquals("freeze and shatter!",
                expand(MonsterMessage.MON_MSG_FREEZE_SHATTER.getMessage(), true));
    }

    @Test
    @DisplayName("MORIA_DEATH uses an empty alternative: a scream / several screams")
    void moria_death_expansion() {
        String t = MonsterMessage.MON_MSG_MORIA_DEATH.getMessage();
        assertEquals("You hear a scream of agony!", expand(t, false));
        assertEquals("You hear several screams of agony!", expand(t, true));
    }

    @Test
    @DisplayName("Rows without brackets read the same for one monster or several")
    void unbracketed_rows() {
        for (MonsterMessage m : List.of(MonsterMessage.MON_MSG_COMMAND, MonsterMessage.MON_MSG_NOT_COMMAND,
                MonsterMessage.MON_MSG_SHAPE_FAIL, MonsterMessage.MON_MSG_NOT_HELD,
                MonsterMessage.MON_MSG_HIT_AND_RUN)) {
            assertFalse(m.getMessage().contains("["), m.name());
            assertEquals(m.getMessage(), expand(m.getMessage(), false), m.name());
            assertEquals(m.getMessage(), expand(m.getMessage(), true), m.name());
        }
    }

    @Test
    @DisplayName("Every template is balanced: it returns to the plain state at the end")
    void templates_are_balanced() {
        for (MonsterMessage m : MonsterMessage.values()) {
            // expand() asserts the final state is the plain one, as C does after its loop
            assertDoesNotThrow(() -> expand(m.getMessage(), false), m.name());
            assertDoesNotThrow(() -> expand(m.getMessage(), true), m.name());
        }
    }

    @Test
    @DisplayName("NONE keeps real text although MonTimed uses it as the no-message value")
    void none_has_real_text() {
        assertEquals("[is|are] hurt.", MonsterMessage.MON_MSG_NONE.getMessage());
        assertEquals(0, MonsterMessage.MON_MSG_NONE.ordinal());
    }
}
