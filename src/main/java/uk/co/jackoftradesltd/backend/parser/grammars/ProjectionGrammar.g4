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
 * Parser for lib/gamedata/projection.txt. Every "projection" which includes
 * element damage types, descriptions, damage formula, message types and
 * display colours. Cf. src/obj-init.c: struct file_parser projection_parser,
 * directive table in obj-init.c (parse_projection
 * _code/_name/_type/_desc/_player_desc/_blind_desc/_lash_desc/_numerator/
 * _denominator/_divisor/_damage_cap/_message_type/_obvious/_wake/_color).
 */

parser grammar ProjectionGrammar;
options { tokenVocab = ProjectionLexer; }

@header {
            import java.util.Arrays;
            import java.util.ArrayList;
            import java.util.List;
        }

/*
 * @author Rowan Crowther
 *
 * "code:<PROJ_CODE>" - starts a new projection record.
 */
code    returns[String projectionCode]
        :   CODE FLAG {
            $projectionCode = $FLAG.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "name:<text>" - human-readable name; links brand.txt entries to this projection.
 */
name    returns[String projectionName]
        :   NAME STRING {
            $projectionName = $STRING.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "type:element|environs|monster" - what kind of projection this is.
 */
type    returns[String projectionType]
        :   TYPE STRING {
            $projectionType = $STRING.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "desc:<text>" - general flavour description.
 */
desc    returns[String projectionDesc]
        :   DESC STRING {
            $projectionDesc = $STRING.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "player-desc:<text>" - description used when the player causes this projection.
 */
playerDesc
        returns[String projectionPlayerDesc]
        :   PLAYER_DESC STRING {
            $projectionPlayerDesc = $STRING.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 *  "blind-desc:<text>" - description used when the player is blind.
 */
blindDesc
        returns[String projectionBlindDesc]
        :   BLIND_DESC STRING {
            $projectionBlindDesc = $STRING.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "lash-desc:<text>" - description used for a "lash" (whip-like) attack of
 * this type.
 */
lashDesc
        returns[String projectionLashDesc]
        :   LASH_DESC STRING {
            $projectionLashDesc = $STRING.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "numerator:<value>" - damage multiplier numerator.
 */
numerator
        returns [String projectionNumerator]
        :   NUMERATOR INTEGER {
            $projectionNumerator = $INTEGER.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "denominator:<value>|<dice>" - damage divisor, either a literal number or
 * a dice expression (e.g. "8+1d4").
 */
denominator
        returns [String projectionDenominator]
        :   DENOMINATOR SIMPLE_DICE {
            $projectionDenominator = $SIMPLE_DICE.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "divisor:<value>" - HP divisor (e.g. for breath attacks scaled to the
 * breather's HP).
 */
divisor returns[String projectionDivisor]
        :   DIVISOR INTEGER {
            $projectionDivisor = $INTEGER.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "damage-cap:<value>" - maximum damage this projection can deal.
 */
damageCap
        returns[String projectionDamageCap]
        :   DAMAGE_CAP INTEGER {
            $projectionDamageCap = $INTEGER.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "msgt:<MSG_TYPE>" - message type/sound used when this projection hits. The text is
 * the whole rest of the line, kept raw; ProjectionAssembler.resolveMsgt skips leading
 * colons, cuts at the next ':' and resolves what is left, as C does.
 */
msgt    returns[String projectionMsgt]
        :   MSGT msg=MSGT_STRING {
            $projectionMsgt = $msg.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "obvious:0|1" - whether this projection's effect is obvious to the player.
 */
obvious returns[String projectionIsObvious]
        :   OBVIOUS INTEGER {
            $projectionIsObvious = $INTEGER.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "wake:0|1" - whether this projection wakes sleeping monsters.
 */
wake    returns[String projectionWillWake]
        :   WAKE INTEGER {
            $projectionWillWake = $INTEGER.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * "color:<name>" - display colour, parsed to a single String.
 */
colour  returns[String projectionColour]
        :   COLOUR STRING {
            $projectionColour = $STRING.getText();
        }
        ;

/*
 * @author Rowan Crowther
 *
 * One projection record - a "code:" line followed by any of the other
 * fourteen directives, in any order and any number of times, as C registers
 * each handler on its own. A repeated directive replaces the earlier one, so
 * the last wins, and a directive that is left out stays null. Only "code:" is
 * mandatory.
 */
projection
        returns[List<String> proj, int lineNo]
        @init {
            String[] projectionStrings = new String[15];
            Arrays.fill(projectionStrings, null);
        }
        @after {
            $proj = new ArrayList<>(Arrays.asList(projectionStrings));
            $proj.add(String.valueOf($lineNo));
        }
        :   code { projectionStrings[0] = $code.projectionCode;
                   $lineNo = $start.getLine(); }
        (   name { projectionStrings[1] = $name.projectionName; }
        |   type { projectionStrings[2] = $type.projectionType; }
        |   desc { projectionStrings[3] = $desc.projectionDesc; }
        |   playerDesc { projectionStrings[4] = $playerDesc.projectionPlayerDesc; }
        |   blindDesc { projectionStrings[5] = $blindDesc.projectionBlindDesc; }
        |   lashDesc { projectionStrings[6] = $lashDesc.projectionLashDesc; }
        |   numerator { projectionStrings[7] = $numerator.projectionNumerator; }
        |   denominator { projectionStrings[8] = $denominator.projectionDenominator; }
        |   divisor { projectionStrings[9] = $divisor.projectionDivisor; }
        |   damageCap { projectionStrings[10] = $damageCap.projectionDamageCap; }
        |   msgt { projectionStrings[11] = $msgt.projectionMsgt; }
        |   obvious { projectionStrings[12] = $obvious.projectionIsObvious; }
        |   wake { projectionStrings[13] = $wake.projectionWillWake; }
        |   colour { projectionStrings[14] = $colour.projectionColour; })*
        ;

/*
 * @author Rowan Crowther
 *
 * Top-level rule: the whole file is zero or more projection records. An empty
 * file parses; the assembler then stops the load on the element count.
 */
file    returns[List<List<String>> projections]
        @init {
            $projections = new ArrayList<>();
        }
        :   (projection {
                $projections.add($projection.proj);
            })*
            EOF
        ;