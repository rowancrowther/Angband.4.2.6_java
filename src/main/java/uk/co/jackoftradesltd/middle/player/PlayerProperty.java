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

package uk.co.jackoftradesltd.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlagType;

import java.util.ArrayList;
import java.util.List;

/**
 * The definition of one player property — a named characteristic (a player flag, an object flag,
 * or an elemental resistance) together with how it is presented in the UI.
 *
 * <p>Ports the C {@code struct player_ability} ({@code player.h}), built from
 * {@code player_property.txt}. In C a single {@code index} field is a hand-rolled union holding a
 * {@code PF_*}, {@code OF_*} or element index, disambiguated by the {@code type} string. Because
 * Java has no unions, the port splits that one field into a {@link #playerPropertyType}
 * discriminator plus separate, correctly-typed carriers — {@link #pCode} for a player flag,
 * {@link #oCode} for an object flag, and {@link #eCode} for an element. {@link #omFlag} is the
 * port's own addition, the carrier for {@link PlayerPropertyType#PROP_TYPE_OBJECT_MODIFIER} — see
 * that constant's own documentation for why {@code player_property.txt} never actually produces one.
 * Exactly one carrier is meaningful, per the discriminator. {@link #value} is unrelated to the
 * union — it is C's separate {@code player_ability.value} field, the resistance level an element
 * property confers.
 *
 * <p>Beyond the C struct the port also holds {@link #entries}: the resolved bindings from this
 * property to the {@code UIEntry} slots that display it (the {@code bindui:} lines), which is how a
 * property surfaces on the character screen.
 *
 * <p>Two things here are not part of what {@code player_property.txt} defines. {@link #group} is
 * C's {@code player_ability.group}, which the data file never sets: it stays
 * {@link PlayerFlagType#PLAYER_FLAG_NONE} on every entry of {@code PlayerRegistry}, and only the
 * copies that {@link #viewAbilities} builds for the "Race and class abilities" menu carry
 * {@code PLAYER_FLAG_CLASS} or {@code PLAYER_FLAG_RACE}. A registry entry is therefore always an
 * ability whatever its group, and a group of {@code NONE} means "no view has assigned one", not
 * "not an ability". {@link #viewAbilities}, {@link #classHasAbility} and {@link #raceHasAbility}
 * are the ports of C's {@code view_abilities}, {@code class_has_ability} and
 * {@code race_has_ability} ({@code player-properties.c}).
 *
 * <p>Class PlayerProperty coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class PlayerProperty {
    /**
     * Logger for this type.
     *
     * <p>Field logger coded before 261009, commented in full on 261009.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * One binding of a player property to a UI display slot (a {@code bindui:} line). Ports C's
     * {@code struct player_bound_ui} ({@code init.c}), the per-line record the parser keeps until
     * {@code finish_parse_player_prop} binds it to the UI entry; here it is kept on the property.
     *
     * <p>Record BindUI coded before 261009, commented in full on 261009.
     *
     * @param uiEntry the UI entry this property is displayed in
     * @param value   the value threshold associated with the binding
     * @param special whether this is a "special" binding variant
     * @param aux     whether this is an auxiliary binding variant
     * @author Rowan Crowther
     */
    public record BindUI(String uiEntry, int value, boolean special, boolean aux) {
    }

    /**
     * Discriminator selecting which flavour of property (and which code carrier) is live. Replaces
     * the {@code type} string of C's {@code player_ability}, which every C test compares with
     * {@code streq}.
     *
     * <p>Field playerPropertyType coded before 261009, commented in full on 261009.
     */
    private PlayerPropertyType playerPropertyType;
    /**
     * Where this ability came from, which the ability menu's display switches on to choose the
     * prefix and colour ("Class:" in umber for {@link PlayerFlagType#PLAYER_FLAG_CLASS}, "Racial:"
     * in orange for {@link PlayerFlagType#PLAYER_FLAG_RACE}, "Specialty Ability:" in green for
     * {@link PlayerFlagType#PLAYER_FLAG_SPECIAL}, "Mysterious" in purple for anything else). C's
     * {@code player_ability.group}, commented there as "set locally when viewing".
     *
     * <p>Holds exactly one value and starts at {@link PlayerFlagType#PLAYER_FLAG_NONE}, as C's
     * zeroed struct does. Set only on the copies {@link #viewAbilities} builds, never on an entry of
     * the registry: an ability that both the class and the race have is listed twice, once per
     * group, so a single shared object could not hold both.
     *
     * <p>Field group coded before 261009, commented in full on 261009.
     */
    private PlayerFlagType group;
    /**
     * Payload when {@link #playerPropertyType} is {@code PROP_TYPE_PLAYER}: the player flag (C's
     * {@code player_ability.index} read as a {@code PF_*}).
     *
     * <p>Field pCode coded before 261009, commented in full on 261009.
     */
    private PlayerFlag pCode;
    /**
     * Payload when {@link #playerPropertyType} is {@code PROP_TYPE_OBJECT}: the object flag (C's
     * {@code player_ability.index} read as an {@code OF_*}).
     *
     * <p>Field oCode coded before 261009, commented in full on 261009.
     */
    private ObjectFlag oCode;
    /**
     * Payload when {@link #playerPropertyType} is {@code PROP_TYPE_ELEMENT}: the element code (C's
     * {@code player_ability.index} read as an element index).
     *
     * <p>Field eCode coded before 261009, commented in full on 261009.
     */
    private ElementEnum eCode;
    /**
     * Payload when {@link #playerPropertyType} is {@code PROP_TYPE_OBJECT_MODIFIER}: the object
     * modifier. The port's own addition; C has no such type.
     *
     * <p>Field omFlag coded before 261009, commented in full on 261009.
     */
    private ObjectModifier omFlag;
    /**
     * Resolved bindings from this property to the UI slots that display it (the {@code bindui:}
     * lines). Not a field of C's {@code player_ability}: C hands each binding to
     * {@code bind_player_ability_to_ui_entry_by_name} ({@code ui-entry.c}) as the property is
     * built and keeps it on the UI entry.
     *
     * <p>Field entries coded before 261009, commented in full on 261009.
     */
    private List<BindUI> entries;
    /**
     * Display name of the property (C: {@code player_ability.name}).
     *
     * <p>Field name coded before 261009, commented in full on 261009.
     */
    private String name;
    /**
     * Human-readable description of the property (C: {@code player_ability.desc}).
     *
     * <p>Field description coded before 261009, commented in full on 261009.
     */
    private String description;
    /**
     * For an element property, the resistance level a race must have to the element for the
     * property to apply: -1 (vulnerability), 1 (resistance) or 3 (immunity), as
     * {@code player_property.txt} documents. {@link #raceHasAbility} compares it for equality with
     * the race's level (C: {@code player_ability.value}). Unused for the other types.
     *
     * <p>Field value retyped from PlayerPropertyValue to int on 261009, commented in full on 261009.
     */
    private int value;

    /**
     * Builds a fully-resolved player property, as produced by the property reader/assembler from one
     * {@code player_property.txt} record. The new property's {@link #group} starts at
     * {@link PlayerFlagType#PLAYER_FLAG_NONE}.
     *
     * <p>Function PlayerProperty coded before 261009, commented in full on 261009.
     *
     * @param playerPropertyType the property flavour / code discriminator
     * @param pCode              the player flag (for {@code PROP_TYPE_PLAYER}; otherwise {@code null})
     * @param oCode              the object flag (for {@code PROP_TYPE_OBJECT}; otherwise {@code null})
     * @param eCode              the element (for {@code PROP_TYPE_ELEMENT}; otherwise {@code null})
     * @param omFlag             the object modifier (for {@code PROP_TYPE_OBJECT_MODIFIER}; otherwise
     *                           {@code null})
     * @param entries            the resolved UI bindings
     * @param name               display name
     * @param description        human-readable description
     * @param value              the resistance level -1, 1 or 3 (for element properties; otherwise 0)
     */
    public PlayerProperty(PlayerPropertyType playerPropertyType,
                          PlayerFlag pCode,
                          ObjectFlag oCode,
                          ElementEnum eCode,
                          ObjectModifier omFlag,
                          List<BindUI> entries,
                          String name,
                          String description,
                          int value) {
        this.playerPropertyType = playerPropertyType;
        this.oCode = oCode;
        this.pCode = pCode;
        this.eCode = eCode;
        this.omFlag = omFlag;
        this.entries = entries;
        this.name = name;
        this.description = description;
        this.value = value;
        this.group = PlayerFlagType.PLAYER_FLAG_NONE;
    }

    /**
     * Reads the discriminator that says which code carrier is live.
     *
     * <p>Function getPlayerPropertyType coded before 261009, commented in full on 261009.
     *
     * @return the property flavour / code discriminator
     */
    public PlayerPropertyType getPlayerPropertyType() {
        return playerPropertyType;
    }

    /**
     * Reads the player flag carrier; {@code null} unless the type is {@code PROP_TYPE_PLAYER}.
     *
     * <p>Function getpCode coded before 261009, commented in full on 261009.
     *
     * @return the player flag this property carries (meaningful for {@code PROP_TYPE_PLAYER})
     */
    public PlayerFlag getpCode() {
        return pCode;
    }

    /**
     * Reads the object flag carrier; {@code null} unless the type is {@code PROP_TYPE_OBJECT}.
     *
     * <p>Function getoCode coded before 261009, commented in full on 261009.
     *
     * @return the object flag this property carries (meaningful for {@code PROP_TYPE_OBJECT})
     */
    public ObjectFlag getoCode() {
        return oCode;
    }

    /**
     * Reads the element carrier; {@code null} unless the type is {@code PROP_TYPE_ELEMENT}.
     *
     * <p>Function geteCode coded before 261009, commented in full on 261009.
     *
     * @return the element code this property carries (meaningful for {@code PROP_TYPE_ELEMENT})
     */
    public ElementEnum geteCode() {
        return eCode;
    }

    /**
     * Reads the object-modifier carrier; {@code null} unless the type is
     * {@code PROP_TYPE_OBJECT_MODIFIER}. Named {@code getomCode} though the field is {@code omFlag}.
     *
     * <p>Function getomCode coded before 261009, commented in full on 261009.
     *
     * @return the object modifier this property carries (meaningful for
     * {@code PROP_TYPE_OBJECT_MODIFIER})
     */
    public ObjectModifier getomCode() {
        return omFlag;
    }
    
    /**
     * Reads the resolved UI bindings; the list is the property's own, not a copy.
     *
     * <p>Function getEntries coded before 261009, commented in full on 261009.
     *
     * @return the resolved UI bindings that display this property
     */
    public List<BindUI> getEntries() {
        return entries;
    }

    /**
     * Reads the display name.
     *
     * <p>Function getName coded before 261009, commented in full on 261009.
     *
     * @return the property's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Reads the human-readable description.
     *
     * <p>Function getDescription coded before 261009, commented in full on 261009.
     *
     * @return the property's human-readable description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gathers the abilities the character's class and race confer, as the list the "Race and class
     * abilities" menu shows - the port of C's {@code view_abilities} ({@code player-properties.c}).
     *
     * <p>The registry is walked twice, class first, then race, each time testing every property with
     * {@link #classHasAbility} or {@link #raceHasAbility}. Each match is copied into a new
     * {@code PlayerProperty} - C's {@code memcpy} into the local {@code ability_list} - and only the
     * copy's group is set, {@code PLAYER_FLAG_CLASS} in the first walk and {@code PLAYER_FLAG_RACE}
     * in the second. The registry entries are never written. An ability the class and the race both
     * have therefore appears twice, once per group, as in C.
     *
     * <p>C's {@code ability_list} is a fixed array of 32; the Java list is unbounded.
     *
     * <p><b>Outstanding:</b> the final call to {@code view_ability_menu} is commented out until the
     * menu is ported, so for now the list is built and discarded. Nothing observable results from
     * calling this method.
     *
     * <p>Function viewAbilities coded on 261009, commented in full on 261009.
     */
    public static void viewAbilities() {
        List<PlayerProperty> properties = PlayerRegistry.getPlayerProperties();
        int numAbilities = 0;
        List<PlayerProperty> abilityList = new ArrayList<>();
        Player player = GameState.getPlayer();
        
        for (PlayerProperty property : properties) {
            if (property.classHasAbility(player.getPlayerClass(), property)) {
                PlayerProperty prop = new PlayerProperty(property.getPlayerPropertyType(), property.getpCode(), property.getoCode(), property.geteCode(),
                        property.getomCode(), property.getEntries(), property.getName(), property.getDescription(), property.getValue());
                prop.setGroup(PlayerFlagType.PLAYER_FLAG_CLASS);      
                numAbilities++;
                abilityList.add(prop);
            }
        }
        
        for (PlayerProperty property : properties) {
            if (property.raceHasAbility(player.getRace(), property)) {
                PlayerProperty prop = new PlayerProperty(property.getPlayerPropertyType(), property.getpCode(), property.getoCode(), property.geteCode(),
                        property.getomCode(), property.getEntries(), property.getName(), property.getDescription(), property.getValue());
                prop.setGroup(PlayerFlagType.PLAYER_FLAG_RACE);
                numAbilities++;
                abilityList.add(prop);
            }
        }
        
        // TODO - uncomment out below line once it is ported
        //
        // viewAbilityMenu(abilityList, numAbilities);
    }

    /**
     * Reads the resistance level; {@link #raceHasAbility} compares it with the race's level.
     *
     * <p>Function getValue coded before 261009, commented in full on 261009.
     *
     * @return the resistance level this property confers (meaningful for element properties)
     */
    public int getValue() {
        return value;
    }

    /**
     * Reads which group this property was listed under; see {@link #group}.
     *
     * <p>Function getGroup coded before 261009, commented in full on 261009.
     *
     * @return the group - {@code PLAYER_FLAG_NONE} for a registry entry, {@code PLAYER_FLAG_CLASS}
     * or {@code PLAYER_FLAG_RACE} for a copy built by {@link #viewAbilities}
     */
    public PlayerFlagType getGroup() {
        return group;
    }

    /**
     * Assigns the group. Call it on a copy only, never on an entry of {@code PlayerRegistry}; see
     * {@link #group} for why.
     *
     * <p>Function setGroup coded before 261009, commented in full on 261009.
     *
     * @param group the group to assign
     */
    public void setGroup(PlayerFlagType group) {
        this.group = group;
    }
    
    /**
     * Tests whether a race confers an ability - the port of C's {@code race_has_ability}
     * ({@code player-properties.c}).
     *
     * <p>A player-flag property applies if the race has that player flag, an object-flag property if
     * the race has that object flag, and an element property if the race's resistance level to the
     * element equals the property's {@link #value} exactly. The levels are compared for equality, not
     * "at least": a race with immunity (3) does not satisfy the resistance property (1). Any other
     * type, including {@code PROP_TYPE_OBJECT_MODIFIER}, applies to nothing, as C's {@code streq}
     * chain falls through to {@code false}.
     *
     * <p>Function raceHasAbility coded on 261009, commented in full on 261009.
     *
     * @param race     the race to test
     * @param property the property to look for
     * @return {@code true} if the race confers the property
     */
    private boolean raceHasAbility(PlayerRace race, PlayerProperty property) {
        if (property.getPlayerPropertyType() == PlayerPropertyType.PROP_TYPE_PLAYER
                && race.getpFlags().has(property.getpCode())) {
            return true;
        } else if (property.getPlayerPropertyType() == PlayerPropertyType.PROP_TYPE_OBJECT 
                && race.getoFlags().has(property.getoCode())) {
            return true;
        } else return property.getPlayerPropertyType() == PlayerPropertyType.PROP_TYPE_ELEMENT
                && race.getResistanceLevel(property.geteCode()) == property.getValue();
    }
    
    /**
     * Tests whether a class confers an ability - the port of C's {@code class_has_ability}
     * ({@code player-properties.c}).
     *
     * <p>A player-flag property applies if the class has that player flag and an object-flag property
     * if the class has that object flag. Unlike {@link #raceHasAbility} there is no element branch:
     * a class has no resistance table, so an element property never applies to a class.
     *
     * <p>Function classHasAbility coded on 261009, commented in full on 261009.
     *
     * @param playerClass the class to test
     * @param property    the property to look for
     * @return {@code true} if the class confers the property
     */
    private boolean classHasAbility(PlayerClass playerClass, PlayerProperty property) {
        if (property.getPlayerPropertyType() == PlayerPropertyType.PROP_TYPE_PLAYER 
            && playerClass.getpFlags().has(property.getpCode())) {
            return true;
        } 
        
        return property.getPlayerPropertyType() == PlayerPropertyType.PROP_TYPE_OBJECT
                && playerClass.getoFlags().has(property.getoCode());
    }

    /**
     * Replaces the object flag carrier. Nothing in {@code src/main} or {@code src/test} calls it:
     * the constructor sets the carrier, and the registry entries are not changed after loading.
     *
     * <p>Function setoCode coded before 261009, commented in full on 261009.
     *
     * @param oCode the object flag
     */
    public void setoCode(ObjectFlag oCode) {
        this.oCode = oCode;
    }

    /**
     * Replaces the player flag carrier. Nothing in {@code src/main} or {@code src/test} calls it;
     * see {@link #setoCode}.
     *
     * <p>Function setpCode coded before 261009, commented in full on 261009.
     *
     * @param pCode the player flag
     */
    public void setpCode(PlayerFlag pCode) {
        this.pCode = pCode;
    }

    /**
     * Replaces the element carrier. Nothing in {@code src/main} or {@code src/test} calls it; see
     * {@link #setoCode}.
     *
     * <p>Function seteCode coded before 261009, commented in full on 261009.
     *
     * @param eCode the element
     */
    public void seteCode(ElementEnum eCode) {
        this.eCode = eCode;
    }

    /**
     * The flavour of a player property, discriminating which code carrier is live and how the
     * property is interpreted (C: the {@code type} string of {@code player_ability}).
     *
     * <p>Enum PlayerPropertyType coded before 261009, commented in full on 261009.
     *
     * @author Rowan Crowther
     */
    public enum PlayerPropertyType {
        /** A player (class/race) flag property; the {@link #getpCode()} carrier is live. */
        PROP_TYPE_PLAYER,
        /** An object flag property; the {@link #getoCode()} carrier is live. */
        PROP_TYPE_OBJECT,
        /** An elemental resistance property; the {@link #getValue()} level is live. */
        PROP_TYPE_ELEMENT,

        /**
         * Reserved, currently unproduced. No {@code player_property.txt} record ever assembles to
         * this member — {@link uk.co.jackoftradesltd.backend.parser.playerproperty.PlayerPropertyAssembler}
         * only ever builds {@code PROP_TYPE_PLAYER}, {@code PROP_TYPE_OBJECT}, {@code PROP_TYPE_ELEMENT}
         * or {@link #PROP_TYPE_OBJECT_MODIFIER}.
         */
        PROP_TYPE_PROPERTY,

        /**
         * An object-modifier property; the {@link #getomCode()} carrier is live. C's
         * {@code type} string has no member that maps here — this is what the assembler falls back to
         * for a {@code type:} value it does not otherwise recognize ({@code player}/{@code object}/
         * {@code element}), which today's {@code player_property.txt} never contains. Wherever this
         * type does occur, {@code UIEntryValueRegistry} does not dispatch on it and so contributes
         * nothing for it, mirroring C's switch having no default case for an unrecognized
         * {@code player_ability.type}.
         */
        PROP_TYPE_OBJECT_MODIFIER
    }

    /**
     * The resistance level an element property confers, from vulnerability through to immunity.
     * Superseded: {@link #value} is now a plain {@code int} (-1, 1 or 3, as C stores it), and
     * nothing in {@code src/main} or {@code src/test} refers to this enum.
     *
     * <p>Enum PlayerPropertyValue coded before 261009, commented in full on 261009.
     *
     * @author Rowan Crowther
     */
    public enum PlayerPropertyValue {
        /** No resistance modifier. */
        NONE,
        /** Takes extra damage from the element. */
        VULNERABILITY,
        /** Takes reduced damage from the element. */
        RESISTANCE,
        /** Takes no damage from the element. */
        IMMUNITY
    }
}