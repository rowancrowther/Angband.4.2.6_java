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

code
        returns[TerrainFlags codeFlag]
        :   CODE TEXT {
                $codeFlag = TerrainFlags.valueOf("FEAT_" + $TEXT.getText());
            }
        ;

name
        returns[String nameStr]
        :   NAME TEXT { $nameStr = $TEXT.getText(); }
        ;

graphics
        returns[String graphicsStr]
        :   GRAPHICS GRAPHICS_CHARACTER // Graphics_character is char : colour
                                        // return it as a string and parse it in the java
            {
                $graphicsStr = $GRAPHICS_CHARACTER.getText();
            }
        ;

mimic
        returns[TerrainFlags mimicFlag]
        :   MIMIC TEXT {
                $mimicFlag = TerrainFlags.valueOf("FEAT_" + $TEXT.getText());
            }
        ;

priority
        returns[int priorityNum]
        :   PRIORITY NUMBER
            {
                $priorityNum = Integer.parseInt($NUMBER.getText());
            }
        ;

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

digging
        returns[int diggingNum]
        :   DIGGING NUMBER
            {
                $diggingNum = Integer.parseInt($NUMBER.getText());
            }
        ;

walk_message
        returns[String walkMsgStr]
        :   WALK_MESSAGE TEXT
            {
                $walkMsgStr = $TEXT.getText();
            }
        ;

run_message
        returns[String runMsgStr]
        :   RUN_MESSAGE TEXT
            {
                $runMsgStr = $TEXT.getText();
            }
        ;

hurt_message
        returns[String hurtMsgStr]
        :   HURT_MESSAGE TEXT
            {
                $hurtMsgStr = $TEXT.getText();
            }
        ;

die_message
        returns[String dieMsgStr]
        :   DIE_MESSAGE TEXT
            {
                $dieMsgStr = $TEXT.getText();
            }
        ;

confused_message
        returns[String confMsgStr]
        :   CONFUSED_MESSAGE TEXT
            {
                $confMsgStr = $TEXT.getText();
            }
        ;

look_prefix
        returns[String prefixStr]
        :   LOOK_PREFIX TEXT
            {
                $prefixStr = $TEXT.getText();
            }
        ;

look_in_preposition
        returns[String prepositionStr]
        :   LOOK_IN_PREPOSITION TEXT
            {
                $prepositionStr = $TEXT.getText();
            }
        ;

resist_flag
        returns[MonsterRaceFlag resistFlag]
        :   RESIST_FLAG TEXT {
                $resistFlag = MonsterRaceFlag.valueOf("RF_" + $TEXT.getText().trim());
            }
        ;

desc
        returns[String descStr]
        @init {
            $descStr = "";
        }
        :   DESC TEXT {
                $descStr = $descStr + $TEXT.getText();
            }
        ;

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
                    tfFlags = $flags.flagsList;
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

file
        returns[List<uk.co.jackoftrades.middle.cave.Feature> features]
        @init{ $features = new ArrayList<>(); }
        :   (terrain {
                $features.add($terrain.feature);
            })+ EOF
        ;

COMMENT
        :   ('# ' | '##') (~'\n')* '\n'+ -> skip
        ;

EOL
        :   '\r'? '\n' -> skip
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

NUMBER
        :   ('0'..'9')+
        ;

GRAPHICS_COLOUR
        :   'D' | 'w' | 's' | 'o' | 'r' | 'g' | 'b' | 'u' | 'W' | 'P' | 'y' | 'R'
                | 'G' | 'B' | 'U' | 'p' | 'v' | 't' | 'm' | 'Y' | 'i' | 'T' | 'V'
                | 'I' | 'M' | 'z' | 'Z'
        ;

GRAPHICS_SYMBOL
        :   ' ' | '+' | '.' | '*' | '<' | '>' | '1' | '2' | '3' | '4' | '5' | '6'
                | '7' | '8' | '#' | ':' | '%' | '\''
        ;

GRAPHICS_CHARACTER
        :   GRAPHICS_SYMBOL ':' GRAPHICS_COLOUR
        ;

TEXT
        :   ('A'..'Z' | 'a'..'z' | ' ' | ',' | '.' | '_' | ';' | '?' | '!' | '-')+
        ;