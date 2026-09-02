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
 * Parser for lib/gamedata/chest_trap.txt, producing one
 * ChestTrapParseRecord per trap. Cf. src/obj-chest.c: struct file_parser
 * chest_trap_parser (obj-chest.c:283), directive table at obj-chest.c:245-254
 * (parse_chest_trap_name/_code/_level/_effect/_dice/_expr/_destroy/_magic/
 * _msg/_msg_death).
 *
 * === What this grammar checks, and what it deliberately leaves alone ===
 *
 * Only two things are structural here: a record starts with name:, and
 * code: follows it. Everything after that is an order-free repetition, which
 * mirrors how the C parser works - parser_reg registers each directive
 * independently and the file is walked line by line, so C accepts the
 * directives of one record in any order and would accept a record missing
 * any of them.
 *
 * That leaves the rules the data file states in prose - the first record is
 * the "no trap", levels ascend, no more than fourteen traps - unenforced by
 * this grammar. They are checked in ChestTrapAssembler instead, where a
 * failure can be reported against a line number and the whole file rejected.
 * C cannot check them at all; they survive there only as comments at the top
 * of chest_trap.txt.
 *
 * === Where the values go ===
 *
 * Every directive is captured as a String, unparsed and unresolved, exactly
 * as the other grammars here do: the assembler turns them into a
 * ChestTrapCode, an int level and booleans. effectBlock is the shared rule
 * from EffectBlock.g4 and may repeat, because one trap can carry several
 * effects - "poison needle" is DAMAGE followed by DRAIN_STAT - and a dice:
 * line binds to the effect block above it, matching C's walk to the tail of
 * the effect list (obj-chest.c:parse_chest_trap_dice).
 */
parser grammar ChestTrapGrammar;

options { tokenVocab = ChestTrapLexer; }

import EffectBlock;

@header {
    import uk.co.jackoftradesltd.backend.parser.chesttrap.ChestTrapParseRecord;
    import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

    import java.util.ArrayList;
    import java.util.List;
}

/*
 * @author Rowan Crowther
 *
 * The declared number of records in the file. An addition of this port -
 * the C parser registers no such directive for chest_trap.txt.
 */
recordCount
        returns[String count]
        :   RECORD_COUNT c=INTEGER { $count = $c.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "name:<text>" - the trap's display name, and the directive that opens a
 * record. Returns the line it was found on as well, so every error the
 * assembler reports about this trap can name a line in the file.
 *
 * Names are not unique ("gas trap" and "poison needle" each cover two
 * traps), so nothing may key on this - see the code rule below.
 */
name
        returns[String nameStr, int line]
        :   NAME n=STRING { 
            $nameStr = $n.getText();
            $line = $start.getLine();
        };

/*
 * @author Rowan Crowther
 *
 * "code:<CODE>" - the trap's identity, resolved to a ChestTrapCode by the
 * assembler. Captured as raw text so an unrecognised code is reported
 * against its line rather than failing to lex.
 */
code
        returns[String codeStr]
        :   CODE c=STRING { $codeStr = $c.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "level:<integer>" - the minimum chest level this trap can appear on.
 */
level
        returns[String levelStr]
        :   LEVEL l=INTEGER { $levelStr = $l.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "destroy:<integer>" - 1 if springing the trap destroys the contents.
 * Kept as text; the assembler decides what counts as true.
 */
destroy 
        returns[String destroyStr]
        :   DESTROY d=INTEGER { $destroyStr = $d.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "magic:<integer>" - 1 if the trap is magical rather than physical.
 */
magic
        returns[String magicStr]
        :   MAGIC m=INTEGER { $magicStr = $m.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "msg:<free text>" - the message shown when the trap fires.
 */
msg
        returns[String msgStr]
        :   MSG m=STRING { $msgStr = $m.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * "msg-death:<free text>" - the message shown if the trap kills the
 * character.
 */
msgDeath
        returns[String msgDeathStr]
        :   MSG_DEATH m=STRING { $msgDeathStr = $m.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * One whole trap record, gathered into a ChestTrapParseRecord.
 *
 * The shape is "name:, then code:, then any number of the rest in any
 * order". name: has to lead because it is what tells one record from the
 * next - it is the directive C allocates on (obj-chest.c:59-76) - and code:
 * is required immediately after it because this port cannot identify a trap
 * without it, where C could carry on regardless.
 *
 * Everything else is a repetition rather than a sequence, so a record may
 * omit any of them: "locked" has no effect and no messages, only one trap
 * sets destroy:, only one sets magic:. Repeating a directive overwrites the
 * previous value - except effectBlock, which accumulates. No current data
 * file repeats one, though note that C would concatenate a repeated msg:
 * rather than overwrite it.
 *
 * @init seeds every field with an empty value, so a directive that is simply
 * absent arrives at the assembler as "" rather than null; a directive that is
 * present but malformed still arrives as null, because ANTLR's error
 * recovery leaves the sub-rule's return unset and the assignment below
 * copies that null over the default. The assembler guards for both.
 */
chestTrap
        returns[ChestTrapParseRecord trap]
        @init {
            String nameInit = "";
            String codeInit = "";
            String levelInit = "";
            List<EffectParseRecord> effectInit = new ArrayList<>();
            String destroyInit = "";
            String magicInit = "";
            String msgInit = "";
            String msgDeathInit = "";
            int lineInit = 0;
        }
        @after {
            $trap = new ChestTrapParseRecord(nameInit, codeInit, levelInit,
                effectInit, destroyInit, magicInit, msgInit, msgDeathInit,
                lineInit);
        }
        :   name { nameInit = $name.nameStr; 
                   lineInit = $name.start.getLine(); }
            code { codeInit = $code.codeStr; }
        (   level { levelInit = $level.levelStr; }
        |   effectBlock { effectInit.add(new EffectParseRecord($effectBlock.typeInit,
                $effectBlock.subtypeWrapperInit, $effectBlock.radius, $effectBlock.other,
                $effectBlock.diceString, $effectBlock.yVal, $effectBlock.xVal,
                $effectBlock.expressionChars, $effectBlock.expressionBase,
                $effectBlock.expressionOperation, $effectBlock.timeDiceString,
                $effectBlock.effectMessage, $effectBlock.start.getLine())); }
        |   destroy { destroyInit = $destroy.destroyStr; }
        |   magic { magicInit = $magic.magicStr; }
        |   msg { msgInit = $msg.msgStr; }
        |   msgDeath { msgDeathInit = $msgDeath.msgDeathStr; } )*
        ;

/*
 * @author Rowan Crowther
 *
 * The whole file: the record count, then one or more traps, then EOF.
 *
 * Both returns are read by ChestTrapReader - the count is compared against
 * the number of records recovered, and the list is handed to
 * ChestTrapAssembler. Requiring EOF is what stops a trailing malformed
 * record being silently ignored.
 */
file
        returns[String declaredRecordCount, List<ChestTrapParseRecord> chestTraps]
        :   recordCount { $declaredRecordCount = $recordCount.count;
                          $chestTraps = new ArrayList<>(); }
            (chestTrap { $chestTraps.add($chestTrap.trap); })+ EOF
        ;
