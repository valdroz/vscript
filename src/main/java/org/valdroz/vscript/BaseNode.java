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
public class BaseNode implements RunBlock, Constants {

    private char operation = 0;

    private Variant value;

    private BaseNode leftNode = null;
    private BaseNode rightNode = null;
    private String name = "";

    private DefaultRunBlock parentRunBlock = null;
    private List<BaseNode> funcParams = null;

    static Supplier<Long> nowProvider = () -> DateTime.now().getMillis();

    BaseNode() {
    }

    public Variant getValue() {
        return Variant.sanitize(value);
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    void setLeftNode(BaseNode leftNode) {
        this.leftNode = leftNode;
    }

    void setRightNode(BaseNode rightNode) {
        this.rightNode = rightNode;
    }

    public BaseNode getLeftNode() {
        return leftNode;
    }

    public BaseNode getRightNode() {
        return rightNode;
    }

    public Variant assignValue(Variant value) {
        this.value = Variant.sanitize(value);
        return this.value;
    }

    char getNodeOperation() {
        return operation;
    }

    BaseNode setNodeOperation(char op) {
        operation = op;
        return this;
    }

    @Override
    public void run(VariantContainer variantContainer) {
        execute(variantContainer);
    }


    public Variant execute(VariantContainer variantContainer) {

        Variant leftNodeResult;
        Variant rightNodeResult;

        switch (operation) {
            case NT_VALUE:
                return Variant.sanitize(value);
            case NT_VARIABLE:
            case NT_LOCAL_VARIABLE:
                if (getParameterNode() != null) {
                    int index = getParameterNode().execute(variantContainer).asNumeric().intValue();
                    return Variant.getArrayItem(variantContainer.getVariant(this.name), index);
                } else {
                    return Variant.sanitize(variantContainer.getVariant(this.name));
                }
            case '*':
                value = leftNode.execute(variantContainer).multiply(rightNode.execute(variantContainer));
                break;

            case '+':
                value = leftNode.execute(variantContainer).add(rightNode.execute(variantContainer));
                break;

            case '-':
                value = leftNode.execute(variantContainer).minus(rightNode.execute(variantContainer));
                break;

            case '=':
                rightNodeResult = rightNode.execute(variantContainer);
                value = leftNode.assignValue(rightNodeResult, variantContainer);
                break;

            case '/':
                value = leftNode.execute(variantContainer).divide(rightNode.execute(variantContainer));
                break;

            case '!':
                value = Variant.fromBoolean(!getParameterNode().execute(variantContainer).asBoolean());
                break;

            case '&':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = Variant.fromLong(leftNodeResult.asNumeric().longValue() & rightNodeResult.asNumeric().longValue());
                break;

            case '|':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = Variant.fromLong(leftNodeResult.asNumeric().longValue() | rightNodeResult.asNumeric().longValue());
                break;

            case '>':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = Variant.fromBoolean(leftNodeResult.compareTo(rightNodeResult) > 0);
                break;

            case '<':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = Variant.fromBoolean(leftNodeResult.compareTo(rightNodeResult) < 0);
                break;

            case '^':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = leftNodeResult.pow(rightNodeResult);
                break;

            case NT_LOP_AND:
                leftNodeResult = leftNode.execute(variantContainer);
                if (leftNodeResult.asBoolean()) {
                    rightNodeResult = rightNode.execute(variantContainer);
                    value = Variant.fromBoolean(rightNodeResult.asBoolean());
                } else {
                    value = Variant.fromBoolean(false);
                }
                break;

            case NT_LOP_OR:
                leftNodeResult = leftNode.execute(variantContainer);
                if (leftNodeResult.asBoolean()) {
                    value = Variant.fromBoolean(true);
                } else {
                    value = Variant.fromBoolean(rightNode.execute(variantContainer).asBoolean());
                }
                break;

            case NT_LOP_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = Variant.fromBoolean(leftNodeResult.equals(rightNodeResult));
                break;

            case NT_LOP_NOT_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = Variant.fromBoolean(!leftNodeResult.equals(rightNodeResult));
                break;

            case NT_LOP_MORE_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = Variant.fromBoolean(leftNodeResult.compareTo(rightNodeResult) >= 0);
                break;

            case NT_LOP_LESS_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value = Variant.fromBoolean(leftNodeResult.compareTo(rightNodeResult) <= 0);
                break;

            case NT_MF_SIN:
                value = Variant.fromDouble(Math.sin(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_COS:
                value = Variant.fromDouble(Math.cos(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_ASIN:
                value = Variant.fromDouble(Math.asin(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_ACOS:
                value = Variant.fromDouble(Math.acos(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_TAN:
                value = Variant.fromDouble(Math.tan(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_ATAN:
                value = Variant.fromDouble(Math.atan(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_EXP:
                value = Variant.fromDouble(Math.exp(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_LN:
            case NT_MF_LOG:
                value = Variant.fromDouble(Math.log(getParameterNode().execute(variantContainer).asNumeric().doubleValue()));
                break;

            case NT_MF_NEG:
                value = getParameterNode().execute(variantContainer).negate();
                break;

            case NT_MF_ABS:
                value = getParameterNode().execute(variantContainer).abs();
                break;

            case NT_MF_SQRT:
                value = getParameterNode().execute(variantContainer).sqrt();
                break;

            case NT_MF_DEBUG:
                System.out.print(Variant.sanitize(getParameterNode().execute(variantContainer)).asString());
                break;

            case NT_MF_DAY:
                value = Variant.fromInt(now().getDayOfMonth());
                break;

            case NT_MF_MONTH:
                value = Variant.fromInt(now().getMonthOfYear());
                break;

            case NT_MF_YEAR:
                value = Variant.fromInt(now().getYear());
                break;

            case NT_MF_DAY_OF_YEAR:
                value = Variant.fromInt(now().getDayOfYear());
                break;

            case NT_MF_DAYS_IN_MONTH: {
                DateTime dt = now();
                Variant month = getParameterNode().execute(variantContainer);
                dt = dt.plusMonths(month.asNumeric().intValue());
                value = Variant.fromInt(dt.dayOfMonth().withMaximumValue().getDayOfMonth());
            }
            break;

            case NT_MF_ISO: {
                Variant isoDate = getParameterNode().execute(variantContainer);
                if (!isoDate.isString()) {
                    throw new RuntimeException("iso expects string as input.");
                }
                value = Variant.fromLong(ISODateTimeFormat.dateOptionalTimeParser().parseDateTime(isoDate.asString()).getMillis());
            }
            break;

            case NT_MF_NOW:
                value = Variant.fromLong(nowProvider.get());
                break;

            case NT_MF_DAYS_BEFORE_NOW:
                value = Variant.fromLong(durationTillNow(getParameterNode().execute(variantContainer)).getStandardDays());
                break;

            case NT_MF_HOURS_BEFORE_NOW:
                value = Variant.fromLong(durationTillNow(getParameterNode().execute(variantContainer)).getStandardHours());
                break;

            case NT_MF_SIZE:
                value = Variant.fromInt(getParameterNode().execute(variantContainer).size());
                break;

            case NT_MF_IS_STRING:
                value = Variant.fromBoolean(getParameterNode().execute(variantContainer).isString());
                break;

            case NT_MF_IS_NUMBER:
                value = Variant.fromBoolean(getParameterNode().execute(variantContainer).isNumeric());
                break;

            case NT_MF_IS_ARRAY:
                value = Variant.fromBoolean(getParameterNode().execute(variantContainer).isArray());
                break;

            case NT_MF_IS_NULL:
                value = Variant.fromBoolean(getParameterNode().execute(variantContainer).isNull());
                break;

            case NT_FUNCTION: {
                AbstractFunction function = parentRunBlock.getFunction(this.name);
                if (function == null) {
                    throw new UndefinedFunction(this.name);
                }
                LocalVariantContainer lvc = new LocalVariantContainer(variantContainer);
                List<String> parameterNames = function.getParameterNames();
                for (int pidx = 0;
                     pidx < Math.min(funcParams.size(), parameterNames.size());
                     ++pidx) {
                    String fpn = parameterNames.get(pidx);
                    if (fpn.length() > 0) {
                        lvc.setVariant(fpn, funcParams.get(pidx).execute(variantContainer) );
                    }
                }
                value = Variant.sanitize(function.execute(lvc));
            }
            break;
            default:
                throw new RuntimeException("Unexpected node: " + operation);

        }
        return value;
    }

    /**
     * Returns parameter node for build-in functions like "sin", "cos" and etc, or array index.
     */
    private BaseNode getParameterNode() {
        if (leftNode == null) return rightNode;
        return leftNode;
    }

    /**
     * Sets calculated value to the variable.
     */
    private Variant assignValue(Variant newValue, VariantContainer variantContainer) {
        if (operation == NT_VARIABLE || operation == NT_LOCAL_VARIABLE) {
            // Array variable node
            if (getParameterNode() != null) {
                int index = getParameterNode().execute(variantContainer).asNumeric().intValue();
                variantContainer.setVariant(name, index, newValue);
            } else {
                variantContainer.setVariant(name, newValue);
            }
            return variantContainer.getVariant(name);
        } else {
            value = newValue;
            return value;
        }
    }


    @Override
    public void setParentRunBlock(DefaultRunBlock runBlock) {
        parentRunBlock = runBlock;
        if (leftNode != null) leftNode.setParentRunBlock(runBlock);
        if (rightNode != null) rightNode.setParentRunBlock(runBlock);

        if (funcParams != null) {
            funcParams.forEach(nd -> nd.setParentRunBlock(runBlock));
        }
    }


    /**
     * Adds node as parameter, for "function" type node.
     */
    void addParameterNode(BaseNode paramNode) {
        funcParams.add(paramNode);
    }

    void initParams() {
        funcParams = new ArrayList<>();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "{", "}")
                .add("o=" + operation)
                .add("l=" + leftNode)
                .add("r=" + rightNode)
                .add("v=" + value)
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
        return new DateTime(nowProvider.get());
    }
}
