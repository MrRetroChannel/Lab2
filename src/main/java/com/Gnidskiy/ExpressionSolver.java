package com.Gnidskiy;
import java.util.HashMap;

public class ExpressionSolver {
    private final HashMap<String, Double> _variables;

    private static final HashMap<String, FunctionType> functions = new HashMap<String, FunctionType>() {{
        put("sin" , FunctionType.SIN);
        put("cos" , FunctionType.COS);
        put("sqrt", FunctionType.SQRT);
        put("log" , FunctionType.LOG);
        put("abs" , FunctionType.ABS);
    }};

    private final char[] _expression;

    private int curIter = 0;

    /**
     * Getting a current char
     * @return Current char in curIter position
     */
    private char curChar() {
        return _expression[curIter]; 
    }

    /**
     * Checking if iterator is in array bounds
     * @return true if curIter is inBounds and false otherwise
     */
    private boolean inBounds() {
        return curIter < _expression.length;
    }

    /**
     * Checks if char is some kind of space
     * @param c checked char
     * @return Result of check 
     */
    private static boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\0';
    }

    /**
     * Checks if char is a letter
     * @param c checked char
     * @return Result of check
     */ 
    private static boolean isLetter(char c) {
        return c >= 'a' && c <= 'z'; 
    }

    /**
     * Checks if char is a number
     * @param c checked char
     * @return Result of check
     */
    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9' || c == '.';
    }

    /**
     * Gets next character in row and checks if provided char matches with current one
     * @param c checked char
     * @return Result of comparison between c and current char
     */
    private boolean getNextToken(char c) {
        if (!inBounds())
            return false;

        while (isSpace(curChar()))
            ++curIter;

        if (curChar() == c) {
            ++curIter;
            return true;
        }

        return false;
    }

    /**
     * Parses current number, called only if current char is a number
     * @return Parsed number
     */
    private double parseNumber() {
        StringBuilder numBuilder = new StringBuilder();

        for (; inBounds() && isNumber(curChar()); ++curIter)
            numBuilder.append(curChar());

        try {
            return Double.parseDouble(numBuilder.toString());
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Wrong number format at " + (curIter - numBuilder.length()));
        }
    }
  
    /**
     * Parses current function or a variable, called only if current char is a letter
     * @return Parsed symbol
     */
    private String parseSymbol() {
        StringBuilder symbolBuilder = new StringBuilder();

        for (; inBounds() && isLetter(curChar()); ++curIter)
            symbolBuilder.append(_expression[curIter]);

        return symbolBuilder.toString();
    }

    /**
     * Solves expression with lowest priority (addition, substraction)
     * @return Result of expression
     */
    private double solveExpression() {
        double result = solveTerm();

        while (inBounds()) {
            if (getNextToken('+'))
                result += solveTerm();
            else
            if (getNextToken('-'))
                result -= solveTerm();
            else
                return result;
        }

        return result;
    }

    /**
     * Solves expression with medium priority (multiplication, division)
     * @return Result of term
    */
    private double solveTerm() {
        double result = solveFactor();

        while (inBounds()) {
            if (getNextToken('*'))
                result *= solveFactor();
            else
            if (getNextToken('/'))
                result /= solveFactor();
            else
                return result;
        }

        return result;
    }

    /**
     * Gets result of first priority expression (brackets) or an unary number
     * @return Result of expression
     */
    private double solveFactor() {
        if (getNextToken('+'))
            return +solveFactor();
        else 
        if (getNextToken('-'))
            return -solveFactor();

        double result = 0.0;
        
        if (getNextToken('(')) {
            result = solveExpression();

            if (!getNextToken(')')) {
                throw new RuntimeException("Expected ), instead found " + curChar());
            }
        }
        else if (isNumber(curChar())) {
            result += parseNumber();
        }
        else if (isLetter(curChar())) {
            String arg = parseSymbol();
            
            FunctionType type = functions.get(arg);
            if (type != null)
                return applyFunction(type, solveExpression());
            else
            if (_variables != null) {
                Double checkVariable = _variables.get(arg);
                if (checkVariable != null)
                    result = checkVariable;
            }
            else
            throw new RuntimeException("There is no function or variable with name " + arg);
        }

        if (getNextToken('^')) {
            result = Math.pow(result, solveFactor());
        }

        return result;
    }

    /**
     * Applise parsed expression to parsed function
     * @param functionType type of function from static functions HashMap
     * @param argument argument of provided function
     * @return
     */
    private double applyFunction(FunctionType functionType, double argument) {
        switch (functionType) {
            case SIN:
                return Math.sin(argument);
            case COS:
                return Math.cos(argument);
            case SQRT:
                return Math.sqrt(argument);
            case LOG:
                return Math.log(argument);
            case ABS:
                return Math.abs(argument);
            default:
                throw new IllegalArgumentException("Unknown function type: " + functionType);
        }
    }

    public ExpressionSolver(String expression) {
        _variables = null;
        _expression = expression.toCharArray();
    }

    public ExpressionSolver(String expression, HashMap<String, Double> variables) {
        _variables = variables;
        _expression = expression.toCharArray();
    }

    /**
     * Takes an expression string and solves it with all mathematical rules
     * @return Result of a mathematical expression
     */
    public double solve() {
        double result = solveExpression();

        if (inBounds())
            throw new RuntimeException("Unexpected end of expression at " + curIter);

        return result;
    }
}
