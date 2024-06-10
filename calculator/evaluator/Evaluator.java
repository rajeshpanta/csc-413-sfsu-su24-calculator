package calculator.evaluator;

import calculator.operators.*;

import java.util.Stack;
import java.util.StringTokenizer;

public class Evaluator {

    private final Stack<Operand> operandStack;
    private final Stack<Operator> operatorStack;

    public Evaluator() {
        operandStack = new Stack<>();
        operatorStack = new Stack<>();
    }

    public int evaluateExpression(String expression) throws InvalidTokenException {
        StringTokenizer expressionTokenizer = new StringTokenizer(expression, " +/*-^()", true);
        while (expressionTokenizer.hasMoreTokens()) {
            String token = expressionTokenizer.nextToken().trim();
            if (!token.isEmpty()) {
                if (Operand.check(token)) {
                    operandStack.push(new Operand(token));
                } else if (token.equals("(")) {
                    operatorStack.push(null); // Use null as a marker for '(' on the stack
                } else if (token.equals(")")) {
                    while (operatorStack.peek() != null) { // Process until the opening '(' is found
                        processOperator();
                    }
                    operatorStack.pop(); // Remove the '(' from the stack
                } else if (Operator.check(token)) {
                    Operator newOperator = Operator.getOperator(token);
                    while (!operatorStack.isEmpty() && operatorStack.peek() != null &&
                           operatorStack.peek().priority() >= newOperator.priority()) {
                        processOperator();
                    }
                    operatorStack.push(newOperator);
                } else {
                    throw new InvalidTokenException("Invalid token: " + token);
                }
            }
        }

        // At the end of the expression, process all remaining operators in the stack
        while (!operatorStack.isEmpty()) {
            if (operatorStack.peek() == null) {
                throw new InvalidTokenException("Mismatched parentheses detected.");
            }
            processOperator();
        }

        if (!operandStack.isEmpty()) {
            return operandStack.pop().getValue();
        } else {
            throw new InvalidTokenException("No operands found in expression.");
        }
    }

    private void processOperator() throws InvalidTokenException {
        if (operatorStack.isEmpty() || operandStack.size() < 2) {
            throw new InvalidTokenException();
        }
        
        Operator operator = operatorStack.pop();
        Operand secondOperand = operandStack.pop();
        Operand firstOperand = operandStack.pop();
        Operand result = operator.execute(firstOperand, secondOperand);
        operandStack.push(result);
    }

    public static void main(String[] args) {
        try {
            if (args.length == 1) {
                Evaluator evaluator = new Evaluator();
                int result = evaluator.evaluateExpression(args[0]);
                System.out.println("Result: " + result);
            } else {
                System.err.println();
            }
        } catch (InvalidTokenException e) {
            System.err.println(e.getMessage());
        }
    }
}
