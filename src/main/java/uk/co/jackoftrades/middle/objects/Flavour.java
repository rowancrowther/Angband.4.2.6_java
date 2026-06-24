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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.middle.objects.enums.TValue;

/**
 * A randomised "flavour" for an unidentified object kind — the disguising name
 * (e.g. a potion's colour) and the glyph/colour it is shown with until
 * identified. This is the Java port of the C original's {@code struct flavor}
 * ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public class Flavour {
    /**
     * The flavour text shown for the unidentified object (e.g. "Azure").
     *
     * @author ClaudeCode
     */
    private String text;

    /**
     * The item type this flavour applies to.
     *
     * @author ClaudeCode
     */
    private TValue tVal;
    /**
     * The sub-type value this flavour applies to.
     *
     * @author ClaudeCode
     */
    private int sVal;

    /**
     * The glyph and colour used to draw the flavoured object.
     *
     * @author ClaudeCode
     */
    private AngbandDisplayCharacter displayCharacter;
}
