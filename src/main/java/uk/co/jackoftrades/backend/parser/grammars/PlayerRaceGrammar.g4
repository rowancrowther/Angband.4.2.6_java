parser grammar PlayerRaceGrammar;

options { tokenVocab = PlayerRaceLexer; }

@header {
    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;
    import java.util.HashMap;

    import uk.co.jackoftrades.backend.parser.playerrace.PlayerRaceParseRecord;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

stats
        returns[Map<String, String> statsMap]
        @init {
            $statsMap = new HashMap<>();
        }
        :   STATS str=INTEGER COLON intVal=INTEGER COLON wis=INTEGER COLON dex=INTEGER COLON con=INTEGER {
                $statsMap.put("STAT_STR", $str.getText());
                $statsMap.put("STAT_INT", $intVal.getText());
                $statsMap.put("STAT_WIS", $wis.getText());
                $statsMap.put("STAT_DEX", $dex.getText());
                $statsMap.put("STAT_CON", $con.getText());
            }
        ;

skillDisarmPhys
        returns[String skillDisarmPhysValue]
        :   SKILL_DISARM_PHYS INTEGER { $skillDisarmPhysValue = $INTEGER.getText(); }
        ;

skillDisarmMagic
        returns[String skillDisarmMagicValue]
        :   SKILL_DISARM_MAGIC INTEGER { $skillDisarmMagicValue = $INTEGER.getText(); }
        ;

skillDevice
        returns[String skillDeviceValue]
        :   SKILL_DEVICE INTEGER { $skillDeviceValue = $INTEGER.getText(); }
        ;

skillSave
        returns[String skillSaveValue]
        :   SKILL_SAVE INTEGER { $skillSaveValue = $INTEGER.getText(); }
        ;

skillStealth
        returns[String skillStealthValue]
        :   SKILL_STEALTH INTEGER { $skillStealthValue = $INTEGER.getText(); }
        ;

skillSearch
        returns[String skillSearchValue]
        :   SKILL_SEARCH INTEGER { $skillSearchValue = $INTEGER.getText(); }
        ;

skillMelee
        returns[String skillMeleeValue]
        :   SKILL_MELEE INTEGER { $skillMeleeValue = $INTEGER.getText(); }
        ;

skillShoot
        returns[String skillShootValue]
        :   SKILL_SHOOT INTEGER { $skillShootValue = $INTEGER.getText(); }
        ;

skillThrow
        returns[String skillThrowValue]
        :   SKILL_THROW INTEGER { $skillThrowValue = $INTEGER.getText(); }
        ;

skillDig
        returns[String skillDigValue]
        :   SKILL_DIG INTEGER { $skillDigValue = $INTEGER.getText(); }
        ;

hitdie
        returns[String hitDieStr]
        :   HITDIE INTEGER { $hitDieStr = $INTEGER.getText(); }
        ;

exp
        returns[String expStr]
        :   EXP INTEGER { $expStr = $INTEGER.getText(); }
        ;

infravision
        returns[String infra]
        :   INFRAVISION INTEGER { $infra = $INTEGER.getText(); }
        ;

history
        returns[String startingHistory]
        :   HISTORY INTEGER { $startingHistory = $INTEGER.getText(); }
        ;

age
        returns[String ageBase, String ageMod]
        :   AGE b=INTEGER COLON m=INTEGER {
                $ageBase = $b.getText();
                $ageMod = $m.getText();
            }
        ;

height
        returns[String heightBase, String heightMod]
        :   HEIGHT b=INTEGER COLON m=INTEGER {
                $heightBase = $b.getText();
                $heightMod = $m.getText();
            }
        ;

weight
        returns[String weightBase, String weightMod]
        :   WEIGHT b=INTEGER COLON m=INTEGER {
                $weightBase = $b.getText();
                $weightMod = $m.getText();
            }
        ;

objFlags
        returns[List<String> oFlags]
        @init {
            $oFlags = new ArrayList<>();
        }
        :   OBJ_FLAGS (f1=FLAG {
                $oFlags.add($f1.getText());
            } (FLAG_OR f2=FLAG {
                $oFlags.add($f2.getText());
            })* )?
        ;

playerFlags
        returns[List<String> pFlags]
        @init {
                $pFlags = new ArrayList<>();
        }
        :   PLAYER_FLAGS (f1=FLAG {
                $pFlags.add($f1.getText());
            } (FLAG_OR f2=FLAG {
                $pFlags.add($f2.getText());
            })* )?
        ;

values
        returns[Map<String, String> valueMap]
        @init {
            $valueMap = new HashMap<>();
        }
        :   VALUES n1=VALUE_NAME VALUE_LBRACKET v1=VALUE_INT VALUE_RBRACKET {
                $valueMap.put($n1.getText(), $v1.getText());
            }
            (VALUE_OR n2=VALUE_NAME VALUE_LBRACKET v2=VALUE_INT VALUE_RBRACKET{
                $valueMap.put($n2.getText(), $v2.getText());
            })*
        ;

playerRace
        returns[PlayerRaceParseRecord race]
        @init {
            String nameInit = "";
            Map<String, String> statsInit = new HashMap<>();
            Map<String, String> skillsInit = new HashMap<>();
            String hitdieInit = "";
            String expInit = "";
            String infravisionInit = "";
            String historyInit = "";
            String ageBaseInit = "";
            String ageModifierInit = "";
            String heightBaseInit = "";
            String heightModifierInit = "";
            String weightBaseInit = "";
            String weightModifierInit = "";
            List<String> oFlagsInit = new ArrayList<>();
            List<String> pFlagsInit = new ArrayList<>();
            Map<String, String> valuesInit = new HashMap<>();
            int lineInit = 0;
        }
        @after {
            $race = new PlayerRaceParseRecord(nameInit, statsInit, skillsInit,
                    hitdieInit, expInit, infravisionInit, historyInit,
                    ageBaseInit, ageModifierInit, heightBaseInit, heightModifierInit,
                    weightBaseInit, weightModifierInit, oFlagsInit, pFlagsInit,
                    valuesInit, lineInit);
        }
        :   name {
                nameInit = $name.nameStr;
                lineInit = $name.line;
            }
        (   stats { statsInit.putAll($stats.statsMap); }
        |   skillDisarmPhys { skillsInit.put("SKILL_DISARM_PHYS", $skillDisarmPhys.skillDisarmPhysValue); }
        |   skillDisarmMagic { skillsInit.put("SKILL_DISARM_MAGIC", $skillDisarmMagic.skillDisarmMagicValue); }
        |   skillDevice { skillsInit.put("SKILL_DEVICE", $skillDevice.skillDeviceValue); }
        |   skillSave { skillsInit.put("SKILL_SAVE", $skillSave.skillSaveValue); }
        |   skillStealth { skillsInit.put("SKILL_STEALTH", $skillStealth.skillStealthValue); }
        |   skillSearch { skillsInit.put("SKILL_SEARCH", $skillSearch.skillSearchValue); }
        |   skillMelee { skillsInit.put("SKILL_TO_HIT_MELEE", $skillMelee.skillMeleeValue); }
        |   skillShoot { skillsInit.put("SKILL_TO_HIT_BOW", $skillShoot.skillShootValue); }
        |   skillThrow { skillsInit.put("SKILL_TO_HIT_THROW", $skillThrow.skillThrowValue); }
        |   skillDig { skillsInit.put("SKILL_DIGGING", $skillDig.skillDigValue); }
        |   hitdie { hitdieInit = $hitdie.hitDieStr; }
        |   exp { expInit = $exp.expStr; }
        |   infravision { infravisionInit = $infravision.infra; }
        |   history { historyInit = $history.startingHistory; }
        |   age { ageBaseInit = $age.ageBase; ageModifierInit = $age.ageMod; }
        |   height { heightBaseInit = $height.heightBase; heightModifierInit = $height.heightMod; }
        |   weight { weightBaseInit = $weight.weightBase; weightModifierInit = $weight.weightMod; }
        |   objFlags { oFlagsInit.addAll($objFlags.oFlags); }
        |   playerFlags { pFlagsInit.addAll($playerFlags.pFlags); }
        |   values { valuesInit.putAll($values.valueMap); })+
        ;

file
        returns[List<PlayerRaceParseRecord> races, String declaredRecordCount]
        @init {
            $races = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (playerRace { $races.add($playerRace.race); })+ EOF
        ;
