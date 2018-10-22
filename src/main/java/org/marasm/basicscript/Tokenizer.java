package org.marasm.basicscript;

import org.marasm.basicscript.tokens.Token;
import org.marasm.basicscript.tokens.TokenType;
import org.marasm.basicscript.tokens.TokenizeState;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    /**
     * This function takes a script as a string of characters and chunks it into
     * a sequence of tokens. Each tokens is a meaningful unit of program, like a
     * variable name, a number, a string, or an operator.
     */
    public static List<Token> tokenize(String source) {
        List<Token> tokens = new ArrayList<>();

        String token = "";
        TokenizeState state = TokenizeState.DEFAULT;

        // Many tokens are a single character, like operators and ().
        String operatorsTokens = "=+-*/%<>";
        String charTokens = "\n" + operatorsTokens + "()";
        TokenType[] tokenTypes = {TokenType.LINE, TokenType.EQUALS,
                TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
                TokenType.OPERATOR, TokenType.OPERATOR, TokenType.OPERATOR,
                TokenType.OPERATOR, TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN
        };

        // Scan through the code one character at a time, building up the list
        // of tokens.
        for (int i = 0; i < source.length(); i++) {
            char prevC = 0;
            if (i != 0) {
                prevC = source.charAt(i - 1);
            }
            char c = source.charAt(i);
            char nextC = 0;
            if (i < source.length() - 1) {
                nextC = source.charAt(i + 1);
            }
            switch (state) {
                case DEFAULT:
                    if (charTokens.indexOf(c) != -1) {
                        if (operatorsTokens.contains(Character.toString(c)) && operatorsTokens.contains(Character.toString(prevC))) {
                            int index = tokens.size() - 1;
                            Token prevToken = tokens.get(index);
                            tokens.remove(index);
                            tokens.add(new Token(Character.toString(prevC) + Character.toString(c), prevToken.type));
                        } else {
                            tokens.add(new Token(Character.toString(c), tokenTypes[charTokens.indexOf(c)]));
                        }
                    } else if (Character.isLetter(c)) {
                        token += c;
                        state = TokenizeState.WORD;
                    } else if (Character.isDigit(c)) {
                        token += c;
                        state = TokenizeState.NUMBER;
                    } else if (c == '"') {
                        state = TokenizeState.STRING;
                    } else if (c == ';') {
                        state = TokenizeState.COMMENT;
                    }
                    break;

                case WORD:
                    if (isWordAcceptable(c, prevC, nextC, token)) {
                        token += c;
                    } else if (c == ':') {
                        tokens.add(new Token(token, TokenType.LABEL));
                        token = "";
                        state = TokenizeState.DEFAULT;
                    } else {
                        tokens.add(new Token(token, TokenType.WORD));
                        token = "";
                        state = TokenizeState.DEFAULT;
                        i--; // Reprocess this character in the default state.
                    }
                    break;

                case NUMBER:
                    // HACK: Negative numbers aren't supported.
                    // To get a negative number, just do 0 - <your number>.
                    if (Character.isDigit(c) || c == '.') {
                        token += c;
                    } else {
                        tokens.add(new Token(token, TokenType.NUMBER));
                        token = "";
                        state = TokenizeState.DEFAULT;
                        i--; // Reprocess this character in the default state.
                    }
                    break;

                case STRING:
                    if (c == '"' && prevC != '\\') {
                        tokens.add(new Token(token, TokenType.STRING));
                        token = "";
                        state = TokenizeState.DEFAULT;
                    } else {
                        if (prevC == '\\') {
                            token = token.substring(0, token.length() - 1);
                        }
                        switch (c) {
                            case 'n':
                                token += '\n';
                                break;
                            default:
                                token += c;
                        }
                    }
                    break;

                case COMMENT:
                    if (c == '\n') {
                        state = TokenizeState.DEFAULT;
                    }
                    break;
            }
        }

        // HACK: Silently ignore any in-progress tokens when we run out of
        // characters. This means that, for example, if a script has a string
        // that's missing the closing ", it will just ditch it.
        return tokens;
    }

    private static boolean isWordAcceptable(char c, char prevC, char nextC, String word) {
        long count = 0;
        if (c == ']' || c == '"') {
            long count1 = word.chars().filter(ch -> ch == '[').count();
            long count2 = word.chars().filter(ch -> ch == ']').count();
            count = count1 - count2;
        }
        if (count != 0) {
            return true;
        }
        return Character.isLetterOrDigit(c) || c == '_' || c == '.' || c == '[';
    }
}
