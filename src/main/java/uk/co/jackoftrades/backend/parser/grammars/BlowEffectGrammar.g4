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
 * Parser for lib/gamedata/blow_effects.txt, the monster melee blow effects.
 *
 * Each rule hands back the raw text of its directive; nothing is converted here.
 * Numbers stay as strings and enum names stay as names, so that a bad value is
 * reported by BlowEffectAssembler against the record's line number rather than
 * throwing out of the parse and losing the rest of the file.
 *
 * A record is a name: followed by its directives in any order. The C original
 * requires effect-type: to precede resist:, because it resolves the resist during
 * parsing; this grammar has no such constraint, since resolution happens after
 * the whole record is in hand.
 */
parser grammar BlowEffectGrammar;

options { tokenVocab = BlowEffectLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.bloweffect.BlowEffectParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

/*
 * @author Rowan Crowther
 *
 * The record-count header. Returned as text for the reader to check against the
 * number of records actually parsed.
 */
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * The directive that opens a record. Also captures the line it sits on, which is
 * what the assembler quotes when reporting a bad value further down the record.
 */
name
        returns[String nameStr, int line]
        :   NAME STRING { $line = $start.getLine(); $nameStr = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * The to-hit power. Kept as text so a malformed number is an assembler error.
 */
power
        returns[String powerVal]
        :   POWER INTEGER { $powerVal = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * The danger-rating weight. Kept as text, as for power.
 */
eval
        returns[String evalVal]
        :   EVAL INTEGER { $evalVal = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * The monster-recall description.
 */
desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player has no protection.
 */
loreColourBase
        returns[String colourStr]
        :   LORE_COLOUR_BASE STRING { $colourStr = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player resists.
 */
loreColourResist
        returns[String colourStr]
        :   LORE_COLOUR_RESIST STRING { $colourStr = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player is immune.
 */
loreColourImmune
        returns[String colourStr]
        :   LORE_COLOUR_IMMUNE STRING { $colourStr = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * What kind of attribute protects against the effect.
 */
effectType
        returns[String type]
        :   EFFECT_TYPE STRING { $type = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * The flag or element that protects against the effect.
 */
resist
        returns[String resistStr]
        :   RESIST STRING { $resistStr = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * The projection this effect becomes in its lash form.
 */
lashType
        returns[String lashStr]
        :   LASH STRING { $lashStr = $STRING.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * One complete record: a mandatory name: followed by one or more directives in
 * any order.
 *
 * The locals are seeded with empty strings so that a directive the record omits
 * arrives at the assembler as "" rather than null, letting it test absence with
 * isEmpty() throughout. The loop terminates naturally at the next record, since
 * name is outside it and so cannot be matched twice.
 *
 * A repeated directive silently keeps the last value seen, which is what the C
 * original does too - its parse handlers simply overwrite the field.
 */
blowEffect
        returns[BlowEffectParseRecord effect]
        @init {
            String nameInit = "";
            String powerInit = "";
            String evalInit = "";
            String descInit = "";
            String loreColourBaseInit = "";
            String loreColourResistInit = "";
            String loreColourImmuneInit = "";
            String effectTypeInit = "";
            String resistInit = "";
            String lashTypeInit = "";
            int line = 0;
        }
        @after {
            $effect = new BlowEffectParseRecord(nameInit, powerInit,
                    evalInit, descInit, loreColourBaseInit,
                    loreColourResistInit, loreColourImmuneInit,
                    effectTypeInit, resistInit, lashTypeInit, line);
        }
        :   name { nameInit = $name.nameStr; line = $name.line; }
        (   power { powerInit = $power.powerVal; }
        |   eval { evalInit = $eval.evalVal; }
        |   desc { descInit = $desc.descStr; }
        |   loreColourBase { loreColourBaseInit = $loreColourBase.colourStr; }
        |   loreColourResist { loreColourResistInit = $loreColourResist.colourStr; }
        |   loreColourImmune { loreColourImmuneInit = $loreColourImmune.colourStr; }
        |   effectType { effectTypeInit = $effectType.type; }
        |   resist { resistInit = $resist.resistStr; }
        |   lashType { lashTypeInit = $lashType.lashStr; }
        )+;

/*
 * @author Rowan Crowther
 *
 * The whole file: the record-count header followed by at least one record.
 *
 * EOF is required so that a record the grammar cannot match is reported as a
 * syntax error rather than quietly ending the file early with the remaining
 * records dropped.
 */
file
        returns[String declaredRecordCount, List<BlowEffectParseRecord> blowEffects]
        @init {
            $blowEffects = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (blowEffect { $blowEffects.add($blowEffect.effect); })+ EOF
        ;
