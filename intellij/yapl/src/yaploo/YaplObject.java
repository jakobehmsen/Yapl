package yaploo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public interface YaplObject {
    void send(YaplObject thread, YaplObject message);

    default YaplObject send(String selector, YaplObject... args) {
        ArrayList<YaplObject> instructions = new ArrayList<>();

        instructions.add(YaplPrimitive.Factory.push(this));

        Arrays.asList(args).stream().forEach(x -> instructions.add(YaplPrimitive.Factory.push(x)));

        instructions.add(YaplPrimitive.Factory.send(selector, args.length));
        instructions.add(YaplPrimitive.Factory.respond);

        YaplObject thread = new YaplThread(
            new YaplFrame(null, this, null, new YaplArray(instructions.toArray(new YaplObject[instructions.size()])), new YaplArray(args)));
        thread.run();
        return thread.getFrame().pop();
    }

    default YaplObject getSelector() {
        return send("getSelector");
        //throw new UnsupportedOperationException();
    }

    default YaplObject getArgs() {
        return send("getArgs");
        //throw new UnsupportedOperationException();
    }

    default YaplObject getSelf() {
        return send("getSelf");
        //throw new UnsupportedOperationException();
    }

    default YaplObject get(int index) {
        return send("get", new YaplInteger(index));
    }

    default void eval(YaplObject thread) {
        send("eval", thread);
        //throw new UnsupportedOperationException();
    }

    default void evalOnWith(YaplObject thread, YaplObject self, YaplObject environment, YaplObject args) {
        send("evalOnWith", thread, self, environment, args);
        //throw new UnsupportedOperationException();
    }

    default int toInt() {
        YaplObject intCompatible = send("toInt");
        return intCompatible.toInt();
        //throw new UnsupportedOperationException();
    }

    default YaplObject pop() {
        return send("pop");
        //throw new UnsupportedOperationException();
    }

    default void push(YaplObject obj) {
        send("push", obj);
        //throw new UnsupportedOperationException();
    }

    default YaplObject getFrame() {
        return send("getFrame");
        //throw new UnsupportedOperationException();
    }

    default YaplObject extend() {
        return send("extend");
        //throw new UnsupportedOperationException();
    }

    default YaplObject resolve(String selector) {
        return send("resolve", new YaplString(selector));
        //throw new UnsupportedOperationException();
    }

    default void pushFrame(YaplObject receiver, YaplObject environment, YaplObject instructions, YaplObject args) {
        send("pushFrame", receiver, environment, instructions, args);
        //throw new UnsupportedOperationException();
    }

    default void popFrame() {
        send("popFrame");
        //throw new UnsupportedOperationException();
    }

    default YaplObject length() {
        return send("length");
        //throw new UnsupportedOperationException();
    }

    default void define(String name, YaplObject obj)  {
        send("define", new YaplString(name), obj);
        //throw new UnsupportedOperationException();
    }

    default YaplObject getOuter() {
        return send("getOuter");
        //throw new UnsupportedOperationException();
    }

    default YaplObject getEnvironment() {
        return send("getEnvironment");
        //throw new UnsupportedOperationException();
    }

    default void incrementIP() {
        send("incrementIP");
        //throw new UnsupportedOperationException();
    }

    default void run() {
        send("run");
        //throw new UnsupportedOperationException();
    }

    default void setFinished() {
        send("setFinished");
        //throw new UnsupportedOperationException();
    }

    default void set(String name, YaplObject value) {
        send("store", new YaplString(name), value);
        //throw new UnsupportedOperationException();
    }

    default void dup() {
        send("dup");
        //throw new UnsupportedOperationException();
    }
}
