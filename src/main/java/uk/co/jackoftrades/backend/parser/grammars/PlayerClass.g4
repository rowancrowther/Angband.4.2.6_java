grammar PlayerClass;

@header {
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.player.StartItem;
    import uk.co.jackoftrades.middle.player.PlayerShape;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.magic.ClassMagic;
    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.magic.MagicBook;
    import uk.co.jackoftrades.middle.magic.MagicRealm;
    import uk.co.jackoftrades.middle.magic.MagicSpell;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.enums.GlyphType;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.effect.Earthquake;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.player.PlayerClass;
    import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}


name
        returns[String nameStr]
        :   NAME string=UPPER_LOWER_STRING { $nameStr = $string.getText(); }
        ;

stats
        returns[Map<Stats, Integer> stat]
        @init {
            $stat = new HashMap<>();
        }
        :   STATS s=INTEGER COLON i=INTEGER COLON w=INTEGER COLON d=INTEGER COLON c=INTEGER {
                $stat.put(Stats.STAT_STR, Integer.parseInt($s.getText()));
                $stat.put(Stats.STAT_INT, Integer.parseInt($i.getText()));
                $stat.put(Stats.STAT_WIS, Integer.parseInt($w.getText()));
                $stat.put(Stats.STAT_DEX, Integer.parseInt($d.getText()));
                $stat.put(Stats.STAT_CON, Integer.parseInt($c.getText()));
            }
        ;

skillDisarmPhys
        returns[int base, int increment]
        :   SKILL_DISARM_PHYS b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillDisarmMagic
        returns[int base, int increment]
        :   SKILL_DISARM_MAGIC b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillDevice
        returns[int base, int increment]
        :   SKILL_DEVICE b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillSave
        returns[int base, int increment]
        :   SKILL_SAVE b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillStealth
        returns[int base, int increment]
        :   SKILL_STEALTH b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillSearch
        returns[int base, int increment]
        :   SKILL_SEARCH b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillMelee
        returns[int base, int increment]
        :   SKILL_MELEE b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillShoot
        returns[int base, int increment]
        :   SKILL_SHOOT b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillThrow
        returns[int base, int increment]
        :   SKILL_THROW b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

skillDig
        returns[int base, int increment]
        :   SKILL_DIG b=INTEGER COLON inc=INTEGER {
                $base = Integer.parseInt($b.getText());
                $increment = Integer.parseInt($inc.getText());
            }
        ;

hitdie
        returns[int increment]
        :   HITDIE INTEGER { $increment = Integer.parseInt($INTEGER.getText()); }
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

equip
        returns[StartItem startItem]
        @init {
            TValue tval = TValue.TV_NONE;
            String sVal = "";
            int min = 0;
            int max = 0;
            String eopt = "";
        }
        @after {
            $startItem = new StartItem(tval, sVal, min, max, eopt);
        }
        //TValue tval, String sVal, int min, int max, String eopt]
        :   EQUIP tv=LOWER_WORD COLON (svs=UPPER_LOWER_STRING | bs=BRACKETED_STRING)
            COLON mn=INTEGER COLON mx=INTEGER COLON eopts=LOWER_WORD {
                String raw = "TV_" + $tv.getText().toUpperCase().replace(' ', '_');
                tval = TValue.fromName(raw);
                if ($bs == null)
                    sVal = $svs.getText();
                else
                    sVal = $bs.getText();
                min = Integer.parseInt($mn.getText());
                max = Integer.parseInt($mx.getText());
                eopt = $eopts.getText();
            }
        ;

equipBlock
        returns[List<StartItem> startItems]
        @init {
            $startItems = new ArrayList<>();
        }
        :   (equip { $startItems.add($equip.startItem); })+
        ;

objectFlags
        returns[Flag<ObjectFlag> oFlags]
        @init {
            $oFlags = new Flag<>(ObjectFlag.class);
        }
        :   OBJ_FLAGS flag1=UPPER_WORD {
                String raw1 = "OF_" + $flag1.getText().trim();
                $oFlags.on(ObjectFlag.valueOf(raw1));
            } (OR flag2=UPPER_WORD {
                String raw2 = "OF_" + $flag2.getText().trim();
                $oFlags.on(ObjectFlag.valueOf(raw2));
            })*
        ;

playerFlags
        returns[Flag<PlayerFlag> pFlags]
        @init {
            $pFlags = new Flag<>(PlayerFlag.class);
        }
        :   PLAYER_FLAGS flag1=UPPER_WORD {
                String raw1 = "PF_" + $flag1.getText().trim();
                $pFlags.on(PlayerFlag.valueOf(raw1));
            } (OR flag2=UPPER_WORD {
                String raw2 = "PF_" + $flag2.getText().trim();
                $pFlags.on(PlayerFlag.valueOf(raw2));

            })*
        ;

title
        returns[String titleStr]
        :   TITLE UPPER_LOWER_STRING { $titleStr = $UPPER_LOWER_STRING.getText(); }
        ;

titleBlock
        returns[List<String> titles]
        @init {
                $titles = new ArrayList<>();
            }
        :   t1=title
            t2=title
            t3=title
            t4=title
            t5=title
            t6=title
            t7=title
            t8=title
            t9=title
            t10=title
            {
                $titles.add($t1.titleStr);
                $titles.add($t2.titleStr);
                $titles.add($t3.titleStr);
                $titles.add($t4.titleStr);
                $titles.add($t5.titleStr);
                $titles.add($t6.titleStr);
                $titles.add($t7.titleStr);
                $titles.add($t8.titleStr);
                $titles.add($t9.titleStr);
                $titles.add($t10.titleStr);
            }
        ;

magic
        returns[ClassMagic classMagic]
        @init {
            int firstSpellLevel = 0;
            int weight = 0;
            int numOfBooks = 0;
        }
        @after {
            $classMagic = new ClassMagic(firstSpellLevel, weight, numOfBooks);
        }
        :   MAGIC first=INTEGER COLON hWeight=INTEGER COLON books=INTEGER {
            firstSpellLevel = Integer.parseInt($first.getText());
            weight = Integer.parseInt($hWeight.getText());
            numOfBooks = Integer.parseInt($books.getText());
        }
        ;

book
        returns[MagicBook magicBook]
        @init {
            ObjectKind kind = null;
            boolean dungeon = false;
            String name = "";
            int numSpells = 0;
            MagicRealm magicRealm = null;
        }
        @after {
            $magicBook = new MagicBook(kind, dungeon, name, numSpells, magicRealm);
        }
        :   BOOK objectBase=LOWER_WORD COLON dung=LOWER_WORD COLON bookName=BRACKETED_STRING
            COLON numOfSpells=INTEGER COLON realm=LOWER_WORD {
                kind = GameConstants.lookupObjectKind($objectBase.getText());
                dungeon = $dung.getText().equals("dungeon");
                name = $bookName.getText();
                numSpells = Integer.parseInt($numOfSpells.getText());
                magicRealm = GameConstants.lookupRealm($realm.getText());
            }
        ;

bookGraphics
        returns[AngbandDisplayCharacter adc]
        :   BOOK_GRAPHICS charGrap=GRAPHICS_CHAR COLON charCol=COLOUR_CHAR {
                String rawChar = $charGrap.getText();
                String rawCol = $charCol.getText();

                if (rawCol.length() != 1 || rawChar.length() != 1) {
                    String message = "Invalid book graphics line, character and/or colour are not 1 character long.\n"
                    + "Line is: book-graphics:" + rawChar + ":" + rawCol;
                    throw new InvalidTokenFoundDuringParse(message);
                }

                $adc = new AngbandDisplayCharacter(rawChar.charAt(0), rawCol.charAt(0));
            }
        ;

bookProperties
        returns[int cost, int commonness, int min, int max] // Need to be stored on ObjectKind
        :   BOOK_PROPERTIES c=INTEGER COLON common=INTEGER COLON range=RANGE {
                $cost = Integer.parseInt($c.getText());
                $commonness = Integer.parseInt($common.getText());
                String rawRange = $range.getText();
                String[] ranges = rawRange.split(" ");
                $min = Integer.parseInt(ranges[0]);
                $max = Integer.parseInt(ranges[2]);
            }
        ;

spell
        returns[MagicSpell magicSpell]
        @init {
            String name = "";
            int lev = 0;
            int man = 0;
            int fai = 0;
            int ex  = 0;
        }
        @after {
            $magicSpell = new MagicSpell(name, lev, man, fai, ex);
        }
        :   sName=SPELL_STRING COLON level=INTEGER COLON mana=INTEGER COLON fail=INTEGER COLON exp=INTEGER {
                name = $sName.getText().substring(6);
                lev  = Integer.parseInt($level.getText());
                man  = Integer.parseInt($mana.getText());
                fai  = Integer.parseInt($fail.getText());
                ex   = Integer.parseInt($exp.getText());
            }
        ;

effect
        returns[Effect effectObj]
        @init {
            EffectEnum effectType = EffectEnum.EF_NONE;
            String radius = "0";
            String extraParm = "0";
            EffectSubTypeWrapper wrappedValue = null;
            EffectSubTypeEnum subType = EffectSubTypeEnum.EST_NONE;
        }
        @after {
            $effectObj = new Effect(effectType, subType, wrappedValue, radius, extraParm);
        }
        :   EFFECT flagA1=UPPER_WORD {
                String rawA1 = "EF_" + $flagA1.getText();
                effectType = EffectEnum.valueOf(rawA1);
                subType = effectType.getSubType();
            }
        |   EFFECT flagA2=UPPER_WORD COLON (flagAa2=UPPER_LOWER_STRING | flagAb2=LOWER_WORD | flagAc2=UPPER_WORD) {
                String rawA2 = "EF_" + $flagA2.getText();
                wrappedValue = null;
                effectType = EffectEnum.valueOf(rawA2);
                subType = effectType.getSubType();
                String rawFlag2;
                if ($flagAa2 == null && $flagAb2 == null)
                    rawFlag2 = $flagAc2.getText();
                else if ($flagAa2 == null && $flagAc2 == null)
                    rawFlag2 = $flagAb2.getText();
                else
                    rawFlag2 = $flagAa2.getText();

                switch(subType) {
                    case EST_TMD:
                        wrappedValue = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + rawFlag2));
                        break;

                    case EST_PROJ:
                        wrappedValue = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + rawFlag2));
                        break;

                    case EST_NOURISH:
                        wrappedValue = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + rawFlag2));
                        break;

                    case EST_SUMMON:
                        wrappedValue = new EffectSubTypeWrapper(GameConstants.lookupSummon(rawFlag2));
                        break;

                    case EST_STAT:
                        wrappedValue = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + rawFlag2));
                        break;

                    case EST_ENCHANT:
                        wrappedValue = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + rawFlag2));
                        break;

                    case EST_SHAPECHANGE:
                        wrappedValue = new EffectSubTypeWrapper(GameConstants.lookupPlayerShape(rawFlag2));
                        break;

                    case EST_EARTHQUAKE:
                        wrappedValue = new EffectSubTypeWrapper(Earthquake.valueOf("QUAKE_" + rawFlag2));
                        break;

                    case EST_GLYPH:
                        wrappedValue = new EffectSubTypeWrapper(GlyphType.valueOf("GLYPH_" + rawFlag2));
                        break;

                    default:
                        String message = "Invalid effect line, illegal or unexpected sub type found.\n"
                        + "Line is: effect:" + $flagA2.getText() + ":" + rawFlag2;
                        throw new InvalidTokenFoundDuringParse(message);
                }
            }
        |   EFFECT flagA3=UPPER_WORD COLON flagB3=UPPER_WORD COLON rad3=INTEGER {
                String rawA3 = "EF_" + $flagA3.getText();
                effectType = EffectEnum.valueOf(rawA3);
                String rawFlag3 = $flagB3.getText();

                switch(effectType.getSubType()) {
                    case EST_TMD:
                        wrappedValue = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + rawFlag3));
                        break;

                    case EST_PROJ:
                        wrappedValue = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + rawFlag3));
                        break;

                    case EST_NOURISH:
                        wrappedValue = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + rawFlag3));
                        break;

                    case EST_SUMMON:
                        wrappedValue = new EffectSubTypeWrapper(GameConstants.lookupSummon(rawFlag3));
                        break;

                    case EST_STAT:
                        wrappedValue = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + rawFlag3));
                        break;

                    case EST_ENCHANT:
                        wrappedValue = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + rawFlag3));
                        break;

                    case EST_EARTHQUAKE:
                        wrappedValue = new EffectSubTypeWrapper(Earthquake.valueOf("QUAKE_" + rawFlag3));
                        break;

                    default:
                        String message = "Invalid effect line, illegal or unexpected sub type found.\n"
                        + "Line is: effect:" + $flagA3.getText() + ":" + rawFlag3 + ":" + $rad3.getText();
                        throw new InvalidTokenFoundDuringParse(message);
                }
                radius = $rad3.getText();
            }
        |   EFFECT flagA4=UPPER_WORD COLON flagB4=UPPER_WORD COLON rad4=INTEGER COLON parm4=INTEGER {
                String rawA4 = "EF_" + $flagA4.getText();
                effectType = EffectEnum.valueOf(rawA4);
                String rawFlag4 = $flagB4.getText();

                switch(effectType.getSubType()) {
                    case EST_TMD:
                        wrappedValue = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + rawFlag4));
                        break;

                    case EST_PROJ:
                        wrappedValue = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + rawFlag4));
                        break;

                    case EST_NOURISH:
                        wrappedValue = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + rawFlag4));
                        break;

                    case EST_SUMMON:
                        wrappedValue = new EffectSubTypeWrapper(GameConstants.lookupSummon(rawFlag4));
                        break;

                    case EST_STAT:
                        wrappedValue = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + rawFlag4));
                        break;

                    case EST_ENCHANT:
                        wrappedValue = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + rawFlag4));
                        break;

                    default:
                        String message = "Invalid effect line, illegal or unexpected sub type found.\n"
                        + "Line is: effect:" + $flagA4.getText() + ":" + rawFlag4 + ":"
                                             + $rad4.getText() + ":" + $parm4.getText();
                        throw new InvalidTokenFoundDuringParse(message);
                }
                radius = $rad4.getText();
                extraParm = $parm4.getText();
            }
        ;

effectMsg
        returns[String msg]
        :   EFFECT_MSG LOWER_WORD { $msg = $LOWER_WORD.getText(); }
        ;

effectYX
        returns[int y, int x]
        :   EFFECT_YX yInt=INTEGER COLON xInt=INTEGER {
                $y = Integer.parseInt($yInt.getText());
                $x = Integer.parseInt($xInt.getText());
            }
        ;

dice
        returns[String diceString]
        :   DICE {
                $diceString = $DICE.getText().substring(5);
            }
        ;

effectBlock
        returns[Effect eff]
        @init {
            $eff = null;
        }
        :   effect { $eff = $effect.effectObj; }
            (dice {
                String diceString = $dice.diceString;
                $eff.setDice(diceString);
            })?
            (effectYX {
                $eff.setYX($effectYX.y, $effectYX.x);
            })?
            (effectMsg {
                $eff.setMsg($effectMsg.msg);
            })?
            (expr {$eff.setExpression($expr.expression); })*
        ;

expr
        returns[Expression expression]
        @init {
            char codeLetter = '\0';
            EffectBaseType type = EffectBaseType.EFB_NONE;
            String operations = "";
        }
        @after {
            $expression = new Expression(codeLetter, type, operations);
        }
        :   EXPR letter=EXPR_LETTER COLON flag=UPPER_WORD COLON {
                String rawLetter = $letter.getText();
                if (rawLetter.length() != 1) {
                    String message = "Invalid expr line.\n";
                    throw new InvalidTokenFoundDuringParse(message);
                }

                codeLetter = rawLetter.charAt(0);
                String rawFlag = $flag.getText();
                type = EffectBaseType.valueOf("EFB_" + rawFlag);
            }
            (expOp1=EXPR_OPERATORS | m1=MINUS | p1=PLUS) SPACE int1=INTEGER {
                if ($expOp1 == null && $m1 == null)
                    operations = operations + $p1.getText();
                else if ($expOp1 == null && $p1 == null)
                    operations = operations + $m1.getText();
                else
                    operations = operations + $expOp1.getText();

                operations = operations + " " + $int1.getText();
            }
            (SPACE (expOp2=EXPR_OPERATORS | m2=MINUS | p2=PLUS) SPACE int2=INTEGER {
                if ($expOp2 == null && $m2 == null)
                    operations = operations + " " + $p2.getText();
                else if ($expOp2 == null && $p2 == null)
                    operations = operations + " " + $m2.getText();
                else
                    operations = operations + " " + $expOp2.getText();

                operations = operations + " " + $int2.getText();
            })*
        ;

desc
        returns[String descString]
        :   DESC_STRING { $descString = $DESC_STRING.getText().substring(5); }
        ;

spellBlock
        returns[MagicSpell magicSpell]
        @init {
            String descStr = "";
        }
        @after {
            $magicSpell.setSpellDescription(descStr);
        }
        :   spell { $magicSpell = $spell.magicSpell; }
            (effectBlock { $magicSpell.addEffect($effectBlock.eff); })+
            (desc { descStr = descStr + $desc.descString; })*
        ;

bookBlock
        returns[MagicBook magicBook, StartItem startItem]
        @init {
            boolean kindFound = false;
            ObjectKind bookKind = new ObjectKind();
            $startItem = null;
        }
        @after {
            if (kindFound)
                $magicBook.setBookKind(bookKind);
        }
        :   book {
                $magicBook = $book.magicBook;
            }
            (bookGraphics {
                bookKind.setCharacter($bookGraphics.adc);
                kindFound = true;
            })?
            (bookProperties {
                bookKind.setAlloc_prob($bookProperties.commonness);
                bookKind.setCost($bookProperties.cost);
                bookKind.setAlloc_min($bookProperties.min);
                bookKind.setAlloc_max($bookProperties.max);
                kindFound = true;
            })?
            (equip {
                $startItem = $equip.startItem;
            })?
            (spellBlock {
                $magicBook.addSpell($spellBlock.magicSpell);
            })+
        ;

playerClass
        returns[PlayerClass player]
        @init {
            String classStrInit = "";
            List<String> titlesInit = new ArrayList<>();
            Map<Stats, Integer> statsInit = new HashMap<>();
            Map<PlayerSkill, Integer> classSkillsInit = new HashMap<>();
            Map<PlayerSkill, Integer> extraSkillsInit = new HashMap<>();
            int hpAdjInit = 0;
            int attackCount = 0;
            int weight = 0;
            int strMult = 0;
            List<StartItem> startItems = new ArrayList<>();
            Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
            Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);
            ClassMagic classMagic = null;
        }
        @after{
            $player = new PlayerClass(classStrInit, titlesInit, statsInit,
            classSkillsInit, extraSkillsInit, hpAdjInit, 100, oFlags,
            pFlags, attackCount, weight, strMult, startItems, classMagic);
        }
        :   name { classStrInit = $name.nameStr; }
            stats { statsInit = $stats.stat; }
            skillDisarmPhys {
                classSkillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, $skillDisarmPhys.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, $skillDisarmPhys.increment);
            }
            skillDisarmMagic {
                classSkillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, $skillDisarmMagic.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, $skillDisarmMagic.increment);
            }
            skillDevice {
                classSkillsInit.put(PlayerSkill.SKILL_DEVICE, $skillDevice.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DEVICE, $skillDevice.increment);
            }
            skillSave {
                classSkillsInit.put(PlayerSkill.SKILL_SAVE, $skillSave.base);
                extraSkillsInit.put(PlayerSkill.SKILL_SAVE, $skillSave.increment);
            }
            skillStealth {
                classSkillsInit.put(PlayerSkill.SKILL_STEALTH, $skillStealth.base);
                extraSkillsInit.put(PlayerSkill.SKILL_STEALTH, $skillStealth.increment);
            }
            skillSearch {
                classSkillsInit.put(PlayerSkill.SKILL_SEARCH, $skillSearch.base);
                extraSkillsInit.put(PlayerSkill.SKILL_SEARCH, $skillSearch.increment);
            }
            skillMelee {
                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, $skillMelee.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, $skillMelee.increment);
            }
            skillShoot {
                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_BOW, $skillShoot.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_BOW, $skillShoot.increment);
            }
            skillThrow {
                classSkillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, $skillThrow.base);
                extraSkillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, $skillThrow.increment);
            }
            skillDig {
                classSkillsInit.put(PlayerSkill.SKILL_DIGGING, $skillDig.base);
                extraSkillsInit.put(PlayerSkill.SKILL_DIGGING, $skillDig.increment);
            }
            hitdie {
                hpAdjInit = $hitdie.increment;
            }
            maxAttacks {
                attackCount = $maxAttacks.count;
            }
            minWeight {
                weight = $minWeight.weight;
            }
            strengthMultiplier {
                strMult = $strengthMultiplier.factor;
            }
            equipBlock {
                startItems = $equipBlock.startItems;
            }
            (objectFlags {
                oFlags = $objectFlags.oFlags;
            })?
            playerFlags {
                pFlags = $playerFlags.pFlags;
            }
            titleBlock { titlesInit = $titleBlock.titles; }
            (magic {
                classMagic = $magic.classMagic;
            }
            (bookBlock {
                classMagic.addMagicBook($bookBlock.magicBook);
                StartItem si = $bookBlock.startItem;
                if (si != null)
                    startItems.add(si);
            })+)?
        ;

file
        returns[List<PlayerClass> playerClasses]
        @init {
            $playerClasses = new ArrayList<>();
        }
        :   (playerClass {
                $playerClasses.add($playerClass.player);
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

EXPR
        :   'expr:'
        ;

EFFECT_MSG
        :   'effect-msg:'
        ;

DESC
        :   'desc:'
        ;

COLON   :   ':'
        ;

MINUS
        :   '-'
        ;

fragment UPPER
        :   ('A'..'Z')
        ;

fragment LOWER
        :   ('a'..'z')
        ;

fragment DIGIT
        :   ('0'..'9')
        ;

SPACE
        :   ' '
        ;

fragment UNDERSCORE
        :   '_'
        ;

OR
        :   ' | '
        ;

COLOUR_CHAR
        :   ('R' | 'r' | 'y' | 'o' | 'G' | 'g' | 'P' | 'p')
        ;

EXPR_LETTER
        :   ('S' | 'M' | 'D' | 'B')
        ;

INTEGER
        :   MINUS? DIGIT+
        ;

UPPER_WORD
        :   UPPER+ (UNDERSCORE (INTEGER | UPPER)+)*
        ;

LOWER_WORD
        :   LOWER+ ((SPACE | UNDERSCORE) LOWER+)*
        ;

UPPER_LOWER_STRING
        :   UPPER LOWER+ ((SPACE | MINUS) (LOWER+ | (UPPER LOWER+)))*
        ;

DESC_STRING
        :  DESC ('A'..'Z' | 'a'..'z' | ' ' | '.' | ',' | '0'..'9' | '-' | '+' | ';' | '%'
                | ')' | '(' | 'ú' | 'ë' | '\'' | '!' | ':')+
        ;

BRACKETED_STRING
        : ('[') ('A'..'Z' | ' ' | 'a'..'z' | '\'')+ (']')
        ;

GRAPHICS_CHAR
        :   ('?')
        ;

RANGE
        :   DIGIT+ ' to ' DIGIT+
        ;

DOLLAR
        :   '$'
        ;

PLUS
        :   '+'
        ;

fragment D
        :   'd'
        ;

fragment M
        :   'M'
        ;

DICE
        :   'dice:' MINUS? ('0'..'9' | '$' | 'm' | M | 'S' | 'B' | 'D' | D | '+' | DIGIT+)+
        ;

EXPR_OPERATORS
        :   ('*' | '/')
        ;

SPELL_STRING
        :   'spell:' ('A'..'Z' | 'a'..'z' | ',' | '&' | ' ' | '-' | '\'' | 'ë')+
        ;