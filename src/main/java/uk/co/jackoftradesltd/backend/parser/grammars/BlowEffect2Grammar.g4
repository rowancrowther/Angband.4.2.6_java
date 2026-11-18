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
 * reported by BlowEffect2Assembler against the record's line number rather than
 * throwing out of the parse and losing the rest of the file.
 *
 * A record is a name: followed by its directives, which this grammar accepts in any
 * order and hands on in file order. The assembler resolves them in that order, so a
 * resist: must come after the effect-type: it depends on. The C original needs the
 * same order, because it resolves the resist during parsing: a resist: with no
 * effect-type: before it reads a null pointer and crashes, where the assembler
 * refuses the line and quotes its line number. Accepted deviation, decided 261117
 * (docs/Accepted_Deviations.md, Blow effect). The check lives in the assembler, not
 * in this grammar.
 */
parser grammar BlowEffect2Grammar;

options { tokenVocab = BlowEffect2Lexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffect2ParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

    import java.util.ArrayList;
    import java.util.List;
}

/*
 * @author Rowan Crowther
 *
 * The directive that opens a record. Also captures the line it sits on, which is
 * what the assembler quotes when reporting a bad value further down the record.
 */
name
        returns[Directive nameDirective]
        :   NAME STRING { 
                String name = $STRING.getText();
                List<String> values = new ArrayList<>();
                values.add(name);
                $nameDirective = new Directive("name", values, $start.getLine()); 
            }
        ;

/*
 * @author Rowan Crowther
 *
 * The to-hit power. Kept as text so a malformed number is an assembler error.
 */
power
        returns[Directive powerDirective]
        :   POWER INTEGER { 
                String powerVal = $INTEGER.getText();
                List<String> values = new ArrayList<>();
                values.add(powerVal);
                $powerDirective = new Directive("power", values, $start.getLine()); 
            }
        ;

/*
 * @author Rowan Crowther
 *
 * The danger-rating weight. Kept as text, as for power.
 */
eval
        returns[Directive evalDirective]
        :   EVAL INTEGER {
                String evalVal = $INTEGER.getText();
                List<String> values = new ArrayList<>();
                values.add(evalVal);
                $evalDirective = new Directive("eval", values, $start.getLine());
            }
        ;

/*
 * @author Rowan Crowther
 *
 * The monster-recall description.
 */
desc
        returns[Directive descDirective]
        :   DESC STRING {
                String descStr = $STRING.getText();
                List<String> values = new ArrayList<>();
                values.add(descStr);
                $descDirective = new Directive("desc", values, $start.getLine());
            }
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player has no protection.
 */
loreColourBase
        returns[Directive baseDirective]
        :   LORE_COLOUR_BASE STRING { 
                String colourStr = $STRING.getText();
                List<String> values = new ArrayList<>();
                values.add(colourStr);
                $baseDirective = new Directive("base-colour", values, $start.getLine());
            }
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player resists.
 */
loreColourResist
        returns[Directive resistColourDirective]
        :   LORE_COLOUR_RESIST STRING { 
                String colourStr = $STRING.getText();
                List<String> values = new ArrayList<>();
                values.add(colourStr);
                $resistColourDirective = new Directive("resist-colour", values, $start.getLine());
            }
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player is immune.
 */
loreColourImmune
        returns[Directive immuneDirective]
        :   LORE_COLOUR_IMMUNE STRING {
                String colourStr = $STRING.getText();
                List<String> values = new ArrayList<>();
                values.add(colourStr);
                $immuneDirective = new Directive("immune-colour", values, $start.getLine());
            }
        ;

/*
 * @author Rowan Crowther
 *
 * What kind of attribute protects against the effect.
 */
effectType
        returns[Directive effectDirective]
        :   EFFECT_TYPE STRING {
                String type = $STRING.getText();
                List<String> values = new ArrayList<>();
                values.add(type);
                $effectDirective = new Directive("effect", values, $start.getLine());
            }
        ;

/*
 * @author Rowan Crowther
 *
 * The flag or element that protects against the effect.
 */
resist
        returns[Directive resistDirective]
        :   RESIST STRING {
                String resistStr = $STRING.getText();
                List<String> values = new ArrayList<>();
                values.add(resistStr);
                $resistDirective = new Directive("resist", values, $start.getLine());
            }
        ;

/*
 * @author Rowan Crowther
 *
 * The projection this effect becomes in its lash form.
 */
lashType
        returns[Directive lashDirective]
        :   LASH STRING {
                String lashStr = $STRING.getText();
                List<String> values = new ArrayList<>();
                values.add(lashStr);
                $lashDirective = new Directive("lash", values, $start.getLine());
            }
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
        returns[BlowEffect2ParseRecord effect]
        @init {
            List<Directive> directives = new ArrayList<>();
        }
        @after {
            $effect = new BlowEffect2ParseRecord(directives);
        }
        :   name { directives.add($name.nameDirective); }
        (   power { directives.add($power.powerDirective); }
        |   eval { directives.add($eval.evalDirective); }
        |   desc { directives.add($desc.descDirective); }
        |   loreColourBase { directives.add($loreColourBase.baseDirective); }
        |   loreColourResist { directives.add($loreColourResist.resistColourDirective); }
        |   loreColourImmune { directives.add($loreColourImmune.immuneDirective); }
        |   effectType { directives.add($effectType.effectDirective); }
        |   resist { directives.add($resist.resistDirective); }
        |   lashType { directives.add($lashType.lashDirective); }
        )*;

/*
 * @author Rowan Crowther
 *
 * The whole file: at least one record.
 *
 * EOF is required so that a record the grammar cannot match is reported as a
 * syntax error rather than quietly ending the file early with the remaining
 * records dropped.
 */
file
        returns[List<BlowEffect2ParseRecord> blowEffects]
        @init {
            $blowEffects = new ArrayList<>();
        }
        :   (blowEffect { $blowEffects.add($blowEffect.effect); })* EOF
        ;
