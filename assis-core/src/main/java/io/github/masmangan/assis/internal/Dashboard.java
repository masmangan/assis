/*
 * Copyright (c) 2025-2026, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.CompilationUnit;

/**
 *
 * @since 0.9.5
 * @author Marco Mangan
 */
public final class Dashboard implements PropertyChangeListener {

	private static final Dashboard INSTANCE = new Dashboard();

	private AtomicInteger fileCount = new AtomicInteger();
	private AtomicInteger unitCount = new AtomicInteger();
	private AtomicInteger typeCount = new AtomicInteger();

	private Dashboard() {
	}

	public static Dashboard getDashboard() {
		return INSTANCE;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case "newPath" -> {
			Path path = (Path) evt.getNewValue();
			int n = fileCount.incrementAndGet();
			System.out.println("Discovered source file #" + n + ": " + path.toString());
		}
		case "newUnit" -> {
			CompilationUnit unit = (CompilationUnit) evt.getNewValue();
			int n = unitCount.incrementAndGet();
			String pkg = unit.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("DEFAULT_PACKAGE");

			System.out.println("Discovered unit #" + n + ": " + pkg);
		}
		case "newType" -> {
			TypeKey key = (TypeKey) evt.getNewValue();
			int n = typeCount.incrementAndGet();
			System.out.println("Discovered type #" + n + ": " + key.text());
		}
		default -> {
			System.out.println("Unknow event #" + evt);

		}
		}
	}

	@Override
	public String toString() {
		return String.format("Dashboard [files=%s, units=%s, types=%s]", fileCount, unitCount, typeCount);
	}

}
