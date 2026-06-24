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

package uk.co.jackoftrades.frontend.entries.enums;

/**
 * Sign-display modes for a UI entry's numeric value, choosing
 * whether and when a {@code +}/{@code -} is shown.
 *
 * @author ClaudeCode
 */
public enum UIEntryEnum {
    /**
     * Never show a sign. @author ClaudeCode
     */
    UI_ENTRY_NO_SIGN,
    /** Always show a sign, including {@code +} for positives. @author ClaudeCode */
    UI_ENTRY_ALWAYS_SIGN,
    /** Show a sign only for negative values. @author ClaudeCode */
    UI_ENTRY_NEGATIVE_SIGN,
    /** Use the renderer's default sign behaviour. @author ClaudeCode */
    UI_ENTRY_SIGN_DEFAULT
}