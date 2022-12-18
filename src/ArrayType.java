public class ArrayType implements Type{
    public Type eleType; // 元素类型
    public int dimen; // 维度

    @Override
    public boolean isSameType(Type type) {
        if (type.getType() != ARRAY_TYPE) return false;
        ArrayType arrayType = (ArrayType) type;
        if (!eleType.isSameType(arrayType.eleType)) return false;
        if (dimen != arrayType.dimen) return false;
        return true;
    }

    @Override
    public String toString() {
        return eleType.toString() + "-" + dimen + "D";
    }

    @Override
    public int getType() {
        return ARRAY_TYPE;
    }
}
