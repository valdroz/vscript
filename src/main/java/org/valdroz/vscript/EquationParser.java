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

import java.util.Optional;

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

    /**
     * Construct equation parser
     *
     * @param source - Equation expression
     */
    EquationParser(String source) {
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
        stopAt = to < source.length() ? to : source.length();
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
    private static char determineMathFuncCode(String funcName) {
        String fn = funcName.toLowerCase();

        switch (fn) {
            case "sin":
                return NT_MF_SIN;
            case "cos":
                return NT_MF_COS;
            case "asin":
                return NT_MF_ASIN;
            case "acos":
                return NT_MF_ACOS;
            case "tan":
                return NT_MF_TAN;
            case "atan":
                return NT_MF_ATAN;
            case "abs":
                return NT_MF_ABS;
            case "neg":
                return NT_MF_NEG;
            case "sqrt":
                return NT_MF_SQRT;
            case "log":
                return NT_MF_LOG;
            case "exp":
                return NT_MF_EXP;
            case "debug":
                return NT_MF_DEBUG;
            case "day":
                return NT_MF_DAY;
            case "month":
                return NT_MF_MONTH;
            case "year":
                return NT_MF_YEAR;
            case "day_of_year":
                return NT_MF_DAY_OF_YEAR;
            case "days_in_month":
                return NT_MF_DAYS_IN_MONTH;
            case "now":
                return NT_MF_NOW;
            case "iso":
                return NT_MF_ISO;
            case "days_before_now":
                return NT_MF_DAYS_BEFORE_NOW;
            case "hours_before_now":
                return NT_MF_HOURS_BEFORE_NOW;
            case "size":
                return NT_MF_SIZE;
            case "isnull":
            case "is_null":
                return NT_MF_IS_NULL;
            case "isnumeric":
            case "is_numeric":
                return NT_MF_IS_NUMBER;
            case "isstring":
            case "is_string":
                return NT_MF_IS_STRING;
            case "isarray":
            case "is_array":
                return NT_MF_IS_ARRAY;
            default:
                return '\0';

        }
    }

    /**
     * Parses assignment node.
     */
    private BaseNode parseAssignmentNode() {
        BaseNode left = parseLogicOpNode();

        if (left == null) return null;
        skipSpaces();

        while ((currentCharCheckExpSeparator() == '=' && charAtCheckExpSeparator(position + 1) != '=')) {
            if (left.getNodeOperation() == NT_CONSTANT) {
                throw new EvaluationException(CE_CONST_ASSIGNMENT, currentLineNumber(), currentPosition());
            }
            BaseNode node = new BaseNode();
            node.setLeftNode(left);
            node.setNodeOperation(currentCharCheckExpSeparator());
            forwardPosition();
            skipSpaces();
            node.setRightNode(parseLogicOpNode());
            if (node.getRightNode() == null) {
                return null;
            }
            left = node;
        }

        return left;
    }

    /**
     * Parses the node for logical `&&` and `||` operators.
     */
    private BaseNode parseLogicOpNode() {
        BaseNode left = parsePlusMinus();

        if (left == null) return null;
        skipSpaces();

        while ((currentCharCheckExpSeparator() == '|' && charAtCheckExpSeparator(position + 1) == '|') ||
                (currentCharCheckExpSeparator() == '&' && charAtCheckExpSeparator(position + 1) == '&')) {
            BaseNode node = new BaseNode();
            node.setLeftNode(left);

            if (currentCharCheckExpSeparator() == '&')
                node.setNodeOperation(NT_LOP_AND);
            else if (currentCharCheckExpSeparator() == '|')
                node.setNodeOperation(NT_LOP_OR);

            forwardPosition();
            forwardPosition();
            skipSpaces();
            node.setRightNode(parsePlusMinus());
            if (node.getRightNode() == null) {
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
            BaseNode node = new BaseNode();
            node.setLeftNode(left);
            node.setNodeOperation(currentCharCheckExpSeparator());

            forwardPosition();
            skipSpaces();
            node.setRightNode(parseMultiplicationDivisionOperator());
            if (node.getRightNode() == null) {
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
            BaseNode node = new BaseNode();
            node.setLeftNode(left);
            node.setNodeOperation(currentCharCheckExpSeparator());
            forwardPosition();
            skipSpaces();
            node.setRightNode(parsePowerOperator());
            if (node.getRightNode() == null) {
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
            BaseNode node = new BaseNode();
            node.setLeftNode(left);
            node.setNodeOperation(currentCharCheckExpSeparator());
            forwardPosition();
            skipSpaces();
            node.setRightNode(parseComparisonOperator());
            if (node.getRightNode() == null) {
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
            BaseNode node = new BaseNode();
            node.setLeftNode(left);
            node.setNodeOperation(currentCharCheckExpSeparator());

            if (charAtCheckExpSeparator(position + 1) == '=') {
                switch (node.getNodeOperation()) {
                    case '>': // >=
                        node.setNodeOperation(NT_LOP_MORE_EQUALS);
                        forwardPosition();
                        break;
                    case '<': // >=
                        node.setNodeOperation(NT_LOP_LESS_EQUALS);
                        forwardPosition();
                        break;
                    case '=': // >=
                        node.setNodeOperation(NT_LOP_EQUALS);
                        forwardPosition();
                        break;
                    case '!': // >=
                        node.setNodeOperation(NT_LOP_NOT_EQUALS);
                        forwardPosition();
                        break;
                }
            }

            forwardPosition();
            skipSpaces();

            node.setRightNode(parseBinAndOrOperator()); // for ! oprs.
            if (node.getRightNode() == null) {
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
            BaseNode node = new BaseNode();
            node.setLeftNode(left);
            node.setNodeOperation(currentCharCheckExpSeparator());

            forwardPosition();
            skipSpaces();

            node.setRightNode(parseNotOperator());
            if (node.getRightNode() == null) {
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
            BaseNode node = new BaseNode();
            node.setNodeOperation(currentCharCheckExpSeparator());
            forwardPosition();
            skipSpaces();
            node.setRightNode(parseNotOperator());
            if (node.getRightNode() == null) {
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
                node = new BaseNode();
                node.setNodeOperation(NT_VALUE);
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

        if (currentChar() == '-' ) {
            forwardPosition();
            skipSpaces();
            String digit = null;
            int _pos = currentPosition();
            if (isDigit()) {
                digit = readWord();
                node = new BaseNode();
                node.setNodeOperation(NT_VALUE);
                try {
                    node.assignValue(Variant.fromBigDecimal('-' + digit));
                } catch (Exception ex) {
                    throw new EvaluationException(ex.getMessage(), currentLineNumber(), currentPosition());
                }
            } else {
                throw new EvaluationException("Expecting negative digit, however found '" + digit + "'", currentLineNumber(), _pos);
            }
        } else if (isDigit()) {
            String digit = readWord();
            node = new BaseNode();
            node.setNodeOperation(NT_VALUE);
            try {
                node.assignValue(Variant.fromBigDecimal(digit));
            } catch (Exception ex) {
                throw new EvaluationException(ex.getMessage(), currentLineNumber(), currentPosition());
            }
        } else if (isText()) {
            String text = readTextSequence();
            node = new BaseNode();
            node.setNodeOperation(NT_VALUE);
            node.assignValue(Variant.fromString(text));
        } else if (isLiteralChar()) {
            int prevPos = currentPosition();
            String word = readWord();
            if (word.length() == 0) return null;

            skipSpaces();
            boolean isFunction = currentCharCheckExpSeparator() == '(';
            boolean isArray = currentCharCheckExpSeparator() == '[';

            char mathFuncOpCode = determineMathFuncCode(word);

            if (mathFuncOpCode > 0 && isFunction) {
                node = new BaseNode();
                node.setNodeOperation(mathFuncOpCode);
                node.setLeftNode(parseEqFactor());
                if (node.getLeftNode() == null) {
                    return null;
                }
            } else if (!isFunction && isArray) {
                forwardPosition();
                BaseNode indexNode = parseAssignmentNode();

                if (indexNode == null) return null;

                if (currentCharCheckExpSeparator() != ']') {
                    throw new EvaluationException(CE_MISSING_BRACKET3, currentLineNumber(), currentPosition());
                }

                node = new BaseNode();
                node.setNodeOperation(NT_VARIABLE);
                node.setLeftNode(indexNode);
                node.setName(word);

                forwardPosition();
                skipSpaces();
                if ( currentChar() == '?' ) {
                    forwardPosition();
                    node.setValueSubstitution(parseNotOperator());
                }
            } else if (!isFunction) {
                node = new BaseNode();
                if (word.equals("var")) {
                    skipSpaces();
                    if (isLiteralChar()) {
                        word = readWord();
                        BaseNode constNode = checkConstants(word);
                        if (constNode != null) {
                            node = constNode;
                        } else {
                            node.setNodeOperation(NT_LOCAL_VARIABLE);
                            node.setName(word);
                        }
                    } else {
                        throw new EvaluationException(CE_ILLEGAL_VAR_NAME, currentLineNumber(), currentPosition());
                    }
                } else {
                    BaseNode constNode = checkConstants(word);
                    if (constNode != null) {
                        node = constNode;
                    } else {
                        node.setNodeOperation(NT_VARIABLE);
                        node.setName(word);
                        skipSpaces();
                        if ( currentChar() == '?' ) {
                            forwardPosition();
                            node.setValueSubstitution(parseNotOperator());
                        }
                    }
                }
            } else {
                setPosition(prevPos);
                node = parseFunction();
                skipSpaces();
                if ( currentChar() == '?' ) {
                    forwardPosition();
                    node.setValueSubstitution(parseNotOperator());
                }
            }
        }

        return node;
    }

    private BaseNode checkConstants(String constName) {
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


    private BaseNode parseFunction() {
        skipSpaces();
        String funcName = readWord();

        BaseNode node = new BaseNode();

        node.setNodeOperation(NT_FUNCTION);
        node.initParams();
        node.setName(funcName);

        skipSpaces();
        if (currentCharCheckExpSeparator() == '(') {
            forwardPosition();
            while (true) {
                BaseNode paramNode = parseAssignmentNode();
                if (paramNode != null) node.addParameterNode(paramNode);

                if (currentChar() == ')') {
                    forwardPosition();
                    skipSpaces();
                    break;
                } else if (currentChar() != ',') {
                    throw new EvaluationException("Expected ',' however got '" + currentChar() + "'", currentLineNumber(), currentPosition() );
                }
                forwardPosition();
                skipSpaces();
            }
        } else {
            throw new EvaluationException("Expected function. Missing open '('", currentLineNumber(), currentPosition() );
        }

        return node;
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
     * Sets the position on the script.
     */
    private void setPosition(int pos) {
        position = pos;
    }

    /**
     * Return current script line.
     */
    int currentLineNumber() {
        return currentLine;
    }

}
