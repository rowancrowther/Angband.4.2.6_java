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

// Parser for lib/gamedata/object_base.txt, paired with ObjectBaseLexer. Each
// directive rule strips its keyword and returns the raw String payload; the
// `object_base` rule accumulates one base's directives (name/graphics header,
// then optional break/max-stack and any number of flags: lines) into an
// ObjectBaseParseRecord, and `file` reads the record-count header, captures the
// two file-wide default: values, and folds them into any base that omitted its
// own break/max-stack. No interpretation happens here: tval/colour/flag/integer
// resolution is deferred to ObjectBaseAssembler, so the rules deal only in raw
// text. Cf. src/obj-init.c struct file_parser object_base_parser.
//
// @author Rowan Crowther

parser grammar ObjectBaseGrammar;
options { tokenVocab = ObjectBaseLexer; }


@header {
    import uk.co.jackoftrades.backend.parser.objectbase.ObjectBaseParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// "default:break-chance:<value>" or "default:max-stack:<value>" - file-wide
// fallback values applied in `file` to any base that omits its own break: or
// max-stack:. Each invocation sets exactly one of the two returns; the other
// stays empty.
default_value
        returns[String maxStackNum, String breakChanceNum]
        @init {
            $maxStackNum = "";
            $breakChanceNum = "";
        }
        :   DEFAULT_BREAK_CHANCE val1=INTEGER {
                $breakChanceNum = $val1.getText();
            }
        |   DEFAULT_MAX_STACK val2=INTEGER {
                $maxStackNum = $val2.getText();
            }
        ;

// "name:<tval>[:<plural name>]" - starts a new base record. Two forms: a tval
// followed by a plural name after a second ':' (first alternative, e.g.
// "name:sword:Bladed weapon~"), or the tval alone (second alternative, e.g.
// "name:gold"). Only the raw tval/name strings are captured here; upper-casing
// and TV_* resolution are the assembler's job.
name
        returns[String tValue, String nameStr, int lineNo]
        :   (NAME tval1=T_VAL T_VAL_COLON nameIn=STRING) {
                $tValue  = $tval1.getText();
                $nameStr = $nameIn.getText();
                $lineNo  = $start.getLine();
            }
        |   (NAME tval2=T_VAL NAME_EOL) {
                $tValue  = $tval2.getText();
                $nameStr = "";
                $lineNo  = $start.getLine();
            }
        ;

// "graphics:<colour name>" - default inventory-display colour for this base.
graphics
        returns[String colour]
                :   GRAPHICS STRING { $colour = $STRING.getText(); }
                ;

// "break:<percent>" - breakage chance when thrown.
break_chance
        returns[String breakChance]
        :   BREAK INTEGER {
                $breakChance = $INTEGER.getText();
            }
        ;

// "max-stack:<count>" - maximum stack size for items of this base.
max_stack
        returns[String maxStack]
        :   MAX_STACK INTEGER {
                $maxStack = $INTEGER.getText();
            }
        ;

// "flags:<FLAG> [| <FLAG> ...]" - base-level flag names, captured raw. The
// assembler splits them: HATES_* map to element-vulnerability flags, the rest to
// ObjectKindFlag (see ObjectBaseAssembler). May appear on several lines per base;
// `object_base` addAll's each line so repeats accumulate rather than replace.
flags
        returns[List<String> flagsList]
        @init {
            $flagsList = new ArrayList<>();
        }
        :   FLAGS flag1=FLAG {
                $flagsList.add($flag1.getText());
            }
            (FLAG_OR flag2=FLAG {
                $flagsList.add($flag2.getText());
            })* FLAG_EOL
        ;

// One full base record: name/graphics header, then optional break/max-stack
// (an absent one arrives as an empty string, later filled from the file-wide
// defaults in `file`) and zero or more flags: lines, whose values accumulate.
object_base
        returns[ObjectBaseParseRecord objectBase]
        @init {
            String name = "";
            String tVal = "";
            String colour = "";
            List<String> flags = new ArrayList<>();
            String breakChance = "";
            String maxStack = "";
            int line = 0;
        }
        @after {
            $objectBase = new ObjectBaseParseRecord(
                name, tVal, colour, flags, breakChance, maxStack, line);
        }
        :   name {
                line = $name.lineNo;
                name = $name.nameStr;
                tVal = $name.tValue;
            }
            graphics {
                colour = $graphics.colour;
            }
            (break_chance {
                breakChance = $break_chance.breakChance;
            })?
            (max_stack {
                maxStack = $max_stack.maxStack;
            })?
            (flags {
                flags.addAll($flags.flagsList);
            })*
        ;

// Top-level rule: the record-count header, the file-wide default: lines, then
// one or more base records - folding the defaults into any base that left
// break/max-stack empty before collecting it.
file
        returns[List<ObjectBaseParseRecord> objectBaseList, String declaredRecordCount]
        @init {
            $objectBaseList = new ArrayList<>();
            String defaultMaxStack = "";
            String defaultBreakChance = "";
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }

            (default_value {
                if ($default_value.maxStackNum.isEmpty())
                    defaultBreakChance = $default_value.breakChanceNum;
                else
                    defaultMaxStack = $default_value.maxStackNum;
            })+

            (object_base {
                ObjectBaseParseRecord base = $object_base.objectBase;
                ObjectBaseParseRecord newBase;

                if (base.breakChance().isEmpty() || base.maxStack().isEmpty()) {
                    newBase = new ObjectBaseParseRecord(base.name(), base.tVal(), base.colour(),
                    base.flags(),
                    base.breakChance().isEmpty() ? defaultBreakChance : base.breakChance(),
                    base.maxStack().isEmpty() ? defaultMaxStack : base.maxStack(), base.line());
                } else {
                    newBase = base;
                }

                // add it to the list
                $objectBaseList.add(newBase);
            })+ EOF
        ;
