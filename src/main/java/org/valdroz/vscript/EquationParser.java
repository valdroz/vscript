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

/**
 * Equation parser.
 *
 * @author Valerijus Drozdovas
 */
class EquationParser implements Constants {

    String source;

    int sourceLength = 0;

    private int currentLine = 0;

    char lastErrorCode = 0;

    int position = 0;
    int stopAt = -1;


    /**
     * Construct equation parser
     *
     * @param source - Equation expression
     */
    EquationParser(String source) {
        this.source = source;
        lastErrorCode = CE_SUCCESS;
        currentLine = 0;
        sourceLength = this.source.length();
        stopAt = source.length();
        position = 0;
    }

    /**
     * Parse equation.
     *
     * @param from - starting position.
     * @return The BaseNode object.
     */
    BaseNode parse(int from) {
        lastErrorCode = 0;
        stopAt = source.length();
        position = from;
        skipSpaces();
        BaseNode node = parseAssignmentNode();
        if (node == null && lastErrorCode == CE_SUCCESS) {
            lastErrorCode = CE_SYNTAX;
        }
        return node;
    }

    /**
     * Method <src>parse</src> parses and return node object.
     *
     * @param from - starting position in the source text.
     * @param to   - ending position in the source text.
     * @return The BaseNode object.
     */
    BaseNode parse(int from, int to) {
        lastErrorCode = 0;
        stopAt = to < source.length() ? to : source.length();
        position = from;
        skipSpaces();
        BaseNode node = parseAssignmentNode();
        if (node == null && lastErrorCode == CE_SUCCESS) {
            lastErrorCode = CE_SYNTAX;
        }
        stopAt = source.length();
        return node;
    }

    String getRemainderSource() {
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
            case "size":
                return NT_MF_SIZE;
            case "isnull":
                return NT_MF_IS_NULL;
            case "isnumeric":
                return NT_MF_IS_NUMBER;
            case "isstring":
                return NT_MF_IS_STRING;
            case "isarray":
                return NT_MF_IS_ARRAY;
            default:
                return '\0';

        }
    }

    /**
     * Generates the the node for +/- operators.
     */
    private BaseNode genPlusMinus() {
        BaseNode left = parseMultiplicationDivisionOperator();

        if (left == null) return null;
        skipSpaces();

        while (currentCharCheckExpSeparator() == '-' || currentCharCheckExpSeparator() == '+') {
            BaseNode node = new BaseNode();
            node.leftNode = left;
            node.operation = currentCharCheckExpSeparator();

            forwardPosition();
            skipSpaces();
            node.rightNode = parseMultiplicationDivisionOperator();
            if (node.rightNode == null) {
                this.lastErrorCode = CE_INCOMPLETE;
                return null;
            }
            return node;
        }

        return left;
    }

    /**
     * Parses the node for logical `&&` and `||` operators.
     */
    private BaseNode parseLogicOpNode() {
        BaseNode left = genPlusMinus();

        if (left == null) return null;
        skipSpaces();

        while ((currentCharCheckExpSeparator() == '|' && charAtCheckExpSeparator(position + 1) == '|') ||
                (currentCharCheckExpSeparator() == '&' && charAtCheckExpSeparator(position + 1) == '&')) {
            BaseNode node = new BaseNode();
            node.leftNode = left;

            if (currentCharCheckExpSeparator() == '&')
                node.operation = NT_LOP_AND;
            else if (currentCharCheckExpSeparator() == '|')
                node.operation = NT_LOP_OR;

            forwardPosition();
            forwardPosition();
            skipSpaces();
            node.rightNode = genPlusMinus();
            if (node.rightNode == null) {
                return null;
            }
            return node;
        }

        return left;
    }

    /**
     * Parses assignment node.
     */
    private BaseNode parseAssignmentNode() {
        BaseNode leftNode = parseLogicOpNode();

        if (leftNode == null) return null;
        skipSpaces();

        while ((currentCharCheckExpSeparator() == '=' && charAtCheckExpSeparator(position + 1) != '=')) {
            if (leftNode.operation == NT_CONSTANT) {
                lastErrorCode = CE_CONST_ASSIGNMENT;
                return null;
            }
            BaseNode node = new BaseNode();
            node.leftNode = leftNode;
            node.operation = currentCharCheckExpSeparator();
            forwardPosition();
            skipSpaces();
            node.rightNode = parseLogicOpNode();
            if (node.rightNode == null) {
                return null;
            }
            leftNode = node;
        }

        return leftNode;
    }


    private BaseNode parseMultiplicationDivisionOperator() {
        BaseNode left = parsePowerOperator();

        if (left == null) return null;
        skipSpaces();

        while (currentCharCheckExpSeparator() == '*' || currentCharCheckExpSeparator() == '/') {
            BaseNode node = new BaseNode();
            node.leftNode = left;
            node.operation = currentCharCheckExpSeparator();
            forwardPosition();
            skipSpaces();
            node.rightNode = parsePowerOperator();
            if (node.rightNode == null) {
                return null;
            }
            return node;
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
            node.leftNode = left;
            node.operation = currentCharCheckExpSeparator();
            forwardPosition();
            skipSpaces();
            node.rightNode = parseComparisonOperator();
            if (node.rightNode == null) {
                return null;
            }
            return node;
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

        while (currentCharCheckExpSeparator() == '>' || currentCharCheckExpSeparator() == '<' ||
                (currentCharCheckExpSeparator() == '=' && charAtCheckExpSeparator(position + 1) == '=') ||
                (currentCharCheckExpSeparator() == '!' && charAtCheckExpSeparator(position + 1) == '=')) {
            BaseNode node = new BaseNode();
            node.leftNode = left;
            node.operation = currentCharCheckExpSeparator();

            if (charAtCheckExpSeparator(position + 1) == '=') {
                switch (node.operation) {
                    case '>': // >=
                        node.operation = NT_LOP_MORE_EQUALS;
                        forwardPosition();
                        break;
                    case '<': // >=
                        node.operation = NT_LOP_LESS_EQUALS;
                        forwardPosition();
                        break;
                    case '=': // >=
                        node.operation = NT_LOP_EQUALS;
                        forwardPosition();
                        break;
                    case '!': // >=
                        node.operation = NT_LOP_NOT_EQUALS;
                        forwardPosition();
                        break;
                }
            }

            forwardPosition();
            skipSpaces();

            node.rightNode = parseBinAndOrOperator(); // for ! oprs.
            if (node.rightNode == null) {
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
                (currentCharCheckExpSeparator() == '|' && currentCharCheckExpSeparator() != '|')) {
            BaseNode node = new BaseNode();
            node.leftNode = left;
            node.operation = currentCharCheckExpSeparator();

            forwardPosition();
            skipSpaces();

            node.rightNode = parseNotOperator();
            if (node.rightNode == null) {
                return null;
            }
            return node;
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
            node.operation = currentCharCheckExpSeparator();
            forwardPosition();
            skipSpaces();
            node.rightNode = parseNotOperator();
            if (node.rightNode == null) {
                return null;
            }
            return node;
        }

        return parseEqFactor();
    }


    /**
     * Generates the node for the identifier with sign, or equation in brackets.
     */
    private BaseNode parseEqFactor() {
        BaseNode left;
        skipSpaces();

        if (currentCharCheckExpSeparator() == '-' || currentCharCheckExpSeparator() == '+') {
            BaseNode node = new BaseNode();
            left = new BaseNode();
            left.operation = NT_VALUE;
            left.value.setValue(currentCharCheckExpSeparator() == '-' ? -1.0 : 1.0);
            node.leftNode = left;
            node.operation = '*';

            forwardPosition();

            node.rightNode = parseAssignmentNode();
            if (node.rightNode == null) {
                return null;
            }
            return node;
        }

        if (currentCharCheckExpSeparator() == '(') {
            BaseNode node;
            forwardPosition();
            skipSpaces();
            if (currentCharCheckExpSeparator() == ')') {
                node = new BaseNode();
                node.operation = NT_VALUE;
                node.value.setValue(0);
            } else {
                node = parseAssignmentNode();
                if (node == null) return null;
                skipSpaces();
                if (currentCharCheckExpSeparator() != ')') {
                    lastErrorCode = CE_MISSING_BRACKET;
                    return null;
                }
            }
            forwardPosition();

            return node;
        }

        return parseAsIdentifiable();
    }


    /**
     * Generates node as variable, function or value.
     */
    private BaseNode parseAsIdentifiable() {
        BaseNode node = null;
        skipSpaces();

        if (isDigit()) {
            String digit = readWord();
            node = new BaseNode();
            node.operation = NT_VALUE;
            try {
                node.value.setValue(Double.valueOf(digit));
            } catch (Exception ex) {
                lastErrorCode = CE_SYNTAX;
                return null;
            }
        } else if (isText()) {
            String text = readTextSequence();
            if (text == null) {
                if (lastErrorCode == 0) lastErrorCode = CE_TEXT_SYNTAX;
                return null;
            }
            node = new BaseNode();
            node.operation = NT_VALUE;
            node.value.setValue(text);
        } else if (isLiteralChar()) {
            int prevPos = getPosition();
            String word = readWord();
            if (word.length() == 0) return null;

            skipSpaces();
            boolean isFunction = currentCharCheckExpSeparator() == '(';
            boolean isArray = currentCharCheckExpSeparator() == '[';

            char mathFuncOpCode = determineMathFuncCode(word);

            if (mathFuncOpCode > 0 && isFunction) {
                node = new BaseNode();
                node.operation = mathFuncOpCode;
                node.leftNode = parseEqFactor();
                if (node.leftNode == null) {
                    return null;
                }
            } else if (!isFunction && isArray) {
                forwardPosition();
                BaseNode indexNode = parseAssignmentNode();

                if (indexNode == null) return null;

                if (currentCharCheckExpSeparator() != ']') {
                    lastErrorCode = CE_MISSING_BRACKET3;
                    return null;
                }
                forwardPosition();
                skipSpaces();

                node = new BaseNode();
                node.operation = NT_VARIABLE;
                node.leftNode = indexNode;
                node.variableName = word;

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
                            node.operation = NT_LOCAL_VARIABLE;
                            node.variableName = word;
                        }
                    } else {
                        lastErrorCode = CE_ILLEGAL_VAR_NAME;
                        return null;
                    }
                } else {
                    BaseNode constNode = checkConstants(word);
                    if (constNode != null) {
                        node = constNode;
                    } else {
                        node.operation = NT_VARIABLE;
                        node.variableName = word;
                    }
                }
            } else {
                setPosition(prevPos);
                node = parseFunction();
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

        node.operation = NT_FUNCTION;
        node.variableName = funcName;

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
                } else if (currentChar() != ',')
                    return null;

                forwardPosition();
                skipSpaces();
            }
        } else {
            return null;
        }

        return node;
    }


    /**
     * Returns char at the defined script position. Depends on char.
     *
     * @param position - position in the script.
     */
    char charAtCheckExpSeparator(int position) {
        char retchar = '\0';
        if (position < stopAt) {
            retchar = source.charAt(position);
            if (retchar == ';') retchar = '\0';
            else if (retchar == ',') retchar = '\0';
        } else if (position == stopAt)
            retchar = 32;

        return retchar;
    }


    char currentCharCheckExpSeparator() {
        return charAtCheckExpSeparator(position);
    }

    /**
     * Return char at the current script position, independ on char.
     */
    char currentChar() {
        return charAt(position);
    }

    /**
     * Return char at the defined script position, independ on char.
     */
    char charAt(int position) {
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
    void skipSpaces() {
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
    void forwardPosition() {
        ++position;
    }

    /**
     * Return the word at the current script position. The position is moved to
     * end of returned word.
     */
    String readWord() {
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
    boolean isDigit() {
        if (charAtCheckExpSeparator(position) >= 0x30 && charAtCheckExpSeparator(position) <= 0x39 || charAtCheckExpSeparator(position) == '.')
            return true;
        return false;
    }

    /**
     * Method which determines that character at the current script position is literal.
     */
    boolean isLiteralChar() {
        char c = charAtCheckExpSeparator(position);
        return c >= 0x41 && c <= 0x5a || c >= 0x61 && c <= 0x7a || c == '_';
    }

    /**
     *
     */
    boolean isText() {
        return charAtCheckExpSeparator(position) == '"';
    }

    /**
     *
     */
    boolean isOperatorSymbol() {
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
    String readTextSequence() {
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
                    lastErrorCode = CE_ILLEGAL_SYMBOL;
                    return null;
                } else if (ch == 13 || ch == 10 || ch == 0) {
                    lastErrorCode = CE_MISSING_QUATATION;
                    return null;
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
     * Return last error code.
     */
    char getLastErrorCode() {
        return lastErrorCode;
    }

    /**
     * Return current script position.
     */
    int getPosition() {
        return position;
    }

    /**
     * Sets the position on the script.
     */
    void setPosition(int pos) {
        position = pos;
    }

    /**
     * Return current script line.
     */
    int currentLineNumber() {
        return currentLine;
    }

}