/**
 * @Author: AlTimofeyev
 * @Date:   Oct 26, 2018
 * @Desc:
 */


import java.util.Stack;

public class SimpleCalculator
{
    public static String expression;
    public static boolean syntaxValid = true;
    public Stack<Double> values = new Stack<Double>();
    public Stack<String> ops = new Stack<String>();
    public char[] tokens;

    public static void main(String[] args)
    {
        expression = "-23+98*(2+2)";

        if(!syntaxValid)
            System.out.println("THE SYNTAX OF ARITHMETIC EXPRESSION IS INVALID");


        //System.out.println(new SimpleCalculator().evaluate(expression));
    }

    // Format the expression and check for syntax errors.
    void formatExpression()
    {
        String newExpression = "";
        String str = null;
        String strBefore = null;
        String strAfter = null;
        int parenthesesCounter = 0;
        boolean isItNegative = false;

        for (int i = 0; i < expression.length(); i++)
        {
            isItNegative = false;

            str = expression.substring(i);
            if (i != 0)
                strBefore = expression.substring(i-1);
            if (i != expression.length()-1)
                strAfter = expression.substring(i+1);

            // Skip over whitespace.
            if(str.equals(" "))
                continue;

            // If it's an opening parenthesis.
            else if(str.equals("("))
            {
                // If there's a number before it.
                if(strBefore != null && !(Character.digit(strBefore.charAt(0),10) < 0))
                {
                    syntaxValid = false;
                    break;
                }

                // If there's a closing parentheses without an opening one.
                if(parenthesesCounter < 0)
                {
                    syntaxValid = false;
                    break;
                }

                // Increment the parenthesesCounter and push onto operator stack.
                parenthesesCounter++;
                ops.push(str);
            }

            // If it's a closing parenthesis.
            else if(str.equals(")"))
            {
                // If there's an operator before it.
                if(strBefore != null && (strBefore.equals("+") || strBefore.equals("-") ||
                        strBefore.equals("/") || strBefore.equals("*") || strBefore.equals("^")))
                {
                    syntaxValid = false;
                    break;
                }

                // If there's a number after it.
                if(strAfter != null && !(Character.digit(strAfter.charAt(0),10) < 0))
                {
                    syntaxValid = false;
                    break;
                }

                // If there's no opening parentheses before it.
                if(parenthesesCounter == 0)
                {
                    syntaxValid = false;
                    break;
                }

                // Decrement the parenthesesCounter and push onto operator stack.
                parenthesesCounter--;
                ops.push(str);
            }
            // If it's an operator (not minus sign).
            else if(str.equals("+") || str.equals("*") ||
                    str.equals("/") || str.equals("^"))
            {
                // If these are at the beginning or end.
                if(i == 0 || i == expression.length()-1)
                {
                    syntaxValid = false;
                    break;
                }

                // If there's an operator before one of these operators.
                if(strBefore != null && (strBefore.equals("+") || strBefore.equals("-") || strBefore.equals("/") ||
                        strBefore.equals("*") || strBefore.equals("^") || strBefore.equals("(")))
                {
                    syntaxValid = false;
                    break;
                }

                // If there's an operator after one of these operators.
                if(strAfter != null && (strAfter.equals("+") || strAfter.equals("/") ||
                        strAfter.equals("*") || strAfter.equals("^")) || strAfter.equals(")"))
                {
                    syntaxValid = false;
                    break;
                }

                ops.push(str);
            }

            // If it's a minus (-) sign.
            else if(str.equals("-"))
            {
                // If this is at the end.
                if(i == expression.length()-1)
                {
                    syntaxValid = false;
                    break;
                }

                // If this is a negative sign (not an operator).
            }

            // Else, if the substring is not one of the qualified above characters
            else
            {
                syntaxValid = false;
                break;
            }
        }

        // Check the parenthesesCounter to make sure there are no
        // open or closed parentheses remaining.
        if (parenthesesCounter != 0)
            syntaxValid = false;
    }

    // This is for negative numbers only.
    boolean isNegative(int i)
    {
        String str = expression.substring(i);
        String strBefore = null;
        String strAfter = null;

        if (i != 0)
            strBefore = expression.substring(i-1);
        if (i != expression.length()-1)
            strAfter = expression.substring(i+1);

        // If there's a number before it, it's not negative.
        if(strBefore != null && !(Character.digit(strAfter.charAt(0),10) < 0))
            return false;

        // Else it's a negative.
        return true;
    }
}
