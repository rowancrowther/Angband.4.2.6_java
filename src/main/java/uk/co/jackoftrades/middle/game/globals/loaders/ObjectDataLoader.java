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

package uk.co.jackoftrades.middle.game.globals.loaders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.parser.*;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.game.globals.AngbandDirs;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.objects.*;

import java.io.IOException;

/**
 * Startup loader for the object slice: parses the object-domain gamedata files
 * ({@code object_base.txt}, {@code slay.txt}, {@code brand.txt}, {@code curse.txt},
 * {@code object.txt}, {@code activation.txt}, {@code ego_item.txt}, {@code artifact.txt},
 * {@code object_property.txt}) and populates
 * {@link uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry} through its setters and
 * {@code addObjectKind}.
 *
 * <p>This is the write side of the object slice, paired with {@code ObjectRegistry} (the read
 * side). Its loaders are invoked by {@code GameConstants.init()} in dependency order — bases before
 * slays/brands/curses; curses and item objects before ego items and artifacts. {@code loadItemObjects}
 * registers the ordinary object kinds and {@code loadArtifacts} synthesises the special artifact
 * kinds into the same table, so their order is load-bearing. It was split out of
 * {@code GameConstants} as one domain slice of the loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class ObjectDataLoader {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Load the object properties from {@code object_property.txt} into {@link ObjectRegistry}. Must
     * run after the UI entries, which the property assembler resolves {@code bindui} targets against.
     */
    public static void loadObjectProperties() {
        ObjectPropertyReader parser = new ObjectPropertyReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "object_property.txt";

        try {
            ParseResult<ObjectProperty> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            ObjectRegistry.setObjectProperties(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the artifacts from {@code artifact.txt} into {@link ObjectRegistry}, synthesising a special
     * {@link uk.co.jackoftrades.middle.objects.ObjectKind} for each into the shared object-kind table.
     * Must run after item objects (so the ordinary kinds are already registered) and after
     * activations, brands, slays and curses, which artifacts reference.
     */
    public static void loadArtifacts() {
        ArtifactReader parser = new ArtifactReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "artifact.txt";

        try {
            ParseResult<Artifact> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            ObjectRegistry.setArtifacts(result.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load the ego items into the relevant list
     */
    public static void loadEgoItems() {
        EgoItemReader reader = new EgoItemReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "ego_item.txt";

        try {
            ParseResult<EgoItem> results = reader.parseWithResults(filename);

            if (results.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            ObjectRegistry.setEgoItems(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load in the activations from activation.txt and store them in a List
     */
    public static void loadActivations() {
        ActivationReader reader = new ActivationReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "activation.txt";

        try {
            ParseResult<Activation> results = reader.parseWithResult(filename);

            if (results.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            ObjectRegistry.setActivations(results.items());
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load in the items from object.txt and store them in a List
     */
    public static void loadItemObjects() throws IOException {
        ItemObjectReader parser = new ItemObjectReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "object.txt";

        try {
            ParseResult<ObjectKind> results = parser.parseWithResults(filename);

            if (results.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            for (ObjectKind kind : results.items()) {
                ObjectRegistry.addObjectKind(kind);
            }

            ObjectRegistry.updateObjectBaseKindMax();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Curses information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadCurses() throws IOException {
        CurseReader parser = new CurseReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "curse.txt";

        try {
            ParseResult<Curse> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            ObjectRegistry.setCurses(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Brand information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadBrands() throws IOException {
        BrandReader parser = new BrandReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "brand.txt";

        try {
            ParseResult<Brand> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            ObjectRegistry.setBrands(result.items());
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Load in the Slays information and store it in a List
     */
    public static void loadSlays() {
        SlayReader parser = new SlayReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "slay.txt";

        try {
            ParseResult<Slay> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            ObjectRegistry.setSlays(result.items());
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
        }
    }

    /**
     * Load in the ObjectBase information and store it in a List
     *
     * @throws IOException an IO error occurred during parsing
     */
    public static void loadObjectBases() throws IOException {
        ObjectBaseReader parser = new ObjectBaseReader();
        String filename = AngbandDirs.ANGBAND_DIR_GAMEDATA + "object_base.txt";

        try {
            ParseResult<ObjectBase> result = parser.parseWithResults(filename);

            if (result.hasErrors()) {
                String errorMessage = "Invalid " + filename + " file";
                IllegalStateException e = new IllegalStateException(errorMessage);
                logger.fatal(errorMessage, e);
                return;
            }

            ObjectRegistry.setObjectBases(result.items());
        } catch (IOException e) {
            logger.error("Exception while loading file {}", filename, e);
            throw e;
        }
    }
}
