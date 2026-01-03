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
 * <p>
 * Output is line-based: each call to {@link #println(String)} writes a single
 * line. This class also provides helper methods to emit common block constructs
 * such as packages and type declarations.
 *
 * <p>
 * Blocks started with {@code begin*} methods must be closed with a
 * corresponding {@code end*} method. While inside a package or type block,
 * subsequent lines are indented by two spaces per indentation level.
 *
 * <p>
 * This class performs no validation of PlantUML syntax and does not attempt to
 * enforce balanced blocks.
 *
 * <p>
 * Closing this writer flushes its output but does not close the underlying
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
	public PlantUMLWriter(final PrintWriter out) {
		this.out = Objects.requireNonNull(out, "out");
	}

	/**
	 * Writes a line, prefixed with the current indentation.
	 *
	 * @param line the line to write (without a line terminator); if {@code null},
	 *             the string {@code "null"} is written
	 */
	public void println(final String line) {
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
	 * Decreases the indentation level for subsequently written lines. If the
	 * indentation level is already zero, this method has no effect.
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
	 * <p>
	 * This method does not close the underlying {@code PrintWriter}.
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
	public void beginDiagram(final String name) {
		Objects.requireNonNull(name, "name");
		println("@startuml " + name);
	}

	/**
	 * Ends the current diagram.
	 *
	 * <p>
	 * The caller is responsible for matching each {@link #beginDiagram(String)}
	 * with exactly one call to this method.
	 * 
	 * @param name package name to quote in the emitted statement; must not be
	 *             {@code null}
	 */
	public void endDiagram(final String name) {
		Objects.requireNonNull(name, "name");
		println("@enduml");
	}

	/**
	 * Begins a PlantUML {@code package} block and increases indentation.
	 *
	 * @param name package name to quote in the emitted statement; must not be
	 *             {@code null}
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginPackage(final String name) {
		Objects.requireNonNull(name, "name");
		println("package " + quote(name) + " {");
		indent();
	}

	/**
	 * Ends the current package block.
	 *
	 * <p>
	 * This method decreases indentation by one level (if greater than zero) and
	 * emits {@code }}. The caller is responsible for matching each
	 * {@link #beginPackage(String)} with exactly one call to this method.
	 * 	
	 * @param name package name to quote in the emitted statement; must not be
	 *             {@code null}
	 */
	public void endPackage(final String name) {
		Objects.requireNonNull(name, "name");
		dedent();
		println("} /' @assis:end package " + quote(name) + " '/");
	}

	/**
	 * Begins a PlantUML {@code class} block and increases indentation.
	 *
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 * @param stereotypes optional stereotypes (e.g., {@code <<Entity>>}); may be
	 *                    {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginClass(final String name, final String stereotypes) {
		beginType("class", name, stereotypes);
	}

	/**
	 * Ends the current class block.
	 * <p>
	 * This method decreases indentation by one level (if greater than zero) and
	 * emits {@code }}. The caller is responsible for matching each
	 * {@link #beginClass(String)} with exactly one call to this method.
	 * 
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 */
	public void endClass(final String name) {
		Objects.requireNonNull(name, "name");
		dedent();
		println("} /' @assis:end class " + quote(name) + " '/");
	}
	
	/**
	 * Begins a PlantUML {@code abstract class} block and increases indentation.
	 *
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 * @param stereotypes optional stereotypes (e.g., {@code <<Entity>>}); may be
	 *                    {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginAbstractClass(final String name, final String stereotypes) {
		beginType("abstract class", name, stereotypes);
	}

	/**
	 * Ends the current abstract class block.
	 * <p>
	 * This method decreases indentation by one level (if greater than zero) and
	 * emits {@code }}. The caller is responsible for matching each
	 * {@link #beginAbstractClass(String)} with exactly one call to this method.
	 * 
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 */
	public void endAbstractClass(final String name) {
		endType("class", name);
	}	
	
	/**
	 * Begins a PlantUML {@code interface} block and increases indentation.
	 *
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 * @param stereotypes optional stereotypes (e.g.,
	 *                    {@code <<FunctionalInterface>>}); may be {@code null} or
	 *                    blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginInterface(final String name, final String stereotypes) {
		beginType("interface", name, stereotypes);
	}

	/**
	 * Ends the current abstract class block.
	 * <p>
	 * This method decreases indentation by one level (if greater than zero) and
	 * emits {@code }}. The caller is responsible for matching each
	 * {@link #beginInterface(String)} with exactly one call to this method.
	 * 
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 */
	public void endInterface(final String name) {
		endType("interface", name);
	}	
	
	/**
	 * Begins a PlantUML {@code record} block and increases indentation.
	 *
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 * @param stereotypes optional stereotypes; may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginRecord(final String name, final String stereotypes) {
		beginType("record", name, stereotypes);
	}

	public void endRecord(final String name) {
		endType("record", name);
	}	
	
	/**
	 * Begins a PlantUML {@code enum} block and increases indentation.
	 *
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 * @param stereotypes optional stereotypes; may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginEnum(final String name, final String stereotypes) {
		beginType("enum", name, stereotypes);
	}

	public void endEnum(final String name) {
		endType("enum", name);
	}	
	
	/**
	 * Begins a PlantUML {@code annotation} block and increases indentation.
	 *
	 * @param name        type name to quote in the emitted statement; must not be
	 *                    {@code null}
	 * @param stereotypes optional stereotypes; may be {@code null} or blank
	 * @throws NullPointerException if {@code name} is {@code null}
	 */
	public void beginAnnotation(final String name, final String stereotypes) {
		beginType("annotation", name, stereotypes);
	}

	public void endAnnotation(final String name) {
		endType("interface", name);
	}	
	
	/**
	 * Ends the current type block started by a {@code begin*} type method.
	 *
	 * <p>
	 * This method decreases indentation by one level (if greater than zero) and
	 * emits {@code }}. The caller is responsible for balancing begin/end calls.
	 */
	private void endType(final String keyword, final String name) {
		endBlock(keyword, name);
	}

	private void beginType(final String keyword, final String name, final String stereotypes) {
		Objects.requireNonNull(keyword, "keyword");
		Objects.requireNonNull(name, "name");
		println(keyword + " " + quote(name) + stereotypesSuffix(stereotypes) + " {");
		indent();
	}

	private void endBlock(final String keyword, final String name) {
		dedent();
		println("} /' @assis:end " + keyword + " " + quote(name) + "'/");
	}

	private static String quote(final String name) {
		return "\"" + name + "\"";
	}

	private static String stereotypesSuffix(final String stereotypes) {
		if (stereotypes == null) {
			return "";
		}
		final String s = stereotypes.trim();
		return s.isEmpty() ? "" : " " + s;
	}

}