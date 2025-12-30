package samples.enums.fields;

public enum Color {
    RED(0xFF0000), BLUE(0x0000FF);

    @Deprecated
    private final int rgb;

    Color(int rgb) { this.rgb = rgb; }
}