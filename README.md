[![SonarCloud analysis](https://github.com/masmangan/assis/actions/workflows/sonarcloud.yml/badge.svg)](https://github.com/masmangan/assis/actions/workflows/sonarcloud.yml)
[![CodeQL Advanced](https://github.com/masmangan/assis/actions/workflows/codeql.yml/badge.svg)](https://github.com/masmangan/assis/actions/workflows/codeql.yml)

# ASSIS
Generate UML diagrams from Java code.

## Getting Started

**Do you have a terminal available?**  
Check the [Getting Started Guide](./docs/user/assis-gs-bash.md).

> The instructions below were validated with **ASSIS v0.9.3**.

## Usage

### Get the latest version

Download the latest release from:  
https://github.com/masmangan/assis/releases

For **v0.9.3**, the released CLI artifact is:

https://github.com/masmangan/assis/releases/download/v0.9.3/assis-cli-0.9.3-beta.jar

Older releases are kept for reference, but documentation always targets the latest release.

### Run ASSIS

ASSIS scans your source folder for `.java` files and generates a PlantUML class diagram
(`class-diagram.puml`).

Place the JAR in the project root folder (or any folder of your choice).

Run ASSIS from the command line:

```bash
java -jar assis-cli-0.9.3-beta.jar
```

After execution, a `class-diagram.puml` file will be generated.

The diagram can be edited and rendered using:
- PlantUML Online Server: https://www.plantuml.com/plantuml/uml/
- IDE plugins (e.g., Eclipse, VS Code)

The `.puml` file can also be embedded in Markdown documents.

## Development

### Build

```bash
mvn -DskipTests package
```

### Run with JAR (from source build)

```bash
java -jar assis-cli/target/assis-cli-0.9.3.jar
```

### Run with Maven

```bash
mvn -q -DskipTests exec:java -Dexec.mainClass=io.github.masmangan.assis.cli.AssisApp
```

## ASSIS Classes Overview

![Assis](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/masmangan/assis/refs/heads/main/assis-core/docs/diagrams/src/class-diagram.puml)

## License

This project is licensed under the MIT License.

## Use of Artificial Intelligence

ASSIS was developed with the assistance of Artificial Intelligence tools used in a
conversational and reflective role (e.g., architectural discussion, diagram modeling,
and documentation), rather than as inline code generation or autonomous programming agents.

All final decisions and implementations remain the responsibility of the author.

## References

ASSIS uses existing open-source tools as building blocks:

- **PlantUML** — UML tool  
  https://plantuml.com

- **JavaParser** — Java source code parser  
  https://javaparser.org
