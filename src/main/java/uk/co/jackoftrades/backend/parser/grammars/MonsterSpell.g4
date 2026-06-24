// Parser+lexer for lib/gamedata/monster_spell.txt - every monster spell
// (cast effect): a name, message type/to-hit, one-or-more effects (each
// with optional coordinates/dice/expr), and per-power-level lore/message
// overrides. Cf. src/mon-init.c: struct file_parser mon_spell_parser
// (mon-init.c:987).
//
// No significant problems found - verified every record has at least one
// of msgt:/hit: (matching `monsterSpellType`'s mandatory `(msgt | hit)+`),
// and effect:'s field count (2-5 colon-separated fields across the file)
// matches the 4 alternatives `effect` provides. `effectBlock`'s subtype
// switch is the most complete of any grammar in this directory - it
// covers EST_PROJ/EST_TMD/EST_NOURISH/EST_MON_TMD/EST_SUMMON/EST_STAT/
// EST_ENCHANT/EST_EARTHQUAKE/EST_GLYPH/EST_TELEPORT/EST_TELEPORT_TO, with
// only EST_SUMMON_SPEC/EST_SHAPECHANGE missing (neither used by any
// current monster_spell.txt effect - the file's SUMMON effects resolve to
// plain EST_SUMMON), and even those fall through to a logged error rather
// than crashing or silently producing a wrong wrapper.

grammar MonsterSpell;

@header {
    import uk.co.jackoftrades.backend.numerics.Random;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.effect.Earthquake;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.effect.SummonType;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.MonTimed;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.enums.GlyphType;
    import uk.co.jackoftrades.middle.enums.MessageType;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.enums.TeleportEnum;
    import uk.co.jackoftrades.middle.monsters.MonsterSpellLevel;
    import uk.co.jackoftrades.middle.monsters.MonsterSpellType;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;

    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import java.util.ArrayList;
    import java.util.List;
}

@members {
    private static final Logger logger = LogManager.getLogger();
}

// "name:<RSF_CODE>" - starts a new spell record; must match a RSF_* constant.
name
        returns[String nameStr]
        :   NAME UCASE { $nameStr = $UCASE.getText(); }
        ;

// "msgt:<MSG_TYPE>" - message type/sound for casting this spell.
msgt
        returns[MessageType msgtEnum]
        :   MSGT UCASE {
                String raw = $UCASE.getText();
                $msgtEnum = MessageType.valueOf("MSG_" + raw);
            }
        ;

// "hit:<value>" - to-hit chance for spells that can be evaded.
hit
        returns[int hitInt]
        :   HIT INTEGER { $hitInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "effect:<TYPE>[:<SUBTYPE>[:<radius>[:<other>]]]" - one alternative per
// field count (1-4); subtype resolution happens later in `effectBlock`'s @after.
effect
        returns[EffectEnum parm1, String parm2, String parm3, String parm4]
        @init {
            String eeRaw = "";
            $parm2 = "";
            $parm3 = "";
            $parm4 = "";
        }
        @after {
            if (!eeRaw.isEmpty())
                $parm1 = EffectEnum.valueOf("EF_" + eeRaw);
        }
        :   EFFECT pa1=UCASE COLON pa2=UCASE COLON pa3=INTEGER COLON pa4=INTEGER {
                eeRaw = $pa1.getText();
                $parm2 = $pa2.getText();
                $parm3 = $pa3.getText();
                $parm4 = $pa4.getText();
            }
        |   EFFECT pb1=UCASE COLON pb2=UCASE COLON pb3=INTEGER {
                eeRaw = $pb1.getText();
                $parm2 = $pb2.getText();
                $parm3 = $pb3.getText();
            }
        |   EFFECT pc1=UCASE COLON pc2=UCASE {
                eeRaw = $pc1.getText();
                $parm2 = $pc2.getText();
            }
        |   EFFECT pd1=UCASE { eeRaw = $pd1.getText(); }
        ;

// "effect-yx:<y>:<x>" - sets a coordinate on the preceding effect:.
effectYX
        returns[int y, int x]
        :   EFFECT_YX yInt=INTEGER COLON xInt=INTEGER {
                $y = Integer.parseInt($yInt.getText());
                $x = Integer.parseInt($xInt.getText());
            }
        ;

// "dice:<dice string>" - dice for the preceding effect:. The whole
// "dice:<string>" text is one DICE token (see below), hence substring(5).
dice
        returns[String diceString]
        :   DICE {
                $diceString = $DICE.getText().substring(5);
            }
        ;

// "expr:<letter>:<EFB_BASE>:<operation>" - binds a dice-string variable
// used in the preceding dice: line. Unlike most other grammars here, the
// whole "expr:..." text is one EXPR token (see below) and split apart by
// this action rather than via separate sub-tokens.
expr
        returns[Expression e]
        :   EXPR {
                // Set the defaults
                char defChar = 'D';
                EffectBaseType defType = EffectBaseType.EFB_NONE;
                String defOperation = "";

                String[] exprStrings = $EXPR.getText().split(":");
                // Ignore the first string
                if (exprStrings.length > 1)
                    defChar = exprStrings[1].charAt(0);
                if (exprStrings.length > 2)
                    defType = EffectBaseType.valueOf("EFB_" + exprStrings[2]);
                if (exprStrings.length > 3)
                    defOperation = exprStrings[3];

                $e = new Expression(defChar, defType, defOperation);
            }
        ;

// Groups one effect: line with its optional effect-yx:/dice:/expr: into a
// single Effect, via the most complete subtype switch of any grammar in
// this directory (see top-of-file note).
effectBlock
        returns[Effect eff]
        @init {
            int yInit = 0;
            int xInit = 0;
            String diceStringInit = "";
            Expression expInit = null;
            EffectEnum parm1Init = EffectEnum.EF_NONE;
            String parm2Init = "";
            EffectSubTypeWrapper wrapperInit = null;
            String radiusInit = "";
            String parm4Init = "";
        }
        @after {
            if (!parm2Init.isEmpty()) {
                // Create the wrapper
                switch (parm1Init.getSubType()) {
                    case EST_PROJ:
                        wrapperInit = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + parm2Init));
                        break;

                    case EST_TMD:
                        wrapperInit = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + parm2Init));
                        break;

                    case EST_NOURISH:
                        wrapperInit = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + parm2Init));
                        break;

                    case EST_MON_TMD:
                        wrapperInit = new EffectSubTypeWrapper(MonTimed.valueOf("MON_TMD_" + parm2Init));
                        break;

                    case EST_SUMMON:
                        wrapperInit = new EffectSubTypeWrapper(SummonType.valueOf("SUM_" + parm2Init));
                        break;

                    case EST_STAT:
                        wrapperInit = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + parm2Init));
                        break;

                    case EST_ENCHANT:
                        wrapperInit = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + parm2Init));
                        break;

                    case EST_EARTHQUAKE:
                        wrapperInit = new EffectSubTypeWrapper(Earthquake.valueOf("QUAKE_" + parm2Init));
                        break;

                    case EST_GLYPH:
                        wrapperInit = new EffectSubTypeWrapper(GlyphType.valueOf("GLYPH_" + parm2Init));
                        break;

                    case EST_TELEPORT:
                        wrapperInit = new EffectSubTypeWrapper(TeleportEnum.valueOf("TELE_TELEPORT_" + parm2Init), false);
                        break;

                    case EST_TELEPORT_TO:
                        wrapperInit = new EffectSubTypeWrapper(TeleportEnum.valueOf("TELE_TELEPORT_" + parm2Init), true);
                        break;

                    default:
                        logger.error("Invalid subtype found in Effect Block.");
                }
            }

            Random radiusRnd = Random.parseStr(radiusInit);

            $eff = new Effect(parm1Init, yInit, xInit, diceStringInit,
                              parm1Init.getSubType(), wrapperInit,
                              radiusRnd, parm4Init, expInit);
        }
        :   effect {
                parm1Init = $effect.parm1;
                parm2Init = $effect.parm2;
                radiusInit = $effect.parm3;
                parm4Init = $effect.parm4;
            }
            (effectYX {
                yInit = $effectYX.y;
                xInit = $effectYX.x;
            })?
            (dice {
                diceStringInit = $dice.diceString;
            })?
            (expr {
                expInit = $expr.e;
            })?
        ;

// "power-cutoff:<value>" - the monster-power threshold above which this
// lore/message tier applies.
powerCutoff
        returns[int powerInt]
        :   POWER_CUTOFF INTEGER {
                $powerInt = Integer.parseInt($INTEGER.getText());
            }
        ;

// "lore:<text>" - monster-recall description for this spell tier; can
// repeat to build up multiple lines.
lore
        returns[String loreStr]
        :   LORE LCASE { $loreStr = $LCASE.getText(); }
        ;

// "lore-color-base:<colour name>" - base lore display colour.
loreColourBase
        returns[ColourType baseCol]
        :   LORE_COLOUR_BASE LCASE {
                $baseCol = ColourType.findColourType($LCASE.getText());
            }
        ;

// "lore-color-resist:<colour name>" - lore colour when resisted.
loreColourResist
        returns[ColourType resCol]
        :   LORE_COLOUR_RESIST LCASE {
                $resCol = ColourType.findColourType($LCASE.getText());
            }
        ;

// "lore-color-immune:<colour name>" - lore colour when immune.
loreColourImmune
        returns[ColourType immCol]
        :   LORE_COLOUR_IMMUNE LCASE {
                $immCol = ColourType.findColourType($LCASE.getText());
            }
        ;

// "message-save:<text>" - message shown when the player saves against this spell.
messageSave
        returns[String saveMsgStr]
        :   MESSAGE_SAVE LCASE {
                $saveMsgStr = $LCASE.getText();
            }
        ;

// "message-vis:<text>" - message shown when the casting monster is visible.
messageVis
        returns[String visMsgStr]
        :   MESSAGE_VIS LCASE {
                $visMsgStr = $LCASE.getText();
            }
        ;

// "message-invis:<text>" - message shown when the casting monster is unseen.
messageInvis
        returns[String invisMsgStr]
        :   MESSAGE_INVIS LCASE {
                $invisMsgStr = $LCASE.getText();
            }
        ;

// "message-miss:<text>" - message shown when the spell misses.
messageMiss
        returns[String missMsgStr]
        :   MESSAGE_MISS LCASE {
                $missMsgStr = $LCASE.getText();
            }
        ;

// One power tier's lore/message overrides: an optional power-cutoff:
// header, then any mix of lore/lore-colour-*/message-* in any order/quantity.
monsterSpellLevel
        returns[MonsterSpellLevel spellLevel]
        @init{
            int powerCutOffInit = 0;
            String loreDescInit = "";
            ColourType loreColTypeInit = ColourType.COLOUR_TYPE_DARK;
            ColourType loreResistColTypeInit = ColourType.COLOUR_TYPE_DARK;
            ColourType loreImmuneColTypeInit = ColourType.COLOUR_TYPE_DARK;
            String messageInit = "";
            String blindMessageInit = "";
            String missMessageInit = "";
            String saveMessageInit = "";
        }
        @after {
            $spellLevel = new MonsterSpellLevel(powerCutOffInit, loreDescInit,
                              loreColTypeInit, loreResistColTypeInit,
                              loreImmuneColTypeInit, messageInit,
                              blindMessageInit, missMessageInit,
                              saveMessageInit);
        }
        :   (powerCutoff {
                powerCutOffInit = $powerCutoff.powerInt;
            })?
        (   lore {
                loreDescInit = loreDescInit + $lore.loreStr;
            }
        |   loreColourBase {
                loreColTypeInit = $loreColourBase.baseCol;
            }
        |   loreColourResist {
                loreResistColTypeInit = $loreColourResist.resCol;
            }
        |   loreColourImmune {
                loreImmuneColTypeInit = $loreColourImmune.immCol;
            }
        |   messageVis {
                messageInit = messageInit + $messageVis.visMsgStr;
            }
        |   messageInvis {
                blindMessageInit = blindMessageInit + $messageInvis.invisMsgStr;
            }
        |   messageMiss {
                missMessageInit = missMessageInit + $messageMiss.missMsgStr;
            }
        |   messageSave {
                saveMessageInit = saveMessageInit + $messageSave.saveMsgStr;
            })+
        ;

// One full spell record: name, then one-or-more of msgt:/hit: (any order),
// zero-or-more effect blocks, then zero-or-more per-power-level lore/
// message tiers.
monsterSpellType
        returns[MonsterSpellType spellType]
        @init {
            MonsterSpell indexInit = MonsterSpell.RSF_NONE;
            MessageType msgInit = MessageType.MSG_NONE;
            int hitInit = 0;
            List<Effect> effectsInit = new ArrayList<>();
            List<MonsterSpellLevel> levels = new ArrayList<>();
        }
        @after {
            $spellType = new MonsterSpellType(indexInit, msgInit,
                             hitInit, effectsInit, levels);
        }
        :   name {
                String raw = $name.nameStr;
                indexInit = MonsterSpell.valueOf("RSF_" + raw);
            }
        (   msgt {
                msgInit = $msgt.msgtEnum;
            }
        |   hit {
                hitInit = $hit.hitInt;
            })+
            (effectBlock {
                effectsInit.add($effectBlock.eff);
            })*
            (monsterSpellLevel {
                levels.add($monsterSpellLevel.spellLevel);
            })*
        ;

// Top-level rule: the whole file is one or more spell records.
file
        returns[List<MonsterSpellType> spellTypes]
        @init {
            $spellTypes = new ArrayList<>();
        }
        :   (monsterSpellType {
                $spellTypes.add($monsterSpellType.spellType);
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

// NAME through MESSAGE_MISS below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

MSGT
        :   'msgt:'
        ;

HIT
        :   'hit:'
        ;

EFFECT
        :   'effect:'
        ;

EFFECT_YX
        :   'effect-yx:'
        ;

// "dice:" plus the entire dice-string expression as one token (cf.
// Random.g4's `dice` rule, the general-purpose version of this mini-language).
DICE
        :   'dice:' ('0'..'9' | 'd' | '+' | '-' | 'M' | ',' | 'S' | 'D' | 'B' | '$')+
        ;

// "expr:" plus the whole "<letter>:<BASE>:<operation>" expression as one
// token, split apart by `expr`'s action via String.split(":").
EXPR
        :   'expr:' ('S' | 'D' | 'B' | 'M') ':'? ('A'..'Z' | '_')* ':'? (' ' | '0'..'9' | '+' | '/' | '-' | '*')*
        ;

POWER_CUTOFF
        :   'power-cutoff:'
        ;


LORE
        :   'lore:'
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

MESSAGE_SAVE
        :   'message-save:'
        ;

MESSAGE_VIS
        :   'message-vis:'
        ;

MESSAGE_INVIS
        :   'message-invis:'
        ;

MESSAGE_MISS
        :   'message-miss:'
        ;

// A (possibly negative) literal integer.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// An UPPER_CASE_WITH_UNDERSCORES symbolic name - used for name:/msgt:/effect:'s
// type and subtype segments.
UCASE
        :   ('A'..'Z' | '_')+
        ;

// Free-running text - used for lore:/message-*:'s text fields, including
// "{pronoun}"-style placeholder syntax.
LCASE
        :   ('a'..'z' | ' ' | '+' | '-' | '/' | '*' | 'A'..'Z' | '{' | '}'
            | '.' | ',' | '!' | '\'')+
        ;

// Field separator within effect:/effect-yx: lines.
COLON
        :   ':'
        ;
