

# Grading Report for P1



## Student Name: 

Rajesh.Panta


## Commit Count: 

3



## Git Diff Since Base Commit: 


<details>
    <summary>Git Diff</summary>

~~~bash
diff --git a/README.md b/README.md
index 19422b5..8d98188 100644
--- a/README.md
+++ b/README.md
@@ -2,11 +2,11 @@
 
 ## Student Information
 
-### Student Name  : Name here
+### Student Name  : Rajesh Panta
 
-### Student ID    : ID here
+### Student ID    : 920636337
 
-### Student Email : Email here
+### Student Email : rpanta@sfsu.edu
 
 ## Requirements
 
@@ -95,4 +95,4 @@ java -jar ./lib/junit-platform-console-standalone-1.9.3.jar -cp target --scan-cl
 - Push work to repository
     ```
     git push origin main
-    ```
\ No newline at end of file
+    ```
diff --git a/calculator/evaluator/Evaluator.java b/calculator/evaluator/Evaluator.java
index 3ce44bc..3052916 100644
--- a/calculator/evaluator/Evaluator.java
+++ b/calculator/evaluator/Evaluator.java
@@ -7,68 +7,79 @@ import java.util.StringTokenizer;
 
 public class Evaluator {
 
-  private final Stack<Operand> operandStack;
-  private final Stack<Operator> operatorStack;
+    private final Stack<Operand> operandStack;
+    private final Stack<Operator> operatorStack;
 
+    public Evaluator() {
+        operandStack = new Stack<>();
+        operatorStack = new Stack<>();
+    }
 
-  public Evaluator() {
-    operandStack = new Stack<>();
-    operatorStack = new Stack<>();
-  }
-
-  public int evaluateExpression(String expression ) throws InvalidTokenException {
-    String expressionToken;
-    StringTokenizer expressionTokenizer;
-    String delimiters = " +/*-^";
+    public int evaluateExpression(String expression) throws InvalidTokenException {
+        StringTokenizer expressionTokenizer = new StringTokenizer(expression, " +/*-^()", true);
+        while (expressionTokenizer.hasMoreTokens()) {
+            String token = expressionTokenizer.nextToken().trim();
+            if (!token.isEmpty()) {
+                if (Operand.check(token)) {
+                    operandStack.push(new Operand(token));
+                } else if (token.equals("(")) {
+                    operatorStack.push(null); // Use null as a marker for '(' on the stack
+                } else if (token.equals(")")) {
+                    while (operatorStack.peek() != null) { // Process until the opening '(' is found
+                        processOperator();
+                    }
+                    operatorStack.pop(); // Remove the '(' from the stack
+                } else if (Operator.check(token)) {
+                    Operator newOperator = Operator.getOperator(token);
+                    while (!operatorStack.isEmpty() && operatorStack.peek() != null &&
+                           operatorStack.peek().priority() >= newOperator.priority()) {
+                        processOperator();
+                    }
+                    operatorStack.push(newOperator);
+                } else {
+                    throw new InvalidTokenException("Invalid token: " + token);
+                }
+            }
+        }
 
-    // 3 arguments tells the tokenizer to return delimiters as tokens
-    expressionTokenizer = new StringTokenizer( expression, delimiters, true );
+        // At the end of the expression, process all remaining operators in the stack
+        while (!operatorStack.isEmpty()) {
+            if (operatorStack.peek() == null) {
+                throw new InvalidTokenException("Mismatched parentheses detected.");
+            }
+            processOperator();
+        }
 
-    while ( expressionTokenizer.hasMoreTokens() ) {
-      // filter out spaces
-      if ( !( expressionToken = expressionTokenizer.nextToken() ).equals( " " )) {
-        // check if token is an operand
-        if ( Operand.check( expressionToken )) {
-          operandStack.push( new Operand( expressionToken ));
+        if (!operandStack.isEmpty()) {
+            return operandStack.pop().getValue();
         } else {
-          if ( ! Operator.check( expressionToken )) {
-            throw new InvalidTokenException(expressionToken);
-          }
-
+            throw new InvalidTokenException("No operands found in expression.");
+        }
+    }
 
-          // TODO fix this line of code.
-          Operator newOperator = new Operator();
+    private void processOperator() throws InvalidTokenException {
+        if (operatorStack.isEmpty() || operandStack.size() < 2) {
+            throw new InvalidTokenException();
+        }
+        
+        Operator operator = operatorStack.pop();
+        Operand secondOperand = operandStack.pop();
+        Operand firstOperand = operandStack.pop();
+        Operand result = operator.execute(firstOperand, secondOperand);
+        operandStack.push(result);
+    }
 
-         
-            while (operatorStack.peek().priority() >= newOperator.priority() ) {
-              Operator operatorFromStack = operatorStack.pop();
-              Operand operandTwo = operandStack.pop();
-              Operand operandOne = operandStack.pop();
-              Operand result = operatorFromStack.execute( operandOne, operandTwo );
-              operandStack.push( result );
+    public static void main(String[] args) {
+        try {
+            if (args.length == 1) {
+                Evaluator evaluator = new Evaluator();
+                int result = evaluator.evaluateExpression(args[0]);
+                System.out.println("Result: " + result);
+            } else {
+                System.err.println();
             }
-
-            operatorStack.push( newOperator );
-          
+        } catch (InvalidTokenException e) {
+            System.err.println(e.getMessage());
         }
-      }
     }
-
-
-    /*
-     * once no more tokens need to be scanned from StringTokenizer,
-     * we need to evaluate the remaining sub-expressions.
-     */
-    return 0;
-  }
-
-  public static void main(String[] args) throws InvalidTokenException {
-     if(args.length == 1){
-      Evaluator e = new Evaluator();
-      System.out.println(e.evaluateExpression(args[0]));
-     }else{
-      System.err.println("Invalid arguments or No expression given");
-     }
-  }
-
 }
diff --git a/calculator/evaluator/EvaluatorUI.java b/calculator/evaluator/EvaluatorUI.java
index 0f391f6..c457567 100644
--- a/calculator/evaluator/EvaluatorUI.java
+++ b/calculator/evaluator/EvaluatorUI.java
@@ -10,6 +10,7 @@ public class EvaluatorUI extends JFrame implements ActionListener {
 
      private JTextField expressionTextField = new JTextField();
      private JPanel buttonPanel = new JPanel();
+    private Evaluator evaluator = new Evaluator();
 
      // total of 20 buttons on the calculator,
      // numbered from left to right, top to bottom
@@ -66,7 +67,30 @@ public class EvaluatorUI extends JFrame implements ActionListener {
       *                          button is pressed.
       */
      public void actionPerformed(ActionEvent actionEventObject) {
+         String command = actionEventObject.getActionCommand();
 
-
+         if (command.equals("=")) {
+             try {
+                 // Use the evaluator to calculate the expression entered and update the text field with the result
+                 int result = evaluator.evaluateExpression(expressionTextField.getText());
+                 expressionTextField.setText(String.valueOf(result));
+             } catch (InvalidTokenException e) {
+                 // Show error message if invalid tokens are found
+                 expressionTextField.setText("Error: " + e.getMessage());
+             }
+         } else if (command.equals("C")) {
+             // Clear the entire expression
+             expressionTextField.setText("");
+         } else if (command.equals("CE")) {
+             // Clear the last entry up to the last operator
+             String text = expressionTextField.getText();
+             if (!text.isEmpty()) {
+                 text = text.substring(0, text.length() - 1);
+                 expressionTextField.setText(text);
+             }
+         } else {
+             // Append the command (number or operator) to the end of the text in the text field
+             expressionTextField.setText(expressionTextField.getText() + command);
+         }
      }
- }
+}
diff --git a/calculator/evaluator/Operand.java b/calculator/evaluator/Operand.java
index 7813d0c..eeb9a8b 100644
--- a/calculator/evaluator/Operand.java
+++ b/calculator/evaluator/Operand.java
@@ -8,22 +8,24 @@ public class Operand {
     /**
      * construct operand from string token.
      */
-    public Operand(String token) {
+    private int value;
 
+    public Operand(String token) {
+        this.value = Integer.parseInt(token);
     }
 
     /**
      * construct operand from integer
      */
     public Operand(int value) {
-
+        this.value = value;
     }
 
     /**
      * return value of operand
      */
     public int getValue() {
-        return 0;
+        return value;
     }
 
     /**
@@ -31,6 +33,11 @@ public class Operand {
      * operand.
      */
     public static boolean check(String token) {
-        return false;
+        try {
+            Integer.parseInt(token);
+            return true;
+        } catch (NumberFormatException e) {
+            return false;
+        }
     }
 }
diff --git a/calculator/operators/AddOperator.java b/calculator/operators/AddOperator.java
new file mode 100644
index 0000000..6299d7a
--- /dev/null
+++ b/calculator/operators/AddOperator.java
@@ -0,0 +1,16 @@
+package calculator.operators;
+
+import calculator.evaluator.Operand;
+
+public class AddOperator extends Operator {
+    @Override
+    public int priority() {
+        return 1;  // Define the correct priority if needed
+    }
+
+    @Override
+    public Operand execute(Operand operandOne, Operand operandTwo) {
+        // Implement the addition logic
+        return new Operand(operandOne.getValue() + operandTwo.getValue());
+    }
+}
diff --git a/calculator/operators/DivideOperator.java b/calculator/operators/DivideOperator.java
new file mode 100644
index 0000000..4c9c175
--- /dev/null
+++ b/calculator/operators/DivideOperator.java
@@ -0,0 +1,18 @@
+package calculator.operators;
+
+import calculator.evaluator.Operand;
+
+public class DivideOperator extends Operator {
+    @Override
+    public int priority() {
+        return 2;  // Same priority as multiplication
+    }
+
+    @Override
+    public Operand execute(Operand op1, Operand op2) {
+        if (op2.getValue() == 0) {
+            throw new IllegalArgumentException("Division by zero is not allowed.");
+        }
+        return new Operand(op1.getValue() / op2.getValue());
+    }
+}
diff --git a/calculator/operators/MultiplyOperator.java b/calculator/operators/MultiplyOperator.java
new file mode 100644
index 0000000..5f8f2b6
--- /dev/null
+++ b/calculator/operators/MultiplyOperator.java
@@ -0,0 +1,16 @@
+package calculator.operators;
+
+import calculator.evaluator.Operand;
+
+public class MultiplyOperator extends Operator {
+    @Override
+    public int priority() {
+        return 2; // The typical priority for multiplication in standard operator precedence
+    }
+
+    @Override
+    public Operand execute(Operand operandOne, Operand operandTwo) {
+        int result = operandOne.getValue() * operandTwo.getValue();
+        return new Operand(result);
+    }
+}
diff --git a/calculator/operators/Operator.java b/calculator/operators/Operator.java
index a64ffda..0162a20 100644
--- a/calculator/operators/Operator.java
+++ b/calculator/operators/Operator.java
@@ -1,6 +1,8 @@
 package calculator.operators;
 
 import calculator.evaluator.Operand;
+import java.util.HashMap;
+
 
 public abstract class Operator {
     // The Operator class should contain an instance of a HashMap
@@ -13,7 +15,14 @@ public abstract class Operator {
     // HashMap operators = new HashMap();
     // operators.put( "+", new AdditionOperator() );
     // operators.put( "-", new SubtractionOperator() );
-
+    private static HashMap<String, Operator> operators = new HashMap<>();
+    static {
+        operators.put("+", new AddOperator());
+        operators.put("-", new SubtractOperator());
+        operators.put("*", new MultiplyOperator());
+        operators.put("/", new DivideOperator());
+        operators.put("^", new PowerOperator());
+    }
 
     /**
      * retrieve the priority of an Operator
@@ -38,7 +47,7 @@ public abstract class Operator {
      * @return reference to a Operator instance.
      */
     public static Operator getOperator(String token) {
-        return null;
+        return operators.get(token);
     }
 
     
@@ -49,6 +58,6 @@ public abstract class Operator {
      * Think about what happens if we add more operators.
      */
     public static boolean check(String token) {
-        return false;
+        return operators.containsKey(token);
     }
 }
diff --git a/calculator/operators/PowerOperator.java b/calculator/operators/PowerOperator.java
new file mode 100644
index 0000000..c229dfa
--- /dev/null
+++ b/calculator/operators/PowerOperator.java
@@ -0,0 +1,15 @@
+package calculator.operators;
+
+import calculator.evaluator.Operand;
+
+public class PowerOperator extends Operator {
+    @Override
+    public int priority() {
+        return 3;  // Highest priority
+    }
+
+    @Override
+    public Operand execute(Operand op1, Operand op2) {
+        return new Operand((int) Math.pow(op1.getValue(), op2.getValue()));
+    }
+}
diff --git a/calculator/operators/SubtractOperator.java b/calculator/operators/SubtractOperator.java
new file mode 100644
index 0000000..658c44e
--- /dev/null
+++ b/calculator/operators/SubtractOperator.java
@@ -0,0 +1,15 @@
+package calculator.operators;
+
+import calculator.evaluator.Operand;
+
+public class SubtractOperator extends Operator {
+    @Override
+    public int priority() {
+        return 1;  // Same priority as addition
+    }
+
+    @Override
+    public Operand execute(Operand op1, Operand op2) {
+        return new Operand(op1.getValue() - op2.getValue());
+    }
+}
diff --git a/documentation/Panta_Rajesh.pdf b/documentation/Panta_Rajesh.pdf
new file mode 100644
index 0000000..b57bd30
Binary files /dev/null and b/documentation/Panta_Rajesh.pdf differ
diff --git a/target/calculator/evaluator/Evaluator.class b/target/calculator/evaluator/Evaluator.class
new file mode 100644
index 0000000..fef5fe7
Binary files /dev/null and b/target/calculator/evaluator/Evaluator.class differ
diff --git a/target/calculator/evaluator/EvaluatorUI.class b/target/calculator/evaluator/EvaluatorUI.class
new file mode 100644
index 0000000..9513c7f
Binary files /dev/null and b/target/calculator/evaluator/EvaluatorUI.class differ
diff --git a/target/calculator/evaluator/InvalidTokenException.class b/target/calculator/evaluator/InvalidTokenException.class
new file mode 100644
index 0000000..c551e44
Binary files /dev/null and b/target/calculator/evaluator/InvalidTokenException.class differ
diff --git a/target/calculator/evaluator/Operand.class b/target/calculator/evaluator/Operand.class
new file mode 100644
index 0000000..550694f
Binary files /dev/null and b/target/calculator/evaluator/Operand.class differ
diff --git a/target/calculator/operators/AddOperator.class b/target/calculator/operators/AddOperator.class
new file mode 100644
index 0000000..f0b22ad
Binary files /dev/null and b/target/calculator/operators/AddOperator.class differ
diff --git a/target/calculator/operators/DivideOperator.class b/target/calculator/operators/DivideOperator.class
new file mode 100644
index 0000000..4fc6c7f
Binary files /dev/null and b/target/calculator/operators/DivideOperator.class differ
diff --git a/target/calculator/operators/MultiplyOperator.class b/target/calculator/operators/MultiplyOperator.class
new file mode 100644
index 0000000..f659b0d
Binary files /dev/null and b/target/calculator/operators/MultiplyOperator.class differ
diff --git a/target/calculator/operators/Operator.class b/target/calculator/operators/Operator.class
new file mode 100644
index 0000000..d233377
Binary files /dev/null and b/target/calculator/operators/Operator.class differ
diff --git a/target/calculator/operators/PowerOperator.class b/target/calculator/operators/PowerOperator.class
new file mode 100644
index 0000000..16fcb06
Binary files /dev/null and b/target/calculator/operators/PowerOperator.class differ
diff --git a/target/calculator/operators/SubtractOperator.class b/target/calculator/operators/SubtractOperator.class
new file mode 100644
index 0000000..769f029
Binary files /dev/null and b/target/calculator/operators/SubtractOperator.class differ
diff --git a/target/tests/EvaluatorTest.class b/target/tests/EvaluatorTest.class
new file mode 100644
index 0000000..3e2fd45
Binary files /dev/null and b/target/tests/EvaluatorTest.class differ
diff --git a/target/tests/operand/OperandTest.class b/target/tests/operand/OperandTest.class
new file mode 100644
index 0000000..71a6e76
Binary files /dev/null and b/target/tests/operand/OperandTest.class differ
diff --git a/target/tests/operator/AddOperatorTest.class b/target/tests/operator/AddOperatorTest.class
new file mode 100644
index 0000000..f92532e
Binary files /dev/null and b/target/tests/operator/AddOperatorTest.class differ
diff --git a/target/tests/operator/DivideOperatorTest.class b/target/tests/operator/DivideOperatorTest.class
new file mode 100644
index 0000000..0892c41
Binary files /dev/null and b/target/tests/operator/DivideOperatorTest.class differ
diff --git a/target/tests/operator/MultiplyOperatorTest.class b/target/tests/operator/MultiplyOperatorTest.class
new file mode 100644
index 0000000..e9900d1
Binary files /dev/null and b/target/tests/operator/MultiplyOperatorTest.class differ
diff --git a/target/tests/operator/OperatorTest.class b/target/tests/operator/OperatorTest.class
new file mode 100644
index 0000000..3ba02d8
Binary files /dev/null and b/target/tests/operator/OperatorTest.class differ
diff --git a/target/tests/operator/PowerOperatorTest.class b/target/tests/operator/PowerOperatorTest.class
new file mode 100644
index 0000000..5b85567
Binary files /dev/null and b/target/tests/operator/PowerOperatorTest.class differ
diff --git a/target/tests/operator/SubtractOperatorTest.class b/target/tests/operator/SubtractOperatorTest.class
new file mode 100644
index 0000000..3ee81ff
Binary files /dev/null and b/target/tests/operator/SubtractOperatorTest.class differ
diff --git a/tests/operator/AddOperatorTest.java b/tests/operator/AddOperatorTest.java
index 127c11e..6c3ab4c 100644
--- a/tests/operator/AddOperatorTest.java
+++ b/tests/operator/AddOperatorTest.java
@@ -5,6 +5,7 @@ import calculator.evaluator.Operand;
 import org.junit.jupiter.api.Assertions;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
+import calculator.operators.AddOperator;
 
 
 @DisplayName("Addition Test")
diff --git a/tests/operator/DivideOperatorTest.java b/tests/operator/DivideOperatorTest.java
index ce169a8..40b428a 100644
--- a/tests/operator/DivideOperatorTest.java
+++ b/tests/operator/DivideOperatorTest.java
@@ -4,6 +4,7 @@ import calculator.evaluator.Operand;
 import org.junit.jupiter.api.Assertions;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
+import calculator.operators.DivideOperator;
 
 
 @DisplayName("Division Test")
diff --git a/tests/operator/MultiplyOperatorTest.java b/tests/operator/MultiplyOperatorTest.java
index 1187039..41c7b8e 100644
--- a/tests/operator/MultiplyOperatorTest.java
+++ b/tests/operator/MultiplyOperatorTest.java
@@ -4,6 +4,7 @@ import calculator.evaluator.Operand;
 import org.junit.jupiter.api.Assertions;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
+import calculator.operators.MultiplyOperator;
 
 
 
diff --git a/tests/operator/PowerOperatorTest.java b/tests/operator/PowerOperatorTest.java
index 5711132..cf487ba 100644
--- a/tests/operator/PowerOperatorTest.java
+++ b/tests/operator/PowerOperatorTest.java
@@ -5,6 +5,7 @@ import calculator.evaluator.Operand;
 import org.junit.jupiter.api.Assertions;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
+import calculator.operators.PowerOperator;
 
 
 
diff --git a/tests/operator/SubtractOperatorTest.java b/tests/operator/SubtractOperatorTest.java
index 7b35647..0a79f29 100644
--- a/tests/operator/SubtractOperatorTest.java
+++ b/tests/operator/SubtractOperatorTest.java
@@ -4,6 +4,7 @@ import calculator.evaluator.Operand;
 import org.junit.jupiter.api.DisplayName;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.Assertions;
+import calculator.operators.SubtractOperator;
 
 
 

~~~

</details>




## Code Review


<details>
    <summary>./calculator/evaluator/Operand.java</summary>

~~~java
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

~~~

</details>



<details>
    <summary>./calculator/operators/Operator.java</summary>

~~~java
package calculator.operators;

import calculator.evaluator.Operand;
import java.util.HashMap;


public abstract class Operator {
    // The Operator class should contain an instance of a HashMap
    // This map will use keys as the tokens we're interested in,
    // and values will be instances of the Operators.
    // ALL subclasses of operator MUST be in their own file.
    // Example:
    // Where does this declaration go? What should its access level be?
    // Class or instance variable? Is this the right declaration?
    // HashMap operators = new HashMap();
    // operators.put( "+", new AdditionOperator() );
    // operators.put( "-", new SubtractionOperator() );
    private static HashMap<String, Operator> operators = new HashMap<>();
    static {
        operators.put("+", new AddOperator());
        operators.put("-", new SubtractOperator());
        operators.put("*", new MultiplyOperator());
        operators.put("/", new DivideOperator());
        operators.put("^", new PowerOperator());
    }

    /**
     * retrieve the priority of an Operator
     * @return priority of an Operator as an int
     */
    public abstract int priority();

    /**
     * Abstract method to execute an operator given two operands.
     * @param operandOne first operand of operator
     * @param operandTwo second operand of operator
     * @return an operand of the result of the operation.
     */
    public abstract Operand execute(Operand operandOne, Operand operandTwo);

    /**
     * used to retrieve an operator from our HashMap.
     * This will act as out publicly facing function,
     * granting access to the Operator HashMap.
     *
     * @param token key of the operator we want to retrieve
     * @return reference to a Operator instance.
     */
    public static Operator getOperator(String token) {
        return operators.get(token);
    }

    
     /**
     * determines if a given token is a valid operator.
     * please do your best to avoid static checks
     * for example token.equals("+") and so on.
     * Think about what happens if we add more operators.
     */
    public static boolean check(String token) {
        return operators.containsKey(token);
    }
}

~~~

</details>



<details>
    <summary>./calculator/operators/AddOperator.java</summary>

~~~java
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

~~~

</details>



<details>
    <summary>./calculator/operators/PowerOperator.java</summary>

~~~java
package calculator.operators;

import calculator.evaluator.Operand;

public class PowerOperator extends Operator {
    @Override
    public int priority() {
        return 3;  // Highest priority
    }

    @Override
    public Operand execute(Operand op1, Operand op2) {
        return new Operand((int) Math.pow(op1.getValue(), op2.getValue()));
    }
}

~~~

</details>



<details>
    <summary>./calculator/operators/DivideOperator.java</summary>

~~~java
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

~~~

</details>



<details>
    <summary>./calculator/operators/SubtractOperator.java</summary>

~~~java
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

~~~

</details>



<details>
    <summary>./calculator/operators/MultiplyOperator.java</summary>

~~~java
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

~~~

</details>



<details>
    <summary>./calculator/evaluator/Evaluator.java</summary>

~~~java
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

~~~

</details>



<details>
    <summary>./calculator/evaluator/EvaluatorUI.java</summary>

~~~java
package calculator.evaluator;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EvaluatorUI extends JFrame implements ActionListener {

     private JTextField expressionTextField = new JTextField();
     private JPanel buttonPanel = new JPanel();
    private Evaluator evaluator = new Evaluator();

     // total of 20 buttons on the calculator,
     // numbered from left to right, top to bottom
     // bText[] array contains the text for corresponding buttons
     private static final String[] buttonText = {
         "7", "8", "9", "+",
         "4", "5", "6", "-",
         "1", "2", "3", "*",
         "(", "0", ")", "/",
         "C", "CE", "=", "^"
     };

     /**
      * C  is for clear, clears entire expression
      * CE is for clear expression, clears last entry up until the last operator.
      */

     public static void main(String argv[]) {
         new EvaluatorUI();
     }

     public EvaluatorUI() {
         setLayout(new BorderLayout());
         this.expressionTextField.setPreferredSize(new Dimension(600, 50));
         this.expressionTextField.setFont(new Font("Courier", Font.BOLD, 24));
         this.expressionTextField.setHorizontalAlignment(JTextField.CENTER);

         add(expressionTextField, BorderLayout.NORTH);
         expressionTextField.setEditable(false);

         add(buttonPanel, BorderLayout.CENTER);
         buttonPanel.setLayout(new GridLayout(5, 4));

         //create 20 buttons with corresponding text in bText[] array
         JButton jb;
         for (String s : EvaluatorUI.buttonText) {
             jb = new JButton(s);
             jb.setFont(new Font("Courier", Font.BOLD, 24));
             jb.addActionListener(this);
             this.buttonPanel.add(jb);
         }

         setTitle("Calculator");
         setSize(400, 400);
         setLocationByPlatform(true);
         setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         setVisible(true);
     }

     /**
      * This function is called anytime a button is pressed
      * on our Calculator GUI.
      * @param actionEventObject Event object generated when a
      *                          button is pressed.
      */
     public void actionPerformed(ActionEvent actionEventObject) {
         String command = actionEventObject.getActionCommand();

         if (command.equals("=")) {
             try {
                 // Use the evaluator to calculate the expression entered and update the text field with the result
                 int result = evaluator.evaluateExpression(expressionTextField.getText());
                 expressionTextField.setText(String.valueOf(result));
             } catch (InvalidTokenException e) {
                 // Show error message if invalid tokens are found
                 expressionTextField.setText("Error: " + e.getMessage());
             }
         } else if (command.equals("C")) {
             // Clear the entire expression
             expressionTextField.setText("");
         } else if (command.equals("CE")) {
             // Clear the last entry up to the last operator
             String text = expressionTextField.getText();
             if (!text.isEmpty()) {
                 text = text.substring(0, text.length() - 1);
                 expressionTextField.setText(text);
             }
         } else {
             // Append the command (number or operator) to the end of the text in the text field
             expressionTextField.setText(expressionTextField.getText() + command);
         }
     }
}

~~~

</details>




## Unit Tests Results



~~~bash

Thanks for using JUnit! Support its development at https://junit.org/sponsoring

╷
├─ JUnit Jupiter ✔
│  ├─ Multiplication Test ✔
│  │  ├─ simpleMultiplicationTest() ✔
│  │  ├─ simpleMultiplicationTestNegativeOperand() ✔
│  │  ├─ multiplicationPriorityTest() ✔
│  │  ├─ simpleMultiplicationTestReversedOperands() ✔
│  │  └─ simpleMultiplicationTestNegativeOperandReversed() ✔
│  ├─ OperatorTest ✔
│  │  ├─ getOperatorReturnTypeTest(String, Class) ✔
│  │  │  ├─ [1] +, class calculator.operators.AddOperator ✔
│  │  │  ├─ [2] -, class calculator.operators.SubtractOperator ✔
│  │  │  ├─ [3] /, class calculator.operators.DivideOperator ✔
│  │  │  ├─ [4] *, class calculator.operators.MultiplyOperator ✔
│  │  │  └─ [5] ^, class calculator.operators.PowerOperator ✔
│  │  └─ operatorCheckTest(String, Boolean) ✔
│  │     ├─ [1] +, true ✔
│  │     ├─ [2] -, true ✔
│  │     ├─ [3] *, true ✔
│  │     ├─ [4] ^, true ✔
│  │     ├─ [5] /, true ✔
│  │     ├─ [6] 156, false ✔
│  │     ├─ [7] 156., false ✔
│  │     ├─ [8] X, false ✔
│  │     └─ [9] **, false ✔
│  ├─ Division Test ✔
│  │  ├─ simpleDivisionTestNegativeResult() ✔
│  │  ├─ simpleDivisionTest() ✔
│  │  ├─ divisionPriorityTest() ✔
│  │  ├─ simpleDivisionTestReversedOperands() ✔
│  │  └─ simpleDivisionTestEvenlyDivisible() ✔
│  ├─ Addition Test ✔
│  │  ├─ simpleAdditionTestWithNegativeOperand() ✔
│  │  ├─ simpleAdditionTestReverseOperands() ✔
│  │  ├─ simpleAdditionTest() ✔
│  │  ├─ additionPriorityTest() ✔
│  │  └─ simpleAdditionTestWithNegativeOperandsReversed() ✔
│  ├─ Power Test ✔
│  │  ├─ simplePowerTest() ✔
│  │  ├─ simplePowerTestNegativeBase() ✔
│  │  └─ powerPriorityTest() ✔
│  ├─ Evaluator Test ✔
│  │  ├─ mediumExpressionPowerWithParensTest() ✔
│  │  ├─ basicExpressionParensOnRightTest() ✔
│  │  ├─ invalidOperatorExpressionProducesInvalidTokenExceptionTest() ✔
│  │  ├─ basicExpressionDivisionNumeratorLessThanDenominatorTest() ✔
│  │  ├─ mediumExpressionParensAsSubExpressionTest() ✔
│  │  ├─ veryDifficultExpressionMixtureOfOperatorsNestedParensTest() ✔
│  │  ├─ mediumExpressionParensAndOperatorsTest() ✔
│  │  ├─ difficultExpressionMixtureOfOperatorsTest() ✔
│  │  ├─ mediumExpressionPowerTest() ✔
│  │  ├─ mediumExpressionPowerWithMultipleOperators() ✔
│  │  ├─ mediumExpressionWithMultipleParensTest() ✔
│  │  ├─ basicExpressionMultipleOperatorTest() ✔
│  │  ├─ basicExpressionSingleOperatorTest() ✔
│  │  └─ basicExpressionParensOnLeftTest() ✔
│  ├─ Subtraction Test ✔
│  │  ├─ simpleSubtractionTestNegativeOperands() ✔
│  │  ├─ simpleSubtractionTestNegativeOperandsReversed() ✔
│  │  ├─ simpleSubtractionTestReversedOperands() ✔
│  │  ├─ simpleSubtractionTest() ✔
│  │  └─ subtractionPriorityTest() ✔
│  └─ Operand Test ✔
│     ├─ getValueTest() ✔
│     ├─ operandCheckTest(String, boolean) ✔
│     │  ├─ [1] 13, true ✔
│     │  ├─ [2] c, false ✔
│     │  ├─ [3] *, false ✔
│     │  ├─ [4] 430., false ✔
│     │  ├─ [5] 430.456, false ✔
│     │  └─ [6] 343324fd, false ✔
│     └─ getValueTypeTest() ✔
├─ JUnit Vintage ✔
└─ JUnit Platform Suite ✔

Test run finished after 99 ms
[        14 containers found      ]
[         0 containers skipped    ]
[        14 containers started    ]
[         0 containers aborted    ]
[        14 containers successful ]
[         0 containers failed     ]
[        59 tests found           ]
[         0 tests skipped         ]
[        59 tests started         ]
[         0 tests aborted         ]
[        59 tests successful      ]
[         0 tests failed          ]


~~~
    
