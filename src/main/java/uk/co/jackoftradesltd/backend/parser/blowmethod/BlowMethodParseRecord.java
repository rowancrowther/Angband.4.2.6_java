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

package uk.co.jackoftradesltd.backend.parser.blowmethod;

import java.util.List;

/**
 * One raw {@code blow_methods.txt} record, exactly as the grammar found it - the
 * inert hand-off between {@code BlowMethodGrammar} and {@link BlowMethodAssembler}.
 * <p>
 * Every field is the verbatim token text, deliberately unconverted: {@code cut} and
 * friends are still {@code "0"}/{@code "1"} rather than booleans, and {@code msg} is
 * still a bare name rather than a {@code MessageType}. Keeping the parse phase free of
 * interpretation means a value that fails to resolve is reported by the assembler
 * against a real line number, instead of throwing somewhere inside a grammar action
 * where there is no error channel to report it on.
 * <p>
 * <strong>{@code msg} has three distinct states</strong>, and they are not
 * interchangeable: a name (the {@code msg:} line carried a value), {@code ""} (the line
 * was absent entirely - the grammar's seeded default), or {@code null} (a bare
 * {@code msg:} with no text). The last two are both "no message type given" and both
 * mean {@code MSG_GENERIC}, which is why the assembler's guard has to tolerate a null.
 * <p>
 * <strong>{@code cut}, {@code stun}, {@code miss} and {@code phys} are {@code null} when the
 * record never named them</strong>, as C's directives may come in any order and any of them
 * may be missing. The assembler reads a {@code null} as false, the {@code mem_zalloc}'d zero.
 * The grammar can no longer produce {@code ""} for them, but a record built by hand may
 * carry it, and the assembler reads that as absent too.
 *
 * <p>Record BlowMethodParseRecord coded before 260915, commented in full on 261107 (absent
 * flags, repeated {@code desc:} lines and the free directive order noted 261107).
 *
 * @param name the method's code, e.g. {@code "HIT"}, {@code "BITE"}
 * @param cut  whether the blow can cause cuts, as raw text; any non-zero is true, {@code null}
 *             if no {@code cut:} line was read
 * @param stun whether the blow can stun, as raw text, {@code null} if no {@code stun:} line was
 *             read
 * @param miss whether a miss is announced to the player, as raw text, {@code null} if no
 *             {@code miss:} line was read
 * @param phys whether the blow does physical damage, as raw text, {@code null} if no
 *             {@code phys:} line was read
 * @param msg  the message-channel name, or {@code ""}/{@code null} if none was given
 * @param act  the flavour action strings narrating the blow, in file order (C holds them
 *             reversed, and the assembler reverses them); may be empty
 * @param desc the short description used in monster lore, with repeated {@code desc:} lines
 *             joined end to end as C's {@code string_append} does; {@code ""} if there was none
 * @param line the line {@code name:} appeared on, for error reporting
 * @author Rowan Crowther
 */
public record BlowMethodParseRecord(String name,
                                    String cut,
                                    String stun,
                                    String miss,
                                    String phys,
                                    String msg,
                                    List<String> act,
                                    String desc,
                                    int line) {
}
