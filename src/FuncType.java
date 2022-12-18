import java.util.List;

public class FuncType implements Type {
    public Type returnType;
    public List<Type> params;

    @Override
    public boolean isSameType(Type type) {
        if (type.getType() != FUNC_TYPE) return false;
        FuncType funcType = (FuncType) type;
        if (!returnType.isSameType(funcType.returnType)) return false;
        if (params.size() != funcType.params.size()) return false;
        for (int i = 0; i < params.size(); i++){
            if (!params.get(i).isSameType(funcType.params.get(i))) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("funcType: <returnType=");
        sb.append(returnType.toString());
        sb.append("> <paramsType=");
        for (Type type : params){
            sb.append(" ").append(type.toString());
        }
        sb.append(">");
        return sb.toString();
    }

    @Override
    public int getType() {
        return FUNC_TYPE;
    }
}
