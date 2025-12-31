/*
 * Copyright (c) 2025, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import java.io.PrintWriter;

/**
 * 
 */
public class PlantUMLWriter implements AutoCloseable {

	/**
	 * 
	 */
	private final PrintWriter out;
	
	/**
	 * 
	 */
	private int indent = 0;

	/**
	 * 
	 * @param out
	 */
	public PlantUMLWriter(PrintWriter out) {
		this.out = out;
	}

	/**
	 * 
	 * @param line
	 */
	public void println(String line) {
		out.println("  ".repeat(indent) + line);
	}

	/**
	 * 
	 */
	public void println() {
		println("");
	}

	/**
	 * 
	 * @param text
	 */
	public void print(String text) {
		out.print(text);
	}

	/**
	 * 
	 */
	public void indent() {
		indent++;
	}

	/**
	 * 
	 */
	public void dedent() {
		if (indent > 0)
			indent--;
	}

	/**
	 * 
	 */
	public void flush() {
		out.flush();
	}

	@Override
	public void close() {
		out.close();
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private static String q(String name) {
		return "\"" + name + "\"";
	}

	/**
	 * 
	 * @param stereotypes
	 * @return
	 */
	private static String st(String stereotypes) {
		if (stereotypes == null)
			return "";
		String s = stereotypes.trim();
		return s.isEmpty() ? "" : " " + s;
	}

	/**
	 * 
	 * @param name
	 */
	public void beginPackage(String name) {
		println("package " + q(name) + " {");
		indent();
	}

	/**
	 * 
	 */
	public void endPackage() {
		endType();
	}

	/**
	 * 
	 * @param name
	 * @param stereotypes
	 */
	public void beginClass(String name, String stereotypes) {
		println("class " + q(name) + st(stereotypes) + " {");
		indent();
	}

	/**
	 * 
	 * @param name
	 * @param stereotypes
	 */
	public void beginAbstractClass(String name, String stereotypes) {
		println("abstract class " + q(name) + st(stereotypes) + " {");
		indent();
	}

	/**
	 * 
	 * @param name
	 * @param stereotypes
	 */
	public void beginInterface(String name, String stereotypes) {
		println("interface " + q(name) + st(stereotypes) + " {");
		indent();
	}

	/**
	 * 
	 * @param name
	 * @param stereotypes
	 */
	public void beginRecord(String name, String stereotypes) {
		println("record " + q(name) + st(stereotypes) + " {");
		indent();
	}

	/**
	 * 
	 * @param name
	 * @param stereotypes
	 */
	public void beginEnum(String name, String stereotypes) {
		println("enum " + q(name) + st(stereotypes) + " {");
		indent();
	}

	/**
	 * 
	 * @param name
	 * @param stereotypes
	 */
	public void beginAnnotation(String name, String stereotypes) {
		println("annotation " + q(name) + st(stereotypes) + " {");
		indent();
	}

	/**
	 * 
	 */
	public void endType() {
		dedent();
		println("}");
	}
}