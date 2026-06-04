grammar EgoItem;

@header {
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
    import uk.co.jackoftrades.middle.Activation;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.Brand;
    import uk.co.jackoftrades.middle.objects.Curse;
    import uk.co.jackoftrades.middle.objects.CurseData;
    import uk.co.jackoftrades.middle.objects.Slay;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.EgoItem;
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.objects.ElementInfo;
    import uk.co.jackoftrades.backend.numerics.Random;

    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

@members {
    public record CurseEntry(Curse curse, CurseData curseData) {}
    private static final Logger logger = LogManager.getLogger();
}

name
        returns[String nameStr]
        :   NAME TEXT { $nameStr = $TEXT.getText(); }
        ;

info
        returns[Random costDS, Random ratingDS]
        :   INFO cost=diceString COLON rating=diceString {
                $costDS = $cost.diceStr;
                $ratingDS = $rating.diceStr;
            }
        ;

alloc
        returns[Random commonInt, int minLev, int maxLev]
        :   ALLOC diceString COLON level=TEXT {
                $commonInt = $diceString.diceStr;

                String[] levels = $level.getText().split(" to ");
                if (levels.length != 2)
                    throw new InvalidTokenFoundDuringParse("Invalid alloc line. alloc:" + $diceString.diceStr
                    + ":" + $level.getText());

                $minLev = Integer.parseInt(levels[0]);
                $maxLev = Integer.parseInt(levels[1]);
            }
        ;

diceString
        returns[Random diceStr]
        :   ds=DICE_STRING { $diceStr = Random.parseStr($ds.getText()); logger.trace("Parsing " + $diceStr); }
        ;

combat
        returns[Random tohStr, Random todStr, Random toaStr]
        :   COMBAT toh=diceString COLON tod=diceString COLON toa=diceString {
                $tohStr = $toh.diceStr;
                $todStr = $tod.diceStr;
                $toaStr = $toa.diceStr;
            }
        ;

minCombat
        returns[Random tohStr, Random todStr, Random toaStr]
        :   MIN_COMBAT toh=diceString COLON tod=diceString COLON toa=diceString {
                $tohStr = $toh.diceStr;
                $todStr = $tod.diceStr;
                $toaStr = $toa.diceStr;
            }
        ;

type
        returns[TValue typeTVal]
        :   TYPE TEXT {
                String raw = "TV_" + $TEXT.getText().toUpperCase().replace(' ', '_');
                $typeTVal = TValue.valueOf(raw);
           }
        ;

item
        returns[TValue itemTVal, ObjectKind oKindObj]
        :   ITEM tval=TEXT COLON sval=TEXT {
                String raw = "TV_" + $tval.getText().toUpperCase().replace(' ', '_');
                $itemTVal = TValue.valueOf(raw);

                $oKindObj = GameConstants.lookupObjectKind($sval.getText());
            }
        ;

flags
        returns[List<ObjectFlag> oFlagList, List<ObjectKindFlag> okFlagList]
        @init {
            $oFlagList = new ArrayList<>();
            $okFlagList = new ArrayList<>();
        }
        :   FLAGS flag1=TEXT {
                String raw1 = $flag1.getText().toUpperCase().trim();
                String oFlagName1 = "OF_" + raw1;
                String kFlagName1 = "KF_" + raw1;
                try {
                    ObjectFlag oFlag1 = ObjectFlag.valueOf(oFlagName1);
                    $oFlagList.add(oFlag1);
                } catch (Exception e) {
                    ObjectKindFlag kFlag1 = ObjectKindFlag.valueOf(kFlagName1);
                    $okFlagList.add(kFlag1);
                }
            } (OR flag2=TEXT {
                String raw2 = $flag2.getText().toUpperCase().trim();
                String oFlagName2 = "OF_" + raw2;
                String kFlagName2 = "KF_" + raw2;
                try {
                    ObjectFlag oFlag2 = ObjectFlag.valueOf(oFlagName2);
                    $oFlagList.add(oFlag2);
                } catch (Exception e) {
                    ObjectKindFlag kFlag2 = ObjectKindFlag.valueOf(kFlagName2);
                    $okFlagList.add(kFlag2);
                }
            })* OR?
        ;

flags_off
        returns[List<ObjectFlag> oFlagList, List<ObjectKindFlag> okFlagList]
        @init {
            $oFlagList = new ArrayList<>();
            $okFlagList = new ArrayList<>();
        }
        :   FLAG_OFF flag1=TEXT {
                String raw1 = $flag1.getText().toUpperCase().trim();
                String oFlagName1 = "OF_" + raw1;
                String kFlagName1 = "KF_" + raw1;
                try {
                    ObjectFlag oFlag1 = ObjectFlag.valueOf(oFlagName1);
                    $oFlagList.add(oFlag1);
                } catch (Exception e) {
                    ObjectKindFlag kFlag1 = ObjectKindFlag.valueOf(kFlagName1);
                    $okFlagList.add(kFlag1);
                }
            } (OR flag2=TEXT {
                String raw2 = $flag2.getText().toUpperCase().trim();
                String oFlagName2 = "OF_" + raw2;
                String kFlagName2 = "KF_" + raw2;
                try {
                    ObjectFlag oFlag2 = ObjectFlag.valueOf(oFlagName2);
                    $oFlagList.add(oFlag2);
                } catch (Exception e) {
                    ObjectKindFlag kFlag2 = ObjectKindFlag.valueOf(kFlagName2);
                    $okFlagList.add(kFlag2);
                }
            })*
        ;

values
        returns[Map<ObjectModifier, Random> valueMap]
        @init {
            $valueMap = new HashMap<>();
        }
        :   VALUES val1=TEXT {
                String raw1 = $val1.getText();
                String[] parts1 = raw1.split("\\[");
                if (parts1.length != 2)
                    throw new InvalidTokenFoundDuringParse("Invalid values tokens. value:" + $val1.getText());

                String secondRaw1 = parts1[1].replace(']', ' ').trim();
                Random valStr1 = Random.parseStr(secondRaw1);
                ObjectModifier key1 = ObjectModifier.valueOf("OM_" + parts1[0]);
                $valueMap.put(key1, valStr1);
            } (OR val2=TEXT {
                String raw2 = $val2.getText();
                String[] parts2 = raw2.split("\\[");
                if (parts2.length != 2)
                    throw new InvalidTokenFoundDuringParse("Invalid values tokens. value:" + $val2.getText());

                String secondRaw2 = parts2[1].replace(']', ' ').trim();
                Random valStr2 = Random.parseStr(secondRaw2);

                ObjectModifier key2 = ObjectModifier.valueOf("OM_" + parts2[0]);
                $valueMap.put(key2, valStr2);
            })*
        ;

minValues
        returns[Map<ObjectModifier, Random> valueMap]
        @init {
            $valueMap = new HashMap<>();
        }
        :   MIN_VALUES val1=TEXT {
                String raw1 = $val1.getText();
                String[] parts1 = raw1.split("\\[");
                if (parts1.length != 2)
                    throw new InvalidTokenFoundDuringParse("Invalid values tokens. value:" + $val1.getText());

                ObjectModifier key1 = ObjectModifier.valueOf("OM_" + parts1[0]);
                Random valStr1 = Random.parseStr(parts1[1].replace(']', ' ').trim());
                $valueMap.put(key1, valStr1);
            } (OR val2=TEXT {
                String raw2 = $val2.getText();
                String[] parts2 = raw2.split("\\[");
                if (parts2.length != 2)
                    throw new InvalidTokenFoundDuringParse("Invalid values tokens. value:" + $val2.getText());

                ObjectModifier key2 = ObjectModifier.valueOf("OM_" + parts2[0]);
                Random valStr2 = Random.parseStr(parts2[1].replace(']', ' ').trim());
                $valueMap.put(key2, valStr2);
            })*
        ;

act
        returns[Activation activation]
        :   ACT actName=TEXT {
                $activation = GameConstants.lookupActivation($actName.getText());
            }
        ;

time
        returns[Random timeDS]
        :   TIME diceString { $timeDS = $diceString.diceStr; }
        ;

brand
        returns[Brand brandObj]
        :   BRAND TEXT { $brandObj = GameConstants.lookupBrandCode($TEXT.getText()); }
        ;

slay
        returns[Slay slayObj]
        :   SLAY TEXT { $slayObj = GameConstants.lookupSlay($TEXT.getText()); }
        ;

/* No curses in the file - reinstate if needed later

curse
        returns[Map<Curse, CurseData> curseMap]
        @init { $curseMap = new HashMap<>(); }
        :   CURSE curNme=TEXT COLON curPwr=diceString {
                String rawCurNme = $curNme.getText();
                String rawCurPwr = $curPwr.getText();
                int powerInt = Integer.parseInt(rawCurPwr);

                Curse curseObj = GameConstants.lookupCurse(rawCurNme);
                CurseData curData = new CurseData(powerInt, 0);

                $curseMap.put(curseObj, curData);
            }
        ; */

desc
        returns[String descStr]
        :   DESC TEXT { $descStr = $TEXT.getText(); }
        ;

egoItem
        returns[EgoItem egoItemObj]
        @init {
            String nameInit = "";
            String descInit = "";
            TValue typeInit = TValue.TV_NONE;
            int egoInit = 0;
            Random costInit = null;
            Flag<ObjectFlag> flagsInit = new Flag<>(ObjectFlag.class);
            Flag<ObjectFlag> flagsOffInit = new Flag<>(ObjectFlag.class);
            Flag<ObjectKindFlag> kindFlagsInit = new Flag<>(ObjectKindFlag.class);
            Flag<ObjectKindFlag> kindFlagsOffInit = new Flag<>(ObjectKindFlag.class);
            Map<ObjectModifier, Random> modifiersInit = new HashMap<>();
            Map<ObjectModifier, Random> minModifiersInit = new HashMap<>();
            Map<Brand, Boolean> brandsInit = new HashMap<>();
            Map<Slay, Boolean> slaysInit = new HashMap<>();
            Map<CurseData, Integer> curseInit = new HashMap<>();
            Random ratingInit = null;
            Random allocProbInit = null;
            int minLevelInit = 0;
            int maxLevelInit = 0;
            Map<TValue, ObjectKind> possItemInit = new HashMap<>();
            Random toHInit = null;
            Random toDInit = null;
            Random toAInit = null;
            Random minToHInit = null;
            Random minToDInit = null;
            Random minToAInit = null;
            Activation actInit = null;
            Random timeInit = null;
            boolean everSeenInit = false;
        }
        @after {
            $egoItemObj = new EgoItem(nameInit, typeInit, egoInit,
            costInit, flagsInit, flagsOffInit, kindFlagsInit,
            kindFlagsOffInit, modifiersInit, minModifiersInit,
            new HashMap<ElementEnum, ElementInfo>(), brandsInit,
            slaysInit, curseInit, ratingInit, allocProbInit,
            minLevelInit, maxLevelInit, possItemInit, toHInit,
            toDInit, toAInit, minToHInit, minToDInit, minToAInit,
            actInit, timeInit, everSeenInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   info {
                costInit = $info.costDS;
                ratingInit = $info.ratingDS;
            }
        |   alloc {
                allocProbInit = $alloc.commonInt;
                minLevelInit = $alloc.minLev;
                maxLevelInit = $alloc.maxLev;
            }
        |   combat {
                toHInit = $combat.tohStr;
                toDInit = $combat.todStr;
                toAInit = $combat.toaStr;
            }
        |   minCombat {
                minToHInit = $minCombat.tohStr;
                minToDInit = $minCombat.todStr;
                minToAInit = $minCombat.toaStr;
            }
        |   type { typeInit = $type.typeTVal; }
        |   item { possItemInit.put($item.itemTVal, $item.oKindObj); }
        |   flags {
                for (ObjectFlag o : $flags.oFlagList)
                    flagsInit.on(o);

                for (ObjectKindFlag k : $flags.okFlagList)
                    kindFlagsInit.on(k);
            }
        |   flags_off {
                for (ObjectFlag o : $flags_off.oFlagList)
                    flagsOffInit.on(o);

                for (ObjectKindFlag k : $flags_off.okFlagList)
                    kindFlagsOffInit.on(k);
            }
        |   values { modifiersInit.putAll($values.valueMap); }
        |   minValues { minModifiersInit.putAll($minValues.valueMap); }
        |   act { actInit = $act.activation;}
        |   time { timeInit = $time.timeDS; }
        |   brand { brandsInit.put($brand.brandObj, true); }
        |   slay { slaysInit.put($slay.slayObj, true);}
//        |   curse
        |   desc { descInit = descInit + $desc.descStr; })+
        ;

file
        returns[List<EgoItem> egoItems]
        @init {
            $egoItems = new ArrayList<>();
        }
        : (egoItem { $egoItems.add($egoItem.egoItemObj); })+ EOF
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

INFO
        :   'info:'
        ;

ALLOC
        :   'alloc:'
        ;

COMBAT
        :   'combat:'
        ;

MIN_COMBAT
        :   'min-combat:'
        ;

TYPE
        :   'type:'
        ;

ITEM
        :   'item:'
        ;

FLAGS
        :   'flags:'
        ;

FLAG_OFF
        :   'flags-off:'
        ;

VALUES
        :   'values:'
        ;

MIN_VALUES
        :   'min-values:'
        ;

ACT
        :   'act:'
        ;

TIME
        :   'time:'
        ;

BRAND
        :   'brand:'
        ;

SLAY
        :   'slay:'
        ;

CURSE
        :   'curse:'
        ;

DESC    :   'desc:'
        ;

COLON
        :   ':'
        ;

DICE_STRING
        :   ('-' | '0'..'9' | '+' | 'd' | 'M' | 'm')+
        ;

TEXT
        :   ('A'..'Z' | 'a'..'z' | ' ' | '_'| '(' | ')' | '-' | '*' | ('0'..'9')
            | ',' | '.' | '\'' | 'ó' | 'û' | '[' | ']' | '+')+
        ;

OR
        :   ' '? '|' ' '?
        ;