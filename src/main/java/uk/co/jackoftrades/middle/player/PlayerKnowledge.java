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

package uk.co.jackoftrades.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.channel.utils.FlagView;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.DamageAspect;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.enums.PlayerNotice;

import java.util.*;

/**
 * The object-knowledge machinery - the port of C's {@code obj-knowledge.c}, minus its parsing and
 * its display.
 *
 * <p>What a character knows about objects is not held on the objects. It is held once, on the
 * player, as {@link Player#itemKnowledge} - the runes, modifiers, elements, brands, slays and
 * curses they have learned to read - and each object carries a second, known counterpart showing
 * only the part of itself that knowledge entitles the player to see. Two directions of travel run
 * through this class, and keeping them apart is the way to read it:
 *
 * <ul>
 *   <li><b>Learning</b> writes to the player. The {@code learn*} family - {@link #learnFlag},
 *       {@link #learnBrand}, {@link #learnSlay}, {@link #learnCurse}, {@link #learnInnate},
 *       {@link #learnAllRunes} - and the {@code equipLearn*} family, which are the hooks that fire
 *       when worn gear is used in anger, all end in {@link #learnRune}. That is C's single
 *       choke point, and it is where the message, the knowledge update and the fan-out happen.</li>
 *   <li><b>Propagation</b> writes to the objects. {@link #knowObject} rewrites one object's known
 *       counterpart from the player's standing knowledge, and {@link #updateObjectKnowledge} runs it
 *       over everything in play, so that a rune learned on one sword shows up on every other object
 *       carrying it.</li>
 * </ul>
 *
 * <p><b>The wrappers are not decoration.</b> C keeps {@code player_learn_rune} file-static and
 * reaches it only through the public wrappers, each of which applies its own already-known guard and
 * resolves a brand, slay or curse to the rune that represents its whole group. A caller that reaches
 * past them skips both, and marks one member of a group where the game means to mark the class.
 *
 * <p>Between the two directions sit the questions - {@link #knowsRune}, {@link #knowsBrand},
 * {@link #knowsSlay}, {@link #knowsCurse}, {@link #knowsEgo}, {@link #nonCurseRunesKnown} - which
 * read the player's knowledge and change nothing. The {@code cursesFind*} methods are a different
 * kind of question again: they run backwards from an observed effect to the curse that could have
 * caused it, which is how a curse is noticed at all.
 *
 * <p>The methods are static and take the player, as C's take {@code struct player *p}: knowledge
 * belongs to a character, and there is no state here to hold. The class is a namespace.
 *
 * <p>Class PlayerKnowledge commented in full on 260901.
 *
 * @author Rowan Crowther
 */
public class PlayerKnowledge {
    private final static Logger logger = LogManager.getLogger(PlayerKnowledge.class);

    /**
     * Transfers what the player knows about object properties in general onto one particular object,
     * the port of C's {@code player_know_object} ({@code obj-knowledge.c:1018}).
     *
     * <p><b>The direction of travel is the thing to hold on to.</b> This does not look at the object
     * and work out what the player has learned; it looks at {@link Player#itemKnowledge} — the player's
     * standing knowledge of what each rune, modifier and element <em>means</em> — and rewrites the
     * object's known counterpart to show only the properties that knowledge entitles the player to
     * read. Learning happens elsewhere, in the {@code learnRune} family; this is the propagation step
     * that runs afterwards, over every object in play, so that a rune learned on one sword shows up
     * on every other object carrying it.
     *
     * <p>That is why nearly every assignment here is a multiplication or a gate rather than a copy.
     * {@link KnownObject}'s numeric fields are one-or-zero knowledge bits, so
     * {@code item.getDamageDice() * itemKnowledge.getDd()} yields the real dice when the player can
     * read dice and zero when they cannot — C's idiom, kept rather than rewritten as a conditional
     * because the zero is meaningful: it is what the display shows for an unknown quantity.
     *
     * <p><b>Three early returns, and they are not degrees of the same thing.</b> A null item or a
     * null counterpart is nothing to do. A kind mismatch between the object and its counterpart means
     * the player has the wrong idea about what the object even is — only sensed, not assessed — and
     * imposing property knowledge on that would be asserting detail about the wrong item. A distant
     * object that has not been {@code OBJ_NOTICE_ASSESSED} gets {@link #setBaseKnown} and no more:
     * the player can see a sword on the floor across the room and know it is a sword, without being
     * close enough to have formed a view about its enchantment.
     *
     * <p>The fourth return, after the flags, is the odd one. A curse holds its own bearer-less
     * {@link ItemObject} to carry the properties it confers, and that object has a null kind. It has
     * flags and modifiers worth knowing, but no ego, no flavour, no effect and nothing to become
     * aware of, so it stops there while real objects carry on.
     *
     * <p><b>Correctness is not yet established.</b> The audit of 260816 found divergences from C in
     * the combat-detail, modifier, element, flag, brand, curse and fully-known blocks; several of
     * them need accessors that do not exist yet. See
     * {@code docs/implementation/260816_functions_implemented.md} for the block-by-block comparison.
     * The blocks recorded there as matching C are the slays, the ego/jewellery/special-artifact
     * branch, the effect, and the guards and early returns described above.
     *
     * <p>Function knowObject coded before 260815 as a stub, implemented on 260816, commented in full
     * on 260816.
     *
     * @param player the player whose standing rune knowledge decides what the counterpart is
     *               allowed to show; nothing here is read off the object itself
     * @param item   the object whose known counterpart should be brought up to date; may be
     *               {@code null}, matching C's {@code if (!obj) return}
     */
    public static void knowObject(Player player, ItemObject item) {
        boolean seen = true;

        // unseen or only sensed items don't get any id
        if (item == null) return;
        if (item.getKnown() == null) return;
        ObjectKind itemKind = item.getKind();
        if (itemKind != item.getKnown().getKind()) return;

        ItemObject known = item.getKnown();

        // Distant objects
        if (itemKind != null && !(known.getNotice().has(ObjectNotice.OBJ_NOTICE_ASSESSED))) {
            setBaseKnown(player, item);
            return;
        }

        // Dice and pval for !chests
        known.setDamageDice(item.getDamageDice() * player.itemKnowledge.getDd());
        known.setDamageSides(item.getDamageSides() * player.itemKnowledge.getDs());
        known.setBaseAC(item.getBaseAC() * player.itemKnowledge.getAc());
        if (!item.gettValue().isChest())
            known.setpValue(item.getpValue());

        // combat details
        known.setToAC(item.getToAC() * player.itemKnowledge.getToA());
        if (!item.hasStandardToH())
            known.setToHit(item.getToHit() * player.itemKnowledge.getToH());
        known.setToDam(item.getToDam() * player.itemKnowledge.getToD());

        // modifiers
        Map<ObjectModifier, Integer> modifiers = item.getModifiers();
        Map<ObjectModifier, Integer> newModifiers = new HashMap<>();
        for (ObjectModifier modifier : ObjectModifier.values()) {
            newModifiers.put(modifier, 0);
        }
        for (ObjectModifier key : modifiers.keySet()) {
            if (player.itemKnowledge.modifierIsKnown(key))
                newModifiers.put(key, modifiers.get(key));
        }
        known.setModifiers(newModifiers);

        // Elements
        Map<ElementEnum, Boolean> knownElements = player.itemKnowledge.getElementResistInfo();
        Map<ElementEnum, ElementInfo> itemElInfo = item.getElInfo();
        Map<ElementEnum, ElementInfo> newElInfo = new HashMap<>(known.getElInfo());
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;

            ElementInfo zero = new ElementInfo();
            zero.setResLevel(0);
            newElInfo.put(element, zero);
        }
        for (ElementEnum key : knownElements.keySet()) {
            if (knownElements.get(key))
                newElInfo.put(key, itemElInfo.get(key).copy());
        }
        known.setElInfo(newElInfo);

        // ObjectFlags
        Flag<ObjectFlag> knownFlags = player.itemKnowledge.getFlags();
        FlagView<ObjectFlag> itemFlags = item.getFlags();
        knownFlags.inter(itemFlags);
        known.setFlagsTo(knownFlags);

        // Curse object structures are finished now
        if (itemKind == null)
            return;

        // Brands
        Set<Brand> brands = item.getBrands();
        if (brands == null) brands = new HashSet<>();
        Set<Brand> knownBrands = known.getBrands();
        if (knownBrands == null) knownBrands = new HashSet<>();
        Set<Brand> union = new HashSet<>(brands);
        union.addAll(knownBrands);

        boolean knownBrand = false;
        for (Brand brand : union) {
            if (knowsBrand(player, brand)) {
                known.addBrand(brand);
                knownBrand = true;
            } else {
                known.removeBrand(brand);
            }
        }

        if (!knownBrand && !known.getBrands().isEmpty()) {
            known.clearBrands();
        }


        // Slays
        Set<Slay> itemSlays = item.getSlays();
        if (itemSlays == null) itemSlays = new HashSet<>();
        Set<Slay> knownSlays = known.getSlays();
        if (knownSlays == null) knownSlays = new HashSet<>();
        Set<Slay> unionSlays = new HashSet<>(itemSlays);
        unionSlays.addAll(knownSlays);

        boolean knowSlay = false;

        for (Slay slay : unionSlays) {
            if (knowsSlay(player, slay)) {
                known.addSlay(slay);
                knowSlay = true;
            } else {
                known.removeSlay(slay);
            }
        }

        if (!knowSlay && !known.getSlays().isEmpty()) {
            known.clearSlays();
        }

        // Curses - be careful re alignment of knowledge
        Map<Curse, CurseData> itemCurses = item.getCurses();
        if (!itemCurses.isEmpty()) {
            boolean knownCursed = false;

            for (Curse curse : itemCurses.keySet()) {
                if (player.itemKnowledge.curseIsKnown(curse) && itemCurses.get(curse).getPower() != 0) {
                    knownCursed = true;
                    CurseData oldData = itemCurses.get(curse);
                    CurseData data = new CurseData(oldData.getPower(), 0);
                    known.addCurse(curse, data);
                } else if (known.getCurses().containsKey(curse)) {
                    known.removeCurse(curse);
                }
            }

            if (!knownCursed) {
                known.clearCurses();
            }
        } else if (!known.getCurses().isEmpty()) {
            known.clearCurses();
        }

        // ego type & jewellery type
        if (knowsEgo(player, item)) {
            seen = item.getEgo().isEverSeen();
            known.setEgo(item.getEgo());
        } else {
            known.setEgo(null);
        }

        if (item.gettValue().isJewellery()) {
            if (nonCurseRunesKnown(item)) {
                seen = (item.isArtifact() || itemKind.isEverseen());
                flavourAware(player, player.getCave(), player.getGear(), item);
            }
        } else if (itemKind.isSpecialArtifactKind()) {
            seen = true;
            flavourAware(player, player.getCave(), player.getGear(), item);
        }

        // Effect is known
        if ((itemKind.isAware() && itemKind.getFlavour() != null) ||
                (!item.gettValue().isWearable() && itemKind.getFlavour() == null) ||
                (item.gettValue().isWearable() && itemKind.getEffect() != null && itemKind.isAware())) {
            known.setEffect(item.getEffect());
        }

        // New stuff
        if (!seen) {
            String objectName;
            Flag<ObjectDescription> descriptionFlag = new Flag<>(ObjectDescription.class);

            if (ObjectUtils.isCarried(player, item)) {
                descriptionFlag.set(ObjectDescription.ODESC_PREFIX,
                        ObjectDescription.ODESC_COMBAT, ObjectDescription.ODESC_EXTRA);
                objectName = item.description(descriptionFlag, player);
                String msg = String.format("You have %s (%c)", objectName, ObjectUtils.gearToLabel(player, item));
                Message.message(msg);
            } else if (player.getCave() != null && player.getCave().getSquare(player.getGrid()).holdsObject(item)) {
                descriptionFlag.set(ObjectDescription.ODESC_PREFIX,
                        ObjectDescription.ODESC_COMBAT, ObjectDescription.ODESC_EXTRA);
                objectName = item.description(descriptionFlag, player);
                String msg = String.format("On the ground: %s.", objectName);
                Message.message(msg);
            }
        }

        // Fully known objects
        if (item.isFullyKnown()) {
            for (ElementEnum element : item.getElInfo().keySet()) {
                if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;

                ElementInfo eInfo = itemElInfo.get(element).copy();
                known.putElInfo(element, eInfo);
            }

            Flag<ObjectFlag> copy = new Flag<>(ObjectFlag.class);
            copy.copyFrom(item.getFlags());
            known.setFlagsTo(copy);
        }
    }

    /**
     * Reports whether every rune on an item except its curses has been learned, the port of C's
     * {@code object_non_curse_runes_known} ({@code obj-knowledge.c}).
     *
     * <p>Answered by comparing the item against its own known counterpart: a property the player can
     * read has been copied across, so anything the item has and the counterpart lacks is something
     * still unlearned. The combat bonuses must match exactly, while the modifiers, elements, brands,
     * slays and flags are one-way containments — the counterpart must cover the item, and is allowed
     * to carry more.
     *
     * <p>Curses are excluded because they are compared differently, by power rather than by
     * presence, and are handled by {@code ItemObject.cursesAreEqual} instead. {@code runesKnown}
     * calls both in turn, which is how C's {@code object_runes_known} is built.
     *
     * <p>Lives on the player rather than on the item, despite reading only the item, because it is
     * the counterpart to the rest of the knowledge code here and there was previously a second copy
     * of it on {@link ItemObject} that drifted from this one. {@code ItemObject.runesKnown}
     * delegates here so there is one implementation to keep right.
     *
     * <p>Iterates each map's own keys rather than the full enum, since an item records only the
     * modifiers and elements it actually carries; C can loop over fixed bounds because its arrays
     * have a slot for every one.
     *
     * <p>Function nonCurseRunesKnown coded before 260817, made public on 260817 when
     * {@code ItemObject}'s duplicate was folded into it, commented in full on 260817.
     *
     * @param item the item to test
     * @return {@code true} if every non-curse rune on the item has been learned
     */
    public static boolean nonCurseRunesKnown(ItemObject item) {
        if (item == null || item.getKnown() == null)
            return false;

        ItemObject knownItem = item.getKnown();

        // Combat details known
        if (knownItem.getToAC() != item.getToAC()) return false;
        if (knownItem.getToDam() != item.getToDam()) return false;
        if (knownItem.getToHit() != item.getToHit()) return false;

        // Modifiers
        Map<ObjectModifier, Integer> knownModifiers = knownItem.getModifiers();
        Map<ObjectModifier, Integer> itemModifiers = item.getModifiers();

        for (ObjectModifier key : itemModifiers.keySet()) {
            if (key == ObjectModifier.OM_MAX || key == ObjectModifier.OM_NONE) continue;
            if (!knownModifiers.containsKey(key)) return false;
            if (!Objects.equals(knownModifiers.get(key), itemModifiers.get(key))) return false;
        }

        // elements
        Map<ElementEnum, ElementInfo> knownEInfo = knownItem.getElInfo();
        Map<ElementEnum, ElementInfo> itemEInfo = item.getElInfo();

        for (ElementEnum key : itemEInfo.keySet()) {
            if (!knownEInfo.containsKey(key)) return false;
            if (itemEInfo.get(key).getResLevel() != 0 && knownEInfo.get(key).getResLevel() == 0) return false;
        }

        // Brands
        Set<Brand> itemBrands = item.getBrands();
        Set<Brand> knownBrands = knownItem.getBrands();

        if (!knownBrands.containsAll(itemBrands)) return false;

        // Slays
        Set<Slay> itemSlays = item.getSlays();
        Set<Slay> knownSlays = knownItem.getSlays();
        if (knownSlays == null) return false;
        if (!knownSlays.containsAll(itemSlays)) return false;

        // Flags
        Flag<ObjectFlag> knownFlags = knownItem.getFlags();
        Flag<ObjectFlag> itemFlags = item.getFlags();

        return knownFlags.isSubset(itemFlags);
    }

    /**
     * Copies onto an item's known counterpart everything that follows from simply recognising what
     * the item is, the port of C's {@code object_set_base_known} ({@code obj-knowledge.c}).
     *
     * <p>The division this draws is between what an item <em>is</em> and what has been done to it.
     * Knowing a weapon is a Long Sword settles its kind, its weight and its damage dice, because
     * every Long Sword shares them; it settles nothing about the enchantment on this particular one,
     * which still has to be learned rune by rune. So the kind-level facts are copied here and the
     * per-object ones are left to {@link PlayerKnowledge#knowObject}.
     *
     * <p>The dice, armour class and to-hit are copied only where the counterpart still holds
     * nothing, so that a figure already learned is never overwritten by the kind's generic one. Each
     * is multiplied by the corresponding 0/1 flag on {@link KnownObject}, which is how C masks a
     * property the player cannot yet read: an unknown armour class multiplies to zero rather than
     * being copied.
     *
     * <p>The effect is copied in two cases, and both are about whether using the item would have
     * taught it. A flavoured kind the player is aware of has been used before; an unflavoured
     * non-wearable — a scroll, a potion — announces what it does when read or drunk. A wearable's
     * activation follows the same rule through the kind's awareness.
     *
     * <p>Throws rather than returning quietly when there is no counterpart to write to, because a
     * carried object without one is a broken invariant rather than a case to handle: C asserts on
     * the same condition.
     *
     * <p>Function setBaseKnown coded before 260817, commented in full on 260817.
     *
     * @param player the player whose awareness of the item's kind decides how much of the
     *               kind-level detail may be copied across
     * @param item   the item whose known counterpart is being brought up to date
     * @throws RuntimeException if the item or its known counterpart is missing
     */
    public static void setBaseKnown(Player player, ItemObject item) {
        if (item == null || item.getKnown() == null) {
            logger.error("Item or item known nonexistent in PlayerKnowledge.setBaseKnown");
            throw new RuntimeException("Item or item known nonexistent in PlayerKnowledge.setBaseKnown");
        }

        ItemObject known = item.getKnown();
        known.setKind(item.getKind());
        known.settValue(item.gettValue());
        known.setsValue(item.getsValue());
        known.setWeight(item.getWeight());
        known.setNumber(item.getNumber());

        ObjectKind itemKind = item.getKind();

        // generic dice and ac/to_h for armour/launcher multipliers
        if (known.getDamageDice() == 0)
            known.setDamageDice(itemKind.getDamageDice() * player.itemKnowledge.getDd());
        if (known.getDamageSides() == 0)
            known.setDamageSides(itemKind.getDamageSides() * player.itemKnowledge.getDs());
        if (known.getBaseAC() == 0)
            known.setBaseAC(itemKind.getAc() * player.itemKnowledge.getAc());
        if (item.hasStandardToH())
            known.setToHit(itemKind.getToH().getBase());
        if (item.gettValue().isLauncher())
            known.setpValue(item.getpValue());

        // Aware flavours and unflavoured non-wearables
        if ((itemKind.isAware() && itemKind.getFlavour() != null)
                || (!item.gettValue().isWearable() && itemKind.getFlavour() == null)) {
            known.setpValue(item.getpValue());
            known.setEffect(item.getEffect());
        }

        // standard activations
        if (item.gettValue().isWearable() && itemKind.isAware() && itemKind.getEffect() != null)
            known.setEffect(item.getEffect());
    }

    /**
     * Marks an object's flavour as one the player has become aware of, and propagates the
     * consequences — the port of C's {@code object_flavor_aware} ({@code obj-knowledge.c:2262}).
     *
     * <p><b>Awareness is a property of the kind, not of the object.</b> Learning that the pink potion
     * is a Potion of Speed is learning it about every pink potion in the game at once, which is why
     * the flag is set on {@link ObjectKind} and why so much of this method is a sweep afterwards
     * putting the rest of the world in step. The object passed in is only the occasion for the
     * discovery, not its subject.
     *
     * <p>The early return on an already-aware kind is what makes the method safe to call freely —
     * {@link PlayerKnowledge#knowObject} calls it on every jewellery item whose non-curse runes are all known, which
     * is most of them once the player is experienced. Without it the floor sweep at the foot would
     * run on every pass.
     *
     * <p><b>The three consequences, in C's order.</b> First the effect becomes readable on this
     * object's counterpart, since an identified flavour is an identified effect. Then the ignore
     * settings are reconciled: a kind the player had set to ignore <em>while unaware</em> of it
     * becomes one to ignore now that they are aware, so the pile of unknown potions they were
     * stepping over does not suddenly reappear under a name. {@code PN_IGNORE} then asks for the
     * ignore pass to be re-run. Finally every object the player is carrying has its base knowledge
     * refreshed, because an aware flavour reveals pval and effect that {@link PlayerKnowledge#setBaseKnown}
     * withholds while the kind is unknown.
     *
     * <p>The floor sweep exists because some kinds change tile on awareness, so any square holding
     * an object of this kind needs redrawing. It starts at {@code (1,1)} rather than {@code (0,0)}:
     * the outermost ring of a level is permanent wall and can hold nothing.
     *
     * <p><b>Two pieces are knowingly absent.</b> C also refreshes store stock, which waits on
     * Chapter 8, and {@link uk.co.jackoftrades.middle.cave.Square#lightSpot} is currently an empty
     * stub deferred to Chapter 4, so the sweep computes the right set of squares and then redraws
     * none of them. Neither is a divergence in this method's own logic.
     *
     * <p>Function flavourAware coded on 260816, commented in full on 260816.
     *
     * @param player the player who has just become aware of the flavour, and whose ignore
     *               settings and carried objects are brought into step with it
     * @param cave   the level whose floor is swept for objects of the newly-aware kind, so that
     *               their squares can be redrawn; {@code null} skips the sweep, as it must
     *               during birth and on loading a save, when no level exists yet
     * @param gear   the objects the player is carrying, each of which has its base knowledge
     *               refreshed because an aware flavour reveals pval and effect that
     *               {@link PlayerKnowledge#setBaseKnown} withholds while the kind is unknown
     * @param item   an object of the kind the player has just become aware of
     */
    public static void flavourAware(Player player, Chunk cave, ArrayList<ItemObject> gear, ItemObject item) {
        ItemObject known = item.getKnown();
        if (known == null) return;
        ObjectKind kind = item.getKind();
        if (kind == null) return;

        if (kind.isAware()) return;
        kind.setAware(true);
        known.setEffect(item.getEffect());

        // Fix ignore/autoinscribe
        if (kind.isIgnoredUnaware())
            kind.setIgnoredAware(true);
        player.getPlayerUpkeep().orNoticeFlag(PlayerNotice.PN_IGNORE);

        // Update player objects
        for (ItemObject obj : gear) {
            setBaseKnown(player, obj);
        }

        // Store objects
        // STUB - Todo: Implement in chapter 8

        if (cave == null) return;

        for (int y = 1; y < cave.getHeight(); y++) {
            for (int x = 1; x < cave.getWidth(); x++) {
                boolean light = false;
                Loc grid = Loc.row(y).col(x);

                Iterator<ItemObject> iterator = cave.getSquare(grid).getObjectPile().getIterator();

                while (iterator.hasNext()) {
                    ItemObject floorObj = iterator.next();
                    if (floorObj.getKind() == kind) {
                        light = true;
                        break;
                    }
                }
                if (light) cave.getSquare(grid).lightSpot();
            }
        }
    }

    /**
     * Reports whether the player could recognise an item's ego type from the properties they can
     * already read, the port of C's {@code player_knows_ego} ({@code obj-knowledge.c}).
     *
     * <p>An ego is not learned directly; it is deduced. Every flag, modifier, resistance, brand,
     * slay and curse an ego always grants must be a rune the player can read, because an ego is
     * only identifiable once nothing it confers is still a mystery. So this walks the ego's
     * properties and asks the player's knowledge about each.
     *
     * <p>The modifier test is the subtle one. An ego's modifier is a range rolled per item, so a
     * range spanning zero can leave an item showing nothing at all — and an item showing nothing
     * gives the player nothing to have failed to notice. That is why an unreadable modifier only
     * disqualifies the ego when the range cannot produce zero ({@code modmax * modmin > 0}) or when
     * this particular item did roll a non-zero value. The ranges are evaluated at both extremes at
     * maximum depth, following C.
     *
     * <p>The item is a parameter rather than the ego alone for exactly that test: C accepts a null
     * object and skips the concession when it has no specific item to consult.
     *
     * <p>Function knowsEgo coded before 260817, commented in full on 260817.
     *
     * @param player the player whose rune knowledge each of the ego's properties is tested
     *               against
     * @param item   the item whose ego is being tested
     * @return {@code true} if the ego is one the player could now identify, {@code false} for an
     * item with no ego at all
     */
    public static boolean knowsEgo(Player player, ItemObject item) {
        EgoItem ego = item.getEgo();

        if (ego == null) return false;

        Flag<ObjectFlag> knownFlags = player.itemKnowledge.getFlags();
        Flag<ObjectFlag> egoFlags = ego.getFlags();

        // All flags known
        if (!knownFlags.isSubset(egoFlags)) return false;

        // Modifiers all known
        for (ObjectModifier modifier : ObjectModifier.values()) {
            if (modifier == ObjectModifier.OM_NONE || modifier == ObjectModifier.OM_MAX) continue;

            Random egoModifier = ego.getModifier(modifier);
            if (egoModifier == null) continue;

            int modMax = egoModifier.randCalc(GameConstants.getWorldMaxDepth(), DamageAspect.MAXIMIZE);
            int modMin = egoModifier.randCalc(GameConstants.getWorldMaxDepth(), DamageAspect.MINIMIZE);

            if ((modMax > 0 || modMin < 0) && !player.itemKnowledge.modifierIsKnown(modifier))
                if (modMax * modMin > 0 || item.getModifiers().getOrDefault(modifier, 0) != 0)
                    return false;
        }

        // all elements known
        Map<ElementEnum, ElementInfo> egoElInfo = ego.getElInfo();
        Map<ElementEnum, Boolean> itemElInfo = player.itemKnowledge.getElementResistInfo();

        for (ElementEnum key : egoElInfo.keySet()) {
            if (key == ElementEnum.ELEM_MAX || key == ElementEnum.ELEM_NONE) continue;
            ElementInfo egoInfo = egoElInfo.get(key);
            if (egoInfo.getResLevel() != 0 && !itemElInfo.get(key))
                return false;
        }

        // All brands known
        Set<Brand> egoBrands = ego.getBrands();
        for (Brand brand : egoBrands) {
            if (!knowsBrand(player, brand)) return false;
        }

        // All slays known
        Set<Slay> egoSlays = ego.getSlays();
        for (Slay slay : egoSlays) {
            if (!knowsSlay(player, slay)) return false;
        }

        // All curses known
        for (Curse curse : ego.getCurses().keySet()) {
            if (!knowsCurse(player, curse)) return false;
        }

        return true;
    }

    /**
     * Records that the player has learned the identity of a curse, typically because its effect
     * just fired on a worn item. The port of C's {@code player_learn_curse}
     * ({@code src/obj-knowledge.c}).
     *
     * <p>C resolves the curse to a rune by name — {@code rune_index(RUNE_VAR_CURSE,
     * lookup_curse(curse->name))} — rather than by identity, which is why
     * {@link Rune#runeIndex(Curse)} matches on the name too. A curse reconstructed from a savefile
     * or built by a test is then still recognised.
     *
     * <p>A curse with no rune yields null here, where C's guard is {@code index >= 0}; the null is
     * handled inside {@link PlayerKnowledge#learnRune}, so the two guards sit in different places but reject the
     * same case. The knowledge update stays outside that guard in both, running even when the
     * lookup found nothing.
     *
     * @param player the player who has just had the curse's nature revealed to them
     * @param curse  the curse whose nature has now been revealed
     */
    public static void learnCurse(Player player, Curse curse) {
        Rune rune = Rune.runeIndex(curse);
        learnRune(player, rune, true);
        updateObjectKnowledge(player);
    }

    /**
     * Records that the player has learned to recognise a brand, typically because they just saw it
     * fire in combat. The port of C's {@code player_learn_brand}.
     *
     * <p>One of the wrapper functions that {@link PlayerKnowledge#learnRune} exists to serve, and it shows the
     * shape they all take: guard on already-knowing, resolve the property to its rune, learn the
     * rune. The resolution step is the one that cannot be skipped — a brand belongs to a group of
     * same-named brands sharing a single rune, and {@link Rune#runeIndex(Brand)} returns the rune
     * for the group rather than for the particular strength passed in. Propagating the new
     * knowledge is not this method's job; {@link PlayerKnowledge#learnRune} has done it by the time it returns.
     *
     * <p>C's {@code player_learn_brand} closes with a second
     * {@code update_player_object_knowledge}, which this port deliberately drops. It cannot do
     * anything: the guard above means the rune is unknown whenever the call is reached — knowledge
     * of a brand and of its rune move together, since {@link KnownObject#learnBrand} marks every
     * same-named brand at once — so {@link PlayerKnowledge#learnRune} always learns, and always updates. The
     * duplicate is boilerplate copied from {@code player_learn_flag}, which has no guard and so is
     * the one wrapper where the trailing call can be the only one that runs. Even there it changes
     * nothing, because it recomputes identical values.
     *
     * @param player the player who has just seen the brand fire
     * @param brand  any brand of the wanted kind, at any strength
     */
    public static void learnBrand(Player player, Brand brand) {
        if (!knowsBrand(player, brand)) {
            Rune rune = Rune.runeIndex(brand);

            learnRune(player, rune, true);
        }
    }

    /**
     * Records that the player has learned to recognise a slay, typically because they just saw it
     * bite. The port of C's {@code player_learn_slay}, and the sibling of {@link #learnBrand}.
     *
     * <p>Same three steps — guard on already-knowing, resolve the property to its rune, learn the
     * rune — but the equivalence the resolution walks is a different one.
     * {@link Rune#runeIndex(Slay)} groups by {@link Slay#sameMonsterSlain}, following C's
     * {@code same_monsters_slain}, and <em>not</em> by name as brands do. The distinction is real:
     * two slays can share the name "evil" and kill different monsters, because one carries a
     * monster base and the other does not. Grouping those together would teach the player a rune
     * they have seen no evidence for.
     *
     * <p>As with {@link #learnBrand}, C's trailing {@code update_player_object_knowledge} is
     * dropped — the guard means {@link PlayerKnowledge#learnRune} always learns, and so always updates.
     *
     * @param player the player who has just seen the slay bite
     * @param slay   any slay of the wanted kind, at any strength
     */
    public static void learnSlay(Player player, Slay slay) {
        if (!knowsSlay(player, slay)) {
            Rune rune = Rune.runeIndex(slay);
            learnRune(player, rune, true);
        }
    }

    /**
     * The port of C's {@code player_knows_brand}. Note that this asks about the exact brand given,
     * not its group — which is the same thing in practice, because learning any member of a group
     * marks all of them (see {@link KnownObject#learnBrand}).
     *
     * @param player the player whose knowledge is being asked about
     * @param brand  the brand to ask about
     * @return true if the player recognises this brand
     */
    public static boolean knowsBrand(Player player, Brand brand) {
        return player.itemKnowledge.brandIsKnown(brand);
    }

    /**
     * Records that the player has learned to recognise an object flag. The port of C's
     * {@code player_learn_flag}, whose one caller is the failed uncursing that leaves an item
     * {@code OF_FRAGILE} ({@code effect-handler-general.c:203}).
     *
     * <p>Flags need no group resolution — each has its own rune, so unlike {@link PlayerKnowledge#learnBrand} and
     * {@link PlayerKnowledge#learnSlay} there is no equivalence class for {@link Rune#runeIndex(ObjectFlag)} to
     * find. The lookup can still answer {@code null}, because not every flag is a learnable
     * property: {@code init_rune} skips the placeholder subtypes, the ones describing the object
     * rather than the player, and the curse-only ones. {@link #learnRune} logs that and returns,
     * where C hands {@code rune_index}'s {@code -1} straight to {@code rune_list[-1]}.
     *
     * <p><b>The already-known guard is this port's, not C's.</b> C's version is unguarded, and
     * relies on the flag arm of {@code player_learn_rune} using {@code of_on}, which reports
     * whether it changed anything — so a flag learned twice is silently not announced twice. The
     * guard here changes no answer (it is the same test one call deeper) and buys consistency with
     * the other wrappers. It also makes C's trailing {@code update_player_object_knowledge}
     * unreachable, which matters only in that this was the single wrapper where that call could
     * have been the one that ran; it recomputed identical values, so nothing is lost.
     *
     * @param player the player to whom the flag has just been shown
     * @param flag   the flag now readable
     */
    public static void learnFlag(Player player, @NotNull ObjectFlag flag) {
        if (player.itemKnowledge.flagIsKnown(flag)) return;

        learnRune(player, Rune.runeIndex(flag), true);
    }

    /**
     * Whether the player can read a rune. The port of C's {@code player_knows_rune}
     * ({@code obj-knowledge.c:257-306}), and the mirror image of {@link #learnRune}: the same seven
     * varieties, each asking {@link Player#itemKnowledge} the question the corresponding {@code learn}
     * arm answers.
     *
     * <p>This is the method that decided {@link KnownObject}'s shape. C's version is a seven-armed
     * switch in which every arm reads one field of {@code p->obj_k}, so between them the arms
     * enumerate everything a knowledge object has to hold. A port of {@code obj_k} is the right
     * size exactly when it can serve all seven with nothing left over — which is why the twelve
     * fields, and not a whole {@code struct object}, are enough.
     *
     * <p>Two arms are worth reading against C rather than taken on trust. The curse arm is
     * {@code p->obj_k->curses[index].power == 1}, where {@code power} is a severity everywhere else
     * in the game but a 0/1 flag on the knowledge side — {@code save.c:661} writes it as
     * {@code power ? 1 : 0} — so {@link KnownObject#curseIsKnown} answering from a boolean loses
     * nothing. The combat arm splits three ways on {@link CombatRunes} where C compares
     * {@code r->index} against three constants, and its {@code COMBAT_RUNE_MAX} case is the
     * sentinel, which is a data error rather than an answer; it is logged and reported unknown.
     *
     * <p>No {@code default}: the switch is over the sealed {@link RuneVariety}, so the compiler
     * proves the seven are covered. An eighth variety would be a compile error here, which is the
     * point — a {@code default} would answer {@code false} for it and say nothing.
     *
     * @param player the player whose {@link Player#itemKnowledge} answers the question
     * @param rune   the rune to ask about
     * @return true if the player can read this rune
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static boolean knowsRune(Player player, @NotNull Rune rune) {
        boolean known;

        switch (rune.getVariety()) {
            case RuneVariety.CombatKey(CombatRunes key) -> known = switch (key) {
                case COMBAT_RUNE_TO_A -> player.itemKnowledge.toAIsKnown();
                case COMBAT_RUNE_TO_D -> player.itemKnowledge.toDIsKnown();
                case COMBAT_RUNE_TO_H -> player.itemKnowledge.toHIsKnown();
                case COMBAT_RUNE_MAX -> {
                    logger.warn("Combat Rune MAX encountered.");
                    yield false;
                }
            };

            case RuneVariety.BrandKey(Brand key) -> known = player.itemKnowledge.brandIsKnown(key);
            case RuneVariety.FlagKey(ObjectFlag key, _) -> known = player.itemKnowledge.flagIsKnown(key);
            case RuneVariety.CurseKey(Curse key) -> known = player.itemKnowledge.curseIsKnown(key);
            case RuneVariety.ModKey(ObjectModifier key, _) -> known = player.itemKnowledge.modifierIsKnown(key);
            case RuneVariety.ResistKey(ElementEnum key, _) -> known = player.itemKnowledge.resistanceIsKnown(key);
            case RuneVariety.SlayKey(Slay key) -> known = player.itemKnowledge.slayIsKnown(key);
        }

        return known;
    }

    /**
     * The port of C's {@code player_knows_slay}, a bare array lookup. As with
     * {@link PlayerKnowledge#knowsBrand}, it asks about the exact slay given rather than its group, and gets the
     * same answer either way: {@link KnownObject#learnSlay} marks every slay that kills the same
     * monsters, so the cost of grouping is paid once on the learning side and this stays cheap.
     *
     * @param player the player whose knowledge is being asked about
     * @param slay   the slay to ask about
     * @return true if the player recognises this slay
     */
    public static boolean knowsSlay(Player player, @NotNull Slay slay) {
        return player.itemKnowledge.slayIsKnown(slay);
    }

    /**
     * The port of C's {@code player_knows_curse}, which reads
     * {@code p->obj_k->curses[index].power == 1}.
     *
     * <p>That {@code power} is not a severity. On a real object it is one — 1 to 99 from
     * {@code apply_curse}, deciding how strong a removal spell must be, with 100 and above meaning
     * permanent — but on the knowledge side it only ever holds 0 or 1, because C types
     * {@code p->obj_k} as a whole {@code struct object} and inherits {@code struct curse_data}
     * whether it wants two integers or not. {@code player_learn_rune} writes a literal 1 and
     * {@code save.c:661} normalises with {@code power ? 1 : 0}. So the port keeps a boolean, and
     * the {@code == 1} has nothing to test.
     *
     * <p>The two meanings meet in {@code player_know_object} ({@code obj-knowledge.c:1131}), where
     * this answer <em>gates</em> the real severity: a recognised curse shows its true power on the
     * known copy of an object, an unrecognised one reads as zero. That is why the curse-removal
     * menu can offer only what the player has learned.
     *
     * <p>Curses are never grouped, so unlike brands and slays there is no fan-out behind this.
     *
     * @param player the player whose knowledge is being asked about
     * @param curse  the curse to ask about
     * @return true if the player recognises this curse
     */
    public static boolean knowsCurse(Player player, @NotNull Curse curse) {
        return player.itemKnowledge.curseIsKnown(curse);
    }

    /**
     * Learns a single rune: marks the property it names as readable, announces it if anything was
     * genuinely new, and updates everything the player can now see. The port of C's
     * {@code player_learn_rune} ({@code src/obj-knowledge.c}), and the one place object knowledge
     * is added.
     *
     * <p><b>This is an internal choke point, not an entry point.</b> C keeps it file-{@code static}
     * and routes every caller through a wrapper — {@code player_learn_flag},
     * {@code player_learn_slay}, {@code player_learn_brand}, {@code player_learn_curse}, the
     * {@code equip_learn_*} family. The wrappers are not decoration. Each resolves its property to
     * a rune through the matching {@link Rune#runeIndex} overload, and for brands, slays and
     * curses that lookup returns the rune for an <em>equivalence class</em> rather than for the
     * exact object handed in. Code that reaches past a wrapper and builds its own {@link Rune}
     * skips that resolution, and learns one member of a group where the game means all of them.
     * Prefer {@link PlayerKnowledge#learnBrand} and its siblings; add new learning paths as further wrappers.
     *
     * <p><b>Package-private, and that is the whole of the enforcement.</b> C's {@code static} means
     * nothing outside {@code obj-knowledge.c} can call it; the package is this port's equivalent, so
     * every wrapper and every {@code object_curses_find_*} helper belongs in
     * {@code middle.player} beside it. The rule has been broken once already — the curse-finding
     * family briefly lived on {@link ItemObject}, which forced this method public for as long as it
     * did. If a future learning path seems to want an object-side home
     * ({@code item.learnOnWield(player)} rather than {@code player.learnOnWield(item)}), that is the
     * same mistake wearing different clothes. Knowledge is player state, the item is only the thing
     * being read, and C's argument order says so.
     *
     * <p>Package-private rather than {@code private} because {@code PlayerRuneLearningTest} shares
     * the package and drives this directly, to exercise each of the seven variety arms in isolation.
     *
     * <p><b>A wrapper does not need to call {@link #updateObjectKnowledge(Player)} ()}.</b> This method
     * leaves object knowledge propagated on every path that learned anything, and that is the
     * invariant the rest of the system is written against: most of C's callers — the
     * {@code equip_learn_*} family, {@code object_learn_on_wield},
     * {@code object_learn_unknown_rune}, {@code missile_learn_on_ranged_attack}, the
     * {@code object_curses_find_*} family, {@code player_learn_all_runes} — have no update call of
     * their own and rely entirely on this one. Only four of C's wrappers add a second, and it is
     * redundant in each (see {@link PlayerKnowledge#learnBrand}); this port omits it rather than copy it.
     *
     * <p>The switch is over a sealed interface, so the seven varieties are matched as record
     * patterns and the compiler proves the set is covered — no {@code default} arm, and no cast to
     * get at each variety's key. C reaches the same seven cases through a {@code switch} on
     * {@code r->variety} followed by an {@code int} index whose meaning changes per case, and
     * closes with a {@code default: learned = false} it cannot show to be unreachable.
     *
     * <p>Only the combat arm can fall through without learning, on the {@code COMBAT_RUNE_MAX}
     * sentinel; C's chain of {@code if}/{@code else if} does the same silently, and the warning
     * here is a Java-side addition for a case that should not arise.
     *
     * <p>The tail order matters and is C's: nothing learned means no message and no update, so a
     * property learned twice is announced once.
     *
     * @param player       the player learning the rune, and whose object knowledge is
     *                     propagated afterwards on every path that learned anything
     * @param rune         the rune to learn; null is logged and ignored, standing in for C's
     *                     {@code assert} on the rune index
     *                     <p>Function learnRune coded before 260815, commented in full before 260815, narrowed to
     *                     package-private on 260815, briefly public while the curse-finding family lived on
     *                     {@link ItemObject}, and narrowed again on 260815 when that family moved here.
     * @param printMessage whether to announce the discovery, false for the paths that learn in
     *                     bulk and would otherwise bury the player in messages
     */
    public static void learnRune(Player player, Rune rune, boolean printMessage) {
        if (rune == null) {
            logger.warn("Rune is null on entering learnRune");
            return;
        }

        boolean learned = false;

        switch (rune.getVariety()) {
            case RuneVariety.CombatKey(CombatRunes key) -> {
                switch (key) {
                    case COMBAT_RUNE_TO_A -> learned = player.itemKnowledge.learnToA();

                    case COMBAT_RUNE_TO_H -> learned = player.itemKnowledge.learnToH();

                    case COMBAT_RUNE_TO_D -> learned = player.itemKnowledge.learnToD();

                    case COMBAT_RUNE_MAX -> logger.warn("Combat Rune MAX encountered.");
                }
            }
            case RuneVariety.ModKey(ObjectModifier key, _) -> learned = player.itemKnowledge.learnModifier(key);

            case RuneVariety.ResistKey(ElementEnum key, _) -> learned = player.itemKnowledge.learnResistance(key);

            case RuneVariety.BrandKey(Brand key) -> learned = player.itemKnowledge.learnBrand(key);

            case RuneVariety.SlayKey(Slay key) -> learned = player.itemKnowledge.learnSlay(key);

            case RuneVariety.CurseKey(Curse key) -> learned = player.itemKnowledge.learnCurse(key);

            case RuneVariety.FlagKey(ObjectFlag key, _) -> learned = player.itemKnowledge.learnFlag(key);
        }

        if (!learned) return;

        if (printMessage)
            Message.messageType(MessageType.MSG_RUNE, "You have learned the rune of "
                    + rune.getVariety().runeName() + ".");

        updateObjectKnowledge(player);
    }

    /**
     * Re-derives the known copy of every object the player could be looking at, now that a rune
     * has been learned. The port of C's {@code update_player_object_knowledge}, which runs
     * {@code player_know_object} over four populations — the objects on the level, the player's
     * gear, every store's stock, and the objects hanging off the curse definitions — then
     * autoinscribes the ground and the pack and signals the inventory and equipment events.
     *
     * <p>Stores and curse objects are in that list for a reason worth keeping: knowledge is a
     * property of the player rather than of the item, so learning a rune changes how a sword in a
     * shop reads without the player ever having touched it.
     *
     * <p>The work is a recomputation rather than a step, so calling this twice in a row is
     * harmless — which is why C's habit of calling it again in the learning wrappers went
     * unnoticed. It is not free, though: each call sweeps four populations and signals two events,
     * so the port calls it once, from {@link PlayerKnowledge#learnRune}.
     *
     * <p><b>One of the four populations are live.</b> The level and the pack are walked; stores is 
     * not, and isn't a matter of writing the loop:
     *
     * <ul>
     *   <li><b>Stores</b> wait on the shop subsystem, Chapter 8.</li>
     *   <li><b>Autoinscribe</b> of ground and pack waits on Chapter 4.</li>
     * </ul>
     *
     * <p><b>The guards are not symmetrical, and only one of them is C's.</b> {@code if (cave)} is
     * real and load-bearing — knowledge is updated during birth and on loading a save, before any
     * level exists. The null test on the gear has no counterpart: C walks {@code p->gear} as a linked
     * list, where a null head is simply an empty loop, while a null {@link ArrayList} would
     * throw. That guard is the port paying for the container change, not copying anything.
     *
     * <p><b>The two signals sit outside every guard</b>, so they fire even when nothing was walked.
     * That is C's placement and it is right: the display has to redraw on the strength of the rune
     * just learned, whether or not any object currently in play happens to carry it.
     *
     * <p>The real work is delegated to {@link PlayerKnowledge#knowObject}, which was written on 260816. What this
     * method is responsible for is the shape around it: the populations, their order, the guards and
     * the signals. See {@code PlayerUpdateObjectKnowledgeTest}, which observes the walk rather than
     * its outcome — deliberately, so that it stays valid however {@code knowObject} changes.
     *
     * <p>Function updateObjectKnowledge coded before 260815 as a stub, implemented as far as the
     * available subsystems allow on 260815, commented in full on 260815. Stub note on
     * {@code knowObject} corrected on 260816.
     *
     * @param player the player whose knowledge has just changed, and whose level and gear are
     *               re-derived from it
     */
    public static void updateObjectKnowledge(Player player) {
        // Know the cave objects
        if (player.getCave() != null) {
            for (ItemObject itemObject : player.getCave().getObjects()) {
                knowObject(player, itemObject);
            }
        }

        // Know the player objects
        if (player.getGear() != null) {
            for (ItemObject itemObject : player.getGear()) {
                knowObject(player, itemObject);
            }
        }

        // Store objects
        // TODO: Implement this branch in chapter 8

        // Curse objects
        for (Curse curse : ObjectRegistry.getCurses()) {
            knowObject(player, curse);
        }

        // Inscription
        // TODO: Implement this branch in chapter 4

        EventsHandler eventsBusHandler = GameEngine.getEventsBusHandler();
        eventsBusHandler.eventSignal(GameEventType.EVENT_INVENTORY);
        eventsBusHandler.eventSignal(GameEventType.EVENT_EQUIPMENT);
    }

    /**
     * Transfers what the player knows about object properties in general onto one curse definition —
     * the curse half of C's {@code player_know_object} ({@code obj-knowledge.c:1032}), which the
     * port has to split into a second method because a curse is no longer an object.
     *
     * <p><b>Why there is an overload at all.</b> C hangs a curse's properties on a bearer-less
     * {@code struct object} with a null {@code kind} ({@code curses[i].obj}) and feeds it to the
     * same function as a real sword; the null kind is what makes it take the short path.
     * {@link Curse} flattens those properties onto itself instead — the shape recorded on that
     * class — so there is no {@link ItemObject} to hand to
     * {@link #knowObject(Player, ItemObject)}, and the short path becomes a method of its own. The
     * TODO this discharges is the one described at {@link #updateObjectKnowledge}: the curse
     * population had nowhere to put its answer until {@link Curse} grew the {@code known*} fields.
     *
     * <p><b>The direction of travel is the same as the object version's.</b> Nothing is read off
     * the curse to find out what the player has learned; {@link Player#itemKnowledge} — the standing
     * knowledge of what each rune, modifier and element means — decides what the curse is allowed to
     * show, and the {@code known*} fields are rewritten to match. Learning happens in the
     * {@code learnRune} family; this is the propagation afterwards, which is why it runs over every
     * curse in {@link ObjectRegistry} on each rune learned rather than over the curses on some
     * particular item.
     *
     * <p><b>What C's short path omits, and this omits with it.</b> There are no early returns: a
     * curse always exists, always has its known fields, has no kind to mismatch and is never a
     * distant object, so the three guards at the head of {@link #knowObject(Player, ItemObject)}
     * have nothing to guard. The dice/sides/base-AC/pval block goes too — a curse has none of them.
     * What is left is C's four blocks in C's order: combat details, modifiers, elements, flags.
     *
     * <p>Within those four, two details differ from the object version and both follow from the
     * flattening:
     *
     * <ul>
     *   <li><b>Elements are read with {@code getOrDefault}.</b> C indexes a dense
     *       {@code el_info[ELEM_MAX]} where an unmentioned element is simply zero;
     *       {@link Curse#getElInfo()} holds only the elements the curse's data lines name, so an
     *       element the player can read but the curse never mentions has to be defaulted rather than
     *       fetched. Same treatment as {@link #objectCursesFindElement}.</li>
     *   <li><b>The new element map starts empty</b> rather than from the existing known one, because
     *       every non-sentinel element is written on the way through — first to a zeroed
     *       {@link ElementInfo}, then to a copy of the real one where the player can read it.
     *       {@link ElementEnum#ELEM_NONE} and {@link ElementEnum#ELEM_MAX} are skipped as
     *       sentinels.</li>
     * </ul>
     *
     * <p>The flag intersection looks as though it damages the player's own knowledge — {@code inter}
     * mutates its receiver — but {@link KnownObject#getFlags()} hands back a fresh copy, so what is
     * narrowed is a throwaway.
     *
     * <p><b>Outstanding: the last two steps have no counterpart in C.</b> C returns at "Curse object
     * structures are finished now", immediately after the flags — before the effect assignment and
     * before the fully-known block — so a curse object never reaches either, and
     * {@code object_fully_known} is never handed one from anywhere else in the original. This method
     * carries on into both. The effect assignment is unconditional here, where C's is gated on the
     * kind's awareness and flavour, neither of which a curse has; the consequence is that
     * {@link Curse#isFullyKnown()}'s effect test always passes. Whether the port wants these two
     * steps at all is the open question — see {@link Curse#isFullyKnown()} and
     * {@link Curse#hasStandardToH()}, where the one behavioural difference they produce is set out.
     *
     * <p>Function knowObject(Player, Curse) coded before 260901, commented in full on 260901.
     *
     * @param player the player whose standing rune knowledge decides what the curse is allowed to
     *               show; nothing here is read off the curse to decide it
     * @param curse  the curse definition whose {@code known*} fields should be brought up to date
     */
    private static void knowObject(Player player, Curse curse) {
        // combat details
        if (player.itemKnowledge != null) {
            curse.setKnownCombatToAC(curse.getCombatAC() * player.itemKnowledge.getToA());
            if (!curse.hasStandardToH())
                curse.setKnownCombatToHit(curse.getCombatToHit() * player.itemKnowledge.getToH());
            curse.setKnownCombatToDam(curse.getCombatDam() * player.itemKnowledge.getToD());
        }

        // modifiers
        Map<ObjectModifier, Integer> modifiers = curse.getModifiers();
        Map<ObjectModifier, Integer> newModifiers = new HashMap<>();
        for (ObjectModifier modifier : ObjectModifier.values()) {
            newModifiers.put(modifier, 0);
        }
        for (ObjectModifier key : modifiers.keySet()) {
            if (player.itemKnowledge != null && player.itemKnowledge.modifierIsKnown(key))
                newModifiers.put(key, modifiers.get(key));
        }
        curse.setKnownModifiers(newModifiers);

        // Elements
        Map<ElementEnum, Boolean> knownElements = player.itemKnowledge == null ?
                new HashMap<>() : player.itemKnowledge.getElementResistInfo();
        Map<ElementEnum, ElementInfo> itemElInfo = curse.getElInfo();
        Map<ElementEnum, ElementInfo> newElInfo = new HashMap<>();
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;

            ElementInfo zero = new ElementInfo();
            zero.setResLevel(0);
            newElInfo.put(element, zero);
        }
        for (ElementEnum key : knownElements.keySet()) {
            if (knownElements.get(key))
                newElInfo.put(key, itemElInfo.getOrDefault(key, new ElementInfo()).copy());
        }
        curse.setKnownElInfo(newElInfo);

        // ObjectFlags
        Flag<ObjectFlag> knownFlags = player.itemKnowledge != null ? player.itemKnowledge.getFlags()
                : new Flag<>(ObjectFlag.class);
        FlagView<ObjectFlag> itemFlags = curse.getObjectFlags();
        knownFlags.inter(itemFlags);
        curse.setKnownObjectFlags(knownFlags);

        curse.setKnownEffect(curse.getEffect());

        // Fully known objects
        if (curse.isFullyKnown()) {
            for (ElementEnum element : curse.getElInfo().keySet()) {
                if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;

                ElementInfo eInfo = itemElInfo.get(element).copy();
                curse.putKnownElementInfo(element, eInfo);
            }

            Flag<ObjectFlag> copy = new Flag<>(ObjectFlag.class);
            copy.copyFrom(curse.getObjectFlags());
            curse.setKnownObjectFlags(copy);
        }
    }

    /**
     * Learns every rune the player's race knows from birth — the elements it resists or is
     * vulnerable to, and the object flags it carries innately. The port of C's
     * {@code player_learn_innate}.
     *
     * <p>A character does not have to find a ring of free action to know what free action feels
     * like when it is part of their body; the point of this pass is that a race's own properties
     * are legible to it from the start, and so are the runes naming them.
     *
     * <p><b>Both loops learn silently.</b> {@link PlayerKnowledge#learnRune} is called with {@code printMessage}
     * false, because this runs at birth and a dwarf does not want a message telling them they have
     * noticed they are a dwarf. That is C's choice too, and the reason {@link PlayerKnowledge#learnRune} takes the
     * flag at all.
     *
     * <p>The element loop skips {@link ElementEnum#ELEM_NONE} and {@link ElementEnum#ELEM_MAX},
     * which are sentinels rather than elements; C has no equivalent of the former and excludes the
     * latter by bounding at {@code ELEM_MAX}. Elements above the highest one carrying a resistance
     * rune answer {@code null} from {@link Rune#runeIndex(ElementEnum)}, which {@link PlayerKnowledge#learnRune}
     * logs and ignores — C would index {@code rune_list[-1]}, so this is a place the port is
     * deliberately safer rather than merely different.
     *
     * <p>The flag loop walks all of {@link ObjectFlag} and asks the race about each, where C walks
     * only the bits actually set, with {@code of_next}. Same set reached, more iterations.
     *
     * <p>C closes with {@code update_player_object_knowledge}, dropped here as in the other
     * wrappers. The reasoning differs slightly: there is no guard to make it unreachable, but each
     * {@link PlayerKnowledge#learnRune} that learned anything has already updated, and if the race knows nothing
     * innately then C's call recomputes a knowledge state that never changed.
     *
     * @param player the player whose race's own resistances and flags are made readable, along
     *               with the runes naming them
     */
    public static void learnInnate(Player player) {
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX)
                continue;

            if (player.getRace().getResistKnowledge(element)) {
                Rune resistRune = Rune.runeIndex(element);
                learnRune(player, resistRune, false);
            }
        }

        for (ObjectFlag flag : ObjectFlag.values()) {
            if (player.getRace().getObjectFlagKnowledge(flag)) {
                Rune rune = Rune.runeIndex(flag);
                learnRune(player, rune, false);
            }
        }
    }

    /**
     * Learns every rune in the game at once. The port of C's {@code player_learn_all_runes}, which
     * is not part of normal play — it is what the debug command and the cheat option call, and what
     * a winner's character gets so the final dump shows everything.
     *
     * <p>C counts to {@code rune_max}; the loop here is over the rune list itself
     * ({@link ObjectRegistry#getRunes}), which is the same set in the same order, that count being
     * only the list's length.
     *
     * <p><b>Silent.</b> {@link PlayerKnowledge#learnRune} is called with {@code printMessage} false for the obvious
     * reason: announcing several hundred discoveries one at a time is not a message, it is a wall.
     * Same reasoning as {@link #learnInnate}, and the second reason the flag exists.
     *
     * <p>Learning is left to run per rune rather than short-circuited, so anything already known
     * falls out at {@link PlayerKnowledge#learnRune}'s own guard and the trailing
     * {@link #updateObjectKnowledge} fires once per rune actually learned.
     *
     * @param player the player handed the whole rune list at once
     */
    public static void learnAllRunes(Player player) {
        for (Rune rune : ObjectRegistry.getRunes()) {
            learnRune(player, rune, false);
        }
    }

    /**
     * Learns the to-AC rune from whatever the player is wearing, on the occasion of being
     * attacked. The port of C's {@code equip_learn_on_defend} ({@code obj-knowledge.c:1970}), the
     * first of the {@code equip_learn_*} family and the model for the rest.
     *
     * <p>The premise is that a property announces itself when it does its job. A blow that lands
     * less heavily than it should have is evidence that something is adding to the armour class,
     * and a blow is the only thing that can produce that evidence — which is why armour is learned
     * by being hit rather than by being examined.
     *
     * <p><b>Three sources are checked, and the first success ends the method.</b> The leading guard
     * and the one at the foot of the loop are the same test: once
     * {@link KnownObject#toAIsKnown} answers true there is nothing further to learn, so the walk
     * stops rather than announcing the same rune from every remaining slot. That early return is
     * also what makes the shape at the end reachable only for an unhelmeted, unarmoured player.
     *
     * <ol>
     *   <li>each equipped item's own bonus, via {@link ItemObject#getToAC} tested against zero —
     *       the faithful port of C's plain {@code if (obj->to_a)}, which is available because the
     *       item carries the figure it rolled rather than the dice it rolled from;</li>
     *   <li>each equipped item's curses, via {@link #cursesFindToA}, which learns the
     *       curse's rune as well as the to-AC one;</li>
     *   <li>the player's assumed shape, whose {@link PlayerShape#getToAc} is a flat parsed
     *       {@code int} — a bear's hide is a to-AC bonus like any other.</li>
     * </ol>
     *
     * <p>An empty slot is skipped, standing in for C's {@code if (obj)} around the whole body:
     * {@code slot_object} answers NULL for a slot with nothing in it, which is most of them for most
     * characters. C's {@code assert(obj->known)} has no counterpart here — it is a debug-build check
     * that the known counterpart was attached, never a condition on learning, and folding it into
     * the test above would quietly skip items instead of failing loudly. See
     * {@link ItemObject#isKnown} for why that reading of the name is a trap.
     *
     * <p>The shape branch drops C's {@code lookup_player_shape(p->shape->name)}, which re-fetches by
     * name the definition {@code p->shape} already points at.
     *
     * <p>Function equipLearnOnDefend coded before 260815, commented in full before 260815, updated on
     * 260815 when the item's own bonus arm stopped being a stub.
     *
     * @param player the player who has just been struck, and whose equipped items, their curses
     *               and assumed shape are searched for a to-AC bonus
     */
    public static void equipLearnOnDefend(Player player) {
        if (player.itemKnowledge.toAIsKnown()) return;

        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            ItemObject slotObject = slot.getItem();
            if (slotObject == null) continue;
            if (slotObject.getToAC() != 0) {
                learnRune(player, Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_A), true);
            }
            cursesFindToA(player, slotObject);
            if (player.itemKnowledge.toAIsKnown()) return;
        }
        if (player.getShape() != null) {
            if (player.getShape().getToAc() != 0) {
                learnRune(player, Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_A), true);
            }
        }
    }

    /**
     * Learns the to-hit rune from whatever the player is wearing, on the occasion of loosing a
     * missile. The port of C's {@code equip_learn_on_ranged_attack} ({@code obj-knowledge.c:2003}).
     *
     * <p>Same premise as {@link #equipLearnOnDefend}, applied to accuracy: a shot that flies truer
     * than the archer had any right to expect is evidence that something is helping, and only
     * shooting can produce that evidence. Only to-hit is learned here — a missile's damage is the
     * launcher's and the ammunition's business, so a ranged attack says nothing about to-damage.
     *
     * <p><b>Two slots are skipped, and this is the reason the method exists separately from
     * {@link #equipLearnOnMeleeAttack}.</b> C skips {@code slot_by_name(p, "weapon")} and
     * {@code slot_by_name(p, "shooting")}; {@code body.txt} pairs those names one-to-one with the
     * slot types ({@code slot:WEAPON:weapon}, {@code slot:BOW:shooting}), so the port compares
     * {@link EquipSlot#getType} and needs no lookup by name. The melee weapon is skipped because a
     * sword hanging at the belt cannot have helped the shot; the launcher is skipped because its
     * contribution cannot be told apart from the archer's own skill.
     *
     * <p>Otherwise the shape is {@link #equipLearnOnDefend}'s: an empty slot is skipped, each
     * surviving item is asked about its own bonus and then about its curses via
     * {@link #cursesFindToH}, the walk stops at the first success because
     * {@link KnownObject#toHIsKnown} has nothing left to gain, and the shape branch at the end is
     * therefore reachable only by a player carrying nothing that could teach it.
     *
     * <p>The item's own bonus goes through {@link ItemObject#hasStandardToH} rather than a non-zero
     * test on the figure, exactly as {@link #equipLearnOnMeleeAttack} does — C calls the same
     * predicate from both. Body armour carries a to-hit penalty as standard equipment, so a plain
     * {@code getToHit() != 0} would have every archer in a hauberk learning the rune from their
     * armour.
     *
     * <p>Function equipLearnOnRangedAttack coded on 260815, commented in full on 260815,
     * updated on 260815 to test the predicate the right way round.
     *
     * @param player the player who has just loosed a shot, and whose equipped items, their
     *               curses and assumed shape are searched for a to-hit bonus
     */
    public static void equipLearnOnRangedAttack(Player player) {
        if (player.itemKnowledge.toHIsKnown()) return;

        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            ItemObject slotObject = slot.getItem();
            if (slotObject == null || slot.getType() == EquipmentSlotsEnum.EQUIP_WEAPON
                    || slot.getType() == EquipmentSlotsEnum.EQUIP_BOW) continue;
            if (!slotObject.hasStandardToH()) {
                learnRune(player, Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H), true);
            }
            cursesFindToH(player, slotObject);
            if (player.itemKnowledge.toHIsKnown()) return;
        }
        if (player.getShape() != null) {
            if (player.getShape().getToHit() != 0) {
                learnRune(player, Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H), true);
            }
        }
    }

    /**
     * Learns the to-hit and to-damage runes from whatever the player is wearing, on the occasion
     * of striking a blow. The port of C's {@code equip_learn_on_melee_attack}
     * ({@code obj-knowledge.c:2039}), the largest of the {@code equip_learn_*} family because it is
     * the only one that pursues two runes at once.
     *
     * <p>That pairing is what makes the method's guards different in kind from its siblings'. Both
     * the leading test and the one at the foot of the loop are conjunctions: there is nothing left
     * to learn only when {@link KnownObject#toHIsKnown} <em>and</em>
     * {@link KnownObject#toDIsKnown} are both satisfied, so a player who has already worked out
     * their weapon's damage keeps walking the remaining slots in the hope of learning accuracy from
     * their gloves. Getting either guard down to a single term would end the walk early and quietly
     * lose the other rune.
     *
     * <p><b>One slot is skipped.</b> C skips {@code slot_by_name(p, "shooting")} and nothing else —
     * a bow is no part of a sword-stroke, but the weapon very much is, which is precisely the slot
     * {@link #equipLearnOnRangedAttack} has to leave alone. As there, the port compares
     * {@link EquipSlot#getType} rather than looking the slot up by name.
     *
     * <p><b>The two tests are not symmetrical.</b> To-damage is a plain non-zero check on
     * {@link ItemObject#getToDam}, matching C's {@code if (obj->to_d)}; to-hit goes through
     * {@link ItemObject#hasStandardToH}, because body armour carries a to-hit penalty as standard
     * equipment and testing it against zero would teach the rune to anyone who wore a hauberk. The
     * curse pair {@link #cursesFindToH} and {@link #cursesFindToD} is then asked
     * for both, and each learns the offending curse's own rune alongside the combat one.
     *
     * <p>The shape branch tests {@link PlayerShape#getToHit} and {@link PlayerShape#getToDam}
     * independently rather than as alternatives, since a shape may well grant both.
     *
     * <p>Function equipLearnOnMeleeAttack coded on 260815, commented in full on 260815.
     *
     * @param player the player who has just landed a blow, and whose equipped items, their
     *               curses and assumed shape are searched for to-hit and to-damage bonuses
     */
    public static void equipLearnOnMeleeAttack(Player player) {
        if (player.itemKnowledge.toDIsKnown() && player.itemKnowledge.toHIsKnown()) return;

        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            ItemObject slotObject = slot.getItem();
            if (slotObject == null || slot.getType() == EquipmentSlotsEnum.EQUIP_BOW) continue;
            if (!slotObject.hasStandardToH())
                learnRune(player, Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H), true);
            if (slotObject.getToDam() != 0)
                learnRune(player, Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_D), true);

            cursesFindToD(player, slotObject);
            cursesFindToH(player, slotObject);
            if (player.itemKnowledge.toDIsKnown() && player.itemKnowledge.toHIsKnown()) return;
        }
        if (player.getShape() != null) {
            if (player.getShape().getToDam() != 0) {
                learnRune(player, Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_D), true);
            }
            if (player.getShape().getToHit() != 0) {
                learnRune(player, Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H), true);
            }
        }
    }

    /**
     * Learns one named object flag from whatever the player is wearing, on the occasion of that
     * flag having just done something. The port of C's {@code equip_learn_flag}
     * ({@code obj-knowledge.c:2084}), and the busiest member of the family — upstream calls it from
     * some thirty places, each naming the flag its own event could have revealed: {@code OF_AFRAID}
     * on failing to attack, {@code OF_FEATHER} on a fall, {@code OF_HOLD_LIFE} on a drain,
     * {@code OF_TRAP_IMMUNE} on a trap that did not fire.
     *
     * <p><b>Unlike its siblings, this one does not stop early.</b> The {@code equip_learn_on_*}
     * methods return the moment their rune is known, because a second slot cannot teach the same
     * thing twice. Here the walk always runs to the end of the body, and the reason is the
     * {@code else} branch: every slot has bookkeeping to do whether or not anything was learned, so
     * there is nothing to be saved by leaving early.
     *
     * <p><b>Three things happen per slot, and the first two are alternatives.</b>
     *
     * <ol>
     *   <li><b>The item has the flag.</b> If the player cannot yet read it, the flag announces
     *       itself — {@link ItemObject#description} names the item, {@link ItemObject#flagMessage}
     *       delivers the property's own wording, and the rune is learned. The inner
     *       {@link KnownObject#flagIsKnown} guard is what keeps a player wearing three items with
     *       the same flag from being told about it three times.</li>
     *   <li><b>The item does not have the flag.</b> Then its absence is itself worth recording, but
     *       only while there is anything left to learn about the item: an item that is not yet
     *       {@link ItemObject#isFullyKnown} gets the flag switched on in its known set, marking that
     *       it has had its chance to display the property and did not. This is how an item is
     *       identified by being used rather than examined — enough events rule out enough
     *       properties, and what remains is the item.</li>
     *   <li><b>Either way, the curses are asked.</b> The flag may be riding on a curse rather than
     *       on the item, which is a different question from both of the above, so
     *       {@link #cursesFindFlags} runs unconditionally. It takes a set rather than a
     *       single flag because its other callers pass real masks; the one-element set built here is
     *       C's {@code f}, assembled at the top of {@code equip_learn_flag} for exactly this
     *       purpose.</li>
     * </ol>
     *
     * <p>The leading guard is C's {@code if (!flag) return;}. C's flag is an index into the flag
     * table and its zero is {@link ObjectFlag#OF_NONE}, so the enum equivalent has to name that
     * sentinel rather than test for null — {@link ObjectFlag#OF_MAX} is rejected on the same
     * grounds, being the other end-marker and no more a real flag than the first.
     *
     * <p><b>Outstanding:</b> {@link ItemObject#description} is still a stub, deferred to Chapter 7,
     * so both this method's message and the one {@link #cursesFindFlags} sends name the item with a
     * placeholder.
     *
     * <p>Function equipLearnFlag coded on 260815, commented in full on 260815, updated on 260815
     * once the curse arm stopped being a stub.
     *
     * @param player the player whose equipped items and their curses are searched for the flag
     * @param flag   the flag whose moment this is; ignored if null or a sentinel
     */
    public static void equipLearnFlag(Player player, ObjectFlag flag) {
        if (flag == null || flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX) return;
        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            ItemObject slotObject = slot.getItem();
            if (slotObject == null) continue;
            if (slotObject.hasFlag(flag)) {
                if (!player.itemKnowledge.flagIsKnown(flag)) {
                    Flag<ObjectDescription> descriptionMode = new Flag<>(ObjectDescription.class);
                    descriptionMode.on(ObjectDescription.ODESC_BASE);

                    String objDesc = slotObject.description(descriptionMode, player);
                    slotObject.flagMessage(flag, objDesc);
                    learnRune(player, Rune.runeIndex(flag), true);
                }
            } else if (!slotObject.isFullyKnown() && slotObject.getKnown() != null) {
                slotObject.getKnown().setFlag(flag);
            }

            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            flags.on(flag);

            cursesFindFlags(player, slotObject, flags);
        }
    }

    /**
     * Learns the to-AC rune, and the curse's own rune, if any curse on the given item contributes an
     * armour-class change the player has just felt. The port of C's
     * {@code object_curses_find_to_a} ({@code obj-knowledge.c:1557}), the first of six near-identical
     * functions covering to-AC, to-hit, to-damage, flags, modifiers and elements.
     *
     * <p>A curse is a thing the player learns by being bitten by it, which is why this is reached
     * from {@link #equipLearnOnDefend} rather than from anything to do with inspecting the item. Two
     * runes are learned, not one: the fact that <em>something</em> is altering the armour class, and
     * the identity of the curse doing it.
     *
     * <p><b>Why the family lives here and not on {@link ItemObject}.</b> All six are {@code static}
     * in {@code obj-knowledge.c}, the same translation unit as {@code player_learn_rune} — they are
     * not object methods in C but player-side helpers that take an object, and the signature says so:
     * {@code (struct player *p, struct object *obj)}. C's file boundary is this port's package
     * boundary, so putting them here is what keeps {@link PlayerKnowledge#learnRune} package-private and lets the
     * compiler refuse any caller that reaches past a wrapper. They read the item entirely through its
     * public surface.
     *
     * <p><b>Where the numbers come from.</b> The armour-class figure belongs to the curse
     * definition, not to the item — {@link Curse#getCombatAC}, the port of {@code curses[i].obj->to_a},
     * parsed once from {@code curse.txt}. What the item holds is the instance data: the power and
     * timeout in {@link CurseData}. C keeps those in two arrays indexed alike, so every one of these
     * functions has to walk {@code 1 .. curse_max} and read {@code obj->curses[i].power} and
     * {@code curses[i].obj->to_a} at the same subscript. {@link ItemObject#getCurses} pairs them
     * directly, mapping each curse to its own {@link CurseData}, so the loop visits only the curses
     * the item actually carries and no index arithmetic survives the port.
     *
     * <p>That also disposes of C's two guards. {@code !obj->curses[i].power} is what stops a dense
     * array from reporting curses the item does not have, and is unnecessary against a map that only
     * contains the ones it does — the port removes a curse outright rather than zeroing it, so an
     * entry of power zero should not arise. The test is kept as a cheap restatement of that
     * invariant. {@code !curses[i].obj} is dead code upstream: the parser allocates that object at
     * the {@code name:} line, so the only null in the array is index 0, the reserved no-curse slot
     * the loop already skips.
     *
     * <p>The rune is resolved once, before the loop. C recomputes it into the same {@code index}
     * variable it then overwrites with the curse's rune, so on a second qualifying curse it relearns
     * the previous curse instead of the to-AC rune — harmless there only because the to-AC rune is
     * already known by that point. Hoisting the lookup out makes the bug unexpressible.
     *
     * <p>Function cursesFindToA coded before 260815, commented in full before 260815, moved here
     * from {@link ItemObject} on 260815 and its arguments turned round to C's order.
     *
     * @param player the player doing the learning, and to whom any discovery is announced
     * @param item   the item whose curses are being read
     */
    public static void cursesFindToA(Player player, ItemObject item) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_A);
        if (!item.getCurses().isEmpty()) {
            for (Curse curse : item.getCurses().keySet()) {
                CurseData value = item.getCurses().get(curse);
                if (value.getPower() != 0)
                    if (curse.getCombatAC() != 0) {
                        // Learn the to AC rune
                        learnRune(player, rune, true);
                        // Learn the to AC Curse rune
                        learnRune(player, Rune.runeIndex(curse), true);
                    }
            }
        }
    }

    /**
     * Learns the to-damage rune, and the curse's own rune, if any curse on the given item
     * contributes a damage change the player has just dealt. The port of C's
     * {@code object_curses_find_to_d} ({@code obj-knowledge.c:1603}), the to-damage sibling of
     * {@link #cursesFindToA}.
     *
     * <p>Structurally identical to that method, and the reasoning there applies unchanged: why the
     * family lives on {@link Player} rather than {@link ItemObject}, why the figure is read from the
     * curse definition ({@link Curse#getCombatDam}, C's {@code curses[i].obj->to_d}) rather than from
     * the item, why the power test survives, and why the rune is resolved once above the loop.
     *
     * <p>What differs is the occasion. This is reached from {@link #equipLearnOnMeleeAttack} — a
     * curse that saps damage announces itself when a blow lands softly, not when one is taken.
     *
     * <p>Function cursesFindToD coded on 260815, commented in full on 260815, moved here from
     * {@link ItemObject} on 260815 and its arguments turned round to C's order, {@code testFlags}
     * widened to {@link FlagView} on 260818.
     *
     * @param player the player doing the learning, and to whom any discovery is announced
     * @param item   the item whose curses are being read
     */
    static void cursesFindToD(Player player, ItemObject item) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_D);
        if (!item.getCurses().isEmpty()) {
            for (Curse curse : item.getCurses().keySet()) {
                if (item.getCurses().get(curse).getPower() != 0)
                    if (curse.getCombatDam() != 0) {
                        // Learn the to-damage rune
                        learnRune(player, rune, true);
                        // Learn the rune of the curse that caused it
                        learnRune(player, Rune.runeIndex(curse), true);
                    }
            }
        }
    }

    /**
     * Learns the to-hit rune, and the curse's own rune, if any curse on the given item contributes
     * an accuracy change the player has just felt. The port of C's {@code object_curses_find_to_h}
     * ({@code obj-knowledge.c:1580}), the to-hit sibling of {@link #cursesFindToA}.
     *
     * <p>Structurally identical to that method — see it for why the family lives here, why the
     * figure is read from the curse definition ({@link Curse#getCombatToHit}, C's
     * {@code curses[i].obj->to_h}), why the power test is kept, and why the rune is hoisted above
     * the loop.
     *
     * <p>This is the one of the three reached from both attack methods,
     * {@link #equipLearnOnMeleeAttack} and {@link #equipLearnOnRangedAttack}: a curse that spoils
     * the player's aim shows itself whichever way they attack.
     *
     * <p>Note that the curse's contribution is judged by a plain non-zero test, with no counterpart
     * to {@link ItemObject#hasStandardToH}. That asymmetry is correct: "standard" is a fact about
     * what a kind of item normally carries, and a curse has no normal to-hit to be measured against.
     *
     * <p>Function cursesFindToH coded on 260815, commented in full on 260815, moved here from
     * {@link ItemObject} on 260815 and its arguments turned round to C's order, {@code testFlags}
     * widened to {@link FlagView} on 260818.
     *
     * @param player the player doing the learning, and to whom any discovery is announced
     * @param item   the item whose curses are being read
     */
    static void cursesFindToH(Player player, ItemObject item) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H);
        if (!item.getCurses().isEmpty()) {
            for (Curse curse : item.getCurses().keySet()) {
                if (item.getCurses().get(curse).getPower() != 0)
                    if (curse.getCombatToHit() != 0) {
                        // Learn the to-hit rune
                        learnRune(player, rune, true);
                        // Learn the rune of the curse that caused it
                        learnRune(player, Rune.runeIndex(curse), true);
                    }
            }
        }
    }

    /**
     * Learns any of the given flags that a curse on the given item has just betrayed, together with
     * the rune of the curse betraying them — the port of C's {@code object_curses_find_flags}
     * ({@code obj-knowledge.c:1634}), the flag member of the same family as {@link #cursesFindToA}
     * and its two siblings.
     *
     * <p><b>Why this one takes a set where the others take nothing.</b> The to-AC, to-hit and
     * to-damage finders each pursue a single fixed property, so the caller has nothing to say. Flags
     * are a population, and the caller decides which of them this occasion could plausibly have
     * revealed. C passes that as a {@code bitflag *test_flags} and intersects it with the curse's
     * own flags, keeping only what is in both. Three call sites, three different sets: a one-element
     * set built on the spot by {@code equip_learn_flag}, the {@code obvious_mask} of everything a
     * wield could show, and the {@code timed_mask} of what only prolonged wear reveals.
     *
     * <p>As in the sibling finders, two runes are learned per hit and not one — the flag itself, and
     * the identity of the curse that carries it. The curse's rune is learned whether or not the flag
     * was new, since meeting a curse is knowledge even when its effect was already understood.
     *
     * <p><b>The intersection is taken on a copy, and it has to be.</b> {@link Flag#inter} is
     * {@code retainAll} — it mutates the set it is called on. The flags being intersected belong to
     * the {@link Curse} definition parsed once from {@code curse.txt} and shared by every item
     * carrying that curse, so intersecting them in place would permanently delete from the
     * definition every flag this one occasion happened not to be asking about.
     * {@link Flag#set(List)} copies element by element into a fresh set, which is what
     * keeps the definition intact. The caller's own set is left alone for the same reason:
     * {@link #equipLearnFlag} builds one and hands it to every slot in turn.
     *
     * <p><b>The curse's rune is learned inside the flag loop, not beside it.</b> That is C's
     * placement and it is load-bearing in one direction: a curse whose flags do not meet the test
     * set teaches nothing at all, not even its own existence, because the player has had no
     * evidence of it. It also means a curse matching two flags learns its rune twice, which the
     * guard inside {@link PlayerKnowledge#learnRune} makes harmless.
     *
     * <p><b>The message is conditional where the learning is not.</b> C wraps only
     * {@code flag_message} in {@code p->upkeep->playing}, so knowledge is recorded during character
     * generation and loading but nothing is announced into a game that has not started. The
     * returned {@code boolean} is C's {@code new} — true if any flag was learned that was not
     * already known, ignored by this caller and used by the wield-time learning.
     *
     * <p>The per-curse guard is on {@link CurseData#getPower}, as in the three sibling finders and
     * as C's {@code if (!obj->curses[i].power)} requires. Power is what says the curse is on the
     * item at all — {@link CurseData#setPower} with a zero is how a curse is removed, so a zeroed
     * entry can outlive the curse it names. C's second guard, {@code !curses[i].obj}, has no
     * counterpart: it exists to skip the reserved index 0 of a dense array, and a map holding only
     * the curses this item carries has no such hole.
     *
     * <p><b>Outstanding:</b> {@link ItemObject#description} is still a stub, deferred to Chapter 7,
     * so the message names the item with a placeholder.
     *
     * <p>Function cursesFindFlags coded on 260815, commented in full on 260815, moved here from
     * {@link ItemObject} on 260815 and its arguments turned round to C's order, {@code testFlags}
     * widened to {@link FlagView} on 260818.
     *
     * @param player    the player doing the learning, and to whom any discovery is announced
     *                  once play has started
     * @param item      the item whose curses are being read
     * @param testFlags the flags this occasion could have revealed, C's {@code test_flags}; read
     *                  only, hence the {@link FlagView} — it is intersected into a working copy
     *                  rather than modified
     * @return whether any flag was learned that the player did not already know
     */
    static boolean cursesFindFlags(Player player, ItemObject item, FlagView<ObjectFlag> testFlags) {
        boolean curseLearned = false;

        Flag<ObjectDescription> baseDesc = new Flag<>(ObjectDescription.class);
        baseDesc.on(ObjectDescription.ODESC_BASE);
        String name = item.description(baseDesc, player);

        if (item.getCurses().isEmpty()) return false;

        // Only loop through the curses on the object, not the entire set of curses
        for (Curse curse : item.getCurses().keySet()) {
            CurseData value = item.getCurses().get(curse);
            if (value.getPower() == 0) continue;

            Flag<ObjectFlag> toTest = new Flag<>(ObjectFlag.class);
            toTest.union(curse.getObjectFlags());
            toTest.inter(testFlags);

            for (ObjectFlag testSubject : toTest) {
                if (!player.itemKnowledge.flagIsKnown(testSubject)) {
                    curseLearned = true;
                    learnRune(player, Rune.runeIndex(testSubject), true);
                    if (player.getPlayerUpkeep().isPlaying())
                        item.flagMessage(testSubject, name);
                }

                // Learn the curse
                Rune rune = Rune.runeIndex(curse);
                if (rune != null)
                    learnRune(player, rune, true);
            }
        }

        return curseLearned;
    }

    /**
     * Learns the elemental resistances carried by the player's wielded items — the port of C's
     * {@code equip_learn_element} ({@code src/obj-knowledge.c:2155}).
     *
     * <p>Called whenever something would have shown the player how well they resist an element: a
     * breath weapon landing, a timed resistance running out, a light or dark attack. Every equipped
     * item is asked whether it moves the resistance level for that element. One that does announces
     * itself as glowing and teaches the player the resistance rune; one that does not has the fact
     * recorded on its known counterpart, so the item is remembered as having had its chance to show
     * the property and stays silent about it thereafter. Either way the item's curses are searched
     * too, because a curse carries element figures of its own.
     *
     * <p>The two sentinels stand in for C's {@code element < 0 || element >= ELEM_MAX} bounds check.
     * {@link ElementEnum} declares {@code ELEM_MAX} in the same position C does — after
     * {@code ELEM_ARROW} — so the pair of comparisons admits exactly the elements C's pair admits,
     * the unresistable damage types among them.
     *
     * <p>C asserts that each equipped item has a known counterpart; the port skips an item without
     * one instead. The assert is a debug-build check rather than a behavioural clause, and the same
     * treatment appears elsewhere in this class.
     *
     * <p>C reads its element figures out of a fixed {@code el_info[ELEM_MAX]} array, so an element
     * the object's data line never mentioned still reads back as a zero resistance level and an
     * empty flag set. The port holds only the elements an item actually names, so both reads are
     * guarded by presence: an absent element takes the same branch C's zero takes, and the flag copy
     * is skipped because the known counterpart's flag set is already the empty one C would have
     * copied. The resistance level is written either way, which is the half that matters.
     *
     * <p>Where the element has no resistance rune, {@link Rune#runeIndex(ElementEnum)} answers
     * {@code null} and {@link PlayerKnowledge#learnRune} declines it, in place of C's {@code -1} index — the same
     * treatment recorded at {@link #objectCursesFindElement}.
     *
     * <p>Function equipLearnElement commented in full on 260831.
     *
     * @param player the player who has just been given the chance to notice the element, and
     *               whose equipped items and their curses are searched for it
     * @param elem   the element the player has just been given a chance to notice
     */
    public static void equipLearnElement(Player player, ElementEnum elem) {
        if (elem == ElementEnum.ELEM_NONE || elem == ElementEnum.ELEM_MAX)
            return;

        if (player.itemKnowledge.getElementResistInfo().get(elem))
            return;

        // All wielded items are eligible
        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            ItemObject item = slot.getItem();
            if (item == null) continue;
            if (item.getKnown() == null) continue;

            // Does the object affect the player's resistance to the element?
            if (item.getElInfo().containsKey(elem) && item.getElInfo().get(elem).getResLevel() != 0) {
                String name = ObjectUtils.objectDesc(item,
                        new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_BASE), player);

                // Message
                Message.message("Your %s glows.", name);

                // Learn the element properties
                learnRune(player, Rune.runeIndex(elem), true);
            } else if (!item.isFullyKnown()) {
                // Objects not fully known yet get marked as having had a chance to display
                // the element
                item.getKnown().setElInfoResLevel(elem, 1);
                ElementInfo info = item.getKnown().getElInfo().get(elem);
                info.getFlags().copyFrom(item.getElInfo().getOrDefault(elem, new ElementInfo()).getFlags());
            }
            // Element may be on a curse
            objectCursesFindElement(player, item, elem);
        }
    }

    /**
     * Learns what a curse on an item teaches about one element — the port of C's
     * {@code object_curses_find_element} ({@code src/obj-knowledge.c:1748}).
     *
     * <p>An item's own element figures are not the only thing that can change how the player resists
     * an element: a curse merged onto the item carries element figures of its own. This walks the
     * item's curses and, for every one actually in force — power non-zero — asks whether it moves
     * the resistance level for {@code elem}. If it does, the player learns the resistance rune
     * (announced once, on first discovery, as the item glowing) and the curse's own rune, and the
     * method reports back that the element was found on a curse.
     *
     * <p>C skips curse slot 0 and any curse whose definition carries no object; neither has an
     * analogue here. The port holds an item's curses as a map of the curses it actually has, so
     * there is no empty slot to step over, and {@link Curse} flattens C's nested
     * {@code curse-&gt;obj} into fields of its own, so a curse cannot be missing one.
     *
     * <p>The null check on the element entry is where the two data shapes part company. C indexes an
     * array of length {@code ELEM_MAX}, so every element always has a {@code res_level}, defaulting
     * to zero; {@link Curse#getElInfo()} holds only the elements that curse's data lines name, and
     * most of the curses in {@code curse.txt} name none at all. A missing entry therefore means what
     * C's zero means — this curse does not touch that element — and is passed over rather than
     * treated as a fault.
     *
     * <p>C's {@code if (index >= 0)} guard before learning the curse rune is absent for the reason
     * it is absent elsewhere in this class: {@link Rune#runeIndex(Curse)} answers {@code null} where
     * C answers {@code -1}, and {@link PlayerKnowledge#learnRune} already declines a null rune. The resistance rune
     * is learned unguarded in both, which is C's own choice rather than an omission here.
     *
     * <p>The description is built once, before the loop, exactly as C builds its {@code o_name}. The
     * cost is paid whether or not anything is found, but it means the message names the item as it
     * read on entry rather than as the first rune learned this call left it.
     *
     * <p>Function objectCursesFindElement commented in full on 260831.
     *
     * @param player the player doing the learning, and to whom any discovery is announced
     * @param item   the item whose curses to search
     * @param elem   the element being learned about
     * @return whether the element appeared on any curse this item carries
     */
    private static boolean objectCursesFindElement(Player player, ItemObject item, ElementEnum elem) {
        boolean newCurse = false;

        Flag<ObjectDescription> flags = new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_BASE);
        String name = item.description(flags, player);

        for (Curse curse : item.getCurses().keySet()) {
            CurseData curseData = item.getCurses().get(curse);

            if (curseData.getPower() == 0)
                continue;

            // Does the object affect the player's resistance to the element?
            if (curse.getElInfo().get(elem) != null && curse.getElInfo().get(elem).getResLevel() != 0) {
                // Learn the element property if we don't know it already
                if (!player.itemKnowledge.getElementResistInfo().get(elem)) {
                    Message.message("Your %s glows.", name);

                    learnRune(player, Rune.runeIndex(elem), true);
                }

                // Learn the curse
                learnRune(player, Rune.runeIndex(curse), true);
                newCurse = true;
            }
        }

        return newCurse;
    }
}
