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

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Equation parser.
 *
 * @author Valerijus Drozdovas
 */
class EquationParser implements Constants {
    private String source;
    private int currentLine = 0;
    private int position = 0;
    private int stopAt = -1;
    private AtomicInteger idgen = new AtomicInteger(1);

    private final Tracer tracer;

    private static final BiMap<String, Integer> functionCodes = ImmutableBiMap.<String, Integer>builder()
            .put("sin", NT_MF_SIN)
            .put("cos", NT_MF_COS)
            .put("asin", NT_MF_ASIN)
            .put("acos", NT_MF_ACOS)
            .put("tan", NT_MF_TAN)
            .put("atan", NT_MF_ATAN)
            .put("abs", NT_MF_ABS)
            .put("neg", NT_MF_NEG)
            .put("sqrt", NT_MF_SQRT)
            .put("log", NT_MF_LOG)
            .put("exp", NT_MF_EXP)
            .put("debug", NT_MF_DEBUG)
            .put("day", NT_MF_DAY)
            .put("month", NT_MF_MONTH)
            .put("year", NT_MF_YEAR)
            .put("day_of_year", NT_MF_DAY_OF_YEAR)
            .put("days_in_month", NT_MF_DAYS_IN_MONTH)
            .put("now", NT_MF_NOW)
            .put("iso", NT_MF_ISO)
            .put("days_before_now", NT_MF_DAYS_BEFORE_NOW)
            .put("hours_before_now", NT_MF_HOURS_BEFORE_NOW)
            .put("size", NT_MF_SIZE)
            //.put("isnull", NT_MF_IS_NULL)
            .put("is_null", NT_MF_IS_NULL)
            //.put("isnumeric", NT_MF_IS_NUMBER)
            .put("is_numeric", NT_MF_IS_NUMBER)
            //.put("isstring", NT_MF_IS_STRING)
            .put("is_string", NT_MF_IS_STRING)
            //.put("isarray", NT_MF_IS_ARRAY)
            .put("is_array", NT_MF_IS_ARRAY)
            .put("to_array", NT_MF_TO_ARRAY)
            .build();


    /**
     * Construct equation parser
     *
     * @param source - Equation expression
     */
    EquationParser(String source, TraceListener traceListener) {
        if (traceListener != null) {
            this.tracer = new Tracer(traceListener);
        } else {
            this.tracer = null;
        }
        this.source = Optional.ofNullable(source).orElse(Variant.EMPTY_STRING).trim();
        if (this.source.length() == 0) {
            this.source = Configuration.getExpressionForEmptyEval();
        }
        currentLine = 1;
        stopAt = this.source.length();
        position = 0;
    }

    /**
     * Parse equation.
     *
     * @param from - starting position.
     * @return The BaseNode object.
     */
    BaseNode parse(int from) {
        return parse(from, source.length());
    }

    /**
     * Method <src>parse</src> parses and return node object.
     *
     * @param from - starting position in the source text.
     * @param to   - ending position in the source text.
     * @return The BaseNode object.
     */
    BaseNode parse(int from, int to) {
        stopAt = Math.min(to, source.length());
        position = from;
        skipSpaces();
        BaseNode node = parseAssignmentNode();
        if (node == null) {
            throw new EvaluationException(CE_SYNTAX, currentLineNumber(), currentPosition());
        }
        stopAt = source.length();
        return node;
    }

    String unprocessedSource() {
        if (position + 1 < stopAt) {
            return source.substring(position, stopAt);
        }
        return "";
    }

    /**
     * Method <src>determineMathFuncCode</src> return internal script
     * function code by it name.
     */
    private static int determineBuildinFuncCode(String funcName) {
        String fn = funcName.toLowerCase();
        return functionCodes.getOrDefault(fn, 0);
    }

    static String functionNameFromCode(int code) {
        return functionCodes.inverse().getOrDefault(code, StringUtils.EMPTY);
    }

    private BaseNode newNode() {
        return (tracer != null) ?
                new TracingBaseNode(newNodeId(), tracer) :
                new BaseNode(newNodeId());
    }

    private BaseNode newConstantNode(){
        return (tracer != null) ?
                new TracingConstantNode(newNodeId(), tracer) :
                new ConstantNode(newNodeId());
    }

    private String newNodeId(){
        return "n" + idgen.getAndIncrement();
    }

    private BaseNode newConstantNode(Variant variant){
        return (tracer != null) ?
                new TracingConstantNode(newNodeId(), variant, tracer) :
                new ConstantNode(newNodeId(), variant);
    }

    /**
     * Parses assignment node.
     */
    private BaseNode parseAssignmentNode() {
        BaseNode left = parseLogicOrNode();

        if (left == null) return null;
        skipSpaces();

        while ((currentCharCheckExpSeparator() == '=' && charAtCheckExpSeparator(position + 1) != '=')) {
            if (left.getNodeOperation() != NT_VARIABLE && left.getNodeOperation() != NT_LOCAL_VARIABLE) {
                throw new EvaluationException(CE_CONST_ASSIGNMENT, currentLineNumber(), currentPosition());
            }
            BaseNode node = newNode()
                    .withLeftNode(left)
                    .withNodeOperation(currentCharCheckExpSeparator());
            forwardPosition();
            skipSpaces();
            if (node.setRightNode(parseLogicOrNode()) == null) {
                return null;
            }
            left = node;
        }

        return left;
    }

    /**
     * Parses the node for logical `&&` and `||` operators.
     */
    private BaseNode parseLogicOrNode() {
        BaseNode left = parseLogicAndNode();

        if (left == null) return null;
        skipSpaces();

        while (currentCharCheckExpSeparator() == '|' && charAtCheckExpSeparator(position + 1) == '|') {
            BaseNode node = newNode()
                    .withLeftNode(left)
                    .withNodeOperation(NT_LOP_OR);

            forwardPosition();
            forwardPosition();
            skipSpaces();
            if (node.setRightNode(parseLogicAndNode()) == null) {
                return null;
            }
            left = node;
        }

        return left;
    }

    private BaseNode parseLogicAndNode() {
        BaseNode left = parsePlusMinus();

        if (left == null) return null;
        skipSpaces();

        while (currentCharCheckExpSeparator() == '&' && charAtCheckExpSeparator(position + 1) == '&') {
            BaseNode node = newNode()
                    .withLeftNode(left)
                    .withNodeOperation(NT_LOP_AND);

            forwardPosition();
            forwardPosition();
            skipSpaces();
            if (node.setRightNode(parsePlusMinus()) == null) {
                return null;
            }
            left = node;
        }

        return left;
    }


    /**
     * Parses} the node for +/- operators.
     */
    private BaseNode parsePlusMinus() {
        BaseNode left = parseMultiplicationDivisionOperator();

        if (left == null) return null;
        skipSpaces();

        while (currentCharCheckExpSeparator() == '-' || currentCharCheckExpSeparator() == '+') {
            BaseNode node = newNode()
                    .withLeftNode(left)
                    .withNodeOperation(currentCharCheckExpSeparator());

            forwardPosition();
            skipSpaces();
            if (node.setRightNode(parseMultiplicationDivisionOperator()) == null) {
                throw new EvaluationException(CE_INCOMPLETE, currentLineNumber(), currentPosition());
            }
            left = node;
        }

        return left;
    }


    private BaseNode parseMultiplicationDivisionOperator() {
        BaseNode left = parsePowerOperator();

        if (left == null) return null;
        skipSpaces();

        while (currentCharCheckExpSeparator() == '*' || currentCharCheckExpSeparator() == '/') {
            BaseNode node = newNode()
                    .withLeftNode(left)
                    .withNodeOperation(currentCharCheckExpSeparator());
            forwardPosition();
            skipSpaces();
            if (node.setRightNode(parsePowerOperator()) == null) {
                return null;
            }
            left = node;
        }

        return left;
    }


    /**
     * Parses power/root operator: ^, $
     */
    private BaseNode parsePowerOperator() {
        BaseNode left = parseComparisonOperator();

        if (left == null) return null;
        skipSpaces();

        while (currentCharCheckExpSeparator() == '^' || currentCharCheckExpSeparator() == '$') {
            BaseNode node = newNode()
                    .withLeftNode(left)
                    .withNodeOperation(currentCharCheckExpSeparator());
            forwardPosition();
            skipSpaces();
            if (node.setRightNode(parseComparisonOperator()) == null) {
                return null;
            }
            left = node;
        }

        return left;
    }


    /**
     * Parses: >, <, >=, <=, ==.
     */
    private BaseNode parseComparisonOperator() {
        BaseNode left = parseBinAndOrOperator(); // for ! oprs.

        if (left == null) return null;
        skipSpaces();

        if (currentCharCheckExpSeparator() == '>' || currentCharCheckExpSeparator() == '<' ||
                (currentCharCheckExpSeparator() == '=' && charAtCheckExpSeparator(position + 1) == '=') ||
                (currentCharCheckExpSeparator() == '!' && charAtCheckExpSeparator(position + 1) == '=')) {
            BaseNode node = newNode()
                    .withLeftNode(left)
                    .withNodeOperation(currentCharCheckExpSeparator());

            if (charAtCheckExpSeparator(position + 1) == '=') {
                switch (node.getNodeOperation()) {
                    case '>': // >=
                        node.withNodeOperation(NT_LOP_MORE_EQUALS);
                        forwardPosition();
                        break;
                    case '<': // <=
                        node.withNodeOperation(NT_LOP_LESS_EQUALS);
                        forwardPosition();
                        break;
                    case '=': // ==
                        node.withNodeOperation(NT_LOP_EQUALS);
                        forwardPosition();
                        break;
                    case '!': // !=
                        node.withNodeOperation(NT_LOP_NOT_EQUALS);
                        forwardPosition();
                        break;
                }
            }

            forwardPosition();
            skipSpaces();

            if (node.setRightNode(parseBinAndOrOperator()) == null) {
                return null;
            }
            return node;
        }

        return left;
    }


    /**
     * Parse: AND/OR (&, |)
     */
    private BaseNode parseBinAndOrOperator() {
        BaseNode left = parseNotOperator();

        if (left == null) return null;
        skipSpaces();

        while ((currentCharCheckExpSeparator() == '&' && charAtCheckExpSeparator(position + 1) != '&') ||
                (currentCharCheckExpSeparator() == '|' && charAtCheckExpSeparator(position + 1) != '|')) {
            BaseNode node = newNode()
                    .withLeftNode(left)
                    .withNodeOperation(currentCharCheckExpSeparator());

            forwardPosition();
            skipSpaces();

            if (node.setRightNode(parseNotOperator()) == null) {
                return null;
            }
            left = node;
        }

        return left;
    }


    /**
     * Parse: NOT (!)
     */
    private BaseNode parseNotOperator() {
        skipSpaces();

        if (currentCharCheckExpSeparator() == '!') {
            BaseNode node = newNode()
                    .withNodeOperation(currentCharCheckExpSeparator());
            forwardPosition();
            skipSpaces();
            if (node.setLeftNode(parseNotOperator()) == null) {
                return null;
            }
            return node;
        }

        return parseEqFactor();
    }


    /**
     * Parse equation node or in bracketed expression.
     */
    private BaseNode parseEqFactor() {
        skipSpaces();
        if (currentCharCheckExpSeparator() == '(') {
            BaseNode node;
            forwardPosition();
            skipSpaces();
            if (currentCharCheckExpSeparator() == ')') {
                node = newConstantNode();
            } else {
                node = parseAssignmentNode();
                if (node == null) return null;
                skipSpaces();
                if (currentCharCheckExpSeparator() != ')') {
                    throw new EvaluationException(CE_MISSING_BRACKET, currentLineNumber(), currentPosition());
                }
            }
            forwardPosition();
            return node;
        }
        return parseAsIdentifiable();
    }


    /**
     * Parse node as variable, function or value.
     */
    private BaseNode parseAsIdentifiable() {
        BaseNode node = null;
        skipSpaces();

        if (currentChar() == '-') {
            forwardPosition();
            skipSpaces();
            String digit = null;
            int _pos = currentPosition();
            if (isDigit()) {
                digit = readWord();
                try {
                    node = newConstantNode(Variant.fromBigDecimal('-' + digit));
                } catch (Exception ex) {
                    throw new EvaluationException(ex.getMessage(), currentLineNumber(), currentPosition());
                }
            } else {
                throw new EvaluationException("Expecting negative digit, however found '" + digit + "'",
                        currentLineNumber(), _pos);
            }
        } else if (isDigit()) {
            String digit = readWord();
            try {
                node = newConstantNode(Variant.fromBigDecimal(digit));
            } catch (Exception ex) {
                throw new EvaluationException(ex.getMessage(), currentLineNumber(), currentPosition());
            }
        } else if (isText()) {
            String text = readTextSequence();
            node = newConstantNode(Variant.fromString(text));
        } else if (isLiteralChar()) {
            String word = readWord();
            if (word.length() == 0) return null;

            skipSpaces();
            boolean isFunction = currentCharCheckExpSeparator() == '(';
            boolean isArray = currentCharCheckExpSeparator() == '[';

            int mathFuncOpCode = determineBuildinFuncCode(word);

            if (isFunction) {
                if (mathFuncOpCode > 0) {
                    node = newNode()
                            .withNodeOperation(mathFuncOpCode);
                    if (parseParamsFor(node, '(', ')', ',') == 0) {
                        node.addParameterNode(Constants.C_NULL);
                    }
                } else {
                    node = newNode()
                            .withNodeOperation(NT_FUNCTION)
                            .withName(word);
                    parseParamsFor(node, '(', ')', ',');
                }
                skipSpaces();
                if (currentChar() == '?') {
                    forwardPosition();
                    node.setValueSubstitution(parseNotOperator());
                }
            } else if (isArray) {
                node = newNode()
                        .withNodeOperation(NT_VARIABLE)
                        .withName(word);
                if (parseParamsFor(node, '[', ']', ',') != 1) return null;
                skipSpaces();
                if (currentChar() == '?') {
                    forwardPosition();
                    node.setValueSubstitution(parseNotOperator());
                }
            } else {
                node = newNode();
                if (word.equals("var")) {
                    skipSpaces();
                    if (isLiteralChar()) {
                        word = readWord();
                        BaseNode constNode = checkConstants(word);
                        if (constNode != null) {
                            node = constNode;
                        } else {
                            node.withNodeOperation(NT_LOCAL_VARIABLE).withName(word);
                        }
                    } else {
                        throw new EvaluationException(CE_ILLEGAL_VAR_NAME, currentLineNumber(), currentPosition());
                    }
                } else {
                    BaseNode constNode = checkConstants(word);
                    if (constNode != null) {
                        node = constNode;
                    } else {
                        node.withNodeOperation(NT_VARIABLE).withName(word);
                        skipSpaces();
                        if (currentChar() == '?') {
                            forwardPosition();
                            node.setValueSubstitution(parseNotOperator());
                        }
                    }
                }
            }
        }

        return node;
    }

    private static BaseNode checkConstants(String constName) {
        switch (constName) {
            case "true":
                return C_TRUE;
            case "false":
                return C_FALSE;
            case "PI":
                return C_PI;
            case "E":
                return C_E;
            case "NULL":
            case "null":
                return C_NULL;
        }
        return null;
    }

    private int parseParamsFor(BaseNode node, char openChar, char closeChar, char sep) {
        int paramCount = 0;
        node.initParams();
        skipSpaces();
        if (currentCharCheckExpSeparator() == openChar) {
            forwardPosition();
            while (true) {
                BaseNode paramNode = parseAssignmentNode();
                if (paramNode != null) {
                    node.addParameterNode(paramNode);
                    paramCount += 1;
                }
                if (currentChar() == closeChar) {
                    forwardPosition();
                    skipSpaces();
                    break;
                } else if (currentChar() != sep) {
                    throw new EvaluationException("Expected ',' however got '" + currentChar() + "'",
                            currentLineNumber(), currentPosition());
                }
                forwardPosition();
                skipSpaces();
            }
        } else {
            throw new EvaluationException("Expected enclosed parameter(s). Missing open " + openChar,
                    currentLineNumber(), currentPosition());
        }
        return paramCount;
    }

    /**
     * Returns char at the defined script position. Depends on char.
     *
     * @param position - position in the script.
     */
    private char charAtCheckExpSeparator(int position) {
        char retchar = '\0';
        if (position < stopAt) {
            retchar = source.charAt(position);
            if (retchar == ';') retchar = '\0';
            else if (retchar == ',') retchar = '\0';
        } else if (position == stopAt) {
            retchar = 32;
        }
        return retchar;
    }


    private char currentCharCheckExpSeparator() {
        return charAtCheckExpSeparator(position);
    }

    /**
     * Return char at the current script position, independ on char.
     */
    private char currentChar() {
        return charAt(position);
    }

    /**
     * Return char at the defined script position, independ on char.
     */
    private char charAt(int position) {
        char retchar = '\0';
        if (position < stopAt) {
            retchar = source.charAt(position);
        } else if (position == stopAt)
            retchar = 32;

        return retchar;
    }


    /**
     * Skip current position to the next script character to be performed.
     */
    private void skipSpaces() {
        char c;
        while ((c = charAtCheckExpSeparator(position)) > 0 && c <= 32) {
            if (c == 10) {
                ++currentLine;
            }
            ++position;
        }
    }


    /**
     * Increments script position by one.
     */
    private void forwardPosition() {
        ++position;
    }

    /**
     * Return the word at the current script position. The position is moved to
     * end of returned word.
     */
    private String readWord() {
        String ret = "";
        skipSpaces();

        if (isDigit()) {
            while (isDigit()) {
                ret += currentCharCheckExpSeparator();
                forwardPosition();
            }
        } else if (isLiteralChar()) {
            while (isLiteralChar() || isDigit()) {
                ret += currentCharCheckExpSeparator();
                forwardPosition();
            }
        } else if (isOperatorSymbol()) {
            while (isOperatorSymbol()) {
                ret += currentCharCheckExpSeparator();
                forwardPosition();
            }
        }

        return ret;
    }

    /**
     * Method which determine that character at the current position is digit char.
     */
    private boolean isDigit() {
        return charAtCheckExpSeparator(position) >= 0x30 && charAtCheckExpSeparator(position) <= 0x39 ||
                charAtCheckExpSeparator(position) == '.';
    }

    /**
     * Method which determines that character at the current script position is literal.
     */
    private boolean isLiteralChar() {
        char c = charAtCheckExpSeparator(position);
        return c >= 0x41 && c <= 0x5a || c >= 0x61 && c <= 0x7a || c == '_';
    }

    /**
     *
     */
    private boolean isText() {
        return charAtCheckExpSeparator(position) == '"';
    }

    /**
     *
     */
    private boolean isOperatorSymbol() {
        char ch = charAtCheckExpSeparator(position);
        switch (ch) {
            case '!':
            case '*':
            case '/':
            case '+':
            case '-':
            case '&':
            case '|':
            case '>':
            case '<':
            case '=':
            case '$':
            case '^':
                return true;
        }

        return false;
    }


    /**
     *
     */
    private String readTextSequence() {
        String text = "";

        if (isText()) {
            forwardPosition();
            boolean next = true;
            boolean specialChar = false;
            char ch = '\0';
            while (next) {
                ch = currentChar();
                forwardPosition();
                if (ch > 0 && ch < 32 && ch != 13 && ch != 10) {
                    throw new EvaluationException(CE_ILLEGAL_SYMBOL, currentLineNumber(), currentPosition());
                } else if (ch == 13 || ch == 10 || ch == 0) {
                    throw new EvaluationException(CE_MISSING_QUOTATION, currentLineNumber(), currentPosition());
                }

                if (ch == '\\' && !specialChar) {
                    ch = currentChar();
                    forwardPosition();
                    specialChar = true;
                }

                if (specialChar)
                    if (ch == 'n') {
                        text += (char) 13;
                        text += (char) 10;
                    } else if (ch == 't')
                        text += (char) 9;
                    else
                        text += ch;
                else if (ch == '"')
                    next = false;
                else
                    text += ch;

                specialChar = false;
            }
        }

        return text;
    }

    /**
     * Return current script position.
     */
    int currentPosition() {
        return position;
    }

    /**
     * Return current script line.
     */
    int currentLineNumber() {
        return currentLine;
    }

}
