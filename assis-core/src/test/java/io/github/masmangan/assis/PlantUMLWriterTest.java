package io.github.masmangan.assis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

class PlantUMLWriterTest {

	@Test
	void testPlantUMLWriter() {
		StringWriter sw = new StringWriter();

		try (PlantUMLWriter w = new PlantUMLWriter(new PrintWriter(sw))) {
			// no-op
		}

		assertEquals("", sw.toString());
	}

	@Test
	void beginAndEndDiagramProducesMinimalBlock() throws Exception {
		StringWriter sw = new StringWriter();

		try (PlantUMLWriter w = new PlantUMLWriter(new PrintWriter(sw))) {
			w.beginDiagram("D");
			w.endDiagram("D");
		}

		String expected = """
				@startuml "D"
				@enduml
				""";

		assertEquals(expected, sw.toString());
	}

	@Test
	void emptyPackageInsideDiagramIsProperlyClosed() throws Exception {
		StringWriter sw = new StringWriter();

		try (PlantUMLWriter w = new PlantUMLWriter(new PrintWriter(sw))) {
			w.beginDiagram("D");
			w.beginPackage("P");
			w.endPackage("P");
			w.endDiagram("D");
		}

		String expected = """
				@startuml "D"
				package "P" { /' @assis:begin package "P" '/
				} /' @assis:end package "P" '/
				@enduml
				""";

		assertEquals(expected, sw.toString());
	}

	@Test
	void emptyClassInsideDiagramIsProperlyClosed() throws Exception {
		StringWriter sw = new StringWriter();

		try (PlantUMLWriter w = new PlantUMLWriter(new PrintWriter(sw))) {
			w.beginDiagram("D");
			w.beginClass("A", "");
			w.endClass("A");
			w.endDiagram("D");
		}

		String expected = """
				@startuml "D"
				class "A" { /' @assis:begin class "A" '/
				} /' @assis:end class "A" '/
				@enduml
				""";

		assertEquals(expected, sw.toString());
	}

	@Test
	void emptyClassInsidePackageGetsIndentation() throws Exception {
		StringWriter sw = new StringWriter();

		try (PlantUMLWriter w = new PlantUMLWriter(new PrintWriter(sw))) {
			w.beginDiagram("D");
			w.beginPackage("P");
			w.beginClass("A", "");
			w.endClass("A");
			w.endPackage("P");
			w.endDiagram("D");
		}

		String expected = """
				@startuml "D"
				package "P" { /' @assis:begin package "P" '/
				  class "A" { /' @assis:begin class "A" '/
				  } /' @assis:end class "A" '/
				} /' @assis:end package "P" '/
				@enduml
				""";

		assertEquals(expected, sw.toString());
	}

	@Test
	void associationWithRole() throws Exception {
	    StringWriter sw = new StringWriter();

	    try (PlantUMLWriter w = new PlantUMLWriter(new PrintWriter(sw))) {
	        w.connectAssociation("A", "B", "r", "");
	    }

	    String expected = """
	        "A" --> "r" "B"
	        """;

	    assertEquals(expected, sw.toString());
	}
	
	@Test
	void associationWithRoleAndStereotype() throws Exception {
	    StringWriter sw = new StringWriter();

	    try (PlantUMLWriter w = new PlantUMLWriter(new PrintWriter(sw))) {
	        w.connectAssociation("A", "B", "r", "<<OneToMany>>");
	    }

	    String expected = """
	        "A" --> "r" "B" : <<OneToMany>>
	        """;

	    assertEquals(expected, sw.toString());
	}
	
}
