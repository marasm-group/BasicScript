package org.marasm.basicscript.statements;

import lombok.AllArgsConstructor;
import org.marasm.basicscript.Jasic;

/**
 * A "goto" statement jumps execution to another place in the program.
 */
@AllArgsConstructor
public class GotoStatement implements Statement {

    private final String label;
    private final Jasic jasic;

    @Override
    public void execute() {
        if (jasic.getLabels().containsKey(label)) {
            jasic.setCurrentStatement(jasic.getLabels().get(label));
        }
    }
}
