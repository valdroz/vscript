/*
 * Copyright 2020 Valerijus Drozdovas
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
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Valerijus Drozdovas
 * Created on 9/24/20
 */
public class TracingBaseNode extends BaseNode {
    private final Tracer tracer;

    public TracingBaseNode(String id, Tracer tracer) {
        super(id);
        this.tracer = tracer;
    }

    @Override
    BaseNode withLeftNode(BaseNode leftNode) {
        return super.withLeftNode(leftNode);
    }

    @Override
    public Variant execute(VariantContainer variantContainer) {
        try {
            Variant result = super.execute(variantContainer);
            tracer.put(getId(), result);
            List<String> params = Lists.newLinkedList();
            if (getParams() != null) {
                getParams().forEach(baseNode -> params.add(nodeAsTrace(baseNode)));
            }
            String buildInFuncName = EquationParser.functionNameFromCode(getNodeOperation());
            String message = "";
            String paramsMsg = "";
            if (params != null) {
                paramsMsg += Joiner.on(", ").join(params);
            }
            if (StringUtils.isNotBlank(buildInFuncName)) {
                message += "FUN: " + buildInFuncName + "(" + paramsMsg + ") YIELDS " + result;
            } else {
                switch (getNodeOperation()) {
                    case NT_VARIABLE:
                    case NT_LOCAL_VARIABLE:
                        message += "GET: " + getName() + "";
                        if (StringUtils.isNotBlank(paramsMsg)) {
                            message += "[" + paramsMsg + "]";
                        }
                        message += " IS " + result;
                        break;
                    case NT_FUNCTION:
                        message += "FUN: " + getName() + "(" + paramsMsg + ") YIELDS " + result;
                        break;
                    case '=':
                        message += "SET: " + getLeftNode().getName();
                        if (StringUtils.isNotBlank(paramsMsg)) {
                            message += "[" + paramsMsg + "]";
                        }
                        message += " TO " + result;
                        break;
                    case '!':
                        message += "OPR: NOT " + nodeAsTrace(getLeftNode()) + " YIELDS " + result;
                        break;
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case '&':
                    case '|':
                    case '>':
                    case '<':
                    case '^':
                    case NT_LOP_AND:
                    case NT_LOP_OR:
                    case NT_LOP_EQUALS:
                    case NT_LOP_NOT_EQUALS:
                    case NT_LOP_MORE_EQUALS:
                    case NT_LOP_LESS_EQUALS:
                        message += "OPR: " + nodeAsTrace(getLeftNode()) +
                                " " + toOpString(getNodeOperation()) + " " + nodeAsTrace(getRightNode()) + " YIELDS " + result;
                        break;
                    default:
                        message += "OPR: " + toOpString(getNodeOperation()) + " YIELDS: " + result;
                        break;
                }
            }

            tracer.trace(message);
            return result;
        } catch (Exception ex) {
            tracer.trace("ERROR: " + ex.getMessage());
            return Variant.nullVariant();
        }
    }

    private String nodeAsTrace(BaseNode node) {
        return tracer.get(node.getId()).toString();
    }

    protected static String toOpString(int op) {
        switch (op) {
            case '+':
                return "PLUS";
            case '-':
                return "MINUS";
            case '*':
                return "MULTIPLY";
            case '/':
                return "DIVIDE";
            case '&':
                return "LOGICAL AND";
            case '|':
                return "LOGICAL OR";
            case '>':
                return "MORE THAN";
            case '<':
                return "LESS THAN";
            case '^':
                return "POWER";
            case NT_LOP_AND:
                return "AND";
            case NT_LOP_OR:
                return "OR";
            case NT_LOP_EQUALS:
                return "EQUALS TO";
            case NT_LOP_NOT_EQUALS:
                return "NOT EQUALS TO";
            case NT_LOP_MORE_EQUALS:
                return "MORE OR EQUALS TO";
            case NT_LOP_LESS_EQUALS:
                return "LESS OR EQUALS TO";
            default:
                return String.valueOf(op);
        }
    }
}
