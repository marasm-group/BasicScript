package org.marasm.basicscript.expressions;

import lombok.Getter;
import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.values.NumberValue;
import org.marasm.basicscript.values.StringValue;
import org.marasm.basicscript.values.Value;

/**
 * An operator expression evaluates two expressions and then performs some
 * arithmetic operation on the results.
 */
public class OperatorExpression implements Expression {
    @Getter
    private final Expression left;
    @Getter
    private final char operator;
    @Getter
    private final Expression right;
    @Getter
    private Jasic jasic;

    public OperatorExpression(Jasic jasic, Expression left, char operator,
                              Expression right) {
        this.jasic = jasic;
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Value evaluate() {
        Value leftVal = left.evaluate();
        Value rightVal = right.evaluate();

        switch (operator) {
            case '=':
                // Coerce to the left argument's type, then compare.
                if (leftVal instanceof NumberValue) {
                    return new NumberValue((leftVal.toNumber() ==
                            rightVal.toNumber()) ? 1 : 0);
                } else {
                    return new NumberValue(leftVal.toString().equals(
                            rightVal.toString()) ? 1 : 0);
                }
            case '+':
                // Addition if the left argument is a number, otherwise do
                // string concatenation.
                if (leftVal instanceof NumberValue) {
                    return new NumberValue(leftVal.toNumber() +
                            rightVal.toNumber());
                } else {
                    return new StringValue(leftVal.toString() +
                            rightVal.toString());
                }
            case '-':
                return new NumberValue(leftVal.toNumber() -
                        rightVal.toNumber());
            case '*':
                return new NumberValue(leftVal.toNumber() *
                        rightVal.toNumber());
            case '/':
                return new NumberValue(leftVal.toNumber() /
                        rightVal.toNumber());
            case '<':
                // Coerce to the left argument's type, then compare.
                if (leftVal instanceof NumberValue) {
                    return new NumberValue((leftVal.toNumber() <
                            rightVal.toNumber()) ? 1 : 0);
                } else {
                    return new NumberValue((leftVal.toString().compareTo(
                            rightVal.toString()) < 0) ? 1 : 0);
                }
            case '>':
                // Coerce to the left argument's type, then compare.
                if (leftVal instanceof NumberValue) {
                    return new NumberValue((leftVal.toNumber() >
                            rightVal.toNumber()) ? 1 : 0);
                } else {
                    return new NumberValue((leftVal.toString().compareTo(
                            rightVal.toString()) > 0) ? 1 : 0);
                }
        }
        throw new Error("Unknown operator.");
    }
}
