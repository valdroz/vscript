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

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * Base interpretable node.
 *
 * @author Valerijus Drozdovas
 */
class BaseNode implements Node, Constants {
    private char operation = 0;
    private String name = "";

    private BaseNode valueSubstitution = null;
    private BaseNode leftNode = null;
    private BaseNode rightNode = null;
    private List<BaseNode> params = null;

    private RunBlock parentRunBlock = null;

    static Supplier<Long> currentTime = () -> DateTime.now().getMillis();

    BaseNode() {
    }

    public String getName() {
        return name;
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

    char getNodeOperation() {
        return operation;
    }

    BaseNode withNodeOperation(char op) {
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
                result = Variant.fromBoolean(!leftNode.execute(variantContainer).asBoolean());
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

            case '^':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                result = leftNodeResult.pow(rightNodeResult);
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
                result = Variant.fromDouble(Math.sin(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_COS:
                result = Variant.fromDouble(Math.cos(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_ASIN:
                result = Variant.fromDouble(Math.asin(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_ACOS:
                result = Variant.fromDouble(Math.acos(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_TAN:
                result = Variant.fromDouble(Math.tan(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_ATAN:
                result = Variant.fromDouble(Math.atan(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_EXP:
                result = Variant.fromDouble(Math.exp(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_LN:
            case NT_MF_LOG:
                result = Variant.fromDouble(Math.log(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_NEG:
                result = getParameterNode().execute(variantContainer).negate();
                break;

            case NT_MF_ABS:
                result = getParameterNode().execute(variantContainer).abs();
                break;

            case NT_MF_SQRT:
                result = getParameterNode().execute(variantContainer).sqrt();
                break;

            case NT_MF_DEBUG:
                System.out.print(Variant.sanitize(getParameterNode().execute(variantContainer)).asString());
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
                Variant month = getParameterNode().execute(variantContainer);
                dt = dt.plusMonths(month.asNumeric().intValue());
                result = Variant.fromInt(dt.dayOfMonth().withMaximumValue().getDayOfMonth());
            }
            break;

            case NT_MF_ISO: {
                Variant isoDate = getParameterNode().execute(variantContainer);
                if (!isoDate.isString()) {
                    throw new RuntimeException("iso expects string as input.");
                }
                result = Variant.fromLong(ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(isoDate.asString()).getMillis());
            }
            break;

            case NT_MF_NOW:
                result = Variant.fromLong(currentTime.get());
                break;

            case NT_MF_DAYS_BEFORE_NOW:
                result = Variant.fromLong(durationTillNow(getParameterNode().execute(variantContainer)).getStandardDays());
                break;

            case NT_MF_HOURS_BEFORE_NOW:
                result = Variant.fromLong(durationTillNow(getParameterNode().execute(variantContainer)).getStandardHours());
                break;

            case NT_MF_SIZE:
                result = Variant.fromInt(getParameterNode().execute(variantContainer).size());
                break;

            case NT_MF_IS_STRING:
                result = Variant.fromBoolean(getParameterNode().execute(variantContainer).isString());
                break;

            case NT_MF_IS_NUMBER:
                result = Variant.fromBoolean(getParameterNode().execute(variantContainer).isNumeric());
                break;

            case NT_MF_IS_ARRAY:
                result = Variant.fromBoolean(getParameterNode().execute(variantContainer).isArray());
                break;

            case NT_MF_IS_NULL:
                result = Variant.fromBoolean(getParameterNode().execute(variantContainer).isNull());
                break;

            case NT_FUNCTION: {
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
     * Returns parameter node for build-in functions like "sin", "cos" and etc, or array index.
     */
    private BaseNode getParameterNode() {
//        if (leftNode == null) return rightNode;
//        return leftNode;
        if (params != null && params.size() > 0) {
            return params.get(0);
        }
        return null;
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
        return new StringJoiner(", ", "{", "}")
                .add("o=" + operation)
                .add("l=" + leftNode)
                .add("r=" + rightNode)
                .add("var='" + name + "'")
                .toString();
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
}
