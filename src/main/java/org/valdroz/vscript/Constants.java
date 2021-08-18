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
    int CE_INCOMPLETE = 1;
    int CE_SYNTAX = 2;
    int CE_RESERVED_WORD_AS_FUNC = 3;
    int CE_MISSING_BRACKET = 4; // )
    int CE_TEXT_SYNTAX = 5;
    int CE_UNSUPPORTED_FUNCTION = 6;
    int CE_ILLEGAL_SYMBOL = 7;
    int CE_MISSING_QUOTATION = 8;
    int CE_MISSING_BRACKET2 = 9; // }
    int CE_UNSUPPORTED_STATEMENT = 10;
    int CE_FUNC_DECL_SYNTAX = 11;
    int CE_MISSING_COMMA = 12;
    int CE_ILLEGAL_VAR_NAME = 13;
    int CE_MISSING_BRACKET3 = 14; // ]
    int CE_CONST_ASSIGNMENT = 15;

    int NT_VARIABLE = 134;
    int NT_FUNCTION = 135;
    int NT_LOCAL_VARIABLE = 137;

    int NT_MF_SIN = 140;
    int NT_MF_COS = 141;
    int NT_MF_ASIN = 142;
    int NT_MF_ACOS = 143;
    int NT_MF_TAN = 144;
    int NT_MF_ATAN = 145;
    int NT_MF_ABS = 146;
    int NT_MF_NEG = 147;
    int NT_MF_SQRT = 148;
    int NT_MF_LN = 149;
    int NT_MF_LOG = 150;
    int NT_MF_EXP = 151;
    int NT_MF_DAY = 153;
    int NT_MF_MONTH = 154;
    int NT_MF_YEAR = 155;
    int NT_MF_DAY_OF_YEAR = 156;
    int NT_MF_DAYS_IN_MONTH = 157;
    int NT_MF_SIZE = 158;
    int NT_MF_IS_STRING = 159;
    int NT_MF_IS_NUMBER = 160;
    int NT_MF_IS_ARRAY = 161;
    int NT_MF_IS_NULL = 162;
    int NT_MF_NOW = 163;
    int NT_MF_ISO = 164;
    int NT_MF_HOURS_BEFORE_NOW = 165;
    int NT_MF_DAYS_BEFORE_NOW = 166;
    int NT_MF_TO_ARRAY = 167;
    int NT_MF_MINUTES_BEFORE_NOW = 168;
    int NT_MF_POWER = 169;
    int NT_MF_IF = 170;


    int NT_LOP_AND = 128;
    int NT_LOP_OR = 129;
    int NT_LOP_MORE_EQUALS = 130;
    int NT_LOP_LESS_EQUALS = 131;
    int NT_LOP_EQUALS = 132;
    int NT_LOP_NOT_EQUALS = 136;

    ConstantNode C_TRUE = new ConstantNode("true", Variant.fromBoolean(true));
    ConstantNode C_FALSE = new ConstantNode("false", Variant.fromBoolean(false));
    ConstantNode C_NULL = new ConstantNode("null", Variant.nullVariant());
    ConstantNode C_PI = new ConstantNode("PI", Variant.fromDouble(Math.PI));
    ConstantNode C_E = new ConstantNode("E", Variant.fromDouble(Math.E));

}
