# Vscript - equation interpreter 
What started as a student project now turned in to simple and handy expression evaluator, or even scripting engine. 

## Syntax

Supported numeric operators `+ -  * / ^`, binary operators
 `&, |`, logical operators ` ==, >, <, >=, <=, !=, ! `


### Reserved Keywords

- `true`		- `1.0` value.
- `false`		- `0.0` value.
- `PI`			- `3.1415926535897932384626433832795`.
- `null`		- Value with no type. Can also be used in assignments.


### Variable types

Variable can have three value types:

- string - E.g. `"Hello"`.
- numeric - E.g. `2.65`. Numeric values can be used as boolean where Zero stands for `false`, otherwise `true`.
- array - `{10.35, "Hello", {30, 2.3}}`. Array elements can be of mixed types.



## Predefined functions:

Math functions:
- sin(n)				- Calculate sine of `n`.
- cos(n)				- Calculate the cosine of `n`.
- asin(n)				- Calculates the arcsine of `n`.
- acos(n)				- Calculates the arccosine of `n`.
- tan(n)				- Calculate the tangent of `n`.
- atan(n)				- Calculates the arctangent of `n`.
- abs(n)				- Calculates the absolute value of `n`.
- neg(n)				- Calculates the negative value of `n`.
- sqrt(n)				- Calculates the square root of `n`.
- log(n)				- Returns the natural logarithm (base e) of `n`.
- exp(n)				- Returns the exponential number `e` raised to the power of a `n`.

Date functions:
- day()				- Returns current day of month (1-31).
- month()			- Returns current month (1-12).
- year()			- Returns current year.
- day_of_year()		- Return current day of year.
- days_in_month(n)	- Return maximum days in the month, where `n` is disposition from current month.
					E.g. to get maximum days in the previous month, the `n` values must be -1.
- iso(str)          - Parses input ISO8601 date or date with time and returns millis.
- now()             - Current time in millis.
- days_before_now(x) - (`x` is string or numeric) Parses input ISO8601 date/timestamp or takes time in milliseconds since January 1, 1970 UTC and returns number of days passed until now. 					
- hours_before_now(x) - (`x` is string or numeric) Parses input ISO8601 date/timestamp or takes time in milliseconds since January 1, 1970 UTC and returns number of hours passed until now.

Misc. functions:
- size(x)			- Length of string variable value or count of elements in array.
- is_null(x)		- Returns `1` if value not set, `0` otherwise.
- is_numeric(x)		- Returns `1` if value is number, `0` otherwise.
- is_string(x)		- Returns `1` if value is string, `0` otherwise.
- is_array(x)		- Returns `1` if value is array, `0` otherwise.

## Example

```java

DefaultVariantContainer container = new DefaultVariantContainer();
container.setVariant("movie.price", new Variant(5));

Variant var = new EquationEval("(3 + movie.price * sqrt(4)) >= 13.0").eval(container);
assertThat(var.toBoolean(), is(true));

```