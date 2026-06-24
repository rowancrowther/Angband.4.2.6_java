// Parser+lexer for lib/gamedata/object.txt - the base kind for every
// generated object (potions, wands, weapons, armor, ...): graphics, tval/
// level/weight/cost, combat stats, allocation, charges/stacking, one or
// more on-use effects, flags/modifiers/elements/slays/curses, and
// descriptions. Cf. src/obj-init.c: struct file_parser object_parser
// (obj-init.c:2230).
//
// POTENTIAL PROBLEMS (#1 is severe and active; #2 is a complete missing
// feature, currently latent only because object.txt doesn't use it yet):
//
//   1. SEVERE/ACTIVE: `effect_block` is a hand-enumerated list of ~20
//      fixed alternatives (e.g. "power effect", "power effect dice",
//      "effect effect_yx", ...), each allowing at most one or two `effect`
//      matches. Real object.txt content routinely chains many `effect:`
//      lines (optionally each with its own dice:/effect-yx:) under a
//      single power:/msg: header - extracting the actual directive
//      sequences from object.txt turns up dozens of distinct shapes
//      including "power,effect,dice,effect,dice,effect,dice,effect,dice,
//      effect,dice,effect,dice" (6 repeated effect+dice pairs) and
//      "power,effect,effect,effect,effect,effect,effect,effect,effect,
//      effect,effect,effect,effect,effect,effect,dice" (14 effects in a
//      row). None of the ~20 enumerated alternatives accept an arbitrary
//      number of effects, so the majority of multi-effect items in
//      object.txt cannot be parsed by this rule as written - this is the
//      same hand-enumeration trap Random.g4's `dice` rule fell into, but
//      far more consequential here since it affects most of the file.
//
//   2. object.txt's own header documents a "brand:" directive ("brand:
//      code" - "adds a brand to the object... may appear more than once
//      for objects with multiple brands", obj-init.c-equivalent doc at
//      object.txt:30/123-125), and the BRAND lexer token exists, but
//      there's no `brand` parser rule at all and no alternative for it in
//      `item_object` - `kBrands`/the ItemObject's brands map can never be
//      populated by this grammar. Current object.txt happens not to use
//      brand: on any entry (0 occurrences), so this is latent rather than
//      actively failing, but it's a wholly unimplemented documented
//      feature, not just an edge case.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar ItemObject;

@header {
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.middle.objects.ObjectBase;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.effect.Expression;
    import uk.co.jackoftrades.middle.enums.EffectBaseType;
    import uk.co.jackoftrades.middle.enums.EffectEnum;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.ItemObject;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.objects.ElementInfo;
    import uk.co.jackoftrades.middle.objects.Brand;
    import uk.co.jackoftrades.middle.objects.CurseData;
    import uk.co.jackoftrades.middle.objects.Curse;
    import uk.co.jackoftrades.middle.Activation;
    import uk.co.jackoftrades.middle.objects.Flavour;
    import uk.co.jackoftrades.backend.strings.Quark;
    import uk.co.jackoftrades.middle.effect.Effect;
    import uk.co.jackoftrades.middle.objects.EgoItem;
    import uk.co.jackoftrades.middle.objects.Artifact;
    import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.cave.Loc;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
    import uk.co.jackoftrades.middle.enums.EffectNourish;
    import uk.co.jackoftrades.middle.enums.EffectEnchant;
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
    import uk.co.jackoftrades.frontend.colour.enums.ColourTranslation;

    import java.util.Map;
    import java.util.HashMap;
    import java.util.List;
    import java.util.ArrayList;
}

// Pairs a resolved Curse with its rolled CurseData (power + the 0 here is a
// placeholder second CurseData field) for `item_object`'s kCurses map key.
@members {
    public record CurseEntry(Curse curse, CurseData curseData) {}
}

// "name:<text>" - starts a new object kind record.
name
        returns[String nameStr]
        :   NAME STRING EOL { $nameStr = $STRING.getText(); }
        ;

// "graphics:<glyph>:<colour>" - display character for this kind.
graphics
        returns[AngbandDisplayCharacter graphicsADC]
        :   GRAPHICS chr=GRAPHICS_CHAR GRAPHICS_CHAR colourChar=STRING EOL {
                String raw = $colourChar.getText();

                if (raw.length() != 1) {
                    String message = "Invalid graphics string. graphics:" + $chr.getText() + ":" + raw;
                    throw new InvalidTokenFoundDuringParse(message);
                }

                char colChar = raw.charAt(0);
                $graphicsADC = new AngbandDisplayCharacter($chr.getText().charAt(0),
                               ColourType.getAttributeColour(colChar, ColourTranslation.ATTR_FULL));
            }
        ;

// "type:<tval text>" - the object's tval (e.g. "type:wand", "type:soft armor").
type
        returns[TValue typeObj]
        :   TYPE STRING EOL {
                String flagStr = $STRING.getText().toUpperCase().replace(' ', '_');
                $typeObj = TValue.valueOf("TV_" + flagStr);
            }
        ;

// "level:<value>" - native depth/difficulty level.
level
        returns[int levelInt]
        :   LEVEL STRING EOL { $levelInt = Integer.parseInt($STRING.getText()); }
        ;

// "weight:<tenths of a pound>".
weight
        returns[int weightInt]
        :   WEIGHT STRING EOL { $weightInt = Integer.parseInt($STRING.getText()); }
        ;

// "cost:<value>" - base shop price.
cost
        returns[int costInt]
        :   COST STRING EOL { $costInt = Integer.parseInt($STRING.getText()); }
        ;

// "attack:<base dice>:<to-hit>:<to-dam>" - melee weapon stats.
attack
        returns[String toh, String tod, String base]
        :   ATTACK b=STRING GRAPHICS_CHAR h=STRING GRAPHICS_CHAR d=STRING EOL {
                $toh = $h.getText();
                $tod = $d.getText();
                $base = $b.getText();
            }
        ;

// "armor:<base AC>:<to-AC>".
armour
        returns[int baseInt, String toa]
        :   ARMOUR base=STRING GRAPHICS_CHAR ac=STRING EOL {
                $baseInt = Integer.parseInt($base.getText());
                $toa = $ac.getText();
            }
        ;

// "alloc:<rarity>:<min level> to <max level>" - generation allocation.
alloc
        returns[int prob, int minLevel, int maxLevel]
        :   ALLOC p=STRING GRAPHICS_CHAR lev=STRING EOL {
                $prob = Integer.parseInt($p.getText());
                String levelSpread = $lev.getText();
                String[] levels = levelSpread.split(" to ");
                $minLevel = Integer.parseInt(levels[0]);
                $maxLevel = Integer.parseInt(levels[1]);
            }
        ;

// "charges:<dice string>" - wand/staff charge count.
charges
        returns[String chargesStr]
        :   CHARGES STRING EOL { $chargesStr = $STRING.getText(); }
        ;

// "pile:<chance>:<dice string>" - chance and amount for generating a stack.
pile
        returns[int prob, String amount]
        :   PILE p=STRING GRAPHICS_CHAR am=STRING EOL {
                $prob = Integer.parseInt($p.getText());
                $amount = $am.getText();
            }
        ;

// "power:<value>" - relative power for object-power calc, consumed as a
// prefix inside `effect_block`.
power
        returns[int powerInt]
        :   POWER STRING EOL { $powerInt = Integer.parseInt($STRING.getText()); }
        ;

// "effect:<TYPE>[:<SUBTYPE>[:<radius>[:<other>]]]" - one alternative per
// field count (1-4), each just collecting raw strings into a list -
// subtype resolution happens later in `effect_block`'s @after.
effect
        returns[List<String> effectList]
        @init {
            $effectList = new ArrayList<>();
        }
        :   EFFECT STRING EOL
            {
                $effectList.add($STRING.getText());
            }
        |   EFFECT STRING GRAPHICS_CHAR p1=STRING EOL
            {
                $effectList.add($STRING.getText());
                $effectList.add($p1.getText());
            }
        |   EFFECT STRING GRAPHICS_CHAR p1=STRING GRAPHICS_CHAR p2=STRING EOL
            {
                $effectList.add($STRING.getText());
                $effectList.add($p1.getText());
                $effectList.add($p2.getText());
            }
        |   EFFECT STRING GRAPHICS_CHAR p1=STRING GRAPHICS_CHAR p2=STRING GRAPHICS_CHAR p3=STRING EOL
            {
                $effectList.add($STRING.getText());
                $effectList.add($p1.getText());
                $effectList.add($p2.getText());
                $effectList.add($p3.getText());
            }
        ;

// "dice:<dice string>" - dice for the preceding effect: line.
dice
        returns[String diceStr]
        :   DICE STRING EOL { $diceStr = $STRING.getText(); }
        ;

// "msg:<text>" - message shown when the effect triggers.
msg
        returns[String msgStr]
        :   MSG STRING EOL { $msgStr = $STRING.getText(); }
        ;

// "vis-msg:<text>" - message shown when the effect is visible to others.
vis_msg
        returns[String msgStr]
        :   VIS_MSG STRING EOL { $msgStr = $STRING.getText(); }
        ;

// "time:<dice string>" - recharge time after using the effect.
time
        returns[String timeStr]
        :   TIME STRING EOL { $timeStr = $STRING.getText(); }
        ;

// "effect-yx:<y>:<x>" - sets a coordinate on the preceding effect:.
effect_yx
        returns[int yInt, int xInt]
        :   EFFECT_YX y=STRING GRAPHICS_CHAR x=STRING EOL {
                $yInt = Integer.parseInt($y.getText());
                $xInt = Integer.parseInt($x.getText());
            }
        ;

// "expr:<letter>:<EFB_BASE>:<operation>" - binds a dice-string variable used
// in the preceding dice: line.
expr
        returns[Expression exprObj]
        :   EXPR ch=STRING GRAPHICS_CHAR func=STRING GRAPHICS_CHAR oper=STRING EOL {
                String rawCh = $ch.getText();
                if (rawCh.length() != 1) {
                    String message = "Invalid exression string. expression:" + rawCh + ":" + $func.getText() + ":" + $oper.getText();
                    throw new InvalidTokenFoundDuringParse(message);
                }

                EffectBaseType exp = EffectBaseType.valueOf("EFB_" + $func.getText());
                String operations = $oper.getText();

                $exprObj = new Expression(rawCh.charAt(0), exp, operations);
            }
        ;

// Groups an optional power:/msg:/vis-msg: header with the effect: (and its
// dice:/expr:/effect-yx:) that follow. See top-of-file problem #1: the
// alternatives below are a fixed, hand-enumerated list that cannot express
// "N repetitions of effect (dice)?" for arbitrary N, but real object.txt
// entries routinely chain many effects under one header.
effect_block
        returns[Effect effObj]
        @init {
            String effRadStr = "";
            String effParmStr = "";
            List<String> effStringList = new ArrayList<>();
            EffectEnum effInit = EffectEnum.EF_NONE;
            String diceInit = "";
            int yInit = 0;
            int xInit = 0;
            EffectSubTypeEnum subTypeInit = EffectSubTypeEnum.EST_NONE;
            EffectSubTypeWrapper value = null;
            int powerInit = 0;
            String msgInit = "";
            String visMsgInit = "";
            String timeInit = "";
            Expression exprObj = null;
        }
        @after {
            // Parse the effect - should have up to 4 values, 1st one is
            // EffectEnum, 2nd is EffectSubType, 3rd is radius & 4th is parameter
            // 3 & 4 can be dice values
            effInit = EffectEnum.valueOf("EF_" + effStringList.get(0));
            subTypeInit = effInit.getSubType();

            if (effStringList.size() > 1) {
                String subTypeName = effStringList.get(1);

                switch (subTypeInit) {
                    case EST_PROJ:
                        value = new EffectSubTypeWrapper(ProjectionEnum.valueOf("PROJ_" + subTypeName));
                        break;

                    case EST_TMD:
                        value = new EffectSubTypeWrapper(TimedEffect.valueOf("TMD_" + subTypeName));
                        break;

                    case EST_STAT:
                        value = new EffectSubTypeWrapper(Stats.valueOf("STAT_" + subTypeName));
                        break;

                    case EST_NOURISH:
                        value = new EffectSubTypeWrapper(EffectNourish.valueOf("EN_" + subTypeName));
                        break;

                    case EST_ENCHANT:
                        value = new EffectSubTypeWrapper(EffectEnchant.valueOf("EE_" + subTypeName));
                        break;

                    case EST_SUMMON:
                        value = new EffectSubTypeWrapper(GameConstants.lookupSummon(subTypeName));
                        break;
                }
            }

            if (effStringList.size() > 2)
                effRadStr = effStringList.get(2);

            if (effStringList.size() > 3)
                effParmStr = effStringList.get(3);

            $effObj = new Effect(effInit, diceInit, yInit, xInit,
                                 value,
                                 effRadStr, effParmStr,
                                 powerInit, msgInit, visMsgInit,
                                 timeInit, exprObj);
        }
        :   power effect {
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
            }
        |   power effect time {
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
                timeInit = $time.timeStr;
            }
        |   power effect effect_yx {
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
                yInit = $effect_yx.yInt;
                xInit = $effect_yx.xInt;
            }
        |   power effect effect_yx time {
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
                yInit = $effect_yx.yInt;
                xInit = $effect_yx.xInt;
                timeInit = $time.timeStr;
            }
        |   power effect dice {
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
            }
        |   power effect dice expr {
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
                exprObj = $expr.exprObj;
            }
        |   power effect dice expr time {
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
                exprObj = $expr.exprObj;
                timeInit = $time.timeStr;
            }
        |   power effect dice time {
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
                timeInit = $time.timeStr;
            }
        |   power msg effect {
                powerInit = $power.powerInt;
                msgInit = $msg.msgStr;
                effStringList = $effect.effectList;
            }
        |   power msg effect dice {
                powerInit = $power.powerInt;
                msgInit = $msg.msgStr;
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
            }
        |   msg effect dice {
                msgInit = $msg.msgStr;
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
            }
        |   msg power effect {
                msgInit = $msg.msgStr;
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
            }
        |   msg effect {
                msgInit = $msg.msgStr;
                effStringList = $effect.effectList;
            }
        |   vis_msg power effect dice {
                visMsgInit = $vis_msg.msgStr;
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
            }
        |   vis_msg power effect dice time {
                visMsgInit = $vis_msg.msgStr;
                powerInit = $power.powerInt;
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
                timeInit = $time.timeStr;
            }
        |   effect dice {
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
            }
        |   effect dice expr {
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
                exprObj = $expr.exprObj;
            }
        |   effect dice expr time {
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
                exprObj = $expr.exprObj;
                timeInit = $time.timeStr;
            }
        |   effect effect_yx {
                effStringList = $effect.effectList;
                yInit = $effect_yx.yInt;
                xInit = $effect_yx.xInt;
            }
        |   effect effect_yx time {
                effStringList = $effect.effectList;
                yInit = $effect_yx.yInt;
                xInit = $effect_yx.xInt;
                timeInit = $time.timeStr;
            }
        |   effect dice time {
                effStringList = $effect.effectList;
                diceInit = $dice.diceStr;
                timeInit = $time.timeStr;
            }
        |   effect time {
                effStringList = $effect.effectList;
                timeInit = $time.timeStr;
            }
        |   effect {
                effStringList = $effect.effectList;
            }
        ;

// "flags:<FLAG> [| <FLAG> ...]" - tries each flag as an ObjectFlag first,
// falling back to ObjectKindFlag on failure, since the file mixes both
// flag families in the same '|'-separated list.
flags
        returns[Flag<ObjectFlag> oFlags, Flag<ObjectKindFlag> kFlags]
        @init {
            $oFlags = new Flag<>(ObjectFlag.class);
            $kFlags = new Flag<>(ObjectKindFlag.class);
        }
        :   FLAGS flag1=STRING {
            String rawFlag1 = $flag1.getText().trim();
            ObjectFlag oFlag1 = ObjectFlag.OF_NONE;
            ObjectKindFlag kFlag1 = ObjectKindFlag.KF_NONE;

            try {
                oFlag1 = ObjectFlag.valueOf("OF_" + rawFlag1);
                $oFlags.on(oFlag1);
            } catch (Exception e) {
                kFlag1 = ObjectKindFlag.valueOf("KF_" + rawFlag1);
                $kFlags.on(kFlag1);
            }
        } (GRAPHICS_CHAR flag2=STRING {
            String rawFlag2 = $flag2.getText().trim();
            ObjectFlag oFlag2 = ObjectFlag.OF_NONE;
            ObjectKindFlag kFlag2 = ObjectKindFlag.KF_NONE;

            try {
                oFlag2 = ObjectFlag.valueOf("OF_" + rawFlag2);
                $oFlags.on(oFlag2);
            } catch (Exception e) {
                kFlag2 = ObjectKindFlag.valueOf("KF_" + rawFlag2);
                $kFlags.on(kFlag2);
            }
        })* EOL
        ;

// "values:<OM_MODIFIER>[<value>] [| ...]" - object modifiers this kind has innately.
values
        returns[Map<ObjectModifier, String> valueMap]
        @init {
            $valueMap = new HashMap<>();
        }
        :   VALUES om1=STRING GRAPHICS_CHAR val1=STRING GRAPHICS_CHAR {
                ObjectModifier om_1 = ObjectModifier.valueOf("OM_" + $om1.getText());
                String val_1 = $val1.getText();
                $valueMap.put(om_1, val_1);
            }(LONG_OR om2=STRING GRAPHICS_CHAR val2=STRING GRAPHICS_CHAR {
                ObjectModifier om_2 = ObjectModifier.valueOf("OM_" + $om2.getText());
                String val_2 = $val2.getText();
                $valueMap.put(om_2, val_2);
            })* EOL
        ;

// "slay:<CODE>" - a slay this kind has innately, looked up in slay.txt; can
// repeat (see `item_object`'s kSlays map).
slay
        returns[Slay slayObj]
        :   SLAY STRING EOL {
                $slayObj = GameConstants.lookupSlay($STRING.getText());
            }
        ;

// "curse:<curse name>:<power>" - a curse this kind comes with innately; can
// repeat (see `item_object`'s kCurses map).
curse
        returns[Map<Curse, CurseData> curseMap]
        @init { $curseMap = new HashMap<>(); }
        :   CURSE curNme=STRING GRAPHICS_CHAR curPwr=STRING EOL {
                String rawCurNme = $curNme.getText();
                String rawCurPwr = $curPwr.getText();
                int powerInt = Integer.parseInt(rawCurPwr);

                Curse curseObj = GameConstants.lookupCurse(rawCurNme);
                CurseData curData = new CurseData(powerInt, 0);

                $curseMap.put(curseObj, curData);
            }
        ;

// "pval:<dice string>" - the kind's primary value (e.g. ring/amulet bonus magnitude).
pval
        returns[String pValStr]
        :   PVAL STRING EOL { $pValStr = $STRING.getText(); }
        ;

// "desc:<text>" - flavour description; can repeat to build up multiple lines.
desc
        returns[String descStr]
        :   DESC STRING EOL { $descStr = $STRING.getText(); }
        ;

// One full object kind record. Builds both an ObjectKind (the base item
// type) and an ItemObject (a concrete instance of it) from the same parsed
// fields, since this codebase doesn't yet separate "kind template" from
// "object instance" construction the way the C struct does (struct
// object_kind vs struct object). See top-of-file problem #2 re: the
// missing brand: support - kBrands is declared here but nothing ever
// populates it.
item_object
        returns[ItemObject itemObj, ObjectKind oKind]
        @init {
            // ItemObject initial values
            EgoItem ioe = null;
            Artifact ioa = null;
            ItemObject known = null;
            Loc location = null;
            TValue ioTVal = TValue.TV_NONE;
            int sValInit = 0;
            String ioPVal = "";
            // read weight from kWeight
            // read damDie from kDamDie
            // read baseDam from kBaseDam
            // read normalAC from kAc
            // read toAc from kToA
            // read toDam from kToD
            // read toHit from kToH
            // read flags from oFlagInit
            // read modifiers from kModsInit
            // read elInfo from kElInfo
            // read brands from kBrands
            // read slays from kSlays
            // read curses from kCurses
            // read effect from kEffects
            String ioEffMsg = "";
            // read activation from kActivs
            // read time from kEffects
            int timeoutInit = 0;
            int numberInit = 0;
            Flag<ObjectNotice> oNotice = new Flag<>(ObjectNotice.class);
            int heldMIndex = -1;
            int mimickingMIndex = -1;
            ObjectOriginEnum ioOrigin = ObjectOriginEnum.ORIGIN_NONE;
            int ioOriginDepth = 0;
            MonsterRace ioMonRace = null;
            Quark note = null;

            // ObjectKind initial values
            String kName = "";
            String kText = "";
            ObjectBase kBase = null;
            String kPVal = "";
            String kToH = "";
            String kToD = "";
            String kBaseDam = "";
            String kToA = "";
            int kAc = 0;
            int kDamDie = 0;
            int kDamSides = 0;
            int kWeight = 0;
            int kCost = 0;
            Flag<ObjectFlag> oFlagInit = new Flag<>(ObjectFlag.class);
            Flag<ObjectKindFlag> oKindFlagInit = new Flag<>(ObjectKindFlag.class);
            Map<ObjectModifier, String> kModsInit = new HashMap<>();
            Map<ElementEnum, ElementInfo> kElInfo = new HashMap<>();
            Map<Brand, Boolean> kBrands = new HashMap<>();
            Map<Slay, Boolean> kSlays = new HashMap<>();
            Map<CurseEntry, Boolean> kCurses = new HashMap<>();
            AngbandDisplayCharacter kChar = null;
            int kAllocProb = 0;
            int kAllocMin = 0;
            int kAllocMax = 0;
            int kLevel = 0;
            List<Activation> kActivs = new ArrayList<>();
            List<Effect> kEffects = new ArrayList<>();
            String kEffectMsg = "";
            String kVisEffectMsg = "";
            String kTime = "";
            String kCharge = "";
            int kGenMultProb = 0;
            String kStackSize = "";
            Flavour kFlavour = null;
            Quark kNoteAware = null;
            Quark kNoteUnaware = null;
            boolean kAware = false;
            boolean kTried = false;
            int kIgnore = 0;
            boolean kEverseen = false;
        }
        @after
        {
            $oKind = new ObjectKind(kName, kText, kBase, 0, kPVal, kToH, kToD, kToA,
                                   kAc, kBaseDam, kDamDie, kDamSides, kWeight, kCost,
                                   oFlagInit, oKindFlagInit, kModsInit, kElInfo, kBrands,
                                   kSlays, kCurses, kChar, kAllocProb, kAllocMin,
                                   kAllocMax, kLevel, kActivs, kEffects, kEffectMsg,
                                   kVisEffectMsg, kTime, kCharge, kGenMultProb,
                                   kStackSize, kFlavour, kNoteAware, kNoteUnaware, kAware,
                                   kTried, kIgnore, kEverseen, ioTVal);

            $itemObj = new ItemObject($oKind, ioe, ioa, known, location, ioTVal, sValInit,
                                     ioPVal, kWeight, kDamDie, kDamSides, kAc, kBaseDam,
                                     kToA, kToD, kToH, oFlagInit, kModsInit, kElInfo,
                                     kBrands, kSlays, kCurses, kEffects, ioEffMsg,
                                     kActivs, kTime, timeoutInit, numberInit, oNotice,
                                     heldMIndex, mimickingMIndex, ioOrigin,
                                     ioOriginDepth, ioMonRace, note);
        }
        :   EOL*
            name { kName = $name.nameStr; }
        (   graphics { kChar = $graphics.graphicsADC; }
        |   type { ioTVal = $type.typeObj; }
        |   level { kLevel = $level.levelInt; }
        |   weight { kWeight = $weight.weightInt; }
        |   cost { kCost = $cost.costInt; }
        |   attack {
                 kToH = $attack.toh;
                 kToD = $attack.tod;
                 kBaseDam = $attack.base;
            }
        |   armour {
                kAc = $armour.baseInt;
                kToA = $armour.toa;
            }
        |   alloc {
                kAllocProb = $alloc.prob;
                kAllocMin = $alloc.minLevel;
                kAllocMax = $alloc.maxLevel;
            }
        |   charges { kCharge = $charges.chargesStr; }
        |   pile {
                kGenMultProb = $pile.prob;
                kStackSize = $pile.amount;
            }
        |   effect_block { kEffects.add($effect_block.effObj); }
        |   flags {
                oFlagInit.union($flags.oFlags);
                oKindFlagInit.union($flags.kFlags);
            }
        |   values { kModsInit.putAll($values.valueMap); }
        |   slay { kSlays.put($slay.slayObj, true); }
        |   curse {
                for (Curse curse : $curse.curseMap.keySet()) {
                    CurseEntry ce = new CurseEntry(curse, $curse.curseMap.get(curse));
                    kCurses.put(ce, true);
                }
            }
        |   pval { ioPVal = $pval.pValStr; }
        |   desc { kText = kText + $desc.descStr; }
        )+  EOL*
        ;

// Top-level rule: the whole file is one or more object kind records.
file
        returns[List<ItemObject> itemObjects, List<ObjectKind> objectKinds]
        @init {
            $itemObjects = new ArrayList<>();
            $objectKinds = new ArrayList<>();
        }
        :   (item_object {
                $itemObjects.add($item_object.itemObj);
                $objectKinds.add($item_object.oKind);
            })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A line ending - deliberately not skipped, since every directive rule
// explicitly matches a trailing EOL and `item_object` uses EOL* to skip
// the blank lines between records.
EOL
        :   ' '* '\r'? '\n'
        ;

// NAME through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:'
        ;

GRAPHICS
        :   'graphics:'
        ;

TYPE
        :   'type:'
        ;

LEVEL
        :   'level:'
        ;

WEIGHT
        :   'weight:'
        ;

COST
        :   'cost:'
        ;

ATTACK
        :   'attack:'
        ;

ARMOUR
        :   'armor:'
        ;

ALLOC
        :   'alloc:'
        ;

CHARGES
        :   'charges:'
        ;

PILE
        :   'pile:'
        ;

POWER
        :   'power:'
        ;

MSG
        :   'msg:'
        ;

VIS_MSG
        :   'vis-msg:'
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

TIME
        :   'time:'
        ;

EXPR
        :   'expr:'
        ;

FLAGS
        :   'flags:'
        ;

VALUES
        :   'values:'
        ;

// PROBLEM: lexed but never consumed by any parser rule - see top-of-file
// problem #2. If object.txt ever adds a brand: line, this token alone
// won't help since there's no `brand` rule to match it.
BRAND
        :   'brand:'
        ;

SLAY
        :   'slay:'
        ;

CURSE
        :   'curse:'
        ;

PVAL
        :   'pval:'
        ;

DESC
        :   'desc:'
        ;

// The " | " separator between modifier entries on a values: line.
LONG_OR
        :   ' | '
        ;

// Disabled/unused - colour-code charset, superseded by graphics: reading a
// plain STRING and validating its length in the `graphics` rule's action
// instead of constraining it at the lexer level.
// GRAPHICS_COLOUR
//        :   ('d' | 'D' | 'w' | 's' | 'o' | 'r' | 'g' | 'b'
//           | 'u' | 'W' | 'P' | 'y' | 'R' | 'G' | 'B' | 'U'
//           | 'p' | 'v' | 't' | 'm' | 'Y' | 'i' | 'T' | 'V'
//           | 'I' | 'M' | 'z' | 'Z')
//        ;

// A generic field separator used as the ':'-equivalent between sub-fields
// within attack:/armor:/alloc:/pile:/effect:/effect-yx:/expr:/values:/
// curse: lines (this grammar uses GRAPHICS_CHAR rather than a dedicated
// COLON token for those, since the values it matches overlap with
// characters that can appear inside item/flag names elsewhere).
GRAPHICS_CHAR
        :   (']' | '&' | '*' | '~' | '!' | ',' | '\\' | '|' | ':' | '/'
           | '}' | '{' | '[' | '(' | ')' | '=' | '"'  | '?' | '-' | '_'
           | '$')
        ;

// Free-running general-purpose text - used for nearly every field's value
// (names, numbers-as-text, flag/effect/type names, dice strings, messages).
STRING
        :   ('a'..'z' | 'A'..'Z' | '<' | '>' | ' ' | '~' | '&' | '\''
           | '0'..'9' | '.' | ',' | '(' | ')' | '-' | ';' | '!' | '_'
           | '+' | '*' | '$' | '/')+
        ;

// POTENTIAL SOLUTIONS
//
//   1. Restructure effect_block around repetition rather than enumeration:
//      something like
//        (power | msg | vis_msg)?
//        (effect (dice (expr)? | effect_yx)?)+
//        time?
//      so any number of effect (each with its own optional dice/expr or
//      effect-yx) can follow one header, instead of hand-listing every
//      combination. Push effObj construction into a list of per-effect
//      records the way `item_object` already does for `effect_block`
//      itself (kEffects.add(...)), rather than building a single Effect
//      per `effect_block` match.
//
//   2. Add a `brand` rule (e.g. `BRAND STRING EOL { $brandObj =
//      GameConstants.lookupBrandCode($STRING.getText()); }`, mirroring
//      `slay`) and a `(brand { kBrands.put($brand.brandObj, true); })*`
//      alternative in `item_object`, so this matches its own documented
//      format and won't break the moment object.txt's first brand: line
//      appears.
