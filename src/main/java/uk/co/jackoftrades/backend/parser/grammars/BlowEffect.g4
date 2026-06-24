// Parser+lexer for lib/gamedata/blow_effects.txt - every monster blow
// effect (NONE, HURT, POISON, ...): to-hit power contribution, power-eval
// weighting, monster-lore description/colours, what resists it, and how it
// translates to a "lash" projection. Cf. src/mon-init.c: struct
// file_parser eff_parser (mon-init.c:479).
//
// No significant problems found - `blowEffect`'s flexible loop (everything
// after the mandatory name/power pair is optional, in any order/quantity)
// correctly matches records with very different field sets (e.g. NONE has
// no desc/resist/effect-type at all, POISON has all of them).
//
// Minor: @header imports ObjectFlag but nothing in this grammar references
// it - harmless, just unused.

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

// "name:<CODE>" - starts a new blow effect record; must match the effect
// name used in monster.txt's blow: lines.
name
        returns[String nameStr]
        :   NAME UCASE { $nameStr = $UCASE.getText(); }
        ;

// "power:<value>" - contribution to monster to-hit chance calculation.
power
        returns[int powerInt]
        :   POWER INTEGER { $powerInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "eval:<value>" - weighting used by eval_blow_effect() for power evaluation.
eval
        returns[int evalInt]
        :   EVAL INTEGER { $evalInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "desc:<text>" - monster-recall description.
desc
        returns[String descStr]
        :   DESC LCASE { $descStr = $LCASE.getText(); }
        ;

// "lore-color-base:<colour name>" - base lore display colour.
loreColourBase
        returns[ColourType colType]
        :   LORE_COLOUR_BASE LCASE {
                String raw = $LCASE.getText();
                $colType = ColourType.getColourType(raw);
            }
        ;

// "lore-color-resist:<colour name>" - lore colour when the effect is resisted.
loreColourResist
        returns[ColourType colType]
        :   LORE_COLOUR_RESIST LCASE {
                String raw = $LCASE.getText();
                $colType = ColourType.getColourType(raw);
            }
        ;

// "lore-color-immune:<colour name>" - lore colour when strongly resisted/immune.
loreColourImmune
        returns[ColourType colType]
        :   LORE_COLOUR_IMMUNE LCASE {
                String raw = $LCASE.getText();
                $colType = ColourType.getColourType(raw);
            }
        ;

// "effect-type:<text>" - what kind of property protects from this effect
// (e.g. "element").
effectType
        returns[String type]
        :   EFFECT_TYPE LCASE { $type = $LCASE.getText(); }
        ;

// "resist:<CODE>" - the precise flag/resist that protects from this effect.
resist
        returns[String res]
        :   RESIST UCASE { $res = $UCASE.getText(); }
        ;

// "lash-type:<CODE>" - the projection type this translates to for the lash
// effect, looked up by lash code.
lashType
        returns[Projection projObj]
        :   LASH_TYPE UCASE {
                String raw = $UCASE.getText().toLowerCase();
                $projObj = GameConstants.lookupProjectionByLash(raw);
            }
        ;

// One full blow effect record: mandatory name/power, then any mix of the
// remaining directives in any order/quantity.
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

// Top-level rule: the whole file is one or more blow effect records.
file
        returns[List<BlowEffect> blowEffects]
        @init {
            $blowEffects = new ArrayList<>();
        }
        :   (blowEffect { $blowEffects.add($blowEffect.effect); })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// NAME through LASH_TYPE below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
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

// A bare non-negative integer.
INTEGER
        :   ('0'..'9')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for name:/resist:/lash-type:.
UCASE
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// Mixed-case text with spaces/hyphens/underscores - used for desc:/effect-type:
// and the colour-name fields.
LCASE
        :   ('a'..'z' | 'A'..'Z' | ' ' | '-' | '_')+
        ;