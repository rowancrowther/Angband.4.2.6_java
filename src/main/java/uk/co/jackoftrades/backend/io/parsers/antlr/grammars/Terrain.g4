grammar Terrain;

@header{
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.frontend.colour.enums.AttributeColour;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.cave.Feature;
    import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
    import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
}

code
        returns[TerrainFlags codeFlag]
        :   CODE TEXT {
            $codeFlag = TerrainFlags.valueOf("FEAT_" + $TEXT.getText());
        };

name
        returns[String nameStr]
        :   NAME TEXT {
            $nameStr = $TEXT.getText();
        };

graphics
        returns[AngbandDisplayCharacter displayCharacter]
        :   GRAPHICS attr=SINGLE_CHAR colour=TEXT {
            char displayChar = $attr.getText().charAt(0);
            char colourChar = $colour.getText().charAt(0);

            AttributeColour attrColour = ColourType.getAttributeColour(colourChar);
            $displayCharacter = new AngbandDisplayCharacter(displayChar, attrColour);
        };

mimic
        returns[TerrainFlags mimicFlag]
        :   MIMIC TEXT {
            $mimicFlag = TerrainFlags.valueOf("FEAT_" + $TEXT.getText());
        };

priority
        returns[int priorityNum]
        :   PRIORITY NUMBER {
            $priorityNum = Integer.parseInt($NUMBER.getText());
        };

flags
        returns[ArrayList<TerrainFeatureFlags> flagList]
        @init {
            $flagList = new ArrayList<>();
        }
        :   (FLAGS flag1=TEXT {
            $flagList.add(TerrainFeatureFlags.valueOf("TF_" + $flag1.getText().trim()));
        } (OR flag2=TEXT {
            $flagList.add(TerrainFeatureFlags.valueOf("TF_" + $flag2.getText().trim()));
        })*)+
        ;

walk_msg
        returns[String walkMsgStr]
        :   WALK_MSG TEXT {
            $walkMsgStr = $TEXT.getText();
        };

run_msg
        returns[String runMsgStr]
        :   RUN_MSG TEXT {
            $runMsgStr = $TEXT.getText();
        };

hurt_msg
        returns[String hurtMsgStr]
        :   HURT_MSG TEXT {
            $hurtMsgStr = $TEXT.getText();
        };

die_msg
        returns[String dieMsgStr]
        :   DIE_MSG TEXT {
            $dieMsgStr = $TEXT.getText();
        };

confused_msg
        returns[String confusedMsgStr]
        :   CONFUSED_MSG TEXT {
            $confusedMsgStr = $TEXT.getText();
        };

look_prefix
        returns[String lookPrefix]
        :   LOOK_PREFIX TEXT {
            $lookPrefix = $TEXT.getText();
        };

look_in_preposition
        returns[String lookInPreposition]
        :   LOOK_IN_PREP TEXT {
            $lookInPreposition = $TEXT.getText();
        };

resist_flag
        returns[MonsterRaceFlag monsterRaceFlag]
        :   RESIST_FLAG TEXT {
            $monsterRaceFlag = MonsterRaceFlag.valueOf("RF_" + $TEXT.getText());
        };

desc
        returns[String descStr]
        @init {
            $descStr = "";
        }
        :   (DESC TEXT {
            $descStr = $descStr + $TEXT.getText();
        })+;

digging
        returns[int diggingNum]
        :   DIGGING NUMBER {
            $diggingNum = Integer.parseInt($NUMBER.getText());
        }
        ;

feature
        returns[TerrainFeature terrain]
        @init {
            TerrainFlags codeInit = TerrainFlags.FEAT_NONE;
            String nameInit = "";
            AngbandDisplayCharacter adcharInit = null;
            TerrainFlags mimicInit = TerrainFlags.FEAT_NONE;
            int priorityInit = 0;
            Flag<TerrainFeatureFlags> flagsInit = null;
            String walkMsgInit = "";
            String runMsgInit = "";
            String hurtMsgInit = "";
            String dieMsgInit = "";
            String confusedMsgInit = "";
            String lookPrefixInit = "";
            String lookInPreposInit = "";
            MonsterRaceFlag resistFlagInit = MonsterRaceFlag.RF_NONE;
            String descInit = "";
            int diggingInit = 0;
            int shopNum = 0;
        }
        @after {
            $terrain = new TerrainFeature(
                    codeInit, nameInit, descInit, mimicInit,
                    priorityInit, shopNum, diggingInit,
                    flagsInit, adcharInit, walkMsgInit,
                    runMsgInit, hurtMsgInit, dieMsgInit,
                    confusedMsgInit, lookPrefixInit,
                    lookInPreposInit,
                    resistFlagInit);
        }
        :   (code {
                codeInit = $code.codeFlag;
            }
            name {
                nameInit = $name.nameStr;
            }
            graphics {
                adcharInit = $graphics.displayCharacter;
            }
            priority {
                priorityInit = $priority.priorityNum;
            }
            (mimic {
                mimicInit = $mimic.mimicFlag;
            })?
            (flags {
                flagsInit = new Flag<TerrainFeatureFlags>(TerrainFeatureFlags.class);

                for (TerrainFeatureFlags flag : $flags.flagList)
                    flagsInit.on(flag);
            })*
            ( digging {
                diggingInit = $digging.diggingNum;
            })?
            (walk_msg {
                walkMsgInit = $walk_msg.walkMsgStr;
            })?
            (run_msg {
                runMsgInit = $run_msg.runMsgStr;
            })?
            (hurt_msg {
                hurtMsgInit = $hurt_msg.hurtMsgStr;
            })?
            (die_msg {
                dieMsgInit = $die_msg.dieMsgStr;
            })?
            (confused_msg {
                confusedMsgInit = $confused_msg.confusedMsgStr;
            })?
            (look_prefix {
                lookPrefixInit = $look_prefix.lookPrefix;
            })?
            (look_in_preposition {
                lookInPreposInit = $look_in_preposition.lookInPreposition;
            })?
            (resist_flag {
                resistFlagInit = $resist_flag.monsterRaceFlag;
            })?
            (desc {
                descInit = $desc.descStr;
            })?) |
            ( code {
                codeInit = $code.codeFlag;
            }
            name {
                nameInit = $name.nameStr;
            }
            graphics {
                adcharInit = $graphics.displayCharacter;
            }
            priority {
                priorityInit = $priority.priorityNum;
            }
            (mimic {
                mimicInit = $mimic.mimicFlag;
            })?
            (flags {
                flagsInit = new Flag<TerrainFeatureFlags>(TerrainFeatureFlags.class);

                for (TerrainFeatureFlags flag : $flags.flagList)
                    flagsInit.on(flag);
            })*
            (look_prefix {
                lookPrefixInit = $look_prefix.lookPrefix;
            })?
            (desc {
                descInit = $desc.descStr;
            })?
            ( digging {
                diggingInit = $digging.diggingNum;
            })?
            (walk_msg {
                walkMsgInit = $walk_msg.walkMsgStr;
            })?
            (run_msg {
                runMsgInit = $run_msg.runMsgStr;
            })?
            (hurt_msg {
                hurtMsgInit = $hurt_msg.hurtMsgStr;
            })?
            (die_msg {
                dieMsgInit = $die_msg.dieMsgStr;
            })?
            (confused_msg {
                confusedMsgInit = $confused_msg.confusedMsgStr;
            })?
            (look_in_preposition {
                lookInPreposInit = $look_in_preposition.lookInPreposition;
            })?
            (resist_flag {
                resistFlagInit = $resist_flag.monsterRaceFlag;
            })?)
        ;

features
        returns[ArrayList<TerrainFeature> terrainFeatures]
        @init {
            $terrainFeatures = new ArrayList<>();
        }
        :   (feature{
               $terrainFeatures.add($feature.terrain);
            })+ EOF
        ;

COMMENT
        :   '#' ~':' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* ('\r'?'\n') -> skip
        ;

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

WALK_MSG
        :   'walk-msg:'
        ;

RUN_MSG
        :   'run-msg:'
        ;

HURT_MSG
        :   'hurt-msg:'
        ;

DIE_MSG
        :   'die-msg:'
        ;

CONFUSED_MSG
        :   'confused-msg:'
        ;

LOOK_PREFIX
        :   'look-prefix:'
        ;

LOOK_IN_PREP
        :   'look-in-preposition:'
        ;

RESIST_FLAG
        :   'resist-flag:'
        ;

DESC
        :   'desc:'
        ;

COLON
        :   ':'
        ;

OR
        :   '| '
        ;

/*
COLOUR_CHAR
        :   [DrWGviMwgPBtTzsbyUmVZouRpYI]
        ; */

SINGLE_CHAR
        :   [12345678 ,+'<>#:%*.] ':'
        ;

NUMBER
        :   [0-9]+
        ;

TEXT
        :   [A-Za-z0-9_.,;!? -]+
        ;