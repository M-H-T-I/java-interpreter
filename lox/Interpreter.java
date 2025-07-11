package lox;

class Interpreter implements Expr.Visitor<Object> {

    // Literal conversion to runtime value:
    @Override
    public Object visitLiteralExpr(Expr.Literal expr){

        return expr.value;

    }


    // Grouping conversion to runtime value:
    @Override 
    public Object visitGroupingExpr(Expr.Grouping expr){

        return evaluate(expr.expression);

    }


    // Unary Expression conversion:
    public Object visitUnaryExpr(Expr.Unary expr){

        Object right = evaluate(expr.right); // calls the appropriate accept method letting the expression call its own vist method

        switch (expr.operator.type) {

            case BANG:

                return !isTruthy(right);

            case MINUS:

                checkNumberOperand(expr.operator, right);
                return -(double)right;
        
        }

        //unreacable
        return null;
    } 


    // Bianry expression conversion
    @Override
    public Object visitBinaryExpr(Expr.Binary expr){


        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {

            case GREATER:

                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;

            case GREATER_EQUAL:

                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;

            case LESS:

                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;

            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;

            
            case BANG_EQUAL: return !isEqual(left,right);
            case EQUAL_EQUAL: return isEqual(left,right);

            case MINUS:
            
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;

            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                } 

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");

            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;

            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            
        }

        //unreachable
        return null;
    }

    void interpreter(Expr expression){

        try{
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        }catch(RuntimeError error){
            Lox.runtimeError(error);
        }

    }

    // Helper Methods

    // checks the operand 
    private void checkNumberOperand(Token operator, Object operand){

        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");

    }

    // checks the number of operands and validates the binary expression
    private void checkNumberOperands(Token operator, Object left, Object right){

        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers");

    }

    //evaluate: calls the expression's accept method allowing the expression to call the appropriate type of method
    private Object evaluate(Expr expr){

        return expr.accept(this);

    }

    //isTruthy: determines what value is truth and coversely what is falsey
    private boolean isTruthy(Object object){

        // only null (nil) and false are falsey

        if(object == null) return false;
        if (object instanceof Boolean) return (boolean) object;

        return true;

    }

    private boolean isEqual(Object a, Object b){

        if (a == null && b == null) return true;
        if (a==null) return false;

        return a.equals(b);

    }

    private String stringify(Object object){

        if(object == null) return "nil";

        if (object instanceof Double){
            
            String text = object.toString();

            // in case number is 12.0, 34.0, etc.
            if (text.endsWith(".0")){

                text=text.substring(0, text.length() - 2);

            }

            return text;

        }

        return object.toString();
    }

}
