package samples.enums.methods;

public enum Op {
    ADD, SUB;

    @Deprecated
    public int apply(int a, int b) { return a + b; }

    @Deprecated
    public static Op parse(String s) { return ADD; }
}