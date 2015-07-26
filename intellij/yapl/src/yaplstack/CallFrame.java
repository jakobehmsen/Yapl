package yaplstack;

import java.util.Stack;
import java.util.stream.Collectors;

public class CallFrame {
    public CallFrame outer;
    public final Instruction[] instructions;
    public int ip;
    public Stack<Object> stack = new Stack<>();

    public CallFrame(Instruction[] instructions) {
        push(new Environment());
        this.instructions = instructions;
    }

    public CallFrame(CallFrame outer, Instruction[] instructions) {
        this.outer = outer;
        this.instructions = instructions;
        if(instructions == null)
            new String();
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

    public void dupx1down() {
        stack.add(stack.size() - 2, stack.peek());
    }

    public void dupx(int delta) {
        // From delta, dup to top
        stack.push(stack.get(stack.size() - delta - 1));
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

    public String toString(Thread thread) {
        return stack.stream().map(x ->
            x instanceof Environment ? ((Environment)x).toString(thread) : x != null ? x.toString() : "null"
        ).collect(Collectors.toList()).toString();
    }
}
