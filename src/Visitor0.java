import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

/**
 * 遍历语法树 生成作用域符号表
 */
public class Visitor0 extends SysYParserBaseVisitor<Type>{

    boolean hasError = false; // 判断是否出现错误

    int target_row; // 待替换变量的行号
    int target_col; // 待替换变量的列号

    Scope currentScope; // 遍历过程中指示当前作用域
    Scope globalScope; // 全局作用域

    Type DefType; // 声明变量的类型 在decl中赋值 在def中使用
    Type ReturnType; // 函数的返回类型 在funcDef中赋值 在return_stmt中使用

    boolean isAssign; // 判断是否是赋值语句 在assign中赋值 在LVal中使用
    boolean isFuncCall; // 判断IDENT是否是函数调用 在FuncCall中赋值 在visitTerminal中使用

    @Override
    public Type visitChildren(RuleNode node) {
        boolean hasError = false;

        Type result = defaultResult();
        int n = node.getChildCount();
        for (int i=0; i<n; i++) {
            if (!shouldVisitNextChild(node, result)) {
                break;
            }

            ParseTree c = node.getChild(i);
            Type childResult = c.accept(this);
            if (childResult != null){
                result = childResult;
                if (childResult.toString().equals("error")) hasError = true;
            }
        }
        if (hasError) return null;
        return result;
    }

    @Override
    public Type visitTerminal(TerminalNode node) {
        int type = node.getSymbol().getType();
        String symbol_name = SysYParser.VOCABULARY.getSymbolicName(type);

        String name = node.toString();

        // 如果是函数定义 其下的终结符已经被处理，此处直接返回
        int parentRuleIdx = ((RuleNode) node.getParent()).getRuleContext().getRuleIndex();
        if (parentRuleIdx == SysYParser.RULE_funcDef) return null;

        if (symbol_name.equals("INTEGR_CONST")){
            return new BaseType("int");
        } else if (symbol_name.equals("IDENT")){
            Symbol symbol = currentScope.resolve(name);
            if (symbol == null){ // not define
                System.err.println("Error type 1 at Line " + node.getSymbol().getLine() + ": IDENT " + name + " is not define...");
                hasError = true;
                return null;
            } else {
                // check whether the target variable need to be replaced
                if (node.getSymbol().getLine() == target_row && node.getSymbol().getCharPositionInLine() == target_col)
                    symbol.isNeedReplace = true;
                // add the use position of this variable
                symbol.addUsePos(node.getSymbol().getLine(), node.getSymbol().getCharPositionInLine());
                int _type = symbol.type.getType();
                // 只有是函数调用 才返回该函数的返回类型
                if (_type == Type.FUNC_TYPE && isFuncCall){
                    return ((FuncType) symbol.type).returnType;
                } else {
                    return symbol.type;
                }
            }
        }
        return null;
    }

    // print the scope tree
    private void print_scope_tree(){
        System.err.println("==========================scope tree===========================\n");
        Queue<Scope> queue = new LinkedList<>();
        queue.add(globalScope);
        int cnt = 0;
        while (!queue.isEmpty()){
            int size = queue.size();
            for (int i = 0; i < size; i++){
                Scope scope = queue.poll();
                System.err.println("------------------- Scope"+(cnt++)+" -------------------");
                print_symbols(scope);
                System.err.println("----------------------------------------------");
                queue.addAll(scope.childScope);
            }
        }
    }

    // print the symbols in a scope
    private void print_symbols(Scope scope){
        for (Map.Entry<String, Symbol> entry : scope.symbols.entrySet()) {
            System.err.println(entry.getValue().toString());
        }
    }

    @Override
    public Type visitProgram(SysYParser.ProgramContext ctx) {
        globalScope = new Scope(null);
        currentScope = globalScope;
        visitChildren(ctx);
//        print_scope_tree();
        return null;
    }


    @Override
    public Type visitFuncDef(SysYParser.FuncDefContext ctx) {
        int line = ctx.getStart().getLine();

        String funcName = ctx.IDENT().getText();
        Symbol symbol0 = currentScope.resolve(funcName);
        if (symbol0 != null) {
            System.err.println("Error type 4 at Line " + line + ": " + funcName + " is redefined...");
            hasError = true;
            return null;
        }

        Scope funcScope = new Scope(currentScope);
        currentScope.addChildScope(funcScope);
        currentScope = funcScope;

        BaseType retType = new BaseType(ctx.funcType().getText());
        ReturnType = retType;

        int n = ctx.getChildCount();
        for (int i = 0; i < n; i++){
            ParseTree child = ctx.getChild(i);
            Type accept = child.accept(this);
            if (i == n-2){ // 遍历到block前一个节点时，处理函数形参列表 形成函数符号
                FuncType funcType = new FuncType();
                List<Type> params = new ArrayList<>();
                if (currentScope.symbols.size() != 0){
                    for (Map.Entry<String, Symbol> entry : currentScope.symbols.entrySet()){
                        params.add(entry.getValue().type);
                    }
                }
                funcType.returnType = retType;
                funcType.params = params;

                Symbol funcSymbol = new Symbol(funcType, ctx.IDENT().getText(), currentScope.enclosingScope);
                funcSymbol.row = line;
                funcSymbol.col = ctx.IDENT().getSymbol().getCharPositionInLine();
                if (funcSymbol.row == target_row && funcSymbol.col == target_col) funcSymbol.isNeedReplace = true;
                currentScope.enclosingScope.define(funcSymbol);
            }
        }

        ReturnType = null;

        currentScope = currentScope.enclosingScope;

        return null;
    }

    @Override
    public Type visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        int line = ctx.getStart().getLine();
        String name = ctx.IDENT().getText();
        // 注意函数参数只需要在当前作用域下解析，即函数参数与全局同名变量不冲突
        Symbol symbol0 = currentScope.resolveCurrentScope(name);
        if (symbol0 != null){
            System.err.println("Error type 3 at Line " + line + ": " + name + " is redefined...");
            hasError = true;
            return null;
        }
        List<TerminalNode> l_brackt = ctx.L_BRACKT();
        Type type;
        Symbol symbol;
        if (l_brackt.size() == 0){ // 基本类型
            type = new BaseType(ctx.bType().getText());
        } else { // 数组类型
            type = new ArrayType();
            ((ArrayType) type).dimen = l_brackt.size();
            ((ArrayType) type).eleType = new BaseType(ctx.bType().getText());
        }
        symbol = new Symbol(type, name, currentScope);
        symbol.row = line;
        symbol.col = ctx.IDENT().getSymbol().getCharPositionInLine();
        if (symbol.row == target_row && symbol.col == target_col) symbol.isNeedReplace = true;
        currentScope.define(symbol);
        return super.visitFuncFParam(ctx);
    }

    @Override
    public Type visitRETURN_STMT(SysYParser.RETURN_STMTContext ctx) {
        int line = ctx.getStart().getLine();
        Type retType = visitChildren(ctx);
        if (ReturnType == null){
            return null;
        }
        boolean hasReturnVal = ctx.exp() != null; // 判断是否有返回值
        // 不能直接用 retType == null 判断是否返回值，等于null有可能是出现了错误
        if ( (hasReturnVal && retType != null && !ReturnType.isSameType(retType))
                || (!hasReturnVal && !ReturnType.toString().equals("void"))){
            System.err.println("Error type 7 at Line " + line + ": " + "return type is not match...");
            hasError = true;
        }
        return null;
    }

    @Override
    public Type visitBlock(SysYParser.BlockContext ctx) {
        Scope localScope = new Scope(currentScope);
        currentScope.addChildScope(localScope);
        currentScope = localScope;

        if (ctx.parent.getRuleIndex() == SysYParser.RULE_funcDef){ // 将函数形参加入作用域，方便函数体内的判断
            if (currentScope.enclosingScope.symbols.size() != 0){
                currentScope.symbols.putAll(currentScope.enclosingScope.symbols);
            }
        }

        visitChildren(ctx);

        currentScope = currentScope.enclosingScope;
        return null;
    }

    @Override
    public Type visitConstDecl(SysYParser.ConstDeclContext ctx) {
        DefType = new BaseType(ctx.bType().getText());
        Type result = visitChildren(ctx);
        DefType = null;
        return result;
    }

    @Override
    public Type visitVarDecl(SysYParser.VarDeclContext ctx) {
        DefType = new BaseType(ctx.bType().getText());
        Type result = visitChildren(ctx);
        DefType = null;
        return result;
    }

    @Override
    public Type visitConstDef(SysYParser.ConstDefContext ctx) {
        int line = ctx.getStart().getLine();
        String name = ctx.IDENT().getText();
        Type type = null;
        if (DefType != null){
            Symbol symbol0 = currentScope.resolveCurrentScope(name); // 在当前作用域里解析
            if (symbol0 != null){
                System.err.println("Error type 3 at Line " + line + ": " + name + " is redefined...");
                hasError = true;
                return null;
            }
            List<TerminalNode> l_brackt = ctx.L_BRACKT();
            if (l_brackt.size() == 0){ // base type
                type = new BaseType(DefType.toString());
            } else { // array type
                type = new ArrayType();
                ((ArrayType) type).dimen = l_brackt.size();
                ((ArrayType) type).eleType = new BaseType(DefType.toString());
            }
            Symbol symbol = new Symbol(type, name, currentScope);
            symbol.row = line;
            symbol.col = ctx.IDENT().getSymbol().getCharPositionInLine();
            if (symbol.row == target_row && symbol.col == target_col) symbol.isNeedReplace = true;
            currentScope.define(symbol);
        }
        Type rightType = visitChildren(ctx); // 如果返回null 说明右侧出错误了
        if (rightType == null) {
            return null;
        } else if (!rightType.isSameType(type)){
            System.err.println("Error type 5 at Line " + line + ": " + "type not match for assignment...");
            hasError = true;
        }
        return null;
    }

    @Override
    public Type visitVarDef(SysYParser.VarDefContext ctx) {
        int line = ctx.getStart().getLine();
        String name = ctx.IDENT().getText();
        Type type = null;
        if (DefType != null){
            Symbol symbol0 = currentScope.resolveCurrentScope(name);
            if (symbol0 != null){
                System.err.println("Error type 3 at Line " + line + ": " + name + " is redefined...");
                hasError = true;
                return null;
            }
            List<TerminalNode> l_brackt = ctx.L_BRACKT();
            if (l_brackt.size() == 0){ // base type
                type = new BaseType(DefType.toString());
            } else { // array type
                type = new ArrayType();
                ((ArrayType) type).dimen = l_brackt.size();
                ((ArrayType) type).eleType = new BaseType(DefType.toString());
            }
            Symbol symbol = new Symbol(type, name, currentScope);
            symbol.row = line;
            symbol.col = ctx.IDENT().getSymbol().getCharPositionInLine();
            if (symbol.row == target_row && symbol.col == target_col) symbol.isNeedReplace = true;
            currentScope.define(symbol);
        }
        Type rightType = visitChildren(ctx); // 如果返回null 说明右侧出错误了
        // visitChildren 返回的是最后一个不为 null 的类型
        // 如果rightType != null 两种情况 1. 没有赋值的情况下 可能性很多 2. 赋值情况下是 initVal 的类型
        if (ctx.ASSIGN() == null) return null; // 没有赋值 直接返回
        if (rightType == null) {
            return null;
        } else if (!rightType.isSameType(type)){
            System.err.println("Error type 5 at Line " + line + ": " + "type not match for assignment...");
            hasError = true;
        }
        return null;
    }

    @Override
    public Type visitFUNC_CALL(SysYParser.FUNC_CALLContext ctx) {
        int line = ctx.getStart().getLine();
        String funcName = ctx.IDENT().getText();
        Symbol symbol = currentScope.resolve(funcName);
        if (symbol != null){
            if (symbol.type.getType() != Type.FUNC_TYPE){
                System.err.println("Error type 10 at Line " + line + ": FUNC " + funcName + " is not a function...");
                hasError = true;
                return null;
            } else {
                int FParamsLen = ((FuncType) symbol.type).params.size();
                int RParamsLen = ctx.funcRParams() == null ? 0 : ctx.funcRParams().param().size();
                if (FParamsLen != RParamsLen){ // 形参和实参数量不一样
                    System.err.println("Error type 8 at Line " + line + ": params not match...");
                    hasError = true;
                    return null;
                }
            }
        } else {
            System.err.println("Error type 2 at Line " + line + ": FUNC " + funcName + " is not define...");
            hasError = true;
            return null;
        }
        isFuncCall = true;
        Type res = super.visitFUNC_CALL(ctx);
        isFuncCall = false;
        return res;
    }

    @Override
    public Type visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        isFuncCall = !isFuncCall;

        int line = ctx.getStart().getLine();

        // 获得函数形参列表
        SysYParser.FUNC_CALLContext funcCallContext = (SysYParser.FUNC_CALLContext) ctx.parent;
        Symbol symbol = currentScope.resolve(funcCallContext.IDENT().getText());
        List<Type> params = ((FuncType) symbol.type).params;

        Type result = defaultResult();
        int n = ctx.getChildCount();
        for (int i = 0; i < n; i++) {

            ParseTree c = ctx.getChild(i);
            Type childResult = c.accept(this);
            if (i % 2 == 0){ // 获得函数实参
                if (childResult != null && !childResult.isSameType(params.get(i / 2 ))){
                    System.err.println("Error type 8 at Line " + line + ": params not match...");
                    hasError = true;
                    isFuncCall = !isFuncCall;
                    return new BaseType("error");
                }
            }
        }

        isFuncCall = !isFuncCall;

        return result;
    }

    @Override
    public Type visitUN_OP(SysYParser.UN_OPContext ctx) {
        int line = ctx.getStart().getLine();
        int childCount = ctx.getChildCount();
        Type type = null;
        for (int i = 0; i < childCount; i ++){
            ParseTree child = ctx.getChild(i);
            Type accept = child.accept(this);
            if (i == 1) type = accept; // + | - exp
        }

        if (type == null) {
            return null;
        }
        if (type.toString().equals("int")){
            return type;
        } else {
            System.err.println("Error type 6 at Line " + line + ": " + "operator not match...");
            hasError = true;
            return null;
        }
    }

    @Override
    public Type visitPM_OP(SysYParser.PM_OPContext ctx) {
        return visitBinaryOP(ctx);
    }

    @Override
    public Type visitMDM_OP(SysYParser.MDM_OPContext ctx) {
        return visitBinaryOP(ctx);
    }

    @Override
    public Type visitLG(SysYParser.LGContext ctx) {
        return visitBinaryOP(ctx);
    }

    @Override
    public Type visitEN(SysYParser.ENContext ctx) {
        return visitBinaryOP(ctx);
    }

    @Override
    public Type visitAND(SysYParser.ANDContext ctx) {
        return visitBinaryOP(ctx);
    }

    @Override
    public Type visitOR(SysYParser.ORContext ctx) {
        return visitBinaryOP(ctx);
    }

    private Type visitBinaryOP(ParserRuleContext ctx){
        int line = ctx.getStart().getLine();
        int childCount = ctx.getChildCount();
        Type type1 = null, type2 = null;
        for (int i = 0; i < childCount; i ++){
            ParseTree child = ctx.getChild(i);
            Type accept = child.accept(this);
            if (i == 0) type1 = accept;
            if (i == 2) type2 = accept;
        }
        if (type1 == null || type2 == null) {
            return null;
        }
        if (type1.toString().equals("int") && type2.toString().equals("int")){
            return type1;
        } else {
            System.err.println("Error type 6 at Line " + line + ": " + "operator not match...");
            hasError = true;
            return null;
        }
    }

    @Override
    public Type visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        TerminalNode l_brace = ctx.L_BRACE();
        boolean isArray = l_brace != null;
        Type result = super.visitConstInitVal(ctx);
        if (isArray){
            if (result == null){ // {{}{}} 针对初始化没有值的情况
                ArrayType type = new ArrayType();
                type.dimen = 1;
                RuleContext parent = ctx.parent;
                while (parent.getRuleIndex() != SysYParser.RULE_constDecl){
                    parent = parent.parent;
                }
                String eleTypeName = ((SysYParser.ConstDeclContext) parent).bType().getText();
                type.eleType = new BaseType(eleTypeName);
                return type;
            } else if (result.getType() != Type.ARRAY_TYPE) { // 返回基础类型 一维数组
                ArrayType type = new ArrayType();
                type.dimen = 1;
                type.eleType = new BaseType(result.toString());
                return type;
            } else { // 返回数组 维度++
                ((ArrayType) result).dimen ++;
                return result;
            }
        } else {
            return result;
        }
    }

    @Override
    public Type visitInitVal(SysYParser.InitValContext ctx) {
        TerminalNode l_brace = ctx.L_BRACE();
        boolean isArray = l_brace != null;
        Type result = super.visitInitVal(ctx);
        if (isArray){
            if (result == null){
                ArrayType type = new ArrayType();
                type.dimen = 1;
                RuleContext parent = ctx.parent;
                while (parent.getRuleIndex() != SysYParser.RULE_varDecl){
                    parent = parent.parent;
                }
                String eleTypeName = ((SysYParser.VarDeclContext) parent).bType().getText();
                type.eleType = new BaseType(eleTypeName);
                return type;
            } else if (result.getType() != Type.ARRAY_TYPE) {
                ArrayType type = new ArrayType();
                type.dimen = 1;
                type.eleType = new BaseType(result.toString());
                return type;
            } else {
                ((ArrayType) result).dimen ++;
                return result;
            }
        } else {
            return result;
        }
    }

    @Override
    public Type visitLVal(SysYParser.LValContext ctx) {
        int line = ctx.getStart().getLine();
        String valName = ctx.IDENT().getText();
        Symbol symbol = currentScope.resolve(valName);
        Type type = null;
        List<TerminalNode> l_brackt = ctx.L_BRACKT();
        if (l_brackt.size() != 0){ // use the []
            // symbol == null 后面会处理
            if (symbol != null ) {
                if (symbol.type.getType() != Type.ARRAY_TYPE) {
                    System.err.println("Error type 9 at Line " + line + ": " + valName + " is not array...");
                    hasError = true;
                    return null;
                } else {
                    int dimen = ((ArrayType) symbol.type).dimen - l_brackt.size();
                    if (dimen == 0){ //  []数量和数组维数一致 之后变为 base type
                        type = new BaseType(((ArrayType) symbol.type).eleType.toString());
                    } else if (dimen > 0){
                        type = new ArrayType();
                        ((ArrayType) type).dimen = dimen;
                        ((ArrayType) type).eleType = new BaseType(((ArrayType) symbol.type).eleType.toString());
                    } else { // dimen < 0
                        // int x[1]; x[1][2]; 相当于对int变量x[1]使用[]操作符
                        System.err.println("Error type 9 at Line " + line + ": [] cnt > dimen, is not array...");
                        hasError = true;
                        return null;
                    }
                }
            }
        }
        // 如果是赋值语句 且 左边是函数
        if (isAssign && symbol != null && symbol.type.getType() == Type.FUNC_TYPE){
            System.err.println("Error type 11 at Line " + line + ": " + "left of the = can not be a function...");
            hasError = true;
            return null;
        }
        int n = ctx.getChildCount();
        Type result = null;
        for (int i = 0; i < n; i++){
            ParseTree child = ctx.getChild(i);
            Type accept = child.accept(this);
            if (i == 0) result = accept;
            if (i >= 2 && (i - 2) % 3 == 0){ // [] 中的表达式
                if (accept != null && !accept.toString().equals("int")){
                    System.err.println("Error type 6 at Line " + line + ": " + "operator not match...");
                    hasError = true;
                    return null;
                }
            }
        }
        if (type != null){
            return type;
        } else {
            return result;
        }
    }

    @Override
    public Type visitASSIGNMENT(SysYParser.ASSIGNMENTContext ctx) {
        int line = ctx.getStart().getLine();
        int childCount = ctx.getChildCount();
        Type type1 = null, type2 = null;
        for (int i = 0; i < childCount; i ++){
            ParseTree child = ctx.getChild(i);
            if (i == 0) isAssign = true;
            Type accept = child.accept(this);
            isAssign = false;
            if (i == 0) type1 = accept;
            if (i == 2) type2 = accept;
        }
        if (type1 == null || type2 == null) {
            return null;
        }
        if (type1.isSameType(type2)){
            return type1;
        } else {
            System.err.println("Error type 5 at Line " + line + ": " + "type not match for assignment...");
            hasError = true;
            return null;
        }
    }
}
