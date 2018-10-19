package org.marasm.basicscript.values;

import org.apache.commons.text.StringEscapeUtils;

/**
 * A string value.
 */
public class StringValue implements Value {
    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public double toNumber() {
        return Double.parseDouble(value);
    }

    @Override
    public Value evaluate() {
        return this;
    }

    @Override
    public String decodedString() {
        return "\"" + StringEscapeUtils.escapeJava(toString()) + "\"";

    }
}
