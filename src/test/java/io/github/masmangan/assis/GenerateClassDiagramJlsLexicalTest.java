/*
 * Copyright (c) 2025, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static io.github.masmangan.assis.TestWorkbench.assertPumlContains;

@Disabled("Incomplete test – to be finished in 0.5.0")
class GenerateClassDiagramJlsLexicalTest {
    @TempDir
    Path tempDir;

    @Test
    void generatesDiagramContainingLexical() throws Exception {
        String puml = TestWorkbench.generatePumlFromSample(
                "jls/lexical",
                tempDir,
                "lexical");

        assertPumlContains(puml, "package testPackage");
        assertPumlContains(puml, "package other");

        assertPumlContains(puml, "class \"testPackage.Test\"");
        assertPumlContains(puml, "class \"testPackage.Other\"");
        assertPumlContains(puml, "class \"other.Other\"");

    }
}
