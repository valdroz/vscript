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

import java.math.RoundingMode;

/**
 * @author Valerijus Drozdovas
 * Created on 3/23/20
 */
public final class Configuration {
    private static int decimalScale = 3;
    private static RoundingMode roundingMode = RoundingMode.HALF_EVEN;
    private static boolean caseSensitive = true;
    private static String emptyEvalExpression = "null";

    private Configuration() {}

    public static int setDecimalScale(int decimalScale) {
        int prev = Configuration.decimalScale;
        Configuration.decimalScale = decimalScale;
        return prev;
    }

    public static RoundingMode setRoundingMode(RoundingMode roundingMode) {
        RoundingMode prev = Configuration.roundingMode;
        Configuration.roundingMode = roundingMode;
        return prev;
    }

    public static boolean setCaseSensitive(boolean caseSensitive) {
        boolean prev = Configuration.caseSensitive;
        Configuration.caseSensitive = caseSensitive;
        return prev;
    }

    public static String setExpressionForEmptyEval(String newEmptyEvalExpression) {
        String prev = emptyEvalExpression;
        if (newEmptyEvalExpression != null && newEmptyEvalExpression.trim().length() > 0) {
            emptyEvalExpression = newEmptyEvalExpression;
        }
        return prev;
    }

    public static int getDecimalScale() {
        return decimalScale;
    }

    public static RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public static boolean isCaseSensitive() {
        return caseSensitive;
    }

    public static String getExpressionForEmptyEval() {
        return emptyEvalExpression;
    }
}
