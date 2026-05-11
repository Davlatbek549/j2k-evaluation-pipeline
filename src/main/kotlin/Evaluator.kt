import java.io.File
import java.util.Locale

private const val CONVERTED_DIR = "kotlin-converted"
private const val REPORT_PATH = "reports/summary.md"

private data class QualityIssue(
    val title: String,
    val count: Int,
    val detail: String
)

private data class FileEvaluation(
    val file: File,
    val issues: List<QualityIssue>
) {
    val issueCount: Int = issues.sumOf { it.count }
    val isClean: Boolean = issueCount == 0
}

fun main() {
    val convertedDirectory = File(CONVERTED_DIR)
    val reportFile = File(REPORT_PATH)

    val evaluations = convertedDirectory
        .walkKtFiles()
        .map(::evaluateFile)
        .toList()

    val report = buildReport(evaluations)
    reportFile.parentFile.mkdirs()
    reportFile.writeText(report)

    println(report)
}

private fun File.walkKtFiles(): Sequence<File> {
    if (!exists() || !isDirectory) {
        return emptySequence()
    }

    return walkTopDown()
        .filter { it.isFile && it.extension == "kt" }
        .sortedBy { it.relativeTo(this).path }
}

private fun evaluateFile(file: File): FileEvaluation {
    val text = file.readText()
    val lines = file.readLines()

    val issues = listOfNotNull(
        countSystemOutPrintln(text),
        countJavaStyleTypes(text),
        countReplaceableNullChecks(text),
        countSemicolons(lines),
        countNewKeywords(text)
    )

    return FileEvaluation(file, issues)
}

private fun countSystemOutPrintln(text: String): QualityIssue? {
    val count = Regex("""\bSystem\.out\.println\s*\(""").findAll(text).count()
    return count.toIssue(
        title = "Java-style console output",
        detail = "Uses System.out.println instead of Kotlin println."
    )
}

private fun countJavaStyleTypes(text: String): QualityIssue? {
    val javaTypes = listOf(
        "Integer",
        "java.lang.Integer",
        "java.lang.Boolean",
        "java.lang.Long",
        "java.lang.Double",
        "java.lang.Float",
        "java.lang.Short",
        "java.lang.Byte",
        "java.lang.Character"
    )

    val count = javaTypes.sumOf { typeName ->
        Regex("""(?<![\w.])${Regex.escape(typeName)}(?![\w.])""").findAll(text).count()
    }

    return count.toIssue(
        title = "Java-style type declarations",
        detail = "Uses Java boxed or java.lang type names instead of idiomatic Kotlin types."
    )
}

private fun countReplaceableNullChecks(text: String): QualityIssue? {
    val count = listOf(
        Regex("""\bif\s*\(\s*[A-Za-z_][A-Za-z0-9_]*\s*!=\s*null\s*\)"""),
        Regex("""\bif\s*\(\s*null\s*!=\s*[A-Za-z_][A-Za-z0-9_]*\s*\)""")
    ).sumOf { it.findAll(text).count() }

    return count.toIssue(
        title = "Replaceable null checks",
        detail = "Contains explicit null checks that may be clearer with Kotlin safe calls."
    )
}

private fun countSemicolons(lines: List<String>): QualityIssue? {
    val count = lines.count { line ->
        val trimmed = line.trim()
        trimmed.endsWith(";") && !trimmed.startsWith("//")
    }

    return count.toIssue(
        title = "Trailing semicolons",
        detail = "Uses semicolons at the end of lines, which is usually a Java habit."
    )
}

private fun countNewKeywords(text: String): QualityIssue? {
    val count = Regex("""\bnew\s+[A-Za-z_]""").findAll(text).count()
    return count.toIssue(
        title = "Java-style object creation",
        detail = "Uses the new keyword instead of Kotlin constructor calls."
    )
}

private fun Int.toIssue(title: String, detail: String): QualityIssue? {
    if (this == 0) {
        return null
    }

    return QualityIssue(
        title = title,
        count = this,
        detail = detail
    )
}

private fun buildReport(evaluations: List<FileEvaluation>): String {
    val totalFiles = evaluations.size
    val totalIssues = evaluations.sumOf { it.issueCount }
    val cleanFiles = evaluations.count { it.isClean }
    val qualityScore = if (totalFiles == 0) {
        100.0
    } else {
        cleanFiles.toDouble() / totalFiles.toDouble() * 100.0
    }

    return buildString {
        appendLine("# J2K Evaluation Summary")
        appendLine()
        appendLine("## Overview")
        appendLine()
        appendLine("- Total files analyzed: $totalFiles")
        appendLine("- Clean conversions: $cleanFiles")
        appendLine("- Total issues found: $totalIssues")
        appendLine("- Overall quality score: ${qualityScore.formatPercentage()}%")
        appendLine()
        appendLine("## Per-file Breakdown")
        appendLine()

        if (evaluations.isEmpty()) {
            appendLine("No Kotlin files were found in `$CONVERTED_DIR/`.")
            appendLine()
        } else {
            evaluations.forEach { evaluation ->
                appendLine("### ${evaluation.file.relativeTo(File(CONVERTED_DIR)).path}")
                appendLine()
                appendLine("- Issues found: ${evaluation.issueCount}")

                if (evaluation.issues.isEmpty()) {
                    appendLine("- Status: Clean conversion")
                } else {
                    evaluation.issues.forEach { issue ->
                        appendLine("- ${issue.title}: ${issue.count}")
                        appendLine("  - ${issue.detail}")
                    }
                }

                appendLine()
            }
        }

        appendLine("## Conclusion")
        appendLine()
        appendLine(buildConclusion(totalFiles, totalIssues, qualityScore))
    }
}

private fun buildConclusion(totalFiles: Int, totalIssues: Int, qualityScore: Double): String {
    if (totalFiles == 0) {
        return "No converted Kotlin files were available to evaluate. Add `.kt` files to `$CONVERTED_DIR/` and rerun the pipeline."
    }

    if (totalIssues == 0) {
        return "All analyzed conversions look clean according to the configured Java-to-Kotlin quality checks."
    }

    return when {
        qualityScore >= 80.0 -> "Most conversions are clean, with a small number of Java-style patterns still worth reviewing."
        qualityScore >= 50.0 -> "The converted output is partially idiomatic, but several files need Kotlin cleanup before the result is production-ready."
        else -> "The converted output needs substantial review because many files still contain Java-style constructs."
    }
}

private fun Double.formatPercentage(): String =
    String.format(Locale.US, "%.2f", this)
