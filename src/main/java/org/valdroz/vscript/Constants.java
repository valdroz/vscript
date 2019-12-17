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
public interface Constants {
    char CE_SUCCESS = 0;
    char CE_INCOMPLETE = 1;
    char CE_SYNTAX = 2;
    char CE_RESERVED_WORD_AS_FUNC = 3;
    char CE_MISSING_BRACKET = 4; // )
    char CE_TEXT_SYNTAX = 5;
    char CE_UNSUPPORTED_FUNCTION = 6;
    char CE_ILLEGAL_SYMBOL = 7;
    char CE_MISSING_QUOTATION = 8;
    char CE_MISSING_BRACKET2 = 9; // }
    char CE_UNSUPPORTED_STATEMENT = 10;
    char CE_FUNC_DECL_SYNTAX = 11;
    char CE_MISSING_COMMA = 12;
    char CE_ILLEGAL_VAR_NAME = 13;
    char CE_MISSING_BRACKET3 = 14; // ]
    char CE_CONST_ASSIGNMENT = 15;

    char NT_VALUE = 133;
    char NT_VARIABLE = 134;
    char NT_FUNCTION = 135;
    char NT_LOCAL_VARIABLE = 137;

    char NT_MF_SIN = 140;
    char NT_MF_COS = 141;
    char NT_MF_ASIN = 142;
    char NT_MF_ACOS = 143;
    char NT_MF_TAN = 144;
    char NT_MF_ATAN = 145;
    char NT_MF_ABS = 146;
    char NT_MF_NEG = 147;
    char NT_MF_SQRT = 148;
    char NT_MF_LN = 149;
    char NT_MF_LOG = 150;
    char NT_MF_EXP = 151;
    char NT_MF_DEBUG = 152;
    char NT_MF_DAY = 153;
    char NT_MF_MONTH = 154;
    char NT_MF_YEAR = 155;
    char NT_MF_DAY_OF_YEAR = 156;
    char NT_MF_DAYS_IN_MONTH = 157;
    char NT_MF_SIZE = 158;
    char NT_MF_IS_STRING = 159;
    char NT_MF_IS_NUMBER = 160;
    char NT_MF_IS_ARRAY = 161;
    char NT_MF_IS_NULL = 162;
    char NT_MF_NOW = 163;
    char NT_MF_ISO = 164;
    char NT_MF_HOURS_BEFORE_NOW = 165;
    char NT_MF_DAYS_BEFORE_NOW = 166;


    char NT_LOP_AND = 128;
    char NT_LOP_OR = 129;
    char NT_LOP_MORE_EQUALS = 130;
    char NT_LOP_LESS_EQUALS = 131;
    char NT_LOP_EQUALS = 132;
    char NT_LOP_NOT_EQUALS = 136;

    char NT_CONSTANT = 200;

    ConstantNode C_TRUE = new ConstantNode(Variant.fromBoolean(true));
    ConstantNode C_FALSE = new ConstantNode(Variant.fromBoolean(false));
    ConstantNode C_NULL = new ConstantNode(Variant.nullVariant());
    ConstantNode C_PI = new ConstantNode(Variant.fromDouble(Math.PI));
    ConstantNode C_E = new ConstantNode(Variant.fromDouble(Math.E));

}
