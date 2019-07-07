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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Variant object.
 *
 * @author Valerijus Drozdovas
 */
public class Variant {
    public static final byte VT_NONE = 0;
    public static final byte VT_NUMERIC = 1;
    public static final byte VT_STRING = 2;
    public static final byte VT_ARRAY = 3;

    /**
     * Variant value type
     */
    private byte vt = VT_NONE;

    private double numericValue = 0;
    private String stringValue = "";
    private List<Variant> arrayValue = null;

    public Variant() {
        makeNull();
    }

    public Variant(double value) {
        setValue(value);
    }

    public Variant(String value) {
        setValue(value);
    }

    public Variant(Variant value) {
        setValue(value);
    }

    /**
     * @return numeric value
     */
    public double getDouble() {
        return numericValue;
    }

    /**
     * @return string value
     */
    public String getString() {
        return stringValue;
    }

    public void setValue(String value) {
        vt = VT_STRING;
        stringValue = value;
    }

    public void setValue(double value) {
        vt = VT_NUMERIC;
        numericValue = value;
    }

    public void setValue(int value) {
        setValue((double) value);
    }

    public void setValue(Variant value) {
        vt = value.vt;
        switch (vt) {
            case VT_NUMERIC:
                numericValue = value.numericValue;
                break;
            case VT_STRING:
                stringValue = value.stringValue;
                break;
            case VT_ARRAY: {
                arrayValue = new ArrayList<>();
                value.arrayValue.forEach(v -> {
                    Variant nv = new Variant(v);
                    arrayValue.add(nv);
                });
            }
        }
    }

    public void makeNull() {
        vt = VT_NONE;
        this.arrayValue = null;
        this.stringValue = "";
        this.numericValue = 0;
    }

    public byte getValueType() {
        return vt;
    }

    /**
     * Convert and return variant value as string.
     * @return variant value converted to string.
     */
    @Override
    public String toString() {
        if (vt == VT_STRING) return stringValue;
        else if (vt == VT_NUMERIC) {
            if ((numericValue - (int) numericValue) == 0.0)
                return Integer.toString((int) numericValue);
            else
                return Double.toString(numericValue);
        } else if (vt == VT_ARRAY && arrayValue != null) {
            String ret = "{";
            int size = arrayValue.size();
            for (int i = 0; i < size; ++i) {
                if (i > 0) ret += ',';
                ret += arrayValue.get(i).toString();
            }
            ret += '}';
            return ret;
        }
        return "null";
    }

    /**
     * @return variant value converted to double.
     */
    public double toDouble() {
        switch (getValueType()) {
            case VT_NUMERIC:
                return numericValue;
            case VT_STRING:
                try {
                    return Double.valueOf(stringValue);
                } catch (Exception ex) {
                    return 0;
                }
        }
        return 0;
    }

    /**
     * @return `true` if variant interpolated value is not equals to zero.
     */
    public boolean toBoolean() {
        return toDouble() != 0;
    }

    private void ensureArrayCapacity(int size) {
        if (arrayValue == null) {
            arrayValue = new ArrayList<>();
        }

        if (size > arrayValue.size()) {
            int diff = size - arrayValue.size();
            for (int i = 0; i < diff; ++i) {
                arrayValue.add(new Variant());
            }
        }
    }

    public void setArrayItem(int item, Variant value) {
        vt = VT_ARRAY;
        ensureArrayCapacity(item + 1);
        arrayValue.set(item, new Variant(value));
    }

    public Variant getArrayItem(int item) {
        if (arrayValue != null && item >= 0 && item < arrayValue.size()) {
            return new Variant(arrayValue.get(item));
        }
        return new Variant();
    }

    /**
     * @return value size. If variant type is array, array size will be returned,
     * value toString() size wil be returned otherwise,
     */
    public int size() {
        if (vt == VT_ARRAY && arrayValue != null)
            return arrayValue.size();
        else if (vt != VT_NONE)
            return toString().length();
        else
            return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variant variant = (Variant) o;
        if (vt != variant.vt) return false;
        switch (vt) {
            case VT_STRING:
                return Objects.equals(stringValue, variant.stringValue);
            case VT_NUMERIC:
                return Double.compare(variant.numericValue, numericValue) == 0;
            case VT_ARRAY:
                if (this.arrayValue.size() != variant.arrayValue.size()) return false;
                Iterator<Variant> i1 = this.arrayValue.iterator();
                Iterator<Variant> i2 = variant.arrayValue.iterator();
                while (i1.hasNext()) {
                    if (!i1.next().equals(i2.next())) return false;
                }
                break;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vt, numericValue, stringValue, arrayValue);
    }
}
