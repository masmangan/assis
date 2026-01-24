/*
 * Copyright (c) 2025-2026, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import static io.github.masmangan.assis.TestWorkbench.assertAnyLineContainsAll;
import static io.github.masmangan.assis.TestWorkbench.assertPumlContains;
import static io.github.masmangan.assis.TestWorkbench.assertPumlContainsClass;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GenerateClassDiagramInheritanceSampleTest {
	@TempDir
	Path tempDir;

	@Test
	void generatesDiagramContainingInheritance() throws Exception {
		String puml = TestWorkbench.generatePumlFromSample("samples/inheritance", tempDir, "inheritance");

		// types
		assertPumlContains(puml, "interface \"pa.A\"");
		assertPumlContainsClass(puml, "pa.Base");
		assertPumlContains(puml, "interface \"pb.B\"");
		assertPumlContainsClass(puml, "pb.Child");

		assertPumlContains(puml, "enum \"pb.E\"");

		// relationships
		assertPumlContains(puml, "\"pb.B\" --|> \"pa.A\"");
		assertPumlContains(puml, "\"pb.Child\" --|> \"pa.Base\"");
		assertPumlContains(puml, "\"pb.Child\" ..|> \"pb.B\"");

		assertAnyLineContainsAll(puml, "pb.E", "..|>", "pa.A");
	}
}
