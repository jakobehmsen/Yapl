package yaplstack;

import java.util.Stack;

public class Frame {
    public final Frame outer;
    public final Environment environment;
    public final Instruction[] instructions;
    public int ip;
    private Stack<Object> stack = new Stack<>();

    public Frame(Instruction[] instructions) {
        this(null, new Environment(), instructions);
    }

    public Frame(Frame outer, Environment environment, Instruction[] instructions) {
        this.outer = outer;
        this.environment = environment;
        this.instructions = instructions;
    }

    public void push(Object obj) {
        stack.push(obj);
    }

    public Object pop() {
        return stack.pop();
    }

    public void incrementIP() {
        ip++;
    }
}
