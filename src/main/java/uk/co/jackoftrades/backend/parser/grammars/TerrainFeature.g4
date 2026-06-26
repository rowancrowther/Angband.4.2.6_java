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

// Reader+lexer for lib/gamedata/terrain.txt - every terrain feature (floor,
// walls, doors, staircases, shop entrances, rubble, lava, ...): display
// glyph, flags, digging difficulty, mimicry, and player-facing messages.
// Cf. src/init.c: struct file_parser feat_parser (init.c:2297), directive
// table in parse_feat_code/_name/_graphics/_mimic/_priority/_flags/
// _digging/_desc and the various *_msg handlers (init.c:2002-2296ish).
// This grammar IS wired up (TerrainReader.java exists), unlike TrapGrammar/
// Class.g4 elsewhere in this directory - the bug below is live.

grammar TerrainFeature;

@header
        {
            import java.util.List;
            import java.util.ArrayList;

            import uk.co.jackoftrades.backend.utils.Flag;
            import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
            import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
            import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
            import uk.co.jackoftrades.middle.cave.Feature;
            import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
            import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;
            import uk.co.jackoftrades.frontend.colour.enums.ColourType;
            import uk.co.jackoftrades.frontend.colour.enums.ColourTranslation;
        }

// "code:<FEAT_CODE>" - starts a new terrain record; must match a code from
// list-terrain.h.
code
        returns[TerrainFlags codeFlag]
        :   CODE TEXT {
                $codeFlag = TerrainFlags.valueOf("FEAT_" + $TEXT.getText());
            }
        ;

// "name:<text>" - the terrain's printable name.
name
        returns[String nameStr]
        :   NAME TEXT { $nameStr = $TEXT.getText(); }
        ;

// "graphics:<symbol>:<colour>" - returned as a raw "X:Y" string and parsed
// apart in `terrain`'s @after instead of here.
graphics
        returns[String graphicsStr]
        :   GRAPHICS GRAPHICS_CHARACTER // Graphics_character is char : colour
                                        // return it as a string and parse it in the java
            {
                $graphicsStr = $GRAPHICS_CHARACTER.getText();
            }
        ;

// "mimic:<FEAT_CODE>" - the terrain this one visually/mechanically mimics.
mimic
        returns[TerrainFlags mimicFlag]
        :   MIMIC TEXT {
                $mimicFlag = TerrainFlags.valueOf("FEAT_" + $TEXT.getText());
            }
        ;

// "priority:<value>" - display priority on the mini-map.
priority
        returns[int priorityNum]
        :   PRIORITY NUMBER
            {
                $priorityNum = Integer.parseInt($NUMBER.getText());
            }
        ;

// "flags:<TF_FLAG> [| <TF_FLAG> ...]" - this terrain's flags; can repeat
// per `terrain`'s `(flags {...})+` - see top-of-file problem #1, where a
// second flags: line replaces rather than merges with the first.
flags
        returns[Flag<TerrainFeatureFlags> flagsList]
        @init {
            $flagsList = new Flag<TerrainFeatureFlags>(TerrainFeatureFlags.class);
        }
        :   FLAGS flag1=TEXT {
                String raw1 = $flag1.getText().trim();
                TerrainFeatureFlags flagEnum1 = TerrainFeatureFlags.valueOf("TF_" + raw1);
                $flagsList.on(flagEnum1);
            } ('|' flag2=TEXT {
                String raw2 = $flag2.getText().trim();
                TerrainFeatureFlags flagEnum2 = TerrainFeatureFlags.valueOf("TF_" + raw2);
                $flagsList.on(flagEnum2);
            })*
        ;

// "digging:<index>" - digging difficulty index for this terrain.
digging
        returns[int diggingNum]
        :   DIGGING NUMBER
            {
                $diggingNum = Integer.parseInt($NUMBER.getText());
            }
        ;

// "walk-msg:<text>" - warning shown when walking onto this terrain.
walk_message
        returns[String walkMsgStr]
        :   WALK_MESSAGE TEXT
            {
                $walkMsgStr = $TEXT.getText();
            }
        ;

// "run-msg:<text>" - warning shown when running onto this terrain.
run_message
        returns[String runMsgStr]
        :   RUN_MESSAGE TEXT
            {
                $runMsgStr = $TEXT.getText();
            }
        ;

// "hurt-msg:<text>" - shown when this terrain damages the player.
hurt_message
        returns[String hurtMsgStr]
        :   HURT_MESSAGE TEXT
            {
                $hurtMsgStr = $TEXT.getText();
            }
        ;

// "die-msg:<text>" - shown if this terrain kills the player.
die_message
        returns[String dieMsgStr]
        :   DIE_MESSAGE TEXT
            {
                $dieMsgStr = $TEXT.getText();
            }
        ;

// "confused-msg:<text>" - shown when a confused monster tries to move into
// this (non-passable) terrain.
confused_message
        returns[String confMsgStr]
        :   CONFUSED_MESSAGE TEXT
            {
                $confMsgStr = $TEXT.getText();
            }
        ;

// "look-prefix:<text>" - text shown before this terrain's name when looking at it.
look_prefix
        returns[String prefixStr]
        :   LOOK_PREFIX TEXT
            {
                $prefixStr = $TEXT.getText();
            }
        ;

// "look-in-preposition:<text>" - preposition used when looking at the
// player's own square (e.g. "in" vs "on").
look_in_preposition
        returns[String prepositionStr]
        :   LOOK_IN_PREPOSITION TEXT
            {
                $prepositionStr = $TEXT.getText();
            }
        ;

// "resist-flag:<RF_FLAG>" - monster race flag for resisting this terrain
// (e.g. fire-resistant monsters ignoring lava damage).
resist_flag
        returns[MonsterRaceFlag resistFlag]
        :   RESIST_FLAG TEXT {
                $resistFlag = MonsterRaceFlag.valueOf("RF_" + $TEXT.getText().trim());
            }
        ;

// "desc:<text>" - flavour description.
desc
        returns[String descStr]
        @init {
            $descStr = "";
        }
        :   DESC TEXT {
                $descStr = $descStr + $TEXT.getText();
            }
        ;

// One full terrain record: code/name/graphics header, then any mix of the
// remaining directives in any order/quantity, accumulated into a Feature.
terrain
        returns[uk.co.jackoftrades.middle.cave.Feature feature]
        @init {
            TerrainFlags codeInit = TerrainFlags.FEAT_NONE;
            String nameInit = "";
            String graphicsStringInit = "";
            int priorityInit = 0;
            TerrainFlags mimicInit = TerrainFlags.FEAT_NONE;
            Flag<TerrainFeatureFlags> tfFlags = new Flag<>(TerrainFeatureFlags.class);
            int diggingInit = 0;
            String hurtInit = "";
            String walkInit = "";
            String runInit = "";
            String dieInit = "";
            String confInit = "";
            String prefixInit = "";
            String prepositionInit = "";
            MonsterRaceFlag resistInit = MonsterRaceFlag.RF_NONE;
            String descInit = "";
        }
        @after {
            char graphicsChar = graphicsStringInit.charAt(0);
            char graphicsColour = graphicsStringInit.charAt(2);
            AttributeColour colour = ColourType.getAttributeColour(graphicsColour, ColourTranslation.ATTR_FULL);

            int shopNum = -1;

            if (tfFlags.has(TerrainFeatureFlags.TF_SHOP)) {
                shopNum = Character.getNumericValue(graphicsChar);
            }

            AngbandDisplayCharacter dc = new AngbandDisplayCharacter(graphicsChar, colour);

            $feature = new Feature(codeInit, nameInit, descInit, mimicInit,
                                   priorityInit, shopNum, diggingInit, tfFlags,
                                   dc, walkInit, runInit, hurtInit, dieInit,
                                   confInit, prefixInit, prepositionInit,
                                   resistInit);
        }
        :   code {
                codeInit = $code.codeFlag;
            }
            name {
                nameInit = $name.nameStr;
            }
            graphics {
                graphicsStringInit = $graphics.graphicsStr;
            }
            (
                priority {
                    priorityInit = $priority.priorityNum;
                }
              | mimic {
                    mimicInit = $mimic.mimicFlag;
                }
              | (flags {
                    tfFlags.union($flags.flagsList);
                })+
              | digging {
                    diggingInit = $digging.diggingNum;
                }
              | walk_message {
                    walkInit = $walk_message.walkMsgStr;
                }
              | run_message {
                    runInit = $run_message.runMsgStr;
                }
              | hurt_message {
                    hurtInit = $hurt_message.hurtMsgStr;
                }
              | die_message {
                    dieInit = $die_message.dieMsgStr;
                }
              | confused_message {
                    confInit = $confused_message.confMsgStr;
                }
              | look_prefix {
                    prefixInit = $look_prefix.prefixStr;
                }
              | look_in_preposition {
                    prepositionInit = $look_in_preposition.prepositionStr;
                }
              | resist_flag {
                    resistInit = $resist_flag.resistFlag;
                }
              | desc {
                    descInit = descInit + $desc.descStr;
                }
            )*
        ;

// Top-level rule: the whole file is one or more terrain records.
file
        returns[List<uk.co.jackoftrades.middle.cave.Feature> features]
        @init{ $features = new ArrayList<>(); }
        :   (terrain {
                $features.add($terrain.feature);
            })+ EOF
        ;

// Comment line: '# ' or '##' to end of line, plus any blank lines immediately after.
COMMENT
        :   ('# ' | '##') (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   '\r'? '\n' -> skip
        ;

// CODE through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
CODE
        :   'code:'
        ;

NAME
        :   'name:'
        ;

GRAPHICS
        :   'graphics:'
        ;

MIMIC
        :   'mimic:'
        ;

PRIORITY
        :   'priority:'
        ;

FLAGS
        :   'flags:'
        ;

DIGGING
        :   'digging:'
        ;

WALK_MESSAGE
        :   'walk-msg:'
        ;

RUN_MESSAGE
        :   'run-msg:'
        ;

HURT_MESSAGE
        :   'hurt-msg:'
        ;

DIE_MESSAGE
        :   'die-msg:'
        ;

CONFUSED_MESSAGE
        :   'confused-msg:'
        ;

LOOK_PREFIX
        :   'look-prefix:'
        ;

LOOK_IN_PREPOSITION
        :   'look-in-preposition:'
        ;

RESIST_FLAG
        :   'resist-flag:'
        ;

DESC
        :   'desc:'
        ;

// A bare non-negative integer - used by priority:/digging:.
NUMBER
        :   ('0'..'9')+
        ;

// The full Angband colour-code alphabet (one char per colour, including
// the extended/bright variants) - used for graphics:'s colour field.
GRAPHICS_COLOUR
        :   'D' | 'w' | 's' | 'o' | 'r' | 'g' | 'b' | 'u' | 'W' | 'P' | 'y' | 'R'
                | 'G' | 'B' | 'U' | 'p' | 'v' | 't' | 'm' | 'Y' | 'i' | 'T' | 'V'
                | 'I' | 'M' | 'z' | 'Z'
        ;

// The display glyph for graphics: - includes digits 1-8 for the numbered
// shop entrances.
GRAPHICS_SYMBOL
        :   ' ' | '+' | '.' | '*' | '<' | '>' | '1' | '2' | '3' | '4' | '5' | '6'
                | '7' | '8' | '#' | ':' | '%' | '\''
        ;

// A whole "graphics:" value, e.g. "<:w" - parsed apart by character
// position in `terrain`'s @after (charAt(0)=symbol, charAt(2)=colour).
GRAPHICS_CHARACTER
        :   GRAPHICS_SYMBOL ':' GRAPHICS_COLOUR
        ;

// Free-running text used for most string fields (names, messages,
// descriptions) - letters, spaces, and common punctuation. Also matches
// flags:'s individual flag names (e.g. "LOS", "PASSABLE") since they fall
// within the same uppercase-letter range.
TEXT
        :   ('A'..'Z' | 'a'..'z' | ' ' | ',' | '.' | '_' | ';' | '?' | '!' | '-')+
        ;