grammar Class;

@header {
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.ItemObject;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.player.StartItem;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.magic.MagicBook;
    import uk.co.jackoftrades.middle.magic.MagicSpell;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
    import uk.co.jackoftrades.middle.magic.MagicRealm;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.middle.Effect;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.player.PlayerClass;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
    import uk.co.jackoftrades.middle.magic.ClassMagic;

    import java.util.Map;
    import java.util.HashMap;
    import java.util.List;
    import java.util.ArrayList;
}

@members {
    public record EffectRecord(EffectEnum type, EffectSubTypeEnum subType,
                               TimedEffect tmdEff, ProjectionEnum projType,
                               EffectNourish nourType, Stats statType,
                               EffectEnchant effEnc, Summon summType, String radiusStr,
                               String parmStr) {}
}

name
        returns[String nameStr]
        :   NAME NAME_STRING { $nameStr = $NAME_STRING.getText(); }
        ;

stats
        returns[Map<Stats, Integer> statsMap]
        @init {
            $statsMap = new HashMap<>();
        }
        :   STATS strv=INTEGER COLON intv=INTEGER COLON wisv=INTEGER COLON dexv=INTEGER COLON conv=INTEGER {
                $statsMap.put(Stats.STAT_STR, Integer.parseInt($strv.getText()));
                $statsMap.put(Stats.STAT_INT, Integer.parseInt($intv.getText()));
                $statsMap.put(Stats.STAT_WIS, Integer.parseInt($wisv.getText()));
                $statsMap.put(Stats.STAT_DEX, Integer.parseInt($dexv.getText()));
                $statsMap.put(Stats.STAT_CON, Integer.parseInt($conv.getText()));
            }
        ;

skillDisarmPhys
        returns[int base, int increment]
        :   SKILL_DISARM_PHYS b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

skillDisarmMagic
        returns[int base, int increment]
        :   SKILL_DISARM_MAGIC b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

skillDevice
        returns[int base, int increment]
        :   SKILL_DEVICE b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

skillSave
        returns[int base, int increment]
        :   SKILL_SAVE b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

skillStealth
        returns[int base, int increment]
        :   SKILL_STEALTH b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

skillSearch
        returns[int base, int increment]
        :   SKILL_SEARCH b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

skillMelee
        returns[int base, int increment]
        :   SKILL_MELEE b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;


skillShoot
        returns[int base, int increment]
        :   SKILL_SHOOT b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

skillThrow
        returns[int base, int increment]
        :   SKILL_THROW b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

skillDig
        returns[int base, int increment]
        :   SKILL_DIG b=INTEGER COLON i=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($i.getText());
            }
        ;

hitdie
        returns[int increment]
        :   HITDIE INTEGER { $increment = Integer.parseInt($INTEGER.getText()); }
        ;

exp
        returns[int percent]
        :   EXP INTEGER { $percent = Integer.parseInt($INTEGER.getText()); }
        ;

maxAttacks
        returns[int count]
        :   MAX_ATTACKS INTEGER { $count = Integer.parseInt($INTEGER.getText()); }
        ;

minWeight
        returns[int weight]
        :   MIN_WEIGHT INTEGER { $weight = Integer.parseInt($INTEGER.getText()); }
        ;

strengthMultiplier
        returns[int factor]
        :   STRENGTH_MULTIPLIER INTEGER { $factor = Integer.parseInt($INTEGER.getText()); }
        ;

title
        returns[String titleStr]
        :   TITLE TITLE_STRING { $titleStr = $TITLE_STRING.getText(); }
        ;

equip
        returns[StartItem startItem]
        @init {
            TValue tVal = TValue.TV_NONE;
            String nameStr = "";
            int minInt = 0;
            int maxInt = 0;
            String eopts = "";
        }
        @after {
            $startItem = new StartItem(tVal, nameStr, minInt, maxInt, eopts);
        }
        :   EQUIP tValue=TVALSTRING COLON obj=STRING COLON min=INTEGER COLON max=INTEGER COLON opts=TVALSTRING {
                String tvFlag = "TV_" + $tValue.getText().toUpperCase().replace(' ', '_').trim();
                tVal = TValue.fromName(tvFlag);
                nameStr = $obj.getText();
                minInt = Integer.parseInt($min.getText());
                maxInt = Integer.parseInt($max.getText());
                eopts = $opts.getText();
            }
        ;

objFlags
        returns[Flag<ObjectFlag> flags]
        @init {
            $flags = new Flag<>(ObjectFlag.class);
        }
        :   OBJ_FLAGS flag1=STRING {
                String rawFlag1 = "OF_" + $flag1.getText().toUpperCase().trim();
                $flags.on(ObjectFlag.valueOf(rawFlag1));
            } (OR flag2=STRING {
                String rawFlag2 = "OF_" + $flag2.getText().toUpperCase().trim();
                $flags.on(ObjectFlag.valueOf(rawFlag2));
            })*
        ;

playerFlags
        returns[Flag<PlayerFlag> flags]
        @init {
            $flags = new Flag<>(PlayerFlag.class);
        }
        :   PLAYER_FLAGS flag1=STRING {
                 String rawFlag1 = "PF_" + $flag1.getText().toUpperCase().trim();
                 $flags.on(PlayerFlag.valueOf(rawFlag1));
            } (OR flag2=STRING {
                 String rawFlag2 = "PF_" + $flag2.getText().toUpperCase().trim();
                 $flags.on(PlayerFlag.valueOf(rawFlag2));
            })*
        ;

magic
        returns[int firstSpellLevel, int weightHurtsSpell, int noBooksUsed]
        :   MAGIC first=INTEGER COLON weight=INTEGER COLON books=INTEGER {
                $firstSpellLevel  = Integer.parseInt($first.getText());
                $weightHurtsSpell = Integer.parseInt($weight.getText());
                $noBooksUsed      = Integer.parseInt($books.getText());
            }
        ;

book
        returns[MagicBook magicBook]
        @init { TValue bookType = TValue.TV_NONE;
                boolean dungeon = false;
                String sVal = "";
                int numSpells = 0;
                MagicRealm magicRealm = null;
              }
        @after {
            $magicBook = new MagicBook(bookType, sVal, dungeon,
                                       numSpells, magicRealm);
        }
        :   BOOK base=STRING COLON location=STRING COLON bookName=STRING COLON noOfSpells=INTEGER COLON realm=STRING {
                String baseRaw = "TV_" + $base.getText().toUpperCase().trim();
                bookType = TValue.fromName(baseRaw);
                dungeon = $location.getText().equals("dungeon");
                sVal = $bookName.getText();
                numSpells = Integer.parseInt($noOfSpells.getText());
                String realmRaw = $realm.getText();
                magicRealm = GameConstants.lookupRealm(realmRaw);
            }
        ;

bookGraphics
        returns[AngbandDisplayCharacter adc]
        :   BOOK_GRAPHICS bookChar=STRING COLON bookColour=STRING {
                String charRaw = $bookChar.getText();
                String colRaw = $bookColour.getText();
                if (charRaw.length() != 1 || colRaw.length() != 1) {
                    String message = "Invalid char representation of a book graphics found while parsing. "
                                   + "line was book:" + charRaw + ":" + colRaw;
                    throw new InvalidTokenFoundDuringParse(message);
                }

                $adc = new AngbandDisplayCharacter(charRaw.charAt(0), colRaw.charAt(0));
            }
        ;

bookProperties
        returns[int costInt, int levelInt, int min, int max]
        :   BOOK_PROPERTIES cost=INTEGER COLON level=INTEGER COLON range=STRING {
                $costInt = Integer.parseInt($cost.getText());
                $levelInt = Integer.parseInt($level.getText());
                String[] minMax = $range.getText().split(" to ");
                $min = Integer.parseInt(minMax[0]);
                $max = Integer.parseInt(minMax[1]);
            }
        ;

spell
        returns[MagicSpell magicSpell]
        @init {
            String spName = "";

            int sLevel = 0;
            int sMana = 0;
            int sFail = 0;
            int sExp = 0;
        }
        @after {
            $magicSpell = new MagicSpell(spName, sLevel, sMana,
                                         sFail, sExp);
        }
        :   SPELL spellName=STRING COLON level=INTEGER COLON mana=INTEGER COLON fail=INTEGER COLON spellExp=INTEGER {
                spName = $spellName.getText();
                sLevel = Integer.parseInt($level.getText());
                sMana  = Integer.parseInt($mana.getText());
                sFail  = Integer.parseInt($fail.getText());
                sExp   = Integer.parseInt($spellExp.getText());
            }
        ;

effect
        returns[EffectRecord effectEntry]
        @init {
            String effRadStr = "";
            int effParmStr = 0;
            EffectEnum effType = EffectEnum.EF_NONE;
            EffectSubTypeEnum subTypeEnum = EffectSubTypeEnum.EST_NONE;
            TimedEffect timedInit = TimedEffect.TMD_NONE;
            ProjectionEnum projInit = ProjectionEnum.PROJ_NONE;
            EffectNourish nourishInit = EffectNourish.EN_NONE;
            Stats statInit = Stats.STAT_NONE;
            EffectEnchant effeInit = EffectEnchant.EE_NONE;
            Summon effSummon = null;

            String entry1 = "";
            String entry2 = "";
            String entry3 = "";
            String entry4 = "";
        }
        @after {
            effType = EffectEnum.valueOf("EF_" + entry1);
            subTypeEnum = effType.getSubType();

            switch(subTypeEnum) {
                case EST_PROJ:
                    projInit = ProjectionEnum.valueOf("PROJ_" + entry2);
                    break;
                case EST_TMD:
                    timedInit = TimedEffect.valueOf("TMD_" + entry2);
                    break;
                case EST_STAT:
                    statInit = Stats.valueOf("STAT_" + entry2);
                    break;
                case EST_NOURISH:
                    nourishInit = EffectNourish.valueOf("EN_" + entry2);
                    break;
                case EST_ENCHANT:
                    effeInit = EffectEnchant.valueOf("EE_" + entry2);
                    break;
                case EST_SUMMON:
                    effSummon = GameConstants.lookupSummon(entry2);
                    break;
            }

            effRadStr = entry3;
            effParmStr = entry4;

            $effectEntry = new EffectRecord(effType, subTypeEnum, timedInit, projInit,
                                            nourishInit, statInit, effeInit, effSummon,
                                            effRadStr, effParmStr);
        }
        :   EFFECT eType1=STRING {
                entry1 = $eType1.getText();
            }
        |   EFFECT eType2=STRING COLON sType2=STRING {
                entry1 = $eType2.getText();
                entry2 = $sType2.getText();
            }
        |   EFFECT eType3=STRING COLON sType3=STRING COLON eRad3=INTEGER {
                entry1 = $eType3.getText();
                entry2 = $sType3.getText();
                entry3 = $eRad3.getText();
            }
        |   EFFECT eType4=STRING COLON sType4=STRING COLON eRad4=INTEGER COLON eParm4=STRING {
                entry1 = $eType4.getText();
                entry2 = $sType4.getText();
                entry3 = $eRad4.getText();
                entry4 = $eParm4.getText();
            }
        ;

effectYX
        returns[int yInt, int xInt]
        :   EFFECT_YX y=INTEGER COLON x=INTEGER {
                $yInt = Integer.parseInt($y.getText());
                $xInt = Integer.parseInt($x.getText());
            }
        ;

dice
        returns[String diceStr]
        :   DICE (STRING {
                $diceStr = $STRING.getText();
            } | INTEGER {
                $diceStr = $INTEGER.getText();
            })
        ;

expr
        returns[Expression exprObj]
        :   EXPR chr=STRING COLON func=STRING COLON oper=STRING {
                String rawCh = $chr.getText();
                if (rawCh.length() != 1) {
                    String message = "Invalid exression string. expression:" + rawCh + ":" + $func.getText() + ":" + $oper.getText();
                    throw new InvalidTokenFoundDuringParse(message);
                }

                EffectBaseType exp = EffectBaseType.valueOf("EFB_" + $func.getText());
                String operations = $oper.getText();

                $exprObj = new Expression(rawCh.charAt(0), exp, operations);
            }
        ;

effectMsg
        returns[String msgStr]
        :   EFFECT_MSG STRING { $msgStr = $STRING.getText(); }
        ;

desc
        returns[String descStr]
        :   DESC STRING { $descStr = $STRING.getText(); }
        ;

effectBlock
        returns[Effect effObj]
        @init {
            String effRadStr = "";
            String effParmStr = "";
            EffectEnum effInit = EffectEnum.EF_NONE;
            String diceInit = "";
            int yInit = 0;
            int xInit = 0;
            EffectSubTypeEnum subTypeInit = EffectSubTypeEnum.EST_NONE;
            TimedEffect timedInit = TimedEffect.TMD_NONE;
            ProjectionEnum projInit = ProjectionEnum.PROJ_NONE;
            EffectNourish effNourish = EffectNourish.EN_NONE;
            EffectEnchant effEnchant = EffectEnchant.EE_NONE;
            Summon effSummon = null;
            Stats effStat = Stats.STAT_NONE;
            int powerInit = 0;
            String msgInit = "";
            String visMsgInit = "";
            String timeInit = "";
            Expression exprObj = null;
        }
        @after {
            $effObj = new Effect(effInit, diceInit, yInit, xInit, timedInit, projInit,
                                 effStat, effNourish, effEnchant, effSummon, effRadStr,
                                 effParmStr, powerInit, msgInit, visMsgInit, timeInit,
                                 exprObj);
        }
        :   effect {
                EffectRecord entry = $effect.effectEntry;

                if (entry != null) {
                    effInit = entry.type();
                    subTypeInit = entry.subType();
                    timedInit = entry.tmdEff();
                    projInit = entry.projType();
                    effNourish = entry.nourType();
                    effStat = entry.statType();
                    effEnchant = entry.effEnc();
                    effSummon = entry.summType();
                    effRadStr = entry.radiusStr();
                    effParmStr = entry.parmStr();
                }
            }
        (   effectYX { yInit = $effectYX.yInt; xInit = $effectYX.xInt; }
        |   dice { diceInit = $dice.diceStr; }
        |   expr { exprObj = $expr.exprObj; }
        |   effectMsg { msgInit = $effectMsg.msgStr; })*
        ;

spellBlock
        returns[MagicSpell fullSpell]
        @init {
            String spellDesc = "";
            List<Effect> spellEffects = new ArrayList<>();
        }
        @after {
            for (Effect spellEffect : spellEffects)
                $fullSpell.addEffect(spellEffect);

            $fullSpell.setSpellDescription(spellDesc);
        }
        :   spell {
                $fullSpell = $spell.magicSpell;
            }
            (effectBlock {
                spellEffects.add($effectBlock.effObj);
            })+
            (desc { spellDesc = spellDesc +  $desc.descStr; })+
        ;

bookBlock
        returns[MagicBook magicBook,
                AngbandDisplayCharacter adc, int costInit, int levelInit,
                int minInit, int maxInit,
                StartItem startItem]
        :   book { $magicBook = $book.magicBook; }
            (bookGraphics { $adc = $bookGraphics.adc; })?
            (bookProperties {
                $costInit  = $bookProperties.costInt;
                $levelInit = $bookProperties.levelInt;
                $minInit   = $bookProperties.min;
                $maxInit   = $bookProperties.max;
            })?
            (equip { $startItem = $equip.startItem; })?
            (spellBlock { $magicBook.addSpell($spellBlock.fullSpell); })+
        ;

playerClass
        returns[PlayerClass newPlayerClass, List<ObjectKind> bookObjects]
        @init{
            String nameInit = "";
            List<String> titlesInit = new ArrayList<>();
            Map<Stats, Integer> statsInit = new HashMap<>();
            Map<PlayerSkill, Integer> classSkillsInit = new HashMap<>();
            Map<PlayerSkill, Integer> extraSkillsInit = new HashMap<>();
            int hitDieInit = 0;
            int expAdjInit = 100;
            Flag<ObjectFlag> oFlagInit = new Flag<>(ObjectFlag.class);
            Flag<PlayerFlag> pFlagInit = new Flag<>(PlayerFlag.class);
            int maxAttacksInit = 1;
            int minWeightInit = 0;
            int strMultInit = 1;
            List<StartItem> startItems = new ArrayList<>();
            ClassMagic magicInit = null;
            $bookObjects = new ArrayList<>();
        }
        @after {
            $newPlayerClass = new PlayerClass(nameInit, titlesInit, statsInit, classSkillsInit,
                                           extraSkillsInit, hitDieInit, expAdjInit, oFlagInit,
                                           pFlagInit, maxAttacksInit, minWeightInit,
                                           strMultInit, startItems, magicInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   stats { statsInit = $stats.statsMap; }
        |   skillDisarmPhys {
                classSkillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, $skillDisarmPhys.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, $skillDisarmPhys.increment);
            }
        |   skillDisarmMagic {
                classSkillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, $skillDisarmMagic.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, $skillDisarmMagic.increment);
            }
        |   skillDevice {
                classSkillsInit.put(PlayerSkill.SKILL_DEVICE, $skillDevice.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DEVICE, $skillDevice.increment);
            }
        |   skillSave {
                classSkillsInit.put(PlayerSkill.SKILL_SAVE, $skillSave.base);
                extraSkillsInit.put(PlayerSkill.SKILL_SAVE, $skillSave.increment);
            }
        |   skillStealth {
                classSkillsInit.put(PlayerSkill.SKILL_STEALTH, $skillStealth.base);
                extraSkillsInit.put(PlayerSkill.SKILL_STEALTH, $skillStealth.increment);
            }
        |   skillSearch {
                classSkillsInit.put(PlayerSkill.SKILL_SEARCH, $skillSearch.base);
                extraSkillsInit.put(PlayerSkill.SKILL_SEARCH, $skillSearch.increment);
            }
        |   skillMelee {
                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, $skillMelee.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, $skillMelee.increment);
            }
        |   skillShoot {
                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_BOW, $skillShoot.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_BOW, $skillShoot.increment);
            }
        |   skillThrow {
                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, $skillThrow.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, $skillThrow.increment);
            }
        |   skillDig {
                classSkillsInit.put(PlayerSkill.SKILL_DIGGING, $skillDig.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DIGGING, $skillDig.increment);
            }
        |   hitdie { hitDieInit = $hitdie.increment; }
        |   exp { expAdjInit = $exp.percent; }
        |   maxAttacks { maxAttacksInit = $maxAttacks.count; }
        |   minWeight { minWeightInit = $minWeight.weight; }
        |   strengthMultiplier { strMultInit = $strengthMultiplier.factor; }
        |   title { titlesInit.add($title.titleStr); }
        |   equip { startItems.add($equip.startItem); }
        |   objFlags { oFlagInit.union($objFlags.flags); }
        |   playerFlags { pFlagInit.union($playerFlags.flags); }
        |   magic {
                magicInit = new ClassMagic($magic.firstSpellLevel, $magic.weightHurtsSpell,
                                           $magic.noBooksUsed);
            }
        |   bookBlock {
                MagicBook magicBook = $bookBlock.magicBook;

                ObjectBase magicBookBase = GameConstants.getBaseFromTVal(magicBook.getBookType());

                String baseName;

                if (magicBookBase == null)
                    baseName = magicBook.getSubType(); // TODO: Change this once we have sorted out subtypes
                else
                    baseName = magicBookBase.getName();

                ObjectKind objKind = new ObjectKind($bookBlock.adc, $bookBlock.costInit,
                    $bookBlock.levelInit, $bookBlock.minInit,
                    $bookBlock.maxInit, baseName,
                    magicBook.getBookType(), magicBook.getSubType(),
                    new ObjectBase(), magicBook.isDungeon());

                $bookObjects.add(objKind);
            })+
        ;

file
        returns[List<PlayerClass> playerClasses]
        @init {
            $playerClasses = new ArrayList<>();
        }
        :   (playerClass {
            $playerClasses.add($playerClass.newPlayerClass);
            for (ObjectKind ok : $playerClass.bookObjects) {
                GameConstants.addObjectKind(ok);
            }
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

STATS
        :   'stats:'
        ;

SKILL_DISARM_PHYS
        :   'skill-disarm-phys:'
        ;

SKILL_DISARM_MAGIC
        :   'skill-disarm-magic:'
        ;

SKILL_DEVICE
        :   'skill-device:'
        ;

SKILL_SAVE
        :   'skill-save:'
        ;

SKILL_STEALTH
        :   'skill-stealth:'
        ;

SKILL_SEARCH
        :   'skill-search:'
        ;

SKILL_MELEE
        :   'skill-melee:'
        ;

SKILL_SHOOT
        :   'skill-shoot:'
        ;

SKILL_THROW
        :   'skill-throw:'
        ;

SKILL_DIG
        :   'skill-dig:'
        ;

HITDIE
        :   'hitdie:'
        ;

EXP
        :   'exp:'
        ;

MAX_ATTACKS
        :   'max-attacks:'
        ;

MIN_WEIGHT
        :   'min-weight:'
        ;

STRENGTH_MULTIPLIER
        :   'strength-multiplier:'
        ;

TITLE
        :   'title:'
        ;

EQUIP
        :   'equip:'
        ;

OBJ_FLAGS
        :   'obj-flags:'
        ;

PLAYER_FLAGS
        :   'player-flags:'
        ;

MAGIC
        :   'magic:'
        ;

BOOK
        :   'book:'
        ;

BOOK_GRAPHICS
        :   'book-graphics:'
        ;

BOOK_PROPERTIES
        :   'book-properties:'
        ;

SPELL
        :   'spell:'
        ;

EFFECT
        :   'effect:'
        ;

EFFECT_YX
        :   'effect-yx:'
        ;

DICE
        :   'dice:'
        ;

EXPR
        :   'expr:'
        ;

EFFECT_MSG
        :   'effect-msg:'
        ;

DESC
        :   'desc:'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

COLON
        :   ':'
        ;

OR
        :   '|'
        ;

TVALSTRING
        :   ('a'..'z')+
        ;

NAME_STRING
        :   ('A'..'Z') ('a'..'z')+
        ;

TITLE_STRING
        :   ('a'..'z' | 'A'..'Z' | '-')+
        ;

STRING
        :   ('a'..'z' | 'A'..'Z' | ' ' | '_' | '0'..'9' | '/' | '+' | ',' | '.' | '[' | ']'
            | '%' | '$' | '?' | '&' | '*' | ';' | '(' | ')' | '\'' | 'ú' | 'ë' | '!')+
        ;