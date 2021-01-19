package org.valdroz.vscript;

import com.google.common.base.Joiner;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.math.BigDecimal;
import java.util.List;

public class VariantMatchers {

    public static org.hamcrest.Matcher<Variant> booleanOf(final boolean value) {
        return new BaseMatcher<Variant>() {
            @Override
            public boolean matches(Object item) {
                Variant v = (Variant) item;
                return v.isBoolean() && v.asBoolean() == value;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is boolean variant of " + value);
            }
        };
    }


    public static org.hamcrest.Matcher<Variant> stringOf(final String value) {
        return new BaseMatcher<Variant>() {
            @Override
            public boolean matches(Object item) {
                Variant v = (Variant) item;
                return v.isString() && v.asString().equals(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is string variant of " + value);
            }
        };
    }

    public static org.hamcrest.Matcher<Variant> numericOf(final double value) {
        return numericOf(BigDecimal.valueOf(value));
    }

    public static org.hamcrest.Matcher<Variant> numericOf(final long value) {
        return numericOf(BigDecimal.valueOf(value));
    }

    public static org.hamcrest.Matcher<Variant> numericOf(final BigDecimal value) {
        return new BaseMatcher<Variant>() {
            @Override
            public boolean matches(Object item) {
                Variant v = (Variant) item;
                return v.isNumeric() && v.asNumeric().compareTo(value) == 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is numeric variant of " + value);
            }
        };
    }

    public static org.hamcrest.Matcher<Variant> arrayOfSize(final int size) {
        return new BaseMatcher<Variant>() {
            @Override
            public boolean matches(Object item) {
                Variant v = (Variant) item;
                return v.isArray() && v.size() == size;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is array variant of size " + size);
            }
        };
    }

    public static org.hamcrest.Matcher<Variant> arrayOf(final Variant... items) {
        return new BaseMatcher<Variant>() {
            @Override
            public boolean matches(Object item) {
                Variant v = (Variant) item;
                if (!v.isArray()) {
                    return false;
                }
                List<Variant> variantList = v.asArray();
                if (variantList.size() != items.length) {
                    return false;
                }

                for (int i = 0; i < variantList.size(); ++i) {
                    if (variantList.get(i).compareTo(items[i]) != 0) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is array variant of [" + Joiner.on(", ").join(items) + "]");
            }
        };
    }

    public static org.hamcrest.Matcher<Variant> nullVariant() {
        return new BaseMatcher<Variant>() {
            @Override
            public boolean matches(Object item) {
                Variant v = (Variant) item;
                return v.isNull();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is null variant");
            }
        };
    }

}
