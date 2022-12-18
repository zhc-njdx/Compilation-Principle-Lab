import org.antlr.v4.runtime.*;

import java.io.*;
import java.util.List;

public class Main
{    
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.err.println("input path is required");
        }
        String source = args[0];
        CharStream input = CharStreams.fromFileName(source);
        SysYLexer sysYLexer = new SysYLexer(input);

        sysYLexer.removeErrorListeners();
        MyErrorListener myErrorListener = new MyErrorListener();
        sysYLexer.addErrorListener(myErrorListener);

        List<? extends Token> allTokens = sysYLexer.getAllTokens();
        // occur error
        if (myErrorListener.flag) return;

        for (Token token : allTokens) {
            int typeId = token.getType();
            String type = SysYLexer.VOCABULARY.getSymbolicName(typeId);
            String text = token.getText();
            if(typeId == SysYLexer.INTEGR_CONST){
                if(text.equals("0")) {
                    text = "0";
                } else if(text.startsWith("0x") || text.startsWith("0X")){
                    text = Integer.parseInt(text.substring(2), 16) + "";
                } else if(text.startsWith("0")){
                    text = Integer.parseInt(text.substring(1), 8) + "";
                }
            }
            int lineNo = token.getLine();
            System.err.println(type + " " + text + " at Line " + lineNo + ".");
        }
    }

    static class MyErrorListener extends BaseErrorListener{
        public MyErrorListener(){}

        public boolean flag = false;

        public void syntaxError(Recognizer<?, ?> recognizer,
                                Object offendingSymbol,
                                int line,
                                int charPositionInLine,
                                String msg,
                                RecognitionException e)
        {
            flag = true;
            System.err.println("Error type A at Line " + line + ": " + msg);
        }
    }
}
