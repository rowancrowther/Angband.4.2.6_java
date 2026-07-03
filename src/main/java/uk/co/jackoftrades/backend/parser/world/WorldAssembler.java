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

package uk.co.jackoftrades.backend.parser.world;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.middle.cave.World;

import java.util.ArrayList;
import java.util.List;

public class WorldAssembler
        implements Assembler<WorldParseRecord, List<World>> {
    /**
     * Assembles the {@link World} record from the {@link WorldParseRecord}
     * records passed in. Doesn't raise any errors.
     *
     * @param records List of {@link WorldParseRecord} objects
     * @param errors  List of errors as string messages - ignored
     *                in this assembler as it doesn't raise any
     *                errors
     * @return result of assembling list of {@link World} objects
     * @author Rowan Crowther
     */
    @Override
    public List<World> assemble(@NotNull List<WorldParseRecord> records, @NotNull List<String> errors) {
        List<World> results = new ArrayList<>();
        for (WorldParseRecord record : records) {
            int levelNumber = -1;
            try {
                levelNumber = Integer.parseInt(record.getLevelNumber());
            } catch (NumberFormatException e) {
                errors.add("Invalid level number: " + record.getLevelNumber());
            }
            String levelName = record.getLevelName();
            String up = record.getUp().equals("None") ? null : record.getUp();
            String down = record.getDown().equals("None") ? null : record.getDown();

            results.add(new World(levelNumber, levelName, up, down));
        }

        return results;
    }
}