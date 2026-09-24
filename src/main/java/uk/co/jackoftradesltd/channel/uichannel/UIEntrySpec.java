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

package uk.co.jackoftradesltd.channel.uichannel;

import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;

/**
 * The slice of a {@link uk.co.jackoftradesltd.frontend.entries.UIEntry} the core needs once loading
 * finishes, carried across the channel inside {@code UIMessage.UIEntriesLoaded}. No C counterpart:
 * C's {@code struct ui_entry} lives in one process and is read directly by whichever code needs it,
 * where this port's core and UI halves run on separate threads with no shared entry list, so the UI
 * side - the one that loads {@code ui_entry.txt} - sends the core just the fields it binds properties
 * against ({@link #combinerName}, {@link #entryFlags}) and the name it binds them by, rather than the
 * whole entry.
 *
 * <p>Record UIEntrySpec coded before 260924, commented in full on 260924.
 *
 * @param entryName    the entry's internal name, matching {@link uk.co.jackoftradesltd.frontend.entries.UIEntry#getName()}
 * @param combinerName the entry's value-combining strategy, matching {@link uk.co.jackoftradesltd.frontend.entries.UIEntry#getCombineType()}
 * @param entryFlags   the entry's behavioural flags, matching {@link uk.co.jackoftradesltd.frontend.entries.UIEntry#getEntryFlag()}
 * @author Rowan Crowther
 */
public record UIEntrySpec(String entryName,
                          CombinerName combinerName,
                          FlagView<ChannelEntryFlag> entryFlags) {
}
