/*
 * Copyright 2019 Valerijus Drozdovas
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

import static org.valdroz.vscript.Constants.*;

/**
 * @author Valerijus Drozdovas
 * Created on 12/16/19
 */
public class EvaluationException extends RuntimeException {

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(int code, int lineNumber, int pos) {
        super(String.format("Error (%d): %s; Line: %d, Pos: %d",
                code,
                errorCodeToMessage(code),
                lineNumber,
                pos));
    }

    public EvaluationException(String message, int lineNumber, int pos) {
        super(String.format("Error: %s; Line %d, Pos %d", message, lineNumber, pos));
    }

    private static String errorCodeToMessage(int errorCode) {
        switch (errorCode) {
            case CE_ILLEGAL_SYMBOL:
                return "Illegal symbol";
            case CE_INCOMPLETE:
                return "Incomplete statement";
            case CE_MISSING_BRACKET:
                return  "Missing ')' bracket";
            case CE_MISSING_BRACKET2:
                return  "Missing '}' bracket";
            case CE_MISSING_BRACKET3: // ]
                return "Missing ']' bracket";
            case CE_MISSING_QUOTATION:
                return  "Missing quotation";
            case CE_RESERVED_WORD_AS_FUNC:
                return "Reserved keyword used";
            case CE_SYNTAX:
                return "Syntax error";
            case CE_TEXT_SYNTAX:
                return  "String definition error";
            case CE_UNSUPPORTED_FUNCTION:
                return  "Unsupported function name";
            case CE_UNSUPPORTED_STATEMENT:
                return  "Unsupported statement or function name";
            case CE_FUNC_DECL_SYNTAX:
                return "Function declaration syntax error";
            case CE_MISSING_COMMA:
                return "Missing comma";
            case CE_ILLEGAL_VAR_NAME:
                return "Illegal variable name";
            case CE_CONST_ASSIGNMENT:
                return  "Constant assigned is not allowed";
        }
        return "Unknown error";
    }

}
