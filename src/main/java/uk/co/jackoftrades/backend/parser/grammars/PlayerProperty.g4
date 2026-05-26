grammar PlayerProperty;

@header {
    import uk.co.jackoftrades.middle.player.PlayerProperty;
    import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyType;
    import uk.co.jackoftrades.middle.player.PlayerProperty.PlayerPropertyValue;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.combat.enums.Element;
    import uk.co.jackoftrades.frontend.entries.UIEntry;

    import java.util.List;
    import java.util.ArrayList;
}

@members { String typeOption; }

type
        :   TYPE TYPE_OPTIONS { typeOption = $TYPE_OPTIONS.getText(); }
        ;

code
        returns[ObjectFlag oFlag, PlayerFlag pFlag]
        @init {
            $oFlag = ObjectFlag.OF_NONE;
            $pFlag = PlayerFlag.PF_NONE;
        }
        :   CODE TEXT {
            String raw = $TEXT.getText();
            if (typeOption.equals("player"))
                $pFlag = PlayerFlag.valueOf("PF_" + raw);
            else if (typeOption.equals("object"))
                $oFlag = ObjectFlag.valueOf("OF_" + raw);
        };

bindui
        returns[UIEntry entry, Element elemEnum, Stats statEnum, String bindUIVal]
        @init{
            $elemEnum = Element.ELEM_NONE;
            $statEnum = Stats.STAT_NONE;
        }
        :   BINDUI TEXT {
            $entry = GameConstants.getUIEntry($TEXT.getText());
        } (TAG {
            String raw = $TAG.getText();
            String flag;
            if (raw.length() < 3) {
                flag = "ERROR";
            } else {
                flag = raw.substring(1, raw.length() -1);
                if (typeOption.equals("player"))
                    $elemEnum = Element.valueOf("ELEM_" + flag);
                else if (typeOption.equals("object"))
                    $statEnum = Stats.valueOf("STAT_" + flag);
            }
        })? BINDUIVAL {
            $bindUIVal = $BINDUIVAL.getText();
        };

name
        returns[String nameStr]
        :   NAME TEXT {$nameStr = $TEXT.getText(); }
        ;

desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

value
        returns[int valueInt]
        :   VALUE INTEGER {
            $valueInt = Integer.parseInt($INTEGER.getText());
        };

property
        returns[PlayerProperty playerProp]
        @init {
            PlayerProperty.PlayerPropertyType typeInit;

            ObjectFlag oFlagInit = ObjectFlag.OF_NONE;
            PlayerFlag pFlagInit = PlayerFlag.PF_NONE;
            UIEntry entryInit = null;
            Stats statInit = Stats.STAT_NONE;
            Element elemInit = Element.ELEM_NONE;
            String bindUIValInit = "";
            String nameInit = "";
            String descInit = "";
            PlayerPropertyValue valueInit = PlayerPropertyValue.NONE;
        }
        @after {
            $playerProp = new PlayerProperty(typeInit, pFlagInit, oFlagInit,
                                            entryInit, statInit, elemInit,
                                            bindUIValInit, nameInit, descInit,
                                            valueInit);
        }
        :   type {
                if (typeOption.equals("player"))
                    typeInit = PlayerPropertyType.PROP_TYPE_PLAYER;
                else if (typeOption.equals("object"))
                    typeInit = PlayerPropertyType.PROP_TYPE_OBJECT;
                else
                    typeInit = PlayerPropertyType.PROP_TYPE_ELEMENT;
            }
            (code {
                oFlagInit = $code.oFlag;
                pFlagInit = $code.pFlag;
            })?
            (bindui {
                entryInit = $bindui.entry;
                elemInit = $bindui.elemEnum;
                statInit = $bindui.statEnum;
                bindUIValInit = $bindui.bindUIVal;
            })?
            name { nameInit = $name.nameStr; }
            (desc { descInit = descInit + $desc.descStr; })*
            (value {
                switch ($value.valueInt) {
                    case -1:
                        valueInit = PlayerPropertyValue.VULNERABILITY;
                        break;
                    case 1:
                        valueInit = PlayerPropertyValue.RESISTANCE;
                        break;
                    case 3:
                        valueInit = PlayerPropertyValue.IMMUNITY;
                        break;
                    default:
                        valueInit = PlayerPropertyValue.NONE;
                }
            })?
        ;

file
        returns[List<PlayerProperty> properties]
        @init {
            $properties = new ArrayList<>();
        }
        :   (property {
                $properties.add($property.playerProp);
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

TYPE_OPTIONS
        :   'player' | 'object' | 'element'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

TEXT
        :   ('A'..'Z' | 'a'..'z' | ' ' | '_' | '.' | '0'..'9' | ',' | '(' | ')' | ';' | '[' | ']')+
        ;

TAG
        : '<' ('A'..'Z')+ '>'
        ;

BINDUIVAL
        :   ':' ('0' | '1') ':' (('-'? ('0'..'9')) | 'special')
        ;