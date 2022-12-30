import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;

import java.util.*;

import static org.bytedeco.llvm.global.LLVM.*;

public class MyVisitor extends SysYParserBaseVisitor<LLVMValueRef>{

    Scope currentScope;
    Scope globalScope;

    //创建module
    LLVMModuleRef module = LLVMModuleCreateWithName("module");

    //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
    LLVMBuilderRef builder = LLVMCreateBuilder();

    //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
    LLVMTypeRef i32Type = LLVMInt32Type();

    LLVMTypeRef voidType = LLVMVoidType();

    LLVMValueRef zero = LLVMConstInt(i32Type, 0, 0);

    public static final BytePointer error = new BytePointer();

    public MyVisitor(){
        // init llvm components
        LLVMInitializeCore(LLVMGetGlobalPassRegistry());
        LLVMLinkInMCJIT();
        LLVMInitializeNativeAsmPrinter();
        LLVMInitializeNativeAsmParser();
        LLVMInitializeNativeTarget();
    }

    @Override
    public LLVMValueRef visitProgram(SysYParser.ProgramContext ctx) {
        globalScope = new Scope(null, "Global");
        currentScope = globalScope;
        super.visitProgram(ctx);
//        print_scope_tree();
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
                System.err.println("------------------- "+scope.name+" -------------------");
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

    private LLVMTypeRef defineFunction(LLVMTypeRef returnType, String functionName, int params){
        if (params == 0){
            return LLVMFunctionType(returnType, voidType, 0, 0);
        }
        PointerPointer<Pointer> args = new PointerPointer<>(params);
        for (int i = 0; i < params; i++) args.put(i, i32Type);
        return LLVMFunctionType(returnType, args, params, 0);
    }

    private LLVMValueRef function;
    private int index; // 指示函数形参下标
    private boolean hasReturn; // 判断是否有return语句
    @Override
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
        //
        String functionName = ctx.IDENT().getText();
        Scope functionScope = new Scope(currentScope, functionName);
        currentScope.addChildScope(functionScope);
        currentScope = functionScope;

        LLVMTypeRef returnType = ctx.funcType().INT() == null ? voidType : i32Type;
        int params = ctx.funcFParams() == null ? 0 : ctx.funcFParams().funcFParam().size();
        LLVMTypeRef functionType = defineFunction(returnType, functionName, params);
        LLVMValueRef function = LLVMAddFunction(module, functionName, functionType);
        LLVMSetFunctionCallConv(function, LLVMCCallConv);
        LLVMBasicBlockRef block = LLVMAppendBasicBlock(function, functionName+"Entry");
        LLVMPositionBuilderAtEnd(builder, block); // 本次实验在一个函数块中不涉及控制流跳转(除return)故只有一个基本块

        Symbol symbol = new Symbol(functionType, function, functionName, currentScope.enclosingScope);
        currentScope.enclosingScope.define(symbol);

        this.function = function;
        this.index = 0;
        this.hasReturn = false;

        super.visitFuncDef(ctx);

        if (!this.hasReturn){
            if (returnType.equals(voidType)) LLVMBuildRet(builder, null);
            else LLVMBuildRet(builder, zero);
        }

        currentScope = currentScope.enclosingScope;
        return null;
    }

    @Override
    public LLVMValueRef visitFuncFParam(SysYParser.FuncFParamContext ctx) {
        String name = ctx.IDENT().getText();
        LLVMValueRef paramValue = LLVMGetParam(this.function, this.index);
        LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, name + "_pointer");
        LLVMBuildStore(builder, paramValue, pointer);
        Symbol symbol = new Symbol(i32Type, pointer, name, currentScope);
        currentScope.define(symbol);
        this.index ++;
        return super.visitFuncFParam(ctx);
    }

    @Override
    public LLVMValueRef visitBlock(SysYParser.BlockContext ctx) {
        Scope localScope = new Scope(currentScope, currentScope.name+"Block");
        currentScope.addChildScope(localScope);
        currentScope = localScope;
        super.visitBlock(ctx);
        currentScope = currentScope.enclosingScope;
        return null;
    }

    private PointerPointer<Pointer> args; // 存放函数实参
    @Override
    public LLVMValueRef visitFUNC_CALL(SysYParser.FUNC_CALLContext ctx) {
        String functionName = ctx.IDENT().getText();
        Symbol symbol = currentScope.resolve(functionName);
        int params = ctx.funcRParams() == null ? 0 : ctx.funcRParams().param().size();
        LLVMTypeRef returnType = LLVMGetReturnType(symbol.type);
        LLVMValueRef result;
        if (params == 0){
            if (returnType.equals(voidType)){
                LLVMBuildCall2(builder, symbol.type, symbol.value, null, 0, "");
                return null;
            } else {
                result = LLVMBuildCall2(builder, symbol.type, symbol.value, null, 0, functionName+"_call");
                return result;
            }
        }
        args = new PointerPointer<>(params);
        super.visitFUNC_CALL(ctx);
        if (returnType.equals(voidType)){
            LLVMBuildCall2(builder, symbol.type, symbol.value, args, params, "");
            return null;
        } else {
            result = LLVMBuildCall2(builder, symbol.type, symbol.value, args, params, functionName+"_call");
            return result;
        }
    }

    @Override
    public LLVMValueRef visitFuncRParams(SysYParser.FuncRParamsContext ctx) {
        int n = ctx.getChildCount();
        for (int i = 0; i < n; i++){
            ParseTree child = ctx.getChild(i);
            LLVMValueRef value = child.accept(this);
            if (i % 2 == 0){ // is param
                args.put(i / 2, value);
            }
        }
        return null;
    }

    @Override
    public LLVMValueRef visitConstDef(SysYParser.ConstDefContext ctx) {
        String constVarName = ctx.IDENT().getText();
        int n = ctx.getChildCount();
        if (ctx.L_BRACKT().size() == 0){ // const int x = 0
            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, constVarName + "_pointer");
            for (int i = 0; i < n; i++){
                ParseTree child = ctx.getChild(i);
                LLVMValueRef value = child.accept(this);
                if (i == n-1) LLVMBuildStore(builder, value, pointer);
            }
            currentScope.define(new Symbol(i32Type, pointer, constVarName, currentScope));
        } else { // const int x[1] = {1}
            LLVMTypeRef vectorType = null;
            LLVMValueRef pointer = null;
            int size = 0;
            initValues = new ArrayList<>();
            for (int i = 0; i < n; i++){
                ParseTree child = ctx.getChild(i);
                LLVMValueRef value = child.accept(this);
                if (i == 2) {
                    size = (int)LLVMConstIntGetSExtValue(value);
                    vectorType = LLVMVectorType(i32Type, size);
                    pointer = LLVMBuildAlloca(builder, vectorType, constVarName + "_vector_pointer");
                    currentScope.define(new Symbol(vectorType, pointer, constVarName, currentScope));
                }
                if (i == n - 1) {
                    for (int j = 0; j < size; j++){
                        LLVMValueRef index = LLVMConstInt(i32Type, j, 0);
                        LLVMValueRef pointer0 = LLVMBuildGEP2(builder, vectorType, pointer,
                                new PointerPointer<>(2).put(0, zero).put(1, index),
                                2, constVarName + "[" + j + "]_pointer");
                        if (j < initValues.size()){
                            LLVMBuildStore(builder, initValues.get(j), pointer0);
                        } else {
                            LLVMBuildStore(builder, zero, pointer0);
                        }
                    }
                    initValues = null;
                }
            }
        }
        return null;
    }

    List<LLVMValueRef> initValues;
    // {1, 2}
    @Override
    public LLVMValueRef visitConstInitVal(SysYParser.ConstInitValContext ctx) {
        LLVMValueRef value = super.visitConstInitVal(ctx);
        if (ctx != null && ctx.constExp() != null && initValues != null) initValues.add(value);
        return value;
    }

    @Override
    // int x;
    // int x = 0;
    // int x[1] = {1};
    public LLVMValueRef visitVarDef(SysYParser.VarDefContext ctx) {
        String varName = ctx.IDENT().getText();
        int n = ctx.getChildCount();
        if (ctx.L_BRACKT().size() == 0) {
            LLVMValueRef pointer = LLVMBuildAlloca(builder, i32Type, varName + "_pointer");
            if (ctx.ASSIGN() == null) { // int x
            } else { // int x = 0;
                for (int i = 0 ; i < n; i++){
                    ParseTree child = ctx.getChild(i);
                    LLVMValueRef valueRef = child.accept(this);
                    if (i == n - 1) LLVMBuildStore(builder, valueRef, pointer);
                }
            }
            currentScope.define(new Symbol(i32Type, pointer, varName, currentScope));
        } else { // int x[1] = {1}
            LLVMTypeRef vectorType = null;
            LLVMValueRef pointer = null;
            int size = 0;
            initValues = new ArrayList<>();
            for (int i = 0; i < n; i++){
                ParseTree child = ctx.getChild(i);
                LLVMValueRef valueRef = child.accept(this);
                if (i == 2) {
                    size = (int)LLVMConstIntGetSExtValue(valueRef);
                    vectorType = LLVMVectorType(i32Type, size);
                    pointer = LLVMBuildAlloca(builder, vectorType, varName + "_vector_pointer");
                    currentScope.define(new Symbol(vectorType, pointer, varName, currentScope));
                }
                if (i == n - 1){
                    for (int j = 0; j < size; j++){
                        LLVMValueRef index = LLVMConstInt(i32Type, j, 0);
                        LLVMValueRef pointer0 = LLVMBuildGEP2(builder, vectorType, pointer,
                                new PointerPointer<>(2).put(0, zero).put(1, index),
                                2, varName + "[" + j + "]_pointer");
                        if (j < initValues.size()){
                            LLVMBuildStore(builder, initValues.get(j), pointer0);
                        } else {
                            LLVMBuildStore(builder, zero, pointer0);
                        }
                    }
                    initValues = null;
                }
            }
        }
        return null;
    }

    @Override
    public LLVMValueRef visitInitVal(SysYParser.InitValContext ctx) {
        LLVMValueRef value = super.visitInitVal(ctx);
        if (ctx != null && ctx.exp() != null && initValues != null) initValues.add(value);
        return value;
    }

    @Override
    public LLVMValueRef visitRETURN_STMT(SysYParser.RETURN_STMTContext ctx) {
        this.hasReturn = true;
        LLVMValueRef result = null;
        int n = ctx.getChildCount();
        for (int i = 0; i < n; i++){
            ParseTree child = ctx.getChild(i);
            LLVMValueRef value = child.accept(this);
            if (i == 1) result = value;
        }
        LLVMBuildRet(builder, result);
        return result;
    }

    @Override
    public LLVMValueRef visitUN_OP(SysYParser.UN_OPContext ctx) {
        int n = ctx.getChildCount();
        LLVMValueRef op = null;
        for (int i = 0; i < n; i++){
            ParseTree child = ctx.getChild(i);
            LLVMValueRef result = child.accept(this);
            if (i == 1) op = result;
        }

        LLVMValueRef result = null;

        if (ctx.unaryOp().PLUS() != null){ // '+op'
            result = op;
        } else if (ctx.unaryOp().MINUS() != null) { // '-op'
            result = LLVMBuildSub(builder, zero, op, "-op");
        } else { // '!op'
            LLVMValueRef cond = LLVMBuildICmp(builder, LLVMIntNE, op, zero, "op==0");
            LLVMValueRef tmp = LLVMBuildXor(builder, cond, LLVMConstInt(LLVMInt1Type(), 1, 0), "op^true");
            result = LLVMBuildZExt(builder, tmp, LLVMInt32Type(), "i1->i32");
        }

        return result;
    }

    @Override
    public LLVMValueRef visitPM_OP(SysYParser.PM_OPContext ctx) {
        int n = ctx.getChildCount();
        LLVMValueRef op1 = null, op2 = null;
        for (int i = 0; i < n; i++){
            ParseTree child = ctx.getChild(i);
            LLVMValueRef result = child.accept(this);
            if (i == 0) op1 = result;
            if (i == 2) op2 = result;
        }

        LLVMValueRef result = null;


        if (ctx.PLUS() != null) { // '+'
            result = LLVMBuildAdd(builder, op1, op2, "op1+op2");
        } else {
            result = LLVMBuildSub(builder, op1, op2, "op1-op2");
        }

        return result;
    }

    @Override
    public LLVMValueRef visitMDM_OP(SysYParser.MDM_OPContext ctx) {
        int n = ctx.getChildCount();
        LLVMValueRef op1 = null, op2 = null;
        for (int i = 0; i < n; i++){
            ParseTree child = ctx.getChild(i);
            LLVMValueRef result = child.accept(this);
            if (i == 0) op1 = result;
            if (i == 2) op2 = result;
        }

        LLVMValueRef result = null;


        if (ctx.MUL() != null) { // '*'
            result = LLVMBuildMul(builder, op1, op2, "op1*op2");
        } else if (ctx.DIV() != null) { // '/'
            result = LLVMBuildSDiv(builder, op1, op2, "op1/op2");
        } else { // '%'
            result = LLVMBuildSRem(builder, op1, op2, "op1%op2");
        }

        return result;
    }

    @Override
    public LLVMValueRef visitNumber(SysYParser.NumberContext ctx) {
        String number = ctx.INTEGR_CONST().getText();
        if (number.equals("0")){
            number = "0";
        } else if (number.startsWith("0x") || number.startsWith("0X")){
            number = Integer.parseInt(number.substring(2), 16) + "";
        } else if (number.startsWith("0")){
            number = Integer.parseInt(number.substring(1), 8) + "";
        }

        int value = Integer.parseInt(number);

        return LLVMConstInt(LLVMInt32Type(), value, 0);
    }

    // 在赋值的时候作为表达式左值应该返回symbol的value即pointer 但是在表达式右边的时候应该返回pointer指向的值
    // 注意 x[x[1]] 里面返回值 外面返回指针
    @Override
    public LLVMValueRef visitLVal(SysYParser.LValContext ctx) {
        String name = ctx.IDENT().getText();
        Symbol symbol = currentScope.resolve(name);
        if (ctx.L_BRACKT().size() == 0) { // common var
            if (isAssign) return symbol.value;
            return LLVMBuildLoad(builder, symbol.value, symbol.name);
        } else { // vector var
            LLVMValueRef index = null;
            int n = ctx.getChildCount();
            boolean flag = false;
            if (isAssign) {
                flag = true;
                isAssign = false;
            }
            for (int i = 0; i < n; i++){
                ParseTree child = ctx.getChild(i);
                LLVMValueRef value = child.accept(this);
                if (i == 2) index = value;
            }
            if (flag) isAssign = true;
            // x[2]
            LLVMValueRef elePointer =
                    LLVMBuildGEP2(builder, symbol.type, symbol.value,
                            new PointerPointer<Pointer>(2).put(0, zero).put(1, index),
                            2, symbol.name + "[]_pointer");
            if (isAssign) return elePointer;
            return LLVMBuildLoad(builder, elePointer, symbol.name+"[]");
        }
    }

    private boolean isAssign;
    @Override
    public LLVMValueRef visitASSIGNMENT(SysYParser.ASSIGNMENTContext ctx) {
        LLVMValueRef lhsPointer = null;
        LLVMValueRef rhsValue = null;
        int n = ctx.getChildCount();
        isAssign = true;
        for (int i = 0; i < n; i++){
            ParseTree child = ctx.getChild(i);
            LLVMValueRef value = child.accept(this);
            if (i == 0) {
                lhsPointer = value;
                isAssign = false;
            }
            if (i == 2) rhsValue = value;
        }
        LLVMBuildStore(builder, rhsValue, lhsPointer); // lhs = rhs
        return null;
    }

    @Override
    // (exp) => exp
    public LLVMValueRef visitEXPR(SysYParser.EXPRContext ctx) {
        int n = ctx.getChildCount();
        LLVMValueRef value = null;
        for (int i = 0; i < n; i ++){
            ParseTree child = ctx.getChild(i);
            LLVMValueRef childValue = child.accept(this);
            if (i == 1) value = childValue;
        }
        return value;
    }

    public void printToFile(String filename){
        LLVMPrintModuleToFile(module, filename, error);
    }
    public void printToConsole() {LLVMDumpModule(module);}
}
