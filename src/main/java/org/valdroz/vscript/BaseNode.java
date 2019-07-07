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

import java.util.*;

/**
 * Base interpretable node.
 *
 * @author Valerijus Drozdovas
 */
public class BaseNode implements RunBlock, Constants {

    char operation = 0;

    BaseNode leftNode = null;
    BaseNode rightNode = null;
    Variant value = new Variant();
    String variableName = "";

    private MasterRunBlock masterRunBlock = null;
    private List<BaseNode> funcParams = null;


    BaseNode() {
    }


    @Override
    public void run(VariantContainer variantContainer) {
        execute(variantContainer);
    }


    public Variant execute(VariantContainer variantContainer) {
        if (operation == NT_VALUE)
            return value;
        else if (operation == NT_VARIABLE || operation == NT_LOCAL_VARIABLE) {
            if (getPreferredNode() != null) {
                int index = (int) getPreferredNode().execute(variantContainer).toDouble();
                return variantContainer.getVariant(this.variableName).getArrayItem(index);
            } else {
                return variantContainer.getVariant(this.variableName);
            }
        }

        double tmp;

        Variant leftNodeResult;
        Variant rightNodeResult;

        switch (operation) {
            case '*':
                value.setValue(leftNode.execute(variantContainer).toDouble() * rightNode.execute(variantContainer).toDouble());
                break;

            case '+':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                if (leftNodeResult.getValueType() == Variant.VT_STRING ||
                        rightNodeResult.getValueType() == Variant.VT_STRING)
                    value.setValue(leftNodeResult.toString() + rightNodeResult.toString());
                else
                    value.setValue(leftNodeResult.getDouble() + rightNodeResult.getDouble());
                break;

            case '-':
                value.setValue(leftNode.execute(variantContainer).toDouble() - rightNode.execute(variantContainer).toDouble());
                break;

            case '=':
                rightNodeResult = rightNode.execute(variantContainer);
                leftNode.setValue(rightNodeResult, variantContainer);
                break;

            case '/':
                tmp = rightNode.execute(variantContainer).toDouble();
                if (tmp == 0.0)
                    value.setValue(Double.MAX_VALUE);
                else
                    value.setValue(leftNode.execute(variantContainer).toDouble() / tmp);
                break;

            case '!':
                value.setValue(getPreferredNode().execute(variantContainer).toBoolean() ? 1.0 : 0.0);
                break;

            case '&':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                tmp = (int) leftNodeResult.toDouble() & (int) rightNodeResult.toDouble();
                value.setValue(tmp);
                break;

            case '|':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                tmp = (int) leftNodeResult.toDouble() | (int) rightNodeResult.toDouble();
                value.setValue(tmp);
                break;

            case '>':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                if (leftNodeResult.getValueType() == Variant.VT_STRING &&
                        rightNodeResult.getValueType() == Variant.VT_STRING)
                    tmp = (leftNodeResult.toString().compareTo(rightNodeResult.toString()) > 0 ? 1.0 : 0.0);
                else
                    tmp = leftNodeResult.toDouble() > rightNodeResult.toDouble() ? 1.0 : 0.0;
                value.setValue(tmp);
                break;

            case '<':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                if (leftNodeResult.getValueType() == Variant.VT_STRING &&
                        rightNodeResult.getValueType() == Variant.VT_STRING)
                    tmp = (leftNodeResult.toString().compareTo(rightNodeResult.toString()) < 0 ? 1.0 : 0.0);
                else
                    tmp = leftNodeResult.toDouble() < rightNodeResult.toDouble() ? 1.0 : 0.0;
                value.setValue(tmp);
                break;

            case '^':
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value.setValue(Math.pow(leftNodeResult.toDouble(), rightNodeResult.toDouble()));
                break;

            case NT_LOP_AND:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value.setValue((leftNodeResult.toBoolean() && rightNodeResult.toBoolean()) ? 1.0 : 0.0);
                break;

            case NT_LOP_OR:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                value.setValue((leftNodeResult.toBoolean() || rightNodeResult.toBoolean()) ? 1.0 : 0.0);
                break;

            case NT_LOP_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                if (leftNodeResult.getValueType() == Variant.VT_STRING ||
                        rightNodeResult.getValueType() == Variant.VT_STRING) {
                    value.setValue(leftNodeResult.toString().equals(rightNodeResult.toString()) ? 1.0 : 0.0);
                } else {
                    value.setValue(leftNodeResult.equals(rightNodeResult) ? 1.0 : 0.0);
                }
                break;

            case NT_LOP_NOT_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                if (leftNodeResult.getValueType() == Variant.VT_STRING ||
                        rightNodeResult.getValueType() == Variant.VT_STRING) {
                    value.setValue(!leftNodeResult.toString().equals(rightNodeResult.toString()) ? 1.0 : 0.0);
                } else {
                    value.setValue(!leftNodeResult.equals(rightNodeResult) ? 1.0 : 0.0);
                }
                break;

            case NT_LOP_MORE_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);

                if (leftNodeResult.getValueType() == Variant.VT_STRING ||
                        rightNodeResult.getValueType() == Variant.VT_STRING) {
                    value.setValue(leftNodeResult.toString().compareTo(rightNodeResult.toString()) >= 0 ? 1.0 : 0.0);
                } else {
                    value.setValue((leftNodeResult.toDouble() >= rightNodeResult.toDouble()) ? 1.0 : 0.0);
                }
                break;

            case NT_LOP_LESS_EQUALS:
                leftNodeResult = leftNode.execute(variantContainer);
                rightNodeResult = rightNode.execute(variantContainer);
                if (leftNodeResult.getValueType() == Variant.VT_STRING ||
                        rightNodeResult.getValueType() == Variant.VT_STRING) {
                    value.setValue(leftNodeResult.toString().compareTo(rightNodeResult.toString()) <= 0 ? 1.0 : 0.0);
                } else {
                    value.setValue((leftNodeResult.toDouble() <= rightNodeResult.toDouble()) ? 1.0 : 0.0);
                }
                break;

            case NT_MF_SIN:
                value.setValue(Math.sin(getPreferredNode().execute(variantContainer).toDouble()));
                break;

            case NT_MF_COS:
                value.setValue(Math.cos(getPreferredNode().execute(variantContainer).toDouble()));
                break;

            case NT_MF_ASIN:
                value.setValue(Math.asin(getPreferredNode().execute(variantContainer).toDouble()));
                break;

            case NT_MF_ACOS:
                value.setValue(Math.acos(getPreferredNode().execute(variantContainer).toDouble()));
                break;

            case NT_MF_TAN:
                value.setValue(Math.tan(getPreferredNode().execute(variantContainer).toDouble()));
                break;

            case NT_MF_ATAN:
                value.setValue(Math.atan(getPreferredNode().execute(variantContainer).toDouble()));
                break;

            case NT_MF_EXP:
                value.setValue(Math.exp(getPreferredNode().execute(variantContainer).toDouble()));
                break;

            case NT_MF_LN:
            case NT_MF_LOG:
                value.setValue(Math.log(getPreferredNode().execute(variantContainer).toDouble()));
                break;

            case NT_MF_NEG:
                tmp = getPreferredNode().execute(variantContainer).toDouble();
                value.setValue(tmp > 0.0 ? tmp : tmp * -1.0);
                break;

            case NT_MF_ABS:
                tmp = getPreferredNode().execute(variantContainer).toDouble();
                value.setValue(Math.abs(tmp));
                break;

            case NT_MF_SQRT:
                tmp = getPreferredNode().execute(variantContainer).toDouble();
                value.setValue(Math.sqrt(tmp));
                break;

            case NT_MF_DEBUG:
                value.setValue(getPreferredNode().execute(variantContainer));
                System.out.print(value.toString());
                break;
            case NT_MF_DAY: {
                Calendar c = new GregorianCalendar();
                c.setTime(new Date());
                value.setValue(c.get(Calendar.DAY_OF_MONTH));
            }
            break;
            case NT_MF_MONTH: {
                Calendar c = new GregorianCalendar();
                c.setTime(new Date());
                value.setValue(c.get(Calendar.MONTH) + 1);
            }
            break;
            case NT_MF_YEAR: {
                Calendar c = new GregorianCalendar();
                c.setTime(new Date());
                value.setValue(c.get(Calendar.YEAR));
            }
            break;
            case NT_MF_DAY_OF_YEAR: {
                Calendar c = new GregorianCalendar();
                c.setTime(new Date());
                value.setValue(c.get(Calendar.DAY_OF_YEAR));
            }
            break;
            case NT_MF_DAYS_IN_MONTH: {
                tmp = getPreferredNode().execute(variantContainer).toDouble();
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(new Date());
                int month = c.get(Calendar.MONTH);
                //java1 --------------------------------------------------
                c.set(Calendar.MONTH, month + (int) tmp + 1);
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.roll(Calendar.DAY_OF_YEAR, false);
                value.setValue(c.get(Calendar.DAY_OF_MONTH));

                //java2 --------------------------------------------------
                //c.set( Calendar.MONTH, month + (int)tmp );
                //value.setValue( c.getActualMaximum( Calendar.DAY_OF_MONTH ) );
            }
            break;
            case NT_MF_SIZE:
                value.setValue(getPreferredNode().execute(variantContainer).size());
                break;

            case NT_MF_IS_STRING:
                value.setValue(getPreferredNode().execute(variantContainer).getValueType() == Variant.VT_STRING ? 1.0 : 0.0);
                break;

            case NT_MF_IS_NUMBER:
                value.setValue(getPreferredNode().execute(variantContainer).getValueType() == Variant.VT_NUMERIC ? 1.0 : 0.0);
                break;

            case NT_MF_IS_ARRAY:
                value.setValue(getPreferredNode().execute(variantContainer).getValueType() == Variant.VT_ARRAY ? 1.0 : 0.0);
                break;

            case NT_MF_IS_NULL:
                value.setValue(getPreferredNode().execute(variantContainer).getValueType() == Variant.VT_NONE ? 1.0 : 0.0);
                break;

            case NT_FUNCTION: {
                List<Variant> paramValues = new LinkedList<>();
                if (funcParams != null) {
                    funcParams.forEach(nd -> paramValues.add(nd.execute(variantContainer)));
                }
                Variant rez = masterRunBlock.callFunction(this.variableName, paramValues, variantContainer);
                if (rez != null) {
                    value.setValue(rez);
                }
            }
            break;
        }
        return value;
    }

    /**
     * Returns the child node for the single sided nodes like "sin", "cos" and etc.
     */
    private BaseNode getPreferredNode() {
        if (leftNode == null) return rightNode;
        return leftNode;
    }

    /**
     * Sets calculated value to the variable.
     */
    private void setValue(Variant newValue, VariantContainer variantContainer) {
        if (operation == NT_VARIABLE || operation == NT_LOCAL_VARIABLE) {
            // Array variable node
            if (getPreferredNode() != null) {
                int index = (int) getPreferredNode().execute(variantContainer).toDouble();
                variantContainer.setVariant(variableName, index, newValue);
            } else {
                variantContainer.setVariant(variableName, newValue);
            }
        } else {
            value.setValue(newValue);
        }
    }


    @Override
    public void setMasterRunBlock(MasterRunBlock runBlock) {
        masterRunBlock = runBlock;
        if (leftNode != null) leftNode.setMasterRunBlock(runBlock);
        if (rightNode != null) rightNode.setMasterRunBlock(runBlock);

        if (funcParams != null) {
            funcParams.forEach(nd -> nd.setMasterRunBlock(runBlock));
        }
    }


    /**
     * Adds node as parameter, for "function" type node.
     */
    void addParameterNode(BaseNode paramNode) {
        if (funcParams == null)
            funcParams = new ArrayList<>();

        funcParams.add(paramNode);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ","{", "}")
                .add("o=" + operation)
                .add("l=" + leftNode)
                .add("r=" + rightNode)
                .add("v=" + value)
                .add("var='" + variableName + "'")
                .toString();
    }
}
