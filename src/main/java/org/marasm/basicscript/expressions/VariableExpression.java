package org.marasm.basicscript.expressions;

import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.Parser;
import org.marasm.basicscript.Tokenizer;
import org.marasm.basicscript.tools.Simplifier;
import org.marasm.basicscript.values.NumberValue;
import org.marasm.basicscript.values.StringValue;
import org.marasm.basicscript.values.Value;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A variable expression evaluates to the current value stored in that
 * variable.
 */
public class VariableExpression implements Expression {

    private final Expression variable;
    private final Jasic jasic;

    public VariableExpression(String variable, Jasic jasic) {
        this.jasic = jasic;
        this.variable = parseVariableExpression(variable, jasic);
    }

    public static Expression parseVariableExpression(String variable, Jasic jasic) {
        if (!variable.contains("[")) {
            return new StringValue(variable);
        }
        List<String> strings = new LinkedList<>(Arrays.asList(variable.split("\\[")));
        strings.remove(0);
        List<Expression> collect = strings.stream()
                .map(s -> s.split("]")[0])
                .map(s -> new Parser(jasic, Tokenizer.tokenize(s + "\n")).expression())
                .map(Simplifier::simplify)
                .collect(Collectors.toList());
        return new StringValue(collect.stream()
                .map(Expression::evaluate)
                .map(Value::toString)
                .map(s -> "[" + s + "]")
                .collect(Collectors.joining()));
    }

    @Override
    public Value evaluate() {
        String name = variable.evaluate().toString();
        if (jasic.getVariables().containsKey(name)) {
            return jasic.getVariables().get(name);
        }
        return new NumberValue(0);
    }

    @Override
    public String decodedString() {
        return variable.toString();
    }


}
