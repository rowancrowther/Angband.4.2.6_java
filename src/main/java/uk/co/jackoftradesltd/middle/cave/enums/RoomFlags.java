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

package uk.co.jackoftradesltd.middle.cave.enums;

import java.util.Arrays;

/**
 * Flags a room type can carry — the port of C's {@code list-room-flags.h}, folded together with
 * the {@code room_flags[]} name table in {@code generate.c}.
 *
 * <p>These are set on rooms by {@code vault.txt} and {@code room_template.txt}. They are read
 * when the room is built ({@code gen-room.c}, functions {@code build_room_template()} and
 * {@code build_vault()}), which records the entrances the tunnelling code should aim for.
 * C keeps the flag names in a separate string table purely so those two parsers can look a flag
 * up by name; the enum constant's own name serves that purpose here, so the second table is
 * unnecessary.
 *
 * <p>{@link #NONE} comes from that string table rather than from {@code list-room-flags.h}, and
 * keeps the ordinals lined up with C: the value stored for {@link #FEW_ENTRANCES} is 1, as
 * its index in the table.
 *
 * <p>Name matching is case-exact, as C's {@code streq} is. C's {@code lookup_flag}
 * ({@code datafile.c}) starts its search at {@code FLAG_START}, which is 1, so it skips the
 * {@code "NONE"} entry and a {@code flags:} line naming {@code NONE} is refused there.
 * {@link #lookup(String)} deliberately does not skip it (accepted deviation, 2026-11-18): it
 * returns {@link #NONE} and leaves the caller to decide what that means. C does accept
 * {@code MAX}, whose name sits at index 2 of the table, and so does {@link #lookup(String)}.
 *
 * <p>Changing these would not break savefiles, but would change how the two data files parse.
 *
 * <p>coded on 2026-09-30 / commented in full on 2026-11-18
 *
 * @author Rowan Crowther
 */
public enum RoomFlags {
    /**
     * No flag — index 0 of C's name table, and what a room with no flags set carries.
     *
     * <p>coded on 2026-09-30 / commented in full on 2026-09-30
     */
    NONE(""),
    /**
     * The room can only be entered from a few directions, or its entrances need digging, so the
     * generator should tunnel to it differently.
     *
     * <p>coded on 2026-09-30 / commented in full on 2026-09-30
     */
    FEW_ENTRANCES("select alternate tunneling for a room since it can only be entered from a few directions or the entrances involve digging"),
    /**
     * Terminator, carried over from C, where it gives the flag set its size
     * ({@code ROOMF_SIZE}). Not a real room feature, but C's name table lists it as
     * {@code "MAX"}, so a {@code flags:} line naming it is accepted there and by
     * {@link #lookup(String)}.
     *
     * <p>coded on 2026-09-30 / commented in full on 2026-11-18
     */
    MAX("");

    /**
     * Description of the flag — the second field of each {@code ROOMF()} line in
     * {@code list-room-flags.h}. C's own expansions of that header discard it, so it is
     * documentation only and nothing displays it.
     *
     * <p>coded on 2026-09-30 / commented in full on 2026-09-30
     */
    private final String helpString;

    /**
     * Builds a constant with its description.
     *
     * <p>coded on 2026-09-30 / commented in full on 2026-09-30
     *
     * @param helpString description of the flag, copied from {@code list-room-flags.h}
     */
    RoomFlags(String helpString) {
        this.helpString = helpString;
    }

    /**
     * Finds a flag by the name used on a {@code flags:} line of {@code vault.txt} or
     * {@code room_template.txt} — the port of C's {@code lookup_flag()} ({@code datafile.c}) run
     * over {@code room_flags[]} ({@code generate.c}), as {@code grab_flag()} does for
     * {@code parse_vault_flags} and {@code parse_room_flags}.
     *
     * <p>The match is case-exact. {@code "FEW_ENTRANCES"} and {@code "MAX"} find their constants,
     * and {@code "few_entrances"}, {@code "FEW_ENTRANCE"}, {@code "ROOMF_FEW_ENTRANCES"} and
     * {@code ""} find nothing. {@code "NONE"} returns {@link #NONE}: C's search starts at index 1
     * and refuses it, so this is an accepted deviation (2026-11-18) and the caller has to treat
     * {@link #NONE} as it sees fit.
     *
     * <p>coded on 2026-11-18 / commented in full on 2026-11-18
     *
     * @param flag the name as written in the data file, or null
     * @return the matching constant, or null if there is none or {@code flag} is null
     */
    public static RoomFlags lookup(String flag) {
        if (flag == null) return null;
        
        return Arrays.stream(RoomFlags.values()).filter(f -> f.name().equals(flag))
                .findFirst().orElse(null);
    }

    /**
     * Returns the flag's description text, empty for {@link #NONE} and {@link #MAX}.
     *
     * <p>coded on 2026-09-30 / commented in full on 2026-09-30
     *
     * @return description of the flag
     */
    public String getHelpString() {
        return helpString;
    }
}
