package org.marasm.basicscript.statements;

import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.expressions.Expression;

/**
 * An if then statement jumps execution to another place in the program, but
 * only if an expression evaluates to something other than 0.
 */
public class IfThenStatement implements Statement {

    private final Jasic jasic;
    private final Expression condition;
    private final String label;

    public IfThenStatement(Expression condition, String label, Jasic jasic) {
        this.condition = condition;
        this.label = label;
        this.jasic = jasic;
    }

    @Override
    public void execute() {
        if (jasic.getLabels().containsKey(label)) {
            double value = condition.evaluate().toNumber();
            if (value != 0) {
                jasic.setCurrentStatement(jasic.getLabels().get(label));
            }
        }
    }
}
