package yaplco;

import java.util.function.Consumer;

public class Frame {
    public final Frame outer;
    public final Consumer<Object> continuation;
    public final Object locals;

    public Frame(Frame outer, Consumer<Object> continuation, Object locals) {
        this.outer = outer;
        this.continuation = continuation;
        this.locals = locals;
    }
}
