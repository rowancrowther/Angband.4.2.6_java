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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.CaveProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;

import java.util.List;

/**
 * Holds the level-generation data loaded from {@code lib/gamedata} for the rest of the game to
 * read — the port's home for what C keeps in the file-scope globals of {@code generate.c}.
 *
 * <p>Populated once at start-up by {@link uk.co.jackoftradesltd.middle.game.globals.loaders.DungeonLoader},
 * which installs each list through the matching setter below. Until a loader has run, the list it
 * feeds is {@code null} — the same state as the C pointers before {@code run_template_parser()}
 * has been called.
 *
 * <p>The three lists correspond to three C globals in {@code generate.c}: {@code cave_profiles}
 * (a {@code static} array, reached from outside {@code generate.c} only through the
 * {@code get_level_profile_*} functions), and {@code room_templates} and {@code vaults} (linked
 * lists, declared {@code extern} in {@code generate.h}). The Java fields are all {@code private};
 * there are no getters yet, because every C reader of these lists ({@code choose_profile()},
 * {@code find_cave_profile()} and the {@code get_level_profile_*} functions in {@code generate.c},
 * {@code random_room_template()} and {@code random_vault()} in {@code gen-room.c}) belongs to the level-generation chapter. The tests
 * read the fields by reflection until then.
 *
 * <p>List order differs between the three, and follows C. {@code finish_parse_profile()} copies the
 * profiles into an array back to front, so {@code cave_profiles} ends up in file order. The room and
 * vault parsers instead prepend each new record ({@code parse_room_name()} and
 * {@code parse_vault_name()}) and {@code finish_parse_room()} / {@code finish_parse_vault()} keep
 * the head, so C's lists run in <em>reverse</em> file order; the Java assemblers use
 * {@code addFirst} to match. Both selectors walk the whole list doing a one-in-n reservoir pick, so
 * the order never changes the odds — only which record a given random sequence lands on.
 *
 * <p>None of the three C lists has a blank slot ({@code z_info->profile_max} counts only real
 * profiles, and the two linked lists have no counter), so the accepted blank-slot deviation for
 * base-1 registries does not arise here.
 *
 * <p>Class DungeonRegistry coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class DungeonRegistry {
    /**
     * Logger for the registry. Nothing in this class logs yet — the setters are plain assignments
     * with no guard to report — and C has no counterpart.
     *
     * <p>Field logger coded before 261014, commented in full on 261014.
     */
    private static final Logger logger = LogManager.getLogger(DungeonRegistry.class);

    /**
     * Every level style the game may build, in file order — C's {@code cave_profiles} array
     * ({@code generate.c}, {@code static}), built from {@code dungeon_profile.txt}.
     *
     * <p>{@code null} until {@code DungeonLoader.loadDungeonProfiles()} has run. The count C keeps
     * as {@code z_info->profile_max} is held separately, in {@code GameConstants}.
     *
     * <p>Field caveProfiles coded before 261014, commented in full on 261014.
     */
    private static List<CaveProfile> caveProfiles;

    /**
     * Every room template the game may lay out, in <em>reverse</em> file order — C's
     * {@code room_templates} linked list ({@code generate.c}, declared in {@code generate.h}),
     * built from {@code room_template.txt}.
     *
     * <p>{@code null} until {@code DungeonLoader.loadRoomTemplates()} has run. Nothing in
     * {@code z_info} counts these; a reader takes the list's size.
     *
     * <p>Field roomTemplates coded before 261014, commented in full on 261014.
     */
    private static List<RoomTemplate> roomTemplates;

    /**
     * Every vault the game may place, in <em>reverse</em> file order — C's {@code vaults} linked
     * list ({@code generate.c}, declared in {@code generate.h}), built from {@code vault.txt}.
     *
     * <p>Holds all six vault types plus the interesting rooms, since {@code vault.txt} carries them
     * in one file and distinguishes them only by the {@code type:} line.
     *
     * <p>{@code null} until {@code DungeonLoader.loadVaultTemplates()} has run.
     *
     * <p>Field vaultTemplates coded before 261014, commented in full on 261014.
     */
    private static List<Vault> vaultTemplates;

    /**
     * Install the level styles read from {@code dungeon_profile.txt}.
     *
     * <p>Replaces whatever was held before, so calling this twice discards the first set. C does
     * the same in {@code finish_parse_profile()} ({@code generate.c}), which assigns a freshly
     * allocated {@code cave_profiles} array. The list is stored as given, not copied, and a
     * {@code null} argument is accepted and puts the registry back in its unloaded state.
     *
     * <p>Method setCaveProfiles coded before 261014, commented in full on 261014.
     *
     * @param caveProfiles the profiles to install, in file order
     */
    public static void setCaveProfiles(List<CaveProfile> caveProfiles) {
        DungeonRegistry.caveProfiles = caveProfiles;
    }

    /**
     * Install the room templates read from {@code room_template.txt}.
     *
     * <p>Replaces whatever was held before, so calling this twice discards the first set. C does
     * the same in {@code finish_parse_room()} ({@code generate.c}), which simply points
     * {@code room_templates} at the head of the new list. The list is stored as given, not
     * copied, and a {@code null} argument is accepted and puts the registry back in its unloaded
     * state.
     *
     * <p>Method setRoomTemplates coded before 261014, commented in full on 261014.
     *
     * @param roomTemplates the templates to install, in reverse file order as C's list is
     */
    public static void setRoomTemplates(List<RoomTemplate> roomTemplates) {
        DungeonRegistry.roomTemplates = roomTemplates;
    }

    /**
     * Install the vaults read from {@code vault.txt}.
     *
     * <p>Replaces whatever was held before, so calling this twice discards the first set. C does
     * the same in {@code finish_parse_vault()} ({@code generate.c}), which points {@code vaults}
     * at the head of the new list. The list is stored as given, not copied, and a {@code null}
     * argument is accepted and puts the registry back in its unloaded state.
     *
     * <p>Method setVaultTemplates coded before 261014, commented in full on 261014.
     *
     * @param items the vaults to install, in reverse file order as C's list is
     */
    public static void setVaultTemplates(List<Vault> items) {
        DungeonRegistry.vaultTemplates = items;
    }
}
