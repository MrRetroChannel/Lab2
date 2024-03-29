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

    private char curChar() {
        return _expression[curIter]; 
    }

    private boolean inBounds() {
        return curIter < _expression.length;
    }

    private static boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\0';
    }

    private static boolean isLetter(char c) {
        return c >= 'a' && c <= 'z'; 
    }

    private static boolean isNumber(char c) {
        return c >= '0' && c <= '9' || c == '.';
    }

    private boolean getNextToken(char c) {
        while (isSpace(curChar()))
            ++curIter;

        if (curChar() == c) {
            ++curIter;
            return true;
        }

        return false;
    }

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
  
    private String parseSymbol() {
        StringBuilder symbolBuilder = new StringBuilder();

        for (; inBounds() && isLetter(curChar()); ++curIter)
            symbolBuilder.append(_expression[curIter]);

        return symbolBuilder.toString();
    }

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

            if (_variables != null) {
                Double checkVariable = _variables.get(arg);
                if (checkVariable != null)
                    return checkVariable;
            }

            throw new RuntimeException("There is no function or variable with name " + arg);
        }
        
        return result;
    }

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

    public double solve() {
        double result = solveExpression();

        if (inBounds())
            throw new RuntimeException("Unexprected end of expression at " + curIter);

        return result;
    }
}
