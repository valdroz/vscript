# Vscript - Equation Interpreter 

Vscript is an extendable and runtime safe equation interpreter capable to evaluate arithmetic 
and logical expression.



## Example

```java
import org.valdroz.vscript.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat; 

public class Example {
    public static void main(String[] args) {
         // Configure decimal scale for numeric operations to 2
        Configuration.setDecimalScale(2);

        // Initialize variant container with one variable as
        // myVar1 = 5.2        
        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("myVar1", Variant.fromDouble(5.2));

        // Execute expression with given variant container
        Variant var = EquationEval.parse("res = 3 + myVar1 * sqrt(4); res == 13.4").execute(container);
        Variant res = container.getVariant("res");

        // Evaluation response expected to be boolean `true` due to `res == 13.4`
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        // Expecting res to be of numeric type since it is a result of algebraic expression
        assertThat(res.isNumeric(), is(true));
        
        // `res` must be equal to 13.4
        assertThat(res.asNumeric().doubleValue(), is(13.4));

        // String should have two places after decimal due to `Configuration.setDecimalScale(2)`
        assertThat(res.asString(), is("13.40"));

        System.out.println(var); // prints: Boolean Variant of true
        System.out.println(res); // prints: Numeric Variant of 13.40
    }
}
```



## Syntax

Supported numeric operators `+ -  * /`, binary operators
 `&, |, ^`, logical operators ` ==, >, <, >=, <=, !=, ! ` and `=` assignment; `?` null substitutions.


### Reserved Keywords

- `true`        - Boolean true.
- `false`       - Boolean false.
- `PI`			- Mathematical PI constant of `3.1415926535897932384626433832795`.
- `null`		- Uninitialized variantValue.


### Variable types

Supported variant variantValue types:

- string - E.g. `"Hello world!"`.
- numeric - E.g. `2.65` backed by `java.math.BigDecimal`
- boolean - E.g. `true` or `false`
- array - `{10.35, "Hello", {30, 2.3}}`. Array elements can be of mixed types.

## Predefined functions:

Math functions:
- sin(n)				: Calculates sine of `n`.
- cos(n)				: Calculates the cosine of `n`.
- asin(n)				: Calculates the arcsine of `n`.
- acos(n)				: Calculates the arccosine of `n`.
- tan(n)				: Calculates the tangent of `n`.
- atan(n)				: Calculates the arctangent of `n`.
- abs(n)				: Calculates the absolute variantValue of `n`.
- neg(n)				: Calculates the negative variantValue of `n`.
- sqrt(n)				: Calculates the square root of `n`.
- log(n)				: Returns the natural logarithm (base e) of `n`.
- exp(n)				: Returns the exponential number `e` raised to the power of a `n`.
- max(n1,....,nN)       : Returns largest value digit from provided numeric value list. 
- min(n1,....,nN)       : Returns smalled value digit from provided numeric value list.
- avg(n1,....,nN)       : Finds average value from provided numeric numeric value list.  
- median(n1,....,nN)    : Finds median value from provided numeric numeric value list.

Date functions:
- day()				: Returns current day of month (1-31).
- month()			: Returns current month (1-12).
- year()			: Returns current year.
- day_of_year()		: Returns current day of year.
- days_in_month(n)	: Returns maximum days in the month, where `n` is disposition from current month.
                    E.g. to get maximum days in the previous month, the `n` values must be -1.
- iso(str)          : Parses input ISO8601 date or date with time and returns millis.
- now()             : Current time in millis.
- days_before_now(x) : (`x` is string or numeric) Parses input ISO8601 date/timestamp or takes time in milliseconds since January 1, 1970 UTC and returns number of days passed until now. 					
- hours_before_now(x) : (`x` is string or numeric) Parses input ISO8601 date/timestamp or takes time in milliseconds since January 1, 1970 UTC and returns number of hours passed until now.
- minutes_before_now(x) : (`x` is string or numeric) Parses input ISO8601 date/timestamp or takes time in milliseconds since January 1, 1970 UTC and returns number of minutes passed until now.

Misc. functions:
- size(x)			: Length of string variable variantValue or count of elements in array.
- is_null(x)		: Returns `true` if variantValue not set, `false` otherwise.
- is_numeric(x)		: Returns `true` if variantValue is number, `false` otherwise.
- is_string(x)		: Returns `true` if variantValue is string, `false` otherwise.
- is_array(x)		: Returns `true` if variantValue is array, `false` otherwise.
- to_array(x1,x2,...,xN) : Makes an array populated with provided values.

## null Substitutions

Default value for missing (`null`) variables can be defined as follows:

```java

var1?"default"

```

... `null` variable substitution can also be chained like so:


```java

var1?var2?var3?year()

```

... yes, function call can be used for variantValue substitutions aswell.

## Extending to meet your needs

Vscript allows defining custom functions to meet your needs.
Simplified example of adding one custom function is shown here:    

```java
import org.valdroz.vscript.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExampleCustomFunction {
    public static void main(String[] args) {
        VariantContainer variantContainer = new DefaultVariantContainer();

        DefaultRunBlock masterRunBlock = new DefaultRunBlock();
        masterRunBlock.registerFunction("custom_multiply(first, second)",
                (lvc) -> lvc.getVariant("first").multiply(lvc.getVariant("second")));

        Variant result = new EquationEval("2 + custom_multiply(3, 4)").withMasterBlock(masterRunBlock)
                .eval(variantContainer);

        assertThat(result.asNumeric().doubleValue(), is(14.0));
    }
}
```
