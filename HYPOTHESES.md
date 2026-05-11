# J2K Edge Case Hypotheses

This dataset is designed to stress-test Java-to-Kotlin conversion quality beyond simple syntax translation.

### NestedAnonymousClass.java

- **Hypothesis:** j2k will struggle converting anonymous classes to Kotlin lambdas
- **Result:** FAILED
- **Actual output:** The converter preserves the nested structure with Kotlin object expressions instead of collapsing the code into concise lambdas.
- **Why:** The output is behavior-preserving, but it remains structurally close to Java. This fails the idiomatic conversion goal because the converter avoids deeper lambda refactoring when anonymous classes are nested and capture surrounding values.

### ComplexGenerics.java

- **Hypothesis:** j2k will produce verbose or incorrect generic syntax
- **Result:** FAILED
- **Actual output:** The converted Kotlin keeps complex type bounds and variance-heavy signatures rather than simplifying the API.
- **Why:** The conversion is useful as a first draft, but wildcard-heavy Java generics map to verbose Kotlin projections. This fails the idiomatic conversion goal because the resulting code needs manual review.

### StaticInnerClass.java

- **Hypothesis:** j2k may incorrectly handle companion objects vs inner classes
- **Result:** PASSED
- **Actual output:** The converter distinguishes static nested classes from instance inner classes, but static members are represented in a Java-like style rather than being redesigned as idiomatic Kotlin.
- **Why:** The conversion generally preserves the distinction correctly, so this does not show a hard functional failure. It still supports the hypothesis at the idiom level because static-style Java structure remains visible.

### MultiCatchException.java

- **Hypothesis:** j2k will handle this fine but may produce verbose output
- **Result:** PASSED
- **Actual output:** The converter preserves the exception-handling behavior, but the result is more verbose than hand-written Kotlin because Kotlin has no direct multi-catch syntax.
- **Why:** This is a good example where j2k is reliable for behavior but cannot always produce the most compact Kotlin representation.

### BuilderPattern.java

- **Hypothesis:** j2k won't convert this to idiomatic Kotlin data class with copy()
- **Result:** FAILED
- **Actual output:** The converted output keeps the Java builder shape: a main class, mutable builder fields, chainable methods, and an explicit `build()` method.
- **Why:** This fails the idiomatic Kotlin expectation. A human Kotlin version would usually use a `data class` with default values and `copy()` instead of preserving the full Java builder ceremony.

### FunctionalInterfaces.java

- **Hypothesis:** j2k may not fully convert to Kotlin lambdas
- **Result:** FAILED
- **Actual output:** The converter handles simple lambdas and method references, but Java streams and functional interfaces remain closer to their Java API form than idiomatic Kotlin collection chains.
- **Why:** The result is likely valid Kotlin, but it still fails the idiomatic conversion goal because Java stream style should usually become Kotlin collection operations.

### EnumWithAbstractMethod.java

- **Hypothesis:** j2k will struggle with this pattern
- **Result:** PASSED
- **Actual output:** The converter keeps enum constants with overridden method bodies, producing a Kotlin enum with per-constant class bodies.
- **Why:** Kotlin supports this pattern, so the conversion can preserve behavior. The result may still be visually heavy, but it is a reasonable direct translation.

### NullHandling.java

- **Hypothesis:** j2k won't fully convert to Kotlin's null safety operators (?., ?:, !!)
- **Result:** FAILED
- **Actual output:** The converted output preserves many explicit null checks and Java `Optional` usage rather than consistently rewriting logic with safe calls and Elvis expressions.
- **Why:** This fails the idiomatic Kotlin expectation. j2k can translate the syntax, but it does not infer the higher-level null-safety simplifications a Kotlin developer would normally write.
