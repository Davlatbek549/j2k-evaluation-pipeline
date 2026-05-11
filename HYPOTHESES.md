# J2K Edge Case Hypotheses

This dataset is designed to stress-test Java-to-Kotlin conversion quality beyond simple syntax translation.

## NestedAnonymousClass.java

Deeply nested anonymous classes may be converted into verbose Kotlin object expressions instead of idiomatic lambdas, especially when nested scopes capture values and throw checked exceptions.

## ComplexGenerics.java

Complex generic bounds, wildcards, and variance may produce overly verbose Kotlin syntax or incorrect type projections.

## StaticInnerClass.java

Static nested classes and instance inner classes may expose mistakes around Kotlin `object`, companion-like static members, and `inner` class translation.

## MultiCatchException.java

Multi-catch exception handling should convert correctly, but the resulting Kotlin may be verbose because Kotlin does not have the same multi-catch syntax.

## BuilderPattern.java

A classic Java builder with a static inner `Builder` class is likely to remain structurally Java-like instead of becoming an idiomatic Kotlin data class with defaults and `copy()`.

## FunctionalInterfaces.java

Java functional interfaces, streams, lambdas, and method references may not be fully rewritten into Kotlin collection operations and idiomatic lambda syntax.

## EnumWithAbstractMethod.java

Enum constants that override abstract methods can be awkward to translate and may produce complex Kotlin enum bodies.

## NullHandling.java

Extensive null checks, `Optional`, and nullable returns may remain Java-like instead of being simplified with Kotlin safe calls, Elvis operators, and nullable types.
