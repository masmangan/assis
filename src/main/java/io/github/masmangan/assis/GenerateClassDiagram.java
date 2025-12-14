package io.github.masmangan.assis;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * 
 */
public class GenerateClassDiagram {

    /**
     * 
     */
    static class TypeInfo {
        String pkg;
        String name;
        boolean isInterface;
        Set<String> extendsTypes = new HashSet<>();
        Set<String> implementsTypes = new HashSet<>();
        Set<String> fieldsToTypes = new HashSet<>();
    }

    /**
     * 
     * @throws Exception
     */
    public static void generate() throws Exception {
        Path src = Paths.get("src/main/java");
        Path out = Paths.get("docs/uml/class-diagram.puml");
        Files.createDirectories(out.getParent());
        generate(src, out);
    }

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void generate(Path src, Path out) throws Exception {
        Map<String, TypeInfo> types = new HashMap<>();
        List<Path> files = new ArrayList<>();
        if (Files.exists(src)) {
            try (var s = Files.walk(src)) {
                s.filter(p -> p.toString().endsWith(".java")).forEach(files::add);
            }
        }

        for (Path p : files) {
            String code = Files.readString(p);
            CompilationUnit cu;
            try {
                cu = StaticJavaParser.parse(code);
            } catch (Exception e) {
                System.err.println("Parser fail: " + p + " (" + e.getMessage() + ")");
                continue;
            }

            String pkg = cu.getPackageDeclaration().map(pd -> pd.getName().toString()).orElse("");

            for (TypeDeclaration<?> td : cu.getTypes()) {
                if (!(td instanceof ClassOrInterfaceDeclaration))
                    continue;

                ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) td;
                TypeInfo info = new TypeInfo();
                info.pkg = pkg;
                info.name = cid.getNameAsString();
                info.isInterface = cid.isInterface();

                // extends / implements
                for (ClassOrInterfaceType ext : cid.getExtendedTypes()) {
                    info.extendsTypes.add(simpleName(ext.getNameAsString()));
                }
                for (ClassOrInterfaceType impl : cid.getImplementedTypes()) {
                    info.implementsTypes.add(simpleName(impl.getNameAsString()));
                }

                // fields -> association candidates
                for (FieldDeclaration fd : cid.getFields()) {
                    String t = fd.getElementType().asString();
                    // tira generics e arrays simples
                    t = t.replaceAll("<.*>", "").replace("[]", "");
                    info.fieldsToTypes.add(simpleName(t));
                }

                types.put(info.name, info);
            }
        }

        // Generate PlantUML
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(out))) {
            pw.println("@startuml");
            // pw.println("skinparam classAttributeIconSize 0");
            pw.println("hide empty members");
            pw.println("!theme blueprint");

            // packages
            Map<String, List<TypeInfo>> byPkg = types.values().stream()
                    .collect(Collectors.groupingBy(t -> t.pkg == null ? "" : t.pkg));

            for (var entry : byPkg.entrySet()) {
                String pkg = entry.getKey();
                if (!pkg.isEmpty())
                    pw.println("package \"" + pkg + "\" {");
                for (TypeInfo t : entry.getValue()) {
                    if (t.isInterface) {
                        pw.println("interface " + t.name);
                    } else {
                        pw.println("class " + t.name);
                    }
                }
                if (!pkg.isEmpty())
                    pw.println("}");
            }

            // Extends and implements
            for (TypeInfo t : types.values()) {
                for (String e : t.extendsTypes) {
                    if (types.containsKey(e)) {
                        pw.println(e + " <|-- " + t.name);
                    }
                }
                for (String i : t.implementsTypes) {
                    if (types.containsKey(i)) {
                        pw.println(i + " <|.. " + t.name);
                    }
                }
            }

            // Associations
            for (TypeInfo t : types.values()) {
                for (String f : t.fieldsToTypes) {
                    if (types.containsKey(f) && !f.equals(t.name)) {
                        pw.println(t.name + " --> " + f);
                    }
                }
            }

            pw.println();
            pw.println("left to right direction");

            addFooter(pw);

            pw.println("@enduml");
        }

        System.out.println("Diagram at: " + out.toAbsolutePath());
    }

    /**
     * 
     * @param pw
     */
    private static void addFooter(PrintWriter pw) {
        pw.println("footer ");
        pw.println("Generated with ASSIS (Java → UML)");
        pw.println("https://github.com/masmangan/javaparser-to-plantuml");
        pw.println("end footer");
    }

    /**
     * 
     * @param qname
     * @return
     */
    private static String simpleName(String qname) {
        int lt = qname.lastIndexOf('.');
        return (lt >= 0) ? qname.substring(lt + 1) : qname;
    }

}