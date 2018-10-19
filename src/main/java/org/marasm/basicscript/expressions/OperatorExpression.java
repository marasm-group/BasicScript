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
    protected static Map<String, OperatorExpressionSupplier> operatorExpressions = new HashMap<>();

    static {
        registerOperators();
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

    @Getter
    private final Expression left;
    @Getter
    private final Expression right;
    @Getter
    private Jasic jasic;

    public OperatorExpression(Jasic jasic, Expression left, Expression right) {
        this.jasic = jasic;
        this.left = left;
        this.right = right;
    }

    public static void registerOperator(String operator, OperatorExpressionSupplier supplier) {
        operatorExpressions.put(operator, supplier);
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

    public abstract Value evaluate(Value leftVal, Value rightVal);

    @Override
    public Value evaluate() {
        Value leftVal = left.evaluate();
        Value rightVal = right.evaluate();
        return evaluate(leftVal, rightVal);
    }

    public abstract String getOperator();

    public static interface OperatorExpressionSupplier<T extends OperatorExpression> {
        T get(Jasic jasic, Expression left, Expression right) throws IllegalAccessException, InvocationTargetException, InstantiationException;
    }

    public static Class dummy() {
        return String.class;
    }


}
