package org.marasm.basicscript.expressions;

import lombok.Getter;
import org.marasm.basicscript.Jasic;
import org.marasm.basicscript.values.Value;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An operator expression evaluates two expressions and then performs some
 * arithmetic operation on the results.
 */
public abstract class OperatorExpression implements Expression {
    private static final Map<String, OperatorExpressionSupplier> operatorExpressions = new HashMap<>();

    static {
        registerOperators();
    }

    @Getter
    private final Expression left;
    @Getter
    private final Expression right;
    @Getter
    private Jasic jasic;

    protected OperatorExpression(Jasic jasic, Expression left, Expression right) {
        this.jasic = jasic;
        this.left = left;
        this.right = right;
    }

    private static void registerOperators() {
        Reflections reflections = new Reflections("");
        Set<Class<? extends OperatorExpression>> operatorClasses = reflections.getSubTypesOf(OperatorExpression.class);
        operatorClasses.forEach(aClass -> {
            try {
                Constructor<? extends OperatorExpression> constructor = aClass.getConstructor(Jasic.class, Expression.class, Expression.class);
                constructor.setAccessible(true);
                OperatorExpression operatorExpression = constructor.newInstance(null, null, null);
                operatorExpressions.put(operatorExpression.getOperator(), constructor::newInstance);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public static OperatorExpression create(Jasic jasic, Expression left, String operator, Expression right) {
        OperatorExpression operatorExpression = null;
        try {
            operatorExpression = operatorExpressions.get(operator).get(jasic, left, right);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        if (operatorExpression == null) {
            throw new Error("Unknown operator '" + operator + "'");
        }
        return operatorExpression;
    }

    protected abstract Value evaluate(Value leftVal, Value rightVal);

    @Override
    public Value evaluate() {
        Value leftVal = left.evaluate();
        Value rightVal = right.evaluate();
        return evaluate(leftVal, rightVal);
    }

    public abstract String getOperator();

    @Override
    public String decodedString() {
        return "(" + left.decodedString() + " " + getOperator() + " " + right.decodedString() + ")";
    }

    public interface OperatorExpressionSupplier<T extends OperatorExpression> {
        T get(Jasic jasic, Expression left, Expression right) throws IllegalAccessException, InvocationTargetException, InstantiationException;
    }

}
