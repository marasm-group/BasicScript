package org.marasm.basicscript.statements;

import lombok.AllArgsConstructor;
import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.values.NumberValue;
import org.marasm.basicscript.values.StringValue;

import java.io.IOException;

/**
 * An "input" statement reads input from the user and stores it in a
 * variable.
 */
@AllArgsConstructor
public class InputStatement implements Statement {

    private final String name;
    private final Jasic jasic;

    public void execute() {
        try {
            String input = jasic.getLineIn().readLine();

            // Store it as a number if possible, otherwise use a string.
            try {
                double value = Double.parseDouble(input);
                jasic.getVariables().put(name, new NumberValue(value));
            } catch (NumberFormatException e) {
                jasic.getVariables().put(name, new StringValue(input));
            }
        } catch (IOException e1) {
            // HACK: Just ignore the problem.
        }
    }
}
