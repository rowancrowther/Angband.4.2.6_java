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

package uk.co.jackoftradesltd.frontend.screen;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.frontend.screen.hooks.TextOutHook;
import uk.co.jackoftradesltd.channel.utils.StringUtils;

/**
 * Formats and colours text before handing it to a {@link TextOutHook} sink, the Java port of
 * the C original's {@code text_out} and {@code text_out_c} ({@code [C] src/z-textblock.c}). C
 * dispatches through a single raw function pointer, {@code text_out_hook}, that the front end
 * points at either a screen writer or {@code text_out_to_file}; this wraps that pointer as an
 * injected {@link TextOutHook} instead, so the destination is fixed per {@code TextOut} instance
 * rather than swapped globally. {@code text_out_to_file}'s own line-wrapping is the concern of
 * whichever {@link TextOutHook} writes to a file, not of this class.
 *
 * @author Rowan Crowther
 */
public class TextOut {
    /**
     * The terminal this instance is attached to. Not yet read by {@link #textOut} or
     * {@link #textOutC} - both write only through {@link #textOutHook}.
     */
    private Term term;

    /**
     * The sink formatted, coloured text is written to, the Java port of C's
     * {@code text_out_hook} function pointer ({@code [C] src/z-textblock.c}).
     */
    private TextOutHook textOutHook;

    /**
     * @param term        the terminal this output is attached to
     * @param textOutHook the sink to write formatted, coloured text to
     */
    public TextOut(Term term, TextOutHook textOutHook) {
        this.term = term;
        this.textOutHook = textOutHook;
    }

    /**
     * Format {@code fmt} with {@code args} and write it in {@link ColourEnum#COLOUR_WHITE}, the
     * Java port of the C original's {@code text_out} ({@code [C] src/z-textblock.c}). Delegates
     * entirely to {@link #textOutC}, matching {@code text_out}'s own call to
     * {@code text_out_hook(COLOUR_WHITE, buf)}.
     *
     * <p>Function textOut coded on 260910, commented in full on 260910.
     *
     * @param fmt  the format string
     * @param args the objects to format {@code fmt} with
     */
    public void textOut(String fmt, Object... args) {
        textOutC(ColourEnum.COLOUR_WHITE, fmt, args);
    }

    /**
     * Format {@code fmt} with {@code args} and write it in {@code colour} to
     * {@link #textOutHook}, the Java port of the C original's {@code text_out_c}
     * ({@code [C] src/z-textblock.c}). C formats into a fixed {@code char buf[1024]} via
     * {@code vstrnfmt(buf, sizeof(buf), fmt, vp)}, which leaves output shorter than the buffer
     * untouched and truncates only text that would overrun it, to 1023 characters; this calls
     * {@link StringUtils#vstrnfmt(int, String, Object...)} with the same 1024 bound to match.
     *
     * <p>Function textOutC coded on 260910, commented in full on 260910.
     *
     * @param colour the colour to write {@code fmt} in
     * @param fmt    the format string
     * @param args   the objects to format {@code fmt} with
     */
    public void textOutC(ColourEnum colour, String fmt, Object... args) {
        String buf = StringUtils.vstrnfmt(1024, fmt, args);
        textOutHook.output(colour, buf);
    }

    /**
     * Format {@code fmt} with {@code args} and write it to {@link #textOutHook} one tagged
     * section at a time, the Java port of the C original's {@code text_out_e}
     * ({@code [C] src/z-textblock.c}). Like {@link #textOutC}, C formats into a fixed
     * {@code char buf[1024]} via {@code vstrnfmt(buf, sizeof(buf), fmt, vp)}; this calls
     * {@link StringUtils#vstrnfmt(int, String, Object...)} with the same 1024 bound to match.
     *
     * <p>C then walks the buffer with repeated calls to {@code next_section}, starting from
     * {@code start = buf}, printing each section it returns in the colour named by that
     * section's tag (or {@code COLOUR_WHITE} if the section is untagged, or its tag doesn't
     * name a real colour), and re-entering the loop from the {@code end} that call produced,
     * until {@code next_section} reports nothing left. This mirrors that shape with
     * {@link #nextSection}: {@code start} is seeded with the freshly formatted {@code buf},
     * each {@link SectionDetails} in turn supplies the colour (via
     * {@link ColourEnum#fromCode(String)}, falling back to {@link ColourEnum#COLOUR_WHITE}
     * for an empty or unrecognised tag) and text for one {@link #textOutHook} call, and the
     * loop re-enters on {@link SectionDetails#next} until {@link SectionDetails#found} is
     * {@code false}.
     *
     * <p>Function textOutE coded on 260910, commented in full on 260911.
     *
     * @param fmt  the format string
     * @param args the objects to format {@code fmt} with
     */
    public void textOutE(String fmt, Object... args) {
        // format the string into a 1024 length buffer
        String buf = StringUtils.vstrnfmt(1024, fmt, args);

        String start = buf;
        SectionDetails currentSection = nextSection(start, 0, "");
        while (currentSection.found) {
            ColourEnum colourAttr = ColourEnum.COLOUR_WHITE;
            String smallBuf = currentSection.text();
            String tag = currentSection.tag();
            String next = currentSection.next();

            if (!tag.isEmpty()) {
                colourAttr = ColourEnum.fromCode(tag);
            }
            
            if (colourAttr == null)
                colourAttr = ColourEnum.COLOUR_WHITE;

            textOutHook.output(colourAttr, smallBuf);

            start = next;

            currentSection = nextSection(start, 0, next);
        }
    }

    /**
     * Finds the next printable section of {@code source} starting at {@code init}, splitting off
     * any {@code {tagName}...{/}} markup as it goes, the Java port of the C original's
     * {@code next_section} ({@code [C] src/z-textblock.c}). C's {@code static bool next_section(...)}
     * hands its result back through a set of {@code char **} out-parameters
     * ({@code text}/{@code len}/{@code tag}/{@code taglen}/{@code end}); this bundles the same
     * information into a {@link SectionDetails} record instead, one section per call.
     *
     * <p>{@code end} exists only for symmetry with the C signature's out-parameter shape - every
     * return path overwrites it before it is read, so whatever the caller passes in is discarded.
     *
     * <p>Mirrors C's outcomes clause for clause. A well-formed {@code {tag}} at the very start of
     * the remaining text clips the section to run up to (not including) the matching {@code {/}},
     * fills {@link SectionDetails#tag} with the name between the braces, and points
     * {@link SectionDetails#next} past the {@code {/}}. A {@code {tag}} found further into the
     * text instead returns only the plain text before it, with an empty tag, and
     * {@link SectionDetails#next} pointing at the {@code {} itself so the following call re-parses
     * it. An unclosed {@code {tag}} (no matching {@code {/}} anywhere after it), an invalid tag
     * body (anything other than letters and spaces before the closing {@code }}), and a {@code {}
     * that runs off the end of {@code source} before closing are all treated the way C treats
     * them: the whole remaining text is handed back untagged, with {@link SectionDetails#next}
     * empty. An empty {@code source} substring returns {@link SectionDetails#found} {@code false},
     * matching C's {@code if (*text[0] == '\0') return false;}.
     *
     * <p>Function nextSection coded on 260910, commented in full on 260911.
     *
     * @param source the full formatted string being scanned
     * @param init   the offset into {@code source} to start scanning from
     * @param end    unused as input - every return path overwrites it before it can be read
     * @return the next section found; {@link SectionDetails#found} is {@code false} only when
     * {@code source.substring(init)} is empty
     */
    public SectionDetails nextSection(String source, int init, String end) {
        String tag = "";
        String text = source.substring(init);

        if (text.isEmpty())
            return new SectionDetails(false, text, tag, text);

        // Does a '{' exist here ar all
        int next = text.indexOf('{');
        int lastOpenBrace = next;

        while (next > -1 && next <= text.length()) {
            next++;
            if (next >= text.length())
                return new SectionDetails(true, text, tag, "");
            char character = text.charAt(next);

            // Loop through normal characters
            while (StringUtils.isAlpha(character) || StringUtils.isSpace(character)) {
                next++;
                if (next >= text.length()) {
                    // Tag runs out before closing brace, return up to the end of the line
                    return new SectionDetails(true, text, tag, "");
                }

                character = text.charAt(next);
            }

            if (character == '}') {
                // Check for a closing tag
                boolean close = text.substring(next + 1).contains("{/}");

                if (close) {
                    // There is a closing tag - it's valid
                    if (lastOpenBrace == 0) {
                        // The tag is at the start of the fragment
                        tag = text.substring(lastOpenBrace + 1, next - lastOpenBrace);
                        end = text.substring(text.indexOf("{/}") + 3);
                        text = text.substring(next + 1, text.indexOf("{/}"));
                        return new SectionDetails(true, text, tag, end);
                    } else {
                        // Return the chunk up to this point
                        tag = "";
                        end = text.substring(lastOpenBrace);
                        return new SectionDetails(true, text.substring(0, lastOpenBrace), tag, end);
                    }
                } else {
                    // No closing tag - the entire rest of the string is one
                    // chunk of text
                    return new SectionDetails(true, text, tag, "");
                }
            } else {
                // Skip the invalid tag and move to the start of the next tag
                next = lastOpenBrace + 1;
            }

            next = text.indexOf('{', next);
            lastOpenBrace = next;
        }

        // Fell off the end of the string
        return new SectionDetails(true, text, tag, "");
    }

    /**
     * States for a character-by-character walk over {@code {tag}...{/}} markup: whether the scan
     * is currently inside an opening {@code {tag}}, inside a closing {@code {/}}, inside the
     * tagged text between them, or in plain text outside any tag. Declared alongside
     * {@link #nextSection} but not read or assigned anywhere in this class - {@link #nextSection}
     * scans with plain string-index arithmetic (tracking {@code next} and {@code lastOpenBrace}
     * directly) instead of stepping through named states, so this enum is currently unused dead
     * code with no C cross-reference of its own; {@code next_section} ({@code [C]
     * src/z-textblock.c}) has no equivalent state enumeration either, since it too works by
     * pointer arithmetic rather than an explicit state machine.
     *
     * <p>Enum TagState coded on 260910, commented in full on 260916.
     */
    private enum TagState {
        /**
         * Scanning the body of an opening {@code {tag}}, between the {@code {} and the {@code }}.
         */
        START_TAG,
        /**
         * Scanning a closing {@code {/}} marker.
         */
        END_TAG,
        /**
         * Scanning the tagged text between a well-formed {@code {tag}} and its {@code {/}}.
         */
        IN_TAG_TEXT,
        /**
         * Scanning plain text that falls outside any tag.
         */
        IN_NORMAL_TEXT
    }

    /**
     * One section of scanned text returned by {@link #nextSection}, the Java replacement for the
     * cluster of {@code char **}/{@code size_t *} out-parameters C's {@code next_section}
     * ({@code [C] src/z-textblock.c}) writes its result through ({@code text}/{@code len}/
     * {@code tag}/{@code taglen}/{@code end}), bundled here as a single return value instead of
     * five separate out-parameters.
     *
     * <p>Record SectionDetails coded on 260910, commented in full on 260916.
     *
     * @param found whether a section was found at all; {@code false} only when the source scanned
     *              was empty, matching C's {@code if (*text[0] == '\0') return false;}
     * @param text  the printable text of this section, with any surrounding {@code {tag}...{/}}
     *              markup already stripped off - C's {@code text}/{@code len} pair
     * @param tag   the tag name found between the braces when this section opened with a
     *              well-formed {@code {tag}}, or the empty string when the section is untagged -
     *              C's {@code tag}/{@code taglen} pair, with C's {@code NULL} represented here as
     *              {@code ""}
     * @param next  the remaining unscanned text to resume scanning from on the following call -
     *              C's {@code end} pointer, represented here as the substring it would point into
     *              rather than as a pointer
     */
    private record SectionDetails(boolean found, String text, String tag, String next) {
    }
}