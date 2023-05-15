# Changes

### \[v3.10.0\] 2023-05-12

Added:

- `format_ts` function is added. Sample `format_ts(timestampToBeFormatted, formatToApply, optionalTimeZone)`

### \[v3.9.0\] 2023-01-26

Added:

- Added `days_since_weekday` function for calculating how many days have passed since a provided day.
  - This function recognizes days of the week numerically, ex: Monday = 1, Tuesday = 2. E.g.:
  - `days_since_weekday(1)` will return `0` if it is called on Monday, and will return `1` if it is called on Tuesday.
- Added `floor_mod` Math function.

### \[v3.8.1\] 2021-09-17

Updated:

- Updated `min`, `max` to support other than numeric types.
- Corrected defect with error text formatting.  

### \[v3.8.0\] 2021-09-15

Added:

- Added `first`, `skip` and `last` new functions for string manipulation. E.g.:
  - `first("text", 2)` takes first two characters and yields `"te"`;
  - `skip("text", 1)` skips one character and return remaining of string as `"ext"`;  
  - and `last("text", 2)` takes last two characters and yields `"xt"`.    
- Added `min`, `max`,`avg` and `median` math functions.

### \[v3.7.0\] 2021-08-16

Added:

- `if` function is added. Sample `if(condition , truestatement , falsestatement)`

### \[v3.6.0\] 2021-07-26

Added:

- `DataSetIndex` now can let consumer to know if given variable path has multiple data permutations.
  
### \[v3.5.0\] 2021-07-10

Added:

- Implemented a new JSON Data Set indexer `DataSetIndex.index(jsonObject)`. It is a substantially 
  faster alternative to `JsonDataSetMaker`.  

### \[v3.4.0\] 2021-04-16

Updated:

- Operator `!` on `null` values will now yield `null`, as suppose to `true`.

Added:

- Added `CHANGELOG.md` file. 
- KEEP_COMPLEX_ARRAYS mode for JsonDataSetMaker. This mode allows preserving object 
  arrays for easier JSON reconstructions.
  
  