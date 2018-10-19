package org.marasm.basicscript.expressions.operators;

import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.expressions.Expression;
import org.marasm.basicscript.expressions.OperatorExpression;
import org.marasm.basicscript.values.NumberValue;
import org.marasm.basicscript.values.StringValue;
import org.marasm.basicscript.values.Value;

public class Plus extends OperatorExpression {

    public Plus(Jasic jasic, Expression left, Expression right) {
        super(jasic, left, right);
    }

    @Override
    public Value evaluate(Value leftVal, Value rightVal) {
        // Addition if the any argument is a number, otherwise do
        // string concatenation.
        if (leftVal instanceof NumberValue && rightVal instanceof NumberValue) {
            return new NumberValue(leftVal.toNumber() + rightVal.toNumber());
        }
        return new StringValue(leftVal.toString() + rightVal.toString());
    }

    @Override
    public String getOperator() {
        return "+";
    }
}
