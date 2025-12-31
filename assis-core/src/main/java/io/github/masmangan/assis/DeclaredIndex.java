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
 * Index of declared types (top-level and nested).
 */
class DeclaredIndex {

  /** FQN → declaration */
  final Map<String, TypeDeclaration<?>> byFqn = new LinkedHashMap<>();

  /** FQN → declared package (from CompilationUnit) */
  final Map<String, String> pkgByFqn = new LinkedHashMap<>();

  /** package → list of FQNs */
  Map<String, List<String>> fqnsByPkg = new LinkedHashMap<>();

  /** simple name → unique FQN (only when unambiguous) */
  final Map<String, String> uniqueBySimple = new LinkedHashMap<>();

  /**
   * 
   * @param cus
   * @return
   */
  static DeclaredIndex build(List<CompilationUnit> cus) {
    DeclaredIndex idx = new DeclaredIndex();

    for (CompilationUnit cu : cus) {
      String pkg = cu.getPackageDeclaration()
          .map(pd -> pd.getNameAsString())
          .orElse("");

      for (TypeDeclaration<?> td : cu.getTypes()) {
        collectTypeRecursive(idx, pkg, td, null);
      }
    }

    for (Map.Entry<String, String> e : idx.pkgByFqn.entrySet()) {
      String fqn = e.getKey();
      String pkg = e.getValue();
      idx.fqnsByPkg
          .computeIfAbsent(pkg, k -> new ArrayList<>())
          .add(fqn);
    }

    idx.fqnsByPkg = sortPackagesByNameFqn(idx.fqnsByPkg);
    idx.fqnsByPkg.values().forEach(list -> list.sort(String::compareTo));

    Map<String, String> seen = new LinkedHashMap<>();
    Set<String> ambiguous = new LinkedHashSet<>();

    for (String fqn : idx.byFqn.keySet()) {
      String simple = GenerateClassDiagram.simpleName(fqn);
      if (!seen.containsKey(simple)) {
        seen.put(simple, fqn);
      } else {
        ambiguous.add(simple);
      }
    }

    for (var e : seen.entrySet()) {
      if (!ambiguous.contains(e.getKey())) {
        idx.uniqueBySimple.put(e.getKey(), e.getValue());
      }
    }

    return idx;
  }

  /**
   * 
   * @param idx
   * @param pkg
   * @param td
   * @param ownerFqn
   */
  private static void collectTypeRecursive(
      DeclaredIndex idx,
      String pkg,
      TypeDeclaration<?> td,
      String ownerFqn
  ) {
    String name = td.getNameAsString();
    String fqn;

    if (ownerFqn == null) {
      fqn = pkg.isEmpty() ? name : pkg + "." + name;
    } else {
      fqn = ownerFqn + "." + name;
    }

    idx.byFqn.put(fqn, td);
    idx.pkgByFqn.put(fqn, pkg); 

    if (td instanceof ClassOrInterfaceDeclaration cid) {
      cid.getMembers().forEach(m -> {
        if (m instanceof TypeDeclaration<?> nested) {
          collectTypeRecursive(idx, pkg, nested, fqn);
        }
      });
    } else if (td instanceof EnumDeclaration ed) {
      ed.getMembers().forEach(m -> {
        if (m instanceof TypeDeclaration<?> nested) {
          collectTypeRecursive(idx, pkg, nested, fqn);
        }
      });
    }
  }

  /**
   * 
   * @param byPkg
   * @return
   */
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

  /**
   * 
   * @param ownerPkg
   * @param rawName
   * @return
   */
  String resolveTypeName(String ownerPkg, String rawName) {
    if (rawName == null) return null;
    String raw = rawName.trim();
    if (raw.isEmpty()) return null;

    if (raw.contains(".") && byFqn.containsKey(raw)) return raw;

    String simple = GenerateClassDiagram.simpleName(raw);

    String samePkg = (ownerPkg == null || ownerPkg.isEmpty())
        ? simple
        : ownerPkg + "." + simple;

    if (byFqn.containsKey(samePkg)) return samePkg;

    return uniqueBySimple.get(simple);
  }

  /**
   * 
   * @param fqn
   * @return
   */
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

  /**
   * 
   * @param fqn
   * @return
   */
  String qPuml(String fqn) {
    return "\"" + pumlName(fqn) + "\"";
  }
}