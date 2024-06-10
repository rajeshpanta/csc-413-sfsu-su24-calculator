package calculator.operators;

import calculator.evaluator.Operand;

public class DivideOperator extends Operator {
    @Override
    public int priority() {
        return 2;  // Same priority as multiplication
    }

    @Override
    public Operand execute(Operand op1, Operand op2) {
        if (op2.getValue() == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
        return new Operand(op1.getValue() / op2.getValue());
    }
}
