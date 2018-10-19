package org.marasm.basicscript.tools;

import org.marasm.basicscript.expressions.Expression;
import org.marasm.basicscript.expressions.OperatorExpression;
import org.marasm.basicscript.values.Value;

public class Simplifier {
    public static Expression simplify(Expression e) {
        try {
            if (e instanceof OperatorExpression) {
                OperatorExpression oe = (OperatorExpression) e;
                Expression l = simplify(oe.getLeft());
                Expression r = simplify(oe.getRight());
                final OperatorExpression res = OperatorExpression.create(oe.getJasic(), l, oe.getOperator(), r);
                if (l instanceof Value && r instanceof Value) {
                    return res.evaluate();
                }
                return res;
            }
        } catch (Throwable ignored) {
        }
        return e;
    }
}
