/*
 * Copyright (c) 2025-2026, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import static io.github.masmangan.assis.TestWorkbench.assertAnyLineContainsAll;
import static io.github.masmangan.assis.TestWorkbench.assertPumlContainsClass;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GenerateClassDiagramTypeRefsSampleTest {
	@TempDir
	Path tempDir;

	@Test
	void generatesDiagramTypeRefs() throws Exception {
		String puml = TestWorkbench.generatePumlFromSample("samples/typerefs", tempDir, "typerefs");

		// types
		assertPumlContainsClass(puml, "A");
		assertAnyLineContainsAll(puml, "interface \"B\"");
		assertPumlContainsClass(puml, "C");
		assertPumlContainsClass(puml, "D");
		assertPumlContainsClass(puml, "E");
		assertPumlContainsClass(puml, "F");

		assertAnyLineContainsAll(puml, "A", "..|>", "Serializable");

		assertAnyLineContainsAll(puml, "ghost", "B", "--|>", "BB");
		assertAnyLineContainsAll(puml, "C", "--|>", "JPanel");
		assertAnyLineContainsAll(puml, "ghost", "D", "--|>", "DD");
		assertAnyLineContainsAll(puml, "E", "--|>", "A");

		assertAnyLineContainsAll(puml, "F", "..|>", "B");
	}

}
