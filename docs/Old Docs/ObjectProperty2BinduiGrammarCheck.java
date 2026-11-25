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

import java.nio.file.*;

/**
 * Pins the lexer and parser half of item 8 of IssuesWithObjectProperty2.md (bindui: entry names).
 * Gradle does not compile src/main/gen, so ObjectProperty2AssemblerTest cannot reach this half.
 * Each case gives the line, then the Java outcome that is expected: either LOAD name|tag|aux|uival
 * (uival "-" for none) or REFUSE (any lexer or syntax error). The C verdict is beside it. Lexer
 * errors count as refusals: the parser alone would hide a dropped character.
 *
 * Run (after regenerating ObjectProperty2Lexer/Grammar into a classes folder):
 *   java -cp <classes>:<project classes>:<antlr runtime>:<log4j> ObjectProperty2BinduiGrammarCheck [path to object_property.txt]
 */
public class ObjectProperty2BinduiGrammarCheck {
  // line, expected Java outcome, C verdict
  static final String[][] CASES = {
    {"bindui:Resist-UI:0",                    "LOAD Resist-UI||0|-",                "C loads"},
    {"bindui:resist<fire>:0",                 "LOAD resist|fire|0|-",               "C loads"},
    {"bindui:resist_ui_compact_0<FIRE>:0",    "LOAD resist_ui_compact_0|FIRE|0|-",  "C loads (the shipped shape)"},
    {"bindui:resist_ui_compact_0<FIRE>:1:5",  "LOAD resist_ui_compact_0|FIRE|1|5",  "C loads"},
    {"bindui:stat_ui:1",                      "LOAD stat_ui||1|-",                  "C loads"},
    {"bindui:resist ui:0",                    "LOAD resist ui||0|-",                "C loads"},
    {"bindui: :0",                            "LOAD  ||0|-",                        "C loads"},
    {"bindui:x::0",                           "LOAD x||0|-",                        "C loads"},
    {"bindui:x:0::5",                         "LOAD x||0|5",                        "C loads"},
    {"bindui:x<A>:0",                         "LOAD x|A|0|-",                       "C loads"},
    // accepted deviations: C loads, Java refuses
    {"bindui:resist<FIRE:0",                  "REFUSE",                             "C loads (accepted)"},
    {"bindui:resist<FIRE>x:0",                "REFUSE",                             "C loads (accepted)"},
    {"bindui:a<B>c<D>:0",                     "REFUSE",                             "C loads (accepted)"},
    {"bindui:<:0",                            "REFUSE",                             "C loads (accepted)"},
    {"bindui::x:0",                           "REFUSE",                             "C loads as x (accepted)"},
    // accepted deviation: C loads the name x<>, Java loads the name x
    {"bindui:x<>:0",                          "LOAD x||0|-",                        "C loads x<> (accepted)"},
    // both refuse
    {"bindui:x",                              "REFUSE",                             "C refuses"},
    {"bindui:x:",                             "REFUSE",                             "C refuses"},
    {"bindui::0",                             "REFUSE",                             "C refuses"},
  };

  public static void main(String[] args) throws Exception {
    int bad = 0;
    for (String[] c : CASES) {
      String got = outcome("name:x\ntype:flag\ncode:NONE\n" + c[0] + "\n");
      boolean ok = got.equals(c[1]);
      if (!ok) bad++;
      System.out.println(String.format("%s  %-40s %-8s %s", ok ? "ok  " : "FAIL", c[0], c[2].startsWith("C refuses") ? "C refuses" : "", got.equals("REFUSE") ? "Java refuses" : "Java loads"));
      if (!ok) System.out.println("      expected " + c[1] + ", got " + got);
    }
    if (args.length > 0) {
      ObjectProperty2Lexer lx = new ObjectProperty2Lexer(CharStreams.fromPath(Path.of(args[0])));
      ObjectProperty2Grammar p = new ObjectProperty2Grammar(new CommonTokenStream(lx));
      var f = p.file();
      int b = 0;
      for (var r : f.properties) for (var d : r.directives()) if (d.name().equals("bindui")) b++;
      boolean ok = p.getNumberOfSyntaxErrors() == 0 && f.properties.size() == 79 && b == 66;
      if (!ok) bad++;
      System.out.println((ok ? "ok  " : "FAIL") + "  shipped file: records=" + f.properties.size() + " bindui=" + b + " syntaxErrors=" + p.getNumberOfSyntaxErrors());
    }
    System.out.println(bad == 0 ? "ALL AS PINNED" : bad + " DIFFER");
  }

  static String outcome(String text) {
    ObjectProperty2Lexer lx = new ObjectProperty2Lexer(CharStreams.fromString(text));
    final int[] errs = {0};
    BaseErrorListener count = new BaseErrorListener() {
      public void syntaxError(Recognizer<?, ?> r, Object o, int l, int ch, String m, RecognitionException e) { errs[0]++; }
    };
    lx.removeErrorListeners();
    lx.addErrorListener(count);
    ObjectProperty2Grammar p = new ObjectProperty2Grammar(new CommonTokenStream(lx));
    p.removeErrorListeners();
    p.addErrorListener(count);
    var f = p.file();
    if (errs[0] > 0) return "REFUSE";
    var v = f.properties.get(0).directives().get(3).values();
    return "LOAD " + v.get(0) + "|" + v.get(1) + "|" + v.get(2) + "|" + (v.get(3) == null ? "-" : v.get(3));
  }
}
