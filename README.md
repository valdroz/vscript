# vscript Language Documentation

Welcome to the vscript language documentation. vscript is a lightweight expression evaluation language designed for embedding in Java applications, perfect for dynamic calculations, business rules, data transformations, and configuration-driven logic.

## Table of Contents

1. [Syntax Guide](SYNTAX.md) - Language syntax, operators, and basic operations
2. [Built-in Functions](FUNCTIONS.md) - Complete reference of all built-in functions
3. [Examples and Usage](EXAMPLES.md) - Practical examples and usage patterns

## Quick Start

### Basic Evaluation

```java
Variant result = new EquationEval("3 + 5 * 2").eval();
// result = 13
```

### With Variables

```java
VariantContainer container = new DefaultVariantContainer();
container.setVariant("price", Variant.fromDouble(5.2));
Variant result = new EquationEval("price * 1.1").eval(container);
// result = 5.72
```

### Multiple Statements

```java
Variant result = new EquationEval(
    "a = 10; b = 20; c = a + b; c > 25"
).eval(container);
// result = true
```

## Key Features

- **Multiple Data Types**: Numeric (BigDecimal), String, Boolean, Array, and Null
- **Rich Operator Set**: Arithmetic, comparison, logical, bitwise, and assignment operators
- **Built-in Functions**: 50+ functions for math, strings, dates, arrays, and type checking
- **Variable Support**: Dynamic variables with local scope support
- **Array Operations**: Full array support with indexing, concatenation, and manipulation
- **Null Safety**: Null substitution operator (`?`) for safe defaults
- **Custom Functions**: Register your own functions with named parameters
- **Configurable**: Precision, rounding mode, and case sensitivity settings

## Data Types

vscript supports five main data types:

| Type | Example | Description |
|------|---------|-------------|
| **Numeric** | `42`, `3.14`, `-7.5` | High-precision decimals using BigDecimal |
| **String** | `"Hello"`, `"World"` | Text enclosed in double quotes |
| **Boolean** | `true`, `false` | Logical true/false values |
| **Array** | `{1, 2, "three"}` | Mixed-type collections |
| **Null** | `null` | Represents uninitialized or missing values |

## Basic Operators

```vscript
// Arithmetic
2 + 3 * 4        // 14
10 / 2 - 1       // 4

// Comparison
5 > 3            // true
10 == 10         // true
"a" != "b"       // true

// Logical
true && false    // false
true || false    // true
!false           // true

// Assignment
x = 42           // Sets x to 42

// Null substitution
value?"default"  // Returns "default" if value is null
```

## Common Use Cases

1. **Business Rules Engines**: Dynamic evaluation of business logic
2. **Data Validation**: Expression-based validation rules
3. **Calculations**: Complex mathematical computations with precision
4. **Configuration**: User-defined formulas and expressions
5. **Data Transformation**: Processing and transforming data on-the-fly

## Next Steps

- Explore the [Syntax Guide](SYNTAX.md) for detailed language syntax
- Browse the [Built-in Functions](FUNCTIONS.md) reference
- Check out [Examples](EXAMPLES.md) for practical usage patterns

## Configuration

Customize vscript behavior with global configuration:

```java
// Set decimal precision
Configuration.setDecimalScale(5);

// Set rounding mode
Configuration.setRoundingMode(RoundingMode.HALF_UP);

// Enable case-insensitive string comparison
Configuration.setCaseSensitive(false);

// Set default expression for empty input
Configuration.setExpressionForEmptyEval("0");
```

## Getting Help

For more information:
- Check the comprehensive [examples](EXAMPLES.md)
- Review function documentation in [FUNCTIONS.md](FUNCTIONS.md)
- Understand syntax details in [SYNTAX.md](SYNTAX.md)
