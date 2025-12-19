# vscript Language Syntax

Complete reference for vscript language syntax, operators, and language constructs.

## Data Types

### Numeric

High-precision decimal numbers backed by `java.math.BigDecimal`.

```vscript
42           // Integer
3.14159      // Decimal
-7.5         // Negative
2.65         // Precision controlled by Configuration
```

**Configuration:**
```java
Configuration.setDecimalScale(3);  // Default precision
Configuration.setRoundingMode(RoundingMode.HALF_EVEN);
```

**Automatic Conversion:**
```vscript
"5" + 3      // 8 (string "5" converted to number)
"2.5" * 2    // 5.0
```

### String

Text values enclosed in double quotes with escape sequence support.

```vscript
"Hello World"
"Line 1\nLine 2"     // Newline
"Tab\tSeparated"     // Tab
"Say \"Hello\""      // Escaped quotes
```

**Supported Escape Sequences:**
- `\n` - Newline
- `\t` - Tab
- `\"` - Double quote

**String Operations:**
```vscript
"Hello" + " " + "World"     // "Hello World"
"Value: " + 42              // "Value: 42"
```

### Boolean

Logical true/false values.

```vscript
true
false

5 > 3        // true
10 == 20     // false
```

**Note:** Booleans cannot be used in arithmetic operations.

### Array

Mixed-type collections with zero-based indexing.

```vscript
{1, 2, 3}                    // Numeric array
{"one", "two", "three"}      // String array
{10, "mixed", true, null}    // Mixed types
```

**Array Operations:**
```vscript
arr[0]                       // Access first element
arr[2] = 100                 // Assign to element
arr + 5                      // Append: {1, 2, 3, 5}
arr + {4, 5}                 // Concatenate: {1, 2, 3, 4, 5}
arr - 2                      // Remove: {1, 3}
arr - {1, 3}                 // Remove multiple: {2}
```

### Null

Represents uninitialized or missing values.

```vscript
null

// Null conversions
null + 5                     // null (propagates)
null == null                 // true
is_null(value)               // Check if null
```

**Null Behavior:**
- Numeric operations: propagates null
- String operations: converts to empty string
- Boolean operations: converts to false

---

## Operators

### Arithmetic Operators

| Operator | Description | Example | Result |
|----------|-------------|---------|--------|
| `+` | Addition / String concatenation | `5 + 3` | `8` |
| `-` | Subtraction | `10 - 4` | `6` |
| `*` | Multiplication | `6 * 7` | `42` |
| `/` | Division | `15 / 3` | `5` |
| `pow(x,y)` | Power/Exponentiation | `pow(2, 3)` | `8` |

**Examples:**
```vscript
2 + 3 * 4            // 14 (multiplication first)
(2 + 3) * 4          // 20 (parentheses first)
10 / 0               // Error: Division by zero
```

### Comparison Operators

| Operator | Description | Example | Result |
|----------|-------------|---------|--------|
| `==` | Equal to | `5 == 5` | `true` |
| `!=` | Not equal to | `5 != 3` | `true` |
| `>` | Greater than | `10 > 5` | `true` |
| `<` | Less than | `3 < 7` | `true` |
| `>=` | Greater than or equal | `5 >= 5` | `true` |
| `<=` | Less than or equal | `3 <= 2` | `false` |

**String Comparison:**
```vscript
"apple" < "banana"            // true (lexicographic)
"abc" == "ABC"                // false (case-sensitive by default)
```

**Array Membership:**
```vscript
5 == {1, 5, 10}              // true (5 is in array)
"x" == {"a", "b", "c"}       // false
```

### Logical Operators

| Operator | Description | Example           | Result |
|----------|------------|-------------------|--------|
| `&&`     | Logical AND | `true && false`   | `false` |
| `\|\|`    | Logical OR | `true \|\| false` | `true` |
| `!`      | Logical NOT | `!true`           | `false` |

**Short-circuit Evaluation:**
```vscript
false && expensive_operation()    // expensive_operation() not called
true || expensive_operation()     // expensive_operation() not called
```

**Examples:**
```vscript
(age >= 18) && (hasLicense == true)
(status == "active") || (status == "pending")
!(x > 10)
```

### Bitwise/Binary Operators

| Operator | Description | Example  | Result |
|----------|-------------|----------|-------|
| `&`      | Bitwise AND | `5 & 3`  | `1` |
| `\|`     | Bitwise OR | `5 \| 3` | `7` |
| `^`      | Bitwise XOR | `5 ^ 3`  | `6` |

**Examples:**
```vscript
12 & 10      // 8  (1100 & 1010 = 1000)
12 | 10      // 14 (1100 | 1010 = 1110)
12 ^ 10      // 6  (1100 ^ 1010 = 0110)
```

### Assignment Operator

| Operator | Description | Example |
|----------|-------------|---------|
| `=` | Assigns value to variable | `x = 42` |

**Examples:**
```vscript
price = 19.99
count = 10
arr[0] = 5
name = "John"
```

### Null Substitution Operator

| Operator | Description | Example |
|----------|-------------|---------|
| `?` | Provides default if null | `value?"default"` |

**Examples:**
```vscript
price?"0"                         // "0" if price is null
firstName?"Unknown"               // "Unknown" if firstName is null

// Chaining
var1?var2?var3?"default"         // First non-null value
user.name?"Guest"                // Default for nested property
myFunction()?0                   // Default if function returns null
```

**Use Cases:**
```vscript
// Safe defaults
discount = discount?0
username = username?"anonymous"

// Fallback chain
value = primary?secondary?tertiary?0

// Array access with default
arr[5]?-1                        // -1 if index out of bounds
```

---

## Variables

### Variable Names

Valid variable names can contain:
- Letters: `a-z`, `A-Z`
- Digits: `0-9` (not as first character)
- Underscores: `_`
- Dots: `.` (for nested properties)

**Examples:**
```vscript
myVar
user_age
product.price
item123
_private
```

**Case Sensitivity:**
By default, variable names are case-sensitive:
```vscript
Value != value != VALUE
```

To disable case sensitivity:
```java
Configuration.setCaseSensitive(false);
```

### Variable Assignment

```vscript
x = 10
name = "Alice"
active = true
items = {1, 2, 3}
```

### Local Variables

Use `var` keyword to declare local variables with limited scope:

```vscript
var temp = 5
var result = temp * 2
result               // 10
```

**Scope:**
Local variables are only visible within the current expression block.

---

## Expression Structure

### Multiple Statements

Separate multiple expressions with semicolons (`;`):

```vscript
a = 10; b = 20; c = a + b
```

The result of the last expression is returned:

```vscript
x = 5; y = 10; x + y        // Returns 15
price = 100; tax = price * 0.1; price + tax  // Returns 110
```

### Operator Precedence

From highest to lowest:

1. **Function calls, Parentheses** `()`, `function_name()`
2. **Array indexing** `[]`
3. **Unary NOT** `!`
4. **Binary operators** `&`, `|`, `^`
5. **Multiplication/Division** `*`, `/`
6. **Addition/Subtraction** `+`, `-`
7. **Comparison** `>`, `<`, `>=`, `<=`, `==`, `!=`
8. **Logical AND** `&&`
9. **Logical OR** `||`
10. **Assignment** `=`

**Examples:**
```vscript
2 + 3 * 4            // 14 (multiplication before addition)
(2 + 3) * 4          // 20 (parentheses first)
5 > 3 && 10 < 20     // true (comparison before logical)
x = 10 + 5           // x = 15 (addition before assignment)
```

---

## Reserved Keywords & Constants

### Keywords

- `true` - Boolean true value
- `false` - Boolean false value
- `null` - Null value
- `var` - Declares local variable

### Mathematical Constants

- `PI` - π ≈ 3.1415926535897932384626433832795
- `E` - e (Euler's number) ≈ 2.718281828459045

**Examples:**
```vscript
2 * PI * radius              // Circle circumference
pow(E, x)                    // e^x
```

---

## Control Flow

### Conditional Expression

```vscript
if(condition, trueValue, falseValue)
```

**Examples:**
```vscript
if(age >= 18, "Adult", "Minor")
if(score > 90, "A", if(score > 80, "B", "C"))
if(stock > 0, price, 0)
```

### Switch Expression

```vscript
switch(expression, case1, value1, case2, value2, ..., default)
```

**Examples:**
```vscript
switch(day, 1, "Mon", 2, "Tue", 3, "Wed", "Unknown")
switch(status, "active", 1, "pending", 0, "inactive", -1, 0)
switch(grade, "A", 4.0, "B", 3.0, "C", 2.0, 0.0)
```

**Without Default:**
```vscript
switch(color, "red", "#FF0000", "green", "#00FF00", "blue", "#0000FF")
```

---

## Advanced Features

### Array Indexing

Zero-based array access:

```vscript
arr = {10, 20, 30, 40}
arr[0]               // 10 (first element)
arr[2]               // 30 (third element)
arr[arr[0]]          // Error if index out of bounds
```

**Modifying Arrays:**
```vscript
arr[1] = 25          // Set second element to 25
arr[size(arr)-1]     // Last element
```

### Nested Expressions

```vscript
// Nested conditionals
if(x > 0, if(x > 10, "large", "small"), "negative")

// Complex calculations
result = ((a + b) * c) / (d - e)
```

---

## Comments

**Note:** vscript does not currently support comments in expressions.

To document complex expressions, use surrounding code comments in your Java application:

```java
// Calculate total with discount and tax
Variant result = new EquationEval(
    "price = 100; discount = price * 0.1; " +
    "discountedPrice = price - discount; " +
    "tax = discountedPrice * 0.08; " +
    "discountedPrice + tax"
).eval();
```

---

## Error Handling

### Common Errors

**Division by Zero:**
```vscript
10 / 0               // Throws EvaluationException
```

**Invalid Operations:**
```vscript
true + 5             // Error: Cannot use boolean in arithmetic
"abc" * 2            // Error: Non-numeric string in arithmetic
```

**Array Out of Bounds:**
```vscript
arr = {1, 2, 3}
arr[10]              // May return null or throw error
arr[10]?-1           // Safe with default: -1
```

---

## Configuration Options

### Decimal Precision

```java
// Set precision to 5 decimal places
Configuration.setDecimalScale(5);
```

```vscript
10 / 3               // 3.33333 (with scale=5)
```

### Rounding Mode

```java
import java.math.RoundingMode;
Configuration.setRoundingMode(RoundingMode.HALF_UP);
```

Available modes: `HALF_UP`, `HALF_DOWN`, `HALF_EVEN`, `UP`, `DOWN`, `CEILING`, `FLOOR`

### Case Sensitivity

```java
// Disable case-sensitive comparisons
Configuration.setCaseSensitive(false);
```

```vscript
"ABC" == "abc"       // true (when case-insensitive)
```

### Default Expression

```java
// Set default for empty input
Configuration.setExpressionForEmptyEval("0");
```

---

## Next Steps

- Explore [Built-in Functions](FUNCTIONS.md) for the complete function reference
- Check [Examples](EXAMPLES.md) for practical usage patterns
- Return to [Main Documentation](README.md)
