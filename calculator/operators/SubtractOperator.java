package calculator.operators;

import calculator.evaluator.Operand;

public class SubtractOperator extends Operator {
    @Override
    public int priority() {
        return 1;  // Same priority as addition
    }

    @Override
    public Operand execute(Operand op1, Operand op2) {
        return new Operand(op1.getValue() - op2.getValue());
    }
}
