// Parser+lexer for lib/gamedata/brand.txt - every elemental brand an object
// can carry (Acid Brand, Lightning Brand, ...): damage multipliers, power
// weighting, combat verb, and which monster race flags resist/are extra
// vulnerable to it. Cf. src/obj-init.c: struct file_parser brand_parser
// (obj-init.c:1021).
//
// No significant problems found - unlike Slay.g4's race-flag/base (which
// are documented as mutually exclusive), brand.txt's resist-flag and
// vuln-flag are independent and several real entries (FIRE_3/COLD_3/
// FIRE_2/COLD_2) genuinely have both, which `brand`'s unordered
// `(... | resist_flag {...} | vuln_flag {...} | ...)*` loop handles
// correctly. Every entry also has exactly one of each of name/verb/
// multiplier/o-multiplier/power despite the grammar not requiring a fixed
// count of any of them.

grammar Brand;

@header {
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.objects.Brand;

    import java.util.List;
    import java.util.ArrayList;
}

// "code:<CODE>" - starts a new brand record; referenced by artifact.txt/
// ego_item.txt/object.txt/player_timed.txt to grant the brand.
code
        returns[String codeStr]
        :   CODE FLAG { $codeStr = $FLAG.getText(); }
        ;

// "name:<text>" - the brand's element name (should match projection.txt's
// name for the same element).
name
        returns[String nameStr]
        :   NAME TEXT { $nameStr = $TEXT.getText(); }
        ;

// "multiplier:<value>" - damage-dice multiplier (normal combat).
multiplier
        returns[int multNum]
        :   MULTIPLIER NUMBER { $multNum = Integer.parseInt($NUMBER.getText()); }
        ;

// "o-multiplier:<value>" - damage-dice multiplier (O-combat variant).
o_multiplier
        returns[int oMultNum]
        :   O_MULTIPLIER NUMBER { $oMultNum = Integer.parseInt($NUMBER.getText()); }
        ;

// "power:<value>" - weighting in object power calculations (100 = neutral).
power
        returns[int powerNum]
        :   POWER NUMBER { $powerNum = Integer.parseInt($NUMBER.getText()); }
        ;

// "verb:<text>" - verb used when hitting a susceptible monster.
verb
        returns[String verbStr]
        :   VERB TEXT { $verbStr = $TEXT.getText(); }
        ;

// "resist-flag:<RF_FLAG>" - monster race flag for resistance to this brand's element.
resist_flag
        returns[MonsterRaceFlag resFlag]
        :   RESIST_FLAG FLAG { $resFlag = MonsterRaceFlag.valueOf("RF_" + $FLAG.getText().trim()); }
        ;

// "vuln-flag:<RF_FLAG>" - monster race flag for special vulnerability to
// this brand's element. Independent of resist_flag - several real entries
// (e.g. FIRE_3) set both (see top-of-file note).
vuln_flag
        returns[MonsterRaceFlag vulnFlag]
        :   VULN_FLAG FLAG { $vulnFlag = MonsterRaceFlag.valueOf("RF_" + $FLAG.getText().trim()); }
        ;

// One full brand record: code/name header, then any mix of multiplier/
// o-multiplier/power/verb/resist-flag/vuln-flag in any order/quantity.
brand
        returns[Brand brandObj]
        @init {
            String codeInit = "";
            String nameInit = "";
            String verbInit = "";
            MonsterRaceFlag resFlagInit = MonsterRaceFlag.RF_NONE;
            MonsterRaceFlag vulnFlagInit = MonsterRaceFlag.RF_NONE;
            int multInit = 0;
            int oMultInit = 0;
            int powerInit = 0;
        }
        @after {
            $brandObj = new Brand(codeInit, nameInit, verbInit, resFlagInit,
                                  vulnFlagInit, multInit, oMultInit, powerInit);
        }
        :   code { codeInit = $code.codeStr; }
            name { nameInit = $name.nameStr; }
            ( multiplier { multInit = $multiplier.multNum; }
            | o_multiplier { oMultInit = $o_multiplier.oMultNum; }
            | power { powerInit = $power.powerNum; }
            | verb {verbInit = $verb.verbStr; }
            | resist_flag { resFlagInit = $resist_flag.resFlag; }
            | vuln_flag { vulnFlagInit = $vuln_flag.vulnFlag; })*
        ;

// Top-level rule: the whole file is one or more brand records.
file
        returns[List<Brand> brands]
        @init {
            $brands = new ArrayList<>();
        }
        :   (brand {
            $brands.add($brand.brandObj);
        })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// CODE through VULN_FLAG below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
CODE
        :   'code:'
        ;

NAME
        :   'name:'
        ;

MULTIPLIER
        :   'multiplier:'
        ;

O_MULTIPLIER
        :   'o-multiplier:'
        ;

POWER
        :   'power:'
        ;

VERB
        :   'verb:'
        ;

RESIST_FLAG
        :   'resist-flag:'
        ;

VULN_FLAG
        :   'vuln-flag:'
        ;

// A bare non-negative integer.
NUMBER
        :   ('0'..'9')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for code:/
// resist-flag:/vuln-flag:.
FLAG
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// Free-running lowercase text (no spaces) - used for name:/verb: (both are
// always single words, e.g. "acid"/"dissolve").
TEXT
        :   ('a'..'z')+
        ;