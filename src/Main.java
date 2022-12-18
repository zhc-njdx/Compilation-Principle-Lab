import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.List;

public class Main
{    
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.err.println("input path is required");
        }
        // get input file and generate the Lexer
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);

        // generate the Parser
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);

        Visitor visitor = new Visitor();

        // add error listener
        sysYParser.removeErrorListeners();
        MyParserErrorListener myParserErrorListener = new MyParserErrorListener(visitor);
        sysYParser.addErrorListener(myParserErrorListener);

        // DFS the tree
        ParseTree tree = sysYParser.program();
        visitor.visit(tree);
    }

    static class MyParserErrorListener extends BaseErrorListener{
        Visitor visitor;
        public MyParserErrorListener(Visitor v){ this.visitor = v; }


        public void syntaxError(Recognizer<?, ?> recognizer,
                                Object offendingSymbol,
                                int line,
                                int charPositionInLine,
                                String msg,
                                RecognitionException e)
        {
            this.visitor.hasError = true;
            System.err.println("Error type B at Line " + line + ": " + msg);
        }
    }
}
