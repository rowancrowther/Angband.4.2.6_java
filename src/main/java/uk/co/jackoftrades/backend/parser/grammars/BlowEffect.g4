grammar BlowEffect;

@header {
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
    import uk.co.jackoftrades.middle.game.Projection;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.BlowEffect;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

    import java.util.List;
    import java.util.ArrayList;
}

name
        returns[String nameStr]
        :   NAME UCASE { $nameStr = $UCASE.getText(); }
        ;

power
        returns[int powerInt]
        :   POWER INTEGER { $powerInt = Integer.parseInt($INTEGER.getText()); }
        ;

eval
        returns[int evalInt]
        :   EVAL INTEGER { $evalInt = Integer.parseInt($INTEGER.getText()); }
        ;

desc
        returns[String descStr]
        :   DESC LCASE { $descStr = $LCASE.getText(); }
        ;

loreColourBase
        returns[ColourType colType]
        :   LORE_COLOUR_BASE LCASE {
                String raw = $LCASE.getText();
                $colType = ColourType.getColourType(raw);
            }
        ;

loreColourResist
        returns[ColourType colType]
        :   LORE_COLOUR_RESIST LCASE {
                String raw = $LCASE.getText();
                $colType = ColourType.getColourType(raw);
            }
        ;

loreColourImmune
        returns[ColourType colType]
        :   LORE_COLOUR_IMMUNE LCASE {
                String raw = $LCASE.getText();
                $colType = ColourType.getColourType(raw);
            }
        ;

effectType
        returns[String type]
        :   EFFECT_TYPE LCASE { $type = $LCASE.getText(); }
        ;

resist
        returns[String res]
        :   RESIST UCASE { $res = $UCASE.getText(); }
        ;

lashType
        returns[Projection projObj]
        :   LASH_TYPE UCASE {
                String raw = $UCASE.getText().toLowerCase();
                $projObj = GameConstants.lookupProjectionByLash(raw);
            }
        ;

blowEffect
        returns[BlowEffect effect]
        @init {
            String nameInit = "";
            int powerInit = 0;
            int evalInit = 0;
            String descInit = "";
            ColourType baseInit = ColourType.COLOUR_TYPE_DARK;
            ColourType resistColInit = ColourType.COLOUR_TYPE_DARK;
            ColourType immuneInit = ColourType.COLOUR_TYPE_DARK;
            String effectInit = "";
            String resistInit = "";
            Projection lashTypeInit = null;
        }
        @after {
            $effect = new BlowEffect(nameInit, powerInit, evalInit, descInit, baseInit, resistColInit, immuneInit,
                                     effectInit, resistInit, lashTypeInit);
        }
        :   name { nameInit = $name.nameStr; }
            power { powerInit = $power.powerInt; }
        (   eval { evalInit = $eval.evalInt; }
        |   desc { descInit = $desc.descStr; }
        |   loreColourBase { baseInit = $loreColourBase.colType; }
        |   loreColourResist { resistColInit = $loreColourResist.colType; }
        |   loreColourImmune { immuneInit = $loreColourImmune.colType; }
        |   effectType { effectInit = $effectType.type; }
        |   resist { resistInit = $resist.res; }
        |   lashType { lashTypeInit = $lashType.projObj; }
        )+;

file
        returns[List<BlowEffect> blowEffects]
        @init {
            $blowEffects = new ArrayList<>();
        }
        :   (blowEffect { $blowEffects.add($blowEffect.effect); })+ EOF
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

POWER
        :   'power:'
        ;

EVAL
        :   'eval:'
        ;

DESC
        :   'desc:'
        ;

LORE_COLOUR_BASE
        :   'lore-color-base:'
        ;

LORE_COLOUR_RESIST
        :   'lore-color-resist:'
        ;

LORE_COLOUR_IMMUNE
        :   'lore-color-immune:'
        ;

EFFECT_TYPE
        :   'effect-type:'
        ;

RESIST
        :   'resist:'
        ;

LASH_TYPE
        :   'lash-type:'
        ;

INTEGER
        :   ('0'..'9')+
        ;

UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

LCASE
        :   ('a'..'z' | 'A'..'Z' | ' ' | '-' | '_')+
        ;