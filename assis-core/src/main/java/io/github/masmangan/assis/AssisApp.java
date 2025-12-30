/*
 * Copyright (c) 2025, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The {@code AssisApp} class is the PlantUML diagram generator entry point.
 */
public final class AssisApp {
	/**
	 * Default documentation path. We are borrowing from PlantUML folder convention.
	 */
	private static final String DOCS_UML_CLASS_DIAGRAM_PUML = "docs/diagrams/src/class-diagram.puml";

	/**
	 * Default source path. We are borrowing from Maven folder convention.
	 */
	private static final String SRC_MAIN_JAVA = "src/main/java";
	
    /**
     * No constructor available.
     */
    private AssisApp() {
    }

    /**
     * Generates PlantUML class diagrams from Java source code.
     * 
     * @param args not used
     * @throws Exception error reading source file or writing diagrams
     */
    public static void main(String[] args) throws Exception {
		Path src = Paths.get(SRC_MAIN_JAVA);
		Path out = Paths.get(DOCS_UML_CLASS_DIAGRAM_PUML);
		Files.createDirectories(out.getParent());
        GenerateClassDiagram.generate(src, out);
    }
}
