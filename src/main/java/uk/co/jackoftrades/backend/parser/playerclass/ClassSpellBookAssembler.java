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

package uk.co.jackoftrades.backend.parser.playerclass;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.Assembler;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.magic.MagicBook;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.magic.MagicSpell;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.ObjectBase;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.*;

/**
 * Turns a class's raw {@link ClassSpellBookParseRecord}s into domain {@link MagicBook}s and, as a
 * side effect, registers the {@link ObjectKind} that backs each book. This is the deepest
 * cross-referencing leg of the class loader: for every book it resolves the magic
 * {@link MagicRealm} ({@link GameConstants#lookupRealm}) and the {@link ObjectBase} of the book's
 * tval ({@link GameConstants#getBaseFromTVal}), and it descends into the book's spells through
 * {@link ClassSpellAssembler} (which in turn resolves each effect, including {@code SHAPECHANGE}
 * targets, against their registries).
 *
 * <p><b>Book-kind synthesis (port of C's {@code write_book_kind}):</b> spellbooks are not listed in
 * {@code object.txt}; the C source manufactures an object kind for each book at class-load time.
 * This assembler does the same — it builds an {@link ObjectKind} from the book's base and
 * properties and adds it to {@link GameConstants} so the book becomes a real, obtainable item.
 * Dungeon books additionally gain the {@code KF_GOOD} flag and {@code EL_INFO_IGNORE} on every base
 * element, matching the C treatment.
 *
 * <p>Every lookup that can miss (unknown tval, realm or base) reports a soft error and skips that
 * book rather than throwing, per the shared contract.
 *
 * @author Rowan Crowther
 */
public class ClassSpellBookAssembler implements Assembler<ClassSpellBookParseRecord, List<MagicBook>> {
    /**
     * Assembles every parsed book into a {@link MagicBook}, parsing its scalars, resolving its
     * realm and object base, building its spells, and registering the synthesised backing
     * {@link ObjectKind}.
     *
     * @param records the raw book records for one class, in file order
     * @param errors  the soft-error channel; unresolved realms/bases and malformed scalars are appended here
     * @return the resolved magic books, minus any dropped for a failed lookup
     * @author Rowan Crowther
     */
    @Override
    public List<MagicBook> assemble(@NotNull List<ClassSpellBookParseRecord> records, @NotNull List<String> errors) {
        List<MagicBook> magicBooks = new ArrayList<>();

        for (ClassSpellBookParseRecord record : records) {
            int line = record.line();
            String tValueString = "TV_" + record.oBase();
            TValue tValue = TValue.fromName(tValueString);
            if (tValue == null) {
                errors.add("Spell book at line: " + line + " has " +
                        "an unknown first parameter: " + tValueString);
                continue;
            }
            boolean dungeon = record.locFrom().equals("dungeon");
            String bookName = record.name();
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
            MagicRealm realm = GameConstants.lookupRealm(record.realm());
            if (realm == null) {
                errors.add("Spell book at line: " + line + " has " +
                        "an unknown realm: " + record.realm());
                continue;
            }
            String glyph = record.glyph();
            String colour = record.colour();
            AngbandDisplayCharacter adc = null;
            if (!glyph.isEmpty() && !colour.isEmpty()) {
                if (glyph.length() != 1) {
                    errors.add("Spell book at line: " + line + " has " +
                            " an illegal glyph:" + glyph);
                    continue;
                }
                ColourType colourType = ColourType.getColourType(colour);
                if (colourType == null) {
                    errors.add("Spell book at line: " + line + " has " +
                            "an unknown colour type: " + colour);
                    continue;
                }
                adc = new AngbandDisplayCharacter(glyph.charAt(0), colourType);
            }
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
            List<MagicSpell> spells = new ClassSpellAssembler().assemble(record.spells(), errors);

            magicBooks.add(new MagicBook(tValue, bookName, dungeon,
                    noOfSpells, realm, adc, cost, commonness, min,
                    max, spells));

            // Synthesise the object kind that backs this book (C: write_book_kind). Spellbooks are
            // not in object.txt, so the kind is built from the base plus this book's properties and
            // registered, making the book an obtainable item. Dungeon books are marked "good" and
            // set to ignore every base element, as in the C original.
            ObjectBase base = GameConstants.getBaseFromTVal(tValue);
            if (base == null) {
                errors.add("Spell book at line: " + line + " has " +
                        "an unknown book tValue: " + bookName);
                continue;
            }

            Flag<ObjectKindFlag> oFlags = base.getKindFlags().copy();
            Map<ElementEnum, ElementInfo> eFlags = new HashMap<>();

            if (dungeon) {
                oFlags.on(ObjectKindFlag.KF_GOOD);
                for (ElementEnum e : Arrays.stream(ElementEnum.values())
                        .filter(ElementEnum::isBase).toList()) {
                    ElementInfo info = eFlags.computeIfAbsent(e, k -> new ElementInfo());
                    Flag<ElementInfoEnum> iFlag = info.getFlags();
                    iFlag.on(ElementInfoEnum.EL_INFO_IGNORE);
                }
            }

            ObjectKind kind = new ObjectKind(bookName, bookName,
                    base, 0, null, null, null,
                    null, 0, null, 0, 0,
                    0, cost, new Flag<>(ObjectFlag.class),
                    oFlags, new HashMap<>(), eFlags, new HashMap<>(),
                    new HashMap<>(), new HashMap<>(), adc,
                    commonness, min, max, 0, new ArrayList<>(), new ArrayList<>(),
                    "", "", "", null, 0,
                    null, null, null, null,
                    false, false, 0, false, tValue);

            GameConstants.addObjectKind(kind);
        }

        return magicBooks;
    }
}
