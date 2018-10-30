/**
 * @Author: Al Timofeyev
 * @Date:   Oct 26, 2018
 * @Desc:   A simple calculator application that performs arithmetic
 *          using -, +, /, *, and ^. Following rules of PEMDAS.
 */


import java.util.Stack;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SimpleCalculator
{
    public static String expression;
    public static double answer;

    public static void main(String[] args)
    {
        new SimpleCalculator().run();
    }

    public void run()
    {
        // Try to set the look and feel of the GUI.
        try
        {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // Use the GUI.
        SwingUtilities.invokeLater(() -> {
            calculatorGUI();
        });
    }

    // ********************** METHODS BELOW **********************

    /*
        The GUI for this simple calculator.
     */
    public void calculatorGUI()
    {
        // Set the text area where the Arithmetic Expression will be typed.
        JTextArea arithmeticTextArea = new JTextArea(5,20);
        arithmeticTextArea.setText(null);
        arithmeticTextArea.setLineWrap(true);
        arithmeticTextArea.setWrapStyleWord(true);
        arithmeticTextArea.setMargin(new Insets(5, 5, 5, 5));
        arithmeticTextArea.setFont(new Font("", 0, 19));

        // Label for this text area.
        JLabel arithmeticTextLabel = new JLabel("Arithmetic Expression");
        arithmeticTextLabel.setFont(new Font("", Font.BOLD, 16));
        arithmeticTextLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Now place it into its own panel.
        JPanel arithmeticTextPanel = new JPanel();
        arithmeticTextPanel.setLayout(new BorderLayout());
        arithmeticTextPanel.add(arithmeticTextLabel, BorderLayout.NORTH);
        arithmeticTextPanel.add(new JScrollPane(arithmeticTextArea), BorderLayout.CENTER);

        // Set the text are where the answer will be displayed.
        JTextArea answerTextArea = new JTextArea();
        answerTextArea.setText(null);
        answerTextArea.setLineWrap(true);
        answerTextArea.setWrapStyleWord(true);
        answerTextArea.setMargin(new Insets(5, 5, 5, 5));
        answerTextArea.setEditable(false);  // Make answer uneditable.
        answerTextArea.setBackground(new Color(241, 241, 241));
        answerTextArea.setFont(new Font("", 0, 20));

        // Label for this text area.
        JLabel answerTextLabel = new JLabel("Answer");
        answerTextLabel.setFont(new Font("", Font.BOLD, 16));
        answerTextLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Now place it into its own panel.
        JPanel answerTextPanel = new JPanel();
        answerTextPanel.setLayout(new BorderLayout());
        answerTextPanel.add(answerTextLabel, BorderLayout.NORTH);
        answerTextPanel.add(new JScrollPane(answerTextArea), BorderLayout.CENTER);

        // Place arithmetic/answer Text Panels into one control panel.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, arithmeticTextPanel, answerTextPanel);

        // Create the two buttons needed for this GUI.
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setPreferredSize(new Dimension(100,50));
        calculateButton.setFont(new Font("", Font.PLAIN, 16));
        JButton clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(100,50));
        clearButton.setFont(new Font("", Font.PLAIN, 16));

        // Set the spot where these two buttons will be located in their own panel.
        JPanel buttonControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonControlPanel.add(calculateButton);
        buttonControlPanel.add(clearButton);

        // Place everything into one JPanel.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(buttonControlPanel, BorderLayout.SOUTH);

        // Add button listeners.
        calculateButton.addActionListener((e) -> {
            expression = arithmeticTextArea.getText().trim();
            try
            {
                if (evaluateExpression())
                    answerTextArea.setText(Double.toString(answer));
                else
                    answerTextArea.setText("SYNTAX ERROR!!");
            }
            catch(UnsupportedOperationException exception)
            {
                answerTextArea.setText("Cannot Divide By Zero!!");
            }
        });

        clearButton.addActionListener((e) -> {
            arithmeticTextArea.setText(null);
            answerTextArea.setText(null);
            answerTextArea.setCaretPosition(0);
            arithmeticTextArea.requestFocus();
        });

        // Add a Key Listener for the Enter key.
        arithmeticTextArea.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e)
            {
                // If the Enter key is pressed, press calculate button.
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    calculateButton.doClick();
                    e.consume(); // Consumes the newline character.
                }
            }
        });

        // Make the Frame.
        JFrame frame = new JFrame();
        frame.setTitle("Simple Calculator");
        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setSize(new Dimension(800, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        splitPane.setDividerLocation(frame.getWidth() / 2);
    }

    /*
        Evaluate the expression while checking the syntax.
     */
    private boolean evaluateExpression()
    {
        // Make sure that the expression isn't null!!
        if(expression.length() == 0 || expression == null)
        {
            answer = 0;
            return true;
        }

        Stack<Double> values = new Stack<Double>();
        Stack<String> ops = new Stack<String>();
        String number;
        String str;
        String strBefore = null;
        String strAfter = null;
        int parenthesesCounter = 0;
        boolean decimalFound = false;

        // Evaluate the whole arithmetic expression.
        for (int i = 0; i < expression.length(); i++)
        {
            number = "";
            str = expression.substring(i, i+1);
            if (i != 0)
                strBefore = expression.substring(i-1, i);
            if (i != expression.length()-1)
                strAfter = expression.substring(i+1, i+2);
            else
                strAfter = null;

            // Skip over whitespace.
            if(str.equals(" "))
                continue;

            // If it's a number.
            else if(!(Character.digit(str.charAt(0),10) < 0))
            {
                // While the next value is a digit or a decimal.
                while(i < expression.length() && (!(Character.digit(str.charAt(0),10) < 0) || str.equals(".")))
                {
                    // If the str is a decimal.
                    if(str.equals("."))
                    {
                        // If there was a decimal found already or
                        // if the number string is empty.
                        if(decimalFound || number.length() == 0)
                            return false;

                        // Else if there's at least one digit in number.
                        else
                            number += str;

                        decimalFound = true;
                    }

                    // Else, just add the digit to number.
                    else
                        number += str;

                    // Increment i for the while loop.
                    i++;

                    // Set the next string value.
                    if(i < expression.length())
                        str = expression.substring(i, i+1);
                }

                // After exiting while loop, reset the i index to previous value for the For loop.
                i--;

                // If the number is a decimal value, add a zero to it.
                // This is to avoid things like "12.", where there are no
                // digits after the decimal.
                if(decimalFound)
                    number += "0";

                // Reset the decimal flag to false.
                decimalFound = false;

                // Add the number to values stack.
                values.push(Double.parseDouble(number));
            }

            // If it's an opening parenthesis.
            else if(str.equals("("))
            {
                // If there's a number before it.
                if(strBefore != null && !(Character.digit(strBefore.charAt(0),10) < 0))
                    return false;

                // If there's a closing parentheses without an opening one.
                if(parenthesesCounter < 0)
                    return false;

                // Increment the parenthesesCounter and push onto operator stack.
                parenthesesCounter++;
                ops.push(str);
            }

            // If it's a closing parenthesis, solve the entire parentheses.
            else if(str.equals(")"))
            {
                // If there's an operator before it.
                if(strBefore != null && (strBefore.equals("+") || strBefore.equals("-") ||
                        strBefore.equals("/") || strBefore.equals("*") || strBefore.equals("^")))
                    return false;

                // If there's a number after it.
                if(strAfter != null && !(Character.digit(strAfter.charAt(0),10) < 0))
                    return false;

                // If there's no opening parentheses before it.
                if(parenthesesCounter == 0)
                    return false;

                // Decrement the parenthesesCounter.
                parenthesesCounter--;

                // Solve entire parentheses.
                while(!ops.peek().equals("("))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));

                // Remove the opening parenthesis from ops stack.
                ops.pop();
            }

            // If it's an operator.
            else if(str.equals("+") || str.equals("-") || str.equals("*") ||
                    str.equals("/") || str.equals("^"))
            {
                // If it's a negative sign.
                if(str.equals("-") && isNegative(i))
                {
                    // If this is at the end.
                    if(i == expression.length()-1)
                        return false;

                    // Add the negative sign to number.
                    number += str;
                    i++;

                    // Set the next str value.
                    if(i < expression.length())
                        str = expression.substring(i, i+1);

                    // While the next value is a digit or a decimal.
                    while(i < expression.length() && (!(Character.digit(str.charAt(0),10) < 0) || str.equals(".")))
                    {
                        // If the str is a decimal.
                        if(str.equals("."))
                        {
                            // If there was a decimal found already or
                            // if there's only a negative sign in number.
                            if(decimalFound || number.length() == 1)
                                return false;

                            // Else if there's more than just a negative sign in number.
                            else
                                number += str;

                            decimalFound = true;
                        }

                        // Else, just add the str to number.
                        else
                            number += str;

                        // Increment i for the while loop.
                        i++;

                        // Set the next string value.
                        if(i < expression.length())
                            str = expression.substring(i, i+1);
                    }

                    // After exiting while loop, reset the i index to previous value for the For loop.
                    i--;

                    // If the number is a decimal value, add a zero to it.
                    if(decimalFound)
                        number += "0";

                    // Reset the decimal flag to false.
                    decimalFound = false;

                    // Add the number to values stack.
                    values.push(Double.parseDouble(number));
                }

                // Else, if it's an operator.
                else
                {
                    // If these are at the beginning or end.
                    if (i == 0 || i == expression.length() - 1)
                        return false;

                    // If there's an operator before one of these operators.
                    if (strBefore != null && (strBefore.equals("+") || strBefore.equals("-") || strBefore.equals("/") ||
                            strBefore.equals("*") || strBefore.equals("^") || strBefore.equals("(")))
                        return false;

                    // If there's an operator after one of these operators.
                    if (strAfter != null && (strAfter.equals("+") || strAfter.equals("/") ||
                            strAfter.equals("*") || strAfter.equals("^")) || strAfter.equals(")"))
                        return false;

                    // While the top of ops has the same or greater precedence to the minus sign.
                    while (!ops.empty() && hasPrecedence(str, ops.peek()))
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));

                    // Push the operator onto the ops stack.
                    ops.push(str);
                }
            }

            // Else, if the substring is not one of the qualified above characters
            else
                return false;
        }

        // Check the parenthesesCounter to make sure there are no
        // open or closed parentheses remaining.
        if (parenthesesCounter != 0)
            return false;

        // If there were no syntax errors so far, apply the remaining operators
        // to the remaining values in the two stacks.
        while(!ops.empty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));

        // Store the final answer.
        answer = values.pop();

        // Return True if the evaluation was performed correctly
        // without syntax errors.
        return true;
    }


    /*
        Returns True if op2 has a higher or same precedence as op1.
        Otherwise it return False.
        @param  op1 The first operator.
        @param  op2 The second operator.
     */
    private boolean hasPrecedence(String op1, String op2)
    {
        if(op2.equals("(") || op2.equals(")"))
            return false;

        if((op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-")))
            return false;

        if(op1.equals("^") && (op2.equals("*") || op2.equals("/") || op2.equals("+") || op2.equals("-")))
            return false;

        else
            return true;
    }

    /*
        A utility method to apply an operator op on operands a and b.
        Returns the result.
        @param  op  The string operator.
        @param  a   The first operand.
        @param  b   The second operand.
     */
    private double applyOp(String op, double b, double a)
    {
        switch(op)
        {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot Divide by Zero!");

                return a / b;
            case "^":
                if(b == 0)
                    return 1;

                double number = a;
                for(int i = 1; i < (int)Math.abs(b); i++)
                {
                    number *= a;
                }

                // If b was negative, divide 1 by number.
                if(b < 0)
                    number = 1.0/number;

                return number;
        }

        return 0;
    }


    /*
        This is for negative numbers only.
        Checks to see if a minus sign is actually a negative sign.
        @param  i   The index of the minus sign in expression string.
     */
    private boolean isNegative(int i)
    {
        String str = expression.substring(i, i+1);
        String strBefore = null;
        String strAfter = null;

        if (i != 0)
            strBefore = expression.substring(i-1, i);
        if (i != expression.length()-1)
            strAfter = expression.substring(i+1, i+2);

        // If there's a number before it or a closing parentheses, it's not negative.
        // The second argument will give TRUE if there's a number before it.
        if(strBefore != null && (!(Character.digit(strBefore.charAt(0),10) < 0) || strBefore.equals(")")))
            return false;

        // Check to make sure that, if there's no number after it, it's not negative.
        // The second argument will give FALSE if there's a number after it.
        if(strAfter != null && (Character.digit(strAfter.charAt(0),10) < 0))
            return false;

        // Else, if there's no numbers or ")" before and a number after it, it's a negative.
        return true;
    }
}
