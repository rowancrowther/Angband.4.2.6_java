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
 *    Java code copyright (c) Rowan Crowther 2026
 */

// Generated from C:/Users/rowan/Documents/IntelliJProjects/Angband.4.2.6/src/main/java/uk/co/jackoftrades/backend/io/parsers/antlr/grammars/PlayerShapeAntlr.g4 by ANTLR 4.13.2
package uk.co.jackoftrades.backend.io.parsers.antlr.playershape;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Effect;
import uk.co.jackoftrades.middle.Expression;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.enums.EffectBaseType;
import uk.co.jackoftrades.middle.enums.EffectEnum;
import uk.co.jackoftrades.middle.enums.ValueEnum;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerBlow;
import uk.co.jackoftrades.middle.player.PlayerShape;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class PlayerShapeAntlrParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int
            COMMENT = 1, EOL = 2, NAME = 3, COMBAT = 4, SKILL_DISARM_PHYS = 5, SKILL_DISARM_MAGIC = 6,
            SKILL_SAVE = 7, SKILL_STEALTH = 8, SKILL_SEARCH = 9, SKILL_MELEE = 10, SKILL_THROW = 11,
            SKILL_DIG = 12, OBJ_FLAGS = 13, PLAYER_FLAGS = 14, VALUES = 15, EFFECT = 16, DICE = 17,
            EXPR = 18, EFFECT_MSG = 19, BLOW = 20, TEXT = 21, COLON = 22, OR = 23, LBRACKET = 24,
            RBRACKET = 25;
    public static final int
            RULE_name = 0, RULE_combat = 1, RULE_skillDisarmPhys = 2, RULE_skillDisarmMagic = 3,
            RULE_skillSave = 4, RULE_skillStealth = 5, RULE_skillSearch = 6, RULE_skillMelee = 7,
            RULE_skillThrow = 8, RULE_skillDig = 9, RULE_objFlags = 10, RULE_playerFlags = 11,
            RULE_values = 12, RULE_effect = 13, RULE_dice = 14, RULE_expr = 15, RULE_effectMsg = 16,
            RULE_blow = 17, RULE_shape = 18, RULE_shapes = 19;

    private static String[] makeRuleNames() {
        return new String[]{
                "name", "combat", "skillDisarmPhys", "skillDisarmMagic", "skillSave",
                "skillStealth", "skillSearch", "skillMelee", "skillThrow", "skillDig",
                "objFlags", "playerFlags", "values", "effect", "dice", "expr", "effectMsg",
                "blow", "shape", "shapes"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[]{
                null, null, null, "'name:'", "'combat:'", "'skill-disarm-phys:'", "'skill-disarm-magic:'",
                "'skill-save:'", "'skill-stealth:'", "'skill-search:'", "'skill-melee:'",
                "'skill-throw:'", "'skill-dig:'", "'obj-flags:'", "'player-flags:'",
                "'values:'", "'effect:'", "'dice:'", "'expr:'", "'effect-msg:'", "'blow:'",
                null, "':'", null, "'['", "']'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[]{
                null, "COMMENT", "EOL", "NAME", "COMBAT", "SKILL_DISARM_PHYS", "SKILL_DISARM_MAGIC",
                "SKILL_SAVE", "SKILL_STEALTH", "SKILL_SEARCH", "SKILL_MELEE", "SKILL_THROW",
                "SKILL_DIG", "OBJ_FLAGS", "PLAYER_FLAGS", "VALUES", "EFFECT", "DICE",
                "EXPR", "EFFECT_MSG", "BLOW", "TEXT", "COLON", "OR", "LBRACKET", "RBRACKET"
        };
    }

    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated
    public static final String[] tokenNames;

    static {
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }

            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    @Override
    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    @Override
    public String getGrammarFileName() {
        return "PlayerShapeAntlr.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public PlayerShapeAntlrParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class NameContext extends ParserRuleContext {
        public String shapeName;
        public Token TEXT;

        public TerminalNode NAME() {
            return getToken(PlayerShapeAntlrParser.NAME, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public NameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_name;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final NameContext name() throws RecognitionException {
        NameContext _localctx = new NameContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_name);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                match(NAME);
                setState(41);
                ((NameContext) _localctx).TEXT = match(TEXT);

                ((NameContext) _localctx).shapeName = ((NameContext) _localctx).TEXT.getText();

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class CombatContext extends ParserRuleContext {
        public int shapeToHit;
        public int shapeToDam;
        public int shapeToAC;
        public Token toHit;
        public Token toDam;
        public Token toAC;

        public TerminalNode COMBAT() {
            return getToken(PlayerShapeAntlrParser.COMBAT, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerShapeAntlrParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerShapeAntlrParser.COLON, i);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(PlayerShapeAntlrParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(PlayerShapeAntlrParser.TEXT, i);
        }

        public CombatContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_combat;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterCombat(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitCombat(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitCombat(this);
            else return visitor.visitChildren(this);
        }
    }

    public final CombatContext combat() throws RecognitionException {
        CombatContext _localctx = new CombatContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_combat);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(44);
                match(COMBAT);
                setState(45);
                ((CombatContext) _localctx).toHit = match(TEXT);
                setState(46);
                match(COLON);
                setState(47);
                ((CombatContext) _localctx).toDam = match(TEXT);
                setState(48);
                match(COLON);
                setState(49);
                ((CombatContext) _localctx).toAC = match(TEXT);

                ((CombatContext) _localctx).shapeToHit = Integer.parseInt(((CombatContext) _localctx).toHit.getText());
                ((CombatContext) _localctx).shapeToDam = Integer.parseInt(((CombatContext) _localctx).toDam.getText());
                ((CombatContext) _localctx).shapeToAC = Integer.parseInt(((CombatContext) _localctx).toAC.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SkillDisarmPhysContext extends ParserRuleContext {
        public int shapeDisarmPhys;
        public Token TEXT;

        public TerminalNode SKILL_DISARM_PHYS() {
            return getToken(PlayerShapeAntlrParser.SKILL_DISARM_PHYS, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public SkillDisarmPhysContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDisarmPhys;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterSkillDisarmPhys(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).exitSkillDisarmPhys(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitSkillDisarmPhys(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDisarmPhysContext skillDisarmPhys() throws RecognitionException {
        SkillDisarmPhysContext _localctx = new SkillDisarmPhysContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_skillDisarmPhys);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(SKILL_DISARM_PHYS);
                setState(53);
                ((SkillDisarmPhysContext) _localctx).TEXT = match(TEXT);

                ((SkillDisarmPhysContext) _localctx).shapeDisarmPhys = Integer.parseInt(((SkillDisarmPhysContext) _localctx).TEXT.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SkillDisarmMagicContext extends ParserRuleContext {
        public int shapeDisarmMagic;
        public Token TEXT;

        public TerminalNode SKILL_DISARM_MAGIC() {
            return getToken(PlayerShapeAntlrParser.SKILL_DISARM_MAGIC, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public SkillDisarmMagicContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDisarmMagic;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterSkillDisarmMagic(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).exitSkillDisarmMagic(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitSkillDisarmMagic(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDisarmMagicContext skillDisarmMagic() throws RecognitionException {
        SkillDisarmMagicContext _localctx = new SkillDisarmMagicContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_skillDisarmMagic);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(56);
                match(SKILL_DISARM_MAGIC);
                setState(57);
                ((SkillDisarmMagicContext) _localctx).TEXT = match(TEXT);

                ((SkillDisarmMagicContext) _localctx).shapeDisarmMagic = Integer.parseInt(((SkillDisarmMagicContext) _localctx).TEXT.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SkillSaveContext extends ParserRuleContext {
        public int shapeSave;
        public Token TEXT;

        public TerminalNode SKILL_SAVE() {
            return getToken(PlayerShapeAntlrParser.SKILL_SAVE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public SkillSaveContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillSave;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterSkillSave(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitSkillSave(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitSkillSave(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillSaveContext skillSave() throws RecognitionException {
        SkillSaveContext _localctx = new SkillSaveContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_skillSave);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(60);
                match(SKILL_SAVE);
                setState(61);
                ((SkillSaveContext) _localctx).TEXT = match(TEXT);

                ((SkillSaveContext) _localctx).shapeSave = Integer.parseInt(((SkillSaveContext) _localctx).TEXT.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SkillStealthContext extends ParserRuleContext {
        public int shapeStealth;
        public Token TEXT;

        public TerminalNode SKILL_STEALTH() {
            return getToken(PlayerShapeAntlrParser.SKILL_STEALTH, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public SkillStealthContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillStealth;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterSkillStealth(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).exitSkillStealth(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitSkillStealth(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillStealthContext skillStealth() throws RecognitionException {
        SkillStealthContext _localctx = new SkillStealthContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_skillStealth);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(64);
                match(SKILL_STEALTH);
                setState(65);
                ((SkillStealthContext) _localctx).TEXT = match(TEXT);

                ((SkillStealthContext) _localctx).shapeStealth = Integer.parseInt(((SkillStealthContext) _localctx).TEXT.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SkillSearchContext extends ParserRuleContext {
        public int shapeSearch;
        public Token TEXT;

        public TerminalNode SKILL_SEARCH() {
            return getToken(PlayerShapeAntlrParser.SKILL_SEARCH, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public SkillSearchContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillSearch;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterSkillSearch(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).exitSkillSearch(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitSkillSearch(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillSearchContext skillSearch() throws RecognitionException {
        SkillSearchContext _localctx = new SkillSearchContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_skillSearch);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(68);
                match(SKILL_SEARCH);
                setState(69);
                ((SkillSearchContext) _localctx).TEXT = match(TEXT);

                ((SkillSearchContext) _localctx).shapeSearch = Integer.parseInt(((SkillSearchContext) _localctx).TEXT.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SkillMeleeContext extends ParserRuleContext {
        public int shapeMelee;
        public Token TEXT;

        public TerminalNode SKILL_MELEE() {
            return getToken(PlayerShapeAntlrParser.SKILL_MELEE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public SkillMeleeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillMelee;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterSkillMelee(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).exitSkillMelee(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitSkillMelee(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillMeleeContext skillMelee() throws RecognitionException {
        SkillMeleeContext _localctx = new SkillMeleeContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_skillMelee);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(72);
                match(SKILL_MELEE);
                setState(73);
                ((SkillMeleeContext) _localctx).TEXT = match(TEXT);

                ((SkillMeleeContext) _localctx).shapeMelee = Integer.parseInt(((SkillMeleeContext) _localctx).TEXT.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SkillThrowContext extends ParserRuleContext {
        public int shapeThrow;
        public Token TEXT;

        public TerminalNode SKILL_THROW() {
            return getToken(PlayerShapeAntlrParser.SKILL_THROW, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public SkillThrowContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillThrow;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterSkillThrow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).exitSkillThrow(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitSkillThrow(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillThrowContext skillThrow() throws RecognitionException {
        SkillThrowContext _localctx = new SkillThrowContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_skillThrow);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(76);
                match(SKILL_THROW);
                setState(77);
                ((SkillThrowContext) _localctx).TEXT = match(TEXT);

                ((SkillThrowContext) _localctx).shapeThrow = Integer.parseInt(((SkillThrowContext) _localctx).TEXT.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class SkillDigContext extends ParserRuleContext {
        public int shapeDig;
        public Token TEXT;

        public TerminalNode SKILL_DIG() {
            return getToken(PlayerShapeAntlrParser.SKILL_DIG, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public SkillDigContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_skillDig;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterSkillDig(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitSkillDig(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitSkillDig(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SkillDigContext skillDig() throws RecognitionException {
        SkillDigContext _localctx = new SkillDigContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_skillDig);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(80);
                match(SKILL_DIG);
                setState(81);
                ((SkillDigContext) _localctx).TEXT = match(TEXT);

                ((SkillDigContext) _localctx).shapeDig = Integer.parseInt(((SkillDigContext) _localctx).TEXT.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ObjFlagsContext extends ParserRuleContext {
        public ArrayList<ObjectFlag> objectFlags;
        public Token flag1;
        public Token flag2;

        public TerminalNode OBJ_FLAGS() {
            return getToken(PlayerShapeAntlrParser.OBJ_FLAGS, 0);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(PlayerShapeAntlrParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(PlayerShapeAntlrParser.TEXT, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(PlayerShapeAntlrParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(PlayerShapeAntlrParser.OR, i);
        }

        public ObjFlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_objFlags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterObjFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitObjFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitObjFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ObjFlagsContext objFlags() throws RecognitionException {
        ObjFlagsContext _localctx = new ObjFlagsContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_objFlags);

        ((ObjFlagsContext) _localctx).objectFlags = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                match(OBJ_FLAGS);
                setState(85);
                ((ObjFlagsContext) _localctx).flag1 = match(TEXT);

                String flag1String = "OF_" + ((ObjFlagsContext) _localctx).flag1.getText().trim();
                _localctx.objectFlags.add(ObjectFlag.valueOf(flag1String));

                setState(92);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(87);
                            match(OR);
                            setState(88);
                            ((ObjFlagsContext) _localctx).flag2 = match(TEXT);

                            String flag2String = "OF_" + ((ObjFlagsContext) _localctx).flag2.getText().trim();
                            _localctx.objectFlags.add(ObjectFlag.valueOf(flag2String));

                        }
                    }
                    setState(94);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class PlayerFlagsContext extends ParserRuleContext {
        public ArrayList<PlayerFlag> playerFlgs;
        public Token flag1;
        public Token flag2;

        public TerminalNode PLAYER_FLAGS() {
            return getToken(PlayerShapeAntlrParser.PLAYER_FLAGS, 0);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(PlayerShapeAntlrParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(PlayerShapeAntlrParser.TEXT, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(PlayerShapeAntlrParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(PlayerShapeAntlrParser.OR, i);
        }

        public PlayerFlagsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_playerFlags;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterPlayerFlags(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).exitPlayerFlags(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitPlayerFlags(this);
            else return visitor.visitChildren(this);
        }
    }

    public final PlayerFlagsContext playerFlags() throws RecognitionException {
        PlayerFlagsContext _localctx = new PlayerFlagsContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_playerFlags);

        ((PlayerFlagsContext) _localctx).playerFlgs = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(95);
                match(PLAYER_FLAGS);
                setState(96);
                ((PlayerFlagsContext) _localctx).flag1 = match(TEXT);

                String flagString1 = "PF_" + ((PlayerFlagsContext) _localctx).flag1.getText().trim();
                _localctx.playerFlgs.add(PlayerFlag.valueOf(flagString1));

                setState(103);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(98);
                            match(OR);
                            setState(99);
                            ((PlayerFlagsContext) _localctx).flag2 = match(TEXT);

                            String flagString2 = "PF_" + ((PlayerFlagsContext) _localctx).flag2.getText().trim();
                            _localctx.playerFlgs.add(PlayerFlag.valueOf(flagString2));

                        }
                    }
                    setState(105);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ValuesContext extends ParserRuleContext {
        public HashMap<ValueEnum, Integer> valueMap;
        public Token enum1;
        public Token val1;
        public Token enum2;
        public Token val2;

        public TerminalNode VALUES() {
            return getToken(PlayerShapeAntlrParser.VALUES, 0);
        }

        public List<TerminalNode> LBRACKET() {
            return getTokens(PlayerShapeAntlrParser.LBRACKET);
        }

        public TerminalNode LBRACKET(int i) {
            return getToken(PlayerShapeAntlrParser.LBRACKET, i);
        }

        public List<TerminalNode> RBRACKET() {
            return getTokens(PlayerShapeAntlrParser.RBRACKET);
        }

        public TerminalNode RBRACKET(int i) {
            return getToken(PlayerShapeAntlrParser.RBRACKET, i);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(PlayerShapeAntlrParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(PlayerShapeAntlrParser.TEXT, i);
        }

        public List<TerminalNode> OR() {
            return getTokens(PlayerShapeAntlrParser.OR);
        }

        public TerminalNode OR(int i) {
            return getToken(PlayerShapeAntlrParser.OR, i);
        }

        public ValuesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_values;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterValues(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitValues(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitValues(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ValuesContext values() throws RecognitionException {
        ValuesContext _localctx = new ValuesContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_values);

        ((ValuesContext) _localctx).valueMap = new HashMap<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(106);
                match(VALUES);
                setState(107);
                ((ValuesContext) _localctx).enum1 = match(TEXT);
                setState(108);
                match(LBRACKET);
                setState(109);
                ((ValuesContext) _localctx).val1 = match(TEXT);
                setState(110);
                match(RBRACKET);

                String valueEnum1Str = "CV_" + ((ValuesContext) _localctx).enum1.getText().trim();
                ValueEnum valueEnum1 = ValueEnum.valueOf(valueEnum1Str);
                int valueVal1 = Integer.parseInt(((ValuesContext) _localctx).val1.getText().trim());
                _localctx.valueMap.put(valueEnum1, valueVal1);

                setState(120);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OR) {
                    {
                        {
                            setState(112);
                            match(OR);
                            setState(113);
                            ((ValuesContext) _localctx).enum2 = match(TEXT);
                            setState(114);
                            match(LBRACKET);
                            setState(115);
                            ((ValuesContext) _localctx).val2 = match(TEXT);
                            setState(116);
                            match(RBRACKET);

                            String valueEnum2Str = "CV_" + ((ValuesContext) _localctx).enum2.getText().trim();
                            ValueEnum valueEnum2 = ValueEnum.valueOf(valueEnum2Str);
                            int valueVal2 = Integer.parseInt(((ValuesContext) _localctx).val2.getText().trim());
                            _localctx.valueMap.put(valueEnum2, valueVal2);

                        }
                    }
                    setState(122);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EffectContext extends ParserRuleContext {
        public EffectEnum effectEnum;
        public TimedEffect timedEffect;
        public ObjectFlag objectFlag;
        public ProjectionEnum projectionEnum;
        public String errorValue;
        public Token proj1;
        public Token proj2;

        public TerminalNode EFFECT() {
            return getToken(PlayerShapeAntlrParser.EFFECT, 0);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(PlayerShapeAntlrParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(PlayerShapeAntlrParser.TEXT, i);
        }

        public TerminalNode COLON() {
            return getToken(PlayerShapeAntlrParser.COLON, 0);
        }

        public EffectContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effect;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterEffect(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitEffect(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitEffect(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectContext effect() throws RecognitionException {
        EffectContext _localctx = new EffectContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_effect);

        String type = "";
        ((EffectContext) _localctx).errorValue = "";
        ((EffectContext) _localctx).effectEnum = EffectEnum.EF_NONE;
        ((EffectContext) _localctx).timedEffect = TimedEffect.TMD_NULL;
        ((EffectContext) _localctx).objectFlag = ObjectFlag.OF_NONE;
        ((EffectContext) _localctx).projectionEnum = ProjectionEnum.PROJ_NONE;

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(123);
                match(EFFECT);
                setState(124);
                ((EffectContext) _localctx).proj1 = match(TEXT);

                type = ((EffectContext) _localctx).proj1.getText();
                ((EffectContext) _localctx).effectEnum = EffectEnum.valueOf("EF_" + type);

                setState(129);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COLON) {
                    {
                        setState(126);
                        match(COLON);
                        setState(127);
                        ((EffectContext) _localctx).proj2 = match(TEXT);

                        String secondFlag = ((EffectContext) _localctx).proj2.getText();
                        if (type.equals("CURE")) {
                            ((EffectContext) _localctx).timedEffect = TimedEffect.valueOf("TMD_" + secondFlag);
                        } else if (type.startsWith("TIMED")) {
                            ((EffectContext) _localctx).timedEffect = TimedEffect.valueOf("TMD_" + secondFlag);
                        } else if (type.startsWith("PROJ")) {
                            ((EffectContext) _localctx).projectionEnum = ProjectionEnum.valueOf("PROJ_" + secondFlag);
                        } else {
                            ((EffectContext) _localctx).errorValue = type + ":" + secondFlag;
                        }

                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class DiceContext extends ParserRuleContext {
        public String diceText;
        public Token TEXT;

        public TerminalNode DICE() {
            return getToken(PlayerShapeAntlrParser.DICE, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public DiceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_dice;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterDice(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitDice(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitDice(this);
            else return visitor.visitChildren(this);
        }
    }

    public final DiceContext dice() throws RecognitionException {
        DiceContext _localctx = new DiceContext(_ctx, getState());
        enterRule(_localctx, 28, RULE_dice);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(131);
                match(DICE);
                setState(132);
                ((DiceContext) _localctx).TEXT = match(TEXT);

                ((DiceContext) _localctx).diceText = ((DiceContext) _localctx).TEXT.getText();

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExprContext extends ParserRuleContext {
        public Expression expression;
        public Token char_;
        public Token flag;
        public Token val;

        public TerminalNode EXPR() {
            return getToken(PlayerShapeAntlrParser.EXPR, 0);
        }

        public List<TerminalNode> COLON() {
            return getTokens(PlayerShapeAntlrParser.COLON);
        }

        public TerminalNode COLON(int i) {
            return getToken(PlayerShapeAntlrParser.COLON, i);
        }

        public List<TerminalNode> TEXT() {
            return getTokens(PlayerShapeAntlrParser.TEXT);
        }

        public TerminalNode TEXT(int i) {
            return getToken(PlayerShapeAntlrParser.TEXT, i);
        }

        public ExprContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expr;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterExpr(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitExpr(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitExpr(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ExprContext expr() throws RecognitionException {
        ExprContext _localctx = new ExprContext(_ctx, getState());
        enterRule(_localctx, 30, RULE_expr);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(135);
                match(EXPR);
                setState(136);
                ((ExprContext) _localctx).char_ = match(TEXT);
                setState(137);
                match(COLON);
                setState(138);
                ((ExprContext) _localctx).flag = match(TEXT);
                setState(139);
                match(COLON);
                setState(140);
                ((ExprContext) _localctx).val = match(TEXT);

                String expFlagString = "EFB_" + ((ExprContext) _localctx).flag.getText();
                ((ExprContext) _localctx).expression = new Expression(((ExprContext) _localctx).char_.getText().charAt(0),
                        EffectBaseType.valueOf(expFlagString),
                        ((ExprContext) _localctx).val.getText());

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class EffectMsgContext extends ParserRuleContext {
        public String message;
        public Token TEXT;

        public TerminalNode EFFECT_MSG() {
            return getToken(PlayerShapeAntlrParser.EFFECT_MSG, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public EffectMsgContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_effectMsg;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener)
                ((PlayerShapeAntlrListener) listener).enterEffectMsg(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitEffectMsg(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitEffectMsg(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EffectMsgContext effectMsg() throws RecognitionException {
        EffectMsgContext _localctx = new EffectMsgContext(_ctx, getState());
        enterRule(_localctx, 32, RULE_effectMsg);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(143);
                match(EFFECT_MSG);
                setState(144);
                ((EffectMsgContext) _localctx).TEXT = match(TEXT);

                ((EffectMsgContext) _localctx).message = ((EffectMsgContext) _localctx).TEXT.getText();

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BlowContext extends ParserRuleContext {
        public String blowType;
        public Token TEXT;

        public TerminalNode BLOW() {
            return getToken(PlayerShapeAntlrParser.BLOW, 0);
        }

        public TerminalNode TEXT() {
            return getToken(PlayerShapeAntlrParser.TEXT, 0);
        }

        public BlowContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_blow;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterBlow(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitBlow(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitBlow(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BlowContext blow() throws RecognitionException {
        BlowContext _localctx = new BlowContext(_ctx, getState());
        enterRule(_localctx, 34, RULE_blow);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(147);
                match(BLOW);
                setState(148);
                ((BlowContext) _localctx).TEXT = match(TEXT);

                ((BlowContext) _localctx).blowType = ((BlowContext) _localctx).TEXT.getText();

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ShapeContext extends ParserRuleContext {
        public PlayerShape playerShape;
        public NameContext name;
        public CombatContext combat;
        public SkillDisarmPhysContext skillDisarmPhys;
        public SkillDisarmMagicContext skillDisarmMagic;
        public SkillSaveContext skillSave;
        public SkillStealthContext skillStealth;
        public SkillSearchContext skillSearch;
        public SkillMeleeContext skillMelee;
        public SkillThrowContext skillThrow;
        public SkillDigContext skillDig;
        public ObjFlagsContext objFlags;
        public PlayerFlagsContext playerFlags;
        public ValuesContext values;
        public EffectMsgContext effectMsg;
        public EffectContext effect;
        public DiceContext dice;
        public ExprContext expr;
        public BlowContext blow;

        public NameContext name() {
            return getRuleContext(NameContext.class, 0);
        }

        public CombatContext combat() {
            return getRuleContext(CombatContext.class, 0);
        }

        public SkillDisarmPhysContext skillDisarmPhys() {
            return getRuleContext(SkillDisarmPhysContext.class, 0);
        }

        public SkillDisarmMagicContext skillDisarmMagic() {
            return getRuleContext(SkillDisarmMagicContext.class, 0);
        }

        public SkillSaveContext skillSave() {
            return getRuleContext(SkillSaveContext.class, 0);
        }

        public SkillStealthContext skillStealth() {
            return getRuleContext(SkillStealthContext.class, 0);
        }

        public SkillSearchContext skillSearch() {
            return getRuleContext(SkillSearchContext.class, 0);
        }

        public SkillMeleeContext skillMelee() {
            return getRuleContext(SkillMeleeContext.class, 0);
        }

        public SkillThrowContext skillThrow() {
            return getRuleContext(SkillThrowContext.class, 0);
        }

        public SkillDigContext skillDig() {
            return getRuleContext(SkillDigContext.class, 0);
        }

        public List<ObjFlagsContext> objFlags() {
            return getRuleContexts(ObjFlagsContext.class);
        }

        public ObjFlagsContext objFlags(int i) {
            return getRuleContext(ObjFlagsContext.class, i);
        }

        public List<PlayerFlagsContext> playerFlags() {
            return getRuleContexts(PlayerFlagsContext.class);
        }

        public PlayerFlagsContext playerFlags(int i) {
            return getRuleContext(PlayerFlagsContext.class, i);
        }

        public List<ValuesContext> values() {
            return getRuleContexts(ValuesContext.class);
        }

        public ValuesContext values(int i) {
            return getRuleContext(ValuesContext.class, i);
        }

        public List<EffectMsgContext> effectMsg() {
            return getRuleContexts(EffectMsgContext.class);
        }

        public EffectMsgContext effectMsg(int i) {
            return getRuleContext(EffectMsgContext.class, i);
        }

        public List<EffectContext> effect() {
            return getRuleContexts(EffectContext.class);
        }

        public EffectContext effect(int i) {
            return getRuleContext(EffectContext.class, i);
        }

        public List<DiceContext> dice() {
            return getRuleContexts(DiceContext.class);
        }

        public DiceContext dice(int i) {
            return getRuleContext(DiceContext.class, i);
        }

        public ExprContext expr() {
            return getRuleContext(ExprContext.class, 0);
        }

        public List<BlowContext> blow() {
            return getRuleContexts(BlowContext.class);
        }

        public BlowContext blow(int i) {
            return getRuleContext(BlowContext.class, i);
        }

        public ShapeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_shape;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterShape(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitShape(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitShape(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ShapeContext shape() throws RecognitionException {
        ShapeContext _localctx = new ShapeContext(_ctx, getState());
        enterRule(_localctx, 36, RULE_shape);

        String nameInit = "";
        int toAcInit = 0;
        int toHitInit = 0;
        int toDamInit = 0;
        HashMap<PlayerSkill, Integer> skillsInit = new HashMap<>();

        for (PlayerSkill playerSkill : PlayerSkill.values())
            skillsInit.put(playerSkill, 0);

        Flag<ObjectFlag> flagsInit = new Flag<ObjectFlag>(ObjectFlag.class);
        Flag<PlayerFlag> pFlagsInit = new Flag<PlayerFlag>(PlayerFlag.class);

        HashMap<ValueEnum, Integer> valueInit = new HashMap<>();

        Expression exprInit = null;

        ArrayList<PlayerBlow> blowInit = new ArrayList<>();
        String diceStringInit = "";
        String expressionDiceStringInit = "";

        String effectMessageInit = "";

        EffectEnum effectEnumInit = EffectEnum.EF_NONE;
        TimedEffect timedEffectInit = TimedEffect.TMD_NULL;
        ObjectFlag objectFlagInit = ObjectFlag.OF_NONE;
        ProjectionEnum projEnumInit = ProjectionEnum.PROJ_NONE;
        String errorValInit = "";

        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(151);
                ((ShapeContext) _localctx).name = name();
                nameInit = ((ShapeContext) _localctx).name.shapeName;
                setState(156);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == COMBAT) {
                    {
                        setState(153);
                        ((ShapeContext) _localctx).combat = combat();

                        toAcInit = ((ShapeContext) _localctx).combat.shapeToAC;
                        toHitInit = ((ShapeContext) _localctx).combat.shapeToHit;
                        toDamInit = ((ShapeContext) _localctx).combat.shapeToDam;

                    }
                }

                setState(161);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SKILL_DISARM_PHYS) {
                    {
                        setState(158);
                        ((ShapeContext) _localctx).skillDisarmPhys = skillDisarmPhys();

                        skillsInit.put(PlayerSkill.SKILL_DISARM_PHYS, ((ShapeContext) _localctx).skillDisarmPhys.shapeDisarmPhys);

                    }
                }

                setState(166);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SKILL_DISARM_MAGIC) {
                    {
                        setState(163);
                        ((ShapeContext) _localctx).skillDisarmMagic = skillDisarmMagic();

                        skillsInit.put(PlayerSkill.SKILL_DISARM_MAGIC, ((ShapeContext) _localctx).skillDisarmMagic.shapeDisarmMagic);

                    }
                }

                setState(171);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SKILL_SAVE) {
                    {
                        setState(168);
                        ((ShapeContext) _localctx).skillSave = skillSave();

                        skillsInit.put(PlayerSkill.SKILL_SAVE, ((ShapeContext) _localctx).skillSave.shapeSave);

                    }
                }

                setState(176);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SKILL_STEALTH) {
                    {
                        setState(173);
                        ((ShapeContext) _localctx).skillStealth = skillStealth();

                        skillsInit.put(PlayerSkill.SKILL_STEALTH, ((ShapeContext) _localctx).skillStealth.shapeStealth);

                    }
                }

                setState(181);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SKILL_SEARCH) {
                    {
                        setState(178);
                        ((ShapeContext) _localctx).skillSearch = skillSearch();

                        skillsInit.put(PlayerSkill.SKILL_SEARCH, ((ShapeContext) _localctx).skillSearch.shapeSearch);

                    }
                }

                setState(186);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SKILL_MELEE) {
                    {
                        setState(183);
                        ((ShapeContext) _localctx).skillMelee = skillMelee();

                        skillsInit.put(PlayerSkill.SKILL_TO_HIT_MELEE, ((ShapeContext) _localctx).skillMelee.shapeMelee);

                    }
                }

                setState(191);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SKILL_THROW) {
                    {
                        setState(188);
                        ((ShapeContext) _localctx).skillThrow = skillThrow();

                        skillsInit.put(PlayerSkill.SKILL_TO_HIT_THROW, ((ShapeContext) _localctx).skillThrow.shapeThrow);

                    }
                }

                setState(196);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == SKILL_DIG) {
                    {
                        setState(193);
                        ((ShapeContext) _localctx).skillDig = skillDig();

                        skillsInit.put(PlayerSkill.SKILL_DIGGING, ((ShapeContext) _localctx).skillDig.shapeDig);

                    }
                }

                setState(203);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == OBJ_FLAGS) {
                    {
                        {
                            setState(198);
                            ((ShapeContext) _localctx).objFlags = objFlags();

                            for (ObjectFlag oFlag : ((ShapeContext) _localctx).objFlags.objectFlags)
                                flagsInit.on(oFlag);

                        }
                    }
                    setState(205);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(211);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == PLAYER_FLAGS) {
                    {
                        {
                            setState(206);
                            ((ShapeContext) _localctx).playerFlags = playerFlags();

                            for (PlayerFlag pFlag : ((ShapeContext) _localctx).playerFlags.playerFlgs)
                                pFlagsInit.on(pFlag);

                        }
                    }
                    setState(213);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(219);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == VALUES) {
                    {
                        {
                            setState(214);
                            ((ShapeContext) _localctx).values = values();

                            valueInit = ((ShapeContext) _localctx).values.valueMap;

                        }
                    }
                    setState(221);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(227);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 16, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(222);
                                ((ShapeContext) _localctx).effectMsg = effectMsg();

                                effectMessageInit = ((ShapeContext) _localctx).effectMsg.message;

                            }
                        }
                    }
                    setState(229);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 16, _ctx);
                }
                setState(235);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 17, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(230);
                                ((ShapeContext) _localctx).effect = effect();

                                effectEnumInit = ((ShapeContext) _localctx).effect.effectEnum;
                                timedEffectInit = ((ShapeContext) _localctx).effect.timedEffect;
                                objectFlagInit = ((ShapeContext) _localctx).effect.objectFlag;
                                projEnumInit = ((ShapeContext) _localctx).effect.projectionEnum;
                                errorValInit = ((ShapeContext) _localctx).effect.errorValue;

                            }
                        }
                    }
                    setState(237);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 17, _ctx);
                }
                setState(241);
                _errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(_input, 18, _ctx)) {
                    case 1: {
                        setState(238);
                        ((ShapeContext) _localctx).dice = dice();

                        String diceString = ((ShapeContext) _localctx).dice.diceText;
                        if (diceString.contains("$")) {
                            expressionDiceStringInit = diceString;
                        } else {
                            diceStringInit = diceString;
                        }

                    }
                    break;
                }
                setState(246);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EXPR) {
                    {
                        setState(243);
                        ((ShapeContext) _localctx).expr = expr();

                        exprInit = ((ShapeContext) _localctx).expr.expression;

                    }
                }

                setState(253);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 20, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(248);
                                ((ShapeContext) _localctx).effect = effect();

                                effectEnumInit = ((ShapeContext) _localctx).effect.effectEnum;
                                timedEffectInit = ((ShapeContext) _localctx).effect.timedEffect;
                                objectFlagInit = ((ShapeContext) _localctx).effect.objectFlag;
                                projEnumInit = ((ShapeContext) _localctx).effect.projectionEnum;
                                errorValInit = ((ShapeContext) _localctx).effect.errorValue;

                            }
                        }
                    }
                    setState(255);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 20, _ctx);
                }
                setState(259);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == EFFECT_MSG) {
                    {
                        setState(256);
                        ((ShapeContext) _localctx).effectMsg = effectMsg();

                        effectMessageInit = ((ShapeContext) _localctx).effectMsg.message;

                    }
                }

                setState(266);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == EFFECT) {
                    {
                        {
                            setState(261);
                            ((ShapeContext) _localctx).effect = effect();

                            effectEnumInit = ((ShapeContext) _localctx).effect.effectEnum;
                            timedEffectInit = ((ShapeContext) _localctx).effect.timedEffect;
                            objectFlagInit = ((ShapeContext) _localctx).effect.objectFlag;
                            projEnumInit = ((ShapeContext) _localctx).effect.projectionEnum;
                            errorValInit = ((ShapeContext) _localctx).effect.errorValue;

                        }
                    }
                    setState(268);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(272);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == DICE) {
                    {
                        setState(269);
                        ((ShapeContext) _localctx).dice = dice();

                        String diceString = ((ShapeContext) _localctx).dice.diceText;
                        if (diceString.contains("$")) {
                            expressionDiceStringInit = diceString;
                        } else {
                            diceStringInit = diceString;
                        }

                    }
                }

                setState(279);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == BLOW) {
                    {
                        {
                            setState(274);
                            ((ShapeContext) _localctx).blow = blow();

                            blowInit.add(new PlayerBlow(((ShapeContext) _localctx).blow.blowType));

                        }
                    }
                    setState(281);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
            _ctx.stop = _input.LT(-1);

            Object otherParameter;
            if (timedEffectInit != TimedEffect.TMD_NULL)
                otherParameter = timedEffectInit;
            else if (objectFlagInit != ObjectFlag.OF_NONE)
                otherParameter = objectFlagInit;
            else
                otherParameter = projEnumInit;

            ((ShapeContext) _localctx).playerShape = new PlayerShape(nameInit,
                    toAcInit,
                    toHitInit,
                    toDamInit,
                    skillsInit,
                    flagsInit,
                    pFlagsInit,
                    null,
                    null,
                    new ArrayList<ElementEnum>(),
                    new Effect(effectEnumInit,
                            expressionDiceStringInit,
                            0, 0,
                            null, 0,
                            otherParameter,
                            effectMessageInit
                    ),
                    blowInit.size(),
                    blowInit);

        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ShapesContext extends ParserRuleContext {
        public ArrayList<PlayerShape> shapeArray;
        public ShapeContext shape;

        public TerminalNode EOF() {
            return getToken(PlayerShapeAntlrParser.EOF, 0);
        }

        public List<ShapeContext> shape() {
            return getRuleContexts(ShapeContext.class);
        }

        public ShapeContext shape(int i) {
            return getRuleContext(ShapeContext.class, i);
        }

        public ShapesContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_shapes;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).enterShapes(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof PlayerShapeAntlrListener) ((PlayerShapeAntlrListener) listener).exitShapes(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof PlayerShapeAntlrVisitor)
                return ((PlayerShapeAntlrVisitor<? extends T>) visitor).visitShapes(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ShapesContext shapes() throws RecognitionException {
        ShapesContext _localctx = new ShapesContext(_ctx, getState());
        enterRule(_localctx, 38, RULE_shapes);

        ((ShapesContext) _localctx).shapeArray = new ArrayList<>();

        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(285);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(282);
                            ((ShapesContext) _localctx).shape = shape();

                            _localctx.shapeArray.add(((ShapesContext) _localctx).shape.playerShape);

                        }
                    }
                    setState(287);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == NAME);
                setState(289);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static final String _serializedATN =
            "\u0004\u0001\u0019\u0124\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001" +
                    "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004" +
                    "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007" +
                    "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b" +
                    "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007" +
                    "\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007" +
                    "\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001" +
                    "\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001" +
                    "\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001" +
                    "\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001" +
                    "\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001" +
                    "\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001" +
                    "\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\t" +
                    "\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001" +
                    "\n\u0005\n[\b\n\n\n\f\n^\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001" +
                    "\u000b\u0001\u000b\u0001\u000b\u0005\u000bf\b\u000b\n\u000b\f\u000bi\t" +
                    "\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f" +
                    "\u0001\f\u0001\f\u0001\f\u0001\f\u0005\fw\b\f\n\f\f\fz\t\f\u0001\r\u0001" +
                    "\r\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0082\b\r\u0001\u000e\u0001" +
                    "\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001" +
                    "\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001" +
                    "\u0010\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001" +
                    "\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003" +
                    "\u0012\u009d\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00a2" +
                    "\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00a7\b\u0012" +
                    "\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00ac\b\u0012\u0001\u0012" +
                    "\u0001\u0012\u0001\u0012\u0003\u0012\u00b1\b\u0012\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0003\u0012\u00b6\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0003\u0012\u00bb\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012" +
                    "\u00c0\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012\u00c5\b" +
                    "\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00ca\b\u0012\n" +
                    "\u0012\f\u0012\u00cd\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005" +
                    "\u0012\u00d2\b\u0012\n\u0012\f\u0012\u00d5\t\u0012\u0001\u0012\u0001\u0012" +
                    "\u0001\u0012\u0005\u0012\u00da\b\u0012\n\u0012\f\u0012\u00dd\t\u0012\u0001" +
                    "\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00e2\b\u0012\n\u0012\f\u0012" +
                    "\u00e5\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00ea\b" +
                    "\u0012\n\u0012\f\u0012\u00ed\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0003\u0012\u00f2\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012" +
                    "\u00f7\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012\u00fc\b" +
                    "\u0012\n\u0012\f\u0012\u00ff\t\u0012\u0001\u0012\u0001\u0012\u0001\u0012" +
                    "\u0003\u0012\u0104\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005\u0012" +
                    "\u0109\b\u0012\n\u0012\f\u0012\u010c\t\u0012\u0001\u0012\u0001\u0012\u0001" +
                    "\u0012\u0003\u0012\u0111\b\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0005" +
                    "\u0012\u0116\b\u0012\n\u0012\f\u0012\u0119\t\u0012\u0001\u0013\u0001\u0013" +
                    "\u0001\u0013\u0004\u0013\u011e\b\u0013\u000b\u0013\f\u0013\u011f\u0001" +
                    "\u0013\u0001\u0013\u0001\u0013\u0000\u0000\u0014\u0000\u0002\u0004\u0006" +
                    "\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&\u0000" +
                    "\u0000\u0129\u0000(\u0001\u0000\u0000\u0000\u0002,\u0001\u0000\u0000\u0000" +
                    "\u00044\u0001\u0000\u0000\u0000\u00068\u0001\u0000\u0000\u0000\b<\u0001" +
                    "\u0000\u0000\u0000\n@\u0001\u0000\u0000\u0000\fD\u0001\u0000\u0000\u0000" +
                    "\u000eH\u0001\u0000\u0000\u0000\u0010L\u0001\u0000\u0000\u0000\u0012P" +
                    "\u0001\u0000\u0000\u0000\u0014T\u0001\u0000\u0000\u0000\u0016_\u0001\u0000" +
                    "\u0000\u0000\u0018j\u0001\u0000\u0000\u0000\u001a{\u0001\u0000\u0000\u0000" +
                    "\u001c\u0083\u0001\u0000\u0000\u0000\u001e\u0087\u0001\u0000\u0000\u0000" +
                    " \u008f\u0001\u0000\u0000\u0000\"\u0093\u0001\u0000\u0000\u0000$\u0097" +
                    "\u0001\u0000\u0000\u0000&\u011d\u0001\u0000\u0000\u0000()\u0005\u0003" +
                    "\u0000\u0000)*\u0005\u0015\u0000\u0000*+\u0006\u0000\uffff\uffff\u0000" +
                    "+\u0001\u0001\u0000\u0000\u0000,-\u0005\u0004\u0000\u0000-.\u0005\u0015" +
                    "\u0000\u0000./\u0005\u0016\u0000\u0000/0\u0005\u0015\u0000\u000001\u0005" +
                    "\u0016\u0000\u000012\u0005\u0015\u0000\u000023\u0006\u0001\uffff\uffff" +
                    "\u00003\u0003\u0001\u0000\u0000\u000045\u0005\u0005\u0000\u000056\u0005" +
                    "\u0015\u0000\u000067\u0006\u0002\uffff\uffff\u00007\u0005\u0001\u0000" +
                    "\u0000\u000089\u0005\u0006\u0000\u00009:\u0005\u0015\u0000\u0000:;\u0006" +
                    "\u0003\uffff\uffff\u0000;\u0007\u0001\u0000\u0000\u0000<=\u0005\u0007" +
                    "\u0000\u0000=>\u0005\u0015\u0000\u0000>?\u0006\u0004\uffff\uffff\u0000" +
                    "?\t\u0001\u0000\u0000\u0000@A\u0005\b\u0000\u0000AB\u0005\u0015\u0000" +
                    "\u0000BC\u0006\u0005\uffff\uffff\u0000C\u000b\u0001\u0000\u0000\u0000" +
                    "DE\u0005\t\u0000\u0000EF\u0005\u0015\u0000\u0000FG\u0006\u0006\uffff\uffff" +
                    "\u0000G\r\u0001\u0000\u0000\u0000HI\u0005\n\u0000\u0000IJ\u0005\u0015" +
                    "\u0000\u0000JK\u0006\u0007\uffff\uffff\u0000K\u000f\u0001\u0000\u0000" +
                    "\u0000LM\u0005\u000b\u0000\u0000MN\u0005\u0015\u0000\u0000NO\u0006\b\uffff" +
                    "\uffff\u0000O\u0011\u0001\u0000\u0000\u0000PQ\u0005\f\u0000\u0000QR\u0005" +
                    "\u0015\u0000\u0000RS\u0006\t\uffff\uffff\u0000S\u0013\u0001\u0000\u0000" +
                    "\u0000TU\u0005\r\u0000\u0000UV\u0005\u0015\u0000\u0000V\\\u0006\n\uffff" +
                    "\uffff\u0000WX\u0005\u0017\u0000\u0000XY\u0005\u0015\u0000\u0000Y[\u0006" +
                    "\n\uffff\uffff\u0000ZW\u0001\u0000\u0000\u0000[^\u0001\u0000\u0000\u0000" +
                    "\\Z\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000]\u0015\u0001\u0000" +
                    "\u0000\u0000^\\\u0001\u0000\u0000\u0000_`\u0005\u000e\u0000\u0000`a\u0005" +
                    "\u0015\u0000\u0000ag\u0006\u000b\uffff\uffff\u0000bc\u0005\u0017\u0000" +
                    "\u0000cd\u0005\u0015\u0000\u0000df\u0006\u000b\uffff\uffff\u0000eb\u0001" +
                    "\u0000\u0000\u0000fi\u0001\u0000\u0000\u0000ge\u0001\u0000\u0000\u0000" +
                    "gh\u0001\u0000\u0000\u0000h\u0017\u0001\u0000\u0000\u0000ig\u0001\u0000" +
                    "\u0000\u0000jk\u0005\u000f\u0000\u0000kl\u0005\u0015\u0000\u0000lm\u0005" +
                    "\u0018\u0000\u0000mn\u0005\u0015\u0000\u0000no\u0005\u0019\u0000\u0000" +
                    "ox\u0006\f\uffff\uffff\u0000pq\u0005\u0017\u0000\u0000qr\u0005\u0015\u0000" +
                    "\u0000rs\u0005\u0018\u0000\u0000st\u0005\u0015\u0000\u0000tu\u0005\u0019" +
                    "\u0000\u0000uw\u0006\f\uffff\uffff\u0000vp\u0001\u0000\u0000\u0000wz\u0001" +
                    "\u0000\u0000\u0000xv\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000" +
                    "y\u0019\u0001\u0000\u0000\u0000zx\u0001\u0000\u0000\u0000{|\u0005\u0010" +
                    "\u0000\u0000|}\u0005\u0015\u0000\u0000}\u0081\u0006\r\uffff\uffff\u0000" +
                    "~\u007f\u0005\u0016\u0000\u0000\u007f\u0080\u0005\u0015\u0000\u0000\u0080" +
                    "\u0082\u0006\r\uffff\uffff\u0000\u0081~\u0001\u0000\u0000\u0000\u0081" +
                    "\u0082\u0001\u0000\u0000\u0000\u0082\u001b\u0001\u0000\u0000\u0000\u0083" +
                    "\u0084\u0005\u0011\u0000\u0000\u0084\u0085\u0005\u0015\u0000\u0000\u0085" +
                    "\u0086\u0006\u000e\uffff\uffff\u0000\u0086\u001d\u0001\u0000\u0000\u0000" +
                    "\u0087\u0088\u0005\u0012\u0000\u0000\u0088\u0089\u0005\u0015\u0000\u0000" +
                    "\u0089\u008a\u0005\u0016\u0000\u0000\u008a\u008b\u0005\u0015\u0000\u0000" +
                    "\u008b\u008c\u0005\u0016\u0000\u0000\u008c\u008d\u0005\u0015\u0000\u0000" +
                    "\u008d\u008e\u0006\u000f\uffff\uffff\u0000\u008e\u001f\u0001\u0000\u0000" +
                    "\u0000\u008f\u0090\u0005\u0013\u0000\u0000\u0090\u0091\u0005\u0015\u0000" +
                    "\u0000\u0091\u0092\u0006\u0010\uffff\uffff\u0000\u0092!\u0001\u0000\u0000" +
                    "\u0000\u0093\u0094\u0005\u0014\u0000\u0000\u0094\u0095\u0005\u0015\u0000" +
                    "\u0000\u0095\u0096\u0006\u0011\uffff\uffff\u0000\u0096#\u0001\u0000\u0000" +
                    "\u0000\u0097\u0098\u0003\u0000\u0000\u0000\u0098\u009c\u0006\u0012\uffff" +
                    "\uffff\u0000\u0099\u009a\u0003\u0002\u0001\u0000\u009a\u009b\u0006\u0012" +
                    "\uffff\uffff\u0000\u009b\u009d\u0001\u0000\u0000\u0000\u009c\u0099\u0001" +
                    "\u0000\u0000\u0000\u009c\u009d\u0001\u0000\u0000\u0000\u009d\u00a1\u0001" +
                    "\u0000\u0000\u0000\u009e\u009f\u0003\u0004\u0002\u0000\u009f\u00a0\u0006" +
                    "\u0012\uffff\uffff\u0000\u00a0\u00a2\u0001\u0000\u0000\u0000\u00a1\u009e" +
                    "\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000\u0000\u00a2\u00a6" +
                    "\u0001\u0000\u0000\u0000\u00a3\u00a4\u0003\u0006\u0003\u0000\u00a4\u00a5" +
                    "\u0006\u0012\uffff\uffff\u0000\u00a5\u00a7\u0001\u0000\u0000\u0000\u00a6" +
                    "\u00a3\u0001\u0000\u0000\u0000\u00a6\u00a7\u0001\u0000\u0000\u0000\u00a7" +
                    "\u00ab\u0001\u0000\u0000\u0000\u00a8\u00a9\u0003\b\u0004\u0000\u00a9\u00aa" +
                    "\u0006\u0012\uffff\uffff\u0000\u00aa\u00ac\u0001\u0000\u0000\u0000\u00ab" +
                    "\u00a8\u0001\u0000\u0000\u0000\u00ab\u00ac\u0001\u0000\u0000\u0000\u00ac" +
                    "\u00b0\u0001\u0000\u0000\u0000\u00ad\u00ae\u0003\n\u0005\u0000\u00ae\u00af" +
                    "\u0006\u0012\uffff\uffff\u0000\u00af\u00b1\u0001\u0000\u0000\u0000\u00b0" +
                    "\u00ad\u0001\u0000\u0000\u0000\u00b0\u00b1\u0001\u0000\u0000\u0000\u00b1" +
                    "\u00b5\u0001\u0000\u0000\u0000\u00b2\u00b3\u0003\f\u0006\u0000\u00b3\u00b4" +
                    "\u0006\u0012\uffff\uffff\u0000\u00b4\u00b6\u0001\u0000\u0000\u0000\u00b5" +
                    "\u00b2\u0001\u0000\u0000\u0000\u00b5\u00b6\u0001\u0000\u0000\u0000\u00b6" +
                    "\u00ba\u0001\u0000\u0000\u0000\u00b7\u00b8\u0003\u000e\u0007\u0000\u00b8" +
                    "\u00b9\u0006\u0012\uffff\uffff\u0000\u00b9\u00bb\u0001\u0000\u0000\u0000" +
                    "\u00ba\u00b7\u0001\u0000\u0000\u0000\u00ba\u00bb\u0001\u0000\u0000\u0000" +
                    "\u00bb\u00bf\u0001\u0000\u0000\u0000\u00bc\u00bd\u0003\u0010\b\u0000\u00bd" +
                    "\u00be\u0006\u0012\uffff\uffff\u0000\u00be\u00c0\u0001\u0000\u0000\u0000" +
                    "\u00bf\u00bc\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000" +
                    "\u00c0\u00c4\u0001\u0000\u0000\u0000\u00c1\u00c2\u0003\u0012\t\u0000\u00c2" +
                    "\u00c3\u0006\u0012\uffff\uffff\u0000\u00c3\u00c5\u0001\u0000\u0000\u0000" +
                    "\u00c4\u00c1\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000" +
                    "\u00c5\u00cb\u0001\u0000\u0000\u0000\u00c6\u00c7\u0003\u0014\n\u0000\u00c7" +
                    "\u00c8\u0006\u0012\uffff\uffff\u0000\u00c8\u00ca\u0001\u0000\u0000\u0000" +
                    "\u00c9\u00c6\u0001\u0000\u0000\u0000\u00ca\u00cd\u0001\u0000\u0000\u0000" +
                    "\u00cb\u00c9\u0001\u0000\u0000\u0000\u00cb\u00cc\u0001\u0000\u0000\u0000" +
                    "\u00cc\u00d3\u0001\u0000\u0000\u0000\u00cd\u00cb\u0001\u0000\u0000\u0000" +
                    "\u00ce\u00cf\u0003\u0016\u000b\u0000\u00cf\u00d0\u0006\u0012\uffff\uffff" +
                    "\u0000\u00d0\u00d2\u0001\u0000\u0000\u0000\u00d1\u00ce\u0001\u0000\u0000" +
                    "\u0000\u00d2\u00d5\u0001\u0000\u0000\u0000\u00d3\u00d1\u0001\u0000\u0000" +
                    "\u0000\u00d3\u00d4\u0001\u0000\u0000\u0000\u00d4\u00db\u0001\u0000\u0000" +
                    "\u0000\u00d5\u00d3\u0001\u0000\u0000\u0000\u00d6\u00d7\u0003\u0018\f\u0000" +
                    "\u00d7\u00d8\u0006\u0012\uffff\uffff\u0000\u00d8\u00da\u0001\u0000\u0000" +
                    "\u0000\u00d9\u00d6\u0001\u0000\u0000\u0000\u00da\u00dd\u0001\u0000\u0000" +
                    "\u0000\u00db\u00d9\u0001\u0000\u0000\u0000\u00db\u00dc\u0001\u0000\u0000" +
                    "\u0000\u00dc\u00e3\u0001\u0000\u0000\u0000\u00dd\u00db\u0001\u0000\u0000" +
                    "\u0000\u00de\u00df\u0003 \u0010\u0000\u00df\u00e0\u0006\u0012\uffff\uffff" +
                    "\u0000\u00e0\u00e2\u0001\u0000\u0000\u0000\u00e1\u00de\u0001\u0000\u0000" +
                    "\u0000\u00e2\u00e5\u0001\u0000\u0000\u0000\u00e3\u00e1\u0001\u0000\u0000" +
                    "\u0000\u00e3\u00e4\u0001\u0000\u0000\u0000\u00e4\u00eb\u0001\u0000\u0000" +
                    "\u0000\u00e5\u00e3\u0001\u0000\u0000\u0000\u00e6\u00e7\u0003\u001a\r\u0000" +
                    "\u00e7\u00e8\u0006\u0012\uffff\uffff\u0000\u00e8\u00ea\u0001\u0000\u0000" +
                    "\u0000\u00e9\u00e6\u0001\u0000\u0000\u0000\u00ea\u00ed\u0001\u0000\u0000" +
                    "\u0000\u00eb\u00e9\u0001\u0000\u0000\u0000\u00eb\u00ec\u0001\u0000\u0000" +
                    "\u0000\u00ec\u00f1\u0001\u0000\u0000\u0000\u00ed\u00eb\u0001\u0000\u0000" +
                    "\u0000\u00ee\u00ef\u0003\u001c\u000e\u0000\u00ef\u00f0\u0006\u0012\uffff" +
                    "\uffff\u0000\u00f0\u00f2\u0001\u0000\u0000\u0000\u00f1\u00ee\u0001\u0000" +
                    "\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000\u00f2\u00f6\u0001\u0000" +
                    "\u0000\u0000\u00f3\u00f4\u0003\u001e\u000f\u0000\u00f4\u00f5\u0006\u0012" +
                    "\uffff\uffff\u0000\u00f5\u00f7\u0001\u0000\u0000\u0000\u00f6\u00f3\u0001" +
                    "\u0000\u0000\u0000\u00f6\u00f7\u0001\u0000\u0000\u0000\u00f7\u00fd\u0001" +
                    "\u0000\u0000\u0000\u00f8\u00f9\u0003\u001a\r\u0000\u00f9\u00fa\u0006\u0012" +
                    "\uffff\uffff\u0000\u00fa\u00fc\u0001\u0000\u0000\u0000\u00fb\u00f8\u0001" +
                    "\u0000\u0000\u0000\u00fc\u00ff\u0001\u0000\u0000\u0000\u00fd\u00fb\u0001" +
                    "\u0000\u0000\u0000\u00fd\u00fe\u0001\u0000\u0000\u0000\u00fe\u0103\u0001" +
                    "\u0000\u0000\u0000\u00ff\u00fd\u0001\u0000\u0000\u0000\u0100\u0101\u0003" +
                    " \u0010\u0000\u0101\u0102\u0006\u0012\uffff\uffff\u0000\u0102\u0104\u0001" +
                    "\u0000\u0000\u0000\u0103\u0100\u0001\u0000\u0000\u0000\u0103\u0104\u0001" +
                    "\u0000\u0000\u0000\u0104\u010a\u0001\u0000\u0000\u0000\u0105\u0106\u0003" +
                    "\u001a\r\u0000\u0106\u0107\u0006\u0012\uffff\uffff\u0000\u0107\u0109\u0001" +
                    "\u0000\u0000\u0000\u0108\u0105\u0001\u0000\u0000\u0000\u0109\u010c\u0001" +
                    "\u0000\u0000\u0000\u010a\u0108\u0001\u0000\u0000\u0000\u010a\u010b\u0001" +
                    "\u0000\u0000\u0000\u010b\u0110\u0001\u0000\u0000\u0000\u010c\u010a\u0001" +
                    "\u0000\u0000\u0000\u010d\u010e\u0003\u001c\u000e\u0000\u010e\u010f\u0006" +
                    "\u0012\uffff\uffff\u0000\u010f\u0111\u0001\u0000\u0000\u0000\u0110\u010d" +
                    "\u0001\u0000\u0000\u0000\u0110\u0111\u0001\u0000\u0000\u0000\u0111\u0117" +
                    "\u0001\u0000\u0000\u0000\u0112\u0113\u0003\"\u0011\u0000\u0113\u0114\u0006" +
                    "\u0012\uffff\uffff\u0000\u0114\u0116\u0001\u0000\u0000\u0000\u0115\u0112" +
                    "\u0001\u0000\u0000\u0000\u0116\u0119\u0001\u0000\u0000\u0000\u0117\u0115" +
                    "\u0001\u0000\u0000\u0000\u0117\u0118\u0001\u0000\u0000\u0000\u0118%\u0001" +
                    "\u0000\u0000\u0000\u0119\u0117\u0001\u0000\u0000\u0000\u011a\u011b\u0003" +
                    "$\u0012\u0000\u011b\u011c\u0006\u0013\uffff\uffff\u0000\u011c\u011e\u0001" +
                    "\u0000\u0000\u0000\u011d\u011a\u0001\u0000\u0000\u0000\u011e\u011f\u0001" +
                    "\u0000\u0000\u0000\u011f\u011d\u0001\u0000\u0000\u0000\u011f\u0120\u0001" +
                    "\u0000\u0000\u0000\u0120\u0121\u0001\u0000\u0000\u0000\u0121\u0122\u0005" +
                    "\u0000\u0000\u0001\u0122\'\u0001\u0000\u0000\u0000\u001a\\gx\u0081\u009c" +
                    "\u00a1\u00a6\u00ab\u00b0\u00b5\u00ba\u00bf\u00c4\u00cb\u00d3\u00db\u00e3" +
                    "\u00eb\u00f1\u00f6\u00fd\u0103\u010a\u0110\u0117\u011f";
    public static final ATN _ATN =
            new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}