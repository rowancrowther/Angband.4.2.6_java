grammar ObjectProperty;

@header {
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.frontend.entries.UIEntry;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.ObjectProperty;
    import uk.co.jackoftrades.middle.objects.ObjectPropertyTypeWrapper;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
    import uk.co.jackoftrades.middle.objects.enums.TValue;

    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

@members {
    private static final Logger logger = LogManager.getLogger();
}

name
        returns[String nameStr]
        :   NAME LCASE {
                $nameStr = $LCASE.getText();
                logger.info("Name: " + $nameStr);
            }
        ;

type
        returns[ObjPropertyType typeObj]
        :   TYPE LCASE {
                String raw = $LCASE.getText();
                switch(raw) {
                    case "resistance":
                        raw = "resist";
                        break;

                    case "vulnerability":
                        raw = "vuln";
                        break;

                    case "immunity":
                        raw = "imm";
                        break;
                }

                $typeObj = ObjPropertyType.valueOf("OBJ_PROPERTY_" + raw.toUpperCase());
            }
        ;

subType
        returns[String subTypeStr]
        :   SUBTYPE LCASE { $subTypeStr = $LCASE.getText(); }
        ;

idType
        returns[String idTypeStr]
        :   ID_TYPE LCASE { $idTypeStr = $LCASE.getText(); }
        ;

code
        returns[String codeStr]
        :   CODE UCASE { $codeStr = $UCASE.getText(); }
        ;

power
        returns[int powerInt]
        :   POWER INTEGER { $powerInt = Integer.parseInt($INTEGER.getText()); }
        ;

mult
        returns[int multInt]
        :   MULT INTEGER { $multInt = Integer.parseInt($INTEGER.getText()); }
        ;

typeMult
        returns[TValue tVal, int value]
        :   TYPE_MULT ty=LCASE COLON val=INTEGER {
                String rawType = "TV_" + $ty.getText().toUpperCase().replace(' ', '_').replace("ARMOUR", "ARMOR");
                $tVal = TValue.valueOf(rawType);
                $value = Integer.parseInt($val.getText());
            }
        ;

adjective
        returns[String adjStr]
        :   ADJECTIVE LCASE { $adjStr = $LCASE.getText(); }
        ;

negAdjective
        returns[String negAdjStr]
        :   NEG_ADJECTIVE LCASE { $negAdjStr = $LCASE.getText(); }
        ;

msg
        returns[String msgStr]
        :   MSG {
                String raw = $MSG.getText();
                $msgStr = raw.substring(4);
            }
        ;

bindUI
        returns[UIEntry entryObj, ObjPropertyType propType, int parm1, int parm2]
        @init {
            $parm2 = 0;
            $propType = ObjPropertyType.OBJ_PROPERTY_NONE;
        }
        :   BINDUI entry=LCASE {
                $entryObj = GameConstants.getUIEntry($entry.getText());
            } (LESSTHAN flg=UCASE GREATERTHAN {
                String typeStr = $flg.getText();
                String testFlag = "STAT_" + typeStr.toUpperCase();
                boolean isStat = false;
                try {
                    Stats st = Stats.valueOf(testFlag);
                    isStat = true;
                } catch (Exception e) {
                    // Do nothing
                }
                if (isStat) {
                    $propType = ObjPropertyType.OBJ_PROPERTY_STAT;
                } else {
                    $propType = ObjPropertyType.OBJ_PROPERTY_RESIST;
                }

            })? COLON value=INTEGER {
                $parm1 = Integer.parseInt($value.getText());
            } (COLON index=INTEGER {
                $parm2 = Integer.parseInt($index.getText());
            })?
        ;

desc
        returns[String descStr]
        :   DESC { $descStr = $DESC.getText().substring(5); }
        ;

objProperty
        returns[ObjectProperty property]
        @init {
            ObjPropertyType typeInit = ObjPropertyType.OBJ_PROPERTY_NONE;
            String subTypeInit = "";
            String idTypeInit = "";
            String codeInit = "";
            ObjectPropertyTypeWrapper wrapperObject = null;
            int powerInit = 0;
            int multInit = 0;
            Map<TValue, Integer> typeMultInit = new HashMap<>();
            String nameInit = "";
            String adjInit = "";
            String negAdjInit = "";
            String message = "";
            String description = "";
            Map<UIEntry, ObjectPropertyTypeWrapper> uiEntriesMap = new HashMap<>();
        }
        @after {
            $property = new ObjectProperty(typeInit, subTypeInit, idTypeInit, wrapperObject, powerInit,
                                           multInit, typeMultInit, nameInit, adjInit, negAdjInit,
                                           message, description, uiEntriesMap);
        }
        :   name { nameInit = $name.nameStr; }
        (   type { typeInit = $type.typeObj; }
        |   code { codeInit = $code.codeStr; }
        |   subType { subTypeInit = $subType.subTypeStr; }
        |   idType { idTypeInit = $idType.idTypeStr; }
        |   power { powerInit = $power.powerInt; }
        |   mult { multInit = $mult.multInt; }
        |   typeMult {
                typeMultInit.put($typeMult.tVal, $typeMult.value);
            }
        |   adjective { adjInit = $adjective.adjStr; }
        |   negAdjective { negAdjInit = $negAdjective.negAdjStr; }
        |   msg { message = message + $msg.msgStr; })+
            (bindUI {
                UIEntry entry = $bindUI.entryObj;

                int p1 = $bindUI.parm1;
                int p2 = $bindUI.parm2;

                switch (typeInit) {
                    case OBJ_PROPERTY_STAT:
                    case OBJ_PROPERTY_MOD:
                        ObjectModifier modifier = ObjectModifier.valueOf("OM_" + codeInit);
                        wrapperObject = new ObjectPropertyTypeWrapper(typeInit, modifier);
                        break;

                    case OBJ_PROPERTY_FLAG:
                        ObjectFlag flag = ObjectFlag.valueOf("OF_" + codeInit);
                        wrapperObject = new ObjectPropertyTypeWrapper(typeInit, flag);
                        break;

                    case OBJ_PROPERTY_IGNORE:
                    case OBJ_PROPERTY_RESIST:
                    case OBJ_PROPERTY_VULN:
                    case OBJ_PROPERTY_IMM:
                        ElementEnum element = ElementEnum.valueOf("ELEM_" + codeInit);
                        wrapperObject = new ObjectPropertyTypeWrapper(typeInit, element);
                        break;

                    default:
                        String msg = "Invalid object property type found while parsing bindui.\n"
                                       + "Type: " + typeInit;
                        throw new InvalidTokenFoundDuringParse(msg);
                }

                wrapperObject.setValues(p1, p2);
                uiEntriesMap.put(entry, wrapperObject);
            })?
            (desc { description = description + $desc.descStr; })?
        ;

// immunity, vulnerability, resistance, ignore, flag, mod, stat

file
        returns[List<ObjectProperty> properties]
        @init {
            $properties = new ArrayList<>();
        }
        :   (objProperty {
                $properties.add($objProperty.property);
        })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

TYPE
        :   'type:'
        ;

SUBTYPE
        :   'subtype:'
        ;

ID_TYPE
        :   'id-type:'
        ;

CODE
        :   'code:'
        ;

POWER
        :   'power:'
        ;

MULT
        :   'mult:'
        ;

TYPE_MULT
        :   'type-mult:'
        ;

ADJECTIVE
        :   'adjective:'
        ;

NEG_ADJECTIVE
        :   'neg-adjective:'
        ;

MSG
        :   'msg:' ('A'..'Z' | 'a'..'z' | '.' | ' ' | '{' | '}' | '!')+
        ;

BINDUI
        :   'bindui:'
        ;

DESC
        :   'desc:' ('a'..'z' | 'A'..'Z' | ' ' | '(' | ')' | ',' | '\'')+
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

LCASE
        :   ('a'..'z' | '_' | '0'..'9' | ' ' | '-')+
        ;

UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

COLON
        :   ':'
        ;

LESSTHAN
        :   '<'
        ;

GREATERTHAN
        :   '>'
        ;