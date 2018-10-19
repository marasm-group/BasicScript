package org.marasm.basicscript.statements;

import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.expressions.Expression;
import org.marasm.basicscript.expressions.VariableExpression;
import org.marasm.basicscript.values.NumberValue;
import org.marasm.basicscript.values.StringValue;

import java.io.IOException;

/**
 * An "input" statement reads input from the user and stores it in a
 * variable.
 */
public class InputStatement implements Statement {

    private final Expression variable;
    private final Jasic jasic;

    public InputStatement(String name, Jasic jasic) {
        this.variable = VariableExpression.parseVariableExpression(name, jasic);
        this.jasic = jasic;
    }

    @Override
    public void execute() {
        try {
            String input = jasic.getLineIn().readLine();

            // Store it as a number if possible, otherwise use a string.
            try {
                double value = Double.parseDouble(input);
                jasic.getVariables().put(variable.evaluate().toString(), new NumberValue(value));
            } catch (NumberFormatException e) {
                jasic.getVariables().put(variable.evaluate().toString(), new StringValue(input));
            }
        } catch (IOException e1) {
            // HACK: Just ignore the problem.
        }
    }
}
