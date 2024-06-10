package calculator.evaluator;

/**
 * Operand class used to represent an operand
 * in a valid mathematical expression.
 */
public class Operand {
    /**
     * construct operand from string token.
     */
    private int value;

    public Operand(String token) {
        this.value = Integer.parseInt(token);
    }

    /**
     * construct operand from integer
     */
    public Operand(int value) {
        this.value = value;
    }

    /**
     * return value of operand
     */
    public int getValue() {
        return value;
    }

    /**
     * Check to see if given token is a valid
     * operand.
     */
    public static boolean check(String token) {
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
