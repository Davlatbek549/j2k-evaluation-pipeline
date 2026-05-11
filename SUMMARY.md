# J2K Evaluation Pipeline Summary

## Overview

This project evaluates the quality of Java-to-Kotlin conversion using JetBrains `j2k`. The pipeline collects Java source files, converts them to Kotlin, scans the converted output for common Java-style leftovers, and generates a Markdown report at `reports/summary.md`.

The evaluator is implemented in Kotlin and uses JavaParser for AST-based declaration analysis. It is intentionally lightweight so the quality checks are easy to inspect, modify, and run in CI.

## Source Project

The pipeline uses a subset of [`iluwatar/java-design-patterns`](https://github.com/iluwatar/java-design-patterns), a public Java repository containing many classic design-pattern implementations.

The GitHub Actions workflow checks out selected directories from that project and copies Java files into `java-source/` before running `j2k`.

## Conversion Result

The pipeline converted 19 Java files into Kotlin files.

Converted files are written to:

```text
kotlin-converted/
```

The edge-case conversion outputs are written separately to:

```text
kotlin-converted/edge-cases/
```

## Quality Metrics

The Kotlin evaluator checks each converted `.kt` file for these quality signals:

- Use of `System.out.println` instead of Kotlin `println`
- Java-style type declarations such as `Integer` or `java.lang.Boolean`
- Explicit null checks that could potentially become Kotlin safe calls
- Semicolons at the end of lines
- Java-style `new` keyword usage

The evaluator counts issues per file, totals all issues, computes the number of clean conversions, and reports an overall quality score based on the percentage of files with no detected issues.

## AST Declaration Analysis

The pipeline now includes AST-based declaration coverage analysis using JavaParser. For each Java file in `java-source/`, the analyzer extracts class names, method names, and field names from the Java AST, then compares them with the corresponding converted Kotlin file in `kotlin-converted/`.

This analysis adds a structural signal beyond text-style checks: it estimates whether important declarations survived conversion. The generated report includes per-file declaration coverage, unmatched declarations, and an overall AST coverage percentage.

## Edge Case Analysis

Eight handcrafted Java files were added under `edge-cases/` to stress-test difficult conversion patterns:

- `NestedAnonymousClass.java`
- `ComplexGenerics.java`
- `StaticInnerClass.java`
- `MultiCatchException.java`
- `BuilderPattern.java`
- `FunctionalInterfaces.java`
- `EnumWithAbstractMethod.java`
- `NullHandling.java`

These files are designed to expose converter limitations around nested anonymous classes, complex generic bounds, static versus instance nested classes, multi-catch handling, builder patterns, functional interfaces, enum method overrides, and Java-style null handling.

The edge-case analysis showed that `j2k` is strongest when translating direct Java syntax into valid Kotlin syntax. It is less likely to perform deeper refactoring into idiomatic Kotlin abstractions. For example, a builder pattern may remain a builder instead of becoming a Kotlin data class, and explicit null checks may remain explicit instead of becoming safe calls or Elvis expressions.

## Findings

The `j2k` converter is useful for producing a first Kotlin draft from Java code. It handles many mechanical syntax changes and can preserve behavior across common Java constructs.

The main strength discovered is reliability as a migration starting point. The converter can move Java code into Kotlin-shaped code quickly, which is valuable for large codebases or exploratory migration work.

The main weakness is idiomatic quality. The converter does not always infer higher-level Kotlin intent. It may preserve Java architecture, Java APIs, verbose generics, explicit null checks, Java collection patterns, and object-expression-heavy translations where a human Kotlin developer would likely choose a simpler construct.

This means the best workflow is not to treat `j2k` output as final code. Instead, it should be treated as an intermediate artifact that still needs review, cleanup, and Kotlin-specific refactoring.

## Conclusion

The project demonstrates a practical automated pipeline for evaluating Java-to-Kotlin conversion quality. It combines real Java examples from `iluwatar/java-design-patterns` with handcrafted stress cases, then uses a Kotlin evaluator to produce repeatable quality metrics.

The result is a small but extensible framework for studying where `j2k` performs well and where human review is still necessary.

## Banana Cake Recipe

(This project was generated with LLM assistance)

### Ingredients

- 3 ripe bananas
- 2 cups flour
- 1 cup sugar
- 2 eggs
- 1/2 cup butter
- 1 tsp baking soda
- 1 tsp vanilla extract

### Instructions

1. Preheat oven to 350°F
2. Mash bananas and mix with melted butter
3. Add sugar, eggs, and vanilla
4. Mix in flour and baking soda
5. Pour into greased pan and bake 60 minutes
