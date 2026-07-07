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

// Parser for lib/gamedata/terrain.txt, paired with TerrainFeatureLexer. Each
// directive rule strips its keyword and returns the raw String payload; the
// `terrain` rule accumulates one record's directives - the mandatory
// code/name/graphics header, then any mix of the optional directives in any
// order and quantity - into a TerrainFeatureParseRecord, and `file` collects the
// record-count header plus every record. No interpretation happens here: enum
// resolution and number parsing are deferred to TerrainFeatureAssembler, so the
// rules deal only in raw text. Cf. src/init.c struct file_parser feat_parser.
//
// @author Rowan Crowther

parser grammar TerrainFeatureGrammar;
options { tokenVocab = TerrainFeatureLexer; }

@header
        {
            import uk.co.jackoftrades.backend.parser.terrainfeature.TerrainFeatureParseRecord;

            import java.util.List;
            import java.util.ArrayList;
        }

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// "code:<FEAT_CODE>" - starts a new terrain record; must match a code from
// list-terrain.h.
code
        returns[String codeFlag, int lineNo]
        :   CODE FLAG FLAG_EOL {
                $codeFlag = $FLAG.getText();
                $lineNo = $start.getLine();
            }
        ;

// "name:<text>" - the terrain's printable name.
name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

// "graphics:<symbol>:<colour>" - the shared AngbandDisplayCharacter tokens
// (GLYPH_VALUES / GLYPH_COLON_SWITCH / COLOUR_VALUES) split the glyph from the
// colour in this rule, so each is returned already separated (the ':' after the
// glyph is the switch token, which is why a ':'-glyph terrain still lexes).
graphics
        returns[String glyph, String colour]
        :   GRAPHICS gly=GLYPH_VALUES GLYPH_COLON_SWITCH col=COLOUR_VALUES
            {
                $glyph = $gly.getText();
                $colour = $col.getText();
            }
        ;

// "mimic:<FEAT_CODE>" - the terrain this one visually/mechanically mimics.
mimic
        returns[String mimicFlag]
        :   MIMIC FLAG FLAG_EOL {
                $mimicFlag = $FLAG.getText();
            }
        ;

// "priority:<value>" - display priority on the mini-map.
priority
        returns[String priorityNum]
        :   PRIORITY INTEGER
            {
                $priorityNum = $INTEGER.getText();
            }
        ;

// "flags:<TF_FLAG> [| <TF_FLAG> ...]" - this terrain's flags. May appear on
// several lines: `terrain`'s `(flags {...})+` addAll's each line's list into one
// accumulated set, so repeated flags: lines merge rather than replace.
flags
        returns[List<String> flagsList]
        @init {
            $flagsList = new ArrayList<>();
        }
        :   FLAGS f1=FLAG {
                $flagsList.add($f1.getText());
            }(FLAG_OR f2=FLAG {
                $flagsList.add($f2.getText());
            })* FLAG_EOL
        ;

// "digging:<index>" - digging difficulty index for this terrain.
digging
        returns[String diggingNum]
        :   DIGGING INTEGER
            {
                $diggingNum = $INTEGER.getText();
            }
        ;

// "walk-msg:<text>" - warning shown when walking onto this terrain.
walk_message
        returns[String walkMsgStr]
        :   WALK_MESSAGE STRING
            {
                $walkMsgStr = $STRING.getText();
            }
        ;

// "run-msg:<text>" - warning shown when running onto this terrain.
run_message
        returns[String runMsgStr]
        :   RUN_MESSAGE STRING
            {
                $runMsgStr = $STRING.getText();
            }
        ;

// "hurt-msg:<text>" - shown when this terrain damages the player.
hurt_message
        returns[String hurtMsgStr]
        :   HURT_MESSAGE STRING
            {
                $hurtMsgStr = $STRING.getText();
            }
        ;

// "die-msg:<text>" - shown if this terrain kills the player.
die_message
        returns[String dieMsgStr]
        :   DIE_MESSAGE STRING
            {
                $dieMsgStr = $STRING.getText();
            }
        ;

// "confused-msg:<text>" - shown when a confused monster tries to move into
// this (non-passable) terrain.
confused_message
        returns[String confMsgStr]
        :   CONFUSED_MESSAGE STRING
            {
                $confMsgStr = $STRING.getText();
            }
        ;

// "look-prefix:<text>" - text shown before this terrain's name when looking at it.
look_prefix
        returns[String prefixStr]
        :   LOOK_PREFIX STRING
            {
                $prefixStr = $STRING.getText();
            }
        ;

// "look-in-preposition:<text>" - preposition used when looking at the
// player's own square (e.g. "in" vs "on").
look_in_preposition
        returns[String prepositionStr]
        :   LOOK_IN_PREPOSITION STRING
            {
                $prepositionStr = $STRING.getText();
            }
        ;

// "resist-flag:<RF_FLAG>" - monster race flag for resisting this terrain
// (e.g. fire-resistant monsters ignoring lava damage).
resist_flag
        returns[List<String> resistFlag]
        @init {
            $resistFlag = new ArrayList<>();
        }
        :   RESIST_FLAG f1=FLAG {
                $resistFlag.add($f1.getText());
            } (FLAG_OR f2=FLAG {
                $resistFlag.add($f2.getText());
            })* FLAG_EOL
        ;

// "desc:<text>" - flavour description.
desc
        returns[String descStr]
        @init {
            StringBuilder sb = new StringBuilder();
        }
        @after {
            $descStr = sb.toString();
        }
        :   (DESC STRING {
                sb.append($STRING.getText());
            })+
        ;

// One full terrain record: code/name/graphics header, then any mix of the
// remaining directives in any order/quantity, accumulated into a Feature.
terrain
        returns[TerrainFeatureParseRecord feature]

        @init {
            String codeInit = "";
            String nameInit = "";
            String glyph = "";
            String colour = "";
            String priorityInit = "";
            String mimicInit = "";
            List<String> tfFlags = new ArrayList<>();
            String diggingInit = "";
            String hurtInit = "";
            String walkInit = "";
            String runInit = "";
            String dieInit = "";
            String confInit = "";
            String prefixInit = "";
            String prepositionInit = "";
            List<String> resistInit = new ArrayList<>();
            String descInit = "";
            int lineNo = 0;
        }
        @after {
            $feature = new TerrainFeatureParseRecord(
                                   codeInit, nameInit, descInit, mimicInit,
                                   priorityInit, diggingInit, tfFlags,
                                   glyph, colour, walkInit, runInit, hurtInit, dieInit,
                                   confInit, prefixInit, prepositionInit,
                                   resistInit, lineNo);
        }
        :   code {
                codeInit = $code.codeFlag;
                lineNo = $code.lineNo;
            }
            name {
                nameInit = $name.nameStr;
            }
            graphics {
                glyph = $graphics.glyph;
                colour = $graphics.colour;
            }
            (
                priority {
                    priorityInit = $priority.priorityNum;
                }
              | mimic {
                    mimicInit = $mimic.mimicFlag;
                }
              | (flags {
                    tfFlags.addAll($flags.flagsList);
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
                    resistInit.addAll($resist_flag.resistFlag);
                }
              | desc {
                    descInit = descInit + $desc.descStr;
                }
            )*
        ;

// Top-level rule: the whole file is one or more terrain records.
file
        returns[List<TerrainFeatureParseRecord> features,
                String declaredRecordCount]
        @init{ $features = new ArrayList<>(); }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (terrain {
                $features.add($terrain.feature);
            })+ EOF
        ;