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

package uk.co.jackoftradesltd.backend.parser.world;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.middle.cave.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns the raw {@link WorldParseRecord}s from {@code world.txt} into {@link World} level
 * objects — Java port of {@code parse_world_level} in {@code src/init.c}. The one piece of real
 * interpretation is the {@code "None"} sentinel: C's {@code streq(up, "None") ? NULL : ...} (and
 * the equivalent for {@code down}) is mirrored here by turning the literal string {@code "None"}
 * into a Java {@code null}, so a level with no level above/below it carries a real {@code null}
 * rather than the placeholder text.
 *
 * <p>Unlike most assemblers in this suite this one has no soft-error channel: {@code errors} is
 * accepted to satisfy the {@link Assembler} contract but never written to, because every field a
 * record carries is already well-formed by the time it reaches here (the level number is the one
 * value parsed as an integer, and a malformed one would throw rather than being reported softly).
 *
 * @author Rowan Crowther
 */
public class WorldAssembler
        implements Assembler<WorldParseRecord, List<World>> {
    /**
     * Assembles the {@link World} record from the {@link WorldParseRecord}
     * records passed in. Doesn't raise any errors.
     *
     * <p>Function assemble coded before 260915, commented in full on 260915.
     *
     * @param records List of {@link WorldParseRecord} objects
     * @param errors  List of errors as string messages - ignored
     *                in this assembler as it doesn't raise any
     *                errors
     * @return result of assembling list of {@link World} objects
     */
    @Override
    public List<World> assemble(@NotNull List<WorldParseRecord> records, @NotNull List<String> errors) {
        List<World> results = new ArrayList<>();
        for (WorldParseRecord record : records) {
            int levelNumber = Integer.parseInt(record.getLevelNumber());
            String levelName = record.getLevelName();
            String up = record.getUp().equals("None") ? null : record.getUp();
            String down = record.getDown().equals("None") ? null : record.getDown();

            results.add(new World(levelNumber, levelName, up, down));
        }

        return results;
    }
}