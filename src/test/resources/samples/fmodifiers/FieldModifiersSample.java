package samples.fmodifiers;

public class FieldModifiersSample {

    // common modifiers
    private static int counter;
    public static final String CONST = "X";
    private final int id;

    // Java keywords that matter structurally
    private transient String cache;
    private volatile boolean dirty;

}
