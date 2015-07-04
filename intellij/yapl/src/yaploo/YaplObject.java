package yaploo;

public interface YaplObject {
    void send(YaplObject message);

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
}
