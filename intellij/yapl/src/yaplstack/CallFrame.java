package yaplstack;

import java.util.Stack;

public class CallFrame {
    public CallFrame outer;
    public Environment environment;
    public final Instruction[] instructions;
    public int ip;
    public Stack<Object> stack = new Stack<>();

    public CallFrame(Instruction[] instructions) {
        //this(new Environment(), null, instructions);
        environment = null;
        push(new Environment());
        this.instructions = instructions;
    }

    public CallFrame(Environment environment, CallFrame outer, Instruction[] instructions) {
        this.environment = environment;
        this.outer = outer;
        this.instructions = instructions;
    }

    public void incrementIP() {
        ip++;
    }

    public void setIP(int index) {
        ip = index;
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

    public void pushTo(CallFrame callFrame, int pushCount) {
        for(int i = pushCount - 1; i >= 0; i--)
            callFrame.stack.push(stack.get(stack.size() - i - 1));
        for(int i = 0; i < pushCount; i++)
            pop();
    }

    public void swap() {
        Object tmp = stack.pop();
        stack.add(stack.size() - 1, tmp);
    }

    public void swapx(int delta) {
        Object tmp = stack.pop();
        stack.add(stack.size() - delta, tmp);
    }

    public Object get(int ordinal) {
        return stack.get(ordinal);
    }

    public void set(int ordinal, Object value) {
        stack.set(ordinal, value);
    }
}
