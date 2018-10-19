package org.marasm.basicscript.values;

/**
 * A numeric value. Jasic uses doubles internally for all numbers.
 */
public class NumberValue implements Value {
    private final double value;

    public NumberValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return Double.toString(value).replaceAll("\\.0$", "");
    }

    @Override
    public double toNumber() {
        return value;
    }

    @Override
    public Value evaluate() {
        return this;
    }
}
