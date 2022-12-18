import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Locale;

public class Visitor extends SysYParserBaseVisitor<Void>{

    public boolean hasError = false;

    private String getColor(int type) {
        if (type < 0 || type >= _COLOR_NAMES.length) return "";
        return _COLOR_NAMES[type];
    }
    private static final String[] _COLOR_NAMES = {
            null, "[orange]", "[orange]", "[orange]", "[orange]", "[orange]", "[orange]", "[orange]", "[orange]",
            "[orange]", "[blue]", "[blue]", "[blue]", "[blue]", "[blue]", "[blue]", "[blue]", "[blue]",
            "[blue]", "[blue]", "[blue]", "[blue]", "[blue]", "[blue]", "[blue]", "", "", "",
            "", "", "", "", "", "[green]",
            "[red]", "", "", ""
    };
    private void printIndents(int depth){
        for (int i = 0; i < depth * 2; i++){
            System.err.print(" ");
        }
    }
    @Override
    public Void visitChildren(RuleNode node) {
        int ruleIdx = node.getRuleContext().getRuleIndex();
        String rule = SysYParser.ruleNames[ruleIdx];
        if (!hasError){
            int depth = node.getRuleContext().depth();
            printIndents(depth-1); // need -1: depth begin from 1
            System.err.println(rule.toUpperCase().charAt(0) + rule.substring(1));
        }
        return super.visitChildren(node);
    }
    @Override
    public Void visitTerminal(TerminalNode node) {
        int type = node.getSymbol().getType(); // node type
        String color = getColor(type);
        if (!hasError && !color.equals("")){ // "" means the terminal node we need, such as '{' '}'
            String literal_name = node.toString();
            String symbol_name = SysYParser.VOCABULARY.getSymbolicName(type);
            // deal with numbers
            if (type == SysYParser.INTEGR_CONST){
                literal_name = convert_to_dec(literal_name);
            }
            int depth = ((RuleNode)node.getParent()).getRuleContext().depth(); // the depth in parser tree
            printIndents(depth);
            System.err.println(literal_name + " " + symbol_name + color);
        }
        return null;
    }

    private String convert_to_dec(String number){
        if (number.equals("0")){
            number = "0";
        } else if (number.startsWith("0x") || number.startsWith("0X")){
            number = Integer.parseInt(number.substring(2), 16) + "";
        } else if (number.startsWith("0")){
            number = Integer.parseInt(number.substring(1), 8) + "";
        }
        return number;
    }
}
