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

package uk.co.jackoftradesltd.backend.parser.playerclass;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.Assembler;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.magic.MagicSpell;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.*;

/**
 * Turns the {@link ClassSpellBookParseRecord}s of one class into the {@link MagicBook}s of its
 * {@link uk.co.jackoftradesltd.middle.magic.ClassMagic}, and registers the {@link ObjectKind} that
 * backs each book. It does the work of four C parser callbacks in {@code init.c}, which fill a
 * {@code struct class_book} one {@code class.txt} line at a time: {@code parse_class_book} for the
 * {@code book:} line, {@code parse_class_book_graphics} and {@code parse_class_book_properties} for
 * the two optional lines after it, and {@code parse_class_spell} and its siblings for the spells
 * (here delegated to {@link ClassSpellAssembler}). The kind is the work of {@code write_book_kind},
 * which {@code parse_class_book} calls.
 *
 * <p><b>Book-kind synthesis.</b> Spellbooks are not in {@code object.txt}; C makes an object kind
 * for each book while it reads {@code class.txt}, and this assembler does the same. A book's kind is
 * found by name alone, whichever tval it has. If one is already registered, because an earlier class
 * lists the same book, it is reused and its sval is shared; otherwise a new kind is built and handed
 * to {@link ObjectRegistry#addObjectKind}, which gives it the next sval under its base. A new kind
 * has damage dice of {@code 1d1} and a weight of 30, inherits the base's kind flags, and takes the
 * book's cost, {@code commonness} (as {@code alloc_prob}), minimum and maximum depth, and glyph and
 * colour. A dungeon book's kind also gets {@code KF_GOOD} and {@code EL_INFO_IGNORE} on every base
 * element. The kind is registered before {@link MagicBook#setSVal()} runs, which is how the book
 * learns its sval.
 *
 * <p><b>Defaults.</b> The graphics and properties lines are optional, and a missing value arrives as
 * an empty string. A missing glyph becomes {@code *} and a missing colour red, which are the
 * defaults {@code write_book_kind} sets before the graphics line overwrites them. A missing cost,
 * commonness, minimum or maximum becomes 0. The declared spell count on the {@code book:} line is
 * ignored: the count is the number of spells assembled. C uses the declared count as a cap and
 * rejects a class that lists more spells than it declared, and this assembler does not.
 *
 * <p><b>Differences from C.</b>
 * <ul>
 *   <li>A shared book's kind is built once, from the first class that lists it, and later listings
 *   do not touch it. C's graphics and properties callbacks rewrite the shared kind every time they
 *   run. In the shipped {@code class.txt} the later listings of a shared book omit both lines, so
 *   the result is the same, but a data file that gave a shared book different values in two places
 *   would keep the first.</li>
 *   <li>The graphics and properties are also copied onto the {@link MagicBook}, where nothing reads
 *   them. A second listing therefore carries the defaults there whatever the shared kind holds.</li>
 *   <li>The kind's {@code level} is the smaller of the minimum and maximum depth. C sets it to the
 *   first number of the {@code minmax} range, which is the same value for a range written
 *   low-to-high.</li>
 *   <li>Any quality other than {@code dungeon} makes a town book. C tests only for {@code dungeon},
 *   so the two agree.</li>
 * </ul>
 *
 * <p><b>Errors.</b> An unknown tval, realm, colour or base, a glyph that is not one character, or a
 * cost, commonness, minimum or maximum that is not an integer, appends a message naming the source
 * line to the soft-error list and skips that book. The spells are assembled before the base is
 * looked up, so a book skipped for an unknown base may already have recorded errors for its spells.
 * Looking up the book's kind throws {@link IllegalStateException} if no object kinds are loaded, so
 * {@code object.txt} must be loaded before any class.
 *
 * <p>Class ClassSpellBookAssembler coded before 260915, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class ClassSpellBookAssembler implements Assembler<ClassSpellBookParseRecord, List<MagicBook>> {
    private static final Logger logger = LogManager.getLogger(ClassSpellBookAssembler.class);
    
    /**
     * Assembles each book record into a {@link MagicBook}: it reads the scalars, resolves the realm
     * and object base, assembles the spells, builds and registers the backing {@link ObjectKind} if
     * none is registered under the book's name, and resolves the book's sval.
     *
     * <p>The steps run in a fixed order, and the first failure skips the book: tval, realm, glyph,
     * colour, cost, commonness, minimum, maximum, spells, base. Books that pass are returned in file
     * order, and the books of one class are registered in that order, so their svals follow it. See
     * the class block for the defaults, the kind that is built, and where this differs from C.
     *
     * <p>Function assemble coded before 260915, commented in full on 261010.
     *
     * @param records the raw book records for one class, in file order
     * @param errors  the soft-error list; a message is appended for each skipped book and for each
     *                spell error
     * @return a new list of the books that were assembled, without the skipped ones
     * @throws IllegalStateException if no object kinds are loaded
     */
    @Override
    public List<MagicBook> assemble(@NotNull List<ClassSpellBookParseRecord> records, @NotNull List<String> errors,
                                    String filename) {
        List<MagicBook> magicBooks = new ArrayList<>();
        for (ClassSpellBookParseRecord record : records) {
            int line = record.line();
            String tValueString = record.oBase();
            TValue tValue = TValue.fromName(tValueString);
            if (tValue == null) {
                errors.add("Spell book at line: " + line + " has " +
                        "an unknown first parameter: " + tValueString);
                continue;
            }
            boolean dungeon = record.locFrom().equals("dungeon");
            String bookName = record.name();
            // number of spells is calculated from the size of the spell list
            // and not the declared number of spells
            int noOfSpells = 0;
            if (!record.noOfSpells().isEmpty()) {
                try {
                    noOfSpells = Integer.parseInt(record.noOfSpells());
                } catch (NumberFormatException e) {
                    errors.add("Spell book at line: " + line + " has " +
                            "a malformed number of spells integer: " + record.noOfSpells());
                    continue;
                }
            }
            MagicRealm realm;
            try {
                realm = PlayerRegistry.lookupRealm(record.realm());
            } catch (IllegalArgumentException e) {
                realm = null;
            }
            if (realm == null) {
                errors.add("Spell book at line: " + line + " has " +
                        "an unknown realm: " + record.realm());
                continue;
            }
            String glyph = record.glyph();
            String colour = record.colour();
            AngbandDisplayCharacter adc;
            char glyphChar = '*';
            ColourEnum colourEnum = ColourEnum.COLOUR_RED;
            if (!glyph.isEmpty()) {
                if (glyph.length() != 1) {
                    errors.add("Spell book at line: " + line + " has " +
                            " an illegal glyph:" + glyph);
                    continue;
                }
                glyphChar = glyph.charAt(0);
            }
            if (!colour.isEmpty()) {
                colourEnum = ColourEnum.fromCode(colour);
                if (colourEnum == null) {
                    errors.add("Spell book at line: " + line + " has " +
                            "an unknown colour type: " + colour);
                    continue;
                }
            }
            adc = new AngbandDisplayCharacter(glyphChar, colourEnum);
            
            int cost = 0;
            if (!record.cost().isEmpty()) {
                try {
                    cost = Integer.parseInt(record.cost());
                } catch (NumberFormatException e) {
                    errors.add("Spell book at line: " + line + " has " +
                            "an illegal cost: " + record.cost());
                    continue;
                }
            }
            int commonness = 0;
            if (!record.commonness().isEmpty()) {
                try {
                    commonness = Integer.parseInt(record.commonness());
                } catch (NumberFormatException e) {
                    errors.add("Spell book at line: " + line + " has " +
                            "an illegal commonness: " + record.commonness());
                    continue;
                }
            }
            int min = 0;
            if (!record.min().isEmpty()) {
                try {
                    min = Integer.parseInt(record.min());
                } catch (NumberFormatException e) {
                    errors.add("Spell book at line: " + line + " has " +
                            "an illegal min: " + record.min());
                    continue;
                }
            }
            int max = 0;
            if (!record.max().isEmpty()) {
                try {
                    max = Integer.parseInt(record.max());
                } catch (NumberFormatException e) {
                    errors.add("Spell book at line: " + line + " has " +
                            "an illegal max: " + record.max());
                    continue;
                }
            }
            List<MagicSpell> spells = new ClassSpellAssembler().assemble(record.spells(), errors, filename);
            if (spells.size() > noOfSpells) {
                String message = "Too many spells. " + spells.size() + " spells found, " 
                        + noOfSpells + " declared. Magic Book defined at " + line + " in " +
                        "file " + filename;
                logger.fatal(message);
                throw new DataFileParseException(message);
            }
            
            for (MagicSpell spell : spells) {
                spell.setRealm(realm);
            }

            // Synthesize the object kind that backs this book (C: write_book_kind). Spellbooks are
            // not in object.txt, so the kind is built from the base plus this book's properties and
            // registered, making the book an obtainable item. Dungeon books are marked "good" and
            // set to ignore every base element, as in the C original.
            ObjectBase base = ObjectRegistry.getBaseFromTVal(tValue);
            if (base == null) {
                errors.add("Spell book at line: " + line + " has " +
                        "an unknown book tValue: " + bookName);
                continue;
            }

            Flag<ObjectKindFlag> oFlags = new Flag<>(ObjectKindFlag.class);

            oFlags.copyFrom(base.getKindFlags());
            Map<ElementEnum, ElementInfo> eFlags = new HashMap<>();

            ObjectKind kind = ObjectRegistry.lookupObjectKind(bookName);
            
            if (dungeon && kind == null) {
                oFlags.on(ObjectKindFlag.KF_GOOD);
                for (ElementEnum e : Arrays.stream(ElementEnum.values())
                        .filter(ElementEnum::isBase).toList()) {
                    ElementInfo info = eFlags.computeIfAbsent(e, k -> new ElementInfo());
                    Flag<ElementInfoEnum> iFlag = info.getFlags();
                    iFlag.on(ElementInfoEnum.EL_INFO_IGNORE);
                }
            }

            Flag<IgnoreFlag> ignoreFlags = new Flag<>(IgnoreFlag.class);

            if (kind == null) {
                kind = new ObjectKind(bookName, null,
                        base, 0, Random.Zero(), Random.Zero(), Random.Zero(),
                        Random.Zero(), 0, Random.Zero(), 1, 1,
                        30, cost, new Flag<>(ObjectFlag.class),
                        oFlags, new HashMap<>(), eFlags, new HashSet<>(),
                        new HashSet<>(), new HashMap<>(), adc,
                        commonness, min, max, Math.min(min, max), new ArrayList<>(), new ArrayList<>(),
                        null, null, Random.Zero(), Random.Zero(), 0,
                        Random.Zero(), null, null, null,
                        false, false, ignoreFlags, false, tValue, 0);
                ObjectRegistry.addObjectKind(kind);
            }

            MagicBook book = new MagicBook(tValue, bookName, dungeon,
                    realm, adc, cost, commonness, min,
                    max, spells);
            book.setSVal();
            magicBooks.add(book);
        }

        return magicBooks;
    }
}
