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

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Base interpretable node.
 *
 * @author Valerijus Drozdovas
 */
class BaseNode implements Node, Constants {
    private final String id;
    private int operation = 0;
    private String name = "";

    private BaseNode valueSubstitution = null;
    private BaseNode leftNode = null;
    private BaseNode rightNode = null;
    private List<BaseNode> params = null;

    private RunBlock parentRunBlock = null;

    static Supplier<Long> currentTime = () -> DateTime.now().getMillis();

    BaseNode(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    protected Iterable<BaseNode> getParams() {
        return params;
    }

    BaseNode withName(String name) {
        this.name = name;
        return this;
    }

    BaseNode withLeftNode(BaseNode leftNode) {
        this.leftNode = leftNode;
        return this;
    }

    BaseNode setLeftNode(BaseNode leftNode) {
        this.leftNode = leftNode;
        return leftNode;
    }

    BaseNode withRightNode(BaseNode rightNode) {
        this.rightNode = rightNode;
        return this;
    }

    BaseNode setRightNode(BaseNode rightNode) {
        this.rightNode = rightNode;
        return rightNode;
    }

    void setValueSubstitution(BaseNode node) {
        this.valueSubstitution = node;
    }

    public BaseNode getLeftNode() {
        return leftNode;
    }

    public BaseNode getRightNode() {
        return rightNode;
    }

    int getNodeOperation() {
        return operation;
    }

    BaseNode withNodeOperation(int op) {
        operation = op;
        return this;
    }

    @Override
    public Variant execute(VariantContainer variantContainer) {
        Variant result = Variant.nullVariant();
        Variant leftNodeResult;
        Variant rightNodeResult;

        switch (operation) {
            case NT_VARIABLE:
            case NT_LOCAL_VARIABLE:
                if (getParameterNode() != null) {
                    int index = getParameterNode().execute(variantContainer).asNumeric().intValue();
                    Variant _v = Variant.getArrayItem(variantContainer.getVariant(getName()), index);
                    if ((_v == null || _v.isNull()) && (valueSubstitution != null)) {
                        _v = valueSubstitution.execute(variantContainer);
                    }
                    return Variant.sanitize(_v);
                } else {
                    Variant _v = variantContainer.getVariant(getName());
                    if ((_v == null || _v.isNull()) && (valueSubstitution != null)) {
                        _v = valueSubstitution.execute(variantContainer);
                    }
                    return Variant.sanitize(_v);
                }
            case '*':
                result = leftNode.execute(variantContainer).multiply(rightNode.execute(variantContainer));
                break;

            case '+':
                result = leftNode.execute(variantContainer).add(rightNode.execute(variantContainer));
                break;

            case '-':
                result = leftNode.execute(variantContainer).minus(rightNode.execute(variantContainer));
                break;

            case '=':
                result = leftNode.assignValue(rightNode.execute(variantContainer), variantContainer);
                break;

            case '/':
                result = leftNode.execute(variantContainer).divide(rightNode.execute(variantContainer));
                break;

            case '!':
                leftNodeResult = leftNode.execute(variantContainer);
                if (leftNodeResult.isNull()) {
                    result = Variant.nullVariant();
                } else {
                    result = Variant.fromBoolean(!leftNodeResult.asBoolean());
                }
                break;

            case '&':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromLong(leftNodeResult.asNumeric().longValue() & rightNodeResult.asNumeric().longValue());
                break;

            case '|':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromLong(leftNodeResult.asNumeric().longValue() | rightNodeResult.asNumeric().longValue());
                break;

            case '^':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromLong(leftNodeResult.asNumeric().longValue() ^ rightNodeResult.asNumeric().longValue());
                break;

            case '>':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromBoolean(leftNodeResult.compareTo(rightNodeResult) > 0);
                break;

            case '<':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromBoolean(leftNodeResult.compareTo(rightNodeResult) < 0);
                break;

            case NT_MF_POWER:
                result = getParameterOrNullNode(0).execute(variantContainer)
                        .pow(getParameterOrNullNode(1).execute(variantContainer));
                break;

            case NT_LOP_AND:
                leftNodeResult = leftNode.execute(variantContainer);
                if (leftNodeResult.asBoolean()) {
                    rightNodeResult = rightNode.execute(variantContainer);
                    result = Variant.fromBoolean(rightNodeResult.asBoolean());
                } else {
                    result = Variant.fromBoolean(false);
                }
                break;

            case NT_LOP_OR:
                leftNodeResult = leftNode.execute(variantContainer);
                if (leftNodeResult.asBoolean()) {
                    result = Variant.fromBoolean(true);
                } else {
                    result = Variant.fromBoolean(rightNode.execute(variantContainer).asBoolean());
                }
                break;

            case NT_LOP_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromBoolean(leftNodeResult.equals(rightNodeResult));
                break;

            case NT_LOP_NOT_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromBoolean(!leftNodeResult.equals(rightNodeResult));
                break;

            case NT_LOP_MORE_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromBoolean(leftNodeResult.compareTo(rightNodeResult) >= 0);
                break;

            case NT_LOP_LESS_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = Variant.fromBoolean(leftNodeResult.compareTo(rightNodeResult) <= 0);
                break;

            case NT_MF_SIN:
            case NT_MF_COS:
            case NT_MF_ASIN:
            case NT_MF_ACOS:
            case NT_MF_TAN:
            case NT_MF_ATAN:
            case NT_MF_EXP:
            case NT_MF_LN:
            case NT_MF_LOG:
            case NT_MF_SQRT:
            case NT_MF_ABS:
                result = getParameterOrNullNode().execute(variantContainer).mfunc(operation);
                break;

            case NT_MF_FLOOR_MOD:
                if (params != null && params.size() == 2) {
                    int x = params.get(0).execute(variantContainer).asNumeric().intValue();
                    int y = params.get(1).execute(variantContainer).asNumeric().intValue();
                    result = Variant.fromInt(Math.floorMod(x, y));
                } else {
                    throw new EvaluationException("Function floor_mod takes two int parameters.");
                }
                break;

            case NT_MF_NEG:
                result = getParameterOrNullNode().execute(variantContainer).negate();
                break;

            case NT_MF_DAY:
                result = Variant.fromInt(now().getDayOfMonth());
                break;

            case NT_MF_MONTH:
                result = Variant.fromInt(now().getMonthOfYear());
                break;

            case NT_MF_YEAR:
                result = Variant.fromInt(now().getYear());
                break;

            case NT_MF_DAY_OF_YEAR:
                result = Variant.fromInt(now().getDayOfYear());
                break;

            case NT_MF_DAYS_IN_MONTH: {
                DateTime dt = now();
                Variant month = getParameterOrNullNode().execute(variantContainer);
                dt = dt.plusMonths(month.asNumeric().intValue());
                result = Variant.fromInt(dt.dayOfMonth().withMaximumValue().getDayOfMonth());
            }
            break;

            case NT_MF_ISO: {
                Variant isoDate = getParameterOrNullNode().execute(variantContainer);
                if (!isoDate.isString()) {
                    throw new EvaluationException("ISO-8601 formatted string expected. Got: " + isoDate);
                }
                result = Variant.fromLong(ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(isoDate.asString()).getMillis());
            }
            break;

            case NT_MF_NOW:
                result = Variant.fromLong(currentTime.get());
                break;

            case NT_MF_DAYS_BEFORE_NOW:
                result = Variant.fromLong(durationTillNow(getParameterOrNullNode().execute(variantContainer)).getStandardDays());
                break;

            case NT_MF_HOURS_BEFORE_NOW:
                result = Variant.fromLong(durationTillNow(getParameterOrNullNode().execute(variantContainer)).getStandardHours());
                break;

            case NT_MF_MINUTES_BEFORE_NOW:
                result = Variant.fromLong(durationTillNow(getParameterOrNullNode().execute(variantContainer)).getStandardMinutes());
                break;

            case NT_MF_DAYS_SINCE_WEEKDAY:
                if (params == null || params.size() != 1) {
                    throw new EvaluationException("Function `days_since_weekday` takes one parameter, the day of the week in numeric form");
                }
                int providedDay = params.get(0).execute(variantContainer).asNumeric().intValue();

                if (providedDay >= 1 && providedDay <= 7) {
                    int today = DateTime.now().getDayOfWeek();
                    int providedDayLastWeek = 7 - Math.abs(today - providedDay);
                    result = (providedDay > today) ? Variant.fromInt(providedDayLastWeek) : Variant.fromInt(today - providedDay);

                } else {
                    throw new EvaluationException("Provided day of week was invalid, needs to be between 1 and 7.");
                }
                break;

            case NT_MF_SIZE:
                result = Variant.fromInt(getParameterOrNullNode().execute(variantContainer).size());
                break;

            case NT_MF_IS_STRING:
                result = Variant.fromBoolean(getParameterOrNullNode().execute(variantContainer).isString());
                break;

            case NT_MF_IS_NUMBER:
                result = Variant.fromBoolean(getParameterOrNullNode().execute(variantContainer).isNumeric());
                break;

            case NT_MF_IS_ARRAY:
                result = Variant.fromBoolean(getParameterOrNullNode().execute(variantContainer).isArray());
                break;

            case NT_MF_IS_NULL:
                result = Variant.fromBoolean(getParameterOrNullNode().execute(variantContainer).isNull());
                break;

            case NT_MF_TO_ARRAY: {
                List<Variant> arrItems = Lists.newArrayList();
                if (params != null) {
                    params.forEach(itemNode -> arrItems.add(itemNode.execute(variantContainer)));
                }
                result = Variant.fromArray(arrItems);
            }
            break;

            case NT_MF_IF: {
                if (params == null || params.size() < 3) {
                    throw new EvaluationException("Function `if` takes 3 parameters. E.g. if(true, \"it is true\", \"it is false\")");
                }
                result = (params.get(0).execute(variantContainer).asBoolean()) ?
                        params.get(1).execute(variantContainer) :
                        params.get(2).execute(variantContainer);
            }
            break;

            case NT_MF_FIRST: {
                if (params == null || params.size() != 2) {
                    throw new EvaluationException(String.format("Function `%1$s` takes 2 parameters. " +
                                    "E.g. %1$s(\"Hello\", 2) will result in \"He\"",
                            EquationParser.functionNameFromCode(operation)));
                }
                String _str = getParameterOrNullNode(0)
                        .execute(variantContainer).asString();
                int _p1 = getParameterOrNullNode(1)
                        .execute(variantContainer).asNumeric().intValue();
                if (_p1 < 0) {
                    result = Variant.emptyStringVariant();
                } else {
                    result = (_p1 >= _str.length()) ?
                            Variant.fromString(_str) :
                            Variant.fromString(_str.substring(0, _p1));
                }
            }
            break;

            case NT_MF_LAST: {
                if (params == null || params.size() != 2) {
                    throw new EvaluationException(String.format("Function `%1$s` takes 2 parameters. " +
                                    "E.g. %1$s(\"Hello\", 2) will result in \"lo\"",
                            EquationParser.functionNameFromCode(operation)));
                }
                String _str = getParameterOrNullNode(0)
                        .execute(variantContainer).asString();
                int _p1 = getParameterOrNullNode(1)
                        .execute(variantContainer).asNumeric().intValue();
                int _l = _str.length();
                if (_p1 > _l) {
                    result = Variant.fromString(_str);
                } else if (_p1 < 0) {
                    result = Variant.emptyStringVariant();
                } else {
                    result = Variant.fromString(_str.substring(_l - _p1, _l));
                }
            }
            break;

            case NT_MF_SKIP: {
                if (params == null || params.size() != 2) {
                    throw new EvaluationException(String.format("Function %1$s` takes 2 parameters. " +
                                    "E.g. %1$s(\"Hello\", 2) will result in \"llo\"",
                            EquationParser.functionNameFromCode(operation)));
                }
                String _str = getParameterOrNullNode(0)
                        .execute(variantContainer).asString();
                int _p1 = getParameterOrNullNode(1)
                        .execute(variantContainer).asNumeric().intValue();
                int _l = _str.length();
                if (_p1 > _l) {
                    result = Variant.emptyStringVariant();
                } else if (_p1 < 0) {
                    result = Variant.fromString(_str);
                } else {
                    result = Variant.fromString(_str.substring(_p1));
                }
            }
            break;

            case NT_MF_MAX:
                int _paramCount = getParameterCount();
                if (_paramCount > 0) {
                    Variant _v = params.get(0).execute(variantContainer);
                    result = (_v.isArray()) ? max(_v.asArray()) : toNumeric(_v);
                    for (int i = 1; i < _paramCount; ++i) {
                        _v = params.get(i).execute(variantContainer);
                        _v = (_v.isArray()) ? max(_v.asArray()) : toNumeric(_v);
                        if (_v.compareTo(result) > 0) {
                            result = _v;
                        }
                    }
                }

                break;

            case NT_MF_MIN:
                _paramCount = getParameterCount();
                if (_paramCount > 0) {
                    Variant _v = params.get(0).execute(variantContainer);
                    result = (_v.isArray()) ? min(_v.asArray()) : _v;
                    for (int i = 1; i < _paramCount; ++i) {
                        _v = params.get(i).execute(variantContainer);
                        _v = (_v.isArray()) ? min(_v.asArray()) : toNumeric(_v);
                        if (!_v.isNull() && _v.compareTo(result) < 0) {
                            result = _v;
                        }
                    }
                }
                break;

            case NT_MF_AVERAGE:
                if (params != null) {
                    BigDecimal sum = BigDecimal.ZERO;
                    int _c = 0;
                    for (int i = 0; i < params.size(); ++i) {
                        Variant _v = params.get(i).execute(variantContainer);
                        if (_v.isNumeric()) {
                            _c += 1;
                            sum = sum.add(_v.asNumeric());
                        }
                    }
                    if (_c > 0) {
                        result = Variant.fromBigDecimal(sum).divide(Variant.fromInt(_c));
                    } else {
                        result = Variant.nullVariant();
                    }
                } else {
                    result = Variant.nullVariant();
                }
                break;

            case NT_MF_MEDIAN:
                if (params != null) {
                    List<BigDecimal> _values = Lists.newArrayList();
                    for (int i = 0; i < params.size(); ++i) {
                        Variant _v = params.get(i).execute(variantContainer);
                        if (_v.isNumeric()) {
                            _values.add(_v.asNumeric());
                        }
                    }
                    _values.sort(BigDecimal::compareTo);
                    if (!_values.isEmpty()) {
                        if (Math.floorMod(_values.size(), 2) == 1) {
                            result = Variant.fromBigDecimal(_values.get(_values.size() / 2));
                        } else {
                            result = Variant.fromBigDecimal(
                                            _values.get(_values.size() / 2)
                                                    .add(_values.get(_values.size() / 2 - 1)))
                                    .divide(Variant.fromInt(2));
                        }
                    } else {
                        result = Variant.nullVariant();
                    }
                } else {
                    result = Variant.nullVariant();
                }
                break;

            case NT_MF_FORMAT_TS:
                if (params == null || params.size() < 2 || params.size() > 3) {
                    throw new EvaluationException(String.format("Function %1$s` takes 2 or 3 parameters. " +
                                    "E.g. %1$s(\"2023-02-28T05:16:55.835697363Z\", \"mm/dd/yy\") will result in \"02/28/23\"",
                            EquationParser.functionNameFromCode(operation)));
                }

                String tsToBeFormatted = getParameterOrNullNode(0).execute(variantContainer).asString();

                if (StringUtils.isBlank(tsToBeFormatted)) {
                    return Variant.nullVariant();
                }

                DateTime ts;
                try {
                    if (StringUtils.isNumeric(tsToBeFormatted)) {
                        ts = new DateTime(Long.valueOf(tsToBeFormatted));
                    } else {
                        ts = new DateTime(tsToBeFormatted);
                    }
                } catch(IllegalArgumentException iae) {
                    throw new EvaluationException("Invalid timestamp.");
                }

                String fmt = getParameterOrNullNode(1).execute(variantContainer).asString();

                DateTimeFormatter dtf;

                try {
                    dtf = DateTimeFormat.forPattern(fmt);
                } catch(IllegalArgumentException iae) {
                    throw new EvaluationException("Invalid format specification.");
                }

                if (params.size() == 3) {
                    String zoneId = getParameterOrNullNode(2).execute(variantContainer).asString();

                    try {
                        DateTimeZone dtz = DateTimeZone.forID(zoneId);
                        ts = ts.withZone(dtz);
                    } catch (IllegalArgumentException iae) {
                        throw new EvaluationException("Invalid time zone.");
                    }
                }

                result = Variant.fromString(ts.toString(dtf));

                break;

            case NT_FUNCTION: {
                if (parentRunBlock == null) {
                    throw new UndefinedFunction(getName());
                }
                AbstractFunction function = parentRunBlock.resolveFunction(getName());
                if (function == null) {
                    throw new UndefinedFunction(getName());
                }
                LocalVariantContainer lvc = new LocalVariantContainer(variantContainer);
                List<String> parameterNames = function.getParameterNames();
                for (int pidx = 0;
                     pidx < Math.min(params.size(), parameterNames.size());
                     ++pidx) {
                    String fpn = parameterNames.get(pidx);
                    if (fpn.length() > 0) {
                        lvc.setVariant(fpn, params.get(pidx).execute(variantContainer));
                    }
                }
                result = function.execute(lvc);
                if ((result == null || result.isNull()) && (valueSubstitution != null)) {
                    result = valueSubstitution.execute(variantContainer);
                }

                result = Variant.sanitize(result);
            }
            break;

            default:
                throw new RuntimeException("Unexpected node: " + operation);

        }
        return result;
    }

    /**
     * Returns parameter node for build-in functions like "sin", "cos" etc., or array index.
     */
    private BaseNode getParameterNode() {
        if (getParameterCount() > 0) {
            return params.get(0);
        }
        return null;
    }

    private BaseNode getParameterOrNullNode() {
        return Optional.ofNullable(getParameterNode()).orElse(C_NULL);
    }

    private BaseNode getParameterOrNullNode(int index) {
        return (getParameterCount() > index) ? params.get(index) : C_NULL;
    }

    private int getParameterCount() {
        return (params != null) ? params.size() : 0;
    }

    /**
     * Set variable node value.
     */
    private Variant assignValue(Variant newValue, VariantContainer variantContainer) {
        if (operation == NT_VARIABLE || operation == NT_LOCAL_VARIABLE) {
            // Array variable node
            if (getParameterNode() != null) {
                int index = getParameterNode().execute(variantContainer).asNumeric().intValue();
                variantContainer.setVariant(getName(), index, newValue);
            } else {
                variantContainer.setVariant(getName(), newValue);
            }
            return variantContainer.getVariant(getName());
        } else {
            throw new EvaluationException("Invalid assignment");
        }
    }


    @Override
    public void setParentRunBlock(RunBlock runBlock) {
        parentRunBlock = runBlock;
        if (leftNode != null) leftNode.setParentRunBlock(runBlock);
        if (rightNode != null) rightNode.setParentRunBlock(runBlock);

        if (params != null) {
            params.forEach(nd -> nd.setParentRunBlock(runBlock));
        }
    }

    @Override
    public void collectStats(NodeStats stats) {
        switch (this.operation) {
            case NT_VARIABLE:
                stats.referencedVariable(getName());
                break;
            case NT_FUNCTION:
                stats.referencedExtFunction(getName());
                break;
        }

        if (valueSubstitution != null) {
            valueSubstitution.collectStats(stats);
        }

        if (leftNode != null) {
            leftNode.collectStats(stats);
        }

        if (rightNode != null) {
            rightNode.collectStats(stats);
        }

        if (params != null) {
            params.forEach(baseNode -> baseNode.collectStats(stats));
        }

    }

    /**
     * Adds node as parameter, for "function" type node.
     */
    void addParameterNode(BaseNode paramNode) {
        params.add(paramNode);
    }

    void initParams() {
        params = new ArrayList<>();
    }

    @Override
    public String toString() {
        return this.id;
    }

    static Duration durationTillNow(Variant from) {
        if (from.isString()) {
            return new Duration(
                    ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(from.asString()),
                    now());
        } else if (from.asNumeric().longValue() > 1) {
            return new Duration(
                    new DateTime(from.asNumeric().longValue()),
                    now());
        }

        throw new RuntimeException("ISO string date or millis is expected as input.");
    }

    static DateTime now() {
        return new DateTime(currentTime.get());
    }


    static Variant max(List<Variant> va) {
        Variant max = Variant.nullVariant();
        for (Variant vi : va) {
            Variant _vn = toNumeric(vi);
            if (!_vn.isNull() && _vn.compareTo(max) > 0) {
                max = _vn;
            }
        }
        return max;
    }

    static Variant min(List<Variant> va) {
        Variant min = Variant.nullVariant();
        for (Variant vi : va) {
            Variant _vn = toNumeric(vi);
            if (!_vn.isNull() && _vn.compareTo(min) < 0) {
                min = _vn;
            }
        }
        return min;
    }

    static Variant toNumeric(Variant v) {
        return v.isNumeric() ? v :
                v.isNull() ? v : Variant.fromBigDecimal(v.asNumeric());
    }

}
