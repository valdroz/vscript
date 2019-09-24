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
 * @author Valerijus Drozdovas
 */
public class Utils implements Constants {

    static String getErrorMsg(char lastErrorCode, int position, int lineNumber) {
        String message = "";

        switch (lastErrorCode) {
            case CE_ILLEGAL_SYMBOL:
                message = "Illegal symbol.";
                break;
            case CE_INCOMPLETE:
                message = "Incompleate statment.";
                break;
            case CE_MISSING_BRACKET:
                message = "Missing bracked ')'.";
                break;
            case CE_MISSING_BRACKET2:
                message = "Missing bracked '}'.";
                break;
            case CE_MISSING_QUOTATION:
                message = "Missing quatation.";
                break;
            case CE_RESERVED_WORD_AS_FUNC:
                message = "Reserved word used as variable name.";
                break;
            case CE_SYNTAX:
                message = "Syntax error.";
                break;
            case CE_TEXT_SYNTAX:
                message = "String definition error.";
                break;
            case CE_UNSUPPORTED_FUNCTION:
                message = "Unsupported function name.";
                break;
            case CE_UNSUPPORTED_STATEMENT:
                message = "Unsupported statement or function name.";
                break;
            case CE_FUNC_DECL_SYNTAX:
                message = "Function declaration syntax error.";
                break;
            case CE_MISSING_COMMA:
                message = "Syntax error, missing comma.";
                break;
            case CE_ILLEGAL_VAR_NAME:
                message = "Syntax error, illegal variable name.";
                break;
            case CE_MISSING_BRACKET3: // ]
                message = "Syntax error, missing ']' bracket.";
                break;
            case CE_CONST_ASSIGNMENT:
                message = "Constants cannot assigned with value.";
                break;
            default:
                return "";
        }

        message += " Error at position (" + position + "), ";
        message += " Line number: " + (lineNumber + 1);

        return message;
    }


}
