/*
 * Copyright (c) 2025, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * 
 */
class CollectRelationshipsVisitor {
	private final DeclaredIndex idx;

	CollectRelationshipsVisitor(DeclaredIndex idx) {
		this.idx = idx;
	}

	void emitAll(PlantUMLWriter pw) {
		for (var entry : idx.fqnsByPkg.entrySet()) {
			String pkg = entry.getKey();
			for (String fqn : entry.getValue()) {
				TypeDeclaration<?> td = idx.byFqn.get(fqn);
				emitExtendsImplements(pw, pkg, fqn, td);
			}
		}

		for (String fqn : idx.byFqn.keySet()) {
			String ownerFqn = ownerFqnOf(fqn);
			if (ownerFqn != null && idx.byFqn.containsKey(ownerFqn)) {
				pw.println(idx.qPuml(ownerFqn) + " +-- " + idx.qPuml(fqn));
			}
		}

		for (var entry : idx.fqnsByPkg.entrySet()) {
			String pkg = entry.getKey();
			for (String fqn : entry.getValue()) {
				TypeDeclaration<?> td = idx.byFqn.get(fqn);
				emitAssociations(pw, pkg, fqn, td);
			}
		}
	}

	private void emitExtendsImplements(PlantUMLWriter pw, String pkg, String subFqn, TypeDeclaration<?> td) {
		if (td instanceof ClassOrInterfaceDeclaration cid) {
			for (ClassOrInterfaceType ext : cid.getExtendedTypes()) {
				String raw = GenerateClassDiagram.simpleName(ext.getNameWithScope());
				String target = idx.resolveTypeName(pkg, raw);
				if (target != null)
					pw.println(idx.qPuml(target) + " <|-- " + idx.qPuml(subFqn));
			}
			for (ClassOrInterfaceType impl : cid.getImplementedTypes()) {
				String raw = GenerateClassDiagram.simpleName(impl.getNameWithScope());
				String target = idx.resolveTypeName(pkg, raw);
				if (target != null)
					pw.println(idx.qPuml(target) + " <|.. " + idx.qPuml(subFqn));
			}
		} else if (td instanceof EnumDeclaration ed) {
			for (ClassOrInterfaceType impl : ed.getImplementedTypes()) {
				String raw = GenerateClassDiagram.simpleName(impl.getNameWithScope());
				String target = idx.resolveTypeName(pkg, raw);
				if (target != null)
					pw.println(idx.qPuml(target) + " <|.. " + idx.qPuml(subFqn));
			}
		}
	}

	private void emitAssociations(PlantUMLWriter pw, String pkg, String ownerFqn, TypeDeclaration<?> td) {
		if (td instanceof ClassOrInterfaceDeclaration cid) {
			for (FieldDeclaration fd : cid.getFields()) {
				for (VariableDeclarator vd : fd.getVariables()) {
					String assoc = assocTypeFrom(pkg, ownerFqn, vd);
					if (assoc != null) {
						pw.println(idx.qPuml(ownerFqn) + " --> " + idx.qPuml(assoc) + " : " + vd.getNameAsString());
					}
				}
			}
		} else if (td instanceof EnumDeclaration ed) {
			for (FieldDeclaration fd : ed.getFields()) {
				for (VariableDeclarator vd : fd.getVariables()) {
					String assoc = assocTypeFrom(pkg, ownerFqn, vd);
					if (assoc != null) {
						pw.println(idx.qPuml(ownerFqn) + " --> " + idx.qPuml(assoc) + " : " + vd.getNameAsString());
					}
				}
			}
		} else if (td instanceof RecordDeclaration rd) {
			for (Parameter p : rd.getParameters()) {
				String raw = p.getType().asString().replaceAll("<.*>", "").replace("[]", "").trim();
				String target = idx.resolveTypeName(pkg, raw);
				if (target != null && !target.equals(ownerFqn)) {
					pw.println(idx.qPuml(ownerFqn) + " --> " + idx.qPuml(target) + " : " + p.getNameAsString()
							+ GenerateClassDiagram.renderStereotypes(GenerateClassDiagram.stereotypesOf(p)));
				}
			}
		}
	}

	private String assocTypeFrom(String pkg, String ownerFqn, VariableDeclarator vd) {
		String raw = vd.getType().asString().replaceAll("<.*>", "").replace("[]", "").trim();
		String resolved = idx.resolveTypeName(pkg, raw);
		if (resolved == null)
			return null;
		if (resolved.equals(ownerFqn))
			return null;
		return resolved;
	}

	private static String ownerFqnOf(String fqn) {
		int lastDot = fqn.lastIndexOf('.');
		if (lastDot < 0)
			return null;
		return fqn.substring(0, lastDot);
	}
}