package org.marasm.basicscript;

import lombok.Getter;
import lombok.Setter;
import org.marasm.basicscript.statements.Statement;
import org.marasm.basicscript.tokens.Token;
import org.marasm.basicscript.values.Value;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This defines a single class that contains an entire interpreter for a
 * language very similar to the original BASIC. Everything is here (albeit in
 * very simplified form): tokenizing, parsing, and interpretation. The file is
 * organized in phases, with each appearing roughly in the order that they
 * occur when a program is run. You should be able to read this top-down to walk
 * through the entire process of loading and running a program.
 * <p>
 * Jasic language syntax
 * ---------------------
 * <p>
 * Comments start with ' and proceed to the end of the line:
 * <p>
 * print "hi there" ' this is a comment
 * <p>
 * Numbers and strings are supported. Strings should be in "double quotes", and
 * only positive integers can be parsed (though numbers are double internally).
 * <p>
 * Variables are identified by name which must start with a letter and can
 * contain letters or numbers. Case is significant for names and keywords.
 * <p>
 * Each statement is on its own line. Optionally, a line may have a label before
 * the statement. A label is a name that ends with a colon:
 * <p>
 * foo:
 * <p>
 * <p>
 * The following statements are supported:
 *
 * <name> = <expression>
 * Evaluates the expression and assigns the result to the given named
 * variable. All variables are globally scoped.
 * <p>
 * pi = (314159 / 10000)
 * <p>
 * print <expression>
 * Evaluates the expression and prints the result.
 * <p>
 * print "hello, " + "world"
 * <p>
 * input <name>
 * Reads in a line of input from the user and stores it in the variable with
 * the given name.
 * <p>
 * input guess
 * <p>
 * goto <label>
 * Jumps to the statement after the label with the given name.
 * <p>
 * goto loop
 * <p>
 * if <expression> then <label>
 * Evaluates the expression. If it evaluates to a non-zero number, then
 * jumps to the statement after the given label.
 * <p>
 * if a < b then dosomething
 * <p>
 * <p>
 * The following expressions are supported:
 *
 * <expression> = <expression>
 * Evaluates to 1 if the two expressions are equal, 0 otherwise.
 *
 * <expression> + <expression>
 * If the left-hand expression is a number, then adds the two expressions,
 * otherwise concatenates the two strings.
 *
 * <expression> - <expression>
 * <expression> * <expression>
 * <expression> / <expression>
 * <expression> < <expression>
 * <expression> > <expression>
 * You can figure it out.
 *
 * <name>
 * A name in an expression simply returns the value of the variable with
 * that name. If the variable was never set, it defaults to 0.
 * <p>
 * All binary operators have the same precedence. Sorry, I had to cut corners
 * somewhere.
 * <p>
 * To keep things simple, I've omitted some stuff or hacked things a bit. When
 * possible, I'll leave a "HACK" note there explaining what and why. If you
 * make your own interpreter, you'll want to address those.
 *
 * @author Bob Nystrom
 */
public class Jasic {

    @Getter
    private final Map<String, Value> variables;

    // Tokenizing (lexing) -----------------------------------------------------
    @Getter
    private final Map<String, Integer> labels;

    // Interpreter -------------------------------------------------------------
    @Getter
    private final BufferedReader lineIn;
    @Getter
    @Setter
    private int currentStatement;

    /**
     * Constructs a new Jasic instance. The instance stores the global state of
     * the interpreter such as the values of all of the variables and the
     * current statement.
     */
    public Jasic() {
        variables = new HashMap<>();
        labels = new HashMap<>();

        InputStreamReader converter = new InputStreamReader(System.in);
        lineIn = new BufferedReader(converter);
    }

    /**
     * Runs the interpreter as a command-line app. Takes one argument: a path
     * to a script file to load and run. The script should contain one
     * statement per line.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        // Just show the usage and quit if a script wasn't provided.
        if (args.length != 1) {
            System.out.println("Usage: jasic <script>");
            System.out.println("Where <script> is a relative path to a .jas script to run.");
            return;
        }

        // Read the file.
        String contents = readFile(args[0]);

        // Run it.
        Jasic jasic = new Jasic();
        jasic.interpret(contents);
    }

    /**
     * Reads the file from the given path and returns its contents as a single
     * string.
     *
     * @param path Path to the text file to read.
     * @return The contents of the file or null if the load failed.
     * @throws IOException
     */
    private static String readFile(String path) {
        try {

            try (FileInputStream stream = new FileInputStream(path)) {
                InputStreamReader input = new InputStreamReader(stream,
                        Charset.defaultCharset());
                Reader reader = new BufferedReader(input);

                StringBuilder builder = new StringBuilder();
                int read;

                while ((read = reader.read()) > 0) {
                    builder.append((char) read);
                }

                // HACK: The parser expects every statement to end in a newline,
                // even the very last one, so we'll just tack one on here in
                // case the file doesn't have one.
                builder.append("\n");

                return builder.toString();
            }
        } catch (IOException ex) {
            return null;
        }
    }

    // Utility stuff -----------------------------------------------------------

    /**
     * This is where the magic happens. This runs the code through the parsing
     * pipeline to generate the AST. Then it executes each statement. It keeps
     * track of the current line in a member variable that the statement objects
     * have access to. This lets "goto" and "if then" do flow control by simply
     * setting the index of the current statement.
     * <p>
     * In an interpreter that didn't mix the interpretation logic in with the
     * AST node classes, this would be doing a lot more work.
     *
     * @param source A string containing the source code of a .jas script to
     *               interpret.
     */
    public void interpret(String source) {
        // Tokenize.
        List<Token> tokens = Tokenizer.tokenize(source);

        // Parse.
        Parser parser = new Parser(this, tokens);
        List<Statement> statements = parser.parse(labels);

        // Interpret until we're done.
        currentStatement = 0;
        while (currentStatement < statements.size()) {
            int thisStatement = currentStatement;
            currentStatement++;
            statements.get(thisStatement).execute();
        }
    }
}