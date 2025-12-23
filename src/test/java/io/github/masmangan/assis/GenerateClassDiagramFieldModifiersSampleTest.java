package io.github.masmangan.assis;

import static io.github.masmangan.assis.TestWorkbench.assertPumlContains;
import static io.github.masmangan.assis.TestWorkbench.assertPumlContainsClass;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GenerateClassDiagramFieldModifiersSampleTest {
    @TempDir
    Path tempDir;

    @Test
    void generatesDiagramContainingFieldModifiers() throws Exception {
        String puml = TestWorkbench.generatePumlFromSample(
                "samples/fmodifiers",
                tempDir,
                "fmodifiers");

        assertPumlContainsClass(puml, "samples.fmodifiers.FieldModifiersSample");
        assertPumlContains(puml, "counter");
                assertPumlContains(puml, "int");

        assertPumlContains(puml, "{static}");

        assertPumlContains(puml, "CONST");
                        assertPumlContains(puml, "String");

        assertPumlContains(puml, "{final}");

        assertPumlContains(puml, "id");
        assertPumlContains(puml, "{static}");

        assertPumlContains(puml, "cache");
         assertPumlContains(puml, "{final}");

        assertPumlContains(puml, "id");
        assertPumlContains(puml, "{static}");       
    }
}
