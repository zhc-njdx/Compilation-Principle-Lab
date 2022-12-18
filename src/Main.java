import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;

public class Main
{    
    public static void main(String[] args) throws IOException {
        if(args.length < 4){
            System.err.println("lack of params...");
        }
        // get the params...
        String source = args[0];
        int rowNo = Integer.parseInt(args[1]);
        int colNo = Integer.parseInt(args[2]);
        String newStr = args[3];

        // get input file and generate the Lexer
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);

        // generate the Parser
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);

        Visitor0 visitor = new Visitor0();
        visitor.target_row = rowNo;
        visitor.target_col = colNo;

        // DFS the tree to make the scope tree
        ParseTree tree = sysYParser.program();
        visitor.visit(tree);

        if (visitor.hasError){
            return;
        }

        // print the parse tree
        Visitor visitor0 = new Visitor(visitor.globalScope, newStr);
        visitor0.visit(tree);
    }
}
