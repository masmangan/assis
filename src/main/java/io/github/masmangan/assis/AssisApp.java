package io.github.masmangan.assis;

/**
 * Class AssisApp is the generator entry point.
 */
public class AssisApp {
    /**
     * Generates PlantUML class diagrams from Java source code.
     * 
     * @param args not used
     * @throws Exception error reading source file or writing diagrams
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Assis, from Java to UML!");
        GenerateClassDiagram.generate();
    }
}
