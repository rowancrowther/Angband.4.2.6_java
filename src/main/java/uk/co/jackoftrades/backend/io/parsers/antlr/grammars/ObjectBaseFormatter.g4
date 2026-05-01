grammar ObjectBaseFormatter;

@header {
            import java.util.ArrayList;

            import uk.co.jackoftrades.middle.objects.ObjectBase;
            import uk.co.jackoftrades.backend.colour.enums.ColourType;
            import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
            import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
            import uk.co.jackoftrades.middle.objects.enums.TValue;
        }

@members
        {
            ArrayList<ObjectBase> bases = new ArrayList<>();
        }

default returns[String tag, int value]
        :   DEFAULTB COLON NUMBER { $tag = "break-chance";
                                    $value = Integer.parseInt($NUMBER.getText()); }
        |   DEFAULTM COLON NUMBER { $tag = "max-stack";
                                    $value = Integer.parseInt($NUMBER.getText()); }
        ;

name    returns[String tValue, String nameString]
        :   NAME COLON LCASE { $tValue = $LCASE.getText(); }
                (COLON MCASE { $nameString = $MCASE.getText(); })?
        ;

graphics returns[ColourType attr]
        :   GRAPHICS COLON LCASE { $attr = ColourType.getColourType($LCASE.getText()); }
        ;

break   returns[int breakChance]
        :   BREAK COLON NUMBER { $breakChance = Integer.parseInt($NUMBER.getText()); }
        ;

maxstack    returns[int maxStack]
        :   MAXSTACK COLON NUMBER { $maxStack = Integer.parseInt($NUMBER.getText()); }
        ;

flag    returns[ String flagString ]
        :   kname=UCASE {
                $flagString = $kname.getText();
            }
        ;

flags   returns[ArrayList<String> flagStrings]
        @init {
            $flagStrings = new ArrayList<>();
        }
        :   FLAGS COLON f1=flag {
                $flagStrings.add($f1.flagString);
            }
            (OR f2=flag {
                $flagStrings.add($f2.flagString);
            })*
        ;

base    returns[ObjectBase objectBase]
        @init {
            $objectBase = new ObjectBase();
        }
        :   name {
                TValue tVal = TValue.fromName($name.tValue);
                $objectBase.settVal(tVal);
                if (null != $name.nameString)
                    $objectBase.setName($name.nameString);
            }
            graphics {
                $objectBase.setAttr($graphics.attr);
            }
            (bc=break {
                $objectBase.setBreakPerc($bc.breakChance);
            })?
            (maxstack {
                $objectBase.setMaxStack($maxstack.maxStack);
            })?
            (flags {
                    $objectBase.setFlags($flags.flagStrings);
            })*
        ;

file    returns[ArrayList<ObjectBase> bases, int defaultBreakChance, int defaultMaxStack]
        @init {
            $bases = new ArrayList<>();
        }
        :
           (def=default {
                if ($def.tag.equals("break-chance"))
                    $defaultBreakChance = $def.value;
                else
                    $defaultMaxStack = $def.value;
           })+
           (bs=base {
               $bases.add($bs.objectBase);
           })+
        ;

COLON   :   ':'
        ;

OR      :   ' | '
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL     :   '\r'? '\n' -> skip
        ;

NUMBER  :   ('0'..'9')+
        ;

DEFAULTB:   'default:break-chance'
        ;

DEFAULTM:   'default:max-stack'
        ;

NAME    :   'name'
        ;

GRAPHICS:   'graphics'
        ;

BREAK   :   'break'
        ;

MAXSTACK:   'max-stack'
        ;

HATES   :   'HATES_'
        ;

FLAGS   :   'flags'
        ;

SPACE   :   ' '
        ;

LCASE   :   ('a'..'z') ('a'..'z'|'-'|' ')*
        ;

UCASE   :   ('A'..'Z') ('A'..'Z'|'_')*
        ;

MCASE   :   ('A'..'Z'|'a'..'z') ('A'..'Z'|'a'..'z'|'-'|'~'|' ')*
        ;