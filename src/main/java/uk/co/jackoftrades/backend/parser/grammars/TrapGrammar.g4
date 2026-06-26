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

// Reader for lib/gamedata/trap.txt, producing one uk.co.jackoftrades.middle.cave.TrapKind
// per record (traps, runes/glyphs, decoys, door locks, etc). Mirrors the C
// parser in src/init.c: struct file_parser trap_parser (init.c:1989), whose
// directive table is registered in init_parse_trap() (init.c:1907-1928) and
// whose per-directive handlers are parse_trap_name/_graphics/_appear/
// _visibility/_flags/_effect/_effect_yx/_dice/_expr/_effect_xtra/.../_save_
// flags/_desc/_msg* (init.c:1515-1928), filling in a `struct trap_kind`.
//
// This file is mid-refactor: the `effect` rule and the @members
// resolveWrapper() helper have already been pulled toward a cleaner,
// shared-helper model, but `effectXtra`/`effectBlock` haven't caught up yet,
// and several actions below are not valid Java.
//
// POTENTIAL PROBLEMS (severity-ordered; see inline "BUG"/"PROBLEM" comments
// at each site, and "POTENTIAL SOLUTIONS" at the bottom of this file):
//
//   0. RESOLVED, not actually blocking: TrapKind.java:53-74 already has a
//      16-arg constructor whose parameter order exactly matches what
//      `trap`'s @after block below already calls (nameInit, textInit,
//      descInit, saveMessInit, failMessInit, xtraMessInit, 0, adcInit,
//      rarityInit, minDepthInit, maxDepthInit, visibilityInit, flagsInit,
//      saveInit, effectInit, effectXtraInit). There's nowhere left for this
//      grammar to lack a place to put its parsed values - the bugs below
//      are the real blockers.
//
//   1. Several actions are not valid Java and will fail ANTLR code
//      generation outright: unbalanced parens in `flags`/`save` - each
//      `$trapFlags.on(TrapEnum.valueOf(raw1);` /
//      `$objectFlags.on(ObjectFlag.valueOf(raw2);` closes valueOf(...)'s own
//      paren but never closes the outer `.on(...)` call before the ';'
//      (4 occurrences); `effectXtra`'s `$radius`/`$extraParm` are declared
//      `String` in `returns[...]` but assigned `Integer.parseInt(...)` (an
//      `int`) at the `COLON rad=INTEGER`/`COLON xtr=INTEGER` actions -
//      `effect` already does this correctly with plain `$rad.getText()`/
//      `$xtr.getText()`, `effectXtra` doesn't; and `String.length` used as
//      a field instead of a `.length()` call in `graphics`.
//
//   2. `effectXtra` still duplicates the old per-subtype switch inline
//      instead of calling the new @members resolveWrapper() helper that
//      `effect` already delegates to, and calls `.getSubType()` on
//      `$eff.getText()` - the raw subtype String, not the constructed
//      EffectEnum - that method doesn't exist on a String either, so this
//      can't compile.
//
//   3. resolveWrapper()'s EST_TELEPORT case (and effectXtra's duplicate of
//      it) calls `new EffectSubTypeWrapper(te)` with a bare TeleportEnum,
//      but EffectSubTypeWrapper only has a (TeleportEnum, boolean) overload
//      for that type - and it hardcodes "TELE_TELEPORT" instead of using
//      the parsed subtype text at all. Currently unreachable in practice
//      since trap.txt's only "effect:TELEPORT" line has no subtype segment,
//      but it's broken the moment that changes.
//
//   4. `effectBlock` matches `effect dice expr` as a flat, fully mandatory
//      sequence (no '?' on dice/expr). Most of trap.txt's effects have no
//      `expr:` line at all (only the $S/$B/$D-dice ones do), and some (e.g.
//      "effect:WAKE") have no `dice:` line either - this rule as written
//      cannot parse the majority of the actual file.
//
//   5. Structural mismatch vs. the C parser, in two parts:
//
//        - parse_trap_effect/_effect_xtra (init.c:1597, :1717) each append a
//          NEW node to a linked list every time an `effect:`/`effect-xtra:`
//          line is seen - trap.txt regularly has several effect/dice/expr
//          triples back to back per trap (e.g. 4 consecutive groups around
//          line 404-414, or two effect-xtra/dice-xtra pairs for the spiked
//          pit around line 124-127). `trap` only has single `Effect
//          effectInit`/`effectXtraInit` fields (not lists), so a second
//          effect/effect-xtra block in the same record overwrites the first
//          instead of accumulating.
//
//        - More immediately: `effectXtra`/`diceXtra`/`exprXtra` are already
//          grouped via the `effectXtraBlock` rule below (not left as bare,
//          ungrouped alternatives), so a dice-xtra/expr-xtra line already
//          attaches to the right effect-xtra. What's actually broken is the
//          surrounding `(appear | visibility | ... | effectXtraBlock)`
//          alternation in `trap` (lines 520-560): it has no `*`/`+` at all,
//          so it matches only ONCE per trap record - contradicting this
//          rule's own header comment ("any number of the directives above
//          in any order"). Almost every real trap record has 4+ of these
//          directives, so this would reject nearly the whole file even once
//          every other bug here is fixed. Also, the `effectXtraBlock`
//          alternative's action reads `$effectXtra.block`, but the rule
//          actually invoked there is `effectXtraBlock`, not `effectXtra` -
//          should be `$effectXtraBlock.block`.
//
//   6. `appear:` is documented (both in trap.txt's own header and in
//      init.c's parse_trap_appear, which stores it as `t->max_num` - itself
//      commented "/**< Unused */" in the upstream trap.h) as "rarity :
//      minimum depth : MAXIMUM NUMBER ON LEVEL", not a depth. The `trap`
//      rule stores `$appear.minNum` into a variable named `maxDepthInit`,
//      and TrapKind has no `maxNum`/`maxOnLevel` field at all - only
//      `maxDepth`. The "how many of this trap per level" value would
//      silently masquerade as a depth.
//
//   7. Missing support for `effect-yx:`/`effect-yx-xtra:` (see TrapLexer.g4 -
//      these directives exist in the original format/parser but aren't
//      used by this repo's trap.txt, so the gap is latent rather than
//      currently broken).
//
//   8. No TrapReader.java exists in backend/parser/ (unlike every other
//      gamedata grammar, which has a corresponding XxxReader.java) - this
//      grammar is not wired into the parsing pipeline yet.
//
//   9. `trap`'s `msgXtra` alternative (line ~548) assigns to `xtraMessave`,
//      a variable that was never declared - the rule's @init block declares
//      `xtraMessInit` (the one actually passed into the @after TrapKind
//      construction), not `xtraMessave`. Won't compile.
//
//  10. resolveWrapper()'s EST_PROJ case uses a bare `Projection` type, which
//      is never imported (only `uk.co.jackoftrades.middle.combat.enums.
//      ProjectionEnum` is, via @header) - and the only class actually named
//      `Projection` in this codebase (`uk.co.jackoftrades.middle.game.
//      Projection`) is an unrelated plain class with no `valueOf` method.
//      EffectSubTypeWrapper's matching constructor overload takes a
//      `ProjectionEnum`, not a `Projection` - that's the type this case
//      should be using.
//
//  11. Runtime, not compile-time: TrapEnum declares `TRF_SAVE_ARMOUR`
//      (British spelling), but trap.txt's flags: lines use `SAVE_ARMOR`
//      (American, no U) four times - the four dart traps. `flags`'s
//      `TrapEnum.valueOf("TRF_" + "SAVE_ARMOR")` throws
//      IllegalArgumentException - this only bites once every compile bug
//      above is fixed and the grammar actually runs against real data.

parser grammar TrapGrammar;
options { tokenVocab = TrapLexer; }

@header {
    import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
    import uk.co.jackoftrades.backend.utils.Flag;
    import uk.co.jackoftrades.middle.cave.TrapKind;
    import uk.co.jackoftrades.middle.effect.Earthquake;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeEnum;
    import uk.co.jackoftrades.middle.effect.EffectSubTypeWrapper;
    import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
    import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
    import uk.co.jackoftrades.middle.enums.*;
    import uk.co.jackoftrades.middle.game.globals.GameConstants;
    import uk.co.jackoftrades.middle.monsters.Summon;
    import uk.co.jackoftrades.middle.player.PlayerShape;
    import uk.co.jackoftrades.middle.player.enums.TimedEffect;
    import uk.co.jackoftrades.frontend.colour.enums.ColourType;
}

// Shared helper: turns an effect's subtype text (e.g. "FIRE", "CUT",
// "STR") into the right kind of EffectSubTypeWrapper based on the
// effect's EffectSubTypeEnum (EST_PROJ/EST_TMD/EST_STAT/EST_SUMMON/
// EST_TELEPORT/EST_EARTHQUAKE - the only subtypes any trap.txt effect type
// actually resolves to). Used by `effect`; `effectXtra` should use this too
// but currently doesn't (see problem #2).
@members {
    private EffectSubTypeWrapper resolveWrapper(EffectSubTypeEnum subType, String rawSub) {
        switch (subType) {
            case EST_PROJ:
                // BUG (problem #10 above): `Projection` isn't imported (only
                // ProjectionEnum is) and isn't even the right type -
                // EffectSubTypeWrapper's matching overload takes a
                // ProjectionEnum, which is what should be used here instead.
                Projection proj = Projection.valueOf("PROJ_" + rawSub);
                return new EffectSubTypeWrapper(proj);

            case EST_TMD:
                TimedEffect tmd = TimedEffect.valueOf("TMD_" + rawSub);
                return new EffectSubTypeWrapper(tmd);

            case EST_STAT:
                Stats st = Stats.valueOf("STAT_" + rawSub);
                return new EffectSubTypeWrapper(st);

            case EST_SUMMON:
                Summon sum = GameConstants.lookupSummon(rawSub);
                return new EffectSubTypeWrapper(sum);

            case EST_TELEPORT:
                // BUG: ignores rawSub entirely, and EffectSubTypeWrapper has no
                // single-arg TeleportEnum constructor - needs (te, false/true).
                // Unreachable today: trap.txt's "effect:TELEPORT" line never
                // has a subtype segment, so this branch never actually runs.
                TeleportEnum te = TeleportEnum.valueOf("TELE_TELEPORT");
                return new EffectSubTypeWrapper(te);

            case EST_EARTHQUAKE:
                Earthquake eq = Earthquake.valueOf("QUAKE_" + rawSub);
                return new EffectSubTypeWrapper(eq);

            default:
                return null;
        }
    }
}

// "name:<name>:<short description>" - cf. parse_trap_name (init.c:1515).
name
        returns[String nameStr, String descStr]
        :   NAME nm=LCASE COLON de=LCASE {
                $nameStr = $nm.getText();
                $descStr = $de.getText();
            }
        ;

// "graphics:<glyph>:<colour>" - cf. parse_trap_graphics (init.c:1528).
graphics
        returns[AngbandDisplayCharacter adc]
        :   GRAPHICS GLYPH_VALUES GLYPH_COLON_SWITCH COLOUR_VALUES {
                char graphicsChr = $GLYPH_VALUES.getText().charAt(0);
                String colour = $COLOUR_VALUES.getText();
                ColourType colourType;
                if (colour.length() > 1)
                    colourType = ColourType.findColourType(colour);
                else
                    colourType = ColourType.findColourType(colour.charAt(0));

                $adc = new AngbandDisplayCharacter(graphicsChr, colourType);
            }
        ;

// "appear:<rarity>:<min depth>:<max number on level>" - cf. parse_trap_appear
// (init.c:1547). See problem #6 re: the third field's name/meaning.
appear
        returns[int rarity, int minDepth, int minNum]
        :   APPEAR rar=INTEGER COLON mind=INTEGER COLON minn=INTEGER {
                $rarity = Integer.parseInt($rar.getText());
                $minDepth = Integer.parseInt($mind.getText());
                $minNum = Integer.parseInt($minn.getText());
            }
        ;

// "visibility:<dice string>" - detectability roll, stored as t->power in the
// C struct - cf. parse_trap_visibility (init.c:1558).
visibility
        returns[Random diceObj]
        :   VISIBILITY DICE_STRING {
                $diceObj = Random.parseStr($DICE_STRING.getText());
            }
        ;

// "desc:<text>" - flavour text shown to the player - cf. parse_trap_desc
// (init.c:1857).
desc
        returns[String descStr]
        :   DESC LCASE { $descStr = $LCASE.getText(); }
        ;

// "flags:<TRF_FLAG> [| <TRF_FLAG> ...]" - cf. parse_trap_flags (init.c:1575).
flags
        returns[Flag<TrapEnum> trapFlags]
        @init {
            $trapFlags = new Flag<>(TrapEnum.class);
        }
        :   FLAGS f1=FLAG_MODE_UCASE {
                String raw1 = "TRF_" + $f1.getText();
                $trapFlags.on(TrapEnum.valueOf(raw1));
            } (FLAG_OR f2=FLAG_MODE_UCASE {
                String raw2 = "TRF_" + $f2.getText();
                $trapFlags.on(TrapEnum.valueOf(raw2));
            })*
        ;

// "msg:<text>" - shown when the trap triggers - cf. parse_trap_msg
// (init.c:1867).
msg
        returns[String message]
        :   MSG LCASE { $message = $LCASE.getText(); }
        ;

// "save:<OF_FLAG> [| <OF_FLAG> ...]" - object flags granting a saving throw -
// cf. parse_trap_save_flags (init.c:1838).
save
        returns[Flag<ObjectFlag> objectFlags]
        @init {
            $objectFlags = new Flag<>(ObjectFlag.class);
        }
        :   SAVE f1=FLAG_MODE_UCASE {
                String raw1 = "OF_" + $f1.getText();
                $objectFlags.on(ObjectFlag.valueOf(raw1));
            } (FLAG_OR f2=FLAG_MODE_UCASE {
                String raw2 = "OF_" + $f2.getText();
                $objectFlags.on(ObjectFlag.valueOf(raw2));
            })*
        ;

// "msg-good:<text>" - shown when the player saves successfully - cf.
// parse_trap_msg_good (init.c:1877).
msgGood
        returns[String message]
        :   MSG_GOOD LCASE { $message = $LCASE.getText(); }
        ;

// "msg-bad:<text>" - shown when the player fails to save - cf.
// parse_trap_msg_bad (init.c:1887).
msgBad
        returns[String message]
        :   MSG_BAD LCASE { $message = $LCASE.getText(); }
        ;

// "effect:<TYPE>[:<SUBTYPE>[:<radius>[:<other>]]]" - the primary effect -
// cf. parse_trap_effect (init.c:1597) and grab_effect_data, registered as
// "effect sym eff ?sym type ?int radius ?int other" in init_parse_trap.
effect
        returns[EffectEnum effectEnum, EffectSubTypeWrapper wrapper, String radius, String extraParm]
        @init {
            EffectSubTypeEnum subType;
        }
        :   EFFECT eff=UCASE {
                String rawEffect = "EF_" + $eff.getText();
                $effectEnum = EffectEnum.valueOf(rawEffect);
                subType = $effectEnum.getSubType();
            } (COLON est=UCASE {
                String rawSub = $est.getText();
                $wrapper = resolveWrapper(subType, rawSub);
            } (COLON rad=INTEGER {
                $radius = $rad.getText();
            } (COLON xtr=INTEGER {
                $extraParm = $xtr.getText();
            })?)?)?
        ;

// "dice:<dice string>" - dice for the most recent effect: line - cf.
// parse_trap_dice (init.c:1636).
dice
        returns[String diceString]
        :   DICE DICE_STRING { $diceString = $DICE_STRING.getText(); }
        ;

// "expr:<letter>:<EFB_BASE>:<operation>" - binds a $-variable used in the
// most recent dice: line to a base value and operation - cf. parse_trap_expr
// (init.c:1669).
expr
        returns[Expression exp]
        @init {
            char codeLetter = '\0';
            EffectBaseType baseType = EffectBaseType.EFB_NONE;
            String operation = "";
        }
        @after {
            $exp = new Expression(codeLetter, baseType, operation);
        }
        :   EXPR EXPR_CHAR EXPR_COLON EXPR_UCASE EXPR_COLON EXPR_OP {
                codeLetter = $EXPR_CHAR.getText().charAt(0);
                String raw = "EFB_" + $EXPR_UCASE.getText().trim();
                baseType = EffectBaseType.valueOf(raw);
                operation = $EXPR_OP.getText();
            }
        ;

// Groups one effect: line with its (supposedly optional) dice:/expr: lines
// into a single Effect. Mirrors how parse_trap_effect/_dice/_expr all
// operate on "the last effect in t->effect's linked list" in the C parser.
effectBlock
        returns[Effect block]
        @init {
            EffectEnum typeInit = EffectEnum.EF_NONE;
            EffectSubTypeWrapper value = null;
            String diceInit = "";
            int yInit = 0;
            int xInit = 0;
            Random radInit = null;
            String otherParmInit = "";
            Expression expInit = null;
        }
        @after {
            $block = new Effect(typeInit, yInit, xInit, diceInit, typeInit.getSubType(),
                                value, radInit, otherParmInit, expInit);
        }
        // PROBLEM: dice and expr are both mandatory here (no '?'), but most
        // trap.txt effects have no expr: line, and some (e.g. "effect:WAKE")
        // have no dice: line either - this sequence rejects most of the file.
        :   effect {
                typeInit = $effect.effectEnum;
                value = $effect.wrapper;
                radInit = Random.parseStr($effect.radius);
                otherParmInit = $effect.extraParm;
            }
            dice? {
                 diceInit = $dice.diceString;
            }
            expr? {
                expInit = $expr.exp;
            }
        ;

// "msg-xtra:<text>" - shown when the (50%-chance) extra effect triggers -
// cf. parse_trap_msg_xtra (init.c:1897).
msgXtra
        returns[String message]
        :   MSG_XTRA LCASE { $message = $LCASE.getText(); }
        ;

// "effect-xtra:<TYPE>[:<SUBTYPE>[:<radius>[:<other>]]]" - the secondary,
// 50%-chance effect - cf. parse_trap_effect_xtra (init.c:1717). Same shape
// as `effect` above; should delegate to resolveWrapper() the same way.
//
// PROBLEM: this whole rule still duplicates the per-subtype switch that
// `effect` now delegates to resolveWrapper() for - it hasn't been brought
// in line with that refactor yet, and calls .getSubType() on
// $eff.getText() (the raw subtype String) rather than the constructed
// EffectEnum. It also has its own compile errors below.
effectXtra
        returns[EffectEnum effectEnum, EffectSubTypeWrapper wrapper, String radius, String extraParm]
        :   EFFECT_XTRA eff=UCASE {
                String rawEffect = "EF_" + $eff.getText();
                $effectEnum = EffectEnum.valueOf(rawEffect);
            } (COLON est=UCASE {
                String rawSub = $est.getText();
                EffectSubTypeEnum subType = $eff.getText().getSubType();
                $wrapper = resolveWrapper(subType, rawSub);
            } (COLON rad=INTEGER {
                // BUG: $radius is declared String above, but Integer.parseInt(...)
                // returns int - incompatible types, won't compile. `effect`'s
                // own radius/extraParm actions (above) get this right by just
                // using $rad.getText()/$xtr.getText() - this should match that.
                $radius = Integer.parseInt($rad.getText());
            } (COLON xtr=INTEGER {
                $extraParm = Integer.parseInt($xtr.getText());
            })?)?)?
        ;

// "dice-xtra:<dice string>" - dice for the most recent effect-xtra: line -
// cf. parse_trap_dice_xtra (init.c:1756).
diceXtra
        returns[String diceString]
        :   DICE_XTRA DICE_STRING { $diceString = $DICE_STRING.getText(); }
        ;

// "expr-xtra:<letter>:<EFB_BASE>:<operation>" - cf. parse_trap_expr_xtra
// (init.c:1789). Same shape as `expr` above.
exprXtra
        returns[Expression exp]
        @init {
            char codeLetter = '\0';
            EffectBaseType baseType = EffectBaseType.EFB_NONE;
            String operation = "";
        }
        @after {
            $exp = new Expression(codeLetter, baseType, operation);
        }
        :   EXPR_XTRA EXPR_CHAR EXPR_COLON EXPR_UCASE EXPR_COLON EXPR_OP {
                codeLetter = $EXPR_CHAR.getText().charAt(0);
                baseType = EffectBaseType.valueOf("EFB_" + $EXPR_UCASE.getText());
                operation = $EXPR_OP.getText();
            }
        ;

effectXtraBlock
        returns[Effect block]
        @init {
            EffectEnum typeInit = EffectEnum.EF_NONE;
            EffectSubTypeWrapper wrapper = null;
            String diceInit = "";
            int yInit = 0;
            int xInit = 0;
            Random radInit = null;
            String otherParmInit = "";
            Expression expInit = null;
        }
        @after {
            $block = new Effect(typeInit, yInit, xInit, diceInit, typeInit.getSubType(),
                                value, radInit, otherParmInit, expInit);
        }
        :   effectXtra {
                typeInit = $effectXtra.effectEnum;
                value = $effectXtra.wrapper;
                radInit = Random.parseStr($effectXtra.radius);
                otherParmInit = $effectXtra.extraParm;
            }
            (diceXtra {
                diceInit = $diceXtra.diceString;
            })?
            (exprXtra {
                expInit = $exprXtra.exp;
            })?
        ;

// One full trap record: a name/graphics header followed by any number of
// the directives above in any order, accumulated into a TrapKind. Cf. how
// init_parse_trap's directive table (init.c:1907-1928) lets every directive
// repeat/interleave freely within one record.
trap
        returns[TrapKind kind]
        @init {
            String nameInit = "";
            String textInit = "";
            String descInit = "";
            String saveMessInit = "";
            String failMessInit = "";
            String xtraMessInit = "";
            AngbandDisplayCharacter adcInit = null;
            int rarityInit = 0;
            int minDepthInit = 0;
            int maxDepthInit = 0;
            Random visibilityInit = null;
            Effect effectInit = null;
            Effect effectXtraInit = null;

            Flag<TrapEnum> flagsInit = new Flag<>(TrapEnum.class);
            Flag<ObjectFlag> saveInit = new Flag<>(ObjectFlag.class);
        }
        @after {
            $kind = new TrapKind(nameInit, textInit, descInit, saveMessInit,
                                 failMessInit, xtraMessInit, 0, adcInit, rarityInit,
                                 minDepthInit, maxDepthInit, visibilityInit,
                                 flagsInit, saveInit, effectInit, effectXtraInit);
        }
        :   name {
                nameInit = $name.nameStr;
                descInit = $name.descStr;
            }
            graphics { adcInit = $graphics.adc; }
        (   appear {
                rarityInit = $appear.rarity;
                minDepthInit = $appear.minDepth;
                // PROBLEM: $appear.minNum is "maximum number of this trap per
                // level" (trap.txt header; t->max_num in init.c's
                // parse_trap_appear), not a depth - TrapKind has no
                // maxNum/maxOnLevel field to put it in, so it's mislabelled
                // into maxDepthInit instead.
                maxDepthInit = $appear.minNum;
            }
        |   visibility {
                visibilityInit = $visibility.diceObj;
            }
        |   desc { descInit = $desc.descStr; }
        |   flags {
                flagsInit.union($flags.trapFlags);
            }
        |   msg {
                textInit = $msg.message;
            }
        |   save {
                saveInit.union($save.objectFlags);
            }
        |   msgGood { saveMessInit = $msgGood.message; }
        |   msgBad { failMessInit = $msgBad.message; }
        |   effectBlock { effectInit = $effectBlock.block; }
        |   msgXtra { xtraMessInit = $msgXtra.message; }
        // PROBLEM (problem #5 above): the enclosing (appear | ... |
    // effectXtraBlock) group has no '*'/'+' at all, so it matches only
        // ONCE per trap record - contradicting this rule's own top comment
        // ("any number of the directives above in any order"); nearly every
        // real trap.txt record has 4+ of these directives. Separately, the
        // action below reads $effectXtra.block, but the rule actually
        // invoked on this alternative is effectXtraBlock, not effectXtra -
        // should be $effectXtraBlock.block. (effectInit/effectXtraInit still
        // being single fields rather than lists, so repeated effect/
        // effect-xtra blocks in one record clobber each other, is the
        // remaining part of problem #5 - see "POTENTIAL SOLUTIONS" below.)
        |   effectXtraBlock { effectXtraInit = $effectXtra.block; })
        ;

// Top-level rule: the whole trap.txt file is one or more trap records.
file
        returns[List<TrapKind> traps]
        @init {
            $traps = new ArrayList<>();
        }
        :   (trap { $traps.add($trap.kind); })+ EOF
        ;

// POTENTIAL SOLUTIONS
//
//   (0 needs no solution - TrapKind.java already has the constructor it
//   needs; see #6 below for its one genuinely missing field.)
//
//   1/2/3. Mechanical fixes: add the missing ')' that closes each
//      `.on(...)` call in flags/save, change `colour.length` to
//      `colour.length()`, fix effectXtra's radius/extraParm to use
//      `$rad.getText()`/`$xtr.getText()` instead of `Integer.parseInt(...)`
//      (matching `effect`'s own actions) and call the existing
//      resolveWrapper(subType, rawSub) helper instead of duplicating the
//      switch, and fix resolveWrapper's EST_TELEPORT branch to
//      `new EffectSubTypeWrapper(TeleportEnum.valueOf("TELE_" + rawSub), false)`.
//
//   4/5. Restructure effectBlock/effectXtra around a fixed, mostly-optional
//      sequence and a matching effectBlockXtra, then have `trap` accumulate
//      List<Effect> for both primary and xtra effects instead of single
//      fields - i.e. `effect dice? expr?` and `effectXtra diceXtra? exprXtra?`,
//      each appended to its own list on every match, mirroring how
//      parse_trap_effect/_effect_xtra append a new linked-list node per
//      line in init.c. A worked sketch of exactly this (including a shared
//      subtype-resolution helper) is in scratch/TrapGrammar.effectBlock.scratch.g4
//      at the repo root. Separately (and more urgently, since it blocks
//      parsing even a single multi-directive record): add `*` to the
//      `(appear | ... | effectXtraBlock)` group in `trap`, and fix its last
//      alternative's action to read `$effectXtraBlock.block` instead of
//      `$effectXtra.block`.
//
//   6. Add the missing field (e.g. `private int maxNum;`) to TrapKind and
//      rename `maxDepthInit`/`$appear.minNum` accordingly.
//
//   7. Only worth doing if/when trap.txt gains an effect-yx/effect-yx-xtra
//      line - add EFFECT_YX/EFFECT_YX_XTRA tokens to TrapLexer.g4 and
//      `effectYX`/`effectYXXtra` rules (int y, int x) feeding into the
//      relevant effectBlock(Xtra), mirroring parse_trap_effect_yx(_xtra).
//
//   8. Once the above compiles, add a TrapReader.java alongside the other
//      backend/parser/*Reader.java classes to actually wire this into the
//      data-loading pipeline.
//
//   9. Rename `xtraMessave` to `xtraMessInit` in the `msgXtra` alternative.
//
//  10. Use `ProjectionEnum.valueOf("PROJ_" + rawSub)` in resolveWrapper's
//      EST_PROJ case instead of the unimported, wrong-type `Projection`.
//
//  11. Either rename TrapEnum's `TRF_SAVE_ARMOUR` to `TRF_SAVE_ARMOR`
//      (matching trap.txt and the American-spelled upstream flag name), or
//      normalize trap.txt's four `SAVE_ARMOR` occurrences to `SAVE_ARMOUR` -
//      whichever this codebase's convention favours elsewhere.
