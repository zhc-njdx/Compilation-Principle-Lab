import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;

public class Main
{
    private static final boolean debug = false; // 提交oj: false 本地开发: true 信息输出控制台

    public static void main(String[] args) throws IOException {
        if(!debug && args.length < 1){
            System.err.println("lack of params...");
            return;
        }
        // get the params...
        String source = debug ? "/home/zhhc/Compile-Principle/Lab/tests/test1.sysy" : args[0];
        String llvm_ir_store_path = debug ? null : args[1];

        // get input file and generate the Lexer
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);

        // generate the Parser
        CommonTokenStream tokens = new CommonTokenStream(sysYLexer);
        SysYParser sysYParser = new SysYParser(tokens);

        MyVisitor visitor = new MyVisitor();

        // DFS the tree to make the scope tree
        ParseTree tree = sysYParser.program();
        visitor.visit(tree);

        if (!debug) visitor.printToFile(llvm_ir_store_path);
        else visitor.printToConsole();
    }
}
