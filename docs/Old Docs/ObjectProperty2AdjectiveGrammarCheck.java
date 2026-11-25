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

import org.antlr.v4.runtime.*;
import uk.co.jackoftradesltd.backend.parser.grammars.*;
import uk.co.jackoftradesltd.backend.parser.objectproperty.*;
public class ObjectProperty2AdjectiveGrammarCheck {
  public static void main(String[] a) {
    String[] cases = {
      "adjective:fast moving", "adjective:Slow", "adjective:half-hearted", "adjective: foo", "adjective:fast", "adjective:foo ",
      "neg-adjective:very weak", "neg-adjective:Weak", "adjective:"
    };
    for (String c : cases) {
      String text = "name:x\ntype:stat\n" + c + "\n";
      ObjectProperty2Lexer lx = new ObjectProperty2Lexer(CharStreams.fromString(text));
      lx.removeErrorListeners();
      ObjectProperty2Grammar p = new ObjectProperty2Grammar(new CommonTokenStream(lx));
      final int[] errs = {0};
      p.removeErrorListeners();
      p.addErrorListener(new BaseErrorListener(){ public void syntaxError(Recognizer<?,?> r,Object o,int l,int ch,String m,RecognitionException e){errs[0]++;}});
      var f = p.file();
      String vals = errs[0]>0 ? "SYNTAX ERROR" : f.properties.get(0).directives().toString();
      System.out.println(String.format("%-28s -> %s", "'"+c+"'", vals));
    }
  }
}
