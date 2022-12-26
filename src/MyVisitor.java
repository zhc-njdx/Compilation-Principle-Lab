import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.llvm.LLVM.*;
import static org.bytedeco.llvm.global.LLVM.*;

public class MyVisitor extends SysYParserBaseVisitor<LLVMValueRef>{

    //创建module
    LLVMModuleRef module = LLVMModuleCreateWithName("module");

    //初始化IRBuilder，后续将使用这个builder去生成LLVM IR
    LLVMBuilderRef builder = LLVMCreateBuilder();

    //考虑到我们的语言中仅存在int一个基本类型，可以通过下面的语句为LLVM的int型重命名方便以后使用
    LLVMTypeRef i32Type = LLVMInt32Type();

    LLVMTypeRef voidType = LLVMVoidType();

    LLVMValueRef zero = LLVMConstInt(i32Type, 0, 0);

    LLVMBasicBlockRef block;

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
    public LLVMValueRef visitFuncDef(SysYParser.FuncDefContext ctx) {
        LLVMTypeRef mainType = LLVMFunctionType(i32Type, voidType, 0, 0);
        LLVMValueRef main = LLVMAddFunction(module, "main", mainType);
        LLVMSetFunctionCallConv(main, LLVMCCallConv);

        block = LLVMAppendBasicBlock(main, "mainEntry");
        return super.visitFuncDef(ctx);
    }

    @Override
    public LLVMValueRef visitRETURN_STMT(SysYParser.RETURN_STMTContext ctx) {
        LLVMPositionBuilderAtEnd(builder, block);
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

    public void printToFile(String filename){
        LLVMPrintModuleToFile(module, filename, error);
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
}
