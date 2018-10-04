package org.marasm.basicscript.tokens;

/**
 * This defines the different kinds of tokens or meaningful chunks of code
 * that the parser knows how to consume. These let us distinguish, for
 * example, between a string "foo" and a variable named "foo".
 * <p>
 * HACK: A typical tokenizer would actually have unique tokens types for
 * each keyword (print, goto, etc.) so that the parser doesn't have to look
 * at the names, but Jasic is a little more crude.
 */
public enum TokenType {
    WORD, NUMBER, STRING, LABEL, LINE,
    EQUALS, OPERATOR, LEFT_PAREN, RIGHT_PAREN, EOF
}
