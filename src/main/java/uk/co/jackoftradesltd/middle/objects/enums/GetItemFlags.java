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

package uk.co.jackoftradesltd.middle.objects.enums;

/**
 * The mode bits a caller passes to {@code get_item()} to say where the item may come from and how the
 * list is drawn. The port of the twelve {@code #define}s under "Bit flags for get_item() function"
 * in {@code game-input.h}.
 *
 * <p>C packs them into an {@code int} ({@code USE_EQUIP} 0x0001 up to {@code SHOW_THROWING} 0x0800,
 * one bit each, in the order declared below) and combines them with {@code |}. This port holds the
 * set in a {@code Flag<GetItemFlags>}, which is an {@link java.util.EnumSet}, so no mask value is
 * stored here and the ordinal carries no meaning for callers. The declaration order still follows
 * the bit order in C, so ordinal {@code n} is the bit {@code 1 << n} if a mask is ever needed.
 *
 * <p>The first four say which containers the player may pick from; the rest are display and
 * warning options read by the item menu in {@code ui-object.c}. A caller that wants everything
 * turns on all four {@code USE_*} bits, as {@code cmd-obj.c} does for {@code do_cmd_inscribe}.
 *
 * <p>Enum GetItemFlags coded before 260815, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public enum GetItemFlags {
    /**
     * Allow items from the equipment. C bit 0x0001. {@code cmd_get_item} ({@code cmd-core.c})
     * switches it off, with {@link #USE_INVEN} and {@link #USE_QUIVER}, when the player is
     * shapechanged, because a shapechanged player can only reach the floor.
     *
     * <p>Constant USE_EQUIP coded before 260815, commented in full on 261008.
     */
    USE_EQUIP,
    /**
     * Allow items from the pack. C bit 0x0002.
     *
     * <p>Constant USE_INVEN coded before 260815, commented in full on 261008.
     */
    USE_INVEN,
    /**
     * Allow items on the floor under the player. C bit 0x0004.
     *
     * <p>Constant USE_FLOOR coded before 260815, commented in full on 261008.
     */
    USE_FLOOR,
    /**
     * Allow items from the quiver. C bit 0x0008.
     *
     * <p>Constant USE_QUIVER coded before 260815, commented in full on 261008.
     */
    USE_QUIVER,
    /**
     * Ignore generic warning inscriptions: {@code get_item_allow} in {@code ui-object.c} skips
     * its check for the {@code !*} inscription, so only the command-specific one can ask for
     * confirmation. C bit 0x0010. Read as {@code is_harmless} in {@code get_item}.
     *
     * <p>Constant IS_HARMLESS coded before 260815, commented in full on 261008.
     */
    IS_HARMLESS,
    /**
     * Show item prices in the item lists. C bit 0x0020. Set by the store UI in {@code ui-store.c}.
     *
     * <p>Constant SHOW_PRICES coded before 260815, commented in full on 261008.
     */
    SHOW_PRICES,
    /**
     * Show the device failure chance in the item lists. C bit 0x0040. Used by the commands that
     * activate or use devices in {@code cmd-obj.c}.
     *
     * <p>Constant SHOW_FAIL coded before 260815, commented in full on 261008.
     */
    SHOW_FAIL,
    /**
     * Show the quiver summary line when the pack is displayed. C bit 0x0080.
     *
     * <p>Constant SHOW_QUIVER coded before 260815, commented in full on 261008.
     */
    SHOW_QUIVER,
    /**
     * Show empty slots in the equipment display. C bit 0x0100.
     *
     * <p>Constant SHOW_EMPTY coded before 260815, commented in full on 261008.
     */
    SHOW_EMPTY,
    /**
     * Use the digits 0 to 9 as the quiver slot labels while selecting. C bit 0x0200. Read as
     * {@code quiver_tags} in {@code ui-object.c}.
     *
     * <p>Constant QUIVER_TAGS coded before 260815, commented in full on 261008.
     */
    QUIVER_TAGS,
    /**
     * Show the recharge failure chance in the item lists. C bit 0x0400. Used by the recharge
     * effect in {@code effect-handler-general.c}.
     *
     * <p>Constant SHOW_RECHARGE coded before 260815, commented in full on 261008.
     */
    SHOW_RECHARGE,
    /**
     * Show the throwable items from the pack, quiver and floor. C bit 0x0800. Read as
     * {@code show_throwing} in {@code ui-object.c}; used by the throw command in
     * {@code player-attack.c}.
     *
     * <p>Constant SHOW_THROWING coded before 260815, commented in full on 261008.
     */
    SHOW_THROWING
}
