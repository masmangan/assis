/*
 * Copyright (c) 2025-2026, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import java.io.PrintWriter;
import java.util.Objects;

/**
 * A writer that emits PlantUML statements to a {@link PrintWriter}.
 *
 * <p>Output is line-based: each call to {@link #println(String)} writes a single line.
 * This class also provides helper methods to emit common block constructs such as
 * packages and type declarations.
 *
 * <p>Blocks started with {@code begin*} methods must be closed with a corresponding
 * {@code end*} method. While inside a package or type block, subsequent lines are
 * indented by two spaces per indentation level.
 *
 * <p>This class performs no validation of PlantUML syntax and does not attempt to
 * enforce balanced blocks.
 *
 * <p>Closing this writer flushes its output but does not close the underlying
 * {@code PrintWriter}, since it is not owned by this instance.
 *
 * @since 0.9.1
 * @author Marco Mangan
 */
public final class PlantUMLWriter implements AutoCloseable {

	private static final String INDENT_UNIT = "  ";

	private final PrintWriter out;

	private int indentLevel;

	/**
	 * Creates a new writer that sends its output to the given {@code PrintWriter}.
	 *
	 * @param out destination writer; must not be {@code null}
	 * @throws NullPointerException if {@code out} is {@code null}
	 */
	public PlantUMLWriter(PrintWriter out) {
		this.out = Objects.requireNonNull(out, "out");
	}

	/**
	 * Writes a line, prefixed with the current indentation.
	 *
	 * @param line the line to write (without a line terminator); if {@code null},
	 *             the string {@code "null"} is written
	 */
	public void println(String line) {
		out.println(INDENT_UNIT.repeat(indentLevel) + String.valueOf(line));
	}

	/**
	 * Writes an empty line.
	 */
	public void println() {
		println("");
	}

	/**
	 * Increases the indentation level for subsequently written lines.
	 */
	public void indent() {
		indentLevel++;
	}

	/**
	 * Decreases the indentation level for subsequently written lines.
	 * If the indentation level is already zero, this method has no effect.
	 */
	public void dedent() {
		if (indentLevel > 0) {
			indentLevel--;
		}
	}

	/**
	 * Flushes the underlying {@code PrintWriter}.
	 */
	public void flush() {
		out.flush();
	}

	/**
	 * Flushes this writer.
	 *
	 * <p>This method does not close the underlying {@code PrintWriter}.
	 */
	@Override
	public void close() {
		flush();
	}

	/**
	 * Begins a PlantUML diagram.
	 *
	 * @param name diagram name in the emitted statement; must not be {@code null}
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginDiagram(String name) {
		Objects.requireNonNull(name, "name");
		println("@startuml " + name);		
	}
	
	/**
	 * Ends the current diagram.
	 *
	 * <p>The caller is responsible for matching each
	 * {@link #beginDiagram(String)} with exactly one call to this method.
	 */
	public void endDiagram() {
		println("@enduml");
	}
	
	/**
	 * Begins a PlantUML {@code package} block and increases indentation.
	 *
	 * @param name package name to quote in the emitted statement; must not be {@code null}
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginPackage(String name) {
		Objects.requireNonNull(name, "name");
		println("package " + quote(name) + " {");
		indent();
	}

	/**
	 * Ends the current package block.
	 *
	 * <p>This method decreases indentation by one level (if greater than zero) and
	 * emits {@code }}. The caller is responsible for matching each
	 * {@link #beginPackage(String)} with exactly one call to this method.
	 */
	public void endPackage() {
		endBlock();
	}

	/**
	 * Begins a PlantUML {@code class} block and increases indentation.
	 *
	 * @param name type name to quote in the emitted statement; must not be {@code null}
	 * @param stereotypes optional stereotypes (e.g., {@code <<Entity>>}); may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginClass(String name, String stereotypes) {
		beginType("class", name, stereotypes);
	}

	/**
	 * Begins a PlantUML {@code abstract class} block and increases indentation.
	 *
	 * @param name type name to quote in the emitted statement; must not be {@code null}
	 * @param stereotypes optional stereotypes (e.g., {@code <<Entity>>}); may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginAbstractClass(String name, String stereotypes) {
		beginType("abstract class", name, stereotypes);
	}

	/**
	 * Begins a PlantUML {@code interface} block and increases indentation.
	 *
	 * @param name type name to quote in the emitted statement; must not be {@code null}
	 * @param stereotypes optional stereotypes (e.g., {@code <<FunctionalInterface>>}); may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginInterface(String name, String stereotypes) {
		beginType("interface", name, stereotypes);
	}

	/**
	 * Begins a PlantUML {@code record} block and increases indentation.
	 *
	 * @param name type name to quote in the emitted statement; must not be {@code null}
	 * @param stereotypes optional stereotypes; may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginRecord(String name, String stereotypes) {
		beginType("record", name, stereotypes);
	}

	/**
	 * Begins a PlantUML {@code enum} block and increases indentation.
	 *
	 * @param name type name to quote in the emitted statement; must not be {@code null}
	 * @param stereotypes optional stereotypes; may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginEnum(String name, String stereotypes) {
		beginType("enum", name, stereotypes);
	}

	/**
	 * Begins a PlantUML {@code annotation} block and increases indentation.
	 *
	 * @param name type name to quote in the emitted statement; must not be {@code null}
	 * @param stereotypes optional stereotypes; may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginAnnotation(String name, String stereotypes) {
		beginType("annotation", name, stereotypes);
	}
	
	/**
	 * Ends the current type block started by a {@code begin*} type method.
	 *
	 * <p>This method decreases indentation by one level (if greater than zero) and
	 * emits {@code }}. The caller is responsible for balancing begin/end calls.
	 */
	public void endType() {
		endBlock();
	}

	private void beginType(String keyword, String name, String stereotypes) {
		Objects.requireNonNull(keyword, "keyword");
		Objects.requireNonNull(name, "name");
		println(keyword + " " + quote(name) + stereotypesSuffix(stereotypes) + " {");
		indent();
	}

	private void endBlock() {
		dedent();
		println("}");
	}

	private static String quote(String name) {
		return "\"" + name + "\"";
	}

	private static String stereotypesSuffix(String stereotypes) {
		if (stereotypes == null) {
			return "";
		}
		String s = stereotypes.trim();
		return s.isEmpty() ? "" : " " + s;
	}



}