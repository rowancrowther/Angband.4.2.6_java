package uk.co.jackoftrades;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import uk.co.jackoftrades.background.io.parsers.antlr.parsers.summon.SummonFormatterLexer;
import uk.co.jackoftrades.background.io.parsers.antlr.parsers.summon.SummonFormatterParser;
import uk.co.jackoftrades.middle.monsters.Summon;

import java.io.IOException;
import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

        CharStream stream = CharStreams.fromFileName("C:\\Users\\rowan\\OneDrive\\Documents\\Desktop\\Angband-4.2.6\\lib\\gamedata\\summon.txt");
        SummonFormatterLexer lexer = new SummonFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SummonFormatterParser parser = new SummonFormatterParser(tokens);
        SummonFormatterParser.FileContext result = parser.file();
        ArrayList<Summon> summons = result.summons;

        for (Summon summon : summons) {
            System.out.println(summon.toString());
        }

/*        CharStream stream = CharStreams.fromString("-1+2*3d4");
        RandomFormatterLexer lexer = new RandomFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RandomFormatterParser parser = new RandomFormatterParser(tokens);
        RandomFormatterParser.RandomContext result = parser.random();
        Random resultDice = result.randomDice;
*/
        ;

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
}