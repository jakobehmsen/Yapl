package yaploo;

import java.util.Stack;

public class YaplFrame implements YaplObject {
    private YaplObject outer;
    private YaplObject self;
    private YaplObject environment;
    private YaplObject instructions;
    private YaplObject args;
    private Stack<YaplObject> stack = new Stack<>();
    private int ip;

    public YaplFrame(YaplObject outer, YaplObject self, YaplObject environment, YaplObject instructions, YaplObject args) {
        this.outer = outer;
        this.self = self;
        this.environment = environment;
        this.instructions = instructions;
        this.args = args;
    }

    public void eval(YaplObject thread) {
        instructions.get(ip).evalOnWith(thread, self, environment, args);
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

    @Override
    public YaplObject getSelf() {
        return self;
    }
}
