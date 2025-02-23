[![Build Gradle Project](https://github.com/ascheman/hcl4j/actions/workflows/default-build.yml/badge.svg)](https://github.com/ascheman/hcl4j/actions/workflows/default-build.yml)

HCL4j
=====

HCL4j is a Parser for the Hashicorp Configuration Language on the JVM. This provides a mechanism for converting HCL syntax into an Object Map that can be used for further inspection. 

Features:

* Support for Syntax parsing
* Nested Array and Map support


## Installation

Using gradle one can include the hcl4j dependency like so:

```groovy
dependencies {
	compile "net.aschemann.iac:hcl4j:0.4.4"
}
```

## What's New

* **0.4.0** Primitive Types are now appended into the Map.  These are of an extended `PrimitiveType` class. These Types include `StringPrimitiveType`, `NumberPrimitiveType`, `BooleanPrimitiveType`, `MapPrimitiveType`, and lastly `ListPrimitiveType` with a `subType` capable property.

## Usage

Using the HCL Parser is fairly straight forward. Most calls are still limited to use of the `HCLParser` class itself. There are several `parse` method helpers supporting both `File`, `InputStream`, `String`, and `Reader` as inputs.


```java
import com.bertramlabs.plugins.hcl4j.HCLParser;

File terraformFile = new File("terraform.tf");
Map results = new HCLParser().parse(terraformFile, "UTF-8");
```

For More Information on the HCL Syntax Please see the project page:

[https://github.com/hashicorp/hcl](https://github.com/hashicorp/hcl)


## Things to be Done

This plugin does not yet handle processing of the interpolated string syntax. While it does generate it into the result map, Parsing the values of the interpolation syntax needs to be done in a followup step using some type of HCL runtime engine

## Development

* If you want to improve this module and are using IntelliJ, you probably like to get syntax highlighting by the https://plugins.jetbrains.com/plugin/263-jflex-support[IntelliJ JFlex Plugin].

## Known Bugs

* If a block is completely on one line (cf. TODOs in HCLParserSpec, it cannot be parsed correctly)