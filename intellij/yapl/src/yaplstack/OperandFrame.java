package yaplstack;

import java.util.Stack;

public class OperandFrame {
    public final OperandFrame outer;
    private Stack<Object> stack = new Stack<>();

    public OperandFrame() {
        this(null);
    }

    public OperandFrame(OperandFrame outer) {
        this.outer = outer;
    }

    public void push(Object obj) {
        stack.push(obj);
    }

    public Object pop() {
        return stack.pop();
    }

    public void dup() {
        stack.push(stack.peek());
    }

    public void dupx1() {
        stack.add(stack.size() - 2, stack.peek());
    }

    public void pushTo(OperandFrame operandFrame, int pushCount) {
        for(int i = pushCount - 1; i >= 0; i--)
            operandFrame.push(stack.get(stack.size() - i - 1));
        for(int i = 0; i < pushCount; i++)
            pop();
    }

    public void swap() {
        Object tmp = stack.pop();
        stack.add(stack.size() - 1, tmp);
    }

    public Object get(int ordinal) {
        return stack.get(ordinal);
    }

    public void set(int ordinal, Object value) {
        stack.set(ordinal, value);
    }
}
