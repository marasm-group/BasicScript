package org.marasm.basicscript.tokens;

import lombok.AllArgsConstructor;

/**
 * This is a single meaningful chunk of code. It is created by the tokenizer
 * and consumed by the parser.
 */
@AllArgsConstructor
public class Token {
    public final String text;
    public final TokenType type;

    @Override
    public String toString() {
        return text;
    }
}
