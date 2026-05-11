# J2K Evaluation Pipeline

A Kotlin/Gradle evaluation pipeline for testing Java-to-Kotlin conversion quality with JetBrains `j2k`.

This project was created for a JetBrains internship task. It automates a small conversion experiment, runs a Kotlin-based quality evaluator over the converted files, and produces a Markdown report summarizing conversion quality and edge-case behavior.

Repository: [github.com/Davlatbek549/j2k-evaluation-pipeline](https://github.com/Davlatbek549/j2k-evaluation-pipeline)

## What the Pipeline Does

The pipeline performs three main steps:

1. Collects Java source files from a selected subset of [`iluwatar/java-design-patterns`](https://github.com/iluwatar/java-design-patterns).
2. Runs the `j2k` converter and writes converted Kotlin files into `kotlin-converted/`.
3. Runs a Kotlin evaluator that scans converted files for Java-style conversion artifacts and writes `reports/summary.md`.

The project also includes a curated `edge-cases/` dataset with Java files designed to stress-test difficult conversion patterns such as nested anonymous classes, complex generics, static inner classes, Java functional interfaces, enum constants with abstract method overrides, and extensive null handling.

## Project Structure

```text
j2k-evaluation-pipeline/
├── .github/workflows/
│   └── j2k-pipeline.yml        # GitHub Actions workflow for CI conversion and reporting
├── edge-cases/                 # Handwritten Java stress tests for j2k
├── java-source/                # Java files collected for conversion
├── kotlin-converted/           # Output directory for converted Kotlin files
│   └── edge-cases/             # Output directory for converted edge-case files
├── reports/
│   └── summary.md              # Generated evaluation report
├── src/main/kotlin/
│   ├── AstAnalyzer.kt          # JavaParser-based declaration coverage analysis
│   └── Evaluator.kt            # Kotlin evaluator implementation and report generation
├── HYPOTHESES.md               # Hypotheses for each edge-case input
├── SUMMARY.md                  # Final project summary and findings
├── build.gradle.kts            # Gradle build configuration
└── settings.gradle.kts         # Gradle project settings
```

## Requirements

- JDK 17 or newer
- Kotlin
- Gradle, or the included Gradle wrapper
- `j2k` when running the full conversion flow outside GitHub Actions

The local evaluator runs through Gradle and uses JavaParser for Java AST declaration analysis.

## Run Locally

From the project root:

```bash
./gradlew run
```

This runs `src/main/kotlin/Evaluator.kt`, scans `kotlin-converted/`, generates `reports/summary.md`, and prints the same report to the console.

If `kotlin-converted/` is empty, the evaluator exits successfully and writes a report explaining that no converted Kotlin files were found.

## Trigger GitHub Actions

The GitHub Actions pipeline is defined in `.github/workflows/j2k-pipeline.yml`.

It runs automatically on:

- pushes to `main`
- pull requests targeting `main`

It can also be started manually:

1. Open the repository on GitHub.
2. Go to **Actions**.
3. Select **J2K Evaluation Pipeline**.
4. Click **Run workflow**.

## Sample Output

When you run `./gradlew run`, you will see output like this in the console:

```text
# J2K Evaluation Summary

## Overview

- Total files analyzed: 19
- Clean conversions: 19
- Total issues found: 0
- Overall quality score: 100.00%

## Per-file Breakdown

All standard converted files passed the configured quality checks.

## Edge Case Analysis

- Edge case files analyzed: 8
- Edge case issues found: 0
- Edge case quality score: 100.00%

The edge-case conversions were generated under `kotlin-converted/edge-cases/`.

## AST Declaration Analysis

- Java/Kotlin file pairs analyzed: 19
- Java declarations found: 142
- Declarations matched in Kotlin: 128
- Overall AST coverage: 90.14%

Some declarations were renamed or structurally rewritten during conversion, so AST coverage is high but not perfect.

## Conclusion

All converted files passed the text-based quality checks, while AST declaration analysis identified a small number of declarations that need manual review.
```

The workflow installs the Kotlin compiler, verifies `j2k`, collects Java files, converts normal and edge-case inputs, runs the evaluator, prints `reports/summary.md` in the CI logs, and uploads conversion artifacts.
