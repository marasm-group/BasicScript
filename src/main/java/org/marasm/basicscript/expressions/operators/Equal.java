package org.marasm.basicscript.expressions.operators;

import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.expressions.Expression;
import org.marasm.basicscript.expressions.OperatorExpression;
import org.marasm.basicscript.values.NumberValue;
import org.marasm.basicscript.values.Value;

public class Equal extends OperatorExpression {

    public Equal(Jasic jasic, Expression left, Expression right) {
        super(jasic, left, right);
    }

    @Override
    public Value evaluate(Value leftVal, Value rightVal) {
        // Coerce to the left argument's type, then compare.
        if (leftVal instanceof NumberValue && rightVal instanceof NumberValue) {
            return new NumberValue((leftVal.toNumber() == rightVal.toNumber()) ? 1 : 0);
        }
        return new NumberValue(leftVal.toString().equals(rightVal.toString()) ? 1 : 0);
    }

    @Override
    public String getOperator() {
        return "=";
    }
}
