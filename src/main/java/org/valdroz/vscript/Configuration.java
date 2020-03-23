package org.valdroz.vscript;

import java.math.RoundingMode;

/**
 * @author vdrozd720
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
