package org.marasm.basicscript.statements;

import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.expressions.VariableExpression;
import org.marasm.basicscript.expressions.Expression;

/**
 * An assignment statement evaluates an expression and stores the result in
 * a variable.
 */
public class AssignStatement implements Statement {

    private final Expression variable;
    private final Expression value;
    private final Jasic jasic;

    public AssignStatement(String variableName, Expression value, Jasic jasic) {
        this.value = value;
        this.jasic = jasic;
        this.variable = VariableExpression.parseVariableExpression(variableName, jasic);
    }

    public void execute() {
        jasic.getVariables().put(variable.evaluate().toString(), value.evaluate());
    }
}
