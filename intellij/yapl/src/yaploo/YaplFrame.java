package yaploo;

import java.util.Stack;

public class YaplFrame implements YaplObject {
    private Stack<YaplObject> stack = new Stack<>();

    @Override
    public void send(YaplObject message) {

    }

    @Override
    public void push(YaplObject obj) {
        stack.push(obj);
    }

    @Override
    public YaplObject pop() {
        return stack.pop();
    }
}
