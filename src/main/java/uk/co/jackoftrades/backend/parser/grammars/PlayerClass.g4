// Parser+lexer for lib/gamedata/class.txt - the playable classes (Warrior,
// Mage, ...): stats, birth skills, starting equipment, titles-by-level,
// innate flags, and (for spellcasters) magic books full of spells and their
// on-cast effects. This is the live, wired-up grammar for class.txt -
// PlayerClassReader.java consumes it; Class.g4 in this same directory is an
// abandoned parallel rewrite with no generated sources/reader (see that
// file's own header). Cf. src/init.c: struct file_parser class_parser
// (init.c:4179), directive table in init_parse_class().
//
// Verified field-by-field against real class.txt content (all 9 classes):
// the strict, non-optional ordering `name stats skill-disarm-phys ...
// equip+ obj-flags? player-flags title*10 (magic book+)?` in `playerClass`
// matches every class record exactly, including which classes have
// obj-flags: (only Blackguard), which have a magic block (everything
// except Warrior/Blackguard's melee-only kit), and the always-exactly-10
// titles per class.
//
// POTENTIAL PROBLEMS (all minor - unused format capacity, not active bugs):
//
//   1. The EXP token ('exp:') is lexed but never referenced anywhere in
//      `playerClass` - there's no `exp` rule at all, and the class's
//      experience-penalty value is hardcoded to the literal 100 in
//      `playerClass`'s @after. class.txt's own header confirms this is
//      currently fine ("The current classes don't set this and get the
//      default of no experience point penalty"), but a future class adding
//      an "exp:" line would hit an unexpected-token parse error.
//
//   2. `equip`'s last field (`eopts=LOWER_WORD`) only captures a single
//      birth-option name, but the header documents it as "names of birth
//      options separated by spaces or |" (a list). Every current equip:
//      line uses just one option ("none" or "birth_no_recall"), so this is
//      unexercised rather than broken.
//
// (Not a grammar problem: class.txt's header documents "name:class
// number:class name" as the name: format, but every actual name: line in
// the file is just "name:<class name>" with no number - `name`'s single-
// field rule matches reality; the header comment is simply stale.)
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

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


// "name:<class name>" - starts a new class record (see top-of-file note re:
// the stale "class number" field in the file's own header doc).
name
        returns[String nameStr]
        :   NAME string=UPPER_LOWER_STRING { $nameStr = $string.getText(); }
        ;

// "stats:<str>:<int>:<wis>:<dex>:<con>" - class stat modifiers.
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

// "skill-<name>:<base>:<increment per 10 levels>" family (skillDisarmPhys
// through skillDig) - one near-identical rule per of the 10 birth skills.
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

// "hitdie:<value>" - class contribution to max hitpoints per level.
hitdie
        returns[int increment]
        :   HITDIE INTEGER { $increment = Integer.parseInt($INTEGER.getText()); }
        ;

// "max-attacks:<count>" - maximum possible blows per round.
maxAttacks
        returns[int count]
        :   MAX_ATTACKS INTEGER { $count = Integer.parseInt($INTEGER.getText()); }
        ;

// "min-weight:<tenths of a pound>" - minimum weapon weight used when
// computing blows per round.
minWeight
        returns[int weight]
        :   MIN_WEIGHT INTEGER { $weight = Integer.parseInt($INTEGER.getText()); }
        ;

// "strength-multiplier:<factor>" - strength's contribution to blows per round.
strengthMultiplier
        returns[int factor]
        :   STRENGTH_MULTIPLIER INTEGER { $factor = Integer.parseInt($INTEGER.getText()); }
        ;

// "equip:<tval>:<sval>:<min>:<max>:<birth option>" - one starting equipment
// item; can repeat (see `equipBlock`). The sval is either plain text
// (UPPER_LOWER_STRING) or bracketed (BRACKETED_STRING, for magic books).
// See top-of-file problem #2 re: eopts only capturing one birth option.
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

// One or more equip: lines - this class's starting gear.
equipBlock
        returns[List<StartItem> startItems]
        @init {
            $startItems = new ArrayList<>();
        }
        :   (equip { $startItems.add($equip.startItem); })+
        ;

// "obj-flags:<OF_FLAG> [| <OF_FLAG> ...]" - object flags this class has
// innately. Optional - only Blackguard uses it in the current data.
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

// "player-flags:<PF_FLAG> [| <PF_FLAG> ...]" - player flags this class has
// innately. Every class has exactly one of these.
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

// "title:<text>" - one level-up title, e.g. "title:Rookie".
title
        returns[String titleStr]
        :   TITLE UPPER_LOWER_STRING { $titleStr = $UPPER_LOWER_STRING.getText(); }
        ;

// Exactly 10 title: lines (verified: every class in class.txt has exactly
// 10) - one per 5-level title tier.
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

// "magic:<first spell level>:<book weight>:<number of books>" - presence of
// this line is what makes a class a spellcaster (optional in `playerClass`).
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

// "book:<object base>:<town|dungeon>:<bracketed display name>:<num spells>:<realm>"
// - one of this class's spellbooks.
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

// "book-graphics:<symbol>:<colour>" - this spellbook's display glyph.
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

// "book-properties:<cost>:<commonness>:<min> to <max>" - this spellbook's
// shop cost, allocation commonness, and depth range.
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

// "spell:<name>:<level>:<mana>:<fail %>:<exp>" - one spell in a spellbook;
// note the "spell:" literal is consumed as part of the SPELL_STRING token
// itself (see that token below), hence the substring(6) in the action.
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

// "effect:<TYPE>[:<SUBTYPE>[:<radius>[:<other>]]]" - one alternative per
// field count (1-4), each resolving SUBTYPE via a switch on
// EffectEnum.getSubType() covering every EST_* case this codebase uses
// (PROJ/TMD/NOURISH/SUMMON/STAT/ENCHANT/SHAPECHANGE/EARTHQUAKE/GLYPH) and
// throwing InvalidTokenFoundDuringParse on an unrecognised one rather than
// silently producing a null/wrong wrapper - this is the most complete
// effect-subtype dispatch of any grammar in this directory.
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

// "effect-msg:<text>" - message shown when the effect: triggers.
effectMsg
        returns[String msg]
        :   EFFECT_MSG LOWER_WORD { $msg = $LOWER_WORD.getText(); }
        ;

// "effect-yx:<y>:<x>" - sets a coordinate on the preceding effect:.
effectYX
        returns[int y, int x]
        :   EFFECT_YX yInt=INTEGER COLON xInt=INTEGER {
                $y = Integer.parseInt($yInt.getText());
                $x = Integer.parseInt($xInt.getText());
            }
        ;

// "dice:<dice string>" - dice for the preceding effect:. Unlike most other
// grammars here, the DICE lexer token itself swallows the whole
// "dice:<string>" text (see the DICE token below), hence substring(5).
dice
        returns[String diceString]
        :   DICE {
                $diceString = $DICE.getText().substring(5);
            }
        ;

// Groups one effect: line with whichever of dice:/effect-yx:/effect-msg:/
// expr: follow it (in any order/quantity) into a single Effect, via the
// Effect setters rather than rebuilding the object.
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

// "expr:<letter>:<EFB_BASE>:<operation>" - binds a dice-string variable used
// in the preceding dice: line. Unlike Random.g4/TrapLexer.g4, the operation
// is tokenized as separate operator/SPACE/integer pieces here and
// reassembled in the action (supporting up to 2 chained operations, e.g.
// "/ 2 + 1") rather than captured as one token.
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

// "desc:<text>" - spell flavour text; can repeat to build up multiple lines.
desc
        returns[String descString]
        :   DESC_STRING { $descString = $DESC_STRING.getText().substring(5); }
        ;

// One full spell entry: the spell: header, one or more effects (each with
// its own dice/expr/etc via effectBlock), and any desc: lines.
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

// One full spellbook: book: header, optional graphics/properties/starting-
// equipment lines, then one or more spells.
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

// One full class record. The sequence below is strict (no '*'/'|' looseness)
// and matches every class in class.txt exactly: name, stats, the 10
// skill-* lines in a fixed order, hitdie/max-attacks/min-weight/strength-
// multiplier, one-or-more equip: lines, an optional obj-flags:, a mandatory
// player-flags:, exactly 10 titles, then an optional magic+book(s) block
// for spellcasters. See top-of-file problem #1 re: there being no `exp`
// step in this sequence at all.
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

// Top-level rule: the whole file is one or more class records.
file
        returns[List<PlayerClass> playerClasses]
        @init {
            $playerClasses = new ArrayList<>();
        }
        :   (playerClass {
                $playerClasses.add($playerClass.player);
            })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
// (Also covers the "###### MAGE ######"-style banner lines between classes.)
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// NAME through SPELL/EFFECT/EFFECT_YX/EXPR/EFFECT_MSG/DESC below: one
// literal directive-keyword token each, matching the rule of the same
// purpose above (e.g. NAME -> `name`, STATS -> `stats`).
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

// Field separator used throughout most directives.
COLON   :   ':'
        ;

// Negative-number sign, also reused inside `expr`'s operation tokenizing.
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

// A literal space - used both standalone (in `expr`) and as a fragment-like
// building block inside LOWER_WORD/UPPER_LOWER_STRING below.
SPACE
        :   ' '
        ;

fragment UNDERSCORE
        :   '_'
        ;

// The " | " separator between flag names on an obj-flags:/player-flags: line.
OR
        :   ' | '
        ;

// A spellbook's display colour code, e.g. "R" for the magic book.
COLOUR_CHAR
        :   ('R' | 'r' | 'y' | 'o' | 'G' | 'g' | 'P' | 'p')
        ;

// The single-letter dice variable an expr: line binds, e.g. "S"/"D"/"B"/"M".
EXPR_LETTER
        :   ('S' | 'M' | 'D' | 'B')
        ;

// A (possibly negative) literal integer.
INTEGER
        :   MINUS? DIGIT+
        ;

// An UPPER_CASE_WITH_UNDERSCORES_OR_DIGITS symbolic name - used for flag/
// effect/subtype names.
UPPER_WORD
        :   UPPER+ (UNDERSCORE (INTEGER | UPPER)+)*
        ;

// Free-running lowercase text, space- or underscore-separated words - used
// for tval names, effect-msg: text, and birth-option names.
LOWER_WORD
        :   LOWER+ ((SPACE | UNDERSCORE) LOWER+)*
        ;

// Title-Case text, possibly multiple space- or hyphen-joined words (e.g.
// "Ration of Food", "Soft Leather Armor") - used for class/title names and
// plain (non-bracketed) equip: sval text.
UPPER_LOWER_STRING
        :   UPPER LOWER+ ((SPACE | MINUS) (LOWER+ | (UPPER LOWER+)))*
        ;

// "desc:" followed by free-running description text, as a single token
// (mirrors DICE/SPELL_STRING's pattern of folding the literal keyword into
// the token itself, hence `desc`'s substring(5) above).
DESC_STRING
        :  DESC ('A'..'Z' | 'a'..'z' | ' ' | '.' | ',' | '0'..'9' | '-' | '+' | ';' | '%'
                | ')' | '(' | 'ú' | 'ë' | '\'' | '!' | ':')+
        ;

// A "[Bracketed Display Name]" - used for magic book svals/titles.
BRACKETED_STRING
        : ('[') ('A'..'Z' | ' ' | 'a'..'z' | '\'')+ (']')
        ;

// The display glyph used for every spellbook in this file ("?").
GRAPHICS_CHAR
        :   ('?')
        ;

// A "<min> to <max>" depth range, e.g. "1 to 100" - book-properties:'s
// trailing field.
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

// "dice:" plus the entire dice-string expression as one token (digits,
// '$'-variables, 'd'/'M' separators, sign) - cf. Random.g4's `dice` rule,
// which is the general-purpose version of this same mini-language.
DICE
        :   'dice:' MINUS? ('0'..'9' | '$' | 'm' | M | 'S' | 'B' | 'D' | D | '+' | DIGIT+)+
        ;

// The '*'/'/' operators usable in an expr: operation (note '+'/'-' are
// handled separately via PLUS/MINUS in the `expr` rule's action).
EXPR_OPERATORS
        :   ('*' | '/')
        ;

// "spell:" plus the spell's name text as one token (same fold-the-keyword-
// into-the-token pattern as DESC_STRING/DICE), hence `spell`'s substring(6).
SPELL_STRING
        :   'spell:' ('A'..'Z' | 'a'..'z' | ',' | '&' | ' ' | '-' | '\'' | 'ë')+
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth adding if class.txt ever sets a non-default exp: value -
//      add an `exp` rule (mirroring PlayerRace.g4's) and an optional
//      `(exp {...})?` step in `playerClass`, replacing the hardcoded 100.
//
//   2. Only worth extending if an equip: line ever needs more than one
//      birth-option exclusion - widen `eopts` to match a '|'/space-
//      separated list the same way obj-flags:/player-flags: already do for
//      their flag lists, instead of a single LOWER_WORD.