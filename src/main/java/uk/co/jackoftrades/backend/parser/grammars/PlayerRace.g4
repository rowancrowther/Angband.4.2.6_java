// Parser+lexer for lib/gamedata/p_race.txt - one record per playable race
// (Human, Half-Elf, Dwarf, ...): base stats, birth skills, hit die, XP
// modifier, age/height/weight ranges, starting history chart, and any
// innate object/player flags or object-modifier values. Cf. src/init.c:
// struct file_parser p_race_parser (init.c:2847), directives registered in
// init_parse_p_race() (~init.c:2750-2820, one parse_p_race_* handler per
// field below).
//
// POTENTIAL PROBLEMS:
//
//   1. `race`'s @init declares `int rIndexInit = 0;` and nothing in the
//      rule ever reassigns it - every PlayerRace built here gets index 0.
//      The C struct's `r->ridx` is a real, meaningful sequential index
//      (0 for the first race in the file, 1 for the second, ...), computed
//      in finish_parse_p_race (init.c:2821-2829) by walking the fully-
//      parsed (reverse-order) list and re-deriving forward indices. Any
//      code that distinguishes races by index (lookups, arrays) would see
//      every race in this Java port as race 0.
//
// (`bodyIntInit` defaulting to 0/never being set is intentional, not a bug
// - p_race.txt has no "body:" directive at all in this version, and the C
// parser hardcodes the same default: "Default body is humanoid",
// init.c:2564-2565.)
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar PlayerRace;

@header {
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
    import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
    import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
    import uk.co.jackoftrades.middle.player.PlayerRace;
    import uk.co.jackoftrades.middle.enums.Stats;
    import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.player.PlayerBody;
    import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;
}

// "name:<race name>" - starts a new race record.
name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

// "stats:<str>:<int>:<wis>:<dex>:<con>" - racial stat modifiers.
stats
        returns[Map<Stats, Integer> statsMap]
        @init {
            $statsMap = new HashMap<>();
        }
        :   STATS str=INTEGER COLON intVal=INTEGER COLON wis=INTEGER COLON dex=INTEGER COLON con=INTEGER {
                $statsMap.put(Stats.STAT_STR, Integer.parseInt($str.getText()));
                $statsMap.put(Stats.STAT_INT, Integer.parseInt($intVal.getText()));
                $statsMap.put(Stats.STAT_WIS, Integer.parseInt($wis.getText()));
                $statsMap.put(Stats.STAT_DEX, Integer.parseInt($dex.getText()));
                $statsMap.put(Stats.STAT_CON, Integer.parseInt($con.getText()));
            }
        ;

// "skill-<name>:<base value>" family (skill_disarm_phys through skill_dig)
// - birth values for each of the 10 racial skills (physical/magical
// disarm, devices, saves, stealth, search, melee/bow/throw to-hit,
// digging). One near-identical rule per skill name in the data file.
skill_disarm_phys
        returns[int value]
        :   SKILL_DISARM_PHYS INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_disarm_magic
        returns[int value]
        :   SKILL_DISARM_MAGIC INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_device
        returns[int value]
        :   SKILL_DEVICE INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_save
        returns[int value]
        :   SKILL_SAVE INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_stealth
        returns[int value]
        :   SKILL_STEALTH INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_search
        returns[int value]
        :   SKILL_SEARCH INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_melee
        returns[int value]
        :   SKILL_MELEE INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_shoot
        returns[int value]
        :   SKILL_SHOOT INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_throw
        returns[int value]
        :   SKILL_THROW INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

skill_dig
        returns[int value]
        :   SKILL_DIG INTEGER { $value = Integer.parseInt($INTEGER.getText()); }
        ;

// "hitdie:<value>" - max hitpoints gained per level.
hitdie
        returns[int die]
        :   HITDIE INTEGER { $die = Integer.parseInt($INTEGER.getText()); }
        ;

// "exp:<value>" - percent of "standard" experience needed to gain a level.
exp
        returns[int expInt]
        :   EXP INTEGER { $expInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "infravision:<value>" - infravision range, in multiples of 10 feet.
infravision
        returns[int infraInt]
        :   INFRAVISION INTEGER { $infraInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "history:<chart number>" - starting point in history.txt's chart chain.
history
        returns[int histInt]
        :   HISTORY INTEGER { $histInt = Integer.parseInt($INTEGER.getText()); }
        ;

// "age:<base>:<random modifier>".
age
        returns[int ageBase, int ageMod]
        :   AGE base=INTEGER COLON mod=INTEGER {
                $ageBase = Integer.parseInt($base.getText());
                $ageMod = Integer.parseInt($mod.getText());
            }
        ;

// "height:<base>:<random modifier>".
height
        returns[int heightBase, int heightMod]
        :   HEIGHT base=INTEGER COLON mod=INTEGER {
                $heightBase = Integer.parseInt($base.getText());
                $heightMod = Integer.parseInt($mod.getText());
            }
        ;

// "weight:<base>:<random modifier>".
weight
        returns[int weightBase, int weightMod]
        :   WEIGHT base=INTEGER COLON mod=INTEGER {
                $weightBase = Integer.parseInt($base.getText());
                $weightMod = Integer.parseInt($mod.getText());
            }
        ;

// "obj-flags:<OF_FLAG> [| <OF_FLAG> ...]" - object flags this race has innately.
obj_flags
        returns[Flag<ObjectFlag> flags]
        @init {
            $flags = new Flag<>(ObjectFlag.class);
        }
        :   OBJ_FLAGS flag1=STRING {
                String raw1 = $flag1.getText().toUpperCase().trim();
                ObjectFlag of1 = ObjectFlag.valueOf("OF_" + raw1);
                $flags.on(of1);
            } (OR flag2=STRING {
                String raw2 = $flag2.getText().toUpperCase().trim();
                ObjectFlag of2 = ObjectFlag.valueOf("OF_" + raw2);
                $flags.on(of2);
            })*
        ;

// "player-flags:<PF_FLAG> [| <PF_FLAG> ...]" - player flags this race has innately.
player_flags
        returns[Flag<PlayerFlag> flags]
        @init {
            $flags = new Flag<>(PlayerFlag.class);
        }
        :   PLAYER_FLAGS flag1=STRING {
                String raw1 = $flag1.getText().toUpperCase().trim();
                PlayerFlag pf1 = PlayerFlag.valueOf("PF_" + raw1);
                $flags.on(pf1);
            } (OR flag2=STRING {
                String raw2 = $flag2.getText().toUpperCase().trim();
                PlayerFlag pf2 = PlayerFlag.valueOf("PF_" + raw2);
                $flags.on(pf2);
            })*
        ;

// "values:<OM_MODIFIER>[<value>] [| <OM_MODIFIER>[<value>] ...]" - object
// modifiers this race has innately, e.g. "values:RES_LIGHT[1]".
values
        returns[Map<ObjectModifier, Integer> modifiers]
        @init {
            $modifiers = new HashMap<>();
        }
        :   VALUES valName1=STRING LBRACKET valInt1=INTEGER RBRACKET {
                String raw1 = $valName1.getText().toUpperCase().trim();
                ObjectModifier om1 = ObjectModifier.valueOf("OM_" + raw1);
                int val1 = Integer.parseInt($valInt1.getText());
                $modifiers.put(om1, val1);
            } (OR valName2=STRING LBRACKET valInt2=INTEGER RBRACKET {
                String raw2 = $valName2.getText().toUpperCase().trim();
                ObjectModifier om2 = ObjectModifier.valueOf("OM_" + raw2);
                int val2 = Integer.parseInt($valInt2.getText());
                $modifiers.put(om2, val2);
            })*
        ;

// One full race record: name, then any mix of stats/skills/hitdie/exp/
// infravision/history/age/height/weight/flags/values in the order they
// appear in the file.
race
        returns[PlayerRace playerRace]
        @init {
            String nameInit = "";
            // BUG: never reassigned below - every PlayerRace ends up with
            // rIndex == 0 instead of its real, file-order-derived ridx. See
            // top-of-file problem #1.
            int rIndexInit = 0;
            Map<Stats, Integer> statsInit = new HashMap<>();
            Map<PlayerSkill, Integer> skillsInit = new HashMap<>();
            int hitDieInit = 0;
            int expInit = 0;
            int infraInit = 0;
            int bodyIntInit = 0;
            int historyInit = 0;
            int ageBaseInit = 0;
            int ageModInit = 0;
            int heightBaseInit = 0;
            int heightModInit = 0;
            int weightBaseInit = 0;
            int weightModInit = 0;
            Flag<ObjectFlag> oFlagsInit = new Flag<>(ObjectFlag.class);
            Flag<PlayerFlag> pFlagsInit = new Flag<>(PlayerFlag.class);
            Map<ObjectModifier, Integer> valuesInit = new HashMap<>();
        }
        @after {
            PlayerHistoryChart chart = GameConstants.lookupPlayerHistoryChart(historyInit);
            PlayerBody bodyInit = GameConstants.lookupPlayerBody(bodyIntInit);

            $playerRace = new PlayerRace(nameInit, rIndexInit, hitDieInit,
                expInit, ageBaseInit, ageModInit, heightBaseInit,
                heightModInit, weightBaseInit, weightModInit,
                infraInit, bodyInit, statsInit, skillsInit, oFlagsInit,
                pFlagsInit, chart, valuesInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   stats { statsInit = $stats.statsMap; }
        |   skill_disarm_phys { skillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, $skill_disarm_phys.value); }
        |   skill_disarm_magic { skillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, $skill_disarm_magic.value); }
        |   skill_device { skillsInit.put(PlayerSkill.SKILL_DEVICE, $skill_device.value); }
        |   skill_save { skillsInit.put(PlayerSkill.SKILL_SAVE, $skill_save.value); }
        |   skill_stealth { skillsInit.put(PlayerSkill.SKILL_STEALTH, $skill_stealth.value); }
        |   skill_search { skillsInit.put(PlayerSkill.SKILL_SEARCH, $skill_search.value); }
        |   skill_melee { skillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, $skill_melee.value); }
        |   skill_shoot { skillsInit.put(PlayerSkill.SKILL_TO_HIT_BOW, $skill_shoot.value); }
        |   skill_throw { skillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, $skill_throw.value); }
        |   skill_dig { skillsInit.put(PlayerSkill.SKILL_DIGGING, $skill_dig.value); }
        |   hitdie { hitDieInit = $hitdie.die; }
        |   exp { expInit = $exp.expInt; }
        |   infravision { infraInit = $infravision.infraInt; }
        |   history { historyInit = $history.histInt; }
        |   age { ageBaseInit = $age.ageBase; ageModInit = $age.ageMod; }
        |   height { heightBaseInit = $height.heightBase; heightModInit = $height.heightMod; }
        |   weight { weightBaseInit = $weight.weightBase; weightModInit = $weight.weightMod; }
        |   obj_flags { oFlagsInit = $obj_flags.flags; }
        |   player_flags { pFlagsInit = $player_flags.flags; }
        |   values { valuesInit = $values.modifiers; }
        )+;

// Top-level rule: the whole file is one or more race records.
file
        returns[List<PlayerRace> races]
        @init {
            $races = new ArrayList<>();
        }
        :   (race {
                $races.add($race.playerRace);
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

INFRAVISION
        :   'infravision:'
        ;

HISTORY
        :   'history:'
        ;

AGE
        :   'age:'
        ;

HEIGHT
        :   'height:'
        ;

WEIGHT
        :   'weight:'
        ;

OBJ_FLAGS
        :   'obj-flags:'
        ;

PLAYER_FLAGS
        :   'player-flags:'
        ;

VALUES
        :   'values:'
        ;

// A (possibly negative) literal integer - used by most numeric fields above.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// A flag/modifier/race name - letters, hyphen, underscore.
STRING
        :   ('A'..'Z' | 'a'..'z' | '-' | '_')+
        ;

// Brackets around a values: modifier's integer argument, e.g. "[1]".
LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        ;

// Field separator used throughout most directives.
COLON
        :   ':'
        ;

// The " | " separator between flag/value names on an obj-flags:/
// player-flags:/values: line.
OR
        :   ' | '
        ;

// POTENTIAL SOLUTIONS
//
//   1. Compute rIndexInit from file order - e.g. have `file` pass a running
//      counter into `race` (or simplest: after collecting all races into
//      $races in `file`, do a second pass setting each PlayerRace's index
//      to its list position) so it matches finish_parse_p_race's
//      zero-based, file-order ridx instead of always being 0.