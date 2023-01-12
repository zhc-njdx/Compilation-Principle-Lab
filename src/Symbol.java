import org.bytedeco.llvm.LLVM.LLVMTypeRef;
import org.bytedeco.llvm.LLVM.LLVMValueRef;


public class Symbol{


    public LLVMTypeRef type;
    public LLVMValueRef value; // 符号类型
    public String name; // 符号名称
    public Scope scope; // 符号所在作用域

    public Symbol(LLVMTypeRef type, LLVMValueRef value, String name, Scope scope){
        this.type = type;
        this.value = value;
        this.name = name;
        this.scope = scope;
    }

    @Override
    public String toString() {
        return name;
    }
}
