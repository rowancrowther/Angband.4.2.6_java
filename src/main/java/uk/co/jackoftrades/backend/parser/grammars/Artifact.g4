grammar Artifact;

@header {
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.Activation;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.objects.Artifact;
    import uk.co.jackoftrades.middle.objects.Brand;
    import uk.co.jackoftrades.middle.objects.Curse;
    import uk.co.jackoftrades.middle.objects.CurseData;
    import uk.co.jackoftrades.middle.objects.ObjectKind;
    import uk.co.jackoftrades.middle.objects.Slay;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

name
        returns [String nameStr]
        :   NAME {
            String raw = $NAME.getText();
            $nameStr = raw.substring(5);
        }
        ;

baseObject
        returns[TValue tVal, String sVal]
        :   BASE_OBJECT COLON MCASE {
                String raw = $BASE_OBJECT.getText().substring(12);
                raw = raw.replace(' ', '_').replace("armour", "armor").toUpperCase();
                $sVal = $MCASE.getText();
                $tVal = TValue.valueOf("TV_" + raw);
            }
        ;

graphics
        returns[AngbandDisplayCharacter adc]
        :   GRAPHICS {
                String raw = $GRAPHICS.getText();
                $adc = new AngbandDisplayCharacter(raw.charAt(9), raw.charAt(11));
            }
        ;

level
        returns[int lev]
        :   LEVEL INTEGER { $lev = Integer.parseInt($INTEGER.getText()); }
        ;

weight
        returns[int wgt]
        :   WEIGHT INTEGER { $wgt = Integer.parseInt($INTEGER.getText()); }
        ;

cost
        returns[int cst]
        :   COST INTEGER { $cst = Integer.parseInt($INTEGER.getText()); }
        ;

alloc
        returns[int prob, int minRange, int maxRange]
        :   ALLOC p=INTEGER COLON min=INTEGER RANGE_TO max=INTEGER {
                $prob = Integer.parseInt($p.getText());
                $minRange = Integer.parseInt($min.getText());
                $maxRange = Integer.parseInt($max.getText());
            }
        ;

attack
        returns[String damage, int toH, int toD]
        :   ATTACK DICE_STRING COLON hit=INTEGER COLON dam=INTEGER {
                $damage = $DICE_STRING.getText();
                $toH = Integer.parseInt($hit.getText());
                $toD = Integer.parseInt($dam.getText());
            }
        ;

armour
        returns[int baseAC, int toA]
        :   ARMOUR base=INTEGER COLON plus=INTEGER {
                $baseAC = Integer.parseInt($base.getText());
                $toA = Integer.parseInt($plus.getText());
            }
        ;

flags
        returns[Flag<ObjectFlag> flag]
        @init {
            $flag = new Flag<>(ObjectFlag.class);
        }
        :   FLAGS f1=MCASE {
                String flag1 = "OF_" + $f1.getText().trim();
                $flag.on(ObjectFlag.valueOf(flag1));
            } (OR f2=MCASE {
                String flag2 = "OF_" + $f2.getText().trim();
                $flag.on(ObjectFlag.valueOf(flag2));
            })* OR?
        ;

values
        returns[Map <ObjectModifier, Integer> skills]
        @init {
            $skills = new HashMap<>();
        }
        :   VALUES ps1=MCASE LBRACKET v1=INTEGER RBRACKET {
                String raw1 = $ps1.getText().trim();
                int val1 = Integer.parseInt($v1.getText());
                $skills.put(ObjectModifier.valueOf("OM_" + raw1), val1);
            } (OR ps2=MCASE LBRACKET v2=INTEGER RBRACKET {
                String raw2 = $ps2.getText().trim();
                int val2 = Integer.parseInt($v2.getText());
                $skills.put(ObjectModifier.valueOf("OM_" + raw2), val2);
            })* OR?
        ;

act
        returns[Activation activation]
        :   ACT {
                String name = $ACT.getText().substring(4);
                $activation = GameConstants.lookupActivation(name);
            }
        ;

time
        returns[String dice]
        :   TIME ((DICE_STRING {
                $dice = $DICE_STRING.getText();
            })  | (INTEGER {
                $dice = $INTEGER.getText();
            }))
        ;

desc    returns[String description]
        :   DESC { $description = $DESC.getText().substring(5); }
        ;

brand
        returns[Brand b]
        :   BRAND {
                String raw = $BRAND.getText();
                $b = GameConstants.lookupBrandCode(raw.substring(6));
            }
        ;

slay
        returns[Slay s]
        :   SLAY {
                String raw = $SLAY.getText();
                $s = GameConstants.lookupSlay(raw.substring(5));
            }
        ;

curse
        returns[Map<Curse, CurseData> curses]
        @init {
            $curses = new HashMap<>();
        }
        :   CURSE MCASE COLON INTEGER {
                String raw = $MCASE.getText();
                int val = Integer.parseInt($INTEGER.getText());
                Curse c = GameConstants.lookupCurse(raw);
                CurseData cd = new CurseData(val, -1);
                $curses.put(c, cd);
            }
        ;

msg
        returns[String message]
        :   MSG { $message = $MSG.getText().substring(4); }
        ;

artifact
        returns[Artifact art]
        @init {
            String nameInit = "";
            TValue tValInit = TValue.TV_NONE;
            String sValInit = "";
            AngbandDisplayCharacter adcInit = null;
            int levelInit = 0;
            int weightInit = 0;
            int costInit = 0;
            int probInit = 0;
            int allocMin = 0;
            int allocMax = 0;
            String damageInit = "";
            int toHInit = 0;
            int toDInit = 0;
            int baseACInit = 0;
            int toAInit = 0;
            Flag<ObjectFlag> flagsInit = new Flag<>(ObjectFlag.class);
            Map<ObjectModifier, Integer> modifierInit = new HashMap<>();
            Activation actInit = null;
            String timeInit = "";
            String descInit = "";
            List<Brand> brandInit = new ArrayList<>();
            List<Slay> slayInit = new ArrayList<>();
            List<Map<Curse, CurseData>> curseInit = new ArrayList<>();
            String messageInit = "";

            ObjectKind kind = null;
        }
        @after {
            kind = GameConstants.lookupObjectKind(tValInit, nameInit);

            if (kind == null) {
                kind = new ObjectKind(adcInit, costInit, levelInit, allocMin, allocMax,
                                      nameInit, tValInit, nameInit, null, true);
            }

            $art = new Artifact(nameInit, descInit, 0, tValInit, sValInit, toHInit,
                                toDInit, toAInit, baseACInit, damageInit,
                                weightInit, costInit, flagsInit, modifierInit,
                                brandInit, slayInit, curseInit, levelInit,
                                probInit, allocMin, allocMax, actInit,
                                messageInit, timeInit);
        }
        :   name {
                nameInit = $name.nameStr;
            }
        (   baseObject {
                tValInit = $baseObject.tVal;
                sValInit = $baseObject.sVal;
            }
        |   graphics {
                adcInit = $graphics.adc;
            }
        |   level {
                levelInit = $level.lev;
            }
        |   weight {
                weightInit =$weight.wgt;
            }
        |   cost {
                costInit = $cost.cst;
            }
        |   alloc {
                probInit = $alloc.prob;
                allocMin = $alloc.minRange;
                allocMax = $alloc.maxRange;
            }
        |   attack {
                damageInit = $attack.damage;
                toHInit = $attack.toH;
                toDInit = $attack.toD;
            }
        |   armour {
                baseACInit = $armour.baseAC;
                toAInit = $armour.toA;
            }
        |   flags {
                flagsInit.union($flags.flag);
            }
        |   brand {
                brandInit.add($brand.b);
            }
        |   curse {
                curseInit.add($curse.curses);
            }
        |   slay {
                slayInit.add($slay.s);
            }
        |   values {
                for (ObjectModifier mod : $values.skills.keySet())
                    modifierInit.put(mod, $values.skills.get(mod));
            }
        |   act {
                actInit = $act.activation;
            }
        |   time {
                timeInit = $time.dice;
            }
        |   msg {
                messageInit = messageInit + $msg.message;
            }
        )+
            (desc {
                descInit = descInit + $desc.description;
            }
        )+
        ;

file
        returns[List<Artifact> artifacts]
        @init {
            $artifacts = new ArrayList<>();
        }
        :   (artifact {
                $artifacts.add($artifact.art);
            })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

fragment MixedCase
        :   ('a'..'z' | 'A'..'Z' | 'ä' | ' ' | '\'' | '.' | ',' | 'á' | '_' | 'â' | '"'
            | '-' | 'ë' | 'û' | ';'| 'ú' | 'ö' | 'É' | '?' | 'ó' | '!' | 'é' | 'í')+
        ;

fragment LowerCase
        :   ('a'..'z')+
        ;

NAME
        :   'name:' MixedCase
        ;

BASE_OBJECT
        :   'base-object:' (LowerCase | ' ')+
        ;

GRAPHICS
        :   'graphics:' ('~' | '=' | '"') COLON ('y' | 'd' | 'g' | 'w')
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

ALLOC
        :   'alloc:'
        ;

ATTACK
        :   'attack:'
        ;

ARMOUR
        :   'armor:'
        ;

FLAGS
        :   'flags:'
        ;

ACT
        :   'act:' ('A'..'Z' | '_')+ ('0'..'9')*
        ;

TIME
        :   'time:'
        ;

MSG
        :   'msg:' ('{' | '}' | '.' | 'a'..'z' | 'A'..'Z' | ' ')+
        ;

VALUES
        :   'values:'
        ;

BRAND
        :   'brand:' ('A'..'Z')+ '_' ('0'..'9')+
        ;

SLAY
        :   'slay:' ('A'..'Z')+ '_' ('0'..'9')+
        ;

CURSE
        :   'curse:'
        ;

DESC
        :   'desc:' MixedCase
        ;

RANGE_TO
        :   ' to '
        ;

DICE_STRING
        :   INTEGER 'd' INTEGER
        |   INTEGER '+d' INTEGER
        |   INTEGER '+' INTEGER 'd' INTEGER
        ;

MCASE
        :   MixedCase
        ;

INTEGER
        :   MINUS? ('0'..'9')+
        ;

COLON
        :   ':'
        ;

LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        |   '] '
        ;

OR
        :   '|'
        ;

PLUS
        :   '+'
        ;

MINUS
        :   '-'
        ;