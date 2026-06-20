grammar MonsterNest;

@header {
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.cave.PitProfile;
    import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.MonsterBase;
    import uk.co.jackoftrades.middle.monsters.MonsterRace;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
    import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;

    import java.util.ArrayList;
    import java.util.List;
}

name
        returns[String nameStr]
        :   NAME STRING { $nameStr = $STRING.getText(); }
        ;

room
        returns[PitRoomType roomTypeEnum]
        :   ROOM INTEGER {
                switch ($INTEGER.getText()) {
                    case "1":
                        $roomTypeEnum = PitRoomType.PIT_TYPE_PIT;
                        break;

                    case "2":
                        $roomTypeEnum = PitRoomType.PIT_TYPE_NEST;
                        break;

                    case "3":
                        $roomTypeEnum = PitRoomType.PIT_TYPE_OTHER;
                        break;

                    default:
                        $roomTypeEnum = PitRoomType.PIT_TYPE_NONE;
                        break;
                }
            }
        ;

alloc
        returns[int rarityInt, int levelInt]
        :   ALLOC rar=INTEGER COLON lev=INTEGER {
                $rarityInt = Integer.parseInt($rar.getText());
                $levelInt = Integer.parseInt($lev.getText());
            }
        ;

objRarity
        returns[int rarityInt]
        :   OBJ_RARITY INTEGER { $rarityInt = Integer.parseInt($INTEGER.getText()); }
        ;

colour
        returns[ColourType colourType]
        :   COLOUR cc=COLOUR_CHAR { $colourType = ColourType.findColourType($cc.getText().charAt(0)); }
        ;

monBase
        returns[MonsterBase base]
        :   MON_BASE STRING {
                String raw = $STRING.getText();
                $base = GameConstants.getMonsterBase(raw);
            }
        ;

flagsReq
        returns[Flag<MonsterRaceFlag> flags]
        @init {
            $flags = new Flag<>(MonsterRaceFlag.class);
        }
        :   FLAGS_REQ f1=STRING {
                String raw1 = "RF_" + $f1.getText().trim().toUpperCase();
                $flags.on(MonsterRaceFlag.valueOf(raw1));
            }
            (OR f2=STRING {
                String raw2 = "RF_" + $f2.getText().trim().toUpperCase();
                $flags.on(MonsterRaceFlag.valueOf(raw2));
            })*
        ;

flagsBan
        returns[Flag<MonsterRaceFlag> flags]
        @init {
            $flags = new Flag<>(MonsterRaceFlag.class);
        }
        :   FLAGS_BAN f1=STRING {
                String raw1 = "RF_" + $f1.getText().trim().toUpperCase();
                $flags.on(MonsterRaceFlag.valueOf(raw1));
            }
            (OR f2=STRING{
                String raw2 = "RF_" + $f2.getText().trim().toUpperCase();
                $flags.on(MonsterRaceFlag.valueOf(raw2));
            })*
        ;

innateFreq
        returns[int innateFreqInt]
        :   INNATE_FREQ INTEGER { $innateFreqInt = Integer.parseInt($INTEGER.getText()); }
        ;

spellReq
        returns[Flag<MonsterSpell> spells]
        @init {
            $spells = new Flag<>(MonsterSpell.class);
        }
        :   SPELL_REQ s1=STRING {
                String raw1 = "RSF_" + $s1.getText().trim().toUpperCase();
                $spells.on(MonsterSpell.valueOf(raw1));
            } (OR s2=STRING {
                String raw2 = "RSF_" + $s2.getText().trim().toUpperCase();
                $spells.on(MonsterSpell.valueOf(raw2));
            })*
        ;

spellBan
        returns[Flag<MonsterSpell> spells]
        @init {
            $spells = new Flag<>(MonsterSpell.class);
        }
        :   SPELL_BAN s1=STRING {
                String raw1 = "RSF_" + $s1.getText().trim().toUpperCase();
                $spells.on(MonsterSpell.valueOf(raw1));
            } (OR s2=STRING {
                String raw2 = "RSF_" + $s2.getText().trim().toUpperCase();
                $spells.on(MonsterSpell.valueOf(raw2));
            })*
        ;

monBan
        returns[MonsterRace race]
        :   MON_BAN STRING { $race = GameConstants.lookupMonsterRace($STRING.getText()); }
        ;

pit
        returns[PitProfile profile]
        @init {
            String nameInit = "";
            PitRoomType roomTypeInit = PitRoomType.PIT_TYPE_NONE;
            int aveInit = 0;
            int rarityInit = 0;
            int objectRarityInit = 0;
            Flag<MonsterRaceFlag> flagsInit = new Flag<>(MonsterRaceFlag.class);
            Flag<MonsterRaceFlag> forbiddenFlagsInit = new Flag<>(MonsterRaceFlag.class);
            int freqInnateInit = 0;
            Flag<MonsterSpell> spellFlags = new Flag<>(MonsterSpell.class);
            Flag<MonsterSpell> forbiddenSpellFlags = new Flag<>(MonsterSpell.class);
            List<MonsterBase> basesInit = new ArrayList<>();
            List<ColourType> coloursInit = new ArrayList<>();
            List<MonsterRace> forbiddenMonstersInit = new ArrayList<>();
        }
        @after {
            $profile = new PitProfile(nameInit, roomTypeInit, aveInit, rarityInit, objectRarityInit,
                                      flagsInit, forbiddenFlagsInit, freqInnateInit, spellFlags,
                                      forbiddenSpellFlags, basesInit, coloursInit, forbiddenMonstersInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   room { roomTypeInit = $room.roomTypeEnum; }
        |   alloc {
                aveInit = $alloc.levelInt;
                rarityInit = $alloc.rarityInt;
            }
        |   objRarity {  objectRarityInit = $objRarity.rarityInt; }
        |   colour { coloursInit.add($colour.colourType); }
        |   monBase { basesInit.add($monBase.base); }
        |   flagsReq { flagsInit.union($flagsReq.flags); }
        |   flagsBan { forbiddenFlagsInit.union($flagsBan.flags); }
        |   innateFreq { freqInnateInit = $innateFreq.innateFreqInt; }
        |   spellReq { spellFlags.union($spellReq.spells); }
        |   spellBan { forbiddenSpellFlags.union($spellBan.spells); }
        |   monBan { forbiddenMonstersInit.add($monBan.race); })+
        ;

file
        returns[List<PitProfile> pits]
        @init {
            $pits = new ArrayList<>();
        }
        :   (pit {
                $pits.add($pit.profile);
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

ROOM
        :   'room:'
        ;

ALLOC
        :   'alloc:'
        ;

OBJ_RARITY
        :   'obj-rarity:'
        ;

COLOUR
        :   'color:'
        ;

MON_BASE
        :   'mon-base:'
        ;

FLAGS_REQ
        :   'flags-req:'
        ;

FLAGS_BAN
        :   'flags-ban:'
        ;

INNATE_FREQ
        :   'innate-freq:'
        ;

SPELL_REQ
        :   'spell-req:'
        ;

SPELL_BAN
        :   'spell-ban:'
        ;

MON_BAN
        :   'mon-ban:'
        ;

INTEGER
        :   ('0'..'9')+
        ;

COLOUR_CHAR
        :   [dwsorgbuDWPyRGBUpvtmYiTVIMzZ]
        ;

STRING
        :   ('a'..'z' | 'A'..'Z' | ' ' | '-' | '_')+
        ;

COLON
        :   ':'
        ;

OR
        :   '|'
        ;
