/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

/*
 * @author Rowan Crowther
 *
 * Parser for lib/gamedata/dungeon_profile.txt, over the tokens produced by
 * DungeonProfileLexer.
 *
 * Cf. the C parser in src/generate.c: struct file_parser profile_parser
 * (generate.c:306). C registers one callback per directive and keeps the
 * record being built in the parser's priv pointer, so "any directive, any
 * order, until the next name:" falls out of the design. This grammar says the
 * same thing explicitly: 'profile' is one 'name' followed by a repeated choice
 * of the other directives, and 'file' is a run of profiles.
 *
 * Each rule carries its fields out in a @init/@after pair rather than through
 * a listener, building the DungeonProfileParseRecord DTO as it goes. Every
 * field is left as a String here; converting to int/boolean, and resolving the
 * profile and room names against their builder enums, is the assembler's job,
 * so a malformed number is reported as a data error rather than a parse error.
 *
 * Rules capture their source line via $start.getLine(), so those assembler-stage
 * errors can name the line they came from.
 */
parser grammar DungeonProfileGrammar;

options { tokenVocab = DungeonProfileLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord;
    import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord.*;

    import java.util.List;
    import java.util.ArrayList;
}

/*
 * @author Rowan Crowther
 *
 * The file's declared record count, checked by the reader against the number
 * of profiles actually parsed. A port-ism: C has no such header.
 *
 * @return count the declared count, unparsed
 */
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "name:<profile name>" - opens a profile record.
 * Cf. parse_profile_name (generate.c:93), which also resolves the name against
 * cave_builders[]; that resolution is deferred to the assembler here.
 *
 * The line number is carried out because it is the profile's own line, and so
 * identifies the whole record in any later error message.
 *
 * @return profileName the name, verbatim to end of line
 * @return lineNo      the source line the record starts on
 */
name
        returns [String profileName, int lineNo]
        :   NAME STRING { $profileName = $STRING.getText(); $lineNo = $start.getLine(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "params:<block>:<rooms>:<unusual>:<rarity>".
 * Cf. parse_profile_params (generate.c:111).
 *
 * @return paramsRecord the four values, unparsed
 */
params
        returns[Params paramsRecord]
        @init {
            String blockSize;
            String rooms;
            String unusual;
            String rarity;
        }
        @after {
            $paramsRecord = new Params(blockSize, rooms, unusual, rarity);
        }
        :   PARAMS b=INTEGER COLON ro=INTEGER COLON u=INTEGER COLON ra=INTEGER {
                blockSize = $b.getText();
                rooms = $ro.getText();
                unusual = $u.getText();
                rarity = $ra.getText();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "tunnel:<rnd>:<chg>:<con>:<pen>:<jct>" - the five corridor-digging chances.
 * Cf. parse_profile_tunnel (generate.c:123).
 *
 * The C field names are terse, so they are spelled out here: rnd is the chance
 * of digging in a random direction rather than towards the target, chg of
 * changing direction at all, con of simply stopping, pen of a door where the
 * tunnel pierces a room, jct of a door at a junction.
 *
 * @return tunnelRecord the five values, unparsed, plus the source line
 */
tunnel
        returns[Tunnel tunnelRecord]
        @init {
            String random;
            String change;
            String conclude;
            String entranceDoor;
            String junction;
            int lineNo;
        }
        @after {
            $tunnelRecord = new Tunnel(random, change, conclude, entranceDoor, junction, lineNo);
        }
        :   TUNNEL r=INTEGER COLON ch=INTEGER COLON co=INTEGER COLON e=INTEGER COLON j=INTEGER {
                random = $r.getText();
                change = $ch.getText();
                conclude = $co.getText();
                entranceDoor = $e.getText();
                junction = $j.getText();
                lineNo = $start.getLine();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "streamer:<den>:<rng>:<mag>:<mc>:<qua>:<qc>" - the mineral veins.
 * Cf. parse_profile_streamer (generate.c:136).
 *
 * As with tunnel, the C names are expanded: den is how many grids near each
 * step of the streamer's random walk become vein, rng how far from the walk
 * those grids may lie, mag and qua how many magma and quartz streamers the
 * level gets, and mc and qc the reciprocal treasure chance in each - a
 * treasure appears with probability 1/mc, so a larger number means less
 * treasure.
 *
 * @return streamerRecord the six values, unparsed, plus the source line
 */
streamer
        returns[Streamer streamerRecord]
        @init {
            String density;
            String range;
            String magma;
            String magmaChance;
            String quartz;
            String quartzChance;
            int lineNo;
        }
        @after {
            $streamerRecord = new Streamer(density, range, magma, magmaChance, quartz, quartzChance, lineNo);
        }
        :   STREAMER d=INTEGER COLON r=INTEGER COLON m=INTEGER COLON mc=INTEGER COLON q=INTEGER COLON qc=INTEGER {
                density = $d.getText();
                range = $r.getText();
                magma = $m.getText();
                magmaChance = $mc.getText();
                quartz = $q.getText();
                quartzChance = $qc.getText();
                lineNo = $start.getLine();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * "alloc:<n>" - the profile's selection weight; may be negative.
 * Cf. parse_profile_alloc (generate.c:197).
 *
 * @return allocChance the weight, unparsed
 */
alloc
        returns[String allocChance]
        :   ALLOC INTEGER { $allocChance = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "min-level:<n>" - the shallowest depth this profile may be used at.
 * Cf. parse_profile_min_level (generate.c:188).
 *
 * @return minLevelValue the depth, unparsed
 */
minLevel
        returns[String minLevelValue]
        :   MIN_LEVEL INTEGER { $minLevelValue = $INTEGER.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "room:<name>:<rating>:<height>:<width>:<level>:<pit>:<rarity>:<cutoff>" -
 * one room the enclosing profile may contain.
 * Cf. parse_profile_room (generate.c:150), which appends to a linked list and
 * resolves the name against room_builders[]; here the rooms accumulate into a
 * List on the enclosing 'profile' rule and the name is resolved later.
 *
 * The room name arrives as TEXT_BETWEEN_COLON rather than a word token, and no
 * COLON follows it, because the lexer's DELIMITED_TEXT mode skips the ':' that
 * ends the name on its way out.
 *
 * pit is a 0/1 int in the file but a bool in C; it stays a String here and is
 * narrowed by the assembler.
 *
 * @return roomRecord the eight values, unparsed, plus the source line
 */
room
        returns[Room roomRecord]
        @init {
            String roomName;
            String rating;
            String height;
            String width;
            String level;
            String pit;
            String rarity;
            String cutoff;
            int lineNo;
        }
        @after {
            $roomRecord = new Room(roomName, rating, height, width, level, pit, rarity, cutoff, lineNo);
        }
        :   ROOM rn=TEXT_BETWEEN_COLON rt=INTEGER COLON h=INTEGER COLON w=INTEGER COLON l=INTEGER COLON p=INTEGER COLON rr=INTEGER COLON ct=INTEGER {
                roomName = $rn.getText();
                rating = $rt.getText();
                height = $h.getText();
                width = $w.getText();
                level = $l.getText();
                pit = $p.getText();
                rarity = $rr.getText();
                cutoff = $ct.getText();
                lineNo = $start.getLine();
            }
        ;

/*
 * @author Rowan Crowther
 *
 * One whole profile: the opening 'name' line, then its directives in any
 * order and any number, up to the next 'name' (which starts the next profile)
 * or end of file.
 *
 * The single-valued directives are seeded null so the assembler can tell "line
 * absent" from "line present and zero" - a distinction C cannot make, since
 * mem_zalloc leaves an absent field at 0 exactly as an explicit 0 would. It
 * matters for alloc, where 0 disables a profile outright.
 *
 * The loop admits repeats of a single-valued directive, in which case the last
 * one wins. That mirrors C, where each callback simply overwrites the field.
 *
 * @return record the assembled parse record for this profile
 */
profile
        returns[DungeonProfileParseRecord record]
        @init {
            String profileName = "";
            Params profileParams = null;
            Tunnel profileTunnel = null;
            Streamer profileStreamer = null;
            String allocNum = null;
            String minLevelNum = null;
            List<Room> rooms = new ArrayList<>();
            int lineNo = 0;
        }
        @after { $record = new DungeonProfileParseRecord(profileName,
            profileParams, profileTunnel, profileStreamer, allocNum,
            minLevelNum, rooms, lineNo); }
        :   name { profileName = $name.profileName;
                   lineNo = $start.getLine(); }
        (   params { profileParams = $params.paramsRecord; }
        |   tunnel { profileTunnel = $tunnel.tunnelRecord; }
        |   streamer { profileStreamer = $streamer.streamerRecord;}
        |   alloc { allocNum = $alloc.allocChance; }
        |   minLevel { minLevelNum = $minLevel.minLevelValue; }
        |   room { rooms.add($room.roomRecord); }
        )+;

/*
 * @author Rowan Crowther
 *
 * The whole file: the record-count header, then one or more profiles.
 * EOF is matched explicitly so trailing rubbish is an error rather than
 * silently ignored.
 *
 * @return declaredRecordCount the header's count, for the reader to check
 * @return profiles            every profile in the file, in file order
 */
file
        returns[String declaredRecordCount, List<DungeonProfileParseRecord> profiles]
        @init {
            $profiles = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (profile { $profiles.add($profile.record); })+ EOF
        ;
