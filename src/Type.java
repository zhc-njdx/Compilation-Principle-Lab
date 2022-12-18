public interface Type {

    static final int BASE_TYPE = 0;
    static final int FUNC_TYPE = 1;
    static final int ARRAY_TYPE = 2;

    boolean isSameType(Type type);

    int getType();
}
