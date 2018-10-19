package org.marasm.basicscript.expressions.operators;

import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.expressions.Expression;
import org.marasm.basicscript.expressions.OperatorExpression;
import org.marasm.basicscript.values.NumberValue;
import org.marasm.basicscript.values.Value;

public class Star extends OperatorExpression {

    public Star(Jasic jasic, Expression left, Expression right) {
        super(jasic, left, right);
    }

    @Override
    public Value evaluate(Value leftVal, Value rightVal) {
        return new NumberValue(leftVal.toNumber() * rightVal.toNumber());
    }

    @Override
    public String getOperator() {
        return "*";
    }
}
