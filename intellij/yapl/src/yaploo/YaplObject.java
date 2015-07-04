package yaploo;

public interface YaplObject {
    void send(YaplObject thread, YaplObject message);

    default YaplObject getSelector() {
        throw new UnsupportedOperationException();
    }

    default YaplObject getArgs() {
        throw new UnsupportedOperationException();
    }

    default YaplObject get(int index) {
        throw new UnsupportedOperationException();
    }

    default void eval(YaplObject thread) {
        throw new UnsupportedOperationException();
    }

    default int toInt() {
        throw new UnsupportedOperationException();
    }

    default YaplObject pop() {
        throw new UnsupportedOperationException();
    }

    default void push(YaplObject obj) {
        throw new UnsupportedOperationException();
    }

    default YaplObject getFrame() {
        throw new UnsupportedOperationException();
    }

    default YaplObject extend() {
        throw new UnsupportedOperationException();
    }

    default YaplObject resolve(String selector) {
        throw new UnsupportedOperationException();
    }

    default void pushFrame(YaplObject receiver, YaplObject environment) {
        throw new UnsupportedOperationException();
    }

    default void popFrame() {
        throw new UnsupportedOperationException();
    }

    default void bindArguments(YaplObject args, YaplObject environment) {
        throw new UnsupportedOperationException();
    }

    default YaplObject length() {
        throw new UnsupportedOperationException();
    }

    default void define(String name, YaplObject obj)  {
        throw new UnsupportedOperationException();
    }

    default YaplObject getOuter() {
        throw new UnsupportedOperationException();
    }

    default YaplObject getEnvironment() {
        throw new UnsupportedOperationException();
    }
}
