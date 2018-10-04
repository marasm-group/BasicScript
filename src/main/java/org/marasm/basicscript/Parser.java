package org.marasm.basicscript;

import org.marasm.basicscript.expressions.Expression;
import org.marasm.basicscript.expressions.OperatorExpression;
import org.marasm.basicscript.expressions.VariableExpression;
import org.marasm.basicscript.statements.*;
import org.marasm.basicscript.tokens.Token;
import org.marasm.basicscript.tokens.TokenType;
import org.marasm.basicscript.tools.Simplifier;
import org.marasm.basicscript.values.NumberValue;
import org.marasm.basicscript.values.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This defines the Jasic parser. The parser takes in a sequence of tokens
 * and generates an abstract syntax tree. This is the nested data structure
 * that represents the series of statements, and the expressions (which can
 * nest arbitrarily deeply) that they evaluate. In technical terms, what we
 * have is a recursive descent parser, the simplest kind to hand-write.
 * <p>
 * As a side-effect, this phase also stores off the line numbers for each
 * label in the program. It's a bit gross, but it works.
 */
public class Parser {
    private final List<Token> tokens;
    private Jasic jasic;
    private int position;

    // The following functions each represent one grammatical part of the
    // language. If this parsed English, these functions would be named like
    // noun() and verb().

    public Parser(Jasic jasic, List<Token> tokens) {
        this.jasic = jasic;
        this.tokens = tokens;
        position = 0;
    }

    /**
     * The top-level function to start parsing. This will keep consuming
     * tokens and routing to the other parse functions for the different
     * grammar syntax until we run out of code to parse.
     *
     * @param labels A map of label names to statement indexes. The
     *               parser will fill this in as it scans the code.
     * @return The list of parsed statements.
     */
    public List<Statement> parse(Map<String, Integer> labels) {
        List<Statement> statements = new ArrayList<>();

        while (true) {
            // Ignore empty lines.
            while (match(TokenType.LINE)) {
            }

            if (match(TokenType.LABEL)) {
                // Mark the index of the statement after the label.
                labels.put(last(1).text, statements.size());
            } else if (match(TokenType.WORD, TokenType.EQUALS)) {
                String name = last(2).text;
                Expression value = expression();
                statements.add(new AssignStatement(name, value, jasic));
            } else if (match("print")) {
                statements.add(new PrintStatement(expression()));
            } else if (match("input")) {
                statements.add(new InputStatement(
                        consume(TokenType.WORD).text, jasic));
            } else if (match("goto")) {
                statements.add(new GotoStatement(
                        consume(TokenType.WORD).text, jasic));
            } else if (match("if")) {
                Expression condition = expression();
                consume("then");
                String label = consume(TokenType.WORD).text;
                statements.add(new IfThenStatement(condition, label, jasic));
            } else {
                break; // Unexpected tokens (likely EOF), so end.
            }
        }

        return statements;
    }

    /**
     * Parses a single expression. Recursive descent parsers start with the
     * lowest-precedent term and moves towards higher precedence. For Jasic,
     * binary operators (+, -, etc.) are the lowest.
     *
     * @return The parsed expression.
     */
    public Expression expression() {
        return operator();
    }

    // The following functions are the core low-level operations that the
    // grammar parser is built in terms of. They match and consume tokens in
    // the tokens stream.

    /**
     * Parses a series of binary operator expressions into a single
     * expression. In Jasic, all operators have the same predecence and
     * associate left-to-right. That means it will interpret:
     * 1 + 2 * 3 - 4 / 5
     * like:
     * ((((1 + 2) * 3) - 4) / 5)
     * <p>
     * It works by building the expression tree one at a time. So, given
     * this code: 1 + 2 * 3, this will:
     * <p>
     * 1. Parse (1) as an atomic expression.
     * 2. See the (+) and start a new operator expression.
     * 3. Parse (2) as an atomic expression.
     * 4. Build a (1 + 2) expression and replace (1) with it.
     * 5. See the (*) and start a new operator expression.
     * 6. Parse (3) as an atomic expression.
     * 7. Build a ((1 + 2) * 3) expression and replace (1 + 2) with it.
     * 8. Return the last expression built.
     *
     * @return The parsed expression.
     */
    private Expression operator() {
        Expression expression = atomic();

        // Keep building operator expressions as long as we have operators.
        while (match(TokenType.OPERATOR) ||
                match(TokenType.EQUALS)) {
            char operator = last(1).text.charAt(0);
            Expression right = atomic();
            expression = new OperatorExpression(jasic, expression, operator, right);
        }

        return Simplifier.simplify(expression);
    }

    /**
     * Parses an "atomic" expression. This is the highest level of
     * precedence and contains single literal tokens like 123 and "foo", as
     * well as parenthesized expressions.
     *
     * @return The parsed expression.
     */
    private Expression atomic() {
        if (match(TokenType.WORD)) {
            // A word is a reference to a variable.
            return new VariableExpression(last(1).text, jasic);
        } else if (match(TokenType.NUMBER)) {
            return new NumberValue(Double.parseDouble(last(1).text));
        } else if (match(TokenType.STRING)) {
            return new StringValue(last(1).text);
        } else if (match(TokenType.LEFT_PAREN)) {
            // The contents of a parenthesized expression can be any
            // expression. This lets us "restart" the precedence cascade
            // so that you can have a lower precedence expression inside
            // the parentheses.
            Expression expression = expression();
            consume(TokenType.RIGHT_PAREN);
            return expression;
        }
        throw new Error("Couldn't parse :(");
    }

    /**
     * Consumes the next two tokens if they are the given type (in order).
     * Consumes no tokens if either check fais.
     *
     * @param type1 Expected type of the next tokens.
     * @param type2 Expected type of the subsequent tokens.
     * @return True if tokens were consumed.
     */
    private boolean match(TokenType type1, TokenType type2) {
        if (get(0).type != type1) {
            return false;
        }
        if (get(1).type != type2) {
            return false;
        }
        position += 2;
        return true;
    }

    /**
     * Consumes the next tokens if it's the given type.
     *
     * @param type Expected type of the next tokens.
     * @return True if the tokens was consumed.
     */
    private boolean match(TokenType type) {
        if (get(0).type != type) {
            return false;
        }
        position++;
        return true;
    }

    /**
     * Consumes the next tokens if it's a word tokens with the given name.
     *
     * @param name Expected name of the next word tokens.
     * @return True if the tokens was consumed.
     */
    private boolean match(String name) {
        if (get(0).type != TokenType.WORD) {
            return false;
        }
        if (!get(0).text.equals(name)) {
            return false;
        }
        position++;
        return true;
    }

    /**
     * Consumes the next tokens if it's the given type. If not, throws an
     * exception. This is for cases where the parser demands a tokens of a
     * certain type in a certain position, for example a matching ) after
     * an opening (.
     *
     * @param type Expected type of the next tokens.
     * @return The consumed tokens.
     */
    private Token consume(TokenType type) {
        if (get(0).type != type) {
            throw new Error("Expected " + type + ".");
        }
        return tokens.get(position++);
    }

    /**
     * Consumes the next tokens if it's a word with the given name. If not,
     * throws an exception.
     *
     * @param name Expected name of the next word tokens.
     * @return The consumed tokens.
     */
    private Token consume(String name) {
        if (!match(name)) {
            throw new Error("Expected " + name + ".");
        }
        return last(1);
    }

    /**
     * Gets a previously consumed tokens, indexing backwards. last(1) will
     * be the tokens just consumed, last(2) the one before that, etc.
     *
     * @param offset How far back in the tokens stream to look.
     * @return The consumed tokens.
     */
    private Token last(int offset) {
        return tokens.get(position - offset);
    }

    /**
     * Gets an unconsumed tokens, indexing forward. get(0) will be the next
     * tokens to be consumed, get(1) the one after that, etc.
     *
     * @param offset How far forward in the tokens stream to look.
     * @return The yet-to-be-consumed tokens.
     */
    private Token get(int offset) {
        if (position + offset >= tokens.size()) {
            return new Token("", TokenType.EOF);
        }
        return tokens.get(position + offset);
    }
}
