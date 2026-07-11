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

/*
 * @author Rowan Crowther
 *
 * Reader for lib/gamedata/activation.txt: Activatable effects used by
 * objects. Includes whether it needs aiming <boolean>, difficulty level
 * <integer>, power <integer>, effect (from EffectBlock), and a description
 * <String>.
 *
 * Cf. src/obj-init.c: struct file_parser act_parser (obj-init.c:1678),
 * directive table around obj-init.c:1600-1630, and finish_parse_act
 * (obj-init.c:1635) which assigns each activation a sequential index
 * used for array-based lookups elsewhere in the engine.
 *
 * This index is removed, as in Java we are using the object itself from
 * a fixed List, not an array index to a [] array.
 */
parser grammar ActivationsGrammar;
options { tokenVocab = ActivationsLexer; }
import EffectBlock;

/*
 * @author Rowan Crowther
 *
 * Imports of List and ArrayList
 */
@header {
    import uk.co.jackoftrades.backend.parser.activation.ActivationParseRecord;
    import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

/*
 * @author Rowan Crowther
 *
 * "record-count:<INTEGER>" number of total records in this file. Remember
 * to change this if you add/remove a record from this file.
 */
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "name:<NAME_STRING>" initial token in an activation record. This
 * string is used by other data files as a reference to get a
 * particular activation from the list of all activations.
 *
 * cf ActivationsLexer.g4 NAME & NAME_STRING tokens
 */
name
        returns[String nameStr]
        :   NAME NAME_STRING { $nameStr = $NAME_STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "aim:0|1" used to create a boolean (1 = true) to determine whether
 * this activation requires aiming.
 *
 * cf ActivationsLexer.g4 AIM token
 */
aim
        returns[String aimBool]
        :   AIM INTEGER { $aimBool = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "level:<INTEGER>" the difficulty of causing this activation to occur.
 *
 * cf ActivationsLexer.g4 LEVEL token
 */
level
        returns[String levelInt]
        :   LEVEL INTEGER { $levelInt = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "power:<INTEGER>" the relative power for an object-power calculation.
 *
 * cf ActivationsLexer.g4 POWER token
 */
power
        returns[String powerInt]
        :   POWER INTEGER { $powerInt = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "desc:<String>" the description shown to the player when
 * this activation is described, such as "cures blindness".
 *
 * This is a partial string and will be made part of a larger
 * string later on.
 *
 * cf ActivationsLexer.g4 DESC & DESC_STRING tokens
 */
desc
        returns[String descStr]
        :   DESC DESC_STRING
            { $descStr = $DESC_STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "msg:<String>" the message shown to the player when the
 * activation is triggered, such as "Your {kind} radiate{s}
 * deep purple...". This message can include {} tags to allow
 * the message to be customised to the item which is being
 * activated.
 *
 * cf ActivationsLexer.g4 MSG & DESC_STRING tokens
 */
msg
        returns[String message]
        :   MSG DESC_STRING
            { $message = $DESC_STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * A full activation record, starting with name, and then followed by a mix of
 * aim/level/power/msg/desc/effectBlock (with the latter being pulled in from
 * the EffectBlock grammar file). These 6 items are allowed in any order and multiple
 * versions of them are allowed per activation.
 *
 * cf EffectBlock.g4 'effectBlock' rule
 */
activation
        returns[ActivationParseRecord activationRecord]
        @init {
            String nameInit = "";
            String aimInit = "";
            String levelInit = "";
            String powerInit = "";
            String messageInit = "";
            String descInit = "";
            List<EffectParseRecord> effects = new ArrayList<>();
        }
        @after {
            $activationRecord =
                new ActivationParseRecord(nameInit,
                    aimInit, levelInit, powerInit,
                    messageInit, descInit, effects,
                    $start.getLine());
        }
        :   name {
                nameInit = $name.nameStr;
            }
        (   aim { aimInit = $aim.aimBool; }
        |   level { levelInit = $level.levelInt; }
        |   power { powerInit = $power.powerInt; }
        |   msg { messageInit = $msg.message; }
        |   effectBlock { effects.add(new EffectParseRecord($effectBlock.typeInit,
                $effectBlock.subtypeWrapperInit, $effectBlock.radius, $effectBlock.other,
                $effectBlock.diceString, $effectBlock.yVal, $effectBlock.xVal,
                $effectBlock.expressionChars, $effectBlock.expressionBase,
                $effectBlock.expressionOperation, $effectBlock.timeDiceString,
                $effectBlock.effectMessage, $effectBlock.start.getLine())); }
    |   desc { descInit = $desc.descStr; })+
        ;

/*
 * @author Rowan Crowther
 *
 * Top level - returns a List of the string representation of the activation
 * to allow the ActivationReader to create the activations from it.
 */
file
        returns[List<ActivationParseRecord> records, String declaredCount]
        :   {
                $records = new ArrayList<>();
            }
            recordCount { $declaredCount = $recordCount.count; }
            (activation {
                $records.add($activation.activationRecord);
            })+ EOF
        ;