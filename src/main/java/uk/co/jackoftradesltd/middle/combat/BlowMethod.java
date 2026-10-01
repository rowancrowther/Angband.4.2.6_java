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

package uk.co.jackoftradesltd.middle.combat;

import uk.co.jackoftradesltd.middle.enums.MessageType;

import java.util.List;

/**
 * The definition of a monster attack "method" (as loaded from
 * {@code blow_methods.txt}) — how a blow is delivered (hit, bite, claw, …),
 * whether it can cut/stun/miss/is physical, and the flavour messages used to
 * describe it. This is the Java port of the C original's {@code struct blow_method},
 * declared in {@code mon-blows.h} and filled by the {@code parse_meth_*} handlers in
 * {@code mon-init.c}.
 * <p>
 * It is an immutable value: C keeps the methods in a linked list ({@code next}) with
 * {@code num_messages} counting the action strings; here the owning collection replaces
 * the chain and {@link #getBlowMessage()}{@code .size()} replaces the counter. Monster
 * blows refer to a method by name, so the lookup that C does with {@code findmeth()}
 * is done against that collection rather than by pointer.
 * <p>
 * The flags are read by {@code mon-attack.c} ({@code cut}/{@code stun} gate the side
 * effects of a landed blow, {@code miss} gates the "misses you" message) and by
 * {@code mon-blows.c} ({@code phys} gates armour reduction, {@code msgt} and the action
 * strings build the blow message).
 *
 * <p>Class coded before 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
public class BlowMethod {
    /**
     * The method's name as written on the {@code name:} line (e.g. "HIT", "BITE"); the
     * key a monster's blow uses to find its method. Port of {@code blow_method.name}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private String name;
    /**
     * Whether a landed blow of this method may cut the target (the {@code cut:} line;
     * any non-zero value is true, as in C). Port of {@code blow_method.cut}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private boolean cut;
    /**
     * Whether a landed blow of this method may stun the target (the {@code stun:} line;
     * any non-zero value is true). Port of {@code blow_method.stun}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private boolean stun;
    /**
     * Whether a miss with this method is reported to the player (the {@code miss:} line;
     * any non-zero value is true). Despite the name it is not an accuracy flag: C only
     * consults it, together with monster visibility, when deciding to print the
     * "misses you" message. Port of {@code blow_method.miss}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private boolean miss;
    /**
     * Whether this method is a physical attack (the {@code phys:} line; any non-zero
     * value is true). C uses it in {@code mon-blows.c} to decide whether the target's
     * armour reduces the damage. Port of {@code blow_method.phys}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private boolean phys;
    /**
     * The message type this blow is reported with. C stores an {@code int} index
     * ({@code msgt}) resolved by {@code message_lookup_by_name}, zero
     * ({@code MSG_GENERIC}) when the optional {@code msg:} line is absent; the enum
     * replaces the index.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private MessageType mesgT;
    /**
     * The alternative action strings for a landed blow, one per {@code act:} line.
     * Replaces C's {@code messages} linked list and {@code num_messages} count. C
     * prepends as it parses, so its list runs in reverse file order; this one keeps file
     * order. That is immaterial because {@code monster_blow_method_action()} picks a
     * uniformly random entry, but a seeded-RNG comparison against C would land on a
     * different string for the same roll.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private List<String> blowMessage;
    /**
     * The short phrase naming this method in monster lore (the {@code desc:} line).
     * C appends repeated {@code desc:} lines with {@code string_append}; the shipped
     * {@code blow_methods.txt} has exactly one per method, so no concatenation arises.
     * Port of {@code blow_method.desc}.
     *
     * <p>Field coded before 260930, commented in full on 260930.
     */
    private String desc;

    /**
     * Build a blow method from its parsed data-file fields. Called by
     * {@code BlowMethodAssembler}, which has already applied C's truthiness rule to the
     * flags and resolved the message type; the list is stored as given, not copied.
     * Stands in for the allocation in {@code parse_meth_name} plus the later
     * {@code parse_meth_*} assignments in {@code mon-init.c}.
     *
     * <p>Constructor coded before 260930, commented in full on 260930.
     *
     * @param name        method name
     * @param cut         whether it can cut
     * @param stun        whether it can stun
     * @param miss        whether it can miss
     * @param phys        whether it is physical
     * @param mesgT       message category
     * @param blowMessage flavour messages
     * @param desc        description
     */
    public BlowMethod(String name, boolean cut, boolean stun, boolean miss, boolean phys, MessageType mesgT,
                      List<String> blowMessage, String desc) {
        this.name = name;
        this.cut = cut;
        this.stun = stun;
        this.miss = miss;
        this.phys = phys;
        this.mesgT = mesgT;
        this.blowMessage = blowMessage;
        this.desc = desc;
    }

    /**
     * The method's name, the key monster blows use to refer to it.
     *
     * <p>Function getName coded before 260930, commented in full on 260930.
     *
     * @return this blow method's name
     */
    public String getName() {
        return name;
    }

    /**
     * Whether a landed blow of this method may cut; the C {@code do_cut} gate in
     * {@code mon-attack.c}.
     *
     * <p>Function isCut coded before 260930, commented in full on 260930.
     *
     * @return whether this method can inflict cuts on its target
     */
    public boolean isCut() {
        return cut;
    }

    /**
     * Whether a landed blow of this method may stun; the C {@code do_stun} gate in
     * {@code mon-attack.c}.
     *
     * <p>Function isStun coded before 260930, commented in full on 260930.
     *
     * @return whether this method can stun its target
     */
    public boolean isStun() {
        return stun;
    }

    /**
     * Whether a failed blow of this kind is announced to the player. This is about
     * <em>reporting</em>, not accuracy: it is false for the methods where a miss would
     * read oddly (crawling, drooling, gazing, wailing), which simply stay silent. C
     * additionally requires the monster to be visible before it prints the message.
     *
     * <p>Function isMiss coded before 260930, commented in full on 260930.
     *
     * @return whether the player is told when this blow misses
     */
    public boolean isMiss() {
        return miss;
    }

    /**
     * Whether this method counts as a physical attack, which in C makes the target's
     * armour reduce the damage ({@code mon-blows.c}).
     *
     * <p>Function isPhys coded before 260930, commented in full on 260930.
     *
     * @return whether this method does physical damage
     */
    public boolean isPhys() {
        return phys;
    }

    /**
     * The message channel this blow is reported on, which drives the sound and colour
     * the interface gives it. Defaults to {@link MessageType#MSG_GENERIC} for a method
     * whose data-file entry gave no {@code msg:}.
     *
     * <p>Function getMesgT coded before 260930, commented in full on 260930.
     *
     * @return this blow method's message type
     */
    public MessageType getMesgT() {
        return mesgT;
    }

    /**
     * The flavour strings narrating a landed blow, one of which is chosen at random when
     * the blow is described - so this holds every alternative, not a sequence (most
     * methods ship one, INSULT and MOAN ship eight apiece). The {@code {target}}-style
     * braces are placeholders still awaiting expansion at display time (C's
     * {@code monster_blow_method_action()} expands {@code {target}}, {@code {pronoun}}
     * and friends). The list is in file order, where C's is reversed; see the field note.
     *
     * <p>Function getBlowMessage coded before 260930, commented in full on 260930.
     *
     * @return this blow method's action messages, in data-file order; may be empty
     */
    public List<String> getBlowMessage() {
        return blowMessage;
    }

    /**
     * The short phrase naming this method in monster lore, e.g. "hit", "drool on you".
     *
     * <p>Function getDesc coded before 260930, commented in full on 260930.
     *
     * @return the short phrase naming this method in monster lore
     */
    public String getDesc() {
        return desc;
    }
}