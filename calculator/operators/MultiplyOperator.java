package calculator.operators;

import calculator.evaluator.Operand;

public class MultiplyOperator extends Operator {
    @Override
    public int priority() {
        return 2; // The typical priority for multiplication in standard operator precedence
    }

    @Override
    public Operand execute(Operand operandOne, Operand operandTwo) {
        int result = operandOne.getValue() * operandTwo.getValue();
        return new Operand(result);
    }
}
