/*
 * Copyright (c) 2025, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

/**
 * The {@code AssisApp} class is the PlantUML diagram generator entry point.
 */
public final class AssisApp {

    /**
     * 
     */
    private AssisApp() {
    }

    /**
     * Extracts package version information.
     * 
     * @return
     */
    private static String versionOrDev() {
        String v = AssisApp.class.getPackage().getImplementationVersion();
        return (v == null || v.isBlank()) ? "dev" : v;
    }

    /**
     * Generates PlantUML class diagrams from Java source code.
     * 
     * @param args not used
     * @throws Exception error reading source file or writing diagrams
     */
    public static void main(String[] args) throws Exception {
        System.out.println("ASSIS " + versionOrDev() + " (Java -> UML)");

        GenerateClassDiagram.generate();
    }
}
