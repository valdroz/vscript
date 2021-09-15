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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.valdroz.vscript.Configuration.*;
import static org.valdroz.vscript.Constants.*;
import static org.valdroz.vscript.EquationParser.functionNameFromCode;

/**
 * Variant object.
 *
 * @author Valerijus Drozdovas
 */
public abstract class Variant implements Comparable<Variant> {
    static final String EMPTY_STRING = "";

    private static final Variant NULL_VARIANT = new NullVariant();

    private static final Variant EMPTY_STRING_VARIANT = Variant.fromString(EMPTY_STRING);

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
        return Lists.newArrayList(this);
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

    public static Variant emptyStringVariant() {
        return EMPTY_STRING_VARIANT;
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

    public abstract Variant negate();

    public abstract Variant mfunc(int func);

    protected RuntimeException invalidOperator(String operator) {
        return new EvaluationException("Invalid " + operator + " operator on [" + this + "]");
    }

    protected static Variant mfunc(int func, double dec) {
        switch (func) {
            case NT_MF_SIN:
                return Variant.fromDouble(Math.sin(dec));
            case NT_MF_COS:
                return Variant.fromDouble(Math.cos(dec));
            case NT_MF_ASIN:
                return Variant.fromDouble(Math.asin(dec));
            case NT_MF_ACOS:
                return Variant.fromDouble(Math.acos(dec));
            case NT_MF_TAN:
                return Variant.fromDouble(Math.tan(dec));
            case NT_MF_ATAN:
                return Variant.fromDouble(Math.atan(dec));
            case NT_MF_EXP:
                return Variant.fromDouble(Math.exp(dec));
            case NT_MF_LN:
            case NT_MF_LOG:
                return Variant.fromDouble(Math.log(dec));
            case NT_MF_SQRT:
                return Variant.fromDouble(Math.sqrt(dec));
            case NT_MF_ABS:
                return Variant.fromDouble(Math.abs(dec));
        }
        return Variant.nullVariant();
    }


    private static class NumericVariant extends Variant {

        private BigDecimal value;

        NumericVariant(BigDecimal value) {
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
                throw new EvaluationException("Division by zero");
            }
            return Variant.fromBigDecimal(
                    value.setScale(getDecimalScale(), getRoundingMode())
                            .divide(divisor.setScale(getDecimalScale(), getRoundingMode()), getRoundingMode())
                            .stripTrailingZeros());
        }

        @Override
        public Variant mfunc(int func) {
            return mfunc(func, value.doubleValue());
        }

        @Override
        public Variant pow(Variant variant) {
            return Variant.fromBigDecimal(value.pow(sanitize(variant).asNumeric().intValue()));
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
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return value.toString();
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
        public Variant mfunc(int func) {
            try {
                return mfunc(func, new BigDecimal(value).doubleValue());
            } catch (Exception ex) {
                throw new EvaluationException("Invalid argument " + this + " for function " + functionNameFromCode(func));
            }
        }


        @Override
        public Variant pow(Variant variant) {
            if (variant != null && variant.isNumeric()) {
                try {
                    return Variant.fromBigDecimal(value).pow(variant);
                } catch (Exception ex) {
                    // do nothing
                }
            }
            throw invalidOperator("pow");
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
                if (that.isArray()) {
                    return that.asArray().contains(this);
                } else {
                    return Objects.equals(value, that.asString());
                }
            } else {
                if (that.isArray()) {
                    for (Variant thatV : that.asArray()) {
                        if (value.equalsIgnoreCase(thatV.asString())) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    return value.equalsIgnoreCase(that.asString());
                }
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "\"" + value + "\"";
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
        public Variant mfunc(int func) {
            throw new EvaluationException("Invalid argument " + this + " for function " + functionNameFromCode(func));
        }

        @Override
        public Variant pow(Variant variant) {
            throw invalidOperator("pow");
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
            return value.toString();
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
        public Variant negate() {
            return nullVariant();
        }

        @Override
        public String toString() {
            return "null";
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public Variant mfunc(int func) {
            return Variant.nullVariant();
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
            while (valueArray.size() < index + 1) {
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
        public Variant mfunc(int func) {
            List<Variant> newValue = new ArrayList<>(valueArray);
            for (int i = 0; i < newValue.size(); ++i) {
                newValue.set(i, newValue.get(i).mfunc(func));
            }
            return Variant.fromArray(newValue);
        }

        @Override
        public Variant pow(Variant variant) {
            List<Variant> newValue = new ArrayList<>(valueArray);
            for (int i = 0; i < newValue.size(); ++i) {
                newValue.set(i, newValue.get(i).pow(variant));
            }
            return Variant.fromArray(newValue);
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
            if (that.isArray()) {
                return valueArray.containsAll(that.asArray());
            }
            return valueArray.contains(that);
        }

        @Override
        public int hashCode() {
            return Objects.hash(valueArray);
        }

        @Override
        public String toString() {
            return "[" + Joiner.on(", ").join(valueArray) + "]";
        }
    }
}
