# javaparser-to-plantuml
Generate a UML diagram from Java code, using Java Parser and PlantUML

## Run

### Build
```bash
mvn -DskipTests package

mvn -DskipTests exec:java -Dexec.mainClass=io.github.masmangan.assis.AssisApp
```
## License

This project is licensed under the MIT License.

## References

Assis uses existing open-source tools as building blocks:

- **PlantUML** — UML diagram language  
  https://plantuml.com

- **JavaParser** — Java source code parser  
  https://javaparser.org