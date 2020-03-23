/*
 * Copyright 2000 Valerijus Drozdovas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.valdroz.vscript;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.valdroz.vscript.Configuration.*;

/**
 * Variant object.
 *
 * @author Valerijus Drozdovas
 */
public abstract class Variant implements Comparable<Variant> {
    static final String EMPTY_STRING = "";

    private static final Variant NULL_VARIANT = new NullVariant();


    @Deprecated
    public static void setDecimalScale(int decimalScale) {
        Configuration.setDecimalScale(decimalScale);
    }

    @Deprecated
    public static void setRoundingMode(RoundingMode roundingMode) {
        Configuration.setRoundingMode(roundingMode);
    }

    @Deprecated
    public static void setCaseSensitive(boolean caseSensitive) {
        Configuration.setCaseSensitive(caseSensitive);
    }

    public abstract BigDecimal asNumeric();

    public abstract Boolean asBoolean();

    public abstract String asString();

    public List<Variant> asArray() {
        List<Variant> arr = new ArrayList<>();
        arr.add(this);
        return arr;
    }

    public int size() {
        return -1;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isString() {
        return false;
    }

    public boolean isNumeric() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public static Variant fromDouble(double value) {
        return new NumericVariant(BigDecimal.valueOf(value));
    }

    public static Variant fromInt(int value) {
        return new NumericVariant(BigDecimal.valueOf(value));
    }

    public static Variant fromLong(long value) {
        return new NumericVariant(BigDecimal.valueOf(value));
    }

    public static Variant fromBigDecimal(BigDecimal value) {
        return new NumericVariant(value);
    }

    public static Variant fromBigDecimal(String value) {
        return new NumericVariant(new BigDecimal(value));
    }

    public static Variant fromString(String value) {
        return new StringVariant(value);
    }

    public static Variant fromBoolean(boolean value) {
        return new BooleanVariant(value);
    }

    public static Variant fromArray(List<Variant> value) {
        return new ArrayVariant(value);
    }

    public static Variant emptyArray() {
        return new ArrayVariant();
    }


    public static Variant fromVariant(Variant variant) {
        if (variant == null) {
            return nullVariant();
        }
        return variant;
    }

    public static Variant nullVariant() {
        return NULL_VARIANT;
    }

    public static Variant setArrayItem(Variant variant, int index, Variant value) {
        if (variant.isArray()) {
            ArrayVariant arrayVariant = (ArrayVariant) variant;
            return arrayVariant.setArrayItem(index, sanitize(value));
        } else {
            return new ArrayVariant().setArrayItem(0, variant).setArrayItem(index, sanitize(value));
        }
    }

    public static Variant getArrayItem(Variant variant, int index) {
        if (variant != null && variant.isArray()) {
            ArrayVariant arrayVariant = (ArrayVariant) variant;
            return arrayVariant.getArrayItem(index);
        }
        return nullVariant();
    }

    public static Variant sanitize(Variant variant) {
        if (variant == null) {
            return nullVariant();
        }
        return variant;
    }

    public static Variant sanitize(Variant variant, Variant substitution) {
        if (variant == null) {
            return substitution;
        }
        return variant;
    }

    // Operator methods
    public abstract Variant multiply(Variant variant);

    public abstract Variant add(Variant variant);

    public abstract Variant minus(Variant variant);

    public abstract Variant divide(Variant variant);

    public abstract Variant pow(Variant variant);

    public abstract Variant sqrt();

    public abstract Variant abs();

    public abstract Variant negate();

    protected RuntimeException invalidOperator(String operator) {
        return new UnsupportedOperationException("Invalid " + operator + " operator on [" + this + "]");
    }


    private static class NumericVariant extends Variant {

        private BigDecimal value;

        NumericVariant(BigDecimal value) {
            //this.value = value.setScale(decimalScale, roundingMode);
            this.value = value;
        }

        @Override
        public BigDecimal asNumeric() {
            return value;
        }

        @Override
        public Boolean asBoolean() {
            return value.compareTo(BigDecimal.ZERO) != 0;
        }

        @Override
        public String asString() {
            return value.toString();
        }

        @Override
        public boolean isNumeric() {
            return true;
        }

        @Override
        public int compareTo(Variant o) {
            return value.compareTo(sanitize(o).asNumeric());
        }

        @Override
        public Variant multiply(Variant variant) {
            return fromBigDecimal(value.multiply(sanitize(variant).asNumeric()));
        }

        @Override
        public Variant add(Variant variant) {
            return fromBigDecimal(value.add(sanitize(variant).asNumeric()));
        }

        @Override
        public Variant minus(Variant variant) {
            return Variant.fromBigDecimal(value.subtract(sanitize(variant).asNumeric()));
        }

        @Override
        public Variant divide(Variant variant) {
            BigDecimal divisor = sanitize(variant).asNumeric();
            if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                throw new UnsupportedOperationException("Division by zero");
            }
            return Variant.fromBigDecimal(
                    value.setScale(getDecimalScale(), getRoundingMode())
                            .divide(divisor.setScale(getDecimalScale(), getRoundingMode()), getRoundingMode())
                            .stripTrailingZeros());
        }

        @Override
        public Variant pow(Variant variant) {
            return Variant.fromBigDecimal(value.pow(sanitize(variant).asNumeric().intValue()));
        }

        @Override
        public Variant sqrt() {
            return Variant.fromDouble(Math.sqrt(value.doubleValue()));
        }

        @Override
        public Variant abs() {
            return Variant.fromDouble(Math.abs(value.doubleValue()));
        }

        @Override
        public Variant negate() {
            return Variant.fromBigDecimal(value.negate());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Variant)) return false;
            Variant that = (Variant) o;
            if (that.isNull() || that.isArray()) return false;

            return value.compareTo(that.asNumeric()) == 0;

            //return Objects.equals(value, that.asNumeric());
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Numeric Variant of " + value;
        }
    }


    private static class StringVariant extends Variant {
        private final String value;

        public StringVariant(String value) {
            this.value = value;
        }

        @Override
        public BigDecimal asNumeric() {
            try {
                return new BigDecimal(value).setScale(getDecimalScale(), getRoundingMode());
            } catch (Exception ex) {
                return BigDecimal.ZERO;
            }
        }

        @Override
        public Boolean asBoolean() {
            return "true".equalsIgnoreCase(value);
        }

        @Override
        public String asString() {
            return value;
        }

        @Override
        public int size() {
            return value.length();
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public int compareTo(Variant o) {
            return value.compareTo(sanitize(o).asString());
        }

        @Override
        public Variant multiply(Variant variant) {
            if (variant != null && variant.isNumeric()) {
                try {
                    return Variant.fromBigDecimal(value).multiply(variant);
                } catch (Exception ex) {
                    // do nothing
                }
            }
            throw invalidOperator("multiply");
        }

        @Override
        public Variant add(Variant variant) {
            if (variant != null && variant.isNumeric()) {
                try {
                    return Variant.fromBigDecimal(value).add(variant);
                } catch (Exception ex) {
                    // do nothing
                }
            }
            return fromString(asString() + sanitize(variant).asString());
        }

        @Override
        public Variant minus(Variant variant) {
            if (variant != null && variant.isNumeric()) {
                try {
                    return Variant.fromBigDecimal(value).minus(variant);
                } catch (Exception ex) {
                    // do nothing
                }
            }
            throw invalidOperator("minus");
        }

        @Override
        public Variant divide(Variant variant) {
            if (variant != null && variant.isNumeric()) {
                try {
                    return Variant.fromBigDecimal(value).divide(variant);
                } catch (Exception ex) {
                    // do nothing
                }
            }
            throw invalidOperator("divide");
        }

        @Override
        public Variant pow(Variant variant) {
            throw invalidOperator("pow");
        }

        @Override
        public Variant sqrt() {
            throw invalidOperator("sqrt");
        }

        @Override
        public Variant abs() {
            throw invalidOperator("sqrt");
        }

        @Override
        public Variant negate() {
            throw invalidOperator("negate");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Variant)) return false;
            Variant that = (Variant) o;
            if (isCaseSensitive()) {
                return Objects.equals(value, that.asString());
            }
            return value.equalsIgnoreCase(that.asString());
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "String Variant of \"" + value + "\"";
        }
    }

    private static class BooleanVariant extends Variant {
        private final Boolean value;

        public BooleanVariant(Boolean value) {
            this.value = value;
        }

        @Override
        public BigDecimal asNumeric() {
            return value ? BigDecimal.ONE : BigDecimal.ZERO;
        }

        @Override
        public Boolean asBoolean() {
            return value;
        }

        @Override
        public String asString() {
            return value.toString();
        }

        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public int compareTo(Variant o) {
            return value.compareTo(sanitize(o).asBoolean());
        }

        @Override
        public Variant multiply(Variant variant) {
            throw invalidOperator("multiply");
        }

        @Override
        public Variant add(Variant variant) {
            throw invalidOperator("add");
        }

        @Override
        public Variant minus(Variant variant) {
            throw invalidOperator("minus");
        }

        @Override
        public Variant divide(Variant variant) {
            throw invalidOperator("divide");
        }

        @Override
        public Variant pow(Variant variant) {
            throw invalidOperator("pow");
        }

        @Override
        public Variant sqrt() {
            throw invalidOperator("sqrt");
        }

        @Override
        public Variant abs() {
            throw invalidOperator("abs");
        }

        @Override
        public Variant negate() {
            return fromBoolean(!value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Variant)) return false;
            Variant that = (Variant) o;
            return Objects.equals(value, that.asBoolean());
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Boolean Variant of " + value;
        }
    }

    private static class NullVariant extends Variant {
        @Override
        public BigDecimal asNumeric() {
            return BigDecimal.ZERO;
        }

        @Override
        public Boolean asBoolean() {
            return Boolean.FALSE;
        }

        @Override
        public String asString() {
            return EMPTY_STRING;
        }

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public int compareTo(Variant o) {
            if (sanitize(o).isNull()) {
                return 0;
            }
            return -1;
        }

        @Override
        public Variant multiply(Variant variant) {
            return nullVariant();
        }

        @Override
        public Variant add(Variant variant) {
            return nullVariant();
        }

        @Override
        public Variant minus(Variant variant) {
            return nullVariant();
        }

        @Override
        public Variant divide(Variant variant) {
            return nullVariant();
        }

        @Override
        public Variant pow(Variant variant) {
            return nullVariant();
        }

        @Override
        public Variant sqrt() {
            return nullVariant();
        }

        @Override
        public Variant abs() {
            return nullVariant();
        }

        @Override
        public Variant negate() {
            return nullVariant();
        }

        @Override
        public String toString() {
            return "Null Variant";
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Variant)) return false;
            return ((Variant) obj).isNull();
        }
    }

    private static class ArrayVariant extends Variant {
        private final List<Variant> valueArray;

        public ArrayVariant() {
            this.valueArray = new ArrayList<>();
        }

        public ArrayVariant(List<Variant> value) {
            this.valueArray = new ArrayList<>();
            this.valueArray.addAll(value);
        }

        @Override
        public BigDecimal asNumeric() {
            return BigDecimal.valueOf(valueArray.size()).setScale(getDecimalScale(), getRoundingMode());
        }

        @Override
        public Boolean asBoolean() {
            return valueArray.size() > 0;
        }

        @Override
        public String asString() {
            final StringBuilder ret = new StringBuilder("{");
            valueArray.forEach(variant -> {
                if (ret.length() > 1) {
                    ret.append(',');
                }
                ret.append(variant.asString());
            });
            ret.append('}');
            return ret.toString();
        }

        @Override
        public List<Variant> asArray() {
            return valueArray;
        }

        @Override
        public int size() {
            return valueArray.size();
        }

        @Override
        public boolean isArray() {
            return true;
        }

        public ArrayVariant setArrayItem(int index, Variant value) {
            for (int i = 0; i < index - valueArray.size() + 1; ++i) {
                valueArray.add(nullVariant());
            }
            valueArray.set(index, Variant.sanitize(value));
            return this;
        }

        public Variant getArrayItem(int index) {
            if (index >= 0 && index < valueArray.size()) {
                return valueArray.get(index);
            }
            return nullVariant();
        }

        @Override
        public int compareTo(Variant o) {
            if (o.isArray()) {
                return Integer.compare(size(), o.size());
            }
            if (size() == 0) {
                return -1;
            }
            return 1;
        }

        @Override
        public Variant multiply(Variant variant) {
            throw invalidOperator("multiply");
        }

        @Override
        public Variant add(Variant variant) {
            List<Variant> newValue = new ArrayList<>(valueArray);
            if (variant != null && variant.isArray()) {
                ArrayVariant ar = (ArrayVariant) variant;
                newValue.addAll(ar.valueArray);
            } else {
                newValue.add(sanitize(variant));
            }
            return Variant.fromArray(newValue);
        }

        @Override
        public Variant minus(Variant variant) {
            List<Variant> newValue = new ArrayList<>(valueArray);
            newValue.removeAll(sanitize(variant).asArray());
            return Variant.fromArray(newValue);
        }

        @Override
        public Variant divide(Variant variant) {
            throw invalidOperator("divide");
        }

        @Override
        public Variant pow(Variant variant) {
            throw invalidOperator("pow");
        }

        @Override
        public Variant sqrt() {
            throw invalidOperator("sqrt");
        }

        @Override
        public Variant abs() {
            throw invalidOperator("abs");
        }

        @Override
        public Variant negate() {
            throw invalidOperator("negate");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Variant)) return false;
            Variant v = (Variant) o;
            if (v.isArray()) {
                return valueArray.containsAll(v.asArray());
            }
            return valueArray.contains(v);
        }

        @Override
        public int hashCode() {
            return Objects.hash(valueArray);
        }

        @Override
        public String toString() {
            return "Array Variant of " + asString();
        }
    }
}
