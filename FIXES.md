# Proposed Edge Case Fix

## Selected Failure: BuilderPattern.java

The `BuilderPattern.java` edge case fails the idiomatic Kotlin hypothesis. The Java source uses a classic static inner `Builder` class with mutable builder fields and chainable methods. j2k can translate that structure, but it keeps the Java design instead of replacing it with Kotlin's simpler data-class model.

## What j2k Got Wrong

j2k preserves the builder ceremony:

- A separate builder type
- Mutable intermediate state
- Chainable setter-like methods
- A final `build()` method
- Manual copying of the `tags` list

That output may be correct, but it is not idiomatic Kotlin. Kotlin can express the same model with constructor defaults, immutable properties, and `copy()`.

## Ideal Kotlin Output

```kotlin
data class BuilderPattern(
    val name: String = "default",
    val retries: Int = 3,
    val enabled: Boolean = true,
    val tags: List<String> = emptyList()
) {
    fun withTag(tag: String): BuilderPattern =
        copy(tags = tags + tag)
}

fun exampleUsage(): BuilderPattern {
    return BuilderPattern()
        .copy(name = "pipeline", retries = 5)
        .withTag("j2k")
        .withTag("edge-case")
}
```

## Why This Is Better

The Kotlin version removes unnecessary mutable builder state and makes the configuration object immutable by default. Default constructor values replace builder defaults, and `copy()` replaces most chainable builder methods. The `withTag` helper keeps tag addition ergonomic while still returning a new immutable value.

This is the kind of semantic improvement that a mechanical converter is unlikely to infer automatically, but it is exactly the kind of cleanup expected after using j2k as a first-pass migration tool.
