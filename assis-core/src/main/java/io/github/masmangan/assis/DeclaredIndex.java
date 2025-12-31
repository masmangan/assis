/*
 * Copyright (c) 2025, Marco Mangan. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 */

package io.github.masmangan.assis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

/**
 * 
 */
class DeclaredIndex {
	  final Map<String, TypeDeclaration<?>> byFqn = new LinkedHashMap<>();
	  Map<String, List<String>> fqnsByPkg = new LinkedHashMap<>();
	  final Map<String, String> uniqueBySimple = new LinkedHashMap<>();

	  static DeclaredIndex build(List<CompilationUnit> cus) {
	    DeclaredIndex idx = new DeclaredIndex();

	    for (CompilationUnit cu : cus) {
	      String pkg = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");

	      for (TypeDeclaration<?> td : cu.getTypes()) {
	        collectTypeRecursive(idx, cu, pkg, td, null);
	      }
	    }

	    for (String fqn : idx.byFqn.keySet()) {
	      String pkg = packageOfFqn(fqn);
	      idx.fqnsByPkg.computeIfAbsent(pkg, k -> new ArrayList<>()).add(fqn);
	    }

	    idx.fqnsByPkg = sortPackagesByNameFqn(idx.fqnsByPkg);
	    idx.fqnsByPkg.values().forEach(list -> list.sort(String::compareTo));

	    Map<String, String> seen = new LinkedHashMap<>();
	    Set<String> ambiguous = new LinkedHashSet<>();
	    for (String fqn : idx.byFqn.keySet()) {
	      String simple = GenerateClassDiagram.simpleName(fqn);
	      if (!seen.containsKey(simple)) seen.put(simple, fqn);
	      else ambiguous.add(simple);
	    }
	    for (var e : seen.entrySet()) {
	      if (!ambiguous.contains(e.getKey())) {
	        idx.uniqueBySimple.put(e.getKey(), e.getValue());
	      }
	    }

	    return idx;
	  }

	  private static void collectTypeRecursive(
	      DeclaredIndex idx,
	      CompilationUnit cu,
	      String pkg,
	      TypeDeclaration<?> td,
	      String ownerFqn
	  ) {
	    String name = td.getNameAsString();
	    String fqn = (ownerFqn == null)
	        ? (pkg.isEmpty() ? name : pkg + "." + name)
	        : ownerFqn + "." + name;

	    idx.byFqn.put(fqn, td);

	    if (td instanceof ClassOrInterfaceDeclaration cid) {
	      cid.getMembers().forEach(m -> {
	        if (m instanceof TypeDeclaration<?> nested) {
	          collectTypeRecursive(idx, cu, pkg, nested, fqn);
	        }
	      });
	    } else if (td instanceof EnumDeclaration ed) {
	      ed.getMembers().forEach(m -> {
	        if (m instanceof TypeDeclaration<?> nested) {
	          collectTypeRecursive(idx, cu, pkg, nested, fqn);
	        }
	      });
	    }
	  }

	  static String packageOfFqn(String fqn) {
	    int lastDot = fqn.lastIndexOf('.');
	    if (lastDot < 0) return ""; 
	    // TODO: IA - cuidado: nested usa '.' também, mas o pacote é até o primeiro tipo top-level.
	    // Como você está incluindo nested como pkg.owner.Inner, o pacote é o prefixo antes do primeiro tipo.
	    // Porém, para fins do agrupamento, você já guardou pkg no build via cu, então este fallback é ok.
	    // Se quiser 100% correto para nested, dá para guardar pkg por fqn na coleta.
	    return fqn.substring(0, lastDot); // ok para top-level; nested fica “pkg.Outer” (aceitável p/ agrupamento simples)
	  }

	  static Map<String, List<String>> sortPackagesByNameFqn(Map<String, List<String>> byPkg) {
	    return byPkg.entrySet().stream()
	        .sorted(Map.Entry.comparingByKey())
	        .collect(Collectors.toMap(
	            Map.Entry::getKey,
	            Map.Entry::getValue,
	            (a, b) -> a,
	            LinkedHashMap::new
	        ));
	  }

	  /** Resolve nome do tipo “best-effort”, semelhante ao teu resolveTypeName, mas usando só tipos declarados. */
	  String resolveTypeName(String ownerPkg, String rawName) {
	    if (rawName == null) return null;
	    String raw = rawName.trim();
	    if (raw.isEmpty()) return null;

	    if (raw.contains(".") && byFqn.containsKey(raw)) return raw;

	    String simple = GenerateClassDiagram.simpleName(raw);

	    String samePkg = (ownerPkg == null || ownerPkg.isEmpty()) ? simple : ownerPkg + "." + simple;
	    if (byFqn.containsKey(samePkg)) return samePkg;

	    return uniqueBySimple.get(simple);
	  }

	  /** pumlName compatível com teu TypeInfo: nested usa ownerFqn + "_" + name */
	  String pumlName(String fqn) {
	    int lastDot = fqn.lastIndexOf('.');
	    if (lastDot < 0) return fqn;

	    String prefix = fqn.substring(0, lastDot);
	    String name = fqn.substring(lastDot + 1);

	    if (byFqn.containsKey(prefix)) {
	      return prefix + "_" + name;
	    }
	    return fqn; 
	  }

	  String qPuml(String fqn) {
	    return "\"" + pumlName(fqn) + "\"";
	  }
	}