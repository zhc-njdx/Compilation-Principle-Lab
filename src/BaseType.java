public class BaseType implements Type{
    public String name;

    public BaseType(String name){
        this.name = name;
    }
    @Override
    public boolean isSameType(Type type) {
        if (type.getType() != BASE_TYPE) return false;
        BaseType baseType = (BaseType) type;
        return name.equals(baseType.name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getType() {
        return BASE_TYPE;
    }
}
