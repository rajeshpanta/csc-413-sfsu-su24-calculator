package calculator.operators;

import calculator.evaluator.Operand;

public class AddOperator extends Operator {
    @Override
    public int priority() {
        return 1;  // Define the correct priority if needed
    }

    @Override
    public Operand execute(Operand operandOne, Operand operandTwo) {
        // Implement the addition logic
        return new Operand(operandOne.getValue() + operandTwo.getValue());
    }
}
