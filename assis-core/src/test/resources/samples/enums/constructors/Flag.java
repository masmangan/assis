package samples.enums.constructors;

public enum Flag {
 A(1), B(2);

 private final int code;

 @Deprecated
 Flag(int code) { this.code = code; }
}