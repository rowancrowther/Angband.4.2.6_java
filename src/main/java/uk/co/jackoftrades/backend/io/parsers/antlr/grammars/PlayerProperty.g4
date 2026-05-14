grammar PlayerProperty;

@header
        {
            import uk.co.jackoftrades.middle.game.GameEnginee.Game;
            import uk.co.jackoftrades.middle.enums.StatElementEnum;
            import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
            import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
            import uk.co.jackoftrades.middle.player.PlayerProperty;
            import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyType;
            import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyValue;
            import uk.co.jackoftrades.frontend.entries.UIEntry;

            import java.util.ArrayList;
        }


type
        returns[PlayerPropertyType ppType]
        : TYPE STRING
        {
            String ppt = "PROP_TYPE_" + $STRING.getText().toUpperCase();
            $ppType = PlayerPropertyType.valueOf(ppt);
        };

code
        returns[ObjectFlag objectFlag, PlayerFlag playerFlag]
        :   CODE STRING {
            String flag = $STRING.getText();
            ObjectFlag oFlag = ObjectFlag.OF_NONE;
            PlayerFlag pFlag = PlayerFlag.PF_NONE;
            boolean isObjectFlag;
            try {
                $objectFlag = ObjectFlag.valueOf("OF_" + flag);
                isObjectFlag = true;
            } catch (Exception e) {
                isObjectFlag = false;
            }

            if (isObjectFlag) {
                $objectFlag = ObjectFlag.valueOf("OF_" + flag);
                $playerFlag = pFlag;
            } else {
                $objectFlag = oFlag;
                $playerFlag = PlayerFlag.valueOf("PF_" + flag);
            }
        };

bindui
        returns[UIEntry uiEntry, StatElementEnum tab, boolean passType, PlayerPropertyValue passValue, boolean special]
        :   BINDUI STRING uiEntryTAG=BINDUITAG?
            COLON passTypeNum=NUMBER
            COLON (SPECIAL | (neg=MINUS? valNum=NUMBER))
        {
            String uiEntryName = $STRING.getText();
            $uiEntry = Game.getUIEntry(uiEntryName);

            if ($uiEntryTAG != null) {
                String tag = $uiEntryTAG.getText().toUpperCase();
                String innerTag = tag.substring(1, tag.length() - 1);
                try {
                    $tab = StatElementEnum.valueOf("STAT_" + innerTag);
                } catch (Exception e) {
                    $tab = StatElementEnum.valueOf("ELEM_" + innerTag);
                }
            }

            int negate = 1;

            if ($neg != null) {
                negate = -1;
            }

            $passType = (Integer.parseInt($passTypeNum.getText()) != 0);

            if ($SPECIAL != null) {
                $special = true;
                $passValue = PlayerPropertyValue.NONE;
            } else {
                $special = false;
                int passVal = Integer.parseInt($valNum.getText());
                if (passVal == 3)
                    $passValue = PlayerPropertyValue.IMMUNITY;
                else {
                    if (negate == -1)
                        $passValue = PlayerPropertyValue.VULNERABILITY;
                    else
                        $passValue = PlayerPropertyValue.RESISTANCE;
                }
            }
        };

name
        returns[String propertyName]
        :   NAME STRING {
            $propertyName = $STRING.getText();
        };

desc
        returns[String description]
        @init {
            $description = "";
        }
        :   (DESC STRING {
                $description = $description + $STRING.getText();
            })+
        ;

value
        returns[PlayerPropertyValue val]
        :   VALUE MINUS? NUMBER {
            if ($MINUS != null) {
                $val = PlayerPropertyValue.VULNERABILITY;
            } else {
                if (Integer.parseInt($NUMBER.getText()) == 1)
                    $val = PlayerPropertyValue.RESISTANCE;
                else
                    $val = PlayerPropertyValue.IMMUNITY;
            }
        };

playerProperty
        returns [PlayerProperty property]
        :   type
            code?
            bindui?
            name
            desc*
            value? {
                PlayerPropertyType ppType = $type.ppType;

                ObjectFlag oCode;
                PlayerFlag pCode;
                if ($code.ctx != null) {
                    oCode = $code.objectFlag;
                    pCode = $code.playerFlag;
                } else {
                    oCode = ObjectFlag.OF_NONE;
                    pCode = PlayerFlag.PF_NONE;
                }

                UIEntry ent = null;
                StatElementEnum statElement = null;
                boolean passType = false;
                PlayerPropertyValue passValue = null;
                boolean special = false;
                if ($bindui.ctx != null) {
                    ent = $bindui.uiEntry;
                    statElement = $bindui.tab;
                    passType = $bindui.passType;
                    passValue = $bindui.passValue;
                    special = $bindui.special;
                }

                String playerName = $name.propertyName;
                String description = $desc.description;

                PlayerPropertyValue val = null;
                if ($value.ctx != null) {
                    val = $value.val;
                }

                $property = new PlayerProperty(ppType, pCode, oCode, ent,
                                               statElement, passType, passValue,
                                               special, playerName, description,
                                               val);
        };

playerProperties
        returns[ArrayList<PlayerProperty> properties]
        @init{
            $properties = new ArrayList<>();
        }
        :   (playerProperty {
                $properties.add($playerProperty.property);
            })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

TYPE
        :   'type:'
        ;

CODE
        :   'code:'
        ;

BINDUI
        :   'bindui:'
        ;

NAME
        :   'name:'
        ;

DESC
        :   'desc:'
        ;

VALUE
        :   'value:'
        ;

NUMBER
        :   [0-9]+
        ;

SPECIAL
        :   'special'
        ;

COLON
        :   ':'
        ;

STRING
        :   [a-zA-Z0-9_.,()[\]; ]+
        ;

BINDUITAG
        :   '<' [A-Z]+ '>'
        ;

MINUS
        :   '-'
        ;