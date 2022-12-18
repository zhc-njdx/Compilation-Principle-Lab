import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.sql.Struct;
import java.util.*;

public class Visitor extends SysYParserBaseVisitor<Void>{

    Scope globalScope; // the root of scope tree made in Visitor0
    String newStr;
    List<int[]> pos; // the position need to be replaced

    public Visitor(Scope globalScope, String newStr){
        this.globalScope = globalScope;
        this.newStr = newStr;
        this.pos = new LinkedList<>();

        // 获取全部的需要重命名的变量的位置
        Queue<Scope> queue = new LinkedList<>();
        queue.add(globalScope);
        boolean isOver = false;
        while (!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++){
                Scope scope = queue.poll();
                for (Map.Entry<String, Symbol> entry : scope.symbols.entrySet()){
                    Symbol symbol = entry.getValue();
                    if (symbol.isNeedReplace){
                        pos.add(new int[]{symbol.row, symbol.col});
                        pos.addAll(symbol.usePos);
                        isOver = true;
                        break;
                    }
                }
                if (isOver) break;
                queue.addAll(scope.childScope);
            }
            if (isOver) break;
        }
    }

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

    private boolean isNeedReplace(int row, int col){
        for (int[] pos : this.pos){
            if (row == pos[0] && col == pos[1]){
                return true;
            }
        }
        return false;
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
            // 判断是否需要重命名
            if (isNeedReplace(node.getSymbol().getLine(), node.getSymbol().getCharPositionInLine())){
                literal_name = newStr;
            }
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
