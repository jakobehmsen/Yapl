package yaploo;

import java.util.Stack;

public class YaplFrame implements YaplObject {
    private YaplObject outer;
    private YaplObject receiver;
    private YaplObject environment;
    private Stack<YaplObject> stack = new Stack<>();

    public YaplFrame(YaplObject outer, YaplObject receiver, YaplObject environment) {
        this.outer = outer;
        this.receiver = receiver;
        this.environment = environment;
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
}
