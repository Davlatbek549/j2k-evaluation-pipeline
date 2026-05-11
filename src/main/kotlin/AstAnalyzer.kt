import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.EnumDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import java.io.File

enum class DeclarationKind {
    CLASS,
    METHOD,
    FIELD
}

data class JavaDeclaration(
    val kind: DeclarationKind,
    val name: String
) {
    val label: String = "${kind.name.lowercase()}: $name"
}

data class AstFileAnalysis(
    val javaFile: File,
    val kotlinFile: File,
    val declarations: List<JavaDeclaration>,
    val matchedDeclarations: List<JavaDeclaration>,
    val unmatchedDeclarations: List<JavaDeclaration>
) {
    val totalDeclarations: Int = declarations.size
    val matchedCount: Int = matchedDeclarations.size
    val coverage: Double = if (totalDeclarations == 0) {
        100.0
    } else {
        matchedCount.toDouble() / totalDeclarations.toDouble() * 100.0
    }
}

object AstAnalyzer {
    fun analyzeProject(
        javaSourceDirectory: File = File("java-source"),
        kotlinConvertedDirectory: File = File("kotlin-converted")
    ): List<AstFileAnalysis> {
        if (!javaSourceDirectory.exists() || !javaSourceDirectory.isDirectory) {
            return emptyList()
        }

        return javaSourceDirectory
            .walkTopDown()
            .filter { it.isFile && it.extension == "java" }
            .sortedBy { it.relativeTo(javaSourceDirectory).path }
            .mapNotNull { javaFile ->
                val kotlinFile = javaFile.correspondingKotlinFile(
                    javaSourceDirectory = javaSourceDirectory,
                    kotlinConvertedDirectory = kotlinConvertedDirectory
                )

                if (!kotlinFile.exists()) {
                    null
                } else {
                    analyzePair(javaFile, kotlinFile)
                }
            }
            .toList()
    }

    fun analyzePair(javaFile: File, kotlinFile: File): AstFileAnalysis {
        val declarations = extractDeclarations(javaFile)
        val kotlinText = kotlinFile.readText()
        val matchedDeclarations = declarations.filter { it.existsInKotlin(kotlinText) }
        val unmatchedDeclarations = declarations - matchedDeclarations.toSet()

        return AstFileAnalysis(
            javaFile = javaFile,
            kotlinFile = kotlinFile,
            declarations = declarations,
            matchedDeclarations = matchedDeclarations,
            unmatchedDeclarations = unmatchedDeclarations
        )
    }

    private fun extractDeclarations(javaFile: File): List<JavaDeclaration> {
        val compilationUnit = StaticJavaParser.parse(javaFile)
        val classes = compilationUnit.findAll(ClassOrInterfaceDeclaration::class.java)
            .map { JavaDeclaration(DeclarationKind.CLASS, it.nameAsString) }
        val enums = compilationUnit.findAll(EnumDeclaration::class.java)
            .map { JavaDeclaration(DeclarationKind.CLASS, it.nameAsString) }
        val methods = compilationUnit.findAll(MethodDeclaration::class.java)
            .map { JavaDeclaration(DeclarationKind.METHOD, it.nameAsString) }
        val fields = compilationUnit.findAll(FieldDeclaration::class.java)
            .flatMap { field ->
                field.variables.map { variable ->
                    JavaDeclaration(DeclarationKind.FIELD, variable.nameAsString)
                }
            }

        return (classes + enums + methods + fields).distinctBy { it.kind to it.name }
    }

    private fun File.correspondingKotlinFile(
        javaSourceDirectory: File,
        kotlinConvertedDirectory: File
    ): File {
        val relativePath = relativeTo(javaSourceDirectory).path
        return File(kotlinConvertedDirectory, relativePath.replace(Regex("""\.java$"""), ".kt"))
    }

    private fun JavaDeclaration.existsInKotlin(kotlinText: String): Boolean =
        when (kind) {
            DeclarationKind.CLASS -> kotlinText.containsDeclaration(
                Regex("""\b(class|interface|object|enum\s+class)\s+${Regex.escape(name)}\b""")
            )
            DeclarationKind.METHOD -> kotlinText.containsDeclaration(
                Regex("""\bfun\s+${Regex.escape(name)}\s*\(""")
            )
            DeclarationKind.FIELD -> kotlinText.containsDeclaration(
                Regex("""\b(val|var)\s+${Regex.escape(name)}\b""")
            )
        }

    private fun String.containsDeclaration(pattern: Regex): Boolean =
        pattern.containsMatchIn(this)
}
