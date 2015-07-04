package yaploo;

import java.util.Stack;

public class YaplFrame implements YaplObject {
    private YaplObject outer;
    private YaplObject receiver;
    private YaplObject environment;
    private YaplObject instructions;
    private Stack<YaplObject> stack = new Stack<>();
    private int ip;

    public YaplFrame(YaplObject outer, YaplObject receiver, YaplObject environment, YaplObject instructions) {
        this.outer = outer;
        this.receiver = receiver;
        this.environment = environment;
        this.instructions = instructions;
    }

    public void eval(YaplObject thread) {
        instructions.get(ip).eval(thread);
    }

    @Override
    public void incrementIP() {
        ip++;
    }

    @Override
    public void send(YaplObject thread, YaplObject message) {

    }

    @Override
    public void push(YaplObject obj) {
        stack.push(obj);
    }

    @Override
    public YaplObject pop() {
        return stack.pop();
    }

    @Override
    public YaplObject getOuter() {
        return outer;
    }

    @Override
    public YaplObject getEnvironment() {
        return environment;
    }
}
