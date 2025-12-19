# Java Integration Examples

Practical examples showing how to integrate and use the vscript library in Java applications.

## Table of Contents

- [Getting Started](#getting-started)
- [Basic Usage Patterns](#basic-usage-patterns)
- [Working with Variables](#working-with-variables)
- [Custom Functions](#custom-functions)
- [Real-World Use Cases](#real-world-use-cases)
- [Array Operations](#array-operations)
- [JSON Integration](#json-integration)
- [Configuration and Testing](#configuration-and-testing)
- [Error Handling](#error-handling)
- [Best Practices](#best-practices)

---

## Getting Started

### Maven Dependency

Add vscript to your `pom.xml`:

```xml
<dependency>
    <groupId>org.valdroz.vscript</groupId>
    <artifactId>vscript</artifactId>
    <version>3.13.0</version>
</dependency>
```

### Simple Evaluation

The most basic way to evaluate an expression:

```java
import org.valdroz.vscript.*;

public class QuickStart {
    public static void main(String[] args) {
        // Evaluate simple expression
        Variant result = new EquationEval("2 + 3 * 4").eval();
        System.out.println(result.asNumeric().doubleValue());  // 14.0

        // Boolean operations
        Variant bool = new EquationEval("5 > 3 && 10 < 20").eval();
        System.out.println(bool.asBoolean());  // true

        // String operations
        Variant str = new EquationEval("\"Hello\" + \" \" + \"World\"").eval();
        System.out.println(str.asString());  // Hello World
    }
}
```

### Complete Example with Variables

```java
import org.valdroz.vscript.*;
import java.math.RoundingMode;

public class Example {
    public static void main(String[] args) {
        // Configure decimal precision
        Configuration.setDecimalScale(2);
        Configuration.setRoundingMode(RoundingMode.HALF_UP);

        // Create variable container
        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("myVar1", Variant.fromDouble(5.2));

        // Execute expression
        Variant result = EquationEval.parse(
            "res = 3 + myVar1 * sqrt(4); res == 13.4"
        ).execute(container);

        // Get computed variable
        Variant res = container.getVariant("res");

        System.out.println("Result: " + res.asNumeric().doubleValue());  // 13.4
        System.out.println("Formatted: " + res.asString());  // 13.40
        System.out.println("Check passed: " + result.asBoolean());  // true
    }
}
```

---

## Basic Usage Patterns

### Pattern 1: Direct Evaluation (No Variables)

Use this when you don't need external variables:

```java
// Simple arithmetic
Variant result = new EquationEval("2 + 3 * 4").eval();
System.out.println(result.asNumeric().doubleValue());  // 14.0

// String concatenation
Variant str = new EquationEval("\"Result: \" + (10 + 5)").eval();
System.out.println(str.asString());  // "Result: 15"

// Boolean logic
Variant bool = new EquationEval("true && false || true").eval();
System.out.println(bool.asBoolean());  // true
```

### Pattern 2: Using Variable Container

Use this when expressions need access to external data:

```java
DefaultVariantContainer container = new DefaultVariantContainer();
container.setVariant("price", Variant.fromDouble(99.99));
container.setVariant("quantity", Variant.fromInt(5));

Variant result = new EquationEval("price * quantity").eval(container);
System.out.println(result.asNumeric().doubleValue());  // 499.95
```

### Pattern 3: Static Parse Method

Parse once, execute multiple times with different containers:

```java
// Parse expression to Node
Node node = EquationEval.parse("price * (1 + taxRate)");

// Execute with different data
VariantContainer order1 = new DefaultVariantContainer();
order1.setVariant("price", Variant.fromDouble(100.0));
order1.setVariant("taxRate", Variant.fromDouble(0.08));
Variant total1 = node.execute(order1);  // 108.0

VariantContainer order2 = new DefaultVariantContainer();
order2.setVariant("price", Variant.fromDouble(50.0));
order2.setVariant("taxRate", Variant.fromDouble(0.10));
Variant total2 = node.execute(order2);  // 55.0
```

### Pattern 4: Getting Expression Statistics

Analyze expressions before execution:

```java
EquationEval eq = new EquationEval(
    "total = price * quantity; discount = total * 0.1; final = total - discount"
);

NodeStats stats = eq.getStats();

System.out.println("Variables referenced: " + stats.referencedVariables());
// [price, quantity, total, discount, final]

System.out.println("Functions used: " + stats.referencedExtFunctions());
// []
```

### Pattern 5: With Tracing/Debugging

Enable tracing to understand expression evaluation:

```java
List<String> trace = new ArrayList<>();

Variant result = new EquationEval("a = 10; b = 20; a + b", trace::add).eval();

// Print trace
trace.forEach(System.out::println);
```

Or print directly:

```java
Variant result = new EquationEval("pow(2, 8)", System.out::println).eval();
```

---

## Working with Variables

### Creating Variables with Different Types

```java
DefaultVariantContainer container = new DefaultVariantContainer();

// Numeric variants
container.setVariant("intValue", Variant.fromInt(42));
container.setVariant("doubleValue", Variant.fromDouble(3.14159));
container.setVariant("bigDecValue", Variant.fromBigDecimal(new BigDecimal("99.99")));

// String variants
container.setVariant("name", Variant.fromString("John Doe"));
container.setVariant("email", Variant.fromString("john@example.com"));

// Boolean variants
container.setVariant("isActive", Variant.fromBoolean(true));
container.setVariant("isPremium", Variant.fromBoolean(false));

// Null variants
container.setVariant("optionalValue", Variant.nullVariant());

// Array variants
container.setVariant("emptyArray", Variant.emptyArray());
container.setVariant("numbers", Variant.fromArray(Arrays.asList(
    Variant.fromInt(1),
    Variant.fromInt(2),
    Variant.fromInt(3)
)));
```

### Reading and Using Variables

```java
DefaultVariantContainer container = new DefaultVariantContainer();

// Execute expression that creates variables
new EquationEval("tax = price * 0.08; total = price + tax").eval(container);

// Retrieve computed values
Variant tax = container.getVariant("tax");
Variant total = container.getVariant("total");

System.out.println("Tax: " + tax.asNumeric().doubleValue());
System.out.println("Total: " + total.asNumeric().doubleValue());

// Check if variable exists
if (container.contains("discount")) {
    Variant discount = container.getVariant("discount");
    // Use discount
}
```

### Dot Notation for Properties

```java
DefaultVariantContainer container = new DefaultVariantContainer();

// Set nested properties
container.setVariant("user.name", Variant.fromString("Alice"));
container.setVariant("user.age", Variant.fromInt(30));
container.setVariant("product.price", Variant.fromDouble(19.99));
container.setVariant("product.stock", Variant.fromInt(100));

// Use in expressions
Variant result = new EquationEval(
    "\"User \" + user.name + \" is ordering at $\" + product.price"
).eval(container);

System.out.println(result.asString());
// "User Alice is ordering at $19.99"
```

### Null Substitution Pattern

```java
VariantContainer container = new DefaultVariantContainer();
container.setVariant("primaryEmail", Variant.nullVariant());
container.setVariant("secondaryEmail", Variant.fromString("backup@example.com"));

// Single substitution
Variant email = new EquationEval("primaryEmail?\"default@example.com\"")
    .eval(container);
System.out.println(email.asString());  // "default@example.com"

// Chained substitution
email = new EquationEval("primaryEmail?secondaryEmail?\"no-email@example.com\"")
    .eval(container);
System.out.println(email.asString());  // "backup@example.com"

// Function as fallback
Variant value = new EquationEval("optionalValue?sqrt(16)")
    .eval(container);
System.out.println(value.asNumeric().intValue());  // 4
```

---

## Custom Functions

### Method 1: Lambda Registration

The simplest way to register custom functions:

```java
DefaultRunBlock runBlock = new DefaultRunBlock();

// Simple multiplication function
runBlock.registerFunction("multiply(first, second)",
    (lvc) -> lvc.getVariant("first").multiply(lvc.getVariant("second"))
);

// String greeting function
runBlock.registerFunction("greet(name)",
    (lvc) -> Variant.fromString("Hello, " + lvc.getVariant("name").asString() + "!")
);

// Use custom functions
VariantContainer container = new DefaultVariantContainer();
container.setVariant("username", Variant.fromString("Alice"));

Variant result = new EquationEval("2 + multiply(3, 4)")
    .withMasterBlock(runBlock)
    .eval(container);
System.out.println(result.asNumeric().intValue());  // 14

result = new EquationEval("greet(username)")
    .withMasterBlock(runBlock)
    .eval(container);
System.out.println(result.asString());  // "Hello, Alice!"
```

### Method 2: Using AbstractFunction

For more complex functions:

```java
DefaultRunBlock runBlock = new DefaultRunBlock();

runBlock.registerFunction(
    new AbstractFunction("calculateDiscount(price, percentage)") {
        @Override
        public Variant execute(VariantContainer vc) {
            double price = vc.getVariant("price").asNumeric().doubleValue();
            double percentage = vc.getVariant("percentage").asNumeric().doubleValue();
            double discount = price * (percentage / 100.0);
            return Variant.fromDouble(discount);
        }
    }
);

VariantContainer container = new DefaultVariantContainer();
Variant discount = new EquationEval("calculateDiscount(100, 15)")
    .withMasterBlock(runBlock)
    .eval(container);

System.out.println("Discount: $" + discount.asNumeric().doubleValue());  // 15.0
```

### Custom Function with Null Safety

```java
DefaultRunBlock runBlock = new DefaultRunBlock();

runBlock.registerFunction("safeDiv(numerator, denominator)",
    (lvc) -> {
        Variant num = lvc.getVariant("numerator");
        Variant den = lvc.getVariant("denominator");

        if (den.isNull() || den.asNumeric().doubleValue() == 0) {
            return Variant.nullVariant();
        }

        return num.divide(den);
    }
);

// Use with null substitution
Variant result = new EquationEval("safeDiv(10, 0)?-1")
    .withMasterBlock(runBlock)
    .eval();

System.out.println(result.asNumeric().intValue());  // -1
```

### Multiple Custom Functions Example

```java
public class CalculatorFunctions {

    public static DefaultRunBlock createMathBlock() {
        DefaultRunBlock runBlock = new DefaultRunBlock();

        // Percentage calculation
        runBlock.registerFunction("percent(value, total)",
            (lvc) -> {
                double value = lvc.getVariant("value").asNumeric().doubleValue();
                double total = lvc.getVariant("total").asNumeric().doubleValue();
                return Variant.fromDouble((value / total) * 100);
            }
        );

        // Clamp value between min and max
        runBlock.registerFunction("clamp(value, min, max)",
            (lvc) -> {
                double value = lvc.getVariant("value").asNumeric().doubleValue();
                double min = lvc.getVariant("min").asNumeric().doubleValue();
                double max = lvc.getVariant("max").asNumeric().doubleValue();
                double clamped = Math.max(min, Math.min(max, value));
                return Variant.fromDouble(clamped);
            }
        );

        return runBlock;
    }

    public static void main(String[] args) {
        DefaultRunBlock mathBlock = createMathBlock();

        Variant pct = new EquationEval("percent(25, 200)")
            .withMasterBlock(mathBlock)
            .eval();
        System.out.println(pct.asNumeric().doubleValue() + "%");  // 12.5%

        Variant clamped = new EquationEval("clamp(150, 0, 100)")
            .withMasterBlock(mathBlock)
            .eval();
        System.out.println(clamped.asNumeric().doubleValue());  // 100.0
    }
}
```

---

## Real-World Use Cases

### E-Commerce Pricing Engine

```java
public class PricingEngine {

    private final Node pricingFormula;
    private final DefaultRunBlock functionBlock;

    public PricingEngine() {
        // Register business functions
        functionBlock = new DefaultRunBlock();

        functionBlock.registerFunction("memberDiscount(isMember, price)",
            (lvc) -> {
                boolean isMember = lvc.getVariant("isMember").asBoolean();
                double price = lvc.getVariant("price").asNumeric().doubleValue();
                return Variant.fromDouble(isMember ? price * 0.95 : price);
            }
        );

        // Parse pricing formula
        pricingFormula = EquationEval.parse(
            "basePrice = quantity * unitPrice; " +
            "volumeDiscount = if(quantity >= 10, 0.2, if(quantity >= 5, 0.1, 0)); " +
            "discountedPrice = basePrice * (1 - volumeDiscount); " +
            "memberPrice = memberDiscount(isMember, discountedPrice); " +
            "tax = memberPrice * taxRate; " +
            "memberPrice + tax"
        );
    }

    public double calculateTotal(int quantity, double unitPrice,
                                 boolean isMember, double taxRate) {
        VariantContainer vars = new DefaultVariantContainer();
        vars.setVariant("quantity", Variant.fromInt(quantity));
        vars.setVariant("unitPrice", Variant.fromDouble(unitPrice));
        vars.setVariant("isMember", Variant.fromBoolean(isMember));
        vars.setVariant("taxRate", Variant.fromDouble(taxRate));

        Variant result = pricingFormula.execute(vars, functionBlock);
        return result.asNumeric().doubleValue();
    }

    public static void main(String[] args) {
        PricingEngine engine = new PricingEngine();

        // Calculate for member with volume discount
        double total = engine.calculateTotal(15, 10.0, true, 0.08);
        System.out.printf("Total: $%.2f%n", total);  // $123.12

        // Calculate for non-member
        total = engine.calculateTotal(3, 10.0, false, 0.08);
        System.out.printf("Total: $%.2f%n", total);  // $32.40
    }
}
```

### Business Rules Engine

```java
public class LoanApprovalEngine {

    private final Node approvalRule;

    public LoanApprovalEngine() {
        // Define approval rules
        approvalRule = EquationEval.parse(
            "eligible = (age >= 21) && (age <= 65); " +
            "incomeOk = income >= 30000; " +
            "creditOk = creditScore >= 650; " +
            "riskLevel = switch(true, " +
            "  creditScore >= 750 && income >= 80000, \"low\", " +
            "  creditScore >= 700 && income >= 50000, \"medium\", " +
            "  creditScore >= 650 && income >= 30000, \"high\", " +
            "  \"reject\"); " +
            "approved = eligible && incomeOk && creditOk && riskLevel != \"reject\""
        );
    }

    public LoanDecision evaluate(int age, double income, int creditScore) {
        VariantContainer vars = new DefaultVariantContainer();
        vars.setVariant("age", Variant.fromInt(age));
        vars.setVariant("income", Variant.fromDouble(income));
        vars.setVariant("creditScore", Variant.fromInt(creditScore));

        approvalRule.execute(vars);

        boolean approved = vars.getVariant("approved").asBoolean();
        String riskLevel = vars.getVariant("riskLevel").asString();

        return new LoanDecision(approved, riskLevel);
    }

    static class LoanDecision {
        final boolean approved;
        final String riskLevel;

        LoanDecision(boolean approved, String riskLevel) {
            this.approved = approved;
            this.riskLevel = riskLevel;
        }
    }

    public static void main(String[] args) {
        LoanApprovalEngine engine = new LoanApprovalEngine();

        LoanDecision decision = engine.evaluate(35, 75000, 720);
        System.out.println("Approved: " + decision.approved);  // true
        System.out.println("Risk: " + decision.riskLevel);     // medium
    }
}
```

### Subscription Billing Calculator

```java
public class BillingCalculator {

    private final Node billingFormula;

    public BillingCalculator() {
        billingFormula = EquationEval.parse(
            "monthlyRate = switch(tier, " +
            "  \"starter\", 9, " +
            "  \"professional\", 29, " +
            "  \"enterprise\", 99, " +
            "  0); " +
            "monthsInPeriod = if(period == \"annual\", 12, 1); " +
            "annualDiscount = if(period == \"annual\", 0.2, 0); " +
            "subtotal = monthlyRate * monthsInPeriod; " +
            "discountAmount = subtotal * annualDiscount; " +
            "subtotal - discountAmount"
        );
    }

    public double calculate(String tier, String billingPeriod) {
        VariantContainer vars = new DefaultVariantContainer();
        vars.setVariant("tier", Variant.fromString(tier));
        vars.setVariant("period", Variant.fromString(billingPeriod));

        Variant result = billingFormula.execute(vars);
        return result.asNumeric().doubleValue();
    }

    public static void main(String[] args) {
        BillingCalculator calc = new BillingCalculator();

        System.out.println("Professional Monthly: $" +
            calc.calculate("professional", "monthly"));  // $29.0

        System.out.println("Professional Annual: $" +
            calc.calculate("professional", "annual"));   // $278.4 (20% off)
    }
}
```

### Date-Based Pricing (Time-Sensitive Discounts)

```java
public class TimeSensitivePricing {

    private final Node pricingRule;
    private final DefaultRunBlock functions;

    public TimeSensitivePricing() {
        functions = new DefaultRunBlock();

        // Custom function to check if date is in range
        functions.registerFunction("isInMonth(targetMonth)",
            (lvc) -> {
                int target = lvc.getVariant("targetMonth").asNumeric().intValue();
                // Use built-in month() function via EquationEval
                Variant current = new EquationEval("month()").eval();
                return Variant.fromBoolean(current.asNumeric().intValue() == target);
            }
        );

        pricingRule = EquationEval.parse(
            "holidayDiscount = if(isInMonth(12), 0.25, 0); " +
            "blackFriday = (month() == 11) && (day() >= 23) && (day() <= 25); " +
            "blackFridayDiscount = if(blackFriday, 0.30, 0); " +
            "maxDiscount = max(holidayDiscount, blackFridayDiscount); " +
            "basePrice * (1 - maxDiscount)"
        );
    }

    public double calculatePrice(double basePrice) {
        VariantContainer vars = new DefaultVariantContainer();
        vars.setVariant("basePrice", Variant.fromDouble(basePrice));

        Variant result = pricingRule.execute(vars, functions);
        return result.asNumeric().doubleValue();
    }

    public static void main(String[] args) {
        TimeSensitivePricing pricing = new TimeSensitivePricing();

        double price = pricing.calculatePrice(100.0);
        System.out.printf("Current price: $%.2f%n", price);
    }
}
```

---

## Array Operations

### Creating and Manipulating Arrays

```java
DefaultVariantContainer container = new DefaultVariantContainer();

// Create array using to_array function
Variant result = new EquationEval("arr = to_array(1, 2, 3, 4, 5)")
    .eval(container);

Variant arr = container.getVariant("arr");
System.out.println("Array size: " +
    new EquationEval("size(arr)").eval(container).asNumeric().intValue());  // 5

// Append elements
new EquationEval("arr = arr + 6 + 7").eval(container);

// Remove elements
new EquationEval("arr = arr - 3").eval(container);  // Removes all 3s

// Access elements
Variant first = new EquationEval("arr[0]").eval(container);
Variant last = new EquationEval("arr[size(arr) - 1]").eval(container);

// Concatenate arrays
Variant combined = new EquationEval(
    "a = to_array(1, 2, 3) + to_array(4, 5, 6)"
).eval(container);
```

### Array Statistics

```java
VariantContainer container = new DefaultVariantContainer();

// Calculate statistics on arrays
Variant max = new EquationEval("max(10, 20, 5, 30, 15)").eval(container);
System.out.println("Max: " + max.asNumeric().intValue());  // 30

Variant min = new EquationEval("min(10, 20, 5, 30, 15)").eval(container);
System.out.println("Min: " + min.asNumeric().intValue());  // 5

Variant avg = new EquationEval("avg(10, 20, 30, 40, 50)").eval(container);
System.out.println("Average: " + avg.asNumeric().doubleValue());  // 30.0

Variant median = new EquationEval("median(1, 3, 3, 6, 7, 8, 9)").eval(container);
System.out.println("Median: " + median.asNumeric().intValue());  // 6
```

### Dynamic Array Operations

```java
// Dynamic array allocation
Variant result = new EquationEval("scores[5] = 100; size(scores)")
    .eval(new DefaultVariantContainer());
System.out.println("Array size: " + result.asNumeric().intValue());  // 6

// Check array membership
VariantContainer container = new DefaultVariantContainer();
container.setVariant("validCodes",
    Variant.fromArray(Arrays.asList(
        Variant.fromString("SAVE10"),
        Variant.fromString("SAVE20"),
        Variant.fromString("SAVE30")
    ))
);

Variant isValid = new EquationEval("\"SAVE20\" == validCodes").eval(container);
System.out.println("Code is valid: " + isValid.asBoolean());  // true
```

---

## JSON Integration

### Working with JSON Data

```java
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.valdroz.vscript.json.JsonVariantContainer;

public class JsonExample {

    public static void main(String[] args) throws Exception {
        // Parse JSON
        String json = """
            {
                "user": {
                    "name": "Alice",
                    "age": 30,
                    "email": "alice@example.com"
                },
                "orders": [
                    {"id": 1, "total": 150.00},
                    {"id": 2, "total": 200.00}
                ],
                "active": true
            }
            """;

        JsonElement je = JsonParser.parseString(json);
        List<JsonVariantContainer> containers =
            JsonVariantContainer.jsonToVariantContainers(je.getAsJsonObject());

        JsonVariantContainer vc = containers.get(0);

        // Access nested properties using dot notation
        Variant name = vc.getVariant("user.name");
        System.out.println("Name: " + name.asString());  // Alice

        // Evaluate expressions against JSON data
        Variant result = new EquationEval(
            "user.age >= 18 && active"
        ).eval(vc);
        System.out.println("Is adult and active: " + result.asBoolean());  // true

        // Modify JSON data
        new EquationEval("user.age = user.age + 1").eval(vc);
        System.out.println("New age: " + vc.getVariant("user.age").asNumeric().intValue());  // 31

        // Array access
        Variant firstOrderTotal = new EquationEval("orders[0].total").eval(vc);
        System.out.println("First order: $" + firstOrderTotal.asNumeric().doubleValue());  // 150.0
    }
}
```

---

## Configuration and Testing

### Configuring Decimal Precision

```java
public class PrecisionExample {

    public static void main(String[] args) {
        // Save current setting
        int originalScale = Configuration.setDecimalScale(4);

        try {
            VariantContainer container = new DefaultVariantContainer();
            container.setVariant("d1", Variant.fromInt(1));
            container.setVariant("d2", Variant.fromInt(3));

            Variant result = new EquationEval("d1 / d2").eval(container);
            System.out.println(result.asString());  // 0.3333

        } finally {
            // Restore original setting
            Configuration.setDecimalScale(originalScale);
        }
    }
}
```

### Case-Insensitive Comparisons

```java
// Enable case-insensitive mode
boolean originalSetting = Configuration.setCaseSensitive(false);

try {
    VariantContainer container = new DefaultVariantContainer();
    container.setVariant("code", Variant.fromString("ABC123"));

    Variant result = new EquationEval("code == \"abc123\"").eval(container);
    System.out.println("Codes match: " + result.asBoolean());  // true

} finally {
    Configuration.setCaseSensitive(originalSetting);
}
```

### Time Mocking for Testing

```java
import java.util.function.Supplier;

public class TestWithMockedTime {

    @Test
    public void testDateCalculation() {
        // Mock current time to 2010-02-05T17:31:15Z
        Supplier<Long> originalSupplier =
            EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);

        try {
            Variant day = new EquationEval("day()").eval();
            assertEquals(5, day.asNumeric().intValue());

            Variant month = new EquationEval("month()").eval();
            assertEquals(2, month.asNumeric().intValue());

            Variant year = new EquationEval("year()").eval();
            assertEquals(2010, year.asNumeric().intValue());

        } finally {
            // Restore original time supplier
            EquationEval.setCurrentTimeSupplier(originalSupplier);
        }
    }
}
```

---

## Error Handling

### Handling Evaluation Exceptions

```java
import org.valdroz.vscript.EvaluationException;

public class ErrorHandlingExample {

    public static void main(String[] args) {
        VariantContainer container = new DefaultVariantContainer();

        // Division by zero
        try {
            Variant result = new EquationEval("10 / 0").eval(container);
        } catch (EvaluationException e) {
            System.err.println("Error: " + e.getMessage());
            // Handle error appropriately
        }

        // Invalid boolean arithmetic
        try {
            Variant result = new EquationEval("true + false").eval();
        } catch (EvaluationException e) {
            System.err.println("Cannot perform arithmetic on booleans");
        }

        // Safe evaluation with null substitution
        String userExpression = getUserInput();  // May be null or invalid
        try {
            String safeExpr = userExpression != null ? userExpression : "0";
            Variant result = new EquationEval(safeExpr).eval(container);
            // Use result
        } catch (EvaluationException e) {
            // Fallback value
            Variant fallback = Variant.fromInt(0);
        }
    }

    private static String getUserInput() {
        // Get user input
        return null;
    }
}
```

### Validating Function Parameters

```java
try {
    // Missing parameter
    Variant result = new EquationEval("first(\"Hello\")").eval();
} catch (EvaluationException e) {
    System.err.println(e.getMessage());
    // "Function `first` takes 2 parameters. E.g. first(\"Hello\", 2) will result in \"He\""
}

try {
    // Invalid parameter type
    Variant result = new EquationEval("sqrt(\"not a number\")").eval();
} catch (EvaluationException e) {
    System.err.println("Invalid parameter type");
}
```

---

## Best Practices

### 1. Reuse Parsed Expressions

Parse expressions once and reuse them:

```java
public class ExpressionCache {
    private final Map<String, Node> cache = new ConcurrentHashMap<>();

    public Variant evaluate(String expression, VariantContainer vars) {
        Node node = cache.computeIfAbsent(expression, EquationEval::parse);
        return node.execute(vars);
    }
}
```

### 2. Separate Configuration from Evaluation

```java
public class Calculator {
    static {
        // Configure once at startup
        Configuration.setDecimalScale(2);
        Configuration.setRoundingMode(RoundingMode.HALF_UP);
    }

    public Variant calculate(String expression, VariantContainer vars) {
        return new EquationEval(expression).eval(vars);
    }
}
```

### 3. Use Type-Safe Variable Access

```java
public class TypeSafeAccess {

    public static double getDoubleOrDefault(VariantContainer vars,
                                           String name, double defaultValue) {
        if (!vars.contains(name)) {
            return defaultValue;
        }
        Variant v = vars.getVariant(name);
        return v.isNull() ? defaultValue : v.asNumeric().doubleValue();
    }

    public static String getStringOrDefault(VariantContainer vars,
                                          String name, String defaultValue) {
        if (!vars.contains(name)) {
            return defaultValue;
        }
        Variant v = vars.getVariant(name);
        return v.isNull() ? defaultValue : v.asString();
    }
}
```

### 4. Build Complex Expressions Safely

```java
public class ExpressionBuilder {

    public static String buildPricingExpression(boolean includeTax,
                                               boolean applyDiscount) {
        StringBuilder expr = new StringBuilder("price * quantity");

        if (applyDiscount) {
            expr.append(" * (1 - discount)");
        }

        if (includeTax) {
            expr.append(" * (1 + taxRate)");
        }

        return expr.toString();
    }
}
```

### 5. Validate Before Evaluation

```java
public class SafeEvaluator {

    public Variant safeEval(String expression, VariantContainer vars) {
        // Get required variables
        EquationEval eq = new EquationEval(expression);
        NodeStats stats = eq.getStats();

        // Check all required variables exist
        for (String varName : stats.referencedVariables()) {
            if (!vars.contains(varName)) {
                throw new IllegalArgumentException(
                    "Missing required variable: " + varName
                );
            }
        }

        return eq.eval(vars);
    }
}
```

---

## Next Steps

- Review [Syntax Guide](SYNTAX.md) for vscript language reference
- Check [Built-in Functions](FUNCTIONS.md) for complete function documentation
- Return to [Main Documentation](README.md)
