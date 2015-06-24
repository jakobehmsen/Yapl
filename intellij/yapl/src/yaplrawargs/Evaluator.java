package yaplrawargs;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Evaluator {
    private Environment environment;
    private Frame frame;

    public Evaluator(Environment environment, Frame frame) {
        this.environment = environment;
        this.frame = frame;
    }

    public void pushFrame(Consumer<Object> continuation) {
        pushFrame(continuation, frame.locals);
    }

    public void pushFrame(Consumer<Object> continuation, Object locals) {
        frame = new Frame(
            frame,
            continuation,
            locals
        );
    }

    public void popFrame(Object result) {
        Frame currentFrame = frame;
        frame = frame.outer;
        currentFrame.continuation.accept(result);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void apply(Object item, Object locals) {
        if(item instanceof BiConsumer)
            ((BiConsumer<Evaluator, Pair>)item).accept(this, (Pair) locals);
        else
            popFrame(item);
    }

    public void evaluate(Object item, Consumer<Object> continuation) {
        pushFrame(continuation);
        evaluate(item);
    }

    public void evaluate(Object item) {
        if(item != null && item instanceof Pair) {
            Pair list = (Pair)item;
            if(list.current instanceof String) {
                String operator = (String)list.current;

                Pair args = list.next;
                Object function = environment.get(operator);
                apply(function, args);

                return;
            } else {
                pushFrame(result -> popFrame(result));
                evaluateList(list, null);

                return;
            }
        }

        popFrame(item);
    }

    public void evaluateList(Pair list, Object lastResult) {
        if(list == null)
            popFrame(lastResult);
        else {
            evaluate(list.current, result ->
                evaluateList(list.next, result));
        }
    }

    public Object getLocals() {
        return frame.locals;
    }
}
