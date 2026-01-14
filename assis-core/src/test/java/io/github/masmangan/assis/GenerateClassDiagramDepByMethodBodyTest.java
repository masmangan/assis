/*
 * Copyright (c) 2025-2026, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import static io.github.masmangan.assis.TestWorkbench.assertAnyLineContainsAll;
import static io.github.masmangan.assis.TestWorkbench.assertPumlContainsName;
import static io.github.masmangan.assis.TestWorkbench.generatePumlFromSample;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GenerateClassDiagramDepByMethodBodyTest {

	@TempDir
	Path tempDir;

	@Test
	void generatesEnumConstantsInDeclarationOrder() throws Exception {
		String puml = generatePumlFromSample("samples/deps/bylocal", tempDir, "bylocal");

		assertPumlContainsName(puml, "A");
		assertPumlContainsName(puml, "B");
		
		assertAnyLineContainsAll(puml, "B", "A", "..>");
	}

}