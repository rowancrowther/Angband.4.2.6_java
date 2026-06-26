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

// Reader+lexer for lib/gamedata/object_property.txt - the catalogue of
// every object property (stat, resistance, vulnerability, immunity, flag,
// modifier): power/mult weighting for object-power calculations, adjective
// text, and an optional binding to a UI element in ui_entry.txt. Cf.
// src/obj-init.c: struct file_parser object_property_parser
// (obj-init.c:3473).
//
// POTENTIAL PROBLEMS (latent - not currently triggered):
//
//   1. object_property.txt's own header explicitly documents bindui: as
//      repeatable ("This field can appear multiple times to bind a
//      property to more than one user interface element", lines 31-32),
//      but `objProperty` matches it with `(bindUI {...})?` (at most once).
//      No current entry actually has two bindui: lines, so this is the
//      same documented-but-unimplemented gap as PlayerProperty.g4's
//      `bindui`.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar ObjectProperty;

@header {
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.frontend.entries.UIEntry;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.ObjectProperty;
    import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
    import uk.co.jackoftrades.middle.objects.enums.TValue;

    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

@members {
    private static final Logger logger = LogManager.getLogger();
}

// "name:<text>" - starts a new property record.
name
        returns[String nameStr]
        :   NAME LCASE {
                $nameStr = $LCASE.getText();
                logger.info("Name: " + $nameStr);
            }
        ;

// "type:resistance|vulnerability|immunity|stat|flag|mod|..." - the property
// category; must appear before code: per the file's own header. The three
// listed synonyms are shortened to match OBJ_PROPERTY_RESIST/_VULN/_IMM.
type
        returns[ObjPropertyType typeObj]
        :   TYPE LCASE {
                String raw = $LCASE.getText();
                switch(raw) {
                    case "resistance":
                        raw = "resist";
                        break;

                    case "vulnerability":
                        raw = "vuln";
                        break;

                    case "immunity":
                        raw = "imm";
                        break;
                }

                $typeObj = ObjPropertyType.valueOf("OBJ_PROPERTY_" + raw.toUpperCase());
            }
        ;

// "subtype:<text>" - subtype, currently only meaningful for flag-typed properties.
subType
        returns[String subTypeStr]
        :   SUBTYPE LCASE { $subTypeStr = $LCASE.getText(); }
        ;

// "id-type:<text>" - how the property is identified, currently only used by flags.
idType
        returns[String idTypeStr]
        :   ID_TYPE LCASE { $idTypeStr = $LCASE.getText(); }
        ;

// "code:<CODE>" - the engine-internal code identifying this property
// within its type (e.g. an OF_*/OM_*/ELEM_* name).
code
        returns[String codeStr]
        :   CODE UCASE { $codeStr = $UCASE.getText(); }
        ;

// "power:<value>" - weighting in object-power calculations.
power
        returns[int powerInt]
        :   POWER INTEGER { $powerInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "mult:<value>" - relative value multiplier used in power calculations.
mult
        returns[int multInt]
        :   MULT INTEGER { $multInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "type-mult:<tval text>:<value>" - per-object-type power multiplier
// override; can repeat (see `objProperty`'s typeMultInit map).
typeMult
        returns[TValue tVal, int value]
        :   TYPE_MULT ty=LCASE COLON val=INTEGER {
                String rawType = "TV_" + $ty.getText().toUpperCase().replace(' ', '_').replace("ARMOUR", "ARMOR");
                $tVal = TValue.valueOf(rawType);
                $value = Integer.parseInt($val.getText());
            }
        ;

// "adjective:<text>" - adjective form, currently only used by stats.
adjective
        returns[String adjStr]
        :   ADJECTIVE LCASE { $adjStr = $LCASE.getText(); }
        ;

// "neg-adjective:<text>" - opposite-direction adjective, currently only used by stats.
negAdjective
        returns[String negAdjStr]
        :   NEG_ADJECTIVE LCASE { $negAdjStr = $LCASE.getText(); }
        ;

// "msg:<text>" - message printed on noticing this property; can repeat to
// build up multiple lines.
msg
        returns[String msgStr]
        :   MSG {
                String raw = $MSG.getText();
                $msgStr = raw.substring(4);
            }
        ;

// "bindui:<ui entry name>[<TAG>]:<aux 0|1>[:<value>]" - binds this property
// to a UI element in ui_entry.txt. See top-of-file problem #1 re: the
// multi-occurrence gap.
bindUI
        returns[UIEntry entryObj, ObjPropertyType propType, int parm1, int parm2]
        @init {
            $parm2 = 0;
            $propType = ObjPropertyType.OBJ_PROPERTY_NONE;
        }
        :   BINDUI entry=LCASE {
                $entryObj = GameConstants.getUIEntry($entry.getText());
            } (LESSTHAN flg=UCASE GREATERTHAN {
                String typeStr = $flg.getText();
                String testFlag = "STAT_" + typeStr.toUpperCase();
                boolean isStat = false;
                try {
                    Stats st = Stats.valueOf(testFlag);
                    isStat = true;
                } catch (Exception e) {
                    // Do nothing
                }
                if (isStat) {
                    $propType = ObjPropertyType.OBJ_PROPERTY_STAT;
                } else {
                    $propType = ObjPropertyType.OBJ_PROPERTY_RESIST;
                }

            })? COLON value=INTEGER {
                $parm1 = Integer.parseInt($value.getText());
            } (COLON index=INTEGER {
                $parm2 = Integer.parseInt($index.getText());
            })?
        ;

// "desc:<text>" - extra descriptive text used in object information.
desc
        returns[String descStr]
        :   DESC { $descStr = $DESC.getText().substring(5); }
        ;

// One full property record: name, then any mix of type/code/subtype/
// id-type/power/mult/type-mult/adjective/neg-adjective/msg (any order/
// quantity), then an optional bindui and an optional desc.
objProperty
        returns[ObjectProperty property]
        @init {
            ObjPropertyType typeInit = ObjPropertyType.OBJ_PROPERTY_NONE;
            String subTypeInit = "";
            String idTypeInit = "";
            String codeInit = "";
            ObjectPropertyTypeWrapper wrapperObject = null;
            int powerInit = 0;
            int multInit = 0;
            Map<TValue, Integer> typeMultInit = new HashMap<>();
            String nameInit = "";
            String adjInit = "";
            String negAdjInit = "";
            String message = "";
            String description = "";
            Map<UIEntry, ObjectPropertyTypeWrapper> uiEntriesMap = new HashMap<>();
        }
        @after {
            $property = new ObjectProperty(typeInit, subTypeInit, idTypeInit, wrapperObject, powerInit,
                                           multInit, typeMultInit, nameInit, adjInit, negAdjInit,
                                           message, description, uiEntriesMap);
        }
        :   name { nameInit = $name.nameStr; }
        (   type { typeInit = $type.typeObj; }
        |   code { codeInit = $code.codeStr; }
        |   subType { subTypeInit = $subType.subTypeStr; }
        |   idType { idTypeInit = $idType.idTypeStr; }
        |   power { powerInit = $power.powerInt; }
        |   mult { multInit = $mult.multInt; }
        |   typeMult {
                typeMultInit.put($typeMult.tVal, $typeMult.value);
            }
        |   adjective { adjInit = $adjective.adjStr; }
        |   negAdjective { negAdjInit = $negAdjective.negAdjStr; }
        |   msg { message = message + $msg.msgStr; })+
            (bindUI {
                UIEntry entry = $bindUI.entryObj;

                int p1 = $bindUI.parm1;
                int p2 = $bindUI.parm2;

                switch (typeInit) {
                    case OBJ_PROPERTY_STAT:
                    case OBJ_PROPERTY_MOD:
                        ObjectModifier modifier = ObjectModifier.valueOf("OM_" + codeInit);
                        wrapperObject = new ObjectPropertyTypeWrapper(typeInit, modifier);
                        break;

                    case OBJ_PROPERTY_FLAG:
                        ObjectFlag flag = ObjectFlag.valueOf("OF_" + codeInit);
                        wrapperObject = new ObjectPropertyTypeWrapper(typeInit, flag);
                        break;

                    case OBJ_PROPERTY_IGNORE:
                    case OBJ_PROPERTY_RESIST:
                    case OBJ_PROPERTY_VULN:
                    case OBJ_PROPERTY_IMM:
                        ElementEnum element = ElementEnum.valueOf("ELEM_" + codeInit);
                        wrapperObject = new ObjectPropertyTypeWrapper(typeInit, element);
                        break;

                    default:
                        String msg = "Invalid object property type found while parsing bindui.\n"
                                       + "Type: " + typeInit;
                        throw new InvalidTokenFoundDuringParse(msg);
                }

                wrapperObject.setValues(p1, p2);
                uiEntriesMap.put(entry, wrapperObject);
            })?
            (desc { description = description + $desc.descStr; })?
        ;

// immunity, vulnerability, resistance, ignore, flag, mod, stat

// Top-level rule: the whole file is one or more property records.
file
        returns[List<ObjectProperty> properties]
        @init {
            $properties = new ArrayList<>();
        }
        :   (objProperty {
                $properties.add($objProperty.property);
        })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// NAME through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above (MSG/DESC fold the value's
// charset into the token itself too).
NAME
        :   'name:'
        ;

TYPE
        :   'type:'
        ;

SUBTYPE
        :   'subtype:'
        ;

ID_TYPE
        :   'id-type:'
        ;

CODE
        :   'code:'
        ;

POWER
        :   'power:'
        ;

MULT
        :   'mult:'
        ;

TYPE_MULT
        :   'type-mult:'
        ;

ADJECTIVE
        :   'adjective:'
        ;

NEG_ADJECTIVE
        :   'neg-adjective:'
        ;

MSG
        :   'msg:' ('A'..'Z' | 'a'..'z' | '.' | ' ' | '{' | '}' | '!')+
        ;

BINDUI
        :   'bindui:'
        ;

DESC
        :   'desc:' ('a'..'z' | 'A'..'Z' | ' ' | '(' | ')' | ',' | '\'')+
        ;

// A (possibly negative) literal integer.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Free-running lowercase text - used for name:/type:/subtype:/id-type:/
// adjective:/neg-adjective:/bindui:'s UI-entry-name field.
LCASE
        :   ('a'..'z' | '_' | '0'..'9' | ' ' | '-')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for code:.
UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// Field separator within type-mult:/bindui: lines.
COLON
        :   ':'
        ;

// Brackets around bindui:'s "<ELEMENT>"/"<STAT>"-style parameterization tag.
LESSTHAN
        :   '<'
        ;

GREATERTHAN
        :   '>'
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth doing if a future property needs more than one UI
//      binding - change `objProperty`'s `(bindUI {...})?` to
//      `(bindUI {...})*` and thread the results into a list/map keyed by
//      UIEntry instead of a single wrapperObject.