package org.marasm.basicscript.statements;

import org.marasm.basicscript.expressions.Expression;

/**
 * A "print" statement evaluates an expression, converts the result to a
 * string, and displays it to the user.
 */
public class PrintStatement implements Statement {
    private final Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    public void execute() {
        System.out.println(expression.evaluate().toString());
    }
}
