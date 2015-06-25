package yapldynscope;

import java.util.function.Consumer;

public class Frame {
    public final Frame outer;
    public final Consumer<Object> responseHandler;

    public Frame(Frame outer, Consumer<Object> responseHandler) {
        this.outer = outer;
        this.responseHandler = responseHandler;
    }
}
