List# vscript Built-in Functions Reference

Complete reference for all built-in functions available in vscript.

## Table of Contents

- [Mathematical Functions](#mathematical-functions)
  - [Trigonometric Functions](#trigonometric-functions)
  - [Basic Math Functions](#basic-math-functions)
  - [Statistical Functions](#statistical-functions)
- [String Functions](#string-functions)
- [Date and Time Functions](#date-and-time-functions)
  - [Current Date/Time](#current-datetime)
  - [Date Calculations](#date-calculations)
  - [Date Parsing and Formatting](#date-parsing-and-formatting)
- [Type Checking Functions](#type-checking-functions)
- [Array Functions](#array-functions)
- [Control Flow Functions](#control-flow-functions)

---

## Mathematical Functions

### Trigonometric Functions

#### `sin(n)`

Returns the sine of n (n in radians).

**Parameters:**
- `n` (Numeric): Angle in radians

**Returns:** Numeric

**Examples:**
```vscript
sin(0)           // 0
sin(PI / 2)      // 1.0
sin(PI)          // ~0 (very small number)
sin(PI / 6)      // 0.5
```

---

#### `cos(n)`

Returns the cosine of n (n in radians).

**Parameters:**
- `n` (Numeric): Angle in radians

**Returns:** Numeric

**Examples:**
```vscript
cos(0)           // 1.0
cos(PI / 2)      // ~0 (very small number)
cos(PI)          // -1.0
cos(PI / 3)      // 0.5
```

---

#### `tan(n)`

Returns the tangent of n (n in radians).

**Parameters:**
- `n` (Numeric): Angle in radians

**Returns:** Numeric

**Examples:**
```vscript
tan(0)           // 0
tan(PI / 4)      // ~1.0
tan(PI / 3)      // ~1.732
```

---

#### `asin(n)`

Returns the arcsine (inverse sine) of n. Result in radians.

**Parameters:**
- `n` (Numeric): Value between -1 and 1

**Returns:** Numeric (radians)

**Examples:**
```vscript
asin(0)          // 0
asin(1)          // PI/2 (~1.571)
asin(0.5)        // PI/6 (~0.524)
```

---

#### `acos(n)`

Returns the arccosine (inverse cosine) of n. Result in radians.

**Parameters:**
- `n` (Numeric): Value between -1 and 1

**Returns:** Numeric (radians)

**Examples:**
```vscript
acos(1)          // 0
acos(0)          // PI/2 (~1.571)
acos(-1)         // PI (~3.142)
```

---

#### `atan(n)`

Returns the arctangent (inverse tangent) of n. Result in radians.

**Parameters:**
- `n` (Numeric): Any numeric value

**Returns:** Numeric (radians)

**Examples:**
```vscript
atan(0)          // 0
atan(1)          // PI/4 (~0.785)
atan(-1)         // -PI/4 (~-0.785)
```

---

### Basic Math Functions

#### `abs(n)`

Returns the absolute value of n.

**Parameters:**
- `n` (Numeric): Any numeric value

**Returns:** Numeric

**Examples:**
```vscript
abs(5)           // 5
abs(-5)          // 5
abs(0)           // 0
abs(-3.14)       // 3.14
```

---

#### `neg(n)`

Returns the negative (negated) value of n.

**Parameters:**
- `n` (Numeric): Any numeric value

**Returns:** Numeric

**Examples:**
```vscript
neg(5)           // -5
neg(-5)          // 5
neg(0)           // 0
```

---

#### `sqrt(n)`

Returns the square root of n.

**Parameters:**
- `n` (Numeric): Non-negative numeric value

**Returns:** Numeric

**Examples:**
```vscript
sqrt(4)          // 2
sqrt(9)          // 3
sqrt(2)          // ~1.414
sqrt(0)          // 0
```

---

#### `pow(x, y)`

Returns x raised to the power of y (x^y).

**Parameters:**
- `x` (Numeric): Base value
- `y` (Numeric): Exponent value

**Returns:** Numeric

**Examples:**
```vscript
pow(2, 3)        // 8 (2^3)
pow(10, 2)       // 100
pow(5, 0)        // 1
pow(2, -1)       // 0.5
pow(9, 0.5)      // 3 (square root)
```

---

#### `log(n)`

Returns the natural logarithm (base e) of n.

**Parameters:**
- `n` (Numeric): Positive numeric value

**Returns:** Numeric

**Examples:**
```vscript
log(E)           // 1
log(1)           // 0
log(10)          // ~2.303
```

---

#### `exp(n)`

Returns e (Euler's number) raised to the power n (e^n).

**Parameters:**
- `n` (Numeric): Any numeric value

**Returns:** Numeric

**Examples:**
```vscript
exp(0)           // 1
exp(1)           // E (~2.718)
exp(2)           // ~7.389
```

---

#### `floor_mod(x, y)`

Returns the floor modulus of x and y.

**Parameters:**
- `x` (Numeric): Dividend
- `y` (Numeric): Divisor

**Returns:** Numeric

**Examples:**
```vscript
floor_mod(10, 3)     // 1
floor_mod(17, 5)     // 2
floor_mod(-10, 3)    // 2
```

---

#### `round(x, y)`

Rounds x to y decimal places using standard rounding rules.

**Parameters:**
- `x` (Numeric): Value to round
- `y` (Numeric): Number of decimal places

**Returns:** Numeric

**Examples:**
```vscript
round(3.14159, 2)    // 3.14
round(3.14159, 0)    // 3
round(3.5, 0)        // 4
round(123.456, 1)    // 123.5
round(5, 2)          // 5.00
```

---

### Statistical Functions

#### `max(n1, ..., nN)`

Returns the largest value from the provided arguments. Can accept arrays.

**Parameters:**
- `n1, ..., nN`: Numeric values or arrays

**Returns:** Numeric

**Examples:**
```vscript
max(5, 10, 3)            // 10
max(1, 2, 3, 4, 5)       // 5
max({10, 20, 5})         // 20
max(5, {10, 15}, 3)      // 15
```

---

#### `min(n1, ..., nN)`

Returns the smallest value from the provided arguments. Can accept arrays.

**Parameters:**
- `n1, ..., nN`: Numeric values or arrays

**Returns:** Numeric

**Examples:**
```vscript
min(5, 10, 3)            // 3
min(1, 2, 3, 4, 5)       // 1
min({10, 20, 5})         // 5
min(5, {10, 15}, 3)      // 3
```

---

#### `avg(n1, ..., nN)`

Returns the average (mean) of the provided numeric values.

**Parameters:**
- `n1, ..., nN`: Numeric values

**Returns:** Numeric

**Examples:**
```vscript
avg(5, 10, 15)           // 10
avg(1, 2, 3, 4, 5)       // 3
avg(100, 200)            // 150
avg({10, 20, 30})        // 20
```

---

#### `median(n1, ..., nN)`

Returns the median value from the provided numeric values.

**Parameters:**
- `n1, ..., nN`: Numeric values

**Returns:** Numeric

**Examples:**
```vscript
median(1, 2, 3, 4, 5)    // 3
median(1, 2, 3, 4)       // 2.5
median(5, 1, 3, 2, 4)    // 3
median({10, 50, 30})     // 30
```

---

## String Functions

#### `first(s, n)`

Returns the first n characters from string s.

**Parameters:**
- `s` (String): Input string
- `n` (Numeric): Number of characters to extract

**Returns:** String

**Examples:**
```vscript
first("Hello", 2)        // "He"
first("World", 3)        // "Wor"
first("abc", 10)         // "abc" (returns available chars)
first("", 5)             // ""
```

---

#### `last(s, n)`

Returns the last n characters from string s.

**Parameters:**
- `s` (String): Input string
- `n` (Numeric): Number of characters to extract

**Returns:** String

**Examples:**
```vscript
last("Hello", 2)         // "lo"
last("World", 3)         // "rld"
last("abc", 10)          // "abc"
last("", 5)              // ""
```

---

#### `skip(s, n)`

Skips the first n characters and returns the remainder of string s.

**Parameters:**
- `s` (String): Input string
- `n` (Numeric): Number of characters to skip

**Returns:** String

**Examples:**
```vscript
skip("Hello", 2)         // "llo"
skip("World", 3)         // "ld"
skip("abc", 10)          // ""
skip("", 5)              // ""
```

---

#### `size(x)`

Returns the length of a string or size of an array.

**Parameters:**
- `x` (String or Array): Input value

**Returns:** Numeric (-1 for non-string/array types)

**Examples:**
```vscript
size("Hello")            // 5
size("")                 // 0
size({1, 2, 3})          // 3
size({})                 // 0
size(42)                 // -1 (not a string/array)
```

---

## Date and Time Functions

### Current Date/Time

#### `now()`

Returns the current time in milliseconds since Unix epoch (January 1, 1970).

**Returns:** Numeric (milliseconds)

**Examples:**
```vscript
now()                    // e.g., 1683900000000
now() > 0                // true
```

---

#### `year()`

Returns the current year.

**Returns:** Numeric

**Examples:**
```vscript
year()                   // e.g., 2024
```

---

#### `month()`

Returns the current month (1-12).

**Returns:** Numeric (1 = January, 12 = December)

**Examples:**
```vscript
month()                  // e.g., 5 (May)
month() > 6              // Check if past June
```

---

#### `day()`

Returns the current day of the month (1-31).

**Returns:** Numeric

**Examples:**
```vscript
day()                    // e.g., 15
day() < 10               // First 9 days of month
```

---

#### `day_of_year()`

Returns the current day of the year (1-366).

**Returns:** Numeric

**Examples:**
```vscript
day_of_year()            // e.g., 135 (May 15th)
day_of_year() > 180      // Past midyear
```

---

### Date Calculations

#### `days_in_month(n)`

Returns the number of days in a month, with n as offset from current month.

**Parameters:**
- `n` (Numeric): Offset from current month (0 = current, -1 = previous, 1 = next)

**Returns:** Numeric

**Examples:**
```vscript
days_in_month(0)         // Days in current month (28-31)
days_in_month(-1)        // Days in previous month
days_in_month(1)         // Days in next month
days_in_month(-12)       // Days in same month last year
```

---

#### `days_since_weekday(x)`

Returns the number of days since the specified weekday.

**Parameters:**
- `x` (Numeric): Weekday (1 = Monday, 7 = Sunday)

**Returns:** Numeric

**Examples:**
```vscript
// If today is Friday (5):
days_since_weekday(1)    // 4 (days since Monday)
days_since_weekday(5)    // 0 (today is Friday)
days_since_weekday(6)    // 6 (days since Saturday)

// Check if today is Monday
days_since_weekday(1) == 0
```

---

#### `days_before_now(x)`

Returns the number of days between time x and now.

**Parameters:**
- `x` (Numeric or String): ISO8601 date string or milliseconds since epoch

**Returns:** Numeric

**Examples:**
```vscript
days_before_now("2024-01-01T00:00:00Z")
days_before_now(1672531200000)
days_before_now(now() - 86400000)    // ~1 day ago
```

---

#### `hours_before_now(x)`

Returns the number of hours between time x and now.

**Parameters:**
- `x` (Numeric or String): ISO8601 date string or milliseconds since epoch

**Returns:** Numeric

**Examples:**
```vscript
hours_before_now("2024-01-01T12:00:00Z")
hours_before_now(now() - 3600000)    // ~1 hour ago
```

---

#### `minutes_before_now(x)`

Returns the number of minutes between time x and now.

**Parameters:**
- `x` (Numeric or String): ISO8601 date string or milliseconds since epoch

**Returns:** Numeric

**Examples:**
```vscript
minutes_before_now("2024-01-01T12:30:00Z")
minutes_before_now(now() - 60000)    // ~1 minute ago
```

---

### Date Parsing and Formatting

#### `iso(str)`

Parses an ISO8601 date string and returns milliseconds since epoch.

**Parameters:**
- `str` (String): ISO8601 formatted date string

**Returns:** Numeric (milliseconds)

**Examples:**
```vscript
iso("2010-02-05T17:31:15Z")          // 1265391075000
iso("2024-01-01T00:00:00Z")
iso("2024-12-25T12:00:00.000Z")
```

**Supported Formats:**
- `2024-01-15T10:30:00Z`
- `2024-01-15T10:30:00.123Z`
- `2024-01-15T10:30:00+05:00`

---

#### `format_ts(x, y [, z])`

Formats a timestamp using the specified pattern and optional timezone.

**Parameters:**
- `x` (Numeric or String): Timestamp (ISO8601 string or milliseconds)
- `y` (String): Format pattern
- `z` (String, optional): Timezone ID (e.g., "America/New_York", "Europe/London")

**Returns:** String

**Common Format Patterns:**
- `MM/dd/yyyy` - Month/Day/Year (e.g., "05/12/2023")
- `dd/MM/yyyy` - Day/Month/Year (e.g., "12/05/2023")
- `yyyy-MM-dd` - ISO format (e.g., "2023-05-12")
- `MMM dd, yyyy` - Month name (e.g., "May 12, 2023")
- `EEEE, MMMM dd, yyyy` - Full names (e.g., "Friday, May 12, 2023")
- `HH:mm:ss` - 24-hour time (e.g., "14:30:45")
- `hh:mm a` - 12-hour time (e.g., "02:30 PM")

**Examples:**
```vscript
// Basic formatting
format_ts(1683900000000, "MM/dd/yyyy")           // "05/12/2023"
format_ts(1683900000000, "dd/MM/yyyy")           // "12/05/2023"
format_ts(1683900000000, "yyyy-MM-dd")           // "2023-05-12"

// With time
format_ts(1683900000000, "yyyy-MM-dd HH:mm:ss")  // "2023-05-12 14:00:00"
format_ts(1683900000000, "MMM dd, yyyy hh:mm a") // "May 12, 2023 02:00 PM"

// With timezone
format_ts(1683900000000, "MM/dd/yyyy HH:mm", "America/New_York")
format_ts(1683900000000, "dd/MM/yyyy HH:mm", "Europe/Paris")
format_ts(1683900000000, "yyyy-MM-dd HH:mm:ss", "Asia/Tokyo")

// From ISO string
format_ts("2023-05-12T14:00:00Z", "MMM dd, yyyy")  // "May 12, 2023"

// Current time
format_ts(now(), "yyyy-MM-dd HH:mm:ss")
```

**Pattern Reference:**
- `y` - Year (e.g., 2023)
- `M` - Month (1-12, or use MM for 01-12, MMM for "Jan", MMMM for "January")
- `d` - Day of month (1-31, or dd for 01-31)
- `H` - Hour (0-23, or HH for 00-23)
- `h` - Hour (1-12, or hh for 01-12)
- `m` - Minute (0-59, or mm for 00-59)
- `s` - Second (0-59, or ss for 00-59)
- `a` - AM/PM marker
- `E` - Day name (EEE for "Mon", EEEE for "Monday")

---

## Type Checking Functions

#### `is_null(x)`

Returns true if x is null.

**Parameters:**
- `x` (Any): Value to check

**Returns:** Boolean

**Examples:**
```vscript
is_null(null)            // true
is_null(0)               // false
is_null("")              // false
is_null({})              // false
```

---

#### `is_numeric(x)`

Returns true if x is a numeric value.

**Parameters:**
- `x` (Any): Value to check

**Returns:** Boolean

**Examples:**
```vscript
is_numeric(42)           // true
is_numeric(3.14)         // true
is_numeric("123")        // false (string)
is_numeric(true)         // false
```

---

#### `is_string(x)`

Returns true if x is a string.

**Parameters:**
- `x` (Any): Value to check

**Returns:** Boolean

**Examples:**
```vscript
is_string("hello")       // true
is_string("")            // true
is_string(42)            // false
is_string(null)          // false
```

---

#### `is_array(x)`

Returns true if x is an array.

**Parameters:**
- `x` (Any): Value to check

**Returns:** Boolean

**Examples:**
```vscript
is_array({1, 2, 3})      // true
is_array({})             // true (empty array)
is_array("array")        // false
is_array(null)           // false
```

---

## Array Functions

#### `to_array(x1, x2, ..., xN)`

Creates an array from the provided values.

**Parameters:**
- `x1, x2, ..., xN` (Any): Values to include in array

**Returns:** Array

**Examples:**
```vscript
to_array(1, 2, 3)                    // {1, 2, 3}
to_array("a", "b", "c")              // {"a", "b", "c"}
to_array(1, "two", true, null)       // {1, "two", true, null}
to_array()                           // {}
```

---

#### Array Operations

While not functions, these operators work with arrays:

**Append Element:**
```vscript
{1, 2, 3} + 4            // {1, 2, 3, 4}
arr + newItem
```

**Concatenate Arrays:**
```vscript
{1, 2} + {3, 4}          // {1, 2, 3, 4}
arr1 + arr2
```

**Remove Element:**
```vscript
{1, 2, 3, 2} - 2         // {1, 3} (removes all occurrences)
arr - itemToRemove
```

**Remove Multiple Elements:**
```vscript
{1, 2, 3, 4, 5} - {2, 4}     // {1, 3, 5}
arr - itemsToRemove
```

**Access Element:**
```vscript
arr[0]                   // First element
arr[size(arr) - 1]       // Last element
```

**Assign Element:**
```vscript
arr[2] = 100             // Set third element
```

---

## Control Flow Functions

#### `if(condition, trueValue, falseValue)`

Evaluates condition and returns trueValue if true, otherwise falseValue.

**Parameters:**
- `condition` (Boolean): Condition to evaluate
- `trueValue` (Any): Value to return if condition is true
- `falseValue` (Any): Value to return if condition is false

**Returns:** Any (type depends on values)

**Examples:**
```vscript
if(age >= 18, "Adult", "Minor")
if(score > 90, "A", "B")
if(x > 0, x, 0)
if(is_null(value), "N/A", value)

// Nested conditionals
if(score >= 90, "A", if(score >= 80, "B", if(score >= 70, "C", "F")))

// With calculations
if(price > 100, price * 0.9, price)    // 10% discount if over 100
```

---

#### `switch(expr, case1, value1, case2, value2, ..., [default])`

Evaluates expr and returns the value corresponding to the matching case.

**Parameters:**
- `expr` (Any): Expression to evaluate
- `case1, case2, ...` (Any): Case values to match against
- `value1, value2, ...` (Any): Values to return for each case
- `default` (Any, optional): Default value if no case matches

**Returns:** Any (depends on matched value)

**Examples:**
```vscript
// Basic switch
switch(day, 1, "Mon", 2, "Tue", 3, "Wed", 4, "Thu", 5, "Fri", "Weekend")

// Numeric results
switch(grade, "A", 4.0, "B", 3.0, "C", 2.0, "D", 1.0, 0.0)

// With expressions
switch(status,
    "active", price * 1.0,
    "premium", price * 0.9,
    "vip", price * 0.8,
    price
)

// Without default
switch(color, "red", "#FF0000", "green", "#00FF00", "blue", "#0000FF")

// String matching (case-sensitive by default)
switch(country, "US", "USD", "UK", "GBP", "EU", "EUR", "Unknown")
```

---

## Custom Functions

You can register custom functions in your Java application:

```java
DefaultRunBlock runBlock = new DefaultRunBlock();

// Register custom function with named parameters
runBlock.registerFunction("multiply(first, second)",
    (lvc) -> lvc.getVariant("first").multiply(lvc.getVariant("second"))
);

runBlock.registerFunction("greet(name)",
    (lvc) -> Variant.fromString("Hello, " + lvc.getVariant("name").asString())
);

// Use in expressions
Variant result = new EquationEval("2 + multiply(3, 4)")
    .withMasterBlock(runBlock)
    .eval(container);
```

**Features:**
- Named parameters for clarity
- Access to local variant container
- Support for null substitution: `custom_func()?defaultValue`
- Can return any Variant type

---

## Function Chaining

Functions can be chained and nested:

```vscript
// Chaining
first(skip("Hello World", 6), 5)     // "World"

// Nesting
max(min(a, b), min(c, d))

// Complex expressions
if(is_numeric(value), round(value, 2), "N/A")

// With null substitution
myFunction(param)?defaultValue
```

---

## Next Steps

- Check out practical [Examples](EXAMPLES.md) showing these functions in action
- Review [Syntax Guide](SYNTAX.md) for language fundamentals
- Return to [Main Documentation](README.md)
